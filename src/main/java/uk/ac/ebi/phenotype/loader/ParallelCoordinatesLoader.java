/**
 * @author tudose
 */
package uk.ac.ebi.phenotype.loader;

import java.io.File;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import uk.ac.ebi.phenotype.service.ObservationService;


/**
 * @author tudose
 *
 */
public class ParallelCoordinatesLoader {

	private ObservationService os;
	
    static final String CONTEXT_ARG = "context";
    private static final Logger logger = LoggerFactory.getLogger(ParallelCoordinatesLoader.class);
	

    public static void main(String[] args) 
    throws Exception {

		ParallelCoordinatesLoader main = new ParallelCoordinatesLoader(args);		
        main.getDataFor();
		
    }
	
    
    public void getDataFor() 
    throws SolrServerException{
    	
    	System.out.println("null " + (os == null));
		String data = os.getMeansFor("IMPC_CBC_*", true);
		System.out.println(data);
    }
    
    
	public ParallelCoordinatesLoader(String[] args) 
	throws Exception {
		

        ApplicationContext applicationContext;
        OptionSet options = parseCommandLine(args);
     
        if (options != null) {
        	
            applicationContext = loadApplicationContext((String) options.valuesOf(CONTEXT_ARG).get(0));
            os = applicationContext.getBean(ObservationService.class);
            
        } else {
            throw new Exception("Failed to parse command-line options.");
        }
		logger.info("Process finished.  Exiting.");

    }
	
	
	protected static OptionSet parseCommandLine(String[] args) {
		
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
}
