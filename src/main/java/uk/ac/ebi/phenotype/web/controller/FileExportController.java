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
package uk.ac.ebi.phenotype.web.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import uk.ac.ebi.generic.util.ExcelWorkBook;
import uk.ac.ebi.generic.util.SolrIndex;
import uk.ac.ebi.phenotype.dao.PhenotypeCallSummaryDAO;
import uk.ac.ebi.phenotype.pojo.PhenotypeCallSummary;
import uk.ac.ebi.phenotype.pojo.PipelineSolrImpl;
import uk.ac.ebi.phenotype.pojo.SexType;
import uk.ac.ebi.phenotype.stats.ExperimentDTO;
import uk.ac.ebi.phenotype.stats.ExperimentService;

@Controller
public class FileExportController {

	private Logger log = Logger.getLogger(this.getClass().getCanonicalName());

	@Autowired
	public PhenotypeCallSummaryDAO phenotypeCallSummaryDAO;

	@Autowired
	private SolrIndex solrIndex;

	@Resource(name="globalConfiguration")
	private Map<String, String> config;

	@Autowired
	private ExperimentService experimentService;
		
	

	private String tsvDelimiter = "\t";
	//eg ESLIM_001_001_115
	private String patternStr = "(.+_\\d+_\\d+_\\d+)";
	private Pattern pattern = Pattern.compile(patternStr);
	private String europhenomeBaseUrl = "http://www.europhenome.org/databrowser/viewer.jsp?set=true&m=true&zygosity=All&compareLines=View+Data";
	private String mgpBaseUrl = "http://www.sanger.ac.uk/mouseportal/search?query=";
	
	/**
	 * <p>Export table as TSV or Excel file.</p>
	 * @param model
	 * @return
	 */	
	
