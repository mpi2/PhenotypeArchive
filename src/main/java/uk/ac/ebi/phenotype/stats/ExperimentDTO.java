package uk.ac.ebi.phenotype.stats;

import java.util.Set;

import uk.ac.ebi.phenotype.pojo.SexType;
import uk.ac.ebi.phenotype.pojo.StatisticalResult;
import uk.ac.ebi.phenotype.pojo.ZygosityType;

public class ExperimentDTO {
	
	private String experimentId;
	private String parameterStableId;
	private String organisation;
	private String strain;
	private String geneMarker;
	private ZygosityType zygosity;
	private Set<SexType> sexes;
	private StatisticalResult result;
	
	private Set<ObservationDTO> mutants;
	private Set<ObservationDTO> controls;
	/**
	 * @return the experimentId
	 */
	public String getExperimentId() {
		return experimentId;
	}
	/**
	 * @param experimentId the experimentId to set
	 */
	public void setExperimentId(String experimentId) {
		this.experimentId = experimentId;
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
	 * @return the geneMarker
	 */
	public String getGeneMarker() {
		return geneMarker;
	}
	/**
	 * @param geneMarker the geneMarker to set
	 */
	public void setGeneMarker(String geneMarker) {
		this.geneMarker = geneMarker;
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
	 * @return the sexes
	 */
	public Set<SexType> getSexes() {
		return sexes;
	}
	/**
	 * @param treeSet the sexes to set
	 */
	public void setSexes(Set<SexType> treeSet) {
		this.sexes = treeSet;
	}
	/**
	 * @return the result
	 */
	public StatisticalResult getResult() {
		return result;
	}
	/**
	 * @param result the result to set
	 */
	public void setResult(StatisticalResult result) {
		this.result = result;
	}
	/**
	 * @return the mutants
	 */
	public Set<ObservationDTO> getMutants() {
		return mutants;
	}
	/**
	 * @param mutants the mutants to set
	 */
	public void setMutants(Set<ObservationDTO> mutants) {
		this.mutants = mutants;
	}
	/**
	 * @return the controls
	 */
	public Set<ObservationDTO> getControls() {
		return controls;
	}
	/**
	 * @param controls the controls to set
	 */
	public void setControls(Set<ObservationDTO> controls) {
		this.controls = controls;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((controls == null) ? 0 : controls.hashCode());
		result = prime * result
				+ ((experimentId == null) ? 0 : experimentId.hashCode());
		result = prime * result
				+ ((geneMarker == null) ? 0 : geneMarker.hashCode());
		result = prime * result + ((mutants == null) ? 0 : mutants.hashCode());
		result = prime * result
				+ ((organisation == null) ? 0 : organisation.hashCode());
		result = prime
				* result
				+ ((parameterStableId == null) ? 0 : parameterStableId
						.hashCode());
		result = prime * result
				+ ((this.result == null) ? 0 : this.result.hashCode());
		result = prime * result + ((sexes == null) ? 0 : sexes.hashCode());
		result = prime * result + ((strain == null) ? 0 : strain.hashCode());
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
		ExperimentDTO other = (ExperimentDTO) obj;
		if (controls == null) {
			if (other.controls != null) {
				return false;
			}
		} else if (!controls.equals(other.controls)) {
			return false;
		}
		if (experimentId == null) {
			if (other.experimentId != null) {
				return false;
			}
		} else if (!experimentId.equals(other.experimentId)) {
			return false;
		}
		if (geneMarker == null) {
			if (other.geneMarker != null) {
				return false;
			}
		} else if (!geneMarker.equals(other.geneMarker)) {
			return false;
		}
		if (mutants == null) {
			if (other.mutants != null) {
				return false;
			}
		} else if (!mutants.equals(other.mutants)) {
			return false;
		}
		if (organisation == null) {
			if (other.organisation != null) {
				return false;
			}
		} else if (!organisation.equals(other.organisation)) {
			return false;
		}
		if (parameterStableId == null) {
			if (other.parameterStableId != null) {
				return false;
			}
		} else if (!parameterStableId.equals(other.parameterStableId)) {
			return false;
		}
		if (result == null) {
			if (other.result != null) {
				return false;
			}
		} else if (!result.equals(other.result)) {
			return false;
		}
		if (sexes == null) {
			if (other.sexes != null) {
				return false;
			}
		} else if (!sexes.equals(other.sexes)) {
			return false;
		}
		if (strain == null) {
			if (other.strain != null) {
				return false;
			}
		} else if (!strain.equals(other.strain)) {
			return false;
		}
		if (zygosity != other.zygosity) {
			return false;
		}
		return true;
	}


	@Override
	public String toString() {
		return "ExperimentDTO [experimentId=" + experimentId
				+ ", parameterStableId=" + parameterStableId
				+ ", organisation=" + organisation + ", strain=" + strain
				+ ", geneMarker=" + geneMarker + ", zygosity=" + zygosity
				+ ", sexes=" + sexes + ", result=" + result + ", Nummutants="
				+ mutants.size() + ", Numcontrols=" + controls.size() + "]";
	}

	
	
}