package uk.ac.ebi.phenotype.pojo;

import java.io.IOException;
import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Autowired;

import uk.ac.ebi.phenotype.service.GenotypePhenotypeService;

public class PipelineSolrImpl {
	
	@Autowired
	GenotypePhenotypeService gpService;
	
	public PipelineSolrImpl() {
	}
	
	public Parameter getParameterByStableId(
			String paramStableId, String queryString) throws IOException,
			URISyntaxException {
	
		return gpService.getParameterByStableId(paramStableId, queryString);
	}
}
