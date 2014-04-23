package uk.ac.ebi.phenotype.stats;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;



public class ColorCodingPaletteTest {

	List<Double> generateRamdomPvalues(double min, double max, int capacity) {
		List<Double> l = new ArrayList<Double>();
		for (int i=0; i<capacity; i++) {
			l.add(Math.random());
		}
		return l;
	}

	@Test
	public void testPaletteOne() {

		ColorCodingPalette ccp = new ColorCodingPalette();
		for (int i=0; i<10000; i++) {
			List<Double> pValues = generateRamdomPvalues(0,0, i+1);
			double scale = 0;
			double minimalPValue = 0.005;
			for (int maxColorIndex = 3; maxColorIndex <= 9; maxColorIndex++) {
				ccp.generateColors(pValues, maxColorIndex, scale, minimalPValue);
//				System.out.println(maxColorIndex + " " + ccp.getPalette().size() + " " + ccp.getPalette().get(1).length);
				assertTrue(ccp.getPalette().size() == 3 && ccp.getPalette().get(1).length == maxColorIndex);
//				System.out.println(pValues.size() + " " + ccp.getColors().length);
				assertTrue(ccp.getColors().length > 0 && ccp.getColors().length == pValues.size());
			}
		}
	}
}
