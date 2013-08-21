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
		JSONObject result = JSONRestUtil.getResults(url);
		System.out.println(result.toString());
		JSONArray maArray=JSONRestUtil.getDocArray(result);
		JSONObject maJson=maArray.getJSONObject(0);
		String maIdString=maJson.getString("ma_id");
		String maTerm=maJson.getString("ma_term");
		if(!maIdString.equals(anatomy_id)) {
			System.err.println("something odd in that the maId doesn't equal the anatomy_id!");
		}
		Anatomy ma = new Anatomy(maIdString,maTerm) ;
		if(maJson.containsKey("child_ma_term")) {
			//we need to remove duplicates
			Collection<String> childTermStrings=JSONArray.toCollection(maJson.getJSONArray("child_ma_term") , String.class);
			childTermStrings=getDistinct(childTermStrings);
			ma.setChildTerms(childTermStrings);
		}
		if(maJson.containsKey("child_ma_id")) {
			Collection<String> childIdStrings=JSONArray.toCollection(maJson.getJSONArray("child_ma_id") , String.class);
			childIdStrings=getDistinct(childIdStrings);
			ma.setChildIds(childIdStrings);
		}
		if(maJson.containsKey("ma_2_mp_mapping")) {
			Collection<String> mpTerms=JSONArray.toCollection(maJson.getJSONArray("ma_2_mp_mapping") , String.class);
			mpTerms=getDistinct(mpTerms);
			ma.setMpTerms(mpTerms);
		}
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
