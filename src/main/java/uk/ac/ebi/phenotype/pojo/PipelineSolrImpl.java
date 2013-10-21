package uk.ac.ebi.phenotype.pojo;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import uk.ac.ebi.generic.util.JSONRestUtil;

public class PipelineSolrImpl {
	Map<String, String> config;
	
	public PipelineSolrImpl( Map<String, String> config) {
		this.config=config;
	}
	
	public Parameter getParameterByStableId(
			String paramStableId, String queryString) throws IOException,
			URISyntaxException {
		String pipelineCoreString="pipeline";
		String solrUrl = config.get("internalSolrUrl");// "http://wwwdev.ebi.ac.uk/mi/solr/genotype-phenotype";
		String url = solrUrl
				+ "/"
				+ pipelineCoreString
				+ "/select/?q=parameter_stable_id:\""
				+ paramStableId
				+ "\"&rows=10000000&version=2.2&start=0&indent=on&wt=json";
		if (queryString.startsWith("&")) {
			url += queryString;
		} else {// add an ampersand parameter splitter if not one as we need
				// one to add to the already present solr query string
			url += "&" + queryString;
		}
		return createParameter(url);
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
}
