 /**
 * Copyright © 2014 EMBL - European Bioinformatics Institute
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

import org.apache.solr.client.solrj.beans.Field;

import java.util.ArrayList;
import java.util.List;


/**
 * This DTO holds the code and data for transferring images from Sanger.
 * <p/>
 * Images annotated to this MA
 *
 * @author mrelac
 */
public class SangerImageDTO {
	public static final String MA_TERM_ID = "maTermId";
	public static final String SELECTED_TOP_LEVEL_MA_TERM = "selected_top_level_ma_term";
	public static final String SELECTED_TOP_LEVEL_MA_TERM_ID = "selected_top_level_ma_term_id";

	public static final String ID = "id";       // image ID (unique key)

	public static final String PROCEDURE_NAME = "procedure_name";
	public static final String EXP_NAME = "expName";
	public static final String EXP_NAME_EXP = "expName_exp";
	public static final String SYMBOL_GENE = "symbol_gene";

	// Genes annotated to this MA through images
	public static final String MGI_ACCESSION_ID = "mgi_accession_id";
	public static final String MARKER_SYMBOL = "marker_symbol";
	public static final String MARKER_NAME = "marker_name";
	public static final String MARKER_SYNONYM = "marker_synonym";
	public static final String MARKER_TYPE = "marker_type";
	public static final String HUMAN_GENE_SYMBOL = "human_gene_symbol";

	// Latest project status (ES cells/mice production status)
	public static final String STATUS = "status";

	// Latest mice phenotyping status for faceting
	public static final String IMITS_PHENOTYPE_STARTED = "imits_phenotype_started";
	public static final String IMITS_PHENOTYPE_COMPLETE = "imits_phenotype_complete";
	public static final String IMITS_PHENOTYPE_STATUS = "imits_phenotype_status";

	// Phenotyping status
	public static final String LATEST_PHENOTYPE_STATUS = "latest_phenotype_status";
	public static final String LEGACY_PHENOTYPE_STATUS = "legacy_phenotype_status";

	// Production/phenotyping centers
	public static final String LATEST_PRODUCTION_CENTRE = "latest_production_centre";
	public static final String LATEST_PHENOTYPING_CENTRE = "latest_phenotyping_centre";

	// Alleles of a gene
	public static final String ALLELE_NAME = "allele_name";
	public static final String INTERMEDIATE_MP_ID = "intermediate_mp_id";
	public static final String INTERMEDIATE_MP_TERM = "intermediate_mp_term";
	public static final String INTERMEDIATE_MP_TERM_SYN = "intermediate_mp_term_synonym";

	public static final String GENOTYPE = "genotype";
	public static final String MOUSE_ID = "mouseId";
	public static final String COLONY_ID = "colony_id";
	public static final String SANGER_PROCEDURE_ID = "sangerProcedureId";
	public static final String SEX = "gender";

	public static final String SELECTED_TOP_LEVEL_MA_TERM_SYNONYM = "selected_top_level_ma_term_synonym";

	@Field(INTERMEDIATE_MP_ID)
	private List<String> intermediateMpId;

	@Field(INTERMEDIATE_MP_TERM)
	private List<String> intermediateMpTerm;

	@Field(INTERMEDIATE_MP_TERM_SYN)
	private List<String> intermediateMpTermSyn;

	@Field(SELECTED_TOP_LEVEL_MA_TERM_ID)
	private List<String> selectedTopLevelMaTermId;

	@Field(GENOTYPE)
	private String genotype;

	@Field(MOUSE_ID)
	private Integer mouseId;

	@Field(COLONY_ID)
	private Integer colonyId;

	@Field(SANGER_PROCEDURE_ID)
	private Integer sangerProcedureId;

	@Field(SEX)
	private String sex;

	@Field("dataType")
	String dataType;

	@Field("fullResolutionFilePath")
	String fullResolutionFilePath;

	@Field("largeThumbnailFilePath")
	String largeThumbnailFilePath;

	@Field("originalFileName")
	String originalFileName;

