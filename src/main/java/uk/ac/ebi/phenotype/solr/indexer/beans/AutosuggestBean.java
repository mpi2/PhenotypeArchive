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
package uk.ac.ebi.phenotype.solr.indexer.beans;

import org.apache.solr.client.solrj.beans.Field;

import java.util.List;


/**
 * SolrJ class to support loading the autosuggest core
 */
public class AutosuggestBean {

	public static final String DOCTYPE = "docType";
	public static final String MGI_ACCESSION_ID = "mgi_accession_id";
	public static final String MGI_ALLELE_ACCESSION_ID = "allele_accession_id";
	public static final String MARKER_SYMBOL = "marker_symbol";
	public static final String MARKER_NAME = "marker_name";
	public static final String MARKER_SYNONYM = "marker_synonym";
	public static final String HUMAN_GENE_SYMBOL = "human_gene_symbol";
	public static final String HP_ID = "hp_id";
	public static final String HP_TERM = "hp_term";
	public static final String HP_SYNONYM = "hp_synonym";
	public static final String HP_TERM_SYNONYM = "hp_term_synonym";
	public static final String HPMP_ID = "hpmp_id";
	public static final String HPMP_TERM = "hpmp_term";
	public static final String MP_ID = "mp_id";
	public static final String MP_TERM = "mp_term";
	public static final String MP_TERM_SYNONYM = "mp_term_synonym";
	public static final String CHILD_MP_ID = "child_mp_id";
	public static final String CHILD_MP_TERM = "child_mp_term";
	public static final String CHILD_MP_TERM_SYNONYM = "child_mp_term_synonym";
	public static final String INTERMEDIATE_MP_ID = "intermediate_mp_id";
	public static final String INTERMEDIATE_MP_TERM = "intermediate_mp_term";
	public static final String INTERMEDIATE_MP_TERM_SYNONYM = "intermediate_mp_term_synonym";
	public static final String TOP_LEVEL_MP_ID = "top_level_mp_id";
	public static final String TOP_LEVEL_MP_TERM = "top_level_mp_term";
	public static final String TOP_LEVEL_MP_TERM_SYNONYM = "top_level_mp_term_synonym";
	public static final String MA_ID = "ma_id";
	public static final String MA_TERM = "ma_term";
	public static final String MA_TERM_SYNONYM = "ma_term_synonym";
	public static final String CHILD_MA_ID = "child_ma_id";
	public static final String CHILD_MA_TERM = "child_ma_term";
	public static final String CHILD_MA_TERM_SYNONYM = "child_ma_term_synonym";
	public static final String SELECTED_TOP_LEVEL_MA_ID = "selected_top_level_ma_id";
	public static final String SELECTED_TOP_LEVEL_MA_TERM = "selected_top_level_ma_term";
	public static final String SELECTED_TOP_LEVEL_MA_TERM_SYNONYM = "selected_top_level_ma_term_synonym";
	public static final String DISEASE_ID = "disease_id";
	public static final String DISEASE_TERM = "disease_term";
	public static final String DISEASE_ALTS = "disease_alts";
	public static final String AUTO_SUGGEST = "auto_suggest";

	public static final String GWAS_MGI_GENE_ID = "gwas_mgi_gene_id";
	public static final String GWAS_MGI_GENE_SYMBOL = "gwas_mgi_gene_symbol";
	public static final String GWAS_MGI_ALLELE_ID = "gwas_mgi_allele_id";
	public static final String GWAS_MGI_ALLELE_NAME = "gwas_mgi_allele_name";
	
	public static final String GWAS_MP_TERM_ID = "gwas_mp_term_id";
	public static final String GWAS_MP_TERM_NAME = "gwas_mp_term_name";
	
