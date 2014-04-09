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
	 * @return a concatenated drupal menu as a string separated by "MAIN*MENU*BELOW", or a default if
	 *         drupal could not be contacted for some reason
	 * @throws standard Exception
	 */
	//public JSONObject getDrupalMenu(String drupalBaseUrl) throws JSONException {
	public String getDrupalMenu(String drupalBaseUrl) {	
		String content = "";
		Random randomGenerator = new Random();
		
		// If we can't get the menu, default to the logged out menu
		try {
			if (getDrupalSessionCookieString() != null && ! getDrupalSessionCookieString().equals("") ) {
				log.info("Getting drupal menu.");				
				//URL url = new URL(drupalBaseUrl + "/menudisplaycombined");
				URL url = new URL(drupalBaseUrl + "/menudisplaycombinedrendered");
								
				content = this.getContent(url);
				
			} else {
				if (publicMenu == null || randomGenerator.nextInt(100) == 1) {
					log.info("Not logged in, using standard menu.");
					//URL url = new URL(drupalBaseUrl + "/menudisplaycombined");
					URL url = new URL(drupalBaseUrl + "/menudisplaycombinedrendered");
								
					publicMenu = this.getContent(url);
					content = publicMenu;
					
				} else {
					content = publicMenu;
				}
			}
		} catch (Exception e) {
			log.error("Cannot retrieve menu from drupal. Using default menu.");
			content = "<ul class=\"links\"><li class=\"menu-3521 first\"><a href=\"/user/login?current=menudisplaycombinedrendered\" title=\"Login with your account\" id=\"login\">Login</a></li><li class=\"menu-3523 last\"><a href=\"/user/register\" title=\"Register for an account\" id=\"register\">Register</a></li></ul>MAIN*MENU*BELOW<div id=\"block-menu-block-1\" class=\"block block-menu-block\"><div class=\"content\"><div class=\"menu-block-wrapper menu-block-1 menu-name-main-menu parent-mlid-0 menu-level-1\"><ul class=\"menu\"><li class=\"first leaf menu-mlid-3127\"><a href=\"https://dev.mousephenotype.org/data/search\">Search</a></li><li class=\"expanded menu-mlid-530\"><a href=\"/goals-and-background\">About IMPC</a><ul class=\"menu\"><li class=\"first leaf menu-mlid-3125\"><a href=\"/goals-and-background\" title=\"\">Goals and Background</a></li><li class=\"leaf menu-mlid-537\"><a href=\"/about-impc/impc-members\">IMPC Members</a></li><li class=\"leaf menu-mlid-3197\"><a href=\"https://www.mousephenotype.org/sites/mousephenotype.org/files/IMPC%20Governance%20and%20Coordination%20v11%20June%202013.pdf\" title=\"\">Governance Documentation</a></li><li class=\"leaf has-children menu-mlid-3525\"><a href=\"/about-impc/coordination\">Coordination</a></li><li class=\"leaf menu-mlid-3229\"><a href=\"/about-impc/industry-sponsors\">Industry Sponsors</a></li><li class=\"leaf menu-mlid-546\"><a href=\"/about-impc/impc-secretariat\">Secretariat</a></li><li class=\"last leaf has-children menu-mlid-3223\"><a href=\"/about-impc/publications\">Additional Information</a></li></ul></li><li class=\"expanded menu-mlid-526\"><a href=\"/news\" title=\"\">News &amp; Events</a><ul class=\"menu\"><li class=\"first leaf menu-mlid-3185\"><a href=\"/news-events/impc-lethal-lines\">IMPC Lethal Lines</a></li><li class=\"leaf menu-mlid-2102\"><a href=\"/news-and-events/meetings-2013\">Meetings</a></li><li class=\"last leaf menu-mlid-3157\"><a href=\"/news-events/phone-conferences\">Phone Conferences</a></li></ul></li><li class=\"leaf menu-mlid-559\"><a href=\"/contact-us\">Contact</a></li><li class=\"last expanded menu-mlid-1220\"><a href=\"/user?current=menudisplaycombinedrendered\" title=\"\">My IMPC</a><ul class=\"menu\"><li class=\"first leaf menu-mlid-1126\"><a href=\"/forum\" title=\"\">IMPC Forum</a></li><li class=\"last leaf menu-mlid-3133\"><a href=\"/my-impc/documentation\">Documentation</a></li></ul></li></ul></div><div class=\"clear\"></div></div></div>";
		}

		//strip off the drupal <front> tag
		content = content.replace("<front>", "");
//		System.out.println(content);
		
		return content;
	}


}
