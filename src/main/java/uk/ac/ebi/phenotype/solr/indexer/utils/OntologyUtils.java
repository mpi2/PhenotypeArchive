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

package uk.ac.ebi.phenotype.solr.indexer.utils;

import uk.ac.ebi.phenotype.solr.indexer.beans.ImpressBean;
import uk.ac.ebi.phenotype.solr.indexer.beans.OrganisationBean;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 *
 * @author mrelac
 */
public class OntologyUtils {
    
    /**
     * Return relevant impress pipeline map
     *
     * @param connection The database connection
     * 
     * @return a map, indexed by primary key, of <code>ImpressBean</code> instances.
     * 
     * @throws java.sql.SQLException when a database exception occurs
     */
    public static Map<Integer, ImpressBean> populateImpressPipeline(Connection connection) throws SQLException {
        Map<Integer, ImpressBean> impressMap;

        String query = "SELECT id, stable_key, name, stable_id FROM phenotype_pipeline";
        try (PreparedStatement p = connection.prepareStatement(query)) {

            impressMap = populateImpressMap(p);
        }

        return impressMap;
    }

    /**
     * Return relevant impress procedure map
     *
     * @param connection The database connection
     * 
     * @return a map, indexed by primary key, of <code>ImpressBean</code> instances.
     *
     * @throws java.sql.SQLException when a database exception occurs
     */
    public static Map<Integer, ImpressBean> populateImpressProcedure(Connection connection) throws SQLException {
        Map<Integer, ImpressBean> impressMap;

        String query = "SELECT id, stable_key, name, stable_id FROM phenotype_procedure";

        try (PreparedStatement p = connection.prepareStatement(query)) {

            impressMap = populateImpressMap(p);
        }

        return impressMap;
    }

    /**
     * Return relevant impress parameter map
     *
     * @param connection The database connection
     * 
     * @return a map, indexed by primary key, of <code>ImpressBean</code> instances.
     *
     * @throws java.sql.SQLException when a database exception occurs
     */
    public static Map<Integer, ImpressBean> populateImpressParameter(Connection connection) throws SQLException {
        Map<Integer, ImpressBean> impressMap;

        String query = "SELECT id, stable_key, name, stable_id FROM phenotype_parameter";

        try (PreparedStatement p = connection.prepareStatement(query)) {

            impressMap = populateImpressMap(p);
        }

        return impressMap;
    }

    /**
     * Populate the map with organisation beans
     *
     * @param connection The database connection
     * 
     * @return a map, indexed by internal db id, o
     * <code>OrganisationBean</code> instances.
     *
     * @throws SQLException when a database error occurs
     */
    public static Map<Integer, OrganisationBean> populateOrganisationMap(Connection connection) throws SQLException {

        Map<Integer, OrganisationBean> map = new HashMap<>();
        String query = "SELECT id, name, fullname, country FROM organisation";
        try (PreparedStatement p = connection.prepareStatement(query)) {

            ResultSet resultSet = p.executeQuery();
            while (resultSet.next()) {
                OrganisationBean b = new OrganisationBean();

                b.setId(resultSet.getInt("id"));
                b.setName(resultSet.getString("name"));
                b.setFullname(resultSet.getString("fullname"));
                b.setCountry(resultSet.getString("country"));
                map.put(b.getId(), b);
            }
        }

        return map;
    }


    // PRIVATE METHODS

    
    private static Map<Integer, ImpressBean> populateImpressMap(PreparedStatement p) throws SQLException {

        Map<Integer, ImpressBean> impressMap = new HashMap<>();

        ResultSet resultSet = p.executeQuery();

        while (resultSet.next()) {
            ImpressBean b = new ImpressBean();

            b.id = resultSet.getInt("id");
            b.stableKey = resultSet.getString("stable_key");
            b.stableId = resultSet.getString("stable_id");
            b.name = resultSet.getString("name");

            impressMap.put(resultSet.getInt("id"), b);
        }

        return impressMap;
    }
}