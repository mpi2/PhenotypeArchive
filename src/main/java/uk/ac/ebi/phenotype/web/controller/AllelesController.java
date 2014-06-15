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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.yaml.snakeyaml.Yaml;

@Controller
public class AllelesController {

    private final Logger log = LoggerFactory.getLogger(AllelesController.class);

    public List<Map<String, String>> getStatuses() {
        List<Map<String, String>> list = new ArrayList<>();

        Map<String, String> status_mice = new HashMap<>();

        status_mice.put("TEXT", "There are mice for this allele");
        status_mice.put("ORDER", "http://www.sanger.ac.uk");
        status_mice.put("DETAILS", "http://www.sanger.ac.uk");

        list.add(status_mice);

        Map<String, String> status_cells = new HashMap<>();

        status_cells.put("TEXT", "There are ES cells for this allele");
        status_cells.put("ORDER", "http://www.google.com");
        status_cells.put("DETAILS", "http://www.google.com");

        list.add(status_cells);

        return list;
    }

    public List<Map<String, String>> getBrowsers() {
        List<Map<String, String>> list = new ArrayList<>();

        Map<String, String> browsers = new HashMap<>();

        browsers.put("browser", "Ensembl (mouse)");
        browsers.put("url", "http://www.ensembl.org/Mus_musculus/Location/View?r=9:54544794-54560…c.uk/das/ikmc_products=normal,contig=normal,ruler=normal,scalebar=normal");

        list.add(browsers);

        Map<String, String> browsers2 = new HashMap<>();

        browsers2.put("browser", "UCSC (mouse)");
        browsers2.put("url", "http://genome.ucsc.edu/cgi-bin/hgTracks?db=mm10&ikmc=pack&ensGene=pack&position=chr9:54544794-54560218");

        list.add(browsers2);

        return list;
    }

    public List<Map<String, String>> getMice() {
        List<Map<String, String>> list = new ArrayList<>();

        Map<String, String> items = new HashMap<>();

        items.put("genetic_background", "C57BL/6NTac;C57BL/6NTac;C57BL/6N-Atm1Brd/a");
        items.put("production_centre", "Harwell");
        items.put("es_cell", "EPD0337_2_D04");
        items.put("southern_tool",
                "http://www.sanger.ac.uk|http://www.sanger.ac.uk/htgt/htgt2/tools/restrictionenzymes?es_clone_name=EPD0337_2_D04&iframe=true&width=100%&height=100%");
        items.put("order_url", "http://www.emmanet.org/mutant_types.php?keyword=Cib2");
        items.put("order_name", "EMMA");

        list.add(items);

        return list;
    }

    public List<Map<String, String>> getEsCells() {
        List<Map<String, String>> list = new ArrayList<>();

        Map<String, String> items = new HashMap<>();

        items.put("genetic_background", "C57BL/6NTac;C57BL/6NTac;C57BL/6N-Atm1Brd/a");
        items.put("es_cell_clone", "EPD0337_2_D04");
        items.put("targeting_vector", "PG00073_Z_5_H01");
        items.put("southern_tool",
                "http://www.sanger.ac.uk|http://www.sanger.ac.uk/htgt/htgt2/tools/restrictionenzymes?es_clone_name=EPD0337_2_D04&iframe=true&width=100%&height=100%");
        items.put("genotyping_primers", "C57BL/6NTac;C57BL/6NTac;C57BL/6N-Atm1Brd/a");
        items.put("order_url", "http://www.emmanet.org/mutant_types.php?keyword=Cib2");
        items.put("order_name", "EMMA");

        list.add(items);

        return list;
    }

    public List<Map<String, String>> getTargetingVectors() {
        List<Map<String, String>> list = new ArrayList<>();

        Map<String, String> items = new HashMap<>();

        items.put("design_oligos_url", "http://www.sanger.ac.uk/htgt/htgt2/design/designedit/refresh_design?design_id=48714");
        items.put("targeting_vector", "PG00073_Z_5_H01");
        items.put("cassette", "L1L2_Bact_P");
        items.put("backbone", "L3L4_pD223_DTA_spec");
        items.put("genbank_file", "http://www.sanger.ac.uk");
        items.put("order_url", "http://www.eummcr.org/order?add=MGI:1929293&material=vectors");
        items.put("order_name", "EUMMCR");

        list.add(items);

        return list;
    }
   
    @RequestMapping("/alleles/{acc}")
    public String alleles(
            @PathVariable String acc,
            Model model,
            HttpServletRequest request,
            RedirectAttributes attributes) throws KeyManagementException, NoSuchAlgorithmException, URISyntaxException, IOException {

        log.info("#### AllelesController::alleles");
        
       // String path = request.getContextPath();
       // log.error("#### path: " + path);
        
       log.error("#### Working Directory = " + System.getProperty("user.dir"));

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

        model.addAttribute("mice", (List<Map<String, String>>)list2.get("mice"));
        
        model.addAttribute("mice_in_progress", (List<Map<String, String>>)list2.get("mice_in_progress"));

        model.addAttribute("es_cells", (List<Map<String, String>>)list2.get("es_cells"));

        model.addAttribute("targeting_vectors", (List<Map<String, String>>)list2.get("targeting_vectors"));
        
        model.addAttribute("mice_blurb", list2.get("mice_blurb"));

        model.addAttribute("tools", (List<Map<String, String>>)list2.get("tools"));

//        Yaml yaml = new Yaml();
//        Map<String, Object> list = new HashMap<>();
//        list.put("MGI:1929293", model.asMap());
//        String output = yaml.dump(list);
//        FileUtils.writeStringToFile(new File("/var/tmp/yaml_test.yml"), output);

        return "alleles";
    }    
    
   // @RequestMapping("/alleles/{acc}")
    public String alleles_old(
            @PathVariable String acc,
            Model model,
            HttpServletRequest request,
            RedirectAttributes attributes) throws KeyManagementException, NoSuchAlgorithmException, URISyntaxException, IOException {

        log.info("#### AllelesController::alleles");

        model.addAttribute("allele_description", "Knockout First, reporter-tagged deletion");

        model.addAttribute("symbol", "Cib2<sup>tm1a(EUCOMM)Wtsi</sup>");

        model.addAttribute("type", "Cre-excised Reporter-tagged deletion (tm1b)");

        model.addAttribute("statuses", getStatuses());

        model.addAttribute("genbank", "https://www.i-dcc.org/imits/targ_rep/alleles/11268/escell-clone-cre-genbank-file");

        model.addAttribute("mutagenesis_url", "http://google.com");

        model.addAttribute("map_image", "http://www.mousephenotype.org/imits/targ_rep/alleles/11268/allele-image");

        model.addAttribute("browsers", getBrowsers());

        model.addAttribute("mice", getMice());

        model.addAttribute("es_cells", getEsCells());

        model.addAttribute("targeting_vectors", getTargetingVectors());

        model.addAttribute("mice_blurb", "Mice");

        Yaml yaml = new Yaml();

        Map<String, Object> list = new HashMap<>();
        list.put("MGI:1929293", model.asMap());
        String output = yaml.dump(list);
        FileUtils.writeStringToFile(new File("/var/tmp/yaml_test.yml"), output);

        return "alleles";
    }

}
