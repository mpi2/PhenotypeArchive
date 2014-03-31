package uk.ac.ebi.phenotype.web.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import uk.ac.ebi.phenotype.dao.DiscreteTimePoint;
import uk.ac.ebi.phenotype.dao.PhenotypePipelineDAO;
import uk.ac.ebi.phenotype.data.impress.Utilities;
import uk.ac.ebi.phenotype.imaging.springrest.images.dao.ImagesSolrDao;
import uk.ac.ebi.phenotype.pojo.ObservationType;
import uk.ac.ebi.phenotype.pojo.Parameter;
import uk.ac.ebi.phenotype.stats.ChartData;
import uk.ac.ebi.phenotype.stats.ExperimentService;
import uk.ac.ebi.phenotype.stats.GenotypePhenotypeService;
import uk.ac.ebi.phenotype.stats.ObservationService;
import uk.ac.ebi.phenotype.stats.StackedBarsData;
import uk.ac.ebi.phenotype.stats.categorical.CategoricalChartAndTableProvider;
import uk.ac.ebi.phenotype.stats.categorical.CategoricalSet;
import uk.ac.ebi.phenotype.stats.timeseries.TimeSeriesChartAndTableProvider;
import uk.ac.ebi.phenotype.stats.unidimensional.UnidimensionalChartAndTableProvider;


@Controller
public class OverviewChartsController {
	

	@Autowired
	private PhenotypePipelineDAO pipelineDao;

	@Autowired
	ObservationService os;
	
	@Autowired
	GenotypePhenotypeService gpService;
	
	ArrayList<String> strains;
	
	public OverviewChartsController(){
		 strains = new ArrayList<>();
			strains.add("MGI:2159965");
			strains.add("MGI:2164831");
	}
	
	@RequestMapping(value="/overviewCharts/{phenotype_id}", method=RequestMethod.GET)
	public String getGraph(
		@PathVariable String phenotype_id, 
		@RequestParam(required = true, value = "parameter_id") String parameterId,
		@RequestParam(required = false, value = "center") String center,
		@RequestParam(required = false, value = "sex") String sex,
		@RequestParam(required = false, value = "source") String source,
		@RequestParam(required = false, value = "all_centers") String allCenters,
		Model model,
		HttpServletRequest request,
		RedirectAttributes attributes) throws SolrServerException, IOException, URISyntaxException, SQLException{
		
			String[] centerArray = (center != null) ? center.split(",") : null;
			String[] sexArray = (sex != null) ? sex.split(",") : null;
			String[] sourceArray = (source != null) ? source.split(",") : null;
			String[] allCentersArray = (allCenters != null) ? allCenters.split(",") : null;

			String[] centers = (centerArray != null) ? centerArray : allCentersArray;
			
			model.addAttribute("chart", getDataOverviewChart(phenotype_id, model, parameterId, centers, sexArray, sourceArray));
			return "overviewChart";
	}
	
	public ChartData getDataOverviewChart(String mpId, Model model, String parameter, String[] center, String[] sex, String[] source) throws SolrServerException, IOException, URISyntaxException, SQLException{
		
		CategoricalChartAndTableProvider cctp = new CategoricalChartAndTableProvider();
		TimeSeriesChartAndTableProvider tstp = new TimeSeriesChartAndTableProvider();
		UnidimensionalChartAndTableProvider uctp = new UnidimensionalChartAndTableProvider();
		Parameter p = pipelineDao.getParameterByStableIdAndVersion(parameter, 1, 0);
		ChartData chartRes = null;
		List<String> genes = null;
		String[] centerToFilter = center;
		
		if (p != null){
						
			genes = gpService.getGenesAssocByParamAndMp(parameter, mpId, source);
		
			if (centerToFilter == null) { // first time we load the page.
				// We need to know centers for the controls, otherwise we show all controls
				Set <String> tempCenters = os.getCenters(p, genes, strains, "experimental");
				centerToFilter = tempCenters.toArray(new String[0]);
			}
			
			if( Utilities.checkType(p).equals(ObservationType.categorical) ){
				CategoricalSet controlSet = os.getCategories(p, null , "control", strains, centerToFilter, sex);
				controlSet.setName("Control");
				CategoricalSet mutantSet = os.getCategories(p, (ArrayList<String>) genes, "experimental", strains, centerToFilter, sex);
				mutantSet.setName("Mutant");
				chartRes = cctp.doCategoricalDataOverview(controlSet, mutantSet, model, p).get(0);
			}
			
			else if ( Utilities.checkType(p).equals(ObservationType.time_series) ){
				Map<String, List<DiscreteTimePoint>> data = new HashMap<String, List<DiscreteTimePoint>>(); 
				data.put("Control", os.getTimeSeriesControlData(parameter, strains, centerToFilter, sex));
				data.putAll(os.getTimeSeriesMutantData(parameter, genes, strains, centerToFilter, sex));
				ChartData chart = tstp.doTimeSeriesOverviewData(data, p);
				chart.setId(parameter);
				chartRes = chart;
			}
			
			else if ( Utilities.checkType(p).equals(ObservationType.unidimensional) ){
				StackedBarsData data = os.getUnidimensionalData(p, genes, strains, "experimental", centerToFilter, sex);
				chartRes = uctp.getStackedHistogram(data, p);
			}
			
			if (chartRes != null && center == null && sex == null){ // we don't do a filtering
				// we want to offer all filter values, not to eliminate males if we filtered on males
				// plus we don't want to do another SolR call each time to get the same data
				Set<String> centerFitlers =	os.getCenters(p, genes, strains, "experimental");
				model.addAttribute("centerFilters", centerFitlers);
			}
		}
		
		return chartRes;
	}
	
}
