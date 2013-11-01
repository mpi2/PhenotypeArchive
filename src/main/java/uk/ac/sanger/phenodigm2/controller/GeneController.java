/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.sanger.phenodigm2.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.ac.sanger.phenodigm2.dao.PhenoDigmDao;
import uk.ac.sanger.phenodigm2.dao.PhenoDigmDaoJdbcImpl;
import uk.ac.sanger.phenodigm2.model.Disease;
import uk.ac.sanger.phenodigm2.model.DiseaseAssociation;
import uk.ac.sanger.phenodigm2.model.Gene;
import uk.ac.sanger.phenodigm2.model.GeneIdentifier;
import uk.ac.sanger.phenodigm2.model.MouseModel;

/**
 *
 * @author jj8
 */

@Controller
public class GeneController {

    private final Logger logger = LoggerFactory.getLogger(GeneController.class);
    
    @Autowired
    private PhenoDigmDao phenoDigmDao;
    
    @RequestMapping("/phenodigm/gene")
    public String allGenes(Model model) {
        logger.info("Making all genes page");
        //Get all genes from the phenoDigmDao where we have a known disease association
        Set<Gene> allGenes = phenoDigmDao.getAllGenes();       
        logger.info("Got all genes. Now adding to model");
        model.addAttribute("genes", allGenes);
        logger.info("Returning all genes page to container");
        return "phenodigm/genes";
    }
    
    
    @RequestMapping(value="/phenodigm/gene/{acc}", method=RequestMethod.GET)
    public String gene(@PathVariable("acc") String acc, Model model) {
        String mgiId = acc;
        logger.info("GeneController: Making gene page for " + mgiId);
        model.addAttribute("mgiId", mgiId);
        
        GeneIdentifier geneIdentifier = phenoDigmDao.getGeneIdentifierForMgiGeneId(mgiId);
        if (geneIdentifier == null) {
            return "geneNotFound";
        }
        
        logger.info("GeneController: Found GeneIdentifier: " + geneIdentifier);
        model.addAttribute("geneIdentifier", geneIdentifier);
        model.addAttribute("humanOrtholog", phenoDigmDao.getHumanOrthologIdentifierForMgiGeneId(mgiId));
        //Diseases 
        //known
        Map<Disease, Set<DiseaseAssociation>> knownDiseaseAssociations = phenoDigmDao.getKnownDiseaseAssociationsForMgiGeneId(mgiId);
        //INTERFACE TESTING ONLY!!! This should be an AJAX call.
        populateDiseasePhenotypeTerms(knownDiseaseAssociations);
        
        //predicted
        Map<Disease, Set<DiseaseAssociation>> predictedDiseaseAssociations = phenoDigmDao.getPredictedDiseaseAssociationsForMgiGeneId(mgiId);
        //INTERFACE TESTING ONLY!!! This should be an AJAX call.
        populateDiseasePhenotypeTerms(predictedDiseaseAssociations);
                
        List<DiseaseAssociationSummary> curatedDiseaseAssociationViews = new ArrayList<DiseaseAssociationSummary>();
        List<DiseaseAssociationSummary> predictedDiseaseAssociationViews = new ArrayList<DiseaseAssociationSummary>();

        for (Disease disease : knownDiseaseAssociations.keySet()) {
            Set<DiseaseAssociation> curatedAssociations = knownDiseaseAssociations.get(disease);
            Set<DiseaseAssociation> phenotypeAssociations = predictedDiseaseAssociations.get(disease);
            DiseaseAssociationSummary assocView = new DiseaseAssociationSummary(geneIdentifier, disease, curatedAssociations, phenotypeAssociations);
            curatedDiseaseAssociationViews.add(assocView);
        }
        model.addAttribute("curatedAssociations", curatedDiseaseAssociationViews);
        
        for (Disease disease : predictedDiseaseAssociations.keySet()) {
            Set<DiseaseAssociation> curatedAssociations = knownDiseaseAssociations.get(disease);
            Set<DiseaseAssociation> phenotypeAssociations = predictedDiseaseAssociations.get(disease);
            DiseaseAssociationSummary assocView = new DiseaseAssociationSummary(geneIdentifier, disease, curatedAssociations, phenotypeAssociations);
            predictedDiseaseAssociationViews.add(assocView);
        }
        model.addAttribute("phenotypeAssociations", predictedDiseaseAssociationViews);
        
        return "phenodigm/gene";
    }
     
    /**
     * Populates the PhenotypeTerms for all DiseaseAssociation in the given map  
     * @param diseaseAssociationsMap 
     */
    private void populateDiseasePhenotypeTerms(Map<Disease, Set<DiseaseAssociation>> diseaseAssociationsMap){
        
        for (Disease disease : diseaseAssociationsMap.keySet()) {
            if (disease.getPhenotypeTerms() == null) {
                disease.setPhenotypeTerms(phenoDigmDao.getDiseasePhenotypeTerms(disease.getDiseaseId()));                
            }
            Set<DiseaseAssociation> diseaseAssociations = diseaseAssociationsMap.get(disease);
            for (DiseaseAssociation diseaseAssociation : diseaseAssociations) {
                MouseModel mouseModel = diseaseAssociation.getMouseModel();
                if (mouseModel.getPhenotypeTerms().isEmpty()) {
                    mouseModel.setPhenotypeTerms(phenoDigmDao.getMouseModelPhenotypeTerms(mouseModel.getMgiModelId()));
                }
            }
        }
    }

}
