package uk.ac.ebi.phenotype.web.util;

import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class DeploymentInterceptor extends HandlerInterceptorAdapter {

	private Logger log = Logger.getLogger(this.getClass().getCanonicalName());

	@Resource(name="globalConfiguration")
	private Map<String, String> config;

	/**
	 * set baseUrl and other variables for all controllers
	 */
	public boolean preHandle(
		HttpServletRequest request,
		HttpServletResponse response, 
		Object handler) throws Exception {

		// Do not set attributes for assets
		if (request.getRequestURI().endsWith(".js") ||
			request.getRequestURI().endsWith(".css") ||
			request.getRequestURI().endsWith(".gif") ||
			request.getRequestURI().endsWith(".png")
			) {
			return true;
		}

		request.setAttribute("baseUrl", request.getContextPath());

		// If this webapp is being accessed behind a proxy, the 
		// x-forwarded-host header will be set, in which case, use the
		// configured baseUrl and mediaBauseUrl paths.  If this webapp is 
		// being accessed directly set the baseUrl and mediaBaseUrl as 
		// the current context path
		if (request.getHeader("x-forwarded-host") != null) {

			String[] forwards = request.getHeader("x-forwarded-host").split(",");

			for (String forward : forwards) {
				if  (! forward.matches(".*ebi\\.ac\\.uk")) {
					log.debug("Request proxied. Using baseUrl: "+config.get("baseUrl"));
					request.setAttribute("baseUrl", config.get("baseUrl"));
					break;
				}
			}
		}


        request.setAttribute("version", config.get("version"));
        request.setAttribute("drupalBaseUrl", config.get("drupalBaseUrl"));
		request.setAttribute("mediaBaseUrl", config.get("mediaBaseUrl"));
		request.setAttribute("solrUrl",config.get("solrUrl"));
		request.setAttribute("internalSolrUrl",config.get("internalSolrUrl"));
		request.setAttribute("googleAnalytics", config.get("googleAnalytics"));
		request.setAttribute("liveSite", config.get("liveSite"));

		log.debug("Interception! Intercepted path "+request.getRequestURI());
		return true;
	}
}
