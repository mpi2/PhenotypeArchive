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
package uk.ac.ebi.phenotype.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest.METHOD;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.PivotField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.FacetParams;
import org.apache.solr.common.util.NamedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.ac.ebi.generic.util.JSONRestUtil;
import uk.ac.ebi.phenotype.chart.CategoricalDataObject;
import uk.ac.ebi.phenotype.chart.CategoricalSet;
import uk.ac.ebi.phenotype.dao.DiscreteTimePoint;
import uk.ac.ebi.phenotype.dao.PhenotypePipelineDAO;
import uk.ac.ebi.phenotype.data.cda.DataBatchesBySex;
import uk.ac.ebi.phenotype.pojo.ObservationType;
import uk.ac.ebi.phenotype.pojo.Parameter;
import uk.ac.ebi.phenotype.pojo.ProcedurePojo;
import uk.ac.ebi.phenotype.pojo.SexType;
import uk.ac.ebi.phenotype.pojo.ZygosityType;
import uk.ac.ebi.phenotype.service.dto.ObservationDTO;
import uk.ac.ebi.phenotype.service.dto.ParallelCoordinatesDTO;
import uk.ac.ebi.phenotype.service.dto.PipelineDTO;
import uk.ac.ebi.phenotype.service.dto.StatisticalResultDTO;
import uk.ac.ebi.phenotype.solr.indexer.beans.ImpressBean;
import uk.ac.ebi.phenotype.web.controller.OverviewChartsController;

@Service
public class ObservationService extends BasicService {

    @Autowired
    PhenotypePipelineDAO parameterDAO;

    private static final Logger LOG = LoggerFactory.getLogger(ObservationService.class);
    private final HttpSolrServer solr;


    public ObservationService() {

        this("http://wwwdev.ebi.ac.uk/mi/impc/dev/solr/experiment"); // default
    }



    public ObservationService(String solrUrl) {
        System.out.println("setting observationService solrUrl=" + solrUrl);
        solr = new HttpSolrServer(solrUrl);
    }

    public  List<Group> getDatapointsByColony(ArrayList<String> resourceName, String parameterStableId, String biologicalSampleGroup)
    throws SolrServerException{

    	SolrQuery q = new SolrQuery();
    	if (resourceName != null) {
            q.setQuery(ObservationDTO.DATASOURCE_NAME + ":" + StringUtils.join(resourceName, " OR " + ObservationDTO.DATASOURCE_NAME + ":"));
        } else {
            q.setQuery("*:*");
        }

    	if (parameterStableId != null){
    		q.addFilterQuery(ObservationDTO.PARAMETER_STABLE_ID + ":" + parameterStableId);
    	}

    	q.addFilterQuery(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":" + biologicalSampleGroup);

    	q.set("group", true);
    	q.set("group.field", ObservationDTO.COLONY_ID);
    	q.set("group.limit", 10000);
    	q.set("group.sort" , ObservationDTO.DATE_OF_EXPERIMENT + " ASC");

    	q.setFields(ObservationDTO.DATA_POINT, ObservationDTO.ZYGOSITY, ObservationDTO.SEX, ObservationDTO.DATE_OF_EXPERIMENT,
			ObservationDTO.ALLELE_SYMBOL, ObservationDTO.GENE_SYMBOL, ObservationDTO.COLONY_ID , ObservationDTO.ALLELE_ACCESSION_ID,
			ObservationDTO.PIPELINE_ID, ObservationDTO.PHENOTYPING_CENTER, ObservationDTO.GENE_ACCESSION_ID, ObservationDTO.STRAIN_ACCESSION_ID,
			ObservationDTO.PARAMETER_ID, ObservationDTO.PHENOTYPING_CENTER_ID);
        q.setRows(10000);

        System.out.println("Solr url for getOverviewGenesWithMoreProceduresThan " + solr.getBaseURL() + "/select?" + q);
        return solr.query(q).getGroupResponse().getValues().get(0).getValues();

    }


    public String getMeansFor(String procedueStableId, boolean requiredParametersOnly)
    throws SolrServerException{

    	HashMap<String, ParallelCoordinatesDTO> row = new HashMap<>();
    	// get parameterStableId facets for  procedueStableId

    	SolrQuery query = new SolrQuery();
    	query.setQuery("*:*");
    	query.setFilterQueries(ObservationDTO.PROCEDURE_STABLE_ID + ":" + procedueStableId);
    	query.addFilterQuery(ObservationDTO.OBSERVATION_TYPE + ":unidimensional");
    	query.setFacet(true);
    	query.setFacetMinCount(1);
    	query.setFacetLimit(100000);
    	query.addFacetField(ObservationDTO.PARAMETER_STABLE_ID);
    	query.addFacetField(ObservationDTO.PARAMETER_NAME);


    	ArrayList<String> parameterStableIds = new ArrayList<>(getFacets(solr.query(query)).get(ObservationDTO.PARAMETER_STABLE_ID).keySet());
    	ArrayList<Parameter> parameterNames = new ArrayList<>();

    	for (String parameterStableId: parameterStableIds){
        	Parameter p = parameterDAO.getParameterByStableId(parameterStableId);
        	if (p.isRequiredFlag()){
        		parameterNames.add(p);
        	}
    	}

    	// query for each parameter

    	int i = 0;

    	for (Parameter p: parameterNames){
    		query = new SolrQuery();
        	query.setQuery("*:*");
        	query.setFilterQueries(ObservationDTO.PARAMETER_STABLE_ID + ":\"" + p.getStableId() + "\"");
        	query.set("group", true);
        	query.set("group.limit", 10000);
        	query.set("group.field", ObservationDTO.GENE_SYMBOL);
        	query.addField(ObservationDTO.DATA_POINT);
        	query.addField(ObservationDTO.PHENOTYPING_CENTER);
        	query.setRows(100000);

        	System.out.println("-- Get means:  " + solr.getBaseURL() + "/select?" + query);

        	row = addMeans(solr.query(query), row, p, parameterNames);
        //	i++;
        //	if (i>100){
        //		break;
        //	}
    	}

    	row = addDefaultMean(row, parameterNames);
    	
		String res = "[";
		String defaultMeans = "";
		i = 0;
    	for (String key: row.keySet()){
    		ParallelCoordinatesDTO bean = row.get(key);
    		if (key == null || !key.equalsIgnoreCase(ParallelCoordinatesDTO.DEFAULT)){
	    		i++;
	    		String currentRow = bean.toString(false);
	    		if (!currentRow.equals("")){
		    		res += "{" + currentRow + "}";
		    		if (i < row.values().size()){
		    			res += ", ";
		    		}
	    		}
    		}
    		else {
    			String currentRow = bean.toString(false);
    			defaultMeans += "{" + currentRow + "}\n";
    		}
    	}
    	res += "]";
    	
    	return "var foods = " + res.toString() + "; \n\n var defaults = " + defaultMeans +";" ;

    }

    
    private HashMap<String, ParallelCoordinatesDTO> addDefaultMean(HashMap<String, ParallelCoordinatesDTO> beans, ArrayList<Parameter> allParameterNames) {

    	ParallelCoordinatesDTO currentBean = new ParallelCoordinatesDTO(ParallelCoordinatesDTO.DEFAULT,  null, null, allParameterNames);
        
    	HashMap<String, ArrayList<Double>> defaultData = new HashMap(); // <parameter name, <mean values>>
    	for (Parameter param : allParameterNames){
    		defaultData.put(param.getName(), new ArrayList<Double>());
    	}
    	
    	for (String key : beans.keySet()){
    		ParallelCoordinatesDTO pc = beans.get(key);
    		for ( String meanKey : pc.getMeans().keySet()){
    			defaultData.get(meanKey).add(pc.getMeans().get(meanKey).getMean());
    		}
    	}
    	
    	for (String key : defaultData.keySet()){
    		Double mean = new Double(0);
    		int sum = 0;
    		for (Double value : defaultData.get(key)){
    			if (value != null){
    				mean += value;
    				sum ++;
    		}
    		}
    		mean = mean / sum;
            currentBean.addMean(null, null, key, null, mean);
    	}
    	
        beans.put(ParallelCoordinatesDTO.DEFAULT, currentBean);
    	
    	return beans;
    	
    }
    	  

    private HashMap<String, ParallelCoordinatesDTO> addMeans(QueryResponse response, HashMap<String, ParallelCoordinatesDTO> beans, Parameter p, ArrayList<Parameter> allParameterNames) {

    	 List<Group> solrGroups = response.getGroupResponse().getValues().get(0).getValues();
        
    	 for (Group gr : solrGroups) {
             SolrDocumentList resDocs = gr.getResult();
        	 HashMap<String, ArrayList<Double> >dataByGroup = new HashMap<>(); // <center, <values>> for each gene
             for (int i = 0; i < resDocs.getNumFound(); i ++) {
                 SolrDocument doc = resDocs.get(i);
                 String center = doc.getFieldValue(ObservationDTO.PHENOTYPING_CENTER).toString();
                 if (!dataByGroup.containsKey(center)){
                	 dataByGroup.put(center, new ArrayList<Double>());
                 }
                 dataByGroup.get(center).add(new Double(doc.getFieldValue(ObservationDTO.DATA_POINT).toString()));
             }
             String gene = gr.getGroupValue();
             for (String center : dataByGroup.keySet()){
                 String group = (gene == null) ? "WT " : center;
	             ParallelCoordinatesDTO currentBean = beans.containsKey(gene + " " + group)? beans.get(gene + " " + group) : new ParallelCoordinatesDTO(gene,  null, group, allParameterNames);
	             Double mean = new Double(0);
		     	 int sum = 0;
		     	 for (Double value : dataByGroup.get(center)){
		     		 if (value != null){
		     			mean += value;
		     			sum ++;
		     		 }
		     	 }
		     	 mean = mean / sum;
	             currentBean.addMean(p.getUnit(), p.getStableId(), p.getName(), null, mean);
	             beans.put(gene + " " + group, currentBean);
             }
         }
         return beans;
	}


