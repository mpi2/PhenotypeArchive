package uk.ac.ebi.phenotype.dao;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.ebi.phenotype.bean.StatisticalResultBean;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-config.xml" })
@TransactionConfiguration
@Transactional
public class StatisticalResultDAOTest {
	
	@Autowired
	StatisticalResultDAO statisticalResultDAO;
	
	@Test
	public void testGetPvaluesByAlleleAndPhenotypingCenterAndPipeline() {
		
		// MGI:4432021?phenotyping_center=HMGU&pipeline_stable_id=ESLIM_001
		String alleleAccession = "MGI:4432021";
		String phenotypingCenter = "HMGU";
		String pipelineStableId = "ESLIM_001";
		
		Map<String, List<StatisticalResultBean>> results = statisticalResultDAO.getPvaluesByAlleleAndPhenotypingCenterAndPipeline(
				alleleAccession, phenotypingCenter, pipelineStableId, null);
		
		for (String key: results.keySet()) {
			System.out.println(key);
			for (StatisticalResultBean bean: results.get(key)) {
				System.out.println(bean.getStatisticalMethod() + "\t" + bean.getpValue() + "\t" + bean.getEffectSize());
			}
		}
	}

}
