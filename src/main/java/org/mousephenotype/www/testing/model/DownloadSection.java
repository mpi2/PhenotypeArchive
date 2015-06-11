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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.mousephenotype.www.testing.exception.GraphTestException;
import uk.ac.ebi.phenotype.chart.ChartType;

/**
 * A download stream can consist of one or more sections, each delimited by a
 * row of column headings that (for now) begin with the string "pipeline name".
 * 
 * This class encapsulates the code and data necessary to represent such a
 * single section.
 * 
 * @author mrelac
 */
public class DownloadSection {
    
    protected Map<TestUtils.DownloadType, List<List<String>>> dataBlockMap = new HashMap();
    
    public DownloadSection() {
        
    }
    
    /**
     * Creates a new instance initialised with <code>downloadType</code> and
     * <code>data</code>
     * 
     * @param dataBlockMap a map keyed by download type containing the data to be added
     */
    public DownloadSection(Map<TestUtils.DownloadType, List<List<String>>> dataBlockMap) {
        this.dataBlockMap = dataBlockMap;
    }
    
        // Download column offsets.
    public static final int PIPELINE_NAME_COL_INDEX       =  0;
    public static final int PIPELINE_STABLE_ID_COL_INDEX  =  1;
    public static final int PROCEDURE_STABLE_ID_COL_INDEX =  2;
    public static final int PROCEDURE_NAME_COL_INDEX      =  3;
    public static final int PARAMETER_STABLE_ID_COL_INDEX =  4;
    public static final int PARAMETER_NAME_COL_INDEX      =  5;
    public static final int STRAIN_ACCESSION_ID_COL_INDEX =  6;
    public static final int STRAIN_COL_INDEX              =  7;
    public static final int GENETIC_BACKGROUND_COL_INDEX  =  8;
    public static final int GENE_SYMBOL_COL_INDEX         =  9;
    public static final int GENE_ACCESSION_COL_INDEX      = 10;
    public static final int ALLELE_SYMBOL_COL_INDEX       = 11;
    public static final int ALLELE_ACCESSION_COL_INDEX    = 12;
    public static final int PHENOTYPING_CENTER_COL_INDEX  = 13;
    public static final int COLONY_ID_COL_INDEX           = 14;
    public static final int DATE_OF_EXPERIMENT_COL_INDEX  = 15;
    public static final int ZYGOSITY_COL_INDEX            = 16;
    public static final int SEX_COL_INDEX                 = 17;
    public static final int GROUP_COL_INDEX               = 18;
    public static final int EXTERNAL_SAMPLE_ID_COL_INDEX  = 19;
    public static final int METADATA_COL_INDEX            = 20;
    public static final int METADATA_GROUP_COL_INDEX      = 21;
    
    // MUTANT key column indexes.
    public static final int[] DOWNLOAD_MUTANT_KEYS_CATEGORICAL_COLUMN_INDEXES = {
          ALLELE_SYMBOL_COL_INDEX
        , GENETIC_BACKGROUND_COL_INDEX
        , GENE_SYMBOL_COL_INDEX
        , METADATA_GROUP_COL_INDEX
        , PARAMETER_NAME_COL_INDEX
        , PARAMETER_STABLE_ID_COL_INDEX
        , PHENOTYPING_CENTER_COL_INDEX
        , PIPELINE_NAME_COL_INDEX
    };
    public static final int[] DOWNLOAD_MUTANT_KEYS_PIE_COLUMN_INDEXES = {
    };
    public static final int[] DOWNLOAD_MUTANT_KEYS_TIME_SERIES_LINE_COLUMN_INDEXES = {
          ALLELE_SYMBOL_COL_INDEX
        , GENETIC_BACKGROUND_COL_INDEX
        , GENE_SYMBOL_COL_INDEX
        , METADATA_GROUP_COL_INDEX
        , PHENOTYPING_CENTER_COL_INDEX
        , PIPELINE_NAME_COL_INDEX
    };
    public static final int[] DOWNLOAD_MUTANT_KEYS_ABR_COLUMN_INDEXES = {
          ALLELE_SYMBOL_COL_INDEX
        , GENETIC_BACKGROUND_COL_INDEX
        , GENE_SYMBOL_COL_INDEX
        , METADATA_GROUP_COL_INDEX
        , PHENOTYPING_CENTER_COL_INDEX
        , PIPELINE_NAME_COL_INDEX
        , PROCEDURE_NAME_COL_INDEX
    };
    public static final int[] DOWNLOAD_MUTANT_KEYS_UNIDIMENSIONAL_COLUMN_INDEXES = {
          ALLELE_SYMBOL_COL_INDEX
        , GENETIC_BACKGROUND_COL_INDEX
        , GENE_SYMBOL_COL_INDEX
        , METADATA_GROUP_COL_INDEX
        , PARAMETER_NAME_COL_INDEX
        , PARAMETER_STABLE_ID_COL_INDEX
        , PHENOTYPING_CENTER_COL_INDEX
        , PIPELINE_NAME_COL_INDEX
    };
    
    // CONTROL key column indexes.
    // If this is a control, don't check the pipeline name. It can be anything.
    
