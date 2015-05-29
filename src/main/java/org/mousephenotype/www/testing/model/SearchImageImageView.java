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

/**
 *
 * @author mrelac
 * 
 * This class encapsulates the code and data necessary to represent the important
 * components of a search page 'imagesGrid' HTML table (image view) for images.
 */
public class SearchImageImageView extends SearchFacetTable {
    
    public final static int COL_INDEX_GENE_TERMS      =  0;
    public final static int COL_INDEX_MA_TERMS        =  1;
    public final static int COL_INDEX_MP_TERMS        =  2;
    public final static int COL_INDEX_PROCEDURE_TERMS =  3;
    
    public final static int COL_INDEX_GENE_IDS        =  4;
    public final static int COL_INDEX_MA_IDS          =  5;
    public final static int COL_INDEX_MP_IDS          =  6;
    public final static int COL_INDEX_PROCEDURE_IDS   =  7;
    
    public final static int COL_INDEX_GENE_LINKS      =  8;
    public final static int COL_INDEX_MA_LINKS        =  9;
    public final static int COL_INDEX_MP_LINKS        = 10;
    public final static int COL_INDEX_PROCEDURE_LINKS = 11;
    
    public final static int COL_INDEX_IMAGE_LINK      = 12;
    public static final int COL_INDEX_LAST = COL_INDEX_IMAGE_LINK;              // Should always point to the last (highest-numbered) index.
    
    private final List<ImageRow> bodyRows = new ArrayList();
    private final GridMap pageData;
    private Map<TableComponent, By> map;
    
    public enum AnnotationType {
        Gene,
        MA,
        MP,
        Procedure
    }
    
    public enum ComponentType {
        Id,
        Link,
        Term
    }
    
    /**
     * Creates a new <code>SearchImageTable</code> instance.
     * 
     * @param driver A <code>WebDriver</code> instance pointing to the search
     * facet table with thead and tbody definitions.
     * @param timeoutInSeconds The <code>WebDriver</code> timeout, in seconds
     * @param map a map of HTML table-related definitions, keyed by <code>
     * TableComponent</code>.
     */
    public SearchImageImageView(WebDriver driver, int timeoutInSeconds, Map<TableComponent, By> map) {
        super(driver, timeoutInSeconds, map);
        this.map = map;
        
        pageData = load();
    }
    
    /**
     * Validates download data against this <code>SearchImageTableAnnotationView</code>
     * instance.
     * 
     * @param downloadDataArray The download data used for comparison
     * @return validation status
     */
    @Override
    public PageStatus validateDownload(String[][] downloadDataArray) {
        final Integer[] pageColumns = {
              COL_INDEX_PROCEDURE_TERMS
            , COL_INDEX_GENE_TERMS
            , COL_INDEX_GENE_LINKS
            , COL_INDEX_MA_TERMS
            , COL_INDEX_MA_LINKS
            , COL_INDEX_IMAGE_LINK
        };
        final Integer[] downloadColumns = {
              DownloadSearchMapImagesImageView.COL_INDEX_PROCEDURE
            , DownloadSearchMapImagesImageView.COL_INDEX_GENE_SYMBOL
            , DownloadSearchMapImagesImageView.COL_INDEX_GENE_SYMBOL_LINK
            , DownloadSearchMapImagesImageView.COL_INDEX_MA_TERM
            , DownloadSearchMapImagesImageView.COL_INDEX_MA_TERM_LINK
            , DownloadSearchMapImagesImageView.COL_INDEX_IMAGE_LINK
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
            
            pageArray[sourceRowIndex][COL_INDEX_PROCEDURE_TERMS] = "";         // Insure there is always a non-null value.
            pageArray[sourceRowIndex][COL_INDEX_GENE_TERMS] = "";
            pageArray[sourceRowIndex][COL_INDEX_GENE_LINKS] = "";
            pageArray[sourceRowIndex][COL_INDEX_MA_TERMS] = "";
            pageArray[sourceRowIndex][COL_INDEX_MA_LINKS] = "";
            pageArray[sourceRowIndex][COL_INDEX_IMAGE_LINK] = "";
            for (WebElement bodyRowElements : bodyRowElementsList) {
                ImageRow bodyRow = new ImageRow(bodyRowElements);
                
                pageArray[sourceRowIndex][COL_INDEX_PROCEDURE_TERMS] = bodyRow.toStringDetailList(bodyRow.procedureDetails, ComponentType.Term).replace("[", "").replace("]", "");
                pageArray[sourceRowIndex][COL_INDEX_GENE_TERMS] = bodyRow.toStringDetailList(bodyRow.geneDetails, ComponentType.Term).replace("[", "").replace("]", "");
                pageArray[sourceRowIndex][COL_INDEX_GENE_LINKS] = bodyRow.toStringDetailList(bodyRow.geneDetails, ComponentType.Link).replace("[", "").replace("]", "");
                pageArray[sourceRowIndex][COL_INDEX_MA_TERMS] = bodyRow.toStringDetailList(bodyRow.maDetails, ComponentType.Term).replace("[", "").replace("]", "");
                pageArray[sourceRowIndex][COL_INDEX_MA_LINKS] = bodyRow.toStringDetailList(bodyRow.maDetails, ComponentType.Link).replace("[", "").replace("]", "");
                pageArray[sourceRowIndex][COL_INDEX_IMAGE_LINK] = bodyRow.getImageLink();
                
                sourceRowIndex++;
                bodyRows.add(bodyRow);
            }
        }
        
        return new GridMap(pageArray, driver.getCurrentUrl());
    }
    
    
    // PRIVATE CLASSES
    
    
    /**
     * AnnotationDetail describes each Gene, MA, MP, or Procedure entry.
     */
    private class AnnotationDetail {
        private String id;
        private String link;
        private String term;
        
