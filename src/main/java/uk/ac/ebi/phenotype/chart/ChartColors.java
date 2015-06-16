/*******************************************************************************
 * Copyright 2015 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 *******************************************************************************/
package uk.ac.ebi.phenotype.chart;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import uk.ac.ebi.phenotype.pojo.SexType;
import uk.ac.ebi.phenotype.pojo.ZygosityType;


/**
 * class to keep static variables for New Design colors
 * @author jwarren
 *
 */
public class ChartColors {
    
    public static final List<String> highDifferenceColors=java.util.Arrays.asList("239, 123, 11" ,  "9, 120, 161", "119, 119, 119",  "238, 238, 180","36, 139, 75", "191, 75, 50", "255, 201, 67", "191, 151, 50", "239, 123, 11" ,  "247, 157, 70", "247, 181, 117",  "191, 75, 50", "151, 51, 51");
	//HEX #EF7B0B
	//rgb(239, 123, 11)
	public static final List<String>maleRgb=java.util.Arrays.asList("9, 120, 161" ,  "61, 167, 208", "100, 178, 208",  "3, 77, 105","36, 139, 75", "1, 121, 46", "51, 51, 51", "191, 151, 50");
	
	//rgb(239, 123, 11)
	//rgb(247, 157, 70)
	//rgb(247, 181, 117)
	//rgb(191, 75, 50)
	//rgb(166, 30, 1)
	
	public static final List<String>femaleRgb=java.util.Arrays.asList("239, 123, 11" ,  "247, 157, 70", "247, 181, 117",  "191, 75, 50", "166, 30, 1", "191, 75, 50", "166, 30, 1", "255, 201, 67");
	
	
	public static final Double alphaOpaque = 1.0;
	public static final Double alphaTranslucid70 = 0.7;
	public static final Double alphaTranslucid50 = 0.5;
	public static final Double alphaTranslucid20 = 0.2;

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
		String defaultColor="\'rgba(9, 120, 161, 0.5)\'";
		if(index>=maleRgb.size()) {
			System.err.println("no color found returning default");
			index=index % maleRgb.size();
			return defaultColor;
		}
		if(sexType.equals(SexType.male)) {
		return "\'rgba("+maleRgb.get(index)+"," +alpha+")\'";
		}
		if(sexType.equals(SexType.female)) {
			return "\'rgba("+femaleRgb.get(index)+"," +alpha+")\'";
			}
		System.err.println("no color found returning default");
		return defaultColor;
	}
	
	public static String getDefaultControlColor (Double alpha){
		return "\'rgba("+femaleRgb.get(3)+"," +alpha+")\'";
	}
	
	public static List<String> getFemaleMaleColorsRgba(Double alpha) {
		List<String> colorStrings=new ArrayList<String>(); 
		for(int i=0; i<ChartColors.maleRgb.size(); i++) {
			colorStrings.add(getRgbaString(SexType.female, i, alphaTranslucid70));
			colorStrings.add(getRgbaString(SexType.male, i, alphaTranslucid70));
		}		
		return colorStrings;		
	}
    
	public static List<String> getMaleColorsRgba(Double alpha) {
		List<String> colorStrings = new ArrayList<String>();
		for (String colorString : ChartColors.maleRgb) {
			colorStrings.add("\'rgba(" + colorString + "," + alpha + ")\'");
		}

		return colorStrings;
	}
	
	public static List<String> getHighDifferenceColorsRgba(Double alpha) {	
        List<String> colorStrings=new ArrayList<String>(); 
		for(String colorString:ChartColors.highDifferenceColors) {
			colorStrings.add("\'rgba("+colorString+"," +alpha+")\'");
		}
		
		return colorStrings;
	}

	public static String getMutantColor(Double alpha) {
		return "\'rgba("+mutantColor+"," +alpha+")\'";
	}
	
	public static String getWTColor(Double alpha) {
		return "\'rgba("+wtColor+"," +alpha+")\'";
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
		
		Double alphaMutants = alpha;
		Double alphaControl = alpha;
		Double alphaControlLine = alpha;
		
		if (alpha == null) {
			alphaMutants = ChartColors.alphaTranslucid70;
			alphaControl = ChartColors.alphaTranslucid20;
			alphaControlLine = ChartColors.alphaTranslucid50;
		}
		
		String symbol="circle";
		String lineColor=ChartColors.getMutantColor(alphaMutants);
		String fillColor=ChartColors.getMutantColor(alphaMutants);
		
		if (zygosityType == null) {// then its WT
			fillColor = ChartColors.getWTColor(alphaControl);
			lineColor = ChartColors.getWTColor(alphaControlLine);
		}
		
		if (sex.equals(SexType.male)) {
			symbol="triangle";
		}
		
		String marker = "marker:{"
			+ " symbol: '" + symbol + "', "
			+ " fillColor:  " + fillColor + ","
			+ " lineWidth: 1,"
			+ " radius: 3,"
			+ " lineColor: " + lineColor + " "
			+ "}";
		return marker;
	}
	
	public static JSONObject getMarkerJSONObject(SexType sex, ZygosityType zygosityType) throws JSONException {
		String markerString=getMarkerString(sex, zygosityType).replace("marker:", "");
		return new JSONObject(markerString);
	}
	
	/**
	 * get a deefault list of colors for WT and zygosities, color RGB String e.g. Homozygous, "239, 123, 11"
	 * @return
	 */
	public static Map<String,String> getZygosityColorMap(){
		//"239, 123, 11" ,  "9, 120, 161", "119, 119, 119",
		Map<String,String> zygColorMap=new LinkedHashMap<>();
		zygColorMap.put("WT", ChartColors.getWTColor(alphaOpaque));
		zygColorMap.put(ZygosityType.homozygote.name(),getHighDifferenceColorsRgba(alphaOpaque).get(1) );
		zygColorMap.put(ZygosityType.heterozygote.name(),getHighDifferenceColorsRgba(alphaOpaque).get(2));
		zygColorMap.put(ZygosityType.hemizygote.name(),getHighDifferenceColorsRgba(alphaOpaque).get(3) );
		return zygColorMap;
	}
}
