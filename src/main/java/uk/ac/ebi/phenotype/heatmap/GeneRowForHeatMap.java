/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.ebi.phenotype.heatmap;

import java.util.HashMap;
import java.util.Map;
import uk.ac.ebi.phenotype.service.*;

/**
 *
 * @author jwarren
 */
public class GeneRowForHeatMap {

    private String accession="";
    Map<String, HeatMapCell> paramToCellMap=new HashMap<>();

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
    
    
    
    
}
