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

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;
import javax.xml.bind.JAXBException;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

import uk.ac.ebi.phenotype.pojo.PhenotypeCallSummary;
import uk.ac.ebi.phenotype.service.dto.MpDTO;
import uk.ac.ebi.phenotype.solr.indexer.beans.MPHPBean;
import uk.ac.ebi.phenotype.solr.indexer.beans.MPStrainBean;
import uk.ac.ebi.phenotype.solr.indexer.beans.MPTermNodeBean;
import uk.ac.ebi.phenotype.solr.indexer.beans.MPTopLevelTermBean;
import uk.ac.ebi.phenotype.solr.indexer.beans.ParamProcedurePipelineBean;
import uk.ac.ebi.phenotype.solr.indexer.beans.PhenotypeCallSummaryBean;

/**
 * @author Matt Pearce
 *
 */
public class MPIndexer {

	private static final Logger logger = LoggerFactory.getLogger(MPIndexer.class);
	private static Connection komp2DbConnection;
	private static Connection ontoDbConnection;

	private static final String ALLELE_URL="http://ves-ebi-d0.ebi.ac.uk:8090/build_indexes/allele";	  
	private static final String IMAGES_URL="http://ves-ebi-d0.ebi.ac.uk:8090/build_indexes/images";       
	private static final String PREQC_URL="http://ves-ebi-d0.ebi.ac.uk:8090/build_indexes/preqc";       
	private static final String PHENODIGM_URL="http://solr-master-sanger.sanger.ac.uk/solr451/phenodigm";
	/** Destination Solr core */
	private static final String MP_URL="http://localhost:8983/solr/mp";
	
	private final SolrServer alleleCore;
	private final SolrServer imagesCore;
	private final SolrServer preqcCore;
	private final SolrServer phenodigmCore;
	private final SolrServer mpCore;
	
	// Maps of supporting database content
	Map<String, List<MPHPBean>> mphpBeans;
	Map<String, List<Integer>> termNodeIds;
	Map<Integer, List<MPTopLevelTermBean>> topLevelTerms;
	// Intermediate node IDs and terms can also be used for allChildren
	Map<Integer, List<Integer>> intermediateNodeIds;
	Map<Integer, List<Integer>> childNodeIds;
	// Intermediate terms can also be used for parents
	Map<Integer, List<MPTermNodeBean>> intermediateTerms;
	Map<Integer, List<Integer>> parentNodeIds;
	// Use single synonym hash
	Map<String, List<String>> mpTermSynonyms;
	Map<String, List<String>> ontologySubsets;
	Map<String, List<String>> goIds;

	// MA Term mappings
	Map<String, List<MPTermNodeBean>> maTermNodes;
	Map<String, List<String>> maInferredSelectedTermNodes;
	Map<String, List<String>> maTermSynonyms;
	
	// Phenotype call summaries (1)
	Map<String, List<PhenotypeCallSummaryBean>> phenotypes1;
	Map<String, List<String>> impcBeans;
	Map<String, List<String>> legeacyBeans;
	
	// Phenotype call summaries (2)
	Map<String, List<PhenotypeCallSummaryBean>> phenotypes2;
	Map<String, List<MPStrainBean>> strains;
	Map<String, List<ParamProcedurePipelineBean>> pppBeans;

	public MPIndexer() {
		this.alleleCore = new HttpSolrServer(ALLELE_URL);
		this.imagesCore = new HttpSolrServer(IMAGES_URL);
		this.preqcCore = new HttpSolrServer(PREQC_URL);
		this.phenodigmCore = new HttpSolrServer(PHENODIGM_URL);
		this.mpCore = new HttpSolrServer(MP_URL);
	}
	
