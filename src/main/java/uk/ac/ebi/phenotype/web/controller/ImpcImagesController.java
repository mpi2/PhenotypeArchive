package uk.ac.ebi.phenotype.web.controller;

import javax.servlet.http.HttpServletRequest;

//import omero.*;
//import omero.api.ServiceFactoryPrx;

import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

//import Glacier2.CannotCreateSessionException;
//import Glacier2.PermissionDeniedException;

@Controller
public class ImpcImagesController {
	@RequestMapping("/impcImages*")
	public String allImages(
			@RequestParam(required = false, defaultValue = "0", value = "start") int start,
			@RequestParam(required = false, defaultValue = "25", value = "length") int length,
			@RequestParam(required = false, defaultValue = "*:*", value = "q") String qIn,
			@RequestParam(required = false, defaultValue = "", value = "phenotype_id") String mpId,
			@RequestParam(required = false, defaultValue = "", value = "gene_id") String geneId,
			@RequestParam(required = false, defaultValue = "", value = "fq") String[] filterField,
			@RequestParam(required = false, defaultValue = "", value = "facet.field") String facetField,
			@RequestParam(required = false, defaultValue = "", value = "qf") String qf,
			@RequestParam(required = false, defaultValue = "", value = "defType") String defType,
			@RequestParam(required = false, defaultValue = "", value = "anatomy_id") String maId,
			HttpServletRequest request, Model model) throws SolrServerException {

//		String hostName="localhost";
//		int port=8081;
//		client client = new client(hostName, port);
//		try {
//			ServiceFactoryPrx entryUnencrypted = client.createSession("root", "omero");
//			//client unsecureClient = client.createClient(false);
//			//ServiceFactoryPrx entryUnencrypted = unsecureClient.getSession();
//
//			//Retrieve the user id.
//			long userId = entryUnencrypted.getAdminService().getEventContext().userId;
//
//			long groupId = entryUnencrypted.getAdminService().getEventContext().groupId;
//			System.out.println("user id="+userId);
//			client.closeSession();
//			//if unsecure client exists.
//			//if (unsecureClient != null) unsecureClient.closeSession();
//		} catch (CannotCreateSessionException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (PermissionDeniedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (ServerError e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		// if you want to have the data transfer encrypted then you can
//		// use the entry variable otherwise use the following
		
		
		
System.out.println("calling impcImages web page");
		return "impcImages";
	}
}
