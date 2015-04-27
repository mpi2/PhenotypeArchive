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
    public PageStatus validate() {
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
        
        GraphHeading h = pageSection.getHeading();
        
        for (TestUtils.DownloadType downloadType : pageSection.getDownloadDataSection().keySet()) {      // tsv / xls
            String[][] downloadSection = pageSection.getDownloadDataSection().get(downloadType);
            for (int i = 1; i < downloadSection.length; i++) {                  // Skip over first [heading] row by starting at 1.
                String[] row = downloadSection[i];
                String group = downloadSection[i][GROUP];
                String page = row[ALLELE_SYMBOL].toLowerCase().trim();
                String file = h.alleleSymbol.toLowerCase().trim();
                if ((! group.equals("control")) && (! page.equals(file)))
                    status.addError(downloadType + " allele symbol mismatch. Page: " + row[ALLELE_SYMBOL] + ". Download: " + h.alleleSymbol + ". URL: " + pageSection.graphUrl);
                
                page = row[GENETIC_BACKGROUND].toLowerCase().trim();
                file = h.geneticBackground.toLowerCase().trim();
                if ( ! page.equals(file))
                    status.addError(downloadType + " genetic background mismatch. Page: " + row[GENETIC_BACKGROUND] + ". Download: " + h.geneticBackground + ". URL: " + pageSection.graphUrl);
                
                page = row[GENE_SYMBOL].toLowerCase().trim();
                file = h.geneSymbol.toLowerCase().trim();
                if ((! group.equals("control")) && (! page.equals(file)))
                    status.addError(downloadType + " gene symbol mismatch. Page: " + row[GENE_SYMBOL] + ". Download: " + h.geneSymbol + ". URL: " + pageSection.graphUrl);

                page = row[PHENOTYPING_CENTER].toLowerCase().trim();
                file = h.phenotypingCenter.toLowerCase().trim();
                if ( ! page.equals(file))
                    status.addError(downloadType + " phenotyping center mismatch. Page: " + row[PHENOTYPING_CENTER] + ". Download: " + h.phenotypingCenter + ". URL: " + pageSection.graphUrl);
                
                page = row[PIPELINE_NAME].toLowerCase().trim();
                file = h.pipelineName.toLowerCase().trim();
                if ( ! page.equals(file)) {
                    if ( ! file.equals(IMPC_PIPELINE)) {                        // "IMPC Pipeline" is also allowed.
                        status.addError(downloadType + " pipeline name mismatch. Page: " + row[PIPELINE_NAME] + ". Download: " + h.pipelineName + ". URL: " + pageSection.graphUrl);
                    }
                }
            }
        }

        return status;
    }
}