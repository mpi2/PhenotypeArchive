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
 * This class implements the contract defined by the DownloadStructure interface
 * as well as any additional methods pertaining to a download stream from a graph
 * page.
 */
public class DownloadStructureUnidimensional implements DownloadStructure {
    private final int BACKGROUND          =  0;
    private final int PIPELINE_NAME       =  0;
    private final int PIPELINE_STABLE_ID  =  1;
    private final int PROCEDURE_STABLE_ID =  2;
    private final int PROCEDURE_NAME      =  3;
    private final int PARAMETER_STABLE_ID =  4;
    private final int PARAMETER_NAME      =  5;
    private final int STRAIN_ACCESSION_ID =  6;
    private final int STRAIN_NAME         =  7;
    private final int GENE_SYMBOL         =  8;
    private final int GENE_ACCESSION      =  9;
    private final int ALLELE_SYMBOL       = 10;
    private final int ALLELE_ACCESSION    = 11;
    private final int PHENOTYPING_CENTER  = 12;
    private final int COLONY_ID           = 13;
    private final int DATE_OF_EXPERIMENT  = 14;
    private final int ZYGOSITY            = 15;
    private final int SEX                 = 16;
    private final int GROUP               = 17;
    private final int EXTERNAL_SAMPLE_ID  = 18;
    private final int METADATA            = 19;
    private final int METADATA_GROUP      = 20;
    private final int DATA_POINT          = 21;
    
    /**
     * @return The 0-relative pipeline name column index in the gene download stream.
     * 
     */
    public int getColIndexAlleleAccession()   { return ALLELE_ACCESSION; }
    @Override
    public int getColIndexAlleleSymbol()      { return ALLELE_SYMBOL; }
    public int getColIndexBackground()        { return BACKGROUND; }
    public int getColIndexColonyId()          { return COLONY_ID; }
    public int getColIndexDataPoint()         { return DATA_POINT; }
    public int getColIndexDateOfExperiment()  { return DATE_OF_EXPERIMENT; }
    public int getColIndexExternalSampleId()  { return EXTERNAL_SAMPLE_ID; }
    public int getColIndexGeneAccession()     { return GENE_ACCESSION; }
    public int getColIndexGeneSymbol()        { return GENE_SYMBOL; }
    public int getColIndexGroup()             { return GROUP; }
    public int getColIndexMetadata()          { return METADATA; }
    public int getColIndexMetadataGroup()     { return METADATA_GROUP; }
    public int getColIndexParameterName()     { return PARAMETER_NAME; }
    public int getColIndexParameterStableId() { return PARAMETER_STABLE_ID; }
    @Override
    public int getColIndexPhenotypingCenter() { return PHENOTYPING_CENTER; }
    public int getColIndexPipelineName()      { return PIPELINE_NAME; }
    public int getColIndexPipelineStableId()  { return PIPELINE_STABLE_ID; }
    public int getColIndexProcedureName()     { return PROCEDURE_NAME; }
    public int getColIndexProcedureStableId() { return PROCEDURE_STABLE_ID; }
    @Override
    public int getColIndexSex()               { return SEX; }
    public int getColIndexStrainAccessionId() { return STRAIN_ACCESSION_ID; }
    public int getColIndexStrainName()        { return STRAIN_NAME; }
    @Override
    public int getColIndexZygosity()          { return ZYGOSITY; }
    
    @Override
    public int getColumnCount() { return 22; }

}