        public AnnotationDetail(String term) {
            this(term, "", "");
        }
        public AnnotationDetail(String term, String id, String link) {
            this.id = id;
            this.link = link;
            this.term = term;
        }

        @Override
        public String toString() {
            return "AnnotationDetail{" + "id=" + id + ", link=" + link + ", term=" + term + '}';
        }
        
        public String toString(ComponentType componentType) {
            String retVal = "";
            
            switch (componentType) {
                case Id:
                    if (id != null)
                        retVal = id;
                    break;
                    
                case Link:
                    if (link != null)
                        retVal = link;
                    break;
                    
                case Term:
                    if (term != null)
                        retVal = term;
                    break;
            }
            
            return retVal;
        }
    }
    
    /**
     * This class encapsulates the code and data representing a single search
     * page [image facet, Image view] image row.
     */
    private class ImageRow {
        private final List<AnnotationDetail> geneDetails = new ArrayList();
        private final List<AnnotationDetail> maDetails = new ArrayList();
        private final List<AnnotationDetail> mpDetails = new ArrayList();
        private final List<AnnotationDetail> procedureDetails = new ArrayList();
        private String                       imageLink;
        
        public ImageRow(WebElement trElement) {
            parse(trElement);
        }

        public String getImageLink() {
            return imageLink;
        }

        @Override
        public String toString() {
            String retVal = "ImageRow{";
            
            retVal += "geneDetails={";
            if (geneDetails != null) {
                retVal += toStringDetailList(geneDetails);
            }
            retVal += "} ";
            
            retVal += "maDetails={";
            if (maDetails != null) {
                retVal += toStringDetailList(maDetails);
            }
            retVal += "} ";
            
            retVal += "mpDetails={";
            if (mpDetails != null) {
                retVal += toStringDetailList(mpDetails);
            }
            retVal += "} ";
            
            retVal += "procedureDetails={";
            if (procedureDetails != null) {
                retVal += toStringDetailList(procedureDetails);
            }
            retVal += "}";
            
            return retVal;
        }
        
