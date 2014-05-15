package uk.ac.ebi.phenotype.web.controller;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.ac.ebi.phenotype.dao.SecondaryProjectDAO;

import uk.ac.ebi.phenotype.stats.graphs.HeatmapDTO;



@Controller
public class GeneHeatmapController {
    
        @Autowired
	private SecondaryProjectDAO secondaryProjectDAO;
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
            try {
                System.out.println("getGeneHeatMap called");
                List<String> accessions=secondaryProjectDAO.getAccessionsBySecondaryProjectId(0);
                //System.out.println("accession for sec project="+accessions);
                //get a list of genes for the project - which will be the row headers
                
                //get a list of procedure-parameters for the project which will be the column headers
                List<String> idgParameters=this.getIdgParameters();
                //mice produced and primary phenotype will be the first two coluns always?
                
                //model.addAttribute("heatmapCode", fillHeatmap(hdto));
               
            } catch (SQLException ex) {
                Logger.getLogger(GeneHeatmapController.class.getName()).log(Level.SEVERE, null, ex);
            }
             return "geneHeatMap";
	}

    private List<String> getIdgParameters() {
        List parameters=new ArrayList<>();
        
       
        return parameters;
    }

}
