package uk.ac.ebi.phenotype.solr.indexer;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import uk.ac.ebi.phenotype.data.imits.EncodedOrganisationConversionMap;
import uk.ac.ebi.phenotype.pojo.SexType;
import uk.ac.ebi.phenotype.service.dto.GenotypePhenotypeDTO;
import uk.ac.ebi.phenotype.solr.indexer.exceptions.IndexerException;
import uk.ac.ebi.phenotype.solr.indexer.exceptions.ValidationException;

import javax.annotation.Resource;
import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.*;

public class PreqcIndexer extends AbstractIndexer {

    private static final Logger logger = LoggerFactory.getLogger(PreqcIndexer.class);

    @Autowired
    @Qualifier("preqcIndexing")
    SolrServer preqcCore;

    @Resource(name = "globalConfiguration")
    Map<String, String> config;

    @Autowired
    @Qualifier("ontodbDataSource")
    DataSource ontodbDataSource;

    @Autowired
    @Qualifier("komp2DataSource")
    DataSource komp2DataSource;

    @Autowired
    EncodedOrganisationConversionMap dccMapping;

    private static Map<String, String> geneSymbol2IdMapping = new HashMap<>();
    private static Map<String, AlleleDTO> alleleSymbol2NameIdMapping = new HashMap<>();
    private static Map<String, String> strainId2NameMapping = new HashMap<>();
    private static Map<String, String> pipelineSid2NameMapping = new HashMap<>();
    private static Map<String, String> procedureSid2NameMapping = new HashMap<>();
    private static Map<String, String> parameterSid2NameMapping = new HashMap<>();

    private static Map<String, String> projectMap = new HashMap<>();
    private static Map<String, String> resourceMap = new HashMap<>();

    private static Map<String, String> mpId2TermMapping = new HashMap<>();
    private static Map<Integer, String> mpNodeId2MpIdMapping = new HashMap<>();

    private static Map<String, String> mpId2NodeIdsMapping = new HashMap<>();
    private static Map<Integer, Node2TopDTO> mpNodeId2TopLevelMapping = new HashMap<>();

    private static Map<Integer, String> mpNodeId2IntermediateNodeIdsMapping = new HashMap<>();

    private static Map<String, List<MpTermDTO>> intermediateMpTerms = new HashMap<>();
    private static Map<String, List<MpTermDTO>> topMpTerms = new HashMap<>();

    private static Map<String, String> zygosityMapping = new HashMap<>();
    private static Set<String> postQcData = new HashSet<>();

    private Connection conn_komp2 = null;
    private Connection conn_ontodb = null;
    private String preqcXmlFilename;

    @Override
    public void validateBuild() throws IndexerException {
        Long numFound = getDocumentCount(preqcCore);
        
        if (numFound <= MINIMUM_DOCUMENT_COUNT)
            throw new IndexerException(new ValidationException("Actual preqc document count is " + numFound + "."));
        
        if (numFound != documentCount)
            logger.warn("WARNING: Added " + documentCount + " preqc documents but SOLR reports " + numFound + " documents.");
        else
            logger.info("validateBuild(): Indexed " + documentCount + " preqc documents.");
    }
    
    @Override
    public void run() throws IndexerException {
        long start = System.currentTimeMillis();

        logger.info(" start time: " + (System.currentTimeMillis() - start));

        zygosityMapping.put("Heterozygous", "heterozygote");
        zygosityMapping.put("Homozygous", "homozygote");
        zygosityMapping.put("Hemizygous", "hemizygote");

        Set<String> bad = new HashSet<>();

        try {

            conn_komp2 = komp2DataSource.getConnection();
            conn_ontodb = ontodbDataSource.getConnection();
            preqcXmlFilename = config.get("preqcXmlFilename");

            doGeneSymbol2IdMapping();
            doAlleleSymbol2NameIdMapping();
            doStrainId2NameMapping();
            doImpressSid2NameMapping();
            doOntologyMapping();
            populatePostQcData();
            populateResourceMap();

            preqcCore.deleteByQuery("*:*");

            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new FileInputStream(preqcXmlFilename));

            Element rootElement = document.getDocumentElement();
            NodeList nodes = rootElement.getElementsByTagName("uk.ac.ebi.phenotype.pojo.PhenotypeCallSummary");

            logger.info("length: " + nodes.getLength());
            logger.info("  read document time: " + (System.currentTimeMillis() - start));

            int counter = 1;
            for (int i = 0; i < nodes.getLength();  ++ i) {
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
                String gene = null;
                Double effectSize = null;
                String strain = null;
                String allele = null;
                String pipeline = null;
                String procedure = null;
                String parameter = null;
                String phenotypingCenter = null;

                for (int j = 0; j < childNodes.getLength();  ++ j) {
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
                                allele = cnode.getTextContent().replace("<sup>", "<").replace("</sup>", ">");
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
                                phenotypingCenter = cnode.getTextContent().toUpperCase();
                                break;
                        }
                    }
                }

