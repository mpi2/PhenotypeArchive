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

package uk.ac.ebi.phenotype.service.dto;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


public class ParallelCoordinatesDTO {

	String geneSymbol;
	String geneAccession;
	HashMap<String, MeanBean> means;
	String group;
	
	public ParallelCoordinatesDTO(String geneSymbol, String geneAccession, String group, ArrayList<String> allColumns){
		this.geneAccession = geneAccession;
		this.geneSymbol = geneSymbol;
		this.group = group;
		means = new HashMap<>();
		for (String column: allColumns){
			means.put(column, new MeanBean( null, null, column, null, null));
		}
	}
	
	public void addMean( String unit, String parameterStableId,
	String parameterName, String parameterStableKey, Double mean){
		means.put(parameterName, new MeanBean( unit, parameterStableId, parameterName, parameterStableKey, mean));
	}
	
	public String toString(){
		String res = "";
		res += "\"name\": \"" + geneSymbol + "\",";
		res += "\"group\": \"" + group + "\",";
		int i = 0; 
		for (MeanBean mean : this.means.values()){
			res += "\"" + mean.parameterName + "\": ";
			res += mean.mean;
			i++;
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
		for (MeanBean mean: this.means.values()){
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
