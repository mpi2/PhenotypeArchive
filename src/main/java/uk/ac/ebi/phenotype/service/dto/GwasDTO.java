/**
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
/**
 * Copyright © 2015 EMBL - European Bioinformatics Institute
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

package uk.ac.ebi.phenotype.service.dto;

import java.util.List;
import java.util.Objects;

/**
 *
 * @author ckchen
 * 
 * This class encapsulates the code and data necessary to represent GWAS data
 * of an IMPC gene
 * 
 */
public class GwasDTO {
    private String mgiGeneId;
    private String mgiGeneSymbol;
    private String mgiAlleleId;
    private String mgiAlleleName;
    private String phenoMappingCategory;
    private String diseaseTrait;
    private float pvalue;
    private String reportedGene;
    private String mappedGene;
    private String upstreamGene;
    private String downstreamGene;
    private String mpTermId;
    private String mpTermName;
    private String mouseGender;
	public String getMgiGeneId() {
		return mgiGeneId;
	}
	public void setMgiGeneId(String mgiGeneId) {
		this.mgiGeneId = mgiGeneId;
	}
	public String getMgiGeneSymbol() {
		return mgiGeneSymbol;
	}
	public void setMgiGeneSymbol(String mgiGeneSymbol) {
		this.mgiGeneSymbol = mgiGeneSymbol;
	}
	public String getMgiAlleleId() {
		return mgiAlleleId;
	}
	public void setMgiAlleleId(String mgiAlleleId) {
		this.mgiAlleleId = mgiAlleleId;
	}
	public String getMgiAlleleName() {
		return mgiAlleleName;
	}
	public void setMgiAlleleName(String mgiAlleleName) {
		this.mgiAlleleName = mgiAlleleName;
	}
	public String getPhenoMappingCategory() {
		return phenoMappingCategory;
	}
	public void setPhenoMappingCategory(String phenoMappingCategory) {
		this.phenoMappingCategory = phenoMappingCategory;
	}
	public String getDiseaseTrait() {
		return diseaseTrait;
	}
	public void setDiseaseTrait(String diseaseTrait) {
		this.diseaseTrait = diseaseTrait;
	}
	public float getPvalue() {
		return pvalue;
	}
	public void setPvalue(float pvalue) {
		this.pvalue = pvalue;
	}
	public String getReportedGene() {
		return reportedGene;
	}
	public void setReportedGene(String reportedGene) {
		this.reportedGene = reportedGene;
	}
	public String getMappedGene() {
		return mappedGene;
	}
	public void setMappedGene(String mappedGene) {
		this.mappedGene = mappedGene;
	}
	public String getUpstreamGene() {
		return upstreamGene;
	}
	public void setUpstreamGene(String upstreamGene) {
		this.upstreamGene = upstreamGene;
	}
	public String getDownstreamGene() {
		return downstreamGene;
	}
	public void setDownstreamGene(String downstreamGene) {
		this.downstreamGene = downstreamGene;
	}
	public String getMpTermId() {
		return mpTermId;
	}
	public void setMpTermId(String mpTermId) {
		this.mpTermId = mpTermId;
	}
	public String getMpTermName() {
		return mpTermName;
	}
	public void setMpTermName(String mpTermName) {
		this.mpTermName = mpTermName;
	}
	public String getMouseGender() {
		return mouseGender;
	}
	public void setMouseGender(String mouseGender) {
		this.mouseGender = mouseGender;
	}
	@Override
	public String toString() {
		return String
				.format("GwasDTO [mgiGeneId=%s, mgiGeneSymbol=%s, mgiAlleleId=%s, mgiAlleleName=%s, phenoMappingCategory=%s, diseaseTrait=%s, pvalue=%s, reportedGene=%s, mappedGene=%s, upstreamGene=%s, downstreamGene=%s, mpTermId=%s, mpTermName=%s, mouseGender=%s]",
						mgiGeneId, mgiGeneSymbol, mgiAlleleId, mgiAlleleName, phenoMappingCategory, diseaseTrait, pvalue, reportedGene, mappedGene,
						upstreamGene, downstreamGene, mpTermId, mpTermName, mouseGender);
	}
	
    
    
	
   
}
