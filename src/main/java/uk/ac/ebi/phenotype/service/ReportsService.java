package uk.ac.ebi.phenotype.service;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uk.ac.ebi.phenotype.dao.PhenotypePipelineDAO;
import uk.ac.ebi.phenotype.pojo.SexType;
import uk.ac.ebi.phenotype.pojo.ZygosityType;
import uk.ac.ebi.phenotype.service.dto.ExperimentDTO;
import uk.ac.ebi.phenotype.service.dto.GeneDTO;
import uk.ac.ebi.phenotype.service.dto.GenotypePhenotypeDTO;
import uk.ac.ebi.phenotype.service.dto.ObservationDTO;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.ExecutionException;

@Service
public class ReportsService {

	@Autowired
	StatisticalResultService srService;

    @Autowired
	ObservationService oService;

	@Autowired
	GeneService geneService;
    
    @Autowired
	ExperimentService experimentService;
    
    @Autowired
	ImageService iService;

    @Autowired
	@Qualifier("postqcService")
    PostQcService gpService;

    @Autowired
    MpService mpService;
    
    @Autowired
    private PhenotypePipelineDAO pipelineDao;

	@Resource(name = "globalConfiguration")
	Map<String, String> config;
    
	private static 
	ArrayList<String> resources;

	public static final String MALE_FERTILITY_PARAMETER = "IMPC_FER_001_001";
	public static final String FEMALE_FERTILITY_PARAMETER = "IMPC_FER_019_001";
	public static final String[] EMPTY_ROW = new String[]{""};


	public ReportsService(){
    	resources = new ArrayList<>();
    	resources.add("IMPC");
    	resources.add("3i");
    }
		
	public List<String[]> getBmdIpdttReport(String parameter)
	throws SolrServerException {

		Long time = System.currentTimeMillis();
		List<String[]> report = new ArrayList<>();
		String[] header = { "Gene", "Allele" , "Colony", "First date", "Last date", 
							"Mean WT Male", "Median WT Male", "SD WT Male", "N WT Male", 
							"Mean HOM Male", "Median HOM Male", "SD HOM Male", "N HOM Male", 
							"Mean HET Male", "Median HET Male", "SD HET Male", "N HET Male", 
							"Mean HEM Male", "Median HEM Male", "SD HEM Male", "N HEM Male", 
							"Mean WT Female", "Median WT Female", "SD WT Female", "N WT Female", 
							"Mean HOM Female", "Median HOM Female", "SD HOM Female", "N HOM Female",
							"Mean HET Female", "Median HET Female", "SD HET Female", "N HET Female" 
							};
		report.add(header);
		report.addAll(getBmpIpgttStats(oService.getDatapointsByColony(resources, parameter, "experimental")));
		System.out.println("Report generation took " + (System.currentTimeMillis() - time));
		return report;
	}
	

