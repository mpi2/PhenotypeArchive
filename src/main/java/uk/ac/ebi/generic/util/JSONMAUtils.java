package uk.ac.ebi.generic.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import uk.ac.ebi.phenotype.web.pojo.Anatomy;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class JSONMAUtils {

	public static  Anatomy getMA(String anatomy_id, Map<String, String> config) throws IOException, URISyntaxException {
		//url += "&facet=on&facet.field=symbol_gene&facet.field=expName_exp&facet.field=maTermName&facet.field=mpTermName&facet.mincount=1&facet.limit=-1";
		String url=config.get("internalSolrUrl")+"/ma/select/?q=ma_id:\""+anatomy_id+"\"&wt=json&start=0&rows=6";
		System.out.println("anatomy url="+url);
		JSONObject result = JSONRestUtil.getResults(url);
		//System.out.println(result.toString());
		JSONArray maArray=JSONRestUtil.getDocArray(result);
		JSONObject maJson=maArray.getJSONObject(0);
		String maIdString=maJson.getString("ma_id");
		String maTerm=maJson.getString("ma_term");
		if(!maIdString.equals(anatomy_id)) {
			System.err.println("something odd in that the maId doesn't equal the anatomy_id!");
		}
		Anatomy ma = new Anatomy(maIdString,maTerm) ;
	
		if(maJson.containsKey("child_ma_idTerm")) {
			Collection<String> idTerms=JSONArray.toCollection(maJson.getJSONArray("child_ma_idTerm") , String.class);
			idTerms=getDistinct(idTerms);
			List<String> ids=new ArrayList<>();
			List<String> terms=new ArrayList<>();
			for(String idTerm: idTerms) {
				if(idTerm.contains("__")) {
				String[] idToTerm = idTerm.split("__");
				ids.add(idToTerm[0]);
				terms.add(idToTerm[1]);
				}else {
					System.err.println("ma id to term expecting a double underscore but non present - please check what has changed in the solr ma core!!!!!!");
				}
			}
			ma.setChildTerms(terms);
			ma.setChildIds(ids);
		}
		//the fields below don't exist anymore in the schema - do we replace these with something or not??? JW
//		if(maJson.containsKey("ma_2_mp_id")) {
//			Collection<String> mpIds=JSONArray.toCollection(maJson.getJSONArray("ma_2_mp_id") , String.class);
//			mpIds=getDistinct(mpIds);
//			ma.setMpIds(mpIds);
//		}
//		if(maJson.containsKey("ma_2_mp_name")) {
//			Collection<String> mpIds=JSONArray.toCollection(maJson.getJSONArray("ma_2_mp_name") , String.class);
//			mpIds=getDistinct(mpIds);
//			ma.setMpTerms(mpIds);
//		}
		return ma;
	}

	/**
	 * returns a Collection of Strings with multiples/duplicates removed
	 * @param childTermStrings
	 * @return
	 */
	private static Collection<String> getDistinct(Collection<String> childTermStrings) {
		HashSet<String> hs = new HashSet<String>();
		hs.addAll(childTermStrings);
		childTermStrings.clear();
		childTermStrings.addAll(hs);
		return childTermStrings;
	}
	
}
