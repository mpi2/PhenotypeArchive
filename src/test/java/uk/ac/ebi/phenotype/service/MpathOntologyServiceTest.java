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

import edu.emory.mathcs.backport.java.util.Arrays;
import java.sql.SQLException;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.phenotype.solr.indexer.IndexerException;
import uk.ac.ebi.phenotype.solr.indexer.beans.OntologyTermBean;

/**
 *
 * @author mrelac
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-config.xml" })
public class MpathOntologyServiceTest {

    @Autowired
    MpathOntologyService instance;
    
    public MpathOntologyServiceTest() {
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
    
    /**
     * Test of populateAncestorGraph method, of class MpOntologyService.
     * 
     * @throws SQLException
     */
//@Ignore
    @Test
    public void testPopulateAncestorGraph() throws SQLException {
        System.out.println("testPopulateAncestorGraph");
        List<List<String>> mpath151 = instance.getAncestorGraphs("MPATH:151");
        
        String errMsg = "Expected size 2. Actual size = " + mpath151.size() + ".";
        assertTrue(errMsg, mpath151.size() >= 2);
        for (List<String> graphs : mpath151) {
            if (graphs.get(1).equals("MPATH:218")) {
                    assertEquals("MPATH:603", graphs.get(0));
                    assertEquals("MPATH:218", graphs.get(1));
                    assertEquals("MPATH:556", graphs.get(2));
            } else {
                    assertEquals("MPATH:603", graphs.get(0));
                    assertEquals("MPATH:126", graphs.get(1));
                    assertEquals("MPATH:602", graphs.get(2));
                    assertEquals("MPATH:149", graphs.get(3));
            }
        }
    }

    /**
     * Test of getDescendentGraphs method, of class MpOntologyService.
     * 
     * @throws SQLException
     */
//@Ignore
    @Test
    public void testGetDescendentGraphs() throws SQLException {
        System.out.println("testGetDescendentGraphs");
        List<List<String>> mpath151 = instance.getDescendentGraphs("MPATH:151");
        
        String errMsg = "Expected size 0. Actual size = " + mpath151.size() + ".";
        assertTrue(errMsg, mpath151.size() >= 0);
        
        List<List<String>> mpath149 = instance.getDescendentGraphs("MPATH:149");
        assertEquals(7, mpath149.size());
        
        String[][] expectedTerms = { 
            { "MPATH:154", "MPATH:156", "MPATH:155" }
          , { "MPATH:152" }
          , { "MPATH:157" }
          , { "MPATH:158" }
          , { "MPATH:151" }
          , { "MPATH:153" }
          , { "MPATH:150" }
        };
        
        for (int rowIndex = 0; rowIndex < mpath149.size(); rowIndex++) {
            List<String> actualRow = mpath149.get(rowIndex);
            String[] actualTermsArray = actualRow.toArray(new String[0]);
            String[] expectedTermsArray = expectedTerms[rowIndex];
            
            assertArrayEquals(expectedTermsArray, actualTermsArray);
        }
    }
    
    /**
     * Test of getSynonyms method, of class MpOntologyService.
     * 
     * @throws SQLException
     */
//@Ignore
    @Test
    public void testGetSynonyms3Synonyms() throws SQLException {
        System.out.println("testGetSynonyms3Synonyms");
        String[] expectedSynonymsArray = new String[] { "cornification", "hyperorthokeratosis", "hyperparakeratosis" };
        List<String> actualSynonyms = instance.getSynonyms("MPATH:154");
        
        List<String> expectedSynonyms = Arrays.asList(expectedSynonymsArray);
        
        String errMsg = "Expected at least 3 synonyms. Actual # synonyms = " + actualSynonyms.size() + ".";
        assertTrue(errMsg, actualSynonyms.size() >= 3);
        
        if ( ! actualSynonyms.containsAll(expectedSynonyms)) {
            fail("Expected synonyms " + expectedSynonyms + ". Actual synonyms = " + StringUtils.join(actualSynonyms, ", "));
        }
    }

    /**
     * Test top level, default level
     * 
     * @throws SQLException
     * @throws IndexerException
     */
