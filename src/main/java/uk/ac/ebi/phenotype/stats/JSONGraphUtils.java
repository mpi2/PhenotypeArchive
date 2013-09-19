package uk.ac.ebi.phenotype.stats;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.ac.ebi.generic.util.JSONRestUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class JSONGraphUtils {

	/**
	 * just return a list of organisations that have experimental data for this param and accession
	 * @param parameterId
	 * @param accession
	 * @param config
	 * @return
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public static net.sf.json.JSONObject getExperimentalData(String parameterId, String accession, Map<String, String> config) throws IOException, URISyntaxException{
	 String url=config.get("internalSolrUrl")+"/experiment/select?q=parameterStableId:"+parameterId+" AND (geneAccession:\""+accession+"\" AND biologicalSampleGroup:experimental)&wt=json&indent=true&rows=1000000&facet=on&facet.field=organisation&facet.field=strain&facet.field=biologicalModelId&facet.field=gender&facet.field=zygosity&facet.field=category";
	System.out.println("experimental data url="+url);	
	 net.sf.json.JSONObject result = JSONRestUtil.getResults(url);
	 System.out.println("got json result");
		return result;
	}
	
//	public static JSONObject getDataExperimentalDataForOrganisationAndStrain(String organisation, String strain,  String parameterId, String accession, Map<String, String> config) throws IOException, URISyntaxException {
//		String url=config.get("internalSolrUrl")+"/experiment/select?q=parameterStableId:"+parameterId+" AND (strain:\""+accession+"\" AND organisation:\""+organisation+"\")&wt=json&indent=true&rows=10000&facet=on&facet.field=strain";
//		net.sf.json.JSONObject result = JSONRestUtil.getResults(url);
//		net.sf.json.JSONObject facetCounts = result.getJSONObject("facet_counts");
//		net.sf.json.JSONObject facetFields = facetCounts.getJSONObject("facet_fields");
//		System.out.println("facetFields in getDataForOrganisations="+facetFields);
//		
//		Map<String, Map<String, JSONObject>> docsForStrains = new HashMap<String, Map<String, JSONObject>>();
//		JSONArray resultArray=JSONRestUtil.getDocArray(result);
//		for (int i = 0; i < resultArray.size(); i++) {
//			JSONObject doc = resultArray.getJSONObject(i);
//			System.out.println("doc="+doc.getString("biologicalSampleGroup"));
//			
//		}
//		
//		return result;
//	}
	
	/**
	 * Get the control biological models as a facet and data points
	 * @param organisation
	 * @param strain
	 * @param parameterId
	 * @param config
	 * @return
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public static JSONObject getControlData(String organisation, String strain,  String parameterId, Map<String, String> config) throws IOException, URISyntaxException {
		String url=config.get("internalSolrUrl")+"/experiment/select?q=parameterStableId:"+parameterId+" AND strain:\""+strain+"\" AND organisation:\""+organisation+"\" AND biologicalSampleGroup:control&wt=json&indent=true&rows=10000&facet=on&facet.field=strain&facet.field=biologicalModelId";
		System.out.println("ControlData url="+url);
		net.sf.json.JSONObject result = JSONRestUtil.getResults(url);
		System.out.println("got control json result");
//		net.sf.json.JSONObject facetCounts = result.getJSONObject("facet_counts");
//		net.sf.json.JSONObject facetFields = facetCounts.getJSONObject("facet_fields");
		//System.out.println("facetFields in getDataForOrganisations="+facetFields);
		
//		Map<String, Map<String, JSONObject>> docsForStrains = new HashMap<String, Map<String, JSONObject>>();
//		JSONArray resultArray=JSONRestUtil.getDocArray(result);
//		for (int i = 0; i < resultArray.size(); i++) {
//			JSONObject doc = resultArray.getJSONObject(i);
//			//System.out.println("doc="+doc.getString("biologicalSampleGroup"));	
//		}
		return result;
	}
}
