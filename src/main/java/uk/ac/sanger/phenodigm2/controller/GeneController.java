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
import uk.ac.sanger.phenodigm2.dao.PhenoDigmWebDao;
import uk.ac.sanger.phenodigm2.model.Gene;
import uk.ac.sanger.phenodigm2.model.GeneIdentifier;
import uk.ac.sanger.phenodigm2.web.AssociationSummary;
import uk.ac.sanger.phenodigm2.web.DiseaseAssociationSummary;

/**
 *
 * @author Jules Jacobsen <jules.jacobsen@sanger.ac.uk>
 */

@Controller
public class GeneController {

    private final Logger logger = LoggerFactory.getLogger(GeneController.class);
    
    @Autowired
    private PhenoDigmWebDao phenoDigmDao;
    private double rawScoreCutoff = 1.97;
    
    public double getRawScoreCutoff() {
        return rawScoreCutoff;
    }

    public void setRawScoreCutoff(double rawScoreCutoff) {
        this.rawScoreCutoff = rawScoreCutoff;
    }
    
    @RequestMapping("/phenodigm/gene")
    public String allGenes(Model model) {
        logger.info("Making all genes page");
        //Get all genes from the phenoDigmDao where we have a known disease association
        Set<Gene> allGenes = new TreeSet<>();//phenoDigmDao.getAllGenes();       
        logger.info("Got all genes. Now adding to model");
        model.addAttribute("genes", allGenes);
        logger.info("Returning all genes page to container");
        return "phenodigm/genes";
    }
    
    
    @RequestMapping(value="/phenodigm/gene/{acc}", method=RequestMethod.GET)
    public String gene(@PathVariable("acc") String acc, Model model) {
        String mgiId = acc;
        logger.info("Making gene page for " + mgiId);
        model.addAttribute("mgiId", mgiId);
        GeneIdentifier geneIdentifier = new GeneIdentifier(mgiId, mgiId);
        
        Gene gene = phenoDigmDao.getGene(geneIdentifier);
        logger.info("Found Gene: " + gene);
        if (gene != null) {
            model.addAttribute("geneIdentifier", gene.getOrthologGeneId());
            model.addAttribute("humanOrtholog", gene.getHumanGeneId());
            logger.info(String.format("Found gene: %s %s", gene.getOrthologGeneId().getCompoundIdentifier(), gene.getOrthologGeneId().getGeneSymbol()));
        } else {
            model.addAttribute("geneIdentifier", geneIdentifier);
            logger.info(String.format("No human ortholog found for gene: %s", geneIdentifier));                
        }
        
        logger.info(String.format("%s - getting disease-gene associations using cutoff %s", geneIdentifier, rawScoreCutoff));
        List<DiseaseAssociationSummary> diseaseAssociationSummarys = phenoDigmDao.getGeneToDiseaseAssociationSummaries(geneIdentifier, rawScoreCutoff);
        logger.info(String.format("%s - recieved %s disease-gene associations", geneIdentifier, diseaseAssociationSummarys.size()));

        List<DiseaseAssociationSummary> curatedAssociationSummaries = new ArrayList<DiseaseAssociationSummary>();
        List<DiseaseAssociationSummary> phenotypeAssociationSummaries = new ArrayList<DiseaseAssociationSummary>();
          
//        for (DiseaseAssociationSummary geneAssociationSummary : diseaseAssociationSummarys) {
//            AssociationSummary associationSummary = geneAssociationSummary.getAssociationSummary();
//            //always want the associations in the phenotypes list
//            phenotypeAssociationSummaries.add(geneAssociationSummary);
//            //but only the curated ones in the curated list...
//            if (associationSummary.isAssociatedInHuman() || associationSummary.isHasLiteratureEvidence()) {
//               curatedAssociationSummaries.add(geneAssociationSummary);
//            }
//        }
                   
        model.addAttribute("phenotypeAssociations", diseaseAssociationSummarys);
        
        logger.info("returning gene page for " + mgiId);
        return "phenodigm/gene";
    }
}
