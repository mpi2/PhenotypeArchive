/**
 * Copyright (c) 2014 Lemur Consulting Ltd.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package uk.ac.ebi.phenotype.solr.indexer;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import uk.ac.ebi.phenotype.service.dto.AlleleDTO;
import uk.ac.ebi.phenotype.service.dto.GenotypePhenotypeDTO;
import uk.ac.ebi.phenotype.solr.indexer.beans.DiseaseBean;
import uk.ac.ebi.phenotype.solr.indexer.beans.SangerAlleleBean;
import uk.ac.ebi.phenotype.solr.indexer.beans.SangerGeneBean;
import uk.ac.ebi.phenotype.solr.indexer.utils.IndexerMap;

import javax.annotation.Resource;
import javax.sql.DataSource;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Index the allele core from the sanger allele2 core
 *
 * @author Matt
 * @author jmason
 *
 */
public class AlleleIndexer extends AbstractIndexer {

    private static final Logger logger = LoggerFactory.getLogger(AlleleIndexer.class);
    public static final int PHENODIGM_BATCH_SIZE = 50000;
    private static Connection connection;

    private static final int BATCH_SIZE = 2500;

    private int assignedEvidCodeRank = 1; // default
    
    // Fetch all phenotyping completed genes with MP calls from genotype-phenotype core
    private static Set<String> gpGenesLookup = new HashSet<>();
    
    // Fetch all phenotyping started genes with MP calls from preqc core
    private static Set<String> preqcGenesLookup = new HashSet<>();
    
    
    // Map gene MGI ID to sanger allele bean
    private static Map<String, List<SangerAlleleBean>> statusLookup = new HashMap<>();

    // Map gene MGI ID to human symbols
    private static Map<String, Set<String>> humanSymbolLookup = new HashMap<>();

    // Map gene MGI ID to disease bean
    private static Map<String, List<DiseaseBean>> diseaseLookup = new HashMap<>();

    // Set of MGI IDs that have legacy projects
    private static Map<String, Integer> legacyProjectLookup = new HashMap<>();

    private static final Map<String, String> ES_CELL_STATUS_MAPPINGS = new HashMap<>();

    // Set of MGI IDs that have GO annotation(s)
    private static Map<String, Set<GoAnnotations>> goTermLookup = new HashMap<>();
    
    // Map MGI accession id to longest Uniprot accession
    private static Map<String, Set<String>> mgi2UniprotLookup = new HashMap<>();
    
    // Uniprot to pfamA mapping
	private static Map<String, Set<PfamAnnotations>> uniprotAccPfamAnnotLookup = new HashMap<>();
	//private static Map<String, Set<String>> uniprotAccPfamJsonLookup = new HashMap<>();

    static {
        ES_CELL_STATUS_MAPPINGS.put("No ES Cell Production", "Not Assigned for ES Cell Production");
        ES_CELL_STATUS_MAPPINGS.put("ES Cell Production in Progress", "Assigned for ES Cell Production");
        ES_CELL_STATUS_MAPPINGS.put("ES Cell Targeting Confirmed", "ES Cells Produced");
    }

    private static final Map<String, String> MOUSE_STATUS_MAPPINGS = new HashMap<>();

    static {
        MOUSE_STATUS_MAPPINGS.put("Chimeras obtained", "Assigned for Mouse Production and Phenotyping");
        MOUSE_STATUS_MAPPINGS.put("Micro-injection in progress", "Assigned for Mouse Production and Phenotyping");
        MOUSE_STATUS_MAPPINGS.put("Cre Excision Started", "Mice Produced");
        MOUSE_STATUS_MAPPINGS.put("Rederivation Complete", "Mice Produced");
        MOUSE_STATUS_MAPPINGS.put("Rederivation Started", "Mice Produced");
        MOUSE_STATUS_MAPPINGS.put("Genotype confirmed", "Mice Produced");
        MOUSE_STATUS_MAPPINGS.put("Cre Excision Complete", "Mice Produced");
        MOUSE_STATUS_MAPPINGS.put("Phenotype Attempt Registered", "Mice Produced");
    }

    private SolrServer sangerAlleleCore;
    private SolrServer phenodigmCore;

    @Autowired
    @Qualifier("komp2DataSource")
    DataSource komp2DataSource;

    @Autowired
    @Qualifier("goaproDataSource")
    DataSource goaproDataSource;
    
    @Autowired
    @Qualifier("uniprotDataSource")
    DataSource uniprotDataSource;
    
    @Autowired
    @Qualifier("pfamDataSource")
    DataSource pfamDataSource;
    
    @Autowired
    @Qualifier("alleleIndexing")
    private SolrServer alleleCore;
    
    @Resource(name = "globalConfiguration")
    private Map<String, String> config;

    public AlleleIndexer() {

    }

    @Override
    public void validateBuild() throws IndexerException {
        Long numFound = getDocumentCount(alleleCore);
        
        if (numFound <= MINIMUM_DOCUMENT_COUNT)
            throw new IndexerException(new ValidationException("Actual allele document count is " + numFound + "."));
        
        if (numFound != documentCount)
            logger.warn("WARNING: Added " + documentCount + " allele documents but SOLR reports " + numFound + " documents.");
        else
            logger.info("validateBuild(): Indexed " + documentCount + " allele documents.");
    }

    @Override
    protected Logger getLogger() {

        return logger;
    }

