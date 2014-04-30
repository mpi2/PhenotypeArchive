package uk.ac.ebi.phenotype.pojo;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

@MappedSuperclass
public class StatisticalResult {

	/**
	 * MySQL auto increment
	 * GenerationType.AUTO won't work
	 */
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id")
	protected Integer id;
	
	@OneToOne
	@JoinColumn(name = "control_id")
	protected BiologicalModel controlBiologicalModel;

	@OneToOne
	@JoinColumn(name = "experimental_id")
	protected BiologicalModel experimentalBiologicalModel;

	@OneToOne
	@JoinColumn(name = "organisation_id")
	protected Organisation organisation;

	@OneToOne
	@JoinColumn(name = "parameter_id")
	protected Parameter parameter;
	
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "pipeline_id")
	protected Pipeline pipeline;

	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "project_id")
	protected Project project;
	
	@Column(name = "control_selection_strategy", length=100)
	private String controlSelectionStrategy;
	
	@Column(name="male_controls")
	private Integer maleControls;

	@Column(name="male_mutants")
	private Integer maleMutants;

	@Column(name="female_controls")
	private Integer femaleControls;

	@Column(name="female_mutants")
	private Integer femaleMutants;

	@Column(name = "statistical_method", length=200)
	private String statisticalMethod;
	
	@Column(name = "metadata_group", length=200)
	private String metadataGroup;

	@Column(name = "raw_output")
	private String rawOutput;

	@Transient
	private Double effectSize;
	
	@Transient
	private SexType sexType;
	
	@Transient
	private ZygosityType zygosityType;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public BiologicalModel getControlBiologicalModel() {
		return controlBiologicalModel;
	}

	public void setControlBiologicalModel(BiologicalModel controlBiologicalModel) {
		this.controlBiologicalModel = controlBiologicalModel;
	}

	public BiologicalModel getExperimentalBiologicalModel() {
		return experimentalBiologicalModel;
	}

	public void setExperimentalBiologicalModel(
			BiologicalModel experimentalBiologicalModel) {
		this.experimentalBiologicalModel = experimentalBiologicalModel;
	}

	public Organisation getOrganisation() {
		return organisation;
	}

	public void setOrganisation(Organisation organisation) {
		this.organisation = organisation;
	}

	public Parameter getParameter() {
		return parameter;
	}

	public void setParameter(Parameter parameter) {
		this.parameter = parameter;
	}

	public Pipeline getPipeline() {
		return pipeline;
	}

	public void setPipeline(Pipeline pipeline) {
		this.pipeline = pipeline;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public String getControlSelectionStrategy() {
		return controlSelectionStrategy;
	}

	public void setControlSelectionStrategy(String controlSelectionStrategy) {
		this.controlSelectionStrategy = controlSelectionStrategy;
	}

	public Integer getMaleControls() {
		return maleControls;
	}

	public void setMaleControls(Integer maleControls) {
		this.maleControls = maleControls;
	}

	public Integer getMaleMutants() {
		return maleMutants;
	}

	public void setMaleMutants(Integer maleMutants) {
		this.maleMutants = maleMutants;
	}

	public Integer getFemaleControls() {
		return femaleControls;
	}

	public void setFemaleControls(Integer femaleControls) {
		this.femaleControls = femaleControls;
	}

	public Integer getFemaleMutants() {
		return femaleMutants;
	}

	public void setFemaleMutants(Integer femaleMutants) {
		this.femaleMutants = femaleMutants;
	}

	public String getStatisticalMethod() {
		return statisticalMethod;
	}

	public void setStatisticalMethod(String statisticalMethod) {
		this.statisticalMethod = statisticalMethod;
	}

	public String getMetadataGroup() {
		return metadataGroup;
	}

	public void setMetadataGroup(String metadataGroup) {
		this.metadataGroup = metadataGroup;
	}

	public String getRawOutput() {
		return rawOutput;
	}

	public void setRawOutput(String rawOutput) {
		this.rawOutput = rawOutput;
	}

	public Double getEffectSize() {
		return effectSize;
	}

	public void setEffectSize(Double effectSize) {
		this.effectSize = effectSize;
	}

	public SexType getSexType() {
		return sexType;
	}

	public void setSexType(SexType sexType) {
		this.sexType = sexType;
	}

	public ZygosityType getZygosityType() {
		return zygosityType;
	}

	public void setZygosityType(ZygosityType zygosityType) {
		this.zygosityType = zygosityType;
	}

	@Override
	public String toString() {
		return "StatisticalResult [id=" + id + ", controlBiologicalModel="
				+ controlBiologicalModel + ", experimentalBiologicalModel="
				+ experimentalBiologicalModel + ", organisation="
				+ organisation + ", parameter=" + parameter + ", pipeline="
				+ pipeline + ", project=" + project
				+ ", controlSelectionStrategy=" + controlSelectionStrategy
				+ ", maleControls=" + maleControls + ", maleMutants="
				+ maleMutants + ", femaleControls=" + femaleControls
				+ ", femaleMutants=" + femaleMutants + ", statisticalMethod="
				+ statisticalMethod + ", metadataGroup=" + metadataGroup
				+ ", rawOutput=" + rawOutput + ", effectSize=" + effectSize
				+ ", sexType=" + sexType + ", zygosityType=" + zygosityType
				+ "]";
	}

	

}
