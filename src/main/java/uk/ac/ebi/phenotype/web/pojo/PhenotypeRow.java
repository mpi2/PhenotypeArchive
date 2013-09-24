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

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.ebi.phenotype.pojo.Allele;
import uk.ac.ebi.phenotype.pojo.GenomicFeature;
import uk.ac.ebi.phenotype.pojo.OntologyTerm;
import uk.ac.ebi.phenotype.pojo.Parameter;
import uk.ac.ebi.phenotype.pojo.Procedure;
import uk.ac.ebi.phenotype.pojo.ZygosityType;

/**
 * 
 * Represents a single row in a phenotype table
 * 
 */
public class PhenotypeRow implements Comparable<PhenotypeRow>{
	
	private final Logger log = LoggerFactory.getLogger(PhenotypeRow.class);

	private OntologyTerm phenotypeTerm;
	private GenomicFeature gene;
	private Allele allele;
	private List<String> sexes;
	private ZygosityType zygosity;
	private String rawZygosity;
	private int projectId;
	private Procedure procedure;
	private Parameter parameter;
	private String dataSourceName;//to hold the name of the origin of the data e.g. Europhenome or WTSI Mouse Genetics Project
	 
	/**
	 * Returns a PhenotypeRow object with the original data provider link
	 * populated.
	 * 
	 * @param pr The original phenotype row object
	 * @return
	 */
	public String getPhenotypeLink() {

		String linkUrl="";

		if(getDataSourceName().equals("EuroPhenome")){
			String sex="";
			if(getSexes().size()==2){
				sex="Both-Split";
			}else{
				Iterator<String> iter = getSexes().iterator();
				String first = (String) iter.next();
				sex=WordUtils.capitalize(first);
			}
			try {
				linkUrl="http://www.europhenome.org/databrowser/viewer.jsp?set=true&m=true&l="+getProjectId()+"&zygosity="+getRawZygosity()+"&x="+sex+"&p="+getProcedure().getStableId()+"&pid_"+getParameter().getStableId()+"=on&compareLines=View+Data";
			} catch (NullPointerException e) {
				log.error("Project: "+getProjectId());
				log.error("Zygosity: "+getRawZygosity());
				log.error("Procedure: "+getProcedure());
				log.error("Parameter: "+getParameter());
				linkUrl = "";
			}
		}

		if(getDataSourceName().equals("WTSI Mouse Genetics Project")){
			
			if(getAllele()!=null  && getAllele().getGene()!=null){
				linkUrl="http://www.sanger.ac.uk/mouseportal/search?query="+getAllele().getGene().getSymbol();
			}

		}

		return linkUrl;
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
	public List<String> getSexes() {
		return sexes;
	}
	public void setSexes(List<String> sex) {
		this.sexes = sex;
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


	public GenomicFeature getGene() {
		return gene;
	}
	public void setGene(GenomicFeature gene) {
		this.gene = gene;
	}
	public Procedure getProcedure() {
		return procedure;
	}
	public void setProcedure(Procedure procedure) {
		this.procedure = procedure;
	}
	public Parameter getParameter() {
		return parameter;
	}
	public void setParameter(Parameter parameter) {
		this.parameter = parameter;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((allele == null) ? 0 : allele.hashCode());
		result = prime * result + ((dataSourceName == null) ? 0 : dataSourceName.hashCode());
		result = prime * result + ((phenotypeTerm == null) ? 0 : phenotypeTerm.hashCode());
		result = prime * result + ((zygosity == null) ? 0 : zygosity.hashCode());
		if (gene != null) {
			result = prime * result + ((parameter == null) ? 0 : parameter.hashCode());
			result = prime * result + ((procedure == null) ? 0 : procedure.hashCode());
		}
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
		PhenotypeRow other = (PhenotypeRow) obj;
		if (allele == null) {
			if (other.allele != null) {
				return false;
			}
		} else if (!allele.equals(other.allele)) {
			return false;
		}
		if (dataSourceName == null) {
			if (other.dataSourceName != null) {
				return false;
			}
		} else if (!dataSourceName.equals(other.dataSourceName)) {
			return false;
		}
		if (gene != null) {
			if (parameter == null) {
				if (other.parameter != null) {
					return false;
				}
			} else if (!parameter.equals(other.parameter)) {
				return false;
			}
			if (procedure == null) {
				if (other.procedure != null) {
					return false;
				}
			} else if (!procedure.equals(other.procedure)) {
				return false;
			}	
		}
		if (phenotypeTerm == null) {
			if (other.phenotypeTerm != null) {
				return false;
			}
		} else if (!phenotypeTerm.equals(other.phenotypeTerm)) {
			return false;
		}
		if (zygosity != other.zygosity) {
			return false;
		}
		return true;
	}


	@Override
	public String toString() {
		return "PhenotypeRow [phenotypeTerm=" + phenotypeTerm
				+ ", gene=" + gene + ", allele=" + allele + ", sexes=" + sexes
				+ ", zygosity=" + zygosity + ", rawZygosity=" + rawZygosity
				+ ", projectId=" + projectId + ", procedure=" + procedure
				+ ", parameter=" + parameter + ", dataSourceName="
				+ dataSourceName + "]";
	}

	@Override
	public int compareTo(PhenotypeRow o) {
		if(o.allele==null || this.allele==null){
			return -1;
		}
		return this.allele.getSymbol().compareTo(o.allele.getSymbol());
		
	}

	

}