//@Ignore
    @Test
    public void testGetTopLevelNotTopDefaultLevel() throws SQLException, IndexerException {
        System.out.println("testGetTopLevelNotTopDefaultLevel");
        String[] expectedTermsArray = { "MPATH:603" };
        List<OntologyTermBean> actualTerms = instance.getTopLevel("MPATH:151");
        String errMsg = "Expected 1 top level MPATH:603 but found " + joinIds(actualTerms);
        assertTrue(errMsg, actualTerms.size() == 1);
        assertEquals(expectedTermsArray[0], actualTerms.get(0).getId());
    }

    /**
     * Test top level, level 1
     * 
     * @throws SQLException
     * @throws IndexerException
     */
//@Ignore
    @Test
    public void testGetTopLevelNotTopLevel1() throws SQLException, IndexerException {
        System.out.println("testGetTopLevelNotTopLevel1");
        String[] expectedTermsArray = { "MPATH:603" };
        List<OntologyTermBean> actualTerms = instance.getTopLevel("MPATH:151", 1);
        String errMsg = "Expected 1 top level MPATH:603 but found " + joinIds(actualTerms);
        assertTrue(errMsg, actualTerms.size() == 1);
        assertEquals(expectedTermsArray[0], actualTerms.get(0).getId());
    }

    /**
     * Test top level, level 2
     * 
     * @throws SQLException
     * @throws IndexerException
     */
//@Ignore
    @Test
    public void testGetTopLevelNotTopLevel2() throws SQLException, IndexerException {
        System.out.println("testGetTopLevelNotTopLevel2");
        String[] expectedTermsArray = { "MPATH:126", "MPATH:218" };
        List<OntologyTermBean> actualTerms = instance.getTopLevel("MPATH:151", 2);
        List<String> expectedTerms = Arrays.asList(expectedTermsArray);
        int count = 0;
        for (OntologyTermBean actualTopLevelTerm : actualTerms) {
            if (expectedTerms.contains(actualTopLevelTerm.getId()))
                count++;
        }
        assertTrue("Expected at least 2 matching ids. Found " + count, count >= 2);
    }

    /**
     * Test top level, level 3
     * 
     * @throws SQLException
     * @throws IndexerException
     */
//@Ignore
    @Test
    public void testGetTopLevelNotTopLevel3() throws SQLException, IndexerException {
        System.out.println("testGetTopLevelNotTopLevel3");
        String[] expectedTermsArray = { "MPATH:602", "MPATH:556" };
        List<OntologyTermBean> actualTerms = instance.getTopLevel("MPATH:151", 3);
        List<String> expectedTerms = Arrays.asList(expectedTermsArray);
        int count = 0;
        for (OntologyTermBean actualTopLevelTerm : actualTerms) {
            if (expectedTerms.contains(actualTopLevelTerm.getId()))
                count++;
        }
        assertTrue("Expected at least 2 matching ids. Found " + count, count >= 2);
    }

    /**
     * Test top level, level 4
     * 
     * @throws SQLException
     * @throws IndexerException
     */
//@Ignore
    @Test
    public void testGetTopLevelNotTopLevel4() throws SQLException, IndexerException {
        System.out.println("testGetTopLevelNotTopLevel4");
        String[] expectedTermsArray = { "MPATH:149" };
        List<OntologyTermBean> actualTerms = instance.getTopLevel("MPATH:151", 4);
        List<String> expectedTerms = Arrays.asList(expectedTermsArray);
        int count = 0;
        for (OntologyTermBean actualTopLevelTerm : actualTerms) {
            if (expectedTerms.contains(actualTopLevelTerm.getId()))
                count++;
        }
        assertTrue("Expected at least 1 matching id. Found " + count, count >= 1);
    }

    /**
     * Test top level, level 5
     * 
     * @throws SQLException
     * @throws IndexerException
     */
//@Ignore
    @Test
    public void testGetTopLevelNotTopLevel5() throws SQLException, IndexerException {
        System.out.println("testGetTopLevelNotTopLevel5");
        List<OntologyTermBean> actualTerms = instance.getTopLevel("MPATH:151", 5);
        assertTrue("Expected no matching ids. Found " + actualTerms.size(), actualTerms.isEmpty());
    }

    /**
     * Test top level, from top id
     * 
     * @throws SQLException
     * @throws IndexerException
     */