	@RequestMapping(value="/export", method=RequestMethod.GET)	
	public String exportTableAsExcelTsv(		
		@RequestParam(value="externalDbId", required=true) Integer extDbId,
		@RequestParam(value="rowStart", required=false) Integer rowStart,
		@RequestParam(value="fileType", required=true) String fileType,
		@RequestParam(value="fileName", required=true) String fileName,
		@RequestParam(value="panel", required=false) String panelName,			
		@RequestParam(value="mpId", required=false) String mpId,
		@RequestParam(value="mpTerm", required=false) String mpTerm,			
		@RequestParam(value="mgiGeneId", required=false) String mgiGeneId,
		@RequestParam(value="parameterStableId", required=false) String parameterStableId, // should be filled for graph data export
		@RequestParam(value="zygosity", required=false) String zygosity, // should be filled for graph data export
		@RequestParam(value="strain", required=false) String strain, // should be filled for graph data export
		@RequestParam(value="geneSymbol", required=false) String geneSymbol,			
		@RequestParam(value="solrCoreName", required=false) String solrCoreName,
		@RequestParam(value="params", required=false) String solrParams,
		@RequestParam(value="gridFields", required=false) String gridFields,		
		@RequestParam(value="showImgView", required=false, defaultValue="false") boolean showImgView,		
		@RequestParam(value="dumpMode", required=false) String dumpMode,	
		@RequestParam(value="baseUrl", required=false) String baseUrl,	
		@RequestParam(value="sex", required=false) String sex,
		@RequestParam(value="phenotypingCenter", required=false) String phenotypingCenter,
		HttpSession session, 
		HttpServletRequest request, 
		HttpServletResponse response,
		Model model
		) throws Exception{	
		log.debug("solr params: " + solrParams);
		Workbook wb = null;
		String dataString = null;
		List<String> dataRows = new ArrayList<String> ();
		// Default to exporting 10 rows
		Integer length = 10;
		
		panelName = panelName == null ? "" : panelName; 
	
		// Excel
		if ( fileType.equals("xls") ){
			
			response.setContentType("application/vnd.ms-excel");					
			response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xls" );
			
			String sheetName = fileName;
			ExcelWorkBook Wb = null;
			
			if ( panelName.equals("geneVariants")){	
				Wb = new ExcelWorkBook(fetchGeneVariantsTitles(), fetchGeneVariantsData(mpId, extDbId), sheetName);					
			}
			else if ( panelName.equals("phenoAssoc")){
				Wb = new ExcelWorkBook(fetchPhenoAssocTitles(), fetchPhenoAssocData(mgiGeneId, extDbId), sheetName);					
			}
			else if ( !solrCoreName.isEmpty() ){					
				if (dumpMode.equals("all")){
					rowStart = 0;
					//length = parseMaxRow(solrParams); // this is the facetCount
					length = 100000;
				}
													
				JSONObject json = solrIndex.getDataTableExportRows(solrCoreName, solrParams, gridFields, rowStart, length);
				List<String> rows;
				if (!solrCoreName.equalsIgnoreCase("experiment")){
					rows = composeDataTableExportRows(solrCoreName, json, rowStart, length, showImgView, solrParams, request);
				}
				else{
					String zyg = (zygosity.equalsIgnoreCase("null")) ? null : zygosity; 
					rows = composeExperimetDataExportRows(parameterStableId, mgiGeneId, (sex.equalsIgnoreCase("null")) ? null : sex, null, zyg, strain, phenotypingCenter);
				}
				// Remove the title row (row 0) from the list and assign it to
				// the string array for the spreadsheet
				String[] titles = rows.remove(0).split("\t");				
				Wb = new ExcelWorkBook(titles, composeXlsTableData(rows), sheetName);
			}
			
			wb = Wb.fetchWorkBook();
		}
		else if ( fileType.equals("tsv") ){				
			response.setContentType("text/tsv; charset=utf-8");					 
			response.setHeader("Content-Disposition","attachment;filename=" + fileName + ".tsv");
			
			if ( panelName.equals("geneVariants") ){
				dataString = composeGeneVariantsTsvString(mpId, extDbId);
			}
			else if ( panelName.equals("phenoAssoc") ){				
				dataString = composePhenoAssocTsvString(mgiGeneId, extDbId);
			}
			else if ( !solrCoreName.isEmpty() ){

				if (dumpMode.equals("all")){
					rowStart = 0;
					length = 100000;
				}

				JSONObject json = solrIndex.getDataTableExportRows(solrCoreName, solrParams, gridFields, rowStart, length);
				if (!solrCoreName.equalsIgnoreCase("experiment")){
					dataRows = composeDataTableExportRows(solrCoreName, json, rowStart, length, showImgView, solrParams, request);}
				else{
					dataRows = composeExperimetDataExportRows(parameterStableId, mgiGeneId, (sex.equalsIgnoreCase("null")) ? null : sex, null, zygosity, strain, phenotypingCenter);
				}
			}
		}
		
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Expires", "0"); 
		
		try {				
			
			if ( fileType.equals("tsv") ){
				// ServletOutputStream output = response.getOutputStream();
				  
				// ckc note: switch to use getWriter() so that we don't get error like
				// java.io.CharConversionException: Not an ISO 8859-1 character
				// and if we do, the error will cause the dump to end prematurely 
				// and we may not get the full rows (depending on which row causes error)        
				PrintWriter output = response.getWriter();
				for (String line : dataRows){
					output.println(line);
				}
//				StringBuffer sb = new StringBuffer();						
//				sb.append(dataString);			
				
				/*
				InputStream in = new ByteArrayInputStream(sb.toString().getBytes("UTF-8"));
	 
				byte[] outputByte = new byte[1024];
				//copy binary content to output stream
				while(in.read(outputByte, 0, 1024) != -1) {
					output.write(outputByte, 0, 1024);
				}
				
				in.close();			*/		
				output.flush();
				output.close();
			}
			else if ( fileType.equals("xls") ) {	
				ServletOutputStream output = response.getOutputStream();
				try {
					wb.write(output);
				}       
				catch (IOException ioe) { 
					log.error("Error: " + ioe.getMessage());
				}
			}	
		}
		catch(Exception e){
			log.error("Error: " + e.getMessage());
		}
		return null;		
	}
	private int parseMaxRow(String solrParams){		
		String[] paramsList = solrParams.split("&"); 
		int facetCount = 0;
		for ( String str : paramsList ){			
			if ( str.startsWith("facetCount=") ){
				String[] vals = str.split("=");
				facetCount = Integer.parseInt(vals[1]);				
			}
		}		
		return facetCount;
	}

	public String[][] composeXlsTableData(List<String> rows) {

		int rowNum = rows.size();// - 1; // omit title row
		int colNum = (rows.size() > 0) ? rows.get(0).split("\t").length : 0;
		String[][] tableData = new String[rowNum][colNum];
		
		// add one to omit title row
		for( int i=0; i<rowNum; i++ ){

			String[] colVals = rows.get(i).split("\t");
			for (int j=0; j<colVals.length; j++) {
				tableData[i][j] = colVals[j];
			}
		}
		return tableData;
	}
	
