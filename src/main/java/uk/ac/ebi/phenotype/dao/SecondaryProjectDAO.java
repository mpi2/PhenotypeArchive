/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.ebi.phenotype.dao;

import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author jwarren
 */
public interface SecondaryProjectDAO {

    List<String> getAccessionsBySecondaryProjectId(int projectId)throws SQLException;
    
}
