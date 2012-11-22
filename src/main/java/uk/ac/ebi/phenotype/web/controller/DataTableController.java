/**
 * Copyright © 2011-2012 EMBL - European Bioinformatics Institute
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
package uk.ac.ebi.phenotype.web.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import uk.ac.ebi.phenotype.util.SolrIndex;


@Controller
public class DataTableController implements BeanFactoryAware {

	private Logger log = Logger.getLogger(this.getClass().getCanonicalName());
	private BeanFactory bf;

	//@Autowired
	/*public FileExportController(PhenotypeCallSummaryDAO phenotypeCallSummaryDAO) { 
		this.phenotypeCallSummaryDAO = phenotypeCallSummaryDAO;	
	}*/

	/**
	 * <p>Return jQuery dataTable from server-side for lazy-loading.</p>
	 * @param  
		
	bRegex=false
	bRegex_0=false
	bRegex_1=false
	bRegex_2=false
	
	bSearchable_0=true
	bSearchable_1=true
	bSearchable_2=true
	
	bSortable_0=true
	bSortable_1=true
	bSortable_2=true
	
	iColumns=3
	
	* for paging: 
		iDisplayLength=10
		iDisplayStart=0
		
	* for sorting:	
		iSortCol_0=0
		iSortingCols=1
		
	* for filtering:
		sSearch=
		sSearch_0=
		sSearch_1=
		sSearch_2=
			
	mDataProp_0=0
	mDataProp_1=1
	mDataProp_2=2
	
	sColumns=
	sEcho=1
			
	sSortDir_0=asc
	 * @return 
	* @return
	*/
	
	@RequestMapping(value="/dataTable", method=RequestMethod.GET)	
	public @ResponseBody String dataTableJson(		
			@RequestParam(value="iDisplayStart", required=true) int iDisplayStart,
			@RequestParam(value="iDisplayLength", required=true) int iDisplayLength,	
			@RequestParam(value="solrParams", required=true) String solrParams,			
			HttpServletRequest request, 
			Model model){
		
			log.debug("CHK: " + solrParams);
			
			JSONObject jParams = (JSONObject) JSONSerializer.toJSON(solrParams);
			log.debug("solr: " + jParams.getString("params"));
			
			String contextPath = (String) request.getAttribute("baseUrl");						
			String solrCoreName = jParams.getString("solrCoreName");
			String query = "";				
			String mode = jParams.getString("mode");
			String solrParamStr = jParams.getString("params");
			boolean showImgView = false;
			try {
				showImgView = jParams.getBoolean("showImgView");
			}
			catch (Exception e){
				 //e.printStackTrace();
			}
			Map config = (Map) bf.getBean("globalConfiguration");

			SolrIndex solrIndex = new SolrIndex(query, solrCoreName, solrParamStr, mode, iDisplayStart, iDisplayLength, showImgView, request, config);			
			String jsonStr = solrIndex.fetchDataTableJson(contextPath);
			
			return jsonStr; 
	}		
	@Override
	public void setBeanFactory(BeanFactory arg0) throws BeansException {
		this.bf=arg0;
		
	}
}
