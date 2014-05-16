/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.ebi.phenotype.heatmap;

import uk.ac.ebi.phenotype.service.*;

/**
 *
 * @author jwarren
 */
public class GeneRowForHeatMap {

    private String accession="";
    
    public GeneRowForHeatMap(String accession){
        this.accession=accession;
    }
    
    public String getAccession() {
        return this.accession;
    }
    
    
    
    
}