	public List<String> getGenesWithMoreProcedures(int n, ArrayList<String> resourceName)
    throws SolrServerException, InterruptedException, ExecutionException {

        List<String> genes = new ArrayList<>();
        SolrQuery q = new SolrQuery();

        if (resourceName != null) {
            q.setQuery(ObservationDTO.DATASOURCE_NAME + ":" + StringUtils.join(resourceName, " OR " + ObservationDTO.DATASOURCE_NAME + ":"));
        } else {
            q.setQuery("*:*");
        }

        String geneProcedurePivot = ObservationDTO.GENE_SYMBOL + "," + ObservationDTO.PROCEDURE_NAME;

        q.add("facet.pivot", geneProcedurePivot);

        q.setFacet(true);
        q.setRows(1);
        q.setFacetMinCount(1);
        q.set("facet.limit", -1);

        System.out.println("Solr url for getOverviewGenesWithMoreProceduresThan " + solr.getBaseURL() + "/select?" + q);
        QueryResponse response = solr.query(q);

        for (PivotField pivot : response.getFacetPivot().get(geneProcedurePivot)) {
            if (pivot.getPivot().size() >= n) {
                genes.add(pivot.getValue().toString());
            }
        }

        return genes;
    }

    public List<ObservationDTO> getObservationsByParameterStableId(String parameterStableId) throws SolrServerException {
        SolrQuery query = new SolrQuery();
        query.setQuery(String.format("%s:\"%s\"", ObservationDTO.PARAMETER_STABLE_ID, parameterStableId));
        query.setRows(Integer.MAX_VALUE);
        return solr.query(query).getBeans(ObservationDTO.class);
    }

    public long getNumberOfDocuments(List<String> resourceName, boolean experimentalOnly)
            throws SolrServerException {

        SolrQuery query = new SolrQuery();
        query.setRows(0);
        if (resourceName != null) {
            query.setQuery(ObservationDTO.DATASOURCE_NAME + ":" + StringUtils.join(resourceName, " OR " + ObservationDTO.DATASOURCE_NAME + ":"));
        } else {
            query.setQuery("*:*");
        }
        if (experimentalOnly) {
            query.addFilterQuery(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":experimental");
        }

        return solr.query(query).getResults().getNumFound();
    }

    public QueryResponse getViabilityData(List<String> resources)
            throws SolrServerException {

        SolrQuery query = new SolrQuery();
        if (resources != null) {
            query.setFilterQueries(ObservationDTO.DATASOURCE_NAME + ":" + StringUtils.join(resources, " OR " + ObservationDTO.DATASOURCE_NAME + ":"));
        }
        query.setQuery(ObservationDTO.PARAMETER_STABLE_ID + ":IMPC_VIA_001_001");
        query.addField(ObservationDTO.GENE_SYMBOL);
        query.addField(ObservationDTO.COLONY_ID);
        query.addField(ObservationDTO.CATEGORY);
        query.setRows(100000);

        System.out.println("getViabilityData Url" + solr.getBaseURL() + "/select?" + query);

        return solr.query(query);
    }

    public Map<String, Set<String>> getColoniesByPhenotypingCenter(ArrayList<String> resourceName, ZygosityType zygosity)
            throws SolrServerException, InterruptedException {

        Map<String, Set<String>> res = new HashMap<>();
        SolrQuery q = new SolrQuery();
        String pivotFacet = ObservationDTO.PHENOTYPING_CENTER + "," + ObservationDTO.COLONY_ID;
        NamedList<List<PivotField>> response;

        if (resourceName != null) {
            q.setQuery(ObservationDTO.DATASOURCE_NAME + ":" + StringUtils.join(resourceName, " OR " + ObservationDTO.DATASOURCE_NAME + ":"));
        } else {
            q.setQuery("*:*");
        }

        if (zygosity != null) {
            q.addFilterQuery(ObservationDTO.ZYGOSITY + ":" + zygosity.name());
        }

        q.addFilterQuery(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":experimental");
        q.addFacetPivotField(pivotFacet);
        q.setFacet(true);
        q.setFacetLimit(-1);
        q.setFacetMinCount(1);
        q.setRows(0);

        try {
            response = solr.query(q).getFacetPivot();
            for (PivotField genePivot : response.get(pivotFacet)) {
                String center = genePivot.getValue().toString();
                HashSet<String> colonies = new HashSet<>();
                for (PivotField f : genePivot.getPivot()) {
                    colonies.add(f.getValue().toString());
                }
                res.put(center, colonies);
            }
        } catch (SolrServerException e) {
            e.printStackTrace();
        }
        return res;
    }

    public Map<String, List<String>> getExperimentKeys(String mgiAccession, String parameterStableId, List<String> pipelineStableId, List<String> phenotypingCenterParams, List<String> strainParams, List<String> metaDataGroups, List<String> alleleAccessions)
            throws SolrServerException {

		// Example of key
        // String experimentKey = observation.getPhenotypingCenter()
        // + observation.getStrain()
        // + observation.getParameterStableId()
        // + observation.getGeneAccession()
        // + observation.getMetadataGroup();
        Map<String, List<String>> map = new LinkedHashMap<>();

        SolrQuery query = new SolrQuery();

        query.setQuery(ObservationDTO.GENE_ACCESSION_ID + ":\"" + mgiAccession + "\"").addFilterQuery(ObservationDTO.PARAMETER_STABLE_ID + ":" + parameterStableId).addFacetField(ObservationDTO.PHENOTYPING_CENTER).addFacetField(ObservationDTO.STRAIN_ACCESSION_ID).addFacetField(ObservationDTO.METADATA_GROUP).addFacetField(ObservationDTO.PIPELINE_STABLE_ID).addFacetField(ObservationDTO.ALLELE_ACCESSION_ID).setRows(0).setFacet(true).setFacetMinCount(1).setFacetLimit(-1).setFacetSort(FacetParams.FACET_SORT_COUNT);

        if (phenotypingCenterParams != null &&  ! phenotypingCenterParams.isEmpty()) {
            List<String> spaceSafeStringsList = new ArrayList<String>();
            for (String pCenter : phenotypingCenterParams) {
                if ( ! pCenter.endsWith("\"") &&  ! pCenter.startsWith("\"")) {
                    spaceSafeStringsList.add("\"" + pCenter + "\"");
                }
            }
            query.addFilterQuery(ObservationDTO.PHENOTYPING_CENTER + ":(" + StringUtils.join(spaceSafeStringsList, " OR ") + ")");
        }

        if (strainParams != null &&  ! strainParams.isEmpty()) {
            query.addFilterQuery(ObservationDTO.STRAIN_ACCESSION_ID + ":(" + StringUtils.join(strainParams, " OR ").replace(":", "\\:") + ")");
        }

        if (metaDataGroups != null &&  ! metaDataGroups.isEmpty()) {
            query.addFilterQuery(ObservationDTO.METADATA_GROUP + ":(" + StringUtils.join(metaDataGroups, " OR ") + ")");
        }

        if (pipelineStableId != null &&  ! pipelineStableId.isEmpty()) {
            query.addFilterQuery(ObservationDTO.PIPELINE_STABLE_ID + ":(" + StringUtils.join(pipelineStableId, " OR ") + ")");
        }

        if (alleleAccessions != null &&  ! alleleAccessions.isEmpty()) {
            String alleleFilter = ObservationDTO.ALLELE_ACCESSION_ID + ":(" + StringUtils.join(alleleAccessions, " OR ").replace(":", "\\:") + ")";
            LOG.debug("alleleFilter=" + alleleFilter);
            query.addFilterQuery(alleleFilter);

        }

        QueryResponse response = solr.query(query);
        LOG.debug("experiment key query=" + query);
        List<FacetField> fflist = response.getFacetFields();

        for (FacetField ff : fflist) {

			// If there are no face results, the values will be null
            // skip this facet field in that case
            // if (ff.getValues() == null) {
            // continue;
            // }
            for (Count count : ff.getValues()) {
                if (map.containsKey(ff.getName())) {
                    map.get(ff.getName()).add(count.getName());
                } else {
                    List<String> newList = new ArrayList<String>();
                    newList.add(count.getName());
                    map.put(ff.getName(), newList);
                }

            }
        }

        LOG.info("experimentKeys=" + map);
        return map;
    }

    /**
     * for testing - not for users
     *
     * @param start
     * @param length
     * @param type
     * @param parameterIds
     * @return
     * @throws URISyntaxException
     * @throws IOException
     * @throws SQLException
     */
    public List<Map<String, String>> getLinksListForStats(Integer start, Integer length, ObservationType type, List<String> parameterIds)
            throws IOException, URISyntaxException, SQLException {

        if (start == null) {
            start = 0;
        }
        if (length == null) {
            length = 100;
        }

        String url = solr.getBaseURL() + "/select?" + "q=" + ObservationDTO.OBSERVATION_TYPE + ":" + type + " AND " + ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":experimental" + "&wt=json&indent=true&start=" + start + "&rows=" + length;

        net.sf.json.JSONObject result = JSONRestUtil.getResults(url);
        JSONArray resultsArray = JSONRestUtil.getDocArray(result);

        List<Map<String, String>> listWithStableId = new ArrayList<>();
        for (int i = 0; i < resultsArray.size(); i ++) {
            Map<String, String> map = new HashMap<>();
            net.sf.json.JSONObject exp = resultsArray.getJSONObject(i);
            String statbleParamId = exp.getString(ObservationDTO.PARAMETER_STABLE_ID);
            String accession = exp.getString(ObservationDTO.GENE_ACCESSION_ID);
            map.put("paramStableId", statbleParamId);
            map.put("accession", accession);
            listWithStableId.add(map);
        }
        return listWithStableId;
    }

