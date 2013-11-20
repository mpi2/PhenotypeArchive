/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.sanger.phenodigm2.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import uk.ac.sanger.phenodigm2.dao.PhenoDigmWebDao;
import uk.ac.sanger.phenodigm2.model.DiseaseAssociation;
import uk.ac.sanger.phenodigm2.model.DiseaseIdentifier;
import uk.ac.sanger.phenodigm2.model.GeneIdentifier;
import uk.ac.sanger.phenodigm2.web.DiseaseGeneAssociationDetail;

/**
 *
 * @author jj8
 */
@Controller
public class PhenoDigmController {
    @Resource(name="globalConfiguration")
    private Map<String, String> config;
  
    private static final Logger logger = LoggerFactory.getLogger(PhenoDigmController.class);

    @Autowired
    private PhenoDigmWebDao phenoDigmDao;
    
    @RequestMapping(value = "")
    public String home(Model model) {

        return "home";
    }
    
    @RequestMapping(value = "/home")
    public String altHome(Model model) {

        return "home";
    }
    
    //AJAX method
    @RequestMapping(value="/diseaseGeneAssociations", method=RequestMethod.GET)
    public ModelAndView getPhenotypes(@RequestParam String requestPageType, @RequestParam String diseaseId, @RequestParam String geneId, Model model) {
        
//        logger.info("AJAX call for {} {} from {} page", diseaseId, geneId, requestPageType);
        model.addAttribute("requestPageType", requestPageType);
        
        DiseaseGeneAssociationDetail details = phenoDigmDao.getDiseaseGeneAssociationDetail(new DiseaseIdentifier(diseaseId), new GeneIdentifier(geneId, geneId));
        model.addAttribute("diseaseGeneAssociationDetails", details);
        
        List<DiseaseAssociation> literatureAssociations = new ArrayList<>();
        List<DiseaseAssociation> phenotypicAssociations = new ArrayList<>();
        
        for (DiseaseAssociation diseaseAssociation : details.getDiseaseAssociations()) {
            if (diseaseAssociation.hasLiteratureEvidence()) {
                literatureAssociations.add(diseaseAssociation);
            }
            phenotypicAssociations.add(diseaseAssociation);
        }
        
        //The lists need sorting according to the view in which they will be appearing
        //we'll assume theat the default is going to be a disease page 
        Comparator pageComparator = DiseaseAssociation.DiseaseToGeneScoreComparator;
        //but it could be a gene page
        if (requestPageType.equals("gene")) {
            pageComparator = DiseaseAssociation.GeneToDiseaseScoreComparator;
            logger.info("Sorting DiseaseAssociations according to m2d score for Gene page");
        }
        else {
            logger.info("Sorting DiseaseAssociations according to d2m score for Disease page");
        }
        //sort the lists according to the view in which they will be appearing
        Collections.sort(literatureAssociations, pageComparator);
        Collections.sort(phenotypicAssociations, pageComparator);
        
        model.addAttribute("literatureAssociations", literatureAssociations);
        model.addAttribute("phenotypicAssociations", phenotypicAssociations);

        return new ModelAndView("diseaseGeneAssociationDetails");
    }
}
