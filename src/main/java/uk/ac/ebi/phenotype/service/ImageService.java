package uk.ac.ebi.phenotype.service;

import java.util.ArrayList;
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
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;

import uk.ac.ebi.phenotype.pojo.SexType;
import uk.ac.ebi.phenotype.service.dto.ImageDTO;
import uk.ac.ebi.phenotype.service.dto.ObservationDTO;
import uk.ac.ebi.phenotype.service.dto.ResponseWrapper;
import uk.ac.ebi.phenotype.web.controller.GenesController;

public class ImageService {

	private final HttpSolrServer solr;
	private final Logger log = LoggerFactory.getLogger(ImageService.class);


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
		// maybe we should go back to using the IMageDTO and add fields to the
		// response wrapper???
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
			log.debug("paramKV=" + paramKV);
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

		return new SolrQuery().setQuery("observation_type:image_record").addFilterQuery("(" + ObservationDTO.DOWNLOAD_FILE_PATH + ":" + "*mousephenotype.org* AND !download_file_path:*.pdf AND !download_file_path:*.dcm)").setRows(1000000000);
	}


	public QueryResponse getProcedureFacetsForGeneByProcedure(String mgiAccession, String experimentOrControl)
	throws SolrServerException {

		// Map<String, ResponseWrapper<ImageDTO>> map=new HashMap<String,
		// ResponseWrapper<ImageDTO>>();
		// String queryString = "q=gene_accession_id:\"" + mgiAccession +
		// "\"&fq=" + ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":" +
		// experimentOrControl+"&facet=true&facet.field=procedure_name&facet.mincount=1";
		// log.debug("queryString in ImageService getFacets=" + queryString);

		// make a facet request first to get the procedures and then reuturn
		// make requests for each procedure
		// http://wwwdev.ebi.ac.uk/mi/impc/dev/solr/impc_images/select?q=gene_accession_id:%22MGI:2384986%22&&fq=biological_sample_group:experimental&facet=true&facet.field=procedure_name
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery("gene_accession_id:\"" + mgiAccession + "\"");
		solrQuery.addFilterQuery(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":" + experimentOrControl);
		solrQuery.setFacetMinCount(1);
		solrQuery.setFacet(true);
		solrQuery.addFacetField("procedure_name");
		// solrQuery.setRows(0);
		QueryResponse response = solr.query(solrQuery);
		return response;
	}


	public QueryResponse getImagesForGeneByProcedure(String mgiAccession, String procedure_name, String parameterStableId, String experimentOrControl, int numberOfImagesToRetrieve, SexType sex, String metadataGroup, String strain)
	throws SolrServerException {

		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery("gene_accession_id:\"" + mgiAccession + "\"");
		solrQuery.addFilterQuery(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":" + experimentOrControl);
		if (metadataGroup != null) {
			solrQuery.addFilterQuery(ObservationDTO.METADATA_GROUP + ":" + metadataGroup);
		}
		if (strain != null) {
			solrQuery.addFilterQuery(ObservationDTO.STRAIN_NAME + ":" + strain);
		}
		if (sex != null) {
			solrQuery.addFilterQuery("sex:" + sex.name());
		}
		if (parameterStableId != null) {
			solrQuery.addFilterQuery(ObservationDTO.PARAMETER_STABLE_ID + ":" + parameterStableId);
		}

		solrQuery.addFilterQuery(ObservationDTO.PROCEDURE_NAME + ":\"" + procedure_name + "\"");
		solrQuery.setRows(numberOfImagesToRetrieve);
		QueryResponse response = solr.query(solrQuery);
		return response;
	}
	
	public QueryResponse getImagesForGeneByParameter(String mgiAccession, String parameterStableId,String experimentOrControl, int numberOfImagesToRetrieve, SexType sex, String metadataGroup, String strain)
	throws SolrServerException {

		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery("gene_accession_id:\"" + mgiAccession + "\"");
		solrQuery.addFilterQuery(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":" + experimentOrControl);
		if (metadataGroup != null) {
			solrQuery.addFilterQuery(ObservationDTO.METADATA_GROUP + ":" + metadataGroup);
		}
		if (strain != null) {
			solrQuery.addFilterQuery(ObservationDTO.STRAIN_NAME + ":" + strain);
		}
		if (sex != null) {
			solrQuery.addFilterQuery("sex:" + sex.name());
		}
		if (parameterStableId != null) {
			solrQuery.addFilterQuery(ObservationDTO.PARAMETER_STABLE_ID + ":" + parameterStableId);
		}

		//solrQuery.addFilterQuery(ObservationDTO.PROCEDURE_NAME + ":\"" + procedure_name + "\"");
		solrQuery.setRows(numberOfImagesToRetrieve);
		QueryResponse response = solr.query(solrQuery);
		return response;
	}



	public QueryResponse getControlImagesForProcedure(String metadataGroup, String center, String strain, String procedure_name, String parameter, Date date, int numberOfImagesToRetrieve, SexType sex)
	throws SolrServerException {

		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":control");
		solrQuery.addFilterQuery(ObservationDTO.PHENOTYPING_CENTER + ":" + center, ObservationDTO.METADATA_GROUP + ":" + metadataGroup, ObservationDTO.STRAIN_NAME + ":" + strain, ObservationDTO.PARAMETER_STABLE_ID + ":" + parameter, ObservationDTO.PROCEDURE_NAME + ":\"" + procedure_name + "\"", "sex:" + sex.name());
		solrQuery.setRows(numberOfImagesToRetrieve);
		QueryResponse response = solr.query(solrQuery);

		return response;
	}


	/**
	 * Get the first control and then experimental images if available for the
	 * all procedures for a gene and then the first parameter for the procedure
	 * that we come across
	 * 
	 * @param acc
	 *            the gene to get the images for
	 * @param model
	 *            the model to add the images to
	 * @param controlsForBothSexes TODO
	 * @throws SolrServerException
	 */
	public void getImpcImagesForGenePage(String acc, Model model, int numberOfControls, int numberOfExperimental, boolean getForAllParameters)
	throws SolrServerException {

		QueryResponse solrR = this.getProcedureFacetsForGeneByProcedure(acc, "experimental");
		if (solrR == null) {
			log.error("no response from solr data source for acc=" + acc);
			return;
		}

		List<FacetField> facets = solrR.getFacetFields();
		if (facets == null) {
			log.error("no facets from solr data source for acc=" + acc);
			return;
		}

		Map<String, SolrDocumentList> facetToDocs = new HashMap<String, SolrDocumentList>();
		List<Count> filteredCounts = new ArrayList<Count>();

		for (FacetField facet : facets) {
			if (facet.getValueCount() != 0) {

				// get rid of wholemount expression/Adult LacZ facet as this is
				// displayed seperately in the using the other method
				// need to put the section in genes.jsp!!!
				for (Count count : facets.get(0).getValues()) {
					if (!count.getName().equals("Adult LacZ")) {
						filteredCounts.add(count);
					}
				}

				for (Count count : facet.getValues()) {
					SolrDocumentList list = new SolrDocumentList();// list of
																	// image
																	// docs to
																	// return to
																	// the
																	// procedure
																	// section
																	// of the
																	// gene page
					if (!count.getName().equals("Wholemount Expression")) {
						QueryResponse responseExperimental = this.getImagesForGeneByProcedure(acc, count.getName(), null, "experimental", 1, null, null, null);
						
						for (SexType sex : SexType.values()) {
							if (!sex.equals(SexType.hermaphrodite)) {
								// get 5 images if available for this experiment
								// type

								// need to add sex to experimental call
								// get information from first experimetal image
								// and
								// get the parameters for this next call to get
								// appropriate control images
								if (responseExperimental.getResults().size() > 0) {
									SolrDocument imgDoc = responseExperimental.getResults().get(0);
									QueryResponse responseExperimental2 = this.getImagesForGeneByProcedure(acc, count.getName(), (String) imgDoc.get(ObservationDTO.PARAMETER_STABLE_ID), "experimental", numberOfExperimental, sex, (String) imgDoc.get(ObservationDTO.METADATA_GROUP), (String) imgDoc.get(ObservationDTO.STRAIN_NAME));
									
										QueryResponse responseControl = this.getControlImagesForProcedure((String) imgDoc.get(ObservationDTO.METADATA_GROUP), (String) imgDoc.get(ObservationDTO.PHENOTYPING_CENTER), (String) imgDoc.get(ObservationDTO.STRAIN_NAME), (String) imgDoc.get(ObservationDTO.PROCEDURE_NAME), (String) imgDoc.get(ObservationDTO.PARAMETER_STABLE_ID), (Date) imgDoc.get(ObservationDTO.DATE_OF_EXPERIMENT), numberOfControls, sex);
										if (responseControl != null && responseControl.getResults().size() > 0) {
											log.info("adding control to list");
											list.addAll(responseControl.getResults());
											
										} else {
											log.error("no control images returned");
										}
									
									if (responseExperimental2 != null) {
										list.addAll(responseExperimental2.getResults());
									}
								}

								for (SolrDocument doc : list) {
									System.out.println("group=" + doc.get(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP));
								}
								facetToDocs.put(count.getName(), list);
							}
						}
					}
				}
			}

			model.addAttribute("impcImageFacets", filteredCounts);
			model.addAttribute("impcFacetToDocs", facetToDocs);
		}

	}


	public void getControlAndExperimentalImpcImages(String acc, Model model, String procedureName, String parameterStableId, int numberOfControls, int numberOfExperimental, boolean getAllParameters)
	throws SolrServerException {

		model.addAttribute("acc",acc);//forward the gene id along to the new page for links
		QueryResponse solrR = this.getParameterFacetsForGeneByProcedure(acc, procedureName, "experimental");
		if (solrR == null) {
			log.error("no response from solr data source for acc=" + acc);
			return;
		}

		List<FacetField> facets = solrR.getFacetFields();
		if (facets == null) {
			log.error("no facets from solr data source for acc=" + acc);
			return;
		}

		Map<String, SolrDocumentList> facetToDocs = new HashMap<String, SolrDocumentList>();
		List<Count> filteredCounts = new ArrayList<Count>();

		// get rid of wholemount expression/Adult LacZ facet as this is
		// displayed seperately in the using the other method
		// need to put the section in genes.jsp!!!
		for (Count count : facets.get(0).getValues()) {
			if (!count.getName().equals("Adult LacZ")) {
				filteredCounts.add(count);
			}
		}
		for (FacetField facet : facets) {
			if (facet.getValueCount() != 0) {
				for (Count count : facet.getValues()) {
					SolrDocumentList list = new SolrDocumentList();// list of
																	// image
																	// docs to
																	// return to
																	// the
																	// procedure
																	// section
																	// of the
																	// gene page
					if (!count.getName().equals("Adult LacZ")) {
						QueryResponse responseExperimental = this.getImagesForGeneByParameter(acc, count.getName(), "experimental", 1, null, null, null);
						int controlCount = 0;
						for (SexType sex : SexType.values()) {
							if (!sex.equals(SexType.hermaphrodite)) {
								// get 5 images if available for this experiment
								// type

								// need to add sex to experimental call
								// get information from first experimetal image
								// and
								// get the parameters for this next call to get
								// appropriate control images
								if (responseExperimental.getResults().size() > 0) {
									SolrDocument imgDoc = responseExperimental.getResults().get(0);
									QueryResponse responseExperimental2 = this.getImagesForGeneByParameter(acc, (String) imgDoc.get(ObservationDTO.PARAMETER_STABLE_ID), "experimental", numberOfExperimental, sex, (String) imgDoc.get(ObservationDTO.METADATA_GROUP), (String) imgDoc.get(ObservationDTO.STRAIN_NAME));
									if (controlCount < 1) {
										QueryResponse responseControl = this.getControlImagesForProcedure((String) imgDoc.get(ObservationDTO.METADATA_GROUP), (String) imgDoc.get(ObservationDTO.PHENOTYPING_CENTER), (String) imgDoc.get(ObservationDTO.STRAIN_NAME), (String) imgDoc.get(ObservationDTO.PROCEDURE_NAME), (String) imgDoc.get(ObservationDTO.PARAMETER_STABLE_ID), (Date) imgDoc.get(ObservationDTO.DATE_OF_EXPERIMENT), numberOfControls, sex);
										if (responseControl != null && responseControl.getResults().size() > 0) {
											log.info("adding control to list");
											list.addAll(responseControl.getResults());
											controlCount++;
										} else {
											log.error("no control images returned");
										}
									}
									if (responseExperimental2 != null) {
										list.addAll(responseExperimental2.getResults());
									}
								}

								for (SolrDocument doc : list) {
									System.out.println("group=" + doc.get(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP));
								}
								facetToDocs.put(count.getName(), list);
							}
						}
					}
				}

				model.addAttribute("impcImageFacets", filteredCounts);
				model.addAttribute("impcFacetToDocs", facetToDocs);
			}
		}
				
	}


	public QueryResponse getParameterFacetsForGeneByProcedure(String acc, String procedureName, String controlOrExperimental)
	throws SolrServerException {
//e.g. http://ves-ebi-d0.ebi.ac.uk:8090/mi/impc/dev/solr/impc_images/query?q=gene_accession_id:%22MGI:2384986%22&fq=biological_sample_group:experimental&fq=procedure_name:X-ray&facet=true&facet.field=parameter_stable_id
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery("gene_accession_id:\"" + acc + "\"");
		solrQuery.addFilterQuery(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":" + controlOrExperimental);
		solrQuery.setFacetMinCount(1);
		solrQuery.setFacet(true);
		solrQuery.addFilterQuery(ObservationDTO.PROCEDURE_NAME + ":" + procedureName);
		solrQuery.addFacetField(ObservationDTO.PARAMETER_STABLE_ID);
		// solrQuery.setRows(0);
		QueryResponse response = solr.query(solrQuery);
		return response;

	}

}
