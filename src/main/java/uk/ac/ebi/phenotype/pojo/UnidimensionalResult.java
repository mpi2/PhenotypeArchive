package uk.ac.ebi.phenotype.pojo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import uk.ac.ebi.phenotype.stats.SignificantType;

/**
 * 
 * A representation of the outcome of a statistical test done using a control
 * set of mice and a mutant strain, including the calculated p-value.
 * 
 */
@Entity
@Table(name = "stats_unidimensional_results")
public class UnidimensionalResult extends StatisticalResult implements Serializable {

    private static final float PHENOTYPE_THRESHOLD = 0.05f;

    private static final long serialVersionUID = -6887634216189711657L;

    @Enumerated(EnumType.STRING)
    @Column(name = "experimental_zygosity")
    private ZygosityType experimentalZygosity;

    @Column(name = "batch_significance")
    private Boolean batchSignificance;

    @Column(name = "variance_significance")
    private Boolean varianceSignificance;

    @Column(name = "null_test_significance")
    private Double nullTestSignificance;

    @Column(name = "genotype_parameter_estimate")
    private Double genotypeParameterEstimate;

    @Column(name = "genotype_stderr_estimate")
    private Double genotypeStandardErrorEstimate;

    @Column(name = "genotype_effect_pvalue")
    private Double genotypeEffectPValue;

    @Column(name = "gender_parameter_estimate")
    private Double genderParameterEstimate;

    @Column(name = "gender_stderr_estimate")
    private Double genderStandardErrorEstimate;

    @Column(name = "gender_effect_pvalue")
    private Double genderEffectPValue;

    @Column(name = "weight_parameter_estimate")
    private Double weightParameterEstimate;

    @Column(name = "weight_stderr_estimate")
    private Double weightStandardErrorEstimate;

    @Column(name = "weight_effect_pvalue")
    private Double weightEffectPValue;

    @Column(name = "gp1_genotype")
    private String gp1Genotype;

    @Column(name = "gp1_residuals_normality_test")
    private Double gp1ResidualsNormalityTest;

    @Column(name = "gp2_genotype")
    private String gp2Genotype;

    @Column(name = "gp2_residuals_normality_test")
    private Double gp2ResidualsNormalityTest;

    @Column(name = "blups_test")
    private Double blupsTest;

    @Column(name = "rotated_residuals_normality_test")
    private Double rotatedResidualsNormalityTest;

    @Column(name = "intercept_estimate")
    private Double interceptEstimate;

    @Column(name = "intercept_stderr_estimate")
    private Double interceptEstimateStandardError;

    @Column(name = "interaction_significance")
    private Boolean interactionSignificance;

    @Column(name = "interaction_effect_pvalue")
    private Double interactionEffectPValue;

    @Column(name = "gender_female_ko_estimate")
    private Double genderFemaleKoEstimate;

    @Column(name = "gender_female_ko_stderr_estimate")
    private Double genderFemaleKoStandardErrorEstimate;

    @Column(name = "gender_female_ko_pvalue")
    private Double genderFemaleKoPValue;

    @Column(name = "gender_male_ko_estimate")
    private Double genderMaleKoEstimate;

    @Column(name = "gender_male_ko_stderr_estimate")
    private Double genderMaleKoStandardErrorEstimate;

    @Column(name = "gender_male_ko_pvalue")
    private Double genderMaleKoPValue;

    /**
     * pValue is reported by the mixed model code as the nullTestSignificance
     * Rather than store pValue twice, just alias the getter/setter
     * 
     * @return
     */
    public Double getpValue() {
        return nullTestSignificance;
    }

