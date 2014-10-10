package uk.ac.ebi.phenotype.chart;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import uk.ac.ebi.phenotype.pojo.Parameter;
import uk.ac.ebi.phenotype.service.dto.ExperimentDTO;

@Service
public class ViabilityChartAndDataProvider {

	
	public ViabilityDTO doViabilityData(Parameter parameter, ViabilityDTO viabilityDTO) {
		System.out.println("calling do viabiltyData!");
		Map<String, Integer> totalLabelToNumber = new HashMap<>();
		totalLabelToNumber.put("WT", 10);
		totalLabelToNumber.put("HET", 10);
		totalLabelToNumber.put("HOM", 20);
		String totalChart = PieChartCreator.getPieChart(totalLabelToNumber, "totalChart", "Total Counts (male and female)");
		viabilityDTO.setTotalChart(totalChart);
		
		Map<String, Integer> maleLabelToNumber = new HashMap<>();
		maleLabelToNumber.put("WT", 10);
		maleLabelToNumber.put("HET", 10);
		maleLabelToNumber.put("HOM", 20);
		String maleChart = PieChartCreator.getPieChart(maleLabelToNumber, "maleChart", "Male Counts");
		viabilityDTO.setMaleChart(maleChart);
		
		Map<String, Integer> femaleLabelToNumber = new HashMap<>();
		femaleLabelToNumber.put("WT", 10);
		femaleLabelToNumber.put("HET", 10);
		femaleLabelToNumber.put("HOM", 20);
		String femaleChart = PieChartCreator.getPieChart(femaleLabelToNumber, "femaleChart", "Female Counts");
		viabilityDTO.setFemaleChart(femaleChart);
		return viabilityDTO;
	}

}
