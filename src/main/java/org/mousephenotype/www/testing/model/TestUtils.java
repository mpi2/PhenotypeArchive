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

import uk.ac.ebi.phenotype.service.dto.GraphTestDTO;
import static com.thoughtworks.selenium.SeleneseTestBase.fail;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.GroupCommand;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.mousephenotype.www.testing.exception.GraphTestException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ebi.generic.util.Tools;
import uk.ac.ebi.phenotype.chart.ChartType;
import uk.ac.ebi.phenotype.chart.ChartUtils;
import uk.ac.ebi.phenotype.dao.PhenotypePipelineDAO;
import uk.ac.ebi.phenotype.pojo.ObservationType;
import uk.ac.ebi.phenotype.service.ObservationService;
import uk.ac.ebi.phenotype.service.PostQcService;
import uk.ac.ebi.phenotype.service.PreQcService;
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
public class TestUtils {
    public final int DEFAULT_COUNT = 10;
    public final static String DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";
    private static final Logger logger = Logger.getLogger("TestUtils");
    
    @Resource(name="testIterationsHash")
    Map<String, String> testIterationsHash;
    
    @Autowired
    ObservationService observationService;
    
    @Autowired
    PostQcService postQcService;
    
    @Autowired
    PreQcService preQcService;
    
    /**
     * Defines the download types.
     */
    public enum DownloadType {
        TSV("tsv")
      , XLS("xls");
      
      private final String name;
      DownloadType(String name) {
          this.name = name;
      }
      
      public String getName() {
          return name;
      }
      
      @Override
      public String toString() {
          return getName();
      }
    }
    
    /**
     * Adds <code>snippet</code> to <code>source</code>, delmited by <code>
     * delimiter</code> if <code>source</code> is not empty.
     * @param source
     * @param snippet
     * @param delimiter
     * @return The string, delimited as appropriate (i.e. not delimited if <code>
     * source</code> was empty)
     */
    public static String addTo(String source, String snippet, String delimiter) {
        if ( ! source.isEmpty())
            source += delimiter;
        
        return source + snippet;
    }
    
