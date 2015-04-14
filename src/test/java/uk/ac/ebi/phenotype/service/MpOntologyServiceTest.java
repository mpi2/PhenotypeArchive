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

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.phenotype.solr.indexer.exceptions.IndexerException;
import uk.ac.ebi.phenotype.solr.indexer.beans.OntologyTermBean;

/**
 *
 * @author mrelac
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-config.xml" })
public class MpOntologyServiceTest {

    @Autowired
    MpOntologyService instance;
    
    public MpOntologyServiceTest() {
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
        List<List<String>> mp0008246 = instance.getAncestorGraphs("MP:0008246");
        
        String errMsg = "Expected size 2. Actual size = " + mp0008246.size() + ".";
        assertTrue(errMsg, mp0008246.size() >= 2);
        for (List<String> graphs : mp0008246) {
            if (graphs.get(0).equals("MP:0005387")) {
                    assertEquals("MP:0005387", graphs.get(0));
                    assertEquals("MP:0000685", graphs.get(1));
                    assertEquals("MP:0000716", graphs.get(2));
            } else {
                    assertEquals("MP:0005397", graphs.get(0));
                    assertEquals("MP:0002396", graphs.get(1));
                    assertEquals("MP:0002429", graphs.get(2));
                    assertEquals("MP:0002123", graphs.get(3));
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
        List<List<String>> mp0008246 = instance.getDescendentGraphs("MP:0008246");
        
        String errMsg = "Expected size 6. Actual size = " + mp0008246.size() + ".";
        assertTrue(errMsg, mp0008246.size() == 6);
        
        for (List<String> graphs : mp0008246) {
            
            switch (graphs.size()) {
                case 594:
                    assertEquals("MP:0008247", graphs.get(0));
                    assertEquals("MP:0013022", graphs.get(593));
                    break;
                    
                case 52:
                    assertEquals("MP:0008250", graphs.get(0));
                    assertEquals("MP:0000219", graphs.get(51));
                    break;
                    
                case 309:
                    assertEquals("MP:0000217", graphs.get(0));
                    assertEquals("MP:0008347", graphs.get(308));
                    break;
                    
                default:
                    fail ("Expected size of: 594, 52, or 309. Size was " + graphs.size());
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
        List<String> actualSynonyms = instance.getSynonyms("MP:0001293");
        String[] expectedSynonymsArray = new String[] { "absence of eyes", "absent eyes", "eyeless" };
        
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
        List<OntologyTermBean> actualTerms = instance.getTopLevel("MP:0008246");
        String[] expectedTermsArray = { "MP:0005397", "MP:0005387" };
        List<String> expectedTerms = Arrays.asList(expectedTermsArray);
        int count = 0;
        for (OntologyTermBean actualTopLevelTerm : actualTerms) {
            if (expectedTerms.contains(actualTopLevelTerm.getId()))
                count++;
        }
        assertTrue("Expected at least 2 matching ids. Found " + count, count >= 2);
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
        List<OntologyTermBean> actualTerms = instance.getTopLevel("MP:0008246", 1);
        String[] expectedTermsArray = { "MP:0005397", "MP:0005387" };
        List<String> expectedTerms = Arrays.asList(expectedTermsArray);
        int count = 0;
        for (OntologyTermBean actualTopLevelTerm : actualTerms) {
            if (expectedTerms.contains(actualTopLevelTerm.getId()))
                count++;
        }
        assertTrue("Expected at least 2 matching ids. Found " + count, count >= 2);
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
        List<OntologyTermBean> actualTerms = instance.getTopLevel("MP:0008246", 2);
        String[] expectedTermsArray = { "MP:0002396", "MP:0000685" };
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
        List<OntologyTermBean> actualTerms = instance.getTopLevel("MP:0008246", 3);
        String[] expectedTermsArray = { "MP:0002429", "MP:0000716" };
        List<String> expectedTerms = Arrays.asList(expectedTermsArray);
        int count = 0;
        for (OntologyTermBean actualTerm : actualTerms) {
            if (expectedTerms.contains(actualTerm.getId()))
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
        List<OntologyTermBean> topLevelTerms = instance.getTopLevel("MP:0008246", 4);
        String[] expectedTopLevelTermsArray = { "MP:0002123" };
        List<String> expectedTerms = Arrays.asList(expectedTopLevelTermsArray);
        int count = 0;
        for (OntologyTermBean actualTerms : topLevelTerms) {
            if (expectedTerms.contains(actualTerms.getId()))
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
        List<OntologyTermBean> actualTerms = instance.getTopLevel("MP:0008246", 5);
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
        List<OntologyTermBean> actualTerms = instance.getTopLevel("MP:0005397");
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
        List<OntologyTermBean> actualTerms = instance.getTopLevel("MP:0005397", 1);
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
        List<OntologyTermBean> actualTerms = instance.getTopLevel("MP:0005397", 2);
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
        List<OntologyTermBean> actualTerms = instance.getAncestors("MP:0008246");
        String[] expectedTermsArray = { "MP:0005397", "MP:0002396", "MP:0002429", "MP:0002123",
                                        "MP:0005387", "MP:0000685", "MP:0000716" };
        List<String> expectedTerms = Arrays.asList(expectedTermsArray);
        int count = 0;
        for (OntologyTermBean actualTopLevelTerm : actualTerms) {
            if (expectedTerms.contains(actualTopLevelTerm.getId()))
                count++;
        }
        assertTrue("Expected at least 7 matching ids. Found " + count, count >= 7);
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
        List<OntologyTermBean> actualTerms = instance.getParents("MP:0008246");
        String[] expectedTermsArray = { "MP:0002123", "MP:0000716" };
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
        List<OntologyTermBean> actualTerms = instance.getIntermediates("MP:0008246");
        String[] expectedTermsArray = { "MP:0002396", "MP:0002429", "MP:0002123",
                                        "MP:0000685", "MP:0000716" };
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
     * Expected result: ["MP:0008247", "MP:0008250", "MP:0000217"]
     */