	public List<String> composeExperimetDataExportRows(String parameterStableId, String geneAccession, String gender, Integer organisationId, String zygosity, String strain, String phenotypingCenter) throws SolrServerException, IOException, URISyntaxException{
		List<String> rows = new ArrayList<String>();
		SexType sex = null;
		if (gender != null)
			sex = SexType.valueOf(gender);
		List<ExperimentDTO> experimentList = new ArrayList<ExperimentDTO> ();
		if (parameterStableId.contains("\t")){
			String [] params = parameterStableId.split("\t");
			for (int k = 0; k < params.length; k++){
				experimentList = experimentService.getExperimentDTO(params[k], geneAccession, sex, organisationId, zygosity, strain, phenotypingCenter);
				for (ExperimentDTO experiment : experimentList) { 
					rows.addAll(experiment.getTabbedToString()) ;
				}
				rows.add("\n");
			}
		}
		else {
			experimentList = experimentService.getExperimentDTO(parameterStableId, geneAccession, sex, organisationId, zygosity, strain, phenotypingCenter);
			for (ExperimentDTO experiment : experimentList) { 
				rows.addAll(experiment.getTabbedToString()) ;
			}
		}
		
		return rows;
	}

	public List<String> composeDataTableExportRows(String solrCoreName, JSONObject json, Integer iDisplayStart, Integer iDisplayLength, boolean showImgView, String solrParams, HttpServletRequest request){
		List<String> rows = null;

		if (solrCoreName.equals("gene") ){			
			rows = composeGeneDataTableRows(json);
		}
		else if ( solrCoreName.equals("mp") ){			
			rows = composeMpDataTableRows(json);
		}
		else if ( solrCoreName.equals("ma") ){			
			rows = composeMaDataTableRows(json);
		}
		else if ( solrCoreName.equals("pipeline") ){
			rows = composeProcedureDataTableRows(json);
		}
		else if ( solrCoreName.equals("images") ){
			rows = composeImageDataTableRows(json,  iDisplayStart,  iDisplayLength, showImgView, solrParams, request);
		}
		else if ( solrCoreName.equals("disease") ){
			rows = composeDiseaseDataTableRows(json);
		}
		else if ( solrCoreName.equals("genotype-phenotype") ){
			rows = composeGPDataTableRows(json, request);
		}
		return rows;
	}
	
