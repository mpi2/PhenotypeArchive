package uk.ac.ebi.phenotype.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

import uk.ac.ebi.phenotype.imaging.utils.ImageServiceUtil;
import uk.ac.ebi.phenotype.pojo.SexType;
import uk.ac.ebi.phenotype.service.dto.ImageDTO;
import uk.ac.ebi.phenotype.service.dto.ObservationDTO;

import java.util.*;

public class ExpressionService {

	private final HttpSolrServer experimentSolr;
	private final HttpSolrServer imagesSolr;
	@Autowired
	ExperimentService experimentService;

	public ExpressionService(String experimentSolrUrl, String imagesSolrUrl) {

		experimentSolr = new HttpSolrServer(experimentSolrUrl);
		imagesSolr = new HttpSolrServer(imagesSolrUrl);
	}

	public QueryResponse getExpressionImagesForGeneByAnatomy(
			String mgiAccession, String anatomy, String experimentOrControl,
			int numberOfImagesToRetrieve, SexType sex, String metadataGroup,
			String strain) throws SolrServerException {

		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery("gene_accession_id:\"" + mgiAccession + "\"");
		solrQuery.addFilterQuery(ImageDTO.BIOLOGICAL_SAMPLE_GROUP + ":"
				+ experimentOrControl);
		if (StringUtils.isNotEmpty(metadataGroup)) {
			solrQuery.addFilterQuery(ImageDTO.METADATA_GROUP + ":"
					+ metadataGroup);
		}
		if (StringUtils.isNotEmpty(strain)) {
			solrQuery.addFilterQuery(ImageDTO.STRAIN_NAME + ":" + strain);
		}
		if (sex != null) {
			solrQuery.addFilterQuery("sex:" + sex.name());
		}
		if (StringUtils.isNotEmpty(anatomy)) {
			solrQuery.addFilterQuery(ImageDTO.PARAMETER_ASSOCIATION_NAME
					+ ":\"" + anatomy + "\"");
		}
		// solrQuery.addFilterQuery(ObservationDTO.PROCEDURE_NAME + ":\"" +
		// procedure_name + "\"");
		solrQuery.setRows(numberOfImagesToRetrieve);
		QueryResponse response = imagesSolr.query(solrQuery);
		return response;
	}

	/**
	 * 
	 * @param mgiAccession
	 *            if mgi accesion null assume a request for control data
	 * @param fields
	 * @return
	 * @throws SolrServerException
	 */
	public QueryResponse getExpressionTableDataImages(String mgiAccession,
			String... fields) throws SolrServerException {
		// e.g.
		// http://ves-ebi-d0.ebi.ac.uk:8090/mi/impc/dev/solr/impc_images/select?q=gene_accession_id:%22MGI:106209%22&facet=true&facet.field=ma_term&facet.mincount=1&fq=(parameter_name:%22LacZ%20Images%20Section%22%20OR%20parameter_name:%22LacZ%20Images%20Wholemount%22)
		SolrQuery solrQuery = new SolrQuery();
		if (mgiAccession != null) {
			solrQuery.setQuery("gene_accession_id:\"" + mgiAccession + "\"");
		} else {
			// http://ves-ebi-d0.ebi.ac.uk:8090/mi/impc/dev/solr/impc_images/select?q=biological_sample_group:control&facet=true&facet.field=ma_term&facet.mincount=1&fq=(parameter_name:%22LacZ%20Images%20Section%22%20OR%20parameter_name:%22LacZ%20Images%20Wholemount%22)&rows=100000
			solrQuery.setQuery(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":\""
					+ "control" + "\"");
		}
		solrQuery.addFilterQuery(ImageDTO.PARAMETER_NAME
				+ ":\"LacZ Images Section\" OR " + ImageDTO.PARAMETER_NAME
				+ ":\"LacZ Images Wholemount\"");
		//solrQuery.addFilterQuery(ImageDTO.ZYGOSITY
		//		+ ":Homozygote");
		// solrQuery.setFacetMinCount(1);
		// solrQuery.setFacet(true);
		solrQuery.setFields(fields);
		// solrQuery.addFacetField("ma_term");
		solrQuery.setRows(100000);
		QueryResponse response = imagesSolr.query(solrQuery);
		return response;
	}

