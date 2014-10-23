package uk.ac.ebi.phenotype.service.dto;

import org.apache.solr.client.solrj.beans.Field;

import java.util.List;


/**
 * Created by jmason on 23/10/2014.
 */
public class MpDTO {

	public static final String MP_ID = "mp_id";
	public static final String MP_TERM = "mp_term";
	public static final String MP_DEFINITION = "mp_definition";
	public static final String MP_TERM_SYNONYM = "mp_term_synonym";
	public static final String MP_NODE_ID = "mp_node_id";
	public static final String CHILD_MP_ID = "child_mp_id";
	public static final String CHILD_MP_TERM = "child_mp_term";
	public static final String CHILD_MP_DEFINITION = "child_mp_definition";
	public static final String CHILD_MP_TERM_SYNONYM = "child_mp_term_synonym";
	public static final String PARENT_MP_ID = "parent_mp_id";
	public static final String PARENT_MP_TERM = "parent_mp_term";
	public static final String PARENT_MP_DEFINITION = "parent_mp_definition";
	public static final String PARENT_MP_TERM_SYNONYM = "parent_mp_term_synonym";
	public static final String INTERMEDIATE_MP_ID = "intermediate_mp_id";
	public static final String INTERMEDIATE_MP_TERM = "intermediate_mp_term";
	public static final String INTERMEDIATE_MP_DEFINITION = "intermediate_mp_definition";
	public static final String INTERMEDIATE_MP_TERM_SYNONYM = "intermediate_mp_term_synonym";
	public static final String TOP_LEVEL_MP_ID = "top_level_mp_id";
	public static final String TOP_LEVEL_MP_TERM = "top_level_mp_term";
	public static final String TOP_LEVEL_MP_DEFINITION = "top_level_mp_definition";
	public static final String TOP_LEVEL_MP_TERM_SYNONYM = "top_level_mp_term_synonym";
	public static final String TOP_LEVEL_MP_TERM_ID = "top_level_mp_term_id";
	public static final String HP_ID = "hp_id";
	public static final String HP_TERM = "hp_term";
	public static final String DATA_TYPE = "dataType";
	public static final String ONTOLOGY_SUBSET = "ontology_subset";
	public static final String MA_ID = "ma_id";
	public static final String MA_TERM = "ma_term";
	public static final String MA_TERM_SYNONYM = "ma_term_synonym";
	public static final String SELECTED_TOP_LEVEL_MA_ID = "selected_top_level_ma_id";
	public static final String SELECTED_TOP_LEVEL_MA_TERM = "selected_top_level_ma_term";
	public static final String SELECTED_TOP_LEVEL_MA_TERM_SYNONYM = "selected_top_level_ma_term_synonym";
	public static final String CHILD_MA_ID = "child_ma_id";
	public static final String CHILD_MA_TERM = "child_ma_term";
	public static final String CHILD_MA_TERM_SYNONYM = "child_ma_term_synonym";
	public static final String INFERRED_MA_ID = "inferred_ma_id";
	public static final String INFERRED_MA_TERM = "inferred_ma_term";
	public static final String INFERRED_MA_TERM_SYNONYM = "inferred_ma_term_synonym";
	public static final String INFERRED_SELECTED_TOP_LEVEL_MA_ID = "inferred_selected_top_level_ma_id";
	public static final String INFERRED_SELECTED_TOP_LEVEL_MA_TERM = "inferred_selected_top_level_ma_term";
	public static final String INFERRED_SELECTED_TOP_LEVEL_MA_TERM_SYNONYM = "inferred_selected_top_level_ma_term_synonym";
	public static final String INFERRED_CHILD_MA_ID = "inferred_child_ma_id";
	public static final String INFERRED_CHILD_MA_TERM = "inferred_child_ma_term";
	public static final String INFERRED_CHILD_MA_TERM_SYNONYM = "inferred_child_ma_term_synonym";
	public static final String GO_ID = "go_id";
	public static final String SIBLING_MP_ID = "sibling_mp_id";
	public static final String SIBLING_MP_TERM = "sibling_mp_term";
	public static final String SIBLING_MP_DEFINITION = "sibling_mp_definition";
	public static final String P_VALUE = "p_value";
	public static final String MGI_ACCESSION_ID = "mgi_accession_id";
	public static final String MARKER_SYMBOL = "marker_symbol";
	public static final String MARKER_ACCESSION_ID = "marker_accession_id";
	public static final String PREQC_GENE_ID = "preqc_gene_id";
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
	public static final String TYPE = "type";
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
	public static final String ANNOTATION_TERM_ID = "annotationTermId";
	public static final String ANNOTATION_TERM_NAME = "annotationTermName";
	public static final String NAME = "name";
	public static final String ACCESSION = "accession";
	public static final String EXP_NAME = "expName";
	public static final String LARGE_THUMBNAIL_FILE_PATH = "largeThumbnailFilePath";
	public static final String SMALL_THUMBNAIL_FILE_PATH = "smallThumbnailFilePath";
	public static final String INFERRED_MA_TERM_ID = "inferredMaTermId";
	public static final String INFERRED_MA_TERM_NAME = "inferredMaTermName";
	public static final String ANNOTATED_HIGHER_LEVEL_MA_TERM_ID = "annotatedHigherLevelMaTermId";
	public static final String ANNOTATED_HIGHER_LEVEL_MA_TERM_NAME = "annotatedHigherLevelMaTermName";
	public static final String ANNOTATED_HIGHER_LEVEL_MP_TERM_ID = "annotatedHigherLevelMpTermId";
	public static final String ANNOTATED_HIGHER_LEVEL_MP_TERM_NAME = "annotatedHigherLevelMpTermName";
	public static final String ANNOTATED_OR_INFERRED_HIGHER_LEVEL_MA_TERM_NAME = "annotated_or_inferred_higherLevelMaTermName";
	public static final String ANNOTATED_OR_INFERRED_HIGHER_LEVEL_MA_TERM_ID = "annotated_or_inferred_higherLevelMaTermId";
	public static final String SYMBOL = "symbol";
	public static final String SANGER_SYMBOL = "sangerSymbol";
	public static final String GENE_NAME = "geneName";
	public static final String SUBTYPE = "subtype";
	public static final String GENE_SYNONYMS = "geneSynonyms";

	public static final String MA_TERM_ID = "maTermId";
	public static final String MA_TERM_NAME = "maTermName";
	public static final String MP_TERM_ID = "mpTermId";
	public static final String MP_TERM_NAME = "mpTermName";
	public static final String EXP_NAME_EXP = "expName_exp";
	public static final String SYMBOL_GENE = "symbol_gene";
	public static final String TOP_LEVEL = "topLevel";
	public static final String ALLELE_SYMBOL = "allele_symbol";
	public static final String ALLELE_ID = "allele_id";
	public static final String STRAIN_NAME = "strain_name";
	public static final String STRAIN_ID = "strain_id";
	public static final String PIPELINE_NAME = "pipeline_name";
	public static final String PIPELINE_STABLE_ID = "pipeline_stable_id";
	public static final String PIPELINE_STABLE_KEY = "pipeline_stable_key";
	public static final String PROCEDURE_NAME = "procedure_name";
	public static final String PROCEDURE_STABLE_ID = "procedure_stable_id";
	public static final String PROCEDURE_STABLE_KEY = "procedure_stable_key";
	public static final String PARAMETER_NAME = "parameter_name";
	public static final String PARAMETER_STABLE_ID = "parameter_stable_id";
	public static final String PARAMETER_STABLE_KEY = "parameter_stable_key";





	@Field(MP_ID)
	private String mpId;

	@Field(MP_TERM)
	private String mpTerm;

	@Field(MP_DEFINITION)
	private String mpDefinition;

	@Field(MP_TERM_SYNONYM)
	private List<String> mpTermSynonym;

	@Field(MP_NODE_ID)
	private List<Integer> mpNodeId;

	@Field(CHILD_MP_ID)
	private List<String> childMpId;

	@Field(CHILD_MP_TERM)
	private List<String> childMpTerm;

	@Field(CHILD_MP_DEFINITION)
	private List<String> childMpDefinition;

	@Field(CHILD_MP_TERM_SYNONYM)
	private List<String> childMpTermSynonym;

	@Field(PARENT_MP_ID)
	private List<String> parentMpId;

	@Field(PARENT_MP_TERM)
	private List<String> parentMpTerm;

	@Field(PARENT_MP_DEFINITION)
	private List<String> parentMpDefinition;

	@Field(PARENT_MP_TERM_SYNONYM)
	private List<String> parentMpTermSynonym;

	@Field(INTERMEDIATE_MP_ID)
	private List<String> intermediateMpId;

	@Field(INTERMEDIATE_MP_TERM)
	private List<String> intermediateMpTerm;

	@Field(INTERMEDIATE_MP_DEFINITION)
	private List<String> intermediateMpDefinition;

