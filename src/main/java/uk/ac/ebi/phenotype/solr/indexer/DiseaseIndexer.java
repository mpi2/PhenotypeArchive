package uk.ac.ebi.phenotype.solr.indexer;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
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
import uk.ac.ebi.phenotype.service.dto.DiseaseDTO;
import uk.ac.ebi.phenotype.service.dto.GeneDTO;
import uk.ac.ebi.phenotype.solr.indexer.beans.DiseaseBean;

import javax.annotation.Resource;
import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.*;


/**
 * @author Matt
 */
public class DiseaseIndexer extends AbstractIndexer {


	private static final Logger logger = LoggerFactory.getLogger(DiseaseIndexer.class);

	// Map gene MGI ID to sanger allele bean
	private static Map<String, GeneData> geneLookup = new HashMap<>();

	private SolrServer phenodigmCore;

	@Autowired
	@Qualifier("geneIndexing")
	private SolrServer geneCore;

	@Autowired
	@Qualifier("diseaseIndexing")
	private SolrServer diseaseCore;

	@Resource(name = "globalConfiguration")
	private Map<String, String> config;


	public DiseaseIndexer() {

	}


	public void run() throws IOException, SolrServerException {

		initializeSolrCores();

		logger.info("Populating lookups");

		populateGenesLookup();
		logger.info("Populated genes lookup, {} records", geneLookup.size());

		long startTime = new Date().getTime();

		String fields = StringUtils.join(Arrays.asList(DiseaseBean.DISEASE_ID,
			DiseaseBean.DISEASE_ID,
			DiseaseBean.TYPE,
			DiseaseBean.DISEASE_SOURCE,
			DiseaseBean.DISEASE_TERM,
			DiseaseBean.DISEASE_ALTS,
			DiseaseBean.DISEASE_CLASSES,
			DiseaseBean.MGI_PREDICTED_KNOWN_GENE,
			DiseaseBean.IMPC_PREDICTED_KNOWN_GENE,
			DiseaseBean.MGI_NOVEL_PREDICTED_IN_LOCUS,
			DiseaseBean.IMPC_NOVEL_PREDICTED_IN_LOCUS), ",");

		SolrQuery query = new SolrQuery("*:*");
		query.addFilterQuery("type:disease");
		query.setFields(fields);
		query.setRows(10000);


		diseaseCore.deleteByQuery("*:*");
		diseaseCore.commit();

		QueryResponse response = phenodigmCore.query(query);
		List<DiseaseBean> diseases = response.getBeans(DiseaseBean.class);

		int count = 0;
		for (DiseaseBean phenDisease : diseases) {

			DiseaseDTO disease = new DiseaseDTO();

			// Populate Phenodigm data
			disease.setDataType(phenDisease.getType());
			disease.setType(phenDisease.getType());
			disease.setDiseaseId(phenDisease.getDiseaseId());
			disease.setDiseaseSource(phenDisease.getDiseaseSource());
			disease.setDiseaseTerm(phenDisease.getDiseaseTerm());
			disease.setDiseaseAlts(phenDisease.getDiseaseAlts());
			disease.setDiseaseClasses(phenDisease.getDiseaseClasses());
			disease.setHumanCurated(phenDisease.isHumanCurated());
			disease.setMouseCurated(phenDisease.isMouseCurated());
			disease.setMgiPredicted(phenDisease.isMgiPredicted());
			disease.setImpcPredicted(phenDisease.isImpcPredicted());
			disease.setMgiPredictedKnownGene(phenDisease.isMgiPredictedKnownGene());
			disease.setImpcPredictedKnownGene(phenDisease.isImpcPredictedKnownGene());
			disease.setMgiNovelPredictedInLocus(phenDisease.isMgiNovelPredictedInLocus());
			disease.setImpcNovelPredictedInLocus(phenDisease.isImpcNovelPredictedInLocus());

			// Populate gene data
			GeneData gene = geneLookup.get(phenDisease.getMgiAccessionId());

			disease.setMgiAccessionId(gene.MGI_ACCESSION_ID);
			disease.setMarkerSymbol(gene.MARKER_SYMBOL);
			disease.setMarkerName(gene.MARKER_NAME);
			disease.setMarkerSynonym(gene.MARKER_SYNONYM);
			disease.setMarkerType(gene.MARKER_TYPE);
			disease.setHumanGeneSymbol(gene.HUMAN_GENE_SYMBOL);
			disease.setStatus(gene.STATUS);
			disease.setLatestProductionCentre(gene.LATEST_PRODUCTION_CENTRE);
			disease.setLatestPhenotypingCentre(gene.LATEST_PHENOTYPING_CENTRE);
			disease.setLatestPhenotypeStatus(gene.LATEST_PHENOTYPE_STATUS);
			disease.setLegacyPhenotypeStatus(gene.LEGACY_PHENOTYPE_STATUS);
			disease.setAlleleName(gene.ALLELE_NAME);
			disease.setMpId(gene.MP_ID);
			disease.setMpTerm(gene.MP_TERM);
			disease.setMpTermDefinition(gene.MP_DEFINITION);
			disease.setMpTermSynonym(gene.MP_SYNONYM);
			disease.setTopLevelMpId(gene.TOP_LEVEL_MP_ID);
			disease.setTopLevelMpTerm(gene.TOP_LEVEL_MP_TERM);
			disease.setTopLevelMpTermSynonym(gene.TOP_LEVEL_MP_TERM_SYNONYM);
			disease.setIntermediateMpId(gene.INTERMEDIATE_MP_ID);
			disease.setIntermediateMpTerm(gene.INTERMEDIATE_MP_TERM);
			disease.setIntermediateMpTermSynonym(gene.INTERMEDIATE_MP_TERM_SYNONYM);
			disease.setChildMpId(gene.CHILD_MP_ID);
			disease.setChildMpTerm(gene.CHILD_MP_TERM);
			disease.setChildMpTermSynonym(gene.CHILD_MP_TERM_SYNONYM);
			disease.setOntologySubset(gene.ONTOLOGY_SUBSET);

			diseaseCore.addBean(disease, 60000);

			count++;
		}

		logger.info("Indexed {} records", count);

		diseaseCore.commit();
		logger.debug("Complete - took {}ms", (new Date().getTime() - startTime));
	}


