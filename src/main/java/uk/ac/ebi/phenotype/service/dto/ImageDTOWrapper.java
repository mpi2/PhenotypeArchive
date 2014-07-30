package uk.ac.ebi.phenotype.service.dto;

import java.util.List;

/**
 * class to add minimal info to the list of image DTOs such as number found
 * 
 * @author jwarren
 * 
 */
public class ImageDTOWrapper {

	private long numberFound = 0;

	private List<ImageDTO> imageDTOs;


	public long getNumberFound() {

		return numberFound;
	}


	public void setNumberFound(long numberFound) {

		this.numberFound = numberFound;
	}


	public List<ImageDTO> getImageDTOs() {

		return imageDTOs;
	}


	public void setImageDTOs(List<ImageDTO> imageDTOs) {

		this.imageDTOs = imageDTOs;
	}

}
