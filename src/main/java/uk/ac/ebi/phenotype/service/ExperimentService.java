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

import org.apache.solr.client.solrj.SolrServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.phenotype.chart.FertilityDTO;
import uk.ac.ebi.phenotype.chart.ViabilityDTO;
import uk.ac.ebi.phenotype.dao.PhenotypePipelineDAO;
import uk.ac.ebi.phenotype.dao.UnidimensionalStatisticsDAO;
import uk.ac.ebi.phenotype.error.SpecificExperimentException;
import uk.ac.ebi.phenotype.pojo.*;
import uk.ac.ebi.phenotype.service.dto.ExperimentDTO;
import uk.ac.ebi.phenotype.service.dto.ObservationDTO;
import uk.ac.ebi.phenotype.stats.strategy.AllControlsStrategy;
import uk.ac.ebi.phenotype.stats.strategy.ControlSelectionStrategy;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.*;

@Service
public class ExperimentService {

    private static final Logger LOG = LoggerFactory.getLogger(ExperimentService.class);

    public static final Integer MIN_CONTROLS = 6;

    @Autowired
    ObservationService os;

    @Autowired
    PhenotypePipelineDAO parameterDAO;

    @Autowired
    private PhenotypeCallSummarySolr phenoDAO;

    @Autowired
    private UnidimensionalStatisticsDAO unidimensionalStatisticsDAO;
    
//    @Autowired
//    private StatisticalResultDAO statisticalResultDAO;
    
    @Autowired
    private StatisticalResultService statisticalResultService;


    public List<ExperimentDTO> getExperimentDTO(Integer parameterId, Integer pipelineId, String geneAccession, SexType sex, Integer phenotypingCenterId, List<String> zygosity, String strain) throws SolrServerException, IOException, URISyntaxException {
        return getExperimentDTO(parameterId, pipelineId, geneAccession, sex, phenotypingCenterId, zygosity, strain, null, Boolean.TRUE, null);
    }

    /**
     * 
     * @param parameterId
     * @param geneAccession
     * @param sex
     *            null for both sexes
     * @param phenotypingCenterId
     *            null for any organisation
     * @param zygosities
     *            null for any zygosity
     * @param strain
     *            null for any strain
     * @param phenotypingCenterId
     *            The database identifier of the center
     * @return list of experiment objects
     * @throws SolrServerException
     * @throws IOException
     * @throws URISyntaxException
     */

