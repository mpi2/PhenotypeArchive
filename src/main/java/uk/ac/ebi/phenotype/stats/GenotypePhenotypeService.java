package uk.ac.ebi.phenotype.stats;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.ac.ebi.generic.util.JSONRestUtil;
import uk.ac.ebi.phenotype.dao.PhenotypePipelineDAO;
import uk.ac.ebi.phenotype.pojo.Allele;
import uk.ac.ebi.phenotype.pojo.CategoricalResult;
import uk.ac.ebi.phenotype.pojo.Datasource;
import uk.ac.ebi.phenotype.pojo.DatasourceEntityId;
import uk.ac.ebi.phenotype.pojo.GenomicFeature;
import uk.ac.ebi.phenotype.pojo.ObservationType;
import uk.ac.ebi.phenotype.pojo.OntologyTerm;
import uk.ac.ebi.phenotype.pojo.Parameter;
import uk.ac.ebi.phenotype.pojo.PhenotypeCallSummary;
import uk.ac.ebi.phenotype.pojo.PipelineSolrImpl;
import uk.ac.ebi.phenotype.pojo.Procedure;
import uk.ac.ebi.phenotype.pojo.Project;
import uk.ac.ebi.phenotype.pojo.SexType;
import uk.ac.ebi.phenotype.pojo.StatisticalResult;
import uk.ac.ebi.phenotype.pojo.UnidimensionalResult;
import uk.ac.ebi.phenotype.pojo.ZygosityType;
import uk.ac.ebi.phenotype.util.PhenotypeFacetResult;
import uk.ac.ebi.phenotype.web.util.HttpProxy;


@Service
public class GenotypePhenotypeService {

	@Autowired
	PhenotypePipelineDAO pipelineDAO;
	
	private HttpSolrServer solr;
	
	public GenotypePhenotypeService(String baseUrl){
		solr = getSolrInstance(baseUrl);
	}
	
	public List<Group> getGenesBy(String phenotype_id, String sex) throws SolrServerException{
		//males only
		SolrQuery q = new SolrQuery()
		.setQuery("(mp_term_id:\"" + phenotype_id + "\" OR top_level_mp_term_id:\"" + phenotype_id + "\") AND (strain_accession_id:\"MGI:2159965\" OR strain_accession_id:\"MGI:2164831\")")
		.setFilterQueries("resource_fullname:EuroPhenome")
		.setRows(10000);
		q.set("group.field", "marker_symbol");
		q.set("group", true);
		if (sex != null){
			q.addFilterQuery("sex:" + sex);
		}
		QueryResponse results = solr.query(q);
		return results.getGroupResponse().getValues().get(0).getValues();
	}
	
