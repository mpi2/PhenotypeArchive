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
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import uk.ac.ebi.phenotype.util.Utils;

/**
 *
 * @author mrelac
 * 
 * This class encapsulates the code and data necessary to represent the important
 * components of a search page 'imagesGrid' HTML table common to all Image facet
 * views.
 */
public class SearchImpcImageTable extends SearchFacetTable {
    private SearchImageAnnotationView searchImageAnnotationView = null;
    private SearchImageImageView      searchImageImageView      = null;
    
    public static final String SHOW_ANNOTATION_VIEW = "Show Annotation View";
    public static final String SHOW_IMAGE_VIEW      = "Show Image View";
    
    /**
     * Creates a new <code>SearchIMPCImageTable</code> instance.
     * @param driver A <code>WebDriver</code> instance pointing to the search
     * facet table with thead and tbody definitions.
     * @param timeoutInSeconds The <code>WebDriver</code> timeout, in seconds
     */
    public SearchImpcImageTable(WebDriver driver, int timeoutInSeconds) {
        super(driver, "//table[@id='impc_imagesGrid']", timeoutInSeconds);
        
        searchImageAnnotationView = new SearchImageAnnotationView(driver, timeoutInSeconds);
    }
    
    public enum ImageFacetView {
        ANNOTATION_VIEW,
        IMAGE_VIEW
    }
    
    public final ImageFacetView getCurrentView() {
        String imgViewSwitcherText = driver.findElement(By.cssSelector("span#imgViewSwitcher")).getText();
        return (imgViewSwitcherText.equals(SHOW_IMAGE_VIEW) ? ImageFacetView.ANNOTATION_VIEW : ImageFacetView.IMAGE_VIEW);
    }
    
    public void setCurrentView(ImageFacetView view) {
        if (getCurrentView() != view) {
            SearchPage.WindowState toolboxState = getToolboxState();            // Save tool box state for later restore.
            clickToolbox(SearchPage.WindowState.CLOSED);
            WebElement imgViewSwitcherElement = driver.findElement(By.cssSelector("span#imgViewSwitcher"));
            TestUtils.scrollToTop(driver, imgViewSwitcherElement, -50);         // Scroll 'Show Image View' link into view.
            driver.findElement(By.cssSelector("span#imgViewSwitcher")).click();
            updateImageTableAfterChange();
            if (toolboxState != getToolboxState())
                clickToolbox(toolboxState);
        }
    }
    
    /**
     * @return the result count at the bottom of the images screen right-hand panel.
     */
    public int getResultCount() {
            String rawResultCount = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@id='impc_imagesGrid_info']"))).getText();
            String[] rawResultCountParts = rawResultCount.split(" ");
            Integer niResultCount = Utils.tryParseInt(rawResultCountParts[5].replace(",", ""));
            return (niResultCount == null ? 0 : niResultCount);
    }
    
    /**
     * This method is meant to be called after any change to the image table,
     * such as changing between annotation and image view, or changing
     * pagination pages. It is required to keep the image table internals in
     * sync with what is seen on the page.
     */
    public void updateImageTableAfterChange() {
        switch (getCurrentView()) {
            case ANNOTATION_VIEW:
                searchImageAnnotationView = new SearchImageAnnotationView(driver, timeoutInSeconds);
                searchImageImageView = null;
                break;

            case IMAGE_VIEW:
                searchImageAnnotationView = null;
                searchImageImageView = new SearchImageImageView(driver, timeoutInSeconds);
                break;
        }
        
        setTable(driver.findElement(By.xpath(tableXpath)));
    }
    
    @Override
    public PageStatus validateDownload(String[][] data) {
        PageStatus status = new PageStatus();
        
        switch (getCurrentView()) {
            case ANNOTATION_VIEW:
                status = searchImageAnnotationView.validateDownload(data);
                break;
                
            case IMAGE_VIEW:
                status = searchImageImageView.validateDownload(data);
                break;
        }
        
        return status;
    }
    
}