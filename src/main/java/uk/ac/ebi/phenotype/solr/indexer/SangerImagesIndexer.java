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

	Map<String, BiologicalDataBean> biologicalData = new HashMap<>();
	Map<String, BiologicalDataBean> lineBiologicalData = new HashMap<>();

	Map<Integer, DcfBean> dcfMap = new HashMap<>();
	// Map<Integer, ImpressBean> procedureMap = new HashMap<>();
	// Map<Integer, ImpressBean> parameterMap = new HashMap<>();

	Map<Integer, DatasourceBean> datasourceMap = new HashMap<>();
	Map<Integer, DatasourceBean> projectMap = new HashMap<>();

	Map<String, Map<String, String>> translateCategoryNames = new HashMap<>();


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
	}


	private void run()
	throws JAXBException, IOException, SQLException, KeyManagementException, NoSuchAlgorithmException, SolrServerException {

		logger.info("Populating dcf maps");
		populateDcfMap();
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
//System.out.println(r.getInt("IMA_IMAGE_RECORD.ID"));
				SangerImagesDTO o = new SangerImagesDTO();
				o.setId(r.getInt("IMA_IMAGE_RECORD.ID"));
				o.setDataType(r.getString("dataType"));
				o.setFullResolutionFilePath(r.getString("FULL_RESOLUTION_FILE_PATH"));
				o.setLargeThumbnailFilePath(r.getString("LARGE_THUMBNAIL_FILE_PATH"));
				o.setOriginalFileName(r.getString("ORIGINAL_FILE_NAME"));
				o.setSmallThumbnailFilePath(r.getString("SMALL_THUMBNAIL_FILE_PATH"));
				o.setInstitute(r.getString("institute"));
				DcfBean dcfInfo=dcfMap.get(r.getInt("IMA_IMAGE_RECORD.ID"));
				if(dcfInfo!=null){
					System.out.println(dcfInfo);
					o.setDcfId(dcfInfo.dcfId);
					o.setDcfExpId(dcfInfo.dcfExpId);
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


		public int  getDcfId() {

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
	 * Add all the relevant data required quickly looking up biological data
	 * associated to a biological sample
	 * 
	 * @throws SQLException
	 *             when a database exception occurs
	 */
	public void populateBiologicalDataMap()
	throws SQLException {

		String query = "SELECT CAST(bs.id AS CHAR) as biological_sample_id, bs.organisation_id as phenotyping_center_id, " + "org.name as phenotyping_center_name, bs.sample_group, bs.external_id as external_sample_id, " + "ls.date_of_birth, ls.colony_id, ls.sex as sex, ls.zygosity, " + "bms.biological_model_id, " + "strain.acc as strain_acc, strain.name as strain_name, " + "(select distinct allele_acc from biological_model_allele bma WHERE bma.biological_model_id=bms.biological_model_id) as allele_accession, " + "(select distinct a.symbol from biological_model_allele bma INNER JOIN allele a on (a.acc=bma.allele_acc AND a.db_id=bma.allele_db_id) WHERE bma.biological_model_id=bms.biological_model_id)  as allele_symbol, " + "(select distinct gf_acc from biological_model_genomic_feature bmgf WHERE bmgf.biological_model_id=bms.biological_model_id) as acc, " + "(select distinct gf.symbol from biological_model_genomic_feature bmgf INNER JOIN genomic_feature gf on gf.acc=bmgf.gf_acc WHERE bmgf.biological_model_id=bms.biological_model_id)  as symbol " + "FROM biological_sample bs " + "INNER JOIN organisation org ON bs.organisation_id=org.id " + "INNER JOIN live_sample ls ON bs.id=ls.id " + "INNER JOIN biological_model_sample bms ON bs.id=bms.biological_sample_id " + "INNER JOIN biological_model_strain bmstrain ON bmstrain.biological_model_id=bms.biological_model_id " + "INNER JOIN strain strain ON strain.acc=bmstrain.strain_acc";

		try (PreparedStatement p = connection.prepareStatement(query)) {

			ResultSet resultSet = p.executeQuery();

			while (resultSet.next()) {
				BiologicalDataBean b = new BiologicalDataBean();

				b.alleleAccession = resultSet.getString("allele_accession");
				b.alleleSymbol = resultSet.getString("allele_symbol");
				b.biologicalModelId = resultSet.getInt("biological_model_id");
				b.biologicalSampleId = resultSet.getInt("biological_sample_id");
				b.colonyId = resultSet.getString("colony_id");
				b.dateOfBirth = resultSet.getDate("date_of_birth");
				b.externalSampleId = resultSet.getString("external_sample_id");
				b.geneAcc = resultSet.getString("acc");
				b.geneSymbol = resultSet.getString("symbol");
				b.phenotypingCenterId = resultSet.getInt("phenotyping_center_id");
				b.phenotypingCenterName = resultSet.getString("phenotyping_center_name");
				b.sampleGroup = resultSet.getString("sample_group");
				b.sex = resultSet.getString("sex");
				b.strainAcc = resultSet.getString("strain_acc");
				b.strainName = resultSet.getString("strain_name");
				b.zygosity = resultSet.getString("zygosity");

				biologicalData.put(resultSet.getString("biological_sample_id"), b);
			}
		}
	}


	/**
	 * Add all the relevant data required quickly looking up biological data
	 * associated to a biological model (really an experiment)
	 * 
	 * @throws SQLException
	 *             when a database exception occurs
	 */
	public void populateLineBiologicalDataMap()
	throws SQLException {

		String query = "SELECT e.id as experiment_id, e.colony_id, e.biological_model_id, " + "e.organisation_id as phenotyping_center_id, org.name as phenotyping_center_name, " + "strain.acc as strain_acc, strain.name as strain_name, " + "(select distinct allele_acc from biological_model_allele bma WHERE bma.biological_model_id=e.biological_model_id) as allele_accession, " + "(select distinct a.symbol from biological_model_allele bma INNER JOIN allele a on (a.acc=bma.allele_acc AND a.db_id=bma.allele_db_id) WHERE bma.biological_model_id=e.biological_model_id)  as allele_symbol, " + "(select distinct gf_acc from biological_model_genomic_feature bmgf WHERE bmgf.biological_model_id=e.biological_model_id) as acc, " + "(select distinct gf.symbol from biological_model_genomic_feature bmgf INNER JOIN genomic_feature gf on gf.acc=bmgf.gf_acc WHERE bmgf.biological_model_id=e.biological_model_id)  as symbol " + "FROM experiment e " + "INNER JOIN organisation org ON e.organisation_id=org.id " + "INNER JOIN biological_model_strain bm_strain ON bm_strain.biological_model_id=e.biological_model_id " + "INNER JOIN strain strain ON strain.acc=bm_strain.strain_acc";

		try (PreparedStatement p = connection.prepareStatement(query)) {

			ResultSet resultSet = p.executeQuery();

			while (resultSet.next()) {

				BiologicalDataBean b = new BiologicalDataBean();

				b.alleleAccession = resultSet.getString("allele_accession");
				b.alleleSymbol = resultSet.getString("allele_symbol");
				b.biologicalModelId = resultSet.getInt("biological_model_id");
				b.colonyId = resultSet.getString("colony_id");
				b.geneAcc = resultSet.getString("acc");
				b.geneSymbol = resultSet.getString("symbol");
				b.phenotypingCenterId = resultSet.getInt("phenotyping_center_id");
				b.phenotypingCenterName = resultSet.getString("phenotyping_center_name");
				b.strainAcc = resultSet.getString("strain_acc");
				b.strainName = resultSet.getString("strain_name");

				lineBiologicalData.put(resultSet.getString("experiment_id"), b);
			}
		}
	}


	/**
	 * Add all the relevant data required for translating the category names in
	 * the cases where the category names are numerals, but the actual name is
	 * in the description field
	 * 
	 * @throws SQLException
	 *             when a database exception occurs
	 */
	public void populateCategoryNamesDataMap()
	throws SQLException {

		String query = "SELECT pp.stable_id, ppo.name, ppo.description FROM phenotype_parameter pp \n" + "INNER JOIN phenotype_parameter_lnk_option pplo ON pp.id=pplo.parameter_id\n" + "INNER JOIN phenotype_parameter_option ppo ON ppo.id=pplo.option_id \n" + "WHERE ppo.name NOT REGEXP '^[a-zA-Z]' AND ppo.description!=''";

		try (PreparedStatement p = connection.prepareStatement(query)) {

			ResultSet resultSet = p.executeQuery();

			while (resultSet.next()) {

				String stableId = resultSet.getString("stable_id");
				if (!translateCategoryNames.containsKey(stableId)) {
					translateCategoryNames.put(stableId, new HashMap<String, String>());
				}

				translateCategoryNames.get(stableId).put(resultSet.getString("name"), resultSet.getString("description"));

			}
		}
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
					System.out.println("adding dcf id="+b);
					dcfMap.put(resultSet.getInt("id"), b);

				}

			}
		}
	}


	public void populateDatasourceDataMap()
	throws SQLException {

		List<String> queries = new ArrayList<>();
		queries.add("SELECT id, short_name as name, 'DATASOURCE' as datasource_type FROM external_db");
		queries.add("SELECT id, name, 'PROJECT' as datasource_type FROM project");

		for (String query : queries) {

			try (PreparedStatement p = connection.prepareStatement(query)) {

				ResultSet resultSet = p.executeQuery();

				while (resultSet.next()) {

					DatasourceBean b = new DatasourceBean();

					b.id = resultSet.getInt("id");
					b.name = resultSet.getString("name");

					switch (resultSet.getString("datasource_type")) {
						case "DATASOURCE":
							datasourceMap.put(resultSet.getInt("id"), b);
							break;
						case "PROJECT":
							projectMap.put(resultSet.getInt("id"), b);
							break;
					}
				}
			}
		}
	}


	public static Connection getConnection() {

		return connection;
	}


	public Map<String, Map<String, String>> getTranslateCategoryNames() {

		return translateCategoryNames;
	}


	public Map<String, BiologicalDataBean> getLineBiologicalData() {

		return lineBiologicalData;
	}


	public Map<String, BiologicalDataBean> getBiologicalData() {

		return biologicalData;
	}


