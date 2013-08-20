package uk.ac.ebi.phenotype.stats.categorical;

import uk.ac.ebi.phenotype.pojo.ZygosityType;

/**
 * class to hold the categorical data with associated meta data and stats so we
 * keep things neat and tidy and don't have long list of parameters and or
 * arrays that can get mixed up
 * 
 * @author jwarren
 * 
 */
public class CategoricalDataObject {
	private String name = "";

	private Long count=new Long(0);
	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getpValue() {
		return this.pValue;
	}

	public void setpValue(double pValue) {
		this.pValue = pValue;
	}

	public Double getMaxEffect() {
		return maxEffect;
	}

	public void setMaxEffect(double maxEffect) {
		this.maxEffect = maxEffect;
	}

	private Double pValue;
	private Double maxEffect;
	private String category="";

	public String getCategory() {
		return category;
	}

	public CategoricalDataObject(String name, double pValue, double maxEffect) {
		this.name = name;
		this.pValue = pValue;
		this.maxEffect = maxEffect;
	}

	public CategoricalDataObject(){
		
	}
	
	public String toString(){
		String name=this.getName();
		String count="";
				if(this.count!=null){
					count=Long.toString(this.count);
				}
		String pValue="";
		if(this.pValue!=null){
			pValue=Double.toString(this.pValue);
		}
		String maxEffect="";
		if(this.maxEffect!=null){
			maxEffect=Double.toString(this.maxEffect);
		}
	
		String string="name="+name+" category="+category+" count="+count+" pValue="+pValue+" maxEffect="+maxEffect;
		return string;
	}

	public void setCategory(String category) {
		this.category=category;
		
	}
	
}
