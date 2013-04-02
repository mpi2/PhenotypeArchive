/**
 * Copyright © 2011-2013 EMBL - European Bioinformatics Institute
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
package uk.ac.ebi.phenotype.imaging.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
/**
 * Class for use by the CreateSolrImagesIndexes class that gives us higher level terms for lower level terms
 * @author jwarren
 *
 */
public class MaHigherLevelOntologyDao extends NamedParameterJdbcDaoSupport{

	public List<Map<String,Object>> getHigherLevelTerm(String lowerLevelTerm){
		
		List<Map<String, Object>>firstResult=null;
		//String cmd = "SELECT * FROM `mp_node2term` WHERE term_id=:mpTerm";
		//
		//String cmd="SELECT * FROM `mp_node2term` mp , mp_node_top_level tl WHERE term_id=:mpTerm and mp.node_id=tl.node_id";
		String cmd="SELECT * FROM lower_to_higher_level_annotation l, higher_level_annotation h	WHERE l.higher_term_id = h.term_id and  l.term_id=:maTerm";
		Map<String, String> params=new HashMap<String ,String>();
		params.put("maTerm", lowerLevelTerm);
		try {
			firstResult= getNamedParameterJdbcTemplate().queryForList(cmd, params);
		} catch (DataAccessException e) {
			
			e.printStackTrace();
			
		}
		//for(Map<String, Object> row:firstResult){
		//	System.out.println(row.keySet());
		//}
		
		return firstResult;
		
	}

	
}
