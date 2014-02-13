package uk.ac.ebi.phenotype.web.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Random;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.log4j.Logger;

public class DrupalHttpProxy extends HttpProxy {

	private Logger log = Logger.getLogger(this.getClass());

	private String debugSession = null;
	private HttpServletRequest request;
	
	protected static String authenticatedMenu = null;
	protected static String publicMenu = null;
	protected static String publicUserMenu = null;
	
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
	 * Get the current menus for the drupal system -- both the main menu and the user menu
	 * 
	 * @return a JSONObject corresponding to the drupal menus, or a default it
	 *         drupal could not be contacted for some reason
	 * @throws JSONException
	 */
	public JSONObject getDrupalMenu(String drupalBaseUrl) throws JSONException {

		String content = "";
		Random randomGenerator = new Random();
		
		// If we can't get the menu, default to the logged out menu
		try {
			if (getDrupalSessionCookieString() != null && ! getDrupalSessionCookieString().equals("") ) {
				log.info("Getting drupal menu.");				
				URL url = new URL(drupalBaseUrl + "/menudisplaycombined");
				content = this.getContent(url);
			} else {
				if (publicMenu == null || randomGenerator.nextInt(100) == 1) {
					log.info("Not logged in, using standard menu.");
					URL url = new URL(drupalBaseUrl + "/menudisplaycombined");
					publicMenu = this.getContent(url);
					content = publicMenu;
				} else {
					content = publicMenu;
				}
			}
		} catch (Exception e) {
			log.error("Cannot retrieve menu from drupal. Using default menu.");
			content = "{/\"mainmenu/\":[{/\"title/\":/\"Search/\",/\"link_path/\":/\"\\\\/data\\\\/search/\",/\"href/\":/\"\\\\/data\\\\/search/\"},{/\"title/\":/\"About IMPC/\",/\"link_path/\":/\"node\\\\/19/\",/\"href/\":/\"node\\\\/19/\",/\"below/\":[{/\"title/\":/\"Goals and Background/\",/\"link_path/\":/\"node\\\\/19/\",/\"href/\":/\"node\\\\/19/\"},{/\"title/\":/\"IMPC Members/\",/\"link_path/\":/\"node\\\\/21/\",/\"href/\":/\"node\\\\/21/\"},{/\"title/\":/\"Governance Documentation/\",/\"link_path/\":/\"\\\\/sites\\\\/mousephenotype.org\\\\/files\\\\/IMPC%20Governance%20and%20Coordination%20v11%20June%202013.pdf/\",/\"href/\":/\"\\\\/sites\\\\/mousephenotype.org\\\\/files\\\\/IMPC%20Governance%20and%20Coordination%20v11%20June%202013.pdf/\"},{/\"title/\":/\"Coordination/\",/\"link_path/\":/\"node\\\\/85139/\",/\"href/\":/\"node\\\\/85139/\",/\"below/\":[{/\"title/\":/\"Steering Committee/\",/\"link_path/\":/\"node\\\\/21/\",/\"href/\":/\"node\\\\/21/\"},{/\"title/\":/\"Panel of Scientific Consultants/\",/\"link_path/\":/\"node\\\\/83909/\",/\"href/\":/\"node\\\\/83909/\"},{/\"title/\":/\"Finance Committee/\",/\"link_path/\":/\"node\\\\/83911/\",/\"href/\":/\"node\\\\/83911/\"},{/\"title/\":/\"Communications Working Group/\",/\"link_path/\":/\"node\\\\/83913/\",/\"href/\":/\"node\\\\/83913/\"},{/\"title/\":/\"Phenotyping Steering Group/\",/\"link_path/\":/\"node\\\\/83915/\",/\"href/\":/\"node\\\\/83915/\"},{/\"title/\":/\"Production Steering Group/\",/\"link_path/\":/\"node\\\\/83917/\",/\"href/\":/\"node\\\\/83917/\"},{/\"title/\":/\"Data Analysis Advisory Committee/\",/\"link_path/\":/\"node\\\\/83919/\",/\"href/\":/\"node\\\\/83919/\"},{/\"title/\":/\"Statistics Technical Group/\",/\"link_path/\":/\"node\\\\/83921/\",/\"href/\":/\"node\\\\/83921/\"},{/\"title/\":/\"MTA and Line Exchange/\",/\"link_path/\":/\"node\\\\/83923/\",/\"href/\":/\"node\\\\/83923/\"},{/\"title/\":/\"iMits Steering Group/\",/\"link_path/\":/\"node\\\\/83925/\",/\"href/\":/\"node\\\\/83925/\"}]},{/\"title/\":/\"Industry Sponsors/\",/\"link_path/\":/\"node\\\\/83967/\",/\"href/\":/\"node\\\\/83967/\"},{/\"title/\":/\"Secretariat/\",/\"link_path/\":/\"node\\\\/23/\",/\"href/\":/\"node\\\\/23/\"},{/\"title/\":/\"Additional Information/\",/\"link_path/\":/\"node\\\\/83927/\",/\"href/\":/\"node\\\\/83927/\",/\"below/\":[{/\"title/\":/\"Publications/\",/\"link_path/\":/\"node\\\\/83927/\",/\"href/\":/\"node\\\\/83927/\"},{/\"title/\":/\"Citing IMPC/\",/\"link_path/\":/\"node\\\\/83929/\",/\"href/\":/\"node\\\\/83929/\"},{/\"title/\":/\"Links/\",/\"link_path/\":/\"node\\\\/32/\",/\"href/\":/\"node\\\\/32/\"}]}]},{/\"title/\":/\"News & Events/\",/\"link_path/\":/\"news/\",/\"href/\":/\"news/\",/\"below/\":[{/\"title/\":/\"IMPC Lethal Lines/\",/\"link_path/\":/\"node\\\\/83629/\",/\"href/\":/\"node\\\\/83629/\"},{/\"title/\":/\"Meetings/\",/\"link_path/\":/\"node\\\\/66633/\",/\"href/\":/\"node\\\\/66633/\"},{/\"title/\":/\"Phone Conferences/\",/\"link_path/\":/\"node\\\\/83047/\",/\"href/\":/\"node\\\\/83047/\"}]},{/\"title/\":/\"Contact/\",/\"link_path/\":/\"node\\\\/33/\",/\"href/\":/\"node\\\\/33/\"},{/\"title/\":/\"My IMPC/\",/\"link_path/\":/\"user/\",/\"href/\":/\"user/\",/\"below/\":[{/\"title/\":/\"IMPC Forum/\",/\"link_path/\":/\"forum/\",/\"href/\":/\"forum/\"},{/\"title/\":/\"Documentation/\",/\"link_path/\":/\"node\\\\/72649/\",/\"href/\":/\"node\\\\/72649/\"}]}],/\"usermenu/\":[{/\"title/\":/\"My IMPC/\",/\"link_path/\":/\"user/\",/\"href/\":/\"user/\",/\"below/\":[{/\"title/\":/\"Create new account/\",/\"link_path/\":/\"user\\\\/register/\",/\"href/\":/\"user\\\\/register/\"},{/\"title/\":/\"Log in/\",/\"link_path/\":/\"user\\\\/login/\",/\"href/\":/\"user\\\\/login/\"},{/\"title/\":/\"Request new password/\",/\"link_path/\":/\"user\\\\/password/\",/\"href/\":/\"user\\\\/password/\"}]},{/\"title/\":/\"Login/\",/\"link_path/\":/\"user\\\\/login/\",/\"href/\":/\"user\\\\/login/\"},{/\"title/\":/\"Register/\",/\"link_path/\":/\"user\\\\/register/\",/\"href/\":/\"user\\\\/register/\"}]}";
		}

		//strip off the drupal <front> tag
		content = content.replace("<front>", "");

		JSONObject menu = (JSONObject) JSONSerializer.toJSON(content);

		return menu;
	}


}