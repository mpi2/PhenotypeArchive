package uk.ac.ebi.phenotype.service.dto;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.solr.client.solrj.beans.Field;

import uk.ac.ebi.phenotype.dao.PhenotypePipelineDAO;

public class ObservationDTO {

    public final static String ID = "id";
    public final static String DATASOURCE_ID = "datasource_id";
    public final static String DATASOURCE_NAME = "datasource_name";
    public final static String PROJECT_ID = "project_id";
    public final static String PROJECT_NAME = "project_name";
    public final static String PHENOTYPING_CENTER = "phenotyping_center";
    public final static String PHENOTYPING_CENTER_ID = "phenotyping_center_id";
    public final static String GENE_ACCESSION_ID = "gene_accession_id";
    public final static String GENE_SYMBOL = "gene_symbol";
    public final static String ALLELE_ACCESSION_ID = "allele_accession_id";
    public final static String ALLELE_SYMBOL = "allele_symbol";
    public final static String ZYGOSITY = "zygosity";
    public final static String SEX = "sex";
    public final static String BIOLOGICAL_MODEL_ID = "biological_model_id";
    public final static String BIOLOGICAL_SAMPLE_ID = "biological_sample_id";
    public final static String BIOLOGICAL_SAMPLE_GROUP = "biological_sample_group";
    public final static String STRAIN_ACCESSION_ID = "strain_accession_id";
    public final static String STRAIN_NAME = "strain_name";
    public final static String PIPELINE_NAME = "pipeline_name";
    public final static String PIPELINE_ID = "pipeline_id";
    public final static String PIPELINE_STABLE_ID = "pipeline_stable_id";
    public final static String PROCEDURE_ID = "procedure_id";
    public final static String PROCEDURE_NAME = "procedure_name";
    public final static String PROCEDURE_STABLE_ID = "procedure_stable_id";
    public final static String PARAMETER_ID = "parameter_id";
    public final static String PARAMETER_NAME = "parameter_name";
    public final static String PARAMETER_STABLE_ID = "parameter_stable_id";
    public final static String EXPERIMENT_ID = "experiment_id";
    public final static String EXPERIMENT_SOURCE_ID = "experiment_source_id";
    public final static String OBSERVATION_TYPE = "observation_type";
    public final static String COLONY_ID = "colony_id";
    public final static String DATE_OF_BIRTH = "date_of_birth";
    public final static String DATE_OF_EXPERIMENT = "date_of_experiment";
    public final static String POPULATION_ID = "population_id";
    public final static String EXTERNAL_SAMPLE_ID = "external_sample_id";
    public final static String DATA_POINT = "data_point";
    public final static String ORDER_INDEX = "order_index";
    public final static String DIMENSION = "dimension";
    public final static String TIME_POINT = "time_point";
    public final static String DISCRETE_POINT = "discrete_point";
    public final static String CATEGORY = "category";
    public final static String VALUE = "value";
    public final static String METADATA = "metadata";
    public final static String METADATA_GROUP = "metadata_group";
	public static final String DOWNLOAD_FILE_PATH = "download_file_path";

	@Field(ID)
    private Integer id;

    @Field(DATASOURCE_ID)
    private Integer dataSourceId;

    @Field(DATASOURCE_NAME)
    private String dataSourceName;

    @Field(PROJECT_ID)
    private Integer projectId;

    @Field(PROJECT_NAME)
    private String projectName;

    @Field(PIPELINE_NAME)
    private String pipelineName;

    @Field(PIPELINE_STABLE_ID)
    private String pipelineStableId;

    @Field(PROCEDURE_STABLE_ID)
    private String procedureStableId;

    @Field(PARAMETER_STABLE_ID)
    private String parameterStableId;

    @Field(PIPELINE_ID)
    private Integer pipelineId;

    @Field(PROCEDURE_ID)
    private Integer procedureId;

    @Field(PARAMETER_ID)
    private Integer parameterId;

    @Field(STRAIN_ACCESSION_ID)
    private String strainAccessionId;

    @Field(STRAIN_NAME)
    private String strainName;

    @Field(EXPERIMENT_SOURCE_ID)
    private String experimentSourceId;

    @Field(GENE_SYMBOL)
    private String geneSymbol;

    @Field(GENE_ACCESSION_ID)
    private String geneAccession;

