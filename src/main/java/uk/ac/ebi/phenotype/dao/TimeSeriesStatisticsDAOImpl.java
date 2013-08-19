package uk.ac.ebi.phenotype.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.ebi.phenotype.pojo.BiologicalModel;
import uk.ac.ebi.phenotype.pojo.Organisation;
import uk.ac.ebi.phenotype.pojo.Parameter;
import uk.ac.ebi.phenotype.pojo.SexType;
import uk.ac.ebi.phenotype.pojo.ZygosityType;

public class TimeSeriesStatisticsDAOImpl extends StatisticsDAOImpl implements TimeSeriesStatisticsDAO {

	/**
	 * Creates a new Hibernate sequence region data access manager.
	 * @param sessionFactory the Hibernate session factory
	 */
	public TimeSeriesStatisticsDAOImpl(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Transactional(readOnly = true)
	public Long hasEnoughData(SexType sex, ZygosityType zygosity, Parameter parameter, Integer populationId){
		return (Long) getCurrentSession()
			.createQuery("SELECT COUNT(*) FROM TimeSeriesMutantView c WHERE c.parameter=? AND c.populationId=?")
			.setLong(0, parameter.getId())
			.setInteger(1, populationId)
			.list()
			.get(0);
	}

	
	
	@Override
	public List<DiscreteTimePoint> getControlStats(SexType sex, Parameter parameter, Integer populationId) {
		logger.debug("calling control query for stats");

		List<DiscreteTimePoint> timeData=new ArrayList<DiscreteTimePoint>();
		String sql="select discrete_point, AVG(data_point) as mean, STDDEV(data_point) as std_deviation from stats_mv_control_time_series_values where sex=? and  population_id=? group  by discrete_point";
			
		try (PreparedStatement stmt = getConnection().prepareStatement(sql)){
			stmt.setString(1, sex.name());
			stmt.setInt(2, populationId);

			ResultSet resultSet = stmt.executeQuery();
			logger.debug("got control Result");
			while(resultSet.next()){
				Float time=resultSet.getFloat("discrete_point");
				Float mean=resultSet.getFloat("mean");
				Float stdDev=resultSet.getFloat("std_deviation");
				DiscreteTimePoint pt=new DiscreteTimePoint(time,mean, stdDev);
				timeData.add(pt);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return timeData;
	}

	@Override
	public List<DiscreteTimePoint> getMutantStats(SexType sex, ZygosityType zygosity, Parameter parameter, Integer populationId) {
		logger.debug("calling mutant query");
		
		List<DiscreteTimePoint> timeData=new ArrayList<DiscreteTimePoint>();
		String sql="select  discrete_point, AVG(data_point) as mean, STDDEV(data_point) as std_deviation from stats_mv_experimental_time_series_values  WHERE sex=? AND zygosity=? AND parameter_id=? AND population_id=? group  by discrete_point";

		try (PreparedStatement stmt = this.getConnection().prepareStatement(sql)){
			stmt.setString(1, sex.name());
			stmt.setString(2,  zygosity.name());
			stmt.setInt(3,  parameter.getId());
			stmt.setInt(4,  populationId);

			ResultSet resultSet = stmt.executeQuery();
			logger.debug("got mutant Result");
			while(resultSet.next()){
				Float time=resultSet.getFloat("discrete_point");
				Float mean=resultSet.getFloat("mean");
				Float stdDev=resultSet.getFloat("std_deviation");
				DiscreteTimePoint pt=new DiscreteTimePoint(time,mean, stdDev);
				timeData.add(pt);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return timeData;
	}

	@Transactional(readOnly = true)
	public BiologicalModel getControlBiologicalModelByPopulation(Integer populationId) {
		return (BiologicalModel) getCurrentSession().createQuery("SELECT bs.biologicalModel FROM Observation o inner join o.sample as bs inner join bs.biologicalModel bm WHERE o.populationId=? AND bs.group='control'")
			.setInteger(0, populationId)
			.list()
			.get(0);
	}

	@Transactional(readOnly = true)
	public BiologicalModel getMutantBiologicalModelByPopulation(Integer populationId) {
		return (BiologicalModel) getCurrentSession().createQuery("SELECT bs.biologicalModel FROM Observation o inner join o.sample as bs inner join bs.biologicalModel bm WHERE o.populationId=? AND bs.group='experimental'")
			.setInteger(0, populationId)
			.list()
			.get(0);
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<BiologicalModel> getBiologicalModelsByParameterAndGene(Parameter parameter, String accessionId) {
		return (List<BiologicalModel>) getCurrentSession().createQuery("SELECT DISTINCT c.biologicalModel FROM TimeSeriesMutantView c inner join c.biologicalModel as bm join bm.genomicFeatures as gf WHERE gf.id.accession=? AND c.parameter=?")
				.setString(0, accessionId)
				.setInteger(1, parameter.getId())
				.list();
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<BiologicalModel> getBiologicalModelsByParameter(Parameter parameter) {
		return (List<BiologicalModel>) getCurrentSession().createQuery("SELECT DISTINCT c.biologicalModel FROM TimeSeriesMutantView c inner join c.biologicalModel as bm join bm.genomicFeatures as gf WHERE  c.parameter=?")
			.setInteger(0, parameter.getId())
			.list();
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Integer> getPopulationIdsByParameterAndMutantBiologicalModel(Parameter parameter, BiologicalModel biologicalModel) {
		return (List<Integer>) getCurrentSession().createQuery("SELECT DISTINCT populationId FROM TimeSeriesMutantView WHERE parameter=? AND biologicalModel=?")
			.setLong(0, parameter.getId())
			.setInteger(1, biologicalModel.getId())
			.list();
	}

	public Double getpValueByParameterAndBiologicalModelAndSexAndZygosity(Parameter parameter, BiologicalModel biologicalModel, SexType sex, ZygosityType zygosity) {
		return (Double) getCurrentSession().createQuery("SELECT DISTINCT populationId FROM TimeSeriesMutantView WHERE parameter=? AND biologicalModel=?")
			.setLong(0, parameter.getId())
			.setInteger(1, biologicalModel.getId())
			.uniqueResult();
	}

	public Double getMaxEffectSizeByParameterAndBiologicalModelAndSexAndZygosity(Parameter parameter, BiologicalModel biologicalModel, SexType sex, ZygosityType zygosity) {
		return (Double) getCurrentSession().createQuery("SELECT DISTINCT populationId FROM TimeSeriesMutantView WHERE parameter=? AND biologicalModel=?")
			.setLong(0, parameter.getId())
			.setInteger(1, biologicalModel.getId())
			.uniqueResult();
	}

	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Integer> getPopulationIdsByParameter(Parameter parameter) {
		return getCurrentSession().createQuery("SELECT DISTINCT populationId FROM TimeSeriesMutantView WHERE parameter=?")
			.setLong(0, parameter.getId())
			.list();	
	}


	@Transactional(readOnly = true)
	public Organisation getOrganisationByPopulation(Integer populationId) {
		Organisation organisation = (Organisation) getCurrentSession().createQuery("SELECT DISTINCT c.organisation FROM TimeSeriesMutantView c WHERE c.populationId=?")
			.setInteger(0, populationId)
			.uniqueResult();
		return organisation;
	}


	/**
	 * return the SexType ENUM associated to this population
	 */
	@Transactional(readOnly = true)
	public SexType getSexByPopulation(Integer populationId) {
		SexType sex = (SexType) getCurrentSession().createQuery("SELECT DISTINCT c.sex FROM TimeSeriesMutantView c WHERE c.populationId=?")
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
		List<ZygosityType> zygosity = (List<ZygosityType>) getCurrentSession().createQuery("SELECT DISTINCT c.zygosity FROM TimeSeriesMutantView c WHERE c.populationId=?")
			.setInteger(0, populationId)
			.list();
		return zygosity;
	}
	
	@Override
	public List<Map<String, String>> getListOfUniqueParametersAndGenes(int start, int length) throws SQLException {
		String query;

		List<Map<String,String>> resultsList=new ArrayList<Map<String,String>>();
		query = "SELECT DISTINCT vw.biological_model_id, vw.parameter_id, bgf.gf_acc FROM stats_mv_experimental_time_series_values vw, biological_model_genomic_feature bgf where bgf.biological_model_id=vw.biological_model_id limit "+start+" , " +length;

		try (PreparedStatement statement = getConnection().prepareStatement(query)) {
	      
		    ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				Map<String,String> row=new HashMap<String,String>();
				row.put("accession", resultSet.getString("gf_acc"));
				row.put("parameter_id", Integer.toString(resultSet.getInt("parameter_id")));
				resultsList.add(row);
			}
		}
		return resultsList;
	}
	
	//select min(uo.data_point), max(uo.data_point) from time_series_observation uo join observation o on uo.id = o.id where o.parameter_stable_id = 'ESLIM_009_001_001';
	public Map<String, Float> getMinAndMaxForParameter(String paramStableId) throws SQLException{
		String query;

		Map<String,Float> resultsMap=new HashMap<String,Float>();
		query = "select min(uo.data_point), max(uo.data_point) from time_series_observation uo join observation o on uo.id = o.id where o.parameter_stable_id = '"+paramStableId+"'";

		try (PreparedStatement statement = getConnection().prepareStatement(query)) {
	      
		    ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				resultsMap.put("min", resultSet.getFloat(1));
				resultsMap.put("max", resultSet.getFloat(2));
				
			}
		}
		
		return resultsMap;
	}
}
