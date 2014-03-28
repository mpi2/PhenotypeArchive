package uk.ac.ebi.phenotype.stats.categorical;

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
