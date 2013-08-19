package uk.ac.ebi.phenotype.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.ac.ebi.phenotype.pojo.PhenotypeCallSummary;

public class PhenotypeFacetResult {
	List<PhenotypeCallSummary> phenotypeCallSummaries=new ArrayList<PhenotypeCallSummary>();
	public List<PhenotypeCallSummary> getPhenotypeCallSummaries() {
		return phenotypeCallSummaries;
	}
	public void setPhenotypeCallSummaries(
			List<PhenotypeCallSummary> phenotypeCallSummaries) {
		this.phenotypeCallSummaries = phenotypeCallSummaries;
	}
	Map<String, Map<String, Integer>> facetResults = new HashMap<String, Map<String, Integer>>();
	public Map<String, Map<String, Integer>> getFacetResults() {
		return facetResults;
	}
	public void setFacetResults(Map<String, Map<String, Integer>> facetResults) {
		this.facetResults = facetResults;
	}
}
