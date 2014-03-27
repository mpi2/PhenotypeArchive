/**
 * Copyright © 2011-2014 EMBL - European Bioinformatics Institute
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License.  
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.phenotype.rest.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import uk.ac.ebi.phenotype.error.GenomicFeatureNotFoundException;
import uk.ac.ebi.phenotype.imaging.persistence.ImaImageRecord;
import uk.ac.ebi.phenotype.imaging.springrest.images.Images;
import uk.ac.ebi.phenotype.imaging.springrest.images.dao.ImagesDao;


@Controller("restfulImagesController")
@RequestMapping("/rest")
public class ImagesController {
	
	private ImagesDao imagesDao;

	@Autowired 
	public ImagesController(ImagesDao imagesDao) {
		this.imagesDao = imagesDao;
	}
	
	
	/*
	 * Zero based access with start and length of images returned from the query
	 */
	@RequestMapping(value = "/images*", method = { RequestMethod.GET, RequestMethod.HEAD })
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public Images getAllImages(
			@RequestParam(required = false, defaultValue = "0", value = "start") int start,
			@RequestParam(required = false, defaultValue = "10", value = "length") int length,
			@RequestParam(required = false, defaultValue = "", value = "search") String search,
			@RequestParam(required = false, defaultValue = "", value = "mpid") String mpId,
			@RequestParam(required = false, defaultValue = "", value = "gene_id") String geneId,
			HttpServletResponse resp) throws Exception {

		resp.setHeader("Access-Control-Allow-Origin", "*");

		if (!geneId.equals("")) {
			geneId = geneId.replace("MGI:", "MGI\\:");
			search = "accession:" + geneId;
		}

		if (!mpId.equals("")) {
			mpId = mpId.replace("MP:", "MP\\:");
			search = "annotationTermId:" + mpId;
		}

		return imagesDao.getAllImages(start, length, search);
	}	
	
	
	@RequestMapping(value = "/images/{id}", method = { RequestMethod.GET, RequestMethod.HEAD })
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public ImaImageRecord getImageById(@PathVariable int id, HttpServletResponse resp) throws IOException {

		resp.setHeader("Access-Control-Allow-Origin", "*");

		ImaImageRecord rec = imagesDao.getImageWithId(id);

		if (rec == null) {
			resp.sendError(HttpServletResponse.SC_NOT_FOUND);
		}

		return rec;
	}

	/**
	 * Error handler for errors
	 * 
	 * @param exception
	 * @return redirect to error page
	 * @throws IOException 
	 * 
	 */
	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ResponseBody
	public String handleException(Exception e, HttpServletResponse response) throws IOException {
		e.printStackTrace();
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setContentType("text/plain");
		return "Not found";
    } 

}
