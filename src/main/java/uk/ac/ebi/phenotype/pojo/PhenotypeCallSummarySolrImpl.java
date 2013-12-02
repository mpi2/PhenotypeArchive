package uk.ac.ebi.phenotype.pojo;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import uk.ac.ebi.generic.util.JSONRestUtil;
import uk.ac.ebi.phenotype.stats.GenotypePhenotypeService;
import uk.ac.ebi.phenotype.util.PhenotypeCallSummaryDAOReadOnly;
import uk.ac.ebi.phenotype.util.PhenotypeFacetResult;

public class PhenotypeCallSummarySolrImpl implements
		PhenotypeCallSummaryDAOReadOnly {

	@Autowired
	GenotypePhenotypeService gpService;
	
	private static final Logger log = Logger
			.getLogger(PhenotypeCallSummarySolrImpl.class);
	
	@Override
	public PhenotypeFacetResult getPhenotypeCallByGeneAccession(String accId)
			throws IOException, URISyntaxException {
		return this.getPhenotypeCallByGeneAccessionAndFilter(accId, "");
	}

	@Override
	public PhenotypeFacetResult getPhenotypeCallByMPAccession(
			String phenotype_id) throws IOException, URISyntaxException {
		return this.getPhenotypeCallByMPAccessionAndFilter(phenotype_id, "");

	}

	@Override
	public PhenotypeFacetResult getPhenotypeCallByMPAccessionAndFilter(
			String phenotype_id, String queryString) throws IOException,
			URISyntaxException {
		// http://wwwdev.ebi.ac.uk/mi/solr/genotype-phenotype/select/?q=mp_term_id:MP:0010025&rows=100&version=2.2&start=0&indent=on&defType=edismax&wt=json&facet=true&facet.field=resource_fullname&facet.field=top_level_mp_term_name&
		return gpService.getMPCallByMPAccessionAndFilter(phenotype_id, queryString);
	}

	@Override
	public PhenotypeFacetResult getPhenotypeCallByGeneAccessionAndFilter(
			String accId, String filterString) throws IOException,
			URISyntaxException {
		return gpService.getMPByGeneAccessionAndFilter(accId, filterString);
	}

	@Override
	public List<? extends StatisticalResult> getStatisticalResultFor(
			String accession, String parameterStableId,
			ObservationType observationType, String strainAccession)
			throws IOException, URISyntaxException {
		return gpService.getStatsResultFor(accession, parameterStableId, observationType, strainAccession);
	}
	
	
	
}