    /**
     * construct a query to get all observations for a given combination of
     * pipeline, parameter, gene, zygosity, organisation and strain
     *
     * @param parameterId
     * @param geneAcc
     * @param zygosity
     * @param organisationId
     * @param strain
     * @param sex
     * @return
     * @throws SolrServerException
     */
    public SolrQuery getSolrQueryByParameterGeneAccZygosityOrganisationStrainSex(Integer parameterId, String geneAcc, String zygosity, Integer organisationId, String strain, String sex)
            throws SolrServerException {

        return new SolrQuery().setQuery("((" + ObservationDTO.GENE_ACCESSION_ID + ":" + geneAcc.replace(":", "\\:") + " AND " + ObservationDTO.ZYGOSITY + ":" + zygosity + ") OR " + ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":control) ").addFilterQuery(ObservationDTO.PARAMETER_ID + ":" + parameterId).addFilterQuery(ObservationDTO.PHENOTYPING_CENTER_ID + ":" + organisationId).addFilterQuery(ObservationDTO.STRAIN_ACCESSION_ID + ":" + strain.replace(":", "\\:")).addFilterQuery(ObservationDTO.SEX + ":" + sex).setStart(0).setRows(10000);
    }

    public String getQueryStringByParameterGeneAccZygosityOrganisationStrainSex(Integer parameterId, String geneAcc, String zygosity, Integer organisationId, String strain, SexType sex)
            throws SolrServerException {

        return getSolrQueryByParameterGeneAccZygosityOrganisationStrainSex(parameterId, geneAcc, zygosity, organisationId, strain, sex.name()).toString();

    }


    public List<ObservationDTO> getObservationsByParameterGeneAccZygosityOrganisationStrainSex(Integer parameterId, String gene, String zygosity, Integer organisationId, String strain, SexType sex)
            throws SolrServerException {

        SolrQuery query = getSolrQueryByParameterGeneAccZygosityOrganisationStrainSex(parameterId, gene, zygosity, organisationId, strain, sex.name());

        return solr.query(query).getBeans(ObservationDTO.class);

    }


    /**
     * Return a list of a all data candidates for deletion prior to statistical
     * analysis
     *
     * @return list of maps of results
     * @throws SolrServerException
     */
    public List<Map<String, String>> getDistinctOrganisaionPipelineParameter()
    throws SolrServerException {

        SolrQuery query = new SolrQuery().setQuery("*:*").addFilterQuery(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":experimental").setRows(0).setFacet(true).setFacetMinCount(1).setFacetLimit(-1).addFacetPivotField( // needs
        ObservationDTO.PHENOTYPING_CENTER_ID + "," + ObservationDTO.PIPELINE_ID + "," + ObservationDTO.PARAMETER_ID);

        QueryResponse response = solr.query(query);

        return getFacetPivotResults(response, false);
    }


    /**
     * Return a list of a all data candidates for deletion prior to statistical
     * analysis
     *
     * @return list of maps of results
     * @throws SolrServerException
     */
    public List<Map<String, String>> getDistinctStatisticalCandidates(List<String> phenotypingCenter, List<String> pipelineStableId, List<String> procedureStub, List<String> parameterStableId, List<String> alleleAccessionId)
            throws SolrServerException {

        String pivotFields = ObservationDTO.PHENOTYPING_CENTER_ID + "," + ObservationDTO.PIPELINE_ID + "," + ObservationDTO.PROCEDURE_ID + "," + ObservationDTO.PARAMETER_ID + "," + ObservationDTO.METADATA_GROUP + "," + ObservationDTO.STRAIN_ACCESSION_ID + "," + ObservationDTO.ALLELE_ACCESSION_ID + "," + ObservationDTO.ZYGOSITY + "," + ObservationDTO.OBSERVATION_TYPE;

        SolrQuery query = new SolrQuery().setQuery("*:*").addFilterQuery(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":experimental").setRows(0).setFacet(true).setFacetMinCount(1).setFacetLimit(-1).addFacetPivotField(pivotFields);

        if (phenotypingCenter != null) {
            List<String> toJoin = new ArrayList<>();
            for (String c : phenotypingCenter) {
                toJoin.add(ObservationDTO.PHENOTYPING_CENTER + ":" + c);
            }
            query.addFilterQuery("(" + StringUtils.join(toJoin, " OR ") + ")");
        }

        if (pipelineStableId != null) {
            List<String> toJoin = new ArrayList<>();
            for (String c : pipelineStableId) {
                toJoin.add(ObservationDTO.PIPELINE_STABLE_ID + ":" + c);
            }
            query.addFilterQuery("(" + StringUtils.join(toJoin, " OR ") + ")");
        }

        if (procedureStub != null) {
            List<String> toJoin = new ArrayList<>();
            for (String c : procedureStub) {
                toJoin.add(ObservationDTO.PROCEDURE_STABLE_ID + ":" + c + "*");
            }
            query.addFilterQuery("(" + StringUtils.join(toJoin, " OR ") + ")");
        }

        if (parameterStableId != null) {
            List<String> toJoin = new ArrayList<>();
            for (String c : parameterStableId) {
                toJoin.add(ObservationDTO.PARAMETER_STABLE_ID + ":" + c);
            }
            query.addFilterQuery("(" + StringUtils.join(toJoin, " OR ") + ")");
        }

        if (alleleAccessionId != null) {
            List<String> toJoin = new ArrayList<>();
            for (String c : alleleAccessionId) {
                toJoin.add(ObservationDTO.ALLELE_ACCESSION_ID + ":\"" + c + "\"");
            }
            query.addFilterQuery("(" + StringUtils.join(toJoin, " OR ") + ")");
        }

        QueryResponse response = solr.query(query);

        return getFacetPivotResults(response, false);
    }


    /**
     * Return a list of a all unidimensional data candidates for statistical
     * analysis for a specific procedure
     *
     * @return list of maps of results
     * @throws SolrServerException
     */
    public List<Map<String, String>> getDistinctUnidimensionalOrgPipelineParamStrainZygosityGeneAccessionAlleleAccessionMetadataByProcedure(String procedureStableId)
            throws SolrServerException {

        SolrQuery query = new SolrQuery().setQuery(ObservationDTO.PROCEDURE_STABLE_ID + ":" + procedureStableId).addFilterQuery(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":experimental").addFilterQuery(ObservationDTO.OBSERVATION_TYPE + ":unidimensional").setRows(0).setFacet(true).setFacetMinCount(1).setFacetLimit(-1).addFacetPivotField( // needs
                // at
                // least
                // 2
                // fields
                ObservationDTO.PHENOTYPING_CENTER_ID + "," + ObservationDTO.PIPELINE_ID + "," + ObservationDTO.PARAMETER_ID + "," + ObservationDTO.STRAIN_ACCESSION_ID + "," + ObservationDTO.ZYGOSITY + "," + ObservationDTO.METADATA_GROUP + "," + ObservationDTO.ALLELE_ACCESSION_ID + "," + ObservationDTO.GENE_ACCESSION_ID);

        QueryResponse response = solr.query(query);

        return getFacetPivotResults(response, false);

    }


    /**
     * Return a list of a all unidimensional data candidates for statistical
     * analysis for all specified procedures
     *
     * @return list of maps of results
     * @throws SolrServerException
     */
    public List<Map<String, String>> getDistinctUnidimensionalOrgPipelineParamStrainZygosityGeneAccessionAlleleAccessionMetadataByProcedure(List<String> procedureStableIds)
    throws SolrServerException {

        // Build the SOLR query string
        String field = ObservationDTO.PROCEDURE_STABLE_ID;
        String q = (procedureStableIds.size() > 1) ? "(" + field + ":\"" + StringUtils.join(procedureStableIds.toArray(), "\" OR " + field + ":\"") + "\")" : field + ":\"" + procedureStableIds.get(0) + "\"";

        SolrQuery query = new SolrQuery().setQuery(q).addFilterQuery(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":experimental").addFilterQuery(ObservationDTO.OBSERVATION_TYPE + ":unidimensional").setRows(0).setFacet(true).setFacetMinCount(1).setFacetLimit(-1).addFacetPivotField( // needs
                // at
                // least
                // 2
                // fields
                ObservationDTO.PHENOTYPING_CENTER_ID + "," + ObservationDTO.PIPELINE_ID + "," + ObservationDTO.PARAMETER_ID + "," + ObservationDTO.STRAIN_ACCESSION_ID + "," + ObservationDTO.ZYGOSITY + "," + ObservationDTO.METADATA_GROUP + "," + ObservationDTO.ALLELE_ACCESSION_ID + "," + ObservationDTO.GENE_ACCESSION_ID);

        QueryResponse response = solr.query(query);

        return getFacetPivotResults(response, false);

    }

    /**
     * Return a list of a all unidimensional data candidates for statistical
     * analysis for all specified parameter
     *
     * @return list of maps of results
     * @throws SolrServerException
     */
    public List<Map<String, String>> getDistinctUnidimensionalOrgPipelineParamStrainZygosityGeneAccessionAlleleAccessionMetadataByParameter(List<String> parameterStableIds)
    throws SolrServerException {

        // Build the SOLR query string
        String field = ObservationDTO.PARAMETER_STABLE_ID;
        String q = (parameterStableIds.size() > 1) ? "(" + field + ":\"" + StringUtils.join(parameterStableIds.toArray(), "\" OR " + field + ":\"") + "\")" : field + ":\"" + parameterStableIds.get(0) + "\"";

        SolrQuery query = new SolrQuery().setQuery(q).addFilterQuery(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":experimental").addFilterQuery(ObservationDTO.OBSERVATION_TYPE + ":unidimensional").setRows(0).setFacet(true).setFacetMinCount(1).setFacetLimit(-1).addFacetPivotField( // needs
                // at
                // least
                // 2
                // fields
                ObservationDTO.PHENOTYPING_CENTER_ID + "," + ObservationDTO.PIPELINE_ID + "," + ObservationDTO.PARAMETER_ID + "," + ObservationDTO.STRAIN_ACCESSION_ID + "," + ObservationDTO.ZYGOSITY + "," + ObservationDTO.METADATA_GROUP + "," + ObservationDTO.ALLELE_ACCESSION_ID + "," + ObservationDTO.GENE_ACCESSION_ID);

        QueryResponse response = solr.query(query);

        return getFacetPivotResults(response, false);

    }


