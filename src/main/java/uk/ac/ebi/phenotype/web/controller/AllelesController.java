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
package uk.ac.ebi.phenotype.web.controller;

//import java.io.File;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.ac.ebi.generic.util.SolrIndex;

@Controller
public class AllelesController {

    private final Logger log = LoggerFactory.getLogger(AllelesController.class);

    @Autowired
    SolrIndex solrIndex;
    
    private HashMap<String, String> makeItem(String marker_symbol, String allele_name, String mgi_accession_id) {
        HashMap<String, String> map = new HashMap<>();        
        map.put("marker_symbol", marker_symbol);
        map.put("allele_name", allele_name);
        map.put("allele_name_e", URLEncoder.encode(allele_name));
        map.put("mgi_accession_id", mgi_accession_id);
        map.put("solr_product", "http://ikmc.vm.bytemark.co.uk:8985/solr/product/select?indent=on&version=2.2&q=" +
                "marker_symbol:" + marker_symbol +
                "&fq=&start=0&rows=10&fl=*%2Cscore&wt=json&explainOther=&hl.fl=");
        map.put("solr_allele2", "http://ikmc.vm.bytemark.co.uk:8985/solr/allele2/select?indent=on&version=2.2&q=" +
                "marker_symbol:" + marker_symbol +
                "&fq=&start=0&rows=10&fl=*%2Cscore&wt=json&explainOther=&hl.fl=");
        return map;
    }

    @RequestMapping("/alleles/")
    public String alleles1(
            Model model,
            HttpServletRequest request,
            RedirectAttributes attributes) throws KeyManagementException, NoSuchAlgorithmException, URISyntaxException, IOException, Exception {
        
        List<Map<String, String>> list = new ArrayList<>();
        
        list.add(makeItem("Foxj3", "tm1a(EUCOMM)Wtsi", "MGI:2443432"));        
        list.add(makeItem("Foxj3", "tm1b(EUCOMM)Wtsi", "MGI:2443432"));
        list.add(makeItem("Foxj3", "tm1e(EUCOMM)Wtsi", "MGI:2443432"));

        list.add(makeItem("Cib2", "tm1a(EUCOMM)Wtsi", "MGI:1929293"));
        list.add(makeItem("Cib2", "tm1e(EUCOMM)Wtsi", "MGI:1929293"));

        list.add(makeItem("Nxn", "tm1a", "MGI:109331"));
        list.add(makeItem("Nxn", "tm1a(EUCOMM)Wtsi", "MGI:109331"));

        model.addAttribute("list", list);

        return "alleles_list";
    }
    
    @RequestMapping("/alleles/{acc}")
    public String alleles1(
            @PathVariable String acc,
            Model model,
            HttpServletRequest request,
            RedirectAttributes attributes) throws KeyManagementException, NoSuchAlgorithmException, URISyntaxException, IOException, Exception {
        return allelesCommon(acc, null, model, request, attributes);
    }
    
    @RequestMapping("/alleles/{acc}/{allele_name}")
    public String alleles2(
            @PathVariable String acc,
            @PathVariable String allele_name,
            Model model,
            HttpServletRequest request,
            RedirectAttributes attributes) throws KeyManagementException, NoSuchAlgorithmException, URISyntaxException, IOException, Exception {
        return allelesCommon(acc, allele_name, model, request, attributes);
    }
    
    public String allelesCommon(
            String acc,
            String allele_name,
            Model model,
            HttpServletRequest request,
            RedirectAttributes attributes) throws KeyManagementException, NoSuchAlgorithmException, URISyntaxException, IOException, Exception {

        log.info("#### AllelesController::alleles");
        log.info("#### acc: " + acc);
        log.info("#### allele_name: " + allele_name);
                
        Map<String, Object> constructs = solrIndex.getGeneProductInfo(acc, allele_name);
      //  log.info("#### constructs: " + constructs.toString());

        if(constructs == null) {
            return "alleles";
        }
        
        model.addAttribute("mice", constructs.get("mice"));
        model.addAttribute("es_cells", constructs.get("es_cells"));
        model.addAttribute("targeting_vectors", constructs.get("targeting_vectors"));
        model.addAttribute("summary", constructs.get("summary"));

        log.info("#### summary: " + constructs.get("summary"));
        
//        Yaml yaml = new Yaml();
//        String content = FileUtils.readFileToString(new File("/var/tmp/yaml_test.yml"));
//        Map<String, Object> list = (Map<String, Object>) yaml.load(content);
//        
//        Map<String, Object> list2 = (Map<String, Object>)list.get(acc);

      //  model.addAttribute("allele_description", list2.get("allele_description"));

       // model.addAttribute("symbol", list2.get("symbol"));

       // model.addAttribute("type", list2.get("type"));

       // model.addAttribute("statuses", (List<Map<String, String>>)list2.get("statuses"));

       // model.addAttribute("genbank", list2.get("genbank"));

       // model.addAttribute("mutagenesis_url", list2.get("mutagenesis_url"));

       // model.addAttribute("map_image", list2.get("map_image"));

      //  model.addAttribute("browsers", (List<Map<String, String>>)list2.get("browsers"));        
        
      //  model.addAttribute("mice_in_progress", (List<Map<String, String>>)list2.get("mice_in_progress"));        

      //  model.addAttribute("tools", (List<Map<String, String>>)list2.get("tools"));

        return "alleles";
    }    
    
}
