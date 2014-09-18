package uk.ac.ebi.phenotype.pojo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import uk.ac.ebi.phenotype.service.PostQcService;

import java.io.IOException;
import java.net.URISyntaxException;

public class PipelineSolrImpl {
	
	@Autowired
	@Qualifier("postqcService")
	PostQcService gpService;
	
	public PipelineSolrImpl() {
	}
	
	public Parameter getParameterByStableId(
			String paramStableId, String queryString) throws IOException,
			URISyntaxException {
	
		return gpService.getParameterByStableId(paramStableId, queryString);
	}
}
