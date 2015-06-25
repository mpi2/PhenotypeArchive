/*******************************************************************************
 * Copyright 2015 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 *******************************************************************************/
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
