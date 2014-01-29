package uk.ac.ebi.phenotype.stats;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle.Control;
import java.util.Set;
import java.util.TreeSet;

import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.ac.ebi.phenotype.dao.PhenotypePipelineDAO;
import uk.ac.ebi.phenotype.pojo.ControlStrategy;
import uk.ac.ebi.phenotype.pojo.ObservationType;
import uk.ac.ebi.phenotype.pojo.Parameter;
import uk.ac.ebi.phenotype.pojo.PhenotypeCallSummaryDAOReadOnly;
import uk.ac.ebi.phenotype.pojo.SexType;
import uk.ac.ebi.phenotype.pojo.ZygosityType;
import uk.ac.ebi.phenotype.stats.strategy.AllControlsStrategy;
import uk.ac.ebi.phenotype.stats.strategy.ControlSelectionStrategy;

@Service
public class ExperimentService {

	public static final Integer MIN_CONTROLS = 5;

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
	 * @param phenotypingCenterId	null for any organisation
	 * @param zygosity	 null for any zygosity	
	 * @param strain	null for any strain
	 * @param phenotypingCenter TODO
	 * @return
	 * @throws SolrServerException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public List<ExperimentDTO> getExperimentDTO(Integer parameterId, String geneAccession, SexType sex, Integer phenotypingCenterId, String zygosity, String strain) throws SolrServerException, IOException, URISyntaxException {
	
		List<ObservationDTO> results = os.getExperimentalUnidimensionalObservationsByParameterGeneAccZygosityOrganisationStrainSex(parameterId, geneAccession, zygosity, phenotypingCenterId, strain, sex);
		
		Map<String, ExperimentDTO> experimentsMap = new HashMap<>();
		
		for (ObservationDTO observation : results) {

	    	// collect all the strains, organisations, sexes, and zygosities 
	    	// combinations of the mutants to get the controls later

	    	// Experiment KEY is a combination of 
	    	// - organisation
	    	// - strain
	    	// - parameter
	    	// - gene
	    	ExperimentDTO experiment;
	    	
	    	String experimentKey = observation.getPhenotypingCenter()
	    			+ observation.getStrain()
	    			+ observation.getParameterStableId()
	    			+ observation.getGeneAccession() // TODO: should this be alleleAccession?
	    			+ observation.getMetadataGroup();

	    	if (experimentsMap.containsKey(experimentKey)) {
	    		experiment = experimentsMap.get(experimentKey);
	    	} else {
	    		experiment = new ExperimentDTO();
	    		experiment.setExperimentId(experimentKey);
	    		experiment.setObservationType(ObservationType.valueOf(observation.getObservationType()));
	    		experiment.setHomozygoteMutants(new HashSet<ObservationDTO>());
	    		experiment.setHeterozygoteMutants(new HashSet<ObservationDTO>());
	    		experiment.setHemizygoteMutants(new HashSet<ObservationDTO>());

	    		//Tree sets to keep "female" before "male" and "hetero" before "hom"
	    		experiment.setSexes(new TreeSet<SexType>());
	    		experiment.setZygosities(new TreeSet<ZygosityType>());
	    	}

	    	if (experiment.getMetadata() == null) {
	    		experiment.setMetadata(observation.getMetadata());
	    	}

	    	if (experiment.getMetadataGroup() == null) {
	    		experiment.setMetadataGroup(observation.getMetadataGroup());
	    	}

	    	if (experiment.getGeneMarker() == null) {
	    		experiment.setGeneMarker(observation.getGeneSymbol());
	    	}

	    	if (experiment.getParameterStableId() == null) {
	    		experiment.setParameterStableId(observation.getParameterStableId());
	    	}

	    	//TODO: Update to support multiple pipelines
	    	if (experiment.getPipelineStableId() == null) {
	    		experiment.setPipelineStableId(observation.getPipelineStableId());
	    	}

	    	if (experiment.getOrganisation() == null) {
	    		experiment.setOrganisation(observation.getPhenotypingCenter());
	    	}

	    	if (experiment.getStrain() == null) {
	    		experiment.setStrain(observation.getStrain());
	    	}
	    	if(experiment.getExperimentalBiologicalModelId()==null) {
	    		experiment.setExperimentalBiologicalModelId(observation.getBiologicalModelId());
	    	}

    		experiment.getZygosities().add(ZygosityType.valueOf(observation.getZygosity()));
     		experiment.getSexes().add(SexType.valueOf(observation.getSex()));

     		// TODO: include allele

     		// TODO: update to make use of the MP to result association
     		if (experiment.getResults()==null && experiment.getExperimentalBiologicalModelId()!=null) {
     			experiment.setResults( phenoDAO.getStatisticalResultFor(observation.getGeneAccession(), experiment.getParameterStableId(), ObservationType.valueOf(observation.getObservationType()), observation.getStrain()));
     		}
	    	
	    	if (ZygosityType.valueOf(observation.getZygosity()).equals(ZygosityType.heterozygote)) {
		    	experiment.getHeterozygoteMutants().add(observation);
	    	} else if (ZygosityType.valueOf(observation.getZygosity()).equals(ZygosityType.homozygote)) {
	    		experiment.getHomozygoteMutants().add(observation);
	    	} else if (ZygosityType.valueOf(observation.getZygosity()).equals(ZygosityType.hemizygote)) {
	    		experiment.getHemizygoteMutants().add(observation);
	    	}

	    	experimentsMap.put(experimentKey, experiment);

	    }

	    // Set to record the experiments that don't have control data
	    Set<String> noControls = new HashSet<>();

	    // TODO: Hom and Het probably need their own control groups
	    // TODO: Update control selection strategy based on recommendation of 
	    // stats working group

	    // TODO: Male and female mutants for UNIDIMENSIONAL PARAMETERS 
	    // must occur on the same day to be "in a same batch"
	    
	    // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
	    // CONTROL SELECTION
	    // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
	    // Loop over all the experiments for which we found mutant data
	    // to gather the control data
	    for (String key : experimentsMap.keySet()) {

	    	// If the requester filtered based on organisation, then the organisationId
	    	// parameter will not be null and we can use that, otherwise we need to
	    	// determine the organisation for this experiment and use that 
	    	
	    	Integer experimentOrganisationId = null;
	    	if(phenotypingCenterId!=null){
	    		experimentOrganisationId = phenotypingCenterId;
	    	}

	    	ExperimentDTO experiment = experimentsMap.get(key);

	    	if (experiment.getControls() == null) {
	    		
	    		experiment.setControls(new HashSet<ObservationDTO>());

	    		String controlSex = null;
	    		// we want single sex controls for unidimensional data with 1 sex parameter only 

	    		if (sex != null){
	    			controlSex = sex.toString();
	    		}
	    		
	    		// ======================================
	    		// CONTROL GROUP SELECTION STRATEGY
	    		// 
	    		// Per meeting 2014-01-21
	    		// - Categorical data
	    		//    Use all control data (broken up by metadata splits)
	    		// - Unidimensional data
	    		//    Use concurrent controls when appropriate
	    		//        Concurrent means all collected on the same day (ALL male/female controls/mutants)
	    		// 
	    		// ======================================

	    		List<ObservationDTO> controls = new ArrayList<ObservationDTO>();
	    		
	    		if (experiment.getObservationType().equals(ObservationType.categorical)) {
		    		// ======================================
		    		// CATEGORICAL CONTROL SELECTION
		    		// ======================================

	    			// Use all appropriate controls for categorical data
	    			controls.addAll(os.getAllControlsBySex(parameterId, experiment.getStrain(), experimentOrganisationId, null, sex.toString(), experiment.getMetadataGroup()));
	    			experiment.setControlSelectionStrategy(ControlStrategy.baseline_all);

	    		} else if (experiment.getObservationType().equals(ObservationType.unidimensional)) {
		    		// ======================================
		    		// UNIDIMENSIONAL CONTROL SELECTION
		    		// ======================================

		    		Set<String> allBatches = new HashSet<String>();
		    		   
		    		Date experimentDate = new Date(0L); //epoch
		    	    for (ObservationDTO o : experiment.getMutants()) {

	    	    		allBatches.add(o.getDateOfExperiment().getYear() + "-" + o.getDateOfExperiment().getMonth() + "-" + o.getDateOfExperiment().getDate());

		    	    	if(experimentOrganisationId==null){
		    	    		experimentOrganisationId = o.getPhenotypingCenterId();
		    	    	}

		    	    	if (o.getDateOfExperiment().after(experimentDate)) {
		    	    		experimentDate=o.getDateOfExperiment();	
		    	    	}

		    	    }

		    	    // If there is only 1 batch, the selection strategy is to
		    	    // use concurrent controls.  If there is more than one 
		    	    // batch, we fall back to baseline controls for the prior
		    	    // 6 months
		    	    if (allBatches.size() == 1) {

		    	    	experiment.setControlSelectionStrategy(ControlStrategy.concurrent);

		    	    } else {

		    	    	experiment.setControlSelectionStrategy(ControlStrategy.baseline_6_months);

		    	    }
		    	    
		    	    // For male and female
		    	    for (SexType s : SexType.values()) {

		    	    	if (experiment.getSexes().contains(s) && allBatches.size()==1) {
				    		// If one batch, use controls from the same day as the experiment batch
				    		// else load all male controls for 6 months previous to the date of the last
				    	    // collected mutants
		
			    			List<ObservationDTO> potentialControls = new ArrayList<ObservationDTO>();
			    			potentialControls = os.getConcurrentControlsBySex(parameterId, experiment.getStrain(), experimentOrganisationId, experimentDate, s.name(), experiment.getMetadataGroup());
		
			    			// Failed to get the required number of control animals
			    			// fall back to baseline controls
			    			if (potentialControls.size()<MIN_CONTROLS) {
				    			potentialControls = os.getAllControlsBySex(parameterId, experiment.getStrain(), experimentOrganisationId, experimentDate, s.name(), experiment.getMetadataGroup());
				    			experiment.setControlSelectionStrategy(ControlStrategy.baseline_6_months);
			    			}
		
			    			controls.addAll(potentialControls);
		
			    		} else if (experiment.getSexes().contains(s)) {
		
			    			// DEFAULT
			    			controls.addAll(os.getAllControlsBySex(parameterId, experiment.getStrain(), experimentOrganisationId, experimentDate, s.name(), experiment.getMetadataGroup()));
			    		
			    		}

		    	    }

		    		experiment.getControls().addAll(controls);
		    		
		    		if(experiment.getControlBiologicalModelId()==null && controls.size()>0) {
			    		experiment.setControlBiologicalModelId(controls.get(0).getBiologicalModelId());
			    	}
		    		
		    	    // Flag all the experiments that don't have control data
		    		if(controls.size()<1) {
		    			noControls.add(key);
		    		}
	    		} // End control selection
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
		return getExperimentDTO(parameterId, geneAccession, null, null, null, null);
	}
	
	public List<ExperimentDTO> getExperimentDTO(String parameterStableId, String geneAccession) throws SolrServerException, IOException, URISyntaxException {
		Parameter p = parameterDAO.getParameterByStableIdAndVersion(parameterStableId, 1, 0);
		return getExperimentDTO(p.getId(), geneAccession);
	}
	
	public List<ExperimentDTO> getExperimentDTO(String parameterStableId, String geneAccession, String strain) throws SolrServerException, IOException, URISyntaxException {
		Parameter p = parameterDAO.getParameterByStableIdAndVersion(parameterStableId, 1, 0);
		return getExperimentDTO(p.getId(), geneAccession, null, null, null, strain);
	}
	
	public List<ExperimentDTO> getExperimentDTO(String parameterStableId, String geneAccession, SexType sex, Integer phenotypingCenterId, String zygosity, String strain) throws SolrServerException, IOException, URISyntaxException {
		Parameter p = parameterDAO.getParameterByStableIdAndVersion(parameterStableId, 1, 0);
		return getExperimentDTO(p.getId(), geneAccession, sex, phenotypingCenterId, zygosity, strain);
	}

	/**
	 * Should only return 1 experimentDTO
	 * @param id
	 * @param acc
	 * @param genderList
	 * @param zyList
	 * @param phenotypingCenterId
	 * @param strain
	 * @return
	 * @throws SolrServerException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public List<ExperimentDTO> getSpecificExperimentDTO(Integer id, String acc,
			List<String> genderList, List<String> zyList, Integer phenotypingCenterId, String strain) throws SolrServerException, IOException, URISyntaxException {
		List<ExperimentDTO> experimentList=new ArrayList<ExperimentDTO>();
		
		if (genderList.isEmpty() || genderList.size()==2) {//if gender list is size 2 assume both sexes so no filter needed
			 
			
			if (zyList.isEmpty() || zyList.size()==2) {//if zygosity list is size 2 then no filter needed either
				experimentList=this.getExperimentDTO(id, acc,  null, phenotypingCenterId, null, strain);
			}else {
				experimentList=this.getExperimentDTO(id, acc,  null, phenotypingCenterId, zyList.get(0), strain);
			}
			
		}else {
			String gender=genderList.get(0);
			if (zyList.isEmpty() || zyList.size()==2) {
				experimentList=this.getExperimentDTO(id, acc, SexType.valueOf(gender), phenotypingCenterId, null, strain);
			}else {
				experimentList=this.getExperimentDTO(id, acc, SexType.valueOf(gender), phenotypingCenterId, zyList.get(0), strain);
			}
			
		}
		
		return experimentList;
	}
	
	
	
	public Map<String,List<String>> getExperimentKeys(String mgiAccession, String parameterStableId) throws SolrServerException{
	return 	os.getExperimentKeys(mgiAccession, parameterStableId);
	}
	
	public String getCategoryLabels (int parameterId, String category) throws SQLException{
		return parameterDAO.getCategoryDescription(parameterId, category);
	}

    /**
     * Control strategy selection based on phenotyping center and user supplied
     * strategy.
     * 
     * @param phenotypingCenter center at which the mutants were phenotyped
     * @param strategies which control selection strategy to use
     * 
     * @return an instance of a control selection strategy
     */
    public ControlSelectionStrategy getControlSelectionStrategy(String[] phenotypingCenter, String[] strategies) {
        // TODO: implement logic to get appropriate control selection strategy
        // object
        return new AllControlsStrategy();
    }
}
