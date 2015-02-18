package uk.ac.ebi.phenotype.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.james.mime4j.field.Field;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.PivotField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.util.NamedList;
import org.springframework.stereotype.Service;

import uk.ac.ebi.phenotype.service.dto.ObservationDTO;

@Service
public class PhenotypeCenterService {

	private final HttpSolrServer solr;
	private final String datasourceName = "IMPC";//pipeline but takes care of things like WTSI MGP select is IMPC!
//	public PhenotypeCenterProgress(){
//		this("https://www.ebi.ac.uk/mi/impc/solr/experiment");//"http://wwwdev.ebi.ac.uk/mi/impc/dev/solr/experiment"); // default
//	}
	public PhenotypeCenterService(String baseSolrUrl){
		solr = new HttpSolrServer(baseSolrUrl);
		
	}
	
	/**
	 * Get a list of phenotyping Centers we have data for e.g. query like below
	 * http://wwwdev.ebi.ac.uk/mi/impc/dev/solr/experiment/select?q=*:*&indent=true&facet=true&facet.field=phenotyping_center&facet.mincount=1&wt=json&rows=0
	 * @return
	 * @throws SolrServerException 
	 */
	public List<String> getPhenotypeCenters() throws SolrServerException {
		
		List<String> centers=new ArrayList<>();
		SolrQuery query = new SolrQuery()
		.setQuery("*:*")
		.addFacetField(ObservationDTO.PHENOTYPING_CENTER)
		.setFacetMinCount(1)
		.setRows(0);
		if(solr.getBaseURL().endsWith("experiment")){
			query.addFilterQuery(ObservationDTO.DATASOURCE_NAME+":"+"\""+datasourceName+"\"");
		}
		
		QueryResponse response = solr.query(query);
		//String resp = response.getResponse().toString();
		List<FacetField> fields = response.getFacetFields();
		//System.out.println("values="+fields.get(0).getValues());
		for(Count values: fields.get(0).getValues()){
			centers.add(values.getName());
		}
		//System.out.println("resp="+resp);
		return centers;
	}
	
	/**
	 * get the strains with data for a center
	 * http://wwwdev.ebi.ac.uk/mi/impc/dev/solr/experiment/select?q=phenotyping_center:%22UC%20Davis%22&wt=json&indent=true&facet=true&facet.field=strain_accession_id&facet.mincount=1&rows=0
	 * @return
	 * @throws SolrServerException
	 */
	public List<String> getStrainsForCenter(String center)  throws SolrServerException {
		
		List<String> strains=new ArrayList<>();
		SolrQuery query = new SolrQuery()
		.setQuery(ObservationDTO.PHENOTYPING_CENTER + ":\"" + center + "\"")
		.addFacetField(ObservationDTO.COLONY_ID)
		.setFacetMinCount(1)
		.setFacetLimit(-1)
		.setRows(0);
		if(solr.getBaseURL().endsWith("experiment")){
				query.addFilterQuery(ObservationDTO.DATASOURCE_NAME + ":" + "\"" + datasourceName + "\"");
		}
		QueryResponse response = solr.query(query);
		List<FacetField> fields = response.getFacetFields();
		for(Count values: fields.get(0).getValues()){
			strains.add(values.getName());
		}
		System.out.println("getStrainsForCenter ---- " + solr.getBaseURL() + "/select?" + query);
		return strains;
	}
	
	public List<String> getMutantStrainsForCenter(String center)  throws SolrServerException {
			
		List<String> strains=new ArrayList<>();
		SolrQuery query = new SolrQuery()
		.setQuery(ObservationDTO.PHENOTYPING_CENTER + ":\"" + center + "\" AND " + ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":experimental")
		.addFacetField(ObservationDTO.COLONY_ID)
		.setFacetMinCount(1)
		.setFacetLimit(-1)
		.setRows(0);
		if(solr.getBaseURL().endsWith("experiment")){
				query.addFilterQuery(ObservationDTO.DATASOURCE_NAME + ":" + "\"" + datasourceName + "\"");
		}
		QueryResponse response = solr.query(query);
		List<FacetField> fields = response.getFacetFields();
		for(Count values: fields.get(0).getValues()){
			strains.add(values.getName());
		}
		System.out.println("getStrainsForCenter ---- " + solr.getBaseURL() + "/select?" + query);
		return strains;
	}
	
	/**
	 * get the list of procedures per strain for the center
	 * http://wwwdev.ebi.ac.uk/mi/impc/dev/solr/experiment/select?q=strain_accession_id:%22MGI:2164831%22&fq=phenotyping_center:%22UC%20Davis%22&wt=json&indent=true&facet=true&facet.field=procedure_name&facet.mincount=1&rows=0
	 *@param center
	 * @param strain
	 * @return
	 * @throws SolrServerException 
	 */
	public List<ProcedureBean> getProceduresPerStrainForCenter(String center,String strain) throws SolrServerException {
		
		List<ProcedureBean> procedures=new ArrayList<>();
		SolrQuery query = new SolrQuery()
		 .setQuery(ObservationDTO.COLONY_ID+":\""+strain+"\"")
		 .addFilterQuery(ObservationDTO.PHENOTYPING_CENTER+":\""+center+"\"")
		 .addFacetField(ObservationDTO.PROCEDURE_NAME)
		 .addFacetField(ObservationDTO.PROCEDURE_STABLE_ID)
		 .setFacetMinCount(1)
		 .setRows(0);
		
		if(solr.getBaseURL().endsWith("experiment")){
			query.addFilterQuery(ObservationDTO.DATASOURCE_NAME+":"+"\""+datasourceName+"\"");
		}
		
		QueryResponse response = solr.query(query);
		List<FacetField> fields = response.getFacetFields();		
		for(int i=0; i< fields.get(0).getValues().size(); i++){
			procedures.add(new ProcedureBean(fields.get(0).getValues().get(i).getName(), fields.get(1).getValues().get(i).getName()));
		}
		
		return procedures;
	}
	
