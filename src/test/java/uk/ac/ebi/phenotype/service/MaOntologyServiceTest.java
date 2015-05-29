/**
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
/**
 * Copyright © 2015 EMBL - European Bioinformatics Institute
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

package uk.ac.ebi.phenotype.service;

import java.sql.SQLException;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.phenotype.solr.indexer.beans.OntologyTermBean;

/**
 *
 * @author mrelac
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-config.xml" })
public class MaOntologyServiceTest {

    @Autowired
    MaOntologyService instance;
    
    public MaOntologyServiceTest() {
        
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }
    
    
    // PRIVATE METHODS
    
    
    private String joinIds(List<OntologyTermBean> terms) {
        String retVal = "";
        for (OntologyTermBean term : terms) {
            if ( ! retVal.isEmpty())
                retVal += ", ";
            retVal += term.getId();
        }
        
        return retVal;
    }
    
    private String[] joinIdsAsArray(List<OntologyTermBean> terms) {
        String[] retVal = new String[terms.size()];
        
        for (int i = 0; i < terms.size(); i++) {
            retVal[i] = terms.get(i).getId();
        }
        
        return retVal;
    }
    
    
    // TESTS
    
    
    /**
     * Test of populateAncestorGraph method, of class MaOntologyService.
     * 
     * Expected result: ["MA:0000326", "MA:0001752"]
     */
//@Ignore
    @Test
    public void testPopulateAncestorGraph() {
        System.out.println("testPopulateAncestorGraph");
        String[] expectedTermIdsArray = new String[] { "MA:0000326", "MA:0001752" };
        
        List<List<String>> ma0002420 = instance.getAncestorGraphs("MA:0002420");
        String[] actualTermIdsArray = ma0002420.get(0).toArray(new String[0]);
        String errMsg = "Expected [" + StringUtils.join(expectedTermIdsArray, ", ")
                   + "]. Actual: [" + StringUtils.join(actualTermIdsArray, ", ");
        assertArrayEquals(errMsg, expectedTermIdsArray, actualTermIdsArray);
        
        actualTermIdsArray = ma0002420.get(1).toArray(new String[0]);
        errMsg = "Expected [" + StringUtils.join(expectedTermIdsArray, ", ")
                   + "]. Actual: [" + StringUtils.join(actualTermIdsArray, ", ");
        assertArrayEquals(errMsg, expectedTermIdsArray, actualTermIdsArray);
    }

    /**
     * Test of getDescendentGraphs method, of class MaOntologyService.
     * 
     * Expected result: [0]: ["MA:0000411"]
     *                  [1]: ["MA:0000384", "MA:0001707", "MA:0001460]
     *                  [2]: ["MA:0000411"]
     *                  [3]: ["MA:0000384", "MA:0001707", "MA:0001460]
     */
//@Ignore
    @Test
    public void testGetDescendentGraphs() {
        System.out.println("testGetDescendentGraphs");
        String[][] expectedTermIdsArray = new String[][] {
            { "MA:0000411" }
          , { "MA:0000384", "MA:0001707", "MA:0001460" }
          , { "MA:0000411" }
          , { "MA:0000384", "MA:0001707", "MA:0001460" }
        };
        
        List<List<String>> ma0002420 = instance.getDescendentGraphs("MA:0002420");
        assertEquals(expectedTermIdsArray.length, ma0002420.size());
        for (int i = 0; i < 4; i++) {
            String[] expected = expectedTermIdsArray[i];
            String[] actual   = ma0002420.get(i).toArray(new String[0]);
            assertEquals(expected.length, actual.length);
            
            String errMsg = "Expected [" + StringUtils.join(expected, ", ")
                       + "]. Actual: [" + StringUtils.join(actual, ", ");
            assertArrayEquals(errMsg, expected, actual);
        }
    }
    
    /**
     * Test of getSynonyms method, of class MaOntologyService.
     * 
     * Expected result: ["MA:0000316"]
     */
//@Ignore
    @Test
    public void testGetSynonyms2Synonyms() {
        System.out.println("testGetSynonyms2Synonyms");
        String[] expectedSynonymsArray = new String[] { "cranial bone", "skull" };
        
        List<String> actualSynonyms = instance.getSynonyms("MA:0000316");
        String[] actualSynonymsArray = actualSynonyms.toArray(new String[0]);
        String errMsg = "Expected [" + StringUtils.join(expectedSynonymsArray, ", ")
                   + "]. Actual: [" + StringUtils.join(actualSynonymsArray, ", " + "]");
        assertArrayEquals(errMsg, expectedSynonymsArray, actualSynonymsArray);
    }

    /**
     * Test top level, default level
     * 
     * Expected result: ["MA:0000326"]
     */