	private void initializeSolrCores() {

		final String PHENODIGM_URL = config.get("phenodigm.solrserver");

		// Use system proxy if set for external solr servers
		if (System.getProperty("externalProxyHost") != null && System.getProperty("externalProxyPort") != null) {

			String PROXY_HOST = System.getProperty("externalProxyHost");
			Integer PROXY_PORT = Integer.parseInt(System.getProperty("externalProxyPort"));

			HttpHost proxy = new HttpHost(PROXY_HOST, PROXY_PORT);
			DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
			CloseableHttpClient client = HttpClients.custom().setRoutePlanner(routePlanner).build();

			logger.info("Using Proxy Settings: " + PROXY_HOST + " on port: " + PROXY_PORT);

			this.phenodigmCore = new HttpSolrServer(PHENODIGM_URL, client);

		} else {

			this.phenodigmCore = new HttpSolrServer(PHENODIGM_URL);

		}
	}


	private void populateGenesLookup() throws SolrServerException {

		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery("*:*");
		solrQuery.setRows(Integer.MAX_VALUE);
		List<GeneDTO> genes = geneCore.query(solrQuery).getBeans(GeneDTO.class);

		for (GeneDTO gene : genes) {
			for (String d : gene.getDiseaseId()) {

				if (!geneLookup.containsKey(d)) {
					geneLookup.put(d, new GeneData());
				}

				geneLookup.get(d).MGI_ACCESSION_ID.add(gene.getMgiAccessionId());
				geneLookup.get(d).MARKER_SYMBOL.add(gene.getMarkerSymbol());
				geneLookup.get(d).MARKER_NAME.add(gene.getMarkerName());
				geneLookup.get(d).MARKER_SYNONYM.addAll(gene.getMarkerSynonym());
				geneLookup.get(d).MARKER_TYPE.add(gene.getMarkerType());
				geneLookup.get(d).HUMAN_GENE_SYMBOL.addAll(gene.getHumanGeneSymbol());
				geneLookup.get(d).STATUS.add(gene.getStatus());
				geneLookup.get(d).LATEST_PRODUCTION_CENTRE.addAll(gene.getLatestProductionCentre());
				geneLookup.get(d).LATEST_PHENOTYPING_CENTRE.addAll(gene.getLatestPhenotypingCentre());
				geneLookup.get(d).LATEST_PHENOTYPE_STATUS.add(gene.getLatestPhenotypeStatus());
				geneLookup.get(d).LEGACY_PHENOTYPE_STATUS.add(gene.getLegacy_phenotype_status());
				geneLookup.get(d).ALLELE_NAME.addAll(gene.getAlleleName());
				geneLookup.get(d).MP_ID.addAll(gene.getMpId());
				geneLookup.get(d).MP_TERM.addAll(gene.getMpTerm());
				geneLookup.get(d).MP_DEFINITION.addAll(gene.getMpDefinition());
				geneLookup.get(d).MP_SYNONYM.addAll(gene.getMpSynonym());
				geneLookup.get(d).TOP_LEVEL_MP_ID.addAll(gene.getTopLevelMpId());
				geneLookup.get(d).TOP_LEVEL_MP_TERM.addAll(gene.getTopLevelMpTerm());
				geneLookup.get(d).TOP_LEVEL_MP_TERM_SYNONYM.addAll(gene.getTopLevelMpSynonym());
				geneLookup.get(d).INTERMEDIATE_MP_ID.addAll(gene.getIntermediateMpId());
				geneLookup.get(d).INTERMEDIATE_MP_TERM.addAll(gene.getIntermediateMpTerm());
				geneLookup.get(d).INTERMEDIATE_MP_TERM_SYNONYM.addAll(gene.getIntermediateMpSynonym());
				geneLookup.get(d).CHILD_MP_ID.addAll(gene.getChildMpId());
				geneLookup.get(d).CHILD_MP_TERM.addAll(gene.getChildMpTerm());
				geneLookup.get(d).CHILD_MP_TERM_SYNONYM.addAll(gene.getChildMpTermSynonym());
				geneLookup.get(d).ONTOLOGY_SUBSET.addAll(gene.getOntologySubset());

			}
		}

//		List<String> geneFields = Arrays.asList(
//			GeneDTO.MGI_ACCESSION_ID,
//			GeneDTO.MARKER_SYMBOL,
//			GeneDTO.MARKER_NAME,
//			GeneDTO.MARKER_SYNONYM,
//			GeneDTO.MARKER_TYPE,
//			GeneDTO.HUMAN_GENE_SYMBOL,
//			GeneDTO.STATUS,
//			GeneDTO.LATEST_PRODUCTION_CENTRE,
//			GeneDTO.LATEST_PHENOTYPING_CENTRE,
//			GeneDTO.LATEST_PHENOTYPE_STATUS,
//			GeneDTO.LEGACY_PHENOTYPE_STATUS,
//			GeneDTO.ALLELE_NAME,
//			GeneDTO.MP_ID,
//			GeneDTO.MP_TERM,
//			GeneDTO.MP_DEFINITION,
//			GeneDTO.MP_SYNONYM,
//			GeneDTO.TOP_LEVEL_MP_ID,
//			GeneDTO.TOP_LEVEL_MP_TERM,
//			GeneDTO.TOP_LEVEL_MP_TERM_SYNONYM,
//			GeneDTO.INTERMEDIATE_MP_ID,
//			GeneDTO.INTERMEDIATE_MP_TERM,
//			GeneDTO.INTERMEDIATE_MP_TERM_SYNONYM,
//			GeneDTO.CHILD_MP_ID,
//			GeneDTO.CHILD_MP_TERM,
//			GeneDTO.CHILD_MP_TERM_SYNONYM,
//			GeneDTO.ONTOLOGY_SUBSET);

	}


