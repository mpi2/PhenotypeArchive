package uk.ac.ebi.phenotype.api;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.solr.client.solrj.SolrServerException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import uk.ac.ebi.phenotype.service.GeneService;
import uk.ac.ebi.phenotype.service.dto.GeneDTO;

@ContextConfiguration(locations = { "classpath:test-config.xml" })
public class GeneServiceTest extends AbstractTransactionalJUnit4SpringContextTests {

	@Autowired
	GeneService geneService;


	@Test
	public void testGetProductionStatusForGeneList() {

		Set<String> geneIds = new TreeSet<>();
		// mgi_accession_id
		geneIds.add("MGI:104874");
		geneIds.add("MGI:2683087");
		try {
			Map<String, String> productionStatuses = geneService.getProductionStatusForGeneSet(geneIds);
			for (String key : productionStatuses.keySet()) {
				System.out.println("key=" + key + "  value=" + productionStatuses.get(key));
			}
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testGetGeneById() throws SolrServerException {
		String mgiId = "MGI:1929293";
		GeneDTO gene = geneService.getGeneById(mgiId);
		assertTrue(gene!=null);
		System.out.println("Gene symbol is: " + gene.getMarkerSymbol());
		System.out.println("Didn't retreive human gene symbol. Proof: " + gene.getHumanGeneSymbol());
	}
	
	@Test
	public void testGet(){
		String url="q=*:*&qf=marker_symbol^100.0 human_gene_symbol^90.0 marker_name^10.0 marker_synonym mgi_accession_id auto_suggest&defType=edismax&wt=json&rows=10&fl=hasQc,marker_symbol,mgi_accession_id,marker_synonym,marker_name,marker_type,human_gene_symbol,latest_es_cell_status,latest_production_status,latest_phenotype_status,status,es_cell_status,mouse_status,allele_name&pf=marker_symbol^1000 human_gene_symbol^800 marker_synonym^700 marker_name^500&bq=latest_phenotype_status:\"Phenotyping Complete\"^200&start=0&rows=10";
		try {
			List<GeneDTO> genes = geneService.getGeneDTOsForSolrUrl(url);
			System.out.println("genes list size="+genes.size());
			for(GeneDTO gene:genes){
				System.out.println("mgi accession from gene="+gene.getMgiAccessionId());
			}
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