//@Ignore
    @Test
    public void testGetTopLevelNotTopDefaultLevel() {
        System.out.println("testGetTopLevelNotTopDefaultLevel");
        String[] expectedTermIdsArray = { "MA:0000326" };
        
        List<OntologyTermBean> actualTerms = instance.getTopLevel("MA:0002420");
        String errMsg = "Expected 1 top level MA:0000326 but found " + joinIds(actualTerms);
        assertTrue(errMsg, actualTerms.size() == 1);
        assertEquals(expectedTermIdsArray[0], actualTerms.get(0).getId());
    }

    /**
     * Test top level, level 1
     * 
     * Expected result: ["MA:0000326"]
     */
//@Ignore
    @Test
    public void testGetTopLevelNotTopLevel1() {
        System.out.println("testGetTopLevelNotTopLevel1");
        String[] expectedTermIdsArray = { "MA:0000326" };
        
        List<OntologyTermBean> actualTerms = instance.getTopLevel("MA:0002420", 1);
        String errMsg = "Expected 1 top level MA:0000326 but found " + joinIds(actualTerms);
        assertTrue(errMsg, actualTerms.size() == 1);
        assertEquals(expectedTermIdsArray[0], actualTerms.get(0).getId());
    }

    /**
     * Test top level, level 2
     * 
     * Expected result: [ ]
     */
//@Ignore
    @Test
    public void testGetTopLevelNotTopLevel2() {
        System.out.println("testGetTopLevelNotTopLevel2");
        String[] expectedTermIdsArray = { "MA:0001752" };
        
        List<OntologyTermBean> actualTerms = instance.getTopLevel("MA:0002420", 2);
        String errMsg = "Expected 1 top level MA:0001752 but found " + joinIds(actualTerms);
        assertTrue(errMsg, actualTerms.size() == 1);
        assertEquals(expectedTermIdsArray[0], actualTerms.get(0).getId());
    }

    /**
     * Test top level, level 3
     * 
     * Expected result: [ ]
     */
//@Ignore
    @Test
    public void testGetTopLevelNotTopLevel3() {
        System.out.println("testGetTopLevelNotTopLevel3");
        
        List<OntologyTermBean> actualTerms = instance.getTopLevel("MA:0002420", 3);
        String errMsg = "Expected no top level ids but found " + joinIds(actualTerms);
        assertTrue(errMsg, actualTerms.isEmpty());
    }

    /**
     * Test top level, from top id
     * 
     * Expected result: [ ]
     */
//@Ignore
    @Test
    public void testGetTopLevelFromTopDefaultLevel() {
        System.out.println("testGetTopLevelFromTopDefaultLevel");
        
        List<OntologyTermBean> actualTerms = instance.getTopLevel("MA:0000326");
        String errMsg = "Expected no top level ids but found " + joinIds(actualTerms);
        assertTrue(errMsg, actualTerms.isEmpty());
    }

    /**
     * Test top level, from top id
     * 
     * Expected result: [ ]
     */
//@Ignore
    @Test
    public void testGetTopLevelFromTopLevel1() {
        System.out.println("testGetTopLevelFromTopLevel1");
        
        List<OntologyTermBean> actualTerms = instance.getTopLevel("MA:0000326", 1);
        String errMsg = "Expected no top level ids but found " + joinIds(actualTerms);
        assertTrue(errMsg, actualTerms.isEmpty());
    }

    /**
     * Test getAncestorTerms
     * 
     * Expected result: ["MA:0000326", "MA:0001752"]
     */
//@Ignore
    @Test
    public void testGetAncestorTerms() {
        System.out.println("testGetAncestorTerms");
        String[] expectedTermIdsArray = { "MA:0000326", "MA:0001752" };
        
        List<OntologyTermBean> actualTerms = instance.getAncestors("MA:0002420");
        String[] actualTermIdsArray = joinIdsAsArray(actualTerms);
        String errMsg = "Expected [" + StringUtils.join(expectedTermIdsArray, ", ")
                     + "] but found [" + joinIds(actualTerms) + "]";
        assertArrayEquals(errMsg, expectedTermIdsArray, actualTermIdsArray);
    }

    /**
     * Test getParentTerms
     * 
     * Expected result: ["MA:0001752"]
     */
//@Ignore
    @Test
    public void testGetParentTerms() {
        System.out.println("testGetParentTerms");
        String[] expectedTermIdsArray = { "MA:0001752" };
        
        List<OntologyTermBean> actualTerms = instance.getParents("MA:0002420");
        String[] actualTermIdsArray = joinIdsAsArray(actualTerms);
        String errMsg = "Expected [" + StringUtils.join(expectedTermIdsArray, ", ")
                     + "] but found [" + joinIds(actualTerms) + "]";
        assertArrayEquals(errMsg, expectedTermIdsArray, actualTermIdsArray);
    }

    /**
     * Test getIntermediateTerms
     * 
     * Expected result: ["MA:0001752"]
     */
