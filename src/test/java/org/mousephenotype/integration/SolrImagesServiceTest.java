package org.mousephenotype.integration;

import static org.junit.Assert.*;

import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.ac.ebi.phenotype.imaging.springrest.images.dao.ImagesSolrJ;
import uk.ac.ebi.phenotype.imaging.utils.SolrUtils;

public class SolrImagesServiceTest {
	private String solrBaseUrl="http://localhost:8080/apache-solr-3.6.0/images";//"http://wwwdev.ebi.ac.uk/mi/solr/images";
	public static SolrServer server = null;
	private SolrUtils solrUtils=null;
	

	@Before
	public void setUp() throws Exception {
		
		server = new CommonsHttpSolrServer(solrBaseUrl);
		solrUtils=new SolrUtils();
	}

	@After
	public void tearDown() throws Exception {
	}


	@Test
	public void accessionTest(){
		//QueryResponse response=solrUtils.runSolrQuery(server, queryString, facetName, facetValue, filterQuerys, start, length);
		//http://wwwdev.ebi.ac.uk/mi/solr/images/select/?q=MGI\:1933365
		String nakedQuery="MGI:1933365";
//		String processedQuery=solrUtils.processQuery(nakedQuery);
//		String query="accession:"+processedQuery;
		System.out.println(nakedQuery);
		QueryResponse response=solrUtils.runSolrQuery(server, nakedQuery, "", "", Collections.<String> emptyList(), 0,10);
		assertTrue(response.getResults().size()==8);
	}
	
	@Test
	public void FieldsExistTest(){
		String nakedQuery="id:70220";
		System.out.println(nakedQuery);
		QueryResponse response=solrUtils.runSolrQuery(server, nakedQuery, "", "", Collections.<String> emptyList(), 0,10);
		assertTrue(response.getResults().size()==1);
		//:response.getResults().iterator()
		for(SolrDocument  doc :response.getResults()){
			Collection<String> fields = doc.getFieldNames();
			System.out.println(fields);
			assertTrue(doc.containsKey("accession"));
			assertTrue(this.listContains(doc, "accession","MGI:1891295" ));
			Integer age =(Integer) doc.get("ageInWeeks");
			assertTrue(age.equals(13));
			assertTrue(this.listContains(doc, "alleleName", "Heterozygous" ));
			assertTrue(this.listContains(doc, "annotationTermId", "MA:0000261" ));
			assertTrue(this.listContains(doc, "annotationTermName", "hard cataracts" ));
			assertTrue(this.singleValueIs(doc, "colonyName", "Ube3b-Gene Trap" ));
			assertTrue(this.listContains(doc, "expName", "Eye Morphology" ));
			assertTrue(this.listContains(doc, "expName_exp", "Eye Morphology_exp" ));
			assertTrue(this.singleValueIs(doc,"fullResolutionFilePath", "0/M00110006_00003602_download_full.jpg"));
			assertTrue(this.singleValueIs(doc, "gender", "Male" ));
			assertTrue(this.listContains(doc, "geneName", "ubiquitin protein ligase E3B" ));
			assertTrue(this.singleValueIs(doc, "genotype", "Ube/+" ));
			assertTrue(this.listContains(doc, "higherLevelMaTermId", "MA:0000016" ));
			assertTrue(this.listContains(doc, "higherLevelMaTermName", "nervous system" ));
			assertTrue(this.listContains(doc, "higherLevelMpTermName", "vision/eye phenotype" ));
			assertTrue(this.singleValueIs(doc, "id", "70220" ));
			assertTrue(this.singleValueIs(doc, "largeThumbnailFilePath", "0/M00110006_00003602_download_tn_large.jpg" ));
			assertTrue(this.listContains(doc, "liveSampleGroup", "experimental" ));//should be single value?
			assertTrue(this.listContains(doc, "maTermId", "MA:0000261" ));
			assertTrue(this.listContains(doc, "maTermName", "eye_MA:0000261" ));
			assertTrue(this.singleValueIs(doc, "mouseId", "110006" ));
			assertTrue(this.listContains(doc, "mpTermId", "MP:0010254"));
			assertTrue(this.listContains(doc, "mpTermName", "hard cataracts_MP:0010254"));
			//should be single valued
			assertTrue(this.singleValueIs(doc, "institute", "WTSI"));
			assertTrue(this.singleValueIs(doc,"originalFileName", "M00110006_00003602_download.tif"));
			assertTrue(this.listContains(doc, "sangerSymbol", "Ube3b<Gt(RRJ142)Byg>"));//different encoding to what comes out of front ends???
			assertTrue(this.singleValueIs(doc,"smallThumbnailFilePath", "0/M00110006_00003602_download_tn_small.jpg"));
			assertTrue(this.listContains(doc, "subtype", "protein coding gene"));
			assertTrue(this.listContains(doc, "symbol", "Ube3b"));
			assertTrue(this.listContains(doc, "symbol_gene", "Ube3b_MGI:1891295"));
			assertTrue(this.listContains(doc, "tagName", "Description"));
			assertTrue(this.listContains(doc, "tagName", "Comment"));
			assertTrue(this.listContains(doc, "tagValue", "Slitlamp"));
			assertTrue(this.listContains(doc, "tagValue", "Left; Large cataract"));
		}
	}
	
	@Test
	public void geneSynonymsExistTest(){
		String nakedQuery="id:70222";
		System.out.println(nakedQuery);
		QueryResponse response=solrUtils.runSolrQuery(server, nakedQuery, "", "", Collections.<String> emptyList(), 0,10);
		assertTrue(response.getResults().size()==1);
		//:response.getResults().iterator()
		for(SolrDocument  doc :response.getResults()){
			assertTrue(this.listContains(doc, "geneSynonyms", "SEST3"));
		}
	}
	
	@Test
	public void testNoNecropsyImages(){
		String nakedQuery="expName:Mouse Necropsy";
		System.out.println(nakedQuery);
		QueryResponse response=solrUtils.runSolrQuery(server, nakedQuery, "", "", Collections.<String> emptyList(), 0,10);
		assertTrue(response.getResults().size()==0);		
	}
	
	private boolean  listContains(SolrDocument doc, String fieldName, String desiredValue){
		List<String> values=(List)doc.get(fieldName);
		for(String value:values){
			System.out.println(value);
			if(value.equals(desiredValue))return true;
		}
		return false;
	}
	
	private boolean singleValueIs(SolrDocument doc, String fieldName, String desiredFieldValue){
		if(doc.get(fieldName).equals(desiredFieldValue )){
			return true;
		}
		return false;
	}

}
