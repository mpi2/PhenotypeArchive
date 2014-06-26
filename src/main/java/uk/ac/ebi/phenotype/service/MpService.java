/**
 * Copyright © 2011-2014 EMBL - European Bioinformatics Institute
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License.  
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.phenotype.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

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

import uk.ac.ebi.phenotype.web.pojo.BasicBean;

@Service
public class MpService {

    private HttpSolrServer solr;

    private Logger log = Logger.getLogger(this.getClass().getCanonicalName());


    public static final class MpField {
        public final static String MP_TERM_ID = "mp_id";
        private static String TOP_LEVEL_MP_TERM = "top_level_mp_term";
        private static String CHILD_MP_ID = "child_mp_id";
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
			//System.out.println("count name="+ff.getName());
			for(Count count: ff.getValues()){
				//System.out.println("count Name="+count.getName());
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

    public ArrayList<String> getChildrenFor(String mpId) throws SolrServerException{
    
    	SolrQuery solrQuery = new SolrQuery();
    	solrQuery.setQuery(MpField.MP_TERM_ID + ":\"" + mpId + "\"");
    	solrQuery.setFields(MpField.CHILD_MP_ID);
		QueryResponse rsp = solr.query(solrQuery);
		SolrDocumentList res = rsp.getResults();
		
		System.out.println(solr.getBaseURL() + "/select?" + solrQuery);
		ArrayList<String> children = new ArrayList<String>();
		
        for (SolrDocument doc : res) {
        	System.out.println(doc);
        	if (doc.containsKey(MpField.CHILD_MP_ID)){
        		for (Object child: doc.getFieldValues(MpField.CHILD_MP_ID)){
        			children.add((String)child);
        		}
        	}
        }
        return children;
    }
    
}
