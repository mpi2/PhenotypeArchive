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

import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.generic.util.JSONRestUtil;
import uk.ac.ebi.phenotype.service.GeneService;
import uk.ac.ebi.phenotype.solr.indexer.exceptions.IndexerException;
import uk.ac.ebi.phenotype.solr.indexer.exceptions.InvalidCoreNameException;
import uk.ac.ebi.phenotype.solr.indexer.exceptions.MissingRequiredArgumentException;

/**
 *
 * @author mrelac
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-config.xml" })
public class IndexerManagerTest {

    @Autowired
    protected GeneService geneService;
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    protected String externalDevUrl = "http://ves-ebi-d0:8090/mi/impc/dev/solr";
    
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
        System.out.println("Command line = ");
        int retVal =  IndexerManager.mainReturnsStatus(new String[] { });
         
        switch (retVal) {
            case IndexerManager.STATUS_NO_ARGUMENT:
                break;
                
            default:
                fail("Expected STATUS_NO_ARGUMENT");
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
        System.out.println("Command line = " + StringUtils.join(args, ","));
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
        System.out.println("Command line = " + StringUtils.join(args, ","));
        int retVal =  IndexerManager.mainReturnsStatus(args);
         
        switch (retVal) {
            case IndexerManager.STATUS_NO_ARGUMENT:
                break;
                
            default:
                fail("Expected STATUS_NO_ARGUMENT");
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
        System.out.println("Command line = " + StringUtils.join(args, ","));
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
      * Expected results: STATUS_NO_ARGUMENT.
      */
     @Test
//@Ignore
     public void testStaticNoCoresNodeps() {
        String testName = "testStaticNoCoresNodeps";
        System.out.println("-------------------" + testName + "-------------------");
        String args[] = { "--context=index-config_DEV.xml", "--nodeps" };
        System.out.println("Command line = " + StringUtils.join(args, ","));
        int retVal =  IndexerManager.mainReturnsStatus(args);
         
        switch (retVal) {
            case IndexerManager.STATUS_NO_ARGUMENT:
                break;

            default:
                fail("Expected STATUS_NO_ARGUMENT");
        }
     }
     
     /**
      * Test invoking IndexerManager instance with invalid nodeps argument specified
      * 
      * Expected results: MissingRequiredArgumentException.
      */
     @Test
