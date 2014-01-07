package uk.ac.ebi.phenotype.stats;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import uk.ac.ebi.phenotype.dao.PhenotypePipelineDAO;
import uk.ac.ebi.phenotype.pojo.ObservationType;
import uk.ac.ebi.phenotype.pojo.SexType;
import uk.ac.ebi.phenotype.pojo.StatisticalResult;
import uk.ac.ebi.phenotype.pojo.ZygosityType;

public class ExperimentDTO {
	
	private String experimentId;
	private String metadataGroup;
	private List<String> metadata;
	private String parameterStableId;
	private ObservationType observationType;
	private String organisation;
	private String strain;
	private String geneMarker;
	private Set<ZygosityType> zygosities;
	private Set<SexType> sexes;
	private List<? extends StatisticalResult> results;
	
	private Set<ObservationDTO> homozygoteMutants;
	private Set<ObservationDTO> heterozygoteMutants;
	private HashSet<ObservationDTO> hemizygoteMutants;

	private Set<ObservationDTO> controls;
	private Integer controlBiologicalModelId;
	private Integer experimentalBiologicalModelId;
	private Set<ObservationDTO> maleControls;
	private Set<ObservationDTO> femaleControls;
	
		
	/**
	 * 
	 * @return list of 
	 * @throws SQLException 
	 */
	public List<String> getTabbedToString(PhenotypePipelineDAO ppDAO) throws SQLException{
		List<String> rows = new ArrayList<String> ();
		for (ObservationDTO obs : homozygoteMutants){
			if (rows.size() == 0)
				rows.add(obs.getTabbedFields());
			rows.add(obs.tabbedToString(ppDAO));
		}
		for (ObservationDTO obs : heterozygoteMutants){
			if (rows.size() == 0)
				rows.add(obs.getTabbedFields());
			rows.add(obs.tabbedToString(ppDAO));
		}
		for (ObservationDTO obs : controls){
			if (rows.size() == 0)
				rows.add(obs.getTabbedFields());
			rows.add(obs.tabbedToString(ppDAO));
		}
		
		return rows;
	}
	
	public ObservationType getObservationType() {
		return observationType;
	}

	public void setObservationType(ObservationType observationType) {
		this.observationType = observationType;
	}
	
	public Integer getControlBiologicalModelId() {
		return controlBiologicalModelId;
	}

	public void setControlBiologicalModelId(Integer controlBiologicalModelId) {
		this.controlBiologicalModelId = controlBiologicalModelId;
	}

	public Integer getExperimentalBiologicalModelId() {
		return experimentalBiologicalModelId;
	}

	public void setExperimentalBiologicalModelId(
			Integer experimentalBiologicalModelId) {
		this.experimentalBiologicalModelId = experimentalBiologicalModelId;
	}


	@Override
	public String toString() {
		return "ExperimentDTO [experimentId=" + experimentId
				+ ", metadataGroup=" + metadataGroup
				+ ", parameterStableId=" + parameterStableId
				+ ", organisation=" + organisation + ", strain=" + strain
				+ ", geneMarker=" + geneMarker + ", zygosities=" + zygosities
				+ ", sexes=" + sexes + ", result=" + results + ", Num homozygous mutants="
				+ homozygoteMutants.size() + ", Num heterozygous mutants="
				+ heterozygoteMutants.size() + ", Numcontrols=" + controls.size() +" control bm id="+this.controlBiologicalModelId+"  exp bm id="+experimentalBiologicalModelId+ "]";
	}


	public Set<String> getCategories() {
		Set<String> categorieSet=new TreeSet<String>();
		for(ObservationDTO ob: controls) {
			categorieSet.add(ob.getCategory());
		}
		for(ObservationDTO ob: homozygoteMutants) {
			categorieSet.add(ob.getCategory());
		}
		for(ObservationDTO ob: heterozygoteMutants) {
			categorieSet.add(ob.getCategory());
		}
		return categorieSet;	
	}
	
	public int getControlSampleSizeFemale() {
		return this.getFemaleControls().size();
	}
	public int getControlSampleSizeMale() {
		return this.getMaleControls().size();
	}
	
	public Set<ObservationDTO> getFemaleControls(){
		return this.getControls(SexType.female);
	}
	
	public Set<ObservationDTO> getMaleControls(){
		return this.getControls(SexType.male);
	}

