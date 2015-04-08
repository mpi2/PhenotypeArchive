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

package uk.ac.ebi.phenotype.dao;

import edu.emory.mathcs.backport.java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
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
import uk.ac.ebi.phenotype.service.dto.ReferenceDTO;

/**
 *
 * @author mrelac
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-config.xml" })
public class ReferenceDAOTest {
    private ReferenceDTO expected25155611;
    
    @Autowired
    ReferenceDAO referenceDAO;
    
    @PostConstruct
    public void initialize() {
        expected25155611 = new ReferenceDTO();
        expected25155611.setAlleleIds(Arrays.asList(new String[] { "MGI:4431566", "MGI:4434431" }));
        expected25155611.setAlleleSymbols(Arrays.asList(new String[] { "Aldh2<tm1a(EUCOMM)Wtsi>", "Fanca<tm1a(EUCOMM)Wtsi>" }));
        expected25155611.setDateOfPublication("2014 Sep");
        expected25155611.setGrantAgencies(Arrays.asList(new String[] { "Cancer Research UK", "Medical Research Council" }));
        expected25155611.setGrantIds(Arrays.asList(new String[] { "13647", "MC_U105178811" }));
        expected25155611.setImpcGeneLinks(Arrays.asList(new String[] { "http://www.mousephenotype.org/data/genes/MGI:99600", "http://www.mousephenotype.org/data/genes/MGI:1341823" }));
        expected25155611.setJournal("Molecular cell");
        expected25155611.setMgiAlleleNames(Arrays.asList(new String[] { "aldehyde dehydrogenase 2, mitochondrial; targeted mutation 1a, Wellcome Trust Sanger Institute", "Fanconi anemia, complementation group A; targeted mutation 1a, Wellcome Trust Sanger Institute" }));
        expected25155611.setPaperLinks(Arrays.asList(new String[] { "http://dx.doi.org/10.1016/j.molcel.2014.07.010", "http://europepmc.org/articles/PMC4175174", "http://europepmc.org/articles/PMC4175174?pdf=render" }));
        expected25155611.setPmid("25155611");
        expected25155611.setTitle("Maternal aldehyde elimination during pregnancy preserves the fetal genome.");
    }

    public ReferenceDAOTest() {
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
     * Test of getReferenceRows method, of class ReferenceDAO.
     */
    @Test
    public void testGetReferenceRowsNoFilter() throws Exception {
        System.out.println("getReferenceRows no filter");
        String filter = "";
        List<ReferenceDTO> resultList = referenceDAO.getReferenceRows(filter);
        
        String expectedPmid = "25155611";
        ReferenceDTO actual25155611 = referenceDAO.getReferenceByPmid(resultList, expectedPmid);
        assertEquals(209, resultList.size());
        assertEquals(expected25155611, actual25155611);
    }

    /**
     * Test of getReferenceRows method, of class ReferenceDAO.
     */
    @Test
    public void testGetReferenceRowsWithFilter() throws Exception {
        System.out.println("getReferenceRows with filter");
        String filter = "25155611";
        List<ReferenceDTO> resultList = referenceDAO.getReferenceRows(filter);
        
        String expectedPmid = "25155611";
        ReferenceDTO actual25155611 = referenceDAO.getReferenceByPmid(resultList, expectedPmid);
        assertEquals(1, resultList.size());
        assertEquals(expected25155611, actual25155611);
    }

    /**
     * Test of getPmidsToOmit method, of class ReferenceDAO.
     */
    @Test
    public void testGetPmidsToOmit() throws Exception {
        System.out.println("getPmidsToOmit");
        String expResult = "'21677750'";
        String result = referenceDAO.getPmidsToOmit();
        assertEquals(expResult, result);
    }

}