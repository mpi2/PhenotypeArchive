package uk.ac.ebi.phenotype.web.controller;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import uk.ac.ebi.phenotype.service.ImageService;


@Controller
public class ReportsController {

	@Autowired 
	ImageService is;
	
	
	@RequestMapping(value="/getLaczSpreadsheet", method = RequestMethod.GET)
	public void getFullData(HttpSession session, HttpServletRequest request, HttpServletResponse response) throws IOException{
		
		System.out.println("CALLED");
	    String result = is.getLaczExpressionSpreadsheet();
	    ControllerUtils.writeAsCSV(result, "impcLaczExpression.csv", response);
	    
	};
	
	@RequestMapping(value="/reports", method = RequestMethod.GET)
	public String defaultAction(HttpSession session, HttpServletRequest request, HttpServletResponse response) throws IOException{
		return "reports";
	};
	
}

