package uk.ac.ebi.phenotype.service.dto;

import java.util.ArrayList;
import java.util.List;

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

	//    "parameter_name": "Submitter ID",
	@Field(ObservationDTO.PROCEDURE_ID)
	private List<Integer> procedureId;
//    "procedure_id": 2,
	@Field(ObservationDTO.PROCEDURE_STABLE_ID)
	private List<String> procedureStableId;
//    "procedure_stable_id": "IMPC_HOU_001",
//	@Field()
//	private int procedureStableKey;
//    "procedure_stable_key": 173,
	@Field(ObservationDTO.PROCEDURE_NAME)
	private List<String> procedureName;
//    "procedure_name": "Housing and Husbandry",
//    "proc_name_id": "Housing and Husbandry___IMPC_HOU_001",
	@Field(MAPPED_PROCEDIURE_NAME)
	private List<String> mappedProcedureName;
//    "mapped_procedure_name": "Housing%20and%20Husbandry",
//    "proc_param_name": "Housing and Husbandry___Submitter ID",
//    "proc_param_stable_id": "IMPC_HOU_001___IMPC_HOU_001_001",
	@Field(ObservationDTO.PIPELINE_NAME)
	private String pipelineName;
//    "pipeline_name": "IMPC Pipeline",
	@Field("pipe_proc_sid")
	private String pipeProcId;
	
	@Field("pipeline_stable_key")
	private int pipelineStableKey;

	@Field("procedure_stable_key")
	private List<Integer> procedureStableKey;
	
	@Field("proc_name_id")
	private List<String> procedureNameId;
	
	
	//    "pipe_proc_sid": "IMPC Pipeline___Housing and Husbandry___IMPC_HOU_001",
	@Field(ObservationDTO.PIPELINE_ID)
	private int pipelineId;
//    "pipeline_id": 1,
	//@Field()
	//private int pipelineStableKey;
//    "pipeline_stable_key": 7,
	@Field(ObservationDTO.PIPELINE_STABLE_ID)
	private String pipelineStableId;
	
	@Field("proc_param_stable_id")
	private List<String> procedureParamStableId;
	
	@Field("proc_param_name")
	private List<String> procedureParamName;
//    "pipeline_stable_id": "IMPC_001",
//    "ididid": "9_2_1",
	@Field("parameter_stable_key")
	private String parameterStableKey;
	@Field("ididid")
	private String ididid;
	
	@Field(GeneDTO.MGI_ACCESSION_ID)
	private List<String> mgiAccession;
	
	

	
	
	
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
	
	public void addMgiAccession(String mgiAccession){
		if(this.mgiAccession==null){
			this.mgiAccession=new ArrayList<>();
		}
		this.mgiAccession.add(mgiAccession);
	}


	public void addProcedureId(int procId) {

		if(this.procedureId==null){
			this.procedureId=new ArrayList<Integer>();
		}
		this.procedureId.add(procId);
	}

	public void addProcedureName(String procedureName) {
		if(this.procedureName==null){
			this.procedureName=new ArrayList<String>();
		}
		this.procedureName.add(procedureName);
		
	}

	public void addProcedureStableId(String procedureStableId) {
		if(this.procedureStableId==null){
			this.procedureStableId=new ArrayList<String>();
		}
		this.procedureStableId.add(procedureStableId);
	}




	public void addProcedureStableKey(Integer procedureStableKey) {

		if(this.procedureStableKey==null){
			this.procedureStableKey=new ArrayList<Integer>();
		}
		this.procedureStableKey.add(procedureStableKey);
		
	}


	public void addProcedureNameId(String procNameId) {
		if(this.procedureNameId==null){
			this.procedureNameId=new ArrayList<String>();
		}
		this.procedureNameId.add(procNameId);
		
	}





	public void addMappedProcedureName(String impcProcedureFromSanger) {
		if(this.mappedProcedureName==null){
			this.mappedProcedureName=new ArrayList<String>();
		}
		this.mappedProcedureName.add(impcProcedureFromSanger);
	}





	public void addProcParamStableId(String procParamStableId) {
		if(this.procedureParamStableId==null){
			this.procedureParamStableId=new ArrayList<String>();
		}
		this.procedureParamStableId.add(procParamStableId);
		
	}





	public void addProcParamName(String procParamName) {

		if(this.procedureParamName==null){
			this.procedureParamName=new ArrayList<String>();
		}
		this.procedureParamName.add(procParamName);
	}





	public void setParameterStableKey(String paramStableKey) {

		this.parameterStableKey=paramStableKey;
		
	}





	public void setIdIdId(String ididid) {

		this.ididid=ididid;
		
	}
}
