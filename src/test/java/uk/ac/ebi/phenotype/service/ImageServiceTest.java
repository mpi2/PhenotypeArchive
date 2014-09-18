package uk.ac.ebi.phenotype.service;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import uk.ac.ebi.phenotype.pojo.SexType;
import uk.ac.ebi.phenotype.service.dto.ImageDTO;
import uk.ac.ebi.phenotype.service.dto.ObservationDTO;
import uk.ac.ebi.phenotype.service.dto.ResponseWrapper;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-config.xml" })
public class ImageServiceTest {

	@Autowired
	private ImageService imageService;


	@Test
	public void testGetImageDTOsForSolrQuery() {

		ResponseWrapper<ImageDTO> imageDTOs;
		try {
			// query is just the part after the core impcImages root
			String query = "q=observation_type:image_record";
			imageDTOs = imageService.getImageDTOsForSolrQuery(query);

			// for(ImageDTO imageDTO:imageDTOs){
			// System.out.println(imageDTO.getOmeroId());
			// }
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	@Test
	public void testGetExperimentalImagesForGene() {

		QueryResponse imagesResponse;
		// gene_accession_id:"MGI:1861899"&fq=biological_sample_group:experimental&fq=sex:male&fq=parameter_stable_id:IMPC_CSD_085_001&rows=2
		String gene = "MGI:1861899";
		String procedureName = "Combined SHIRPA and Dysmorphology";
		String parameterStableId = "IMPC_CSD_085_001";
		String metaDataGroup = "ba6c7cda9f0d4ce7d9a676c2aef86e22";
		String strain = "C57BL/6NJ";
		SexType sex = SexType.female;
		String biologicalSampleGroup = "experimental";
		try {
			imagesResponse = imageService.getImagesForGeneByProcedure(gene, procedureName, parameterStableId, biologicalSampleGroup, 2, sex, metaDataGroup, strain);

			for (SolrDocument doc : imagesResponse.getResults()) {
				assertTrue(doc.get(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP).equals(biologicalSampleGroup));
				assertTrue(doc.get(ObservationDTO.SEX).equals(sex.name()));
				assertTrue(doc.get(ObservationDTO.PROCEDURE_NAME).equals(procedureName));
			}
			// assertTrue(imageDTOs.getList().size()>1);
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	@Test
	public void testGetControlImagesForProcedure() {

		// http://ves-ebi-d0.ebi.ac.uk:8090/mi/impc/dev/solr/impc_images/query?q=biological_sample_group:control&fq=phenotyping_center:JAX&fq=metadata_group:ba6c7cda9f0d4ce7d9a676c2aef86e22&fq=strain_name:C57BL/6NJ&fq=parameter_stable_id:IMPC_CSD_085_001&fq=procedure_name:%22Combined%20SHIRPA%20and%20Dysmorphology%22&rows=1&fq=sex:female
		QueryResponse imagesResponse;
		// gene_accession_id:"MGI:1861899"&fq=biological_sample_group:experimental&fq=sex:male&fq=parameter_stable_id:IMPC_CSD_085_001&rows=2
		String procedureName = "Combined SHIRPA and Dysmorphology";
		String parameterStableId = "IMPC_CSD_085_001";
		String metaDataGroup = "ba6c7cda9f0d4ce7d9a676c2aef86e22";
		String strain = "C57BL/6NJ";
		SexType sex = SexType.female;
		String biologicalSampleGroup = "control";
		try {
			imagesResponse = imageService.getControlImagesForProcedure(metaDataGroup, "JAX", strain, procedureName, parameterStableId, null, 2, sex);

			for (SolrDocument doc : imagesResponse.getResults()) {
				assertTrue(doc.get(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP).equals(biologicalSampleGroup));
				assertTrue(doc.get(ObservationDTO.SEX).equals(sex.name()));
				assertTrue(doc.get(ObservationDTO.PROCEDURE_NAME).equals(procedureName));
			}
			// assertTrue(imageDTOs.getList().size()>1);
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	@Test
	public void testGetFacetsForGeneByProcedure() {

		QueryResponse imagesResponse;
		// gene_accession_id:"MGI:1861899"&fq=biological_sample_group:experimental&fq=sex:male&fq=parameter_stable_id:IMPC_CSD_085_001&rows=2
		String gene = "MGI:1861899";

		try {
			imagesResponse = imageService.getProcedureFacetsForGeneByProcedure(gene, "experimental");

			for (FacetField facet : imagesResponse.getFacetFields()) {
				System.out.println(facet.getValues());

			}
			assertTrue(imagesResponse.getFacetFields().size() > 0);
			// assertTrue(imageDTOs.getList().size()>1);
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testGetParameterFacetsForGeneByProcedure(){
		QueryResponse imagesResponse;
		//http://ves-ebi-d0.ebi.ac.uk:8090/mi/impc/dev/solr/impc_images/query?q=gene_accession_id:%22MGI:2384986%22&fq=biological_sample_group:experimental&fq=procedure_name:X-ray&facet=true&facet.field=parameter_stable_id
		String gene = "MGI:2384986";
		String procedureName="X-ray";
		String controlOrExperimental="experimental";
		try {
			imagesResponse = imageService.getParameterFacetsForGeneByProcedure(gene, procedureName, controlOrExperimental);
			for (FacetField facet : imagesResponse.getFacetFields()) {
				System.out.println(facet.getValues());

			}
			assertTrue(imagesResponse.getFacetFields().size() > 0);
			// assertTrue(imageDTOs.getList().size()>1);
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
