/**
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



package org.mousephenotype.www.testing.model;

import static com.thoughtworks.selenium.SeleneseTestBase.fail;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.stereotype.Component;
import uk.ac.ebi.generic.util.Tools;
import uk.ac.ebi.phenotype.util.Utils;

/**
 *
 * @author mrelac
 * 
 * Returns an iteration count. The count may be specified in one of three ways.
 * In order of priority:
 * <ul>
 * <li>system property specified as -D<i>testName=iterationCount</i> parameter on JVM command line</li>
 * <li>matching value from 'testIterations.properties' properties file (<i>testName=iterationCount</i> in properties file</li>
 * <li>value passed in by caller using getTargetCount(String, Integer)</li>
 * </ul>
 * If the property is still not found, the value specified in DEFAULT_COUNT is used.
 */
@Component
public class TestUtils {
    public final int DEFAULT_COUNT = 10;
    public final static String DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";
    
    @Resource(name="testIterationsHash")
    Map<String, String> testIterationsHash;
    
    /**
     * Return target count prioritized as follows:
     * <ul>
     * <li>if there is a system property matching <i>testMethodName</i>, that value is used</li>
     * <li>else if <i>testMethodName</i> appears in the <code>testIterations.properties</code> file, that value is used</li>
     * <li>else if <i>defaultCount</i> is not null, it is used</li>
     * <li>else the value defined by <i>DEFAULT_COUNT</i> is used</li>
     * </ul>
     * @param testMethodName the method to which the target count applies
     * @param collection the collection to be tested (used for maximum size when target count of -1 is specified)
     * @param defaultCount if not null, the value to use if it was not specified as a -D parameter on the command line
     *                     and no match was found for <i>testMethodName</i> in <code>testIterations.properties</code>
     * @return target count
     */
    public int getTargetCount(String testMethodName, Collection<String> collection, Integer defaultCount) {
        Integer targetCount = null;
        
        if (defaultCount != null)
            targetCount = defaultCount;
        
        if (testIterationsHash.containsKey(testMethodName)) {
            if (Utils.tryParseInt(testIterationsHash.get(testMethodName)) != null) {
                targetCount = Utils.tryParseInt(testIterationsHash.get(testMethodName));
            }
        }
        if (Utils.tryParseInt(System.getProperty(testMethodName)) != null) {
            targetCount = Utils.tryParseInt(System.getProperty(testMethodName));
        }
        
        if (targetCount == null) {
            targetCount = DEFAULT_COUNT;
        }
        
        if (targetCount == -1)
            targetCount = collection.size();
        
        return Math.min(targetCount, collection.size());
    }
    
    /**
     * Searches <code>list</code> for <code>searchToken</code>
     * @param list the list to search
     * @param searchToken the token to search for
     * @return true if <code>searchToken</code> was found in one of the strings
     * in <code>list</code>; false otherwise
     */
    public static boolean contains(List<WebElement> list, String searchToken) {
        if ((list == null) || (list.isEmpty()))
            return false;
        
        for (WebElement e : list) {
            if (e.getText().contains(searchToken))
                return true;
        }
        
        return false;
    }
    
    public static WebElement find(List<WebElement> list, String searchToken) {
        if ((list == null) || (list.isEmpty()))
            return null;
        
        for (WebElement e : list) {
            if (e.getText().contains(searchToken))
                return e;
        }
        
        return null;
    }
    
    /**
     * Searches each <code>WebElement</code>'s String value in <i>list</i> and,
     * if no strings are found, returns true; else returns false.
     * @param list the list to search
     * @return true if all <code>WebElement</code> strings are empty; false otherwise
     */
    public static boolean isEmpty(List<WebElement> list) {
        if ((list == null) || (list.isEmpty()))
            return true;
        
        for (WebElement e : list) {
            if ( ! e.getText().isEmpty())
                return false;
        }
        
        return true;
    }
    
    /**
     * Given an initialized <code>WebDriver</code> instance and a selenium URL,
     * prints the test environment for the test associated with <code>driver<code>.
     * @param driver the initialized <code>WebDriver</code> instance
     * @param seleniumUrl the Selenium URL
     */
    public static void printTestEnvironment(WebDriver driver, String seleniumUrl) {
        String browserName = "<Unknown>";
        String version = "<Unknown>";
        String platform = "<Unknown>";
        if (driver instanceof RemoteWebDriver) {
            RemoteWebDriver remoteWebDriver = (RemoteWebDriver)driver;
            browserName = remoteWebDriver.getCapabilities().getBrowserName();
            version = remoteWebDriver.getCapabilities().getVersion();
            platform = remoteWebDriver.getCapabilities().getPlatform().name();
        }
        
        System.out.println("\nTESTING AGAINST " + browserName + " version " + version + " on platform " + platform);
        System.out.println("seleniumUrl: " + seleniumUrl);
    }
    
    /**
     * Given a test name, test start time, error list, exception list, success list,
     * and total number of expected records to be processed, writes the given
     * information to stdout.
     * 
     * @param testName the test name (must not be null)
     * @param start the test start time (must not be null)
     * @param errorList the error list
     * @param exceptionList the exception list
     * @param successList the success list
     * @param totalRecords the total number of expected records to process
     * @param totalPossible the total number of possible records to process
     */
    public static void printEpilogue(String testName, Date start, List<String> errorList, List<String> exceptionList, List<String> successList, int totalRecords, int totalPossible) {
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        System.out.println(dateFormat.format(new Date()) + ": " + testName + " finished.");
        Date stop;
        
        if (errorList == null) errorList = new ArrayList();
        if (exceptionList == null) exceptionList = new ArrayList();
        if (successList == null) successList = new ArrayList();
        
        if ( ! errorList.isEmpty()) {
            System.out.println(errorList.size() + " records failed:");
            for (String s : errorList) {
                System.out.println("\t" + s);
            }
        }
        
        if ( ! exceptionList.isEmpty()) {
            System.out.println(exceptionList.size() + " records caused exceptions to be thrown:");
            for (String s : exceptionList) {
                System.out.println("\t" + s);
            }
        }
        
        stop = new Date();
        System.out.println(dateFormat.format(stop) + ": " + successList.size() + " of " + totalRecords + " (" + totalPossible + ") records successfully processed in " + Tools.dateDiff(start, stop) + ".");
        
        if (errorList.size() + exceptionList.size() > 0) {
            fail("ERRORS: " + errorList.size() + ". EXCEPTIONS: " + exceptionList.size());
        }
    }
    
    /**
     * Sleeps the thread for <code>thread_wait_in_ms</code> milliseconds.
     * 
     * @param thread_wait_in_ms length of time, in milliseconds, to sleep.
     */
    public static void sleep(long thread_wait_in_ms) {
            try { Thread.sleep(thread_wait_in_ms); } catch (Exception e) { }
    }
}
