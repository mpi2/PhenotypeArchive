package uk.ac.ebi.phenotype.web.controller;

import java.util.Map;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import uk.ac.ebi.phenotype.imaging.springrest.images.dao.ImagesSolrDao;
import uk.ac.ebi.phenotype.util.SolrIndex;


@Controller
public class MpController implements BeanFactoryAware {

	private BeanFactory bf;
	
	private ImagesSolrDao imagesSolrDao;

	/**
	 * Creates a new GenomicFeatureController with a given genomicFeature
	 * manager.
	 */
	@Autowired
	public MpController(ImagesSolrDao imagesSolrDao) {
		this.imagesSolrDao = imagesSolrDao;
	}
	/**
	 * <p>Simply takes us to the MP page or fileNotFound page.</p>
	 * @param model
	 * @return
	 */
	
	@RequestMapping(value="/mp", method=RequestMethod.GET)
	public String loadMpPage(@RequestParam("mpid") String mpid, Model model) {	
		
		this.getFirstImages(mpid, model);
		String solrCoreName = "mp";
		String mode = "mpPage";
		
		Map config = (Map) bf.getBean("globalConfiguration");
		SolrIndex solrIndex = new SolrIndex(mpid, solrCoreName, mode, config);
		
		System.out.println("CHECK numFound : " + solrIndex.fetchNumFound());
		if ( solrIndex.fetchNumFound() == 0 ){
			// load fileNotFound.jsp: a workaround to simulate 404 as '0 found' in solr response is also a success
			return "fileNotFound";
		}		
		return "mp";
	}	
	
	private void getFirstImages(String mpid, Model model){
		QueryResponse response=imagesSolrDao.getDocsForMpTerm(mpid, 0, 6);
		model.addAttribute("numberFound", response.getResults().getNumFound());
		model.addAttribute("images", response.getResults());
	}
	@Override
	public void setBeanFactory(BeanFactory arg0) throws BeansException {
		this.bf=arg0;
		
	}
	
}
