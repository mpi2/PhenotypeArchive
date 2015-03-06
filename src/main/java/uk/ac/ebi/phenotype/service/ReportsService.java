package uk.ac.ebi.phenotype.service;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uk.ac.ebi.phenotype.dao.AnalyticsDAO;
import uk.ac.ebi.phenotype.dao.PhenotypePipelineDAO;
import uk.ac.ebi.phenotype.pojo.ZygosityType;
import uk.ac.ebi.phenotype.service.dto.GenotypePhenotypeDTO;
import uk.ac.ebi.phenotype.service.dto.ObservationDTO;

import java.util.*;
import java.util.concurrent.ExecutionException;

@Service
public class ReportsService {

    @Autowired
	StatisticalResultService srService;

    @Autowired
	ObservationService oService;
    
    @Autowired
	ImageService iService;

    @Autowired
	@Qualifier("postqcService")
    PostQcService gpService;

    @Autowired
    MpService mpService;
    
    @Autowired
    private PhenotypePipelineDAO pipelineDao;
    
	@Autowired
	private AnalyticsDAO analyticsDAO;
    
	private static 
	ArrayList<String> resources;
    
    public ReportsService(){
    	resources = new ArrayList<>();
    	resources.add("IMPC");
    	resources.add("3i");
    }

	public static final String MALE_FERTILITY_PARAMETER = "IMPC_FER_001_001";
	public static final String FEMALE_FERTILITY_PARAMETER = "IMPC_FER_019_001";

	/**
	 * Generate the report for fertility data
	 *   Fertile, Infertile, male & female infertiliy
	 *
	 * @return list of list of strings intended to be transformed into a delimited file for download
	 */
	public List<String[]> getFertilityData() throws SolrServerException {

		List<String[]> report = new ArrayList<>();

		try {
			List<String> ALLOWED_DATASOURCES = Arrays.asList("IMPC", "3i");


			List<ObservationDTO> results;
			Map<String, Set<String>> maleColonies = new HashMap<>();
			Map<String, Set<String>> femaleColonies = new HashMap<>();
			Map<String, Set<String>> bothColonies = new HashMap<>();

			Map<String, Set<String>> maleGenes = new HashMap<>();
			Map<String, Set<String>> femaleGenes = new HashMap<>();
			Map<String, Set<String>> bothGenes = new HashMap<>();

			maleColonies.put("Fertile", new HashSet<String>());
			maleColonies.put("Infertile", new HashSet<String>());
			femaleColonies.put("Fertile", new HashSet<String>());
			femaleColonies.put("Infertile", new HashSet<String>());
			bothColonies.put("Fertile", new HashSet<String>());
			bothColonies.put("Infertile", new HashSet<String>());

			maleGenes.put("Fertile", new HashSet<String>());
			maleGenes.put("Infertile", new HashSet<String>());
			femaleGenes.put("Fertile", new HashSet<String>());
			femaleGenes.put("Infertile", new HashSet<String>());
			bothGenes.put("Fertile", new HashSet<String>());
			bothGenes.put("Infertile", new HashSet<String>());


			results = oService.getObservationsByParameterStableId(MALE_FERTILITY_PARAMETER);
			for (ObservationDTO result : results) {
				if (ALLOWED_DATASOURCES.contains(result.getDataSourceName())) {
					String key = result.getCategory();
					maleColonies.get(key).add(result.getColonyId());
					maleGenes.get(key).add(result.getGeneSymbol());
				}
			}

			results = oService.getObservationsByParameterStableId(FEMALE_FERTILITY_PARAMETER);
			for (ObservationDTO result : results) {
				if (ALLOWED_DATASOURCES.contains(result.getDataSourceName())) {
					String key = result.getCategory();
					femaleColonies.get(key).add(result.getColonyId());
					femaleGenes.get(key).add(result.getGeneSymbol());
				}
			}


			bothColonies.put("Fertile", new HashSet<>(femaleColonies.get("Fertile")));
			bothColonies.put("Infertile", new HashSet<>(femaleColonies.get("Infertile")));
			bothGenes.put("Infertile", new HashSet<>(femaleGenes.get("Infertile")));
			bothGenes.put("Infertile", new HashSet<>(femaleGenes.get("Infertile")));

			bothColonies.get("Fertile").retainAll(maleColonies.get("Fertile"));
			bothColonies.get("Infertile").retainAll(maleColonies.get("Infertile"));
			bothGenes.get("Fertile").retainAll(maleGenes.get("Fertile"));
			bothGenes.get("Infertile").retainAll(maleGenes.get("Infertile"));

			report.add(Arrays.asList("Sex", "IMPC/3i Line count", "IMPC/3i Gene count", "IMPC/3i Gene Symbols").toArray(new String[4]));
			report.add(Arrays.asList("Both infertile", Integer.toString(bothColonies.get("Infertile").size()), Integer.toString(bothGenes.get("Infertile").size()), StringUtils.join(bothGenes.get("Infertile"), ";")).toArray(new String[4]));
			report.add(Arrays.asList("Both fertile", Integer.toString(bothColonies.get("Fertile").size()), Integer.toString(bothGenes.get("Fertile").size()), "").toArray(new String[4]));
			report.add(Arrays.asList("Males infertile", Integer.toString(maleColonies.get("Infertile").size()), Integer.toString(maleGenes.get("Infertile").size()), StringUtils.join(maleGenes.get("Infertile"), ";")).toArray(new String[4]));
			report.add(Arrays.asList("Males fertile", Integer.toString(maleColonies.get("Fertile").size()), Integer.toString(bothGenes.get("Fertile").size()), "").toArray(new String[4]));
			report.add(Arrays.asList("Females infertile", Integer.toString(femaleColonies.get("Infertile").size()), Integer.toString(femaleGenes.get("Infertile").size()), StringUtils.join(femaleGenes.get("Infertile"), ";")).toArray(new String[4]));
			report.add(Arrays.asList("Females fertile", Integer.toString(femaleColonies.get("Fertile").size()), Integer.toString(bothGenes.get("Fertile").size()), "").toArray(new String[4]));

		}catch (Exception e) {
			e.printStackTrace();
		}

		return report;
	}



