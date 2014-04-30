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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.ac.ebi.phenotype.dao.PhenotypePipelineDAO;
import uk.ac.ebi.phenotype.dao.UnidimensionalStatisticsDAO;
import uk.ac.ebi.phenotype.error.SpecificExperimentException;
import uk.ac.ebi.phenotype.pojo.CategoricalResult;
import uk.ac.ebi.phenotype.pojo.ControlStrategy;
import uk.ac.ebi.phenotype.pojo.ObservationType;
import uk.ac.ebi.phenotype.pojo.Parameter;
import uk.ac.ebi.phenotype.pojo.PhenotypeCallSummaryDAOReadOnly;
import uk.ac.ebi.phenotype.pojo.SexType;
import uk.ac.ebi.phenotype.pojo.StatisticalResult;
import uk.ac.ebi.phenotype.pojo.UnidimensionalResult;
import uk.ac.ebi.phenotype.pojo.ZygosityType;
import uk.ac.ebi.phenotype.stats.strategy.AllControlsStrategy;
import uk.ac.ebi.phenotype.stats.strategy.ControlSelectionStrategy;

@Service
public class ExperimentService {

    private static final Logger LOG = LoggerFactory.getLogger(ExperimentService.class);

    public static final Integer MIN_CONTROLS = 6;

    @Autowired
    ObservationService os;

    @Autowired
    PhenotypePipelineDAO parameterDAO;

    @Autowired
    private PhenotypeCallSummaryDAOReadOnly phenoDAO;

    @Autowired
    private UnidimensionalStatisticsDAO unidimensionalStatisticsDAO;

    public List<ExperimentDTO> getExperimentDTO(Integer parameterId, Integer pipelineId, String geneAccession, SexType sex, Integer phenotypingCenterId, List<String> zygosity, String strain) throws SolrServerException, IOException, URISyntaxException {
        return getExperimentDTO(parameterId, pipelineId, geneAccession, sex, phenotypingCenterId, zygosity, strain, null, Boolean.TRUE);
    }

    /**
     * 
     * @param parameterId
     * @param geneAccession
     * @param sex
     *            null for both sexes
     * @param phenotypingCenterId
     *            null for any organisation
     * @param zygosity
     *            null for any zygosity
     * @param strain
     *            null for any strain
     * @param phenotypingCenterId
     *            The database identifier of the center
     * @param metaDataString
     *            TODO
     * @param phenotypingCenter
     *            TODO
     * @return list of experiment objects
     * @throws SolrServerException
     * @throws IOException
     * @throws URISyntaxException
     */

