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

	final static String totalPupsWt="IMPC_VIA_004_001";
	final static String totalPupsHom="IMPC_VIA_006_001";
	final static String totalPupsHet="IMPC_VIA_005_001";
	final static String totalMalePups="IMPC_VIA_010_001";// Total Male Pups value=96.0
	final static String totalFemalePups="IMPC_VIA_014_001";// Total Female Pups value=105.0
	final static String totalMaleHom="IMPC_VIA_009_001";// Total Male Homozygous value=2.0
	final static String totalFemaleHet="IMPC_VIA_012_001";// Total Female Heterozygous value=56.0
	final static String totalMaleHet="IMPC_VIA_008_001";// Total Male Heterozygous value=48.0
	final static String totalFemaleWt="IMPC_VIA_011_001";// Total Female WT value=46.0
	final static String totalMaleWt="IMPC_VIA_007_001";// Total Male WT value=46.0
	final static String totalFemaleHom="IMPC_VIA_013_001";// Total Female Homozygous value=3.0
	
	public ViabilityDTO doViabilityData(Parameter parameter, ViabilityDTO viabilityDTO) {
		System.out.println("calling do viabiltyData!");
		//we need 3 sets of data for the 3 graphs
		Map<String, ObservationDTO> paramStableIdToObservation = viabilityDTO.getParamStableIdToObservation();
		List<ObservationDTO> totals=new ArrayList<>();
		totals.add(paramStableIdToObservation.get(totalPupsWt));
		totals.add(paramStableIdToObservation.get(totalPupsHet));
		totals.add(paramStableIdToObservation.get(totalPupsHom));
		
		List<ObservationDTO> male=new ArrayList<>();
		male.add(paramStableIdToObservation.get(totalMaleHet));
		male.add(paramStableIdToObservation.get(totalMaleHom));
		male.add(paramStableIdToObservation.get(totalMaleWt));
		
		List<ObservationDTO> female=new ArrayList<>();
		female.add(paramStableIdToObservation.get(totalFemaleHet));
		female.add(paramStableIdToObservation.get(totalFemaleHom));
		female.add(paramStableIdToObservation.get(totalFemaleWt));
		
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
