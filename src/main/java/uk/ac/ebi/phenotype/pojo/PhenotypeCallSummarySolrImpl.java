package uk.ac.ebi.phenotype.pojo;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import uk.ac.ebi.generic.util.JSONRestUtil;
import uk.ac.ebi.phenotype.util.PhenotypeCallSummaryDAOReadOnly;
import uk.ac.ebi.phenotype.util.PhenotypeFacetResult;

public class PhenotypeCallSummarySolrImpl implements
		PhenotypeCallSummaryDAOReadOnly {

	
	private static final Logger log = Logger
			.getLogger(PhenotypeCallSummarySolrImpl.class);
	
	@Resource(name = "globalConfiguration")
	private Map<String, String> config;
	// TODO change this to come from the configuration
	private final String core = "genotype-phenotype";

	@Override
	public PhenotypeFacetResult getPhenotypeCallByGeneAccession(String accId)
			throws IOException, URISyntaxException {
		return this.getPhenotypeCallByGeneAccessionAndFilter(accId, "");
	}

	@Override
	public PhenotypeFacetResult getPhenotypeCallByGeneAccessionAndFilter(
			String accId, String queryString) throws IOException,
			URISyntaxException {

		String solrUrl = config.get("internalSolrUrl");// "http://wwwdev.ebi.ac.uk/mi/solr/genotype-phenotype";
		String url = solrUrl
				+ "/"
				+ core
				+ "/select/?q=marker_accession_id:\""
				+ accId
				+ "\"&fq=-resource_name:IMPC&rows=10000000&version=2.2&start=0&indent=on&wt=json&facet=true&facet.field=resource_fullname&facet.field=top_level_mp_term_name";
		if (queryString.startsWith("&")) {
			url += queryString;
		} else {// add an ampersand parameter splitter if not one as we need
				// one to add to the already present solr query string
			url += "&" + queryString;
		}
		return createPhenotypeResultFromSolrResponse(url);
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
				parameter=this.getParameterByStableId(phen.getString("parameter_stable_id"));
				System.out.println("parameter is derived="+parameter.getDerivedFlag());
//				parameter.setStableId();
//				parameter.setName(phen.getString("parameter_name"));
				//we need to set is derived in the solr core!
				//pipeline core parameter_derived field
				
//				if (phen.containsKey("procedure_stable_key")) {
//					parameter.setStableKey(Integer.parseInt(phen
//							.getString("procedure_stable_key")));
//				}
			} else {
				System.err.println("parameter_stable_id missing");
			}

			sum.setParameter(parameter);
			Project project = new Project();
			project.setName(phen.getString("project_name"));
			project.setDescription(phen.getString("project_fullname"));// is
																		// this
																		// right
																		// for
																		// description?
																		// no
																		// other
																		// field
																		// in
																		// solr
																		// index!!!
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

	@Override
	public PhenotypeFacetResult getPhenotypeCallByMPAccession(
			String phenotype_id) throws IOException, URISyntaxException {
		return this.getPhenotypeCallByMPAccessionAndFilter(phenotype_id, "");

	}

	@Override
	public PhenotypeFacetResult getPhenotypeCallByMPAccessionAndFilter(
			String phenotype_id, String queryString) throws IOException,
			URISyntaxException {
		// http://wwwdev.ebi.ac.uk/mi/solr/genotype-phenotype/select/?q=mp_term_id:MP:0010025&rows=100&version=2.2&start=0&indent=on&defType=edismax&wt=json&facet=true&facet.field=resource_fullname&facet.field=top_level_mp_term_name&
		String solrUrl = config.get("internalSolrUrl");// "http://wwwdev.ebi.ac.uk/mi/solr/genotype-phenotype";
		String url = solrUrl
				+ "/"
				+ core
				+ "/select/?q=(mp_term_id:\""
				+ phenotype_id
				+ "\"+OR+top_level_mp_term_id:\""
				+ phenotype_id
				+ "\")&fq=-resource_name:IMPC&rows=1000000&version=2.2&start=0&indent=on&wt=json&facet=true&facet.field=resource_fullname&facet.field=procedure_name&facet.field=marker_symbol";
		// if (!filterString.equals("")) {
		if (queryString.startsWith("&")) {
			url += queryString;
		} else {// add an ampersand parameter splitter if not one as we need
				// one to add to the already present solr query string
			url += "&" + queryString;
		}
		// }

		return createPhenotypeResultFromSolrResponse(url);

	}
	
	public List<? extends StatisticalResult> getStatisticalResultFor(String accession, String parameterStableId, ObservationType observationType, String strainAccession) throws IOException, URISyntaxException {
		
		String solrUrl = config.get("internalSolrUrl");// "http://wwwdev.ebi.ac.uk/mi/solr/genotype-phenotype";
		String url = solrUrl
				+ "/"
				+ core
				+ "/select/?q=marker_accession_id:\""
				+ accession+"\""+"&fq=parameter_stable_id:"+parameterStableId+"&fq=strain_accession_id:\""+strainAccession+"\"&rows=10000000&version=2.2&start=0&indent=on&wt=json";
		List<? extends StatisticalResult> statisticalResult=this.createStatsResultFromSolr(url, observationType);
		return statisticalResult;
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

	
	
	public Parameter getParameterByStableId(String parameterStableId) throws IOException, URISyntaxException {
			PipelineSolrImpl pipelineSolrImpl=new PipelineSolrImpl(config);
			Parameter parameter=pipelineSolrImpl.getParameterByStableId(parameterStableId, "");
			return parameter;
	}
	
	
}
