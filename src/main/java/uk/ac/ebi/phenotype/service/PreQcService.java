/*******************************************************************************
 * Copyright 2015 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 *******************************************************************************/

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

public class PreQcService extends AbstractGenotypePhenotypeService {

    public PreQcService(String solrUrl, PhenotypePipelineDAO pipelineDao) {
        solr = new HttpSolrServer(solrUrl);
        pipelineDAO = pipelineDao;
        isPreQc = true;
    }

    public PreQcService() {
        super();
    }
    
    /**
     * Returns a list of <code>count GraphTestDTO</code> instances.
     *
     * @param count the number of <code>GraphTestDTO</code> instances to return
     *
     * @return a list of <code>count GraphTestDTO</code> instances.
     * 
     * @throws SolrServerException
     */
    public List<GraphTestDTO> getGeneAccessionIds(int count) throws SolrServerException {
        List<GraphTestDTO> retVal = new ArrayList();
        
        if (count < 1)
            return retVal;
        
        SolrQuery query = new SolrQuery();
        query
            .setQuery("*:*")
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
                
                SolrDocument doc = docs.get(0);                                 // All elements in this collection have the same mgi_accession_id.
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