//@Ignore
     public void testInstanceNoCoresNodeps() {
        String testName = "testInstanceNoCoresNodeps";
        System.out.println("-------------------" + testName + "-------------------");
        String args[] = { "--context=index-config_DEV.xml", "--nodeps" };
        System.out.println("Command line = " + StringUtils.join(args, ","));
        IndexerManager indexerManager = new IndexerManager();
        
        // Determine which cores to build.
        try {
            indexerManager.initialise(args);
        } catch (IndexerException ie) {
            if (ie.getCause() instanceof MissingRequiredArgumentException) {
                // Do nothing. This is the expected exception.
            } else {
                fail("Expected MissingRequiredArgumentException");
            }
        } catch (Exception e) {
            fail("Expected MissingRequiredArgumentException");
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
        System.out.println("Command line = " + StringUtils.join(args, ","));
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
        System.out.println("Command line = " + StringUtils.join(args, ","));
        IndexerManager indexerManager = new IndexerManager();
        
        // Determine which cores to build.
        try {
            indexerManager.initialise(args);
        } catch (IndexerException ie) {
            if (ie.getCause() instanceof InvalidCoreNameException) {
                // Do nothing. This is the expected exception.
            } else {
                fail("Expected InvalidCoreNameException");
            }
        } catch (Exception e) {
            fail("Expected InvalidCoreNameException");
        }
     }
    
    /**
      * Test invoking IndexerManager instance with no 'cores=' argument.
      * 
      * Expected results: MissingRequiredArgumentException.
      */
    @Test
//@Ignore
    public void testInstanceNoCores() {
        String testName = "testInstanceNoCores";
        System.out.println("-------------------" + testName + "-------------------");
        String[] args = { "--context=index-config_DEV.xml" };
        System.out.println("Command line = " + StringUtils.join(args, ","));
        IndexerManager indexerManager = new IndexerManager();
        
        // Determine which cores to build.
        try {
            indexerManager.initialise(args);
        } catch (IndexerException ie) {
            if (ie.getCause() instanceof MissingRequiredArgumentException) {
                // Do nothing. This is the expected exception.
            } else {
                fail("Expected MissingRequiredArgumentException");
            }
        } catch (Exception e) {
            fail("Expected MissingRequiredArgumentException");
        }
    }
    
     /**
      * Test invoking IndexerManager instance with empty 'cores=' argument.
      * 
      * Expected results: MissingRequiredArgumentException.
      */
     @Test
//@Ignore
    public void testInstanceEmptyCoresNoEquals() {
        String testName = "testInstanceEmptyCoresNoEquals";
        System.out.println("-------------------" + testName + "-------------------");
        String[] args = new String[] { "--context=index-config_DEV.xml", "--cores" };
        System.out.println("Command line = " + StringUtils.join(args, ","));
        IndexerManager indexerManager = new IndexerManager();
        
        // Determine which cores to build.
        try {
            indexerManager.initialise(args);
        } catch (IndexerException ie) {
            if (ie.getCause() instanceof MissingRequiredArgumentException) {
                // Do nothing. This is the expected exception.
            } else {
                fail("Expected MissingRequiredArgumentException");
            }
        } catch (Exception e) {
            fail("Expected MissingRequiredArgumentException");
        }
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
        System.out.println("Command line = " + StringUtils.join(args, ","));
        IndexerManager indexerManager = new IndexerManager();
        
        // Determine which cores to build.
        try {
            indexerManager.initialise(args);
        } catch (IndexerException ie) {
            if (ie.getCause() instanceof MissingRequiredArgumentException) {
                // Do nothing. This is the expected exception.
            } else {
                fail("Expected MissingRequiredArgumentException");
            }
        } catch (Exception e) {
            fail("Expected MissingRequiredArgumentException");
        }
    }
    
     /**
      * Test invoking IndexerManager instance with empty 'cores=' argument, --nodeps BEFORE --cores.
      * 
      * Expected results: MissingRequiredArgumentException.
      */
     @Test
//@Ignore
    public void testInstanceEmptyCoresNoEqualsNodepsBeforeCores() {
        String testName = "testInstanceEmptyCoresNoEqualsNodepsBeforeCores";
        System.out.println("-------------------" + testName + "-------------------");
        String[] args = new String[] { "--context=index-config_DEV.xml", "--nodeps", "--cores" };
        System.out.println("Command line = " + StringUtils.join(args, ","));
        IndexerManager indexerManager = new IndexerManager();
        
        // Determine which cores to build.
        try {
            indexerManager.initialise(args);
        } catch (IndexerException ie) {
            if (ie.getCause() instanceof MissingRequiredArgumentException) {
                // Do nothing. This is the expected exception.
            } else {
                fail(ie.getLocalizedMessage());
            }
        } catch (Exception e) {
            fail(e.getLocalizedMessage());
        }
    }
    
     /**
      * Test invoking IndexerManager instance with empty 'cores=' argument, --nodeps BEFORE --cores.
      * 
      * Expected results: MissingRequiredArgumentException.
      */
     @Test
//@Ignore
    public void testInstanceEmptyCoresNodepsBeforeCores() {
        String testName = "testInstanceEmptyCoresNodepsBeforeCores";
        System.out.println("-------------------" + testName + "-------------------");
        String[] args = new String[] { "--context=index-config_DEV.xml", "--nodeps", "--cores=" };
        System.out.println("Command line = " + StringUtils.join(args, ","));
        IndexerManager indexerManager = new IndexerManager();
        
        // Determine which cores to build.
        try {
            indexerManager.initialise(args);
        } catch (IndexerException ie) {
            if (ie.getCause() instanceof MissingRequiredArgumentException) {
                // Do nothing. This is the expected exception.
            } else {
                fail("Expected MissingRequiredArgumentException");
            }
        } catch (Exception e) {
            fail("Expected MissingRequiredArgumentException");
        }
    }
    
     /**
      * Test invoking IndexerManager instance with empty 'cores=' argument, --nodeps AFTER --cores.
      * 
      * Expected results: InvalidCoreNameException.
      */
     @Test
//@Ignore
    public void testInstanceEmptyCoresNoEqualsNodepsAfterCores() {
        String testName = "testInstanceEmptyCoresNoEqualsNodepsAfterCores";
        System.out.println("-------------------" + testName + "-------------------");
        String[] args = new String[] { "--context=index-config_DEV.xml", "--cores", "--nodeps" };
        System.out.println("Command line = " + StringUtils.join(args, ","));
        IndexerManager indexerManager = new IndexerManager();
        
        // Determine which cores to build.
        try {
            indexerManager.initialise(args);
        } catch (IndexerException ie) {
            if (ie.getCause() instanceof InvalidCoreNameException) {
                // Do nothing. This is the expected exception.
            } else {
                fail("Expected InvalidCoreNameException");
            }
        } catch (Exception e) {
            fail("Expected InvalidCoreNameException");
        }
    }
    
     /**
      * Test invoking IndexerManager instance with empty 'cores=' argument, --nodeps AFTER --cores.
      * 
      * Expected results: InvalidCoreNameException.
      */
     @Test
//@Ignore
    public void testInstanceEmptyCoresNodepsAfterCores() {
        String testName = "testInstanceEmptyCoresNodepsAfterCores";
        System.out.println("-------------------" + testName + "-------------------");
        String[] args = new String[] { "--context=index-config_DEV.xml", "--cores=", "--nodeps" };
        System.out.println("Command line = " + StringUtils.join(args, ","));
        IndexerManager indexerManager = new IndexerManager();
        
        // Determine which cores to build.
        try {
            indexerManager.initialise(args);
        } catch (IndexerException ie) {
            if (ie.getCause() instanceof InvalidCoreNameException) {
                // Do nothing. This is the expected exception.
            } else {
                fail("Expected InvalidCoreNameException");
            }
        } catch (Exception e) {
            fail("Expected InvalidCoreNameException");
        }
    }
    
     /**
      * Test invoking static main with --all and --cores=ma
      * 
      * Expected results: STATUS_VALIDATION_ERROR.
      */
     @Test
//@Ignore
    public void testStaticAllAndCores() {
        String testName = "testStaticAllAndCores";
        System.out.println("-------------------" + testName + "-------------------");
        String[] args = { "--context=index-config_DEV.xml", "--all", "--cores=ma" };
        System.out.println("Command line = " + StringUtils.join(args, ","));
        int retVal =  IndexerManager.mainReturnsStatus(args);
         
        switch (retVal) {
            case IndexerManager.STATUS_VALIDATION_ERROR:
                break;
                
            default:
                fail("Expected STATUS_VALIDATION_ERROR");
                break;
        }
    }
    
     /**
      * Test invoking static main with --all and --nodeps
      * 
      * Expected results: STATUS_VALIDATION_ERROR.
      */
     @Test
//@Ignore
    public void testStaticAllAndNodeps() {
        String testName = "testStaticAllAndNodeps";
        System.out.println("-------------------" + testName + "-------------------");
        String[] args = { "--context=index-config_DEV.xml", "--all", "--nodeps" };
        System.out.println("Command line = " + StringUtils.join(args, ","));
        int retVal =  IndexerManager.mainReturnsStatus(args);
         
        switch (retVal) {
            case IndexerManager.STATUS_VALIDATION_ERROR:
                break;
                
            default:
                fail("Expected STATUS_VALIDATION_ERROR");
                break;
        }
    }
    
     /**
      * Test invoking static main with --all and --nodeps
      * 
      * Expected results: STATUS_VALIDATION_ERROR.
      */
     @Test
//@Ignore
    public void testStaticDailyAndNodeps() {
        String testName = "testStaticDailyAndNodeps";
        System.out.println("-------------------" + testName + "-------------------");
        String[] args = { "--context=index-config_DEV.xml", "--daily", "--nodeps" };
        System.out.println("Command line = " + StringUtils.join(args, ","));
        int retVal =  IndexerManager.mainReturnsStatus(args);
         
        switch (retVal) {
            case IndexerManager.STATUS_VALIDATION_ERROR:
                break;
                
            default:
                fail("Expected STATUS_VALIDATION_ERROR");
                break;
        }
    }
    
     /**
      * Test invoking static main with --all and --cores=ma
      * 
      * Expected results: STATUS_VALIDATION_ERROR.
      */
     @Test
//@Ignore
    public void testStaticDailyAndCores() {
        String testName = "testStaticDailyAndCores";
        System.out.println("-------------------" + testName + "-------------------");
        String[] args = { "--context=index-config_DEV.xml", "--daily", "--cores=ma" };
        System.out.println("Command line = " + StringUtils.join(args, ","));
        int retVal =  IndexerManager.mainReturnsStatus(args);
         
        switch (retVal) {
            case IndexerManager.STATUS_VALIDATION_ERROR:
                break;
                
            default:
                fail("Expected STATUS_VALIDATION_ERROR");
                break;
        }
    }
    
     /**
      * Test invoking static main with --all and --cores=ma
      * 
      * Expected results: STATUS_VALIDATION_ERROR.
      */
     @Test
//@Ignore
    public void testStaticAllAndDaily() {
        String testName = "testStaticAllAndDaily";
        System.out.println("-------------------" + testName + "-------------------");
        String[] args = { "--context=index-config_DEV.xml", "--all", "--daily" };
        System.out.println("Command line = " + StringUtils.join(args, ","));
        int retVal =  IndexerManager.mainReturnsStatus(args);
         
        switch (retVal) {
            case IndexerManager.STATUS_VALIDATION_ERROR:
                break;
                
            default:
                fail("Expected STATUS_VALIDATION_ERROR");
                break;
        }
    }
    
     /**
      * Test invoking static main with --all and --nodeps
      * 
      * Expected results: STATUS_VALIDATION_ERROR.
      */
     @Test
//@Ignore
    public void testStaticAllAndDailyAndNodeps() {
        String testName = "testStaticAllAndDailyAndNodeps";
        System.out.println("-------------------" + testName + "-------------------");
        String[] args = { "--context=index-config_DEV.xml", "--all", "--daily", "--nodeps" };
        System.out.println("Command line = " + StringUtils.join(args, ","));
        int retVal =  IndexerManager.mainReturnsStatus(args);
         
        switch (retVal) {
            case IndexerManager.STATUS_VALIDATION_ERROR:
                break;
                
            default:
                fail("Expected STATUS_VALIDATION_ERROR");
                break;
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
        System.out.println("Command line = " + StringUtils.join(args, ","));
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
        System.out.println("Command line = " + StringUtils.join(args, ","));
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
        System.out.println("Command line = " + StringUtils.join(args, ","));
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
        System.out.println("Command line = " + StringUtils.join(args, ","));
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
        System.out.println("Command line = " + StringUtils.join(args, ","));
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
        System.out.println("Command line = " + StringUtils.join(args, ","));
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
        System.out.println("Command line = " + StringUtils.join(args, ","));
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
        System.out.println("Command line = " + StringUtils.join(args, ","));
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
     
     /**
      * Test invoking IndexerManager instance  using the --all argument.
      * 
      * Expected results: cores observation to autosuggest ready to run.
      */
     @Test
//@Ignore
     public void testInstanceAll() {
        String testName = "testInstanceAll";
        System.out.println("-------------------" + testName + "-------------------");
        String[] args = new String[] { "--context=index-config_DEV.xml", "--all" };
        System.out.println("Command line = " + StringUtils.join(args, ","));
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
      * Test invoking IndexerManager instance  using the --daily argument.
      * 
      * Expected results: cores preqc to autosuggest ready to run.
      */
     @Test
//@Ignore
     public void testInstanceDaily() {
        String testName = "testInstanceDaily";
        System.out.println("-------------------" + testName + "-------------------");
        String[] args = new String[] { "--context=index-config_DEV.xml", "--daily" };
        System.out.println("Command line = " + StringUtils.join(args, ","));
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
      * Test invoking static main with --cores=ma --nodeps --deploy
      * 
      * Expected results: ma core to be created, built, and deployed.
      */
     @Test
//@Ignore
    public void testStaticBuildAndDeploy() {
        String testName = "testStaticBuildAndDeploy";
        System.out.println("-------------------" + testName + "-------------------");
        String[] args = { "--context=index-config_DEV.xml", "--cores=ma", "--nodeps" };
        System.out.println("Command line = " + StringUtils.join(args, ","));
        int retVal =  IndexerManager.mainReturnsStatus(args);
         
        switch (retVal) {
            case IndexerManager.STATUS_OK:
                break;
                
            default:
                fail("Expected STATUS_OK");
                break;
        }
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
        System.out.println("Command line = " + StringUtils.join(args, ","));
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
        System.out.println("Command line = " + StringUtils.join(args, ","));
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
            System.out.println("Command line = " + StringUtils.join(args, ","));
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
        System.out.println("Command line = " + StringUtils.join(args, ","));
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
        System.out.println("Command line = " + StringUtils.join(args, ","));
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
     
//@Ignore
     @Test
     public void testGetSolrCoreDocumentCount() throws Exception {
         String querySegment = "/allele/select?q=*:*&rows=0&wt=json";
         String query = externalDevUrl + querySegment;
        JSONObject alleleResults = JSONRestUtil.getResults(query);
        
        Integer numFound = (Integer)alleleResults.getJSONObject("response").get("numFound");
        if (numFound == null)
            fail("Unable to fetch number of documents.");
        else if (numFound <= 0)
            fail("Expected at least 1 document. Document count = " + numFound);
     }
     
     /**
      * Build daily cores. NOTE: This test is not meant to be run with the
      * test suite, as it takes a long time to complete. It is here to permit
      * building core(s) quickly and easily.
      * 
      * Expected results: The specified cores to be built.
      */
//     @Test
//     public void testStaticBuildDailyCores() {
//        String testName = "testStaticBuildDailyCores";
//        System.out.println("-------------------" + testName + "-------------------");
//        String[] args = new String[] { "--context=index-config_DEV.xml", "--cores=ma,mp,disease", "--nodeps" };
//        System.out.println("Command line = " + StringUtils.join(args, ","));
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
     
//     /**
//      * Build all cores. NOTE: This test is not meant to be run with the
//      * test suite, as it takes a long time to complete. It is here to permit
//      * building core(s) quickly and easily.
//      * 
//      * Expected results: The specified cores to be built.
//      */
//     @Test
//     public void testStaticBuildCores() {
//        String testName = "testStaticBuildCores";
//        System.out.println("-------------------" + testName + "-------------------");
//        String[] args = new String[] { "--context=index-config_DEV.xml", "--cores=experiment" };
//        logger.info("Command line = " + StringUtils.join(args, ","));
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