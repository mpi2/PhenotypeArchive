/*******************************************************************************
 * Copyright 2015 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 *******************************************************************************/

package uk.ac.ebi.phenotype.solr.indexer;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import uk.ac.ebi.phenotype.service.MaOntologyService;
import uk.ac.ebi.phenotype.service.dto.MaDTO;
import uk.ac.ebi.phenotype.service.dto.SangerImageDTO;
import uk.ac.ebi.phenotype.solr.indexer.beans.OntologyTermBean;
import uk.ac.ebi.phenotype.solr.indexer.beans.OntologyTermMaBeanList;
import uk.ac.ebi.phenotype.solr.indexer.exceptions.IndexerException;
import uk.ac.ebi.phenotype.solr.indexer.exceptions.ValidationException;
import uk.ac.ebi.phenotype.solr.indexer.utils.IndexerMap;
import uk.ac.ebi.phenotype.solr.indexer.utils.SolrUtils;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static uk.ac.ebi.phenotype.service.OntologyService.BATCH_SIZE;

/**
 * Populate the MA core
 */
public class MAIndexer extends AbstractIndexer {

    private static final Logger logger = LoggerFactory.getLogger(MAIndexer.class);

    @Autowired
    @Qualifier("ontodbDataSource")
    DataSource ontodbDataSource;
    
    @Autowired
    @Qualifier("sangerImagesIndexing")
    SolrServer imagesCore;

    @Autowired
    @Qualifier("maIndexing")
    SolrServer maCore;
    
    @Autowired
    MaOntologyService maOntologyService;
    
    private Map<String, List<SangerImageDTO>> maImagesMap = new HashMap();      // key = term_id.
    
    public MAIndexer() {
        
    }

    @Override
    public void validateBuild() throws IndexerException {
        Long numFound = getDocumentCount(maCore);
        
        if (numFound <= MINIMUM_DOCUMENT_COUNT)
            throw new IndexerException(new ValidationException("Actual ma document count is " + numFound + "."));
        
        if (numFound != documentCount)
            logger.warn("WARNING: Added " + documentCount + " ma documents but SOLR reports " + numFound + " documents.");
        else
            logger.info("validateBuild(): Indexed " + documentCount + " ma documents.");
    }
    
    @Override
    public void initialise(String[] args) throws IndexerException {
        super.initialise(args);
    }
    
    @Override
    public void run() throws IndexerException {
        try {
            logger.info("Starting MA Indexer...");
            initialiseSupportingBeans();

            List<MaDTO> maBatch = new ArrayList(BATCH_SIZE);
            int count = 0;

            logger.info("Starting indexing loop");

            // Add all ma terms to the index.
            List<OntologyTermBean> beans = maOntologyService.getAllTerms();
            for (OntologyTermBean bean : beans) {
                MaDTO ma = new MaDTO();
                
                // Set scalars.
                ma.setDataType("ma");
                ma.setMaId(bean.getId());
                ma.setMaTerm(bean.getName());
                
                // Set collections.
                OntologyTermMaBeanList sourceList = new OntologyTermMaBeanList(maOntologyService, bean.getId());
                ma.setOntologySubset(sourceList.getSubsets());
                ma.setMaTermSynonym(sourceList.getSynonyms());
                
                ma.setChildMaId(sourceList.getChildren().getIds());
                ma.setChildMaIdTerm(sourceList.getChildren().getId_name_concatenations());
                ma.setChildMaTerm(sourceList.getChildren().getNames());
                ma.setChildMaTermSynonym(sourceList.getChildren().getSynonyms());
                
                ma.setSelectedTopLevelMaId(sourceList.getTopLevels().getIds());
                ma.setSelectedTopLevelMaTerm(sourceList.getTopLevels().getNames());
                ma.setSelectedTopLevelMaTermSynonym(sourceList.getTopLevels().getSynonyms());
                
                // Image association fields
                List<SangerImageDTO> sangerImages = maImagesMap.get(bean.getId());
                if (sangerImages != null) {
                    for (SangerImageDTO sangerImage : sangerImages) {
                        ma.setProcedureName(sangerImage.getProcedureName());
                        ma.setExpName(sangerImage.getExpName());
                        ma.setExpNameExp(sangerImage.getExpNameExp());
                        ma.setSymbolGene(sangerImage.getSymbolGene());
                        
                        ma.setMgiAccessionId(sangerImage.getMgiAccessionId());
                        ma.setMarkerSymbol(sangerImage.getMarkerSymbol());
                        ma.setMarkerName(sangerImage.getMarkerName());
                        ma.setMarkerSynonym(sangerImage.getMarkerSynonym());
                        ma.setMarkerType(sangerImage.getMarkerType());
                        ma.setHumanGeneSymbol(sangerImage.getHumanGeneSymbol());
                        
                        ma.setStatus(sangerImage.getStatus());
                        
                        ma.setImitsPhenotypeStarted(sangerImage.getImitsPhenotypeStarted());
                        ma.setImitsPhenotypeComplete(sangerImage.getImitsPhenotypeComplete());
                        ma.setImitsPhenotypeStatus(sangerImage.getImitsPhenotypeStatus());
                        
                        ma.setLatestPhenotypeStatus(sangerImage.getLatestPhenotypeStatus());
                        ma.setLatestPhenotypingCentre(sangerImage.getLatestPhenotypingCentre());
                        
                        ma.setLatestProductionCentre(sangerImage.getLatestProductionCentre());
                        ma.setLatestPhenotypingCentre(sangerImage.getLatestPhenotypingCentre());
                        
                        ma.setAlleleName(sangerImage.getAlleleName());
                    }
                }
                
                count ++;
                maBatch.add(ma);
                if (maBatch.size() == BATCH_SIZE) {
                    // Update the batch, clear the list
                    documentCount += maBatch.size();
                    maCore.addBeans(maBatch, 60000);
                    maBatch.clear();
                }
            }

            // Make sure the last batch is indexed
            if (maBatch.size() > 0) {
                documentCount += maBatch.size();
                maCore.addBeans(maBatch, 60000);
                count += maBatch.size();
            }
            
            // Send a final commit
            maCore.commit();
            logger.info("Indexed {} beans in total", count);
        } catch (SolrServerException| IOException e) {
            throw new IndexerException(e);
        }
        

        logger.info("MA Indexer complete!");
    }
    
    
    // PROTECTED METHODS
    
    
    @Override
    protected Logger getLogger() {
        return logger;
    }
    
    @Override
    protected void printConfiguration() {
        if (logger.isDebugEnabled()) {
            logger.debug("WRITING ma     CORE TO: " + SolrUtils.getBaseURL(maCore));
            logger.debug("USING   images CORE AT: " + SolrUtils.getBaseURL(imagesCore));
        }
    }
    
    
    // PRIVATE METHODS
    
    
    private final Integer MAX_ITERATIONS = 2;                                   // Set to non-null value > 0 to limit max_iterations.
    
    private void initialiseSupportingBeans() throws IndexerException {
        // Grab all the supporting database content
        maImagesMap = IndexerMap.getSangerImagesByMA(imagesCore);
        if (logger.isDebugEnabled()) {
            IndexerMap.dumpSangerImagesMap(maImagesMap, "Images map:", MAX_ITERATIONS);
        }
    }

    public static void main(String[] args) throws IndexerException {
        MAIndexer indexer = new MAIndexer();
        indexer.initialise(args);
        indexer.run();
        indexer.validateBuild();

        logger.info("Process finished.  Exiting.");
    }
}
