package uk.ac.ebi.phenotype.ontology;

import java.util.ArrayList;

public class PhenotypeSummaryBySex {
	private ArrayList <PhenotypeSummaryType> malePhens;
	private ArrayList <PhenotypeSummaryType> femalePhens;
	private ArrayList <PhenotypeSummaryType> bothPhens;
	int total = 0;
	
	public PhenotypeSummaryBySex(){
		malePhens = new ArrayList<PhenotypeSummaryType>();
		femalePhens = new ArrayList<PhenotypeSummaryType>();
		bothPhens = new ArrayList<PhenotypeSummaryType>();
	}
	
	public void addPhenotye ( PhenotypeSummaryType obj) throws Exception{
		String sex = obj.getSex();
		if (sex.equals("male"))
			malePhens.add(obj);
		else if (sex.equals("female"))
			femalePhens.add(obj);
		else if (sex.equals("both sexes"))
			bothPhens.add(obj);
		else throw (new Exception("Object of type PhenotypeSummaryTuype recieved without valid sex field."));
		
	}
	
	public ArrayList <PhenotypeSummaryType> getMalePhenotypes(){
		return malePhens;
	}
	
	public ArrayList <PhenotypeSummaryType> getFemalePhenotypes(){
		return femalePhens;
	}
	
	public ArrayList <PhenotypeSummaryType> getBothPhenotypes(){
		return bothPhens;
	}
	
	public int getTotalPhenotypesNumber(){
		for (PhenotypeSummaryType entry: malePhens)
			total += entry.getNumberOfEntries();		
		for (PhenotypeSummaryType entry: femalePhens)
			total += entry.getNumberOfEntries();		
		for (PhenotypeSummaryType entry: bothPhens)
			total += entry.getNumberOfEntries();
		return total;
	}
	
}
