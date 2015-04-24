/**
 * Copyright © 2011-2014 EMBL - European Bioinformatics Institute
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License.  
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.phenotype.service;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.math3.random.EmpiricalDistribution;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.GroupCommand;
import org.apache.solr.client.solrj.response.PivotField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.util.NamedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import uk.ac.ebi.phenotype.bean.StatisticalResultBean;
import uk.ac.ebi.phenotype.chart.StackedBarsData;
import uk.ac.ebi.phenotype.comparator.GeneRowForHeatMap3IComparator;
import uk.ac.ebi.phenotype.dao.*;
import uk.ac.ebi.phenotype.pojo.*;
import uk.ac.ebi.phenotype.service.dto.GenotypePhenotypeDTO;
import uk.ac.ebi.phenotype.service.dto.ObservationDTO;
import uk.ac.ebi.phenotype.service.dto.StatisticalResultDTO;
import uk.ac.ebi.phenotype.web.controller.OverviewChartsController;
import uk.ac.ebi.phenotype.web.pojo.BasicBean;
import uk.ac.ebi.phenotype.util.PhenotypeFacetResult;
import uk.ac.ebi.phenotype.web.pojo.GeneRowForHeatMap;
import uk.ac.ebi.phenotype.web.pojo.HeatMapCell;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.Set;

@Service
public class StatisticalResultService extends AbstractGenotypePhenotypeService {

    @Autowired
    BiologicalModelDAO bmDAO;
    
    @Autowired
    DatasourceDAO datasourceDAO;
    
    @Autowired
    OrganisationDAO organisationDAO;
    
    @Autowired
	@Qualifier("postqcService")
    AbstractGenotypePhenotypeService gpService;
        
    @Autowired
    ProjectDAO projectDAO;
    

    private static final Logger LOG = LoggerFactory.getLogger(StatisticalResultService.class);

    Map<String, ArrayList<String>> maleParamToGene = null;
    Map<String, ArrayList<String>> femaleParamToGene = null;


	public StatisticalResultService(String solrUrl, PhenotypePipelineDAO pipelineDao) {
		solr = new HttpSolrServer(solrUrl);
		pipelineDAO = pipelineDao;
		isPreQc = false; 
	}
	    

	public Map<String, Long> getColoniesNoMPHit(ArrayList<String> resourceName, ZygosityType zygosity) 
	throws SolrServerException{
		Map<String, Long>  res = new HashMap<>();
    	Long time = System.currentTimeMillis();
    	SolrQuery q = new SolrQuery();
    	            
    	if (resourceName != null){
            q.setQuery(GenotypePhenotypeDTO.RESOURCE_NAME + ":" + StringUtils.join(resourceName, " OR " + GenotypePhenotypeDTO.RESOURCE_NAME + ":"));
        }else {
            q.setQuery("*:*");
        }    
    	
    	if (zygosity != null){
    		q.addFilterQuery(GenotypePhenotypeDTO.ZYGOSITY + ":" + zygosity.name());
    	}
    	
    	q.addFilterQuery(GenotypePhenotypeDTO.P_VALUE+ ":[" + this.P_VALUE_THRESHOLD + " TO 1]");
    	    	
    	q.addFacetField(StatisticalResultDTO.COLONY_ID);
    	q.setFacetMinCount(1);
    	q.setFacet(true);
    	q.setRows(1);
    	q.set("facet.limit", -1); 

    	System.out.println("Solr url for getColoniesNoMPHit " + solr.getBaseURL() + "/select?" + q);
    	QueryResponse response = solr.query(q);
    	
    	for( Count facet : response.getFacetField(StatisticalResultDTO.COLONY_ID).getValues()){
    		String value = facet.getName();
    		long count = facet.getCount();
    		res.put(value,count);
    	}
    		
    	System.out.println("Done in " + (System.currentTimeMillis() - time));
    	return res;
    
	}
	

	public StackedBarsData getUnidimensionalData(Parameter p, List<String> genes, ArrayList<String> strains, String biologicalSample, String[] center, String[] sex)
	throws SolrServerException {

		String urlParams = "";
		SolrQuery query = new SolrQuery().addFilterQuery(StatisticalResultDTO.PARAMETER_STABLE_ID + ":" + p.getStableId());
		String q = (strains.size() > 1) ? "(" + StatisticalResultDTO.STRAIN_ACCESSION_ID + ":\"" + StringUtils.join(strains.toArray(), "\" OR " + StatisticalResultDTO.STRAIN_ACCESSION_ID + ":\"") + "\")" : StatisticalResultDTO.STRAIN_ACCESSION_ID + ":\"" + strains.get(0) + "\"";
		if (strains.size() > 0) {
			urlParams += "&strain=" + StringUtils.join(strains.toArray(), "&strain=");
		}

		if (center != null && center.length > 0) {
			q += " AND (";
			q += (center.length > 1) ? StatisticalResultDTO.PHENOTYPING_CENTER + ":\"" + StringUtils.join(center, "\" OR " + StatisticalResultDTO.PHENOTYPING_CENTER + ":\"") + "\"" : StatisticalResultDTO.PHENOTYPING_CENTER + ":\"" + center[0] + "\"";
			q += ")";
			urlParams += "&phenotyping_center=" + StringUtils.join(center, "&phenotyping_center=");
		}

		if (sex != null && sex.length == 1) {
			q += " AND " + StatisticalResultDTO.SEX + ":\"" + sex[0] + "\"";
		}

		query.setQuery(q);
		query.setRows(10000000);
		query.setFields(StatisticalResultDTO.MARKER_ACCESSION_ID, StatisticalResultDTO.FEMALE_CONTROL_MEAN, StatisticalResultDTO.MARKER_SYMBOL,
			StatisticalResultDTO.FEMALE_MUTANT_MEAN, StatisticalResultDTO.MALE_CONTROL_MEAN, StatisticalResultDTO.MALE_MUTANT_MEAN,
			StatisticalResultDTO.FEMALE_CONTROL_COUNT, StatisticalResultDTO.FEMALE_MUTANT_COUNT, StatisticalResultDTO.MALE_CONTROL_COUNT, StatisticalResultDTO.MALE_MUTANT_COUNT);
		query.set("group", true);
		query.set("group.field", StatisticalResultDTO.COLONY_ID);
		query.set("group.limit", 1);

		
		System.out.println("SOLR URL FOR OVERVIEW CHARTS ::: " + solr.getBaseURL() + "/select?" + query);
		
		// for each colony get the mean & put it in the array of data to plot
		List<Group> groups = solr.query(query).getGroupResponse().getValues().get(0).getValues();
		double[] meansArray = new double[groups.size()];
		String[] genesArray = new String[groups.size()];
		String[] geneSymbolArray = new String[groups.size()];
		int i = 0;
		
		for (Group gr : groups) {
			SolrDocumentList resDocs = gr.getResult();
			String sexToDisplay = null;
			genesArray[i] = (String) resDocs.get(0).get(StatisticalResultDTO.MARKER_ACCESSION_ID);
			geneSymbolArray[i] = (String) resDocs.get(0).get(StatisticalResultDTO.MARKER_SYMBOL);
			
			if (sex != null && sex.length == 1){
				if (sex[0].equalsIgnoreCase(SexType.male.getName())) {
					sexToDisplay = 	SexType.male.getName();
				}
				if (sex[0].equalsIgnoreCase(SexType.female.getName())) {
					sexToDisplay = 	SexType.female.getName();
				}
			}
			if (sex == null || sex.length == 0 || sex.length == 2) {
				if ( resDocs.get(0).containsKey(StatisticalResultDTO.FEMALE_CONTROL_MEAN) &&
					 resDocs.get(0).containsKey(StatisticalResultDTO.FEMALE_MUTANT_MEAN) &&  
					 resDocs.get(0).containsKey(StatisticalResultDTO.MALE_CONTROL_MEAN) &&
					 resDocs.get(0).containsKey(StatisticalResultDTO.MALE_MUTANT_MEAN)){
					sexToDisplay = null;
				}
				else if (resDocs.get(0).containsKey(StatisticalResultDTO.FEMALE_CONTROL_MEAN) &&
				 resDocs.get(0).containsKey(StatisticalResultDTO.FEMALE_MUTANT_MEAN) &&  
				 (! resDocs.get(0).containsKey(StatisticalResultDTO.MALE_CONTROL_MEAN) ||
				 !resDocs.get(0).containsKey(StatisticalResultDTO.MALE_MUTANT_MEAN))){
					sexToDisplay = SexType.female.getName();
				}
				else if (!resDocs.get(0).containsKey(StatisticalResultDTO.FEMALE_CONTROL_MEAN) ||
				 !resDocs.get(0).containsKey(StatisticalResultDTO.FEMALE_MUTANT_MEAN) &&  
				 resDocs.get(0).containsKey(StatisticalResultDTO.MALE_CONTROL_MEAN) &&
				 !resDocs.get(0).containsKey(StatisticalResultDTO.MALE_MUTANT_MEAN)){
					sexToDisplay = SexType.male.getName();
				}
			}
			
			if (sex == null ) {					
				System.out.println("BOTH SEXES");
				
				Float totalMutant = Float.parseFloat(resDocs.get(0).get(StatisticalResultDTO.FEMALE_MUTANT_COUNT).toString())
						+ Float.parseFloat(resDocs.get(0).get(StatisticalResultDTO.MALE_MUTANT_COUNT).toString());
				Float ratioMale = Float.parseFloat(resDocs.get(0).get(StatisticalResultDTO.MALE_MUTANT_COUNT).toString()) / totalMutant;
				Float ratioFemale = Float.parseFloat(resDocs.get(0).get(StatisticalResultDTO.FEMALE_MUTANT_COUNT).toString()) / totalMutant;
				meansArray[i] =  Float.parseFloat(resDocs.get(0).get(StatisticalResultDTO.FEMALE_MUTANT_MEAN).toString()) * ratioFemale 
					+ Float.parseFloat(resDocs.get(0).get(StatisticalResultDTO.MALE_MUTANT_MEAN).toString()) * ratioMale;
				
				System.out.println(genesArray[i]  + "  " + ratioMale + " " + ratioFemale + " => " + meansArray[i] );
				
				totalMutant = Float.parseFloat(resDocs.get(0).get(StatisticalResultDTO.FEMALE_CONTROL_COUNT).toString())
					+ Float.parseFloat(resDocs.get(0).get(StatisticalResultDTO.MALE_CONTROL_COUNT).toString());
				ratioMale = Float.parseFloat(resDocs.get(0).get(StatisticalResultDTO.MALE_CONTROL_COUNT).toString()) / totalMutant;
				ratioFemale = Float.parseFloat(resDocs.get(0).get(StatisticalResultDTO.FEMALE_CONTROL_COUNT).toString()) / totalMutant;				
				meansArray[i] = (Float.parseFloat(resDocs.get(0).get(StatisticalResultDTO.FEMALE_CONTROL_MEAN).toString()) * ratioFemale 
					+ Float.parseFloat(resDocs.get(0).get(StatisticalResultDTO.MALE_CONTROL_MEAN).toString()) * ratioMale);		

				System.out.println(genesArray[i]  + "   " + ratioMale + " " + ratioFemale + " => " + meansArray[i]);
				
			} else if (sexToDisplay.equalsIgnoreCase(SexType.male.getName())){
				System.out.println("MALE ONLY");
				meansArray[i] = Float.parseFloat(resDocs.get(0).get(StatisticalResultDTO.MALE_MUTANT_MEAN).toString()) / Float.parseFloat(resDocs.get(0).get(StatisticalResultDTO.MALE_CONTROL_MEAN).toString());
			} else if (sexToDisplay.equalsIgnoreCase(SexType.female.getName())) {
				System.out.println("FEMALE ONLY");
				meansArray[i] = Float.parseFloat(resDocs.get(0).get(StatisticalResultDTO.FEMALE_MUTANT_MEAN).toString()) / Float.parseFloat(resDocs.get(0).get(StatisticalResultDTO.FEMALE_CONTROL_MEAN).toString());
			}
			
			i++;
		}

		// we do the binning for all the data but fill the bins after that to
		// keep tract of phenotype associations
		int binCount = Math.min((int) Math.floor((double) groups.size() / 2), 20);
		ArrayList<String> mutantGenes = new ArrayList<String>();
		ArrayList<String> controlGenes = new ArrayList<String>();
		ArrayList<String> mutantGeneAcc = new ArrayList<String>();
		ArrayList<String> controlGeneAcc = new ArrayList<String>();
		ArrayList<Double> upperBounds = new ArrayList<Double>();
		EmpiricalDistribution distribution = new EmpiricalDistribution(binCount);
		if (meansArray.length > 0) {
			distribution.load(meansArray);
			for (double bound : distribution.getUpperBounds()) {
				upperBounds.add(bound);
			}
			// we we need to distribute the control mutants and the
			// phenotype-mutants in the bins
			ArrayList<Double> controlM = new ArrayList<Double>();
			ArrayList<Double> phenMutants = new ArrayList<Double>();

			for (int j = 0; j < upperBounds.size(); j++) {
				controlM.add((double) 0);
				phenMutants.add((double) 0);
				controlGenes.add("");
				mutantGenes.add("");
				controlGeneAcc.add("");
				mutantGeneAcc.add("");
			}

			for (int j = 0; j < groups.size(); j++) {
				// find out the proper bin
				int binIndex = getBin(upperBounds, meansArray[j]);
				if (genes.contains(genesArray[j])) {
					phenMutants.set(binIndex, 1 + phenMutants.get(binIndex));
					String genesString = mutantGenes.get(binIndex);
					if (!genesString.contains(geneSymbolArray[j])) {
						if (genesString.equals("")) {
							mutantGenes.set(binIndex, geneSymbolArray[j]);
							mutantGeneAcc.set(binIndex, "accession=" + genesArray[j]);
						} else {
							mutantGenes.set(binIndex, genesString + ", " + geneSymbolArray[j]);
							mutantGeneAcc.set(binIndex, mutantGeneAcc.get(binIndex) + "&accession=" + genesArray[j]);
						}
					}
				} else { // treat as control because they don't have this
							// phenotype association
					String genesString = controlGenes.get(binIndex);
					if (!genesString.contains(geneSymbolArray[j])) {
						if (genesString.equalsIgnoreCase("")) {
							controlGenes.set(binIndex, geneSymbolArray[j]);
							controlGeneAcc.set(binIndex, "accession=" + genesArray[j]);
						} else {
							controlGenes.set(binIndex, genesString + ", " + geneSymbolArray[j]);
							controlGeneAcc.set(binIndex, controlGeneAcc.get(binIndex) + "&accession=" + genesArray[j]);
						}
					}
					controlM.set(binIndex, 1 + controlM.get(binIndex));
				}
			}
			// System.out.println(" Mutants list " + phenMutants);

			// add the rest of parameters to the graph urls
			for (int t = 0; t < controlGeneAcc.size(); t++) {
				controlGeneAcc.set(t, controlGeneAcc.get(t) + urlParams);
				mutantGeneAcc.set(t, mutantGeneAcc.get(t) + urlParams);
			}

			StackedBarsData data = new StackedBarsData();
			data.setUpperBounds(upperBounds);
			data.setControlGenes(controlGenes);
			data.setControlMutatns(controlM);
			data.setMutantGenes(mutantGenes);
			data.setPhenMutants(phenMutants);
			data.setControlGeneAccesionIds(controlGeneAcc);
			data.setMutantGeneAccesionIds(mutantGeneAcc);
			return data;
		}

		return null;
	}


	private int getBin(List<Double> bins, Double valueToBin) {

		for (Double upperBound : bins) {
			if (valueToBin < upperBound) { return bins.indexOf(upperBound); }
		}
		return bins.size() - 1;
	}


    public Map<String, ArrayList<String>> getDistributionOfLinesByMPTopLevel(ArrayList<String> resourceName, Float pValueThreshold)
	throws SolrServerException, InterruptedException, ExecutionException {

		Map<String, ArrayList<String>> res = new ConcurrentHashMap<>(); //<parameter, <genes>>
		Long time = System.currentTimeMillis();
		String pivotFacet =  StatisticalResultDTO.TOP_LEVEL_MP_TERM_NAME + "," + StatisticalResultDTO.COLONY_ID;
		SolrQuery q = new SolrQuery();
		
		if (resourceName != null){
            q.setQuery(StatisticalResultDTO.RESOURCE_NAME + ":" + StringUtils.join(resourceName, " OR " + StatisticalResultDTO.RESOURCE_NAME + ":"));
        }else {
            q.setQuery("*:*");
        }

        if (pValueThreshold != null){
        	q.setFilterQueries(StatisticalResultDTO.P_VALUE + ":[0 TO " + pValueThreshold + "]");
        } 
        
		q.set("facet.pivot", pivotFacet);
		q.setFacet(true);
		q.setFacetMinCount(1);
		q.setRows(1);
		q.set("facet.limit", -1); 

		System.out.println("Solr url for getDistributionOfLinesByMPTopLevel " + solr.getBaseURL() + "/select?" + q);
		QueryResponse response = solr.query(q);
		
		for( PivotField pivot : response.getFacetPivot().get(pivotFacet)){
			ArrayList<String> colonies = new ArrayList<>();
			for (PivotField colony : pivot.getPivot()){
				colonies.add(colony.getValue().toString());
			}
			res.put(pivot.getValue().toString(), new ArrayList<String>(colonies));
		}
		
		System.out.println("Done in " + (System.currentTimeMillis() - time));
		return res;
	}
   
    
    public Map<String, ArrayList<String>> getDistributionOfGenesByMPTopLevel(ArrayList<String> resourceName, Float pValueThreshold)
	throws SolrServerException, InterruptedException, ExecutionException {

		Map<String, ArrayList<String>> res = new ConcurrentHashMap<>(); //<parameter, <genes>>
		Long time = System.currentTimeMillis();
		String pivotFacet =  StatisticalResultDTO.TOP_LEVEL_MP_TERM_NAME + "," + StatisticalResultDTO.MARKER_ACCESSION_ID;
		SolrQuery q = new SolrQuery();
		
		if (resourceName != null){
            q.setQuery(StatisticalResultDTO.RESOURCE_NAME + ":" + StringUtils.join(resourceName, " OR " + StatisticalResultDTO.RESOURCE_NAME + ":"));
        }else {
            q.setQuery("*:*");
        }

        if (pValueThreshold != null){
        	q.setFilterQueries(StatisticalResultDTO.P_VALUE + ":[0 TO " + pValueThreshold + "]");
        } 
        
		q.set("facet.pivot", pivotFacet);
		q.setFacet(true);
		q.setRows(1);
		q.set("facet.limit", -1); 

		System.out.println("Solr url for getDistributionOfGenesByMPTopLevel " + solr.getBaseURL() + "/select?" + q);
		QueryResponse response = solr.query(q);
		
		for( PivotField pivot : response.getFacetPivot().get(pivotFacet)){
			ArrayList<String> genes = new ArrayList<>();
			for (PivotField gene : pivot.getPivot()){
				genes.add(gene.getValue().toString());
			}
			res.put(pivot.getValue().toString(), new ArrayList<String>(genes));
		}
		
		System.out.println("Done in " + (System.currentTimeMillis() - time));
		return res;
	}
    
    /**
     * @return Map <String, Long> : <top_level_mp_name, number_of_annotations>
     * @author tudose
     */
    public TreeMap<String, Long> getDistributionOfAnnotationsByMPTopLevel(ArrayList<String> resourceName, Float pValueThreshold) {

        SolrQuery query = new SolrQuery();
        
        if (resourceName != null){
            query.setQuery(StatisticalResultDTO.RESOURCE_NAME + ":" + StringUtils.join(resourceName, " OR " + StatisticalResultDTO.RESOURCE_NAME + ":"));
        }else {
            query.setQuery("*:*");
        }

        if (pValueThreshold != null){
        	query.setFilterQueries(StatisticalResultDTO.P_VALUE + ":[0 TO " + pValueThreshold + "]");
        } 
        	
        query.setFacet(true);
        query.setFacetLimit(-1);
        query.setFacetMinCount(1);
        query.setRows(0);
        query.addFacetField(StatisticalResultDTO.TOP_LEVEL_MP_TERM_NAME);

        try {
            QueryResponse response = solr.query(query);
            TreeMap<String, Long> res = new TreeMap<>();
            res.putAll(getFacets(response).get(StatisticalResultDTO.TOP_LEVEL_MP_TERM_NAME));
            return res;
        } catch (SolrServerException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    
    /**
     * Get the result for a set of 
     *  allele strain phenotypeCenter, pipeline, parameter, metadata, zygosity, sex
     * @param statisticalType 
     * @throws SolrServerException 
     */
    public List<? extends StatisticalResult> getStatisticalResult(
            String alleleAccession, 
            String strain, 
            String phenotypingCenter, 
            String pipelineStableId, 
            String parameterStableId, 
            String metadataGroup, 
            ZygosityType zygosity, 
            SexType sex, 
            ObservationType statisticalType) throws SolrServerException {

        List<StatisticalResult> results = new ArrayList<>();

        QueryResponse response = new QueryResponse();
    
        SolrQuery query = new SolrQuery()
            .setQuery("*:*")
            .addFilterQuery(StatisticalResultDTO.ALLELE_ACCESSION_ID + ":\"" + alleleAccession + "\"")
            .addFilterQuery(StatisticalResultDTO.PHENOTYPING_CENTER + ":\"" + phenotypingCenter + "\"")
            .addFilterQuery(StatisticalResultDTO.PIPELINE_STABLE_ID + ":" + pipelineStableId)
            .addFilterQuery(StatisticalResultDTO.PARAMETER_STABLE_ID + ":" + parameterStableId)
            .addFilterQuery(StatisticalResultDTO.ZYGOSITY + ":" + zygosity.name())
            .setStart(0)
            .setRows(10)
        ;

        if(strain != null) {
            query.addFilterQuery(StatisticalResultDTO.STRAIN_ACCESSION_ID + ":\"" + strain + "\"");
        }

        if(sex != null) {
            query.addFilterQuery(StatisticalResultDTO.SEX + ":" + sex);
        }

        if(metadataGroup==null) {
            // don't add a metadata group filter
        } else if (metadataGroup.isEmpty()) {
            query.addFilterQuery(StatisticalResultDTO.METADATA_GROUP + ":\"\"");
        } else {
            query.addFilterQuery(StatisticalResultDTO.METADATA_GROUP + ":" + metadataGroup);
        }

        response = solr.query(query);
        List<StatisticalResultDTO> solrResults = response.getBeans(StatisticalResultDTO.class);

        if (statisticalType == ObservationType.unidimensional) {

            for (StatisticalResultDTO solrResult : solrResults) {
                results.add(translateStatisticalResultToUnidimensionalResult(solrResult));
            }

        } else if (statisticalType == ObservationType.categorical) {

            for (StatisticalResultDTO solrResult : solrResults) {
                results.add(translateStatisticalResultToCategoricalResult(solrResult));
            }

        }
        
        return results;
    }
    

    public Map<String, Set<String>> getAccessionProceduresMap(String resourceName){
    	
    	SolrQuery query = new SolrQuery();
    	Map<String, Set<String>> res =  new HashMap<>(); 
    	NamedList<List<PivotField>> response;
    	
    	if (resourceName == null){
    		query.setQuery("*:*");
    	}else {
    		query.setQuery(StatisticalResultDTO.RESOURCE_NAME + ":" + resourceName);
    	}
    	query.setFacet(true);
    	query.addFacetPivotField(StatisticalResultDTO.MARKER_ACCESSION_ID + "," + StatisticalResultDTO.PROCEDURE_STABLE_ID);
    	query.setFacetLimit(-1);
    	query.setFacetMinCount(1);
    	query.setRows(0);
    	
		try {
			response = solr.query(query).getFacetPivot();
			for (PivotField genePivot : response.get(StatisticalResultDTO.MARKER_ACCESSION_ID + "," + StatisticalResultDTO.PROCEDURE_STABLE_ID)){
				String geneName = genePivot.getValue().toString();
				Set<String> procedures = new HashSet<>();
				for (PivotField f : genePivot.getPivot()){
					procedures.add(f.getValue().toString());
				}
				res.put(geneName, procedures);
			}
		} catch (SolrServerException e) {
			e.printStackTrace();
		}
    	return res;
    }
    

    public Map<String, List<StatisticalResultBean>> getPvaluesByAlleleAndPhenotypingCenterAndPipeline(String alleleAccession, String phenotypingCenter,	String pipelineStableId, List<String> procedureStableIds, ArrayList<String> resource) 
	throws NumberFormatException, SolrServerException {
    	
    	Map<String, List<StatisticalResultBean>> results = new HashMap<String, List<StatisticalResultBean>>();
    	SolrQuery query = new SolrQuery();
    	
		query.setQuery(StatisticalResultDTO.PHENOTYPING_CENTER + ":\"" + phenotypingCenter + "\" AND "
    			+ StatisticalResultDTO.PIPELINE_STABLE_ID + ":" + pipelineStableId + " AND "
				+ StatisticalResultDTO.ALLELE_ACCESSION_ID + ":\"" + alleleAccession + "\"");
		if (procedureStableIds != null){
			query.addFilterQuery("(" + StatisticalResultDTO.PROCEDURE_STABLE_ID + ":" 
					+ StringUtils.join(procedureStableIds, " OR " + StatisticalResultDTO.PROCEDURE_STABLE_ID + ":") + ")");
		}
		if (resource != null) {
			query.addFilterQuery("(" + StatisticalResultDTO.RESOURCE_NAME + ":" + StringUtils.join(resource, " OR " + StatisticalResultDTO.RESOURCE_NAME + ":") + ")");
		}
		query.setRows(Integer.MAX_VALUE);
		query.addField(StatisticalResultDTO.P_VALUE)
			.addField(StatisticalResultDTO.EFFECT_SIZE)
			.addField(StatisticalResultDTO.STATUS)
			.addField(StatisticalResultDTO.STATISTICAL_METHOD)
			.addField(StatisticalResultDTO.ZYGOSITY)
			.addField(StatisticalResultDTO.MALE_CONTROL_COUNT)
			.addField(StatisticalResultDTO.MALE_MUTANT_COUNT)
			.addField(StatisticalResultDTO.FEMALE_CONTROL_COUNT)
			.addField(StatisticalResultDTO.FEMALE_MUTANT_COUNT)
			.addField(StatisticalResultDTO.PARAMETER_STABLE_ID)
			.addField(StatisticalResultDTO.METADATA_GROUP);		
		query.set("sort", StatisticalResultDTO.P_VALUE + " asc");

		for (SolrDocument doc : solr.query(query).getResults()){
			String parameterStableId = doc.getFieldValue(StatisticalResultDTO.PARAMETER_STABLE_ID).toString();
			List<StatisticalResultBean> lb = null;
			
			if (results.containsKey(parameterStableId)) {
				lb = results.get(parameterStableId);
			} else {
				lb = new ArrayList<StatisticalResultBean>();
				results.put(parameterStableId, lb);
			} 
			
			Double effectSize = doc.containsKey(StatisticalResultDTO.EFFECT_SIZE) ? Double.parseDouble(doc.getFieldValue(StatisticalResultDTO.EFFECT_SIZE).toString()) : 1000000000;
			String status = doc.containsKey(StatisticalResultDTO.STATUS) ? doc.getFieldValue(StatisticalResultDTO.STATUS).toString() : "no status found";
			
			lb.add(new StatisticalResultBean(
						Double.parseDouble(doc.getFieldValue(StatisticalResultDTO.P_VALUE).toString()), 
						effectSize,
						status,
						doc.getFieldValue(StatisticalResultDTO.STATISTICAL_METHOD).toString(),
						"don't know",
						doc.getFieldValue(StatisticalResultDTO.ZYGOSITY).toString(),
						Integer.parseInt(doc.getFieldValue(StatisticalResultDTO.MALE_CONTROL_COUNT).toString()),
						Integer.parseInt(doc.getFieldValue(StatisticalResultDTO.MALE_MUTANT_COUNT).toString()),
						Integer.parseInt(doc.getFieldValue(StatisticalResultDTO.FEMALE_CONTROL_COUNT).toString()),
						Integer.parseInt(doc.getFieldValue(StatisticalResultDTO.FEMALE_MUTANT_COUNT).toString()),
						doc.getFieldValue(StatisticalResultDTO.METADATA_GROUP).toString()
				 ));
		}
		
		return results;
		
    }
    
    
    public PhenotypeFacetResult getPhenotypeFacetResultByPhenotypingCenterAndPipeline(String phenotypingCenter, String pipelineStableId)
	throws IOException, URISyntaxException {
    	
    	System.out.println("DOING PHEN CALL SUMMARY RESULTS FROM SRS");
		SolrQuery query = new SolrQuery();
		query.setQuery(StatisticalResultDTO.PHENOTYPING_CENTER + ":\"" + phenotypingCenter);
		query.addFilterQuery(StatisticalResultDTO.PIPELINE_STABLE_ID + ":" + pipelineStableId);
		query.setFacet(true);
		query.addFacetField(StatisticalResultDTO.RESOURCE_FULLNAME);
		query.addFacetField(StatisticalResultDTO.PROCEDURE_NAME );
		query.addFacetField(StatisticalResultDTO.MARKER_SYMBOL );
		query.addFacetField(StatisticalResultDTO.MP_TERM_NAME );
		query.set("sort", "p_value asc");
		query.setRows(10000000);
		query.set("wt", "json");
		query.set("version", "2.2");
		
		String solrUrl = solr.getBaseURL() + "/select?" + query;
		return gpService.createPhenotypeResultFromSolrResponse(solrUrl, false);
	}

    
    public Set<String> getAccessionsByResourceName(String resourceName){
    	
    	Set<String> res = new HashSet<>();
    	SolrQuery query = new SolrQuery()
        	.setQuery(StatisticalResultDTO.RESOURCE_NAME + ":" + resourceName);
    	query.setFacet(true);
    	query.addFacetField(StatisticalResultDTO.MARKER_ACCESSION_ID);
    	query.setFacetLimit(10000000);
    	query.setFacetMinCount(1);
    	query.setRows(0);
    	
    	QueryResponse response;
		try {
			response = solr.query(query);
			for (Count id: response.getFacetField(StatisticalResultDTO.MARKER_ACCESSION_ID).getValues()){
				res.add(id.getName());
			}
		} catch (SolrServerException e) {
			e.printStackTrace();
		}
    	return res;
    }
    
    protected UnidimensionalResult translateStatisticalResultToUnidimensionalResult(StatisticalResultDTO result) {
        UnidimensionalResult r = new UnidimensionalResult();
        
        if(result.getBatchSignificant()!=null) r.setBatchSignificance(Boolean.valueOf(result.getBatchSignificant()));
        if(result.getBlupsTest()!= null) r.setBlupsTest(new Double(result.getBlupsTest()));
        r.setColonyId(result.getColonyId());
        if(result.getControlBiologicalModelId()!= null) r.setControlBiologicalModel(bmDAO.getBiologicalModelById(result.getControlBiologicalModelId()));
        r.setControlSelectionStrategy(result.getControlSelectionMethod());
        if(result.getResourceId()!= null) r.setDatasource(datasourceDAO.getDatasourceById(result.getResourceId()));
        r.setDependentVariable(result.getDependentVariable());
        if(result.getEffectSize()!= null) r.setEffectSize(new Double(result.getEffectSize()));
        if(result.getMutantBiologicalModelId()!= null) r.setExperimentalBiologicalModel(bmDAO.getBiologicalModelById(result.getMutantBiologicalModelId()));
        if(result.getZygosity()!= null) r.setExperimentalZygosity(ZygosityType.valueOf(result.getZygosity()));
        r.setFemaleControls(result.getFemaleControlCount());
        r.setFemaleMutants(result.getFemaleMutantCount());
        if(result.getGenotypeEffectPValue()!= null) r.setGenderEffectPValue(new Double(result.getGenotypeEffectPValue()));
        if(result.getFemaleKoParameterEstimate()!= null) r.setGenderFemaleKoEstimate(new Double(result.getFemaleKoParameterEstimate()));
        if(result.getFemaleKoEffectPValue()!= null) r.setGenderFemaleKoPValue(new Double(result.getFemaleKoEffectPValue()));
        if(result.getFemaleKoEffectStderrEstimate()!= null) r.setGenderFemaleKoStandardErrorEstimate(new Double(result.getFemaleKoEffectStderrEstimate()));
        if(result.getMaleKoParameterEstimate()!= null) r.setGenderMaleKoEstimate(new Double(result.getMaleKoParameterEstimate()));
        if(result.getMaleKoEffectPValue()!= null) r.setGenderMaleKoPValue(new Double(result.getMaleKoEffectPValue()));
        if(result.getMaleKoEffectStderrEstimate()!= null) r.setGenderMaleKoStandardErrorEstimate(new Double(result.getMaleKoEffectStderrEstimate()));
        if(result.getSexEffectParameterEstimate()!= null) r.setGenderParameterEstimate(new Double(result.getSexEffectParameterEstimate()));
        if(result.getSexEffectStderrEstimate()!= null) r.setGenderStandardErrorEstimate(new Double(result.getSexEffectStderrEstimate()));
        if(result.getGenotypeEffectPValue()!= null) r.setGenotypeEffectPValue(new Double(result.getGenotypeEffectPValue()));
        if(result.getGenotypeEffectParameterEstimate()!= null) r.setGenotypeParameterEstimate(new Double(result.getGenotypeEffectParameterEstimate()));
        if(result.getGenotypeEffectStderrEstimate()!= null) r.setGenotypeStandardErrorEstimate(new Double(result.getGenotypeEffectStderrEstimate()));
        r.setGp1Genotype(result.getGroup1Genotype());
        if(result.getGroup1ResidualsNormalityTest()!= null) r.setGp1ResidualsNormalityTest(new Double(result.getGroup1ResidualsNormalityTest()));
        r.setGp2Genotype(result.getGroup2Genotype());
        if(result.getGroup2ResidualsNormalityTest()!= null) r.setGp2ResidualsNormalityTest(new Double(result.getGroup2ResidualsNormalityTest()));
        r.setId(result.getDbId());
        if(result.getInteractionEffectPValue()!= null) r.setInteractionEffectPValue(new Double(result.getInteractionEffectPValue()));
        r.setInteractionSignificance(result.getInteractionSignificant());
        if(result.getInterceptEstimate()!= null) r.setInterceptEstimate(new Double(result.getInterceptEstimate()));
        if(result.getInterceptEstimateStderrEstimate()!= null) r.setInterceptEstimateStandardError(new Double(result.getInterceptEstimateStderrEstimate()));
        r.setMaleControls(result.getMaleControlCount());
        r.setMaleMutants(result.getMaleMutantCount()); 
        r.setMetadataGroup(result.getMetadataGroup());
        if(result.getNullTestPValue()!= null) r.setNullTestSignificance(new Double(result.getNullTestPValue()));
        if(result.getPhenotypingCenter()!= null) r.setOrganisation(organisationDAO.getOrganisationByName(result.getPhenotypingCenter()));
        if(result.getParameterStableId()!= null) r.setParameter(pipelineDAO.getParameterByStableId(result.getParameterStableId()));
        if(result.getPipelineStableId()!= null) r.setPipeline(pipelineDAO.getPhenotypePipelineByStableId(result.getPipelineStableId()));
        if(result.getProjectName()!= null) r.setProject(projectDAO.getProjectByName(result.getProjectName()));
        if(result.getpValue()!= null) r.setpValue(new Double(result.getpValue()));
        r.setRawOutput(result.getRawOutput());
        if(result.getRotatedResidualsTest()!= null) r.setRotatedResidualsNormalityTest(new Double(result.getRotatedResidualsTest()));
        if(result.getSex()!= null) r.setSexType(SexType.valueOf(result.getSex()));
        r.setStatisticalMethod(result.getStatisticalMethod());
        r.setVarianceSignificance(result.getVarianceSignificant());
        if(result.getWeightEffectPValue()!= null) r.setWeightEffectPValue(new Double(result.getWeightEffectPValue()));
        if(result.getWeightEffectParameterEstimate()!= null) r.setWeightParameterEstimate(new Double(result.getWeightEffectParameterEstimate()));
        if(result.getWeightEffectStderrEstimate()!= null) r.setWeightStandardErrorEstimate(new Double(result.getWeightEffectStderrEstimate()));
        if(result.getZygosity()!= null) r.setZygosityType(ZygosityType.valueOf(result.getZygosity()));

        return r;
    }

    protected CategoricalResult translateStatisticalResultToCategoricalResult(StatisticalResultDTO result) {
        CategoricalResult r = new CategoricalResult();
        r.setColonyId(result.getColonyId());
        if(result.getControlBiologicalModelId()!= null) r.setControlBiologicalModel(bmDAO.getBiologicalModelById(result.getControlBiologicalModelId()));
        r.setControlSelectionStrategy(result.getControlSelectionMethod());
        if(result.getResourceId()!= null) r.setDatasource(datasourceDAO.getDatasourceById(result.getResourceId()));
        r.setDependentVariable(result.getDependentVariable());
        if(result.getEffectSize()!= null) r.setEffectSize(new Double(result.getEffectSize()));
        if(result.getMutantBiologicalModelId()!= null) r.setExperimentalBiologicalModel(bmDAO.getBiologicalModelById(result.getMutantBiologicalModelId()));
        if(result.getZygosity()!= null) r.setExperimentalZygosity(ZygosityType.valueOf(result.getZygosity()));
        r.setFemaleControls(result.getFemaleControlCount());
        r.setFemaleMutants(result.getFemaleMutantCount());
        r.setMaleControls(result.getMaleControlCount());
        r.setMaleMutants(result.getMaleMutantCount()); 
        r.setMetadataGroup(result.getMetadataGroup());
        if(result.getPhenotypingCenter()!= null) r.setOrganisation(organisationDAO.getOrganisationByName(result.getPhenotypingCenter()));
        if(result.getParameterStableId()!= null) r.setParameter(pipelineDAO.getParameterByStableId(result.getParameterStableId()));
        if(result.getPipelineStableId()!= null) r.setPipeline(pipelineDAO.getPhenotypePipelineByStableId(result.getPipelineStableId()));
        if(result.getProjectName()!= null) r.setProject(projectDAO.getProjectByName(result.getProjectName()));
        if(result.getpValue()!= null) r.setpValue(new Double(result.getpValue()));
        r.setRawOutput(result.getRawOutput());
        if(result.getSex()!= null) r.setSexType(SexType.valueOf(result.getSex()));
        r.setStatisticalMethod(result.getStatisticalMethod());
        if(result.getZygosity()!= null) r.setZygosityType(ZygosityType.valueOf(result.getZygosity()));
        r.setCategoryA(StringUtils.join(result.getCategories(), "|"));
        if(result.getSex()!= null) r.setControlSex(SexType.valueOf(result.getSex()));
        r.setEffectSize(result.getEffectSize());
        if(result.getSex()!= null) r.setExperimentalSex(SexType.valueOf(result.getSex()));

        return r;
    }


    public GeneRowForHeatMap getResultsForGeneHeatMap(String accession, GenomicFeature gene, Map<String, Set<String>> map, String resourceName) {
    	
        GeneRowForHeatMap row = new GeneRowForHeatMap(accession);
        Map<String, HeatMapCell> paramPValueMap = new HashMap<>();
        
        if (gene != null) {
            row.setSymbol(gene.getSymbol());
        } else {
            System.err.println("error no symbol for gene " + accession);
        }
        
        for (String procedure : map.get(accession)) {
        	paramPValueMap.put(procedure, null);
        }

        SolrQuery q = new SolrQuery()
                .setQuery(StatisticalResultDTO.MARKER_ACCESSION_ID + ":\"" + accession + "\"")
                .addFilterQuery(StatisticalResultDTO.RESOURCE_NAME + ":\"" + resourceName + "\"")
                .setSort(StatisticalResultDTO.P_VALUE, SolrQuery.ORDER.asc)
                .addField(StatisticalResultDTO.PROCEDURE_STABLE_ID)
                .addField(StatisticalResultDTO.STATUS)
                .addField(StatisticalResultDTO.P_VALUE)
                .setRows(10000000);
        q.add("group", "true");
        q.add("group.field", StatisticalResultDTO.PROCEDURE_STABLE_ID);
        q.add("group.sort", StatisticalResultDTO.P_VALUE + " asc");
        
        try {
        	GroupCommand groups = solr.query(q).getGroupResponse().getValues().get(0);
            for (Group group:  groups.getValues()){
            	HeatMapCell cell = new HeatMapCell();
            	SolrDocument doc = group.getResult().get(0);
            	cell.setxAxisKey(doc.get(StatisticalResultDTO.PROCEDURE_STABLE_ID).toString());
            	if(Double.valueOf(doc.getFieldValue(StatisticalResultDTO.P_VALUE).toString()) < 0.0001){
            		cell.setStatus("Significant call");
            	} else if (doc.getFieldValue(StatisticalResultDTO.STATUS).toString().equals("Success")){
            			cell.setStatus("Data analysed, no significant call");
            		} else {
            			cell.setStatus("Could not analyse");
            		}
            	paramPValueMap.put(doc.getFieldValue(StatisticalResultDTO.PROCEDURE_STABLE_ID).toString(), cell);
            }
            row.setXAxisToCellMap(paramPValueMap);
        } catch (SolrServerException ex) {
            LOG.error(ex.getMessage());
        }
        return row;
    }
    
    public List<GeneRowForHeatMap> getSecondaryProjectMapForResource(String resourceName) {
    	
    	List<GeneRowForHeatMap> res = new ArrayList<>();    	
        HashMap<String, GeneRowForHeatMap> geneRowMap = new HashMap<>(); // <geneAcc, row>
        List<BasicBean> procedures = getProceduresForDataSource(resourceName);
        
        for (BasicBean procedure : procedures){
	        SolrQuery q = new SolrQuery()
	        .setQuery(StatisticalResultDTO.RESOURCE_NAME + ":\"" + resourceName + "\"")
	        .addFilterQuery(StatisticalResultDTO.PROCEDURE_STABLE_ID + ":" + procedure.getId())
	        .setSort(StatisticalResultDTO.P_VALUE, SolrQuery.ORDER.asc)
	        .addField(StatisticalResultDTO.PROCEDURE_STABLE_ID)
	        .addField(StatisticalResultDTO.MARKER_ACCESSION_ID)
	        .addField(StatisticalResultDTO.MARKER_SYMBOL)
	        .addField(StatisticalResultDTO.STATUS)
	        .addField(StatisticalResultDTO.P_VALUE)
	        .setRows(10000000);
	        q.add("group", "true");
	        q.add("group.field", StatisticalResultDTO.MARKER_ACCESSION_ID);
	        q.add("group.sort", StatisticalResultDTO.P_VALUE + " asc");
	
	        try {
	        	GroupCommand groups = solr.query(q).getGroupResponse().getValues().get(0);
		        		        	
		        for (Group group:  groups.getValues()){
		        	GeneRowForHeatMap row;
		            HeatMapCell cell = new HeatMapCell();
		            SolrDocument doc = group.getResult().get(0);
		        	String geneAcc = doc.get(StatisticalResultDTO.MARKER_ACCESSION_ID).toString();
		            Map<String, HeatMapCell> xAxisToCellMap = new HashMap<>();
		            
		        	if (geneRowMap.containsKey(geneAcc)){
		        		row = geneRowMap.get(geneAcc);
		        		xAxisToCellMap = row.getXAxisToCellMap();
		        	} else {
		        		row = new GeneRowForHeatMap(geneAcc);
		        		row.setSymbol(doc.get(StatisticalResultDTO.MARKER_SYMBOL).toString());
			        	xAxisToCellMap.put(procedure.getId(), null);
		        	}
		            cell.setxAxisKey(doc.get(StatisticalResultDTO.PROCEDURE_STABLE_ID).toString());
		            if(Double.valueOf(doc.getFieldValue(StatisticalResultDTO.P_VALUE).toString()) < 0.0001){
		            	cell.setStatus(HeatMapCell.THREE_I_DEVIANCE_SIGNIFICANT);
		            } else if (doc.getFieldValue(StatisticalResultDTO.STATUS).toString().equals("Success")){
		            		cell.setStatus(HeatMapCell.THREE_I_DATA_ANALYSED_NOT_SIGNIFICANT);
		            } else {
		            	cell.setStatus(HeatMapCell.THREE_I_COULD_NOT_ANALYSE);
		            }
		            xAxisToCellMap.put(doc.getFieldValue(StatisticalResultDTO.PROCEDURE_STABLE_ID).toString(), cell);
			        row.setXAxisToCellMap(xAxisToCellMap);
			        geneRowMap.put(geneAcc, row);
		            }
		        } catch (SolrServerException ex) {
		            LOG.error(ex.getMessage());
		        }
        }
        
        res = new ArrayList<>(geneRowMap.values());
        Collections.sort(res, new GeneRowForHeatMap3IComparator());
     
        return res;
    }
  
    
    public List<BasicBean> getProceduresForDataSource(String resourceName){
    	
    	List<BasicBean> res = new ArrayList();
    	SolrQuery q = new SolrQuery()
          	.setQuery(StatisticalResultDTO.RESOURCE_NAME + ":\"" + resourceName + "\"")
          	.setRows(10000);
    	q.add("group", "true");
    	q.add("group.field", StatisticalResultDTO.PROCEDURE_NAME);
    	q.add("group.rows","1");
        q.add("fl", StatisticalResultDTO.PROCEDURE_NAME + "," + StatisticalResultDTO.PROCEDURE_STABLE_ID);
    	
    	System.out.println("Procedure query " + solr.getBaseURL() + "/select?" + q);
    	
    	try {
    		GroupCommand groups = solr.query(q).getGroupResponse().getValues().get(0);
            for (Group group: groups.getValues()){
            	BasicBean bb = new BasicBean();
            	SolrDocument doc = group.getResult().get(0);
            	bb.setName(doc.getFieldValue(StatisticalResultDTO.PROCEDURE_NAME).toString());
            	bb.setId(doc.getFieldValue(StatisticalResultDTO.PROCEDURE_STABLE_ID).toString());
            	res.add(bb);
            }
        } catch (SolrServerException ex) {
            LOG.error(ex.getMessage());
        }
    	return res;
    }
     
    
    /*
	 * End of method for PhenotypeCallSummarySolrImpl
	 */
	public GeneRowForHeatMap getResultsForGeneHeatMap(String accession, GenomicFeature gene, List<BasicBean> xAxisBeans, Map<String, List<String>> geneToTopLevelMpMap) {

		GeneRowForHeatMap row = new GeneRowForHeatMap(accession);
		if (gene != null) {
			row.setSymbol(gene.getSymbol());
		} else {
			System.err.println("error no symbol for gene " + accession);
		}

		Map<String, HeatMapCell> xAxisToCellMap = new HashMap<>();
		for (BasicBean xAxisBean : xAxisBeans) {
			HeatMapCell cell = new HeatMapCell();
			if (geneToTopLevelMpMap.containsKey(accession)) {

				List<String> mps = geneToTopLevelMpMap.get(accession);
				// cell.setLabel("No Phenotype Detected");
				if (mps != null && !mps.isEmpty()) {
					if (mps.contains(xAxisBean.getId())) {
						cell.setxAxisKey(xAxisBean.getId());
						cell.setLabel("Data Available");
						cell.setStatus("Data Available");
					} else {
						cell.setStatus("No MP");
					}
				} else {
					// System.err.println("mps are null or empty");
					cell.setStatus("No MP");
				}
			} else {
				// if no doc found for the gene then no data available
				cell.setStatus("No Data Available");
			}
			xAxisToCellMap.put(xAxisBean.getId(), cell);
		}
		row.setXAxisToCellMap(xAxisToCellMap);

		return row;
	}
   

	/**
	 * This map is needed for the summary on phenotype pages (the percentages &
	 * pie chart). It takes a long time to load so it does it asynchronously.
	 * 
	 * @param sex
	 * @return Map < String parameterStableId , ArrayList<String
	 *         geneMgiIdWithParameterXMeasured>>
	 * @throws SolrServerException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @author tudose
	 */
	public Map<String, ArrayList<String>> getParameterToGeneMap(SexType sex)
	throws SolrServerException, InterruptedException, ExecutionException {

		Map<String, ArrayList<String>> res = new ConcurrentHashMap<>(); //<parameter, <genes>>
		Long time = System.currentTimeMillis();
		String pivotFacet =  StatisticalResultDTO.PARAMETER_STABLE_ID + "," + StatisticalResultDTO.MARKER_ACCESSION_ID;
		SolrQuery q = new SolrQuery().setQuery(ObservationDTO.SEX + ":" + sex.name());
		q.setFilterQueries( StatisticalResultDTO.STRAIN_ACCESSION_ID + ":\"" +
			StringUtils.join(OverviewChartsController.OVERVIEW_STRAINS, "\" OR " + ObservationDTO.STRAIN_ACCESSION_ID + ":\"") + "\"");
		q.set("facet.pivot", pivotFacet);
		q.setFacet(true);
		q.setRows(1);
		q.set("facet.limit", -1); 

		System.out.println("Solr url for getParameterToGeneMap " + solr.getBaseURL() + "/select?" + q);
		QueryResponse response = solr.query(q);
		
		for( PivotField pivot : response.getFacetPivot().get(pivotFacet)){
			ArrayList<String> genes = new ArrayList<>();
			for (PivotField gene : pivot.getPivot()){
				genes.add(gene.getValue().toString());
			}
			res.put(pivot.getValue().toString(), new ArrayList<String>(genes));
		}
		
		System.out.println("Done in " + (System.currentTimeMillis() - time));
		return res;
	}

	public void addGenesForBothSexes()
	throws SolrServerException, InterruptedException, ExecutionException {

		Long time = System.currentTimeMillis();
		String pivotFacet =  StatisticalResultDTO.PARAMETER_STABLE_ID + "," + StatisticalResultDTO.MARKER_ACCESSION_ID;
		SolrQuery q = new SolrQuery().setQuery("-" + ObservationDTO.SEX + ":*");
		q.setFilterQueries( StatisticalResultDTO.STRAIN_ACCESSION_ID + ":\"" + StringUtils.join(OverviewChartsController.OVERVIEW_STRAINS, "\" OR " + ObservationDTO.STRAIN_ACCESSION_ID + ":\"") + "\"");
		q.set("facet.pivot", pivotFacet);
		q.setFacet(true);
		q.setRows(1);
		q.set("facet.limit", -1); 
		
		QueryResponse response = solr.query(q);
		System.out.println("Solr url for getParameterToGeneMap " + solr.getBaseURL() + "/select?" + q);
		
		for( PivotField pivot : response.getFacetPivot().get(pivotFacet)){
			ArrayList<String> genes = new ArrayList<>();
			for (PivotField gene : pivot.getPivot()){
				genes.add(gene.getValue().toString());
			}
			maleParamToGene.put(pivot.getValue().toString(), new ArrayList<String>(genes));
			femaleParamToGene.put(pivot.getValue().toString(), new ArrayList<String>(genes));
		}

		System.out.println("Done in " + (System.currentTimeMillis() - time));
	}

	private void fillMaps() {
        System.out.println("Initializing ParameterToGeneMap. This will take a while...");
        try {
    		femaleParamToGene = getParameterToGeneMap(SexType.female);
    		maleParamToGene = getParameterToGeneMap(SexType.male);
    		addGenesForBothSexes();	
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public Set<String> getTestedGenes(List<String> parameters, SexType sex) {
        HashSet<String> res = new HashSet<>();
        if (femaleParamToGene == null || maleParamToGene == null) {
            fillMaps();
        }
        if (sex == null || sex.equals(SexType.female)) {
            for (String p : parameters) {
                if (femaleParamToGene.containsKey(p)) {
                    res.addAll(femaleParamToGene.get(p));
                }
            }
        }
        if (sex == null || sex.equals(SexType.male)) {
            for (String p : parameters) {
                if (maleParamToGene.containsKey(p)) {
                    res.addAll(maleParamToGene.get(p));
                }
            }
        }
        return res;
    }
    
    public List<Group> getGenesBy(String mpId, String sex)
    throws SolrServerException {
    	
		SolrQuery q = new SolrQuery().setQuery("(" + StatisticalResultDTO.MP_TERM_ID + ":\"" + mpId + "\" OR " +
				StatisticalResultDTO.TOP_LEVEL_MP_TERM_ID + ":\"" + mpId + "\" OR " + StatisticalResultDTO.INTERMEDIATE_MP_TERM_ID 
				+ ":\"" + mpId + "\")").setRows(10000);
		q.set("group.field", "" + StatisticalResultDTO.MARKER_SYMBOL);
		q.set("group", true);
		q.set("group.limit", 0);
		
		if (sex != null) {
		    q.addFilterQuery(GenotypePhenotypeDTO.SEX + ":" + sex);
		}
		QueryResponse results = solr.query(q);
		return results.getGroupResponse().getValues().get(0).getValues();
	}
}
