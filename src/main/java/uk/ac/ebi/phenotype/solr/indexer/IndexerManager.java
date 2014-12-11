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

package uk.ac.ebi.phenotype.solr.indexer;

import java.util.Arrays;
import java.util.List;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static uk.ac.ebi.phenotype.solr.indexer.AbstractIndexer.CONTEXT_ARG;

/**
 * This class encapsulates the code and data necessary to represent an index
 * manager that manages the creation and deployment of all of the indexes
 * required for phenotype archive; a job previously delegated to Jenkins that
 * was susceptible to frequent failure.
 * 
 * @author mrelac
 */
public class IndexerManager {
    private static final Logger logger = LoggerFactory.getLogger(IndexerManager.class);
    
    // core names.
    public static final String HUMAN_2_MOUSE_CORE = "human2mouse_symbol";
    public static final String ALLELE_CORE = "allele";
    public static final String IMAGES_CORE = "images";
    public static final String MP_CORE = "mp";
    public static final String MA_CORE = "ma";
    public static final String PIPELINE_CORE = "pipeline";
    public static final String GENE_CORE = "gene";
    public static final String GENOTYPE_PHENOTYPE_CORE = "genotype-phenotype";
    public static final String EXPERIMENT_CORE = "experiment";
    public static final String DISEASE_CORE = "disease";
    public static final String STATSTICAL_RESULT_CORE = "statistical-result";
    
    // main return values.
    public static final int STATUS_OK = 0;
    public static final int STATUS_NO_CONTEXT = 1;
    
    private Boolean nodeps;
    private List<String> cores;
    public static final String[] allCoresArray = new String[] {      // In dependency order.
          HUMAN_2_MOUSE_CORE
        , ALLELE_CORE
        , IMAGES_CORE
        , MP_CORE
        , MA_CORE
        , PIPELINE_CORE
        , GENE_CORE
        , GENOTYPE_PHENOTYPE_CORE
        , EXPERIMENT_CORE
        , DISEASE_CORE
        , STATSTICAL_RESULT_CORE
    };
    private final List<String> allCores = Arrays.asList(allCoresArray);
    
    public static final String NO_DEPS_ARG = "nodeps";
    public static final String CORES_ARG = "cores";
    public static final String HELP_ARG = "help";
    
    public IndexerManager() {
        
    }
    
    
    // GETTERS

    
    public static Logger getLogger() {
        return logger;
    }

    public Boolean getNodeps() {
        return nodeps;
    }

    public List<String> getCores() {
        return cores;
    }
    
    
    // PUBLIC METHODS
    
    
    public void initialise(String[] args) throws IndexerException {
        parseCommandLine(args);
    }

    public void run() throws IndexerException {
        logger.info("Starting IndexerManager...");

    }
    
    
    // PRIVATE METHODS
    
    
    /**
     * Rules:
     * 1. context is always required.
     * 2. cores is optional.
     * 2a. If cores is missing or empty:
     * - build all cores.
     * - specifying nodeps throws IllegalArgumentException.
     * 2b. If 1 core:
     * - core parameter is required and must reference a valid core name.
     * - build specified core
     * - if nodeps is specified:
     *   - don't build downstream cores.
     * - else
     *   - build all downstream cores.
     * 2c. If more than 1 core:
     * - each core must reference a valid core name. No downstream cores are built.
     * - if nodeps is specified, it is ignored.
     *
     * nodeps Truth Table:
     *     cores=           nodeps on cmd line   nodeps value returned
     *    |-------------------------------------------------------|
     *    | NOT SPECIFIED | NOT SPECIFIED      | false            |
     *    | OR EMPTY      | SPECIFIED          | IndexerException |
     *    |-------------------------------------------------------|
     *    |     1         | NOT SPECIFIED      | false            |
     *    |               | SPECIFIED          | true             |
     *    |-------------------------------------------------------|
     *    |    +1         | NOT SPECIFIED      | true             |
     *    |               | SPECIFIED          | true             |
     *    |-------------------------------------------------------|
     *
     * @param args command-line arguments
     * @return<code>OptionSet</code> of parsed parameters
     * @throws IndexerException 
     */
    private OptionSet parseCommandLine(String[] args) throws IndexerException {
        OptionParser parser = new OptionParser();
        OptionSet options = null;
        
        // Spring context file parameter [required]
        parser.accepts(CONTEXT_ARG)
                .withRequiredArg()
                .required()
                .ofType(String.class)
                .describedAs("Spring context file, such as 'index-app-config.xml'");
        
        parser.accepts(CORES_ARG)
                .withOptionalArg()
                .ofType(String.class)
                .describedAs("A list of cores, in build order.");
        parser.accepts(NO_DEPS_ARG);
        parser.accepts(HELP_ARG)
                .forHelp();
        
        // Validate the parameters.
        try {
            boolean coreMissingOrEmpty = false;
            options = parser.parse(args);
            
            // Cores parameter list (may be empty)
            String[] coresArray;
            if (options.has(CORES_ARG)) {
                String s = (String)options.valueOf((CORES_ARG));
                if ((s == null) || (s.trim().isEmpty())) {
                    // Cores list is empty.
                    coreMissingOrEmpty = true;
                    coresArray = allCoresArray;
                } else if ( ! s.contains(",")) {
                    coresArray = new String[] { s };
                } else {
                    coresArray = ((String)options.valueOf(CORES_ARG)).split(",");
                }
            } else {
                // Cores list is missing.
                coreMissingOrEmpty = true;
                coresArray = allCoresArray;
            }
                
            if (coreMissingOrEmpty) {
                // Default behavior is to build all cores using dependencies.
                if (options.has(NO_DEPS_ARG)) {
                    throw new IndexerException("Invalid argument 'nodeps' specified with empty core list.");
                }
            } else {
                // Core(s) specified. Verify that each core name exists.
                for (String core : coresArray) {
                    if ( ! allCores.contains(core)) {
                        throw new IndexerException("Invalid core name '" + core + "'");
                    }
                }
            }
            
            cores = Arrays.asList(coresArray);
            
            // Determine nodeps behaviour.
            if (coreMissingOrEmpty) {
                nodeps = false;
            } else if (cores.size() == 1) {
                nodeps = options.has(NO_DEPS_ARG);
                if (nodeps == null)
                    nodeps = false;
            } else {
                nodeps = true;
            }
        } catch (Exception uoe) {
            if (args.length < 1) {
                System.out.println("Expected required context file parameter, such as 'index-app-config.xml'.");
            }
            try { parser.printHelpOn(System.out); } catch (Exception e) {}
            throw new IndexerException(uoe);
        }

        return options;
    }
    
    
    
    public static int main(String[] args) {
        try {
            IndexerManager manager = new IndexerManager();
            manager.initialise(args);
            manager.run();
            logger.info("IndexerManager process finished successfully.  Exiting.");
        } catch (IndexerException ie) {
             if (ie.getCause().getClass().getName().equals("joptsimple.MissingRequiredOptionsException")) {
                 return STATUS_NO_CONTEXT;
             }
        }
        
        return STATUS_OK;
    }
}
