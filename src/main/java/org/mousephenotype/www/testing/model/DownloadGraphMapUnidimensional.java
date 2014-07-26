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
 
 This class implements the contract defined by the DownloadGraphMap interface
 as well as any additional methods pertaining to a download stream from a graph
 page.
 */
public class DownloadGraphMapUnidimensional implements DownloadGraphMap {
    public final int BACKGROUND          =  0;
    public final int PIPELINE_NAME       =  0;
    public final int PIPELINE_STABLE_ID  =  1;
    public final int PROCEDURE_STABLE_ID =  2;
    public final int PROCEDURE_NAME      =  3;
    public final int PARAMETER_STABLE_ID =  4;
    public final int PARAMETER_NAME      =  5;
    public final int STRAIN_ACCESSION_ID =  6;
    public final int STRAIN_NAME         =  7;
    public final int GENE_SYMBOL         =  8;
    public final int GENE_ACCESSION      =  9;
    public final int ALLELE_SYMBOL       = 10;
    public final int ALLELE_ACCESSION    = 11;
    public final int PHENOTYPING_CENTER  = 12;
    public final int COLONY_ID           = 13;
    public final int DATE_OF_EXPERIMENT  = 14;
    public final int ZYGOSITY            = 15;
    public final int SEX                 = 16;
    public final int GROUP               = 17;
    public final int EXTERNAL_SAMPLE_ID  = 18;
    public final int METADATA            = 19;
    public final int METADATA_GROUP      = 20;
    public final int DATA_POINT          = 21;
    
    @Override public int getColIndexAlleleSymbol()      { return ALLELE_SYMBOL; }
    @Override public int getColIndexBackground()        { return BACKGROUND; }
    @Override public int getColIndexGeneSymbol()        { return GENE_SYMBOL; }
    @Override public int getColIndexMetadataGroup()     { return METADATA_GROUP; }
    @Override public int getColIndexParameterName()     { return PARAMETER_NAME; }
    @Override public int getColIndexParameterStableId() { return PARAMETER_STABLE_ID; }
    @Override public int getColIndexPipelineName()      { return PIPELINE_NAME; }
    @Override public int getColIndexPhenotypingCenter() { return PHENOTYPING_CENTER; }
    
    @Override public int getColumnCount() { return 23; }

}