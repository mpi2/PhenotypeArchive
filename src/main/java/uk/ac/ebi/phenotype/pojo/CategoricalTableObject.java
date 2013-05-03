package uk.ac.ebi.phenotype.pojo;

import java.util.List;

public class CategoricalTableObject {
private String title;
private List <String>headers;
private String sexType;
private List<String> xAxisCategories;
private List<String> categories;
private List<List<Long>>seriesDataForCategoricalType;
private String renderTo;

public String getTitle() {
	return title;
}
public void setTitle(String title) {
	this.title = title;
}
public List<String> getHeaders() {
	return headers;
}
public void setHeaders(List<String> headers) {
	this.headers = headers;
}
public String getSexType() {
	return sexType;
}
public void setSexType(String sexType) {
	this.sexType = sexType;
}
public List<String> getxAxisCategories() {
	return xAxisCategories;
}
public void setxAxisCategories(List<String> xAxisCategories) {
	this.xAxisCategories = xAxisCategories;
}
public List<String> getCategories() {
	return categories;
}
public void setCategories(List<String> categories) {
	this.categories = categories;
}
public List<List<Long>> getSeriesDataForCategoricalType() {
	return seriesDataForCategoricalType;
}
public void setSeriesDataForCategoricalType(
		List<List<Long>> seriesDataForCategoricalType) {
	this.seriesDataForCategoricalType = seriesDataForCategoricalType;
}
public String getRenderTo() {
	return renderTo;
}
public void setRenderTo(String renderTo) {
	this.renderTo = renderTo;
}


}
