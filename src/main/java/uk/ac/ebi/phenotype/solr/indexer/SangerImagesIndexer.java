package uk.ac.ebi.phenotype.solr.indexer;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.beans.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

import uk.ac.ebi.phenotype.bean.GenomicFeatureBean;
import uk.ac.ebi.phenotype.pojo.BiologicalSampleType;
import uk.ac.ebi.phenotype.pojo.ImageRecordObservation;
import uk.ac.ebi.phenotype.pojo.SexType;
import uk.ac.ebi.phenotype.pojo.ZygosityType;
import uk.ac.ebi.phenotype.service.dto.ObservationDTO;

import javax.sql.DataSource;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Populate the experiment core
 */

public class SangerImagesIndexer {

	private static final Logger logger = LoggerFactory.getLogger(SangerImagesIndexer.class);
	private static Connection connection;

	@Autowired
	@Qualifier("sangerImagesIndexing")
	SolrServer sangerImagesIndexing;

	Map<Integer, DcfBean> dcfMap = new HashMap<>();
	Map<String, Map<String, String>> translateCategoryNames = new HashMap<>();
	Map<Integer, MouseBean> mouseMvMap = new HashMap<>();
	Map<String, AlleleBean> alleleMpiMap = new HashMap<>();
	Map<String, List<String>> synonyms = new HashMap<>();
	Map<String, GenomicFeatureBean> featuresMap = new HashMap<>();
	private Map<Integer, ExperimentDict> expMap = new HashMap<>();
	Map<String, String> sangerProcedureToImpcMapping=new HashMap<String, String>();
	Map<Integer, List<Annotation>> annotations=new HashMap<>();


	public SangerImagesIndexer() {

	}


	public SangerImagesIndexer(Connection connection) {

		SangerImagesIndexer.connection = connection;
	}


	public static void main(String[] args)
	throws SQLException, InterruptedException, JAXBException, IOException, NoSuchAlgorithmException, KeyManagementException, SolrServerException {

		OptionParser parser = new OptionParser();

		// parameter to indicate which spring context file to use
		parser.accepts("context").withRequiredArg().ofType(String.class);

		OptionSet options = parser.parse(args);
		String context = (String) options.valuesOf("context").get(0);

		logger.info("Using application context file {}", context);

		// Wire up spring support for this application
		SangerImagesIndexer main = new SangerImagesIndexer();

		ApplicationContext applicationContext;
		try {

			// Try context as a file resource
			applicationContext = new FileSystemXmlApplicationContext("file:" + context);

		} catch (RuntimeException e) {

			logger.warn("An error occurred loading the file: {}", e.getMessage());

			// Try context as a class path resource
			applicationContext = new ClassPathXmlApplicationContext(context);

			logger.warn("Using classpath app-config file: {}", context);

		}
		applicationContext.getAutowireCapableBeanFactory().autowireBeanProperties(main, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);

		// allow hibernate session to stay open the whole execution
		PlatformTransactionManager transactionManager = (PlatformTransactionManager) applicationContext.getBean("transactionManager");
		DefaultTransactionAttribute transactionAttribute = new DefaultTransactionAttribute(TransactionDefinition.PROPAGATION_REQUIRED);
		transactionAttribute.setIsolationLevel(TransactionDefinition.ISOLATION_SERIALIZABLE);
		transactionManager.getTransaction(transactionAttribute);

		DataSource ds = ((DataSource) applicationContext.getBean("komp2DataSource"));
		connection = ds.getConnection();

		main.run();

		logger.info("Process finished.  Exiting.");
		System.exit(0);
	}