	/**
	 * 
	 * @param mgiAccession
	 *            if mgi accesion null assume a request for control data
	 * @param fields
	 * @return
	 * @throws SolrServerException
	 */
	public QueryResponse getCategoricalAdultLacZData(String mgiAccession,
			String... fields) throws SolrServerException {
		// e.g.
		// http://ves-ebi-d0.ebi.ac.uk:8090/mi/impc/dev/solr/experiment/select?q=gene_accession_id:%22MGI:1351668%22&facet=true&facet.field=parameter_name&facet.mincount=1&fq=(procedure_name:%22Adult%20LacZ%22)&rows=10000
		SolrQuery solrQuery = new SolrQuery();
		if (mgiAccession != null) {
			solrQuery.setQuery("gene_accession_id:\"" + mgiAccession + "\"");
		} else {
			// http://ves-ebi-d0.ebi.ac.uk:8090/mi/impc/dev/solr/impc_images/select?q=biological_sample_group:control&facet=true&facet.field=ma_term&facet.mincount=1&fq=(parameter_name:%22LacZ%20Images%20Section%22%20OR%20parameter_name:%22LacZ%20Images%20Wholemount%22)&rows=100000
			solrQuery.setQuery(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":\""
					+ "control" + "\"");
		}

		solrQuery.addFilterQuery(ImageDTO.PROCEDURE_NAME + ":\"Adult LacZ\"");
		solrQuery.addFilterQuery("!" + ImageDTO.PARAMETER_NAME
				+ ":\"LacZ Images Section\"");
		solrQuery.addFilterQuery("!" + ImageDTO.PARAMETER_NAME
				+ ":\"LacZ Images Wholemount\"");
		solrQuery.addFilterQuery(ObservationDTO.OBSERVATION_TYPE
				+ ":\"categorical\"");
		// solrQuery.setFacetMinCount(1);
		// solrQuery.setFacet(true);
		solrQuery.setFields(fields);
		// solrQuery.addFacetField("ma_term");
		solrQuery.setRows(100000);
		QueryResponse response = experimentSolr.query(solrQuery);
		return response;
	}

	public QueryResponse getLaczFacetsForGene(String mgiAccession,
			String... fields) throws SolrServerException {
		// e.g.
		// http://ves-ebi-d0.ebi.ac.uk:8090/mi/impc/dev/solr/impc_images/select?q=gene_accession_id:%22MGI:1920455%22&facet=true&facet.field=selected_top_level_ma_term&fq=(parameter_name:%22LacZ%20Images%20Section%22%20OR%20parameter_name:%22LacZ%20Images%20Wholemount%22)
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery("gene_accession_id:\"" + mgiAccession + "\"");
		solrQuery.addFilterQuery(ImageDTO.PARAMETER_NAME
				+ ":\"LacZ Images Section\" OR " + ImageDTO.PARAMETER_NAME
				+ ":\"LacZ Images Wholemount\"");
		solrQuery.setFacetMinCount(1);
		solrQuery.setFacet(true);
		solrQuery.setFields(fields);
		solrQuery.addFacetField("selected_top_level_ma_term");
		solrQuery.setRows(100000);
		QueryResponse response = imagesSolr.query(solrQuery);
		return response;
	}

