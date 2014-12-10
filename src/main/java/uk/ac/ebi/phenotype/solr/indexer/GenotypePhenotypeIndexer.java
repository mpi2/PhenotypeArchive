package uk.ac.ebi.phenotype.solr.indexer;

import joptsimple.OptionSet;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import uk.ac.ebi.phenotype.service.dto.ObservationDTO;
import uk.ac.ebi.phenotype.solr.indexer.beans.OntologyTermBean;
import uk.ac.ebi.phenotype.solr.indexer.utils.IndexerMap;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Populate the Genotype-Phenotype core
 */

public class GenotypePhenotypeIndexer extends AbstractIndexer {

	private static final Logger logger = LoggerFactory.getLogger(GenotypePhenotypeIndexer.class);
	private static Connection connection;

	@Autowired
	@Qualifier("komp2DataSource")
	DataSource komp2DataSource;

	@Autowired
	@Qualifier("ontodbDataSource")
	DataSource ontodbDataSource;

	@Autowired
	@Qualifier("genotypePhenotypeIndexing")
	SolrServer gpSolrServer;

	Map<Integer, ImpressBean> pipelineMap = new HashMap<>();
	Map<Integer, ImpressBean> procedureMap = new HashMap<>();
	Map<Integer, ImpressBean> parameterMap = new HashMap<>();
	Map<String, List<OntologyTermBean>> mpTopTerms = new HashMap<>();
	Map<String, List<OntologyTermBean>> mpIntTerms = new HashMap<>();

	public GenotypePhenotypeIndexer() {
	}

	public void initialise(String[] args) throws IndexerException {
		OptionSet options = parseCommandLine(args);
		applicationContext = loadApplicationContext((String) options.valuesOf(CONTEXT_ARG).get(0));
		applicationContext.getAutowireCapableBeanFactory().autowireBeanProperties(this, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);
		initialiseHibernateSession(applicationContext);

		try {

			connection = komp2DataSource.getConnection();

			mpTopTerms = IndexerMap.getMpTopLevelTerms(ontodbDataSource.getConnection());
			mpIntTerms = IndexerMap.getMpIntermediateLevelTerms(ontodbDataSource.getConnection());

		} catch (SQLException e) {
			throw new IndexerException(e);
		}

		printConfiguration();

	}


	public static void main(String[] args) throws IndexerException {
		GenotypePhenotypeIndexer main = new GenotypePhenotypeIndexer();
		main.initialise(args);
		main.run();

		logger.info("Process finished.  Exiting.");
	}


	@Override
	protected Logger getLogger() {
		return logger;
	}


	public void run() throws IndexerException {

		Long start = System.currentTimeMillis();
		try {

			logger.info("Populating impress maps");
			populateImpressDataMap();

			logger.info("Populating genotype-phenotype solr core");
			populateGenotypePhenotypeSolrCore();

		} catch (SQLException | IOException | SolrServerException e) {
			throw new IndexerException(e);
		}

		logger.info("Populating experiment solr core - done [took: {}s]", (System.currentTimeMillis() - start) / 1000.0);
	}


	public void populateGenotypePhenotypeSolrCore() throws SQLException, IOException, SolrServerException {

		int count=0;

		gpSolrServer.deleteByQuery("*:*");

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

		try (PreparedStatement p = connection.prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {

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
				String procedureStableId = procedureMap.get(r.getInt("procedure_id")).stableId;
				o.setProcedureStableId(procedureStableId);
				o.setProcedureGroup(procedureStableId.substring(0, procedureStableId.lastIndexOf("_")));

				o.setPipelineId(pipelineMap.get(r.getInt("pipeline_id")).id);
				o.setPipelineName(pipelineMap.get(r.getInt("pipeline_id")).name);
				o.setPipelineStableId(pipelineMap.get(r.getInt("pipeline_id")).stableId);

				gpSolrServer.addBean(o);

				count++;

				if (count%1000 == 0) {
					logger.info(" added {} beans", count);
				}

			}

			// Final commit to save the rest of the docs
			gpSolrServer.commit();

		} catch (Exception e) {
			logger.error("Big error {}", e.getMessage(), e);
		}

	}





	/**
	 * Add all the relevant data to the Impress map
	 *
	 * @throws java.sql.SQLException when a database exception occurs
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




	public Map<Integer, ImpressBean> getPipelineMap() {
		return pipelineMap;
	}

	public Map<Integer, ImpressBean> getProcedureMap() {
		return procedureMap;
	}

	public Map<Integer, ImpressBean> getParameterMap() {
		return parameterMap;
	}



	/**
	 * Internal class to act as Map value DTO for impress data
	 */
	protected class ImpressBean {
		public Integer id;
		public String stableId;
		public String name;
	}


}
