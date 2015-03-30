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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
 * components of a search page 'imagesGrid' HTML table (annotation view) for images.
 */
public class SearchImageAnnotationView extends SearchFacetTable {
    
    public static final int COL_INDEX_ANNOTATION_TYPE     = 0;
    public static final int COL_INDEX_ANNOTATION_TERM     = 1;
    public static final int COL_INDEX_ANNOTATION_ID       = 2;
    public static final int COL_INDEX_RELATED_IMAGE_COUNT = 3;
    public static final int COL_INDEX_LAST = COL_INDEX_RELATED_IMAGE_COUNT;     // Should always point to the last (highest-numbered) index.
    
    private final List<ImageRow> bodyRows = new ArrayList();
    private final GridMap pageData;
    private Map<TableComponent, By> map;
    
    /**
     * Creates a new <code>SearchImageTable</code> instance.
     * 
     * @param driver A <code>WebDriver</code> instance pointing to the search
     * facet table with thead and tbody definitions.
     * @param timeoutInSeconds The <code>WebDriver</code> timeout, in seconds
     * @param map a map of HTML table-related definitions, keyed by <code>
     * TableComponent</code>.
     */
    public SearchImageAnnotationView(WebDriver driver, int timeoutInSeconds, Map<TableComponent, By> map) {
        super(driver, timeoutInSeconds, map);
        this.map = map;
        
        pageData = load();
    }

    /**
     * Validates download data against this <code>SearchImageAnnotationView</code>
     * instance.
     * 
     * @param downloadDataArray The download data used for comparison
     * @return validation status
     * annotationType, annotationTerm, annotationId, annotationIdLink, relatedImageCount, imagesLink
     */
    @Override
    public PageStatus validateDownload(String[][] downloadDataArray) {
        final Integer[] pageColumns = {
              COL_INDEX_ANNOTATION_ID
            , COL_INDEX_ANNOTATION_TERM
            , COL_INDEX_ANNOTATION_TYPE
            , COL_INDEX_RELATED_IMAGE_COUNT
        };
        final Integer[] downloadColumns = {
              DownloadSearchMapImagesAnnotationView.COL_INDEX_ANNOTATION_ID
            , DownloadSearchMapImagesAnnotationView.COL_INDEX_ANNOTATION_TERM
            , DownloadSearchMapImagesAnnotationView.COL_INDEX_ANNOTATION_TYPE
            , DownloadSearchMapImagesAnnotationView.COL_INDEX_RELATED_IMAGE_COUNT
        };
        
        return validateDownloadInternal(pageData, pageColumns, downloadDataArray, downloadColumns, driver.getCurrentUrl());   
    }
    
    
    // PRIVATE METHODS
    
    
    /**
     * Pulls all rows of data and column access variables from the search page's
     * 'maGrid' HTML table.
     *
     * @return <code>numRows</code> rows of data and column access variables
     * from the search page's 'maGrid' HTML table.
     */
    private GridMap load() {
        return load(null);
    }

    /**
     * Pulls <code>numRows</code> rows of search page gene facet data and column
     * access variables from the search page's 'maGrid' HTML table.
     *
     * @param numRows the number of <code>GridMap</code> table rows to return,
     * including the heading row. To specify all rows, set <code>numRows</code>
     * to null.
     * @return <code>numRows</code> rows of search page gene facet data and
     * column access variables from the search page's 'maGrid' HTML table.
     */
    private GridMap load(Integer numRows) {
        if (numRows == null)
            numRows = computeTableRowCount();
        
        String[][] pageArray;
        
        // Wait for page.
        wait.until(ExpectedConditions.presenceOfElementLocated(map.get(TableComponent.BY_TABLE)));
        int numCols = COL_INDEX_LAST + 1;
        
        pageArray = new String[numRows][numCols];                               // Allocate space for the data.
        for (int i = 0; i < numCols; i++) {
            pageArray[0][i] = "Column_" + i;                                    // Set the headings.
        }
        
        // Save the body values.
        List<WebElement> bodyRowElementsList = table.findElements(By.cssSelector("tbody tr"));
        if ( ! bodyRowElementsList.isEmpty()) {
            int sourceRowIndex = 1;
            
            pageArray[sourceRowIndex][COL_INDEX_ANNOTATION_ID] = "";            // Insure there is always a non-null value.
            pageArray[sourceRowIndex][COL_INDEX_ANNOTATION_TERM] = "";
            pageArray[sourceRowIndex][COL_INDEX_ANNOTATION_TYPE] = "";
            pageArray[sourceRowIndex][COL_INDEX_RELATED_IMAGE_COUNT] = "";
            for (WebElement bodyRowElements : bodyRowElementsList) {
                ImageRow bodyRow = new ImageRowFactory(bodyRowElements).getImageRow();
                
                pageArray[sourceRowIndex][COL_INDEX_ANNOTATION_ID] = bodyRow.getAnnotationId();
                pageArray[sourceRowIndex][COL_INDEX_ANNOTATION_TERM] = bodyRow.getAnnotationTerm();
                pageArray[sourceRowIndex][COL_INDEX_ANNOTATION_TYPE] = bodyRow.getAnnotationType();
                pageArray[sourceRowIndex][COL_INDEX_RELATED_IMAGE_COUNT] = Integer.toString(bodyRow.getRelatedImageCount());
                
                sourceRowIndex++;
                bodyRows.add(bodyRow);
            }
        }
        
        return new GridMap(pageArray, driver.getCurrentUrl());
    }
    
    
    // PRIVATE CLASSES
    
    
    private class ImageRowDefault implements ImageRow {
        protected String annotationType     = "";
        protected String annotationTerm     = "";
        protected String annotationId       = "";
        protected String annotationIdLink   = "";
        protected int    relatedImageCount  = -1;
        protected String imagesLink         = "";
        
