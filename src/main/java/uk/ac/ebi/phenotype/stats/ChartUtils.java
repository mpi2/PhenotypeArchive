package uk.ac.ebi.phenotype.stats;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;

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
				logger.debug(newChartString);
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
				//System.out.println(tempNumber);
				if(tempNumber>numberOfDecimalPlaces)numberOfDecimalPlaces=tempNumber;
				i++;
				if(i>100)break;//only sample the first 100 hopefully representative
			}
			return numberOfDecimalPlaces;
		}
		
		public static Float getDecimalAdjustedFloat(Float number, int numberOfDecimals) {
			//1 decimal #.#
			DecimalFormat df = new DecimalFormat("#.");
			String decimalAdjustedMean = df.format(number);
			Float decFloat=new Float(decimalAdjustedMean);
			return decFloat;
		}
}
