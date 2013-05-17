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
package uk.ac.ebi.phenotype.dao;

/**
 * 
 * Ontology DB data access manager implementation.
 * 
 * @author Gautier Koscielny (EMBL-EBI) <koscieln@ebi.ac.uk>
 * @since February 2012
 */

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OntodbDAOImpl extends HibernateDAOImpl implements OntodbDAO {

	@Autowired
	private SessionFactory sessionFactory;
	
	@Override
	public List<String> getAllCellTerms() {
		
		
		List<String> results = new LinkedList<String>();
		String sql = "SELECT distinct(name) FROM ma_terms";
		
		
		Connection connection = getConnection();
		Statement stmt;
		try {
			stmt = connection.createStatement();
		
		ResultSet rs = null;        
		rs = stmt.executeQuery(sql);
		while (rs.next()) {
		  results.add(rs.getString(1));
		}
		
		rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return results;
	}

}
