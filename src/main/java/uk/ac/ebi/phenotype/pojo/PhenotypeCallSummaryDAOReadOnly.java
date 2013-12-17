package uk.ac.ebi.phenotype.pojo;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import uk.ac.ebi.phenotype.util.PhenotypeFacetResult;



public interface PhenotypeCallSummaryDAOReadOnly {

	public PhenotypeFacetResult getPhenotypeCallByGeneAccession(String accId) throws IOException, URISyntaxException;
	
	public PhenotypeFacetResult getPhenotypeCallByGeneAccessionAndFilter(String accId, String filterString) throws IOException, URISyntaxException;

	public PhenotypeFacetResult getPhenotypeCallByMPAccession(
			String phenotype_id) throws IOException, URISyntaxException;
	
	public PhenotypeFacetResult getPhenotypeCallByMPAccessionAndFilter(
			String phenotype_id, String filter) throws IOException, URISyntaxException;
	
	public List<? extends StatisticalResult> getStatisticalResultFor(String accession, String parameterStableId, ObservationType observationType, String strainAccession)throws IOException, URISyntaxException;
	
}
