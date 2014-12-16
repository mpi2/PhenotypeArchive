/**
 * Copyright (c) 2014 Lemur Consulting Ltd.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.phenotype.service.dto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.solr.client.solrj.beans.Field;

/**
 * @author Matt Pearce
 *
 */
public class AlleleDTO {

	public static final String ALLELE_DATA_TYPE = "gene";

	public static final String MGI_ACCESSION_ID = "mgi_accession_id";
	public static final String DATA_TYPE = "dataType";
	public static final String MARKER_TYPE = "marker_type";
	public static final String MARKER_SYMBOL = "marker_symbol";
	public static final String MARKER_SYNONYM = "marker_synonym";
	public static final String MARKER_NAME = "marker_name";
	public static final String HUMAN_GENE_SYMBOL = "human_gene_symbol";
	public static final String GENE_LATEST_ES_CELL_STATUS = "gene_latest_es_cell_status";
	public static final String LATEST_ES_CELL_STATUS = "latest_es_cell_status";
	public static final String GENE_LATEST_MOUSE_STATUS = "gene_latest_mouse_status";
	public static final String LATEST_MOUSE_STATUS = "latest_mouse_status";
	public static final String LATEST_PROJECT_STATUS = "latest_project_status";
	public static final String LATEST_PRODUCTION_STATUS = "latest_production_status";
	public static final String IMITS_PHENOTYPE_STARTED = "imits_phenotype_started";
	public static final String IMITS_PHENOTYPE_COMPLETE = "imits_phenotype_complete";
	public static final String LATEST_PHENOTYPE_STATUS = "latest_phenotype_status";
	public static final String IMITS_PHENOTYPE_STATUS = "imits_phenotype_status";
	public static final String GF_ACC = "gf_acc";
	public static final String LATEST_PRODUCTION_CENTRE = "latest_production_centre";
	public static final String LATEST_PHENOTYPING_CENTRE = "latest_phenotyping_centre";
	public static final String ALLELE_NAME = "allele_name";
	public static final String IMITS_ES_CELL_STATUS = "imits_es_cell_status";
	public static final String ES_CELL_STATUS = "es_cell_status";
	public static final String LEGACY_PHENOTYPE_STATUS = "legacy_phenotype_status";
	public static final String IMITS_MOUSE_STATUS = "imits_mouse_status";
	public static final String MOUSE_STATUS = "mouse_status";
	public static final String PHENOTYPE_STATUS = "phenotype_status";
	public static final String PRODUCTION_CENTRE = "production_centre";
	public static final String PHENOTYPING_CENTRE = "phenotyping_centre";
	
	public static final String GOTERMID = "go_term_id";
	public static final String GOTERMNAME = "go_term_name";
	public static final String GOTERMDEF = "go_term_def";
	public static final String GOTERMEVID = "go_term_evid";
	
	public static final String DISEASE_ID = "disease_id";
	public static final String DISEASE_SOURCE = "disease_source";
	public static final String DISEASE_TERM = "disease_term";
	public static final String DISEASE_ALTS = "disease_alts";
	public static final String DISEASE_CLASSES = "disease_classes";
	public static final String HUMAN_CURATED = "human_curated";
	public static final String MOUSE_CURATED = "mouse_curated";
	public static final String MGI_PREDICTED = "mgi_predicted";
	public static final String IMPC_PREDICTED = "impc_predicted";
	public static final String MGI_PREDICTED_KNOWN_GENE = "mgi_predicted_known_gene";
	public static final String IMPC_PREDICTED_KNOWN_GENE = "impc_predicted_known_gene";
	public static final String MGI_NOVEL_PREDICTED_IN_LOCUS = "mgi_novel_predicted_in_locus";
	public static final String IMPC_NOVEL_PREDICTED_IN_LOCUS = "impc_novel_predicted_in_locus";
	public static final String DISEASE_HUMAN_PHENOTYPES = "disease_human_phenotypes";
	public static final String STATUS="status";
	public static final String TYPE="type";

	
	@Field(STATUS)
	private String status;
	@Field(TYPE)
	private List<String> type;
	
	
	
	public List<String> getType() {
	
		return type;
	}



	
	public void setType(List<String> type) {
	
		this.type = type;
	}



