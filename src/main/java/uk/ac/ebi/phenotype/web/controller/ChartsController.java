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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.exception.ExceptionUtils;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.xmlbeans.impl.jam.mutable.MPackage;
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
import uk.ac.ebi.phenotype.pojo.SexType;
import uk.ac.ebi.phenotype.pojo.ZygosityType;
import uk.ac.ebi.phenotype.stats.ChartData;
import uk.ac.ebi.phenotype.stats.ChartType;
import uk.ac.ebi.phenotype.stats.ExperimentDTO;
import uk.ac.ebi.phenotype.stats.ExperimentService;
import uk.ac.ebi.phenotype.stats.PipelineProcedureData;
import uk.ac.ebi.phenotype.stats.PipelineProcedureTablesCreator;
import uk.ac.ebi.phenotype.stats.TableObject;
import uk.ac.ebi.phenotype.stats.categorical.CategoricalChartAndTableProvider;
import uk.ac.ebi.phenotype.stats.categorical.CategoricalResultAndCharts;
import uk.ac.ebi.phenotype.stats.graphs.GraphUtils;
import uk.ac.ebi.phenotype.stats.timeseries.TimeSeriesChartAndTableProvider;
import uk.ac.ebi.phenotype.stats.unidimensional.UnidimensionalChartAndTableProvider;
import uk.ac.ebi.phenotype.stats.unidimensional.UnidimensionalDataSet;

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
	 * @param acc
	 * @param model
	 * @return
	 * @throws GenomicFeatureNotFoundException
	 * @throws ParameterNotFoundException
	 * @throws IOException
	 * @throws URISyntaxException
	 * @throws SolrServerException
	 */
	@RequestMapping("/stats/genes/{acc}")
	public String statisticsGraphs(
			@RequestParam(required = false, value = "parameterId") String[] parameterIds,
			@RequestParam(required = false, value = "gender") String[] gender,
			@RequestParam(required = false, value = "zygosity") String[] zygosity,
			@RequestParam(required = false, value = "phenotypingCenter") String[] phenotypingCenter,
			@RequestParam(required = false, value = "strategy") String[] strategies,
			@PathVariable String acc, Model model)
			throws GenomicFeatureNotFoundException, ParameterNotFoundException,
			IOException, URISyntaxException, SolrServerException {

		GraphUtils graphUtils = new GraphUtils(experimentService);

		GenomicFeature gene = genesDao.getGenomicFeatureByAccession(acc);
		if (gene == null) {
			throw new GenomicFeatureNotFoundException("Gene " + acc
					+ " can't be found.", acc);
		}

		// TODO: Implement control selection stragety by center
		// ControlSelectionStrategy controlSelectionStrategy =
		// experimentService.getControlSelectionStrategy(phenotypingCenter,
		// strategies);
		log.info(gene.toString());
		model.addAttribute("gene", gene);

		List<String> paramIds = getParamsAsList(parameterIds);
		List<String> genderList = getParamsAsList(gender);
		if (genderList.size() == 0) {// add them explicitly here so graphs urls
										// are created seperately
			genderList.add(SexType.male.name());
			genderList.add(SexType.female.name());
		}
		List<String> zyList = getParamsAsList(zygosity);
		if (zyList.isEmpty()) {
			zyList.add(ZygosityType.homozygote.name());
			zyList.add(ZygosityType.heterozygote.name());
			zyList.add(ZygosityType.hemizygote.name());
		}
		List<String> phenotypingCenters = getParamsAsList(phenotypingCenter);

		List<UnidimensionalDataSet> allUnidimensionalDataSets = new ArrayList<>();

		List<Parameter> parameters = new ArrayList<>();

		Set<String> allGraphUrlSet = new HashSet<String>();
		for (String parameterId : paramIds) {

			Parameter parameter = pipelineDAO.getParameterByStableIdAndVersion(
					parameterId, 1, 0);
			if (parameter == null) {
				throw new ParameterNotFoundException("Parameter " + parameterId
						+ " can't be found.", parameterId);
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

			ObservationType observationTypeForParam = Utilities
					.checkType(parameter);
			log.info("param=" + parameter.getName() + " Description="
					+ parameter.getDescription() + " xUnits=" + xUnits
					+ " yUnits=" + yUnits + " dataType="
					+ observationTypeForParam);

			if (parameter.isIncrementFlag()) {
				for (ParameterIncrement increment : parameter.getIncrement()) {

					// proper time-series data will 0,15,30 etc?
					// grip strength ESLIM_009_001_002 - not timed but increment
					// value unit = number increment values 1,2,3 so attempts at
					// any time not specific times
					// oxygne consumption ESLIM_003_001_003
					// systolic arterial pressure datatype= something different?
					// distance travelled ESLIM_007_001_001 increment unit
					// minutes, increment values 5,10,15 etc
					// grip strength repeat experiment url=
					// http://localhost:8080/PhenotypeArchive/stats/genes/MGI:1916658?parameterId=ESLIM_009_001_002
					// increment=Value: 3; dataType: REPEAT; unit: number;
					// minimum: 0 increment min=0 data typ=REPEATnumber
					// oxygen conusmption will not have increments?
					// http://localhost:8080/PhenotypeArchive/stats/genes/MGI:102674?parameterId=ESLIM_003_001_003&gender=male&zygosity=homozygote
					// increment=Value: ; dataType: REPEAT; unit: Time in hours
					// relative to lights out; minimum: 0 increment min=0 data
					// typ=REPEATTime in hours relative to lights out
					// https://www.mousephenotype.org/data/stats/genes/MGI:2141881?parameterId=ESLIM_007_001_002
					// http://localhost:8080/PhenotypeArchive/stats/genes/MGI:2141881?parameterId=ESLIM_007_001_002
					if (increment.getUnit().equals("")) {

						// if increment flag true and increment unit is empty
						// then display as unidimensional
						// e.g.
						// http://localhost:8080/PhenotypeArchive/stats/genes/MGI:1098275?parameterId=ESLIM_002_001_002&gender=female&zygosity=heterozygote
						// as per ticket
						// https://www.ebi.ac.uk/panda/jira/browse/MPII-145
						log.info("increment is empty !!!!!!!!!");
						log.warn("changing parameter type from time_series to unidimensional");
						observationTypeForParam = ObservationType.unidimensional;
					}

					log.debug("increment=" + increment + " increment min="
							+ increment.getMinimum() + " data typ="
							+ increment.getDataType() + increment.getUnit());

				}

			}
			// instead of an experiment list here we need just the outline of
			// the experiments - how many, observation types

			Set<String> graphUrlsForParam = graphUtils.getGraphUrls(acc,
					parameter.getStableId(), genderList, zyList);
			allGraphUrlSet.addAll(graphUrlsForParam);

		}// end of parameterId iterations

		model.addAttribute("allGraphUrlSet", allGraphUrlSet);
		// model.addAttribute("allUnidimensionalDataSets",
		// allUnidimensionalDataSets);
		// model.addAttribute("timeSeriesMutantBiologicalModels",
		// timeSeriesMutantBiologicalModels);
		// model.addAttribute("timeSeriesChartsAndTables",
		// timeSeriesChartsAndTables);
		// model.addAttribute("categoricalMutantBModel",
		// categoricalMutantBiologicalModels);
		// model.addAttribute("allCategoricalResultAndCharts",
		// allCategoricalResultAndCharts);
		// model.addAttribute("statsError", statsError);
		// model.addAttribute("noData", noData);
		model.addAttribute("parameters", parameters);
		return "stats";
	}

	/**
	 * Will only ever return one chart!
	 * @param chartId
	 * @param accession
	 * @param strain
	 * @param metadataGroup
	 * @param parameterIds
	 * @param parameterStableIds
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
	public String chart(
			@RequestParam(required = true, value = "chartId") String chartId,// we
																				// need
																				// a
																				// chartId
																				// at
																				// the
																				// least
																				// the
																				// 1st
																				// one
																				// if
																				// doing
																				// seperate
																				// ones
																				// for
																				// male
																				// and
																				// female
			@RequestParam(required = false, value = "accession") String[] accession,
			@RequestParam(required = false, value = "strain") String[] strain,
			@RequestParam(required = false, value = "metadata_group") String[] metadataGroup,
			@RequestParam(required = false, value = "parameterId") String[] parameterIds,
			@RequestParam(required = false, value = "parameterStableId") String[] parameterStableIds,
			@RequestParam(required = false, value = "gender") String gender,//only have one gender per graph
			@RequestParam(required = false, value = "zygosity") String[] zygosity,
			@RequestParam(required = false, value = "phenotyping_center") String[] phenotypingCenter,
			@RequestParam(required = false, value = "strategy") String[] strategies,
			Model model) throws GenomicFeatureNotFoundException,
			ParameterNotFoundException, IOException, URISyntaxException,
			SolrServerException {
		
		UnidimensionalDataSet unidimensionalChartNTable=null;
		ChartData timeSeriesForParam=null;
		CategoricalResultAndCharts categoricalResultAndChart=null;
		boolean statsError = false;
		System.out.println("calling chart" + accession);
		// TODO need to check we don't have more than one accession and one
		// parameter throw and exception if we do

		// get the parameter internal int id as we dont' have it
		Parameter parameter = pipelineDAO.getParameterByStableIdAndVersion(
				parameterIds[0], 1, 0);
		if (parameter == null) {
			throw new ParameterNotFoundException("Parameter " + parameterIds[0]
					+ " can't be found.", parameterIds[0]);
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

		ObservationType observationTypeForParam = Utilities
				.checkType(parameter);
		log.info("param=" + parameter.getName() + " Description="
				+ parameter.getDescription() + " xUnits=" + xUnits + " yUnits="
				+ yUnits + " dataType=" + observationTypeForParam);

		// System.out.println("paramId="+parameter.getId());
		// Use the first phenotyping center passed in (ignore the others?)
		// should only now be one center at this stage for one graph/experiment
		// TODO put length check and exception here
		List<String> phenotypingCenters = getParamsAsList(phenotypingCenter);
		Integer phenotypingCenterId = null;
		if (phenotypingCenters != null && phenotypingCenters.size() > 0) {
			try {
				phenotypingCenterId = organisationDAO.getOrganisationByName(
						phenotypingCenters.get(0)).getId();
			} catch (NullPointerException e) {
				log.error("Cannot find center ID for organisation with name "
						+ phenotypingCenter);
			}
		}

		
		List<String> zyList = getParamsAsList(zygosity);
		// List<ExperimentDTO> experimentList =
		// experimentService.getExperimentDTO(parameterStableIds[0],
		// accession[0], gender[0], zygosity[0], phenotypingCenterIdInt);
		// TDO handle male and female appropriately
		List<ExperimentDTO> experimentList = experimentService
				.getExperimentDTO(parameterIds[0], accession[0],
						SexType.valueOf(gender), phenotypingCenterId,
						ZygosityType.homozygote.toString(), strain[0]);
		System.out.println("experiment list size=" + experimentList.size());
		log.info(experimentList.toString());
		if (!experimentList.isEmpty()) {
			// log.debug("Experiment dto marker="+experimentList);
			// ESLIM_003_001_003 id=962 calorimetry data for time series graph
			// new MGI:1926153
			// http://localhost:8080/PhenotypeArchive/stats/genes/MGI:1926153?parameterId=ESLIM_003_001_003
		
			
			List<TableObject> categoricalTables = new ArrayList<>();
		
			try {

				switch (observationTypeForParam) {

				 case unidimensional:
				 //http://localhost:8080/phenotype-archive/stats/genes/MGI:1920000?parameterId=ESLIM_015_001_018
				
				 unidimensionalChartNTable =
				 continousChartAndTableProvider.doUnidimensionalData(experimentList.get(0),
				 chartId, parameter, accession[0], model,
				 gender, zyList, ChartType.UnidimensionalBoxPlot, false);
				 break;
				
				case categorical:
					// https://dev.mousephenotype.org/mi/impc/dev/phenotype-archive/stats/genes/MGI:1346872?parameterId=ESLIM_001_001_004

					 categoricalResultAndChart = categoricalChartAndTableProvider
							.doCategoricalData(experimentList.get(0), parameter,
									accession[0], gender, zyList, chartId,
									categoricalTables, parameterIds[0]);
					
					break;

				case time_series:
					// http://localhost:8080/PhenotypeArchive/stats/genes/MGI:1920000?parameterId=ESLIM_004_001_002

					timeSeriesForParam = timeSeriesChartAndTableProvider
							.doTimeSeriesData(experimentList.get(0), parameter, gender,
									zyList, chartId);
					
					break;

				default:
					// Trying to graph Unknown observation type
					log.error("Unknown how to display graph for observation type: "
							+ observationTypeForParam);
					break;
				}

			} catch (SQLException e) {
				log.error(ExceptionUtils.getFullStackTrace(e));
				statsError = true;
			}
			String unichart="";
			if(unidimensionalChartNTable!=null)unichart=unidimensionalChartNTable.getSexChartAndTables().get(0).getChart();
			model.addAttribute("unidimensionalChartsAndTable", unichart);
			model.addAttribute("categoricalResultAndChart",
					categoricalResultAndChart);
			model.addAttribute("timeSeriesChartsAndTable",
					timeSeriesForParam);
			model.addAttribute("statsError", statsError);

		}

		return "chart";
	}


//	@RequestMapping("/stats/genes/scatter/{acc}")
//	public String genesScatter(
//			@RequestParam(required = false, /*
//											 * defaultValue =
//											 * "ESLIM_001_001_007",
//											 */value = "parameterId") String[] parameterIds,
//			@RequestParam(required = false, value = "gender") String[] gender,
//			@RequestParam(required = false, value = "zygosity") String[] zygosity,
//			@RequestParam(required = false, value = "model") String[] biologicalModelsParam,
//			@RequestParam(required = false, value = "phenotypingCenter") String[] phenotypingCenter,
//			@RequestParam(required = false, value = "byMouseId", defaultValue = "false") Boolean byMouseId,
//			@PathVariable String acc, Model model)
//			throws GenomicFeatureNotFoundException, ParameterNotFoundException,
//			IOException, URISyntaxException, SolrServerException {
////TODO refactor this into the main chart code not have a method of its own!!!
//		boolean statsError = false;
//
//		GenomicFeature gene = genesDao.getGenomicFeatureByAccession(acc);
//		if (gene == null) {
//			throw new GenomicFeatureNotFoundException("Gene " + acc
//					+ " can't be found.", acc);
//		}
//
//		log.info(gene.toString());
//		model.addAttribute("gene", gene);
//
//		List<String> paramIds = getParamsAsList(parameterIds);
//		List<String> genderList = getParamsAsList(gender);
//		List<String> zyList = getParamsAsList(zygosity);
//		List<String> phenotypingCenters = getParamsAsList(phenotypingCenter);
//
//		List<UnidimensionalDataSet> allUnidimensionalDataSets = new ArrayList<>();
//
//		List<BiologicalModel> unidimensionalMutantBiologicalModels = new ArrayList<>();
//		List<Parameter> parameters = new ArrayList<>();
//
//		// Use the first phenotyping center passed in (ignore the others?)
//		Integer phenotypingCenterId = null;
//		if (phenotypingCenters != null && phenotypingCenters.size() > 0) {
//			try {
//				phenotypingCenterId = organisationDAO.getOrganisationByName(
//						phenotypingCenters.get(0)).getId();
//			} catch (NullPointerException e) {
//				log.error("Cannot find center ID for organisation with name "
//						+ phenotypingCenter);
//			}
//		}
//
//		for (String parameterId : paramIds) {
//			Parameter parameter = pipelineDAO.getParameterByStableIdAndVersion(
//					parameterId, 1, 0);
//			parameters.add(parameter);
//			ObservationType observationTypeForParam = Utilities
//					.checkType(parameter);
//			String[] parameterUnits = parameter.checkParameterUnits();
//			if (gene == null) {
//				throw new GenomicFeatureNotFoundException("Gene " + acc
//						+ " can't be found.", acc);
//			}
//			String xUnits = "";
//			String yUnits = "";
//
//			if (parameterUnits.length > 0) {
//				xUnits = parameterUnits[0];
//			}
//			if (parameterUnits.length > 1) {
//				yUnits = parameterUnits[1];
//			}
//
//			List<ExperimentDTO> experimentList = experimentService
//					.getExperimentDTO(parameter.getId(), acc, genderList,
//							zyList, phenotypingCenterId);
//
//			log.info("param=" + parameter.getName() + " Description="
//					+ parameter.getDescription() + " xUnits=" + xUnits
//					+ " yUnits=" + yUnits + " dataType="
//					+ observationTypeForParam);
//
//			// ESLIM_003_001_003 id=962 calorimetry data for time series graph
//			// new MGI:1926153
//			// http://localhost:8080/PhenotypeArchive/stats/genes/MGI:1926153?parameterId=ESLIM_003_001_003
//			try {
//
//				if (observationTypeForParam
//						.equals(ObservationType.unidimensional)
//						|| observationTypeForParam
//								.equals(ObservationType.time_series)) {
//
//					UnidimensionalDataSet unidimensionalChartNTables = continousChartAndTableProvider
//							.doUnidimensionalData(experimentList, bmDAO,
//									chartId,
//									parameter, acc, model, genderList, zyList,
//									ChartType.UnidimensionalScatter, byMouseId);
//					allUnidimensionalDataSets
//							.add(unidimensionalChartNTables);
//
//				} else {
//					// must be categorical
//					// we don't want scatters for categorical!
//				}
//
//			} catch (SQLException e) {
//				log.error(ExceptionUtils.getFullStackTrace(e));
//				statsError = true;
//			}
//		}// end of parameterId iterations
//
//		model.addAttribute("unidimensionalMutantBiologicalModels",
//				unidimensionalMutantBiologicalModels);
//		model.addAttribute("allUnidimensionalDataSets",
//				allUnidimensionalDataSets);
//		model.addAttribute("statsError", statsError);
//		model.addAttribute("parameters", parameters);
//		return "scatter";
//	}

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

	@ExceptionHandler(GenomicFeatureNotFoundException.class)
	public ModelAndView handleGenomicFeatureNotFoundException(
			GenomicFeatureNotFoundException exception) {
		ModelAndView mv = new ModelAndView("identifierError");
		mv.addObject("errorMessage", exception.getMessage());
		mv.addObject("acc", exception.getAcc());
		mv.addObject("type", "MGI gene");
		mv.addObject("exampleURI", "/stats/genes/MGI:104874");
		return mv;
	}

	@ExceptionHandler(ParameterNotFoundException.class)
	public ModelAndView handleParameterNotFoundException(
			ParameterNotFoundException exception) {
		ModelAndView mv = new ModelAndView("identifierError");
		mv.addObject("errorMessage", exception.getMessage());
		mv.addObject("acc", exception.getAcc());
		mv.addObject("type", "Parameter");
		mv.addObject(
				"exampleURI",
				"/stats/genes/MGI:98373?parameterId=M-G-P_014_001_001&gender=male&zygosity=homozygote&phenotypingCenter=WTSI");
		return mv;
	}

}
