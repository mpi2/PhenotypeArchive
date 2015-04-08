package uk.ac.ebi.phenotype.chart;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import uk.ac.ebi.phenotype.service.ExperimentService;


public class BodyWeightChartAndTableProvider {
	@Autowired 
	ExperimentService es;
		
	
	
	
	public String getDerivedParameter(String parameter){
		
		List<String> eslim702 = Arrays.asList("ESLIM_009_001_003", "ESLIM_010_001_003", "ESLIM_011_001_011", "ESLIM_012_001_005", "ESLIM_013_001_018", "ESLIM_022_001_001");
		List<String> eslim701 = Arrays.asList("ESLIM_001_001_001", "ESLIM_002_001_001", "ESLIM_003_001_001", "ESLIM_004_001_001", "ESLIM_005_001_001", "ESLIM_020_001_001", "ESLIM_022_001_001");
		List<String> impcBwt = Arrays.asList("IMPC_GRS_003_001", "IMPC_CAL_001_001", "IMPC_DXA_001_001", "IMPC_HWT_007_001", "IMPC_PAT_049_001", "IMPC_BWT_001_001", "IMPC_ABR_001_001", "IMPC_CHL_001_001", "TCP_CHL_001_001", "HMGU_ROT_004_001");
				
		if (eslim702.contains(parameter)){
			return "ESLIM_022_001_702";
		} else if (eslim701.contains(parameter)){
			return "ESLIM_022_001_701";
		} else if (impcBwt.contains(parameter)){
			return "IMPC_BWT_008_001";
		}
		
		return parameter;
	}
}
