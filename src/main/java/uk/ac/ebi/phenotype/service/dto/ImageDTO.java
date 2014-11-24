package uk.ac.ebi.phenotype.service.dto;

import org.apache.solr.client.solrj.beans.Field;

import uk.ac.ebi.phenotype.service.dto.ObservationDTO;

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
	
	@Field("ma_term_id")
	private String maTermId;


	
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
	
	

	
}