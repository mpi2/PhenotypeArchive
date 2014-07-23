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
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
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
     * Counts and returns the number of sex icons in <code>table</code>
     * @param table the data store
     * @param sexColumnIndex the zero-relative sex column index in the data store
     * @return the number of sex icons in <code>table</code>: for each row,
     * if the sex = "male" or "female", add 1. If the sex = "both", add 2.
     */
    public static int getSexIconCount(GridMap table, int sexColumnIndex) {
        int retVal = 0;
        
        for (String[] sA : table.getBody()) {
            if (sA[sexColumnIndex].equalsIgnoreCase("female"))
                retVal++;
            else if (sA[sexColumnIndex].equalsIgnoreCase("male"))
                retVal++;
            else if (sA[sexColumnIndex].equalsIgnoreCase("both"))
                retVal += 2;
        }
        
        return retVal;
    }
    
    /**
     * Return target count prioritized as follows:
     * <p><b>NOTE: If the returned target size is less than the collection size,
     * the collection is shuffled (i.e. randomized)</b></p>
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
    public int getTargetCount(String testMethodName, List<String> collection, Integer defaultCount) {
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
        
        // If targetCount is less than the collection, randomize the collection.
        if (targetCount < collection.size()) {
            Collections.shuffle(collection);
            System.out.println("Randomizing collection.");
        }
        
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
    
    /**
     * Decodes <code>url</code>, into UTF-8, making it suitable to use as a link.
     * Invalid url strings are ignored and the original string is returned.
     * @param url the url to decode
     * @return the decoded url
     */
    public static String urlDecode(String url) {
        String retVal = url;
        try {
            String decodedValue = URLDecoder.decode(url, "UTF-8");
            retVal = decodedValue;
        } catch (Exception e) {
            System.out.println("Decoding of value '" + (url == null ? "<null>" : url) + "' failed: " + e.getLocalizedMessage());
        }
        
        return retVal;
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
     * The baseUrl for testing typically looks like:
     *     "http://ves-ebi-d0:8080/mi/impc/dev/phenotype-archive".
     * Typical urls (e.g. graph urls) look like:
     *     "http://ves-ebi-d0:8080/data/charts?accession=MGI:xxx...."
     * Typical tokenMatch for graph pages looks like "/charts?". For download
     * links it looks like "/export?".
     * 
     * @param baseUrl the base url
     * @param url the graph (or other page) url
     * @param tokenMatch the token matching the start of the good part of the url.
     * @return a useable url that starts with the baseUrl followed by
     * everything including and after the '/charts?' part of the url.
     */
    public static String patchUrl(String baseUrl, String url, String tokenMatch) {
        int idx = url.indexOf(tokenMatch);
        return baseUrl + url.substring(idx);
    }
    
    /**
     * This handles a specific download test condition when the baseUrl is ves-ebi-d0.
     * Download tests on DEV may use either:
     * <ul><li>dev.mousephenotype.org or</li>
     * <li>ves-ebi-d0:8080</li>
     * 
     * This method looks at the incoming url and, if it is ves-ebi-d0:8080,
     * replaces it with dev.mousephenotype.org.
     * 
     * This permits the download test comparison to be accurate and to succeed
     * only when appropriate.
     * @param url target url
     * @return modified url if target url was ebi-ves-d0.
     */
    public static String patchVesEbiD0(String url) {
        return url.replace("ves-ebi-d0:8080", "dev.mousephenotype.org");
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
     * Removes the protocol and double slashes from the url string
     * @param url url string which may or may not contain a protocol
     * @return the url, without the protocol or the double slashes
     */
    public static String removeProtocol(String url) {
        return (url.replace("https://", "").replace("http://", ""));
    }
    
    /**
     * Sleeps the thread for <code>thread_wait_in_ms</code> milliseconds.
     * If <code>threadWaitInMs</code> is null or 0, no sleep is executed.
     * 
     * @param threadWaitInMs length of time, in milliseconds, to sleep.
     */
    public static void sleep(Integer threadWaitInMs) {
        if ((threadWaitInMs != null) && (threadWaitInMs > 0))
            try { Thread.sleep(threadWaitInMs); } catch (Exception e) { }
    }
}