        @Override
        public String toString() {
            return "annotationType: '"       + annotationType
                 + "'  annotationTerm: '"    + annotationTerm
                 + "'  annotationId: '"      + annotationId
                 + "'  annotationIdLink: '"  + annotationIdLink
                 + "'  annotationName: '"    + annotationTerm
                 + "'  relatedImageCount: '" + relatedImageCount
                 + "'  imagesLink: '"        + imagesLink + "'";
        }
        
        public ImageRowDefault(WebElement trElement) {
            List<WebElement> bodyRowElementList= trElement.findElements(By.cssSelector("td"));
            WebElement line1Element = bodyRowElementList.get(0);
            String message;
            
            // Example parsings: "Gene: Pus7l (1 image)"                "Pus7l" and "1 image" are links.
            //                   "Procedure: Dysmorphology (1 image)"   "1 image" is the only link.
            String[] line1FirstPart = line1Element.getText().split(":");    // [0]: 'Gene'           [1]: 'Pus7l (1 image)'      or
                                                                            // [0]: 'Procedure'      [1]: 'Dysmorphology Observation V2 (Mouse GP) 17-02-2009 (1 image)'
            annotationType = line1FirstPart[0].trim();
            
            int lastLParenIndex = line1FirstPart[1].lastIndexOf("(");
            annotationTerm = line1FirstPart[1].substring(0, lastLParenIndex - 1).trim();
            
            String line1SecondPart = line1FirstPart[1].substring(lastLParenIndex);  // [0]: 'Pus71 '         [1]: '1 image)'    or
            int spacePos = line1SecondPart.indexOf(" ");                            // [0]: 'Dysmorphology Observation V2 (Mouse GP) 17-02-2009 ' [1]: '1 image)'
            String sImageCount = line1SecondPart.substring(1, spacePos);
            
            Integer imageCount = Utils.tryParseInt(sImageCount);
            if (imageCount == null) {
                
                message = "ERROR: SearchImageAnnotationView.ImageRowDefault.ImageRowDefault(): Couldn't find image count. URL: " + driver.getCurrentUrl();
                System.out.println(message);
                throw new RuntimeException(message);
            }
            relatedImageCount = imageCount;
            
            // Some annotation types are followed by two links (e.g. Gene: Ell2 (1 image)), and
            // some are followed by text and a single link (e.g. Procedure: Dysmorphology (1 image)
            // Parse accordingly.
            List<WebElement> anchorElements = bodyRowElementList.get(0).findElements(By.cssSelector("a"));
            WebElement imageAnchorElement = null;
            switch (anchorElements.size()) {
                case 1:
                    annotationId = "exp";
                    imageAnchorElement = anchorElements.get(0);
                    break;
                    
                case 2:
                    annotationIdLink = anchorElements.get(0).getAttribute("href");                                      // annotationLink.
                    annotationIdLink = TestUtils.urlDecode(annotationIdLink);                                           //    Decode it.
                    int pos = annotationIdLink.lastIndexOf("/");
                    annotationId = annotationIdLink.substring(pos + 1).trim();                                          // annotationId.
                    imageAnchorElement = anchorElements.get(1);
                    break;
            }
            
            if (imageAnchorElement != null) {
                imagesLink = imageAnchorElement.getAttribute("href");                                                   // imagesLink.
                imagesLink = TestUtils.urlDecode(imagesLink);                                                           //    Decode it.
            }
        }

        @Override
        public String getAnnotationType() {
            return annotationType;
        }

        @Override
        public String getAnnotationTerm() {
            return annotationTerm;
        }

        @Override
        public String getAnnotationId() {
            return annotationId;
        }

        @Override
        public String getAnnotationIdLink() {
            return annotationIdLink;
        }

        @Override
        public int getRelatedImageCount() {
            return relatedImageCount;
        }

        @Override
        public String getImagesLink() {
            return imagesLink;
        }
    }
    

    public class ImageRowPhenotype extends ImageRowDefault {
        public ImageRowPhenotype(WebElement trElement) {
            super(trElement);
        }
    }
    
    public class ImageRowDisease extends ImageRowDefault {
        public ImageRowDisease(WebElement trElement) {
            super(trElement);
        }
    }
    
    public class ImageRowProcedure extends ImageRowDefault {
        public ImageRowProcedure(WebElement trElement) {
            super(trElement);
        }
    }
    
    public class ImageRowImage extends ImageRowDefault {
        public ImageRowImage(WebElement trElement) {
            super(trElement);
        }
    }
    
    public interface ImageRow {
        public String getAnnotationType();
        public String getAnnotationTerm();
        public String getAnnotationId();
        public String getAnnotationIdLink();
        public int getRelatedImageCount();
        public String getImagesLink();
    }
    
    public class ImageRowFactory {
        private ImageRow imageRow;
        private String annotationType = "";
        
        public ImageRowFactory(WebElement trElement) {
            List<WebElement> bodyRowElementList= trElement.findElements(By.cssSelector("td"));
            annotationType = bodyRowElementList.get(0).findElement(By.cssSelector("span.annotType")).getText();
            
            switch (annotationType) {
                case "Gene":
                case "MP":
                case "Anatomy":
                case "Procedure":
                case "Disease":
                case "Image":
                    imageRow = new ImageRowDefault(trElement);
                    break;
                    
                default:
                    throw new RuntimeException("SearchImageAnnotationView.ImageRowFactory.ImageRowFactory: Unknown annotation type '" + annotationType + "'");
            }
        }
            
        public ImageRow getImageRow() {
            return imageRow;
        }
    }

}