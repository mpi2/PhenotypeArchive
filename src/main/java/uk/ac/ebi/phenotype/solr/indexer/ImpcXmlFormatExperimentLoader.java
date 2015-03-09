package uk.ac.ebi.phenotype.solr.indexer;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.common.StatusCode;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.*;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.Experiment;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.Procedure;
import org.mousephenotype.dcc.exportlibrary.xmlserialization.exceptions.XMLloadingException;
import org.mousephenotype.dcc.utils.xml.XMLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import uk.ac.ebi.phenotype.dao.*;
import uk.ac.ebi.phenotype.pojo.*;

import javax.sql.DataSource;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;

import static uk.ac.ebi.phenotype.data.imits.EncodedOrganisationConversionMap.dccCenterMap;
import static uk.ac.ebi.phenotype.solr.indexer.SqlUtils.setSqlParameter;

/**
 * Load experiment data that was encoded using the IMPC XML format
 */
@Component
public class ImpcXmlFormatExperimentLoader {

	private static final Logger logger = LoggerFactory.getLogger(ImpcXmlFormatExperimentLoader.class);

	private static final String analyticsQuery = "INSERT INTO analytics_experiment_load (filename, center_id, date_of_experiment, sequence_id, experiment_id, specimen_id, impress_pipeline, impress_procedure, impress_parameters, parameter_types, data_values, metadata_values, missing_data_values, status, message, additional_information) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	private static final String colonyMapQuery = "SELECT DISTINCT specimen_id, colony_id, mapped_project FROM analytics_specimen_load WHERE mapped_project != ''";

	private static final String CONTEXT_PATH = "org.mousephenotype.dcc.exportlibrary.datastructure.core.common:org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure:org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen:org.mousephenotype.dcc.exportlibrary.datastructure.tracker.submission:org.mousephenotype.dcc.exportlibrary.datastructure.tracker.validation";

	private static Connection connection;
	private static String filename;
	private static String datasourceName;
	private static Organisation phenotypingCenter;
	private static Pipeline pipeline;

	/**
	 * Local variables
	 */
	protected Map<String, Project> colonyProjectMap = new HashMap<>();
	protected Map<String, Project> specimenProjectMap = new HashMap<>();
	protected Map<String, Project> projects = new HashMap<>();
	protected Map<String, Pipeline> pipelines = new HashMap<>();
	protected Map<String, uk.ac.ebi.phenotype.pojo.Procedure> procedures = new HashMap<>();
	protected Map<String, uk.ac.ebi.phenotype.pojo.Parameter> parameters = new HashMap<>();
	protected Map<String, String> parameterIncrementUnit = new HashMap<>();
	List<Observation> observations = new ArrayList<>();
	private static Map<String, Integer> centerMap = new HashMap<>();
	static {
		// Provided by MRC Harwell
		centerMap.put("HMGU", 1);
		centerMap.put("MRC Harwell", 2);
		centerMap.put("WTSI", 3);
		centerMap.put("ICS", 4);
	}
	Map<String, LiveSample> specimens = new HashMap<>();
	boolean bSkipExperiment = false;
	private Integer unknownAnimals = 0;
	private Integer parsed = 0;
	private Integer skipped = 0;

	/**
	 * Spring auto wired dependencies
	 */
	@Autowired private DatasourceDAO dsDAO;
	@Autowired private OrganisationDAO orgDAO;
	@Autowired private BiologicalModelDAO biologicalModelDAO;
	@Autowired private PhenotypePipelineDAO pipelineDAO;
	@Autowired private ProjectDAO projectDAO;
	@Autowired private ObservationDAO observationDAO;
	@Autowired private LoadImagesDao loadImagesDao;


	public static void main(String[] args)
		throws InstantiationException, IOException, SQLException, InterruptedException, ExecutionException, GenericRServicesException, SolrServerException, JAXBException, XMLloadingException, NoSuchAlgorithmException, KeyManagementException {

		OptionParser parser = new OptionParser();

		// parameter to indicate the name of the file to process
		parser.accepts("filename").withRequiredArg().ofType(String.class);

		// parameter to indicate the short name of the datasource
		parser.accepts("datasource").withRequiredArg().ofType(String.class);

		// parameter to indicate which spring context file to use
		parser.accepts("context").withRequiredArg().ofType(String.class);

		OptionSet options = parser.parse(args);
		filename = (String) options.valuesOf("filename").get(0);
		datasourceName = (String) options.valuesOf("datasource").get(0);
		String context = (String) options.valuesOf("context").get(0);

		logger.info("Loading experiments file {}", filename);
		logger.info("Using application context file {}", context);

		// Wire up spring support for this application
		ImpcXmlFormatExperimentLoader main = new ImpcXmlFormatExperimentLoader();

		ApplicationContext applicationContext;
		try {

			// Try context as a file resource
			applicationContext = new FileSystemXmlApplicationContext("file:" + context);

		} catch (RuntimeException e) {

			// Try context as a class path resource
			applicationContext = new ClassPathXmlApplicationContext(context);

		}
		applicationContext.getAutowireCapableBeanFactory().autowireBeanProperties(main, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);

		PlatformTransactionManager transactionManager = (PlatformTransactionManager) applicationContext.getBean("transactionManager");
		DefaultTransactionAttribute transactionAttribute = new DefaultTransactionAttribute(TransactionDefinition.PROPAGATION_REQUIRED);
		transactionAttribute.setIsolationLevel(TransactionDefinition.ISOLATION_SERIALIZABLE);
		transactionManager.getTransaction(transactionAttribute);

		DataSource ds = ((DataSource) applicationContext.getBean("komp2DataSource"));
		connection = ds.getConnection();

		main.run();

		logger.info("Process finished.  Exiting.");

	}


