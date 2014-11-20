/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.ebi.phenotype.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.solr.client.solrj.SolrServerException;

import uk.ac.ebi.phenotype.pojo.Parameter;
import uk.ac.ebi.phenotype.web.pojo.BasicBean;
import uk.ac.ebi.phenotype.web.pojo.GeneRowForHeatMap;

/**
 *
 * @author jwarren
 */
public interface SecondaryProjectDAO {

    Set<String> getAccessionsBySecondaryProjectId(String projectId)throws SQLException;
    
    List<GeneRowForHeatMap> getGeneRowsForHeatMap(HttpServletRequest request) throws SolrServerException;
    
    List<BasicBean> getXAxisForHeatMap();
    
}