	public static final String GWAS_DISEASE_TRAIT = "gwas_disease_trait";
	public static final String GWAS_REPORTED_GENE = "gwas_reported_gene";
	public static final String GWAS_MAPPED_GENE = "gwas_mapped_gene";
	public static final String GWAS_UPSTREAM_GENE = "gwas_upstream_gene";
	public static final String GWAS_DOWNSTREAM_GENE = "gwas_downstream_gene";
	public static final String GWAS_SNP_ID = "gwas_snp_id";
	
	@Field(DOCTYPE)
	private String docType;

	@Field(MGI_ACCESSION_ID)
	private String mgiAccessionID;

	@Field(MGI_ALLELE_ACCESSION_ID)
	private List<String> mgiAlleleAccessionIds;
	
	@Field(MARKER_SYMBOL)
	private String markerSymbol;

	@Field(MARKER_NAME)
	private String markerName;

	@Field(MARKER_SYNONYM)
	private String markerSynonym;

	@Field(HUMAN_GENE_SYMBOL)
	private String humanGeneSymbol;

	@Field(HP_ID)
	private String hpID;

	@Field(HP_TERM)
	private String hpTerm;

	@Field(HP_SYNONYM)
	private String hpSynonym;

	@Field(HP_TERM_SYNONYM)
	private String hpTermSynonym;

	@Field(HPMP_ID)
	private String hpmpID;

	@Field(HPMP_TERM)
	private String hpmpTerm;

	@Field(MP_ID)
	private String mpID;

	@Field(MP_TERM)
	private String mpTerm;

	@Field(MP_TERM_SYNONYM)
	private String mpTermSynonym;

	@Field(CHILD_MP_ID)
	private String childMpID;

	@Field(CHILD_MP_TERM)
	private String childMpTerm;

	@Field(CHILD_MP_TERM_SYNONYM)
	private String childMpTermSynonym;

	@Field(INTERMEDIATE_MP_ID)
	private String intermediateMpID;

	@Field(INTERMEDIATE_MP_TERM)
	private String intermediateMpTerm;

	@Field(INTERMEDIATE_MP_TERM_SYNONYM)
	private String intermediateMpTermSynonym;

	@Field(TOP_LEVEL_MP_ID)
	private String topLevelMpID;

	@Field(TOP_LEVEL_MP_TERM)
	private String topLevelMpTerm;

	@Field(TOP_LEVEL_MP_TERM_SYNONYM)
	private String topLevelMpTermSynonym;

	@Field(MA_ID)
	private String maID;

	@Field(MA_TERM)
	private String maTerm;

	@Field(MA_TERM_SYNONYM)
	private String maTermSynonym;

	@Field(CHILD_MA_ID)
	private String childMaID;

	@Field(CHILD_MA_TERM)
	private String childMaTerm;

	@Field(CHILD_MA_TERM_SYNONYM)
	private String childMaTermSynonym;

	@Field(SELECTED_TOP_LEVEL_MA_ID)
	private String selectedTopLevelMaID;

	@Field(SELECTED_TOP_LEVEL_MA_TERM)
	private String selectedTopLevelMaTerm;

	@Field(SELECTED_TOP_LEVEL_MA_TERM_SYNONYM)
	private String selectedTopLevelMaTermSynonym;

	@Field(DISEASE_ID)
	private String diseaseID;

	@Field(DISEASE_TERM)
	private String diseaseTerm;

	@Field(DISEASE_ALTS)
	private String diseaseAlts;

	@Field(AUTO_SUGGEST)
	private List<String> autosuggest;

	@Field(GWAS_MGI_GENE_ID)
	private String gwasMgiGeneId;

	@Field(GWAS_MGI_GENE_SYMBOL)
	private String gwasMgiGeneSymbol;

	@Field(GWAS_MGI_ALLELE_ID)
	private String gwasMgiAlleleId;

	@Field(GWAS_MGI_ALLELE_NAME)
	private String gwasMgiAlleleName;
	
	@Field(GWAS_MP_TERM_ID)
	private String gwasMpTermId;
	