    @Override
    public void run() throws IndexerException {

    	int start = 0;
        long rows = 0;
        long startTime = new Date().getTime();

        try {
            connection = komp2DataSource.getConnection();

            initializeSolrCores();

            SolrQuery query = new SolrQuery("mgi_accession_id:MGI*");  
            query.addFilterQuery("feature_type:* AND -feature_type:Pseudogene AND -feature_type:\"heritable+phenotypic+marker\" AND type:gene");
            query.setRows(BATCH_SIZE);

            logger.info("Populating lookups");

            populateStatusLookup();
            logger.info("Populated status lookup, {} records", statusLookup.size());

            populateHumanSymbolLookup();
            logger.info("Populated human symbol lookup, {} records", humanSymbolLookup.size());

            populateDiseaseLookup();
            logger.info("Populated disease lookup, {} records", diseaseLookup.size());

            populateLegacyLookup();
            logger.info("Populated legacy project lookup, {} records", legacyProjectLookup.size());

            // GoTerm from GO at EBI: MGI gene id to GO term mapping
            populateGoTermLookup();
            logger.info("Populated go terms lookup, {} records", goTermLookup.size());

            // MGI gene id to Uniprot accession mapping
            populateMgi2UniprotLookup();
            logger.info("Populated mgi to uniprot lookup, {} records", mgi2UniprotLookup.size());
            
            // Uniprot to pfamA mapping
            populateUniprot2pfamA();
            logger.info("Populated uniprot to pfamA lookup, {} records", uniprotAccPfamAnnotLookup.size());
           
            
            alleleCore.deleteByQuery("*:*");
            alleleCore.commit();

            while (start <= rows) {
                query.setStart(start);
                QueryResponse response = sangerAlleleCore.query(query);
                rows = response.getResults().getNumFound();
                List<SangerGeneBean> sangerGenes = response.getBeans(SangerGeneBean.class);

                // Convert to Allele DTOs
                Map<String, AlleleDTO> alleles = convertSangerGeneBeans(sangerGenes);

                // Look up the marker synonyms
                lookupMarkerSynonyms(alleles);

                // Look up the human mouse symbols
                lookupHumanMouseSymbols(alleles);

                // Look up the ES cell status
                lookupEsCellStatus(alleles);

                // Look up the disease data
                lookupDiseaseData(alleles);

                // Look up the GO Term data
                lookupGoData(alleles);

                // Look up gene to Uniprot mapping
                lookupUniprotAcc(alleles);
                
                // Look up uniprot to pfamA mapping
                // NOTE: this MUST be done after lookupUniprotAcc()
                lookupUniprotAcc2pfamA(alleles);
                
                
                // Now index the alleles
                documentCount += alleles.size();
                indexAlleles(alleles);

                start += BATCH_SIZE;

                logger.info("Indexed {} records", start);

            }

            alleleCore.commit();

        } catch (SQLException | SolrServerException | IOException | ClassNotFoundException e) {
            throw new IndexerException(e);
        }

        logger.debug("Complete - took {}ms", (new Date().getTime() - startTime));
    }

    private void initializeSolrCores() {

        final String SANGER_ALLELE_URL = config.get("imits.solrserver");
        final String PHENODIGM_URL = config.get("phenodigm.solrserver");

        // Use system proxy if set for external solr servers
        if (System.getProperty("externalProxyHost") != null && System.getProperty("externalProxyPort") != null) {

            String PROXY_HOST = System.getProperty("externalProxyHost");
            Integer PROXY_PORT = Integer.parseInt(System.getProperty("externalProxyPort"));

            HttpHost proxy = new HttpHost(PROXY_HOST, PROXY_PORT);
            DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
            CloseableHttpClient client = HttpClients.custom().setRoutePlanner(routePlanner).build();

            logger.info("Using Proxy Settings: " + PROXY_HOST + " on port: " + PROXY_PORT);

            this.sangerAlleleCore = new HttpSolrServer(SANGER_ALLELE_URL, client);
            this.phenodigmCore = new HttpSolrServer(PHENODIGM_URL, client);

        } else {

            this.sangerAlleleCore = new HttpSolrServer(SANGER_ALLELE_URL);
            this.phenodigmCore = new HttpSolrServer(PHENODIGM_URL);

        }
    }

    public class GoAnnotations {

        public String goTermId;
        public String goTermName;
        public String goTermDef;    // not populated for now
        public String goTermEvid; 	
        public String goTermDomain;   
		public String mgiSymbol;
		//public String uniprotAcc;

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            GoAnnotations that = (GoAnnotations) o;

            if (goTermDomain != null ?  ! goTermDomain.equals(that.goTermDomain) : that.goTermDomain != null) {
                return false;
            }
            /*if (goTermDef != null ?  ! goTermDef.equals(that.goTermDef) : that.goTermDef != null) {
                return false;
            }*/
            if (goTermEvid != null ?  ! goTermEvid.equals(that.goTermEvid) : that.goTermEvid != null) {
                return false;
            }
            if (goTermId != null ?  ! goTermId.equals(that.goTermId) : that.goTermId != null) {
                return false;
            }
            if (goTermName != null ?  ! goTermName.equals(that.goTermName) : that.goTermName != null) {
                return false;
            }
//            if (uniprotAcc != null ?  ! uniprotAcc.equals(that.uniprotAcc) : that.uniprotAcc != null) {
//                return false;
//            }

