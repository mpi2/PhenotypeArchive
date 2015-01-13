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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.apache.commons.lang3.StringUtils;
import org.mousephenotype.www.testing.model.TestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
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
    //      These are built only for a new data release.
    public static final String OBSERVATION_CORE = "experiment";                 // For historic reasons, the core's actual name is 'experiment'.
    public static final String GENOTYPE_PHENOTYPE_CORE = "genotype-phenotype";
    public static final String STATSTICAL_RESULT_CORE = "statistical-result";
    
    //      These are built daily.
    public static final String PREQC_CORE = "preqc";
    public static final String ALLELE_CORE = "allele";
    public static final String IMAGES_CORE = "images";
    public static final String IMPC_IMAGES_CORE = "impc_images";
    public static final String MP_CORE = "mp";
    public static final String MA_CORE = "ma";
    public static final String PIPELINE_CORE = "pipeline";
    public static final String GENE_CORE = "gene";
    public static final String DISEASE_CORE = "disease";
    public static final String AUTOSUGGEST_CORE = "autosuggest";
    
    // main return values.
    public static final int STATUS_OK                = 0;
    public static final int STATUS_NO_CONTEXT        = 1;
    public static final int STATUS_NO_DEPS           = 2;
    public static final int STATUS_INVALID_CORE_NAME = 3;
    public static final int STATUS_VALIDATION_ERROR  = 4;
    
    public static String getStatusCodeName(int statusCode) {
        switch (statusCode) {
            case STATUS_OK:                 return "STATUS_OK";
            case STATUS_NO_CONTEXT:         return "STATUS_NO_CONTEXT";
            case STATUS_NO_DEPS:            return "STATUS_NO_DEPS";
            case STATUS_INVALID_CORE_NAME:  return "STATUS_INVALID_CORE_NAME";
            case STATUS_VALIDATION_ERROR:   return "STATUS_VALIDATION_ERROR";
            default:                        return "Unknown status code " + statusCode;
        }
    }
    
    // These are the args passed to the individual indexers. They should be all the same and should be the same context argument passed to the indexerManager.
    private String[] indexerArgs;
    
    private Boolean nodeps;
    private List<String> cores;
    public static final String[] allCoresArray = new String[] {      // In dependency order.
          // In dependency order. These are built only for a new data release.
          OBSERVATION_CORE
        , GENOTYPE_PHENOTYPE_CORE
        , STATSTICAL_RESULT_CORE
            
          // These are built daily.
        , PREQC_CORE
        , ALLELE_CORE
        , IMAGES_CORE
        , IMPC_IMAGES_CORE
        , MP_CORE
        , MA_CORE
        , PIPELINE_CORE
        , GENE_CORE
        , DISEASE_CORE
        , AUTOSUGGEST_CORE
    };
    private final List<String> allCoresList = Arrays.asList(allCoresArray);
    
    public static final String[] allDailyCoresArray = new String[] {      
          // In dependency order. These are built daily.
          PREQC_CORE
        , ALLELE_CORE
        , IMAGES_CORE
        , IMPC_IMAGES_CORE
        , MP_CORE
        , MA_CORE
        , PIPELINE_CORE
        , GENE_CORE
        , DISEASE_CORE
        , AUTOSUGGEST_CORE
    };
    
    public static final int RETRY_COUNT = 5;                                    // If any core fails, retry building it up to this many times.
    public static final int RETRY_SLEEP_IN_MS = 60000;                             // If any core fails, sleep this long before reattempting to build the core.
    
    @Autowired
    ObservationIndexer observationIndexer;
        
    @Autowired
    GenotypePhenotypeIndexer genotypePhenotypeIndexer;
    
    @Autowired
    StatisticalResultIndexer statisticalResultIndexer;
    
    @Autowired
    PreqcIndexer preqcIndexer;
        
    @Autowired
    AlleleIndexer alleleIndexer;
        
    @Autowired
    SangerImagesIndexer imagesIndexer;
        
    @Autowired
    ImpcImagesIndexer impcImagesIndexer;
        
    @Autowired
    MPIndexer mpIndexer;
        
    @Autowired
    MAIndexer maIndexer;
        
    @Autowired
    PipelineIndexer pipelineIndexer;
        
    @Autowired
    GeneIndexer geneIndexer;
        
    @Autowired
    DiseaseIndexer diseaseIndexer;
    
    @Autowired
    AutosuggestIndexer autosuggestIndexer;
    
    
    
    private IndexerItem[] indexerItems;
    
    public String[] args;
    
    public static final String NO_DEPS_ARG = "nodeps";
    public static final String CORES_ARG = "cores";
    public static final String HELP_ARG = "help";
    