	/**
	 * 
	 * @param acc
	 *            mgi_accession for gene
	 * @param topMaNameFilter
	 *            Only include images under the top level ma term specified here
	 * @param imagesOverview
	 *            If imagesOverview true then restrict response to only certain
	 *            fields as we are only displaying annotations for a dataset not
	 *            a specific thumbnail
	 * @param expressionOverview
	 *            If true we want some expression data/stats added to the model
	 *            for display in the tabbed pane on the gene page.
	 * @param model
	 *            Spring MVC model
	 * @throws SolrServerException
	 */
	public void getLacImageDataForGene(String acc, String topMaNameFilter,
			boolean imagesOverview, boolean expressionOverview, Model model)
			throws SolrServerException {
		QueryResponse laczResponse = null;
		if (imagesOverview) {
			laczResponse = getLaczFacetsForGene(acc, ImageDTO.OMERO_ID,
					ImageDTO.JPEG_URL, ImageDTO.SELECTED_TOP_LEVEL_MA_TERM,
					ImageDTO.PARAMETER_ASSOCIATION_NAME,
					ImageDTO.PARAMETER_ASSOCIATION_VALUE);
		} else {
			laczResponse = getLaczFacetsForGene(acc, ImageDTO.OMERO_ID,
					ImageDTO.JPEG_URL, ImageDTO.SELECTED_TOP_LEVEL_MA_TERM,
					ImageDTO.PARAMETER_ASSOCIATION_NAME,
					ImageDTO.PARAMETER_ASSOCIATION_VALUE, ImageDTO.ZYGOSITY,
					ImageDTO.SEX, ImageDTO.ALLELE_SYMBOL,
					ImageDTO.DOWNLOAD_URL, ImageDTO.IMAGE_LINK);
		}
		SolrDocumentList imagesResponse = laczResponse.getResults();
		// System.out.println("lacZimages found=" +
		// imagesResponse.getNumFound());
		List<FacetField> fields = laczResponse.getFacetFields();
		// System.out.println(fields);

		// we have the unique ma top level terms associated and all the images
		// now we need lists of images with these top level ma terms in their
		// annotation
		Map<String, SolrDocumentList> expFacetToDocs = new HashMap<>();
		String noTopMa = "Z Top Level MA";
		expFacetToDocs.put(noTopMa, new SolrDocumentList());

		for (SolrDocument doc : imagesResponse) {
			ArrayList<String> tops = (ArrayList<String>) doc
					.get(ImageDTO.SELECTED_TOP_LEVEL_MA_TERM);

			if (tops == null) {
				// top = "NA";
				// System.out.println("top is null");
				expFacetToDocs.get(noTopMa).add(doc);
			} else {

				for (String top : tops) {
					SolrDocumentList list = null;
					if (!expFacetToDocs.containsKey(top)) {
						expFacetToDocs.put(top, new SolrDocumentList());
					}
					list = expFacetToDocs.get(top);
					// System.out.println("adding doc="+doc);
					list.add(doc);

				}

			}
		}

		List<Count> topLevelMaTerms = fields.get(0).getValues();
		// Count dummyCountForImagesWithNoHigherLevelMa=new Count(new
		// FacetField(noTopMa),noTopMa,expFacetToDocs.get(noTopMa).size());
		// topLevelMaTerms.add(dummyCountForImagesWithNoHigherLevelMa);
		List<Count> filteredTopLevelMaTerms = new ArrayList<>();
		if (topMaNameFilter != null) {
			for (Count topLevel : topLevelMaTerms) {
				if (topLevel.getName().equals(topMaNameFilter)) {
					filteredTopLevelMaTerms.add(topLevel);
				}
			}
		} else {
			filteredTopLevelMaTerms = topLevelMaTerms;
		}

		ImageServiceUtil
				.sortHigherLevelTermCountsAlphabetically(filteredTopLevelMaTerms);
		ImageServiceUtil.sortDocsByExpressionAlphabetically(expFacetToDocs);
		model.addAttribute("impcExpressionImageFacets", filteredTopLevelMaTerms);
		model.addAttribute("impcExpressionFacetToDocs", expFacetToDocs);

	}

