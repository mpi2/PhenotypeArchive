package uk.ac.ebi.phenotype.stats.graphs;

import java.util.ArrayList;
import java.util.List;

import org.antlr.grammar.v3.ANTLRv3Parser.finallyClause_return;

import uk.ac.ebi.phenotype.pojo.SexType;


/**
 * class to keep static variables for New Design colors
 * @author jwarren
 *
 */
public class ChartColors {
	//HEX #EF7B0B
	//rgb(239, 123, 11)
	public static final List<String>maleRgb=java.util.Arrays.asList("9, 120, 161" ,  "61, 167, 208", "100, 178, 208",  "3, 77, 105","36, 139, 75", "1, 121, 46");
	
	//rgb(239, 123, 11)
	//rgb(247, 157, 70)
	//rgb(247, 181, 117)
	//rgb(191, 75, 50)
	//rgb(166, 30, 1)
	
	public static final List<String>femaleRgb=java.util.Arrays.asList("239, 123, 11" ,  "247, 157, 70", "247, 181, 117",  "191, 75, 50", "166, 30, 1");
	
	
	public static final Double alphaBox=1.0;//set the opacity for boxes here
	public static final Double alphaScatter=0.7;//set the opacity for scatter points here
	
	/**
	 * get a string to represent rgba for highcharts for either sex and choose your alpha (opacity) 0.0-1.0
	 * @param sexType
	 * @param index
	 * @param alpha
	 * @return
	 */
	public static String  getRgbaString(SexType sexType, int index, Double alpha) {
		String defaultColor="rgba(9, 120, 161, 0.5)";
		if(index>maleRgb.size()) {
			System.err.println("no color found returning default");
			return defaultColor;
		}
		if(sexType.equals(SexType.male)) {
		return "rgba("+maleRgb.get(index)+"," +alpha+")";
		}
		if(sexType.equals(SexType.female)) {
			return "rgba("+femaleRgb.get(index)+"," +alpha+")";
			}
		System.err.println("no color found returning default");
		return defaultColor;
	}
	
}
