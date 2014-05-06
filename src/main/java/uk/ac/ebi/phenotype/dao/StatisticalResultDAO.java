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
package uk.ac.ebi.phenotype.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import uk.ac.ebi.phenotype.pojo.BiologicalModel;
import uk.ac.ebi.phenotype.pojo.CategoricalResult;
import uk.ac.ebi.phenotype.pojo.Parameter;
import uk.ac.ebi.phenotype.pojo.SexType;
import uk.ac.ebi.phenotype.pojo.UnidimensionalResult;
import uk.ac.ebi.phenotype.pojo.ZygosityType;
import uk.ac.ebi.phenotype.stats.MouseDataPoint;
import uk.ac.ebi.phenotype.bean.StatisticalResultBean;

/**
 * 
 * The statistical result data access object is a wrapper to get access to
 * the results stored in the databased once the statistical pipeline has been
 * run on the categorical, unidimensional and derived parameters.
 * 
 * @author Jonathan Warren <jwarren@ebi.ac.uk>
 * @author Gautier Koscielny (EMBL-EBI) <koscieln@ebi.ac.uk>
 * @since May 2014
 */

public interface StatisticalResultDAO {

	/**
	 * Give an allele identifier,a phenotyping center and a pipeline stable id, 
	 * returns a list of pValues and effect size for every parameter.
	 * @return a map of pvalues and effect size for the given phenotyping pipeline
	 */
	public Map<String, StatisticalResultBean> getPvaluesByAlleleAndPhenotypingCenterAndPipeline(
			String alleleAccession, String phenotypingCenter, String pipelineStableId);
	
	/**
	 * Given a procedure parameter and a biological model, returns a list of 
	 * unidimensional results for this parameter and model. 
	 * @param parameter 
	 * @param controlBiologicalModel
	 * @param biologicalModel
	 * @return
	 */
	public List<UnidimensionalResult> getUnidimensionalResultByParameterAndBiologicalModel(
			Parameter parameter, BiologicalModel controlBiologicalModel,
			BiologicalModel biologicalModel);
	
	public List<UnidimensionalResult> getUnidimensionalResultByParameterIdAndBiologicalModelIds(
			Integer parameter, Integer controlBiologicalId,
			Integer biologicalId);
	
	
	public List<MouseDataPoint> getMutantDataPointsWithMouseName(SexType sex, ZygosityType zygosity, Parameter parameter,  Integer populationId);
	public List<MouseDataPoint> getControlDataPointsWithMouseName(Integer populationId);

	public UnidimensionalResult getUnidimensionalStatsForPhenotypeCallSummaryId(
			int phenotypeCallSummaryId) throws SQLException;
        
    public CategoricalResult getCategoricalStatsForPhenotypeCallSummaryId(int phenotypeCallSummaryId) throws SQLException;
        
}
