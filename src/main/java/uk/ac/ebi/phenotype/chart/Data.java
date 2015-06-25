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
package uk.ac.ebi.phenotype.chart;

public class Data {
	private String parameterId="";
	/**
	 * get the ParameterId of this if we want it for a link i.e. there is one in the phenotype call summary table
	 * @return
	 */
	public String getParameterId() {
		return parameterId;
	}

	public void setParameterId(String parameterId) {
		this.parameterId = parameterId;
	}
	private int colspan=1;
	public Data(String string) {
		this.dataString=string;
	}
	
	public Data(String string, String paramId) {
		this.dataString=string;
		this.parameterId=paramId;
	}
	
	public Data(String string, int colspan) {
		this.dataString=string;
		this.colspan=colspan;
	}
	public int getColspan() {
		return colspan;
	}
	public void setColspan(int colspan) {
		this.colspan = colspan;
	}
	public String getDataString() {
		return dataString;
	}
	public void setDataString(String dataString) {
		this.dataString = dataString;
	}
	private String dataString="";
	
	@Override
	public String toString(){
		return "'"+dataString+"' colspan="+colspan;
	}
}
