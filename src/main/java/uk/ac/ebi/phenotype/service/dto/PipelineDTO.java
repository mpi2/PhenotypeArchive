package uk.ac.ebi.phenotype.service.dto;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.beans.Field;

public class PipelineDTO {

	private static final String MAPPED_PROCEDIURE_NAME = "mapped_procedure_name";
	@Field(ObservationDTO.PARAMETER_ID)
	private int parameterId;
	// "parameter_id": 9,
	// "dataType": "pipeline",
	@Field(ObservationDTO.PARAMETER_STABLE_ID)
	private String parameterStableId;
	// "parameter_stable_id": "IMPC_HOU_001_001",
	// @Field(ObservationDTO.par)
	// private int parameterStableKey;
	// "parameter_stable_key": 4181,
	@Field(ObservationDTO.PARAMETER_NAME)
	private String parameterName;

	// "parameter_name": "Submitter ID",
	@Field(ObservationDTO.PROCEDURE_ID)
	private List<Integer> procedureId;
	// "procedure_id": 2,
	@Field(ObservationDTO.PROCEDURE_STABLE_ID)
	private List<String> procedureStableId;
	// "procedure_stable_id": "IMPC_HOU_001",
	// @Field()
	// private int procedureStableKey;
	// "procedure_stable_key": 173,
	@Field(ObservationDTO.PROCEDURE_NAME)
	private List<String> procedureName;
	// "procedure_name": "Housing and Husbandry",
	// "proc_name_id": "Housing and Husbandry___IMPC_HOU_001",
	@Field(MAPPED_PROCEDIURE_NAME)
	private List<String> mappedProcedureName;
	// "mapped_procedure_name": "Housing%20and%20Husbandry",
	// "proc_param_name": "Housing and Husbandry___Submitter ID",
	// "proc_param_stable_id": "IMPC_HOU_001___IMPC_HOU_001_001",
	@Field(ObservationDTO.PIPELINE_NAME)
	private String pipelineName;
	// "pipeline_name": "IMPC Pipeline",
	@Field("pipe_proc_sid")
	private String pipeProcId;

	@Field("pipeline_stable_key")
	private int pipelineStableKey;

	@Field("procedure_stable_key")
	private List<Integer> procedureStableKey;

	@Field("proc_name_id")
	private List<String> procedureNameId;

	// "pipe_proc_sid": "IMPC Pipeline___Housing and Husbandry___IMPC_HOU_001",
	@Field(ObservationDTO.PIPELINE_ID)
	private int pipelineId;
	// "pipeline_id": 1,
	// @Field()
	// private int pipelineStableKey;
	// "pipeline_stable_key": 7,
	@Field(ObservationDTO.PIPELINE_STABLE_ID)
	private String pipelineStableId;

	@Field("proc_param_stable_id")
	private List<String> procedureParamStableId;

	@Field("proc_param_name")
	private List<String> procedureParamName;
	// "pipeline_stable_id": "IMPC_001",
	// "ididid": "9_2_1",
	@Field("parameter_stable_key")
	private String parameterStableKey;
	@Field("ididid")
	private String ididid;

	@Field(GeneDTO.MGI_ACCESSION_ID)
	private List<String> mgiAccession;
	@Field(GeneDTO.MARKER_TYPE)
	private List<String> markerType;
	@Field(GeneDTO.MARKER_SYMBOL)
	private List<String> markerSymbol;
	@Field(GeneDTO.MARKER_SYNONYM)
	private List<String> markerSynonyms;
	@Field(GeneDTO.MARKER_NAME)
	private List<String> markerName;
	@Field(GeneDTO.HUMAN_GENE_SYMBOL)
	private List<String> humanGeneSymbol;
	@Field(GeneDTO.STATUS)
	private List<String> status;// status name from Bill Skarnes and used at EBI
								// -->

	@Field(GeneDTO.IMITS_PHENOTYPE_STARTED)
	private List<String> imitsPhenotypeStarted;

	@Field(GeneDTO.IMITS_PHENOTYPE_COMPLETE)
	private List<String> imitsPhenotypeComplete;
	@Field(GeneDTO.IMITS_PHENOTYPE_STATUS)
	private List<String> imitsPhenotypeStatus;
	@Field(GeneDTO.LATEST_PRODUCTION_CENTRE)
	private List<String> latestProductionCentre;
	@Field(GeneDTO.LATEST_PHENOTYPING_CENTRE)
	private List<String> latestPhenotypingCentre;
	@Field(GeneDTO.LATEST_PHENOTYPE_STATUS)
	private List<String> latestPhenotypingStatus;
	@Field(GeneDTO.LEGACY_PHENOTYPE_STATUS)
	private List<String> legacyPhenotypingStatus;
	@Field(GeneDTO.ALLELE_NAME)
	private List<String> alleleName;

