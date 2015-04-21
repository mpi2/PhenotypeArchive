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
package uk.ac.ebi.phenotype.service;

import java.util.ArrayList;
import java.util.List;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.GroupCommand;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import uk.ac.ebi.phenotype.service.dto.GraphTestDTO;
import uk.ac.ebi.phenotype.dao.PhenotypePipelineDAO;

public class PostQcService extends AbstractGenotypePhenotypeService {

    public PostQcService(String solrUrl, PhenotypePipelineDAO pipelineDao) {
        solr = new HttpSolrServer(solrUrl);
        pipelineDAO = pipelineDao;
        isPreQc = false;
    }

    public PostQcService() {
        super();
    }
    
    /**
     * Returns a list of <code>count GraphTestDTO</code> instances matching the
     * given parameter stable ids.
     *
     * @param parameterStableIds a list of parameter stable ids used to feed the
     *                           query
     * @param count the number of <code>GraphTestDTO</code> instances to return
     *
     * @return a list of <code>count GraphTestDTO</code> instances matching the
     * given parameter stable ids.
     * 
     * @throws SolrServerException
     */
    public List<GraphTestDTO> getGeneAccessionIdsByParameterStableId(List<String> parameterStableIds, int count) throws SolrServerException {
        List<GraphTestDTO> retVal = new ArrayList();
        
        if (count < 1)
            return retVal;
        
        String queryString = "";
        for (String parameterStableId : parameterStableIds) {
            if ( ! queryString.isEmpty()) {
                queryString += " OR ";
            }
            queryString += "parameter_stable_id:" + parameterStableId;
        }
        SolrQuery query = new SolrQuery();
        // http://ves-ebi-d0:8090/mi/impc/dev/solr/experiment/select?q=observation_type%3Acategorical&rows=12&wt=json&indent=true&facet=true&facet.field=parameter_stable_id
        query
            .setQuery(queryString)
            .setRows(count)
            .setFields("parameter_stable_id, marker_accession_id", "procedure_name", "parameter_name")
            .add("group", "true")
            .add("group.field", "marker_accession_id")
            .add("group.limit", Integer.toString(count))
        ;
        
        QueryResponse response = solr.query(query);
        List<GroupCommand> groupResponse = response.getGroupResponse().getValues();
        for (GroupCommand groupCommand : groupResponse) {
            List<Group> groups = groupCommand.getValues();
            for (Group group : groups) {
                SolrDocumentList docs = group.getResult();
                
                SolrDocument doc = docs.get(0);                                                    // All elements in this collection have the same mgi_accession_id.
                GraphTestDTO geneGraph = new GraphTestDTO();
                geneGraph.setParameterStableId((String)doc.get("parameter_stable_id"));
                geneGraph.setMgiAccessionId((String)doc.get("marker_accession_id"));
                geneGraph.setParameterName((String)doc.get("parameter_name"));
                geneGraph.setProcedureName((String)doc.get("procedure_name"));
                retVal.add(geneGraph);
                count--;
                if (count == 0) {
                    return retVal;
                }
            }
        }
        
        return retVal;
    }

}
