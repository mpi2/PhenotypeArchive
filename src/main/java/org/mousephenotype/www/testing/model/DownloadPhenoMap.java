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
 * This class extends <code>GridMap</code> by adding column definitions for
 * the download phenotype page.
 */
public class DownloadPhenoMap {
    public final static int COL_INDEX_GENE                = 0;
    public final static int COL_INDEX_ALLELE              = 1;
    public final static int COL_INDEX_ZYGOSITY            = 2;
    public final static int COL_INDEX_SEX                 = 3;
    public final static int COL_INDEX_PHENOTYPE           = 4;
    public final static int COL_INDEX_PROCEDURE_PARAMETER = 5;
    public final static int COL_INDEX_PHENOTYPING_CENTER  = 6;
    public final static int COL_INDEX_SOURCE              = 7;
    public final static int COL_INDEX_P_VALUE             = 8;
    public final static int COL_INDEX_GRAPH               = 9;
}