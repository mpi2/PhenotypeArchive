/**
 * Copyright (c) 2014 Lemur Consulting Ltd.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.phenotype.solr.indexer;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import uk.ac.ebi.phenotype.service.dto.AlleleDTO;
import uk.ac.ebi.phenotype.solr.indexer.beans.DiseaseBean;
import uk.ac.ebi.phenotype.solr.indexer.beans.SangerAlleleBean;
import uk.ac.ebi.phenotype.solr.indexer.beans.SangerGeneBean;

import javax.annotation.Resource;
import javax.sql.DataSource;
import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


/**
 * @author Matt
 */
public class AlleleIndexer {

	private static final Logger logger = LoggerFactory.getLogger(AlleleIndexer.class);
	public static final int PHENODIGM_BATCH_SIZE = 50000;
	private static Connection connection;

	private static final int BATCH_SIZE = 2500;

	// Map gene MGI ID to sanger allele bean
	private static Map<String, List<SangerAlleleBean>> statusLookup = new HashMap<>();

	// Map gene MGI ID to human symbols
	private static Map<String, Set<String>> humanSymbolLookup = new HashMap<>();

	// Map gene MGI ID to disease bean
	private static Map<String, List<DiseaseBean>> diseaseLookup = new HashMap<>();

	// Set of MGI IDs that have legacy projects
	private static Map<String, Integer> legacyProjectLookup = new HashMap<>();


	private static final Map<String, String> ES_CELL_STATUS_MAPPINGS = new HashMap<>();


	static {
		ES_CELL_STATUS_MAPPINGS.put("No ES Cell Production", "Not Assigned for ES Cell Production");
		ES_CELL_STATUS_MAPPINGS.put("ES Cell Production in Progress", "Assigned for ES Cell Production");
		ES_CELL_STATUS_MAPPINGS.put("ES Cell Targeting Confirmed", "ES Cells Produced");
	}


	private static final Map<String, String> MOUSE_STATUS_MAPPINGS = new HashMap<>();


	static {
		MOUSE_STATUS_MAPPINGS.put("Chimeras obtained", "Assigned for Mouse Production and Phenotyping");
		MOUSE_STATUS_MAPPINGS.put("Micro-injection in progress", "Assigned for Mouse Production and Phenotyping");
		MOUSE_STATUS_MAPPINGS.put("Cre Excision Started", "Mice Produced");
		MOUSE_STATUS_MAPPINGS.put("Rederivation Complete", "Mice Produced");
		MOUSE_STATUS_MAPPINGS.put("Rederivation Started", "Mice Produced");
		MOUSE_STATUS_MAPPINGS.put("Genotype confirmed", "Mice Produced");
		MOUSE_STATUS_MAPPINGS.put("Cre Excision Complete", "Mice Produced");
		MOUSE_STATUS_MAPPINGS.put("Phenotype Attempt Registered", "Mice Produced");
	}


	private SolrServer sangerAlleleCore;
	private SolrServer phenodigmCore;

	@Autowired
	@Qualifier("alleleIndexing")
	private SolrServer alleleCore;

	@Resource(name = "globalConfiguration")
	private Map<String, String> config;


	public AlleleIndexer() {

	}


	public void run() throws IOException, SolrServerException {

		initializeSolrCores();

		int start = 0;
		long rows = 0;
		long startTime = new Date().getTime();
		SolrQuery query = new SolrQuery("mgi_accession_id:*");
		query.addFilterQuery("feature_type:* AND -feature_type:Pseudogene AND -feature_type:\"heritable+phenotypic+marker\" AND type:gene");
		query.setRows(BATCH_SIZE);

		logger.info("Populating lookups");

		populateStatusLookup();
		logger.info("Populated status lookup, {} records", statusLookup.size());

		populateHumanSymbolLookup();
		logger.info("Populated human symbol lookup, {} records", humanSymbolLookup.size());

		populateDiseaseLookup();
		logger.info("Populated disease lookup, {} records", diseaseLookup.size());

		populateLegacyLookup();
		logger.info("Populated legacy project lookup, {} records", legacyProjectLookup.size());

		alleleCore.deleteByQuery("*:*");
		alleleCore.commit();

		while (start <= rows) {
			query.setStart(start);
			QueryResponse response = sangerAlleleCore.query(query);
			rows = response.getResults().getNumFound();
			List<SangerGeneBean> sangerGenes = response.getBeans(SangerGeneBean.class);

			// Convert to Allele DTOs
			Map<String, AlleleDTO> alleles = convertSangerGeneBeans(sangerGenes);

			// Look up the marker synonyms
			lookupMarkerSynonyms(alleles);

			// Look up the human mouse symbols
			lookupHumanMouseSymbols(alleles);

			// Look up the ES cell status
			lookupEsCellStatus(alleles);

			// Look up the disease data
			lookupDiseaseData(alleles);

			// Do the second set of mappings
			doSangerAlleleMapping(alleles);

			// Now index the alleles
			indexAlleles(alleles);

			start += BATCH_SIZE;

			logger.info("Indexed {} records", start);

		}

		alleleCore.commit();
		logger.debug("Complete - took {}ms", (new Date().getTime() - startTime));
	}