//@Ignore
    @Test
    public void testGetTopLevelFromTopDefaultLevel() throws SQLException, IndexerException {
        System.out.println("testGetTopLevelFromTopDefaultLevel");
        List<OntologyTermBean> actualTerms = instance.getTopLevel("MPATH:603");
        assertTrue("Expected no matching ids. Found " + actualTerms.size(), actualTerms.isEmpty());
    }

    /**
     * Test top level, from top id
     * 
     * @throws SQLException
     * @throws IndexerException
     */
//@Ignore
    @Test
    public void testGetTopLevelFromTopLevel1() throws SQLException, IndexerException {
        System.out.println("testGetTopLevelFromTopLevel1");
        List<OntologyTermBean> actualTerms = instance.getTopLevel("MPATH:603", 1);
        assertTrue("Expected no matching ids. Found " + actualTerms.size(), actualTerms.isEmpty());
    }

    /**
     * Test top level, from top id
     * 
     * @throws SQLException
     * @throws IndexerException
     */
//@Ignore
    @Test
    public void testGetTopLevelFromTopLevel2() throws SQLException, IndexerException {
        System.out.println("testGetTopLevelFromTopLevel2");
        List<OntologyTermBean> actualTerms = instance.getTopLevel("MPATH:603", 2);
        assertTrue("Expected no matching ids. Found " + actualTerms.size(), actualTerms.isEmpty());
    }

    /**
     * Test getAncestorTerms
     * 
     * Expected result: ["MP:0005397", "MP:0002396", "MP:0002429", "MP:0002123",
     *                   "MP:0005387", "MP:00000685", "MP:00000716"]
     */
//@Ignore
    @Test
    public void testGetAncestorTerms() {
        System.out.println("testGetAncestorTerms");
        List<OntologyTermBean> actualTerms = instance.getAncestors("MPATH:151");
        String[] expectedTermsArray = { "MPATH:603", "MPATH:126", "MPATH:602",
                                        "MPATH:149", "MPATH:218", "MPATH:556" };
        List<String> expectedTerms = Arrays.asList(expectedTermsArray);
        int count = 0;
        for (OntologyTermBean actualTopLevelTerm : actualTerms) {
            if (expectedTerms.contains(actualTopLevelTerm.getId()))
                count++;
        }
        assertTrue("Expected at least 6 matching ids. Found " + count, count >= 6);
    }

    /**
     * Test getParentTerms
     * 
     * Expected result: ["MP:0002123", "MP:00000716"]
     */
//@Ignore
    @Test
    public void testGetParentTerms() {
        System.out.println("testGetParentTerms");
        List<OntologyTermBean> actualTerms = instance.getParents("MPATH:151");
        String[] expectedTermsArray = { "MPATH:149", "MPATH:556" };
        List<String> expectedTerms = Arrays.asList(expectedTermsArray);
        int count = 0;
        for (OntologyTermBean actualTopLevelTerm : actualTerms) {
            if (expectedTerms.contains(actualTopLevelTerm.getId()))
                count++;
        }
        assertTrue("Expected at least 2 matching ids. Found " + count, count >= 2);
    }

    /**
     * Test getIntermediateTerms
     * 
     * Expected result: ["MP:0005397", "MP:0002396", "MP:0002429", "MP:0002123",
     *                   "MP:0005387", "MP:00000685", "MP:00000716"]
     */
//@Ignore
    @Test
    public void testGetIntermediateTerms() {
        System.out.println("testGetIntermediateTerms");
        List<OntologyTermBean> actualTerms = instance.getIntermediates("MPATH:151");
        String[] expectedTermsArray = { "MPATH:126", "MPATH:602",
                                        "MPATH:149", "MPATH:218", "MPATH:556" };
        List<String> expectedTerms = Arrays.asList(expectedTermsArray);
        int count = 0;
        for (OntologyTermBean actualTopLevelTerm : actualTerms) {
            if (expectedTerms.contains(actualTopLevelTerm.getId()))
                count++;
        }
        assertTrue("Expected at least 5 matching ids. Found " + count, count >= 5);
    }

    /**
     * Test getChildTerms
     * 
     * Expected result: ["MP:0008247"]
     */
