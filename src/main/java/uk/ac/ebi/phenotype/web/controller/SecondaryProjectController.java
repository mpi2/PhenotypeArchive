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

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.ac.ebi.phenotype.chart.Constants;
import uk.ac.ebi.phenotype.chart.PhenomeChartProvider;
import uk.ac.ebi.phenotype.chart.UnidimensionalChartAndTableProvider;
import uk.ac.ebi.phenotype.dao.SecondaryProjectDAO;
import uk.ac.ebi.phenotype.pojo.PhenotypeCallSummary;
import uk.ac.ebi.phenotype.service.*;
import uk.ac.ebi.phenotype.service.dto.AlleleDTO;
import uk.ac.ebi.phenotype.service.dto.GeneDTO;
import uk.ac.ebi.phenotype.service.dto.GenotypePhenotypeDTO;
import uk.ac.ebi.phenotype.service.dto.MpDTO;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.*;
import uk.ac.ebi.phenotype.solr.indexer.utils.IndexerMap;

@Controller
public class SecondaryProjectController {

    private final Logger logger = LoggerFactory.getLogger(SecondaryProjectController.class);

    @Resource(name = "globalConfiguration")
    private Map<String, String> config;

    @Autowired
    AlleleService as;

    @Autowired
    @Qualifier("postqcService")
    PostQcService genotypePhenotypeService;

    @Autowired
    @Qualifier("preqcService")
    PreQcService preQcService;

    @Autowired
    GeneService geneService;

    @Autowired
    MpService mpService;

    @Autowired
    UnidimensionalChartAndTableProvider chartProvider;

    @Autowired
    @Qualifier("idg")
    SecondaryProjectDAO idg;

    @Autowired
    @Qualifier("threeI")
    SecondaryProjectDAO threeI;

    @Autowired
    @Qualifier("solrServer")
    SolrServer phenodigmCore;

    private PhenomeChartProvider phenomeChartProvider = new PhenomeChartProvider();

