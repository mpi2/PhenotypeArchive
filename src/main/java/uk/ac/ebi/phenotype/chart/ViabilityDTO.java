package uk.ac.ebi.phenotype.chart;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.ac.ebi.phenotype.service.dto.ObservationDTO;

public class ViabilityDTO {
	final static String totalPups="IMPC_VIA_003_001";
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
	
	Map<String, ObservationDTO> paramStableIdToObservation= new HashMap<>();

	
	public Map<String, ObservationDTO> getParamStableIdToObservation() {
	
		return paramStableIdToObservation;
	}


	
	public void setParamStableIdToObservation(Map<String, ObservationDTO> paramStableIdToObservation) {
	
		this.paramStableIdToObservation = paramStableIdToObservation;
	}


	private String totalChart = "";
	private String maleChart = "";
	private String femaleChart = "";
	String category = "";// should get set to e.g. Homozygous - Viable




	public String getCategory() {

		return category;
	}


	public void setCategory(String category) {

		this.category = category;
	}


	public String getTotalChart() {

		return totalChart;
	}


	public String getMaleChart() {

		return maleChart;
	}


	public String getFemaleChart() {

		return femaleChart;
	}


	public void setTotalChart(String totalChart) {

		this.totalChart = totalChart;
	}


	public void setMaleChart(String maleChart) {

		this.maleChart = maleChart;
	}


	public void setFemaleChart(String femaleChart) {

		this.femaleChart = femaleChart;
	}

}
