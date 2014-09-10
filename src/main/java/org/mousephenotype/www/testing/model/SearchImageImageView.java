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
 * components of a search page 'imagesGrid' HTML table (image view) for images.
 */
public class SearchImageImageView {
    private final List<ImageRow> bodyRows = new ArrayList();
    private final WebDriver      driver;
    private final int            timeoutInSeconds;
    
    public static final int COL_INDEX_NAME           = 0;
    public static final int COL_INDEX_IMAGE          = 1;
    
    public enum AnnotationType {
        Gene,
        MA,
        MP,
        Procedure
    }
    
    /**
     * Creates a new <code>SearchImageTable</code> instance.
     * @param driver A <code>WebDriver</code> instance pointing to the search
     * facet table with thead and tbody definitions.
     * @param timeoutInSeconds The <code>WebDriver</code> timeout, in seconds
     */
    public SearchImageImageView(WebDriver driver, int timeoutInSeconds) {
        this.driver = driver;
        this.timeoutInSeconds = timeoutInSeconds;
        
        parseBodyRows();
    }
    
    /**
     * Validates download data against this <code>SearchImageTableAnnotationView</code>
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
            "Annotation term"
          , "Annotation id"
          , "Annotation id link"
          , "Procedure"
          , "Gene symbol"
          , "Gene symbol link"
          , "Image link"
        };
        SearchFacetTable.validateDownloadHeading("IMAGES (Image view)", status, expectedHeadingList, downloadData[0]);

        for (int i = 1; i < downloadData.length - 1; i++) {                     // Loop for one row less to account for [already validated] heading row.
            String[] downloadRow = downloadData[i];
            ImageRow pageRow = bodyRows.get(i);
            
            // Verify the components. Drive from download file.
            // annotationTerm.
            for (int j = 0; j < downloadRow.length; j++) {
                String dnldTermCollection = downloadRow[j];
                String pageTermCollection = "";
                if (pageRow.maTerm != null)
                    pageTermCollection += pageRow.maTerm.toString();
                if (pageRow.mpTerm != null)
                    pageTermCollection += pageRow.mpTerm.toString();

                int mm = 17;
            }
            
        
        
        
            
////////            // annotationType.
////////            if ( ! pageRow.annotationType.equals(downloadRow[DownloadSearchMapImagesAnnotationView.COL_INDEX_ANNOTATION_TYPE]))
////////                status.addError("IMAGE MISMATCH for term " + pageRow.annotationName + ": page value annotationType = '"
////////                        + pageRow.annotationType + "' doesn't match download value '"
////////                        + downloadRow[DownloadSearchMapImagesAnnotationView.COL_INDEX_ANNOTATION_TYPE] + "'.");
////////            
////////            // annotationIdLink.
////////            if ( ! pageRow.annotationIdLink.equals(downloadRow[DownloadSearchMapImagesAnnotationView.COL_INDEX_ANNOTATION_ID_LINK]))
////////                status.addError("IMAGE MISMATCH for term " + pageRow.annotationName + ": page value annotationIdLink = '"
////////                        + pageRow.annotationIdLink + "' doesn't match download value '"
////////                        + downloadRow[DownloadSearchMapImagesAnnotationView.COL_INDEX_ANNOTATION_ID_LINK] + "'.");
////////            
////////            // annotationName.
////////            if ( ! pageRow.annotationName.equals(downloadRow[DownloadSearchMapImagesAnnotationView.COL_INDEX_ANNOTATION_NAME]))
////////                status.addError("IMAGE MISMATCH for term " + pageRow.annotationName + ": page value annotationName = '"
////////                        + pageRow.annotationName + "' doesn't match download value '"
////////                        + downloadRow[DownloadSearchMapImagesAnnotationView.COL_INDEX_ANNOTATION_NAME] + "'.");
////////            
////////            // relatedImageCount.
////////            if ( ! pageRow.relatedImageCount.equals(downloadRow[DownloadSearchMapImagesAnnotationView.COL_INDEX_RELATED_IMAGE_COUNT]))
////////                status.addError("IMAGE MISMATCH for term " + pageRow.annotationName + ": page value relatedImageCount = '"
////////                        + pageRow.relatedImageCount + "' doesn't match download value '"
////////                        + downloadRow[DownloadSearchMapImagesAnnotationView.COL_INDEX_RELATED_IMAGE_COUNT] + "'.");
////////            
////////            // imagesLink.
////////            if ( ! pageRow.imagesLink.equals(downloadRow[DownloadSearchMapImagesAnnotationView.COL_INDEX_IMAGES_LINK]))
////////                status.addError("IMAGE MISMATCH for term " + pageRow.annotationName + ": page value imagesLink = '"
////////                        + pageRow.imagesLink + "' doesn't match download value '"
////////                        + downloadRow[DownloadSearchMapImagesAnnotationView.COL_INDEX_IMAGES_LINK] + "'.");
////////            
////////            // synonyms collection.
////////            HashMap<String, String> downloadSynonymHash = new HashMap();
////////            String rawSynonymString = downloadRow[DownloadSearchMapAnatomy.COL_INDEX_ANATOMY_SYNONYMS];
////////            if ((rawSynonymString != null) && ( ! rawSynonymString.isEmpty())) {
////////                String[] downloadSynonyms = rawSynonymString.split(",");
////////                for (String downloadSynonym : downloadSynonyms) {
////////                    downloadSynonymHash.put(downloadSynonym.trim(), downloadSynonym.trim());
////////                }
////////            }
////////            for (String pageSynonym : pageRow.synonyms) {
////////                String downloadSynonym = downloadSynonymHash.get(pageSynonym);
////////                if (downloadSynonym == null) {
////////                    status.addError("ANATOMY MISMATCH for term " + pageRow.anatomyTerm + ": page value synonym = '" + pageSynonym + "' was not found in the download file.");
////////                }
////////                downloadSynonymHash.remove(downloadSynonym);
////////            }
        }

        return status;
    }
    
    
    // PRIVATE METHODS
    
    
    /**
     * Parse all of the Image tr rows.
     * 
     * annotationTerms, annotationIds, annotationIdLinks, procedures, geneSymbols, geneSymbolLinks, imageLink
     */
    private void parseBodyRows() {
        // Save the body values.
        List<WebElement> trElements = driver.findElements(By.xpath("//table[@id='imagesGrid']/tbody/tr"));
        if ( ! trElements.isEmpty()) {
            int index = 0;
            for (WebElement bodyRowElements : trElements) {
                ImageRow bodyRow = new ImageRow(bodyRowElements);
//System.out.println("bodyRowAnnotation[ " + index + " ]: " + bodyRow.toString());
                index++;
                bodyRows.add(bodyRow);
            }
        }
    }
    
    
    // PRIVATE CLASSES
    
    
    private class ImageRow {
        private AnnotationTerm geneTerm;
        private AnnotationTerm maTerm;
        private AnnotationTerm mpTerm;
        private AnnotationTerm procedureTerm;
        private String         imageLink;
        