	public String getStatus() {
	
		return status;
	}


	
	public void setStatus(String status) {
	
		this.status = status;
	}

	@Field(MGI_ACCESSION_ID)
	private String mgiAccessionId;
	@Field(DATA_TYPE)
	private String dataType;
	@Field(MARKER_TYPE)
	private String markerType;
	@Field(MARKER_SYMBOL)
	private String markerSymbol;

	@Field(MARKER_SYNONYM)
	private List<String> markerSynonym;
	@Field(MARKER_NAME)
	private String markerName;

	@Field(HUMAN_GENE_SYMBOL)
	private List<String> humanGeneSymbol;

	@Field(GENE_LATEST_ES_CELL_STATUS)
	private String geneLatestEsCellStatus;
	@Field(LATEST_ES_CELL_STATUS)
	private String latestEsCellStatus;
	@Field(GENE_LATEST_MOUSE_STATUS)
	private String geneLatestMouseStatus;
	@Field(LATEST_MOUSE_STATUS)
	private String latestMouseStatus;
	@Field(LATEST_PROJECT_STATUS)
	private String latestProjectStatus;
	@Field(LATEST_PRODUCTION_STATUS)
	private String latestProductionStatus;
	@Field(IMITS_PHENOTYPE_STARTED)
	private String imitsPhenotypeStarted;
	@Field(IMITS_PHENOTYPE_COMPLETE)
	private String imitsPhenotypeComplete;
	@Field(LATEST_PHENOTYPE_STATUS)
	private String latestPhenotypeStatus;
	@Field(IMITS_PHENOTYPE_STATUS)
	private String imitsPhenotypeStatus;
	@Field(LEGACY_PHENOTYPE_STATUS)
	private Integer legacyPhenotypeStatus;


	@Field(GF_ACC)
	private String gfAcc;

	@Field(LATEST_PHENOTYPING_CENTRE)
	private List<String> latestPhenotypingCentre;
	@Field(LATEST_PRODUCTION_CENTRE)
	private List<String> latestProductionCentre;

	@Field(ALLELE_NAME)
	private List<String> alleleName = new ArrayList<>();
	@Field(IMITS_ES_CELL_STATUS)
	private String imitsEsCellStatus;
	@Field(ES_CELL_STATUS)
	private List<String> esCellStatus = new ArrayList<>();
	@Field(IMITS_MOUSE_STATUS)
	private String imitsMouseStatus;
	@Field(MOUSE_STATUS)
	private List<String> mouseStatus = new ArrayList<>();
	@Field(PHENOTYPE_STATUS)
	private List<String> phenotypeStatus = new ArrayList<>();
	@Field(PRODUCTION_CENTRE)
	private List<String> productionCentre = new ArrayList<>();
	@Field(PHENOTYPING_CENTRE)
	private List<String> phenotypingCentre = new ArrayList<>();

	
	@Field(GOTERMID)
	private List<String> goTermIds = new ArrayList<>();
	
	@Field(GOTERMNAME)
	private List<String> goTermNames = new ArrayList<>();
	
	@Field(GOTERMDEF)
	private List<String> goTermDefs = new ArrayList<>();
	
	@Field(GOTERMEVID)
	private List<String> goTermEvids = new ArrayList<>();
	
	
	@Field(DISEASE_ID)
	private List<String> diseaseId = new ArrayList<>();
	@Field(DISEASE_SOURCE)
	private List<String> diseaseSource = new ArrayList<>();
	@Field(DISEASE_TERM)
	private List<String> diseaseTerm = new ArrayList<>();
	@Field(DISEASE_ALTS)
	private List<String> diseaseAlts = new ArrayList<>();
	@Field(DISEASE_CLASSES)
	private List<String> diseaseClasses = new ArrayList<>();
	@Field(HUMAN_CURATED)
	private List<Boolean> humanCurated = new ArrayList<>();
	@Field(MOUSE_CURATED)
	private List<Boolean> mouseCurated = new ArrayList<>();
	@Field(MGI_PREDICTED)
	private List<Boolean> mgiPredicted = new ArrayList<>();
	@Field(IMPC_PREDICTED)
	private List<Boolean> impcPredicted = new ArrayList<>();
	@Field(MGI_PREDICTED_KNOWN_GENE)
	private List<Boolean> mgiPredictedKnownGene = new ArrayList<>();
	@Field(IMPC_PREDICTED_KNOWN_GENE)
	private List<Boolean> impcPredictedKnownGene = new ArrayList<>();
	@Field(MGI_NOVEL_PREDICTED_IN_LOCUS)
	private List<Boolean> mgiNovelPredictedInLocus = new ArrayList<>();
	@Field(IMPC_NOVEL_PREDICTED_IN_LOCUS)
	private List<Boolean> impcNovelPredictedInLocus = new ArrayList<>();
	@Field(DISEASE_HUMAN_PHENOTYPES)
	private List<String> diseaseHumanPhenotypes = new ArrayList<>();