    public List<List<String[]>> getViabilityReport(){

    	List<List<String[]>> res = new ArrayList<>();
    	List<String[]> allTable = new ArrayList<>();
    	List<String[]> countsTable = new ArrayList<>();
    	HashMap<String, Integer> countsByCategory = new HashMap<>();
    	
    	try {
    		QueryResponse response = oService.getViabilityData();
    		String[] header = {"Gene", "Colony", "Category"};
    		allTable.add(header);
    		for ( SolrDocument doc : response.getResults()){
    			String category = doc.getFieldValue(ObservationDTO.CATEGORY).toString();
    			String[] row = {(doc.getFieldValue(ObservationDTO.GENE_SYMBOL) != null) ? doc.getFieldValue(ObservationDTO.GENE_SYMBOL).toString() : "",
    				doc.getFieldValue(ObservationDTO.COLONY_ID).toString(), category};
    			allTable.add(row);
    			if (countsByCategory.containsKey(category)){
    				countsByCategory.put(category, countsByCategory.get(category) + 1);
    			}else {
    				countsByCategory.put(category, 1);
    			}
    			
    		}
      		
      		for (String cat: countsByCategory.keySet()){
      			String[] row = {cat, countsByCategory.get(cat).toString()};
      			countsTable.add(row);
      		}

      		res.add(countsTable);
      		res.add(allTable);
		
		} catch (SolrServerException e) {
			e.printStackTrace();
		}
    	return res;
    }


