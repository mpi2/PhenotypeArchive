package uk.ac.ebi.phenotype.util;

public class PhenotypeGeneSummaryDTO {

	public String getMalePercentage() {
		return malePercentage;
	}
	public void setMalePercentage(float malePercentage) {
		this.malePercentage = String.format("%.2f", malePercentage);
	}
	public String getFemalePercentage() {
		return femalePercentage;
	}
	public void setFemalePercentage(float femalePercentage) {
		this.femalePercentage = String.format("%.2f", femalePercentage);
	}
	public String getTotalPercentage() {
		return totalPercentage;
	}
	public void setTotalPercentage(float totalPercentage) {
		this.totalPercentage = String.format("%.2f", totalPercentage);
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
	public String getPieChartCode() {
		return pieChartCode;
	}
	public void setPieChartCode(String pieChartCode) {
		this.pieChartCode = pieChartCode;
	}


	private String pieChartCode;
	
	private boolean display;
	
	private String malePercentage;
	private String femalePercentage;
	private String totalPercentage;
	
	private int maleGenesTested;
	private int femaleGenesTested;
	private int totalGenesTested;

	private int maleGenesAssociated;
	private int femaleGenesAssociated;
	private int totalGenesAssociated;
	
}
