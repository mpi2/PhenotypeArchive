package uk.ac.ebi.phenotype.service;

import java.util.ArrayList;
import java.util.concurrent.Future;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;

import uk.ac.ebi.phenotype.pojo.SexType;
import uk.ac.ebi.phenotype.service.dto.ObservationDTO;
import uk.ac.ebi.phenotype.web.controller.OverviewChartsController;

public class ParameterMapService {

	private final HttpSolrServer solr;


	public ParameterMapService(String solrUrl) {

		solr = new HttpSolrServer(solrUrl);
	}


	public ParameterMapService() {

		solr = new HttpSolrServer("http://wwwdev.ebi.ac.uk/mi/impc/dev/solr/experiment");
	}


	/**
	 * Asychronous method to get all genes with a given parameter measured.
	 * [NOTE] Fiters on B6N background!
	 * 
	 * @param parameterStableId
	 * @param sex
	 *            (null if both sexes wanted)
	 * @return
	 * @throws SolrServerException
	 * @author tudose
	 */
	@Async
	public Future<ArrayList<String>> getAllGenesWithMeasuresForParameter(String parameterStableId, SexType sex)
	throws SolrServerException {

		SolrQuery query;

		if (sex != null) {
			query = new SolrQuery().setQuery(ObservationDTO.SEX + ":" + sex.name()).setRows(1);
		} else {
			query = new SolrQuery().setQuery("*:*");
		}

		query.setFilterQueries(ObservationDTO.PARAMETER_STABLE_ID + ":" + parameterStableId);
		query.setFilterQueries(ObservationDTO.STRAIN_ACCESSION_ID + ":\"" + StringUtils.join(OverviewChartsController.OVERVIEW_STRAINS, "\" OR " + ObservationDTO.STRAIN_ACCESSION_ID + ":\"") + "\"");
		query.set("facet.field", ObservationDTO.GENE_ACCESSION_ID);
		query.set("facet", true);
		query.set("facet.limit", -1); // we want all facets

		QueryResponse response = solr.query(query);

		ArrayList<String> genes = new ArrayList<>();
		for (Count gene : response.getFacetField(ObservationDTO.GENE_ACCESSION_ID).getValues()) {
			if (gene.getCount() > 0) {
				genes.add(gene.getName());
			}
		}

		return new AsyncResult<>(genes);
	}

}
