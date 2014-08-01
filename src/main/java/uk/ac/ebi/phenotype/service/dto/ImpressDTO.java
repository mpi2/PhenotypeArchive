package uk.ac.ebi.phenotype.service.dto;

import org.apache.solr.client.solrj.beans.Field;


public class ImpressDTO {

	public static final String DATA_TYPE = "dataType" ;
	public static final String PARAMETER_ID = "parameter_id" ;
	public static final String PARAMETER_STABLE_ID = "parameter_stable_id" ;
	public static final String PARAMETER_STABLE_KEY = "parameter_stable_key" ;
	public static final String PARAMETER_NAME = "parameter_name" ;
	public static final String PROCEDURE_ID = "procedure_id" ;
	public static final String PROCEDURE_STABLE_ID = "procedure_stable_id" ;
	public static final String PROCEDURE_STABLE_KEY = "procedure_stable_key" ;
	public static final String PROCEDURE_NAME = "procedure_name" ;
	public static final String PIPELINE_ID = "pipeline_id" ;
	public static final String PIPELINE_STABLE_ID = "pipeline_stable_id" ;
	public static final String PIPELINE_STABLE_KEY = "pipeline_stable_key" ;
	public static final String PIPELINE_NAME = "pipeline_name" ;

	
	@Field(PARAMETER_ID)
	Integer parameterId;
	
	@Field(DATA_TYPE)
	String dataType;
	
	@Field(PARAMETER_STABLE_ID)
	String parameterStableId;
	
	@Field(PARAMETER_STABLE_KEY)
	Integer parameterStableKey;
	
	@Field(PARAMETER_NAME)
	String parameterName;
	
	@Field(PROCEDURE_ID)
	Integer procedureId;
	
	@Field(PROCEDURE_STABLE_ID)
	String procedureStableId;
	
	@Field(PROCEDURE_STABLE_KEY)
	Integer procedureStableKey;
	
	@Field(PROCEDURE_NAME)
	String procedureName;
	
	@Field(PIPELINE_NAME)
	String pipelineName;
	
	@Field(PIPELINE_ID)
	Integer pipelineId;
	
	@Field(PIPELINE_STABLE_KEY)
	Integer pipelineStableKey;
	
