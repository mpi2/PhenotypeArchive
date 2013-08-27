package uk.ac.ebi.phenotype.data.imits;

import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.conn.ConnectTimeoutException;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.ebi.phenotype.dao.BiologicalModelDAO;
import uk.ac.ebi.phenotype.dao.GenomicFeatureDAO;
import uk.ac.ebi.phenotype.pojo.GenomicFeature;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:app-config.xml" })
@TransactionConfiguration
@Transactional
public class phenotypeStatusTest {

	Logger log = Logger.getLogger(this.getClass().getCanonicalName());

	
	@Autowired
	@Qualifier("solr")
	PhenotypeStatusDAO psDao;// = new SolrPhenotypeStatusDAOImpl();
	@Autowired
	@Qualifier("biomart")
	PhenotypeStatusDAO oldPsDao;// = new BioMartPhenotypeStatusDAOImpl();


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

	@Test
	public void testCar4() {

		testColony("testCar4","MGI:1096574");

	}

	@Test
	public void testCib2() {

		testColony("testCib2","MGI:1929293");

	}

	@Test
	public void testFbxo7() {

		testColony("testFbxo7", "MGI:1917004");
	}

	@Test
	public void testDusp3() {
		testColony("Dusp3", "MGI:1919599");
	}

	public void testColony(String symbol, String acc) {

		System.setProperty("HTTP_PROXY_HOST","hx-wwwcache.ebi.ac.uk");
		System.setProperty("HTTP_PROXY_PORT", "3128");
		
		log.info("test " + symbol);
		GenomicFeature gene = genesDao.getGenomicFeatureByAccession(acc);

		List<ColonyStatus> list;
		List<ColonyStatus> list2;

		try {
			list = psDao.getColonyStatus(gene);
			list2=oldPsDao.getColonyStatus(gene);
			
			assertTrue(colonyListEqual(list, list2));

//			for (ColonyStatus cs: list) {
//				log.info("new status"+cs.toString());
//			}
//			for (ColonyStatus cs: list2) {
//				log.info(cs.toString());		}
		} catch (ConnectTimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean colonyListEqual(List<ColonyStatus> listOne, List<ColonyStatus> list2){
		if(listOne.size()!=list2.size()){
			System.out.println("list sizes are not the same!!!  listOne Size="+ listOne.size()+ " list2 size="+list2.size());
			return false;
		}
		for(ColonyStatus cStatus: listOne){
			String alleleName=cStatus.getAlleleName();
			for(ColonyStatus cStatus2: list2){
				if(cStatus2.getAlleleName().equalsIgnoreCase(alleleName)){
					System.out.println("allelenames equal so comparing");
					if(!colonyStatusEqual(cStatus, cStatus2)){
						System.out.println(cStatus.toString());
						System.out.println(cStatus2.toString());
						return false;
					}
					
				}
			}
		}
		return true;
	}
	
	public boolean colonyStatusEqual(ColonyStatus one, ColonyStatus two){
		
		//+"\talleleType="+this.alleleType+"\tbackgroundStrain=" + this.getBackgroundStrain() + "\tcolonyId=" + this.getColonyID() + "\tphenotypeStatus=" + this.getPhenotypeStatus() + "\tphenotypeCenter=" + this.getPhenotypeCenter() + "\tphenotypeStarted:" + this.getPhenotypeStarted() + "\tphenotypeCompleted:"+this.getPhenotypeCompleted()+"\tproductionStatus="+this.getProductionStatus();
	 
		 if(!one.getAlleleName().equals(two.getAlleleName())){
			 System.out.println("allelename diff");
			return false;
		 }
//		 if(!one.getAlleleType().equals(two.getAlleleType())){
//			 System.out.println("alletype diff");
//			 return false;
//		 }
//		 if(!one.getBackgroundStrain().equals(two.getBackgroundStrain())){
//			 System.out.println("backstrain diff");
//			 return false;
//		 }
		 if(!one.getPhenotypeStatus().equals(two.getPhenotypeStatus())){
			 System.out.println("phenostatus diff");
			 return false;
		 }
//		 if(!one.getPhenotypeCenter().equals(two.getPhenotypeCenter())){
//			 System.out.println("center  diff");
//			 return false;
//		 }
		 if(one.getPhenotypeStarted()!=two.getPhenotypeStarted()){
			 System.out.println("phenoStarted diff");
			 return false;
		 }
		 if(one.getPhenotypeCompleted()!=two.getPhenotypeCompleted()){
			 System.out.println("phenoCompleted diff");
		 }
		return true;
		
	}

}
