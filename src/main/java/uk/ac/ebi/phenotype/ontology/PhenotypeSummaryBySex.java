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