	public Integer getLegacyPhenotypeStatus() {

		return legacyPhenotypeStatus;
	}


	public void setLegacyPhenotypeStatus(Integer legacyPhenotypeStatus) {

		this.legacyPhenotypeStatus = legacyPhenotypeStatus;
	}



	/**
	 * @return the mgiAccessionId
	 */
	public String getMgiAccessionId() {
		return mgiAccessionId;
	}

	/**
	 * @param mgiAccessionId
	 *            the mgiAccessionId to set
	 */
	public void setMgiAccessionId(String mgiAccessionId) {
		this.mgiAccessionId = mgiAccessionId;
	}

	/**
	 * @return the dataType
	 */
	public String getDataType() {
		return dataType;
	}

	/**
	 * @param dataType
	 *            the dataType to set
	 */
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	/**
	 * @return the markerType
	 */
	public String getMarkerType() {
		return markerType;
	}

	/**
	 * @param markerType
	 *            the markerType to set
	 */
	public void setMarkerType(String markerType) {
		this.markerType = markerType;
	}

	/**
	 * @return the markerSymbol
	 */
	public String getMarkerSymbol() {
		return markerSymbol;
	}

	/**
	 * @param markerSymbol
	 *            the markerSymbol to set
	 */
	public void setMarkerSymbol(String markerSymbol) {
		this.markerSymbol = markerSymbol;
	}

	/**
	 * @return the markerSynonym
	 */
	public List<String> getMarkerSynonym() {
		return markerSynonym;
	}

	/**
	 * @param markerSynonym
	 *            the markerSynonym to set
	 */
	public void setMarkerSynonym(List<String> markerSynonym) {
		this.markerSynonym = markerSynonym;
	}

	/**
	 * @return the markerName
	 */
	public String getMarkerName() {
		return markerName;
	}

	/**
	 * @param markerName
	 *            the markerName to set
	 */
	public void setMarkerName(String markerName) {
		this.markerName = markerName;
	}

	/**
	 * @return the humanGeneSymbol
	 */
	public List<String> getHumanGeneSymbol() {
		return humanGeneSymbol;
	}

	/**
	 * @param humanGeneSymbol
	 *            the humanGeneSymbol to set
	 */
	public void setHumanGeneSymbol(List<String> humanGeneSymbol) {
		this.humanGeneSymbol = humanGeneSymbol;
	}

	/**
	 * @return the geneLatestEsCellStatus
	 */
	public String getGeneLatestEsCellStatus() {
		return geneLatestEsCellStatus;
	}

	/**
	 * @param geneLatestEsCellStatus
	 *            the geneLatestEsCellStatus to set
	 */
	public void setGeneLatestEsCellStatus(String geneLatestEsCellStatus) {
		this.geneLatestEsCellStatus = geneLatestEsCellStatus;
	}

	/**
	 * @return the latestEsCellStatus
	 */
	public String getLatestEsCellStatus() {
		return latestEsCellStatus;
	}

	/**
	 * @param latestEsCellStatus
	 *            the latestEsCellStatus to set
	 */
	public void setLatestEsCellStatus(String latestEsCellStatus) {
		this.latestEsCellStatus = latestEsCellStatus;
	}

	/**
	 * @return the geneLatestMouseStatus
	 */
	public String getGeneLatestMouseStatus() {
		return geneLatestMouseStatus;
	}

