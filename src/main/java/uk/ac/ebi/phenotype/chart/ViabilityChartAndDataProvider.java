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
public class ViabilityChartAndDataProvider {
	
	
	public ViabilityDTO doViabilityData(Parameter parameter, ViabilityDTO viabilityDTO) {
		//we need 3 sets of data for the 3 graphs
		Map<String, ObservationDTO> paramStableIdToObservation = viabilityDTO.getParamStableIdToObservation();
		List<ObservationDTO> totals=new ArrayList<>();
		totals.add(paramStableIdToObservation.get(ViabilityDTO.totalPupsWt));
		totals.add(paramStableIdToObservation.get(ViabilityDTO.totalPupsHom));
		totals.add(paramStableIdToObservation.get(ViabilityDTO.totalPupsHet));
		
		List<ObservationDTO> male=new ArrayList<>();
		male.add(paramStableIdToObservation.get(ViabilityDTO.totalMaleWt));
		male.add(paramStableIdToObservation.get(ViabilityDTO.totalMaleHom));
		male.add(paramStableIdToObservation.get(ViabilityDTO.totalMaleHet));
		
		
		List<ObservationDTO> female=new ArrayList<>();
		
	
		female.add(paramStableIdToObservation.get(ViabilityDTO.totalFemaleWt));
		female.add(paramStableIdToObservation.get(ViabilityDTO.totalFemaleHom));
		female.add(paramStableIdToObservation.get(ViabilityDTO.totalFemaleHet));
		
		Map<String, Integer> totalLabelToNumber = new LinkedHashMap<>();
		for(ObservationDTO ob:totals){
			if(Math.round(ob.getDataPoint())>0){
		totalLabelToNumber.put(WordUtils.capitalize(ob.getParameterName()), Math.round(ob.getDataPoint()));
			}
		}
		String totalChart = PieChartCreator.getPieChart(totalLabelToNumber, "totalChart", "Total Counts (Male and Female)", ChartColors.getZygosityColorMap());
		viabilityDTO.setTotalChart(totalChart);
		
		Map<String, Integer> maleLabelToNumber = new LinkedHashMap<>();
		for(ObservationDTO ob:male){
			if(Math.round(ob.getDataPoint())>0){
			maleLabelToNumber.put(WordUtils.capitalize(ob.getParameterName()), Math.round(ob.getDataPoint()));
			}
		}
		Map<String, Integer> femaleLabelToNumber = new LinkedHashMap<>();
		String maleChart = PieChartCreator.getPieChart(maleLabelToNumber, "maleChart", "Male Counts", ChartColors.getZygosityColorMap());
		viabilityDTO.setMaleChart(maleChart);
		
		for(ObservationDTO ob:female){
			if(Math.round(ob.getDataPoint())>0){
			femaleLabelToNumber.put(WordUtils.capitalize(ob.getParameterName()), Math.round(ob.getDataPoint()));
			}
		}
		String femaleChart = PieChartCreator.getPieChart(femaleLabelToNumber, "femaleChart", "Female Counts", ChartColors.getZygosityColorMap());
		viabilityDTO.setFemaleChart(femaleChart);
		return viabilityDTO;
	}

}
