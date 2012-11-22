/**
 * Copyright © 2011-2012 EMBL - European Bioinformatics Institute
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
 * Phenotype pipeline data access manager implementation.
 * 
 * @author Gautier Koscielny (EMBL-EBI) <koscieln@ebi.ac.uk>
 * @since May 2012
 */

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.ebi.phenotype.pojo.Parameter;
import uk.ac.ebi.phenotype.pojo.Pipeline;
import uk.ac.ebi.phenotype.pojo.Procedure;
import uk.ac.ebi.phenotype.pojo.Project;
import uk.ac.ebi.phenotype.pojo.Strain;



public class PhenotypePipelineDAOImpl extends HibernateDAOImpl implements PhenotypePipelineDAO {

	/**
	 * Creates a new Hibernate pipeline data access manager.
	 * @param sessionFactory the Hibernate session factory
	 */
	public PhenotypePipelineDAOImpl(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public List<Pipeline> getAllPhenotypePipelines() {
		Session session = getCurrentSession();
		List<Pipeline> pipelines = getCurrentSession().createQuery("from Pipeline").list();
		return pipelines;
	}

	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public Pipeline getPhenotypePipelineByStableId(String stableId) {
		return (Pipeline) getCurrentSession().createQuery("from Pipeline as p where p.stableId = ?").setString(0, stableId).uniqueResult();
	}

	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public Pipeline getPhenotypePipelineByStableIdAndVersion(String stableId, int majorVersion, int minorVersion) {
		return (Pipeline) getCurrentSession().createQuery("from Pipeline as p where p.stableId = ? and p.majorVersion = ? and p.minorVersion = ?")
				.setString(0, stableId)
				.setInteger(1, majorVersion)
				.setInteger(2, minorVersion)
				.uniqueResult();
	}
	
	
	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public Procedure getProcedureByStableIdAndVersion(String stableId, int majorVersion, int minorVersion) {
		return (Procedure) getCurrentSession().createQuery("from Procedure as p where p.stableId = ? and p.majorVersion = ? and p.minorVersion = ?")
				.setString(0, stableId)
				.setInteger(1, majorVersion)
				.setInteger(2, minorVersion)
				.uniqueResult();
	}
	
	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public Parameter getParameterByStableIdAndVersion(String stableId, int majorVersion, int minorVersion) {
		return (Parameter) getCurrentSession().createQuery("from Parameter as p where p.stableId = ? and p.majorVersion = ? and p.minorVersion = ?")
				.setString(0, stableId)
				.setInteger(1, majorVersion)
				.setInteger(2, minorVersion)
				.uniqueResult();
	}
	
	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public List<Parameter> getProcedureMetaDataParametersByStableIdAndVersion(String stableId, int majorVersion, int minorVersion) {
		List<Parameter> parameters =   getCurrentSession().createQuery("select param from Parameter as param inner join param.procedure as proc where proc.stableId = ? and param.majorVersion = ? and param.minorVersion = ? and param.metaDataFlag = true")
				.setString(0, stableId)
				.setInteger(1, majorVersion)
				.setInteger(2, minorVersion)
				.list();
		return parameters;
	}
	
	@Transactional(readOnly = false)
	public void savePipeline(Pipeline pipeline) {
		getCurrentSession().saveOrUpdate(pipeline);
		
	}
	
	@Transactional(readOnly = false)
	public void saveProcedure(Procedure procedure) {
		getCurrentSession().saveOrUpdate(procedure);
	}
	
	@Transactional(readOnly = false)
	public void saveParameter(Parameter parameter) {
		getCurrentSession().saveOrUpdate(parameter);
	}
	
}