	@Field(GWAS_MP_TERM_NAME)
	private String gwasMpTermName;
	
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
	
	
	public String getDocType() {

		return docType;
	}


	public void setDocType(String docType) {

		this.docType = docType;
	}

	public String getMgiAccessionID() {

		return mgiAccessionID;
	}

	public void setMgiAccessionID(String mgiAccessionID) {

		this.mgiAccessionID = mgiAccessionID;
	}

	public List<String> getMgiAlleleAccessionIds() {

		return mgiAlleleAccessionIds;
	}

	public void setMgiAlleleAccessionIds(List<String> mgiAlleleAccessionIds) {

		this.mgiAlleleAccessionIds = mgiAlleleAccessionIds;
	}
	

	public String getMarkerSymbol() {

		return markerSymbol;
	}


	public void setMarkerSymbol(String markerSymbol) {

		this.markerSymbol = markerSymbol;
	}


	public String getMarkerName() {

		return markerName;
	}


	public void setMarkerName(String markerName) {

		this.markerName = markerName;
	}


	public String getMarkerSynonym() {

		return markerSynonym;
	}


	public void setMarkerSynonym(String markerSynonym) {

		this.markerSynonym = markerSynonym;
	}


	public String getHumanGeneSymbol() {

		return humanGeneSymbol;
	}


	public void setHumanGeneSymbol(String humanGeneSymbol) {

		this.humanGeneSymbol = humanGeneSymbol;
	}


	public String getHpID() {

		return hpID;
	}


	public void setHpID(String hpID) {

		this.hpID = hpID;
	}


	public String getHpTerm() {

		return hpTerm;
	}


	public void setHpTerm(String hpTerm) {

		this.hpTerm = hpTerm;
	}


	public String getHpSynonym() {

		return hpSynonym;
	}


	public void setHpSynonym(String hpSynonym) {

		this.hpSynonym = hpSynonym;
	}


	public String getHpTermSynonym() {

		return hpTermSynonym;
	}


	public void setHpTermSynonym(String hpTermSynonym) {

		this.hpTermSynonym = hpTermSynonym;
	}


	public String getHpmpID() {

		return hpmpID;
	}


	public void setHpmpID(String hpmpID) {

		this.hpmpID = hpmpID;
	}


	public String getHpmpTerm() {

		return hpmpTerm;
	}


	public void setHpmpTerm(String hpmpTerm) {

		this.hpmpTerm = hpmpTerm;
	}


	public String getMpID() {

		return mpID;
	}


	public void setMpID(String mpID) {

		this.mpID = mpID;
	}


	public String getMpTerm() {

		return mpTerm;
	}


	public void setMpTerm(String mpTerm) {

		this.mpTerm = mpTerm;
	}


	public String getMpTermSynonym() {

		return mpTermSynonym;
	}


	public void setMpTermSynonym(String mpTermSynonym) {

		this.mpTermSynonym = mpTermSynonym;
	}


	public String getChildMpID() {

		return childMpID;
	}


	public void setChildMpID(String childMpID) {

		this.childMpID = childMpID;
	}


	public String getChildMpTerm() {

		return childMpTerm;
	}


	public void setChildMpTerm(String childMpTerm) {

		this.childMpTerm = childMpTerm;
	}


	public String getChildMpTermSynonym() {

		return childMpTermSynonym;
	}


	public void setChildMpTermSynonym(String childMpTermSynonym) {

		this.childMpTermSynonym = childMpTermSynonym;
	}


	public String getIntermediateMpID() {

		return intermediateMpID;
	}


	public void setIntermediateMpID(String intermediateMpID) {

		this.intermediateMpID = intermediateMpID;
	}


	public String getIntermediateMpTerm() {

		return intermediateMpTerm;
	}


	public void setIntermediateMpTerm(String intermediateMpTerm) {

		this.intermediateMpTerm = intermediateMpTerm;
	}


