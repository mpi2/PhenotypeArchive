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
import java.util.HashMap;
import java.util.List;

import uk.ac.ebi.phenotype.pojo.SexType;


/**
 * Class to hold multiple CategoricalDataObjects pertaining to one bar no the graph eg. data for control, normal, abnormal is one set another set would be data for het, normal and abnormal
 * @author jwarren
 *
 */
public class CategoricalSet {
	String name="";
		List<CategoricalDataObject> catObjects=new ArrayList<CategoricalDataObject>();
		HashMap<String, CategoricalDataObject> catObjMap = new HashMap<String, CategoricalDataObject>();
		long count = 0;
		private SexType sexType;

		public List<CategoricalDataObject> getCatObjects() {
			return catObjects;
		}

		public void setCatObjects(List<CategoricalDataObject> catObjects) {
			this.catObjects = catObjects;
			for (CategoricalDataObject obj : catObjects){
				catObjMap.put(obj.getCategory(), obj);
			}
		}

		public void setName(String name) {
			this.name=name;
		}
		
		public CategoricalDataObject getCategoryByLabel(String label){
			if (!catObjMap.containsKey(label))
				return null;
			return catObjMap.get(label);
		}
		
		public void addToCategory(String categoryLabel){
			if (catObjMap.containsKey(categoryLabel)){
				long count = catObjMap.get(categoryLabel).getCount() + 1;
				catObjMap.get(categoryLabel).setCount(count);
			}
			else {
				CategoricalDataObject catObj = new CategoricalDataObject();
				catObj.setName(categoryLabel);
				catObj.setCount((long)1);
				catObjMap.put(categoryLabel, catObj);
			}
			count++;
			catObjects = new ArrayList<CategoricalDataObject>(catObjMap.values());
		}
		public long getCount(){
			return count;
		}
		
		/**
		 * Name of set e.g. Control, Homozygote, Heterozygote
		 * @return
		 */
		public String getName(){
			return this.name;
		}

		public void add(CategoricalDataObject controlCatData) {
			this.catObjects.add(controlCatData);
			this.catObjMap.put(controlCatData.getCategory(), controlCatData);
			this.count += controlCatData.getCount();
		}
		
		public SexType getSexType() {
			return sexType;
		}

		public void setSexType(SexType sexType) {
			this.sexType = sexType;
		}
		
		public String toString(){
			String string="Categorical Set name="+this.name+" CategroicalDataObjects=";
			for(CategoricalDataObject cat:catObjects){
				string+=cat.toString();
			}
			return string;
			
		}

}
