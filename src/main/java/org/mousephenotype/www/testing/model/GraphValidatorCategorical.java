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
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import org.mousephenotype.www.testing.exception.GraphTestException;
import uk.ac.ebi.phenotype.pojo.ObservationType;
import uk.ac.ebi.phenotype.util.Utils;

/**
 *
 * @author mrelac
 * 
 * This class encapsulates the code and data necessary to validate a categorical
 * graph.
 */
public class GraphValidatorCategorical extends GraphValidator {
    
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
    
    public GraphValidatorCategorical() {
        super();
    }
    
    @Override
    public PageStatus validate() throws GraphTestException {
        PageStatus status = new PageStatus();
        
        status.add(super.validate());                                           // Validate common components.
        
        if (pageSection.getHeading().getObservationType() != ObservationType.categorical) {
            status.addError("ERROR: Expected categorical graph but found " + pageSection.getHeading().getObservationType().name());
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
        
        // Validate that the required HTML table 'catTable' exists.
        if (pageSection.getCatTable() == null) {
            status.addError("ERROR: categorical graph has no catTable. URL: " + pageSection.graphUrl);
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
                    if ( ! file.equals(IMPC_PIPELINE)) {                        // "IMPC Pipeline" is also allowed.
                        status.addError(downloadType + " pipeline name mismatch. Page: " + row[PIPELINE_NAME] + ". Download: " + h.pipelineName + ". URL: " + pageSection.graphUrl);
                    }
                }
            }
            
            status.add(validateDownloadCounts(pageSection.getCatTable(), downloadSection));
        }

        return status;
    }
    
    /**
     * Validates download counts against categorical graph section totals.
     *
     * @param catTable a single graph's catTable
     * @param downloadData download data segment for this catTable, including
     * heading
     * 
     * @return validation status
     * 
     */
    private PageStatus validateDownloadCounts(GraphCatTable catTable, String[][] downloadData) {
        PageStatus status = new PageStatus();
        
        // key = "Control" or "Experimental". value is zygosity hash map.
        HashMap<String, HashMap<String, HashMap<String, HashMap<String, Integer>>>> groupHash = new HashMap();
        
        // Walk the download stream summing the counts.
        // Layout:      HashMap groupHash
        //                  "Control"
        //                  "Experimental"
        //                                  HashMap zygosity
        //                                      "Control"
        //                                      "Homozygote"
        //                                                      HashMap category
        //                                                          "Absent"
        //                                                          "Present"
        //                                                                          HashMap sex
        //                                                                              "Female"
        //                                                                              "Male"
        //                                                                                          Integer
        
        // Skip over heading (first row). Also, sometimes there are extra blank lines at the end of the stream.
        // lowercase the hash keys on put and use lowercase when retrieving.
        int colCountFirstRow = 0;
        for (int i = 1; i < downloadData.length; i++) {
            if (i == 1)
                colCountFirstRow = downloadData[i].length;                      // Save the column count, then check it each time. Skip rows with mismatched column counts.
            if (downloadData[i].length != colCountFirstRow)
                continue;
            
            String[] row = downloadData[i];
            
            String zygosity = row[ZYGOSITY].toLowerCase();
            String sex = row[SEX].toLowerCase();
            String group = row[GROUP].toLowerCase();
            String category = row[CATEGORY].toLowerCase();
            
            if ( ! groupHash.containsKey(group)) {
                groupHash.put(group, new HashMap<String, HashMap<String, HashMap<String, Integer>>>());
            }
            HashMap<String, HashMap<String, HashMap<String, Integer>>> zygosityHash = groupHash.get(group);
            // If this is a control, set 'zygosity' (which is otherwise blank) to 'control'.
            if (group.toLowerCase().equals("control")) {
                zygosity = group.toLowerCase();
            }
            if ( ! zygosityHash.containsKey(zygosity)) {
                zygosityHash.put(zygosity, new HashMap<String, HashMap<String, Integer>>());
            }
            HashMap<String, HashMap<String, Integer>> categoryHash = zygosityHash.get(zygosity);
            if ( ! categoryHash.containsKey(category)) {
                categoryHash.put(category, new HashMap<String, Integer>());
            }
            HashMap<String, Integer> sexHash = categoryHash.get(category);
            if ( ! sexHash.containsKey(sex)) {
                sexHash.put(sex, 0);
            }
            sexHash.put(sex, sexHash.get(sex) + 1);
        }
        
        // We now have all the counts. Compare them against the page values.
        List<GraphCatTable.Row> rows = catTable.getBodyRowsList();
        for (GraphCatTable.Row row : rows) {                                    // For all of the Control/Hom/Het rows in catTable ...
            Iterator<Entry<String, String>> categoryIt = row.getCategoryHash().entrySet().iterator();
            while (categoryIt.hasNext()) {                                      // ... For all of the categories ...
                Entry<String, String> entry = categoryIt.next();
                Integer pageValue = Utils.tryParseInt(entry.getValue());
                
                // If this is a control, set 'zygosity' (which is otherwise blank) to 'control'.
                String zygosityKey = (row.group == GraphCatTable.Group.CONTROL ? row.group.toString().toLowerCase() : row.zygosity.toLowerCase());

                // Sometimes some of these components are null. Wrap this in a try block.
                Integer downloadValue = null;
                try {
                    downloadValue = groupHash
                            .get(row.group.toString().toLowerCase())
                            .get(zygosityKey)
                            .get(entry.getKey().toLowerCase())
                            .get(row.sex.toString().toLowerCase());
                } catch (Exception e) { }
                downloadValue = (downloadValue == null ? 0 : downloadValue);    // 0 count values on the page have no hash entry (i.e. returned hash value is null).
                if ( ! pageValue.equals(downloadValue)) {
                    status.addError("ERROR: validating " + row.group.toString() + "." + row.zygosity + "." + entry.getKey() + "." + row.sex.toString() + ": " +
                            "page value = '" + pageValue + "'. download value = '" + downloadValue + "'.");
                }
            }
        }
        
        return status;
    }
}