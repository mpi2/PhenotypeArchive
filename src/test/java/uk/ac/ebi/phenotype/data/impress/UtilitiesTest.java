package uk.ac.ebi.phenotype.data.impress;

import static org.junit.Assert.fail;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.ebi.phenotype.dao.PhenotypePipelineDAO;
import uk.ac.ebi.phenotype.pojo.ObservationType;
import uk.ac.ebi.phenotype.pojo.Parameter;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-config.xml" })
@TransactionConfiguration
@Transactional
public class UtilitiesTest {

    @Autowired
    PhenotypePipelineDAO pDAO;
    

    @Test
    public void testCheckTypeParameterString() {
        Parameter p = pDAO.getParameterByStableId("ESLIM_003_001_006");
        String value= "2.092";
        ObservationType oType = Utilities.checkType(p, value);
   
        System.out.println(oType);
        assert(oType.equals(ObservationType.time_series));
    }

}
