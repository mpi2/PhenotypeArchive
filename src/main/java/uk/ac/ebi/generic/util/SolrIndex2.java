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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Resource;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
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
    private static final String ALLELE_NAME_FIELD = "allele_name_str";
    private static final String ALLELE_TYPE_FIELD = "allele_type";
    private static final String IMITS_SOLR_CORE_URL = "http://ikmc.vm.bytemark.co.uk:8983";
    
    private static final Map<String, String> phraseMap;

    static {
        Map<String, String> pMap = new HashMap<>();
        pMap.put("mice", "There are mice");
        pMap.put("mice-prod", "There is mouse production in progress");
        pMap.put("no-mice-prod", "There is no mouse production");

        pMap.put("cells", "There are targeted ES Cells");
        pMap.put("cells-prod", "There is cell production");
        pMap.put("no-cells-prod", "There is no cell production");
        phraseMap = Collections.unmodifiableMap(pMap);
    }
    
// Get product data for order section on the gene page 
    public List<Map<String, Object>> getGeneProductInfo(String accession) throws IOException, URISyntaxException, Exception {

        
        
        String alleleSearch = getAlleleUrl(accession, null);
        String allelelUrl = searchAlleleCore("impc", alleleSearch);
                
        JSONObject jsonAllele = getResults(allelelUrl);
        JSONArray alleleDocs = jsonAllele.getJSONObject("response").getJSONArray("docs");
        
        Map<String, Map<String, Object>> alleleMap = new HashMap<>();
        for (Object alelelDoc : alleleDocs){
            JSONObject jsonObject2 = (JSONObject) alelelDoc;
            String allele_name = jsonObject2.getString("allele_name");        
            alleleMap.put(allele_name, getAlleleData(jsonObject2));
        }
        
        String productSearch = getAllProductsUrl(accession, null);
        String url = searchProductCore("impc", productSearch);
                
        JSONObject jsonObject1 = getResults(url);
        JSONArray docs = jsonObject1.getJSONObject("response").getJSONArray("docs");

        if (docs.size() < 1) {
            return null;
        }

        List<Map<String, Object>> mice = new ArrayList<>();
        List<Map<String, Object>> es_cells = new ArrayList<>();
        List<Map<String, Object>> targeting_vectors = new ArrayList<>();
        HashMap<String, Object> cre_status = new HashMap<>();
        cre_status.put("cre_exists", "false");
        cre_status.put("product_type", "None");
        cre_status.put("mgi_acc", "");
        
        for (Object doc : docs) {
            JSONObject jsonObject2 = (JSONObject) doc;
            String type = jsonObject2.getString("type");

            if (type.equals("mouse")) {
                Map<String, Object> mouse = getMouseData(jsonObject2);
                if (mouse.get("production_completed").toString().equals("true")) {
                    if (alleleMap.get(mouse.get("allele_name").toString()) != null){
                        mouse.put("allele_description",  alleleMap.get(mouse.get("allele_name").toString()).get("allele_description"));
                        mouse.put("genbank_file",  alleleMap.get(mouse.get("allele_name").toString()).get("genbank_file"));
                        mouse.put("allele_simple_image",  alleleMap.get(mouse.get("allele_name").toString()).get("allele_simple_image"));
                    } else {
                        mouse.put("allele_description", "");
                        mouse.put("genbank_file", "");
                        mouse.put("allele_simple_image", "");
                    }
                    mice.add(mouse);
                }
            }

            if (type.equals("es_cell")) {
                Map<String, Object> es_cell = getEsCellData(jsonObject2);
                                        
                if (alleleMap.get(es_cell.get("allele_name").toString()) != null){
              
                    es_cell.put("allele_description",  alleleMap.get(es_cell.get("allele_name").toString()).get("allele_description"));
                    es_cell.put("genbank_file",  alleleMap.get(es_cell.get("allele_name").toString()).get("genbank_file"));
                    es_cell.put("allele_simple_image",  alleleMap.get(es_cell.get("allele_name").toString()).get("allele_simple_image"));
                } else {
                    es_cell.put("allele_description", "");
                    es_cell.put("genbank_file", "");
                    es_cell.put("allele_simple_image", "");
                }
                es_cells.add(es_cell);
            }

            if (type.equals("targeting_vector")) {
                Map<String, Object> targeting_vector = getTargetingVectorData(jsonObject2);
                if (alleleMap.get(targeting_vector.get("allele_name").toString()) != null){ 
                    targeting_vector.put("allele_description",  alleleMap.get(targeting_vector.get("allele_name").toString()).get("allele_description"));        
                } else {
                    targeting_vector.put("allele_description", "");                
                }
                targeting_vectors.add(targeting_vector);
            }
        }
                
        List<Map<String, Object>> mapper = filterGeneProductInfoList(mice, es_cells, targeting_vectors);
        
        String creSearch = getMiceAndEsCellsUrl(accession, null);
        String creUrl = searchProductCore("cre", creSearch);
        JSONObject jsonObjectCre = getResults(creUrl);
        JSONArray creDocs = jsonObjectCre.getJSONObject("response").getJSONArray("docs");
        
        if (creDocs.size() > 0) {
           for (Object credoc : creDocs) {
               JSONObject jsonObjectCre2 = (JSONObject) credoc;
               String creType = jsonObjectCre2.getString("type");
               if (creType.equals("mouse")) {
                   cre_status.put("cre_exists", "true");
                   cre_status.put("product_type", "Mice");
                   cre_status.put("mgi_acc", jsonObjectCre2.getString("mgi_accession_id"));
               }
               if (creType.equals("es_cell")) {
                   if (!cre_status.get("product_type").equals("Mice") ){
                       cre_status.put("cre_exists", "true");
                       cre_status.put("product_type", "ES Cell");  
                       cre_status.put("mgi_acc", jsonObjectCre2.getString("mgi_accession_id"));
                   }
               }
           } 
            
        }

        mapper.add(cre_status);

 //       log.info("#### getGeneProductInfo2: count: " + mapper.size());

        return mapper;
    }

    