	public void run() throws SolrServerException, SQLException {
		initialiseSupportingBeans();
		
		// Loop through the mp_term_infos
		String q = "select 'mp' as dataType, ti.term_id, ti.name, ti.definition, nt.node_id from mp_term_infos ti, mp_node2term nt where ti.term_id=nt.term_id and ti.term_id !='MP:0000001' order by ti.term_id";
		PreparedStatement ps = ontoDbConnection.prepareStatement(q);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			String termId = rs.getString("term_id");
			
			MpDTO mp = new MpDTO();
			mp.setDataType(rs.getString("dataType"));
			mp.setMpId(termId);
			mp.setMpTerm(rs.getString("name"));
			mp.setMpDefinition(rs.getString("definition"));
			mp.setMpNodeId(new ArrayList<Integer>());
			mp.getMpNodeId().add(rs.getInt("node_id"));
			
			addMpHpTerms(mp, mphpBeans.get(termId));
			buildNodes(mp);
			mp.setOntologySubset(ontologySubsets.get(termId));
			mp.setMpTermSynonym(mpTermSynonyms.get(termId));
			mp.setGoId(goIds.get(termId));
		}
	}
	
	private void initialiseSupportingBeans() throws SQLException, SolrServerException {
		// Grab all the supporting database content
		mphpBeans = getMPHPBeans();
		termNodeIds = getNodeIds();
		topLevelTerms = getTopLevelTerms();
		// Intermediate node terms can also be used for allChildren
		intermediateNodeIds = getIntermediateNodeIds();
		// ChildNodeIds is inverse of intermediateNodeIds
		childNodeIds = getChildNodeIds();
		// Intermediate terms can also be used for parents
		intermediateTerms = getIntermediateTerms();
		parentNodeIds = getParentNodeIds();
		// Use single synonym hash
		mpTermSynonyms = getMPTermSynonyms();
		ontologySubsets = getOntologySubsets();
		goIds = getGOIds();

		// MA Term mappings
		maTermNodes = getMATermNodes();
		maInferredSelectedTermNodes = getInferredSelectedMATermNodes();
		maTermSynonyms = getMATermSynonyms();
		
		// Phenotype call summaries (1)
		phenotypes1 = getPhenotypeCallSummary1();
		impcBeans = getImpcPipe();
		legeacyBeans = getLegacyPipe();
		
		// Phenotype call summaries (2)
		phenotypes2 = getPhenotypeCallSummary2();
		strains = getStrains();
		pppBeans = getPPPBeans();
	}
	
	private Map<String, List<MPHPBean>> getMPHPBeans() throws SolrServerException {
		Map<String, List<MPHPBean>> beans = new HashMap<>();
		
		SolrQuery query = new SolrQuery("*:*");
		query.addFilterQuery("type:mp_hp");
		query.setFields("mp_id", "hp_id", "hp_term");
		query.setRows(5000);
		
		QueryResponse response = phenodigmCore.query(query);
		List<MPHPBean> docs = response.getBeans(MPHPBean.class);
		int count = 0;
		for (MPHPBean doc : docs) {
			if (!beans.containsKey(doc.getMpId())) {
				beans.put(doc.getMpId(), new ArrayList<MPHPBean>());
			}
			beans.get(doc.getMpId()).add(doc);
			count ++;
		}
		logger.debug("Loaded {} mphp docs", count);
		
		return beans;
	}
	
	private Map<String, List<Integer>> getNodeIds() throws SQLException {
		Map<String, List<Integer>> beans = new HashMap<>();
		
		String q = "select nt.node_id, ti.term_id from mp_term_infos ti, mp_node2term nt where ti.term_id=nt.term_id and ti.term_id !='MP:0000001'";
		PreparedStatement ps = ontoDbConnection.prepareStatement(q);
		ResultSet rs = ps.executeQuery();
		int count = 0;
		while (rs.next()) {
			String tId = rs.getString("term_id");
			int nId = rs.getInt("node_id");
			if (!beans.containsKey(tId)) {
				beans.put(tId, new ArrayList<Integer>());
			}
			beans.get(tId).add(nId);
			count ++;
		}
		logger.debug("Loaded {} node Ids", count);
		
		return beans;
	}

	private Map<Integer, List<MPTopLevelTermBean>> getTopLevelTerms() throws SQLException {
		Map<Integer, List<MPTopLevelTermBean>> beans = new HashMap<>();
		
		String q = "select lv.node_id as mp_node_id, ti.term_id, ti.name, ti.definition, concat(ti.name, '___', ti.term_id) as top_level_mp_term_id from mp_node_top_level lv inner join mp_node2term nt on lv.top_level_node_id=nt.node_id inner join mp_term_infos ti on nt.term_id=ti.term_id and ti.term_id!='MP:0000001'";
		PreparedStatement ps = ontoDbConnection.prepareStatement(q);
		ResultSet rs = ps.executeQuery();
		int count = 0;
		while (rs.next()) {
			int nId = rs.getInt("mp_node_id");
			
			MPTopLevelTermBean bean = new MPTopLevelTermBean();
			bean.setTermId(rs.getString("term_id"));
			bean.setName(rs.getString("name"));
			bean.setDefinition(rs.getString("definition"));
			bean.setTopLevelMPTermId(rs.getString("top_level_mp_term_id"));
			
			if (!beans.containsKey(nId)) {
				beans.put(nId, new ArrayList<MPTopLevelTermBean>());
			}
			beans.get(nId).add(bean);
			count ++;
		}
		logger.debug("Loaded {} top level terms", count);
		
		return beans;
	}
	
	/**
	 * Build a map of child node ID -> node IDs, to use to build the intermediate nodes.
	 * @return the map.
	 * @throws SQLException
	 */
	private Map<Integer, List<Integer>> getIntermediateNodeIds() throws SQLException {
		Map<Integer, List<Integer>> beans = new HashMap<>();
		
		String q = "select node_id, child_node_id from mp_node_subsumption_fullpath";
		PreparedStatement ps = ontoDbConnection.prepareStatement(q);
		ResultSet rs = ps.executeQuery();
		int count = 0;
		while (rs.next()) {
			int childId = rs.getInt("child_node_id");
			int nodeId = rs.getInt("node_id");
			if (!beans.containsKey(childId)) {
				beans.put(childId, new ArrayList<Integer>());
			}
			beans.get(childId).add(nodeId);
			count ++;
		}
		logger.debug("Loaded {} intermediate node Ids", count);
		
		return beans;
	}

	/**
	 * Build a map of node ID -> child node IDs.
	 * @return the map.
	 * @throws SQLException
	 */
	private Map<Integer, List<Integer>> getChildNodeIds() throws SQLException {
		Map<Integer, List<Integer>> beans = new HashMap<>();
		
		String q = "select node_id, child_node_id from mp_node_subsumption_fullpath";
		PreparedStatement ps = ontoDbConnection.prepareStatement(q);
		ResultSet rs = ps.executeQuery();
		int count = 0;
		while (rs.next()) {
			int nId = rs.getInt("node_id");
			int childId = rs.getInt("child_node_id");
			if (!beans.containsKey(nId)) {
				beans.put(nId, new ArrayList<Integer>());
			}
			beans.get(nId).add(childId);
			count ++;
		}
		logger.debug("Loaded {} child node Ids", count);
		
		return beans;
	}

	private Map<Integer, List<MPTermNodeBean>> getIntermediateTerms() throws SQLException {
		Map<Integer, List<MPTermNodeBean>> beans = new HashMap<>();
		
		String q = "select nt.node_id, ti.term_id, ti.name, ti.definition from mp_term_infos ti, mp_node2term nt where ti.term_id=nt.term_id and ti.term_id !='MP:0000001'";
		PreparedStatement ps = ontoDbConnection.prepareStatement(q);
		ResultSet rs = ps.executeQuery();
		int count = 0;
		while (rs.next()) {
			int nId = rs.getInt("node_id");
			
			MPTermNodeBean bean = new MPTermNodeBean();
			bean.setTermId(rs.getString("term_id"));
			bean.setName(rs.getString("name"));
			bean.setDefinition(rs.getString("definition"));
			
			if (!beans.containsKey(nId)) {
				beans.put(nId, new ArrayList<MPTermNodeBean>());
			}
			beans.get(nId).add(bean);
			count ++;
		}
		logger.debug("Loaded {} intermediate level terms", count);
		
		return beans;
	}
	
	private Map<Integer, List<Integer>> getParentNodeIds() throws SQLException {
		Map<Integer, List<Integer>> beans = new HashMap<>();
		
		String q = "select parent_node_id, child_node_id from mp_parent_children";
		PreparedStatement ps = ontoDbConnection.prepareStatement(q);
		ResultSet rs = ps.executeQuery();
		int count = 0;
		while (rs.next()) {
			int nId = rs.getInt("child_node_id");
			int parentId = rs.getInt("parent_node_id");
			if (!beans.containsKey(nId)) {
				beans.put(nId, new ArrayList<Integer>());
			}
			beans.get(nId).add(parentId);
			count ++;
		}
		logger.debug("Loaded {} parent node Ids", count);
		
		return beans;
	}

	private Map<String, List<String>> getMPTermSynonyms() throws SQLException {
		Map<String, List<String>> beans = new HashMap<>();
		
		String q = "select term_id, syn_name from mp_synonyms";
		PreparedStatement ps = ontoDbConnection.prepareStatement(q);
		ResultSet rs = ps.executeQuery();
		int count = 0;
		while (rs.next()) {
			String tId = rs.getString("term_id");
			String syn = rs.getString("syn_name");
			if (!beans.containsKey(tId)) {
				beans.put(tId, new ArrayList<String>());
			}
			beans.get(tId).add(syn);
			count ++;
		}
		logger.debug("Loaded {} MP term synonyms", count);
		
		return beans;
	}
	
	private Map<String, List<MPTermNodeBean>> getMATermNodes() throws SQLException {
		Map<String, List<MPTermNodeBean>> beans = new HashMap<>();
		
		String q = "select mp.term_id, ti.term_id as ma_term_id, ti.name as ma_term_name from mp_mappings mp inner join ma_term_infos ti on mp.mapped_term_id=ti.term_id and mp.ontology='MA'";
		PreparedStatement ps = ontoDbConnection.prepareStatement(q);
		ResultSet rs = ps.executeQuery();
		int count = 0;
		while (rs.next()) {
			String tId = rs.getString("term_id");
			String maTermId = rs.getString("ma_term_id");
			String maTermName = rs.getString("ma_term_name");
			MPTermNodeBean bean = new MPTermNodeBean();
			bean.setTermId(maTermId);
			bean.setName(maTermName);
			if (!beans.containsKey(tId)) {
				beans.put(tId, new ArrayList<MPTermNodeBean>());
			}
			beans.get(tId).add(bean);
			count ++;
		}
		logger.debug("Loaded {} MA term nodes", count);
		
		return beans;
	}
	
	private Map<String, List<String>> getInferredSelectedMATermNodes() throws SQLException {
		Map<String, List<String>> beans = new HashMap<>();
		
		String q = "select distinct ti.term_id, ti.name from ma_node2term nt, ma_node_2_selected_top_level_mapping m, ma_term_infos ti where nt.node_id=m.node_id and m.top_level_term_id=ti.term_id";
		PreparedStatement ps = ontoDbConnection.prepareStatement(q);
		ResultSet rs = ps.executeQuery();
		int count = 0;
		while (rs.next()) {
			String tId = rs.getString("term_id");
			String name = rs.getString("name");
			if (!beans.containsKey(tId)) {
				beans.put(tId, new ArrayList<String>());
			}
			beans.get(tId).add(name);
			count ++;
		}
		logger.debug("Loaded {} inferred selected MA term nodes", count);
		
		return beans;
	}
	
	private Map<String, List<String>> getMATermSynonyms() throws SQLException {
		Map<String, List<String>> beans = new HashMap<>();
		
		String q = "select term_id, syn_name from ma_synonyms";
		PreparedStatement ps = ontoDbConnection.prepareStatement(q);
		ResultSet rs = ps.executeQuery();
		int count = 0;
		while (rs.next()) {
			String tId = rs.getString("term_id");
			String syn = rs.getString("syn_name");
			if (!beans.containsKey(tId)) {
				beans.put(tId, new ArrayList<String>());
			}
			beans.get(tId).add(syn);
			count ++;
		}
		logger.debug("Loaded {} MA term synonyms", count);
		
		return beans;
	}
	
	private Map<String, List<String>> getOntologySubsets() throws SQLException {
		Map<String, List<String>> beans = new HashMap<>();
		
		String q = "select term_id, subset from mp_term_subsets";
		PreparedStatement ps = ontoDbConnection.prepareStatement(q);
		ResultSet rs = ps.executeQuery();
		int count = 0;
		while (rs.next()) {
			String tId = rs.getString("term_id");
			String subset = rs.getString("subset");
			if (!beans.containsKey(tId)) {
				beans.put(tId, new ArrayList<String>());
			}
			beans.get(tId).add(subset);
			count ++;
		}
		logger.debug("Loaded {} subsets", count);
		
		return beans;
	}
	
	private Map<String, List<String>> getGOIds() throws SQLException {
		Map<String, List<String>> beans = new HashMap<>();
		
		String q = "select distinct x.xref_id, ti.term_id from mp_dbxrefs x inner join mp_term_infos ti on x.term_id=ti.term_id and x.xref_id like 'GO:%'";
		PreparedStatement ps = ontoDbConnection.prepareStatement(q);
		ResultSet rs = ps.executeQuery();
		int count = 0;
		while (rs.next()) {
			String tId = rs.getString("term_id");
			String xrefId = rs.getString("xref_id");
			if (!beans.containsKey(tId)) {
				beans.put(tId, new ArrayList<String>());
			}
			beans.get(tId).add(xrefId);
			count ++;
		}
		logger.debug("Loaded {} xrefs", count);
		
		return beans;
	}
	
	private Map<String, List<PhenotypeCallSummaryBean>> getPhenotypeCallSummary1() throws SQLException {
		Map<String, List<PhenotypeCallSummaryBean>> beans = new HashMap<>();
		
		String q = "select distinct gf_acc, mp_acc, concat(mp_acc,'_',gf_acc) as mp_mgi, parameter_id, procedure_id, pipeline_id, allele_acc, strain_acc from phenotype_call_summary where p_value &lt;= 0.0001 and gf_db_id=3 and gf_acc like 'MGI:%' and allele_acc is not null and strain_acc is not null";
		PreparedStatement ps = komp2DbConnection.prepareStatement(q);
		ResultSet rs = ps.executeQuery();
		int count = 0;
		while (rs.next()) {
			PhenotypeCallSummaryBean bean = new PhenotypeCallSummaryBean();
			
			String mpAcc = rs.getString("mp_acc");
			
			bean.setGfAcc(rs.getString("gf_acc"));
			bean.setMpAcc(mpAcc);
			bean.setMpMgi(rs.getString("mp_mgi"));
			bean.setParameterId(rs.getString("parameter_id"));
			bean.setProcedureId(rs.getString("procedure_id"));
			bean.setPipelineId(rs.getString("pipeline_id"));
			bean.setAlleleAcc(rs.getString("allele_acc"));
			bean.setStrainAcc(rs.getString("strain_acc"));
			
			if (!beans.containsKey(mpAcc)) {
				beans.put(mpAcc, new ArrayList<PhenotypeCallSummaryBean>());
			}
			beans.get(mpAcc).add(bean);
			count ++;
		}
		logger.debug("Loaded {} phenotype call summaries (1)", count);
		
		return beans;
	}
	
	private Map<String, List<String>> getImpcPipe() throws SQLException {
		Map<String, List<String>> beans = new HashMap<>();
		
		String q = "select distinct external_db_id as 'impc', concat (mp_acc,'_', gf_acc) as mp_mgi from phenotype_call_summary where p_value &lt; 0.0001 and external_db_id = 22";
		PreparedStatement ps = ontoDbConnection.prepareStatement(q);
		ResultSet rs = ps.executeQuery();
		int count = 0;
		while (rs.next()) {
			String tId = rs.getString("mp_mgi");
			String impc = rs.getString("impc");
			if (!beans.containsKey(tId)) {
				beans.put(tId, new ArrayList<String>());
			}
			beans.get(tId).add(impc);
			count ++;
		}
		logger.debug("Loaded {} IMPC", count);
		
		return beans;
	}
	
	private Map<String, List<String>> getLegacyPipe() throws SQLException {
		Map<String, List<String>> beans = new HashMap<>();
		
		String q = "select distinct external_db_id as 'legacy', concat (mp_acc,'_', gf_acc) as mp_mgi from phenotype_call_summary where p_value &lt; 0.0001 and external_db_id = 12";
		PreparedStatement ps = ontoDbConnection.prepareStatement(q);
		ResultSet rs = ps.executeQuery();
		int count = 0;
		while (rs.next()) {
			String tId = rs.getString("mp_mgi");
			String legacy = rs.getString("legacy");
			if (!beans.containsKey(tId)) {
				beans.put(tId, new ArrayList<String>());
			}
			beans.get(tId).add(legacy);
			count ++;
		}
		logger.debug("Loaded {} legacy", count);
		
		return beans;
	}
	
	private Map<String, List<PhenotypeCallSummaryBean>> getPhenotypeCallSummary2() throws SQLException {
		Map<String, List<PhenotypeCallSummaryBean>> beans = new HashMap<>();
		
		String q = "select distinct gf_acc, mp_acc, parameter_id, procedure_id, pipeline_id, concat(parameter_id,'_',procedure_id,'_',pipeline_id) as ididid, allele_acc, strain_acc from phenotype_call_summary where gf_db_id=3 and gf_acc like 'MGI:%' and allele_acc is not null and strain_acc is not null";
		PreparedStatement ps = komp2DbConnection.prepareStatement(q);
		ResultSet rs = ps.executeQuery();
		int count = 0;
		while (rs.next()) {
			PhenotypeCallSummaryBean bean = new PhenotypeCallSummaryBean();
			
			String mpAcc = rs.getString("mp_acc");
			
			bean.setGfAcc(rs.getString("gf_acc"));
			bean.setMpAcc(mpAcc);
			bean.setParamProcPipelineId(rs.getString("ididid"));
			bean.setParameterId(rs.getString("parameter_id"));
			bean.setProcedureId(rs.getString("procedure_id"));
			bean.setPipelineId(rs.getString("pipeline_id"));
			bean.setAlleleAcc(rs.getString("allele_acc"));
			bean.setStrainAcc(rs.getString("strain_acc"));
			
			if (!beans.containsKey(mpAcc)) {
				beans.put(mpAcc, new ArrayList<PhenotypeCallSummaryBean>());
			}
			beans.get(mpAcc).add(bean);
			count ++;
		}
		logger.debug("Loaded {} phenotype call summaries (2)", count);
		
		return beans;
	}
	
	private Map<String, List<MPStrainBean>> getStrains() throws SQLException {
		Map<String, List<MPStrainBean>> beans = new HashMap<>();
		
		String q = "select distinct name, acc from strain where db_id=3";
		PreparedStatement ps = komp2DbConnection.prepareStatement(q);
		ResultSet rs = ps.executeQuery();
		int count = 0;
		while (rs.next()) {
			MPStrainBean bean = new MPStrainBean();
			
			String acc = rs.getString("acc");
			
			bean.setAcc(acc);
			bean.setName(rs.getString("name"));
			
			if (!beans.containsKey(acc)) {
				beans.put(acc, new ArrayList<MPStrainBean>());
			}
			beans.get(acc).add(bean);
			count ++;
		}
		logger.debug("Loaded {} strain beans", count);
		
		return beans;
	}
	
	private Map<String, List<ParamProcedurePipelineBean>> getPPPBeans() throws SQLException {
		Map<String, List<ParamProcedurePipelineBean>> beans = new HashMap<>();
		
		String q = "select concat(pp.id,'_',pproc.id,'_',ppipe.id) as ididid, pp.name as parameter_name, pp.stable_key as parameter_stable_key, pp.stable_id as parameter_stable_id, pproc.name as procedure_name, pproc.stable_key as procedure_stable_key, pproc.stable_id as procedure_stable_id, ppipe.name as pipeline_name, ppipe.stable_key as pipeline_key, ppipe.stable_id as pipeline_stable_id from phenotype_parameter pp inner join phenotype_procedure_parameter ppp on pp.id=ppp.parameter_id inner join phenotype_procedure pproc on ppp.procedure_id=pproc.id inner join phenotype_pipeline_procedure ppproc on pproc.id=ppproc.procedure_id inner join phenotype_pipeline ppipe on ppproc.pipeline_id=ppipe.id";
		PreparedStatement ps = komp2DbConnection.prepareStatement(q);
		ResultSet rs = ps.executeQuery();
		int count = 0;
		while (rs.next()) {
			ParamProcedurePipelineBean bean = new ParamProcedurePipelineBean();
			
			String id = rs.getString("ididid");
			
			bean.setParameterName(rs.getString("parameter_name"));
			bean.setParameterStableId(rs.getString("parameter_stable_id"));
			bean.setParameterStableKey(rs.getString("paramter_stable_key"));
			bean.setProcedureName(rs.getString("procedure_name"));
			bean.setProcedureStableId(rs.getString("procedure_stable_id"));
			bean.setProcedureStableKey(rs.getString("paramter_stable_key"));
			bean.setPipelineName(rs.getString("pipeline_name"));
			bean.setPipelineStableId(rs.getString("pipeline_stable_id"));
			bean.setPipelineStableKey(rs.getString("paramter_stable_key"));
			
			if (!beans.containsKey(id)) {
				beans.put(id, new ArrayList<ParamProcedurePipelineBean>());
			}
			beans.get(id).add(bean);
			count ++;
		}
		logger.debug("Loaded {} PPP beans", count);
		
		return beans;
	}
	
	private void addMpHpTerms(MpDTO mp, List<MPHPBean> hpBeans) {
		List<String> hpIds = new ArrayList<>(hpBeans.size());
		List<String> hpTerms = new ArrayList<>(hpBeans.size());
		
		for (MPHPBean bean : hpBeans) {
			hpIds.add(bean.getHpId());
			hpIds.add(bean.getHpTerm());
		}
		
		mp.setHpId(hpIds);
		mp.setHpTerm(hpTerms);
	}
	
	private void buildNodes(MpDTO mp) {
		List<Integer> nodeIds = termNodeIds.get(mp.getMpId());
		
		for (Integer nodeId : nodeIds) {
			// Build the top level nodes
			buildTopLevelNodes(mp, nodeId);
			buildIntermediateLevelNodes(mp, nodeId);
			buildChildLevelNodes(mp, nodeId);
			buildParentLevelNodes(mp, nodeId);
		}
	}
	
	private void buildTopLevelNodes(MpDTO mp, int nodeId) {
		List<MPTopLevelTermBean> topLevelTermBeans = topLevelTerms.get(nodeId);
		List<String> topLevelMpIds = new ArrayList<>(topLevelTermBeans.size());
		List<String> topLevelMpTerms = new ArrayList<>(topLevelTermBeans.size());
		List<String> topLevelMpTermIds = new ArrayList<>(topLevelTermBeans.size());
		Set<String> topLevelSynonyms = new HashSet<>();
		
		for (MPTopLevelTermBean bean : topLevelTermBeans) {
			topLevelMpIds.add(bean.getTermId());
			topLevelMpTerms.add(bean.getName());
			topLevelMpTermIds.add(bean.getTopLevelMPTermId());
			topLevelSynonyms.addAll(mpTermSynonyms.get(bean.getTermId()));
		}
		
		mp.setTopLevelMpId(topLevelMpIds);
		mp.setTopLevelMpTerm(topLevelMpTerms);
		mp.setTopLevelMpTermId(topLevelMpTermIds);
		mp.setTopLevelMpTermSynonym(new ArrayList<String>(topLevelSynonyms));
	}
	
	private void buildIntermediateLevelNodes(MpDTO mp, int nodeId) {
		List<String> intermediateTermIds = new ArrayList<>();
		List<String> intermediateTermNames = new ArrayList<>();
		Set<String> intermediateSynonyms = new HashSet<>();
		
		for (Integer intId : intermediateNodeIds.get(nodeId)) {
			for (MPTermNodeBean bean : intermediateTerms.get(intId)) {
				intermediateTermIds.add(bean.getTermId());
				intermediateTermNames.add(bean.getName());
				intermediateSynonyms.addAll(mpTermSynonyms.get(intId));
			}
		}
		
		mp.setIntermediateMpId(intermediateTermIds);
		mp.setIntermediateMpTerm(intermediateTermNames);
		mp.setIntermediateMpTermSynonym(new ArrayList<String>(intermediateSynonyms));
	}
	
	private void buildChildLevelNodes(MpDTO mp, int nodeId) {
		List<String> childTermIds = new ArrayList<>();
		List<String> childTermNames = new ArrayList<>();
		Set<String> childSynonyms = new HashSet<>();
		
		for (Integer childId : childNodeIds.get(nodeId)) {
			for (MPTermNodeBean bean : intermediateTerms.get(childId)) {
				childTermIds.add(bean.getTermId());
				childTermNames.add(bean.getName());
				childSynonyms.addAll(mpTermSynonyms.get(childId));
			}
		}
		
		mp.setChildMpId(childTermIds);
		mp.setChildMpTerm(childTermNames);
		mp.setChildMpTermSynonym(new ArrayList<String>(childSynonyms));
	}
	
	private void buildParentLevelNodes(MpDTO mp, int nodeId) {
		List<String> parentTermIds = new ArrayList<>();
		List<String> parentTermNames = new ArrayList<>();
		Set<String> parentSynonyms = new HashSet<>();
		
		for (Integer parentId : parentNodeIds.get(nodeId)) {
			for (MPTermNodeBean bean : intermediateTerms.get(parentId)) {
				parentTermIds.add(bean.getTermId());
				parentTermNames.add(bean.getName());
				parentSynonyms.addAll(mpTermSynonyms.get(parentId));
			}
		}
		
		mp.setParentMpId(parentTermIds);
		mp.setParentMpTerm(parentTermNames);
		mp.setParentMpTermSynonym(new ArrayList<String>(parentSynonyms));
	}
	
	private void addTermSubsets(MpDTO mp) {
		mp.setOntologySubset(ontologySubsets.get(mp.getMpId()));
	}
	

	public static void main(String[] args) throws SQLException, InterruptedException, JAXBException, IOException, NoSuchAlgorithmException, KeyManagementException, SolrServerException {
		OptionParser parser = new OptionParser();

		// parameter to indicate which spring context file to use
		parser.accepts("context").withRequiredArg().ofType(String.class);

		OptionSet options = parser.parse(args);
		String context = (String) options.valuesOf("context").get(0);

		logger.info("Using application context file {}", context);

		// Wire up spring support for this application
		MPIndexer main = new MPIndexer();

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
		applicationContext.getAutowireCapableBeanFactory().autowireBeanProperties(main, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);

		// allow hibernate session to stay open the whole execution
		PlatformTransactionManager transactionManager = (PlatformTransactionManager) applicationContext.getBean("transactionManager");
		DefaultTransactionAttribute transactionAttribute = new DefaultTransactionAttribute(TransactionDefinition.PROPAGATION_REQUIRED);
		transactionAttribute.setIsolationLevel(TransactionDefinition.ISOLATION_SERIALIZABLE);
		transactionManager.getTransaction(transactionAttribute);

		DataSource komp2DS = ((DataSource) applicationContext.getBean("komp2DataSource"));
		komp2DbConnection = komp2DS.getConnection();
		DataSource ontoDS = ((DataSource)applicationContext.getBean("ontodbDataSource"));
		ontoDbConnection = ontoDS.getConnection();

		main.run();

		logger.info("Process finished.  Exiting.");
	}

}
