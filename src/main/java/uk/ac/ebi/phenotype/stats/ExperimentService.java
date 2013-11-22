package uk.ac.ebi.phenotype.stats;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.ac.ebi.phenotype.dao.PhenotypePipelineDAO;
import uk.ac.ebi.phenotype.pojo.ObservationType;
import uk.ac.ebi.phenotype.pojo.Parameter;
import uk.ac.ebi.phenotype.pojo.SexType;
import uk.ac.ebi.phenotype.pojo.ZygosityType;
import uk.ac.ebi.phenotype.stats.categorical.CategoricalSet;
import uk.ac.ebi.phenotype.util.PhenotypeCallSummaryDAOReadOnly;

@Service
public class ExperimentService {

	@Autowired
	ObservationService os;

	@Autowired
	PhenotypePipelineDAO parameterDAO;
	
	@Autowired
	private PhenotypeCallSummaryDAOReadOnly phenoDAO;

/**
 * 
 * @param parameterId
 * @param geneAccession
 * @param sex	null for both sexes
 * @param organisationId	null for any organisation
 * @param zygosity	 null for any zygosity	
 * @param strain	null for any strain
 * @return
 * @throws SolrServerException
 * @throws IOException
 * @throws URISyntaxException
 */
	public List<ExperimentDTO> getExperimentDTO(Integer parameterId, String geneAccession, SexType sex, Integer organisationId, String zygosity, String strain) throws SolrServerException, IOException, URISyntaxException {
	
		List<ObservationDTO> results = os.getExperimentalUnidimensionalObservationsByParameterGeneAccZygosityOrganisationStrainSex(parameterId, 
				geneAccession, zygosity, organisationId, strain, sex);
		
		Map<String, ExperimentDTO> experimentsMap = new HashMap<String, ExperimentDTO>();
		
		for (ObservationDTO observation : results) {

	    	// collect all the strains, organisations, sexes, and zygosities 
	    	// combinations of the mutants to get the controls later

	    	// Experiment ID is a combination of 
	    	// - organisation
	    	// - strain
	    	// - parameter
	    	// - gene
	    	// - zygosity
	    	ExperimentDTO experiment;
	    	
	    	String experimentKey = observation.getOrganisation()
	    			+ observation.getStrain()
	    			+ observation.getParameterStableId()
	    			+ observation.getGeneAccession();

	    	if (experimentsMap.containsKey(experimentKey)) {
	    		experiment = experimentsMap.get(experimentKey);
	    	} else {
	    		experiment = new ExperimentDTO();
	    		experiment.setExperimentId(experimentKey);
	    		experiment.setObservationType(ObservationType.valueOf(observation.getObservationType()));
	    		experiment.setHomozygoteMutants(new HashSet<ObservationDTO>());
	    		experiment.setHeterozygoteMutants(new HashSet<ObservationDTO>());

	    		//Tree sets to keep "female" before "male" and "hetero" before "hom"
	    		experiment.setSexes(new TreeSet<SexType>());
	    		experiment.setZygosities(new TreeSet<ZygosityType>());
	    	}

	    	if (experiment.getGeneMarker() == null) {
	    		experiment.setGeneMarker(observation.getGeneSymbol());
	    	}

	    	if (experiment.getParameterStableId() == null) {
	    		experiment.setParameterStableId(observation.getParameterStableId());
	    	}

	    	if (experiment.getOrganisation() == null) {
	    		experiment.setOrganisation(observation.getOrganisation());
	    	}

	    	if (experiment.getStrain() == null) {
	    		experiment.setStrain(observation.getStrain());
	    	}
	    	if(experiment.getExperimentalBiologicalModelId()==null) {
	    		experiment.setExperimentalBiologicalModelId(observation.getBiologicalModelId());
	    	}

    		experiment.getZygosities().add(ZygosityType.valueOf(observation.getZygosity()));
     		experiment.getSexes().add(SexType.valueOf(observation.getSex()));
     		
    	//	System.out.println("control mId="+experiment.getControlBiologicalModelId()+" exp mod Id="+experiment.getExperimentalBiologicalModelId());
     		if (experiment.getResults()==null && experiment.getExperimentalBiologicalModelId()!=null) {
     			
     			//for unidimensional example http://localhost:8080/PhenotypeArchive/stats/genes/MGI:97525?parameterId=GMC_906_001_016&gender=male&zygosity=homozygote
     			experiment.setResults( phenoDAO.getStatisticalResultFor(observation.getGeneAccession(), experiment.getParameterStableId(), ObservationType.valueOf(observation.getObservationType()), observation.getStrain()));
     		}
	    	
	    	if (ZygosityType.valueOf(observation.getZygosity()).equals(ZygosityType.heterozygote) || ZygosityType.valueOf(observation.getZygosity()).equals(ZygosityType.hemizygote)) {
	    		// NOTE: in the stats analysis we collapse hom and hemi together
		    	experiment.getHeterozygoteMutants().add(observation);	    		
	    	} else if (ZygosityType.valueOf(observation.getZygosity()).equals(ZygosityType.homozygote)) {
	    		experiment.getHomozygoteMutants().add(observation);
	    	}

	    	experimentsMap.put(experimentKey, experiment);

	    }

	    // Set to record the experiments that don't have control data
	    Set<String> noControls = new HashSet<String>();

	    // NOTE!!!
	    // TODO: Hom and Het probably need their own control groups
	    // because of the sliding window of control selection based on date
	    for (String key : experimentsMap.keySet()) {

	    	// If the requester filtered based on organisation, then the organisationId
	    	// parameter will not be null and we can use that, otherwise we need to
	    	// determine the organisation for this experiment and use that 
	    	
	    	Integer experimentOrganisationId = null;
	    	if(organisationId!=null){
	    		experimentOrganisationId = organisationId;
	    	}

	    	ExperimentDTO experiment = experimentsMap.get(key);

	    	if (experiment.getControls() == null) {

	    	    String observationType = null;
	    		Date max = new Date(0L); //epoch
	    	    for (ObservationDTO o : experiment.getHeterozygoteMutants()) {

	    	    	if(experimentOrganisationId==null){
	    	    		experimentOrganisationId = o.getOrganisationId();
	    	    	}

	    	    	if (o.getDateOfExperiment().after(max)) {
	    	    		max=o.getDateOfExperiment();
	    	    		observationType = o.getObservationType();
	    	    	}
	    	    }
	    	    for (ObservationDTO o : experiment.getHomozygoteMutants()) {

	    	    	if(experimentOrganisationId==null){
	    	    		experimentOrganisationId = o.getOrganisationId();
	    	    	}

	    	    	if (o.getDateOfExperiment().after(max)) {
	    	    		max=o.getDateOfExperiment();
	    	    		observationType = o.getObservationType();
	    	    	}
	    	    }

	    		experiment.setControls(new HashSet<ObservationDTO>());

	    		// SHOW all the data
//    			List<ObservationDTO> controls = os.getControls(parameterId, experiment.getStrain(), organisationId, new Date(), Boolean.TRUE);
	    		String controlSex = null;
	    		// we want single sex controls for unidimensional data with 1 sex parameter only 
	    		if (observationType.equals(ObservationType.valueOf("unidimensional")) && sex != null){
	    			controlSex = sex.toString();
//	    			System.out.println("Sex : " + controlSex + ".");
	    		}
//	    		System.out.println("BEFORE GET CONTROLS: "+ parameterId + experiment.getStrain() + organisationId + max + Boolean.FALSE + controlSex);
	    		List<ObservationDTO> controls = os.getControls(parameterId, experiment.getStrain(), experimentOrganisationId, max, Boolean.FALSE, controlSex);
    			experiment.getControls().addAll(controls);
	    		
	    		if(experiment.getControlBiologicalModelId()==null && controls.size()>0) {
		    		experiment.setControlBiologicalModelId(controls.get(0).getBiologicalModelId());
		    	}
	    		
	    	    // Flag all the experiments that don't have control data
	    		if(controls.size()<1) {
	    			noControls.add(key);
	    		}
	    	}	    	
	    }

	    // had to comment this out as we have phenotype calls from the pheno summary table that are for graphs with no control data
	    //so if we take those out then there appears to be no reason to have a graph link.
//	    for(String key : noControls) {
//	    	experimentsMap.remove(key);
//	    }
	    
		return new ArrayList<ExperimentDTO>(experimentsMap.values());	
	}
	
	
	/**
	 * Method to return all the experiments for a given combination of parameter and gene organised into
	 * discrete experiments by strain, origanisation, etc.
	 * 
	 * @param parameterId
	 * @param geneAccession
	 * @return set of experiment DTOs
	 * @throws SolrServerException 
	 * @throws URISyntaxException 
	 * @throws IOException 
	 */
	public List<ExperimentDTO> getExperimentDTO(Integer parameterId, String geneAccession) throws SolrServerException, IOException, URISyntaxException {
		
//		System.out.println("EXPERIMENT DTO + " + geneAccession);

		return getExperimentDTO(parameterId, geneAccession, null, null, null, null);
		
/*	    List<ObservationDTO> results = os.getExperimentalUnidimensionalObservationsByParameterGeneAcc(parameterId, geneAccession);
	    Integer organisationId = null;
	    
	    Map<String, ExperimentDTO> experimentsMap = new HashMap<String, ExperimentDTO>();

	    for (ObservationDTO observation : results) {

	    	// collect all the strains, organisations, sexes, and zygosities 
	    	// combinations of the mutants to get the controls later

	    	// Experiment ID is a combination of 
	    	// - organisation
	    	// - strain
	    	// - parameter
	    	// - gene
	    	// - zygosity
	    	ExperimentDTO experiment;
	    	
	    	String experimentKey = observation.getOrganisation()
	    			+ observation.getStrain()
	    			+ observation.getParameterStableId()
	    			+ observation.getGeneAccession();

	    	if (experimentsMap.containsKey(experimentKey)) {
	    		experiment = experimentsMap.get(experimentKey);
	    	} else {
	    		experiment = new ExperimentDTO();
	    		experiment.setExperimentId(experimentKey);
	    		experiment.setObservationType(ObservationType.valueOf(observation.getObservationType()));
	    		experiment.setHomozygoteMutants(new HashSet<ObservationDTO>());
	    		experiment.setHeterozygoteMutants(new HashSet<ObservationDTO>());

	    		//Tree sets to keep "female" before "male" and "hetero" before "hom"
	    		experiment.setSexes(new TreeSet<SexType>());
	    		experiment.setZygosities(new TreeSet<ZygosityType>());
	    	}

	    	if(organisationId==null){
	    		organisationId = observation.getOrganisationId();
	    	}

	    	if (experiment.getGeneMarker() == null) {
	    		experiment.setGeneMarker(observation.getGeneSymbol());
	    	}

	    	if (experiment.getParameterStableId() == null) {
	    		experiment.setParameterStableId(observation.getParameterStableId());
	    	}

	    	if (experiment.getOrganisation() == null) {
	    		experiment.setOrganisation(observation.getOrganisation());
	    	}

	    	if (experiment.getStrain() == null) {
	    		experiment.setStrain(observation.getStrain());
	    	}
	    	if(experiment.getExperimentalBiologicalModelId()==null) {
	    		experiment.setExperimentalBiologicalModelId(observation.getBiologicalModelId());
	    	}

    		experiment.getZygosities().add(ZygosityType.valueOf(observation.getZygosity()));
     		experiment.getSexes().add(SexType.valueOf(observation.getSex()));
     		
    	//System.out.println("control mId="+experiment.getControlBiologicalModelId()+" exp mod Id="+experiment.getExperimentalBiologicalModelId());
     		if (experiment.getResults()==null && experiment.getExperimentalBiologicalModelId()!=null) {
     			
     			//for unidimensional example http://localhost:8080/PhenotypeArchive/stats/genes/MGI:97525?parameterId=GMC_906_001_016&gender=male&zygosity=homozygote
     			experiment.setResults( phenoDAO.getStatisticalResultFor(observation.getGeneAccession(), experiment.getParameterStableId(), ObservationType.valueOf(observation.getObservationType()), observation.getStrain()));
     		}
	    	
	    	if (ZygosityType.valueOf(observation.getZygosity()).equals(ZygosityType.heterozygote) || ZygosityType.valueOf(observation.getZygosity()).equals(ZygosityType.hemizygote)) {
	    		// NOTE: in the stats analysis we collapse hom and hemi together
		    	experiment.getHeterozygoteMutants().add(observation);	    		
	    	} else if (ZygosityType.valueOf(observation.getZygosity()).equals(ZygosityType.homozygote)) {
	    		experiment.getHomozygoteMutants().add(observation);
	    	}


	    	experimentsMap.put(experimentKey, experiment);

	    }

	    // Set to record the experiments that don't have control data
	    Set<String> noControls = new HashSet<String>();

	    // NOTE!!!
	    // TODO: Hom and Het probably need their own control groups
	    // because of the sliding window of control selection based on date
	    for (String key : experimentsMap.keySet()) {

	    	ExperimentDTO experiment = experimentsMap.get(key);

	    	if (experiment.getControls() == null) {

	    		Date max = new Date(0L); //epoch
	    	    for (ObservationDTO o : experiment.getHeterozygoteMutants()) {
	    	    	if (o.getDateOfExperiment().after(max)) {
	    	    		max=o.getDateOfExperiment();
	    	    	}
	    	    }
	    	    for (ObservationDTO o : experiment.getHomozygoteMutants()) {
	    	    	if (o.getDateOfExperiment().after(max)) {
	    	    		max=o.getDateOfExperiment();
	    	    	}
	    	    }

	    		experiment.setControls(new HashSet<ObservationDTO>());

	    		// SHOW all the data
//    			List<ObservationDTO> controls = os.getControls(parameterId, experiment.getStrain(), organisationId, new Date(), Boolean.TRUE);
	    		List<ObservationDTO> controls = os.getControls(parameterId, experiment.getStrain(), organisationId, max, Boolean.FALSE);
    			experiment.getControls().addAll(controls);
	    		
	    		if(experiment.getControlBiologicalModelId()==null && controls.size()>0) {
		    		experiment.setControlBiologicalModelId(controls.get(0).getBiologicalModelId());
		    	}
	    		
	    	    // Flag all the experiments that don't have control data
	    		if(controls.size()<1) {
	    			noControls.add(key);
	    		}
	    	}	    	
	    }

	    // had to comment this out as we have phenotype calls from the pheno summary table that are for graphs with no control data
	    //so if we take those out then there appears to be no reason to have a graph link.
//	    for(String key : noControls) {
//	    	experimentsMap.remove(key);
//	    }
	    
		return new ArrayList<ExperimentDTO>(experimentsMap.values());	
		*/
	}
	
