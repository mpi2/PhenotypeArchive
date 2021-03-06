/*******************************************************************************
 * Copyright 2015 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 *******************************************************************************/
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.ebi.phenotype.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.apache.solr.client.solrj.SolrServerException;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import uk.ac.ebi.phenotype.pojo.GenomicFeature;
import uk.ac.ebi.phenotype.pojo.Parameter;
import uk.ac.ebi.phenotype.service.GeneService;
import uk.ac.ebi.phenotype.service.PostQcService;
import uk.ac.ebi.phenotype.service.MpService;
import uk.ac.ebi.phenotype.web.controller.GeneHeatmapController;
import uk.ac.ebi.phenotype.web.pojo.BasicBean;
import uk.ac.ebi.phenotype.web.pojo.GeneRowForHeatMap;
import uk.ac.ebi.phenotype.web.pojo.HeatMapCell;

/**
 * 
 * @author jwarren
 */
class SecondaryProjectIdgImpl extends HibernateDAOImpl implements SecondaryProjectDAO {

	@Autowired
	private GeneService geneService;

	@Autowired
	private GenomicFeatureDAO genesDao;

	@Autowired
	@Qualifier("postqcService")
	private PostQcService genotypePhenotypeService;

	@Autowired
	private MpService mpService;

	@Autowired
	private PhenotypePipelineDAO pDAO;

	/**
	 * Creates a new Hibernate project data access manager.
	 * 
	 * @param sessionFactory
	 *            the Hibernate session factory
	 */
	public SecondaryProjectIdgImpl(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public Set<String> getAccessionsBySecondaryProjectId(String projectId)
			throws SQLException {
		Set<String> accessions = new TreeSet<>();

		String query = "select * from genes_secondary_project where secondary_project_id="
				+ "\"" + projectId + "\"";// +" limit 10";

		try (PreparedStatement statement = getConnection().prepareStatement(
				query)) {

			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				String result = resultSet.getString(1);
				System.out.println("result +++ " +  result);
				accessions.add(result);
			}
		}
		// accessions.add("MGI:104874");//just for testing as no others seem to
		// have mice produced so far for idg
		// accessions.add("MGI:2683087");
		return accessions;
	}

	@Override
	public List<GeneRowForHeatMap> getGeneRowsForHeatMap(HttpServletRequest request)
			throws SolrServerException {
		List<GeneRowForHeatMap> geneRows = new ArrayList<>();
		List<BasicBean> parameters = this.getXAxisForHeatMap();

		try {
			System.out.println("getGeneHeatMap called");
			// get a list of genes for the project - which will be the row
			// headers
			Set<String> accessions = this
					.getAccessionsBySecondaryProjectId("idg");
			Map<String, String> geneToMouseStatusMap = geneService
					.getProductionStatusForGeneSet(accessions, request);
			Map<String, List<String>> geneToTopLevelMpMap = geneService
					.getTopLevelMpForGeneSet(accessions);
			// for(String key: geneToMouseStatusMap.keySet()){
			// System.out.println("key="+key+"  value="+geneToMouseStatusMap.get(key));
			// }
			for (String accession : accessions) {
				// System.out.println("accession="+accession);
				GenomicFeature gene = genesDao
						.getGenomicFeatureByAccession(accession);
				// get a data structure with the gene accession,with parameter
				// associated with a Value or status ie. not phenotyped, not
				// significant
				GeneRowForHeatMap row = genotypePhenotypeService
						.getResultsForGeneHeatMap(accession, gene, parameters,
								geneToTopLevelMpMap);
				if (geneToMouseStatusMap.containsKey(accession)) {
					row.setMiceProduced(geneToMouseStatusMap.get(accession));
					//System.out.println("Mice produced=|"+row.getMiceProduced()+"|");
					if (row.getMiceProduced().equals("Neither production nor phenotyping status available ")) {//note the space on the end - why we should have enums
						for (HeatMapCell cell : row.getXAxisToCellMap().values()) {
							// set all the cells to No Data Available
							cell.setStatus("No Data Available");
						}
					}
				} else {
					row.setMiceProduced("No");// if not contained in map just
												// set no to mice produced
				}
				geneRows.add(row);
			}
			// model.addAttribute("heatmapCode", fillHeatmap(hdto));

		} catch (SQLException ex) {
			Logger.getLogger(GeneHeatmapController.class.getName()).log(
					Level.SEVERE, null, ex);
		}
		Collections.sort(geneRows);
		return geneRows;
	}

	@Override
	public List<BasicBean> getXAxisForHeatMap() {
		List<BasicBean> mp = new ArrayList<>();
		try {
			Set<BasicBean> topLevelPhenotypes = mpService
					.getAllTopLevelPhenotypesAsBasicBeans();
			mp.addAll(topLevelPhenotypes);
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mp;
	}
}
