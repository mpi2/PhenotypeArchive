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
import org.junit.Ignore;
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
//@Ignore
     public void testStaticCommandLineNoArgs() {
        String testName = "testStaticCommandLineNoArgs";
        System.out.println("-------------------" + testName + "-------------------");
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
      * Test invoking static main with empty context.
      * 
      * Expected results: STATUS_NO_CONTEXT.
      */
     @Test
//@Ignore
     public void testStaticCommandLineEmptyContext() {
        String testName = "testStaticCommandLineEmptyContext";
        System.out.println("-------------------" + testName + "-------------------");
        String args[] = { "--context=" };
        int retVal =  IndexerManager.main(args);
         
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
//@Ignore
     public void testStaticCommandLineValidContext() {
        String testName = "testStaticCommandLineValidContext";
        System.out.println("-------------------" + testName + "-------------------");
        String args[] = { "--context=index-config_DEV.xml" };
        int retVal =  IndexerManager.main(args);
         
        switch (retVal) {
            case IndexerManager.STATUS_OK:
                break;

            default:
                fail("Expected success using context file 'index-config_DEV.xml'");
        }
     }
     
     /**
      * Test invoking static main with invalid nodeps
      * 
      * Expected results: STATUS_NO_DEPS.
      */
     @Test
//@Ignore
     public void testStaticCommandLineInvalidNoDeps() {
        String testName = "testStaticCommandLineInvalidNoDeps";
        System.out.println("-------------------" + testName + "-------------------");
        String args[] = { "--context=index-config_DEV.xml", "--nodeps" };
        int retVal =  IndexerManager.main(args);
         
        switch (retVal) {
            case IndexerManager.STATUS_NO_DEPS:
                break;

            default:
                fail("Expected STATUS_NO_DEPS");
        }
     }
     
     /**
      * Test invoking static main with invalid core name.
      * 
      * Expected results: STATUS_INVALID_CORE_NAME.
      */
     @Test
//@Ignore
     public void testStaticCommandLineInvalidCoreName() {
        String testName = "testStaticCommandLineInvalidCoreName";
        System.out.println("-------------------" + testName + "-------------------");
        String args[] = { "--context=index-config_DEV.xml", "--cores=junk" };
        int retVal =  IndexerManager.main(args);
         
        switch (retVal) {
            case IndexerManager.STATUS_INVALID_CORE_NAME:
                break;

            default:
                fail("Expected STATUS_INVALID_CORE_NAME");
        }
     }
     
     
     // INSTANCE TESTS (i.e. creates an IndexManager instance)
     
     
     /**
      * Test missing cores without nodeps option.
      * 
      * Expected results:
      * 1. build all cores.
      * 2. nodeps = false.
      */
     @Test
//@Ignore
     public void testCommandLineMissingCoresWithoutNodeps() {
        String testName = "testCommandLineMissingCoresWithoutNodeps";
        System.out.println("-------------------" + testName + "-------------------");
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
      * Expected results: IndexerException with cause: NoDepsException
      */
     @Test
//@Ignore
     public void testCommandLineMissingCoresWithNodeps() {
        String testName = "testCommandLineMissingCoresWithNodeps";
        System.out.println("-------------------" + testName + "-------------------");
        String[] args = new String[] { "--context=index-config_DEV.xml", "--nodeps" };
        IndexerManager indexManager = new IndexerManager();
        try {
            indexManager.initialise(args);
        } catch (IndexerException ie) {
            if (ie.getCause() instanceof NoDepsException) {
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
//@Ignore
     public void testCommandLineEmptyCoresWithoutNodeps() {
        String testName = "testCommandLineEmptyCoresWithoutNodeps";
        System.out.println("-------------------" + testName + "-------------------");
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
      * Expected results: IndexerException with cause: NoDepsException
      */
     @Test
//@Ignore
     public void testCommandLineEmptyCoresWithNodeps() {
        String testName = "testCommandLineEmptyCoresWithNodeps";
        System.out.println("-------------------" + testName + "-------------------");
        String[] args = new String[] { "--context=index-config_DEV.xml", "--nodeps", "--cores=" };
        IndexerManager indexManager = new IndexerManager();
        try {
            indexManager.initialise(args);
        } catch (IndexerException ie) {
            if (ie.getCause() instanceof NoDepsException) {
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
//@Ignore
     public void testCommandLine1CoresWithoutNodeps() {
        String testName = "testCommandLine1CoresWithoutNodeps";
        System.out.println("-------------------" + testName + "-------------------");
        String[] args = new String[] { "--context=index-config_DEV.xml", "--cores=observation" };
        IndexerManager indexerManager = new IndexerManager();
        try {
            indexerManager.initialise(args);
        } catch (IndexerException ie) {
            fail(ie.getLocalizedMessage());
        }
        
        String[] actualCores = indexerManager.getCores().toArray(new String[0]);
        assertArrayEquals(actualCores, new String[] { IndexerManager.OBSERVATION_CORE });
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
//@Ignore
     public void testCommandLine1CoresWithNodeps() {
        String testName = "testCommandLine1CoresWithNodeps";
        System.out.println("-------------------" + testName + "-------------------");
        String[] args = new String[] { "--context=index-config_DEV.xml", "--cores=observation", "--nodeps" };
        IndexerManager indexerManager = new IndexerManager();
        try {
            indexerManager.initialise(args);
        } catch (IndexerException ie) {
            fail(ie.getLocalizedMessage());
        }
        
        String[] actualCores = indexerManager.getCores().toArray(new String[0]);
        assertArrayEquals(actualCores, new String[] { "observation" });
        assertEquals(true, indexerManager.getNodeps());
     }
     
     /**
      * Test invalid cores.
      * 
      * Expected results: IndexerException with cause: InvalidCoreNameException
      */
//@Ignore
     @Test
     public void testCommandLineInvalidCores() {
        String testName = "testCommandLineInvalidCores";
        System.out.println("-------------------" + testName + "-------------------");
        String[] args = new String[] { "--context=index-config_DEV.xml", "--cores=junk" };
        IndexerManager indexManager = new IndexerManager();
        try {
            indexManager.initialise(args);
        } catch (IndexerException ie) {
            if (ie.getCause() instanceof InvalidCoreNameException) {
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
//@Ignore
     @Test
     public void testCommandLine2CoresWithoutNodeps() {
        String testName = "testCommandLine2CoresWithoutNodeps";
        System.out.println("-------------------" + testName + "-------------------");
        String[] args = new String[] { "--context=index-config_DEV.xml", "--cores=observation,allele" };
        IndexerManager indexerManager = new IndexerManager();
        try {
            indexerManager.initialise(args);
        } catch (IndexerException ie) {
            fail(ie.getLocalizedMessage());
        }
        
        String[] actualCores = indexerManager.getCores().toArray(new String[0]);
        assertArrayEquals(actualCores, new String[] { "observation", "allele" });
        assertEquals(true, indexerManager.getNodeps());
     }
     
     /**
      * Test two cores with nodeps option.
      * 
      * Expected results:
      * 1. build specified core.
      * 2. nodeps = true.
      */
//@Ignore
     @Test
     public void testCommandLine2CoresWithNodeps() {
        String testName = "testCommandLine2CoresWithNodeps";
        System.out.println("-------------------" + testName + "-------------------");
        String[] args = new String[] { "--context=index-config_DEV.xml", "--cores=observation,allele" };
        IndexerManager indexerManager = new IndexerManager();
        try {
            indexerManager.initialise(args);
        } catch (IndexerException ie) {
            fail(ie.getLocalizedMessage());
        }
        
        String[] actualCores = indexerManager.getCores().toArray(new String[0]);
        assertArrayEquals(actualCores, new String[] { "observation", "allele" });
        assertEquals(true, indexerManager.getNodeps());
     }
     
//@Ignore
     @Test
     public void testBuildMaCoreWithNodeps() {
        String testName = "testBuildMaCoreWithNodeps";
        System.out.println("-------------------" + testName + "-------------------");
        String[] args = new String[] { "--context=index-config_DEV.xml", "--cores=ma,observation" };
        int retVal = IndexerManager.main(args);
     }
}