    /**
     * Return a list of a all unidimensional data candidates for statistical
     * analysis for a specific parameter
     *
     * @return list of maps of results
     * @throws SolrServerException
     */
    public List<Map<String, String>> getDistinctUnidimensionalOrgPipelineParamStrainZygosityGeneAccessionAlleleAccessionMetadataByParameter(String parameterStableId)
    throws SolrServerException {

        SolrQuery query = new SolrQuery().setQuery(ObservationDTO.PARAMETER_STABLE_ID + ":" + parameterStableId).addFilterQuery(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":experimental").addFilterQuery(ObservationDTO.OBSERVATION_TYPE + ":unidimensional").setRows(0).setFacet(true).setFacetMinCount(1).setFacetLimit(-1).addFacetPivotField( // needs
                // at
                // least
                // 2
                // fields
                ObservationDTO.PHENOTYPING_CENTER_ID + "," + ObservationDTO.PIPELINE_ID + "," + ObservationDTO.PARAMETER_ID + "," + ObservationDTO.STRAIN_ACCESSION_ID + "," + ObservationDTO.ZYGOSITY + "," + ObservationDTO.METADATA_GROUP + "," + ObservationDTO.ALLELE_ACCESSION_ID + "," + ObservationDTO.GENE_ACCESSION_ID);

        QueryResponse response = solr.query(query);

        return getFacetPivotResults(response, false);

    }


    /**
     * Return a list of a all unidimensional data candidates for statistical
     * analysis
     *
     * @return list of maps of results
     * @throws SolrServerException
     */
    public List<Map<String, String>> getDistinctUnidimensionalOrgPipelineParamStrainZygosityGeneAccessionAlleleAccessionMetadata()
    throws SolrServerException {

        List<Map<String, String>> candidates = new ArrayList<>();

        SolrQuery centersQuery = new SolrQuery()
            .setQuery("*:*")
            .addFilterQuery(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":experimental")
            .addFilterQuery(ObservationDTO.OBSERVATION_TYPE + ":unidimensional")
            .setRows(0)
            .setFacet(true)
            .setFacetMinCount(1)
            .setFacetLimit(-1)
            .addFacetField(ObservationDTO.PROCEDURE_GROUP);

        System.out.println(solr.getBaseURL() + "/select?"+centersQuery);
        System.out.println("\n");

        QueryResponse centerResponse = solr.query(centersQuery);
        List<FacetField> candidateSubsets = centerResponse.getFacetFields();

        for (FacetField ff : candidateSubsets) {

            // If there are no face results, the values will be null
            // skip this facet field in that case
            if (ff.getValues() == null) {
                continue;
            }

            for (Count c : ff.getValues()) {
                String candidateSubset = c.getName();
                SolrQuery query = new SolrQuery()
                    .setQuery("*:*")
                    .addFilterQuery(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":experimental")
                    .addFilterQuery(ObservationDTO.OBSERVATION_TYPE + ":unidimensional")
                    .addFilterQuery(ObservationDTO.PROCEDURE_GROUP + ":" + candidateSubset)
                    .setRows(0)
                    .setFacet(true)
                    .setFacetMinCount(1)
                    .setFacetLimit(-1)
                    .addFacetPivotField(StringUtils.join(Arrays.asList(ObservationDTO.PIPELINE_ID, ObservationDTO.PARAMETER_ID, ObservationDTO.STRAIN_ACCESSION_ID, ObservationDTO.ZYGOSITY, ObservationDTO.METADATA_GROUP, ObservationDTO.ALLELE_ACCESSION_ID, ObservationDTO.GENE_ACCESSION_ID), ","));

                System.out.println(solr.getBaseURL() + "/select?"+query);

                QueryResponse response = solr.query(query);

                List<Map<String, String>> centerCandidates = getFacetPivotResults(response, false);
                for (Map<String, String> centerCandidate : centerCandidates) {
                    centerCandidate.put(ObservationDTO.PROCEDURE_GROUP, candidateSubset);
                }

                candidates.addAll(centerCandidates);
            }
        }


        return candidates;

    }


    /**
     * Return a list of a all data candidates for statistical analysis
     *
     * @return list of maps of results
     * @throws SolrServerException
     */
    public List<Map<String, String>> getDistinctCategoricalOrgPipelineParamStrainZygositySexGeneAccessionAlleleAccessionMetadata()
    throws SolrServerException {

        List<String> pivotFields = Arrays.asList(ObservationDTO.PHENOTYPING_CENTER_ID, ObservationDTO.PIPELINE_ID, ObservationDTO.PROCEDURE_GROUP, ObservationDTO.PARAMETER_ID, ObservationDTO.STRAIN_ACCESSION_ID, ObservationDTO.ZYGOSITY, ObservationDTO.SEX, ObservationDTO.METADATA_GROUP, ObservationDTO.ALLELE_ACCESSION_ID, ObservationDTO.GENE_ACCESSION_ID);

        SolrQuery query = new SolrQuery()
                .setQuery("*:*")
                .addFilterQuery(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":experimental")
                .addFilterQuery(ObservationDTO.OBSERVATION_TYPE + ":categorical")
                .setRows(0)
                .setFacet(true)
                .setFacetMinCount(1)
                .setFacetLimit(-1)
                .addFacetPivotField(StringUtils.join(pivotFields, ","));

        QueryResponse response = solr.query(query);
        LOG.debug(" getDistinctCategoricalOrgPipelineParamStrainZygositySexGeneAccessionAlleleAccessionMetadata: Solr query - {}", query.toString());
        LOG.debug(" getDistinctCategoricalOrgPipelineParamStrainZygositySexGeneAccessionAlleleAccessionMetadata: Num Solr documents - {}", response.getResults().getNumFound());

        return getFacetPivotResults(response, false);

    }


    public List<Map<String, String>> getDistinctCategoricalOrgPipelineParamStrainZygositySexGeneAccessionAlleleAccessionMetadataByParameter(String parameterStableId)
    throws SolrServerException {

        List<String> pivotFields = Arrays.asList(ObservationDTO.PHENOTYPING_CENTER_ID, ObservationDTO.PIPELINE_ID, ObservationDTO.PARAMETER_ID, ObservationDTO.STRAIN_ACCESSION_ID, ObservationDTO.ZYGOSITY, ObservationDTO.SEX, ObservationDTO.METADATA_GROUP, ObservationDTO.ALLELE_ACCESSION_ID, ObservationDTO.GENE_ACCESSION_ID);

        SolrQuery query = new SolrQuery()
                .setQuery(ObservationDTO.PARAMETER_STABLE_ID + ":" + parameterStableId)
                .addFilterQuery(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":experimental")
                .addFilterQuery(ObservationDTO.OBSERVATION_TYPE + ":categorical")
                .setRows(0)
                .setFacet(true)
                .setFacetMinCount(1)
                .setFacetLimit(-1)
                .addFacetPivotField(StringUtils.join(pivotFields, ","));

        QueryResponse response = solr.query(query);

        return getFacetPivotResults(response, false);

    }

    public List<ObservationDTO> getExperimentObservationsBy(Integer parameterId, Integer pipelineId, String gene, List<String> zygosities, Integer organisationId, String strain, SexType sex, String metaDataGroup, String alleleAccession)
    throws SolrServerException {

        List<ObservationDTO> resultsDTO;
        SolrQuery query = new SolrQuery()
                .setQuery(ObservationDTO.GENE_ACCESSION_ID + ":" + gene.replace(":", "\\:"))
                .addFilterQuery(ObservationDTO.PARAMETER_ID + ":" + parameterId)
                .setStart(0)
                .setRows(10000);

        if (pipelineId != null) {
            query.addFilterQuery(ObservationDTO.PIPELINE_ID + ":" + pipelineId);
        }

        if (zygosities != null && zygosities.size() > 0 && zygosities.size() != 3) {
            if (zygosities.size() == 2) {
                query.addFilterQuery(ObservationDTO.ZYGOSITY + ":(" + zygosities.get(0) + " OR " + zygosities.get(1) + ")");
            } else {
                if ( ! zygosities.get(0).equalsIgnoreCase("null")) {
                    query.addFilterQuery(ObservationDTO.ZYGOSITY + ":" + zygosities.get(0));
                }
            }
        }
        if (strain != null) {
            query.addFilterQuery(ObservationDTO.STRAIN_ACCESSION_ID + ":" + strain.replace(":", "\\:"));
        }
        if (organisationId != null) {
            query.addFilterQuery(ObservationDTO.PHENOTYPING_CENTER_ID + ":" + organisationId);
        }
        if (sex != null) {
            query.addFilterQuery(ObservationDTO.SEX + ":" + sex);
        }
        if (metaDataGroup != null) {
            query.addFilterQuery(ObservationDTO.METADATA_GROUP + ":\"" + metaDataGroup + "\"");
        }
        if (alleleAccession != null) {
            query.addFilterQuery(ObservationDTO.ALLELE_ACCESSION_ID + ":" + alleleAccession.replace(":", "\\:"));
        }

        //System.out.println("get experiment :: " + solr.getBaseURL() + "/select?" + query );

        QueryResponse response = solr.query(query);
        resultsDTO = response.getBeans(ObservationDTO.class);

        System.out.println("Solr URL for getExperimentObservationsBy " + solr.getBaseURL() + "/select?" + query);
        
        return resultsDTO;
    }