//    public static class NotImplementedYet extends AbstractIndexer {
//        @Override
//        public void run() throws IndexerException {
//            throw new IndexerException("Not implemented yet.");
//        }    @Override
//        protected Logger getLogger() {
//            return LoggerFactory.getLogger(NotImplementedYet.class);
//        }
//    }
    
    public class IndexerItem {
        public final String name;
        public final AbstractIndexer indexer;
        
        public IndexerItem(String name, AbstractIndexer indexer) {
            this.name = name;
            this.indexer = indexer;
        }
    }
    
    protected ApplicationContext applicationContext;
    
    
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
        OptionSet options = parseCommandLine(args);
        if (options != null) {
            this.args = args;
            applicationContext = loadApplicationContext((String)options.valuesOf(CONTEXT_ARG).get(0));
            applicationContext.getAutowireCapableBeanFactory().autowireBeanProperties(this, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);
            initialiseHibernateSession(applicationContext);
            loadIndexers();
        } else {
            throw new IndexerException("Failed to parse command-line options.");
        }
        
        final int mb = 1024*1024;
        Runtime runtime = Runtime.getRuntime();
        DecimalFormat formatter = new DecimalFormat("#,###");
        logger.info("Used memory : " + (formatter.format(runtime.totalMemory() - runtime.freeMemory() / mb)));
        logger.info("Free memory : " + formatter.format(runtime.freeMemory()));
        logger.info("Total memory: " + formatter.format(runtime.totalMemory()));
        logger.info("Max memory  : " + formatter.format(runtime.maxMemory()));
    }
	
    protected void initialiseHibernateSession(ApplicationContext applicationContext) {
        // allow hibernate session to stay open the whole execution
        PlatformTransactionManager transactionManager = (PlatformTransactionManager) applicationContext.getBean("transactionManager");
        DefaultTransactionAttribute transactionAttribute = new DefaultTransactionAttribute(TransactionDefinition.PROPAGATION_REQUIRED);
        transactionAttribute.setIsolationLevel(TransactionDefinition.ISOLATION_SERIALIZABLE);
        transactionManager.getTransaction(transactionAttribute);
    }

    public void run() throws IndexerException {
        logger.info("Starting IndexerManager. nodeps = " + nodeps + ". Building the following cores (in order):");
        logger.info("\t" + StringUtils.join(cores));
        
        for (IndexerItem indexerItem : indexerItems) {
            indexerItem.indexer.initialise(indexerArgs);
            // If the core build fails, retry up to RETRY_COUNT times before failing the IndexerManager build.
            for (int i = 0; i <= RETRY_COUNT; i++) {
                try {
                    indexerItem.indexer.run();
                    indexerItem.indexer.validateBuild();
                    break;
                } catch (IndexerException ie) {
                    if (i < RETRY_COUNT) {
                        logger.warn("IndexerException: core build attempt[" + i + "] failed. Retrying.");
                        TestUtils.sleep(RETRY_SLEEP_IN_MS);
                    } else {
                        throw ie;
                    }
                } catch (Exception e) {
                    if (i < RETRY_COUNT) {
                        logger.warn("Exception: core build attempt[" + i + "] failed. Retrying.");
                        TestUtils.sleep(RETRY_SLEEP_IN_MS);
                    } else {
                        throw new IndexerException(e);
                    }
                }
            }
        }
    }
    
    protected ApplicationContext loadApplicationContext(String context) {
        ApplicationContext appContext;

        try {
            // Try context as a file resource
            getLogger().info("Trying to load context from file system...");
            appContext = new FileSystemXmlApplicationContext("file:" + context);
            getLogger().info("Context loaded from file system");
        } catch (BeansException e) {
            getLogger().warn("Unable to load the context file: {}", e.getMessage());

            // Try context as a class path resource
            appContext = new ClassPathXmlApplicationContext(context);
            getLogger().warn("Using classpath app-config file: {}", context);
        }

        return appContext;
    }
    
    private void loadIndexers() {
        List<IndexerItem> indexerItemList = new ArrayList();
        
        for (String core : cores) {
            switch (core) {
                case OBSERVATION_CORE:          indexerItemList.add(new IndexerItem(OBSERVATION_CORE, observationIndexer));                 break;
                case GENOTYPE_PHENOTYPE_CORE:   indexerItemList.add(new IndexerItem(GENOTYPE_PHENOTYPE_CORE, genotypePhenotypeIndexer));    break;
                case STATSTICAL_RESULT_CORE:    indexerItemList.add(new IndexerItem(STATSTICAL_RESULT_CORE, statisticalResultIndexer));     break;
                    
                case PREQC_CORE:                indexerItemList.add(new IndexerItem(PREQC_CORE, preqcIndexer));                             break;
                case ALLELE_CORE:               indexerItemList.add(new IndexerItem(ALLELE_CORE, alleleIndexer));                           break;
                case IMAGES_CORE:               indexerItemList.add(new IndexerItem(IMAGES_CORE, imagesIndexer));                           break;
                case IMPC_IMAGES_CORE:          indexerItemList.add(new IndexerItem(IMPC_IMAGES_CORE, impcImagesIndexer));                  break;
                case MP_CORE:                   indexerItemList.add(new IndexerItem(MP_CORE, mpIndexer));                                   break;
                case MA_CORE:                   indexerItemList.add(new IndexerItem(MA_CORE, maIndexer));                                   break;
                case PIPELINE_CORE:             indexerItemList.add(new IndexerItem(PIPELINE_CORE, pipelineIndexer));                       break;
                case GENE_CORE:                 indexerItemList.add(new IndexerItem(GENE_CORE, geneIndexer));                               break;
                case DISEASE_CORE:              indexerItemList.add(new IndexerItem(DISEASE_CORE, diseaseIndexer));                         break;
//                case AUTOSUGGEST_CORE:          indexerItemList.add(new IndexerItem(AUTOSUGGEST_CORE, autosuggestIndexer));                 break;
            }
        }
        
        indexerItems = indexerItemList.toArray(new IndexerItem[0]);
    }
    
    // PRIVATE METHODS
    
    /*
     * Rules:
     * 1. context is always required.
     * 2. cores is optional.
     * 2a. If cores is missing or empty:
     *     - build all cores starting at the preqc core (don't build OBSERVATION_CORE, GENOTYPE_PHENOTYPE_CORE, or STATSTICAL_RESULT_CORE).
     *     - specifying nodeps throws IllegalArgumentException.
     * 2b. If 1 core:
     *     - core name must be valid.
     *     - if nodeps is specified:
     *       - build only the specified core. Don't build downstream cores.
     *     - else
     *       - build requested core and all downstream cores.
     * 2c. If more than 1 core:
     *     - if nodeps is specified, it is ignored.
     *     - core names must be valid. No downstream cores are built.
     *
     * Core Build Truth Table (assume a valid '--context=' parameter is always supplied - not shown in table below to save space):
     *    |---------------------------------------------------------------------------|
     *    |          command line           |  nodeps value   |     cores built       |
     *    |---------------------------------------------------------------------------|
     *    | <empty>                         | false           | preqc to autosuggest  |
     *    | --cores                         | false           | preqc to autosuggest  |
     *    | --nodeps                        | NoDepsException | <none>                |
     *    | --cores --nodeps                | NoDepsException | <none>                |
     *    | --cores=mp                      | false           | mp to autosuggest     |
     *    | --cores=mp --nodeps             | true            | mp                    |
     *    | --cores=mp,observation          | true            | mp,observation        |
     *    | --cores-mp,observation --nodeps | true            | mp,observation        |
     *    |---------------------------------------------------------------------------|
     
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
        
        // cores [optional]
        parser.accepts(CORES_ARG)
                .withOptionalArg()
                .ofType(String.class)
                .describedAs("A list of cores, in build order.");
        parser.accepts(NO_DEPS_ARG);
        parser.accepts(HELP_ARG)
                .forHelp();
        
        try {
            // Parse the parameters.
            options = parser.parse(args);
            nodeps = options.has(NO_DEPS_ARG);
            boolean coreMissingOrEmpty = false;
            List<String> coresRequested = new ArrayList();                      // This is a list of the unique core values specified in the 'cores=' argument.
            
            // Create a list of cores requested based on the value (or absence of) the 'cores=' argument.
            if (options.has(CORES_ARG)) {
                String rawCoresArgument = (String)options.valueOf((CORES_ARG));
                if ((rawCoresArgument == null) || (rawCoresArgument.trim().isEmpty())) {
                    // Cores list is empty.
                    coreMissingOrEmpty = true;
                } else if ( ! rawCoresArgument.contains(",")) {
                    coresRequested.add(rawCoresArgument);
                } else {
                    String[] coresArray = ((String)options.valueOf(CORES_ARG)).split(",");
                    coresRequested.addAll(Arrays.asList(coresArray));
                }
            } else {
                // Cores list is missing.
                coreMissingOrEmpty = true;
            }
            
            // Validate the parameters.
            if (coreMissingOrEmpty) {
                // nodeps cannot be specified if the 'cores=' argument is missing.
                if (options.has(NO_DEPS_ARG)) {
                    throw new IndexerException(new NoDepsException("Invalid argument 'nodeps' specified with empty core list."));
                }
            }
            // Verify that each core name in coresRequested exists. Throw an exception if any does not.
            for (String core : coresRequested) {
                if ( ! allCoresList.contains(core)) {
                    throw new IndexerException(new InvalidCoreNameException("Invalid core name '" + core + "'"));
                }
            }
            
            // Build the cores list as follows:
            //   If coresRequested is empty
            //       set firstCore to the first daily core, preqc.
            //   Else if coresRequested size == 1
            //     If nodeps
            //       set firstCore to null.
            //       set cores to coresRequested[0].
            //     Else
            //       set firstCore to coresRequested[0].
            //   Else (coresRequested.size > 1)
            //       set firstCore to null.
            //       set cores to each value in coresRequested.
            //
            //   If firstCore is not null
            //     search allCoresArray for the 0-relative firstCoreOffset of the value matching firstCore.
            //     add to cores every core name from firstCoreOffset to the last core in allCoresList.
            
            String firstCore = null;
            cores = new ArrayList();
            
            if (coresRequested.isEmpty()) {
                firstCore = PREQC_CORE;
            } else if (coresRequested.size() == 1) {
                if (nodeps) {
                    cores.addAll(coresRequested);
                } else {
                    firstCore = coresRequested.get(0);
                }
            } else {    // coresRequested.size() > 1.
                cores.addAll(coresRequested);
            }
            
            if (firstCore != null) {
                int firstCoreOffset = 0;
                for (String core : allCoresArray) {
                    if (core.equals(firstCore)) {
                        break;
                    }
                    firstCoreOffset++;
                }
                for (int i = firstCoreOffset; i < allCoresArray.length; i++) {
                    cores.add(allCoresArray[i]);
                } 
            }
        } catch (IndexerException icne) {
            if (icne.getCause() instanceof InvalidCoreNameException) {
                System.out.println("Expected required context file parameter, such as 'index-app-config.xml'.");
            }
            try { parser.printHelpOn(System.out); } catch (Exception e) {}
            throw icne;
        } catch (Exception uoe) {
            if ( (uoe.getLocalizedMessage().contains("Option context requires an argument")) 
               || uoe.getLocalizedMessage().contains("Missing required option(s) context"))
            {
                System.out.println("Expected required context file parameter, such as 'index-app-config.xml'.");
            }
            try { parser.printHelpOn(System.out); } catch (Exception e) {}
            throw new IndexerException(uoe);
        }
        indexerArgs = new String[] { "--context=" + (String)options.valueOf(CONTEXT_ARG) };
        logger.info("indexer config file: '" + indexerArgs[0] + "'");
        
        return options;
    }
    
    public static void main(String[] args) throws IndexerException {
        int retVal = mainReturnsStatus(args);
        if (retVal != STATUS_OK) {
            throw new IndexerException("Build failed: " + getStatusCodeName(retVal));
        }
    }
    
    
    
    public static int mainReturnsStatus(String[] args) {
        try {
            IndexerManager manager = new IndexerManager();
            manager.initialise(args);
            manager.run();
            logger.info("IndexerManager process finished successfully.  Exiting.");
        } catch (IndexerException ie) {
            // Print out the exceptions.
            if (ie.getLocalizedMessage() != null) {
                logger.error(ie.getLocalizedMessage());
            }
            int i = 0;
            Throwable t = ie.getCause();
            while (t != null) {
                StringBuilder errMsg = new StringBuilder("Level " + i + ": ");
                if (t.getLocalizedMessage() != null) {
                    errMsg.append(t.getLocalizedMessage());
                } else {
                    errMsg.append("<null>");
                }
                logger.error(errMsg.toString());
                i++;
                t = t.getCause();
            }
            if (ie.getCause() instanceof MissingRequiredContextException) {
                return STATUS_NO_CONTEXT;
            } else if (ie.getCause() instanceof NoDepsException) {
                return STATUS_NO_DEPS;
            } else if (ie.getCause() instanceof InvalidCoreNameException) {
                return STATUS_INVALID_CORE_NAME;
            } else if (ie.getCause() instanceof ValidationException) {
                return STATUS_VALIDATION_ERROR;
            }
            
            return -1;
        }
        
        return STATUS_OK;
    }
}