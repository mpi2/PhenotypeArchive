package uk.ac.ebi.phenotype.stats;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ChartUtilsTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetDecimalPlaces() {
		ExperimentDTO experiment=new ExperimentDTO();
		Set<ObservationDTO> controls=new HashSet<>();
		ObservationDTO ob1=new ObservationDTO();
		ob1.setDataPoint(new Float(1));
		controls.add(ob1);
		experiment.setControls(controls);
		int numberOfDecimalPlaces=ChartUtils.getDecimalPlaces(experiment);
		assertTrue(numberOfDecimalPlaces==1);
		ObservationDTO ob2=new ObservationDTO();
		ob2.setDataPoint(new Float(100.0003));
		controls.add(ob2);
		
		int numberOfDecimalPlaces2=ChartUtils.getDecimalPlaces(experiment);
		assertTrue(numberOfDecimalPlaces2==4);
	}

	@Test
	public void testGetDecimalAdjustedFloat() {
		int numberOfDecimals=2;
		Float n1=new Float(10.0001);
		ChartUtils.getDecimalAdjustedFloat(n1, numberOfDecimals);
		
	}

}
