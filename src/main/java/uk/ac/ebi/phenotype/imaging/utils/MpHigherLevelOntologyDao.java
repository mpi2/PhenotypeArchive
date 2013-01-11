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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;

/**
 * Just a helper class to write solr fields in xml document for posting to solr to create index
 * @Author jw12
 */
public class MpHigherLevelOntologyDao extends NamedParameterJdbcDaoSupport{

	public Map<String,String> getHigherLevelTerm(String lowerLevelTerm){
		
		List<Map<String, Object>>firstResult=null;
		//String cmd = "SELECT * FROM `mp_node2term` WHERE term_id=:mpTerm";
		String cmd="SELECT * FROM `mp_node2term` mp , mp_node_top_level tl WHERE term_id=:mpTerm and mp.node_id=tl.node_id";
		Map<String, String> params=new HashMap<String ,String>();
		params.put("mpTerm", lowerLevelTerm);
		try {
			firstResult= getNamedParameterJdbcTemplate().queryForList(cmd, params);
		} catch (DataAccessException e) {
			
			e.printStackTrace();
			
		}
		
		return getMpTerms(firstResult);
		
	}

	private Map<String,String> getMpTerms(List<Map<String, Object>> firstResult) {
		Map<String,String> higherTerms=new HashMap();
		for(Map <String,Object>map:firstResult){
			Integer topLevelNodeId=(Integer) map.get("top_level_node_id");
			//System.out.println("topLevelNOdeId="+topLevelNodeId);
			Map<String, String> params=new HashMap<String ,String>();
			params.put("topLevelNodeId", topLevelNodeId.toString());
			//String cmd="SELECT * FROM `mp_node2term` mp WHERE node_id=:topLevelNodeId";
			String cmd="SELECT * FROM `mp_node2term` mp , `mp_term_infos` inf WHERE mp.node_id=:topLevelNodeId and inf.term_id=mp.term_id";
			List<Map<String, Object>> termIdResults= getNamedParameterJdbcTemplate().queryForList(cmd, params);
			for(Map <String, Object>higherTermMap:termIdResults){
				String higherTermId=(String) higherTermMap.get("term_id");
				//System.out.println(higherTermId);
				String higherTermName=(String) higherTermMap.get("name");
				//System.out.println(higherTermName);
				higherTerms.put(higherTermId,higherTermName);
			}
			
			
		}
		return higherTerms;
	}
}