	public String getIntermediateMpTermSynonym() {

		return intermediateMpTermSynonym;
	}


	public void setIntermediateMpTermSynonym(String intermediateMpTermSynonym) {

		this.intermediateMpTermSynonym = intermediateMpTermSynonym;
	}


	public String getTopLevelMpID() {

		return topLevelMpID;
	}


	public void setTopLevelMpID(String topLevelMpID) {

		this.topLevelMpID = topLevelMpID;
	}


	public String getTopLevelMpTerm() {

		return topLevelMpTerm;
	}


	public void setTopLevelMpTerm(String topLevelMpTerm) {

		this.topLevelMpTerm = topLevelMpTerm;
	}


	public String getTopLevelMpTermSynonym() {

		return topLevelMpTermSynonym;
	}


	public void setTopLevelMpTermSynonym(String topLevelMpTermSynonym) {

		this.topLevelMpTermSynonym = topLevelMpTermSynonym;
	}


	public String getMaID() {

		return maID;
	}


	public void setMaID(String maID) {

		this.maID = maID;
	}


	public String getMaTerm() {

		return maTerm;
	}


	public void setMaTerm(String maTerm) {

		this.maTerm = maTerm;
	}


	public String getMaTermSynonym() {

		return maTermSynonym;
	}


	public void setMaTermSynonym(String maTermSynonym) {

		this.maTermSynonym = maTermSynonym;
	}


	public String getChildMaID() {

		return childMaID;
	}


	public void setChildMaID(String childMaID) {

		this.childMaID = childMaID;
	}


	public String getChildMaTerm() {

		return childMaTerm;
	}


	public void setChildMaTerm(String childMaTerm) {

		this.childMaTerm = childMaTerm;
	}


	public String getChildMaTermSynonym() {

		return childMaTermSynonym;
	}


	public void setChildMaTermSynonym(String childMaTermSynonym) {

		this.childMaTermSynonym = childMaTermSynonym;
	}


	public String getSelectedTopLevelMaID() {

		return selectedTopLevelMaID;
	}


	public void setSelectedTopLevelMaID(String selectedTopLevelMaID) {

		this.selectedTopLevelMaID = selectedTopLevelMaID;
	}


	public String getSelectedTopLevelMaTerm() {

		return selectedTopLevelMaTerm;
	}


	public void setSelectedTopLevelMaTerm(String selectedTopLevelMaTerm) {

		this.selectedTopLevelMaTerm = selectedTopLevelMaTerm;
	}


	public String getSelectedTopLevelMaTermSynonym() {

		return selectedTopLevelMaTermSynonym;
	}


	public void setSelectedTopLevelMaTermSynonym(String selectedTopLevelMaTermSynonym) {

		this.selectedTopLevelMaTermSynonym = selectedTopLevelMaTermSynonym;
	}


	public String getDiseaseID() {

		return diseaseID;
	}


	public void setDiseaseID(String diseaseID) {

		this.diseaseID = diseaseID;
	}


	public String getDiseaseTerm() {

		return diseaseTerm;
	}


	public void setDiseaseTerm(String diseaseTerm) {

		this.diseaseTerm = diseaseTerm;
	}


	public String getDiseaseAlts() {

		return diseaseAlts;
	}


	public void setDiseaseAlts(String diseaseAlts) {

		this.diseaseAlts = diseaseAlts;
	}


	public List<String> getAutosuggest() {

		return autosuggest;
	}