	// Export for tables on gene  & phenotype page
	private List<String> composeGPDataTableRows(JSONObject json, HttpServletRequest request){ 
		JSONArray docs = json.getJSONObject("response").getJSONArray("docs");	
		List<String> rowData = new ArrayList<String>();
		PipelineSolrImpl pipe = new PipelineSolrImpl(config);
		// add respective table header 
		// from the phenotype core we export both the table on gene & phenotype page so we need to check on which file we are
		
		if (request.getParameter("page").equalsIgnoreCase("gene")){
			rowData.add("Phenotype\tAllele\tZygosity\tSex\tProcedure / Parameter\tSource\tGraph"); 
			for (int i=0; i<docs.size(); i++) {			
				List<String> data = new ArrayList<String>();
				JSONObject doc = docs.getJSONObject(i);
				if (!doc.getString("resource_fullname").equalsIgnoreCase("International Mouse Phenotyping Consortium")){
					data.add(doc.getString("mp_term_name"));
					if (doc.containsKey("allele_symbol"))
						data.add(doc.getString("allele_symbol"));
					else data.add("");
					data.add(doc.getString("zygosity"));
					data.add(doc.getString("sex"));
					data.add(doc.getString("procedure_name") + " / " + doc.getString("parameter_name"));
					data.add(doc.getString("resource_fullname"));
					String graphUrl = "\"\"";
					if ((doc.getString("resource_fullname")).equalsIgnoreCase("EuroPhenome")){ // only show links for Europhenome
					// also don't show graph links for derived parameters
					//		if (!pipe.getParameterByStableId(doc.getString("parameter_stable_id"), "").getDerivedFlag()){
								graphUrl = request.getParameter("baseUrl").replace("/genes/", "/stats/genes/") + "?parameterId=" ;
								graphUrl += doc.getString("parameter_stable_id") + "&gender=" + doc.getString("sex");
								graphUrl += "&zygosity=" + doc.getString("zygosity");
								if (doc.containsKey("phenotyping_center")){
									graphUrl += "&phenotyping_center=" +doc.getString("phenotyping_center");
								}
					//		}
					}
					data.add(graphUrl);
					String line = StringUtils.join(data, "\t");
					if (!rowData.contains(line)){
						rowData.add(line);
					}else 
		//				System.out.println("Duplicate row " + line);
						;
				}
			}
		}
		else if (request.getParameter("page").equalsIgnoreCase("phenotype")){
			boolean isTopLevel = (!docs.getJSONObject(0).containsKey("top_level_mp_term_name"));
			// table is different for top level terms and the rest
			if (!isTopLevel){
				// top_level ones don't have this field
				rowData.add("Gene\tAllele\tZygosity\tSex\tProcedure / Parameter\tSource\tGraph"); 
			}
			else {
				rowData.add("Gene\tAllele\tZygosity\tSex\tPhenotype / Parameter\tSource\tGraph"); 
			}
			for (int i=0; i<docs.size(); i++) {		
				JSONObject doc = docs.getJSONObject(i);
				// for some reason we need to filter out the IMPC entries.
				if (!doc.getString("resource_fullname").equalsIgnoreCase("International Mouse Phenotyping Consortium")){
					List<String> data = new ArrayList<String>();
					data.add(doc.getString("marker_symbol"));
					if (doc.containsKey("allele_symbol"))
						data.add(doc.getString("allele_symbol"));
					else data.add("");
					data.add(doc.getString("zygosity"));
					data.add(doc.getString("sex"));
					if (isTopLevel)
					{
						data.add(doc.getString("mp_term_name") + " / " + doc.getString("parameter_name"));
					}
					else {
						data.add(doc.getString("procedure_name") + " / " + doc.getString("parameter_name"));
					}
					data.add(doc.getString("resource_fullname"));
					String graphUrl = "\"\"";
					// TODO check, this might need to change
					if ((doc.getString("resource_fullname")).equalsIgnoreCase("EuroPhenome")){ // only show links for Europhenome
					//		if (!pipe.getParameterByStableId(doc.getString("parameter_stable_id"), "").getDerivedFlag()){
								graphUrl = request.getParameter("baseUrl").replace("/phenotypes/", "/stats/genes/") + "?parameterId=" ;
								graphUrl += doc.getString("parameter_stable_id") + "&gender=" + doc.getString("sex");
								graphUrl += "&zygosity=" + doc.getString("zygosity") ;
								if (doc.containsKey("phenotyping_center")){
									graphUrl += "&phenotyping_center=" +doc.getString("phenotyping_center");
								}
					//		}
					}
					data.add(graphUrl);
					rowData.add(StringUtils.join(data, "\t"));
				}
			}
		}
		return rowData;
	}
	
	private List<String> composeProcedureDataTableRows(JSONObject json){
		JSONArray docs = json.getJSONObject("response").getJSONArray("docs");	
		
		List<String> rowData = new ArrayList<String>();
		rowData.add("Parameter\tProcedure\tPipeline"); // column names	
		
		for (int i=0; i<docs.size(); i++) {			
			List<String> data = new ArrayList<String>();
			JSONObject doc = docs.getJSONObject(i);
			data.add(doc.getString("parameter_name"));
			data.add(doc.getString("procedure_name"));
			data.add(doc.getString("pipeline_name"));
			rowData.add(StringUtils.join(data, "\t"));
		}
		return rowData;
	}
	