	public static void main(String[] args) throws SQLException, InterruptedException, JAXBException, IOException, NoSuchAlgorithmException, KeyManagementException, SolrServerException {

		OptionParser parser = new OptionParser();

		// parameter to indicate which spring context file to use
		parser.accepts("context").withRequiredArg().ofType(String.class);

		OptionSet options = parser.parse(args);
		String context = (String) options.valuesOf("context").get(0);

		logger.info("Using application context file {}", context);

		// Wire up spring support for this application
		DiseaseIndexer main = new DiseaseIndexer();

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

		main.run();

		logger.info("Process finished.  Exiting.");
	}


	@Override
	protected Logger getLogger() {

		return logger;
	}


	private class GeneData {
		Set<String> MGI_ACCESSION_ID = new HashSet<>();
		Set<String> MARKER_SYMBOL = new HashSet<>();
		Set<String> MARKER_NAME = new HashSet<>();
		Set<String> MARKER_SYNONYM = new HashSet<>();
		Set<String> MARKER_TYPE = new HashSet<>();
		Set<String> HUMAN_GENE_SYMBOL = new HashSet<>();
		Set<String> STATUS = new HashSet<>();
		Set<String> LATEST_PRODUCTION_CENTRE = new HashSet<>();
		Set<String> LATEST_PHENOTYPING_CENTRE = new HashSet<>();
		Set<String> LATEST_PHENOTYPE_STATUS = new HashSet<>();
		Set<Integer> LEGACY_PHENOTYPE_STATUS = new HashSet<>();
		Set<String> ALLELE_NAME = new HashSet<>();
		Set<String> MP_ID = new HashSet<>();
		Set<String> MP_TERM = new HashSet<>();
		Set<String> MP_DEFINITION = new HashSet<>();
		Set<String> MP_SYNONYM = new HashSet<>();
		Set<String> TOP_LEVEL_MP_ID = new HashSet<>();
		Set<String> TOP_LEVEL_MP_TERM = new HashSet<>();
		Set<String> TOP_LEVEL_MP_TERM_SYNONYM = new HashSet<>();
		Set<String> INTERMEDIATE_MP_ID = new HashSet<>();
		Set<String> INTERMEDIATE_MP_TERM = new HashSet<>();
		Set<String> INTERMEDIATE_MP_TERM_SYNONYM = new HashSet<>();
		Set<String> CHILD_MP_ID = new HashSet<>();
		Set<String> CHILD_MP_TERM = new HashSet<>();
		Set<String> CHILD_MP_TERM_SYNONYM = new HashSet<>();
		Set<String> ONTOLOGY_SUBSET = new HashSet<>();
	}
}
