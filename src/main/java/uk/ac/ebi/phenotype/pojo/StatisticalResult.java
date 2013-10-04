package uk.ac.ebi.phenotype.pojo;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;

@MappedSuperclass
public class StatisticalResult {

	@Override
	public String toString() {
		return "StatisticalResult [id=" + id + ", controlBiologicalModel="
				+ controlBiologicalModel + ", experimentalBiologicalModel="
				+ experimentalBiologicalModel + ", organisation="
				+ organisation + ", parameter=" + parameter + "]";
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

}
