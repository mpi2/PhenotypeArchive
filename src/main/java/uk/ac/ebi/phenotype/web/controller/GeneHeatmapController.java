package uk.ac.ebi.phenotype.web.controller;

import java.util.ArrayList;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import uk.ac.ebi.phenotype.stats.graphs.HeatmapDTO;



@Controller
public class GeneHeatmapController {
	/**
         * 
         * @param project is the external project for example a secondary screen e.g. IDG or 3I
         * @param model
         * @param request
         * @param attributes
         * @return 
         */
	@RequestMapping("/geneHeatMap")
	public String getHeatmapJS(@RequestParam(required = false, value = "project") String[] project,
                Model model,
			HttpServletRequest request,
			RedirectAttributes attributes){
		System.out.println("getGeneHeatMap called");
		//get a list of genes for the project - which will be the row headers
                
                //get a list of procedure-parameters for the project which will be the column headers
                
                //mice produced and primary phenotype will be the first two coluns always?
		
		//model.addAttribute("heatmapCode", fillHeatmap(hdto));
		return "geneHeatMap";
	}

}
