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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import uk.ac.ebi.phenotype.dao.GwasDAO;
import uk.ac.ebi.phenotype.service.MaOntologyService;
import uk.ac.ebi.phenotype.service.dto.GeneDTO;
import uk.ac.ebi.phenotype.service.dto.GenotypePhenotypeDTO;
import uk.ac.ebi.phenotype.service.dto.ImageDTO;
import uk.ac.ebi.phenotype.service.dto.MaDTO;
import uk.ac.ebi.phenotype.service.dto.MpDTO;
import uk.ac.ebi.phenotype.service.dto.SangerImageDTO;
import uk.ac.ebi.phenotype.solr.indexer.beans.OntologyTermBean;
import uk.ac.ebi.phenotype.solr.indexer.beans.OntologyTermMaBeanList;
import uk.ac.ebi.phenotype.solr.indexer.exceptions.IndexerException;
import uk.ac.ebi.phenotype.solr.indexer.exceptions.ValidationException;
import uk.ac.ebi.phenotype.solr.indexer.utils.IndexerMap;
import uk.ac.ebi.phenotype.solr.indexer.utils.SolrUtils;

import javax.sql.DataSource;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	@Qualifier("impcImagesIndexing")
    SolrServer impcImagesCore;
    
    @Autowired
    @Qualifier("geneIndexing")
    SolrServer geneCore;
    
    @Autowired
    @Qualifier("mpIndexing")
    SolrServer mpCore;
    
    @Autowired
    @Qualifier("genotypePhenotypeIndexing")
    SolrServer gpCore;
    
    
    @Autowired
    @Qualifier("maIndexing")
    SolrServer maCore;
    
    @Autowired
    MaOntologyService maOntologyService;
    
    private Map<String, List<SangerImageDTO>> maImagesMap = new HashMap();      // key = ma_term_id.
    private Map<String, List<ImageDTO>> maImpcImagesMap = new HashMap();        // key = ma_term_id
    private Map<String, List<MpDTO>> maInferredFromMpMap = new HashMap();        // key = ma_term_id
    private Map<String, List<GeneDTO>> geneMap = new HashMap(); // key = gene_accession_id
    private Map<String, List<GenotypePhenotypeDTO>> mp2GeneFromGP = new HashMap(); // key = ma_term_id
    
    
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
    public void run() throws IndexerException, SQLException {
        try {
            logger.info("Starting MA Indexer...");
            initialiseSupportingBeans();
            
            populateImpcImages();
            populateMp2GeneFromGene();
            populateMp2GeneFromGP();
            populateMpTermsViaInferredMaTerms();
            
            
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
                // Sanger images
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
                        
                        ma.setAlleleName(sangerImage.getAlleleName());
                    }
                }
                
                if ( maImpcImagesMap.containsKey(bean.getId())) {
	                List<ImageDTO> impcImages = maImpcImagesMap.get(bean.getId());
	            	for( ImageDTO impcImage : impcImages ){
	            		
	            		if ( impcImage.getGeneAccession() != null && ! impcImage.getGeneAccession().equals(ma.getMgiAccessionId()) ){
	            			
	            			if (ma.getMgiAccessionId() == null){
	            				
	            				ma.setMgiAccessionId(Arrays.asList(impcImage.getGeneAccession()));
	            				ma.setMarkerSymbol(impcImage.getMarkerSymbol());
	            				ma.setMarkerSynonym(impcImage.getMarkerSynonym());
	            				ma.setMarkerName(impcImage.getMarkerName());
	            				ma.setMarkerType(impcImage.getMarkerType());
	            				ma.setHumanGeneSymbol(impcImage.getHumanGeneSymbol());
	            				ma.setAlleleName(impcImage.getAlleleName());
	            			}
	            			else {
			            		List<String> mgiAccessionIds = new ArrayList<>(ma.getMgiAccessionId());
			            		List<String> accs = new ArrayList<>();
			            		accs.add(impcImage.getGeneAccession());
			            		mgiAccessionIds.addAll(accs);
			            		ma.setMgiAccessionId(mgiAccessionIds);
			            		
			            		List<String> markerSymbols = new ArrayList<>(ma.getMarkerSymbol());
			            		markerSymbols.addAll(impcImage.getMarkerSymbol());
			                    ma.setMarkerSymbol(markerSymbols);
			                    
			                    Set<String> markerSynonyms = new HashSet<>();
			                    if (ma.getMarkerSynonym() != null){markerSynonyms.addAll(ma.getMarkerSynonym());}
			                    if (impcImage.getMarkerSynonym() != null){markerSynonyms.addAll(impcImage.getMarkerSynonym());}
			                    if (markerSynonyms.size()>0) {
			                    	ma.setMarkerSynonym(new ArrayList(markerSynonyms));
			                    }
			                    
			                    List<String> markerNames = new ArrayList<>(ma.getMarkerName());
			                    markerNames.addAll(impcImage.getMarkerName());
			                    ma.setMarkerName(markerNames);
			                    
			                    List<String> markerTypes = new ArrayList<>(ma.getMarkerType());
			                    markerTypes.addAll(impcImage.getMarkerType());
			                    ma.setMarkerType(markerTypes);
			                    
			                    Set<String> humanGeneSymbols = new HashSet<>();
			                    if (ma.getHumanGeneSymbol()!=null){humanGeneSymbols.addAll(ma.getHumanGeneSymbol()); }
			                    if (impcImage.getHumanGeneSymbol()!=null){humanGeneSymbols.addAll(impcImage.getHumanGeneSymbol()); }
			                    if (humanGeneSymbols.size()>0) {
				                    ma.setHumanGeneSymbol(new ArrayList(humanGeneSymbols));
			                    }

			                    List<String> alleleNames = new ArrayList<>(ma.getAlleleName());
			                    alleleNames.addAll(impcImage.getAlleleName());
			                    ma.setAlleleName(alleleNames);
	            			}
	            		}
	            		
	            		Set<String> status = new HashSet<>();
	                    if (ma.getStatus()!=null){status.addAll(ma.getStatus()); }
	                    if (impcImage.getStatus()!=null){status.addAll(impcImage.getStatus()); }
	                    if (status.size()>0) {
		                    ma.setStatus(new ArrayList(status));
	                    }
	            			            		
	                    Set<String> imitsPhenotypeStarted = new HashSet<>();
	                    if (ma.getImitsPhenotypeStarted()!=null){imitsPhenotypeStarted.addAll(ma.getImitsPhenotypeStarted()); }
	                    if (impcImage.getImitsPhenotypeStarted()!=null){imitsPhenotypeStarted.addAll(impcImage.getImitsPhenotypeStarted()); }
	                    if (imitsPhenotypeStarted.size()>0) {
		                    ma.setImitsPhenotypeStarted(new ArrayList(imitsPhenotypeStarted));
	                    }
	            		
	                    Set<String> imitsPhenotypeComplete = new HashSet<>();
	                    if (ma.getImitsPhenotypeComplete()!=null){imitsPhenotypeComplete.addAll(ma.getImitsPhenotypeComplete()); }
	                    if (impcImage.getImitsPhenotypeComplete()!=null){imitsPhenotypeComplete.addAll(impcImage.getImitsPhenotypeComplete()); }
	                    if (imitsPhenotypeComplete.size()>0) {
		                    ma.setImitsPhenotypeComplete(new ArrayList(imitsPhenotypeComplete));
	                    }
	                    
	                    Set<String> imitsPhenotypeStatus = new HashSet<>();
	                    if (ma.getImitsPhenotypeStatus()!=null){imitsPhenotypeStatus.addAll(ma.getImitsPhenotypeStatus()); }
	                    if (impcImage.getImitsPhenotypeStatus()!=null){imitsPhenotypeStatus.addAll(impcImage.getImitsPhenotypeStatus()); }
	                    if (imitsPhenotypeStatus.size()>0) {
		                    ma.setImitsPhenotypeStatus(new ArrayList(imitsPhenotypeStatus));
	                    }
	                    
	                    Set<String> imitsLatestPhenotypeStatus = new HashSet<>();
	                    if (ma.getLatestPhenotypeStatus()!=null){imitsLatestPhenotypeStatus.addAll(ma.getLatestPhenotypeStatus()); }
	                    if (impcImage.getLatestPhenotypeStatus()!=null){imitsLatestPhenotypeStatus.addAll(impcImage.getLatestPhenotypeStatus()); }
	                    if (imitsLatestPhenotypeStatus.size()>0) {
		                    ma.setLatestPhenotypeStatus(new ArrayList(imitsLatestPhenotypeStatus));
	                    }
	            		
	                    Set<String> imitsLatestPhenotypingCentre = new HashSet<>();
	                    if (ma.getLatestPhenotypingCentre()!=null){imitsLatestPhenotypingCentre.addAll(ma.getLatestPhenotypingCentre()); }
	                    if (impcImage.getLatestPhenotypingCentre()!=null){imitsLatestPhenotypingCentre.addAll(impcImage.getLatestPhenotypingCentre()); }
	                    if (imitsLatestPhenotypingCentre.size()>0) {
		                    ma.setLatestPhenotypingCentre(new ArrayList(imitsLatestPhenotypingCentre));
	                    }
	            		
	                    Set<String> imitsLatestProductionCentre = new HashSet<>();
	                    if (ma.getLatestProductionCentre()!=null){imitsLatestProductionCentre.addAll(ma.getLatestProductionCentre()); }
	                    if (impcImage.getLatestProductionCentre()!=null){imitsLatestProductionCentre.addAll(impcImage.getLatestProductionCentre()); }
	                    if (imitsLatestProductionCentre.size()>0) {
		                    ma.setLatestProductionCentre(new ArrayList(imitsLatestProductionCentre));
	                    }
	            	}
                }
                
            	// for MP terms with inferred MA term (in MP core), we index those MP here
                if ( maInferredFromMpMap.containsKey(bean.getId())) {
	            	List<MpDTO> maInferredFromMps = maInferredFromMpMap.get(bean.getId());
	            	
	            	//System.out.println("Got " + maInferredFromMps.size() + " mps");
	            	for( MpDTO maInferredFromMp : maInferredFromMps ){
	            		
	            		String mpid = maInferredFromMp.getMpId();
	            		ma.setMpId(Arrays.asList(mpid));
	            		
	            		//System.out.println("mp_term: " + maInferredFromMp.getMpTerm());
	            		ma.setMpTerm(Arrays.asList(maInferredFromMp.getMpTerm()));
	            		
	            		
	            		ma.setMpTermSynonym(maInferredFromMp.getMpTermSynonym());
	            		
	            		//System.out.println("mp_top_level_id: " + maInferredFromMp.getTopLevelMpId());
	            		ma.setTopLevelMpId(maInferredFromMp.getTopLevelMpId());
	            		ma.setTopLevelMpTerm(maInferredFromMp.getTopLevelMpTerm());
	            		ma.setTopLevelMpTermSynonym(maInferredFromMp.getTopLevelMpTermSynonym());
	            		
	            		ma.setIntermediateMpId(maInferredFromMp.getIntermediateMpId());
	            		ma.setIntermediateMpTerm(maInferredFromMp.getIntermediateMpTerm());
	            		ma.setIntermediateMpTermSynonym(maInferredFromMp.getIntermediateMpTermSynonym());
	            		
	            		ma.setChildMpId(maInferredFromMp.getChildMpId());
	            		ma.setChildMpTerm(maInferredFromMp.getChildMpTerm());
	            		ma.setChildMpTermSynonym(maInferredFromMp.getChildMpTermSynonym());
	            		
	            		// lookup MP in genotype-phenotype core to get associated gene
	            		if ( mp2GeneFromGP.containsKey(mpid) ){
	            			List<GenotypePhenotypeDTO> gps = mp2GeneFromGP.get(mpid);
	            			for ( GenotypePhenotypeDTO gp : gps ) {
	            				String geneAccId = gp.getMarkerAccessionId();
	            				
	            				if ( geneMap.containsKey(geneAccId) ){
	            					List<GeneDTO> genes = geneMap.get(geneAccId);
	            					for( GeneDTO gene : genes ){
	            						if (ma.getMgiAccessionId() == null){
	        	            				
	        	            				ma.setMgiAccessionId(Arrays.asList(geneAccId));
	        	            				ma.setMarkerSymbol(Arrays.asList(gene.getMarkerSymbol()));
	        	            				ma.setMarkerSynonym(gene.getMarkerSynonym());
	        	            				ma.setMarkerName(Arrays.asList(gene.getMarkerName()));
	        	            				ma.setMarkerType(Arrays.asList(gene.getMarkerType()));
	        	            				ma.setHumanGeneSymbol(gene.getHumanGeneSymbol());
	        	            				ma.setAlleleName(gene.getAlleleName());
	        	            			}
	            						else {
	            							List<String> mgiAccessionIds = new ArrayList<>(ma.getMgiAccessionId());
	        			            		List<String> accs = new ArrayList<>();
	        			            		accs.add(geneAccId);
	        			            		mgiAccessionIds.addAll(accs);
	        			            		ma.setMgiAccessionId(mgiAccessionIds);
	        			            		
	        			            		List<String> markerSymbols = new ArrayList<>(ma.getMarkerSymbol());
	        			            		markerSymbols.add(gene.getMarkerSymbol());
	        			                    ma.setMarkerSymbol(markerSymbols);
	        			                    
	        			                    Set<String> markerSynonyms = new HashSet<>();
	        			                    if (ma.getMarkerSynonym() != null){markerSynonyms.addAll(ma.getMarkerSynonym());}
	        			                    if (gene.getMarkerSynonym() != null){markerSynonyms.addAll(gene.getMarkerSynonym());}
	        			                    if (markerSynonyms.size()>0) {
	        			                    	ma.setMarkerSynonym(new ArrayList(markerSynonyms));
	        			                    }
	        			                    
	        			                    List<String> markerNames = new ArrayList<>(ma.getMarkerName());
	        			                    markerNames.add(gene.getMarkerName());
	        			                    ma.setMarkerName(markerNames);
	        			                    
	        			                    List<String> markerTypes = new ArrayList<>(ma.getMarkerType());
	        			                    markerTypes.add(gene.getMarkerType());
	        			                    ma.setMarkerType(markerTypes);
	        			                    
	        			                    Set<String> humanGeneSymbols = new HashSet<>();
	        			                    if (ma.getHumanGeneSymbol()!=null){humanGeneSymbols.addAll(ma.getHumanGeneSymbol()); }
	        			                    if (gene.getHumanGeneSymbol()!=null){humanGeneSymbols.addAll(gene.getHumanGeneSymbol()); }
	        			                    if (humanGeneSymbols.size()>0) {
	        				                    ma.setHumanGeneSymbol(new ArrayList(humanGeneSymbols));
	        			                    }
	
	        			                    Set<String> alleleNames = new HashSet<>();
	        			                    if (ma.getAlleleName()!=null){alleleNames.addAll(ma.getAlleleName()); }
	        			                    if (gene.getAlleleName()!=null){alleleNames.addAll(gene.getAlleleName()); }
	        			                    if (alleleNames.size()>0) {
	        				                    ma.setAlleleName(new ArrayList(alleleNames));
	        			                    }
	        	            			}
	            						
	            						Set<String> status = new HashSet<>();
	            	                    if (ma.getStatus()!=null){status.addAll(ma.getStatus()); }
	            	                    if (gene.getStatus()!=null){status.add(gene.getStatus()); }
	            	                    if (status.size()>0) {
	            		                    ma.setStatus(new ArrayList(status));
	            	                    }
	            	            			            		
	            	                    Set<String> imitsPhenotypeStarted = new HashSet<>();
	            	                    if (ma.getImitsPhenotypeStarted()!=null){imitsPhenotypeStarted.addAll(ma.getImitsPhenotypeStarted()); }
	            	                    if (gene.getImitsPhenotypeStarted()!=null){imitsPhenotypeStarted.add(gene.getImitsPhenotypeStarted()); }
	            	                    if (imitsPhenotypeStarted.size()>0) {
	            		                    ma.setImitsPhenotypeStarted(new ArrayList(imitsPhenotypeStarted));
	            	                    }
	            	            		
	            	                    Set<String> imitsPhenotypeComplete = new HashSet<>();
	            	                    if (ma.getImitsPhenotypeComplete()!=null){imitsPhenotypeComplete.addAll(ma.getImitsPhenotypeComplete()); }
	            	                    if (gene.getImitsPhenotypeComplete()!=null){imitsPhenotypeComplete.add(gene.getImitsPhenotypeComplete()); }
	            	                    if (imitsPhenotypeComplete.size()>0) {
	            		                    ma.setImitsPhenotypeComplete(new ArrayList(imitsPhenotypeComplete));
	            	                    }
	            	                    
	            	                    Set<String> imitsPhenotypeStatus = new HashSet<>();
	            	                    if (ma.getImitsPhenotypeStatus()!=null){imitsPhenotypeStatus.addAll(ma.getImitsPhenotypeStatus()); }
	            	                    if (gene.getImitsPhenotypeStatus()!=null){imitsPhenotypeStatus.add(gene.getImitsPhenotypeStatus()); }
	            	                    if (imitsPhenotypeStatus.size()>0) {
	            		                    ma.setImitsPhenotypeStatus(new ArrayList(imitsPhenotypeStatus));
	            	                    }
	            	                    
	            	                    Set<String> imitsLatestPhenotypeStatus = new HashSet<>();
	            	                    if (ma.getLatestPhenotypeStatus()!=null){imitsLatestPhenotypeStatus.addAll(ma.getLatestPhenotypeStatus()); }
	            	                    if (gene.getLatestPhenotypeStatus()!=null){imitsLatestPhenotypeStatus.add(gene.getLatestPhenotypeStatus()); }
	            	                    if (imitsLatestPhenotypeStatus.size()>0) {
	            		                    ma.setLatestPhenotypeStatus(new ArrayList(imitsLatestPhenotypeStatus));
	            	                    }
	            	            		
	            	                    Set<String> imitsLatestPhenotypingCentre = new HashSet<>();
	            	                    if (ma.getLatestPhenotypingCentre()!=null){imitsLatestPhenotypingCentre.addAll(ma.getLatestPhenotypingCentre()); }
	            	                    if (gene.getLatestPhenotypingCentre()!=null){imitsLatestPhenotypingCentre.addAll(gene.getLatestPhenotypingCentre()); }
	            	                    if (imitsLatestPhenotypingCentre.size()>0) {
	            		                    ma.setLatestPhenotypingCentre(new ArrayList(imitsLatestPhenotypingCentre));
	            	                    }
	            	            		
	            	                    Set<String> imitsLatestProductionCentre = new HashSet<>();
	            	                    if (ma.getLatestProductionCentre()!=null){imitsLatestProductionCentre.addAll(ma.getLatestProductionCentre()); }
	            	                    if (gene.getLatestProductionCentre()!=null){imitsLatestProductionCentre.addAll(gene.getLatestProductionCentre()); }
	            	                    if (imitsLatestProductionCentre.size()>0) {
	            		                    ma.setLatestProductionCentre(new ArrayList(imitsLatestProductionCentre));
	            	                    }
	            						
		            				}
	            				}
	            			}
	            		}
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
            logger.debug("WRITING ma         CORE TO: " + SolrUtils.getBaseURL(maCore));
            logger.debug("USING   images     CORE AT: " + SolrUtils.getBaseURL(imagesCore));
            logger.debug("USING   impcImages CORE AT: " + SolrUtils.getBaseURL(impcImagesCore));
            logger.debug("USING   mp         CORE AT: " + SolrUtils.getBaseURL(mpCore));
        }
    }
    
    
    // PRIVATE METHODS
    private final Integer MAX_ITERATIONS = 2;                                   // Set to non-null value > 0 to limit max_iterations.
    
    private void initialiseSupportingBeans() throws IndexerException, SolrServerException, IOException, SQLException {
        // Grab all the supporting database content
        maImagesMap = IndexerMap.getSangerImagesByMA(imagesCore);
        
        if (logger.isDebugEnabled()) {
            IndexerMap.dumpSangerImagesMap(maImagesMap, "Images map:", MAX_ITERATIONS);
        }
    }
    private void populateMpTermsViaInferredMaTerms() throws SolrServerException, IOException {
    	
    	List<String> mpFields = Arrays.asList(
                    MpDTO.INFERRED_MA_TERM_ID, MpDTO.MP_ID, MpDTO.MP_TERM, MpDTO.MP_TERM_SYNONYM, MpDTO.TOP_LEVEL_MP_ID, 
                    MpDTO.TOP_LEVEL_MP_TERM, MpDTO.TOP_LEVEL_MP_TERM_SYNONYM, MpDTO.INTERMEDIATE_MP_ID, MpDTO.INTERMEDIATE_MP_TERM, 
                    MpDTO.INTERMEDIATE_MP_TERM_SYNONYM, MpDTO.CHILD_MP_ID, MpDTO.CHILD_MP_TERM, MpDTO.CHILD_MP_TERM_SYNONYM
            );
            
        SolrQuery query = new SolrQuery()
            .setQuery("*:*")
            .setFields(StringUtils.join(mpFields, ","))
            .setRows(Integer.MAX_VALUE);

        List<MpDTO> mps = mpCore.query(query).getBeans(MpDTO.class);
        
        for (MpDTO mp : mps) {
        	if ( mp.getInferredMaTermId() != null ){
        		List<String> maIds = mp.getInferredMaTermId();
        		//System.out.println("ma: "+ maIds);
        		for( String maId : maIds ){
        			if ( ! maInferredFromMpMap.containsKey(maId) ) {
        				maInferredFromMpMap.put(maId, new ArrayList<MpDTO>());
        			}
        			maInferredFromMpMap.get(maId).add(mp);
        			//System.out.println("mpid: " + mp.getMpId());
        		}
        	}
        	
        }
    	
        logger.info("Finished populating maInferredFromMpMap");
        
    }
    
    private void populateImpcImages() throws SolrServerException, IOException, SQLException {

    	List<String> impcImagesFields = Arrays.asList(
        	ImageDTO.MA_ID,	
    		ImageDTO.GENE_ACCESSION_ID, 
    		ImageDTO.MARKER_SYMBOL, 
    		ImageDTO.MARKER_NAME, 
    		ImageDTO.MARKER_SYNONYM, 
    		ImageDTO.MARKER_TYPE,
    		ImageDTO.HUMAN_GENE_SYMBOL,
    		ImageDTO.STATUS,
    		ImageDTO.IMITS_PHENOTYPE_STARTED,
    		ImageDTO.IMITS_PHENOTYPE_COMPLETE,
    		ImageDTO.IMITS_PHENOTYPE_STATUS,
    		ImageDTO.LATEST_PHENOTYPE_STATUS,
    		ImageDTO.LATEST_PHENOTYPING_CENTRE,
    		ImageDTO.LATEST_PRODUCTION_CENTRE,
    		ImageDTO.LATEST_PHENOTYPING_CENTRE,
    		ImageDTO.ALLELE_NAME
        );

        SolrQuery impcImgesQuery = new SolrQuery()
            .setQuery("*:*")
            .setFields(StringUtils.join(impcImagesFields, ","))
            .setRows(Integer.MAX_VALUE);
        
        List<ImageDTO> impcImages = impcImagesCore.query(impcImgesQuery).getBeans(ImageDTO.class);
        
        for (ImageDTO impcImage : impcImages) {
        	List<String> maIds = impcImage.getMaTermId();
        	if ( maIds == null ){
        		continue;
        	}
        	for ( String maId : maIds ){
	        	if ( !maImpcImagesMap.containsKey(maId) ){
	        		maImpcImagesMap.put(maId, new ArrayList<ImageDTO>());
	        	}
	        	maImpcImagesMap.get(maId).add(impcImage);
        	}
        	
        }
        logger.info("Finished populating impcImages");
    	
    }
    
    private void populateMp2GeneFromGene() throws SolrServerException{
    	List<String> geneFields = Arrays.asList(
    			GeneDTO.MP_ID,
    			GeneDTO.MGI_ACCESSION_ID,	
    			GeneDTO.MARKER_SYMBOL, 
    			GeneDTO.MARKER_NAME, 
    			GeneDTO.MARKER_SYNONYM, 
    			GeneDTO.MARKER_TYPE,
    			GeneDTO.HUMAN_GENE_SYMBOL,
    			GeneDTO.STATUS,
    			GeneDTO.IMITS_PHENOTYPE_STARTED,
    			GeneDTO.IMITS_PHENOTYPE_COMPLETE,
    			GeneDTO.IMITS_PHENOTYPE_STATUS,
    			GeneDTO.LATEST_PHENOTYPE_STATUS,
    			GeneDTO.LATEST_PHENOTYPING_CENTRE,
    			GeneDTO.LATEST_PRODUCTION_CENTRE,
    			GeneDTO.LATEST_PHENOTYPING_CENTRE,
    			GeneDTO.ALLELE_NAME
            );
    	
    		SolrQuery geneQuery = new SolrQuery()
    		.setQuery("*:*")
    		.setFields(StringUtils.join(geneFields, ","))
    		.setRows(Integer.MAX_VALUE);
    
    		List<GeneDTO> genes = geneCore.query(geneQuery).getBeans(GeneDTO.class);
    		for (GeneDTO gene : genes) {
    			if ( gene.getMgiAccessionId() != null ){
	    			String mgiId = gene.getMgiAccessionId();
	    			if ( ! geneMap.containsKey(mgiId) ){
	    				geneMap.put(mgiId, Arrays.asList(gene));
	    			}
	    			List<GeneDTO> mpGenes = new ArrayList<>();
	    			mpGenes.add(gene);
	    			mpGenes.addAll(geneMap.get(mgiId));
	    			geneMap.put(mgiId, mpGenes);
    			}
    		}
    		logger.info("Finished populating genes map. Number of genes found: " + geneMap.size());   	
    }
    
    private void populateMp2GeneFromGP() throws SolrServerException{
    	List<String> gpFields = Arrays.asList(
    			GenotypePhenotypeDTO.MP_TERM_ID,	
    			GenotypePhenotypeDTO.MARKER_ACCESSION_ID
        );

        SolrQuery gpQuery = new SolrQuery()
            .setQuery("*:*")
            .setFields(StringUtils.join(gpFields, ","))
            .setRows(Integer.MAX_VALUE);
        
        List<GenotypePhenotypeDTO> gps = gpCore.query(gpQuery).getBeans(GenotypePhenotypeDTO.class);
        for (GenotypePhenotypeDTO gp : gps ) {
        	String mpid = gp.getMpTermId();
        	if ( ! mp2GeneFromGP.containsKey(mpid) ){
        		mp2GeneFromGP.put(mpid, Arrays.asList(gp));
        	}
        	List<GenotypePhenotypeDTO> gpGenes = new ArrayList<>();
			gpGenes.add(gp);
			gpGenes.addAll(mp2GeneFromGP.get(mpid));
			mp2GeneFromGP.put(mpid, gpGenes);
			mp2GeneFromGP.get(mpid).add(gp); 
        }
        
        logger.info("Finished populating genotype-phenotype using MP term as key. Number of MPs found: " + mp2GeneFromGP.size());  
    	
    }
    
    public static void main(String[] args) throws IndexerException, SQLException {
        MAIndexer indexer = new MAIndexer();
        indexer.initialise(args);
        indexer.run();
        indexer.validateBuild();

        logger.info("Process finished.  Exiting.");
    }
}