    @Field(EXPERIMENT_ID)
    private Integer experimentId;

    @Field(PHENOTYPING_CENTER_ID)
    private Integer phenotypingCenterId;

    @Field(PHENOTYPING_CENTER)
    private String phenotypingCenter;

    @Field(OBSERVATION_TYPE)
    private String observationType;

    @Field(COLONY_ID)
    private String colonyId;

    @Field(DATE_OF_EXPERIMENT)
    private Date dateOfExperiment;

    @Field(DATE_OF_BIRTH)
    private Date dateOfBirth;

    @Field(BIOLOGICAL_SAMPLE_ID)
    private Integer biologicalSampleId;

    @Field(BIOLOGICAL_MODEL_ID)
    private Integer biologicalModelId;

    @Field(ZYGOSITY)
    private String zygosity;

    @Field(SEX)
    private String sex;

    @Field(BIOLOGICAL_SAMPLE_GROUP)
    private String group;

    @Field(CATEGORY)
    private String category;

    @Field(DATA_POINT)
    private Float dataPoint;

    @Field(ORDER_INDEX)
    private Integer orderIndex;

    @Field(DIMENSION)
    private String dimension;

    @Field(TIME_POINT)
    private String timePoint;

    @Field(DISCRETE_POINT)
    private Float discretePoint;

    @Field(EXTERNAL_SAMPLE_ID)
    private String externalSampleId;

    @Field(PARAMETER_NAME)
    private String parameterName;

    @Field(PROCEDURE_NAME)
    private String procedureName;

    @Field(METADATA_GROUP)
    private String metadataGroup;

    @Field(METADATA)
    private List<String> metadata;

    @Field(ALLELE_ACCESSION_ID)
    private String alleleAccession;
    
    @Field(ALLELE_SYMBOL)
    private String alleleSymbol;

    @Field(DOWNLOAD_FILE_PATH)
    private String downloadFilePath;
    

	/**
     * helper methods
     * 
     * @throws SQLException
     */

    public String tabbedToString(PhenotypePipelineDAO ppDAO) throws SQLException {
        String tabbed = pipelineName
                + "\t" + pipelineStableId
                + "\t" + procedureStableId
                + "\t" + procedureName
                + "\t" + parameterStableId
                + "\t" + parameterName
                // + "\t" + pipelineId
                // + "\t" + procedureId
                // + "\t" + parameterId
                + "\t" + strainAccessionId
                + "\t" + strainName
                // + "\t" + experimentSourceId
                + "\t" + geneSymbol
                + "\t" + geneAccession
                + "\t" + alleleSymbol
                + "\t" + alleleAccession
                // + "\t" + experimentId
                // + "\t" + organisationId
                // + "\t" + observationType
                + "\t" + phenotypingCenter
                + "\t" + colonyId
                + "\t" + dateOfExperiment
                // + "\t" + dateOfBirth
                // + "\t" + biologicalSampleId
                // + "\t" + biologicalModelId
                + "\t" + zygosity
                + "\t" + sex
                + "\t" + group
                // + "\t" + category
                // + "\t" + dataPoint
                // + "\t" + orderIndex
                // + "\t" + dimension
                // + "\t" + timePoint
                // + "\t" + discretePoint
                + "\t" + externalSampleId
                + "\t\"" + metadata + "\""
                +"\t" + metadataGroup;
        ;

        if (observationType.equalsIgnoreCase("unidimensional")) {
            tabbed += "\t" + dataPoint;
        }
        else if (observationType.equalsIgnoreCase("categorical")) {
            tabbed += "\t" + ppDAO.getCategoryDescription(parameterId, category);
        }
        else if (observationType.equalsIgnoreCase("time_series")) {
            tabbed += "\t" + dataPoint + "\t" + discretePoint;
        }
        return tabbed;
    }

