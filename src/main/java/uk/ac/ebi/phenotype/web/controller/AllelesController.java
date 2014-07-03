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
package uk.ac.ebi.phenotype.web.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.ac.ebi.generic.util.SolrIndex2;

@Controller
public class AllelesController {

    private final Logger log = LoggerFactory.getLogger(AllelesController.class);

    @Autowired
    SolrIndex2 solrIndex;
    
    private HashMap<String, String> makeItem(String marker_symbol, String allele_name, String mgi_accession_id) {
        HashMap<String, String> map = new HashMap<>();        
        map.put("marker_symbol", marker_symbol);
        map.put("allele_name", allele_name);
        map.put("allele_name_e", URLEncoder.encode(allele_name));
        map.put("mgi_accession_id", mgi_accession_id);
        map.put("solr_product", "http://ikmc.vm.bytemark.co.uk:8985/solr/product/select?indent=on&version=2.2&q=" +
                "marker_symbol:" + marker_symbol +
                "&fq=&start=0&rows=10&fl=*%2Cscore&wt=json&explainOther=&hl.fl=");

        log.info("#### makeItem...");

        HashMap<String, String> params1 = new HashMap<>();
        params1.put("allele_name", allele_name);
        params1.put("marker_symbol", marker_symbol);
        params1.put("type", "mouse");
        map.put("solr_product_mouse", solrIndex.getGeneProductCoreUrl2(params1));
        
        params1 = new HashMap<>();
        params1.put("allele_name", allele_name);
        params1.put("marker_symbol", marker_symbol);
        params1.put("type", "es_cell");
        map.put("solr_product_es_cell", solrIndex.getGeneProductCoreUrl2(params1));
        
        params1 = new HashMap<>();
        params1.put("allele_name", allele_name);
        params1.put("marker_symbol", marker_symbol);
        params1.put("type", "targeting_vector");
        map.put("solr_product_targeting_vector", solrIndex.getGeneProductCoreUrl2(params1));
        
        String url = "http://ikmc.vm.bytemark.co.uk:8985/solr/allele2/select?indent=on&version=2.2&q=" +
                "TEMPLATE" +
                "&fq=&start=0&rows=10&fl=*%2Cscore&wt=json&explainOther=&hl.fl=&indent=on";
        
        map.put("solr_allele2", url.replace("TEMPLATE", "marker_symbol:" + marker_symbol));
        map.put("solr_allele2_alleles", url.replace("TEMPLATE", "marker_symbol:" + marker_symbol +  " type:allele"));
        map.put("solr_allele2_genes", url.replace("TEMPLATE", "marker_symbol:" + marker_symbol +  " type:gene"));

        log.info("#### makeItem: map: " + map.toString());

        return map;
    }

    @RequestMapping("/qc_data/{alleleType}/{type}/{name}")
    public String qcData(
            @PathVariable String alleleType,
            @PathVariable String type,
            @PathVariable String name,
            Model model,
            HttpServletRequest request,
            RedirectAttributes attributes) throws KeyManagementException, NoSuchAlgorithmException, URISyntaxException, IOException, Exception {

            HashMap<String, HashMap<String, List<String>>> constructs = solrIndex.getAlleleQcInfo(alleleType, type, name);
            model.addAttribute("qcData", constructs);
            return "qcData";
    }
    
    @RequestMapping("/alleles")
    public String alleles0(
            Model model,
            HttpServletRequest request,
            RedirectAttributes attributes) throws KeyManagementException, NoSuchAlgorithmException, URISyntaxException, IOException, Exception {

        log.info("#### alleles0...");
        
        List<String> targetList = new ArrayList<>();
        targetList.add("marker_symbol:Foxj3");
        targetList.add("marker_symbol:Cib2");
        targetList.add("marker_symbol:Nxn");
        targetList.add("marker_symbol:Morn1");
        targetList.add("marker_symbol:Cbx1");
        targetList.add("marker_symbol:Zfp111");
        targetList.add("marker_symbol:Arhgef6");        
        targetList.add("marker_symbol:Heyl");        

        String qs = StringUtils.join(targetList, " OR ");

        HashMap<String, String> params1 = new HashMap<>();
        
        String qs2 = URLEncoder.encode("(" + qs + ")" + " AND type:mouse");
        
        params1.put("multi", qs2);        
        
        List<Map<String, Object>> list1 = solrIndex.getProductGeneDetails(params1);
        List<Map<String, String>> list = new ArrayList<>();
        
        for(Map<String, Object> item : list1) {        
            list.add(makeItem((String)item.get("marker_symbol"), (String)item.get("allele_name"), (String)item.get("mgi_accession_id")));  
        }
        
        model.addAttribute("list", list);

        return "alleles_list";
    }

