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

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;
import java.util.ArrayList;

@Controller
public class AllelesController {

    private final Logger log = LoggerFactory.getLogger(AllelesController.class);

    public List<Map<String, String>> getStatuses() {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();

        Map<String, String> status_mice = new HashMap<String, String>();

        status_mice.put("TEXT", "There are mice for this allele");
        status_mice.put("ORDER", "http://www.sanger.ac.uk");
        status_mice.put("DETAILS", "http://www.sanger.ac.uk");

        list.add(status_mice);

        Map<String, String> status_cells = new HashMap<String, String>();

        status_cells.put("TEXT", "There are ES cells for this allele");
        status_cells.put("ORDER", "http://www.google.com");
        status_cells.put("DETAILS", "http://www.google.com");

        list.add(status_cells);

        return list;
    }

//    public Map<String, Map<String, String>> getStatuses() {
//        Map<String, String> status_mice = new HashMap<String, String>();
//
//        status_mice.put("order", "http://www.sanger.ac.uk");
//        status_mice.put("details", "http://www.sanger.ac.uk");
//
//        Map<String, Map<String, String>> statuses = new HashMap<String, Map<String, String>>();
//
//        statuses.put("mice", status_mice);
//
//        Map<String, String> status_cells = new HashMap<String, String>();
//
//        status_cells.put("order", "http://www.google.com");
//        status_cells.put("details", "http://www.google.com");
//
//        statuses.put("cells", status_cells);
//
//        return statuses;
//    }
    public List<Map<String, String>> getBrowsers() {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();

        Map<String, String> browsers = new HashMap<String, String>();

        browsers.put("browser", "Ensembl (mouse)");
        browsers.put("url", "http://www.ensembl.org/Mus_musculus/Location/View?r=9:54544794-54560…c.uk/das/ikmc_products=normal,contig=normal,ruler=normal,scalebar=normal");

        list.add(browsers);

        Map<String, String> browsers2 = new HashMap<String, String>();

        browsers2.put("browser", "UCSC (mouse)");
        browsers2.put("url", "http://genome.ucsc.edu/cgi-bin/hgTracks?db=mm10&ikmc=pack&ensGene=pack&position=chr9:54544794-54560218");

        list.add(browsers2);

        return list;
    }

    public List<Map<String, String>> getTools() {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();

        Map<String, String> items = new HashMap<String, String>();

        items.put("tool", "Southern Blot Tool");
        items.put("url", "http://www.sanger.ac.uk/htgt/htgt2/tools/restrictionenzymes?es_clone_name=EPD0337_2_D04&iframe=true&width=100%&height=100%");

        list.add(items);

        return list;
    }

    public List<Map<String, String>> getMice() {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();

        Map<String, String> items = new HashMap<String, String>();

        items.put("Genetic background", "C57BL/6NTac;C57BL/6NTac;C57BL/6N-Atm1Brd/a");
        items.put("Production Centre", "Harwell");
        items.put("ES Cell", "EPD0337_2_D04");
        items.put("QC Data / Southern tool", "http://www.sanger.ac.uk|http://www.sanger.ac.uk/htgt/htgt2/tools/restrictionenzymes?es_clone_name=EPD0337_2_D04&iframe=true&width=100%&height=100%");
        items.put("order", "http://www.emmanet.org/mutant_types.php?keyword=Cib2");

        list.add(items);

        return list;
    }

    @RequestMapping("/alleles/{acc}")
    public String alleles(
            @PathVariable String acc,
            Model model,
            HttpServletRequest request,
            RedirectAttributes attributes) throws KeyManagementException, NoSuchAlgorithmException, URISyntaxException, IOException {

        model.addAttribute("allele_description", "Knockout First, reporter-tagged deletion");

        model.addAttribute("symbol", "Cib2<sup>tm1b(EUCOMM)Wtsi</sup>");

        model.addAttribute("type", "Cre-excised Reporter-tagged deletion (tm1b)");

        model.addAttribute("statuses", getStatuses());

        model.addAttribute("genbank", "https://www.i-dcc.org/imits/targ_rep/alleles/11268/escell-clone-cre-genbank-file");

        model.addAttribute("mutagenesis_blurb", "mutagenesis blurb");

        model.addAttribute("map_image", "http://www.mousephenotype.org/imits/targ_rep/alleles/11268/allele-image");

        model.addAttribute("browsers", getBrowsers());

        model.addAttribute("tools", getTools());

        model.addAttribute("mice", getMice());

        return "alleles";
    }

}