    /**
     * Counts and returns the number of sex icons in <code>table</code>
     * @param table the data store
     * @param sexColumnIndex the zero-relative sex column index in the data store
     * @param graphColumnIndex if not null, the zero-relative graph column index
     *                         which will be used to filter out non preqc-rows.
     * @return the number of sex icons in <code>table</code>: for each row,
     * if the sex = "male" or "female", add 1. If the sex = "both", add 2.
     */
    public static int getSexIconCount(GridMap table, int sexColumnIndex, Integer graphColumnIndex) {
        int retVal = 0;
        
        for (String[] sA : table.getBody()) {
            // If this is a preqc row, skip it.
            if (graphColumnIndex != null) {
                if (sA[graphColumnIndex].contains("/phenoview/")) {
                    continue;
                }
            }
            
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
    public int getTargetCount(String testMethodName, List collection, Integer defaultCount) {
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
     * @param defaultCount if not null, the value to use if it was not specified as a -D parameter on the command line
     *                     and no match was found for <i>testMethodName</i> in <code>testIterations.properties</code>
     * @return target count
     */
    public int getTargetCount(String testMethodName, Integer defaultCount) {
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
        
        return targetCount;
    }

    /**
     * Compares two strings, each delimited by <code>delimiter</code> by count
     * and value: if each has the same number of delimited segments, and each
     * delimited segment's value is equal, returns true. If <code>
     * ignoreNoInfoAvailable</code> is true, any string values equaling 
     * 'No information available' are first removed from each string before the
     * comparison is performed.
     * NOTE: null/empty strings are treated equally and, if both are either null
     * or empty, return true.
     * 
     * @param string1 the first string operand
     * @param string2 the second string operand
     * @param delimiter string segment delimiter
     * @param ignoreNoInfoAvailable if true, the string 'No information available'
     *        is first removed from both strings.
     * @return true if each string's segments are equal; false otherwise
     */
    public static boolean isEqual(String string1, String string2, String delimiter, boolean ignoreNoInfoAvailable) {
        boolean retVal;
        Set<String> string1Set = new HashSet();
        Set<String> string2Set = new HashSet();
        
        if (string1 == null)
            string1 = "";
        if (string2 == null)
            string2 = "";
        
        String[] parts = string1.split(delimiter);
        for (String part : parts) {
            if ((ignoreNoInfoAvailable) && (part.equals(SearchFacetTable.NO_INFO_AVAILABLE))) {
                // Do nothing. This is the 'No information available' string, which the caller has asked us not to include.
            } else {
                string1Set.add(part);
            }
        }
        parts = string2.split(delimiter);
        for (String part : parts) {
            if ((ignoreNoInfoAvailable) && (part.equals(SearchFacetTable.NO_INFO_AVAILABLE))) {
                // Do nothing. This is the 'No information available' string, which the caller has asked us not to include.
            } else {
                string2Set.add(part);
            }
        }
        
        if (string1Set.size() == string2Set.size()) {
            string1Set.removeAll(string2Set);
            retVal = string1Set.isEmpty();
        } else {
            retVal = false;
        }
        
        return retVal;
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
     * Searches <code>list</code> for <code>searchToken</code>, returning the
     * number of times <code>searchToken</code> appears in <code>list</code>.
     *
     * @param list the list to search
     * @param searchToken the token to search for
     * @return the number of times <code>searchToken</code> appears in <code>list</code>.
     */
    public static int count(List<String> list, String searchToken) {
        int retVal = 0;
        
        if ((list == null) || (list.isEmpty()))
            return retVal;
        
        for (String s : list) {
            if (s.contains(searchToken))
                retVal++;
        }
        
        return retVal;
    }
    
    /**
     * Clones an existing set.
     * @param input set to be cloned
     * @return a new deep-copy instance of input
     */
    public static Set cloneStringSet(Set<String> input) {
        return cloneStringSet(input, false);
    }
    
    /**
     * Clones an existing set, lowercasing each string if directed.
     * @param input set to be cloned
     * @param setToLowercase if true, each string is set to lowercase; otherwise
     *        each string is left untouched.
     * @return a new deep-copy instance of input
     */
    public static Set cloneStringSet(Set<String> input, boolean setToLowercase) {
        HashSet resultSet = new HashSet();
        
        for (String s : input) {
            if (setToLowercase) {
               resultSet.add(s.toLowerCase());
            } else {
               resultSet.add(s);
            }
        }
        
        return resultSet;
    }
    
    /**
     * Returns the closest match to <code>stringToMatch</code> in
     * <code>set</code>
     *
     * @param set the set to search
     * 
     * @param stringToMatch the string to match
     * 
     * @return the closest match to <code>stringToMatch</code> in <code>set</code>
     */
    public static String closestMatch(Set<String> set, String stringToMatch) {
        String matchedString = "";
        Integer matchedScore = null;
        if ((set == null) || (stringToMatch == null))
            return matchedString;
        
        Iterator<String> it = set.iterator();
        while (it.hasNext()) {
            String candidate = it.next();
            int candidateScore = StringUtils.getLevenshteinDistance(candidate, stringToMatch);
            if (matchedString.isEmpty()) {                                      // First time through, populate matchedXxx.
                matchedString = candidate;
                matchedScore = candidateScore;
            } else {
                if ((candidateScore >= 0) && (candidateScore < matchedScore)) {
                    matchedScore = candidateScore;
                    matchedString = candidate;
                }
            }
        }
        
        return matchedString;
    }
    
    /**
     * Creates a set from <code>input</code> using <code>colIndexes</code>, using
     * the underscore character as a column delimiter. Each value is first trimmed,
     * then lowercased.
     * 
     * Example: input.body[][] = "a", "b", "c", "d", "e"
     *                           "f", "g", "h", "i", "j"
     * 
     * colIndexes = 1, 3, 4
     * 
     * produces a set that looks like:  "b_d_e_"
     *                                  "g_i_j_"
     * @param input Input object
     * @param colIndexes indexes of columns to be copied
     * @return a set containing the concatenated values.
     */
    public static Set<String> createSet(GridMap input, Integer[] colIndexes) {
        HashSet resultSet = new HashSet();
        
        String[][] body = input.getBody();
        for (int rowIndex = 0; rowIndex < body.length; rowIndex++) {
            String[] row = body[rowIndex];
            String resultString = "";
            for (int colIndex : colIndexes) {
                resultString += row[colIndex].trim().toLowerCase() + "_";
            }
            resultSet.add(resultString);
        }
        
        return resultSet;
    }
    
    /**
     * Dump <code>set</code> using logger ('info' level)
     * 
     * @param name the set name (for display purposes)
     * @param set the set to be dumped
     */
    public static void dumpSet(String name, Set<String> set) {
        System.out.println("\nDumping set '" + name + "'. Contains " + set.size() + " records:");
        if (set.size() <= 0)
            return;
        
        String[] data = set.toArray(new String[0]);
        for (int i = 0; i < set.size(); i++) {
            System.out.println("[" + i + "]: " + data[i]);
        }
        System.out.println();
    }
    
    /**
     * Creates a set from <code>input</code> using <code>colIndexes</code>, using
     * the underscore character as a column delimiter. Each value is first trimmed,
     * then lowercased.
     * 
     * Example: input.body[][] = "a", "b", "c", "d", "e"
     *                           "f", "g", "h", "i", "j"
     * 
     * colIndexes = 1, 3, 4
     * 
     * produces a set that looks like:  "b_d_e_"
     *                                  "g_i_j_"
     * @param input Input object
     * @param colIndexes indexes of columns to be copied
     * @return a set containing the concatenated values.
     */
    public static Set<String> createSet(GridMap input, List<Integer> colIndexes) {
        HashSet resultSet = new HashSet();
        
        String[][] body = input.getBody();
        for (int rowIndex = 0; rowIndex < body.length; rowIndex++) {
            String[] row = body[rowIndex];
            String resultString = "";
            for (int colIndex : colIndexes) {
                resultString += row[colIndex].trim().toLowerCase() + "_";
            }
            resultSet.add(resultString);
        }
        
        return resultSet;
    }
    
    /**
     * Given a source array of 'array of String' and a starting index in that
     * array, copies <code>count</code> 'array of String' elements into a new
     * array returned to the caller.
     * @param src the source array
     * @param startIndex the source array starting index
     * @param count the number of elements to copy
     * @return the requested elements
     */
    public static String[][] copy(String[][] src, int startIndex, int count) {
        if (src == null)
            return null;
        if ((src.length == 0) || (src[0].length == 0))
            return new String[0][0];
        
        String[][] retVal = new String[src.length - 1][src[0].length];
        for (int i = 0; i < count; i++) {
            retVal[i] = src[i + startIndex];
        }
        
        return retVal;
    }
    
    private final static double EPSILON = 0.000000001;
    /**
     * Performs an approximate match between two doubles. Returns true if 
     * the two values are within a difference of 0.000000001; false otherwise
     * @param a first operand
     * @param b second operand
     * @return true if  the two values are within a difference of 0.000000001;
     * false otherwise
     */
    public static boolean equals(double a, double b) {
        return (a == b ? true : Math.abs(a - b) < EPSILON);
    }
    
    /**
     * Performs an approximate match between two doubles. Returns true if 
     * the two values are within <code>epsilon</code>; false otherwise
     * @param a first operand
     * @param b second operand
     * @param epsilon the difference within which both operands are considered
     * equal
     * @return true if  the two values are within <code>epsilon</code>; false otherwise
     */
    public static boolean equals(double a, double b, double epsilon) {
        return (a == b ? true : Math.abs(a - b) < epsilon);
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
     * 
     * @param graphUrl The graph URL being tested
     * @return true if this is a preQC link; false otherwise.
     */
    public static boolean isPreQcLink(String graphUrl) {
        if (graphUrl == null)
            return false;
        
        return graphUrl.contains("/phenoview/");
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
     * Patches any non-heading <code>input</code> string values that are empty
     * with the string 'No information available', returning a new <code>
     * GridMap</code> instance identical to the input, with empty strings replaced
     * as described.
     *
     * @param input the <code>GridMap</code> to be scanned and patched
     * @return a copy of the input <code>GridMap</code>, with empty strings
     * replaced with 'No information available'.
     */
    public static GridMap patchEmptyFields(GridMap input) {
        String[][] dataOut = new String[input.getData().length][input.getData()[0].length];
        String[][] dataIn = input.getData();
        dataOut[0] = input.getHeading();                                           // Copy heading to output object.
        for (int rowIndex = 1; rowIndex < dataOut.length; rowIndex++) {
            String[] row = dataIn[rowIndex];
            for (int colIndex = 0; colIndex < row.length; colIndex++) {
                dataOut[rowIndex][colIndex] = ((row[colIndex] == null) || (row[colIndex].isEmpty()) ? "No information available" : row[colIndex]);
            }
        }
        
        return new GridMap(dataOut, input.getTarget());
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
     * @Deprecated Please use the printEpilogue that contains a PageStatus.
     */
    @Deprecated
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
     * Given a test name, test start time, error list, exception list, success list,
     * and total number of expected records to be processed, writes the given
     * information to stdout.
     * 
     * @param testName the test name (must not be null)
     * @param start the test start time (must not be null)
     * @param status the <code>PageStatus</code> instance
     * @param totalRecords the total number of expected records to process
     * @param totalPossible the total number of possible records to process
     */
    public static void printEpilogue(String testName, Date start, PageStatus status, int totalRecords, int totalPossible) {
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        System.out.println(dateFormat.format(new Date()) + ": " + testName + " finished.");
        Date stop;
        
        if (status.hasWarnings()) {
            System.out.println(status.getWarningMessages().size() + " records had warnings:");
            System.out.println(status.toStringWarningMessages());
        }
        
        if (status.hasErrors()) {
            System.out.println(status.getErrorMessages().size() + " records failed:");
            System.out.println(status.toStringErrorMessages());
        }
        
        stop = new Date();
        String warningClause = (status.hasWarnings() ? " (" + status.getWarningMessages().size() + " warning(s) " : "");
        System.out.println(dateFormat.format(stop) + ": " + status.getSuccessCount() + " of " + totalRecords + " (total possible: " + totalPossible + ") records successfully processed" + warningClause + " in " + Tools.dateDiff(start, stop) + ".");
        if (status.hasErrors()) {
            fail("ERRORS: " + status.getErrorMessages().size());
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
    
    public enum HTTP_PROTOCOL {
        http
      , https
    };
    
    /**
     * Sets the protocol (http or https).
     * @param url url string which may or may not contain a protocol
     * @param protocol one of: http or https (choose from enum)
     * @return the url, with the protocol changed, if it exists
     */
    public static String setProtocol(String url, HTTP_PROTOCOL protocol) {
        return (url.replace("https://", protocol.name() + "://").replace("http://", protocol.name() + "://"));
    }
    
    /**
     * Scrolls <code>element</code> to the top
     * @param driver <code>WebDriver</code> instance
     * @param element Element to scroll to top
     */
    public static void scrollToTop(WebDriver driver, WebElement element) {
        scrollToTop(driver, element, null);
    }
    
    /**
     * There is a selenium bug that silently removes opening parentheses from
     * a sendkeys string. See http://stackoverflow.com/questions/19704559/selenium-sendkeys-not-working-for-open-brackets-and-harsh-keys-when-using-java
     * 
     * This is the workaround.
     * 
     * @param element <code>WebElement</code> against which to use the sendKeys
     * @param text the text to send (may contain open parenthesis)
     */
    public static void seleniumSendKeysHack(WebElement element, String text) {
        char[] chars = text.toCharArray();
        for (char c : chars) {
            if (c == '(') {
                element.sendKeys(Keys.chord(Keys.SHIFT, "9"));
            } else {
                StringBuffer sb = new StringBuffer().append(c);
                element.sendKeys(sb);
            }
        }
    }
    
    /**
     * Scrolls <code>element</code> to the top
     * @param driver <code>WebDriver</code> instance
     * @param element Element to scroll to top
     * @param yOffsetInPixels An <code>Integer</code> which, if not null and not 0,
     *     first scrolls the element to the top, then further scrolls it <code>
     *     yOffsetInPixels</code> pixels down (if negative number) or up (if
     *     positive).
     */
    public static void scrollToTop(WebDriver driver, WebElement element, Integer yOffsetInPixels) {
        Point p = element.getLocation();
        ((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView(true);", element);
        
        if ((yOffsetInPixels != null) && (yOffsetInPixels != 0)) {
            ((JavascriptExecutor)driver).executeScript("window.scroll(" + p.getX() + "," + (p.getY() + yOffsetInPixels) + ");");
        }
        
        sleep(100);
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
    
    /**
     * Sorts <code>delimitedString</code> alphabetically, first splitting the
     * string into separate segments, delimited by <code>delimiter</code>.
     * 
     * @param delimitedString the delimited string to sort
     * @param delimiter the delimiter
     * 
     * @return the sorted string
     */
    public static String sortDelimitedArray(String delimitedString, String delimiter) {
        if (( delimitedString == null) || (delimitedString.isEmpty()))
            return delimitedString;
        
        String[] partsArray = delimitedString.split(Pattern.quote(delimiter));
        List<String> partsList = Arrays.asList(partsArray);
        Collections.sort(partsList);
        
        String retVal = "";
        for (String part : partsList) {
            if ( ! retVal.isEmpty()) {
                retVal += delimiter;
            }
            retVal += part;
        }
            
//        logger.debug("retVal: '" + retVal + "'");
        
        return retVal;
    }
    
    /**
     * Sorts the delimited cells in the specified columns of each row in <code>delimitedArray</code> alphabetically
     * For example, given:
     *      [0]           [1]           [2]
     *      "abc"         "f|e|d"       "ghi"
     *      "l|k|j"       "klm"         "o|p|n"
     * 
     * and a 'columns' specification of [1, 2], the resulting returned array will be:
     *      [0]           [1]           [2]
     *      "abc"         "d|e|f"       "ghi"
     *      "l|k|j"       "klm"         "n|o|p"
     *                    
     * @param delimitedArray the input data set
     * @param delimiter the delimiter
     * @param columns the list of columns to sort
     * 
     * @return the sorted list
     */
    public static String[][] sortDelimitedArray(String[][] delimitedArray, String delimiter, List<Integer> columns) {
        if ((delimitedArray == null) || (delimitedArray.length == 0))
            return delimitedArray;
            
        String[][] retVal = new String[delimitedArray.length][delimitedArray[0].length];
        for (int rowIndex = 0; rowIndex < delimitedArray.length; rowIndex++) {
            String[] row = delimitedArray[rowIndex];
            for (int colIndex = 0; colIndex < row.length; colIndex++) {
                String cell = row[colIndex];
                if (columns.contains(colIndex)) {
                    retVal[rowIndex][colIndex] = sortDelimitedArray(cell, delimiter);
                } else {
                    retVal[rowIndex][colIndex] = cell;
                }
            }
        }
        
        return retVal;
    }
    
    /**
     * Returns the classpath, prefaced by the string 'Classpath:\n'. Each
     * file is separated by a newline.
     * 
     * @return the classpath, prefaced by the string 'Classpath:\n'. Each
     * file is separated by a newline.
     */
    public static String getClasspath() {
        StringBuilder sb = new StringBuilder("Classpath:\n");
        ClassLoader cl = ClassLoader.getSystemClassLoader();
        URL[] urls = ((URLClassLoader) cl).getURLs();
        
        for (URL url : urls) {
            sb.append(url.getFile()).append("\n");
        }
        
        return sb.toString();
    }
    
    /**
     * Queries the preqc core for <code>count</code> mpIds of phenotype pages that
     * contain preqc links.
     * @param solrUrl The solr URL as defined in the pom or the app-config.xml file
     * @param phenotypePipelineDAO a valid <code>PhenotypePipelineDAO</code> instance
     * @param count the number of random mpIds to return. A null or 0 value means return all.
     * @return a list of <code>count</code> strings containing mpIds with preqc links
     */
    public static List<String> getPreqcIds(String solrUrl, PhenotypePipelineDAO phenotypePipelineDAO, Integer count) {
        Set<String> geneIds = new HashSet();
        if ((count != null) && (count < 1))
            count = 1000000;           // Null/0 indicates fetch all gene IDs (well, many, at least).
        
        try {
            PreQcService preqcService = new PreQcService(solrUrl, phenotypePipelineDAO);
            SolrServer server = preqcService.getSolrServer();
            
            /*logger.debug*/System.out.println("TestUtils.getPreqcIds(): querying preqc core for " + (count == null ? "all" : count) + " gene ids.");

            SolrQuery solrQuery = new SolrQuery();
            solrQuery
                    .setQuery("*:*")
//                    .setFields("marker_accession_id")
                    .setRows(Integer.MAX_VALUE)
                    .add("group", "true")
                    .add("group.field", "marker_accession_id")
                    .add("group.limit", "0")
//                    .add("rows", "0")
                    ;
            
            System.out.println("solrQuery = " + solrQuery.toString());
            
            QueryResponse response = server.query(solrQuery);
            
            List<GroupCommand> groupResponse = response.getGroupResponse().getValues();
            for (GroupCommand groupCommand : groupResponse) {
                List<Group> groups = groupCommand.getValues();
                for (Group group : groups) {
                    geneIds.add(group.getGroupValue());
                    
                    
                    SolrDocumentList docs = group.getResult();
                    Iterator<SolrDocument> it = docs.iterator();
                    while (it.hasNext()) {
                        SolrDocument doc = it.next();
                        String mgiAccessionId = (String)doc.get("marker_accession_id");
                        geneIds.add(mgiAccessionId);
                    }
                }
            }
        } catch (Exception e) {
            String errMsg = "ERROR: " + e.getLocalizedMessage();
            System.out.println(errMsg);
            throw new RuntimeException(errMsg, e);
        }
        
        return new ArrayList(geneIds);
    }
    
    /**
     * Returns <em>count</em> <code>GraphTestDTO</code> instances matching genes
     * with graph links of type <code>chartType</code>.
     * 
     * @param chartType the desired chart type
     * @param count the desired number of instances to be returned. If -1,
     * MAX_INT instances will be returned.
     * 
     * @return <em>count</em> <code>GraphTestDTO</code> instances matching genes
     * with graph links of type <code>chartType</code>.
     * 
     * @throws GraphTestException
     */
    public List<GraphTestDTO> getGeneGraphs(ChartType chartType, int count) throws GraphTestException {
        List<GraphTestDTO> geneGraphs = new ArrayList();
        
        if (count == -1)
            count = Integer.MAX_VALUE;
        
        switch (chartType) {
            case CATEGORICAL_STACKED_COLUMN:
                try {
                    List<String> parameterStableIds = observationService.getParameterStableIdsByObservationType(ObservationType.categorical, count);
                    geneGraphs = postQcService.getGeneAccessionIdsByParameterStableId(parameterStableIds, count);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new GraphTestException("TestUtils.getGeneGraphs() CATEGORICAL_STACKED_COLUMN EXCEPTION: " + e.getLocalizedMessage());
                }
                break;
                
            case PIE:
                try {
                    List<String> parameterStableIds = Arrays.asList(new String[] { "*_VIA_*" });
                    geneGraphs = postQcService.getGeneAccessionIdsByParameterStableId(parameterStableIds, count);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new GraphTestException("TestUtils.getGeneGraphs() PIE EXCEPTION: " + e.getLocalizedMessage());
                }
                break;
                
            case UNIDIMENSIONAL_ABR_PLOT:
                try {
                    List<String> parameterStableIds = Arrays.asList(new String[] { "*_ABR_*" });
                    geneGraphs = postQcService.getGeneAccessionIdsByParameterStableId(parameterStableIds, count);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new GraphTestException("TestUtils.getGeneGraphs() UNIDIMENSIONAL_ABR_PLOT EXCEPTION: " + e.getLocalizedMessage());
                }
                break;
                
            case UNIDIMENSIONAL_BOX_PLOT:
            case UNIDIMENSIONAL_SCATTER_PLOT:
                try {
                    List<String> parameterStableIds = observationService.getParameterStableIdsByObservationType(ObservationType.unidimensional, count);
                    geneGraphs = postQcService.getGeneAccessionIdsByParameterStableId(parameterStableIds, count);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new GraphTestException("TestUtils.getGeneGraphs() UNIDIMENSIONAL_XXX EXCEPTION: " + e.getLocalizedMessage());
                }
                break;
                
            case PREQC:
                try {
                    geneGraphs = preQcService.getGeneAccessionIds(count);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new GraphTestException("TestUtils.getGeneGraphs() PREQC EXCEPTION: " + e.getLocalizedMessage());
                }
            break;
                
            case TIME_SERIES_LINE:
            case TIME_SERIES_LINE_BODYWEIGHT:
                try {
                    List<String> parameterStableIds = new ArrayList();
                    parameterStableIds.addAll(ChartUtils.ESLIM_701);
                    parameterStableIds.addAll(ChartUtils.ESLIM_702);
                    parameterStableIds.addAll(ChartUtils.IMPC_BWT);
                    geneGraphs = postQcService.getGeneAccessionIdsByParameterStableId(parameterStableIds, count);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new GraphTestException("TestUtils.getGeneGraphs() TIME_SERIES_XXX EXCEPTION: " + e.getLocalizedMessage());
                }
            break;
                
        }

        return geneGraphs;
    }
}