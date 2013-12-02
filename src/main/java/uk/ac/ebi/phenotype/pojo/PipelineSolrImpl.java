package uk.ac.ebi.phenotype.pojo;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import uk.ac.ebi.generic.util.JSONRestUtil;
import uk.ac.ebi.phenotype.stats.GenotypePhenotypeService;

public class PipelineSolrImpl {
	
	@Autowired
	GenotypePhenotypeService gpService;
	
	public PipelineSolrImpl( ) {
	}
	
	public Parameter getParameterByStableId(
			String paramStableId, String queryString) throws IOException,
			URISyntaxException {
	
		return gpService.getParameterByStableId(paramStableId, queryString);
	}
}