                // Skip this one: phenotypeTerm is null
                if (phenotypeTerm == null) {
                    logger.warn("Phenotype term is missing for record with id {}", id);
                    continue;
                }

                // Skip this one: pValue not significant OR phenotypeTerm is MA
                if ((pValue != null && pValue >= 0.0001) || phenotypeTerm.startsWith("MA:")) {//|| id != 726238) {
                    continue;
                }

                if (mpId2TermMapping.get(phenotypeTerm) == null) {
                    bad.add(phenotypeTerm);
                    continue;
                }

                // Skip if we already have this data postQC
                phenotypingCenter = dccMapping.dccCenterMap.containsKey(phenotypingCenter) ? dccMapping.dccCenterMap.get(phenotypingCenter) : phenotypingCenter;
                if (postQcData.contains(StringUtils.join(Arrays.asList(new String[]{colonyId, parameter, phenotypingCenter.toUpperCase()}), "_"))) {
                    continue;
                }

                GenotypePhenotypeDTO o = new GenotypePhenotypeDTO();

                // Procedure prefix is the first two strings of the parameter after splitting on underscore
                // i.e. IMPC_BWT_001_001 => IMPC_BWT
                String procedurePrefix = StringUtils.join(Arrays.asList(parameter.split("_")).subList(0, 2), "_");
                if (GenotypePhenotypeIndexer.source3iProcedurePrefixes.contains(procedurePrefix)) {
//                    o.setResourceName("3i");
//                    o.setResourceFullname("Infection, Immunity and Immunophenotyping consortium");
                    o.setResourceName(StatisticalResultIndexer.RESOURCE_3I.toUpperCase());
                    o.setResourceFullname(resourceMap.get(StatisticalResultIndexer.RESOURCE_3I.toUpperCase()));

                } else {
                    o.setResourceName(datasource);
                    if(resourceMap.containsKey(project.toUpperCase())) {
                        o.setResourceFullname(resourceMap.get(project.toUpperCase()));
                    }
                }

                o.setProjectName(project);
                if(projectMap.containsKey(project.toUpperCase())) {
                    o.setProjectFullname(projectMap.get(project.toUpperCase()));
                }

                o.setColonyId(colonyId);
                o.setExternalId(externalId);
                o.setStrainAccessionId(strain);
                o.setStrainName(strainId2NameMapping.get(strain));
                o.setMarkerSymbol(gene);
                o.setMarkerAccessionId(geneSymbol2IdMapping.get(gene));
                o.setPipelineName(pipelineSid2NameMapping.get(pipeline));
                o.setPipelineStableId(pipeline);
                o.setProcedureName(procedureSid2NameMapping.get(procedure));
                o.setProcedureStableId(procedure);
                o.setParameterName(parameterSid2NameMapping.get(parameter));
                o.setParameterStableId(parameter);
                o.setMpTermId(phenotypeTerm);
                o.setMpTermName(mpId2TermMapping.get(phenotypeTerm));
                o.setP_value(pValue);
                o.setEffect_size(effectSize);

                if ( ! zygosityMapping.containsKey(zygosity)) {
                    logger.warn("Zygosity {} not found for record id {}", zygosity, id);
                    continue;
                }
                o.setZygosity(zygosityMapping.get(zygosity));

