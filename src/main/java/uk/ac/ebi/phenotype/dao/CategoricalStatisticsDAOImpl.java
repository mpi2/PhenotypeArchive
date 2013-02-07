package uk.ac.ebi.phenotype.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.ebi.phenotype.pojo.BiologicalModel;
import uk.ac.ebi.phenotype.pojo.CategoricalControlView;
import uk.ac.ebi.phenotype.pojo.CategoricalMutantView;
import uk.ac.ebi.phenotype.pojo.CategoricalResult;
import uk.ac.ebi.phenotype.pojo.Observation;
import uk.ac.ebi.phenotype.pojo.Organisation;
import uk.ac.ebi.phenotype.pojo.Parameter;
import uk.ac.ebi.phenotype.pojo.ParameterOption;
import uk.ac.ebi.phenotype.pojo.SexType;
import uk.ac.ebi.phenotype.pojo.ZygosityType;

public class CategoricalStatisticsDAOImpl extends HibernateDAOImpl implements CategoricalStatisticsDAO {

	public CategoricalStatisticsDAOImpl(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	@Transactional(readOnly = true)
	public List<String> getCategories(Parameter parameter) {

		// Get the list of categories that are appropriate for this parameter

		List<ParameterOption> options = parameter.getOptions();
		List<String> categories = new ArrayList<String>();
		for (ParameterOption option : options ){
			categories.add(option.getName());
		}
		return categories;
	}

	@Transactional(readOnly = true)
	public Long hasEnoughData(SexType sex, ZygosityType zygosity, Parameter parameter, Integer populationId){
		return (Long) getCurrentSession()
			.createQuery("SELECT COUNT(*) FROM CategoricalMutantView c WHERE c.sex=? AND c.zygosity=? AND c.parameter=? AND c.populationId=?")
			.setString(0, sex.name())
			.setString(1, zygosity.name())
			.setLong(2, parameter.getId())
			.setInteger(3, populationId)
			.list()
			.get(0);
	}

	
	@Transactional(readOnly = true)
	public Long countControl(SexType sex, Parameter parameter, String category, Integer populationId){
		return (Long) getCurrentSession()
			.createQuery("SELECT COUNT(*) FROM CategoricalControlView c WHERE c.sex=? AND c.parameter=? AND c.category=? AND c.populationId=?")
			.setString(0, sex.name())
			.setLong(1, parameter.getId())
			.setString(2, category)
			.setInteger(3, populationId)
			.list()
			.get(0);
	}

	@Transactional(readOnly = true)
	public Long countMutant(SexType sex, ZygosityType zygosity, Parameter parameter, String category, Integer populationId){
		return (Long) getCurrentSession()
			.createQuery("SELECT COUNT(*) FROM CategoricalMutantView c WHERE c.sex=? AND c.zygosity=? AND c.parameter=? AND c.category=? AND c.populationId=?")
			.setString(0, sex.name())
			.setString(1, zygosity.name())
			.setLong(2, parameter.getId())
			.setString(3, category)
			.setInteger(4, populationId)
			.list()
			.get(0);
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
	public List<Integer> getPopulationIdsByParameter(Parameter parameter) {
		return getCurrentSession().createQuery("SELECT DISTINCT populationId FROM CategoricalMutantView WHERE parameter=?")
			.setLong(0, parameter.getId())
			.list();	
	}


	@Transactional(readOnly = true)
	public Organisation getOrganisationByPopulation(Integer populationId) {
		CategoricalControlView ccv = (CategoricalControlView) getCurrentSession().createQuery("FROM CategoricalControlView c WHERE c.populationId=?")
			.setInteger(0, populationId)
			.list()
			.get(0);
		return ccv.getOrganisation();
	}


	/**
	 * return the SexType ENUM associated to this population
	 */
	@Transactional(readOnly = true)
	public SexType getSexByPopulation(Integer populationId) {
		CategoricalMutantView ccv = (CategoricalMutantView) getCurrentSession().createQuery("FROM CategoricalMutantView c WHERE c.populationId=?")
			.setInteger(0, populationId)
			.list()
			.get(0);
		return SexType.valueOf(ccv.getSex());
	}

	/**
	 * return the ZygosityType ENUM associated to this population
	 */
	@Transactional(readOnly = true)
	public ZygosityType getZygosityByPopulation(Integer populationId) {
		CategoricalMutantView ccv = (CategoricalMutantView) getCurrentSession().createQuery("FROM CategoricalMutantView c WHERE c.populationId=?")
			.setInteger(0, populationId)
			.list()
			.get(0);
		return ZygosityType.valueOf(ccv.getZygosity());
	}


	@Transactional(readOnly = false)
	public void saveCategoricalResult(CategoricalResult result) {
		getCurrentSession().saveOrUpdate(result);
		getCurrentSession().flush();
	}

}
