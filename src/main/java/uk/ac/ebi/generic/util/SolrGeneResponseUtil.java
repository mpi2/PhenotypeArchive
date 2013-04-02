package uk.ac.ebi.generic.util;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class SolrGeneResponseUtil {

	public  static String deriveGeneStatus(JSONObject doc) {

		// order of status: latest to oldest (IMPORTANT for deriving correct
		// status)
		// returns the latest status (6 statuses available)

		// Phenotype Data Available
	
			if (doc.has("imits_report_phenotyping_complete_date"))return "Phenotype Data Available";

		// Mice Produced
			if (doc.has("imits_report_genotype_confirmed_date"))return "Mice Produced";


		// Assigned for Mouse Production and Phenotyping
			boolean nonAssignedStatuses = false;
			boolean assignedStatuses = false;

			if (doc.has("imits_report_mi_plan_status")) {
				JSONArray plans = doc
						.getJSONArray("imits_report_mi_plan_status");
				for (Object p : plans) {
					if (p.toString().equals("Inactive")
							|| p.toString().equals("Withdrawn")) {
						nonAssignedStatuses = true;
					} else {
						assignedStatuses = true;
					}
				}
				if (!nonAssignedStatuses && assignedStatuses) {
					return "Assigned for Mouse Production and Phenotyping";
				}
			}
	

		// ES Cells Produced
		if (doc.has("escell"))return "ES Cells Produced";

		// Assigned for ES Cell Production
		if (doc.has("ikmc_project"))return "Assigned for ES Cell Production";

		// gets the oldest/initial status if none of the above applies
		return "Not Assigned for ES Cell Production";
	}
}
