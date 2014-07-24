/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.phenotype.web.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import uk.ac.sanger.phenodigm2.dao.PhenoDigmWebDao;
import uk.ac.sanger.phenodigm2.model.DiseaseIdentifier;
import uk.ac.sanger.phenodigm2.model.DiseaseModelAssociation;
import uk.ac.sanger.phenodigm2.model.GeneIdentifier;
import uk.ac.sanger.phenodigm2.web.DiseaseGeneAssociationDetail;

/**
 * Controller for producing the disease-gene phenotype association details from 
 * Phenodigm.
 * 
 * @author Jules Jacobsen <jules.jacobsen@sanger.ac.uk>
 */
@Controller
public class PhenoDigmController {
  
    private static final Logger logger = LoggerFactory.getLogger(PhenoDigmController.class);

    @Autowired
    private PhenoDigmWebDao phenoDigmDao;
      
    //AJAX method
    @RequestMapping(value="/phenodigm/diseaseGeneAssociations", method=RequestMethod.GET)
    public String getPhenotypes(@RequestParam String requestPageType, @RequestParam String diseaseId, @RequestParam String geneId, Model model) {
        
        logger.info(String.format("AJAX call for %s %s from %s page", diseaseId, geneId, requestPageType));
        model.addAttribute("requestPageType", requestPageType);
        
        DiseaseGeneAssociationDetail details = phenoDigmDao.getDiseaseGeneAssociationDetail(new DiseaseIdentifier(diseaseId), new GeneIdentifier(geneId, geneId));
        model.addAttribute("diseaseGeneAssociationDetails", details);
        
        List<DiseaseModelAssociation> literatureAssociations = new ArrayList<>();
        List<DiseaseModelAssociation> phenotypicAssociations = new ArrayList<>();
        
        for (DiseaseModelAssociation diseaseAssociation : details.getDiseaseAssociations()) {
            if (diseaseAssociation.hasLiteratureEvidence()) {
                literatureAssociations.add(diseaseAssociation);
            }
            phenotypicAssociations.add(diseaseAssociation);
        }
        
        //The lists need sorting according to the view in which they will be appearing
        //we'll assume theat the default is going to be a disease page 
        Comparator pageComparator = DiseaseModelAssociation.DiseaseToGeneScoreComparator;
        //but it could be a gene page
        if (requestPageType.equals("gene")) {
            pageComparator = DiseaseModelAssociation.GeneToDiseaseScoreComparator;
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

        return "diseaseGeneAssociationDetails";
    }
}
