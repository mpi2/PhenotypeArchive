package uk.ac.ebi.phenotype.service.dto;

import java.util.List;

import org.apache.solr.client.solrj.beans.Field;

public class GeneDTO {

	// <!-- gene level fields -->

	@Field("dataType")
	String dataType;

	@Field("mgi_accession_id")
	String mgiAccessionId;

	@Field("marker_symbol")
	String markerSymbol;

	@Field("human_gene_symbol")
	List<String> humanGeneSymbol;

	@Field("marker_name")
	String markerName;

	@Field("marker_synonym")
	List<String> markerSynonym;

	@Field("marker_type")
	String markerType;

	@Field("imits_phenotype_started")
	String imitsPhenotypeStarted;

	@Field("imits_phenotype_complete")
	String imitsPhenotypeComplete;

	@Field("imits_phenotype_status")
	String imitsPhenotypeStatus;

	@Field("status")
	String status;

	@Field("latest_es_cell_status")
	String latestEsCellStatus;

	@Field("latest_mouse_status")
	String latestMouseStatus;

	@Field("latest_phenotype_status")
	String latestPhenotypeStatus;

	@Field("latest_project_status")
	String latestProjectStatus;

	@Field("latest_production_centre")
	List<String> latestProductionCentre;

	@Field("latest_phenotyping_centre")
	List<String> latestPhenotypingCentre;

	@Field("disease_human_phenotypes")
	List<String> diseaseHumanPhenotypes;

	// <!-- gene has QC: ie, a record in experiment core -->

	@Field("hasQc")
	Integer hasQc;

	// <!-- allele level fields of a gene -->

	@Field("allele_name")
	List<String> alleleName;

	@Field("imits_es_cell_status")
	String imitsEsCellStatus;

	@Field("es_cell_status")
	List<String> esCellStatus;

	@Field("imits_mouse_status")
	String imitsMouseStatus;

	@Field("mouse_status")
	List<String> mouseStatus;

	@Field("phenotype_status")
	List<String> phenotypeStatus;

	@Field("production_centre")
	List<String> productionCentre;

	@Field("phenotyping_centre")
	List<String> phenotypingCentre;

	// <!-- annotated and inferred mp term -->

	@Field("p_value")
	float p_value;

	@Field("mp_id")
	List<String> mpId;

	@Field("mp_term")
	List<String> mpTerm;

	@Field("mp_definition")
	List<String> mpDefinition;

	@Field("mp_synonym")
	List<String> mpSynonym;

	@Field("top_level_mp_id")
	List<String> topLevelMpId;

	@Field("top_level_mp_term")
	List<String> topLevelMpTerm;

	@Field("top_level_mp_definition")
	List<String> topLevelMpDefinition;

	// <!-- ontology subset of mp terms -->

	@Field("ontology_subset")
	List<String> ontologySubset;

	// <!-- annotated and inferred ma term -->

	@Field("inferred_ma_id")
	List<String> inferredMaId;

	@Field("inferred_ma_term")
	List<String> inferredMaTerm;

	@Field("inferred_selected_top_level_ma_id")
	List<String> inferredSelectedTopLevelMaId;

	@Field("inferred_selected_top_level_ma_term")
	List<String> inferredSelectedTopLevelMaTerm;

	// <!--disease fields -->

	@Field("type")
	List<String> type;

	@Field("disease_id")
	List<String> diseaseId;

	@Field("disease_source")
	List<String> diseaseSource;

	@Field("disease_term")
	List<String> diseaseTerm;

	@Field("disease_alts")
	List<String> diseaseAlts;

	@Field("disease_classes")
	List<String> diseaseClasses;

	@Field("human_curated")
	List<Boolean> humanCurated;

	@Field("mouse_curated")
	List<Boolean> mouseCurated;

	@Field("mgi_predicted")
	List<Boolean> mgiPredicted;

	@Field("impc_predicted")
	List<Boolean> impcPredicted;

	@Field("mgi_predicted_in_locus")
	List<Boolean> mgiPredictedInLocus;

	@Field("impc_predicted_in_locus")
	List<Boolean> impcPredictedInLocus;

	// <!-- pipeline stuff -->

	@Field("pipeline_name")
	List<String> pipelineName;

	@Field("pipeline_stable_id")
	List<String> pipelineStableId;

	@Field("procedure_name")
	List<String> procedureName;

	@Field("procedure_stable_id")
	List<String> procedureStableId;

	@Field("parameter_name")
	List<String> parameterName;

