package uk.ac.ebi.phenotype.stats.timeseries;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.collections.map.LinkedMap;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.apache.log4j.Logger;

import uk.ac.ebi.phenotype.dao.DiscreteTimePoint;

public class TimeSeriesStats {

	
	
	private static final Logger log = Logger.getLogger(TimeSeriesStats.class);

	public List<DiscreteTimePoint> getMeanDataPoints(List<DiscreteTimePoint> timeSeriesDataForLine) {
		List<DiscreteTimePoint> means=new ArrayList<>();
		

			// Add the data from the array
		 SortedMap<Float, List<Float>> timeMap=new TreeMap<Float, List<Float>>();
		 for(DiscreteTimePoint timePoint: timeSeriesDataForLine) {
			 if(timeMap.containsKey(timePoint.getDiscreteTime())) {
				 timeMap.get(timePoint.getDiscreteTime()).add(timePoint.getData());
			 }else {
				 List<Float> dataPointsFloats=new ArrayList<Float>();
				 dataPointsFloats.add(timePoint.getData());
				 timeMap.put(timePoint.getDiscreteTime(), dataPointsFloats);
			 }
			 
		 }
		log.debug("time map size="+timeMap.keySet().size());
		 
			for(Float time:  timeMap.keySet()) {
				 DescriptiveStatistics stats = new DescriptiveStatistics();
				log.debug("time="+time+" number of points="+ timeMap.get(time).size());
				for(Float data: timeMap.get(time)) {
			        stats.addValue(data);
				}
				
				// Compute some statistics
				double mean = stats.getMean();
				double std = stats.getStandardDeviation();
				double median = stats.getPercentile(50);
				DiscreteTimePoint meanDataTimePoint=new DiscreteTimePoint(time, new Float(mean), new Float(stats.getStandardDeviation()));
				List<Float> errorPair=new ArrayList<>();
				
				Float lower=new Float(mean-std);
				Float higher=new Float(mean+std);
				errorPair.add(lower);
				errorPair.add(higher);
				log.debug("stddev="+std+" lower="+lower+" higher="+higher);
				meanDataTimePoint.setErrorPair(errorPair);
				//meanDataTimePoint.s
				means.add(meanDataTimePoint);
				
			}

			
			return means;
	}
}
