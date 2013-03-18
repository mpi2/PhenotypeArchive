/**
 * Copyright © 2011-2013 EMBL - European Bioinformatics Institute
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
package uk.ac.ebi.phenotype.web.controller;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.WordUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import uk.ac.ebi.generic.util.BasicStats;
import uk.ac.ebi.phenotype.dao.BiologicalModelDAO;
import uk.ac.ebi.phenotype.dao.CategoricalStatisticsDAO;
import uk.ac.ebi.phenotype.dao.GenomicFeatureDAO;
import uk.ac.ebi.phenotype.dao.PhenotypeCallSummaryDAO;
import uk.ac.ebi.phenotype.dao.PhenotypeCallSummaryDAOImpl;
import uk.ac.ebi.phenotype.dao.PhenotypePipelineDAO;
import uk.ac.ebi.phenotype.dao.DiscreteTimePoint;
import uk.ac.ebi.phenotype.dao.TimeSeriesStatisticsDAO;
import uk.ac.ebi.phenotype.dao.UnidimensionalStatisticsDAO;
import uk.ac.ebi.phenotype.error.GenomicFeatureNotFoundException;
import uk.ac.ebi.phenotype.pojo.BiologicalModel;
import uk.ac.ebi.phenotype.pojo.CategoricalTableObject;
import uk.ac.ebi.phenotype.pojo.Datasource;
import uk.ac.ebi.phenotype.pojo.GenomicFeature;
import uk.ac.ebi.phenotype.pojo.ObservationType;
import uk.ac.ebi.phenotype.pojo.Parameter;
import uk.ac.ebi.phenotype.pojo.PhenotypeCallSummary;
import uk.ac.ebi.phenotype.pojo.Pipeline;
import uk.ac.ebi.phenotype.pojo.Procedure;
import uk.ac.ebi.phenotype.pojo.SexType;
import uk.ac.ebi.phenotype.pojo.TimeSeriesControlView;
import uk.ac.ebi.phenotype.pojo.ZygosityType;
import uk.ac.ebi.phenotype.stats.PipelineProcedureData;
import uk.ac.ebi.phenotype.stats.PipelineProcedureTablesCreator;
import uk.ac.ebi.phenotype.stats.categorical.CategoricalChartAndTableProvider;
import uk.ac.ebi.phenotype.stats.continuous.ContinousChartAndTableProvider;
import uk.ac.ebi.phenotype.stats.timeseries.TimeSeriesChartAndTableProvider;
import uk.ac.ebi.phenotype.stats.timeseries.TimeSeriesStats;
import uk.ac.ebi.phenotype.data.impress.Utilities;

@Controller
public class StatsController implements BeanFactoryAware {

	private final Logger log = LoggerFactory.getLogger(StatsController.class);
	private BeanFactory bf;

	@Autowired
	private BiologicalModelDAO bmDAO;

	@Autowired
	private PhenotypePipelineDAO pipelineDAO;

	@Autowired
	private GenomicFeatureDAO genesDao;

	@Autowired
	private CategoricalStatisticsDAO categoricalStatsDao;
	
	@Autowired
	private UnidimensionalStatisticsDAO unidimensionalStatisticsDAO;
	
	@Autowired
	private TimeSeriesStatisticsDAO timeSeriesStatisticsDAO;

	@Autowired
	private PhenotypeCallSummaryDAO phenoDAO;
	@Autowired
	private CategoricalChartAndTableProvider categoricalChartAndTableProvider;
	@Autowired
	private TimeSeriesChartAndTableProvider timeSeriesChartAndTableProvider;
	@Autowired
	private ContinousChartAndTableProvider continousChartAndTableProvider;
	@Autowired
	private PhenotypeCallSummaryDAO phenotypeCallSummaryDAO;
		
	/**
	 * Runs when the request missing an accession ID. This redirects to the
	 * search page which defaults to showing all genes in the list
	 */
	@RequestMapping("/stats")
	public String rootForward() {
		return "redirect:/search";
	}

	@RequestMapping("/stats/genes/{acc}")
	public String genesStats(
			@RequestParam(required = false, /*defaultValue = "ESLIM_001_001_007",*/ value = "parameterId") String[] parameterIds,
			@RequestParam(required = false, value = "gender") String[] gender,
			@RequestParam(required = false, value = "zygosity") String[] zygosity,
			@RequestParam(required = false, value = "model") String[] biologicalModelsParam,
			@RequestParam(required = false, defaultValue="false", value = "categorical") String categorical,
			@PathVariable String acc, Model model, HttpServletRequest request,
			RedirectAttributes attributes)
			throws GenomicFeatureNotFoundException {

		// Get the global application configuration
		@SuppressWarnings("unchecked")
		Map<String, String> config = (Map<String, String>) bf
				.getBean("globalConfiguration");

//		categoricalChartAndTableProvider=new CategoricalChartAndTableProvider(categoricalStatsDao);
//		timeSeriesChartAndTableProvider=new TimeSeriesChartAndTableProvider(timeSeriesStatisticsDAO);
//		continousChartAndTableProvider=new ContinousChartAndTableProvider(unidimensionalStatisticsDAO);
		GenomicFeature gene = genesDao.getGenomicFeatureByAccession(acc);
		System.out.println(gene);
		if (gene == null) {
			throw new GenomicFeatureNotFoundException("Gene " + acc
					+ " can't be found.", acc);
		}
		model.addAttribute("gene", gene);

		List<String> paramIds = getParamsAsList(parameterIds);
		List<String> genderList = getParamsAsList(gender);
		List<String> zyList=getParamsAsList(zygosity);
		List<String> biologicalModelsParams=getParamsAsList(biologicalModelsParam);
		
		if(paramIds.isEmpty() && genderList.isEmpty() && zyList.isEmpty() && biologicalModelsParams.isEmpty()){
			System.out.println("Gene Accession without any web params");
			//a method here to get a general page for Gene and all procedures associated
			List<PhenotypeCallSummary> allPhenotypeSummariesForGene = phenotypeCallSummaryDAO.getPhenotypeCallByAccession(acc);
			//a method here to get a general page for Gene and all procedures associated
			List<Pipeline> pipelines = pipelineDAO.getAllPhenotypePipelines();
			PipelineProcedureTablesCreator creator=new PipelineProcedureTablesCreator();
			List<PipelineProcedureData> dataForTables=creator.createArraysForTables(pipelines, allPhenotypeSummariesForGene, gene);
//			for(PipelineProcedureData dataForATable: dataForTables){
//				dataForATable.getTableData();
//			}
			model.addAttribute("pipelineProcedureData", dataForTables);
			model.addAttribute("allPipelines", pipelines);//limit pipelines to two for testing
			return "procedures";
		}
		
		

		// MGI:105313 male het param 655
		// System.out.println("acc=" + acc);
		List<JSONObject> charts = new ArrayList<JSONObject>();
		List<String> continuouscharts = new ArrayList<String>();
		List<String> continuousBarCharts = new ArrayList<String>();
		List<String> timeSeriesCharts = new ArrayList<String>();
		List<String> categoricalBarCharts=new ArrayList<String>();
		List<CategoricalTableObject> tables=new ArrayList<CategoricalTableObject>();
		// param 655
		// female homzygote
		// population id=4640 or 4047 - male, het.
		
		
		
		int chartNumber = 1;
		for (String parameterId : paramIds) {
			
			Parameter parameter = pipelineDAO.getParameterByStableIdAndVersion(parameterId, 1, 0);
			String[] parameterUnits=parameter.checkParameterUnits();
			String xUnits="";
			String yUnits="";
			
			if(parameterUnits.length>0){
			 xUnits=parameterUnits[0];
			}
			if(parameterUnits.length>1){
				yUnits=parameterUnits[1];
			}
			ObservationType observationTypeForParam=Utilities.checkType(parameter);
			System.out.println("param="+parameter.getName()+" Description="+parameter.getDescription()+ " xUnits="+xUnits + " yUnits="+yUnits + " dataType="+observationTypeForParam);
			
			if(observationTypeForParam.equals(ObservationType.time_series)){
				//http://localhost:8080/PhenotypeArchive/stats/genes/MGI:1920000?parameterId=ESLIM_004_001_002
				timeSeriesChartAndTableProvider.doTimeSeriesData(parameter, acc , model, genderList, zyList, timeSeriesCharts, biologicalModelsParams);
			}
			
			if(observationTypeForParam.equals(ObservationType.unidimensional)){
				//http://localhost:8080/phenotype-archive/stats/genes/MGI:1920000?parameterId=ESLIM_015_001_018
				continousChartAndTableProvider.doContinuousData(parameter, acc , model, genderList, zyList, continuouscharts, continuousBarCharts);
			}
			if(observationTypeForParam.equals(ObservationType.categorical)){
				//https://dev.mousephenotype.org/mi/impc/dev/phenotype-archive/stats/genes/MGI:1346872?parameterId=ESLIM_001_001_004
				categoricalChartAndTableProvider.doCategoricalData(categoricalBarCharts, parameter, acc, model, genderList, zyList,
				biologicalModelsParams, charts, tables, 
				parameterId);
			}
		}// end of parameterId iterations

		model.addAttribute("timeSeriesCharts", timeSeriesCharts);
		model.addAttribute("categoricalBarCharts", categoricalBarCharts);
		model.addAttribute("tables", tables);
		return "stats";
	}


	
	
	private List<String> getParamsAsList(String[] parameterIds) {
		List<String> paramIds = null;
		if (parameterIds == null) {
			 paramIds=Collections.EMPTY_LIST;
		}else{
			paramIds = Arrays.asList(parameterIds); 
		}
		return paramIds;
	}



	/**
	 * required for implementing BeanFactoryAware
	 * 
	 * @see org.springframework.beans.factory.BeanFactoryAware#setBeanFactory(org.springframework.beans.factory.BeanFactory)
	 */
	@Override
	public void setBeanFactory(BeanFactory arg0) throws BeansException {
		this.bf = arg0;
	}

	@RequestMapping("/statsOld/genes/{acc}")
	public String genesStats2(Model model, HttpServletRequest request,
			RedirectAttributes attributes) {
		return "statsOld";
	}
	

}
