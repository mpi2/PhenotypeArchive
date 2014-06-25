package uk.ac.ebi.phenotype.api;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Set;

import javax.validation.constraints.AssertTrue;

import org.apache.solr.client.solrj.SolrServerException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import uk.ac.ebi.phenotype.service.MpService;
import uk.ac.ebi.phenotype.web.pojo.BasicBean;


@ContextConfiguration( locations={ "classpath:test-config.xml" })
public class MpServiceTest extends  AbstractTransactionalJUnit4SpringContextTests {

	@Autowired
	MpService mpService;
	
	@Test
	public void testGetAllTopLevelPhenotypesAsBasicBeans(){
		try {
			Set<BasicBean> basicMpBeans=mpService.getAllTopLevelPhenotypesAsBasicBeans();
			for(BasicBean bean: basicMpBeans){
				System.out.println("MP name in test="+bean.getName()+" mp id in test="+bean.getId());
			}
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	
	@Test
	public void testGetChildren(){
		ArrayList<String> children;
		try {
			children = mpService.getChildrenFor("MP:0002461");
			assertTrue(children.size() > 0);
		} catch (SolrServerException e) {
			e.printStackTrace();
			fail();
		}
	}
}