        public String toString(AnnotationType annotationType, ComponentType componentType) {
            switch (annotationType) {
                case Gene:
                    return toStringDetailList(geneDetails, componentType);
                case MA:
                    return toStringDetailList(maDetails, componentType);
                case MP:
                    return toStringDetailList(mpDetails, componentType);
                case Procedure:
                    return toStringDetailList(procedureDetails, componentType);
            }
            
            return "";
        }
        
        
        // PRIVATE METHODS
        
        
        private String toStringDetailList(List<AnnotationDetail> detailList) {
            String retVal = "";
        
            for (int i = 0; i < detailList.size(); i++) {
                if (i > 0)
                    retVal += "|";
            
                AnnotationDetail detail = detailList.get(i);
                retVal += "[" + detail.toString() + "]";
            }
            
            return retVal;
        }
        
        private String toStringDetailList(List<AnnotationDetail> detailList, ComponentType componentType) {
            String retVal = "";
        
            for (int i = 0; i < detailList.size(); i++) {
                if (i > 0)
                    retVal += "|";
            
                AnnotationDetail detail = detailList.get(i);
                retVal += "[" + detail.toString(componentType) + "]";
            }
            
            return retVal;
        }
        
        /**
         * For debugging: dump out the topElement [type and text] and its children [type and text]
         * @param s Identifying string prepended to the first line of the output.
         * @param topElement The element whose self and children are to be dumped
         */
//        private void dumpElement(String s, WebElement topElement) {
//            List<WebElement> elements = topElement.findElements(By.cssSelector("*"));
//            System.out.println(s + ": Top element type: " + topElement.getTagName() + "(" + elements.size() + "). text = '" + topElement.getText() + "'. children:");
//            int index = 0;
//            for (WebElement element : elements) {
//                System.out.println("\telement [" + index++ + "] type: " + element.getTagName() + ". text = '" + element.getText() + "'");
//            }
//            System.out.println();
//        }
        
        private void parse(WebElement trElement) {
//dumpElement("trElement", trElement);
            // There may be as many imgAnnotsElements as there are subfacets under Images (i.e. Phenotype [MP], Anatomy [MA], Procedure, Gene)
            List<WebElement> imgAnnotsElements = trElement.findElements(By.cssSelector("td > span.imgAnnots"));
            for (WebElement imgAnnotsElement : imgAnnotsElements) {
// dumpElement("imgAnnotsElement", imgAnnotsElement);
                parseImageAnnots(imgAnnotsElement);
            }
            
            imageLink = trElement.findElements(By.cssSelector("td")).get(1).findElement(By.cssSelector("a")).getAttribute("fullres");
            imageLink = TestUtils.urlDecode(imageLink);                         // Decode the link.
            imageLink = TestUtils.setProtocol(imageLink, TestUtils.HTTP_PROTOCOL.http); // remap protocol to http to facilitate match.
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
            List<WebElement> anchorElements = imgAnnotsElement.findElements(By.cssSelector("a"));
            AnnotationType annotationType = AnnotationType.valueOf(spanAnnotTypeElement.getText().trim());
            if (anchorElements.size() > 0) {
                for (WebElement anchorElement : anchorElements) {
                    AnnotationDetail annotationDetail = new AnnotationDetail(anchorElement.getText().trim());
                    annotationDetail.link = anchorElement.getAttribute("href");
                    annotationDetail.link = TestUtils.urlDecode(annotationDetail.link);                         // Decode the link.
                    int pos = annotationDetail.link.lastIndexOf("/");
                    annotationDetail.id = annotationDetail.link.substring(pos + 1).trim();
                    addAnnotationDetail(annotationType, annotationDetail);
                }
            } else {
                // There are no anchor elements. This is the simplest case where
                // there is only a term after the ":"; no 'a', no 'href'
                addAnnotationDetail(annotationType, new AnnotationDetail(imgAnnotsElement.getText().split(":")[1].trim()));
            }
        }
        
        private void addAnnotationDetail(AnnotationType annotationType, AnnotationDetail annotationDetail) {
            switch (annotationType) {
                case Gene:
                    geneDetails.add(annotationDetail);
                    break;

                case MA:
                    maDetails.add(annotationDetail);
                    break;

                case MP:
                    mpDetails.add(annotationDetail);
                    break;

                case Procedure:
                    procedureDetails.add(annotationDetail);
                    break;
            }
        }
    }
}