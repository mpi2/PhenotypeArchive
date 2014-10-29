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
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import uk.ac.ebi.phenotype.util.Utils;

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
            String pageValue = pageRow.getAnnotationType();
            String downloadValue = downloadRow[DownloadSearchMapImagesAnnotationView.COL_INDEX_ANNOTATION_TYPE];
//System.out.println("[" + i + "][0]: pageValue:     '" + pageValue + "'");
//System.out.println("[" + i + "][0]: downloadValue: '" + downloadValue + "'\n");
            if ( ! TestUtils.pageEqualsDownload(pageValue, downloadValue)) {
                status.addError("IMAGE MISMATCH for term " + pageRow.getAnnotationTerm() + ": page value annotationType = '"
                        + pageValue + "' doesn't match download value '" + downloadValue + "'.");
            }
            
            // annotationTerm.
            pageValue = pageRow.getAnnotationTerm();
            downloadValue = downloadRow[DownloadSearchMapImagesAnnotationView.COL_INDEX_ANNOTATION_TERM];
//System.out.println("[" + i + "][1]: pageValue:     '" + pageValue + "'");
//System.out.println("[" + i + "][1]: downloadValue: '" + downloadValue + "'\n");
            if ( ! TestUtils.pageEqualsDownload(pageValue, downloadValue)) {
                status.addError("IMAGE MISMATCH for term " + pageRow.getAnnotationTerm() + ": page value annotationTerm = '"
                        + pageValue + "' doesn't match download value '" + downloadValue + "'.");
            }
            
            // annotationId.
            pageValue = pageRow.getAnnotationId();
            downloadValue = downloadRow[DownloadSearchMapImagesAnnotationView.COL_INDEX_ANNOTATION_ID];
//System.out.println("[" + i + "][2]: pageValue:     '" + pageValue + "'");
//System.out.println("[" + i + "][2]: downloadValue: '" + downloadValue + "'\n");
            if ( ! TestUtils.pageEqualsDownload(pageValue, downloadValue)) {
                status.addError("IMAGE MISMATCH for term " + pageRow.getAnnotationTerm() + ": page value annotationId = '"
                        + pageValue + "' doesn't match download value '" + downloadValue + "'.");
            }
            
            // annotationIdLink.
            pageValue = pageRow.getAnnotationIdLink();
            downloadValue = downloadRow[DownloadSearchMapImagesAnnotationView.COL_INDEX_ANNOTATION_ID_LINK];
//System.out.println("[" + i + "][3]: pageValue:     '" + pageValue + "'");
//System.out.println("[" + i + "][3]: downloadValue: '" + downloadValue + "'\n");
            if ( ! TestUtils.pageEqualsDownload(pageValue, downloadValue)) {
                status.addError("IMAGE MISMATCH for term " + pageRow.getAnnotationTerm() + ": page value annotationIdLink = '"
                        + pageValue + "' doesn't match download value '" + downloadValue + "'.");
            }
            
            // relatedImageCount.
            pageValue = Integer.toString(pageRow.getRelatedImageCount());
            downloadValue = downloadRow[DownloadSearchMapImagesAnnotationView.COL_INDEX_RELATED_IMAGE_COUNT];
//System.out.println("[" + i + "][4]: pageValue:     '" + pageValue + "'");
//System.out.println("[" + i + "][4]: downloadValue: '" + downloadValue + "'\n");
            if ( ! TestUtils.pageEqualsDownload(pageValue, downloadValue)) {
                status.addError("IMAGE MISMATCH for term " + pageRow.getAnnotationTerm() + ": page value relatedImageCount = '"
                        + pageValue + "' doesn't match download value '" + downloadValue + "'.");
            }
                
            // imagesLink.
            pageValue = pageRow.getImagesLink();
            downloadValue = downloadRow[DownloadSearchMapImagesAnnotationView.COL_INDEX_IMAGES_LINK];
//System.out.println("[" + i + "][5]: pageValue:     '" + pageValue + "'");
//System.out.println("[" + i + "][5]: downloadValue: '" + downloadValue + "'\n\n");
            if ( ! TestUtils.pageEqualsDownload(pageValue, downloadValue)) {
                status.addError("IMAGE MISMATCH for term " + pageRow.getAnnotationTerm() + ": page value imagesLink = '"
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
                ImageRow bodyRow = new ImageRowFactory(trElement).getImageRow();
//System.out.println("bodyRowAnnotation[ " + index + " ]: " + bodyRow.toString() + "\n");
                index++;
                bodyRows.add(bodyRow);
            }
        }
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
                                                                            // [0]: 'Procedure'      [1]: 'Dysmorphology (1 image)'
            annotationType = line1FirstPart[0].trim();
            
            String[] line1SecondPart = line1FirstPart[1].split("\\(");      // [0]: 'Pus71 '         [1]: '1 image)'    or
                                                                            // [0]: 'Dysmorphology ' [1]: '1 image)'
            annotationTerm = line1SecondPart[0].trim();
            int spacePos = line1SecondPart[1].indexOf(" ");
            String sImageCount = line1SecondPart[1].substring(0, spacePos);
            Integer imageCount = Utils.tryParseInt(sImageCount);
            if (imageCount == null) {
                message = "ERROR: SearchImageAnnotationView.ImageRowDefault.ImageRowDefault(): Couldn't find image count.";
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
        private String annotationType     = "";
        
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