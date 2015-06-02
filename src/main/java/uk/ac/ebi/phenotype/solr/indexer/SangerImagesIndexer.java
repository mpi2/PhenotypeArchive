package uk.ac.ebi.phenotype.solr.indexer;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import uk.ac.ebi.phenotype.bean.GenomicFeatureBean;
import uk.ac.ebi.phenotype.service.dto.AlleleDTO;
import uk.ac.ebi.phenotype.service.dto.SangerImageDTO;
import uk.ac.ebi.phenotype.solr.indexer.exceptions.IndexerException;
import uk.ac.ebi.phenotype.solr.indexer.exceptions.ValidationException;
import uk.ac.ebi.phenotype.solr.indexer.utils.IndexerMap;
import uk.ac.ebi.phenotype.solr.indexer.utils.SangerProcedureMapper;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Populate the experiment core
 */
public class SangerImagesIndexer extends AbstractIndexer {

    private static final Logger logger = LoggerFactory.getLogger(SangerImagesIndexer.class);
    private static Connection komp2DbConnection;
    private static Connection ontoDbConnection;

    @Autowired
    @Qualifier("komp2DataSource")
    DataSource komp2DbDataSource;

    @Autowired
    @Qualifier("ontodbDataSource")
    DataSource ontodbDataSource;
    
    @Autowired
    @Qualifier("solrServer")
    SolrServer phenodigmServer;

    @Autowired
    @Qualifier("alleleIndexing")
    SolrServer alleleCore;

    @Autowired
    @Qualifier("sangerImagesIndexing")
    SolrServer sangerImagesCore;

    private Map<Integer, DcfBean> dcfMap = new HashMap<>();
    private Map<String, Map<String, String>> translateCategoryNames = new HashMap<>();
    private Map<Integer, MouseBean> mouseMvMap = new HashMap<>();
    private Map<String, AlleleBean> alleleMpiMap = new HashMap<>();
    private Map<String, List<String>> synonyms = new HashMap<>();
    private Map<String, GenomicFeatureBean> featuresMap = new HashMap<>();
    private Map<Integer, ExperimentDict> expMap = new HashMap<>();
    private Map<Integer, List<Tag>> tags = new HashMap<>();
    private Map<Integer, List<Annotation>> annotationsMap = new HashMap<>();
    private Map<String, String> uptoDateMaMap = new HashMap<>();
    private Map<String, Integer> maTermToNodeMap = new HashMap<>();
    private Map<String, List<String>> termIdToMaSynonyms = new HashMap<>();
    private Map<String, String> subtypeMap = new HashMap<>();

    private Map<String, List<String>> mpSynMap = new HashMap<>();
    private Map<String, Set<Integer>> maNode2TermMap = new HashMap<>();
    private Map<Integer, TopLevelBean> maNodeToTopLevel = new HashMap<>();
    private Map<String, TopLevelBean> mpNode2termTopLevel = new HashMap<>();
    private Map<Integer, TopLevelBean> nodeIdToMpTermInfo = new HashMap<>();
    private Map<String, List<AlleleDTO>> alleles;
    Map<String, List<Map<String, String>>> mpToHpMap;

    HashMap<Integer, String> nodeToMp = new HashMap();// used
    // for
    // multiple
    // MP
    // parents
    // for
    // mp to
    // hp
    // mappings

    public SangerImagesIndexer() {

    }

    @Override
    public void validateBuild() throws IndexerException {
        Long numFound = getDocumentCount(sangerImagesCore);
        
        if (numFound <= MINIMUM_DOCUMENT_COUNT)
            throw new IndexerException(new ValidationException("Actual images document count is " + numFound + "."));
        
        if (numFound != documentCount)
            logger.warn("WARNING: Added " + documentCount + " images documents but SOLR reports " + numFound + " documents.");
        else
            logger.info("validateBuild(): Indexed " + documentCount + " images documents.");
    }

    @Override
    public void initialise(String[] args) throws IndexerException {

        super.initialise(args);

        try {

            komp2DbConnection = komp2DbDataSource.getConnection();
            ontoDbConnection = ontodbDataSource.getConnection();

            populateMAs();
            try {
                populateDcfMap();
                populateMouseMv();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                throw new IndexerException(e);
            }
            populateAlleleMpi();
            populateSynonyms();
            populateGenomicFeature2();
            populateExperiments();
            populateTAGS();
            populateAnnotations();
            populateSubType();
            populateMpSynonyms();
            populateMaNodeToTerms();
            populateMaNodeToTopLevel();
            populateMaSynonyms();
            populateMpNode2TopLevelTerms();
            populateMpTermInfo();
            populateAlleles();
            populateMpToHpTermsMap();

        } catch (SQLException e) {
            throw new IndexerException(e);
        }

        printConfiguration();
    }

    public static void main(String[] args) throws IndexerException {

        SangerImagesIndexer main = new SangerImagesIndexer();
        main.initialise(args);
        main.run();
        main.validateBuild();

        logger.info("Process finished.  Exiting.");
    }

    @Override
    protected Logger getLogger() {

        return logger;
    }

    @Override
    public void run() throws IndexerException {

        logger.info("run method started");

        Long start = System.currentTimeMillis();
        try {
            populateSangerImagesCore();
        } catch (Exception e) {
            e.printStackTrace();
            throw new IndexerException(e);
        }
        
        logger.info("Populating experiment solr core - done [took: {}s]", (System.currentTimeMillis() - start) / 1000.0);
    }

