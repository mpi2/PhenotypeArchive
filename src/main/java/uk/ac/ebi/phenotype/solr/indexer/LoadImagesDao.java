package uk.ac.ebi.phenotype.solr.indexer;

import org.codehaus.plexus.util.ExceptionUtils;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.*;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.Procedure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.phenotype.dao.ObservationDAOImpl;
import uk.ac.ebi.phenotype.pojo.*;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;
import java.util.Map;

/**
 * used by the xml importer only to add images data to the tables
 *
 * @author jwarren
 */
public class LoadImagesDao {

    private static final Logger logger = LoggerFactory.getLogger(LoadImagesDao.class);

    private DataSource dataSource;
    private Connection connection;
    private PreparedStatement observationPreparedStatement;
    private PreparedStatement mediaValuePreparedStatement;
    private PreparedStatement parameterAssociationPreparedStatement;
    private PreparedStatement dimensionPreparedStatement;
    private PreparedStatement procedureMetaDataPreparedStatement;
    private Procedure procedure;
    private uk.ac.ebi.phenotype.pojo.Experiment experiment;
    private PreparedStatement loadExperimentObservationPreparedStatement;

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        try {
            this.connection = dataSource.getConnection();//make sure we have a connection before any requests start
        } catch (SQLException e) {
            logger.error(ExceptionUtils.getFullStackTrace(e));
        }
    }

    /**
     * @param seriesMediaParameter
     * @param connection
     * @return
     * @throws SQLException
     */
    public int loadSeriesMediaParameter(Connection connection, SeriesMediaParameter seriesMediaParameter, Parameter parameter,
                                        BiologicalSample sample,
                                        Datasource datasource) throws SQLException {

        int lastAutoId = 0;

        for (SeriesMediaParameterValue value : seriesMediaParameter.getValue()) {
            String parameterStatusString = seriesMediaParameter.getParameterStatus();

            //load the observation and get the observation id - use this id as the id of the observation_record
            int observationId = this.loadObservation(parameter, sample, datasource, seriesMediaParameter.getParameterStatus(), value.getURI());

            if (parameterStatusString == null) {
                logger.debug("observationid=" + observationId);
                if (lastAutoId != -1) {
                    lastAutoId = this.loadSeriesMediaParameterValue(observationId, sample.getId(), value);
                } else {
                    logger.debug("image record not loaded " + value.getURI());
                }
                //seriesMediaParameter.getParameterStatus();

                logger.debug("loaded image record/seriesMediaParameterValue with id=" + lastAutoId);
            }
        }

        //connection.close();
        return lastAutoId;

    }


    private PreparedStatement getMediaValuePreparedStatement() throws SQLException {
        if (mediaValuePreparedStatement == null) {
            String insertTableSQL = "INSERT INTO image_record_observation (id, sample_id, download_file_path,  increment_value, file_type, media_sample_local_id, media_section_id, organisation_id) VALUES (?,?,?,?,?,?,?, ?)";
            mediaValuePreparedStatement = connection.prepareStatement(insertTableSQL, PreparedStatement.RETURN_GENERATED_KEYS);
        }
        return mediaValuePreparedStatement;
    }

    public int loadSeriesMediaParameterValue(Integer observationId, Integer specimenId, SeriesMediaParameterValue value) throws SQLException {
        return loadMediaValue(connection, observationId, value.getIncrementValue(), specimenId, value.getURI(), value.getFileType(), value.getParameterAssociation(), value.getProcedureMetadata(), null, null);
    }

    /**
     * General method used for all media types elements so we can reuse same code for different object types
     *
     * @param observationId
     * @param incrementValue
     * @param specimenId
     * @param downloadFilePath
     * @param fileType
     * @param parameterAssociations
     * @param procedureMetadatas
     * @param mediaSampleLocalId
     * @param mediaSectionId
     * @return
     * @throws SQLException
     */
    private int loadMediaValue(Connection conncetion, Integer observationId, String incrementValue, Integer specimenId, String downloadFilePath, String fileType,
                               List<ParameterAssociation> parameterAssociations, List<ProcedureMetadata> procedureMetadatas, String mediaSampleLocalId, String mediaSectionId) throws SQLException {

        PreparedStatement preparedStatement = this.getMediaValuePreparedStatement();
        preparedStatement.setInt(1, observationId);
        preparedStatement.setInt(2, specimenId);
        preparedStatement.setString(3, downloadFilePath);
        if (incrementValue != null) {
            preparedStatement.setString(4, incrementValue);
        } else {
            preparedStatement.setNull(4, java.sql.Types.VARCHAR);
        }
        if (fileType != null) {
            preparedStatement.setString(5, fileType);
        } else {
            preparedStatement.setNull(5, java.sql.Types.VARCHAR);
        }
        if (mediaSampleLocalId != null) {
            preparedStatement.setString(6, mediaSampleLocalId);
        } else {
            preparedStatement.setNull(6, java.sql.Types.VARCHAR);
        }
        if (mediaSectionId != null) {
            preparedStatement.setString(7, mediaSectionId);
        } else {
            preparedStatement.setNull(7, java.sql.Types.VARCHAR);
        }
        preparedStatement.setInt(8, experiment.getOrganisation().getId());
        int rownum = preparedStatement.executeUpdate();
        ResultSet autoResult = preparedStatement.getGeneratedKeys();
        int lastAutoId = -1;
        if (rownum != 0 && autoResult.next()) {
            lastAutoId = autoResult.getInt(1);
        }
        //preparedStatement.setTimestamp(4, getCurrentTimeStamp());
        // execute insert SQL stetement
        logger.debug("loaded uri=" + downloadFilePath + "increment value=" + incrementValue);


        //if we have parameter associations we better store them

        //use just made parameter_association and dim tables
        //parameter_association_id and dim_id are the primary key on dim as dim_id should be unique per parameter_association
        //so get the
        logger.debug("observation_id=" + lastAutoId);
        for (ParameterAssociation parameterAssociation : parameterAssociations) {
            this.loadParameterAssociation(lastAutoId, parameterAssociation);
        }

        //if we have procedure metat data we better store that as well?
        for (ProcedureMetadata metaData : procedureMetadatas) {
            logger.debug("meta data=" + metaData.getParameterStatus());
            this.loadProcedureMetaData(conncetion, metaData, lastAutoId);
        }
        return lastAutoId;
    }


    /**
     * Store procedure meta data with a specifc ids as possible - if observation level store observation id if at procedure just store procedure and experiment id?
     *
     * @param connection2
     * @param metaData
     * @param observationId can be just 0 if not at the observation level e.g. at procedure level
     * @throws SQLException
     */
    public void loadProcedureMetaData(Connection connection2, ProcedureMetadata metaData, int observationId) throws SQLException {
        PreparedStatement preparedStatement = this.getProcedureMetaDataPreparedStatement();
        //parameter_id is the only thing required!
        preparedStatement.setString(1, procedure.getProcedureID());
        preparedStatement.setInt(2, experiment.getId());
        preparedStatement.setString(3, metaData.getParameterID());
        //handle nulls on all the following fields
        if (metaData.getSequenceID() != null) {
            preparedStatement.setDouble(4, metaData.getSequenceID().intValue());
        } else {
            preparedStatement.setNull(4, java.sql.Types.INTEGER);
        }
        if (metaData.getParameterStatus() != null) {
            preparedStatement.setString(5, metaData.getParameterStatus());
        } else {
            preparedStatement.setNull(5, java.sql.Types.VARCHAR);
        }
        if (metaData.getValue() != null) {
            preparedStatement.setString(6, metaData.getValue().trim().replace("\n", ""));//remove newlines from the end of the erroneous old europhenome ICS data!!
        } else {
            preparedStatement.setNull(6, java.sql.Types.VARCHAR);
        }

        preparedStatement.setInt(7, observationId);


        int rownum = preparedStatement.executeUpdate();
//		ResultSet autoResult = preparedStatement.getGeneratedKeys();  
//		int lastAutoId=-1;
//		if( rownum != 0 && autoResult.next()) {  
//		      lastAutoId = autoResult.getInt(1);  
//		}  
    }

    private PreparedStatement getProcedureMetaDataPreparedStatement() throws SQLException {
        if (procedureMetaDataPreparedStatement == null) {
            String insertTableSQL = "INSERT INTO procedure_meta_data (procedure_id, experiment_id, parameter_id, sequence_id, parameter_status, value, observation_id) VALUES (?,?,?,?,?,?, ?)";
            procedureMetaDataPreparedStatement = connection.prepareStatement(insertTableSQL, PreparedStatement.RETURN_GENERATED_KEYS);
        }
        return procedureMetaDataPreparedStatement;
    }

    private PreparedStatement getParameterAssociationPreparedStatment() throws SQLException {
        if (parameterAssociationPreparedStatement == null) {
            String insertTableSQL = "INSERT INTO parameter_association (observation_id,  parameter_id, sequence_id, parameter_association_value) VALUES (?,?,?, ?)";
            parameterAssociationPreparedStatement = connection.prepareStatement(insertTableSQL, PreparedStatement.RETURN_GENERATED_KEYS);
        }
        return parameterAssociationPreparedStatement;
    }

    private PreparedStatement getLoadExperimentObservationPreparedStatement() throws SQLException {
        if (loadExperimentObservationPreparedStatement == null) {
            String insertTableSQL = "INSERT INTO experiment_observation (experiment_id, observation_id)  VALUES (?,?)";
            loadExperimentObservationPreparedStatement = connection.prepareStatement(insertTableSQL);
        }
        return loadExperimentObservationPreparedStatement;
    }

    private int loadParameterAssociation(int observationId,
                                         ParameterAssociation parameterAssociation) throws SQLException {
        PreparedStatement preparedStatement = this.getParameterAssociationPreparedStatment();

        preparedStatement.setInt(1, observationId);
        preparedStatement.setString(2, parameterAssociation.getParameterID());
        //if sequence id is set set it if not set it to null
        if (parameterAssociation.getSequenceID() != null) {
            preparedStatement.setInt(3, parameterAssociation.getSequenceID().intValue());
        } else {
            preparedStatement.setNull(3, java.sql.Types.INTEGER);
        }
        //set the parameter association value here - is always a simpleParameter or Ontology so not multiple values allowed
        //loop through simple parameters (but not ontology parameters as they don't have values) to get the value for this paramAssociation and get the value for it for storage here
        String value=null;
        for(SimpleParameter sp:this.procedure.getSimpleParameter()){
        	String paramStableId=sp.getParameterID();//parameter stable id
        	if(paramStableId.equals(parameterAssociation.getParameterID())){
        		value=sp.getValue();
        	}
        }
        if(value!=null){
        	 preparedStatement.setString(4, value);
        }
        
        int rownum = preparedStatement.executeUpdate();
        ResultSet autoResult = preparedStatement.getGeneratedKeys();
        int lastAutoId = -1;
        if (rownum != 0 && autoResult.next()) {
            lastAutoId = autoResult.getInt(1);
        }
        //if dim has more than one (get method never returns null just an empty array
        for (Dimension dim : parameterAssociation.getDim()) {

            this.loadDimension(lastAutoId, dim);

        }

        return lastAutoId;
    }

    private PreparedStatement getDimensionPreparedStatement() throws SQLException {
        if (dimensionPreparedStatement == null) {
            String insertTableSQL = "INSERT INTO dimension (parameter_association_id, id,  origin, unit, value) VALUES (?,?,?, ?,?)";
            dimensionPreparedStatement = connection.prepareStatement(insertTableSQL, PreparedStatement.RETURN_GENERATED_KEYS);
        }
        return dimensionPreparedStatement;
    }

    private int loadDimension(int parameterAssociationAutoId,
                              Dimension dim) throws SQLException {
        PreparedStatement preparedStatement = this.getDimensionPreparedStatement();
        preparedStatement.setInt(1, parameterAssociationAutoId);
        preparedStatement.setString(2, dim.getId());
        preparedStatement.setString(3, dim.getOrigin());
        //unit is optional
        if (dim.getUnit() != null) {
            preparedStatement.setString(4, dim.getUnit());
        } else {
            preparedStatement.setNull(4, java.sql.Types.VARCHAR);
        }
        preparedStatement.setDouble(5, dim.getValue().doubleValue());
        int rownum = preparedStatement.executeUpdate();
        ResultSet autoResult = preparedStatement.getGeneratedKeys();
        int lastAutoId = -1;
        if (rownum != 0 && autoResult.next()) {
            lastAutoId = autoResult.getInt(1);
        }
        return lastAutoId;
    }

    private PreparedStatement getObservationPreparedStatement() throws SQLException {
        if (observationPreparedStatement == null) {
            String insertTableSQL = "INSERT INTO observation (parameter_id, biological_sample_id,  parameter_stable_id, observation_type, missing, population_id, db_id, parameter_status, parameter_status_message) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            observationPreparedStatement = connection.prepareStatement(insertTableSQL, PreparedStatement.RETURN_GENERATED_KEYS);
        }
        return observationPreparedStatement;
    }

    /**
     * load a image observation type into the observation table with a link to the main image table whether thats the image_observation table or something else??
     *
     * @param parameter
     * @param sample
     * @param datasource
     * @return
     * @throws SQLException
     */
    public int loadObservation(Parameter parameter, BiologicalSample sample, Datasource datasource, String parameterStatus, String URI) throws SQLException {
//		@OneToOne
//		@JoinColumn(name = "biological_sample_id")
//		private BiologicalSample sample;
//		
//		@OneToOne
//		@JoinColumn(name = "parameter_id")
//		private Parameter parameter;
//		
//		@Column(name = "parameter_stable_id")	
//		private String parameterStableId;
//		
//		@Enumerated(EnumType.STRING)
//		@Column(name = "observation_type")
//		private ObservationType type;
//		
//		@Column(name = "missing")
//		private boolean missingFlag;
//
//		@Column(name = "population_id")
//		private int populationId;


        PreparedStatement preparedStatement = this.getObservationPreparedStatement();
        preparedStatement.setInt(1, parameter.getId());
        preparedStatement.setInt(2, sample.getId());
        preparedStatement.setString(3, parameter.getStableId());
        preparedStatement.setString(4, ObservationType.image_record.name());

        // Calculate missing flag
        if (parameterStatus != null) {
            preparedStatement.setInt(5, 1);
        } else if (URI == null || URI.isEmpty() || URI.endsWith("/")) {
            preparedStatement.setInt(5, 1);
        } else {
            preparedStatement.setInt(5, 0);
        }

        preparedStatement.setInt(6, 0);
        preparedStatement.setInt(7, datasource.getId());

        Map<String, String> pStatMap = ObservationDAOImpl.getParameterStatusAndMessage(parameterStatus);

        if (pStatMap.get("status") != null) {
            preparedStatement.setString(8, pStatMap.get("status"));
        } else {
            preparedStatement.setNull(8, java.sql.Types.VARCHAR);
        }

        if (pStatMap.get("message") != null) {
            preparedStatement.setString(9, pStatMap.get("message"));
        } else {
            preparedStatement.setNull(9, java.sql.Types.VARCHAR);
        }

        int rownum = preparedStatement.executeUpdate();
        ResultSet autoResult = preparedStatement.getGeneratedKeys();
        int lastAutoId = -1;
        if (rownum != 0 && autoResult.next()) {
            lastAutoId = autoResult.getInt(1);
        }
        //preparedStatement.setTimestamp(4, getCurrentTimeStamp());
        // execute insert SQL stetement
        //also need to load experiment_observation table
        this.loadExperimentObservation(experiment.getId(), lastAutoId);
        return lastAutoId;

    }

    private void loadExperimentObservation(Integer experimentId, int observationId) throws SQLException {
        PreparedStatement preparedStatement = this.getLoadExperimentObservationPreparedStatement();
        preparedStatement.setInt(1, experimentId);
        preparedStatement.setInt(2, observationId);

        int rownum = preparedStatement.executeUpdate();
    }

    public int deleteImageRecordByRecordId(int imageRecordId) {
        try {
            connection = dataSource.getConnection();
            String delete = "delete from image_record_observation where ID=? ";
            PreparedStatement preparedStatement = connection.prepareStatement(delete);
            preparedStatement.setInt(1, imageRecordId);

            preparedStatement.executeUpdate();


        } catch (SQLException e) {
            logger.error(ExceptionUtils.getFullStackTrace(e));
        }
        return 0;

    }


    public int loadMediaSampleParameter(Connection connection2,
                                        MediaSampleParameter currentMediaSampleParameter, Parameter parameter, LiveSample sample, Datasource datasource) throws SQLException {

        logger.error("warning loading mediaSampleParameters!!!!!");

        String info = currentMediaSampleParameter.getParameterID() + currentMediaSampleParameter.getParameterStatus();
        String mediaSampleString = "";
        for (MediaSample mediaSample : currentMediaSampleParameter.getMediaSample()) {
            mediaSampleString += mediaSample.getLocalId();
            for (MediaSection mediaSection : mediaSample.getMediaSection()) {
                mediaSampleString += mediaSection.getLocalId();
                for (MediaFile mediaFile : mediaSection.getMediaFile()) {
                    mediaSampleString += mediaFile.getFileType();
                    mediaSampleString += mediaFile.getLocalId();
                    mediaSampleString += mediaFile.getURI();
                    mediaSampleString += mediaFile.getParameterAssociation().get(0).getParameterID();
                }
            }
        }
        logger.debug("mediaSampleParam=" + info);
        logger.debug("mediaSampleString=" + mediaSampleString);

        connection = connection2;
        //for testing lets delete the tables
        int lastAutoId = 0;

        for (MediaSample mediaSample : currentMediaSampleParameter.getMediaSample()) {
            //load the observation and get the observation id - use this id as the id of the observation_record
            String mediaSampleLocalId = mediaSample.getLocalId();
            for (MediaSection mediaSection : mediaSample.getMediaSection()) {

                for (MediaFile mediaFile : mediaSection.getMediaFile()) {
                    String parameterStatusString = currentMediaSampleParameter.getParameterStatus();
                    int observationId = this.loadObservation(parameter, sample, datasource, currentMediaSampleParameter.getParameterStatus(), mediaFile.getURI());
                    String mediaSectionId = mediaSection.getLocalId();
                    if (parameterStatusString == null) {
                        logger.debug("observationId=" + observationId);
                        if (lastAutoId != -1) {
                            lastAutoId = this.loadMediaValue(connection, observationId, null, sample.getId(), mediaFile.getURI(), mediaFile.getFileType(), mediaFile.getParameterAssociation(), mediaFile.getProcedureMetadata(), mediaSampleLocalId, mediaSectionId);
                        } else {
                            logger.debug("image record not loaded " + mediaFile.getURI());
                        }
                        //seriesMediaParameter.getParameterStatus();

                        logger.debug("loaded image record/mediaSampleParameterValue with id=" + lastAutoId);
                    }
                }
            }
        }

        return lastAutoId;

    }

    public int loadMediaParameter(Connection connection,
                                  MediaParameter mediaParameter, Parameter parameter,
                                  LiveSample animal, Datasource datasource) throws SQLException {

        int lastAutoId = 0;

        String parameterStatusString = mediaParameter.getParameterStatus();
        //load the observation and get the observation id - use this id as the id of the observation_record
        int observationId = this.loadObservation(parameter, animal, datasource, parameterStatusString, mediaParameter.getURI());

        if (parameterStatusString == null) {//only load the image record if the parameter status is not there - we have recorded the status and observation id in the observation table already
            logger.debug("observationId=" + observationId);
            if (observationId != -1) {
                lastAutoId = this.loadMediaValue(connection, observationId, null, animal.getId(), mediaParameter.getURI(), mediaParameter.getFileType(), mediaParameter.getParameterAssociation(), mediaParameter.getProcedureMetadata(), null, null);
            } else {
                logger.debug("image record not loaded " + mediaParameter.getURI());
            }

            logger.debug("loaded image record/MediaParameter with id=" + lastAutoId);
        }

        return lastAutoId;
    }

    public void setAutoIdSoDoesntConflictWithSanger(Connection connection)
            throws SQLException {

        Statement findNumberStatement = connection.createStatement();
        String findsizeImageObservationTable = "SELECT id from image_record_observation ORDER BY id DESC LIMIT 1";
        findNumberStatement.execute(findsizeImageObservationTable);
        ResultSet rs = findNumberStatement.getResultSet();

        // if nothing in the new image table we want to set auto_id for the id
        // column to be one higher than the sanger image record table so lets do
        // it
        if (!rs.isBeforeFirst()) {

            Statement findNumberStatement2 = connection.createStatement();
            String findsizeSanger = "SELECT id from ima_image_record ORDER BY id DESC LIMIT 1";
            findNumberStatement2.execute(findsizeSanger);
            ResultSet rs2 = findNumberStatement2.getResultSet();
            while (rs2.next()) {
                int largestId2 = rs2.getInt("id");
                logger.debug("largest id={}", largestId2);
                // ALTER TABLE users AUTO_INCREMENT=11000;
                // image_record_observation
                int start = largestId2 + 1;
                String sql = "ALTER TABLE image_record_observation  AUTO_INCREMENT=" + start;
                Statement stmnt = connection.createStatement();
                stmnt.executeUpdate(sql);
            }
        }
    }

    public void setProcedure(Procedure procedure) {
        this.procedure = procedure;

    }

    public void setExperiment(
            uk.ac.ebi.phenotype.pojo.Experiment experiment) {
        this.experiment = experiment;

    }


}
