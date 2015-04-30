/**
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
/**
 * Copyright © 2015 EMBL - European Bioinformatics Institute
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

import org.mousephenotype.www.testing.exception.GraphTestException;

/**
 *
 * @author mrelac
 * 
 * This class encapsulates the code and data necessary to validate an ABR
 * graph.
 */
public class GraphValidatorABR extends GraphValidator {
    
    // Download column offsets.
    public final int PIPELINE_NAME       =  0;
    public final int PIPELINE_STABLE_ID  =  1;
    public final int PROCEDURE_STABLE_ID =  2;
    public final int PROCEDURE_NAME      =  3;
    public final int PARAMETER_STABLE_ID =  4;
    public final int PARAMETER_NAME      =  5;
    public final int STRAIN_ACCESSION_ID =  6;
    public final int STRAIN              =  7;
    public final int GENETIC_BACKGROUND  =  8;
    public final int GENE_SYMBOL         =  9;
    public final int GENE_ACCESSION      = 10;
    public final int ALLELE_SYMBOL       = 11;
    public final int ALLELE_ACCESSION    = 12;
    public final int PHENOTYPING_CENTER  = 13;
    public final int COLONY_ID           = 14;
    public final int DATE_OF_EXPERIMENT  = 15;
    public final int ZYGOSITY            = 16;
    public final int SEX                 = 17;
    public final int GROUP               = 18;
    public final int EXTERNAL_SAMPLE_ID  = 19;
    public final int METADATA            = 20;
    public final int METADATA_GROUP      = 21;
    public final int CATEGORY            = 22;
    
    public GraphValidatorABR() {
        super();
    }
    
    @Override
    public PageStatus validate() throws GraphTestException {
        PageStatus status = new PageStatus();
        
        status.add(super.validate());                                           // Validate common components.
        
        // Verify title contains 'Allele'.
        if ( ! pageSection.getHeading().title.startsWith("Allele -")) {
            status.addError("ERROR: expected title to start with 'Allele -'. Title is '" + pageSection.getHeading().title + "'. URL: " + pageSection.graphUrl);
        }
        
        status.add(validateDownload());                                         // Validate download streams.
        
        return status;
    }
    

    // PRIVATE METHODS
    
    
    /**
     * Validates what is displayed on the page with the TSV and XLS download
     * streams. Any errors are returned in a new <code>PageStatus</code> instance.
     * 
     * Categorical graphs need to test the following:
     * <ul><li>that the TSV and XLS links create a download stream</li>
     * <li>that the graph page parameters, such as <code>pipeline name</code>,
     * <code>pipelineStableId</code>, <code>parameterName</code>, etc. match</li>
     * <li>that the <code>catTable</code> HTML table counts match the sum of the
     * requisite values in the download stream</li></ul>
     * 
     * @return validation results
     */
     private PageStatus validateDownload() {
        PageStatus status = new PageStatus();
        String message;
        
        GraphHeading h = pageSection.getHeading();
        
        for (TestUtils.DownloadType downloadType : pageSection.getDownloadDataSection().keySet()) {      // tsv / xls
            String[][] downloadSection = pageSection.getDownloadDataSection().get(downloadType);
            for (int i = 1; i < downloadSection.length; i++) {                  // Skip over first [heading] row by starting at 1.
                String[] row = downloadSection[i];
                String group = downloadSection[i][GROUP];
                String file = row[ALLELE_SYMBOL].toLowerCase().trim();
                String page = h.alleleSymbol.toLowerCase().trim();
                if ((! group.equals("control")) && (! file.equals(page)))
                    status.addError(downloadType + " allele symbol mismatch. Download: " + row[ALLELE_SYMBOL] + ". Page: " + h.alleleSymbol + ". URL: " + pageSection.graphUrl);
                
                file = row[GENETIC_BACKGROUND].toLowerCase().trim();
                page = h.geneticBackground.toLowerCase().trim();
                if ( ! file.equals(page))
                    status.addError(downloadType + " genetic background mismatch. Download: " + row[GENETIC_BACKGROUND] + ". Page: " + h.geneticBackground + ". URL: " + pageSection.graphUrl);
                
                file = row[GENE_SYMBOL].toLowerCase().trim();
                page = h.geneSymbol.toLowerCase().trim();
                if ((! group.equals("control")) && (! file.equals(page)))
                    status.addError(downloadType + " gene symbol mismatch. Download: " + row[GENE_SYMBOL] + ". Page: " + h.geneSymbol + ". URL: " + pageSection.graphUrl);

                file = row[PHENOTYPING_CENTER].toLowerCase().trim();
                page = h.phenotypingCenter.toLowerCase().trim();
                if ( ! file.equals(page))
                    status.addError(downloadType + " phenotyping center mismatch. Download: " + row[PHENOTYPING_CENTER] + ". Page: " + h.phenotypingCenter + ". URL: " + pageSection.graphUrl);
                
                // If this is a control, don't check the pipeline name. It can be anything.
                if ( ! row[GROUP].toLowerCase().equals("control")) {
                    file = row[PIPELINE_NAME].toLowerCase().trim();
                    page = h.pipelineName.toLowerCase().trim();
                    if ( ! file.equals(page)) {
                        status.addError(downloadType + " pipeline name mismatch. Download: " + row[PIPELINE_NAME] + ". Page: " + h.pipelineName + ". URL: " + pageSection.graphUrl);
                    }
                }

                file = row[PROCEDURE_NAME].toLowerCase().trim();
                if (h.procedureName == null) {
                    message = downloadType + " procedure name is NULL. Download procedure name: " + row[PROCEDURE_NAME] + ". URL: " + pageSection.graphUrl;
                    if ( ! status.getErrorMessages().contains(message)) {       // Only write the error message once.
                        status.addError(message);
                    }
                } else {
                    page = h.procedureName.toLowerCase().trim();
                    if ( ! file.equals(page))
                        status.addError(downloadType + " procedure name mismatch. Download: " + row[PROCEDURE_NAME] + ". Page: " + h.procedureName + ". URL: " + pageSection.graphUrl);
                }
            }
        }
        
        return status;
    }
}