	public void run() throws IOException, JAXBException, SQLException, InterruptedException {

		long start = System.currentTimeMillis();
		loadAllPipelinesByStableIds();
		logger.info("Preparing pipeline/procedure/parameter maps - complete (took {}s)", ((System.currentTimeMillis() - start) / 1000.0));

		start = System.currentTimeMillis();
		for (Project p : projectDAO.getAllProjects()) {

			// Translate all project names to upper case for easy mapping
			projects.put(p.getName().toUpperCase(), p);

			// Also add to the map translations from legacy iMits project names
			if (p.getName().equals("MGP")) {
				projects.put("MGP Legacy", p);
				projects.put("MGP LEGACY", p);
			}

			if (p.getName().equals("EUMODIC")) {
				projects.put("EUCOMM-EUMODIC", p);
			}
		}
		logger.debug("Preparing project map - complete (took {}s)", ((System.currentTimeMillis() - start) / 1000.0));

		start = System.currentTimeMillis();
		loadColonyIdToProjectMapping();
		logger.debug("Preparing colony to project re-map - complete (took {}s)", ((System.currentTimeMillis() - start) / 1000.0));

		start = System.currentTimeMillis();
		Datasource globalDatasource = dsDAO.getDatasourceByShortName(datasourceName);
		logger.debug("Loading data for datasource {} - complete (took {}s)", datasourceName, ((System.currentTimeMillis() - start) / 1000.0));

		// Cache the MGP datasource to support remapping projects indicated in imits
		// as MGP projects to the correct datasource
		Datasource mgpDatasource =  dsDAO.getDatasourceByShortName("MGP");

		List<CentreProcedure> centreProcedures = XMLUtils.unmarshal(ImpcXmlFormatExperimentLoader.CONTEXT_PATH, CentreProcedureSet.class, filename).getCentre();

		// Persist errors to display at the end
		Set<String> possibleErrors = new TreeSet<>();

		for (CentreProcedure centreProcedure : centreProcedures) {

			// Default datasource is the one passed in on the command line
			Datasource datasource = globalDatasource;

			// get the project name
			String projectName = centreProcedure.getProject().toUpperCase();


			// get the phenotypingCenter
			String centreID = centreProcedure.getCentreID().name();
			// Map the DCC center name to the CDA name
			centreID = getCdaOrganisationName(centreID);
			logger.debug("Loading data for phenotypingCenter {}", centreID);
			phenotypingCenter = orgDAO.getOrganisationByName(centreID);
			if (phenotypingCenter == null) {
				logger.error("phenotypingCenter can't be found. Skipping %s", centreID);
				continue;
			}

			// get the pipeline
			String pipelineName = centreProcedure.getPipeline();
			logger.debug("Loading data for pipeline {}", pipelineName);
			pipeline = pipelines.get(pipelineName);
			if (pipeline == null) {
				logger.error("Pipeline can't be found. Skipping {}", pipelineName);
				continue;
			}

			loadAllAnimals(phenotypingCenter);

			//
			// Line level procedures
			//

			for (Line line : centreProcedure.getLine()) {

				logger.debug("Loading data for project {}", projectName);
				Project project = projects.get(projectName);
				if (project == null) {
					logger.error("project can't be found. Skipping line colony id {} for project {}", line.getColonyID(), projectName);
					continue;
				}

				if( colonyProjectMap.containsKey(line.getColonyID()) && ! project.equals(colonyProjectMap.get(line.getColonyID()))) {

					logger.info("Remapping colony "+line.getColonyID()+" project association from "+projectName+" to "+ (colonyProjectMap.containsKey(line.getColonyID()) ? colonyProjectMap.get(line.getColonyID()).getName() : "(colony not found)"));

					if ( ! colonyProjectMap.containsKey(line.getColonyID())) {
						String errMsg = String.format("Cannot find the project for: %s", line.getColonyID());
						logger.warn(errMsg);
						recordAnalytics(line, "Skipped - Missing line level project", errMsg, null);
						continue;
					}

					// Remap this line to MGP from EUMODIC/EuroPhenome
					project = colonyProjectMap.get(line.getColonyID());
					datasource = mgpDatasource;

				}


				Procedure impressProcedure = line.getProcedure();
				String procedureName = impressProcedure.getProcedureID();
				uk.ac.ebi.phenotype.pojo.Procedure procedure = procedures.get(procedureName);//pipelineDAO.getProcedureByStableId(procedureName);

				// Fail fast on parameter types we do not import yet
				// Only type we currently import are simple parameters
				if (!impressProcedure.getMediaParameter().isEmpty()) {
					String errMsg = String.format("We don't process line level MediaParameter: %s", procedureName);
					logger.warn(errMsg);
					recordAnalytics(line, "Skipped - Inappropriate line level parameter type", errMsg, null);
					bSkipExperiment = true;
				}
				if (!impressProcedure.getMediaSampleParameter().isEmpty()) {
					String errMsg = String.format("We don't process line level MediaSampleParameter: %s", procedureName);
					logger.warn(errMsg);
					recordAnalytics(line, "Skipped - Inappropriate line level parameter type", errMsg, null);
					bSkipExperiment = true;
				}
				if (!impressProcedure.getOntologyParameter().isEmpty()) {
					String errMsg = String.format("We don't process line level OntologyParameter: %s", procedureName);
					logger.warn(errMsg);
					recordAnalytics(line, "Skipped - Inappropriate line level parameter type", errMsg, null);
					bSkipExperiment = true;
				}
				if (!impressProcedure.getSeriesMediaParameter().isEmpty()) {
					String errMsg = String.format("We don't process line level SeriesMediaParameter: %s", procedureName);
					logger.warn(errMsg);
					recordAnalytics(line, "Skipped - Inappropriate line level parameter type", errMsg, null);
					bSkipExperiment = true;
				}
				if (!impressProcedure.getSeriesParameter().isEmpty()) {
					String errMsg = String.format("We don't process line level SeriesParameter: %s", procedureName);
					logger.warn(errMsg);
					recordAnalytics(line, "Skipped - Inappropriate line level parameter type", errMsg, null);
					bSkipExperiment = true;
				}


				if (bSkipExperiment) {

					skipped++;

				} else {

					logger.debug("fertility and viability data procedure {}", procedureName);

					String experimentID = String.format("%s-%s", line.getProcedure().getProcedureID(), line.getColonyID());
					logger.debug("constructed experiment ID {}", experimentID);

					BiologicalModel associatedModel;
					try {

						associatedModel = getBaseBiologicalModelByColonyId(line.getColonyID(), impressProcedure.getSimpleParameter(), datasource);

					} catch (BiologicalModelNotFoundException | SQLException e) {
						logger.error("Cannot find or create biological model for line level parameter for colony {}", line.getColonyID());
						recordAnalytics(line, "Skipped - No biological model", null, ExceptionUtils.getFullStackTrace(e));
						continue;
					}

					// Create a CDA experiment to collect this data
					uk.ac.ebi.phenotype.pojo.Experiment currentExperiment = new uk.ac.ebi.phenotype.pojo.Experiment();
					currentExperiment.setDatasource(datasource);
					currentExperiment.setDateOfExperiment(null);
					currentExperiment.setExternalId(experimentID);
					currentExperiment.setModel(associatedModel);
					currentExperiment.setColonyId(line.getColonyID());
					currentExperiment.setOrganisation(phenotypingCenter);
					currentExperiment.setProject(project);
					currentExperiment.setPipeline(pipeline);
					currentExperiment.setPipelineStableId(pipeline.getStableId());
					currentExperiment.setProcedure(procedure);
					currentExperiment.setProcedureStableId(procedure.getStableId());

					// Add status codes to the experiment
					for (StatusCode statusCode : line.getStatusCode()) {

						String code = statusCode.getValue();

						if (code.contains(":")) {

							String message = code.substring(code.indexOf(":") + 1, code.length()).trim();
							currentExperiment.setProcedureStatusMessage(message);

							code = code.substring(0, code.indexOf(":"));

						}

						currentExperiment.setProcedureStatus(code);

					}
					observationDAO.saveExperiment(currentExperiment);

					loadImagesDao.setProcedure(impressProcedure);
					loadImagesDao.setExperiment(currentExperiment);

					List<SimpleParameter> simpleParameters = impressProcedure.getSimpleParameter();

					// Save the parameter information
					for (SimpleParameter simpleParameter : simpleParameters) {

						// get the parameter ID fo a simple parameter
						String parameterID = simpleParameter.getParameterID();

						//need suffix here but not on other params?
						Parameter parameter = parameters.get(parameterID);
						if (parameter == null) {
							logger.error("Parameter object for id {} cannot be found", parameterID);
							continue;
						}

						String simpleValue = simpleParameter.getValue();
						if (simpleValue == null) {
							simpleValue = "null";//done this to mimick the behaviour of the sax parser when this is null! check this ok - doesn't throw null pointer error anymore
						}
						ObservationType observationType = uk.ac.ebi.phenotype.data.impress.Utilities.checkType(parameter, simpleValue);

						// then depending on the type, create the relevant information
						Observation observation = observationDAO.createSimpleObservation(observationType, simpleValue, parameter, null, datasource, currentExperiment, simpleParameter.getParameterStatus());
						observations.add(observation);


					}

					// Save the metadata for this experiment
					for (ProcedureMetadata procedureMetadata : impressProcedure.getProcedureMetadata()) {
						try {

							loadImagesDao.loadProcedureMetaData(connection, procedureMetadata, 0);

						} catch (SQLException e) {
							logger.error(ExceptionUtils.getFullStackTrace(e));

							recordAnalytics(line, "Error - metadata", null, ExceptionUtils.getFullStackTrace(e));
                            break;

						}
					}

                    recordAnalytics(line, "Success", null, null);

				} // end of skip experiment
				bSkipExperiment = false;

			}


			//
			// Experiment level procedures
			//

			for (Experiment experiment : centreProcedure.getExperiment()) {

				logger.debug("Loading data for project {}", projectName);
				Project project = projects.get(projectName);
				if (project == null) {
					logger.warn("project can't be found. Skipping experiment id {} for project {}", experiment.getExperimentID(), projectName);
					continue;
				}

				// IMPReSS supports multiple specimens per experiment but we do not
				// We fail fast when getting multiple specimens or when a specimen can't be found
				if (experiment.getSpecimenID().isEmpty()) {
					String errMsg = String.format("No specimen for experiment %s", experiment.getExperimentID());
					logger.warn(errMsg);
					recordAnalytics(experiment, "Skipped - Missing specimen", errMsg, null);
					continue;
				}
				if (experiment.getSpecimenID().size() > 1) {
					String errMsg = String.format("Cannot find unique specimen for experiment %s", experiment.getExperimentID());
					logger.warn(errMsg);
					recordAnalytics(experiment, "Skipped - Multiple specimens", errMsg, null);
					continue;
				}

				// Else get the first (and only) specimen
				String specimenId = experiment.getSpecimenID().get(0);

				if( specimenProjectMap.get(specimenId) != null && ! project.equals(specimenProjectMap.get(specimenId))) {

					logger.debug("Remapping experiment for specimen "+experiment.getSpecimenID()+" project association from "+projectName+" to "+ specimenProjectMap.get(specimenId).getName());

					if ( ! specimenProjectMap.containsKey(specimenId)) {
						String errMsg = String.format("Cannot find the project for specimen %s", specimenId);
						logger.info(errMsg);
						recordAnalytics(experiment, "Skipped - Missing experiment level project", errMsg, null);
						continue;
					}

					// Remap this specimen to MGP from EUMODIC/EuroPhenome
					project = specimenProjectMap.get(specimenId);
					datasource = mgpDatasource;

				}

				String experimentID = experiment.getExperimentID();
				String sequenceID = experiment.getSequenceID();
				Procedure procedure = experiment.getProcedure();

				String procedureName = experiment.getProcedure().getProcedureID();
				uk.ac.ebi.phenotype.pojo.Procedure proc = procedures.get(procedureName);

				if (proc == null) {
					// Procedure missing in IMPReSS
					logger.error("Experiment ID {} is associated to IMPReSS procedure {} which does not exist in the IMPC database. Skipping.", experimentID, procedureName);
					bSkipExperiment = true;
					recordAnalytics(experiment, "Skipped - Invalid IMPReSS procedure", "Experiment  " + experimentID + " has procedure " + procedure.getProcedureID(), null);
					continue;
				}

				//new meta data loading
				if (experiment.getStatusCode().size() > 1) {
					String errorString = "More than one procedure status code for this experiment:" + experimentID;
					possibleErrors.add(errorString);
				}

				// check dates match up as converting to a string?
				// Use date parsed by Dom object
				Date dateOfExperiment = experiment.getDateOfExperiment().getTime();

				// Skip the experiment if the date is bad
				try {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					Date date = sdf.parse("1975-01-01");// groovy baby, yeah.

					if (dateOfExperiment.before(date)) {

						logger.warn("Skipping experiment '{}' due to invalid date {}", experimentID, dateOfExperiment);

						bSkipExperiment = true;
						recordAnalytics(experiment, "Skipped - Invalid date", "Experiment  " + experimentID + " has date " + dateOfExperiment, null);
						continue;

					}
				} catch (Exception e) {

					bSkipExperiment = true;
					recordAnalytics(experiment, "Skipped - Invalid date", "Experiment  " + experimentID + " has date " + dateOfExperiment, null);
					continue;

				}

				uk.ac.ebi.phenotype.pojo.Experiment currentExperiment = new uk.ac.ebi.phenotype.pojo.Experiment();

				currentExperiment.setDatasource(datasource);
				currentExperiment.setDateOfExperiment(dateOfExperiment);
				currentExperiment.setExternalId(experimentID);
				currentExperiment.setOrganisation(phenotypingCenter);
				currentExperiment.setProject(project);
				currentExperiment.setSequenceId(sequenceID);

				// Add the pipeline to the experiment
				currentExperiment.setPipeline(pipeline);
				currentExperiment.setPipelineStableId(pipeline.getStableId());

				// Add the procedure to the experiment
				currentExperiment.setProcedure(proc);
				currentExperiment.setProcedureStableId(proc.getStableId());

				// Add the status code to the experiment
				for (StatusCode statusCode : experiment.getStatusCode()) {

					String code = statusCode.getValue();

					if (code.contains(":")) {

						String message = code.substring(code.indexOf(":") + 1, code.length()).trim();
						currentExperiment.setProcedureStatusMessage(message);

						code = code.substring(0, code.indexOf(":"));

					}

					currentExperiment.setProcedureStatus(code);

				}

				if (experiment.getSpecimenID().size() > 1) {
					logger.warn("warning experiment {} contains more than 1 specimen id can we handle these?", experimentID);
				}

				String rawSampleID = experiment.getSpecimenID().get(0);
				// check - can we have more than one specimen id per
				// experiment DOM suggests we can?
				// for Wtsi animal we have to remove _WTSI
				String sampleID = rawSampleID.replaceAll("_WTSI", "");

				LiveSample specimen;
				// check the sample exists
				if (!specimens.containsKey(sampleID)) {

					bSkipExperiment = true;
					recordAnalytics(experiment, "Skipped - Unknown specimen", "Specimen "+ sampleID + "not found in database", null);
					unknownAnimals++;
					continue;

				} else {

					specimen = specimens.get(sampleID);
					try {

						observationDAO.saveExperiment(currentExperiment);

					} catch (Exception e) {

						bSkipExperiment = true;
						recordAnalytics(experiment, "Skipped - Error", "An error occurred saving experiment  " + experimentID, ExceptionUtils.getFullStackTrace(e));
						continue;

					}


				}

				if (bSkipExperiment) {

					recordAnalytics(experiment, "Skipped", "Experiment " + experimentID + " was skipped", null);
					skipped++;

				} else {

					// Initialize the loadImagesDAO with the correct procedure and experiment
					// TODO: change the way loadImagesDao works to accept procedure and experiment as arguments
					loadImagesDao.setProcedure(procedure);
					loadImagesDao.setExperiment(currentExperiment);

					for (SimpleParameter simpleParameter : procedure.getSimpleParameter()) {

						// get the parameter ID fo a simple parameter
						String parameterID = simpleParameter.getParameterID();

						//need suffix here but not on other params?
						Parameter parameter = parameters.get(parameterID);
                        if (parameter == null) {
                            logger.error("Parameter object for id {} cannot be found", parameterID);
                            continue;
                        }

						String simpleValue = simpleParameter.getValue();
						if (simpleValue == null) {
							simpleValue = "null";//done this to mimick the behaviour of the sax parser when this is null! check this ok - doesn't throw null pointer error anymore
						}
						ObservationType observationType = uk.ac.ebi.phenotype.data.impress.Utilities.checkType(parameter, simpleValue);

						// then depending on the type, create the relevant information
						Observation observation = observationDAO.createSimpleObservation(observationType, simpleValue, parameter, specimen, datasource, currentExperiment, simpleParameter.getParameterStatus());
						observations.add(observation);

					}


					for (SeriesParameter seriesParameter : procedure.getSeriesParameter()) {

						for (SeriesParameterValue sParameterValue : seriesParameter.getValue()) {

							// new meta data loading
							String parameterID = seriesParameter.getParameterID();

							Parameter parameter = parameters.get(parameterID);//pipelineDAO.getParameterByStableId(parameterID);

							// first check the parameter data type
							String incrementValue = sParameterValue.getIncrementValue();
							String simpleValue = sParameterValue.getValue();
							ObservationType observationType = uk.ac.ebi.phenotype.data.impress.Utilities.checkType(parameter, simpleValue);
							String[] units = parameter.checkParameterUnits();

							Observation observation;
							if (incrementValue.contains("-") && incrementValue.contains(" ")) {
								String discreteTimepoint = Float.toString(convertTimepoint(incrementValue, phenotypingCenter));
								observation = observationDAO.createTimeSeriesObservationWithOriginalDate(observationType, simpleValue, discreteTimepoint, incrementValue, units[0], parameter, specimen, datasource, currentExperiment, seriesParameter.getParameterStatus());
							} else {
								observation = observationDAO.createObservation(observationType, simpleValue, incrementValue, units[0], parameter, specimen, datasource, currentExperiment, seriesParameter.getParameterStatus());
							}
							if (observation != null) {
								observations.add(observation);
							}

						}

					}

					for (MediaParameter mediaParameter : procedure.getMediaParameter()) {

						String parameterID = mediaParameter.getParameterID();
						Parameter parameter = parameters.get(parameterID);

						try {

							loadImagesDao.loadMediaParameter(connection, mediaParameter, parameter, specimen, datasource);

						} catch (SQLException e) {

							logParameterError(centreID, experimentID, parameterID, mediaParameter.getParameterStatus());
							logger.error(ExceptionUtils.getFullStackTrace(e));
							recordAnalytics(experiment, "Error", "Error loading observation for experiment "+experimentID+"parameter "+parameterID, ExceptionUtils.getFullStackTrace(e));

						}

					}

					for (SeriesMediaParameter seriesMediaParameter : procedure.getSeriesMediaParameter()) {

						String parameterID = seriesMediaParameter.getParameterID();
						Parameter parameter = parameters.get(parameterID);

						try {

							loadImagesDao.loadSeriesMediaParameter(connection, seriesMediaParameter, parameter, specimen, datasource);

						} catch (SQLException e) {

							logParameterError(centreID, experimentID, parameterID, seriesMediaParameter.getParameterStatus());
							logger.error(ExceptionUtils.getFullStackTrace(e));
							recordAnalytics(experiment, "Error", "Error loading observation for experiment " + experimentID + "parameter " + parameterID, ExceptionUtils.getFullStackTrace(e));

						}


					}
					for (MediaSampleParameter mediaSampleParameter : procedure.getMediaSampleParameter()) {

						String parameterID = mediaSampleParameter.getParameterID();
						Parameter parameter = parameters.get(parameterID);
						try {

							loadImagesDao.loadMediaSampleParameter(connection, mediaSampleParameter, parameter, specimen, datasource);

						} catch (SQLException e) {

							logParameterError(centreID, experimentID, parameterID, mediaSampleParameter.getParameterStatus());
							logger.error(ExceptionUtils.getFullStackTrace(e));
							recordAnalytics(experiment, "Error", "Error loading observation for experiment " + experimentID + "parameter " + parameterID, ExceptionUtils.getFullStackTrace(e));

						}


					}
					for (OntologyParameter ontologyParameter : procedure.getOntologyParameter()) {

						String parameterID = ontologyParameter.getParameterID();

						possibleErrors.add("Skipping ontology parameter: " + parameterID);

						if (ontologyParameter.getParameterStatus() != null) {
							String errorString = "parameterStatus in ontologyParameter id=" + parameterID + " paramStatus=" + ontologyParameter.getParameterStatus();
							possibleErrors.add(errorString);
						}

					}
					for (ProcedureMetadata procedureMetadata : procedure.getProcedureMetadata()) {

						try {
							loadImagesDao.loadProcedureMetaData(connection, procedureMetadata, 0);
						} catch (SQLException e) {
							logger.error(ExceptionUtils.getFullStackTrace(e));
							recordAnalytics(experiment, "Error", "Error loading observation for experiment " + experimentID + "parameter " + procedureMetadata.getParameterID(), ExceptionUtils.getFullStackTrace(e));
						}

					}
					parsed++;//increment the number of experiments parsed
					if (parsed % 1000 == 0) {
						logger.info("parsed exp=" + parsed);
					}

					recordAnalytics(experiment, "Success", null, null);

				}//end of skip experiment


				bSkipExperiment = false;
			}
		}

		start = System.currentTimeMillis();
		for (Observation observation : observations) {
			observationDAO.saveObservation(observation);
		}
		logger.info("Saving {} observations - complete (took {}s)", observations.size(), ((System.currentTimeMillis()-start)/1000.0));



		for (String error : possibleErrors) {
			logger.error(error);
		}
		logger.info("Parsed experiments: {}", parsed);
		logger.info("Skipped experiments: {}", skipped);
		logger.info("Missing animals: {}", unknownAnimals);

	}


