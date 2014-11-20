package uk.ac.ebi.phenotype.service.dto;

import java.util.List;


public class ResponseWrapper<E> {
	List<E> list;
	Long totalNumberFound;
		
	public ResponseWrapper(List<E> list){
		this.list=list;
	}
	public Long getTotalNumberFound() {
	
		return totalNumberFound;
	}
	
	public void setTotalNumberFound(Long totalNumberFound) {
	
		this.totalNumberFound = totalNumberFound;
	}
	
	public List<E> getList() {
	
		return list;
	}
	public void setList(List<E> list) {
		this.list = list;
	}
	
	
}