/**
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
/**
 * Copyright © 2014 EMBL - European Bioinformatics Institute
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License.  
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mousephenotype.www.testing.model;

/**
 *
 * @author mrelac
 * 
 * The download streams for gene, pheno, and graph pages are similar in a
 * general way but differ in column name, placement, and count. This interface
 * defines the contract for column indexes common to all download streams.
 * Instantiated subclasses provide the implementations and may add additional
 * column index getters.
 */
public interface DownloadGraphMap {
    // These are the column indexes in the TSV/XLS streams for the common elements
    // that are validated in download code common to all graphs.
    public int getColIndexAlleleSymbol();
    public int getColIndexBackground();
    public int getColIndexGeneSymbol();
    public int getColIndexMetadataGroup();
    public int getColIndexParameterName();
    public int getColIndexParameterStableId();
    public int getColIndexPipelineName();
    public int getColIndexPhenotypingCenter();
    
    public int getColumnCount();
}
