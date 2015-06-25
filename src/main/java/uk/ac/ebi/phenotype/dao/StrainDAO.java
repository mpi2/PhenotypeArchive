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
package uk.ac.ebi.phenotype.dao;

/**
 *
 * Strain data access manager interface.
 * 
 * @author Gautier Koscielny (EMBL-EBI) <koscieln@ebi.ac.uk>
 * @since February 2012
 */

import java.util.List;

import uk.ac.ebi.phenotype.pojo.Strain;

public interface StrainDAO extends HibernateDAO {

        public List<Strain> getAllStrains();

        /**
         * Find a strain by its nane.
         * @param name the strain name
         * @return the strain
         */
        public Strain getStrainByName(String name);     

        /**
         * Find a strain by its name or synonym.
         * @param name the strain name or synonym
         * @return the strain
         */
        public Strain getStrainBySynonym(String name);  

        /**
         * Save a strain
         * @param strain the strain to save
         */
        public void saveStrain(Strain strain);

        public Strain getStrainByAcc(String strain);

        public void saveOrUpdateStrain(Strain strain);
}

