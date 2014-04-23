package uk.ac.ebi.phenotype.data.imits;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.ebi.phenotype.dao.GenomicFeatureDAO;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-config.xml" })
@TransactionConfiguration
@Transactional
public class phenotypeStatusTest {

	Logger log = Logger.getLogger(this.getClass().getCanonicalName());

	

	@Autowired
	private GenomicFeatureDAO genesDao;


	public void testRegex() {
		String alleleSymbolSuperscript = "tm1a(EUCOMM)Wtsi";
		String phenotypeAlleleType = "b";

		Pattern p1 = Pattern.compile("^tm\\d{1}\\(");
		Matcher m1 = p1.matcher(alleleSymbolSuperscript);

		Pattern p2 = Pattern.compile("^tm\\d{1}[a-z]{1}\\(");
		Matcher m2 = p2.matcher(alleleSymbolSuperscript);

		if (m1.find()) {
			log.info("replacing " + alleleSymbolSuperscript.replaceAll("^(tm\\d{1})", "$1" + phenotypeAlleleType));
		} else if (m2.find()) {
			log.info(alleleSymbolSuperscript.replaceAll("^(tm\\d{1})([a-z]{1})", "$1" + phenotypeAlleleType));
		} 
	}



}
