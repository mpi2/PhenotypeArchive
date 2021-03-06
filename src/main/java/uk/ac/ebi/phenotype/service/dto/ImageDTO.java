/*******************************************************************************
 * Copyright 2015 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 *******************************************************************************/
package uk.ac.ebi.phenotype.service.dto;

import org.apache.solr.client.solrj.beans.Field;

import java.util.ArrayList;
import java.util.List;

public class ImageDTO extends ObservationDTO {
	
	

	public static final String FULL_RESOLUTION_FILE_PATH="full_resolution_file_path";

	public static final String OMERO_ID = "omero_id";
	public static final String DOWNLOAD_URL = "download_url";
	public static final String JPEG_URL = "jpeg_url";
	public static final String IMAGE_LINK = "image_link";
	public static final String MA_ID = "ma_id";
	public static final String MA_TERM = "ma_term";
	public static final String MA_ID_TERM = "ma_id_term";
	public static final String MA_TERM_SYNONYM = "ma_term_synonym";
	public static final String SELECTED_TOP_LEVEL_MA_ID = "selected_top_level_ma_id";
	public static final String SELECTED_TOP_LEVEL_MA_TERM = "selected_top_level_ma_term";
	public static final String SELECTED_TOP_LEVEL_MA_TERM_SYNONYM = "selected_top_level_ma_term_synonym";
	public static final String SYMBOL_GENE = "symbol_gene";
	public static final String SYMBOL = "symbol";
	public static final String SUBTYPE = "subtype";	
	public static final String STATUS = "status";	
	public static final String IMITS_PHENOTYPE_STARTED = SangerImageDTO.IMITS_PHENOTYPE_STARTED;	
	public static final String IMITS_PHENOTYPE_COMPLETE = SangerImageDTO.IMITS_PHENOTYPE_COMPLETE;	
	public static final String IMITS_PHENOTYPE_STATUS = SangerImageDTO.IMITS_PHENOTYPE_STATUS;	
	public static final String LEGACY_PHENOTYPE_STATUS = AlleleDTO.LEGACY_PHENOTYPE_STATUS;
	public static final String LATEST_PRODUCTION_CENTRE = SangerImageDTO.LATEST_PRODUCTION_CENTRE;	
	public static final String LATEST_PHENOTYPING_CENTRE = SangerImageDTO.LATEST_PHENOTYPING_CENTRE;	
	public static final String ALLELE_NAME = SangerImageDTO.ALLELE_NAME;	
	public static final String MARKER_SYMBOL = SangerImageDTO.MARKER_SYMBOL;	
	public static final String MARKER_NAME = SangerImageDTO.MARKER_NAME;	
	public static final String MARKER_SYNONYM = SangerImageDTO.MARKER_SYNONYM;	
	public static final String MARKER_TYPE = SangerImageDTO.MARKER_TYPE;		
	public static final String GENE_ACCESSION_ID = ObservationDTO.GENE_ACCESSION_ID;	
	

	public static final String HUMAN_GENE_SYMBOL = SangerImageDTO.HUMAN_GENE_SYMBOL;
	public static final String LATEST_PHENOTYPE_STATUS = AlleleDTO.LATEST_PHENOTYPE_STATUS;

	public static final String INTERMEDIATE_LEVEL_MA_TERM_ID = "intermediate_ma_term_id";
	public static final String INTERMEDIATE_LEVEL_MA_TERM = "intermediate_ma_term";
	public static final String INTERMEDIATE_LEVEL_MA_TERM_SYNONYM = "intermediate_ma_term_synonym";
	
	@Field(FULL_RESOLUTION_FILE_PATH)
	private String fullResolutionFilePath;
	
	@Field(OMERO_ID)
	private int omeroId;
	
	@Field(DOWNLOAD_URL)
	private String downloadUrl;
	
	@Field(IMAGE_LINK)
	private String imageLink;

	@Field(JPEG_URL)
	private String jpegUrl;
	
	@Field(MA_ID)
	private List<String> maTermId;
	
	@Field(MA_TERM)
	private List<String> maTerm;
	
	@Field(MA_ID_TERM)
	private List<String> maIdTerm;
	
	@Field(MA_TERM_SYNONYM)
	private List<String> maTermSynonym;

	@Field(SELECTED_TOP_LEVEL_MA_ID)
	private List<String> topLevelMaIds;

	@Field(SELECTED_TOP_LEVEL_MA_TERM)
	private List<String> topLeveMaTerm;
	
	@Field(SELECTED_TOP_LEVEL_MA_TERM_SYNONYM)
	private List<String> topLevelMaTermSynonym;

	@Field(SYMBOL_GENE)
	private String symbolGene;//for search and annotation view	
	
	@Field(STATUS)
	private List<String> status;
	
	@Field(IMITS_PHENOTYPE_STARTED)
	private List<String> imitsPhenotypeStarted;
	
	@Field(IMITS_PHENOTYPE_COMPLETE)
	private List<String> imitsPhenotypeComplete;
	
	@Field(IMITS_PHENOTYPE_STATUS)
	private List<String> imitsPhenotypeStatus;
	