	private void initializeSolrCores() {

		final String SANGER_ALLELE_URL = config.get("imits.solrserver");
		final String PHENODIGM_URL = config.get("phenodigm.solrserver");

		// Use system proxy if set for external solr servers
		if (System.getProperty("externalProxyHost") != null && System.getProperty("externalProxyPort") != null) {

			String PROXY_HOST = System.getProperty("externalProxyHost");
			Integer PROXY_PORT = Integer.parseInt(System.getProperty("externalProxyPort"));

			HttpHost proxy = new HttpHost(PROXY_HOST, PROXY_PORT);
			DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
			CloseableHttpClient client = HttpClients.custom().setRoutePlanner(routePlanner).build();

			logger.info("Using Proxy Settings: " + PROXY_HOST + " on port: " + PROXY_PORT);

			this.sangerAlleleCore = new HttpSolrServer(SANGER_ALLELE_URL, client);
			this.phenodigmCore = new HttpSolrServer(PHENODIGM_URL, client);

		} else {

			this.sangerAlleleCore = new HttpSolrServer(SANGER_ALLELE_URL);
			this.phenodigmCore = new HttpSolrServer(PHENODIGM_URL);

		}
	}


	private void populateLegacyLookup() throws SolrServerException {

		String query = "SELECT DISTINCT project_id, gf_acc FROM phenotype_call_summary WHERE p_value < 0.0001 AND (project_id = 1 OR project_id = 8)";

		try (PreparedStatement ps = connection.prepareStatement(query)) {

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {

				legacyProjectLookup.put(rs.getString("gf_acc"), 1);

			}
		} catch (SQLException e) {
			logger.error("SQL Exception looking up legacy projects: {}", e.getMessage());
		}

	}


	private void populateStatusLookup() throws SolrServerException {

		SolrQuery query = new SolrQuery("*:*");
		query.setRows(Integer.MAX_VALUE);
		query.addFilterQuery("type:allele");

		QueryResponse response = sangerAlleleCore.query(query);
		List<SangerAlleleBean> sangerAlleles = response.getBeans(SangerAlleleBean.class);
		for (SangerAlleleBean allele : sangerAlleles) {
			if (!statusLookup.containsKey(allele.getMgiAccessionId())) {
				statusLookup.put(allele.getMgiAccessionId(), new ArrayList<SangerAlleleBean>());
			}
			statusLookup.get(allele.getMgiAccessionId()).add(allele);
		}
	}


	private void populateHumanSymbolLookup() throws IOException {

		File file = new File(config.get("human2mouseFilename"));
		List<String> lines = FileUtils.readLines(file, "UTF-8");

		for (String line : lines) {
			String[] pieces = line.trim().split("\t");

			if (pieces.length < 5) {
				continue;
			}

			String humanSymbol = pieces[0];
			String mgiId = pieces[4].trim();
			if (!mgiId.startsWith("MGI:")) {
				continue;
			}

			if (!humanSymbolLookup.containsKey(mgiId)) {
				humanSymbolLookup.put(mgiId, new HashSet<String>());
			}

			(humanSymbolLookup.get(mgiId)).add(humanSymbol);

		}

	}