	@Field("parameter_stable_id")
	List<String> parameterStableId;

	@Field("proc_param_name")
	List<String> procParamName;

	@Field("proc_param_stable_id")
	List<String> procParamStableId;

	// <!-- images annotated to a gene/mp/ma/procedure -->

	@Field("expName")
	List<String> expName;

	@Field("subtype")
	List<String> subtype;

	@Field("annotatedHigherLevelMaTermName")
	List<String> annotatedHigherLevelMaTermName;

	@Field("annotatedHigherLevelMpTermName")
	List<String> annotatedHigherLevelMpTermName;

	// <!-- for copyfield -->
	@Field("text")
	List<String> text;

	@Field("auto_suggest")
	List<String> autoSuggest;

	@Field("selected_top_level_ma_term")
	List<String> selectedTopLevelMaTerm;


	public String getDataType() {

		return dataType;
	}


	public void setDataType(String dataType) {

		this.dataType = dataType;
	}


	public String getMgiAccessionId() {

		return mgiAccessionId;
	}


	public void setMgiAccessionId(String mgiAccessionId) {

		this.mgiAccessionId = mgiAccessionId;
	}


	public String getMarkerSymbol() {

		return markerSymbol;
	}


	public void setMarkerSymbol(String markerSymbol) {

		this.markerSymbol = markerSymbol;
	}


	public List<String> getHumanGeneSymbol() {

		return humanGeneSymbol;
	}


	public void setHumanGeneSymbol(List<String> humanGeneSymbol) {

		this.humanGeneSymbol = humanGeneSymbol;
	}


	public String getMarkerName() {

		return markerName;
	}


	public void setMarkerName(String markerName) {

		this.markerName = markerName;
	}


	public List<String> getMarkerSynonym() {

		return markerSynonym;
	}


	public void setMarkerSynonym(List<String> markerSynonym) {

		this.markerSynonym = markerSynonym;
	}


	public String getMarkerType() {

		return markerType;
	}


	public void setMarkerType(String markerType) {

		this.markerType = markerType;
	}


	public String getImitsPhenotypeStarted() {

		return imitsPhenotypeStarted;
	}


	public void setImitsPhenotypeStarted(String imitsPhenotypeStarted) {

		this.imitsPhenotypeStarted = imitsPhenotypeStarted;
	}


	public String getImitsPhenotypeComplete() {

		return imitsPhenotypeComplete;
	}


	public void setImitsPhenotypeComplete(String imitsPhenotypeComplete) {

		this.imitsPhenotypeComplete = imitsPhenotypeComplete;
	}


	public String getImitsPhenotypeStatus() {

		return imitsPhenotypeStatus;
	}


	public void setImitsPhenotypeStatus(String imitsPhenotypeStatus) {

		this.imitsPhenotypeStatus = imitsPhenotypeStatus;
	}


	public String getStatus() {

		return status;
	}


	public void setStatus(String status) {

		this.status = status;
	}


	public String getLatestEsCellStatus() {

		return latestEsCellStatus;
	}


	public void setLatestEsCellStatus(String latestEsCellStatus) {

		this.latestEsCellStatus = latestEsCellStatus;
	}


	public String getLatestMouseStatus() {

		return latestMouseStatus;
	}


	public void setLatestMouseStatus(String latestMouseStatus) {

		this.latestMouseStatus = latestMouseStatus;
	}


	public String getLatestPhenotypeStatus() {

		return latestPhenotypeStatus;
	}


	public void setLatestPhenotypeStatus(String latestPhenotypeStatus) {

		this.latestPhenotypeStatus = latestPhenotypeStatus;
	}


	public String getLatestProjectStatus() {

		return latestProjectStatus;
	}