    public void setpValue(Double pValue) {
        this.nullTestSignificance = pValue;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ZygosityType getExperimentalZygosity() {
        return experimentalZygosity;
    }

    public void setExperimentalZygosity(ZygosityType experimentalZygosity) {
        this.experimentalZygosity = experimentalZygosity;
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

    public Double getNullTestSignificance() {
        return nullTestSignificance;
    }

    public void setNullTestSignificance(Double nullTestSignificance) {
        this.nullTestSignificance = nullTestSignificance;
    }

    public Double getGenotypeParameterEstimate() {
        return genotypeParameterEstimate;
    }

    public void setGenotypeParameterEstimate(Double genotypeParameterEstimate) {
        this.genotypeParameterEstimate = genotypeParameterEstimate;
    }

    public Double getGenotypeStandardErrorEstimate() {
        return genotypeStandardErrorEstimate;
    }

    public void setGenotypeStandardErrorEstimate(
            Double genotypeStandardErrorEstimate) {
        this.genotypeStandardErrorEstimate = genotypeStandardErrorEstimate;
    }

    public Double getGenotypeEffectPValue() {
        return genotypeEffectPValue;
    }

    public void setGenotypeEffectPValue(Double genotypeEffectPValue) {
        this.genotypeEffectPValue = genotypeEffectPValue;
    }

    public Double getGenderParameterEstimate() {
        return genderParameterEstimate;
    }

    public void setGenderParameterEstimate(Double genderParameterEstimate) {
        this.genderParameterEstimate = genderParameterEstimate;
    }

    public Double getGenderStandardErrorEstimate() {
        return genderStandardErrorEstimate;
    }

    public void setGenderStandardErrorEstimate(Double genderStandardErrorEstimate) {
        this.genderStandardErrorEstimate = genderStandardErrorEstimate;
    }

    public Double getGenderEffectPValue() {
        return genderEffectPValue;
    }

    public void setGenderEffectPValue(Double genderEffectPValue) {
        this.genderEffectPValue = genderEffectPValue;
    }

    public Double getWeightParameterEstimate() {
        return weightParameterEstimate;
    }

    public void setWeightParameterEstimate(Double weightParameterEstimate) {
        this.weightParameterEstimate = weightParameterEstimate;
    }

    public Double getWeightStandardErrorEstimate() {
        return weightStandardErrorEstimate;
    }

    public void setWeightStandardErrorEstimate(Double weightStandardErrorEstimate) {
        this.weightStandardErrorEstimate = weightStandardErrorEstimate;
    }

    public Double getWeightEffectPValue() {
        return weightEffectPValue;
    }

    public void setWeightEffectPValue(Double weightEffectPValue) {
        this.weightEffectPValue = weightEffectPValue;
    }

    public String getGp1Genotype() {
        return gp1Genotype;
    }

    public void setGp1Genotype(String gp1Genotype) {
        this.gp1Genotype = gp1Genotype;
    }

    public Double getGp1ResidualsNormalityTest() {
        return gp1ResidualsNormalityTest;
    }

    public void setGp1ResidualsNormalityTest(Double gp1ResidualsNormalityTest) {
        this.gp1ResidualsNormalityTest = gp1ResidualsNormalityTest;
    }

    public String getGp2Genotype() {
        return gp2Genotype;
    }

    public void setGp2Genotype(String gp2Genotype) {
        this.gp2Genotype = gp2Genotype;
    }

    public Double getGp2ResidualsNormalityTest() {
        return gp2ResidualsNormalityTest;
    }

    public void setGp2ResidualsNormalityTest(Double gp2ResidualsNormalityTest) {
        this.gp2ResidualsNormalityTest = gp2ResidualsNormalityTest;
    }

    public Double getBlupsTest() {
        return blupsTest;
    }

    public void setBlupsTest(Double blupsTest) {
        this.blupsTest = blupsTest;
    }

    public Double getRotatedResidualsNormalityTest() {
        return rotatedResidualsNormalityTest;
    }

    public void setRotatedResidualsNormalityTest(
            Double rotatedResidualsNormalityTest) {
        this.rotatedResidualsNormalityTest = rotatedResidualsNormalityTest;
    }

    public Double getInterceptEstimate() {
        return interceptEstimate;
    }

    public void setInterceptEstimate(Double interceptEstimate) {
        this.interceptEstimate = interceptEstimate;
    }

    public Double getInterceptEstimateStandardError() {
        return interceptEstimateStandardError;
    }

    public void setInterceptEstimateStandardError(
            Double interceptEstimateStandardError) {
        this.interceptEstimateStandardError = interceptEstimateStandardError;
    }

    public Boolean getInteractionSignificance() {
        return interactionSignificance;
    }

    public void setInteractionSignificance(Boolean interactionSignificance) {
        this.interactionSignificance = interactionSignificance;
    }

    public Double getInteractionEffectPValue() {
        return interactionEffectPValue;
    }

    public void setInteractionEffectPValue(Double interactionEffectPValue) {
        this.interactionEffectPValue = interactionEffectPValue;
    }

    public Double getGenderFemaleKoEstimate() {
        return genderFemaleKoEstimate;
    }

    public void setGenderFemaleKoEstimate(Double genderFemaleKoEstimate) {
        this.genderFemaleKoEstimate = genderFemaleKoEstimate;
    }

    public Double getGenderFemaleKoStandardErrorEstimate() {
        return genderFemaleKoStandardErrorEstimate;
    }

    public void setGenderFemaleKoStandardErrorEstimate(
            Double genderFemaleKoStandardErrorEstimate) {
        this.genderFemaleKoStandardErrorEstimate = genderFemaleKoStandardErrorEstimate;
    }

    public Double getGenderFemaleKoPValue() {
        return genderFemaleKoPValue;
    }

    public void setGenderFemaleKoPValue(Double genderFemaleKoPValue) {
        this.genderFemaleKoPValue = genderFemaleKoPValue;
    }

    public Double getGenderMaleKoEstimate() {
        return genderMaleKoEstimate;
    }

    public void setGenderMaleKoEstimate(Double genderMaleKoEstimate) {
        this.genderMaleKoEstimate = genderMaleKoEstimate;
    }

    public Double getGenderMaleKoStandardErrorEstimate() {
        return genderMaleKoStandardErrorEstimate;
    }

    public void setGenderMaleKoStandardErrorEstimate(
            Double genderMaleKoStandardErrorEstimate) {
        this.genderMaleKoStandardErrorEstimate = genderMaleKoStandardErrorEstimate;
    }

    public Double getGenderMaleKoPValue() {
        return genderMaleKoPValue;
    }

    public void setGenderMaleKoPValue(Double genderMaleKoPValue) {
        this.genderMaleKoPValue = genderMaleKoPValue;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime
                * result
                + ((batchSignificance == null) ? 0 : batchSignificance
                        .hashCode());
        result = prime * result
                + ((blupsTest == null) ? 0 : blupsTest.hashCode());
        result = prime
                * result
                + ((controlBiologicalModel == null) ? 0
                        : controlBiologicalModel.hashCode());
        result = prime
                * result
                + ((experimentalZygosity == null) ? 0 : experimentalZygosity
                        .hashCode());
        result = prime
                * result
                + ((genderEffectPValue == null) ? 0 : genderEffectPValue
                        .hashCode());
        result = prime
                * result
                + ((genderFemaleKoEstimate == null) ? 0
                        : genderFemaleKoEstimate.hashCode());
        result = prime
                * result
                + ((genderFemaleKoPValue == null) ? 0 : genderFemaleKoPValue
                        .hashCode());
        result = prime
                * result
                + ((genderFemaleKoStandardErrorEstimate == null) ? 0
                        : genderFemaleKoStandardErrorEstimate.hashCode());
        result = prime
                * result
                + ((genderMaleKoEstimate == null) ? 0 : genderMaleKoEstimate
                        .hashCode());
        result = prime
                * result
                + ((genderMaleKoPValue == null) ? 0 : genderMaleKoPValue
                        .hashCode());
        result = prime
                * result
                + ((genderMaleKoStandardErrorEstimate == null) ? 0
                        : genderMaleKoStandardErrorEstimate.hashCode());
        result = prime
                * result
                + ((genderParameterEstimate == null) ? 0
                        : genderParameterEstimate.hashCode());
        result = prime
                * result
                + ((genderStandardErrorEstimate == null) ? 0
                        : genderStandardErrorEstimate.hashCode());
        result = prime
                * result
                + ((genotypeEffectPValue == null) ? 0 : genotypeEffectPValue
                        .hashCode());
        result = prime
                * result
                + ((genotypeParameterEstimate == null) ? 0
                        : genotypeParameterEstimate.hashCode());
        result = prime
                * result
                + ((genotypeStandardErrorEstimate == null) ? 0
                        : genotypeStandardErrorEstimate.hashCode());
        result = prime * result
                + ((gp1Genotype == null) ? 0 : gp1Genotype.hashCode());
        result = prime
                * result
                + ((gp1ResidualsNormalityTest == null) ? 0
                        : gp1ResidualsNormalityTest.hashCode());
        result = prime * result
                + ((gp2Genotype == null) ? 0 : gp2Genotype.hashCode());
        result = prime
                * result
                + ((gp2ResidualsNormalityTest == null) ? 0
                        : gp2ResidualsNormalityTest.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime
                * result
                + ((interactionEffectPValue == null) ? 0
                        : interactionEffectPValue.hashCode());
        result = prime
                * result
                + ((interactionSignificance == null) ? 0
                        : interactionSignificance.hashCode());
        result = prime
                * result
                + ((interceptEstimate == null) ? 0 : interceptEstimate
                        .hashCode());
        result = prime
                * result
                + ((interceptEstimateStandardError == null) ? 0
                        : interceptEstimateStandardError.hashCode());
        result = prime
                * result
                + ((experimentalBiologicalModel == null) ? 0 : experimentalBiologicalModel
                        .hashCode());
        result = prime
                * result
                + ((nullTestSignificance == null) ? 0 : nullTestSignificance
                        .hashCode());
        result = prime * result
                + ((organisation == null) ? 0 : organisation.hashCode());
        result = prime * result
                + ((parameter == null) ? 0 : parameter.hashCode());
        result = prime
                * result
                + ((rotatedResidualsNormalityTest == null) ? 0
                        : rotatedResidualsNormalityTest.hashCode());
        result = prime
                * result
                + ((varianceSignificance == null) ? 0 : varianceSignificance
                        .hashCode());
        result = prime
                * result
                + ((weightEffectPValue == null) ? 0 : weightEffectPValue
                        .hashCode());
        result = prime
                * result
                + ((weightParameterEstimate == null) ? 0
                        : weightParameterEstimate.hashCode());
        result = prime
                * result
                + ((weightStandardErrorEstimate == null) ? 0
                        : weightStandardErrorEstimate.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) { return true; }
        if (obj == null) { return false; }
        if (getClass() != obj.getClass()) { return false; }
        UnidimensionalResult other = (UnidimensionalResult) obj;
        if (batchSignificance == null) {
            if (other.batchSignificance != null) { return false; }
        } else if (!batchSignificance.equals(other.batchSignificance)) { return false; }
        if (blupsTest == null) {
            if (other.blupsTest != null) { return false; }
        } else if (!blupsTest.equals(other.blupsTest)) { return false; }
        if (controlBiologicalModel == null) {
            if (other.controlBiologicalModel != null) { return false; }
        } else if (!controlBiologicalModel.equals(other.controlBiologicalModel)) { return false; }
        if (experimentalZygosity != other.experimentalZygosity) { return false; }
        if (genderEffectPValue == null) {
            if (other.genderEffectPValue != null) { return false; }
        } else if (!genderEffectPValue.equals(other.genderEffectPValue)) { return false; }
        if (genderFemaleKoEstimate == null) {
            if (other.genderFemaleKoEstimate != null) { return false; }
        } else if (!genderFemaleKoEstimate.equals(other.genderFemaleKoEstimate)) { return false; }
        if (genderFemaleKoPValue == null) {
            if (other.genderFemaleKoPValue != null) { return false; }
        } else if (!genderFemaleKoPValue.equals(other.genderFemaleKoPValue)) { return false; }
        if (genderFemaleKoStandardErrorEstimate == null) {
            if (other.genderFemaleKoStandardErrorEstimate != null) { return false; }
        } else if (!genderFemaleKoStandardErrorEstimate
                .equals(other.genderFemaleKoStandardErrorEstimate)) { return false; }
        if (genderMaleKoEstimate == null) {
            if (other.genderMaleKoEstimate != null) { return false; }
        } else if (!genderMaleKoEstimate.equals(other.genderMaleKoEstimate)) { return false; }
        if (genderMaleKoPValue == null) {
            if (other.genderMaleKoPValue != null) { return false; }
        } else if (!genderMaleKoPValue.equals(other.genderMaleKoPValue)) { return false; }
        if (genderMaleKoStandardErrorEstimate == null) {
            if (other.genderMaleKoStandardErrorEstimate != null) { return false; }
        } else if (!genderMaleKoStandardErrorEstimate
                .equals(other.genderMaleKoStandardErrorEstimate)) { return false; }
        if (genderParameterEstimate == null) {
            if (other.genderParameterEstimate != null) { return false; }
        } else if (!genderParameterEstimate
                .equals(other.genderParameterEstimate)) { return false; }
        if (genderStandardErrorEstimate == null) {
            if (other.genderStandardErrorEstimate != null) { return false; }
        } else if (!genderStandardErrorEstimate
                .equals(other.genderStandardErrorEstimate)) { return false; }
        if (genotypeEffectPValue == null) {
            if (other.genotypeEffectPValue != null) { return false; }
        } else if (!genotypeEffectPValue.equals(other.genotypeEffectPValue)) { return false; }
        if (genotypeParameterEstimate == null) {
            if (other.genotypeParameterEstimate != null) { return false; }
        } else if (!genotypeParameterEstimate
                .equals(other.genotypeParameterEstimate)) { return false; }
        if (genotypeStandardErrorEstimate == null) {
            if (other.genotypeStandardErrorEstimate != null) { return false; }
        } else if (!genotypeStandardErrorEstimate
                .equals(other.genotypeStandardErrorEstimate)) { return false; }
        if (gp1Genotype == null) {
            if (other.gp1Genotype != null) { return false; }
        } else if (!gp1Genotype.equals(other.gp1Genotype)) { return false; }
        if (gp1ResidualsNormalityTest == null) {
            if (other.gp1ResidualsNormalityTest != null) { return false; }
        } else if (!gp1ResidualsNormalityTest
                .equals(other.gp1ResidualsNormalityTest)) { return false; }
        if (gp2Genotype == null) {
            if (other.gp2Genotype != null) { return false; }
        } else if (!gp2Genotype.equals(other.gp2Genotype)) { return false; }
        if (gp2ResidualsNormalityTest == null) {
            if (other.gp2ResidualsNormalityTest != null) { return false; }
        } else if (!gp2ResidualsNormalityTest
                .equals(other.gp2ResidualsNormalityTest)) { return false; }
        if (id == null) {
            if (other.id != null) { return false; }
        } else if (!id.equals(other.id)) { return false; }
        if (interactionEffectPValue == null) {
            if (other.interactionEffectPValue != null) { return false; }
        } else if (!interactionEffectPValue
                .equals(other.interactionEffectPValue)) { return false; }
        if (interactionSignificance == null) {
            if (other.interactionSignificance != null) { return false; }
        } else if (!interactionSignificance
                .equals(other.interactionSignificance)) { return false; }
        if (interceptEstimate == null) {
            if (other.interceptEstimate != null) { return false; }
        } else if (!interceptEstimate.equals(other.interceptEstimate)) { return false; }
        if (interceptEstimateStandardError == null) {
            if (other.interceptEstimateStandardError != null) { return false; }
        } else if (!interceptEstimateStandardError
                .equals(other.interceptEstimateStandardError)) { return false; }
        if (experimentalBiologicalModel == null) {
            if (other.experimentalBiologicalModel != null) { return false; }
        } else if (!experimentalBiologicalModel.equals(other.experimentalBiologicalModel)) { return false; }
        if (nullTestSignificance == null) {
            if (other.nullTestSignificance != null) { return false; }
        } else if (!nullTestSignificance.equals(other.nullTestSignificance)) { return false; }
        if (organisation == null) {
            if (other.organisation != null) { return false; }
        } else if (!organisation.equals(other.organisation)) { return false; }
        if (parameter == null) {
            if (other.parameter != null) { return false; }
        } else if (!parameter.equals(other.parameter)) { return false; }
        if (rotatedResidualsNormalityTest == null) {
            if (other.rotatedResidualsNormalityTest != null) { return false; }
        } else if (!rotatedResidualsNormalityTest
                .equals(other.rotatedResidualsNormalityTest)) { return false; }
        if (varianceSignificance == null) {
            if (other.varianceSignificance != null) { return false; }
        } else if (!varianceSignificance.equals(other.varianceSignificance)) { return false; }
        if (weightEffectPValue == null) {
            if (other.weightEffectPValue != null) { return false; }
        } else if (!weightEffectPValue.equals(other.weightEffectPValue)) { return false; }
        if (weightParameterEstimate == null) {
            if (other.weightParameterEstimate != null) { return false; }
        } else if (!weightParameterEstimate
                .equals(other.weightParameterEstimate)) { return false; }
        if (weightStandardErrorEstimate == null) {
            if (other.weightStandardErrorEstimate != null) { return false; }
        } else if (!weightStandardErrorEstimate
                .equals(other.weightStandardErrorEstimate)) { return false; }
        return true;
    }

    @Override
    public String toString() {
        return "UnidimensionalResult ["
                + ", controlBiologicalModelId=" + ((controlBiologicalModel != null) ? controlBiologicalModel.getId() : null)
                + ", mutantBiologicalModelId=" + ((experimentalBiologicalModel != null) ? experimentalBiologicalModel.getId() : null)
                + ", experimentalZygosity=" + experimentalZygosity
                + ", organisation=" + ((organisation != null) ? organisation.getName() : null)
                + ", parameter=" + ((parameter != null) ? parameter.getStableId() : null)
                + ", batchSignificance=" + batchSignificance
                + ", varianceSignificance=" + varianceSignificance
                + ", nullTestSignificance=" + nullTestSignificance
                + ", genotypeParameterEstimate=" + genotypeParameterEstimate
                + ", genotypeStandardErrorEstimate=" + genotypeStandardErrorEstimate
                + ", genotypeEffectPValue=" + genotypeEffectPValue
                + ", genderParameterEstimate=" + genderParameterEstimate
                + ", genderStandardErrorEstimate=" + genderStandardErrorEstimate
                + ", genderEffectPValue=" + genderEffectPValue
                + ", weightParameterEstimate=" + weightParameterEstimate
                + ", weightStandardErrorEstimate=" + weightStandardErrorEstimate
                + ", weightEffectPValue=" + weightEffectPValue
                + ", gp1Genotype=" + gp1Genotype
                + ", gp1ResidualsNormalityTest=" + gp1ResidualsNormalityTest
                + ", gp2Genotype=" + gp2Genotype
                + ", gp2ResidualsNormalityTest=" + gp2ResidualsNormalityTest
                + ", blupsTest=" + blupsTest
                + ", rotatedResidualsNormalityTest=" + rotatedResidualsNormalityTest
                + ", interceptEstimate=" + interceptEstimate
                + ", interceptEstimateStandardError=" + interceptEstimateStandardError
                + ", interactionSignificance=" + interactionSignificance
                + ", interactionEffectPValue=" + interactionEffectPValue
                + ", genderFemaleKoEstimate=" + genderFemaleKoEstimate
                + ", genderFemaleKoStandardErrorEstimate=" + genderFemaleKoStandardErrorEstimate
                + ", genderFemaleKoPValue=" + genderFemaleKoPValue
                + ", genderMaleKoEstimate=" + genderMaleKoEstimate
                + ", genderMaleKoStandardErrorEstimate=" + genderMaleKoStandardErrorEstimate
                + ", genderMaleKoPValue=" + genderMaleKoPValue + "]";
    }

    /**
     * return the correct classification enum for this unidimensional result.
     * 
     * @return SignificantType classification of the type of significance found
     *         for the result of the mixed model calculation on this data
     */
    public SignificantType getSignificanceClassification() {

        if (getNullTestSignificance() == null) {
            return null;
        } else if (getNullTestSignificance() > PHENOTYPE_THRESHOLD) {
            return SignificantType.none;
        } else {

            if (!getInteractionSignificance()) {
                return SignificantType.both_equally;
            } else if (getGenderFemaleKoPValue() >= PHENOTYPE_THRESHOLD && getGenderMaleKoPValue() >= PHENOTYPE_THRESHOLD) {
                return SignificantType.cannot_classify;
            } else if (getGenderFemaleKoPValue() < PHENOTYPE_THRESHOLD && getGenderMaleKoPValue() >= PHENOTYPE_THRESHOLD) {
                return SignificantType.female_only;
            } else if (getGenderFemaleKoPValue() >= PHENOTYPE_THRESHOLD && getGenderMaleKoPValue() < PHENOTYPE_THRESHOLD) {
                return SignificantType.male_only;
            } else if ((getGenderFemaleKoEstimate() > 0 && getGenderMaleKoEstimate() > 0) || (getGenderFemaleKoEstimate() < 0 && getGenderMaleKoEstimate() < 0)) {

                if (Math.abs(getGenderFemaleKoEstimate()) > Math.abs(getGenderMaleKoEstimate())) {
                    return SignificantType.female_greater;
                } else {
                    return SignificantType.male_greater;
                }

            } else {
                return SignificantType.different_directions;
            }

        }

    }

}
