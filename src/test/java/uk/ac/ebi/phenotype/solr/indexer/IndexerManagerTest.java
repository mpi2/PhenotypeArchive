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
    
    
    // Consult IndexerManager.parseCommandLine() javadoc for derived test cases.
    
    
    /***********************************************************************************/
    /*    THE FOLLOWING TESTS GENERATE EXPECTED EXCEPTIONS AND THUS DO NOT BUILD       */
    /*    ANY CORES. THEY ARE INTENDED TO TEST THE SPECIFIED COMMAND-LINE PARAMETERS   */
    /*    FOR INVALID COMMAND-LINE OPTIONS.                                            */
    /***********************************************************************************/
    

     /**
      * Test invoking static main with no arguments.
      * 
      * Expected results: STATUS_NO_ARGUMENT.
      */
     @Test
//@Ignore
    public void testStaticNoArgs() {
        String testName = "testStaticNoArgs";
        System.out.println("-------------------" + testName + "-------------------");
        int retVal =  IndexerManager.mainReturnsStatus(new String[] { });
         
        switch (retVal) {
            case IndexerManager.STATUS_NO_ARGUMENT:
                break;
                
            default:
                fail("Expected MissingRequiredOptionsException");
                break;
        }
    }
    
     /**
      * Test invoking IndexerManagerInstance with no arguments.
      * 
      * Expected results: MissingRequiredArgumentException.
      */
     @Test
//@Ignore
    public void testInstanceNoArgs() {
        String testName = "testInstanceNoArgs";
        System.out.println("-------------------" + testName + "-------------------");
        String[] args = new String[] { };
        IndexerManager indexerManager = new IndexerManager();
        
        // Determine which cores to build.
        try {
            indexerManager.initialise(args);
        } catch (IndexerException ie) {
            if (ie.getCause() instanceof MissingRequiredArgumentException) {
                // Do nothing. This is what we expect.
                return;
            }
        }
                
        fail("Expected MissingRequiredArgumentException");
    }
    
     /**
      * Test invoking static main with empty context.
      * 
      * Expected results: STATUS_NO_ARGUMENT.
      */
     @Test
//@Ignore
    public void testStaticEmptyContext() {
        String testName = "testStaticEmptyContext";
        System.out.println("-------------------" + testName + "-------------------");
        String[] args = { "--context=" };
        int retVal =  IndexerManager.mainReturnsStatus(args);
         
        switch (retVal) {
            case IndexerManager.STATUS_NO_ARGUMENT:
                break;
                
            default:
                fail("Expected MissingRequiredOptionsException");
                break;
        }
    }
    
     /**
      * Test invoking IndexerManager instance with empty context.
      * 
      * Expected results: MissingRequiredArgumentException.
      */
     @Test
//@Ignore
    public void testInstanceEmptyContext() {
        String testName = "testInstanceEmptyContext";
        System.out.println("-------------------" + testName + "-------------------");
        String[] args = { "--context=" };
        IndexerManager indexerManager = new IndexerManager();
        
        // Determine which cores to build.
        try {
            indexerManager.initialise(args);
        } catch (IndexerException ie) {
            if (ie.getCause() instanceof MissingRequiredArgumentException) {
                // Do nothing. This is what we expect.
                return;
            }
        }
                
        fail("Expected MissingRequiredArgumentException");
    }
    
     /**
      * Test invoking static main with invalid nodeps
      * 
      * Expected results: STATUS_NO_DEPS.
      */
     @Test
//@Ignore
     public void testStaticNoCoresNodeps() {
        String testName = "testStaticNoCoresNodeps";
        System.out.println("-------------------" + testName + "-------------------");
        String args[] = { "--context=index-config_DEV.xml", "--nodeps" };
        int retVal =  IndexerManager.mainReturnsStatus(args);
         
        switch (retVal) {
            case IndexerManager.STATUS_NO_DEPS:
                break;

            default:
                fail("Expected STATUS_NO_DEPS");
        }
     }
     
     /**
      * Test invoking IndexerManager instance with invalid nodeps argument specified
      * 
      * Expected results: NoDepsException.
      */
     @Test