	/**
	 * 
	 * @param acc
	 *            mgi_accession for gene
	 * @param model
	 *            Spring MVC model
	 * @throws SolrServerException
	 */
	public void getExpressionDataForGene(String acc, Model model)
			throws SolrServerException {

		
		QueryResponse laczDataResponse = getCategoricalAdultLacZData(acc,
				ImageDTO.ZYGOSITY,
				ImageDTO.EXTERNAL_SAMPLE_ID, ObservationDTO.OBSERVATION_TYPE, ObservationDTO.PARAMETER_NAME, ObservationDTO.CATEGORY, ObservationDTO.BIOLOGICAL_SAMPLE_GROUP);
		SolrDocumentList mutantCategoricalAdultLacZData = laczDataResponse.getResults();
//		System.out.println("mutantCategoricalAdultLacZData found="
//				+ mutantCategoricalAdultLacZData.getNumFound());
		Map<String, SolrDocumentList> expressionAnatomyToDocs = getAnatomyToDocsForCategorical(mutantCategoricalAdultLacZData);
		Map<String, ExpressionRowBean> expressionAnatomyToRow = new TreeMap<>();
		Map<String, ExpressionRowBean> wtAnatomyToRow = new TreeMap<>();
		
		QueryResponse wtLaczDataResponse = getCategoricalAdultLacZData(null,
				ImageDTO.ZYGOSITY,
				ImageDTO.EXTERNAL_SAMPLE_ID, ObservationDTO.OBSERVATION_TYPE, ObservationDTO.PARAMETER_NAME, ObservationDTO.CATEGORY, ObservationDTO.BIOLOGICAL_SAMPLE_GROUP);
		SolrDocumentList wtCategoricalAdultLacZData = wtLaczDataResponse.getResults();
		//System.out.println("wtCategoricalAdultLacZData found="
				//+ wtCategoricalAdultLacZData.getNumFound());
		Map<String, SolrDocumentList> wtAnatomyToDocs = getAnatomyToDocsForCategorical(wtCategoricalAdultLacZData);
			
			
		QueryResponse laczImagesResponse = null;

		laczImagesResponse = getExpressionTableDataImages(acc, ImageDTO.ZYGOSITY,
				ImageDTO.PARAMETER_ASSOCIATION_NAME,
				ObservationDTO.OBSERVATION_TYPE, ObservationDTO.BIOLOGICAL_SAMPLE_GROUP);
		SolrDocumentList imagesMutantResponse = laczImagesResponse.getResults();
		//System.out.println("imagesMutantResponse found="
				//+ imagesMutantResponse.getNumFound());
		Map<String, ExpressionRowBean> mutantImagesAnatomyToRow = new TreeMap<>();
//		Map<String, ExpressionRowBean> controlAnatomyToRow = new TreeMap<String, ExpressionRowBean>();
//
//		Map<String, SolrDocumentList> controlAnatomyToDocs = getAnatomyToDocs(imagesControlResponse);
		Map<String, SolrDocumentList> mutantImagesAnatomyToDocs = getAnatomyToDocs(imagesMutantResponse);
		
		
		for (String anatomy : expressionAnatomyToDocs.keySet()) {
			
			System.out.println("getting exp row");
			ExpressionRowBean expressionRow = getAnatomyRow(anatomy,
					expressionAnatomyToDocs);
			int hetSpecimens=0;
			for (String key : expressionRow.getSpecimen().keySet()) {
				// System.out.println("specimen key="+key);
				if (expressionRow.getSpecimen().get(key).getZyg()
						.equalsIgnoreCase("heterozygote")) {
					hetSpecimens++;
				}	
			}
			expressionRow.setNumberOfHetSpecimens(hetSpecimens);
//			if(expressionRow.getSpecimenExpressed().keySet().size()>0){
//				expressionRow.setMutantExpression(true);
//			}
//			if(expressionRow.getSpecimenNotExpressed().keySet().size()>0){
//				expressionRow.setMutantNotExpressed(true);
//			}
//			if(expressionRow.getSpecimenNoTissueAvailable().keySet().size()>0){
//				expressionRow.setMutantNoTissueAvailable(true);
//			}
			expressionAnatomyToRow.put(anatomy, expressionRow);
			
			System.out.println("getting control row");
			ExpressionRowBean wtRow = getAnatomyRow(anatomy,
					wtAnatomyToDocs);
			
				//int wtHhetSpecimens=0;
//				for (String key : wtRow.getSpecimen().keySet()) {
//					// System.out.println("specimen key="+key);
//					if (wtRow.getSpecimen().get(key).getZyg()
//							.equalsIgnoreCase("heterozygote")) {
//						wtHhetSpecimens++;
//					}	
//				}
//				wtRow.setNumberOfHetSpecimens(wtHhetSpecimens);
				if(wtRow.getSpecimenExpressed().keySet().size()>0){
					wtRow.setWildTypeExpression(true);
				}
				wtAnatomyToRow.put(anatomy, wtRow);
			
//
//		QueryResponse laczControlResponse = null;
//
//		laczControlResponse = getExpressionTableDataImages(null,
//				ImageDTO.OMERO_ID, ImageDTO.JPEG_URL,
//				ImageDTO.SELECTED_TOP_LEVEL_MA_TERM,
//				ImageDTO.PARAMETER_ASSOCIATION_NAME,
//				ImageDTO.PARAMETER_ASSOCIATION_VALUE, ImageDTO.ZYGOSITY,
//				ImageDTO.SEX, ImageDTO.ALLELE_SYMBOL, ImageDTO.DOWNLOAD_URL,
//				ImageDTO.IMAGE_LINK, ImageDTO.BIOLOGICAL_SAMPLE_GROUP,
//				ImageDTO.EXTERNAL_SAMPLE_ID, ObservationDTO.OBSERVATION_TYPE);
//
//		SolrDocumentList imagesControlResponse = laczControlResponse
//				.getResults();
//		System.out.println("Images Controls data found="
//				+ imagesControlResponse.getNumFound());
	
		// now we have the docs seperated by anatomy terms lets get the data
		// needed for the table
		// should web be looking at experiment core? Are there expression
		// parameters with no image??? Looks like there are 100 more from this
		// query
		// http://ves-ebi-d0.ebi.ac.uk:8090/mi/impc/dev/solr/impc_images/select?q=*:*&facet=true&facet.field=ma_term&facet.mincount=1&fq=(parameter_name:%22LacZ%20Images%20Section%22%20OR%20parameter_name:%22LacZ%20Images%20Wholemount%22)
		// vs
		// http://ves-ebi-d0.ebi.ac.uk:8090/mi/impc/dev/solr/experiment/select?q=*:*&facet=true&facet.field=ma_term&facet.mincount=1&fq=(parameter_name:%22LacZ%20Images%20Section%22%20OR%20parameter_name:%22LacZ%20Images%20Wholemount%22)
		
			//System.out.println("expressionRow="+expressionRow.getExpressed());
//			ExpressionRowBean controlRow = getAnatomyRow(anatomy,
//					controlAnatomyToDocs);
//			if (controlRow.getExpressed() > 0) {
//				controlRow.setWildTypeExpression(true);
//			}
//			// System.out.println("getting mutants");
			ExpressionRowBean mutantImagesRow = getAnatomyRow(anatomy,
					mutantImagesAnatomyToDocs);
//			controlAnatomyToRow.put(anatomy, controlRow);
//			int hetSpecimens = 0;
//			for (String key : mutantRow.getSpecimenExpressed().keySet()) {
//				// System.out.println("specimen key="+key);
//				if (mutantRow.getSpecimenExpressed().get(key).getZyg()
//						.equalsIgnoreCase("heterozygote")) {
//					hetSpecimens++;
//				}
//				
//			}
//			
			mutantImagesRow.setNumberOfHetSpecimens(hetSpecimens);
			mutantImagesAnatomyToRow.put(anatomy, mutantImagesRow);
			//mutants parameter associations will contain some expression calls for the same docs as the categorical data - so will need to 
			//screen them out somehow? experiment id?

		}
		
		model.addAttribute("expressionAnatomyToRow", expressionAnatomyToRow);
		model.addAttribute("mutantImagesAnatomyToRow", mutantImagesAnatomyToRow);
		model.addAttribute("wtAnatomyToRow", wtAnatomyToRow);

	}