    public static final int[] DOWNLOAD_CONTROL_KEYS_CATEGORICAL_COLUMN_INDEXES = {
          GENETIC_BACKGROUND_COL_INDEX
        , METADATA_GROUP_COL_INDEX
        , PARAMETER_NAME_COL_INDEX
        , PARAMETER_STABLE_ID_COL_INDEX
        , PHENOTYPING_CENTER_COL_INDEX
    };
    public static final int[] DOWNLOAD_CONTROL_KEYS_PIE_COLUMN_INDEXES = {
    };
    public static final int[] DOWNLOAD_CONTROL_KEYS_TIME_SERIES_LINE_COLUMN_INDEXES = {
          GENETIC_BACKGROUND_COL_INDEX
        , METADATA_GROUP_COL_INDEX
        , PHENOTYPING_CENTER_COL_INDEX
    };
    public static final int[] DOWNLOAD_CONTROL_KEYS_ABR_COLUMN_INDEXES = {
          GENETIC_BACKGROUND_COL_INDEX
        , METADATA_GROUP_COL_INDEX
        , PHENOTYPING_CENTER_COL_INDEX
        , PROCEDURE_NAME_COL_INDEX
    };
    public static final int[] DOWNLOAD_CONTROL_KEYS_UNIDIMENSIONAL_COLUMN_INDEXES = {
          GENETIC_BACKGROUND_COL_INDEX
        , METADATA_GROUP_COL_INDEX
        , PARAMETER_NAME_COL_INDEX
        , PARAMETER_STABLE_ID_COL_INDEX
        , PHENOTYPING_CENTER_COL_INDEX
    };
    
    /**
     * Builds and returns a set of all [non-heading] mutant and control rows
     * using the fields from the defined chart type.
     * 
     * @param chartType chart type whose fields are used to build the mutant
     *                  and control keys
     * @param downloadType the download type
     * 
     * @return a set of all [non-heading] mutant and control rows using the
     * fields from the defined chart type and download type.
     * 
     * @throws GraphTestException for unknown chart types
     */
    public final Set<String> getKeys(ChartType chartType, TestUtils.DownloadType downloadType)  throws GraphTestException {
        Set<String> retVal = new HashSet();
        
        // Build a set of mutant and control data from the input parameters.
        TestUtils.ExperimentGroup group = TestUtils.ExperimentGroup.MUTANT;
        for (List<String> row : dataBlockMap.get(downloadType)) {
            retVal.add(makeKey(chartType, group, row));
        }
        group = TestUtils.ExperimentGroup.CONTROL;
        for (List<String> row : dataBlockMap.get(downloadType)) {
            retVal.add(makeKey(chartType, group, row));
        }
        
        return retVal;
    }
    
    
    // PRIVATE METHODS
    
    
    private String makeKey(ChartType chartType, TestUtils.ExperimentGroup group, List<String> row) throws GraphTestException {
        int[] columnIndexes = new int[0];
        
        switch (chartType) {
            case CATEGORICAL_STACKED_COLUMN:
                switch (group) {
                    case CONTROL:
                        columnIndexes = DOWNLOAD_CONTROL_KEYS_CATEGORICAL_COLUMN_INDEXES;
                        break;
                        
                    case MUTANT:
                        columnIndexes = DOWNLOAD_MUTANT_KEYS_CATEGORICAL_COLUMN_INDEXES;
                        break;
                }
                break;
                
            case PIE:
                break;
                
            case PREQC:
                break;
                
            case TIME_SERIES_LINE:
            case TIME_SERIES_LINE_BODYWEIGHT:
                switch (group) {
                    case CONTROL:
                        columnIndexes = DOWNLOAD_CONTROL_KEYS_TIME_SERIES_LINE_COLUMN_INDEXES;
                        break;
                        
                    case MUTANT:
                        columnIndexes = DOWNLOAD_MUTANT_KEYS_TIME_SERIES_LINE_COLUMN_INDEXES;
                        break;
                }
                break;
                
            case UNIDIMENSIONAL_ABR_PLOT:
                switch (group) {
                    case CONTROL:
                        columnIndexes = DOWNLOAD_CONTROL_KEYS_ABR_COLUMN_INDEXES;
                        break;
                        
                    case MUTANT:
                        columnIndexes = DOWNLOAD_MUTANT_KEYS_ABR_COLUMN_INDEXES;
                        break;
                }
                break;
                
            case UNIDIMENSIONAL_BOX_PLOT:
            case UNIDIMENSIONAL_SCATTER_PLOT:
                switch (group) {
                    case CONTROL:
                        columnIndexes = DOWNLOAD_CONTROL_KEYS_UNIDIMENSIONAL_COLUMN_INDEXES;
                        break;
                        
                    case MUTANT:
                        columnIndexes = DOWNLOAD_MUTANT_KEYS_UNIDIMENSIONAL_COLUMN_INDEXES;
                        break;
                }
                break;
                
            default:
                throw new GraphTestException("Unknown chart type " + chartType);
        }
        
        return TestUtils.makeKey(columnIndexes, row);
    }
}