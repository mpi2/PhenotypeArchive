/**
 * Copyright © 2011-2014 Genome Research Limited
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
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Resource;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import uk.ac.ebi.phenotype.web.util.HttpProxy;

@Service
public class SolrIndex2 {

    private final Logger log = Logger.getLogger(this.getClass().getCanonicalName());

    @Resource(name = "globalConfiguration")
    private Map<String, String> config;

    private static final String NOT_FOUND = "NOT-FOUND";
    private static final String NOT_COMPLETE = "placeholder";
    //private static final String ALLELE_NAME_FIELD_ORIGINAL = "allele_name";
    private static final String ALLELE_NAME_FIELD = "allele_name_str";
    //private static final String ALLELE_TYPE_FIELD = "allele_type_str";
    private static final String ALLELE_TYPE_FIELD = "allele_type";
    private static final String ALLELE2_CORE_URL = "http://ikmc.vm.bytemark.co.uk:8985";
    //private static final String ALLELE2_CORE_URL = "http://localhost:8983";

//    private String coreUrl() {
//        Map<String, String> env = System.getenv();
//        if(env.containsKey("ALLELE2_CORE_URL")) {
//            return env.get("ALLELE2_CORE_URL");
//        }
//        return "http://ikmc.vm.bytemark.co.uk:8985";
//    }
//
//    private String alleleNameField() {
//        Map<String, String> env = System.getenv();
//        if(env.containsKey("ALLELE_NAME_FIELD")) {
//            return env.get("ALLELE_NAME_FIELD");
//        }
//        return "allele_name_str";
//    }

    public JSONObject getResults(String url) throws IOException,
            URISyntaxException {

        log.debug("GETTING CONTENT FROM: " + url);

        HttpProxy proxy = new HttpProxy();
        String content = proxy.getContent(new URL(url));

        return (JSONObject) JSONSerializer.toJSON(content);
    }

    public List<Map<String, String>> getGeneAllele2Info(String accession, String allele_name) throws IOException, URISyntaxException {

        String url = getGeneAllele2CoreUrl(accession, allele_name);

        log.info("#### url for getGeneAllele2Info=" + url);

        JSONObject jsonObject1 = getResults(url);

        JSONArray docs = jsonObject1.getJSONObject("response").getJSONArray("docs");

        if (docs.size() < 1) {
            log.info("#### No rows returned for the query!");
            return null;
        }

        String[] stringArray = new String[]{"mgi_accession_id", "marker_symbol", "marker_type", "feature_type", "latest_project_status",
            "latest_phenotype_started", "latest_phenotype_complete",
            "latest_phenotype_status", "latest_es_cell_status", "latest_mouse_status", "latest_project_status_legacy",
            "es_cell_status", "mouse_status", "phenotype_status", "production_centre", "phenotyping_centre", "allele_name",
            "allele_type", "type", "genbank_file", "allele_image", "design_id", "ikmc_project_id"
        };

        List<Map<String, String>> list = new ArrayList<>();

        for (Object doc : docs) {
            JSONObject jsonObject2 = (JSONObject) doc;

            HashMap<String, String> map = new HashMap<>();

            for (String s : stringArray) {
                if (jsonObject2.has(s)) {
                    String o = jsonObject2.getString(s);
                    map.put(s, o);
                } else {
                    map.put(s, "");
                }
            }

            if (jsonObject2.containsKey("links")) {
                JSONArray links = jsonObject2.getJSONArray("links");
                for (Object l : links) {
                    String link = (String) l;
                    if (link.contains("ensembl")) {
                        map.put("ensembl_url", link);
                        break;
                    }
                }
            }

            list.add(map);
        }

        return list;
    }

    private String getGeneProductInfoArrayEntry(String key, JSONArray item) throws IOException, URISyntaxException {
        if (item == null) {
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

    private List<Map<String, Object>> getGeneProductInfoOrderContactInfo(String target, JSONObject jsonObject2) throws IOException, URISyntaxException {
        if (!target.equals("order") && !target.equals("contact")) {
            log.error("#### getGeneProductInfoOrderContactInfo: illegal param: " + target);
            return null;
        }

        List<Map<String, Object>> list = new ArrayList<>();

        if (!jsonObject2.has(target + "_names") || !jsonObject2.has(target + "_links")) {
            return null;
        }

        JSONArray names = jsonObject2.getJSONArray(target + "_names");
        JSONArray links = jsonObject2.getJSONArray(target + "_links");

        if (names.size() != links.size()) {
            log.error("#### getGeneProductInfoOrderContactInfo: arrays unequal size");
            return null;
        }

        boolean allele_has_issue = false;
        if (jsonObject2.has("allele_has_issues")) {
            allele_has_issue = jsonObject2.getString("allele_has_issues").equals("true");
        }

        for (int k = 0; k < names.size(); k++) {
            HashMap<String, Object> item = new HashMap<>();
            String name = names.getString(k);
            if (name.equals("NULL")) {
                name = target;
            }
            item.put("name", name);
            item.put("url", links.getString(k));

            if(allele_has_issue) {
                String allele_id = jsonObject2.has("allele_id") ? jsonObject2.getString("allele_id") : null;
                String product_id = jsonObject2.has("product_id") ? jsonObject2.getString("product_id") : null;
                String host = "https://www.mousephenotype.org/imits";
                //host = "localhost:3000";
                String url = host + "/targ_rep/alleles/" + allele_id + "/show-issue?product_id=" + product_id + "&core=product";
                item.put("url", url);
            }

            list.add(item);
        }
        return list;
    }

    private List<Map<String, Object>> getGeneProductInfoOrderInfo(JSONObject jsonObject2) throws IOException, URISyntaxException {
        List<Map<String, Object>> orders = new ArrayList<>();
//        HashMap<String, Object> map3 = new HashMap<>();

        if (!jsonObject2.has("order_names") || !jsonObject2.has("order_links")) {
            return null;
        }

        if (!jsonObject2.has("production_completed") || (jsonObject2.has("production_completed") && jsonObject2.get("production_completed").toString().equals("false"))) {
            return null;
        }

        log.info("#### getGeneProductInfoOrderInfo: jsonObject2: " + jsonObject2);

        boolean allele_has_issue = false;
        if (jsonObject2.has("allele_has_issues")) {
            allele_has_issue = jsonObject2.getString("allele_has_issues").equals("true");
        }

        JSONArray array_order_names = jsonObject2.getJSONArray("order_names");
        JSONArray array_order_links = jsonObject2.getJSONArray("order_links");
        for (int k = 0; k < array_order_names.size(); k++) {
            HashMap<String, Object> map3 = new HashMap<>();
            String name = array_order_names.getString(k);
            if (name.equals("NULL")) {
                name = "ORDER";
            }
            map3.put("name", name);
            map3.put("url", array_order_links.getString(k));

            if(allele_has_issue) {
                String allele_id = jsonObject2.has("allele_id") ? jsonObject2.getString("allele_id") : null;
                String product_id = jsonObject2.has("product_id") ? jsonObject2.getString("product_id") : null;
                String host = "https://www.mousephenotype.org/imits";
                //host = "localhost:3000";
                String url = host + "/targ_rep/alleles/" + allele_id + "/show-issue?product_id=" + product_id + "&core=product";
                map3.put("url", url);
            }

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
        map2.put("contacts", getGeneProductInfoOrderContactInfo("contact", jsonObject2));

        map2.put("production_centre", jsonObject2.getString("production_centre"));
        map2.put("colony_name", jsonObject2.getString("name"));
        map2.put("allele_type", jsonObject2.getString("allele_type"));
        String background_colony_strain = getGeneProductInfoArrayEntry("background_colony_strain", jsonObject2.getJSONArray("genetic_info"));

        map2.put("genetic_background", background_colony_strain);

        String associated_product_colony_name = "";
        if (jsonObject2.has("associated_product_colony_name")) {
            associated_product_colony_name = jsonObject2.getString("associated_product_colony_name");
            map2.put("associated_product_colony_name", associated_product_colony_name);
        }

        if (jsonObject2.has("marker_symbol")) {
            map2.put("marker_symbol", jsonObject2.getString("marker_symbol"));
        }

        String es_cell = NOT_FOUND;
        map2.put("associated_product_es_cell_name", "");
        if (jsonObject2.has("associated_product_es_cell_name")) {
            es_cell = jsonObject2.getString("associated_product_es_cell_name");
            map2.put("associated_product_es_cell_name", es_cell);
        }

        if (es_cell.length() < 1) {
            es_cell = associated_product_colony_name;
        }

        map2.put("es_cell", es_cell);

        map2.put("associated_product_vector_name", "");
        if (jsonObject2.has("associated_product_vector_name")) {
            map2.put("associated_product_vector_name", jsonObject2.getString("associated_product_vector_name"));
        }

        map2.put("qc_data", NOT_COMPLETE);

        if (!es_cell.equals(NOT_FOUND)) {
            map2.put("southern_tool", "http://www.sanger.ac.uk/htgt/htgt2/tools/restrictionenzymes?es_clone_name=" + es_cell + "&iframe=true&width=100%&height=100%");
        }

        map2.put("production_completed", jsonObject2.getString("production_completed"));

        //    log.info("#### MOUSE production_completed:" + map2.get("production_completed"));
        String production_graph = getGeneProductInfoArrayEntry("production_graph", jsonObject2.getJSONArray("other_links"));

        map2.put("production_graph", production_graph);

        map2.put("_status", jsonObject2.getString("status"));

        map2.put("allele_has_issue", "false");
        if (jsonObject2.has("allele_has_issues")) {
            map2.put("allele_has_issue", jsonObject2.getString("allele_has_issues"));
        }

        log.info("#### getGeneProductInfoMice:" + map2);

        return map2;
    }

    private String stripMGIAccessionId(String mgi_allele_name) {
        String stripped = mgi_allele_name.replaceAll(".+?\\<sup\\>", "");
        stripped = stripped.replaceAll("\\<\\/sup\\>", "");
        return stripped;
    }

    private Map<String, Object> getGeneProductInfoMice2(JSONObject jsonObject2) throws IOException, URISyntaxException {
        String type = jsonObject2.getString("type");

        if (!type.equals("mouse")) {
            return null;
        }

        HashMap<String, Object> map2 = new HashMap<>();

        map2.put("type", type);
        map2.put("product", "Mouse");
        map2.put("orders", getGeneProductInfoOrderInfo(jsonObject2));
        map2.put("contacts", getGeneProductInfoOrderContactInfo("contact", jsonObject2));

        map2.put("allele_description", getAlleleDescription("1" + jsonObject2.get("allele_type")));

      //  map2.put("production_centre", jsonObject2.getString("production_centre"));
        map2.put("colony_name", jsonObject2.getString("name"));
        map2.put("allele_type", jsonObject2.getString("allele_type"));
        String background_colony_strain = getGeneProductInfoArrayEntry("background_colony_strain", jsonObject2.getJSONArray("genetic_info"));

        map2.put("genetic_background", background_colony_strain);

        map2.put("mgi_accession_id", jsonObject2.getString("mgi_accession_id"));

//        String associated_product_colony_name = "";
//        if (jsonObject2.has("associated_product_colony_name")) {
//            associated_product_colony_name = jsonObject2.getString("associated_product_colony_name");
//            map2.put("associated_product_colony_name", associated_product_colony_name);
//        }

        if (jsonObject2.has("marker_symbol")) {
            map2.put("marker_symbol", jsonObject2.getString("marker_symbol"));
        }

        map2.put("allele_name", jsonObject2.getString("allele_name"));



      //  String mgi_allele_name = getTitle(jsonObject2.getString("marker_symbol"), jsonObject2.getString("allele_name")));
        map2.put("mgi_allele_name", getTitle(jsonObject2.getString("marker_symbol"), jsonObject2.getString("allele_name")));
//stripMGIAccessionId

//        String es_cell = NOT_FOUND;
//        map2.put("associated_product_es_cell_name", "");
//        if (jsonObject2.has("associated_product_es_cell_name")) {
//            es_cell = jsonObject2.getString("associated_product_es_cell_name");
//            map2.put("associated_product_es_cell_name", es_cell);
//        }
//
//        if (es_cell.length() < 1) {
//            es_cell = associated_product_colony_name;
//        }

      //  map2.put("es_cell", es_cell);

//        map2.put("associated_product_vector_name", "");
//        if (jsonObject2.has("associated_product_vector_name")) {
//            map2.put("associated_product_vector_name", jsonObject2.getString("associated_product_vector_name"));
//        }
//
//        map2.put("qc_data", NOT_COMPLETE);
//
//        if (!es_cell.equals(NOT_FOUND)) {
//            map2.put("southern_tool", "http://www.sanger.ac.uk/htgt/htgt2/tools/restrictionenzymes?es_clone_name=" + es_cell + "&iframe=true&width=100%&height=100%");
//        }

        map2.put("production_completed", jsonObject2.getString("production_completed"));

     //   String production_graph = getGeneProductInfoArrayEntry("production_graph", jsonObject2.getJSONArray("other_links"));

    //    map2.put("production_graph", production_graph);

        map2.put("_status", jsonObject2.getString("status"));

        map2.put("allele_has_issue", "false");
        if (jsonObject2.has("allele_has_issues")) {
            map2.put("allele_has_issue", jsonObject2.getString("allele_has_issues"));
        }

        if(jsonObject2.has("other_links")) {
            String genbank_file = getGeneProductInfoArrayEntry("genbank_file", jsonObject2.getJSONArray("other_links"));
            if (genbank_file != null) {
                genbank_file = genbank_file.replace("escell-clone_genbank_file", "escell-clone-genbank-file");
                genbank_file = genbank_file.replace("escell-clone_cre-genbank_file", "escell-clone-cre-genbank-file");
                map2.put("genbank_file", genbank_file);
            }
            String allele_image = getGeneProductInfoArrayEntry("allele_image", jsonObject2.getJSONArray("other_links"));
            if (allele_image != null) {
                map2.put("allele_image", allele_image);
            }
        }

        if (jsonObject2.has("ikmc_project_id")) {
            map2.put("ikmc_project_id", jsonObject2.getString("ikmc_project_id"));
        }

      //  log.info("#### getGeneProductInfoMice:" + map2);

        return map2;
    }

    // see https://www.mousephenotype.org/martsearch_ikmc_project/aboutkompstrategies
    private static final Map<String, String> productMap;

    static {
        Map<String, String> map = new HashMap<>();
        map.put("1a", "KO first allele (reporter-tagged insertion allele)");
        map.put("1b", "Reporter-tagged deletion allele (post-Cre)");
        map.put("1c", "Conditional allele (post-Flp)");
        map.put("1d", "Deletion allele (post-Flp and Cre with no reporter)");
        map.put("1e", "targeted, non-conditional allele");
        map.put("1", "Reporter-tagged deletion allele (with selection cassette)");
        map.put("1.1", "Reporter-tagged deletion allele (post Cre, with no selection cassette)");
        map.put("1.2", "Reporter-tagged deletion allele (post Flp, with no reporter and selection cassette)");
        productMap = Collections.unmodifiableMap(map);
    }

    private static final Map<String, String> productMap2;

    static {
        Map<String, String> map = new HashMap<>();
        map.put("1a", "Knockout First, Reporter-tagged insertion with conditional potential");
        map.put("1b", "Knockout-First, Post-Cre - Reporter Tagged Deletion");
        map.put("1c", "Knockout-First, Post-Flp - Conditional");
        map.put("1d", "Knockout-First, Post-Flp and Cre - Deletion, No Reporter");
        map.put("1e", "Targeted Non-Conditional");
        map.put("1", "Reporter-tagged deletion");
        map.put("1.1", "Reporter-tagged deletion");
        map.put("1.2", "Reporter-tagged deletion");
        productMap2 = Collections.unmodifiableMap(map);
    }

    private String getAlleleDescription(String type) {
        if (!productMap.containsKey(type)) {
            return "";
        }
        return productMap.get(type);
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

        map2.put("allele_type", jsonObject2.getString("allele_type"));

        String targeting_vectors = jsonObject2.getString("associated_product_vector_name");

        map2.put("targeting_vector", targeting_vectors);

        map2.put("southern_tool", "http://www.sanger.ac.uk/htgt/htgt2/tools/restrictionenzymes?es_clone_name=" + jsonObject2.getString("name") + "&iframe=true&width=100%&height=100%");

        map2.put("qc_data", NOT_COMPLETE);

        map2.put("production_completed", jsonObject2.getString("production_completed"));

        map2.put("allele_name", jsonObject2.getString("allele_name"));

        map2.put("marker_symbol", jsonObject2.getString("marker_symbol"));

        map2.put("name", jsonObject2.getString("name"));

        map2.put("allele_has_issue", "false");
        if (jsonObject2.has("allele_has_issues")) {
            map2.put("allele_has_issue", jsonObject2.getString("allele_has_issues"));
        }

        return map2;
    }

    private Map<String, Object> getGeneProductInfoEsCells2(JSONObject jsonObject2) throws IOException, URISyntaxException {
        String type = jsonObject2.getString("type");

        if (!type.equals("es_cell")) {
            return null;
        }

        HashMap<String, Object> map2 = new HashMap<>();
        map2.put("type", type);
        map2.put("product", "ES Cell");
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

        String cassette = getGeneProductInfoArrayEntry("cassette", jsonObject2.getJSONArray("genetic_info"));
        map2.put("cassette", cassette);

        map2.put("es_cell_clone", jsonObject2.getString("name"));

        map2.put("allele_type", jsonObject2.getString("allele_type"));

        map2.put("allele_description", getAlleleDescription("1" + jsonObject2.get("allele_type")));

        map2.put("mgi_accession_id", jsonObject2.getString("mgi_accession_id"));

        String targeting_vectors = jsonObject2.getString("associated_product_vector_name");
        map2.put("targeting_vector", targeting_vectors);

     //   map2.put("southern_tool", "http://www.sanger.ac.uk/htgt/htgt2/tools/restrictionenzymes?es_clone_name=" + jsonObject2.getString("name") + "&iframe=true&width=100%&height=100%");

    //    map2.put("qc_data", NOT_COMPLETE);

        map2.put("production_completed", jsonObject2.getString("production_completed"));

        map2.put("allele_name", jsonObject2.getString("allele_name"));

        map2.put("marker_symbol", jsonObject2.getString("marker_symbol"));

        map2.put("name", jsonObject2.getString("name"));

        map2.put("mgi_allele_name", getTitle(jsonObject2.getString("marker_symbol"), jsonObject2.getString("allele_name")));

        map2.put("allele_has_issue", "false");
        if (jsonObject2.has("allele_has_issues")) {
            map2.put("allele_has_issue", jsonObject2.getString("allele_has_issues"));
        }

        if(jsonObject2.has("other_links")) {
            String genbank_file = getGeneProductInfoArrayEntry("genbank_file", jsonObject2.getJSONArray("other_links"));
            if (genbank_file != null) {
                genbank_file = genbank_file.replace("escell-clone_genbank_file", "escell-clone-genbank-file");
                genbank_file = genbank_file.replace("escell-clone_cre-genbank_file", "escell-clone-cre-genbank-file");
                map2.put("genbank_file", genbank_file);
            }
            String allele_image = getGeneProductInfoArrayEntry("allele_image", jsonObject2.getJSONArray("other_links"));
            if (allele_image != null) {
                map2.put("allele_image", allele_image);
            }
        }

        if (jsonObject2.has("ikmc_project_id")) {
            map2.put("ikmc_project_id", jsonObject2.getString("ikmc_project_id"));
        }

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
        if (design_oligos_url != null) {
            map2.put("design_oligos_url", design_oligos_url);
        }

        map2.put("name", jsonObject2.getString("name"));
        map2.put("marker_symbol", jsonObject2.getString("marker_symbol"));
        map2.put("allele_name", jsonObject2.getString("allele_name"));

        //http://www.mousephenotype.org/imits/targ_rep/alleles/903/vector-image
        String allele_image = getGeneProductInfoArrayEntry("allele_image", jsonObject2.getJSONArray("other_links"));
        if (allele_image != null) {
            map2.put("allele_image", allele_image);
        }

        map2.put("allele_has_issue", "false");
        if (jsonObject2.has("allele_has_issues")) {
            map2.put("allele_has_issue", jsonObject2.getString("allele_has_issues"));
        }

        return map2;
    }

    private Map<String, Object> getGeneProductInfoTargetingVectors2(JSONObject jsonObject2) throws IOException, URISyntaxException {
        if (!jsonObject2.getString("type").equals("targeting_vector")) {
            return null;
        }

        HashMap<String, Object> map2 = new HashMap<>();
        map2.put("type", jsonObject2.getString("type"));
        map2.put("product", "Targeting Vector");
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

        map2.put("mgi_accession_id", jsonObject2.getString("mgi_accession_id"));

     //   log.info("#### url for genbank_file_url:" + genbank_file_url);

//        String design_oligos_url = getGeneProductInfoArrayEntry("design_link", jsonObject2.getJSONArray("other_links"));
//        if (design_oligos_url != null) {
//            map2.put("design_oligos_url", design_oligos_url);
//        }

        map2.put("name", jsonObject2.getString("name"));
        map2.put("marker_symbol", jsonObject2.getString("marker_symbol"));
        map2.put("allele_name", jsonObject2.getString("allele_name"));

        map2.put("mgi_allele_name", getTitle(jsonObject2.getString("marker_symbol"), jsonObject2.getString("allele_name")));

        map2.put("allele_description", "Unknown");
        if(jsonObject2.has("allele_type")) {
            map2.put("allele_description", getAlleleDescription("1" + jsonObject2.get("allele_type")));
        }

        map2.put("allele_has_issue", "false");
        if (jsonObject2.has("allele_has_issues")) {
            map2.put("allele_has_issue", jsonObject2.getString("allele_has_issues"));
        }

        if(jsonObject2.has("other_links")) {
            String genbank_file = getGeneProductInfoArrayEntry("genbank_file", jsonObject2.getJSONArray("other_links"));
            if (genbank_file != null) {
                genbank_file = genbank_file.replace("escell-clone_genbank_file", "escell-clone-genbank-file");
                genbank_file = genbank_file.replace("escell-clone_cre-genbank_file", "escell-clone-cre-genbank-file");
                map2.put("genbank_file", genbank_file);
            }
            String allele_image = getGeneProductInfoArrayEntry("allele_image", jsonObject2.getJSONArray("other_links"));
            if (allele_image != null) {
                map2.put("allele_image", allele_image);
            }
        }

        if (jsonObject2.has("ikmc_project_id")) {
            map2.put("ikmc_project_id", jsonObject2.getString("ikmc_project_id"));
        }

        return map2;
    }

    private static final Map<String, String> phraseMap;

    static {
        Map<String, String> pMap = new HashMap<>();
        pMap.put("mice", "There are mice");
        pMap.put("mice-prod", "There is mouse production");
        pMap.put("no-mice-prod", "There is no mouse production");

        pMap.put("cells", "There are cells");
        pMap.put("cells-prod", "There is cell production");
        pMap.put("no-cells-prod", "There is no cell production");
        phraseMap = Collections.unmodifiableMap(pMap);
    }

    private List<HashMap<String, String>> getGeneProductInfoStatuses(Map<String, Object> genes) {
        List<HashMap<String, String>> list1 = getGeneProductInfoStatusesMouse(genes, true);
        List<HashMap<String, String>> list2 = getGeneProductInfoStatusesEsCell(genes, true);

        list1.addAll(list2);
        return list1;
    }

    // TODO: fix me!
    private Map<String, Object> getGeneProductInfoSorter(Map<String, Object> genes) {
        List<Map<String, Object>> mice = (List<Map<String, Object>>) genes.get("mice");
        List<Map<String, Object>> es_cells = (List<Map<String, Object>>) genes.get("es_cells");
        List<Map<String, Object>> targeting_vectors = (List<Map<String, Object>>) genes.get("targeting_vectors");

        List<Map<String, Object>> mice_new = new ArrayList<>();
        List<Map<String, Object>> cells_new = new ArrayList<>();
        List<Map<String, Object>> vectors_new = new ArrayList<>();

        // log.info("#### before: mice.size(): " + mice.size());
        if (mice != null) {
            Iterator<Map<String, Object>> i0 = mice.iterator();
            while (i0.hasNext()) {
                Map<String, Object> mouse = i0.next(); // must be called before you can call i.remove()
                if (mouse.get("production_completed") == "true") {
                    mice_new.add(mouse);
                    i0.remove();
                }
            }
        }

        if (mice_new.size() > 0 && es_cells != null) {

            log.info("#### do cells");

            for (Map<String, Object> mouse : mice_new) {
                log.info("#### mouse: " + mouse.toString());

                Iterator<Map<String, Object>> i1 = es_cells.iterator();
                while (i1.hasNext()) {
                    Map<String, Object> es_cell = i1.next();

                    if (mouse.containsKey("associated_product_es_cell_name")) {

                        log.info("#### associated_product_es_cell_name: " + mouse.get("associated_product_es_cell_name"));
                        log.info("#### name: " + es_cell.get("name"));

                        if (mouse.get("associated_product_es_cell_name").equals(es_cell.get("name"))) {
                            cells_new.add(es_cell);
                            i1.remove();
                        }
                    }
                }
            }
        }

        if (mice_new.size() > 0 && targeting_vectors != null) {

            log.info("#### do vectors");

            for (Map<String, Object> mouse : mice_new) {
                log.info("#### mouse: " + mouse.toString());

                Iterator<Map<String, Object>> i2 = targeting_vectors.iterator();
                while (i2.hasNext()) {
                    Map<String, Object> targeting_vector = i2.next();

                    if (mouse.containsKey("associated_product_vector_name")) {
                        log.info("#### associated_product_vector_name: " + mouse.get("associated_product_vector_name"));
                        log.info("#### name: " + targeting_vector.get("name"));
                        log.info("#### targeting_vector: " + targeting_vector.get("targeting_vector"));

                        if (mouse.get("associated_product_vector_name").equals(targeting_vector.get("name"))) {
                            vectors_new.add(targeting_vector);
                            i2.remove();
                        }
                    }
                }
            }
        }

        if (mice_new.size() > 0) {
            mice_new.addAll(mice);
            genes.put("mice", mice_new);

            cells_new.addAll(es_cells);
            genes.put("es_cells", cells_new);

            vectors_new.addAll(targeting_vectors);
            genes.put("targeting_vectors", vectors_new);
        }

        return genes;
    }

    private List<HashMap<String, String>> getGeneProductInfoStatusesMouse(Map<String, Object> genes, boolean restrict) {
        List<Map<String, Object>> mice = (List<Map<String, Object>>) genes.get("mice");
        List<HashMap<String, String>> listFound = new ArrayList<>();
        List<HashMap<String, String>> listNotFound = new ArrayList<>();

        for (Map<String, Object> mouse : mice) {
            if (mouse.get("production_completed") == "true") {
                HashMap<String, String> map2 = new HashMap<>();
                map2.put("DETAILS", mouse.get("production_graph").toString());
                map2.put("TEXT", phraseMap.get("mice"));
                map2.put("TEXT2", mouse.get("_status").toString() + " (" + mouse.get("production_completed").toString() + ")");
                map2.put("COLOUR", "#E0F9FF");  // TODO: fix me!

                List<Map<String, Object>> orders = (List<Map<String, Object>>) mouse.get("orders");

                if (orders != null) {
                    for (Map<String, Object> order : orders) {
                        map2.put("ORDER", order.get("url").toString());
                    }
                }

                listFound.add(map2);
            } else {
                HashMap<String, String> map2 = new HashMap<>();
                map2.put("TEXT", phraseMap.get("mice-prod"));
                map2.put("TEXT2", mouse.get("_status").toString() + " (" + mouse.get("production_completed").toString() + ")");
                map2.put("COLOUR", "#FFE0B2");  // TODO: fix me!
                map2.put("DETAILS", mouse.get("production_graph").toString());

                List<Map<String, Object>> contacts = (List<Map<String, Object>>) mouse.get("contacts");

                if (contacts != null) {
                    for (Map<String, Object> contact : contacts) {
                        map2.put("CONTACT", contact.get("url").toString());
                    }
                }

                listNotFound.add(map2);
            }
        }

        log.info("#### getGeneProductInfoStatusesMouse: listFound: " + listFound);
        log.info("#### getGeneProductInfoStatusesMouse: listNotFound: " + listNotFound);

        if (listFound.size() > 0) {
            if (restrict) {
                return listFound.subList(0, 1); // TODO: fix me!
            } else {
                return listFound;
            }
        }

        if (listNotFound.size() > 0) {
            if (restrict) {
                return listNotFound.subList(0, 1); // TODO: fix me!
            } else {
                return listNotFound;
            }
        }

        HashMap<String, String> map2 = new HashMap<>();
        map2.put("TEXT", phraseMap.get("no-mice-prod"));
        map2.put("TEXT2", "");
        listNotFound.add(map2);

        return listNotFound;
    }

    private List<HashMap<String, String>> getGeneProductInfoStatusesEsCell(Map<String, Object> genes, boolean restrict) {

        List<Map<String, Object>> es_cells = (List<Map<String, Object>>) genes.get("es_cells");

        List<HashMap<String, String>> listFound = new ArrayList<>();
        List<HashMap<String, String>> listNotFound = new ArrayList<>();

        for (Map<String, Object> es_cell : es_cells) {
            if (es_cell.get("production_completed") == "true") {       // && mice.get("mice_complete")) {
                HashMap<String, String> map2 = new HashMap<>();
                map2.put("TEXT", phraseMap.get("cells"));
                map2.put("COLOUR", "#E0F9FF");  // TODO: fix me!

                List<Map<String, Object>> orders = (List<Map<String, Object>>) es_cell.get("orders");

                if (orders != null) {
                    for (Map<String, Object> order : orders) {
                        map2.put("ORDER", order.get("url").toString());
                    }
                }

                listFound.add(map2);
            } else {
                HashMap<String, String> map2 = new HashMap<>();
                map2.put("TEXT", phraseMap.get("cell-prod"));
                map2.put("COLOUR", "#FFE0B2");  // TODO: fix me!

                List<Map<String, Object>> contacts = (List<Map<String, Object>>) es_cell.get("contacts");

                if (contacts != null) {
                    for (Map<String, Object> contact : contacts) {
                        map2.put("CONTACT", contact.get("url").toString());
                    }
                }

                listNotFound.add(map2);
            }
        }

        if (listFound.size() > 0) {
            if (restrict) {
                return listFound.subList(0, 1); // TODO: fix me!
            } else {
                return listFound;
            }
        }

        if (listNotFound.size() > 0) {
            if (restrict) {
                return listNotFound.subList(0, 1); // TODO: fix me!
            } else {
                return listNotFound;
            }
        }

        HashMap<String, String> map2 = new HashMap<>();
        map2.put("TEXT", phraseMap.get("no-cell-prod"));
        map2.put("TEXT2", "");
        listNotFound.add(map2);
        return listNotFound;
    }

    private String getGeneAllele2CoreUrl(String accession, String allele_name) {
        String qallele_name = "";
        if (allele_name != null) {
            //qallele_name = " " + ALLELE_NAME_FIELD_ORIGINAL + ":\"" + allele_name + "\"";
            qallele_name = " " + ALLELE_NAME_FIELD + ":\"" + allele_name + "\"";
        }

        String url = ALLELE2_CORE_URL + "/solr/allele2/search?q=mgi_accession_id:"
                + accession.replace(":", "\\:")
                + qallele_name
                + "&start=0&rows=100&hl=true&wt=json";

        return url;
    }

    public String getGeneProductCoreUrl2(Map<String, String> params) {
        log.info("#### getGeneProductCoreUrl2");

        List<String> qparams = new ArrayList<>();
        for (String s : params.keySet()) {
            if (s.equals("multi")) {
                qparams.add(params.get(s));
                continue;
            }
            qparams.add(s + ":" + params.get(s).replace(":", "\\:"));
        }

        String qs = StringUtils.join(qparams, " ");

        String url = ALLELE2_CORE_URL + "/solr/product/search?q="
                + qs
                + "&start=0&rows=100&hl=true&wt=json&indent=on";

        log.info("#### getGeneProductCoreUrl2: url: " + url);

        return url;
    }

    public String getGeneProductCoreUrl3(Map<String, String> params) {
        List<String> qparams = new ArrayList<>();
        for (String s : params.keySet()) {
            if (s.equals("multi")) {
                qparams.add(params.get(s));
                continue;
            }
            qparams.add(s + ":" + params.get(s).replace(":", "\\:"));
        }

        String qs = StringUtils.join(qparams, " ");

        String url = ALLELE2_CORE_URL + "/solr/product/select?q="
                + qs
                + "&start=0&rows=100&hl=true&wt=json";

        return url;
    }

    private String getGeneProductCoreUrl(String accession, String allele_name) {
        String qallele_name = "";
        if (allele_name != null) {
            qallele_name = " " + ALLELE_NAME_FIELD + ":\"" + allele_name + "\"";
        }

        String url = ALLELE2_CORE_URL + "/solr/product/search?q=mgi_accession_id:"
                + accession.replace(":", "\\:")
                + qallele_name
                + "&start=0&rows=100&hl=true&wt=json";

        return url;
    }

    private String getGeneProductCoreUrlAlt3(String accession, String allele_name) {
        String qallele_name = "";
        String qallele_type = "";
        if (allele_name != null) {
            qallele_name = " AND " + ALLELE_NAME_FIELD + ":\"" + allele_name + "\"";

            Pattern pattern = Pattern.compile(".+?\\d(.)");
            Matcher matcher = pattern.matcher(allele_name);
            if (matcher.find()) {
                qallele_type = " AND allele_type:\"" + matcher.group(1) + "\"";
            }
        }

        String left = "(-type:targeting_vector AND mgi_accession_id:" + accession.replace(":", "\\:") + qallele_name + ")";
        String right = "(type:targeting_vector AND mgi_accession_id:" + accession.replace(":", "\\:") + qallele_type + ")";

        String url = ALLELE2_CORE_URL + "/solr/product/select?q="
                + left
                + " OR "
                + right
                + "&start=0&rows=100&hl=true&wt=json";

        log.info("####### getGeneProductCoreUrlAlt3: left: " + left);
        log.info("####### getGeneProductCoreUrlAlt3: right: " + right);
        log.info("####### getGeneProductCoreUrlAlt3: url: " + url);

        return url;
    }

    public static final String[] DELETE_VALUES = new String[]{"1b", "1d", "1", "1.1", "1.2"};

    private String isDeleted(String type) {
        return Arrays.asList(DELETE_VALUES).contains(type) ? "true" : "false";
    }

//    private String getTitleOld(String marker_symbol, String allele_name) {
//        if (marker_symbol == null || allele_name == null) {
//            return "";
//        }
//
//        if (marker_symbol.length() < 1 || allele_name.length() < 1) {
//            return "";
//        }
//
//        String title = marker_symbol + "<sup>" + allele_name + "</sup>";
//
//        return title;
//    }

    private String getTitle(String marker_symbol, String allele_name) {
        if (marker_symbol == null || marker_symbol.length() < 1) {
            marker_symbol = "Unknown";
        }
        if (allele_name == null || allele_name.length() < 1) {
            allele_name = "Unknown";
        }

        String title = marker_symbol + "<sup>" + allele_name + "</sup>";

        return title;
    }

    private Map<String, String> getLoaAssay(JSONArray docs) {

        for (Object doc : docs) {
            JSONObject jsonObject2 = (JSONObject) doc;
            String type = jsonObject2.getString("type");

            if (!type.equals("es_cell")) {
                continue;
            }

            if (!jsonObject2.containsKey("loa_assays")) {
                continue;
            }

            Map<String, String> loas = new HashMap<>();

            JSONArray array = jsonObject2.getJSONArray("loa_assays");
            if (!array.isEmpty()) {
                for (Object i : array) {
                    String item = (String) i;
                    if (!item.isEmpty()) {
                        Pattern pattern = Pattern.compile("(.+):(.+)");
                        Matcher matcher = pattern.matcher(item);
                        if (matcher.find()) {
                            String loa_assay_url = "http://www.lifetechnologies.com/order/genome-database/searchResults?searchMode=keyword&productTypeSelect=cnv&keyword=";
                            loas.put(matcher.group(1), loa_assay_url + matcher.group(2));
                        }
                    }
                }
                return loas;
            }
        }

        return null;
    }

    //private List<Map<String, Object>> squashGeneProductInfo2EsCells(List<Map<String, Object>> es_cells) {
    //    Map<String, Map<String, String>> map = new HashMap<>();
    //    for(Map<String, Object> es_cell : es_cells) {
    //        String allele_name = (String)es_cell.get("allele_name");
    //        String genetic_background = (String)es_cell.get("genetic_background");
    //
    //        if(!map.containsKey(allele_name + genetic_background)) {
    //            map.put(allele_name + genetic_background, new HashMap<String, String>());
    //        }
    //
    //        Map<String, String> submap = map.get(allele_name + genetic_background);
    //
    //        if(!submap.containsKey(map)) {
    //
    //        }
    //
    //        submap.put(NOT_FOUND, allele_name);
    //
    //      //  map.put(allele_name + genetic_background, true);
    //    }
    //    return es_cells;
    //}

//    private List<Map<String, Object>> squashGeneProductInfo2EsCells(List<Map<String, Object>> es_cells) {
//        Map<String, Object> map = new HashMap<>();
//        Map<String, Map<String, Boolean>> map2 = new HashMap<>();
//        for(Map<String, Object> es_cell : es_cells) {
//            String allele_name = (String)es_cell.get("allele_name");
//
//            if(!map.containsKey(allele_name)) {
//                map.put(allele_name, es_cell);
//            }
//            
//            List<Map<String, Object>> orders = (List<Map<String, Object>>)es_cell.get("orders");
//            
//        //    map2.put(allele_name, es_cell);
//        }
//        return es_cells;
//    }

    //The following logic should be maintained
    //
    //1. Mice appear at the top followed by es_cells and then targeting vectors.
    //2. You should only show ES Cells if there is not a mouse with the same allele_name.
    //3. Only display Targeting Vectors if the targeting vector is different from all the targeting_vectors that created the ES Cells.
    //  The targeting vector is only considered to be different if the cassette and allele_type is different
    //
    //The following logic should be added
    //1. Compress the mouse records for the same allele_name into one row. And display all the different order links across the different mouse records.
    //2. Add a new record for all structurally different targeting vectors.

    private List<Map<String, Object>> squashGeneProductInfo2ListNew(List<Map<String, Object>> mice,
        List<Map<String, Object>> es_cells,
        List<Map<String, Object>> targeting_vectors) throws IOException, URISyntaxException, Exception {

        List<Map<String, Object>> array = new ArrayList<>();

        Map<String, Boolean> mice_allele_names = new HashMap<>();
        Map<String, Boolean> es_cell_allele_names = new HashMap<>();
        Map<String, Boolean> targeting_vector_names = new HashMap<>();
        //Map<String, String> cassette_allele_type = new HashMap<>();

        for(Map<String, Object> mouse : mice) {
            mice_allele_names.put((String)mouse.get("allele_name"), true);
            array.add(mouse);
        }

        log.info("#### squashGeneProductInfo2ListNew: mice_allele_names: " + mice_allele_names);

        for(Map<String, Object> es_cell : es_cells) {
            es_cell_allele_names.put((String)es_cell.get("allele_name"), true);
            if(!mice_allele_names.containsKey((String)es_cell.get("allele_name"))) {
                array.add(es_cell);
            }
            targeting_vector_names.put((String)es_cell.get("targeting_vector"), true);
        }

        log.info("#### squashGeneProductInfo2ListNew: es_cell_allele_names: " + es_cell_allele_names);

        for(Map<String, Object> targeting_vector : targeting_vectors) {
            if(!targeting_vector_names.containsKey((String)targeting_vector.get("name"))) {
                array.add(targeting_vector);
            }
        }

        log.info("#### squashGeneProductInfo2ListNew: count: " + array.size());

        return array;
    }

    private List<Map<String, Object>> squashGeneProductInfo2ListOld(List<Map<String, Object>> mice,
        List<Map<String, Object>> es_cells,
        List<Map<String, Object>> targeting_vectors) throws IOException, URISyntaxException, Exception {

        List<Map<String, Object>> array = new ArrayList<>();

        array.addAll(mice);
        array.addAll(es_cells);
        array.addAll(targeting_vectors);

        log.info("#### squashGeneProductInfo2ListOld: count: " + array.size());

        return array;
    }

    private List<Map<String, Object>> squashGeneProductInfo2List(List<Map<String, Object>> mice,
        List<Map<String, Object>> es_cells,
        List<Map<String, Object>> targeting_vectors) throws IOException, URISyntaxException, Exception {

        return squashGeneProductInfo2ListNew(mice, es_cells, targeting_vectors);

        //return squashGeneProductInfo2ListOld(mice, es_cells, targeting_vectors);
    }

    public List<Map<String, Object>> getGeneProductInfo2(String accession) throws IOException, URISyntaxException, Exception {

        String url = getGeneProductCoreUrlAlt3(accession, null);
        log.info("#### url for getGeneProductInfo2=" + url);

        JSONObject jsonObject1 = getResults(url);

        JSONArray docs = jsonObject1.getJSONObject("response").getJSONArray("docs");

        if (docs.size() < 1) {
            log.info("#### No rows returned for the query!");
            return null;
        }

        log.info("#### Found " + docs.size() + " rows!");

        List<Map<String, Object>> mice = new ArrayList<>();
        List<Map<String, Object>> es_cells = new ArrayList<>();
        List<Map<String, Object>> targeting_vectors = new ArrayList<>();

        for (Object doc : docs) {
            JSONObject jsonObject2 = (JSONObject) doc;
            String type = jsonObject2.getString("type");

            if (type.equals("mouse")) {
                Map<String, Object> mouse = getGeneProductInfoMice2(jsonObject2);
                mice.add(mouse);
            }

            if (type.equals("es_cell")) {
                Map<String, Object> es_cell = getGeneProductInfoEsCells2(jsonObject2);
                es_cells.add(es_cell);
            }

            if (type.equals("targeting_vector")) {
                Map<String, Object> targeting_vector = getGeneProductInfoTargetingVectors2(jsonObject2);
                targeting_vectors.add(targeting_vector);
            }
        }

        //List<Map<String, Object>> mapper = new ArrayList<>();
        //mapper.addAll(mice);
        //mapper.addAll(es_cells);
        //mapper.addAll(targeting_vectors);

        List<Map<String, Object>> mapper = squashGeneProductInfo2List(mice, es_cells, targeting_vectors);

        log.info("#### getGeneProductInfo2: count: " + mapper.size());
       // log.info("#### getGeneProductInfo2: mapper: " + mapper);

        return mapper;
    }

    public Map<String, Object> getGeneProductInfo(String accession, String allele_name, boolean debug) throws IOException, URISyntaxException, Exception {

        String url = getGeneProductCoreUrlAlt3(accession, allele_name);
        //String url = getGeneProductCoreUrl(accession, allele_name);

        log.info("#### url for getGeneProductInfo=" + url);

        JSONObject jsonObject1 = getResults(url);

        JSONArray docs = jsonObject1.getJSONObject("response").getJSONArray("docs");

        if (docs.size() < 1) {
            log.info("#### No rows returned for the query!");
            return null;
        }

        log.info("#### Found " + docs.size() + " rows!");

        Map<String, Object> mapper = new HashMap<>();
        List<Map<String, Object>> mice = new ArrayList<>();
        List<Map<String, Object>> es_cells = new ArrayList<>();
        List<Map<String, Object>> targeting_vectors = new ArrayList<>();

        String title;
        String marker_symbol = "";
        Object loa_assays = getLoaAssay(docs);
        log.info("#### getGeneProductInfo: loa_assay: " + loa_assays);

        for (Object doc : docs) {
            JSONObject jsonObject2 = (JSONObject) doc;
            String type = jsonObject2.getString("type");

            if (type.equals("mouse")) {
                Map<String, Object> mouse = getGeneProductInfoMice(jsonObject2);
              //  title = getTitle((String) mouse.get("marker_symbol"), (String) mouse.get("allele_name"));
                log.info("#### getGeneProductInfo: mouse: " + mouse);
                String ms = (String)mouse.get("marker_symbol");
                marker_symbol = marker_symbol.length() == 0 && ms != null && ms.length() > 0 ? ms : marker_symbol;
                mice.add(mouse);
            }

            if (type.equals("es_cell")) {
                Map<String, Object> es_cell = getGeneProductInfoEsCells(jsonObject2);
            //    title = getTitle((String) es_cell.get("marker_symbol"), (String) es_cell.get("allele_name"));
                String ms = (String)es_cell.get("marker_symbol");
                marker_symbol = marker_symbol.length() == 0 && ms != null && ms.length() > 0 ? ms : marker_symbol;
                es_cells.add(es_cell);

//                if (loa_assay == null) {
//                    loa_assay = getLoaAssay(es_cell);
//                    log.info("#### getGeneProductInfo: loa_assay: " + loa_assay);
//                }
            }

            if (type.equals("targeting_vector")) {
                Map<String, Object> targeting_vector = getGeneProductInfoTargetingVectors(jsonObject2);
             //  title = getTitle((String) targeting_vector.get("marker_symbol"), (String) targeting_vector.get("allele_name"));
                String ms = (String)targeting_vector.get("marker_symbol");
                marker_symbol = marker_symbol.length() == 0 && ms != null && ms.length() > 0 ? ms : marker_symbol;
                targeting_vectors.add(targeting_vector);
            }
        }

        log.info("#### getGeneProductInfo: mice.size(): " + mice.size());
        log.info("#### getGeneProductInfo: es_cells.size(): " + es_cells.size());

        title = getTitle(marker_symbol, allele_name);

        mapper.put("title", title);
        mapper.put("mice", mice);
        mapper.put("es_cells", es_cells);
        mapper.put("targeting_vectors", targeting_vectors);

        //List<Map<String, String>> constructs1 = getGeneAlleleInfo(accession, allele_name);
        List<Map<String, String>> constructs2 = getGeneAllele2Info(accession, allele_name);

        List<HashMap<String, Object>> summaries = new ArrayList<>();

        if (constructs2 != null) {
            for (Map<String, String> item : constructs2) {
                HashMap<String, Object> summary = new HashMap<>();
                summary.put("buttons", "true"); // flag to indicate if we have data - TODO: fix!
                summary.put("marker_symbol", item.get("marker_symbol"));
                summary.put("symbol", item.get("marker_symbol") + "<sup>" + item.get("allele_name") + "</sup>");

             //   title = getTitle(item.get("marker_symbol"), item.get("allele_name"));

                summary.put("allele_name", item.get("allele_name"));

                String genbank_file = item.get("genbank_file"); // TODO: fix this in the index
                genbank_file = genbank_file.replace("escell-clone_genbank_file", "escell-clone-genbank-file");
                genbank_file = genbank_file.replace("escell-clone_cre-genbank_file", "escell-clone-cre-genbank-file");
                summary.put("genbank", genbank_file);

                if (item.containsKey("allele_image")) {
                    String s = item.get("allele_image");
                    if (!s.isEmpty()) {
                        summary.put("map_image", s + "?simple=true");
                    }
                }

                // TODO: is this right?

                summary.put("allele_description", getAlleleDescription("1" + item.get("allele_type")));
                summary.put("allele_description_1b", getAlleleDescription("1b"));
                summary.put("is_deleted", isDeleted("1" + item.get("allele_type")));

                List<String> southern_tools = new ArrayList<>();
                for (Map<String, Object> mouse : mice) {

                    if (mouse.containsKey("associated_product_es_cell_name") && mouse.containsKey("production_completed") && mouse.get("production_completed").equals("true")) {
                        if (mouse.get("associated_product_es_cell_name").toString().length() > 0) {
                            String st = "http://www.sanger.ac.uk/htgt/htgt2/tools/restrictionenzymes?es_clone_name="
                                    + mouse.get("associated_product_es_cell_name")
                                    + "&iframe=true&width=100%&height=100%";
                            southern_tools.add(st);
                            log.info("#### getGeneProductInfo: southern_tool: " + st);
                        }
                    }
                    summary.put("southern_tools", southern_tools);
                }

                if (southern_tools.size() > 0) {
                    summary.put("southern_tool", southern_tools.get(0));
                }

                summary.put("lrpcr_genotyping_primers", "/phenotype-archive/lrpcr/" + URLEncoder.encode(accession) + "/" + URLEncoder.encode(allele_name));

                summary.put("mutagenesis_url", "/phenotype-archive/mutagenesis/" + URLEncoder.encode(accession) + "/" + URLEncoder.encode(allele_name));

                summary.put("statuses", getGeneProductInfoStatuses(mapper));    // TODO: FIX-ME!

                summary.put("status_mice", getGeneProductInfoStatusesMouseAlt(mapper));

                if (loa_assays != null) {
                    log.info("#### getGeneProductInfo: loa_assay: " + loa_assays);
                    summary.put("loa_assays", loa_assays);
                }

                summary.put("status_es_cells", getGeneProductInfoStatusesEsCellAlt(mapper));

                log.info("#### getGeneProductInfo: status_mice: " + mapper.get("status_mice"));
                log.info("#### getGeneProductInfo: status_es_cells: " + mapper.get("status_es_cells"));

                if (item.containsKey("ensembl_url")) {
                    HashMap<String, String> map3 = new HashMap<>();
                    map3.put("browser", "Ensembl");
                    map3.put("url", item.get("ensembl_url"));
                    List<HashMap<String, String>> list3 = new ArrayList<>();
                    list3.add(map3);
                    summary.put("browsers", list3);
                }

                summaries.add(summary);
            }
        }

      //  mapper.put("title", title);

        String stripped = title.replaceAll("\\<sup\\>", "");
        stripped = stripped.replaceAll("\\<\\/sup\\>", "");

        mapper.put("title_alt", stripped);

        log.info("#### getGeneProductInfo: title_alt: " + stripped);

        if (summaries.isEmpty()) {
            log.info("#### getGeneProductInfo: cannot get any summary for '" + accession + "' - '" + allele_name + "'");
        }

        if (summaries.isEmpty()) {
            HashMap<String, Object> summary = new HashMap<>();
            summary.put("statuses", getGeneProductInfoStatuses(mapper));    // TODO: FIX-ME!
            summaries.add(summary);
        }

        if (summaries.size() > 1) {
            log.error("######## invalid count for summary!");
        }

        if (summaries.size() > 0) {
            mapper.put("summary", summaries.get(0));
        }

        log.info("#### summaries: " + summaries.toString());

        if (!debug) {
            mapper = getGeneProductInfoSorter(mapper);
        }

        log.info("#### mapper: " + mapper);

        return mapper;
    }

    public List<Map<String, Object>> getProductGeneDetails(String acc) throws IOException, URISyntaxException {
        String url = getGeneProductCoreUrl(acc, null);

        log.info("#### url for solr=" + url);

        JSONObject jsonObject1 = getResults(url);

        JSONArray docs = jsonObject1.getJSONObject("response").getJSONArray("docs");

        if (docs.size() < 1) {
            log.info("#### No rows returned for the query!");
            return null;
        }

        List<Map<String, Object>> list = new ArrayList<>();

        Map<String, String> clashes = new HashMap<>();

        for (Object doc : docs) {
            JSONObject jsonObject2 = (JSONObject) doc;

            if (!jsonObject2.containsKey("marker_symbol") || !jsonObject2.containsKey("allele_name") || !jsonObject2.containsKey("mgi_accession_id")) {
                continue;
            }

            Map<String, Object> hash = new HashMap<>();
            String key = jsonObject2.get("marker_symbol") + "_" + jsonObject2.get("allele_name") + "_" + jsonObject2.get("mgi_accession_id");
            if (clashes.containsKey(key)) {
                continue;
            }
            clashes.put(key, "found");
            hash.put("marker_symbol", jsonObject2.get("marker_symbol"));
            hash.put("allele_name", jsonObject2.get("allele_name"));
            hash.put("mgi_accession_id", jsonObject2.get("mgi_accession_id"));
            hash.put("allele_type", jsonObject2.get("allele_type"));
            list.add(hash);
        }

        return list;
    }

    public List<Map<String, Object>> getProductGeneDetails(Map<String, String> params) throws IOException, URISyntaxException {

        String url = getGeneProductCoreUrl3(params);

        log.info("#### url for solr=" + url);

        JSONObject jsonObject1 = getResults(url);

        JSONArray docs = jsonObject1.getJSONObject("response").getJSONArray("docs");

        if (docs.size() < 1) {
            log.info("#### No rows returned for the query!");
            return null;
        }

        List<Map<String, Object>> list = new ArrayList<>();

        Map<String, String> clashes = new HashMap<>();

        for (Object doc : docs) {
            JSONObject jsonObject2 = (JSONObject) doc;
            String type = jsonObject2.getString("type");

            Map<String, Object> hash = new HashMap<>();
            String key = jsonObject2.get("marker_symbol") + "_" + jsonObject2.get("allele_name") + "_" + jsonObject2.get("mgi_accession_id");
            if (clashes.containsKey(key)) {
                continue;
            }
            clashes.put(key, "found");
            hash.put("marker_symbol", jsonObject2.get("marker_symbol"));
            hash.put("allele_name", jsonObject2.get("allele_name"));
            hash.put("mgi_accession_id", jsonObject2.get("mgi_accession_id"));
            hash.put("allele_type", jsonObject2.get("allele_type"));
            list.add(hash);
        }

        return list;
    }

    public HashMap<String, HashMap<String, List<String>>> getAlleleQcInfo(String alleleType, String type, String name)
            throws IOException, URISyntaxException {

        name = name.replace("-", "\\-");
        String url = ALLELE2_CORE_URL + "/solr/product/search?q=name:"
                + name
                + " AND type:"
                + type
                + " AND " + ALLELE_TYPE_FIELD + ":"
                + alleleType
                + "&start=0&rows=100&hl=true&wt=json";

        log.info("url for productQc=" + url);

        JSONObject jsonObject = getResults(url);
        //int numberFound = Integer.parseInt(jsonObject.getJSONObject("response").getString("numFound"));

        JSONArray docs = jsonObject.getJSONObject("response").getJSONArray("docs");
        HashMap<String, HashMap<String, List<String>>> construct = new HashMap<>();

        if (docs.size() < 1) {
            log.info("No " + type + "found with a name equal to " + name);
        } else {
            try {
                if (docs.getJSONObject(0).has("qc_data") && docs.getJSONObject(0).getString("qc_data").length() > 0) {
                    construct = extractQcData(docs, 0);
                }
            } catch (Exception e) {
//                e.printStackTrace();
                log.error("#### getAlleleQcInfo exception: " + e);
            }
        }
        return construct;
    }

    private HashMap<String, HashMap<String, List<String>>> extractQcData(JSONArray docs, int i) {
        HashMap<String, HashMap<String, List<String>>> deep = new HashMap<>();

       // String[] qc;
//        String qc_group = "";
//        String qc_type = "";
//        String qc_result = "";
        JSONArray qcDataArray = docs.getJSONObject(i).getJSONArray("qc_data");
        for (int j = 0; j < qcDataArray.size(); j++) {
            String[] qc = qcDataArray.getString(j).split(":");

            String qc_group = qc != null && qc.length > 0 ? qc[0] : "";
            String qc_type = qc != null && qc.length > 1 ? qc[1] : "";
            String qc_result = qc != null && qc.length > 2 ? qc[2] : "";

//            if (qc != null && qc.length > 0) {
//                qc_group = qc[0];
//            }
//
//            if (qc != null && qc.length > 1) {
//                qc_type = qc[1];
//            }
//
//            if (qc != null && qc.length > 2) {
//                qc_result = qc[2];
//            }
            if (!deep.containsKey(qc_group)) {
                deep.put(qc_group, new HashMap<String, List<String>>());
                deep.get(qc_group).put("fieldNames", new ArrayList());
                deep.get(qc_group).put("values", new ArrayList());
            }
            deep.get(qc_group).get("fieldNames").add(qc_type);
            deep.get(qc_group).get("values").add(qc_result);
        }
        return deep;
    }

    public String getProject(String accession, String alleleName)
            throws IOException, URISyntaxException {

        String qallele_name = " " + ALLELE_NAME_FIELD + ":\"" + alleleName + "\"";

        String url = ALLELE2_CORE_URL + "/solr/product/select?q=mgi_accession_id:"
                + accession.replace(":", "\\:")
                + " AND " + qallele_name
                + " AND ikmc_project_id:* "
                + "&start=0&rows=100&hl=true&wt=json";

        log.info("url for getProject=" + url);

        JSONObject jsonObject = getResults(url);

        JSONArray docs = jsonObject.getJSONObject("response").getJSONArray("docs");

        if (docs.size() < 1) {
            log.info("#### getProject: No rows returned for the query!");
            return null;
        }

        if (docs.size() > 1) {
            log.info("#### getProject: too many rows returned for the query: (" + docs.size() + ")");
        }

        log.info("#### getProject: " + docs);

        JSONObject doc = (JSONObject) docs.get(0);

        String project_ids = doc.getString("ikmc_project_id");

        return project_ids;
    }

    public boolean isMirko(String accession, String alleleName)
            throws IOException, URISyntaxException {

        String qallele_name = " " + ALLELE_NAME_FIELD + ":\"" + alleleName + "\"";

        String url = ALLELE2_CORE_URL + "/solr/product/select?q=mgi_accession_id:"
                + accession.replace(":", "\\:")
                + " AND " + qallele_name
                + " AND ikmc_project_id:* "
                + "&start=0&rows=100&hl=true&wt=json";

        log.info("url for isMirko=" + url);

        JSONObject jsonObject = getResults(url);

        JSONArray docs = jsonObject.getJSONObject("response").getJSONArray("docs");

        if (docs.size() < 1) {
            log.info("#### isMirko: No rows returned for the query!");
            return false;
        }

        if (docs.size() > 1) {
            log.info("#### isMirko: too many rows returned for the query: (" + docs.size() + ")");
        }

        log.info("#### isMirko: " + docs);

        JSONObject doc = (JSONObject) docs.get(0);

        if (!doc.containsKey("production_pipeline")) {
            return false;
        }

        String production_pipeline = doc.getString("production_pipeline");

        return production_pipeline.equals("mirKO");
    }

    public String getDesign(String accession, String alleleName)
            throws IOException, URISyntaxException {

        String qallele_name = " " + ALLELE_NAME_FIELD + ":\"" + alleleName + "\"";

        String url = ALLELE2_CORE_URL + "/solr/product/select?q=mgi_accession_id:"
                + accession.replace(":", "\\:")
                + " AND " + qallele_name
                + " AND design_id:* "
                + "&start=0&rows=100&hl=true&wt=json";

        log.info("url for getDesign=" + url);

        JSONObject jsonObject = getResults(url);

        JSONArray docs = jsonObject.getJSONObject("response").getJSONArray("docs");

        if (docs.size() < 1) {
            log.info("#### getDesign: No rows returned for the query!");
            return null;
        }

        if (docs.size() > 1) {
            log.info("#### getDesign: too many rows returned for the query: (" + docs.size() + ")");
        }

        log.info("#### getDesign: " + docs);

        JSONObject doc = (JSONObject) docs.get(0);

        String design_id = doc.getString("design_id");

        return design_id;
    }

    private Map<String, Object> getGeneProductInfoStatusesEsCellAlt(Map<String, Object> genes) {

        List<Map<String, Object>> es_cells = (List<Map<String, Object>>) genes.get("es_cells");

        Map<String, Object> foundMap = new HashMap<>();
        Map<String, Object> notFoundMap = new HashMap<>();

        for (Map<String, Object> es_cell : es_cells) {
            if (es_cell.get("production_completed") == "true") {       // && mice.get("mice_complete")) {
                foundMap.put("TEXT", phraseMap.get("cells"));
                foundMap.put("COLOUR", "#E0F9FF");  // TODO: fix me!

                List<Map<String, Object>> orders = (List<Map<String, Object>>) es_cell.get("orders");

                if (orders != null) {
                    for (Map<String, Object> order : orders) {
                        if (!foundMap.containsKey("orders")) {
                            foundMap.put("orders", new ArrayList<String>());
                        }
                        ArrayList<String> o = (ArrayList<String>) foundMap.get("orders");
                        if (!o.contains(order.get("url").toString())) {
                            o.add(order.get("url").toString());
                        }
                    }
                }
            } else {
                notFoundMap.put("TEXT", phraseMap.get("cell-prod"));
                notFoundMap.put("COLOUR", "#FFE0B2");  // TODO: fix me!

                List<Map<String, Object>> contacts = (List<Map<String, Object>>) es_cell.get("contacts");

                if (contacts != null) {
                    for (Map<String, Object> contact : contacts) {
                        if (!notFoundMap.containsKey("contacts")) {
                            notFoundMap.put("contacts", new ArrayList<String>());
                        }
                        ArrayList<String> o = (ArrayList<String>) notFoundMap.get("contacts");
                        if (!o.contains(contact.get("url").toString())) {
                            o.add(contact.get("url").toString());
                        }
                    }
                }
            }
        }

        log.info("#### getGeneProductInfoStatusesEsCellAlt: foundMap: " + foundMap);
        log.info("#### getGeneProductInfoStatusesEsCellAlt: notFoundMap: " + notFoundMap);

        if (foundMap.size() > 0) {
            return foundMap;
        }

        if (notFoundMap.size() > 0) {
            return notFoundMap;
        }

        notFoundMap.put("TEXT", phraseMap.get("no-cell-prod"));
        notFoundMap.put("TEXT2", "");
        return notFoundMap;
    }

    private Map<String, Object> getGeneProductInfoStatusesMouseAlt(Map<String, Object> genes) {
        List<Map<String, Object>> mice = (List<Map<String, Object>>) genes.get("mice");
        Map<String, Object> foundMap = new HashMap<>();
        Map<String, Object> notFoundMap = new HashMap<>();

        for (Map<String, Object> mouse : mice) {
            if (mouse.get("production_completed") == "true") {
                foundMap.put("TEXT", phraseMap.get("mice"));
                foundMap.put("COLOUR", "#E0F9FF");  // TODO: fix me!

                String production_graph = mouse.get("production_graph").toString();

                if (production_graph.length() > 0) {
                    if (!foundMap.containsKey("details")) {
                        foundMap.put("details", new ArrayList<String>());
                    }
                    ArrayList<String> o = (ArrayList<String>) foundMap.get("details");
                    if (!o.contains(production_graph)) {
                        o.add(production_graph);
                    }
                }

                List<Map<String, Object>> orders = (List<Map<String, Object>>) mouse.get("orders");

                if (orders != null) {
                    for (Map<String, Object> order : orders) {
                        if (!foundMap.containsKey("orders")) {
                            foundMap.put("orders", new ArrayList<String>());
                        }
                        ArrayList<String> o = (ArrayList<String>) foundMap.get("orders");
                        if (!o.contains(order.get("url").toString())) {
                            o.add(order.get("url").toString());
                        }
                    }
                }

            } else {
                notFoundMap.put("TEXT", phraseMap.get("mice-prod"));
                notFoundMap.put("COLOUR", "#FFE0B2");  // TODO: fix me!

                String production_graph = mouse.get("production_graph").toString();

                if (production_graph.length() > 0) {
                    if (!notFoundMap.containsKey("details")) {
                        notFoundMap.put("details", new ArrayList<String>());
                    }
                    ArrayList<String> o = (ArrayList<String>) notFoundMap.get("details");
                    if (!o.contains(production_graph)) {
                        o.add(production_graph);
                    }
                }

                List<Map<String, Object>> contacts = (List<Map<String, Object>>) mouse.get("contacts");

                if (contacts != null) {
                    for (Map<String, Object> contact : contacts) {
                        if (!notFoundMap.containsKey("contacts")) {
                            notFoundMap.put("contacts", new ArrayList<String>());
                        }
                        ArrayList<String> o = (ArrayList<String>) notFoundMap.get("contacts");
                        if (contact.containsKey("url") && !o.contains(contact.get("url").toString())) {
                            o.add(contact.get("url").toString());
                        }
                    }
                }
            }
        }

        log.info("#### getGeneProductInfoStatusesMouseAlt: foundMap: " + foundMap);
        log.info("#### getGeneProductInfoStatusesMouseAlt: notFoundMap: " + notFoundMap);

        if (!foundMap.isEmpty()) {
            return foundMap;
        }

        if (!notFoundMap.isEmpty()) {
            return notFoundMap;
        }

        notFoundMap.put("TEXT", phraseMap.get("no-mice-prod"));
        notFoundMap.put("TEXT2", "");

        return notFoundMap;
    }
}
