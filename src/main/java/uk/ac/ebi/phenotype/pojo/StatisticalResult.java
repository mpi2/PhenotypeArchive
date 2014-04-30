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

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@MappedSuperclass
public class StatisticalResult {

	@Override
	public String toString() {
		return "StatisticalResult [id=" + id + ", controlBiologicalModel="
				+ controlBiologicalModel + ", experimentalBiologicalModel="
				+ experimentalBiologicalModel + ", organisation="
				+ organisation + ", parameter=" + parameter +"metadataGroup="+metadataGroup+ "]";
	}

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
	
	
	public SexType getSexType() {
		return sexType;
	}

	public void setSexType(SexType sexType) {
		this.sexType = sexType;
	}

	public Double getEffectSize() {
		return effectSize;
	}

	public void setEffectSize(Double effectSize) {
		this.effectSize = effectSize;
	}

	@Transient
	ZygosityType zygosityType;
	
	public ZygosityType getZygosityType() {
		return zygosityType;
	}

	public void setZygosityType(ZygosityType zygosityType) {
		this.zygosityType = zygosityType;
	}


	/**
	 * @return the experimentalBiologicalModel
	 */
	public BiologicalModel getExperimentalBiologicalModel() {
		return experimentalBiologicalModel;
	}

	/**
	 * @param experimentalBiologicalModel
	 *            the experimentalBiologicalModel to set
	 */
	public void setExperimentalBiologicalModel(BiologicalModel experimentalBiologicalModel) {
		this.experimentalBiologicalModel = experimentalBiologicalModel;
	}

	/**
	 * @return the controlBiologicalModel
	 */
	public BiologicalModel getControlBiologicalModel() {
		return controlBiologicalModel;
	}

	/**
	 * @param controlBiologicalModel
	 *            the controlBiologicalModel to set
	 */
	public void setControlBiologicalModel(BiologicalModel controlBiologicalModel) {
		this.controlBiologicalModel = controlBiologicalModel;
	}

	/**
	 * @return the organisation
	 */
	public Organisation getOrganisation() {
		return organisation;
	}

	/**
	 * @param organisation the organisation to set
	 */
	public void setOrganisation(Organisation organisation) {
		this.organisation = organisation;
	}

	/**
	 * @return the parameter
	 */
	public Parameter getParameter() {
		return parameter;
	}

	/**
	 * @param parameter
	 *            the parameter to set
	 */
	public void setParameter(Parameter parameter) {
		this.parameter = parameter;
	}
	
	public String getMetadataGroup() {
		return metadataGroup;
	}

	public void setMetadataGroup(String metadataGroup) {
		this.metadataGroup = metadataGroup;
	}
	

}
