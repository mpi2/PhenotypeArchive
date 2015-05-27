package uk.ac.ebi.generic.util;

import org.springframework.web.multipart.MultipartFile;  

public class UploadedFile {
	 
    public int length;
    public byte[] bytes;
    public String name;
    public String type;
}