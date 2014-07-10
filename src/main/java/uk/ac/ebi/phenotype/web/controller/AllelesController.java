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
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
//import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
//import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
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
import uk.ac.ebi.phenotype.web.util.HttpProxy;

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
        map.put("solr_product", "http://ikmc.vm.bytemark.co.uk:8985/solr/product/select?indent=on&version=2.2&q="
                + "marker_symbol:" + marker_symbol
                + "&fq=&start=0&rows=10&fl=*%2Cscore&wt=json&explainOther=&hl.fl=");

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

        String url = "http://ikmc.vm.bytemark.co.uk:8985/solr/allele2/select?indent=on&version=2.2&q="
                + "TEMPLATE"
                + "&fq=&start=0&rows=10&fl=*%2Cscore&wt=json&explainOther=&hl.fl=&indent=on";

        map.put("solr_allele2", url.replace("TEMPLATE", "marker_symbol:" + marker_symbol));
        map.put("solr_allele2_alleles", url.replace("TEMPLATE", "marker_symbol:" + marker_symbol + " type:allele"));
        map.put("solr_allele2_genes", url.replace("TEMPLATE", "marker_symbol:" + marker_symbol + " type:gene"));

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

        for (Map<String, Object> item : list1) {
            list.add(makeItem((String) item.get("marker_symbol"), (String) item.get("allele_name"), (String) item.get("mgi_accession_id")));
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

        if (acc.length() == 1) {
            HashMap<String, String> params1 = new HashMap<>();

            params1.put("allele_type", acc);
            params1.put("type", "mouse");

            List<Map<String, Object>> list1 = solrIndex.getProductGeneDetails(params1);
            List<Map<String, String>> list = new ArrayList<>();

            if (list1 != null && !list1.isEmpty()) {
                list1 = list1.subList(0, 50);

                for (Map<String, Object> item : list1) {
                    list.add(makeItem((String) item.get("marker_symbol"), (String) item.get("allele_name"), (String) item.get("mgi_accession_id")));
                }

                model.addAttribute("list", list);
            }

            return "alleles_list";
        }

        List<Map<String, Object>> list1 = solrIndex.getProductGeneDetails(acc);
        List<Map<String, String>> list = new ArrayList<>();

        if (list1 != null) {
            for (Map<String, Object> item : list1) {
                list.add(makeItem((String) item.get("marker_symbol"), (String) item.get("allele_name"), (String) item.get("mgi_accession_id")));
            }
        }

        model.addAttribute("list", list);

        return "alleles_list";
    }

//    def calculate_mutagenesis_prediction_stats( transcripts )
//      MartSearch::Controller.instance().logger.debug("[MartSearch::ProjectUtils] ::calculate_mutagenesis_prediction_stats - running calculate_mutagenesis_prediction_stats()")
//
//      count = {
//        :wt_transcripts                 => 0,
//        :wt_non_coding_transcripts      => 0,
//        :wt_proteien_coding_transcripts => 0,
//        :mut_nmd_transcripts            => 0,
//        :mut_coding_transcripts         => 0,
//        :mut_nmd_rescue_transcripts     => 0
//      }
//
//      transcripts.each do |transcript|
//        count[:wt_transcripts] += 1
//        if transcript[:biotype].eql?('protein_coding')
//          count[:wt_proteien_coding_transcripts] += 1
//          count[:mut_nmd_transcripts]            += 1 if transcript[:floxed_transcript_description] =~ /^No protein product \(NMD\)/
//          count[:mut_coding_transcripts]         += 1 if transcript[:floxed_transcript_description] =~ /^No protein product \(NMD\)/ or transcript[:floxed_transcript_description] !~ /^No protein product^/
//          count[:mut_nmd_rescue_transcripts]     += 1 if transcript[:floxed_transcript_description] =~ /^Possible NMD rescue/
//        end
//      end
//
//      count[:wt_non_coding_transcripts] = count[:wt_transcripts] - count[:wt_proteien_coding_transcripts]
//
//      MartSearch::Controller.instance().logger.debug("[MartSearch::ProjectUtils] ::calculate_mutagenesis_prediction_stats - running calculate_mutagenesis_prediction_stats() - DONE")
//
//      return count
//    end
    private Map<String, Integer> getMutagenesisStats(JSONArray transcripts) {
        
        if(transcripts == null) {
            return null;
        }
        
        Map<String, Integer> map = new HashMap<>();
        map.put("wt_transcripts", 0);
        map.put("wt_non_coding_transcripts", 0);
        map.put("wt_proteien_coding_transcripts", 0);
        map.put("mut_nmd_transcripts", 0);
        map.put("mut_coding_transcripts", 0);
        map.put("mut_nmd_rescue_transcripts", 0);

        for (Object o : transcripts) {
            JSONObject o2 = (JSONObject) o;
            map.put("wt_proteien_coding_transcripts", map.get("wt_proteien_coding_transcripts") + 1);
            if (o2.has("biotype") && o2.getString("biotype").equals("protein_coding")) {
                map.put("wt_transcripts", map.get("wt_transcripts") + 1);

                if (o2.getString("floxed_transcript_description").matches("^No protein product \\(NMD\\)")) {
                    map.put("mut_nmd_transcripts", map.get("mut_nmd_transcripts") + 1);
                }

                if (o2.getString("floxed_transcript_description").matches("^No protein product \\(NMD\\)")
                        || o2.getString("floxed_transcript_description").matches("^No protein product")) {
                    map.put("mut_coding_transcripts", map.get("mut_coding_transcripts") + 1);
                }

                if (o2.getString("floxed_transcript_description").matches("^Possible NMD rescue")) {
                    map.put("mut_nmd_rescue_transcripts", map.get("mut_nmd_rescue_transcripts") + 1);
                }
            }
            map.put("wt_non_coding_transcripts", map.get("wt_transcripts") - map.get("wt_proteien_coding_transcripts"));
        }
        return map;
    }