	// <field column="mp_id" xpath="/response/result/doc/str[@name='mp_id']" />
	@Field(MpDTO.MP_ID)
	private List<String> mpId;
	// <field column="mp_term" xpath="/response/result/doc/str[@name='mp_term']"
	// />
	@Field(MpDTO.MP_TERM)
	private List<String> mpTerm;
	// <field column="mp_definition"
	// xpath="/response/result/doc/str[@name='mp_definition']" />
	
	// <field column="mp_term_synonym"
	// xpath="/response/result/doc/arr[@name='mp_term_synonym']/str" />
	@Field(MpDTO.MP_TERM_SYNONYM)
	private List<String> mpTermSynonym;
	// <field column="ontology_subset"
	// xpath="/response/result/doc/arr[@name='ontology_subset']/str" />
	@Field(MpDTO.ONTOLOGY_SUBSET)
	private List<String> ontologySubset;
	// <field column="top_level_mp_id"
	// xpath="/response/result/doc/arr[@name='top_level_mp_id']/str" />
	@Field(MpDTO.TOP_LEVEL_MP_ID)
	private List<String> topLevelMpId;
	// <field column="top_level_mp_term"
	// xpath="/response/result/doc/arr[@name='top_level_mp_term']/str" />
	@Field(MpDTO.TOP_LEVEL_MP_TERM)
	private List<String> topLevelMpTerm;
	// <field column="top_level_mp_term_synonym"
	// xpath="/response/result/doc/arr[@name='top_level_mp_term_synonym']/str"
	// />
	@Field(MpDTO.TOP_LEVEL_MP_TERM_SYNONYM)
	private List<String> topLevelMpTermSynonym;
	//
	// <field column="intermediate_mp_id"
	// xpath="/response/result/doc/arr[@name='intermediate_mp_id']/str" />
	@Field(MpDTO.INTERMEDIATE_MP_ID)
	private List<String> intermediateMpId;
	// <field column="intermediate_mp_term"
	// xpath="/response/result/doc/arr[@name='intermediate_mp_term']/str" />
	@Field(MpDTO.INTERMEDIATE_MP_TERM)
	private List<String> intermediateMpTerm;
	// <field column="intermediate_mp_term_synonym"
	// xpath="/response/result/doc/arr[@name='intermediate_mp_term_synonym']/str"
	// />

	@Field(MpDTO.INTERMEDIATE_MP_TERM_SYNONYM)
	private List<String> intermediateMpTermSynonym;
	// <field column="child_mp_id"
	// xpath="/response/result/doc/arr[@name='child_mp_id']/str" />
	@Field(MpDTO.CHILD_MP_ID)
	private List<String> childMpId;
	// <field column="child_mp_term"
	// xpath="/response/result/doc/arr[@name='child_mp_term']/str" />
	@Field(MpDTO.CHILD_MP_TERM)
	private List<String> childMpTerm;
	// <field column="child_mp_term_synonym"
	// xpath="/response/result/doc/arr[@name='child_mp_term_synonym']/str" />
	@Field(MpDTO.CHILD_MP_TERM_SYNONYM)
	private List<String> childMpTermSynonym;
	//
	// <field column="hp_id" xpath="/response/result/doc/arr[@name='hp_id']/str"
	// />
	@Field(MpDTO.HP_ID)
	private List<String> hpId;
	// <field column="hp_term"
	// xpath="/response/result/doc/arr[@name='hp_term']/str" />
	@Field(MpDTO.HP_TERM)
	private List<String> hpTerm;
	// <!-- MA: inferred from MP -->
	// <field column="inferred_ma_id"
	// xpath="/response/result/doc/arr[@name='inferred_ma_id']/str" />
	@Field(MpDTO.INFERRED_MA_ID)
	private List<String> inferredMaId;
	// <field column="inferred_ma_term"
	// xpath="/response/result/doc/arr[@name='inferred_ma_term']/str" />
	@Field(MpDTO.INFERRED_CHILD_MA_TERM)
	private List<String> inferredMaTerm;
	// <field column="inferred_ma_term_synonym"
	// xpath="/response/result/doc/arr[@name='inferred_ma_term_synonym']/str" />
	@Field(MpDTO.INFERRED_MA_TERM_SYNONYM)
	private List<String> inferredMaTermSynonym;
	//
	// <field column="inferred_selected_top_level_ma_id"
	// xpath="/response/result/doc/arr[@name='inferred_selected_top_level_ma_id']/str"
	// />
	@Field(MpDTO.INFERRED_SELECTED_TOP_LEVEL_MA_ID)
	private List<String> selectedTopLevelMaId;
	// <field column="inferred_selected_top_level_ma_term"
	// xpath="/response/result/doc/arr[@name='inferred_selected_top_level_ma_term']/str"
	// />
	@Field(MpDTO.INFERRED_SELECTED_TOP_LEVEL_MA_TERM)
	private List<String> inferredSelectedTopLevelMaTerm;
	// <field column="inferred_selected_top_level_ma_term_synonym"
	// xpath="/response/result/doc/arr[@name='inferred_selected_top_level_ma_term_synonym']/str"
	// />
	@Field(MpDTO.INFERRED_SELECTED_TOP_LEVEL_MA_TERM_SYNONYM)
	private List<String> inferredSelectedToLevelMaTermSynonym;
	// <field column="inferred_child_ma_id"
	// xpath="/response/result/doc/arr[@name='inferred_child_ma_id']/str" />
	@Field(MpDTO.INFERRED_CHILD_MA_ID)
	private List<String> inferredChildMaId;
	// <field column="inferred_child_ma_term"
	// xpath="/response/result/doc/arr[@name='inferred_child_ma_term']/str" />
	@Field(MpDTO.INFERRED_CHILD_MA_TERM)
	private List<String> inferredChildMaTerm;
	// <field column="inferred_child_ma_term_synonym"
	// xpath="/response/result/doc/arr[@name='inferred_child_ma_term_synonym']/str"
	// />
	@Field(MpDTO.INFERRED_CHILD_MA_TERM_SYNONYM)
	private List<String> inferredChildMaTermSynonym;
	private List<String> inferredSelectedTopLevelMaId;


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