    public void populateSangerImagesCore()
            throws SQLException, IOException, SolrServerException {

        int count = 0;

        sangerImagesCore.deleteByQuery("*:*");

		// <entity dataSource="komp2ds" name="ima_image_record"
        //
        // <field column="id" name="id" />
        // <field column="dataType" name="dataType"/>
        // <field column="FULL_RESOLUTION_FILE_PATH"
        // name="fullResolutionFilePath" />
        // <field column="LARGE_THUMBNAIL_FILE_PATH"
        // name="largeThumbnailFilePath" />
        // <field column="ORIGINAL_FILE_NAME" name="originalFileName" />
        // <field column="SMALL_THUMBNAIL_FILE_PATH"
        // name="smallThumbnailFilePath" />
        // <field column="institute" name="institute" />
        //
        // <entity dataSource="komp2ds" name="imaDcfImageView"
        // query="SELECT DCF_ID, NAME, PROCEDURE_ID, EXPERIMENT_ID, MOUSE_ID FROM `IMA_DCF_IMAGE_VW` dcf, IMA_IMAGE_RECORD ir, PHN_STD_OPERATING_PROCEDURE stdOp WHERE dcf.id=ir.id and dcf.dcf_id=stdOp.id and ir.id=${ima_image_record.ID}">
        // <field column="DCF_ID" name="dcfId" />
        // <field column="EXPERIMENT_ID" name="dcfExpId" />
        // <field column="NAME" name="sangerProcedureName" />
        // <field column="PROCEDURE_ID" name="sangerProcedureId" />
        // </entity>
        String query = "SELECT 'images' as dataType, IMA_IMAGE_RECORD.ID, FOREIGN_TABLE_NAME, FOREIGN_KEY_ID, ORIGINAL_FILE_NAME, CREATOR_ID, CREATED_DATE, EDITED_BY, EDIT_DATE, CHECK_NUMBER, FULL_RESOLUTION_FILE_PATH, SMALL_THUMBNAIL_FILE_PATH, LARGE_THUMBNAIL_FILE_PATH, SUBCONTEXT_ID, QC_STATUS_ID, PUBLISHED_STATUS_ID, o.name as institute, IMA_EXPERIMENT_DICT.ID as experiment_dict_id FROM IMA_IMAGE_RECORD, IMA_SUBCONTEXT, IMA_EXPERIMENT_DICT, organisation o  WHERE IMA_IMAGE_RECORD.organisation=o.id AND IMA_IMAGE_RECORD.subcontext_id=IMA_SUBCONTEXT.id AND IMA_SUBCONTEXT.experiment_dict_id=IMA_EXPERIMENT_DICT.id AND IMA_EXPERIMENT_DICT.name!='Mouse Necropsy' ";// and
        // IMA_IMAGE_RECORD.ID=70220

        try (PreparedStatement p = komp2DbConnection.prepareStatement(query, java.sql.ResultSet.TYPE_FORWARD_ONLY, java.sql.ResultSet.CONCUR_READ_ONLY)) {

            p.setFetchSize(Integer.MIN_VALUE);

            ResultSet r = p.executeQuery();
            while (r.next()) {
                // System.out.println(r.getInt("IMA_IMAGE_RECORD.ID"));
                SangerImageDTO o = new SangerImageDTO();
                int imageRecordId = r.getInt("IMA_IMAGE_RECORD.ID");
                o.setId(String.valueOf(imageRecordId));
                o.setDataType(r.getString("dataType"));
                o.setFullResolutionFilePath(r.getString("FULL_RESOLUTION_FILE_PATH"));
                o.setLargeThumbnailFilePath(r.getString("LARGE_THUMBNAIL_FILE_PATH"));
                o.setOriginalFileName(r.getString("ORIGINAL_FILE_NAME"));
                o.setSmallThumbnailFilePath(r.getString("SMALL_THUMBNAIL_FILE_PATH"));
                o.setInstitute(r.getString("institute"));
                DcfBean dcfInfo = dcfMap.get(imageRecordId);
                if (dcfInfo != null) {
                    // System.out.println("dcfInfo="+dcfInfo);
                    o.setDcfId(dcfInfo.dcfId);
                    o.setDcfExpId(dcfInfo.dcfExpId);
                    o.setSangerProcedureName(dcfInfo.sangerProcedureName);
                    o.setExpNameExp(Arrays.asList(dcfInfo.sangerProcedureName + "_exp"));
                    o.setSangerProcedureId(dcfInfo.sangerProcedureId);
                }
                MouseBean mb = mouseMvMap.get(r.getInt("FOREIGN_KEY_ID"));
                if (mb != null) {
                    // System.out.println("adding mouse=" + mb);
                    o.setAgeInWeeks(mb.ageInWeeks);
                    o.setGenotypeString(mb.genotypeString);
                    o.setGenotype(mb.genotype);// genotype is WT or HET not like
                    // genotypeString which is like
                    // allele....
                    AlleleBean alBean = alleleMpiMap.get(mb.genotypeString);
                    o.setMouseId(mb.mouseId);
                    o.setSex(mb.sex);
                    o.setColonyId(mb.colonyId);
                    if (alBean != null) {
                        o.setAllele_accession(alBean.allele_accession);
                        o.setSangerSymbol(Arrays.asList(alBean.sangerSymbol));
                        if (featuresMap.containsKey(alBean.gf_acc)) {
                            GenomicFeatureBean feature = featuresMap.get(alBean.gf_acc);
                            o.setSymbol(Arrays.asList(feature.getSymbol()));
							// <entity dataSource="komp2ds" name="subtype2"
                            // query="select  name,  concat('${genomic_feature2.symbol}_', '${genomic_feature2.acc}') as symbol_gene from `ontology_term` where acc='${genomic_feature2.subtype_acc}' and db_id=${genomic_feature2.subtype_db_id}">
                            String symbolGene = feature.getSymbol() + "_" + feature.getAccession();
                            o.setAccession(feature.getAccession());
                            List<String> symbolGeneList = new ArrayList<>();
                            symbolGeneList.add(symbolGene);
                            o.setSymbolGene(symbolGeneList);
                            String subtypeKey = feature.getSubtypeAccession() + "_" + feature.getSubtypeDbId();
							// System.out.println("checking for subyte with key="
                            // + subtypeKey);
                            if (subtypeMap.containsKey(subtypeKey)) {
                                o.setSubtype(Arrays.asList(subtypeMap.get(subtypeKey)));
                            }
							// System.out.println("setting symbol in main method via feature="
                            // + feature.getSymbol());
                            o.setGeneName(Arrays.asList(feature.getName()));
                            this.populateImageDtoStatuses(o, feature.getAccession());

                            if (synonyms.containsKey(feature.getAccession())) {
                                List<String> syns = synonyms.get(feature.getAccession());
                                o.setSynonyms(syns);
                                o.setMarkerSynonym(syns);
								// for(String syn:syns){
                                // System.out.println("syn="+syn);
                                //
                                // }
                            }
                        }
                        // o.setSubType(alBean.subType);
                    }
                }
                if (expMap.containsKey(new Integer(r.getInt("ID")))) {
                    ExperimentDict expBean = expMap.get(r.getInt("ID"));
                    o.setExpName(Arrays.asList(expBean.name));
                    o.setSangerProcedureName(expBean.name);
                    List<String> procedureList = new ArrayList<String>();
                    procedureList.add(SangerProcedureMapper.getImpcProcedureFromSanger(expBean.name));
                    o.setProcedureName(procedureList);
                    // o.setExperimentName(name)
                }
                if (tags.containsKey(imageRecordId)) {
                    List<Tag> annotationList = tags.get(imageRecordId);
                    List<String> tagNames = new ArrayList<>();
                    List<String> tagValues = new ArrayList<>();
                    for (Tag tag : annotationList) {
                        tagNames.add(tag.tagName);
                        tagValues.add(tag.tagValue);
                        if (annotationsMap.containsKey(tag.tagId)) {
                            List<Annotation> annotations = annotationsMap.get(tag.tagId);
							// System.out.println("annotations size=" +
                            // annotations.size());
                            List<String> annotationTermIds = new ArrayList<>();
                            List<String> annotationTermNames = new ArrayList<>();
                            List<String> ma_ids = new ArrayList<>();
                            List<String> ma_terms = new ArrayList<>();
                            List<String> mp_ids = new ArrayList<>();
                            List<String> mp_terms = new ArrayList<>();

                            ArrayList<String> maTopLevelTermIds = new ArrayList<>();
                            ArrayList<String> maTopLevelTerms = new ArrayList<>();
                            ArrayList<String> ma_term_synonyms = new ArrayList<>();
                            ArrayList<String> selected_top_level_ma_term_synonym = new ArrayList<>();
                            ArrayList<String> annotatedHigherLevelMpTermId = new ArrayList<>();
                            ArrayList<String> annotatedHigherLevelMpTermName = new ArrayList<>();
                            List<String> topLevelMpTermSynonym = new ArrayList<>();
                            for (Annotation annotation : annotations) {
                                annotationTermIds.add(annotation.annotationTermId);
                                //if ma term
                                
                                //if mp term
                                
                                
                                if (annotation.ma_id != null) {
                                	annotationTermNames.add(uptoDateMaMap.get(annotation.annotationTermId));
                                    ma_ids.add(annotation.ma_id);
                                    ma_terms.add(annotation.ma_term);

									// ArrayList<String> maTopLevelSynonyms=new
                                    // ArrayList<>();
                                    if (maNode2TermMap.containsKey(annotation.ma_id)) {
                                        for (Integer nodeId : maNode2TermMap.get(annotation.ma_id)) {

                                            if (maNodeToTopLevel.containsKey(nodeId)) {
                                                TopLevelBean maTopLevelBean = maNodeToTopLevel.get(nodeId);
                                                maTopLevelTermIds.add(maTopLevelBean.termId);
                                                maTopLevelTerms.add(maTopLevelBean.termName);
                                                if (termIdToMaSynonyms.containsKey(nodeId)) {
                                                    // ma_term_synonym
                                                    ma_term_synonyms.addAll(termIdToMaSynonyms.get(nodeId));
                                                }
                                            }
											// <field column="term_id"
                                            // name="selected_top_level_ma_id"
                                            // />
                                            // <field column="name"
                                            // name="selected_top_level_ma_term"
                                            // />

                                        }
                                        // maTopLevelSynonyms.addAll(lookupMaSynonyms(annotation.ma_term));
                                    }
                                }
                                if (annotation.mp_id != null) {
                                	annotationTermNames.add(annotation.mp_term);
                                    mp_ids.add(annotation.mp_id);
                                    mp_terms.add(annotation.mp_term);

                                    if (mpToHpMap.containsKey(annotation.mp_id)) {

                                        List<Map<String, String>> hpMap = mpToHpMap.get(annotation.mp_id);
                                        List<String> hpIds = new ArrayList<>();
                                        List<String> hpTerms = new ArrayList<>();

                                        for (Map<String, String> map : hpMap) {
                                            String hpId = map.get("hp_id");
                                            String hpTerm = map.get("hp_term");
                                            if (hpId != null) {
                                                hpIds.add(hpId);
                                            }
                                            if (hpTerm != null) {
                                                hpTerms.add(hpTerm);
                                            }
                                        }

                                        if (hpIds != null &&  ! hpIds.isEmpty()) {
                                            o.setHpId(hpIds);
                                        }
                                        if (hpTerms != null &&  ! hpTerms.isEmpty()) {
                                            o.setHpTerm(hpTerms);
                                        }
                                    }

                                    // need to get top level stuff here
                                    if (mpNode2termTopLevel.containsKey(annotation.mp_id)) {
                                        TopLevelBean topLevelBean = mpNode2termTopLevel.get(annotation.mp_id);
										// System.out.println("TopLevel=" +
                                        // topLevelBean.termId);
                                        if (nodeIdToMpTermInfo.containsKey(topLevelBean.topLevelNodeId)) {
                                            TopLevelBean realTopLevel = nodeIdToMpTermInfo.get(topLevelBean.topLevelNodeId);
											// System.out.println("realTopLevel="
                                            // +
                                            // realTopLevel.termId+" name="+realTopLevel.termName);
                                            // <field column="name"
                                            // name="annotatedHigherLevelMpTermName"
                                            // />
                                            // <field column="mpTerm"
                                            // name="annotatedHigherLevelMpTermId"
                                            // />
                                            annotatedHigherLevelMpTermId.add(realTopLevel.termId);
                                            annotatedHigherLevelMpTermName.add(realTopLevel.termName);
                                            if (mpSynMap.containsKey(realTopLevel.termId)) {
                                                List<String> topLevelSynonyms = mpSynMap.get(realTopLevel.termId);
                                                topLevelMpTermSynonym.addAll(topLevelSynonyms);
                                            }
                                        }
                                    } else {
                                        logger.info("No top level for " + annotation.mp_id);
                                    }
                                    if (mpSynMap.containsKey(annotation.mp_id)) {
                                        o.setMpSyns(mpSynMap.get(annotation.mp_id));
                                    }
                                }

                            }
                            o.setTopLevelMpTermSynonym(topLevelMpTermSynonym);
                            o.setAnnotatedHigherLevelMpTermId(annotatedHigherLevelMpTermId);
                            o.setAnnotatedHigherLevelMpTermName(annotatedHigherLevelMpTermName);
                            o.setAnnotationTermId(annotationTermIds);
                            o.setAnnotationTermName(annotationTermNames);
                            o.setMaTopLevelTermIds(maTopLevelTermIds);
                            o.setMaTopLevelTerms(maTopLevelTerms);
                            o.setSelectedTopLevelMaTermSynonym(selected_top_level_ma_term_synonym);
							// System.out.println("ma_ids=" + ma_ids);
                            // System.out.println("ma_terms=" + ma_terms);
                            o.setMaId(ma_ids);
                            o.setMaTerm(ma_terms);
                            o.setMaTermName(ma_terms);

                            o.setMpId(mp_ids);
                            o.setMpTerm(mp_terms);
                            o.setMp_id(mp_ids);
                            o.setMpTermName(mp_terms);
                            for (String maId : ma_ids) {
								// get the top level and child terms
                                // if (maChildMap.containsKey(maId)) {
                                // List<OntologyTermBean> childMas =
                                // maChildMap.get(maId);
                                // for (OntologyTermBean childMa : childMas) {
                                // //System.out.println("child="+childMa.getId());
                                // }
                                // }
                                // if (maParentMap.containsKey(maId)) {
                                // List<OntologyTermBean> parentMas =
                                // maParentMap.get(maId);
                                // for (OntologyTermBean parentMa : parentMas) {
                                // //System.out.println("parent="+parentMa.getId());
                                // }
                                // }

                                if (maTermToNodeMap.containsKey(maId)) {
                                    int nodeId = maTermToNodeMap.get(maId);
                                    // System.out.println("nodeId=" + nodeId);
                                    if (termIdToMaSynonyms.containsKey(nodeId)) {
                                        List<String> maSyns = termIdToMaSynonyms.get(nodeId);
                                        // System.out.println("setting ma synonyms="+maSyns);
                                        o.setMaTermSynonym(maSyns);
                                    }
                                }
                            }
                            // ma

                        }

                    }
                    o.setTagNames(tagNames);
                    o.setTagValues(tagValues);

                }

    // xxxxxxxxxxxxx0 seconds between commits
                documentCount++;
                sangerImagesCore.addBean(o, 10000);

                count ++;

                if (count % 10000 == 0) {
                    logger.info(" added " + count + " beans");
                }

            }

            // Final commit to save the rest of the docs
            sangerImagesCore.commit();

        } catch (Exception e) {
            logger.error("Big error {}", e.getMessage(), e);
        }

    }

