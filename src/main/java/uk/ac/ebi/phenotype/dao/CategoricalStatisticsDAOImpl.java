package uk.ac.ebi.phenotype.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.ebi.phenotype.pojo.BiologicalModel;
import uk.ac.ebi.phenotype.pojo.CategoricalGroupKey;
import uk.ac.ebi.phenotype.pojo.Organisation;
import uk.ac.ebi.phenotype.pojo.Parameter;
import uk.ac.ebi.phenotype.pojo.ParameterOption;
import uk.ac.ebi.phenotype.pojo.SexType;
import uk.ac.ebi.phenotype.pojo.ZygosityType;

public class CategoricalStatisticsDAOImpl extends StatisticsDAOImpl implements CategoricalStatisticsDAO {

	private static final Logger log = Logger.getLogger(CategoricalStatisticsDAOImpl.class);

	public CategoricalStatisticsDAOImpl(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	/**
	 * Get the list of categories that are appropriate for this parameter
	 */
	@Transactional(readOnly = true)
	public List<String> getCategories(Parameter parameter) {
		List<ParameterOption> options = parameter.getOptions();
		List<String> categories = new ArrayList<String>();

		for (ParameterOption option : options) {
			categories.add(option.getName());
		}

		return categories;
	}


	@Transactional(readOnly = true)
	public Map<Integer, Integer> getOrganisationsByParameter(Parameter parameter) {

		Map<Integer, Integer> data = new HashMap<Integer, Integer>();

		try {
			Statement stmt = getConnection().createStatement();
			ResultSet resultSet = stmt
					.executeQuery("select distinct population_id, organisation_id from stats_mv_control_categorical_values where parameter_id = "
							+ parameter.getId());

			while (resultSet.next()) {
				data.put(resultSet.getInt("population_id"),
						resultSet.getInt("organisation_id"));
			}
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return data;
	}

	@Transactional(readOnly = true)
	public List<CategoricalGroupKey> getControlCategoricalDataByParameter(
			Parameter parameter) {

		List<CategoricalGroupKey> data = new ArrayList<CategoricalGroupKey>();

		try {
			Statement stmt = getConnection().createStatement();
			ResultSet resultSet = stmt
					.executeQuery("select * from stats_mv_control_categorical_values where parameter_id = "
							+ parameter.getId());

			while (resultSet.next()) {
				CategoricalGroupKey result = new CategoricalGroupKey();
				result.setCategory(resultSet.getString("category"));
				result.setParameter(parameter);
				result.setPopulationId(resultSet.getInt("population_id"));
				result.setSex(SexType.valueOf(resultSet.getString("sex")));
				result.setZygosity(null); // Disregard zygostiy for control
											// groups
				data.add(result);
			}
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return data;
	}

	@Transactional(readOnly = true)
	public List<CategoricalGroupKey> getMutantCategoricalDataByParameter(
			Parameter parameter) {

		List<CategoricalGroupKey> data = new ArrayList<CategoricalGroupKey>();

		try {
			Statement stmt = getConnection().createStatement();
			ResultSet resultSet = stmt
					.executeQuery("select * from stats_mv_experimental_categorical_values where parameter_id = "
							+ parameter.getId());

			while (resultSet.next()) {
				CategoricalGroupKey result = new CategoricalGroupKey();
				result.setCategory(resultSet.getString("category"));
				result.setParameter(parameter);
				result.setPopulationId(resultSet.getInt("population_id"));
				result.setSex(SexType.valueOf(resultSet.getString("sex")));
				result.setZygosity(ZygosityType.valueOf(resultSet
						.getString("zygosity")));
				data.add(result);
			}
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return data;
	}

	@Transactional(readOnly = true)
	public Long hasEnoughData(SexType sex, ZygosityType zygosity,
			Parameter parameter, Integer populationId) {
		return (Long) getCurrentSession()
				.createQuery(
						"SELECT COUNT(*) FROM CategoricalMutantView c WHERE c.parameter=? AND c.populationId=?")
				.setLong(0, parameter.getId()).setInteger(1, populationId)
				.list().get(0);
	}

	@Transactional(readOnly = true)
	public Long countControl(SexType sex, Parameter parameter, String category, Integer populationId) throws SQLException {
		log.debug(sex.name() + " " + parameter.getStableId() + " " + category + populationId);

		Long count = null;
		String sql = "SELECT count(*)  from stats_mv_control_categorical_values  WHERE sex=? AND category=? AND population_id=?";

		try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
			stmt.setString(1, sex.name());
			stmt.setString(2, category);
			stmt.setInt(3, populationId.intValue());
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				count = rs.getLong(1);
			} else {
				log.warn("error: could not get the record counts");
			}
		} 

		return count;
	}

	@Transactional(readOnly = true)
	public Long countMutant(SexType sex, ZygosityType zygosity, Parameter parameter, String category, Integer populationId) throws SQLException {
		log.debug(sex.name() + " " +zygosity+" "+ parameter.getStableId() + " " + category+ populationId);

		Long count = null;
		String sql = "SELECT count(*)  from stats_mv_experimental_categorical_values  WHERE sex=? AND zygosity=? AND parameter_id=? AND category=? AND population_id=?";
		
		try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
			stmt.setString(1, sex.name());
			stmt.setString(2, zygosity.name());
			stmt.setInt(3, parameter.getId());
			stmt.setString(4, category);
			stmt.setInt(5, populationId);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				count = rs.getLong(1);
			} else {
				log.warn("error: could not get the record counts");
			}
		} 

		return count;
	}

