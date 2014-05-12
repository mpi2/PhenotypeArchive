package uk.ac.ebi.phenotype.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.antlr.grammar.v3.ANTLRv3Parser.finallyClause_return;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
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
import uk.ac.ebi.phenotype.pojo.Pipeline;
import uk.ac.ebi.phenotype.pojo.Procedure;
import uk.ac.ebi.phenotype.pojo.Project;
import uk.ac.ebi.phenotype.pojo.SexType;
import uk.ac.ebi.phenotype.pojo.StatisticalResult;
import uk.ac.ebi.phenotype.pojo.UnidimensionalResult;
import uk.ac.ebi.phenotype.pojo.ZygosityType;
import uk.ac.ebi.phenotype.util.PhenotypeFacetResult;


@Service
public class GenotypePhenotypeService {

	@Autowired
	PhenotypePipelineDAO pipelineDAO;
	
	private HttpSolrServer solr;
	
	
	public static final class GenotypePhenotypeField {
		public final static String PHENOTYPING_CENTER = "phenotyping_center"; //
		public final static String ZYGOSITY = "zygosity"; //
		public final static String MARKER_SYMBOL = "marker_symbol";//
		public final static String ALLELE_NAME = "allele_name";//
		public final static String SEX = "sex"; //
		public final static String PROJECT_NAME = "project_name";//
		public final static String STRAIN_NAME = "strain_name"; //
		public final static String PROJECT_FULLNAME = "project_fullname";//
		public final static String EFFECT_SIZE = "effect_size";///
		public final static String MARKER_ACCESSION_ID = "marker_accession_id";//
		public final static String ALLELE_SYMBOL = "allele_symbol";//
		public final static String MP_TERM_NAME = "mp_term_name"; //
		public final static String RESOURCE_NAME = "resource_name"; //
		public final static String DOC_ID = "doc_id";//
		public final static String ALLELE_ACCESSION_ID = "allele_accession_id";//
		public final static String STRAIN_ACCESSION_ID = "strain_accession_id"; //
		public final static String P_VALUE = "p_value";//
		public final static String PCS_ID="doc_id";// use doc_id which is the phenotype call summary Id..
		public final static String RESOURCE_FULLNAME = "resource_fullname";//
		public final static String MP_TERM_ID = "mp_term_id"; //
		public final static String PROJECT_EXTERNAL_ID = "project_external_id";//
		public final static String TOP_LEVEL_MP_TERM_NAME = "top_level_mp_term_name";//
		public final static String TOP_LEVEL_MP_TERM_ID = "top_level_mp_term_id";//
		public final static String PARAMETER_STABLE_ID = "parameter_stable_id";//
		public final static String PARAMETER_NAME = "parameter_name";//
		public final static String PROCEDURE_STABLE_ID = "procedure_stable_id";//
		public final static String PROCEDURE_STABLE_KEY = "procedure_stable_key";		//
		public final static String PROCEDURE_NAME = "procedure_name";		//
		public final static String PIPELINE_STABLE_ID = "pipeline_stable_id";		//
		public final static String PIPELINE_STABLE_KEY = "pipeline_stable_key";		//
		public final static String PIPELINE_NAME = "pipeline_name";		//
	}
	
	public GenotypePhenotypeService(String solrUrl){
		solr = new HttpSolrServer(solrUrl);
	}
	