	private void run()
	throws JAXBException, IOException, SQLException, KeyManagementException, NoSuchAlgorithmException, SolrServerException {

		logger.info("Populating dcf maps");
		populateDcfMap();
		populateMouseMv();
		populateAlleleMpi();
		populateSynonyms();
		populateGenomicFeature2();
		populateExperiments();
		loadAnnotations();
		sangerProcedureToImpcMapping.put("Wholemount Expression", "Adult LacZ");
		sangerProcedureToImpcMapping.put("Xray", "X-ray");
		// 'Xray' : 'X-ray Imaging',
		sangerProcedureToImpcMapping.put("Flow Cytometry", "FACS Analysis");
		sangerProcedureToImpcMapping.put("Histology Slide", "Histopathology");
		sangerProcedureToImpcMapping.put("Embryo Dysmorphology", "Combined SHIRPA and Dysmorphology");
		//
		// logger.info("Populating data source, project, and category translation maps");
		// populateDatasourceDataMap();
		// populateCategoryNamesDataMap();
		//
		// logger.info("Populating biological data maps");
		// populateBiologicalDataMap();
		// populateLineBiologicalDataMap();
		//
		// logger.info("Populating experiment solr core");
		Long start = System.currentTimeMillis();
		populateSangerImagesCore();
		logger.info("Populating experiment solr core - done [took: {}s]", (System.currentTimeMillis() - start) / 1000.0);

	}


	public void populateSangerImagesCore()
	throws SQLException, IOException, SolrServerException {

		int count = 0;

		sangerImagesIndexing.deleteByQuery("*:*");

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

		String query = "SELECT 'images' as dataType, IMA_IMAGE_RECORD.ID, FOREIGN_TABLE_NAME, FOREIGN_KEY_ID, ORIGINAL_FILE_NAME, CREATOR_ID, CREATED_DATE, EDITED_BY, EDIT_DATE, CHECK_NUMBER, FULL_RESOLUTION_FILE_PATH, SMALL_THUMBNAIL_FILE_PATH, LARGE_THUMBNAIL_FILE_PATH, SUBCONTEXT_ID, QC_STATUS_ID, PUBLISHED_STATUS_ID, o.name as institute, IMA_EXPERIMENT_DICT.ID as experiment_dict_id FROM IMA_IMAGE_RECORD, IMA_SUBCONTEXT, IMA_EXPERIMENT_DICT, organisation o  WHERE IMA_IMAGE_RECORD.organisation=o.id AND IMA_IMAGE_RECORD.subcontext_id=IMA_SUBCONTEXT.id AND IMA_SUBCONTEXT.experiment_dict_id=IMA_EXPERIMENT_DICT.id AND IMA_EXPERIMENT_DICT.name!='Mouse Necropsy' limit 1000";

		try (PreparedStatement p = connection.prepareStatement(query, java.sql.ResultSet.TYPE_FORWARD_ONLY, java.sql.ResultSet.CONCUR_READ_ONLY)) {

			p.setFetchSize(Integer.MIN_VALUE);

			ResultSet r = p.executeQuery();
			while (r.next()) {
				// System.out.println(r.getInt("IMA_IMAGE_RECORD.ID"));
				SangerImagesDTO o = new SangerImagesDTO();
				int imageRecordId=r.getInt("IMA_IMAGE_RECORD.ID");
				o.setId(imageRecordId);
				o.setDataType(r.getString("dataType"));
				o.setFullResolutionFilePath(r.getString("FULL_RESOLUTION_FILE_PATH"));
				o.setLargeThumbnailFilePath(r.getString("LARGE_THUMBNAIL_FILE_PATH"));
				o.setOriginalFileName(r.getString("ORIGINAL_FILE_NAME"));
				o.setSmallThumbnailFilePath(r.getString("SMALL_THUMBNAIL_FILE_PATH"));
				o.setInstitute(r.getString("institute"));
				DcfBean dcfInfo = dcfMap.get(imageRecordId);
				if (dcfInfo != null) {
					//System.out.println("dcfInfo="+dcfInfo);
					o.setDcfId(dcfInfo.dcfId);
					o.setDcfExpId(dcfInfo.dcfExpId);
					o.setSangerProcedureName(dcfInfo.sangerProcedureName);
					o.setSangerProcedureId(dcfInfo.sangerProcedureName);
				}
				MouseBean mb = mouseMvMap.get(r.getInt("FOREIGN_KEY_ID"));
				if (mb != null) {
					//System.out.println("adding mouse=" + mb);
					o.setAgeInWeeks(mb.ageInWeeks);
					o.setGenotypeString(mb.genotypeString);
					AlleleBean alBean = alleleMpiMap.get(mb.genotypeString);
					if (alBean != null) {
						o.setAllele_accession(alBean.allele_accession);
						o.setSangerSymbol(alBean.sangerSymbol);
						if (featuresMap.containsKey(alBean.gf_acc)) {
							GenomicFeatureBean feature = featuresMap.get(alBean.gf_acc);
							o.setSymbol(feature.getSymbol());
							//System.out.println("setting symbol in main method via feature=" + feature.getSymbol());
							o.setGeneName(feature.getName());
							if(synonyms.containsKey(feature.getAccession())){
								List<String> syns=synonyms.get(feature.getAccession());
								o.setSynonyms(syns);
//								for(String syn:syns){
//									System.out.println("syn="+syn);
//							
//								}
							}
						}
						// o.setSubType(alBean.subType);
					}
				}
				if (expMap.containsKey(new Integer(r.getInt("ID")))) {
					ExperimentDict expBean = expMap.get(r.getInt("ID"));
					o.setExperimentName(expBean.name);
					o.setSangerProcedureName(expBean.name);
					o.setProcedureName(this.getImpcProcedureFromSanger(expBean.name));
					// o.setExperimentName(name)
				}
				if(annotations.containsKey(imageRecordId)){
					List<Annotation> annotationList=annotations.get(imageRecordId);
					List<String> tagNames=new ArrayList<>();
					List<String> tagValues=new ArrayList<>();
					for(Annotation ann:annotationList){
						tagNames.add(ann.tagName);
						tagValues.add(ann.tagValue);
					}
					o.setTagNames(tagNames);
					o.setTagValues(tagValues);
					
				}
				

				// 60 seconds between commits
				sangerImagesIndexing.addBean(o, 60000);

				count++;

				if (count % 100000 == 0) {
					logger.info(" added " + count + " beans");
				}

			}

			// Final commit to save the rest of the docs
			sangerImagesIndexing.commit();

		} catch (Exception e) {
			logger.error("Big error {}", e.getMessage(), e);
		}

	}