//@Ignore
    @Test
    public void testGetChildTerms() {
        System.out.println("testGetChildTerms");
        List<OntologyTermBean> actualTerms = instance.getChildren("MPATH:151");
        assertTrue(actualTerms.isEmpty());
        
        actualTerms = instance.getChildren("MPATH:149");
        
        String[] expectedTermsArray =
            { "MPATH:154", "MPATH:152", "MPATH:157", "MPATH:158", "MPATH:151", "MPATH:153", "MPATH:150" };
        
        List<String> expectedTerms = Arrays.asList(expectedTermsArray);
        int count = 0;
        for (OntologyTermBean actualTopLevelTerm : actualTerms) {
            if (expectedTerms.contains(actualTopLevelTerm.getId()))
                count++;
        }
        assertEquals(expectedTermsArray.length, count);
    }
    
    /**
     * Test getDescendentTerms
     * 
     * Expected result count: 9 unique descendent terms:
     *     ["MPATH:154", "MPATH:156", "MPATH:155",
            "MPATH:152", "MPATH:157", "MPATH:158",
            "MPATH:151", "MPATH:153", "MPATH:150"]
     */
//@Ignore
    @Test
    public void testGetDescendentTermsDefault() {
        System.out.println("testGetDescendentTermsDefault");
        List<OntologyTermBean> actualTerms = instance.getDescendents("MPATH:149");
        String[] expectedTermsArray = { "MPATH:154", "MPATH:156", "MPATH:155",
                                        "MPATH:152", "MPATH:157", "MPATH:158",
                                        "MPATH:151", "MPATH:153", "MPATH:150" };
        String errMsg = "Expected 9 unique descendents but found " + actualTerms.size();
        if (actualTerms.size() != 9)
            fail (errMsg);
        // Check them.
        assertEquals(expectedTermsArray[0], actualTerms.get(0).getId());
        assertEquals(expectedTermsArray[1], actualTerms.get(1).getId());
        assertEquals(expectedTermsArray[2], actualTerms.get(2).getId());
        assertEquals(expectedTermsArray[3], actualTerms.get(3).getId());
        assertEquals(expectedTermsArray[4], actualTerms.get(4).getId());
        assertEquals(expectedTermsArray[5], actualTerms.get(5).getId());
        assertEquals(expectedTermsArray[6], actualTerms.get(6).getId());
        assertEquals(expectedTermsArray[7], actualTerms.get(7).getId());
        assertEquals(expectedTermsArray[8], actualTerms.get(8).getId());
    }
    
    private String joinIds(List<OntologyTermBean> terms) {
        String retVal = "";
        for (OntologyTermBean term : terms) {
            if ( ! retVal.isEmpty())
                retVal += ", ";
            retVal += term.getId();
        }
        
        return retVal;
    }
    
    /**
     * Test getDescendentTermsLevel
     */
//@Ignore
    @Test
    public void testGetDescendentTermsWithLevels() {
        System.out.println("testGetDescendentTermsWithLevels");
        String[] expectedTermIdsArray;
        
        List<OntologyTermBean> actualTerms = instance.getDescendents("MPATH:151", 1);
        assertTrue("Expected no descendents for level 1, MPATH:151", actualTerms.isEmpty());
        
        actualTerms = instance.getDescendents("MPATH:149", 1);                  // level 1.
        expectedTermIdsArray = new String[]
            { "MPATH:154", "MPATH:152", "MPATH:157", "MPATH:158", "MPATH:151", "MPATH:153", "MPATH:150" };
        List<String> expectedTerms = Arrays.asList(expectedTermIdsArray);
        int count = 0;
        for (OntologyTermBean actualTopLevelTerm : actualTerms) {
            if (expectedTerms.contains(actualTopLevelTerm.getId()))
                count++;
        }
        assertEquals(expectedTermIdsArray.length, count);
        
        actualTerms = instance.getDescendents("MPATH:149", 2);                  // level 2.
        assertEquals(1, actualTerms.size());
        assertEquals("MPATH:156", actualTerms.get(0).getId());
        
        actualTerms = instance.getDescendents("MPATH:149", 3);                  // level 3.
        assertEquals(1, actualTerms.size());
        assertEquals("MPATH:155", actualTerms.get(0).getId());
        
        actualTerms = instance.getDescendents("MPATH:149", 4);                  // [non-existent] level 4.
        assertEquals(0, actualTerms.size());
    }
}