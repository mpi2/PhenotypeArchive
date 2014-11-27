package uk.ac.ebi.phenotype.service.dto;

import org.apache.solr.client.solrj.beans.Field;

import java.util.List;
import java.util.Set;


/**
 * Created by jmason on 26/11/2014.
 */
public class DiseaseDTO {

	public static final String TYPE = "type";
	public static final String DATATYPE = "dataType";
	public static final String DISEASE_ID = "disease_id";
	public static final String DISEASE_SOURCE = "disease_source";
	public static final String DISEASE_TERM = "disease_term";
	public static final String DISEASE_ALTS = "disease_alts";
	public static final String DISEASE_CLASSES = "disease_classes";
	public static final String DISEASE_HUMAN_PHENOTYPES = "disease_human_phenotypes";
	public static final String HUMAN_CURATED = "human_curated";
	public static final String MOUSE_CURATED = "mouse_curated";
	public static final String MGI_PREDICTED = "mgi_predicted";
	public static final String IMPC_PREDICTED = "impc_predicted";
	public static final String MGI_PREDICTED_KNOWN_GENE = "mgi_predicted_known_gene";
	public static final String IMPC_PREDICTED_KNOWN_GENE = "impc_predicted_known_gene";
	public static final String MGI_NOVEL_PREDICTED_IN_LOCUS = "mgi_novel_predicted_in_locus";
	public static final String IMPC_NOVEL_PREDICTED_IN_LOCUS = "impc_novel_predicted_in_locus";

	public static final String MGI_ACCESSION_ID = "mgi_accession_id";
	public static final String MARKER_SYMBOL = "marker_symbol";
	public static final String MARKER_NAME = "marker_name";
	public static final String MARKER_SYNONYM = "marker_synonym";
	public static final String MARKER_TYPE = "marker_type";
	public static final String HUMAN_GENE_SYMBOL = "human_gene_symbol";
	public static final String STATUS = "status";
	public static final String IMITS_PHENOTYPE_STARTED = "imits_phenotype_started";
	public static final String IMITS_PHENOTYPE_COMPLETE = "imits_phenotype_complete";
	public static final String IMITS_PHENOTYPE_STATUS = "imits_phenotype_status";
	public static final String LATEST_PRODUCTION_CENTRE = "latest_production_centre";
	public static final String LATEST_PHENOTYPING_CENTRE = "latest_phenotyping_centre";
	public static final String LATEST_PHENOTYPE_STATUS = "latest_phenotype_status";
	public static final String LEGACY_PHENOTYPE_STATUS = "legacy_phenotype_status";
	public static final String ALLELE_NAME = "allele_name";
	public static final String MP_ID = "mp_id";
	public static final String MP_TERM = "mp_term";
	public static final String MP_TERM_DEFINITION = "mp_term_definition";
	public static final String MP_TERM_SYNONYM = "mp_term_synonym";
	public static final String ONTOLOGY_SUBSET = "ontology_subset";
	public static final String HP_ID = "hp_id";
	public static final String HP_TERM = "hp_term";
	public static final String TOP_LEVEL_MP_ID = "top_level_mp_id";
	public static final String TOP_LEVEL_MP_TERM = "top_level_mp_term";
	public static final String TOP_LEVEL_MP_TERM_SYNONYM = "top_level_mp_term_synonym";
	public static final String INTERMEDIATE_MP_ID = "intermediate_mp_id";
	public static final String INTERMEDIATE_MP_TERM = "intermediate_mp_term";
	public static final String INTERMEDIATE_MP_TERM_SYNONYM = "intermediate_mp_term_synonym";
	public static final String CHILD_MP_ID = "child_mp_id";
	public static final String CHILD_MP_TERM = "child_mp_term";
	public static final String CHILD_MP_TERM_SYNONYM = "child_mp_term_synonym";

	@Field(TYPE)
	private String type;

	@Field(DATATYPE)
	private String dataType;

	@Field(DISEASE_ID)
	private String diseaseId;

	@Field(DISEASE_SOURCE)
	private String diseaseSource;

	@Field(DISEASE_TERM)
	private String diseaseTerm;

	@Field(DISEASE_ALTS)
	private List<String> diseaseAlts;

	@Field(DISEASE_CLASSES)
	private List<String> diseaseClasses;

	@Field(DISEASE_HUMAN_PHENOTYPES)
	private List<String> diseaseHumanPhenotypes;

	@Field(HUMAN_CURATED)
	private Boolean humanCurated;

	@Field(MOUSE_CURATED)
	private Boolean mouseCurated;