	@Field("smallThumbnailFilePath")
	String smallThumbnailFilePath;

	@Field("institute")
	String institute;

	// need method to get imaDcfImageView data for here
	@Field("dcfId")
	String dcfId;

	@Field("dcfExpId")
	String dcfExpId;

	@Field("sangerProcedureName")
	String sangerProcedureName;

	@Field("genotypeString")
	String genotypeString;

	@Field("geneName")
	private List<String> geneName;

	@Field("geneSynonyms")
	private List<String> synonyms;

	@Field("tagValue")
	private List<String> tagValues;

	@Field("tagName")
	private List<String> tagNames;

	@Field("annotationTermId")
	private List<String> annotationTermIds;

	@Field("annotationTermName")
	private List<String> annotationTermNames;

	@Field("maTermId")
	private List<String> maIds;

	@Field("ma_term")
	private List<String> ma_terms;

	@Field("maTermName")
	private List<String> maTermName;

	@Field("ma_term_synonym")
	private List<String> maTermSynonym;

	@Field("subtype")
	private List<String> subtype;

	@Field(SELECTED_TOP_LEVEL_MA_TERM)
	private List<String> selectedTopLevelMaTerm;

	@Field(MA_TERM_ID)
	private List<String> maTermId;

	@Field(ID)
	private String id;

	@Field(PROCEDURE_NAME)
	private List<String> procedureName;

	@Field(EXP_NAME)
	private List<String> expName;

	@Field(EXP_NAME_EXP)
	private List<String> expNameExp;

	@Field(SYMBOL_GENE)
	private List<String> symbolGene;

	@Field(MGI_ACCESSION_ID)
	private List<String> mgiAccessionId;

	@Field(MARKER_SYMBOL)
	private List<String> markerSymbol;

	@Field(MARKER_NAME)
	private List<String> markerName;

	@Field(MARKER_SYNONYM)
	private List<String> markerSynonym;

	@Field(MARKER_TYPE)
	private List<String> markerType;

	@Field(HUMAN_GENE_SYMBOL)
	private List<String> humanGeneSymbol;

	@Field(STATUS)
	private List<String> status;

	@Field(IMITS_PHENOTYPE_STARTED)
	private List<String> imitsPhenotypeStarted;

	@Field(IMITS_PHENOTYPE_COMPLETE)
	private List<String> imitsPhenotypeComplete;

	@Field(IMITS_PHENOTYPE_STATUS)
	private List<String> imitsPhenotypeStatus;

	@Field(LATEST_PHENOTYPE_STATUS)
	private List<String> latestPhenotypeStatus;

	@Field(LEGACY_PHENOTYPE_STATUS)
	private Integer legacyPhenotypeStatus;

	@Field(LATEST_PRODUCTION_CENTRE)
	private List<String> latestProductionCentre;

	@Field(LATEST_PHENOTYPING_CENTRE)
	private List<String> latestPhenotypingCentre;

	@Field(ALLELE_NAME)
	private List<String> alleleName;

	@Field("hp_id")
	private List<String> hpId;

	@Field("hp_term")
	private List<String> hpTerm;

	@Field("mp_id")
	private List<String> mp_id;

	@Field("mp_term_synonym")
	private List<String> mpSyns;

	@Field("selected_top_level_ma_id")
	private List<String> maTopLevelTermIds;

	@Field(SELECTED_TOP_LEVEL_MA_TERM_SYNONYM)
	private ArrayList<String> selectedTopLevelMaTermSynonym;

	@Field("annotatedHigherLevelMpTermName")
	private List<String> annotatedHigherLevelMpTermName;

	@Field("annotatedHigherLevelMpTermId")
	private List<String> annotatedHigherLevelMpTermId;

	@Field("top_level_mp_term_synonym")
	private List<String> topLevelMpTermSynonym;

	@Field("selected_top_level_ma_term")
	private List<String> maTopLevelTerms;

	@Field("mpTermId")
	private List<String> mpTermId;

	@Field("mp_term")
	private List<String> mpTerm;