    public List<ObservationDTO> getViabilityData(String parameterStableId, Integer pipelineId, String gene, List<String> zygosities, Integer organisationId, String strain, SexType sex, String metaDataGroup, String alleleAccession)
    throws SolrServerException {

        List<ObservationDTO> resultsDTO;
        SolrQuery query = new SolrQuery()
                .setQuery(ObservationDTO.GENE_ACCESSION_ID + ":" + gene.replace(":", "\\:"))
                .addFilterQuery(ObservationDTO.PARAMETER_STABLE_ID + ":" + parameterStableId)
                .setStart(0)
                .setRows(10000);

        if (pipelineId != null) {
            query.addFilterQuery(ObservationDTO.PIPELINE_ID + ":" + pipelineId);
        }

        if (zygosities != null && zygosities.size() > 0 && zygosities.size() != 3) {
            if (zygosities.size() == 2) {
                query.addFilterQuery(ObservationDTO.ZYGOSITY + ":(" + zygosities.get(0) + " OR " + zygosities.get(1) + ")");
            } else {
                if ( ! zygosities.get(0).equalsIgnoreCase("null")) {
                    query.addFilterQuery(ObservationDTO.ZYGOSITY + ":" + zygosities.get(0));
                }
            }

        }
        if (strain != null) {
            query.addFilterQuery(ObservationDTO.STRAIN_ACCESSION_ID + ":" + strain.replace(":", "\\:"));
        }
        if (organisationId != null) {
            query.addFilterQuery(ObservationDTO.PHENOTYPING_CENTER_ID + ":" + organisationId);
        }
        if (sex != null) {
            query.addFilterQuery(ObservationDTO.SEX + ":" + sex);
        }
        if (metaDataGroup != null) {
            query.addFilterQuery(ObservationDTO.METADATA_GROUP + ":\"" + metaDataGroup + "\"");
        }
        if (alleleAccession != null) {
            query.addFilterQuery(ObservationDTO.ALLELE_ACCESSION_ID + ":" + alleleAccession.replace(":", "\\:"));
        }

        QueryResponse response = solr.query(query);
        resultsDTO = response.getBeans(ObservationDTO.class);
        return resultsDTO;
    }


    /**
     * Return a list of a triplets of pipeline stable id, phenotyping center and
     * allele accession
     *
     *
     * @param genomicFeatureAcc a gene accession
     * @return list of triplets
     * @throws SolrServerException
     */
    public List<Map<String, String>> getDistinctPipelineAlleleCenterListByGeneAccession(String genomicFeatureAcc)
    throws SolrServerException {

        List<Map<String, String>> results = new LinkedList<>();
        List<String> facetFields = Arrays.asList(ObservationDTO.PIPELINE_STABLE_ID, ObservationDTO.PIPELINE_NAME, ObservationDTO.PHENOTYPING_CENTER, ObservationDTO.ALLELE_ACCESSION_ID, ObservationDTO.ALLELE_SYMBOL);

        SolrQuery query = new SolrQuery().setQuery("*:*")
                .addFilterQuery(ObservationDTO.GENE_ACCESSION_ID + ":" + "\"" + genomicFeatureAcc + "\"")
                .addFilterQuery(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":experimental")
                .setRows(0)
                .setFacet(true)
                .setFacetMinCount(1)
                .setFacetLimit(-1)
                .addFacetPivotField(StringUtils.join(facetFields, ","));

        QueryResponse response = solr.query(query);

        NamedList<List<PivotField>> facetPivot = response.getFacetPivot();

        if (facetPivot != null && facetPivot.size() > 0) {
            for (int i = 0; i < facetPivot.size(); i ++) {

                String name = facetPivot.getName(i); // in this case only one of
                // them
                LOG.debug("facetPivot name" + name);
                List<PivotField> pivotResult = facetPivot.get(name);

                // iterate on results
                for (int j = 0; j < pivotResult.size(); j ++) {

					// create a HashMap to store a new triplet of data
                    PivotField pivotLevel = pivotResult.get(j);
                    List<Map<String, String>> lmap = getLeveledFacetPivotValue(pivotLevel, null, false);
                    results.addAll(lmap);
                }
            }
        }

        return results;
    }


    /**
     * Return a list of parameters measured for a particular pipeline, allele
     * and center combination. A list of filters (meaning restriction to some
     * specific procedures is passed).
     *
     * @param alleleAccession an allele accession
     * @return list of tripels
     * @throws SolrServerException
     */
    public List<Map<String, String>> getDistinctParameterListByPipelineAlleleCenter(String pipelineStableId, String alleleAccession, String phenotypingCenter, List<String> procedureFilters, ArrayList<String> resource)
    throws SolrServerException {

        SolrQuery query = new SolrQuery()
                .setQuery("*:*")
                .addFilterQuery(ObservationDTO.PIPELINE_STABLE_ID + ":" + pipelineStableId)
                .addFilterQuery(ObservationDTO.PHENOTYPING_CENTER + ":\"" + phenotypingCenter + "\"")
                .addFilterQuery(ObservationDTO.ALLELE_ACCESSION_ID + ":\"" + alleleAccession + "\"");
        if (resource != null) {
            query.addFilterQuery("(" + ObservationDTO.DATASOURCE_NAME + ":" + StringUtils.join(resource, " OR " + ObservationDTO.DATASOURCE_NAME + ":") + ")");
        }

        int index = 0;
        if (procedureFilters != null && procedureFilters.size() > 0) {
            StringBuilder queryBuilder = new StringBuilder(ObservationDTO.PROCEDURE_STABLE_ID + ":(");

            for (String procedureFilter : procedureFilters) {
                if (index == 0) {
                    queryBuilder.append(procedureFilter);
                } else {
                    queryBuilder.append(" OR " + procedureFilter);
                }
                index ++;
            }
            queryBuilder.append(")");
            query.addFilterQuery(queryBuilder.toString());
        }

        query.setRows(0).setFacet(true).setFacetMinCount(1).setFacetLimit(-1).addFacetPivotField(ObservationDTO.PROCEDURE_STABLE_ID
                + "," + ObservationDTO.PROCEDURE_NAME + "," + ObservationDTO.PARAMETER_STABLE_ID + "," + ObservationDTO.PARAMETER_NAME
                + "," + ObservationDTO.OBSERVATION_TYPE + "," + ObservationDTO.ZYGOSITY);

        System.out.println(solr.getBaseURL() + "/select?" + query.toString());
        QueryResponse response = solr.query(query);
        NamedList<List<PivotField>> facetPivot = response.getFacetPivot();
        List<Map<String, String>> results = new LinkedList<Map<String, String>>();

        if (facetPivot != null && facetPivot.size() > 0) {

            for (int i = 0; i < facetPivot.size(); i ++) {

                String name = facetPivot.getName(i);
                List<PivotField> pivotResult = facetPivot.get(name);

                for (int j = 0; j < pivotResult.size(); j ++) {

                    PivotField pivotLevel = pivotResult.get(j);
                    List<Map<String, String>> lmap = getLeveledFacetPivotValue(pivotLevel, null, false);
                    results.addAll(lmap);
                }

            }
        }

        return results;
    }


    /**
     * Return a list of procedures effectively performed given pipeline stable
     * id, phenotyping center and allele accession
     *
     * @param alleleAccession an allele accession
     * @return list of integer db keys of the parameter rows
     * @throws SolrServerException
     */
    public List<String> getDistinctProcedureListByPipelineAlleleCenter(String pipelineStableId, String alleleAccession, String phenotypingCenter)
    throws SolrServerException {

        List<String> results = new LinkedList<String>();

        SolrQuery query = new SolrQuery().setQuery("*:*").addFilterQuery(ObservationDTO.PIPELINE_STABLE_ID + ":" + pipelineStableId).addFilterQuery(ObservationDTO.PHENOTYPING_CENTER + ":" + phenotypingCenter).addFilterQuery(ObservationDTO.ALLELE_ACCESSION_ID + ":\"" + alleleAccession + "\"").setRows(0).setFacet(true).setFacetMinCount(1).setFacetLimit(-1).addFacetField(ObservationDTO.PROCEDURE_STABLE_ID);

        QueryResponse response = solr.query(query);
        List<FacetField> fflist = response.getFacetFields();

        for (FacetField ff : fflist) {

			// If there are no face results, the values will be null
            // skip this facet field in that case
            if (ff.getValues() == null) {
                continue;
            }

            for (Count c : ff.getValues()) {
                results.add(c.getName());
            }
        }

        return results;
    }


