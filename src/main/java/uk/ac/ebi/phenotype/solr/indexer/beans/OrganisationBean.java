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