	@Field(MGI_PREDICTED)
	private Boolean mgiPredicted;

	@Field(IMPC_PREDICTED)
	private Boolean impcPredicted;

	@Field(MGI_PREDICTED_KNOWN_GENE)
	private Boolean mgiPredictedKnownGene;

	@Field(IMPC_PREDICTED_KNOWN_GENE)
	private Boolean impcPredictedKnownGene;

	@Field(MGI_NOVEL_PREDICTED_IN_LOCUS)
	private Boolean mgiNovelPredictedInLocus;

	@Field(IMPC_NOVEL_PREDICTED_IN_LOCUS)
	private Boolean impcNovelPredictedInLocus;

	@Field(MGI_ACCESSION_ID)
	private Set<String> mgiAccessionId;

	@Field(MARKER_SYMBOL)
	private Set<String> markerSymbol;

	@Field(MARKER_NAME)
	private Set<String> markerName;

	@Field(MARKER_SYNONYM)
	private Set<String> markerSynonym;

	@Field(MARKER_TYPE)
	private Set<String> markerType;

	@Field(HUMAN_GENE_SYMBOL)
	private Set<String> humanGeneSymbol;

	@Field(STATUS)
	private Set<String> status;

	@Field(IMITS_PHENOTYPE_STARTED)
	private Set<String> imitsPhenotypeStarted;

	@Field(IMITS_PHENOTYPE_COMPLETE)
	private Set<String> imitsPhenotypeComplete;

	@Field(IMITS_PHENOTYPE_STATUS)
	private Set<String> imitsPhenotypeStatus;

	@Field(LATEST_PRODUCTION_CENTRE)
	private Set<String> latestProductionCentre;

	@Field(LATEST_PHENOTYPING_CENTRE)
	private Set<String> latestPhenotypingCentre;

	@Field(LATEST_PHENOTYPE_STATUS)
	private Set<String> latestPhenotypeStatus;

	@Field(LEGACY_PHENOTYPE_STATUS)
	private Set<Integer> legacyPhenotypeStatus;

	@Field(ALLELE_NAME)
	private Set<String> alleleName;

	@Field(MP_ID)
	private Set<String> mpId;

	@Field(MP_TERM)
	private Set<String> mpTerm;

	@Field(MP_TERM_DEFINITION)
	private Set<String> mpTermDefinition;

	@Field(MP_TERM_SYNONYM)
	private Set<String> mpTermSynonym;

	@Field(ONTOLOGY_SUBSET)
	private Set<String> ontologySubset;

	@Field(HP_ID)
	private Set<String> hpId;

	@Field(HP_TERM)
	private Set<String> hpTerm;

	@Field(TOP_LEVEL_MP_ID)
	private Set<String> topLevelMpId;

	@Field(TOP_LEVEL_MP_TERM)
	private Set<String> topLevelMpTerm;

	@Field(TOP_LEVEL_MP_TERM_SYNONYM)
	private Set<String> topLevelMpTermSynonym;

	@Field(INTERMEDIATE_MP_ID)
	private Set<String> intermediateMpId;

	@Field(INTERMEDIATE_MP_TERM)
	private Set<String> intermediateMpTerm;

	@Field(INTERMEDIATE_MP_TERM_SYNONYM)
	private Set<String> intermediateMpTermSynonym;

	@Field(CHILD_MP_ID)
	private Set<String> childMpId;

	@Field(CHILD_MP_TERM)
	private Set<String> childMpTerm;

	@Field(CHILD_MP_TERM_SYNONYM)
	private Set<String> childMpTermSynonym;


	public String getType() {

		return type;
	}


	public void setType(String type) {

		this.type = type;
	}


	public String getDataType() {

		return dataType;
	}


	public void setDataType(String dataType) {

		this.dataType = dataType;
	}


	public String getDiseaseId() {

		return diseaseId;
	}


	public void setDiseaseId(String diseaseId) {

		this.diseaseId = diseaseId;
	}


	public String getDiseaseSource() {

		return diseaseSource;
	}


	public void setDiseaseSource(String diseaseSource) {

		this.diseaseSource = diseaseSource;
	}


	public String getDiseaseTerm() {

		return diseaseTerm;
	}


	public void setDiseaseTerm(String diseaseTerm) {

		this.diseaseTerm = diseaseTerm;
	}


	public List<String> getDiseaseAlts() {

		return diseaseAlts;
	}


	public void setDiseaseAlts(List<String> diseaseAlts) {

		this.diseaseAlts = diseaseAlts;
	}


	public List<String> getDiseaseClasses() {

		return diseaseClasses;
	}