	public Integer getPopulationIdByColonySexParameter(String colonyId, SexType sex, Parameter parameter) {
		Integer populationId = (Integer) getCurrentSession()
				.createQuery(
						"SELECT distinct populationId FROM CategoricalMutantView c WHERE c.colony = ? AND c.sex = ? AND c.parameter = ?")
				.setString(0, colonyId).setString(1, sex.name())
				.setInteger(2, parameter.getId()).uniqueResult();
		return populationId;
	}

	@Transactional(readOnly = true)
	public BiologicalModel getControlBiologicalModelByPopulation(
			Integer populationId) {
		BiologicalModel bm = (BiologicalModel) getCurrentSession()
				.createQuery(
						"SELECT bs.biologicalModel FROM Observation o inner join o.sample as bs inner join bs.biologicalModel bm WHERE o.populationId=? AND bs.group='control'")
				.setInteger(0, populationId).list().get(0);
		return bm;
	}

	@Transactional(readOnly = true)
	public BiologicalModel getMutantBiologicalModelByPopulation(
			Integer populationId) {
		BiologicalModel bm = (BiologicalModel) getCurrentSession()
				.createQuery(
						"SELECT bs.biologicalModel FROM Observation o inner join o.sample as bs inner join bs.biologicalModel bm WHERE o.populationId=? AND bs.group='experimental'")
				.setInteger(0, populationId).list().get(0);
		return bm;
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<BiologicalModel> getMutantBiologicalModelsByParameterAndGene(
			Parameter parameter, String accessionId) {
		List<BiologicalModel> bms = (List<BiologicalModel>) getCurrentSession()
				.createQuery(
						"SELECT DISTINCT c.biologicalModel FROM CategoricalMutantView c inner join c.biologicalModel as bm join bm.genomicFeatures as gf WHERE gf.id.accession=? AND c.parameter=?")
				.setString(0, accessionId).setInteger(1, parameter.getId())
				.list();
		return bms;
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<BiologicalModel> getBiologicalModelsByParameter(
			Parameter parameter) {
		List<BiologicalModel> bms = (List<BiologicalModel>) getCurrentSession()
				.createQuery(
						"SELECT DISTINCT c.biologicalModel FROM CategoricalMutantView c inner join c.biologicalModel as bm join bm.genomicFeatures as gf WHERE  c.parameter=?")
				.setInteger(0, parameter.getId()).list();
		return bms;
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Integer> getPopulationIdsByParameterAndMutantBiologicalModel(
			Parameter parameter, BiologicalModel biologicalModel) {
		return (List<Integer>) getCurrentSession()
				.createQuery(
						"SELECT DISTINCT populationId FROM CategoricalMutantView WHERE parameter=? AND biologicalModel=?")
				.setLong(0, parameter.getId())
				.setInteger(1, biologicalModel.getId()).list();
	}

	public Double getpValueByParameterAndMutantBiologicalModelAndSexAndZygosity(
			Parameter parameter, BiologicalModel controlBiologicalModel, SexType sex,
			ZygosityType zygosity) {
		return (Double) getCurrentSession()
				.createQuery(
						"SELECT pValue FROM CategoricalResult WHERE parameter=? AND experimentalBiologicalModel=? AND experimentalSex=? AND experimentalZygosity=?")
				.setLong(0, parameter.getId())
				.setInteger(1, controlBiologicalModel.getId())
				.setString(2, sex.name()).setString(3, zygosity.name())
				.uniqueResult();
	}

	public Double getMaxEffectSizeByParameterAndMutantBiologicalModelAndSexAndZygosity(
			Parameter parameter, BiologicalModel mutantBiologicalModel, SexType sex,
			ZygosityType zygosity) {
		return (Double) getCurrentSession()
				.createQuery(
						"SELECT maxEffect FROM CategoricalResult WHERE parameter=? AND experimentalBiologicalModel=? AND experimentalSex=? AND experimentalZygosity=?")
				.setLong(0, parameter.getId())
				.setInteger(1, mutantBiologicalModel.getId())
				.setString(2, sex.name()).setString(3, zygosity.name())
				.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Integer> getPopulationIdsByParameter(Parameter parameter) {
		return getCurrentSession()
				.createQuery(
						"SELECT DISTINCT populationId FROM CategoricalMutantView WHERE parameter=?")
				.setLong(0, parameter.getId()).list();
	}

	@Transactional(readOnly = true)
	public Organisation getOrganisationByPopulation(Integer populationId) {
		Organisation organisation = (Organisation) getCurrentSession()
				.createQuery(
						"SELECT DISTINCT c.organisation FROM CategoricalControlView c WHERE c.populationId=?")
				.setInteger(0, populationId).uniqueResult();
		return organisation;
	}

	/**
	 * return the SexType ENUM associated to this population
	 */
	@Transactional(readOnly = true)
	public SexType getSexByPopulation(Integer populationId) {
		SexType sex = (SexType) getCurrentSession()
				.createQuery(
						"SELECT DISTINCT c.sex FROM CategoricalMutantView c WHERE c.populationId=?")
				.setInteger(0, populationId).uniqueResult();
		return sex;
	}

	/**
	 * return the ZygosityType ENUM associated to this population
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<ZygosityType> getZygositiesByPopulation(Integer populationId) {
		List<ZygosityType> zygosity = (List<ZygosityType>) getCurrentSession()
				.createQuery(
						"SELECT DISTINCT c.zygosity FROM CategoricalMutantView c WHERE c.populationId=?")
				.setInteger(0, populationId).list();
		return zygosity;
	}

	@Transactional(readOnly = false)
	public void deleteCategoricalResultByParameter(Parameter parameter)
			throws HibernateException, SQLException {
		Statement stmt = getConnection().createStatement();
		stmt.executeUpdate("DELETE FROM stats_categorical_results WHERE parameter_id="
				+ parameter.getId());
		stmt.close();
	}

	@Override
	public Double getpValueByParameterAndControlBiologicalModelAndSexAndZygosity(
			Parameter parameter, BiologicalModel biologicalModel, SexType sex,
			ZygosityType zygosity) {
	return (Double) getCurrentSession()
			.createQuery(
					"SELECT pValue FROM CategoricalResult WHERE parameter=? AND controlBiologicalModel=? AND controlSex=? AND controlZygosity=?")
			.setLong(0, parameter.getId())
			.setInteger(1, biologicalModel.getId())
			.setString(2, sex.name()).setString(3, zygosity.name())
			.uniqueResult();
	}

	@Override
	public List<Map<String, String>> getListOfUniqueParametersAndGenes(
			int start, int length) throws SQLException {
		String query;

		List<Map<String,String>> resultsList=new ArrayList<Map<String,String>>();
		query = "SELECT DISTINCT vw.biological_model_id, vw.parameter_id, bgf.gf_acc FROM stats_mv_experimental_categorical_values vw, biological_model_genomic_feature bgf where bgf.biological_model_id=vw.biological_model_id limit "+start+" , " +length;

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



}
