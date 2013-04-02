/**
 * Copyright © 2011-2013 EMBL - European Bioinformatics Institute
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License.  
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package uk.ac.ebi.phenotype.imaging.utils;

import java.io.PrintWriter;

/**
 * Just a helper class to write solr fields in xml document for posting to solr to create index
 * @Author jw12
 */
public class CreateSolrIndexerUtil {

	private String nl;
	public CreateSolrIndexerUtil(){
		nl=System.getProperty("line.separator");
	}
	protected void createField(PrintWriter out, String name, String content){
		//only print if the content is not null
		if(content!=null && !content.equals("") && !content.contains("\u0000")){
                    content=content.replace("<a href", "");
                    content=content.replace("</a>", "");
                    content=content.replace(">","&gt;");
                    content=content.replace("<","&lt");
		String line="<field name=\""+name+"\">"+content+"</field>"+nl;
		line=line.replace("&", "");
		//System.out.println(".");
		out.write(line);
		}
	}
	/**
	 * @return
	 */
	public String getLineSeperator() {
		return nl;
	}
}
