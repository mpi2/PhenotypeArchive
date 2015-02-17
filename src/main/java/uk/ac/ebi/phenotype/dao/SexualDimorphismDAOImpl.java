package uk.ac.ebi.phenotype.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.ebi.phenotype.chart.ChartUtils;
import uk.ac.ebi.phenotype.pojo.ZygosityType;


public class SexualDimorphismDAOImpl  extends HibernateDAOImpl implements SexualDimorphismDAO {
    	
	public SexualDimorphismDAOImpl(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Transactional(readOnly = true)
	public List<String[]> sexualDimorphismReportNoBodyWeight(String baseUrl) {
		
		PreparedStatement statement = null;        
        String command = "SELECT  gf.symbol as gene_symbol, gf.acc as gene_acc, allele_acc, allele.symbol as allele_symbol, "
        + " experimental_zygosity, colony_id, allele_acc, pp.name as parameter, dependent_variable, female_mutants, female_controls, "
        + " male_mutants, male_controls, null_test_significance as globalPValue, genotype_percentage_change as standardEffectSize, "
        + " gender_male_ko_pvalue as male_genotype_pvalue, gender_male_ko_estimate as male_genotype_estimate, "
        + " gender_male_ko_stderr_estimate male_genotype_stderr, gender_female_ko_pvalue as female_genotype_pvalue, "
        + " gender_female_ko_estimate as female_genotype_estimate, gender_female_ko_stderr_estimate as female_genotype_stderr"
        + " FROM logistic_regression.stats_unidimensional_results sur "
        + " INNER JOIN biological_model_allele bma ON bma.biological_model_id = sur.experimental_id"
        + " INNER JOIN biological_model_genomic_feature bmgf ON bmgf.biological_model_id = sur.experimental_id"
        + " INNER JOIN phenotype_parameter pp on pp.id = sur.parameter_id"
        + " INNER JOIN allele on allele.acc = bma.allele_acc"
        + " INNER JOIN genomic_feature gf on gf.acc = bmgf.gf_acc "
        + " WHERE sur.status like \"SUCCESS\" AND classification_tag not in (\"Both genders equally\", \"No significant change\", \"If phenotype is significant - can not classify effect\", \"If phenotype is significant it is for the one sex tested\")"
        + "	AND statistical_method not in (\"Wilcoxon rank sum test with continuity correction\") AND interaction_significance = 1 AND project_id not in (1,8)"
        + " LIMIT 100000;";
        List<String[]> res = new ArrayList<>();
        String[] temp = new String[1];
        
        try (Connection connection = getConnection()) {
    			
    		statement = connection.prepareStatement(command);
     		ResultSet results = statement.executeQuery();

 			List <String> header = new ArrayList<>();
 			header.add("gene_symbol");
 			header.add("gene_acc");
 			header.add("allele_symbol");
 			header.add("allele_acc");
 			header.add("experimental_zygosity");
 			header.add("colony_id");
 			header.add("parameter");
 			header.add("dependent_variable");
 			header.add("female_mutants");
 			header.add("female_contols");
 			header.add("male_mutants");
 			header.add("male_contols");
 			header.add("globalPValue");
 			header.add("standardEffectSize");
 			header.add("effect_size_difference");
 			header.add("male_genotype_pvalue");
 			header.add("male_genotype_estimate");
 			header.add("male_genotype_stderr");
 			header.add("female_genotype_pvalue");
 			header.add("female_genotype_estimate");
 			header.add("female_genotype_stderr");
 			header.add("graph");
 			res.add(header.toArray(temp));
     		
     		while (results.next()){
     			List <String> row = new ArrayList<>();
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
     			row.add(results.getString("globalPValue"));
     			row.add(results.getString("standardEffectSize"));
     			Float effectSize = Math.abs(results.getFloat("female_genotype_estimate") - results.getFloat("male_genotype_estimate"));
     			row.add(effectSize.toString());
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
	        
	    } catch(Exception e){
	        	e.printStackTrace();
	    }
        return res;
	}

	
	@Override
	public List<String[]> sexualDimorphismReportWithBodyWeight(String baseUrl) {
		
		return null;
	}
	
	
	
	
}
