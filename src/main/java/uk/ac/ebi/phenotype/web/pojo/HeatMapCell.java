/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.ebi.phenotype.web.pojo;

import org.apache.solr.client.solrj.beans.Field;

/**
 *
 * @author jwarren
 */
public class HeatMapCell {
    @Field("p_value")
    private Float pValue;
    @Field("parameter_stable_id")
    private String parameterStableId;
    
     @Field("mp_term_name")
    private String mpTermName="";

    public String getParameterStableId() {
        return parameterStableId;
    }

    public void setParameterStableId(String parameterStableId) {
        this.parameterStableId = parameterStableId;
    }

    public Float getpValue() {
        return pValue;
    }

    public void setpValue(Float pValue) {
        this.pValue = pValue;
    }
   

    public String getMpTermName() {
        return mpTermName;
    }

    public void setMpTermName(String mpTermName) {
        this.mpTermName = mpTermName;
    }
    
    
    
   
}