    public List<ExperimentDTO> getExperimentDTO(Integer parameterId, Integer pipelineId, String geneAccession, 
    SexType sex, Integer phenotypingCenterId, List<String> zygosities, String strain, String metaDataGroup, 
    Boolean includeResults, String alleleAccession) 
    throws SolrServerException, IOException, URISyntaxException {

        LOG.debug("metadataGroup parmeter is=" + metaDataGroup);

        List<ObservationDTO> observations = os.getExperimentObservationsBy(parameterId, pipelineId, geneAccession, zygosities, phenotypingCenterId, strain, sex, metaDataGroup, alleleAccession);
        Map<String, ExperimentDTO> experimentsMap = new HashMap<>();

        for (ObservationDTO observation : observations) {

            // collect all the strains, organisations, sexes, and zygosities
            // combinations of the mutants to get the controls later

            // Experiment KEY is a combination of
            // - organisation
            // - strain
            // - parameter
            // - pipeline
            // - gene
            // - meatdata group
            ExperimentDTO experiment;

            String experimentKey = observation.getKey();
            
            if (experimentsMap.containsKey(experimentKey)) {
                experiment = experimentsMap.get(experimentKey);
            } else {
                experiment = new ExperimentDTO();
                experiment.setExperimentId(experimentKey);
                experiment.setObservationType(ObservationType.valueOf(observation.getObservationType()));
                experiment.setHomozygoteMutants(new HashSet<ObservationDTO>());
                experiment.setHeterozygoteMutants(new HashSet<ObservationDTO>());
                experiment.setHemizygoteMutants(new HashSet<ObservationDTO>());

                // Tree sets to keep "female" before "male" and "hetero" before
                // "hom"
                experiment.setSexes(new TreeSet<SexType>());
                experiment.setZygosities(new TreeSet<ZygosityType>());
            }

            if (experiment.getMetadata() == null) {
                experiment.setMetadata(observation.getMetadata());
            }

            if (experiment.getMetadataGroup() == null) {
                // LOG.debug("metaDataGroup in observation="+observation.getMetadataGroup());
                experiment.setMetadataGroup(observation.getMetadataGroup());
            }

            if (experiment.getGeneMarker() == null) {
                experiment.setGeneMarker(observation.getGeneSymbol());
            }

            if (experiment.getParameterStableId() == null) {
                experiment.setParameterStableId(observation.getParameterStableId());
            }

            if (experiment.getPipelineStableId() == null) {
                LOG.debug("setting pipelinestabl=" + observation.getPipelineStableId());
                experiment.setPipelineStableId(observation.getPipelineStableId());
            }

            if (experiment.getOrganisation() == null) {
                experiment.setOrganisation(observation.getPhenotypingCenter());
            }

            if (experiment.getStrain() == null) {
                experiment.setStrain(observation.getStrain());
            }
            if (experiment.getExperimentalBiologicalModelId() == null) {
                experiment.setExperimentalBiologicalModelId(observation.getBiologicalModelId());
            }
             if (experiment.getAlleleAccession() == null) {
                experiment.setAlleleAccession(observation.getAlleleAccession());
            }
             

            experiment.getZygosities().add(ZygosityType.valueOf(observation.getZygosity()));
            experiment.getSexes().add(SexType.valueOf(observation.getSex()));

            // includeResults variable skips the results when gathering
            // experiments for calculating the results (performance)
            if (experiment.getResults() == null && experiment.getExperimentalBiologicalModelId() != null && includeResults) {
                
                String phenotypingCenter = observation.getPhenotypingCenter();
                String parameterStableId = observation.getParameterStableId();
                String pipelineStableId = observation.getPipelineStableId();
                ObservationType statisticalType = experiment.getObservationType();
                ZygosityType zygosity = ZygosityType.valueOf(observation.getZygosity());

                List<? extends StatisticalResult> results = statisticalResultService.getStatisticalResult(alleleAccession, strain, phenotypingCenter, pipelineStableId, parameterStableId, metaDataGroup, zygosity, sex, statisticalType);
                System.out.println("results="+results);
                experiment.setResults(results);

            }

            if (ZygosityType.valueOf(observation.getZygosity()).equals(ZygosityType.heterozygote)) {
                experiment.getHeterozygoteMutants().add(observation);
            } else if (ZygosityType.valueOf(observation.getZygosity()).equals(ZygosityType.homozygote)) {
                experiment.getHomozygoteMutants().add(observation);
            } else if (ZygosityType.valueOf(observation.getZygosity()).equals(ZygosityType.hemizygote)) {
                experiment.getHemizygoteMutants().add(observation);
            }

            experiment.setProcedureStableId(observation.getProcedureStableId());
            experiment.setProcedureName(observation.getProcedureName());
            
            experimentsMap.put(experimentKey, experiment);

        }
        
        // Set to record the experiments that don't have control data
        Set<String> noControls = new HashSet<>();

        // TODO: Update control selection strategy based on recommendation of
        // stats working group

        // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
        // CONTROL SELECTION
        // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
        // Loop over all the experiments for which we found mutant data
        // to gather the control data
        for (String key : experimentsMap.keySet()) {

            // If the requester filtered based on organisation, then the
            // organisationId parameter will not be null and we can use that,
            // otherwise we need to determine the organisation for this
            // experiment and use that

            Integer experimentOrganisationId = null;
            if (phenotypingCenterId != null) {
                experimentOrganisationId = phenotypingCenterId;
            }

            ExperimentDTO experiment = experimentsMap.get(key);

            if (experiment.getControls() == null) {

                experiment.setControls(new HashSet<ObservationDTO>());

                // ======================================
                // CONTROL GROUP SELECTION STRATEGY
                //
                // Per meeting 2014-01-21
                // - Categorical data
                // Use all control data (broken up by metadata splits)
                // - Unidimensional data
                // Use concurrent controls when appropriate
                // Concurrent means all collected on the same day (ALL
                // male/female controls/mutants)
                //
                // Per discussion with Terry 2014-03-24
                // - Categorical data
                // No change
                // - Unidimensional data
                // Use concurrent controls when appropriate
                // Concurrent means all collected on the same day (ALL
                // male/female controls/mutants)
                // Else, use all appropriate data for controls
                // ======================================

                List<ObservationDTO> controls = new ArrayList<ObservationDTO>();

                if (experiment.getObservationType().equals(ObservationType.categorical)) {
                    // ======================================
                    // CATEGORICAL CONTROL SELECTION
                    // ======================================

                    // Use all appropriate controls for categorical data
                    experiment.setControlSelectionStrategy(ControlStrategy.baseline_all);

                    if (experiment.getSexes() != null) {

                        for (SexType s : SexType.values()) {
                            if (!experiment.getSexes().contains(s)) {
                                continue;
                            }

                            controls.addAll(os.getAllControlsBySex(parameterId, experiment.getStrain(), experimentOrganisationId, null, s.toString(), experiment.getMetadataGroup()));

                        }

                    } else {

                        controls.addAll(os.getAllControlsBySex(parameterId, experiment.getStrain(), experimentOrganisationId, null, null, experiment.getMetadataGroup()));

                    }

                } else {
                    // ======================================
                    // UNIDIMENSIONAL/TIMESERIES CONTROL SELECTION
                    // ======================================

                    // Default to using all controls
                    experiment.setControlSelectionStrategy(ControlStrategy.baseline_all);

                    // Find the dates of the experiments
                    Set<String> allBatches = new HashSet<String>();
                    Date experimentDate = new Date(0L);
                    for (ObservationDTO o : experiment.getMutants()) {

                        allBatches.add(o.getDateOfExperiment().getYear() + "-" + o.getDateOfExperiment().getMonth() + "-" + o.getDateOfExperiment().getDate());

                        if (experimentOrganisationId == null) {
                            experimentOrganisationId = o.getPhenotypingCenterId();
                        }

                        if (o.getDateOfExperiment().after(experimentDate)) {
                            experimentDate = o.getDateOfExperiment();
                        }

                    }
                    LOG.info("Number of batches: " + allBatches.size());

                    // If there is only 1 batch, the selection strategy is to
                    // try to use concurrent controls. If there is more than one
                    // batch, we default to all controls
                    experiment.setControlSelectionStrategy(ControlStrategy.baseline_all);

                    //
                    // If one sex specified
                    //
                    if (experiment.getSexes() != null && experiment.getSexes().size() < 2) {

                        for (SexType s : SexType.values()) {

                            if (!experiment.getSexes().contains(s)) {
                                continue;
                            }

                            if (allBatches.size() == 1) {

                                // If we have enough control data in the same
                                // batch (same day) for this sex, then use
                                // concurrent controls

                                List<ObservationDTO> potentialControls = os.getConcurrentControlsBySex(parameterId, experiment.getStrain(), experimentOrganisationId, experimentDate, s.name(), experiment.getMetadataGroup());
                                LOG.info("Number of potential controls for sex: " + s.name() + ": " + potentialControls.size());

                                if (potentialControls.size() >= MIN_CONTROLS) {

                                    controls = potentialControls;
                                    experiment.setControlSelectionStrategy(ControlStrategy.concurrent);
                                    LOG.info("Setting concurrent controls for sex: " + s.name());

                                } else {

                                    controls = os.getAllControlsBySex(parameterId, experiment.getStrain(), experimentOrganisationId, null, s.name(), experiment.getMetadataGroup());
                                    LOG.info("Using baseline controls for sex: " + s.name() + ", num controls: " + controls.size());

                                }

                            } else {

                                controls = os.getAllControlsBySex(parameterId, experiment.getStrain(), experimentOrganisationId, null, s.name(), experiment.getMetadataGroup());
                                LOG.info("Using baseline controls for sex: " + s.name() + ", num controls: " + controls.size());
                            }
                        }

                    } else {

                        //
                        // Processing both sexes
                        //

                        if (allBatches.size() == 1) {

                            List<ObservationDTO> potentialMaleControls = os.getConcurrentControlsBySex(parameterId, experiment.getStrain(), experimentOrganisationId, experimentDate, SexType.male.name(), experiment.getMetadataGroup());
                            List<ObservationDTO> potentialFemaleControls = os.getConcurrentControlsBySex(parameterId, experiment.getStrain(), experimentOrganisationId, experimentDate, SexType.female.name(), experiment.getMetadataGroup());

                            LOG.info("Number of potential controls for males: " + potentialMaleControls.size());
                            LOG.info("Number of potential controls for females: " + potentialFemaleControls.size());

                            // Only if BOTH counts of male and
                            // female controls are equal or more than
                            // MIN_CONTROLS
                            // do we do concurrent controls

                            if (potentialMaleControls.size() >= MIN_CONTROLS && potentialFemaleControls.size() >= MIN_CONTROLS) {

                                controls = potentialMaleControls;
                                controls.addAll(potentialFemaleControls);
                                experiment.setControlSelectionStrategy(ControlStrategy.concurrent);
                                LOG.info("Setting concurrent controls");

                            } else {

                                controls = os.getAllControlsBySex(parameterId, experiment.getStrain(), experimentOrganisationId, null, null, experiment.getMetadataGroup());
                                LOG.info("Using baseline controls, num controls: " + controls.size());

                            }

                        } else {

                            controls = os.getAllControlsBySex(parameterId, experiment.getStrain(), experimentOrganisationId, null, null, experiment.getMetadataGroup());
                            LOG.info("Using baseline controls, num controls: " + controls.size());
                        }

                    }

                } // End control selection
                
                experiment.getControls().addAll(controls);

                if (experiment.getControlBiologicalModelId() == null && controls.size() > 0) {
                    experiment.setControlBiologicalModelId(controls.get(0).getBiologicalModelId());
                }

                // Flag all the experiments that don't have control data
                if (controls.size() < 1) {
                    noControls.add(key);
                }
            }
        }

        // had to comment this out as we have phenotype calls from the pheno
        // summary table that are for graphs with no control data
        // so if we take those out then there appears to be no reason to have a
        // graph link.
        // for(String key : noControls) {
        // experimentsMap.remove(key);
        // }

        return new ArrayList<ExperimentDTO>(experimentsMap.values());
    }

