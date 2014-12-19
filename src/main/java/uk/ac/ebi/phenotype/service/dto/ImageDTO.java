package uk.ac.ebi.phenotype.service.dto;

import org.apache.solr.client.solrj.beans.Field;

import java.util.ArrayList;
import java.util.List;

public class ImageDTO extends ObservationDTO {
	
	

	public static final String FULL_RESOLUTION_FILE_PATH="full_resolution_file_path";
	private static final String OMERO_ID = "omero_id";
	private static final String DOWNLOAD_URL = "download_url";
	private static final String JPEG_URL = "jpeg_url";
	
	@Field(FULL_RESOLUTION_FILE_PATH)
	private String fullResolutionFilePath;
	
	@Field(OMERO_ID)
	private int omeroId;
	
	@Field(DOWNLOAD_URL)
	private String downloadUrl;
	
	@Field(JPEG_URL)
	private String jpegUrl;
	
	@Field("maTermId")
	private String maTermId;

	@Field("symbol_gene")
	private String symbolGene;//for search and annotation view
	
	@Field(SangerImageDTO.STATUS)
	private List<String> status;
	
	@Field(SangerImageDTO.IMITS_PHENOTYPE_STARTED)
	private List<String> imitsPhenotypeStarted;
	@Field(SangerImageDTO.IMITS_PHENOTYPE_COMPLETE)
	private List<String> imitsPhenotypeComplete;
	@Field(SangerImageDTO.IMITS_PHENOTYPE_STATUS)
	private List<String> imitsPhenotypeStatus;
	@Field(AlleleDTO.LEGACY_PHENOTYPE_STATUS)
	private Integer legacyPhenotypeStatus;
	
	

	@Field(SangerImageDTO.LATEST_PRODUCTION_CENTRE)
	private List<String> latestProductionCentre;
	@Field(SangerImageDTO.LATEST_PHENOTYPING_CENTRE)
	private List<String> latestPhenotypingCentre;
	@Field(SangerImageDTO.ALLELE_NAME)
	private List<String> alleleName;
	@Field(SangerImageDTO.MARKER_SYMBOL)
	private List<String> markerSymbol;
	@Field(SangerImageDTO.MARKER_NAME)
	private List<String> markerName;
	@Field(SangerImageDTO.MARKER_SYNONYM)
	private List<String> markerSynonym;
	@Field(SangerImageDTO.MARKER_TYPE)
	private String markerType;
	@Field(SangerImageDTO.HUMAN_GENE_SYMBOL)
	private List<String> humanGeneSymbol;
	@Field("symbol")
	private String symbol;
	@Field("subtype")
	private String subtype;
	@Field(AlleleDTO.LATEST_PHENOTYPE_STATUS)
	private List<String> latestPhenotypeStatus;

	
	
	
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


	public void setSubtype(String subtype) {
	
		this.subtype = subtype;
	}




	public String getSymbolGene() {
		if((this.getGeneSymbol()!=null)&&(this.getGeneAccession()!=null)){
			this.symbolGene=this.getGeneSymbol()+"_"+this.getAlleleAccession();
			}
		return this.symbolGene;
	}




	public String getMaTermId() {
	
		return maTermId;
	}



	
	public void setMaTermId(String maTermId) {
	
		this.maTermId = maTermId;
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




	public void addImitsPhenotypeStarted(String imitsPhenotypeStarted1) {

		if(this.imitsPhenotypeStarted==null){
			this.imitsPhenotypeStarted=new ArrayList<String>();
		}
		this.imitsPhenotypeStarted.add(imitsPhenotypeStarted1);
		
	}




	public void addImitsPhenotypeComplete(String imitsPhenotypeComplete1) {

		if(this.imitsPhenotypeComplete==null){
			this.imitsPhenotypeComplete=new ArrayList<String>();
		}
		this.imitsPhenotypeComplete.add(imitsPhenotypeComplete1);
		
	}




	public void addImitsPhenotypeStatus(String imitsPhenotypeStatus1) {

		if(this.imitsPhenotypeStatus==null){
			this.imitsPhenotypeStatus=new ArrayList<String>();
		}
		this.imitsPhenotypeStatus.add(imitsPhenotypeStatus1);
		
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




	public void setLatestPhenotypingCentre(List<String> latestPhenotypingCentre) {

		this.latestPhenotypingCentre=latestPhenotypingCentre;
		
	}




	public void setAlleleName(List<String> alleleName) {

		this.alleleName=alleleName;
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




	public void addMarkerType(String markerType) {

		this.markerType=markerType;
		
	}




	public void addHumanGeneSymbol(List<String> humanGeneSymbol) {

		if(this.humanGeneSymbol==null){
			this.humanGeneSymbol=new ArrayList<String>();
		}
		this.humanGeneSymbol.addAll(humanGeneSymbol);
		
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
	
	

	
}
