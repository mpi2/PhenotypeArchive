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
public class MpService {

    private HttpSolrServer solr;

    private Logger log = Logger.getLogger(this.getClass().getCanonicalName());

    public static final class MpField {
        public final static String MP_TERM_ID = "mp_id";
    }
    
    public MpService(String solrUrl) {
        solr = new HttpSolrServer(solrUrl);
    }

    /**
     * Return all phenotypes from the mp core.
     *
     * @return all genes from the gene core.
     * @throws SolrServerException
     */
    public Set<String> getAllPhenotypes() throws SolrServerException {

        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery(MpField.MP_TERM_ID + ":*");
        solrQuery.setRows(1000000);
        QueryResponse rsp = null;
        rsp = solr.query(solrQuery);
        SolrDocumentList res = rsp.getResults();
        HashSet<String> allPhenotypes = new HashSet<String>();
        for (SolrDocument doc : res) {
            allPhenotypes.add((String) doc.getFieldValue(MpField.MP_TERM_ID));
        }
        return allPhenotypes;
    }

}