	public List<Group> getGenesBy(String phenotype_id, String sex) throws SolrServerException{
		//males only
		SolrQuery q = new SolrQuery()
		.setQuery("(" + GenotypePhenotypeField.MP_TERM_ID + ":\"" + phenotype_id + "\" OR " + GenotypePhenotypeField.TOP_LEVEL_MP_TERM_ID + ":\"" 
		+ phenotype_id + "\") AND (" + GenotypePhenotypeField.STRAIN_ACCESSION_ID + ":\"MGI:2159965\" OR " + GenotypePhenotypeField.STRAIN_ACCESSION_ID + ":\"MGI:2164831\")")
		.setFilterQueries(GenotypePhenotypeField.RESOURCE_FULLNAME + ":EuroPhenome")
		.setRows(10000);
		q.set("group.field", "" + GenotypePhenotypeField.MARKER_SYMBOL);
		q.set("group", true);
		if (sex != null){
			q.addFilterQuery( GenotypePhenotypeField.SEX + ":" + sex);
		}
		QueryResponse results = solr.query(q);
		return results.getGroupResponse().getValues().get(0).getValues();
	}

	
	public List<String> getGenesAssocByParamAndMp (String parameterStableId, String phenotype_id, String[] resource) throws SolrServerException{
		List<String> res = new ArrayList<String>();
		SolrQuery query = new SolrQuery()
		.setQuery("(" + GenotypePhenotypeField.MP_TERM_ID + ":\"" + phenotype_id + "\" OR " + GenotypePhenotypeField.TOP_LEVEL_MP_TERM_ID + ":\"" + phenotype_id 
				+ "\") AND (" + GenotypePhenotypeField.STRAIN_ACCESSION_ID + ":\"MGI:2159965\" OR " + GenotypePhenotypeField.STRAIN_ACCESSION_ID 
				+ ":\"MGI:2164831\") AND " + GenotypePhenotypeField.PARAMETER_STABLE_ID + ":\"" + parameterStableId+"\"")
		.setRows(10000);
		if (resource != null)
			System.out.println("\t Resource " + resource.length + " - " + StringUtils.join(resource, " OR "));
		if (resource == null || resource.length == 0 || (resource.length == 1 && resource[0].equalsIgnoreCase(""))){
			query.setFilterQueries("(" + GenotypePhenotypeField.RESOURCE_NAME + ":EuroPhenome OR " + GenotypePhenotypeField.RESOURCE_NAME + ":IMPC)");
		}
		else query.setFilterQueries("(" + GenotypePhenotypeField.RESOURCE_NAME + ":" + StringUtils.join(resource, " OR " + GenotypePhenotypeField.RESOURCE_NAME + ":") + ")");
		query.set("group.field", GenotypePhenotypeField.MARKER_ACCESSION_ID );
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
		solrQuery.setQuery( GenotypePhenotypeField.MARKER_ACCESSION_ID + ":*");
		solrQuery.setRows(1000000);
		solrQuery.setFields( GenotypePhenotypeField.MARKER_ACCESSION_ID );
		QueryResponse rsp = null;
		rsp = solr.query(solrQuery);
		SolrDocumentList res = rsp.getResults();
		HashSet<String> allGenes = new HashSet<String>();
		for (SolrDocument doc: res){
			allGenes.add((String) doc.getFieldValue( GenotypePhenotypeField.MARKER_ACCESSION_ID ));
		}
		return allGenes;
	}
	

	public Set<String> getAllPhenotypes() throws SolrServerException{
		
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery( GenotypePhenotypeField.MP_TERM_ID + ":*");
		solrQuery.setRows(1000000);
		solrQuery.setFields( GenotypePhenotypeField.MP_TERM_ID );
		QueryResponse rsp = solr.query(solrQuery);
		SolrDocumentList res = rsp.getResults();
		HashSet<String> allPhenotypes = new HashSet();
		for (SolrDocument doc: res){
                    allPhenotypes.add((String) doc.getFieldValue( GenotypePhenotypeField.MP_TERM_ID ));
		}
		return allPhenotypes;
	}
	

	public Set<String> getAllTopLevelPhenotypes() throws SolrServerException{
		
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery( GenotypePhenotypeField.TOP_LEVEL_MP_TERM_ID + ":*");
		solrQuery.setRows(1000000);
		solrQuery.setFields( GenotypePhenotypeField.TOP_LEVEL_MP_TERM_ID );
		QueryResponse rsp = solr.query(solrQuery);
		SolrDocumentList res = rsp.getResults();
		HashSet<String> allTopLevelPhenotypes = new HashSet();
		for (SolrDocument doc: res){
                    ArrayList<String> ids = (ArrayList<String>)doc.getFieldValue( GenotypePhenotypeField.TOP_LEVEL_MP_TERM_ID );
                    for (String id : ids) {
			allTopLevelPhenotypes.add(id);
                    }
		}
		return allTopLevelPhenotypes;
	}
	
	/*
	 * Methods used by PhenotypeSummaryDAO
	 */
	
	public SolrDocumentList getPhenotypesForTopLevelTerm(String gene, String mpID) throws SolrServerException {	
		SolrDocumentList result = runQuery( GenotypePhenotypeField.MARKER_ACCESSION_ID + ":\"" + gene + "\" AND " + GenotypePhenotypeField.TOP_LEVEL_MP_TERM_ID + ":\"" + mpID + "\"");
		// mpID might be in mp_id instead of top level field
		if (result.size() == 0 || result == null)
		//	result = runQuery("marker_accession_id:" + gene.replace(":", "\\:") + " AND mp_term_id:" + mpID.replace(":", "\\:"));
			result = runQuery(GenotypePhenotypeField.MARKER_ACCESSION_ID + ":\"" + gene + "\" AND " + GenotypePhenotypeField.MP_TERM_ID + ":\"" + mpID + "\"");// AND -" + GenotypePhenotypeField.RESOURCE_NAME + ":IMPC");
		return result;
	}
	