// gets product data for allele page
    public Map<String, Object> getAlleleProductInfo(String pipeline, Map<String, String> hash, boolean debug) throws IOException, URISyntaxException, Exception {
        log.info("#### getAlleleProductInfo");
        String accession = hash.get("accession");
        String searchParams = "";
        String otherAllelesSearchParams;
        String alleleSearchParams = "";
        String geneSearchParams;
        String url;
        String allele_name = "";
        String allele_url;
        String other_alleles_url;
        String gene_url;
        
        if (hash.containsKey("allele_name") && hash.get("allele_name") != null){
            log.info(" With allele name");
            log.info("MGI ACCESSION" + accession);
            allele_name = hash.get("allele_name");
            searchParams = getAllProductsUrl(accession, allele_name);    
            alleleSearchParams = getAlleleUrl(accession, allele_name);    
        }
        else if (hash.containsKey("cassette") && hash.containsKey("design_id")){
            log.info(" With cassette and design_id");
            log.info("MGI ACCESSION" + accession);
            String cassette = hash.get("cassette");
            String design_id = hash.get("design_id");
            searchParams = getAllProductsUrl(accession, cassette, design_id);
            alleleSearchParams = getAlleleUrl(accession, cassette, design_id);
        }
        
        otherAllelesSearchParams = getMiceAndEsCellsUrl(accession, null);
        geneSearchParams = getGeneUrl(accession);
        
        url = searchProductCore(pipeline, searchParams);
        allele_url = searchAlleleCore(pipeline, alleleSearchParams);
        other_alleles_url = searchProductCore(pipeline, otherAllelesSearchParams);
        gene_url = searchAlleleCore(pipeline, geneSearchParams);
        
        log.info("#### url for getAlleleProductInfo=" + url);
        log.info("#### url for getAlleleProductInfo=" + allele_url);
        log.info("#### url for getAlleleProductInfo=" + other_alleles_url);
        log.info("#### url for getAlleleProductInfo=" + gene_url);        
        
        JSONObject geneJsonObject = getResults(gene_url);
        JSONArray geneDoc = geneJsonObject.getJSONObject("response").getJSONArray("docs");
        
        JSONObject alleleJsonObject = getResults(allele_url);
        JSONArray alleleDoc = alleleJsonObject.getJSONObject("response").getJSONArray("docs");

        JSONObject otherAllelesJsonObject = getResults(other_alleles_url);
        JSONArray otherAllelesDoc = otherAllelesJsonObject.getJSONObject("response").getJSONArray("docs");
        
        // gets Mouse and ES Cell data
        JSONObject jsonObject1 = getResults(url);
        JSONArray docs = jsonObject1.getJSONObject("response").getJSONArray("docs");
        
        Map<String, Object> mapper = new HashMap<>();
        List<Map<String, Object>> mice = new ArrayList<>();
        List<Map<String, Object>> es_cells = new ArrayList<>();
        List<Map<String, Object>> targeting_vectors = new ArrayList<>();
        
        if (alleleDoc.size() < 1) {
            log.info("#### No rows returned for the query!");
            return null;
        }
            

        log.info("#### Found " + docs.size() + " rows!");

        String title;
        String marker_symbol;

       
        HashMap<String, Object> otherAlleleWithMice = new HashMap<>();
        HashMap<String, Object> otherAlleleWithEscells = new HashMap<>();
        HashMap<String, Object> colonyMap = new HashMap<>();
        for (Object otherAlleleDoc : otherAllelesDoc) {
            JSONObject jsonObject2 = (JSONObject) otherAlleleDoc;
            String type = jsonObject2.getString("type");
            if (type.equals("mouse")) {
                HashMap<String, Object> otherAlleleHash = new HashMap<>();
                Map<String, Object> allele = getMouseData(jsonObject2);
                String alleleIndex = allele.get("allele_name").toString();
                if (alleleIndex.equals(allele_name) || alleleIndex.equals("None")) {continue;}
                
                 if (! otherAlleleWithMice.containsKey(alleleIndex)){
                     List<String> colony_list = new ArrayList<>();
                     colony_list.add(allele.get("colony_name").toString());                     
                     
                     otherAlleleHash.put("allele_type", allele.get("allele_type").toString());
                     otherAlleleHash.put("allele_name", alleleIndex);
                     otherAlleleHash.put("allele_name_suffix", getSuffix(alleleIndex));
                     otherAlleleHash.put("colony_names", colony_list);
                     otherAlleleHash.put("mice_available", allele.get("production_completed").toString());
                     otherAlleleHash.put("mice_production_started", "true");
                     otherAlleleWithMice.put(alleleIndex, otherAlleleHash);
                     colonyMap.put(allele.get("colony_name").toString(), otherAlleleHash);
                 } else {
                     HashMap<String, Object> oa = (HashMap<String, Object>) otherAlleleWithMice.get(alleleIndex);
                     String mice_available = oa.get("mice_available").toString();
                     if (allele.get("production_completed").equals("true")){
                         mice_available = "true";
                     }
                     
                     List<String> colony_list = (ArrayList) oa.get("colony_names");
                     colony_list.add(allele.get("colony_name").toString());
                     
                     otherAlleleWithMice.remove(alleleIndex);
                     otherAlleleHash.put("allele_type", allele.get("allele_type").toString());
                     otherAlleleHash.put("allele_name", alleleIndex);
                     otherAlleleHash.put("allele_name_suffix", getSuffix(alleleIndex));
                     otherAlleleHash.put("colony_names", colony_list);
                     otherAlleleHash.put("mice_available", mice_available);
                     otherAlleleHash.put("mice_production_started", "true");
                     
                     otherAlleleWithMice.put(alleleIndex, otherAlleleHash);
                     colonyMap.put(allele.get("colony_name").toString(), otherAlleleHash);
                 }                
            }
            if (type.equals("es_cell")) {
                HashMap<String, Object> otherAlleleHash = new HashMap<>();
                Map<String, Object> allele = getEsCellData(jsonObject2);
                String alleleIndex = allele.get("allele_name").toString();
                if (alleleIndex.equals(allele_name) || alleleIndex.equals("None")) {continue;}
                
                 if (! otherAlleleWithEscells.containsKey(alleleIndex)){        
                     List<String> es_cell_list = new ArrayList<>();
                     es_cell_list.add(allele.get("es_cell_clone").toString()); 
                     
                     otherAlleleHash.put("allele_type", allele.get("allele_type").toString());
                     otherAlleleHash.put("allele_name", alleleIndex);
                     otherAlleleHash.put("allele_name_suffix", getSuffix(alleleIndex));
                     otherAlleleHash.put("es_cell_names", es_cell_list);
                     otherAlleleHash.put("es_cells_available", allele.get("production_completed").toString());
                     otherAlleleHash.put("es_cell_production_started", "true");
                     otherAlleleWithEscells.put(alleleIndex, otherAlleleHash);
                 } else {
                     HashMap<String, Object> oa2 = (HashMap<String, Object>) otherAlleleWithEscells.get(alleleIndex);
                     String es_cells_available = oa2.get("es_cells_available").toString();
                     if (allele.get("production_completed").equals("true")){
                         es_cells_available = "true";
                     }
                     List<String> es_cell_list = (ArrayList) oa2.get("es_cell_names");
                     es_cell_list.add(allele.get("es_cell_clone").toString());
                     
                     otherAlleleWithEscells.remove(alleleIndex);
                     otherAlleleHash.put("allele_type", allele.get("allele_type").toString());
                     otherAlleleHash.put("allele_name", alleleIndex);
                     otherAlleleHash.put("allele_name_suffix", getSuffix(alleleIndex));
                     otherAlleleHash.put("es_cell_names", es_cell_list);
                     otherAlleleHash.put("es_cells_available", es_cells_available);
                     otherAlleleHash.put("es_cell_production_started", "true");
                     otherAlleleWithEscells.put(alleleIndex, otherAlleleHash);
                 } 
            }

        }

        HashMap<String, Object> allele_excision_mapping = new HashMap<>();
        
        allele_excision_mapping.put("a-b", "Cre");
        allele_excision_mapping.put("a-c", "Flp");
        allele_excision_mapping.put("a-d", "Flp and Cre");
        allele_excision_mapping.put("c-d", "Cre");
        allele_excision_mapping.put("e-e.1", "Cre");
        allele_excision_mapping.put("-.1", "Cre");
        allele_excision_mapping.put("-.2", "Flp");
        
        for (Object doc : docs) {
            JSONObject jsonObject2 = (JSONObject) doc;
            String type = jsonObject2.getString("type");

            if (type.equals("mouse")) {
                Map<String, Object> mouse = getMouseData(jsonObject2);

                
                 String colonyIndex = mouse.get("associated_product_colony_name").toString();
            
                 mouse.put("associated_colony_allele_name","");
                 mouse.put("associated_colony_allele_type","");
                 mouse.put("excision","");

                 if (colonyMap.containsKey(colonyIndex)) {
                     mouse.put("associated_colony_allele_name",((HashMap) colonyMap.get(colonyIndex)).get("allele_name"));
                     mouse.put("associated_colony_allele_type",((HashMap) colonyMap.get(colonyIndex)).get("allele_type"));
                     
                     if (allele_excision_mapping.containsKey( ((HashMap) colonyMap.get(colonyIndex)).get("allele_type") + "-" + mouse.get("allele_type").toString())){
                         mouse.put("excision",allele_excision_mapping.get(((HashMap) colonyMap.get(colonyIndex)).get("allele_type") + "-" + mouse.get("allele_type").toString()));
                     }
                 }
                 

                log.info("#### getAlleleProductInfo: mouse: " + mouse);   
                
                mice.add(mouse);
            }

            if (type.equals("es_cell")) {
                Map<String, Object> es_cell = getEsCellData(jsonObject2);
                es_cells.add(es_cell);
            }
            
            if (type.equals("targeting_vector")) {
                Map<String, Object> targeting_vector = getTargetingVectorData(jsonObject2);
                targeting_vectors.add(targeting_vector);
            }
        }
        
        
        mapper.put("other_alleles_with_mice",otherAlleleWithMice);
        mapper.put("other_alleles_with_es_cells",otherAlleleWithEscells);
        
        JSONObject geneObject = (JSONObject) geneDoc.getJSONObject(0);
        JSONObject alleleObject = (JSONObject) alleleDoc.getJSONObject(0); 

        mapper.put("gene", getGeneData(geneObject));
        mapper.put("allele", getAlleleData(alleleObject));

        Map<String, String>  gene = (Map<String, String>)mapper.get("gene");
        marker_symbol = gene.get("marker_symbol");
        title = getTitle(marker_symbol, allele_name);

        
        mapper.put("title", title);
        mapper.put("mice", mice);
        mapper.put("es_cells", es_cells);
        mapper.put("targeting_vectors", targeting_vectors);

        mapper.put("summary", getSummary(accession, allele_name, mapper));

        String stripped = title.replaceAll("\\<sup\\>", "");
        stripped = stripped.replaceAll("\\<\\/sup\\>", "");
        mapper.put("title_alt", stripped);
        mapper = sortAlleleProducts(mapper);

        return mapper;
    }


    public List<Map<String, Object>> getAllAlleles(String pipeline, String acc) throws IOException, URISyntaxException {
        
        String searchString;
        String url;
        
        searchString = getAlleleUrl(acc, null);
        url = searchAlleleCore(pipeline, searchString);

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
            list.add(getAlleleData(jsonObject2));
        }

        return list;
    }
    
    public HashMap<String, HashMap<String, List<String>>> getAlleleQcInfo(String pipeline, String type, String name)
            throws IOException, URISyntaxException {

        String url;
        String searchParams;
        searchParams = getProduct(name, type);
        url = searchProductCore(pipeline, searchParams);
        JSONObject jsonObject = getResults(url);
        
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
                log.error("#### getAlleleQcInfo exception: " + e);
            }
        }
        return construct;
    }
    
    
    private String getAllProductsUrl(String accession, String allele_name) {
        log.info("#### getAllProductsUrl");
        String qallele_name = "";

        if (allele_name != null) {
            qallele_name = " AND " + ALLELE_NAME_FIELD + ":\"" + allele_name + "\"";

        }

        String target = "mgi_accession_id:" + accession.replace(":", "\\:") + qallele_name;

        String search_url = "/select?q="
                + target
                + "&start=0&rows=100&hl=true&wt=json";

        log.info(search_url);
        return search_url;
    }
    
    private String getAllProductsUrl(String accession, String cassette, String design) {
        log.info("#### getAllProductsUrl");
        String qallele_search = "";

        if (cassette != null && design != null) {
            qallele_search = " AND cassette:\"" + cassette + "\" AND design_id:\"" + design + "\""  ;
        }

        String target = "mgi_accession_id:" + accession.replace(":", "\\:") + qallele_search;

        String search_url = "/select?q="
                + target
                + "&start=0&rows=100&hl=true&wt=json";

        log.info(search_url);
        return search_url;
    }
    
    private String getMiceAndEsCellsUrl(String accession, String allele_name) {
        log.info("#### getMiceAndEsCellsUrl");
        String qallele_name = "";

        if (allele_name != null) {
            qallele_name = " AND " + ALLELE_NAME_FIELD + ":\"" + allele_name + "\"";

        }

        String target = "-type:targeting_vector AND mgi_accession_id:" + accession.replace(":", "\\:") + qallele_name;

        String search_url = "/select?q="
                + target
                + "&start=0&rows=100&hl=true&wt=json";

        log.info(search_url);
        return search_url;
    }
    
    private String getMiceAndEsCellsUrl(String accession, String cassette, String design) {
        String qallele_search = "";

        if (cassette != null && design != null) {
            qallele_search = " AND cassette:\"" + cassette + "\" AND design_id:\"" + design + "\""  ;
        }

        String target = "-type:targeting_vector AND mgi_accession_id:" + accession.replace(":", "\\:") + qallele_search;

        String search_url = "/select?q="
                + target
                + "&start=0&rows=100&hl=true&wt=json";

        return search_url;
    }
    
    
    
    private String getProduct(String name, String type) {
        name = name.replace("-", "\\-");
        String search_url = "/search?q=name:"
                + '"' + name + '"'
                + " AND type:"
                + '"' + type + '"'
                + "&start=0&rows=100&hl=true&wt=json";
        
        return search_url;
    }
    
    
    private String getAlleleUrl(String accession, String allele_name) {
        log.info("#### getMiceAndEsCellsUrl");
        String qallele_name = "";

        if (allele_name != null) {
            qallele_name = " AND " + ALLELE_NAME_FIELD + ":\"" + allele_name + "\"";

        }

        String target = "type:allele AND mgi_accession_id:" + accession.replace(":", "\\:") + qallele_name;

        String search_url = "/select?q="
                + target
                + "&start=0&rows=100&hl=true&wt=json";

        log.info(search_url);
        return search_url;
    }
    
    private String getAlleleUrl(String accession, String cassette, String design) {
        String qallele_search = "";

        if (cassette != null && design != null) {
            qallele_search = " AND cassette:\"" + cassette + "\" AND design_id:\"" + design + "\""  ;
        }

        String target = "type:allele AND mgi_accession_id:" + accession.replace(":", "\\:") + qallele_search;

        String search_url = "/select?q="
                + target
                + "&start=0&rows=100&hl=true&wt=json";

        return search_url;
    }  
    
    private String getGeneUrl(String accession) {

        String target = "type:gene AND mgi_accession_id:" + accession.replace(":", "\\:");

        String search_url = "/select?q="
                + target
                + "&start=0&rows=100&hl=true&wt=json";

        return search_url;
    }  
    
    