	@Field("mpTermName")
	private List<String> mpTermName;

	@Field("accession")
	private String accession;

	@Field("symbol")
	private List<String> symbol;

	@Field("ageInWeeks")
	String ageInWeeks;

	@Field("sangerSymbol")
	List<String> sangerSymbol;

	@Field("allele_accession")
	String allele_accession;


	// SETTERS AND GETTERS


	public List<String> getSelectedTopLevelMaTermId() {

		return selectedTopLevelMaTermId;
	}


	public void setSelectedTopLevelMaTermId(List<String> selectedTopLevelMaTermId) {

		this.selectedTopLevelMaTermId = selectedTopLevelMaTermId;
	}


	public List<String> getSelectedTopLevelMaTerm() {

		return selectedTopLevelMaTerm;
	}


	public void setSelectedTopLevelMaTerm(List<String> selectedTopLevelMaTerm) {

		this.selectedTopLevelMaTerm = selectedTopLevelMaTerm;
	}


	public List<String> getIntermediateMpTermSyn() {

		return intermediateMpTermSyn;
	}


	public void setIntermediateMpTermSyn(List<String> intermediateMpTermSyn) {

		this.intermediateMpTermSyn = intermediateMpTermSyn;
	}


	public List<String> getIntermediateMpTerm() {

		return intermediateMpTerm;
	}


	public void setIntermediateMpTerm(List<String> intermediateMpTerm) {

		this.intermediateMpTerm = intermediateMpTerm;
	}


	public List<String> getIntermediateMpId() {

		return intermediateMpId;
	}


	public void setIntermediateMpId(List<String> intermediateMpId) {

		this.intermediateMpId = intermediateMpId;
	}


	public List<String> getHpId() {

		return hpId;
	}


	public void setHpId(List<String> hpId) {

		this.hpId = hpId;
	}


	public List<String> getHpTerm() {

		return hpTerm;
	}


	public void setHpTerm(List<String> hpTerm) {

		this.hpTerm = hpTerm;
	}


	public List<String> getMaTermId() {

		return maTermId;
	}


	public void setMaTermId(List<String> maId) {

		this.maTermId = maId;
	}


	public String getId() {

		return id;
	}


	public void setId(String id) {

		this.id = id;
	}


	public List<String> getProcedureName() {

		return procedureName;
	}


	public void setProcedureName(List<String> procedureName) {

		this.procedureName = procedureName;
	}


	public List<String> getExpName() {

		return expName;
	}


	public void setExpName(List<String> expName) {

		this.expName = expName;
	}


	public List<String> getExpNameExp() {

		return expNameExp;
	}


	public void setExpNameExp(List<String> expNameExp) {

		this.expNameExp = expNameExp;
	}


	public List<String> getSymbolGene() {

		return symbolGene;
	}


	public void setSymbolGene(List<String> symbolGene) {

		this.symbolGene = symbolGene;
	}


	public List<String> getMgiAccessionId() {

		return mgiAccessionId;
	}


	public void setMgiAccessionId(List<String> mgiAccessionId) {

		this.mgiAccessionId = mgiAccessionId;
	}


	public List<String> getMarkerSymbol() {

		return markerSymbol;
	}


	public void setMarkerSymbol(List<String> markerSymbol) {

		this.markerSymbol = markerSymbol;
	}


	public List<String> getMarkerName() {

		return markerName;
	}


	public void setMarkerName(List<String> markerName) {

		this.markerName = markerName;
	}


	public List<String> getMarkerSynonym() {

		return markerSynonym;
	}


	public void setMarkerSynonym(List<String> markerSynonym) {

		this.markerSynonym = markerSynonym;
	}


	public List<String> getMarkerType() {

		return markerType;
	}


	public void setMarkerType(List<String> markerType) {

		this.markerType = markerType;
	}


	public List<String> getHumanGeneSymbol() {

		return humanGeneSymbol;
	}


	public void setHumanGeneSymbol(List<String> humanGeneSymbol) {

		this.humanGeneSymbol = humanGeneSymbol;
	}


