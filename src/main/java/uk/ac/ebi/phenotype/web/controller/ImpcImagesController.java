package uk.ac.ebi.phenotype.web.controller;

import javax.servlet.http.HttpServletRequest;

//import omero.*;
//import omero.api.ServiceFactoryPrx;

import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

//import Glacier2.CannotCreateSessionException;
//import Glacier2.PermissionDeniedException;

@Controller
public class ImpcImagesController {
	@RequestMapping("/impcImages*")
	public String allImages(
			@RequestParam(required = false, defaultValue = "0", value = "start") int start,
			@RequestParam(required = false, defaultValue = "25", value = "length") int length,
			@RequestParam(required = false, defaultValue = "*:*", value = "q") String qIn,
			@RequestParam(required = false, defaultValue = "", value = "phenotype_id") String mpId,
			@RequestParam(required = false, defaultValue = "", value = "gene_id") String geneId,
			@RequestParam(required = false, defaultValue = "", value = "fq") String[] filterField,
			@RequestParam(required = false, defaultValue = "", value = "facet.field") String facetField,
			@RequestParam(required = false, defaultValue = "", value = "qf") String qf,
			@RequestParam(required = false, defaultValue = "", value = "defType") String defType,
			@RequestParam(required = false, defaultValue = "", value = "anatomy_id") String maId,
			HttpServletRequest request, Model model) throws SolrServerException {

		
		//for a test lets just get some image ids and display them here
		
		
		
		
System.out.println("calling impcImages web page");
		return "impcImages";
	}
}
