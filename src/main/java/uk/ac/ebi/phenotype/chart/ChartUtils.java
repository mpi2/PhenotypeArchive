package uk.ac.ebi.phenotype.chart;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.log4j.Logger;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.json.JSONArray;

import uk.ac.ebi.phenotype.pojo.SexType;
import uk.ac.ebi.phenotype.pojo.ZygosityType;
import uk.ac.ebi.phenotype.service.dto.ExperimentDTO;
import uk.ac.ebi.phenotype.service.dto.ObservationDTO;

public class ChartUtils {
	
	static List<String> eslim702 = Arrays.asList("ESLIM_009_001_003", "ESLIM_010_001_003", "ESLIM_011_001_011", "ESLIM_012_001_005", "ESLIM_013_001_018", "ESLIM_022_001_001");
	static List<String> eslim701 = Arrays.asList("ESLIM_001_001_001", "ESLIM_002_001_001", "ESLIM_003_001_001", "ESLIM_004_001_001", "ESLIM_005_001_001", "ESLIM_020_001_001", "ESLIM_022_001_001");
	static List<String> impcBwt = Arrays.asList("IMPC_GRS_003_001", "IMPC_CAL_001_001", "IMPC_DXA_001_001", "IMPC_HWT_007_001", "IMPC_PAT_049_001", "IMPC_BWT_001_001", "IMPC_ABR_001_001", "IMPC_CHL_001_001", "TCP_CHL_001_001", "HMGU_ROT_004_001");


	private static final Logger logger = Logger.getLogger(ChartUtils.class);

	/**
	 * method that changes the javascript of the chart to have a new max yAxis, currently relies on replacing a string "max: 2" to another value
	 * @param chartsAndTablesForParameter
	 * @param max
	 * @param max2 
	 * @return
	 */
		public static List<ChartData> alterMinAndMaxYAxisOfCharts(List<ChartData> chartsAndTablesForParameter,
				Float min, Float max) {
			
			for(ChartData chartNTable: chartsAndTablesForParameter){
				//for each chart replace the strings that set the min and max values
				String chartString=chartNTable.getChart();
				String newChartString=chartString.replace("min: 0", "min: "+min);
				newChartString=newChartString.replace("max: 2", "max: "+max);
				logger.debug("altering chart string="+newChartString);
				chartNTable.setChart(newChartString);
				}
			return chartsAndTablesForParameter;
		}

		public static int getDecimalPlaces(ExperimentDTO experiment) {
			int numberOfDecimalPlaces=0;
			int i=0;
			for(ObservationDTO control:experiment.getControls()) {
				Float dataPoint = control.getDataPoint();
				String dString=dataPoint.toString();
				int pointIndex=dString.indexOf(".");
				int length=dString.length();
				int tempNumber=length-(pointIndex+1);
				if(tempNumber>numberOfDecimalPlaces)numberOfDecimalPlaces=tempNumber;
				i++;
				if(i>100)break;//only sample the first 100 hopefully representative
			}
			return numberOfDecimalPlaces;
		}
		
		public static Float getDecimalAdjustedFloat(Float number, int numberOfDecimals) {
			//1 decimal #.#
			String decimalFormatString="#.";
			for(int i=0; i<numberOfDecimals;i++) {
				decimalFormatString+="#";
			}
			
			DecimalFormat df = new DecimalFormat(decimalFormatString);
			String decimalAdjustedMean = df.format(number);
			Float decFloat=new Float(decimalAdjustedMean);
			return decFloat;
		}
		
		public static String getChartPageUrlPostQc( String baseUrl, String geneAcc, String alleleAcc, ZygosityType zygosity, String parameterStableId, String pipelineStableId, String phenotypingCenter){
			String url= baseUrl;
	    	url += "/charts?accession=" + geneAcc;
	    	url += "&allele_accession=" + alleleAcc;
	    	if (zygosity != null){
	    		url += "&zygosity=" + zygosity.name();
	    	}
			if (parameterStableId != null) {
				url += "&parameter_stable_id=" + parameterStableId;
			}
			if (pipelineStableId != null) {
				url += "&pipeline_stable_id=" + pipelineStableId;
			}
			if (phenotypingCenter != null) {
				url += "&phenotyping_center=" + phenotypingCenter;
			}
	        return url;
		}
		
		
		public static Map<String, Float> getMinMaxXAxis(List<ChartsSeriesElement> chartsSeriesElementsList, ExperimentDTO experiment){
			
			Float min = new Float(Integer.MAX_VALUE);
			Float max = new Float(Integer.MIN_VALUE);
			Map<String, Float> res = new HashMap<>();
			
			int decimalPlaces = ChartUtils.getDecimalPlaces(experiment);
			
			for (ChartsSeriesElement chartsSeriesElement : chartsSeriesElementsList) {	
			
				List<Float> listOfFloats = chartsSeriesElement.getOriginalData();
				PercentileComputation pc = new PercentileComputation(listOfFloats);
				
				for (Float point : listOfFloats) {
					if (point > max) max = point;
					if (point < min) min = point;
				}
								
				if (listOfFloats.size() > 0) {
					
					double Q1 = ChartUtils.getDecimalAdjustedFloat(new Float(pc.getLowerQuartile()), decimalPlaces);
					double Q3 = ChartUtils.getDecimalAdjustedFloat(new Float(pc.getUpperQuartile()), decimalPlaces);
					double IQR = Q3 - Q1;

					Float minIQR = ChartUtils.getDecimalAdjustedFloat(new Float(Q1 - (1.5 * IQR)), decimalPlaces);
					if (minIQR < min) min = minIQR;

					Float maxIQR = ChartUtils.getDecimalAdjustedFloat(new Float(Q3 + (1.5 * IQR)), decimalPlaces);
					if (maxIQR > max) max = maxIQR;

				}
			}
			
			res.put("min", min);
			res.put("max", max);
			
			return res;
		}
		
		public static String getLabel(ZygosityType zyg, SexType sex){
			
			return StringUtils.capitalize(sex.getName()) + " " + (zyg == null ? "WT" : StringUtils.capitalize(zyg.getName().substring(0, 3) + ".")) ;
		}
	
		
		public static String getPlotParameter(String parameter){
							
			if (eslim702.contains(parameter)){
				return "ESLIM_022_001_702";
			} else if (eslim701.contains(parameter)){
				return "ESLIM_022_001_701";
			} else if (impcBwt.contains(parameter)){
				return "IMPC_BWT_008_001";
			}
			
			return parameter;
		}
		
		public static ChartType getPlotType(String parameter){
			
			if (eslim702.contains(parameter) || parameter.equals("ESLIM_022_001_702") || eslim701.contains(parameter) || parameter.equals("ESLIM_022_001_701") ||
				impcBwt.contains(parameter) || parameter.equals("IMPC_BWT_008_001")){
				return ChartType.TIME_SERIES_LINE;
			}
			
			return null;
		}
		
}
