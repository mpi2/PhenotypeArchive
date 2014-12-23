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

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import uk.ac.ebi.generic.util.SolrIndex2;
import uk.ac.ebi.phenotype.web.util.HttpProxy;

import javax.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class AllelesController {

    private final Logger log = LoggerFactory.getLogger(AllelesController.class);
    // TODO: get rid!
    private static final String VERSIONDATE = "Mon Oct 20 2014";

    @Autowired
    SolrIndex2 solrIndex2;

//    private HashMap<String, String> makeItem(Map<String, Object> map2) {
//        HashMap<String, String> map = new HashMap<>();
//        map.put("marker_symbol", (String)map2.get("marker_symbol"));
//        map.put("allele_name", (String)map2.get("allele_name"));
//        map.put("mgi_accession_id", (String)map2.get("mgi_accession_id"));
//        map.put("type", (String)map2.get("type"));
//        map.put("display_name_debug", (String)map2.get("display_name_debug"));
//        map.put("display_name", (String)map2.get("display_name"));
//        map.put("url", (String)map2.get("url"));
//
//        log.info("#### makeItem...");
//        log.info("#### makeItem: map2: " + map2);
//        log.info("#### makeItem: map: " + map);
//
//        return map;
//    }

    @RequestMapping("/alleles/qc_data/{alleleType}/{type}/{name}")
    public String qcData(
            @PathVariable String alleleType,
            @PathVariable String type,
            @PathVariable String name,
            Model model,
            HttpServletRequest request,
            RedirectAttributes attributes) throws Exception {

        HashMap<String, HashMap<String, List<String>>> constructs = solrIndex2.getAlleleQcInfo(alleleType, type, name);
        model.addAttribute("qcData", constructs);

        String simple = request.getParameter("simple");
        boolean s = simple != null && simple.equals("true");
        if(s) {
            return "qcDataSimple";
        }

        log.info("#### qcData: model: " + model);

        return "qcData";
    }

    @RequestMapping("/alleles/{acc}")
    public String alleles1(
            @PathVariable String acc,
            Model model,
            HttpServletRequest request,
            RedirectAttributes attributes) throws Exception {

        log.info("#### alleles1...");

        List<Map<String, Object>> list1 = solrIndex2.getProductGeneDetails(acc);
        List<Map<String, Object>> list_alleles = new ArrayList<>();
        List<Map<String, Object>> list_none_alleles = new ArrayList<>();
        String mgi_accession_id = acc;

        if (list1 != null) {
            for (Map<String, Object> item : list1) {
                if(((String) item.get("allele_name")).equals("None")) {
                    list_none_alleles.add(item);
                }
                else {
                    list_alleles.add(item);
                }
               // mgi_accession_id = (String)item.get("mgi_accession_id");
            }
        }

        log.info("#### alleles1: list1: " + list1);

        model.addAttribute("mgi_accession_id", mgi_accession_id);

        model.addAttribute("list_alleles", list_alleles);
        model.addAttribute("list_none_alleles", list_none_alleles);

        model.addAttribute("versionDate", VERSIONDATE);

        String debug = request.getParameter("debug");
        boolean d = debug != null && debug.equals("true");
        if(d) {
            model.addAttribute("debug", "true");
        }

        return "alleles_list";
    }

//    @RequestMapping("/alleles")
//    public String alleles0(
//            Model model,
//            HttpServletRequest request,
//            RedirectAttributes attributes) throws KeyManagementException, NoSuchAlgorithmException, URISyntaxException, IOException, Exception {
//
//        log.info("#### alleles0...");
//
//        return alleles1("Cbx1", model, request, attributes);
//    }

    private Map<String, Integer> getMutagenesisStats(JSONArray transcripts) {

        if (transcripts == null) {
            return null;
        }

        Map<String, Integer> map = new HashMap<>();
        map.put("wt_transcripts", 0);
        map.put("wt_non_coding_transcripts", 0);
        map.put("wt_protein_coding_transcripts", 0);
        map.put("mut_nmd_transcripts", 0);
        map.put("mut_coding_transcripts", 0);
        map.put("mut_nmd_rescue_transcripts", 0);

        for (Object o : transcripts) {
            JSONObject o2 = (JSONObject) o;
            map.put("wt_transcripts", map.get("wt_transcripts") + 1);
            if (o2.has("biotype") && o2.getString("biotype").equals("protein_coding")) {
                map.put("wt_protein_coding_transcripts", map.get("wt_protein_coding_transcripts") + 1);

                if (o2.getString("floxed_transcript_description").matches("^No protein product \\(NMD\\)")) {
                    map.put("mut_nmd_transcripts", map.get("mut_nmd_transcripts") + 1);
                }

                if (o2.getString("floxed_transcript_description").matches("^No protein product .NMD.")
                        || !o2.getString("floxed_transcript_description").matches("^No protein product")) {
                    map.put("mut_coding_transcripts", map.get("mut_coding_transcripts") + 1);
                }

                if (o2.getString("floxed_transcript_description").matches("^Possible NMD rescue")) {
                    map.put("mut_nmd_rescue_transcripts", map.get("mut_nmd_rescue_transcripts") + 1);
                }
            }
            map.put("wt_non_coding_transcripts", map.get("wt_transcripts") - map.get("wt_protein_coding_transcripts"));
        }
        return map;
    }

    // TODO: fix dodgy routine returning html!

    private String getMutagenesisBlurb(Map<String, Integer> stats, HashMap<String, Object> summary) {

        if (stats == null) {
            return null;
        }

        String s
                = "This gene has " + stats.get("wt_transcripts") + " wild type transcripts, "
                + "of which " + stats.get("wt_protein_coding_transcripts") + " are protein-coding. "
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

        if (summary.get("is_deleted").equals("true")) {
            s += "This mutation is of type 'Deletion' (more information on IKMC alleles can be found "
                    + "<a href=\"http://www.knockoutmouse.org/about/targeting-strategies\">here</a>). The table below "
                    + "shows the <strong>predicted</strong> structure of the gene transcripts. "
                    + "Click the 'view' button for each transcript to see the full prediction for that transcript. ";
        } else {
            s += " The original allele for this mutation is of type '" + summary.get("allele_description").toString() + "'. The table below "
                    + "shows the <strong>predicted</strong> structure of the gene transcripts after application of Flp and Cre "
                    + "(forming a " + summary.get("allele_description_1b").toString() + " allele - more information on IKMC alleles can be found "
                    + "<a href=\"http://www.knockoutmouse.org/about/targeting-strategies\">here</a>). "
                    + "Click the 'view' button for each transcript to see the full prediction for that transcript. ";
        }

        return s;
    }

    // TODO: move to separate class?

    private JSONArray getMutagenesisDetails(
            String acc,
            String allele_name,
            String projectId) throws MalformedURLException, IOException, URISyntaxException {

        if (projectId == null) {
            log.info("#### getMutagenesisDetails: no project id!");
            return null;
        }

        String url = "http://www.sanger.ac.uk/htgt/htgt2/tools/mutagenesis_prediction/project/" + projectId + "/detail";

        log.info("#### getMutagenesisDetails: url: ");
        log.info(url);

        HttpProxy proxy = new HttpProxy();
        String content;

        try {
            content = proxy.getContent(new URL(url));
        } catch (IOException | URISyntaxException ex) {
            log.error("#### getMutagenesisDetails: " + ex);
            return null;
        }

        log.info("#### content: " + content);

        JSONArray json = (JSONArray) JSONSerializer.toJSON(content);

        log.info("#### before: " + json);

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
                o.put("floxed_transcript_translation", floxed_transcript_translation.trim());
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

        log.info("#### after: " + json);

        return json;
    }

    @RequestMapping("/alleles/{acc}/{allele_name}")
    public String alleles2(
            @PathVariable String acc,
            @PathVariable String allele_name,
            @RequestParam(value = "bare", required = false, defaultValue = "false") Boolean bare,
            Model model,
            HttpServletRequest request,
            RedirectAttributes attributes) throws KeyManagementException, NoSuchAlgorithmException, URISyntaxException, IOException, Exception {

        log.info("#### AllelesController::alleles2");
        log.info("#### acc: " + acc);
        log.info("#### allele_name: " + allele_name);

        String debug = request.getParameter("debug");
        log.info("#### alleles1: debug: " + debug);
        boolean d = debug != null && debug.equals("true");

        if(bare) model.addAttribute("bare", bare);

        return allelesCommon(acc, allele_name, model, request, attributes);
    }

    @RequestMapping("/alleles/{acc}/{cassette}/{design_id}")
    public String alleles3(
            @PathVariable String acc,
            @PathVariable String cassette,
            @PathVariable String design_id,
            Model model,
            HttpServletRequest request,
            RedirectAttributes attributes) throws KeyManagementException, NoSuchAlgorithmException, URISyntaxException, IOException, Exception {

        log.info("#### AllelesController::alleles3");
        log.info("#### acc: " + acc);
        log.info("#### cassette: " + cassette);
        log.info("#### design_id: " + design_id);

        return allelesCommon2(acc, cassette, design_id, model, request, attributes);
    }

    public String allelesCommon2(
            String acc,
            String cassette,
            String design_id,
            Model model,
            HttpServletRequest request,
            RedirectAttributes attributes) throws KeyManagementException, NoSuchAlgorithmException, URISyntaxException, IOException, Exception {

        String debug = request.getParameter("debug");
        log.info("#### allelesCommon: debug: " + debug);
        boolean d = debug != null && debug.equals("true");
        if(d) {
            model.addAttribute("debug", "true");
        }

        Map<String, Object> constructs = solrIndex2.getGeneProductInfo(acc, cassette, design_id, d);

        if (constructs == null) {
            return "alleles";
        }

        model.addAttribute("mice", constructs.get("mice"));
        model.addAttribute("es_cells", constructs.get("es_cells"));
        model.addAttribute("targeting_vectors", constructs.get("targeting_vectors"));
        model.addAttribute("summary", constructs.get("summary"));

        model.addAttribute("title", constructs.get("title"));

        log.info("#### mice: " + constructs.get("mice"));
        log.info("#### es_cells: " + constructs.get("es_cells"));
        log.info("#### targeting_vectors: " + constructs.get("targeting_vectors"));
        log.info("#### summary: " + constructs.get("summary"));
        log.info("#### title: " + constructs.get("title"));

        return "alleles";
    }

    public String allelesCommon(
            String acc,
            String allele_name,
            Model model,
            HttpServletRequest request,
            RedirectAttributes attributes) throws Exception {

        log.info("#### AllelesController::allelesCommon");
        log.info("#### acc: " + acc);
        log.info("#### allele_name: " + allele_name);

        String debug = request.getParameter("debug");
        log.info("#### allelesCommon: debug: " + debug);
        boolean d = debug != null && debug.equals("true");
        if(d) {
            model.addAttribute("debug", "true");
        }
        
        Map<String, Object> constructs;
       
        if ( request.getParameter("bare").equals("true") ) {
        	constructs = solrIndex2.getEucommToolsGeneProductInfo(acc, allele_name, d);
        }
        else {
        	constructs = solrIndex2.getGeneProductInfo(acc, allele_name, d);
        }
        
        if (constructs == null) {
            return "alleles";
        }

        model.addAttribute("mice", constructs.get("mice"));
        model.addAttribute("es_cells", constructs.get("es_cells"));
        model.addAttribute("targeting_vectors", constructs.get("targeting_vectors"));
        model.addAttribute("summary", constructs.get("summary"));

        model.addAttribute("title", constructs.get("title"));

        //                    <a class="hasTooltip" href="${baseUrl}/alleles/qc_data/${mouse['allele_type']}/mouse/${mouse['colony_name']}">QC data</a>
        //model.addAttribute("qc_data_mouse", "alleles/qc_data/" + constructs.get("mice").get("title"));


        log.info("#### mice: " + constructs.get("mice"));
        log.info("#### es_cells: " + constructs.get("es_cells"));
        log.info("#### targeting_vectors: " + constructs.get("targeting_vectors"));
        log.info("#### summary: " + constructs.get("summary"));
        log.info("#### title: " + constructs.get("title"));

        if (model.containsAttribute("show_header")) {
            return "alleles_noheader";
        }
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
        String blurb = getMutagenesisBlurb(stats, null);

        model.addAttribute("mutagenesis", mutagenesis);
        model.addAttribute("mutagenesis_stats", stats);
        model.addAttribute("mutagenesis_blurb", blurb);

        log.info("#### mutagenesis: " + mutagenesis);

        return "mutagenesis";
    }

    private JSONObject getPcrDetails(
            boolean isMirko,
            String id) throws MalformedURLException, IOException, URISyntaxException {

        if (id == null) {
            return null;
        }

        String url = "http://www.sanger.ac.uk/htgt/htgt2/tools/genotypingprimers/" + id;

        if(isMirko) {
            url = "http://www.sanger.ac.uk/htgt/htgt2/tools/genotypingprimers/mirko_primers/" + id;
        }

        HttpProxy proxy = new HttpProxy();
        String content;

        try {
            content = proxy.getContent(new URL(url));
        } catch (IOException | URISyntaxException ex) {
            log.error("#### getPcrDetails: " + ex);
            return null;
        }

        log.info("#### getPcrDetails: " + content);

        JSONObject jsonObject;

        try {
            jsonObject = (JSONObject) JSONSerializer.toJSON(content);
        } catch (Exception ex) {
            log.error("#### getPcrDetails: " + ex);
            return null;
        }

        return jsonObject;
    }

    @RequestMapping("/lrpcr/{acc}/{allele_name}")
    public String lrpcr_acc_allele(
            @PathVariable String acc,
            @PathVariable String allele_name,
            Model model,
            HttpServletRequest request,
            RedirectAttributes attributes) throws KeyManagementException, NoSuchAlgorithmException, URISyntaxException, IOException, Exception {

        log.info("#### lrpcr_acc_allele");

        Map<String, Object> products = solrIndex2.getGeneProductInfo(acc, allele_name, false);

        if (products == null) {
            return "lrpcr_genotyping_primers";
        }

        model.addAttribute("mice", products.get("mice"));
        model.addAttribute("es_cells", products.get("es_cells"));
        model.addAttribute("targeting_vectors", products.get("targeting_vectors"));
        model.addAttribute("summary", products.get("summary"));

        HashMap<String, Object> summary = (HashMap<String, Object>) products.get("summary");

        boolean isMirko = solrIndex2.isMirko(acc, allele_name);

        String id;

        if(isMirko) {
            id = solrIndex2.getDesign(acc, allele_name);
        }
        else {
            id = solrIndex2.getProject(acc, allele_name);
        }

        JSONObject object = getPcrDetails(isMirko, id);
        model.addAttribute("lrpcr", object);

        if (id != null) {
            model.addAttribute("lrpcr_title", summary.get("symbol") + " LRPCR Genotyping Primers");
        } else {
            model.addAttribute("lrpcr_title", "LRPCR Genotyping Primers not available");
        }

        return "lrpcr_genotyping_primers";
    }

    @RequestMapping("/mutagenesis/{acc}/{allele_name}")
    public String mutagenesis_project_type(
            @PathVariable String acc,
            @PathVariable String allele_name,
            Model model,
            HttpServletRequest request,
            RedirectAttributes attributes) throws KeyManagementException, NoSuchAlgorithmException, URISyntaxException, IOException, Exception {

        log.info("#### mutagenesis_project_type");

        Map<String, Object> products = solrIndex2.getGeneProductInfo(acc, allele_name, false);

        if (products == null) {
            return "mutagenesis";
        }

        model.addAttribute("mice", products.get("mice"));
        model.addAttribute("es_cells", products.get("es_cells"));
        model.addAttribute("targeting_vectors", products.get("targeting_vectors"));
        model.addAttribute("summary", products.get("summary"));

        HashMap<String, Object> summary = (HashMap<String, Object>) products.get("summary");

        String projectId = solrIndex2.getProject(acc, allele_name);

        JSONArray mutagenesis = getMutagenesisDetails(null, null, projectId);
        Map<String, Integer> stats = getMutagenesisStats(mutagenesis);
        String blurb = getMutagenesisBlurb(stats, (HashMap<String, Object>) products.get("summary"));

        model.addAttribute("mutagenesis", mutagenesis);
        model.addAttribute("mutagenesis_stats", stats);
        model.addAttribute("mutagenesis_blurb", blurb); // TODO: rename me

        if (projectId != null) {
            model.addAttribute("mutagenesis_title", summary.get("symbol") + " Mutagenesis Prediction");
        } else {
            model.addAttribute("mutagenesis_title", "Mutagenesis Prediction not available");
        }

        log.info("#### mutagenesis: " + mutagenesis);

        return "mutagenesis";
    }
}
