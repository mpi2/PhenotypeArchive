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
import java.util.List;

public enum CategoriesExclude {

	IMAGE_ONLY("imageOnly"), NO_DATA("no data"), NOT_DEFINED("not defined");
	
	private String text;
	
	private CategoriesExclude(String text){
		this.text=text;
	}
	
	@Override
	public String toString(){
		return this.text;
	}
	
	/**
	 * Use for use interface categories excludes things like "no data" "image only" and "not defined"
	 * @param categories
	 * @return usable categories List<String>
	 */
	public static List<String> getInterfaceFreindlyCategories(List<String> categories) {
		ArrayList<String> okCategoriesArrayList=new ArrayList<String>(categories);
		CategoriesExclude[] catExclude = CategoriesExclude.values();
		for(String catString: categories) {
			for(CategoriesExclude exclude:catExclude) {
				if(exclude.toString().equals(catString)) {
					okCategoriesArrayList.remove(catString);//remove category as should be excluded from the interface graphs etc
				}
			}
		}
		
		return okCategoriesArrayList;
	}
}
