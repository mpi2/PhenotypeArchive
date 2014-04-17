/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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

package phenotype.web;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;
import org.junit.Before;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.TestContextManager;
import uk.ac.ebi.phenotype.stats.GenotypePhenotypeService;

/**
 *
 * This abstract class serves as the parent for all selenium web-based tests
 * requiring:
 *      Spring
 *      baseUrl (the phenotype archive web applicatin instance. For DEV, it's http://dev.mousephenotype.org/data)
 
 * @author mrelac
 */
public abstract class AbstractJunit4Tester implements ApplicationContextAware {
    private ApplicationContext ac;
    private boolean firstPass = true;
    
    @Autowired
    protected GenotypePhenotypeService genotypePhenotypeService;
    protected String baseUrl;
    protected WebDriver driver;
    
    public DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ac = applicationContext;
    }

    /**
     * This constructor gets called once before every new WebDriver parameter.
     * JUnit doesn't provide a hook that is called after all of the tests [for
     * one WebDriver] have been executed, so there is no handy place to close
     * each browser after an execution of this test file for a given WebDriver.
     * If you need to explicitly close the WebDriver browser window, you can
     * test the 'driver' instance variable for null and, if it is not null, call
     * driver.close().
     * 
     * @param driver the next parameterized driver instance
     * @throws Exception 
     */
    public AbstractJunit4Tester(WebDriver driver) throws Exception {
        String browserName = "<Unknown>";
        String version = "<Unknown>";
        String platform = "<Unknown>";
        if (driver instanceof RemoteWebDriver) {
            RemoteWebDriver remoteWebDriver = (RemoteWebDriver)driver;
            browserName = remoteWebDriver.getCapabilities().getBrowserName();
            version = remoteWebDriver.getCapabilities().getVersion();
            platform = remoteWebDriver.getCapabilities().getPlatform().name();
        }
        
        System.out.println("TESTING AGAINST " + browserName + " version " + version + " on platform " + platform);
        
        this.driver = driver;
    }
    
    @Before
    public void setupAbstract() {
        // This code initializes Spring. Since we can't safely use 'this' in the
        // constructor, we initialize Spring here, only once.
        if (firstPass) {
            // These two statements perform the work that @RunWith(SpringJUnit4ClassRunner.class) 
            // used to perform (Parameterization now uses the RunWith instead)
            TestContextManager testContextManager = new TestContextManager(getClass());
            try {
                testContextManager.prepareTestInstance(this);
            } catch (Exception e) {
                System.out.println("ERROR: prepareTestInstance call failed.");
                throw new RuntimeException(e);
            }

            Map<String,String> config = (Map<String, String>) ac.getBean("globalConfiguration");
            baseUrl = config.get("baseUrl");
            firstPass = false;
        }
    }
    
}