	/**
	 * Gets or creates a biological model without an allelic composition and a not applicable zygosity
	 * for use with line level parameters
	 *
	 * @param colonyID the colony for which to retrieve the biological model
	 * @return a biological model object
	 * @throws SQLException
	 */
	private BiologicalModel getBaseBiologicalModelByColonyId(String colonyID, List<SimpleParameter> simpleParameters, Datasource datasource) throws SQLException, BiologicalModelNotFoundException {

		// Default zygosity is homozygous since most of the time this will be the case
		ZygosityType zygosity = ZygosityType.homozygote;

		// Check if Hemizygote
		for (SimpleParameter param : simpleParameters) {

			// Find the associated "Outcome" parameter
			if (param.getParameterID().equals("IMPC_VIA_001_001")) {

				// Found the outcome parameter, check zygosity
				String category = param.getValue();

				if (category != null && category.contains("Hemizygous")) {
					zygosity = ZygosityType.hemizygote;
				}

				break;
			}

		}

		String query = "SELECT bm.id FROM biological_model bm " +
			"INNER JOIN experiment e ON bm.id=e.biological_model_id " +
			"WHERE bm.allelic_composition !='' AND e.colony_id = ? AND bm.zygosity = ? AND bm.db_id = ? LIMIT 1";


		try (PreparedStatement statement = connection.prepareStatement(query)) {

			statement.setString(1, colonyID);
			statement.setString(2, zygosity.getName());
			statement.setInt(3, datasource.getId());

			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {

				BiologicalModel bm = biologicalModelDAO.getBiologicalModelById(resultSet.getInt("id"));

				if (bm != null) {
					// Base Biological model found

					logger.info("Using existing line level biological model for colony {}", colonyID);

					return bm;
				}
			}

		}

		// Create an appropriate biological model based on an existing one

		// Query out an existing biological model for this colony
		// Any biological model will do since we are interested in the strain, gene, allele
		// associations, we'll set the zygosity
		query = "SELECT bm.id FROM biological_model bm " +
			"INNER JOIN biological_model_sample bms ON bm.id=bms.biological_model_id " +
			"INNER JOIN live_sample ls ON bms.biological_sample_id=ls.id " +
			"WHERE bm.allelic_composition !='' AND colony_id = ? AND bm.db_id = ? LIMIT 1";

		try (PreparedStatement statement = connection.prepareStatement(query)) {
			statement.setString(1, colonyID);
			statement.setInt(2, datasource.getId());

			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {

				BiologicalModel bm = biologicalModelDAO.getBiologicalModelById(resultSet.getInt("id"));

				if (bm == null) {
					// Something wrong... there is no existing biological model for this colony ID!
					throw new BiologicalModelNotFoundException("Biological model not found");
				}

				logger.info("Creating new line level biological model for colony {}", colonyID);

				// Copy associations from the existing biological model when appropriate.
				// Save and return the new model
				BiologicalModel newBm = new BiologicalModel();
				newBm.setAllelicComposition(bm.getAllelicComposition());
				newBm.setGeneticBackground(bm.getGeneticBackground());

				// Set the appropriate zygosity for the line
				newBm.setZygosity(zygosity.getName());

				newBm.setDatasource(bm.getDatasource());

				for (Allele a : bm.getAlleles()) {
					newBm.addAllele(a);
				}

				for (Strain s : bm.getStrains()) {
					newBm.addStrain(s);
				}

				for (GenomicFeature gf : bm.getGenomicFeatures()) {
					newBm.addGenomicFeature(gf);
				}

				biologicalModelDAO.saveBiologicalModel(newBm);

				// Return on the first valid result set since we don't care which
				// existing model we're copying -- they're all the same gene/allele/strain!
				return newBm;
			}

		}

        logger.error("Cannot find existing biological model for line level parameter for colony {}", colonyID);
        return null;
	}