    public String getTabbedFields() {
        String tabbed = "pipeline name"
                + "\t pipelineStableId"
                + "\t procedureStableId"
                + "\t procedureName"
                + "\t parameterStableId"
                + "\t parameterName"
                // + "\t pipeline id"
                // + "\t procedureId"
                // + "\t parameterId"
                + "\t strainAccessionId"
                + "\t strainName"
                // + "\t experimentSourceId"
                + "\t geneSymbol"
                + "\t geneAccession"
                + "\t alleleSymbol"
                + "\t alleleAccession"
                // + "\t experimentId"
                // + "\t organisationId"
                // + "\t observationType"
                + "\t phenotypingCenter"
                + "\t colonyId"
                + "\t dateOfExperiment"
                // + "\t dateOfBirth"
                // + "\t biologicalSampleId"
                // + "\t biologicalModelId"
                + "\t zygosity"
                + "\t sex"
                + "\t group"
                // + "\t category"
                // + "\t dataPoint"
                // + "\t orderIndex"
                // + "\t dimension"
                // + "\t timePoint"
                // + "\t discretePoint"
                + "\t externalSampleId"
                + "\t metadata"
                + "\t metadataGroup";
        if (observationType.equalsIgnoreCase("unidimensional")) {
            tabbed += "\t" + "dataPoint";
        }
        else if (observationType.equalsIgnoreCase("categorical")) {
            tabbed += "\t" + "category";
        }
        else if (observationType.equalsIgnoreCase("time_series")) {
            tabbed += "\t" + "dataPoint" + "\t" + "discretePoint";
        }
        return tabbed;
    }

    public String getParameterName() {
        return parameterName;
    }

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    public String getProcedureName() {
        return this.procedureName;
    }

    public void setProcedureName(String procedureName) {
        this.procedureName = procedureName;
    }

    public String getExternalSampleId() {
        return externalSampleId;
    }

    public void setExternalSampleId(String externalSampleId) {
        this.externalSampleId = externalSampleId;
    }

    /**
     * Format date of experiment string into day/month/year format
     * 
     * @return string representation of the date the experiment was performed
     */
    public String getDateOfExperimentString() {
        return new SimpleDateFormat("dd/MM/yyyy").format(dateOfExperiment);
    }

    public String getDateOfBirthString() {
        return new SimpleDateFormat("dd/MM/yyyy").format(dateOfBirth);

    }

    public boolean isControl() {
        return this.group.equals("control");
    }

    public boolean isMutant() {
        return this.group.equals("experimental");
    }

    /**
     * 
     * @return key uniquely identifying the group in which the ObservationDTO
     *         object is analysed. A concatenation of phenotyping center,
     *         strainAccessionId, allele, parameter, pipeline, zygosity, sex, metadata
     */

    public String getKey() {
        return "[allele: " + this.getAlleleAccession()
                + " , strainAccessionId :" + this.getStrain()
                + " , phenotyping center :" + this.getPhenotypingCenter()
                + " , parameter :" + this.getParameterStableId()
                + " , pipeline :" + this.getPipelineStableId()
                + " , zygosity :" + this.getZygosity()
                + " , metadata :" + this.getMetadataGroup()
                + " ]";
    }

    /**
     * end helper methods
     */

    @Override
    public String toString() {
        return "id=" + id
                + ", parameterId=" + parameterId
                + ", phenotypingCenterId=" + phenotypingCenterId
                + ", biologicalModelId=" + biologicalModelId
                + ", zygosity=" + zygosity
                + ", sex=" + sex
                + ", group=" + group
                + ", colonyId=" + colonyId
                + ", metadataGroup=" + metadataGroup
                + ", dataPoint=" + dataPoint
                + ", category=" + category
                + ", dateOfExperiment=" + dateOfExperiment
                + ", orderIndex=" + orderIndex
                + ", dimension=" + dimension
                + ", timePoint=" + timePoint
                + ", discretePoint=" + discretePoint
                + ", externalSampleId=" + externalSampleId;
    }

    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return the pipelineName
     */
    public String getPipelineName() {
        return pipelineName;
    }

    /**
     * @param pipelineName
     *            the pipelineName to set
     */
    public void setPipelineName(String pipelineName) {
        this.pipelineName = pipelineName;
    }

    /**
     * @return the pipelineStableId
     */
    public String getPipelineStableId() {
        return pipelineStableId;
    }

    /**
     * @param pipelineStableId
     *            the pipelineStableId to set
     */
    public void setPipelineStableId(String pipelineStableId) {
        this.pipelineStableId = pipelineStableId;
    }

    /**
     * @return the procedureStableId
     */
    public String getProcedureStableId() {
        return procedureStableId;
    }

    /**
     * @param procedureStableId
     *            the procedureStableId to set
     */
    public void setProcedureStableId(String procedureStableId) {
        this.procedureStableId = procedureStableId;
    }