	public void setLatestProjectStatus(String latestProjectStatus) {

		this.latestProjectStatus = latestProjectStatus;
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


	public List<String> getDiseaseHumanPhenotypes() {

		return diseaseHumanPhenotypes;
	}


	public void setDiseaseHumanPhenotypes(List<String> diseaseHumanPhenotypes) {

		this.diseaseHumanPhenotypes = diseaseHumanPhenotypes;
	}


	public Integer getHasQc() {

		return hasQc;
	}


	public void setHasQc(Integer hasQc) {

		this.hasQc = hasQc;
	}


	public List<String> getAlleleName() {

		return alleleName;
	}


	public void setAlleleName(List<String> alleleName) {

		this.alleleName = alleleName;
	}


	public String getImitsEsCellStatus() {

		return imitsEsCellStatus;
	}


	public void setImitsEsCellStatus(String imitsEsCellStatus) {

		this.imitsEsCellStatus = imitsEsCellStatus;
	}


	public List<String> getEsCellStatus() {

		return esCellStatus;
	}


	public void setEsCellStatus(List<String> esCellStatus) {

		this.esCellStatus = esCellStatus;
	}


	public String getImitsMouseStatus() {

		return imitsMouseStatus;
	}


	public void setImitsMouseStatus(String imitsMouseStatus) {

		this.imitsMouseStatus = imitsMouseStatus;
	}


	public List<String> getMouseStatus() {

		return mouseStatus;
	}


	public void setMouseStatus(List<String> mouseStatus) {

		this.mouseStatus = mouseStatus;
	}


	public List<String> getPhenotypeStatus() {

		return phenotypeStatus;
	}


	public void setPhenotypeStatus(List<String> phenotypeStatus) {

		this.phenotypeStatus = phenotypeStatus;
	}


	public List<String> getProductionCentre() {

		return productionCentre;
	}


	public void setProductionCentre(List<String> productionCentre) {

		this.productionCentre = productionCentre;
	}


	public List<String> getPhenotypingCentre() {

		return phenotypingCentre;
	}


	public void setPhenotypingCentre(List<String> phenotypingCentre) {

		this.phenotypingCentre = phenotypingCentre;
	}


	public float getP_value() {

		return p_value;
	}


	public void setP_value(float p_value) {

		this.p_value = p_value;
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


	public List<String> getMpDefinition() {

		return mpDefinition;
	}


	public void setMpDefinition(List<String> mpDefinition) {

		this.mpDefinition = mpDefinition;
	}


	public List<String> getMpSynonym() {

		return mpSynonym;
	}


	public void setMpSynonym(List<String> mpSynonym) {

		this.mpSynonym = mpSynonym;
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


	public List<String> getOntologySubset() {

		return ontologySubset;
	}


	public void setOntologySubset(List<String> ontologySubset) {

		this.ontologySubset = ontologySubset;
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


	public List<Boolean> getMgiPredictedInLocus() {

		return mgiPredictedInLocus;
	}


	public void setMgiPredictedInLocus(List<Boolean> mgiPredictedInLocus) {

		this.mgiPredictedInLocus = mgiPredictedInLocus;
	}


	public List<Boolean> getImpcPredictedInLocus() {

		return impcPredictedInLocus;
	}


	public void setImpcPredictedInLocus(List<Boolean> impcPredictedInLocus) {

		this.impcPredictedInLocus = impcPredictedInLocus;
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


	public List<String> getProcParamName() {

		return procParamName;
	}


	public void setProcParamName(List<String> procParamName) {

		this.procParamName = procParamName;
	}


	public List<String> getProcParamStableId() {

		return procParamStableId;
	}


	public void setProcParamStableId(List<String> procParamStableId) {

		this.procParamStableId = procParamStableId;
	}


	public List<String> getExpName() {

		return expName;
	}


	public void setExpName(List<String> expName) {

		this.expName = expName;
	}


	public List<String> getSubtype() {

		return subtype;
	}


	public void setSubtype(List<String> subtype) {

		this.subtype = subtype;
	}


	public List<String> getAnnotatedHigherLevelMaTermName() {

		return annotatedHigherLevelMaTermName;
	}


	public void setAnnotatedHigherLevelMaTermName(List<String> annotatedHigherLevelMaTermName) {

		this.annotatedHigherLevelMaTermName = annotatedHigherLevelMaTermName;
	}


	public List<String> getAnnotatedHigherLevelMpTermName() {

		return annotatedHigherLevelMpTermName;
	}


	public void setAnnotatedHigherLevelMpTermName(List<String> annotatedHigherLevelMpTermName) {

		this.annotatedHigherLevelMpTermName = annotatedHigherLevelMpTermName;
	}


	public List<String> getText() {

		return text;
	}


	public void setText(List<String> text) {

		this.text = text;
	}


	public List<String> getAutoSuggest() {

		return autoSuggest;
	}


	public void setAutoSuggest(List<String> autoSuggest) {

		this.autoSuggest = autoSuggest;
	}


	public List<String> getSelectedTopLevelMaTerm() {

		return selectedTopLevelMaTerm;
	}


	public void setSelectedTopLevelMaTerm(List<String> selectedTopLevelMaTerm) {

		this.selectedTopLevelMaTerm = selectedTopLevelMaTerm;
	}


	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + ((alleleName == null) ? 0 : alleleName.hashCode());
		result = prime * result + ((annotatedHigherLevelMaTermName == null) ? 0 : annotatedHigherLevelMaTermName.hashCode());
		result = prime * result + ((annotatedHigherLevelMpTermName == null) ? 0 : annotatedHigherLevelMpTermName.hashCode());
		result = prime * result + ((autoSuggest == null) ? 0 : autoSuggest.hashCode());
		result = prime * result + ((dataType == null) ? 0 : dataType.hashCode());
		result = prime * result + ((diseaseAlts == null) ? 0 : diseaseAlts.hashCode());
		result = prime * result + ((diseaseClasses == null) ? 0 : diseaseClasses.hashCode());
		result = prime * result + ((diseaseHumanPhenotypes == null) ? 0 : diseaseHumanPhenotypes.hashCode());
		result = prime * result + ((diseaseId == null) ? 0 : diseaseId.hashCode());
		result = prime * result + ((diseaseSource == null) ? 0 : diseaseSource.hashCode());
		result = prime * result + ((diseaseTerm == null) ? 0 : diseaseTerm.hashCode());
		result = prime * result + ((esCellStatus == null) ? 0 : esCellStatus.hashCode());
		result = prime * result + ((expName == null) ? 0 : expName.hashCode());
		result = prime * result + ((hasQc == null) ? 0 : hasQc.hashCode());
		result = prime * result + ((humanCurated == null) ? 0 : humanCurated.hashCode());
		result = prime * result + ((humanGeneSymbol == null) ? 0 : humanGeneSymbol.hashCode());
		result = prime * result + ((imitsEsCellStatus == null) ? 0 : imitsEsCellStatus.hashCode());
		result = prime * result + ((imitsMouseStatus == null) ? 0 : imitsMouseStatus.hashCode());
		result = prime * result + ((imitsPhenotypeComplete == null) ? 0 : imitsPhenotypeComplete.hashCode());
		result = prime * result + ((imitsPhenotypeStarted == null) ? 0 : imitsPhenotypeStarted.hashCode());
		result = prime * result + ((imitsPhenotypeStatus == null) ? 0 : imitsPhenotypeStatus.hashCode());
		result = prime * result + ((impcPredicted == null) ? 0 : impcPredicted.hashCode());
		result = prime * result + ((impcPredictedInLocus == null) ? 0 : impcPredictedInLocus.hashCode());
		result = prime * result + ((inferredMaId == null) ? 0 : inferredMaId.hashCode());
		result = prime * result + ((inferredMaTerm == null) ? 0 : inferredMaTerm.hashCode());
		result = prime * result + ((inferredSelectedTopLevelMaId == null) ? 0 : inferredSelectedTopLevelMaId.hashCode());
		result = prime * result + ((inferredSelectedTopLevelMaTerm == null) ? 0 : inferredSelectedTopLevelMaTerm.hashCode());
		result = prime * result + ((latestEsCellStatus == null) ? 0 : latestEsCellStatus.hashCode());
		result = prime * result + ((latestMouseStatus == null) ? 0 : latestMouseStatus.hashCode());
		result = prime * result + ((latestPhenotypeStatus == null) ? 0 : latestPhenotypeStatus.hashCode());
		result = prime * result + ((latestPhenotypingCentre == null) ? 0 : latestPhenotypingCentre.hashCode());
		result = prime * result + ((latestProductionCentre == null) ? 0 : latestProductionCentre.hashCode());
		result = prime * result + ((latestProjectStatus == null) ? 0 : latestProjectStatus.hashCode());
		result = prime * result + ((markerName == null) ? 0 : markerName.hashCode());
		result = prime * result + ((markerSymbol == null) ? 0 : markerSymbol.hashCode());
		result = prime * result + ((markerSynonym == null) ? 0 : markerSynonym.hashCode());
		result = prime * result + ((markerType == null) ? 0 : markerType.hashCode());
		result = prime * result + ((mgiAccessionId == null) ? 0 : mgiAccessionId.hashCode());
		result = prime * result + ((mgiPredicted == null) ? 0 : mgiPredicted.hashCode());
		result = prime * result + ((mgiPredictedInLocus == null) ? 0 : mgiPredictedInLocus.hashCode());
		result = prime * result + ((mouseCurated == null) ? 0 : mouseCurated.hashCode());
		result = prime * result + ((mouseStatus == null) ? 0 : mouseStatus.hashCode());
		result = prime * result + ((mpDefinition == null) ? 0 : mpDefinition.hashCode());
		result = prime * result + ((mpId == null) ? 0 : mpId.hashCode());
		result = prime * result + ((mpSynonym == null) ? 0 : mpSynonym.hashCode());
		result = prime * result + ((mpTerm == null) ? 0 : mpTerm.hashCode());
		result = prime * result + ((ontologySubset == null) ? 0 : ontologySubset.hashCode());
		result = prime * result + Float.floatToIntBits(p_value);
		result = prime * result + ((parameterName == null) ? 0 : parameterName.hashCode());
		result = prime * result + ((parameterStableId == null) ? 0 : parameterStableId.hashCode());
		result = prime * result + ((phenotypeStatus == null) ? 0 : phenotypeStatus.hashCode());
		result = prime * result + ((phenotypingCentre == null) ? 0 : phenotypingCentre.hashCode());
		result = prime * result + ((pipelineName == null) ? 0 : pipelineName.hashCode());
		result = prime * result + ((pipelineStableId == null) ? 0 : pipelineStableId.hashCode());
		result = prime * result + ((procParamName == null) ? 0 : procParamName.hashCode());
		result = prime * result + ((procParamStableId == null) ? 0 : procParamStableId.hashCode());
		result = prime * result + ((procedureName == null) ? 0 : procedureName.hashCode());
		result = prime * result + ((procedureStableId == null) ? 0 : procedureStableId.hashCode());
		result = prime * result + ((productionCentre == null) ? 0 : productionCentre.hashCode());
		result = prime * result + ((selectedTopLevelMaTerm == null) ? 0 : selectedTopLevelMaTerm.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((subtype == null) ? 0 : subtype.hashCode());
		result = prime * result + ((text == null) ? 0 : text.hashCode());
		result = prime * result + ((topLevelMpDefinition == null) ? 0 : topLevelMpDefinition.hashCode());
		result = prime * result + ((topLevelMpId == null) ? 0 : topLevelMpId.hashCode());
		result = prime * result + ((topLevelMpTerm == null) ? 0 : topLevelMpTerm.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {

		if (this == obj) { return true; }
		if (obj == null) { return false; }
		if (getClass() != obj.getClass()) { return false; }
		GeneDTO other = (GeneDTO) obj;
		if (alleleName == null) {
			if (other.alleleName != null) { return false; }
		} else if (!alleleName.equals(other.alleleName)) { return false; }
		if (annotatedHigherLevelMaTermName == null) {
			if (other.annotatedHigherLevelMaTermName != null) { return false; }
		} else if (!annotatedHigherLevelMaTermName.equals(other.annotatedHigherLevelMaTermName)) { return false; }
		if (annotatedHigherLevelMpTermName == null) {
			if (other.annotatedHigherLevelMpTermName != null) { return false; }
		} else if (!annotatedHigherLevelMpTermName.equals(other.annotatedHigherLevelMpTermName)) { return false; }
		if (autoSuggest == null) {
			if (other.autoSuggest != null) { return false; }
		} else if (!autoSuggest.equals(other.autoSuggest)) { return false; }
		if (dataType == null) {
			if (other.dataType != null) { return false; }
		} else if (!dataType.equals(other.dataType)) { return false; }
		if (diseaseAlts == null) {
			if (other.diseaseAlts != null) { return false; }
		} else if (!diseaseAlts.equals(other.diseaseAlts)) { return false; }
		if (diseaseClasses == null) {
			if (other.diseaseClasses != null) { return false; }
		} else if (!diseaseClasses.equals(other.diseaseClasses)) { return false; }
		if (diseaseHumanPhenotypes == null) {
			if (other.diseaseHumanPhenotypes != null) { return false; }
		} else if (!diseaseHumanPhenotypes.equals(other.diseaseHumanPhenotypes)) { return false; }
		if (diseaseId == null) {
			if (other.diseaseId != null) { return false; }
		} else if (!diseaseId.equals(other.diseaseId)) { return false; }
		if (diseaseSource == null) {
			if (other.diseaseSource != null) { return false; }
		} else if (!diseaseSource.equals(other.diseaseSource)) { return false; }
		if (diseaseTerm == null) {
			if (other.diseaseTerm != null) { return false; }
		} else if (!diseaseTerm.equals(other.diseaseTerm)) { return false; }
		if (esCellStatus == null) {
			if (other.esCellStatus != null) { return false; }
		} else if (!esCellStatus.equals(other.esCellStatus)) { return false; }
		if (expName == null) {
			if (other.expName != null) { return false; }
		} else if (!expName.equals(other.expName)) { return false; }
		if (hasQc == null) {
			if (other.hasQc != null) { return false; }
		} else if (!hasQc.equals(other.hasQc)) { return false; }
		if (humanCurated == null) {
			if (other.humanCurated != null) { return false; }
		} else if (!humanCurated.equals(other.humanCurated)) { return false; }
		if (humanGeneSymbol == null) {
			if (other.humanGeneSymbol != null) { return false; }
		} else if (!humanGeneSymbol.equals(other.humanGeneSymbol)) { return false; }
		if (imitsEsCellStatus == null) {
			if (other.imitsEsCellStatus != null) { return false; }
		} else if (!imitsEsCellStatus.equals(other.imitsEsCellStatus)) { return false; }
		if (imitsMouseStatus == null) {
			if (other.imitsMouseStatus != null) { return false; }
		} else if (!imitsMouseStatus.equals(other.imitsMouseStatus)) { return false; }
		if (imitsPhenotypeComplete == null) {
			if (other.imitsPhenotypeComplete != null) { return false; }
		} else if (!imitsPhenotypeComplete.equals(other.imitsPhenotypeComplete)) { return false; }
		if (imitsPhenotypeStarted == null) {
			if (other.imitsPhenotypeStarted != null) { return false; }
		} else if (!imitsPhenotypeStarted.equals(other.imitsPhenotypeStarted)) { return false; }
		if (imitsPhenotypeStatus == null) {
			if (other.imitsPhenotypeStatus != null) { return false; }
		} else if (!imitsPhenotypeStatus.equals(other.imitsPhenotypeStatus)) { return false; }
		if (impcPredicted == null) {
			if (other.impcPredicted != null) { return false; }
		} else if (!impcPredicted.equals(other.impcPredicted)) { return false; }
		if (impcPredictedInLocus == null) {
			if (other.impcPredictedInLocus != null) { return false; }
		} else if (!impcPredictedInLocus.equals(other.impcPredictedInLocus)) { return false; }
		if (inferredMaId == null) {
			if (other.inferredMaId != null) { return false; }
		} else if (!inferredMaId.equals(other.inferredMaId)) { return false; }
		if (inferredMaTerm == null) {
			if (other.inferredMaTerm != null) { return false; }
		} else if (!inferredMaTerm.equals(other.inferredMaTerm)) { return false; }
		if (inferredSelectedTopLevelMaId == null) {
			if (other.inferredSelectedTopLevelMaId != null) { return false; }
		} else if (!inferredSelectedTopLevelMaId.equals(other.inferredSelectedTopLevelMaId)) { return false; }
		if (inferredSelectedTopLevelMaTerm == null) {
			if (other.inferredSelectedTopLevelMaTerm != null) { return false; }
		} else if (!inferredSelectedTopLevelMaTerm.equals(other.inferredSelectedTopLevelMaTerm)) { return false; }
		if (latestEsCellStatus == null) {
			if (other.latestEsCellStatus != null) { return false; }
		} else if (!latestEsCellStatus.equals(other.latestEsCellStatus)) { return false; }
		if (latestMouseStatus == null) {
			if (other.latestMouseStatus != null) { return false; }
		} else if (!latestMouseStatus.equals(other.latestMouseStatus)) { return false; }
		if (latestPhenotypeStatus == null) {
			if (other.latestPhenotypeStatus != null) { return false; }
		} else if (!latestPhenotypeStatus.equals(other.latestPhenotypeStatus)) { return false; }
		if (latestPhenotypingCentre == null) {
			if (other.latestPhenotypingCentre != null) { return false; }
		} else if (!latestPhenotypingCentre.equals(other.latestPhenotypingCentre)) { return false; }
		if (latestProductionCentre == null) {
			if (other.latestProductionCentre != null) { return false; }
		} else if (!latestProductionCentre.equals(other.latestProductionCentre)) { return false; }
		if (latestProjectStatus == null) {
			if (other.latestProjectStatus != null) { return false; }
		} else if (!latestProjectStatus.equals(other.latestProjectStatus)) { return false; }
		if (markerName == null) {
			if (other.markerName != null) { return false; }
		} else if (!markerName.equals(other.markerName)) { return false; }
		if (markerSymbol == null) {
			if (other.markerSymbol != null) { return false; }
		} else if (!markerSymbol.equals(other.markerSymbol)) { return false; }
		if (markerSynonym == null) {
			if (other.markerSynonym != null) { return false; }
		} else if (!markerSynonym.equals(other.markerSynonym)) { return false; }
		if (markerType == null) {
			if (other.markerType != null) { return false; }
		} else if (!markerType.equals(other.markerType)) { return false; }
		if (mgiAccessionId == null) {
			if (other.mgiAccessionId != null) { return false; }
		} else if (!mgiAccessionId.equals(other.mgiAccessionId)) { return false; }
		if (mgiPredicted == null) {
			if (other.mgiPredicted != null) { return false; }
		} else if (!mgiPredicted.equals(other.mgiPredicted)) { return false; }
		if (mgiPredictedInLocus == null) {
			if (other.mgiPredictedInLocus != null) { return false; }
		} else if (!mgiPredictedInLocus.equals(other.mgiPredictedInLocus)) { return false; }
		if (mouseCurated == null) {
			if (other.mouseCurated != null) { return false; }
		} else if (!mouseCurated.equals(other.mouseCurated)) { return false; }
		if (mouseStatus == null) {
			if (other.mouseStatus != null) { return false; }
		} else if (!mouseStatus.equals(other.mouseStatus)) { return false; }
		if (mpDefinition == null) {
			if (other.mpDefinition != null) { return false; }
		} else if (!mpDefinition.equals(other.mpDefinition)) { return false; }
		if (mpId == null) {
			if (other.mpId != null) { return false; }
		} else if (!mpId.equals(other.mpId)) { return false; }
		if (mpSynonym == null) {
			if (other.mpSynonym != null) { return false; }
		} else if (!mpSynonym.equals(other.mpSynonym)) { return false; }
		if (mpTerm == null) {
			if (other.mpTerm != null) { return false; }
		} else if (!mpTerm.equals(other.mpTerm)) { return false; }
		if (ontologySubset == null) {
			if (other.ontologySubset != null) { return false; }
		} else if (!ontologySubset.equals(other.ontologySubset)) { return false; }
		if (Float.floatToIntBits(p_value) != Float.floatToIntBits(other.p_value)) { return false; }
		if (parameterName == null) {
			if (other.parameterName != null) { return false; }
		} else if (!parameterName.equals(other.parameterName)) { return false; }
		if (parameterStableId == null) {
			if (other.parameterStableId != null) { return false; }
		} else if (!parameterStableId.equals(other.parameterStableId)) { return false; }
		if (phenotypeStatus == null) {
			if (other.phenotypeStatus != null) { return false; }
		} else if (!phenotypeStatus.equals(other.phenotypeStatus)) { return false; }
		if (phenotypingCentre == null) {
			if (other.phenotypingCentre != null) { return false; }
		} else if (!phenotypingCentre.equals(other.phenotypingCentre)) { return false; }
		if (pipelineName == null) {
			if (other.pipelineName != null) { return false; }
		} else if (!pipelineName.equals(other.pipelineName)) { return false; }
		if (pipelineStableId == null) {
			if (other.pipelineStableId != null) { return false; }
		} else if (!pipelineStableId.equals(other.pipelineStableId)) { return false; }
		if (procParamName == null) {
			if (other.procParamName != null) { return false; }
		} else if (!procParamName.equals(other.procParamName)) { return false; }
		if (procParamStableId == null) {
			if (other.procParamStableId != null) { return false; }
		} else if (!procParamStableId.equals(other.procParamStableId)) { return false; }
		if (procedureName == null) {
			if (other.procedureName != null) { return false; }
		} else if (!procedureName.equals(other.procedureName)) { return false; }
		if (procedureStableId == null) {
			if (other.procedureStableId != null) { return false; }
		} else if (!procedureStableId.equals(other.procedureStableId)) { return false; }
		if (productionCentre == null) {
			if (other.productionCentre != null) { return false; }
		} else if (!productionCentre.equals(other.productionCentre)) { return false; }
		if (selectedTopLevelMaTerm == null) {
			if (other.selectedTopLevelMaTerm != null) { return false; }
		} else if (!selectedTopLevelMaTerm.equals(other.selectedTopLevelMaTerm)) { return false; }
		if (status == null) {
			if (other.status != null) { return false; }
		} else if (!status.equals(other.status)) { return false; }
		if (subtype == null) {
			if (other.subtype != null) { return false; }
		} else if (!subtype.equals(other.subtype)) { return false; }
		if (text == null) {
			if (other.text != null) { return false; }
		} else if (!text.equals(other.text)) { return false; }
		if (topLevelMpDefinition == null) {
			if (other.topLevelMpDefinition != null) { return false; }
		} else if (!topLevelMpDefinition.equals(other.topLevelMpDefinition)) { return false; }
		if (topLevelMpId == null) {
			if (other.topLevelMpId != null) { return false; }
		} else if (!topLevelMpId.equals(other.topLevelMpId)) { return false; }
		if (topLevelMpTerm == null) {
			if (other.topLevelMpTerm != null) { return false; }
		} else if (!topLevelMpTerm.equals(other.topLevelMpTerm)) { return false; }
		if (type == null) {
			if (other.type != null) { return false; }
		} else if (!type.equals(other.type)) { return false; }
		return true;
	}


	@Override
	public String toString() {

		return "GeneDTO [dataType=" + dataType + ", mgiAccessionId=" + mgiAccessionId + ", markerSymbol=" + markerSymbol + ", humanGeneSymbol=" + humanGeneSymbol + ", markerName=" + markerName + ", markerSynonym=" + markerSynonym + ", markerType=" + markerType + ", imitsPhenotypeStarted=" + imitsPhenotypeStarted + ", imitsPhenotypeComplete=" + imitsPhenotypeComplete + ", imitsPhenotypeStatus=" + imitsPhenotypeStatus + ", status=" + status + ", latestEsCellStatus=" + latestEsCellStatus + ", latestMouseStatus=" + latestMouseStatus + ", latestPhenotypeStatus=" + latestPhenotypeStatus + ", latestProjectStatus=" + latestProjectStatus + ", latestProductionCentre=" + latestProductionCentre + ", latestPhenotypingCentre=" + latestPhenotypingCentre + ", diseaseHumanPhenotypes=" + diseaseHumanPhenotypes + ", hasQc=" + hasQc + ", alleleName=" + alleleName + ", imitsEsCellStatus=" + imitsEsCellStatus + ", esCellStatus=" + esCellStatus + ", imitsMouseStatus=" + imitsMouseStatus + ", mouseStatus=" + mouseStatus + ", phenotypeStatus=" + phenotypeStatus + ", productionCentre=" + productionCentre + ", phenotypingCentre=" + phenotypingCentre + ", p_value=" + p_value + ", mpId=" + mpId + ", mpTerm=" + mpTerm + ", mpDefinition=" + mpDefinition + ", mpSynonym=" + mpSynonym + ", topLevelMpId=" + topLevelMpId + ", topLevelMpTerm=" + topLevelMpTerm + ", topLevelMpDefinition=" + topLevelMpDefinition + ", ontologySubset=" + ontologySubset + ", inferredMaId=" + inferredMaId + ", inferredMaTerm=" + inferredMaTerm + ", inferredSelectedTopLevelMaId=" + inferredSelectedTopLevelMaId + ", inferredSelectedTopLevelMaTerm=" + inferredSelectedTopLevelMaTerm + ", type=" + type + ", diseaseId=" + diseaseId + ", diseaseSource=" + diseaseSource + ", diseaseTerm=" + diseaseTerm + ", diseaseAlts=" + diseaseAlts + ", diseaseClasses=" + diseaseClasses + ", humanCurated=" + humanCurated + ", mouseCurated=" + mouseCurated + ", mgiPredicted=" + mgiPredicted + ", impcPredicted=" + impcPredicted + ", mgiPredictedInLocus=" + mgiPredictedInLocus + ", impcPredictedInLocus=" + impcPredictedInLocus + ", pipelineName=" + pipelineName + ", pipelineStableId=" + pipelineStableId + ", procedureName=" + procedureName + ", procedureStableId=" + procedureStableId + ", parameterName=" + parameterName + ", parameterStableId=" + parameterStableId + ", procParamName=" + procParamName + ", procParamStableId=" + procParamStableId + ", expName=" + expName + ", subtype=" + subtype + ", annotatedHigherLevelMaTermName=" + annotatedHigherLevelMaTermName + ", annotatedHigherLevelMpTermName=" + annotatedHigherLevelMpTermName + ", text=" + text + ", autoSuggest=" + autoSuggest + ", selectedTopLevelMaTerm=" + selectedTopLevelMaTerm + "]";
	}

}
