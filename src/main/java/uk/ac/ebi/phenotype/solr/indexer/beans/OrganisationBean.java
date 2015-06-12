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

/**
 * Organisation DTO
 */
public class OrganisationBean {
	Integer id;
	String name;
	String fullname;
	String country;


	public Integer getId() {

		return id;
	}


	public void setId(Integer id) {

		this.id = id;
	}


	public String getName() {

		return name;
	}


	public void setName(String name) {

		this.name = name;
	}


	public String getFullname() {

		return fullname;
	}


	public void setFullname(String fullname) {

		this.fullname = fullname;
	}


	public String getCountry() {

		return country;
	}


	public void setCountry(String country) {

		this.country = country;
	}


	@Override
	public boolean equals(Object o) {

		if (this == o) return true;
		if (!(o instanceof OrganisationBean)) return false;

		OrganisationBean that = (OrganisationBean) o;

		if (country != null ? !country.equals(that.country) : that.country != null) return false;
		if (fullname != null ? !fullname.equals(that.fullname) : that.fullname != null) return false;
		if (id != null ? !id.equals(that.id) : that.id != null) return false;
		if (name != null ? !name.equals(that.name) : that.name != null) return false;

		return true;
	}


	@Override
	public int hashCode() {

		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + (name != null ? name.hashCode() : 0);
		result = 31 * result + (fullname != null ? fullname.hashCode() : 0);
		result = 31 * result + (country != null ? country.hashCode() : 0);
		return result;
	}
}
