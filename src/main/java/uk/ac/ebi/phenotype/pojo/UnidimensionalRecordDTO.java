package uk.ac.ebi.phenotype.pojo;



public class UnidimensionalRecordDTO {

	private Boolean mutant = Boolean.FALSE;
	private Integer controlModelId;
	private Integer mutantModelId;
	private ZygosityType mutantZygosity;
	private String colony;
	private Parameter parameter;
	private Organisation organisation;
	private String genotype;
	private String zygosity;
	private String gender;
	private String value;
	private String experimentDate;
	
	public String getExperimentDate() {
		return experimentDate;
	}
	public void setExperimentDate(String experimentDate) {
		this.experimentDate = experimentDate;
	}
	
	
	public Boolean isMutant() {
		return mutant;
	}
	public void setIsMutant(Boolean mutant) {
		this.mutant = mutant;
	}
	public Integer getControlModelId() {
		return controlModelId;
	}
	public void setControlModelId(Integer controlModelId) {
		this.controlModelId = controlModelId;
	}
	public Integer getMutantModelId() {
		return mutantModelId;
	}
	public void setMutantModelId(Integer mutantModelId) {
		this.mutantModelId = mutantModelId;
	}
	public ZygosityType getMutantZygosity() {
		return mutantZygosity;
	}
	public void setMutantZygosity(ZygosityType mutantZygosity) {
		this.mutantZygosity = mutantZygosity;
	}
	public String getColony() {
		return colony;
	}
	public void setColony(String colony) {
		this.colony = colony;
	}
	public Parameter getParameter() {
		return parameter;
	}
	public void setParameter(Parameter parameter) {
		this.parameter = parameter;
	}
	public Organisation getOrganisation() {
		return organisation;
	}
	public void setOrganisation(Organisation organisation) {
		this.organisation = organisation;
	}
	public String getGenotype() {
		return genotype;
	}
	public void setGenotype(String genotype) {
		this.genotype = genotype;
	}
	public String getZygosity() {
		return zygosity;
	}
	public void setZygosity(String zygosity) {
		this.zygosity = zygosity;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((colony == null) ? 0 : colony.hashCode());
		result = prime
				* result
				+ ((controlModelId == null) ? 0 : controlModelId.hashCode());
		result = prime * result + ((gender == null) ? 0 : gender.hashCode());
		result = prime * result
				+ ((genotype == null) ? 0 : genotype.hashCode());
		result = prime * result
				+ ((mutant == null) ? 0 : mutant.hashCode());
		result = prime * result
				+ ((mutantZygosity == null) ? 0 : mutantZygosity.hashCode());
		result = prime * result
				+ ((mutantModelId == null) ? 0 : mutantModelId.hashCode());
		result = prime * result
				+ ((organisation == null) ? 0 : organisation.hashCode());
		result = prime * result
				+ ((parameter == null) ? 0 : parameter.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		UnidimensionalRecordDTO other = (UnidimensionalRecordDTO) obj;
		if (colony == null) {
			if (other.colony != null) {
				return false;
			}
		} else if (!colony.equals(other.colony)) {
			return false;
		}
		if (controlModelId == null) {
			if (other.controlModelId != null) {
				return false;
			}
		} else if (!controlModelId.equals(other.controlModelId)) {
			return false;
		}
		if (gender == null) {
			if (other.gender != null) {
				return false;
			}
		} else if (!gender.equals(other.gender)) {
			return false;
		}
		if (genotype == null) {
			if (other.genotype != null) {
				return false;
			}
		} else if (!genotype.equals(other.genotype)) {
			return false;
		}
		if (mutant == null) {
			if (other.mutant != null) {
				return false;
			}
		} else if (!mutant.equals(other.mutant)) {
			return false;
		}
		if (mutantZygosity != other.mutantZygosity) {
			return false;
		}
		if (mutantModelId == null) {
			if (other.mutantModelId != null) {
				return false;
			}
		} else if (!mutantModelId.equals(other.mutantModelId)) {
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
		if (value == null) {
			if (other.value != null) {
				return false;
			}
		} else if (!value.equals(other.value)) {
			return false;
		}
		if (zygosity == null) {
			if (other.zygosity != null) {
				return false;
			}
		} else if (!zygosity.equals(other.zygosity)) {
			return false;
		}
		return true;
	}

	
}
