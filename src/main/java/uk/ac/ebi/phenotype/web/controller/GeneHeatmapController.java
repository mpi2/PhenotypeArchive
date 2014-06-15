package uk.ac.ebi.phenotype.web.controller;


import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import uk.ac.ebi.phenotype.dao.SecondaryProjectDAO;
import uk.ac.ebi.phenotype.pojo.Parameter;
import uk.ac.ebi.phenotype.web.pojo.BasicBean;
import uk.ac.ebi.phenotype.web.pojo.GeneRowForHeatMap;



@Controller
public class GeneHeatmapController {
    
    @Autowired
    @Qualifier("idg")  
	private SecondaryProjectDAO idgSecondaryProjectDAO; 
    
//    @Autowired
//    @Qualifier("3I")  
//	private SecondaryProjectDAO threeISecondaryProjectDAO;  
    

	/**
         * 
         * @param project is the external project for example a secondary screen e.g. IDG or 3I
         * @param model
         * @param request
         * @param attributes
         * @return 
	 * @throws SolrServerException 
         */
	@RequestMapping("/geneHeatMap")
	public String getHeatmapJS(@RequestParam(required = false, value = "project") String project,
                Model model,
			HttpServletRequest request,
			RedirectAttributes attributes) throws SolrServerException{
		System.out.println("calling heatmap controller method");
		SecondaryProjectDAO secondaryProjectDAO=this.getSecondaryProjectDao(project);
             List<GeneRowForHeatMap> geneRows = secondaryProjectDAO.getGeneRowsForHeatMap();
             List<BasicBean> xAxisBeans = secondaryProjectDAO.getXAxisForHeatMap();
            model.addAttribute("geneRows", geneRows);
            model.addAttribute("xAxisBeans", xAxisBeans);
             return "geneHeatMap";
	}


	private SecondaryProjectDAO getSecondaryProjectDao(String project) {
		if(project.equalsIgnoreCase("idg")){
			return idgSecondaryProjectDAO;
		}
//		if(project.equalsIgnoreCase("3I")){
//			return threeISecondaryProjectDAO;
//		}
		return null;
	}

}