	private ExpressionRowBean getAnatomyRow(String anatomy,
			Map<String, SolrDocumentList> anatomyToDocs) {
		
		ExpressionRowBean row = new ExpressionRowBean();
		if (anatomyToDocs.containsKey(anatomy)) {
			for (SolrDocument doc : anatomyToDocs.get(anatomy)) {
				//System.out.println("categorical"+ doc.get(ObservationDTO.OBSERVATION_TYPE));
				if (doc.containsKey(ObservationDTO.OBSERVATION_TYPE) && doc.get(ObservationDTO.OBSERVATION_TYPE).equals(
						"categorical")) {
					//System.out.println("categorical");
					row=getExpressionCountForAnatomyTerm(anatomy, row, doc);
				} else if(doc.get(ObservationDTO.OBSERVATION_TYPE).equals("image_record") && doc.get(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP).equals("experimental")) {//assume image with parameterAssociation
					row = homImages(row, doc);
					if(anatomy.equalsIgnoreCase("adrenal")){
//						System.out.println("adrenal found");
//						System.out.println("doc="+doc);
//						System.out.println("control or exp="+doc.get(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP));
					}
					row.setImagesAvailable(true);
				}
			}
			if(row.getSpecimenExpressed().keySet().size()>0){
				row.setExpression(true);
			}
			if(row.getSpecimenNotExpressed().keySet().size()>0){
				row.setNotExpressed(true);
			}
			if(row.getSpecimenNoTissueAvailable().keySet().size()>0){
				row.setNoTissueAvailable(true);
			}

		}
		row.anatomy = anatomy;
		return row;
	}


//	private ExpressionRowBean getExpressionCountForAnatomyTermFromImages(String anatomy,
//			ExpressionRowBean row, SolrDocument doc) {
//		// System.out.println("anatomy="+anatomy);
//		if (doc.containsKey(ImageDTO.PARAMETER_ASSOCIATION_VALUE)) {
//			List<String> paramAssNames = (List<String>) doc
//					.get(ImageDTO.PARAMETER_ASSOCIATION_NAME);
//			List<String> paramAssValues = (List<String>) doc
//					.get(ImageDTO.PARAMETER_ASSOCIATION_VALUE);
//			for (int i = 0; i < paramAssNames.size(); i++) {
//				String paramAssName = paramAssNames.get(i);
//				String paramAssValue = paramAssValues.get(i);
//				// System.out.println("paramAssName=" + paramAssName);
//				// System.out.println("paramAssValue=" + paramAssValue);
//				if (paramAssName.equalsIgnoreCase(anatomy)) {
//					if (paramAssValue.equalsIgnoreCase("expression")) {
//						// System.out.println("zyg="+(String)doc.get(ImageDTO.ZYGOSITY));
//						row.addSpecimenExpressed(
//								(String) doc.get(ImageDTO.EXTERNAL_SAMPLE_ID),
//								(String) doc.get(ImageDTO.ZYGOSITY));
//						// System.out.println("paramAssValue=" + paramAssValue);
//					} else if (paramAssValue.equalsIgnoreCase("ambiguous")) {
//						//row.setAmbiguousExpression(row.getAmbiguousExpression() + 1);
//						// row.setSpecimenAmbiguous(row.getSpecimenAmbiguous()+1);
//					} else if (paramAssValue.equalsIgnoreCase("no expression")) {
//						//row.setNotExpressed(row.getNotExpressed() + 1);
//						// row.setSpecimenNotExpressed(row.getSpecimenNotExpressed()+1);
//					}
//				}
//
//			}
//		}
//		return row;
//	}
	
