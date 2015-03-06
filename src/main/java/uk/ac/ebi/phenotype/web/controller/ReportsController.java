package uk.ac.ebi.phenotype.web.controller;

import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.ac.ebi.phenotype.dao.SexualDimorphismDAO;
import uk.ac.ebi.phenotype.service.ImageService;
import uk.ac.ebi.phenotype.service.ReportsService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author tudose
 * @since Feb 2015
 */

@Controller
public class ReportsController {

	@Autowired 
	ImageService is;
	
	@Autowired
	SexualDimorphismDAO sdDAO;

    @Resource(name = "globalConfiguration")
	Map<String, String> config;
    
    @Autowired
	ReportsService rService;
    
	@RequestMapping(value="/reports/getLaczSpreadsheet", method = RequestMethod.GET)
	public void getFullData(HttpSession session, HttpServletRequest request, HttpServletResponse response) throws IOException{
		
	    List<String[]> result = is.getLaczExpressionSpreadsheet();
	    ControllerUtils.writeAsCSV(result, "impc_lacz_expression.csv", response);
	}
	
	@RequestMapping(value="/reports/sexualDimorphism", method = RequestMethod.GET)
	public void getSexualDimorphismReport(HttpSession session, HttpServletRequest request, HttpServletResponse response) throws IOException{
			
		List<String[]> result = sdDAO.sexualDimorphismReportNoBodyWeight(config.get("drupalBaseUrl")+ "/data");
	    ControllerUtils.writeAsCSV(result, "sexual_dimorphism_no_body_weight_IMPC.csv", response);
	}
	
	@RequestMapping(value="/reports/sexualDimorphismWithBodyWeight", method = RequestMethod.GET)
	public void getSexualDimorphismWithBodyWeightReport(HttpSession session, HttpServletRequest request, HttpServletResponse response) throws IOException{
			
		List<String[]> result = sdDAO.sexualDimorphismReportWithBodyWeight(config.get("drupalBaseUrl") + "/data");
	    ControllerUtils.writeAsCSV(result, "sexual_dimorphism_with_body_weight_IMPC.csv", response);
	}
	
	@RequestMapping(value="/reports/mpCallDistribution", method = RequestMethod.GET)
	public void getMpCallDistribution(HttpSession session, HttpServletRequest request, HttpServletResponse response) throws IOException{
			
		List<List<String[]>> result = rService.getMpCallDistribution();
	    ControllerUtils.writeAsCSVMultipleTables(result, "mp_call_distribution.csv", response);
	}
		
	@RequestMapping(value="/reports/hitsPerLine", method = RequestMethod.GET)
	public void getHitsPerLine(HttpSession session, HttpServletRequest request, HttpServletResponse response) throws IOException{
			
		List<List<String[]>> result = rService.getHitsPerLine();
	    ControllerUtils.writeAsCSVMultipleTables(result, "hits_per_line.csv", response);
	}

	@RequestMapping(value="/reports/hitsPerPP", method = RequestMethod.GET)
	public void getHitsPerPP(HttpSession session, HttpServletRequest request, HttpServletResponse response) throws IOException{
			
		List<List<String[]>> result = rService.getHitsPerParamProcedure();
	    ControllerUtils.writeAsCSVMultipleTables(result, "hits_per_parameter_procedure.csv", response);
	}
	
	@RequestMapping(value="/reports/dataOverview", method = RequestMethod.GET)
	public void getDataOverview(HttpSession session, HttpServletRequest request, HttpServletResponse response) throws IOException{
			
		List<List<String[]>> result = rService.getDataOverview();
	    ControllerUtils.writeAsCSVMultipleTables(result, "data_overview.csv", response);
	}


	@RequestMapping(value="/reports/fertility", method = RequestMethod.GET)
	public void getFertilityReport(HttpServletResponse response) throws IOException, SolrServerException {

		List<String[]> result = rService.getFertilityData();
		ControllerUtils.writeAsCSV(result, "fertility_report.csv", response);
	}


	};
	
	@RequestMapping(value="/reports/viability", method = RequestMethod.GET)
	public void getViabilityReport(HttpSession session, HttpServletRequest request, HttpServletResponse response) throws IOException{
			
		List<List<String[]>> result = rService.getViabilityReport();
	    ControllerUtils.writeAsCSVMultipleTables(result, "viability_report.csv", response);
	};
	
	@RequestMapping(value="/reports", method = RequestMethod.GET)
	public String defaultAction(HttpSession session, HttpServletRequest request, HttpServletResponse response) throws IOException{
		return "reports";
	}
	
}