//	public Map<Integer, ImpressBean> getPipelineMap() {
//
//		return pipelineMap;
//	}


	public Map<Integer, DatasourceBean> getDatasourceMap() {

		return datasourceMap;
	}


	public Map<Integer, DatasourceBean> getProjectMap() {

		return projectMap;
	}


//	public Map<Integer, ImpressBean> getProcedureMap() {
//
//		return procedureMap;
//	}
//
//
//	public Map<Integer, ImpressBean> getParameterMap() {
//
//		return parameterMap;
//	}

	/**
	 * Internal class to act as Map value DTO for biological data
	 */
	protected class BiologicalDataBean {

		public String alleleAccession;
		public String alleleSymbol;
		public Integer biologicalModelId;
		public Integer biologicalSampleId;
		public String colonyId;
		public Date dateOfBirth;
		public String externalSampleId;
		public String geneAcc;
		public String geneSymbol;
		public String phenotypingCenterName;
		public Integer phenotypingCenterId;
		public String sampleGroup;
		public String sex;
		public String strainAcc;
		public String strainName;
		public String zygosity;
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
		public String toString(){
			return "dcf="+dcfId+" "+dcfExpId+" "+sangerProcedureName+" "+sangerProcedureId; 
		}

	}

	/**
	 * Internal class to act as Map value DTO for datasource data
	 */
	protected class DatasourceBean {

		public Integer id;
		public String name;
	}
}
