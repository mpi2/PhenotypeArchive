package uk.ac.ebi.phenotype.service;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;

import uk.ac.ebi.phenotype.service.dto.PipelineDTO;

import javax.annotation.Resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Wrapper around the pipeline core.
 * 
 * @author tudose
 * 
 */

public class ImpressService {

	@Resource(name = "globalConfiguration")
	private Map<String, String> config;

	private final HttpSolrServer solr; 


	public ImpressService() {

		this("http://wwwdev.ebi.ac.uk/mi/impc/dev/solr/pipeline"); // default
	}


	public ImpressService(String solrUrl) {

		solr = new HttpSolrServer(solrUrl);
	}


	public List<Integer> getProcedureStableKey(String procedureStableId) {

		try {
			SolrQuery query = new SolrQuery()
				.setQuery(PipelineDTO.PROCEDURE_STABLE_ID + ":\"" + procedureStableId + "\"")
				.setFields(PipelineDTO.PROCEDURE_STABLE_KEY);

			QueryResponse response = solr.query(query);
//System.out.println("impress in getprocedureStablekey response ="+response);
			return response.getBeans(PipelineDTO.class).get(0).getProcedureStableKey();

		} catch (SolrServerException | IndexOutOfBoundsException e) {
			e.printStackTrace();
		}

		return null;
	}

	
	public List<Integer> getPipelineStableKey(String pipelineStableId) {

		try {
			SolrQuery query = new SolrQuery()
				.setQuery(PipelineDTO.PIPELINE_STABLE_ID + ":\"" + pipelineStableId + "\"")
				.setFields(PipelineDTO.PIPELINE_STABLE_KEY);

			QueryResponse response = solr.query(query);

			return response.getBeans(PipelineDTO.class).get(0).getPipelineStableKey();

		} catch (SolrServerException | IndexOutOfBoundsException e) {
			e.printStackTrace();
		}

		return null;
	}


	public String getProcedureUrlByKey(String procedureStableKey) {

		return config.get("drupalBaseUrl") + "/impress/impress/displaySOP/" + procedureStableKey;
	}


	/**
	 * Return a string that either contains the name of the procedure if the
	 * procedure key cannot be found, or a string that has an HTML anchor tag
	 * ready to be used in a chart.
	 * 
	 * @param procedureName
	 *            the name of the procedure
	 * @param procedureStableId
	 *            the IMPReSS stable ID of the procedure
	 * @return a string that either has the name of the procedure or and HTML
	 *         anchor tag to be used by the chart
	 */
	/* Temp comment out as pipeline core does not contian all procedures. 
	public String getAnchorForProcedure(String procedureName, String procedureStableId) {

		String anchor = procedureName;
		String procKey = getProcedureStableKey(procedureStableId).toString();
		if (procKey != null) {
			anchor = String.format("<a href=\"%s\">%s</a>", getProcedureUrlByKey(procKey), procedureName);
		}

		return anchor;
	}

*/	
	public String getPipelineUrlByStableId(String stableId){
		List<Integer> pipelineKey = getPipelineStableKey(stableId);
		if (pipelineKey != null && pipelineKey.size()>0){
			return config.get("drupalBaseUrl") + "/impress/procedures/" + pipelineKey.get(0);
		}
		else return "#";
	}
	
	public Map<String,String> getParameterStableIdToAbnormalMaMap(){
		//http://ves-ebi-d0.ebi.ac.uk:8090/mi/impc/dev/solr/pipeline/select?q=*:*&facet=true&facet.field=parameter_name&facet.mincount=1&fq=(abnormal_ma_id:*)&rows=100
		Map<String,String> idToAbnormalMaId=new HashMap<>();
		List<PipelineDTO> pipelineDtos=null;
			SolrQuery query = new SolrQuery()
				.setQuery(PipelineDTO.ABNORMAL_MA_ID + ":*" )
				.setFields(PipelineDTO.ABNORMAL_MA_ID, PipelineDTO.PARAMETER_STABLE_ID).setRows(1000000);

			QueryResponse response=null;
			try {
				response = solr.query(query);
				pipelineDtos = response.getBeans(PipelineDTO.class);
				for(PipelineDTO pipe:pipelineDtos){
					if(!idToAbnormalMaId.containsKey(pipe.getParameterStableId())){
						idToAbnormalMaId.put(pipe.getParameterStableId(),pipe.getAbnormalMaTermId());
					}
				}
			} catch (SolrServerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return idToAbnormalMaId;
	}
}
