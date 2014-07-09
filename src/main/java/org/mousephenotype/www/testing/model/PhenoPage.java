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

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 *
 * @author mrelac
 
 * This class encapsulates the code and data necessary to represent a Phenotype
 * Archive phenotype page for Selenium testing.
 */
public class PhenoPage {
    private final String phenoPageTarget;
    private final WebDriverWait wait;
    private final WebDriver driver;
    private final String phenoId;
    
    public PhenoPage(WebDriver driver, WebDriverWait wait, String phenoPageTarget, String phenoId) {
        this.driver = driver;
        this.wait = wait;
        this.phenoPageTarget = phenoPageTarget;
        this.phenoId = phenoId;
        
        load();
    }
    
    public String[][] getPhenotypeTableData(int maxRows) {
        PhenotypeTablePheno ptPheno = new PhenotypeTablePheno();
        return ptPheno.getData(driver, wait, phenoPageTarget, maxRows);
    }
    
    public final PageStatus load() {
        String message;
        PageStatus status = new PageStatus();
        
        // Wait for page to load.
        try {
            driver.get(phenoPageTarget);
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.inner a").linkText(phenoId)));
        } catch (NoSuchElementException | TimeoutException te ) {
            message = "Expected page for MP_TERM_ID " + phenoId + "(" + phenoPageTarget + ") but found none.";
            status.addFail(message);
        } catch (Exception e) {
            message = "EXCEPTION processing target URL " + phenoPageTarget + ": " + e.getLocalizedMessage();
            status.addFail(message);
        }
        
        return status;
    }
    
    // PRIVATE METHODS
    
    
    
    // GETTERS AND SETTERS
    

    
}
