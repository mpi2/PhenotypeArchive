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
import org.apache.commons.lang3.StringUtils;
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
    public static final String OBSERVATION_CORE = "observation";                // For historic reasons, the core's actual name is 'experiment'.
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
    
    // main return values.
    public static final int STATUS_OK = 0;
    public static final int STATUS_NO_CONTEXT = 1;
    public static final int STATUS_NO_DEPS = 2;
    public static final int STATUS_INVALID_CORE_NAME = 3;
    
    private Boolean nodeps;
    private List<String> cores;
    public static final String[] allCoresArray = new String[] {      // In dependency order.
          // These are built only for a new data release.
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
    };
    private final List<String> allCores = Arrays.asList(allCoresArray);
    
    // -------------------- These aren't implemented yet. --------------------
//    @Autowired
//    ObservationIndexer observationIndexer;
//        
//    @Autowired
//    GenotypePhenotypeIndexer genotypePhenotypeIndexer;
//        
//    @Autowired
//    StatisticalResultIndexer statisticalResultIndexer;
    
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
    
    
    
    private final IndexerItem[] indexers = new IndexerItem[] {
        // Not implemented yet.
//        new IndexerItem(OBSERVATION_CORE,        observationIndexer)
//      , new IndexerItem(GENOTYPE_PHENOTYPE_CORE, genotypePhenotypeIndexer)
//      , new IndexerItem(STATSTICAL_RESULT_CORE,  statisticalResultIndexer)
            
        new IndexerItem(PREQC_CORE,              preqcIndexer)
      , new IndexerItem(ALLELE_CORE,             alleleIndexer)
      , new IndexerItem(IMAGES_CORE,             imagesIndexer)
      , new IndexerItem(IMPC_IMAGES_CORE,        impcImagesIndexer)
      , new IndexerItem(MP_CORE,                 mpIndexer)
      , new IndexerItem(MA_CORE,                 maIndexer)
      , new IndexerItem(PIPELINE_CORE,           pipelineIndexer)
      , new IndexerItem(GENE_CORE,               geneIndexer)
      , new IndexerItem(DISEASE_CORE,            diseaseIndexer)
    };
    
    public static final String NO_DEPS_ARG = "nodeps";
    public static final String CORES_ARG = "cores";
    public static final String HELP_ARG = "help";
    
    public static class NotImplementedYet extends AbstractIndexer {
        @Override
        public void run() throws IndexerException {
            throw new IndexerException("Not implemented yet.");
        }    @Override
        protected Logger getLogger() {
            return LoggerFactory.getLogger(NotImplementedYet.class);
        }
    }
    
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
            applicationContext = loadApplicationContext((String)options.valuesOf(CONTEXT_ARG).get(0));
            applicationContext.getAutowireCapableBeanFactory().autowireBeanProperties(this, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);
            initialiseHibernateSession(applicationContext);
        } else {
            throw new IndexerException("Failed to parse command-line options.");
        }
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
        
        for (IndexerItem indexer : indexers) {
//            indexer.indexer.initialise(args);
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
     * Core Build Truth Table (example):
     *    |--------------------------------------------------------------------------------|
     *    |          command line          |  nodeps value   |       cores built           |
     *    |--------------------------------------------------------------------------------|
     *    | <empty>                        | false           | preqc to disease            |
     *    | --nodeps                       | NoDepsException | <none>                      |
     *    | --cores=mp                     | false           | mp,ma,pipeline,gene,disease |
     *    | --cores-mp --nodeps            | true            | mp                          |
     *    | --cores=mp,observation         | true            | mp,observation              |
     *    |--cores-mp,observation --nodeps | true            | mp,observation              |
     *    |--------------------------------------------------------------------------------|
     
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
                    throw new IndexerException(new NoDepsException("Invalid argument 'nodeps' specified with empty core list."));
                }
            } else {
                // Core(s) specified. Verify that each core name exists.
                for (String core : coresArray) {
                    if ( ! allCores.contains(core)) {
                        throw new IndexerException(new InvalidCoreNameException("Invalid core name '" + core + "'"));
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
            throw new IndexerException(new MissingRequiredContextException());
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
            if (ie.getCause() instanceof MissingRequiredContextException) {
                return STATUS_NO_CONTEXT;
            } else if (ie.getCause() instanceof NoDepsException) {
                return STATUS_NO_DEPS;
            } else if (ie.getCause() instanceof InvalidCoreNameException) {
                return STATUS_INVALID_CORE_NAME;
            }
        }
        
        return STATUS_OK;
    }
}
