/**
 * Copyright © 2011-2014 EMBL - European Bioinformatics Institute
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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.solr.client.solrj.SolrServerException;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import uk.ac.ebi.generic.util.ExcelWorkBook;
import uk.ac.ebi.generic.util.SolrIndex;
import uk.ac.ebi.phenotype.dao.AlleleDAO;
import uk.ac.ebi.phenotype.dao.OrganisationDAO;
import uk.ac.ebi.phenotype.dao.PhenotypeCallSummaryDAO;
import uk.ac.ebi.phenotype.dao.PhenotypePipelineDAO;
import uk.ac.ebi.phenotype.dao.StrainDAO;
import uk.ac.ebi.phenotype.pojo.Allele;
import uk.ac.ebi.phenotype.pojo.Organisation;
import uk.ac.ebi.phenotype.pojo.Parameter;
import uk.ac.ebi.phenotype.pojo.PhenotypeCallSummary;
import uk.ac.ebi.phenotype.pojo.PhenotypeCallSummaryDAOReadOnly;
import uk.ac.ebi.phenotype.pojo.Pipeline;
import uk.ac.ebi.phenotype.pojo.Procedure;
import uk.ac.ebi.phenotype.pojo.SexType;
import uk.ac.ebi.phenotype.pojo.Strain;
import uk.ac.ebi.phenotype.service.ExperimentService;
import uk.ac.ebi.phenotype.stats.ExperimentDTO;
import uk.ac.ebi.phenotype.stats.ObservationDTO;
import uk.ac.ebi.phenotype.util.PhenotypeFacetResult;
import uk.ac.ebi.phenotype.web.pojo.PhenotypeRow;

@Controller
public class FileExportController {

    private Logger log = Logger.getLogger(this.getClass().getCanonicalName());

    @Autowired
    public PhenotypeCallSummaryDAO phenotypeCallSummaryDAO;

    @Autowired
    private SolrIndex solrIndex;

    @Autowired
    private PhenotypePipelineDAO ppDAO;

    @Resource(name = "globalConfiguration")
    private Map<String, String> config;

    @Autowired
    private ExperimentService experimentService;

    @Autowired
    OrganisationDAO organisationDao;

    @Autowired
    StrainDAO strainDAO;

    @Autowired
    AlleleDAO alleleDAO;

    @Autowired
    private PhenotypeCallSummaryDAOReadOnly phenoDAO;

    
    /**
     * Return a TSV formatted response which contains all datapoints
     * @param phenotypingCenterParameter
     * @param pipelineStableId
     * @param procedureStableId
     * @param parameterStableId
     * @param alleleAccession
     * @param sexesParameter
     * @param zygositiesParameter
     * @param strainParameter
     * @return
     * @throws SolrServerException
     * @throws IOException
     * @throws URISyntaxException
     */
    @ResponseBody
    @RequestMapping(value = "/exportraw", method = RequestMethod.GET)
    public String getExperimentalData(
            @RequestParam(value = "phenotyping_center", required = true) String phenotypingCenterParameter,
            @RequestParam(value = "pipeline_stable_id", required = true) String pipelineStableId,
            @RequestParam(value = "procedure_stable_id", required = true) String procedureStableId,
            @RequestParam(value = "parameter_stable_id", required = true) String parameterStableId,
            @RequestParam(value = "allele_accession", required = true) String alleleAccession,
            @RequestParam(value = "sex", required = false) String[] sexesParameter,
            @RequestParam(value = "zygosity", required = false) String[] zygositiesParameter,
            @RequestParam(value = "strain", required = false) String strainParameter
            ) throws SolrServerException, IOException, URISyntaxException {

        Organisation phenotypingCenter = organisationDao.getOrganisationByName(phenotypingCenterParameter);
        Pipeline pipeline = ppDAO.getPhenotypePipelineByStableId(pipelineStableId);
        Parameter parameter = ppDAO.getParameterByStableId(parameterStableId);
        Allele allele = alleleDAO.getAlleleByAccession(alleleAccession);
        SexType sex = (sexesParameter !=null && sexesParameter.length > 1) ? SexType.valueOf(sexesParameter[0]) : null;
        List<String> zygosities = (zygositiesParameter==null) ? null : Arrays.asList(zygositiesParameter);
        String center = phenotypingCenter.getName();
        Integer centerId = phenotypingCenter.getId();
        String geneAcc = allele.getGene().getId().getAccession();
        String alleleAcc = allele.getId().getAccession();

        String strainAccession = null;
        if (strainParameter != null) {
            Strain s = strainDAO.getStrainByName(strainParameter);
            if (s != null) {
                strainAccession = s.getId().getAccession();
            }
        }

        List<ExperimentDTO> experiments = experimentService.getExperimentDTO(parameter.getId(), pipeline.getId(), geneAcc, sex, centerId, zygosities, strainAccession, null, Boolean.FALSE, alleleAcc);
        
        List<String> rows = new ArrayList<>();
        rows.add(StringUtils.join(new String[] { "Experiment", "Center", "Pipeline", "Procedure", "Parameter", "Strain", "Colony", "Gene", "Allele", "MetadataGroup", "Zygosity", "Sex", "AssayDate", "Value" }, ", "));
        
        Integer i=1;
        
        for (ExperimentDTO experiment : experiments) {

            // Adding all data points to the export
            Set<ObservationDTO> observations = experiment.getControls();
            observations.addAll(experiment.getMutants());

            for (ObservationDTO observation : observations) {
                List<String> row = new ArrayList<>();
                row.add("Exp"+i.toString());
                row.add(center);
                row.add(pipelineStableId);
                row.add(procedureStableId);
                row.add(parameterStableId);
                row.add(observation.getStrain());
                row.add((observation.getGroup().equals("control"))?"+/+":observation.getColonyId());
                row.add((observation.getGroup().equals("control"))?"\"\"":geneAcc);
                row.add((observation.getGroup().equals("control"))?"\"\"":alleleAcc);
                row.add((observation.getMetadataGroup()!=null && !observation.getMetadataGroup().isEmpty()) ? observation.getMetadataGroup():"\"\"");
                row.add((observation.getZygosity() != null && !observation.getZygosity().isEmpty()) ? observation.getZygosity():"\"\"");
                row.add(observation.getSex());
                row.add(observation.getDateOfExperimentString());
                
                String dataValue = observation.getCategory();
                if(dataValue==null) {
                    dataValue = observation.getDataPoint().toString();
                }
                
                row.add(dataValue);

                rows.add(StringUtils.join(row, ", "));
            }
            
            // Next experiment
            i++;
        }

        return StringUtils.join(rows, "\n");
    }

	/**
	 * <p>Export table as TSV or Excel file.</p>
	 * @param model
	 * @return
	 */	
	
	@RequestMapping(value="/export", method=RequestMethod.GET)	
	public String exportTableAsExcelTsv(		
		@RequestParam(value="allele", required=false) String[] allele,
		@RequestParam(value="externalDbId", required=true) Integer extDbId,
		@RequestParam(value="rowStart", required=false) Integer rowStart,
		@RequestParam(value="fileType", required=true) String fileType,
		@RequestParam(value="fileName", required=true) String fileName,
		@RequestParam(value="panel", required=false) String panelName,			
		@RequestParam(value="mpId", required=false) String mpId,
		@RequestParam(value="mpTerm", required=false) String mpTerm,			
		@RequestParam(value="mgiGeneId", required=false) String [] mgiGeneId,
		@RequestParam(value="parameterStableId", required=false) String []parameterStableId, // should be filled for graph data export
		@RequestParam(value="zygosity", required=false) String []zygosities, // should be filled for graph data export
		@RequestParam(value="strains", required=false) String[] strains, // should be filled for graph data export
		@RequestParam(value="geneSymbol", required=false) String geneSymbol,			
		@RequestParam(value="solrCoreName", required=false) String solrCoreName,
		@RequestParam(value="params", required=false) String solrParams,
		@RequestParam(value="gridFields", required=false) String gridFields,		
		@RequestParam(value="showImgView", required=false, defaultValue="false") boolean showImgView,		
		@RequestParam(value="dumpMode", required=false) String dumpMode,	
		@RequestParam(value="baseUrl", required=false) String baseUrl,	
		@RequestParam(value="sex", required=false) String sex,
		@RequestParam(value="phenotypingCenter", required=false) String[] phenotypingCenter,
		@RequestParam(value="pipelineStableId", required=false) String[] pipelineStableId,
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

		ArrayList<Integer> phenotypingCenterIds = new ArrayList<>();
		try {
			for (int i = 0; i < phenotypingCenter.length; i++ ){
				phenotypingCenterIds.add(organisationDao.getOrganisationByName(phenotypingCenter[i].replaceAll("%20", " " )).getId());
			}
		} catch (NullPointerException e) {
			log.error("Cannot find organisation ID for org with name " + phenotypingCenter);
		}
		
		panelName = panelName == null ? "" : panelName; 
	
		
		if ( !solrCoreName.isEmpty() ){					
			if (dumpMode.equals("all")){
				rowStart = 0;
				//length = parseMaxRow(solrParams); // this is the facetCount
				length = 100000;
			}
								
			if (solrCoreName.equalsIgnoreCase("experiment")){
				List<String> zygList=null;
				if(zygosities!=null) {
					zygList=Arrays.asList(zygosities);
				}
				String s = (sex.equalsIgnoreCase("null")) ? null : sex;
				dataRows = composeExperimentDataExportRows(parameterStableId, mgiGeneId, allele, s, phenotypingCenterIds, zygList, strains, pipelineStableId);
			}
			else if (solrCoreName.equalsIgnoreCase("genotype-phenotype")){
				if (mgiGeneId !=null)
					dataRows = composeDataRowGeneOrPhenPage(mgiGeneId[0], request.getParameter("page"));
				else if (mpId != null)
					dataRows = composeDataRowGeneOrPhenPage(mpId, request.getParameter("page"));
			}
			else{
				JSONObject json = solrIndex.getDataTableExportRows(solrCoreName, solrParams, gridFields, rowStart, length);
				dataRows = composeDataTableExportRows(solrCoreName, json, rowStart, length, showImgView, solrParams, request);
			}
		}
		
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Expires", "0"); 
		
		try {				
			
			if ( fileType.equals("tsv") ){

				response.setContentType("text/tsv; charset=utf-8");					 
				response.setHeader("Content-Disposition","attachment;filename=" + fileName + ".tsv");
				// ServletOutputStream output = response.getOutputStream();
				  
				// ckc note: switch to use getWriter() so that we don't get error like
				// java.io.CharConversionException: Not an ISO 8859-1 character
				// and if we do, the error will cause the dump to end prematurely 
				// and we may not get the full rows (depending on which row causes error)        
				PrintWriter output = response.getWriter();
				for (String line : dataRows){
					output.println(line);
				}
	
				output.flush();
				output.close();
			}
			else if ( fileType.equals("xls") ) {	
				
				response.setContentType("application/vnd.ms-excel");					
				response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xls" );
				
				String sheetName = fileName;
				ExcelWorkBook Wb = null;
			
				// Remove the title row (row 0) from the list and assign it to
				// the string array for the spreadsheet
				String[] titles = dataRows.remove(0).split("\t");				
				Wb = new ExcelWorkBook(titles, composeXlsTableData(dataRows), sheetName);

				wb = Wb.fetchWorkBook();
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
	
	public List<String> composeExperimentDataExportRows(String[] parameterStableId, String[] geneAccession, String allele[], String gender, ArrayList<Integer> phenotypingCenterIds, List<String> zygosity, String[] strain, String[] pipelines) throws SolrServerException, IOException, URISyntaxException, SQLException{

		List<String> rows = new ArrayList<String>();
		SexType sex = null;
		if (gender != null)
			sex = SexType.valueOf(gender);
		if (phenotypingCenterIds == null || phenotypingCenterIds.size() == 0){
			phenotypingCenterIds.add(null);
		}
		if (strain == null || strain.length == 0){
			strain = new String[1];
			strain[0] = null;
		}
		if (allele == null || allele.length == 0){
			allele = new String[1];
			allele[0] = null;
		}
		ArrayList<Integer> pipelineIds = new ArrayList<>();
		if (pipelines != null){
			for (String pipe: pipelines){
				pipelineIds.add(ppDAO.getPhenotypePipelineByStableId(pipe).getId());
			}
		}
		if (pipelineIds.size() == 0)
			pipelineIds.add(null);
			
		List<ExperimentDTO> experimentList = new ArrayList<ExperimentDTO> ();	
		for (int k = 0; k < parameterStableId.length; k++){
			for (int mgiI = 0; mgiI < geneAccession.length; mgiI++){
				for (Integer pCenter : phenotypingCenterIds){
					for (Integer pipelineId : pipelineIds){
						for (int strainI = 0; strainI < strain.length; strainI++){
							for (int alleleI = 0; alleleI < allele.length; alleleI++){
								experimentList = experimentService.getExperimentDTO(parameterStableId[k], pipelineId,  geneAccession[mgiI], sex, pCenter, zygosity, strain[strainI]);
								if (experimentList.size() > 0){
									for (ExperimentDTO experiment : experimentList) { 
										rows.addAll(experiment.getTabbedToString(ppDAO)) ;
									}
									rows.add("\n\n");
								}
							}
						}
					}
				}
			}
		}
		return rows;
	}

	public List<String> composeDataTableExportRows(String solrCoreName, JSONObject json, Integer iDisplayStart, Integer iDisplayLength, boolean showImgView, String solrParams, HttpServletRequest request){
		List<String> rows = null;

		if (solrCoreName.equals("gene") ){			
			rows = composeGeneDataTableRows(json, request);
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
		return rows;
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
			System.out.println("MODE: annotview " + showImgView);
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
	private List<String> composeGeneDataTableRows(JSONObject json, HttpServletRequest request){
				
		JSONArray docs = json.getJSONObject("response").getJSONArray("docs");		
		
		List<String> rowData = new ArrayList<String>();
				
		rowData.add("Marker symbol\tHuman ortholog\tMaker name\tSynonym\tProduction status\tPhenotype status"); // column names		
				
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
			if ( doc.has("marker_name") ){
				data.add(doc.getString("marker_name"));
			}
			else {
				data.add("NA");				
			}			
			
			if( doc.has("marker_synonym") ) {
				List<String> synData = new ArrayList<String>();
				JSONArray syn = doc.getJSONArray("marker_synonym");
				for(int s=0; s<syn.size();s++) {					
					synData.add(syn.getString(s));
				}
				data.add(StringUtils.join(synData, "|")); // use | as a multiValue separator in CSV output
			}
			else {
				data.add("NA");
			}			
			
			// ES/Mice production status			
			boolean toExport = true;
			String prodStatus = solrIndex.deriveProductionStatusForEsCellAndMice(doc, request, toExport);	
			data.add(prodStatus);
			
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
			data.add(doc.getString("human_curated"));
			data.add(doc.getString("mouse_curated"));
			data.add(doc.getString("impc_predicted"));
			data.add(doc.getString("mgi_predicted"));
						
			rowData.add(StringUtils.join(data, "\t"));
		}
		return rowData;
	}
	

	private List<String> composeDataRowGeneOrPhenPage(String id, String pageName){
		
		List<String> res = new ArrayList<>();
		List<PhenotypeCallSummary> phenotypeList = new ArrayList<PhenotypeCallSummary>();
		PhenotypeFacetResult phenoResult=null;
		
		if (pageName.equalsIgnoreCase("gene")){
			
			try {	
				phenoResult = phenoDAO.getPhenotypeCallByGeneAccessionAndFilter(id, "");
				phenotypeList=phenoResult.getPhenotypeCallSummaries();
			} catch (HibernateException|JSONException e) {
				log.error("ERROR GETTING PHENOTYPE LIST");
				e.printStackTrace();
				phenotypeList = new ArrayList<PhenotypeCallSummary>();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
						
			ArrayList<PhenotypeRow> phenotypes = new ArrayList<PhenotypeRow>(); 
			
			for (PhenotypeCallSummary pcs : phenotypeList) {	
				// Use a tree set to maintain an alphabetical order (Female, Male)
				List<String> sex = new ArrayList<String>();
				sex.add(pcs.getSex().toString());	
				PhenotypeRow pr = new PhenotypeRow( pcs, config.get("drupalBaseUrl")+"/data");
				phenotypes.add(pr);
			}
						
			Collections.sort(phenotypes); // sort in alpha order by MP term name
			res.add("Phenotype\tAllele\tZygosity\tSex\tProcedure / Parameter\tPhenotyping Center\tSource\tGraph");
			for (PhenotypeRow pr : phenotypes){
				res.add(pr.toTabbedString("gene"));
			}		
		
		}
		
		else if (pageName.equalsIgnoreCase("phenotype")){

			phenotypeList = new ArrayList<PhenotypeCallSummary>();

			try {
				phenoResult = phenoDAO.getPhenotypeCallByMPAccessionAndFilter(id.replaceAll("\"", ""), "");
				phenotypeList = phenoResult.getPhenotypeCallSummaries();
			} catch (HibernateException|JSONException e) {
				log.error("ERROR GETTING PHENOTYPE LIST");
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
	
			ArrayList<PhenotypeRow> phenotypes = new ArrayList<PhenotypeRow>();
			res.add("Gene\tAllele\tZygosity\tSex\tPhenotype\tProcedure / Parameter\tPhenotyping Center\tSource\tGraph"); 
			for (PhenotypeCallSummary pcs : phenotypeList) {
	
				// Use a tree set to maintain an alphabetical order (Female, Male)
				List<String> sex = new ArrayList<String>();
				sex.add(pcs.getSex().toString());
	
				PhenotypeRow pr = new PhenotypeRow( pcs, config.get("drupalBaseUrl") + "/data");
				
				if(pr.getParameter() != null && pr.getProcedure()!= null) {		
					phenotypes.add(pr);
				}
			}


			Collections.sort(phenotypes); // sort in alpha order by gene symbol name
			for (PhenotypeRow pr : phenotypes){
				res.add(pr.toTabbedString("phenotype"));
			}
		}
		return res;
	}
	
}
