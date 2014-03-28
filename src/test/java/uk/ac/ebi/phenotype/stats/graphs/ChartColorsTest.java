package uk.ac.ebi.phenotype.stats.graphs;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ChartColorsTest {

	@Before
	public void setUp() throws Exception {
		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		
		List<String> colorStrings=ChartColors.getFemaleMaleColorsRgba(0.7);
		System.out.println(colorStrings);
		assertTrue(colorStrings.size()>3);
	}

}
