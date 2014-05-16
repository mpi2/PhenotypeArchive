/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.ebi.phenotype.heatmap;

/**
 *
 * @author jwarren
 */
public class HeatMapCell {
    private Float pValue;

    public Float getpValue() {
        return pValue;
    }

    public void setpValue(Float pValue) {
        this.pValue = pValue;
    }
    private String mpTermName="";

    public String getMpTermName() {
        return mpTermName;
    }

    public void setMpTermName(String mpTermName) {
        this.mpTermName = mpTermName;
    }
    
    
    
   
}