    public List<ExperimentDTO> getExperimentDTO(Integer parameterId, Integer pipelineId, String geneAccession, SexType sex, Integer phenotypingCenterId, List<String> zygosity, String strain, String metaDataGroup, Boolean includeResults) throws SolrServerException, IOException, URISyntaxException {

        LOG.debug("metadataGroup parmeter is=" + metaDataGroup);

        List<ObservationDTO> observations = os.getExperimentalUnidimensionalObservationsByParameterPipelineGeneAccZygosityOrganisationStrainSexSexAndMetaDataGroup(parameterId, pipelineId, geneAccession, zygosity, phenotypingCenterId, strain, sex, metaDataGroup);
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

            String experimentKey = observation.getPhenotypingCenter() + observation.getStrain() + observation.getParameterStableId() + observation.getPipelineStableId() + observation.getGeneAccession() // TODO:
                                                                                                                                                                                                          // should
                                                                                                                                                                                                          // this
                                                                                                                                                                                                          // be
                                                                                                                                                                                                          // alleleAccession?
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

            experiment.getZygosities().add(ZygosityType.valueOf(observation.getZygosity()));
            experiment.getSexes().add(SexType.valueOf(observation.getSex()));

            // TODO: include allele
            // TODO: update to make use of the MP to result association
            // includeResults variable skips the results when gathering
            // experiments for calculating the results (performance)
            if (experiment.getResults() == null && experiment.getExperimentalBiologicalModelId() != null && includeResults) {
                // this call to solr is fine if all we want is pValue and effect
                // size, but for unidimensional data we need more stats info to
                // populate the extra table so we need to make a db call instead
                // for unidimensional
                List<? extends StatisticalResult> basicResults = phenoDAO.getStatisticalResultFor(observation.getGeneAccession(), observation.getParameterStableId(), ObservationType.valueOf(observation.getObservationType()), observation.getStrain());
                LOG.debug("basic results list size for experiment=" + basicResults.size());
                LOG.debug("experiment id=" + experiment.getExperimentId());
                List<StatisticalResult> populatedResults = new ArrayList<>();

                if (experiment.getObservationType() == ObservationType.unidimensional) {
                    for (StatisticalResult basicResult : basicResults) {
                        // get one for female and one for male if exist
                        UnidimensionalResult unidimensionalResult = (UnidimensionalResult) basicResult;
                        LOG.debug("basic result PCSummary Id=" + unidimensionalResult.getId() + " basic result sex type=" + unidimensionalResult.getSexType() + " p value=" + unidimensionalResult.getpValue());
                        // LOG.debug("basic result metadataGroup="+basicResult.getMetadataGroup());

                        try {
                            UnidimensionalResult result = unidimensionalStatisticsDAO.getUnidimensionalStatsForPhenotypeCallSummaryId(unidimensionalResult.getId());
                            if (result == null) {
                                LOG.debug("no comprehensive result found for unidimensionalresult with id=" + unidimensionalResult.getId());
                            }
                            if (result != null) {
                                // result.setSexType(unidimensionalResult.getSexType());//set
                                // the sextype from our already called solr
                                // result as it's not set by hibernate
                                LOG.debug("yes result found for for unidimensionalresult with id=" + unidimensionalResult.getId());
                                result.setZygosityType(unidimensionalResult.getZygosityType());
                                if (experiment.getMetadataGroup() != null && result.getMetadataGroup() != null) {
                                    if (experiment.getMetadataGroup().equals(result.getMetadataGroup())) {
                                        LOG.debug("metadata group in experiment and result are equal so adding " + metaDataGroup);
                                        LOG.debug("adding pValue from comprehensive result=" + result.getpValue());
                                        populatedResults.add(result);
                                    }
                                }
                            }
                        } catch (SQLException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    }
                }
                if (experiment.getObservationType() == ObservationType.categorical) {
                    for (StatisticalResult basicResult : basicResults) {
                        // get one for female and one for male if exist
                        CategoricalResult categoricalResult = (CategoricalResult) basicResult;
                        LOG.debug("basic result PCSummary Id=" + categoricalResult.getId() + " basic result sex type=" + categoricalResult.getSexType() + " p value=" + categoricalResult.getpValue());
                        // LOG.debug("basic result metadataGroup="+basicResult.getMetadataGroup());
                        // populatedResults.add(categoricalResult);
                        try {
                            CategoricalResult result = unidimensionalStatisticsDAO.getCategoricalStatsForPhenotypeCallSummaryId(categoricalResult.getId());
                            if (result == null) {
                                LOG.debug("no comprehensive result found for categoricalResult with id=" + categoricalResult.getId());
                            }
                            if (result != null) {
                                // result.setSexType(unidimensionalResult.getSexType());//set
                                // the sextype from our already called solr
                                // result as it's not set by hibernate
                                LOG.debug("yes result found for for categoricalResult with id=" + categoricalResult.getId());
                                result.setZygosityType(categoricalResult.getZygosityType());
                                result.setSexType(categoricalResult.getExperimentalSex());// set
                                                                                          // the
                                                                                          // sexType
                                                                                          // here
                                                                                          // so
                                                                                          // it
                                                                                          // is
                                                                                          // set
                                                                                          // like
                                                                                          // it
                                                                                          // is
                                                                                          // when
                                                                                          // calling
                                                                                          // from
                                                                                          // solr

                                LOG.debug("result in expservice has sex before meatadat group filter of " + result.getSexType());
                                if (experiment.getMetadataGroup() != null && result.getMetadataGroup() != null) {
                                    if (experiment.getMetadataGroup().equals(result.getMetadataGroup())) {
                                        LOG.debug("metadata group in experiment and result are equal so adding " + metaDataGroup);
                                        LOG.debug("adding pValue from comprehensive result=" + result.getpValue());
                                        // System.out.println("sex of metadata match is="+result.getSexType());
                                        result.setSexType(basicResult.getSexType());// this
                                                                                    // is
                                                                                    // horrible
                                                                                    // -
                                                                                    // how
                                                                                    // can
                                                                                    // we
                                                                                    // change
                                                                                    // this
                                                                                    // difference
                                                                                    // between
                                                                                    // hibernate
                                                                                    // way
                                                                                    // and
                                                                                    // solr
                                                                                    // way?
                                        populatedResults.add(result);
                                    }
                                }
                            }
                        } catch (SQLException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    }
                }
                // if (populatedResults.isEmpty()) {
                // LOG.debug("resorting to basic stats result");
                // //System.out.println("basic results size=" +
                // basicResults.size());
                // for (StatisticalResult bR : basicResults) {
                // //System.out.println("basic result metadataGroup=" +
                // bR.getMetadataGroup());
                // if (experiment.getMetadataGroup().equals(bR)){
                // UnidimensionalResult uniTempResult=(UnidimensionalResult)bR;
                // //LOG.debug("adding pValue from basic result="+uniTempResult.getpValue());
                // populatedResults.add((UnidimensionalResult)bR);
                // }
                // }
                // }

                experiment.setResults(populatedResults);

                // doc_id from above call is the phenotype_call_summary id
                // use this to get the correct stats result from the db
                // some dao .getStatsForPhenotypeCallSummaryId(14309);
                // note there will only be extra stats in the
                // stat_result_phenotype_call_summary if the call is an impc one
                // otherwise like this query it will be empty!
                // SELECT * FROM Y.stat_result_phenotype_call_summary where
                // phenotype_call_summary_id=88370;
                // # categorical_result_id, unidimensional_result_id,
                // phenotype_call_summary_id
                // 0, 204749, 88370

                // needs to get information from

                // >>>>>>> refs/remotes/origin/fixedNewDesign
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
    public ExperimentDTO getSpecificExperimentDTO(Integer id, Integer pipelineId, String acc, List<String> genderList, List<String> zyList, Integer phenotypingCenterId, String strain, String metadataGroup) throws SolrServerException, IOException, URISyntaxException, SpecificExperimentException {
        List<ExperimentDTO> experimentList = new ArrayList<ExperimentDTO>();
        boolean includeResults = true;


        // if gender list is size 2 assume both sexes so no filter needed
        if (genderList.isEmpty() || genderList.size() == 2) {

            // if zygosity list is size 3 then no filter needed either
            if (zyList.isEmpty() || zyList.size() == 3) {
                experimentList = this.getExperimentDTO(id, pipelineId, acc, null, phenotypingCenterId, null, strain, metadataGroup, includeResults);
            } else {
                experimentList = this.getExperimentDTO(id, pipelineId, acc, null, phenotypingCenterId, zyList, strain, metadataGroup, includeResults);
            }

        } else {
            String gender = genderList.get(0);
            if (zyList.isEmpty() || zyList.size() == 3) {
                experimentList = this.getExperimentDTO(id, pipelineId, acc, SexType.valueOf(gender), phenotypingCenterId, null, strain, metadataGroup, includeResults);
            } else {
                experimentList = this.getExperimentDTO(id, pipelineId, acc, SexType.valueOf(gender), phenotypingCenterId, zyList, strain, metadataGroup, includeResults);
            }

        }
        if (experimentList.isEmpty()) {
            return null;// return null if no experiments
        }
        if (experimentList.size() > 1) {
            throw new SpecificExperimentException("too many experiments returned - should only be one from this method call");
        }
        return experimentList.get(0);
    }

    public Map<String, List<String>> getExperimentKeys(String mgiAccession, String parameterStableIds, List<String> pipelineStableIds, List<String> phenotypingCenter, List<String> strain, List<String> metaDataGroup) throws SolrServerException {
        return os.getExperimentKeys(mgiAccession, parameterStableIds, pipelineStableIds, phenotypingCenter, strain, metaDataGroup);
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
