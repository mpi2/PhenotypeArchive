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
			return srs.getAccessionsByResourceName("MGP");
		}
		return null;
	}


	@Override
	public List<GeneRowForHeatMap> getGeneRowsForHeatMap(HttpServletRequest request)
	throws SolrServerException {

		List<GeneRowForHeatMap> geneRows = new ArrayList<>();
	
		System.out.println("-----getGeneHeatMap called");

		Map<String, Set<String>> geneToProcedureMap = srs.getAccessionProceduresMap("MGP");
		Set<String> accessions = geneToProcedureMap.keySet();
		System.out.println("Accessions found " + accessions.size());
		Map<String, String> geneToMouseStatusMap = gs.getProductionStatusForGeneSet(accessions, request);

		for (String accession : accessions) {
			GenomicFeature gene = genesDao.getGenomicFeatureByAccession(accession);

			GeneRowForHeatMap row = srs.getResultsForGeneHeatMap(accession, gene, geneToProcedureMap);
			System.out.println(" -- gene " + accession + " -- cell -- " + row);
			if (geneToMouseStatusMap.containsKey(accession)) {
				row.setMiceProduced(geneToMouseStatusMap.get(accession));
				if (row.getMiceProduced().equals("Neither production nor phenotyping status available ")) {//note the space on the end - why we should have enums
						for (HeatMapCell cell : row.getXAxisToCellMap().values()) {
							cell.setStatus("No Data Available");
						}
					}
				} else {
					row.setMiceProduced("No");
				}
				geneRows.add(row);	
		}
			
//		Collections.sort(geneRows);
		return geneRows;
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