	public SolrDocumentList getPgehnotypes(String gene) throws SolrServerException{
		SolrDocumentList result = runQuery( GenotypePhenotypeField.MARKER_ACCESSION_ID + ":\"" + gene + "\"");
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
		SolrDocumentList result = runQuery( GenotypePhenotypeField.MARKER_ACCESSION_ID + ":\"" + gene + "\"");// AND -" + GenotypePhenotypeField.RESOURCE_NAME + ":IMPC");
		if (result.size() > 0) {
			for (int i = 0; i < result.size(); i++) {
				SolrDocument doc = result.get(i);
				if (doc.getFieldValue( GenotypePhenotypeField.TOP_LEVEL_MP_TERM_ID ) != null){
					ArrayList<String> tlTermIDs = (ArrayList<String>) doc.getFieldValue( GenotypePhenotypeField.TOP_LEVEL_MP_TERM_ID );
					ArrayList<String> tlTermNames = (ArrayList<String>) doc.getFieldValue( GenotypePhenotypeField.TOP_LEVEL_MP_TERM_NAME );
					int len = tlTermIDs.size();
					for (int k = 0 ; k < len ; k++){
						tl.put( tlTermIDs.get(k), tlTermNames.get(k));
					}
	//					tl.put((String) doc.getFieldValue("top_level_mp_term_id"), (String) doc.getFieldValue("top_level_mp_term_name"));
				}
				else { // it seems that when the term id is a top level term itself the top level term field 
					tl.put((String) doc.getFieldValue( GenotypePhenotypeField.MP_TERM_ID ), (String) doc.getFieldValue( GenotypePhenotypeField.MP_TERM_NAME ));					
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
		String solrUrl = solr.getBaseURL()
				+ "/select/?q=" + GenotypePhenotypeField.PARAMETER_STABLE_ID + ":\""
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
			parameter.setName(paramDoc.getString(GenotypePhenotypeField.PARAMETER_NAME));
			//we need to set is derived in the solr core!
			//pipeline core parameter_derived field
			parameter.setStableId(paramDoc.getString("" + GenotypePhenotypeField.PARAMETER_STABLE_ID + ""));
			if (paramDoc.containsKey( GenotypePhenotypeField.PROCEDURE_STABLE_KEY )) {
				parameter.setStableKey(Integer.parseInt(paramDoc
						.getString( GenotypePhenotypeField.PROCEDURE_STABLE_KEY )));
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
	public List<? extends StatisticalResult> getStatsResultFor(String accession, String parameterStableId, ObservationType observationType, String strainAccession, String alleleAccession) throws IOException, URISyntaxException {
		
		String solrUrl = solr.getBaseURL();// "http://wwwdev.ebi.ac.uk/mi/solr/genotype-phenotype";
		solrUrl += "/select/?q=" + GenotypePhenotypeField.MARKER_ACCESSION_ID + ":\""
				+ accession+"\""+"&fq=" + GenotypePhenotypeField.PARAMETER_STABLE_ID + ":"+parameterStableId+"&fq=" + GenotypePhenotypeField.STRAIN_ACCESSION_ID + ":\""+strainAccession+"\""+"&fq="+ GenotypePhenotypeField.ALLELE_ACCESSION_ID + ":\""+alleleAccession+"\"&rows=10000000&version=2.2&start=0&indent=on&wt=json";
		System.out.println("solr url for stats results="+solrUrl);
		List<? extends StatisticalResult> statisticalResult = this.createStatsResultFromSolr(solrUrl, observationType);
		return statisticalResult;
	}

	
	public PhenotypeFacetResult getMPByGeneAccessionAndFilter(
			String accId, String queryString) throws IOException,
			URISyntaxException {

		String solrUrl = solr.getBaseURL()
				+ "/select/?q=" + GenotypePhenotypeField.MARKER_ACCESSION_ID + ":\""
				+ accId+ "\""
//				+ "&fq=-" + GenotypePhenotypeField.RESOURCE_NAME + ":IMPC"
				+ "&rows=10000000&version=2.2&start=0&indent=on&wt=json&facet=true&facet.field=" 
				+ GenotypePhenotypeField.RESOURCE_FULLNAME 
				+ "&facet.field=" + GenotypePhenotypeField.TOP_LEVEL_MP_TERM_NAME + "";
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
				+ "/select/?q=(" + GenotypePhenotypeField.MP_TERM_ID + ":\""
				+ phenotype_id
				+ "\"+OR+" + GenotypePhenotypeField.TOP_LEVEL_MP_TERM_ID + ":\""
				+ phenotype_id + "\")" 
//				+ "&fq=-" + GenotypePhenotypeField.RESOURCE_NAME + ":IMPC" +
				+ "&rows=1000000&version=2.2&start=0&indent=on&wt=json&facet=true&facet.field=" 
				+ GenotypePhenotypeField.RESOURCE_FULLNAME + "&facet.field=" + GenotypePhenotypeField.PROCEDURE_NAME+ "&facet.field=" 
				+ GenotypePhenotypeField.MARKER_SYMBOL + "&facet.field="
				+ GenotypePhenotypeField.MP_TERM_NAME + "";
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
		for (Object doc : docs) {
			UnidimensionalResult unidimensionalResult=new UnidimensionalResult();
			JSONObject phen = (JSONObject) doc;
			String pValue = phen.getString( GenotypePhenotypeField.P_VALUE );
			String sex = phen.getString( GenotypePhenotypeField.SEX );
			String zygosity=phen.getString( GenotypePhenotypeField.ZYGOSITY );
			String effectSize=phen.getString(GenotypePhenotypeField.EFFECT_SIZE);
			String phenoCallSummaryId=phen.getString(GenotypePhenotypeField.PCS_ID);
			
			
			//System.out.println("pValue="+pValue);
			if(pValue!=null) {
				unidimensionalResult.setId(Integer.parseInt(phenoCallSummaryId));//one id for each document and for each sex
				unidimensionalResult.setpValue(Double.valueOf(pValue));
				unidimensionalResult.setZygosityType(ZygosityType.valueOf(zygosity));
				unidimensionalResult.setEffectSize(new Double(effectSize));
				unidimensionalResult.setSexType(SexType.valueOf(sex));
			}
			results.add(unidimensionalResult);
		}
		return results;
	}

		
		if(observationType==ObservationType.categorical) {
			
			for (Object doc : docs) {
                            CategoricalResult catResult=new CategoricalResult();
				JSONObject phen = (JSONObject) doc;
				//System.out.println("pValue="+pValue);
				String pValue = phen.getString( GenotypePhenotypeField.P_VALUE );
				String sex = phen.getString( GenotypePhenotypeField.SEX );
				String zygosity=phen.getString( GenotypePhenotypeField.ZYGOSITY );
				String effectSize=phen.getString(GenotypePhenotypeField.EFFECT_SIZE);
				String phenoCallSummaryId=phen.getString(GenotypePhenotypeField.PCS_ID);
				
				//System.out.println("pValue="+pValue);
				//if(pValue!=null) {
                                    catResult.setId(Integer.parseInt(phenoCallSummaryId));//one id for each document and for each sex
					catResult.setpValue(Double.valueOf(pValue));
					catResult.setZygosityType(ZygosityType.valueOf(zygosity));
					catResult.setEffectSize(new Double(Double.valueOf(effectSize)));
					catResult.setSexType(SexType.valueOf(sex)); 
                                       // System.out.println("adding sex="+SexType.valueOf(sex));
				//}
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
			String mpTerm = phen.getString( GenotypePhenotypeField.MP_TERM_NAME );
			String mpId = phen.getString( GenotypePhenotypeField.MP_TERM_ID );
			PhenotypeCallSummary sum = new PhenotypeCallSummary();
			OntologyTerm phenotypeTerm = new OntologyTerm();
			phenotypeTerm.setName(mpTerm);
			phenotypeTerm.setDescription(mpTerm);
			DatasourceEntityId mpEntity = new DatasourceEntityId();
			mpEntity.setAccession(mpId);
			phenotypeTerm.setId(mpEntity);
			sum.setPhenotypeTerm(phenotypeTerm);
			sum.setPhenotypeingCenter(phen.getString( GenotypePhenotypeField.PHENOTYPING_CENTER ));
			if (phen.containsKey( GenotypePhenotypeField.ALLELE_SYMBOL )) {
				Allele allele = new Allele();
				allele.setSymbol(phen.getString(GenotypePhenotypeField.ALLELE_SYMBOL ));
				GenomicFeature alleleGene = new GenomicFeature();
				DatasourceEntityId alleleEntity = new DatasourceEntityId();
				alleleEntity
						.setAccession(phen.getString( GenotypePhenotypeField.ALLELE_ACCESSION_ID ));
				allele.setId(alleleEntity);
				alleleGene.setId(alleleEntity);
				alleleGene.setSymbol(phen.getString(GenotypePhenotypeField.MARKER_SYMBOL));
				allele.setGene(alleleGene);
				sum.setAllele(allele);
			}
			if (phen.containsKey(GenotypePhenotypeField.MARKER_SYMBOL)) {
				GenomicFeature gf = new GenomicFeature();
				gf.setSymbol(phen.getString( GenotypePhenotypeField.MARKER_SYMBOL ));
				DatasourceEntityId geneEntity = new DatasourceEntityId();
				geneEntity.setAccession(phen.getString( GenotypePhenotypeField.MARKER_ACCESSION_ID ));
				gf.setId(geneEntity);
				sum.setGene(gf);
			}
			if (phen.containsKey(GenotypePhenotypeField.PHENOTYPING_CENTER )){
				sum.setPhenotypeingCenter(phen.getString( GenotypePhenotypeField.PHENOTYPING_CENTER ));
			}
			// GenomicFeature gene=new GenomicFeature();
			// gene.
			// allele.setGene(gene);
			String zygosity = phen.getString( GenotypePhenotypeField.ZYGOSITY );
			ZygosityType zyg = ZygosityType.valueOf(zygosity);
			sum.setZygosity(zyg);
			String sex = phen.getString( GenotypePhenotypeField.SEX );
			SexType sexType = SexType.valueOf(sex);
			sum.setSex(sexType);
			String provider = phen.getString(GenotypePhenotypeField.RESOURCE_NAME);
			Datasource datasource = new Datasource();
			datasource.setName(provider);
			sum.setDatasource(datasource);

			// "parameter_stable_id":"557",
			// "parameter_name":"Bone Mineral Content",
			// "procedure_stable_id":"41",
			// "procedure_stable_key":"41",
			Parameter parameter = new Parameter();
			if (phen.containsKey( GenotypePhenotypeField.PARAMETER_STABLE_ID )) {
				parameter = pipelineDAO.getParameterByStableId(phen.getString( GenotypePhenotypeField.PARAMETER_STABLE_ID ));
			} else {
				System.err.println("parameter_stable_id missing");
			}
			sum.setParameter(parameter);
			
			Pipeline pipeline=new Pipeline(); 
			if (phen.containsKey( GenotypePhenotypeField.PARAMETER_STABLE_ID )) {
				pipeline = pipelineDAO.getPhenotypePipelineByStableId(phen.getString(GenotypePhenotypeField.PIPELINE_STABLE_ID));
			} else {
				System.err.println("pipeline stable_id missing");
			}
			sum.setPipeline(pipeline);
			
			Project project = new Project();
			project.setName(phen.getString( GenotypePhenotypeField.PROJECT_NAME ));
			project.setDescription(phen.getString( GenotypePhenotypeField.PROJECT_FULLNAME )); // is this right for description? no other field in solr index!!!
			if (phen.containsKey( GenotypePhenotypeField.PROJECT_EXTERNAL_ID )) {
				sum.setExternalId(phen.getInt( GenotypePhenotypeField.PROJECT_EXTERNAL_ID ));
			}
			sum.setProject(project);
			// "procedure_stable_id":"77",
			// "procedure_stable_key":"77",
			// "procedure_name":"Plasma Chemistry",
			Procedure procedure = new Procedure();
			if (phen.containsKey(GenotypePhenotypeField.PROCEDURE_STABLE_ID)) {
				procedure.setStableId(phen.getString( GenotypePhenotypeField.PROCEDURE_STABLE_ID ));
				procedure.setStableKey(Integer.valueOf(phen
						.getString( GenotypePhenotypeField.PROCEDURE_STABLE_KEY )));
				procedure.setName(phen.getString( GenotypePhenotypeField.PROCEDURE_NAME));
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
//		System.out.println("\n\nFacet count : " + results.getJSONObject("facet_counts")
	//			.getJSONObject("facet_fields") );
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
