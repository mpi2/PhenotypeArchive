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

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;


/**
 * 
 * @author tudose
 *
 *	Implenetation of Method 2 from http://en.wikipedia.org/wiki/Quartile
 *	1. Use the median to divide the ordered data set into two halves. If the median is a datum (as opposed to being the mean of the middle two data), include the median in both halves.
 *	2. The lower quartile value is the median of the lower half of the data. The upper quartile value is the median of the upper half of the data.
 */

public class PercentileComputation{

	private List<Float> upperValues;
	private List<Float> lowerValues;
	private List<Float> values;
	
	public PercentileComputation(List<Float> val){
		ArrayList <Float> sortedValues = (ArrayList<Float>)val;
		Collections.sort(sortedValues);
		upperValues = new ArrayList<>();
		lowerValues = new ArrayList<>();
		values = val;
		// Use the median to divide the ordered data set into two halves. 
		// If the median is a datum (as opposed to being the mean of the middle two data), include the median in both halves.
		int n = sortedValues.size(); 
		if (n % 2 == 1){
			lowerValues =  sortedValues.subList(0, (n+1)/2);
			upperValues =  sortedValues.subList((n-1)/2, n);
		}
		else{
			lowerValues =  sortedValues.subList(0, n/2);
			upperValues =  sortedValues.subList(n/2, n);
		}
	}

	//The lower quartile value is the median of the lower half of the data. The upper quartile value is the median of the upper half of the data.
	public float getUpperQuartile() {
		return getMedian(upperValues);
	}
	
	public float getLowerQuartile() {
		return getMedian(lowerValues);
	}
	
	public float getMedian(){
		return getMedian(values);
	}
	
	private Float getMedian(List<Float> list){
		int n = list.size(); 
		if (n % 2 == 1){
			return list.get((n - 1)/2);
		}
		else{
			return (list.get(n/2 - 1) + list.get(n/2)) / 2;
		}
	}

}
