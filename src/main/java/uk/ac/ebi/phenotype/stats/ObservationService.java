package uk.ac.ebi.phenotype.stats;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.ac.ebi.phenotype.dao.PhenotypePipelineDAO;
import uk.ac.ebi.phenotype.dao.UnidimensionalStatisticsDAO;
import uk.ac.ebi.phenotype.pojo.Parameter;
import uk.ac.ebi.phenotype.pojo.SexType;

@Service
public class ObservationService {

	@Autowired
	UnidimensionalStatisticsDAO uDAO;
	
	@Autowired
	PhenotypePipelineDAO parameterDAO;
	
	private static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'00:00:00");

	private HttpSolrServer solr;

	public ObservationService() {
		String solrURL = "http://localhost:8080/solr/experiment"; //default
		solr = new HttpSolrServer(solrURL);
	}

	public ObservationService(String solrUrl) {
		solr = new HttpSolrServer(solrUrl);
	}

	/**
	 * Wrapper to get the parameter ID when passed a parameter object.  This
	 * calls the getControls method with an integer for a parameterID
	 * 
	 * @param parameterStableId the stable identifier of the parameter in question
	 * @param strain the strain
	 * @param organisationId the organisation
	 * @param max the date at which to cut off results (i.e. no results after date "max" will be returned) 
	 * @return list of observations 
	 * @throws SolrServerException when solr has a troubled mind/heart/body
	 */
	protected List<ObservationDTO> getControls(String parameterStableId, String strain, Integer organisationId, Date max) throws SolrServerException {
		Parameter p = parameterDAO.getParameterByStableIdAndVersion(parameterStableId, 1, 0);
		return getControls(p.getId(), strain, organisationId, max);
	}

	protected List<ObservationDTO> getControls(Integer parameterId, String strain, Integer organisationId, Date max) throws SolrServerException {

		return getControls(parameterId, strain, organisationId, max, Boolean.FALSE, null);
	}

	/**
	 * get control data observations for the combination of parameters passed
	 * in.
	 * 
	 * @param parameterStableId the stable identifier of the parameter in question
	 * @param strain the strain
	 * @param organisationId the organisation
	 * @param max the date at which to cut off results (i.e. no results after date "max" will be returned) 
	 * @return list of observations 
	 * @throws SolrServerException when solr has a troubled mind/heart/body
	 */
	protected List<ObservationDTO> getControls(Integer parameterId, String strain, Integer organisationId, Date max, Boolean showAll, String sex) throws SolrServerException {

		int n = 1000;		
		if (showAll){
			n = 10000000;
		}
		List<ObservationDTO> results = new ArrayList<ObservationDTO>();
		if (sex == null){
			results.addAll(getControlsBySex(parameterId, strain, organisationId, max, showAll, "female", n/2));
			results.addAll(getControlsBySex(parameterId, strain, organisationId, max, showAll, "male", n/2));
		}
		else 
			results.addAll(getControlsBySex(parameterId, strain, organisationId, max, showAll, sex, n));
		System.out.println("Controls returned: " +  results.size());
		return results;
	}

