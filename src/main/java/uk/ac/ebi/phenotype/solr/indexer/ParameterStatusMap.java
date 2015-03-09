package uk.ac.ebi.phenotype.solr.indexer;

import java.util.HashMap;
import java.util.Map;



public class ParameterStatusMap {
	
	private static final Map<String, String> paramIdToDescriptionMap=new HashMap<String, String>();
	private static final String mouseDiedString="Mouse died";
	private static final String mouseCulledString="Mouse culled";
	private static final String singleProcNotPerfoString="Single procedure not performed - welfare";
	private static final String singleProcNotPerfoScheduleString="Single procedure not performed - schedule";
	private static final String pipelineStoppedWelfare="Pipeline stopped - welfare";
	private static final String pipelineStoppedScheduling="Pipeline stopped - scheduling";
	private static final String procedureFailedEquipmentFailed = "Procedure Failed - Equipment Failed";
	private static final String procedureFailedEquipmentSampleLost = "Procedure Failed - Sample Lost";
	private static final String procedureFailedInsufficientSample = "Procedure Failed - Insufficient Sample";
	private static final String procedureFailedProcessFailed =  "Procedure Failed - Process Failed";
	private static final String procedureQcFailed = "Procedure QC Failed";
	private static final String paramNotMeasureEquipmentFailed =  "Parameter not measured - Equipment Failed";
	private static final String parameterNotMeasureSampleLost = "Parameter not measured - Sample Lost";
	private static final String parmeterNotMeasureInsufficientSample = "Parameter not measured - Insufficient sample";
	private static final String paramNotRecordedWelfareIssue =  "Parameter not recorded - welfare issue";
	private static final String freeTextOfIssues = "Free Text of Issues";
	private static final String extraInfo =  "Extra Information	e.g. For media parameters a link to the parameter associated with the picture (submitted like ESLIM_PARAMSC_005: ESLIM_013_001_014)";
	private static final String paramNotInSop = "Parameter not measured - not in SOP 	Not in SOP at time of measurement";

