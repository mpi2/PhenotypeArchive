package uk.ac.ebi.phenotype.imaging.springrest.images.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

@ContextConfiguration( locations={ "classpath:app-config.xml" })
public class ImagesSolrjTest extends AbstractTransactionalJUnit4SpringContextTests{
	
	@Autowired
	ImagesSolrDao imagesSolrDao;


	@Test
	public void testGetIdsForKeywordsSearch() throws SolrServerException {
		List<String> result = imagesSolrDao.getIdsForKeywordsSearch("accession:MGI\\:1933365", 0, 10);
		assertTrue(result.size() > 0);
	}

	@Test
	public void testGetExperimentalFacetForGeneAccession() throws SolrServerException {
		String geneId = "MGI:1933365";
		QueryResponse solrR = imagesSolrDao.getExperimentalFacetForGeneAccession(geneId);
		assertTrue(solrR.getFacetFields().size() > 0);

	}

	@Test
	public void testGetDocsForGeneWithFacetField() throws SolrServerException {

		String geneId = "MGI:4433191";
		QueryResponse response = imagesSolrDao.getDocsForGeneWithFacetField(geneId, "expName", "Xray","", 0, 5);
		assertTrue(response.getResults().size() > 0);

		for (SolrDocument doc : response.getResults()) {
			assertTrue("Image ID is null for a SOLR result", doc.getFieldValues("id") != null);
		}

		//no Histology Slide expName anymore?? what happened
		response = imagesSolrDao.getDocsForGeneWithFacetField(geneId, "expName", "Wholemount Expression","", 0, 10);
		assertTrue(response.getResults().size() > 0);

		for (SolrDocument doc : response.getResults()) {
			assertTrue("Image ID is null for a SOLR result", doc.getFieldValues("id") != null);
		}
	}

	
	@Test
	public void testGetExpressionFacetForGeneAccession() throws SolrServerException {
		QueryResponse solrR = null;
		solrR = imagesSolrDao.getExpressionFacetForGeneAccession("MGI:1933365");
		assertTrue(solrR.getFacetFields().size() > 0);
	}
	
	
	@Test
	public void testgetFilteredDocsForQuery() throws SolrServerException{
		String filter="expName:Wholemount Expression";
		List<String> filters=new ArrayList<String>();
		filters.add(filter);

		QueryResponse solrR=imagesSolrDao.getFilteredDocsForQuery("accession:MGI\\:1933365",filters,"","", 0, 10);
		assertTrue(solrR.getResults().size()>0);
		
		QueryResponse solrR3=imagesSolrDao.getFilteredDocsForQuery("accession:MGI\\:1933365",filters,"auto_suggest","",  0, 10);
		assertTrue(solrR3.getResults().size()>0);
	}

	
	@Test
	public void testProcessSpacesForSolr() throws MalformedURLException {

		ImagesSolrJ imgJ = new ImagesSolrJ("http://ves-ebi-d0.ebi.ac.uk:8090/mi/impc/dev/solr");
		Map<String,String> testcases = new HashMap<String,String>();
		testcases.put("test \"1", "\"test \\\"1\"");
		testcases.put("test 1", "\"test 1\"");
		testcases.put("*", "*");
		testcases.put("asdf *asdf", "\"asdf *asdf\"");
		testcases.put("test: test", "\"test: test\"");
		testcases.put("\"te st", "\"\\\"te st\"");
		testcases.put("\"te st\"", "\"te st\"");
		
        for(Map.Entry<String, String> testcase : testcases.entrySet()) {
            String key = testcase.getKey();
            String expected = testcase.getValue();
            String processed = imgJ.processValueForSolr(key);
            assertEquals(expected, processed);
        }
		
	}
}
