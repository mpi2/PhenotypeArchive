package uk.ac.ebi.phenotype.bean;

import java.util.ArrayList;
import org.apache.commons.lang.StringUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


public class ParallelCoordinatesBean {

	String geneSymbol;
	String geneAccession;
	ArrayList<MeanBean> means;
	
	public ParallelCoordinatesBean(String geneSymbol, String geneAccession){
		this.geneAccession = geneAccession;
		this.geneSymbol = geneSymbol;
		means = new ArrayList<>();
	}
	
	public void addMean( String unit, String parameterStableId,
	String parameterName, String parameterStableKey, Double mean){
		means.add(new MeanBean( unit, parameterStableId, parameterName, parameterStableKey, mean));
	}
	
	public String toString(){
		String res = "";
		res += "\"name\": \"" + geneSymbol + "\",";
		res += "\"group\": \"default gene group\",";
		int i = 0; 
		while (i < this.means.size()){
			MeanBean mean = this.means.get(i++);
			res += "\"" + mean.parameterName + "\": " + mean.mean;
			if (i < this.means.size()){
				res +=", ";
			}
		}
		return res;
	}
	
	
	public JSONObject getJson(){
		JSONObject obj = new JSONObject();
		obj.accumulate("name", this.geneSymbol);
		obj.accumulate("group", "default gene group");
		for (MeanBean mean: this.means){
			obj.accumulate(mean.parameterName, mean.mean);
		}
		return obj;
	}
	
	class MeanBean{
		String unit;
		String parameterStableId;
		String parameterName;
		String parameterStableKey;
		Double mean;
		
		public MeanBean(String unit, String parameterStableId,
		String parameterName, String parameterStableKey, Double mean){
			this.unit = unit;
			this.parameterName = parameterName;
			this.parameterStableId = parameterStableId;
			this.parameterStableKey = parameterStableKey;
			this.mean = mean;
		}
	}
}
