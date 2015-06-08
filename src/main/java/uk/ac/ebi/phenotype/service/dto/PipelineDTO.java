package uk.ac.ebi.phenotype.service.dto;

import org.apache.solr.client.solrj.beans.Field;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class PipelineDTO {

	public static final String PIPELINE_ID = ObservationDTO.PIPELINE_ID;
	public static final String PIPELINE_STABLE_ID = ObservationDTO.PIPELINE_STABLE_ID;
	public static final String PIPELINE_STABLE_KEY = "pipeline_stable_key";
	public static final String PIPELINE_NAME = ObservationDTO.PIPELINE_NAME;

	public static final String PROCEDURE_ID = ObservationDTO.PROCEDURE_ID;
	public static final String PROCEDURE_STABLE_ID = ObservationDTO.PROCEDURE_STABLE_ID;
	public static final String PROCEDURE_STABLE_KEY = "procedure_stable_key";
	public static final String PROCEDURE_NAME = ObservationDTO.PROCEDURE_NAME;
	public static final String PROCEDURE_NAME_ID = "proc_name_id";

	public static final String PROCEDURE_PARAMETER_STABLE_ID = "proc_param_stable_id";
	public static final String PROCEDURE_PARAMETER_NAME = "proc_param_name";

	public static final String PARAMETER_ID = ObservationDTO.PARAMETER_ID;
	public static final String PARAMETER_STABLE_ID = ObservationDTO.PARAMETER_STABLE_ID;
	public static final String PARAMETER_STABLE_KEY = "parameter_stable_key";
	public static final String PARAMETER_NAME = ObservationDTO.PARAMETER_NAME;

	private static final String MAPPED_PROCEDURE_NAME = "mapped_procedure_name";

	private static final String PIPE_PROC_SID = "pipe_proc_sid";
	private static final String ID_ID_ID = "ididid";

	public static final String MGI_ACCESSION_ID = GeneDTO.MGI_ACCESSION_ID;
	public static final String MARKER_TYPE = GeneDTO.MARKER_TYPE;
	public static final String MARKER_SYMBOL = GeneDTO.MARKER_SYMBOL;
	public static final String MARKER_SYNONYM = GeneDTO.MARKER_SYNONYM;
	public static final String MARKER_NAME = GeneDTO.MARKER_NAME;
	public static final String HUMAN_GENE_SYMBOL = GeneDTO.HUMAN_GENE_SYMBOL;
	public static final String STATUS = GeneDTO.STATUS;
	public static final String IMITS_PHENOTYPE_STARTED = GeneDTO.IMITS_PHENOTYPE_STARTED;
	public static final String IMITS_PHENOTYPE_COMPLETE = GeneDTO.IMITS_PHENOTYPE_COMPLETE;
	public static final String IMITS_PHENOTYPE_STATUS = GeneDTO.IMITS_PHENOTYPE_STATUS;
	public static final String LATEST_PRODUCTION_CENTRE = GeneDTO.LATEST_PRODUCTION_CENTRE;
	public static final String LATEST_PHENOTYPING_CENTRE = GeneDTO.LATEST_PHENOTYPING_CENTRE;
	public static final String LATEST_PHENOTYPE_STATUS = GeneDTO.LATEST_PHENOTYPE_STATUS;
	public static final String LEGACY_PHENOTYPE_STATUS = GeneDTO.LEGACY_PHENOTYPE_STATUS;
	public static final String ALLELE_NAME = GeneDTO.ALLELE_NAME;

	public static final String MP_ID = MpDTO.MP_ID;
	public static final String MP_TERM = MpDTO.MP_TERM;
	public static final String MP_TERM_SYNONYM = MpDTO.MP_TERM_SYNONYM;
	public static final String ONTOLOGY_SUBSET = MpDTO.ONTOLOGY_SUBSET;
	public static final String TOP_LEVEL_MP_ID = MpDTO.TOP_LEVEL_MP_ID;
	public static final String TOP_LEVEL_MP_TERM = MpDTO.TOP_LEVEL_MP_TERM;
	public static final String TOP_LEVEL_MP_TERM_SYNONYM = MpDTO.TOP_LEVEL_MP_TERM_SYNONYM;
	public static final String INTERMEDIATE_MP_ID = MpDTO.INTERMEDIATE_MP_ID;
	public static final String INTERMEDIATE_MP_TERM = MpDTO.INTERMEDIATE_MP_TERM;
	public static final String INTERMEDIATE_MP_TERM_SYNONYM = MpDTO.INTERMEDIATE_MP_TERM_SYNONYM;
	public static final String CHILD_MP_ID = MpDTO.CHILD_MP_ID;
	public static final String CHILD_MP_TERM = MpDTO.CHILD_MP_TERM;
	public static final String CHILD_MP_TERM_SYNONYM = MpDTO.CHILD_MP_TERM_SYNONYM;
	public static final String HP_ID = MpDTO.HP_ID;
	public static final String HP_TERM = MpDTO.HP_TERM;
	public static final String INFERRED_MA_ID = MpDTO.INFERRED_MA_ID;
	public static final String INFERRED_MA_TERM_SYNONYM = MpDTO.INFERRED_MA_TERM_SYNONYM;
	public static final String INFERRED_SELECTED_TOP_LEVEL_MA_ID = MpDTO.INFERRED_SELECTED_TOP_LEVEL_MA_ID;
	public static final String INFERRED_SELECTED_TOP_LEVEL_MA_TERM = MpDTO.INFERRED_SELECTED_TOP_LEVEL_MA_TERM;
	public static final String INFERRED_SELECTED_TOP_LEVEL_MA_TERM_SYNONYM = MpDTO.INFERRED_SELECTED_TOP_LEVEL_MA_TERM_SYNONYM;
	public static final String INFERRED_CHILD_MA_ID = MpDTO.INFERRED_CHILD_MA_ID;
	public static final String INFERRED_CHILD_MA_TERM = MpDTO.INFERRED_CHILD_MA_TERM;
	public static final String INFERRED_CHILD_MA_TERM_SYNONYM = MpDTO.INFERRED_CHILD_MA_TERM_SYNONYM;
	public static final String ABNORMAL_MA_ID = "abnormal_ma_id";
	public static final String ABNORMAL_MA_NAME = "abnormal_ma_name";

	//
	// IMPReSS fields
	//

	@Field(PARAMETER_ID)
	private int parameterId;

	@Field(PARAMETER_STABLE_ID)
	private String parameterStableId;

	@Field(PARAMETER_NAME)
	private String parameterName;

	@Field(PROCEDURE_ID)
	private List<Integer> procedureId;

	@Field(PROCEDURE_STABLE_ID)
	private List<String> procedureStableId;

	@Field(PROCEDURE_NAME)
	private List<String> procedureName;

	@Field(MAPPED_PROCEDURE_NAME)
	private List<String> mappedProcedureName;

	@Field(PIPELINE_ID)
	private int pipelineId;

	@Field(PIPELINE_STABLE_ID)
	private List<String> pipelineStableId;

	@Field(PIPELINE_STABLE_KEY)
	private List<Integer> pipelineStableKey;

	@Field(PIPELINE_NAME)
	private List<String> pipelineName;

	@Field(PIPE_PROC_SID)
	private List<String> pipeProcId;

	@Field(PROCEDURE_STABLE_KEY)
	private List<Integer> procedureStableKey;

	@Field(PROCEDURE_NAME_ID)
	private List<String> procedureNameId;

	@Field(PROCEDURE_PARAMETER_STABLE_ID)
	private List<String> procedureParamStableId;

	@Field(PROCEDURE_PARAMETER_NAME)
	private List<String> procedureParamName;

	@Field(PARAMETER_STABLE_KEY)
	private String parameterStableKey;

	@Field(ID_ID_ID)
	private String ididid;

	//
	// Gene fields
	//

	@Field(MGI_ACCESSION_ID)
	private List<String> mgiAccession;

	@Field(MARKER_TYPE)
	private List<String> markerType;

	@Field(MARKER_SYMBOL)
	private List<String> markerSymbol;

	@Field(MARKER_SYNONYM)
	private List<String> markerSynonyms;

	@Field(MARKER_NAME)
	private List<String> markerName;

	@Field(HUMAN_GENE_SYMBOL)
	private List<String> humanGeneSymbol;

	// status name from Bill Skarnes and used at EBI
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
	private List<String> latestPhenotypingStatus;

	@Override
	public String toString() {
		return "PipelineDTO [parameterId=" + parameterId
				+ ", parameterStableId=" + parameterStableId
				+ ", parameterName=" + parameterName + ", procedureId="
				+ procedureId + ", procedureStableId=" + procedureStableId
				+ ", procedureName=" + procedureName + ", mappedProcedureName="
				+ mappedProcedureName + ", pipelineId=" + pipelineId
				+ ", pipelineStableId=" + pipelineStableId
				+ ", pipelineStableKey=" + pipelineStableKey
				+ ", pipelineName=" + pipelineName + ", pipeProcId="
				+ pipeProcId + ", procedureStableKey=" + procedureStableKey
				+ ", procedureNameId=" + procedureNameId
				+ ", procedureParamStableId=" + procedureParamStableId
				+ ", procedureParamName=" + procedureParamName
				+ ", parameterStableKey=" + parameterStableKey + ", ididid="
				+ ididid + ", mgiAccession=" + mgiAccession + ", markerType="
				+ markerType + ", markerSymbol=" + markerSymbol
				+ ", markerSynonyms=" + markerSynonyms + ", markerName="
				+ markerName + ", humanGeneSymbol=" + humanGeneSymbol
				+ ", status=" + status + ", imitsPhenotypeStarted="
				+ imitsPhenotypeStarted + ", imitsPhenotypeComplete="
				+ imitsPhenotypeComplete + ", imitsPhenotypeStatus="
				+ imitsPhenotypeStatus + ", latestProductionCentre="
				+ latestProductionCentre + ", latestPhenotypingCentre="
				+ latestPhenotypingCentre + ", latestPhenotypingStatus="
				+ latestPhenotypingStatus + ", legacyPhenotypingStatus="
				+ legacyPhenotypingStatus + ", alleleName=" + alleleName
				+ ", mpId=" + mpId + ", mpTerm=" + mpTerm + ", mpTermSynonym="
				+ mpTermSynonym + ", ontologySubset=" + ontologySubset
				+ ", topLevelMpId=" + topLevelMpId + ", topLevelMpTerm="
				+ topLevelMpTerm + ", topLevelMpTermSynonym="
				+ topLevelMpTermSynonym + ", intermediateMpId="
				+ intermediateMpId + ", intermediateMpTerm="
				+ intermediateMpTerm + ", intermediateMpTermSynonym="
				+ intermediateMpTermSynonym + ", childMpId=" + childMpId
				+ ", childMpTerm=" + childMpTerm + ", childMpTermSynonym="
				+ childMpTermSynonym + ", hpId=" + hpId + ", hpTerm=" + hpTerm
				+ ", inferredMaId=" + inferredMaId + ", inferredMaTerm="
				+ inferredMaTerm + ", inferredMaTermSynonym="
				+ inferredMaTermSynonym + ", selectedTopLevelMaId="
				+ selectedTopLevelMaId + ", inferredSelectedTopLevelMaTerm="
				+ inferredSelectedTopLevelMaTerm
				+ ", inferredSelectedToLevelMaTermSynonym="
				+ inferredSelectedToLevelMaTermSynonym + ", inferredChildMaId="
				+ inferredChildMaId + ", inferredChildMaTerm="
				+ inferredChildMaTerm + ", inferredChildMaTermSynonym="
				+ inferredChildMaTermSynonym
				+ ", inferredSelectedTopLevelMaId="
				+ inferredSelectedTopLevelMaId + "]";
	}


	@Field(LEGACY_PHENOTYPE_STATUS)
	private List<String> legacyPhenotypingStatus;

	@Field(ALLELE_NAME)
	private List<String> alleleName;

	//
	// MP fields
	//

	@Field(MP_ID)
	private List<String> mpId;

	@Field(MP_TERM)
	private List<String> mpTerm;

	@Field(MP_TERM_SYNONYM)
	private List<String> mpTermSynonym;

	@Field(ONTOLOGY_SUBSET)
	private List<String> ontologySubset;

	@Field(TOP_LEVEL_MP_ID)
	private List<String> topLevelMpId;

	@Field(TOP_LEVEL_MP_TERM)
	private List<String> topLevelMpTerm;

	@Field(TOP_LEVEL_MP_TERM_SYNONYM)
	private List<String> topLevelMpTermSynonym;

	@Field(INTERMEDIATE_MP_ID)
	private List<String> intermediateMpId;

	@Field(INTERMEDIATE_MP_TERM)
	private List<String> intermediateMpTerm;

	@Field(INTERMEDIATE_MP_TERM_SYNONYM)
	private List<String> intermediateMpTermSynonym;

	@Field(CHILD_MP_ID)
	private List<String> childMpId;

	@Field(CHILD_MP_TERM)
	private List<String> childMpTerm;

	@Field(CHILD_MP_TERM_SYNONYM)
	private List<String> childMpTermSynonym;

	@Field(HP_ID)
	private List<String> hpId;

	@Field(HP_TERM)
	private List<String> hpTerm;

	@Field(INFERRED_MA_ID)
	private List<String> inferredMaId;

	@Field(INFERRED_CHILD_MA_TERM)
	private List<String> inferredMaTerm;

	@Field(INFERRED_MA_TERM_SYNONYM)
	private List<String> inferredMaTermSynonym;

	@Field(INFERRED_SELECTED_TOP_LEVEL_MA_ID)
	private List<String> selectedTopLevelMaId;

	@Field(INFERRED_SELECTED_TOP_LEVEL_MA_TERM)
	private List<String> inferredSelectedTopLevelMaTerm;

	@Field(INFERRED_SELECTED_TOP_LEVEL_MA_TERM_SYNONYM)
	private List<String> inferredSelectedToLevelMaTermSynonym;

	@Field(INFERRED_CHILD_MA_ID)
	private List<String> inferredChildMaId;

	@Field(INFERRED_CHILD_MA_TERM)
	private List<String> inferredChildMaTerm;

	@Field(INFERRED_CHILD_MA_TERM_SYNONYM)
	private List<String> inferredChildMaTermSynonym;

	@Field(INFERRED_SELECTED_TOP_LEVEL_MA_ID)
	private List<String> inferredSelectedTopLevelMaId;
	
	@Field(ABNORMAL_MA_ID)
	private String abnormalMaTermId;
	
	@Field(ABNORMAL_MA_NAME)
	private String abnormalMaName;


	public String getAbnormalMaName() {
		return abnormalMaName;
	}


	public void setAbnormalMaName(String abnormalMaName) {
		this.abnormalMaName = abnormalMaName;
	}


	public List<String> getMpId() {

		return mpId;
	}


	public void setMpId(List<String> mpId) {

		this.mpId = mpId;
	}


	public List<String> getMpTerm() {

		return mpTerm;
	}


	public void setMpTerm(List<String> mpTerm) {

		this.mpTerm = mpTerm;
	}


	public List<String> getMpTermSynonym() {

		return mpTermSynonym;
	}


	public void setMpTermSynonym(List<String> mpTermSynonym) {

		this.mpTermSynonym = mpTermSynonym;
	}


	public List<String> getOntologySubset() {

		return ontologySubset;
	}


	public void setOntologySubset(List<String> ontologySubset) {

		this.ontologySubset = ontologySubset;
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


	public List<String> getTopLevelMpTermSynonym() {

		return topLevelMpTermSynonym;
	}


	public void setTopLevelMpTermSynonym(List<String> topLevelMpTermSynonym) {

		this.topLevelMpTermSynonym = topLevelMpTermSynonym;
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


	public List<String> getIntermediateMpTermSynonym() {

		return intermediateMpTermSynonym;
	}


	public void setIntermediateMpTermSynonym(List<String> intermediateMpTermSynonym) {

		this.intermediateMpTermSynonym = intermediateMpTermSynonym;
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


	public List<String> getChildMpTermSynonym() {

		return childMpTermSynonym;
	}


	public void setChildMpTermSynonym(List<String> childMpTermSynonym) {

		this.childMpTermSynonym = childMpTermSynonym;
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


	public List<String> getSelectedTopLevelMaId() {

		return selectedTopLevelMaId;
	}


	public void setSelectedTopLevelMaId(List<String> selectedTopLevelMaId) {

		this.selectedTopLevelMaId = selectedTopLevelMaId;
	}


	public List<String> getInferredSelectedTopLevelMaTerm() {

		return inferredSelectedTopLevelMaTerm;
	}


	public void setInferredSelectedTopLevelMaTerm(List<String> inferredSelectedTopLevelMaTerm) {

		this.inferredSelectedTopLevelMaTerm = inferredSelectedTopLevelMaTerm;
	}


	public List<String> getInferredSelectedToLevelMaTermSynonym() {

		return inferredSelectedToLevelMaTermSynonym;
	}


	public void setInferredSelectedToLevelMaTermSynonym(List<String> inferredSelectedToLevelMaTermSynonym) {

		this.inferredSelectedToLevelMaTermSynonym = inferredSelectedToLevelMaTermSynonym;
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


	public List<String> getProcedureParamStableId() {

		return procedureParamStableId;
	}


	public void setProcedureParamStableId(List<String> procedureParamStableId) {

		this.procedureParamStableId = procedureParamStableId;
	}


	public List<String> getProcedureParamName() {

		return procedureParamName;
	}


	public void setProcedureParamName(List<String> procedureParamName) {

		this.procedureParamName = procedureParamName;
	}


	public String getIdidid() {

		return ididid;
	}


	public void setIdidid(String ididid) {

		this.ididid = ididid;
	}


	public List<String> getMarkerType() {

		return markerType;
	}


	public void setMarkerType(List<String> markerType) {

		this.markerType = markerType;
	}


	public List<String> getMarkerSymbol() {

		return markerSymbol;
	}


	public void setMarkerSymbol(List<String> markerSymbol) {

		this.markerSymbol = markerSymbol;
	}


	public List<String> getMarkerSynonyms() {

		return markerSynonyms;
	}


	public void setMarkerSynonyms(List<String> markerSynonyms) {

		this.markerSynonyms = markerSynonyms;
	}


	public List<String> getMarkerName() {

		return markerName;
	}


	public void setMarkerName(List<String> markerName) {

		this.markerName = markerName;
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


	public List<String> getLatestPhenotypingStatus() {

		return latestPhenotypingStatus;
	}


	public void setLatestPhenotypingStatus(List<String> latestPhenotypingStatus) {

		this.latestPhenotypingStatus = latestPhenotypingStatus;
	}


	public List<String> getLegacyPhenotypingStatus() {

		return legacyPhenotypingStatus;
	}


	public void setLegacyPhenotypingStatus(List<String> legacyPhenotypingStatus) {

		this.legacyPhenotypingStatus = legacyPhenotypingStatus;
	}


	public List<String> getAlleleName() {

		return alleleName;
	}


	public void setAlleleName(List<String> alleleName) {

		this.alleleName = alleleName;
	}


	public String getParameterStableKey() {

		return parameterStableKey;
	}


	public List<String> getProcedureNameId() {

		return procedureNameId;
	}


	public void setProcedureNameId(List<String> procedureNameId) {

		this.procedureNameId = procedureNameId;
	}


	public List<Integer> getProcedureStableKey() {

		return procedureStableKey;
	}


	public void setProcedureStableKey(List<Integer> procedureStableKey) {

		this.procedureStableKey = procedureStableKey;
	}


	public List<Integer> getPipelineStableKey() {

		return pipelineStableKey;
	}


	public void setPipelineStableKey(List<Integer> pipelineStableKey) {

		this.pipelineStableKey = pipelineStableKey;
	}


	public List<String> getPipeProcId() {

		return pipeProcId;
	}


	public void setPipeProcId(List<String> pipeProcId) {

		this.pipeProcId = pipeProcId;
	}


	public int getParameterId() {

		return parameterId;
	}


	public void setParameterId(int parameterId) {

		this.parameterId = parameterId;
	}


	public String getParameterStableId() {

		return parameterStableId;
	}


	public void setParameterStableId(String parameterStableId) {

		this.parameterStableId = parameterStableId;
	}


	public String getParameterName() {

		return parameterName;
	}


	public void setParameterName(String parameterName) {

		this.parameterName = parameterName;
	}


	public List<Integer> getProcedureId() {

		return procedureId;
	}


	public void setProcedureId(List<Integer> procedureId) {

		this.procedureId = procedureId;
	}


	public List<String> getProcedureStableId() {

		return procedureStableId;
	}


	public void setProcedureStableId(List<String> procedureStableId) {

		this.procedureStableId = procedureStableId;
	}


	public List<String> getProcedureName() {

		return procedureName;
	}


	public void setProcedureName(List<String> procedureName) {

		this.procedureName = procedureName;
	}


	public List<String> getMappedProcedureName() {

		return mappedProcedureName;
	}


	public void setMappedProcedureName(List<String> mappedProcedureName) {

		this.mappedProcedureName = mappedProcedureName;
	}


	public List<String> getPipelineName() {

		return pipelineName;
	}


	public void setPipelineName(List<String> pipelineName) {

		this.pipelineName = pipelineName;
	}


	public int getPipelineId() {

		return pipelineId;
	}


	public void setPipelineId(int pipelineId) {

		this.pipelineId = pipelineId;
	}


	public List<String> getPipelineStableId() {

		return pipelineStableId;
	}


	public void setPipelineStableId(List<String> pipelineStableId) {

		this.pipelineStableId = pipelineStableId;
	}


	public List<String> getMgiAccession() {

		return mgiAccession;
	}


	public void setMgiAccession(List<String> mgiAccession) {

		this.mgiAccession = mgiAccession;
	}


	public void addMgiAccession(String mgiAccession) {

		if (this.mgiAccession == null) {
			this.mgiAccession = new ArrayList<>();
		}
		this.mgiAccession.add(mgiAccession);
	}


	public void addProcedureId(int procId) {

		if (this.procedureId == null) {
			this.procedureId = new ArrayList<Integer>();
		}
		this.procedureId.add(procId);
	}


	public void addProcedureName(String procedureName) {

		if (this.procedureName == null) {
			this.procedureName = new ArrayList<String>();
		}
		this.procedureName.add(procedureName);

	}


	public void addProcedureStableId(String procedureStableId) {

		if (this.procedureStableId == null) {
			this.procedureStableId = new ArrayList<String>();
		}
		this.procedureStableId.add(procedureStableId);
	}


	public void addProcedureStableKey(Integer procedureStableKey) {

		if (this.procedureStableKey == null) {
			this.procedureStableKey = new ArrayList<Integer>();
		}
		this.procedureStableKey.add(procedureStableKey);

	}


	public void addProcedureNameId(String procNameId) {

		if (this.procedureNameId == null) {
			this.procedureNameId = new ArrayList<String>();
		}
		this.procedureNameId.add(procNameId);

	}


	public void addMappedProcedureName(String impcProcedureFromSanger) {

		if (this.mappedProcedureName == null) {
			this.mappedProcedureName = new ArrayList<String>();
		}
		this.mappedProcedureName.add(impcProcedureFromSanger);
	}


	public void addProcParamStableId(String procParamStableId) {

		if (this.procedureParamStableId == null) {
			this.procedureParamStableId = new ArrayList<String>();
		}
		this.procedureParamStableId.add(procParamStableId);

	}


	public void addProcParamName(String procParamName) {

		if (this.procedureParamName == null) {
			this.procedureParamName = new ArrayList<String>();
		}
		this.procedureParamName.add(procParamName);
	}


	public void setParameterStableKey(String paramStableKey) {

		this.parameterStableKey = paramStableKey;

	}


	public void setIdIdId(String ididid) {

		this.ididid = ididid;

	}


	public void addMarkerType(String markerType) {

		if (this.markerType == null) {
			this.markerType = new ArrayList<String>();
		}
		this.markerType.add(markerType);

	}


	public void addMarkerSymbol(String markerSymbol2) {

		if (this.markerSymbol == null) {
			this.markerSymbol = new ArrayList<String>();
		}
		this.markerSymbol.add(markerSymbol2);

	}


	public void addMarkerSynonym(List<String> markerSynonym) {

		if (this.markerSynonyms == null) {
			this.markerSynonyms = new ArrayList<String>();
		}
		this.markerSynonyms.addAll(markerSynonym);

	}


	public void addMarkerName(String markerName) {

		if (this.markerName == null) {
			this.markerName = new ArrayList<String>();
		}
		this.markerName.add(markerName);

	}


	public void addHumanGeneSymbol(List<String> humanGeneSymbol) {

		if (this.humanGeneSymbol == null) {
			this.humanGeneSymbol = new ArrayList<String>();
		}
		this.humanGeneSymbol.addAll(humanGeneSymbol);

	}


	public void addStatus(String status) {

		if (this.status == null) {
			this.status = new ArrayList<String>();
		}
		this.status.add(status);

	}


	public void addImitsPhenotypeStarted(String imitsPhenotypeStarted) {

		if (this.imitsPhenotypeStarted == null) {
			this.imitsPhenotypeStarted = new ArrayList<String>();
		}
		this.imitsPhenotypeStarted.add(imitsPhenotypeStarted);
	}


	public void addImitsPhenotypeComplete(String imitsPhenotypeComplete) {

		if (this.imitsPhenotypeComplete == null) {
			this.imitsPhenotypeComplete = new ArrayList<String>();
		}
		this.imitsPhenotypeComplete.add(imitsPhenotypeComplete);

	}


	public void addImitsPhenotypeStatus(String imitsPhenotypeStatus) {

		if (this.imitsPhenotypeStatus == null) {
			this.imitsPhenotypeStatus = new ArrayList<String>();
		}
		this.imitsPhenotypeStatus.add(imitsPhenotypeStatus);

	}


	public void addLatestProductionCentre(List<String> latestProductionCentre) {

		if (this.latestProductionCentre == null) {
			this.latestProductionCentre = new ArrayList<String>();
		}
		this.latestProductionCentre.addAll(latestProductionCentre);

	}


	public void addLatestPhenotypingCentre(List<String> latestPhenotypingCentre) {

		if (this.latestPhenotypingCentre == null) {
			this.latestPhenotypingCentre = new ArrayList<String>();
		}
		this.latestPhenotypingCentre.addAll(latestPhenotypingCentre);
	}


	public void addLatestPhenotypingCentre(String latestPhenotypingStatus) {

		if (this.latestPhenotypingStatus == null) {
			this.latestPhenotypingStatus = new ArrayList<String>();
		}
		this.latestPhenotypingStatus.add(latestPhenotypingStatus);
	}


	public void addLegacyPhenotypingStatus(String latestPhenotypingStatus) {

		if (this.latestPhenotypingStatus == null) {
			this.latestPhenotypingStatus = new ArrayList<String>();
		}
		this.latestPhenotypingStatus.add(latestPhenotypingStatus);

	}


	public void addAlleleName(List<String> alleleName) {

		if (this.alleleName == null) {
			this.alleleName = new ArrayList<>();
		}
		this.alleleName.addAll(alleleName);

	}


	public void addMpId(String mpTermId) {

		if (this.mpId == null) {
			this.mpId = new ArrayList<>();
		}
		this.mpId.add(mpTermId);

	}


	public void addMpTerm(String mpTerm) {

		if (this.mpTerm == null) {
			this.mpTerm = new ArrayList<>();
		}
		this.mpTerm.add(mpTerm);

	}


	public void addMpTermSynonym(List<String> mpTermSynonym) {

		if (this.mpTermSynonym == null) {
			this.mpTermSynonym = new ArrayList<>();
		}
		this.mpTermSynonym.addAll(mpTermSynonym);
		this.mpTermSynonym = new ArrayList<>(new HashSet<>(this.mpTermSynonym));
	}


	public void addOntologySubset(List<String> ontologySubset) {

		if (this.ontologySubset == null) {
			this.ontologySubset = new ArrayList<>();
		}
		this.ontologySubset.addAll(ontologySubset);
		this.ontologySubset = new ArrayList<>(new HashSet<>(this.ontologySubset));
	}


	public void addTopLevelMpId(List<String> topLevelMpTermId) {
		if (this.topLevelMpId == null) {
			this.topLevelMpId = new ArrayList<>();
		}
		this.topLevelMpId.addAll(topLevelMpTermId);
		this.topLevelMpId = new ArrayList<>(new HashSet<>(this.topLevelMpId));
	}


	public void addTopLevelMpTerm(List<String> topLevelMpTerm) {
		if (this.topLevelMpTerm == null) {
			this.topLevelMpTerm = new ArrayList<>();
		}
		this.topLevelMpTerm.addAll(topLevelMpTerm);
		this.topLevelMpTerm = new ArrayList<>(new HashSet<>(this.topLevelMpTerm));
	}


	public void addTopLevelMpTermSynonym(List<String> topLevelMpTermSynonym) {
		if (this.topLevelMpTermSynonym == null) {
			this.topLevelMpTermSynonym = new ArrayList<>();
		}
		this.topLevelMpTermSynonym.addAll(topLevelMpTermSynonym);
		this.topLevelMpTermSynonym = new ArrayList<>(new HashSet<>(this.topLevelMpTermSynonym));
	}


	public void addIntermediateMpId(List<String> intermediateMpId) {
		if (this.intermediateMpId == null) {
			this.intermediateMpId = new ArrayList<>();
		}
		this.intermediateMpId.addAll(intermediateMpId);
		this.intermediateMpId = new ArrayList<>(new HashSet<>(this.intermediateMpId));
	}


	public void addIntermediateMpTerm(List<String> intermediateMpTerm) {
		if (this.intermediateMpTerm == null) {
			this.intermediateMpTerm = new ArrayList<>();
		}
		this.intermediateMpTerm.addAll(intermediateMpTerm);
		this.intermediateMpTerm = new ArrayList<>(new HashSet<>(this.intermediateMpTerm));
	}


	public void addIntermediateMpTermSynonym(List<String> intermediateMpTermSynonym) {

		if (this.intermediateMpTermSynonym == null) {
			this.intermediateMpTermSynonym = new ArrayList<>();
		}
		this.intermediateMpTermSynonym.addAll(intermediateMpTermSynonym);
		this.intermediateMpTermSynonym = new ArrayList<>(new HashSet<>(this.intermediateMpTermSynonym));
	}


	public void addChildMpId(List<String> childMpId) {

		if (this.childMpId == null) {
			this.childMpId = new ArrayList<>();
		}
		this.childMpId.addAll(childMpId);
		this.childMpId = new ArrayList<>(new HashSet<>(this.childMpId));
	}


	public void addChildMpTerm(List<String> childMpTerm) {

		if (this.childMpTerm == null) {
			this.childMpTerm = new ArrayList<>();
		}
		this.childMpTerm.addAll(childMpTerm);
		this.childMpTerm = new ArrayList<>(new HashSet<>(this.childMpTerm));
	}


	public void addChildMpTermSynonym(List<String> childMpTermSynonym) {

		if (this.childMpTermSynonym == null) {
			this.childMpTermSynonym = new ArrayList<>();
		}
		this.childMpTermSynonym.addAll(childMpTermSynonym);
		this.childMpTermSynonym = new ArrayList<>(new HashSet<>(this.childMpTermSynonym));
	}


	public void addHpId(List<String> hpId) {
		if (this.hpId == null) {
			this.hpId = new ArrayList<>();
		}
		this.hpId.addAll(hpId);
	}


	public void addHpTerm(List<String> hpTerm) {

		if (this.hpTerm == null) {
			this.hpTerm = new ArrayList<>();
		}
		this.hpTerm.addAll(hpTerm);
		
	}


	public void addInferredMaId(List<String> inferredChildMaId) {

		if (this.inferredChildMaId == null) {
			this.inferredChildMaId = new ArrayList<>();
		}
		this.inferredChildMaId.addAll(inferredChildMaId);
		
	}


	public void addInferredMaTerm(List<String> inferredChildMaTerm) {

		if (this.inferredChildMaTerm == null) {
			this.inferredChildMaTerm = new ArrayList<>();
		}
		this.inferredChildMaTerm.addAll(inferredChildMaTerm);
		
	}


	public void addInferredMaTermSynonym(List<String> inferredChildMaTermSynonym) {

		if (this.inferredChildMaTermSynonym == null) {
			this.inferredChildMaTermSynonym = new ArrayList<>();
		}
		this.inferredChildMaTermSynonym.addAll(inferredChildMaTermSynonym);
		
		
	}


	public void addInferredSelectedTopLevelMaId(List<String> inferredSelectedTopLevelMaId) {

		if (this.inferredSelectedTopLevelMaId == null) {
			this.inferredSelectedTopLevelMaId = new ArrayList<>();
		}
		this.inferredSelectedTopLevelMaId.addAll(inferredSelectedTopLevelMaId);
		
		
	}


	public void addInferredSelectedTopLevelMaTerm(List<String> inferredSelectedTopLevelMaTerm) {

		if (this.inferredSelectedTopLevelMaTerm == null) {
			this.inferredSelectedTopLevelMaTerm = new ArrayList<>();
		}
		this.inferredSelectedTopLevelMaTerm.addAll(inferredSelectedTopLevelMaTerm);
		
	}


	public void addInferredSelectedToLevelMaTermSynonym(List<String> inferredSelectedTopLevelMaTermSynonym) {

		if (this.inferredSelectedToLevelMaTermSynonym== null) {
			this.inferredSelectedToLevelMaTermSynonym = new ArrayList<>();
		}
		this.inferredSelectedToLevelMaTermSynonym.addAll(inferredSelectedTopLevelMaTermSynonym);
		
	}


	public void addInferredChildMaId(List<String> inferredChildMaId) {

		if (this.inferredChildMaId== null) {
			this.inferredChildMaId = new ArrayList<>();
		}
		this.inferredChildMaId.addAll(inferredChildMaId);
		
	}


	public void addInferredChildMaTerm(List<String> inferredChildMaTerm) {

		if (this.inferredChildMaTerm== null) {
			this.inferredChildMaTerm = new ArrayList<>();
		}
		this.inferredChildMaTerm.addAll(inferredChildMaTerm);
		
	}


	public void addInferredChildMaTermSynonyms(List<String> inferredChildMaTermSynonym) {
		if (this.inferredChildMaTermSynonym== null) {
			this.inferredChildMaTermSynonym = new ArrayList<>();
		}
		this.inferredChildMaTermSynonym.addAll(inferredChildMaTermSynonym);
		
	}


	public void addPipelineName(String pipelineName) {

		if (this.pipelineName== null) {
			this.pipelineName = new ArrayList<>();
		}
		this.pipelineName.add(pipelineName);
		
	}


	public void addPipelineStableId(String pipelineStableId) {

		if (this.pipelineStableId== null) {
			this.pipelineStableId = new ArrayList<>();
		}
		this.pipelineStableId.add(pipelineStableId);
		
	}


	public void addPipelineStableKey(Integer pipelineStableKey) {
		if (this.pipelineStableKey== null) {
			this.pipelineStableKey = new ArrayList<>();
		}
		this.pipelineStableKey.add(pipelineStableKey);
		
	}


	public void addPipeProcId(String pipeProcId) {

		if (this.pipeProcId== null) {
			this.pipeProcId = new ArrayList<>();
		}
		this.pipeProcId.add(pipeProcId);
		
	}


	public void setAbnormalMaTermId(String abnormalMaTermId) {
		this.abnormalMaTermId=abnormalMaTermId;
		
	}


	public String getAbnormalMaTermId() {
		return abnormalMaTermId;
	}

}