        public ImageRow(WebElement trElement) {
            parse(trElement);
        }
        
        /**
         * For debugging: dump out the topElement [type and text] and its children [type and text]
         * @param s Identifying string prepended to the first line of the output.
         * @param topElement The element whose self and children are to be dumped
         */
        private void dumpElement(String s, WebElement topElement) {
            List<WebElement> elements = topElement.findElements(By.cssSelector("*"));
            System.out.println(s + ": Top element type: " + topElement.getTagName() + "(" + elements.size() + "). text = '" + topElement.getText() + "'. children:");
            int index = 0;
            for (WebElement element : elements) {
                System.out.println("\telement [" + index++ + "] type: " + element.getTagName() + ". text = '" + element.getText() + "'");
            }
            System.out.println();
        }
        
        private void parse(WebElement trElement) {
//dumpElement("trElement", trElement);
            // There may be as many imgAnnotsElements as there are subfacets under Images (i.e. Phenotype [MP], Anatomy [MA], Procedure, Gene)
            List<WebElement> imgAnnotsElements = trElement.findElements(By.cssSelector("td > span.imgAnnots"));
            for (WebElement imgAnnotsElement : imgAnnotsElements) {
// dumpElement("imgAnnotsElement", imgAnnotsElement);
                parseImageAnnots(imgAnnotsElement);
            }
            
            imageLink = trElement.findElements(By.cssSelector("td")).get(1).findElement(By.cssSelector("a")).getAttribute("href");
        }
    
