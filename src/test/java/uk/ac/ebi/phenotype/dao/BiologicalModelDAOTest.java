/*
 * Copyright © 2015 EMBL - European Bioinformatics Institute
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
 *
 */

package uk.ac.ebi.phenotype.dao;

import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.phenotype.pojo.BiologicalModel;


/**
 * Created by jmason on 16/06/2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-config.xml" })
public class BiologicalModelDAOTest extends TestCase {

	@Autowired
	BiologicalModelDAO bmDao;

	@Test
	public void testGetAllBiologicalModels() throws Exception {
		BiologicalModel bm=null;

		bm = bmDao.findByDbidAndAllelicCompositionAndGeneticBackgroundAndZygosity(12, "", "involves: C57BL/6", "homozygote");
		assert(bm!=null);


	}
}