	public List<ExperimentDTO> getExperimentDTO(String parameterStableId, String geneAccession) throws SolrServerException, IOException, URISyntaxException {
		Parameter p = parameterDAO.getParameterByStableIdAndVersion(parameterStableId, 1, 0);
		return getExperimentDTO(p.getId(), geneAccession);
	}
	
	public List<ExperimentDTO> getExperimentDTO(String parameterStableId, String geneAccession, String strain) throws SolrServerException, IOException, URISyntaxException {
		Parameter p = parameterDAO.getParameterByStableIdAndVersion(parameterStableId, 1, 0);
		return getExperimentDTO(p.getId(), geneAccession, null, null, null, strain);
	}
	
// public List<ExperimentDTO> getExperimentDTO(Integer parameterId, String geneAccession, SexType sex, Integer organisationId, String zygosity, String strain) throws SolrServerException, IOException, URISyntaxException {

	public List<ExperimentDTO> getExperimentDTO(String parameterStableId, String geneAccession, SexType sex, Integer organisationId, String zygosity, String strain) throws SolrServerException, IOException, URISyntaxException {
		Parameter p = parameterDAO.getParameterByStableIdAndVersion(parameterStableId, 1, 0);
		return getExperimentDTO(p.getId(), geneAccession, sex, organisationId, zygosity, strain);
	}

