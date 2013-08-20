package uk.ac.ebi.phenotype.stats.categorical;

import java.util.ArrayList;
import java.util.List;

import uk.ac.ebi.phenotype.pojo.BiologicalModel;
import uk.ac.ebi.phenotype.pojo.CategoricalResult;
import uk.ac.ebi.phenotype.pojo.SexType;

public class CategoricalChartDataObject {
	List<CategoricalSet> categoricalSets;
	private SexType sexType;
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

	public SexType getSexType() {
		return sexType;
	}

	public void setSexType(SexType sexType) {
		this.sexType = sexType;
	}

	public CategoricalChartDataObject() {
		this.categoricalSets = new ArrayList<CategoricalSet>();
	}

	public void add(CategoricalSet categoricalSet) {
		categoricalSets.add(categoricalSet);

	}
	
	public String toString(){
		String sex="";
		if(this.sexType!=null){
			sex=this.sexType.name();
		}
		String dataString="";
		for(CategoricalSet data: this.categoricalSets){
			dataString+=data.toString()+"\n";
		}
		String string="chart has sexType="+sex+"\n "+dataString;
		return string;
	}

	public List<CategoricalSet> getCategoricalSets() {
	return this.categoricalSets;
	}

	public void setChart(String javascript) {
		this.chart=javascript;
		
	}
}
