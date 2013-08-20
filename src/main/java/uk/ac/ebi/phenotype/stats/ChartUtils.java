package uk.ac.ebi.phenotype.stats;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

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

}