	public void setDiseaseClasses(List<String> diseaseClasses) {

		this.diseaseClasses = diseaseClasses;
	}


	public List<String> getDiseaseHumanPhenotypes() {

		return diseaseHumanPhenotypes;
	}


	public void setDiseaseHumanPhenotypes(List<String> diseaseHumanPhenotypes) {

		this.diseaseHumanPhenotypes = diseaseHumanPhenotypes;
	}


	public Boolean getHumanCurated() {

		return humanCurated;
	}


	public void setHumanCurated(Boolean humanCurated) {

		this.humanCurated = humanCurated;
	}


	public Boolean getMouseCurated() {

		return mouseCurated;
	}


	public void setMouseCurated(Boolean mouseCurated) {

		this.mouseCurated = mouseCurated;
	}


	public Boolean getMgiPredicted() {

		return mgiPredicted;
	}


	public void setMgiPredicted(Boolean mgiPredicted) {

		this.mgiPredicted = mgiPredicted;
	}


	public Boolean getImpcPredicted() {

		return impcPredicted;
	}


	public void setImpcPredicted(Boolean impcPredicted) {

		this.impcPredicted = impcPredicted;
	}


	public Boolean getMgiPredictedKnownGene() {

		return mgiPredictedKnownGene;
	}


	public void setMgiPredictedKnownGene(Boolean mgiPredictedKnownGene) {

		this.mgiPredictedKnownGene = mgiPredictedKnownGene;
	}


	public Boolean getImpcPredictedKnownGene() {

		return impcPredictedKnownGene;
	}


	public void setImpcPredictedKnownGene(Boolean impcPredictedKnownGene) {

		this.impcPredictedKnownGene = impcPredictedKnownGene;
	}


	public Boolean getMgiNovelPredictedInLocus() {

		return mgiNovelPredictedInLocus;
	}


	public void setMgiNovelPredictedInLocus(Boolean mgiNovelPredictedInLocus) {

		this.mgiNovelPredictedInLocus = mgiNovelPredictedInLocus;
	}


	public Boolean getImpcNovelPredictedInLocus() {

		return impcNovelPredictedInLocus;
	}


	public void setImpcNovelPredictedInLocus(Boolean impcNovelPredictedInLocus) {

		this.impcNovelPredictedInLocus = impcNovelPredictedInLocus;
	}


	public Set<String> getMgiAccessionId() {

		return mgiAccessionId;
	}


	public void setMgiAccessionId(Set<String> mgiAccessionId) {

		this.mgiAccessionId = mgiAccessionId;
	}


	public Set<String> getMarkerSymbol() {

		return markerSymbol;
	}


	public void setMarkerSymbol(Set<String> markerSymbol) {

		this.markerSymbol = markerSymbol;
	}


	public Set<String> getMarkerName() {

		return markerName;
	}


	public void setMarkerName(Set<String> markerName) {

		this.markerName = markerName;
	}


	public Set<String> getMarkerSynonym() {

		return markerSynonym;
	}


	public void setMarkerSynonym(Set<String> markerSynonym) {

		this.markerSynonym = markerSynonym;
	}


	public Set<String> getMarkerType() {

		return markerType;
	}


	public void setMarkerType(Set<String> markerType) {

		this.markerType = markerType;
	}


	public Set<String> getHumanGeneSymbol() {

		return humanGeneSymbol;
	}


	public void setHumanGeneSymbol(Set<String> humanGeneSymbol) {

		this.humanGeneSymbol = humanGeneSymbol;
	}


	public Set<String> getStatus() {

		return status;
	}


	public void setStatus(Set<String> status) {

		this.status = status;
	}


	public Set<String> getImitsPhenotypeStarted() {

		return imitsPhenotypeStarted;
	}


	public void setImitsPhenotypeStarted(Set<String> imitsPhenotypeStarted) {

		this.imitsPhenotypeStarted = imitsPhenotypeStarted;
	}


	public Set<String> getImitsPhenotypeComplete() {

		return imitsPhenotypeComplete;
	}


	public void setImitsPhenotypeComplete(Set<String> imitsPhenotypeComplete) {

		this.imitsPhenotypeComplete = imitsPhenotypeComplete;
	}


	public Set<String> getImitsPhenotypeStatus() {

		return imitsPhenotypeStatus;
	}


	public void setImitsPhenotypeStatus(Set<String> imitsPhenotypeStatus) {

		this.imitsPhenotypeStatus = imitsPhenotypeStatus;
	}


	public Set<String> getLatestProductionCentre() {

		return latestProductionCentre;
	}