	private  List<ObservationDTO> getControlsBySex(Integer parameterId, String strain, Integer organisationId, Date max, Boolean showAll, String sex, int resultsMaxSize) throws SolrServerException{
		
		int dateIncrement = 6; // month
		
		List<ObservationDTO> results = new ArrayList<ObservationDTO>();
		Boolean withinTimeLimits = Boolean.TRUE;
		QueryResponse responseb = new QueryResponse();
		QueryResponse responsea = new QueryResponse();
		Date today = new Date();
		Date minDate = new Date();
		SimpleDateFormat f = new SimpleDateFormat("dd-MMM-yyyy");
		try {
			minDate = f.parse("01-Jan-2000");
		} catch (ParseException e) {
			e.printStackTrace();
		} // Jan 1, 2000 UTC
		
		SolrQuery queryb = new SolrQuery()
		.setQuery("*:*")
		.addFilterQuery("biologicalSampleGroup:control")
		.addFilterQuery("dateOfExperiment:["+df.format(minDate)+"Z TO "+df.format(max)+"Z]")
		.addFilterQuery("parameterId:"+parameterId)
		.addFilterQuery("organisationId:"+organisationId)
		.addFilterQuery("strain:"+strain.replace(":", "\\:"))
		.addFilterQuery("gender:"+sex)
		.setSortField("dateOfExperiment", ORDER.desc) // good for dates before the date of the experiment
		;
		queryb.setStart(0).setRows(resultsMaxSize);
		
		SolrQuery querya = new SolrQuery()
		.setQuery("*:*")
		.addFilterQuery("biologicalSampleGroup:control")
		.addFilterQuery("dateOfExperiment:["+df.format(DateUtils.addDays(max, -1))+"Z TO "+df.format(today)+"Z]")
		.addFilterQuery("parameterId:"+parameterId)
		.addFilterQuery("organisationId:"+organisationId)
		.addFilterQuery("strain:"+strain.replace(":", "\\:"))
		.addFilterQuery("gender:"+sex)
		.setSortField("dateOfExperiment", ORDER.asc) // good for dates after the date of the experiment
		;
		querya.setStart(0).setRows(resultsMaxSize);

		responseb = solr.query(queryb);
		responsea = solr.query(querya);
		
		List<ObservationDTO> resA = responsea.getBeans(ObservationDTO.class);
		List<ObservationDTO> resB = responseb.getBeans(ObservationDTO.class);
		ArrayList<ObservationDTO> closest = new ArrayList<ObservationDTO>();
		long datInMs = max.getTime();
		
		while (results.size() < resultsMaxSize && resA.size() > 0 && resB.size() > 0){
			closest = getClosest(datInMs, resA.get(0), resB.get(0));
			// (b, a)
			if (closest.get(0) != null){
				results.add(closest.get(0));
				resA.remove(0);
			}
			if (closest.get(1) != null){
				results.add(closest.get(1));
				resB.remove(0);
			}
		}
		
//		results.addAll(responsea.getBeans(ObservationDTO.class).subList(0, neededAfter));
//		results.addAll(responseb.getBeans(ObservationDTO.class).subList(0, neededBefore));

//		System.out.println(solr.getBaseURL() + querya);
//		System.out.println(solr.getBaseURL() + queryb);
		
//		System.out.println("------Got after : " + sizeA );
//		System.out.println("------Got before : " + sizeB );
//		System.out.println("------Return total " + results.size());
		
	/*/
	 * 
http://ves-ebi-d0.ebi.ac.uk:8090/mi/impc/dev/solr/experimentq=*%3A*&fq=biologicalSampleGroup%3Acontrol&fq=dateOfExperiment%3A%5B2009-12-14T00%3A00%3A00Z+TO+2013
-11-07T00%3A00%3A00Z%5D&fq=parameterId%3A1326&fq=organisationId%3A8&fq=strain%3AMGI%5C%3A2164831&fq=gender%3Amale&sort=dateOfExperiment+asc

http://ves-ebi-d0.ebi.ac.uk:8090/mi/impc/dev/solr/experimentq=*%3A*&fq=biologicalSampleGroup%3Acontrol&fq=dateOfExperiment%3A%5B2009-12-14T00%3A00%3A00Z+TO+2010
-12-15T00%3A00%3A00Z%5D&fq=parameterId%3A1326&fq=organisationId%3A8&fq=strain%3AMGI%5C%3A2164831&fq=gender%3Amale&sort=dateOfExperiment+asc&start=0&rows=500
------Got after : 10 take 10
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 */
/*		int resSize = 0;
		int loop = 1;
		
		while (resSize < resultsMaxSize && withinTimeLimits) {
			Date start = DateUtils.addMonths(max, -(dateIncrement*loop));			
			Date end = DateUtils.addMonths(max, (dateIncrement*loop));

			if (start.before(minDate) && end.after(today)) { // Jan 1, 2000 UTC & today
				withinTimeLimits = Boolean.FALSE;
			}			
			
			SolrQuery queryb = new SolrQuery()
			.setQuery("*:*")
			.addFilterQuery("biologicalSampleGroup:control")
			.addFilterQuery("dateOfExperiment:["+df.format(start)+"Z TO "+df.format(max)+"Z]")
			.addFilterQuery("parameterId:"+parameterId)
			.addFilterQuery("organisationId:"+organisationId)
			.addFilterQuery("strain:"+strain.replace(":", "\\:"))
			.addFilterQuery("gender:"+sex)
			.setSortField("dateOfExperiment", ORDER.desc) // good for dates before the date of the experiment
			;
			queryb.setStart(0).setRows(resultsMaxSize/2);
			
			SolrQuery querya = new SolrQuery()
			.setQuery("*:*")
			.addFilterQuery("biologicalSampleGroup:control")
			.addFilterQuery("dateOfExperiment:["+df.format(DateUtils.addDays(max, -1))+"Z TO "+df.format(end)+"Z]")
			.addFilterQuery("parameterId:"+parameterId)
			.addFilterQuery("organisationId:"+organisationId)
			.addFilterQuery("strain:"+strain.replace(":", "\\:"))
			.addFilterQuery("gender:"+sex)
			.setSortField("dateOfExperiment", ORDER.asc) // good for dates after the date of the experiment
			;
			
			querya.setStart(0).setRows(resultsMaxSize/2);

			responseb = solr.query(queryb);
			responsea = solr.query(querya);
			
			// in case the data is all on one side of the max date but all within the same time frame, take it all
			int resSizeA = responsea.getResults().size();
			int resSizeB = responseb.getResults().size();
			resSize = resSizeB + resSizeA;
			
			
			if (resSize < resultsMaxSize){
				if (resSizeA == resultsMaxSize/2){
					querya.setStart(0).setRows(resultsMaxSize);
					responsea = solr.query(querya);
					resSizeA = responsea.getResults().size();
					
				}
				else if (resSizeB == resultsMaxSize/2){
					queryb.setStart(0).setRows(resultsMaxSize);
					responseb = solr.query(queryb);
					resSizeB = responseb.getResults().size();
				}
			}
			
			resSize = resSizeB + resSizeA;

	//		System.out.println("resSizeA : " + resSizeA);
	//		System.out.println("resSizeB : " + resSizeB);
			

			System.out.println(solr.getBaseURL() + querya);
			System.out.println(solr.getBaseURL() + querybs);
			loop++;
		}

		results.addAll(responsea.getBeans(ObservationDTO.class));
		results.addAll(responseb.getBeans(ObservationDTO.class));
		
		
		
//		for (ObservationDTO obs : results){
//				System.out.println(obs);
//			}
 //*/
		System.out.println("returning : " + results.size() + " for " + sex);
//		System.out.println("---- I RETURN :" + results.size() + " , " + responsea.getResults().size() + " + " + responseb.getResults().size()) ;
		return results;
	}

