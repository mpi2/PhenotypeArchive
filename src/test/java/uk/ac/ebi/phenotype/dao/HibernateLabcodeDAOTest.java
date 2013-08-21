package uk.ac.ebi.phenotype.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.orm.hibernate3.annotation.AnnotationSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;


import uk.ac.ebi.phenotype.pojo.Labcode;

/**
 * Unit test for the Hibernate-based labcode manager implementation. Tests application behavior to verify the Account
 * Hibernate mapping is correct.
 */

@ContextConfiguration( locations={ "classpath:app-config.xml" })
public class HibernateLabcodeDAOTest extends AbstractTransactionalJUnit4SpringContextTests {

	@Autowired
	private LabcodeDAO labcodeDAO;

/*	private PlatformTransactionManager transactionManager;

	private TransactionStatus transactionStatus;

	@Before
	public void setUp() throws Exception {
		// setup the repository to test
		SessionFactory sessionFactory = createTestSessionFactory();
		labcodeDAO = new LabcodeDAOImpl(sessionFactory);
		// begin a transaction
		transactionManager = new HibernateTransactionManager(sessionFactory);
		transactionStatus = transactionManager.getTransaction(new DefaultTransactionDefinition());
	}*/

	@Test
	public void testGetAllLabcodes() {
		List<Labcode> labcodes = labcodeDAO.getAllLabcodes();
		assertEquals("Wrong number of labcodes", 8470, labcodes.size());
	}

	@Test
	public void testGetLabcode() {
		
		Labcode labcode = labcodeDAO.getLabcode("H");
		// assert the returned labcode contains what you expect given the state
		// of the database
		assertNotNull("labcode should never be null", labcode);
		assertEquals("wrong labcode", "H", labcode.getLabcode());
		assertEquals("wrong labcode status", "active", labcode.getStatus());
		assertEquals("wrong investigator", "MRC Mammalian Genetics Unit, Harwell", labcode.getInvestigator());
		assertEquals("wrong organisation", "MRC Mammalian Genetics Unit", labcode.getOrganisation());

	}

/*	@After
	public void tearDown() throws Exception {
		// rollback the transaction to avoid corrupting other tests
		if (transactionManager != null) transactionManager.rollback(transactionStatus);
	}

	private SessionFactory createTestSessionFactory() throws Exception {
		// create a FactoryBean to help create a Hibernate SessionFactory
		//ApplicationContext appContext = 
		//    	  new ClassPathXmlApplicationContext("/Users/gautier/Documents/workspace/PhenotypeArchive/WebContent/WEB-INF/app-config.xml");
		
		AnnotationSessionFactoryBean factoryBean = new AnnotationSessionFactoryBean();
		factoryBean.setHibernateProperties(createHibernateProperties());
		
		return factoryBean.getConfiguration().configure("/Users/gautier/Documents/workspace/PhenotypeArchive/WebContent/WEB-INF/app-config.xml").buildSessionFactory();
		
		//.buildSessionFactory();
		
		//factoryBean.setAnnotatedClasses(new Class[]{Labcode.class});
		
		// initialize according to the Spring InitializingBean contract
		//factoryBean.afterPropertiesSet();
		// get the created session factory
		//return (SessionFactory) factoryBean.getObject();
	}*/


/*	private Properties createHibernateProperties() {
		Properties properties = new Properties();
		// turn on formatted SQL logging (very useful to verify Hibernate is
		// issuing proper SQL)
		properties.setProperty("hibernate.show_sql", "true");
		properties.setProperty("hibernate.format_sql", "true");
		return properties;
	}*/
}
