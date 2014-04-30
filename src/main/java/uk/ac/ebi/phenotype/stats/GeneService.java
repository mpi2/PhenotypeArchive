package uk.ac.ebi.phenotype.stats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.stereotype.Service;

@Service
public class GeneService {

    private HttpSolrServer solr;

    private Logger log = Logger.getLogger(this.getClass().getCanonicalName());

    public static final class GeneField {
        public final static String PHENOTYPE_STATUS = "phenotype_status";
        public final static String MGI_ACCESSION_ID = "mgi_accession_id";
        public final static String LATEST_PHENOTYPING_CENTRE = "latest_phenotyping_centre";
    }
    
    public static final class GeneFieldValue {
        public final static String PHENOTYPE_STATUS_STARTED = "Phenotyping Started";
        public final static String PRODUCTION_CENTRE_WTSI = "WTSI";
    }

    public GeneService(String solrUrl) {
        solr = new HttpSolrServer(solrUrl);

    }

    private String derivePhenotypingStatus(SolrDocument doc) {

        String field = "latest_phenotype_status";
        try {
            // Phenotyping complete
            if (doc.containsKey(field)
                    && !doc.getFirstValue(field).toString().equals("")) {
                String val = doc.getFirstValue(field).toString();
                if (val.equals("Phenotyping Started")
                        || val.equals("Phenotyping Complete")) {
                    return "available";
                }
            }

			// for legacy data: indexed through experiment core (so not want
            // Sanger Gene or Allele cores)
            if (doc.containsKey("hasQc")) {
                return "QCed data available";
            }
        } catch (Exception e) {
            log.error("Error getting phenotyping status");
            log.error(e.getLocalizedMessage());
        }

        return "";
    }

    /**
     * Return all genes in the gene core matching phenotypeStatus and productionCentre.
     * @param phenotypeStatus phenotype status
     * @param productionCentre production centre
     * @return all genes in the gene core matching phenotypeStatus and productionCentre.
     * @throws SolrServerException
     */
    public Set<String> getGenesByPhenotypeStatusAndProductionCentre(String phenotypeStatus, String productionCentre) throws SolrServerException {

        SolrQuery solrQuery = new SolrQuery();
        String queryString = "(" + GeneField.PHENOTYPE_STATUS + ":\"" + phenotypeStatus + "\") AND (" + GeneField.LATEST_PHENOTYPING_CENTRE + ":\"" + productionCentre + "\")";
        solrQuery.setQuery(queryString);
        solrQuery.setRows(1000000);
        solrQuery.setFields(GeneField.MGI_ACCESSION_ID);
        QueryResponse rsp = null;
        rsp = solr.query(solrQuery);
        SolrDocumentList res = rsp.getResults();
        HashSet<String> allGenes = new HashSet<String>();
        for (SolrDocument doc : res) {
            allGenes.add((String) doc.getFieldValue(GeneField.MGI_ACCESSION_ID));
        }
        
        log.debug("getGenesByPhenotypeStatusAndProductionCentre: solrQuery = " + queryString);
        return allGenes;
    }

    /**
     * Return all genes from the gene core.
     *
     * @return all genes from the gene core.
     * @throws SolrServerException
     */
    public Set<String> getAllGenes() throws SolrServerException {

        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery(GeneField.MGI_ACCESSION_ID + ":*");
        solrQuery.setRows(1000000);
        solrQuery.setFields(GeneField.MGI_ACCESSION_ID);
        QueryResponse rsp = null;
        rsp = solr.query(solrQuery);
        SolrDocumentList res = rsp.getResults();
        HashSet<String> allGenes = new HashSet<String>();
        for (SolrDocument doc : res) {
            allGenes.add((String) doc.getFieldValue(GeneField.MGI_ACCESSION_ID));
        }
        return allGenes;
    }

