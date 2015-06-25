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

package uk.ac.ebi.phenotype.service.dto;

import java.util.List;
import java.util.Objects;

import org.apache.solr.client.solrj.beans.Field;

/**
 *
 * @author ckchen
 * 
 * This class encapsulates the code and data necessary to represent GWAS data
 * of an IMPC gene
 * 
 */
public class GwasDTO {
	
	public static final String GWAS_MGI_GENE_ID = "gwasMgiGeneId";
	public static final String GWAS_MGI_GENE_SYMBOL = "gwasMgiGeneSymbol";
	public static final String GWAS_MGI_ALLELE_ID = "gwasMgiAlleleId";
	public static final String GWAS_MGI_ALLELE_NAME = "gwasMgiAlleleName";
	public static final String GWAS_PHENO_MAPPING_CATEGORY = "gwasPhenoMappingCategory";
	
	public static final String GWAS_MP_TERM_ID = "gwasMpTermId";
	public static final String GWAS_MP_TERM_NAME = "gwasMpTermName";
	public static final String GWAS_MOUSE_GENDER = "gwasMouseGender";
	
	public static final String GWAS_PVALUE = "gwasPvalue";
	public static final String GWAS_DISEASE_TRAIT = "gwasDiseaseTrait";
	public static final String GWAS_REPORTED_GENE = "gwasReportedGene";
	public static final String GWAS_MAPPED_GENE = "gwasMappedGene";
	public static final String GWAS_UPSTREAM_GENE = "gwasUpstreamGene";
	public static final String GWAS_DOWNSTREAM_GENE = "gwasDownstreamGene";
	public static final String GWAS_SNP_ID = "gwasSnpId";
    
	@Field(GWAS_MGI_GENE_ID)
	private String gwasMgiGeneId;

	@Field(GWAS_MGI_GENE_SYMBOL)
	private String gwasMgiGeneSymbol;

	@Field(GWAS_MGI_ALLELE_ID)
	private String gwasMgiAlleleId;

	@Field(GWAS_MGI_ALLELE_NAME)
	private String gwasMgiAlleleName;
	
	@Field(GWAS_PHENO_MAPPING_CATEGORY)
	private String gwasPhenoMappingCategory;
	
	@Field(GWAS_MP_TERM_ID)
	private String gwasMpTermId;
	
	@Field(GWAS_MP_TERM_NAME)
	private String gwasMpTermName;
	
	@Field(GWAS_MOUSE_GENDER)
	private String gwasMouseGender;
	
	@Field(GWAS_PVALUE)
	private float gwasPvalue;
	
	@Field(GWAS_DISEASE_TRAIT)
	private String gwasDiseaseTrait;
	
	@Field(GWAS_REPORTED_GENE)
	private String gwasReportedGene;
	
	@Field(GWAS_MAPPED_GENE)
	private String gwasMappedGene;
	
	@Field(GWAS_UPSTREAM_GENE)
	private String gwasUpstreamGene;
	
	@Field(GWAS_DOWNSTREAM_GENE)
	private String gwasDownstreamGene;
	
	@Field(GWAS_SNP_ID)
	private String gwasSnpId;

	public String getGwasMgiGeneId() {
		return gwasMgiGeneId;
	}

	public void setGwasMgiGeneId(String gwasMgiGeneId) {
		this.gwasMgiGeneId = gwasMgiGeneId;
	}

	public String getGwasMgiGeneSymbol() {
		return gwasMgiGeneSymbol;
	}

	public void setGwasMgiGeneSymbol(String gwasMgiGeneSymbol) {
		this.gwasMgiGeneSymbol = gwasMgiGeneSymbol;
	}

	public String getGwasMgiAlleleId() {
		return gwasMgiAlleleId;
	}

	public void setGwasMgiAlleleId(String gwasMgiAlleleId) {
		this.gwasMgiAlleleId = gwasMgiAlleleId;
	}

	public String getGwasMgiAlleleName() {
		return gwasMgiAlleleName;
	}

