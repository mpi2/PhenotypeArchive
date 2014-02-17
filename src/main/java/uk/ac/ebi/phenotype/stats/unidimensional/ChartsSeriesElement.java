package uk.ac.ebi.phenotype.stats.unidimensional;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import uk.ac.ebi.phenotype.pojo.SexType;
import uk.ac.ebi.phenotype.pojo.ZygosityType;

//to hold a highchart section of data with properties together so we can inject it into highcharts via Java JSON objects
//name: 'Observation',
//color: 'rgba(30, 151, 50,0.7)', 
//type: 'scatter',
//data: [
//[2, 3.26],
//[2, 2.7],
//[2, 2.66],
//[3, 2.35],
//[3, 2.73],
//[3, 2.55],
//[3, 2.92]
//]
public class ChartsSeriesElement {
	
	List<Float>originalData=new ArrayList<>();//to hold original data before being processsed to chart objects
	public List<Float> getOriginalData() {
		return originalData;
	}
	public void setOriginalData(List<Float> originalData) {
		this.originalData = originalData;
	}
	@Override
	public String toString() {
		return "ChartsSeriesElement [originalData=" + originalData
				+ ", column=" + column + ", sexType=" + sexType
				+ ", controlOrZygosity=" + zygosityType + ", name=" + name
				+ ", colorString=" + colorString + ", chartTypeString="
				+ chartTypeString + ", dataArray=" + boxPlotArray + "]\n";
	}
	int column;//to record the column of this data if say for boxplots we need this to determine how many other [] arrays to add before us
	SexType sexType;
	ZygosityType zygosityType;
	
	/**
	 * if zygosity type is null then it must be wildtype
	 * @return
	 */
	public ZygosityType getZygosityType() {
		return zygosityType;
	}
	public void setZygosityType(ZygosityType zygosityType) {
		this.zygosityType = zygosityType;
	}
	
	String getControlOrZygosityString() {
		if(zygosityType==null) {
			return "WT";
		}else {
			return zygosityType.getName();
		}
		
	}
	public int getColumn() {
		return column;
	}
	public void setColumn(int column) {
		this.column = column;
	}
	
	public SexType getSexType() {
		return sexType;
	}
	public void setSexType(SexType sexType) {
		this.sexType = sexType;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getColorString() {
		return colorString;
	}
	public void setColorString(String colorString) {
		this.colorString = colorString;
	}
	public String getChartTypeString() {
		return chartTypeString;
	}
	public void setChartTypeString(String chartTypeString) {
		this.chartTypeString = chartTypeString;
	}
	public JSONArray getBoxPlotArray() {
		return boxPlotArray;
	}
	public void setBoxPlotArray(JSONArray dataArray) {
		this.boxPlotArray = dataArray;
	}
	String name;
	String colorString;
	String chartTypeString;
	JSONArray boxPlotArray;

}
