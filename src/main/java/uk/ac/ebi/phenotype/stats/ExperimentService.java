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
import java.util.Set;
import java.util.TreeSet;

import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.ac.ebi.phenotype.dao.PhenotypePipelineDAO;
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
		
		Map<String, ExperimentDTO> experimentsMap = new HashMap<String, ExperimentDTO>();
		
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
	    			+ observation.getGeneAccession()
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
     		
    	//	System.out.println("control mId="+experiment.getControlBiologicalModelId()+" exp mod Id="+experiment.getExperimentalBiologicalModelId());
     		if (experiment.getResults()==null && experiment.getExperimentalBiologicalModelId()!=null) {
     			
     			//for unidimensional example http://localhost:8080/PhenotypeArchive/stats/genes/MGI:97525?parameterId=GMC_906_001_016&gender=male&zygosity=homozygote
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
	    Set<String> noControls = new HashSet<String>();

	    // NOTE!!!
	    // TODO: Hom and Het probably need their own control groups
	    // because of the sliding window of control selection based on date
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
	    		
	    		Set<String> femaleBatches = new HashSet<String>();
	    		Set<String> maleBatches = new HashSet<String>();
   
	    		Date experimentDate = new Date(0L); //epoch
	    	    for (ObservationDTO o : experiment.getHeterozygoteMutants()) {
	    	    	
	    	    	// Batch grouping is defined as "collected on the same day"
	    	    	if (o.getSex().equals(SexType.female.name())) {
	    	    		femaleBatches.add(o.getDateOfExperiment().getYear() + "-" + o.getDateOfExperiment().getMonth() + "-" + o.getDateOfExperiment().getDate());
	    	    	} else {
	    	    		maleBatches.add(o.getDateOfExperiment().getYear() + "-" + o.getDateOfExperiment().getMonth() + "-" + o.getDateOfExperiment().getDate());
	    	    	}

	    	    	if(experimentOrganisationId==null){
	    	    		experimentOrganisationId = o.getPhenotypingCenterId();
	    	    	}

	    	    	if (o.getDateOfExperiment().after(experimentDate)) {
	    	    		experimentDate=o.getDateOfExperiment();	
	    	    	}
	    	    }
	    	    for (ObservationDTO o : experiment.getHomozygoteMutants()) {

	    	    	// Batch grouping is defined as "collected on the same day"
	    	    	if (o.getSex().equals(SexType.female.name())) {
	    	    		femaleBatches.add(o.getDateOfExperiment().getYear() + "-" + o.getDateOfExperiment().getMonth() + "-" + o.getDateOfExperiment().getDate());
	    	    	} else {
	    	    		maleBatches.add(o.getDateOfExperiment().getYear() + "-" + o.getDateOfExperiment().getMonth() + "-" + o.getDateOfExperiment().getDate());
	    	    	}

	    	    	if(experimentOrganisationId==null){
	    	    		experimentOrganisationId = o.getPhenotypingCenterId();
	    	    	}

	    	    	if (o.getDateOfExperiment().after(experimentDate)) {
	    	    		experimentDate=o.getDateOfExperiment();
	    	    	}
	    	    }
	    	    //added as we have above for het and hom
	    	    for (ObservationDTO o : experiment.getHemizygoteMutants()) {

	    	    	// Batch grouping is defined as "collected on the same day"
	    	    	if (o.getSex().equals(SexType.female.name())) {
	    	    		femaleBatches.add(o.getDateOfExperiment().getYear() + "-" + o.getDateOfExperiment().getMonth() + "-" + o.getDateOfExperiment().getDate());
	    	    	} else {
	    	    		maleBatches.add(o.getDateOfExperiment().getYear() + "-" + o.getDateOfExperiment().getMonth() + "-" + o.getDateOfExperiment().getDate());
	    	    	}

	    	    	if(experimentOrganisationId==null){
	    	    		experimentOrganisationId = o.getPhenotypingCenterId();
	    	    	}

	    	    	if (o.getDateOfExperiment().after(experimentDate)) {
	    	    		experimentDate=o.getDateOfExperiment();	
	    	    	}
	    	    }

	    		experiment.setControls(new HashSet<ObservationDTO>());

	    		String controlSex = null;
	    		// we want single sex controls for unidimensional data with 1 sex parameter only 

	    		if (sex != null){
	    			controlSex = sex.toString();
	    		}
	    		
	    		// ======================================
	    		// CONTROL GROUP SELECTION STRATEGY
	    		// Use concurrent controls if appropriate
	    		// else use ALL control data
	    		//
	    		// TODO: When multiple batches of mutants, check if each batch
	    		//       has accompanying controls in the same batch (i.e. on
	    		//       the same day), if so, use those controls
	    		//
	    		// TODO: Verify all the centers control strategy
	    		//
	    		// ======================================

	    		List<ObservationDTO> controls = new ArrayList<ObservationDTO>();

	    		// If one batch male, use controls from the same day as the experiment batch
	    		// else load all male controls
	    		if (experiment.getSexes().contains(SexType.male) && maleBatches.size()==1) {

	    			List<ObservationDTO> potentialControls = new ArrayList<ObservationDTO>();
	    			potentialControls = os.getConcurrentControlsBySex(parameterId, experiment.getStrain(), experimentOrganisationId, experimentDate, SexType.male.name(), experiment.getMetadataGroup());

	    			if (potentialControls.size()<MIN_CONTROLS) {
		    			potentialControls = os.getAllControlsBySex(parameterId, experiment.getStrain(), experimentOrganisationId, SexType.male.name(), experiment.getMetadataGroup());
	    			}

	    			controls.addAll(potentialControls);
	    		} else if (experiment.getSexes().contains(SexType.male) && maleBatches.size()>1) {

	    			controls.addAll(os.getAllControlsBySex(parameterId, experiment.getStrain(), experimentOrganisationId, SexType.male.name(), experiment.getMetadataGroup()));
	    		
	    		}

	    		// If one batch female, use controls from the same day as the experiment batch
	    		// else load all female controls
	    		if (experiment.getSexes().contains(SexType.female) && femaleBatches.size()==1) {
	    		
	    			List<ObservationDTO> potentialControls = new ArrayList<ObservationDTO>();
	    			potentialControls = os.getConcurrentControlsBySex(parameterId, experiment.getStrain(), experimentOrganisationId, experimentDate, SexType.female.name(), experiment.getMetadataGroup());
	    				    			
	    			if (potentialControls.size()<MIN_CONTROLS) {
		    			controls.addAll(os.getAllControlsBySex(parameterId, experiment.getStrain(), experimentOrganisationId, SexType.female.name(), experiment.getMetadataGroup()));
	    			}
	    			
	    			controls.addAll(potentialControls);
	    		
	    		} else if (experiment.getSexes().contains(SexType.female) && femaleBatches.size()>1) {
	    		
	    			controls.addAll(os.getAllControlsBySex(parameterId, experiment.getStrain(), experimentOrganisationId, SexType.female.name(), experiment.getMetadataGroup()));
	    		
	    		}
	    		
	    		//os.getControls(parameterId, experiment.getStrain(), experimentOrganisationId, experimentDate, Boolean.FALSE, SexType.female.name());

	    		// If both sexes contain multiple batches, use all control animals
	    		if (maleBatches.size()>2 && femaleBatches.size()>2) {
	    			controls = os.getAllControlsBySex(parameterId, experiment.getStrain(), experimentOrganisationId, controlSex, experiment.getMetadataGroup());
	    		}

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

	public List<ExperimentDTO> getExperimentDTO(Integer id, String acc,
			List<String> genderList, List<String> zyList, Integer phenotypingCenterId) throws SolrServerException, IOException, URISyntaxException {
		List<ExperimentDTO> experimentList=new ArrayList<ExperimentDTO>();
		
		if (genderList.isEmpty() || genderList.size()==2) {//if gender list is size 2 assume both sexes so no filter needed
			
			
			if (zyList.isEmpty() || zyList.size()==2) {//if zygosity list is size 2 then no filter needed either
				experimentList=this.getExperimentDTO(id, acc,  null, phenotypingCenterId, null, null);
			}else {
				experimentList=this.getExperimentDTO(id, acc,  null, phenotypingCenterId, zyList.get(0), null);
			}
			
		}else {
			String gender=genderList.get(0);
			if (zyList.isEmpty() || zyList.size()==2) {
				experimentList=this.getExperimentDTO(id, acc, SexType.valueOf(gender), phenotypingCenterId, null, null);
			}else {
				experimentList=this.getExperimentDTO(id, acc, SexType.valueOf(gender), phenotypingCenterId, zyList.get(0), null);
			}
			
		}
		
		return experimentList;
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
