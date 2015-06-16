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

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;



/**
 * 
 * A representation of a  mouse for a unidimensional value.
 * 
 */
@MappedSuperclass
public abstract class UnidimensionalView implements Serializable {

	private static final long serialVersionUID = -6296688722173548764L;

	abstract public String whatAmI();

	public boolean isMutant(){ 
		return whatAmI().equals("mutant");
	}
	
	@Id
	@OneToOne
	@JoinColumn(name = "biological_model_id")
	protected BiologicalModel biologicalModel;

	@OneToOne
	@JoinColumn(name = "biological_sample_id")
	protected BiologicalSample biologicalSample;

	@OneToOne
	@JoinColumn(name = "organisation_id")
	protected Organisation organisation;

	@OneToOne
	@JoinColumn(name = "parameter_id")
	protected Parameter parameter;

	@Enumerated(EnumType.STRING)
	@Column(name = "sex")
	protected SexType sex;

	@Enumerated(EnumType.STRING)
	@Column(name = "zygosity")
	protected ZygosityType zygosity;
	
	@Column(name = "colony_id")
	protected String colony;
	
	@Column(name = "population_id")
	protected Integer populationId;

	@Column(name = "data_point")
	protected Float dataPoint;

	public BiologicalModel getBiologicalModel() {
		return biologicalModel;
	}

	public void setBiologicalModel(BiologicalModel biologicalModel) {
		this.biologicalModel = biologicalModel;
	}

	public BiologicalSample getBiologicalSample() {
		return biologicalSample;
	}

	public void setBiologicalSample(BiologicalSample biologicalSample) {
		this.biologicalSample = biologicalSample;
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

	public SexType getSex() {
		return sex;
	}

	public void setSex(SexType sex) {
		this.sex = sex;
	}

	public ZygosityType getZygosity() {
		return zygosity;
	}

	public void setZygosity(ZygosityType zygosity) {
		this.zygosity = zygosity;
	}

	public String getColony() {
		return colony;
	}

	public void setColony(String colony) {
		this.colony = colony;
	}

	public Integer getPopulationId() {
		return populationId;
	}

	public void setPopulationId(Integer populationId) {
		this.populationId = populationId;
	}

	public Float getDataPoint() {
		return dataPoint;
	}

	public void setDataPoint(Float dataPoint) {
		this.dataPoint = dataPoint;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((biologicalModel == null) ? 0 : biologicalModel.hashCode());
		result = prime
				* result
				+ ((biologicalSample == null) ? 0 : biologicalSample.hashCode());
		result = prime * result + ((colony == null) ? 0 : colony.hashCode());
		result = prime * result
				+ ((dataPoint == null) ? 0 : dataPoint.hashCode());
		result = prime * result
				+ ((organisation == null) ? 0 : organisation.hashCode());
		result = prime * result
				+ ((parameter == null) ? 0 : parameter.hashCode());
		result = prime * result
				+ ((populationId == null) ? 0 : populationId.hashCode());
		result = prime * result + ((sex == null) ? 0 : sex.hashCode());
		result = prime * result
				+ ((zygosity == null) ? 0 : zygosity.hashCode());
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
		UnidimensionalView other = (UnidimensionalView) obj;
		if (biologicalModel == null) {
			if (other.biologicalModel != null) {
				return false;
			}
		} else if (!biologicalModel.equals(other.biologicalModel)) {
			return false;
		}
		if (biologicalSample == null) {
			if (other.biologicalSample != null) {
				return false;
			}
		} else if (!biologicalSample.equals(other.biologicalSample)) {
			return false;
		}
		if (colony == null) {
			if (other.colony != null) {
				return false;
			}
		} else if (!colony.equals(other.colony)) {
			return false;
		}
		if (dataPoint == null) {
			if (other.dataPoint != null) {
				return false;
			}
		} else if (!dataPoint.equals(other.dataPoint)) {
			return false;
		}
		if (organisation == null) {
			if (other.organisation != null) {
				return false;
			}
		} else if (!organisation.equals(other.organisation)) {
			return false;
		}
		if (parameter == null) {
			if (other.parameter != null) {
				return false;
			}
		} else if (!parameter.equals(other.parameter)) {
			return false;
		}
		if (populationId == null) {
			if (other.populationId != null) {
				return false;
			}
		} else if (!populationId.equals(other.populationId)) {
			return false;
		}
		if (sex != other.sex) {
			return false;
		}
		if (zygosity != other.zygosity) {
			return false;
		}
		return true;
	}


}