	private void populateDiseaseLookup() throws SolrServerException {

		int docsRetrieved = 0;
		int numDocs = getDiseaseDocCount();

		// Fields in the solr core to bring back
		String fields = StringUtils.join(Arrays.asList(DiseaseBean.DISEASE_ID,
			DiseaseBean.MGI_ACCESSION_ID,
			DiseaseBean.DISEASE_SOURCE,
			DiseaseBean.DISEASE_TERM,
			DiseaseBean.DISEASE_ALTS,
			DiseaseBean.DISEASE_CLASSES,
			DiseaseBean.HUMAN_CURATED,
			DiseaseBean.MOUSE_CURATED,
			DiseaseBean.MGI_PREDICTED,
			DiseaseBean.IMPC_PREDICTED,
			DiseaseBean.MGI_PREDICTED_KNOWN_GENE,
			DiseaseBean.IMPC_PREDICTED_KNOWN_GENE,
			DiseaseBean.MGI_NOVEL_PREDICTED_IN_LOCUS,
			DiseaseBean.IMPC_NOVEL_PREDICTED_IN_LOCUS,
			DiseaseBean.DISEASE_HUMAN_PHENOTYPES), "");

		// The solrcloud instance cannot give us all results back at once,
		// we must batch up the calls and build it up piece at a time
		while (docsRetrieved < numDocs + PHENODIGM_BATCH_SIZE) {

			SolrQuery query = new SolrQuery("*:*");
			query.addFilterQuery("type:disease_gene_summary");
			query.setFields(fields);
			query.setStart(docsRetrieved);
			query.setRows(PHENODIGM_BATCH_SIZE);
			query.setSort(DiseaseBean.DISEASE_ID, SolrQuery.ORDER.asc);

			QueryResponse response = phenodigmCore.query(query);
			List<DiseaseBean> diseases = response.getBeans(DiseaseBean.class);
			for (DiseaseBean disease : diseases) {
				if (!diseaseLookup.containsKey(disease.getMgiAccessionId())) {
					diseaseLookup.put(disease.getMgiAccessionId(), new ArrayList<DiseaseBean>());
				}
				diseaseLookup.get(disease.getMgiAccessionId()).add(disease);
			}

			docsRetrieved += PHENODIGM_BATCH_SIZE;
			logger.info("Processed {} documents from phenodigm. {} genes in the index", docsRetrieved, diseaseLookup.size());

		}
	}


	private int getDiseaseDocCount() throws SolrServerException {

		SolrQuery query = new SolrQuery("*:*");
		query.setRows(0);
		query.addFilterQuery("type:disease_gene_summary");

		QueryResponse response = phenodigmCore.query(query);
		return (int) response.getResults().getNumFound();
	}


	private Map<String, AlleleDTO> convertSangerGeneBeans(List<SangerGeneBean> beans) {

		Map<String, AlleleDTO> map = new HashMap<>(beans.size());

		for (SangerGeneBean bean : beans) {
			String id = bean.getMgiAccessionId();
			AlleleDTO dto = new AlleleDTO();

			// Copy the fields
			dto.setMgiAccessionId(id);
			dto.setMarkerType(bean.getFeatureType());
			dto.setMarkerSymbol(bean.getMarkerSymbol());
			dto.setGeneLatestEsCellStatus(bean.getLatestEsCellStatus());
			dto.setGeneLatestMouseStatus(bean.getLatestMouseStatus());
			dto.setImitsPhenotypeStarted(bean.getLatestPhenotypeStarted());
			dto.setImitsPhenotypeComplete(bean.getLatestPhenotypeComplete());
			dto.setLatestPhenotypeStatus(bean.getLatestPhenotypeStatus());
			dto.setLatestProductionCentre(bean.getLatestProductionCentre());
			dto.setLatestPhenotypingCentre(bean.getLatestPhenotypingCentre());
			dto.setLatestProjectStatus(bean.getLatestProjectStatus());

			if (legacyProjectLookup.containsKey(bean.getMgiAccessionId())) {
				dto.setLegacyPhenotypeStatus(1);
			}

			// Do the additional mappings
			dto.setDataType(AlleleDTO.ALLELE_DATA_TYPE);

			map.put(id, dto);
		}

		return map;
	}


