package uk.ac.ebi.phenotype.stats;

import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.validation.constraints.AssertTrue;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocumentList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-config.xml" })


public class PercentileComputationTest {

	@Test
	public void testOddNumber(){

		ArrayList<Float> testarray = new ArrayList<>();
		testarray.add((float)6);
		testarray.add((float)7);
		testarray.add((float) 15);
		testarray.add((float)36);
		testarray.add((float)39);
		testarray.add((float)40);
		testarray.add((float)41);
		testarray.add((float)42);
		testarray.add((float)43);
		testarray.add((float)47);
		testarray.add((float)49);
		PercentileComputation pc = new PercentileComputation(testarray);
		assertTrue(pc.getLowerQuartile() == 25.5);
		assertTrue(pc.getUpperQuartile() == 42.5);
	}

	@Test
	public void testEvenNumber(){
		ArrayList<Float> testarray = new ArrayList<>();
		testarray.add((float)7);
		testarray.add((float) 15);
		testarray.add((float)36);
		testarray.add((float)39);
		testarray.add((float)40);
		testarray.add((float)41);
		PercentileComputation pc = new PercentileComputation(testarray);
		assertTrue(pc.getLowerQuartile() == 15.0);
		assertTrue(pc.getUpperQuartile() == 40.0);
	}

	@Test
	public void testNumbersFromHugh(){
		ArrayList<Float> testarray = new ArrayList<>();
		testarray.add((float) 0.3441);
		testarray.add((float) 0.3675);
		testarray.add((float) 0.4842);
		testarray.add((float) 0.3074);
		testarray.add((float) 0.489);
		testarray.add((float) 0.3188);
		testarray.add((float) 0.385);		
		PercentileComputation pc = new PercentileComputation(testarray);
		assertTrue((float)ChartUtils.getDecimalAdjustedFloat(pc.getLowerQuartile(),4) == (float)0.3314);
		assertTrue((float)ChartUtils.getDecimalAdjustedFloat(pc.getUpperQuartile(),4) == (float)0.4346);
		assertTrue((float)ChartUtils.getDecimalAdjustedFloat(pc.getMedian(),4) == (float)0.3675);
	}

	
}