    /**
     * Method to return all the experiments for a given combination of parameter
     * and gene organised into discrete experiments by strain, origanisation,
     * etc.
     * 
     * @param parameterId
     * @param geneAccession
     * @return set of experiment DTOs
     * @throws SolrServerException
     * @throws URISyntaxException
     * @throws IOException
     */
    public List<ExperimentDTO> getExperimentDTO(Integer parameterId, Integer pipelineId, String geneAccession) throws SolrServerException, IOException, URISyntaxException {
        return getExperimentDTO(parameterId, pipelineId, geneAccession, null, null, null, null);
    }

    public List<ExperimentDTO> getExperimentDTO(String parameterStableId, Integer pipelineId, String geneAccession) throws SolrServerException, IOException, URISyntaxException {
        Parameter p = parameterDAO.getParameterByStableId(parameterStableId);
        return getExperimentDTO(p.getId(), pipelineId, geneAccession);
    }

    public List<ExperimentDTO> getExperimentDTO(String parameterStableId, Integer pipelineId, String geneAccession, String strain) throws SolrServerException, IOException, URISyntaxException {
        Parameter p = parameterDAO.getParameterByStableId(parameterStableId);
        return getExperimentDTO(p.getId(), pipelineId, geneAccession, null, null, null, strain);
    }

