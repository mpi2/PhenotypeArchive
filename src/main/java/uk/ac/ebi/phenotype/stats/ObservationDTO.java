package uk.ac.ebi.phenotype.stats;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.solr.client.solrj.beans.Field;

public class ObservationDTO {

	@Field("id")
	private Integer id;

	@Field("pipeline_name")
	private String pipelineName;

	@Field("pipeline_stable_id")
	private String pipelineStableId;

	@Field("procedure_stable_id")
	private String procedureStableId;

	@Field("parameter_stable_id")
	private String parameterStableId;

	@Field("pipeline_id")
	private Integer pipelineId;

	@Field("procedure_id")
	private Integer procedureId;

	@Field("parameter_id")
	private Integer parameterId;

	@Field("strain")
	private String strain;

	@Field("experiment_source_id")
	private String experimentSourceId;

	@Field("gene_symbol")
	private String geneSymbol;

	@Field("gene_accession")
	private String geneAccession;

	@Field("experiment_id")
	private Integer experimentId;

	@Field("phenotyping_center_id")
	private Integer phenotypingCenterId;

	@Field("phenotyping_center")
	private String phenotypingCenter;

	@Field("observation_type")
	private String observationType;

	@Field("colony_id")
	private String colonyId;

	@Field("date_of_experiment")
	private Date dateOfExperiment;

	@Field("date_of_birth")
	private Date dateOfBirth;

	@Field("biological_sample_id")
	private Integer biologicalSampleId;

	@Field("biological_model_id")
	private Integer biologicalModelId;

	@Field("zygosity")
	private String zygosity;

	@Field("sex")
	private String sex;

	@Field("biological_sample_group")
	private String group;

	@Field("category")
	private String category;

	@Field("data_point")
	private Float dataPoint;

	@Field("order_index")
	private Integer orderIndex;

	@Field("dimension")
	private String dimension;

	@Field("time_point")
	private String timePoint;

	@Field("discrete_point")
	private Float discretePoint;

	@Field("external_sample_id")
	private String externalSampleId;

	@Field("parameter_name")
	private String parameterName;
	
	@Field("procedure_name")
	private String procedureName;

	@Field("metadata_group")
	private String metadataGroup;

	@Field("metadata")
	private List<String> metadata;
	

	/**
	 * helper methods
	 */

	public String tabbedToString(){
		String tabbed =	pipelineName 
				 + "\t" + pipelineStableId 
				 + "\t" + procedureStableId 
				 + "\t" + procedureName
				 + "\t" + parameterStableId
				 + "\t" + parameterName
			//	 + "\t" + pipelineId
			//	 + "\t" + procedureId
			//	 + "\t" + parameterId
				 + "\t" + strain
			//	 + "\t" + experimentSourceId
				 + "\t" + geneSymbol
				 + "\t" + geneAccession
			//	 + "\t" + experimentId
			//	 + "\t" + organisationId
			//	 + "\t" + observationType
				 + "\t" + phenotypingCenter
				 + "\t" + colonyId
				 + "\t" + dateOfExperiment
			//	 + "\t" + dateOfBirth
				 + "\t" + biologicalSampleId
			//	 + "\t" + biologicalModelId
				 + "\t" + zygosity
				 + "\t" + sex
				 + "\t" + group
			//	 + "\t" + category
			//	 + "\t" + dataPoint
			//	 + "\t" + orderIndex
			//	 + "\t" + dimension
			//	 + "\t" + timePoint
			//	 + "\t" + discretePoint
				 + "\t" + externalSampleId
				 ;
		
		if (observationType.equalsIgnoreCase("unidimensional")){
			tabbed += "\t" + dataPoint;
		}
		else if (observationType.equalsIgnoreCase("categorical")){
			tabbed += "\t" + category;
		}
		else if (observationType.equalsIgnoreCase("time_series")){
			tabbed += "\t" + dataPoint + "\t" + discretePoint;
		}
		return tabbed;
	}
	
