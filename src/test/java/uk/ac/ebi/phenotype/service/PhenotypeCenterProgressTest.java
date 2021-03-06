package uk.ac.ebi.phenotype.service;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.solr.client.solrj.SolrServerException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-config.xml" })
public class PhenotypeCenterProgressTest {
	@Qualifier("phenotypeCenterService")
	@Autowired
	PhenotypeCenterService phenotypeCenterProgress;
	@Test
	public void getPhenotypeCentersTest() {
		List<String> centers=null;
		try {
			centers = phenotypeCenterProgress.getPhenotypeCenters();
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(centers.size()>3);
	}
	
	@Test
	public void getStrainsForCenterTest(){
		List<String> strains=null;
		try {
			strains = phenotypeCenterProgress.getStrainsForCenter("MRC Harwell");
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(strains.size()>3);
	}
	
	@Test
	public void getProceduresPerStrainForCenterTest(){
		List<ProcedureBean> procedures=null;
		try {
			procedures = phenotypeCenterProgress.getProceduresPerStrainForCenter("UC Davis", "BL2327");
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(procedures.size()>3);
	}
	
	@Test
	public void getCentersProgressInformationTest(){
		Map<String,Map<String, List<ProcedureBean>>> centerData=null;
		try {
			centerData = phenotypeCenterProgress.getCentersProgressInformation();
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(centerData.size()>3);
	}

}
