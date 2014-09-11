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
import java.util.HashMap;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 *
 * @author mrelac
 * 
 * This class encapsulates the code and data necessary to represent the important
 * components of a search page 'imagesGrid' HTML table (annotation view) for images.
 */
public class SearchImageAnnotationView {
    private final List<ImageRow> bodyRows = new ArrayList();
    private final WebDriver      driver;
    private final int            timeoutInSeconds;
    
    public static final int COL_INDEX_NAME           = 0;
    public static final int COL_INDEX_EXAMPLE_IMAGES = 1;
    
    
    /**
     * Creates a new <code>SearchImageTable</code> instance.
     * @param driver A <code>WebDriver</code> instance pointing to the search
     * facet table with thead and tbody definitions.
     * @param timeoutInSeconds The <code>WebDriver</code> timeout, in seconds
     */
    public SearchImageAnnotationView(WebDriver driver, int timeoutInSeconds) {
        this.driver = driver;
        this.timeoutInSeconds = timeoutInSeconds;
        
        parseBodyRows();
    }

    /**
     * Validates download data against this <code>SearchImageAnnotationView</code>
     * instance.
     * 
     * @param downloadData The download data used for comparison
     * @return validation status
     * annotationType, annotationTerm, annotationId, annotationIdLink, relatedImageCount, imagesLink
     */
    public PageStatus validateDownload(String[][] downloadData) {
        PageStatus status = new PageStatus();
        HashMap<String, String[]> downloadHash = new HashMap();
        
        if ((bodyRows.isEmpty()) || (downloadData.length == 0))
            return status;
            
        // Validate the pageHeading.
        String[] expectedHeadingList = {
            "Annotation type"
          , "Annotation term"
          , "Annotation id"
          , "Annotation id link"
          , "Related image count"
          , "Images link"
        };
        SearchFacetTable.validateDownloadHeading("IMAGE (annotation view)", status, expectedHeadingList, downloadData[0]);

        for (int i = 0; i < bodyRows.size(); i++) {
            String[] downloadRow = downloadData[i + 1];                         // Skip over heading row.
            ImageRow pageRow = bodyRows.get(i);
            
            // Verify the components.
            
            // annotationType.
            String pageValue = pageRow.annotationType;
            String downloadValue = downloadRow[DownloadSearchMapImagesAnnotationView.COL_INDEX_ANNOTATION_TYPE];
            if ( ! TestUtils.pageEqualsDownload(pageValue, downloadValue)) {
                status.addError("IMAGE MISMATCH for term " + pageRow.annotationTerm + ": page value annotationType = '"
                        + pageValue + "' doesn't match download value '" + downloadValue + "'.");
            }
            
            // annotationTerm.
            pageValue = pageRow.annotationTerm;
            downloadValue = downloadRow[DownloadSearchMapImagesAnnotationView.COL_INDEX_ANNOTATION_TERM];
            if ( ! TestUtils.pageEqualsDownload(pageValue, downloadValue)) {
                status.addError("IMAGE MISMATCH for term " + pageRow.annotationTerm + ": page value annotationTerm = '"
                        + pageValue + "' doesn't match download value '" + downloadValue + "'.");
            }
            
            // annotationId.
            pageValue = pageRow.annotationId;
            downloadValue = downloadRow[DownloadSearchMapImagesAnnotationView.COL_INDEX_ANNOTATION_ID];
            if ( ! TestUtils.pageEqualsDownload(pageValue, downloadValue)) {
                status.addError("IMAGE MISMATCH for term " + pageRow.annotationTerm + ": page value annotationId = '"
                        + pageValue + "' doesn't match download value '" + downloadValue + "'.");
            }
            
            // annotationIdLink.
            pageValue = pageRow.annotationIdLink;
            downloadValue = downloadRow[DownloadSearchMapImagesAnnotationView.COL_INDEX_ANNOTATION_ID_LINK];
            if ( ! TestUtils.pageEqualsDownload(pageValue, downloadValue)) {
                status.addError("IMAGE MISMATCH for term " + pageRow.annotationTerm + ": page value annotationIdLink = '"
                        + pageValue + "' doesn't match download value '" + downloadValue + "'.");
            }
            
            // relatedImageCount.
            pageValue = pageRow.relatedImageCount;
            downloadValue = downloadRow[DownloadSearchMapImagesAnnotationView.COL_INDEX_RELATED_IMAGE_COUNT];
            if ( ! TestUtils.pageEqualsDownload(pageValue, downloadValue)) {
                status.addError("IMAGE MISMATCH for term " + pageRow.annotationTerm + ": page value relatedImageCount = '"
                        + pageValue + "' doesn't match download value '" + downloadValue + "'.");
            }
                
            // imagesLink.
            pageValue = pageRow.imagesLink;
            downloadValue = downloadRow[DownloadSearchMapImagesAnnotationView.COL_INDEX_IMAGES_LINK];
            if ( ! TestUtils.pageEqualsDownload(pageValue, downloadValue)) {
                status.addError("IMAGE MISMATCH for term " + pageRow.annotationTerm + ": page value imagesLink = '"
                        + pageValue + "' doesn't match download value '" + downloadValue + "'.");
            }
        }

        return status;
    }
    
    
    // PRIVATE METHODS
    
    
    /**
     * Parse all of the Annotation tr rows.
     * 
     * annotationType, annotationTerm, annotationId, annotationIdLink, relatedImageCount, imagesLink
     */
    private void parseBodyRows() {
        // Save the body values.
        List<WebElement> trElements = driver.findElements(By.xpath("//table[@id='imagesGrid']/tbody/tr"));
        if ( ! trElements.isEmpty()) {
            int index = 0;
            for (WebElement trElement : trElements) {
                ImageRow bodyRow = new ImageRow(trElement);
//System.out.println("bodyRowAnnotation[ " + index + " ]: " + bodyRow.toString());
                index++;
                bodyRows.add(bodyRow);
            }
        }
    }
    
    
    // PRIVATE CLASSES
    
    
    private class ImageRow {
        private String annotationType     = "";
        private String annotationTerm     = "";
        private String annotationId       = "";
        private String annotationIdLink   = "";
        private String relatedImageCount  = "";
        private String imagesLink         = "";
        
