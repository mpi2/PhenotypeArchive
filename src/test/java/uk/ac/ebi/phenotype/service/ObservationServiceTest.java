package uk.ac.ebi.phenotype.service;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.solr.client.solrj.SolrServerException;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ObservationServiceTest {

    private ObservationService os = new ObservationService();
 
    @Test
    public void testGetAllGenesWithMeasuresForParameter(){
    	Future<ArrayList<String>> res = null;
        System.out.println("About to run...");
        try{
        	res = os.getAllGenesWithMeasuresForParameter("ESLIM_003_001_011", null);
        	System.out.println("And the result of get() is " + res.get());
        }catch(Exception e){
        	e.printStackTrace();
        }
        Assert.assertTrue(res != null);
 
    }
	
}