	// gets categorical data for graphs on phenotype page 
	public CategoricalSet getCategories(String parameter, ArrayList<String >genes, String biologicalSampleGroup, ArrayList<String>  strains) throws SolrServerException{
		return os.getCategories(parameter, genes, biologicalSampleGroup, strains);
	}


	public List<ExperimentDTO> getExperimentDTO(Integer id, String acc,
			List<String> genderList, List<String> zyList) throws SolrServerException, IOException, URISyntaxException {
		List<ExperimentDTO> experimentList=new ArrayList<ExperimentDTO>();
		
		if (genderList.isEmpty() || genderList.size()==2) {//if gender list is size 2 assume both sexes so no filter needed
			
			
			if (zyList.isEmpty() || zyList.size()==2) {//if zygosity list is size 2 then no filter needed either
				experimentList=this.getExperimentDTO(id, acc,  null, null, null, null);
			}else {
				experimentList=this.getExperimentDTO(id, acc,  null, null, zyList.get(0), null);
			}
			
		}else {
			String gender=genderList.get(0);
			if (zyList.isEmpty() || zyList.size()==2) {
				experimentList=this.getExperimentDTO(id, acc, SexType.valueOf(gender), null, null, null);
			}else {
				experimentList=this.getExperimentDTO(id, acc, SexType.valueOf(gender), null, zyList.get(0), null);
			}
			
		}
		
		return experimentList;
	}
	
	
}
