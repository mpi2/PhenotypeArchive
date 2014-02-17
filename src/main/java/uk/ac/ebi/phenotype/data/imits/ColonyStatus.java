/**
 * Copyright © 2011-2014 EMBL - European Bioinformatics Institute
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License.  
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.phenotype.data.imits;

/**
 * 
 * Represent the status of a mouse colony in IMITS
 * @author Gautier Koscielny
 * @since April 2013
 */

public class ColonyStatus {

	String colonyID;
	String phenotypeStatus;
	String productionStatus;
	String alleleType;
	String alleleName;
	String backgroundStrain;
	String phenotypeCenter;
	int phenotypeStarted = 0;
	int phenotypeCompleted = 0;


	/**
	 * @param colonyID
	 * @param phenotypeStatus
	 * @param productionStatus
	 * @param alleleType
	 * @param phenotypeStarted
	 * @param phenotypeCompleted
	 */
	public ColonyStatus(String colonyID, String phenotypeStatus,
			String productionStatus, String alleleType, int phenotypeStarted,
			int phenotypeCompleted) {
		super();
		this.colonyID = colonyID;
		this.phenotypeStatus = phenotypeStatus;
		this.productionStatus = productionStatus;
		this.alleleType = alleleType;
		this.phenotypeStarted = phenotypeStarted;
		this.phenotypeCompleted = phenotypeCompleted;
	}



	public ColonyStatus(String alleleName) {
		this.alleleName=alleleName;
	}



	/**
	 * @return the phenotypeCenter
	 */
	public String getPhenotypeCenter() {
		return phenotypeCenter;
	}



	/**
	 * @param phenotypeCenter the phenotypeCenter to set
	 */
	public void setPhenotypeCenter(String phenotypeCenter) {
		this.phenotypeCenter = phenotypeCenter;
	}



	/**
	 * @return the backgroundStrain
	 */
	public String getBackgroundStrain() {
		return backgroundStrain;
	}



	/**
	 * @param backgroundStrain the backgroundStrain to set
	 */
	public void setBackgroundStrain(String backgroundStrain) {
		this.backgroundStrain = backgroundStrain;
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
	 * @return the phenotypeStarted
	 */
	public int getPhenotypeStarted() {
		return phenotypeStarted;
	}



	/**
	 * @param phenotypeStarted the phenotypeStarted to set
	 */
	public void setPhenotypeStarted(int phenotypeStarted) {
		this.phenotypeStarted = phenotypeStarted;
	}



	/**
	 * @return the phenotypeCompleted
	 */
	public int getPhenotypeCompleted() {
		return phenotypeCompleted;
	}



	/**
	 * @param phenotypeCompleted the phenotypeCompleted to set
	 */
	public void setPhenotypeCompleted(int phenotypeCompleted) {
		this.phenotypeCompleted = phenotypeCompleted;
	}



	/**
	 * @return the alleleType
	 */
	public String getAlleleType() {
		return alleleType;
	}

	
	
	/**
	 * @param alleleType the alleleType to set
	 */
	public void setAlleleType(String alleleType) {
		this.alleleType = alleleType;
	}

	/**
	 * @return the colonyID
	 */
	public String getColonyID() {
		return colonyID;
	}
	/**
	 * @param colonyID the colonyID to set
	 */
	public void setColonyID(String colonyID) {
		this.colonyID = colonyID;
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
	 * @return the productionStatus
	 */
	public String getProductionStatus() {
		return productionStatus;
	}
	/**
	 * @param productionStatus the productionStatus to set
	 */
	public void setProductionStatus(String productionStatus) {
		this.productionStatus = productionStatus;
	}
	
	public String toString(){
		return this.getAlleleName() +"\talleleType="+this.alleleType+"\tbackgroundStrain=" + this.getBackgroundStrain() + "\tcolonyId=" + this.getColonyID() + "\tphenotypeStatus=" + this.getPhenotypeStatus() + "\tphenotypeCenter=" + this.getPhenotypeCenter() + "\tphenotypeStarted:" + this.getPhenotypeStarted() + "\tphenotypeCompleted:"+this.getPhenotypeCompleted()+"\tproductionStatus="+this.getProductionStatus();
		
	}
	
}
