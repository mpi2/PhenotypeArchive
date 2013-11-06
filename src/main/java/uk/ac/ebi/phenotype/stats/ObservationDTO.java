package uk.ac.ebi.phenotype.stats;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.solr.client.solrj.beans.Field;

public class ObservationDTO {

	@Field
	private Integer id;

	@Field
	private String pipelineName;

	@Field
	private String pipelineStableId;

	@Field
	private String procedureStableId;

	@Field
	private String parameterStableId;

	@Field
	private Integer pipelineId;

	@Field
	private Integer procedureId;

	@Field
	private Integer parameterId;

	@Field
	private String strain;

	@Field
	private String experimentSourceId;

	@Field
	private String geneSymbol;

	@Field
	private String geneAccession;

	@Field
	private Integer experimentId;

	@Field
	private Integer organisationId;

	@Field
	private String observationType;

	@Field
	private String organisation;

	@Field
	private String colonyId;

	@Field
	private Date dateOfExperiment;

	@Field
	private Date dateOfBirth;

	@Field
	private Integer biologicalSampleId;

	@Field
	private Integer biologicalModelId;

	@Field
	private String zygosity;

	@Field("gender")
	private String sex;

	@Field("biologicalSampleGroup")
	private String group;

	@Field
	private String category;

	@Field
	private Float dataPoint;

	@Field
	private Integer orderIndex;

	@Field
	private String dimension;

	@Field
	private String timePoint;

	@Field
	private Float discretePoint;

	@Field
	private String externalSampleId;



	/**
	 * helper methods
	 */

	public String tabbedToString(){
		return 	id + "\t" + pipelineName + "\t" + pipelineStableId + "\t" + procedureStableId + "\t" + parameterStableId
				 + "\t" + pipelineId
				 + "\t" + procedureId
				 + "\t" + parameterId
				 + "\t" + strain
				 + "\t" + experimentSourceId
				 + "\t" + geneSymbol
				 + "\t" + geneAccession
				 + "\t" + experimentId
				 + "\t" + organisationId
				 + "\t" + observationType
				 + "\t" + organisation
				 + "\t" + colonyId
				 + "\t" + dateOfExperiment
				 + "\t" + dateOfBirth
				 + "\t" + biologicalSampleId
				 + "\t" + biologicalModelId
				 + "\t" + zygosity
				 + "\t" + sex
				 + "\t" + group
				 + "\t" + category
				 + "\t" + dataPoint
				 + "\t" + orderIndex
				 + "\t" + dimension
				 + "\t" + timePoint
				 + "\t" + discretePoint
				 + "\t" + externalSampleId;
	}
	
	public String getTabbedFields(){
		return 	"id \t pipeline name" 
				 + "\t pipeline stable id \t procedure stable id \t parameter stable id"
				 + "\t pipeline id" 
				 + "\t  procedureId"
				 + "\t  parameterId"
				 + "\t  strain"
				 + "\t experimentSourceId"
				 + "\t geneSymbol"
				 + "\t geneAccession"
				 + "\t experimentId"
				 + "\t organisationId"
				 + "\t observationType"
				 + "\t organisation"
				 + "\t colonyId"
				 + "\t dateOfExperiment"
				 + "\t dateOfBirth"
				 + "\t biologicalSampleId"
				 + "\t biologicalModelId"
				 + "\t zygosity"
				 + "\t sex"
				 + "\t group"
				 + "\t category"
				 + "\t dataPoint"
				 + "\t orderIndex"
				 + "\t dimension"
				 + "\t timePoint"
				 + "\t discretePoint"
				 + "\t externalSampleId";
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
		return "id=" + id + ", parameterId="
			+ parameterId + ", organisationId=" + organisationId
			+ ", biologicalModelId=" + biologicalModelId + ", zygosity="
			+ zygosity + ", sex=" + sex + ", group=" + group
			+ ", colonyId=" + colonyId + ", dataPoint=" + dataPoint
			+ ", dateOfExperiment=" + dateOfExperiment;
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
	public Integer getOrganisationId() {
		return organisationId;
	}

	/**
	 * @param organisationId the organisationId to set
	 */
	public void setOrganisationId(Integer organisationId) {
		this.organisationId = organisationId;
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
	public String getOrganisation() {
		return organisation;
	}

	/**
	 * @param organisation the organisation to set
	 */
	public void setOrganisation(String organisation) {
		this.organisation = organisation;
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

}
