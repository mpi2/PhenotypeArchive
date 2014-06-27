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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.response.PivotField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.util.NamedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class BasicService {

	private static final Logger LOG = LoggerFactory.getLogger(BasicService.class);
    
	
	protected static final int MAX_NB_DOCS = 1000000;
	
    /**
     * Recursive method to fill a map with multiple combination of pivot fields.
     * Each pivot level can have multiple children. Hence, each level should
     * pass back to the caller a list of all possible combination
     * 
     * @param pivotLevel
     * @param map
     */
    protected List<Map<String, String>> getLeveledFacetPivotValue(PivotField pivotLevel, PivotField parentPivot, boolean keepCount) {

        List<Map<String, String>> results = new ArrayList<Map<String, String>>();

        List<PivotField> pivotResult = pivotLevel.getPivot();
        if (pivotResult != null) {
            for (int i = 0; i < pivotResult.size(); i++) {
                List<Map<String, String>> lmap = getLeveledFacetPivotValue(pivotResult.get(i), pivotLevel, keepCount);
                
                // add the parent pivot
                if (parentPivot != null) {
                    for (Map<String, String> map : lmap) {
                        map.put(parentPivot.getField(), parentPivot.getValue().toString());
                        if (keepCount)
                        	map.put(parentPivot.getField() + "_count", new Integer(parentPivot.getCount()).toString());
                    }
                }
                results.addAll(lmap);
            }
        } else {
            Map<String, String> map = new HashMap<String, String>();
            map.put(pivotLevel.getField(), pivotLevel.getValue().toString());
            if (keepCount)
            	map.put(pivotLevel.getField() + "_count", new Integer(pivotLevel.getCount()).toString());
            // add the parent pivot
            if (parentPivot != null) {
                map.put(parentPivot.getField(), parentPivot.getValue().toString());
                if (keepCount)
                	map.put(parentPivot.getField() + "_count", new Integer(parentPivot.getCount()).toString());
            }
            results.add(map);
        }
        //
        return results;
    }

	
    /**
     * Unwrap results from a facet pivot solr query and return the flattened
     * list of maps of results
     * 
     * @param response
     *            list of maps
     * @return
     */
    protected List<Map<String, String>> getFacetPivotResults(QueryResponse response, boolean keepCount) {
        List<Map<String, String>> results = new LinkedList<Map<String, String>>();
        NamedList<List<PivotField>> facetPivot = response.getFacetPivot();

        if (facetPivot != null && facetPivot.size() > 0) {
            for (int i = 0; i < facetPivot.size(); i++) {

                String name = facetPivot.getName(i); // in this case only one of
                                                     // them
                LOG.debug("facetPivot name" + name);
                List<PivotField> pivotResult = facetPivot.get(name);

                // iterate on results
                for (int j = 0; j < pivotResult.size(); j++) {

                    // create a HashMap to store a new triplet of data

                    PivotField pivotLevel = pivotResult.get(j);
                    List<Map<String, String>> lmap = getLeveledFacetPivotValue(pivotLevel, null, keepCount);
                    results.addAll(lmap);
                }
            }
        }

        return results;
    }
    
}