	public void setGwasMgiAlleleName(String gwasMgiAlleleName) {
		this.gwasMgiAlleleName = gwasMgiAlleleName;
	}

	public String getGwasPhenoMappingCategory() {
		return gwasPhenoMappingCategory;
	}

	public void setGwasPhenoMappingCategory(String gwasPhenoMappingCategory) {
		this.gwasPhenoMappingCategory = gwasPhenoMappingCategory;
	}

	public String getGwasMpTermId() {
		return gwasMpTermId;
	}

	public void setGwasMpTermId(String gwasMpTermId) {
		this.gwasMpTermId = gwasMpTermId;
	}

	public String getGwasMpTermName() {
		return gwasMpTermName;
	}

	public void setGwasMpTermName(String gwasMpTermName) {
		this.gwasMpTermName = gwasMpTermName;
	}

	public String getGwasMouseGender() {
		return gwasMouseGender;
	}

	public void setGwasMouseGender(String gwasMouseGender) {
		this.gwasMouseGender = gwasMouseGender;
	}

	public float getGwasPvalue() {
		return gwasPvalue;
	}

	public void setGwasPvalue(float gwasPvalue) {
		this.gwasPvalue = gwasPvalue;
	}

	public String getGwasDiseaseTrait() {
		return gwasDiseaseTrait;
	}

	public void setGwasDiseaseTrait(String gwasDiseaseTrait) {
		this.gwasDiseaseTrait = gwasDiseaseTrait;
	}

	public String getGwasReportedGene() {
		return gwasReportedGene;
	}

	public void setGwasReportedGene(String gwasReportedGene) {
		this.gwasReportedGene = gwasReportedGene;
	}

	public String getGwasMappedGene() {
		return gwasMappedGene;
	}

	public void setGwasMappedGene(String gwasMappedGene) {
		this.gwasMappedGene = gwasMappedGene;
	}

	public String getGwasUpstreamGene() {
		return gwasUpstreamGene;
	}

	public void setGwasUpstreamGene(String gwasUpstreamGene) {
		this.gwasUpstreamGene = gwasUpstreamGene;
	}

	public String getGwasDownstreamGene() {
		return gwasDownstreamGene;
	}

	public void setGwasDownstreamGene(String gwasDownstreamGene) {
		this.gwasDownstreamGene = gwasDownstreamGene;
	}

	public String getGwasSnpId() {
		return gwasSnpId;
	}