	/**
	 * Record the attempt to load the data into the experiment analytics tables
	 *
	 * @param experimentOrLine either and experiment or a line could trigger the analytics
	 * @param status the status of the insert
	 * @param message a brief message to provide more information about the status of the data load
	 * @param additionalInformation large messages (i.e. stack traces) about the status of the data load
	 * @throws SQLException when the database could not insert the data point
	 */
	private void recordAnalytics(Object experimentOrLine, String status, String message, String additionalInformation)
		throws SQLException {

		Procedure procedure;
		String dateOfExperiment;
		String sequenceId;
		String experimentId;
		String specimenId;

		if(experimentOrLine.getClass().equals(Experiment.class)) {
			Experiment eol = (Experiment)experimentOrLine;
			procedure = eol.getProcedure();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			dateOfExperiment = sdf.format(eol.getDateOfExperiment().getTime());
			sequenceId = eol.getSequenceID();
			experimentId = eol.getExperimentID();
			specimenId = eol.getSpecimenID().get(0);
		} else {
			Line eol = (Line)experimentOrLine;
			procedure = eol.getProcedure();
			dateOfExperiment = null;
			sequenceId = eol.getSequenceID();
			experimentId = String.format("%s-%s", eol.getProcedure().getProcedureID(), eol.getColonyID());
			specimenId = null;
		}

        if(sequenceId == null) {
            sequenceId = "";
        }


		List<Object> parameters = new ArrayList<>();
		parameters.addAll(procedure.getSimpleParameter());
		parameters.addAll(procedure.getSeriesParameter());
		parameters.addAll(procedure.getSeriesMediaParameter());
		parameters.addAll(procedure.getMediaParameter());
		parameters.addAll(procedure.getMediaSampleParameter());
		parameters.addAll(procedure.getOntologyParameter());
		parameters.addAll(procedure.getProcedureMetadata());

		Set<String> parameterTypes = new HashSet<>();
		Set<String> parameterIds = new TreeSet<>();
		Integer parameterValues = 0;
		Integer metadataValues = 0;
		Integer missing = 0;

		for(Object parameter : parameters) {

			switch (parameter.getClass().getName()) {
				case "org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.SimpleParameter":
					parameterTypes.add("SimpleParameter");
					parameterIds.add(((SimpleParameter) parameter).getParameterID());
					if(((SimpleParameter) parameter).getParameterStatus() == null) {
						parameterValues += 1;
					} else {
						missing += 1;
					}
					break;
				case "org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.SeriesParameter":
					parameterTypes.add("SeriesParameter");
					parameterIds.add(((SeriesParameter) parameter).getParameterID());
					if(((SeriesParameter) parameter).getParameterStatus() == null) {
						parameterValues += ((SeriesParameter) parameter).getValue().size();
					} else {
						missing += 1;
					}
					break;
				case "org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.SeriesMediaParameter":
					parameterTypes.add("SeriesMediaParameter");
					parameterIds.add(((SeriesMediaParameter) parameter).getParameterID());
					if(((SeriesMediaParameter) parameter).getParameterStatus() == null) {
						parameterValues += ((SeriesMediaParameter) parameter).getValue().size();
					} else {
						missing += 1;
					}
					break;
				case "org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.MediaParameter":
					parameterTypes.add("MediaParameter");
					parameterIds.add(((MediaParameter) parameter).getParameterID());
					if(((MediaParameter) parameter).getParameterStatus() == null) {
						parameterValues += 1;
					} else {
						missing += 1;
					}
					break;
				case "org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.MediaSampleParameter":
					parameterTypes.add("MediaSampleParameter");
					parameterIds.add(((MediaSampleParameter) parameter).getParameterID());
					if(((MediaSampleParameter) parameter).getParameterStatus() == null) {
						parameterValues += ((MediaSampleParameter) parameter).getMediaSample().size();
					} else {
//						missing += ((MediaSampleParameter) parameter).getMediaSample().size();
						missing += 1;
					}
					break;
				case "org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.OntologyParameter":
					parameterTypes.add("OntologyParameter");
					parameterIds.add(((OntologyParameter) parameter).getParameterID());
					if(((OntologyParameter) parameter).getParameterStatus() == null) {
						parameterValues += ((OntologyParameter) parameter).getTerm().size();
					} else {
//						missing += ((OntologyParameter) parameter).getTerm().size();
						missing += 1;
					}
					break;
				case "org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.ProcedureMetadata":
					parameterTypes.add("ProcedureMetadata");
					parameterIds.add(((ProcedureMetadata) parameter).getParameterID());
					if(((ProcedureMetadata) parameter).getParameterStatus() == null) {
						metadataValues += 1;
					}
					break;
				default:
					break;
			}

		}

		try (PreparedStatement p = connection.prepareStatement(analyticsQuery)) {
			// filename, center_id, date_of_experiment,
			// sequence_id, experiment_id, specimen_id,
			// impress_pipeline, impress_procedure, impress_parameter,
			// parameter_type, data_values, missing, status,
			// message, additional_information
			Integer i = 1;
			setSqlParameter(p, filename, i++);
			setSqlParameter(p, phenotypingCenter.getName(), i++);
			setSqlParameter(p, dateOfExperiment, i++);
			setSqlParameter(p, sequenceId, i++);
			setSqlParameter(p, experimentId, i++);
			setSqlParameter(p, specimenId, i++);
			setSqlParameter(p, pipeline.getName(), i++);
			setSqlParameter(p, procedure.getProcedureID(), i++);
			setSqlParameter(p, StringUtils.join(parameterIds, ", "), i++);
			setSqlParameter(p, StringUtils.abbreviate(StringUtils.join(parameterTypes, ", "), 255), i++);
			setSqlParameter(p, parameterValues, i++);
			setSqlParameter(p, metadataValues, i++);
			setSqlParameter(p, missing, i++);
			setSqlParameter(p, status, i++);
			setSqlParameter(p, message, i++);
			setSqlParameter(p, additionalInformation, i); // last field
			p.executeUpdate();
		}

	}


