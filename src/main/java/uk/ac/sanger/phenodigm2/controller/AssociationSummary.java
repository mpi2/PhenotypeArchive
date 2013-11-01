/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.sanger.phenodigm2.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import uk.ac.sanger.phenodigm2.model.Disease;
import uk.ac.sanger.phenodigm2.model.DiseaseAssociation;
import uk.ac.sanger.phenodigm2.model.GeneIdentifier;

/**
 * Base class for Disease and GeneAssociationSummaries. 
 * This needs to be public otherwise there are issues with javax.el.ELException
 * being thrown.
 * @author Jules Jacobsen <jules.jacobsen@sanger.ac.uk>
 */
public class AssociationSummary {
    
    private boolean associatedInHuman;
    private boolean hasLiteratureEvidence;
    private double bestScore;
    private List<DiseaseAssociation> curatedAssociations;
    private List<DiseaseAssociation> phenotypicAssociations;

    protected AssociationSummary(GeneIdentifier mouseGeneId, Disease disease, Set<DiseaseAssociation> curatedAssociationsSet, Set<DiseaseAssociation> phenotypicAssociationsSet) {

        this.associatedInHuman = false;
        for (GeneIdentifier diseaseGeneId : disease.getAssociatedMouseGenes()) {
            if (diseaseGeneId.equals(mouseGeneId)) {
                associatedInHuman = true;
                break;
            }
        }
        hasLiteratureEvidence = false;
        this.curatedAssociations = new ArrayList<DiseaseAssociation>();
        if (curatedAssociationsSet != null) {
            this.curatedAssociations.addAll(curatedAssociationsSet);
            if (!curatedAssociations.isEmpty()) {
                hasLiteratureEvidence = true;
            }
        }
        
        this.phenotypicAssociations = new ArrayList<DiseaseAssociation>();
        if (phenotypicAssociationsSet != null) {
            this.phenotypicAssociations.addAll(phenotypicAssociationsSet);
        }
        //work out the best score
        if (! phenotypicAssociations.isEmpty()) {
            this.bestScore = phenotypicAssociations.get(0).getModelToDiseaseScore();        
        } else if (!curatedAssociations.isEmpty()) {
            this.bestScore = curatedAssociations.get(0).getModelToDiseaseScore();
        } else {
            this.bestScore = 0.0;
        }
        //Collections.sort(phenotypicAssociations, new DiseaseAssociation.DiseaseAssociationComparator()));
    }   
    
    
    public boolean isAssociatedInHuman() {
        return associatedInHuman;
    }

    public void setAssociatedInHuman(boolean associatedInHuman) {
        this.associatedInHuman = associatedInHuman;
    }

    public boolean isHasLiteratureEvidence() {
        return hasLiteratureEvidence;
    }

    public void setHasLiteratureEvidence(boolean hasLiteratureEvidence) {
        this.hasLiteratureEvidence = hasLiteratureEvidence;
    }

    public double getBestScore() {
        return bestScore;
    }

    public void setBestScore(double bestScore) {
        this.bestScore = bestScore;
    }

    public List<DiseaseAssociation> getCuratedAssociations() {
        return curatedAssociations;
    }

    public void setCuratedAssociations(List<DiseaseAssociation> curatedAssociations) {
        this.curatedAssociations = curatedAssociations;
    }

    public List<DiseaseAssociation> getPhenotypicAssociations() {
        return phenotypicAssociations;
    }

    public void setPhenotypicAssociations(List<DiseaseAssociation> phenotypicAssociations) {
        this.phenotypicAssociations = phenotypicAssociations;
    }
    
}