    // gets categorical data for graphs on phenotype page
    public Map<String, List<DiscreteTimePoint>> getTimeSeriesMutantData(String parameter, List<String> genes, ArrayList<String> strains, String[] center, String[] sex)
    throws SolrServerException {

        Map<String, List<DiscreteTimePoint>> finalRes = new HashMap<String, List<DiscreteTimePoint>>(); // <allele_accession,
        // timeSeriesData>

        SolrQuery query = new SolrQuery().addFilterQuery(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":experimental").addFilterQuery(ObservationDTO.PARAMETER_STABLE_ID + ":" + parameter);

        String q = (strains.size() > 1) ? "(" + ObservationDTO.STRAIN_ACCESSION_ID + ":\"" + StringUtils.join(strains.toArray(), "\" OR " + ObservationDTO.STRAIN_ACCESSION_ID + ":\"") + "\")" : ObservationDTO.STRAIN_ACCESSION_ID + ":\"" + strains.get(0) + "\"";

        if (genes != null && genes.size() > 0) {
            q += " AND (";
            q += (genes.size() > 1) ? ObservationDTO.GENE_ACCESSION_ID + ":\"" + StringUtils.join(genes.toArray(), "\" OR " + ObservationDTO.GENE_ACCESSION_ID + ":\"") + "\"" : ObservationDTO.GENE_ACCESSION_ID + ":\"" + genes.get(0) + "\"";
            q += ")";
        }

        if (center != null && center.length > 0) {
            q += " AND (";
            q += (center.length > 1) ? ObservationDTO.PHENOTYPING_CENTER + ":\"" + StringUtils.join(center, "\" OR " + ObservationDTO.PHENOTYPING_CENTER + ":\"") + "\"" : ObservationDTO.PHENOTYPING_CENTER + ":\"" + center[0] + "\"";
            q += ")";
        }

        if (sex != null && sex.length == 1) {
            q += " AND " + ObservationDTO.SEX + ":\"" + sex[0] + "\"";
        }

        query.setQuery(q);
        query.set("group.field", ObservationDTO.GENE_SYMBOL);
        query.set("group", true);
        query.set("fl", ObservationDTO.DATA_POINT + "," + ObservationDTO.DISCRETE_POINT);
        query.set("group.limit", 100000); // number of documents to be returned
        // per group
        query.set("group.sort", ObservationDTO.DISCRETE_POINT + " asc");
        query.setRows(10000);

		// System.out.println("+_+_+ " + solr.getBaseURL() + "/select?" +
        // query);
        List<Group> groups = solr.query(query).getGroupResponse().getValues().get(0).getValues();
		// for mutants it doesn't seem we need binning
        // groups are the alleles
        for (Group gr : groups) {
            SolrDocumentList resDocs = gr.getResult();
            DescriptiveStatistics stats = new DescriptiveStatistics();
            float discreteTime = (float) resDocs.get(0).getFieldValue(ObservationDTO.DISCRETE_POINT);
            ArrayList<DiscreteTimePoint> res = new ArrayList<DiscreteTimePoint>();
            for (int i = 0; i < resDocs.getNumFound(); i ++) {
                SolrDocument doc = resDocs.get(i);
                stats.addValue((float) doc.getFieldValue(ObservationDTO.DATA_POINT));
                if (discreteTime != (float) doc.getFieldValue(ObservationDTO.DISCRETE_POINT) || i == resDocs.getNumFound() - 1) { // we
                    // are
                    // at
                    // the
                    // end
                    // of
                    // the
                    // document
                    // list
                    // add to list
                    float discreteDataPoint = (float) stats.getMean();
                    DiscreteTimePoint dp = new DiscreteTimePoint(discreteTime, discreteDataPoint, new Float(stats.getStandardDeviation()));
                    List<Float> errorPair = new ArrayList<>();
                    Float lower = new Float(discreteDataPoint);
                    Float higher = new Float(discreteDataPoint);
                    errorPair.add(lower);
                    errorPair.add(higher);
                    dp.setErrorPair(errorPair);
                    res.add(dp);
                    // update discrete point
                    discreteTime = Float.valueOf(doc.getFieldValue(ObservationDTO.DISCRETE_POINT).toString());
                    // update stats
                    stats = new DescriptiveStatistics();
                }
            }
            // add list
            finalRes.put(gr.getGroupValue(), res);
        }
        return finalRes;
    }


    // gets categorical data for graphs on phenotype page
    public List<DiscreteTimePoint> getTimeSeriesControlData(String parameter, ArrayList<String> strains, String[] center, String[] sex)
    throws SolrServerException {

        ArrayList<DiscreteTimePoint> res = new ArrayList<DiscreteTimePoint>();
        SolrQuery query = new SolrQuery().addFilterQuery(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":control").addFilterQuery(ObservationDTO.PARAMETER_STABLE_ID + ":" + parameter);
        String q = (strains.size() > 1) ? "(" + ObservationDTO.STRAIN_ACCESSION_ID + ":\"" + StringUtils.join(strains.toArray(), "\" OR " + ObservationDTO.STRAIN_ACCESSION_ID + ":\"") + "\")" : ObservationDTO.STRAIN_ACCESSION_ID + ":\"" + strains.get(0) + "\"";

        if (center != null && center.length > 0) {
            q += " AND (";
            q += (center.length > 1) ? ObservationDTO.PHENOTYPING_CENTER + ":\"" + StringUtils.join(center, "\" OR " + ObservationDTO.PHENOTYPING_CENTER + ":\"") + "\"" : ObservationDTO.PHENOTYPING_CENTER + ":\"" + center[0] + "\"";
            q += ")";
        }

        if (sex != null && sex.length == 1) {
            q += " AND " + ObservationDTO.SEX + ":\"" + sex[0] + "\"";
        }

        query.setQuery(q);
        query.set("group.field", ObservationDTO.DISCRETE_POINT);
        query.set("group", true);
        query.set("fl", ObservationDTO.DATA_POINT + "," + ObservationDTO.DISCRETE_POINT);
        query.set("group.limit", 100000); // number of documents to be returned
        // per group
        query.set("sort", ObservationDTO.DISCRETE_POINT + " asc");
        query.setRows(10000);

		// System.out.println("+_+_+ " + solr.getBaseURL() + "/select?" +
        // query);
        List<Group> groups = solr.query(query).getGroupResponse().getValues().get(0).getValues();
        boolean rounding = false;
		// decide if binning is needed i.e. is the increment points are too
        // scattered, as for calorimetry
        if (groups.size() > 30) { // arbitrary value, just piced it because it
            // seems reasonable for the size of our
            // graphs
            if (Float.valueOf(groups.get(groups.size() - 1).getGroupValue()) - Float.valueOf(groups.get(0).getGroupValue()) <= 30) { // then
                // rounding
                // will
                // be
                // enough
                rounding = true;
            }
        }
        if (rounding) {
            int bin = Math.round(Float.valueOf(groups.get(0).getGroupValue()));
            for (Group gr : groups) {
                int discreteTime = Math.round(Float.valueOf(gr.getGroupValue()));
                // for calormetry ignore what's before -5 and after 16
                if (parameter.startsWith("IMPC_CAL") || parameter.startsWith("ESLIM_003_001") || parameter.startsWith("M-G-P_003_001")) {
                    if (discreteTime < -5) {
                        continue;
                    } else if (discreteTime > 16) {
                        break;
                    }
                }
                float sum = 0;
                SolrDocumentList resDocs = gr.getResult();
                DescriptiveStatistics stats = new DescriptiveStatistics();
                for (SolrDocument doc : resDocs) {
                    sum += (float) doc.getFieldValue(ObservationDTO.DATA_POINT);
                    stats.addValue((float) doc.getFieldValue(ObservationDTO.DATA_POINT));
                }
                if (bin < discreteTime || groups.indexOf(gr) == groups.size() - 1) { // finished
                    // the
                    // groups
                    // of
                    // filled
                    // the
                    // bin
                    float discreteDataPoint = sum / resDocs.getNumFound();
                    DiscreteTimePoint dp = new DiscreteTimePoint((float) discreteTime, discreteDataPoint, new Float(stats.getStandardDeviation()));
                    List<Float> errorPair = new ArrayList<>();
                    double std = stats.getStandardDeviation();
                    Float lower = new Float(discreteDataPoint - std);
                    Float higher = new Float(discreteDataPoint + std);
                    errorPair.add(lower);
                    errorPair.add(higher);
                    dp.setErrorPair(errorPair);
                    res.add(dp);
                    bin = discreteTime;
                }
            }
        } else {
            for (Group gr : groups) {
                Float discreteTime = Float.valueOf(gr.getGroupValue());
                float sum = 0;
                SolrDocumentList resDocs = gr.getResult();
                DescriptiveStatistics stats = new DescriptiveStatistics();
                for (SolrDocument doc : resDocs) {
                    sum += (float) doc.getFieldValue(ObservationDTO.DATA_POINT);
                    stats.addValue((float) doc.getFieldValue(ObservationDTO.DATA_POINT));
                }
                float discreteDataPoint = sum / resDocs.getNumFound();
                DiscreteTimePoint dp = new DiscreteTimePoint(discreteTime, discreteDataPoint, new Float(stats.getStandardDeviation()));
                List<Float> errorPair = new ArrayList<>();
                double std = stats.getStandardDeviation();
                Float lower = new Float(discreteDataPoint - std);
                Float higher = new Float(discreteDataPoint + std);
                errorPair.add(lower);
                errorPair.add(higher);
                dp.setErrorPair(errorPair);
                res.add(dp);
            }
        }
        return res;
    }



    /**
     *
     * @param p
     * @param genes
     * @param strains
     * @param biologicalSample
     * @return list of centers and sexes for the given parameters
     * @throws SolrServerException
     */
    public Set<String> getCenters(Parameter p, List<String> genes, ArrayList<String> strains, String biologicalSample)
    throws SolrServerException {

        Set<String> centers = new HashSet<String>();
        SolrQuery query = new SolrQuery().addFilterQuery(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":" + biologicalSample).addFilterQuery(ObservationDTO.PARAMETER_STABLE_ID + ":" + p.getStableId());
        String q = (strains.size() > 1) ? "(" + ObservationDTO.STRAIN_ACCESSION_ID + ":\"" + StringUtils.join(strains.toArray(), "\" OR " + ObservationDTO.STRAIN_ACCESSION_ID + ":\"") + "\")" : ObservationDTO.STRAIN_ACCESSION_ID + ":\"" + strains.get(0) + "\"";
        String fq = "";
        if (genes != null && genes.size() > 0) {
            fq += " (";
            fq += (genes.size() > 1) ? ObservationDTO.GENE_ACCESSION_ID + ":\"" + StringUtils.join(genes.toArray(), "\" OR " + ObservationDTO.GENE_ACCESSION_ID + ":\"") + "\"" : ObservationDTO.GENE_ACCESSION_ID + ":\"" + genes.get(0) + "\"";
            fq += ")";
        }
        query.addFilterQuery(fq);
        query.setQuery(q);
        query.setRows(100000000);
        query.setFields(ObservationDTO.GENE_ACCESSION_ID, ObservationDTO.DATA_POINT);
        query.set("group", true);
        query.set("group.field", ObservationDTO.PHENOTYPING_CENTER);

        List<Group> groups = solr.query(query, METHOD.POST).getGroupResponse().getValues().get(0).getValues();
        for (Group gr : groups) {
            centers.add((String) gr.getGroupValue());
        }

        System.out.println("CENTERS ::: " + centers);
        return centers;
    }


