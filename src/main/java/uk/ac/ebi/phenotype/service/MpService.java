package uk.ac.ebi.phenotype.service;

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
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.stereotype.Service;

import uk.ac.ebi.phenotype.service.GenotypePhenotypeService.GenotypePhenotypeField;
import uk.ac.ebi.phenotype.web.pojo.BasicBean;

@Service
public class MpService {

    private HttpSolrServer solr;

    private Logger log = Logger.getLogger(this.getClass().getCanonicalName());

    private static String TOP_LEVEL_MP_TERM="top_level_mp_term";
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
    
    public Set<BasicBean> getAllTopLevelPhenotypesAsBasicBeans() throws SolrServerException{
    	
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.addFacetField( "top_level_mp_term_id");
		solrQuery.setRows(0);
		QueryResponse rsp = solr.query(solrQuery);
		System.out.println("solr query in basicbean="+solrQuery);
		SolrDocumentList res = rsp.getResults();
		
		HashSet<BasicBean> allTopLevelPhenotypes = new HashSet();
		for (FacetField ff:rsp.getFacetFields()){
			System.out.println("count name="+ff.getName());
			for(Count count: ff.getValues()){
				System.out.println("count Name="+count.getName());
				String mpArray[]=count.getName().split("___");
				BasicBean bean=new BasicBean();
				bean.setName(mpArray[0]);
				bean.setId(mpArray[1]);
				allTopLevelPhenotypes.add(bean);
			}
			//ArrayList<String> names = (ArrayList<String>)doc.getFieldValue( GenotypePhenotypeField.MP_TERM_NAME );
//			for (String id : ids) {
//				BasicBean bean=new BasicBean();
//				bean.setId(id);
//				allTopLevelPhenotypes.add(bean);
//			}
		}
		return allTopLevelPhenotypes;
	}

}
