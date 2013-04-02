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
	public Images getAllImages(int start,int length, String search) {
		Images images=new Images();
		//default for start and length if not specified is set in the controller
		System.out.println("search="+search+" start="+start+" length="+length);
		if(!search.equals("")&& (!search.equals(""))){
			images= getImagesFromSearch(start, length, search);
		}else{
		images= queryDatabaseByRows(start, length);
		}
		images.setStart(start);
		images.setLength(length);
		return images;
	}


	private Images getImagesFromSearch(int start, int length, String search) {
		//if a search query param is present we search solr or the database
		List<ImaImageRecord> list=new ArrayList<ImaImageRecord>();
		Images images=new Images();
			
			List<String> ids = imagesSolrDao.getIdsForKeywordsSearch(search, start, length);
			for(String id:ids){
				ImaImageRecord record=queryDatabaseById(id);
				list.add(record);
			}
			//set the data about how many images there are which we can get from solr
			images.setTotal(imagesSolrDao.getNumberFound());
			images.setImages(list);
		return images;
	}


	public ImaImageRecord queryDatabaseById(String id) {

		String basicQuery="from ImaImageRecord where   id="+id;
		Query q=sessionFactory.getCurrentSession().createQuery(basicQuery);		
		ImaImageRecord record=(ImaImageRecord) q.uniqueResult();
		return record;
	}
	
	
	private Images queryDatabaseByRows(int start, int length) {
		Images images=new Images();
		String basicQuery="from ImaImageRecord where published_status_id=1";
		if(sessionFactory.isClosed()){
			sessionFactory.openSession();
		}
		Query q=sessionFactory.getCurrentSession().createQuery(basicQuery);
		
		//if length is 0 then just output all the results
		if(length!=0){
		q.setFirstResult(start);
		q.setMaxResults(length);
		}
		List<ImaImageRecord> list=q.list();
//		for(ImaImageRecord item:list){
//			System.out.println(".");
//		}
		images.setTotal(0);//set total to 0 here as we don't get the total size info - though we could through a count(*)
		images.setImages(list);
		
		//System.out.println("==============================list size="+list.size());
		return images;
	}
	
	

	@Transactional(readOnly = true)
	public ImaImageRecord getImageWithId(int id) {
		Query q=sessionFactory.getCurrentSession().createQuery("from ImaImageRecord where published_status_id=1 and  id="+id);
		ImaImageRecord image=(ImaImageRecord) q.uniqueResult();
		
		return image;
	}


	@Override
	@Transactional(readOnly = true)
	public long getTotalNumberOfImages() {
		//select count(*) from IMA_IMAGE_RECORD;
		String numberQuery="select count(*) from ImaImageRecord where published_status_id=1";
		Query q=sessionFactory.getCurrentSession().createQuery(numberQuery);		
		long records=(Long) q.uniqueResult();
		return records;
	}
}
