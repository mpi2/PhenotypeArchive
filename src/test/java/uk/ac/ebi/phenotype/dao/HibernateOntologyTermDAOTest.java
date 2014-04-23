package uk.ac.ebi.phenotype.dao;

import static org.junit.Assert.*;
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


import uk.ac.ebi.phenotype.pojo.OntologyTerm;
import uk.ac.ebi.phenotype.pojo.Synonym;

/**
 * Unit test for the Hibernate-based ontologyTerm manager implementation. Tests application behavior to verify the Account
 * Hibernate mapping is correct.
 */

@ContextConfiguration( locations={ "classpath:test-config.xml" })
public class HibernateOntologyTermDAOTest extends AbstractTransactionalJUnit4SpringContextTests {

	@Autowired
	private OntologyTermDAO ontologyTermDAO;

	@Test
	public void testGetAllOntologyTermsFromMGIGenomeType() {
		List<OntologyTerm> ontologyTerms = ontologyTermDAO.getAllOntologyTermsByDatabaseId(2);
		System.out.println(ontologyTerms.size());
		assertTrue("Wrong number of ontologyTerms for MGI genome feature types", ontologyTerms.size() >= 39);
	}
	
	@Test
	public void testGetAllOntologyTermsFromMP() {
		List<OntologyTerm> ontologyTerms = ontologyTermDAO.getAllOntologyTermsByDatabaseId(5);
		assertTrue("Wrong number of ontologyTerms for MP", 9008<= ontologyTerms.size());
	}
	
	@Test
	public void testGetOntologyTerm() {
		
		OntologyTerm ontologyTerm = ontologyTermDAO.getOntologyTermByName("BAC/YAC end");
		// assert the returned ontologyTerm contains what you expect given the state
		// of the database
		assertNotNull("ontologyTerm should never be null", ontologyTerm);
		assertEquals("wrong ontologyTerm", "BAC/YAC end", ontologyTerm.getName());
		assertEquals("wrong ontologyTerm description","A region of sequence from the end of a BAC or YAC clone used as a reagent in mapping and genome assembly.", ontologyTerm.getDescription());

	}
	
	@Test
	public void testGetOntologyTermFromMP() {
		
		OntologyTerm ontologyTerm = ontologyTermDAO.getOntologyTermByName("abnormal vibrissa morphology");
		// assert the returned ontologyTerm contains what you expect given the state
		// of the database
		assertNotNull("ontologyTerm should never be null", ontologyTerm);
		assertEquals("wrong ontologyTerm accession", "MP:0002098", ontologyTerm.getId().getAccession());
		assertEquals("wrong ontologyTerm name ", "abnormal vibrissa morphology", ontologyTerm.getName());
		assertEquals("wrong ontologyTerm description","any structural anomaly of the stiff hairs projecting from the face around the nose of most mammals which act as touch receptors", ontologyTerm.getDescription());
		for (Synonym synonym: ontologyTerm.getSynonyms()) {
			System.out.println("Synonym: " + synonym.getSymbol());
		}
	}
	
	@Test
	public void testGetOntologyTermByAccessionAndDatabaseId() {
		
		OntologyTerm ontologyTerm = ontologyTermDAO.getOntologyTermByAccessionAndDatabaseId("MP:0002098", 5);
		// assert the returned ontologyTerm contains what you expect given the state
		// of the database
		assertNotNull("ontologyTerm should never be null", ontologyTerm);
		assertEquals("wrong ontologyTerm accession", "MP:0002098", ontologyTerm.getId().getAccession());
		assertEquals("wrong ontologyTerm name ", "abnormal vibrissa morphology", ontologyTerm.getName());
		assertEquals("wrong ontologyTerm description","any structural anomaly of the stiff hairs projecting from the face around the nose of most mammals which act as touch receptors", ontologyTerm.getDescription());
		for (Synonym synonym: ontologyTerm.getSynonyms()) {
			System.out.println("Synonym: " + synonym.getSymbol());
		}
	}
}
