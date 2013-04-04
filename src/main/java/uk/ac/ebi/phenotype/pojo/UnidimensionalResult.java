package uk.ac.ebi.phenotype.pojo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * 
 * A representation of the outcome of a statistical test done using a control
 * set of mice and a mutant strain, including the calculated p-value.
 * 
 */
@Entity
@Table(name = "stats_categorical_results")
public class UnidimensionalResult implements Serializable {

	private static final long serialVersionUID = -6887634216189711657L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@Column(name = "population_id")
	private Integer populationId;

	@OneToOne
	@JoinColumn(name = "organisation_id")
	private Organisation organisation;
	
	@OneToOne
	@JoinColumn(name = "parameter_id")
	private Parameter parameter;

	@Column(name = "mixed_model")
	private String mixedModel;

	@Column(name = "dependant_variable")
	private String dependantVariable;

	@Column(name = "batch_significance")
	private Boolean batchSignificance;

	@Column(name = "variance_significance")
	private Boolean varianceSignificance;

	@Column(name = "null_test_significance")
	private double nullTestSignificance;

	@Column(name = "genotype_parameter_estimate")
	private double genotypeParameterEstimate;

	@Column(name = "genotype_stderr_estimate")
	private double genotypeStandardErrorEstimate;

	@Column(name = "genotype_effect_pvalue")
	private double genotypeEffectPValue;

	@Column(name = "gender_parameter_estimate")
	private double genderParameterEstimate;

	@Column(name = "gender_stderr_estimate")
	private double genderStandardErrorEstimate;

	@Column(name = "gender_effect_pvalue")
	private double genderEffectPValue;

	@Column(name = "interaction_parameter_estimate")
	private double interactionParameterEstimate;

	@Column(name = "interaction_stderr_estimate")
	private double interactionStandardErrorEstimate;

	@Column(name = "interaction_effect_pvalue")
	private double interactionEffectPValue;

	@Column(name = "weight_parameter_estimate")
	private double weightParameterEstimate;

	@Column(name = "weight_stderr_estimate")
	private double weightStandardErrorEstimate;

	@Column(name = "weight_effect_pvalue")
	private double weightEffectPValue;

	@Column(name = "gp1_genotype")
	private String gp1Genotype;

	@Column(name = "gp1_residuals_normality_test")
	private double gp1ResidualsNormalityTest;

	@Column(name = "gp2_genotype")
	private String gp2Genotype;

	@Column(name = "gp2_residuals_normality_test")
	private double gp2ResidualsNormalityTest;

	@Column(name = "blups_test")
	private double blupsTest;

	@Column(name = "rotated_residuals_normality_test")
	private double rotatedResidualsNormalityTest;

	@Column(name = "intercept_estimate")
	private double interceptEstimate;

	@Column(name = "intercept_estimate_stderr")
	private double interceptEstimateStandardError;
	
	/**
	 * pValue is reported by the mixed model code as the nullTestSignificance
	 * Rather than store pValue twice, just alias the getter/setter
	 * @return
	 */
	public double getpValue() {
		return nullTestSignificance;
	}

