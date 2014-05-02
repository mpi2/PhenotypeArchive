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

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.bcel.generic.IF_ACMPEQ;
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
import uk.ac.ebi.phenotype.error.SpecificExperimentException;
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
import uk.ac.ebi.phenotype.stats.graphs.ScatterChartAndData;
import uk.ac.ebi.phenotype.stats.graphs.ScatterChartAndTableProvider;
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
	private ScatterChartAndTableProvider scatterChartAndTableProvider;

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
	@RequestMapping("/charts")
	public String charts(
                        @RequestParam(required = false, value = "accession") String[] accessionsParams,
			@RequestParam(required = false, value = "parameter_stable_id") String[] parameterIds,
			@RequestParam(required = false, value = "gender") String[] gender,
			@RequestParam(required = false, value = "zygosity") String[] zygosity,
			@RequestParam(required = false, value = "phenotyping_center") String[] phenotypingCenter,
			@RequestParam(required = false, value = "strategy") String[] strategies,
			@RequestParam(required = false, value = "strain") String[] strains,
			@RequestParam(required = false, value = "metadata_group") String[] metadataGroup,
			@RequestParam(required = false, value = "scatter") boolean scatter,
			@RequestParam(required = false, value = "pipeline_stable_id") String [] pipelineStableIds,
                        @RequestParam(required = false, value = "allele_accession") String []alleleAccession,
			Model model)
			throws GenomicFeatureNotFoundException, ParameterNotFoundException,
			IOException, URISyntaxException, SolrServerException {
		System.out.println("calling charts");
		return createCharts(accessionsParams, pipelineStableIds,  parameterIds, gender, phenotypingCenter, strains, metadataGroup, zygosity, model, scatter, alleleAccession);
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
			@RequestParam(required = true, value = "experimentNumber") String experimentNumber,// we																	// female
			@RequestParam(required = false, value = "accession") String[] accession,
			@RequestParam(required = false, value = "strain") String strain,
                        @RequestParam(required = false, value = "allele_accession") String alleleAccession,
			@RequestParam(required = false, value = "metadata_group") String metadataGroup,
			@RequestParam(required = false, value = "parameter_stable_id") String parameterStableIds,
			@RequestParam(required = false, value = "gender") String[] gender,//only have one gender per graph
			@RequestParam(required = false, value = "zygosity") String[] zygosity,
			@RequestParam(required = false, value = "phenotyping_center") String phenotypingCenter,
			@RequestParam(required = false, value = "strategy") String[] strategies,
			@RequestParam(required = false, value = "pipeline_stable_id") String pipelineStableId,
			@RequestParam(required = false, value = "scatter") boolean scatter,
			Model model) throws GenomicFeatureNotFoundException,
			ParameterNotFoundException, IOException, URISyntaxException,
			SolrServerException,  SpecificExperimentException {
		
		UnidimensionalDataSet unidimensionalChartDataSet=null;
		ChartData timeSeriesForParam=null;
		CategoricalResultAndCharts categoricalResultAndChart=null;
		boolean statsError = false;
		// TODO need to check we don't have more than one accession and one
		// parameter throw and exception if we do

		// get the parameter internal int id as we dont' have it
		Parameter parameter = pipelineDAO.getParameterByStableId(
				parameterStableIds);
		if (parameter == null) {
			throw new ParameterNotFoundException("Parameter " + parameterStableIds
					+ " can't be found.", parameterStableIds);
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

		
		List<String> genderList = getParamsAsList(gender);
		
		// System.out.println("paramId="+parameter.getId());
		// Use the first phenotyping center passed in (ignore the others?)
		// should only now be one center at this stage for one graph/experiment
		// TODO put length check and exception here
//		List<String> phenotypingCenters = getParamsAsList(phenotypingCenter);
		Integer phenotypingCenterId = null;
		if (phenotypingCenter != null) {
			try {
				phenotypingCenterId = organisationDAO.getOrganisationByName(
						phenotypingCenter).getId();
			} catch (NullPointerException e) {
				log.error("Cannot find center ID for organisation with name "
						+ phenotypingCenter);
			}
		}

		String metaDataGroupString=null;
                if(metadataGroup!=null){
                metaDataGroupString= metadataGroup;
                }
		List<String> zyList = getParamsAsList(zygosity);
		
		Integer pipelineId=null;
		Pipeline pipeline=null;
		if(pipelineStableId!=null && !pipelineStableId.equals("")) {
		log.debug("pipe stable id="+pipelineStableId);
		pipeline=pipelineDAO.getPhenotypePipelineByStableId(pipelineStableId);
		pipelineId=pipeline.getId();//swap the human readable pipeline  id from the url to our internal id
		System.out.println("internernal pipe id="+pipelineId);
		}
           
		ExperimentDTO experiment = experimentService
				.getSpecificExperimentDTO(parameter.getId(),pipelineId,  accession[0],
						genderList, zyList,phenotypingCenterId,
						  strain , metaDataGroupString, alleleAccession);
              
		//System.out.println("experiment in chart method="+experiment);
		
		if (experiment!=null) {
			// log.info("Experiment dto marker="+experiment.getFemaleControls());
			// ESLIM_003_001_003 id=962 calorimetry data for time series graph
			// new MGI:1926153
			// http://localhost:8080/PhenotypeArchive/charts?accession=/MGI:1926153&parameterId=ESLIM_003_001_003
                 if(pipeline==null){
                    //if we don't already have the pipeline for this experiment from the url params lets get it via the experiment returned
                    pipeline=pipelineDAO.getPhenotypePipelineByStableId(experiment.getPipelineStableId());                   
                }
		String title=parameter.getName();
		String xAxisTitle=xUnits;//set up some default strings here that most graphs will use?
		String yAxisTitle= yUnits;
		//String subTitle=gender;
		BiologicalModel expBiologicalModel=bmDAO.getBiologicalModelById(experiment.getExperimentalBiologicalModelId());
		//use some sort of map to pass in these or helper method? so we don't have to redo title, subtitle for each one??
		String allelicCompositionString="unknown";
		String symbol="unknown";
		String geneticBackgroundString="unknown";
		if(expBiologicalModel!=null) {
		allelicCompositionString=expBiologicalModel.getAllelicComposition();
		symbol=expBiologicalModel.getAlleles().get(0).getSymbol();
		geneticBackgroundString=expBiologicalModel.getGeneticBackground();
		}
			try {
				if(scatter) {
					System.out.println("calling scatter!");
					
					ScatterChartAndData scatterChartAndData=scatterChartAndTableProvider.doScatterData(experiment, parameter, experimentNumber, expBiologicalModel);
					model.addAttribute("scatterChartAndData", scatterChartAndData);
				}else {

				switch (observationTypeForParam) {

				 case unidimensional:
				 //http://localhost:8080/phenotype-archive/charts?accession=/MGI:1920000&parameterId=ESLIM_015_001_018
				
//					 if(experiment.getResults().size()!=0) {
					 unidimensionalChartDataSet =
				 continousChartAndTableProvider.doUnidimensionalData(experiment,
						 experimentNumber, parameter,
				 ChartType.UnidimensionalBoxPlot, false, xAxisTitle,expBiologicalModel);
					model.addAttribute("unidimensionalChartDataSet", unidimensionalChartDataSet);
					// }
				 break;
				
				case categorical:
					// https://dev.mousephenotype.org/mi/impc/dev/phenotype-archive/stats/genes/MGI:1346872?parameterId=ESLIM_001_001_004

					 categoricalResultAndChart = categoricalChartAndTableProvider
							.doCategoricalData(experiment, parameter,
									accession[0], experimentNumber, expBiologicalModel);
					 model.addAttribute("categoricalResultAndChart",
								categoricalResultAndChart);
					
					break;

				case time_series:
					// http://localhost:8080/PhenotypeArchive/stats/genes/MGI:1920000?parameterId=ESLIM_004_001_002
					timeSeriesForParam = timeSeriesChartAndTableProvider
							.doTimeSeriesData(experiment, parameter, experimentNumber, expBiologicalModel);
					model.addAttribute("timeSeriesChartsAndTable",
							timeSeriesForParam);
					break;

				default:
					// Trying to graph Unknown observation type
					log.error("Unknown how to display graph for observation type: "
							+ observationTypeForParam);
					break;
				}
				}

			} catch (SQLException e) {
				log.error(ExceptionUtils.getFullStackTrace(e));
				statsError = true;
			}
			
			model.addAttribute("pipeline", pipeline);
			model.addAttribute("allelicCompositionString", allelicCompositionString);
			model.addAttribute("symbol", symbol);
			model.addAttribute("geneticBackgroundString", geneticBackgroundString);
			model.addAttribute("phenotypingCenter", phenotypingCenter);
			
			model.addAttribute("experimentNumber", experimentNumber);
			model.addAttribute("statsError", statsError);

		}else {
			log.info("empty experiment");
			model.addAttribute("emptyExperiment", true);
		}

		return "chart";
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

	@ExceptionHandler(GenomicFeatureNotFoundException.class)
	public ModelAndView handleGenomicFeatureNotFoundException(
			GenomicFeatureNotFoundException exception) {
		ModelAndView mv = new ModelAndView("identifierError");
		mv.addObject("errorMessage", exception.getMessage());
		mv.addObject("acc", exception.getAcc());
		mv.addObject("type", "MGI gene");
		mv.addObject("exampleURI", "/charts?accession=MGI:104874");
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
				"/charts?accession=MGI:98373&parameterId=M-G-P_014_001_001&gender=male&zygosity=homozygote&phenotypingCenter=WTSI");
		return mv;
	}
	
	@ExceptionHandler(SpecificExperimentException.class)
	public ModelAndView handleSpecificExperimentException(
			ParameterNotFoundException exception) {
		ModelAndView mv = new ModelAndView("Specific Experiment Not Found Error");
		mv.addObject("errorMessage", exception.getMessage());
		mv.addObject("acc", exception.getAcc());
		mv.addObject("type", "Parameter");
		return mv;
	}

    private String createCharts(String[] accessionsParams,String[] pipelineStableIdsArray, String[] parameterIds, String[] gender, String[] phenotypingCenter, String[] strains, String[] metadataGroup, String[] zygosity, Model model, boolean scatter,String[] alleleAccession) throws SolrServerException, GenomicFeatureNotFoundException, ParameterNotFoundException {
        GraphUtils graphUtils = new GraphUtils(experimentService);
        List<String> geneIds = getParamsAsList(accessionsParams);
        List<String> paramIds = getParamsAsList(parameterIds);
        List<String> genderList = getParamsAsList(gender);
        List<String> phenotypingCentersList=getParamsAsList(phenotypingCenter);
        List<String> strainsList=getParamsAsList(strains);
        List<String> metadataGroups=getParamsAsList(metadataGroup);
        List<String> pipelineStableIds=getParamsAsList(pipelineStableIdsArray);
        List<String> alleleAccessions=getParamsAsList(alleleAccession);
        if (genderList.isEmpty()) {// add them explicitly here so graphs urls
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
        
        //List<Parameter> parameters = new ArrayList<>();
        
        Set<String> allGraphUrlSet = new LinkedHashSet<>();
       for(String geneId: geneIds){
           GenomicFeature gene = genesDao.getGenomicFeatureByAccession(geneId);
        if (gene == null) {
                throw new GenomicFeatureNotFoundException("Gene " + geneId
                                + " can't be found.", geneId);
        }

        // TODO: Implement control selection stragety by center
        // ControlSelectionStrategy controlSelectionStrategy =
        // experimentService.getControlSelectionStrategy(phenotypingCenter,
        // strategies);
        log.info(gene.toString());
        model.addAttribute("gene", gene);
        for (String parameterId : paramIds) {

                Parameter parameter = pipelineDAO.getParameterByStableId(
                                parameterId);
                if (parameter == null) {
                        throw new ParameterNotFoundException("Parameter " + parameterId
                                        + " can't be found.", parameterId);
                }
                // instead of an experiment list here we need just the outline of
                // the experiments - how many, observation types
                Set<String> graphUrlsForParam = graphUtils.getGraphUrls(geneId,
                                parameter.getStableId(),pipelineStableIds, genderList, zyList, phenotypingCentersList, strainsList, metadataGroups, scatter, alleleAccessions);
                allGraphUrlSet.addAll(graphUrlsForParam);

        }// end of parameterId iterations
}//end of gene iterations
        model.addAttribute("allGraphUrlSet", allGraphUrlSet);
        //model.addAttribute("parameters", parameters);
        return "stats";
    }

}
