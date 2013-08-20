package uk.ac.ebi.phenotype.web.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Random;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONSerializer;

import org.apache.log4j.Logger;

public class DrupalHttpProxy extends HttpProxy {

	private Logger log = Logger.getLogger(this.getClass());

	private String debugSession = null;
	private HttpServletRequest request;
	
	protected static String authenticatedMenu = null;
	protected static String publicMenu = null;
	
	public DrupalHttpProxy(HttpServletRequest request) {
		super();
		this.request = request;
	}

	/**
	 * helper method to get a page from an external URL by including in the
	 * drupal session cookie
	 * 
	 * NOTE: The drupal session cookie is identified because it starts with the
	 * string "SSESS" ... this is not a very strong signal.
	 * 
	 * CAUTION: This method bypasses the SSL certificate verification to allow
	 * secure content from domains other than "www." to be accessed. This is
	 * required because the drupal content pages return coorectly only when 
	 * SSL is enabled. We need the drupal session cookie to determine if the user
	 * is logged in.
	 * 
	 * @param url
	 *            the url to interrogate
	 * @return the content in one big string with leading/trailing whitespace
	 *         removed
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public String getContent(URL url) throws IOException, URISyntaxException {
		long startTime = System.currentTimeMillis();

		if (cache.containsKey(url)) {
			return cache.get(url);
		}

		// Convert to a URI and back to a URL to get any strange characters to
		// be encoded during the process
		URL escapedUrl = new URL(url.toExternalForm().replace(" ", "%20"));

		String content = "";

		// Set the drupal session before getting the content
		this.setCookieString(getDrupalSessionCookieString());

		if(url.getProtocol().toLowerCase().equals("https")) {
			content = getSecureContent(escapedUrl);
		} else {			
			content = getNonSecureContent(escapedUrl);
		}

		long duration = System.currentTimeMillis() - startTime;
		log.debug("Got content from: "+escapedUrl+" in : "+duration +" milliseconds.");

		return content;
	}
	
	/**
	 * returns the drupal session cookie. If the debugSession class variable is
	 * set, the session variable is overriden.
	 * 
	 * NOTE: If the class variable debugSession is set, then that string
	 * overrides any session variable picked up from the cookie jar.
	 * 
	 * NOTE: The drupal session cookie is identified by starting with the string
	 * "SSESS" which, in hindsight, may turn out not to be the best idea in the
	 * world.
	 * 
	 * @return the drupal session cookie string
	 * 
	 */
	public String getDrupalSessionCookieString() {

		String session = "";

		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().startsWith("SSESS")) {
					session = cookie.getName() + "=" + cookie.getValue();
					break;
				}
			}
		}

		if (this.debugSession != null) {
			session = this.debugSession;
		}

		return session;
	}

	public String getDebugSession() {
		return debugSession;
	}

	public void setDebugSession(String debugSession) {
		this.debugSession = debugSession;
	}

	/**
	 * Get the current menu for the drupal system
	 * 
	 * @return a jsonArray corresponding to the drupal menu, or a default it
	 *         drupal could not be contacted for some reason
	 * @throws JSONException
	 */
	public JSONArray getDrupalMenu(String drupalBaseUrl) throws JSONException {

		String content = "";
		Random randomGenerator = new Random();
		
		// If we can't get the menu, default to the logged out menu
		try {
			if (getDrupalSessionCookieString() != null && ! getDrupalSessionCookieString().equals("") ) {
				log.info("Getting drupal menu.");				
				URL url = new URL(drupalBaseUrl + "/menudisplay");
				content = this.getContent(url);
			} else {
				if (publicMenu == null || randomGenerator.nextInt(100) == 1) {
					log.info("Not logged in, using standard menu.");
					URL url = new URL(drupalBaseUrl + "/menudisplay");
					publicMenu = this.getContent(url);
					content = publicMenu;
					// "[{\"title\":\"Home\",\"link_path\":\"\",\"href\":\"\"},{\"title\":\"About IMPC\",\"link_path\":\"node\\/19\",\"href\":\"node\\/19\",\"below\":[{\"title\":\"IMPC Goals\",\"link_path\":\"node\\/20\",\"href\":\"node\\/20\"},{\"title\":\"Members\",\"link_path\":\"node\\/21\",\"href\":\"node\\/21\",\"below\":[{\"title\":\"Locations\",\"link_path\":\"node\\/22\",\"href\":\"node\\/22\"}]},{\"title\":\"Secretariat\",\"link_path\":\"node\\/23\",\"href\":\"node\\/23\"},{\"title\":\"Workgroups\",\"link_path\":\"node\\/24\",\"href\":\"node\\/24\",\"below\":[{\"title\":\"IT Work Group\",\"link_path\":\"node\\/25\",\"href\":\"node\\/25\"},{\"title\":\"Phenotyping Work Group\",\"link_path\":\"node\\/26\",\"href\":\"node\\/26\"},{\"title\":\"Industry Work Group\",\"link_path\":\"node\\/27\",\"href\":\"node\\/27\"}]},{\"title\":\"Links\",\"link_path\":\"node\\/32\",\"href\":\"node\\/32\"}]},{\"title\":\"Search\",\"link_path\":\"https:\\/\\/dev.mousephenotype.org\\/mi\\/impc\\/dev\\/phenotype-archive\\/search\",\"href\":\"https:\\/\\/dev.mousephenotype.org\\/mi\\/impc\\/dev\\/phenotype-archive\\/search\"},{\"title\":\"News and Events\",\"link_path\":\"news\",\"href\":\"news\",\"below\":[{\"title\":\"Teleconference and Events Calendar\",\"link_path\":\"telephone-calendar\\/month\",\"href\":\"telephone-calendar\\/month\"}]},{\"title\":\"Send Us Feedback\",\"link_path\":\"contact\\/Beta Website Feedback\",\"href\":\"contact\\/Beta Website Feedback\",\"below\":[{\"title\":\"Newsletters\",\"link_path\":\"node\\/103011\",\"href\":\"node\\/103011\"}]},{\"title\":\"My IMPC\",\"link_path\":\"user\",\"href\":\"user\",\"below\":[{\"title\":\"Login\",\"link_path\":\"user\\/login\",\"href\":\"user\\/login\"},{\"title\":\"Access IMITS\",\"link_path\":\"http:\\/\\/mousephenotype.org\\/imits\",\"href\":\"http:\\/\\/mousephenotype.org\\/imits\"}]},{\"title\":\"Register\",\"link_path\":\"user\\/register\",\"href\":\"user\\/register\"}]";				
				} else {
					content = publicMenu;
				}
			}
		} catch (Exception e) {
			log.error("Cannot retreive menu from drupal. Using default menu.");
			content = "[{\"title\":\"Home\",\"link_path\":\"\",\"href\":\"\"},{\"title\":\"About IMPC\",\"link_path\":\"node\\/19\",\"href\":\"node\\/19\",\"below\":[{\"title\":\"Goals\",\"link_path\":\"node\\/72545\",\"href\":\"node\\/72545\"},{\"title\":\"Members\",\"link_path\":\"node\\/21\",\"href\":\"node\\/21\",\"below\":[{\"title\":\"Locations\",\"link_path\":\"node\\/22\",\"href\":\"node\\/22\"}]},{\"title\":\"Secretariat\",\"link_path\":\"node\\/23\",\"href\":\"node\\/23\"},{\"title\":\"Workgroups\",\"link_path\":\"node\\/24\",\"href\":\"node\\/24\",\"below\":[{\"title\":\"IT Work Group\",\"link_path\":\"node\\/25\",\"href\":\"node\\/25\"},{\"title\":\"Phenotyping Work Group\",\"link_path\":\"node\\/26\",\"href\":\"node\\/26\"},{\"title\":\"Industry Work Group\",\"link_path\":\"node\\/27\",\"href\":\"node\\/27\"}]},{\"title\":\"Phenotype Protocols\",\"link_path\":\"http:\\/\\/www.mousephenotype.org\\/impress\",\"href\":\"http:\\/\\/www.mousephenotype.org\\/impress\"},{\"title\":\"Links\",\"link_path\":\"node\\/32\",\"href\":\"node\\/32\"}]},{\"title\":\"Search\",\"link_path\":\"https:\\/\\/www.mousephenotype.org\\/mi\\/impc\\/phenotype-archive\\/search\",\"href\":\"https:\\/\\/www.mousephenotype.org\\/mi\\/impc\\/phenotype-archive\\/search\"},{\"title\":\"News and Events\",\"link_path\":\"news\",\"href\":\"news\",\"below\":[{\"title\":\"Meetings\",\"link_path\":\"node\\/66633\",\"href\":\"node\\/66633\"}]},{\"title\":\"Contact Us\",\"link_path\":\"node\\/33\",\"href\":\"node\\/33\",\"below\":[{\"title\":\"Outreach\",\"link_path\":\"node\\/66694\",\"href\":\"node\\/66694\"}]},{\"title\":\"My IMPC\",\"link_path\":\"user\",\"href\":\"user\",\"below\":[{\"title\":\"Login\",\"link_path\":\"user\\/login\",\"href\":\"user\\/login\"},{\"title\":\"Access IMITS\",\"link_path\":\"https:\\/\\/www.mousephenotype.org\\/imits\",\"href\":\"https:\\/\\/www.mousephenotype.org\\/imits\"},{\"title\":\"Newsletters\",\"link_path\":\"node\\/67171\",\"href\":\"node\\/67171\"},{\"title\":\"Documentation\",\"link_path\":\"node\\/72649\",\"href\":\"node\\/72649\"}]},{\"title\":\"Register\",\"link_path\":\"user\\/register\",\"href\":\"user\\/register\"}]";
		}

		//strip off the drupal <front> tag
		content = content.replace("<front>", "");

		JSONArray menu = (JSONArray) JSONSerializer.toJSON(content);

		return menu;
	}

}