    public double getMeanPValue(Parameter p, ArrayList<String> strains, String biologicalSample, String[] center, SexType sex)
    throws SolrServerException {

        System.out.println("GETTING THE MEAN");
        SolrQuery query = new SolrQuery().addFilterQuery(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":" + biologicalSample).addFilterQuery(ObservationDTO.PARAMETER_STABLE_ID + ":" + p.getStableId());
        String q = (strains.size() > 1) ? "(" + ObservationDTO.STRAIN_ACCESSION_ID + ":\"" + StringUtils.join(strains.toArray(), "\" OR " + ObservationDTO.STRAIN_ACCESSION_ID + ":\"") + "\")" : ObservationDTO.STRAIN_ACCESSION_ID + ":\"" + strains.get(0) + "\"";
        double mean = 0;

        if (center != null && center.length > 0) {
            q += " AND (";
            q += (center.length > 1) ? ObservationDTO.PHENOTYPING_CENTER + ":\"" + StringUtils.join(center, "\" OR " + ObservationDTO.PHENOTYPING_CENTER + ":\"") + "\"" : ObservationDTO.PHENOTYPING_CENTER + ":\"" + center[0] + "\"";
            q += ")";
        }

        if (sex != null) {
            q += " AND " + ObservationDTO.SEX + ":\"" + sex.getName() + "\"";
        }

        query.setQuery(q);
        query.setRows(0);
        query.set("stats", true);
        query.set("stats.field", ObservationDTO.DATA_POINT);
        query.set("omitHeader", true);
        query.set("wt", "json");

        try {
            JSONObject response = JSONRestUtil.getResults(solr.getBaseURL() + "/select?" + query);
            mean = response.getJSONObject("stats").getJSONObject("stats_fields").getJSONObject("data_point").getDouble("mean");
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return mean;
    }

    // gets categorical data for graphs on phenotype page
    public CategoricalSet getCategories(Parameter parameter, ArrayList<String> genes, String biologicalSampleGroup, ArrayList<String> strains, String[] center, String[] sex)
    throws SolrServerException, SQLException {

        CategoricalSet resSet = new CategoricalSet();
        resSet.setName(biologicalSampleGroup);
        SolrQuery query = new SolrQuery().addFilterQuery(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":" + biologicalSampleGroup).addFilterQuery(ObservationDTO.PARAMETER_STABLE_ID + ":" + parameter.getStableId());

        String q = (strains.size() > 1) ? "(" + ObservationDTO.STRAIN_ACCESSION_ID + ":\"" + StringUtils.join(strains.toArray(), "\" OR " + ObservationDTO.STRAIN_ACCESSION_ID + ":\"") + "\")" : ObservationDTO.STRAIN_ACCESSION_ID + ":\"" + strains.get(0) + "\"";

        if (genes != null && genes.size() > 0) {
            q += " AND (";
            q += (genes.size() > 1) ? ObservationDTO.GENE_ACCESSION_ID + ":\"" + StringUtils.join(genes.toArray(), "\" OR " + ObservationDTO.GENE_ACCESSION_ID + ":\"") + "\"" : ObservationDTO.GENE_ACCESSION_ID + ":\"" + genes.get(0) + "\"";
            q += ")";
        }

        if (center != null && center.length > 0) {
            q += " AND (";
            q += (center.length > 1) ? ObservationDTO.PHENOTYPING_CENTER + ":\"" + StringUtils.join(center, "\" OR " + ObservationDTO.PHENOTYPING_CENTER + ":\"") + "\"" : ObservationDTO.PHENOTYPING_CENTER + ":\"" + center[0] + "\"";
            q += ")";
        }

        if (sex != null && sex.length == 1) {
            q += " AND " + ObservationDTO.SEX + ":\"" + sex[0] + "\"";
        }

        query.setQuery(q);
        query.set("group.field", ObservationDTO.CATEGORY);
        query.set("group", true);
        query.setRows(100);

        List<String> categories = new ArrayList<String>();
        List<Group> groups = solr.query(query).getGroupResponse().getValues().get(0).getValues();
        for (Group gr : groups) {
            categories.add((String) gr.getGroupValue());
            CategoricalDataObject catObj = new CategoricalDataObject();
            catObj.setCount((long) gr.getResult().getNumFound());
            String catLabel = parameterDAO.getCategoryDescription(parameter.getId(), gr.getGroupValue());
            catObj.setCategory(catLabel);
            resSet.add(catObj);
        }
        return resSet;
    }


    public ObservationType getObservationTypeForParameterStableId(String paramStableId)
    throws SolrServerException {

        SolrQuery q = new SolrQuery().setQuery(ObservationDTO.PARAMETER_STABLE_ID + ":" + paramStableId);
        q.set("rows", 1);
        QueryResponse response = solr.query(q);
        String type = (String) response.getResults().get(0).getFieldValue(ObservationDTO.OBSERVATION_TYPE);

        if (type.equalsIgnoreCase(ObservationType.unidimensional.toString())) {
            return ObservationType.unidimensional;
        }

        if (type.equalsIgnoreCase(ObservationType.categorical.toString())) {
            return ObservationType.categorical;
        }

        if (type.equalsIgnoreCase(ObservationType.time_series.toString())) {
            return ObservationType.time_series;
        }

        if (type.equalsIgnoreCase(ObservationType.image_record.toString())) {
            return ObservationType.image_record;
        }

        if (type.equalsIgnoreCase(ObservationType.metadata.toString())) {
            return ObservationType.metadata;
        }

        if (type.equalsIgnoreCase(ObservationType.multidimensional.toString())) {
            return ObservationType.multidimensional;
        }

        if (type.equalsIgnoreCase(ObservationType.text.toString())) {
            return ObservationType.text;
        }

        return null;
    }


    public Set<String> getTestedGenes(String sex, List<String> parameters)
    throws SolrServerException {

        HashSet<String> genes = new HashSet<String>();
        int i = 0;
        while (i < parameters.size()) {
			// Add no more than 10 params at the time so the url doesn't get too
            // long
            String parameter = parameters.get(i ++);
            String query = "(" + ObservationDTO.PARAMETER_STABLE_ID + ":" + parameter;
            while (i % 15 != 0 && i < parameters.size()) {
                parameter = parameters.get(i ++);
                query += " OR " + ObservationDTO.PARAMETER_STABLE_ID + ":" + parameter;
            }
            query += ")";

            SolrQuery q = new SolrQuery().setQuery(query).addField(ObservationDTO.GENE_ACCESSION_ID)
                    .setFilterQueries(ObservationDTO.STRAIN_ACCESSION_ID + ":\"" + StringUtils.join(OverviewChartsController.OVERVIEW_STRAINS, "\" OR " + ObservationDTO.STRAIN_ACCESSION_ID + ":\"") + "\"").setRows(-1);
            q.set("group.field", ObservationDTO.GENE_ACCESSION_ID);
            q.set("group", true);
            if (sex != null) {
                q.addFilterQuery(ObservationDTO.SEX + ":" + sex);
            }
            List<Group> groups = solr.query(q).getGroupResponse().getValues().get(0).getValues();
            for (Group gr : groups) {
                genes.add((String) gr.getGroupValue());
            }
        }
        return genes;
    }

    /**
     * Get all controls for a specified set of center, strain, parameter,
     * (optional) sex, and metadata group.
     *
     * @param parameterId
     * @param strain
     * @param organisationId
     * @param experimentDate date of experiment
     * @param sex if null, both sexes are returned
     * @param metadataGroup when metadataGroup is empty string, force solr to
     * search for metadata_group:""
     * @return list of control observationDTOs that conform to the search
     * criteria
     * @throws SolrServerException
     */
    public List<ObservationDTO> getAllControlsBySex(Integer parameterId, String strain, Integer organisationId, Date experimentDate, String sex, String metadataGroup)
    throws SolrServerException {

        List<ObservationDTO> results = new ArrayList<ObservationDTO>();

        QueryResponse response = new QueryResponse();

        SolrQuery query = new SolrQuery().setQuery("*:*").addFilterQuery(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":control").addFilterQuery(ObservationDTO.PARAMETER_ID + ":" + parameterId).addFilterQuery(ObservationDTO.STRAIN_ACCESSION_ID + ":" + strain.replace(":", "\\:")).setStart(0).setRows(5000);
        if (organisationId != null) {
            query.addFilterQuery(ObservationDTO.PHENOTYPING_CENTER_ID + ":" + organisationId);
        }

        if (metadataGroup == null) {
        } else if (metadataGroup.isEmpty()) {
            query.addFilterQuery(ObservationDTO.METADATA_GROUP + ":\"\"");
        } else {
            query.addFilterQuery(ObservationDTO.METADATA_GROUP + ":" + metadataGroup);
        }

        if (sex != null) {
            query.addFilterQuery(ObservationDTO.SEX + ":" + sex);
        }

		// Filter starting at 2000-01-01 and going through the end
        // of day on the experiment date
        if (experimentDate != null) {

			// Set time range to the last possible time on the day for SOLR
            // range query to include all observations on the same day
            Calendar cal = Calendar.getInstance();
            cal.setTime(DateUtils.addDays(experimentDate, 1));
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            cal.set(Calendar.MILLISECOND, 999);
            Date maxDate = cal.getTime();

            Date beginning = new Date(946684800000L); // Start date (Jan 1 2000)
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            String dateFilter = df.format(beginning) + "Z TO " + df.format(maxDate) + "Z";
            query.addFilterQuery(ObservationDTO.DATE_OF_EXPERIMENT + ":[" + dateFilter + "]");
        }
        response = solr.query(query);
        results = response.getBeans(ObservationDTO.class);
        LOG.debug("getAllControlsBySex " + query);
        return results;
    }

    /**
     * Get all controls for a specified set of center, strain, parameter,
     * (optional) sex, and metadata group that occur on the same day as passed
     * in (or in WTSI case, the same week).
     *
     * @param parameterId
     * @param strain
     * @param organisationId
     * @param experimentDate the date of interest
     * @param sex if null, both sexes are returned
     * @param metadataGroup when metadataGroup is empty string, force solr to
     * search for metadata_group:""
     * @return list of control observationDTOs that conform to the search
     * criteria
     * @throws SolrServerException
     */
    public List<ObservationDTO> getConcurrentControlsBySex(Integer parameterId, String strain, Integer organisationId, Date experimentDate, String sex, String metadataGroup)
    throws SolrServerException {

        List<ObservationDTO> results = new ArrayList<ObservationDTO>();

		// Use any control mouse ON THE SAME DATE as concurrent control
        // Set min and max time ranges to encompass the whole day
        Calendar cal = Calendar.getInstance();
        cal.setTime(experimentDate);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date minDate = cal.getTime();

        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        Date maxDate = cal.getTime();

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String dateFilter = df.format(minDate) + "Z TO " + df.format(maxDate) + "Z";

        QueryResponse response = new QueryResponse();

        SolrQuery query = new SolrQuery().setQuery("*:*").addFilterQuery(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":control").addFilterQuery(ObservationDTO.DATE_OF_EXPERIMENT + ":[" + dateFilter + "]").addFilterQuery(ObservationDTO.PARAMETER_ID + ":" + parameterId).addFilterQuery(ObservationDTO.PHENOTYPING_CENTER_ID + ":" + organisationId).addFilterQuery(ObservationDTO.STRAIN_ACCESSION_ID + ":" + strain.replace(":", "\\:")).addFilterQuery(ObservationDTO.SEX + ":" + sex).setStart(0).setRows(5000);

        if (metadataGroup == null) {
            // don't add a metadata group filter
        } else if (metadataGroup.isEmpty()) {
            query.addFilterQuery(ObservationDTO.METADATA_GROUP + ":\"\"");
        } else {
            query.addFilterQuery(ObservationDTO.METADATA_GROUP + ":" + metadataGroup);
        }

        response = solr.query(query);
        results = response.getBeans(ObservationDTO.class);

        return results;
    }


    public List<ObservationDTO> getAllImageRecordObservations()
    throws SolrServerException {

        SolrQuery query = ImageService.allImageRecordSolrQuery();
        return solr.query(query).getBeans(ObservationDTO.class);

    }


    public HttpSolrServer getSolrServer() {
        return solr;
    }


    public Set<String> getAllGeneIdsByResource(List<String> resourceName, boolean experimentalOnly) {

        SolrQuery q = new SolrQuery();
        q.setFacet(true);
        q.setFacetMinCount(1);
        q.setFacetLimit(-1);
        q.setRows(0);
        q.addFacetField(ObservationDTO.GENE_ACCESSION_ID);
        if (resourceName != null) {
            q.setQuery(ObservationDTO.DATASOURCE_NAME + ":" + StringUtils.join(resourceName, " OR " + ObservationDTO.DATASOURCE_NAME + ":"));
        } else {
            q.setQuery("*:*");
        }

        if (experimentalOnly) {
            q.addFilterQuery(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":experimental");
        }

        LOG.info("Solr URL getAllGeneIdsByResource " + solr.getBaseURL() + "/select?" + q);
        try {
            return getFacets(solr.query(q)).get(ObservationDTO.GENE_ACCESSION_ID).keySet();
        } catch (SolrServerException e) {
            e.printStackTrace();
        }

        return null;
    }


    public Set<String> getAllColonyIdsByResource(List<String> resourceName, boolean experimentalOnly) {

        SolrQuery q = new SolrQuery();
        q.setFacet(true);
        q.setFacetMinCount(1);
        q.setFacetLimit(-1);
        q.setRows(0);
        q.addFacetField(ObservationDTO.COLONY_ID);

        if (resourceName != null) {
            q.setQuery(ObservationDTO.DATASOURCE_NAME + ":" + StringUtils.join(resourceName, " OR " + ObservationDTO.DATASOURCE_NAME + ":"));
        } else {
            q.setQuery("*:*");
        }

        if (experimentalOnly) {
            q.addFilterQuery(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":experimental");
        }

        LOG.info("Solr URL getAllColonyIdsByResource " + solr.getBaseURL() + "/select?" + q);
        try {
            return getFacets(solr.query(q)).get(ObservationDTO.COLONY_ID).keySet();
        } catch (SolrServerException e) {
            e.printStackTrace();
        }

        return null;
    }


    public DataBatchesBySex getExperimentalBatches(String phenotypingCenter, String pipelineStableId, String parameterStableId, String strainAccessionId, String zygosity, String metadataGroup, String alleleAccessionId)
    throws SolrServerException {

        SolrQuery q = new SolrQuery()
                .setQuery("*:*")
                .setRows(10000)
                .setFields(ObservationDTO.SEX, ObservationDTO.DATE_OF_EXPERIMENT)
                .addFilterQuery(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":experimental")
                .addFilterQuery(ObservationDTO.PHENOTYPING_CENTER + ":\"" + phenotypingCenter + "\"")
                .addFilterQuery(ObservationDTO.PIPELINE_STABLE_ID + ":" + pipelineStableId)
                .addFilterQuery(ObservationDTO.PARAMETER_STABLE_ID + ":" + parameterStableId)
                .addFilterQuery(ObservationDTO.STRAIN_ACCESSION_ID + ":\"" + strainAccessionId + "\"")
                .addFilterQuery(ObservationDTO.ZYGOSITY + ":" + zygosity)
                .addFilterQuery(ObservationDTO.ALLELE_ACCESSION_ID + ":\"" + alleleAccessionId + "\"")
                .addFilterQuery(ObservationDTO.METADATA_GROUP + ":\"" + metadataGroup + "\"");

        return new DataBatchesBySex(solr.query(q).getBeans(ObservationDTO.class));
    }


    /**
     * Returns a list of <code>count</code> parameter stable ids matching <code>observationType</code>.
     *
     * @param observationType desired observation type
     * @param count the number of parameter stable ids to return
     *
     * @return a list of <code>count</code> parameter stable ids matching <code>observationType</code>.
     * @throws SolrServerException
     */
    public List<String> getParameterStableIdsByObservationType(ObservationType observationType, int count) throws SolrServerException {
        List<String> retVal = new ArrayList();

        if (count < 1)
            return retVal;

        SolrQuery query = new SolrQuery();
        // http://ves-ebi-d0:8090/mi/impc/dev/solr/experiment/select?q=observation_type%3Acategorical&rows=12&wt=json&indent=true&facet=true&facet.field=parameter_stable_id
        query
            .setQuery("observation_type:" + observationType.name())
            .addFacetField(ObservationDTO.PARAMETER_STABLE_ID)
            .setFacetMinCount(1)
            .setFacet(true)
            .setRows(count)
            .set("facet.limit", count);

        QueryResponse response = solr.query(query);
        for (Count facet: response.getFacetField(ObservationDTO.PARAMETER_STABLE_ID).getValues()) {
            retVal.add(facet.getName());
        }

        return retVal;
    }
    

    /**
     * @author tudose
     * @date 2015/07/08
     * @param alleleAccession
     * @param phenotypingCenter
     * @param resource
     * @return List of pipelines with data for the given parameters.
     * @throws SolrServerException 
     */    
    public List<ImpressBean> getPipelines(String alleleAccession, String phenotypingCenter, List<String> resource) 
    throws SolrServerException{
    	
    	List<ImpressBean> pipelines = new ArrayList<>();
		
    	SolrQuery query = new SolrQuery()
			.setQuery("*:*")
			.addFilterQuery(ObservationDTO.ALLELE_ACCESSION_ID + ":\"" + alleleAccession + "\"")
			.addField(ObservationDTO.PIPELINE_ID)
			.addField(ObservationDTO.PIPELINE_NAME)
			.addField(ObservationDTO.PIPELINE_STABLE_ID);
    	if (phenotypingCenter != null){
			query.addFilterQuery(ObservationDTO.PHENOTYPING_CENTER + ":\"" + phenotypingCenter + "\"");
    	}
    	if ( resource != null){
    		query.addFilterQuery(ObservationDTO.DATASOURCE_NAME + ":\"" + StringUtils.join(resource, "\" OR " + ObservationDTO.PHENOTYPING_CENTER + ":\"") + "\"");
    	}
    	
		query.set("group", true);
		query.set("group.field", ObservationDTO.PIPELINE_STABLE_ID);
		query.setRows(10000);
		query.set("group.limit", 1);

		System.out.println("SOLR URL getPipelines " + solr.getBaseURL() + "/select?" + query);
		
		QueryResponse response = solr.query(query);
		
		for ( Group group: response.getGroupResponse().getValues().get(0).getValues()){

			SolrDocument doc = group.getResult().get(0);
			ImpressBean pipeline = new ImpressBean(null, doc.getFirstValue(ObservationDTO.PIPELINE_ID).toString(), doc.getFirstValue(ObservationDTO.PIPELINE_STABLE_ID).toString(), doc.getFirstValue(ObservationDTO.PIPELINE_NAME).toString());
			pipelines.add(pipeline);
			
		}

		return pipelines;
    }
    
}