	public List<String> getStatus() {

		return status;
	}


	public void setStatus(List<String> status) {

		this.status = status;
	}


	public List<String> getImitsPhenotypeStarted() {

		return imitsPhenotypeStarted;
	}


	public void setImitsPhenotypeStarted(List<String> imitsPhenotypeStarted) {

		this.imitsPhenotypeStarted = imitsPhenotypeStarted;
	}


	public List<String> getImitsPhenotypeComplete() {

		return imitsPhenotypeComplete;
	}


	public void setImitsPhenotypeComplete(List<String> imitsPhenotypeComplete) {

		this.imitsPhenotypeComplete = imitsPhenotypeComplete;
	}


	public List<String> getImitsPhenotypeStatus() {

		return imitsPhenotypeStatus;
	}


	public void setImitsPhenotypeStatus(List<String> imitsPhenotypeStatus) {

		this.imitsPhenotypeStatus = imitsPhenotypeStatus;
	}


	public List<String> getLatestPhenotypeStatus() {

		return latestPhenotypeStatus;
	}


	public void setLatestPhenotypeStatus(List<String> latestPhenotypeStatus) {

		this.latestPhenotypeStatus = latestPhenotypeStatus;
	}


	public Integer getLegacyPhenotypeStatus() {

		return legacyPhenotypeStatus;
	}


	public void setLegacyPhenotypeStatus(Integer legacyPhenotypeStatus) {

		this.legacyPhenotypeStatus = legacyPhenotypeStatus;
	}


	public List<String> getLatestProductionCentre() {

		return latestProductionCentre;
	}


	public void setLatestProductionCentre(List<String> latestProductionCentre) {

		this.latestProductionCentre = latestProductionCentre;
	}


	public List<String> getLatestPhenotypingCentre() {

		return latestPhenotypingCentre;
	}


	public void setLatestPhenotypingCentre(List<String> latestPhenotypingCentre) {

		this.latestPhenotypingCentre = latestPhenotypingCentre;
	}


	public List<String> getAlleleName() {

		return alleleName;
	}


	public void setAlleleName(List<String> alleleName) {

		this.alleleName = alleleName;
	}


	public List<String> getTopLevelMpTermSynonym() {

		return topLevelMpTermSynonym;
	}


	public void setTopLevelMpTermSynonym(List<String> topLevelMpTermSynonym) {

		this.topLevelMpTermSynonym = topLevelMpTermSynonym;
	}


	public List<String> getAnnotatedHigherLevelMpTermName() {

		return annotatedHigherLevelMpTermName;
	}


	public void setAnnotatedHigherLevelMpTermName(List<String> annotatedHigherLevelMpTermName) {

		this.annotatedHigherLevelMpTermName = annotatedHigherLevelMpTermName;
	}


	public List<String> getAnnotatedHigherLevelMpTermId() {

		return annotatedHigherLevelMpTermId;
	}


	public void setAnnotatedHigherLevelMpTermId(List<String> annotatedHigherLevelMpTermId) {

		this.annotatedHigherLevelMpTermId = annotatedHigherLevelMpTermId;
	}


	public List<String> getMaTopLevelTermIds() {

		return maTopLevelTermIds;
	}


	public ArrayList<String> getSelectedTopLevelMaTermSynonym() {

		return selectedTopLevelMaTermSynonym;
	}


	public void setSelectedTopLevelMaTermSynonym(ArrayList<String> selectedTopLevelMaTermSynonym) {

		this.selectedTopLevelMaTermSynonym = selectedTopLevelMaTermSynonym;
	}


	public void setMaTopLevelTermIds(List<String> maTopLevelTermIds) {

		this.maTopLevelTermIds = maTopLevelTermIds;
	}


	public List<String> getMaTopLevelTerms() {

		return maTopLevelTerms;
	}


	public void setMaTopLevelTerms(List<String> maTopLevelTerms) {

		this.maTopLevelTerms = maTopLevelTerms;
	}


