package uk.ac.ebi.phenotype.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.ui.Model;

import uk.ac.ebi.phenotype.imaging.utils.ImageServiceUtil;
import uk.ac.ebi.phenotype.pojo.SexType;
import uk.ac.ebi.phenotype.service.dto.ImageDTO;
import uk.ac.ebi.phenotype.service.dto.ObservationDTO;

public class ExpressionService {

	private final HttpSolrServer solr;
	
	public ExpressionService(String solrUrl) {

		solr = new HttpSolrServer(solrUrl);
	}
	
	
	public QueryResponse getExpressionImagesForGeneByAnatomy(String mgiAccession,
			String anatomy, String experimentOrControl,
			int numberOfImagesToRetrieve, SexType sex, String metadataGroup,
			String strain) throws SolrServerException {

		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery("gene_accession_id:\"" + mgiAccession + "\"");
		solrQuery.addFilterQuery(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":"
				+ experimentOrControl);
		if (StringUtils.isNotEmpty(metadataGroup)) {
			solrQuery.addFilterQuery(ObservationDTO.METADATA_GROUP + ":"
					+ metadataGroup);
		}
		if (StringUtils.isNotEmpty(strain)) {
			solrQuery.addFilterQuery(ObservationDTO.STRAIN_NAME + ":" + strain);
		}
		if (sex != null) {
			solrQuery.addFilterQuery("sex:" + sex.name());
		}
		if (StringUtils.isNotEmpty(anatomy)) {
			solrQuery.addFilterQuery(ObservationDTO.PARAMETER_ASSOCIATION_VALUE + ":"
					+ anatomy);
		}
		// solrQuery.addFilterQuery(ObservationDTO.PROCEDURE_NAME + ":\"" +
		// procedure_name + "\"");
		solrQuery.setRows(numberOfImagesToRetrieve);
		QueryResponse response = solr.query(solrQuery);
		return response;
	}
	/**
	 * 
	 * @param mgiAccession
	 *            if mgi accesion null assume a request fro control data
	 * @param fields
	 * @return
	 * @throws SolrServerException
	 */
	public QueryResponse getExpressionTableData(String mgiAccession,
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
		// solrQuery.setFacetMinCount(1);
		// solrQuery.setFacet(true);
		solrQuery.setFields(fields);
		// solrQuery.addFacetField("ma_term");
		solrQuery.setRows(100000);
		QueryResponse response = solr.query(solrQuery);
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
		QueryResponse response = solr.query(solrQuery);
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
	public void getLacDataForGene(String acc, String topMaNameFilter,
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
		System.out.println("lacZimages found=" + imagesResponse.getNumFound());
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
	public void getExpressionDataForGene(String acc, Model model)
			throws SolrServerException {
		QueryResponse laczResponse = null;

		laczResponse = getExpressionTableData(acc, ImageDTO.OMERO_ID,
				ImageDTO.JPEG_URL, ImageDTO.SELECTED_TOP_LEVEL_MA_TERM,
				ImageDTO.PARAMETER_ASSOCIATION_NAME,
				ImageDTO.PARAMETER_ASSOCIATION_VALUE, ImageDTO.ZYGOSITY,
				ImageDTO.SEX, ImageDTO.ALLELE_SYMBOL, ImageDTO.DOWNLOAD_URL,
				ImageDTO.IMAGE_LINK, ImageDTO.BIOLOGICAL_SAMPLE_GROUP, ImageDTO.EXTERNAL_SAMPLE_ID);

		QueryResponse laczControlResponse = null;

		laczControlResponse = getExpressionTableData(null, ImageDTO.OMERO_ID,
				ImageDTO.JPEG_URL, ImageDTO.SELECTED_TOP_LEVEL_MA_TERM,
				ImageDTO.PARAMETER_ASSOCIATION_NAME,
				ImageDTO.PARAMETER_ASSOCIATION_VALUE, ImageDTO.ZYGOSITY,
				ImageDTO.SEX, ImageDTO.ALLELE_SYMBOL, ImageDTO.DOWNLOAD_URL,
				ImageDTO.IMAGE_LINK, ImageDTO.BIOLOGICAL_SAMPLE_GROUP, ImageDTO.EXTERNAL_SAMPLE_ID);

		SolrDocumentList controlResponse = laczControlResponse.getResults();
		System.out.println("Controls data found="
				+ controlResponse.getNumFound());
		SolrDocumentList mutantResponse = laczResponse.getResults();
		System.out.println("Expression data found="
				+ mutantResponse.getNumFound());
		Map<String, ExpressionRowBean> mutantAnatomyToRow = new TreeMap<>();
		Map<String, ExpressionRowBean> controlAnatomyToRow = new TreeMap<String, ExpressionRowBean>();
		
		Map<String, SolrDocumentList> controlAnatomyToDocs = getAnatomyToDocs(controlResponse);
		Map<String, SolrDocumentList> mutantAnatomyToDocs = getAnatomyToDocs(mutantResponse);
		// now we have the docs seperated by anatomy terms lets get the data
		// needed for the table
		// should web be looking at experiment core? Are there expression
		// parameters with no image??? Looks like there are 100 more from this
		// query
		// http://ves-ebi-d0.ebi.ac.uk:8090/mi/impc/dev/solr/impc_images/select?q=*:*&facet=true&facet.field=ma_term&facet.mincount=1&fq=(parameter_name:%22LacZ%20Images%20Section%22%20OR%20parameter_name:%22LacZ%20Images%20Wholemount%22)
		// vs
		// http://ves-ebi-d0.ebi.ac.uk:8090/mi/impc/dev/solr/experiment/select?q=*:*&facet=true&facet.field=ma_term&facet.mincount=1&fq=(parameter_name:%22LacZ%20Images%20Section%22%20OR%20parameter_name:%22LacZ%20Images%20Wholemount%22)
		for (String anatomy : mutantAnatomyToDocs.keySet()) {
			System.out.println("getting controls");
			ExpressionRowBean controlRow = getAnatomyRow(anatomy,
					controlAnatomyToDocs);
			System.out.println(controlRow);
			if (controlRow.getExpressed() > 0) {
				controlRow.setWildTypeExpression(true);
			}
			System.out.println("getting mutants");
			ExpressionRowBean mutantRow = getAnatomyRow(anatomy,
					mutantAnatomyToDocs);
			controlAnatomyToRow.put(anatomy, controlRow);
			int hetSpecimens=0;
			for(String key: mutantRow.getSpecimenExpressed().keySet()){
				System.out.println("specimen key="+key);
				if(mutantRow.getSpecimenExpressed().get(key).getZyg().equalsIgnoreCase("heterozygote")){
					hetSpecimens++;
				};
			}
			mutantRow.setNumberOfHetSpecimens(hetSpecimens);
			mutantAnatomyToRow.put(anatomy, mutantRow);

		}
		
		model.addAttribute("mutantAnatomyToRow", mutantAnatomyToRow);
		model.addAttribute("controlAnatomyToRow", controlAnatomyToRow);

	}

	private ExpressionRowBean getAnatomyRow(String anatomy,
			Map<String, SolrDocumentList> anatomyToDocs) {
		int hets=0; int homs=0; 
		ExpressionRowBean row = new ExpressionRowBean();
		if (anatomyToDocs.containsKey(anatomy)) {
			for (SolrDocument doc : anatomyToDocs.get(anatomy)) {
				if (doc.containsKey(ImageDTO.ZYGOSITY)) {
					String zyg = (String) doc.get(ImageDTO.ZYGOSITY);
					if (zyg.equalsIgnoreCase("homozygote")) {
						homs++;
					}

					if (zyg.equalsIgnoreCase("heterozygote")) {
						hets++;
					}

				}
			

			row = getExpressionCountForAnatomyTerm(anatomy,
					row, doc);
			}

		}
		row.anatomy = anatomy;
		if (homs > 0) {
			row.homImages = true;
		}
		row.numberOfHet = hets;

		return row;
	}

	private ExpressionRowBean getExpressionCountForAnatomyTerm(String anatomy,
			ExpressionRowBean row, SolrDocument doc) {
		//System.out.println("anatomy="+anatomy);
		if (doc.containsKey(ImageDTO.PARAMETER_ASSOCIATION_VALUE)) {
			List<String> paramAssNames = (List<String>) doc
					.get(ImageDTO.PARAMETER_ASSOCIATION_NAME);
			List<String> paramAssValues = (List<String>) doc
					.get(ImageDTO.PARAMETER_ASSOCIATION_VALUE);
			for (int i = 0; i < paramAssNames.size(); i++) {
				String paramAssName = paramAssNames.get(i);
				String paramAssValue = paramAssValues.get(i);
				//System.out.println("paramAssName=" + paramAssName);
				//System.out.println("paramAssValue=" + paramAssValue);
				if (paramAssName.equalsIgnoreCase(anatomy)) {
					if (paramAssValue.equalsIgnoreCase("expression")) {

						row.setExpression(true);
						//System.out.println("row get expressed="+row.getExpressed());
						row.setExpressed(row.getExpressed()+1);
						//System.out.println("zyg="+(String)doc.get(ImageDTO.ZYGOSITY));
						row.addSpecimenExpressed((String)doc.get(ImageDTO.EXTERNAL_SAMPLE_ID),(String)doc.get(ImageDTO.ZYGOSITY),  1);
						//System.out.println("paramAssValue=" + paramAssValue);
					}
					else if(paramAssValue.equalsIgnoreCase("ambiguous")){
						row.setAmbiguousExpression(row.getAmbiguousExpression()+1);
						//row.setSpecimenAmbiguous(row.getSpecimenAmbiguous()+1);
					}
					else if(paramAssValue.equalsIgnoreCase("no expression")){
						row.setNotExpressed(row.getNotExpressed()+1);
						//row.setSpecimenNotExpressed(row.getSpecimenNotExpressed()+1);
					}
				}

			}
		}
		return row;
	}

	private Map<String, SolrDocumentList> getAnatomyToDocs(
			SolrDocumentList controlResponse) {
		Map<String, SolrDocumentList> anatomyToDocs = new HashMap<>();
		for (SolrDocument doc : controlResponse) {
			ArrayList<String> tops = (ArrayList<String>) doc
					.get(ImageDTO.SELECTED_TOP_LEVEL_MA_TERM);
			ArrayList<String> anatomies = (ArrayList<String>) doc
					.get(ImageDTO.PARAMETER_ASSOCIATION_NAME);
			//System.out.println("anatomies=" + anatomies);
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

	/**
	 * class for storing just the data needed for one row of the expression
	 * table on the gene page
	 * 
	 * @author jwarren
	 *
	 */
	public class ExpressionRowBean {
		String anatomy;

		public int getNumberOfHet() {
			return numberOfHet;
		}

		public void setNumberOfHet(int numberOfHet) {
			this.numberOfHet = numberOfHet;
		}

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

		public boolean isExpression() {
			return expression;
		}

		public void setExpression(boolean mutantExpression) {
			this.expression = mutantExpression;
		}

		public int getExpressed() {
			return expressed;
		}

		public void setExpressed(int mutantsExpressed) {
			this.expressed = mutantsExpressed;
		}

		int numberOfHet;
		int numberOfHetSpecimens;
		
		public int getNumberOfHetSpecimens() {
			return numberOfHetSpecimens;
		}

		public void setNumberOfHetSpecimens(int numberOfHetSpecimens) {
			this.numberOfHetSpecimens = numberOfHetSpecimens;
		}

		boolean homImages = false;
		boolean wildTypeExpression;
		boolean expression;
		int expressed;
		int notExpressed;
		int ambiguousExpression;
		Map<String, Specimen> specimenExpressed=new HashMap<>();
		
		public int getAmbiguousExpression() {
			return ambiguousExpression;
		}

		public void setAmbiguousExpression(int ambiguousExpression) {
			this.ambiguousExpression = ambiguousExpression;
		}

		public Map<String, Specimen> getSpecimenExpressed() {
			return specimenExpressed;
		}

		public void addSpecimenExpressed(String specimenId, String zygosity, int i) {
			if(!this.getSpecimenExpressed().containsKey(specimenId)){
				this.getSpecimenExpressed().put(specimenId,new Specimen());
			}
			Specimen specimen=this.getSpecimenExpressed().get(specimenId);
			specimen.setNumberOfExpressionImagesForSpecimen(specimen.getNumberOfExpressionImagesForSpecimen()+i);
			specimen.setZyg(zygosity);
			this.specimenExpressed.put(specimenId, specimen);
		}

		public int getSpecimenNotExpressed() {
			return specimenNotExpressed;
		}

		public void setSpecimenNotExpressed(int specimenNotExpressed) {
			this.specimenNotExpressed = specimenNotExpressed;
		}

		public int getSpecimenAmbiguous() {
			return specimenAmbiguous;
		}

		public void setSpecimenAmbiguous(int specimenAmbiguous) {
			this.specimenAmbiguous = specimenAmbiguous;
		}

		int specimenNotExpressed;
		int specimenAmbiguous;

		public int getNotExpressed() {
			return notExpressed;
		}

		public void setNotExpressed(int notExpressed) {
			this.notExpressed = notExpressed;
		}

		public int getTotal() {
			return expressed + notExpressed+ambiguousExpression;
		}

	}
	
	public class Specimen{
		
		private String zyg;
		public String getZyg() {
			return zyg;
		}
		public void setZyg(String zyg) {
			this.zyg = zyg;
		}
		
		private Integer numberOfExpressionImagesForSpecimen;

		public Integer getNumberOfExpressionImagesForSpecimen() {
			return numberOfExpressionImagesForSpecimen;
		}
		public void setNumberOfExpressionImagesForSpecimen(
				Integer numberOfExpressionImagesForSpecimen) {
			this.numberOfExpressionImagesForSpecimen = numberOfExpressionImagesForSpecimen;
		}
		public Specimen(){
			this.zyg="none set";
			numberOfExpressionImagesForSpecimen=new Integer(0);
		}
		public Specimen(String zyg, Integer numberOfSpecimens){
			this.zyg=zyg;
			this.numberOfExpressionImagesForSpecimen=numberOfSpecimens;
		}
		
	}

}