	/**
	 * Map the supplied organisation name to the CDA approved name
	 *
	 * @param orgName the organisation name to map
	 * @return the mapped organisation name
	 */
	public String getCdaOrganisationName(String orgName) {

		String upperOrgName = orgName.toUpperCase();

		if (dccCenterMap.containsKey(upperOrgName)) {
			upperOrgName = dccCenterMap.get(upperOrgName).toUpperCase();
		}

		return upperOrgName;
	}


	/**
	 * Populate the specimen map for fast lookup of biological samples to attach the data.
	 *
	 * @param organisation the organisation for which the data is being loaded
	 */
	private void loadAllAnimals(Organisation organisation) {
		if (specimens.size() < 1) {
			List<LiveSample> animalList = biologicalModelDAO.getAllLiveSampleByOrganisation(organisation);
			for (LiveSample animal : animalList) {
				specimens.put(animal.getStableId(), animal);
			}
		}
	}
	private void logParameterError(String centreID, String experimentID, String parameterID,
	                               String parameterStatusString) {
		String errorString="center="+centreID+"experimentId="+experimentID+"parameterId="+parameterID+" parameterStatus="+parameterStatusString+" paramStatusDescription="+ ParameterStatusMap.getParameterStatusDescription(parameterStatusString);
		logger.error(errorString);
	}