	public List<String> getMp_id() {

		return mp_id;
	}


	public List<String> getMpSyns() {

		return mpSyns;
	}


	public void setMpSyns(List<String> mpSyns) {

		this.mpSyns = mpSyns;
	}


	public void setMp_id(List<String> mp_id) {

		this.mp_id = mp_id;
	}


	public List<String> getMpTermId() {

		return mpTermId;
	}


	public void setMpTermId(List<String> mpTermId) {

		this.mpTermId = mpTermId;
	}


	public List<String> getMpTerm() {

		return mpTerm;
	}


	public List<String> getMpTermName() {

		return mpTermName;
	}


	public void setMpTermName(List<String> mpTermName) {

		this.mpTermName = mpTermName;
	}


	public void setMpTerm(List<String> mpTerm) {

		this.mpTerm = mpTerm;

	}


	public void setMpId(List<String> mpTermId) {

		this.mpTermId = mpTermId;

	}


	public List<String> getSubtype() {

		return subtype;
	}


	public void setSubtype(List<String> subtype) {

		this.subtype = subtype;
	}


	public List<String> getMaTermSynonym() {

		return maTermSynonym;
	}


	public void setMaTermSynonym(List<String> maTermSynonym) {

		this.maTermSynonym = maTermSynonym;
	}


	public List<String> getMaTermName() {

		return maTermName;
	}


	public void setMaTermName(List<String> maTermName) {

		this.maTermName = maTermName;
	}


	public List<String> getGeneName() {

		return geneName;
	}


	public void setMaTerm(List<String> ma_terms) {

		this.ma_terms = ma_terms;

	}


	public void setMaId(List<String> ma_ids) {

		this.maIds = ma_ids;

	}


	public void setAnnotationTermName(List<String> annotationTermNames) {

		this.annotationTermNames = annotationTermNames;

	}


	public void setAnnotationTermId(List<String> annotationTermIds) {

		this.annotationTermIds = annotationTermIds;

	}


	public void setTagValues(List<String> tagValues) {

		this.tagValues = tagValues;

	}


	public void setTagNames(List<String> tagNames) {

		this.tagNames = tagNames;

	}


	public void setSynonyms(List<String> syns) {

		this.synonyms = syns;

	}


	public String getAccession() {

		return accession;
	}


	public List<String> getSymbol() {

		return symbol;
	}


	public String getGenotypeString() {

		return genotypeString;
	}


	public void setGeneName(List<String> geneName) {

		this.geneName = geneName;

	}


	public void setAccession(String accession) {

		this.accession = accession;

	}


	public void setSymbol(List<String> symbol) {

		this.symbol = symbol;

	}


	public void setGenotypeString(String genotypeString) {

		this.genotypeString = genotypeString;
	}


	public String getAgeInWeeks() {

		return ageInWeeks;
	}


	public void setAgeInWeeks(String ageInWeeks) {

		this.ageInWeeks = ageInWeeks;
	}


	public List<String> getSangerSymbol() {

		return sangerSymbol;
	}


	public void setSangerSymbol(List<String> sangerSymbol) {

		this.sangerSymbol = sangerSymbol;
	}


	public String getAllele_accession() {

		return allele_accession;
	}


	public void setAllele_accession(String allele_accession) {

		this.allele_accession = allele_accession;
	}


	public String getDataType() {

		return dataType;
	}


	public void setDataType(String dataType) {

		this.dataType = dataType;
	}


	public String getFullResolutionFilePath() {

		return fullResolutionFilePath;
	}


	public void setFullResolutionFilePath(String fullResolutionFilePath) {

		this.fullResolutionFilePath = fullResolutionFilePath;
	}


	public String getLargeThumbnailFilePath() {

		return largeThumbnailFilePath;
	}


	public void setLargeThumbnailFilePath(String largeThumbnailFilePath) {

		this.largeThumbnailFilePath = largeThumbnailFilePath;
	}


	public String getOriginalFileName() {

		return originalFileName;
	}


	public void setOriginalFileName(String originalFileName) {

		this.originalFileName = originalFileName;
	}