// SOLR QUERIES
    
    private String searchProductCore(String pipeline, String searchUrl) throws IOException,
            URISyntaxException {
        
        String hostUrl;
        String url;        
        
        if (pipeline.equals("cre")) {
            hostUrl = IMITS_SOLR_CORE_URL + "/solr/eucommtoolscre_product";
        }
        else {
            hostUrl = IMITS_SOLR_CORE_URL + "/solr/product";
        }
        
        url = hostUrl + searchUrl;
        
        log.info("GETTING CONTENT FROM: " + url);
        return url;
    }

    private String searchAlleleCore(String pipeline, String searchUrl) throws IOException,
            URISyntaxException {
        
        String hostUrl;
        String url;        
        
        if (pipeline.equals("cre")) {
            hostUrl = IMITS_SOLR_CORE_URL + "/solr/eucommtoolscre_allele2";
        }
        else {
            hostUrl = IMITS_SOLR_CORE_URL + "/solr/allele2";
        }
        
        url = hostUrl + searchUrl;
        
        log.info("GETTING CONTENT FROM: " + url);
        return url;
    }
    
    private JSONObject getResults(String url) throws IOException,
            URISyntaxException {

        log.info("GETTING CONTENT FROM: " + url);

        HttpProxy proxy = new HttpProxy();
        String content = proxy.getContent(new URL(url));

        return (JSONObject) JSONSerializer.toJSON(content);
    } 
    
    
 // GET ALLELE OBJECT DATA
    private Map<String, Object> getGeneData(JSONObject geneDoc) {
        
        HashMap<String, Object> gene = new HashMap<>();

        gene.put("marker_symbol" , geneDoc.get("marker_symbol"));
        gene.put("mgi_accession_id" , geneDoc.get("mgi_accession_id"));

//          if (geneDoc.has("genetic_map_links")){
//            gene.put("mgi_genetic_map" , getKeyValuePairFromArray("mgi", geneDoc.getJSONArray("genetic_map_links")));
//          }
        
        if (geneDoc.has("sequence_map_links")){
            gene.put("vega_sequence_map" , getKeyValuePairFromArray("vega", geneDoc.getJSONArray("sequence_map_links")));
            gene.put("ensembl_sequence_map" , getKeyValuePairFromArray("ensembl", geneDoc.getJSONArray("sequence_map_links")));
            gene.put("ucsc_sequence_map" , getKeyValuePairFromArray("ucsc", geneDoc.getJSONArray("sequence_map_links")));
            gene.put("ncbi_sequence_map" , getKeyValuePairFromArray("ncbi", geneDoc.getJSONArray("sequence_map_links")));
        }
        
        gene.put("es_cell_status" , geneDoc.get("latest_es_cell_status"));
        gene.put("mouse_status" , geneDoc.get("latest_mouse_status"));
        gene.put("phenotyping_status" , geneDoc.get("latest_phenotype_status"));

        return gene;
    }  
    