	@Field(INTERMEDIATE_MP_TERM_SYNONYM)
	private List<String> intermediateMpTermSynonym;

	@Field(TOP_LEVEL_MP_ID)
	private List<String> topLevelMpId;

	@Field(TOP_LEVEL_MP_TERM)
	private List<String> topLevelMpTerm;

	@Field(TOP_LEVEL_MP_DEFINITION)
	private List<String> topLevelMpDefinition;

	@Field(TOP_LEVEL_MP_TERM_SYNONYM)
	private List<String> topLevelMpTermSynonym;

	@Field(TOP_LEVEL_MP_TERM_ID)
	private List<String> topLevelMpTermId;

	@Field(HP_ID)
	private List<String> hpId;

	@Field(HP_TERM)
	private List<String> hpTerm;

	@Field(DATA_TYPE)
	private String dataType;

	@Field(ONTOLOGY_SUBSET)
	private List<String> ontologySubset;

	@Field(MA_ID)
	private List<String> maId;

	@Field(MA_TERM)
	private List<String> maTerm;

	@Field(MA_TERM_SYNONYM)
	private List<String> maTermSynonym;

	@Field(SELECTED_TOP_LEVEL_MA_ID)
	private List<String> selectedTopLevelMaId;

	@Field(SELECTED_TOP_LEVEL_MA_TERM)
	private List<String> selectedTopLevelMaTerm;

	@Field(SELECTED_TOP_LEVEL_MA_TERM_SYNONYM)
	private List<String> selectedTopLevelMaTermSynonym;

	@Field(CHILD_MA_ID)
	private List<String> childMaId;

	@Field(CHILD_MA_TERM)
	private List<String> childMaTerm;

	@Field(CHILD_MA_TERM_SYNONYM)
	private List<String> childMaTermSynonym;

	@Field(INFERRED_MA_ID)
	private List<String> inferredMaId;

	@Field(INFERRED_MA_TERM)
	private List<String> inferredMaTerm;

	@Field(INFERRED_MA_TERM_SYNONYM)
	private List<String> inferredMaTermSynonym;

	@Field(INFERRED_SELECTED_TOP_LEVEL_MA_ID)
	private List<String> inferredSelectedTopLevelMaId;

	@Field(INFERRED_SELECTED_TOP_LEVEL_MA_TERM)
	private List<String> inferredSelectedTopLevelMaTerm;

	@Field(INFERRED_SELECTED_TOP_LEVEL_MA_TERM_SYNONYM)
	private List<String> inferredSelectedTopLevelMaTermSynonym;

	@Field(INFERRED_CHILD_MA_ID)
	private List<String> inferredChildMaId;

	@Field(INFERRED_CHILD_MA_TERM)
	private List<String> inferredChildMaTerm;

	@Field(INFERRED_CHILD_MA_TERM_SYNONYM)
	private List<String> inferredChildMaTermSynonym;

	@Field(GO_ID)
	private List<String> goId;

	@Field(SIBLING_MP_ID)
	private List<String> siblingMpId;

	@Field(SIBLING_MP_TERM)
	private List<String> siblingMpTerm;

	@Field(SIBLING_MP_DEFINITION)
	private List<String> siblingMpDefinition;

	@Field(P_VALUE)
	private List<Float> pValue;

	@Field(MGI_ACCESSION_ID)
	private List<String> mgiAccessionId;

	@Field(MARKER_SYMBOL)
	private List<String> markerSymbol;

	@Field(MARKER_ACCESSION_ID)
	private String markerAccessionId;

	@Field(PREQC_GENE_ID)
	private List<String> preqcGeneId;

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

	@Field(LATEST_PRODUCTION_CENTRE)
	private List<String> latestProductionCentre;

	@Field(LATEST_PHENOTYPING_CENTRE)
	private List<String> latestPhenotypingCentre;

	@Field(LATEST_PHENOTYPE_STATUS)
	private List<String> latestPhenotypeStatus;

	@Field(LEGACY_PHENOTYPE_STATUS)
	private Integer legacyPhenotypeStatus;

	@Field(TYPE)
	private List<String> type;

	@Field(DISEASE_ID)
	private List<String> diseaseId;

	@Field(DISEASE_SOURCE)
	private List<String> diseaseSource;

	@Field(DISEASE_TERM)
	private List<String> diseaseTerm;

	@Field(DISEASE_ALTS)
	private List<String> diseaseAlts;

	@Field(DISEASE_CLASSES)
	private List<String> diseaseClasses;

	@Field(DISEASE_HUMAN_PHENOTYPES)
	private List<String> diseaseHumanPhenotypes;

	@Field(HUMAN_CURATED)
	private List<Boolean> humanCurated;

	@Field(MOUSE_CURATED)
	private List<Boolean> mouseCurated;

	@Field(MGI_PREDICTED)
	private List<Boolean> mgiPredicted;

	@Field(IMPC_PREDICTED)
	private List<Boolean> impcPredicted;

	@Field(MGI_PREDICTED_KNOWN_GENE)
	private List<Boolean> mgiPredictedKnownGene;

	@Field(IMPC_PREDICTED_KNOWN_GENE)
	private List<Boolean> impcPredictedKnownGene;

	@Field(MGI_NOVEL_PREDICTED_IN_LOCUS)
	private List<Boolean> mgiNovelPredictedInLocus;

	@Field(IMPC_NOVEL_PREDICTED_IN_LOCUS)
	private List<Boolean> impcNovelPredictedInLocus;

	@Field(ANNOTATION_TERM_ID)
	private List<String> annotationTermId;

	@Field(ANNOTATION_TERM_NAME)
	private List<String> annotationTermName;

	@Field(NAME)
	private List<String> name;

	@Field(ACCESSION)
	private List<String> accession;

	@Field(EXP_NAME)
	private List<String> expName;

	@Field(LARGE_THUMBNAIL_FILE_PATH)
	private String largeThumbnailFilePath;

	@Field(SMALL_THUMBNAIL_FILE_PATH)
	private String smallThumbnailFilePath;

	@Field(INFERRED_MA_TERM_ID)
	private List<String> inferredMaTermId;

	@Field(INFERRED_MA_TERM_NAME)
	private List<String> inferredMaTermName;

	@Field(ANNOTATED_HIGHER_LEVEL_MA_TERM_ID)
	private List<String> annotatedHigherLevelMaTermId;

	@Field(ANNOTATED_HIGHER_LEVEL_MA_TERM_NAME)
	private List<String> annotatedHigherLevelMaTermName;

	@Field(ANNOTATED_HIGHER_LEVEL_MP_TERM_ID)
	private List<String> annotatedHigherLevelMpTermId;

	@Field(ANNOTATED_HIGHER_LEVEL_MP_TERM_NAME)
	private List<String> annotatedHigherLevelMpTermName;

	@Field(ANNOTATED_OR_INFERRED_HIGHER_LEVEL_MA_TERM_NAME)
	private List<String> annotatedOrInferredHigherLevelMaTermName;

	@Field(ANNOTATED_OR_INFERRED_HIGHER_LEVEL_MA_TERM_ID)
	private List<String> annotatedOrInferredHigherLevelMaTermId;

	@Field(SYMBOL)
	private List<String> symbol;

	@Field(SANGER_SYMBOL)
	private List<String> sangerSymbol;

	@Field(GENE_NAME)
	private List<String> geneName;

	@Field(SUBTYPE)
	private List<String> subtype;

	@Field(GENE_SYNONYMS)
	private List<String> geneSynonyms;

	@Field(ALLELE_NAME)
	private List<String> alleleName;

	@Field(MA_TERM_ID)
	private List<String> maTermId;

	@Field(MA_TERM_NAME)
	private List<String> maTermName;

	@Field(MP_TERM_ID)
	private List<String> mpTermId;

	@Field(MP_TERM_NAME)
	private List<String> mpTermName;

	@Field(EXP_NAME_EXP)
	private List<String> expNameExp;

	@Field(SYMBOL_GENE)
	private List<String> symbolGene;

	@Field(TOP_LEVEL)
	private List<String> topLevel;

	@Field(ALLELE_SYMBOL)
	private List<String> alleleSymbol;

	@Field(ALLELE_ID)
	private List<String> alleleId;

	@Field(STRAIN_NAME)
	private List<String> strainName;

	@Field(STRAIN_ID)
	private List<String> strainId;

	@Field(PIPELINE_NAME)
	private List<String> pipelineName;

	@Field(PIPELINE_STABLE_ID)
	private List<String> pipelineStableId;

	@Field(PIPELINE_STABLE_KEY)
	private List<String> pipelineStableKey;

	@Field(PROCEDURE_NAME)
	private List<String> procedureName;

	@Field(PROCEDURE_STABLE_ID)
	private List<String> procedureStableId;

	@Field(PROCEDURE_STABLE_KEY)
	private List<String> procedureStableKey;

