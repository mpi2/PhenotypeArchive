/**
 * Copyright © 2011-2013 EMBL - European Bioinformatics Institute
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

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrServerException;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.ebi.phenotype.imaging.persistence.ImaImageRecord;
import uk.ac.ebi.phenotype.imaging.springrest.images.Images;


public class HibernateImagesDao implements ImagesDao {

	
	private SessionFactory sessionFactory;
	private ImagesSolrDao imagesSolrDao;


	/**
	 * Creates a new Hibernate  manager to get images data. Used by the restful web service currently only.
	 * @param sessionFactory the Hibernate session factory
	 */
	public HibernateImagesDao(SessionFactory sessionFactory, ImagesSolrDao imagesSolrDao) {
		this.sessionFactory = sessionFactory;
		this.imagesSolrDao=imagesSolrDao;
		
	}

	@Transactional(readOnly = true)
	public Images getAllImages(int start, int length, String search) throws Exception {
		Images images = new Images();

		if ( ! search.equals("")) {
			images = getImagesFromSearch(start, length, search);
		} else {
			images = queryDatabaseByRows(start, length);
		}

		images.setStart(start);
		images.setLength(length);

		return images;
	}


	private Images getImagesFromSearch(int start, int length, String search) throws SolrServerException {
		List<ImaImageRecord> list = new ArrayList<ImaImageRecord>();
		Images images = new Images();

		List<String> ids = imagesSolrDao.getIdsForKeywordsSearch(search, start, length);
		for (String id : ids) {
			ImaImageRecord record = getImageWithId(Integer.valueOf(id));
			list.add(record);
		}

		//set the data about how many images there are which we can get from solr
		images.setTotal(imagesSolrDao.getNumberFound());
		images.setImages(list);

		return images;
	}


	private Images queryDatabaseByRows(int start, int length) {
		Images images = new Images();
		Query q=sessionFactory.getCurrentSession().createQuery("from ImaImageRecord where published_status_id=1");
		
		// Set offset and limit if provided
		if (length != 0) {
			q.setFirstResult(start);
			q.setMaxResults(length);
		}

		// set total to 0 here as we don't get the total size 
		// info - though we could through a count(*)
		images.setTotal(0);
		images.setImages(q.list());

		return images;
	}
	
	

	@Transactional(readOnly = true)
	public ImaImageRecord getImageWithId(int id) {
		return (ImaImageRecord) sessionFactory.getCurrentSession()
			.createQuery("FROM ImaImageRecord WHERE published_status_id=1 AND id=?")
			.setInteger(0, id)
			.uniqueResult();
	}


	@Override
	@Transactional(readOnly = true)
	public long getTotalNumberOfImages() {
		return (Long) sessionFactory.getCurrentSession()
			.createQuery("select count(*) from ImaImageRecord where published_status_id=1")
			.uniqueResult();
	}
}
