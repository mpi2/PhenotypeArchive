package uk.ac.ebi.phenotype.stats;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.ebi.generic.util.SolrIndex;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:app-config.xml" })
@TransactionConfiguration
@Transactional
public class PreliminaryPhenotypeCallSummaryTest {

	@Autowired
	private SolrIndex solrIndex;
	
	@Test
	public void testPreliminaryPhenotypeCallSummaryTableCreator(){
		
		PreliminaryPhenotypeCallSummaryTableCreator creator=new PreliminaryPhenotypeCallSummaryTableCreator();
		creator.getPreliminaryCallSummaries(solrIndex);
	}
}
