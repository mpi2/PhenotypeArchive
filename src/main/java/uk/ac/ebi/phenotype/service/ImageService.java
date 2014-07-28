package uk.ac.ebi.phenotype.service;

import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;

import uk.ac.ebi.phenotype.service.dto.ImageDTO;
import uk.ac.ebi.phenotype.service.dto.ImageDTOWrapper;
import uk.ac.ebi.phenotype.service.dto.ObservationDTO;

public class ImageService {
	
	private final HttpSolrServer solr;
	
	public ImageService(String solrUrl){
		System.out.println("constructing image service with url="+solrUrl);
		  solr = new HttpSolrServer(solrUrl);
	}
	
	public List<ImageDTO> getAllImageDTOs()
	throws SolrServerException {

		SolrQuery query = allImageRecordSolrQuery();
		return solr.query(query).getBeans(ImageDTO.class);

	}
	
	/**
	 * 
	 * @param query the url from the page name onwards e.g q=observation_type:image_record
	 * @return
	 * @throws SolrServerException
	 */
	public ImageDTOWrapper getImageDTOsForSolrQuery(String query) throws SolrServerException{
		SolrQuery solrQuery = new SolrQuery();
		String[] paramsKeyValues = query.split("&");
		for (String paramKV : paramsKeyValues) {
			System.out.println("paramKV=" + paramKV);
			String[] keyValue = paramKV.split("=");
			if (keyValue.length > 1) {
				String key = keyValue[0];
				String value = keyValue[1];
				System.out.println("param=" + key + " value=" + value);
				solrQuery.setParam(key, value);
			}

		}
		QueryResponse response = solr.query(solrQuery);
		ImageDTOWrapper wrapper=new ImageDTOWrapper();
		wrapper.setImageDTOs(response.getBeans(ImageDTO.class));
		wrapper.setNumberFound(response.getResults().getNumFound());
		return wrapper;
	}


	public static SolrQuery allImageRecordSolrQuery()
	throws SolrServerException {

		return new SolrQuery().setQuery("observation_type:image_record").addFilterQuery("(" + ObservationDTO.DOWNLOAD_FILE_PATH + ":" + "*mousephenotype.org* AND !download_file_path:*.pdf)").setRows(1000000000);
	}
	
}
