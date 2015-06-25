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
package uk.ac.ebi.phenotype.web.controller;

import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import uk.ac.ebi.generic.util.UploadedFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
 
@Controller
//@RequestMapping("/cont")
public class FileUploadController {
 
	UploadedFile ufile;
	
	public FileUploadController(){
		ufile = new UploadedFile();
	}
 
	@RequestMapping(value = "/batchQuery", method = RequestMethod.POST)
	public @ResponseBody String upload(MultipartHttpServletRequest request, HttpServletResponse response) throws IOException {                 
 
		String dataType = request.getParameter("dataType");
		System.out.println("datatype: " + dataType);
		Iterator<String> itr = request.getFileNames();
 
		MultipartFile mpf = request.getFile(itr.next());
		System.out.println(mpf.getOriginalFilename() +" uploaded! -- " + mpf.getSize());
		byte[] bytes = getBytesFromFile(mpf);
 
		String idstr = new String(bytes);
		String[] idList = idstr.split("\t|\n|,|\\s");
		List<String> goodIdList = new ArrayList();
		List<String> badIdList = new ArrayList();
		
		for ( int i=0; i<idList.length; i++ ){
			String currId = idList[i].trim().replaceAll("^,*|,*$", "").toUpperCase();
			if ( currId.equals("") ){
				continue;
			}
			
			if ( dataType.equals("disease") ){
    			if ( ! (currId.startsWith("OMIM:") ||  
    				 currId.startsWith("ORPHANET:") || 
    				 currId.startsWith("DECIPHER:") ) ){
    				badIdList.add(currId);
        			System.out.println("ERROR - " + currId + " is not a member of " + dataType + " datatype");
        		}
    		}
    		else if ( dataType.equals("gene") && ! currId.startsWith("MGI:") ){
    			badIdList.add(currId);
    			System.out.println("ERROR - " + currId + " is not a member of " + dataType + " datatype");
    		}
    		else if ( (dataType.equals("mp") || dataType.equals("hp")) && ! currId.startsWith(dataType.toUpperCase()) ){
    			badIdList.add(currId);
        		System.out.println("ERROR - " + currId + " is not a member of " + dataType + " datatype");
    		}
			
			goodIdList.add("\"" + currId + "\"");
		}
		
		JSONObject j = new JSONObject();
        j.put("goodIdList", StringUtils.join(goodIdList, ","));
        j.put("badIdList", StringUtils.join(badIdList, ", "));
		
		
//		String filePath = "/tmp/impc_batch_query_upload/";
//		//write bytes to file
//		File outFile = new File(filePath + mpf.getOriginalFilename());
//		writeBytes2File(bytes, outFile); 
     
		return j.toString();
	}
 
	// Returns the contents of the file in a byte array.
	public static byte[] getBytesFromFile(MultipartFile file) throws IOException {
		System.out.println("Start of getBytesFromFile reached / " + file.getSize());
		InputStream is = file.getInputStream();
		
		// Get the size of the file
		long length = file.getSize();
       
		if (length > 5000000) {// 5 MB
			// File is too large
			throw new IOException("The uploaded file size is bigger than the 5MB limit");
		}

		// Create the byte array to hold the data
		byte[] bytes = new byte[(int) length];

		// Read in the bytes
		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length
				&& (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
			offset += numRead;
		}
		
		// Ensure all the bytes have been read in
		if (offset < bytes.length) {
			throw new IOException("Could not completely read file " + file.getName());
		}
		
		// Close the input stream and return bytes
		is.close();
		System.out.println("End of getBytesFromFile reached / " + bytes.length);
		
		return bytes;
	}

	public void writeBytes2File(byte[] bytes, File outfile) {
      
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(outfile);
			fos.write(bytes);
			fos.flush();
			System.out.println("outfile : " + outfile);
		} 
		catch (IOException ex) {
			//Logger.getLogger(FileUploadController.class.getName()).log(Level.SEVERE, null, ex);
			System.out.println(ex);
		} 
		finally {
			try {
				fos.close();
			} catch (IOException ex) {
				//Logger.getLogger(FileUploadController.class.getName()).log(Level.SEVERE, null, ex);
				System.out.println(ex);
			}
		}
	}
}