	protected class SangerImagesDTO {

		// <field column="dataType" name="dataType"/>
		// <field column="FULL_RESOLUTION_FILE_PATH"
		// name="fullResolutionFilePath" />
		// <field column="LARGE_THUMBNAIL_FILE_PATH"
		// name="largeThumbnailFilePath" />
		// <field column="ORIGINAL_FILE_NAME" name="originalFileName" />
		// <field column="SMALL_THUMBNAIL_FILE_PATH"
		// name="smallThumbnailFilePath" />
		// <field column="institute" name="institute" />
		@Field("id")
		int id;
		@Field("dataType")
		String dataType;
		@Field("fullResolutionFilePath")
		String fullResolutionFilePath;
		@Field("largeThumbnailFilePath")
		String largeThumbnailFilePath;
		@Field("originalFileName")
		String originalFileName;
		@Field("smallThumbnailFilePath")
		String smallThumbnailFilePath;
		@Field("institute")
		String institute;
		// need method to get imaDcfImageView data for here
		@Field("dcfId")
		int dcfId;
		@Field("dcfExpId")
		String dcfExpId;
		@Field("sangerProcedureName")
		String sangerProcedureName;
		@Field("procedure_name")
		String procedureName;
		@Field("genotypeString")
		String genotypeString;
		@Field("geneName")
		private String geneName;
		@Field("expName")
		private String experimentName;
		@Field("expName_exp")
		private String expName_exp;
		@Field("procedure_name")
		private String procedure_name;
		@Field("geneSynonyms")
		private List<String> synonyms;
		@Field("tagValue")
		private List<String> tagValues;
		@Field("tagName")
		private List<String> tagNames;


		public String getGeneName() {

			return geneName;
		}


		

		public void setTagValues(List<String> tagValues) {

			this.tagValues=tagValues;
			
		}




		public void setTagNames(List<String> tagNames) {

		this.tagNames=tagNames;
			
		}




		public void setSynonyms(List<String> syns) {

			this.synonyms=syns;
			
		}


		public void setProcedureName(String procedureName2) {

			this.procedureName=procedureName2;
			
		}


