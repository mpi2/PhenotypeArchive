/*******************************************************************************
 * Copyright 2015 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 *******************************************************************************/
package uk.ac.ebi.generic.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import net.sf.json.JSONObject;

/**
 * class for getting appropriate image info from the solr image rest service
 * should use JSONRestService for generic rest/json work.
 * @author jwarren
 *
 */
public class JSONImageUtils {

//	public static  JSONObject getAnatomyAssociatedImagesWithMP(String anatomy_id, String mp, Map<String, String> config) throws IOException, URISyntaxException {
//		//http://wwwdev.ebi.ac.uk/mi/impc/dev/solr/images/select/?q=annotationTermId:%22MA:0000072%22%20AND%20mpTermId:*&wt=json&start=0&rows=19&defType=edismax&indent=on
//		String url=config.get("internalSolrUrl")+"/images/select/?q=annotationTermId:\""+anatomy_id+"\" AND mpTermId:\""+mp+"\" AND -expName:\"Wholemount Expression\"&wt=json&start=0&rows=6&defType=edismax";
//		JSONObject result = JSONRestUtil.getResults(url);
//		System.out.println(result.toString());
//		return result;
//	}
//	
//	public static  JSONObject getAnatomyAssociatedImagesNotExpression(String anatomy_id, Map<String, String> config) throws IOException, URISyntaxException {
//		//url += "&facet=on&facet.field=symbol_gene&facet.field=expName_exp&facet.field=maTermName&facet.field=mpTermName&facet.mincount=1&facet.limit=-1";
//		String url=config.get("internalSolrUrl")+"/images/select/?q=annotationTermId:\""+anatomy_id+"\" AND -expName:\"Wholemount%20Expression\"&wt=json&start=0&rows=6&defType=edismax";
//		JSONObject result = JSONRestUtil.getResults(url);
//		System.out.println(result.toString());
//		return result;
//	}
	
	public static  JSONObject getAnatomyAssociatedExpressionImages(String anatomy_id, Map<String, String> config, int numberOfImagesToDisplay) throws IOException, URISyntaxException {
		//url += "&facet=on&facet.field=symbol_gene&facet.field=expName_exp&facet.field=maTermName&facet.field=mpTermName&facet.mincount=1&facet.limit=-1";
		String url=config.get("internalSolrUrl")+"/images/select/?q=annotationTermId:"+anatomy_id+"&wt=json&start=0&rows="+numberOfImagesToDisplay+"&facet=on&fq=expName:\"Wholemount Expression\"&defType=edismax";
		JSONObject result = JSONRestUtil.getResults(url);
		return result;
	}
}
