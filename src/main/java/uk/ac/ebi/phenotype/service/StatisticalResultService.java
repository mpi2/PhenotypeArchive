/**
 * Copyright © 2011-2014 EMBL - European Bioinformatics Institute
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
package uk.ac.ebi.phenotype.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.ac.ebi.phenotype.dao.BiologicalModelDAO;
import uk.ac.ebi.phenotype.dao.DatasourceDAO;
import uk.ac.ebi.phenotype.dao.OrganisationDAO;
import uk.ac.ebi.phenotype.dao.PhenotypePipelineDAO;
import uk.ac.ebi.phenotype.dao.ProjectDAO;
import uk.ac.ebi.phenotype.pojo.CategoricalResult;
import uk.ac.ebi.phenotype.pojo.GenomicFeature;
import uk.ac.ebi.phenotype.pojo.ObservationType;
import uk.ac.ebi.phenotype.pojo.Parameter;
import uk.ac.ebi.phenotype.pojo.SexType;
import uk.ac.ebi.phenotype.pojo.StatisticalResult;
import uk.ac.ebi.phenotype.pojo.UnidimensionalResult;
import uk.ac.ebi.phenotype.pojo.ZygosityType;
import uk.ac.ebi.phenotype.service.dto.GenotypePhenotypeDTO;
import uk.ac.ebi.phenotype.service.dto.StatisticalResultDTO;
import uk.ac.ebi.phenotype.web.pojo.GeneRowForHeatMap;
import uk.ac.ebi.phenotype.web.pojo.HeatMapCell;

@Service
public class StatisticalResultService {

    @Autowired
    BiologicalModelDAO bmDAO;
    
    @Autowired
    DatasourceDAO datasourceDAO;
    
    @Autowired
    OrganisationDAO organisationDAO;
    
    @Autowired
    PhenotypePipelineDAO pDAO;
    
    @Autowired
    ProjectDAO projectDAO;
    
    private HttpSolrServer solr;

    private static final Logger LOG = LoggerFactory.getLogger(StatisticalResultService.class);

    public StatisticalResultService(String solrUrl) {
        solr = new HttpSolrServer(solrUrl);
    }

    /**
     * Get the result for a set of 
     *  allele strain phenotypeCenter, pipeline, parameter, metadata, zygosity, sex
     * @param statisticalType 
     * @throws SolrServerException 
     */
    public List<? extends StatisticalResult> getStatisticalResult(
            String alleleAccession, 
            String strain, 
            String phenotypingCenter, 
            String pipelineStableId, 
            String parameterStableId, 
            String metadataGroup, 
            ZygosityType zygosity, 
            SexType sex, 
            ObservationType statisticalType) throws SolrServerException {

        List<StatisticalResult> results = new ArrayList<>();

        QueryResponse response = new QueryResponse();
    
        SolrQuery query = new SolrQuery()
            .setQuery("*:*")
            .addFilterQuery(StatisticalResultDTO.ALLELE_ACCESSION_ID + ":\"" + alleleAccession + "\"")
            .addFilterQuery(StatisticalResultDTO.PHENOTYPING_CENTER + ":\"" + phenotypingCenter + "\"")
            .addFilterQuery(StatisticalResultDTO.PIPELINE_STABLE_ID + ":" + pipelineStableId)
            .addFilterQuery(StatisticalResultDTO.PARAMETER_STABLE_ID + ":" + parameterStableId)
            .addFilterQuery(StatisticalResultDTO.ZYGOSITY + ":" + zygosity.name())
            .setStart(0)
            .setRows(10)
        ;

        if(strain != null) {
            query.addFilterQuery(StatisticalResultDTO.STRAIN_ACCESSION_ID + ":\"" + strain + "\"");
        }

        if(sex != null) {
            query.addFilterQuery(StatisticalResultDTO.SEX + ":" + sex);
        }

        if(metadataGroup==null) {
            // don't add a metadata group filter
        } else if (metadataGroup.isEmpty()) {
            query.addFilterQuery(StatisticalResultDTO.METADATA_GROUP + ":\"\"");
        } else {
            query.addFilterQuery(StatisticalResultDTO.METADATA_GROUP + ":" + metadataGroup);
        }

        response = solr.query(query);
        List<StatisticalResultDTO> solrResults = response.getBeans(StatisticalResultDTO.class);

        if (statisticalType == ObservationType.unidimensional) {

            for (StatisticalResultDTO solrResult : solrResults) {
                results.add(translateStatisticalResultToUnidimensionalResult(solrResult));
            }

        } else if (statisticalType == ObservationType.categorical) {

            for (StatisticalResultDTO solrResult : solrResults) {
                results.add(translateStatisticalResultToCategoricalResult(solrResult));
            }

        }
        
        return results;
    }


    protected UnidimensionalResult translateStatisticalResultToUnidimensionalResult(StatisticalResultDTO result) {
        UnidimensionalResult r = new UnidimensionalResult();
        
        if(result.getBatchSignificant()!=null) r.setBatchSignificance(Boolean.valueOf(result.getBatchSignificant()));
        if(result.getBlupsTest()!= null) r.setBlupsTest(new Double(result.getBlupsTest()));
        r.setColonyId(result.getColonyId());
        if(result.getControlBiologicalModelId()!= null) r.setControlBiologicalModel(bmDAO.getBiologicalModelById(result.getControlBiologicalModelId()));
        r.setControlSelectionStrategy(result.getControlSelectionMethod());
        if(result.getResourceId()!= null) r.setDatasource(datasourceDAO.getDatasourceById(result.getResourceId()));
        r.setDependentVariable(result.getDependentVariable());
        if(result.getEffectSize()!= null) r.setEffectSize(new Double(result.getEffectSize()));
        if(result.getMutantBiologicalModelId()!= null) r.setExperimentalBiologicalModel(bmDAO.getBiologicalModelById(result.getMutantBiologicalModelId()));
        if(result.getZygosity()!= null) r.setExperimentalZygosity(ZygosityType.valueOf(result.getZygosity()));
        r.setFemaleControls(result.getFemaleControlCount());
        r.setFemaleMutants(result.getFemaleMutantCount());
        if(result.getGenotypeEffectPValue()!= null) r.setGenderEffectPValue(new Double(result.getGenotypeEffectPValue()));
        if(result.getFemaleKoParameterEstimate()!= null) r.setGenderFemaleKoEstimate(new Double(result.getFemaleKoParameterEstimate()));
        if(result.getFemaleKoEffectPValue()!= null) r.setGenderFemaleKoPValue(new Double(result.getFemaleKoEffectPValue()));
        if(result.getFemaleKoEffectStderrEstimate()!= null) r.setGenderFemaleKoStandardErrorEstimate(new Double(result.getFemaleKoEffectStderrEstimate()));
        if(result.getMaleKoParameterEstimate()!= null) r.setGenderMaleKoEstimate(new Double(result.getMaleKoParameterEstimate()));
        if(result.getMaleKoEffectPValue()!= null) r.setGenderMaleKoPValue(new Double(result.getMaleKoEffectPValue()));
        if(result.getMaleKoEffectStderrEstimate()!= null) r.setGenderMaleKoStandardErrorEstimate(new Double(result.getMaleKoEffectStderrEstimate()));
        if(result.getSexEffectParameterEstimate()!= null) r.setGenderParameterEstimate(new Double(result.getSexEffectParameterEstimate()));
        if(result.getSexEffectStderrEstimate()!= null) r.setGenderStandardErrorEstimate(new Double(result.getSexEffectStderrEstimate()));
        if(result.getGenotypeEffectPValue()!= null) r.setGenotypeEffectPValue(new Double(result.getGenotypeEffectPValue()));
        if(result.getGenotypeEffectParameterEstimate()!= null) r.setGenotypeParameterEstimate(new Double(result.getGenotypeEffectParameterEstimate()));
        if(result.getGenotypeEffectStderrEstimate()!= null) r.setGenotypeStandardErrorEstimate(new Double(result.getGenotypeEffectStderrEstimate()));
        r.setGp1Genotype(result.getGroup1Genotype());
        if(result.getGroup1ResidualsNormalityTest()!= null) r.setGp1ResidualsNormalityTest(new Double(result.getGroup1ResidualsNormalityTest()));
        r.setGp2Genotype(result.getGroup2Genotype());
        if(result.getGroup2ResidualsNormalityTest()!= null) r.setGp2ResidualsNormalityTest(new Double(result.getGroup2ResidualsNormalityTest()));
        r.setId(result.getDbId());
        if(result.getInteractionEffectPValue()!= null) r.setInteractionEffectPValue(new Double(result.getInteractionEffectPValue()));
        r.setInteractionSignificance(result.getInteractionSignificant());
        if(result.getInterceptEstimate()!= null) r.setInterceptEstimate(new Double(result.getInterceptEstimate()));
        if(result.getInterceptEstimateStderrEstimate()!= null) r.setInterceptEstimateStandardError(new Double(result.getInterceptEstimateStderrEstimate()));
        r.setMaleControls(result.getMaleControlCount());
        r.setMaleMutants(result.getMaleMutantCount()); 
        r.setMetadataGroup(result.getMetadataGroup());
        if(result.getNullTestPValue()!= null) r.setNullTestSignificance(new Double(result.getNullTestPValue()));
        if(result.getPhenotypingCenter()!= null) r.setOrganisation(organisationDAO.getOrganisationByName(result.getPhenotypingCenter()));
        if(result.getParameterStableId()!= null) r.setParameter(pDAO.getParameterByStableId(result.getParameterStableId()));
        if(result.getPipelineStableId()!= null) r.setPipeline(pDAO.getPhenotypePipelineByStableId(result.getPipelineStableId()));
        if(result.getProjectName()!= null) r.setProject(projectDAO.getProjectByName(result.getProjectName()));
        if(result.getpValue()!= null) r.setpValue(new Double(result.getpValue()));
        r.setRawOutput(result.getRawOutput());
        if(result.getRotatedResidualsTest()!= null) r.setRotatedResidualsNormalityTest(new Double(result.getRotatedResidualsTest()));
        if(result.getSex()!= null) r.setSexType(SexType.valueOf(result.getSex()));
        r.setStatisticalMethod(result.getStatisticalMethod());
        r.setVarianceSignificance(result.getVarianceSignificant());
        if(result.getWeightEffectPValue()!= null) r.setWeightEffectPValue(new Double(result.getWeightEffectPValue()));
        if(result.getWeightEffectParameterEstimate()!= null) r.setWeightParameterEstimate(new Double(result.getWeightEffectParameterEstimate()));
        if(result.getWeightEffectStderrEstimate()!= null) r.setWeightStandardErrorEstimate(new Double(result.getWeightEffectStderrEstimate()));
        if(result.getZygosity()!= null) r.setZygosityType(ZygosityType.valueOf(result.getZygosity()));

        return r;
    }

    protected CategoricalResult translateStatisticalResultToCategoricalResult(StatisticalResultDTO result) {
        CategoricalResult r = new CategoricalResult();
        r.setColonyId(result.getColonyId());
        if(result.getControlBiologicalModelId()!= null) r.setControlBiologicalModel(bmDAO.getBiologicalModelById(result.getControlBiologicalModelId()));
        r.setControlSelectionStrategy(result.getControlSelectionMethod());
        if(result.getResourceId()!= null) r.setDatasource(datasourceDAO.getDatasourceById(result.getResourceId()));
        r.setDependentVariable(result.getDependentVariable());
        if(result.getEffectSize()!= null) r.setEffectSize(new Double(result.getEffectSize()));
        if(result.getMutantBiologicalModelId()!= null) r.setExperimentalBiologicalModel(bmDAO.getBiologicalModelById(result.getMutantBiologicalModelId()));
        if(result.getZygosity()!= null) r.setExperimentalZygosity(ZygosityType.valueOf(result.getZygosity()));
        r.setFemaleControls(result.getFemaleControlCount());
        r.setFemaleMutants(result.getFemaleMutantCount());
        r.setMaleControls(result.getMaleControlCount());
        r.setMaleMutants(result.getMaleMutantCount()); 
        r.setMetadataGroup(result.getMetadataGroup());
        if(result.getPhenotypingCenter()!= null) r.setOrganisation(organisationDAO.getOrganisationByName(result.getPhenotypingCenter()));
        if(result.getParameterStableId()!= null) r.setParameter(pDAO.getParameterByStableId(result.getParameterStableId()));
        if(result.getPipelineStableId()!= null) r.setPipeline(pDAO.getPhenotypePipelineByStableId(result.getPipelineStableId()));
        if(result.getProjectName()!= null) r.setProject(projectDAO.getProjectByName(result.getProjectName()));
        if(result.getpValue()!= null) r.setpValue(new Double(result.getpValue()));
        r.setRawOutput(result.getRawOutput());
        if(result.getSex()!= null) r.setSexType(SexType.valueOf(result.getSex()));
        r.setStatisticalMethod(result.getStatisticalMethod());
        if(result.getZygosity()!= null) r.setZygosityType(ZygosityType.valueOf(result.getZygosity()));
        r.setCategoryA(StringUtils.join(result.getCategories(), "|"));
        if(result.getSex()!= null) r.setControlSex(SexType.valueOf(result.getSex()));
        r.setEffectSize(result.getEffectSize());
        if(result.getSex()!= null) r.setExperimentalSex(SexType.valueOf(result.getSex()));

        return r;
    }

    
    // ***************************************************************//
    // DO WE NEED THIS ANYMORE?
    // ***************************************************************//
    public GeneRowForHeatMap getResultsForGeneHeatMap(String accession, GenomicFeature gene, List<Parameter> parameters) {
        GeneRowForHeatMap row = new GeneRowForHeatMap(accession);
        if (gene != null) {
            row.setSymbol(gene.getSymbol());
        } else {
            System.err.println("error no symbol for gene " + accession);
        }
        List<HeatMapCell> results = new ArrayList<HeatMapCell>();
        // search by gene and a list of params
        // or search on gene and then loop through params to add the results if
        // available order by ascending p value means we can just pick off the
        // first entry for that param
        // http://wwwdev.ebi.ac.uk/mi/impc/dev/solr/genotype-phenotype/select/?q=marker_accession_id:%22MGI:104874%22&rows=10000000&version=2.2&start=0&indent=on&wt=json

        Map<String, HeatMapCell> paramMap = new HashMap<>();// map to contain
                                                            // parameters with
                                                            // their associated
                                                            // status or pvalue
                                                            // as a string
        for (Parameter param : parameters) {
            // System.out.println("adding param to paramMap="+paramId);
            paramMap.put(param.getStableId(), null);
        }

        SolrQuery q = new SolrQuery()
                .setQuery(StatisticalResultDTO.MARKER_ACCESSION_ID + ":\"" + accession + "\"").setSort(StatisticalResultDTO.P_VALUE, SolrQuery.ORDER.asc)
                .setRows(10000);
        QueryResponse response = null;

        try {
            response = solr.query(q);
            results = response.getBeans(HeatMapCell.class);
            for (HeatMapCell cell : results) {
                // System.out.println(doc.getFieldValues("p_value"));

                String paramStableId = cell.getxAxisKey();
                // System.out.println("comparing"+cell.getParameterStableId()+"|");
                if (paramMap.containsKey(cell.getxAxisKey())) {
                    System.out.println("cell mp Term name=" + cell.getLabel());
                    System.out.println("cell p value=" + cell.getFloatValue());
                    System.out.println(cell.getFloatValue() + "found");
                    paramMap.put(paramStableId, cell);
                    if (row.getLowestPValue() > cell.getFloatValue()) {
                        row.setLowestPValue(cell.getFloatValue());
                    }
                }
            }

            row.setXAxisToCellMap(paramMap);
        } catch (SolrServerException ex) {
            LOG.error(ex.getMessage());
        }

        return row;
    }
    // ***************************************************************//
    // DO WE NEED THIS ANYMORE?
    // ***************************************************************//

}
