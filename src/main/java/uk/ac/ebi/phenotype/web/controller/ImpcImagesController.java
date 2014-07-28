package uk.ac.ebi.phenotype.web.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

//import omero.*;
//import omero.api.ServiceFactoryPrx;

import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import uk.ac.ebi.phenotype.service.ImageService;
import uk.ac.ebi.phenotype.service.dto.ImageDTO;

//import Glacier2.CannotCreateSessionException;
//import Glacier2.PermissionDeniedException;

@Controller
public class ImpcImagesController {

	@Autowired
	ImageService imageService;


	@RequestMapping("/impcImages*")
	public String allImages(HttpServletRequest request, Model model)
	throws SolrServerException {
		
		//http://localhost:8080/phenotype-archive/impcImages?q=observation_type:image_record&rows=100
		String solrQueryString=request.getQueryString();
		System.out.println("impcImages query="+solrQueryString);
		System.out.println("calling impcImages web page");
		
		
		List<ImageDTO> imageDTOs = imageService.getImageDTOsForSolrQuery(solrQueryString);
		model.addAttribute("images", imageDTOs);
		model.addAttribute("imageCount", imageDTOs.size());//need to change this to the potential number we could get
		for (ImageDTO imageDTO : imageDTOs) {
			System.out.println(imageDTO.getOmeroId());
		}
		return "impcImages";
	}
}