	private void lookupMarkerSynonyms(Map<String, AlleleDTO> alleles) {
		// Build the lookup string
		String lookup = buildIdQuery(alleles.keySet());

		String query = "select s.acc as id, s.symbol as marker_synonym, gf.name as marker_name "
			+ "from synonym s, genomic_feature gf "
			+ "where s.acc=gf.acc "
			+ "and gf.acc IN (" + lookup + ")";
		try {
			logger.debug("Starting marker synonym lookup");
			PreparedStatement ps = connection.prepareStatement(query);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				String id = rs.getString("id");
				AlleleDTO allele = alleles.get(id);
				if (allele.getMarkerSynonym() == null) {
					allele.setMarkerSynonym(new ArrayList<String>());
				}
				allele.getMarkerSynonym().add(rs.getString("marker_synonym"));
				allele.setMarkerName(rs.getString("marker_name"));
			}
			logger.debug("Finished marker synonym lookup");
		} catch (SQLException sqle) {
			logger.error("SQL Exception looking up marker symbols: {}", sqle.getMessage());
		}
	}


	private void lookupHumanMouseSymbols(Map<String, AlleleDTO> alleles) {

		for (String id : alleles.keySet()) {
			AlleleDTO dto = alleles.get(id);

			if (humanSymbolLookup.containsKey(id)) {
				dto.setHumanGeneSymbol(new ArrayList<>(humanSymbolLookup.get(id)));
			}

		}

		logger.debug("Finished human marker symbol lookup");
	}


	private String buildIdQuery(Collection<String> ids) {

		StringBuilder lookup = new StringBuilder();
		int i = 0;
		for (String id : ids) {
			if (i > 0) {
				lookup.append(",");
			}
			lookup.append("'").append(id).append("'");
			i++;
		}
		return lookup.toString();
	}


	private void lookupEsCellStatus(Map<String, AlleleDTO> alleles) {

		for (String id : alleles.keySet()) {
			AlleleDTO dto = alleles.get(id);

			if (!statusLookup.containsKey(id)) {
				continue;
			}

			for (SangerAlleleBean sab : statusLookup.get(id)) {
				dto.getAlleleName().add(sab.getAlleleName());
				dto.setImitsEsCellStatus(sab.getEsCellStatus());
				dto.setImitsMouseStatus(sab.getMouseStatus());
				dto.getPhenotypeStatus().add(sab.getPhenotypeStatus());
				dto.getProductionCentre().add(sab.getProductionCentre());
				dto.getPhenotypingCentre().add(sab.getPhenotypingCentre());
			}
		}

		logger.debug("Finished ES cell status lookup");
	}


	private void lookupDiseaseData(Map<String, AlleleDTO> alleles) {

		logger.debug("Starting disease data lookup");
		for (String id : alleles.keySet()) {

			AlleleDTO dto = alleles.get(id);

			if (!diseaseLookup.containsKey(id)) {
				continue;
			}

			for (DiseaseBean db : diseaseLookup.get(id)) {
				dto.getDiseaseId().add(db.getDiseaseId());
				dto.getDiseaseSource().add(db.getDiseaseSource());
				dto.getDiseaseTerm().add(db.getDiseaseTerm());
				if (db.getDiseaseAlts() != null) {
					dto.getDiseaseAlts().addAll(db.getDiseaseAlts());
				}
				if (db.getDiseaseClasses() != null) {
					dto.getDiseaseClasses().addAll(db.getDiseaseClasses());
				}
				dto.getHumanCurated().add(db.isHumanCurated());
				dto.getMouseCurated().add(db.isMouseCurated());
				dto.getMgiPredicted().add(db.isMgiPredicted());
				dto.getImpcPredicted().add(db.isImpcPredicted());
				dto.getMgiPredictedKnownGene().add(db.isMgiPredictedKnownGene());
				dto.getImpcPredictedKnownGene().add(db.isImpcPredictedKnownGene());
				dto.getMgiNovelPredictedInLocus().add(db.isMgiNovelPredictedInLocus());
				dto.getImpcNovelPredictedInLocus().add(db.isImpcNovelPredictedInLocus());
				if (db.getDiseaseHumanPhenotypes() != null) {
					dto.getDiseaseHumanPhenotypes().addAll(db.getDiseaseHumanPhenotypes());
				}
			}

		}
		logger.debug("Finished disease data lookup");
	}


	private void indexAlleles(Map<String, AlleleDTO> alleles) throws SolrServerException, IOException {

		alleleCore.addBeans(alleles.values(), 60000);
	}


	/**
	 * Equivalent to mapping(row) Javascript method in DIH script.
	 *
	 * @param alleles
	 */
	private void doSangerAlleleMapping(Map<String, AlleleDTO> alleles) {

		for (AlleleDTO allele : alleles.values()) {
			if (allele.getLatestPhenotypeStatus() != null) {
				allele.setImitsPhenotypeStatus(allele.getLatestPhenotypeStatus());
			}

			if (allele.getImitsEsCellStatus() != null || allele.getGeneLatestEsCellStatus() != null) {
				String esCellStatus = null;
				boolean latest;
				if (allele.getImitsEsCellStatus() != null) {
					esCellStatus = allele.getImitsEsCellStatus();
					latest = false;
				} else {
					esCellStatus = allele.getGeneLatestEsCellStatus();
					latest = true;
				}

				if (ES_CELL_STATUS_MAPPINGS.containsKey(esCellStatus)) {
					esCellStatus = ES_CELL_STATUS_MAPPINGS.get(esCellStatus);
				}

				if (latest) {
					if (!"".equals(esCellStatus)) {
						// Single value
						allele.setLatestProductionStatus(esCellStatus);
					}
					// Single value
					allele.setLatestEsCellStatus(esCellStatus);
				} else {
					// Multi value
					allele.getEsCellStatus().add(esCellStatus);
				}
			}

			if (allele.getImitsMouseStatus() != null || allele.getGeneLatestMouseStatus() != null) {
				String mouseStatus = null;
				boolean latest;
				if (allele.getImitsMouseStatus() != null) {
					mouseStatus = allele.getImitsMouseStatus();
					latest = false;
				} else {
					mouseStatus = allele.getGeneLatestMouseStatus();
					latest = true;
				}

				if (MOUSE_STATUS_MAPPINGS.containsKey(mouseStatus)) {
					mouseStatus = MOUSE_STATUS_MAPPINGS.get(mouseStatus);
				}

				if (latest) {
					if (!"".equals(mouseStatus)) {
						// Single-valued
						allele.setLatestProductionStatus(mouseStatus);
					}
					// Single value
					allele.setLatestMouseStatus(mouseStatus);
				} else {
					// Multi value
					allele.getMouseStatus().add(mouseStatus);
				}
			}
		}
	}


	public static void main(String[] args) throws SQLException, InterruptedException, JAXBException, IOException, NoSuchAlgorithmException, KeyManagementException, SolrServerException {

		OptionParser parser = new OptionParser();

		// parameter to indicate which spring context file to use
		parser.accepts("context").withRequiredArg().ofType(String.class);

		OptionSet options = parser.parse(args);
		String context = (String) options.valuesOf("context").get(0);

		logger.info("Using application context file {}", context);

		// Wire up spring support for this application
		AlleleIndexer main = new AlleleIndexer();

		ApplicationContext applicationContext;

		File f = new File(context);
		if (f.exists() && !f.isDirectory()) {

			try {

				// Try context as a file resource
				applicationContext = new FileSystemXmlApplicationContext("file:" + context);

			} catch (RuntimeException e) {

				logger.warn("An error occurred loading the file: {}", e.getMessage());

				// Try context as a class path resource
				applicationContext = new ClassPathXmlApplicationContext(context);

				logger.warn("Using classpath app-config file: {}", context);

			}

		} else {

			// Try context as a class path resource
			applicationContext = new ClassPathXmlApplicationContext(context);

			logger.warn("Using classpath app-config file: {}", context);

		}

		applicationContext.getAutowireCapableBeanFactory().autowireBeanProperties(main, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);

		// allow hibernate session to stay open the whole execution
		PlatformTransactionManager transactionManager = (PlatformTransactionManager) applicationContext.getBean("transactionManager");
		DefaultTransactionAttribute transactionAttribute = new DefaultTransactionAttribute(TransactionDefinition.PROPAGATION_REQUIRED);
		transactionAttribute.setIsolationLevel(TransactionDefinition.ISOLATION_SERIALIZABLE);
		transactionManager.getTransaction(transactionAttribute);

		DataSource ds = ((DataSource) applicationContext.getBean("komp2DataSource"));
		connection = ds.getConnection();

		main.run();

		logger.info("Process finished.  Exiting.");
	}

}
