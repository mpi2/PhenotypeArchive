package uk.ac.ebi.generic.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.log4j.Logger;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import uk.ac.ebi.phenotype.web.util.HttpProxy;

public class JSONRestUtil {

private static final Logger log = Logger.getLogger(JSONRestUtil.class);
	/**
	 * Get the results of a query from the provided url. make sure the url requests JSON!!!
	 * 
	 * @param url
	 *            the URL from which to get the content
	 * @return a JSONObject representing the result of the query
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public static JSONObject getResults(String url) throws IOException, URISyntaxException {

		log.debug("GETTING CONTENT FROM: " + url);
		
		HttpProxy proxy = new HttpProxy();
		String content = proxy.getContent(new URL(url));

		return (JSONObject) JSONSerializer.toJSON(content);
	}
}
