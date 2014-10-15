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
		List<ObservationDTO> totals=new ArrayList<>();
		List<ObservationDTO> male=new ArrayList<>();
		List<ObservationDTO> female=new ArrayList<>();
		//loop over the keys
		Set<Entry<String, ObservationDTO>> entrySet = viabilityDTO.getParamNameToDataPoint().entrySet();
		for(Entry<String, ObservationDTO> entry:entrySet){
			String paramName=entry.getValue().getParameterName();
			System.out.println("entry="+entry.getKey()+" "+WordUtils.capitalize(entry.getValue().getParameterName())+" value="+entry.getValue().getDataPoint());
			if(paramName.contains("pups")){
				totals.add(entry.getValue());
			}
			if(paramName.contains(" male ") && !paramName.contains(" total ")){
				male.add(entry.getValue());
			}
			if(paramName.contains("female") && !paramName.contains(" total ")){
				female.add(entry.getValue());
			}
		}
		Map<String, ObservationDTO> labelToDataPoint = viabilityDTO.getParamNameToDataPoint();
		labelToDataPoint.keySet();
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
