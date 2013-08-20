package uk.ac.ebi.phenotype.stats.unidimensional;

import uk.ac.ebi.phenotype.pojo.SexType;
import uk.ac.ebi.phenotype.pojo.ZygosityType;

public class UnidimensionalStatsObject {
private String line="Control";//if not set we assume control
private ZygosityType zygosity=ZygosityType.homozygote;
private SexType sexType=SexType.male;
private Float mean=new Float(0);
private Float sd=new Float(0);
private Integer sampleSize=100;
private String allele="allele should be set here";
private String geneticBackground="genetic background should be here";
public String getGeneticBackground() {
	return geneticBackground;
}
public void setGeneticBackground(String geneticBackground) {
	this.geneticBackground = geneticBackground;
}
public String getAllele() {
	return allele;
}
public void setAllele(String allele) {
	this.allele = allele;
}
public String getLine() {
	return line;
}
public void setLine(String line) {
	this.line = line;
}
public ZygosityType getZygosity() {
	return zygosity;
}
public void setZygosity(ZygosityType zygosity) {
	this.zygosity = zygosity;
}
public SexType getSexType() {
	return sexType;
}
public void setSexType(SexType sexType) {
	this.sexType = sexType;
}
public Float getMean() {
	return mean;
}
public void setMean(Float mean) {
	this.mean = mean;
}
public Float getSd() {
	return sd;
}
public void setSd(Float sd) {
	this.sd = sd;
}
public Integer getSampleSize() {
	return sampleSize;
}
public void setSampleSize(Integer sampleSize) {
	this.sampleSize = sampleSize;
}

}
