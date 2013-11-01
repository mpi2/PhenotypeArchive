/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.sanger.phenodigm2.controller;

import java.util.Set;
import uk.ac.sanger.phenodigm2.model.Disease;
import uk.ac.sanger.phenodigm2.model.DiseaseAssociation;
import uk.ac.sanger.phenodigm2.model.GeneIdentifier;

/**
 *
 * @author Jules Jacobsen <jules.jacobsen@sanger.ac.uk>
 */
public class GeneAssociationSummary extends AssociationSummary {
    
    private GeneIdentifier humanGeneIdentifier;
    private GeneIdentifier mouseGeneIdentifier;

    public GeneAssociationSummary(GeneIdentifier humanGeneIdentifier, GeneIdentifier mouseGeneIdentifier, Disease disease, Set<DiseaseAssociation> curatedAssociationsSet, Set<DiseaseAssociation> phenotypicAssociationsSet) {
        super(mouseGeneIdentifier, disease, curatedAssociationsSet, phenotypicAssociationsSet);
        this.humanGeneIdentifier = humanGeneIdentifier;
        this.mouseGeneIdentifier = mouseGeneIdentifier;
    }

    public GeneIdentifier getHumanGeneIdentifier() {
        return humanGeneIdentifier;
    }

    public void setHumanGeneIdentifier(GeneIdentifier humanGeneIdentifier) {
        this.humanGeneIdentifier = humanGeneIdentifier;
    }

    public GeneIdentifier getMouseGeneIdentifier() {
        return mouseGeneIdentifier;
    }

    public void setMouseGeneIdentifier(GeneIdentifier mouseGeneIdentifier) {
        this.mouseGeneIdentifier = mouseGeneIdentifier;
    }

    @Override
    public String toString() {
        return String.format("GeneAssociationSummary{ %s %s Human Association: %s Mouse Literature Evidence: %s Best Phenotype Score: %s}", humanGeneIdentifier, mouseGeneIdentifier, super.isAssociatedInHuman(), super.isHasLiteratureEvidence(), super.getBestScore());
    }
    
}