//@Ignore
    @Test
    public void testGetChildTerms() {
        System.out.println("testGetChildTerms");
        List<OntologyTermBean> actualTerms = instance.getChildren("MP:0008246");
        String[] expectedTermsArray = { "MP:0008247", "MP:0008250", "MP:0000217" };
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
     * Test a case with many descendents in multiple graphs, most or all are
     * duplicated in each graph.
     * 
     * Expected result count: 111 unique descendent terms.
     * First three expected: ["MP:0008247", "MP:0002619", "MP:0004939"]
     */
//@Ignore
    @Test
    public void testGetDescendentTermsDuplicates() {
        System.out.println("testGetDescendentTermsDuplicates");
        List<OntologyTermBean> actualTerms = instance.getDescendents("MP:0008246");
        String[] expectedTermsArray = { "MP:0008247", "MP:0002619", "MP:0004939" };
        String errMsg = "Expected 111 unique descendents but found " + actualTerms.size();
        assertEquals(errMsg, 111, actualTerms.size());
        
        // Check the first 3:
        assertEquals(expectedTermsArray[0], actualTerms.get(0).getId());
        assertEquals(expectedTermsArray[1], actualTerms.get(1).getId());
        assertEquals(expectedTermsArray[2], actualTerms.get(2).getId());
    }
    
    /**
     * Test getDescendentTerms
     * 
     * Test a case with many descendents in multiple graphs, most or all are
     * duplicated in each graph, using different levels.
     * 
     * Level 1: ["MP:0008247", "MP:0008250", "MP:0000217"]
     * Level 2: ["MP:0002619", "MP:0008251", "MP:0012441"]
     * Level 3: ["MP:0004939", "MP:0002648", "MP:0000222"]
     */
//@Ignore
    @Test
    public void testGetDescendentTermsWithLevels() {
        System.out.println("testGetDescendentTermsWithLevels");
        
        List<OntologyTermBean> actualTerms;
        try {
            instance.getDescendents("MP:0008246", 0);
            fail("Expected exception: level == 0.");
        } catch (Exception e) {
            // Expected behavior. Do nothing.
        }
        
        actualTerms = instance.getDescendents("MP:0008246", 1);                 // level 1.
        assertEquals(3, actualTerms.size());
        assertEquals("MP:0008247", actualTerms.get(0).getId());
        assertEquals("MP:0008250", actualTerms.get(1).getId());
        assertEquals("MP:0000217", actualTerms.get(2).getId());
        
        actualTerms = instance.getDescendents("MP:0008246", 2);                 // level 2.
        assertEquals(3, actualTerms.size());
        assertEquals("MP:0002619", actualTerms.get(0).getId());
        assertEquals("MP:0008251", actualTerms.get(1).getId());
        assertEquals("MP:0012441", actualTerms.get(2).getId());
        
        actualTerms = instance.getDescendents("MP:0008246", 3);                 // level 3.
        assertEquals(3, actualTerms.size());
        assertEquals("MP:0004939", actualTerms.get(0).getId());
        assertEquals("MP:0008248", actualTerms.get(1).getId());
        assertEquals("MP:0000223", actualTerms.get(2).getId());
        
        actualTerms = instance.getDescendents("MP:0008246", 594);               // level 594.
        assertEquals(1, actualTerms.size());
        assertEquals("MP:0013022", actualTerms.get(0).getId());
        
        actualTerms = instance.getDescendents("MP:0008246", 595);               // level 595.
        assertEquals(0, actualTerms.size());
        
        actualTerms = instance.getDescendents("MP:0008246", 2000);
        assertEquals(0, actualTerms.size());
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
