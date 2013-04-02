package uk.ac.ebi.phenotype.dao;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


public class DiscreteTimePoint implements Comparable{
	
	private List<Float> errorPair;//if this is the mean of a set we can store the error bars data and other data such as Standard Deviation etc?
	
	public List<Float> getErrorPair() {
		return errorPair;
	}
	public void setErrorPair(List<Float> errorPair) {
		this.errorPair = errorPair;
	}
	public DiscreteTimePoint(Float discreteTime, Float discreteDataPoint){
		this.discreteTime=discreteTime;
		this.data=discreteDataPoint;
	}
	
private Float discreteTime;
 public Float getDiscreteTime() {
	return discreteTime;
}
public void setDiscreteTime(Float discretePoint) {
	this.discreteTime = discretePoint;
}

public Float getData() {
	return data;
}
public void setData(Float data) {
	this.data = data;
}
private Float data;
 
public String toString(){
	//String myString = DateFormat.getDateInstance(DateFormat.FULL).format(date);
	return "["+discreteTime+" ,"+ data+"]";
	
}

@Override
public int compareTo(Object arg0) {
	DiscreteTimePoint p = (DiscreteTimePoint) arg0; 
	if(p.getDiscreteTime()>this.getDiscreteTime()){
		return -1;
	}else{
		return 1;
	}

}


}