	public List<List<String[]>> getDataOverview(){
		// Lines phenotyped	with data pass QC	+
		// broken out by center and total
		// Phenotype hits	+
		// Datapoints
		// Images
      	
    	List<List<String[]>> res = new ArrayList<>();
    	List<String[]> overview = new ArrayList<>();
		String[] forArrayType = new String[0];
    	
		Map<String, String> metaInfo = analyticsDAO.getMetaData();
		List<String> row = new ArrayList<>();
		row.add("# phenotyped genes");
		row.add(metaInfo.get("phenotyped_genes"));
    	overview.add(row.toArray(forArrayType));
    	
    	row = new ArrayList<>();
		row.add("# phenotyped lines");
		row.add(metaInfo.get("phenotyped_lines"));
    	overview.add(row.toArray(forArrayType));
    
    	row = new ArrayList<>();
		row.add("# phenotype hits");
		row.add(metaInfo.get("statistically_significant_calls"));
    	overview.add(row.toArray(forArrayType));
    	
		try {
	    	row = new ArrayList<>();
			row.add("# data points");
			row.add(Long.toString(oService.getNumberOfDocuments()));
	    	overview.add(row.toArray(forArrayType));
	    
	    	row = new ArrayList<>();
			row.add("# images");
			row.add(Long.toString(iService.getNumberOfDocuments()));
	    	overview.add(row.toArray(forArrayType));
	       	
		} catch (SolrServerException e) {
			e.printStackTrace();
		}

    	res.add(overview);
    	
    	List<String[]> lines = new ArrayList<>();

		String centers = metaInfo.get("phenotyped_lines_centers");
		String[] phenotypingCenters = centers.split(",");
		String[] values = new String[phenotypingCenters.length]; 
		for (int i = 0; i < phenotypingCenters.length; i++){
			values[i] = metaInfo.get("phenotyped_lines_" + phenotypingCenters[i]);
			phenotypingCenters[i] = phenotypingCenters[i] + " lines";
 		}
		
    	lines.add(phenotypingCenters);
    	lines.add(values);
    	
    	res.add(lines);
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
        
	
    public List<List<String[]>> getHitsPerLine(){
   
    	// TODO refactor to pivot facet on zygosity, colony_id (this order) => 1 call instead of 2
      	//Columns:		parameter name | parameter stable id | number of significant hits

    	List<List<String[]>> res = new ArrayList<>();
    	try {
    		List<String[]> zygosityTable = new ArrayList<>();
    		String [] headerParams  ={"# hits", "# colonies with this many HOM hits", "# colonies with this many HET hits", "# colonies with this many calls"};
    		zygosityTable.add(headerParams);

    		Map<String, Long> homsMap = gpService.getHitsDistributionBySomethingNoIds(GenotypePhenotypeDTO.COLONY_ID, resources, ZygosityType.homozygote, 1, srService.P_VALUE_THRESHOLD);
    		Map<String, Long> hetsMap = gpService.getHitsDistributionBySomethingNoIds(GenotypePhenotypeDTO.COLONY_ID, resources, ZygosityType.heterozygote, 1, srService.P_VALUE_THRESHOLD);
    		Map<String, Long> allMap = gpService.getHitsDistributionBySomethingNoIds(GenotypePhenotypeDTO.COLONY_ID, resources, null, 1, srService.P_VALUE_THRESHOLD);
         		
    		Map<String, Long> homsNoHits = srService.getColoniesNoMPHit(resources, ZygosityType.homozygote);
    		Map<String, Long> hetsNoHits = srService.getColoniesNoMPHit(resources, ZygosityType.heterozygote);
    		Map<String, Long> allNoHits = srService.getColoniesNoMPHit(resources, null);
    		
    		HashMap<Long, Integer> homRes = new HashMap<>();
    		HashMap<Long, Integer> hetRes = new HashMap<>();   
    		HashMap<Long, Integer> allRes = new HashMap<>();   
    		
    		long maxHitsPerColony = 0;
    		
    		for (String colony: homsMap.keySet()){
    			if (homsNoHits.containsKey(colony)){
    				homsNoHits.remove(colony);
    			}
    			long count = homsMap.get(colony);
    			if (homRes.containsKey(count)){
    				homRes.put(count, homRes.get(count) + 1);
    				if (count > maxHitsPerColony){
    					maxHitsPerColony = count;
    				}
    			} else {
    				homRes.put(count, 1);
    			}
    		}
    		for (String colony: hetsMap.keySet()){
    			if (hetsNoHits.containsKey(colony)){
    				hetsNoHits.remove(colony);
    			}
    			long count = hetsMap.get(colony);
    			if (hetRes.containsKey(count)){
    				hetRes.put(count, hetRes.get(count) + 1);
    				if (count > maxHitsPerColony){
    					maxHitsPerColony = count;
    				}
    			} else {
    				hetRes.put(count, 1);
    			}
    		}
    		for (String colony: allMap.keySet()){
    			if (allNoHits.containsKey(colony)){
    				allNoHits.remove(colony);
    			}
    			long count = allMap.get(colony);
    			if (allRes.containsKey(count)){
    				allRes.put(count, allRes.get(count) + 1);
    				if (count > maxHitsPerColony){
    					maxHitsPerColony = count;
    				}
    			} else {
    				allRes.put(count, 1);
    			}
    		}

    		homRes.put(Long.parseLong("0"), homsNoHits.size());
    		hetRes.put(Long.parseLong("0"), hetsNoHits.size());
    		allRes.put(Long.parseLong("0"), allNoHits.size());
    		
    		long iterator = 0;
    		
    		while (iterator <= maxHitsPerColony){
    			String[] row = {Long.toString(iterator), Long.toString(homRes.containsKey(iterator)? homRes.get(iterator) : 0),  
    				Long.toString(hetRes.containsKey(iterator)? hetRes.get(iterator) : 0), Long.toString(allRes.containsKey(iterator)? allRes.get(iterator) : 0)};
    			zygosityTable.add(row);
    			iterator += 1;
    		}
    		
			res.add(zygosityTable);
			
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