    public List<ExperimentDTO> getExperimentDTO(String parameterStableId, Integer pipelineId, String geneAccession, SexType sex, Integer phenotypingCenterId, List<String> zygosity, String strain) throws SolrServerException, IOException, URISyntaxException {
        Parameter p = parameterDAO.getParameterByStableId(parameterStableId);
        LOG.debug("--- getting p for : " + parameterStableId);
        return getExperimentDTO(p.getId(), pipelineId, geneAccession, sex, phenotypingCenterId, zygosity, strain);
    }
    
    /**
     * Should only return 1 experimentDTO - returns null if none and exception
     * if more than 1 - used by ajax charts
     * 
     * @param parameterId
     * @param acc
     * @param phenotypingCenterId
     * @param strain
     * @param metadataGroup
     * @return
     * @throws SolrServerException
     * @throws IOException
     * @throws URISyntaxException
     * @throws SpecificExperimentException
     */
    public ViabilityDTO getSpecificViabilityExperimentDTO(Integer parameterId, Integer pipelineId, String acc, Integer phenotypingCenterId, String strain, String metadataGroup, String alleleAccession) throws SolrServerException, IOException, URISyntaxException, SpecificExperimentException {
        ViabilityDTO viabilityDTO=new ViabilityDTO();
        Map<String, ObservationDTO> paramStableIdToObservation = new HashMap<>();
            //for viability we don't need to filter on Sex or Zygosity
        List<ObservationDTO> observations = os.getExperimentObservationsBy(parameterId, pipelineId, acc, null, phenotypingCenterId, strain, null, metadataGroup, alleleAccession);
        ObservationDTO outcomeObservation = observations.get(0);  
        System.out.println("specific outcome="+observations);
           System.out.println("category of observation="+outcomeObservation.getCategory());
       viabilityDTO.setCategory(observations.get(0).getCategory());
       for(int i=3;i<15; i++){
    	   String formatted = String.format("%02d",i);
           System.out.println("Number with leading zeros: " + formatted);
    	   String param="IMPC_VIA_0"+formatted+"_001";
           List<ObservationDTO> observationsForCounts = os.getViabilityData(param, pipelineId, acc, null, phenotypingCenterId, strain, null, metadataGroup, alleleAccession);
           if(observationsForCounts.size()>1){
        	   System.err.println("More than one observation found for a viability request!!!");
           }
           System.out.println("vai param name="+observationsForCounts.get(0).getParameterName());
           System.out.println("via data_point="+observationsForCounts.get(0).getDataPoint());
           paramStableIdToObservation.put(param,observationsForCounts.get(0));
       }
       viabilityDTO.setParamStableIdToObservation(paramStableIdToObservation);
        return viabilityDTO;
    }
    
