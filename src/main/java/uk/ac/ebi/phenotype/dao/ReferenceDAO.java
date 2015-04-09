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
import uk.ac.ebi.phenotype.service.dto.ReferenceDTO;

@Repository
public class ReferenceDAO {
    private Logger log = Logger.getLogger(this.getClass().getCanonicalName());

    public final String heading =
            "MGI allele symbol"
        + "\tMGI allele id"
        + "\tIMPC gene link"
        + "\tMGI allele name"
        + "\tTitle"
        + "\tjournal"
        + "\tPMID"
        + "\tDate of publication"
        + "\tGrant id"
        + "\tGrant agency"
        + "\tPaper link";
    
    
    @Autowired
    @Qualifier("admintoolsDataSource")
    private DataSource admintoolsDataSource;

    public ReferenceDAO() {
        
    }

    /**
     * Given a list of <code>ReferenceDTO</code> and a <code>pubMedId</code>,
     * returns the first matching ReferenceDTO matching pubMedId, if found; null
     * otherwise.
     * 
     * @param references a list of <code>ReferenceDTO</code>
     * 
     * @param pubMedId pub med ID
     * 
     * @return the first matching ReferenceDTO matching pubMedId, if found; null
     * otherwise.
     * 
     * @throws SQLException 
     */
    public ReferenceDTO getReferenceByPmid(List<ReferenceDTO> references, String pubMedId
    ) throws SQLException {
        for (ReferenceDTO reference : references) {
            if (reference.getPmid().equals(pubMedId)) {
                return reference;
            }
        }
        
        return null;
    }

    /**
     * Fetch all reference rows.
     *
     * @return all reference rows.
     *
     * @throws SQLException
     */
    public List<ReferenceDTO> getReferenceRows() throws SQLException {
        return getReferenceRows("");
    }

