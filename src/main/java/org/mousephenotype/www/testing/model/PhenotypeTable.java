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

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 *
 * @author mrelac
 * 
 * This class defines methods to fetch data from any page with an HTML table
 * with id <code>phenotypes</code>. Currently both the gene and phenotype pages
 * contain such a table whose contents are similar but whose columns differ
 * slightly in both name and order. Common processing code should go in this
 * superclass while individual page implementation details, such as the column
 * count and indexes should go into derived classes. These derived classes are
 * the ones that should be instantiated.
 */
public abstract class PhenotypeTable {
    public abstract int getTableColumnCount();
    public abstract int getColIndexPhenotype();
    public abstract int getColIndexZygosity();
    public abstract int getColIndexSex();
    public abstract int getColIndexProcedureParameter();
    public abstract int getColIndexPhenotypingCenter();
    public abstract int getColIndexSource();
    public abstract int getColIndexPvalue();
    public abstract int getColIndexGraph();
    public abstract String[][] getData(WebDriver driver, WebDriverWait wait, String target, int maxRows);

}
