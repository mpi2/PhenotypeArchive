/**
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
/**
 * Copyright © 2014 EMBL - European Bioinformatics Institute
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

package uk.ac.ebi.phenotype.solr.indexer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import uk.ac.ebi.phenotype.service.dto.ImageDTO;
import static uk.ac.ebi.phenotype.solr.indexer.OntologyUtil.BATCH_SIZE;

/**
 *
 * @author mrelac
 */
public class SolrUtils {

    
    /**
     * Fetch a map of image terms indexed by ma id
     * 
     * @param imagesCore a valid solr connection
     * @return a map, indexed by child ma id, of all parent terms with associations
     * to child terms
     */
    public static  Map<String, List<ImageDTO>> populateImageBean(SolrServer imagesCore) throws SolrServerException {
        Map<String, List<ImageDTO>> map = new HashMap();

        int pos = 0;
        long total = Integer.MAX_VALUE;
        SolrQuery query = new SolrQuery("q=maTermId:*");
        query.setRows(BATCH_SIZE);
        while (pos < total) {
            query.setStart(pos);
            QueryResponse response = imagesCore.query(query);
            total = response.getResults().getNumFound();
            List<ImageDTO> imageList = response.getBeans(ImageDTO.class);
            for (ImageDTO image : imageList) {
                if ( ! map.containsKey(image.getMaTermId())) {
                    map.put(image.getMaTermId(), new ArrayList<ImageDTO>());
                }
                map.get(image.getMaTermId()).add(image);
            }
            pos += BATCH_SIZE;
        }

        return map;
    }
    
}
