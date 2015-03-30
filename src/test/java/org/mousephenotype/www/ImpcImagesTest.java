/**
 * Copyright © 2014 EMBL - European Bioinformatics Institute
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License.  
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mousephenotype.www;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServerException;
import org.junit.After;
import org.junit.AfterClass;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.www.testing.model.GenePage;
import org.mousephenotype.www.testing.model.PageStatus;
import org.mousephenotype.www.testing.model.TestUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import uk.ac.ebi.phenotype.dao.PhenotypePipelineDAO;
import uk.ac.ebi.phenotype.service.GeneService;
import uk.ac.ebi.phenotype.service.dto.GeneDTO;
import uk.ac.ebi.phenotype.util.Utils;

/**
 *
 * @author mrelac
 * 
 *         These are selenium-based JUnit web tests that are configured (via the
 *         pom.xml) not to run with the default profile because they take too
 *         long to complete. To run them, use the 'web-tests' profile.
 * 
 *         These selenium tests use selenium's WebDriver protocol and thus need
 *         a hub against which to run. The url for the hub is defined in the
 *         Test Packages /src/test/resources/testConfig.properties file (driven
 *         by /src/test/resources/test-config.xml).
 * 
 *         To run these tests, edit /src/test/resources/testConfig.properties,
 *         making sure that the properties 'seleniumUrl' and
 *         'desiredCapabilities' are defined. Consult
 *         /src/test/resources/test-config.xml for valid desiredCapabilities
 *         bean ids.
 * 
 *         Examples:
 *         seleniumUrl=http://mi-selenium-win.windows.ebi.ac.uk:4444/wd/hub
 *         desiredCapabilities=firefoxDesiredCapabilities
 * 
 *         testAkt2() - @author Gautier Koscielny Selenium test for graph query
 *         coverage ensuring each graph display works for any given gene
 *         accession/parameter/zygosity from the Solr core
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-config.xml" })
public class ImpcImagesTest {

	@Autowired
	protected GeneService geneService;

	@Autowired
	private PhenotypePipelineDAO phenotypePipelineDAO;

	@Autowired
	protected String baseUrl;

	@Autowired
	protected WebDriver driver;

	@Autowired
	protected String seleniumUrl;

	@Autowired
	protected TestUtils testUtils;

	private final int TIMEOUT_IN_SECONDS = 4;
	private final int THREAD_WAIT_IN_MILLISECONDS = 20;

	private int timeout_in_seconds = TIMEOUT_IN_SECONDS;
	private int thread_wait_in_ms = THREAD_WAIT_IN_MILLISECONDS;

	private final Logger log = Logger.getLogger(this.getClass()
			.getCanonicalName());

	@Before
	public void setup() {
		if (Utils.tryParseInt(System.getProperty("TIMEOUT_IN_SECONDS")) != null)
			timeout_in_seconds = Utils.tryParseInt(System
					.getProperty("TIMEOUT_IN_SECONDS"));
		if (Utils
				.tryParseInt(System.getProperty("THREAD_WAIT_IN_MILLISECONDS")) != null)
			thread_wait_in_ms = Utils.tryParseInt(System
					.getProperty("THREAD_WAIT_IN_MILLISECONDS"));

		TestUtils.printTestEnvironment(driver, seleniumUrl);
		driver.navigate().refresh();
		try {
			Thread.sleep(thread_wait_in_ms);
		} catch (Exception e) {
		}
	}

	@After
	public void teardown() {
		if (driver != null) {
			driver.quit();
		}
	}

	@BeforeClass
	public static void setUpClass() {
	}

	@AfterClass
	public static void tearDownClass() {
	}

	// TESTS

	@Test
	// @Ignore
	public void testImpcImagesOnGenePage() throws Exception {

		String testName = "testImpcImagesOnGenePage";
		ArrayList<String> genes = new ArrayList<>();
		// genes.add("Akt2"); should fail on Akt2 as no impc_images
		
		genes.add("Ccdc120");
		genes.add("Cenpo");
		genes.add("Cwc27");
		genes.add("Eya4");
		genes.add("Htr1b");
		genes.add("Lrp1");
		genes.add("Osm");
		genes.add("Ppp2r2b");
		genes.add("Prkab1");
		genes.add("Rhbdl1");
		genes.add("Rxfp2");
		genes.add("Snrnp200");
		genes.add("Tpgs2");
		genes.add("Wee1");

		genes.add("Abcb11");
		genes.add("Baz1a");
		genes.add("C3");
		genes.add("Ddx41");
		genes.add("Dnajb7");
		genes.add("Idh1");
		genes.add("Ovgp1");
		genes.add("Palb2");
		genes.add("Pipox");
		genes.add("Pkd2l2");
		genes.add("Plekhm1");
		genes.add("Stk16");
		genes.add("Vps13d");
		String geneString=genes.toString();
//		System.out.println(geneString);
		String orQuery=geneString.replace(",", " OR ");
		System.out.println(orQuery);
		List<String> geneIds = new ArrayList<>();
		
		for (String gene : genes) {
			GeneDTO geneDto = geneService.getGeneByGeneSymbol(gene);
			System.out.println("geneDto=" + geneDto.getMgiAccessionId());
			geneIds.add(geneDto.getMgiAccessionId());
		}
		geneIdsTestEngine(testName, geneIds);

	}

	@Test
	// @Ignore
	public void testImpcImagesOnaSpecificGenePage() throws Exception {

		String testName = "testImpcImagesOnGenePage";
		ArrayList<String> genes = new ArrayList<>();
		// genes.add("Akt2"); should fail on Akt2 as no impc_images
		genes.add("Baz1a");
		String geneString = genes.toString();
		System.out.println(geneString);
		String orQuery = geneString.replace(",", " OR ");
		System.out.println(orQuery);
		List<String> geneIds = new ArrayList<>();
		// genes.add("Wee1");
		//
		// GeneDTO geneDto = geneService.getGeneByGeneSymbol(gene);
		// System.out.println("geneDto=" + geneDto.getMgiAccessionId());
		// geneIds.add(geneDto.getMgiAccessionId());
		// }

	}

	// PRIVATE METHODS

	private void geneIdsTestEngine(String testName, List<String> geneIds)
			throws SolrServerException {
		DateFormat dateFormat = new SimpleDateFormat(TestUtils.DATE_FORMAT);

		String target = "";
		List<String> errorList = new ArrayList();
		List<String> successList = new ArrayList();
		List<String> exceptionList = new ArrayList();
		String message;
		Date start = new Date();

		System.out.println(dateFormat.format(start) + ": " + testName
				+ " started. Expecting to process " + geneIds.size()
				+ " of a total of " + geneIds.size() + " records.");

		// Loop through all genes, testing each one for valid page load.
		int i = 0;
		WebDriverWait wait = new WebDriverWait(driver, timeout_in_seconds);
		for (String geneId : geneIds) {

			target = baseUrl + "/genes/" + geneId;
			System.out.println("gene[" + i + "] URL: " + target);

			try {
				driver.get(target);
				wait.until(ExpectedConditions.presenceOfElementLocated(By
						.cssSelector("span#enu")));
				GenePage genePage = new GenePage(driver, wait, target, geneId,
						phenotypePipelineDAO, baseUrl);
				boolean hasImpcImages = genePage.hasImpcImages();
				if (!hasImpcImages) {
					String localMessage = "no impc images for gene " + geneId;

					errorList.add(localMessage);
				}
				assertTrue(hasImpcImages);
				List<String> parameters = genePage
						.getAssociatedImpcImageSections();
				assertTrue(parameters.size() > 0);
			} catch (NoSuchElementException | TimeoutException te) {
				message = "Expected page for MGI_ACCESSION_ID " + geneId + "("
						+ target + ") but found none.";
				errorList.add(message);
				TestUtils.sleep(thread_wait_in_ms);
				continue;
			} catch (Exception e) {
				message = "EXCEPTION processing target URL " + target + ": "
						+ e.getLocalizedMessage();
				exceptionList.add(message);
				TestUtils.sleep(thread_wait_in_ms);
				continue;
			}

			message = "SUCCESS: MGI_ACCESSION_ID " + geneId + ". URL: "
					+ target;
			successList.add(message);

			TestUtils.sleep(thread_wait_in_ms);
			i++;
		}

		TestUtils.printEpilogue(testName, start, errorList, exceptionList,
				successList, i, geneIds.size());
	}

}