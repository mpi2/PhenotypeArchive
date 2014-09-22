package uk.ac.ebi.phenotype.solr.indexer;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.beans.Field;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

import uk.ac.ebi.phenotype.service.ImageService;
import uk.ac.ebi.phenotype.service.ObservationService;

/**
 * class to load the image data into the solr core - use for impc data first
 * then we can do sanger images as well?
 * 
 * @author jwarren
 * 
 */
public class ImagesIndexer {
	private static final Logger logger = LoggerFactory.getLogger(ImagesIndexer.class);
	@Autowired
	private BasicDataSource connection;
	@Autowired
	private ObservationService observationService;
	@Autowired
	ImageObservationDao imageObservationDao;
	
	private String solrUrl;
	


	
	public String getSolrUrl() {
	
		return solrUrl;
	}


	
	public void setSolrUrl(String solrUrl) {
	
		this.solrUrl = solrUrl;
	}


	public ImagesIndexer() {
		super();
		
	}


	public String getKomp2User() {

		return komp2User;
	}


	public void setKomp2User(String komp2User) {

		this.komp2User = komp2User;
	}


	public String getMysqlHost() {

		return mysqlHost;
	}


	public void setMysqlHost(String mysqlHost) {

		this.mysqlHost = mysqlHost;
	}


	public Integer getMysqlPort() {

		return mysqlPort;
	}


	public void setMysqlPort(Integer mysqlPort) {

		this.mysqlPort = mysqlPort;
	}


	public String getMysqlDbName() {

		return mysqlDbName;
	}


	public void setMysqlDbName(String mysqlDbName) {

		this.mysqlDbName = mysqlDbName;
	}


	public String getKomp2Pass() {

		return komp2Pass;
	}


	public void setKomp2Pass(String komp2Pass) {

		this.komp2Pass = komp2Pass;
	}

	// these variables should be got from the config bean or main args
	private String core = "impc_images";
	private String komp2Pass = "wr1t3rmig";
	private String komp2User = "migrw";
	private String mysqlHost = "mysql-mi-dev";
	// String mysqlProdHost="mysql-mi-prod";
	private Integer mysqlPort = 4356;
	// Integer mysqlProdPort=4404;
	// String mysqlHost="localhost";
	// Integer mysqlPort=3306;
	private String mysqlDbName = "komp2";// "komp2_beta";

	private String urlRootPathForImages;// ="http://img1.sanger.ac.uk/";
	private String moveToDir;// =
								// "/Users/jwarren/Documents/ImagesFromSangerUrls/";//
								// root directory on filesystem we want to
								// move the sub directories and image to.


	public static void main(String[] args) {

		
		OptionParser parser = new OptionParser();

		// parameter to indicate which spring context file to use
		parser.accepts("context").withRequiredArg().ofType(String.class);
		parser.accepts("solrUrl").withRequiredArg().ofType(String.class);

		OptionSet options = parser.parse(args);
		String context = (String) options.valuesOf("context").get(0);

		logger.info("Using application context file {}", context);

		

		ApplicationContext applicationContext;
		try {

			// Try context as a file resource
			applicationContext = new FileSystemXmlApplicationContext("file:" + context);

		} catch (RuntimeException e) {

			logger.debug("Failed to load file system context trying to use classpath application context!");

			// Try context as a class path resource
			applicationContext = new FileSystemXmlApplicationContext(context);

		}
		// Wire up spring support for this application
		ImagesIndexer main = applicationContext.getBean(ImagesIndexer.class);
		
		try {
			main.runSolrIndexImagesUpdate();
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("Process finished.  Exiting.");
	}



	private void runSolrIndexImagesUpdate()
	throws SolrServerException, IOException {

		List<uk.ac.ebi.phenotype.service.dto.ImageDTO> imageObservations = observationService.getAllImageDTOs();
		System.out.println("image observations size=" + imageObservations.size());
		imageObservations = imageObservationDao.setExtraFields(imageObservations);
		String solrQ = solrUrl + "/" + core;
		HttpSolrServer server = new HttpSolrServer(solrQ);
		System.out.println("images solr=" + solrQ);
		server.addBeans(imageObservations);
		System.out.println("commiting");
		server.commit();
		// for(int i=0;i<1000;++i) {
		// //add Item objects to the list
		//
		//
		// if(i%100==0) {
		// System.out.println("commiting 100 docs now");//server.commit(); //
		// periodically flush
		// }
		// }
		// server.commit();
		System.out.println("end of impc_image indexing");
	}

}
