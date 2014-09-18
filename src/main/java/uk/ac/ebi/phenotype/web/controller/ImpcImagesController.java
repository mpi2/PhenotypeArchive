package uk.ac.ebi.phenotype.web.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import uk.ac.ebi.phenotype.service.ImageService;
import uk.ac.ebi.phenotype.service.dto.ImageDTO;
import uk.ac.ebi.phenotype.service.dto.ResponseWrapper;

//import Glacier2.CannotCreateSessionException;
//import Glacier2.PermissionDeniedException;

@Controller
public class ImpcImagesController {

	@Autowired
	ImageService imageService;


	@RequestMapping("/impcImages/ContAndExp*")
	public String imagesControlAndExperimental(
	HttpServletRequest request, Model model)
	throws SolrServerException, IOException, URISyntaxException {	
		System.out.println("calling imagesContAndExp");
		String solrQueryString = request.getQueryString();
		System.out.println("solrQueryString="+solrQueryString);
		String acc=request.getParameter("acc");
		String procedureName=request.getParameter("procedure_name");
		String parameterStableId=request.getParameter("parameter_stable_id");
		imageService.getControlAndExperimentalImpcImages(acc, model,procedureName, parameterStableId, 5, 100, true);
		return "impcImagesContAndExp";
	}
	
	@RequestMapping("/impcImages/images*")
	public String allImages(HttpServletRequest request, Model model)
	throws SolrServerException, IOException, URISyntaxException {

		// http://localhost:8080/phenotype-archive/impcImages?q=observation_type:image_record&rows=100
		String solrQueryString = request.getQueryString();
		System.out.println("impcImages query=" + solrQueryString);
		System.out.println("calling impcImages web page");

		this.sendQueryStringToSolr(request, model);
		return "impcImages";
	}
	
	


	private void sendQueryStringToSolr(HttpServletRequest request, Model model)
	throws IOException, URISyntaxException, SolrServerException {

		String queryString = request.getQueryString();
		String startString = "0";
		String rowsString = "25";// the number of images passed back for each
									// solr request
		if (request.getParameter("start") != null) {
			startString = request.getParameter("start");
		}
		// if (request.getParameter("rows") != null) {
		// rowsString = request.getParameter("rows");
		// }

		// Map params = request.getParameterMap();
		// Map newParamsMap=new HashMap<>();
		// newParamsMap.putAll(params);
		// newParamsMap.remove("rows");
		// newParamsMap.remove("start");
		String newQueryString = "";
		Enumeration keys = request.getParameterNames();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			// System.out.println("key=" + key);

			// To retrieve a single value
			String value = request.getParameter(key);
			// System.out.println("value=" + value);
			// only add to our new query string if not rows or length as we want
			// to set those to specific values in the jsp
			if (!key.equals("rows") && !key.equals("start")) {
				if(value.contains("MGI:")){
					value=value.replace("MGI:", "MGI\\:");//for mgi ids for example encode the :
				}
				newQueryString += "&" + key + "=" + value;
				// If the same key has multiple values (check boxes)
				String[] valueArray = request.getParameterValues(key);

				for (int i = 0; i > valueArray.length; i++) {
					System.out.println("VALUE ARRAY" + valueArray[i]);
				}
			}
		}
		newQueryString += "&start=" + startString + "&rows=" + rowsString;
		System.out.println("newQueryString="+newQueryString);
		QueryResponse imageResponse = imageService.getResponseForSolrQuery(newQueryString);
		

		if (imageResponse.getResults() != null) {
			model.addAttribute("images", imageResponse.getResults());
			Long totalNumberFound = imageResponse.getResults().getNumFound();
			// System.out.println("image count=" + numberFound);
			model.addAttribute("imageCount", totalNumberFound);
			model.addAttribute("q", newQueryString);
			// model.addAttribute("filterQueries", filterQueries);
			// model.addAttribute("filterField", filterField);
			// model.addAttribute("qf", qf);//e.g. auto_suggest
			// //model.addAttribute("filterParam", filterParam);
			// model.addAttribute("queryTerms", queryTerms);
			model.addAttribute("start", Integer.valueOf(startString));
			model.addAttribute("length", Integer.valueOf(rowsString));
			// model.addAttribute("defType", defType);
		}
	}

}
