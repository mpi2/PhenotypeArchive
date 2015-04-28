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

import java.util.HashMap;
import uk.ac.ebi.phenotype.pojo.ObservationType;

/**
 *
 * @author mrelac
 *
 * This class encapsulates the code and data necessary to validate a
 * unidimensional graph.
 */
public class GraphValidatorUnidimensional extends GraphValidator {
    
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
    public final int DATA_POINT          = 22;
    
    public GraphValidatorUnidimensional() {
        super();
    }
    
    @Override
    public PageStatus validate() {
        PageStatus status = new PageStatus();
        
        status.add(super.validate());                                           // Validate common components.
        
        if (pageSection.getHeading().getObservationType() != ObservationType.unidimensional) {
            status.addError("ERROR: Expected unidimensional graph but found " + pageSection.getHeading().getObservationType().name());
        }
        
        // Verify title contains 'Allele'.
        if ( ! pageSection.getHeading().title.startsWith("Allele -")) {
            status.addError("ERROR: expected title to start with 'Allele -'. Title is '" + pageSection.getHeading().title + "'. URL: " + pageSection.graphUrl);
        }
        
        // Verify parameter name on graph matches that in the Parameter instance.
        String parameterObjectName = pageSection.getHeading().parameterObject.getName().trim();
        if (parameterObjectName.compareToIgnoreCase(pageSection.getHeading().parameterName) != 0) {
            status.addError("ERROR: parameter name mismatch. parameter on graph: '" 
                    + pageSection.getHeading().parameterName
                    + "'. From parameterObject: " + parameterObjectName
                    + ". URL: " + pageSection.graphUrl);
        }
        
        // Validate that the required HTML table 'globalTest' exists and is valid.
        GraphGlobalTestTable globalTestTable = pageSection.getGlobalTestTable();
        if (globalTestTable == null) {
            status.addError("ERROR: unidimensional graph has no globalTest table. URL: " + pageSection.graphUrl);
        } else {
            status.add(globalTestTable.validate());
        }
        
        // Validate that the required HTML table 'continuousTable' exists.
        if (pageSection.getContinuousTable() == null) {
            status.addError("ERROR: unidimensional graph has no continuousTable. URL: " + pageSection.graphUrl);
        }
        
        // Validate that there is a 'More statistics' link, click it and validate it.
        GraphSection.MoreStatisticsLink moreStatisticsLink = pageSection.getMoreStatisticsLink();
        if (moreStatisticsLink == null) {
            status.addError("ERROR: unidimensional graph expected 'More statistics' link. URL: " + pageSection.graphUrl);
        } else {
            status.add(moreStatisticsLink.validate());
        }
        
        status.add(validateDownload());                                         // Validate download streams.
        
        return status;
    }
    
    
    // PRIVATE METHODS
    
    
    /**
     * Validates what is displayed on the page with the TSV and XLS download
     * streams. Any errors are returned in a new <code>PageStatus</code> instance.
     * 
     * Unidimensional graphs need to test the following:
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
                String file = row[ALLELE_SYMBOL].toLowerCase().trim();
                String page = h.alleleSymbol.toLowerCase().trim();
                if ((! group.equals("control")) && (! file.equals(page)))
                    status.addError(downloadType + " allele symbol mismatch. Page: " + row[ALLELE_SYMBOL] + ". Download: " + h.alleleSymbol + ". URL: " + pageSection.graphUrl);
                
                file = row[GENETIC_BACKGROUND].toLowerCase().trim();
                page = h.geneticBackground.toLowerCase().trim();
                if ( ! file.equals(page))
                    status.addError(downloadType + " genetic background mismatch. Page: " + row[GENETIC_BACKGROUND] + ". Download: " + h.geneticBackground + ". URL: " + pageSection.graphUrl);
                
                file = row[GENE_SYMBOL].toLowerCase().trim();
                page = h.geneSymbol.toLowerCase().trim();
                if ((! group.equals("control")) && (! file.equals(page)))
                    status.addError(downloadType + " gene symbol mismatch. Page: " + row[GENE_SYMBOL] + ". Download: " + h.geneSymbol + ". URL: " + pageSection.graphUrl);
                
                file = row[METADATA_GROUP].toLowerCase().trim();
                page = h.metadataGroup.toLowerCase().trim();
                if ( ! file.equals(page))
                    status.addError(downloadType + " metadata group mismatch. Page: " + row[METADATA_GROUP] + ". Download: " + h.metadataGroup + ". URL: " + pageSection.graphUrl);
                
                file = row[PARAMETER_NAME].toLowerCase().trim();
                page = h.parameterName.toLowerCase().trim();
                if ( ! file.equals(page))
                    status.addError(downloadType + " parameter name mismatch. Page: " + row[PARAMETER_NAME] + ". Download: " + h.parameterName + ". URL: " + pageSection.graphUrl);
                
                file = row[PARAMETER_STABLE_ID].toLowerCase().trim();
                page = h.parameterStableId.toLowerCase().trim();
                if ( ! file.equals(page))
                    status.addError(downloadType + " parameter stable id mismatch. Page: " + row[PARAMETER_STABLE_ID] + ". Download: " + h.parameterStableId + ". URL: " + pageSection.graphUrl);
                
                file = row[PHENOTYPING_CENTER].toLowerCase().trim();
                page = h.phenotypingCenter.toLowerCase().trim();
                if ( ! file.equals(page))
                    status.addError(downloadType + " phenotyping center mismatch. Page: " + row[PHENOTYPING_CENTER] + ". Download: " + h.phenotypingCenter + ". URL: " + pageSection.graphUrl);
                
                file = row[PIPELINE_NAME].toLowerCase().trim();
                page = h.pipelineName.toLowerCase().trim();
                if ( ! file.equals(page)) {
                    if ( ! page.equals(IMPC_PIPELINE)) {                        // "IMPC Pipeline" is also allowed.
                        status.addError(downloadType + " pipeline name mismatch. Page: " + row[PIPELINE_NAME] + ". Download: " + h.pipelineName + ". URL: " + pageSection.graphUrl);
                    }
                }
            }
            
            status.add(validateDownloadCounts(downloadSection));
        }

        return status;
    }
    
    /**
     * Validates download counts against unidimensional graph page totals.
     * 
     * @param downloadData download data for one graph, including heading (as
     * the first line)
     * 
     * @return validation status
     * 
     */
    private PageStatus validateDownloadCounts(String[][] downloadData) {
        PageStatus status = new PageStatus();
        
        // key = "Control" or "Experimental". value is zygosity hash map.
        HashMap<String, HashMap<String, HashMap<String, Integer>>> groupHash = new HashMap();
        
        // Walk the download stream summing the counts.
        // Layout:      HashMap groupHash
        //                  "Control"
        //                  "Experimental"
        //                                  HashMap zygosity
        //                                      "Control"
        //                                      "Homozygote"
        //                                                      HashMap sex
        //                                                          "Female"
        //                                                          "Male"
        //                                                                          Integer
        
        // Skip over heading (first row). Also, sometimes there are extra blank lines at the end of the stream.
        // lowercase the hash keys on put and use lowercase when retrieving.
        int colCountFirstRow = 0;
        for (int i = 0; i < downloadData.length; i++) {
            String[] rawRow = downloadData[i];
            if (rawRow[0].equals("pipeline name")) {
                groupHash = new HashMap();
                colCountFirstRow = downloadData[i].length;                      // Save the column count, then check it each time. Skip rows with mismatched column counts.
            }
            
            if (downloadData[i].length != colCountFirstRow)
                continue;
            
            String zygosity = rawRow[ZYGOSITY].toLowerCase();
            String sex = rawRow[SEX].toLowerCase();
            String group = rawRow[GROUP].toLowerCase();
            
            if ( ! groupHash.containsKey(group)) {
                groupHash.put(group, new HashMap<String, HashMap<String, Integer>>());
            }
            HashMap<String, HashMap<String, Integer>> zygosityHash = groupHash.get(group);
            // If this is a control, set 'zygosity' (which is otherwise blank) to 'control'.
            if (group.toLowerCase().equals("control")) {
                zygosity = group.toLowerCase();
            }
            if ( ! zygosityHash.containsKey(zygosity)) {
                zygosityHash.put(zygosity, new HashMap<String, Integer>());
            }
            HashMap<String, Integer> sexHash = zygosityHash.get(zygosity);
            if ( ! sexHash.containsKey(sex)) {
                sexHash.put(sex, 0);
            }
            sexHash.put(sex, sexHash.get(sex) + 1);
        }
        
        return status;
    }
}