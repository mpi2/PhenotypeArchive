package uk.ac.ebi.generic.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
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
//		<doc>
//		<str name="ma_id">MA:0000072</str>
//		<str name="ma_term">heart</str>
//		<arr name="top_level_ma_id">
//		<str>MA:0000003</str>
//		<str>MA:0002405</str>
//		<str>MA:0002405</str>
//		<str>MA:0002405</str>
//		<str>MA:0002405</str>
//		<str>MA:0002433</str>
//		<str>MA:0002433</str>
//		<str>MA:0002433</str>
//		</arr>
//		<arr name="top_level_ma_term">
//		<str>organ system</str>
//		<str>postnatal mouse</str>
//		<str>postnatal mouse</str>
//		<str>postnatal mouse</str>
//		<str>postnatal mouse</str>
//		<str>anatomic region</str>
//		<str>anatomic region</str>
//		<str>anatomic region</str>
//		</arr>
//		</doc>
		JSONObject maJson=maArray.getJSONObject(0);
		String maIdString=maJson.getString("ma_id");
		String maTerm=maJson.getString("ma_term");
		if(!maIdString.equals(anatomy_id)) {
			System.err.println("something odd in that the maId doesn't equal the anatomy_id!");
		}
		Anatomy ma = new Anatomy(maIdString,maTerm) ;
		if(maJson.containsKey("top_level_ma_term")) {
			ma.setTopLevelTerms(JSONArray.toCollection(maJson.getJSONArray("top_level_ma_term"), String.class));
		}
		if(maJson.containsKey("top_level_ma_id")) {
			ma.setTopLevelIds(JSONArray.toCollection(maJson.getJSONArray("top_level_ma_id")));
		}
		return ma;
	}
	
}