    // returns ready formatted icons
    public Map<String, String> getProductionStatus(String geneId) throws SolrServerException {

        SolrQuery query = new SolrQuery();
        query.setQuery("mgi_accession_id:\"" + geneId + "\"");
        QueryResponse response = solr.query(query);
        SolrDocument doc = response.getResults().get(0);
        String miceStatus = "";
        String esCellStatus = "";
        String phenStatus = "";
        Boolean order = false;

        try {

            /* ******** phenotype status ******** */
            phenStatus = derivePhenotypingStatus(doc).equals("") ? "" : "<a class='status done' title='Scroll down for phenotype associations.'><span>phenotype data available</span></a>";

            /* ******** mice production status ******** */
            String patternStr = "(tm.*)\\(.+\\).+"; // allele name pattern
            Pattern pattern = Pattern.compile(patternStr);

            // Mice: blue tm1/tm1a/tm1e... mice (depending on how many allele docs) 
            if (doc.containsKey("mouse_status")) {

                ArrayList<String> alleleNames = (ArrayList<String>) doc.getFieldValue("allele_name");
                ArrayList<String> mouseStatus = (ArrayList<String>) doc.getFieldValue("mouse_status");

                for (int i = 0; i < mouseStatus.size(); i++) {
                    String mouseStatusStr = mouseStatus.get(i).toString();

                    if (mouseStatusStr.equals("Mice Produced")) {
                        String alleleName = alleleNames.get(i).toString();
                        Matcher matcher = pattern.matcher(alleleName);
                        if (matcher.find()) {
                            String alleleType = matcher.group(1);
                            miceStatus += "<span class='status done' title='" + mouseStatusStr + "' >"
                                    + "	<span>Mice<br>" + alleleType + "</span>"
                                    + "</span>";
                        }
                        order = true;
                    } else if (mouseStatusStr.equals("Assigned for Mouse Production and Phenotyping")) {
                        String alleleName = alleleNames.get(i).toString();
                        Matcher matcher = pattern.matcher(alleleName);
                        if (matcher.find()) {
                            String alleleType = matcher.group(1);
                            miceStatus += "<span class='status inprogress' title='Mice production in progress' >"
                                    + "	<span>Mice<br>" + alleleType + "</span>"
                                    + "</span>";
                        }
                    }
                }
                // if no mice status found but there is already allele produced, mark it as "mice produced planned"
                for (int j = 0; j < alleleNames.size(); j++) {
                    String alleleName = alleleNames.get(j).toString();
                    if (!alleleName.equals("") && !alleleName.equals("None") && mouseStatus.get(j).toString().equals("")) {
                        Matcher matcher = pattern.matcher(alleleName);
                        if (matcher.find()) {
                            String alleleType = matcher.group(1);
                            miceStatus += "<span class='status none' title='Mice production planned' >"
                                    + "	<span>Mice<br>" + alleleType + "</span>"
                                    + "</span>";
                        }
                    }
                }

            }

            /* ******** ES cell production status  ******** */
            String field = "latest_es_cell_status";
            if (doc.containsKey(field)) {
                // blue es cell status				
                String text = doc.getFirstValue(field).toString();
                if (text.equals("ES Cell Targeting Confirmed")) {
                    esCellStatus = "<a class='status done' href='' title='ES Cells produced' >"
                            + " <span>ES cells</span>"
                            + "</a>";
                    order = true;
                } else if (text.equals("ES Cell Production in Progress")) {
                    esCellStatus = "<span class='status inprogress' title='ES cells production in progress' >"
                            + "	<span>ES Cell</span>"
                            + "</span>";
                }

            }

        } catch (Exception e) {
            log.error("Error getting ES cell/Mice status");
            log.error(e.getLocalizedMessage());
        }
        HashMap<String, String> res = new HashMap<>();
        res.put("icons", esCellStatus + miceStatus + phenStatus);
        res.put("orderPossible", order.toString());
        return res;

    }

    public Boolean checkPhenotypeStarted(String geneAcc) {

        SolrQuery query = new SolrQuery();
        query.setQuery("mgi_accession_id:\"" + geneAcc + "\"");

        QueryResponse response;
        try {
            response = solr.query(query);

            SolrDocument doc = response.getResults().get(0);
            // phenotype_status
            if (doc.containsKey("phenotype_status")) {
                ArrayList<String> statuses = (ArrayList<String>) doc.getFieldValue("phenotype_status");
                for (String status : statuses) {
                    if (status.equalsIgnoreCase("Phenotyping Started")) {
                        return true;
                    }
                }
            }
        } catch (SolrServerException e) {
            e.printStackTrace();
        }
        return false;
    }

}