// GET ALLELE OBJECT DATA
    private Map<String, Object> getAlleleData(JSONObject alleleDoc) {
        
        HashMap<String, Object> allele = new HashMap<>();

        allele.put("marker_symbol" , alleleDoc.get("marker_symbol"));
        allele.put("mgi_accession_id" , alleleDoc.get("mgi_accession_id"));
        allele.put("allele_name" , alleleDoc.get("allele_name"));
        allele.put("allele_type" , alleleDoc.get("allele_type"));
        allele.put("allele_description" , alleleDoc.get("allele_description"));
        allele.put("genbank_file" , alleleDoc.get("genbank_file"));
        allele.put("allele_image" , alleleDoc.get("allele_image"));        
        allele.put("allele_simple_image" , alleleDoc.get("allele_simple_image")); 
        allele.put("cassette" , alleleDoc.get("cassette"));
        allele.put("design_id" , alleleDoc.get("design_id"));
        allele.put("es_cell_status" , alleleDoc.get("es_cell_status"));
        allele.put("targeting_vector_status" , "No Targeting Vector Production");
        allele.put("mouse_status" , alleleDoc.get("mouse_status"));
        allele.put("phenotyping_status" , alleleDoc.get("phenotyping_status"));
        allele.put("ikmc_project" , "");
        
        if (alleleDoc.has("es_cell_status") && ! alleleDoc.get("es_cell_status").equals("No ES Cell Production") && ! alleleDoc.get("es_cell_status").equals("")) {
            allele.put("targeting_vector_status" , "Targeting Vector Confirmed");
        }
        
        if (alleleDoc.has("ikmc_project")){
          allele.put("ikmc_project" , alleleDoc.getJSONArray("ikmc_project").get(0));
        }
        allele.put("pipeline" , "");
        if (alleleDoc.has("pipeline")){
          allele.put("pipeline" , alleleDoc.getJSONArray("pipeline").get(0));
        }
        if (alleleDoc.has("links")){
          allele.put("mutagenesis_url" , getKeyValuePairFromArray("mutagenesis_url", alleleDoc.getJSONArray("links")));
          allele.put("southern_tools" , getKeyValuePairFromArray("southern_tools", alleleDoc.getJSONArray("links")));
          allele.put("lrpcr_genotype_primers" , getKeyValuePairFromArray("lrpcr_genotype_primers", alleleDoc.getJSONArray("links")));
          allele.put("genotype_primers" , getKeyValuePairFromArray("genotyping_primers", alleleDoc.getJSONArray("links")));
          allele.put("loa_link_id" , getKeyValuePairFromArray("loa_link_id", alleleDoc.getJSONArray("links")));
        }
        
        return allele;
    }
    
   private Map<String, Object> getMouseData(JSONObject jsonObject2) throws IOException, URISyntaxException {
        String type = jsonObject2.getString("type");

        if (!type.equals("mouse")) {
            return null;
        }

        HashMap<String, Object> map2 = new HashMap<>();
        
        String background_colony_strain = "";
        
        if (jsonObject2.has("genetic_info")){
          background_colony_strain = getKeyValuePairFromArray("background_colony_strain", jsonObject2.getJSONArray("genetic_info"));
        }
 //       String production_graph = getKeyValuePairFromArray("production_graph", jsonObject2.getJSONArray("other_links"));
        
        map2.put("type", type);
        map2.put("product", "Mouse");
        map2.put("marker_symbol", jsonObject2.getString("marker_symbol"));
        map2.put("mgi_accession_id", jsonObject2.getString("mgi_accession_id"));
        map2.put("mgi_allele_name", getTitle(jsonObject2.getString("marker_symbol"), jsonObject2.getString("allele_name")));
        map2.put("allele_name", jsonObject2.getString("allele_name"));
        map2.put("allele_type", jsonObject2.getString("allele_type"));
        map2.put("colony_name", jsonObject2.getString("name"));
        map2.put("genetic_background", background_colony_strain);
        map2.put("production_centre", jsonObject2.getString("production_centre"));
        map2.put("_status", jsonObject2.getString("status"));
        map2.put("production_completed", jsonObject2.getString("production_completed"));
        map2.put("production_graph", "");
        map2.put("orders", getOrderInfo(jsonObject2));
        map2.put("contacts", getContactInfo(jsonObject2));
        map2.put("qc_about", "http://www.knockoutmouse.org/kb/entry/90/");
        map2.put("product_url", "alleles/" + jsonObject2.getString("mgi_accession_id") + "/" + jsonObject2.getString("allele_name") + "/");
        
        if(jsonObject2.containsKey("name") && jsonObject2.getString("name").length() > 0) {
            map2.put("qc_data_url", "alleles/qc_data/mouse/" + jsonObject2.getString("name") + "/");
        }

        String associated_product_colony_name;
        map2.put("associated_product_colony_name", "");
        if (jsonObject2.has("associated_product_colony_name")) {
            associated_product_colony_name = jsonObject2.getString("associated_product_colony_name");
            map2.put("associated_product_colony_name", associated_product_colony_name);
        }

        String es_cell = NOT_FOUND;
        map2.put("associated_product_es_cell_name", "");
        if (jsonObject2.has("associated_product_es_cell_name")) {
            es_cell = jsonObject2.getString("associated_product_es_cell_name");
            map2.put("associated_product_es_cell_name", es_cell);
        }

        map2.put("associated_product_vector_name", "");
        if (es_cell.equals(NOT_FOUND) && jsonObject2.has("associated_product_vector_name")) {
            map2.put("associated_product_vector_name", jsonObject2.getString("associated_product_vector_name"));
        }

        map2.put("allele_has_issue", "false");
        if (jsonObject2.has("allele_has_issues")) {
            map2.put("allele_has_issue", jsonObject2.getString("allele_has_issues"));
        }
        
        return map2;
    }


    private Map<String, Object> getEsCellData(JSONObject jsonObject2) throws IOException, URISyntaxException {
        String type = jsonObject2.getString("type");

        if (!type.equals("es_cell")) {
            return null;
        }

        HashMap<String, Object> map2 = new HashMap<>();

        map2.put("type", type);
        map2.put("product", "ES Cell");
        map2.put("marker_symbol", jsonObject2.getString("marker_symbol"));
        map2.put("mgi_accession_id", jsonObject2.getString("mgi_accession_id"));
        map2.put("mgi_allele_name", getTitle(jsonObject2.getString("marker_symbol"), jsonObject2.getString("allele_name")));
        map2.put("allele_name", jsonObject2.getString("allele_name"));
        map2.put("allele_type", jsonObject2.getString("allele_type"));
        map2.put("es_cell_clone", jsonObject2.getString("name"));
        map2.put("associated_product_vector_name", jsonObject2.getString("associated_product_vector_name"));        
        map2.put("production_completed", jsonObject2.getString("production_completed"));
        map2.put("orders", getOrderInfo(jsonObject2));
        map2.put("product_url", "alleles/" + jsonObject2.getString("mgi_accession_id") + "/" + jsonObject2.getString("allele_name") + "/");
        map2.put("allele_has_issue", "false");
        map2.put("qc_about", "http://www.knockoutmouse.org/kb/entry/78/");
        
        if(jsonObject2.containsKey("name") && jsonObject2.getString("name").length() > 0) {
            map2.put("qc_data_url", "alleles/qc_data/es_cell/" + jsonObject2.getString("name") + "/");
        }

        
        String parental_cell_line;
        String strain;
        String cassette;

        if (jsonObject2.has("genetic_info")) {
            parental_cell_line = getKeyValuePairFromArray("parent_es_cell_line", jsonObject2.getJSONArray("genetic_info"));
            strain = getKeyValuePairFromArray("strain", jsonObject2.getJSONArray("genetic_info"));
            cassette = getKeyValuePairFromArray("cassette", jsonObject2.getJSONArray("genetic_info"));
        
            if (parental_cell_line != null) {
                map2.put("parental_cell_line", parental_cell_line);
            }

            if (strain != null) {
                map2.put("es_cell_strain", strain);
                map2.put("genetic_background", strain);
            }
        
            map2.put("cassette", cassette);
        
        } 

        if (jsonObject2.has("allele_has_issues")) {
            map2.put("allele_has_issue", jsonObject2.getString("allele_has_issues"));
        }
        
        if (jsonObject2.has("ikmc_project_id")) {
            map2.put("ikmc_project_id", jsonObject2.getString("ikmc_project_id"));
        }

        return map2;
    }


    private Map<String, Object> getTargetingVectorData(JSONObject jsonObject2) throws IOException, URISyntaxException {
        if (!jsonObject2.getString("type").equals("targeting_vector")) {
            return null;
        }

        HashMap<String, Object> map2 = new HashMap<>();
        
        map2.put("type", jsonObject2.getString("type"));
        map2.put("product", "Targeting Vector");
        map2.put("marker_symbol", jsonObject2.getString("marker_symbol"));
        map2.put("mgi_accession_id", jsonObject2.getString("mgi_accession_id"));
        map2.put("allele_type", jsonObject2.getString("allele_type"));
        map2.put("allele_name", jsonObject2.getString("allele_name"));
        
        map2.put("targeting_vector", jsonObject2.getString("name"));
        map2.put("production_completed", jsonObject2.getString("production_completed"));
        
        map2.put("orders", getOrderInfo(jsonObject2));

        map2.put("design_id", jsonObject2.getString("design_id"));

        
        
        String cassette;
        String backbone;
        
        if (jsonObject2.has("genetic_info")) {
            cassette = getKeyValuePairFromArray("cassette", jsonObject2.getJSONArray("genetic_info"));
            backbone = getKeyValuePairFromArray("backbone", jsonObject2.getJSONArray("genetic_info"));

            map2.put("cassette", cassette);
            map2.put("backbone", backbone);
            
            map2.put("product_url", "alleles/" + jsonObject2.getString("mgi_accession_id") + "/" + cassette + "/" + jsonObject2.getString("design_id") + "/");
        }
        
        if (jsonObject2.has("other_links")) {

            String design_oligos_url = getKeyValuePairFromArray("design_link", jsonObject2.getJSONArray("other_links"));
            if (design_oligos_url != null) {
                map2.put("design_oligos_url", design_oligos_url);
            }

            String allele_image = getKeyValuePairFromArray("allele_image", jsonObject2.getJSONArray("other_links"));
            if (allele_image != null) {
                map2.put("allele_image", allele_image);
                map2.put("allele_simple_image", allele_image + "?simple=true");
            }
        
            String genbank_file = getKeyValuePairFromArray("genbank_file", jsonObject2.getJSONArray("other_links"));
            if (genbank_file != null) {
              map2.put("genbank_file", genbank_file);
            }
        }
        
        map2.put("allele_has_issue", "false");
        if (jsonObject2.has("allele_has_issues")) {
            map2.put("allele_has_issue", jsonObject2.getString("allele_has_issues"));
        }
        if (jsonObject2.has("ikmc_project_id")) {
            map2.put("ikmc_project_id", jsonObject2.getString("ikmc_project_id"));
        }
        
        return map2;
    }
   
    
    private Map<String, Object> getSummary(
            String accession,
            String allele_name,
            Map<String, Object> mapper
    ) throws IOException, URISyntaxException {

        List<Map<String, Object>> mice = (List<Map<String, Object>>)mapper.get("mice");
        Map<String, Object> allele = (Map<String, Object>)mapper.get("allele");
        Map<String, Object> gene = (Map<String, Object>)mapper.get("gene");
        Object loa_assays = mapper.get("loa_assays");
        
        HashMap<String, Object> summary = new HashMap<>();
        
        if (gene != null){
            summary.put("ensembl_url", gene.get("ensembl_sequence_map"));
        }
                    
        if (allele != null) {
            
            summary.put("marker_symbol", allele.get("marker_symbol"));
            summary.put("symbol", allele.get("marker_symbol") + "<sup>" + allele.get("allele_name") + "</sup>");
            summary.put("allele_name", allele.get("allele_name"));
            summary.put("allele_type", allele.get("allele_type"));
            summary.put("allele_description", allele.get("allele_description"));
            summary.put("southern_tool", allele.get("southern_tools"));
            summary.put("lrpcr_genotype_primers", allele.get("lrpcr_genotype_primers"));
            summary.put("genotype_primers", allele.get("genotype_primers"));
            summary.put("mutagenesis_url", allele.get("mutagenesis_url"));
            summary.put("map_image", allele.get("allele_simple_image"));
            summary.put("genbank", allele.get("genbank_file"));           

            summary.put("ikmc_project", allele.get("ikmc_project"));
            summary.put("pipeline", allele.get("pipeline"));
            summary.put("design_id", allele.get("design_id"));
            summary.put("statuses", getGeneProductInfoStatuses(mapper));    // TODO: FIX-ME!
            summary.put("status_mice", getGeneProductInfoStatusesMouseAlt(mapper));
            summary.put("loa_link_id", allele.get("loa_link_id"));

            summary.put("status_es_cells", getGeneProductInfoStatusesEsCellAlt(mapper));

//            summary.put("other_alleles_with_mice", getGeneProductInfoStatusesEsCellAlt(mapper));
//            summary.put("other_alleles_with_es_cells", getGeneProductInfoStatusesEsCellAlt(mapper));

        }

        if (summary.isEmpty()) {
            return null;
        }

        return summary;
    }
    
    
    private HashMap<String, HashMap<String, List<String>>> extractQcData(JSONArray docs, int i) {
        HashMap<String, HashMap<String, List<String>>> deep = new HashMap<>();

        JSONArray qcDataArray = docs.getJSONObject(i).getJSONArray("qc_data");
        for (int j = 0; j < qcDataArray.size(); j++) {
            String[] qc = qcDataArray.getString(j).split(":");

            String qc_group = qc != null && qc.length > 0 ? qc[0] : "";
            String qc_type = qc != null && qc.length > 1 ? qc[1] : "";
            String qc_result = qc != null && qc.length > 2 ? qc[2] : "";

            if (!deep.containsKey(qc_group)) {
                deep.put(qc_group, new HashMap<String, List<String>>());
                deep.get(qc_group).put("fieldNames", new ArrayList());
                deep.get(qc_group).put("values", new ArrayList());
            }
            deep.get(qc_group).get("fieldNames").add(qc_type.replace("_", " "));
            deep.get(qc_group).get("values").add(qc_result);
        }
        return deep;
    }
    
    
    private String getTitle(String marker_symbol, String allele_name) {
        if (marker_symbol == null || marker_symbol.length() < 1) {
            marker_symbol = "Unknown";
        }
        if (allele_name == null || allele_name.length() < 1) {
            allele_name = "";
        }
        else {
            allele_name = "<sup>" + allele_name + "</sup>";
        }

        String title = marker_symbol + allele_name;

        return title;
    }
    
    
    private String getKeyValuePairFromArray(String target_key, JSONArray object) {
        if (object == null) {
            return null;
        }

        for (Object o : object) {
            String s = (String) o;
            Pattern pattern = Pattern.compile(target_key + ":(.+)");
            Matcher matcher = pattern.matcher(s);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }
        return null;
    }


    private String getSuffix(String allele_name) {
        if (allele_name == null) {
            return null;
        }
        
        Pattern pattern = Pattern.compile("(^[a-zA-Z0-9.]*)");
        Matcher matcher = pattern.matcher(allele_name);
        if (matcher.find()) {
            return matcher.group(1);
        }
        
        return null;
    }
    
    private List<Map<String, Object>> getOrderInfo(JSONObject jsonObject2) throws IOException, URISyntaxException {
        List<Map<String, Object>> orders = new ArrayList<>();

        if (!jsonObject2.has("order_names") || !jsonObject2.has("order_links")) {
            return null;
        }

        if (!jsonObject2.has("production_completed") || (jsonObject2.has("production_completed") && jsonObject2.get("production_completed").toString().equals("false"))) {
            return null;
        }

//        log.info("#### getGeneProductInfoOrderInfo: jsonObject2: " + jsonObject2);

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

    private List<Map<String, Object>> getContactInfo(JSONObject jsonObject2) throws IOException, URISyntaxException {
        List<Map<String, Object>> contacts = new ArrayList<>();

        if (!jsonObject2.has("contact_names") || !jsonObject2.has("contact_links")) {
            return null;
        }

 //       log.info("#### getGeneProductInfoContactInfo: jsonObject2: " + jsonObject2);

        JSONArray contact_names = jsonObject2.getJSONArray("contact_names");
        JSONArray contact_links = jsonObject2.getJSONArray("contact_links");

        for (int k = 0; k < contact_names.size(); k++) {
            HashMap<String, Object> map3 = new HashMap<>();
            String name = contact_names.getString(k);
            String link = contact_links.getString(k);

            if(name == null || link == null || name.length() < 1 || link.length() < 1) {
                continue;
            }

            if (name.equals("NULL")) {
                name = "CONTACT";
            }
            map3.put("name", name);
            map3.put("url", link);

            contacts.add(map3);
        }
        return contacts;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    private Map<String, Object> getGeneProductInfoStatusesEsCellAlt(Map<String, Object> genes) {

        List<Map<String, Object>> es_cells = (List<Map<String, Object>>) genes.get("es_cells");

        Map<String, Object> foundMap = new HashMap<>();
        Map<String, Object> notFoundMap = new HashMap<>();

        for (Map<String, Object> es_cell : es_cells) {
            if (es_cell.get("production_completed").toString().equals("true")) {
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

        if (!foundMap.isEmpty()) {
            return foundMap;
        }

        if (!notFoundMap.isEmpty()) {
            return notFoundMap;
        }

        notFoundMap.put("TEXT", phraseMap.get("no-cells-prod"));
        notFoundMap.put("TEXT2", "");

        return notFoundMap;
    }
    
    
    private Map<String, Object> getGeneProductInfoStatusesMouseAlt(Map<String, Object> genes) {
        List<Map<String, Object>> mice = (List<Map<String, Object>>) genes.get("mice");
        Map<String, Object> foundMap = new HashMap<>();
        Map<String, Object> notFoundMap = new HashMap<>();

        for (Map<String, Object> mouse : mice) {
            if (mouse.get("production_completed").toString().equals("true")) {
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
    
// Completed Mouse production appears first
// The ES Cells that created the mice appear at the top of the list
    private Map<String, Object> sortAlleleProducts(Map<String, Object> genes) {
        List<Map<String, Object>> mice = (List<Map<String, Object>>) genes.get("mice");
        List<Map<String, Object>> es_cells = (List<Map<String, Object>>) genes.get("es_cells");
        List<Map<String, Object>> targeting_vectors = (List<Map<String, Object>>) genes.get("targeting_vectors");

        List<Map<String, Object>> mice_new = new ArrayList<>();
        List<Map<String, Object>> cells_new = new ArrayList<>();
        List<Map<String, Object>> vectors_new = new ArrayList<>();

        if (mice != null) {
            Iterator<Map<String, Object>> i0 = mice.iterator();
            while (i0.hasNext()) {
                Map<String, Object> mouse = i0.next(); // must be called before you can call i.remove()
                if (mouse.get("production_completed").toString().equals("true")) {
                    mice_new.add(mouse);
                    i0.remove();
                }
            }
            if (mice.size() > 0) {
                mice_new.addAll(mice);
            } 
        } 
        
        if (mice_new.size() > 0 && es_cells != null) {

     //       log.info("#### do cells");

            for (Map<String, Object> mouse : mice_new) {
      //          log.info("#### mouse: " + mouse.toString());

                Iterator<Map<String, Object>> i1 = es_cells.iterator();
                while (i1.hasNext()) {
                    Map<String, Object> es_cell = i1.next();

                    if (mouse.containsKey("associated_product_es_cell_name")) {

       //                 log.info("#### associated_product_es_cell_name: " + mouse.get("associated_product_es_cell_name"));
       //                 log.info("#### name: " + es_cell.get("name"));

                        if (mouse.get("associated_product_es_cell_name").equals(es_cell.get("name"))) {
                            cells_new.add(es_cell);
                            i1.remove();
                        }
                    }
                }
            }            
        }
        if (es_cells != null && es_cells.size() > 0) {
            cells_new.addAll(es_cells);
        }

        if (cells_new.size() > 0 && targeting_vectors != null) {

     //       log.info("#### do cells");

            for (Map<String, Object> es_cell : cells_new) {
      //          log.info("#### mouse: " + mouse.toString());

                Iterator<Map<String, Object>> i2 = targeting_vectors.iterator();
                while (i2.hasNext()) {
                    Map<String, Object> targeting_vector = i2.next();

                    if (es_cell.containsKey("associated_product_targeting_vector_name")) {

                        if (es_cell.get("associated_product_targeting_vector_name").equals(targeting_vector.get("name"))) {
                            vectors_new.add(targeting_vector);
                            i2.remove();
                        }
                    }
                }
            }       
        }
        if(targeting_vectors != null && targeting_vectors.size() > 0) {
            vectors_new.addAll(targeting_vectors);
        } 
         
        genes.put("mice", mice_new);
        genes.put("es_cells", cells_new);     
        genes.put("targeting_vectors", vectors_new);        
        
        return genes;
    }

    private List<HashMap<String, String>> getGeneProductInfoStatuses(Map<String, Object> genes) {
        List<HashMap<String, String>> list1 = getGeneProductInfoStatusesMouse(genes, true);
        List<HashMap<String, String>> list2 = getGeneProductInfoStatusesEsCell(genes, true);

        list1.addAll(list2);
        return list1;
    }



    private List<HashMap<String, String>> getGeneProductInfoStatusesMouse(Map<String, Object> genes, boolean restrict) {
        List<Map<String, Object>> mice = (List<Map<String, Object>>) genes.get("mice");
        List<HashMap<String, String>> listFound = new ArrayList<>();
        List<HashMap<String, String>> listNotFound = new ArrayList<>();

        for (Map<String, Object> mouse : mice) {
            if (mouse.get("production_completed").toString().equals("true")) {
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

  //      log.info("#### getGeneProductInfoStatusesMouse: listFound: " + listFound);
  //      log.info("#### getGeneProductInfoStatusesMouse: listNotFound: " + listNotFound);

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
            if (es_cell.get("production_completed").toString().equals("true")) {
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
        map2.put("TEXT", phraseMap.get("no-cells-prod"));
        map2.put("TEXT2", "");
        listNotFound.add(map2);
        return listNotFound;
    }
    
    private List<Map<String, Object>> filterGeneProductInfoList(List<Map<String, Object>> mice,
        List<Map<String, Object>> es_cells,
        List<Map<String, Object>> targeting_vectors) throws IOException, URISyntaxException, Exception {

        List<Map<String, Object>> array = new ArrayList<>();
        Map<String, Boolean> mice_allele_names = new HashMap<>();
        CassetteAlleleTypeManager cat_mgr = new CassetteAlleleTypeManager(es_cells);
        Map<String, Boolean> targeting_vector_names = new HashMap<>();

        mice = filterGeneProductInfoAE(filterGeneProductInfoGeneric(mice));
        
        
        for(Map<String, Object> mouse : mice) {
            mice_allele_names.put((String)mouse.get("allele_name"), true);
            array.add(mouse);
        }

        log.info("#### filterGeneProductInfo2ListNew: mice_allele_names: " + mice_allele_names);

        es_cells = filterGeneProductInfoAE(filterGeneProductInfoGeneric(es_cells));
        
        for(Map<String, Object> es_cell : es_cells) {
            if(!mice_allele_names.containsKey((String)es_cell.get("allele_name"))) {
                array.add(es_cell);
            }
            targeting_vector_names.put((String)es_cell.get("targeting_vector"), true);
        }

        for(Map<String, Object> targeting_vector : targeting_vectors) {
            //if( ! cat_mgr.has(targeting_vector) && ! targeting_vector_names.containsKey((String)targeting_vector.get("name")) ) {
            //if( ! targeting_vector_names.containsKey((String)targeting_vector.get("name")) ) {
            if( ! cat_mgr.has(targeting_vector) ) {
                cat_mgr.put(targeting_vector);
                array.add(targeting_vector);
            }
        }

 //       log.info("#### filterGeneProductInfoListNew: count: " + array.size());

        return array;
    }

    
    // if there's a a & e only show a's
    private List<Map<String, Object>> filterGeneProductInfoAE(List<Map<String, Object>> list) {
        List<Map<String, Object>> as = new ArrayList<>();
        List<Map<String, Object>> es = new ArrayList<>();
        List<Map<String, Object>> others = new ArrayList<>();
        for(Map<String, Object> item : list) {
            String allele_type = (String)item.get("allele_type");
            switch (allele_type) {
                case "a":
                    as.add(item);
                    break;
                case "e":
                    es.add(item);
                    break;
                default:
                    others.add(item);
                    break;
            }
        }
        if(!as.isEmpty() && !es.isEmpty()) {
            others.addAll(as);
            return others;
        }
        others.addAll(as);
        others.addAll(es);
        return others;
    }
    
 
    
    // routine to take orders & contacts and group them based on allele_name
    private List<Map<String, Object>> filterGeneProductInfoGeneric(List<Map<String, Object>> list) {
        Map<String, Map<String, Object>> map = new HashMap<>();
        OrderContactManager order_mgr = new OrderContactManager("orders");
        OrderContactManager contact_mgr = new OrderContactManager("contacts");

        for(Map<String, Object> item : list) {
            String allele_name = (String)item.get("allele_name");

            if(!map.containsKey(allele_name)) {
                map.put(allele_name, item);
            }

            if(item.containsKey("orders")) {
                order_mgr.put(item);
            }
            if(item.containsKey("contacts")) {
                contact_mgr.put(item);
            }
        }

        List<Map<String, Object>> remainders = new ArrayList<>();

        for(String key : map.keySet()) {
            Map<String, Object> m = map.get(key);
            if(order_mgr.getArray(key) != null) {
                m.put("orders", order_mgr.getArray(key));
            }
            if(contact_mgr.getArray(key) != null) {
                m.put("contacts", contact_mgr.getArray(key));
            }
            remainders.add(m);
        }

        return remainders;
    }  
    
    
    class OrderContactManager {
        private Map<String, Set<Map<String, String>>> _map;
        private String _key = "";

        public OrderContactManager(String key) {
            _map = new HashMap<>();
            _key = key;
        }

        private void put(Map<String, Object> map) {
            if(!_map.containsKey((String)map.get("allele_name"))) {
                _map.put((String)map.get("allele_name"), new HashSet<Map<String, String>>());
            }

            Set<Map<String, String>> om = _map.get((String)map.get("allele_name"));

            List<Map<String, Object>> list = (List<Map<String, Object>>)map.get(_key);

            if(list == null) {
                return;
            }

            for(Map<String, Object> item : list) {
                Map<String, String> m = new HashMap<>();
                m.put("name", (String)item.get("name"));
                m.put("url", (String)item.get("url"));
                om.add(m);
            }
        }

        public List<Map<String, String>> getArray(String key) {
            if(_map.containsKey(key)) {
                Set<Map<String, String>> set = _map.get(key);
                List<Map<String, String>> os = new ArrayList(set);
                return os;
            }
            return null;
        }
    }


    class CassetteAlleleTypeManager {
        private Map<String, Boolean> _map;      // = new HashMap<>();
        @SuppressWarnings("FieldMayBeFinal")
        private Logger log = Logger.getLogger(this.getClass().getCanonicalName());

        private CassetteAlleleTypeManager() {
        }

        public CassetteAlleleTypeManager(List<Map<String, Object>> list) {
            _map = new HashMap<>();
            put(list);
        }

        private void put(List<Map<String, Object>> list) {
            for(Map<String, Object> item : list) {
                put(item);
            }
        }

        public void put(Map<String, Object> es_cell) {
            String cassette = (String)es_cell.get("cassette");
            String allele_type = (String)es_cell.get("allele_type");
            _map.put(cassette + allele_type, true);
        }
        public boolean has(Map<String, Object> tv) {
            String cassette = (String)tv.get("cassette");
            String allele_type = (String)tv.get("allele_type");
            boolean b = _map.containsKey(cassette + allele_type);
            return b;
        }
    }  
    
}
