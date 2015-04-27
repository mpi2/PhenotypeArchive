package uk.ac.ebi.phenotype.web.controller;

import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import uk.ac.ebi.phenotype.chart.*;
import uk.ac.ebi.phenotype.dao.DiscreteTimePoint;
import uk.ac.ebi.phenotype.dao.PhenotypePipelineDAO;
import uk.ac.ebi.phenotype.data.impress.Utilities;
import uk.ac.ebi.phenotype.pojo.ObservationType;
import uk.ac.ebi.phenotype.pojo.Parameter;
import uk.ac.ebi.phenotype.service.ObservationService;
import uk.ac.ebi.phenotype.service.PostQcService;
import uk.ac.ebi.phenotype.service.StatisticalResultService;

import javax.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.*;


@Controller
public class OverviewChartsController {
	
	// B6N strains to use for the overview & stats , see https://www.ebi.ac.uk/panda/jira/browse/MPII-781
	public static final ArrayList<String> OVERVIEW_STRAINS = new ArrayList(Arrays.asList("MGI:2159965", "MGI:2164831", "MGI:3056279", "MGI:2683688"));
	
	@Autowired
	private PhenotypePipelineDAO pipelineDao;

	@Autowired
	ObservationService os;
	
	@Autowired
	PostQcService gpService;
	

	@Autowired
	StatisticalResultService srs;

	@Autowired
	Utilities impressUtilities;

	public OverviewChartsController(){

	}
	
	@RequestMapping(value="/overviewCharts/{phenotype_id}", method=RequestMethod.GET)
	public String getGraph(
		@PathVariable String phenotype_id, 
		@RequestParam(required = true, value = "parameter_id") String parameterId,
		@RequestParam(required = false, value = "center") String center,
		@RequestParam(required = false, value = "sex") String sex,
		@RequestParam(required = false, value = "all_centers") String allCenters,
		Model model,
		HttpServletRequest request,
		RedirectAttributes attributes) throws SolrServerException, IOException, URISyntaxException, SQLException{
		
			String[] centerArray = (center != null) ? center.split(",") : null;
			String[] sexArray = (sex != null) ? sex.split(",") : null;
			String[] allCentersArray = (allCenters != null) ? allCenters.split(",") : null;

			String[] centers = (centerArray != null) ? centerArray : allCentersArray;
			
			model.addAttribute("chart", getDataOverviewChart(phenotype_id, model, parameterId, centers, sexArray));
			return "overviewChart";
	}
	
	public ChartData getDataOverviewChart(String mpId, Model model, String parameter, String[] center, String[] sex) 
	throws SolrServerException, IOException, URISyntaxException, SQLException{
		
		CategoricalChartAndTableProvider cctp = new CategoricalChartAndTableProvider();
		TimeSeriesChartAndTableProvider tstp = new TimeSeriesChartAndTableProvider();
		UnidimensionalChartAndTableProvider uctp = new UnidimensionalChartAndTableProvider();
		Parameter p = pipelineDao.getParameterByStableId(parameter);
		ChartData chartRes = null;
		List<String> genes = null;
		String[] centerToFilter = center;
		
		
		// Assuming that different versions of a procedure will keep the same name. 
		String procedureName = p.getProcedures().iterator().next().getName();
		
		if (p != null){
						
			genes = gpService.getGenesAssocByParamAndMp(parameter, mpId);
		
			if (centerToFilter == null) { // first time we load the page.
				// We need to know centers for the controls, otherwise we show all controls
				Set <String> tempCenters = os.getCenters(p, genes, OVERVIEW_STRAINS, "experimental");
				centerToFilter = tempCenters.toArray(new String[0]);
			} else {

				System.out.println("CENTER :: " + center.length + " " + center[0]);
			}
			
			if( impressUtilities.checkType(p).equals(ObservationType.categorical) ){
				CategoricalSet controlSet = os.getCategories(p, null , "control", OVERVIEW_STRAINS, centerToFilter, sex);
				controlSet.setName("Control");
				CategoricalSet mutantSet = os.getCategories(p, (ArrayList<String>) genes, "experimental", OVERVIEW_STRAINS, centerToFilter, sex);
				mutantSet.setName("Mutant");
				List<ChartData> chart = cctp.doCategoricalDataOverview(controlSet, mutantSet, model, p, procedureName);
				if (chart.size() > 0){
					chartRes = chart.get(0);
				}
			}
			
			else if ( impressUtilities.checkType(p).equals(ObservationType.time_series) ){
				Map<String, List<DiscreteTimePoint>> data = new HashMap<String, List<DiscreteTimePoint>>(); 
				data.put("Control", os.getTimeSeriesControlData(parameter, OVERVIEW_STRAINS, centerToFilter, sex));
				data.putAll(os.getTimeSeriesMutantData(parameter, genes, OVERVIEW_STRAINS, centerToFilter, sex));
				ChartData chart = tstp.doTimeSeriesOverviewData(data, p);
				chart.setId(parameter);
				chartRes = chart;
			}
			
			else if ( impressUtilities.checkType(p).equals(ObservationType.unidimensional) ){
				StackedBarsData data = srs.getUnidimensionalData(p, genes, OVERVIEW_STRAINS, "experimental", centerToFilter, sex);
				chartRes = uctp.getStackedHistogram(data, p, procedureName);
			}
			
			if (chartRes != null && center == null && sex == null){ // we don't do a filtering
				// we want to offer all filter values, not to eliminate males if we filtered on males
				// plus we don't want to do another SolR call each time to get the same data
				Set<String> centerFitlers =	os.getCenters(p, genes, OVERVIEW_STRAINS, "experimental");
				model.addAttribute("centerFilters", centerFitlers);
			}
		}
		
		return chartRes;
	}
	
}
