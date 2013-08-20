package uk.ac.ebi.phenotype.stats;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.ac.ebi.generic.util.SolrIndex;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class PreliminaryPhenotypeCallSummaryTableCreator {

	public void getPreliminaryCallSummaries(SolrIndex solrIndex) {

		// url
		// http://wwwdev.ebi.ac.uk/mi/solr/gene/select/?q=*%3A*&version=2.2&start=0&rows=100&indent=on
		// gets first 100 only!!!
		// find all with status of 1 <arr name="imits_phenotype_started">
		// <str>1</str>
		// </arr>
		try {
			List<Map<String, String>> results = solrIndex
					.getGenesWithPhenotypeStartedFromAll();

			for (Map<String, String> map : results) {
				// https://www.mousephenotype.org/heatmap/rest/ontological/heatmap?mgiid=MGI:1921677
				String mgi = map.get("mgi");
				String url = "https://www.mousephenotype.org/heatmap/rest/ontological/heatmap?mgiid="
						+ mgi;
				List<Map<String, Double>> mpToSignificanceAll=new ArrayList<Map<String, Double>>();//need a new one to hold all otherwise get concurrant modification exception
				List<Map<String, Double>> mpToSignificance = getPhenotypeInfo(
						solrIndex, url);
				mpToSignificanceAll.addAll(mpToSignificance);
				// pigmentation is first in list of higher level ontology terms
				// can drill down using the mp term added to the previous url
				// https://dev.mousephenotype.org/heatmap/rest/ontological/heatmap?mgiid=MGI:1921677&type=MP:0001186
				// so loop through the returned MP terms and go down a level and
				// see if we have more data
				for (Map<String, Double> mpToSig : mpToSignificance) {
				for(String key: mpToSig.keySet()){
						String lowerLevelUrl2 = "https://www.mousephenotype.org/heatmap/rest/ontological/heatmap?mgiid="
								+ mgi + "&type=" + key;
						List<Map<String, Double>> mpToSignificanceLevel2 = getPhenotypeInfo(
								solrIndex, lowerLevelUrl2);
						//assuming MP terms are only mentioned once in a heirachy - reasonable I think so add maps together higher and lower level
						mpToSignificanceAll.addAll(mpToSignificanceLevel2);
					}

				}

			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * @param solrIndex
	 * @param url
	 * @return
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	private List<Map<String, Double>> getPhenotypeInfo(SolrIndex solrIndex,
			String url) throws IOException, URISyntaxException {
		List<Map<String, Double>> listOfMpIdsWithPValue = new ArrayList<Map<String, Double>>();
		JSONObject overview = solrIndex.getResults(url);
		JSONArray pValuesArray = overview.getJSONObject("heatmap")
				.getJSONArray("significance");
		JSONArray pValuesForMP = pValuesArray.getJSONArray(0);
		// get the matching list of MP terms with these significance values
		JSONArray rowHeaders = overview.getJSONObject("heatmap").getJSONArray(
				"row_headers");

		if (rowHeaders.size() == pValuesArray.size()) {
			int i = 0;
			for (Object obj : rowHeaders) {
				JSONObject objectForMP = (JSONObject) obj;
				String mpId = objectForMP.getString("k");
				Map<String, Double> idToPValue = new HashMap<String, Double>();
				System.out.println("adding to map " + mpId + " pValue="
						+ pValuesArray.getJSONArray(i).getDouble(0));
				idToPValue.put(mpId, pValuesArray.getJSONArray(i).getDouble(0));
				listOfMpIdsWithPValue.add(idToPValue);
				i++;
			}
		} else {
			System.err
					.println("Number of MP terms does not match the number of pValues from the harwell restful interface");
		}
		return listOfMpIdsWithPValue;
	}

}
