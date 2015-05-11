/**
 * Copyright © 2011-2014 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package uk.ac.ebi.phenotype.web.controller;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import uk.ac.ebi.phenotype.chart.AbrChartAndTableProvider;
import uk.ac.ebi.phenotype.chart.CategoricalChartAndTableProvider;
import uk.ac.ebi.phenotype.chart.CategoricalResultAndCharts;
import uk.ac.ebi.phenotype.chart.ChartData;
import uk.ac.ebi.phenotype.chart.ChartType;
import uk.ac.ebi.phenotype.chart.ChartUtils;
import uk.ac.ebi.phenotype.chart.FertilityChartAndDataProvider;
import uk.ac.ebi.phenotype.chart.GraphUtils;
import uk.ac.ebi.phenotype.chart.ScatterChartAndData;
import uk.ac.ebi.phenotype.chart.ScatterChartAndTableProvider;
import uk.ac.ebi.phenotype.chart.TimeSeriesChartAndTableProvider;
import uk.ac.ebi.phenotype.chart.UnidimensionalChartAndTableProvider;
import uk.ac.ebi.phenotype.chart.UnidimensionalDataSet;
import uk.ac.ebi.phenotype.chart.UnidimensionalStatsObject;
import uk.ac.ebi.phenotype.chart.ViabilityChartAndDataProvider;
import uk.ac.ebi.phenotype.chart.ViabilityDTO;
import uk.ac.ebi.phenotype.dao.*;
import uk.ac.ebi.phenotype.data.impress.Utilities;
import uk.ac.ebi.phenotype.error.GenomicFeatureNotFoundException;
import uk.ac.ebi.phenotype.error.ParameterNotFoundException;
import uk.ac.ebi.phenotype.error.SpecificExperimentException;
import uk.ac.ebi.phenotype.pojo.*;
import uk.ac.ebi.phenotype.service.ExperimentService;
import uk.ac.ebi.phenotype.service.ImpressService;
import uk.ac.ebi.phenotype.service.dto.ExperimentDTO;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.*;
import javax.annotation.Resource;

@Controller
public class ChartsController {

    private final Logger log = LoggerFactory.getLogger(ChartsController.class);

    @Autowired
    private BiologicalModelDAO bmDAO;

    @Autowired
    private PhenotypePipelineDAO pipelineDAO;

    @Autowired
    private GenomicFeatureDAO genesDao;

    @Autowired
    private OrganisationDAO organisationDAO;

    @Autowired
    private PhenotypeCallSummaryDAO phenotypeCallSummaryDAO;

    @Autowired
    private CategoricalChartAndTableProvider categoricalChartAndTableProvider;

    @Autowired
    private TimeSeriesChartAndTableProvider timeSeriesChartAndTableProvider;

    @Autowired
    private UnidimensionalChartAndTableProvider continousChartAndTableProvider;

    @Autowired
    private ScatterChartAndTableProvider scatterChartAndTableProvider;

    @Autowired
    private AbrChartAndTableProvider abrChartAndTableProvider;

    @Autowired
    private ViabilityChartAndDataProvider viabilityChartAndDataProvider;

    @Autowired
    private FertilityChartAndDataProvider fertilityChartAndDataProvider;

    @Autowired
    private ExperimentService experimentService;

    @Autowired
    private ImpressService is;

    @Autowired
    Utilities impressUtilities;
    @Resource(name = "globalConfiguration")
    private Map<String, String> config;

    /**
     * Runs when the request missing an accession ID. This redirects to the
     * search page which defaults to showing all genes in the list
     *
     * @return string to instruct spring to redirect to the search page
     */
    @RequestMapping("/stats")
    public String rootForward() {

        return "redirect:/search";
    }

    /**
     * This method should take in the parameters and then generate a skeleton
     * jsp page with urls that can be called by a jquery ajax requests for each
     * graph div and table div
     *
     * @param parameterIds
     * @param gender
     * @param zygosity
     * @param phenotypingCenter
     * @param strategies
     * @param accessionsParams
     * @param model
     * @return
     * @throws GenomicFeatureNotFoundException
     * @throws ParameterNotFoundException
     * @throws IOException
     * @throws URISyntaxException
     * @throws SolrServerException
     */
    @RequestMapping("/charts")
    public String charts(@RequestParam(required = false, value = "accession") String[] accessionsParams,
                         @RequestParam(required = false, value = "parameter_stable_id") String[] parameterIds,
                         @RequestParam(required = false, value = "gender") String[] gender,
                         @RequestParam(required = false, value = "zygosity") String[] zygosity,
                         @RequestParam(required = false, value = "phenotyping_center") String[] phenotypingCenter,
                         @RequestParam(required = false, value = "strategy") String[] strategies,
                         @RequestParam(required = false, value = "strain") String[] strains,
                         @RequestParam(required = false, value = "metadata_group") String[] metadataGroup,
                         @RequestParam(required = false, value = "chart_type") ChartType chartType,
                         @RequestParam(required = false, value = "pipeline_stable_id") String[] pipelineStableIds,
                         @RequestParam(required = false, value = "allele_accession_id") String[] alleleAccession,
                         Model model)
            throws GenomicFeatureNotFoundException, ParameterNotFoundException, IOException, URISyntaxException, SolrServerException {
        
        if ((accessionsParams != null) && (accessionsParams.length > 0) && (parameterIds != null) && (parameterIds.length > 0)) {
            for (String parameterStableId : parameterIds) {
                if (parameterStableId.contains("_FER_")) {
                    String url = config.get("baseUrl") + "/genes/" + accessionsParams[0];
                    return "redirect:" + url;
                }
            }
        }
        
        return createCharts(accessionsParams, pipelineStableIds, parameterIds, gender, phenotypingCenter, strains, metadataGroup, zygosity, model, chartType, alleleAccession);
    }

    /**
     * Will only ever return one chart!
     *
     * @param accession
     * @param strain
     * @param metadataGroup
     * @param parameterStableId
     * @param gender
     * @param zygosity
     * @param phenotypingCenter
     * @param strategies
     * @param model
     * @return
     * @throws GenomicFeatureNotFoundException
     * @throws ParameterNotFoundException
     * @throws IOException
     * @throws URISyntaxException
     * @throws SolrServerException
     */
    @RequestMapping("/chart")
    public String chart(@RequestParam(required = true, value = "experimentNumber") String experimentNumber,
                        @RequestParam(required = false, value = "accession") String[] accession,
                        @RequestParam(required = false, value = "strain_accession_id") String strain,
                        @RequestParam(required = false, value = "allele_accession_id") String alleleAccession,
                        @RequestParam(required = false, value = "metadata_group") String metadataGroup,
                        @RequestParam(required = false, value = "parameter_stable_id") String parameterStableId,
                        @RequestParam(required = false, value = "gender") String[] gender,
                        @RequestParam(required = false, value = "zygosity") String[] zygosity,
                        @RequestParam(required = false, value = "phenotyping_center") String phenotypingCenter,
                        @RequestParam(required = false, value = "strategy") String[] strategies,
                        @RequestParam(required = false, value = "pipeline_stable_id") String pipelineStableId,
                        @RequestParam(required = false, value = "chart_type") ChartType chartType,
                        @RequestParam(required = false, value = "standAlone") boolean standAlone, Model model)
            throws GenomicFeatureNotFoundException, ParameterNotFoundException, IOException, URISyntaxException, SolrServerException, SpecificExperimentException {
   	
        UnidimensionalDataSet unidimensionalChartDataSet = null;
        ChartData timeSeriesForParam = null;
        CategoricalResultAndCharts categoricalResultAndChart = null;

        boolean statsError = false;

        if (parameterStableId.startsWith("IMPC_FER_")) {
            String url = config.get("baseUrl") + "/genes/" + accession[0];
            return "redirect:" + url;
        }

		// TODO need to check we don't have more than one accession and one
        // parameter throw and exception if we do
        // get the parameter object from the stable id
        Parameter parameter = pipelineDAO.getParameterByStableId(parameterStableId);
        if (parameter == null) {
            throw new ParameterNotFoundException("Parameter " + parameterStableId + " can't be found.", parameterStableId);
        }

        String[] parameterUnits = parameter.checkParameterUnits();
        String xUnits = "";
        String yUnits = "";

        if (parameterUnits.length > 0) {
            xUnits = parameterUnits[0];
        }
        if (parameterUnits.length > 1) {
            yUnits = parameterUnits[1];
        }
        
        ObservationType observationTypeForParam = impressUtilities.checkType(parameter);
        log.info("param=" + parameter.getName() + " Description=" + parameter.getDescription() + " xUnits=" + xUnits + " yUnits=" + yUnits + " chartType=" + chartType + " dataType=" + observationTypeForParam);

        List<String> genderList = getParamsAsList(gender);

		// Use the first phenotyping center passed in (ignore the others?)
        // should only now be one center at this stage for one graph/experiment
        // TODO put length check and exception here
        // List<String> phenotypingCenters = getParamsAsList(phenotypingCenter);
        Integer phenotypingCenterId = null;
        if (phenotypingCenter != null) {
            try {
                phenotypingCenterId = organisationDAO.getOrganisationByName(phenotypingCenter).getId();
            } catch (NullPointerException e) {
                log.error("Cannot find center ID for organisation with name " + phenotypingCenter);
            }
        }

        String metaDataGroupString = null;
        if (metadataGroup != null) {
            metaDataGroupString = metadataGroup;
        }

        List<String> zyList = getParamsAsList(zygosity);

        Integer pipelineId = null;
        Pipeline pipeline = null;

        if (pipelineStableId != null &&  ! pipelineStableId.equals("")) {
            log.debug("pipe stable id=" + pipelineStableId);
            pipeline = pipelineDAO.getPhenotypePipelineByStableId(pipelineStableId);
            pipelineId = pipeline.getId();
            model.addAttribute("pipeline", pipeline);
            model.addAttribute("pipelineUrl", is.getPipelineUrlByStableId(pipeline.getStableId()));
        }

        model.addAttribute("phenotypingCenter", phenotypingCenter);

        ExperimentDTO experiment = null;
        if (parameterStableId.startsWith("IMPC_VIA_")) {
			// Its a viability outcome param which means its a line level query
            // so we don't use the normal experiment query in experiment service
            ViabilityDTO viability = experimentService.getSpecificViabilityExperimentDTO(parameter.getId(), pipelineId, accession[0], phenotypingCenterId, strain, metaDataGroupString, alleleAccession);
            ViabilityDTO viabilityDTO = viabilityChartAndDataProvider.doViabilityData(parameter, viability);
            model.addAttribute("viabilityDTO", viabilityDTO);
            BiologicalModel expBiologicalModel = bmDAO.getBiologicalModelById(viabilityDTO.getParamStableIdToObservation().entrySet().iterator().next().getValue().getBiologicalModelId());
            setTitlesForGraph(model, expBiologicalModel);
        }

        // 29-Apr-2015 (mrelac) The team has determined that we don't display fertility graphs because Impress does not require all the supporting
        // data to be uploaded, and some centers don't upload it, so we don't know if the data is valid or not.
        
//        if (parameterStableId.startsWith("IMPC_FER_")) {
//			// Its a fertility outcome param which means its a line level query
//            // so we don't use the normal experiment query in experiment service
//            //http://ves-ebi-d0.ebi.ac.uk:8090/mi/impc/dev/solr/experiment/query?q=parameter_stable_id:IMPC_FER_*&facet=true&facet.field=parameter_stable_id&rows=300&fq=gene_accession_id:%22MGI:1918788%22
//            //http://localhost:8080/phenotype-archive/charts?accession=MGI:1918788&parameter_stable_id=IMPC_FER_001_001
//            FertilityDTO fertility = experimentService.getSpecificFertilityExperimentDTO(parameter.getId(), pipelineId, accession[0], phenotypingCenterId, strain, metaDataGroupString, alleleAccession);
//            FertilityDTO fertilityDTO = fertilityChartAndDataProvider.doFertilityData(parameter, fertility);
//            if (fertilityDTO != null) {
//                model.addAttribute("fertilityDTO", fertilityDTO);
//                BiologicalModel expBiologicalModel = bmDAO.getBiologicalModelById(fertilityDTO.getParamStableIdToObservation().entrySet().iterator().next().getValue().getBiologicalModelId());
//                setTitlesForGraph(model, expBiologicalModel);
//            }
//            return "chart";
//        }

        if ( ! ChartUtils.getPlotParameter(parameter.getStableId()).equalsIgnoreCase(parameter.getStableId())) {
            parameter = pipelineDAO.getParameterByStableId(ChartUtils.getPlotParameter(parameter.getStableId()));
            chartType = ChartUtils.getPlotType(parameterStableId);
            if (chartType.equals(ChartType.TIME_SERIES_LINE)){
            	metaDataGroupString = null;
            }
        }

        experiment = experimentService.getSpecificExperimentDTO(parameter.getId(), pipelineId, accession[0], genderList, zyList, phenotypingCenterId, strain, metaDataGroupString, alleleAccession);

        if (experiment != null) {

            if (pipeline == null) {
                // if we don't already have the pipeline from the url params get it via the experiment returned
                pipeline = pipelineDAO.getPhenotypePipelineByStableId(experiment.getPipelineStableId());
            }

            String xAxisTitle = xUnits;
            BiologicalModel expBiologicalModel = bmDAO.getBiologicalModelById(experiment.getExperimentalBiologicalModelId());
            setTitlesForGraph(model, expBiologicalModel);

            try {
				// if (chartType == null){
                // chartType = GraphUtils.getDefaultChartType(parameter);
                // // chartType might still be null after this
                // if(chartType==ChartType.PIE){
                // viabilityDTO =
                // viabilityChartAndDataProvider.doViabilityData(null, null);
                // model.addAttribute("viabilityDTO", viabilityDTO);
                // //model.addAttribute("tableData", viabilityDTO);
                // return "chart";
                // }
                // }
                if (chartType != null) {

                    ScatterChartAndData scatterChartAndData;

                    switch (chartType) {

                        case UNIDIMENSIONAL_SCATTER_PLOT:

                            scatterChartAndData = scatterChartAndTableProvider.doScatterData(experiment, null, null, parameter, experimentNumber, expBiologicalModel);
                            model.addAttribute("scatterChartAndData", scatterChartAndData);

                            if (observationTypeForParam.equals(ObservationType.unidimensional)) {
                                List<UnidimensionalStatsObject> unidimenStatsObjects = scatterChartAndData.getUnidimensionalStatsObjects();
                                unidimensionalChartDataSet = new UnidimensionalDataSet();
                                unidimensionalChartDataSet.setStatsObjects(unidimenStatsObjects);
                                model.addAttribute("unidimensionalChartDataSet", unidimensionalChartDataSet);
                            }
                            break;

                        case UNIDIMENSIONAL_ABR_PLOT:

                            // get experiments for other parameters too
                            model.addAttribute("abrChart", abrChartAndTableProvider.getChart(pipelineId, accession[0], genderList, zyList, phenotypingCenterId, strain, metaDataGroupString, alleleAccession, "abrChart" + experimentNumber));
                            break;

                        case UNIDIMENSIONAL_BOX_PLOT:

                            unidimensionalChartDataSet = continousChartAndTableProvider.doUnidimensionalData(experiment, experimentNumber, parameter, ChartType.UNIDIMENSIONAL_BOX_PLOT, false, xAxisTitle, expBiologicalModel);
                            model.addAttribute("unidimensionalChartDataSet", unidimensionalChartDataSet);

                            scatterChartAndData = scatterChartAndTableProvider.doScatterData(experiment, unidimensionalChartDataSet.getMin(), unidimensionalChartDataSet.getMax(), parameter, experimentNumber, expBiologicalModel);
                            model.addAttribute("scatterChartAndData", scatterChartAndData);

                            break;

                        case CATEGORICAL_STACKED_COLUMN:

                            categoricalResultAndChart = categoricalChartAndTableProvider.doCategoricalData(experiment, parameter, accession[0], experimentNumber, expBiologicalModel);
                            model.addAttribute("categoricalResultAndChart", categoricalResultAndChart);
                            break;

                        case TIME_SERIES_LINE:

                            timeSeriesForParam = timeSeriesChartAndTableProvider.doTimeSeriesData(experiment, parameter, experimentNumber, expBiologicalModel);
                            model.addAttribute("timeSeriesChartsAndTable", timeSeriesForParam);
                            break;

                        default:

                            log.error("Unknown how to display graph for observation type: " + observationTypeForParam);
                            break;
                    }
                }

            } catch (SQLException e) {
                log.error(ExceptionUtils.getFullStackTrace(e));
                statsError = true;
            }

            model.addAttribute("pipeline", pipeline);
            model.addAttribute("phenotypingCenter", phenotypingCenter);
            model.addAttribute("experimentNumber", experimentNumber);
            model.addAttribute("statsError", statsError);

        } else {
            System.out.println("empty experiment");
            model.addAttribute("emptyExperiment", true);
        }

        return "chart";
    }

    private void setTitlesForGraph(Model model, BiologicalModel expBiologicalModel) {

        String allelicCompositionString = "unknown";
        String symbol = "unknown";
        String geneticBackgroundString = "unknown";

        if (expBiologicalModel != null) {
            allelicCompositionString = expBiologicalModel.getAllelicComposition();
            symbol = expBiologicalModel.getAlleles().get(0).getSymbol();
            geneticBackgroundString = expBiologicalModel.getGeneticBackground();
            model.addAttribute("allelicCompositionString", allelicCompositionString);
            model.addAttribute("symbol", symbol);
            model.addAttribute("geneticBackgroundString", geneticBackgroundString);
        }
    }

    private String createCharts(String[] accessionsParams, String[] pipelineStableIdsArray, String[] parameterIds, String[] gender, String[] phenotypingCenter, 
    			String[] strains, String[] metadataGroup, String[] zygosity, Model model, ChartType chartType, String[] alleleAccession)
    throws SolrServerException, GenomicFeatureNotFoundException, ParameterNotFoundException {

        GraphUtils graphUtils = new GraphUtils(experimentService);
        List<String> geneIds = getParamsAsList(accessionsParams);
        List<String> paramIds = getParamsAsList(parameterIds);
        List<String> genderList = getParamsAsList(gender);
        List<String> phenotypingCentersList = getParamsAsList(phenotypingCenter);
        List<String> strainsList = getParamsAsList(strains);
        List<String> metadataGroups = getParamsAsList(metadataGroup);
        List<String> pipelineStableIds = getParamsAsList(pipelineStableIdsArray);
        List<String> alleleAccessions = getParamsAsList(alleleAccession);

        // add sexes explicitly here so graphs urls are created separately
        if (genderList.isEmpty()) {
            genderList.add(SexType.male.name());
            genderList.add(SexType.female.name());
        }

        List<String> zyList = getParamsAsList(zygosity);
        if (zyList.isEmpty()) {
            zyList.add(ZygosityType.homozygote.name());
            zyList.add(ZygosityType.heterozygote.name());
            zyList.add(ZygosityType.hemizygote.name());
        }

        Set<String> allGraphUrlSet = new LinkedHashSet<>();
        String allParameters = "";

        for (String geneId : geneIds) {

            GenomicFeature gene = genesDao.getGenomicFeatureByAccession(geneId);

            if (gene == null) {
                throw new GenomicFeatureNotFoundException("Gene " + geneId + " can't be found.", geneId);
            }

            log.info(gene.toString());

            model.addAttribute("gene", gene);

            List<String> pNames = new ArrayList<>();

            for (String parameterId : paramIds) {

                Parameter parameter = pipelineDAO.getParameterByStableId(parameterId);
                pNames.add(StringUtils.capitalize(parameter.getName()) + " (" + parameter.getStableId() + ")");

                if (parameter == null) {
                    throw new ParameterNotFoundException("Parameter " + parameterId + " can't be found.", parameterId);
                }

				// instead of an experiment list here we need just the outline
                // of the experiments - how many, observation types
                Set<String> graphUrlsForParam = graphUtils.getGraphUrls(geneId, parameter, pipelineStableIds, genderList, zyList, phenotypingCentersList, 
                								strainsList, metadataGroups, chartType, alleleAccessions);
                allGraphUrlSet.addAll(graphUrlsForParam);

            }// end of parameterId iterations

            allParameters = StringUtils.join(pNames, ", ");

        }// end of gene iterations
        System.out.println("all graphs=" + StringUtils.join(allGraphUrlSet, "\n"));
        model.addAttribute("allGraphUrlSet", allGraphUrlSet);
        model.addAttribute("allParameters", allParameters);

        return "stats";
    }

    /**
     * Exception handler for gene not found
     *
     * @param exception of proper type to indicate gene not found
     * @return model and view for error page
     */
    @ExceptionHandler(GenomicFeatureNotFoundException.class)
    public ModelAndView handleGenomicFeatureNotFoundException(GenomicFeatureNotFoundException exception) {

        log.error(exception.getMessage());

        ModelAndView mv = new ModelAndView("identifierError");
        mv.addObject("errorMessage", exception.getMessage());
        mv.addObject("acc", exception.getAcc());
        mv.addObject("type", "MGI gene");
        mv.addObject("exampleURI", "/charts?accession=MGI:104874");

        return mv;
    }

    /**
     * Exception handler for parameter not found
     *
     * @param exception of proper type to indicate parameter not found
     * @return model and view for error page
     */
    @ExceptionHandler(ParameterNotFoundException.class)
    public ModelAndView handleParameterNotFoundException(ParameterNotFoundException exception) {

        log.error(exception.getMessage());

        ModelAndView mv = new ModelAndView("identifierError");
        mv.addObject("errorMessage", exception.getMessage());
        mv.addObject("acc", exception.getAcc());
        mv.addObject("type", "Parameter");
        mv.addObject("exampleURI", "/charts?accession=MGI:98373&parameterId=M-G-P_014_001_001&gender=male&zygosity=homozygote&phenotypingCenter=WTSI");

        return mv;
    }

    /**
     * Exception handler for experiment not found
     *
     * @param exception of proper type to indicate experiment not found
     * @return model and view for error page
     */
    @ExceptionHandler(SpecificExperimentException.class)
    public ModelAndView handleSpecificExperimentException(ParameterNotFoundException exception) {

        log.error(exception.getMessage());

        ModelAndView mv = new ModelAndView("Specific Experiment Not Found Error");
        mv.addObject("errorMessage", exception.getMessage());
        mv.addObject("acc", exception.getAcc());
        mv.addObject("type", "Parameter");

        return mv;
    }

    /**
     * Convenience method that just changes an array [] to a more modern LIst (I
     * hate arrays! :) )
     *
     * @param parameterIds
     * @return
     */
    private List<String> getParamsAsList(String[] parameterIds) {

        List<String> paramIds;
        if (parameterIds == null) {
            paramIds = new ArrayList<String>();
        } else {
            paramIds = Arrays.asList(parameterIds);
        }
        return paramIds;
    }

}
