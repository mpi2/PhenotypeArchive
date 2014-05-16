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
    private Double pValue;
    private String mpTermName="";

    public String getMpTermName() {
        return mpTermName;
    }

    public void setpValue(Double pValue) {
        this.pValue = pValue;
    }

    public void setMpTermName(String mpTermName) {
        this.mpTermName = mpTermName;
    }
    
    
    public Double getPValue(){
        return this.pValue;
    }
    
    public void setPValue(Double pValue){
        this.pValue=pValue;
    }
    

    public void setPValue(Object fieldValue) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
