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
package uk.ac.ebi.phenotype.solr.indexer.beans;

import org.apache.solr.client.solrj.beans.Field;

/**
 * @author Matt Pearce
 */
public class SangerAlleleBean {

	public static final String MGI_ACCESSION_ID = "mgi_accession_id";
	public static final String ALLELE_NAME = "allele_name";
	public static final String ES_CELL_STATUS = "es_cell_status";
	public static final String MOUSE_STATUS = "mouse_status";
	public static final String PHENOTYPE_STATUS = "phenotype_status";
	public static final String PRODUCTION_CENTRE = "production_centre";
	public static final String PHENOTYPING_CENTRE = "phenotyping_centre";

	@Field(MGI_ACCESSION_ID)
	private String mgiAccessionId;

	@Field(ALLELE_NAME)
	private String alleleName;

	@Field(ES_CELL_STATUS)
	private String esCellStatus;

	@Field(MOUSE_STATUS)
	private String mouseStatus;

	@Field(PHENOTYPE_STATUS)
	private String phenotypeStatus;

	@Field(PRODUCTION_CENTRE)
	private String productionCentre;

	@Field(PHENOTYPING_CENTRE)
	private String phenotypingCentre;


	public String getMgiAccessionId() {

		return mgiAccessionId;
	}


	public void setMgiAccessionId(String mgiAccessionId) {

		this.mgiAccessionId = mgiAccessionId;
	}


	/**
	 * @return the alleleName
	 */
	public String getAlleleName() {
		return alleleName;
	}

	/**
	 * @param alleleName the alleleName to set
	 */
	public void setAlleleName(String alleleName) {
		this.alleleName = alleleName;
	}

	/**
	 * @return the imitsEsCellStatus
	 */
	public String getEsCellStatus() {
		return esCellStatus;
	}

	/**
	 * @param imitsEsCellStatus the imitsEsCellStatus to set
	 */
	public void setEsCellStatus(String imitsEsCellStatus) {
		this.esCellStatus = imitsEsCellStatus;
	}

	/**
	 * @return the imitsMouseStatus
	 */
	public String getMouseStatus() {
		return mouseStatus;
	}

	/**
	 * @param imitsMouseStatus the imitsMouseStatus to set
	 */
	public void setMouseStatus(String imitsMouseStatus) {
		this.mouseStatus = imitsMouseStatus;
	}

	/**
	 * @return the phenotypeStatus
	 */
	public String getPhenotypeStatus() {
		return phenotypeStatus;
	}

	/**
	 * @param phenotypeStatus the phenotypeStatus to set
	 */
	public void setPhenotypeStatus(String phenotypeStatus) {
		this.phenotypeStatus = phenotypeStatus;
	}

	/**
	 * @return the productionCentre
	 */
	public String getProductionCentre() {
		return productionCentre;
	}

	/**
	 * @param productionCentre the productionCentre to set
	 */
	public void setProductionCentre(String productionCentre) {
		this.productionCentre = productionCentre;
	}

	/**
	 * @return the phenotypingCentre
	 */
	public String getPhenotypingCentre() {
		return phenotypingCentre;
	}

	/**
	 * @param phenotypingCentre the phenotypingCentre to set
	 */
	public void setPhenotypingCentre(String phenotypingCentre) {
		this.phenotypingCentre = phenotypingCentre;
	}

}
