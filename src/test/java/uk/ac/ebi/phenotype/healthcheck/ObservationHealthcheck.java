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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    private final String DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";

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
//@Ignore
    public void testMissingIsZero() throws SQLException {
        String testName = "testMissingIsZero";
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        Date start = new Date();
        List<String[]> data = observationDAO.getNotMissingNotEmpty();
        System.out.println(dateFormat.format(start) + ": " + testName + " started.");
        if ( ! data.isEmpty()) {
            System.out.println("WARNING: there were " + data.size() + " parameter values for not-missing data");
            System.out.printf("%10s %10s %15s %20s %-50s %-100s\n", "missing", "count", "organisation_id", "observation_type", "parameter_status", "parameter_status_message");
            for (String[] s : data) {
                System.out.printf("%10s %10s %15s %20s %-50s %-100s\n", s[0], s[1], s[2], s[3], s[4], s[5]);
            }
            fail("There were parameter values for not-missing data");
        } else {
            System.out.println("SUCCESS: " + testName);
        }
    }
    
    /**
     * When <code>missing</code> is one, <code>parameter_status</code> should 
     * contain a controlled vocabulary message, taken from the <code>ontology_term</code>
     * table, describing the reason the data is missing. This field must not be
     * null/empty. Issue a warning if it is. The <code>parameter_status_message</code>
     * may or may not be empty.
     * @throws SQLException
     */
    @Test
//@Ignore
    public void testMissingIsOne() throws SQLException {
        String testName = "testMissingIsOne";
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        Date start = new Date();
        List<String[]> data = observationDAO.getMissingEmpty();
        System.out.println(dateFormat.format(start) + ": " + testName + " started.");
        if ( ! data.isEmpty()) {
            System.out.println("ERROR: there were null/empty parameter values for missing data:");
            System.out.printf("%10s %10s %15s %20s %-50s %-100s\n", "missing", "count", "organisation_id", "observation_type", "parameter_status", "parameter_status_message");
            for (String[] s : data) {
                System.out.printf("%10s %10s %15s %20s %-50s %-100s\n", s[0], s[1], s[2], s[3], s[4], s[5]);
            }
            fail("There were null/empty parameter values for missing data");
        } else {
            System.out.println("SUCCESS: " + testName);
        }
    }
    
    /**
     * This test fetches the list of observation.parameter_status that is not in
     * IMPC ontology_term.acc and prints out the information necessary to resolve
     * the missing terms.
     * @throws SQLException
     */
    @Test
// @Ignore
    public void testMissingParameterStatusFromOntologyTerm() throws SQLException {
        String testName = "testMissingParameterStatusFromOntologyTerm";
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        Date start = new Date();
        List<String[]> data = observationDAO.getMissingOntologyTerms();
        System.out.println(dateFormat.format(start) + ": " + testName + " started.");
        if ( ! data.isEmpty()) {
            System.out.println("ERROR: there are ontology.parameter_status terms that do not exist in ontology_term.acc:");
            System.out.printf("%50s %10s %15s %20s\n", "parameter_status", "acc", "organisation_id", "observation_type");
            for (String[] s : data) {
                System.out.printf("%50s %10s %15s %20s\n", s[0], s[1], s[2], s[3]);
            }
            fail("There are ontology.parameter_status terms that do not exist in ontology_term.acc");
        } else {
            System.out.println("SUCCESS: " + testName);
        }
    }

}