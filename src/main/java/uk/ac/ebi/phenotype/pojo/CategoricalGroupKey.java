package uk.ac.ebi.phenotype.pojo;


public class CategoricalGroupKey {

	private Parameter parameter;
	private Integer populationId;
	private SexType sex;
	private ZygosityType zygosity;
	private String category;

	public CategoricalGroupKey(Parameter parameter, Integer populationId, SexType sex, ZygosityType zygosity, String category) {
		this.parameter = parameter;
		this.populationId = populationId;
		this.sex = sex;
		this.zygosity = zygosity;
		this.category = category;
	}

	public CategoricalGroupKey() {
		super();
	}

	public Parameter getParameter() {
		return parameter;
	}
	public void setParameter(Parameter parameter) {
		this.parameter = parameter;
	}
	public Integer getPopulationId() {
		return populationId;
	}
	public void setPopulationId(Integer populationId) {
		this.populationId = populationId;
	}
	public SexType getSex() {
		return sex;
	}
	public void setSex(SexType sex) {
		this.sex = sex;
	}
	public ZygosityType getZygosity() {
		return zygosity;
	}
	public void setZygosity(ZygosityType zygosity) {
		this.zygosity = zygosity;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((category == null) ? 0 : category.hashCode());
		result = prime * result
				+ ((parameter == null) ? 0 : parameter.hashCode());
		result = prime * result
				+ ((populationId == null) ? 0 : populationId.hashCode());
		result = prime * result + ((sex == null) ? 0 : sex.hashCode());
		result = prime * result
				+ ((zygosity == null) ? 0 : zygosity.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CategoricalGroupKey other = (CategoricalGroupKey) obj;
		if (category == null) {
			if (other.category != null)
				return false;
		} else if (!category.equals(other.category))
			return false;
		if (parameter == null) {
			if (other.parameter != null)
				return false;
		} else if (!parameter.equals(other.parameter))
			return false;
		if (populationId == null) {
			if (other.populationId != null)
				return false;
		} else if (!populationId.equals(other.populationId))
			return false;
		if (sex != other.sex)
			return false;
		if (zygosity != other.zygosity)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "CategoricalGroupKey [parameter=" + parameter
				+ ", populationId=" + populationId + ", sex=" + sex
				+ ", zygosity=" + zygosity + ", category=" + category + "]";
	}
	
	
}
