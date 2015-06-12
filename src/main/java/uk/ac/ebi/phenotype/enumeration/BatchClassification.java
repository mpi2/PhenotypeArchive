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
package uk.ac.ebi.phenotype.enumeration;

/**
 * Captures the possible states of the batch classification
 *
 * not_applicable = when the assay doesn't have an associated workflow (i.e. viability)
 * one_sex_only = only one sex tested
 * one_batch = all data for both males and females was gathered on the same day
 * low_batch = data was gathered on 3 or fewer days, or one sex < 3 batches or the other < 2
 * multi_batch = one sex >= 3 batches and the other >= 2 batches
 *
 */
public enum BatchClassification {

	not_applicable,
	one_sex_only,
	one_batch,
	low_batch,
	multi_batch;

	public String getName(){
		return this.toString();
	}

}
