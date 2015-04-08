package uk.ac.ebi.phenotype.pojo;

/**
 * Enumeration of allowed stage types from IMPRESS
 * see:  {@link https://github.com/mpi2/exportlibrary/blob/master/exportlibrary.datastructure/src/main/resources/schemas/core/common.xsd}
 */
public enum StageUnitType {
	DPC("DPC"),
	THEILER("Theiler");

	private final String stageUnit;

	StageUnitType(String stageUnit) {
		this.stageUnit = stageUnit;
	}

	public String getStageUnitName() {return stageUnit;}

}