    public FertilityDTO getSpecificFertilityExperimentDTO(Integer parameterId, Integer pipelineId, String acc, Integer phenotypingCenterId, String strain, String metadataGroup, String alleleAccession) throws SolrServerException, IOException, URISyntaxException, SpecificExperimentException {
    	FertilityDTO fertilityDTO=new FertilityDTO();
        Map<String, ObservationDTO> paramStableIdToObservation = new HashMap<>();
            //for viability we don't need to filter on Sex or Zygosity
        List<ObservationDTO> observations = os.getExperimentObservationsBy(parameterId, pipelineId, acc, null, phenotypingCenterId, strain, null, metadataGroup, alleleAccession);
        ObservationDTO outcomeObservation = observations.get(0);  
        System.out.println("specific outcome="+observations);
           System.out.println("category of observation="+outcomeObservation.getCategory());
           fertilityDTO.setCategory(observations.get(0).getCategory());
       for(int i=1;i<14; i++){
    	   String formatted = String.format("%02d",i);
           System.out.println("Number with leading zeros: " + formatted);
    	   String param="IMPC_FER_0"+formatted+"_001";
    	   System.out.println("fert param="+param);
           List<ObservationDTO> observationsForCounts = os.getViabilityData(param, pipelineId, acc, null, phenotypingCenterId, strain, null, metadataGroup, alleleAccession);
           if(observationsForCounts.size()>1){
        	   System.err.println("More than one observation found for a viability request!!!");
           }
           if(observationsForCounts.size()>0){
           System.out.println("vai param name="+observationsForCounts.get(0).getParameterName());
           System.out.println("via data_point="+observationsForCounts.get(0).getDataPoint());
           paramStableIdToObservation.put(param,observationsForCounts.get(0));
           }
           
       }
       //do for "IMPC_FER_019_001" Gross findings female 
       List<ObservationDTO> observationsForCounts = os.getViabilityData("IMPC_FER_019_001", pipelineId, acc, null, phenotypingCenterId, strain, null, metadataGroup, alleleAccession);
       if(observationsForCounts.size()>0){
    	   System.out.println("vai param name="+observationsForCounts.get(0).getParameterName());
           System.out.println("via data_point="+observationsForCounts.get(0).getDataPoint());
    	   paramStableIdToObservation.put("IMPC_FER_019_001",observationsForCounts.get(0));
       }
       
       fertilityDTO.setParamStableIdToObservation(paramStableIdToObservation);
        return fertilityDTO;
    }

