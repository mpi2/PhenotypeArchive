package uk.ac.ebi.phenotype.api;

import java.util.Set;

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
	
}
