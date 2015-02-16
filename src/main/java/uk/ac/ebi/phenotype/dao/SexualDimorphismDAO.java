package uk.ac.ebi.phenotype.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;



public class SexualDimorphismDAO  extends HibernateDAOImpl {
	//TODO get sexualDimorphismReportNoBodyWeight

	
	/*SELECT experimental_zygosity, colony_id, allele_acc, pp.name, dependent_variable, female_mutants, male_mutants, null_test_significance as globalPValue,
	genotype_percentage_change as standardEffectSize, gender_male_ko_pvalue, gender_male_ko_estimate, gender_male_ko_stderr_estimate, gender_female_ko_pvalue, gender_female_ko_estimate, gender_female_ko_stderr_estimate
FROM logistic_regression.stats_unidimensional_results sar
INNER JOIN biological_model_allele bmgf ON bmgf.biological_model_id=sar.experimental_id 
INNER JOIN phenotype_parameter pp on pp.id=sar.parameter_id
WHERE status like "SUCCESS" AND classification_tag not in ("Both genders equally", "No significant change", "If phenotype is significant - can not classify effect", "If phenotype is significant it is for the one sex tested")
	AND statistical_method not in ("Wilcoxon rank sum test with continuity correction") AND interaction_significance = 1 AND project_id not in (1,8);
*/
	public List<String[]> sexualDimorphismReportNoBodyWeight() {
		
		List<String[]> res =  new ArrayList<>();
		PreparedStatement statement = null;
//		ApplicationContext ac = new ClassPathXmlApplicationContext("app-config.xml");
//		DataSource dataSource = (DataSource) ac.getBean("komp2DataSource");
        
        String command = "SELECT * FROM logistic_regression.stats_unidimensional_results sar INNER"
        		+ " JOIN biological_model_allele bmgf ON bmgf.biological_model_id=sar.experimental_id;";
        
        try (Connection connection = getConnection()) {
    			
    		statement = connection.prepareStatement(command);
     		ResultSet results = statement.executeQuery();
                  					
	        while ( results.next()){
	          
	        
	        }
	        
	    } catch(Exception e){
	        	e.printStackTrace();
	    }
        return res;
	}
	
	
	
	//TODO get sexualDimorphismReportWithBodyWeight 
}
