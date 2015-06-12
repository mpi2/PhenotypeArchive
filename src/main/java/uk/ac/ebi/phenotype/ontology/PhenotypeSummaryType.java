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
import java.util.HashSet;


public class PhenotypeSummaryType {
	private String id; // mp top level term id
	private String name;
	private String sex;
	private long  numberOfEntries;
	private HashSet <String> dataSources;
	
	public PhenotypeSummaryType (String mpId, String mpName, String sex, long numberOfEntries, HashSet <String> dataSources){
		this.id = mpId;
		this.name = mpName;
		this.sex = sex;
		this.numberOfEntries = numberOfEntries;
		this.dataSources = dataSources;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getSex() {
		return sex;
	}

	public long getNumberOfEntries() {
		return numberOfEntries;
	}

	public ArrayList<String> getDataSources() {
		return new ArrayList<String>(dataSources);
	}
	
	public String getGroup(){// grouping of mp top level terms done by Terry
		if (this.name.equalsIgnoreCase("immune system phenotype") || this.name.equalsIgnoreCase("hematopoietic system phenotype") ){
			return "immune system phenotype or hematopoietic system phenotype";
		}
		else if (this.name.equalsIgnoreCase("behavior/neurological phenotype") || this.name.equalsIgnoreCase("nervous system phenotype") ){
			return "behavior/neurological phenotype or nervous system phenotype";
		}
		else if (this.name.equalsIgnoreCase("digestive/alimentary phenotype") || this.name.equalsIgnoreCase("liver biliary system phenotype") ){
			return "digestive/alimentary phenotype or liver/biliary system phenotype";
		}
		else if (this.name.equalsIgnoreCase("homeostasis/metabolism phenotype") || this.name.equalsIgnoreCase("adipose tissue phenotype") ){
			return "homeostasis/metabolism phenotype or adipose tissue phenotype";
		}
		else if (this.name.equalsIgnoreCase("integument phenotype") || this.name.equalsIgnoreCase("pigmentation phenotype") ){
			return "integument phenotype or pigmentation phenotype";
		}
		else if ( this.name.equalsIgnoreCase("muscle phenotype") || this.name.equalsIgnoreCase("renal/urinary system phenotype") || this.name.equalsIgnoreCase("reproductive system phenotype") ||
		this.name.equalsIgnoreCase("respiratory system phenotype") || this.name.equalsIgnoreCase("craniofacial phenotype") || this.name.equalsIgnoreCase("hearing/vestibular/ear phenotype") ||
		this.name.equalsIgnoreCase("limbs/digits/tail phenotype") || this.name.equalsIgnoreCase("cardiovascular system phenotype") || this.name.equalsIgnoreCase("mortality/aging") ||
		this.name.equalsIgnoreCase("skeleton phenotype") || this.name.equalsIgnoreCase("growth/size/body phenotype") || this.name.equalsIgnoreCase("vision/eye phenotype")){
			return this.name;			
		}
				
		return "NA";
	}

}
