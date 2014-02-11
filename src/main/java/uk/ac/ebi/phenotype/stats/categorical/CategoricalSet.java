package uk.ac.ebi.phenotype.stats.categorical;

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