	public List<String[]> getBmpIpgttStats(List<Group> groups){
		
		List<String[]> rows = new ArrayList<>();

		try {
			for (Group group: groups) {
				IpGTTStats stats;
				stats = new IpGTTStats(group);
				
				String[] row = { stats.geneSymbol, stats.alleleSymbol, stats.colony, stats.firstDate, stats.lastDate,
						"" + stats.getMean(SexType.male, null), "" + stats.getMedian(SexType.male, null), "" + stats.getSD(SexType.male, null), "" + stats.getN(SexType.male, null),
						"" + stats.getMean(SexType.male, ZygosityType.homozygote), "" + stats.getMedian(SexType.male, ZygosityType.homozygote), "" + stats.getSD(SexType.male, ZygosityType.homozygote), "" + stats.getN(SexType.male, ZygosityType.homozygote),
						"" + stats.getMean(SexType.male, ZygosityType.heterozygote), "" + stats.getMedian(SexType.male, ZygosityType.heterozygote), "" + stats.getSD(SexType.male, ZygosityType.heterozygote), "" + stats.getN(SexType.male, ZygosityType.heterozygote),
						"" + stats.getMean(SexType.male, ZygosityType.hemizygote), "" + stats.getMedian(SexType.male, ZygosityType.hemizygote), "" + stats.getSD(SexType.male, ZygosityType.hemizygote), "" + stats.getN(SexType.male, ZygosityType.hemizygote),
						"" + stats.getMean(SexType.female, null), "" + stats.getMedian(SexType.female, null), "" + stats.getSD(SexType.female, null), "" + stats.getN(SexType.female, null),
						"" + stats.getMean(SexType.female, ZygosityType.homozygote), "" + stats.getMedian(SexType.female, ZygosityType.homozygote), "" + stats.getSD(SexType.female, ZygosityType.homozygote), "" + stats.getN(SexType.female, ZygosityType.homozygote),
						"" + stats.getMean(SexType.female, ZygosityType.heterozygote), "" + stats.getMedian(SexType.female, ZygosityType.heterozygote), "" + stats.getSD(SexType.female, ZygosityType.heterozygote), "" + stats.getN(SexType.female, ZygosityType.heterozygote),
				};
				rows.add(row);	
			}
        }catch (Exception e) {
        	e.printStackTrace();
		}
				
		return rows;
	}
	
	
	/**
	 * Generate the report for fertility data
	 *   Fertile, Infertile, male & female infertiliy
	 *
	 * @return list of list of strings intended to be transformed into a delimited file for download
	 */
	public List<String[]> getFertilityData() throws SolrServerException {

		List<String[]> report = new ArrayList<>();

		try {
			
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
				if (resources.contains(result.getDataSourceName())) {
					String key = result.getCategory();
					maleColonies.get(key).add(result.getColonyId());
					maleGenes.get(key).add(result.getGeneSymbol());
				}
			}

			results = oService.getObservationsByParameterStableId(FEMALE_FERTILITY_PARAMETER);
			for (ObservationDTO result : results) {
				if (resources.contains(result.getDataSourceName())) {
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
    	List<String[]> genesTable = new ArrayList<>();
    	HashMap<String, Integer> countsByCategory = new HashMap<>();
    	HashMap<String, HashSet<String>> genesByVia = new HashMap<>();
    	
    	try {
    		QueryResponse response = oService.getViabilityData(resources);
    		String[] header = {"Gene", "Colony", "Category"};
    		allTable.add(header);
    		for ( SolrDocument doc : response.getResults()){
    			String category = doc.getFieldValue(ObservationDTO.CATEGORY).toString();
    			HashSet genes = new HashSet<>();
    			String[] row = {(doc.getFieldValue(ObservationDTO.GENE_SYMBOL) != null) ? doc.getFieldValue(ObservationDTO.GENE_SYMBOL).toString() : "",
    				doc.getFieldValue(ObservationDTO.COLONY_ID).toString(), category};
    			allTable.add(row);
    			if (countsByCategory.containsKey(category)){
    				countsByCategory.put(category, countsByCategory.get(category) + 1);
    			}else {
    				countsByCategory.put(category, 1);
    			}
    			if (genesByVia.containsKey(category)){
    				genes = genesByVia.get(category);
    			}

			    if (doc.getFieldValue(ObservationDTO.GENE_SYMBOL) != null) {
				    genes.add(doc.getFieldValue(ObservationDTO.GENE_SYMBOL).toString());
			    } else {
				    System.out.println("  ERROR: Could not get solr document field gene_symbol for document: " + doc);
			    }
				genesByVia.put(category, genes);
    		}
      		
      		for (String cat: countsByCategory.keySet()){
      			String[] row = {cat, countsByCategory.get(cat).toString()};
      			countsTable.add(row);
      		}
      		
      		String[] genesHeader = {"Category", "# genes", "Genes"};
    		genesTable.add(genesHeader);
    		for (String cat : genesByVia.keySet()){
    			String[] row = {cat, "" + genesByVia.get(cat).size(), StringUtils.join(genesByVia.get(cat), ", ")};
    			genesTable.add(row);
    		}
      		
      		HashSet<String> conflicts = new HashSet<>();
      		for (String cat : genesByVia.keySet()){
      			for (String otherCat : genesByVia.keySet()){
      				if (!otherCat.equalsIgnoreCase(cat)){
      					Set<String> conflictingGenes = genesByVia.get(otherCat);
      					conflictingGenes.retainAll(genesByVia.get(cat));
      					conflicts.addAll(conflictingGenes);
      				}
      			}
      		}

		    genesTable.add(EMPTY_ROW);
      		String[] row = {"Conflicting", "" + conflicts.size(), StringUtils.join(conflicts, ", ")};
    		genesTable.add(row); 
    		String[] note = {"NOTE: Symbols in the conflicting list represent genes that are included in more than one viability category."};
    		genesTable.add(note);    		
    		
      		res.add(countsTable);
      		res.add(genesTable);
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
    
		try {
		
			List<String> row = new ArrayList<>();
			row.add("# phenotyped genes");
			row.add(Integer.toString(oService.getAllGeneIdsByResource(resources, false).size()));
    		overview.add(row.toArray(forArrayType));
			
	    	row = new ArrayList<>();
			row.add("# phenotyped mutant lines");
			row.add(Integer.toString(oService.getAllColonyIdsByResource(resources, true).size()));
	    	overview.add(row.toArray(forArrayType));
	    
	    	row = new ArrayList<>();
			row.add("# phenotype hits");
			row.add(Long.toString(gpService.getNumberOfDocuments(resources)));
	    	overview.add(row.toArray(forArrayType));
	    	
	    	row = new ArrayList<>();
			row.add("# data points");
			row.add(Long.toString(oService.getNumberOfDocuments(resources, false)));
	    	overview.add(row.toArray(forArrayType));
	    
	    	row = new ArrayList<>();
			row.add("# images");
			row.add(Long.toString(iService.getNumberOfDocuments(resources, false)));
	    	overview.add(row.toArray(forArrayType));
	       	
		} catch (SolrServerException e) {
			e.printStackTrace();
		}

		
    	res.add(overview);
    	
    	List<String[]> linesPerCenter = new ArrayList<>();
		try {
			Map<String, Set<String>> result = oService.getColoniesByPhenotypingCenter(resources, null);
			for (String center: result.keySet()){
				String[] row= {"# mutant lines phenotyped at " + center, Integer.toString(result.get(center).size())};
				linesPerCenter.add(row);
			}
			
		} catch (SolrServerException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		   	
    	res.add(linesPerCenter);


    	List<String> genesAll;
		List<String> genesComplete;
		List<String[]>  mpTable = new ArrayList<>();

    	try {

		    genesAll = oService.getGenesWithMoreProcedures(1, resources);
		    genesComplete = oService.getGenesWithMoreProcedures(13, resources);

		    // Process top level MP terms
		    String mpTopLevelGenePivot = GenotypePhenotypeDTO.TOP_LEVEL_MP_TERM_NAME + "," + GenotypePhenotypeDTO.MARKER_SYMBOL;
		    Map<String, List<String>> topLevelMpTermByGeneMapAll = gpService.getMpTermByGeneMap(genesAll, mpTopLevelGenePivot, resources);
		    Map<String, List<String>> topLevelMpTermByGeneMapComplete = gpService.getMpTermByGeneMap(genesComplete, mpTopLevelGenePivot, resources);

		    String[] headerTopLevel = {"Top Level MP term", "# associated genes with >= 1 procedure done", "% associated genes of all genes with >= 1 procedure done", "# associated genes with >= 13 procedures done", "% associated genes of all genes with >= 13 procedures done"};
		    mpTable.add(headerTopLevel);
		    for(String mpTerm : topLevelMpTermByGeneMapAll.keySet()) {
			    String[] row = {
				    mpTerm,
				    new Integer(topLevelMpTermByGeneMapAll.get(mpTerm).size()).toString(),
				    new Float((float) topLevelMpTermByGeneMapAll.get(mpTerm).size() / genesAll.size()*100)+"%",
				    new Integer(topLevelMpTermByGeneMapComplete.get(mpTerm).size()).toString(),
				    new Float((float) topLevelMpTermByGeneMapComplete.get(mpTerm).size() / genesComplete.size()*100)+"%"};
			    mpTable.add(row);
		    }

		    String[] emptyRow = {""};
		    mpTable.add(emptyRow);

		    // Process granular MP terms
		    String mpGenePivot = GenotypePhenotypeDTO.MP_TERM_NAME + "," + GenotypePhenotypeDTO.MARKER_SYMBOL;
		    Map<String, List<String>> mpTermByGeneMapAll = gpService.getMpTermByGeneMap(genesAll, mpGenePivot, resources);
		    Map<String, List<String>> mpTermByGeneMapComplete = gpService.getMpTermByGeneMap(genesComplete, mpGenePivot, resources);

		    String[] headerMp = {"MP term", "# associated genes with >= 1 procedure done", "% associated genes of all genes with >= 1 procedure done", "# associated genes with >= 13 procedures done", "% associated genes of all genes with >= 13 procedures done"};
		    mpTable.add(headerMp);
		    for(String mpTerm : mpTermByGeneMapAll.keySet()) {
			    String[] row = {
				    mpTerm,
				    new Integer(mpTermByGeneMapAll.get(mpTerm).size()).toString(),
				    new Float((float) mpTermByGeneMapAll.get(mpTerm).size() / genesAll.size()*100)+"%",
				    new Integer(mpTermByGeneMapComplete.get(mpTerm).size()).toString(),
				    new Float((float) mpTermByGeneMapComplete.get(mpTerm).size() / genesComplete.size()*100)+"%"};
			    mpTable.add(row);
		    }

		} catch (SolrServerException | InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
    	
		res.add(mpTable);

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
			
		} catch (SolrServerException | InterruptedException | ExecutionException e) {
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
    			if (allNoHits.containsKey(colony)){
    				allNoHits.remove(colony);
    			}
    			long count = homsMap.get(colony);
    			if (homRes.containsKey(count)){
    				homRes.put(count, homRes.get(count) + 1);
    			} else {
    				homRes.put(count, 1);
    				if (count > maxHitsPerColony){
    					maxHitsPerColony = count;
    				}
    			}
    		}
    		for (String colony: hetsMap.keySet()){
    			if (hetsNoHits.containsKey(colony)){
    				hetsNoHits.remove(colony);
    			}
    			if (allNoHits.containsKey(colony)){
    				allNoHits.remove(colony);
    			}
    			long count = hetsMap.get(colony);
    			if (hetRes.containsKey(count)){
    				hetRes.put(count, hetRes.get(count) + 1);
    			} else {
    				hetRes.put(count, 1);
    				if (count > maxHitsPerColony){
    					maxHitsPerColony = count;
    				}
    			}
    		}
    		int tempI = 0;
    		for (String colony: allMap.keySet()){
    			if (allNoHits.containsKey(colony)){
    				allNoHits.remove(colony);
    			}
    			long count = allMap.get(colony);
    			if (allRes.containsKey(count)){
        			tempI++;
    				allRes.put(count, allRes.get(count) + 1);
    			} else {
    				allRes.put(count, 1);
        			tempI++;
    				if (count > maxHitsPerColony){
    					maxHitsPerColony = count;
    				}
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

	    } catch (SolrServerException | InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}

    	return res;
    	
    }


	/**
	 * This will return a report of genes with counts of MP term associations.
	 * The count is
	 *   - Sexes are collapsed,
	 *     if both sexes have the same MP call, count once
	 *     if one sex has an MP call, but the other doesn't, count once
	 *     if one sex has an MP call, and the other has a different call, count twice
	 *
	 * @return list of string arrays for populating a CSV file
	 */
	public List<String[]> getHitsPerGene(){

		List<String[]> res = new ArrayList<>();
		String [] headerParams  ={"Marker symbol", "# phenotype hits", "phenotype hits"};
		res.add(headerParams);

		try {

			List<GenotypePhenotypeDTO> gps = gpService.getAllGenotypePhenotypes(resources);

			Map<String, Set<String>> geneToPhenotypes = new HashMap<>();

			for (GenotypePhenotypeDTO gp : gps) {

				// Exclude LacZ calls
				if(gp.getParameterStableId().contains("ALZ")) {
					continue;
				}

				if( ! geneToPhenotypes.containsKey(gp.getMarkerSymbol())) {
					geneToPhenotypes.put(gp.getMarkerSymbol(), new HashSet<String>());
				}

				geneToPhenotypes.get(gp.getMarkerSymbol()).add(gp.getMpTermName());
			}

			Set<String> allGenes = new HashSet<>(oService.getGenesWithMoreProcedures(1, resources));
			allGenes.removeAll(geneToPhenotypes.keySet());

			for (String geneSymbol : geneToPhenotypes.keySet()) {
				String [] row = {geneSymbol, Integer.toString(geneToPhenotypes.get(geneSymbol).size()), StringUtils.join(geneToPhenotypes.get(geneSymbol),": ")};
				res.add(row);
			}

			for (String geneSymbol : allGenes) {
				String [] row = {geneSymbol, "0", ""};
				res.add(row);
			}

		} catch (SolrServerException | InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}

		return res;

	}


	/**
	 * This will return a report of genes with MP term associations brokwn down by zygosity..  It will also list
	 * the viability call(s) for that gene.
	 *
	 * @return list of string arrays for populating a CSV file
	 */
	public List<String[]> getGeneByZygosity() {

		List<String[]> res = new ArrayList<>();
		Map<Pair<String, ZygosityType>, Set<String>> mps = new TreeMap<>();

		Map<GeneCenterZygosity, List<String>> data = new HashMap<>();
		Map<GeneCenterZygosity, List<String>> viabilityData = new HashMap<>();

		String [] headerParams  ={"MP Term", "Zygosity", "# Genes", "Genes" };
		res.add(headerParams);

		try {

			// Get the list of phenotype calls
			List<GenotypePhenotypeDTO> gps = gpService.getAllGenotypePhenotypes(resources);

			for (GenotypePhenotypeDTO gp : gps) {

				// Exclude Viability calls from the counts of genes by zygosity
				if(gp.getParameterStableId().contains("VIA")) {
					continue;
				}

				// Exclude LacZ calls
				if(gp.getParameterStableId().contains("ALZ")) {
					continue;
				}

				final String symbol = gp.getMarkerSymbol();
				final ZygosityType zygosity = ZygosityType.valueOf(gp.getZygosity());
				final List<String> topLevelMpTermName = gp.getTopLevelMpTermName();

				if (topLevelMpTermName==null) continue;

				// Collect top level MP term information
				for (String mp : topLevelMpTermName) {
					Pair<String, ZygosityType> k = new ImmutablePair<>(mp, zygosity);
					if ( ! mps.containsKey(k)) {
						mps.put(k, new HashSet<String>());
					}
					mps.get(k).add(symbol);
				}

				// Collect gene center zygosity -> mp term
				GeneCenterZygosity g = new GeneCenterZygosity();
				g.setGeneSymbol(symbol);
				g.setZygosity(ZygosityType.valueOf(gp.getZygosity()));
				g.setPhenotypeCenter(gp.getPhenotypingCenter());
				if( ! data.containsKey(g)) {
					data.put(g, new ArrayList<String>());
				}

				data.get(g).add(gp.getMpTermName());

			}

			for (Pair<String, ZygosityType> k : mps.keySet()) {
				final String symbol = k.getLeft();
				final String zygosity = k.getRight().getName();

				String[] row = {symbol, zygosity, Integer.toString(mps.get(k).size()), StringUtils.join(mps.get(k), ": ") };
				res.add(row);
			}


			res.add(EMPTY_ROW);
			res.add(EMPTY_ROW);

			// Get the viability data from the experiment core directly
			for (ObservationDTO obs : oService.getObservationsByParameterStableId("IMPC_VIA_001_001")) {


				// Skip records that are not for the resources we are interested in
				if ( ! resources.contains(obs.getDataSourceName())) {
					continue;
				}

				String symbol = obs.getGeneSymbol();

				GeneCenterZygosity g = new GeneCenterZygosity();
				g.setGeneSymbol(symbol);
				g.setZygosity(ZygosityType.valueOf(obs.getZygosity()));
				g.setPhenotypeCenter(obs.getPhenotypingCenter());
				if( ! viabilityData.containsKey(g)) {
					viabilityData.put(g, new ArrayList<String>());
				}

				viabilityData.get(g).add(obs.getCategory());

			}


			String [] resetHeaderParams = {"Marker symbol", "Center", "Viability", "Hom", "Het", "Hemi", "Link to Gene page" };
			res.add(resetHeaderParams);

			Set<String> geneSymbols = new HashSet<>();
			for (GeneCenterZygosity g : data.keySet()) {
				geneSymbols.add(g.getGeneSymbol());
			}

			Set<String> centers = new HashSet<>();
			for (GeneCenterZygosity g : viabilityData.keySet()) {
				geneSymbols.add(g.getGeneSymbol());
				centers.add(g.getPhenotypeCenter());
			}


			for (String geneSymbol : geneSymbols) {

				GeneDTO gene = geneService.getGeneByGeneSymbol(geneSymbol);

				for (String center : centers) {

					boolean include = false;

					List<String> via = new ArrayList<>();

					GeneCenterZygosity candidate = new GeneCenterZygosity();
					candidate.setPhenotypeCenter(center);
					candidate.setGeneSymbol(geneSymbol);

					candidate.setZygosity(ZygosityType.homozygote);
					String homCount = (data.get(candidate)!=null) ? Integer.toString(data.get(candidate).size()) : "";
					if (viabilityData.containsKey(candidate)) via.addAll(viabilityData.get(candidate));
					include = (viabilityData.containsKey(candidate) || data.containsKey(candidate)) ? true : include;

					candidate.setZygosity(ZygosityType.heterozygote);
					String hetCount = (data.get(candidate)!=null) ? Integer.toString(data.get(candidate).size()) : "";
					if (viabilityData.containsKey(candidate)) via.addAll(viabilityData.get(candidate));
					include = (viabilityData.containsKey(candidate) || data.containsKey(candidate)) ? true : include;

					candidate.setZygosity(ZygosityType.hemizygote);
					String hemiCount = (data.get(candidate)!=null) ? Integer.toString(data.get(candidate).size()) : "";
					if (viabilityData.containsKey(candidate)) via.addAll(viabilityData.get(candidate));
					include = (viabilityData.containsKey(candidate) || data.containsKey(candidate)) ? true : include;

					String geneLink = "";
					if (gene!=null) {
						geneLink = config.get("drupalBaseUrl") + "/data/genes/" + gene.getMgiAccessionId();
					}

					String[] row = {geneSymbol, center, StringUtils.join(via, ": "), homCount, hetCount, hemiCount, geneLink };
					if (include) res.add(row);

				}

			}


		} catch (SolrServerException e) {
			e.printStackTrace();
		}
		return res;

	}


	private class GeneCenterZygosity {
		private String geneSymbol;
		private String phenotypeCenter;
		private ZygosityType zygosity;


		public String getGeneSymbol() {

			return geneSymbol;
		}


		public void setGeneSymbol(String geneSymbol) {

			this.geneSymbol = geneSymbol;
		}


		public String getPhenotypeCenter() {

			return phenotypeCenter;
		}


		public void setPhenotypeCenter(String phenotypeCenter) {

			this.phenotypeCenter = phenotypeCenter;
		}


		public ZygosityType getZygosity() {

			return zygosity;
		}


		public void setZygosity(ZygosityType zygosity) {

			this.zygosity = zygosity;
		}


		@Override
		public boolean equals(Object o) {

			if (this == o) {
				return true;
			}
			if (!(o instanceof GeneCenterZygosity)) {
				return false;
			}

			GeneCenterZygosity that = (GeneCenterZygosity) o;

			if (geneSymbol != null ? !geneSymbol.equals(that.geneSymbol) : that.geneSymbol != null) {
				return false;
			}
			if (phenotypeCenter != null ? !phenotypeCenter.equals(that.phenotypeCenter) : that.phenotypeCenter != null) {
				return false;
			}
			return zygosity == that.zygosity;

		}


		@Override
		public int hashCode() {

			int result = geneSymbol != null ? geneSymbol.hashCode() : 0;
			result = 31 * result + (phenotypeCenter != null ? phenotypeCenter.hashCode() : 0);
			result = 31 * result + (zygosity != null ? zygosity.hashCode() : 0);
			return result;
		}
	}

	public List<List<String[]>> getMpCallDistribution(){
    	
    	Float pVal = (float) 0.0001;
    	TreeMap<String, Long> significant = srService.getDistributionOfAnnotationsByMPTopLevel(resources, pVal);
    	TreeMap<String, Long> all = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
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
    	} catch (SolrServerException | InterruptedException | ExecutionException e) {
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
	    } catch (SolrServerException | InterruptedException | ExecutionException e) {
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

    
    class IpGTTStats {
    	
    	String geneSymbol;
    	String alleleSymbol;
    	String colony;
    	String firstDate;
    	String lastDate;
    	// < sex, <zygosity, <datapoints>>>
    	HashMap<String, HashMap<String, ArrayList<Float>>> datapoints;
    	HashMap<String, HashMap<String, DescriptiveStatistics>> stats;
            	
    	
    	public IpGTTStats(Group group) throws NumberFormatException, SolrServerException, IOException, URISyntaxException{
    		
    		SolrDocumentList docList = group.getResult();
    		colony = group.getGroupValue();
    		SolrDocument doc = docList.get(0);
    		alleleSymbol = doc.getFieldValue(ObservationDTO.ALLELE_SYMBOL).toString();
    		geneSymbol = doc.getFieldValue(ObservationDTO.GENE_SYMBOL).toString();
    		firstDate = doc.getFieldValue(ObservationDTO.DATE_OF_EXPERIMENT).toString();
    		lastDate = docList.get(docList.size()-1).getFieldValue(ObservationDTO.DATE_OF_EXPERIMENT).toString();
    		datapoints = new HashMap<>();
    		stats = new HashMap<>();

    		List<String> zygosities = new ArrayList<>();  
    		List<String> sexes = new ArrayList<>();    		
    		
    		for (SolrDocument d : docList){
    			String sex = d.getFieldValue(ObservationDTO.SEX).toString();
    			String zyg = d.getFieldValue(ObservationDTO.ZYGOSITY).toString();
    			if (!datapoints.containsKey(sex)) {
    				datapoints.put(sex, new HashMap<String, ArrayList<Float>>());
    				stats.put(sex, new HashMap<String, DescriptiveStatistics>());
    			}
    			if (!datapoints.get(sex).containsKey(zyg)){
    				datapoints.get(sex).put(zyg, new ArrayList<Float>());
    				stats.get(sex).put(zyg, new DescriptiveStatistics());
    			}
    			datapoints.get(sex).get(zyg).add((Float)d.getFieldValue(ObservationDTO.DATA_POINT));
    			stats.get(sex).get(zyg).addValue(Double.parseDouble("" + d.getFieldValue(ObservationDTO.DATA_POINT)));
    			
    			if (!zygosities.contains(zyg)){
    				zygosities.add(zyg);
    			}
    			if (!sexes.contains(sex)){
    				sexes.add(sex);
    				datapoints.get(sex).put("WT", new ArrayList<Float>());
    				stats.get(sex).put("WT", new DescriptiveStatistics());
    			}
    				
    		}
    		
    		for (String sex : sexes){
	    		List<ExperimentDTO> experiments = experimentService.getExperimentDTO(
					(Integer)Integer.parseInt(doc.getFieldValue(ObservationDTO.PARAMETER_ID).toString()),
					(Integer)Integer.parseInt(doc.getFieldValue(ObservationDTO.PIPELINE_ID).toString()),  
					doc.getFieldValue(ObservationDTO.GENE_ACCESSION_ID).toString(),  
					SexType.valueOf(sex),  
					(Integer)Integer.parseInt(doc.getFieldValue(ObservationDTO.PHENOTYPING_CENTER_ID).toString()),   
					zygosities, 
					doc.getFieldValue(ObservationDTO.STRAIN_ACCESSION_ID).toString(),    
					null, 
					Boolean.FALSE, 
					doc.getFieldValue(ObservationDTO.ALLELE_ACCESSION_ID).toString());
	    		for (ExperimentDTO exp: experiments){
	    			for (ObservationDTO obs: exp.getControls()){
	    				datapoints.get(sex).get("WT").add((Float)obs.getDataPoint());
	        			stats.get(sex).get("WT").addValue(Double.parseDouble("" + obs.getDataPoint()));
	    			}
	    		}
    		}
    	}
    	
    	public Double getMean(SexType sex, ZygosityType zyg){
    		
    		String zygosity = (zyg != null) ? zyg.getName() : "WT";
    		if (stats.containsKey(sex.getName()) && stats.get(sex.getName()).containsKey(zygosity)){
    			return stats.get(sex.getName()).get(zygosity).getMean();
    		}
    		return null;
    	}
    	
    	public Double getSD(SexType sex, ZygosityType zyg){
    		
    		String zygosity = (zyg != null) ? zyg.getName() : "WT";
    		if (stats.containsKey(sex.getName()) && stats.get(sex.getName()).containsKey(zygosity)){
    			return stats.get(sex.getName()).get(zygosity).getStandardDeviation();
    		}
    		return null;
    	}
    	
    	public Integer getN(SexType sex, ZygosityType zyg){
    		
    		String zygosity = (zyg != null) ? zyg.getName() : "WT";
    		if (datapoints.containsKey(sex.getName()) && datapoints.get(sex.getName()).containsKey(zygosity)){
    			return datapoints.get(sex.getName()).get(zygosity).size();
    		}
    		return null;
    	}
    	
    	public Float getMedian(SexType sex, ZygosityType zyg){
    		
    		String zygosity = (zyg != null) ? zyg.getName() : "WT";
    		if (datapoints.containsKey(sex.getName()) && datapoints.get(sex.getName()).containsKey(zygosity)){
    			return getMedian(datapoints.get(sex.getName()).get(zygosity));
    		}
    		return null;
    	}    	

    	private Float getMedian(List<Float> list){
    		
    		Float median = (float)0.0;
    		int middle = list.size()/2;
    		Collections.sort(list);
    		
    		if ( list.size() == 0){
    			return null;
    		}
    		if (list.size() % 2 == 0){
    			median = (list.get(middle - 1) + list.get(middle)) /2;
    		}else {
    			median = list.get(middle);
    		}
    		
    		return median;
    	}
    }

}
