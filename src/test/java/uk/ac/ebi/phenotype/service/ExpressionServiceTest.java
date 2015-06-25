package uk.ac.ebi.phenotype.service;

import static org.junit.Assert.*;

import java.util.Map;

import org.apache.solr.client.solrj.SolrServerException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import uk.ac.ebi.phenotype.service.ExpressionService.ExpressionRowBean;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-config.xml" })
public class ExpressionServiceTest {

	@Autowired
	private ExpressionService expressionService;
	Model modelReturned;
	Map<String, ExpressionRowBean> expressionAnatomyToRow;
	Map<String, ExpressionRowBean> mutantImagesAnatomyToRow;
	Map<String, ExpressionRowBean> wtAnatomyToRow;
	String anatomy="Bladder";//note this is currently the impress term not the MA term which differs often in small ways
	String anatomy2="Kidney";
	@Before
	public void setUp(){
		//http://ves-ebi-d0.ebi.ac.uk:8090/mi/impc/dev/solr/impc_images/select?q=gene_accession_id:%22MGI:97931%22&fq=ma_term:%22urinary%20bladder%22&fq=procedure_name:%22Adult%20LacZ%22&sort=parameter_name+asc&rows=1000
		String acc="MGI:97931";
		Model model=new ExtendedModelMap();
		try {
			modelReturned = expressionService.getExpressionDataForGene(acc, model);
			assertTrue(modelReturned!=null);
			Map map=model.asMap();
			 expressionAnatomyToRow=(Map<String, ExpressionRowBean>)map.get("expressionAnatomyToRow");
			 mutantImagesAnatomyToRow=(Map<String, ExpressionRowBean>)map.get("mutantImagesAnatomyToRow");
			 wtAnatomyToRow=(Map<String, ExpressionRowBean>)map.get("wtAnatomyToRow");
			
			
//			model.addAttribute("expressionAnatomyToRow", expressionAnatomyToRow);
//			model.addAttribute("mutantImagesAnatomyToRow", mutantImagesAnatomyToRow);
//			model.addAttribute("wtAnatomyToRow", wtAnatomyToRow);
			
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void imagesTest(){
		System.out.println("expression="+expressionAnatomyToRow.get(anatomy));
		ExpressionRowBean imagesRow=mutantImagesAnatomyToRow.get(anatomy);
		assertTrue(imagesRow.isHomImages());
		assertTrue(imagesRow.isImagesAvailable());
		assertTrue(imagesRow.getNumberOfImages()>=11);//currently 11 images for Bladder/urinary bladder
		
		System.out.println("expression="+expressionAnatomyToRow.get(anatomy2));
		ExpressionRowBean imagesRow2=mutantImagesAnatomyToRow.get(anatomy2);
		assertTrue(imagesRow2.isHomImages());
		assertTrue(imagesRow2.isImagesAvailable());
		assertTrue(imagesRow2.getNumberOfImages()>=11);//currently 11 images for Bladder/urinary bladder
		
	}
	
	@Test
	public void numberOfHetsTest(){
		
		System.out.println("expression="+expressionAnatomyToRow.get(anatomy));
		ExpressionRowBean expRow=expressionAnatomyToRow.get(anatomy);
		assertTrue(expRow.getNumberOfHetSpecimens()==4);
	}
	
	@Test
	public void mutantExpressionTest(){
		
		System.out.println("expression="+expressionAnatomyToRow.get(anatomy));
		ExpressionRowBean expRow=expressionAnatomyToRow.get(anatomy);
		assertFalse(expRow.isExpression());
		assertTrue(expRow.getSpecimenExpressed().size()==0);
		assertFalse(expRow.isNotExpressed());//there is no flag on any observation that says not expressed
		assertTrue(expRow.getSpecimenNotExpressed().size()==0);
		assertFalse(expRow.isNoTissueAvailable());
		assertTrue(expRow.getSpecimenNoTissueAvailable().size()==0);
		//falls back to ambiguous if no numbers for above and displays ambiguous like for this example currently
		
	}
	
	@Test
	public void wtExpressionTest(){
		
		System.out.println("expression="+wtAnatomyToRow.get(anatomy));
		ExpressionRowBean wtRow=wtAnatomyToRow.get(anatomy);
		assertFalse(wtRow.isExpression());
		assertTrue(wtRow.getSpecimenExpressed().size()==0);
		assertTrue(wtRow.isNotExpressed());//there is no flag on any observation that says not expressed
		assertTrue(wtRow.getSpecimenNotExpressed().size()>=44);
		assertFalse(wtRow.isNoTissueAvailable());
		assertTrue(wtRow.getSpecimenNoTissueAvailable().size()==0);
		//falls back to ambiguous
		
	}
	
}