	@Field(PARAMETER_NAME)
	private List<String> parameterName;

	@Field(PARAMETER_STABLE_ID)
	private List<String> parameterStableId;

	@Field(PARAMETER_STABLE_KEY)
	private List<String> parameterStableKey;


	public String getMpId() {

		return mpId;
	}


	public void setMpId(String mpId) {

		this.mpId = mpId;
	}


	public String getMpTerm() {

		return mpTerm;
	}


	public void setMpTerm(String mpTerm) {

		this.mpTerm = mpTerm;
	}


	public String getMpDefinition() {

		return mpDefinition;
	}


	public void setMpDefinition(String mpDefinition) {

		this.mpDefinition = mpDefinition;
	}


	public List<String> getMpTermSynonym() {

		return mpTermSynonym;
	}


	public void setMpTermSynonym(List<String> mpTermSynonym) {

		this.mpTermSynonym = mpTermSynonym;
	}


	public List<Integer> getMpNodeId() {

		return mpNodeId;
	}


	public void setMpNodeId(List<Integer> mpNodeId) {

		this.mpNodeId = mpNodeId;
	}


	public List<String> getChildMpId() {

		return childMpId;
	}


	public void setChildMpId(List<String> childMpId) {

		this.childMpId = childMpId;
	}


	public List<String> getChildMpTerm() {

		return childMpTerm;
	}


	public void setChildMpTerm(List<String> childMpTerm) {

		this.childMpTerm = childMpTerm;
	}


	public List<String> getChildMpDefinition() {

		return childMpDefinition;
	}


	public void setChildMpDefinition(List<String> childMpDefinition) {

		this.childMpDefinition = childMpDefinition;
	}


	public List<String> getChildMpTermSynonym() {

		return childMpTermSynonym;
	}


	public void setChildMpTermSynonym(List<String> childMpTermSynonym) {

		this.childMpTermSynonym = childMpTermSynonym;
	}


	public List<String> getParentMpId() {

		return parentMpId;
	}


	public void setParentMpId(List<String> parentMpId) {

		this.parentMpId = parentMpId;
	}


	public List<String> getParentMpTerm() {

		return parentMpTerm;
	}


	public void setParentMpTerm(List<String> parentMpTerm) {

		this.parentMpTerm = parentMpTerm;
	}


	public List<String> getParentMpDefinition() {

		return parentMpDefinition;
	}


	public void setParentMpDefinition(List<String> parentMpDefinition) {

		this.parentMpDefinition = parentMpDefinition;
	}


	public List<String> getParentMpTermSynonym() {

		return parentMpTermSynonym;
	}


	public void setParentMpTermSynonym(List<String> parentMpTermSynonym) {

		this.parentMpTermSynonym = parentMpTermSynonym;
	}


	public List<String> getIntermediateMpId() {

		return intermediateMpId;
	}


	public void setIntermediateMpId(List<String> intermediateMpId) {

		this.intermediateMpId = intermediateMpId;
	}


	public List<String> getIntermediateMpTerm() {

		return intermediateMpTerm;
	}


	public void setIntermediateMpTerm(List<String> intermediateMpTerm) {

		this.intermediateMpTerm = intermediateMpTerm;
	}


	public List<String> getIntermediateMpDefinition() {

		return intermediateMpDefinition;
	}


	public void setIntermediateMpDefinition(List<String> intermediateMpDefinition) {

		this.intermediateMpDefinition = intermediateMpDefinition;
	}


	public List<String> getIntermediateMpTermSynonym() {

		return intermediateMpTermSynonym;
	}


	public void setIntermediateMpTermSynonym(List<String> intermediateMpTermSynonym) {

		this.intermediateMpTermSynonym = intermediateMpTermSynonym;
	}


	public List<String> getTopLevelMpId() {

		return topLevelMpId;
	}


	public void setTopLevelMpId(List<String> topLevelMpId) {

		this.topLevelMpId = topLevelMpId;
	}


	public List<String> getTopLevelMpTerm() {

		return topLevelMpTerm;
	}


	public void setTopLevelMpTerm(List<String> topLevelMpTerm) {

		this.topLevelMpTerm = topLevelMpTerm;
	}


	public List<String> getTopLevelMpDefinition() {

		return topLevelMpDefinition;
	}


	public void setTopLevelMpDefinition(List<String> topLevelMpDefinition) {

		this.topLevelMpDefinition = topLevelMpDefinition;
	}


	public List<String> getTopLevelMpTermSynonym() {

		return topLevelMpTermSynonym;
	}


	public void setTopLevelMpTermSynonym(List<String> topLevelMpTermSynonym) {

		this.topLevelMpTermSynonym = topLevelMpTermSynonym;
	}


	public List<String> getTopLevelMpTermId() {

		return topLevelMpTermId;
	}


