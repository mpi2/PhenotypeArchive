/*
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

package uk.ac.ebi.phenotype.dao;

import java.util.Set;
import org.apache.solr.client.solrj.SolrServerException;
import org.junit.Test;
import static org.junit.Assert.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import uk.ac.ebi.phenotype.stats.GenotypePhenotypeService;

/**
 *
 * @author mrelac
 */
@ContextConfiguration( locations={ "classpath:app-config.xml" })
public class GenotypePhenotypeServiceTest extends AbstractTransactionalJUnit4SpringContextTests {
    
    public GenotypePhenotypeServiceTest() {
    }
    
    @Autowired
    private GenotypePhenotypeService pgService;

    @Test
    public void testGetAllPhenotypes() throws SolrServerException {
        System.out.println("run testGetAllPhenotypes");
        
        Set<String> phenotypes = pgService.getAllPhenotypes();
        
        if (phenotypes == null) {
            fail("GenotypePhenotypeService.getAllPhenotypes() returned null!");
        } else {
            System.out.println("testGetAllPhenotypes: " + phenotypes.size() + " phenotypes found.");
            assertTrue("Expected at least 100 genotypes.", phenotypes.size() >= 100);
        }
    }
}
