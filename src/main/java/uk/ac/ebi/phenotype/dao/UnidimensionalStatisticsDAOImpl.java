package uk.ac.ebi.phenotype.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.ebi.phenotype.pojo.BiologicalModel;
import uk.ac.ebi.phenotype.pojo.Organisation;
import uk.ac.ebi.phenotype.pojo.Parameter;
import uk.ac.ebi.phenotype.pojo.SexType;
import uk.ac.ebi.phenotype.pojo.UnidimensionalControlView;
import uk.ac.ebi.phenotype.pojo.UnidimensionalMutantView;
import uk.ac.ebi.phenotype.pojo.UnidimensionalResult;
import uk.ac.ebi.phenotype.pojo.UnidimensionalView;
import uk.ac.ebi.phenotype.pojo.ZygosityType;

@Component
public class UnidimensionalStatisticsDAOImpl extends HibernateDAOImpl implements UnidimensionalStatisticsDAO {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private BiologicalModelDAO bmDAO;
	
	public UnidimensionalStatisticsDAOImpl(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	// TODO: rename method to reflect what it's doing
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Float> countControl(SexType sex, Parameter parameter, Integer populationId){
		return (List<Float>) getCurrentSession()
			.createQuery("SELECT c.dataPoint FROM UnidimensionalControlView c WHERE c.populationId=?")
			.setInteger(0, populationId)
			.list();
	}

	// TODO: rename method to reflect what it's doing
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Float> countMutant(SexType sex, ZygosityType zygosity, Parameter parameter,  Integer populationId){
		return (List<Float>) getCurrentSession()
			.createQuery("SELECT c.dataPoint FROM UnidimensionalMutantView c WHERE c.sex=? AND c.zygosity=? AND c.parameter=?  AND c.populationId=?")
			.setString(0, sex.name())
			.setString(1, zygosity.name())
			.setLong(2, parameter.getId())
			.setInteger(3, populationId)
			.list();
	}

	@Transactional(readOnly = true)
	public BiologicalModel getControlBiologicalModelByPopulation(Integer populationId) {
		BiologicalModel bm = (BiologicalModel) getCurrentSession().createQuery("SELECT bs.biologicalModel FROM Observation o inner join o.sample as bs inner join bs.biologicalModel bm WHERE o.populationId=? AND bs.group='control'")
			.setInteger(0, populationId)
			.list()
			.get(0);
		return bm;
	}

	@Transactional(readOnly = true)
	public BiologicalModel getMutantBiologicalModelByPopulation(Integer populationId) {
		BiologicalModel bm = (BiologicalModel) getCurrentSession().createQuery("SELECT bs.biologicalModel FROM Observation o inner join o.sample as bs inner join bs.biologicalModel bm WHERE o.populationId=? AND bs.group='experimental'")
			.setInteger(0, populationId)
			.list()
			.get(0);
		return bm;
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<BiologicalModel> getBiologicalModelsByParameterAndGene(Parameter parameter, String accessionId) {
		List<BiologicalModel> bms = (List<BiologicalModel>) getCurrentSession().createQuery("SELECT DISTINCT c.biologicalModel FROM UnidimensionalMutantView c inner join c.biologicalModel as bm join bm.genomicFeatures as gf WHERE gf.id.accession=? AND c.parameter=?")
			.setString(0, accessionId)
			.setInteger(1, parameter.getId())
			.list();
		return bms;
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<BiologicalModel> getMutantBiologicalModelsByParameter(Parameter parameter) {
		return (List<BiologicalModel>) getCurrentSession().createQuery("SELECT DISTINCT c.biologicalModel FROM UnidimensionalMutantView c inner join c.biologicalModel as bm join bm.genomicFeatures as gf WHERE  c.parameter=?")
			.setInteger(0, parameter.getId())
			.list();
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Integer> getPopulationIdsByParameterAndMutantBiologicalModel(Parameter parameter, BiologicalModel biologicalModel) {
		return (List<Integer>) getCurrentSession().createQuery("SELECT DISTINCT populationId FROM UnidimensionalMutantView WHERE parameter=? AND biologicalModel=?")
				.setLong(0, parameter.getId())
				.setInteger(1, biologicalModel.getId())
				.list();
	}

	public Double getpValueByParameterAndBiologicalModel(Parameter parameter, BiologicalModel biologicalModel) {
		return (Double) getCurrentSession().createQuery("SELECT DISTINCT populationId FROM UnidimensionalMutantView WHERE parameter=? AND biologicalModel=?")
				.setLong(0, parameter.getId())
				.setInteger(1, biologicalModel.getId())
				.uniqueResult();
	}


	/**
	 * return the SexType ENUM associated to this population
	 */
	@Transactional(readOnly = true)
	public SexType getSexByPopulation(Integer populationId) {
		SexType sex = (SexType) getCurrentSession().createQuery("SELECT DISTINCT c.sex FROM UnidimensionalMutantView c WHERE c.populationId=?")
				.setInteger(0, populationId)
				.uniqueResult();
		return sex;
	}

	/**
	 * return the ZygosityType ENUM associated to this population
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<ZygosityType> getZygositiesByPopulation(Integer populationId) {
		List<ZygosityType> zygosity = (List<ZygosityType>) getCurrentSession().createQuery("SELECT DISTINCT c.zygosity FROM UnidimensionalMutantView c WHERE c.populationId=?")
			.setInteger(0, populationId)
			.list();
		return zygosity;
	}


	@Transactional(readOnly = false)
	public void deleteUnidimensionalResultByParameter(Parameter parameter) throws HibernateException, SQLException {
		Statement stmt = getConnection().createStatement();
		stmt.executeUpdate("DELETE FROM stats_unidimensional_results WHERE parameter_id="+parameter.getId());
		stmt.close();
	}

	@Transactional(readOnly = false)
	public void saveUnidimensionalResult(UnidimensionalResult result) {
		getCurrentSession().saveOrUpdate(result);
		getCurrentSession().flush();
	}

	/**
	 * Get all records for a specific colony and organization for a parameter.
	 * 
	 * This will return all mutant and control records.
	 * 
	 */
	@Override
	public Set<UnidimensionalView> getUnidimensionalData(Parameter parameter, Organisation organization, String colonyId) {
		
		Set<UnidimensionalView> results = new HashSet<UnidimensionalView>();
		Set<Integer> populationIds = new HashSet<Integer>();

		Statement stmt;
		ResultSet rs;

		try {

//TODO: Use the prepared statement style
//			String query = "SELECT * FROM stats_mv_experimental_unidimensional_values WHERE parameter_id=? AND colony_id=?";
//			List<?> r = (getSessionFactory().getCurrentSession().createSQLQuery(query))
//				.setInteger(0, parameter.getId())
//				.setString(1, colonyId)
//				.list();

			String query;
			
			// Get all the population IDs for this colony
			stmt = getConnection().createStatement();
//			query = "SELECT DISTINCT population_id FROM stats_mv_experimental_unidimensional_values WHERE organisation_id = "+organization.getId()+" AND parameter_id=" + parameter.getId() + " AND colony_id='"+colonyId+"'";
			query = "SELECT DISTINCT o.population_id FROM biological_sample bs JOIN observation o ON o.biological_sample_id = bs.id JOIN live_sample ls ON ls.id = bs.id  WHERE o.observation_type = 'unidimensional' AND bs.sample_group = 'experimental' AND bs.organisation_id="+organization.getId()+" AND o.parameter_id="+parameter.getId()+" AND ls.colony_id='"+colonyId+"'";
			System.out.println(query);
			rs = stmt.executeQuery(query);
			
			while(rs.next()){
				// Collect all population ids for getting the control group
				// of records later
				populationIds.add(rs.getInt("population_id"));
			}

			stmt = getConnection().createStatement();
			query = "SELECT DISTINCT biological_model_id, biological_sample_id, zygosity, sex, data_point FROM stats_mv_experimental_unidimensional_values WHERE organisation_id = "+organization.getId()+" AND parameter_id=" + parameter.getId() + " AND colony_id='"+colonyId+"'";
			System.out.println(query);
			rs = stmt.executeQuery(query);
			
			while(rs.next()){


				UnidimensionalMutantView uv = new UnidimensionalMutantView();
				uv.setColony(colonyId);
				uv.setOrganisation(organization);
				uv.setParameter(parameter);
				uv.setBiologicalModel(bmDAO.getBiologicalModelById(rs.getInt("biological_model_id")));
				uv.setBiologicalSample(bmDAO.getBiologicalSampleById(rs.getInt("biological_sample_id")));
				uv.setDataPoint(rs.getFloat("data_point"));
				uv.setSex(SexType.valueOf(rs.getString("sex")));
				uv.setZygosity(ZygosityType.valueOf(rs.getString("zygosity")));
	
				results.add(uv);
			}
			stmt.close();

			stmt = getConnection().createStatement();
			query = "SELECT biological_model_id, biological_sample_id, sex, data_point FROM stats_mv_control_unidimensional_values WHERE population_id IN ("+StringUtils.join(populationIds, ", ")+")";
			System.out.println(query);
			rs = stmt.executeQuery(query);

			while(rs.next()){
				
				// For controls we don't need population ID or zygosity
				
				UnidimensionalControlView uv = new UnidimensionalControlView();
				uv.setBiologicalModel(bmDAO.getBiologicalModelById(rs.getInt("biological_model_id")));
				uv.setBiologicalSample(bmDAO.getBiologicalSampleById(rs.getInt("biological_sample_id")));
				uv.setDataPoint(rs.getFloat("data_point"));
				uv.setSex(SexType.valueOf(rs.getString("sex")));
				
				//Prepare for mixed model (all controls are expected to be labled with genotype "+/+" in the R code)
				uv.setColony("+/+");
				
				uv.setOrganisation(organization);
				uv.setParameter(parameter);
	
				results.add(uv);
			}
			stmt.close();
		} catch (SQLException e) {
			log.error(e.getLocalizedMessage());
		}

		return results;
	}


}