	/**
	 * @param geneLatestMouseStatus
	 *            the geneLatestMouseStatus to set
	 */
	public void setGeneLatestMouseStatus(String geneLatestMouseStatus) {
		this.geneLatestMouseStatus = geneLatestMouseStatus;
	}

	/**
	 * @return the latestMouseStatus
	 */
	public String getLatestMouseStatus() {
		return latestMouseStatus;
	}

	/**
	 * @param latestMouseStatus
	 *            the latestMouseStatus to set
	 */
	public void setLatestMouseStatus(String latestMouseStatus) {
		this.latestMouseStatus = latestMouseStatus;
	}

	/**
	 * @return the latestProjectStatus
	 */
	public String getLatestProjectStatus() {
		return latestProjectStatus;
	}

	/**
	 * @param latestProjectStatus
	 *            the latestProjectStatus to set
	 */
	public void setLatestProjectStatus(String latestProjectStatus) {
		this.latestProjectStatus = latestProjectStatus;
	}

	/**
	 * @return the latestProductionStatus
	 */
	public String getLatestProductionStatus() {
		return latestProductionStatus;
	}

	/**
	 * @param latestProductionStatus
	 *            the latestProductionStatus to set
	 */
	public void setLatestProductionStatus(String latestProductionStatus) {
		this.latestProductionStatus = latestProductionStatus;
	}

	/**
	 * @return the imitsPhenotypeStarted
	 */
	public String getImitsPhenotypeStarted() {
		return imitsPhenotypeStarted;
	}

	/**
	 * @param imitsPhenotypeStarted
	 *            the imitsPhenotypeStarted to set
	 */
	public void setImitsPhenotypeStarted(String imitsPhenotypeStarted) {
		this.imitsPhenotypeStarted = imitsPhenotypeStarted;
	}

	/**
	 * @return the imitsPhenotypeComplete
	 */
	public String getImitsPhenotypeComplete() {
		return imitsPhenotypeComplete;
	}

	/**
	 * @param imitsPhenotypeComplete
	 *            the imitsPhenotypeComplete to set
	 */
	public void setImitsPhenotypeComplete(String imitsPhenotypeComplete) {
		this.imitsPhenotypeComplete = imitsPhenotypeComplete;
	}

	/**
	 * @return the latestPhenotypeStatus
	 */
	public String getLatestPhenotypeStatus() {
		return latestPhenotypeStatus;
	}

	/**
	 * @param latestPhenotypeStatus
	 *            the latestPhenotypeStatus to set
	 */
	public void setLatestPhenotypeStatus(String latestPhenotypeStatus) {
		this.latestPhenotypeStatus = latestPhenotypeStatus;
	}

	/**
	 * @return the imitsPhenotypeStatus
	 */
	public String getImitsPhenotypeStatus() {
		return imitsPhenotypeStatus;
	}

	/**
	 * @param imitsPhenotypeStatus
	 *            the imitsPhenotypeStatus to set
	 */
	public void setImitsPhenotypeStatus(String imitsPhenotypeStatus) {
		this.imitsPhenotypeStatus = imitsPhenotypeStatus;
	}

	/**
	 * @return the gfAcc
	 */
	public String getGfAcc() {
		return gfAcc;
	}

	/**
	 * @param gfAcc
	 *            the gfAcc to set
	 */
	public void setGfAcc(String gfAcc) {
		this.gfAcc = gfAcc;
	}

	/**
	 * @return the alleleName
	 */
	public List<String> getAlleleName() {
		return alleleName;
	}

	/**
	 * @param alleleName
	 *            the alleleName to set
	 */
	public void setAlleleName(List<String> alleleName) {
		this.alleleName = alleleName;
	}

	/**
	 * @return the imitsEsCellStatus
	 */
	public String getImitsEsCellStatus() {
		return imitsEsCellStatus;
	}

	/**
	 * @param imitsEsCellStatus
	 *            the imitsEsCellStatus to set
	 */
	public void setImitsEsCellStatus(String imitsEsCellStatus) {
		this.imitsEsCellStatus = imitsEsCellStatus;
	}

	/**
	 * @return the esCellStatus
	 */
	public List<String> getEsCellStatus() {
		return esCellStatus;
	}

