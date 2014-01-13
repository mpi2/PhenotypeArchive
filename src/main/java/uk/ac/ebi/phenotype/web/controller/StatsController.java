/**
 * Copyright © 2011-2013 EMBL - European Bioinformatics Institute
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

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang.exception.ExceptionUtils;

import org.apache.solr.client.solrj.SolrServerException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import uk.ac.ebi.phenotype.dao.BiologicalModelDAO;
import uk.ac.ebi.phenotype.dao.GenomicFeatureDAO;
import uk.ac.ebi.phenotype.dao.OrganisationDAO;
import uk.ac.ebi.phenotype.dao.PhenotypeCallSummaryDAO;
import uk.ac.ebi.phenotype.dao.PhenotypePipelineDAO;
import uk.ac.ebi.phenotype.data.impress.Utilities;
import uk.ac.ebi.phenotype.error.GenomicFeatureNotFoundException;
import uk.ac.ebi.phenotype.error.ParameterNotFoundException;
import uk.ac.ebi.phenotype.pojo.BiologicalModel;
import uk.ac.ebi.phenotype.pojo.GenomicFeature;
import uk.ac.ebi.phenotype.pojo.ObservationType;
import uk.ac.ebi.phenotype.pojo.Parameter;
import uk.ac.ebi.phenotype.pojo.ParameterIncrement;
import uk.ac.ebi.phenotype.pojo.PhenotypeCallSummary;
import uk.ac.ebi.phenotype.pojo.Pipeline;
import uk.ac.ebi.phenotype.stats.ChartData;
import uk.ac.ebi.phenotype.stats.ChartType;
import uk.ac.ebi.phenotype.stats.ExperimentDTO;
import uk.ac.ebi.phenotype.stats.ExperimentService;
import uk.ac.ebi.phenotype.stats.PipelineProcedureData;
import uk.ac.ebi.phenotype.stats.PipelineProcedureTablesCreator;
import uk.ac.ebi.phenotype.stats.TableObject;
import uk.ac.ebi.phenotype.stats.categorical.CategoricalChartAndTableProvider;
import uk.ac.ebi.phenotype.stats.categorical.CategoricalResultAndCharts;
import uk.ac.ebi.phenotype.stats.timeseries.TimeSeriesChartAndTableProvider;
import uk.ac.ebi.phenotype.stats.unidimensional.UnidimensionalChartAndTableProvider;
import uk.ac.ebi.phenotype.stats.unidimensional.UnidimensionalDataSet;

@Controller
public class StatsController {

    private final Logger log = LoggerFactory.getLogger(StatsController.class);

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
    private ExperimentService experimentService;

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

    @RequestMapping("/stats/genes/{acc}")
    public String statisticsGraphs(
            @RequestParam(required = false, value = "parameterId") String[] parameterIds,
            @RequestParam(required = false, value = "gender") String[] gender,
            @RequestParam(required = false, value = "zygosity") String[] zygosity,
            @RequestParam(required = false, value = "phenotypingCenter") String[] phenotypingCenter,
            @RequestParam(required = false, value = "strategy") String[] strategies,
            @PathVariable String acc, Model model)
            throws GenomicFeatureNotFoundException, ParameterNotFoundException, IOException, URISyntaxException, SolrServerException {

        boolean statsError = false;

        //if we have no data for this phenotype call summary we link back to europhenome
        boolean noData = true;

        GenomicFeature gene = genesDao.getGenomicFeatureByAccession(acc);
        if (gene == null) {
            throw new GenomicFeatureNotFoundException("Gene " + acc + " can't be found.", acc);
        }

        // TODO: Implement control selection stragety by center
        //ControlSelectionStrategy controlSelectionStrategy = experimentService.getControlSelectionStrategy(phenotypingCenter, strategies);
        log.info(gene.toString());
        model.addAttribute("gene", gene);

        List<String> paramIds = getParamsAsList(parameterIds);
        List<String> genderList = getParamsAsList(gender);
        List<String> zyList = getParamsAsList(zygosity);
        List<String> phenotypingCenters = getParamsAsList(phenotypingCenter);

        //if no parameter Ids or gender or zygosity or bm specified then create a procedure view by default 
        if (paramIds.isEmpty() && genderList.isEmpty() && zyList.isEmpty()) {
            generateProcedureView(acc, model, gene);
            return "procedures";
        }

        List<JSONObject> charts = new ArrayList<>();
        List<UnidimensionalDataSet> allUnidimensionalDataSets = new ArrayList<>();
        List<CategoricalResultAndCharts> allCategoricalResultAndCharts = new ArrayList<>();
        List<TableObject> categoricalTables = new ArrayList<>();
        List<BiologicalModel> categoricalMutantBiologicalModels = new ArrayList<>();
        List<BiologicalModel> unidimensionalMutantBiologicalModels = new ArrayList<>();
        List<BiologicalModel> timeSeriesMutantBiologicalModels = new ArrayList<>();
        List<ChartData> timeSeriesChartsAndTables = new ArrayList<>();
        List<Parameter> parameters = new ArrayList<>();

        // Use the first phenotyping center passed in (ignore the others?)
        Integer phenotypingCenterId = null;
        if (phenotypingCenters != null && phenotypingCenters.size() > 0) {
            try {
                phenotypingCenterId = organisationDAO.getOrganisationByName(phenotypingCenters.get(0)).getId();
            } catch (NullPointerException e) {
                log.error("Cannot find center ID for organisation with name " + phenotypingCenter);
            }
        }

        for (String parameterId : paramIds) {

            Parameter parameter = pipelineDAO.getParameterByStableIdAndVersion(parameterId, 1, 0);
            if (parameter == null) {
                throw new ParameterNotFoundException("Parameter " + parameterId + " can't be found.", parameterId);
            }

            parameters.add(parameter);
            String[] parameterUnits = parameter.checkParameterUnits();
            String xUnits = "";
            String yUnits = "";

            if (parameterUnits.length > 0) {
                xUnits = parameterUnits[0];
            }
            if (parameterUnits.length > 1) {
                yUnits = parameterUnits[1];
            }

            ObservationType observationTypeForParam = Utilities.checkType(parameter);
            log.info("param=" + parameter.getName() + " Description=" + parameter.getDescription() + " xUnits=" + xUnits + " yUnits=" + yUnits + " dataType=" + observationTypeForParam);

            if (parameter.isIncrementFlag()) {
                for (ParameterIncrement increment : parameter.getIncrement()) {

                    //proper time-series data will 0,15,30 etc?
                    //grip strength ESLIM_009_001_002 - not timed but increment value unit = number increment values 1,2,3 so attempts at any time not specific times
                    //oxygne consumption ESLIM_003_001_003
                    //systolic arterial pressure datatype= something different?
                    //distance travelled ESLIM_007_001_001 increment unit minutes, increment values 5,10,15 etc
                    //grip strength repeat experiment url=
                    //http://localhost:8080/PhenotypeArchive/stats/genes/MGI:1916658?parameterId=ESLIM_009_001_002
                    //increment=Value: 3; dataType: REPEAT; unit: number; minimum: 0 increment min=0 data typ=REPEATnumber
                    //oxygen conusmption will not have increments?
                    //http://localhost:8080/PhenotypeArchive/stats/genes/MGI:102674?parameterId=ESLIM_003_001_003&gender=male&zygosity=homozygote
                    //increment=Value: ; dataType: REPEAT; unit: Time in hours relative to lights out; minimum: 0 increment min=0 data typ=REPEATTime in hours relative to lights out
                    //https://www.mousephenotype.org/data/stats/genes/MGI:2141881?parameterId=ESLIM_007_001_002
                    //http://localhost:8080/PhenotypeArchive/stats/genes/MGI:2141881?parameterId=ESLIM_007_001_002
                    if (increment.getUnit().equals("")) {

                        //if increment flag true and increment unit is empty then display as unidimensional
                        //e.g. http://localhost:8080/PhenotypeArchive/stats/genes/MGI:1098275?parameterId=ESLIM_002_001_002&gender=female&zygosity=heterozygote
                        //as per ticket https://www.ebi.ac.uk/panda/jira/browse/MPII-145
                        log.info("increment is empty !!!!!!!!!");
                        log.warn("changing parameter type from time_series to unidimensional");
                        observationTypeForParam = ObservationType.unidimensional;
                    }

                    log.debug("increment=" + increment + " increment min=" + increment.getMinimum() + " data typ=" + increment.getDataType() + increment.getUnit());

                }

            }

            List<ExperimentDTO> experimentList = experimentService.getExperimentDTO(parameter.getId(), acc, genderList, zyList, phenotypingCenterId);
            log.info(experimentList.toString());

            if (!experimentList.isEmpty()) {
                noData = false;
                //log.debug("Experiment dto marker="+experimentList);
                //ESLIM_003_001_003 id=962 calorimetry data for time series graph new MGI:1926153
                //http://localhost:8080/PhenotypeArchive/stats/genes/MGI:1926153?parameterId=ESLIM_003_001_003

                try {

                    switch (observationTypeForParam) {
                        case time_series:
                            //http://localhost:8080/PhenotypeArchive/stats/genes/MGI:1920000?parameterId=ESLIM_004_001_002

                            List<ChartData> timeSeriesForParam = timeSeriesChartAndTableProvider.doTimeSeriesData(bmDAO, experimentList, parameter, model, genderList, zyList, timeSeriesChartsAndTables.size() + 1);
                            timeSeriesChartsAndTables.addAll(timeSeriesForParam);
                            break;

                        case unidimensional:
                            //http://localhost:8080/phenotype-archive/stats/genes/MGI:1920000?parameterId=ESLIM_015_001_018

                            List<UnidimensionalDataSet> unidimensionalChartNTables = continousChartAndTableProvider.doUnidimensionalData(experimentList, bmDAO, unidimensionalMutantBiologicalModels, parameter, acc, model, genderList, zyList, ChartType.UnidimensionalBoxPlot, false);
                            allUnidimensionalDataSets.addAll(unidimensionalChartNTables);
                            break;

                        case categorical:
                            //https://dev.mousephenotype.org/mi/impc/dev/phenotype-archive/stats/genes/MGI:1346872?parameterId=ESLIM_001_001_004

                            List<CategoricalResultAndCharts> listOfcategoricalResultAndCharts = categoricalChartAndTableProvider.doCategoricalData(experimentList, bmDAO, parameter, acc, model, genderList, zyList, charts, categoricalTables, parameterId);
                            allCategoricalResultAndCharts.addAll(listOfcategoricalResultAndCharts);
                            break;

                        default:
                            // Trying to graph Unknown observation type
                            log.error("Unknown how to display graph for observation type: " + observationTypeForParam);
                            break;
                    }

                } catch (SQLException e) {
                    log.error(ExceptionUtils.getFullStackTrace(e));
                    statsError = true;
                }
            }
        }// end of parameterId iterations

        model.addAttribute("unidimensionalMutantBiologicalModels", unidimensionalMutantBiologicalModels);
        model.addAttribute("allUnidimensionalDataSets", allUnidimensionalDataSets);
        model.addAttribute("timeSeriesMutantBiologicalModels", timeSeriesMutantBiologicalModels);
        model.addAttribute("timeSeriesChartsAndTables", timeSeriesChartsAndTables);
        model.addAttribute("categoricalMutantBModel", categoricalMutantBiologicalModels);
        model.addAttribute("allCategoricalResultAndCharts", allCategoricalResultAndCharts);
        model.addAttribute("statsError", statsError);
        model.addAttribute("noData", noData);
        model.addAttribute("parameters", parameters);
        return "stats";
    }

    private void generateProcedureView(String acc, Model model, GenomicFeature gene) {

        log.info("Gene Accession without any web params");

        //a method here to get a general page for Gene and all procedures associated
        List<PhenotypeCallSummary> allPhenotypeSummariesForGene = phenotypeCallSummaryDAO.getPhenotypeCallByAccession(acc);

        //a method here to get a general page for Gene and all procedures associated
        List<Pipeline> pipelines = pipelineDAO.getAllPhenotypePipelines();
        PipelineProcedureTablesCreator creator = new PipelineProcedureTablesCreator();
        List<PipelineProcedureData> dataForTables = creator.createArraysForTables(pipelines, allPhenotypeSummariesForGene, gene);
        model.addAttribute("pipelineProcedureData", dataForTables);
    }

    @RequestMapping("/stats/genes/scatter/{acc}")
    public String genesScatter(
            @RequestParam(required = false, /*defaultValue = "ESLIM_001_001_007",*/ value = "parameterId") String[] parameterIds,
            @RequestParam(required = false, value = "gender") String[] gender,
            @RequestParam(required = false, value = "zygosity") String[] zygosity,
            @RequestParam(required = false, value = "model") String[] biologicalModelsParam,
            @RequestParam(required = false, value = "phenotypingCenter") String[] phenotypingCenter,
            @RequestParam(required = false, value = "byMouseId", defaultValue = "false") Boolean byMouseId,
            @PathVariable String acc, Model model)
            throws GenomicFeatureNotFoundException, ParameterNotFoundException, IOException, URISyntaxException, SolrServerException {

        boolean statsError = false;

        GenomicFeature gene = genesDao.getGenomicFeatureByAccession(acc);
        if (gene == null) {
            throw new GenomicFeatureNotFoundException("Gene " + acc
                    + " can't be found.", acc);
        }

        log.info(gene.toString());
        model.addAttribute("gene", gene);

        List<String> paramIds = getParamsAsList(parameterIds);
        List<String> genderList = getParamsAsList(gender);
        List<String> zyList = getParamsAsList(zygosity);
        List<String> phenotypingCenters = getParamsAsList(phenotypingCenter);

        List<UnidimensionalDataSet> allUnidimensionalDataSets = new ArrayList<>();

        List<BiologicalModel> unidimensionalMutantBiologicalModels = new ArrayList<>();
        List<Parameter> parameters = new ArrayList<>();

        // Use the first phenotyping center passed in (ignore the others?)
        Integer phenotypingCenterId = null;
        if (phenotypingCenters != null && phenotypingCenters.size() > 0) {
            try {
                phenotypingCenterId = organisationDAO.getOrganisationByName(phenotypingCenters.get(0)).getId();
            } catch (NullPointerException e) {
                log.error("Cannot find center ID for organisation with name " + phenotypingCenter);
            }
        }

        for (String parameterId : paramIds) {
            Parameter parameter = pipelineDAO.getParameterByStableIdAndVersion(parameterId, 1, 0);
            parameters.add(parameter);
            ObservationType observationTypeForParam = Utilities.checkType(parameter);
            String[] parameterUnits = parameter.checkParameterUnits();
            if (gene == null) {
                throw new GenomicFeatureNotFoundException("Gene " + acc
                        + " can't be found.", acc);
            }
            String xUnits = "";
            String yUnits = "";

            if (parameterUnits.length > 0) {
                xUnits = parameterUnits[0];
            }
            if (parameterUnits.length > 1) {
                yUnits = parameterUnits[1];
            }

            List<ExperimentDTO> experimentList = experimentService.getExperimentDTO(parameter.getId(), acc, genderList, zyList, phenotypingCenterId);

            log.info("param=" + parameter.getName() + " Description=" + parameter.getDescription() + " xUnits=" + xUnits + " yUnits=" + yUnits + " dataType=" + observationTypeForParam);

            //ESLIM_003_001_003 id=962 calorimetry data for time series graph new MGI:1926153
            //http://localhost:8080/PhenotypeArchive/stats/genes/MGI:1926153?parameterId=ESLIM_003_001_003
            try {

                if (observationTypeForParam.equals(ObservationType.unidimensional) || observationTypeForParam.equals(ObservationType.time_series)) {

                    List<UnidimensionalDataSet> unidimensionalChartNTables = continousChartAndTableProvider.doUnidimensionalData(experimentList, bmDAO, unidimensionalMutantBiologicalModels, parameter, acc, model, genderList, zyList, ChartType.UnidimensionalScatter, byMouseId);
                    allUnidimensionalDataSets.addAll(unidimensionalChartNTables);

                } else {
                    // must be categorical
                    // we don't want scatters for categorical!
                }

            } catch (SQLException e) {
                log.error(ExceptionUtils.getFullStackTrace(e));
                statsError = true;
            }
        }// end of parameterId iterations

        model.addAttribute("unidimensionalMutantBiologicalModels", unidimensionalMutantBiologicalModels);
        model.addAttribute("allUnidimensionalDataSets", allUnidimensionalDataSets);
        model.addAttribute("statsError", statsError);
        model.addAttribute("parameters", parameters);
        return "scatter";
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
            paramIds = Collections.emptyList();
        } else {
            paramIds = Arrays.asList(parameterIds);
        }
        return paramIds;
    }

    @ExceptionHandler(GenomicFeatureNotFoundException.class)
    public ModelAndView handleGenomicFeatureNotFoundException(GenomicFeatureNotFoundException exception) {
        ModelAndView mv = new ModelAndView("identifierError");
        mv.addObject("errorMessage", exception.getMessage());
        mv.addObject("acc", exception.getAcc());
        mv.addObject("type", "MGI gene");
        mv.addObject("exampleURI", "/stats/genes/MGI:104874");
        return mv;
    }

    @ExceptionHandler(ParameterNotFoundException.class)
    public ModelAndView handleParameterNotFoundException(ParameterNotFoundException exception) {
        ModelAndView mv = new ModelAndView("identifierError");
        mv.addObject("errorMessage", exception.getMessage());
        mv.addObject("acc", exception.getAcc());
        mv.addObject("type", "Parameter");
        mv.addObject("exampleURI", "/stats/genes/MGI:98373?parameterId=M-G-P_014_001_001&gender=male&zygosity=homozygote&phenotypingCenter=WTSI");
        return mv;
    }

}
