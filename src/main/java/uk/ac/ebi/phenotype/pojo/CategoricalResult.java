/*******************************************************************************
 * Copyright 2015 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 *******************************************************************************/
package uk.ac.ebi.phenotype.pojo;

import javax.persistence.*;

import java.io.Serializable;

/**
 * 
 * A representation of the outcome of a statistical test done using a control
 * set of mice and a mutant strain, including the calculated p-value.
 * 
 */
@Entity
@Table(name = "stats_categorical_results")
public class CategoricalResult extends StatisticalResult implements Serializable {

    private static final long serialVersionUID = -3086932382286798568L;

    @Enumerated(EnumType.STRING)
    @Column(name = "control_sex")
    private SexType controlSex;

    @Enumerated(EnumType.STRING)
    @Column(name = "experimental_sex")
    private SexType experimentalSex;

    @Enumerated(EnumType.STRING)
    @Column(name = "experimental_zygosity")
    private ZygosityType experimentalZygosity;

    @Column(name = "category_a", length = 200)
    private String categoryA;

    @Column(name = "category_b", length = 200)
    private String categoryB;

    @Column(name = "p_value")
    private double pValue;

    @Column(name = "effect_size")
    private Double categoricalEffectSize;

	private String mpTermId;
	
	private String mpTermName;

    public String getMpTermName() {
		return mpTermName;
	}

	public void setMpTermName(String mpTermName) {
		this.mpTermName = mpTermName;
	}

	public String getMpTermId() {
		return mpTermId;
	}

	/**
     * @return the controlSex
     */
    public SexType getControlSex() {
        return controlSex;
    }

    /**
     * @param sex
     *            the controlSex to set
     */
    public void setControlSex(SexType sex) {
        this.controlSex = sex;
    }

    /**
     * @return the experimentalSex
     */
    public SexType getExperimentalSex() {
        return experimentalSex;
    }

    /**
     * @param experimentalSex
     *            the experimentalSex to set
     */
    public void setExperimentalSex(SexType experimentalSex) {
        this.experimentalSex = experimentalSex;
    }

    /**
     * @return the experimentalZygosity
     */
    public ZygosityType getExperimentalZygosity() {
        return experimentalZygosity;
    }

    /**
     * @param zygosity
     *            the experimentalZygosity to set
     */
    public void setExperimentalZygosity(ZygosityType zygosity) {
        this.experimentalZygosity = zygosity;
    }

    /**
     * @return the categoryA
     */
    public String getCategoryA() {
        return categoryA;
    }

    /**
     * @param categoryA
     *            the categoryA to set
     */
    public void setCategoryA(String categoryA) {
        this.categoryA = categoryA;
    }

    /**
     * @return the categoryB
     */
    public String getCategoryB() {
        return categoryB;
    }

    /**
     * @param categoryB
     *            the categoryB to set
     */
    public void setCategoryB(String categoryB) {
        this.categoryB = categoryB;
    }

    /**
     * @return the pValue
     */
    public double getpValue() {
        return pValue;
    }

    /**
     * @param pValue2
     *            the pValue to set
     */
    public void setpValue(double pValue2) {
        this.pValue = pValue2;
    }

    /**
     * @return the effectSize
     */
    public Double getCategoricalEffectSize() {
        return categoricalEffectSize;
    }

    /**
     * @param effectSize
     *            the effectSize to set
     */
    public void setCategoricalEffectSize(double effectSize) {
        this.categoricalEffectSize = effectSize;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((categoryA == null) ? 0 : categoryA.hashCode());
        result = prime * result + ((categoryB == null) ? 0 : categoryB.hashCode());
        result = prime * result + ((controlBiologicalModel == null) ? 0 : controlBiologicalModel.hashCode());
        result = prime * result + ((controlSex == null) ? 0 : controlSex.hashCode());
        result = prime * result + ((experimentalBiologicalModel == null) ? 0 : experimentalBiologicalModel.hashCode());
        result = prime * result + ((experimentalSex == null) ? 0 : experimentalSex.hashCode());
        result = prime * result + ((experimentalZygosity == null) ? 0 : experimentalZygosity.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        long temp;
        temp = Double.doubleToLongBits(categoricalEffectSize);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + ((organisation == null) ? 0 : organisation.hashCode());
        temp = Double.doubleToLongBits(pValue);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + ((parameter == null) ? 0 : parameter.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        CategoricalResult other = (CategoricalResult) obj;
        if (categoryA == null) {
            if (other.categoryA != null) {
                return false;
            }
        } else if (!categoryA.equals(other.categoryA)) {
            return false;
        }
        if (categoryB == null) {
            if (other.categoryB != null) {
                return false;
            }
        } else if (!categoryB.equals(other.categoryB)) {
            return false;
        }
        if (controlBiologicalModel == null) {
            if (other.controlBiologicalModel != null) {
                return false;
            }
        } else if (!controlBiologicalModel.equals(other.controlBiologicalModel)) {
            return false;
        }
        if (controlSex != other.controlSex) {
            return false;
        }
        if (experimentalBiologicalModel == null) {
            if (other.experimentalBiologicalModel != null) {
                return false;
            }
        } else if (!experimentalBiologicalModel.equals(other.experimentalBiologicalModel)) {
            return false;
        }
        if (experimentalSex != other.experimentalSex) {
            return false;
        }
        if (experimentalZygosity != other.experimentalZygosity) {
            return false;
        }
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        if (Double.doubleToLongBits(categoricalEffectSize) != Double.doubleToLongBits(other.categoricalEffectSize)) {
            return false;
        }
        if (organisation == null) {
            if (other.organisation != null) {
                return false;
            }
        } else if (!organisation.equals(other.organisation)) {
            return false;
        }
        if (Double.doubleToLongBits(pValue) != Double.doubleToLongBits(other.pValue)) {
            return false;
        }
        if (parameter == null) {
            if (other.parameter != null) {
                return false;
            }
        } else if (!parameter.equals(other.parameter)) {
            return false;
        }
        return true;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "CategoricalResult [controlSex=" + controlSex + ", experimentalSex=" + experimentalSex + ", experimentalZygosity=" + experimentalZygosity + ", categoryA=" + categoryA + ", categoryB=" + categoryB + ", pValue=" + pValue + ", effectSize=" + categoricalEffectSize + "]";
    }

	public void setMpTermId(String mpTermId) {
		this.mpTermId=mpTermId;
		
	}

    
}
