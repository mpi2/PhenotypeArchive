package uk.ac.ebi.phenotype.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;

import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import uk.ac.ebi.phenotype.dao.PhenotypePipelineDAO;

@Service
public class ReportsService {

    @Autowired
	StatisticalResultService srService;

    @Autowired
	@Qualifier("postqcService")
    PostQcService gpService;

    @Autowired
    MpService mpService;
    
    @Autowired
    private PhenotypePipelineDAO pipelineDao;
    
    private static 
	ArrayList<String> resources;
    
    public ReportsService(){
    	resources = new ArrayList<>();
    	resources.add("IMPC");
    	resources.add("3i");
    }
    

    public List<List<String[]>> getHitsPerLine(){
    	//Columns:
    	//	parameter name | parameter stable id | number of significant hits

    	List<List<String[]>> res = new ArrayList<>();
    	try {
    		List<String[]> parameters = new ArrayList<>();
    		String [] headerParams  ={"Parameter Id", "Parameter Name", "# significant hits"};
    		parameters.add(headerParams);
    		parameters.addAll(gpService.getHitsDistributionByParameter(resources));

    		List<String[]> procedures = new ArrayList<>();
    		String [] headerProcedures  ={"Procedure Id", "Procedure Name", "# significant hits"};
    		procedures.add(headerProcedures);
    		procedures.addAll(gpService.getHitsDistributionByProcedure(resources));
    		
			res.add(parameters);
			res.add(procedures);
			
		} catch (SolrServerException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
    	return res;
    	
    }
    
    
	
    public List<List<String[]>> getHitsPerParamProcedure(){
    	//Columns:
    	//	parameter name | parameter stable id | number of significant hits

    	List<List<String[]>> res = new ArrayList<>();
    	try {
    		List<String[]> parameters = new ArrayList<>();
    		String [] headerParams  ={"Parameter Id", "Parameter Name", "# significant hits"};
    		parameters.add(headerParams);
    		parameters.addAll(gpService.getHitsDistributionByParameter(resources));

    		List<String[]> procedures = new ArrayList<>();
    		String [] headerProcedures  ={"Procedure Id", "Procedure Name", "# significant hits"};
    		procedures.add(headerProcedures);
    		procedures.addAll(gpService.getHitsDistributionByProcedure(resources));
    		
			res.add(parameters);
			res.add(procedures);
			
		} catch (SolrServerException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
    	return res;
    	
    }
    
    
    public List<List<String[]>> getMpCallDistribution(){
    	
    	Float pVal = (float) 0.0001;
    	TreeMap<String, Long> significant = srService.getDistributionOfAnnotationsByMPTopLevel(resources, pVal);
    	TreeMap<String, Long> all = new TreeMap<String, Long>(String.CASE_INSENSITIVE_ORDER);
    	all.putAll(srService.getDistributionOfAnnotationsByMPTopLevel(resources, null));
    	List<List<String[]>> res = new ArrayList<>();
    	List<String[]> table = new ArrayList<>();
    	String[] header = new String[4];
    	header[0] = "Top Level MP Term";
    	header[1] = "No. Significant Calls"; 
    	header[2] = "No. Not Significant Calls";  
    	header[3] = "% Significant Calls";   	
    	table.add(header);
    	
    	for (String mp : all.keySet()){
	   		if (!mp.equalsIgnoreCase("reproductive system phenotype")){ // line data is not in statistical result core yet
	    		String[] row = new String[4];
	    		row[0] = mp;
	    		Long sign = (long) 0;
	    		if (significant.containsKey(mp)){
	    			sign = significant.get(mp);
	    		}
	    		row[1] = sign.toString();
	    		Long notSignificant = all.get(mp) - sign;
	    		row[2] = notSignificant.toString();
	    		Float percentage =  100 * ((float)sign / (float)all.get(mp)); 
	    		row[3] = (percentage.toString());
	    		table.add(row);
	   		}
    	}

    	res.add(new ArrayList<>(table));
    	
    	table = new ArrayList<>();
    	String[] headerLines = new String[4];
    	headerLines[0] = "Top Level MP Term";
    	headerLines[1] = "Lines Associated"; 
    	headerLines[2] = "Lines Tested";    
    	headerLines[3] = "% Lines Associated";    	
    	table.add(headerLines); 	
    
    	try {
    		Map<String, ArrayList<String>> genesSignificantMp = srService.getDistributionOfLinesByMPTopLevel(resources, pVal);
    		TreeMap<String, ArrayList<String>> genesAllMp = new TreeMap<String, ArrayList<String>>(String.CASE_INSENSITIVE_ORDER);
    		genesAllMp.putAll(srService.getDistributionOfLinesByMPTopLevel(resources, null));
		
		   	for (String mp : genesAllMp.keySet()){
		   		if (!mp.equalsIgnoreCase("reproductive system phenotype")){
			   		String[] row = new String[4];
		    		row[0] = mp;
		    		int sign = 0;
		    		if (genesSignificantMp.containsKey(mp)){
		    			sign = genesSignificantMp.get(mp).size();
		    		}
		    		row[1] = Integer.toString(sign);
		    		row[2] = Integer.toString(genesAllMp.get(mp).size());
		    		Float percentage =  100 * ((float)sign / (float)genesAllMp.get(mp).size()); 
		    		row[3] = (percentage.toString());
		    		table.add(row);
		   		}
	    	}
    	} catch (SolrServerException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}

    	res.add(new ArrayList<>(table));
    	
    	table = new ArrayList<>();
    	String[] headerGenes = new String[4];
    	headerGenes[0] = "Top Level MP Term";
    	headerGenes[1] = "Genes Associated"; 
    	headerGenes[2] = "Genes Tested";    
    	headerGenes[3] = "% Associated";    	
    	table.add(headerGenes); 	
    
    	try {
    		Map<String, ArrayList<String>> genesSignificantMp = srService.getDistributionOfGenesByMPTopLevel(resources, pVal);
    		TreeMap<String, ArrayList<String>> genesAllMp = new TreeMap<String, ArrayList<String>>(String.CASE_INSENSITIVE_ORDER);
    		genesAllMp.putAll(srService.getDistributionOfGenesByMPTopLevel(resources, null));
		
		   	for (String mp : genesAllMp.keySet()){
		   		if (!mp.equalsIgnoreCase("reproductive system phenotype")){
			   		String[] row = new String[4];
		    		row[0] = mp;
		    		int sign = 0;
		    		if (genesSignificantMp.containsKey(mp)){
		    			sign = genesSignificantMp.get(mp).size();
		    		}
		    		row[1] = Integer.toString(sign);
		    		row[2] = Integer.toString(genesAllMp.get(mp).size());
		    		Float percentage =  100 * ((float)sign / (float)genesAllMp.get(mp).size()); 
		    		row[3] = (percentage.toString());
		    		table.add(row);
		   		}
	    	}
    	} catch (SolrServerException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
    	
    	res.add(new ArrayList<>(table));
    	
    	return res;
    }
    	
    

    /**
     *
     * @param mpTermId
     * @return List of all parameters that may lead to associations to the MP
     * term or any of it's children (based on the slim only)
     */
    public HashSet<String> getParameterStableIdsByPhenotypeAndChildren(String mpTermId) {
        HashSet<String> res = new HashSet<>();
        ArrayList<String> mpIds;
        try {
            mpIds = mpService.getChildrenFor(mpTermId);
            res.addAll(pipelineDao.getParameterStableIdsByPhenotypeTerm(mpTermId));
            for (String mp : mpIds) {
                res.addAll(pipelineDao.getParameterStableIdsByPhenotypeTerm(mp));
            }
        } catch (SolrServerException e) {
            e.printStackTrace();
        }
        return res;
    }
}
