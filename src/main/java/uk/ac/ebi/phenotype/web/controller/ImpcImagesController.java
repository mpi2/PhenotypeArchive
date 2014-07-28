package uk.ac.ebi.phenotype.web.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

//import omero.*;
//import omero.api.ServiceFactoryPrx;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import uk.ac.ebi.generic.util.JSONRestUtil;
import uk.ac.ebi.phenotype.service.ImageService;
import uk.ac.ebi.phenotype.service.dto.ImageDTO;
import uk.ac.ebi.phenotype.service.dto.ImageDTOWrapper;

//import Glacier2.CannotCreateSessionException;
//import Glacier2.PermissionDeniedException;

@Controller
public class ImpcImagesController {

	@Autowired
	ImageService imageService;


	@RequestMapping("/impcImages*")
	public String allImages(HttpServletRequest request, Model model)
	throws SolrServerException, IOException, URISyntaxException {
		
		//http://localhost:8080/phenotype-archive/impcImages?q=observation_type:image_record&rows=100
		String solrQueryString=request.getQueryString();
		System.out.println("impcImages query="+solrQueryString);
		System.out.println("calling impcImages web page");
		
		
		this.sendQueryStringToSolr(request, model);
		return "impcImages";
	}
	
	private void sendQueryStringToSolr(HttpServletRequest request, Model model)
	throws IOException, URISyntaxException, SolrServerException {

String queryString = request.getQueryString();
String startString = "0";
String rowsString = "25";//the number of images passed back for each solr request
if (request.getParameter("start") != null) {
	startString = request.getParameter("start");
}
//if (request.getParameter("rows") != null) {
//	rowsString = request.getParameter("rows");
//}

// Map params = request.getParameterMap();
// Map newParamsMap=new HashMap<>();
// newParamsMap.putAll(params);
// newParamsMap.remove("rows");
// newParamsMap.remove("start");
String newQueryString = "";
Enumeration keys = request.getParameterNames();
while (keys.hasMoreElements()) {
	String key = (String) keys.nextElement();
	//System.out.println("key=" + key);

	// To retrieve a single value
	String value = request.getParameter(key);
	//System.out.println("value=" + value);
	//only add to our new query string if not rows or length as we want to set those to specific values in the jsp
	if (!key.equals("rows") && !key.equals("start")) {
		newQueryString += "&" + key + "=" + value;
		// If the same key has multiple values (check boxes)
		String[] valueArray = request.getParameterValues(key);

		for (int i = 0; i > valueArray.length; i++) {
			System.out.println("VALUE ARRAY" + valueArray[i]);
		}
	}
}
newQueryString+="&start="+startString+"&rows="+rowsString;

//System.out.println("queryString=" + newQueryString);
//JSONObject imageResults = JSONRestUtil.getResults(config
		//.get("internalSolrUrl") + "/images/select?" + newQueryString);
ImageDTOWrapper imageDTOWrapper = imageService.getImageDTOsForSolrQuery(newQueryString);
List<ImageDTO> imageDTOs=imageDTOWrapper.getImageDTOs();
for (ImageDTO imageDTO : imageDTOs) {
	System.out.println(imageDTO.getOmeroId());
}


if (imageDTOs != null) {
	model.addAttribute("images", imageDTOs);
	int numberFound = (int) imageDTOWrapper.getNumberFound();
	//System.out.println("image count=" + numberFound);
	model.addAttribute("imageCount", numberFound);
	model.addAttribute("q", newQueryString);
	// model.addAttribute("filterQueries", filterQueries);
	// model.addAttribute("filterField", filterField);
	// model.addAttribute("qf", qf);//e.g. auto_suggest
	// //model.addAttribute("filterParam", filterParam);
	// model.addAttribute("queryTerms", queryTerms);
	model.addAttribute("start", Integer.valueOf(startString));
	model.addAttribute("length", Integer.valueOf(rowsString));
	// model.addAttribute("defType", defType);
} else {
	model.addAttribute("solrImagesError", "");
}
}

}
