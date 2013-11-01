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
 * Provides a direct-mapping between the data on the disease associations page and the model.
 * @author Jules Jacobsen <jules.jacobsen@sanger.ac.uk>
 */
public class DiseaseAssociationSummary extends AssociationSummary {
    private Disease disease;

    public DiseaseAssociationSummary(GeneIdentifier mouseGeneId, Disease disease, Set<DiseaseAssociation> curatedAssociationsSet, Set<DiseaseAssociation> phenotypicAssociationsSet) {
        super(mouseGeneId, disease, curatedAssociationsSet, phenotypicAssociationsSet);
        this.disease = disease;
    }

    public Disease getDisease() {
        return disease;
    }

    public void setDisease(Disease disease) {
        this.disease = disease;
    }

    @Override
    public String toString() {
        return String.format("DiseaseAssociationSummary{ %s Human Association: %s Mouse Literature Evidence: %s Best Phenotype Score: %s}", disease.getDiseaseIdentifier().getCompoundIdentifier(), super.isAssociatedInHuman(), super.isHasLiteratureEvidence(), super.getBestScore());
    }
 
    
}