	/**
	 * Are there hom images in this set (needed for the expression table on gene page
	 * @param anatomy
	 * @param row
	 * @param doc
	 * @return
	 */
	private ExpressionRowBean homImages(ExpressionRowBean row, SolrDocument doc) {
		// System.out.println("anatomy="+anatomy);
		if (doc.containsKey(ImageDTO.ZYGOSITY)) {
			if(doc.get(ImageDTO.ZYGOSITY).equals("homozygote")){
				row.setHomImages(true);
			}
		}
		return row;
	}
	
	private ExpressionRowBean getExpressionCountForAnatomyTerm(String anatomy,
			ExpressionRowBean row, SolrDocument doc) {
		// System.out.println("anatomy="+anatomy);
		if (doc.containsKey(ImageDTO.PARAMETER_NAME)) {
				String paramAssName = (String)doc.get(ObservationDTO.PARAMETER_NAME);
				String paramAssValue = (String)doc.get(ObservationDTO.CATEGORY);
				// System.out.println("paramAssName=" + paramAssName);
				// System.out.println("paramAssValue=" + paramAssValue);
				String sampleId=(String) doc.get(ImageDTO.EXTERNAL_SAMPLE_ID);
				String zyg=(String) doc.get(ImageDTO.ZYGOSITY);
				if (paramAssName.equalsIgnoreCase(anatomy)) {
					row.addSpecimen(sampleId,zyg);
					if (paramAssValue.equalsIgnoreCase("expression")) {
						row.addSpecimenExpressed(sampleId,zyg);
						//System.out.println("paramAssValue=" + paramAssValue);
					} else if (paramAssValue.equalsIgnoreCase("tissue not available")) {
						row.addNoTissueAvailable(sampleId, zyg);
						// row.setSpecimenAmbiguous(row.getSpecimenAmbiguous()+1);
					} else if (paramAssValue.equalsIgnoreCase("no expression")) {
						row.addNotExpressed(sampleId, zyg);
						// row.setSpecimenNotExpressed(row.getSpecimenNotExpressed()+1);
					}
				}

			}
		
		return row;
	}

