package uk.ac.ebi.phenotype.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.coode.parsers.ManchesterOWLSyntaxAutoCompleteCombined_ManchesterOWLSyntaxAutoCompleteBase.incompleteAssertionAxiom_return;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.phenotype.pojo.BiologicalModel;
import uk.ac.ebi.phenotype.pojo.CategoricalResult;
import uk.ac.ebi.phenotype.pojo.Organisation;
import uk.ac.ebi.phenotype.pojo.Parameter;
import uk.ac.ebi.phenotype.pojo.SexType;
import uk.ac.ebi.phenotype.pojo.UnidimensionalRecordDTO;
import uk.ac.ebi.phenotype.pojo.UnidimensionalResult;
import uk.ac.ebi.phenotype.pojo.ZygosityType;
import uk.ac.ebi.phenotype.stats.MouseDataPoint;

public class UnidimensionalStatisticsDAOImpl extends StatisticsDAOImpl implements UnidimensionalStatisticsDAO {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	/**
	 * Creates a new Hibernate sequence region data access manager.
	 * @param sessionFactory the Hibernate session factory
	 */
	public UnidimensionalStatisticsDAOImpl(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Float> getControlDataPointsForPopulation(Integer populationId){
		return (List<Float>) getCurrentSession()
			.createQuery("SELECT c.dataPoint FROM UnidimensionalControlView c WHERE c.populationId=?")
			.setInteger(0, populationId)
			.list();
	}


	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Float> getMutantDataPoints(SexType sex, ZygosityType zygosity, Parameter parameter,  Integer populationId){
		return (List<Float>) getCurrentSession()
			.createQuery("SELECT c.dataPoint FROM UnidimensionalMutantView c WHERE c.sex=? AND c.zygosity=? AND c.parameter=?  AND c.populationId=?")
			.setString(0, sex.name())
			.setString(1, zygosity.name())
			.setLong(2, parameter.getId())
			.setInteger(3, populationId)
			.list();
		
		
	}
	
	@Transactional(readOnly = true)
	public List<MouseDataPoint> getMutantDataPointsWithMouseName(SexType sex, ZygosityType zygosity, Parameter parameter,  Integer populationId){
		
	    PreparedStatement statement = null;
	    ResultSet resultSet = null;
		List<MouseDataPoint> mouseDataPoints = new ArrayList<MouseDataPoint>();

		String query = "SELECT * FROM komp2.stats_mv_experimental_unidimensional_values vw, biological_sample bs where vw.sex=? AND vw.zygosity=? AND vw.parameter_id=?  AND vw.population_id=? and vw.biological_sample_id=bs.id";

		try (Connection connection = getConnection()) {
	        statement = connection.prepareStatement(query);
	        statement.setString(1, sex.name());
	        statement.setString(2, zygosity.getName());
	        statement.setInt(3, parameter.getId());
	        statement.setInt(4, populationId);
	        resultSet = statement.executeQuery();
			while (resultSet.next()) {
				mouseDataPoints.add(new MouseDataPoint(resultSet.getString("external_id"), resultSet.getFloat("data_point")));
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
		//SELECT * FROM komp2.stats_mv_experimental_unidimensional_values vw, biological_sample bs where vw.sex='male' AND vw.zygosity='heterozygote' AND vw.parameter_id=1268  AND vw.population_id=301919 and vw.biological_sample_id=bs.id;
	return mouseDataPoints;
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<MouseDataPoint> getControlDataPointsWithMouseName(Integer populationId){
		
	    PreparedStatement statement = null;
	    ResultSet resultSet = null;
		List<MouseDataPoint> mouseDataPoints = new ArrayList<MouseDataPoint>();

		String query = "SELECT * FROM komp2.stats_mv_control_unidimensional_values vw, biological_sample bs where  vw.population_id=? and vw.biological_sample_id=bs.id";

		try (Connection connection = getConnection()) {
			 statement = connection.prepareStatement(query);
	        statement.setInt(1, populationId);
	        resultSet = statement.executeQuery();
			while (resultSet.next()) {
				mouseDataPoints.add(new MouseDataPoint(resultSet.getString("external_id"), resultSet.getFloat("data_point")));
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
		//SELECT * FROM komp2.stats_mv_experimental_unidimensional_values vw, biological_sample bs where vw.sex='male' AND vw.zygosity='heterozygote' AND vw.parameter_id=1268  AND vw.population_id=301919 and vw.biological_sample_id=bs.id;
	return mouseDataPoints;
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
		return (List<BiologicalModel>) getCurrentSession().createQuery("SELECT DISTINCT c.biologicalModel FROM UnidimensionalMutantView c inner join c.biologicalModel as bm join bm.genomicFeatures as gf WHERE gf.id.accession=? AND c.parameter=?")
			.setString(0, accessionId)
			.setInteger(1, parameter.getId())
			.list();
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

	public List<UnidimensionalResult> getUnidimensionalResultByParameterAndBiologicalModel(Parameter parameter, BiologicalModel controlBiologicalModel, BiologicalModel mutantBiologicalModel) {
		return (List<UnidimensionalResult>) getCurrentSession().createQuery("from UnidimensionalResult u  WHERE parameter=? and control_id=? and experimental_id=?")
			.setInteger(0, parameter.getId())
			.setInteger(1, controlBiologicalModel.getId())
			.setInteger(2, mutantBiologicalModel.getId())
			.list();
		
		//select p from AnalysisPolicy p where exists elements(p.nodeIds)
	}


	/**
	 * return the SexType ENUM associated to this population
	 */
	@Transactional(readOnly = true)
	public SexType getSexByPopulation(Integer populationId) {
		return (SexType) getCurrentSession().createQuery("SELECT DISTINCT c.sex FROM UnidimensionalMutantView c WHERE c.populationId=?")
			.setInteger(0, populationId)
			.uniqueResult();
	}

	/**
	 * return the ZygosityType ENUM associated to this population
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<ZygosityType> getZygositiesByPopulation(Integer populationId) {
		return (List<ZygosityType>) getCurrentSession().createQuery("SELECT DISTINCT c.zygosity FROM UnidimensionalMutantView c WHERE c.populationId=?")
			.setInteger(0, populationId)
			.list();
	}



	/**
	 * Return the organisation associated to this colony
	 * @param colony The colony id
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Organisation> getOrganisationsByColonyAndParameter(String colony, Parameter parameter) {
		return (List<Organisation>) getCurrentSession().createQuery("SELECT DISTINCT c.organisation FROM UnidimensionalMutantView c WHERE c.colony=? and c.parameter=?")
			.setString(0, colony)
			.setInteger(1, parameter.getId())
			.list();
	}
		

	/** 
	 * Delete all unidimensional result records for the supplied
	 * parameter.
	 * 
	 * @throws SQLException if there's an error accessing the database
	 */
	@Transactional(readOnly = false)
	public void deleteUnidimensionalResultByParameter(Parameter parameter) throws SQLException {

		String query = "DELETE FROM stats_unidimensional_results"
				+ " WHERE parameter_id=" 
				+ parameter.getId();

		try(Statement statement = getConnection().createStatement()) {
			statement.executeUpdate(query);
		}
	}

	/** 
	 * Return all zygosities for this colony id organisation and parameter.
	 * 
	 * @throws SQLException if there's an error accessing the database
	 */
	public Set<ZygosityType> getZygosities(Parameter parameter, Organisation organisation, String colonyId) throws SQLException {
		Connection connection = null;
	    PreparedStatement statement = null;
	    ResultSet resultSet = null;
		Set<ZygosityType> zygosities = new HashSet<ZygosityType>();

		String query = "SELECT DISTINCT zygosity"
				+ " FROM biological_sample bs" 
				+ " JOIN observation o ON o.biological_sample_id = bs.id"
				+ " JOIN live_sample ls ON ls.id = bs.id"
				+ " WHERE o.observation_type = 'unidimensional'"
				+ " AND bs.sample_group = 'experimental'"
				+ " AND bs.organisation_id=?"
				+ " AND o.parameter_id=?" 
				+ " AND ls.colony_id=?";

		try {
	        connection = getConnection();
	        statement = connection.prepareStatement(query);
	        statement.setInt(1, organisation.getId());
	        statement.setInt(2, parameter.getId());
	        statement.setString(3, colonyId);
	        resultSet = statement.executeQuery();
			while (resultSet.next()) {
				zygosities.add(ZygosityType.valueOf(resultSet.getString("zygosity")));
			}
		} finally {
			if (resultSet != null) try { resultSet.close(); } catch (SQLException e) {log.error(e.getLocalizedMessage());}
	        if (statement != null) try { statement.close(); } catch (SQLException e) {log.error(e.getLocalizedMessage());}
	        if (connection != null) try { connection.close(); } catch (SQLException e) {log.error(e.getLocalizedMessage());}
		}
		
		return zygosities;
	}

	public Set<Integer> getPopulationIds(Parameter parameter, Organisation organisation, String colonyId, ZygosityType zygosity) throws SQLException {
	    Set<Integer> populationIds = new HashSet<Integer>();

		String query = "SELECT DISTINCT o.population_id"
			+ " FROM biological_sample bs"
			+ " JOIN observation o ON o.biological_sample_id = bs.id"
			+ " JOIN live_sample ls ON ls.id = bs.id"
			+ " WHERE o.observation_type = 'unidimensional'"
			+ " AND bs.sample_group = 'experimental'"
			+ " AND bs.organisation_id=?"
			+ " AND o.parameter_id=?"
			+ " AND ls.colony_id=?"
			+ " AND ls.zygosity=?";

		try (PreparedStatement statement = getConnection().prepareStatement(query)){
	        statement.setInt(1, organisation.getId());
	        statement.setInt(2, parameter.getId());
	        statement.setString(3, colonyId);
	        statement.setString(4, zygosity.name());
		    ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				populationIds.add(resultSet.getInt("population_id"));
			}
		}

		return populationIds; 
	}


	public Set<String> getColoniesByParameter(Parameter parameter) throws SQLException {
	    Set<String> colonies = new HashSet<String>();

		String query = "SELECT DISTINCT colony_id"
		+ " FROM stats_mv_experimental_unidimensional_values"
		+ " WHERE parameter_id=?";

		try (PreparedStatement statement = getConnection().prepareStatement(query)){
	        statement.setInt(1, parameter.getId());
		    ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				colonies.add(resultSet.getString("colony_id"));
			}
		}

		return colonies; 
	}

	
	/**
	 * Get all records for a specific colony and organization for a parameter.
	 * 
	 * This will return all mutant and control records.
	 * @throws SQLException 
	 * 
	 */
	@Override
	public Set<UnidimensionalRecordDTO> getUnidimensionalData(Parameter parameter, Organisation organisation, String colonyId, ZygosityType zygosity) throws SQLException {
		Set<UnidimensionalRecordDTO> resultsDTO = new HashSet<UnidimensionalRecordDTO>();

		String query;

		query = "SELECT DISTINCT biological_model_id, biological_sample_id, zygosity, sex, data_point "
				+ " FROM stats_mv_experimental_unidimensional_values"
				+ " WHERE organisation_id=?"
				+ " AND parameter_id=?"
				+ " AND colony_id=?"
				+ " AND zygosity=?";

		try (PreparedStatement statement = getConnection().prepareStatement(query)) {
	        statement.setInt(1, organisation.getId());
	        statement.setInt(2, parameter.getId());
	        statement.setString(3, colonyId);
	        statement.setString(4, zygosity.name());
		    ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				UnidimensionalRecordDTO urdto = new UnidimensionalRecordDTO();
				urdto.setIsMutant(true);
				urdto.setColony(colonyId);
				urdto.setGender(resultSet.getString("sex"));
				urdto.setGenotype(colonyId);
				urdto.setMutantModelId(resultSet.getInt("biological_model_id"));
				urdto.setMutantZygosity(ZygosityType.valueOf(resultSet.getString("zygosity")));
				urdto.setOrganisation(organisation);
				urdto.setParameter(parameter);
				urdto.setValue(resultSet.getString("data_point"));
				resultsDTO.add(urdto);
			}
		}

		Set<Integer> populationIds = getPopulationIds(parameter, organisation, colonyId, zygosity);
		query = "SELECT DISTINCT biological_model_id, biological_sample_id, sex, data_point"
				+ " FROM stats_mv_control_unidimensional_values"
				+ " WHERE population_id IN ("+StringUtils.join(populationIds, ", ")+")";
		try (PreparedStatement statement = getConnection().prepareStatement(query)){
		    ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				UnidimensionalRecordDTO urdto = new UnidimensionalRecordDTO();
				urdto.setIsMutant(false);
				urdto.setColony("+/+");
				urdto.setGender(resultSet.getString("sex"));
				urdto.setGenotype(colonyId);
				urdto.setControlModelId(resultSet.getInt("biological_model_id"));
				urdto.setOrganisation(organisation);
				urdto.setParameter(parameter);
				urdto.setValue(resultSet.getString("data_point"));
				resultsDTO.add(urdto);
			}
		}

		return resultsDTO;
	}
	
	
	public List<Map<String,String>> getListOfUniqueParametersAndGenes(int start, int length) throws SQLException{
		String query;

		List<Map<String,String>> resultsList=new ArrayList<Map<String,String>>();
		query = "SELECT DISTINCT vw.biological_model_id, vw.parameter_id, bgf.gf_acc FROM stats_mv_experimental_unidimensional_values vw, biological_model_genomic_feature bgf where bgf.biological_model_id=vw.biological_model_id limit "+start+" , " +length;

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
	
	public List<Map<String,String>> getListOfUniqueParametersAndGenes(int start, int length, String parameterId) throws SQLException{
		String query;

		List<Map<String,String>> resultsList=new ArrayList<Map<String,String>>();
		query = "SELECT DISTINCT vw.biological_model_id, vw.parameter_id, bgf.gf_acc FROM stats_mv_experimental_unidimensional_values vw, biological_model_genomic_feature bgf where bgf.biological_model_id=vw.biological_model_id and vw.parameter_id='"+parameterId+"' limit "+start+" , " +length;
System.out.println(query);
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

	// select min(uo.data_point), max(uo.data_point) from
	// unidimensional_observation uo join observation o on uo.id = o.id where
	// o.parameter_stable_id = 'M-G-P_025_001_022';
	public Map<String, Float> getMinAndMaxForParameter(String paramStableId) throws SQLException {
		Map<String, Float> resultsMap = new HashMap<String, Float>();

		String query = "SELECT MIN(uo.data_point), MAX(uo.data_point) FROM unidimensional_observation uo JOIN observation o ON uo.id = o.id WHERE o.parameter_stable_id = '"
				+ paramStableId + "'";

		try (PreparedStatement statement = getConnection().prepareStatement(query)) {

			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				resultsMap.put("min", resultSet.getFloat(1));
				resultsMap.put("max", resultSet.getFloat(2));

			}
		}

		return resultsMap;
	}

	@Override
	public List<UnidimensionalResult> getUnidimensionalResultByParameterIdAndBiologicalModelIds(
			Integer parameterId, Integer controlBiologicalId,
			Integer biologicalId) {
		return (List<UnidimensionalResult>) getCurrentSession().createQuery("from UnidimensionalResult u  WHERE parameter=? and control_id=? and experimental_id=?")
				.setInteger(0, parameterId)
				.setInteger(1, controlBiologicalId)
				.setInteger(2, biologicalId)
				.list();
	}
	
	private int getUnidimensionalResultIdFromStatsResultPhenotypeCallSummary(int id) throws SQLException {
		
		int result=-1;
		String query = "SELECT unidimensional_result_id FROM stat_result_phenotype_call_summary where phenotype_call_summary_id= '"
				+ id + "'";

		try (PreparedStatement statement = getConnection().prepareStatement(query)) {

			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				result=resultSet.getInt(1);
			}
		}
		return result;
	}
	
	@Override
	public UnidimensionalResult getUnidimensionalStatsForPhenotypeCallSummaryId(int phenotypeCallSummaryId) throws SQLException{
		//get the id we need from the join table
		int resultId=this.getUnidimensionalResultIdFromStatsResultPhenotypeCallSummary(phenotypeCallSummaryId);
		//use the join table id to get the actual result
		return (UnidimensionalResult) getCurrentSession().get(UnidimensionalResult.class,  resultId);
	}
        @Override
        public CategoricalResult getCategoricalStatsForPhenotypeCallSummaryId(int phenotypeCallSummaryId) throws SQLException{
		//get the id we need from the join table
		int resultId=this.getCategoricalResultIdFromStatsResultPhenotypeCallSummary(phenotypeCallSummaryId);
		//use the join table id to get the actual result
		return (CategoricalResult) getCurrentSession().get(CategoricalResult.class,  resultId);
	}

    private int getCategoricalResultIdFromStatsResultPhenotypeCallSummary(int id)throws SQLException{
        //get the id we need from the join table
		int result=-1;
		String query = "SELECT categorical_result_id FROM stat_result_phenotype_call_summary where phenotype_call_summary_id= '"
				+ id + "'";

		try (PreparedStatement statement = getConnection().prepareStatement(query)) {

			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				result=resultSet.getInt(1);
			}
		}
		return result;
    }

}
