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
        
        // This validation gets called with paged data (e.g. only the rows showing in the displayed page)
        // and with all data (the data for all of the pages). As such, the only effective way to validate
        // it is to stuff the download data elements into a hash, then loop through the pageData rows
        // querying the downloadData hash for each value (then removing that value from the hash to handle duplicates).
        for (int i = 1; i < downloadData.length; i++) {     
            // Copy all but the pageHeading into the hash.
            String[] row = downloadData[i];
            downloadHash.put(row[DownloadSearchMapImagesAnnotationView.COL_INDEX_ANNOTATION_TERM], row);
        }
        
        for (ImageRow pageRow : bodyRows) {
            String[] downloadRow = downloadHash.get(pageRow.annotationTerm);
            if (downloadRow == null) {
                status.addError("IMAGE MISMATCH: page value annotationName = '" + pageRow.annotationTerm + "' was not found in the download file.");
                continue;
            }
            downloadHash.remove(pageRow.annotationTerm);
            
            // Verify the components.
            
            // annotationType.
            if ( ! pageRow.annotationType.equals(downloadRow[DownloadSearchMapImagesAnnotationView.COL_INDEX_ANNOTATION_TYPE]))
                status.addError("IMAGE MISMATCH for term " + pageRow.annotationTerm + ": page value annotationType = '"
                        + pageRow.annotationType + "' doesn't match download value '"
                        + downloadRow[DownloadSearchMapImagesAnnotationView.COL_INDEX_ANNOTATION_TYPE] + "'.");
            
            // annotationIdLink.
            if ( ! pageRow.annotationIdLink.equals(downloadRow[DownloadSearchMapImagesAnnotationView.COL_INDEX_ANNOTATION_ID_LINK]))
                status.addError("IMAGE MISMATCH for term " + pageRow.annotationTerm + ": page value annotationIdLink = '"
                        + pageRow.annotationIdLink + "' doesn't match download value '"
                        + downloadRow[DownloadSearchMapImagesAnnotationView.COL_INDEX_ANNOTATION_ID_LINK] + "'.");
            
            // annotationName.
            if ( ! pageRow.annotationTerm.equals(downloadRow[DownloadSearchMapImagesAnnotationView.COL_INDEX_ANNOTATION_TERM]))
                status.addError("IMAGE MISMATCH for term " + pageRow.annotationTerm + ": page value annotationName = '"
                        + pageRow.annotationTerm + "' doesn't match download value '"
                        + downloadRow[DownloadSearchMapImagesAnnotationView.COL_INDEX_ANNOTATION_TERM] + "'.");
            
            // relatedImageCount.
            if ( ! pageRow.relatedImageCount.equals(downloadRow[DownloadSearchMapImagesAnnotationView.COL_INDEX_RELATED_IMAGE_COUNT]))
                status.addError("IMAGE MISMATCH for term " + pageRow.annotationTerm + ": page value relatedImageCount = '"
                        + pageRow.relatedImageCount + "' doesn't match download value '"
                        + downloadRow[DownloadSearchMapImagesAnnotationView.COL_INDEX_RELATED_IMAGE_COUNT] + "'.");
            
            // imagesLink.
            if ( ! pageRow.imagesLink.equals(downloadRow[DownloadSearchMapImagesAnnotationView.COL_INDEX_IMAGES_LINK]))
                status.addError("IMAGE MISMATCH for term " + pageRow.annotationTerm + ": page value imagesLink = '"
                        + pageRow.imagesLink + "' doesn't match download value '"
                        + downloadRow[DownloadSearchMapImagesAnnotationView.COL_INDEX_IMAGES_LINK] + "'.");
        }

        return status;
    }
    
    
    // PRIVATE METHODS
    
    
    /**
     * Parse all of the Annotation tr rows.
     * 
     * annotationType, annotationId, annotationIdLink, annotationName, relatedImageCount, imagesLink
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
                 + "'  annotationId: '" + annotationId
                 + "'  annotationIdLink: '" + annotationIdLink
                 + "'  annotationName: '" + annotationTerm
                 + "'  relatedImageCount: '" + relatedImageCount
                 + "'  imagesLink: '" + imagesLink + "'";
        }
        
        public ImageRow(WebElement trElement) {
            List<WebElement> bodyRowElementList= trElement.findElements(By.cssSelector("td"));

            annotationType = bodyRowElementList.get(0).findElement(By.cssSelector("span.annotType")).getText(); // annotationType.

            List<WebElement> anchorElements = bodyRowElementList.get(0).findElements(By.cssSelector("a"));
            annotationIdLink = anchorElements.get(0).getAttribute("href");                                      // annotationLink.
            annotationIdLink = TestUtils.urlDecode(annotationIdLink);                                           //    Decode it.
            int pos = annotationIdLink.lastIndexOf("/");
            annotationId = annotationIdLink.substring(pos + 1).trim();                                          // annotationId.
            annotationTerm = anchorElements.get(0).getText();                                                   // annotationName.
            imagesLink = anchorElements.get(1).getAttribute("href");                                            // imagesLink.
            imagesLink = TestUtils.urlDecode(imagesLink);                                                       //    Decode it.
            relatedImageCount = anchorElements.get(1).getText().replace(" images", "");                         // relatedImageCount.
        }
    }

}