	private Map<String, SolrDocumentList> getAnatomyToDocs(
			SolrDocumentList controlResponse) {
		Map<String, SolrDocumentList> anatomyToDocs = new HashMap<>();
		for (SolrDocument doc : controlResponse) {
			ArrayList<String> anatomies = (ArrayList<String>) doc
					.get(ImageDTO.PARAMETER_ASSOCIATION_NAME);
			// System.out.println("anatomies=" + anatomies);
			if (anatomies != null) {
				SolrDocumentList anatomyList = null;
				for (String anatomy : anatomies) {
					if (!anatomyToDocs.containsKey(anatomy)) {
						anatomyToDocs.put(anatomy, new SolrDocumentList());
					}
					anatomyList = anatomyToDocs.get(anatomy);
					// System.out.println("adding doc="+doc);
					anatomyList.add(doc);
				}
			}

		}
		return anatomyToDocs;
	}
	
	private Map<String, SolrDocumentList> getAnatomyToDocsForCategorical(
			SolrDocumentList response) {
		Map<String, SolrDocumentList> anatomyToDocs = new HashMap<>();
		for (SolrDocument doc : response) {
			if (doc.containsKey(ObservationDTO.OBSERVATION_TYPE)
					&& doc.get(ObservationDTO.OBSERVATION_TYPE).equals(
							"categorical")) {
				String anatomy = (String) doc.get(ImageDTO.PARAMETER_NAME);
				SolrDocumentList anatomyList = null;
				if (!anatomyToDocs.containsKey(anatomy)) {
					anatomyToDocs.put(anatomy, new SolrDocumentList());
				}
				// System.out.println("adding categorical anatomy "+anatomy);
				anatomyList = anatomyToDocs.get(anatomy);
				anatomyList.add(doc);
			}
		}
		return anatomyToDocs;
	}

	/**
	 * class for storing just the data needed for one row of the expression
	 * table on the gene page
	 * 
	 * @author jwarren
	 *
	 */
	public class ExpressionRowBean {
		String anatomy;
		boolean homImages = false;
		boolean wildTypeExpression=false;
		boolean expression=false;
		public boolean isExpression() {
			return expression;
		}