        @Override
        public String toString() {
            return "annotationType: '" + annotationType
                 + "'  annotationTerm: '" + annotationTerm
                 + "'  annotationId: '" + annotationId
                 + "'  annotationIdLink: '" + annotationIdLink
                 + "'  annotationName: '" + annotationTerm
                 + "'  relatedImageCount: '" + relatedImageCount
                 + "'  imagesLink: '" + imagesLink + "'";
        }
        
        public ImageRow(WebElement trElement) {
            List<WebElement> bodyRowElementList= trElement.findElements(By.cssSelector("td"));

            List<WebElement> anchorElements = bodyRowElementList.get(0).findElements(By.cssSelector("a"));
            annotationType = bodyRowElementList.get(0).findElement(By.cssSelector("span.annotType")).getText(); // annotationType.
            annotationTerm = anchorElements.get(0).getText();                                                   // annotationTerm.
            annotationIdLink = anchorElements.get(0).getAttribute("href");                                      // annotationLink.
            annotationIdLink = TestUtils.urlDecode(annotationIdLink);                                           //    Decode it.
            int pos = annotationIdLink.lastIndexOf("/");
            annotationId = annotationIdLink.substring(pos + 1).trim();                                          // annotationId.
            relatedImageCount = anchorElements.get(1).getText().replace(" images", "");                         // relatedImageCount.
            imagesLink = anchorElements.get(1).getAttribute("href");                                            // imagesLink.
            imagesLink = TestUtils.urlDecode(imagesLink);                                                       //    Decode it.
        }
    }

}