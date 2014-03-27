package uk.ac.ebi.phenotype.ontology;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class OntologyTermsRetriever {

	public static void main(String[] args) throws SQLException, IOException {
		ApplicationContext ac = new ClassPathXmlApplicationContext("appConfig.xml");
		DataSource ds = (DataSource) ac.getBean("komp2DataSource");
		System.out.println(ds.toString());
		PreparedStatement statement = ds.getConnection().prepareStatement("SELECT DISTINCT (mp_acc) FROM phenotype_call_summary WHERE mp_acc != '' ");
		ResultSet res = statement.executeQuery();
		BufferedWriter cout = new BufferedWriter (new FileWriter("mpTerms.txt"));
		while (res.next()) {
			cout.write(res.getString("mp_acc").toString()+"\n");
		}
		cout.close();
	}

}

/*	public class main {

		public static void main(String[] args) {
		//	extractSlim(loadOntology("/Users/tudose/Documents/mp-ext-merged.owl"), "/Users/tudose/Documents/mpTerms.txt", null, "MP:", "/Users/tudose/Documents/newOnt.obo");
			
		//	ArrayList<String> relLabelsToFollow = new ArrayList<String>();
		//	relLabelsToFollow.add("part of");
		//	extractSlim(loadOBOOntology("/Users/tudose/Documents/adult_mouse_anatomy.obo"), "/Users/tudose/Documents/maTerms.txt", relLabelsToFollow, "MA:", "/Users/tudose/Documents/newMAOnt.obo");;
		
			grepMPReferencedMATerms("/Users/tudose/Documents/mpTerms.txt", "/Users/tudose/Documents/mp-ext-merged.owl", "/Users/tudose/Documents/maTermsReferencedByMP.txt", "/Users/tudose/Documents/ma-mp_newOnt.obo");
		}
*/	