		public void setExpression(boolean expression) {
			this.expression = expression;
		}

		public boolean isNotExpressed() {
			return notExpressed;
		}

		public void setNotExpressed(boolean notExpressed) {
			this.notExpressed = notExpressed;
		}

		public boolean isNoTissueAvailable() {
			return noTissueAvailable;
		}

		public void setNoTissueAvailable(boolean noTissueAvailable) {
			this.noTissueAvailable = noTissueAvailable;
		}


		boolean notExpressed=false;
		boolean noTissueAvailable=false;
		


		private boolean imagesAvailable;
		
		public boolean isImagesAvailable() {
			return imagesAvailable;
		}

		public void setImagesAvailable(boolean b) {
			this.imagesAvailable=b;
		}


		Map<String, Specimen> specimenExpressed = new HashMap<>();
		Map<String, Specimen> specimen = new HashMap<>();


		public boolean isHomImages() {
			return homImages;
		}

		public void setHomImages(boolean homImages) {
			this.homImages = homImages;
		}

		public boolean isWildTypeExpression() {
			return wildTypeExpression;
		}

		public void setWildTypeExpression(boolean wildTypeExpression) {
			this.wildTypeExpression = wildTypeExpression;
		}

	
		int numberOfHetSpecimens;
		private Map<String, Specimen> specimenNotExpressed=new HashMap<>();;
		private Map<String, Specimen> specimenNoTissueAvailable=new HashMap<>();;

		public int getNumberOfHetSpecimens() {
			return numberOfHetSpecimens;
		}

		public void setNumberOfHetSpecimens(int numberOfHetSpecimens) {
			this.numberOfHetSpecimens = numberOfHetSpecimens;
		}

		
		public Map<String, Specimen> getSpecimenExpressed() {
			return specimenExpressed;
		}
		
		public Map<String, Specimen> getSpecimenNotExpressed() {
			return specimenNotExpressed;
		}
		
		public Map<String, Specimen> getSpecimenNoTissueAvailable() {
			return specimenNoTissueAvailable;
		}

		public void addSpecimenExpressed(String specimenId, String zygosity) {
			if (!this.getSpecimenExpressed().containsKey(specimenId)) {
				this.getSpecimenExpressed().put(specimenId, new Specimen());
			}
			Specimen specimen = this.getSpecimenExpressed().get(specimenId);
			specimen.setZyg(zygosity);
			this.specimenExpressed.put(specimenId, specimen);
		}
		
		public void addNotExpressed(String specimenId, String zygosity) {
			if (!this.getSpecimenNotExpressed().containsKey(specimenId)) {
				this.getSpecimenNotExpressed().put(specimenId, new Specimen());
			}
			Specimen specimen = this.getSpecimenNotExpressed().get(specimenId);
			specimen.setZyg(zygosity);
			this.specimenNotExpressed.put(specimenId, specimen);
		}

		public void addNoTissueAvailable(String specimenId, String zygosity) {
			if (!this.getSpecimenNoTissueAvailable().containsKey(specimenId)) {
				this.getSpecimenNoTissueAvailable().put(specimenId, new Specimen());
			}
			Specimen specimen = this.getSpecimenNoTissueAvailable().get(specimenId);
			specimen.setZyg(zygosity);
			this.specimenNoTissueAvailable.put(specimenId, specimen);
		}

		public void addSpecimen(String specimenId, String zygosity) {
			if (!this.getSpecimen().containsKey(specimenId)) {
				this.getSpecimen().put(specimenId, new Specimen());
			}
			Specimen specimen = this.getSpecimen().get(specimenId);
			specimen.setZyg(zygosity);
			this.specimen.put(specimenId, specimen);
		}

		public  Map<String, Specimen> getSpecimen() {
			return this.specimen;
		}

	}

	public class Specimen {

		private String zyg;

		public String getZyg() {
			return zyg;
		}

		public void setZyg(String zyg) {
			this.zyg = zyg;
		}


	}

}