   	/**
     * Should only return 1 experimentDTO - returns null if none and exception
     * if more than 1 - used by ajax charts
     * 
     * @param id
     * @param acc
     * @param genderList
     * @param zyList
     * @param phenotypingCenterId
     * @param strain
     * @param metadataGroup
     * @return
     * @throws SolrServerException
     * @throws IOException
     * @throws URISyntaxException
     * @throws SpecificExperimentException
     */
    public ExperimentDTO getSpecificExperimentDTO(Integer id, Integer pipelineId, String acc, List<String> genderList, List<String> zyList, Integer phenotypingCenterId, String strain, String metadataGroup, String alleleAccession) throws SolrServerException, IOException, URISyntaxException, SpecificExperimentException {
        
    	List<ExperimentDTO> experimentList = new ArrayList<>();
        boolean includeResults = true;
               
        // if gender list is size 2 assume both sexes so no filter needed
        if (genderList.isEmpty() || genderList.size() == 2) {

            // if zygosity list is size 3 then no filter needed either
            if (zyList.isEmpty() || zyList.size() == 3) {
                experimentList = this.getExperimentDTO(id, pipelineId, acc, null, phenotypingCenterId, null, strain, metadataGroup, includeResults, alleleAccession);
            } else {
                experimentList = this.getExperimentDTO(id, pipelineId, acc, null, phenotypingCenterId, zyList, strain, metadataGroup, includeResults, alleleAccession);
            }

        } else {
            String gender = genderList.get(0);
            if (zyList.isEmpty() || zyList.size() == 3) {
                experimentList = this.getExperimentDTO(id, pipelineId, acc, SexType.valueOf(gender), phenotypingCenterId, null, strain, metadataGroup, includeResults, alleleAccession);
            } else {
                experimentList = this.getExperimentDTO(id, pipelineId, acc, SexType.valueOf(gender), phenotypingCenterId, zyList, strain, metadataGroup, includeResults, alleleAccession);
            }

        }
        if (experimentList.isEmpty()) {
            return null;// return null if no experiments
        }
        if (experimentList.size() > 1) {
            throw new SpecificExperimentException("Too many experiments returned - should only be one from this method call");
        }
        return experimentList.get(0);
    }

    public Map<String, List<String>> getExperimentKeys(String mgiAccession, String parameterStableIds, List<String> pipelineStableIds, List<String> phenotypingCenter, List<String> strain, List<String> metaDataGroup, List<String> alleleAccession) throws SolrServerException {
        return os.getExperimentKeys(mgiAccession, parameterStableIds, pipelineStableIds, phenotypingCenter, strain, metaDataGroup, alleleAccession);
    }

    public String getCategoryLabels(int parameterId, String category) throws SQLException {
        return parameterDAO.getCategoryDescription(parameterId, category);
    }

    /**
     * Control strategy selection based on phenotyping center and user supplied
     * strategy.
     * 
     * @param phenotypingCenter
     *            center at which the mutants were phenotyped
     * @param strategies
     *            which control selection strategy to use
     * 
     * @return an instance of a control selection strategy
     */
    public ControlSelectionStrategy getControlSelectionStrategy(String[] phenotypingCenter, String[] strategies) {
        // TODO: implement logic to get appropriate control selection strategy
        // object
        return new AllControlsStrategy();
    }
}