	public int getPipelineStableKey() {

		return pipelineStableKey;
	}


	public void setPipelineStableKey(int pipelineStableKey) {

		this.pipelineStableKey = pipelineStableKey;
	}


	public String getPipeProcId() {

		return pipeProcId;
	}


	public void setPipeProcId(String pipeProcId) {

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


	public String getPipelineName() {

		return pipelineName;
	}


	public void setPipelineName(String pipelineName) {

		this.pipelineName = pipelineName;
	}


	public int getPipelineId() {

		return pipelineId;
	}


	public void setPipelineId(int pipelineId) {

		this.pipelineId = pipelineId;
	}


	public String getPipelineStableId() {

		return pipelineStableId;
	}


	public void setPipelineStableId(String pipelineStableId) {

		this.pipelineStableId = pipelineStableId;
	}


	public static String getMappedProcediureName() {

		return MAPPED_PROCEDIURE_NAME;
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
			this.alleleName = new ArrayList<String>();
		}
		this.alleleName.addAll(alleleName);

	}


	public void addMpId(String mpTermId) {

		if (this.mpId == null) {
			this.mpId = new ArrayList<String>();
		}
		this.mpId.add(mpTermId);

	}


	public void addMpTerm(String mpTerm) {

		if (this.mpTerm == null) {
			this.mpTerm = new ArrayList<String>();
		}
		this.mpTerm.add(mpTerm);

	}


	public void addMpTermSynonym(List<String> mpTermSynonym) {

		if (this.mpTermSynonym == null) {
			this.mpTermSynonym = new ArrayList<String>();
		}
		this.mpTermSynonym.addAll(mpTermSynonym);
	}


	public void addOntologySubset(List<String> ontologySubset) {

		if (this.ontologySubset == null) {
			this.ontologySubset = new ArrayList<String>();
		}
		this.ontologySubset.addAll(ontologySubset);
	}


	public void addTopLevelMpId(List<String> topLevelMpTermId) {
		if (this.topLevelMpId == null) {
			this.topLevelMpId = new ArrayList<String>();
		}
		this.topLevelMpId.addAll(topLevelMpTermId);
		
	}


	public void addTopLevelMpTerm(List<String> topLevelMpTerm) {
		if (this.topLevelMpTerm == null) {
			this.topLevelMpTerm = new ArrayList<String>();
		}
		this.topLevelMpTerm.addAll(topLevelMpTerm);
	}


	public void addTopLevelMpTermSynonym(List<String> topLevelMpTermSynonym) {
		if (this.topLevelMpTermSynonym == null) {
			this.topLevelMpTermSynonym = new ArrayList<String>();
		}
		this.topLevelMpTermSynonym.addAll(topLevelMpTermSynonym);
		
	}


	public void addIntermediateMpId(List<String> intermediateMpId) {
		if (this.intermediateMpId == null) {
			this.intermediateMpId = new ArrayList<String>();
		}
		this.intermediateMpId.addAll(intermediateMpId);
		
	}