	public void setGwasSnpId(String gwasSnpId) {
		this.gwasSnpId = gwasSnpId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((gwasDiseaseTrait == null) ? 0 : gwasDiseaseTrait.hashCode());
		result = prime * result + ((gwasDownstreamGene == null) ? 0 : gwasDownstreamGene.hashCode());
		result = prime * result + ((gwasMappedGene == null) ? 0 : gwasMappedGene.hashCode());
		result = prime * result + ((gwasMgiAlleleId == null) ? 0 : gwasMgiAlleleId.hashCode());
		result = prime * result + ((gwasMgiAlleleName == null) ? 0 : gwasMgiAlleleName.hashCode());
		result = prime * result + ((gwasMgiGeneId == null) ? 0 : gwasMgiGeneId.hashCode());
		result = prime * result + ((gwasMgiGeneSymbol == null) ? 0 : gwasMgiGeneSymbol.hashCode());
		result = prime * result + ((gwasMouseGender == null) ? 0 : gwasMouseGender.hashCode());
		result = prime * result + ((gwasMpTermId == null) ? 0 : gwasMpTermId.hashCode());
		result = prime * result + ((gwasMpTermName == null) ? 0 : gwasMpTermName.hashCode());
		result = prime * result + ((gwasPhenoMappingCategory == null) ? 0 : gwasPhenoMappingCategory.hashCode());
		result = prime * result + Float.floatToIntBits(gwasPvalue);
		result = prime * result + ((gwasReportedGene == null) ? 0 : gwasReportedGene.hashCode());
		result = prime * result + ((gwasSnpId == null) ? 0 : gwasSnpId.hashCode());
		result = prime * result + ((gwasUpstreamGene == null) ? 0 : gwasUpstreamGene.hashCode());
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
		GwasDTO other = (GwasDTO) obj;
		if (gwasDiseaseTrait == null) {
			if (other.gwasDiseaseTrait != null)
				return false;
		} else if (!gwasDiseaseTrait.equals(other.gwasDiseaseTrait))
			return false;
		if (gwasDownstreamGene == null) {
			if (other.gwasDownstreamGene != null)
				return false;
		} else if (!gwasDownstreamGene.equals(other.gwasDownstreamGene))
			return false;
		if (gwasMappedGene == null) {
			if (other.gwasMappedGene != null)
				return false;
		} else if (!gwasMappedGene.equals(other.gwasMappedGene))
			return false;
		if (gwasMgiAlleleId == null) {
			if (other.gwasMgiAlleleId != null)
				return false;
		} else if (!gwasMgiAlleleId.equals(other.gwasMgiAlleleId))
			return false;
		if (gwasMgiAlleleName == null) {
			if (other.gwasMgiAlleleName != null)
				return false;
		} else if (!gwasMgiAlleleName.equals(other.gwasMgiAlleleName))
			return false;
		if (gwasMgiGeneId == null) {
			if (other.gwasMgiGeneId != null)
				return false;
		} else if (!gwasMgiGeneId.equals(other.gwasMgiGeneId))
			return false;
		if (gwasMgiGeneSymbol == null) {
			if (other.gwasMgiGeneSymbol != null)
				return false;
		} else if (!gwasMgiGeneSymbol.equals(other.gwasMgiGeneSymbol))
			return false;
		if (gwasMouseGender == null) {
			if (other.gwasMouseGender != null)
				return false;
		} else if (!gwasMouseGender.equals(other.gwasMouseGender))
			return false;
		if (gwasMpTermId == null) {
			if (other.gwasMpTermId != null)
				return false;
		} else if (!gwasMpTermId.equals(other.gwasMpTermId))
			return false;
		if (gwasMpTermName == null) {
			if (other.gwasMpTermName != null)
				return false;
		} else if (!gwasMpTermName.equals(other.gwasMpTermName))
			return false;
		if (gwasPhenoMappingCategory == null) {
			if (other.gwasPhenoMappingCategory != null)
				return false;
		} else if (!gwasPhenoMappingCategory.equals(other.gwasPhenoMappingCategory))
			return false;
		if (Float.floatToIntBits(gwasPvalue) != Float.floatToIntBits(other.gwasPvalue))
			return false;
		if (gwasReportedGene == null) {
			if (other.gwasReportedGene != null)
				return false;
		} else if (!gwasReportedGene.equals(other.gwasReportedGene))
			return false;
		if (gwasSnpId == null) {
			if (other.gwasSnpId != null)
				return false;
		} else if (!gwasSnpId.equals(other.gwasSnpId))
			return false;
		if (gwasUpstreamGene == null) {
			if (other.gwasUpstreamGene != null)
				return false;
		} else if (!gwasUpstreamGene.equals(other.gwasUpstreamGene))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String
				.format("GwasDTO [gwasMgiGeneId=%s, gwasMgiGeneSymbol=%s, gwasMgiAlleleId=%s, gwasMgiAlleleName=%s, gwasPhenoMappingCategory=%s, gwasMpTermId=%s, gwasMpTermName=%s, gwasMouseGender=%s, gwasPvalue=%s, gwasDiseaseTrait=%s, gwasReportedGene=%s, gwasMappedGene=%s, gwasUpstreamGene=%s, gwasDownstreamGene=%s, gwasSnpId=%s]",
						gwasMgiGeneId, gwasMgiGeneSymbol, gwasMgiAlleleId, gwasMgiAlleleName, gwasPhenoMappingCategory, gwasMpTermId, gwasMpTermName,
						gwasMouseGender, gwasPvalue, gwasDiseaseTrait, gwasReportedGene, gwasMappedGene, gwasUpstreamGene, gwasDownstreamGene, gwasSnpId);
	}

	

	
   
}
