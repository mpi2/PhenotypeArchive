package uk.ac.ebi.phenotype.stats;

import java.util.ArrayList;

/**
 * Type/structure that I needed to pass bigger objects around for the stacked bars / histograms.
 * @author tudose
 *
 */
public class StackedBarsData {
	ArrayList<String> mutantGenes;
	ArrayList<String> controlGenes;
	ArrayList<String> mutantGeneAccesionIds;
	ArrayList<String> controlGeneAccesionIds;
	ArrayList<Double> upperBounds;
	ArrayList<Double> controlMutatns;
	ArrayList<Double> phenMutants;
	
	
	public ArrayList<String> getMutantGeneAccesionIds() {
		return mutantGeneAccesionIds;
	}
	public void setMutantGeneAccesionIds(ArrayList<String> mutantGeneAccesionIds) {
		this.mutantGeneAccesionIds = mutantGeneAccesionIds;
	}
	public ArrayList<String> getControlGeneAccesionIds() {
		return controlGeneAccesionIds;
	}
	public void setControlGeneAccesionIds(ArrayList<String> controlGeneAccesionIds) {
		this.controlGeneAccesionIds = controlGeneAccesionIds;
	}
	public ArrayList<String> getMutantGenes() {
		return mutantGenes;
	}
	public void setMutantGenes(ArrayList<String> mutantGenes) {
		this.mutantGenes = mutantGenes;
	}
	public ArrayList<String> getControlGenes() {
		return controlGenes;
	}
	public void setControlGenes(ArrayList<String> controlGenes) {
		this.controlGenes = controlGenes;
	}
	public ArrayList<Double> getUpperBounds() {
		return upperBounds;
	}
	public void setUpperBounds(ArrayList<Double> upperBounds) {
		this.upperBounds = upperBounds;
	}
	public ArrayList<Double> getControlMutatns() {
		return controlMutatns;
	}
	public void setControlMutatns(ArrayList<Double> controlMutatns) {
		this.controlMutatns = controlMutatns;
	}
	public ArrayList<Double> getPhenMutants() {
		return phenMutants;
	}
	public void setPhenMutants(ArrayList<Double> phenMutants) {
		this.phenMutants = phenMutants;
	}
	
	
}