	private Set<ObservationDTO> getControls(SexType sex) {

		if (femaleControls == null || maleControls == null) {
			femaleControls = new HashSet<ObservationDTO>();
			maleControls = new HashSet<ObservationDTO>();
			for (ObservationDTO control : this.getControls()) {

				if (SexType.valueOf(control.getSex()).equals(SexType.female)) {
					femaleControls.add(control);
				} else {
					maleControls.add(control);
				}
			}
		}
		if (sex.equals(SexType.female)) {
			return femaleControls;
		} else {
			return maleControls;
		}

	}

	public Set<ObservationDTO> getMutants(SexType sex, ZygosityType zyg) {
		Set<ObservationDTO> mutantsDtos = new HashSet<ObservationDTO>();
		Set<ObservationDTO> mutantDtosForSex = new HashSet<ObservationDTO>();
		if (zyg == null || zyg.equals(ZygosityType.homozygote)) {
			mutantsDtos = this.getHomozygoteMutants();
		} 
		if (zyg == null || zyg.equals(ZygosityType.heterozygote)){
			mutantsDtos = this.getHeterozygoteMutants();
		}
		if (zyg == null || zyg.equals(ZygosityType.hemizygote)){
			mutantsDtos = this.getHemizygoteMutants();
		}
		

		for (ObservationDTO mutant : mutantsDtos) {
			if (sex == null || sex.equals(SexType.valueOf(mutant.getSex()))) {
				mutantDtosForSex.add(mutant);
			} 
		}
		return mutantDtosForSex;
	}

	
	
	// *************************** Generated *************************** //

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
	public Set<ZygosityType> getZygosities() {
		return zygosities;
	}

	/**
	 * @param zygosity the zygosity to set
	 */
	public void setZygosities(Set<ZygosityType> zygosities) {
		this.zygosities = zygosities;
	}

	/**
	 * @return the sexes
	 */
	public Set<SexType> getSexes() {
		return sexes;
	}

	/**
	 * @param sexes the sexes to set
	 */
	public void setSexes(Set<SexType> sexes) {
		this.sexes = sexes;
	}

	/**
	 * @return the result
	 */
	public List<? extends StatisticalResult> getResults() {
		return results;
	}

	/**
	 * @param result the result to set
	 */
	public void setResults(List<? extends StatisticalResult> results) {
		this.results = results;
	}

	/**
	 * @return the homozygoteMutants
	 */
	public Set<ObservationDTO> getHomozygoteMutants() {
		return homozygoteMutants;
	}
	
	public Set<ObservationDTO> getHemizygoteMutants() {
		return hemizygoteMutants;
	}

	/**
	 * @param homozygoteMutants the homozygoteMutants to set
	 */
	public void setHomozygoteMutants(Set<ObservationDTO> homozygoteMutants) {
		this.homozygoteMutants = homozygoteMutants;
	}

	/**
	 * @return the heterozygoteMutants
	 */
	public Set<ObservationDTO> getHeterozygoteMutants() {
		return heterozygoteMutants;
	}

	/**
	 * @param heterozygoteMutants the heterozygoteMutants to set
	 */
	public void setHeterozygoteMutants(Set<ObservationDTO> heterozygoteMutants) {
		this.heterozygoteMutants = heterozygoteMutants;
	}
	
