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

package uk.ac.ebi.phenotype.api;

import java.util.Collection;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.junit.Test;

import static org.junit.Assert.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import uk.ac.ebi.phenotype.stats.GeneService;
import uk.ac.ebi.phenotype.stats.GenotypePhenotypeService;
import uk.ac.ebi.phenotype.stats.MpService;

/**
 * @author ilinca
 */

@ContextConfiguration( locations={ "classpath:test-config.xml" })
public class CoreTests extends AbstractTransactionalJUnit4SpringContextTests {
        
    @Autowired
    private GenotypePhenotypeService gpService;

    @Autowired
    private GeneService gService;
    
    @Autowired
    private MpService mpService;
    
    @Test
    public void testAllGPGenesInGeneCore() throws SolrServerException {
    	System.out.println("Test if all genes in genotype-phenotype core are indexed in the gene core.");
         
        Set<String> gpGenes = gpService.getAllGenes();
        
        Set<String> gGenes = gService.getAllGenes();
        
  //      System.out.println("Before " + gpGenes.size() + "  " + gGenes.size() );
        
        Collection res = CollectionUtils.subtract(gpGenes, gGenes);
        
  //      System.out.println(" After substract: " + res.size());
        
        if (res.size() > 0){
        	System.out.println("The following genes are in in the genotype-phenotype core but not in the gene core: " + res);
        	fail("The following genes are in in the genotype-phenotype core but not in the gene core: " + res);
        }
    }
    
    @Test
    public void testAllGPPhenotypeInMP() throws SolrServerException {
    	System.out.println("Test if all phenotypes in genotype-phenotype core are indexed in the mp core.");
         
        Set<String> gpPhen = gpService.getAllPhenotypes();
        
        Set<String> mpPhen = mpService.getAllPhenotypes();
        
 //       System.out.println("Before " + gpPhen.size() + "  " + mpPhen.size() );
        
        Collection res = CollectionUtils.subtract(gpPhen, mpPhen);
        
 //       System.out.println(" After substract: " + res.size());
        
        if (res.size() > 0){
        	fail("The following phenotypes are in in the genotype-phenotype core but not in the MP core: " + res);
        }
    }
}