                if (alleleSymbol2NameIdMapping.get(allele) == null) {
                    // use fake id if we cannot find the symbol from komp2
                    o.setAlleleAccessionId(createFakeIdFromSymbol(allele));
                    o.setAlleleName(allele);
                } else {
                    o.setAlleleAccessionId(alleleSymbol2NameIdMapping.get(allele).acc);
                    o.setAlleleName(alleleSymbol2NameIdMapping.get(allele).name);
                }
                o.setAlleleSymbol(allele);

                if (dccMapping.dccCenterMap.containsKey(phenotypingCenter)) {
                    o.setPhenotypingCenter(dccMapping.dccCenterMap.get(phenotypingCenter));
                } else {
                    o.setPhenotypingCenter(phenotypingCenter);
                }

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

                    // use incremental id instead of id field from Harwell
                    o.setId(counter ++);
                    o.setSex(SexType.female.getName());
                    documentCount++;
                    preqcCore.addBean(o);

                    o.setId(counter ++);
                    o.setSex(SexType.male.getName());
                    documentCount++;
                    preqcCore.addBean(o);

                } else {

                    o.setId(counter ++);

                    try {

                        SexType.valueOf(sex.toLowerCase());

                    } catch (IllegalArgumentException se) {
                        logger.error("Got unexpected sex value '{}' from PreQC file. Not loading", se);
                        continue;
                    }

                    o.setSex(sex.toLowerCase());
                    documentCount++;
                    preqcCore.addBean(o);
                }

                if (counter % 1000 == 0) {
                    logger.info("Added {} preqc documents to index", counter);
                }

            }

            preqcCore.commit();

        } catch (ParserConfigurationException | SAXException | SQLException | SolrServerException | IOException e) {
            throw new IndexerException(e);
        }

        logger.info("time: " + (System.currentTimeMillis() - start));
        if (bad.size() > 0) {
            logger.warn("found {} unique mps not in ontodb", bad.size());
            logger.warn("MP terms not found: {} ", StringUtils.join(bad, ","));
        }
    }

    public String createFakeIdFromSymbol(String alleleSymbol) {
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


    public void populateResourceMap() throws SQLException {

        String projQuery = "SELECT p.name as name, p.fullname as fullname FROM project p";
        String resQuery = "SELECT db.short_name as name, db.name as fullname FROM external_db db ";

        try (PreparedStatement p = conn_komp2.prepareStatement(projQuery, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {
            p.setFetchSize(Integer.MIN_VALUE);
            ResultSet r = p.executeQuery();
            while (r.next()) {
                projectMap.put(r.getString("name").toUpperCase(), r.getString("fullname"));
            }
        }
        try (PreparedStatement p = conn_komp2.prepareStatement(resQuery, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {
            p.setFetchSize(Integer.MIN_VALUE);
            ResultSet r = p.executeQuery();
            while (r.next()) {
                resourceMap.put(r.getString("name").toUpperCase(), r.getString("fullname"));
            }
        }
    }

    public void doImpressSid2NameMapping() {

        Map<String, Map> impressMapping = new HashMap<>();
        impressMapping.put("phenotype_pipeline", pipelineSid2NameMapping);
        impressMapping.put("phenotype_procedure", procedureSid2NameMapping);
        impressMapping.put("phenotype_parameter", parameterSid2NameMapping);

        ResultSet rs = null;
        Statement statement = null;

        for (Map.Entry entry : impressMapping.entrySet()) {
            String tableName = entry.getKey().toString();
            Map<String, String> mapping = (Map) entry.getValue();

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

        if ( ! intermediateMpTerms.containsKey(mpId)) {

            // default to empty list
            intermediateMpTerms.put(mpId, new ArrayList<MpTermDTO>());

            // MP:0012441 -- > 618,732,741,971,1090,1204,1213
            if (mpId2NodeIdsMapping.containsKey(mpId)) {
                String[] nodeIdsStr = mpId2NodeIdsMapping.get(mpId).split(",");

                List<MpTermDTO> mps = new ArrayList<>();

                for (int i = 0; i < nodeIdsStr.length; i ++) {
                    int childNodeId = Integer.parseInt(nodeIdsStr[i]);
                    List<MpTermDTO> top = getTopMpTerms(mpId);

                    // top level mp do not have intermediate mp
                    if (mpNodeId2IntermediateNodeIdsMapping.get(childNodeId) != null) {
                        String[] intermediateNodeIdsStr = mpNodeId2IntermediateNodeIdsMapping.get(childNodeId).split(",");

                        for (int j = 0; j < intermediateNodeIdsStr.length; j ++) {
                            int intermediateNodeId = Integer.parseInt(intermediateNodeIdsStr[j]);

                            MpTermDTO mp = new MpTermDTO();
                            mp.id = mpNodeId2MpIdMapping.get(intermediateNodeId);
                            mp.name = mpId2TermMapping.get(mp.id);

                            // don't want to include self as intermediate parent
                            if (childNodeId != intermediateNodeId &&  ! top.contains(mp)) {
                                mps.add(mp);
                            }
                        }
                    }
                }

                // added only we got intermediates
                if (mps.size() != 0) {
                    intermediateMpTerms.put(mpId, mps);
                }
            }
        }

        return intermediateMpTerms.get(mpId);
    }

    public List<MpTermDTO> getTopMpTerms(String mpId) {

        if ( ! topMpTerms.containsKey(mpId)) {

            // default to empty list
            topMpTerms.put(mpId, new ArrayList<MpTermDTO>());

            // MP:0012441 -- > 618,732,741,971,1090,1204,1213
            if (mpId2NodeIdsMapping.containsKey(mpId)) {

                String[] nodeIdsStr = mpId2NodeIdsMapping.get(mpId).split(",");

                List<MpTermDTO> mps = new ArrayList<>();

                for (int i = 0; i < nodeIdsStr.length; i ++) {

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

    public void populatePostQcData() {

        String query = "SELECT DISTINCT CONCAT(e.colony_id, '_', o.parameter_stable_id, '_', UPPER(org.name)) AS data_value " +
            "FROM observation o " +
            "INNER JOIN experiment_observation eo ON eo.observation_id=o.id " +
            "INNER JOIN experiment e ON e.id=eo.experiment_id " +
            "INNER JOIN organisation org ON org.id=e.organisation_id " +
            "WHERE e.colony_id IS NOT NULL " +
            "UNION " +
            "SELECT DISTINCT CONCAT(ls.colony_id, '_', o.parameter_stable_id, '_', UPPER(org.name)) AS data_value " +
            "FROM observation o " +
            "INNER JOIN live_sample ls ON ls.id=o.biological_sample_id " +
            "INNER JOIN biological_sample bs ON bs.id=o.biological_sample_id " +
            "INNER JOIN organisation org ON org.id=bs.organisation_id " ;

        try (PreparedStatement p = conn_komp2.prepareStatement(query)) {
            ResultSet resultSet = p.executeQuery();

            while (resultSet.next()) {
                postQcData.add(resultSet.getString("data_value"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
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

    public class MpTermDTO {

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

            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            MpTermDTO other = (MpTermDTO) obj;
            if ( ! getOuterType().equals(other.getOuterType())) {
                return false;
            }
            if (id == null) {
                if (other.id != null) {
                    return false;
                }
            } else if ( ! id.equals(other.id)) {
                return false;
            }
            if (name == null) {
                if (other.name != null) {
                    return false;
                }
            } else if ( ! name.equals(other.name)) {
                return false;
            }
            return true;
        }

        private PreqcIndexer getOuterType() {

            return PreqcIndexer.this;
        }
    }
    
    @Override
    public void initialise(String[] args) throws IndexerException {
        super.initialise(args);
    }

    public static void main(String[] args) throws IndexerException {

        PreqcIndexer main = new PreqcIndexer();
        main.initialise(args);
        main.run();
        main.validateBuild();

        logger.info("Process finished.  Exiting.");

    }

    @Override
    protected Logger getLogger() {

        return logger;
    }

}
