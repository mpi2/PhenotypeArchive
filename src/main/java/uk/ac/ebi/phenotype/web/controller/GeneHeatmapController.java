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

import uk.ac.ebi.phenotype.dao.SecondaryProject3iImpl;
import uk.ac.ebi.phenotype.dao.SecondaryProjectDAO;
import uk.ac.ebi.phenotype.pojo.Parameter;
import uk.ac.ebi.phenotype.service.StatisticalResultService;
import uk.ac.ebi.phenotype.web.pojo.BasicBean;
import uk.ac.ebi.phenotype.web.pojo.GeneRowForHeatMap;



@Controller
public class GeneHeatmapController {
    
    @Autowired
    @Qualifier("idg")  
	private SecondaryProjectDAO idgSecondaryProjectDAO; 
    
    @Autowired
    @Qualifier("threeI")  
	private SecondaryProject3iImpl threeISecondaryProjectDAO; 
    
    @Autowired
    StatisticalResultService srService;
        

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
	public String getHeatmapJS(@RequestParam(required = true, value = "project") String project,
                Model model,
                HttpServletRequest request,
                RedirectAttributes attributes) throws SolrServerException{
		
		System.out.println("calling heatmap controller method for " + project);
	
		if (project.equalsIgnoreCase("idg")){
			SecondaryProjectDAO secondaryProjectDAO = this.getSecondaryProjectDao(project);
			List<GeneRowForHeatMap> geneRows = secondaryProjectDAO.getGeneRowsForHeatMap(request);
		    List<BasicBean> xAxisBeans = secondaryProjectDAO.getXAxisForHeatMap();
		    model.addAttribute("geneRows", geneRows);
		    model.addAttribute("xAxisBeans", xAxisBeans);
		}
        return "geneHeatMap";
	}
	@RequestMapping("/threeIMap")
	public String getThreeIMap(Model model, HttpServletRequest request, RedirectAttributes attributes) 
	throws SolrServerException{
		
		System.out.println("calling heatmap controller method for 3i");
		String project="threeI";
		Long time = System.currentTimeMillis();
	    List<BasicBean> xAxisBeans = srService.getProceduresForDataSource("3i"); //procedures
		SecondaryProjectDAO secondaryProjectDAO = this.getSecondaryProjectDao(project);
		List<GeneRowForHeatMap> geneRows = secondaryProjectDAO.getGeneRowsForHeatMap(request);
	    model.addAttribute("geneRows", geneRows);
	    model.addAttribute("xAxisBeans", xAxisBeans);
		System.out.println("3i and xAxisBeans.size (procedures) = " + xAxisBeans.size());
		System.out.println("Getting the data took " + (System.currentTimeMillis() - time) + "ms");
	    return "threeIMap";
	}

	private SecondaryProjectDAO getSecondaryProjectDao(String project) {
		if(project.equalsIgnoreCase(SecondaryProjectDAO.SecondaryProjectIds.IDG.name())){
			return idgSecondaryProjectDAO;
		}
		if(project.equalsIgnoreCase(SecondaryProjectDAO.SecondaryProjectIds.threeI.name())){
			return threeISecondaryProjectDAO;
		}
		return null;
	}

}