	public void setTopLevelMpTermId(List<String> topLevelMpTermId) {

		this.topLevelMpTermId = topLevelMpTermId;
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


	public String getDataType() {

		return dataType;
	}


	public void setDataType(String dataType) {

		this.dataType = dataType;
	}


	public List<String> getOntologySubset() {

		return ontologySubset;
	}


	public void setOntologySubset(List<String> ontologySubset) {

		this.ontologySubset = ontologySubset;
	}


	public List<String> getMaId() {

		return maId;
	}


	public void setMaId(List<String> maId) {

		this.maId = maId;
	}


	public List<String> getMaTerm() {

		return maTerm;
	}


	public void setMaTerm(List<String> maTerm) {

		this.maTerm = maTerm;
	}


	public List<String> getMaTermSynonym() {

		return maTermSynonym;
	}


	public void setMaTermSynonym(List<String> maTermSynonym) {

		this.maTermSynonym = maTermSynonym;
	}


	public List<String> getSelectedTopLevelMaId() {

		return selectedTopLevelMaId;
	}


	public void setSelectedTopLevelMaId(List<String> selectedTopLevelMaId) {

		this.selectedTopLevelMaId = selectedTopLevelMaId;
	}


	public List<String> getSelectedTopLevelMaTerm() {

		return selectedTopLevelMaTerm;
	}


	public void setSelectedTopLevelMaTerm(List<String> selectedTopLevelMaTerm) {

		this.selectedTopLevelMaTerm = selectedTopLevelMaTerm;
	}


	public List<String> getSelectedTopLevelMaTermSynonym() {

		return selectedTopLevelMaTermSynonym;
	}


	public void setSelectedTopLevelMaTermSynonym(List<String> selectedTopLevelMaTermSynonym) {

		this.selectedTopLevelMaTermSynonym = selectedTopLevelMaTermSynonym;
	}


	public List<String> getChildMaId() {

		return childMaId;
	}


	public void setChildMaId(List<String> childMaId) {

		this.childMaId = childMaId;
	}


	public List<String> getChildMaTerm() {

		return childMaTerm;
	}


	public void setChildMaTerm(List<String> childMaTerm) {

		this.childMaTerm = childMaTerm;
	}


	public List<String> getChildMaTermSynonym() {

		return childMaTermSynonym;
	}


	public void setChildMaTermSynonym(List<String> childMaTermSynonym) {

		this.childMaTermSynonym = childMaTermSynonym;
	}


	public List<String> getInferredMaId() {

		return inferredMaId;
	}


	public void setInferredMaId(List<String> inferredMaId) {

		this.inferredMaId = inferredMaId;
	}


	public List<String> getInferredMaTerm() {

		return inferredMaTerm;
	}


	public void setInferredMaTerm(List<String> inferredMaTerm) {

		this.inferredMaTerm = inferredMaTerm;
	}


	public List<String> getInferredMaTermSynonym() {

		return inferredMaTermSynonym;
	}


	public void setInferredMaTermSynonym(List<String> inferredMaTermSynonym) {

		this.inferredMaTermSynonym = inferredMaTermSynonym;
	}


	public List<String> getInferredSelectedTopLevelMaId() {

		return inferredSelectedTopLevelMaId;
	}


	public void setInferredSelectedTopLevelMaId(List<String> inferredSelectedTopLevelMaId) {

		this.inferredSelectedTopLevelMaId = inferredSelectedTopLevelMaId;
	}


	public List<String> getInferredSelectedTopLevelMaTerm() {

		return inferredSelectedTopLevelMaTerm;
	}


	public void setInferredSelectedTopLevelMaTerm(List<String> inferredSelectedTopLevelMaTerm) {

		this.inferredSelectedTopLevelMaTerm = inferredSelectedTopLevelMaTerm;
	}


	public List<String> getInferredSelectedTopLevelMaTermSynonym() {

		return inferredSelectedTopLevelMaTermSynonym;
	}


	public void setInferredSelectedTopLevelMaTermSynonym(List<String> inferredSelectedTopLevelMaTermSynonym) {

		this.inferredSelectedTopLevelMaTermSynonym = inferredSelectedTopLevelMaTermSynonym;
	}


	public List<String> getInferredChildMaId() {

		return inferredChildMaId;
	}


	public void setInferredChildMaId(List<String> inferredChildMaId) {

		this.inferredChildMaId = inferredChildMaId;
	}


	public List<String> getInferredChildMaTerm() {

		return inferredChildMaTerm;
	}


	public void setInferredChildMaTerm(List<String> inferredChildMaTerm) {

		this.inferredChildMaTerm = inferredChildMaTerm;
	}


	public List<String> getInferredChildMaTermSynonym() {

		return inferredChildMaTermSynonym;
	}


	public void setInferredChildMaTermSynonym(List<String> inferredChildMaTermSynonym) {

		this.inferredChildMaTermSynonym = inferredChildMaTermSynonym;
	}


	public List<String> getGoId() {

		return goId;
	}


	public void setGoId(List<String> goId) {

		this.goId = goId;
	}


	public List<String> getSiblingMpId() {

		return siblingMpId;
	}


	public void setSiblingMpId(List<String> siblingMpId) {

		this.siblingMpId = siblingMpId;
	}


	public List<String> getSiblingMpTerm() {

		return siblingMpTerm;
	}


	public void setSiblingMpTerm(List<String> siblingMpTerm) {

		this.siblingMpTerm = siblingMpTerm;
	}


	public List<String> getSiblingMpDefinition() {

		return siblingMpDefinition;
	}


	public void setSiblingMpDefinition(List<String> siblingMpDefinition) {

		this.siblingMpDefinition = siblingMpDefinition;
	}


	public List<Float> getpValue() {

		return pValue;
	}


	public void setpValue(List<Float> pValue) {

		this.pValue = pValue;
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


	public String getMarkerAccessionId() {

		return markerAccessionId;
	}


	public void setMarkerAccessionId(String markerAccessionId) {

		this.markerAccessionId = markerAccessionId;
	}


	public List<String> getPreqcGeneId() {

		return preqcGeneId;
	}


	public void setPreqcGeneId(List<String> preqcGeneId) {

		this.preqcGeneId = preqcGeneId;
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


	public List<String> getType() {

		return type;
	}


	public void setType(List<String> type) {

		this.type = type;
	}


	public List<String> getDiseaseId() {

		return diseaseId;
	}


	public void setDiseaseId(List<String> diseaseId) {

		this.diseaseId = diseaseId;
	}


	public List<String> getDiseaseSource() {

		return diseaseSource;
	}


	public void setDiseaseSource(List<String> diseaseSource) {

		this.diseaseSource = diseaseSource;
	}


	public List<String> getDiseaseTerm() {

		return diseaseTerm;
	}


	public void setDiseaseTerm(List<String> diseaseTerm) {

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


	public List<Boolean> getHumanCurated() {

		return humanCurated;
	}


	public void setHumanCurated(List<Boolean> humanCurated) {

		this.humanCurated = humanCurated;
	}


	public List<Boolean> getMouseCurated() {

		return mouseCurated;
	}


	public void setMouseCurated(List<Boolean> mouseCurated) {

		this.mouseCurated = mouseCurated;
	}


	public List<Boolean> getMgiPredicted() {

		return mgiPredicted;
	}


	public void setMgiPredicted(List<Boolean> mgiPredicted) {

		this.mgiPredicted = mgiPredicted;
	}


	public List<Boolean> getImpcPredicted() {

		return impcPredicted;
	}


	public void setImpcPredicted(List<Boolean> impcPredicted) {

		this.impcPredicted = impcPredicted;
	}


	public List<Boolean> getMgiPredictedKnownGene() {

		return mgiPredictedKnownGene;
	}


	public void setMgiPredictedKnownGene(List<Boolean> mgiPredictedKnownGene) {

		this.mgiPredictedKnownGene = mgiPredictedKnownGene;
	}


	public List<Boolean> getImpcPredictedKnownGene() {

		return impcPredictedKnownGene;
	}


	public void setImpcPredictedKnownGene(List<Boolean> impcPredictedKnownGene) {

		this.impcPredictedKnownGene = impcPredictedKnownGene;
	}


	public List<Boolean> getMgiNovelPredictedInLocus() {

		return mgiNovelPredictedInLocus;
	}


	public void setMgiNovelPredictedInLocus(List<Boolean> mgiNovelPredictedInLocus) {

		this.mgiNovelPredictedInLocus = mgiNovelPredictedInLocus;
	}


	public List<Boolean> getImpcNovelPredictedInLocus() {

		return impcNovelPredictedInLocus;
	}


	public void setImpcNovelPredictedInLocus(List<Boolean> impcNovelPredictedInLocus) {

		this.impcNovelPredictedInLocus = impcNovelPredictedInLocus;
	}


	public List<String> getAnnotationTermId() {

		return annotationTermId;
	}


	public void setAnnotationTermId(List<String> annotationTermId) {

		this.annotationTermId = annotationTermId;
	}


	public List<String> getAnnotationTermName() {

		return annotationTermName;
	}


	public void setAnnotationTermName(List<String> annotationTermName) {

		this.annotationTermName = annotationTermName;
	}


	public List<String> getName() {

		return name;
	}


	public void setName(List<String> name) {

		this.name = name;
	}


	public List<String> getAccession() {

		return accession;
	}


	public void setAccession(List<String> accession) {

		this.accession = accession;
	}


	public List<String> getExpName() {

		return expName;
	}


	public void setExpName(List<String> expName) {

		this.expName = expName;
	}


	public String getLargeThumbnailFilePath() {

		return largeThumbnailFilePath;
	}


	public void setLargeThumbnailFilePath(String largeThumbnailFilePath) {

		this.largeThumbnailFilePath = largeThumbnailFilePath;
	}


	public String getSmallThumbnailFilePath() {

		return smallThumbnailFilePath;
	}


	public void setSmallThumbnailFilePath(String smallThumbnailFilePath) {

		this.smallThumbnailFilePath = smallThumbnailFilePath;
	}


	public List<String> getInferredMaTermId() {

		return inferredMaTermId;
	}


	public void setInferredMaTermId(List<String> inferredMaTermId) {

		this.inferredMaTermId = inferredMaTermId;
	}


	public List<String> getInferredMaTermName() {

		return inferredMaTermName;
	}


	public void setInferredMaTermName(List<String> inferredMaTermName) {

		this.inferredMaTermName = inferredMaTermName;
	}


	public List<String> getAnnotatedHigherLevelMaTermId() {

		return annotatedHigherLevelMaTermId;
	}


	public void setAnnotatedHigherLevelMaTermId(List<String> annotatedHigherLevelMaTermId) {

		this.annotatedHigherLevelMaTermId = annotatedHigherLevelMaTermId;
	}


	public List<String> getAnnotatedHigherLevelMaTermName() {

		return annotatedHigherLevelMaTermName;
	}


	public void setAnnotatedHigherLevelMaTermName(List<String> annotatedHigherLevelMaTermName) {

		this.annotatedHigherLevelMaTermName = annotatedHigherLevelMaTermName;
	}


	public List<String> getAnnotatedHigherLevelMpTermId() {

		return annotatedHigherLevelMpTermId;
	}


	public void setAnnotatedHigherLevelMpTermId(List<String> annotatedHigherLevelMpTermId) {

		this.annotatedHigherLevelMpTermId = annotatedHigherLevelMpTermId;
	}


	public List<String> getAnnotatedHigherLevelMpTermName() {

		return annotatedHigherLevelMpTermName;
	}


	public void setAnnotatedHigherLevelMpTermName(List<String> annotatedHigherLevelMpTermName) {

		this.annotatedHigherLevelMpTermName = annotatedHigherLevelMpTermName;
	}


	public List<String> getAnnotatedOrInferredHigherLevelMaTermName() {

		return annotatedOrInferredHigherLevelMaTermName;
	}


	public void setAnnotatedOrInferredHigherLevelMaTermName(List<String> annotatedOrInferredHigherLevelMaTermName) {

		this.annotatedOrInferredHigherLevelMaTermName = annotatedOrInferredHigherLevelMaTermName;
	}


	public List<String> getAnnotatedOrInferredHigherLevelMaTermId() {

		return annotatedOrInferredHigherLevelMaTermId;
	}


	public void setAnnotatedOrInferredHigherLevelMaTermId(List<String> annotatedOrInferredHigherLevelMaTermId) {

		this.annotatedOrInferredHigherLevelMaTermId = annotatedOrInferredHigherLevelMaTermId;
	}


	public List<String> getSymbol() {

		return symbol;
	}


	public void setSymbol(List<String> symbol) {

		this.symbol = symbol;
	}


	public List<String> getSangerSymbol() {

		return sangerSymbol;
	}


	public void setSangerSymbol(List<String> sangerSymbol) {

		this.sangerSymbol = sangerSymbol;
	}


	public List<String> getGeneName() {

		return geneName;
	}


	public void setGeneName(List<String> geneName) {

		this.geneName = geneName;
	}


	public List<String> getSubtype() {

		return subtype;
	}


	public void setSubtype(List<String> subtype) {

		this.subtype = subtype;
	}


	public List<String> getGeneSynonyms() {

		return geneSynonyms;
	}


	public void setGeneSynonyms(List<String> geneSynonyms) {

		this.geneSynonyms = geneSynonyms;
	}


	public List<String> getAlleleName() {

		return alleleName;
	}


	public void setAlleleName(List<String> alleleName) {

		this.alleleName = alleleName;
	}


	public List<String> getMaTermId() {

		return maTermId;
	}


	public void setMaTermId(List<String> maTermId) {

		this.maTermId = maTermId;
	}


	public List<String> getMaTermName() {

		return maTermName;
	}


	public void setMaTermName(List<String> maTermName) {

		this.maTermName = maTermName;
	}


	public List<String> getMpTermId() {

		return mpTermId;
	}


	public void setMpTermId(List<String> mpTermId) {

		this.mpTermId = mpTermId;
	}


	public List<String> getMpTermName() {

		return mpTermName;
	}


	public void setMpTermName(List<String> mpTermName) {

		this.mpTermName = mpTermName;
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


	public List<String> getTopLevel() {

		return topLevel;
	}


	public void setTopLevel(List<String> topLevel) {

		this.topLevel = topLevel;
	}


	public List<String> getAlleleSymbol() {

		return alleleSymbol;
	}


	public void setAlleleSymbol(List<String> alleleSymbol) {

		this.alleleSymbol = alleleSymbol;
	}


	public List<String> getAlleleId() {

		return alleleId;
	}


	public void setAlleleId(List<String> alleleId) {

		this.alleleId = alleleId;
	}


	public List<String> getStrainName() {

		return strainName;
	}


	public void setStrainName(List<String> strainName) {

		this.strainName = strainName;
	}


	public List<String> getStrainId() {

		return strainId;
	}


	public void setStrainId(List<String> strainId) {

		this.strainId = strainId;
	}


	public List<String> getPipelineName() {

		return pipelineName;
	}


	public void setPipelineName(List<String> pipelineName) {

		this.pipelineName = pipelineName;
	}


	public List<String> getPipelineStableId() {

		return pipelineStableId;
	}


	public void setPipelineStableId(List<String> pipelineStableId) {

		this.pipelineStableId = pipelineStableId;
	}


	public List<String> getPipelineStableKey() {

		return pipelineStableKey;
	}


	public void setPipelineStableKey(List<String> pipelineStableKey) {

		this.pipelineStableKey = pipelineStableKey;
	}


	public List<String> getProcedureName() {

		return procedureName;
	}


	public void setProcedureName(List<String> procedureName) {

		this.procedureName = procedureName;
	}


	public List<String> getProcedureStableId() {

		return procedureStableId;
	}


	public void setProcedureStableId(List<String> procedureStableId) {

		this.procedureStableId = procedureStableId;
	}


	public List<String> getProcedureStableKey() {

		return procedureStableKey;
	}


	public void setProcedureStableKey(List<String> procedureStableKey) {

		this.procedureStableKey = procedureStableKey;
	}


	public List<String> getParameterName() {

		return parameterName;
	}


	public void setParameterName(List<String> parameterName) {

		this.parameterName = parameterName;
	}


	public List<String> getParameterStableId() {

		return parameterStableId;
	}


	public void setParameterStableId(List<String> parameterStableId) {

		this.parameterStableId = parameterStableId;
	}


	public List<String> getParameterStableKey() {

		return parameterStableKey;
	}


	public void setParameterStableKey(List<String> parameterStableKey) {

		this.parameterStableKey = parameterStableKey;
	}


	@Override
	public boolean equals(Object o) {

		if (this == o) return true;
		if (!(o instanceof MpDTO)) return false;

		MpDTO mpDTO = (MpDTO) o;

		if (accession != null ? !accession.equals(mpDTO.accession) : mpDTO.accession != null) return false;
		if (alleleId != null ? !alleleId.equals(mpDTO.alleleId) : mpDTO.alleleId != null) return false;
		if (alleleName != null ? !alleleName.equals(mpDTO.alleleName) : mpDTO.alleleName != null) return false;
		if (alleleSymbol != null ? !alleleSymbol.equals(mpDTO.alleleSymbol) : mpDTO.alleleSymbol != null) return false;
		if (annotatedHigherLevelMaTermId != null ? !annotatedHigherLevelMaTermId.equals(mpDTO.annotatedHigherLevelMaTermId) : mpDTO.annotatedHigherLevelMaTermId != null)
			return false;
		if (annotatedHigherLevelMaTermName != null ? !annotatedHigherLevelMaTermName.equals(mpDTO.annotatedHigherLevelMaTermName) : mpDTO.annotatedHigherLevelMaTermName != null)
			return false;
		if (annotatedHigherLevelMpTermId != null ? !annotatedHigherLevelMpTermId.equals(mpDTO.annotatedHigherLevelMpTermId) : mpDTO.annotatedHigherLevelMpTermId != null)
			return false;
		if (annotatedHigherLevelMpTermName != null ? !annotatedHigherLevelMpTermName.equals(mpDTO.annotatedHigherLevelMpTermName) : mpDTO.annotatedHigherLevelMpTermName != null)
			return false;
		if (annotatedOrInferredHigherLevelMaTermId != null ? !annotatedOrInferredHigherLevelMaTermId.equals(mpDTO.annotatedOrInferredHigherLevelMaTermId) : mpDTO.annotatedOrInferredHigherLevelMaTermId != null)
			return false;
		if (annotatedOrInferredHigherLevelMaTermName != null ? !annotatedOrInferredHigherLevelMaTermName.equals(mpDTO.annotatedOrInferredHigherLevelMaTermName) : mpDTO.annotatedOrInferredHigherLevelMaTermName != null)
			return false;
		if (annotationTermId != null ? !annotationTermId.equals(mpDTO.annotationTermId) : mpDTO.annotationTermId != null)
			return false;
		if (annotationTermName != null ? !annotationTermName.equals(mpDTO.annotationTermName) : mpDTO.annotationTermName != null)
			return false;
		if (childMaId != null ? !childMaId.equals(mpDTO.childMaId) : mpDTO.childMaId != null) return false;
		if (childMaTerm != null ? !childMaTerm.equals(mpDTO.childMaTerm) : mpDTO.childMaTerm != null) return false;
		if (childMaTermSynonym != null ? !childMaTermSynonym.equals(mpDTO.childMaTermSynonym) : mpDTO.childMaTermSynonym != null)
			return false;
		if (childMpDefinition != null ? !childMpDefinition.equals(mpDTO.childMpDefinition) : mpDTO.childMpDefinition != null)
			return false;
		if (childMpId != null ? !childMpId.equals(mpDTO.childMpId) : mpDTO.childMpId != null) return false;
		if (childMpTerm != null ? !childMpTerm.equals(mpDTO.childMpTerm) : mpDTO.childMpTerm != null) return false;
		if (childMpTermSynonym != null ? !childMpTermSynonym.equals(mpDTO.childMpTermSynonym) : mpDTO.childMpTermSynonym != null)
			return false;
		if (dataType != null ? !dataType.equals(mpDTO.dataType) : mpDTO.dataType != null) return false;
		if (diseaseAlts != null ? !diseaseAlts.equals(mpDTO.diseaseAlts) : mpDTO.diseaseAlts != null) return false;
		if (diseaseClasses != null ? !diseaseClasses.equals(mpDTO.diseaseClasses) : mpDTO.diseaseClasses != null)
			return false;
		if (diseaseHumanPhenotypes != null ? !diseaseHumanPhenotypes.equals(mpDTO.diseaseHumanPhenotypes) : mpDTO.diseaseHumanPhenotypes != null)
			return false;
		if (diseaseId != null ? !diseaseId.equals(mpDTO.diseaseId) : mpDTO.diseaseId != null) return false;
		if (diseaseSource != null ? !diseaseSource.equals(mpDTO.diseaseSource) : mpDTO.diseaseSource != null)
			return false;
		if (diseaseTerm != null ? !diseaseTerm.equals(mpDTO.diseaseTerm) : mpDTO.diseaseTerm != null) return false;
		if (expName != null ? !expName.equals(mpDTO.expName) : mpDTO.expName != null) return false;
		if (expNameExp != null ? !expNameExp.equals(mpDTO.expNameExp) : mpDTO.expNameExp != null) return false;
		if (geneName != null ? !geneName.equals(mpDTO.geneName) : mpDTO.geneName != null) return false;
		if (geneSynonyms != null ? !geneSynonyms.equals(mpDTO.geneSynonyms) : mpDTO.geneSynonyms != null) return false;
		if (goId != null ? !goId.equals(mpDTO.goId) : mpDTO.goId != null) return false;
		if (hpId != null ? !hpId.equals(mpDTO.hpId) : mpDTO.hpId != null) return false;
		if (hpTerm != null ? !hpTerm.equals(mpDTO.hpTerm) : mpDTO.hpTerm != null) return false;
		if (humanCurated != null ? !humanCurated.equals(mpDTO.humanCurated) : mpDTO.humanCurated != null) return false;
		if (humanGeneSymbol != null ? !humanGeneSymbol.equals(mpDTO.humanGeneSymbol) : mpDTO.humanGeneSymbol != null)
			return false;
		if (imitsPhenotypeComplete != null ? !imitsPhenotypeComplete.equals(mpDTO.imitsPhenotypeComplete) : mpDTO.imitsPhenotypeComplete != null)
			return false;
		if (imitsPhenotypeStarted != null ? !imitsPhenotypeStarted.equals(mpDTO.imitsPhenotypeStarted) : mpDTO.imitsPhenotypeStarted != null)
			return false;
		if (imitsPhenotypeStatus != null ? !imitsPhenotypeStatus.equals(mpDTO.imitsPhenotypeStatus) : mpDTO.imitsPhenotypeStatus != null)
			return false;
		if (impcNovelPredictedInLocus != null ? !impcNovelPredictedInLocus.equals(mpDTO.impcNovelPredictedInLocus) : mpDTO.impcNovelPredictedInLocus != null)
			return false;
		if (impcPredicted != null ? !impcPredicted.equals(mpDTO.impcPredicted) : mpDTO.impcPredicted != null)
			return false;
		if (impcPredictedKnownGene != null ? !impcPredictedKnownGene.equals(mpDTO.impcPredictedKnownGene) : mpDTO.impcPredictedKnownGene != null)
			return false;
		if (inferredChildMaId != null ? !inferredChildMaId.equals(mpDTO.inferredChildMaId) : mpDTO.inferredChildMaId != null)
			return false;
		if (inferredChildMaTerm != null ? !inferredChildMaTerm.equals(mpDTO.inferredChildMaTerm) : mpDTO.inferredChildMaTerm != null)
			return false;
		if (inferredChildMaTermSynonym != null ? !inferredChildMaTermSynonym.equals(mpDTO.inferredChildMaTermSynonym) : mpDTO.inferredChildMaTermSynonym != null)
			return false;
		if (inferredMaId != null ? !inferredMaId.equals(mpDTO.inferredMaId) : mpDTO.inferredMaId != null) return false;
		if (inferredMaTerm != null ? !inferredMaTerm.equals(mpDTO.inferredMaTerm) : mpDTO.inferredMaTerm != null)
			return false;
		if (inferredMaTermId != null ? !inferredMaTermId.equals(mpDTO.inferredMaTermId) : mpDTO.inferredMaTermId != null)
			return false;
		if (inferredMaTermName != null ? !inferredMaTermName.equals(mpDTO.inferredMaTermName) : mpDTO.inferredMaTermName != null)
			return false;
		if (inferredMaTermSynonym != null ? !inferredMaTermSynonym.equals(mpDTO.inferredMaTermSynonym) : mpDTO.inferredMaTermSynonym != null)
			return false;
		if (inferredSelectedTopLevelMaId != null ? !inferredSelectedTopLevelMaId.equals(mpDTO.inferredSelectedTopLevelMaId) : mpDTO.inferredSelectedTopLevelMaId != null)
			return false;
		if (inferredSelectedTopLevelMaTerm != null ? !inferredSelectedTopLevelMaTerm.equals(mpDTO.inferredSelectedTopLevelMaTerm) : mpDTO.inferredSelectedTopLevelMaTerm != null)
			return false;
		if (inferredSelectedTopLevelMaTermSynonym != null ? !inferredSelectedTopLevelMaTermSynonym.equals(mpDTO.inferredSelectedTopLevelMaTermSynonym) : mpDTO.inferredSelectedTopLevelMaTermSynonym != null)
			return false;
		if (intermediateMpDefinition != null ? !intermediateMpDefinition.equals(mpDTO.intermediateMpDefinition) : mpDTO.intermediateMpDefinition != null)
			return false;
		if (intermediateMpId != null ? !intermediateMpId.equals(mpDTO.intermediateMpId) : mpDTO.intermediateMpId != null)
			return false;
		if (intermediateMpTerm != null ? !intermediateMpTerm.equals(mpDTO.intermediateMpTerm) : mpDTO.intermediateMpTerm != null)
			return false;
		if (intermediateMpTermSynonym != null ? !intermediateMpTermSynonym.equals(mpDTO.intermediateMpTermSynonym) : mpDTO.intermediateMpTermSynonym != null)
			return false;
		if (largeThumbnailFilePath != null ? !largeThumbnailFilePath.equals(mpDTO.largeThumbnailFilePath) : mpDTO.largeThumbnailFilePath != null)
			return false;
		if (latestPhenotypeStatus != null ? !latestPhenotypeStatus.equals(mpDTO.latestPhenotypeStatus) : mpDTO.latestPhenotypeStatus != null)
			return false;
		if (latestPhenotypingCentre != null ? !latestPhenotypingCentre.equals(mpDTO.latestPhenotypingCentre) : mpDTO.latestPhenotypingCentre != null)
			return false;
		if (latestProductionCentre != null ? !latestProductionCentre.equals(mpDTO.latestProductionCentre) : mpDTO.latestProductionCentre != null)
			return false;
		if (legacyPhenotypeStatus != null ? !legacyPhenotypeStatus.equals(mpDTO.legacyPhenotypeStatus) : mpDTO.legacyPhenotypeStatus != null)
			return false;
		if (maId != null ? !maId.equals(mpDTO.maId) : mpDTO.maId != null) return false;
		if (maTerm != null ? !maTerm.equals(mpDTO.maTerm) : mpDTO.maTerm != null) return false;
		if (maTermId != null ? !maTermId.equals(mpDTO.maTermId) : mpDTO.maTermId != null) return false;
		if (maTermName != null ? !maTermName.equals(mpDTO.maTermName) : mpDTO.maTermName != null) return false;
		if (maTermSynonym != null ? !maTermSynonym.equals(mpDTO.maTermSynonym) : mpDTO.maTermSynonym != null)
			return false;
		if (markerAccessionId != null ? !markerAccessionId.equals(mpDTO.markerAccessionId) : mpDTO.markerAccessionId != null)
			return false;
		if (markerName != null ? !markerName.equals(mpDTO.markerName) : mpDTO.markerName != null) return false;
		if (markerSymbol != null ? !markerSymbol.equals(mpDTO.markerSymbol) : mpDTO.markerSymbol != null) return false;
		if (markerSynonym != null ? !markerSynonym.equals(mpDTO.markerSynonym) : mpDTO.markerSynonym != null)
			return false;
		if (markerType != null ? !markerType.equals(mpDTO.markerType) : mpDTO.markerType != null) return false;
		if (mgiAccessionId != null ? !mgiAccessionId.equals(mpDTO.mgiAccessionId) : mpDTO.mgiAccessionId != null)
			return false;
		if (mgiNovelPredictedInLocus != null ? !mgiNovelPredictedInLocus.equals(mpDTO.mgiNovelPredictedInLocus) : mpDTO.mgiNovelPredictedInLocus != null)
			return false;
		if (mgiPredicted != null ? !mgiPredicted.equals(mpDTO.mgiPredicted) : mpDTO.mgiPredicted != null) return false;
		if (mgiPredictedKnownGene != null ? !mgiPredictedKnownGene.equals(mpDTO.mgiPredictedKnownGene) : mpDTO.mgiPredictedKnownGene != null)
			return false;
		if (mouseCurated != null ? !mouseCurated.equals(mpDTO.mouseCurated) : mpDTO.mouseCurated != null) return false;
		if (mpDefinition != null ? !mpDefinition.equals(mpDTO.mpDefinition) : mpDTO.mpDefinition != null) return false;
		if (mpId != null ? !mpId.equals(mpDTO.mpId) : mpDTO.mpId != null) return false;
		if (mpNodeId != null ? !mpNodeId.equals(mpDTO.mpNodeId) : mpDTO.mpNodeId != null) return false;
		if (mpTerm != null ? !mpTerm.equals(mpDTO.mpTerm) : mpDTO.mpTerm != null) return false;
		if (mpTermId != null ? !mpTermId.equals(mpDTO.mpTermId) : mpDTO.mpTermId != null) return false;
		if (mpTermName != null ? !mpTermName.equals(mpDTO.mpTermName) : mpDTO.mpTermName != null) return false;
		if (mpTermSynonym != null ? !mpTermSynonym.equals(mpDTO.mpTermSynonym) : mpDTO.mpTermSynonym != null)
			return false;
		if (name != null ? !name.equals(mpDTO.name) : mpDTO.name != null) return false;
		if (ontologySubset != null ? !ontologySubset.equals(mpDTO.ontologySubset) : mpDTO.ontologySubset != null)
			return false;
		if (pValue != null ? !pValue.equals(mpDTO.pValue) : mpDTO.pValue != null) return false;
		if (parameterName != null ? !parameterName.equals(mpDTO.parameterName) : mpDTO.parameterName != null)
			return false;
		if (parameterStableId != null ? !parameterStableId.equals(mpDTO.parameterStableId) : mpDTO.parameterStableId != null)
			return false;
		if (parameterStableKey != null ? !parameterStableKey.equals(mpDTO.parameterStableKey) : mpDTO.parameterStableKey != null)
			return false;
		if (parentMpDefinition != null ? !parentMpDefinition.equals(mpDTO.parentMpDefinition) : mpDTO.parentMpDefinition != null)
			return false;
		if (parentMpId != null ? !parentMpId.equals(mpDTO.parentMpId) : mpDTO.parentMpId != null) return false;
		if (parentMpTerm != null ? !parentMpTerm.equals(mpDTO.parentMpTerm) : mpDTO.parentMpTerm != null) return false;
		if (parentMpTermSynonym != null ? !parentMpTermSynonym.equals(mpDTO.parentMpTermSynonym) : mpDTO.parentMpTermSynonym != null)
			return false;
		if (pipelineName != null ? !pipelineName.equals(mpDTO.pipelineName) : mpDTO.pipelineName != null) return false;
		if (pipelineStableId != null ? !pipelineStableId.equals(mpDTO.pipelineStableId) : mpDTO.pipelineStableId != null)
			return false;
		if (pipelineStableKey != null ? !pipelineStableKey.equals(mpDTO.pipelineStableKey) : mpDTO.pipelineStableKey != null)
			return false;
		if (preqcGeneId != null ? !preqcGeneId.equals(mpDTO.preqcGeneId) : mpDTO.preqcGeneId != null) return false;
		if (procedureName != null ? !procedureName.equals(mpDTO.procedureName) : mpDTO.procedureName != null)
			return false;
		if (procedureStableId != null ? !procedureStableId.equals(mpDTO.procedureStableId) : mpDTO.procedureStableId != null)
			return false;
		if (procedureStableKey != null ? !procedureStableKey.equals(mpDTO.procedureStableKey) : mpDTO.procedureStableKey != null)
			return false;
		if (sangerSymbol != null ? !sangerSymbol.equals(mpDTO.sangerSymbol) : mpDTO.sangerSymbol != null) return false;
		if (selectedTopLevelMaId != null ? !selectedTopLevelMaId.equals(mpDTO.selectedTopLevelMaId) : mpDTO.selectedTopLevelMaId != null)
			return false;
		if (selectedTopLevelMaTerm != null ? !selectedTopLevelMaTerm.equals(mpDTO.selectedTopLevelMaTerm) : mpDTO.selectedTopLevelMaTerm != null)
			return false;
		if (selectedTopLevelMaTermSynonym != null ? !selectedTopLevelMaTermSynonym.equals(mpDTO.selectedTopLevelMaTermSynonym) : mpDTO.selectedTopLevelMaTermSynonym != null)
			return false;
		if (siblingMpDefinition != null ? !siblingMpDefinition.equals(mpDTO.siblingMpDefinition) : mpDTO.siblingMpDefinition != null)
			return false;
		if (siblingMpId != null ? !siblingMpId.equals(mpDTO.siblingMpId) : mpDTO.siblingMpId != null) return false;
		if (siblingMpTerm != null ? !siblingMpTerm.equals(mpDTO.siblingMpTerm) : mpDTO.siblingMpTerm != null)
			return false;
		if (smallThumbnailFilePath != null ? !smallThumbnailFilePath.equals(mpDTO.smallThumbnailFilePath) : mpDTO.smallThumbnailFilePath != null)
			return false;
		if (status != null ? !status.equals(mpDTO.status) : mpDTO.status != null) return false;
		if (strainId != null ? !strainId.equals(mpDTO.strainId) : mpDTO.strainId != null) return false;
		if (strainName != null ? !strainName.equals(mpDTO.strainName) : mpDTO.strainName != null) return false;
		if (subtype != null ? !subtype.equals(mpDTO.subtype) : mpDTO.subtype != null) return false;
		if (symbol != null ? !symbol.equals(mpDTO.symbol) : mpDTO.symbol != null) return false;
		if (symbolGene != null ? !symbolGene.equals(mpDTO.symbolGene) : mpDTO.symbolGene != null) return false;
		if (topLevel != null ? !topLevel.equals(mpDTO.topLevel) : mpDTO.topLevel != null) return false;
		if (topLevelMpDefinition != null ? !topLevelMpDefinition.equals(mpDTO.topLevelMpDefinition) : mpDTO.topLevelMpDefinition != null)
			return false;
		if (topLevelMpId != null ? !topLevelMpId.equals(mpDTO.topLevelMpId) : mpDTO.topLevelMpId != null) return false;
		if (topLevelMpTerm != null ? !topLevelMpTerm.equals(mpDTO.topLevelMpTerm) : mpDTO.topLevelMpTerm != null)
			return false;
		if (topLevelMpTermId != null ? !topLevelMpTermId.equals(mpDTO.topLevelMpTermId) : mpDTO.topLevelMpTermId != null)
			return false;
		if (topLevelMpTermSynonym != null ? !topLevelMpTermSynonym.equals(mpDTO.topLevelMpTermSynonym) : mpDTO.topLevelMpTermSynonym != null)
			return false;
		if (type != null ? !type.equals(mpDTO.type) : mpDTO.type != null) return false;

		return true;
	}


	@Override
	public int hashCode() {

		int result = mpId != null ? mpId.hashCode() : 0;
		result = 31 * result + (mpTerm != null ? mpTerm.hashCode() : 0);
		result = 31 * result + (mpDefinition != null ? mpDefinition.hashCode() : 0);
		result = 31 * result + (mpTermSynonym != null ? mpTermSynonym.hashCode() : 0);
		result = 31 * result + (mpNodeId != null ? mpNodeId.hashCode() : 0);
		result = 31 * result + (childMpId != null ? childMpId.hashCode() : 0);
		result = 31 * result + (childMpTerm != null ? childMpTerm.hashCode() : 0);
		result = 31 * result + (childMpDefinition != null ? childMpDefinition.hashCode() : 0);
		result = 31 * result + (childMpTermSynonym != null ? childMpTermSynonym.hashCode() : 0);
		result = 31 * result + (parentMpId != null ? parentMpId.hashCode() : 0);
		result = 31 * result + (parentMpTerm != null ? parentMpTerm.hashCode() : 0);
		result = 31 * result + (parentMpDefinition != null ? parentMpDefinition.hashCode() : 0);
		result = 31 * result + (parentMpTermSynonym != null ? parentMpTermSynonym.hashCode() : 0);
		result = 31 * result + (intermediateMpId != null ? intermediateMpId.hashCode() : 0);
		result = 31 * result + (intermediateMpTerm != null ? intermediateMpTerm.hashCode() : 0);
		result = 31 * result + (intermediateMpDefinition != null ? intermediateMpDefinition.hashCode() : 0);
		result = 31 * result + (intermediateMpTermSynonym != null ? intermediateMpTermSynonym.hashCode() : 0);
		result = 31 * result + (topLevelMpId != null ? topLevelMpId.hashCode() : 0);
		result = 31 * result + (topLevelMpTerm != null ? topLevelMpTerm.hashCode() : 0);
		result = 31 * result + (topLevelMpDefinition != null ? topLevelMpDefinition.hashCode() : 0);
		result = 31 * result + (topLevelMpTermSynonym != null ? topLevelMpTermSynonym.hashCode() : 0);
		result = 31 * result + (topLevelMpTermId != null ? topLevelMpTermId.hashCode() : 0);
		result = 31 * result + (hpId != null ? hpId.hashCode() : 0);
		result = 31 * result + (hpTerm != null ? hpTerm.hashCode() : 0);
		result = 31 * result + (dataType != null ? dataType.hashCode() : 0);
		result = 31 * result + (ontologySubset != null ? ontologySubset.hashCode() : 0);
		result = 31 * result + (maId != null ? maId.hashCode() : 0);
		result = 31 * result + (maTerm != null ? maTerm.hashCode() : 0);
		result = 31 * result + (maTermSynonym != null ? maTermSynonym.hashCode() : 0);
		result = 31 * result + (selectedTopLevelMaId != null ? selectedTopLevelMaId.hashCode() : 0);
		result = 31 * result + (selectedTopLevelMaTerm != null ? selectedTopLevelMaTerm.hashCode() : 0);
		result = 31 * result + (selectedTopLevelMaTermSynonym != null ? selectedTopLevelMaTermSynonym.hashCode() : 0);
		result = 31 * result + (childMaId != null ? childMaId.hashCode() : 0);
		result = 31 * result + (childMaTerm != null ? childMaTerm.hashCode() : 0);
		result = 31 * result + (childMaTermSynonym != null ? childMaTermSynonym.hashCode() : 0);
		result = 31 * result + (inferredMaId != null ? inferredMaId.hashCode() : 0);
		result = 31 * result + (inferredMaTerm != null ? inferredMaTerm.hashCode() : 0);
		result = 31 * result + (inferredMaTermSynonym != null ? inferredMaTermSynonym.hashCode() : 0);
		result = 31 * result + (inferredSelectedTopLevelMaId != null ? inferredSelectedTopLevelMaId.hashCode() : 0);
		result = 31 * result + (inferredSelectedTopLevelMaTerm != null ? inferredSelectedTopLevelMaTerm.hashCode() : 0);
		result = 31 * result + (inferredSelectedTopLevelMaTermSynonym != null ? inferredSelectedTopLevelMaTermSynonym.hashCode() : 0);
		result = 31 * result + (inferredChildMaId != null ? inferredChildMaId.hashCode() : 0);
		result = 31 * result + (inferredChildMaTerm != null ? inferredChildMaTerm.hashCode() : 0);
		result = 31 * result + (inferredChildMaTermSynonym != null ? inferredChildMaTermSynonym.hashCode() : 0);
		result = 31 * result + (goId != null ? goId.hashCode() : 0);
		result = 31 * result + (siblingMpId != null ? siblingMpId.hashCode() : 0);
		result = 31 * result + (siblingMpTerm != null ? siblingMpTerm.hashCode() : 0);
		result = 31 * result + (siblingMpDefinition != null ? siblingMpDefinition.hashCode() : 0);
		result = 31 * result + (pValue != null ? pValue.hashCode() : 0);
		result = 31 * result + (mgiAccessionId != null ? mgiAccessionId.hashCode() : 0);
		result = 31 * result + (markerSymbol != null ? markerSymbol.hashCode() : 0);
		result = 31 * result + (markerAccessionId != null ? markerAccessionId.hashCode() : 0);
		result = 31 * result + (preqcGeneId != null ? preqcGeneId.hashCode() : 0);
		result = 31 * result + (markerName != null ? markerName.hashCode() : 0);
		result = 31 * result + (markerSynonym != null ? markerSynonym.hashCode() : 0);
		result = 31 * result + (markerType != null ? markerType.hashCode() : 0);
		result = 31 * result + (humanGeneSymbol != null ? humanGeneSymbol.hashCode() : 0);
		result = 31 * result + (status != null ? status.hashCode() : 0);
		result = 31 * result + (imitsPhenotypeStarted != null ? imitsPhenotypeStarted.hashCode() : 0);
		result = 31 * result + (imitsPhenotypeComplete != null ? imitsPhenotypeComplete.hashCode() : 0);
		result = 31 * result + (imitsPhenotypeStatus != null ? imitsPhenotypeStatus.hashCode() : 0);
		result = 31 * result + (latestProductionCentre != null ? latestProductionCentre.hashCode() : 0);
		result = 31 * result + (latestPhenotypingCentre != null ? latestPhenotypingCentre.hashCode() : 0);
		result = 31 * result + (latestPhenotypeStatus != null ? latestPhenotypeStatus.hashCode() : 0);
		result = 31 * result + (legacyPhenotypeStatus != null ? legacyPhenotypeStatus.hashCode() : 0);
		result = 31 * result + (type != null ? type.hashCode() : 0);
		result = 31 * result + (diseaseId != null ? diseaseId.hashCode() : 0);
		result = 31 * result + (diseaseSource != null ? diseaseSource.hashCode() : 0);
		result = 31 * result + (diseaseTerm != null ? diseaseTerm.hashCode() : 0);
		result = 31 * result + (diseaseAlts != null ? diseaseAlts.hashCode() : 0);
		result = 31 * result + (diseaseClasses != null ? diseaseClasses.hashCode() : 0);
		result = 31 * result + (diseaseHumanPhenotypes != null ? diseaseHumanPhenotypes.hashCode() : 0);
		result = 31 * result + (humanCurated != null ? humanCurated.hashCode() : 0);
		result = 31 * result + (mouseCurated != null ? mouseCurated.hashCode() : 0);
		result = 31 * result + (mgiPredicted != null ? mgiPredicted.hashCode() : 0);
		result = 31 * result + (impcPredicted != null ? impcPredicted.hashCode() : 0);
		result = 31 * result + (mgiPredictedKnownGene != null ? mgiPredictedKnownGene.hashCode() : 0);
		result = 31 * result + (impcPredictedKnownGene != null ? impcPredictedKnownGene.hashCode() : 0);
		result = 31 * result + (mgiNovelPredictedInLocus != null ? mgiNovelPredictedInLocus.hashCode() : 0);
		result = 31 * result + (impcNovelPredictedInLocus != null ? impcNovelPredictedInLocus.hashCode() : 0);
		result = 31 * result + (annotationTermId != null ? annotationTermId.hashCode() : 0);
		result = 31 * result + (annotationTermName != null ? annotationTermName.hashCode() : 0);
		result = 31 * result + (name != null ? name.hashCode() : 0);
		result = 31 * result + (accession != null ? accession.hashCode() : 0);
		result = 31 * result + (expName != null ? expName.hashCode() : 0);
		result = 31 * result + (largeThumbnailFilePath != null ? largeThumbnailFilePath.hashCode() : 0);
		result = 31 * result + (smallThumbnailFilePath != null ? smallThumbnailFilePath.hashCode() : 0);
		result = 31 * result + (inferredMaTermId != null ? inferredMaTermId.hashCode() : 0);
		result = 31 * result + (inferredMaTermName != null ? inferredMaTermName.hashCode() : 0);
		result = 31 * result + (annotatedHigherLevelMaTermId != null ? annotatedHigherLevelMaTermId.hashCode() : 0);
		result = 31 * result + (annotatedHigherLevelMaTermName != null ? annotatedHigherLevelMaTermName.hashCode() : 0);
		result = 31 * result + (annotatedHigherLevelMpTermId != null ? annotatedHigherLevelMpTermId.hashCode() : 0);
		result = 31 * result + (annotatedHigherLevelMpTermName != null ? annotatedHigherLevelMpTermName.hashCode() : 0);
		result = 31 * result + (annotatedOrInferredHigherLevelMaTermName != null ? annotatedOrInferredHigherLevelMaTermName.hashCode() : 0);
		result = 31 * result + (annotatedOrInferredHigherLevelMaTermId != null ? annotatedOrInferredHigherLevelMaTermId.hashCode() : 0);
		result = 31 * result + (symbol != null ? symbol.hashCode() : 0);
		result = 31 * result + (sangerSymbol != null ? sangerSymbol.hashCode() : 0);
		result = 31 * result + (geneName != null ? geneName.hashCode() : 0);
		result = 31 * result + (subtype != null ? subtype.hashCode() : 0);
		result = 31 * result + (geneSynonyms != null ? geneSynonyms.hashCode() : 0);
		result = 31 * result + (alleleName != null ? alleleName.hashCode() : 0);
		result = 31 * result + (maTermId != null ? maTermId.hashCode() : 0);
		result = 31 * result + (maTermName != null ? maTermName.hashCode() : 0);
		result = 31 * result + (mpTermId != null ? mpTermId.hashCode() : 0);
		result = 31 * result + (mpTermName != null ? mpTermName.hashCode() : 0);
		result = 31 * result + (expNameExp != null ? expNameExp.hashCode() : 0);
		result = 31 * result + (symbolGene != null ? symbolGene.hashCode() : 0);
		result = 31 * result + (topLevel != null ? topLevel.hashCode() : 0);
		result = 31 * result + (alleleSymbol != null ? alleleSymbol.hashCode() : 0);
		result = 31 * result + (alleleId != null ? alleleId.hashCode() : 0);
		result = 31 * result + (strainName != null ? strainName.hashCode() : 0);
		result = 31 * result + (strainId != null ? strainId.hashCode() : 0);
		result = 31 * result + (pipelineName != null ? pipelineName.hashCode() : 0);
		result = 31 * result + (pipelineStableId != null ? pipelineStableId.hashCode() : 0);
		result = 31 * result + (pipelineStableKey != null ? pipelineStableKey.hashCode() : 0);
		result = 31 * result + (procedureName != null ? procedureName.hashCode() : 0);
		result = 31 * result + (procedureStableId != null ? procedureStableId.hashCode() : 0);
		result = 31 * result + (procedureStableKey != null ? procedureStableKey.hashCode() : 0);
		result = 31 * result + (parameterName != null ? parameterName.hashCode() : 0);
		result = 31 * result + (parameterStableId != null ? parameterStableId.hashCode() : 0);
		result = 31 * result + (parameterStableKey != null ? parameterStableKey.hashCode() : 0);
		return result;
	}

}