	 static {
		
		paramIdToDescriptionMap.put("ESLIM_PSC_001", mouseDiedString);
		paramIdToDescriptionMap.put("IMPC_PSC_001", mouseDiedString);
		
		paramIdToDescriptionMap.put("ESLIM_PSC_002", mouseCulledString);
		paramIdToDescriptionMap.put("IMPC_PSC_002", mouseCulledString);
		
		paramIdToDescriptionMap.put("ESLIM_PSC_003", singleProcNotPerfoString);
		paramIdToDescriptionMap.put("IMPC_PSC_003", singleProcNotPerfoString);
		
		
		paramIdToDescriptionMap.put("ESLIM_PSC_004", singleProcNotPerfoScheduleString);
		paramIdToDescriptionMap.put("IMPC_PSC_004", singleProcNotPerfoScheduleString);
		
		
		paramIdToDescriptionMap.put("ESLIM_PSC_005", pipelineStoppedWelfare);
		paramIdToDescriptionMap.put("IMPC_PSC_005", pipelineStoppedWelfare);
		
		paramIdToDescriptionMap.put("ESLIM_PSC_006", pipelineStoppedScheduling);
		paramIdToDescriptionMap.put("IMPC_PSC_006", pipelineStoppedScheduling);
		
		paramIdToDescriptionMap.put("ESLIM_PSC_007", procedureFailedEquipmentFailed);
		paramIdToDescriptionMap.put("IMPC_PSC_007", procedureFailedEquipmentFailed);
		
		paramIdToDescriptionMap.put("ESLIM_PSC_008", procedureFailedEquipmentSampleLost);
		paramIdToDescriptionMap.put("ESLIM_PSC_008", procedureFailedEquipmentSampleLost);
		
		paramIdToDescriptionMap.put("ESLIM_PSC_009", procedureFailedInsufficientSample);
		paramIdToDescriptionMap.put("IMPC_PSC_009", procedureFailedInsufficientSample);
		
		paramIdToDescriptionMap.put("ESLIM_PSC_010",procedureFailedProcessFailed);
		paramIdToDescriptionMap.put("IMPC_PSC_010", procedureFailedProcessFailed);
		
		paramIdToDescriptionMap.put("IMPC_PSC_011", procedureQcFailed);
		
		paramIdToDescriptionMap.put("IMPC_PSC_012", "LIMS not ready yet");
		paramIdToDescriptionMap.put("IMPC_PSC_013", "Software failure");
		paramIdToDescriptionMap.put("IMPC_PSC_014",	"Uncooperative mouse");
		paramIdToDescriptionMap.put("IMPC_PSC_015","Free Text of Issues");
		
		paramIdToDescriptionMap.put("ESLIM_PARAMSC_001",paramNotMeasureEquipmentFailed);
		paramIdToDescriptionMap.put("IMPC_PARAMSC_001", paramNotMeasureEquipmentFailed);
		
		paramIdToDescriptionMap.put("ESLIM_PARAMSC_002", parameterNotMeasureSampleLost);
		paramIdToDescriptionMap.put("IMPC_PARAMSC_002", parameterNotMeasureSampleLost);
		
		paramIdToDescriptionMap.put("ESLIM_PARAMSC_003", parmeterNotMeasureInsufficientSample);
		paramIdToDescriptionMap.put("IMPC_PARAMSC_003",parmeterNotMeasureInsufficientSample);
		
		paramIdToDescriptionMap.put("ESLIM_PARAMSC_004",paramNotRecordedWelfareIssue);
		paramIdToDescriptionMap.put("IMPC_PARAMSC_004", paramNotRecordedWelfareIssue);
		
		paramIdToDescriptionMap.put("ESLIM_PARAMSC_005", freeTextOfIssues);
		paramIdToDescriptionMap.put("IMPC_PARAMSC_005", freeTextOfIssues);
		
		paramIdToDescriptionMap.put("ESLIM_PARAMSC_006",extraInfo);

		paramIdToDescriptionMap.put("IMPC_PARAMSC_006", extraInfo);
		
		paramIdToDescriptionMap.put("ESLIM_PARAMSC_007", paramNotInSop);
		paramIdToDescriptionMap.put("IMPC_PARAMSC_007",paramNotInSop);
		
		
		paramIdToDescriptionMap.put("IMPC_PARAMSC_008",
				"Above upper limit of quantitation");
		paramIdToDescriptionMap.put("IMPC_PARAMSC_009",
				"Below lower limit of quantitation");
		paramIdToDescriptionMap.put("IMPC_PARAMSC", "010	Parameter QC Failed");
		paramIdToDescriptionMap.put("IMPC_PARAMSC_011", "LIMS not ready yet");
		paramIdToDescriptionMap.put("IMPC_PARAMSC_012", "Software failure");
		paramIdToDescriptionMap.put("IMPC_PARAMSC_013", "Uncooperative mouse");
		paramIdToDescriptionMap.put("IMPC_PARAMSC_014",
				"Parameter not measured - Equipment Incompatible");

		paramIdToDescriptionMap.put("IMPC_SSC_001", "Genotyping failed");
		paramIdToDescriptionMap.put("IMPC_SSC_002", "Health Issue");
		paramIdToDescriptionMap.put("IMPC_SSC_003", "Free Text of Issues");
		
	}
	
	/**
	 * Should handle when an entry and when just free text
	 * @param parameterStatusString
	 * @return
	 */
	public static String getParameterStatusDescription(String parameterStatusString) {
		if(parameterStatusString!=null) {
		if(paramIdToDescriptionMap.containsKey(parameterStatusString)) {
			String  valueString=paramIdToDescriptionMap.get(parameterStatusString);
			return valueString;
		}else {
			//if not a parameterStatus code we should just return the String submitted as just free text
			if(parameterStatusString.contains("IMPC") || parameterStatusString.contains("ESLIM")) {
				System.err.println("looks like this could be a parameterStatus code but we didn't find one in map="+parameterStatusString);
			}
			//need to cope with situation where ESLIM_PARAMSC_005:missing data unknown reason
			//which is common with extra info after the :
			//or do we actually need to do anything? just write out the parameterStatus?
			//try grepping some of these to see what exactly we can expect
			return parameterStatusString;
		}
		}return "";
	}

}
