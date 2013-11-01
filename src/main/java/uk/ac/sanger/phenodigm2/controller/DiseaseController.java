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
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.ac.sanger.phenodigm2.dao.PhenoDigmDao;
import uk.ac.sanger.phenodigm2.dao.PhenoDigmDaoJdbcImpl;
import uk.ac.sanger.phenodigm2.model.Disease;
import uk.ac.sanger.phenodigm2.model.DiseaseAssociation;
import uk.ac.sanger.phenodigm2.model.GeneIdentifier;
import uk.ac.sanger.phenodigm2.model.MouseModel;

/**
 *
 * @author jj8
 */
@Controller
public class DiseaseController {
    
    private static final Logger logger = Logger.getLogger(DiseaseController.class);
    
    @Autowired
    PhenoDigmDao diseaseDao;

    @RequestMapping(value = "phenodigm/disease")
    public String allDiseases(Model model) {
        logger.info("Making page for all diseases");

        Set<Disease> allDiseases = diseaseDao.getAllDiseses();

        model.addAttribute("allDiseases", allDiseases);
        
        logger.info(String.format("Found %d diseases", allDiseases.size()));

        return "phenodigm/diseases";
    }

    @RequestMapping(value = "phenodigm/disease/{diseaseId}")
    public String disease(@PathVariable("diseaseId") String diseaseId, Model model) {
        
        logger.info("Making page for disease: " + diseaseId);
        
        Disease disease = diseaseDao.getDiseaseByDiseaseId(diseaseId);
        model.addAttribute("disease", disease);
        logger.info(String.format("Found disease: %s %s", disease.getDiseaseId(), disease.getTerm()));
        
        disease.setPhenotypeTerms(diseaseDao.getDiseasePhenotypeTerms(diseaseId));
        
        //add known curated disease associations - 
        Map<GeneIdentifier, Set<DiseaseAssociation>> knownAssociations =  diseaseDao.getKnownDiseaseAssociationsForDiseaseId(diseaseId);
        populateGenePhenotypeTerms(knownAssociations);
        logInfo("orthologous", disease, knownAssociations);

       
        //also want to display the genes associated by phenotype similarity
        Map<GeneIdentifier, Set<DiseaseAssociation>> predictedAssociations = diseaseDao.getPredictedDiseaseAssociationsForDiseaseId(diseaseId);
        populateGenePhenotypeTerms(predictedAssociations);
        logInfo("predicted", disease, predictedAssociations);

        List<GeneAssociationSummary> curatedAssociationSummaries = new ArrayList<GeneAssociationSummary>();
        List<GeneAssociationSummary> phenotypeAssociationSummaries = new ArrayList<GeneAssociationSummary>();
        //a 'known' disease association could be by orthology or by manual curation from MGI or both (this is the known bit).
        //They could also have predicted phenotype associations too, hence the need to do this join here.  
        for (GeneIdentifier geneIdentifier : knownAssociations.keySet()) {
            Set<DiseaseAssociation> curatedAssociations = knownAssociations.get(geneIdentifier);
            Set<DiseaseAssociation> phenotypeAssociations = predictedAssociations.get(geneIdentifier);
            GeneAssociationSummary geneAssociationSummary = new GeneAssociationSummary(diseaseDao.getHumanOrthologIdentifierForMgiGeneId(geneIdentifier.getCompoundIdentifier()), geneIdentifier, disease, curatedAssociations, phenotypeAssociations);            
            curatedAssociationSummaries.add(geneAssociationSummary);
        }
        model.addAttribute("curatedAssociations", curatedAssociationSummaries);
        
        for (GeneIdentifier geneIdentifier : predictedAssociations.keySet()) {
            Set<DiseaseAssociation> curatedAssociations = knownAssociations.get(geneIdentifier);
            Set<DiseaseAssociation> phenotypeAssociations = predictedAssociations.get(geneIdentifier);
            GeneAssociationSummary geneAssociationSummary = new GeneAssociationSummary(diseaseDao.getHumanOrthologIdentifierForMgiGeneId(geneIdentifier.getCompoundIdentifier()), geneIdentifier, disease, curatedAssociations, phenotypeAssociations);            
            phenotypeAssociationSummaries.add(geneAssociationSummary);
        }
        
        model.addAttribute("phenotypeAssociations", phenotypeAssociationSummaries);
        
        
        return "phenodigm/disease";
    }

    /**
     * Populates the PhenotypeTerms for all DiseaseAssociation in the given map  
     * @param geneAssociationsMap 
     */
    private void populateGenePhenotypeTerms(Map<GeneIdentifier, Set<DiseaseAssociation>> geneAssociationsMap){
        
        for (GeneIdentifier geneId : geneAssociationsMap.keySet()) {           
            Set<DiseaseAssociation> diseaseAssociations = geneAssociationsMap.get(geneId);
            for (DiseaseAssociation diseaseAssociation : diseaseAssociations) {
                MouseModel mouseModel = diseaseAssociation.getMouseModel();
                if (mouseModel.getPhenotypeTerms().isEmpty()) {
                    mouseModel.setPhenotypeTerms(diseaseDao.getMouseModelPhenotypeTerms(mouseModel.getMgiModelId()));
                }
            }
        }
    }
    
    private void logInfo(String associationsType, Disease disease, Map<GeneIdentifier, Set<DiseaseAssociation>> geneAssociations) {
        Set<GeneIdentifier> genesWithMultipleDiseaseModels = new TreeSet<GeneIdentifier>();
        
        logger.info(String.format("%s has %d %s gene associations:", disease.getDiseaseId(), geneAssociations.keySet().size(), associationsType));
        int num = 1;
        for (GeneIdentifier geneId : geneAssociations.keySet()) {
            Set<DiseaseAssociation> diseaseAssociations = geneAssociations.get(geneId);
            if (diseaseAssociations.size() > 1) {
                genesWithMultipleDiseaseModels.add(geneId);
            }
            for (DiseaseAssociation diseaseAssociation : diseaseAssociations) {
                logger.info(String.format("%d %s: %s", num++, geneId, diseaseAssociation));
            }
        }
        if (!genesWithMultipleDiseaseModels.isEmpty()) {
            logger.info(String.format("Note the following genes have more than one mouse model which matches the %s disease phenotype: %s", disease.getDiseaseId(), genesWithMultipleDiseaseModels));
        }
    }

}