	public void setpValue(double pValue) {
		this.nullTestSignificance = pValue;
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getPopulationId() {
		return populationId;
	}

	public void setPopulationId(Integer populationId) {
		this.populationId = populationId;
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

	public String getMixedModel() {
		return mixedModel;
	}

	public void setMixedModel(String mixedModel) {
		this.mixedModel = mixedModel;
	}

	public String getDependantVariable() {
		return dependantVariable;
	}

	public void setDependantVariable(String dependantVariable) {
		this.dependantVariable = dependantVariable;
	}

	public Boolean getBatchSignificance() {
		return batchSignificance;
	}

	public void setBatchSignificance(Boolean batchSignificance) {
		this.batchSignificance = batchSignificance;
	}

	public Boolean getVarianceSignificance() {
		return varianceSignificance;
	}

	public void setVarianceSignificance(Boolean varianceSignificance) {
		this.varianceSignificance = varianceSignificance;
	}

	public double getNullTestSignificance() {
		return nullTestSignificance;
	}

	public void setNullTestSignificance(double nullTestSignificance) {
		this.nullTestSignificance = nullTestSignificance;
	}

	public double getGenotypeParameterEstimate() {
		return genotypeParameterEstimate;
	}

	public void setGenotypeParameterEstimate(double genotypeParameterEstimate) {
		this.genotypeParameterEstimate = genotypeParameterEstimate;
	}

	public double getGenotypeStandardErrorEstimate() {
		return genotypeStandardErrorEstimate;
	}

	public void setGenotypeStandardErrorEstimate(
			double genotypeStandardErrorEstimate) {
		this.genotypeStandardErrorEstimate = genotypeStandardErrorEstimate;
	}

	public double getGenotypeEffectPValue() {
		return genotypeEffectPValue;
	}

	public void setGenotypeEffectPValue(double genotypeEffectPValue) {
		this.genotypeEffectPValue = genotypeEffectPValue;
	}

	public double getGenderParameterEstimate() {
		return genderParameterEstimate;
	}

	public void setGenderParameterEstimate(double genderParameterEstimate) {
		this.genderParameterEstimate = genderParameterEstimate;
	}

	public double getGenderStandardErrorEstimate() {
		return genderStandardErrorEstimate;
	}

	public void setGenderStandardErrorEstimate(double genderStandardErrorEstimate) {
		this.genderStandardErrorEstimate = genderStandardErrorEstimate;
	}

	public double getGenderEffectPValue() {
		return genderEffectPValue;
	}

	public void setGenderEffectPValue(double genderEffectPValue) {
		this.genderEffectPValue = genderEffectPValue;
	}

	public double getInteractionParameterEstimate() {
		return interactionParameterEstimate;
	}

	public void setInteractionParameterEstimate(double interactionParameterEstimate) {
		this.interactionParameterEstimate = interactionParameterEstimate;
	}

	public double getInteractionStandardErrorEstimate() {
		return interactionStandardErrorEstimate;
	}

	public void setInteractionStandardErrorEstimate(
			double interactionStandardErrorEstimate) {
		this.interactionStandardErrorEstimate = interactionStandardErrorEstimate;
	}

	public double getInteractionEffectPValue() {
		return interactionEffectPValue;
	}

	public void setInteractionEffectPValue(double interactionEffectPValue) {
		this.interactionEffectPValue = interactionEffectPValue;
	}

	public double getWeightParameterEstimate() {
		return weightParameterEstimate;
	}

	public void setWeightParameterEstimate(double weightParameterEstimate) {
		this.weightParameterEstimate = weightParameterEstimate;
	}

	public double getWeightStandardErrorEstimate() {
		return weightStandardErrorEstimate;
	}

	public void setWeightStandardErrorEstimate(double weightStandardErrorEstimate) {
		this.weightStandardErrorEstimate = weightStandardErrorEstimate;
	}

	public double getWeightEffectPValue() {
		return weightEffectPValue;
	}

	public void setWeightEffectPValue(double weightEffectPValue) {
		this.weightEffectPValue = weightEffectPValue;
	}

	public String getGp1Genotype() {
		return gp1Genotype;
	}

	public void setGp1Genotype(String gp1Genotype) {
		this.gp1Genotype = gp1Genotype;
	}

	public double getGp1ResidualsNormalityTest() {
		return gp1ResidualsNormalityTest;
	}

	public void setGp1ResidualsNormalityTest(double gp1ResidualsNormalityTest) {
		this.gp1ResidualsNormalityTest = gp1ResidualsNormalityTest;
	}

	public String getGp2Genotype() {
		return gp2Genotype;
	}

	public void setGp2Genotype(String gp2Genotype) {
		this.gp2Genotype = gp2Genotype;
	}

	public double getGp2ResidualsNormalityTest() {
		return gp2ResidualsNormalityTest;
	}

	public void setGp2ResidualsNormalityTest(double gp2ResidualsNormalityTest) {
		this.gp2ResidualsNormalityTest = gp2ResidualsNormalityTest;
	}

	public double getBlupsTest() {
		return blupsTest;
	}

	public void setBlupsTest(double blupsTest) {
		this.blupsTest = blupsTest;
	}

	public double getRotatedResidualsNormalityTest() {
		return rotatedResidualsNormalityTest;
	}

	public void setRotatedResidualsNormalityTest(
			double rotatedResidualsNormalityTest) {
		this.rotatedResidualsNormalityTest = rotatedResidualsNormalityTest;
	}

	public double getInterceptEstimate() {
		return interceptEstimate;
	}

	public void setInterceptEstimate(double interceptEstimate) {
		this.interceptEstimate = interceptEstimate;
	}

	public double getInterceptEstimateStandardError() {
		return interceptEstimateStandardError;
	}

	public void setInterceptEstimateStandardError(
			double interceptEstimateStandardError) {
		this.interceptEstimateStandardError = interceptEstimateStandardError;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((batchSignificance == null) ? 0 : batchSignificance
						.hashCode());
		long temp;
		temp = Double.doubleToLongBits(blupsTest);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime
				* result
				+ ((dependantVariable == null) ? 0 : dependantVariable
						.hashCode());
		temp = Double.doubleToLongBits(genderEffectPValue);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(genderParameterEstimate);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(genderStandardErrorEstimate);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(genotypeEffectPValue);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(genotypeParameterEstimate);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(genotypeStandardErrorEstimate);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result
				+ ((gp1Genotype == null) ? 0 : gp1Genotype.hashCode());
		temp = Double.doubleToLongBits(gp1ResidualsNormalityTest);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result
				+ ((gp2Genotype == null) ? 0 : gp2Genotype.hashCode());
		temp = Double.doubleToLongBits(gp2ResidualsNormalityTest);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		temp = Double.doubleToLongBits(interactionEffectPValue);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(interactionParameterEstimate);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(interactionStandardErrorEstimate);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(interceptEstimate);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(interceptEstimateStandardError);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result
				+ ((mixedModel == null) ? 0 : mixedModel.hashCode());
		temp = Double.doubleToLongBits(nullTestSignificance);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result
				+ ((organisation == null) ? 0 : organisation.hashCode());
		result = prime * result
				+ ((parameter == null) ? 0 : parameter.hashCode());
		result = prime * result
				+ ((populationId == null) ? 0 : populationId.hashCode());
		temp = Double.doubleToLongBits(rotatedResidualsNormalityTest);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime
				* result
				+ ((varianceSignificance == null) ? 0 : varianceSignificance
						.hashCode());
		temp = Double.doubleToLongBits(weightEffectPValue);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(weightParameterEstimate);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(weightStandardErrorEstimate);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UnidimensionalResult other = (UnidimensionalResult) obj;
		if (batchSignificance == null) {
			if (other.batchSignificance != null)
				return false;
		} else if (!batchSignificance.equals(other.batchSignificance))
			return false;
		if (Double.doubleToLongBits(blupsTest) != Double
				.doubleToLongBits(other.blupsTest))
			return false;
		if (dependantVariable == null) {
			if (other.dependantVariable != null)
				return false;
		} else if (!dependantVariable.equals(other.dependantVariable))
			return false;
		if (Double.doubleToLongBits(genderEffectPValue) != Double
				.doubleToLongBits(other.genderEffectPValue))
			return false;
		if (Double.doubleToLongBits(genderParameterEstimate) != Double
				.doubleToLongBits(other.genderParameterEstimate))
			return false;
		if (Double.doubleToLongBits(genderStandardErrorEstimate) != Double
				.doubleToLongBits(other.genderStandardErrorEstimate))
			return false;
		if (Double.doubleToLongBits(genotypeEffectPValue) != Double
				.doubleToLongBits(other.genotypeEffectPValue))
			return false;
		if (Double.doubleToLongBits(genotypeParameterEstimate) != Double
				.doubleToLongBits(other.genotypeParameterEstimate))
			return false;
		if (Double.doubleToLongBits(genotypeStandardErrorEstimate) != Double
				.doubleToLongBits(other.genotypeStandardErrorEstimate))
			return false;
		if (gp1Genotype == null) {
			if (other.gp1Genotype != null)
				return false;
		} else if (!gp1Genotype.equals(other.gp1Genotype))
			return false;
		if (Double.doubleToLongBits(gp1ResidualsNormalityTest) != Double
				.doubleToLongBits(other.gp1ResidualsNormalityTest))
			return false;
		if (gp2Genotype == null) {
			if (other.gp2Genotype != null)
				return false;
		} else if (!gp2Genotype.equals(other.gp2Genotype))
			return false;
		if (Double.doubleToLongBits(gp2ResidualsNormalityTest) != Double
				.doubleToLongBits(other.gp2ResidualsNormalityTest))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (Double.doubleToLongBits(interactionEffectPValue) != Double
				.doubleToLongBits(other.interactionEffectPValue))
			return false;
		if (Double.doubleToLongBits(interactionParameterEstimate) != Double
				.doubleToLongBits(other.interactionParameterEstimate))
			return false;
		if (Double.doubleToLongBits(interactionStandardErrorEstimate) != Double
				.doubleToLongBits(other.interactionStandardErrorEstimate))
			return false;
		if (Double.doubleToLongBits(interceptEstimate) != Double
				.doubleToLongBits(other.interceptEstimate))
			return false;
		if (Double.doubleToLongBits(interceptEstimateStandardError) != Double
				.doubleToLongBits(other.interceptEstimateStandardError))
			return false;
		if (mixedModel == null) {
			if (other.mixedModel != null)
				return false;
		} else if (!mixedModel.equals(other.mixedModel))
			return false;
		if (Double.doubleToLongBits(nullTestSignificance) != Double
				.doubleToLongBits(other.nullTestSignificance))
			return false;
		if (organisation == null) {
			if (other.organisation != null)
				return false;
		} else if (!organisation.equals(other.organisation))
			return false;
		if (parameter == null) {
			if (other.parameter != null)
				return false;
		} else if (!parameter.equals(other.parameter))
			return false;
		if (populationId == null) {
			if (other.populationId != null)
				return false;
		} else if (!populationId.equals(other.populationId))
			return false;
		if (Double.doubleToLongBits(rotatedResidualsNormalityTest) != Double
				.doubleToLongBits(other.rotatedResidualsNormalityTest))
			return false;
		if (varianceSignificance == null) {
			if (other.varianceSignificance != null)
				return false;
		} else if (!varianceSignificance.equals(other.varianceSignificance))
			return false;
		if (Double.doubleToLongBits(weightEffectPValue) != Double
				.doubleToLongBits(other.weightEffectPValue))
			return false;
		if (Double.doubleToLongBits(weightParameterEstimate) != Double
				.doubleToLongBits(other.weightParameterEstimate))
			return false;
		if (Double.doubleToLongBits(weightStandardErrorEstimate) != Double
				.doubleToLongBits(other.weightStandardErrorEstimate))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "UnidimensionalResult [id=" + id + ", populationId="
				+ populationId + ", organisation=" + organisation
				+ ", parameter=" + parameter + ", mixedModel=" + mixedModel
				+ ", dependantVariable=" + dependantVariable
				+ ", batchSignificance=" + batchSignificance
				+ ", varianceSignificance=" + varianceSignificance
				+ ", nullTestSignificance=" + nullTestSignificance
				+ ", genotypeParameterEstimate=" + genotypeParameterEstimate
				+ ", genotypeStandardErrorEstimate="
				+ genotypeStandardErrorEstimate + ", genotypeEffectPValue="
				+ genotypeEffectPValue + ", genderParameterEstimate="
				+ genderParameterEstimate + ", genderStandardErrorEstimate="
				+ genderStandardErrorEstimate + ", genderEffectPValue="
				+ genderEffectPValue + ", interactionParameterEstimate="
				+ interactionParameterEstimate
				+ ", interactionStandardErrorEstimate="
				+ interactionStandardErrorEstimate
				+ ", interactionEffectPValue=" + interactionEffectPValue
				+ ", weightParameterEstimate=" + weightParameterEstimate
				+ ", weightStandardErrorEstimate="
				+ weightStandardErrorEstimate + ", weightEffectPValue="
				+ weightEffectPValue + ", gp1Genotype=" + gp1Genotype
				+ ", gp1ResidualsNormalityTest=" + gp1ResidualsNormalityTest
				+ ", gp2Genotype=" + gp2Genotype
				+ ", gp2ResidualsNormalityTest=" + gp2ResidualsNormalityTest
				+ ", blupsTest=" + blupsTest
				+ ", rotatedResidualsNormalityTest="
				+ rotatedResidualsNormalityTest + ", interceptEstimate="
				+ interceptEstimate + ", interceptEstimateStandardError="
				+ interceptEstimateStandardError + "]";
	}


}