    /**
     * @return the parameterStableId
     */
    public String getParameterStableId() {
        return parameterStableId;
    }

    /**
     * @param parameterStableId
     *            the parameterStableId to set
     */
    public void setParameterStableId(String parameterStableId) {
        this.parameterStableId = parameterStableId;
    }

    /**
     * @return the pipelineId
     */
    public Integer getPipelineId() {
        return pipelineId;
    }

    /**
     * @param pipelineId
     *            the pipelineId to set
     */
    public void setPipelineId(Integer pipelineId) {
        this.pipelineId = pipelineId;
    }

    /**
     * @return the procedureId
     */
    public Integer getProcedureId() {
        return procedureId;
    }

    /**
     * @param procedureId
     *            the procedureId to set
     */
    public void setProcedureId(Integer procedureId) {
        this.procedureId = procedureId;
    }

    /**
     * @return the parameterId
     */
    public Integer getParameterId() {
        return parameterId;
    }

    /**
     * @param parameterId
     *            the parameterId to set
     */
    public void setParameterId(Integer parameterId) {
        this.parameterId = parameterId;
    }

    /**
     * @return the strainAccessionId
     */
    public String getStrain() {
        return strainAccessionId;
    }

    /**
     * @param strainAccessionId
     *            the strainAccessionId to set
     */
    public void setStrain(String strain) {
        this.strainAccessionId = strain;
    }

    /**
     * @return the experimentSourceId
     */
    public String getExperimentSourceId() {
        return experimentSourceId;
    }

    /**
     * @param experimentSourceId
     *            the experimentSourceId to set
     */
    public void setExperimentSourceId(String experimentSourceId) {
        this.experimentSourceId = experimentSourceId;
    }

    /**
     * @return the geneSymbol
     */
    public String getGeneSymbol() {
        return geneSymbol;
    }

    /**
     * @param geneSymbol
     *            the geneSymbol to set
     */
    public void setGeneSymbol(String geneSymbol) {
        this.geneSymbol = geneSymbol;
    }

    /**
     * @return the geneAccession
     */
    public String getGeneAccession() {
        return geneAccession;
    }

    /**
     * @param geneAccession
     *            the geneAccession to set
     */
    public void setGeneAccession(String geneAccession) {
        this.geneAccession = geneAccession;
    }

    /**
     * @return the experimentId
     */
    public Integer getExperimentId() {
        return experimentId;
    }

    /**
     * @param experimentId
     *            the experimentId to set
     */
    public void setExperimentId(Integer experimentId) {
        this.experimentId = experimentId;
    }

    /**
     * @return the organisationId
     */
    public Integer getPhenotypingCenterId() {
        return phenotypingCenterId;
    }

    /**
     * @param organisationId
     *            the organisationId to set
     */
    public void setPhenotypingCenterId(Integer phenotypingCenterId) {
        this.phenotypingCenterId = phenotypingCenterId;
    }

    /**
     * @return the observationType
     */
    public String getObservationType() {
        return observationType;
    }

    /**
     * @param observationType
     *            the observationType to set
     */
    public void setObservationType(String observationType) {
        this.observationType = observationType;
    }

    /**
     * @return the organisation
     */
    public String getPhenotypingCenter() {
        return phenotypingCenter;
    }

    /**
     * @param organisation
     *            the organisation to set
     */
    public void setPhenotypingCenter(String phenotypingCenter) {
        this.phenotypingCenter = phenotypingCenter;
    }

    /**
     * @return the colonyId
     */
    public String getColonyId() {
        return colonyId;
    }

    /**
     * @param colonyId
     *            the colonyId to set
     */
    public void setColonyId(String colonyId) {
        this.colonyId = colonyId;
    }

    /**
     * @return the dateOfExperiment
     */
    public Date getDateOfExperiment() {
        return dateOfExperiment;
    }

    /**
     * @param dateOfExperiment
     *            the dateOfExperiment to set
     */
    public void setDateOfExperiment(Date dateOfExperiment) {
        this.dateOfExperiment = dateOfExperiment;
    }

    /**
     * @return the dateOfBirth
     */
    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    /**
     * @param dateOfBirth
     *            the dateOfBirth to set
     */
    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    /**
     * @return the biologicalSampleId
     */
    public Integer getBiologicalSampleId() {
        return biologicalSampleId;
    }

