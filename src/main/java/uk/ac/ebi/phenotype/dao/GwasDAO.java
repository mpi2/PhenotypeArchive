/**
 * Copyright © 2015 EMBL - European Bioinformatics Institute
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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import uk.ac.ebi.phenotype.service.dto.GwasDTO;

@Repository
public class GwasDAO {
	
    private Logger log = Logger.getLogger(this.getClass().getCanonicalName());

    @Autowired
    @Qualifier("admintoolsDataSource")
    //@Qualifier("admintoolsDataSourceLocal")
    private DataSource admintoolsDataSource;

    public GwasDAO() {
        
    }

    /**
     * 
     * 
     * @throws SQLException 
     */
    public List<GwasDTO> getGwasMappingByGeneSymbol(List<GwasDTO> gwasMappings, String mgiGeneSymbol) throws SQLException {
    	
    	List<GwasDTO> mappedList = new ArrayList<>(); 
    	
        for (GwasDTO gwasMapping : gwasMappings) {
            if (gwasMapping.getMgiGeneSymbol().equals(mgiGeneSymbol)) {
                mappedList.add(gwasMapping);
            }
            
            return mappedList;
        }
        
        return null;
    }

    /**
     * Fetch all gwas mapping rows.
     *
     * @return all gwas mapping rows.
     *
     * @throws SQLException
     */
    public List<GwasDTO> getGwasMappingRows() throws SQLException {
        return getGwasMappingRows("");
    }

    /**
     * Fetch the GWAS Mapping rows, optionally filtered with mgi_gene_symbol.
     *
     * @param String mgiGeneSymbol, which may be null or empty, indicating no
     * filtering by gene symbol.
     *
     * @return the GWAS Mapping rows, optionally filtered with mgi_gene_symbol
     *
     * @throws SQLException
     */
    public List<GwasDTO> getGwasMappingRows(String mgiGeneSymbol) throws SQLException {
    	
    	Connection connection = admintoolsDataSource.getConnection();
    	// need to set max length for group_concat() otherwise some values would get chopped off !!

    	//String gcsql = "SET SESSION GROUP_CONCAT_MAX_LEN = 100000000";
    	//PreparedStatement pst = connection.prepareStatement(gcsql);
    	//pst.executeQuery();
    	
        String impcGeneBaseUrl = "http://www.mousephenotype.org/data/genes/";
       
        String whereClause = null;
        String query = null;
        
        if ( ! mgiGeneSymbol.isEmpty() ) {
        	query = "SELECT * FROM impc2gwas WHERE mgi_gene_symbol = ?";
        }
        else {
        	query = "SELECT * FROM impc2gwas";
        }
        
        
//        if ( ! filter.isEmpty()) {
//        	whereClause = 
//                  "  WHERE mgi_gene_id          LIKE ?\n"
//                + "  OR mgi_symbol              LIKE ?\n"
//                + "  OR mgi_allele_id           LIKE ?\n"
//                + "  OR in_gwas                 LIKE ?\n"
//                + "  OR pheno_mapping_category  LIKE ?\n"
//                + "  OR gwas_disease_trait      LIKE ?\n"
//                + "  OR gwas_reported_gene      LIKE ?\n"
//                + "  OR gwas_mapped_gene        LIKE ?\n"
//                + "  OR gwas_upstream_gene      LIKE ?)\n"
//                + "  OR gwas_downstream_gene    LIKE ?\n"
//                + "  OR mp_term_id              LIKE ?\n"
//                + "  OR mp_term_name            LIKE ?)\n"
//                + "  OR impc_mouse_gender       LIKE ?)\n";
//        	
//        	query = "SELECT * FROM impc2gwas" + filter;
//        }

        
        //System.out.println("gwas mapping query: " + query);
        
        List<GwasDTO> results = new ArrayList<>();
        
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            if ( ! mgiGeneSymbol.isEmpty()) {
                // Replace parameter holder ? with the value.
            	ps.setString(1, mgiGeneSymbol);
            }
        
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
               
                GwasDTO gwasMappingRow = new GwasDTO();
                
                gwasMappingRow.setMgiGeneId(resultSet.getString("mgi_gene_id"));
                gwasMappingRow.setMgiGeneSymbol(resultSet.getString("mgi_gene_symbol"));
                gwasMappingRow.setMgiAlleleId(resultSet.getString("mgi_allele_id"));
                gwasMappingRow.setMgiAlleleName(resultSet.getString("mgi_allele_name"));
                gwasMappingRow.setPhenoMappingCategory(resultSet.getString("pheno_mapping_category"));
                gwasMappingRow.setDiseaseTrait(resultSet.getString("gwas_disease_trait"));
                gwasMappingRow.setPvalue(resultSet.getFloat("gwas_p_value"));
                gwasMappingRow.setReportedGene(resultSet.getString("gwas_reported_gene"));
                gwasMappingRow.setMappedGene(resultSet.getString("gwas_mapped_gene"));
                gwasMappingRow.setUpstreamGene(resultSet.getString("gwas_upstream_gene"));
                gwasMappingRow.setDownstreamGene(resultSet.getString("gwas_downstream_gene"));
                gwasMappingRow.setMpTermId(resultSet.getString("mp_term_id"));
                gwasMappingRow.setMpTermName(resultSet.getString("mp_term_name"));
                gwasMappingRow.setMouseGender(resultSet.getString("impc_mouse_gender"));
                
                results.add(gwasMappingRow);
            }
            resultSet.close();
            ps.close();
            connection.close();
            
        } catch (Exception e) {
            log.error("Fetch IMPC GWAS mapping data failed: " + e.getLocalizedMessage());
            e.printStackTrace();
        }
        
        return results;
    }
   
}
