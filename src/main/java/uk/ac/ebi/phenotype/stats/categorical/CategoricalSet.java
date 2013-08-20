package uk.ac.ebi.phenotype.stats.categorical;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to hold multiple CategoricalDataObjects pertaining to one bar no the graph eg. data for control, normal, abnormal is one set another set would be data for het, normal and abnormal
 * @author jwarren
 *
 */
public class CategoricalSet {
	String name="";
		List<CategoricalDataObject> catObjects=new ArrayList<CategoricalDataObject>();

		public List<CategoricalDataObject> getCatObjects() {
			return catObjects;
		}

		public void setCatObjects(List<CategoricalDataObject> catObjects) {
			this.catObjects = catObjects;
		}

		public void setName(String name) {
			this.name=name;
			
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
			
		}
		
		public String toString(){
			String string="Categorical Set name="+this.name+" CategroicalDataObjects=";
			for(CategoricalDataObject cat:catObjects){
				string+=cat.toString();
			}
			return string;
			
		}

}