    /**
     * @param biologicalSampleId
     *            the biologicalSampleId to set
     */
    public void setBiologicalSampleId(Integer biologicalSampleId) {
        this.biologicalSampleId = biologicalSampleId;
    }

    /**
     * @return the biologicalModelId
     */
    public Integer getBiologicalModelId() {
        return biologicalModelId;
    }

    /**
     * @param biologicalModelId
     *            the biologicalModelId to set
     */
    public void setBiologicalModelId(Integer biologicalModelId) {
        this.biologicalModelId = biologicalModelId;
    }

    /**
     * @return the zygosity
     */
    public String getZygosity() {
        return zygosity;
    }

    /**
     * @param zygosity
     *            the zygosity to set
     */
    public void setZygosity(String zygosity) {
        this.zygosity = zygosity;
    }

    /**
     * @return the sex
     */
    public String getSex() {
        return sex;
    }

    /**
     * @param sex
     *            the sex to set
     */
    public void setSex(String sex) {
        this.sex = sex;
    }

    /**
     * @return the group
     */
    public String getGroup() {
        return group;
    }

    /**
     * @param group
     *            the group to set
     */
    public void setGroup(String group) {
        this.group = group;
    }

    /**
     * @return the category
     */
    public String getCategory() {
        return category;
    }

    /**
     * @param category
     *            the category to set
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * @return the dataPoint
     */
    public Float getDataPoint() {
        return dataPoint;
    }

    /**
     * @param dataPoint
     *            the dataPoint to set
     */
    public void setDataPoint(Float dataPoint) {
        this.dataPoint = dataPoint;
    }

    /**
     * @return the orderIndex
     */
    public Integer getOrderIndex() {
        return orderIndex;
    }

    /**
     * @param orderIndex
     *            the orderIndex to set
     */
    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }

    /**
     * @return the dimension
     */
    public String getDimension() {
        return dimension;
    }

    /**
     * @param dimension
     *            the dimension to set
     */
    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    /**
     * @return the timePoint
     */
    public String getTimePoint() {
        return timePoint;
    }

    /**
     * @param timePoint
     *            the timePoint to set
     */
    public void setTimePoint(String timePoint) {
        this.timePoint = timePoint;
    }

    /**
     * @return the discretePoint
     */
    public Float getDiscretePoint() {
        return discretePoint;
    }

    /**
     * @param discretePoint
     *            the discretePoint to set
     */
    public void setDiscretePoint(Float discretePoint) {
        this.discretePoint = discretePoint;
    }

    /**
     * @return the metadataGroup
     */
    public String getMetadataGroup() {
        return metadataGroup;
    }

    /**
     * @param metadataGroup
     *            the metadataGroup to set
     */
    public void setMetadataGroup(String metadataGroup) {
        this.metadataGroup = metadataGroup;
    }

    /**
     * @return the metadata
     */
    public List<String> getMetadata() {
        return metadata;
    }

    public String getStrainAccessionId() {
		return strainAccessionId;
	}

	public void setStrainAccessionId(String strainAccessionId) {
		this.strainAccessionId = strainAccessionId;
	}

	public String getStrainName() {
		return strainName;
	}

	public void setStrainName(String strainName) {
		this.strainName = strainName;
	}

	public String getAlleleSymbol() {
		return alleleSymbol;
	}

	public void setAlleleSymbol(String alleleSymbol) {
		this.alleleSymbol = alleleSymbol;
	}

	/**
     * @param metadata
     *            the metadata to set
     */
    public void setMetadata(List<String> metadata) {
        this.metadata = metadata;
    }

    public String getAlleleAccession() {
        return this.alleleAccession;
    }

    public void setAlleleAccession(String alleleAccession) {
        this.alleleAccession = alleleAccession;
    }

    public Integer getDataSourceId() {
        return dataSourceId;
    }

    public void setDataSourceId(Integer dataSourceId) {
        this.dataSourceId = dataSourceId;
    }

    public String getDataSourceName() {
        return dataSourceName;
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }
    
    public Integer getProjectId() {
        return projectId;
    }
    
    public String getProjectName() {
        return projectName;
    }
    
    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }
    
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
    
    public String getDownloadFilePath() {
    	
		return downloadFilePath;
	}

	public void setDownloadFilePath(String downloadFilePath) {
	
		this.downloadFilePath = downloadFilePath;
	}
}
