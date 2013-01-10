package uk.ac.ebi.phenotype.web.controller;

import java.util.Map;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import uk.ac.ebi.generic.util.SolrIndex;
import uk.ac.ebi.phenotype.imaging.springrest.images.dao.ImagesSolrDao;

@Controller
public class PhenotypesController implements BeanFactoryAware {

	private final Logger log = LoggerFactory.getLogger(GenesController.class);

	private BeanFactory bf;
	
	@Autowired
	private ImagesSolrDao imagesSolrDao;

	/**
	 * Phenotype controller loads information required for displaying 
	 * the phenotype page or, in the case of an error, redirects to the 
	 * error page
	 * 
	 * @param phenotype_id the Mammalian phenotype id of the phenotype to display
	 * @return the name of the view to render, or redirect to search page on error
	 * 
	 */
	
	@RequestMapping(value="/phenotypes/{phenotype_id}", method=RequestMethod.GET)
	public String loadMpPage(
			@PathVariable String phenotype_id, 
			Model model,
			RedirectAttributes attributes) {
		
		// Get the global application configuration
		@SuppressWarnings("unchecked")
		Map<String,String> config = (Map<String,String>) bf.getBean("globalConfiguration");

		model.addAttribute("phenotype_id", phenotype_id);

		// Query the images for this phenotype
		QueryResponse response=imagesSolrDao.getDocsForMpTerm(phenotype_id, 0, 6);
		model.addAttribute("numberFound", response.getResults().getNumFound());
		model.addAttribute("images", response.getResults());

		// Check to see if the phenotype term is found in the index
		// if not, redirect to the 
		String solrCoreName = "mp";
		String mode = "mpPage";
		SolrIndex solrIndex = new SolrIndex(phenotype_id, solrCoreName, mode, config);
		
		log.info("CHECK numFound : " + solrIndex.fetchNumFound());

		if ( solrIndex.fetchNumFound() == 0 ){
			attributes.addFlashAttribute("message", "Phenotype <b>" + phenotype_id + "</b> was not found. Please search for your phenotype of interest.");
			return "redirect:/search";
		}

		return "phenotypes";
	}	

	
	/**
	 * required for implementing BeanFactoryAware
	 * 
	 * @see org.springframework.beans.factory.BeanFactoryAware#setBeanFactory(org.springframework.beans.factory.BeanFactory)
	 */
	@Override
	public void setBeanFactory(BeanFactory arg0) throws BeansException {
		this.bf=arg0;
	}
}
