/**
 * Copyright © 2011-2013 EMBL - European Bioinformatics Institute
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License.  
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.phenotype.web.pojo;

import java.util.Set;

import uk.ac.ebi.phenotype.pojo.Allele;
import uk.ac.ebi.phenotype.pojo.OntologyTerm;
import uk.ac.ebi.phenotype.pojo.ZygosityType;

/**
 * 
 * Represents a single row in a phenotype table
 * 
 */
public class PhenotypeRow {
	
	private OntologyTerm phenotypeTerm;
	private Allele allele;
	private Set<String> sexes;
	private ZygosityType zygosity;
	private String rawZygosity;
	private int projectId;
	private String procedureId;
	private String parameterId;
	private String dataSourceName;//to hold the name of the origin of the data e.g. Europhenome or WTSI Mouse Genetics Project
	private String linkToOriginalDataProvider;
	
	public String getLinkToOriginalDataProvider() {
		return linkToOriginalDataProvider;
	}
	public void setLinkToOriginalDataProvider(String linkToOriginalDataProvider) {
		this.linkToOriginalDataProvider = linkToOriginalDataProvider;
	}
	public String getDataSourceName() {
		return dataSourceName;
	}
	public void setDataSourceName(String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}
	public OntologyTerm getPhenotypeTerm() {
		return phenotypeTerm;
	}
	public void setPhenotypeTerm(OntologyTerm term) {
		this.phenotypeTerm = term;
	}
	public Allele getAllele() {
		return allele;
	}
	public void setAllele(Allele allele) {
		this.allele = allele;
	}
	public Set<String> getSexes() {
		return sexes;
	}
	public void setSexes(Set<String> sexes) {
		this.sexes = sexes;
	}
	public ZygosityType getZygosity() {
		return zygosity;
	}
	public void setZygosity(ZygosityType zygosityType) {
		this.zygosity = zygosityType;
	}
		
	/**
	 * @return the rawZygosity
	 */
	public String getRawZygosity() {
		return rawZygosity;
	}
	
	/**
	 * @param rawZygosity the rawZygosity to set
	 */
	public void setRawZygosity(String rawZygosity) {
		this.rawZygosity = rawZygosity;
	}
	
	/**
	 * @return the projectId
	 */
	public int getProjectId() {
		return projectId;
	}
	/**
	 * @param projectId the projectId to set
	 */
	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}
	/**
	 * @return the procedureId
	 */
	public String getProcedureId() {
		return procedureId;
	}
	/**
	 * @param procedureId the procedureId to set
	 */
	public void setProcedureId(String procedureId) {
		this.procedureId = procedureId;
	}
	/**
	 * @return the parameterId
	 */
	public String getParameterId() {
		return parameterId;
	}
	/**
	 * @param parameterId the parameterId to set
	 */
	public void setParameterId(String parameterId) {
		this.parameterId = parameterId;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((allele == null) ? 0 : allele.hashCode());
		result = prime * result + ((phenotypeTerm == null) ? 0 : phenotypeTerm.hashCode());
		result = prime * result
				+ ((zygosity == null) ? 0 : zygosity.hashCode());
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
		PhenotypeRow other = (PhenotypeRow) obj;
		if (allele == null) {
			if (other.allele != null)
				return false;
		} else if (!allele.equals(other.allele))
			return false;
		if (phenotypeTerm == null) {
			if (other.phenotypeTerm != null)
				return false;
		} else if (!phenotypeTerm.equals(other.phenotypeTerm))
			return false;
		if (zygosity == null) {
			if (other.zygosity != null)
				return false;
		} else if (!zygosity.equals(other.zygosity))
			return false;
		return true;
	}

}
