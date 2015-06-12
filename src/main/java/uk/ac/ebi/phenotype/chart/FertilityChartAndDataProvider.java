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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang.WordUtils;
import org.springframework.stereotype.Service;

import uk.ac.ebi.phenotype.pojo.Parameter;
import uk.ac.ebi.phenotype.service.dto.ExperimentDTO;
import uk.ac.ebi.phenotype.service.dto.ObservationDTO;

@Service
public class FertilityChartAndDataProvider {

	public FertilityDTO doFertilityData(Parameter parameter, FertilityDTO fertility) {

		// we need 3 sets of data for the 3 graphs
		Map<String, ObservationDTO> paramStableIdToObservation = fertility.getParamStableIdToObservation();
//		List<ObservationDTO> totals = new ArrayList<>();
//		totals.add(paramStableIdToObservation.get(FertilityDTO.totalPupsWt));
//		totals.add(paramStableIdToObservation.get(FertilityDTO.totalPupsHom));
//		totals.add(paramStableIdToObservation.get(FertilityDTO.totalPupsHet));

//		List<ObservationDTO> male = new ArrayList<>();
//		male.add(paramStableIdToObservation.get(FertilityDTO.totalMaleWt));
//		male.add(paramStableIdToObservation.get(FertilityDTO.totalMaleHom));
//		male.add(paramStableIdToObservation.get(FertilityDTO.totalMaleHet));
//
//		List<ObservationDTO> female = new ArrayList<>();
//
//		female.add(paramStableIdToObservation.get(FertilityDTO.totalFemaleWt));
//		female.add(paramStableIdToObservation.get(FertilityDTO.totalFemaleHom));
//		female.add(paramStableIdToObservation.get(FertilityDTO.totalFemaleHet));

		Map<String, Integer> totalLabelToNumber = new LinkedHashMap<>();
		//code here to look at numbers we get back on Fertility data
		for(String key:paramStableIdToObservation.keySet()){
			ObservationDTO ob = paramStableIdToObservation.get(key);
			if (ob.getDataPoint() != null) {
				if (Math.round(ob.getDataPoint()) > 0) {
					totalLabelToNumber.put(WordUtils.capitalize(ob.getParameterName()), Math.round(ob.getDataPoint()));
				}
			}
		}
		if(totalLabelToNumber.size()==0){//we have no data for this graph so don't return a Fertility object so we can not display a chart holder
			return null;
		}
		
		String totalChart = PieChartCreator.getPieChart(totalLabelToNumber, "totalChart", "Total Counts (Male and Female)",null);
		System.out.println("total fert chart="+totalChart);
		fertility.setTotalChart(totalChart);
		
//		for (ObservationDTO ob : totals) {
//			if (ob.getDataPoint() != null) {
//				if (Math.round(ob.getDataPoint()) > 0) {
//					totalLabelToNumber.put(WordUtils.capitalize(ob.getParameterName()), Math.round(ob.getDataPoint()));
//				}
//			}
//		}
//		String totalChart = PieChartCreator.getPieChart(totalLabelToNumber, "totalChart", "Total Counts (Male and Female)", ChartColors.getZygosityColorMap());
//		 fertility.setTotalChart(totalChart);
		
//		Map<String, Integer> maleLabelToNumber = new LinkedHashMap<>();
//		for (ObservationDTO ob : male) {
//			if (Math.round(ob.getDataPoint()) > 0) {
//				maleLabelToNumber.put(WordUtils.capitalize(ob.getParameterName()), Math.round(ob.getDataPoint()));
//			}
//		}
		// Map<String, Integer> femaleLabelToNumber = new LinkedHashMap<>();
		// String maleChart = PieChartCreator.getPieChart(maleLabelToNumber,
		// "maleChart", "Male Counts", ChartColors.getZygosityColorMap());
		// fertility.setMaleChart(maleChart);
		//
		// for(ObservationDTO ob:female){
		// if(Math.round(ob.getDataPoint())>0){
		// femaleLabelToNumber.put(WordUtils.capitalize(ob.getParameterName()),
		// Math.round(ob.getDataPoint()));
		// }
		// }
		// String femaleChart = PieChartCreator.getPieChart(femaleLabelToNumber,
		// "femaleChart", "Female Counts", ChartColors.getZygosityColorMap());
		// fertility.setFemaleChart(femaleChart);
		return fertility;
	}

}
