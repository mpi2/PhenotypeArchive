package uk.ac.ebi.phenotype.util;

import java.util.List;

import org.json.JSONArray;

import uk.ac.ebi.phenotype.stats.graphs.ChartColors;

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
		if (pieChartCode != null){
			return pieChartCode;
		}else {
			return  getPiechart(getMaleOnlyNumber(), getFemaleOnlyNumber(), getBothNumber(), getTotalGenesTested());
		}
	}
	public void fillPieChartCode() {
		this.pieChartCode = getPiechart(getMaleOnlyNumber(), getFemaleOnlyNumber(), getBothNumber(), getTotalGenesTested());
	}
	public int getFemaleOnlyNumber() {
		return femaleOnlyNumber;
	}
	public void setFemaleOnlyNumber(int femaleOnlyNumber) {
		this.femaleOnlyNumber = femaleOnlyNumber;
	}
	public int getMaleOnlyNumber() {
		return maleOnlyNumber;
	}
	public void setMaleOnlyNumber(int maleOnlyNumber) {
		this.maleOnlyNumber = maleOnlyNumber;
	}
	public int getBothNumber() {
		return bothNumber;
	}
	public void setBothNumber(int bothNumber) {
		this.bothNumber = bothNumber;
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
	
	private int femaleOnlyNumber; // with phenotypeL
	private int maleOnlyNumber;
	private int bothNumber;
	

	protected String getPiechart(int maleOnly, int femaleOnly, int both, int total){
	
		List<String> colors = ChartColors.getHighDifferenceColorsRgba(ChartColors.alphaBox);
		JSONArray colorArray = new JSONArray(colors);	
		
		String chart = "$(function () { $('#pieChart').highcharts({ "
				 + " chart: { plotBackgroundColor: null, plotShadow: false}, "	
				 + " colors:"+colorArray+", "
				 + " title: {  text: '' }, "
				 + " credits: { enabled: false }, "
				 + " tooltip: {  pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'},"
				 + " plotOptions: { "
				 	+ "pie: { "
				 		+ "size: 200, "
				 		+ "allowPointSelect: true, "
				 		+ "cursor: 'pointer', "
				 		+ "dataLabels: { enabled: true, format: '<b>{point.name}</b>: {point.percentage:.1f} %', "
				 		+ "style: { color: '#666', width:'60px' }  }  },"
				 	+ "series: {  dataLabels: {  enabled: true, format: '{point.name}: {point.percentage:.1f}%'} }"
				 + " },"
			+ " series: [{  type: 'pie',   name: '',  "
				+ "data: [ { name: 'Female only', y: " + femaleOnly + ", sliced: true, selected: true }, "
					+ "{ name: 'Male only', y: " + maleOnly + ", sliced: true, selected: true }, "
					+ "{ name: 'Both sexes', y: " + both + ", sliced: true, selected: true }, "
					+ "['Phenotype not present', " + (total- maleOnly - femaleOnly - both) + " ] ]  }]"
		+" }); });";
		
		return chart;
	}
	
	
}