	@Field(LEGACY_PHENOTYPE_STATUS)
	private Integer legacyPhenotypeStatus;	

	@Field(LATEST_PRODUCTION_CENTRE)
	private List<String> latestProductionCentre;
	
	@Field(LATEST_PHENOTYPING_CENTRE)
	private List<String> latestPhenotypingCentre;
	
	@Field(ALLELE_NAME)
	private List<String> alleleName;
	
	@Field(MARKER_SYMBOL)
	private List<String> markerSymbol;
	
	@Field(MARKER_NAME)
	private List<String> markerName;
	
	@Field(MARKER_SYNONYM)
	private List<String> markerSynonym;
	
	@Field(MARKER_TYPE)
	private List<String> markerType;
	
	@Field(HUMAN_GENE_SYMBOL)
	private List<String> humanGeneSymbol;
	
	@Field(SYMBOL)
	private String symbol;
	
	@Field(SUBTYPE)
	private String subtype;
	
	@Field(LATEST_PHENOTYPE_STATUS)
	private List<String> latestPhenotypeStatus;

	@Field(INTERMEDIATE_LEVEL_MA_TERM_ID)
	private ArrayList<String> intermediateLevelMaId;


	@Field(INTERMEDIATE_LEVEL_MA_TERM)
	private ArrayList<String> intermediateLevelMaTerm;
	@Field(INTERMEDIATE_LEVEL_MA_TERM_SYNONYM)
	private ArrayList<String> intermediateLevelMaTermSynonym;

	
	public String getImageLink() {
		return imageLink;
	}


	public void setImageLink(String imageLink) {
		this.imageLink = imageLink;
	}
	
	public String getSubtype() {
	
		return subtype;
	}


	public List<String> getMarkerName() {

		return markerName;
	}
	
	public void setMarkerName(List<String> markerName) {

		this.markerName = markerName;
	}


	public List<String> getMarkerSymbol() {

		return markerSymbol;
	}


	public void setMarkerSymbol(List<String> markerSymbol) {

		this.markerSymbol = markerSymbol;
	}

	public List<String> getMarkerSynonym() {

		return markerSynonym;
	}


	public void setMarkerSynonym(List<String> markerSynonym) {

		this.markerSynonym = markerSynonym;
	}
	
	
	public void setSubtype(String subtype) {
	
		this.subtype = subtype;
	}




	public String getSymbolGene() {
		if((this.getGeneSymbol()!=null)&&(this.getGeneAccession()!=null)){
			this.symbolGene=this.getGeneSymbol()+"_"+this.getAlleleAccession();
			}
		return this.symbolGene;
	}




	public List<String> getMaTermId() {
	
		return maTermId;
	}

	public void setMaTermId(List<String> maTermId) {
	
		this.maTermId = maTermId;
	}

	public List<String> getMaIdTerm() {
		
		return maIdTerm;
	}
	
	public void setMaIdTerm(List<String> maIdTerms) {
		
		this.maIdTerm = maIdTerms;
	}
	

	public String getDownloadUrl() {
	
		return downloadUrl;
	}



	public int getOmeroId() {
	
		return omeroId;
	}


	
	public void setOmeroId(int omeroId) {
	
		this.omeroId = omeroId;
	}


	public String getFullResolutionFilePath() {
	
		return fullResolutionFilePath;
	}

	
	public void setFullResolutionFilePath(String fullResolutionFilePath) {
	
		this.fullResolutionFilePath = fullResolutionFilePath;
	}

	public ImageDTO(){
		super();
	}



	public void setDownloadUrl(String downloadUrl) {

		this.downloadUrl=downloadUrl;
		
	}



	public void setJpegUrl(String jpegUrl) {

		this.jpegUrl=jpegUrl;
		
	}
	
	public String getJpegUrl() {
		
		return jpegUrl;
	}


	public void addStatus(String status1) {

		if(this.status==null){
			status=new ArrayList<String>();
		}
		status.add(status1);
		
	}
	public List<String> getStatus() {
		return status;
	}



	public void addImitsPhenotypeStarted(String imitsPhenotypeStarted1) {

		if(this.imitsPhenotypeStarted==null){
			this.imitsPhenotypeStarted=new ArrayList<String>();
		}
		this.imitsPhenotypeStarted.add(imitsPhenotypeStarted1);
		
	}

	public List<String> getImitsPhenotypeStarted() {
		return imitsPhenotypeStarted;
	}


	public void addImitsPhenotypeComplete(String imitsPhenotypeComplete1) {

		if(this.imitsPhenotypeComplete==null){
			this.imitsPhenotypeComplete=new ArrayList<String>();
		}
		this.imitsPhenotypeComplete.add(imitsPhenotypeComplete1);
		
	}

	public List<String> getImitsPhenotypeComplete() {
		return imitsPhenotypeComplete;
	}


	public void addImitsPhenotypeStatus(String imitsPhenotypeStatus1) {

		if(this.imitsPhenotypeStatus==null){
			this.imitsPhenotypeStatus=new ArrayList<String>();
		}
		this.imitsPhenotypeStatus.add(imitsPhenotypeStatus1);
		
	}