	/**
	 * @param esCellStatus
	 *            the esCellStatus to set
	 */
	public void setEsCellStatus(List<String> esCellStatus) {
		this.esCellStatus = esCellStatus;
	}

	/**
	 * @return the imitsMouseStatus
	 */
	public String getImitsMouseStatus() {
		return imitsMouseStatus;
	}

	/**
	 * @param imitsMouseStatus
	 *            the imitsMouseStatus to set
	 */
	public void setImitsMouseStatus(String imitsMouseStatus) {
		this.imitsMouseStatus = imitsMouseStatus;
	}

	/**
	 * @return the mouseStatus
	 */
	public List<String> getMouseStatus() {
		return mouseStatus;
	}

	/**
	 * @param mouseStatus
	 *            the mouseStatus to set
	 */
	public void setMouseStatus(List<String> mouseStatus) {
		this.mouseStatus = mouseStatus;
	}

	/**
	 * @return the phenotypeStatus
	 */
	public List<String> getPhenotypeStatus() {
		return phenotypeStatus;
	}

	/**
	 * @param phenotypeStatus
	 *            the phenotypeStatus to set
	 */
	public void setPhenotypeStatus(List<String> phenotypeStatus) {
		this.phenotypeStatus = phenotypeStatus;
	}

	/**
	 * @return the productionCentre
	 */
	public List<String> getProductionCentre() {
		return productionCentre;
	}

	/**
	 * @param productionCentre
	 *            the productionCentre to set
	 */
	public void setProductionCentre(List<String> productionCentre) {
		this.productionCentre = productionCentre;
	}

	/**
	 * @return the phenotypingCentre
	 */
	public List<String> getPhenotypingCentre() {
		return phenotypingCentre;
	}

	/**
	 * @param phenotypingCentre
	 *            the phenotypingCentre to set
	 */
	public void setPhenotypingCentre(List<String> phenotypingCentre) {
		this.phenotypingCentre = phenotypingCentre;
	}

	

	/**
	 * @return the goTermIds
	 */
	public List<String> getGoTermIds() {
		return goTermIds;
	}

	/**
	 * @param goTermIds
	 *            the goTermIds to set
	 */
	public void setGoTermIds(List<String> goTermIds) {
		this.goTermIds = goTermIds;
	}
	
	/**
	 * @return the goTermNames
	 */
	public List<String> getGoTermNames() {
		return goTermNames;
	}

	/**
	 * @param goTermNames
	 *            the goTermNames to set
	 */
	public void setGoTermNames(List<String> goTermNames) {
		this.goTermNames = goTermNames;
	}
	
	/**
	 * @return the goTermDefs
	 */
	public List<String> getGoTermDefs() {
		return goTermDefs;
	}

	/**
	 * @param goTermDefs
	 *            the goTermDefs to set
	 */
	public void setGoTermDefs(List<String> goTermDefs) {
		this.goTermDefs = goTermDefs;
	}
	
	
	/**
	 * @return the goTermEvids
	 */
	public List<String> getGoTermEvids() {
		return goTermEvids;
	}

	/**
	 * @param goTermEvids
	 *            the goTermEvids to set
	 */
	public void setGoTermEvids(List<String> goTermEvids) {
		this.goTermEvids = goTermEvids;
	}
	
	
	
	/**
	 * @return the diseaseId
	 */
	public List<String> getDiseaseId() {
		return diseaseId;
	}

	/**
	 * @param diseaseId
	 *            the diseaseId to set
	 */
	public void setDiseaseId(List<String> diseaseId) {
		this.diseaseId = diseaseId;
	}

	/**
	 * @return the diseaseSource
	 */
	public List<String> getDiseaseSource() {
		return diseaseSource;
	}

	/**
	 * @param diseaseSource
	 *            the diseaseSource to set
	 */
	public void setDiseaseSource(List<String> diseaseSource) {
		this.diseaseSource = diseaseSource;
	}

	/**
	 * @return the diseaseTerm
	 */
	public List<String> getDiseaseTerm() {
		return diseaseTerm;
	}

	/**
	 * @param diseaseTerm
	 *            the diseaseTerm to set
	 */
	public void setDiseaseTerm(List<String> diseaseTerm) {
		this.diseaseTerm = diseaseTerm;
	}

