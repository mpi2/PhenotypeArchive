package uk.ac.ebi.phenotype.service;

import static org.junit.Assert.*;

import java.util.List;

import org.apache.solr.client.solrj.SolrServerException;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import uk.ac.ebi.phenotype.service.dto.ImageDTO;
import uk.ac.ebi.phenotype.service.dto.ResponseWrapper;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-config.xml" })
public class ImageServiceTest {
	
	@Autowired
	private ImageService imageService;


	@Test
	public void testGetImageDTOsForSolrQuery(){
		ResponseWrapper<ImageDTO> imageDTOs;
		try {
			//query is just the part after the core impcImages root
			String query="q=observation_type:image_record";
			imageDTOs = imageService.getImageDTOsForSolrQuery(query);
		
//		for(ImageDTO imageDTO:imageDTOs){
//			System.out.println(imageDTO.getOmeroId());
//		}
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
//	@Test
//	public void testGetExperimentalImagesForGene(){
//		ResponseWrapper<ImageDTO> imageDTOs;
//		String gene="MGI:2384986";
//		try {
//			imageDTOs= imageService.getExperimentalImagesForGeneByProcedure(gene);
//		
//		for(ImageDTO imageDTO:imageDTOs.getList()){
//			System.out.println(imageDTO.getOmeroId());
//		}
//		assertTrue(imageDTOs.getList().size()>1);
//		} catch (SolrServerException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
	
}
