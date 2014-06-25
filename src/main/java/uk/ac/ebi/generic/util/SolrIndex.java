/**
 * Copyright © 2011-2014 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package uk.ac.ebi.generic.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServerException;
//import org.apache.solr.common.SolrDocument;
//import org.apache.solr.common.SolrDocumentList;
import org.springframework.stereotype.Service;

import uk.ac.ebi.phenotype.web.util.DrupalHttpProxy;
import uk.ac.ebi.phenotype.web.util.HttpProxy;

@Service
public class SolrIndex {

    private Logger log = Logger.getLogger(this.getClass().getCanonicalName());

    @Resource(name = "globalConfiguration")
    private Map<String, String> config;

    /**
     * Return the number of documents found for a specified solr query on a
     * specified core.
     *
     * @param query the query
     * @param core which solr core to query
     * @param mode which configuration mode to operate in
     * @param solrParams the default solr parameters to also restrict the query
     * @return integer count of the number of matching documents
     * @throws IOException
     * @throws URISyntaxException
     */
    public Integer getNumFound(String query, String core, String mode,
            String solrParams) throws IOException, URISyntaxException {
        JSONObject json = getResults(composeSolrUrl(core, mode, query,
                solrParams, 0, 0, false));
        return json.getJSONObject("response").getInt("numFound");
    }

    /**
     * Gets the json string representation for the query.
     *
     * @param query the query for which documents
     * @param core the solr core to query
     * @param gridSolrParams the default solr parameters to append to the query
     * @param mode the configuration mode to operate in
     * @param start where to start the offset
     * @param length how many documents to return
     * @param showImgView is this query showing the annotation view of the
     * images (true/false)
     * @return
     * @throws IOException
     * @throws URISyntaxException
     */
    public JSONObject getDataTableJson(String query, String core,
            String gridSolrParams, String mode, int start, int length,
            boolean showImgView) throws IOException, URISyntaxException {

        if (gridSolrParams.equals("")) {
            gridSolrParams = "qf=auto_suggest&defType=edismax&wt=json&q=*:*";
        }

        return getResults(composeSolrUrl(core, mode, query, gridSolrParams,
                start, length, showImgView));

    }

    /**
     * Get rows for saving to an external file.
     *
     * @param core the solr core to query
     * @param gridSolrParams the default parameters to use in the query
     * @param start where to start the query
     * @param gridFields the default solr parameters to append to the query
     * @param length how many documents to return
     * @return json representation of the results of the solr query
     * @throws IOException
     * @throws URISyntaxException
     */
    public JSONObject getDataTableExportRows(String core,
            String gridSolrParams, String gridFields, int start, int length)
            throws IOException, URISyntaxException {

        System.out.println("GRID SOLR PARAMS : " + gridSolrParams);

        if (core.equals("gene")) {
            //gridFields += ",imits_report_phenotyping_complete_date,imits_report_genotype_confirmed_date,imits_report_mi_plan_status,escell,ikmc_project,imits_phenotype_started,imits_phenotype_complete,imits_phenotype_status";
        }

        //String newgridSolrParams = gridSolrParams + "&rows=" + length
        gridSolrParams = gridSolrParams.replace("rows=10", "rows=" + length);
        String newgridSolrParams = gridSolrParams
                + "&start=" + start + "&fl=" + gridFields;

        String url = composeSolrUrl(core, "", "", newgridSolrParams, start,
                length, false);

        log.debug("Export data URL: " + url);
        System.out.println("Export data URL: " + url);
        return getResults(url);
    }

    /**
     * Prepare a url for querying the solr indexes based on the passed in
     * arguments.
     *
     * @param core which solr core to query
     * @param mode which configuration mode to operate in
     * @param query what to query
     * @param gridSolrParams default parameters to add to the solr query
     * @param iDisplayStart starting point of the query
     * @param iDisplayLength length of the query
     * @param showImgView which image mode to operate in
     * @return the constructed url including all parameters
     */
    private String composeSolrUrl(String core, String mode, String query,
            String gridSolrParams, Integer iDisplayStart,
            Integer iDisplayLength, boolean showImgView) {
        String internalSolrUrl = config.get("internalSolrUrl");

        String url = internalSolrUrl + "/" + core + "/select?";

        System.out.println(("BASEURL: " + url));
        if (mode.equals("mpPage")) {
            url += "q=" + query;
            url += "&start=0&rows=0&wt=json&qf=auto_suggest&defType=edismax";
        } else if (mode.equals("geneGrid")) {
            url += gridSolrParams + "&start=" + iDisplayStart + "&rows="
                    + iDisplayLength;
            System.out.println("GENE PARAMS: " + url);
        } else if (mode.equals("pipelineGrid")) {
            url += gridSolrParams + "&start=" + iDisplayStart + "&rows="
                    + iDisplayLength;
//			System.out.println("PROTOCOL PARAMS: " + url);
        } else if (mode.equals("imagesGrid")) {
            url += gridSolrParams + "&start=" + iDisplayStart + "&rows="
                    + iDisplayLength;
            if (!showImgView) {
                url += "&facet=on&facet.field=symbol_gene&facet.field=expName_exp&facet.field=maTermName&facet.field=mpTermName&facet.mincount=1&facet.limit=-1";
            }
            System.out.println("IMG PARAMS: " + url);
        } else if (mode.equals("mpGrid")) {
            url += gridSolrParams.replaceAll(" ", "%20") + "&start="
                    + iDisplayStart + "&rows=" + iDisplayLength;
            System.out.println("MP PARAMS: " + url);
        } else if (mode.equals("maGrid")) {
            url += gridSolrParams.replaceAll(" ", "%20") + "&start="
                    + iDisplayStart + "&rows=" + iDisplayLength;
//			System.out.println("MA PARAMS: " + url);
        } else if (mode.equals("diseaseGrid")) {
            url += gridSolrParams.replaceAll(" ", "%20") + "&start="
                    + iDisplayStart + "&rows=" + iDisplayLength;
//			System.out.println("DISEASE PARAMS: " + url);
        } else if (mode.equals("ikmcAlleleGrid")) {
            url += "q=" + query;
            url += "&start=0&rows=0&wt=json";
//			System.out.println("IKMC ALLELE PARAMS: " + url);
        } else if (mode.equals("all") || mode.equals("page") || mode.equals("")) { // download search page result
            url += gridSolrParams;
            if (core.equals("images") && !showImgView) {
                url += "&facet=on&facet.field=symbol_gene&facet.field=expName_exp&facet.field=maTermName&facet.field=mpTermName&facet.mincount=1&facet.limit=-1";
            }
//			System.out.println("GRID DUMP PARAMS - " + core + ": " + url);
        }
		// OTHER solrCoreNames to be added here

        return url;
    }

    /**
     * Get the simplified production status of ES cells/mice for a document.
     *
     * @param doc represents a gene with imits status fields
     * @return the latest status at the gene level for both ES cells and alleles
     */
    public String deriveLatestProductionStatusForEsCellAndMice(JSONObject doc, HttpServletRequest request, boolean toExport) {

        String esCellStatus = fetchEsCellStatus(doc, request, toExport);
        String miceStatus = "";
        final List<String> exportMiceStatus = new ArrayList<String>();

        Map<String, String> sh = new HashMap<String, String>();

        try {

			// mice production status			
            // Mice: blue tm1/tm1a/tm1e... mice (depending on how many allele docs) 
            if (doc.containsKey("mouse_status")) {

                JSONArray alleleNames = doc.getJSONArray("allele_name");
                JSONArray mouseStatus = doc.getJSONArray("mouse_status");

                for (int i = 0; i < mouseStatus.size(); i++) {
                    String mouseStatusStr = mouseStatus.get(i).toString();
                    sh.put(mouseStatusStr, "yes");
                }

                // if no mice status found but there is already allele produced, mark it as "mice produced planned"				
                for (int j = 0; j < alleleNames.size(); j++) {
                    String alleleName = alleleNames.get(j).toString();
                    if (!alleleName.equals("") && !alleleName.equals("None") && mouseStatus.get(j).toString().equals("")) {
                        sh.put("mice production planned", "yes");
                    }
                }

                if (sh.containsKey("Mice Produced")) {
                    miceStatus = "<a class='status done' oldtitle='Mice Produced' title=''>"
                            + "<span>Mice</span>"
                            + "</a>";

                    exportMiceStatus.add("mice produced");
                } else if (sh.containsKey("Assigned for Mouse Production and Phenotyping")) {
                    miceStatus = "<a class='status inprogress' oldtitle='Mice production in progress' title=''>"
                            + "<span>Mice</span>"
                            + "</a>";
                    exportMiceStatus.add("mice production in progress");
                } else if (sh.containsKey("mice production planned")) {
                    miceStatus = "<a class='status none' oldtitle='Mice production planned' title=''>"
                            + "<span>Mice</span>"
                            + "</a>";
                    exportMiceStatus.add("mice production in progress");
                }
            }
        } catch (Exception e) {
            log.error("Error getting ES cell/Mice status");
            log.error(e.getLocalizedMessage());
        }

        if (toExport) {
            exportMiceStatus.add(0, esCellStatus); // want to keep this at front
            return StringUtils.join(exportMiceStatus, ", ");
        }
        return esCellStatus + miceStatus;

    }

    /**
     * Get the production status of ES cells/mice for a document.
     *
     * @param doc represents a gene with imits status fields
     * @return the latest status at the gene level for ES cells and all statuses
     * at the allele level for mice as a comma separated string
     */
    public String deriveProductionStatusForEsCellAndMice(JSONObject doc, HttpServletRequest request, boolean toExport) {

        String esCellStatus = fetchEsCellStatus(doc, request, toExport);
        String miceStatus = "";
        final List<String> exportMiceStatus = new ArrayList<String>();

        String patternStr = "(tm.*)\\(.+\\).+"; // allele name pattern
        Pattern pattern = Pattern.compile(patternStr);

        try {

			// mice production status
            // Mice: blue tm1/tm1a/tm1e... mice (depending on how many allele docs) 
            if (doc.containsKey("mouse_status")) {

                JSONArray alleleNames = doc.getJSONArray("allele_name");
                JSONArray mouseStatus = doc.getJSONArray("mouse_status");

                for (int i = 0; i < mouseStatus.size(); i++) {
                    String mouseStatusStr = mouseStatus.get(i).toString();

                    if (mouseStatusStr.equals("Mice Produced")) {
                        String alleleName = alleleNames.getString(i).toString();
                        Matcher matcher = pattern.matcher(alleleName);
						//System.out.println(matcher.toString());

                        if (matcher.find()) {
                            String alleleType = matcher.group(1);
                            miceStatus += "<span class='status done' oldtitle='" + mouseStatusStr + "' title=''>"
                                    + "<span>Mice<br>" + alleleType + "</span>"
                                    + "</span>";

                            exportMiceStatus.add(alleleType + " mice produced");
                        }
                    } else if (mouseStatusStr.equals("Assigned for Mouse Production and Phenotyping")) {
                        String alleleName = alleleNames.getString(i).toString();
                        Matcher matcher = pattern.matcher(alleleName);
						//System.out.println(matcher.toString());

                        if (matcher.find()) {
                            String alleleType = matcher.group(1);
                            miceStatus += "<span class='status inprogress' oldtitle='Mice production in progress' title=''>"
                                    + "<span>Mice<br>" + alleleType + "</span>"
                                    + "</span>";
                            exportMiceStatus.add(alleleType + " mice production in progress");
                        }
                    }
                }
                // if no mice status found but there is already allele produced, mark it as "mice produced planned"				
                for (int j = 0; j < alleleNames.size(); j++) {
                    String alleleName = alleleNames.get(j).toString();
                    if (!alleleName.equals("") && !alleleName.equals("None") && mouseStatus.get(j).toString().equals("")) {
                        Matcher matcher = pattern.matcher(alleleName);
						//System.out.println(matcher.toString());

                        if (matcher.find()) {
                            String alleleType = matcher.group(1);
                            miceStatus += "<span class='status none' oldtitle='Mice production planned' title=''>"
                                    + "<span>Mice<br>" + alleleType + "</span>"
                                    + "</span>";

                            exportMiceStatus.add(alleleType + " mice production planned");
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error getting ES cell/Mice status");
            log.error(e.getLocalizedMessage());
        }

        if (toExport) {
            exportMiceStatus.add(0, esCellStatus); // want to keep this at front
            return StringUtils.join(exportMiceStatus, ", ");
        }
        return esCellStatus + miceStatus;

    }

    /**
     * Get the latest production status of ES cells for a document.
     *
     * @param doc represents a gene with imits status fields
     * @return the latest status at the gene level for ES cells as a string
     */
    public String fetchEsCellStatus(JSONObject doc, HttpServletRequest request, boolean toExport) {

        String mgiId = doc.getString("mgi_accession_id");
        String geneUrl = request.getAttribute("baseUrl") + "/genes/" + mgiId;

        String esCellStatus = "";
        String exportEsCellStatus = "";
        try {
            final String field = "latest_es_cell_status";
            // ES cell production status
            if (doc.containsKey(field)) {
                // blue es cell status				
                esCellStatus = doc.getString(field);
                if (esCellStatus.equals("ES Cell Targeting Confirmed")) {
                    esCellStatus = "<a id='status_done_999' class='status done' href='" + geneUrl + "' title='' data-mgiId='" + mgiId + "'>"
                            + " <span>ES cells</span>"
                            + "</a>";

                    exportEsCellStatus += "Es cells produced";
                } else if (esCellStatus.equals("ES Cell Production in Progress")) {
                    esCellStatus = "<span class='status inprogress' oldtitle='ES cells production in progress' title=''>"
                            + "	<span>ES Cells</span>"
                            + "</span>";

                    exportEsCellStatus += "Es cells production in progress";
                } else {
                    esCellStatus = "";
                    exportEsCellStatus = "No ES cell produced";
                }
            }
        } catch (Exception e) {
            log.error("Error getting ES cell/Mice status");
            log.error(e.getLocalizedMessage());
        }

        if (toExport) {
            return exportEsCellStatus;
        }
        return esCellStatus;
    }

    /**
     * Get the latest phenotyping status for a document.
     *
     * @param doc represents a gene with imits status fields
     * @return the latest status (Complete or Started or Phenotype Attempt
     * Registered) as appropriate for this gene
     */
    public String deriveLatestPhenotypingStatus(JSONObject doc) {

        final String field = "latest_phenotype_status";
        try {
            // Phenotyping complete			
            if (doc.containsKey(field) && !doc.getString(field).equals("")) {
                String val = doc.getString(field);
                if (val.equals("Phenotyping Started") || val.equals("Phenotyping Complete")) {
                    return "Available";
                }
            }

            // for legacy data: indexed through experiment core (so not want Sanger Gene or Allele cores)
            if (doc.containsKey("hasQc")) {
                return "QCed data available";
            }
        } catch (Exception e) {
            log.error("Error getting phenotyping status");
            log.error(e.getLocalizedMessage());
        }

        return "NA";
    }
    /*public String deriveLatestPhenotypingStatus(JSONObject doc) {

     // order of status: latest to oldest (IMPORTANT for deriving correct
     // status)

     try {
     // Phenotyping complete
     if (doc.containsKey("imits_phenotype_complete")) {
     JSONArray complete = doc
     .getJSONArray("imits_phenotype_complete");
     for (Object c : complete) {
     if (c.toString().equals("1")) {
     return "Complete";
     }
     }
     }

     // Phenotyping started
     if (doc.containsKey("imits_phenotype_started")) {
     JSONArray started = doc.getJSONArray("imits_phenotype_started");
     for (Object s : started) {
     if (s.toString().equals("1")) {
     return "Started";
     }
     }
     }

     // Phenotype Attempt Registered
     if (doc.containsKey("imits_phenotype_status")) {
     JSONArray statuses = doc.getJSONArray("imits_phenotype_status");
     for (Object s : statuses) {
     if (s.toString().equals("Phenotype Attempt Registered")) {
     return "Attempt Registered";
     }
     }
     }
     if (doc.containsKey("hasQc")) {				
     return "QCed data available";			
     }
			
     } catch (Exception e) {
     log.error("Error getting phenotyping status");
     log.error(e.getLocalizedMessage());
     }

     // if all the above fails: no phenotyping data yet
     //return "Not Applicable";
     return "";
		
     }*/

    /**
     * Generates a map of label, field, link representing a facet field
     *
     * @param names an array of strings representing the facet ID and the name
     * of the facet
     * @param baseUrl the base url of the generated links
     * @return a map represneting the facet, facet label and link
     */
    public Map<String, String> renderFacetField(String[] names, String baseUrl) {

        // key: display label, value: facetField
        Map<String, String> hm = new HashMap<String, String>();
        String name = names[0];
        String id = names[1];

        if (id.startsWith("MP:")) {
            String url = baseUrl + "/phenotypes/" + id;
            hm.put("label", "MP");
            hm.put("field", "annotationTermName");
            //hm.put("field", "mpTermName");
            hm.put("link", "<a href='" + url + "'>" + name + "</a>");
        } else if (id.startsWith("MA:")) {
            String url = baseUrl + "/anatomy/" + id;
            hm.put("label", "MA");
            hm.put("field", "annotationTermName");
            hm.put("link", name);
        } else if (id.equals("exp")) {
            hm.put("label", "Procedure");
            hm.put("field", "expName");
            hm.put("link", name);
        } else if (id.startsWith("MGI:")) {
            String url = baseUrl + "/genes/" + id;
            hm.put("label", "Gene");
            hm.put("field", "symbol");
            hm.put("link", "<a href='" + url + "'>" + name + "</a>");
        }
        return hm;
    }

    /**
     * Merge all the facets together based on whether they include an underscore
     * because underscore facet names means that the solr field name represents
     * a facet and it's identifier, which are the ones we are concerned with.
     *
     * Each facet is formatted as an array where, starting at the zeroth
     * element, it alternates between facet name and count for that facet
     *
     * @param facetFields the json representation of all the facets as arrays
     * @return a json array which combined all the passed in facets filtered for
     * inclusion of underscore
     */
    public JSONArray mergeFacets(JSONObject facetFields) {

        JSONArray fields = new JSONArray();

        // Initialize a list on creation using an inner anonymous class
        List<String> facetNames = new ArrayList<String>() {
            private static final long serialVersionUID = 1L;

            {
                add("symbol_gene");
                add("expName_exp");
                add("mpTermName");
                add("maTermName");
            }
        };
        for (String facet : facetNames) {

            JSONArray arr = facetFields.getJSONArray(facet);
            for (int i = 0; i < arr.size(); i = i + 2) {
				// We only want facet fields that contain an underscore
                // as it contains ID info we want
                if (((String) arr.get(i)).contains("_")) {
                    fields.add(arr.get(i));
                    fields.add(arr.get(i + 1));
                }
            }
        }

        return fields;
    }

    /**
     * Get the IMPC status for a gene identified by accession id.
     *
     * @param accession the MGI id of the gene in question
     * @return the status
     * @throws IOException
     * @throws URISyntaxException
     */
    public String getGeneStatus(String accession) throws IOException,
            URISyntaxException {

        String url = config.get("internalSolrUrl")
                + "/gene/select?wt=json&q=mgi_accession_id:"
                + accession.replace(":", "\\:");

        log.info("url for geneDao=" + url);

        JSONObject json = getResults(url);
        JSONArray docs = json.getJSONObject("response").getJSONArray("docs");

        if (docs.size() > 1) {
            log.error("Error, Only expecting 1 document from an accession/gene request");
        }

        String geneStatus = docs.getJSONObject(0).getString("status");

        log.debug("gene status=" + geneStatus);

        return geneStatus;
    }

    public List<Map<String, String>> getGenesWithPhenotypeStartedFromAll()
            throws IOException, URISyntaxException {
        List<Map<String, String>> geneStatuses = new ArrayList<Map<String, String>>();
        String url = config.get("internalSolrUrl")
                + "/gene/select?wt=json&q=*%3A*&version=2.2&start=0&rows=100";// 2147483647";//max
        // size of int to make sure we get back all the rows in index

        log.info("url for geneDao=" + url);

        JSONObject json = getResults(url);
        JSONArray docs = json.getJSONObject("response").getJSONArray("docs");
        for (Object doc : docs) {
            JSONObject jsonObject = (JSONObject) doc;
            String geneStatus = this.deriveLatestPhenotypingStatus(jsonObject);
            if (geneStatus.equals("Started")) {
                String mgi = jsonObject.getString("mgi_accession_id");
                String symbol = jsonObject.getString("marker_symbol");
                Map map = new HashMap<String, String>();
                map.put("mgi", mgi);
                map.put("symbol", symbol);
                geneStatuses.add(map);
            }
        }
        return geneStatuses;
    }

    /**
     * Get the results of a query from the provided url.
     *
     * @param url the URL from which to get the content
     * @return a JSONObject representing the result of the query
     * @throws IOException
     * @throws URISyntaxException
     */
    public JSONObject getResults(String url) throws IOException,
            URISyntaxException {

        log.debug("GETTING CONTENT FROM: " + url);

        HttpProxy proxy = new HttpProxy();
        String content = proxy.getContent(new URL(url));

        return (JSONObject) JSONSerializer.toJSON(content);
    }

    /**
     * Get the results of a query from the provided url using a proxy that
     * includes the drupal session cookie.
     *
     * @param url the URL from which to get the content
     * @return a JSONObject representing the result of the query
     * @throws IOException
     * @throws URISyntaxException
     */
    public JSONObject getResults(DrupalHttpProxy drupalProxy, String url)
            throws IOException, URISyntaxException {
        String content = "";

        log.debug("GETTING CONTENT FROM: " + url);
        System.out.println("CHK URL: " + url);
        if (drupalProxy != null) {
            content = drupalProxy.getContent(new URL(url));
        } else {
            return getResults(url);
        }

        return (JSONObject) JSONSerializer.toJSON(content);
    }

    /**
     * Get the MP solr document associated to a specific MP term. The document
     * contains the relations to MA terms and sibling MP terms
     *
     * @param phenotype_id the MP term in question
     * @return a (json) solr document with the results of doing a solr query on
     * the mp index for the mp term in question
     * @throws IOException
     * @throws URISyntaxException
     */
    public JSONObject getMpData(String phenotype_id) throws IOException,
            URISyntaxException {
        String url = config.get("internalSolrUrl")
                + "/mp/select?wt=json&qf=mp_id&defType=edismax&q="
                + phenotype_id;

        return getResults(url);
    }

    public List<Map<String, String>> getGeneAlleleInfo(String accession)
            throws IOException, URISyntaxException {

        String url = "http://ikmc.vm.bytemark.co.uk:8983/solr/allele/search?q=mgi_accession_id:"
                + accession.replace(":", "\\:")
                + " AND product_type:Mouse"
                + "&start=0&rows=100&hl=true&wt=json";

        log.info("url for geneAllele=" + url);

        JSONObject jsonObject = getResults(url);
        int numberFound = Integer.parseInt(jsonObject.getJSONObject("response").getString("numFound"));

        JSONArray docs = jsonObject.getJSONObject("response").getJSONArray("docs");

        if (docs.size() < 1) {
            log.info("No Mice returned for the query!");
        }

        List<String> mouseConstructs = new ArrayList<String>();
        List<Map<String, String>> esCellConstructs = new ArrayList<Map<String, String>>();
        List<Map<String, String>> nonTargetedEsCellConstructs = new ArrayList<Map<String, String>>();
        List<Map<String, String>> geneConstructs = new ArrayList<Map<String, String>>();
        List<Map<String, String>> constructs = new ArrayList<Map<String, String>>();

        try {
            for (int i = 0; i < numberFound; i++) {
                Map<String, String> construct = new HashMap<String, String>();
                constructs.add(geneAlleleConstruct(docs, i));
                mouseConstructs.add(docs.getJSONObject(i).getString("allele_name"));
                // }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String esCellUrl = "http://ikmc.vm.bytemark.co.uk:8983/solr/allele/search?q=mgi_accession_id:"
                + accession.replace(":", "\\:")
                + " AND product_type:ES Cell"
                + "&start=0&rows=100&hl=true&wt=json";

        log.info("url for geneAllele=" + esCellUrl);

        JSONObject esCellJsonObject = getResults(esCellUrl);
        int esCellNumberFound = Integer.parseInt(esCellJsonObject.getJSONObject("response").getString("numFound"));

        JSONArray esCellDocs = esCellJsonObject.getJSONObject("response").getJSONArray("docs");

        if (esCellDocs.size() < 1) {
            log.info("No EsCells returned for the query!");
        }

        try {
            for (int i = 0; i < esCellNumberFound; i++) {

                if (!mouseConstructs.contains(esCellDocs.getJSONObject(i).getString("allele_name"))) {
                    if (esCellDocs.getJSONObject(i).getString("allele_type").equals("Targeted Non Conditional")) {
                        nonTargetedEsCellConstructs.add(geneAlleleConstruct(esCellDocs, i));
                    } else {
                        esCellConstructs.add(geneAlleleConstruct(esCellDocs, i));
                    }
                }
                // }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String geneUrl = "http://ikmc.vm.bytemark.co.uk:8983/solr/allele/search?q=mgi_accession_id:"
                + accession.replace(":", "\\:")
                + " AND type:gene"
                + "&start=0&rows=100&hl=true&wt=json";

        log.info("url for geneAllele=" + geneUrl);

        JSONObject geneJsonObject = getResults(geneUrl);
        int geneNumberFound = Integer.parseInt(geneJsonObject.getJSONObject("response").getString("numFound"));

        JSONArray geneDocs = geneJsonObject.getJSONObject("response").getJSONArray("docs");

        if (geneDocs.size() < 1) {
            log.info("No gene info returned for the query!");
        }

        try {
            for (int i = 0; i < geneNumberFound; i++) {
                if (geneDocs.getJSONObject(i).has("vector_project_ids") && geneDocs.getJSONObject(i).getString("vector_project_ids").length() > 0) {
                    geneConstructs.add(geneAlleleConstruct(geneDocs, i));
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        constructs.addAll(esCellConstructs);
        if (constructs.size() < 1) {
            constructs.addAll(nonTargetedEsCellConstructs);
        }
        constructs.addAll(geneConstructs);
        return constructs;
    }

    private Map<String, String> geneAlleleConstruct(JSONArray docs, int i) {
        Map<String, String> construct = new HashMap<String, String>();
        String markerSymbol = "";
        String product = "";
        String alleleType = "";
        String type = "";
        String strainOfOrigin = "";
        String mgiAlleleName = "";
        String alleleMap = "";
        String alleleGenbankFile = "";
        String ikmcProjectId = "";
        String orderFromNames = "";
        String orderFromUrls = "";
        String orderHtml = "";
        String vectorProjectIds = "";
        String vectorProjectHtml = "";

        if (docs.getJSONObject(i).has("marker_symbol")) {
            markerSymbol = docs.getJSONObject(i).getString("marker_symbol");
        }
        if (docs.getJSONObject(i).has("product_type")) {
            product = docs.getJSONObject(i).getString("product_type");
        }
        if (docs.getJSONObject(i).has("type")) {
            type = docs.getJSONObject(i).getString("type");
        }
        if (docs.getJSONObject(i).has("allele_type")) {
            alleleType = docs.getJSONObject(i).getString("allele_type");
            if (alleleType.equals("Conditional Ready")) {
                alleleType = "Knockout First, Reporter-tagged insertion with conditional potential";
            } else if (alleleType.equals("Deletion")) {
                alleleType = "Reporter-Tagged Deletion";
            }
        }
        if (docs.getJSONObject(i).has("strain")) {
            strainOfOrigin = docs.getJSONObject(i).getString("strain");
        }
        if (docs.getJSONObject(i).has("allele_name")) {
            mgiAlleleName = docs.getJSONObject(i).getString("allele_name");
        }
        if (docs.getJSONObject(i).has("allele_image_url")) {
            alleleMap = docs.getJSONObject(i).getString("allele_image_url");
        }
        if (docs.getJSONObject(i).has("genbank_file_url")) {
            alleleGenbankFile = docs.getJSONObject(i).getString("genbank_file_url");
        }
        if (docs.getJSONObject(i).has("project_ids")) {
            JSONArray projectArray = docs.getJSONObject(i).getJSONArray("project_ids");
            if (projectArray.size() > 0) {
                ikmcProjectId = projectArray.getString(0);
            }
        }
        if (docs.getJSONObject(i).has("order_from_names")) {
            orderFromNames = docs.getJSONObject(i).getString("order_from_names");
        }
        if (docs.getJSONObject(i).has("order_from_urls")) {
            orderFromUrls = docs.getJSONObject(i).getString("order_from_urls");
        }
        if (docs.getJSONObject(i).has("order_from_urls") && docs.getJSONObject(i).has("order_from_names")) {
            JSONArray orderUrlsArray = docs.getJSONObject(i).getJSONArray("order_from_urls");
            JSONArray orderNamesArray = docs.getJSONObject(i).getJSONArray("order_from_names");
            for (int j = 0; j < orderNamesArray.size(); j++) {
                orderHtml += "<div style='padding:3px'><a class='btn' href=" + orderUrlsArray.getString(j) + "><i class='fa fa-shopping-cart'></i> " + orderNamesArray.getString(j) + "</a></div>";
            }
        }
        if (docs.getJSONObject(i).has("vector_project_ids")) {
            vectorProjectIds = docs.getJSONObject(i).getString("vector_project_ids");
        }
        if (docs.getJSONObject(i).has("vector_project_ids")) {
            JSONArray vectorProjectsArray = docs.getJSONObject(i).getJSONArray("vector_project_ids");
            for (int k = 0; k < vectorProjectsArray.size(); k++) {
                vectorProjectHtml += "<a href=http://www.mousephenotype.org/martsearch_ikmc_project/martsearch/ikmc_project/" + vectorProjectsArray.getString(k) + ">" + vectorProjectsArray.getString(k) + "</a> ";
            }
        }

        construct.put("markerSymbol", markerSymbol);
        construct.put("product", product);
        construct.put("alleleType", alleleType);
        construct.put("type", type);
        construct.put("strainOfOrigin", strainOfOrigin);
        construct.put("mgiAlleleName", mgiAlleleName);
        construct.put("alleleMap", alleleMap);
        construct.put("alleleGenbankFile", alleleGenbankFile);
        construct.put("ikmcProjectId", ikmcProjectId);
        construct.put("orderFromNames", orderFromNames);
        construct.put("orderFromUrls", orderFromUrls);
        construct.put("orderHtml", orderHtml);
        construct.put("vectorProjectIds", vectorProjectIds);
        construct.put("vectorProjectHtml", vectorProjectHtml);
        return construct;
    }

    public HashMap<String, HashMap<String, List<String>>>  getAlleleQcInfo(String type, String name)
        throws IOException, URISyntaxException {

        String url = "http://ikmc.vm.bytemark.co.uk:8985/solr/product/search?q=name:"
                + name
                + " AND type:"
                + type
                + "&start=0&rows=100&hl=true&wt=json";

        log.info("url for productQc=" + url);

        JSONObject jsonObject = getResults(url);
        int numberFound = Integer.parseInt(jsonObject.getJSONObject("response").getString("numFound"));

        JSONArray docs = jsonObject.getJSONObject("response").getJSONArray("docs");
        HashMap<String, HashMap<String, List<String>>> construct = new HashMap<String, HashMap<String, List<String>>>();

        if (docs.size() < 1) {
            log.info("No " + type + "found with a name equal to " + name);
        } 
        else {
        try {
            if (docs.getJSONObject(0).has("qc_data") && docs.getJSONObject(0).getString("qc_data").length() > 0) {
                construct = extractQcData(docs , 0);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            }            
        }
        return construct;
    }
    
    private HashMap<String, HashMap<String, List<String>>> extractQcData(JSONArray docs, int i) {
        HashMap<String, HashMap<String, List<String>>> deep = new HashMap<String, HashMap<String, List<String>>>();
        
        String[] qc;
        qc = new String[3];
        String qc_group;
        String qc_type;
        String qc_result;
        
        JSONArray qcDataArray = docs.getJSONObject(i).getJSONArray("qc_data");
        for (int j = 0; j < qcDataArray.size(); j++) {
            qc = qcDataArray.getString(j).split(":");
            qc_group = qc[0];
            qc_type = qc[1];
            qc_result = qc[2];
            if (!deep.containsKey(qc_group)){
                deep.put(qc_group, new HashMap<String, List<String>>());
                deep.get(qc_group).put("fieldNames", new ArrayList());
                deep.get(qc_group).put("values", new ArrayList());
            }
            deep.get(qc_group).get("fieldNames").add(qc_type);
            deep.get(qc_group).get("values").add(qc_result);
        }
        return deep;
    }
            
    public JSONObject getImageInfo(int imageId) throws SolrServerException,
            IOException, URISyntaxException {

        String url = config.get("internalSolrUrl")
                + "/images/select?wt=json&q=id:" + imageId;
        JSONObject json = getResults(url);
        JSONArray docs = json.getJSONObject("response").getJSONArray("docs");

        if (docs.size() > 1) {
            log.error("Error, Only expecting 1 document from an accession/gene request");
        }
        if (docs.size() < 1) {//if nothing returned return an empty json object
            return new JSONObject();
        }

        JSONObject imageInfo = docs.getJSONObject(0);
        return imageInfo;

    }

    public Map<String, JSONObject> getExampleImages(int controlImageId,
            int expImageId) throws SolrServerException, IOException,
            URISyntaxException {
        Map<String, JSONObject> map = new HashMap<String, JSONObject>();
        JSONObject controlDocument = this.getImageInfo(controlImageId);
        JSONObject expDocument = this.getImageInfo(expImageId);

        map.put("control", controlDocument);
        map.put("experimental", expDocument);
        return map;
    }

    public List<Map<String, String>> getGeneAllele2Info(String accession, String allele_name) throws IOException, URISyntaxException {

        String qallele_name = "";
        if(allele_name != null) {
            qallele_name = " allele_name:\"" + allele_name + "\"";            
        }
        
        String url = "http://ikmc.vm.bytemark.co.uk:8985/solr/allele2/search?q=mgi_accession_id:"
                + accession.replace(":", "\\:")
                + qallele_name
                + "&start=0&rows=100&hl=true&wt=json";

        log.info("#### url for getGeneAllele2Info=" + url);

        JSONObject jsonObject1 = getResults(url);

        JSONArray docs = jsonObject1.getJSONObject("response").getJSONArray("docs");

        if (docs.size() < 1) {
            log.info("#### No rows returned for the query!");
            return null;
        }

//        "genbank_file":"https://www.mousephenotype.org/imits/targ_rep/alleles/190/escell_clone_cre_genbank_file",
//        "allele_image":"https://www.mousephenotype.org/imits/targ_rep/alleles/190/allele_image_cre",
       
        String[] stringArray = new String[]{"mgi_accession_id", "marker_symbol", "marker_type", "feature_type", "latest_project_status", 
            "latest_phenotype_started", "latest_phenotype_complete",
            "latest_phenotype_status", "latest_es_cell_status", "latest_mouse_status", "latest_project_status_legacy",
            "es_cell_status",  "mouse_status", "phenotype_status", "production_centre", "phenotyping_centre", "allele_name",
            "allele_type", "type", "genbank_file", "allele_image"
        };

        List<Map<String, String>> genes = new ArrayList<>();

        for (Object doc : docs) {
            JSONObject jsonObject2 = (JSONObject) doc;

            HashMap<String, String> map = new HashMap<>();

            for (String s : stringArray) {
                if(jsonObject2.has(s)) {
                    String o = jsonObject2.getString(s);
                    map.put(s, o);
                }
            }

//            if (jsonObject2.has("allele_type")) {
//                if (jsonObject2.getString("allele_type").equals("Conditional Ready")) {
//                    map.put("alleleType", "Knockout First, Reporter-tagged insertion with conditional potential");
//                } else if (jsonObject2.getString("allele_type").equals("Deletion")) {
//                    map.put("alleleType", "Reporter-Tagged Deletion");
//                }
//            }

            genes.add(map);
        }

        return genes;
    }

    private static final String NOT_FOUND = "NOT-FOUND";
    private static final String NOT_COMPLETE = "placeholder";
    //private static final String NOT_AVAILABLE = "not-available";
    
    private String getGeneProductInfoArrayEntry(String key, JSONArray item) throws IOException, URISyntaxException {
        if(item == null) {
            return null;
        }
        
        for (Object o : item) {
            String s = (String) o;
            Pattern pattern = Pattern.compile(key + ":(.+)");
            Matcher matcher = pattern.matcher(s);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }
        return null;
    }

    private List<Map<String, Object>> getGeneProductInfoOrderInfo(JSONObject jsonObject2) throws IOException, URISyntaxException {
        List<Map<String, Object>> orders = new ArrayList<>();
        HashMap<String, Object> map3 = new HashMap<>();
        
        if(!jsonObject2.has("order_names") || !jsonObject2.has("order_links")) {
            return null;
        }       
        
        JSONArray array_order_names = jsonObject2.getJSONArray("order_names");
        JSONArray array_order_links = jsonObject2.getJSONArray("order_links");
        for (int k = 0; k < array_order_names.size(); k++) {
            String name = array_order_names.getString(k);
            if (name.equals("NULL")) {
                name = NOT_FOUND;
            }
            map3.put("name", name);
            map3.put("url", array_order_links.getString(k));
            orders.add(map3);
        }
        return orders;
    }

    private Map<String, Object> getGeneProductInfoMice(JSONObject jsonObject2) throws IOException, URISyntaxException {
        String type = jsonObject2.getString("type");

        if (!type.equals("mouse")) {
            return null;
        }

        HashMap<String, Object> map2 = new HashMap<>();

        map2.put("orders", getGeneProductInfoOrderInfo(jsonObject2));

        map2.put("production_centre", jsonObject2.getString("production_centre"));

        String background_colony_strain = getGeneProductInfoArrayEntry("background_colony_strain", jsonObject2.getJSONArray("genetic_info"));

       // map2.put("genetic_background", NOT_AVAILABLE);

      //  if (background_colony_strain != null && background_colony_strain.length() > 0) {
            map2.put("genetic_background", background_colony_strain);
      //  }

        String es_cell = jsonObject2.getString("associated_product_es_cell_name");
        if (es_cell.length() < 1) {
            es_cell = NOT_FOUND;
        }

        map2.put("es_cell", es_cell);
        map2.put("qc_data", NOT_COMPLETE);
        
        if(!es_cell.equals(NOT_FOUND)) {
            map2.put("southern_tool", "http://www.sanger.ac.uk/htgt/htgt2/tools/restrictionenzymes?es_clone_name="+es_cell+"&iframe=true&width=100%&height=100%");
        }
        
        map2.put("production_completed", jsonObject2.getString("production_completed"));

        log.info("#### MOUSE production_completed:" + map2.get("production_completed"));
        
        return map2;
    }
    
    // see https://www.mousephenotype.org/martsearch_ikmc_project/aboutkompstrategies

    private Map<String, String> getGeneProductInfoTypeMapping() {
        Map<String, String> map = new HashMap<>();
        map.put("1a", "KO first allele (reporter-tagged insertion allele)");
        map.put("1b", "Reporter-tagged deletion allele (post-Cre)");
        map.put("1c", "Conditional allele (post-Flp)");
        map.put("1d", "Deletion allele (post-Flp and Cre with no reporter)");
        map.put("1e", "targeted, non-conditional allele");
        map.put("1", "Reporter-tagged deletion allele (with selection cassette)");
        map.put("1.1", "Reporter-tagged deletion allele (post Cre, with no selection cassette)");
        map.put("1.2", "Reporter-tagged deletion allele (post Flp, with no reporter and selection cassette)");
        return map;
    }
    
    private Map<String, Object> getGeneProductInfoEsCells(JSONObject jsonObject2) throws IOException, URISyntaxException {
        String type = jsonObject2.getString("type");

        if (!type.equals("es_cell")) {
            return null;
        }
       
        HashMap<String, Object> map2 = new HashMap<>();
        map2.put("orders", getGeneProductInfoOrderInfo(jsonObject2));
        
        String parental_cell_line = getGeneProductInfoArrayEntry("parent_es_cell_line", jsonObject2.getJSONArray("genetic_info"));

        if (parental_cell_line != null) {
            map2.put("parental_cell_line", parental_cell_line);
        }

        String strain = getGeneProductInfoArrayEntry("strain", jsonObject2.getJSONArray("genetic_info"));

        if (strain != null) {
            map2.put("es_cell_strain", strain);        
            map2.put("genetic_background", strain);
        }
        
        map2.put("es_cell_clone", jsonObject2.getString("name"));
        
        String targeting_vectors = jsonObject2.getString("associated_product_vector_name");        
        
        map2.put("targeting_vector", targeting_vectors);

        map2.put("southern_tool", "http://www.sanger.ac.uk/htgt/htgt2/tools/restrictionenzymes?es_clone_name="+jsonObject2.getString("name")+"&iframe=true&width=100%&height=100%");
        
        map2.put("qc_data", NOT_COMPLETE);

        map2.put("production_completed", jsonObject2.getString("production_completed"));

        return map2;
    }
   
    private Map<String, Object> getGeneProductInfoTargetingVectors(JSONObject jsonObject2) throws IOException, URISyntaxException {
        if (!jsonObject2.getString("type").equals("targeting_vector")) {
            return null;
        }
                
        HashMap<String, Object> map2 = new HashMap<>();
        map2.put("orders", getGeneProductInfoOrderInfo(jsonObject2));

        String cassette = getGeneProductInfoArrayEntry("cassette", jsonObject2.getJSONArray("genetic_info"));
        map2.put("cassette", cassette);

        map2.put("targeting_vector", jsonObject2.getString("name"));
        
        String backbone = getGeneProductInfoArrayEntry("backbone", jsonObject2.getJSONArray("genetic_info"));
        map2.put("backbone", backbone);
        
        map2.put("production_completed", jsonObject2.getString("production_completed"));

        String genbank_file_url = getGeneProductInfoArrayEntry("genbank_file", jsonObject2.getJSONArray("other_links"));
        if (genbank_file_url != null) {
            map2.put("genbank_file_url", genbank_file_url);
        }

        log.info("#### url for genbank_file_url:" + genbank_file_url);

        String design_oligos_url = getGeneProductInfoArrayEntry("design_link", jsonObject2.getJSONArray("other_links"));
        if (genbank_file_url != null) {
            map2.put("design_oligos_url", design_oligos_url);
        }

        log.info("#### url for design_oligos_url:" + design_oligos_url);
        
        return map2;
    }

    public Map<String, Object> getGeneProductInfo(String accession, String allele_name) throws IOException, URISyntaxException, Exception {

        String qallele_name = "";
        if(allele_name != null) {
            qallele_name = " allele_name:\"" + allele_name + "\"";            
        }
        
            String url = "http://ikmc.vm.bytemark.co.uk:8985/solr/product/search?q=mgi_accession_id:"
                + accession.replace(":", "\\:")
                + qallele_name
                + "&start=0&rows=100&hl=true&wt=json";

        log.info("#### url for getGeneProductInfo=" + url);

        JSONObject jsonObject1 = getResults(url);

        JSONArray docs = jsonObject1.getJSONObject("response").getJSONArray("docs");

        if (docs.size() < 1) {
            log.info("#### No rows returned for the query!");
            return null;
        }

        Map<String, Object> genes = new HashMap<>();
        List<Map<String, Object>> mice = new ArrayList<>();
        List<Map<String, Object>> es_cells = new ArrayList<>();
        List<Map<String, Object>> targeting_vectors = new ArrayList<>();
        boolean mice_complete = true;

        for (Object doc : docs) {
            JSONObject jsonObject2 = (JSONObject) doc;
            String type = jsonObject2.getString("type");

            if (type.equals("mouse")) {
                Map<String, Object> mouse = getGeneProductInfoMice(jsonObject2);
                if(mouse.get("production_completed") == "false") {
                    mice_complete = false;
                }
                mice.add(mouse);
            }

            if (type.equals("es_cell")) {
                es_cells.add(getGeneProductInfoEsCells(jsonObject2));
            }

            if (type.equals("targeting_vector")) {
                targeting_vectors.add(getGeneProductInfoTargetingVectors(jsonObject2));
            }
        }

        genes.put("mice", mice);
        genes.put("es_cells", es_cells);
        genes.put("targeting_vectors", targeting_vectors);

        List<Map<String, String>> constructs2 = getGeneAllele2Info(accession, allele_name);
        log.info("#### constructs2: " + constructs2.toString()); 
        
        List<HashMap<String, Object>> summaries = new ArrayList<>();
        
        Map<String, String> mapping = getGeneProductInfoTypeMapping();

        for(Map<String, String> item : constructs2) {
            HashMap<String, Object> summary = new HashMap<>();
            summary.put("marker_symbol", item.get("marker_symbol"));
//            summary.put("phenotype_status", item.get("phenotype_status"));
//            summary.put("es_cell_status", item.get("es_cell_status"));
//            summary.put("mouse_status", item.get("mouse_status"));
            summary.put("symbol", item.get("marker_symbol") + "<sup>" + item.get("allele_name") + "</sup>");
//            complete 
            // "genbank_file", "allele_image"
            summary.put("genbank", item.get("genbank_file"));
            summary.put("map_image", item.get("allele_image"));
            summary.put("allele_description", mapping.get("1" + item.get("allele_type")));

            
            
            
            
            summary.put("mutagenesis_url", "http://www.google.com");    // FIX-ME!
            
            
//  statuses:
//  - {DETAILS: 'http://www.sanger.ac.uk', TEXT: There are mice for this allele, ORDER: 'http://www.sanger.ac.uk'}
//  - {DETAILS: 'http://www.google.com', TEXT: There are ES cells for this allele, ORDER: 'http://www.google.com'}
            
            HashMap<String, String> map2 = new HashMap<>();
            map2.put("DETAILS", "http://www.sanger.ac.uk");
            map2.put("TEXT", "There are mice for this allele");
            map2.put("ORDER", "http://www.sanger.ac.uk");
            List<HashMap<String, String>> list2 = new ArrayList<>();
            list2.add(map2);

            summary.put("statuses", list2);    // FIX-ME!
            
//  browsers:
//  - {browser: Ensembl (mouse), url: 'http://www.ensembl.org/Mus_musculus/Location/View?r=9:54544794-54560…c.uk/das/ikmc_products=normal,contig=normal,ruler=normal,scalebar=normal'}
//  - {browser: UCSC (mouse), url: 'http://genome.ucsc.edu/cgi-bin/hgTracks?db=mm10&ikmc=pack&ensGene=pack&position=chr9:54544794-54560218'}

            HashMap<String, String> map3 = new HashMap<>();
            map3.put("browser", "Ensembl (mouse)");
            map3.put("url", "http://www.ensembl.org/Mus_musculus/Location/View?r=9:54544794-54560…c.uk/das/ikmc_products=normal,contig=normal,ruler=normal,scalebar=normal");
            List<HashMap<String, String>> list3 = new ArrayList<>();
            list3.add(map3);
            
            summary.put("browsers", list3);    // FIX-ME!
            
//  tools:
//  - {name: Genotyping primers, url: 'http://www.google.com'}
//  - {name: TAQMan LOA primers, url: 'http://www.google.com'}
            
            HashMap<String, String> map4 = new HashMap<>();
            map4.put("name", "Genotyping primers");
            map4.put("url", "http://www.google.com");
            List<HashMap<String, String>> list4 = new ArrayList<>();
            list4.add(map4);
            
            summary.put("tools", list4);    // FIX-ME!

            
            
            
            
            
            summaries.add(summary);
        }

//    tools
        
        
        
        
    //    genes.put("mice_complete", mice_complete);
        
        if(summaries.size() > 1) {
            log.error("######## invalid count for summary!"); 
           // throw new Exception("#### invalid count for summary!");
        }

        if(summaries.size() > 0) {
            genes.put("summary", summaries.get(0));
        }
        
        log.info("#### summaries: " + summaries.toString()); 

        return genes;
    }
}
