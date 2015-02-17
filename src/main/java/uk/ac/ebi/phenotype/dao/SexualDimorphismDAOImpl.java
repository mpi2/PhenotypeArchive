package uk.ac.ebi.phenotype.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Transactional;


public class SexualDimorphismDAOImpl  extends HibernateDAOImpl implements SexualDimorphismDAO {
	
	public SexualDimorphismDAOImpl(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	public List<String[]> sexualDimorphismReportNoBodyWeight() {
		
		PreparedStatement statement = null;        
        String command = "SELECT experimental_zygosity, colony_id, allele_acc, pp.name, dependent_variable, female_mutants, male_mutants, "
        + " null_test_significance as globalPValue, genotype_percentage_change as standardEffectSize, gender_male_ko_pvalue, gender_male_ko_estimate,"
        + " gender_male_ko_stderr_estimate, gender_female_ko_pvalue, gender_female_ko_estimate, gender_female_ko_stderr_estimate "
        + " FROM logistic_regression.stats_unidimensional_results sar "
        + " INNER JOIN biological_model_allele bmgf ON bmgf.biological_model_id=sar.experimental_id "
        + " INNER JOIN phenotype_parameter pp on pp.id=sar.parameter_id "
        + " WHERE status like \"SUCCESS\" AND classification_tag not in (\"Both genders equally\", \"No significant change\", \"If phenotype is significant - can not classify effect\", \"If phenotype is significant it is for the one sex tested\")"
        + "	AND statistical_method not in (\"Wilcoxon rank sum test with continuity correction\") AND interaction_significance = 1 AND project_id not in (1,8)"
        + " LIMIT 100000;";
        List<String[]> res = new ArrayList<>();
        String[] temp = new String[1];
        
        try (Connection connection = getConnection()) {
    			
    		statement = connection.prepareStatement(command);
     		ResultSet results = statement.executeQuery();

 			List <String> header = new ArrayList<>();
 			header.add("experimental_zygosity");
 			header.add("colony_id");
 			header.add("allele_acc");
 			header.add("name");
 			header.add("dependent_variable");
 			header.add("female_mutants");
 			header.add("male_mutants");
 			header.add("globalPValue");
 			header.add("standardEffectSize");
 			header.add("gender_male_ko_pvalue");
 			header.add("gender_male_ko_estimate");
 			header.add("gender_male_ko_stderr_estimate");
 			header.add("gender_female_ko_pvalue");
 			header.add("gender_female_ko_estimate");
 			header.add("gender_female_ko_stderr_estimate");
 			res.add(header.toArray(temp));
     		
     		while (results.next()){
     			List <String> row = new ArrayList<>();
     			row.add(results.getString("experimental_zygosity"));
     			row.add(results.getString("colony_id"));
     			row.add(results.getString("allele_acc"));
     			row.add(results.getString("name"));
     			row.add(results.getString("dependent_variable"));
     			row.add(results.getString("female_mutants"));
     			row.add(results.getString("male_mutants"));
     			row.add(results.getString("globalPValue"));
     			row.add(results.getString("standardEffectSize"));
     			row.add(results.getString("gender_male_ko_pvalue"));
     			row.add(results.getString("gender_male_ko_estimate"));
     			row.add(results.getString("gender_male_ko_stderr_estimate"));
     			row.add(results.getString("gender_female_ko_pvalue"));
     			row.add(results.getString("gender_female_ko_estimate"));
     			row.add(results.getString("gender_female_ko_stderr_estimate"));
     			res.add(row.toArray(temp));
     		}
	        
	    } catch(Exception e){
	        	e.printStackTrace();
	    }
        return res;
	}
}
