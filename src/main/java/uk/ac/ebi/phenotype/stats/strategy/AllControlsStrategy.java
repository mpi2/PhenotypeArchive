/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.ebi.phenotype.stats.strategy;

import java.util.List;
import uk.ac.ebi.phenotype.pojo.SexType;
import uk.ac.ebi.phenotype.pojo.ZygosityType;
import uk.ac.ebi.phenotype.stats.ExperimentDTO;
/**
 * Implements the all control selection strategy.
 * 
 * All controls means control data that were collected at any time.
 */
public class AllControlsStrategy implements ControlSelectionStrategy {

    @Override
    public List<ExperimentDTO> execute(String geneAcc, ZygosityType zygosity, List<SexType> sexes, String parameterId, String metadataGroup) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}