	public String getSmallThumbnailFilePath() {

		return smallThumbnailFilePath;
	}


	public void setSmallThumbnailFilePath(String smallThumbnailFilePath) {

		this.smallThumbnailFilePath = smallThumbnailFilePath;
	}


	public String getInstitute() {

		return institute;
	}


	public void setInstitute(String institute) {

		this.institute = institute;
	}


	public String getDcfId() {

		return dcfId;
	}


	public void setDcfId(String dcfId) {

		this.dcfId = dcfId;
	}


	public String getDcfExpId() {

		return dcfExpId;
	}


	public void setDcfExpId(String dcfExpId) {

		this.dcfExpId = dcfExpId;
	}


	public String getSangerProcedureName() {

		return sangerProcedureName;
	}


	public void setSangerProcedureName(String sangerProcedureName) {

		this.sangerProcedureName = sangerProcedureName;
	}


	public Integer getSangerProcedureId() {

		return sangerProcedureId;
	}


	public void setSangerProcedureId(Integer sangerProcedureId) {

		this.sangerProcedureId = sangerProcedureId;
	}


	public String getGenotype() {

		return genotype;
	}


	public void setGenotype(String genotype) {

		this.genotype = genotype;

	}


	public void setMouseId(Integer mouseId) {

		this.mouseId = mouseId;
	}


	public Integer getMouseId() {

		return mouseId;
	}


	public void setSex(String sex) {

		this.sex = sex;

	}


	public String getSex() {

		return sex;
	}


	public void setColonyId(int colonyId) {

		this.colonyId = colonyId;

	}


	public Integer getColonyId() {

		return colonyId;
	}


	public void addMarkerSymbol(String markerSymbol) {

		if (this.markerSymbol == null) {
			this.markerSymbol = new ArrayList<>();
		}
		this.markerSymbol.add(markerSymbol);

	}


	public void addMarkerName(String markerName) {

		if (this.markerName == null) {
			this.markerName = new ArrayList<>();
		}
		this.markerName.add(markerName);
	}


	public void addMarkerSynonym(List<String> markerSynonym) {

		if (this.markerSynonym == null) {
			this.markerSynonym = new ArrayList<>();
		}
		this.markerSynonym.addAll(markerSynonym);
	}


	public void addMarkerType(String markerType) {

		if (this.markerType == null) {
			this.markerType = new ArrayList<>();
		}
		this.markerType.add(markerType);
	}


	public void addHumanGeneSymbol(List<String> humanGeneSymbol) {

		if (this.humanGeneSymbol == null) {
			this.humanGeneSymbol = new ArrayList<>();
		}
		this.humanGeneSymbol.addAll(humanGeneSymbol);

	}


	public void addStatus(String status) {

		if (this.status == null) {
			this.status = new ArrayList<>();
		}
		this.status.add(status);

	}


	public void addImitsPhenotypeStarted(String imitsPhenotypeStarted) {

		if (this.imitsPhenotypeStarted == null) {
			this.imitsPhenotypeStarted = new ArrayList<>();
		}
		this.imitsPhenotypeStarted.add(imitsPhenotypeStarted);
	}


	public void addImitsPhenotypeComplete(String imitsPhenotypeComplete) {

		if (this.imitsPhenotypeComplete == null) {
			this.imitsPhenotypeComplete = new ArrayList<>();
		}
		this.imitsPhenotypeComplete.add(imitsPhenotypeComplete);
	}


	public void addImitsPhenotypeStatus(String imitsPhenotypeStatus) {

		if (this.imitsPhenotypeStatus == null) {
			this.imitsPhenotypeStatus = new ArrayList<>();
		}
		this.imitsPhenotypeStatus.add(imitsPhenotypeStatus);

	}


	public void addMgiAccessionId(String mgiAccessionId) {

		if (this.mgiAccessionId == null) {
			this.mgiAccessionId = new ArrayList<>();
		}
		this.mgiAccessionId.add(mgiAccessionId);


	}

}
