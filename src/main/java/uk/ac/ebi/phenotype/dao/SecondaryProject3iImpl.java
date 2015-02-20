package uk.ac.ebi.phenotype.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import uk.ac.ebi.phenotype.pojo.GenomicFeature;
import uk.ac.ebi.phenotype.pojo.Parameter;
import uk.ac.ebi.phenotype.service.GeneService;
import uk.ac.ebi.phenotype.service.MpService;
import uk.ac.ebi.phenotype.service.PostQcService;
import uk.ac.ebi.phenotype.service.StatisticalResultService;
import uk.ac.ebi.phenotype.web.controller.GeneHeatmapController;
import uk.ac.ebi.phenotype.web.pojo.BasicBean;
import uk.ac.ebi.phenotype.web.pojo.GeneRowForHeatMap;
import uk.ac.ebi.phenotype.web.pojo.HeatMapCell;

/**
 * 
 * @author tudose
 *
 */

public class SecondaryProject3iImpl implements SecondaryProjectDAO {

	
	@Autowired 
	StatisticalResultService srs;
	
	@Autowired 
	GeneService gs;
	
	@Autowired
	private GenomicFeatureDAO genesDao;

	@Autowired
	@Qualifier("postqcService")
	private PostQcService gps;
	

	@Autowired
	private MpService mpService;
	
	@Override
	public Set<String> getAccessionsBySecondaryProjectId(String projectId)
	throws SQLException {

		if (projectId.equalsIgnoreCase(SecondaryProjectDAO.SecondaryProjectIds.threeI.name())){
			return srs.getAccessionsByResourceName("3i");
		}
		return null;
	}


	@Override
	public List<GeneRowForHeatMap> getGeneRowsForHeatMap(HttpServletRequest request)
	throws SolrServerException {

/*		List<GeneRowForHeatMap> geneRows = new ArrayList<>();
		Map<String, Set<String>> geneToProcedureMap = srs.getAccessionProceduresMap("3i");
		Set<String> accessions = geneToProcedureMap.keySet();

		for (String accession : accessions) {			
			GenomicFeature gene = genesDao.getGenomicFeatureByAccession(accession);
			GeneRowForHeatMap row = srs.getResultsForGeneHeatMap(accession, gene, geneToProcedureMap, "3i");
			geneRows.add(row);	
		}
		return geneRows;
*/
	
		return  srs.getSecondaryProjectMapForResource("3i");
	}


	@Override
	public List<BasicBean> getXAxisForHeatMap() {
		
		List<BasicBean> mp = new ArrayList<>();
		try {
			Set<BasicBean> topLevelPhenotypes = mpService.getAllTopLevelPhenotypesAsBasicBeans();
			mp.addAll(topLevelPhenotypes);
		} catch (SolrServerException e) {
			e.printStackTrace();
		}
		return mp;
	}

}
