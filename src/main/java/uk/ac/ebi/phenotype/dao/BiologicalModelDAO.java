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

/**
 * 
 * Biological model manager interface.
 * 
 * @author Gautier Koscielny (EMBL-EBI) <koscieln@ebi.ac.uk>
 * @since May 2012
 */

import java.util.List;

import uk.ac.ebi.phenotype.pojo.BiologicalModel;
import uk.ac.ebi.phenotype.pojo.BiologicalSample;
import uk.ac.ebi.phenotype.pojo.Datasource;
import uk.ac.ebi.phenotype.pojo.LiveSample;
import uk.ac.ebi.phenotype.pojo.Organisation;

public interface BiologicalModelDAO extends HibernateDAO {

	public void saveBiologicalSample(BiologicalSample sample);
	public void saveLiveSample(LiveSample sample);
	public int deleteAllLiveSamplesByDatasource(Datasource datasource);
	public int  deleteAllLiveSamplesWithoutModelsByDatasource(Datasource datasource);
	public int deleteAllBiologicalSamplesByDatasource(Datasource datasource);
	
	public List<BiologicalModel> getAllBiologicalModelsByDatasourceId(int databaseId);
	public List<BiologicalModel> getAllBiologicalModelsByAccession(String accession);
	
	public List<LiveSample> getAllLiveSamplesByDatasourceId(int databaseId);
	
	
	public BiologicalModel getBiologicalModelById(int modelId);
	public BiologicalSample getBiologicalSampleById(int sampleId);
	
	public void saveBiologicalModel(BiologicalModel model);
	
	public int deleteAllBiologicalModelsByDatasource(Datasource datasource);
	
	public List<LiveSample> getAllLiveSampleByOrganisationAndDatasource(Organisation organisation, Datasource datasource);
	public LiveSample getAllLiveSampleBySampleId(String sampleId);
	public void deleteAllBiologicalModelsAndRelatedDataByDatasourceOrganisation(Datasource ds, Organisation o);
}
