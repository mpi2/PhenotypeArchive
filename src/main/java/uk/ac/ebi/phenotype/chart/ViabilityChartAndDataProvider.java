package uk.ac.ebi.phenotype.chart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.WordUtils;
import org.springframework.stereotype.Service;

import uk.ac.ebi.phenotype.pojo.Parameter;
import uk.ac.ebi.phenotype.service.dto.ExperimentDTO;
import uk.ac.ebi.phenotype.service.dto.ObservationDTO;

@Service
public class ViabilityChartAndDataProvider {
	
	
	public ViabilityDTO doViabilityData(Parameter parameter, ViabilityDTO viabilityDTO) {
		System.out.println("calling do viabiltyData!");
		//we need 3 sets of data for the 3 graphs
		Map<String, ObservationDTO> paramStableIdToObservation = viabilityDTO.getParamStableIdToObservation();
		List<ObservationDTO> totals=new ArrayList<>();
		totals.add(paramStableIdToObservation.get(ViabilityDTO.totalPupsWt));
		totals.add(paramStableIdToObservation.get(ViabilityDTO.totalPupsHet));
		totals.add(paramStableIdToObservation.get(ViabilityDTO.totalPupsHom));
		
		List<ObservationDTO> male=new ArrayList<>();
		male.add(paramStableIdToObservation.get(ViabilityDTO.totalMaleHet));
		male.add(paramStableIdToObservation.get(ViabilityDTO.totalMaleHom));
		male.add(paramStableIdToObservation.get(ViabilityDTO.totalMaleWt));
		
		List<ObservationDTO> female=new ArrayList<>();
		female.add(paramStableIdToObservation.get(ViabilityDTO.totalFemaleHet));
		female.add(paramStableIdToObservation.get(ViabilityDTO.totalFemaleHom));
		female.add(paramStableIdToObservation.get(ViabilityDTO.totalFemaleWt));
		
		Map<String, Integer> totalLabelToNumber = new HashMap<>();
		for(ObservationDTO ob:totals){
		totalLabelToNumber.put(WordUtils.capitalize(ob.getParameterName()), Math.round(ob.getDataPoint()));
		}
		String totalChart = PieChartCreator.getPieChart(totalLabelToNumber, "totalChart", "Total Counts (Male and Female)");
		viabilityDTO.setTotalChart(totalChart);
		
		Map<String, Integer> maleLabelToNumber = new HashMap<>();
		for(ObservationDTO ob:male){
			maleLabelToNumber.put(WordUtils.capitalize(ob.getParameterName()), Math.round(ob.getDataPoint()));
		}
		Map<String, Integer> femaleLabelToNumber = new HashMap<>();
		String maleChart = PieChartCreator.getPieChart(maleLabelToNumber, "maleChart", "Male Counts");
		viabilityDTO.setMaleChart(maleChart);
		
		for(ObservationDTO ob:female){
			femaleLabelToNumber.put(WordUtils.capitalize(ob.getParameterName()), Math.round(ob.getDataPoint()));
		}
		String femaleChart = PieChartCreator.getPieChart(femaleLabelToNumber, "femaleChart", "Female Counts");
		viabilityDTO.setFemaleChart(femaleChart);
		return viabilityDTO;
	}

}
