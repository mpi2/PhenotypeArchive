package uk.ac.ebi.phenotype.service;

import java.util.ArrayList;
import java.util.concurrent.Future;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import uk.ac.ebi.phenotype.pojo.SexType;
import uk.ac.ebi.phenotype.service.ObservationService.ExperimentField;

@Service
public class ParameterMapService {

    private final HttpSolrServer solr;

    public ParameterMapService(String solrUrl) {
        solr = new HttpSolrServer(solrUrl);
    }

    public ParameterMapService() {
        solr = new HttpSolrServer("http://wwwdev.ebi.ac.uk/mi/impc/dev/solr/experiment");
    }

    /**
     * Asychronous method to get all genes with a given parameter measured. [NOTE] Fiters on B6N background!
     * @param parameterStableId
     * @param sex (null if both sexes wanted)
     * @return  
     * @throws SolrServerException
     * @author tudose
     */
    @Async
    public Future<ArrayList<String>> getAllGenesWithMeasuresForParameter (String parameterStableId, SexType sex) throws SolrServerException{
        String threadName = Thread.currentThread().getName(); 
        System.out.println("   " + threadName + " beginning work on " + parameterStableId);
        SolrQuery query;
        if (sex != null)
            query = new SolrQuery().setQuery(ExperimentField.SEX + ":" + sex.name()).setRows(1);
        else {
            query = new SolrQuery().setQuery("*:*");            
        }
        query.setFilterQueries(ExperimentField.PARAMETER_STABLE_ID + ":" + parameterStableId);
        query.setFilterQueries(ExperimentField.STRAIN + ":\"MGI:2159965\" OR " + ExperimentField.STRAIN + ":\"MGI:2164831\"");
        query.set("facet.field", ExperimentField.GENE_ACCESSION);
        query.set("facet", true);
        query.set("facet.limit", -1); // we want all facets
        QueryResponse response2 = solr.query(query);
        ArrayList<String> genes = new ArrayList<>();
        for (Count gene : response2.getFacetField(ExperimentField.GENE_ACCESSION).getValues()){
            if (gene.getCount()>0){
                genes.add(gene.getName());
            }
        }
        return new AsyncResult<>(genes);
    }

}