	/**
	 * Convert a time unit from absolute date to "time since lights out" as required
	 * by the SOP.
	 *
	 * input comes in as a full time stamp string like 2009-01-22 14:05:00
	 *
	 * @param input the absolute date/time to convert
	 * @param phenotypingCenter location the time was recorded
	 * @return float representing the converted time between -5 hours and +23 hours
	 */
	private float convertTimepoint(String input, Organisation phenotypingCenter) {

		String [] dateTime = input.split(" ");
		String [] time = dateTime[1].split(":");
		Integer hour = Integer.parseInt(time[0]);
		Integer mins = Integer.parseInt(time[1]);

		if (hour < 13) { // Morning times are the next day
			hour += 24;
		}

		float retFloat;

		switch (centerMap.get(phenotypingCenter.getName())) {
			case 1: // HMGU
				retFloat = (float) hour - 18;
				break;
			case 2: // MRC
				retFloat = (float) hour - 19;
				break;
			case 3: // WTSI
				retFloat = (float) hour - 19;
				if (mins > 29) {
					retFloat += 1;
				}
				break;
			case 4: // ICS
				retFloat = (float) hour - 19;
				break;
			default: // CMHD never sent calorimetry, so this should never happen
				retFloat = hour;
				break;
		}
		return retFloat;
	}

