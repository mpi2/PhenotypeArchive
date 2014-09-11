package uk.ac.ebi.phenotype.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.common.SolrDocument;

import uk.ac.ebi.phenotype.pojo.SexType;
import uk.ac.ebi.phenotype.service.dto.ImageDTO;
import uk.ac.ebi.phenotype.service.dto.ObservationDTO;
import uk.ac.ebi.phenotype.service.dto.ResponseWrapper;

public class ImageService {

	private final HttpSolrServer solr;

	public ImageService(String solrUrl) {

		System.out.println("constructing image service with url=" + solrUrl);
		solr = new HttpSolrServer(solrUrl);
	}


	/**
	 * 
	 * @param query
	 *            the url from the page name onwards e.g
	 *            q=observation_type:image_record
	 * @return
	 * @throws SolrServerException
	 */
	public ResponseWrapper<ImageDTO> getImageDTOsForSolrQuery(String query)
	throws SolrServerException {

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
		ResponseWrapper<ImageDTO> wrapper = new ResponseWrapper<ImageDTO>(response.getBeans(ImageDTO.class));
		List<FacetField> facetFields = response.getFacetFields();
		//maybe we should go back to using the IMageDTO and add fields to the response wrapper???
		if (facetFields != null) {
			for (FacetField facetField : facetFields) {
				System.out.println("facetFields=" + facetField.getName() + facetField.getValueCount() + facetField.getValues());
			}
		}

		wrapper.setTotalNumberFound(response.getResults().getNumFound());
		return wrapper;
	}
	
	/**
	 * 
	 * @param query
	 *            the url from the page name onwards e.g
	 *            q=observation_type:image_record
	 * @return
	 * @throws SolrServerException
	 */
	public QueryResponse getResponseForSolrQuery(String query)
	throws SolrServerException {

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
		
		return response;
	}


	public static SolrQuery allImageRecordSolrQuery()
	throws SolrServerException {

		return new SolrQuery().setQuery("observation_type:image_record").addFilterQuery("(" + ObservationDTO.DOWNLOAD_FILE_PATH + ":" + "*mousephenotype.org* AND !download_file_path:*.pdf)").setRows(1000000000);
	}


	public QueryResponse getFacetsForGeneByProcedure(String mgiAccession, String experimentOrControl)
	throws SolrServerException {
		Map<String, ResponseWrapper<ImageDTO>> map=new HashMap<String, ResponseWrapper<ImageDTO>>();
		String queryString = "q=gene_accession_id:\"" + mgiAccession + "\"&" +  "&fq=" + ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":" + experimentOrControl+"&facet=true&facet.field=procedure_name&facet.mincount=1";
		System.out.println("queryString in ImageService=" + queryString);
		
		
		//make a facet request first to get the procedures and then reuturn make requests for each procedure
		//http://wwwdev.ebi.ac.uk/mi/impc/dev/solr/impc_images/select?q=gene_accession_id:%22MGI:2384986%22&&fq=biological_sample_group:experimental&facet=true&facet.field=procedure_name
		QueryResponse response=this.getResponseForSolrQuery(queryString);
		return response;
	}
	
	public QueryResponse getImagesForGeneByProcedure(String mgiAccession, String procedure, String experimentOrControl, int numberOfImagesToRetrieve, SexType sex)
	throws SolrServerException {
		String queryString = "q=gene_accession_id:\"" + mgiAccession + "\"&" +  "&fq=" + ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":" + experimentOrControl+"&rows="+numberOfImagesToRetrieve;
		if(sex!=null){//add a sex specifier
			queryString+="&fq=sex:"+sex.name();
		}
		System.out.println("queryString in ImageService=" + queryString);
		QueryResponse response = this.getResponseForSolrQuery(queryString);
		return response;
	}
	
	public QueryResponse getControlImagesForProcedure(String metadataGroup,String center,String strain, String parameter, Date date, int numberOfImagesToRetrieve, SexType sex)
	throws SolrServerException {
		String queryString = "q="+ObservationDTO.METADATA_GROUP+":" + metadataGroup + "&fq=" + ObservationDTO.PHENOTYPING_CENTER + ":" + center+"&"+"fq="+ObservationDTO.STRAIN_NAME+":"+strain+"&fq="+ObservationDTO.PARAMETER_STABLE_ID+":"+parameter+"&rows="+numberOfImagesToRetrieve;
		if(sex!=null){//add a sex specifier
			queryString+="&fq=sex:"+sex.name();
		}
		System.out.println("queryString in ImageService for controls=" + queryString);
		QueryResponse response = this.getResponseForSolrQuery(queryString);
		return response;
	}

}
