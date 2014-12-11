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

package uk.ac.ebi.phenotype.solr.indexer;

import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.phenotype.service.GeneService;

/**
 *
 * @author mrelac
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-config.xml" })
public class IndexerManagerTest {

    @Autowired
    protected GeneService geneService;
    public IndexerManagerTest() {
    }
    
    public final String NO_DEPS_ERROR_MESSAGE = "Invalid argument 'nodeps' specified with empty core list.";
    
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
      * Test invoking static main with no valid context.
      * 
      * Expected results: STATUS_NO_CONTEXT.
      */
     @Test
     public void testCommandLineNoArgs() {
        int retVal =  IndexerManager.main(new String[] { });
         
        switch (retVal) {
            case IndexerManager.STATUS_NO_CONTEXT:
                break;
                
            default:
                fail("Expected MissingRequiredOptionsException");
                break;
        }
     }
     
     /**
      * Test invoking static main with valid context.
      * 
      * Expected results: STATUS_OK.
      */
     @Test
     public void testCommandLineContext() {
        String args[] = { "--context=index-config_DEV.xml" };
        int retVal =  IndexerManager.main(args);
         
        switch (retVal) {
            case IndexerManager.STATUS_OK:
                break;

            default:
                fail("Expected success using context file '" + "'");
        }
     }
     
     /**
      * Test missing cores without nodeps option.
      * 
      * Expected results:
      * 1. build all cores.
      * 2. nodeps = false.
      */
     @Test
     public void testCommandLineMissingCoresWithoutNodeps() {
        String[] args = new String[] { "--context=index-config_DEV.xml" };
        IndexerManager indexerManager = new IndexerManager();
        try {
            indexerManager.initialise(args);
        } catch (IndexerException ie) {
            fail(ie.getLocalizedMessage());
        }
        
        String[] actualCores = indexerManager.getCores().toArray(new String[0]);
        assertArrayEquals(actualCores, IndexerManager.allCoresArray);
        assertEquals(false, indexerManager.getNodeps());
     }
     
     /**
      * Test missing cores with nodeps option.
      * 
      * Expected results: IndexerException with text containing
      * 'Invalid argument 'nodeps' specified with empty core list'.
      */
     @Test
     public void testCommandLineMissingCoresWithNodeps() {
        String[] args = new String[] { "--context=index-config_DEV.xml", "--nodeps" };
        IndexerManager indexManager = new IndexerManager();
        try {
            indexManager.initialise(args);
        } catch (IndexerException ie) {
            if (ie.getLocalizedMessage().contains(NO_DEPS_ERROR_MESSAGE)) {
                // Do nothing. This is the expected exception.
            } else {
                fail(ie.getLocalizedMessage());
            }
        } catch (Exception e) {
            fail(e.getLocalizedMessage());
        }
     }
     
     /**
      * Test empty cores without nodeps option.
      * 
      * Expected results:
      * 1. build all cores.
      * 2. nodeps = false.
      */
     @Test
     public void testCommandLineEmptyCoresWithoutNodeps() {
        String[] args = new String[] { "--context=index-config_DEV.xml", "--cores=" };
        IndexerManager indexerManager = new IndexerManager();
        try {
            indexerManager.initialise(args);
        } catch (IndexerException ie) {
            fail(ie.getLocalizedMessage());
        }
        
        String[] actualCores = indexerManager.getCores().toArray(new String[0]);
        assertArrayEquals(actualCores, IndexerManager.allCoresArray);
        assertEquals(false, indexerManager.getNodeps());
     }
     
     /**
      * Test empty cores with nodeps option.
      * 
      * Expected results: IndexerException with text containing
      * 'Invalid argument 'nodeps' specified with empty core list'.
      */
     @Test
     public void testCommandLineEmptyCoresWithNodeps() {
        String[] args = new String[] { "--context=index-config_DEV.xml", "--nodeps", "--cores=" };
        IndexerManager indexManager = new IndexerManager();
        try {
            indexManager.initialise(args);
        } catch (IndexerException ie) {
            if (ie.getLocalizedMessage().contains(NO_DEPS_ERROR_MESSAGE)) {
                // Do nothing. This is the expected exception.
            } else {
                fail(ie.getLocalizedMessage());
            }
        } catch (Exception e) {
            fail(e.getLocalizedMessage());
        }
     }
     
     /**
      * Test single cores without nodeps option.
      * 
      * Expected results:
      * 1. build specified core.
      * 2. nodeps = false.
      */
     @Test
     public void testCommandLine1CoresWithoutNodeps() {
        String[] args = new String[] { "--context=index-config_DEV.xml", "--cores=experiment" };
        IndexerManager indexerManager = new IndexerManager();
        try {
            indexerManager.initialise(args);
        } catch (IndexerException ie) {
            fail(ie.getLocalizedMessage());
        }
        
        String[] actualCores = indexerManager.getCores().toArray(new String[0]);
        assertArrayEquals(actualCores, new String[] { "experiment" });
        assertEquals(false, indexerManager.getNodeps());
     }
     
     /**
      * Test single cores with nodeps option.
      * 
      * Expected results:
      * 1. build specified core.
      * 2. nodeps = true.
      */
     @Test
     public void testCommandLine1CoresWithNodeps() {
        String[] args = new String[] { "--context=index-config_DEV.xml", "--cores=experiment", "--nodeps" };
        IndexerManager indexerManager = new IndexerManager();
        try {
            indexerManager.initialise(args);
        } catch (IndexerException ie) {
            fail(ie.getLocalizedMessage());
        }
        
        String[] actualCores = indexerManager.getCores().toArray(new String[0]);
        assertArrayEquals(actualCores, new String[] { "experiment" });
        assertEquals(true, indexerManager.getNodeps());
     }
     
     /**
      * Test invalid cores.
      * 
      * Expected results: IndexerException with text containing
      * 'Invalid argument 'nodeps' specified with empty core list'.
      */
     @Test
     public void testCommandLineInvalidCores() {
        String[] args = new String[] { "--context=index-config_DEV.xml", "--cores=junk" };
        IndexerManager indexManager = new IndexerManager();
        try {
            indexManager.initialise(args);
        } catch (IndexerException ie) {
            if (ie.getLocalizedMessage().contains("Invalid core name")) {
                // Do nothing. This is the expected exception.
            } else {
                fail(ie.getLocalizedMessage());
            }
        } catch (Exception e) {
            fail(e.getLocalizedMessage());
        }
     }
     
     /**
      * Test two cores without nodeps option.
      * 
      * Expected results:
      * 1. build specified cores, in the order specified.
      * 2. nodeps = false.
      */
     @Test
     public void testCommandLine2CoresWithoutNodeps() {
        String[] args = new String[] { "--context=index-config_DEV.xml", "--cores=experiment,allele" };
        IndexerManager indexerManager = new IndexerManager();
        try {
            indexerManager.initialise(args);
        } catch (IndexerException ie) {
            fail(ie.getLocalizedMessage());
        }
        
        String[] actualCores = indexerManager.getCores().toArray(new String[0]);
        assertArrayEquals(actualCores, new String[] { "experiment", "allele" });
        assertEquals(true, indexerManager.getNodeps());
     }
     
     /**
      * Test two cores with nodeps option.
      * 
      * Expected results:
      * 1. build specified core.
      * 2. nodeps = true.
      */
     @Test
     public void testCommandLine2CoresWithNodeps() {
        String[] args = new String[] { "--context=index-config_DEV.xml", "--cores=experiment,allele" };
        IndexerManager indexerManager = new IndexerManager();
        try {
            indexerManager.initialise(args);
        } catch (IndexerException ie) {
            fail(ie.getLocalizedMessage());
        }
        
        String[] actualCores = indexerManager.getCores().toArray(new String[0]);
        assertArrayEquals(actualCores, new String[] { "experiment", "allele" });
        assertEquals(true, indexerManager.getNodeps());
     }
     
}