/*******************************************************************************
 * Copyright 2015 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 *******************************************************************************/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.phenotype.web.controller;

import java.util.ArrayList;
import java.util.List;
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
 * @author Jules Jacobsen <jules.jacobsen@sanger.ac.uk>
 */
@Controller
public class DiseaseController {

    private static final Logger logger = Logger.getLogger(DiseaseController.class);

    @Autowired
    private PhenoDigmWebDao phenoDigmDao;
    private double rawScoreCutoff = 1.97;

    public double getRawScoreCutoff() {
        return rawScoreCutoff;
    }

    public void setRawScoreCutoff(double rawScoreCutoff) {
        this.rawScoreCutoff = rawScoreCutoff;
    }

    @RequestMapping(value = "/disease")
    public String allDiseases(Model model) {
        logger.info("Making page for all diseases");

        Set<Disease> allDiseases = new TreeSet<Disease>();//phenoDigmDao.getAllDiseses();

        model.addAttribute("allDiseases", allDiseases);

        logger.info(String.format("Found %d diseases", allDiseases.size()));

        return "diseases";
    }

    @RequestMapping(value = "/disease/{diseaseId}")
    public String disease(@PathVariable("diseaseId") String diseaseId, Model model) {

        logger.info("Making disease page for " + diseaseId);

        DiseaseIdentifier diseaseIdentifier = new DiseaseIdentifier(diseaseId);
        Disease disease = phenoDigmDao.getDisease(diseaseIdentifier);
        logger.info(String.format("Found disease: %s %s", disease.getDiseaseId(), disease.getTerm()));
        model.addAttribute("disease", disease);

        logger.info(String.format("%s - getting gene-disease associations using cutoff %s", diseaseId, rawScoreCutoff));
        List<GeneAssociationSummary> geneAssociationSummarys = phenoDigmDao.getDiseaseToGeneAssociationSummaries(diseaseIdentifier, rawScoreCutoff);
        logger.info(String.format("%s - recieved %s gene-disease associations", diseaseId, geneAssociationSummarys.size()));

        //add associated genes for use in the top panel
        List<GeneAssociationSummary> orthologousGeneAssociations = new ArrayList<>();
        //add associated genes for use in the bottom panel
        List<GeneAssociationSummary> phenotypicGeneAssociations = new ArrayList<>();
        //add the known association summaries to a dedicated list for the top panel
        for (GeneAssociationSummary geneAssociationSummary : geneAssociationSummarys) {
            AssociationSummary associationSummary = geneAssociationSummary.getAssociationSummary();
            if (associationSummary.isAssociatedInHuman()) {
                orthologousGeneAssociations.add(geneAssociationSummary);
            } else {
                phenotypicGeneAssociations.add(geneAssociationSummary);
            }
        }
        model.addAttribute("orthologousGeneAssociations", orthologousGeneAssociations);

        model.addAttribute("phenotypicGeneAssociations", phenotypicGeneAssociations);
        logger.info("Returning disease page for " + diseaseId);
        return "disease";
    }
}