	// list <b,a>
	private ArrayList<ObservationDTO> getClosest (long datInMs, ObservationDTO aObj, ObservationDTO bObj){
		ArrayList<ObservationDTO> res = new ArrayList<>();
		long distanceA = aObj.getDateOfExperiment().getTime() - datInMs;
		long distanceB = datInMs - bObj.getDateOfExperiment().getTime();
		if (distanceA == distanceB){
			res.add(aObj);
			res.add(bObj);
		}
		else if (distanceA < distanceB){
			res.add(aObj);
			res.add(null);
		}
		else if (distanceB < distanceA){
			res.add(null);
			res.add(bObj);
		}
		return res;
	}
	
	/**
	 * Return all the unidimensional observations for a given
	 * combination of parameter, gene, zygosity, organisation and strain
	 *   
	 * ex solr query: parameterId:1116%20AND%20geneAccession:MGI\:1923523%20AND%20zygosity:homozygote%20AND%20organisationId:9%20AND%20colonyId:HEPD0550_6_G09%20AND%20gender:female
	 * 
	 * @param parameterId
	 * @param gene
	 * @param zygosity
	 * @param organisationId
	 * @param strain
	 * @return
	 * @throws SolrServerException
	 */
	public List<ObservationDTO> getUnidimensionalObservationsByParameterGeneAccZygosityOrganisationStrain(Integer parameterId, String gene, String zygosity, Integer organisationId, String strain) throws SolrServerException {
		List<ObservationDTO> resultsDTO = new ArrayList<ObservationDTO>();

		SolrQuery query = new SolrQuery()
	    	.setQuery("geneAccession:"+gene.replace(":", "\\:") + " AND zygosity:"+zygosity+"")
	    	.addFilterQuery("parameterId:"+parameterId)
	    	.addFilterQuery("organisationId:"+organisationId)
	    	.addFilterQuery("strain:"+strain.replace(":", "\\:"))
	    	;
	    query.setStart(0).setRows(1000);
	
	    QueryResponse response = solr.query(query);
	    resultsDTO = response.getBeans(ObservationDTO.class);
	    
	    Date max = new Date(0L); //epoch
	    for (ObservationDTO o : resultsDTO) {
	    	if (o.getDateOfExperiment().after(max)) {
	    		max=o.getDateOfExperiment();
	    	}
	    }
	    System.out.println("---- Experiment date ----  " + max);

		resultsDTO.addAll(getControls(parameterId, strain, organisationId, max));
	    
		return resultsDTO;
	}

	public String getQueryStringByParameterGeneAccZygosityOrganisationStrain(Integer parameterId, String gene, String zygosity, Integer organisationId, String strain) throws SolrServerException {

		SolrQuery query = new SolrQuery()
	    	.setQuery("geneAccession:"+gene.replace(":", "\\:") + " AND zygosity:"+zygosity+"")
	    	.addFilterQuery("parameterId:"+parameterId)
	    	.addFilterQuery("organisationId:"+organisationId)
	    	.addFilterQuery("strain:"+strain.replace(":", "\\:"))
	    	.setStart(0)
	    	.setRows(1000);
		return query.toString();
	}

