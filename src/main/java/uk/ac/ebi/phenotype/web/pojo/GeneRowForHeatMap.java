/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.ebi.phenotype.web.pojo;

import java.util.HashMap;
import java.util.Map;
import uk.ac.ebi.phenotype.service.*;

/**
 *
 * @author jwarren
 */
public class GeneRowForHeatMap implements Comparable<GeneRowForHeatMap>{

    private String accession="";
    private String symbol="";
    private String miceProduced="No";//not boolean as 3 states No, Yes, In progress - could have an enum I guess?
    private Boolean primaryPhenotype=false;
    
    public String getMiceProduced() {
		return miceProduced;
	}

	public void setMiceProduced(String miceProduced) {
		this.miceProduced = miceProduced;
	}

	public Boolean getPrimaryPhenotype() {
		return primaryPhenotype;
	}

	public void setPrimaryPhenotype(Boolean primaryPhenotype) {
		this.primaryPhenotype = primaryPhenotype;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	Map<String, HeatMapCell> paramToCellMap=new HashMap<>();
	private Float lowestPValue=new Float(1000000);//just large number so we don't get null pointers

    public Map<String, HeatMapCell> getParamToCellMap() {
        return paramToCellMap;
    }

    public void setParamToCellMap(Map<String, HeatMapCell> paramToCellMap) {
        this.paramToCellMap = paramToCellMap;
    }

    public GeneRowForHeatMap(String accession){
        this.accession=accession;
    }
    
    public String getAccession() {
        return this.accession;
    }

    public void add(HeatMapCell cell) {
       this.paramToCellMap.put(cell.getParameterStableId(), cell);
    }
    
    public int compareTo(GeneRowForHeatMap compareRow) {
		Float compareQuantity =  compareRow.getLowestPValue(); 
		if(this.lowestPValue>compareQuantity){
			return 1;
		}
		if(this.lowestPValue<compareQuantity){
			return -1;
		}
		return 0;
	}

	public Float getLowestPValue() {
		return this.lowestPValue;
		
	}

	public void setLowestPValue(Float getpValue) {
		this.lowestPValue=getpValue;
	}	
    
    
    
}
