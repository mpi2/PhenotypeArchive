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
import uk.ac.ebi.phenotype.service.ImpressService.OntologyBean;
import uk.ac.ebi.phenotype.service.dto.ImageDTO;
import uk.ac.ebi.phenotype.service.dto.ObservationDTO;
import uk.ac.ebi.phenotype.solr.indexer.beans.OntologyTermBean;

import java.util.*;

import javax.annotation.PostConstruct;

public class ExpressionService {

	private final HttpSolrServer experimentSolr;
	private final HttpSolrServer imagesSolr;
	@Autowired
	ExperimentService experimentService;
	@Autowired
	ImpressService impressService;

	Map<String, OntologyBean> abnormalMaFromImpress = null;

	public ExpressionService(String experimentSolrUrl, String imagesSolrUrl) {

		experimentSolr = new HttpSolrServer(experimentSolrUrl);
		imagesSolr = new HttpSolrServer(imagesSolrUrl);
	}

	@PostConstruct
	private void initialiseAbnormalMaMap() {
		abnormalMaFromImpress = impressService
				.getParameterStableIdToAbnormalMaMap();
		
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
		// solrQuery.addFilterQuery(ImageDTO.ZYGOSITY
		// + ":Homozygote");
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
	private QueryResponse getCategoricalAdultLacZData(String mgiAccession,
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

	private QueryResponse getLaczFacetsForGene(String mgiAccession,
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
		
		List<FacetField> fields = laczResponse.getFacetFields();

		// we have the unique ma top level terms associated and all the images
		// now we need lists of images with these top level ma terms in their
		// annotation
		Map<String, SolrDocumentList> expFacetToDocs = new HashMap<>();
		String noTopMa = "No Top Level MA";
		expFacetToDocs.put(noTopMa, new SolrDocumentList());

		for (SolrDocument doc : imagesResponse) {
			ArrayList<String> tops = (ArrayList<String>) doc
					.get(ImageDTO.SELECTED_TOP_LEVEL_MA_TERM);

			if (tops == null) {
				expFacetToDocs.get(noTopMa).add(doc);
			} else {

				for (String top : tops) {
					SolrDocumentList list = null;
					if (!expFacetToDocs.containsKey(top)) {
						expFacetToDocs.put(top, new SolrDocumentList());
					}
					list = expFacetToDocs.get(top);
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
	public Model getExpressionDataForGene(String acc, Model model)
			throws SolrServerException {

		QueryResponse laczDataResponse = getCategoricalAdultLacZData(acc,
				ImageDTO.ZYGOSITY, ImageDTO.EXTERNAL_SAMPLE_ID,
				ObservationDTO.OBSERVATION_TYPE,
				ObservationDTO.PARAMETER_STABLE_ID,
				ObservationDTO.PARAMETER_NAME, ObservationDTO.CATEGORY,
				ObservationDTO.BIOLOGICAL_SAMPLE_GROUP);
		SolrDocumentList mutantCategoricalAdultLacZData = laczDataResponse
				.getResults();
		Map<String, SolrDocumentList> expressionAnatomyToDocs = getAnatomyToDocsForCategorical(mutantCategoricalAdultLacZData);
		Map<String, ExpressionRowBean> expressionAnatomyToRow = new TreeMap<>();
		Map<String, ExpressionRowBean> wtAnatomyToRow = new TreeMap<>();

		QueryResponse wtLaczDataResponse = getCategoricalAdultLacZData(null,
				ImageDTO.ZYGOSITY, ImageDTO.EXTERNAL_SAMPLE_ID,
				ObservationDTO.OBSERVATION_TYPE, ObservationDTO.PARAMETER_NAME,
				ObservationDTO.CATEGORY, ObservationDTO.BIOLOGICAL_SAMPLE_GROUP);
		SolrDocumentList wtCategoricalAdultLacZData = wtLaczDataResponse
				.getResults();
		Map<String, SolrDocumentList> wtAnatomyToDocs = getAnatomyToDocsForCategorical(wtCategoricalAdultLacZData);

		QueryResponse laczImagesResponse = null;

		laczImagesResponse = getExpressionTableDataImages(acc,
				ImageDTO.ZYGOSITY, ImageDTO.PARAMETER_ASSOCIATION_NAME,
				ObservationDTO.OBSERVATION_TYPE,
				ObservationDTO.BIOLOGICAL_SAMPLE_GROUP);
		SolrDocumentList imagesMutantResponse = laczImagesResponse.getResults();
		Map<String, ExpressionRowBean> mutantImagesAnatomyToRow = new TreeMap<>();
		Map<String, SolrDocumentList> mutantImagesAnatomyToDocs = getAnatomyToDocs(imagesMutantResponse);

		for (String anatomy : expressionAnatomyToDocs.keySet()) {
			
			ExpressionRowBean expressionRow = getAnatomyRow(anatomy,
					expressionAnatomyToDocs);
			int hetSpecimens = 0;
			for (String key : expressionRow.getSpecimen().keySet()) {
				
				if (expressionRow.getSpecimen().get(key).getZyg()
						.equalsIgnoreCase("heterozygote")) {
					hetSpecimens++;
				}
			}
			expressionRow.setNumberOfHetSpecimens(hetSpecimens);
			expressionAnatomyToRow.put(anatomy, expressionRow);

			ExpressionRowBean wtRow = getAnatomyRow(anatomy, wtAnatomyToDocs);

			if (wtRow.getSpecimenExpressed().keySet().size() > 0) {
				wtRow.setWildTypeExpression(true);
			}
			wtAnatomyToRow.put(anatomy, wtRow);

			ExpressionRowBean mutantImagesRow = getAnatomyRow(anatomy,
					mutantImagesAnatomyToDocs);
						mutantImagesRow.setNumberOfHetSpecimens(hetSpecimens);
			mutantImagesAnatomyToRow.put(anatomy, mutantImagesRow);

		}

		model.addAttribute("expressionAnatomyToRow", expressionAnatomyToRow);
		model.addAttribute("mutantImagesAnatomyToRow", mutantImagesAnatomyToRow);
		model.addAttribute("wtAnatomyToRow", wtAnatomyToRow);
		return model;

	}

	private ExpressionRowBean getAnatomyRow(String anatomy,
			Map<String, SolrDocumentList> anatomyToDocs) {

		ExpressionRowBean row = new ExpressionRowBean();
		if (anatomyToDocs.containsKey(anatomy)) {
			
			for (SolrDocument doc : anatomyToDocs.get(anatomy)) {

				if (doc.containsKey(ObservationDTO.OBSERVATION_TYPE)
						&& doc.get(ObservationDTO.OBSERVATION_TYPE).equals(
								"categorical")) {
					
					if (doc.containsKey(ImageDTO.PARAMETER_STABLE_ID)
							&& row.getParameterStableId() == null) {
						String parameterStableId = (String) doc
								.get(ImageDTO.PARAMETER_STABLE_ID);
						row.setParameterStableId(parameterStableId);
						OntologyBean ontologyBean = abnormalMaFromImpress
								.get(parameterStableId);
						
						if (ontologyBean != null) {

							row.setAbnormalMaId(ontologyBean.getMaId());
							row.setMaName(StringUtils.capitalize(ontologyBean.getName()));
						} else {
							System.out.println("no ma id for anatomy term="
									+ anatomy);
						}
					}
					row = getExpressionCountForAnatomyTerm(anatomy, row, doc);
				} else if (doc.get(ObservationDTO.OBSERVATION_TYPE).equals(
						"image_record")
						&& doc.get(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP)
								.equals("experimental")) {// assume image with
															// parameterAssociation
					row = homImages(row, doc);
					row.setImagesAvailable(true);
					row.setNumberOfImages(row.getNumberOfImages()+1);
				}
			}
			if (row.getSpecimenExpressed().keySet().size() > 0) {
				row.setExpression(true);
			}
			if (row.getSpecimenNotExpressed().keySet().size() > 0) {
				row.setNotExpressed(true);
			}
			if (row.getSpecimenNoTissueAvailable().keySet().size() > 0) {
				row.setNoTissueAvailable(true);
			}

		}
		row.anatomy = anatomy;
		return row;
	}


	/**
	 * Are there hom images in this set (needed for the expression table on gene
	 * page
	 * 
	 * @param anatomy
	 * @param row
	 * @param doc
	 * @return
	 */
	private ExpressionRowBean homImages(ExpressionRowBean row, SolrDocument doc) {
		
		if (doc.containsKey(ImageDTO.ZYGOSITY)) {
			if (doc.get(ImageDTO.ZYGOSITY).equals("homozygote")) {
				row.setHomImages(true);
			}
		}
		return row;
	}

	private ExpressionRowBean getExpressionCountForAnatomyTerm(String anatomy,
			ExpressionRowBean row, SolrDocument doc) {
		
		if (doc.containsKey(ImageDTO.PARAMETER_NAME)) {
			String paramAssName = (String) doc
					.get(ObservationDTO.PARAMETER_NAME);
			String paramAssValue = (String) doc.get(ObservationDTO.CATEGORY);
			
			String sampleId = (String) doc.get(ImageDTO.EXTERNAL_SAMPLE_ID);
			String zyg = (String) doc.get(ImageDTO.ZYGOSITY);
			if (paramAssName.equalsIgnoreCase(anatomy)) {
				row.addSpecimen(sampleId, zyg);
				if (paramAssValue.equalsIgnoreCase("expression")) {
					row.addSpecimenExpressed(sampleId, zyg);
					
				} else if (paramAssValue
						.equalsIgnoreCase("tissue not available")) {
					row.addNoTissueAvailable(sampleId, zyg);
					
				} else if (paramAssValue.equalsIgnoreCase("no expression")) {
					row.addNotExpressed(sampleId, zyg);
					
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
			
			if (anatomies != null) {
				SolrDocumentList anatomyList = null;
				for (String anatomy : anatomies) {
					if (!anatomyToDocs.containsKey(anatomy)) {
						anatomyToDocs.put(anatomy, new SolrDocumentList());
					}
					anatomyList = anatomyToDocs.get(anatomy);
					
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
		@Override
		public String toString() {
			return "ExpressionRowBean [anatomy=" + anatomy + ", abnormalMaId="
					+ abnormalMaId + ", numberOfImages=" + numberOfImages
					+ ", parameterStableId=" + parameterStableId
					+ ", abnormalMaName=" + abnormalMaName + ", homImages="
					+ homImages + ", wildTypeExpression=" + wildTypeExpression
					+ ", expression=" + expression + ", notExpressed="
					+ notExpressed + ", noTissueAvailable=" + noTissueAvailable
					+ ", imagesAvailable=" + imagesAvailable
					+ ", specimenExpressed=" + specimenExpressed
					+ ", specimen=" + specimen + ", numberOfHetSpecimens="
					+ numberOfHetSpecimens + ", specimenNotExpressed="
					+ specimenNotExpressed + ", specimenNoTissueAvailable="
					+ specimenNoTissueAvailable + "]";
		}



		String anatomy;
		String abnormalMaId;
		private int numberOfImages;
		public int getNumberOfImages() {
			return numberOfImages;
		}

		public String getAbnormalMaId() {
			return abnormalMaId;
		}

		public void setNumberOfImages(int numberOfImages) {
			this.numberOfImages=numberOfImages;
			
		}

		public void setAbnormalMaId(String abnormalMaId) {
			this.abnormalMaId = abnormalMaId;
		}



		private String parameterStableId;
		private String abnormalMaName;

		public String getAbnormalMaName() {
			return abnormalMaName;
		}

		public void setAbnormalMaName(String abnormalMaName) {
			this.abnormalMaName = abnormalMaName;
		}

		public String getParameterStableId() {
			return parameterStableId;
		}

		public void setMaName(String abnormalMaName) {
			this.abnormalMaName=abnormalMaName;
			
		}

		

		public void setParameterStableId(String parameterStableId) {
			this.parameterStableId = parameterStableId;
		}

		

		boolean homImages = false;
		boolean wildTypeExpression = false;
		boolean expression = false;

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

		boolean notExpressed = false;
		boolean noTissueAvailable = false;

		private boolean imagesAvailable;

		public boolean isImagesAvailable() {
			return imagesAvailable;
		}

		public void setImagesAvailable(boolean b) {
			this.imagesAvailable = b;
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
		private Map<String, Specimen> specimenNotExpressed = new HashMap<>();;
		private Map<String, Specimen> specimenNoTissueAvailable = new HashMap<>();;

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
				this.getSpecimenNoTissueAvailable().put(specimenId,
						new Specimen());
			}
			Specimen specimen = this.getSpecimenNoTissueAvailable().get(
					specimenId);
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

		public Map<String, Specimen> getSpecimen() {
			return this.specimen;
		}
		
		

	}

	public class Specimen {

		@Override
		public String toString() {
			return "Specimen [zyg=" + zyg + "]";
		}

		private String zyg;

		public String getZyg() {
			return zyg;
		}

		public void setZyg(String zyg) {
			this.zyg = zyg;
		}

	}

}
