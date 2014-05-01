package org.mousephenotype.www;

import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-config.xml" })
public class BrowserIntegrationTest implements ApplicationContextAware {

	private ApplicationContext ac;
	
	private String HOST = "http://localhost:8080";
	private String URL;
	static WebDriver driver;
	
	@Before
	public void setup() {
		@SuppressWarnings("unchecked")
		Map<String,String> config = (Map<String, String>) ac.getBean("globalConfiguration");
		URL = HOST + config.get("baseUrl");
		driver = new FirefoxDriver();
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
	}

	@After
	public void teardown() {
		driver.quit();
	}

	@Test
        @Ignore
	public void testEya4GenePage() throws InterruptedException {

		driver.get(URL + "/genes/MGI:1337104");

		WebElement element = driver.findElement(By.xpath("//*[@id=\"gene_name\"]"));

		assertTrue(element.getText().contains("eyes absent 4 homolog (Drosophila)"));
		assertTrue(driver.getPageSource().contains("Chr10:22823974-23069701"));
		assertTrue(!driver.getPageSource().contains("pax6"));
	}


	@Test
	@Ignore
	public void testAnophthalmiaPhenotypePage() throws InterruptedException {

		driver.get(URL + "/phenotypes/MP:0001293");

		WebElement element = driver.findElement(By.xpath("//*[@id=\"mpOverviewTable\"]/tbody/tr[1]/td[2]"));

		assertTrue(element.getText().contains("absence of the globe and ocular"));
		assertTrue(driver.getPageSource().contains("abnormal uvea morphology"));
		assertTrue(!driver.getPageSource().contains("pax6"));
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		ac = applicationContext;
	}

}
