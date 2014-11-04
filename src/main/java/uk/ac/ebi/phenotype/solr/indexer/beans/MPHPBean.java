/**
 * Copyright (c) 2014 Lemur Consulting Ltd.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.phenotype.solr.indexer.beans;

import org.apache.solr.client.solrj.beans.Field;

/**
 * @author Matt Pearce
 *
 */
public class MPHPBean {
	
	@Field("mp_id")
	private String mpId;
	
	@Field("hp_id")
	private String hpId;
	
	@Field("hp_term")
	private String hpTerm;

	/**
	 * @return the mpId
	 */
	public String getMpId() {
		return mpId;
	}

	/**
	 * @param mpId the mpId to set
	 */
	public void setMpId(String mpId) {
		this.mpId = mpId;
	}

	/**
	 * @return the hpId
	 */
	public String getHpId() {
		return hpId;
	}

	/**
	 * @param hpId the hpId to set
	 */
	public void setHpId(String hpId) {
		this.hpId = hpId;
	}

	/**
	 * @return the hpTerm
	 */
	public String getHpTerm() {
		return hpTerm;
	}

	/**
	 * @param hpTerm the hpTerm to set
	 */
	public void setHpTerm(String hpTerm) {
		this.hpTerm = hpTerm;
	}

}
