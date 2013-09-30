package uk.ac.ebi.phenotype.stats;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.ac.ebi.generic.util.JSONRestUtil;
import uk.ac.ebi.phenotype.pojo.ObservationType;
import uk.ac.ebi.phenotype.pojo.SexType;
import uk.ac.ebi.phenotype.pojo.ZygosityType;

public class JSONGraphUtilsTest {
	String parameterString = "ESLIM_007_001_006";
	String accessionString = "MGI:1931053";
	private String organisation = "MRC Harwell";

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testgetExperimentalData() {
		Map<String, String> config = new HashMap<String, String>();
		config.put("internalSolrUrl",
				"http://wwwdev.ebi.ac.uk/mi/impc/dev/solr");

		JSONObject expResult = null;
		try {
			expResult = JSONGraphUtils.getExperimentalData(parameterString,
					accessionString, config);
		} catch (IOException | URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// facetCounts.getJSONArray(key)
		System.out.println("orgs with data=" + expResult);
	}

	@Test
	public void testgetControlData() {
		Map<String, String> config = new HashMap<String, String>();
		config.put("internalSolrUrl",
				"http://wwwdev.ebi.ac.uk/mi/impc/dev/solr");
		String strain = "MGI:3028467";
		try {
			JSONObject object = JSONGraphUtils.getControlData(organisation,
					strain, parameterString, config);
			
			net.sf.json.JSONObject facetCounts = object
					.getJSONObject("facet_counts");
			net.sf.json.JSONObject facetFields = facetCounts
					.getJSONObject("facet_fields");
			System.out.println("facetFields=" + facetFields);
			
			JSONArray facets = facetFields.getJSONArray("biologicalModelId");
			ArrayList<String> biologicalModelIds = new ArrayList<>();
			for (int i = 0; i < facets.size(); i += 2) {
				String facet = facets.getString(i);
				int count = facets.getInt(i + 1);
				if (count > 0) {
					biologicalModelIds.add(facet);
				}
			}

			System.out.println("Control Biological models=" + biologicalModelIds);
			
		} catch (IOException | URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// facetCounts.getJSONArray(key)

	}

	@Test
	public void testProcessResults() {
		Map<String, String> config = new HashMap<String, String>();
		config.put("internalSolrUrl",
				"http://wwwdev.ebi.ac.uk/mi/impc/dev/solr");

		JSONObject expResult = null;
		try {
			expResult = JSONGraphUtils.getExperimentalData(parameterString,
					accessionString, config);
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}

		net.sf.json.JSONObject facetCounts = expResult
				.getJSONObject("facet_counts");
		net.sf.json.JSONObject facetFields = facetCounts
				.getJSONObject("facet_fields");
		System.out.println("facetFields=" + facetFields);
		
		JSONArray facets = facetFields.getJSONArray("organisation");
		ArrayList<String> organisationsWithData = new ArrayList<>();
		for (int i = 0; i < facets.size(); i += 2) {
			String facet = facets.getString(i);
			int count = facets.getInt(i + 1);
			if (count > 0) {
				organisationsWithData.add(facet);
			}
		}

		System.out.println("organisations with data=" + organisationsWithData);

		JSONArray facets2 = facetFields.getJSONArray("strain");
		// get the strains from the facets
		ArrayList<String> strains = new ArrayList<>();
		for (int i = 0; i < facets2.size(); i += 2) {
			String facet = facets2.getString(i);
			int count = facets2.getInt(i + 1);
			if (count > 0) {
				strains.add(facet);
			}
		}
		System.out.println("strains=" + strains);
		
		net.sf.json.JSONArray facets3 = facetFields.getJSONArray("biologicalModelId");
		// get the strains from the facets
		ArrayList<String> biologicalModelIds = new ArrayList<>();
		for (int i = 0; i < facets3.size(); i += 2) {
			String facet = facets3.getString(i);
			int count = facets3.getInt(i + 1);
			if (count > 0) {
				biologicalModelIds.add(facet);
			}
		}
		System.out.println("biologicalModelIds=" + biologicalModelIds);
		
		net.sf.json.JSONArray facets4 = facetFields.getJSONArray("gender");
		// get the strains from the facets
		ArrayList<String> genders = new ArrayList<>();
		for (int i = 0; i < facets4.size(); i += 2) {
			String facet = facets4.getString(i);
			int count = facets4.getInt(i + 1);
			if (count > 0) {
				genders.add(facet);
			}
		}
		System.out.println("genders=" + genders);
		net.sf.json.JSONArray facets5 = facetFields.getJSONArray("zygosity");
		// get the strains from the facets
		ArrayList<ZygosityType> zygosities = new ArrayList<ZygosityType>();
		for (int i = 0; i < facets5.size(); i += 2) {
			String facet = facets5.getString(i);
			int count = facets5.getInt(i + 1);
			if (count > 0) {
				ZygosityType zygosityType=ZygosityType.valueOf(facet);
				zygosities.add(zygosityType);
			}
		}
		System.out.println("zygosities=" + zygosities);
		

		ObservationType observationTypeForParam = null;
		JSONArray resultsArray = JSONRestUtil.getDocArray(expResult);
		if (resultsArray.size() > 0) {
			// only need to get first as parameter is same for all
			net.sf.json.JSONObject exp = resultsArray.getJSONObject(1);
			observationTypeForParam = ObservationType.valueOf(exp
					.getString("observationType"));
			System.out.println("observationTypeForParam="
					+ observationTypeForParam);
		}
		
		net.sf.json.JSONArray docs = JSONRestUtil.getDocArray(expResult);
		for(int i=0; i<docs.size();i++) {
			net.sf.json.JSONObject doc = docs.getJSONObject(i);
			SexType sexType=SexType.valueOf(doc.getString("gender"));
			System.out.println("sexType="+sexType);
			
			
		}

	}

	// get controls
	// http://wwwdev.ebi.ac.uk/mi/impc/dev/solr/experiment/select?q=parameterStableId:ESLIM_007_001_006%20AND%20%20strain:%22MGI:3028467%22%20AND%20organisation:%22MRC%20Harwell%22&wt=json&indent=true&rows=100000&facet=on&facet.field=organisation&facet.field=strain

}
