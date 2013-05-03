package uk.ac.ebi.generic.util;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.ebi.phenotype.web.util.DrupalHttpProxy;

public class RegisterInterestDrupalSolr {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private DrupalHttpProxy drupalProxy;
	private String drupalBaseUrl;

	private Boolean loggedIn = null;
	private List<String> interestingGenes = null;

	public RegisterInterestDrupalSolr(Map<String,String> config, HttpServletRequest request) {
		this.drupalBaseUrl = config.get("drupalBaseUrl");
		this.drupalProxy = new DrupalHttpProxy(request);
	}

	/**
	 * is this user interested in the passed in gene
	 * 
	 * the user can have a set of genes that they are interested in.  this 
	 * method checks an external URL to get the JSON list of MGI IDs that the
	 * user has already expressed interest in and returns a true/false for
	 * the gene indicated in the geneid parameter
	 *  
	 * @param geneid the gene id to check
	 * 
	 * @return true if the user has expressed interest in the gene, false
	 * if not
	 */
	public boolean alreadyInterested(String geneid) {
		
		if ( ! loggedIn()) { return false; }

		if (interestingGenes == null) {
			interestingGenes = new ArrayList<String>();
	
			try {
				URL url = new URL(drupalBaseUrl + "/genesofinterest");
				String content = drupalProxy.getContent(url);
				JSONObject j = (JSONObject) JSONSerializer.toJSON(content);
				if(j.has("MGIGeneid")) {
					JSONArray ar = j.getJSONArray("MGIGeneid");
					for(int i = 0; i<ar.size();i++) {
						interestingGenes.add(ar.getString(i));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return interestingGenes.contains(geneid);
	}
	
	
	/**
	 * is this user logged in
	 * 
	 * This checks an external URL on the drupal instance to see if the
	 * user is logged in.  It uses the "/roles" URL.
	 * 
	 * @return true if the user is logged in, false if not
	 */
	public  boolean loggedIn() {

		if (drupalProxy.getDrupalSessionCookieString().isEmpty()) {
			log.debug("No drupal session cookie means not logged in");
			return false;
		}

		if (loggedIn == null) {
			loggedIn = false;

			try {
				URL url = new URL(drupalBaseUrl + "/roles");
				String content = drupalProxy.getContent(url);
				if (content.contains("authenticated user")) {
					log.debug("User is logged in to drupal");
					loggedIn = true;
				}
			} catch (Exception e) {
				e.printStackTrace();
				loggedIn = false;
			}
		}

		return loggedIn;
	}
}