	@Field(PIPELINE_STABLE_ID)
	String pipelineStableId;

	
	public Integer getParameterId() {
	
		return parameterId;
	}

	
	public void setParameterId(Integer parameterId) {
	
		this.parameterId = parameterId;
	}

	
	public String getDataType() {
	
		return dataType;
	}

	
	public void setDataType(String dataType) {
	
		this.dataType = dataType;
	}

	
	public String getParameterStableId() {
	
		return parameterStableId;
	}

	
	public void setParameterStableId(String parameterStableId) {
	
		this.parameterStableId = parameterStableId;
	}

	
	public Integer getParameterStableKey() {
	
		return parameterStableKey;
	}

	
	public void setParameterStableKey(Integer parameterStableKey) {
	
		this.parameterStableKey = parameterStableKey;
	}

	
	public String getParameterName() {
	
		return parameterName;
	}

	
	public void setParameterName(String parameterName) {
	
		this.parameterName = parameterName;
	}

	
	public Integer getProcedureId() {
	
		return procedureId;
	}

	
	public void setProcedureId(Integer procedureId) {
	
		this.procedureId = procedureId;
	}

	
	public String getProcedureStableId() {
	
		return procedureStableId;
	}

	
	public void setProcedureStableId(String procedureStableId) {
	
		this.procedureStableId = procedureStableId;
	}

	
	public Integer getProcedureStableKey() {
	
		return procedureStableKey;
	}

	
	public void setProcedureStableKey(Integer procedureStableKey) {
	
		this.procedureStableKey = procedureStableKey;
	}

	
	public String getProcedureName() {
	
		return procedureName;
	}

	
	public void setProcedureName(String procedureName) {
	
		this.procedureName = procedureName;
	}

	
	public String getPipelineName() {
	
		return pipelineName;
	}

	
	public void setPipelineName(String pipelineName) {
	
		this.pipelineName = pipelineName;
	}

	
	public Integer getPipelineId() {
	
		return pipelineId;
	}

	
	public void setPipelineId(Integer pipelineId) {
	
		this.pipelineId = pipelineId;
	}

	
	public Integer getPipelineStableKey() {
	
		return pipelineStableKey;
	}

	
	public void setPipelineStableKey(Integer pipelineStableKey) {
	
		this.pipelineStableKey = pipelineStableKey;
	}

	
	public String getPipelineStableId() {
	
		return pipelineStableId;
	}

	
	public void setPipelineStableId(String pipelineStableId) {
	
		this.pipelineStableId = pipelineStableId;
	}


	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + ((dataType == null) ? 0 : dataType.hashCode());
		result = prime * result + ((parameterId == null) ? 0 : parameterId.hashCode());
		result = prime * result + ((parameterName == null) ? 0 : parameterName.hashCode());
		result = prime * result + ((parameterStableId == null) ? 0 : parameterStableId.hashCode());
		result = prime * result + ((parameterStableKey == null) ? 0 : parameterStableKey.hashCode());
		result = prime * result + ((pipelineId == null) ? 0 : pipelineId.hashCode());
		result = prime * result + ((pipelineName == null) ? 0 : pipelineName.hashCode());
		result = prime * result + ((pipelineStableId == null) ? 0 : pipelineStableId.hashCode());
		result = prime * result + ((pipelineStableKey == null) ? 0 : pipelineStableKey.hashCode());
		result = prime * result + ((procedureId == null) ? 0 : procedureId.hashCode());
		result = prime * result + ((procedureName == null) ? 0 : procedureName.hashCode());
		result = prime * result + ((procedureStableId == null) ? 0 : procedureStableId.hashCode());
		result = prime * result + ((procedureStableKey == null) ? 0 : procedureStableKey.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {

		if (this == obj) { return true; }
		if (obj == null) { return false; }
		if (getClass() != obj.getClass()) { return false; }
		ImpressDTO other = (ImpressDTO) obj;
		if (dataType == null) {
			if (other.dataType != null) { return false; }
		} else if (!dataType.equals(other.dataType)) { return false; }
		if (parameterId == null) {
			if (other.parameterId != null) { return false; }
		} else if (!parameterId.equals(other.parameterId)) { return false; }
		if (parameterName == null) {
			if (other.parameterName != null) { return false; }
		} else if (!parameterName.equals(other.parameterName)) { return false; }
		if (parameterStableId == null) {
			if (other.parameterStableId != null) { return false; }
		} else if (!parameterStableId.equals(other.parameterStableId)) { return false; }
		if (parameterStableKey == null) {
			if (other.parameterStableKey != null) { return false; }
		} else if (!parameterStableKey.equals(other.parameterStableKey)) { return false; }
		if (pipelineId == null) {
			if (other.pipelineId != null) { return false; }
		} else if (!pipelineId.equals(other.pipelineId)) { return false; }
		if (pipelineName == null) {
			if (other.pipelineName != null) { return false; }
		} else if (!pipelineName.equals(other.pipelineName)) { return false; }
		if (pipelineStableId == null) {
			if (other.pipelineStableId != null) { return false; }
		} else if (!pipelineStableId.equals(other.pipelineStableId)) { return false; }
		if (pipelineStableKey == null) {
			if (other.pipelineStableKey != null) { return false; }
		} else if (!pipelineStableKey.equals(other.pipelineStableKey)) { return false; }
		if (procedureId == null) {
			if (other.procedureId != null) { return false; }
		} else if (!procedureId.equals(other.procedureId)) { return false; }
		if (procedureName == null) {
			if (other.procedureName != null) { return false; }
		} else if (!procedureName.equals(other.procedureName)) { return false; }
		if (procedureStableId == null) {
			if (other.procedureStableId != null) { return false; }
		} else if (!procedureStableId.equals(other.procedureStableId)) { return false; }
		if (procedureStableKey == null) {
			if (other.procedureStableKey != null) { return false; }
		} else if (!procedureStableKey.equals(other.procedureStableKey)) { return false; }
		return true;
	}


	@Override
	public String toString() {

		return "ImpressDTO [parameterId=" + parameterId + ", dataType=" + dataType + ", parameterStableId=" + parameterStableId + ", parameterStableKey=" + parameterStableKey + ", parameterName=" + parameterName + ", procedureId=" + procedureId + ", procedureStableId=" + procedureStableId + ", procedureStableKey=" + procedureStableKey + ", procedureName=" + procedureName + ", pipelineName=" + pipelineName + ", pipelineId=" + pipelineId + ", pipelineStableKey=" + pipelineStableKey + ", pipelineStableId=" + pipelineStableId + "]";
	}
	

	
}