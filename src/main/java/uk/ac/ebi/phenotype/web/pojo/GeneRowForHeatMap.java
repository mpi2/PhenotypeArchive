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
 
		//ascending order
		if(this.lowestPValue>compareQuantity){
			return 1;
		}
		if(this.lowestPValue<compareQuantity){
			return -1;
		}
		
		return 0;
 
		//descending order
		//return compareQuantity - this.quantity;
 
	}

	public Float getLowestPValue() {
		return this.lowestPValue;
		
	}

	public void setLowestPValue(Float getpValue) {
		this.lowestPValue=getpValue;
	}	
    
    
    
}