            return true;
        }

        @Override
        public int hashCode() {
            int result = goTermId != null ? goTermId.hashCode() : 0;
            result = 31 * result + (goTermName != null ? goTermName.hashCode() : 0);
           // result = 31 * result + (goTermDef != null ? goTermDef.hashCode() : 0);
            result = 31 * result + (goTermEvid != null ? goTermEvid.hashCode() : 0);
            result = 31 * result + (goTermDomain != null ? goTermDomain.hashCode() : 0);
            //result = 31 * result + (uniprotAcc != null ? uniprotAcc.hashCode() : 0);
            return result;
        }
    }
    
    public class PfamAnnotations {
		
		public String scdbId;
		public String scdbLink;
		public String clanId;
		public String clanAcc;
		public String clanDesc;
		public String uniprotAcc;
		public String uniprotId;
		public String pfamAacc;
		public String pfamAId;
		public String pfamAgoId;
		public String pfamAgoTerm;
		public String pfamAgoCat;
		public String pfamAjson;
		
	   @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            PfamAnnotations that = (PfamAnnotations) o;

            if (scdbId != null ?  ! scdbId.equals(that.scdbId) : that.scdbId != null) {
                return false;
            }
            if (scdbLink != null ?  ! scdbLink.equals(that.scdbLink) : that.scdbLink != null) {
                return false;
            }
            if (clanId != null ?  ! clanId.equals(that.clanId) : that.clanId != null) {
                return false;
            }
            if (clanAcc != null ?  ! clanAcc.equals(that.clanAcc) : that.clanAcc != null) {
                return false;
            }
            if (clanDesc != null ?  ! clanDesc.equals(that.clanDesc) : that.clanDesc != null) {
                return false;
            }
            if (pfamAacc != null ?  ! pfamAacc.equals(that.pfamAacc) : that.pfamAacc != null) {
                return false;
            }
            if (pfamAId != null ?  ! pfamAId.equals(that.pfamAId) : that.pfamAId != null) {
                return false;
            }
            /*if (pfamAgoId != null ?  ! pfamAgoId.equals(that.pfamAgoId) : that.pfamAgoId != null) {
                return false;
            }
            if (pfamAgoTerm != null ?  ! pfamAgoTerm.equals(that.pfamAgoTerm) : that.pfamAgoTerm != null) {
                return false;
            }
            if (pfamAgoCat != null ?  ! pfamAgoCat.equals(that.pfamAgoCat) : that.pfamAgoCat != null) {
                return false;
            }*/

            return true;
        }

        @Override
        public int hashCode() {
            int result = scdbId != null ? scdbId.hashCode() : 0;
            result = 31 * result + (scdbLink != null ? scdbLink.hashCode() : 0);
            result = 31 * result + (clanId != null ? clanId.hashCode() : 0);
            result = 31 * result + (clanAcc != null ? clanAcc.hashCode() : 0);
            result = 31 * result + (clanDesc != null ? clanDesc.hashCode() : 0);
            result = 31 * result + (pfamAacc != null ? pfamAacc.hashCode() : 0);
            result = 31 * result + (pfamAId != null ? pfamAId.hashCode() : 0);
            
            
            return result;
        }

        // these getters/setters are needed as JSONSerializer.toJSON() works on JavaBeans
		public String getScdbId() {
			return scdbId;
		}

		public void setScdbId(String scdbId) {
			this.scdbId = scdbId;
		}

		public String getScdbLink() {
			return scdbLink;
		}

		public void setScdbLink(String scdbLink) {
			this.scdbLink = scdbLink;
		}

		public String getClanId() {
			return clanId;
		}

		public void setClanId(String clanId) {
			this.clanId = clanId;
		}

		public String getClanAcc() {
			return clanAcc;
		}

		public void setClanAcc(String clanAcc) {
			this.clanAcc = clanAcc;
		}

		public String getClanDesc() {
			return clanDesc;
		}

		public void setClanDesc(String clanDesc) {
			this.clanDesc = clanDesc;
		}

		public String getUniprotAcc() {
			return uniprotAcc;
		}

		public void setUniprotAcc(String uniprotAcc) {
			this.uniprotAcc = uniprotAcc;
		}

		public String getUniprotId() {
			return uniprotId;
		}

		public void setUniprotId(String uniprotId) {
			this.uniprotId = uniprotId;
		}

		public String getPfamAacc() {
			return pfamAacc;
		}

		public void setPfamAacc(String pfamAacc) {
			this.pfamAacc = pfamAacc;
		}

		public String getPfamAId() {
			return pfamAId;
		}

		public void setPfamAId(String pfamAId) {
			this.pfamAId = pfamAId;
		}

		public String getPfamAgoId() {
			return pfamAgoId;
		}

		public void setPfamAgoId(String pfamAgoId) {
			this.pfamAgoId = pfamAgoId;
		}

		public String getPfamAgoTerm() {
			return pfamAgoTerm;
		}

		public void setPfamAgoTerm(String pfamAgoTerm) {
			this.pfamAgoTerm = pfamAgoTerm;
		}

		public String getPfamAgoCat() {
			return pfamAgoCat;
		}

		public void setPfamAgoCat(String pfamAgoCat) {
			this.pfamAgoCat = pfamAgoCat;
		}

		public String getPfamAjson() {
			return pfamAjson;
		}

		public void setPfamAjson(String pfamAjson) {
			this.pfamAjson = pfamAjson;
		}
		
	}
    
    private void populateGoTermLookup() throws IOException, SQLException, ClassNotFoundException {

	    String queryString = "select distinct m.gene_name, a.go_id, t.name as go_name, t.category as go_domain, evi.go_evidence "
				+ "from go.annotations a "
				+ "join "
				+ "go.cv_sources s on (s.code = a.source) "
				+ "join "
				+ "go.eco2evidence evi on (evi.eco_id = a.eco_id) "
				+ "join "
				+ "go.terms t on (t.go_id = a.go_id) "
				+ "join "
				+ "go.uniprot_protein_metadata m on (m.accession = a.canonical_id) "
				+ "where "
				+ "s.is_public = 'Y' "
				+ "and m.tax_id = 10090 "
				+ "and m.gene_name is not null "
				+ "and t.category in ('F', 'P') "
				+ "and a.qualifier not like 'NOT%'";
	    
	    Connection conn = goaproDataSource.getConnection();

	    try (PreparedStatement p = conn.prepareStatement(queryString)) {
            ResultSet resultSet = p.executeQuery();

            while (resultSet.next()) {
            	
            	GoAnnotations ga = new GoAnnotations();
            	
    			ga.mgiSymbol  = resultSet.getString("gene_name");
    			//ga.uniprotAcc = resultSet.getString("accession");
    			ga.goTermId   = resultSet.getString("go_id");
    			ga.goTermName = resultSet.getString("go_name");
    			ga.goTermEvid = resultSet.getString("go_evidence");
    			ga.goTermDomain = resultSet.getString("go_domain").toString().equals("F") ? "molecular_function" : "biological_process";
    			
            	if ( ! goTermLookup.containsKey(ga.mgiSymbol)) {
            		goTermLookup.put(ga.mgiSymbol, new HashSet<GoAnnotations>());
            	}
            	
    			goTermLookup.get(ga.mgiSymbol).add(ga);
            }

            logger.info("Populated goTerm lookup, {} records", goTermLookup.size());

        } 
	    catch (Exception e) {
            e.printStackTrace();
        }
	}

    private void populateMgi2UniprotLookup() throws IOException, SQLException, ClassNotFoundException{
    	
    	// first we need to prepare of Map for Ensembl Gene Id -> MGI id 
		String komp2Qry = "select xref_acc, acc from xref where xref_acc like 'ENSMUSG%'";
		Map<String, String> ensg2mgi = new HashMap<>();
		
		try (PreparedStatement s = connection.prepareStatement(komp2Qry)) {
            ResultSet resultSet = s.executeQuery();

            while (resultSet.next()) {
            	ensg2mgi.put(resultSet.getString("xref_acc"), resultSet.getString("acc"));
            }
	    }
	    catch(Exception e) {
            e.printStackTrace();
	    } 
		
	    String queryString = "SELECT distinct name, accession "
	    		+ "FROM sptr.GENE_CENTRIC_ENTRY "
	    		+ "WHERE tax_id = 10090 "
	    		//+ "AND IS_CANONICAL = 1 " (take all, including isoforms)
	    		+ "AND release IN "
	    		+ " (SELECT max(release) FROM sptr.GENE_CENTRIC_ENTRY where tax_id = 10090 and IS_CANONICAL = 1 ) ";
	    	
	    Connection connUniprot = uniprotDataSource.getConnection();
	    
	    
	    // take all isoforms of gene product mapped to uniprot (swissprot or trembl)
	    try (PreparedStatement p = connUniprot.prepareStatement(queryString)) {
            ResultSet resultSet = p.executeQuery();

            while (resultSet.next()) {
            	String geneLabel = resultSet.getString("name");
            	String uniprotAcc = resultSet.getString("accession");
            	
            	if ( ensg2mgi.containsKey(geneLabel) ){
            		// ensembl gene id to mgi_id conversion
            		String mgiId = ensg2mgi.get(geneLabel);
            		
            		if ( ! mgi2UniprotLookup.containsKey(mgiId) ) {
            			mgi2UniprotLookup.put(mgiId, new HashSet<String>());
            		}
            		mgi2UniprotLookup.get(mgiId).add(uniprotAcc);
            	}
            	else {
            		// this would be either mgi id or mgi symbol
            		if ( ! mgi2UniprotLookup.containsKey(geneLabel) ) {
            			mgi2UniprotLookup.put(geneLabel, new HashSet<String>());
            		}
            		mgi2UniprotLookup.get(geneLabel).add(uniprotAcc);
            	}
            }
	    }
	    catch(Exception e) {
            e.printStackTrace();
	    } 
    	
	}
    
    private void populateUniprot2pfamA() throws IOException, SQLException, ClassNotFoundException{
    	
    	// do batch lookup of uniprot accs on pfam db (pfamA)
      
    	Connection connPfam = pfamDataSource.getConnection();
       
    	String pfamQry = "SELECT lk.db_id, "
       			+ "lk.db_link, "
       			+ "c.clan_id, "
       			+ "c.clan_acc, "
       			+ "c.clan_description, "
       			+ "pfamseq_id, "
       			+ "pfamseq_acc, "
       			+ "pfamA_acc, "
       			+ "pfamA_id, "
       			+ "g.go_id, "
       			+ "g.term as go_name, "
       			+ "g.category as go_category "
       			+ "FROM pfamseq s, "
       			+ " pfamA a, "
       			+ " pfamA_reg_full_significant f, "
       			+ " gene_ontology g, "
       			+ " clans c, "
       			+ " clan_membership cm, "
       			+ " clan_database_links lk "
       			+ "WHERE f.in_full = 1 "
       			+ "AND lk.auto_clan = c.auto_clan "
       			+ "AND c.auto_clan = cm.auto_clan "
       			+ "AND cm.auto_pfamA = a.auto_pfamA "
       			+ "AND s.auto_pfamseq = f.auto_pfamseq "
       			+ "AND f.auto_pfamA = a.auto_pfamA "
       			+ "AND a.auto_pfamA = g.auto_pfamA "
       			+ "AND s.ncbi_taxid=10090 "  // mouse proteins only
       			+ "AND a.type = 'family' ";
       
    	try (PreparedStatement p2 = connPfam.prepareStatement(pfamQry)) {
    		ResultSet resultSet2 = p2.executeQuery();

    		while (resultSet2.next()) {
    			
	        	PfamAnnotations pa = new PfamAnnotations();
	           	
	           	pa.uniprotAcc = resultSet2.getString("pfamseq_acc");
	           	pa.scdbId = resultSet2.getString("db_id");
	           	pa.scdbLink = resultSet2.getString("db_link");
	           	pa.clanId = resultSet2.getString("clan_id");
	           	pa.clanAcc = resultSet2.getString("clan_acc");
	           	pa.clanDesc = resultSet2.getString("clan_description");
	           	pa.pfamAacc = resultSet2.getString("pfamA_acc");
	           	pa.pfamAId =  resultSet2.getString("pfamA_id");
	           	pa.pfamAgoId = resultSet2.getString("go_id");
	           	pa.pfamAgoTerm = resultSet2.getString("go_name");
	           	pa.pfamAgoCat = resultSet2.getString("go_category");
		       	
	           	if ( ! uniprotAccPfamAnnotLookup.containsKey(pa.uniprotAcc)) {
	           		uniprotAccPfamAnnotLookup.put(pa.uniprotAcc, new HashSet<PfamAnnotations>());
	           	}

	           	pa.pfamAjson = JSONSerializer.toJSON(pa).toString(); // add the above fields as json string
	           	uniprotAccPfamAnnotLookup.get(pa.uniprotAcc).add(pa);
           }
           
           //System.out.println("Found " + uniprotAccPfamJsonLookup.size() + " mouse proteins annotated in pFam");
       }
       catch (Exception e) {
           e.printStackTrace();
       }
	}
    
    private void populateLegacyLookup() throws SolrServerException {

        String query = "SELECT DISTINCT project_id, gf_acc FROM phenotype_call_summary WHERE p_value < 0.0001 AND (project_id = 1 OR project_id = 8)";

        try (PreparedStatement ps = connection.prepareStatement(query)) {

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                legacyProjectLookup.put(rs.getString("gf_acc"), 1);

            }
        } catch (SQLException e) {
            logger.error("SQL Exception looking up legacy projects: {}", e.getMessage());
        }

    }

    private void populateStatusLookup() throws SolrServerException {

        SolrQuery query = new SolrQuery("*:*");
        query.setRows(Integer.MAX_VALUE);
        query.addFilterQuery("type:allele");

        QueryResponse response = sangerAlleleCore.query(query);
        List<SangerAlleleBean> sangerAlleles = response.getBeans(SangerAlleleBean.class);
        for (SangerAlleleBean allele : sangerAlleles) {
            if ( ! statusLookup.containsKey(allele.getMgiAccessionId())) {
                statusLookup.put(allele.getMgiAccessionId(), new ArrayList<SangerAlleleBean>());
            }
            statusLookup.get(allele.getMgiAccessionId()).add(allele);
        }
    }

    private void populateHumanSymbolLookup() throws IOException {

        File file = new File(config.get("human2mouseFilename"));
        List<String> lines = FileUtils.readLines(file, "UTF-8");

        for (String line : lines) {
            String[] pieces = line.trim().split("\t");

            if (pieces.length < 5) {
                continue;
            }

            String humanSymbol = pieces[0];
            String mgiId = pieces[4].trim();
            if ( ! mgiId.startsWith("MGI:")) {
                continue;
            }

            if ( ! humanSymbolLookup.containsKey(mgiId)) {
                humanSymbolLookup.put(mgiId, new HashSet<String>());
            }

            (humanSymbolLookup.get(mgiId)).add(humanSymbol);

        }

    }

    private void populateDiseaseLookup() throws SolrServerException {

        int docsRetrieved = 0;
        int numDocs = getDiseaseDocCount();

        // Fields in the solr core to bring back
        String fields = StringUtils.join(Arrays.asList(DiseaseBean.DISEASE_ID,
                                                       DiseaseBean.MGI_ACCESSION_ID,
                                                       DiseaseBean.DISEASE_SOURCE,
                                                       DiseaseBean.DISEASE_TERM,
                                                       DiseaseBean.DISEASE_ALTS,
                                                       DiseaseBean.DISEASE_CLASSES,
                                                       DiseaseBean.HUMAN_CURATED,
                                                       DiseaseBean.MOUSE_CURATED,
                                                       DiseaseBean.MGI_PREDICTED,
                                                       DiseaseBean.IMPC_PREDICTED,
                                                       DiseaseBean.MGI_PREDICTED_KNOWN_GENE,
                                                       DiseaseBean.IMPC_PREDICTED_KNOWN_GENE,
                                                       DiseaseBean.MGI_NOVEL_PREDICTED_IN_LOCUS,
                                                       DiseaseBean.IMPC_NOVEL_PREDICTED_IN_LOCUS), ",");

		// The solrcloud instance cannot give us all results back at once,
        // we must batch up the calls and build it up piece at a time
        while (docsRetrieved < numDocs + PHENODIGM_BATCH_SIZE) {

            SolrQuery query = new SolrQuery("*:*");
            query.addFilterQuery("type:disease_gene_summary");
            query.setFields(fields);
            query.setStart(docsRetrieved);
            query.setRows(PHENODIGM_BATCH_SIZE);
            query.setSort(DiseaseBean.DISEASE_ID, SolrQuery.ORDER.asc);

            QueryResponse response = phenodigmCore.query(query);
            List<DiseaseBean> diseases = response.getBeans(DiseaseBean.class);
            for (DiseaseBean disease : diseases) {
                if ( ! diseaseLookup.containsKey(disease.getMgiAccessionId())) {
                    diseaseLookup.put(disease.getMgiAccessionId(), new ArrayList<DiseaseBean>());
                }
                diseaseLookup.get(disease.getMgiAccessionId()).add(disease);
            }

            docsRetrieved += PHENODIGM_BATCH_SIZE;
            logger.info("Processed {} documents from phenodigm. {} genes in the index", docsRetrieved, diseaseLookup.size());

        }
    }

    private int getDiseaseDocCount() throws SolrServerException {

        SolrQuery query = new SolrQuery("*:*");
        query.setRows(0);
        query.addFilterQuery("type:disease_gene_summary");

        QueryResponse response = phenodigmCore.query(query);
        return (int) response.getResults().getNumFound();
    }

    private Map<String, AlleleDTO> convertSangerGeneBeans(List<SangerGeneBean> beans) {

        Map<String, AlleleDTO> map = new HashMap<>(beans.size());

        for (SangerGeneBean bean : beans) {
            String id = bean.getMgiAccessionId();
            AlleleDTO dto = new AlleleDTO();

            // Copy the fields
            dto.setMgiAccessionId(id);
            dto.setMarkerType(bean.getFeatureType());
            dto.setMarkerSymbol(bean.getMarkerSymbol());
            dto.setGeneLatestEsCellStatus(bean.getLatestEsCellStatus());
            dto.setGeneLatestMouseStatus(bean.getLatestMouseStatus());
            dto.setImitsPhenotypeStarted(bean.getLatestPhenotypeStarted());
            dto.setImitsPhenotypeComplete(bean.getLatestPhenotypeComplete());
            dto.setLatestPhenotypeStatus(bean.getLatestPhenotypeStatus());
            dto.setLatestProductionCentre(bean.getLatestProductionCentre());
            dto.setLatestPhenotypingCentre(bean.getLatestPhenotypingCentre());
            dto.setLatestProjectStatus(bean.getLatestProjectStatus());
            dto.setAlleleAccessionIds(bean.getMgiAlleleAccessionIds());

            String latestEsStatus = ES_CELL_STATUS_MAPPINGS.containsKey(bean.getLatestEsCellStatus()) ? ES_CELL_STATUS_MAPPINGS.get(bean.getLatestEsCellStatus()) : bean.getLatestEsCellStatus();
            dto.setLatestProductionStatus(latestEsStatus);
            dto.setLatestEsCellStatus(latestEsStatus);

            if (StringUtils.isNotEmpty(bean.getLatestMouseStatus())) {
                String latestMouseStatus = MOUSE_STATUS_MAPPINGS.containsKey(bean.getLatestMouseStatus()) ? MOUSE_STATUS_MAPPINGS.get(bean.getLatestMouseStatus()) : bean.getLatestMouseStatus();
                dto.setLatestProductionStatus(latestMouseStatus);
                dto.setLatestMouseStatus(latestMouseStatus);
            }

            if (legacyProjectLookup.containsKey(bean.getMgiAccessionId())) {
                dto.setLegacyPhenotypeStatus(1);
            }

            // Do the additional mappings
            dto.setDataType(AlleleDTO.ALLELE_DATA_TYPE);

            map.put(id, dto);
        }

        return map;
    }

    private void lookupMarkerSynonyms(Map<String, AlleleDTO> alleles) {
        // Build the lookup string
        String lookup = buildIdQuery(alleles.keySet());

        String query = "select s.acc as id, s.symbol as marker_synonym, gf.name as marker_name "
                + "from synonym s, genomic_feature gf "
                + "where s.acc=gf.acc "
                + "and gf.acc IN (" + lookup + ")";
        try {
            logger.debug("Starting marker synonym lookup");
            PreparedStatement ps = connection.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String id = rs.getString("id");
                AlleleDTO allele = alleles.get(id);
                if (allele.getMarkerSynonym() == null) {
                    allele.setMarkerSynonym(new ArrayList<String>());
                }
                allele.getMarkerSynonym().add(rs.getString("marker_synonym"));
                allele.setMarkerName(rs.getString("marker_name"));
            }
            logger.debug("Finished marker synonym lookup");
        } catch (SQLException sqle) {
            logger.error("SQL Exception looking up marker symbols: {}", sqle.getMessage());
        }
    }

    private void lookupHumanMouseSymbols(Map<String, AlleleDTO> alleles) {

        for (String id : alleles.keySet()) {
            AlleleDTO dto = alleles.get(id);

            if (humanSymbolLookup.containsKey(id)) {
                dto.setHumanGeneSymbol(new ArrayList<>(humanSymbolLookup.get(id)));
            }

        }

        logger.debug("Finished human marker symbol lookup");
    }

    private String buildIdQuery(Collection<String> ids) {

        StringBuilder lookup = new StringBuilder();
        int i = 0;
        for (String id : ids) {
            if (i > 0) {
                lookup.append(",");
            }
            lookup.append("'").append(id).append("'");
            i ++;
        }
        return lookup.toString();
    }

    private void lookupEsCellStatus(Map<String, AlleleDTO> alleles) {

        for (String id : alleles.keySet()) {
            AlleleDTO dto = alleles.get(id);

            if ( ! statusLookup.containsKey(id)) {
                continue;
            }

            for (SangerAlleleBean sab : statusLookup.get(id)) {

                dto.getAlleleName().add(sab.getAlleleName());
                dto.getPhenotypeStatus().add(sab.getPhenotypeStatus());
                dto.getProductionCentre().add(sab.getProductionCentre());
                dto.getPhenotypingCentre().add(sab.getPhenotypingCentre());

                String esCellStat = ES_CELL_STATUS_MAPPINGS.containsKey(sab.getEsCellStatus()) ? ES_CELL_STATUS_MAPPINGS.get(sab.getEsCellStatus()) : sab.getEsCellStatus();
                dto.getEsCellStatus().add(esCellStat);

                if (StringUtils.isNotEmpty(sab.getMouseStatus())) {
                    String mouseStatus = MOUSE_STATUS_MAPPINGS.containsKey(sab.getMouseStatus()) ? MOUSE_STATUS_MAPPINGS.get(sab.getMouseStatus()) : sab.getMouseStatus();
                    dto.getMouseStatus().add(mouseStatus);
                } else {
                    dto.getMouseStatus().add("");
                }

//				dto.setImitsEsCellStatus(sab.getEsCellStatus());
//				dto.setImitsMouseStatus(sab.getMouseStatus());
            }
        }

        logger.debug("Finished ES cell status lookup");
    }

    private void lookupDiseaseData(Map<String, AlleleDTO> alleles) {

        logger.debug("Starting disease data lookup");
        for (String id : alleles.keySet()) {

            AlleleDTO dto = alleles.get(id);

            if ( ! diseaseLookup.containsKey(id)) {
                continue;
            }

            for (DiseaseBean db : diseaseLookup.get(id)) {
                dto.getDiseaseId().add(db.getDiseaseId());
                dto.getDiseaseSource().add(db.getDiseaseSource());
                dto.getDiseaseTerm().add(db.getDiseaseTerm());
                if (db.getDiseaseAlts() != null) {
                    dto.getDiseaseAlts().addAll(db.getDiseaseAlts());
                }
                if (db.getDiseaseClasses() != null) {
                    dto.getDiseaseClasses().addAll(db.getDiseaseClasses());
                }
                dto.getHumanCurated().add(db.isHumanCurated());
                dto.getMouseCurated().add(db.isMouseCurated());
                dto.getMgiPredicted().add(db.isMgiPredicted());
                dto.getImpcPredicted().add(db.isImpcPredicted());
                dto.getMgiPredictedKnownGene().add(db.isMgiPredictedKnownGene());
                dto.getImpcPredictedKnownGene().add(db.isImpcPredictedKnownGene());
                dto.getMgiNovelPredictedInLocus().add(db.isMgiNovelPredictedInLocus());
                dto.getImpcNovelPredictedInLocus().add(db.isImpcNovelPredictedInLocus());
            }

        }
        logger.debug("Finished disease data lookup");
    }
    
    private Integer assignCodeRank(int currRank){
    	// set for highest evidCodeRank
    	assignedEvidCodeRank = currRank > assignedEvidCodeRank ? currRank : assignedEvidCodeRank;
    	return assignedEvidCodeRank;
    }
    
    private void lookupGoData(Map<String, AlleleDTO> alleles) {
        logger.debug("Starting GO data lookup");

        //GO evidence code ranking mapping
        Map<String,Integer> codeRank = new HashMap<>();
        // experimental 
        codeRank.put("EXP", 4);codeRank.put("IDA", 4);codeRank.put("IPI", 4);codeRank.put("IMP", 4);
        codeRank.put("IGI", 4);codeRank.put("IEP", 4);codeRank.put("TAS", 4);
        
        // curated computational
        codeRank.put("ISS", 3);codeRank.put("ISO", 3);codeRank.put("ISA", 3);codeRank.put("ISM", 3);
        codeRank.put("IGC", 3);codeRank.put("IBA", 3);codeRank.put("IBD", 3);codeRank.put("IKR", 3);
        codeRank.put("IRD", 3);codeRank.put("RCA", 3);codeRank.put("IC", 3);codeRank.put("NAS", 3);
        
        // automated electronic
        codeRank.put("IEA", 2);
        
        // no biological data available
        codeRank.put("ND", 1);
        
        for (String id : alleles.keySet()) {

            AlleleDTO dto = alleles.get(id);
           
            assignedEvidCodeRank = 1; // reset
            
            // GO is populated based on gene symbol
            if ( ! goTermLookup.containsKey(dto.getMarkerSymbol())) {
                continue;
            }

            for (GoAnnotations ga : goTermLookup.get(dto.getMarkerSymbol())) {
                dto.getGoTermIds().add(ga.goTermId);
                dto.getGoTermNames().add(ga.goTermName);
                //dto.getGoTermDefs().add(ga.goTermDef);
                dto.getGoTermEvids().add(ga.goTermEvid);
                dto.getGoTermDomains().add(ga.goTermDomain);
                dto.setEvidCodeRank( assignCodeRank(codeRank.get(ga.goTermEvid)) );
            }
        }
    }

    private void lookupUniprotAcc(Map<String, AlleleDTO> alleles) {
    	 logger.debug("Starting Uniprot Acc lookup");
         for (String id : alleles.keySet()) {

             AlleleDTO dto = alleles.get(id);
             
             String gSymbol = dto.getMarkerSymbol();
             String mgiAcc = dto.getMgiAccessionId();
             
             if ( ! mgi2UniprotLookup.containsKey(gSymbol) && ! mgi2UniprotLookup.containsKey(mgiAcc) ) {
                 continue;
             }
             else if ( mgi2UniprotLookup.containsKey(gSymbol)  ){
            	 
            	 dto.setUniprotAccs(new ArrayList<String>(mgi2UniprotLookup.get(gSymbol)));
             }
             else if ( mgi2UniprotLookup.containsKey(mgiAcc) ){
            	 dto.setUniprotAccs(new ArrayList<String>(mgi2UniprotLookup.get(mgiAcc)));
             }
         }
    }
    
    private void lookupUniprotAcc2pfamA(Map<String, AlleleDTO> alleles) {
    	logger.debug("Starting Uniprot to pfamA lookup");

        for (String id : alleles.keySet()) {

        	AlleleDTO dto = alleles.get(id);
            
            List<String> uniproAccs = dto.getUniprotAccs();
        
        	List<String> scdbIds = new ArrayList<>();
            List<String> scdbLinks = new ArrayList<>();
            List<String> clanIds = new ArrayList<>();
            List<String> clanAccs = new ArrayList<>();
            List<String> clanDescs = new ArrayList<>();
            List<String> pfamAIds = new ArrayList<>();
            List<String> pfamAaccs = new ArrayList<>();
            List<String> pfamAgoIds = new ArrayList<>();
            List<String> pfamAgoTerms = new ArrayList<>();
            List<String> pfamAgoCats = new ArrayList<>();
            List<String> pfamAjsons = new ArrayList<>();
            
            for ( String uniproAcc : uniproAccs ){
            	
            	if ( ! uniprotAccPfamAnnotLookup.containsKey(uniproAcc) ) {
            		continue;
            	}
            
	            for ( PfamAnnotations pa : uniprotAccPfamAnnotLookup.get(uniproAcc) ) {
	            	scdbIds.add(pa.scdbId);
	            	scdbLinks.add(pa.scdbLink);
	                clanIds.add(pa.clanId);
	                clanAccs.add(pa.clanAcc);
	                clanDescs.add(pa.clanDesc);
	                pfamAIds.add(pa.pfamAId);
	                pfamAaccs.add(pa.pfamAacc);
	                pfamAgoIds.add(pa.pfamAgoId);
	                pfamAgoTerms.add(pa.pfamAgoTerm);
	                pfamAgoCats.add(pa.pfamAgoCat);
	                pfamAjsons.add(pa.pfamAjson);
	            }
            }
            // get unique
            dto.getScdbIds().addAll(new ArrayList<>(new LinkedHashSet<>(scdbIds)));
            dto.getScdbLinks().addAll(new ArrayList<>(new LinkedHashSet<>(scdbLinks)));
            dto.getClanIds().addAll(new ArrayList<>(new LinkedHashSet<>(clanIds)));
            dto.getClanAccs().addAll(new ArrayList<>(new LinkedHashSet<>(clanAccs)));
            dto.getClanDescs().addAll(new ArrayList<>(new LinkedHashSet<>(clanDescs)));
            dto.getPfamaIds().addAll(new ArrayList<>(new LinkedHashSet<>(pfamAIds)));
            dto.getPfamaAccs().addAll(new ArrayList<>(new LinkedHashSet<>(pfamAaccs)));
            dto.getPfamaGoIds().addAll(new ArrayList<>(new LinkedHashSet<>(pfamAgoIds)));
            dto.getPfamaGoTerms().addAll(new ArrayList<>(new LinkedHashSet<>(pfamAgoTerms)));
            dto.getPfamaGoCats().addAll(new ArrayList<>(new LinkedHashSet<>(pfamAgoCats)));
            dto.getPfamaJsons().addAll(new ArrayList<>(new LinkedHashSet<>(pfamAjsons)));
        }
    }
    
    private void indexAlleles(Map<String, AlleleDTO> alleles) throws SolrServerException, IOException {

        alleleCore.addBeans(alleles.values(), 60000);
    }

    public static void main(String[] args) throws IndexerException {

        AlleleIndexer main = new AlleleIndexer();
        main.initialise(args);
        main.run();
        main.validateBuild();

        logger.info("Process finished.  Exiting.");
    }

}
