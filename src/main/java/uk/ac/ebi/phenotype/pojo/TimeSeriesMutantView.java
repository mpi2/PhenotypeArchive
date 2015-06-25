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
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;



/**
 * 
 * A representation of a mutant mouse for a categorical value.
 * 
 */
@Entity
@Table(name = "stats_mv_experimental_time_series_values")
public class TimeSeriesMutantView implements Serializable {

	private static final long serialVersionUID = 4505038269190999331L;

	@Id
	@OneToOne
	@JoinColumn(name = "biological_model_id")
	private BiologicalModel biologicalModel;

	@OneToOne
	@JoinColumn(name = "biological_sample_id")
	private BiologicalSample biologicalSample;

	@OneToOne
	@JoinColumn(name = "organisation_id")
	private Organisation organisation;

	@OneToOne
	@JoinColumn(name = "parameter_id")
	private Parameter parameter;

	@Column(name = "colony_id")
	private String colony;
	
	@Column(name = "population_id")
	private Integer populationId;

	@Enumerated(EnumType.STRING)
	@Column(name = "sex")
	private SexType sex;

	@Enumerated(EnumType.STRING)
	@Column(name = "zygosity")
	private ZygosityType zygosity;
	
	@Column(name = "category")
	private String category;
	
	@Column(name = "data_point")
	private Float dataPoint;

	public Float getDataPoint() {
		return dataPoint;
	}
	
	@Column(name = "discrete_point")
	private Date timePoint;

	public Date getTimePoint() {
		return timePoint;
	}

	public void setTimePoint(Date timePoint) {
		this.timePoint = timePoint;
	}

	public void setDataPoint(Float dataPoint) {
		this.dataPoint = dataPoint;
	}

	/**
	 * @return the biologicalModel
	 */
	public BiologicalModel getBiologicalModel() {
		return biologicalModel;
	}

	/**
	 * @param biologicalModel the biologicalModel to set
	 */
	public void setBiologicalModel(BiologicalModel biologicalModel) {
		this.biologicalModel = biologicalModel;
	}

	/**
	 * @return the biologicalSample
	 */
	public BiologicalSample getBiologicalSample() {
		return biologicalSample;
	}

	/**
	 * @param biologicalSample the biologicalSample to set
	 */
	public void setBiologicalSample(BiologicalSample biologicalSample) {
		this.biologicalSample = biologicalSample;
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
	 * @param parameter the parameter to set
	 */
	public void setParameter(Parameter parameter) {
		this.parameter = parameter;
	}

	/**
	 * @return the colony
	 */
	public String getColony() {
		return colony;
	}

	/**
	 * @param colony the colony to set
	 */
	public void setColony(String colony) {
		this.colony = colony;
	}

	/**
	 * @return the populationId
	 */
	public Integer getPopulationId() {
		return populationId;
	}

	/**
	 * @param populationId the populationId to set
	 */
	public void setPopulationId(Integer populationId) {
		this.populationId = populationId;
	}

	/**
	 * @return the sex
	 */
	public SexType getSex() {
		return sex;
	}

	/**
	 * @param sex the sex to set
	 */
	public void setSex(SexType sex) {
		this.sex = sex;
	}

	/**
	 * @return the zygosity
	 */
	public ZygosityType getZygosity() {
		return zygosity;
	}

	/**
	 * @param zygosity the zygosity to set
	 */
	public void setZygosity(ZygosityType zygosity) {
		this.zygosity = zygosity;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((biologicalModel == null) ? 0 : biologicalModel.hashCode());
		result = prime
				* result
				+ ((biologicalSample == null) ? 0 : biologicalSample.hashCode());
		result = prime * result
				+ ((category == null) ? 0 : category.hashCode());
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
				+ ((timePoint == null) ? 0 : timePoint.hashCode());
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
		TimeSeriesMutantView other = (TimeSeriesMutantView) obj;
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
		if (category == null) {
			if (other.category != null) {
				return false;
			}
		} else if (!category.equals(other.category)) {
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
		if (timePoint == null) {
			if (other.timePoint != null) {
				return false;
			}
		} else if (!timePoint.equals(other.timePoint)) {
			return false;
		}
		if (zygosity != other.zygosity) {
			return false;
		}
		return true;
	}

	

}