	/**
	 * Pre-load the IMPReSS maps for faster look-ups
	 */
	protected void loadAllPipelinesByStableIds() {

		List<Pipeline> pipelineList = pipelineDAO.getAllPhenotypePipelines();

		for (Pipeline p: pipelineList) {
			pipelines.put(p.getStableId(), p);
			for (uk.ac.ebi.phenotype.pojo.Procedure proc: p.getProcedures()) {
				procedures.put(proc.getStableId(), proc);
				for (uk.ac.ebi.phenotype.pojo.Parameter param: proc.getParameters()) {
					parameters.put(param.getStableId(), param);
					if (param.isIncrementFlag()) {
						for (ParameterIncrement increment: param.getIncrement()) {
							if (increment.getValue().length() > 0) {
								parameterIncrementUnit.put(param.getStableId(), increment.getDataType());
							}
						}
					}
				}
			}
		}
	}

	protected void loadColonyIdToProjectMapping() throws SQLException {
		try (PreparedStatement statement = connection.prepareStatement(colonyMapQuery)){

			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				colonyProjectMap.put(resultSet.getString("colony_id"), projects.get(resultSet.getString("mapped_project")));
				specimenProjectMap.put(resultSet.getString("specimen_id"), projects.get(resultSet.getString("mapped_project")));
			}

		}
	}
}
