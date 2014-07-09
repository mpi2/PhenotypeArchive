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
 * This class extends PhenotypeTable, which has code common to the gene page and
 * the phenotypes page, by providing getters for the column indexes.
 */
public class PhenotypeTablePheno extends PhenotypeTable {
    
    /**
     * Return the 0-relative gene / allele column index in the phenotypes html table on the gene page.
     * @return the 0-relative gene / allele column index in the phenotypes html table on the gene page.
     */
    public int getColIndexGeneAllele() { return 0; }
    
    /**
     * Return the 0-relative zygosity column index in the phenotypes html table on the gene page.
     * @return the 0-relative zygosity column index in the phenotypes html table on the gene page.
     */
    @Override
    public int getColIndexZygosity(){ return 1; }
    
    /**
     * Return the 0-relative sex column index in the phenotypes html table on the gene page.
     * @return the 0-relative sex column index in the phenotypes html table on the gene page.
     */
    @Override
    public int getColIndexSex(){ return 2; }
    
    /**
     * Return the 0-relative phenotyp column index in the phenotypes html table on the gene page.
     * @return the 0-relative phenotype column index in the phenotypes html table on the gene page.
     */
    @Override
    public int getColIndexPhenotype(){ return 3; }
    
    /**
     * Return the 0-relative procedure/parameter column index in the phenotypes html table on the gene page.
     * @return the 0-relative procedure/parameter column index in the phenotypes html table on the gene page.
     */
    @Override
    public int getColIndexProcedureParameter(){ return 4; }
    
    /**
     * Return the 0-relative phenotyping center column index in the phenotypes html table on the gene page.
     * @return the 0-relative phenotyping center column index in the phenotypes html table on the gene page.
     */
    @Override
    public int getColIndexPhenotypingCenter(){ return 5; }
    
    /**
     * Return the 0-relative source column index in the phenotypes html table on the gene page.
     * @return the 0-relative source column index in the phenotypes html table on the gene page.
     */
    @Override
    public int getColIndexSource(){ return 6; }
    
    /**
     * Return the 0-relative p value column index in the phenotypes html table on the gene page.
     * @return the 0-relative p value column index in the phenotypes html table on the gene page.
     */
    @Override
    public int getColIndexPvalue(){ return 7; }
    
    /**
     * Return the 0-relative graph link column index in the phenotypes html table on the gene page.
     * @return the 0-relative graph link column index in the phenotypes html table on the gene page.
     */
    @Override
    public int getColIndexGraph(){ return 8; }
    
    /**
     * Return the number of columns in the phenotypes html table on the gene page.
     * @return the number of columns in the phenotypes html table on the gene page.
     */
    @Override
    public int getTableColumnCount() { return 9; }
    
}