	private HttpSolrServer getSolrInstance(String solrBaseUrl){
		Proxy proxy; 
		HttpSolrServer server = null;
		try {
			proxy = (new HttpProxy()).getProxy(new URL(solrBaseUrl));
			if (proxy != null) {
				DefaultHttpClient client = new DefaultHttpClient();
				client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
				server = new HttpSolrServer(solrBaseUrl, client);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		if(server == null){
			server = new HttpSolrServer(solrBaseUrl);
		}
		return server;
	}
	
	public List<String> getGenesAssocByParamAndMp (String parameterStableId, String phenotype_id) throws SolrServerException{
		List<String> res = new ArrayList<String>();
		SolrQuery query = new SolrQuery()
		.setQuery("(mp_term_id:\"" + phenotype_id + "\" OR top_level_mp_term_id:\"" + phenotype_id + "\") AND (strain_accession_id:\"MGI:2159965\" OR strain_accession_id:\"MGI:2164831\") AND parameter_stable_id:\"" + parameterStableId+"\"")
		.setFilterQueries("resource_fullname:EuroPhenome")
		.setRows(10000);	
		query.set("group.field", "marker_accession_id");
		query.set("group", true);
		List<Group> groups = solr.query(query).getGroupResponse().getValues().get(0).getValues();
		for (Group gr : groups){
			if (!res.contains((String)gr.getGroupValue())){
				res.add((String) gr.getGroupValue());
			}
		}
		return res;
	}
	

	public Set<String> getAllGenes() throws SolrServerException{
		
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery("marker_accession_id:*");
		solrQuery.setRows(1000000);
		solrQuery.setFields("marker_accession_id");
		QueryResponse rsp = null;
		rsp = solr.query(solrQuery);
		SolrDocumentList res = rsp.getResults();
		HashSet<String> allGenes = new HashSet<String>();
		for (SolrDocument doc: res){
			allGenes.add((String) doc.getFieldValue("marker_accession_id"));
		}
		return allGenes;
	}
	
	/*
	 * Methods used by PhenotypeSummaryDAO
	 */
	
	public SolrDocumentList getPhenotypesForTopLevelTerm(String gene, String mpID) throws SolrServerException {	
		SolrDocumentList result = runQuery("marker_accession_id:\"" + gene + "\" AND top_level_mp_term_id:\"" + mpID + "\"");
		// mpID might be in mp_id instead of top level field
		if (result.size() == 0 || result == null)
		//	result = runQuery("marker_accession_id:" + gene.replace(":", "\\:") + " AND mp_term_id:" + mpID.replace(":", "\\:"));
			result = runQuery("marker_accession_id:\"" + gene + "\" AND mp_term_id:\"" + mpID + "\" AND -resource_name:IMPC");
		return result;
	}
	
	public SolrDocumentList getPgehnotypes(String gene) throws SolrServerException{
		SolrDocumentList result = runQuery("marker_accession_id:\"" + gene + "\"");
		return result;
	}
	
	private SolrDocumentList runQuery(String q) throws SolrServerException {
		SolrQuery solrQuery = new SolrQuery().setQuery(q);
		solrQuery.setRows(1000000);
		QueryResponse rsp = null;
		rsp = solr.query(solrQuery);
		return rsp.getResults();
	}
	
	public HashMap<String, String> getTopLevelMPTerms(String gene) throws SolrServerException {
		HashMap<String,String> tl = new HashMap<String,String>(); 
//		SolrDocumentList result = runQuery("marker_accession_id:" + gene.replace(":", "\\:"));
		SolrDocumentList result = runQuery("marker_accession_id:\"" + gene + "\" AND -resource_name:IMPC");
		if (result.size() > 0) {
			for (int i = 0; i < result.size(); i++) {
				SolrDocument doc = result.get(i);
				if (doc.getFieldValue("top_level_mp_term_id") != null){
					ArrayList<String> tlTermIDs = (ArrayList<String>) doc.getFieldValue("top_level_mp_term_id");
					ArrayList<String> tlTermNames = (ArrayList<String>) doc.getFieldValue("top_level_mp_term_name");
					int len = tlTermIDs.size();
					for (int k = 0 ; k < len ; k++){
						tl.put( tlTermIDs.get(k), tlTermNames.get(k));
					}
	//					tl.put((String) doc.getFieldValue("top_level_mp_term_id"), (String) doc.getFieldValue("top_level_mp_term_name"));
				}
				else { // it seems that when the term id is a top level term itself the top level term field 
					tl.put((String) doc.getFieldValue("mp_term_id"), (String) doc.getFieldValue("mp_term_name"));					
				}
			}
		}		
		return tl;
	}
	/*
	 * End of Methods for PhenotypeSummaryDAO
	 */
	
	
	/*
	 * Methods for PipelineSolrImpl
	 */
	public Parameter getParameterByStableId(
			String paramStableId, String queryString) throws IOException,
			URISyntaxException {
		String pipelineCoreString="pipeline";
		String solrUrl = solr.getBaseURL()
				+ "/select/?q=parameter_stable_id:\""
				+ paramStableId
				+ "\"&rows=10000000&version=2.2&start=0&indent=on&wt=json";
		if (queryString.startsWith("&")) {
			solrUrl += queryString;
		} else {// add an ampersand parameter splitter if not one as we need
				// one to add to the already present solr query string
			solrUrl += "&" + queryString;
		}
		return createParameter(solrUrl);
	}
	
	private Parameter createParameter(String url) throws IOException, URISyntaxException {
		Parameter parameter=new Parameter();
		JSONObject results = null;
		results = JSONRestUtil.getResults(url);

		JSONArray docs = results.getJSONObject("response").getJSONArray("docs");
		for (Object doc : docs) {
			JSONObject paramDoc = (JSONObject) doc;
			String isDerivedInt = paramDoc.getString("parameter_derived");
			boolean derived=false;
			if( isDerivedInt.equals("true")) {
				derived=true;
			}
			parameter.setDerivedFlag(derived);
			parameter.setName(paramDoc.getString("parameter_name"));
			//we need to set is derived in the solr core!
			//pipeline core parameter_derived field
			parameter.setStableId(paramDoc.getString("parameter_stable_id"));
			if (paramDoc.containsKey("procedure_stable_key")) {
				parameter.setStableKey(Integer.parseInt(paramDoc
						.getString("procedure_stable_key")));
			}
			
		}
		return parameter;
	}
	/* 
	 * End of method for PipelineSolrImpl
	 */

	
	
	/*
	 * Methods used by PhenotypeCallSummarySolrImpl
	 */
	public List<? extends StatisticalResult> getStatsResultFor(String accession, String parameterStableId, ObservationType observationType, String strainAccession) throws IOException, URISyntaxException {
		
		String solrUrl = solr.getBaseURL();// "http://wwwdev.ebi.ac.uk/mi/solr/genotype-phenotype";
		solrUrl += "/select/?q=marker_accession_id:\""
				+ accession+"\""+"&fq=parameter_stable_id:"+parameterStableId+"&fq=strain_accession_id:\""+strainAccession+"\"&rows=10000000&version=2.2&start=0&indent=on&wt=json";
		List<? extends StatisticalResult> statisticalResult = this.createStatsResultFromSolr(solrUrl, observationType);
		return statisticalResult;
	}

	
	public PhenotypeFacetResult getMPByGeneAccessionAndFilter(
			String accId, String queryString) throws IOException,
			URISyntaxException {

		String solrUrl = solr.getBaseURL()
				+ "/select/?q=marker_accession_id:\""
				+ accId
				+ "\"&fq=-resource_name:IMPC&rows=10000000&version=2.2&start=0&indent=on&wt=json&facet=true&facet.field=resource_fullname&facet.field=top_level_mp_term_name";
		if (queryString.startsWith("&")) {
			solrUrl += queryString;
		} else {// add an ampersand parameter splitter if not one as we need
				// one to add to the already present solr query string
			solrUrl += "&" + queryString;
		}
		return createPhenotypeResultFromSolrResponse(solrUrl);
	}
	
	public PhenotypeFacetResult getMPCallByMPAccessionAndFilter(
			String phenotype_id, String queryString) throws IOException,
			URISyntaxException {
		// http://wwwdev.ebi.ac.uk/mi/solr/genotype-phenotype/select/?q=mp_term_id:MP:0010025&rows=100&version=2.2&start=0&indent=on&defType=edismax&wt=json&facet=true&facet.field=resource_fullname&facet.field=top_level_mp_term_name&
		String solrUrl = solr.getBaseURL()
				+ "/select/?q=(mp_term_id:\""
				+ phenotype_id
				+ "\"+OR+top_level_mp_term_id:\""
				+ phenotype_id
				+ "\")&fq=-resource_name:IMPC&rows=1000000&version=2.2&start=0&indent=on&wt=json&facet=true&facet.field=resource_fullname&facet.field=procedure_name&facet.field=marker_symbol&facet.field=mp_term_name";
		// if (!filterString.equals("")) {
		if (queryString.startsWith("&")) {
			solrUrl += queryString;
		} else {// add an ampersand parameter splitter if not one as we need
				// one to add to the already present solr query string
			solrUrl += "&" + queryString;
		}
		// }

		return createPhenotypeResultFromSolrResponse(solrUrl);

	}
	

	private List<? extends StatisticalResult> createStatsResultFromSolr(String url, ObservationType observationType) throws IOException, URISyntaxException {
		//need some way of determining what type of data and therefor what type of stats result object to create default to unidimensional for now
		List<StatisticalResult> results=new ArrayList<>();
//		StatisticalResult statisticalResult=new StatisticalResult();
		
		JSONObject resultsj = null;
		resultsj = JSONRestUtil.getResults(url);
		JSONArray docs = resultsj.getJSONObject("response").getJSONArray("docs");
		
		if(observationType==ObservationType.unidimensional) {
		UnidimensionalResult unidimensionalResult=new UnidimensionalResult();//dummy result just in case no other cases are met!
		for (Object doc : docs) {
			JSONObject phen = (JSONObject) doc;
			String pValue = phen.getString("p_value");
			String sex = phen.getString("sex");
			String zygosity=phen.getString("zygosity");
			String effectSize=phen.getString("effect_size");
			
			
			//System.out.println("pValue="+pValue);
			if(pValue!=null) {
				unidimensionalResult.setpValue(Double.valueOf(pValue));
				unidimensionalResult.setZygosityType(ZygosityType.valueOf(zygosity));
				unidimensionalResult.setEffectSize(new Double(Double.valueOf(effectSize)));
				unidimensionalResult.setSexType(SexType.valueOf(sex));
			}
			results.add(unidimensionalResult);
		}
		return results;
	}

		
		if(observationType==ObservationType.categorical) {
			CategoricalResult catResult=new CategoricalResult();
			for (Object doc : docs) {
				JSONObject phen = (JSONObject) doc;
				//System.out.println("pValue="+pValue);
				String pValue = phen.getString("p_value");
				String sex = phen.getString("sex");
				String zygosity=phen.getString("zygosity");
				String effectSize=phen.getString("effect_size");
				
				
				//System.out.println("pValue="+pValue);
				if(pValue!=null) {
					catResult.setpValue(Double.valueOf(pValue));
					catResult.setZygosityType(ZygosityType.valueOf(zygosity));
					catResult.setEffectSize(new Double(Double.valueOf(effectSize)));
					catResult.setSexType(SexType.valueOf(sex));
				}
				results.add(catResult);
			}
			return results;
		}
		return results;
		}

		
	private PhenotypeFacetResult createPhenotypeResultFromSolrResponse(
			String url) throws IOException, URISyntaxException {
		PhenotypeFacetResult facetResult = new PhenotypeFacetResult();
		List<PhenotypeCallSummary> list = new ArrayList<PhenotypeCallSummary>();

		JSONObject results = null;
		results = JSONRestUtil.getResults(url);

		JSONArray docs = results.getJSONObject("response").getJSONArray("docs");
		for (Object doc : docs) {
			JSONObject phen = (JSONObject) doc;
			String mpTerm = phen.getString("mp_term_name");
			String mpId = phen.getString("mp_term_id");
			PhenotypeCallSummary sum = new PhenotypeCallSummary();

			OntologyTerm phenotypeTerm = new OntologyTerm();
			phenotypeTerm.setName(mpTerm);
			phenotypeTerm.setDescription(mpTerm);
			DatasourceEntityId mpEntity = new DatasourceEntityId();
			mpEntity.setAccession(mpId);
			phenotypeTerm.setId(mpEntity);
			sum.setPhenotypeTerm(phenotypeTerm);
			if (phen.containsKey("allele_symbol")) {
				Allele allele = new Allele();
				allele.setSymbol(phen.getString("allele_symbol"));
				GenomicFeature alleleGene = new GenomicFeature();
				DatasourceEntityId alleleEntity = new DatasourceEntityId();
				alleleEntity
						.setAccession(phen.getString("allele_accession_id"));
				allele.setId(alleleEntity);
				alleleGene.setId(alleleEntity);
				alleleGene.setSymbol(phen.getString("marker_symbol"));
				allele.setGene(alleleGene);
				sum.setAllele(allele);
			}
			if (phen.containsKey("marker_symbol")) {
				GenomicFeature gf = new GenomicFeature();
				gf.setSymbol(phen.getString("marker_symbol"));
				DatasourceEntityId geneEntity = new DatasourceEntityId();
				geneEntity.setAccession(phen.getString("marker_accession_id"));
				gf.setId(geneEntity);
				sum.setGene(gf);
			}
			if (phen.containsKey("phenotyping_center")){
				sum.setPhenotypeingCenter(phen.getString("phenotyping_center"));
			}
			// GenomicFeature gene=new GenomicFeature();
			// gene.
			// allele.setGene(gene);
			String zygosity = phen.getString("zygosity");
			ZygosityType zyg = ZygosityType.valueOf(zygosity);
			sum.setZygosity(zyg);
			String sex = phen.getString("sex");
			SexType sexType = SexType.valueOf(sex);
			sum.setSex(sexType);
			String provider = phen.getString("resource_fullname");
			Datasource datasource = new Datasource();
			datasource.setName(provider);
			sum.setDatasource(datasource);

			// "parameter_stable_id":"557",
			// "parameter_name":"Bone Mineral Content",
			// "procedure_stable_id":"41",
			// "procedure_stable_key":"41",
			Parameter parameter = new Parameter();
			if (phen.containsKey("parameter_stable_id")) {
				parameter = pipelineDAO.getParameterByStableIdAndVersion(phen.getString("parameter_stable_id"), 1, 0);
			} else {
				System.err.println("parameter_stable_id missing");
			}

			sum.setParameter(parameter);
			Project project = new Project();
			project.setName(phen.getString("project_name"));
			project.setDescription(phen.getString("project_fullname")); // is this right for description? no other field in solr index!!!
			if (phen.containsKey("project_external_id")) {
				sum.setExternalId(phen.getInt("project_external_id"));
			}
			sum.setProject(project);
			// "procedure_stable_id":"77",
			// "procedure_stable_key":"77",
			// "procedure_name":"Plasma Chemistry",
			Procedure procedure = new Procedure();
			if (phen.containsKey("procedure_stable_id")) {
				procedure.setStableId(phen.getString("procedure_stable_id"));
				procedure.setStableKey(Integer.valueOf(phen
						.getString("procedure_stable_key")));
				procedure.setName(phen.getString("procedure_name"));
				sum.setProcedure(procedure);
			} else {
				System.err.println("procedure_stable_id");
			}
			list.add(sum);
		}

		// if (!filterString.equals("")) {//only run facet code if there is a
		// facet in the query!!
		// get the facet information that we can use to create the buttons /
		// dropdowns/ checkboxes
		JSONObject facets = results.getJSONObject("facet_counts")
				.getJSONObject("facet_fields");
		Iterator<String> ite = facets.keys();
		Map<String, Map<String, Integer>> dropdowns = new HashMap<String, Map<String, Integer>>();
		while (ite.hasNext()) {
			Map<String, Integer> map = new HashMap<String, Integer>();
			String key = (String) ite.next();
			JSONArray array = (JSONArray) facets.get(key);
			int i = 0;
			while (i + 1 < array.size()) {
				String facetString = array.get(i).toString();
				int number = array.getInt(i + 1);
				if (number != 0) {// only add if some counts to filter on!
					map.put(facetString, number);
				}
				i += 2;
				// System.out.println("i="+i);
			}
			dropdowns.put(key, map);
		}
		facetResult.setFacetResults(dropdowns);

		// }
		facetResult.setPhenotypeCallSummaries(list);
		return facetResult;
	}
	/*
	 * End of method for PhenotypeCallSummarySolrImpl
	 */

}