		public void setExperimentName(String name) {

			this.experimentName = name;

		}


		public String getAccession() {

			return accession;
		}


		public String getSymbol() {

			return symbol;
		}

		@Field("accession")
		private String accession;
		@Field("symbol")
		private String symbol;


		public String getGenotypeString() {

			return genotypeString;
		}


		public void setGeneName(String geneName) {

			this.geneName = geneName;

		}


		public void setAccession(String accession) {

			this.accession = accession;

		}


		public void setSymbol(String symbol) {

			this.symbol = symbol;

		}


		public void setGenotypeString(String genotypeString) {

			this.genotypeString = genotypeString;
		}


		public String getAgeInWeeks() {

			return ageInWeeks;
		}


		public void setAgeInWeeks(String ageInWeeks) {

			this.ageInWeeks = ageInWeeks;
		}

		@Field("ageInWeeks")
		String ageInWeeks;

		@Field("sangerSymbol")
		String sangerSymbol;


		public String getSangerSymbol() {

			return sangerSymbol;
		}


		public void setSangerSymbol(String sangerSymbol) {

			this.sangerSymbol = sangerSymbol;
		}


		public String getAllele_accession() {

			return allele_accession;
		}


		public void setAllele_accession(String allele_accession) {

			this.allele_accession = allele_accession;
		}

		@Field("allele_accession")
		String allele_accession;


		public int getId() {

			return id;
		}


		public void setId(int id) {

			this.id = id;
		}


		public String getDataType() {

			return dataType;
		}


		public void setDataType(String dataType) {

			this.dataType = dataType;
		}


		public String getFullResolutionFilePath() {

			return fullResolutionFilePath;
		}


		public void setFullResolutionFilePath(String fullResolutionFilePath) {

			this.fullResolutionFilePath = fullResolutionFilePath;
		}


		public String getLargeThumbnailFilePath() {

			return largeThumbnailFilePath;
		}


		public void setLargeThumbnailFilePath(String largeThumbnailFilePath) {

			this.largeThumbnailFilePath = largeThumbnailFilePath;
		}


		public String getOriginalFileName() {

			return originalFileName;
		}


		public void setOriginalFileName(String originalFileName) {

			this.originalFileName = originalFileName;
		}


		public String getSmallThumbnailFilePath() {

			return smallThumbnailFilePath;
		}


		public void setSmallThumbnailFilePath(String smallThumbnailFilePath) {

			this.smallThumbnailFilePath = smallThumbnailFilePath;
		}


		public String getInstitute() {

			return institute;
		}


		public void setInstitute(String institute) {

			this.institute = institute;
		}


		public int getDcfId() {

			return dcfId;
		}


		public void setDcfId(int dcfId) {

			this.dcfId = dcfId;
		}


		public String getDcfExpId() {

			return dcfExpId;
		}


		public void setDcfExpId(String dcfExpId) {

			this.dcfExpId = dcfExpId;
		}


		public String getSangerProcedureName() {

			return sangerProcedureName;
		}


		public void setSangerProcedureName(String sangerProcedureName) {

			this.sangerProcedureName = sangerProcedureName;
		}


		public String getSangerProcedureId() {

			return sangerProcedureId;
		}


		public void setSangerProcedureId(String sangerProcedureId) {

			this.sangerProcedureId = sangerProcedureId;
		}

		String sangerProcedureId;

		//
		// <entity dataSource="komp2ds" name="imaDcfImageView"
		// query="SELECT DCF_ID, NAME, PROCEDURE_ID, EXPERIMENT_ID, MOUSE_ID FROM `IMA_DCF_IMAGE_VW` dcf, IMA_IMAGE_RECORD ir, PHN_STD_OPERATING_PROCEDURE stdOp WHERE dcf.id=ir.id and dcf.dcf_id=stdOp.id and ir.id=${ima_image_record.ID}">
		// <field column="DCF_ID" name="dcfId" />
		// <field column="EXPERIMENT_ID" name="dcfExpId" />
		// <field column="NAME" name="sangerProcedureName" />
		// <field column="PROCEDURE_ID" name="sangerProcedureId" />
		// </entity>
	}


