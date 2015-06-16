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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.ebi.phenotype.web.pojo;

import java.util.Collection;
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
	Map<String, HeatMapCell> xAxisToCellMap=new HashMap<>();
	private Float lowestPValue=new Float(1000000);//just large number so we don't get null pointers

	
	
    public Map<String, HeatMapCell> getXAxisToCellMap() {
        return xAxisToCellMap;
    }

    public void setXAxisToCellMap(Map<String, HeatMapCell> paramToCellMap) {
        this.xAxisToCellMap = paramToCellMap;
    }

    public GeneRowForHeatMap(String accession){
        this.accession=accession;
    }
    
    public String getAccession() {
        return this.accession;
    }

    public void add(HeatMapCell cell) {
       this.xAxisToCellMap.put(cell.getxAxisKey(), cell);
    }
    
    
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
    
    public int compareTo(GeneRowForHeatMap compareRow) {
		int compareNumberOfMps =  0;
		Collection<HeatMapCell> compareValues = compareRow.getXAxisToCellMap().values();
		for(HeatMapCell cell:compareValues){
			if(cell.getStatus().equals("Data Available")){
				compareNumberOfMps++;
			}
		}
		Collection<HeatMapCell> values = this.xAxisToCellMap.values();
		int thisNumberOfMps=0;
		for(HeatMapCell cell:values){
			if(cell.getStatus().equals("Data Available")){
				thisNumberOfMps++;
			}
		}
		if(thisNumberOfMps>compareNumberOfMps){
			return -1;
		}
		if(thisNumberOfMps<compareNumberOfMps){
			return 1;
		}
		if(thisNumberOfMps==0 && compareNumberOfMps==0){
			//compare if they have No Data Available as there first result
			HeatMapCell compareCell = compareRow.getXAxisToCellMap().entrySet().iterator().next().getValue();
			HeatMapCell thisCell = (HeatMapCell)this.getXAxisToCellMap().entrySet().iterator().next().getValue();
			if(!thisCell.getStatus().equals("No Data Available") && compareCell.getStatus().equals("No Data Available")){
				return -1;
			}
			if(thisCell.getStatus().equals("No Data Available") && !compareCell.getStatus().equals("No Data Available")){
				return 1;
			}
		}
//		Float compareQuantity =  compareRow.getLowestPValue(); 
//		if(this.lowestPValue>compareQuantity){
//			return 1;
//		}
//		if(this.lowestPValue<compareQuantity){
//			return -1;
//		}
		return 0;
	}

	public Float getLowestPValue() {
		return this.lowestPValue;
		
	}

	public void setLowestPValue(Float getpValue) {
		this.lowestPValue=getpValue;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		return "GeneRowForHeatMap [accession=" + accession + ", symbol=" + symbol + ", miceProduced=" + miceProduced + ", primaryPhenotype=" + primaryPhenotype + ", xAxisToCellMap=" + xAxisToCellMap + ", lowestPValue=" + lowestPValue + "]";
	}	
    
    
    
}
