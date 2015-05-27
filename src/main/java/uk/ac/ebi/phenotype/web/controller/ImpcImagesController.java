package uk.ac.ebi.phenotype.web.controller;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.ac.ebi.phenotype.pojo.SexType;
import uk.ac.ebi.phenotype.service.ExpressionService;
import uk.ac.ebi.phenotype.service.GeneService;
import uk.ac.ebi.phenotype.service.ImageService;
import uk.ac.ebi.phenotype.service.dto.GeneDTO;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

//import Glacier2.CannotCreateSessionException;
//import Glacier2.PermissionDeniedException;

@Controller
public class ImpcImagesController {

	@Autowired
	ImageService imageService;
	
	@Autowired
	ExpressionService expressionService;
	
	@Autowired
	GeneService geneService;

	@RequestMapping("/impcImages/laczimages/{acc}/{topLevelMa}")
	public String laczImages(@PathVariable String acc, @PathVariable String topLevelMa, Model model)
			throws SolrServerException, IOException, URISyntaxException {

		// http://ves-ebi-d0.ebi.ac.uk:8090/mi/impc/dev/solr/impc_images/select?q=gene_accession_id:%22MGI:2387599%22&facet=true&facet.field=selected_top_level_ma_term&fq=parameter_name:%22LacZ%20Images%20Section%22&group=true&group.field=selected_top_level_ma_term

		System.out.println("calling laczImages web page");
		addGeneSymbolToPage(acc, model);
		boolean overview=false;
		expressionService.getLacDataForGene(acc, topLevelMa,overview, false, model);

		return "laczImages";
	}
	
	@RequestMapping("/impcImages/laczimages/{acc}")
	public String laczImages(@PathVariable String acc, Model model)
			throws SolrServerException, IOException, URISyntaxException {
		addGeneSymbolToPage(acc, model);
		boolean overview=false;
		expressionService.getLacDataForGene(acc, null, overview, false, model);

		return "laczImages";
	}

	private void addGeneSymbolToPage(String acc, Model model)
			throws SolrServerException {
		GeneDTO gene = geneService.getGeneById(acc);
		model.addAttribute("symbol", gene.getMarkerSymbol());
	}

	
	@RequestMapping("/imagePicker/{acc}/{parameter_stable_id}")
	public String imagePicker(@PathVariable String acc,
			@PathVariable String parameter_stable_id, Model model)
			throws SolrServerException {

		// good example url with control and experimental images
		// http://localhost:8080/phenotype-archive/imagePicker/MGI:2669829/IMPC_EYE_050_001
		System.out.println("calling image picker");

		// get experimental images
		// we will also want to call the getControls method and display side by
		// side
		SolrDocumentList experimental = new SolrDocumentList();
		QueryResponse responseExperimental2 = imageService
				.getImagesForGeneByParameter(acc, parameter_stable_id,
						"experimental", 10000, null, null, null);
		if (responseExperimental2 != null) {
			experimental.addAll(responseExperimental2.getResults());
		}
		System.out.println("list size=" + experimental.size());
		SolrDocumentList controls = new SolrDocumentList();
		// QueryResponse responseControl =
		// imageService.getImagesForGeneByParameter(acc, parameter_stable_id,
		// "control", 6, null, null, null);
		SolrDocument imgDoc = responseExperimental2.getResults().get(0);
		int numberOfControlsPerSex = 5;
		// int daysEitherSide = 30;// get a month either side
		for (SexType sex : SexType.values()) {
			SolrDocumentList list = imageService.getControls(numberOfControlsPerSex, sex, imgDoc);
			controls.addAll(list);
		}

		System.out.println("experimental size=" + experimental.size());
		model.addAttribute("experimental", experimental);
		System.out.println("controls size=" + controls.size());
		model.addAttribute("controls", controls);
		return "imagePicker";
	}
	
	@RequestMapping("/expressionImagePicker/{acc}/{anatomy}")
	public String expressionImagePicker(@PathVariable String acc,
			@PathVariable String anatomy, Model model)
			throws SolrServerException {

		// good example url with control and experimental images
		// http://localhost:8080/phenotype-archive/imagePicker/MGI:2669829/IMPC_EYE_050_001
		System.out.println("calling image picker");

		// get experimental images
		// we will also want to call the getControls method and display side by
		// side
		SolrDocumentList experimental = new SolrDocumentList();
		QueryResponse responseExperimental2 = expressionService
				.getExpressionImagesForGeneByAnatomy(acc, anatomy,
						"experimental", 10000, null, null, null);
		if (responseExperimental2 != null) {
			experimental.addAll(responseExperimental2.getResults());
		}
		System.out.println("list size=" + experimental.size());
		SolrDocumentList controls = new SolrDocumentList();
		// QueryResponse responseControl =
		// imageService.getImagesForGeneByParameter(acc, parameter_stable_id,
		// "control", 6, null, null, null);
		SolrDocument imgDoc = responseExperimental2.getResults().get(0);
		int numberOfControlsPerSex = 5;
		// int daysEitherSide = 30;// get a month either side
		for (SexType sex : SexType.values()) {
			SolrDocumentList list = null;
			list = imageService.getControls(numberOfControlsPerSex, sex, imgDoc);
			controls.addAll(list);
		}

		System.out.println("experimental size=" + experimental.size());
		model.addAttribute("experimental", experimental);
		System.out.println("controls size=" + controls.size());
		model.addAttribute("controls", controls);
		return "imagePicker";
	}