	public void setLatestProductionCentre(Set<String> latestProductionCentre) {

		this.latestProductionCentre = latestProductionCentre;
	}


	public Set<String> getLatestPhenotypingCentre() {

		return latestPhenotypingCentre;
	}


	public void setLatestPhenotypingCentre(Set<String> latestPhenotypingCentre) {

		this.latestPhenotypingCentre = latestPhenotypingCentre;
	}


	public Set<String> getLatestPhenotypeStatus() {

		return latestPhenotypeStatus;
	}


	public void setLatestPhenotypeStatus(Set<String> latestPhenotypeStatus) {

		this.latestPhenotypeStatus = latestPhenotypeStatus;
	}


	public Set<Integer> getLegacyPhenotypeStatus() {

		return legacyPhenotypeStatus;
	}


	public void setLegacyPhenotypeStatus(Set<Integer> legacyPhenotypeStatus) {

		this.legacyPhenotypeStatus = legacyPhenotypeStatus;
	}


	public Set<String> getAlleleName() {

		return alleleName;
	}


	public void setAlleleName(Set<String> alleleName) {

		this.alleleName = alleleName;
	}


	public Set<String> getMpId() {

		return mpId;
	}


	public void setMpId(Set<String> mpId) {

		this.mpId = mpId;
	}


	public Set<String> getMpTerm() {

		return mpTerm;
	}


	public void setMpTerm(Set<String> mpTerm) {

		this.mpTerm = mpTerm;
	}


	public Set<String> getMpTermDefinition() {

		return mpTermDefinition;
	}


	public void setMpTermDefinition(Set<String> mpTermDefinition) {

		this.mpTermDefinition = mpTermDefinition;
	}


	public Set<String> getMpTermSynonym() {

		return mpTermSynonym;
	}


	public void setMpTermSynonym(Set<String> mpTermSynonym) {

		this.mpTermSynonym = mpTermSynonym;
	}


	public Set<String> getOntologySubset() {

		return ontologySubset;
	}


	public void setOntologySubset(Set<String> ontologySubset) {

		this.ontologySubset = ontologySubset;
	}


	public Set<String> getHpId() {

		return hpId;
	}


	public void setHpId(Set<String> hpId) {

		this.hpId = hpId;
	}


	public Set<String> getHpTerm() {

		return hpTerm;
	}


	public void setHpTerm(Set<String> hpTerm) {

		this.hpTerm = hpTerm;
	}


	public Set<String> getTopLevelMpId() {

		return topLevelMpId;
	}


	public void setTopLevelMpId(Set<String> topLevelMpId) {

		this.topLevelMpId = topLevelMpId;
	}


	public Set<String> getTopLevelMpTerm() {

		return topLevelMpTerm;
	}


	public void setTopLevelMpTerm(Set<String> topLevelMpTerm) {

		this.topLevelMpTerm = topLevelMpTerm;
	}


	public Set<String> getTopLevelMpTermSynonym() {

		return topLevelMpTermSynonym;
	}


	public void setTopLevelMpTermSynonym(Set<String> topLevelMpTermSynonym) {

		this.topLevelMpTermSynonym = topLevelMpTermSynonym;
	}


	public Set<String> getIntermediateMpId() {

		return intermediateMpId;
	}


	public void setIntermediateMpId(Set<String> intermediateMpId) {

		this.intermediateMpId = intermediateMpId;
	}


	public Set<String> getIntermediateMpTerm() {

		return intermediateMpTerm;
	}


	public void setIntermediateMpTerm(Set<String> intermediateMpTerm) {

		this.intermediateMpTerm = intermediateMpTerm;
	}


	public Set<String> getIntermediateMpTermSynonym() {

		return intermediateMpTermSynonym;
	}


	public void setIntermediateMpTermSynonym(Set<String> intermediateMpTermSynonym) {

		this.intermediateMpTermSynonym = intermediateMpTermSynonym;
	}


	public Set<String> getChildMpId() {

		return childMpId;
	}


	public void setChildMpId(Set<String> childMpId) {

		this.childMpId = childMpId;
	}


	public Set<String> getChildMpTerm() {

		return childMpTerm;
	}


	public void setChildMpTerm(Set<String> childMpTerm) {

		this.childMpTerm = childMpTerm;
	}


	public Set<String> getChildMpTermSynonym() {

		return childMpTermSynonym;
	}


	public void setChildMpTermSynonym(Set<String> childMpTermSynonym) {

		this.childMpTermSynonym = childMpTermSynonym;
	}
}
