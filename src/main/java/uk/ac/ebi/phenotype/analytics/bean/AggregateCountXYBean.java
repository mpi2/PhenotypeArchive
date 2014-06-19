package uk.ac.ebi.phenotype.analytics.bean;



public class AggregateCountXYBean {

	int aggregateCount = 0;
	String xValue = null;
	String xName = null;
	String xAttribute = null;
	String yValue = null;
	String yName = null;
	String yAttribute = null;
	
	/**
	 * @param aggregateCount
	 * @param xValue
	 * @param xName
	 * @param xAttribute
	 * @param yValue
	 * @param yName
	 * @param yAttribute
	 */
	public AggregateCountXYBean(int aggregateCount, String xValue,
			String xName, String xAttribute, String yValue, String yName,
			String yAttribute) {
		super();
		this.aggregateCount = aggregateCount;
		this.xValue = xValue;
		this.xName = xName;
		this.xAttribute = xAttribute;
		this.yValue = yValue;
		this.yName = yName;
		this.yAttribute = yAttribute;
	}

	/**
	 * @return the aggregateCount
	 */
	public int getAggregateCount() {
		return aggregateCount;
	}

	/**
	 * @param aggregateCount the aggregateCount to set
	 */
	public void setAggregateCount(int aggregateCount) {
		this.aggregateCount = aggregateCount;
	}

	/**
	 * @return the xValue
	 */
	public String getxValue() {
		return xValue;
	}

	/**
	 * @param xValue the xValue to set
	 */
	public void setxValue(String xValue) {
		this.xValue = xValue;
	}

	/**
	 * @return the xName
	 */
	public String getxName() {
		return xName;
	}

	/**
	 * @param xName the xName to set
	 */
	public void setxName(String xName) {
		this.xName = xName;
	}

	/**
	 * @return the xAttribute
	 */
	public String getxAttribute() {
		return xAttribute;
	}

	/**
	 * @param xAttribute the xAttribute to set
	 */
	public void setxAttribute(String xAttribute) {
		this.xAttribute = xAttribute;
	}

	/**
	 * @return the yValue
	 */
	public String getyValue() {
		return yValue;
	}

	/**
	 * @param yValue the yValue to set
	 */
	public void setyValue(String yValue) {
		this.yValue = yValue;
	}

	/**
	 * @return the yName
	 */
	public String getyName() {
		return yName;
	}

	/**
	 * @param yName the yName to set
	 */
	public void setyName(String yName) {
		this.yName = yName;
	}

	/**
	 * @return the yAttribute
	 */
	public String getyAttribute() {
		return yAttribute;
	}

	/**
	 * @param yAttribute the yAttribute to set
	 */
	public void setyAttribute(String yAttribute) {
		this.yAttribute = yAttribute;
	}

	

}
