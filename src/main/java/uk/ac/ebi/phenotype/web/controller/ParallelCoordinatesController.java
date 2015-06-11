package uk.ac.ebi.phenotype.web.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.servlet.http.HttpServletRequest;

import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import uk.ac.ebi.phenotype.error.GenomicFeatureNotFoundException;
import uk.ac.ebi.phenotype.service.ObservationService;

@Controller
public class ParallelCoordinatesController {
	
	@Autowired 
	ObservationService os;

	@RequestMapping(value="/parallel", method=RequestMethod.GET)
	public String getGraph(Model model,	HttpServletRequest request,	RedirectAttributes attributes) 
	throws SolrServerException{
		
		model.addAttribute("procedure", "Clinical Blood Chemistry");
		String data = os.getMeansFor("IMPC_CBC_*");
		System.out.println(data);
		model.addAttribute("dataJs", data + ";");
		
		return "parallel";
	}
	
}
