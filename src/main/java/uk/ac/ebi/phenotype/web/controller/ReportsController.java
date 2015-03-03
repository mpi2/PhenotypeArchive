package uk.ac.ebi.phenotype.web.controller;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.sun.jna.Function.PostCallRead;

import uk.ac.ebi.phenotype.dao.SexualDimorphismDAO;
import uk.ac.ebi.phenotype.service.ImageService;
import uk.ac.ebi.phenotype.service.PostQcService;
import uk.ac.ebi.phenotype.service.ReportsService;
import uk.ac.ebi.phenotype.service.StatisticalResultService;

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
	};
	
	@RequestMapping(value="/reports/sexualDimorphism", method = RequestMethod.GET)
	public void getSexualDimorphismReport(HttpSession session, HttpServletRequest request, HttpServletResponse response) throws IOException{
			
		List<String[]> result = sdDAO.sexualDimorphismReportNoBodyWeight(config.get("drupalBaseUrl")+ "/data");
	    ControllerUtils.writeAsCSV(result, "sexual_dimorphism_no_body_weight_IMPC.csv", response);
	};
	
	@RequestMapping(value="/reports/sexualDimorphismWithBodyWeight", method = RequestMethod.GET)
	public void getSexualDimorphismWithBodyWeightReport(HttpSession session, HttpServletRequest request, HttpServletResponse response) throws IOException{
			
		List<String[]> result = sdDAO.sexualDimorphismReportWithBodyWeight(config.get("drupalBaseUrl")+ "/data");
	    ControllerUtils.writeAsCSV(result, "sexual_dimorphism_with_body_weight_IMPC.csv", response);
	};
	@RequestMapping(value="/reports/mpCallDistribution", method = RequestMethod.GET)
	public void getMpCallDistribution(HttpSession session, HttpServletRequest request, HttpServletResponse response) throws IOException{
			
		List<List<String[]>> result = rService.getMpCallDistribution();
	    ControllerUtils.writeAsCSVMultipleTables(result, "mp_call_distribution.csv", response);
	};
		
	@RequestMapping(value="/reports", method = RequestMethod.GET)
	public String defaultAction(HttpSession session, HttpServletRequest request, HttpServletResponse response) throws IOException{
		return "reports";
	};
	
}