	/**
	 * Add all the relevant data to the Impress map
	 * 
	 * @throws SQLException
	 *             when a database exception occurs
	 */
	public void populateDcfMap()
	throws SQLException {

		List<String> queries = new ArrayList<>();
		queries.add("SELECT ir.id as id, DCF_ID, NAME, PROCEDURE_ID, EXPERIMENT_ID, MOUSE_ID FROM `IMA_DCF_IMAGE_VW` dcf, IMA_IMAGE_RECORD ir, PHN_STD_OPERATING_PROCEDURE stdOp WHERE dcf.id=ir.id and dcf.dcf_id=stdOp.id");// joins
		// on
		for (String query : queries) {

			try (PreparedStatement p = connection.prepareStatement(query)) {

				ResultSet resultSet = p.executeQuery();

				while (resultSet.next()) {

					DcfBean b = new DcfBean();
					/*
					 * <field column="DCF_ID" name="dcfId" /> <field
					 * column="EXPERIMENT_ID" name="dcfExpId" /> <field
					 * column="NAME" name="sangerProcedureName" /> <field
					 * column="PROCEDURE_ID" name="sangerProcedureId" />
					 */

					b.dcfId = resultSet.getInt("DCF_ID");
					b.dcfExpId = resultSet.getString("EXPERIMENT_ID");
					b.sangerProcedureName = resultSet.getString("NAME");
					b.sangerProcedureId = resultSet.getString("PROCEDURE_ID");
					// System.out.println("adding dcf id=" + b);
					dcfMap.put(resultSet.getInt("id"), b);

				}

			}
		}
	}


