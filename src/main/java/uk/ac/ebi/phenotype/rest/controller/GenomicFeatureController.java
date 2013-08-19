/**
 * Copyright © 2011-2013 EMBL - European Bioinformatics Institute
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

/**
 *
 * GenomicFeature controller in a RESTful context.
 * 
 * @author Gautier Koscielny (EMBL-EBI) <koscieln@ebi.ac.uk>
 * @since February 2012
 * 
 */

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import uk.ac.ebi.phenotype.bean.ListContainer;
import uk.ac.ebi.phenotype.dao.GenomicFeatureDAO;
import uk.ac.ebi.phenotype.pojo.GenomicFeature;
import uk.ac.ebi.phenotype.rest.util.AtomUtil;

import com.sun.syndication.feed.atom.Feed;

@Controller
public class GenomicFeatureController {

	@Autowired
	private GenomicFeatureDAO genomicFeatureDAO;

	private Jaxb2Marshaller jaxb2Mashaller;

	public void setJaxb2Mashaller(Jaxb2Marshaller jaxb2Mashaller) {
		this.jaxb2Mashaller = jaxb2Mashaller;
	}

	private static final String XML_VIEW_NAME = "genomicFeatures";

	/**
	 * <p>
	 * Provide a model with an genomicFeature for the genomicFeature detail
	 * page.
	 * </p>
	 * 
	 * @param id
	 *            the id of the genomicFeature
	 * @param model
	 *            the "implicit" model created by Spring MVC
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/genomicFeature/{acc}")
	public ModelAndView getGenomicFeature(@PathVariable String acc) {
		GenomicFeature gf = genomicFeatureDAO.getGenomicFeatureByAccessionAndDbId(acc, 3);
		return new ModelAndView(XML_VIEW_NAME, "object", gf);
	}

	/**
	 * <p>
	 * Provide a model with a list of all genomicFeatures for the genomicFeature
	 * List page.
	 * </p>
	 * 
	 * @param model
	 *            the "implicit" model created by Spring MVC
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/genomicFeatures")
	public ModelAndView getGenomicFeatures() {
		List<GenomicFeature> gfs = genomicFeatureDAO.getAllGenomicFeatures();
		ListContainer<GenomicFeature> list = new ListContainer<GenomicFeature>(gfs);
		return new ModelAndView(XML_VIEW_NAME, "genomicFeatures", list);
	}

	// //////////////////////// @ResponseBody ////////////////////////

	@RequestMapping(method = RequestMethod.GET, value = "/genfeat/{acc}", headers = "Accept=application/xml, application/json")
	public @ResponseBody
	GenomicFeature getGenFeat(@PathVariable String acc) {
		GenomicFeature gf = genomicFeatureDAO.getGenomicFeatureByAccessionAndDbId(acc, 3);
		return gf;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/genfeats", headers = "Accept=application/xml, application/json")
	public @ResponseBody
	ListContainer<GenomicFeature> getAllGenFeat() {
		List<GenomicFeature> gfs = genomicFeatureDAO.getAllGenomicFeatures();
		ListContainer<GenomicFeature> list = new ListContainer<GenomicFeature>(gfs);
		return list;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/genfeats", headers = "Accept=application/atom+xml")
	public @ResponseBody
	Feed getGenFeatFeed() {
		List<GenomicFeature> gfs = genomicFeatureDAO.getAllGenomicFeatures();
		return AtomUtil.genomicFeatureFeed(gfs, jaxb2Mashaller);
	}

}
