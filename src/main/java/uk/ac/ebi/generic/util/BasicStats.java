package uk.ac.ebi.generic.util;

import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
/**
 * Basic Stats Calculator for Box Plots
 * @author jwarren
 *
 */
public class BasicStats {

private static final Logger logger = Logger.getLogger(BasicStats.class);

	private List<Float> listOfFloats;

	private Float median=null;

	private int middle=0;

	private boolean sorted=false;
	// the array Float[] m MUST BE SORTED
//	public static Float median(Float[] m) {
//	    int middle = m.length/2;
//	    if (m.length%2 == 1) {
//	        return m[middle];
//	    } else {
//	        return (m[middle-1] + m[middle]) / 2.0;
//	    }
//	}
	public BasicStats(List<Float> listOfFloats){
		this.listOfFloats=listOfFloats;
		this.median=this.median();
	}
	
	public  Float median(){
		if(median!=null)return this.median;
		this.median=median(listOfFloats);
		return this.median;
	}
	
	private Float median(List<Float> floats){
		if(!sorted){//sort the main list if not sorted already - if main list is sorted we don't need to sort the lowerQ or upperQ again!
		Collections.sort(floats);
		sorted=true;
		}
	    this.middle = floats.size()/2;
	    if (floats.size()%2 == 1) {
	        return floats.get(middle);
	    } else {
	        return  (float) ((floats.get(middle-1) + floats.get(middle)) / 2.0);
	    }
	}
	

	public Float lowerQuartile(){
		if(this.median==null)this.median=median();
		List<Float> lowerNumbers=listOfFloats.subList(0, this.middle);
		logger.debug(lowerNumbers);
		Float lowerQ = this.median(lowerNumbers);
		logger.debug("lowerQ="+lowerQ);
		return lowerQ;
		
	}
	
	public Float upperQuartile(){
		if(this.median==null)this.median=median();
		List<Float> lowerNumbers=listOfFloats.subList(this.middle+1, listOfFloats.size());
		logger.debug(lowerNumbers);
		Float upperQ = this.median(lowerNumbers);
		logger.debug("upperQ="+upperQ);
		return upperQ;
	}
	
	/**
	 * for outliers
	 * @return
	 */
	public Float getSmallest(){
		Float smallest=this.listOfFloats.get(0);
		logger.debug("smallest="+smallest);
		return smallest;
		
	}
	/**
	 * for outliers
	 * @return
	 */
	public Float getLargest(){
		Float largest=this.listOfFloats.get(listOfFloats.size()-1);
		logger.debug("largest="+largest);
		return largest;
	}
	
	 
	    

	   public  Float getMean()
	    {
	        Float sum = new Float(0.0);
	        for(Float a : listOfFloats)
	            sum += a;
	            return sum/listOfFloats.size();
	    }

	        public Float getVariance()
	        {
	            Float mean = getMean();
	            Float temp = new Float(0);
	            for(Float a :listOfFloats)
	                temp += (mean-a)*(mean-a);
	                float variance = temp/listOfFloats.size();
	                logger.debug("variance="+variance);
	                return variance;
	        }

	        /**
	         * 
	         * @return Variance(Population Standard deviation)
	         */
	        public  Float getStdDev()
	        {
//	        	double d = 3.0;
//	        	float f = (float) d;
	        	Float stdDev = Float.valueOf((float)Math. sqrt(getVariance()));
	        	logger.debug("stdDev="+stdDev);
	            return (Float.valueOf((float)Math. sqrt(getVariance())));
	        }

//	        public static Float median() 
//	        {
//	               Float[] b = new int[data.length];
//	               System.arraycopy(data, 0, b, 0, b.length);
//	               Arrays.sort(b);
//
//	               if (data.length % 2 = 0) 
//	               {
//	                  return (b[(b.length / 2) - 1] + b[b.length / 2]) / 2.0;
//	               } 
//	               else 
//	               {
//	                  return b[b.length / 2];
//	               }
//	        }
}