	private List<String> composeImageDataTableRows(JSONObject json, Integer iDisplayStart, Integer iDisplayLength, boolean showImgView, String solrParams, HttpServletRequest request){
		
		String serverName = request.getServerName();
		
		String mediaBaseUrl = config.get("mediaBaseUrl");
		
		List<String> rowData = new ArrayList<String>();
				
		if (showImgView){
			
			System.out.println("MODE: imgview " + showImgView);
			JSONArray docs = json.getJSONObject("response").getJSONArray("docs");
			rowData.add("Annotation_term\tAnnotation_id\tProcedure\tGene_Symbol\tImage_path"); // column names	
			
			for (int i=0; i<docs.size(); i++) {			
				List<String> data = new ArrayList<String>();
				JSONObject doc = docs.getJSONObject(i);
								
				String[] fields = {"annotationTermName", "annotationTermId", "expName", "symbol_gene"};
				for( String fld : fields ){
					if(doc.has(fld)) {
						List<String> lists = new ArrayList<String>();
						JSONArray list = doc.getJSONArray(fld);
						for(int l=0; l<list.size();l++) {					
							lists.add(list.getString(l));
						}
						data.add(StringUtils.join(lists, "|")); 
					}
					else {
						data.add("NA");
					}				
				}
				
				data.add(mediaBaseUrl + "/" + doc.getString("largeThumbnailFilePath"));
				rowData.add(StringUtils.join(data, "\t"));
			}
		}
		else {
			System.out.println("MODE: imgview " + showImgView);
			// annotation view
			// annotation view: images group by annotationTerm per row
			rowData.add("Annotation_type\tAnnotation_name\tRelated_image_count\tUrl_to_images"); // column names	
			JSONObject facetFields = json.getJSONObject("facet_counts").getJSONObject("facet_fields");
						
			JSONArray sumFacets = solrIndex.mergeFacets(facetFields);
						
			int numFacets = sumFacets.size();		
			int quotient = (numFacets/2)/iDisplayLength -((numFacets/2)%iDisplayLength) / iDisplayLength;
			int remainder = (numFacets/2) % iDisplayLength;
			int start = iDisplayStart*2;  // 2 elements(name, count), hence multiply by 2
	        int end =  iDisplayStart == quotient*iDisplayLength ? (iDisplayStart+remainder)*2 : (iDisplayStart+iDisplayLength)*2;  
				        
			for (int i=start; i<end; i=i+2){
				List<String> data = new ArrayList<String>();
				// array element is an alternate of facetField and facetCount	
				
				String[] names = sumFacets.get(i).toString().split("_");
				if (names.length == 2 ){  // only want facet value of xxx_yyy
					String annotName = names[0];
					Map<String, String> hm = solrIndex.renderFacetField(names, request.getParameter("baseUrl")); //MA:xxx, MP:xxx, MGI:xxx, exp					
									
					data.add(hm.get("label").toString());
					data.add(annotName);
					//data.add(hm.get("link").toString());
									
					String imgCount = sumFacets.get(i+1).toString();	
					data.add(imgCount);
					
					String facetField = hm.get("field").toString();
					
					String imgSubSetLink = serverName + request.getAttribute("baseUrl") + "/images?" + solrParams + "q=*:*&fq=" + facetField + ":\"" + names[0] + "\"";						
					data.add(imgSubSetLink);
					rowData.add(StringUtils.join(data, "\t"));
				}
			}	
		}		
		
		return rowData;
	}
	
	private List<String> composeMpDataTableRows(JSONObject json){
		JSONArray docs = json.getJSONObject("response").getJSONArray("docs");	
		
		List<String> rowData = new ArrayList<String>();
		rowData.add("MP_term\tMP_id\tMP_definition\tTop_level_MP_term"); // column names	
		
		for (int i=0; i<docs.size(); i++) {			
			List<String> data = new ArrayList<String>();
			JSONObject doc = docs.getJSONObject(i);
			
			data.add(doc.getString("mp_term"));
			data.add(doc.getString("mp_id"));				
					
			if(doc.has("mp_definition")) {				
				data.add(doc.getString("mp_definition"));					
			}
			else {
				data.add("NA");
			}
			
			if(doc.has("top_level_mp_term")) {
				List<String> tops = new ArrayList<String>();
				JSONArray top = doc.getJSONArray("top_level_mp_term");
				for(int t=0; t<top.size();t++) {					
					tops.add(top.getString(t));
				}
				data.add(StringUtils.join(tops, "|")); 			
			}
			else {
				data.add("NA");
			}
			
			rowData.add(StringUtils.join(data, "\t"));
		}
		return rowData;
	}
	
