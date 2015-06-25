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
package uk.ac.ebi.phenotype.chart;

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
