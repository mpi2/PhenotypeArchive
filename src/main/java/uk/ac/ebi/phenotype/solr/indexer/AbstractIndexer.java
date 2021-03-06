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
package uk.ac.ebi.phenotype.solr.indexer;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.slf4j.Logger;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

import uk.ac.ebi.phenotype.solr.indexer.exceptions.IndexerException;

import java.io.File;
import java.sql.SQLException;


/**
 * @author Matt Pearce
 */
public abstract class AbstractIndexer {

    static final String CONTEXT_ARG = "context";

    protected static final String KOMP2_DATASOURCE_BEAN = "komp2DataSource";
    protected static final String ONTODB_DATASOURCE_BEAN = "ontodbDataSource";
    protected static final int MINIMUM_DOCUMENT_COUNT = 100;

    protected ApplicationContext applicationContext;
    
    // This is used to track the number of documents that were requested to be added by the core.addBeans() call.
    // It is used for later validation by querying the core after the build.
    protected int documentCount = 0;

    protected abstract Logger getLogger();

    public abstract void run() throws IndexerException, SQLException;

    public abstract void validateBuild() throws IndexerException;

    public long getDocumentCount(SolrServer solrServer) throws IndexerException {
        Long numFound = 0L;
        SolrQuery query = new SolrQuery().setQuery("*:*").setRows(0);
        try {
            numFound = solrServer.query(query).getResults().getNumFound();
        } catch (SolrServerException sse) {
            throw new IndexerException(sse);
        }
        
        return numFound;
    }
    
    public void initialise(String[] args) throws IndexerException {
        getLogger().info("args = " + StringUtils.join(args));
        OptionSet options = parseCommandLine(args);
        if (options != null) {
            applicationContext = loadApplicationContext((String) options.valuesOf(CONTEXT_ARG).get(0));
            applicationContext.getAutowireCapableBeanFactory().autowireBeanProperties(this, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);
            initialiseHibernateSession(applicationContext);
            printConfiguration();
        } else {
            throw new IndexerException("Failed to parse command-line options.");
        }
    }

    protected OptionSet parseCommandLine(String[] args) {
        OptionParser parser = new OptionParser();
        OptionSet options = null;

        // parameter to indicate which spring context file to use
        parser.accepts(CONTEXT_ARG).withRequiredArg().ofType(String.class)
                .describedAs("Spring context file, such as 'index-app-config.xml'");

        try {
            options = parser.parse(args);
        } catch (OptionException uoe) {
            if (args.length < 1) {
                System.out.println("Expected required context file parameter, such as 'index-app-config.xml'.");
            } else {
                System.out.println("Unable to open required context file '" + CONTEXT_ARG + ".\n\nUsage:\n");
            }
            try {
                parser.printHelpOn(System.out);
            } catch (Exception e) {
            }
            throw uoe;
        }

        return options;
    }

    protected ApplicationContext loadApplicationContext(String context) {
        ApplicationContext appContext;

        // Try context as a file resource.
        File file = new File(context);
        if (file.exists()) {
            // Try context as a file resource
            getLogger().info("Trying to load context from file system file {} ...", context);
            appContext = new FileSystemXmlApplicationContext("file:" + context);
        } else {
            // Try context as a class path resource
            getLogger().info("Trying to load context from classpath file: {}... ", context);
            appContext = new ClassPathXmlApplicationContext(context);
        }
            
        getLogger().info("Context loaded");
        
        return appContext;
    }

    protected void initialiseHibernateSession(ApplicationContext applicationContext) {
        // allow hibernate session to stay open the whole execution
        PlatformTransactionManager transactionManager = (PlatformTransactionManager) applicationContext.getBean("transactionManager");
        DefaultTransactionAttribute transactionAttribute = new DefaultTransactionAttribute(TransactionDefinition.PROPAGATION_REQUIRED);
        transactionAttribute.setIsolationLevel(TransactionDefinition.ISOLATION_SERIALIZABLE);
        transactionManager.getTransaction(transactionAttribute);
    }

    /**
     * This is a hook for extended classes to implement to print their
     * configuration - e.g. source and target solr urls, batch values, etc.
     *
     * The intention is to someday make this abstract to insure all implementors
     * provide a printConfiguration method specific to their indexer.
     */
    protected void printConfiguration() {

    }

}