	public void addIntermediateMpTerm(List<String> intermediateMpTerm) {
		if (this.intermediateMpTerm == null) {
			this.intermediateMpTerm = new ArrayList<String>();
		}
		this.intermediateMpTerm.addAll(intermediateMpTerm);
	}


	public void addIntermediateMpTermSynonym(List<String> intermediateMpTermSynonym) {

		if (this.intermediateMpTermSynonym == null) {
			this.intermediateMpTermSynonym = new ArrayList<String>();
		}
		this.intermediateMpTermSynonym.addAll(intermediateMpTermSynonym);
		
	}


	public void addChildMpId(List<String> childMpId) {

		if (this.childMpId == null) {
			this.childMpId = new ArrayList<String>();
		}
		this.childMpId.addAll(childMpId);
		
		
	}


	public void addChildMpTerm(List<String> childMpTerm) {

		if (this.childMpTerm == null) {
			this.childMpTerm = new ArrayList<String>();
		}
		this.childMpTerm.addAll(childMpTerm);
		
	}


	public void addChildMpTermSynonym(List<String> childMpTermSynonym) {

		if (this.childMpTermSynonym == null) {
			this.childMpTermSynonym = new ArrayList<String>();
		}
		this.childMpTermSynonym.addAll(childMpTermSynonym);
	}


	public void addHpId(List<String> hpId) {
		if (this.hpId == null) {
			this.hpId = new ArrayList<String>();
		}
		this.hpId.addAll(hpId);
	}


	public void addHpTerm(List<String> hpTerm) {

		if (this.hpTerm == null) {
			this.hpTerm = new ArrayList<String>();
		}
		this.hpTerm.addAll(hpTerm);
		
	}


	public void addInferredMaId(List<String> inferredChildMaId) {

		if (this.inferredChildMaId == null) {
			this.inferredChildMaId = new ArrayList<String>();
		}
		this.inferredChildMaId.addAll(inferredChildMaId);
		
	}


	public void addInferredMaTerm(List<String> inferredChildMaTerm) {

		if (this.inferredChildMaTerm == null) {
			this.inferredChildMaTerm = new ArrayList<String>();
		}
		this.inferredChildMaTerm.addAll(inferredChildMaTerm);
		
	}


	public void addInferredMaTermSynonym(List<String> inferredChildMaTermSynonym) {

		if (this.inferredChildMaTermSynonym == null) {
			this.inferredChildMaTermSynonym = new ArrayList<String>();
		}
		this.inferredChildMaTermSynonym.addAll(inferredChildMaTermSynonym);
		
		
	}


	public void addInferredSelectedTopLevelMaId(List<String> inferredSelectedTopLevelMaId) {

		if (this.inferredSelectedTopLevelMaId == null) {
			this.inferredSelectedTopLevelMaId = new ArrayList<String>();
		}
		this.inferredSelectedTopLevelMaId.addAll(inferredSelectedTopLevelMaId);
		
		
	}


	public void addInferredSelectedTopLevelMaTerm(List<String> inferredSelectedTopLevelMaTerm) {

		if (this.inferredSelectedTopLevelMaTerm == null) {
			this.inferredSelectedTopLevelMaTerm = new ArrayList<String>();
		}
		this.inferredSelectedTopLevelMaTerm.addAll(inferredSelectedTopLevelMaTerm);
		
	}


	public void addInferredSelectedToLevelMaTermSynonym(List<String> inferredSelectedTopLevelMaTermSynonym) {

		if (this.inferredSelectedToLevelMaTermSynonym== null) {
			this.inferredSelectedTopLevelMaTerm = new ArrayList<String>();
		}
		this.inferredSelectedTopLevelMaTerm.addAll(inferredSelectedTopLevelMaTerm);
		
	}


	public void addInferredChildMaId(List<String> inferredChildMaId) {

		if (this.inferredChildMaId== null) {
			this.inferredChildMaId = new ArrayList<String>();
		}
		this.inferredChildMaId.addAll(inferredChildMaId);
		
	}


	public void addInferredChildMaTerm(List<String> inferredChildMaTerm) {

		if (this.inferredChildMaTerm== null) {
			this.inferredChildMaTerm = new ArrayList<String>();
		}
		this.inferredChildMaTerm.addAll(inferredChildMaTerm);
		
	}


	public void addInferredChildMaTermSynonyms(List<String> inferredChildMaTermSynonym) {
		if (this.inferredChildMaTermSynonym== null) {
			this.inferredChildMaTermSynonym = new ArrayList<String>();
		}
		this.inferredChildMaTermSynonym.addAll(inferredChildMaTermSynonym);
		
	}
}