	public void populateMouseMv()
	throws SQLException {

		// select * from IMPC_MOUSE_ALLELE_MV where
		// MOUSE_ID=${ima_image_record.FOREIGN_KEY_ID}
		System.out.println("populating MouseMv");
		String query = "select MOUSE_ID, AGE_IN_WEEKS, ALLELE from IMPC_MOUSE_ALLELE_MV";// where
																							// MOUSE_ID=${ima_image_record.FOREIGN_KEY_ID}");//
																							// image
																							// record.foreignkeyid
																							// to
																							// mouse_id
																							// on

		try (PreparedStatement p = connection.prepareStatement(query)) {

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
		System.out.println("populating alleleMpi");
		String query = "select * from `allele`";// where
												// MOUSE_ID=${ima_image_record.FOREIGN_KEY_ID}");//
												// image record.foreignkeyid to
												// mouse_id
												// on

		try (PreparedStatement p = connection.prepareStatement(query)) {

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

		System.out.println("populating genomicFeature2");
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

		try (PreparedStatement p = connection.prepareStatement(query)) {
			ResultSet resultSet = p.executeQuery();

			while (resultSet.next()) {
				GenomicFeatureBean gf = new GenomicFeatureBean();
				gf.setSymbol(resultSet.getString("symbol"));
				gf.setAccession(resultSet.getString("acc"));
				gf.setName(resultSet.getString("name"));
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
		System.out.println("populating genomicFeature2");
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

		try (PreparedStatement p = connection.prepareStatement(query)) {
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
		System.out.println("populating synonyms");
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

		try (PreparedStatement p = connection.prepareStatement(query)) {

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
			System.out.println("synonyms size=" + synonyms.size());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	private void loadAnnotations(){
		//select * from IMA_IMAGE_TAG
		System.out.println("populating annotations");
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

		try (PreparedStatement p = connection.prepareStatement(query)) {

			ResultSet resultSet = p.executeQuery();
//			<field column="TAG_NAME" name="tagName" />
//			<field column="TAG_VALUE" name="tagValue" />
			while (resultSet.next()) {
				int irId = resultSet.getInt("IMAGE_RECORD_ID");
				
				Annotation annotation=new Annotation();
				annotation.tagName=resultSet.getString("TAG_NAME");
				annotation.tagValue=resultSet.getString("TAG_VALUE");
				if (annotations.containsKey(irId)) {
					List<Annotation> list = annotations.get(irId);
					
					list.add(annotation);
				} else {
					List<Annotation> list = new ArrayList<>();
					list.add(annotation);
					annotations.put(irId, list);
				}

			}
			System.out.println("synonyms size=" + synonyms.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	protected class Annotation{

		public String tagValue;
		public String tagName;
		
	}

	// protected AlleleBean populateSubType(AlleleBean b){
	// // <entity dataSource="komp2ds" name="notnull"
	// query="select * from `genomic_feature` where acc='${alleleMpi.gf_acc}' and db_id=${alleleMpi.gf_db_id}">
	// // <entity dataSource="komp2ds" name="subtype2"
	// query="select  name,  concat('${genomic_feature2.symbol}_', '${genomic_feature2.acc}') as symbol_gene from `ontology_term` where acc='${genomic_feature2.subtype_acc}' and db_id=${genomic_feature2.subtype_db_id}">
	// // <field column="name" name="subtype" />
	// String query =
	// "select  name,  concat(?, ?) as symbol_gene from `ontology_term` where acc=? and db_id=?";//
	// where
	// // MOUSE_ID=${ima_image_record.FOREIGN_KEY_ID}");//
	// // image record.foreignkeyid to
	// // mouse_id
	// // on
	//
	// try (PreparedStatement p = connection.prepareStatement(query)) {
	// p.setString(1, b.symbol);
	// p.setString(2, b.accession);
	// p.setInt(3,b.subtypeAccession);
	// p.setInt(4,b.subtypeDbId);
	// ResultSet resultSet = p.executeQuery();
	//
	// while (resultSet.next()) {
	//
	// b.subType = resultSet.getString("name");
	// System.out.println("setting subtyp="+b.subType);
	//
	//
	// }
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// return b;
	// }

	protected class AlleleBean {

		// <field column="symbol" name="sangerSymbol" />
		// <field column="acc" name="allele_accession" />
		String gf_acc;
		String sangerSymbol;
		String allele_accession;
		String subType;

	}


	// <entity dataSource="komp2ds" name="mouse"
	// query="select * from IMPC_MOUSE_ALLELE_MV where MOUSE_ID=${ima_image_record.FOREIGN_KEY_ID}">
	// <field column="MOUSE_ID" name="mouseId" />
	// <!-- <field column="COLONY_NAME" name="colonyName" />
	// <field column="AGE_IN_WEEKS" name="ageInWeeks" /> -->
	//
	// <!-- </edata-config.xmlntity>
	// <entity dataSource="komp2ds" name="mouse"
	// query="select * from ima_mouse_image_vw where id=${ima_image_record.FOREIGN_KEY_ID}">
	// <field column="ID" name="mouseId" /> -->
	// <!-- <field column="COLONY_NAME" name="colonyName" />-->
	// <field column="AGE_IN_WEEKS" name="ageInWeeks" />
	// <field column="ALLELE" name="genotypeString" />
	// <!-- <entity dataSource="komp2ds" name="mts_mouse_allele_mv"
	// query="select * from `mts_mouse_allele_mv` where MOUSE_ID=${mouse.MOUSE_ID}">
	// -->
	// <!--
	// <entity dataSource="komp2ds" name="MtsMouseAllele"
	// query="select * from `MTS_MOUSE_ALLELE` where MOUSE_ID=${mts_mouse_allele_mv.MOUSE_ID}">
	// <entity dataSource="komp2ds" name="mtsGenotypeDict"
	// query="select * from `MTS_GENOTYPE_DICT` where ID=${MtsMouseAllele.GENOTYPE_DICT_ID}">
	// <field column="NAME" name="alleleName" />
	// </entity>
	// </entity>
	// -->
	//
	// <entity dataSource="komp2ds" name="alleleMpi"
	// query="select * from `allele` where symbol='${mouse.ALLELE}'">
	// <!-- Get gene associated information from our sanger tables (we also
	// get it from our main mpi2 db at the moment as well- this sanger tables
	// are
	// temporary for demo purposes??? -->
	// <field column="symbol" name="sangerSymbol" />
	// <field column="acc" name="allele_accession" />
	//
	// <entity dataSource="komp2ds" name="genomic_feature2"
	// query="select * from `genomic_feature` where acc='${alleleMpi.gf_acc}' and db_id=${alleleMpi.gf_db_id}">
	// <field column="symbol" name="symbol" />
	// <field column="acc" name="accession" />
	// <field column="name" name="geneName" />
	//
	// <entity dataSource="komp2ds" name="notnull"
	// query="select * from `genomic_feature` where acc='${alleleMpi.gf_acc}' and db_id=${alleleMpi.gf_db_id}">
	// <entity dataSource="komp2ds" name="subtype2"
	// query="select  name,  concat('${genomic_feature2.symbol}_', '${genomic_feature2.acc}') as symbol_gene from `ontology_term` where acc='${genomic_feature2.subtype_acc}' and db_id=${genomic_feature2.subtype_db_id}">
	// <field column="name" name="subtype" />
	// </entity>
	// </entity>
	//
	// <entity dataSource="komp2ds" name="synonym"
	// query="select * from synonym where acc='${genomic_feature2.acc}' ">
	// <field column="symbol" name="geneSynonyms" />
	// </entity>
	//
	// <!-- other gene core stuff -->
	// <entity dataSource="allele_core" name="genedoc" stream="true"
	// url="q=mgi_accession_id:&quot;${genomic_feature2.acc}&quot;&amp;rows=1&amp;wt=normal"
	// processor="XPathEntityProcessor" forEach="/response/result/doc/" >
	//
	// <field column="mgi_accession_id"
	// xpath="/response/result/doc/str[@name='mgi_accession_id']" />
	// <field column="marker_symbol"
	// xpath="/response/result/doc/str[@name='marker_symbol']" />
	// <field column="marker_name"
	// xpath="/response/result/doc/str[@name='marker_name']" />
	// <field column="marker_synonym"
	// xpath="/response/result/doc/arr[@name='marker_synonym']/str" />
	// <field column="marker_type"
	// xpath="/response/result/doc/str[@name='marker_type']" />
	// <field column="human_gene_symbol"
	// xpath="/response/result/doc/arr[@name='human_gene_symbol']/str" />
	//
	// <!-- latest project status (ES cells/mice production status) -->
	// <field column="status" xpath="/response/result/doc/str[@name='status']"
	// />
	//
	// <!-- latest mice phenotyping status for faceting -->
	// <field column="imits_phenotype_started"
	// xpath="/response/result/doc/str[@name='imits_phenotype_started']" />
	// <field column="imits_phenotype_complete"
	// xpath="/response/result/doc/str[@name='imits_phenotype_complete']" />
	// <field column="imits_phenotype_status"
	// xpath="/response/result/doc/str[@name='imits_phenotype_status']" />
	//
	// <!-- phenotyping status -->
	// <field column="latest_phenotype_status"
	// xpath="/response/result/doc/str[@name='latest_phenotype_status']" />
	// <field column="legacy_phenotype_status"
	// xpath="/response/result/doc/int[@name='legacy_phenotype_status']" />
	//
	// <!-- production/phenotyping centers -->
	// <field column="latest_production_centre"
	// xpath="/response/result/doc/arr[@name='latest_production_centre']/str" />
	// <field column="latest_phenotyping_centre"
	// xpath="/response/result/doc/arr[@name='latest_phenotyping_centre']/str"
	// />
	//
	// <!-- alleles of a gene -->
	// <field column="allele_name"
	// xpath="/response/result/doc/arr[@name='allele_name']/str" />
	//
	// </entity>
	//
	// </entity>
	// </entity>
	// <!-- </entity> -->
	// </entity>

	public static Connection getConnection() {

		return connection;
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
		public Integer dcfId;
		public String dcfExpId;
		public String sangerProcedureName;
		public String sangerProcedureId;


		@Override
		public String toString() {

			return "dcf=" + dcfId + " " + dcfExpId + " " + sangerProcedureName + " " + sangerProcedureId;
		}

	}

	protected class MouseBean {

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
	
	private String getImpcProcedureFromSanger(String sangerProcedure){
		if(sangerProcedureToImpcMapping.containsKey(sangerProcedure)){
			return sangerProcedureToImpcMapping.get(sangerProcedure);
		}else{
			return sangerProcedure;
		}
		
	}


}
