package uk.ac.ebi.phenotype.service.dto;

import org.apache.solr.client.solrj.beans.Field;


public class PipelineDTO {
	private static final String MAPPED_PROCEDIURE_NAME = "mapped_procedure_name";
	@Field(ObservationDTO.PARAMETER_ID)
	private int parameterId;
//	"parameter_id": 9,
//    "dataType": "pipeline",
	@Field(ObservationDTO.PARAMETER_STABLE_ID)
	private String parameterStableId;
//    "parameter_stable_id": "IMPC_HOU_001_001",
//	@Field(ObservationDTO.par)
//	private int parameterStableKey;
//    "parameter_stable_key": 4181,
	@Field(ObservationDTO.PARAMETER_NAME)
	private String parameterName;

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
	
	public int getProcedureId() {
	
		return procedureId;
	}
	
	public void setProcedureId(int procedureId) {
	
		this.procedureId = procedureId;
	}
	
	public String getProcedureStableId() {
	
		return procedureStableId;
	}
	
	public void setProcedureStableId(String procedureStableId) {
	
		this.procedureStableId = procedureStableId;
	}
	
	public String getProcedureName() {
	
		return procedureName;
	}
	
	public void setProcedureName(String procedureName) {
	
		this.procedureName = procedureName;
	}
	
	public String getMappedProcedureName() {
	
		return mappedProcedureName;
	}
	
	public void setMappedProcedureName(String mappedProcedureName) {
	
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
	//    "parameter_name": "Submitter ID",
	@Field(ObservationDTO.PROCEDURE_ID)
	private int procedureId;
//    "procedure_id": 2,
	@Field(ObservationDTO.PROCEDURE_STABLE_ID)
	private String procedureStableId;
//    "procedure_stable_id": "IMPC_HOU_001",
//	@Field()
//	private int procedureStableKey;
//    "procedure_stable_key": 173,
	@Field(ObservationDTO.PROCEDURE_NAME)
	private String procedureName;
//    "procedure_name": "Housing and Husbandry",
//    "proc_name_id": "Housing and Husbandry___IMPC_HOU_001",
	@Field(MAPPED_PROCEDIURE_NAME)
	private String mappedProcedureName;
//    "mapped_procedure_name": "Housing%20and%20Husbandry",
//    "proc_param_name": "Housing and Husbandry___Submitter ID",
//    "proc_param_stable_id": "IMPC_HOU_001___IMPC_HOU_001_001",
	@Field(ObservationDTO.PIPELINE_NAME)
	private String pipelineName;
//    "pipeline_name": "IMPC Pipeline",
	
//    "pipe_proc_sid": "IMPC Pipeline___Housing and Husbandry___IMPC_HOU_001",
	@Field(ObservationDTO.PIPELINE_ID)
	private int pipelineId;
//    "pipeline_id": 1,
	//@Field()
	//private int pipelineStableKey;
//    "pipeline_stable_key": 7,
	@Field(ObservationDTO.PIPELINE_STABLE_ID)
	private String pipelineStableId;
//    "pipeline_stable_id": "IMPC_001",
//    "ididid": "9_2_1",
}