//    tm1a: KO first allele (reporter-tagged insertion allele)
//    tm1b: Reporter-tagged deletion allele (post-Cre)
//    tm1c: Conditional allele (post-Flp)
//    tm1d: Deletion allele (post-Flp and Cre with no reporter)
//    tm1e: targeted, non-conditional allele
//    tm1: Reporter-tagged deletion allele (with selection cassette)
//    tm1.1: Reporter-tagged deletion allele (post Cre, with no selection cassette)
//    tm1.2: Reporter-tagged deletion allele (post Flp, with no reporter and selection cassette)
//
//      This gene has <%= counts[:wt_transcripts] %> wild type transcripts,
//      of which <%= counts[:wt_proteien_coding_transcripts] %> are protein-coding.
//      Following removal of the floxed region, <%= counts[:mut_coding_transcripts] %>
//      <% if counts[:mut_coding_transcripts] == 1 %>transcript is<% else %>transcripts are<% end %>
//      <strong>predicted</strong> to produce a truncated protein product of which
//      <%= counts[:mut_nmd_transcripts] %> may be subject to non-sense mediated decay (NMD).
//
//      <% if counts[:mut_nmd_rescue_transcripts] > 0 %>
//        <strong>NOTE:</strong> Of the <%= counts[:wt_non_coding_transcripts] %> non-coding wild type transcripts,
//        <%= counts[:mut_nmd_rescue_transcripts] %> are possibly subject to NMD rescue in the mutant.
//      <% end %>
//
//      <% if @data[:allele_design_type] == 'Deletion' %>
//        This mutation is of type '<%= @data[:allele_design_type] %>' (more information on IKMC alleles can be found
//        <a href="http://www.knockoutmouse.org/about/targeting-strategies">here</a>). The table below
//        shows the <strong>predicted</strong> structure of the gene transcripts.
//        Click the 'view' button for each transcript to see the full prediction for that transcript.
//      <% else %>
//        The original allele for this mutation is of type '<%= @data[:allele_design_type] %>'. The table below
//        shows the <strong>predicted</strong> structure of the gene transcripts after application of Flp and Cre
//        (forming a '<%= allele_type('tm1d') %>' allele - more information on IKMC alleles can be found
//        <a href="http://www.knockoutmouse.org/about/targeting-strategies">here</a>).
//        Click the 'view' button for each transcript to see the full prediction for that transcript.
//      <% end %>
//    tm1a: KO first allele (reporter-tagged insertion allele)
//    tm1b: Reporter-tagged deletion allele (post-Cre)
//    tm1c: Conditional allele (post-Flp)
//    tm1d: Deletion allele (post-Flp and Cre with no reporter)
//    tm1e: targeted, non-conditional allele
//    tm1: Reporter-tagged deletion allele (with selection cassette)
//    tm1.1: Reporter-tagged deletion allele (post Cre, with no selection cassette)
//    tm1.2: Reporter-tagged deletion allele (post Flp, with no reporter and selection cassette)
//    public static final String[] DELETE_VALUES = new String[]{"tm1b", "tm1d", "tm1", "tm1.1", "tm1.2"};
//
//    private boolean isDeleted(String type) {
//        return Arrays.asList(DELETE_VALUES).contains(type);
//    }

    private String getMutagenesisBlurb(Map<String, Integer> stats) {
        
        if(stats == null) {
            return null;
        }

        String s
                = "This gene has " + stats.get("wt_transcripts") + " wild type transcripts, "
                + "of which " + stats.get("wt_proteien_coding_transcripts") + " are protein-coding. "
                + "Following removal of the floxed region, " + stats.get("mut_coding_transcripts") + " ";

        if (stats.get("mut_coding_transcripts") == 1) {
            s += "transcript is ";
        } else {
            s += "transcripts are ";
        }

        s += "<strong>predicted</strong> to produce a truncated protein product of which "
                + stats.get("mut_nmd_transcripts") + " may be subject to non-sense mediated decay (NMD).";

        if (stats.get("mut_nmd_rescue_transcripts") > 0) {
            s += "<strong>NOTE:</strong> Of the " + stats.get("wt_non_coding_transcripts") + " non-coding wild type transcripts, "
                    + stats.get("mut_nmd_rescue_transcripts") + " are possibly subject to NMD rescue in the mutant.";
        }

        // <% if @data[:allele_design_type] == 'Deletion' %>
        // TODO: we need allele_design_type from somewhere
        String tbd = "<strong><span style='color:red'>TBD</span></strong>";

        if (false) {
            s += "This mutation is of type 'Deletion' (more information on IKMC alleles can be found "
                    + "<a href=\"http://www.knockoutmouse.org/about/targeting-strategies\">here</a>). The table below "
                    + "shows the <strong>predicted</strong> structure of the gene transcripts. "
                    + "Click the 'view' button for each transcript to see the full prediction for that transcript. ";
        } else {
            s += "The original allele for this mutation is of type '" + tbd + "'. The table below "
                    + "shows the <strong>predicted</strong> structure of the gene transcripts after application of Flp and Cre "
                    + "(forming a " + tbd + " allele - more information on IKMC alleles can be found "
                    + "<a href=\"http://www.knockoutmouse.org/about/targeting-strategies\">here</a>). "
                    + "Click the 'view' button for each transcript to see the full prediction for that transcript. ";

//                    "<br/><br/><strong><span style='color:red'>Note that TBD indicates a place-holder</span></strong>";
        }

        return s;
    }

    private JSONArray getMutagenesisDetails(
            String acc,
            String allele_name,
            String projectId) throws MalformedURLException, IOException, URISyntaxException {

        // TODO: fix me!
        //String url = "http://www.sanger.ac.uk/htgt/htgt2/tools/mutagenesis_prediction/project/35505/detail";
        String url = "http://www.sanger.ac.uk/htgt/htgt2/tools/mutagenesis_prediction/project/35505/detail";

        if (projectId != null) {
            url = "http://www.sanger.ac.uk/htgt/htgt2/tools/mutagenesis_prediction/project/" + projectId + "/detail";
        }

        HttpProxy proxy = new HttpProxy();
        String content = "";

        try {
            content = proxy.getContent(new URL(url));
        } catch (Exception name) {
            log.error("#### getMutagenesisDetails: " + name);
            return null;
        }

        log.info("#### content: " + content);

        JSONArray json = (JSONArray) JSONSerializer.toJSON(content);

        Map<String, String> mapper = new HashMap<>();
        mapper.put("UC", "UTR + start codon + CDS");
        mapper.put("CU", "CDS + stop codon + UTR");
        mapper.put("C", "CDS");
        mapper.put("U", "UTR");

        for (Object json1 : json) {
            JSONObject o = (JSONObject) json1;
            log.info("#### o: " + o);
            if (o.has("floxed_transcript_translation")) {
                String floxed_transcript_translation = o.getString("floxed_transcript_translation");
                //      log.info("#### 1: floxed_transcript_translation: " + floxed_transcript_translation);
                o.put("floxed_transcript_translation", floxed_transcript_translation.trim());
                //      log.info("#### 2: floxed_transcript_translation: " + floxed_transcript_translation);
            }
            if (o.has("exons")) {
                JSONArray exons = o.getJSONArray("exons");
                for (Object exon : exons) {
                    JSONObject o2 = (JSONObject) exon;
                    if (o2.has("structure")) {
                        String structure = o2.getString("structure");
                        o2.put("structure_ex", mapper.get(structure));
                    }
                    if (o2.has("floxed_structure")) {
                        String floxed_structure = o2.getString("floxed_structure");
                        o2.put("floxed_structure_ex", mapper.get(floxed_structure));
                    } else {
                        o2.put("floxed_structure_ex", "Deleted");
                    }
                    if (o2.has("domains")) {
                        JSONArray domains = o2.getJSONArray("domains");
                        for (Object domain : domains) {
                            JSONObject o3 = (JSONObject) domain;
                            JSONArray amino_acids = o3.getJSONArray("amino_acids");
                            String amino_acid_1 = amino_acids.size() > 1 ? amino_acids.getString(0) : "";
                            String amino_acid_2 = amino_acids.size() > 1 ? amino_acids.getString(1) : "";
                            String s = o3.getString("description") + " (" + amino_acid_1 + "/" + amino_acid_2 + " aa)";
                            o3.put("domains_ex", s);
                        }
                    }
                }
            }
        }
        return json;
    }

    @RequestMapping("/alleles/{acc}/{allele_name}")
    public String alleles2(
            @PathVariable String acc,
            @PathVariable String allele_name,
            Model model,
            HttpServletRequest request,
            RedirectAttributes attributes) throws KeyManagementException, NoSuchAlgorithmException, URISyntaxException, IOException, Exception {

        if (allele_name.equals("mutagenesis_url")) {
            model.addAttribute("message", "Mutagenesis not yet implemented!");
            log.info("#### alleles2: mutagenesis_url");

            JSONArray mutagenesis = getMutagenesisDetails(acc, allele_name, null);
            Map<String, Integer> stats = getMutagenesisStats(mutagenesis);
            String blurb = getMutagenesisBlurb(stats);

            model.addAttribute("mutagenesis", mutagenesis);
            model.addAttribute("mutagenesis_stats", stats);
            model.addAttribute("mutagenesis_blurb", blurb);

            log.info("#### mutagenesis: " + mutagenesis);

            return "mutagenesis_url";
        }

        if (allele_name.equals("lrpcr_genotyping_primers")) {
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

        if (constructs == null) {
            return "alleles";
        }

        model.addAttribute("mice", constructs.get("mice"));
        model.addAttribute("es_cells", constructs.get("es_cells"));
        model.addAttribute("targeting_vectors", constructs.get("targeting_vectors"));
        model.addAttribute("summary", constructs.get("summary"));

        log.info("#### summary: " + constructs.get("summary"));

        return "alleles";
    }

    @RequestMapping("/mutagenesis/{projectId}")
    public String mutagenesis_project(
            @PathVariable String projectId,
            Model model,
            HttpServletRequest request,
            RedirectAttributes attributes) throws KeyManagementException, NoSuchAlgorithmException, URISyntaxException, IOException, Exception {

        JSONArray mutagenesis = getMutagenesisDetails(null, null, projectId);
        Map<String, Integer> stats = getMutagenesisStats(mutagenesis);
        String blurb = getMutagenesisBlurb(stats);

        model.addAttribute("mutagenesis", mutagenesis);
        model.addAttribute("mutagenesis_stats", stats);
        model.addAttribute("mutagenesis_blurb", blurb);

        log.info("#### mutagenesis: " + mutagenesis);

        return "mutagenesis_url";
    }

    @RequestMapping("/mutagenesis")
    public String mutagenesis(
            @PathVariable String projectId,
            Model model,
            HttpServletRequest request,
            RedirectAttributes attributes) throws KeyManagementException, NoSuchAlgorithmException, URISyntaxException, IOException, Exception {

        return "mutagenesis_url";
    }
}