	public void setAutosuggest(List<String> autosuggest) {

		this.autosuggest = autosuggest;
	}

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
		result = prime * result + ((autosuggest == null) ? 0 : autosuggest.hashCode());
		result = prime * result + ((childMaID == null) ? 0 : childMaID.hashCode());
		result = prime * result + ((childMaTerm == null) ? 0 : childMaTerm.hashCode());
		result = prime * result + ((childMaTermSynonym == null) ? 0 : childMaTermSynonym.hashCode());
		result = prime * result + ((childMpID == null) ? 0 : childMpID.hashCode());
		result = prime * result + ((childMpTerm == null) ? 0 : childMpTerm.hashCode());
		result = prime * result + ((childMpTermSynonym == null) ? 0 : childMpTermSynonym.hashCode());
		result = prime * result + ((diseaseAlts == null) ? 0 : diseaseAlts.hashCode());
		result = prime * result + ((diseaseID == null) ? 0 : diseaseID.hashCode());
		result = prime * result + ((diseaseTerm == null) ? 0 : diseaseTerm.hashCode());
		result = prime * result + ((docType == null) ? 0 : docType.hashCode());
		result = prime * result + ((gwasDiseaseTrait == null) ? 0 : gwasDiseaseTrait.hashCode());
		result = prime * result + ((gwasDownstreamGene == null) ? 0 : gwasDownstreamGene.hashCode());
		result = prime * result + ((gwasMappedGene == null) ? 0 : gwasMappedGene.hashCode());
		result = prime * result + ((gwasMgiAlleleId == null) ? 0 : gwasMgiAlleleId.hashCode());
		result = prime * result + ((gwasMgiAlleleName == null) ? 0 : gwasMgiAlleleName.hashCode());
		result = prime * result + ((gwasMgiGeneId == null) ? 0 : gwasMgiGeneId.hashCode());
		result = prime * result + ((gwasMgiGeneSymbol == null) ? 0 : gwasMgiGeneSymbol.hashCode());
		result = prime * result + ((gwasMpTermId == null) ? 0 : gwasMpTermId.hashCode());
		result = prime * result + ((gwasMpTermName == null) ? 0 : gwasMpTermName.hashCode());
		result = prime * result + ((gwasReportedGene == null) ? 0 : gwasReportedGene.hashCode());
		result = prime * result + ((gwasSnpId == null) ? 0 : gwasSnpId.hashCode());
		result = prime * result + ((gwasUpstreamGene == null) ? 0 : gwasUpstreamGene.hashCode());
		result = prime * result + ((hpID == null) ? 0 : hpID.hashCode());
		result = prime * result + ((hpSynonym == null) ? 0 : hpSynonym.hashCode());
		result = prime * result + ((hpTerm == null) ? 0 : hpTerm.hashCode());
		result = prime * result + ((hpTermSynonym == null) ? 0 : hpTermSynonym.hashCode());
		result = prime * result + ((hpmpID == null) ? 0 : hpmpID.hashCode());
		result = prime * result + ((hpmpTerm == null) ? 0 : hpmpTerm.hashCode());
		result = prime * result + ((humanGeneSymbol == null) ? 0 : humanGeneSymbol.hashCode());
		result = prime * result + ((intermediateMpID == null) ? 0 : intermediateMpID.hashCode());
		result = prime * result + ((intermediateMpTerm == null) ? 0 : intermediateMpTerm.hashCode());
		result = prime * result + ((intermediateMpTermSynonym == null) ? 0 : intermediateMpTermSynonym.hashCode());
		result = prime * result + ((maID == null) ? 0 : maID.hashCode());
		result = prime * result + ((maTerm == null) ? 0 : maTerm.hashCode());
		result = prime * result + ((maTermSynonym == null) ? 0 : maTermSynonym.hashCode());
		result = prime * result + ((markerName == null) ? 0 : markerName.hashCode());
		result = prime * result + ((markerSymbol == null) ? 0 : markerSymbol.hashCode());
		result = prime * result + ((markerSynonym == null) ? 0 : markerSynonym.hashCode());
		result = prime * result + ((mgiAccessionID == null) ? 0 : mgiAccessionID.hashCode());
		result = prime * result + ((mgiAlleleAccessionIds == null) ? 0 : mgiAlleleAccessionIds.hashCode());
		result = prime * result + ((mpID == null) ? 0 : mpID.hashCode());
		result = prime * result + ((mpTerm == null) ? 0 : mpTerm.hashCode());
		result = prime * result + ((mpTermSynonym == null) ? 0 : mpTermSynonym.hashCode());
		result = prime * result + ((selectedTopLevelMaID == null) ? 0 : selectedTopLevelMaID.hashCode());
		result = prime * result + ((selectedTopLevelMaTerm == null) ? 0 : selectedTopLevelMaTerm.hashCode());
		result = prime * result + ((selectedTopLevelMaTermSynonym == null) ? 0 : selectedTopLevelMaTermSynonym.hashCode());
		result = prime * result + ((topLevelMpID == null) ? 0 : topLevelMpID.hashCode());
		result = prime * result + ((topLevelMpTerm == null) ? 0 : topLevelMpTerm.hashCode());
		result = prime * result + ((topLevelMpTermSynonym == null) ? 0 : topLevelMpTermSynonym.hashCode());
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
		AutosuggestBean other = (AutosuggestBean) obj;
		if (autosuggest == null) {
			if (other.autosuggest != null)
				return false;
		} else if (!autosuggest.equals(other.autosuggest))
			return false;
		if (childMaID == null) {
			if (other.childMaID != null)
				return false;
		} else if (!childMaID.equals(other.childMaID))
			return false;
		if (childMaTerm == null) {
			if (other.childMaTerm != null)
				return false;
		} else if (!childMaTerm.equals(other.childMaTerm))
			return false;
		if (childMaTermSynonym == null) {
			if (other.childMaTermSynonym != null)
				return false;
		} else if (!childMaTermSynonym.equals(other.childMaTermSynonym))
			return false;
		if (childMpID == null) {
			if (other.childMpID != null)
				return false;
		} else if (!childMpID.equals(other.childMpID))
			return false;
		if (childMpTerm == null) {
			if (other.childMpTerm != null)
				return false;
		} else if (!childMpTerm.equals(other.childMpTerm))
			return false;
		if (childMpTermSynonym == null) {
			if (other.childMpTermSynonym != null)
				return false;
		} else if (!childMpTermSynonym.equals(other.childMpTermSynonym))
			return false;
		if (diseaseAlts == null) {
			if (other.diseaseAlts != null)
				return false;
		} else if (!diseaseAlts.equals(other.diseaseAlts))
			return false;
		if (diseaseID == null) {
			if (other.diseaseID != null)
				return false;
		} else if (!diseaseID.equals(other.diseaseID))
			return false;
		if (diseaseTerm == null) {
			if (other.diseaseTerm != null)
				return false;
		} else if (!diseaseTerm.equals(other.diseaseTerm))
			return false;
		if (docType == null) {
			if (other.docType != null)
				return false;
		} else if (!docType.equals(other.docType))
			return false;
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
		if (hpID == null) {
			if (other.hpID != null)
				return false;
		} else if (!hpID.equals(other.hpID))
			return false;
		if (hpSynonym == null) {
			if (other.hpSynonym != null)
				return false;
		} else if (!hpSynonym.equals(other.hpSynonym))
			return false;
		if (hpTerm == null) {
			if (other.hpTerm != null)
				return false;
		} else if (!hpTerm.equals(other.hpTerm))
			return false;
		if (hpTermSynonym == null) {
			if (other.hpTermSynonym != null)
				return false;
		} else if (!hpTermSynonym.equals(other.hpTermSynonym))
			return false;
		if (hpmpID == null) {
			if (other.hpmpID != null)
				return false;
		} else if (!hpmpID.equals(other.hpmpID))
			return false;
		if (hpmpTerm == null) {
			if (other.hpmpTerm != null)
				return false;
		} else if (!hpmpTerm.equals(other.hpmpTerm))
			return false;
		if (humanGeneSymbol == null) {
			if (other.humanGeneSymbol != null)
				return false;
		} else if (!humanGeneSymbol.equals(other.humanGeneSymbol))
			return false;
		if (intermediateMpID == null) {
			if (other.intermediateMpID != null)
				return false;
		} else if (!intermediateMpID.equals(other.intermediateMpID))
			return false;
		if (intermediateMpTerm == null) {
			if (other.intermediateMpTerm != null)
				return false;
		} else if (!intermediateMpTerm.equals(other.intermediateMpTerm))
			return false;
		if (intermediateMpTermSynonym == null) {
			if (other.intermediateMpTermSynonym != null)
				return false;
		} else if (!intermediateMpTermSynonym.equals(other.intermediateMpTermSynonym))
			return false;
		if (maID == null) {
			if (other.maID != null)
				return false;
		} else if (!maID.equals(other.maID))
			return false;
		if (maTerm == null) {
			if (other.maTerm != null)
				return false;
		} else if (!maTerm.equals(other.maTerm))
			return false;
		if (maTermSynonym == null) {
			if (other.maTermSynonym != null)
				return false;
		} else if (!maTermSynonym.equals(other.maTermSynonym))
			return false;
		if (markerName == null) {
			if (other.markerName != null)
				return false;
		} else if (!markerName.equals(other.markerName))
			return false;
		if (markerSymbol == null) {
			if (other.markerSymbol != null)
				return false;
		} else if (!markerSymbol.equals(other.markerSymbol))
			return false;
		if (markerSynonym == null) {
			if (other.markerSynonym != null)
				return false;
		} else if (!markerSynonym.equals(other.markerSynonym))
			return false;
		if (mgiAccessionID == null) {
			if (other.mgiAccessionID != null)
				return false;
		} else if (!mgiAccessionID.equals(other.mgiAccessionID))
			return false;
		if (mgiAlleleAccessionIds == null) {
			if (other.mgiAlleleAccessionIds != null)
				return false;
		} else if (!mgiAlleleAccessionIds.equals(other.mgiAlleleAccessionIds))
			return false;
		if (mpID == null) {
			if (other.mpID != null)
				return false;
		} else if (!mpID.equals(other.mpID))
			return false;
		if (mpTerm == null) {
			if (other.mpTerm != null)
				return false;
		} else if (!mpTerm.equals(other.mpTerm))
			return false;
		if (mpTermSynonym == null) {
			if (other.mpTermSynonym != null)
				return false;
		} else if (!mpTermSynonym.equals(other.mpTermSynonym))
			return false;
		if (selectedTopLevelMaID == null) {
			if (other.selectedTopLevelMaID != null)
				return false;
		} else if (!selectedTopLevelMaID.equals(other.selectedTopLevelMaID))
			return false;
		if (selectedTopLevelMaTerm == null) {
			if (other.selectedTopLevelMaTerm != null)
				return false;
		} else if (!selectedTopLevelMaTerm.equals(other.selectedTopLevelMaTerm))
			return false;
		if (selectedTopLevelMaTermSynonym == null) {
			if (other.selectedTopLevelMaTermSynonym != null)
				return false;
		} else if (!selectedTopLevelMaTermSynonym.equals(other.selectedTopLevelMaTermSynonym))
			return false;
		if (topLevelMpID == null) {
			if (other.topLevelMpID != null)
				return false;
		} else if (!topLevelMpID.equals(other.topLevelMpID))
			return false;
		if (topLevelMpTerm == null) {
			if (other.topLevelMpTerm != null)
				return false;
		} else if (!topLevelMpTerm.equals(other.topLevelMpTerm))
			return false;
		if (topLevelMpTermSynonym == null) {
			if (other.topLevelMpTermSynonym != null)
				return false;
		} else if (!topLevelMpTermSynonym.equals(other.topLevelMpTermSynonym))
			return false;
		return true;
	}

	

}
