/*******************************************************************************
 * Copyright 2015 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 *******************************************************************************/

package org.mousephenotype.www.testing.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import org.mousephenotype.www.testing.exception.GraphTestException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import uk.ac.ebi.phenotype.dao.PhenotypePipelineDAO;
import uk.ac.ebi.phenotype.pojo.ObservationType;
import uk.ac.ebi.phenotype.util.Utils;

/**
 *
 * @author mrelac
 * 
 * This class encapsulates the code and data necessary to validate a categorical
 * graph section.
 */
public class GraphSectionCategorical extends GraphSection {
    
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

    /**
     * Creates a new <code>GraphSectionCategorical</code> instance
     * 
     * @param driver <code>WebDriver</code> instance
     * @param wait <code>WebDriverWait</code> instance
     * @param phenotypePipelineDAO <code>PhenotypePipelineDAO</code> instance
     * @param graphUrl the graph url
     * @param chartElement <code>WebElement</code> pointing to the HTML
     *                     div.chart element of the categorical chart section.
     * 
     * @throws GraphTestException
     */
    public GraphSectionCategorical(WebDriver driver, WebDriverWait wait, PhenotypePipelineDAO phenotypePipelineDAO, String graphUrl, WebElement chartElement) throws GraphTestException {
        super(driver, wait, phenotypePipelineDAO, graphUrl, chartElement);
    }
    
    @Override
    public PageStatus validate() throws GraphTestException {
        PageStatus status = super.validate();                                   // Validate common components.
        
        if (getHeading().getObservationType() != ObservationType.categorical) {
            status.addError("ERROR: Expected categorical graph but found " + getHeading().getObservationType().name());
        }
        
        // Verify parameter name on graph matches that in the Parameter instance.
        String parameterObjectName = getHeading().parameterObject.getName().trim();
        if (parameterObjectName.compareToIgnoreCase(getHeading().parameterName) != 0) {
            status.addError("ERROR: parameter name mismatch. parameter on graph: '" 
                    + getHeading().parameterName
                    + "'. From parameterObject: " + parameterObjectName
                    + ". URL: " + graphUrl);
        }
        
        // Validate that the required HTML table 'catTable' exists.
        if (getCatTable() == null) {
            status.addError("ERROR: categorical graph has no catTable. URL: " + graphUrl);
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
        GraphHeading heading = getHeading();
        
        // For all download types in the map, walk each download section, using
        // the key defined by the group. When found, add the rows to a set.
        Set<String> keySet = new HashSet();
        
        for (List<List<String>> block : downloadSection.dataBlockMap.values()) {
            for (List<String> row : block) {
                if (GraphPage.isHeading(row))                                   // Skip headings.
                    continue;
                
                String group = row.get(GROUP);
                switch (group) {
                    case "control":
                        keySet.add(TestUtils.makeKey(DownloadSection.DOWNLOAD_CONTROL_KEYS_CATEGORICAL_COLUMN_INDEXES, row));
                        break;
                        
                    default:
                        keySet.add(TestUtils.makeKey(DownloadSection.DOWNLOAD_MUTANT_KEYS_CATEGORICAL_COLUMN_INDEXES, row));
                        break;
                }
            }
            
            status.add(validateDownloadCounts(getCatTable(), block));
        }
        
        // Remove the control and mutant keys from the set. If the set is empty,
        // validation succeeds; otherwise, validation fails.
        keySet.remove(heading.controlKey);
        keySet.remove(heading.mutantKey);
        
        if (! keySet.isEmpty()) {
            status.addError("Key mismatch. URL: " + graphUrl
                          + "\ncontrolKey = " + heading.controlKey
                          + "\nmutantKey  = " + heading.mutantKey
                          + "\nset        = " + TestUtils.dumpSet(keySet));
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
    private PageStatus validateDownloadCounts(GraphCatTable catTable, List<List<String>> downloadDataBlock) {
        PageStatus status = new PageStatus();
        String[][] downloadData = TestUtils.listToArray(downloadDataBlock);
        
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