	/**
	 * @return the diseaseAlts
	 */
	public List<String> getDiseaseAlts() {
		return diseaseAlts;
	}

	/**
	 * @param diseaseAlts
	 *            the diseaseAlts to set
	 */
	public void setDiseaseAlts(List<String> diseaseAlts) {
		this.diseaseAlts = diseaseAlts;
	}

	/**
	 * @return the diseaseClasses
	 */
	public List<String> getDiseaseClasses() {
		return diseaseClasses;
	}

	/**
	 * @param diseaseClasses
	 *            the diseaseClasses to set
	 */
	public void setDiseaseClasses(List<String> diseaseClasses) {
		this.diseaseClasses = diseaseClasses;
	}

	/**
	 * @return the humanCurated
	 */
	public List<Boolean> getHumanCurated() {
		return humanCurated;
	}

	/**
	 * @param humanCurated
	 *            the humanCurated to set
	 */
	public void setHumanCurated(List<Boolean> humanCurated) {
		this.humanCurated = humanCurated;
	}

	/**
	 * @return the mouseCurated
	 */
	public List<Boolean> getMouseCurated() {
		return mouseCurated;
	}

	/**
	 * @param mouseCurated
	 *            the mouseCurated to set
	 */
	public void setMouseCurated(List<Boolean> mouseCurated) {
		this.mouseCurated = mouseCurated;
	}

	/**
	 * @return the mgiPredicted
	 */
	public List<Boolean> getMgiPredicted() {
		return mgiPredicted;
	}

	/**
	 * @param mgiPredicted
	 *            the mgiPredicted to set
	 */
	public void setMgiPredicted(List<Boolean> mgiPredicted) {
		this.mgiPredicted = mgiPredicted;
	}

	/**
	 * @return the impcPredicted
	 */
	public List<Boolean> getImpcPredicted() {
		return impcPredicted;
	}

	/**
	 * @param impcPredicted
	 *            the impcPredicted to set
	 */
	public void setImpcPredicted(List<Boolean> impcPredicted) {
		this.impcPredicted = impcPredicted;
	}

	/**
	 * @return the mgiPredictedKnownGene
	 */
	public List<Boolean> getMgiPredictedKnownGene() {
		return mgiPredictedKnownGene;
	}

	/**
	 * @param mgiPredictedKnownGene
	 *            the mgiPredictedKnownGene to set
	 */
	public void setMgiPredictedKnownGene(List<Boolean> mgiPredictedKnownGene) {
		this.mgiPredictedKnownGene = mgiPredictedKnownGene;
	}

	/**
	 * @return the impcPredictedKnownGene
	 */
	public List<Boolean> getImpcPredictedKnownGene() {
		return impcPredictedKnownGene;
	}

	/**
	 * @param impcPredictedKnownGene
	 *            the impcPredictedKnownGene to set
	 */
	public void setImpcPredictedKnownGene(List<Boolean> impcPredictedKnownGene) {
		this.impcPredictedKnownGene = impcPredictedKnownGene;
	}

	/**
	 * @return the mgiNovelPredictedInLocus
	 */
	public List<Boolean> getMgiNovelPredictedInLocus() {
		return mgiNovelPredictedInLocus;
	}

	/**
	 * @param mgiNovelPredictedInLocus
	 *            the mgiNovelPredictedInLocus to set
	 */
	public void setMgiNovelPredictedInLocus(List<Boolean> mgiNovelPredictedInLocus) {
		this.mgiNovelPredictedInLocus = mgiNovelPredictedInLocus;
	}

	/**
	 * @return the impcNovelPredictedInLocus
	 */
	public List<Boolean> getImpcNovelPredictedInLocus() {
		return impcNovelPredictedInLocus;
	}

	/**
	 * @param impcNovelPredictedInLocus
	 *            the impcNovelPredictedInLocus to set
	 */
	public void setImpcNovelPredictedInLocus(List<Boolean> impcNovelPredictedInLocus) {
		this.impcNovelPredictedInLocus = impcNovelPredictedInLocus;
	}

	/**
	 * @return the diseaseHumanPhenotypes
	 */
	public List<String> getDiseaseHumanPhenotypes() {
		return diseaseHumanPhenotypes;
	}

