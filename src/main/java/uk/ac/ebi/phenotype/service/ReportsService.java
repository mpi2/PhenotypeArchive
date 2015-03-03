package uk.ac.ebi.phenotype.service;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReportsService {

    @Autowired
	StatisticalResultService srService;
    
    public List<String[]> getMpCallDistribution(){
    	
    	ArrayList<String> resources = new ArrayList<>();
    	resources.add("IMPC");
    	resources.add("3i");
    	Float pVal = (float) 0.001;
    	TreeMap<String, Long> significant = srService.getDistributionOfAnnotationsByMPTopLevel(resources, pVal);
    	TreeMap<String, Long> all = srService.getDistributionOfAnnotationsByMPTopLevel(resources, null);
    	List<String[]> res = new ArrayList<>();
    	String[] header = new String[3];
    	header[0] = "Top Level MP Term";
    	header[1] = " No. Significant Calls"; 
    	header[2] = " No. Not Significant Calls";   	
    	res.add(header);
    	
    	for (String mp : all.keySet()){
    		String[] row = new String[3];
    		row[0] = mp;
    		Long sign = (long) 0;
    		if (significant.containsKey(mp)){
    			sign = significant.get(mp);
    		}
    		row[1] = sign.toString();
    		Long notSignificant = all.get(mp) - sign;
    		row[2] = notSignificant.toString();
    		res.add(row);
    	}
    	
    	return res;
    }
    	
}
