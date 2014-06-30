package uk.ac.ebi.phenotype.util;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-config.xml" })

public class ParameterToGeneMapTest {

	@Autowired
	ParameterToGeneMap parameterToGeneMap;
	
	@Ignore
	@Test
	public void testDecentSize(){
		parameterToGeneMap.getMaleMap();
		
	}
}
