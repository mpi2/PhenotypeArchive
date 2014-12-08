package uk.ac.ebi.generic.util;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
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

	private List<String> interestingTerms = null;
	private Boolean loggedIn = null;
	
	public RegisterInterestDrupalSolr(String drupalBaseUrl, HttpServletRequest request) {
		this.drupalBaseUrl = drupalBaseUrl;
		this.drupalProxy = new DrupalHttpProxy(request);
	}

	/**
	 * is this user interested in the passed in term
	 * 
	 * the user can have a set of terms that they are interested in.  this 
	 * method checks an external URL to get the JSON list of IDs that the
	 * user has already expressed interest in and returns a true/false for
	 * the term indicated as id in the parameter
	 *  
	 * @param id the term id to check
	 * 
	 * @return true if the user has expressed interest in the term, false
	 * if not
	 */
	public boolean alreadyInterested(String id) {
		String endpoint = null;
		String key = null;
		
		if ( id.startsWith("MP") ){
			endpoint ="/phenotypesofinterest"; 
			key = "MPterms";
		}
		else if (id.startsWith("MGI") ){
			endpoint ="/genesofinterest"; 
			key = "MGIGeneid";
		}
		
		
		if ( ! loggedIn()) { return false; }

		if (interestingTerms == null) {
			interestingTerms = new ArrayList<String>();
	
			try {
				URL url = new URL(drupalBaseUrl + endpoint);
				String content = drupalProxy.getContent(url);
				JSONObject j = (JSONObject) JSONSerializer.toJSON(content);
				if(j.has(key)) {
					JSONArray ar = j.getJSONArray(key);
					for(int i = 0; i<ar.size();i++) {
						interestingTerms.add(ar.getString(i));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return interestingTerms.contains(id);
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
	
	
	public Map<String, String> registerInterestState(String acc, HttpServletRequest request, RegisterInterestDrupalSolr registerInterest)
			throws UnsupportedEncodingException {

			String registerInterestButtonString = "";
			String registerButtonAnchor = "";
			String id = acc;

			
			
			if (registerInterest.loggedIn()) {
				if (registerInterest.alreadyInterested(acc)) {
					registerInterestButtonString = "Unregister interest";
					id = acc;
				} else {
					registerInterestButtonString = "Register interest";
					id = acc;
				}
			} else {
				registerInterestButtonString = "Login to register interest";
				// Use the drupal destination parameter to redirect back to this
				// page
				// after logging in
				String dest = (String) request.getAttribute("javax.servlet.forward.request_uri");
				if (request.getQueryString() != null) {
					dest += URLEncoder.encode("?" + request.getQueryString(), "UTF-8");
				}

				if (dest == null) {
					dest = ((String) request.getAttribute("baseUrl")).substring(1) + request.getRequestURI().substring(request.getContextPath().length());
				}

				registerButtonAnchor = "/user/login?destination=" + dest;

			}

			Map<String, String> retVal = new HashMap<>();
			retVal.put("registerInterestButtonString", registerInterestButtonString);
			retVal.put("registerButtonAnchor", registerButtonAnchor);
			retVal.put("registerButtonId", id);
			
			return retVal;
		}

	
}
