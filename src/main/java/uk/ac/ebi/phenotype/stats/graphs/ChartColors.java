package uk.ac.ebi.phenotype.stats.graphs;

import java.util.ArrayList;
import java.util.List;

import org.antlr.grammar.v3.ANTLRv3Parser.finallyClause_return;
import org.apache.bcel.generic.RETURN;
import org.json.JSONException;
import org.json.JSONObject;
import org.semanticweb.elk.reasoner.saturation.conclusions.ForwardLink.ThisBackwardLinkRule;

import uk.ac.ebi.phenotype.pojo.SexType;
import uk.ac.ebi.phenotype.pojo.ZygosityType;


/**
 * class to keep static variables for New Design colors
 * @author jwarren
 *
 */
public class ChartColors {
    
    public static final List<String> highDifferenceColors=java.util.Arrays.asList("239, 123, 11" ,  "9, 120, 161", "119, 119, 119",  "238, 238, 238","36, 139, 75", "191, 75, 50", "255, 201, 67", "191, 151, 50", "239, 123, 11" ,  "247, 157, 70", "247, 181, 117",  "191, 75, 50", "151, 51, 51");
	//HEX #EF7B0B
	//rgb(239, 123, 11)
	public static final List<String>maleRgb=java.util.Arrays.asList("9, 120, 161" ,  "61, 167, 208", "100, 178, 208",  "3, 77, 105","36, 139, 75", "1, 121, 46", "51, 51, 51", "191, 151, 50");
	
	//rgb(239, 123, 11)
	//rgb(247, 157, 70)
	//rgb(247, 181, 117)
	//rgb(191, 75, 50)
	//rgb(166, 30, 1)
	
	public static final List<String>femaleRgb=java.util.Arrays.asList("239, 123, 11" ,  "247, 157, 70", "247, 181, 117",  "191, 75, 50", "166, 30, 1", "191, 75, 50", "166, 30, 1", "255, 201, 67");
	
	
	public static final Double alphaBox=1.0;//set the opacity for boxes here
	public static final Double alphaScatter=0.7;//set the opacity for scatter points here

	private static String wtColor="239, 123, 11";
	private static String mutantColor="9, 120, 161";
	
	/**
	 * get a string to represent rgba for highcharts for either sex and choose your alpha (opacity) 0.0-1.0
	 * @param sexType
	 * @param index
	 * @param alpha
	 * @return
	 */
	public static String  getRgbaString(SexType sexType, int index, Double alpha) {
		String defaultColor="rgba(9, 120, 161, 0.5)";
		if(index>=maleRgb.size()) {
			System.err.println("no color found returning default");
			index=index % maleRgb.size();
			System.out.println("color index="+index);
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
	
	public static List<String> getFemaleMaleColorsRgba(Double alpha) {
		List<String> colorStrings=new ArrayList<String>(); 
		for(int i=0; i<ChartColors.maleRgb.size(); i++) {
			colorStrings.add(getRgbaString(SexType.female, i, alphaScatter));
			colorStrings.add(getRgbaString(SexType.male, i, alphaScatter));
		}
		
		return colorStrings;
		
	}
        
        public static List<String> getHighDifferenceColorsRgba(Double alpha) {
		return highDifferenceColors;	
	}

	public static String getMutantColor(Double alpha) {
		return "rgba("+mutantColor+"," +alpha+")";
	}
	
	public static String getWTColor(Double alpha) {
		return "rgba("+wtColor+"," +alpha+")";
	}
	
	/**
	 * convenience method that uses default scatter alpha for getMarkerString
	 * @param sex
	 * @param zygosityType
	 * @return
	 */
	public static String getMarkerString(SexType sex, ZygosityType zygosityType) {
		
		return getMarkerString(sex, zygosityType, null);
	}
	/**
	 * Get a marker string for use in highcharts to display the data with consitent colors and symbols based on these parameters
	 * @param sex
	 * @param zygosityType if null then its WT
	 * @return
	 */
	public static String getMarkerString(SexType sex, ZygosityType zygosityType, Double alpha) {
		if(alpha==null) {
			alpha=ChartColors.alphaScatter;
		}
		String symbol="circle";
		String lineColor=ChartColors.getMutantColor(alpha);
		String color=ChartColors.getMutantColor(alpha);
		String fillColor=color;
		if(zygosityType==null) {// then its WT
			color=ChartColors.getWTColor(alpha);
			fillColor="white";
			lineColor=color;
		}
		
		if(sex.equals(SexType.male) ) {
			symbol="triangle";
		}
		//&& chartsSeriesElement.getControlOrZygosity().equals("WT")
		
String marker="marker:{"
			+"symbol: '"+symbol
			+"', fillColor:  '"+fillColor+"'," +
					" lineWidth: 1,"
		    +" lineColor: '"+lineColor+ "' "
      +" }";
		return marker;
	}
	
	public static JSONObject getMarkerJSONObject(SexType sex, ZygosityType zygosityType) throws JSONException {
		String markerString=getMarkerString(sex, zygosityType).replace("marker:", "");
		return new JSONObject(markerString);
	}
}
