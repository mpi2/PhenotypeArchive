package uk.ac.ebi.phenotype.service;

import java.util.List;

import org.apache.solr.client.solrj.SolrServerException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import uk.ac.ebi.phenotype.service.dto.ImageDTO;
import uk.ac.ebi.phenotype.service.dto.ImageDTOWrapper;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-config.xml" })
public class ImageServiceTest {
	
	@Autowired
	private ImageService imageService;


	@Test
	public void testGgetImageDTOsForSolrQuery(){
		ImageDTOWrapper imageDTOs;
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
	
	@Test
	public void testgetAllImages(){
		List<ImageDTO> imageDTOs;
		try {
			imageDTOs = imageService.getAllImageDTOs();
		
		for(ImageDTO imageDTO:imageDTOs){
			System.out.println(imageDTO.getOmeroId());
		}
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
