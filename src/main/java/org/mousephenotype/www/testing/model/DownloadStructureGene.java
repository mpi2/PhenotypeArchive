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
 * as well as any additional methods pertaining to a download stream from a gene
 * page.
 */
public class DownloadStructureGene implements DownloadStructure {
    
    /**
     * @return The 0-relative phenotype column index in the gene download stream.
     */
    @Override
    public int getColIndexPhenotype() { return 0; }
    
    /**
     * @return The 0-relative allele column index in the gene download stream.
     */
    @Override
    public int getColIndexAllele() { return 1; }
    
    /**
     * @return The 0-relative zygosity column index in the gene download stream.
     */
    @Override
    public int getColIndexZygosity() { return 2; }
    
    /**
     * @return The 0-relative sex column index in the gene download stream.
     */
    @Override
    public int getColIndexSex() { return 3; }
    
    /**
     * @return The 0-relative procedure | parameter column index in the gene download stream.
     */
    @Override
    public int getColIndexProcedureParameter() { return 4; }
    
    /**
     * @return The 0-relative phenotyping center column index in the gene download stream.
     */
    @Override
    public int getColIndexPhenotypingCenter() { return 5; }
    
    /**
     * @return The 0-relative source column index in the gene download stream.
     */
    @Override
    public int getColIndexSource() { return 6; }
    
    /**
     * @return The 0-relative p value column index in the gene download stream.
     */
    @Override
    public int getColIndexPvalue() { return 7; }
    
    /**
     * @return The 0-relative graph link column index in the gene download stream.
     */
    @Override
    public int getColIndexGraph() { return 8; }
    
    /**
     * @return The number of columns in the gene download stream.
     */
    @Override
    public int getColumnCount() { return 9; }
    
    /**
     * 
     * @param tableIndex the gene page's phenotypes table column index
     * @return the matching download stream column index, if found; -1 otherwise
     */
    @Override
    public int getColIndex(int tableIndex) {
        PhenotypeTableGene table = new PhenotypeTableGene();
        if (tableIndex == table.getColIndexPhenotype()) {
            return getColIndexPhenotype();
        } else if (tableIndex == table.getColIndexAllele()) {
            return getColIndexAllele();
        } else if (tableIndex == table.getColIndexZygosity()) {
            return getColIndexZygosity();
        } else if (tableIndex == table.getColIndexSex()) {
            return getColIndexSex();
        } else if (tableIndex == table.getColIndexProcedureParameter()) {
            return getColIndexProcedureParameter();
        } else if (tableIndex == table.getColIndexPhenotypingCenter()) {
            return getColIndexPhenotypingCenter();
        } else if (tableIndex == table.getColIndexSource()) {
            return getColIndexSource();
        } else if (tableIndex == table.getColIndexPvalue()) {
            return getColIndexPvalue();
        } else if (tableIndex == table.getColIndexGraph()) {
            return getColIndexGraph();
        }
            
        return -1;
    }
}
