package uk.ac.ebi.phenotype.stats;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import uk.ac.ebi.phenotype.pojo.GenomicFeature;
import uk.ac.ebi.phenotype.pojo.MetaData;
import uk.ac.ebi.phenotype.pojo.PhenotypeCallSummary;
import uk.ac.ebi.phenotype.pojo.Pipeline;
import uk.ac.ebi.phenotype.pojo.Procedure;

public class PipelineProcedureTablesCreator {

	public List<PipelineProcedureData> createArraysForTables(
			List<Pipeline> pipelines,
			List<PhenotypeCallSummary> allPhenotypeSummariesForGene,
			GenomicFeature gene) {

		List<PipelineProcedureData> pipeProcDataList = new ArrayList<PipelineProcedureData>();

		Set<Integer> pipelineIds = new TreeSet<Integer>();

		for (PhenotypeCallSummary phenoSummary : allPhenotypeSummariesForGene) {
			pipelineIds.add(phenoSummary.getPipeline().getId());
		}

		// for each pipeline get the procedures and arrange them by
		// ascending weeks they belong to
		for (Pipeline pipe : pipelines) {

			//only display data if we have data for this particular pipeline in the phenotype call summary table
			if (pipelineIds.contains(pipe.getId())) {
				PipelineProcedureData tempData = new PipelineProcedureData(pipe.getName(), allPhenotypeSummariesForGene);

				for (Procedure proc : pipe.getProcedures()) {
					tempData.add(getWeek(proc), proc);
				}

				tempData.calculateDataStructure();
				pipeProcDataList.add(tempData);
			}
		}

		return pipeProcDataList;
	}

	private Integer getWeek(Procedure proc) {
		for (MetaData mData : proc.getMetaDataSet()) {
			if (mData.getName().equals("week")) {
				return Integer.valueOf(mData.getValue());
			}
		}

		// if no value return null as 0 will mean 0 weeks old
		return PipelineProcedureData.NO_WEEK_FOR_PROCEDURE;
	}
}
