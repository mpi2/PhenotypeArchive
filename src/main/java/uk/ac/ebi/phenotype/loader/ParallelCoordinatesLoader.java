
/**
 * @author tudose
 */
package uk.ac.ebi.phenotype.loader;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import org.apache.solr.client.solrj.SolrServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

import uk.ac.ebi.phenotype.service.ObservationService;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


/**
 * @author tudose
 *
 */
public class ParallelCoordinatesLoader {

    static final String CONTEXT_ARG = "context";
    private static final Logger logger = LoggerFactory.getLogger(ParallelCoordinatesLoader.class);

    @Autowired
    private ObservationService os;


    public static void main(String[] args)
    throws Exception {

        ParallelCoordinatesLoader loader = new ParallelCoordinatesLoader();
        loader.initialise(args);
        loader.getDataFor();
       
    }
    

    public void getDataFor()
    throws SolrServerException{
       
        String data = os.getMeansFor("IMPC_CBC_*", true);
        File f = new File("WebContent/resources/js/data/IMPC_CBC.js");
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(f));
			out.write(data);
	        out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
        System.out.println(data);
        System.out.println(f.getAbsolutePath());
    }


    private void initialise(String[] args)
    throws Exception {
       
        OptionSet options = parseCommandLine(args);

        if (options != null) {

            ApplicationContext applicationContext = loadApplicationContext((String) options.valuesOf(CONTEXT_ARG).get(0));

            applicationContext.getAutowireCapableBeanFactory().autowireBeanProperties(this, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);
            PlatformTransactionManager transactionManager = (PlatformTransactionManager) applicationContext.getBean("transactionManager");
            DefaultTransactionAttribute transactionAttribute = new DefaultTransactionAttribute(TransactionDefinition.PROPAGATION_REQUIRED);
            transactionAttribute.setIsolationLevel(TransactionDefinition.ISOLATION_SERIALIZABLE);
            transactionManager.getTransaction(transactionAttribute);

        } else {
            throw new Exception("Failed to parse command-line options.");
        }
        logger.info("Process finished.  Exiting.");

    }
    
    
    private OptionSet parseCommandLine(String[] args) {
       
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
    

    private ApplicationContext loadApplicationContext(String context) {
       
        ApplicationContext appContext;

        // Try context as a file resource.
        File file = new File(context);
        if (file.exists()) {
            // Try context as a file resource
            logger.info("Trying to load context from file system file {} ...", context);
            appContext = new FileSystemXmlApplicationContext("file:" + context);
        } else {
            // Try context as a class path resource
            logger.info("Trying to load context from classpath file: {}... ", context);
            appContext = new ClassPathXmlApplicationContext(context);
        }

        logger.info("Context loaded");

        return appContext;
    }


    public ObservationService getOs() {
        return os;
    }


    public void setOs(ObservationService os) {
        this.os = os;
    }
}
