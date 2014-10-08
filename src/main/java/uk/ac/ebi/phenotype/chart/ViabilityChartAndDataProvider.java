package uk.ac.ebi.phenotype.chart;

import org.springframework.stereotype.Service;

import uk.ac.ebi.phenotype.pojo.Parameter;
import uk.ac.ebi.phenotype.service.dto.ExperimentDTO;

@Service
public class ViabilityChartAndDataProvider {

	public ViabilityDTO doViabilityData(Parameter parameter, ExperimentDTO experiment) {

		System.out.println("calling do viabiltyData!");
		return new ViabilityDTO();
	}

}
