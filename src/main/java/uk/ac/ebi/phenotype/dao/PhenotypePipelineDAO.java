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
package uk.ac.ebi.phenotype.dao;

/**
 * 
 * Phenotype pipeline data access manager interface.
 * 
 * @author Gautier Koscielny (EMBL-EBI) <koscieln@ebi.ac.uk>
 * @since May 2012
 */

import java.util.List;

import uk.ac.ebi.phenotype.pojo.Datasource;
import uk.ac.ebi.phenotype.pojo.Parameter;
import uk.ac.ebi.phenotype.pojo.ParameterIncrement;
import uk.ac.ebi.phenotype.pojo.ParameterOntologyAnnotation;
import uk.ac.ebi.phenotype.pojo.ParameterOption;
import uk.ac.ebi.phenotype.pojo.Pipeline;
import uk.ac.ebi.phenotype.pojo.Procedure;

public interface PhenotypePipelineDAO extends HibernateDAO {

	/**
	 * Get all pipelines in the system
	 * @return all pipelines
	 */
	public List<Pipeline> getAllPhenotypePipelines();

	/**
	 * Find a pipeline by its stable id.
	 * It will return the latest version of the pipeline
	 * @param name the pipeline stable id
	 * @return the pipeline
	 */
	public Pipeline getPhenotypePipelineByStableId(String stableId);
	
	/**
	 * Find a pipeline by its stable id.
	 * It will return the latest version of the pipeline
	 * @param name the pipeline stable id
	 * @return the pipeline
	 */
	public Pipeline getPhenotypePipelineByStableIdAndVersion(String stableId, int majorVersion, int minorVersion);
		
	/**
	 * Find a procedure by its stable id and versions
	 * It will return the latest version of the procedure
	 * @param name the procedure stable id
	 * @return the procedure
	 */
	public Procedure getProcedureByStableIdAndVersion(String stableId, int majorVersion, int minorVersion);	
	
	/**
	 * Find a parameter by its stable id and versions
	 * It will return the latest version of the parameter
	 * @param name the parameter stable id
	 * @return the parameter
	 */
	public Parameter getParameterByStableIdAndVersion(String stableId, int majorVersion, int minorVersion);
	
	public List<Parameter> getProcedureMetaDataParametersByStableIdAndVersion(String stableId, int majorVersion, int minorVersion);		
	
	public void savePipeline(Pipeline pipeline);
	public void saveProcedure(Procedure procedure);
	public void saveParameter(Parameter parameter);
	public void saveParameterOption(ParameterOption parameterOption);
	public void saveParameterIncrement(ParameterIncrement parameterIncrement);
	public void saveParameterOntologyAnnotation(ParameterOntologyAnnotation parameterOntologyAnnotation);
	
	/**
	 * Delete phenotype pipelines from a specific datasource;
	 * Status: experimental
	 * @param datasource
	 */
	public void deleteAllPipelinesByDatasource(Datasource datasource);
	
}
