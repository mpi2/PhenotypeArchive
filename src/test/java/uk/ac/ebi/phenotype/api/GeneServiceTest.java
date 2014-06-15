package uk.ac.ebi.phenotype.api;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.solr.client.solrj.SolrServerException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import uk.ac.ebi.phenotype.service.MpService;
import uk.ac.ebi.phenotype.web.pojo.BasicBean;
import uk.ac.ebi.phenotype.service.GeneService;

@ContextConfiguration( locations={ "classpath:test-config.xml" })
public class GeneServiceTest extends  AbstractTransactionalJUnit4SpringContextTests{
	@Autowired
	GeneService geneService;
	
 @Test
 public void testGetProductionStatusForGeneList(){
	 Set<String> geneIds=new TreeSet<>();
	 //mgi_accession_id
	 geneIds.add("MGI:104874");
	 geneIds.add("MGI:2683087");
	 try {
		Map<String, String> productionStatuses = geneService.getProductionStatusForGeneSet(geneIds);
		for(String key: productionStatuses.keySet()){
	System.out.println("key="+key+"  value="+productionStatuses.get(key));
		}
	 } catch (SolrServerException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
 }
}
