package uk.ac.ebi.phenotype.chart;

import java.util.ArrayList;
import java.util.List;

import uk.ac.ebi.phenotype.pojo.BiologicalModel;
import uk.ac.ebi.phenotype.pojo.CategoricalResult;
import uk.ac.ebi.phenotype.pojo.SexType;

public class CategoricalChartDataObject {
	List<CategoricalSet> categoricalSets;
	private String chart="";
	private String chartIdentifier="";
	private BiologicalModel biologicalModel=new BiologicalModel();//one bm per chart???
	
	public BiologicalModel getBiologicalModel() {
		return biologicalModel;
	}

	public void setBiologicalModel(BiologicalModel biologicalModel) {
		this.biologicalModel = biologicalModel;
	}

	public String getChartIdentifier() {
		return chartIdentifier;
	}

	public void setChartIdentifier(String chartIdentifier) {
		this.chartIdentifier = chartIdentifier;
	}

	public String getChart() {
		return chart;
	}

	public CategoricalChartDataObject() {
		this.categoricalSets = new ArrayList<CategoricalSet>();
	}

	public void add(CategoricalSet categoricalSet) {
		categoricalSets.add(categoricalSet);

	}
	
	public String toString(){
		String dataString="";
		for(CategoricalSet data: this.categoricalSets){
			dataString+=data.toString()+"\n";
		}
		 return dataString;
		
	}

	public List<CategoricalSet> getCategoricalSets() {
	return this.categoricalSets;
	}

	public void setChart(String javascript) {
		this.chart=javascript;
		
	}
	

}