	/**
	 * @param diseaseHumanPhenotypes
	 *            the diseaseHumanPhenotypes to set
	 */
	public void setDiseaseHumanPhenotypes(List<String> diseaseHumanPhenotypes) {
		this.diseaseHumanPhenotypes = diseaseHumanPhenotypes;
	}

	/**
	 * @return the latestPhenotypingCentre
	 */
	public List<String> getLatestPhenotypingCentre() {
		return latestPhenotypingCentre;
	}

	/**
	 * @param latestPhenotypingCentre the latestPhenotypingCentre to set
	 */
	public void setLatestPhenotypingCentre(List<String> latestPhenotypingCentre) {
		this.latestPhenotypingCentre = latestPhenotypingCentre;
	}

	/**
	 * @param latestProductionCentre the latestProductionCentre to set
	 */
	public void setLatestProductionCentre(List<String> latestProductionCentre) {
		this.latestProductionCentre = latestProductionCentre;
	}

	/**
	 * @returnthe latestProductionCentre
	 */
	public List<String> getLatestProductionCentre() {
		return latestProductionCentre;
	}
	
	@Override
	public String toString() {

		return "AlleleDTO{" +
			"mgiAccessionId='" + mgiAccessionId + '\'' +
			", dataType='" + dataType + '\'' +
			", markerType='" + markerType + '\'' +
			", markerSymbol='" + markerSymbol + '\'' +
			", markerSynonym=" + markerSynonym +
			", markerName='" + markerName + '\'' +
			", humanGeneSymbol=" + humanGeneSymbol +
			", geneLatestEsCellStatus='" + geneLatestEsCellStatus + '\'' +
			", latestEsCellStatus='" + latestEsCellStatus + '\'' +
			", geneLatestMouseStatus='" + geneLatestMouseStatus + '\'' +
			", latestMouseStatus='" + latestMouseStatus + '\'' +
			", latestProjectStatus='" + latestProjectStatus + '\'' +
			", latestProductionStatus='" + latestProductionStatus + '\'' +
			", imitsPhenotypeStarted='" + imitsPhenotypeStarted + '\'' +
			", imitsPhenotypeComplete='" + imitsPhenotypeComplete + '\'' +
			", latestPhenotypeStatus='" + latestPhenotypeStatus + '\'' +
			", legacyPhenotypeStatus='" + legacyPhenotypeStatus + '\'' +
			", imitsPhenotypeStatus='" + imitsPhenotypeStatus + '\'' +
			", gfAcc='" + gfAcc + '\'' +
			", latestPhenotypingCentre=" + latestPhenotypingCentre +
			", latestProductionCentre=" + latestProductionCentre +
			", alleleName=" + alleleName +
			", imitsEsCellStatus='" + imitsEsCellStatus + '\'' +
			", esCellStatus=" + esCellStatus +
			", imitsMouseStatus='" + imitsMouseStatus + '\'' +
			", mouseStatus=" + mouseStatus +
			", phenotypeStatus=" + phenotypeStatus +
			", productionCentre=" + productionCentre +
			", phenotypingCentre=" + phenotypingCentre +
			", goTermIds=" + goTermIds + 
			", goTermNames" + goTermNames +
			", goTermDefs" + goTermDefs +
			", goTermEvids" + goTermEvids +
			", diseaseId=" + diseaseId +
			", diseaseSource=" + diseaseSource +
			", diseaseTerm=" + diseaseTerm +
			", diseaseAlts=" + diseaseAlts +
			", diseaseClasses=" + diseaseClasses +
			", humanCurated=" + humanCurated +
			", mouseCurated=" + mouseCurated +
			", mgiPredicted=" + mgiPredicted +
			", impcPredicted=" + impcPredicted +
			", mgiPredictedKnownGene=" + mgiPredictedKnownGene +
			", impcPredictedKnownGene=" + impcPredictedKnownGene +
			", mgiNovelPredictedInLocus=" + mgiNovelPredictedInLocus +
			", impcNovelPredictedInLocus=" + impcNovelPredictedInLocus +
			", diseaseHumanPhenotypes=" + diseaseHumanPhenotypes +
			'}';
	}
}