	private List<String> composeMaDataTableRows(JSONObject json){
		JSONArray docs = json.getJSONObject("response").getJSONArray("docs");	
		
		List<String> rowData = new ArrayList<String>();
		rowData.add("MA_term\tMA_id"); // column names	
		
		for (int i=0; i<docs.size(); i++) {			
			List<String> data = new ArrayList<String>();
			JSONObject doc = docs.getJSONObject(i);
			
			data.add(doc.getString("ma_term"));
			data.add(doc.getString("ma_id"));				
				
			// will have these cols coming later
			/*if(doc.has("mp_definition")) {				
				data.add(doc.getString("mp_definition"));					
			}
			else {
				data.add("NA");
			}
			
			if(doc.has("top_level_mp_term")) {
				List<String> tops = new ArrayList<String>();
				JSONArray top = doc.getJSONArray("top_level_mp_term");
				for(int t=0; t<top.size();t++) {					
					tops.add(top.getString(t));
				}
				data.add(StringUtils.join(tops, "|")); 			
			}
			else {
				data.add("NA");
			}*/
			
			rowData.add(StringUtils.join(data, "\t"));
		}
		return rowData;
	}
	private List<String> composeGeneDataTableRows(JSONObject json){
				
		JSONArray docs = json.getJSONObject("response").getJSONArray("docs");		
				
		List<String> rowData = new ArrayList<String>();
		rowData.add("Marker symbol\tHuman ortholog\tMaker name\tSynonym\tMouse production status\tPhenotyping status"); // column names		
		
		for (int i=0; i<docs.size(); i++) {			
			List<String> data = new ArrayList<String>();
			JSONObject doc = docs.getJSONObject(i);
			
			data.add(doc.getString("marker_symbol"));
			
			if(doc.has("human_gene_symbol")) {				
				List<String> hsynData = new ArrayList<String>();
				JSONArray hs = doc.getJSONArray("human_gene_symbol");
				for(int s=0; s<hs.size();s++) {					
					hsynData.add(hs.getString(s));
				}
				data.add(StringUtils.join(hsynData, "|")); // use | as a multiValue separator in CSV output
			}
			else {
				data.add("NA");				
			}	
			
			// Sanger problem, they should have use string for marker_name and not array
			//data.add(doc.getJSONArray("marker_name").getString(0));
			// now corrected using httpdatasource in dataImportHandler
			data.add(doc.getString("marker_name"));
			
			if(doc.has("marker_synonym")) {
				List<String> synData = new ArrayList<String>();
				JSONArray syn = doc.getJSONArray("marker_synonym");
				for(int s=0; s<syn.size();s++) {					
					synData.add(syn.getString(s));
				}
				data.add(StringUtils.join(synData, "|")); // use | as a multiValue separator in CSV output
			}
			else {
				//data.add("NA");
			}			
			
			// mouse production status
			data.add(doc.getString("status"));			
			
			// phenotyping status
			data.add(solrIndex.deriveLatestPhenotypingStatus(doc));
			
			// put together as tab delimited
			rowData.add(StringUtils.join(data, "\t"));			
		}		
		
		return rowData;		
	}

	private List<String> composeDiseaseDataTableRows(JSONObject json){
		JSONArray docs = json.getJSONObject("response").getJSONArray("docs");	
		
		List<String> rowData = new ArrayList<String>();
		rowData.add("Disease id\tDisease name\tSource\tCurated genes in human\tCurated genes in mouse (MGI)\tCandidate genes by phenotype (MGP)\tCandidate genes by phenotype (MGI)"); // column names	
		
		for (int i=0; i<docs.size(); i++) {			
			List<String> data = new ArrayList<String>();
			JSONObject doc = docs.getJSONObject(i);
			data.add(doc.getString("disease_id"));
			data.add(doc.getString("disease_term"));
			data.add(doc.getString("disease_source"));
			data.add(doc.getString("human_curated").equals("1") ? "Yes" : "-");
			data.add(doc.getString("mouse_curated").equals("1") ? "Yes" : "-");
			data.add((doc.getString("impc_predicted").equals("1") || doc.getString("impc_predicted_in_locus").equals("1")) ? "Yes" : "-");
			data.add((doc.getString("mgi_predicted").equals("1") || doc.getString("mgi_predicted_in_locus").equals("1")) ? "Yes" : "-");
						
			rowData.add(StringUtils.join(data, "\t"));
		}
		return rowData;
	}

