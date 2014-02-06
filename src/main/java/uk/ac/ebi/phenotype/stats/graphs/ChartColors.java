package uk.ac.ebi.phenotype.stats.graphs;

import java.util.ArrayList;
import java.util.List;

import uk.ac.ebi.phenotype.pojo.SexType;


/**
 * class to keep static variables for New Design colors
 * @author jwarren
 *
 */
public class ChartColors {
	//HEX #EF7B0B
	//rgb(239, 123, 11)
	public static final List<String>maleRgb=java.util.Arrays.asList("9, 120, 161" ,  "61, 167, 208", "100, 178, 208",  "3, 77, 105, 0.5");
	
	public static String  getRgbaString(SexType sexType, int index, Double alpha) {
		if(sexType.equals(SexType.male)) {
		return "rgba("+maleRgb.get(index)+"," +alpha+")";
		}
		return null;
	}
	
}
