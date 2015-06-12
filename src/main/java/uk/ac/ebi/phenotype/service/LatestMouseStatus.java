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
package uk.ac.ebi.phenotype.service;
/**
 * class to represent the mouse status at the gene level (not allele)
 * http://wwwdev.ebi.ac.uk/mi/impc/dev/solr/gene/select/?q=*:*&facet=true&facet.field=latest_mouse_status&rows=0
 * thre states currently: <int name="">43785</int>
<int name="Mice Produced">3707</int>
<int name="Assigned for Mouse Production and Phenotyping">1698</int>
but from Cks code it's not that simple and we have from the facets in the search others as well
 * @author jwarren
 *
 */
public enum LatestMouseStatus {
	NONE("none"),//will/should we get his status?
	ASSIGNED_FOR_MOUSE_PRODUCTION_AND_PHENOTYPING("Assigned for Mouse Production and Phenotyping"),
	MICE_PRODUCED("Mice Produced"),
	ES_CELLS_PRODUCED("ES Cells Produced"),
	ASSIGNED_FOR_ES_CELL_PRODUCTION("Assigned for ES Cell Production"),
	NOT_ASSIGNED_FOR_ES_CELL_PRODUCTION("Not Assigned for ES Cell Production");
	
	String label;
	private LatestMouseStatus(String label){
		this.label=label;
	}
	public String getName(){
		return this.label;
	}
}
