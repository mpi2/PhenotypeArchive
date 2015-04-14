package uk.ac.ebi.phenotype.chart;

import java.text.DecimalFormat;
import java.util.ArrayList;
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
			
			return StringUtils.capitalize(sex.getName()) + " " + (zyg == null ? "WT" : StringUtils.capitalize(zyg.getName())) ;
		}
	
		
}
