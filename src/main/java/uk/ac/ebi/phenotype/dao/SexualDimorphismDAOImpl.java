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
package uk.ac.ebi.phenotype.dao;

import org.apache.commons.lang.StringUtils;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.phenotype.chart.ChartUtils;
import uk.ac.ebi.phenotype.pojo.ZygosityType;
import uk.ac.ebi.phenotype.service.ObservationService;
import uk.ac.ebi.phenotype.solr.indexer.ObservationIndexer;
import uk.ac.ebi.phenotype.solr.indexer.StatisticalResultIndexer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class SexualDimorphismDAOImpl extends HibernateDAOImpl implements SexualDimorphismDAO {

	@Autowired
	ObservationService observationService;

	public SexualDimorphismDAOImpl(SessionFactory sessionFactory) {

		this.sessionFactory = sessionFactory;
	}


	@Transactional(readOnly = true)
	public List<String[]> sexualDimorphismReportNoBodyWeight(String baseUrl) {

		List<String[]> res = new ArrayList<>();
		String[] temp = new String[1];

		try (PreparedStatement statement = getSexualDimorphismReportQuery(false)) {

			ResultSet results = statement.executeQuery();
			List<String> header = getSexualDimorphismHeader();

			res.add(header.toArray(temp));

			while (results.next()) {
				List<String> row = new ArrayList<>();
				row.add(results.getString("gene_symbol"));
				row.add(results.getString("gene_acc"));
				row.add(results.getString("allele_symbol"));
				row.add(results.getString("allele_acc"));
				row.add(results.getString("experimental_zygosity"));
				row.add(results.getString("colony_id"));
				row.add(results.getString("parameter"));
				row.add(results.getString("dependent_variable"));
				row.add(results.getString("female_mutants"));
				row.add(results.getString("female_controls"));
				row.add(results.getString("male_mutants"));
				row.add(results.getString("male_controls"));
				row.add(results.getString("classification_tag"));

				row.add(observationService.getExperimentalBatches(
					results.getString("phenotyping_center")
					, results.getString("pipeline_stable_id")
					, results.getString("dependent_variable")
					, results.getString("strain_accession_id")
					, results.getString("experimental_zygosity")
					, results.getString("metadata_group")
					, results.getString("allele_acc")).getBatchClassification().toString());

				row.add(results.getString("globalPValue"));
				row.add(results.getString("standardEffectSize"));
				row.add(getEffectDifference(results.getString("standardEffectSize")).toString());
				row.add(results.getString("male_genotype_pvalue"));
				row.add(results.getString("male_genotype_estimate"));
				row.add(results.getString("male_genotype_stderr"));
				row.add(results.getString("female_genotype_pvalue"));
				row.add(results.getString("female_genotype_estimate"));
				row.add(results.getString("female_genotype_stderr"));
				String chartUrl = ChartUtils.getChartPageUrlPostQc(baseUrl, results.getString("gene_acc"), results.getString("allele_acc"),
					ZygosityType.valueOf(results.getString("experimental_zygosity")), results.getString("dependent_variable"), null, null);
				row.add(chartUrl);
				res.add(row.toArray(temp));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}


	@Override
	@Transactional(readOnly = true)
	public List<String[]> sexualDimorphismReportWithBodyWeight(String baseUrl) {

		List<String[]> res = new ArrayList<>();
		String[] temp = new String[1];

		try (PreparedStatement statement = getSexualDimorphismReportQuery(true)) {

			ResultSet results = statement.executeQuery();
			List<String> header = getSexualDimorphismHeader();

			res.add(header.toArray(temp));

			while (results.next()) {
				List<String> row = new ArrayList<>();
				row.add(results.getString("gene_symbol"));
				row.add(results.getString("gene_acc"));
				row.add(results.getString("allele_symbol"));
				row.add(results.getString("allele_acc"));
				row.add(results.getString("experimental_zygosity"));
				row.add(results.getString("colony_id"));
				row.add(results.getString("parameter"));
				row.add(results.getString("dependent_variable"));
				row.add(results.getString("female_mutants"));
				row.add(results.getString("female_controls"));
				row.add(results.getString("male_mutants"));
				row.add(results.getString("male_controls"));
				row.add(results.getString("classification_tag"));

				row.add(observationService.getExperimentalBatches(
					results.getString("phenotyping_center")
					, results.getString("pipeline_stable_id")
					, results.getString("dependent_variable")
					, results.getString("strain_accession_id")
					, results.getString("experimental_zygosity")
					, results.getString("metadata_group")
					, results.getString("allele_acc")).getBatchClassification().toString());

				row.add(results.getString("globalPValue"));
				row.add(results.getString("standardEffectSize"));
				row.add(getEffectDifference(results.getString("standardEffectSize")).toString());
				row.add(results.getString("male_genotype_pvalue"));
				row.add(results.getString("male_genotype_estimate"));
				row.add(results.getString("male_genotype_stderr"));
				row.add(results.getString("female_genotype_pvalue"));
				row.add(results.getString("female_genotype_estimate"));
				row.add(results.getString("female_genotype_stderr"));
				String chartUrl = ChartUtils.getChartPageUrlPostQc(baseUrl, results.getString("gene_acc"), results.getString("allele_acc"),
					ZygosityType.valueOf(results.getString("experimental_zygosity")), results.getString("dependent_variable"), null, null);
				row.add(chartUrl);
				res.add(row.toArray(temp));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}


	PreparedStatement getSexualDimorphismReportQuery(boolean withBodyWeight) {

		String database;
		PreparedStatement statement = null;

		if (withBodyWeight) {
			database = "logistic_regression.";
		} else {
			database = "";
		}

		String command = "SELECT distinct bmstrain.strain_acc as strain_accession_id, sur.metadata_group, org.name as phenotyping_center, gf.symbol as gene_symbol, gf.acc as gene_acc, allele_acc, allele.symbol as allele_symbol, "
			+ " experimental_zygosity, colony_id, allele_acc, ppp.stable_id as pipeline_stable_id, pp.name as parameter, dependent_variable, female_mutants, female_controls, "
			+ " male_mutants, male_controls, null_test_significance as globalPValue, genotype_percentage_change as standardEffectSize, "
			+ " gender_male_ko_pvalue as male_genotype_pvalue, gender_male_ko_estimate as male_genotype_estimate, "
			+ " gender_male_ko_stderr_estimate male_genotype_stderr, gender_female_ko_pvalue as female_genotype_pvalue, "
			+ " gender_female_ko_estimate as female_genotype_estimate, gender_female_ko_stderr_estimate as female_genotype_stderr, "
			+ " REPLACE(classification_tag, 'If phenotype is significant - ', '') as classification_tag "
			+ " FROM " + database + "stats_unidimensional_results sur "
			+ " INNER JOIN " + database + "biological_model_allele bma ON bma.biological_model_id = sur.experimental_id"
			+ " INNER JOIN " + database + "biological_model_genomic_feature bmgf ON bmgf.biological_model_id = sur.experimental_id"
			+ " INNER JOIN " + database + "biological_model_strain bmstrain ON bmstrain.biological_model_id = sur.control_id"
			+ " INNER JOIN " + database + "phenotype_parameter pp on pp.id = sur.parameter_id"
			+ " INNER JOIN " + database + "phenotype_pipeline ppp on ppp.id = sur.pipeline_id"
			+ " INNER JOIN " + database + "allele on allele.acc = bma.allele_acc"
			+ " INNER JOIN " + database + "genomic_feature gf on gf.acc = bmgf.gf_acc "
			+ " INNER JOIN " + database + "organisation org on sur.organisation_id = org.id "
			+ " WHERE sur.status like \"SUCCESS\" AND classification_tag not in (\"Both genders equally\", \"No significant change\", \"If phenotype is significant - can not classify effect\", \"If phenotype is significant it is for the one sex tested\")"
			+ "	AND statistical_method not in (\"Wilcoxon rank sum test with continuity correction\") AND interaction_significance = 1 AND project_id not in (1,8)"
			+ " AND pp.stable_id NOT IN (" + StringUtils.join(ObservationIndexer.weightParameters, ",") + ")"
			+ " AND null_test_significance < 0.0001 LIMIT 100000" ;

		System.out.println(command);
		try (Connection connection = getConnection()) {
			statement = connection.prepareStatement(command);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return statement;
	}


	List<String> getSexualDimorphismHeader() {

		List<String> header = new ArrayList<>();
		header.add("gene_symbol");
		header.add("gene_acc");
		header.add("allele_symbol");
		header.add("allele_acc");
		header.add("experimental_zygosity");
		header.add("colony_id");
		header.add("parameter");
		header.add("dependent_variable");
		header.add("female_mutants");
		header.add("female_controls");
		header.add("male_mutants");
		header.add("male_controls");
		header.add("classification");
		header.add("workflow");
		header.add("globalPValue");
		header.add("standardEffectSize");
		header.add("effect_percentage_difference");
		header.add("male_genotype_pvalue");
		header.add("male_genotype_estimate");
		header.add("male_genotype_stderr");
		header.add("female_genotype_pvalue");
		header.add("female_genotype_estimate");
		header.add("female_genotype_stderr");
		header.add("graph");
		return header;
	}


	public Double getEffectDifference(String field) {

		Double female = StatisticalResultIndexer.getFemalePercentageChange(field);
		Double male = StatisticalResultIndexer.getMalePercentageChange(field);
		return Math.abs(female - male);
	}

}