    @RequestMapping("/alleles/{acc}")
    public String alleles1(
            @PathVariable String acc,
            Model model,
            HttpServletRequest request,
            RedirectAttributes attributes) throws KeyManagementException, NoSuchAlgorithmException, URISyntaxException, IOException, Exception {

        log.info("#### alleles1...");
        
        if(acc.length() == 1) {
            HashMap<String, String> params1 = new HashMap<>();

            params1.put("allele_type", acc);        
            params1.put("type", "mouse");

            List<Map<String, Object>> list1 = solrIndex.getProductGeneDetails(params1);
            List<Map<String, String>> list = new ArrayList<>();
            
            if(list1 != null && ! list1.isEmpty()) {
                list1 = list1.subList(0, 50);

                for(Map<String, Object> item : list1) {        
                    list.add(makeItem((String)item.get("marker_symbol"), (String)item.get("allele_name"), (String)item.get("mgi_accession_id")));  
                }

                model.addAttribute("list", list);
            }
            
            return "alleles_list";
        }
        
        List<Map<String, Object>> list1 = solrIndex.getProductGeneDetails(acc);
        List<Map<String, String>> list = new ArrayList<>();
        
        if(list1 != null) {
            for(Map<String, Object> item : list1) {        
                list.add(makeItem((String)item.get("marker_symbol"), (String)item.get("allele_name"), (String)item.get("mgi_accession_id")));  
            }
        }
        
        model.addAttribute("list", list);

        return "alleles_list";
    }
    
    @RequestMapping("/alleles/{acc}/{allele_name}")
    public String alleles2(
            @PathVariable String acc,
            @PathVariable String allele_name,
            Model model,
            HttpServletRequest request,
            RedirectAttributes attributes) throws KeyManagementException, NoSuchAlgorithmException, URISyntaxException, IOException, Exception {
        
        if(allele_name.equals("mutagenesis_url")) {
            model.addAttribute("message", "Mutagenesis not yet implemented!");
            log.info("#### alleles2: mutagenesis_url");
            return "alleles_list";
        }

        if(allele_name.equals("lrpcr_genotyping_primers")) {
            model.addAttribute("message", "lrpcr genotyping primers not yet implemented!");
            log.info("#### alleles2: lrpcr_genotyping_primers");
            return "alleles_list";
        }

        return allelesCommon(acc, allele_name, model, request, attributes, false);
    }
    
    @RequestMapping("/alleles/{acc}/{allele_name}/{debug}")
    public String alleles3(
            @PathVariable String acc,
            @PathVariable String allele_name,
            @PathVariable String debug,
            Model model,
            HttpServletRequest request,
            RedirectAttributes attributes) throws KeyManagementException, NoSuchAlgorithmException, URISyntaxException, IOException, Exception {
        return allelesCommon(acc, allele_name, model, request, attributes, debug.equals("debug"));
    }
    
    public String allelesCommon(
            String acc,
            String allele_name,
            Model model,
            HttpServletRequest request,
            RedirectAttributes attributes,
            boolean debug) throws KeyManagementException, NoSuchAlgorithmException, URISyntaxException, IOException, Exception {

        log.info("#### AllelesController::alleles");
        log.info("#### acc: " + acc);
        log.info("#### allele_name: " + allele_name);
                
        Map<String, Object> constructs = solrIndex.getGeneProductInfo(acc, allele_name, debug);

        if(constructs == null) {
            return "alleles";
        }
        
        model.addAttribute("mice", constructs.get("mice"));
        model.addAttribute("es_cells", constructs.get("es_cells"));
        model.addAttribute("targeting_vectors", constructs.get("targeting_vectors"));
        model.addAttribute("summary", constructs.get("summary"));

        log.info("#### summary: " + constructs.get("summary"));

        return "alleles";
    }    
    
}