	/**
	 * @param hemizygote Mutants the hemizygoteMutants to set
	 */
	public void setHemizygoteMutants(HashSet<ObservationDTO> hemizygote) {
		this.hemizygoteMutants = hemizygote;
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

	/**
	 * @return the metadataGroup
	 */
	public String getMetadataGroup() {
		return metadataGroup;
	}

	/**
	 * @param metadataGroup the metadataGroup to set
	 */
	public void setMetadataGroup(String metadataGroup) {
		this.metadataGroup = metadataGroup;
	}

	/**
	 * @return the metadata
	 */
	public List<String> getMetadata() {
		return metadata;
	}

	/**
	 * @param metadata the metadata to set
	 */
	public void setMetadata(List<String> metadata) {
		this.metadata = metadata;
	}

	/**
	 * @param maleControls the maleControls to set
	 */
	public void setMaleControls(Set<ObservationDTO> maleControls) {
		this.maleControls = maleControls;
	}

	/**
	 * @param femaleControls the femaleControls to set
	 */
	public void setFemaleControls(Set<ObservationDTO> femaleControls) {
		this.femaleControls = femaleControls;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((controlBiologicalModelId == null) ? 0
						: controlBiologicalModelId.hashCode());
		result = prime * result
				+ ((controls == null) ? 0 : controls.hashCode());
		result = prime * result
				+ ((experimentId == null) ? 0 : experimentId.hashCode());
		result = prime
				* result
				+ ((experimentalBiologicalModelId == null) ? 0
						: experimentalBiologicalModelId.hashCode());
		result = prime * result
				+ ((femaleControls == null) ? 0 : femaleControls.hashCode());
		result = prime * result
				+ ((geneMarker == null) ? 0 : geneMarker.hashCode());
		result = prime
				* result
				+ ((hemizygoteMutants == null) ? 0 : hemizygoteMutants
						.hashCode());
		result = prime
				* result
				+ ((heterozygoteMutants == null) ? 0 : heterozygoteMutants
						.hashCode());
		result = prime
				* result
				+ ((homozygoteMutants == null) ? 0 : homozygoteMutants
						.hashCode());
		result = prime * result
				+ ((maleControls == null) ? 0 : maleControls.hashCode());
		result = prime * result
				+ ((metadata == null) ? 0 : metadata.hashCode());
		result = prime * result
				+ ((metadataGroup == null) ? 0 : metadataGroup.hashCode());
		result = prime * result
				+ ((observationType == null) ? 0 : observationType.hashCode());
		result = prime * result
				+ ((organisation == null) ? 0 : organisation.hashCode());
		result = prime
				* result
				+ ((parameterStableId == null) ? 0 : parameterStableId
						.hashCode());
		result = prime * result + ((results == null) ? 0 : results.hashCode());
		result = prime * result + ((sexes == null) ? 0 : sexes.hashCode());
		result = prime * result + ((strain == null) ? 0 : strain.hashCode());
		result = prime * result
				+ ((zygosities == null) ? 0 : zygosities.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
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
		if (controlBiologicalModelId == null) {
			if (other.controlBiologicalModelId != null) {
				return false;
			}
		} else if (!controlBiologicalModelId
				.equals(other.controlBiologicalModelId)) {
			return false;
		}
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
		if (experimentalBiologicalModelId == null) {
			if (other.experimentalBiologicalModelId != null) {
				return false;
			}
		} else if (!experimentalBiologicalModelId
				.equals(other.experimentalBiologicalModelId)) {
			return false;
		}
		if (femaleControls == null) {
			if (other.femaleControls != null) {
				return false;
			}
		} else if (!femaleControls.equals(other.femaleControls)) {
			return false;
		}
		if (geneMarker == null) {
			if (other.geneMarker != null) {
				return false;
			}
		} else if (!geneMarker.equals(other.geneMarker)) {
			return false;
		}
		if (hemizygoteMutants == null) {
			if (other.hemizygoteMutants != null) {
				return false;
			}
		} else if (!hemizygoteMutants.equals(other.hemizygoteMutants)) {
			return false;
		}
		if (heterozygoteMutants == null) {
			if (other.heterozygoteMutants != null) {
				return false;
			}
		} else if (!heterozygoteMutants.equals(other.heterozygoteMutants)) {
			return false;
		}
		if (homozygoteMutants == null) {
			if (other.homozygoteMutants != null) {
				return false;
			}
		} else if (!homozygoteMutants.equals(other.homozygoteMutants)) {
			return false;
		}
		if (maleControls == null) {
			if (other.maleControls != null) {
				return false;
			}
		} else if (!maleControls.equals(other.maleControls)) {
			return false;
		}
		if (metadata == null) {
			if (other.metadata != null) {
				return false;
			}
		} else if (!metadata.equals(other.metadata)) {
			return false;
		}
		if (metadataGroup == null) {
			if (other.metadataGroup != null) {
				return false;
			}
		} else if (!metadataGroup.equals(other.metadataGroup)) {
			return false;
		}
		if (observationType != other.observationType) {
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
		if (results == null) {
			if (other.results != null) {
				return false;
			}
		} else if (!results.equals(other.results)) {
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
		if (zygosities == null) {
			if (other.zygosities != null) {
				return false;
			}
		} else if (!zygosities.equals(other.zygosities)) {
			return false;
		}
		return true;
	}

	
	

}