//@Ignore
     public void testInstanceNoCoresNodeps() {
        String testName = "testInstanceNoCoresNodeps";
        System.out.println("-------------------" + testName + "-------------------");
        String args[] = { "--context=index-config_DEV.xml", "--nodeps" };
        IndexerManager indexerManager = new IndexerManager();
        
        // Determine which cores to build.
        try {
            indexerManager.initialise(args);
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
      * Test invoking static main with invalid core name.
      * 
      * Expected results: STATUS_INVALID_CORE_NAME.
      */
     @Test
//@Ignore
     public void testStaticInvalidCoreName() {
        String testName = "testStaticInvalidCoreName";
        System.out.println("-------------------" + testName + "-------------------");
        String args[] = { "--context=index-config_DEV.xml", "--cores=junk" };
        int retVal =  IndexerManager.mainReturnsStatus(args);
         
        switch (retVal) {
            case IndexerManager.STATUS_INVALID_CORE_NAME:
                break;

            default:
                fail("Expected STATUS_INVALID_CORE_NAME");
        }
     }
     
     /**
      * Test invoking static main with invalid core name.
      * 
      * Expected results: InvalidCoreNameException.
      */
    @Test
//@Ignore
    public void testInstanceInvalidCoreName() {
        String testName = "testInstanceInvalidCoreName";
        System.out.println("-------------------" + testName + "-------------------");
        String args[] = { "--context=index-config_DEV.xml", "--cores=junk" };
        IndexerManager indexerManager = new IndexerManager();
        
        // Determine which cores to build.
        try {
            indexerManager.initialise(args);
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
    
    
    /************************************************************************************************/
    /*    THE FOLLOWING TESTS ARE NOT EXPECTED TO GENERATE EXCEPTIONS; THUS THEY CAN                */
    /*    BUILD CORES. SINCE IT IS NOT THE JOB OF THE TESTS TO BUILD THE CORES, ONLY                */
    /*    THE initialise() METHOD IS RUN; THE run() METHOD THAT ACTUALLY BUILDS THE CORES           */
    /*    IS NOT RUN. THESE THEY ARE INTENDED TO TEST THE SPECIFIED COMMAND-LINE PARAMETERS         */
    /*    FOR COMMAND-LINE OPTIONS AND TO TEST THAT ONLY THE EXPECTED CORES WOULD BE BUILT.         */
    /*    testStaticXxx VERSIONS OF THESE TESTS CANNOT BE RUN BECAUSE THE run() METHOD IS ALWAYS    */
    /*    CALLED AUTOMATICALLY, THERE BEING NO WAY TO SUPPRESS IT.                                  */
    /************************************************************************************************/
     
    
    /**
      * Test invoking IndexerManager instance with no 'cores=' argument.
      * 
      * Expected results: cores preqc to autosuggest ready to run.
      */
    @Test
//@Ignore
    public void testInstanceNoCores() {
        String testName = "testInstanceNoCores";
        System.out.println("-------------------" + testName + "-------------------");
        String[] args = { "--context=index-config_DEV.xml" };
        IndexerManager indexerManager = new IndexerManager();
        
        // Determine which cores to build.
        try {
            indexerManager.initialise(args);
        } catch (IndexerException ie) {
            fail(ie.getLocalizedMessage());
        }
        
        String[] actualCores = indexerManager.getCores().toArray(new String[0]);
        assertArrayEquals(IndexerManager.allDailyCoresArray, actualCores);
    }
    
     /**
      * Test invoking IndexerManager instance with empty 'cores=' argument.
      * 
      * Expected results: MissingRequiredArgumentException.
      */
     @Test
//@Ignore
    public void testInstanceEmptyCores() {
        String testName = "testInstanceEmptyCores";
        System.out.println("-------------------" + testName + "-------------------");
        String[] args = new String[] { "--context=index-config_DEV.xml", "--cores=" };
        IndexerManager indexerManager = new IndexerManager();
        
        // Determine which cores to build.
        try {
            indexerManager.initialise(args);
        } catch (IndexerException ie) {
            if (ie.getCause() instanceof MissingRequiredArgumentException) {
                // Expected result. Do nothing.
                return;
            } else {
                fail("Expected MissingRequiredArgumentException");
            }
        }
        
        fail("Expected MissingRequiredArgumentException");
    }
     
     /**
      * Test invoking IndexerManager instance starting at the first core (the
      * observation core).
      * 
      * Expected results: cores observation to autosuggest ready to run.
      */
     @Test
//@Ignore
     public void testInstanceFirstCore() {
        String testName = "testInstanceFirstCore";
        System.out.println("-------------------" + testName + "-------------------");
        String[] args = new String[] { "--context=index-config_DEV.xml", "--cores=experiment" };
        IndexerManager indexerManager = new IndexerManager();
        
        // Determine which cores to build.
        try {
            indexerManager.initialise(args);
        } catch (IndexerException ie) {
            fail(ie.getLocalizedMessage());
        }
        
        String[] actualCores = indexerManager.getCores().toArray(new String[0]);
        assertArrayEquals(IndexerManager.allCoresArray, actualCores);
     }
     
     /**
      * Test invoking IndexerManager instance starting at the first core (the
      * observation core), using the nodeps option.
      * 
      * Expected results: the single observation core, ready to run.
      */
     @Test
//@Ignore
     public void testInstanceFirstCoreNodeps() {
        String testName = "testInstanceFirstCoreNodeps";
        System.out.println("-------------------" + testName + "-------------------");
        String[] args = new String[] { "--context=index-config_DEV.xml", "--cores=experiment", "--nodeps" };
        IndexerManager indexerManager = new IndexerManager();
        
        // Determine which cores to build.
        try {
            indexerManager.initialise(args);
        } catch (IndexerException ie) {
            fail(ie.getLocalizedMessage());
        }
        
        String[] actualCores = indexerManager.getCores().toArray(new String[0]);
        String[] expectedCores = new String[] { IndexerManager.OBSERVATION_CORE };
        assertArrayEquals(expectedCores, actualCores);
     }
     
     /**
      * Test invoking IndexerManager instance starting at the first daily core
      * (the preqc core).
      * 
      * Expected results: All of the cores from preqc to autosuggest, ready to run.
      */
     @Test
//@Ignore
     public void testInstanceFirstDailyCore() {
        String testName = "testInstanceFirstDailyCore";
        System.out.println("-------------------" + testName + "-------------------");
        String[] args = new String[] { "--context=index-config_DEV.xml", "--cores=preqc" };
        IndexerManager indexerManager = new IndexerManager();
        
        // Determine which cores to build.
        try {
            indexerManager.initialise(args);
        } catch (IndexerException ie) {
            fail(ie.getLocalizedMessage());
        }
        
        String[] actualCores = indexerManager.getCores().toArray(new String[0]);
        String[] expectedCores = new String[] {
          IndexerManager.PREQC_CORE
        , IndexerManager.ALLELE_CORE
        , IndexerManager.IMAGES_CORE
        , IndexerManager.IMPC_IMAGES_CORE
        , IndexerManager.MP_CORE
        , IndexerManager.MA_CORE
        , IndexerManager.PIPELINE_CORE
        , IndexerManager.GENE_CORE
        , IndexerManager.DISEASE_CORE
        , IndexerManager.AUTOSUGGEST_CORE
        };
        assertArrayEquals(expectedCores, actualCores);
     }
     
     /**
      * Test invoking IndexerManager instance starting at the first daily core
      * (the preqc core), using the nodeps option.
      * 
      * Expected results: the single preqc core, ready to run.
      */
     @Test
//@Ignore
     public void testInstanceFirstDailyCoreNodeps() {
        String testName = "testInstanceFirstDailyCoreNodeps";
        System.out.println("-------------------" + testName + "-------------------");
        String[] args = new String[] { "--context=index-config_DEV.xml", "--cores=preqc", "--nodeps" };
        IndexerManager indexerManager = new IndexerManager();
        
        // Determine which cores to build.
        try {
            indexerManager.initialise(args);
        } catch (IndexerException ie) {
            fail(ie.getLocalizedMessage());
        }
        
        String[] actualCores = indexerManager.getCores().toArray(new String[0]);
        String[] expectedCores = new String[] { IndexerManager.PREQC_CORE };
        assertArrayEquals(expectedCores, actualCores);
     }
     
     /**
      * Test invoking IndexerManager instance starting at the last core (the
      * autosuggest core).
      * 
      * Expected results: the single autosuggest core, ready to run.
      */
     @Test
//@Ignore
     public void testInstanceLastCore() {
        String testName = "testInstanceLastCore";
        System.out.println("-------------------" + testName + "-------------------");
        String[] args = new String[] { "--context=index-config_DEV.xml", "--cores=autosuggest" };
        IndexerManager indexerManager = new IndexerManager();
        
        // Determine which cores to build.
        try {
            indexerManager.initialise(args);
        } catch (IndexerException ie) {
            fail(ie.getLocalizedMessage());
        }
        
        String[] actualCores = indexerManager.getCores().toArray(new String[0]);
        String[] expectedCores = new String[] { IndexerManager.AUTOSUGGEST_CORE };
        assertArrayEquals(expectedCores, actualCores);
     }
     
     /**
      * Test invoking IndexerManager instance starting at the last core (the
      * autosuggest core), using the nodeps option.
      * 
      * Expected results: the single autosuggest core, ready to run.
      */
     @Test
//@Ignore
     public void testInstanceLastCoreNodeps() {
        String testName = "testInstanceLastCoreNodeps";
        System.out.println("-------------------" + testName + "-------------------");
        String[] args = new String[] { "--context=index-config_DEV.xml", "--cores=autosuggest", "--nodeps" };
        IndexerManager indexerManager = new IndexerManager();
        
        // Determine which cores to build.
        try {
            indexerManager.initialise(args);
        } catch (IndexerException ie) {
            fail(ie.getLocalizedMessage());
        }
        
        String[] actualCores = indexerManager.getCores().toArray(new String[0]);
        String[] expectedCores = new String[] { IndexerManager.AUTOSUGGEST_CORE };
        assertArrayEquals(expectedCores, actualCores);
     }
     
     /**
      * Test invoking IndexerManager instance specifying specific cores
      * 
      * Expected results: the specified cores, ready to run.
      */
     @Test
//@Ignore
     public void testInstanceMultipleCores() {
        String testName = "testInstanceMultipleCores";
        System.out.println("-------------------" + testName + "-------------------");
        String[] args = new String[] { "--context=index-config_DEV.xml", "--cores=pipeline,allele,impc_images,ma,disease,mp" };
        IndexerManager indexerManager = new IndexerManager();
        
        // Determine which cores to build.
        try {
            indexerManager.initialise(args);
        } catch (IndexerException ie) {
            fail(ie.getLocalizedMessage());
        }
        
        String[] actualCores = indexerManager.getCores().toArray(new String[0]);
        String[] expectedCores = new String[] {
          IndexerManager.PIPELINE_CORE
        , IndexerManager.ALLELE_CORE
        , IndexerManager.IMPC_IMAGES_CORE
        , IndexerManager.MA_CORE
        , IndexerManager.DISEASE_CORE
        , IndexerManager.MP_CORE
        };
        assertArrayEquals(expectedCores, actualCores);
     }
     
     /**
      * Test invoking IndexerManager instance specifying specific cores), using
      * the nodeps option.
      * 
      * Expected results: the specified cores, ready to run.
      */
     @Test
//@Ignore
     public void testInstanceMultipleCoresNodeps() {
        String testName = "testInstanceMultipleCoresNodeps";
        System.out.println("-------------------" + testName + "-------------------");
        String[] args = new String[] { "--context=index-config_DEV.xml", "--cores=pipeline,preqc,allele,impc_images,ma,disease,mp", "--nodeps" };
        IndexerManager indexerManager = new IndexerManager();
        
        // Determine which cores to build.
        try {
            indexerManager.initialise(args);
        } catch (IndexerException ie) {
            fail(ie.getLocalizedMessage());
        }
        
        String[] actualCores = indexerManager.getCores().toArray(new String[0]);
        String[] expectedCores = new String[] {
          IndexerManager.PIPELINE_CORE
        , IndexerManager.PREQC_CORE
        , IndexerManager.ALLELE_CORE
        , IndexerManager.IMPC_IMAGES_CORE
        , IndexerManager.MA_CORE
        , IndexerManager.DISEASE_CORE
        , IndexerManager.MP_CORE
        };
        assertArrayEquals(expectedCores, actualCores);
     }
    
    
    /************************************************************************************************/
    /*    THE FOLLOWING TESTS ARE NOT EXPECTED TO GENERATE EXCEPTIONS. THEY TEST THE BUILDING OF    */
    /*    THE CORES. SOME CORES TAKE A LONG TIME TO BUILD, SO ONLY CORES WITH A SHORT BUILD TIME    */
    /*    WILL BE INCLUDED HERE.                                                                    */
    /************************************************************************************************/
     
     
     /**
      * Test invoking static main specifying single core, using the nodeps option.
      * 
      * Expected results: The specified core to be built.
      */
//@Ignore
     @Test
     public void testStaticBuildSingleCoreNodeps() {
        String testName = "testStaticBuildSingleCoreNodeps";
        System.out.println("-------------------" + testName + "-------------------");
        String[] args = new String[] { "--context=index-config_DEV.xml", "--cores=ma", "--nodeps" };
        int retVal =  IndexerManager.mainReturnsStatus(args);
         
        switch (retVal) {
            case IndexerManager.STATUS_OK:
                break;

            default:
                fail("Expected STATUS_OK but found " + IndexerManager.getStatusCodeName(retVal));
        }
     }
     
     /**
      * Test invoking IndexerManager instance specifying a single core, using
      * the nodeps option.
      * 
      * Expected results: The specified core to be built.
      */
//@Ignore
     @Test
     public void testInstanceBuildSingleCoreNodeps() {
        String testName = "testInstanceBuildSingleCoreNodeps";
        System.out.println("-------------------" + testName + "-------------------");
        String[] args = new String[] { "--context=index-config_DEV.xml", "--cores=ma", "--nodeps" };
        IndexerManager indexerManager = new IndexerManager();
        
        // Determine which cores to build.
        try {
            indexerManager.initialise(args);
        } catch (IndexerException ie) {
            fail(ie.getLocalizedMessage());
        }
        
        String[] actualCores = indexerManager.getCores().toArray(new String[0]);
        String[] expectedCores = new String[] { IndexerManager.MA_CORE };
        assertArrayEquals(expectedCores, actualCores);
        
        // Initialise, validate, and build the cores.
        try {
            indexerManager.maIndexer.initialise(new String[] { "--context=index-config_DEV.xml" });
            indexerManager.maIndexer.run();
            indexerManager.maIndexer.validateBuild();
        } catch (IndexerException ie) {
            fail(ie.getLocalizedMessage());
        }
     }
     
     /**
      * Test invoking static main specifying multiple cores, using the nodeps option.
      * 
      * Expected results: The specified cores to be built.
      */
//@Ignore
     @Test
     public void testStaticBuildMultipleCoresNodeps() {
        String testName = "testStaticBuildMultipleCoresNodeps";
        System.out.println("-------------------" + testName + "-------------------");
        String[] args = new String[] { "--context=index-config_DEV.xml", "--cores=ma,ma", "--nodeps" };
        int retVal =  IndexerManager.mainReturnsStatus(args);
         
        switch (retVal) {
            case IndexerManager.STATUS_OK:
                break;

            default:
                fail("Expected STATUS_OK but found " + IndexerManager.getStatusCodeName(retVal));
        }
     }

     
     /**
      * Test invoking IndexerManager instance specifying multiple cores, using
      * the nodeps option.
      * 
      * Expected results: All of the specified cores built.
      */
//@Ignore
     @Test
     public void testInstanceBuildMultipleCoresNodeps() {
        String testName = "testInstanceBuildMultipleCoresNodeps";
        System.out.println("-------------------" + testName + "-------------------");
        String[] args = new String[] { "--context=index-config_DEV.xml", "--cores=ma,ma", "--nodeps" };
        IndexerManager indexerManager = new IndexerManager();
        
        // Determine which cores to build.
        try {
            indexerManager.initialise(args);
        } catch (IndexerException ie) {
            fail(ie.getLocalizedMessage());
        }
        
        String[] actualCores = indexerManager.getCores().toArray(new String[0]);
        String[] expectedCores = new String[] { IndexerManager.MA_CORE, IndexerManager.MA_CORE };
        assertArrayEquals(expectedCores, actualCores);
        
        // Initialise, validate, and build the cores.
        try {
            indexerManager.maIndexer.initialise(new String[] { "--context=index-config_DEV.xml" });
            indexerManager.maIndexer.run();
            indexerManager.maIndexer.validateBuild();
        } catch (IndexerException ie) {
            fail(ie.getLocalizedMessage());
        }
     }
     
     /**
      * Build daily cores. NOTE: This test is not meant to be run with the
      * test suite, as it takes a long time to complete. It is here to permit
      * building core(s) quickly and easily.
      * 
      * Expected results: The specified cores to be built.
      */
//@Ignore
//     @Test
//     public void testStaticBuildDailyCores() {
//        String testName = "testStaticBuildDailyCores";
//        System.out.println("-------------------" + testName + "-------------------");
//        String[] args = new String[] { "--context=index-config_DEV.xml", "--cores=preqc" };
//        int retVal =  IndexerManager.mainReturnsStatus(args);
//         
//        switch (retVal) {
//            case IndexerManager.STATUS_OK:
//                break;
//
//            default:
//                fail("Expected STATUS_OK but found " + IndexerManager.getStatusCodeName(retVal));
//        }
//     }
     
     /**
      * Build all cores. NOTE: This test is not meant to be run with the
      * test suite, as it takes a long time to complete. It is here to permit
      * building core(s) quickly and easily.
      * 
      * Expected results: The specified cores to be built.
      */
//@Ignore
//     @Test
//     public void testStaticBuildCores() {
//        String testName = "testStaticBuildCores";
//        System.out.println("-------------------" + testName + "-------------------");
//        String[] args = new String[] { "--context=index-config_DEV.xml", "--cores=experiment" };
//        int retVal =  IndexerManager.mainReturnsStatus(args);
//         
//        switch (retVal) {
//            case IndexerManager.STATUS_OK:
//                break;
//
//            default:
//                fail("Expected STATUS_OK but found " + IndexerManager.getStatusCodeName(retVal));
//        }
//     }
}