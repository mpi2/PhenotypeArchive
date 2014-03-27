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
 * Strain data access manager implementation.
 * 
 * @author Gautier Koscielny (EMBL-EBI) <koscieln@ebi.ac.uk>
 * @since February 2012
 */

import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.ebi.phenotype.pojo.Allele;
import uk.ac.ebi.phenotype.pojo.BiologicalModel;
import uk.ac.ebi.phenotype.pojo.Strain;


public class StrainDAOImpl extends HibernateDAOImpl implements StrainDAO {

        /**
         * Creates a new Hibernate sequence region data access manager.
         * @param sessionFactory the Hibernate session factory
         */
        public StrainDAOImpl(SessionFactory sessionFactory) {
                this.sessionFactory = sessionFactory;
        }

        @Override
        @SuppressWarnings("unchecked")
        public List<Strain> getAllStrains() {
                return getCurrentSession().createQuery("from Strain").list();
        }

        @Transactional(readOnly = true)
        public Strain getStrainByName(String name) {
                return (Strain) getCurrentSession().createQuery("from Strain as s where s.name= ?").setString(0, name).uniqueResult();
        }
        
        @Transactional(readOnly = true)
        public Strain getStrainByAcc(String acc) {
                return (Strain) getCurrentSession().createQuery("from Strain as s where s.id.accession= ?").setString(0, acc).uniqueResult();
        }


        @Transactional(readOnly = true)
        public Strain getStrainBySynonym(String name) {
                // select mother from Cat as mother, Cat as kit
                //where kit in elements(foo.kittens)
                //return (Strain) getCurrentSession().createQuery("select strain from Strain as strain, Synonym as synonym where synonym in elements(strain.synonyms) and synonym.symbol = ?").setString(0, name).uniqueResult();
                return (Strain) getCurrentSession().createQuery("from Strain as strain left join strain.synonyms as synonym where synonym.symbol = ?").setString(0, name).uniqueResult();
        }

        @Transactional(readOnly = false)
        public void saveStrain(Strain strain) {
                try {
                        getCurrentSession().merge(strain);
                } catch (Exception e) {
                        e.printStackTrace();
                        getCurrentSession().saveOrUpdate(strain);
                }

        }
        
        @Transactional(readOnly = false)
        public void saveOrUpdateStrain(Strain strain) {
                getCurrentSession().saveOrUpdate(strain);
                getCurrentSession().flush();
        }

}