	@RequestMapping("/imageComparator")
	public String imageComparator(HttpServletRequest request, Model model) {

		String page = "imageComparator";
		System.out.println("calling imageComparator");
		// String[] omeroIds = request.getParameterValues("imgId");
		// if(omeroIds==null || omeroIds.length==0){
		// System.out.println("error no items selected");
		// model.addAttribute("error", "You need to select at least one image");
		// return page;
		// }
		//
		// for (String value : omeroIds) {
		// System.out.println("omeroId=" + value);
		// }
		// model.addAttribute("omeroIds", omeroIds);

		return page;
	}

	@RequestMapping("/imageNavigator")
	public String imageControlNavigator(HttpServletRequest request, Model model) {

		String page = "imageNavigator";
		System.out.println("calling imageNavigator");
		return page;
	}

	@RequestMapping("/impcImages/ContAndExp*")
	public String imagesControlAndExperimental(HttpServletRequest request,
			Model model) throws SolrServerException, IOException,
			URISyntaxException {

		System.out.println("calling imagesContAndExp");
		String solrQueryString = request.getQueryString();
		System.out.println("solrQueryString=" + solrQueryString);
		String acc = request.getParameter("gene_accession_id");
		String procedureName = request.getParameter("procedure_name");
		String parameterStableId = request.getParameter("parameter_stable_id");
		List<Count> filteredCounts = new ArrayList<Count>();
		Map<String, SolrDocumentList> facetToDocs = new HashMap<String, SolrDocumentList>();
		imageService.getControlAndExperimentalImpcImages(acc, model,
				procedureName, parameterStableId, 5, 100, null, filteredCounts,
				facetToDocs);
		model.addAttribute("impcImageFacets", filteredCounts);
		model.addAttribute("impcFacetToDocs", facetToDocs);
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
		String titleString = "";
		String queryString = request.getQueryString();
		System.out.println("QUERY: " + queryString);
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
		String qStr = null;
		String fqStr = null;
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
				if (value.contains("MGI:")) {
					value = value.replace("MGI:", "MGI\\:");// for mgi ids for
															// example encode
															// the :
				}
				newQueryString += "&" + key + "=" + value;
				// If the same key has multiple values (check boxes)
				String[] valueArray = request.getParameterValues(key);

				for (int i = 0; i > valueArray.length; i++) {
					System.out.println("VALUE ARRAY" + valueArray[i]);
				}
			}
			if (key.equals("q")) {

				qStr = value;
				// get rid of wierd solr comments etc so more human readable
				titleString = qStr;
				titleString = titleString.replace(
						"observation_type:image_record AND", " ");

				// also check what is in fq
				if (request.getParameterValues("fq") != null) {

					String[] fqStrings = request.getParameterValues("fq");
					fqStr = fqStrings[0];
					if (titleString.equals("*:*") && fqStr.equals("*:*")) {
						titleString = "IMPC image dataset";
					} else if (titleString.equals("*:*")
							&& !fqStr.equals("*:*")) {
						titleString = fqStr;
					} else {
						titleString += " AND " + fqStr;
					}

					titleString = titleString.replace("\"", " ");
					titleString = titleString.replace("(", " ");
					titleString = titleString.replace(")", " ");
					titleString = titleString.replace("_", " ");
				}
			}
		}
		String qBaseStr = newQueryString;
		newQueryString += "&start=" + startString + "&rows=" + rowsString;
		QueryResponse imageResponse = imageService
				.getResponseForSolrQuery(newQueryString);
		if (imageResponse.getResults() != null) {
			model.addAttribute("images", imageResponse.getResults());
			Long totalNumberFound = imageResponse.getResults().getNumFound();
			// System.out.println("image count=" + numberFound);
			model.addAttribute("imageCount", totalNumberFound);
			// model.addAttribute("q", newQueryString);
			model.addAttribute("q", qStr);
			model.addAttribute("qBaseStr", qBaseStr);
			if (request.getParameter("title") != null) {// if title is provided
														// as a parameter use
														// that for the title
				titleString = request.getParameter("title");
			}
			model.addAttribute("titleString", titleString);

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
