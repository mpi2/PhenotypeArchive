/**
 * Copyright © 2011-2014 Genome Research Limited
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License.  
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.generic.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
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

    /**
     * Get the results of a query from the provided url.
     * 
     * @param url
     *            the URL from which to get the content
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

    public List<Map<String, String>> getGeneAllele2Info(String accession, String allele_name) throws IOException, URISyntaxException {
        
        String url = getGeneAlleleCoreUrl(accession, allele_name);

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

            genes.add(map);
        }

        return genes;
    }

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

    private List<Map<String, Object>> getGeneProductInfoOrderContactInfo(String target, JSONObject jsonObject2) throws IOException, URISyntaxException {
        if(!target.equals("order") && !target.equals("contact")) {
            log.error("#### getGeneProductInfoOrderContactInfo: illegal param: " + target);
            return null;
        }
        
        List<Map<String, Object>> list = new ArrayList<>();
        HashMap<String, Object> item = new HashMap<>();
        
        if(!jsonObject2.has(target + "_names") || !jsonObject2.has(target + "_links")) {
            return null;
        }       
        
        JSONArray names = jsonObject2.getJSONArray(target + "_names");
        JSONArray links = jsonObject2.getJSONArray(target + "_links");
        
        if(names.size() != links.size()) {
            log.error("#### getGeneProductInfoOrderContactInfo: arrays unequal size");
            return null;
        }
        
        for (int k = 0; k < names.size(); k++) {
            String name = names.getString(k);
            if (name.equals("NULL")) {
                name = target;
            }
            item.put("name", name);
            item.put("url", links.getString(k));
            list.add(item);
        }
        return list;
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
                name = "Order";
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
        map2.put("contacts", getGeneProductInfoOrderContactInfo("contact", jsonObject2));        

        map2.put("production_centre", jsonObject2.getString("production_centre"));
        map2.put("colony_name", jsonObject2.getString("name"));
        map2.put("allele_type", jsonObject2.getString("allele_type"));
        String background_colony_strain = getGeneProductInfoArrayEntry("background_colony_strain", jsonObject2.getJSONArray("genetic_info"));

        map2.put("genetic_background", background_colony_strain);        
        
        String associated_product_colony_name = "";
        if(jsonObject2.has("associated_product_colony_name")) {
            associated_product_colony_name = jsonObject2.getString("associated_product_colony_name");
            map2.put("associated_product_colony_name", associated_product_colony_name);
        }        
        
        String es_cell = NOT_FOUND;
        map2.put("associated_product_es_cell_name", "");
        if(jsonObject2.has("associated_product_es_cell_name")) {
            es_cell = jsonObject2.getString("associated_product_es_cell_name");
            map2.put("associated_product_es_cell_name", es_cell);
        }
        
        if(es_cell.length() < 1) {
            es_cell = associated_product_colony_name;
        }
        
        map2.put("es_cell", es_cell);
        
        map2.put("associated_product_vector_name", "");
        if(jsonObject2.has("associated_product_vector_name")) {
            map2.put("associated_product_vector_name", jsonObject2.getString("associated_product_vector_name"));
        }

        map2.put("qc_data", NOT_COMPLETE);
        
        if(!es_cell.equals(NOT_FOUND)) {
            map2.put("southern_tool", "http://www.sanger.ac.uk/htgt/htgt2/tools/restrictionenzymes?es_clone_name="+es_cell+"&iframe=true&width=100%&height=100%");
        }
        
        map2.put("production_completed", jsonObject2.getString("production_completed"));

        log.info("#### MOUSE production_completed:" + map2.get("production_completed"));

        String production_graph = getGeneProductInfoArrayEntry("production_graph", jsonObject2.getJSONArray("other_links"));

        map2.put("production_graph", production_graph);
        
        map2.put("_status", jsonObject2.getString("status"));        
        
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

        map2.put("southern_tool", "http://www.sanger.ac.uk/htgt/htgt2/tools/restrictionenzymes?es_clone_name="+jsonObject2.getString("name")+"&iframe=true&width=100%&height=100%");
        
        map2.put("qc_data", NOT_COMPLETE);

        map2.put("production_completed", jsonObject2.getString("production_completed"));

        map2.put("name", jsonObject2.getString("name"));

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
        
        //http://www.mousephenotype.org/imits/targ_rep/alleles/903/vector-image
        String allele_image = getGeneProductInfoArrayEntry("allele_image", jsonObject2.getJSONArray("other_links"));
        if (allele_image != null) {
            map2.put("allele_image", allele_image);
        }
        
        return map2;
    }
    
//    private List<HashMap<String, String>> getGeneProductInfoTools(String accession, String allele_name) {
//        List<HashMap<String, String>> list4 = new ArrayList<>();
//        HashMap<String, String> map5 = new HashMap<>();
//        map5.put("name", "product-core-mouse");
//        HashMap<String, String> params1 = new HashMap<>();
//        params1.put("mgi_accession_id", accession);
//        if(allele_name != null) {
//            params1.put("allele_name", allele_name);
//        }
//        params1.put("type", "mouse");
//        map5.put("url", getGeneProductCoreUrl2(params1) + "&indent=on");
//        list4.add(map5);
//        HashMap<String, String> map4 = new HashMap<>();
//        map4.put("name", "allele-core");
//        map4.put("url", getGeneAlleleCoreUrl(accession, allele_name) + "&indent=on");
//        list4.add(map4);
//        return list4;
//    }

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
        List<HashMap<String, String>> list1 = getGeneProductInfoStatusesMouse(genes);
        List<HashMap<String, String>> list2 = getGeneProductInfoStatusesEsCell(genes);
        list1.addAll(list2);
        return list1;
    }

       
    private Map<String, Object> getGeneProductInfoRejig(Map<String, Object> genes) {
        List<Map<String, Object>> mice = (List<Map<String, Object>>)genes.get("mice");
        List<Map<String, Object>> es_cells = (List<Map<String, Object>>)genes.get("es_cells");
        List<Map<String, Object>> targeting_vectors = (List<Map<String, Object>>)genes.get("targeting_vectors");
       
        if(mice != null) {
            log.info("#### mice.size(): " + mice.size()); 
        }
        if(es_cells != null) {
            log.info("#### es_cells.size(): " + es_cells.size()); 
        }
        if(targeting_vectors != null) {
            log.info("#### targeting_vectors.size(): " + targeting_vectors.size()); 
        }
        
        List<Map<String, Object>> mice_new = new ArrayList<>();
        List<Map<String, Object>> cells_new = new ArrayList<>();
        List<Map<String, Object>> vectors_new = new ArrayList<>();
        
        for(Map<String, Object> mouse : mice) {
            if(mouse.get("production_completed") == "true") {
                mice_new.add(mouse);
            }
        }
        
        if(mice_new.size() > 0 && es_cells != null) {
            
            log.info("#### do cells"); 
            
            for(Map<String, Object> mouse : mice_new) {
                log.info("#### mouse: " + mouse.toString()); 
                for(Map<String, Object> es_cell : es_cells) {
                    if(mouse.containsKey("associated_product_es_cell_name")) {
                        
                        log.info("#### associated_product_es_cell_name: " + mouse.get("associated_product_es_cell_name")); 
                        log.info("#### name: " + es_cell.get("name")); 
                        
                        if(mouse.get("associated_product_es_cell_name").equals(es_cell.get("name"))) {
                            cells_new.add(es_cell);
                        }
                    }
                }                
            }            
        }
        
        if(mice_new.size() > 0 && targeting_vectors != null) {

            log.info("#### do vectors"); 

            for(Map<String, Object> mouse : mice_new) {
                log.info("#### mouse: " + mouse.toString()); 
                for(Map<String, Object> targeting_vector : targeting_vectors) {
                    if(mouse.containsKey("associated_product_vector_name")) {

                        log.info("#### associated_product_vector_name: " + mouse.get("associated_product_vector_name")); 
                        log.info("#### name: " + targeting_vector.get("name")); 
                        log.info("#### targeting_vector: " + targeting_vector.get("targeting_vector")); 

                        if(mouse.get("associated_product_vector_name").equals(targeting_vector.get("name"))) {
                            vectors_new.add(targeting_vector);
                        }
                    }
                }                
            }            
        }
        
        if(mice_new.size() > 0) {
            genes.put("mice", mice_new);
            genes.put("es_cells", cells_new);
            genes.put("targeting_vectors", vectors_new);
            
//            summary.put("statuses", getGeneProductInfoStatuses(genes));
//            genes.put("summary", summaries.get(0));
        }
        
        return genes;
    }
    
    private Map<String, Object> getGeneProductInfoRejig2(Map<String, Object> genes) {
        List<Map<String, Object>> mice = (List<Map<String, Object>>)genes.get("mice");
        List<Map<String, Object>> es_cells = (List<Map<String, Object>>)genes.get("es_cells");
        List<Map<String, Object>> targeting_vectors = (List<Map<String, Object>>)genes.get("targeting_vectors");
       
        if(mice != null) {
            log.info("#### mice.size(): " + mice.size()); 
        }
        if(es_cells != null) {
            log.info("#### es_cells.size(): " + es_cells.size()); 
        }
        if(targeting_vectors != null) {
            log.info("#### targeting_vectors.size(): " + targeting_vectors.size()); 
        }
        
        List<Map<String, Object>> mice_new = new ArrayList<>();
        List<Map<String, Object>> cells_new = new ArrayList<>();
        List<Map<String, Object>> vectors_new = new ArrayList<>();
        
       // log.info("#### before: mice.size(): " + mice.size()); 

        if(mice != null) {
            Iterator<Map<String, Object>> i0 = mice.iterator();
            while (i0.hasNext()) {
               Map<String, Object> mouse = i0.next(); // must be called before you can call i.remove()
               if(mouse.get("production_completed") == "true") {
                  mice_new.add(mouse);
                  i0.remove();
                }
            }
        }

//        log.info("#### mice_new.size(): " + mice_new.size()); 
//        log.info("#### after: mice.size(): " + mice.size()); 

        if(mice_new.size() > 0 && es_cells != null) {
            
            log.info("#### do cells"); 
            
            for(Map<String, Object> mouse : mice_new) {
                log.info("#### mouse: " + mouse.toString()); 

                    Iterator<Map<String, Object>> i1 = es_cells.iterator();
                    while (i1.hasNext()) {
                        Map<String, Object> es_cell = i1.next(); 

                        if(mouse.containsKey("associated_product_es_cell_name")) {
                        
                        log.info("#### associated_product_es_cell_name: " + mouse.get("associated_product_es_cell_name")); 
                        log.info("#### name: " + es_cell.get("name")); 
                        
                        if(mouse.get("associated_product_es_cell_name").equals(es_cell.get("name"))) {
                            cells_new.add(es_cell);
                            i1.remove();
                        }
                    }
                }                
            }            
        }
        
        if(mice_new.size() > 0 && targeting_vectors != null) {

            log.info("#### do vectors"); 

            for(Map<String, Object> mouse : mice_new) {
                log.info("#### mouse: " + mouse.toString()); 
                
                   Iterator<Map<String, Object>> i2 = targeting_vectors.iterator();
                    while (i2.hasNext()) {
                       Map<String, Object> targeting_vector = i2.next(); 
                
                        if(mouse.containsKey("associated_product_vector_name")) {
                            log.info("#### associated_product_vector_name: " + mouse.get("associated_product_vector_name")); 
                            log.info("#### name: " + targeting_vector.get("name")); 
                            log.info("#### targeting_vector: " + targeting_vector.get("targeting_vector")); 

                            if(mouse.get("associated_product_vector_name").equals(targeting_vector.get("name"))) {
                                vectors_new.add(targeting_vector);
                                i2.remove();
                            }
                        }
                }                
            }            
        }
        
        if(mice_new.size() > 0) {
//            log.info("#### end: mice: " + mice.size()); 
//            log.info("#### end: mice_new: " + mice_new.size()); 
            mice_new.addAll(mice);
          //  log.info("#### done: mice_new: " + mice_new.size()); 
            genes.put("mice", mice_new);

         //   log.info("#### end: es_cells: " + es_cells.size()); 
         //   log.info("#### end: cells_new: " + cells_new.size()); 
            cells_new.addAll(es_cells);
          //  log.info("#### done: cells_new: " + cells_new.size()); 
            genes.put("es_cells", cells_new);

        //    log.info("#### end: targeting_vectors: " + targeting_vectors.size()); 
         //   log.info("#### end: vectors_new: " + vectors_new.size()); 
            vectors_new.addAll(targeting_vectors);
         //   log.info("#### done: vectors_new: " + vectors_new.size()); 
            genes.put("targeting_vectors", vectors_new);
            
//            summary.put("statuses", getGeneProductInfoStatuses(genes));
//            genes.put("summary", summaries.get(0));
        }
        
        return genes;
    }

    private List<HashMap<String, String>> getGeneProductInfoStatusesMouse(Map<String, Object> genes) {
        
        List<Map<String, Object>> mice = (List<Map<String, Object>>)genes.get("mice");
            
        List<HashMap<String, String>> listFound = new ArrayList<>();
        List<HashMap<String, String>> listNotFound = new ArrayList<>();
      //  List<HashMap<String, String>> listAll = new ArrayList<>();
        
        for(Map<String, Object> mouse : mice) {
            if(mouse.get("production_completed") == "true") {       // && mice.get("mice_complete")) {
                HashMap<String, String> map2 = new HashMap<>();
                map2.put("DETAILS", mouse.get("production_graph").toString());
                map2.put("TEXT", phraseMap.get("mice"));
                map2.put("TEXT2", mouse.get("_status").toString() + " (" + mouse.get("production_completed").toString() + ")");
                map2.put("COLOUR", "#E0F9FF");

                List<Map<String, Object>> orders = (List<Map<String, Object>>) mouse.get("orders");

                if(orders != null) {
                    for(Map<String, Object> order : orders) {                
                        map2.put("ORDER", order.get("url").toString());
                    }
                }

                listFound.add(map2);
              //  listAll.add(map2);                
            }
            else {
                HashMap<String, String> map2 = new HashMap<>();
                map2.put("TEXT", phraseMap.get("mice-prod"));
                map2.put("TEXT2", mouse.get("_status").toString() + " (" + mouse.get("production_completed").toString() + ")");
                map2.put("COLOUR", "#FFE0B2");
                map2.put("DETAILS", mouse.get("production_graph").toString());

                List<Map<String, Object>> contacts = (List<Map<String, Object>>) mouse.get("contacts");

                if(contacts != null) {
                    for(Map<String, Object> contact : contacts) {                
                        map2.put("CONTACT", contact.get("url").toString());
                    }
                }
                
                listNotFound.add(map2);
               // listAll.add(map2);
            }
        }

        if(listFound.size() > 0) {
            return listFound.subList(0, 1); // TODO: fix me!
        }

        if(listNotFound.size() > 0) {
            return listNotFound.subList(0, 1); // TODO: fix me!
        }

        HashMap<String, String> map2 = new HashMap<>();
        map2.put("TEXT", phraseMap.get("no-mice-prod"));
        map2.put("TEXT2", "");
        listNotFound.add(map2);
        return listNotFound;
    }

    private List<HashMap<String, String>> getGeneProductInfoStatusesEsCell(Map<String, Object> genes) {
        
        List<Map<String, Object>> es_cells = (List<Map<String, Object>>)genes.get("es_cells");
            
        List<HashMap<String, String>> listFound = new ArrayList<>();
        List<HashMap<String, String>> listNotFound = new ArrayList<>();
        //List<HashMap<String, String>> listAll = new ArrayList<>();
        
        for(Map<String, Object> es_cell : es_cells) {
            if(es_cell.get("production_completed") == "true") {       // && mice.get("mice_complete")) {
                HashMap<String, String> map2 = new HashMap<>();
                map2.put("TEXT", phraseMap.get("cells"));
                map2.put("COLOUR", "#E0F9FF");

                List<Map<String, Object>> orders = (List<Map<String, Object>>) es_cell.get("orders");

                if(orders != null) {
                    for(Map<String, Object> order : orders) {            
                        map2.put("ORDER", order.get("url").toString());
                    }
                }

                listFound.add(map2);
              //  listAll.add(map2);                
            }
            else {
                HashMap<String, String> map2 = new HashMap<>();
                map2.put("TEXT", phraseMap.get("cell-prod"));
                map2.put("COLOUR", "#FFE0B2");

                List<Map<String, Object>> contacts = (List<Map<String, Object>>) es_cell.get("contacts");

                if(contacts != null) {
                    for(Map<String, Object> contact : contacts) {                
                        map2.put("CONTACT", contact.get("url").toString());
                    }
                }
                
                listNotFound.add(map2);
              //  listAll.add(map2);
            }
        }
        
        if(listFound.size() > 0) {
            return listFound.subList(0, 1); // TODO: fix me!
        }

        if(listNotFound.size() > 0) {
            return listNotFound.subList(0, 1); // TODO: fix me!
        }

        HashMap<String, String> map2 = new HashMap<>();
        map2.put("TEXT", phraseMap.get("no-cell-prod"));
        map2.put("TEXT2", "");
        listNotFound.add(map2);
        return listNotFound;
    }
    
    private String getGeneAlleleCoreUrl2(HashMap<String, String> params) {
        List<String> qparams = new ArrayList<>();
        for(String s : params.keySet()) {
            qparams.add(s + ":" + params.get(s).replace(":", "\\:"));
        }
        
        String qs = StringUtils.join(qparams, " ");
                
        String url = "http://ikmc.vm.bytemark.co.uk:8985/solr/allele2/search?q="
            + qs
            + "&start=0&rows=100&hl=true&wt=json";
        
            return url;
    }
    
    private String getGeneAlleleCoreUrl(String accession, String allele_name) {
        String qallele_name = "";
        if(allele_name != null) {
            qallele_name = " allele_name:\"" + allele_name + "\"";            
        }
        
        String url = "http://ikmc.vm.bytemark.co.uk:8985/solr/allele2/search?q=mgi_accession_id:"
                + accession.replace(":", "\\:")
                + qallele_name
                + "&start=0&rows=100&hl=true&wt=json";

        return url;
    }

    public String getGeneProductCoreUrl2(Map<String, String> params) {
        log.info("#### getGeneProductCoreUrl2");
        
        List<String> qparams = new ArrayList<>();
        for(String s : params.keySet()) {
            if(s.equals("multi")) {
                qparams.add(params.get(s));
                continue;
            }
            qparams.add(s + ":" + params.get(s).replace(":", "\\:"));
        }
        
        String qs = StringUtils.join(qparams, " ");
                
        String url = "http://ikmc.vm.bytemark.co.uk:8985/solr/product/search?q="
            + qs
            + "&start=0&rows=100&hl=true&wt=json&indent=on";

        log.info("#### getGeneProductCoreUrl2: url: " + url);
        
        return url;
    }

    public String getGeneProductCoreUrl3(Map<String, String> params) {
        List<String> qparams = new ArrayList<>();
        for(String s : params.keySet()) {
            if(s.equals("multi")) {
                qparams.add(params.get(s));
                continue;
            }
            qparams.add(s + ":" + params.get(s).replace(":", "\\:"));
        }
        
        String qs = StringUtils.join(qparams, " ");
                
        String url = "http://ikmc.vm.bytemark.co.uk:8985/solr/product/select?q="
            + qs
            + "&start=0&rows=100&hl=true&wt=json";
        
            return url;
    }

    private String getGeneProductCoreUrl(String accession, String allele_name) {
        String qallele_name = "";
        if(allele_name != null) {
            qallele_name = " allele_name:\"" + allele_name + "\"";            
        }
        
        String url = "http://ikmc.vm.bytemark.co.uk:8985/solr/product/search?q=mgi_accession_id:"
            + accession.replace(":", "\\:")
            + qallele_name
            + "&start=0&rows=100&hl=true&wt=json";
        
            return url;
    }
    
    public Map<String, Object> getGeneProductInfo(String accession, String allele_name, boolean debug) throws IOException, URISyntaxException, Exception {
        
        String url = getGeneProductCoreUrl(accession, allele_name);

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
        
        int targeting_vector_counter = 0;

        for (Object doc : docs) {
            JSONObject jsonObject2 = (JSONObject) doc;
            String type = jsonObject2.getString("type");

            if (type.equals("mouse")) {
                mice.add(getGeneProductInfoMice(jsonObject2));
            }

            if (type.equals("es_cell")) {
                es_cells.add(getGeneProductInfoEsCells(jsonObject2));
            }

            if (type.equals("targeting_vector")) {
                targeting_vector_counter += 1;
                targeting_vectors.add(getGeneProductInfoTargetingVectors(jsonObject2));
            }
        }

        log.info("#### getGeneProductInfo: mice.size(): " + mice.size());
        log.info("#### getGeneProductInfo: es_cells.size(): " + es_cells.size());
        log.info("#### getGeneProductInfo: targeting_vectors.size(): " + targeting_vectors.size() +" - targeting_vector_counter: " + targeting_vector_counter);
        
        genes.put("mice", mice);
        genes.put("es_cells", es_cells);
        genes.put("targeting_vectors", targeting_vectors);

        List<Map<String, String>> constructs2 = getGeneAllele2Info(accession, allele_name);
        
        List<HashMap<String, Object>> summaries = new ArrayList<>();
        
        if(constructs2 != null) {
        for(Map<String, String> item : constructs2) {
            HashMap<String, Object> summary = new HashMap<>();
            summary.put("marker_symbol", item.get("marker_symbol"));
            summary.put("symbol", item.get("marker_symbol") + "<sup>" + item.get("allele_name") + "</sup>");
            summary.put("genbank", item.get("genbank_file"));
            summary.put("map_image", item.get("allele_image") + "?simple=true");
            summary.put("allele_description", productMap.get("1" + item.get("allele_type")));
                        
            List<String> southern_tools = new ArrayList<>();
            for(Map<String, Object> mouse : mice) {
                
                if(mouse.containsKey("associated_product_es_cell_name")) {
                    String st = "http://www.sanger.ac.uk/htgt/htgt2/tools/restrictionenzymes?es_clone_name=" +
                    mouse.get("associated_product_es_cell_name") + 
                    "&iframe=true&width=100%&height=100%";
                    southern_tools.add(st);
                    log.info("#### getGeneProductInfo: southern_tool: " + st.toString());
                }
                summary.put("southern_tools", southern_tools);
            }
            
            if(southern_tools.size() > 0) {
                summary.put("southern_tool", southern_tools.get(0));
            }
            
            summary.put("lrpcr_genotyping_primers", "lrpcr_genotyping_primers");

            summary.put("mutagenesis_url", "mutagenesis_url");    // FIX-ME!
            
            summary.put("statuses", getGeneProductInfoStatuses(genes));    // FIX-ME!                

            HashMap<String, String> map3 = new HashMap<>();
            map3.put("browser", "Ensembl");
            map3.put("url", "http://www.ensembl.org/Mus_musculus/Location/View?r=9:54544794-54560…c.uk/das/ikmc_products=normal,contig=normal,ruler=normal,scalebar=normal");
            List<HashMap<String, String>> list3 = new ArrayList<>();
            list3.add(map3);
            
            summary.put("browsers", list3);    // FIX-ME!
            
           // summary.put("tools", getGeneProductInfoTools(accession, allele_name));    // FIX-ME!
            summaries.add(summary);
        }
        }
      
        if(summaries.size() > 1) {
            log.error("######## invalid count for summary!"); 
        }

        if(summaries.size() > 0) {
            genes.put("summary", summaries.get(0));
        }
        
        log.info("#### summaries: " + summaries.toString()); 
        
        if(!debug) {
            genes = getGeneProductInfoRejig2(genes);
        }

        return genes;
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
            String type = jsonObject2.getString("type");

            if (type.equals("mouse")) {
                Map<String, Object> hash = new HashMap<>();
                String key = jsonObject2.get("marker_symbol") + "_" + jsonObject2.get("allele_name") + "_" + jsonObject2.get("mgi_accession_id");
                if(clashes.containsKey(key)) {
                    continue;
                }
                clashes.put(key, "found");
                hash.put("marker_symbol", jsonObject2.get("marker_symbol"));            
                hash.put("allele_name", jsonObject2.get("allele_name"));            
                hash.put("mgi_accession_id", jsonObject2.get("mgi_accession_id"));     
                list.add(hash);
            }
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

            if (type.equals("mouse")) {
                Map<String, Object> hash = new HashMap<>();
                String key = jsonObject2.get("marker_symbol") + "_" + jsonObject2.get("allele_name") + "_" + jsonObject2.get("mgi_accession_id");
                if(clashes.containsKey(key)) {
                    continue;
                }
                clashes.put(key, "found");
                hash.put("marker_symbol", jsonObject2.get("marker_symbol"));            
                hash.put("allele_name", jsonObject2.get("allele_name"));            
                hash.put("mgi_accession_id", jsonObject2.get("mgi_accession_id"));     
                list.add(hash);
            }
        }
        
        return list;
    }
    
    public HashMap<String, HashMap<String, List<String>>>  getAlleleQcInfo(String alleleType, String type, String name)
    throws IOException, URISyntaxException {

        name = name.replace("-","\\-");
        //.replace('+','\+').replace('!','\!').replace('(','\(').replace(')','\)').replace('{','\{').replace('}','\}').replace('[','\[').replace(']','\]').replace(':','\:');
        String url = "http://ikmc.vm.bytemark.co.uk:8985/solr/product/search?q=name:"
                + name
                + " AND type:"
                + type
                + " AND allele_type:"
                + alleleType
                + "&start=0&rows=100&hl=true&wt=json"
                ;

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
}
