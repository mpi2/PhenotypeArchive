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


	@Override
	public boolean equals(Object o) {

		if (this == o) return true;
		if (!(o instanceof AutosuggestBean)) return false;

		AutosuggestBean that = (AutosuggestBean) o;

		if (autosuggest != null ? !autosuggest.equals(that.autosuggest) : that.autosuggest != null) return false;
		if (childMaID != null ? !childMaID.equals(that.childMaID) : that.childMaID != null) return false;
		if (childMaTerm != null ? !childMaTerm.equals(that.childMaTerm) : that.childMaTerm != null) return false;
		if (childMaTermSynonym != null ? !childMaTermSynonym.equals(that.childMaTermSynonym) : that.childMaTermSynonym != null)
			return false;
		if (childMpID != null ? !childMpID.equals(that.childMpID) : that.childMpID != null) return false;
		if (childMpTerm != null ? !childMpTerm.equals(that.childMpTerm) : that.childMpTerm != null) return false;
		if (childMpTermSynonym != null ? !childMpTermSynonym.equals(that.childMpTermSynonym) : that.childMpTermSynonym != null)
			return false;
		if (diseaseAlts != null ? !diseaseAlts.equals(that.diseaseAlts) : that.diseaseAlts != null) return false;
		if (diseaseID != null ? !diseaseID.equals(that.diseaseID) : that.diseaseID != null) return false;
		if (diseaseTerm != null ? !diseaseTerm.equals(that.diseaseTerm) : that.diseaseTerm != null) return false;
		if (docType != null ? !docType.equals(that.docType) : that.docType != null) return false;
		if (hpID != null ? !hpID.equals(that.hpID) : that.hpID != null) return false;
		if (hpSynonym != null ? !hpSynonym.equals(that.hpSynonym) : that.hpSynonym != null) return false;
		if (hpTerm != null ? !hpTerm.equals(that.hpTerm) : that.hpTerm != null) return false;
		if (hpTermSynonym != null ? !hpTermSynonym.equals(that.hpTermSynonym) : that.hpTermSynonym != null)
			return false;
		if (hpmpID != null ? !hpmpID.equals(that.hpmpID) : that.hpmpID != null) return false;
		if (hpmpTerm != null ? !hpmpTerm.equals(that.hpmpTerm) : that.hpmpTerm != null) return false;
		if (humanGeneSymbol != null ? !humanGeneSymbol.equals(that.humanGeneSymbol) : that.humanGeneSymbol != null)
			return false;
		if (intermediateMpID != null ? !intermediateMpID.equals(that.intermediateMpID) : that.intermediateMpID != null)
			return false;
		if (intermediateMpTerm != null ? !intermediateMpTerm.equals(that.intermediateMpTerm) : that.intermediateMpTerm != null)
			return false;
		if (intermediateMpTermSynonym != null ? !intermediateMpTermSynonym.equals(that.intermediateMpTermSynonym) : that.intermediateMpTermSynonym != null)
			return false;
		if (maID != null ? !maID.equals(that.maID) : that.maID != null) return false;
		if (maTerm != null ? !maTerm.equals(that.maTerm) : that.maTerm != null) return false;
		if (maTermSynonym != null ? !maTermSynonym.equals(that.maTermSynonym) : that.maTermSynonym != null)
			return false;
		if (markerName != null ? !markerName.equals(that.markerName) : that.markerName != null) return false;
		if (markerSymbol != null ? !markerSymbol.equals(that.markerSymbol) : that.markerSymbol != null) return false;
		if (markerSynonym != null ? !markerSynonym.equals(that.markerSynonym) : that.markerSynonym != null)
			return false;
		if (mgiAccessionID != null ? !mgiAccessionID.equals(that.mgiAccessionID) : that.mgiAccessionID != null)
			return false;
		if (mgiAlleleAccessionIds != null ? !mgiAlleleAccessionIds.equals(that.mgiAlleleAccessionIds) : that.mgiAlleleAccessionIds != null)
			return false;
		if (mpID != null ? !mpID.equals(that.mpID) : that.mpID != null) return false;
		if (mpTerm != null ? !mpTerm.equals(that.mpTerm) : that.mpTerm != null) return false;
		if (mpTermSynonym != null ? !mpTermSynonym.equals(that.mpTermSynonym) : that.mpTermSynonym != null)
			return false;
		if (selectedTopLevelMaID != null ? !selectedTopLevelMaID.equals(that.selectedTopLevelMaID) : that.selectedTopLevelMaID != null)
			return false;
		if (selectedTopLevelMaTerm != null ? !selectedTopLevelMaTerm.equals(that.selectedTopLevelMaTerm) : that.selectedTopLevelMaTerm != null)
			return false;
		if (selectedTopLevelMaTermSynonym != null ? !selectedTopLevelMaTermSynonym.equals(that.selectedTopLevelMaTermSynonym) : that.selectedTopLevelMaTermSynonym != null)
			return false;
		if (topLevelMpID != null ? !topLevelMpID.equals(that.topLevelMpID) : that.topLevelMpID != null) return false;
		if (topLevelMpTerm != null ? !topLevelMpTerm.equals(that.topLevelMpTerm) : that.topLevelMpTerm != null)
			return false;
		if (topLevelMpTermSynonym != null ? !topLevelMpTermSynonym.equals(that.topLevelMpTermSynonym) : that.topLevelMpTermSynonym != null)
			return false;

		return true;
	}


	@Override
	public int hashCode() {

		int result = docType != null ? docType.hashCode() : 0;
		result = 31 * result + (mgiAccessionID != null ? mgiAccessionID.hashCode() : 0);
		result = 31 * result + (mgiAlleleAccessionIds != null ? mgiAlleleAccessionIds.hashCode() : 0);
		result = 31 * result + (markerSymbol != null ? markerSymbol.hashCode() : 0);
		result = 31 * result + (markerName != null ? markerName.hashCode() : 0);
		result = 31 * result + (markerSynonym != null ? markerSynonym.hashCode() : 0);
		result = 31 * result + (humanGeneSymbol != null ? humanGeneSymbol.hashCode() : 0);
		result = 31 * result + (hpID != null ? hpID.hashCode() : 0);
		result = 31 * result + (hpTerm != null ? hpTerm.hashCode() : 0);
		result = 31 * result + (hpSynonym != null ? hpSynonym.hashCode() : 0);
		result = 31 * result + (hpTermSynonym != null ? hpTermSynonym.hashCode() : 0);
		result = 31 * result + (hpmpID != null ? hpmpID.hashCode() : 0);
		result = 31 * result + (hpmpTerm != null ? hpmpTerm.hashCode() : 0);
		result = 31 * result + (mpID != null ? mpID.hashCode() : 0);
		result = 31 * result + (mpTerm != null ? mpTerm.hashCode() : 0);
		result = 31 * result + (mpTermSynonym != null ? mpTermSynonym.hashCode() : 0);
		result = 31 * result + (childMpID != null ? childMpID.hashCode() : 0);
		result = 31 * result + (childMpTerm != null ? childMpTerm.hashCode() : 0);
		result = 31 * result + (childMpTermSynonym != null ? childMpTermSynonym.hashCode() : 0);
		result = 31 * result + (intermediateMpID != null ? intermediateMpID.hashCode() : 0);
		result = 31 * result + (intermediateMpTerm != null ? intermediateMpTerm.hashCode() : 0);
		result = 31 * result + (intermediateMpTermSynonym != null ? intermediateMpTermSynonym.hashCode() : 0);
		result = 31 * result + (topLevelMpID != null ? topLevelMpID.hashCode() : 0);
		result = 31 * result + (topLevelMpTerm != null ? topLevelMpTerm.hashCode() : 0);
		result = 31 * result + (topLevelMpTermSynonym != null ? topLevelMpTermSynonym.hashCode() : 0);
		result = 31 * result + (maID != null ? maID.hashCode() : 0);
		result = 31 * result + (maTerm != null ? maTerm.hashCode() : 0);
		result = 31 * result + (maTermSynonym != null ? maTermSynonym.hashCode() : 0);
		result = 31 * result + (childMaID != null ? childMaID.hashCode() : 0);
		result = 31 * result + (childMaTerm != null ? childMaTerm.hashCode() : 0);
		result = 31 * result + (childMaTermSynonym != null ? childMaTermSynonym.hashCode() : 0);
		result = 31 * result + (selectedTopLevelMaID != null ? selectedTopLevelMaID.hashCode() : 0);
		result = 31 * result + (selectedTopLevelMaTerm != null ? selectedTopLevelMaTerm.hashCode() : 0);
		result = 31 * result + (selectedTopLevelMaTermSynonym != null ? selectedTopLevelMaTermSynonym.hashCode() : 0);
		result = 31 * result + (diseaseID != null ? diseaseID.hashCode() : 0);
		result = 31 * result + (diseaseTerm != null ? diseaseTerm.hashCode() : 0);
		result = 31 * result + (diseaseAlts != null ? diseaseAlts.hashCode() : 0);
		result = 31 * result + (autosuggest != null ? autosuggest.hashCode() : 0);
		return result;
	}
}