	public List<String> getImitsPhenotypeStatus() {
		return imitsPhenotypeStatus;
	}



	public void setLegacyPhenotypeStatus(Integer legacyPhenotypeStatus) {

		this.legacyPhenotypeStatus=legacyPhenotypeStatus;
		
	}

	public Integer getLegacyPhenotypeStatus() {
		
		return legacyPhenotypeStatus;
	}



	public void setLatestProductionCentre(List<String> latestProductionCentre) {

		this.latestProductionCentre=latestProductionCentre;
		
	}

	public List<String> getLatestProductionCentre() {
		return latestProductionCentre;
	}


	public void setLatestPhenotypingCentre(List<String> latestPhenotypingCentre) {

		this.latestPhenotypingCentre=latestPhenotypingCentre;
		
	}

	public List<String> getLatestPhenotypingCentre() {
		return latestPhenotypingCentre;
	}



	public void setAlleleName(List<String> alleleName) {
		this.alleleName=alleleName;
	}

	public List<String> getAlleleName() {
		return alleleName;
	}


	public void addMarkerName(String markerName) {
		if(this.markerName==null){
			this.markerName=new ArrayList<>();
		}
		this.markerName.add(markerName);
		
		
	}




	public void addMarkerSynonym(List<String> markerSynonym) {
		if(this.markerSynonym==null){
			this.markerSynonym=new ArrayList<>();
		}
		this.markerSynonym.addAll(markerSynonym);
		
	}




	public void addMarkerType(List<String> markerType) {

		this.markerType=markerType;
		
	}

	public List<String> getMarkerType() {

		return markerType;
		
	}


	public void addHumanGeneSymbol(List<String> humanGeneSymbol) {

		if(this.humanGeneSymbol==null){
			this.humanGeneSymbol=new ArrayList<String>();
		}
		this.humanGeneSymbol.addAll(humanGeneSymbol);
		
	}

	public List<String> getHumanGeneSymbol() {
		return humanGeneSymbol;
	}

	public void addSymbol(String markerName) {

		this.symbol=markerName;
		
	}


	


	public void setLatestPhenotypeStatus(List<String> latestPhenotypeStatus) {

		this.latestPhenotypeStatus=latestPhenotypeStatus;
		
	}
	
	public void addLatestPhenotypeStatus(String latestPhenotypeStatus) {
		if(this.latestPhenotypeStatus==null){
			this.latestPhenotypeStatus=new ArrayList<String>();
		}
		this.latestPhenotypeStatus.add(latestPhenotypeStatus);
		
	}
	public List<String> getLatestPhenotypeStatus() {
		return latestPhenotypeStatus;
	}
	
	

	public String getSymbol() {
		// TODO Auto-generated method stub
		return symbol;
	}


	public void setSymbolGene(String symbolGene) {
		this.symbolGene=symbolGene;
		
	}

	
	public List<String> getMaTerm() {
		return maTerm;
	}


	public void setMaTerm(List<String> maTerm) {
		this.maTerm = maTerm;
	}
	
	public List<String> getMaTermSynonym() {
		return maTermSynonym;
	}


	public void setMaTermSynonym(List<String> maTermSynonym) {
		this.maTermSynonym = maTermSynonym;
	}


	public void setTopLevelMaId(ArrayList<String> topLevelMaIds) {
		this.topLevelMaIds=topLevelMaIds;
		
	}


	public void setTopLevelMaTerm(ArrayList<String> topLevelMaTerm) {
		this.topLeveMaTerm=topLevelMaTerm;
		
	}


	public void setTopLevelMaTermSynonym(ArrayList<String> topLevelMaTermSynonym) {
		this.topLevelMaTermSynonym=topLevelMaTermSynonym;
	}
	
	public List<String> getTopLevelMaIds() {
		return topLevelMaIds;
	}


	public List<String> getTopLeveMaTerm() {
		return topLeveMaTerm;
	}


	public List<String> getTopLevelMaTermSynonym() {
		return topLevelMaTermSynonym;
	}

	public String getExpression(String maId){
		
		int pos = maTermId.indexOf(maId);
		return getParameterAssociationValue().get(pos);
		
	}


	public void setIntermediateLevelMaId(ArrayList<String> intermediateLevelMaId) {
		this.intermediateLevelMaId=intermediateLevelMaId;
		
	}


	public void setIntermediateLevelMaTerm(ArrayList<String> intermediateLevelMaTerm) {
		this.intermediateLevelMaTerm=intermediateLevelMaTerm;
		
	}


	public void setIntermediateLevelMaTermSynonym(
			ArrayList<String> intermediateLevelMaTermSynonym) {
		this.intermediateLevelMaTermSynonym=intermediateLevelMaTermSynonym;
		
	}
	
	public static String getIntermediateLevelMaTermId() {
		return INTERMEDIATE_LEVEL_MA_TERM_ID;
	}


	public static String getIntermediateLevelMaTerm() {
		return INTERMEDIATE_LEVEL_MA_TERM;
	}


	public static String getIntermediateLevelMaTermSynonym() {
		return INTERMEDIATE_LEVEL_MA_TERM_SYNONYM;
	}
	
}
