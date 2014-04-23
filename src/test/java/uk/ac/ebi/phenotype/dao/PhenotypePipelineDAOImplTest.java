package uk.ac.ebi.phenotype.dao;

import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.ebi.phenotype.pojo.Datasource;
import uk.ac.ebi.phenotype.pojo.MetaData;
import uk.ac.ebi.phenotype.pojo.OntologyTerm;
import uk.ac.ebi.phenotype.pojo.Parameter;
import uk.ac.ebi.phenotype.pojo.Pipeline;
import uk.ac.ebi.phenotype.pojo.Procedure;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-config.xml" })
@TransactionConfiguration
@Transactional
public class PhenotypePipelineDAOImplTest {

	@Autowired
	private PhenotypePipelineDAO phenotypePipelineDAO;
	
	@Autowired
	private OntologyTermDAO ontologyTermDAO;

	@Autowired
	private DatasourceDAO datasourceDAO;
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetAllPhenotypePipelines() {
		
		List<Pipeline> pipelines = phenotypePipelineDAO.getAllPhenotypePipelines();
		assertTrue(pipelines.size()>6);
		for(Procedure proc: pipelines.get(0).getProcedures()){
			for(MetaData meta :proc.getMetaDataSet()){
				assertTrue(meta.getName() != null);
				assertTrue(meta.getValue() != null);
			}
		}
	}

	@Test
	public void testGetPhenotypePipelineByStableId() {
		Pipeline pipeline = phenotypePipelineDAO.getPhenotypePipelineByStableId("HRWL_001");
		assertTrue(pipeline != null);
	}

	@Test
	public void testGetPhenotypePipelineByStableIdAndVersion() {
		Pipeline pipeline = phenotypePipelineDAO.getPhenotypePipelineByStableIdAndVersion("HRWL_001", 1, 0);
		assertTrue(pipeline != null);
	}

	@Test
	public void testGetProcedureByStableId() {
		Procedure procedure = phenotypePipelineDAO.getProcedureByStableId("IMPC_BWT_001");
		assertTrue(procedure != null);
	}

	@Test
	public void testGetParameterByStableIdAndVersion() {
		Parameter parameter = phenotypePipelineDAO.getParameterByStableIdAndVersion("ESLIM_013_001_001", 1, 0);
		assertTrue(parameter != null);
	}

	@Test
	public void testGetProcedureMetaDataParametersByStableIdAndVersion() {
		List<Parameter> metaParams = phenotypePipelineDAO.getProcedureMetaDataParametersByStableIdAndVersion("IMPC_BWT_001", 1, 0);
		assertTrue(metaParams != null);
	}
	
	@Test
	public void testGetProcedureByOntologyTerm() {
		Datasource ds = datasourceDAO.getDatasourceByShortName("MP");
		OntologyTerm term = ontologyTermDAO.getOntologyTermByAccessionAndDatabaseId("MP:0001304", ds.getId());
		Set<Procedure> procedures = phenotypePipelineDAO.getProceduresByOntologyTerm(term);
		assertTrue(procedures.size() != 0);
	}

}
