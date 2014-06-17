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

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
//import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.yaml.snakeyaml.Yaml;
import uk.ac.ebi.generic.util.SolrIndex;

@Controller
public class AllelesController {

    private final Logger log = LoggerFactory.getLogger(AllelesController.class);

    @Autowired
    SolrIndex solrIndex;
   
    //@RequestMapping("/alleles/{acc}/{symbol}")
    @RequestMapping("/alleles/{acc}")
    public String alleles(
            @PathVariable String acc,
            //@PathVariable String symbol,
            Model model,
            HttpServletRequest request,
            RedirectAttributes attributes) throws KeyManagementException, NoSuchAlgorithmException, URISyntaxException, IOException {

        log.info("#### AllelesController::alleles");
        
        Map<String, Object> constructs = solrIndex.getGeneProductInfo(acc);
        model.addAttribute("mice", constructs.get("mice"));
        model.addAttribute("es_cells", constructs.get("es_cells"));
        model.addAttribute("targeting_vectors", constructs.get("targeting_vectors"));

        Yaml yaml = new Yaml();
        String content = FileUtils.readFileToString(new File("/var/tmp/yaml_test.yml"));
        Map<String, Object> list = (Map<String, Object>) yaml.load(content);
        
        Map<String, Object> list2 = (Map<String, Object>)list.get(acc);

        model.addAttribute("allele_description", list2.get("allele_description"));

        model.addAttribute("symbol", list2.get("symbol"));

        model.addAttribute("type", list2.get("type"));

        model.addAttribute("statuses", (List<Map<String, String>>)list2.get("statuses"));

        model.addAttribute("genbank", list2.get("genbank"));

        model.addAttribute("mutagenesis_url", list2.get("mutagenesis_url"));

        model.addAttribute("map_image", list2.get("map_image"));

        model.addAttribute("browsers", (List<Map<String, String>>)list2.get("browsers"));        
        
        model.addAttribute("mice_in_progress", (List<Map<String, String>>)list2.get("mice_in_progress"));        

        model.addAttribute("tools", (List<Map<String, String>>)list2.get("tools"));

        return "alleles";
    }    
    
}
