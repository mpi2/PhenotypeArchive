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
 * 
 * This test class is intended to run healthchecks against the observation table.
 */



package uk.ac.ebi.phenotype.healthcheck;

import java.sql.SQLException;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.phenotype.dao.ObservationDAO;

/**
 * Mouseinformatics fetches an xml file nightly that contains all of the
 * phenotyping data from YYY. The first step to QC-ing the data is to transform
 * that file into a set of import statistics that are loaded into the
 * 'observations' table. The important fields are:
 * <ul><li><code>missing</code> - if 1, the record identified by xxx is missing;
 * if 0, the data is not missing</li>
 * <li><code>parameter_status</code> - a controlled vocabulary term describing
 * the reason the data is missing, whose value comes from the <code>ontology_term<code> table</li>
 * <li><code>parameter_status_message</code> - a free-text field further describing
 * the reason the data is missing</li></ul>
 * If <code>missing</code> equals 0, no data is missing. The <code>parameter_status</code> and <code>parameter_status_message</code>
 *      fields should both be null or empty. A warning should be issued if they are not.
 * If <code>missing<code> equals 1, data is missing. The <code>parameter_status</code> should not be null.
 *      It should contain a term matching one of the values from the <code>ontology_term</code> table.
 *      This is the only data this field should contain. A warning should be issued if there is no such term.
 *      The <code>parameter_status_message</code> field may be null, empty, or not empty.
 *
 * @author mrelac
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-config.xml" })
@TransactionConfiguration
@Transactional
public class ObservationHealthcheck {

    public ObservationHealthcheck() {
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

    @Autowired
    ObservationDAO observationDAO;

    
    /**
     * When <code>missing</code> is zero, <code>parameter_status</code> and 
     * <code>parameter_status_message</code> should both be null/empty. Issue
     * a warning if they are not, and display some useful debugging information.
     * @throws SQLException
     */
    @Test
    public void testMissingIsZero() throws SQLException {
        List<String[]> data = observationDAO.getNotMissingNotEmpty();
        if ( ! data.isEmpty()) {
            System.out.println("WARNING:");
            System.out.printf("%10s %10s %15s %20s %-50s %-100s\n", "missing", "count", "organisation_id", "observation_type", "parameter_status", "parameter_status_message");
            for (String[] s : data) {
                System.out.printf("%10s %10s %15s %20s %-50s %-100s\n", s[0], s[1], s[2], s[3], s[4], s[5]);
            }
            fail("There were parameter values for not-missing data");
        }
    }
    
    /**
     * When <code>missing</code> is one, <code>parameter_status</code> should 
     * contain a controlled vocabulary message, taken from the <code>ontology_term</code>
     * table, describing the reason the data is missing. This field must not be
     * null/empty. Issue a warning if it is. The <code>parameter_status_message</code>
     * may or may not be empty.
     */
    @Test
@Ignore
    public void testMissingIsOne() {
        
    }

}