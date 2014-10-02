package uk.ac.ebi.phenotype.solr.indexer;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
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

public class ObservationIndexer {

	private static final Logger logger = LoggerFactory.getLogger(ObservationIndexer.class);
	private static Connection connection;

	@Autowired
	@Qualifier("observationIndexing")
	SolrServer observationSolrServer;

	Map<String, BiologicalDataBean> biologicalData = new HashMap<>();
	Map<String, BiologicalDataBean> lineBiologicalData = new HashMap<>();

	Map<Integer, ImpressBean> pipelineMap = new HashMap<>();
	Map<Integer, ImpressBean> procedureMap = new HashMap<>();
	Map<Integer, ImpressBean> parameterMap = new HashMap<>();

	Map<Integer, DatasourceBean> datasourceMap = new HashMap<>();
	Map<Integer, DatasourceBean> projectMap = new HashMap<>();

	Map<String, Map<String, String>> translateCategoryNames = new HashMap<>();


	public ObservationIndexer() {
	}


	public ObservationIndexer(Connection connection) {
		ObservationIndexer.connection = connection;
	}



	public static void main(String[] args) throws SQLException, InterruptedException, JAXBException, IOException, NoSuchAlgorithmException, KeyManagementException, SolrServerException {
		OptionParser parser = new OptionParser();

		// parameter to indicate which spring context file to use
		parser.accepts("context").withRequiredArg().ofType(String.class);

		OptionSet options = parser.parse(args);
		String context = (String) options.valuesOf("context").get(0);

		logger.info("Using application context file {}", context);

		// Wire up spring support for this application
		ObservationIndexer main = new ObservationIndexer();

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


	private void run() throws JAXBException, IOException, SQLException, KeyManagementException, NoSuchAlgorithmException, SolrServerException {


		logger.info("Populating impress maps");
		populateImpressDataMap();

		logger.info("Populating data source, project, and category translation maps");
		populateDatasourceDataMap();
		populateCategoryNamesDataMap();

		logger.info("Populating biological data maps");
		populateBiologicalDataMap();
		populateLineBiologicalDataMap();

		logger.info("Populating experiment solr core");
		Long start = System.currentTimeMillis();
		populateObservationSolrCore();
		logger.info("Populating experiment solr core - done [took: {}s]", (System.currentTimeMillis() - start) / 1000.0);

	}


	public void populateObservationSolrCore() throws SQLException, IOException, SolrServerException {

		int count=0;

		observationSolrServer.deleteByQuery("*:*");

		String query = "SELECT o.id as id, o.db_id as datasource_id, o.parameter_id as parameter_id, o.parameter_stable_id, " +
			"o.observation_type, o.missing, o.parameter_status, o.parameter_status_message, " +
			"o.biological_sample_id, " +
			"e.project_id as project_id, e.pipeline_id as pipeline_id, e.procedure_id as procedure_id, " +
			"e.date_of_experiment, e.external_id, e.id as experiment_id, " +
			"e.metadata_combined as metadata_combined, e.metadata_group as metadata_group, " +
			"co.category as raw_category, " +
			"uo.data_point as unidimensional_data_point, " +
			"mo.data_point as multidimensional_data_point, " +
			"tso.data_point as time_series_data_point, " +
			"mo.order_index, " +
			"mo.dimension, " +
			"tso.time_point, " +
			"tso.discrete_point, " +
			"iro.file_type, " +
			"iro.download_file_path " +
			"FROM observation o " +
			"LEFT OUTER JOIN categorical_observation co ON o.id=co.id " +
			"LEFT OUTER JOIN unidimensional_observation uo ON o.id=uo.id " +
			"LEFT OUTER JOIN multidimensional_observation mo ON o.id=mo.id " +
			"LEFT OUTER JOIN time_series_observation tso ON o.id=tso.id " +
			"LEFT OUTER JOIN image_record_observation iro ON o.id=iro.id " +
			"INNER JOIN experiment_observation eo ON eo.observation_id=o.id " +
			"INNER JOIN experiment e on eo.experiment_id=e.id " +
			"WHERE o.missing=0";

		try (PreparedStatement p = connection.prepareStatement(query, java.sql.ResultSet.TYPE_FORWARD_ONLY, java.sql.ResultSet.CONCUR_READ_ONLY)) {

			p.setFetchSize(Integer.MIN_VALUE);

			ResultSet r = p.executeQuery();
			while (r.next()) {

				ObservationDTO o = new ObservationDTO();
				o.setId(r.getInt("id"));
				o.setParameterId(r.getInt("parameter_id"));
				o.setExperimentId(r.getInt("experiment_id"));
				o.setDateOfExperiment(r.getDate("date_of_experiment"));
				o.setExperimentSourceId(r.getString("external_id"));

				o.setParameterId(parameterMap.get(r.getInt("parameter_id")).id);
				o.setParameterName(parameterMap.get(r.getInt("parameter_id")).name);
				o.setParameterStableId(parameterMap.get(r.getInt("parameter_id")).stableId);

				o.setProcedureId(procedureMap.get(r.getInt("procedure_id")).id);
				o.setProcedureName(procedureMap.get(r.getInt("procedure_id")).name);
				o.setProcedureStableId(procedureMap.get(r.getInt("procedure_id")).stableId);

				o.setPipelineId(pipelineMap.get(r.getInt("pipeline_id")).id);
				o.setPipelineName(pipelineMap.get(r.getInt("pipeline_id")).name);
				o.setPipelineStableId(pipelineMap.get(r.getInt("pipeline_id")).stableId);

				o.setDataSourceId(datasourceMap.get(r.getInt("datasource_id")).id);
				o.setDataSourceName(datasourceMap.get(r.getInt("datasource_id")).name);

				o.setProjectId(projectMap.get(r.getInt("project_id")).id);
				o.setProjectName(projectMap.get(r.getInt("project_id")).name);

				o.setMetadataGroup(r.getString("metadata_group"));
				if (r.wasNull()) {
					o.setMetadataGroup("");
					o.setMetadata(new ArrayList<String>());
				}

				String metadataCombined = r.getString("metadata_combined");
				if (!r.wasNull()) {
					o.setMetadata(new ArrayList<>(Arrays.asList(metadataCombined.split("::"))));
				}

				// Add the Biological data
				String bioSampleId = r.getString("biological_sample_id");
				if (r.wasNull()) {
					// Line level data

					BiologicalDataBean b = lineBiologicalData.get(r.getString("experiment_id"));
					o.setBiologicalModelId(b.biologicalModelId);
					o.setGeneAccession(b.geneAcc);
					o.setGeneSymbol(b.geneSymbol);
					o.setAlleleAccession(b.alleleAccession);
					o.setAlleleSymbol(b.alleleSymbol);
					o.setStrainAccessionId(b.strainAcc);
					o.setStrainName(b.strainName);
					o.setPhenotypingCenter(b.phenotypingCenterName);
					o.setPhenotypingCenterId(b.phenotypingCenterId);

					// All line level parameters are sample group "experimental" due to the nature of the
					// procedures (i.e. no control mice will go through VIA or FER procedures.)
					o.setGroup(BiologicalSampleType.experimental.getName());



				} else {
					// Specimen level data

					BiologicalDataBean b = biologicalData.get(bioSampleId);
					o.setBiologicalModelId(b.biologicalModelId);
					o.setGeneAccession(b.geneAcc);
					o.setGeneSymbol(b.geneSymbol);
					o.setAlleleAccession(b.alleleAccession);
					o.setAlleleSymbol(b.alleleSymbol);
					o.setStrainAccessionId(b.strainAcc);
					o.setStrainName(b.strainName);
					o.setPhenotypingCenter(b.phenotypingCenterName);
					o.setPhenotypingCenterId(b.phenotypingCenterId);

					o.setColonyId(b.colonyId);
					o.setZygosity(b.zygosity);
					o.setDateOfBirth(b.dateOfBirth);
					o.setSex(b.sex);
					o.setGroup(b.sampleGroup);
					o.setBiologicalSampleId(b.biologicalSampleId);
					o.setExternalSampleId(b.externalSampleId);

				}

				o.setObservationType(r.getString("observation_type"));

				String cat = r.getString("raw_category");
				if (!r.wasNull()) {

					String param = r.getString("parameter_stable_id");
					if (translateCategoryNames.containsKey(param)) {

						String transCat = translateCategoryNames.get(param).get(cat);
						if (transCat != null && !transCat.equals("")) {
							o.setCategory(transCat);
						} else {
							o.setCategory(cat);
						}

					} else {
						o.setCategory(cat);
					}
				}

				// Add the correct "data point" for the type
				switch (r.getString("observation_type")) {
					case "unidimensional":
						o.setDataPoint(r.getFloat("unidimensional_data_point"));
						break;
					case "multidimensional":
						o.setDataPoint(r.getFloat("multidimensional_data_point"));
						break;
					case "time_series":
						o.setDataPoint(r.getFloat("time_series_data_point"));
						break;
				}

				Integer order_index = r.getInt("order_index");
				if (!r.wasNull()) {
					o.setOrderIndex(order_index);
				}

				String dimension = r.getString("dimension");
				if (!r.wasNull()) {
					o.setDimension(dimension);
				}

				String time_point = r.getString("time_point");
				if (!r.wasNull()) {
					o.setTimePoint(time_point);
				}

				Float discrete_point = r.getFloat("discrete_point");
				if (!r.wasNull()) {
					o.setDiscretePoint(discrete_point);
				}

				String file_type = r.getString("file_type");
				if (!r.wasNull()) {
					o.setFileType(file_type);
				}

				String download_file_path = r.getString("download_file_path");
				if (!r.wasNull()) {
					o.setDownloadFilePath(download_file_path);
				}

				// 60 seconds between commits
				observationSolrServer.addBean(o, 60000);

				count++;

				if (count%100000 == 0) {
					logger.info(" added "+count+" beans");
				}

			}

			// Final commit to save the rest of the docs
			observationSolrServer.commit();

		} catch (Exception e) {
			logger.error("Big error {}", e.getMessage(), e);
		}

	}

	/**
	 * Add all the relevant data required quickly looking up biological data associated to a biological sample
	 *
	 * @throws SQLException when a database exception occurs
	 */
	public void populateBiologicalDataMap() throws SQLException {

		String query = "SELECT CAST(bs.id AS CHAR) as biological_sample_id, bs.organisation_id as phenotyping_center_id, " +
			"org.name as phenotyping_center_name, bs.sample_group, bs.external_id as external_sample_id, " +
			"ls.date_of_birth, ls.colony_id, ls.sex as sex, ls.zygosity, " +
			"bms.biological_model_id, " +
			"strain.acc as strain_acc, strain.name as strain_name, " +
			"(select distinct allele_acc from biological_model_allele bma WHERE bma.biological_model_id=bms.biological_model_id) as allele_accession, " +
			"(select distinct a.symbol from biological_model_allele bma INNER JOIN allele a on (a.acc=bma.allele_acc AND a.db_id=bma.allele_db_id) WHERE bma.biological_model_id=bms.biological_model_id)  as allele_symbol, " +
			"(select distinct gf_acc from biological_model_genomic_feature bmgf WHERE bmgf.biological_model_id=bms.biological_model_id) as acc, " +
			"(select distinct gf.symbol from biological_model_genomic_feature bmgf INNER JOIN genomic_feature gf on gf.acc=bmgf.gf_acc WHERE bmgf.biological_model_id=bms.biological_model_id)  as symbol " +
			"FROM biological_sample bs " +
			"INNER JOIN organisation org ON bs.organisation_id=org.id " +
			"INNER JOIN live_sample ls ON bs.id=ls.id " +
			"INNER JOIN biological_model_sample bms ON bs.id=bms.biological_sample_id " +
			"INNER JOIN biological_model_strain bmstrain ON bmstrain.biological_model_id=bms.biological_model_id " +
			"INNER JOIN strain strain ON strain.acc=bmstrain.strain_acc";

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
	 * Add all the relevant data required quickly looking up biological data associated to a biological model
	 * (really an experiment)
	 *
	 * @throws SQLException when a database exception occurs
	 */
	public void populateLineBiologicalDataMap() throws SQLException {

		String query = "SELECT e.id as experiment_id, e.colony_id, e.biological_model_id, " +
			"e.organisation_id as phenotyping_center_id, org.name as phenotyping_center_name, " +
			"strain.acc as strain_acc, strain.name as strain_name, " +
			"(select distinct allele_acc from biological_model_allele bma WHERE bma.biological_model_id=e.biological_model_id) as allele_accession, " +
			"(select distinct a.symbol from biological_model_allele bma INNER JOIN allele a on (a.acc=bma.allele_acc AND a.db_id=bma.allele_db_id) WHERE bma.biological_model_id=e.biological_model_id)  as allele_symbol, " +
			"(select distinct gf_acc from biological_model_genomic_feature bmgf WHERE bmgf.biological_model_id=e.biological_model_id) as acc, " +
			"(select distinct gf.symbol from biological_model_genomic_feature bmgf INNER JOIN genomic_feature gf on gf.acc=bmgf.gf_acc WHERE bmgf.biological_model_id=e.biological_model_id)  as symbol " +
			"FROM experiment e " +
			"INNER JOIN organisation org ON e.organisation_id=org.id " +
			"INNER JOIN biological_model_strain bm_strain ON bm_strain.biological_model_id=e.biological_model_id " +
			"INNER JOIN strain strain ON strain.acc=bm_strain.strain_acc";

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
	 * Add all the relevant data required for translating the category names in the cases where
	 * the category names are numerals, but the actual name is in the description field
	 *
	 * @throws SQLException when a database exception occurs
	 */
	public void populateCategoryNamesDataMap() throws SQLException {

		String query = "SELECT pp.stable_id, ppo.name, ppo.description FROM phenotype_parameter pp \n" +
			"INNER JOIN phenotype_parameter_lnk_option pplo ON pp.id=pplo.parameter_id\n" +
			"INNER JOIN phenotype_parameter_option ppo ON ppo.id=pplo.option_id \n" +
			"WHERE ppo.name NOT REGEXP '^[a-zA-Z]' AND ppo.description!=''";

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
	 * @throws SQLException when a database exception occurs
	 */
	public void populateImpressDataMap() throws SQLException {

		List<String> queries = new ArrayList<>();
		queries.add("SELECT id, name, stable_id, 'PIPELINE' as impress_type FROM phenotype_pipeline");
		queries.add("SELECT id, name, stable_id, 'PROCEDURE' as impress_type FROM phenotype_procedure");
		queries.add("SELECT id, name, stable_id, 'PARAMETER' as impress_type FROM phenotype_parameter");

		for (String query : queries) {

			try (PreparedStatement p = connection.prepareStatement(query)) {

				ResultSet resultSet = p.executeQuery();

				while (resultSet.next()) {

					ImpressBean b = new ImpressBean();

					b.id = resultSet.getInt("id");
					b.stableId = resultSet.getString("stable_id");
					b.name = resultSet.getString("name");

					switch (resultSet.getString("impress_type")) {
						case "PIPELINE":
							pipelineMap.put(resultSet.getInt("id"), b);
							break;
						case "PROCEDURE":
							procedureMap.put(resultSet.getInt("id"), b);
							break;
						case "PARAMETER":
							parameterMap.put(resultSet.getInt("id"), b);
							break;
					}
				}
			}
		}
	}


	public void populateDatasourceDataMap() throws SQLException {

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


	public Map<Integer, ImpressBean> getPipelineMap() {
		return pipelineMap;
	}


	public Map<Integer, DatasourceBean> getDatasourceMap() {
		return datasourceMap;
	}


	public Map<Integer, DatasourceBean> getProjectMap() {
		return projectMap;
	}


	public Map<Integer, ImpressBean> getProcedureMap() {
		return procedureMap;
	}


	public Map<Integer, ImpressBean> getParameterMap() {
		return parameterMap;
	}


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
	protected class ImpressBean {
		public Integer id;
		public String stableId;
		public String name;
	}

	/**
	 * Internal class to act as Map value DTO for datasource data
	 */
	protected class DatasourceBean {
		public Integer id;
		public String name;
	}
}