	private String[] fetchGeneVariantsTitles(){		
		String[] titles = {"Gene", "Allele Symbol", "Zygosity", "Sex", "Procedure", "Data"};		
		return titles;
	}
	private String[] fetchPhenoAssocTitles(){
		String[] titles = {"Phenotype", "Allele", "Zygosity", "Sex", "Data"};		
		return titles;
	}
	private String[][] fetchPhenoAssocData(String mgiGeneId, int extDbId){
		List<PhenotypeCallSummary> summaries = phenotypeCallSummaryDAO.getPhenotypeCallByAccession(mgiGeneId, extDbId);			
		int rowNum = summaries.size();
		String[][] tableData = new String[rowNum][5];	
				
		int i=0;		
		for(PhenotypeCallSummary p : summaries){					
			tableData[i][0] = p.getPhenotypeTerm().getName();
			tableData[i][1] = p.getAllele().getSymbol();
			tableData[i][2] = p.getZygosity().name();
			tableData[i][3] = p.getSex().name();
			tableData[i][4] = composeLegacyDataLink(p);			
			i++;			
		}
		return tableData;
	}
	private String[][] fetchGeneVariantsData(String mpId, int extDbId){
		
		List<PhenotypeCallSummary> summaries = phenotypeCallSummaryDAO.getPhenotypeCallByMPAccession(mpId, extDbId);			
		int rowNum = summaries.size();
		String[][] tableData = new String[rowNum][6];	
		
		int i=0;		
		for(PhenotypeCallSummary p : summaries){
			
			// only want phenotypes that link to a gene
			if ( p.getGene() != null && p.getStrain() != null ){
			
				tableData[i][0] = p.getGene().getSymbol();
				tableData[i][1] = p.getAllele().getSymbol();
				tableData[i][2] = p.getZygosity().name();
				tableData[i][3] = p.getSex().name();
				tableData[i][4] = p.getProcedure().getName();
				tableData[i][5] = composeLegacyDataLink(p);
				i++;
			}
		}
		return tableData;
	}	
	
	private String composeGeneVariantsTsvString(String mpId, int extDbId){	
		
		List<String> rows = new ArrayList<String>();		
		rows.add(StringUtils.join(fetchGeneVariantsTitles(), tsvDelimiter)); //"Gene, Allele Symbol, Zygosity, Sex, Procedure, Data");		
		
		List<PhenotypeCallSummary> summaries = phenotypeCallSummaryDAO.getPhenotypeCallByMPAccession(mpId, extDbId);
		for(PhenotypeCallSummary p : summaries) {
				
			// only want phenotypes that link to a gene
			if ( p.getGene() != null && p.getStrain() != null ){
				List<String> data = new ArrayList<String>();			
				data.add(p.getGene().getSymbol());
				data.add(p.getAllele().getSymbol());
				data.add(p.getZygosity().name());
				data.add(p.getSex().name());
				data.add(p.getProcedure().getName());
								
				// compose legacy data link
				data.add(composeLegacyDataLink(p));	
				
				rows.add(StringUtils.join(data, tsvDelimiter));
			}
		}
		
		return StringUtils.join(rows, "\n");
	}
	
	private String composeLegacyDataLink(PhenotypeCallSummary p){
		String linkParam = "";
		String extId = p.getExternalId() + "";
		String sex = p.getSex().name();
	
		String procedure_sid = p.getProcedure().getStableId();
		String parameter_sid = p.getParameter().getStableId();
		Matcher matcher = this.pattern.matcher(parameter_sid);
		
		if (p.getDatasource().getName().equals("WTSI Mouse Genetics Project") && p.getGene() != null) {
			linkParam = mgpBaseUrl + p.getGene().getSymbol();
		} else if (matcher.find()) {
			String params = "&l=" + extId + "&x=" + sex + "&p=" + procedure_sid + "&pid_" + matcher.group(1) + "=on";
			linkParam = europhenomeBaseUrl + params;
		} else {			
			linkParam = "No link to legacy data";
		}		
	
		return linkParam;
	}
	
	private String composePhenoAssocTsvString(String mgiGeneId, int extDbId){	
	
		List<String> rows = new ArrayList<String>();		
		rows.add(StringUtils.join(fetchPhenoAssocTitles(), tsvDelimiter)); //"Phenotype, Allele, Zygosity, Sex");
				
		List<PhenotypeCallSummary> summaries = phenotypeCallSummaryDAO.getPhenotypeCallByAccession(mgiGeneId, extDbId);
		for(PhenotypeCallSummary p : summaries) {							
			List<String> data = new ArrayList<String>();			
			data.add(p.getPhenotypeTerm().getName());
			data.add(p.getAllele().getSymbol());
			data.add(p.getZygosity().name());
			data.add(p.getSex().name());
			
			// compose legacy data link
			data.add(composeLegacyDataLink(p));
			
			rows.add(StringUtils.join(data, tsvDelimiter));
		}		
		return StringUtils.join(rows, "\n");
	}

}
