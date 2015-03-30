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
public class HpOntologyServiceTest {

    @Autowired
    HpOntologyService instance;
    
    public HpOntologyServiceTest() {
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
        List<List<String>> hp151 = instance.getAncestorGraphs("HP:0000026");
        
        String errMsg = "Expected size 3. Actual size = " + hp151.size() + ".";
        assertTrue(errMsg, hp151.size() >= 3);
        for (List<String> graphs : hp151) {
            switch (graphs.size()) {
                case 6:
                    assertEquals("HP:0000118", graphs.get(0));
                    assertEquals("HP:0000119", graphs.get(1));
                    assertEquals("HP:0000078", graphs.get(2));
                    assertEquals("HP:0000080", graphs.get(3));
                    assertEquals("HP:0012874", graphs.get(4));
                    assertEquals("HP:0000025", graphs.get(5));
                    break;
                    
                case 5:
                    assertEquals("HP:0000118", graphs.get(0));
                    assertEquals("HP:0000119", graphs.get(1));
                    assertEquals("HP:0000078", graphs.get(2));
                    assertEquals("HP:0000080", graphs.get(3));
                    assertEquals("HP:0000135", graphs.get(4));
                    break;
                    
                case 4:
                    assertEquals("HP:0000118", graphs.get(0));
                    assertEquals("HP:0000818", graphs.get(1));
                    assertEquals("HP:0008373", graphs.get(2));
                    assertEquals("HP:0000135", graphs.get(3));
                    break;
                    
                default:
                    fail("Expected 4, 5, or 6 ids. Found " + graphs.size() + ". ids: " + StringUtils.join(graphs, ", "));
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
        String errMsg;
        
        List<List<String>> hp0000026 = instance.getDescendentGraphs("HP:0000026");
        assertEquals(0, hp0000026.size());
        
        List<List<String>> hp0000025 = instance.getDescendentGraphs("HP:0000025");
        for (List<String> graphs : hp0000025) {
            
            switch (graphs.size()) {
                case 6:
                    assertEquals("HP:0008669", graphs.get(0));
                    assertEquals("HP:0000027", graphs.get(1));
                    assertEquals("HP:0011962", graphs.get(2));
                    assertEquals("HP:0011963", graphs.get(3));
                    assertEquals("HP:0011961", graphs.get(4));
                    assertEquals("HP:0000798", graphs.get(5));
                    break;
                    
                case 1:
                    assertEquals("HP:0000026", graphs.get(0));
                    break;
                    
                case 3:
                    assertEquals("HP:0012206", graphs.get(0));
                    assertEquals("HP:0012208", graphs.get(1));
                    assertEquals("HP:0012207", graphs.get(2));
                    break;
                    
                default:
                    fail ("Expected size of: 6, 1, and 3. Size was " + graphs.size());
            }
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
        String[] expectedSynonymsArray = new String[] { "Absent kidney", "Renal aplasia" };
        List<String> actualSynonyms = instance.getSynonyms("HP:0000104");
        
        List<String> expectedSynonyms = Arrays.asList(expectedSynonymsArray);
        
        String errMsg = "Expected at least 2 synonyms. Actual # synonyms = " + actualSynonyms.size() + ".";
        assertTrue(errMsg, actualSynonyms.size() >= 2);
        
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
        String[] expectedTermsArray = { "HP:0000118" };
        List<OntologyTermBean> actualTerms = instance.getTopLevel("HP:0000026");
        String errMsg = "Expected 1 top level HP:0000118 but found " + joinIds(actualTerms);
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
        String[] expectedTermsArray = { "HP:0000118" };
        List<OntologyTermBean> actualTerms = instance.getTopLevel("HP:0000026", 1);
        String errMsg = "Expected 1 top level HP:0000118 but found " + joinIds(actualTerms);
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
        String[] expectedTermsArray = { "HP:0000119", "HP:0000818" };
        List<OntologyTermBean> actualTerms = instance.getTopLevel("HP:0000026", 2);
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
        String[] expectedTermsArray = { "HP:0000078", "HP:0008373" };
        List<OntologyTermBean> actualTerms = instance.getTopLevel("HP:0000026", 3);
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
        String[] expectedTermsArray = { "HP:0000080", "HP:0000135" };
        List<OntologyTermBean> actualTerms = instance.getTopLevel("HP:0000026", 4);
        List<String> expectedTerms = Arrays.asList(expectedTermsArray);
        int count = 0;
        for (OntologyTermBean actualTopLevelTerm : actualTerms) {
            if (expectedTerms.contains(actualTopLevelTerm.getId()))
                count++;
        }
        assertTrue("Expected at least 2 matching ids. Found " + count, count >= 2);
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
        String[] expectedTermsArray = { "HP:0012874", "HP:0000135" };
        List<OntologyTermBean> actualTerms = instance.getTopLevel("HP:0000026", 5);
        List<String> expectedTerms = Arrays.asList(expectedTermsArray);
        int count = 0;
        for (OntologyTermBean actualTopLevelTerm : actualTerms) {
            if (expectedTerms.contains(actualTopLevelTerm.getId()))
                count++;
        }
        assertTrue("Expected at least 2 matching ids. Found " + count, count >= 2);
    }

    /**
     * Test top level, level 6
     * 
     * @throws SQLException
     * @throws IndexerException
     */
//@Ignore
    @Test
    public void testGetTopLevelNotTopLevel6() throws SQLException, IndexerException {
        System.out.println("testGetTopLevelNotTopLevel6");
        String[] expectedTermsArray = { "HP:0000025" };
        List<OntologyTermBean> actualTerms = instance.getTopLevel("HP:0000026", 6);
        List<String> expectedTerms = Arrays.asList(expectedTermsArray);
        int count = 0;
        for (OntologyTermBean actualTopLevelTerm : actualTerms) {
            if (expectedTerms.contains(actualTopLevelTerm.getId()))
                count++;
        }
        assertTrue("Expected at least 1 matching id. Found " + count, count >= 1);
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
        List<OntologyTermBean> actualTerms = instance.getTopLevel("HP:0000118");
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
        List<OntologyTermBean> actualTerms = instance.getTopLevel("HP:0000118", 1);
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
        List<OntologyTermBean> actualTerms = instance.getTopLevel("HP:0000118", 2);
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
        List<OntologyTermBean> actualTerms = instance.getAncestors("HP:0000026");
        String[] expectedTermsArray = { "HP:0000118", "HP:0000119", "HP:0000078",
                                        "HP:0000080", "HP:0012874", "HP:0000025",
                                        "HP:0000135", "HP:0000818", "HP:0008373"};
        List<String> expectedTerms = Arrays.asList(expectedTermsArray);
        int count = 0;
        for (OntologyTermBean actualTopLevelTerm : actualTerms) {
            if (expectedTerms.contains(actualTopLevelTerm.getId()))
                count++;
        }
        assertTrue("Expected 9 matching ids. Found " + count, count == 9);
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
        List<OntologyTermBean> actualTerms = instance.getParents("HP:0000026");
        String[] expectedTermsArray = { "HP:0000025", "HP:0000135" };
        List<String> expectedTerms = Arrays.asList(expectedTermsArray);
        int count = 0;
        for (OntologyTermBean actualTopLevelTerm : actualTerms) {
            if (expectedTerms.contains(actualTopLevelTerm.getId()))
                count++;
        }
        assertTrue("Expected exactly 2 matching ids. Found " + count, count == 2);
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
        List<OntologyTermBean> actualTerms = instance.getAncestors("HP:0000026");
        String[] expectedTermsArray = { "HP:0000119", "HP:0000078",
                                        "HP:0000080", "HP:0012874", "HP:0000025",
                                        "HP:0000135", "HP:0000818", "HP:0008373"};
        List<String> expectedTerms = Arrays.asList(expectedTermsArray);
        int count = 0;
        for (OntologyTermBean actualTopLevelTerm : actualTerms) {
            if (expectedTerms.contains(actualTopLevelTerm.getId()))
                count++;
        }
        assertTrue("Expected at least 8 matching ids. Found " + count, count >= 8);
    }

    /**
     * Test getChildTerms
     * 
     * Expected result: ["HP:0008669", "HP:0000026", "HP:0012206"]
     */
//@Ignore
    @Test
    public void testGetChildTerms() {
        System.out.println("testGetChildTerms");
        
        List<OntologyTermBean> actualTerms = instance.getChildren("HP:0000026");
        assertTrue(actualTerms.isEmpty());
        
        actualTerms = instance.getChildren("HP:0000025");
        String[] expectedTermsArray = { "HP:0008669", "HP:0000026", "HP:0012206" };
        String errMsg = "Expected " + StringUtils.join(expectedTermsArray, ", ") + " but found " + joinIds(actualTerms);
        assertEquals(errMsg, 3, actualTerms.size());
        List<String> expectedTerms = Arrays.asList(expectedTermsArray);
        int count = 0;
        for (OntologyTermBean actualTerm : actualTerms) {
            if (expectedTerms.contains(actualTerm.getId()))
                count++;
        }
        assertEquals(3, count);
    }
    
    /**
     * Test getDescendentTerms
     * 
     * Expected result count: 10 unique descendent terms:
     *     ["HP:0008669", "HP:0000027", "HP:0011962",
            "HP:0011961", "HP:0011963", "HP:0000798",
            "HP:0000026", "HP:0012206", "HP:0012208",
            "HP:0012207"]
     */
//@Ignore
    @Test
    public void testGetDescendentTermsDefault() {
        System.out.println("testGetDescendentTermsDefault");
        List<OntologyTermBean> actualTerms = instance.getDescendents("HP:0000025");
        String[] expectedTermsArray = { "HP:0008669", "HP:0000027", "HP:0011962",
                                        "HP:0011963", "HP:0011961", "HP:0000798",
                                        "HP:0000026", "HP:0012206", "HP:0012208",
                                        "HP:0012207" };
        String errMsg = "Expected 10 unique descendents but found " + actualTerms.size();
        if (actualTerms.size() != 10)
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
        assertEquals(expectedTermsArray[9], actualTerms.get(9).getId());
    }
    
    /**
     * Test getDescendentTermsLevel
     */
//@Ignore
    @Test
    public void testGetDescendentTermsWithLevels() {
        System.out.println("testGetDescendentTermsWithLevels");
                
        List<OntologyTermBean> actualTerms;
        try {
            instance.getDescendents("HP:0000026", 0);
            fail("Expected exception: level == 0.");
        } catch (Exception e) {
            // Expected behavior. Do nothing.
        }
        
        actualTerms = instance.getDescendents("HP:0000025", 1);
        assertEquals(3, actualTerms.size());
        assertEquals("HP:0008669", actualTerms.get(0).getId());
        assertEquals("HP:0000026", actualTerms.get(1).getId());
        assertEquals("HP:0012206", actualTerms.get(2).getId());
        
        actualTerms = instance.getDescendents("HP:0000025", 2);
        assertEquals(2, actualTerms.size());
        assertEquals("HP:0000027", actualTerms.get(0).getId());
        assertEquals("HP:0012208", actualTerms.get(1).getId());
        
        actualTerms = instance.getDescendents("HP:0000025", 3);
        assertEquals(2, actualTerms.size());
        assertEquals("HP:0011962", actualTerms.get(0).getId());
        assertEquals("HP:0012207", actualTerms.get(1).getId());
        
        actualTerms = instance.getDescendents("HP:0000025", 4);
        assertEquals(1, actualTerms.size());
        assertEquals("HP:0011963", actualTerms.get(0).getId());
        
        actualTerms = instance.getDescendents("HP:0000025", 5);
        assertEquals(1, actualTerms.size());
        assertEquals("HP:0011961", actualTerms.get(0).getId());
        
        actualTerms = instance.getDescendents("HP:0000025", 6);
        assertEquals(1, actualTerms.size());
        assertEquals("HP:0000798", actualTerms.get(0).getId());
        
        actualTerms = instance.getDescendents("HP:0000025", 7);
        assertEquals(0, actualTerms.size());
        
        actualTerms = instance.getDescendents("HP:0000025", 2000);
        assertTrue(actualTerms.isEmpty());
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
}