//@Ignore
    @Test
    public void testGetIntermediateTerms() {
        System.out.println("testGetIntermediateTerms");
        String[] expectedTermIdsArray = { "MA:0001752" };
        
        List<OntologyTermBean> actualTerms = instance.getIntermediates("MA:0002420");
        String[] actualTermIdsArray = joinIdsAsArray(actualTerms);
        String errMsg = "Expected 1 matching id MA:0001752. Found " + joinIds(actualTerms);
        assertArrayEquals(errMsg, expectedTermIdsArray, actualTermIdsArray);
    }

    /**
     * Test getChildTerms
     * 
     * Expected result: ["MA:0000411", "MA:0000384"]
     */
//@Ignore
    @Test
    public void testGetChildTerms() {
        System.out.println("testGetChildTerms");
        String[] expectedTermIdsArray;
        List<OntologyTermBean> actualTerms;
        String[] actualTermIdsArray;
        String errMsg;
        
        actualTerms = instance.getChildren("MA:0002420");
        actualTermIdsArray = joinIdsAsArray(actualTerms);
        expectedTermIdsArray = new String[] { "MA:0000411", "MA:0000384" };
        errMsg = "Expected " + StringUtils.join(expectedTermIdsArray, ", ") + " but found " + joinIds(actualTerms);
        assertArrayEquals(errMsg, expectedTermIdsArray, actualTermIdsArray);
    }
    
    /**
     * Test getDescendentTerms
     * 
     * Expected result: ["MA:0000411", "MA:0000384", "MA:0001707",
     *                   "MA:0001460"]
     */
//@Ignore
    @Test
    public void testGetDescendentTermsDefault() {
        System.out.println("testGetDescendentTermsDefault");
        String[] expectedTermIdsArray = { "MA:0000411", "MA:0000384", "MA:0001707",
                                        "MA:0001460" };
        
        List<OntologyTermBean> actualTerms = instance.getDescendents("MA:0002420");
        String[] actualTermIdsArray = joinIdsAsArray(actualTerms);
        String errMsg = "Expected [" + StringUtils.join(actualTermIdsArray, ", ")
                   + "]. Actual: [" + StringUtils.join(actualTermIdsArray, ", ");
        assertArrayEquals(errMsg, expectedTermIdsArray, actualTermIdsArray);
    }
    
    /**
     * Test getDescendentTermsLevel
     * 
     * Expected result: level 0:   Exception
     *                  level 1:   ["MA:0000411"]
     *                  level 4:   ["MA:0001460"]
     *                  level 6:   [ ]
     *                  level 100: [ ]
     */
//@Ignore
    @Test
    public void testGetDescendentTermsWithLevels() {
        System.out.println("testGetDescendentTermsWithLevels");
        String[] expectedTermsArray;
        List<OntologyTermBean> actualTerms;
        String[] actualTermIdsArray;
        String errMsg;
        
        try {
            instance.getDescendents("MA:0002405", 0);                           // Level 0.
            fail( "Expected exception when testing with level 0");
        } catch (Exception e) {
            // Expected exception. Do nothing.
        }
        
        expectedTermsArray = new String[] { "MA:0000411", "MA:0000384" };
        actualTerms = instance.getDescendents("MA:0002420", 1);                 // Level 1.
        actualTermIdsArray = joinIdsAsArray(actualTerms);
        errMsg = "Expected [" + StringUtils.join(actualTermIdsArray, ", ") + "]"
                   + "]. Actual: [" + StringUtils.join(actualTermIdsArray, ", ");
        assertArrayEquals(errMsg, expectedTermsArray, actualTermIdsArray);
        
        expectedTermsArray = new String[] { "MA:0001460" };
        actualTerms = instance.getDescendents("MA:0002420", 3);                 // Level 3.
        actualTermIdsArray = joinIdsAsArray(actualTerms);
        errMsg = "Expected [" + StringUtils.join(actualTermIdsArray, ", ") + "]"
                   + "]. Actual: [" + StringUtils.join(actualTermIdsArray, ", ");
        assertArrayEquals(errMsg, expectedTermsArray, actualTermIdsArray);
        
        expectedTermsArray = new String[] { };
        actualTerms = instance.getDescendents("MA:0002420", 100);               // Level 100.
        actualTermIdsArray = joinIdsAsArray(actualTerms);
        errMsg = "Expected: [ ].  Actual: [" + StringUtils.join(actualTermIdsArray, ", ") + "]";
        assertArrayEquals(errMsg, expectedTermsArray, actualTermIdsArray);
    }
    
//@Ignore
    @Test
    public void testBuildAncestorMap() throws SQLException {
        instance.setShowAncestorMapWarnings(true);                              // Turn on ancestor map warnings.
        instance.populateAncestorMap();
        
        if (instance.hasAncestorMapWarnings())
            fail("There are ancestor map warnings.");
    }
    
}