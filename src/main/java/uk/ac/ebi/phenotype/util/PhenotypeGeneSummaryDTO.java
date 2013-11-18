package uk.ac.ebi.phenotype.util;

public class PhenotypeGeneSummaryDTO {

	public float getMalePercentage() {
		return malePercentage;
	}
	public void setMalePercentage(float malePercentage) {
		this.malePercentage = malePercentage;
	}
	public float getFemalePercentage() {
		return femalePercentage;
	}
	public void setFemalePercentage(float femalePercentage) {
		this.femalePercentage = femalePercentage;
	}
	public float getTotalPercentage() {
		return totalPercentage;
	}
	public void setTotalPercentage(float totalPercentage) {
		this.totalPercentage = totalPercentage;
	}
	public int getMaleGenesTested() {
		return maleGenesTested;
	}
	public void setMaleGenesTested(int maleGenesTestes) {
		this.maleGenesTested = maleGenesTestes;
	}
	public int getFemaleGenesTested() {
		return femaleGenesTested;
	}
	public void setFemaleGenesTested(int femaleGenesTestes) {
		this.femaleGenesTested = femaleGenesTestes;
	}
	public int getTotalGenesTested() {
		return totalGenesTested;
	}
	public void setTotalGenesTested(int totalGenesTestes) {
		this.totalGenesTested = totalGenesTestes;
	}
	public int getMaleGenesAssociated() {
		return maleGenesAssociated;
	}
	public void setMaleGenesAssociated(int maleGenesAssociated) {
		this.maleGenesAssociated = maleGenesAssociated;
	}
	public int getFemaleGenesAssociated() {
		return femaleGenesAssociated;
	}
	public void setFemaleGenesAssociated(int femaleGenesAssociated) {
		this.femaleGenesAssociated = femaleGenesAssociated;
	}
	public int getTotalGenesAssociated() {
		return totalGenesAssociated;
	}
	public void setTotalGenesAssociated(int totalGenesAssociated) {
		this.totalGenesAssociated = totalGenesAssociated;
	}
	public boolean getDisplay() {
		return display;
	}
	public void setDisplay(boolean display) {
		this.display = display;
	}
	
	private boolean display;
	
	private float malePercentage;
	private float femalePercentage;
	private float totalPercentage;
	
	private int maleGenesTested;
	private int femaleGenesTested;
	private int totalGenesTested;

	private int maleGenesAssociated;
	private int femaleGenesAssociated;
	private int totalGenesAssociated;
	
}