	/**
	 * @author tudose
	 * @return
	 * @throws SolrServerException
	 */
	public Map<String, List<String>> getProceduresPerCenter() throws SolrServerException{
		
		SolrQuery query = new SolrQuery()
		 .setQuery(ObservationDTO.DATASOURCE_NAME + ":IMPC")
		 .setFacet(true)
		 .setFacetLimit(-1)
		 .addFacetPivotField(ObservationDTO.PHENOTYPING_CENTER + "," + ObservationDTO.PROCEDURE_STABLE_ID)
		 .setFacetMinCount(1)
		 .setRows(0);
		QueryResponse response = solr.query(query);
		Map<String, List<String>> res = new HashMap<>();
		List<PivotField> fields = response.getFacetPivot().get(ObservationDTO.PHENOTYPING_CENTER + "," + ObservationDTO.PROCEDURE_STABLE_ID);
		
		for (PivotField facet: fields){
			List<String> proceduresList = new ArrayList<>();
			String center = facet.getValue().toString();
			List<PivotField> procedures = facet.getPivot();
			for (PivotField procedure : procedures){
				proceduresList.add(procedure.getValue().toString());
			}
			res.put(center, proceduresList);
		}
		return res;
	}
	
	
	public List<String> getDoneProcedureIdsPerStrainAndCenter(String center,String strain) throws SolrServerException {
		
		List<String> procedures=new ArrayList<>();
		SolrQuery query = new SolrQuery()
		 .setQuery(ObservationDTO.COLONY_ID+":\""+strain+"\" AND " + ObservationDTO.DATASOURCE_NAME + ":IMPC")
		 .addFilterQuery(ObservationDTO.PHENOTYPING_CENTER+":\""+center+"\"")
		 .addFacetField(ObservationDTO.PROCEDURE_STABLE_ID)
		 .setFacetLimit(-1)
		 .setFacetMinCount(1)
		 .setRows(0);
				
		QueryResponse response = solr.query(query);
		List<FacetField> fields = response.getFacetFields();		
		for( Count field: fields.get(0).getValues()){
			procedures.add(field.getName());
		}
		
		return procedures;
	}	
	
	/**
	 * Uses the methods in this service to get center progress information for each center i.e. procedures we have data for on a per strain basis
	 * @return
	 * @throws SolrServerException 
	 */
	public Map<String, Map<String, List<ProcedureBean>>> getCentersProgressInformation() throws SolrServerException {
		
		//map of centers to a map of strain to procedures list
		Map<String,Map<String, List<ProcedureBean>>> centerData = new HashMap<>();
		List<String> centers = this.getPhenotypeCenters();
		
		for(String center:centers){	
			List<String> strains = this.getStrainsForCenter(center);
			Map<String,List<ProcedureBean>> strainsToProcedures = new HashMap<>();
			
			for(String strain:strains){
				List<ProcedureBean> procedures = this.getProceduresPerStrainForCenter(center, strain);
				strainsToProcedures.put(strain, procedures);
			}
			centerData.put(center, strainsToProcedures);
		}
		return centerData;
	}	
	
	
	/**
	 * @author tudose
	 * @return
	 * @throws SolrServerException
	 */
	public List<String[]> getCentersProgressByStrainCsv() throws SolrServerException {
		
		List<String> centers = getPhenotypeCenters();
        List<String[]> results = new ArrayList<>();
        String[] temp = new String[1];
        List<String> header = new ArrayList<>();
        header.add("colonyId");
        header.add("phenotypingCenter");
        header.add("percenageDone");
        header.add("numberOfDoneProcedures");
        header.add("doneProcedures");
        header.add("numberOfMissingProcedures");
        header.add("missingProcedures");
		results.add(header.toArray(temp));
        
		Map<String, List<String>> possibleProceduresPerCenter = getProceduresPerCenter();
		
		for(String center: centers){	
			List<String> strains = getMutantStrainsForCenter(center);
			Map<String,List<ProcedureBean>> strainsToProcedures = new HashMap<>();
						
			for(String colonyId: strains){
				List<String> procedures = getDoneProcedureIdsPerStrainAndCenter(center, colonyId);
				List<String> row = new ArrayList<>();
				row.add(colonyId);
				row.add(center);
				Float percentageDone = (float) ((procedures.size() * 100) / (float)possibleProceduresPerCenter.get(center).size()); 
				row.add(percentageDone.toString());
				row.add("" + procedures.size()); // #procedures done
				row.add(procedures.toString()); // procedures done
				row.add("" + (possibleProceduresPerCenter.get(center).size() - procedures.size()));	// #missing procedures
				List<String> missing = new ArrayList<>(possibleProceduresPerCenter.get(center));
				missing.removeAll(procedures); // missing procedures
				row.add(missing.toString());
				results.add(row.toArray(temp));
			}
		}
		return results;
	}
	
}
