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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.ac.sanger.phenodigm2.dao.PhenoDigmWebDao;
import uk.ac.sanger.phenodigm2.model.Disease;
import uk.ac.sanger.phenodigm2.model.DiseaseIdentifier;
import uk.ac.sanger.phenodigm2.web.AssociationSummary;
import uk.ac.sanger.phenodigm2.web.GeneAssociationSummary;

/**
 *
 * @author jj8
 */
@Controller
public class DiseaseController {

    private static final Logger logger = Logger.getLogger(DiseaseController.class);

    @Autowired
    private PhenoDigmWebDao phenoDigmDao;

    @RequestMapping(value = "phenodigm/disease")
    public String allDiseases(Model model) {
        logger.info("Making page for all diseases");

        Set<Disease> allDiseases = new TreeSet<Disease>();//phenoDigmDao.getAllDiseses();

        model.addAttribute("allDiseases", allDiseases);

        logger.info(String.format("Found %d diseases", allDiseases.size()));

        return "phenodigm/diseases";
    }

    @RequestMapping(value = "phenodigm/disease/{diseaseId}")
    public String disease(@PathVariable("diseaseId") String diseaseId, Model model) {

        logger.info("Making page for disease: " + diseaseId);

        Map<Disease, List<GeneAssociationSummary>> diseaseToGeneAssociationsMap = phenoDigmDao.getDiseaseToGeneAssociationSummaries(new DiseaseIdentifier(diseaseId));

        List<GeneAssociationSummary> curatedAssociationSummaries = new ArrayList<GeneAssociationSummary>();
        List<GeneAssociationSummary> phenotypeAssociationSummaries = new ArrayList<GeneAssociationSummary>();

        for (Disease disease : diseaseToGeneAssociationsMap.keySet()) {
            model.addAttribute("disease", disease);
            logger.info(String.format("Found disease: %s %s", disease.getDiseaseId(), disease.getTerm()));
            List<GeneAssociationSummary> geneAssociationSummarys = diseaseToGeneAssociationsMap.get(disease);
            if (geneAssociationSummarys != null) {
                for (GeneAssociationSummary geneAssociationSummary : geneAssociationSummarys) {
                    AssociationSummary associationSummary = geneAssociationSummary.getAssociationSummary();
                    //always want the associations in the phenotypes list
                    if (associationSummary.getBestImpcScore() > 0.0 || associationSummary.getBestMgiScore() > 0.0) {
                        phenotypeAssociationSummaries.add(geneAssociationSummary);
                    }
                    //but only the curated ones in the curated list...
                    if (associationSummary.isAssociatedInHuman() || associationSummary.isHasLiteratureEvidence()) {
                        curatedAssociationSummaries.add(geneAssociationSummary);
                    }
                }
            }
        }

        model.addAttribute("curatedAssociations", curatedAssociationSummaries);
        model.addAttribute("phenotypeAssociations", phenotypeAssociationSummaries);

        return "phenodigm/disease";
    }
}