    public void populateMaSynonyms() {

		// <entity dataSource="ontods" name="ma_term_2Syn"
        // onError="continue"
        // processor="CachedSqlEntityProcessor"
        // query="select * from ma_synonyms"
        // where="term_id=maAnnotations.TERM_ID">
        //
        // <field column="syn_name" name="ma_term_synonym" />
        //
        // </entity>
        logger.info("populating MA synonyms");
		// use annotationTermName from ontodb not from Sanger image
        // annotation(risk of out of date)
        String query = "select * from ma_synonyms";

        try (PreparedStatement p = ontoDbConnection.prepareStatement(query)) {

            ResultSet resultSet = p.executeQuery();

            while (resultSet.next()) {

                String termId = resultSet.getString("term_id");
                String synName = resultSet.getString("syn_name");
                if (termIdToMaSynonyms.containsKey(termId)) {
                    List<String> maSynonyms = termIdToMaSynonyms.get(termId);
                    maSynonyms.add(synName);
                } else {
                    List<String> maSynonyms = new ArrayList<>();
                    maSynonyms.add(synName);
                    termIdToMaSynonyms.put(termId, maSynonyms);
                }
                // termToNodeMap.put(termId, nodeId);

            }
            logger.info("termIdToMaSynonyms size=" + termIdToMaSynonyms.size());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Add all the relevant data to the Impress map
     *
     * @throws SQLException when a database exception occurs
     */
    public void populateDcfMap()
            throws SQLException {

        List<String> queries = new ArrayList<>();
        queries.add("SELECT ir.id as id, DCF_ID, NAME, PROCEDURE_ID, EXPERIMENT_ID, MOUSE_ID FROM `IMA_DCF_IMAGE_VW` dcf, IMA_IMAGE_RECORD ir, PHN_STD_OPERATING_PROCEDURE stdOp WHERE dcf.id=ir.id and dcf.dcf_id=stdOp.id");// joins
        // on
        for (String query : queries) {

            try (PreparedStatement p = komp2DbConnection.prepareStatement(query)) {

                ResultSet resultSet = p.executeQuery();

                while (resultSet.next()) {

                    DcfBean b = new DcfBean();
                    /*
                     * <field column="DCF_ID" name="dcfId" /> <field
                     * column="EXPERIMENT_ID" name="dcfExpId" /> <field
                     * column="NAME" name="sangerProcedureName" /> <field
                     * column="PROCEDURE_ID" name="sangerProcedureId" />
                     */

                    b.dcfId = resultSet.getString("DCF_ID");
                    b.dcfExpId = resultSet.getString("EXPERIMENT_ID");
                    b.sangerProcedureName = resultSet.getString("NAME");
                    b.sangerProcedureId = resultSet.getInt("PROCEDURE_ID");
                    // System.out.println("adding dcf id=" + b);
                    dcfMap.put(resultSet.getInt("id"), b);

                }

            }
        }
    }

    public void populateMAs() {

        logger.info("populating MAs");
        String query = "select ma_term_infos.term_id, ma_term_infos.name, ma_node2term.node_id, ma_node2term.term_id from ma_term_infos, ma_node2term where ma_term_infos.term_id=ma_node2term.term_id";

        try (PreparedStatement p = ontoDbConnection.prepareStatement(query)) {

            ResultSet resultSet = p.executeQuery();

            while (resultSet.next()) {
                String termId = resultSet.getString("term_id");
                String termName = resultSet.getString("name");
                int nodeId = resultSet.getInt("node_id");
				// System.out.println("adding term to node=" + termId + " " +
                // nodeId);
                maTermToNodeMap.put(termId, nodeId);
                uptoDateMaMap.put(termId, termName);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void populateMouseMv()
            throws SQLException {

		// select * from IMPC_MOUSE_ALLELE_MV where
        // MOUSE_ID=${ima_image_record.FOREIGN_KEY_ID}
        logger.info("populating MouseMv");
        String query = "select MOUSE_ID, AGE_IN_WEEKS, ALLELE, GENOTYPE, GENDER, COLONY_ID from IMPC_MOUSE_ALLELE_MV";// where
        // MOUSE_ID=${ima_image_record.FOREIGN_KEY_ID}");//
        // image
        // record.foreignkeyid
        // to
        // mouse_id
        // on

        try (PreparedStatement p = komp2DbConnection.prepareStatement(query)) {

            ResultSet resultSet = p.executeQuery();

            while (resultSet.next()) {

                MouseBean b = new MouseBean();
                /*
                 * <field column="AGE_IN_WEEKS" name="ageInWeeks" /> <field
                 * column="ALLELE" name="genotypeString" />
                 */

                b.mouseId = resultSet.getInt("MOUSE_ID");
                b.ageInWeeks = resultSet.getString("AGE_IN_WEEKS");
                b.genotypeString = resultSet.getString("ALLELE");
                b.genotype = resultSet.getString("GENOTYPE");
                b.sex = resultSet.getString("gender");
                b.colonyId = resultSet.getInt("COLONY_ID");
                // System.out.println("adding mouse id="+b);
                mouseMvMap.put(b.mouseId, b);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void populateAlleleMpi() {

		// select * from IMPC_MOUSE_ALLELE_MV where
        // MOUSE_ID=${ima_image_record.FOREIGN_KEY_ID}
        logger.info("populating alleleMpi");
        String query = "select * from `allele`";// where
        // MOUSE_ID=${ima_image_record.FOREIGN_KEY_ID}");//
        // image record.foreignkeyid to
        // mouse_id
        // on

        try (PreparedStatement p = komp2DbConnection.prepareStatement(query)) {

            ResultSet resultSet = p.executeQuery();

            while (resultSet.next()) {

                AlleleBean b = new AlleleBean();

				// <field column="symbol" name="sangerSymbol" />
                // <field column="acc" name="allele_accession" />
                b.gf_acc = resultSet.getString("gf_acc");
                b.sangerSymbol = resultSet.getString("symbol");
                b.allele_accession = resultSet.getString("acc");
                alleleMpiMap.put(b.sangerSymbol, b);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void populateGenomicFeature2() {

        logger.info("populating genomicFeature2");
		// <entity dataSource="komp2ds" name="genomic_feature2"
        // query="select * from `genomic_feature` where acc='${alleleMpi.gf_acc}' and db_id=${alleleMpi.gf_db_id}">
        // <field column="symbol" name="symbol" />
        // <field column="acc" name="accession" />
        // <field column="name" name="geneName" />
        String query = "select * from `genomic_feature";// where
        // MOUSE_ID=${ima_image_record.FOREIGN_KEY_ID}");//
        // image record.foreignkeyid to
        // mouse_id
        // on

        try (PreparedStatement p = komp2DbConnection.prepareStatement(query)) {
            ResultSet resultSet = p.executeQuery();

            while (resultSet.next()) {
                GenomicFeatureBean gf = new GenomicFeatureBean();
                gf.setSymbol(resultSet.getString("symbol"));
                gf.setAccession(resultSet.getString("acc"));
                gf.setName(resultSet.getString("name"));
                gf.setSubtypeAccession(resultSet.getString("subtype_acc"));
                gf.setSubtypeDbId(resultSet.getString("subtype_db_id"));
                featuresMap.put(resultSet.getString("acc"), gf);
                // System.out.println("gene name="+b.geneName);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void populateExperiments() {

		// select IMA_EXPERIMENT_DICT.NAME, IMA_EXPERIMENT_DICT.DESCRIPTION,
        // concat(IMA_EXPERIMENT_DICT.NAME,'_exp') as expName_exp FROM
        // IMA_EXPERIMENT_DICT, IMA_SUBCONTEXT, IMA_IMAGE_RECORD where
        // IMA_SUBCONTEXT.ID=IMA_IMAGE_RECORD.SUBCONTEXT_ID and
        // IMA_EXPERIMENT_DICT.ID=IMA_SUBCONTEXT.EXPERIMENT_DICT_ID;# AND
        // IMA_IMAGE_RECORD.ID=${ima_image_record.ID}
        logger.info("populating experiments");
		// <entity dataSource="komp2ds" name="genomic_feature2"
        // query="select * from `genomic_feature` where acc='${alleleMpi.gf_acc}' and db_id=${alleleMpi.gf_db_id}">
        // <field column="symbol" name="symbol" />
        // <field column="acc" name="accession" />
        // <field column="name" name="geneName" />
        String query = "select IMA_IMAGE_RECORD.ID, IMA_EXPERIMENT_DICT.NAME, IMA_EXPERIMENT_DICT.DESCRIPTION, concat(IMA_EXPERIMENT_DICT.NAME,'_exp') as expName_exp FROM IMA_EXPERIMENT_DICT, IMA_SUBCONTEXT, IMA_IMAGE_RECORD where IMA_SUBCONTEXT.ID=IMA_IMAGE_RECORD.SUBCONTEXT_ID and IMA_EXPERIMENT_DICT.ID=IMA_SUBCONTEXT.EXPERIMENT_DICT_ID";// where
        // MOUSE_ID=${ima_image_record.FOREIGN_KEY_ID}");//
        // image record.foreignkeyid to
        // mouse_id
        // on

        try (PreparedStatement p = komp2DbConnection.prepareStatement(query)) {
            ResultSet resultSet = p.executeQuery();

            while (resultSet.next()) {
                ExperimentDict exp = new ExperimentDict();
                exp.name = resultSet.getString("NAME");
                exp.description = resultSet.getString("DESCRIPTION");
                expMap.put(resultSet.getInt("ID"), exp);
                // System.out.println("gene name="+b.geneName);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class ExperimentDict {

        String name;
        String description;

    }

    protected void populateSynonyms() {

        // select * from synonym
        logger.info("populating synonyms");
		// <entity dataSource="komp2ds" name="genomic_feature2"
        // query="select * from `genomic_feature` where acc='${alleleMpi.gf_acc}' and db_id=${alleleMpi.gf_db_id}">
        // <field column="symbol" name="symbol" />
        // <field column="acc" name="accession" />
        // <field column="name" name="geneName" />
        String query = "select * from synonym";// where
        // MOUSE_ID=${ima_image_record.FOREIGN_KEY_ID}");//
        // image record.foreignkeyid to
        // mouse_id
        // on

        try (PreparedStatement p = komp2DbConnection.prepareStatement(query)) {

            ResultSet resultSet = p.executeQuery();

            while (resultSet.next()) {
                String accession = resultSet.getString("acc");
                String symb = resultSet.getString("symbol");
                if (synonyms.containsKey(accession)) {
                    List<String> list = synonyms.get(accession);
                    list.add(symb);
                } else {
                    List<String> synList = new ArrayList<>();
                    synList.add(symb);
                    synonyms.put(accession, synList);
                }

            }
            logger.info("synonyms size=" + synonyms.size());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void populateTAGS() {

        // select * from IMA_IMAGE_TAG
        logger.info("populating TAGS");
		// <entity dataSource="komp2ds" name="genomic_feature2"
        // query="select * from `genomic_feature` where acc='${alleleMpi.gf_acc}' and db_id=${alleleMpi.gf_db_id}">
        // <field column="symbol" name="symbol" />
        // <field column="acc" name="accession" />
        // <field column="name" name="geneName" />
        String query = "select * from IMA_IMAGE_TAG";// where
        // MOUSE_ID=${ima_image_record.FOREIGN_KEY_ID}");//
        // image record.foreignkeyid to
        // mouse_id
        // on

        try (PreparedStatement p = komp2DbConnection.prepareStatement(query)) {

            ResultSet resultSet = p.executeQuery();
			// <field column="TAG_NAME" name="tagName" />
            // <field column="TAG_VALUE" name="tagValue" />
            while (resultSet.next()) {
                int irId = resultSet.getInt("IMAGE_RECORD_ID");

                Tag tag = new Tag();
                tag.tagId = resultSet.getInt("ID");
                tag.tagName = resultSet.getString("TAG_NAME");
                tag.tagValue = resultSet.getString("TAG_VALUE");
                if (tags.containsKey(irId)) {
                    List<Tag> list = tags.get(irId);

                    list.add(tag);
                } else {
                    List<Tag> list = new ArrayList<>();
                    list.add(tag);
                    tags.put(irId, list);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    protected void populateAnnotations() {

		// <entity dataSource="komp2ds" name="maAnnotations"
        // query="select * from ANN_ANNOTATION where FOREIGN_KEY_ID=${tag.ID} and TERM_ID like 'MA%'">
        //
        // <field column="TERM_ID" name="annotationTermId" />
        // <field column="TERM_ID" name="maTermId" />
        // <field column="TERM_NAME" name="ma_term" />
        // <field column="TERM_ID" name="ma_id" />
        logger.info("populating Annotations");
        String query = "select * from ANN_ANNOTATION";// where TERM_ID like
        // 'MA%'";// where
        // FOREIGN_KEY_ID=${tag.ID}
        // and TERM_ID like
        // 'MA%'";// where

        try (PreparedStatement p = komp2DbConnection.prepareStatement(query)) {
            ResultSet resultSet = p.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("FOREIGN_KEY_ID");

                Annotation ann = new Annotation();
                String annotationTermId = resultSet.getString("TERM_ID");
                String annotationTermName = resultSet.getString("TERM_NAME");
                ann.annotationTermId = annotationTermId;
                if (annotationTermId.startsWith("MA:")) {
                    ann.ma_id = annotationTermId;
                    ann.ma_term = annotationTermName;
                }
                if (annotationTermId.startsWith("MP:")) {
                    ann.mp_id = annotationTermId;
                    ann.mp_term = annotationTermName;
                }

				// if(annotationTermId.contains("MA:")){
                // ann.mpTermId = annotationTermId;
                // ann.ma_term=annotationTermName;
                // }
                if (annotationsMap.containsKey(id)) {
                    List<Annotation> list = annotationsMap.get(id);

                    list.add(ann);
                } else {
                    List<Annotation> list = new ArrayList<>();
                    list.add(ann);
                    annotationsMap.put(id, list);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    protected class Annotation {

        String annotationTermId;
        String annotationTermName;
        String ma_term;
        String ma_id;
        String mp_term;
        String mp_id;
    }

    protected class Tag {

        public int tagId;
        public String tagValue;
        public String tagName;

    }

    protected void populateSubType() {

        logger.info("populating subtype");
		// <entity dataSource="komp2ds" name="notnull"
        // query="select * from `genomic_feature` where acc='${alleleMpi.gf_acc}' and db_id=${alleleMpi.gf_db_id}">
        // <entity dataSource="komp2ds" name="subtype2"
        // query="select  name,  concat('${genomic_feature2.symbol}_', '${genomic_feature2.acc}') as symbol_gene from `ontology_term` where acc='${genomic_feature2.subtype_acc}' and db_id=${genomic_feature2.subtype_db_id}">
        // <field column="name" name="subtype" />
        String query = "select  * from ontology_term";
		// "select  name,  concat(?, ?) as symbol_gene from `ontology_term` where acc=? and db_id=?";//
        // where
        // CREATE TABLE `ontology_term` (
        // `acc` varchar(20) NOT NULL,
        // `db_id` int(10) NOT NULL,
        // `name` text NOT NULL,
        // `description` text,
        // `is_obsolete` tinyint(1) DEFAULT '0',
        // PRIMARY KEY (`acc`,`db_id`)

        try (PreparedStatement p = komp2DbConnection.prepareStatement(query)) {
            ResultSet resultSet = p.executeQuery();

            while (resultSet.next()) {

                String subtype = resultSet.getString("name");
                String acc = resultSet.getString("acc");
                int db_id = resultSet.getInt("db_id");
                String key = acc + "_" + db_id;
                // System.out.println("setting subtype id="+key);
                subtypeMap.put(key, subtype);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    protected class AlleleBean {

		// <field column="symbol" name="sangerSymbol" />
        // <field column="acc" name="allele_accession" />
        String gf_acc;
        String sangerSymbol;
        String allele_accession;
        String subType;

    }

    public Map<String, Map<String, String>> getTranslateCategoryNames() {

        return translateCategoryNames;
    }

    /**
     * Internal class to act as Map value DTO for impress data
     */
    protected class DcfBean {

        /*
         * <field column="DCF_ID" name="dcfId" /> <field column="EXPERIMENT_ID"
         * name="dcfExpId" /> <field column="NAME" name="sangerProcedureName" />
         * <field column="PROCEDURE_ID" name="sangerProcedureId" />
         */
        public String dcfId;
        public String dcfExpId;
        public String sangerProcedureName;
        public Integer sangerProcedureId;

        @Override
        public String toString() {

            return "dcf=" + dcfId + " " + dcfExpId + " " + sangerProcedureName + " " + sangerProcedureId;
        }

    }

    protected class MouseBean {

        public int colonyId;
        public String sex;
        public String genotype;
        /*
         * <field column="DCF_ID" name="dcfId" /> <field column="EXPERIMENT_ID"
         * name="dcfExpId" /> <field column="NAME" name="sangerProcedureName" />
         * <field column="PROCEDURE_ID" name="sangerProcedureId" />
         */
        public Integer mouseId;
        public String ageInWeeks;
        public String genotypeString;

        @Override
        public String toString() {

            return "mouseId=" + mouseId + " " + ageInWeeks + " " + genotypeString;
        }

    }

    public void populateMpSynonyms() {

        logger.info("populating MP synonyms");
        // <field column="syn_name" name="mp_term_synonym" />
        String query = "select * from mp_synonyms";

        try (PreparedStatement p = ontoDbConnection.prepareStatement(query)) {
            ResultSet resultSet = p.executeQuery();

            while (resultSet.next()) {
                String termId = resultSet.getString("term_id");
                String mp_term_synonym = resultSet.getString("syn_name");
                if (mpSynMap.containsKey(termId)) {
                    List<String> syns = mpSynMap.get(termId);
                    syns.add(mp_term_synonym);
                } else {
                    List<String> syns = new ArrayList<>();
                    syns.add(mp_term_synonym);
                    mpSynMap.put(termId, syns);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void populateMaNodeToTerms() {

        logger.info("populating ma_node2term");
        // <field column="syn_name" name="mp_term_synonym" />
        String query = "select * from ma_node2term";

        try (PreparedStatement p = ontoDbConnection.prepareStatement(query)) {
            ResultSet resultSet = p.executeQuery();

            while (resultSet.next()) {
                Integer nodeId = resultSet.getInt("node_id");
                String termId = resultSet.getString("term_id");
                if (maNode2TermMap.containsKey(termId)) {
                    Set<Integer> nodeIds = maNode2TermMap.get(termId);
                    nodeIds.add(nodeId);
                } else {
                    Set<Integer> nodeIds = new HashSet<>();
                    nodeIds.add(nodeId);
                    maNode2TermMap.put(termId, nodeIds);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void populateMaNodeToTopLevel() {

        logger.info("populating ma_node2topLevel");
        // <field column="syn_name" name="mp_term_synonym" />
        String query = "select distinct m.node_id, ti.term_id, ti.name from ma_node2term nt, ma_node_2_selected_top_level_mapping m, ma_term_infos ti where nt.node_id=m.node_id and m.top_level_term_id=ti.term_id";

        try (PreparedStatement p = ontoDbConnection.prepareStatement(query)) {
            ResultSet resultSet = p.executeQuery();

            while (resultSet.next()) {
                int nodeId = resultSet.getInt("node_id");
                String termId = resultSet.getString("term_id");
                String termName = resultSet.getString("name");
                if ( ! maNodeToTopLevel.containsKey(nodeId)) {
                    maNodeToTopLevel.put(nodeId, new TopLevelBean(nodeId, termId, termName));
					// System.out.println("adding to maNodeToTopLevel" + nodeId
                    // + " " + termId);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private class TopLevelBean {

        Integer nodeId;
        Integer topLevelNodeId;
        String termId;
        String termName;

        public TopLevelBean(int nodeId, String termId, String termName) {

            this.nodeId = nodeId;
            this.termId = termId;
            this.termName = termName;
        }

        public TopLevelBean(int nodeId, String termId, String termName, int topLevelNodeId) {

            this(topLevelNodeId, termId, termName);
            this.topLevelNodeId = topLevelNodeId;
        }
    }

    private void populateMpNode2TopLevelTerms() {

		// SELECT * FROM `mp_node2term` mp, mp_node_top_level tl WHERE
        // mp.node_id=tl.node_id
        logger.info("populating mpNode2termTopLevel");
        // <field column="syn_name" name="mp_term_synonym" />
        String query = "SELECT * FROM `mp_node2term` mp, mp_node_top_level tl WHERE mp.node_id=tl.node_id";

        try (PreparedStatement p = ontoDbConnection.prepareStatement(query)) {
            ResultSet resultSet = p.executeQuery();

            while (resultSet.next()) {
                int nodeId = resultSet.getInt("node_id");
                String termId = resultSet.getString("term_id");
                // String termName = resultSet.getString("name");
                int topLevelNodeId = resultSet.getInt("top_level_node_id");
                if ( ! mpNode2termTopLevel.containsKey(termId)) {
                    mpNode2termTopLevel.put(termId, new TopLevelBean(nodeId, termId, null, topLevelNodeId));
					// System.out.println("adding to mpNode2termTopLevel" +
                    // nodeId + " " + termId);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void populateMpTermInfo() {

		// SELECT mp.node_id, mp.term_id as mpTerm, inf.term_id, name FROM
        // `mp_node2term` mp , `mp_term_infos` inf WHERE inf.term_id=mp.term_id
        logger.info("populating mpTermInfo");
        // <field column="syn_name" name="mp_term_synonym" />
        String query = "SELECT mp.node_id, mp.term_id as mpTerm, inf.term_id, name FROM `mp_node2term` mp , `mp_term_infos` inf WHERE  inf.term_id=mp.term_id";

        try (PreparedStatement p = ontoDbConnection.prepareStatement(query)) {
            ResultSet resultSet = p.executeQuery();

            while (resultSet.next()) {
                int nodeId = resultSet.getInt("node_id");
                String termId = resultSet.getString("term_id");
                String termName = resultSet.getString("name");

                if ( ! nodeIdToMpTermInfo.containsKey(nodeId)) {
                    nodeIdToMpTermInfo.put(nodeId, new TopLevelBean(nodeId, termId, termName));
					// System.out.println("adding to mpNode2termTopLevel" +
                    // nodeId + " " + termId);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // need allele core mappings for status etc
    public void populateAlleles()
            throws IndexerException {

        alleles = IndexerMap.getGeneToAlleles(alleleCore);
    }

    private void populateImageDtoStatuses(SangerImageDTO img, String geneAccession) {

        if (alleles.containsKey(geneAccession)) {
            List<AlleleDTO> localAlleles = alleles.get(geneAccession);
            for (AlleleDTO allele : localAlleles) {
                if (allele.getMgiAccessionId() != null) {
                    img.addMgiAccessionId(allele.getMgiAccessionId());

                }
                if (allele.getMarkerSymbol() != null) {
                    img.addMarkerSymbol(allele.getMarkerSymbol());
                }

				// <field column="marker_symbol"
                // xpath="/response/result/doc/str[@name='marker_symbol']" />
                // <field column="marker_name"
                if (allele.getMarkerName() != null) {
                    img.addMarkerName(allele.getMarkerName());
                }
				// xpath="/response/result/doc/str[@name='marker_name']" />
                // <field column="marker_synonym"
                if (allele.getMarkerSynonym() != null) {
                    img.addMarkerSynonym(allele.getMarkerSynonym());
                }
				// xpath="/response/result/doc/arr[@name='marker_synonym']/str"
                // />
                // <field column="marker_type"
                if (allele.getMarkerType() != null) {
                    img.addMarkerType(allele.getMarkerType());

                }

                // xpath="/response/result/doc/str[@name='marker_type']" />
                if (allele.getHumanGeneSymbol() != null) {
                    img.addHumanGeneSymbol(allele.getHumanGeneSymbol());
                }
				// <field column="human_gene_symbol"
                // xpath="/response/result/doc/arr[@name='human_gene_symbol']/str"
                // />
                //
                if (allele.getStatus() != null) {

                    img.addStatus(allele.getStatus());
                }
				// <!-- latest project status (ES cells/mice production status)
                // -->
                // <field column="status"
                // xpath="/response/result/doc/str[@name='status']"
                // />
                //
                if (allele.getImitsPhenotypeStarted() != null) {
                    img.addImitsPhenotypeStarted(allele.getImitsPhenotypeStarted());
                }
				// <!-- latest mice phenotyping status for faceting -->
                // <field column="imits_phenotype_started"
                // xpath="/response/result/doc/str[@name='imits_phenotype_started']"
                // />
                // <field column="imits_phenotype_complete"
                if (allele.getImitsPhenotypeComplete() != null) {
                    img.addImitsPhenotypeComplete(allele.getImitsPhenotypeComplete());
                }
				// xpath="/response/result/doc/str[@name='imits_phenotype_complete']"
                // />
                // <field column="imits_phenotype_status"
                if (allele.getImitsPhenotypeStatus() != null) {
                    img.addImitsPhenotypeStatus(allele.getImitsPhenotypeStatus());
                }
				// xpath="/response/result/doc/str[@name='imits_phenotype_status']"
                // />
                //
                // <!-- phenotyping status -->
                // <field column="latest_phenotype_status"
                // xpath="/response/result/doc/str[@name='latest_phenotype_status']"
                // />
                if (allele.getLegacyPhenotypeStatus() != null) {
                    img.setLegacyPhenotypeStatus(allele.getLegacyPhenotypeStatus());
                }
				// <field column="legacy_phenotype_status"
                // xpath="/response/result/doc/int[@name='legacy_phenotype_status']"
                // />
                //
                // <!-- production/phenotyping centers -->
                img.setLatestProductionCentre(allele.getLatestProductionCentre());
				// <field column="latest_production_centre"
                // xpath="/response/result/doc/arr[@name='latest_production_centre']/str"
                // />
                // <field column="latest_phenotyping_centre"
                img.setLatestPhenotypingCentre(allele.getLatestPhenotypingCentre());
				// xpath="/response/result/doc/arr[@name='latest_phenotyping_centre']/str"
                // />
                //
                // <!-- alleles of a gene -->
                img.setAlleleName(allele.getAlleleName());
				// <field column="allele_name"
                // xpath="/response/result/doc/arr[@name='allele_name']/str" />
                //
                // </entity>
            }
        }

    }

	// need hp mapping from phenodign core
    private void populateMpToHpTermsMap()
            throws IndexerException {

        logger.info("populating Mp To Hp Term map");
        mpToHpMap = IndexerMap.getMpToHpTerms(phenodigmServer);
    }

    private void populateMpToNode() {

		// select nt.node_id, ti.term_id from mp_term_infos ti, mp_node2term nt
        // where ti.term_id=nt.term_id and ti.term_id !='MP:0000001'
        logger.info("populating mpTermToNode");
        // <field column="syn_name" name="mp_term_synonym" />
        String query = "select nt.node_id, ti.term_id from mp_term_infos ti, mp_node2term nt where ti.term_id=nt.term_id and ti.term_id !='MP:0000001'";

        try (PreparedStatement p = ontoDbConnection.prepareStatement(query)) {
            ResultSet resultSet = p.executeQuery();

            while (resultSet.next()) {
                int nodeId = resultSet.getInt("node_id");
                String termId = resultSet.getString("term_id");

                if ( ! nodeToMp.containsKey(nodeId)) {
                    nodeToMp.put(nodeId, nodeToMp.get(termId));
					// System.out.println("adding to mpNode2termTopLevel" +
                    // nodeId + " " +
                    // termId);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