	public String getTabbedFields(){
		String tabbed = "pipeline name" 
				 + "\t pipeline stable id"
				 + "\t procedure stable id"
				 + "\t procedure name"
				 + "\t parameter stable id"
				 + "\t parameter name"
			//	 + "\t pipeline id" 
			//	 + "\t procedureId"
			//	 + "\t parameterId"
				 + "\t strain"
			//	 + "\t experimentSourceId"
				 + "\t geneSymbol"
				 + "\t geneAccession"
			//	 + "\t experimentId"
			//	 + "\t organisationId"
			//	 + "\t observationType"
				 + "\t organisation"
				 + "\t colonyId"
				 + "\t dateOfExperiment"
			//	 + "\t dateOfBirth"
			//	 + "\t biologicalSampleId"
			//	 + "\t biologicalModelId"
				 + "\t zygosity"
				 + "\t sex"
				 + "\t group"
			//	 + "\t category"
			//	 + "\t dataPoint"
			//	 + "\t orderIndex"
			//	 + "\t dimension"
			//	 + "\t timePoint"
			//	 + "\t discretePoint"
				 + "\t externalSampleId"
				 ;
		if (observationType.equalsIgnoreCase("unidimensional")){
			tabbed += "\t" + "dataPoint";
		}
		else if (observationType.equalsIgnoreCase("categorical")){
			tabbed += "\t" + "category";
		}
		else if (observationType.equalsIgnoreCase("time_series")){
			tabbed += "\t" + "dataPoint" + "\t" + "discretePoint";
		}
		return tabbed;
	}
	
	public String getParameterName(){
		return parameterName;
	}
	
	public void setParameterName(String parameterName){
		this.parameterName = parameterName;
	}
	
	public String getProcedureName(){
		return this.procedureName;
	}
	
	public void setProcedureName(String procedureName){
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
	 * @param id the id to set
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
	 * @param pipelineName the pipelineName to set
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
	 * @param pipelineStableId the pipelineStableId to set
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
	 * @param procedureStableId the procedureStableId to set
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
	 * @param parameterStableId the parameterStableId to set
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
	 * @param pipelineId the pipelineId to set
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
	 * @param procedureId the procedureId to set
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
	 * @param parameterId the parameterId to set
	 */
	public void setParameterId(Integer parameterId) {
		this.parameterId = parameterId;
	}

	/**
	 * @return the strain
	 */
	public String getStrain() {
		return strain;
	}

	/**
	 * @param strain the strain to set
	 */
	public void setStrain(String strain) {
		this.strain = strain;
	}

	/**
	 * @return the experimentSourceId
	 */
	public String getExperimentSourceId() {
		return experimentSourceId;
	}

	/**
	 * @param experimentSourceId the experimentSourceId to set
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
	 * @param geneSymbol the geneSymbol to set
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
	 * @param geneAccession the geneAccession to set
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
	 * @param experimentId the experimentId to set
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
	 * @param organisationId the organisationId to set
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
	 * @param observationType the observationType to set
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
	 * @param organisation the organisation to set
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
	 * @param colonyId the colonyId to set
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
	 * @param dateOfExperiment the dateOfExperiment to set
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
	 * @param dateOfBirth the dateOfBirth to set
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
	 * @param biologicalSampleId the biologicalSampleId to set
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
	 * @param biologicalModelId the biologicalModelId to set
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
	 * @param zygosity the zygosity to set
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
	 * @param sex the sex to set
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
	 * @param group the group to set
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
	 * @param category the category to set
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
	 * @param dataPoint the dataPoint to set
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
	 * @param orderIndex the orderIndex to set
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
	 * @param dimension the dimension to set
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
	 * @param timePoint the timePoint to set
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
	 * @param discretePoint the discretePoint to set
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
	 * @param metadataGroup the metadataGroup to set
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

	/**
	 * @param metadata the metadata to set
	 */
	public void setMetadata(List<String> metadata) {
		this.metadata = metadata;
	}

	
	
}