    @RequestMapping(value = "/secondaryproject/{id}/download", method = RequestMethod.GET)
    public ResponseEntity<String> downloadSecondaryProjectData(@PathVariable String id, Model model) {

        logger.info("Downloading data for secondary project id=" + id);

        Map<String, Set<String>> mpterms = new HashMap<>();
        Map<String, Set<String>> mptermnames = new HashMap<>();
        Map<String, Set<String>> highMpterms = new HashMap<>();
        Map<String, Set<String>> highMptermnames = new HashMap<>();
        Map<String, Set<String>> preqcmpterms = new HashMap<>();
        Map<String, Set<String>> hpterms = new HashMap<>();
        Map<String, Set<String>> hptermnames = new HashMap<>();
        Map<String, Set<String>> humangenes = new HashMap<>();
        Map<String, Set<String>> diseaseterms = new HashMap<>();
        Map<String, String> mousesymbols = new HashMap<>();
        List<String> resp = new ArrayList<>();

        try {
            Set<String> accessions = idg.getAccessionsBySecondaryProjectId(id);

            Map<String, List<Map<String, String>>> getMpToHpTerms = IndexerMap.getMpToHpTerms(phenodigmCore);
            Map<String, GeneDTO> genes = geneService.getHumanOrthologsForGeneSet(accessions);

            // Gather all the phenotype calls by gene id
            Map<String, List<PhenotypeCallSummary>> pcss = new HashMap<>();
            for (PhenotypeCallSummary pcs : genotypePhenotypeService.getPhenotypeFacetResultByGenomicFeatures(accessions).getPhenotypeCallSummaries()) {
                if ( ! pcss.containsKey(pcs.getGene().getId().getAccession())) {
                    pcss.put(pcs.getGene().getId().getAccession(), new ArrayList<PhenotypeCallSummary>());
                }
                pcss.get(pcs.getGene().getId().getAccession()).add(pcs);
            }

            for (String MGIID : accessions) {

                if ( ! mpterms.containsKey(MGIID)) {
                    mpterms.put(MGIID, new TreeSet<String>());
                    mptermnames.put(MGIID, new TreeSet<String>());
                    highMpterms.put(MGIID, new TreeSet<String>());
                    highMptermnames.put(MGIID, new TreeSet<String>());
                }
                if ( ! preqcmpterms.containsKey(MGIID)) {
                    preqcmpterms.put(MGIID, new TreeSet<String>());
                }
                if ( ! hpterms.containsKey(MGIID)) {
                    hpterms.put(MGIID, new TreeSet<String>());
                    hptermnames.put(MGIID, new TreeSet<String>());
                }
                if ( ! humangenes.containsKey(MGIID)) {
                    humangenes.put(MGIID, new TreeSet<String>());
                }
                if ( ! diseaseterms.containsKey(MGIID)) {
                    diseaseterms.put(MGIID, new TreeSet<String>());
                }

                GeneDTO g = geneService.getGeneById(MGIID);
                if (g != null) {
                    mousesymbols.put(MGIID, g.getMarkerSymbol());
                }

                logger.info(" looking for mp terms for {}", MGIID);
                if (pcss.containsKey(MGIID)) {
                    for (PhenotypeCallSummary pcs : pcss.get(MGIID)) {
                        String mp = pcs.getPhenotypeTerm().getId().getAccession();
                        String mpterm = pcs.getPhenotypeTerm().getName();
                        mpterms.get(MGIID).add(mp);
                        mptermnames.get(MGIID).add(mpterm);

                        MpDTO mpDTO = mpService.getPhenotypes(mp);
                        if (mpDTO != null) {

                            if (mpDTO.getTopLevelMpId() != null) {
                                highMpterms.get(MGIID).addAll(mpDTO.getTopLevelMpId());
                            }

                            if (mpDTO.getTopLevelMpTerm() != null) {
                                highMptermnames.get(MGIID).addAll(mpDTO.getTopLevelMpTerm());
                            }
                        }

                        logger.info("  looking for hp terms for {}", mp);
                        if (getMpToHpTerms.containsKey(mp)) {
                            for (Map<String, String> hpTerm : getMpToHpTerms.get(mp)) {
                                String hpid = hpTerm.get("hp_id");
                                String hpname = hpTerm.get("hp_term");
                                logger.info("   adding hp term {} for {}", hpid, mp);
                                hpterms.get(MGIID).add(hpid);
                                hptermnames.get(MGIID).add(hpname);
                            }
                        }
                    }
                } else if (genes.get(MGIID) != null && genes.get(MGIID).getLatestPhenotypeStatus() != null && genes.get(MGIID).getLatestPhenotypeStatus().equalsIgnoreCase(GeneService.GeneFieldValue.PHENOTYPE_STATUS_COMPLETE)) {
                    mpterms.get(MGIID).add("No phenotype calls");
                } else if (genes.get(MGIID) != null && genes.get(MGIID).getLatestPhenotypeStatus() != null && genes.get(MGIID).getLatestPhenotypeStatus().equalsIgnoreCase(GeneService.GeneFieldValue.PHENOTYPE_STATUS_STARTED)) {

                    SolrDocumentList preQcMpTerms = preQcService.getPhenotypes(MGIID);
                    for (SolrDocument doc : preQcMpTerms) {
                        if (doc.getFieldValue(GenotypePhenotypeDTO.MP_TERM_ID) != null) {
                            preqcmpterms.get(MGIID).add((String) doc.getFieldValue(GenotypePhenotypeDTO.MP_TERM_ID));
                        }
                    }
                    if (preqcmpterms.get(MGIID).isEmpty()) {
                        preqcmpterms.get(MGIID).add("No phenotype calls");
                    }

                    mpterms.get(MGIID).add("PREQC_MP_TERMS(" + StringUtils.join(preqcmpterms.get(MGIID), ",") + ")");

                } else {
                    mpterms.get(MGIID).add("No data");
                }

                logger.info("  looking for human symbols for {}", MGIID);
                if (genes.get(MGIID) != null && genes.get(MGIID).getHumanGeneSymbol() != null) {
                    humangenes.get(MGIID).addAll(genes.get(MGIID).getHumanGeneSymbol());
                    logger.info("   adding human symbols {} for {}", genes.get(MGIID).getHumanGeneSymbol(), MGIID);
                }

                if (genes.get(MGIID) != null && genes.get(MGIID).getDiseaseId() != null) {

                    diseaseterms.get(MGIID).addAll(genes.get(MGIID).getDiseaseId());
                }

            }

            resp.add(StringUtils.join(Arrays.asList("MGI ID", "Mouse symbol", "MP term IDs", "MP term names", "MP high-level term IDs", "MP high-level term names", "Human Gene symbol", "HP term IDs", "HP term names", "Disease associations"), "\t"));
            for (String MGIID : accessions) {
                List line = Arrays.asList(
                        MGIID,
                        mousesymbols.get(MGIID),
                        StringUtils.join(mpterms.get(MGIID), ","),
                        StringUtils.join(mptermnames.get(MGIID), ","),
                        StringUtils.join(highMpterms.get(MGIID), ","),
                        StringUtils.join(highMptermnames.get(MGIID), ","),
                        StringUtils.join(humangenes.get(MGIID), ","),
                        StringUtils.join(hpterms.get(MGIID), ","),
                        StringUtils.join(hptermnames.get(MGIID), ","),
                        StringUtils.join(diseaseterms.get(MGIID), ",")
                );
                resp.add(StringUtils.join(line, "\t"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(StringUtils.join(resp, "\n"), createResponseHeaders(), HttpStatus.OK);
    }

    private HttpHeaders createResponseHeaders() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.TEXT_PLAIN);
        return responseHeaders;
    }

    @RequestMapping(value = "/secondaryproject/{id}", method = RequestMethod.GET)
    public String loadSecondaryProjectPage(@PathVariable String id, Model model, HttpServletRequest request, RedirectAttributes attributes)
            throws SolrServerException, IOException, URISyntaxException {

        System.out.println("calling secondary project id=" + id);
        Set<String> accessions;
        if (id.equalsIgnoreCase(SecondaryProjectDAO.SecondaryProjectIds.IDG.name())) {
            try {
                accessions = idg.getAccessionsBySecondaryProjectId(id);
                model.addAttribute("genotypeStatusChart", chartProvider.getStatusColumnChart(as.getStatusCount(accessions, AlleleDTO.GENE_LATEST_MOUSE_STATUS), "Genotype Status Chart", "genotypeStatusChart"));
                model.addAttribute("phenotypeStatusChart", chartProvider.getStatusColumnChart(as.getStatusCount(accessions, AlleleDTO.LATEST_PHENOTYPE_STATUS), "Phenotype Status Chart", "phenotypeStatusChart"));
                List<PhenotypeCallSummary> results = genotypePhenotypeService.getPhenotypeFacetResultByGenomicFeatures(accessions).getPhenotypeCallSummaries();
                String chart = phenomeChartProvider.generatePhenomeChartByGenes(results, null, Constants.SIGNIFICANT_P_VALUE);
                model.addAttribute("chart", chart);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            System.out.println("returning to idg");
            return "idg";
        } else if (id.equalsIgnoreCase(SecondaryProjectDAO.SecondaryProjectIds.threeI.name())) {
            System.out.println("returning to threeI");
            return "threeI";
        }

        return "idg";
    }

}