    /**
     * Fetch the reference rows, optionally filtered.
     *
     * @param filter Filter string, which may be null or empty, indicating no
     * filtering is desired. If supplied, a WHERE clause of the form "LIKE
     * '%<i>filter</i>%' is used in the query to query all fields for
     * <code>filter</code>.
     *
     * @return the reference rows, optionally filtered.
     *
     * @throws SQLException
     */
    public List<ReferenceDTO> getReferenceRows(String filter) throws SQLException {
        if (filter == null) 
            filter = "";
        
        String impcGeneBaseUrl = "http://www.mousephenotype.org/data/genes/";
        Connection connection = admintoolsDataSource.getConnection();
        String pmidsToOmit = getPmidsToOmit();
        String notInClause = (pmidsToOmit.isEmpty() ? "" : "  AND pmid NOT IN (" + pmidsToOmit + ")\n");
        String searchClause = "";
        if ( ! filter.isEmpty()) {
            searchClause = 
                "  AND (\n"
                + "     title               LIKE ?\n"
                + "  OR journal             LIKE ?\n"
                + "  OR acc                 LIKE ?\n"
                + "  OR symbol              LIKE ?\n"
                + "  OR pmid                LIKE ?\n"
                + "  OR date_of_publication LIKE ?\n"
                + "  OR grant_id            LIKE ?\n"
                + "  OR agency              LIKE ?\n"
                + "  OR acronym             LIKE ?)\n";
        }
        
        String whereClause =
                "WHERE\n"
              + "      reviewed = 'yes'\n"
              + "  AND symbol != ''\n"
              + notInClause
              + searchClause;
        
        String query =
                "SELECT\n"
              + "  GROUP_CONCAT(DISTINCT symbol    SEPARATOR \"|||\") AS alleleSymbols\n"
              + ", GROUP_CONCAT(DISTINCT acc       SEPARATOR \"|||\") AS alleleIds\n"
              + ", GROUP_CONCAT(DISTINCT gacc      SEPARATOR \"|||\") AS gaccs\n"
              + ", GROUP_CONCAT(DISTINCT name      SEPARATOR \"|||\") AS alleleNames\n"
              + ", title\n"
              + ", journal\n"
              + ", pmid\n"
              + ", date_of_publication\n"
              + ", GROUP_CONCAT(DISTINCT grant_id  SEPARATOR \"|||\") AS grantIds\n"
              + ", GROUP_CONCAT(DISTINCT agency    SEPARATOR \"|||\") AS agencies\n"
              + ", GROUP_CONCAT(DISTINCT paper_url SEPARATOR \"|||\") AS paperUrls\n"
              + "FROM allele_ref AS ar\n"
              + whereClause
              + "GROUP BY pmid\n"
              + "ORDER BY date_of_publication DESC\n";
        
        List<ReferenceDTO> results = new ArrayList<>();
        
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            if ( ! searchClause.isEmpty()) {
                // Replace the 9 parameter holder ? with the values.
                String like = "%" + filter + "%";
                for (int i = 0; i < 9; i++) {                                   // If a search clause was specified, load the parameters.
                    ps.setString(i + 1, like);
                }
            }
        
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                final String delimeter = "\\|\\|\\|";
                ReferenceDTO referenceRow = new ReferenceDTO();
                
                referenceRow.setAlleleSymbols((Arrays.asList(resultSet.getString("alleleSymbols").split(delimeter))));
                referenceRow.setAlleleIds((Arrays.asList(resultSet.getString("alleleIds").split(delimeter))));
                String gacc = resultSet.getString("gaccs").trim();
                List<String> geneLinks = new ArrayList();
                if ( ! gacc.isEmpty()) {
                    String[] parts = gacc.split(delimeter);
                    for (String s : parts) {
                        geneLinks.add(impcGeneBaseUrl + s.trim());
                    }
                    referenceRow.setImpcGeneLinks(geneLinks);
                }
                referenceRow.setMgiAlleleNames((Arrays.asList(resultSet.getString("alleleNames").split(delimeter))));
                referenceRow.setTitle(resultSet.getString("title"));
                referenceRow.setJournal(resultSet.getString("journal"));
                referenceRow.setPmid(resultSet.getString("pmid"));
                referenceRow.setDateOfPublication(resultSet.getString("date_of_publication"));
                referenceRow.setGrantIds((Arrays.asList(resultSet.getString("grantIds").split(delimeter))));
                referenceRow.setGrantAgencies((Arrays.asList(resultSet.getString("agencies").split(delimeter))));
                
                String paperUrls = resultSet.getString("paperUrls").trim();
                List<String> paperLinks = new ArrayList();
                if ( ! paperUrls.isEmpty()) {
                    String[] parts = paperUrls.split(delimeter);
                    for (String s : parts) {
                        String[] parts2 = s.split(",");
                        paperLinks.addAll(Arrays.asList(parts2));
                    }
                    referenceRow.setPaperLinks(paperLinks);
                }
                
                results.add(referenceRow);
            }
            resultSet.close();
            ps.close();
            connection.close();
            
        } catch (Exception e) {
            log.error("download rowData extract failed: " + e.getLocalizedMessage());
            e.printStackTrace();
        }
        
        return results;
    }
    
    /**
     * Returns a comma-separated, single-quoted list of pmid values to omit.
     * This is meant to be a filter to omit any common distinct papers. Rows
     * with more than MAX_ROWS distinct paper references are excluded from the
     * download row set.
     *
     * @return a comma-separated list of pmid values to omit.
     */
    public String getPmidsToOmit() throws SQLException {
        StringBuilder retVal = new StringBuilder();
        
        // Filter to omit any common distinct papers. Rows with more than MAX_ROWS distinct papers are excluded.
        final int MAX_PAPERS = 140;
        
        Connection connection = admintoolsDataSource.getConnection();
        String query = "SELECT pmid FROM allele_ref ar GROUP BY pmid HAVING (SELECT COUNT(symbol) FROM allele_ref ar2 WHERE ar2.pmid = ar.pmid) > " + MAX_PAPERS;

        try (PreparedStatement ps = connection.prepareStatement(query)) {

            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                if (retVal.length() > 0) {
                    retVal.append(", ");
                }
                retVal.append("'").append(rs.getString("pmid")).append("'");
            }
            rs.close();
            ps.close();
            connection.close();
        } catch (SQLException e) {
            log.error("getPmidsToOmit() call failed: " + e.getLocalizedMessage());
            e.printStackTrace();
        }
        
        return retVal.toString();
    }
}