	public String getQueryStringByParameterGeneAccZygosityOrganisationStrainSex(Integer parameterId, String gene, String zygosity, Integer organisationId, String strain, SexType sex) throws SolrServerException {

		SolrQuery query = new SolrQuery()
	    	.setQuery("geneAccession:"+gene.replace(":", "\\:") + " AND zygosity:"+zygosity+"")
	    	.addFilterQuery("parameterId:"+parameterId)
	    	.addFilterQuery("organisationId:"+organisationId)
	    	.addFilterQuery("strain:"+strain.replace(":", "\\:"))
	    	.addFilterQuery("gender:"+sex.name())
	    	.setStart(0)
	    	.setRows(1000);
		return query.toString();
	}
	
	/**
	 * 
	 * construct a query to get all the categortical observations for a given
	 * combination of parameter, gene, zygosity, organisation and strain
	 *   
	 * ex solr query: parameterId:1116%20AND%20geneAccession:MGI\:1923523%20AND%20zygosity:homozygote%20AND%20organisationId:9%20AND%20colonyId:HEPD0550_6_G09%20AND%20gender:female
	 * 
	 * @param parameterId
	 * @param gene
	 * @param zygosity
	 * @param organisationId
	 * @param strain
	 * @param sex
	 * @return
	 * @throws SolrServerException
	 */
	public List<ObservationDTO> getCategoricalObservationsByParameterGeneAccZygosityOrganisationStrainSex(Integer parameterId, String gene, String zygosity, Integer organisationId, String strain, String sex) throws SolrServerException {

		List<ObservationDTO> resultsDTO = new ArrayList<ObservationDTO>();

		SolrQuery query = new SolrQuery()
	    	.setQuery("((geneAccession:"+gene.replace(":", "\\:") + " AND zygosity:"+zygosity+") OR biologicalSampleGroup:control) ")
	    	.addFilterQuery("parameterId:"+parameterId)
	    	.addFilterQuery("organisationId:"+organisationId)
	    	.addFilterQuery("strain:"+strain.replace(":", "\\:"))
	    	.addFilterQuery("gender:"+sex)
	    	;
	    query.setStart(0).setRows(10000);    

	    QueryResponse response = solr.query(query);
	    resultsDTO = response.getBeans(ObservationDTO.class);

		return resultsDTO;
	}

	public List<ObservationDTO> getExperimentalUnidimensionalObservationsByParameterGeneAcc(Integer parameterId, String geneAccession) throws SolrServerException {

		SolrQuery query = new SolrQuery()
	    	.setQuery("geneAccession:"+geneAccession.replace(":", "\\:"))
	    	.addFilterQuery("parameterId:"+parameterId)
	    	.addFilterQuery("biologicalSampleGroup:experimental")
	    	.setStart(0)
	    	.setRows(10000);
		
		System.out.println("SOLR QWUERY : " + solr.getBaseURL() + query);
		
		return solr.query(query).getBeans(ObservationDTO.class);
	}

	public List<ObservationDTO> getExperimentalUnidimensionalObservationsByParameterGeneAccZygosityOrganisationStrainSex(Integer parameterId, String gene, String zygosity, Integer organisationId, String strain, SexType sex) throws SolrServerException {
		
		List<ObservationDTO> resultsDTO = new ArrayList<ObservationDTO>();
		SolrQuery query = new SolrQuery().setQuery("geneAccession:"+gene.replace(":", "\\:"));
		if (zygosity != null && !zygosity.equalsIgnoreCase("null"))
			query.addFilterQuery("zygosity:"+zygosity);
		query.addFilterQuery("parameterId:"+parameterId);
		if (strain != null){
	    	query.addFilterQuery("strain:"+strain.replace(":", "\\:"));
		}
		if (organisationId != null){
			query.addFilterQuery("organisationId:"+organisationId);
		}
		if (sex != null){
			query.addFilterQuery("gender:"+sex);
		}
	    query.setStart(0).setRows(10000);    
	    
	    System.out.println("SOLR QUERY : " + solr.getBaseURL() + query.toString());
	    
	    QueryResponse response = solr.query(query);
	    resultsDTO = response.getBeans(ObservationDTO.class);
		return resultsDTO;
	}

	public List<ObservationDTO> getObservationsByParameterGeneAccZygosityOrganisationStrainSex(Integer parameterId, String gene, String zygosity, Integer organisationId, String strain, SexType sex) throws SolrServerException {
        List<ObservationDTO> resultsDTO = new ArrayList<ObservationDTO>();

        SolrQuery query = new SolrQuery()
            .setQuery("((geneAccession:"+gene.replace(":", "\\:") + " AND zygosity:"+zygosity+") OR biologicalSampleGroup:control) ")
            .addFilterQuery("parameterId:"+parameterId)
            .addFilterQuery("organisationId:"+organisationId)
            .addFilterQuery("strain:"+strain.replace(":", "\\:"))
            .addFilterQuery("gender:"+sex)
            ;
    query.setStart(0).setRows(10000);    

    QueryResponse response = solr.query(query);
    resultsDTO = response.getBeans(ObservationDTO.class);

        return resultsDTO;
}

}
