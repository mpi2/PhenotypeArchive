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
package uk.ac.ebi.phenotype.imaging.springrest.images.dao;

import uk.ac.ebi.phenotype.imaging.persistence.ImaImageRecord;
import uk.ac.ebi.phenotype.imaging.springrest.images.Images;

/**
 * Images dao interface used by currently hibernate and our images restful web service
 * @author jwarren
 *
 */
public interface ImagesDao {
	
	public Images getAllImages(int start,int length, String search) throws Exception;

	public ImaImageRecord getImageWithId(int id);
	
	public long getTotalNumberOfImages();

}