        /**
         * Parses a single <code>span.imgAnnots</code> that does not contain embedded
         * </code>span.imgAnnots</code> elements.
         * 
         * @param imgAnnotsElement single element to parse
         */
        private void parseImageAnnots(WebElement imgAnnotsElement) {
            WebElement spanAnnotTypeElement = imgAnnotsElement.findElement(By.cssSelector("span.annotType"));
            
            // imgAnnots encapsulates all of the information in the Image view 'Name' column, currently:
            //      'MA', 'MP', 'Procedure', and 'Gene'.
            // annotTypes have, at a minimum, one term type and at least one term. Each
            // term may (but is not required to) have an 'a' with a 'href'. If there are
            // multiple values for a given term, they are wrapped in a <ul> tag. Examples:
            //
            // Case 1: SINGLE TERM, NO 'a'/'href':
            // <span.imgAnnots>
            //      <span class="annotType">Procedure</span>
            //      : Eye Morphology
            // </span.imgAnnots>
            //
            // Case 2: SINGLE TERM, 'a'/'href':
            // <span.imgAnnots>
            //      <span class="annotType">MA</span>
            //      :
            //      <a href="/data/anatomy/MA:0001910">snout</a>
            // </span.imgAnnots>
            //
            // Case 3: MULTIPLE 'a'/'href' terms:
            // <span.imgAnnots>
            //      <span class="annotType">MP</span>
            //      :
            //      <ul class="imgMp">
            //          <li>
            //              <a href="/data/phenotypes/MP:0000445">short snout</a>
            //          </li>
            //            .
            //            .
            //            .
            //      </ul>
            // </span.imgAnnots>
            String annotationType = spanAnnotTypeElement.getText().trim();            // term type, as String.
            List<WebElement> anchorElements = imgAnnotsElement.findElements(By.cssSelector("a"));
            AnnotationTerm annotationTerm = new AnnotationTerm(annotationType);
            if (anchorElements.size() > 0) {
                for (WebElement anchorElement : anchorElements) {
                    AnnotationDetail annotationDetail = new AnnotationDetail(anchorElement.getText().trim());
                    annotationDetail.link = anchorElement.getAttribute("href");
                    annotationDetail.link = TestUtils.urlDecode(annotationDetail.link);                         // Decode the link.
                    int pos = annotationDetail.link.lastIndexOf("/");
                    annotationDetail.id = annotationDetail.link.substring(pos + 1).trim();
                    annotationTerm.termDetails.add(annotationDetail);
                }
            } else {
                // There are no anchor elements. This is the simplest case where
                // there is only a term after the ":"; no 'a', no 'href'
                AnnotationDetail annotationDetail = new AnnotationDetail(imgAnnotsElement.getText().split(":")[1].trim());
                annotationTerm.termDetails.add(annotationDetail);
            }
            
            switch (annotationTerm.termType) {
                case Gene:
                    geneTerm = annotationTerm;
                    break;
                    
                case MA:
                    maTerm = annotationTerm;
                    break;
                    
                case MP:
                    mpTerm = annotationTerm;
                    break;
                        
                case Procedure:
                    procedureTerm = annotationTerm;
                    break;
            }
        }
        
        @Override
        public String toString() {
            String retVal = "";
            if (mpTerm != null)
                retVal += "mpTerm: '" + mpTerm.toString() + "'. ";
            if (maTerm != null)
                retVal += "maTerm: '" + maTerm.toString() + "'. ";
            if (procedureTerm != null)
                retVal += "procedure: '" + procedureTerm + "'. ";
            if (geneTerm != null)
                retVal += "geneTerm: '" + geneTerm.toString() + "'.";

            return retVal;
        }
    }
    
    /**
     * Encapsulates term type and list of <code>AnnotationDetail</code>.
     */
    private class AnnotationTerm {
        private AnnotationType         termType    = null;  
        private List<AnnotationDetail> termDetails = new ArrayList();
        
        public AnnotationTerm(String termType) {
            switch (termType.trim().toLowerCase()) {
                case "gene":
                    this.termType = AnnotationType.Gene;
                    break;
                    
                case "ma":
                    this.termType = AnnotationType.MA;
                    break;
                    
                case "mp":
                    this.termType = AnnotationType.MP;
                    break;
                    
                case "procedure":
                    this.termType = AnnotationType.Procedure;
                    break;
                    
                default:
                    throw new RuntimeException("ERROR: SearchImageTable.AnnotationTerm.AnnotationTerm(): Unsupported termType '" + termType + "'.");
            }
        }
        
        @Override
        public String toString() {
            String retVal = "";
            for (int i = 0; i < termDetails.size(); i++) {
                AnnotationDetail detail = termDetails.get(i);
                if (i > 0)
                    retVal += "|";
                retVal += termType + ":" + detail.term;
            }
            
            return retVal;
        }
    }
    
    /**
     * AnnotationDetail describes each Gene, MA, MP, or Procedure entry.
     */
    private class AnnotationDetail {
        private String term;
        private String id;
        private String link;
        
        public AnnotationDetail(String term) {
            this(term, "", "");
        }
        public AnnotationDetail(String term, String id, String link) {
            this.term = term;
            this.id = id;
            this.link = link;
        }
        
        @Override
        public String toString() {
            return term;
        }
    }
    
}