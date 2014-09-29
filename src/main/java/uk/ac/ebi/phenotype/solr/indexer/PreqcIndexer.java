package uk.ac.ebi.phenotype.solr.indexer;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import uk.ac.ebi.phenotype.service.dto.GenotypePhenotypeDTO;

import javax.annotation.Resource;
import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class PreqcIndexer {

	private static final Logger logger = LoggerFactory.getLogger(PreqcIndexer.class);

	@Autowired
	@Qualifier("preqcIndexing")
	SolrServer destServer;

	@Resource(name = "globalConfiguration")
	Map<String, String> config;

	@Autowired
	@Qualifier("ontodbDataSource")
	DataSource ontodbDataSource;

	@Autowired
	@Qualifier("komp2DataSource")
	DataSource komp2DataSource;

	private static HashMap<String, String> geneSymbol2IdMapping = new HashMap<>();
	private static HashMap<String, AlleleDTO> alleleSymbol2NameIdMapping = new HashMap<>();
	private static HashMap<String, String> strainId2NameMapping = new HashMap<>();
	private static HashMap<String, String> pipelineSid2NameMapping = new HashMap<>();
	private static HashMap<String, String> procedureSid2NameMapping = new HashMap<>();
	private static HashMap<String, String> parameterSid2NameMapping = new HashMap<>();

	private static HashMap<String, String> mpId2TermMapping = new HashMap<>();
	private static HashMap<Integer, String> mpNodeId2MpIdMapping = new HashMap<>();

	private static HashMap<String, String> mpId2NodeIdsMapping = new HashMap<>();
	private static HashMap<Integer, Node2TopDTO> mpNodeId2TopLevelMapping = new HashMap<>();

	private static HashMap<Integer, String> mpNodeId2IntermediateNodeIdsMapping = new HashMap<>();

	private static HashMap<String, List<MpTermDTO>> intermediateMpTerms = new HashMap<>();
	private static HashMap<String, List<MpTermDTO>> topMpTerms = new HashMap<>();

	private static HashMap<String, String> zygosityMapping = new HashMap<String, String>();

	private Connection conn_komp2 = null;
	private Connection conn_ontodb = null;
	private String preqcXmlFilename;

	public static void main(String[] args) throws XPathExpressionException, IOException, SolrServerException, ParserConfigurationException, SAXException,
			SQLException {
		OptionParser parser = new OptionParser();

		// parameter to indicate which spring context file to use
		parser.accepts("context").withRequiredArg().ofType(String.class);

		OptionSet options = parser.parse(args);
		String context = (String) options.valuesOf("context").get(0);

		logger.info("Using application context file {}", context);

		// Wire up spring support for this application

		PreqcIndexer indexer = new PreqcIndexer();

		ApplicationContext applicationContext;
		try {

			// Try context as a file resource
			applicationContext = new FileSystemXmlApplicationContext("file:" + context);

		} catch (RuntimeException e) {

			logger.warn("An error occurred loading the file: {}", e.getMessage());

			// Try context as a class path resource
			applicationContext = new ClassPathXmlApplicationContext(context);

			logger.warn("Using classpath app-config file: {}", context);

		}
		applicationContext.getAutowireCapableBeanFactory().autowireBeanProperties(indexer, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);

		// allow hibernate session to stay open the whole execution
		PlatformTransactionManager transactionManager = (PlatformTransactionManager) applicationContext.getBean("transactionManager");
		DefaultTransactionAttribute transactionAttribute = new DefaultTransactionAttribute(TransactionDefinition.PROPAGATION_REQUIRED);
		transactionAttribute.setIsolationLevel(TransactionDefinition.ISOLATION_SERIALIZABLE);
		transactionManager.getTransaction(transactionAttribute);

		indexer.run();
		logger.info("Process finished.  Exiting.");
	}

	private void run() throws IOException, SolrServerException, SAXException, ParserConfigurationException, SQLException {
		long start = System.currentTimeMillis();

		logger.info(" start time: " + (System.currentTimeMillis() - start));

		conn_komp2 = komp2DataSource.getConnection();
		conn_ontodb = ontodbDataSource.getConnection();
		preqcXmlFilename = config.get("preqcXmlFilename");

		zygosityMapping.put("Heterozygous", "heterozygote");
		zygosityMapping.put("homozygous", "homozygote");
		zygosityMapping.put("Hemizygous", "hemizygote");

		doGeneSymbol2IdMapping();
		doAlleleSymbol2NameIdMapping();
		doStrainId2NameMapping();
		doImpressSid2NameMapping();
		doOntologyMapping();
		
		destServer.deleteByQuery("*:*");

		Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new FileInputStream(preqcXmlFilename));

		Element rootElement = document.getDocumentElement();
		XPath xPath = XPathFactory.newInstance().newXPath();
		NodeList nodes = rootElement.getElementsByTagName("uk.ac.ebi.phenotype.pojo.PhenotypeCallSummary");

		System.out.println("length: " + nodes.getLength());
		System.out.println(" read document time: " + (System.currentTimeMillis() - start));

		int counter = 1;
		Set<String> bad = new HashSet<>();
		
		for (int i = 0; i < nodes.getLength(); ++i) {
			Element e = (Element) nodes.item(i);

			NodeList childNodes = e.getChildNodes();

			Integer id = null;
			String colonyId = null;
			Double pValue = null;
			String sex = null;
			String phenotypeTerm = null;
			String externalId = null;
			String zygosity = null;
			String datasource = null;
			String project = null;
			// <organisation>Wtsi</organisation>
			String gene = null;
			Double effectSize = null;
			String strain = null; // MGI:2159965</strain>
			String allele = null; // >Pfkfb4&lt;sup&gt;tm1a(KOMP)Wtsi&lt;/sup&gt;</allele>
			String pipeline = null; // >IMPC_001</pipeline>
			String procedure = null; // >IMPC_ABR</procedure>
			String parameter = null; // >IMPC_ABR_002_001</parameter>
			String phenotypingCenter = null; // >Wtsi</phenotypingCenter>

			
			for (int j = 0; j < childNodes.getLength(); ++j) {
				Node cnode = childNodes.item(j);
				if (cnode.getNodeType() == Node.ELEMENT_NODE) {
					switch (cnode.getNodeName()) {
					case "id":
						id = Integer.parseInt(cnode.getTextContent());
						break;
					case "colonyId":
						colonyId = cnode.getTextContent();
						break;
					case "pValue":
						pValue = Double.parseDouble(cnode.getTextContent());
						break;
					case "sex":
						sex = cnode.getTextContent();
						break;
					case "phenotypeTerm":
						phenotypeTerm = cnode.getTextContent();
						break;
					case "externalId":
						externalId = cnode.getTextContent();
						break;
					case "zygosity":
						zygosity = cnode.getTextContent();
						break;
					case "datasource":
						datasource = cnode.getTextContent();
						break;
					case "project":
						project = cnode.getTextContent();
						break;
					case "gene":
						gene = cnode.getTextContent();
						break;
					case "effectSize":
						effectSize = Double.parseDouble(cnode.getTextContent());
						break;
					case "strain":
						strain = cnode.getTextContent();
						break;
					case "allele":
						allele = cnode.getTextContent().replace("<sup>","<").replace("</sup>",">");
						break;
					case "pipeline":
						pipeline = cnode.getTextContent();
						break;
					case "procedure":
						procedure = cnode.getTextContent();
						break;
					case "parameter":
						parameter = cnode.getTextContent();
						break;
					case "phenotypingCenter":
						phenotypingCenter = cnode.getTextContent();
						break;
					}
				}
			}

			// Skip this one: pValue not significant OR phenotypeTerm is MA
			if ( (pValue != null && pValue >= 0.0001 ) || phenotypeTerm.startsWith("MA:") ){//|| id != 726238) {
				continue;
			}

			if (mpId2TermMapping.get(phenotypeTerm) == null){
				bad.add(phenotypeTerm);
				continue;
			}
			
			GenotypePhenotypeDTO o = new GenotypePhenotypeDTO();
			
			o.setColonyId(colonyId);
			o.setP_value(pValue);
			o.setExternalId(externalId);
			o.setZygosity(zygosityMapping.get(zygosity));
			o.setResourceName(datasource);
			o.setProjectName(project);

			o.setMarkerSymbol(gene);
			o.setMarkerAccessionId(geneSymbol2IdMapping.get(gene));

			o.setEffect_size(effectSize);

			o.setStrainAccessionId(strain);
			o.setStrainName(strainId2NameMapping.get(strain));
			
			if ( alleleSymbol2NameIdMapping.get(allele) == null  ){
				// use fake id if we cannot find the symbol from komp2
				o.setAlleleAccessionId(createFakeIdFromSymbol(allele));
				o.setAlleleName(allele);
			}
			else {
				o.setAlleleAccessionId(alleleSymbol2NameIdMapping.get(allele).acc);
				o.setAlleleName(alleleSymbol2NameIdMapping.get(allele).name);
			}
			o.setAlleleSymbol(allele);
			
			o.setPipelineName(pipelineSid2NameMapping.get(pipeline));
			o.setPipelineStableId(pipeline);

			o.setProcedureName(procedureSid2NameMapping.get(procedure));
			o.setProcedureStableId(procedure);

			o.setParameterName(parameterSid2NameMapping.get(parameter));
			o.setParameterStableId(parameter);

			o.setPhenotypingCenter(phenotypingCenter);

			o.setMpTermId(phenotypeTerm);
			
			o.setMpTermName(mpId2TermMapping.get(phenotypeTerm));
			
			// Set the intermediate terms
			List<String> ids = new ArrayList<>();
			List<String> names = new ArrayList<>();

			for (MpTermDTO mp : getIntermediateMpTerms(phenotypeTerm)) {
				ids.add(mp.id);
				names.add(mp.name);
			}

			o.setIntermediateMpTermId(ids);
			o.setIntermediateMpTermName(names);

			// Set the top level terms
			ids = new ArrayList<>();
			names = new ArrayList<>();

			for (MpTermDTO mp : getTopMpTerms(phenotypeTerm)) {
				ids.add(mp.id);
				names.add(mp.name);
			}

			o.setTopLevelMpTermId(ids);
			o.setTopLevelMpTermName(names);

			if (sex.equals("Both")) {
				o.setId(counter++); // use incremental id instead of id field from Harwell
				o.setSex("female");
				destServer.addBean(o, 60000); // commit by time: 1min

				o.setId(counter++);
				o.setSex("male");
				destServer.addBean(o, 60000);
			} 
			else {
				o.setId(counter++);
				o.setSex(sex);
				destServer.addBean(o, 60000);
			}
			
		}
		destServer.commit();

		System.out.println("time: " + (System.currentTimeMillis() - start));
		logger.warn("found {} unique mps not in ontodb", bad.size());
		logger.warn("MP terms not found: {} ", StringUtils.join(bad, ","));
	}

	public String createFakeIdFromSymbol(String alleleSymbol){
		String fakeId = null;
		
		ResultSet rs = null;
		Statement statement = null;

		String query = "select CONCAT('NULL-', UPPER(SUBSTR(MD5('" + alleleSymbol + "'),1,10))) as fakeId";
		try {
			statement = conn_komp2.createStatement();
			rs = statement.executeQuery(query);

			while (rs.next()) {
				// Retrieve by column name
				fakeId = rs.getString("fakeId");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return fakeId;
	}

	public void doGeneSymbol2IdMapping() {

		ResultSet rs = null;
		Statement statement = null;

		String query = "select acc, symbol from genomic_feature";
		try {
			statement = conn_komp2.createStatement();
			rs = statement.executeQuery(query);

			while (rs.next()) {
				// Retrieve by column name
				String acc = rs.getString("acc");
				String symbol = rs.getString("symbol");
				// logger.error(acc + " -- "+ symbol);
				geneSymbol2IdMapping.put(symbol, acc);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void doImpressSid2NameMapping() {

		HashMap<String, HashMap> impressMapping = new HashMap<String, HashMap>();
		impressMapping.put("phenotype_pipeline", pipelineSid2NameMapping);
		impressMapping.put("phenotype_procedure", procedureSid2NameMapping);
		impressMapping.put("phenotype_parameter", parameterSid2NameMapping);

		ResultSet rs = null;
		Statement statement = null;

		for (Map.Entry entry : impressMapping.entrySet()) {
			String tableName = entry.getKey().toString();
			HashMap<String, String> mapping = (HashMap) entry.getValue();

			String query = "select name, stable_id from " + tableName;
			try {
				statement = conn_komp2.createStatement();
				rs = statement.executeQuery(query);

				while (rs.next()) {
					// Retrieve by column name
					String sid = rs.getString("stable_id");
					String name = rs.getString("name");

					if (tableName.equals("phenotype_procedure")) {
						// Harwell does not include version in
						// procedure_stable_id
						sid = sid.replaceAll("_\\d+$", "");
					}

					mapping.put(sid, name);
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void doAlleleSymbol2NameIdMapping() {

		ResultSet rs = null;
		Statement statement = null;

		String query = "select acc, symbol, name from allele";
		try {
			statement = conn_komp2.createStatement();
			rs = statement.executeQuery(query);

			while (rs.next()) {
				// Retrieve by column name
				String acc = rs.getString("acc");
				String symbol = rs.getString("symbol");
				String name = rs.getString("name");
				// logger.error(acc + " -- "+ symbol);

				AlleleDTO al = new AlleleDTO();
				al.acc = acc;
				al.name = name;
				alleleSymbol2NameIdMapping.put(symbol, al);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void doStrainId2NameMapping() {

		ResultSet rs = null;
		Statement statement = null;

		String query = "select acc, name from strain";
		try {
			statement = conn_komp2.createStatement();
			rs = statement.executeQuery(query);

			while (rs.next()) {
				// Retrieve by column name
				String acc = rs.getString("acc");
				String name = rs.getString("name");
				//logger.error(acc + " -- "+ name);
				strainId2NameMapping.put(acc, name);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void doOntologyMapping() {

		ResultSet rs1, rs2, rs3, rs4, rs45 = null;
		Statement statement = null;

		// all MPs
		String query1 = "select ti.term_id, ti.name, nt.node_id from mp_term_infos ti, mp_node2term nt where ti.term_id=nt.term_id and ti.term_id !='MP:0000001'";

		// all top_level nodes of MP
		String query2 = "select lv.node_id as mp_node_id, ti.term_id, ti.name from mp_node_top_level lv"
				+ " inner join mp_node2term nt on lv.top_level_node_id=nt.node_id" + " inner join mp_term_infos ti on nt.term_id=ti.term_id"
				+ " and ti.term_id!='MP:0000001'";

		// all nodes of MPs
		String query3 = "select ti.term_id, group_concat(nt.node_id) as nodeIds from mp_term_infos ti, mp_node2term nt where ti.term_id=nt.term_id and ti.term_id !='MP:0000001' group by ti.term_id order by ti.term_id";

		// all intermediate nodes of MP nodes
		String query4 = "select child_node_id, group_concat(node_id) as intermediate_nodeIds from mp_node_subsumption_fullpath group by child_node_id";

		try {

			// we need to create a new state for each query
			// otherwise we get "Operation not allowed after ResultSet closed"
			// error
			statement = conn_ontodb.createStatement();
			rs1 = statement.executeQuery(query1);

			statement = conn_ontodb.createStatement();
			rs2 = statement.executeQuery(query2);

			statement = conn_ontodb.createStatement();
			rs3 = statement.executeQuery(query3);

			statement = conn_ontodb.createStatement();
			rs4 = statement.executeQuery(query4);

			while (rs1.next()) {
				String mp_term_id = rs1.getString("term_id");
				String mp_term_name = rs1.getString("name");
				int mp_node_id = rs1.getInt("node_id");
				// logger.error("rs1: " + mp_term_id + " -- "+ mp_term_name);
				// logger.error("rs1: " + mp_node_id + " -- "+ mp_term_name);
				mpId2TermMapping.put(mp_term_id, mp_term_name);
				mpNodeId2MpIdMapping.put(mp_node_id, mp_term_id);
			}

			// top level MPs
			while (rs2.next()) {
				int top_level_mp_node_id = rs2.getInt("mp_node_id");
				String top_level_mp_term_id = rs2.getString("term_id");
				String top_level_mp_term_name = rs2.getString("name");
				// logger.error("rs2: " + top_level_mp_node_id + " --> " +
				
				Node2TopDTO n2t = new Node2TopDTO();
				n2t.topLevelMpTermId = top_level_mp_term_id;
				n2t.topLevelMpTermName = top_level_mp_term_name;
				mpNodeId2TopLevelMapping.put(top_level_mp_node_id, n2t);
			}

			while (rs3.next()) {
				String mp_node_ids = rs3.getString("nodeIds");
				String mp_term_id = rs3.getString("term_id");
				// logger.error("rs3: " + mp_term_id + " -- > " + mp_node_ids);
				mpId2NodeIdsMapping.put(mp_term_id, mp_node_ids);
			}

			// intermediate nodeId mapping
			while (rs4.next()) {
				int child_node_id = rs4.getInt("child_node_id");
				String intermediate_nodeIds = rs4.getString("intermediate_nodeIds");
				// logger.error("rs4: " + child_node_id + " -- > " +
				// intermediate_nodeIds);
				mpNodeId2IntermediateNodeIdsMapping.put(child_node_id, intermediate_nodeIds);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public List<MpTermDTO> getIntermediateMpTerms(String mpId) {

		if (!intermediateMpTerms.containsKey(mpId)) {

			// default to empty list
			intermediateMpTerms.put(mpId, new ArrayList<MpTermDTO>());

			// MP:0012441 -- > 618,732,741,971,1090,1204,1213
			if (mpId2NodeIdsMapping.containsKey(mpId)) {
				String[] nodeIdsStr = mpId2NodeIdsMapping.get(mpId).split(",");

				List<MpTermDTO> mps = new ArrayList<>();

				for (int i = 0; i < nodeIdsStr.length; i++) {
					int childNodeId = Integer.parseInt(nodeIdsStr[i]);
					List<MpTermDTO> top = getTopMpTerms(mpId);

					// top level mp do not have intermediate mp
					if (mpNodeId2IntermediateNodeIdsMapping.get(childNodeId) != null){
						String[] intermediateNodeIdsStr = mpNodeId2IntermediateNodeIdsMapping.get(childNodeId).split(",");
						
						for (int j = 0; j < intermediateNodeIdsStr.length; j++) {
							int intermediateNodeId = Integer.parseInt(intermediateNodeIdsStr[j]);
	
							MpTermDTO mp = new MpTermDTO();
							mp.id = mpNodeId2MpIdMapping.get(intermediateNodeId);
							mp.name = mpId2TermMapping.get(mp.id);
	
							// don't want to include self as intermediate parent
							if (childNodeId != intermediateNodeId && !top.contains(mp)) {
								mps.add(mp);
							}
						}
					}
				}

				// added only we got intermediates
				if ( mps.size() != 0){
					intermediateMpTerms.put(mpId, mps);
				}
			}
		}

		return intermediateMpTerms.get(mpId);
	}

	public List<MpTermDTO> getTopMpTerms(String mpId) {

		if (!topMpTerms.containsKey(mpId)) {

			// default to empty list
			topMpTerms.put(mpId, new ArrayList<MpTermDTO>());

			// MP:0012441 -- > 618,732,741,971,1090,1204,1213
			if (mpId2NodeIdsMapping.containsKey(mpId)) {

				String[] nodeIdsStr = mpId2NodeIdsMapping.get(mpId).split(",");

				List<MpTermDTO> mps = new ArrayList<>();

				for (int i = 0; i < nodeIdsStr.length; i++) {

					int topLevelMpNodeId = Integer.parseInt(nodeIdsStr[i]);
					// System.out.println(mpId + " - top_level_node_id: " +
					// topLevelMpNodeId);

					if (mpNodeId2TopLevelMapping.containsKey(topLevelMpNodeId)) {

						MpTermDTO mp = new MpTermDTO();
						mp.id = mpNodeId2TopLevelMapping.get(topLevelMpNodeId).topLevelMpTermId;
						mp.name = mpNodeId2TopLevelMapping.get(topLevelMpNodeId).topLevelMpTermName;
						mps.add(mp);
					}

				}

				topMpTerms.put(mpId, mps);

			}
		}

		return topMpTerms.get(mpId);
	}

	private class Node2TopDTO {
		String topLevelMpTermId;
		String topLevelMpTermName;
	}

	private class AlleleDTO {
		String symbol;
		String acc;
		String name;
	}

	private class MpTermDTO {
		String id;
		String name;

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((id == null) ? 0 : id.hashCode());
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			MpTermDTO other = (MpTermDTO) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (id == null) {
				if (other.id != null)
					return false;
			} else if (!id.equals(other.id))
				return false;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			return true;
		}

		private PreqcIndexer getOuterType() {
			return PreqcIndexer.this;
		}

	}
}
