/*******************************************************************************
 * Copyright 2015 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 *******************************************************************************/
package uk.ac.ebi.phenotype.web.util;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

public class DeploymentInterceptor extends HandlerInterceptorAdapter {

    private Logger log = Logger.getLogger(this.getClass().getCanonicalName());

    @Autowired
    DataReleaseVersionManager dataReleaseVersionManager;

    @Resource(name = "globalConfiguration")
    private Map<String, String> config;

    /**
     * set baseUrl and other variables for all controllers
     */
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws Exception {

        // Do not set attributes for assets
        if (request.getRequestURI().endsWith(".js")
                || request.getRequestURI().endsWith(".css")
                || request.getRequestURI().endsWith(".gif")
                || request.getRequestURI().endsWith(".png")) {
            return true;
        }

        request.setAttribute("baseUrl", request.getContextPath());
        request.setAttribute("releaseVersion", dataReleaseVersionManager.getReleaseVersion());

        String mappedHostname = "http://" + (String)request.getServerName();
        if (request.getServerPort() != 80) {
            mappedHostname += ":" + request.getServerPort();
        }
        request.setAttribute("mappedHostname", mappedHostname);

        // If this webapp is being accessed behind a proxy, the 
        // x-forwarded-host header will be set, in which case, use the
        // configured baseUrl and mediaBauseUrl paths.  If this webapp is 
        // being accessed directly set the baseUrl and mediaBaseUrl as 
        // the current context path
        if (request.getHeader("x-forwarded-host") != null) {

            String[] forwards = request.getHeader("x-forwarded-host").split(",");

            for (String forward : forwards) {
                if ( ! forward.matches(".*ebi\\.ac\\.uk")) {
                    log.debug("Request proxied. Using baseUrl: " + config.get("baseUrl"));
                    request.setAttribute("baseUrl", config.get("baseUrl"));
                    break;
                }
            }
            request.setAttribute("mappedHostname", config.get("drupalBaseUrl"));
        }

        request.setAttribute("version", config.get("version"));

        if (config.get("liveSite").equals("false")) {
            // If DEV or BETA, refresh the cache daily
            String dateStamp = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
            request.setAttribute("version", dateStamp);
        }

        request.setAttribute("drupalBaseUrl", config.get("drupalBaseUrl"));
        request.setAttribute("mediaBaseUrl", config.get("mediaBaseUrl"));
        request.setAttribute("impcMediaBaseUrl", config.get("impcMediaBaseUrl"));
        request.setAttribute("pdfThumbnailUrl", config.get("pdfThumbnailUrl"));
        request.setAttribute("solrUrl", config.get("solrUrl"));
        request.setAttribute("internalSolrUrl", config.get("internalSolrUrl"));
        request.setAttribute("googleAnalytics", config.get("googleAnalytics"));
        request.setAttribute("liveSite", config.get("liveSite"));

        log.debug("Interception! Intercepted path " + request.getRequestURI());
        return true;
    }
}
