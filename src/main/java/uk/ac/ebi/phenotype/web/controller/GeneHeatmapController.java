package uk.ac.ebi.phenotype.web.controller;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.ac.ebi.phenotype.dao.PhenotypePipelineDAO;
import uk.ac.ebi.phenotype.dao.SecondaryProjectDAO;
import uk.ac.ebi.phenotype.pojo.Parameter;
import uk.ac.ebi.phenotype.pojo.Pipeline;
import uk.ac.ebi.phenotype.pojo.PipelineSolrImpl;
import uk.ac.ebi.phenotype.pojo.Procedure;
import uk.ac.ebi.phenotype.service.GenotypePhenotypeService;

import uk.ac.ebi.phenotype.stats.graphs.HeatmapDTO;
import uk.ac.ebi.phenotype.web.pojo.GeneRowForHeatMap;



@Controller
public class GeneHeatmapController {
    
        @Autowired
	private SecondaryProjectDAO secondaryProjectDAO;
        
        @Autowired
	private PhenotypePipelineDAO pDAO;
        
         @Autowired
	private GenotypePhenotypeService genotypePhenotypeService;
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
             List<GeneRowForHeatMap>geneRows=new ArrayList<>();
             List<String> parameters=this.getIdgParameters();
            try {
                System.out.println("getGeneHeatMap called");
               
                //System.out.println("accession for sec project="+accessions);
                //get a list of genes for the project - which will be the row headers
                List<String> accessions=secondaryProjectDAO.getAccessionsBySecondaryProjectId(0);
                //get a list of procedure-parameters for the project which will be the column headers
                
                //mice produced and primary phenotype will be the first two coluns always?
                for(String accession: accessions){
                    
                    //get a data structure with the gene accession,with parameter associated with a Value or status ie. not phenotyped, not significant
                    GeneRowForHeatMap row = genotypePhenotypeService.getResultsForGeneHeatMap(accession,parameters );
                    geneRows.add(row);
                }
                //model.addAttribute("heatmapCode", fillHeatmap(hdto));
               
            } catch (SQLException ex) {
                Logger.getLogger(GeneHeatmapController.class.getName()).log(Level.SEVERE, null, ex);
            }
            model.addAttribute("geneRows", geneRows);
            model.addAttribute("parameters", parameters);
             return "geneHeatMap";
	}

    private List<String> getIdgParameters() {
        //for now lets just a get a list of parameters but eventuallly we'll need a specific list and store it in the db or flat file for pick up by jenkins?
        List<String> parameters=new ArrayList<>();
            List<Pipeline> pipelines = pDAO.getAllPhenotypePipelines();
            for(Pipeline pipeline:pipelines){
                System.out.println("pipeline="+pipeline.getDescription());
                Set<Procedure> procedures = pipeline.getProcedures();
                //this is example so just get first param for each procedure
               int i=0;
                for(Procedure procedure: procedures){
                    System.out.println("procedure="+procedure.getName());
                    int j=0;
                    for(Parameter param: procedure.getParameters()){
                        System.out.println("param name="+param.getName());
                        parameters.add(param.getStableId());
                        j++;
                        if(j>2)break;
                    }
                    i++;
                    if(i>1)break;
                }
            }
       
        return parameters;
    }

}
