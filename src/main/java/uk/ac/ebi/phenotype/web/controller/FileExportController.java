/**
 * Copyright © 2011-2014 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package uk.ac.ebi.phenotype.web.controller;

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
import uk.ac.ebi.generic.util.SolrIndex.AnnotNameValCount;
import uk.ac.ebi.generic.util.Tools;
import uk.ac.ebi.phenotype.dao.*;
import uk.ac.ebi.phenotype.ontology.SimpleOntoTerm;
import uk.ac.ebi.phenotype.pojo.*;
import uk.ac.ebi.phenotype.service.ExperimentService;
import uk.ac.ebi.phenotype.service.GeneService;
import uk.ac.ebi.phenotype.service.dto.ExperimentDTO;
import uk.ac.ebi.phenotype.service.dto.ObservationDTO;
import uk.ac.ebi.phenotype.util.PhenotypeFacetResult;
import uk.ac.ebi.phenotype.web.pojo.DataTableRow;
import uk.ac.ebi.phenotype.web.pojo.GenePageTableRow;
import uk.ac.ebi.phenotype.web.pojo.PhenotypePageTableRow;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.*;


import uk.ac.ebi.phenotype.service.MpService;

@Controller
public class FileExportController {

    private Logger log = Logger.getLogger(this.getClass().getCanonicalName());

    @Autowired
    public PhenotypeCallSummaryDAO phenotypeCallSummaryDAO;

    @Autowired
    private SolrIndex solrIndex;

	@Autowired
	private GeneService geneService;
    
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
	private MpService mpService;
    
    @Autowired
    private PhenotypeCallSummarySolr phenoDAO;

    private String NO_INFO_MSG = "No information available";
    
    private String hostName;
    
    /**
     * Return a TSV formatted response which contains all datapoints
     *
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
        SexType sex = (sexesParameter != null && sexesParameter.length > 1) ? SexType.valueOf(sexesParameter[0]) : null;
        List<String> zygosities = (zygositiesParameter == null) ? null : Arrays.asList(zygositiesParameter);
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
        rows.add(StringUtils.join(new String[]{"Experiment", "Center", "Pipeline", "Procedure", "Parameter", "Strain", "Colony", "Gene", "Allele", "MetadataGroup", "Zygosity", "Sex", "AssayDate", "Value","Metadata"}, ", "));

        Integer i = 1;

        for (ExperimentDTO experiment : experiments) {

            // Adding all data points to the export
            Set<ObservationDTO> observations = experiment.getControls();
            observations.addAll(experiment.getMutants());

            for (ObservationDTO observation : observations) {
                List<String> row = new ArrayList<>();
                row.add("Exp" + i.toString());
                row.add(center);
                row.add(pipelineStableId);
                row.add(procedureStableId);
                row.add(parameterStableId);
                row.add(observation.getStrain());
                row.add((observation.getGroup().equals("control")) ? "+/+" : observation.getColonyId());
                row.add((observation.getGroup().equals("control")) ? "\"\"" : geneAcc);
                row.add((observation.getGroup().equals("control")) ? "\"\"" : alleleAcc);
                row.add((observation.getMetadataGroup() != null &&  ! observation.getMetadataGroup().isEmpty()) ? observation.getMetadataGroup() : "\"\"");
                row.add((observation.getZygosity() != null &&  ! observation.getZygosity().isEmpty()) ? observation.getZygosity() : "\"\"");
                row.add(observation.getSex());
                row.add(observation.getDateOfExperimentString());

                String dataValue = observation.getCategory();
                if (dataValue == null) {
                    dataValue = observation.getDataPoint().toString();
                }

                row.add(dataValue);
                row.add("\"" + StringUtils.join(observation.getMetadata(), "::") + "\"");

                rows.add(StringUtils.join(row, ", "));
            }

            // Next experiment
            i ++;
        }

        return StringUtils.join(rows, "\n");
    }

    /**
     * <p>
     * Export table as TSV or Excel file.</p>
     *
     * @param model
     * @return
     */
    @RequestMapping(value = "/export", method = RequestMethod.GET)
    public String exportTableAsExcelTsv(
    		/* ********************************************************************
    		 *  Please keep in mind that /export is used for ALL exports on the website so be cautious about required parameters  
    		 *  *******************************************************************/
            @RequestParam(value = "externalDbId", required = true) Integer extDbId,
            @RequestParam(value = "fileType", required = true) String fileType,
            @RequestParam(value = "fileName", required = true) String fileName,
            @RequestParam(value = "legacyOnly", required = false) boolean legacyOnly,
            @RequestParam(value = "allele", required = false) String[] allele,
            @RequestParam(value = "rowStart", required = false) Integer rowStart,
            @RequestParam(value = "length", required = false) Integer length,
            @RequestParam(value = "panel", required = false) String panelName,
            @RequestParam(value = "mpId", required = false) String mpId,
            @RequestParam(value = "mpTerm", required = false) String mpTerm,
            @RequestParam(value = "mgiGeneId", required = false) String[] mgiGeneId,
            @RequestParam(value = "parameterStableId", required = false) String[] parameterStableId, // should be filled for graph data export
            @RequestParam(value = "zygosity", required = false) String[] zygosities, // should be filled for graph data export
            @RequestParam(value = "strains", required = false) String[] strains, // should be filled for graph data export
            @RequestParam(value = "geneSymbol", required = false) String geneSymbol,
            @RequestParam(value = "solrCoreName", required = false) String solrCoreName,
            @RequestParam(value = "params", required = false) String solrFilters,
            @RequestParam(value = "gridFields", required = false) String gridFields,
            @RequestParam(value = "showImgView", required = false, defaultValue = "false") boolean showImgView,
            @RequestParam(value = "dumpMode", required = false) String dumpMode,
            @RequestParam(value = "baseUrl", required = false) String baseUrl,
            @RequestParam(value = "sex", required = false) String sex,
            @RequestParam(value = "phenotypingCenter", required = false) String[] phenotypingCenter,
            @RequestParam(value = "pipelineStableId", required = false) String[] pipelineStableId,
            @RequestParam(value = "dogoterm", required = false) boolean dogoterm,
            @RequestParam(value = "goevids", required = false) String goevids,
            @RequestParam(value = "gene2pfam", required = false) boolean gene2pfam,
            HttpSession session,
            HttpServletRequest request,
            HttpServletResponse response,
            Model model
    ) throws Exception {

    	hostName = request.getAttribute("mappedHostname").toString().replace("https:", "http:");
    	System.out.println("------------\nEXPORT \n---------");
        log.debug("solr params: " + solrFilters);
        
        String query = "*:*"; // default
        String[] pairs = solrFilters.split("&");		
		for (String pair : pairs) {
			try {
				String[] parts = pair.split("=");				
				if (parts[0].equals("q")) {
					query = parts[1];	
				}
			}catch (Exception e) {
				log.error("Error getting value of q");			
			}			
		}		
		
        Workbook wb = null;
        List<String> dataRows = new ArrayList();
        // Default to exporting 10 rows
        length = length != null ? length : 10;
        
        panelName = panelName == null ? "" : panelName;

        if ( ! solrCoreName.isEmpty()) {
            if (dumpMode.equals("all")) {
                rowStart = 0;
                //length = parseMaxRow(solrParams); // this is the facetCount
                length = 10000000;
            }

            if (solrCoreName.equalsIgnoreCase("experiment")) {
            	ArrayList<Integer> phenotypingCenterIds = new ArrayList<Integer>();
                try {
                    for (int i = 0; i < phenotypingCenter.length; i ++) {
                        phenotypingCenterIds.add(organisationDao.getOrganisationByName(phenotypingCenter[i].replaceAll("%20", " ")).getId());
                    }
                } catch (NullPointerException e) {
                    log.error("Cannot find organisation ID for org with name " + phenotypingCenter);
                }
                List<String> zygList = null;
                if (zygosities != null) {
                    zygList = Arrays.asList(zygosities);
                }
                String s = (sex.equalsIgnoreCase("null")) ? null : sex;
                dataRows = composeExperimentDataExportRows(parameterStableId, mgiGeneId, allele, s, phenotypingCenterIds, zygList, strains, pipelineStableId);
            } 
            else if (solrCoreName.equalsIgnoreCase("genotype-phenotype")) {
                if (mgiGeneId != null) {
                    dataRows = composeDataRowGeneOrPhenPage(mgiGeneId[0], request.getParameter("page"), solrFilters, request);
                } else if (mpId != null) {
                    dataRows = composeDataRowGeneOrPhenPage(mpId, request.getParameter("page"), solrFilters, request);
                }
            }
            else if ( dogoterm ) {
            	JSONObject json = solrIndex.getDataTableExportRows(solrCoreName, solrFilters, gridFields, rowStart, length, showImgView);
            	dataRows = composeGene2GoAnnotationDataRows(json, request, dogoterm);
            }
            else if ( gene2pfam ){            	
            	JSONObject json = solrIndex.getDataTableExportRows(solrCoreName, solrFilters, gridFields, rowStart, length, showImgView);
            	dataRows = composeGene2PfamClansDataRows(json, request);
            }
            else {
                JSONObject json = solrIndex.getDataTableExportRows(solrCoreName, solrFilters, gridFields, rowStart, length, showImgView);
                dataRows = composeDataTableExportRows(query, solrCoreName, json, rowStart, length, showImgView, solrFilters, request, legacyOnly);
            }
        }

        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");

        try {

            if (fileType.equals("tsv")) {

                response.setContentType("text/tsv; charset=utf-8");
                response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".tsv");
				// ServletOutputStream output = response.getOutputStream();

				// ckc note: switch to use getWriter() so that we don't get error like
                // java.io.CharConversionException: Not an ISO 8859-1 character
                // and if we do, the error will cause the dump to end prematurely 
                // and we may not get the full rows (depending on which row causes error)        
                PrintWriter output = response.getWriter();
                for (String line : dataRows) {
                    output.println(line);
                }

                output.flush();
                output.close();
            } else if (fileType.equals("xls")) {

                response.setContentType("application/vnd.ms-excel");
                response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xls");

                String sheetName = fileName;

                String[] titles = new String[0];
                String[][] tableData = new String[0][0];
                if ( ! dataRows.isEmpty()) {
                    // Remove the title row (row 0) from the list and assign it to
                    // the string array for the spreadsheet
                    titles = dataRows.remove(0).split("\t");
                    tableData = Tools.composeXlsTableData(dataRows);
                }
                
                wb = new ExcelWorkBook(titles, tableData, sheetName).fetchWorkBook();
                ServletOutputStream output = response.getOutputStream();
                try {
                    wb.write(output);
                } catch (IOException ioe) {
                    log.error("Error: " + ioe.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("Error: " + e.getMessage());
        }
        return null;
    }

    private int parseMaxRow(String solrParams) {
        String[] paramsList = solrParams.split("&");
        int facetCount = 0;
        for (String str : paramsList) {
            if (str.startsWith("facetCount=")) {
                String[] vals = str.split("=");
                facetCount = Integer.parseInt(vals[1]);
            }
        }
        return facetCount;
    }

    public List<String> composeExperimentDataExportRows(String[] parameterStableId, String[] geneAccession, String allele[], String gender, ArrayList<Integer> phenotypingCenterIds, List<String> zygosity, String[] strain, String[] pipelines) throws SolrServerException, IOException, URISyntaxException, SQLException {

        List<String> rows = new ArrayList();
        SexType sex = null;
        if (gender != null) {
            sex = SexType.valueOf(gender);
        }
        if (phenotypingCenterIds == null) {
            throw new RuntimeException("ERROR: phenotypingCenterIds is null. Expected non-null value.");
        }
        if (phenotypingCenterIds.isEmpty()) {
            phenotypingCenterIds.add(null);
        }
        
        if (strain == null || strain.length == 0) {
            strain = new String[1];
            strain[0] = null;
        }
        if (allele == null || allele.length == 0) {
            allele = new String[1];
            allele[0] = null;
        }
        ArrayList<Integer> pipelineIds = new ArrayList<>();
        if (pipelines != null) {
            for (String pipe : pipelines) {
                pipelineIds.add(ppDAO.getPhenotypePipelineByStableId(pipe).getId());
            }
        }
        if (pipelineIds.isEmpty()) {
            pipelineIds.add(null);
        }

        List<ExperimentDTO> experimentList;
        for (int k = 0; k < parameterStableId.length; k ++) {
            for (int mgiI = 0; mgiI < geneAccession.length; mgiI ++) {
                for (Integer pCenter : phenotypingCenterIds) {
                    for (Integer pipelineId : pipelineIds) {
                        for (int strainI = 0; strainI < strain.length; strainI ++) {
                            for (int alleleI = 0; alleleI < allele.length; alleleI ++) {
                                experimentList = experimentService.getExperimentDTO(parameterStableId[k], pipelineId, geneAccession[mgiI], sex, pCenter, zygosity, strain[strainI]);
                                if (experimentList.size() > 0) {
                                    for (ExperimentDTO experiment : experimentList) {
                                        rows.addAll(experiment.getTabbedToString(ppDAO));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return rows;
    }

    public List<String> composeDataTableExportRows(String query, String solrCoreName, JSONObject json, Integer iDisplayStart, Integer iDisplayLength, boolean showImgView, String solrParams, HttpServletRequest request, boolean legacyOnly) {
        List<String> rows = null;

        if (solrCoreName.equals("gene")) {
            rows = composeGeneDataTableRows(json, request, legacyOnly);
        } else if (solrCoreName.equals("mp")) {
            rows = composeMpDataTableRows(json, request);
        } else if (solrCoreName.equals("ma")) {
            rows = composeMaDataTableRows(json, request);
        } else if (solrCoreName.equals("pipeline")) {
            rows = composeProtocolDataTableRows(json, request);
        } else if (solrCoreName.equals("images")) {
            rows = composeImageDataTableRows(query, json, iDisplayStart, iDisplayLength, showImgView, solrParams, request);
        } else if (solrCoreName.equals("impc_images")) {
            rows = composeImpcImageDataTableRows(query, json, iDisplayStart, iDisplayLength, showImgView, solrParams, request);
        } 
        else if (solrCoreName.equals("disease")) {
            rows = composeDiseaseDataTableRows(json, request);
        }
       
        return rows;
    }

    private List<String> composeProtocolDataTableRows(JSONObject json, HttpServletRequest request) {
        JSONArray docs = json.getJSONObject("response").getJSONArray("docs");

        String impressBaseUrl = request.getAttribute("drupalBaseUrl").toString().replace("https","http") + "/impress/impress/displaySOP/";
        //String impressBaseUrl = request.getAttribute("drupalBaseUrl") + "/impress/impress/displaySOP/";

        List<String> rowData = new ArrayList();
        rowData.add("Parameter\tProcedure\tProcedure Impress link\tPipeline"); // column names	

        for (int i = 0; i < docs.size(); i ++) {
            List<String> data = new ArrayList();
            JSONObject doc = docs.getJSONObject(i);
            data.add(doc.getString("parameter_name"));
            
            
            JSONArray procedures = doc.getJSONArray("procedure_name");
			JSONArray procedure_stable_keys = doc.getJSONArray("procedure_stable_key");
			
			List<String> procedureLinks = new ArrayList<String>();
			for( int p=0; p<procedures.size(); p++ ){
				//String procedure = procedures.get(p).toString();
				String procedure_stable_key = procedure_stable_keys.get(p).toString();
				procedureLinks.add(impressBaseUrl + procedure_stable_key);
			}
            
			//String procedure = doc.getString("procedure_name");
            //data.add(procedure);
			data.add( StringUtils.join(procedures,"|") );
			
            
            //String procedure_stable_key = doc.getString("procedure_stable_key");	
			//String procedureLink = impressBaseUrl + procedure_stable_key;			
			//data.add(procedureLink);				
			data.add( StringUtils.join(procedureLinks,"|") );
			
            data.add(doc.getString("pipeline_name"));
            rowData.add(StringUtils.join(data, "\t"));
        }
        return rowData;
    }

    private List<String> composeImageDataTableRows(String query, JSONObject json, Integer iDisplayStart, Integer iDisplayLength, boolean showImgView, String solrParams, HttpServletRequest request) {
       //System.out.println("query: "+ query + " -- "+ solrParams);
    	
    	String mediaBaseUrl = config.get("mediaBaseUrl").replace("https:", "http:");
        
        List<String> rowData = new ArrayList();

        String mpBaseUrl   = request.getAttribute("baseUrl") + "/phenotypes/";
        String maBaseUrl   = request.getAttribute("baseUrl") + "/anatomy/";
        String geneBaseUrl = request.getAttribute("baseUrl") + "/genes/";
       
        if (showImgView) {

            JSONArray docs = json.getJSONObject("response").getJSONArray("docs");
            rowData.add("Annotation term\tAnnotation id\tAnnotation id link\tProcedure\tGene symbol\tGene symbol link\tImage link"); // column names	

            for (int i = 0; i < docs.size(); i ++) {
                List<String> data = new ArrayList();
                JSONObject doc = docs.getJSONObject(i);
                //String[] fields = {"annotationTermName", "annotationTermId", "expName", "symbol_gene"};
                String[] fields = {"annotationTermId", "expName", "symbol_gene"};
                for (String fld : fields) {
                    if (doc.has(fld)) {
                        List<String> lists = new ArrayList();
                        
                    	if ( fld.equals("annotationTermId") ){
                    		
                    		// annotaton term with prefix
                    		List<String> termLists = new ArrayList();
                    		JSONArray termList = doc.getJSONArray("annotationTermName");
                            
                            // annotation id links
                            List<String> link_lists = new ArrayList();
                            
                            JSONArray list = doc.getJSONArray(fld); // annotationTermId
	                    	for (int l = 0; l < list.size(); l++) {
	                    		String value = list.getString(l);
	                    		String termVal = termList.getString(l);
	                    		
	                    		if ( value.startsWith("MP:") ){
	                    			link_lists.add(hostName + mpBaseUrl + value);
	                    			termLists.add("MP:"+termVal);
	                    		}
	                    		else {
	                    			link_lists.add(hostName + maBaseUrl + value);
	                    			termLists.add("MA:"+termVal);
	                    		}
	                    		
	                         	lists.add(value);
	                        }
	                    	
	                    	data.add(StringUtils.join(termLists, "|"));
	                        data.add(StringUtils.join(lists, "|"));
	                        data.add(StringUtils.join(link_lists, "|"));
                    	}
                    	else if ( fld.equals("symbol_gene") ){
                    		// gene symbol and its link
                            List<String> link_lists = new ArrayList();
                            
                            JSONArray list = doc.getJSONArray(fld);
	                    	for (int l = 0; l < list.size(); l++) {
	                    		String[] parts = list.getString(l).split("_");
	                    		String symbol = parts[0];
	                    		String mgiId  = parts[1];
	                         	lists.add(symbol);
	                         	link_lists.add(hostName + geneBaseUrl + mgiId);
	                        }
	                        data.add(StringUtils.join(lists, "|"));
	                        data.add(StringUtils.join(link_lists, "|"));
                    		
                    	}
                    	else {
                    		JSONArray list = doc.getJSONArray(fld);
	                    	for (int l = 0; l < list.size(); l++) {
	                    		String value = list.getString(l);
	                         	lists.add(value);
	                        }
	                        data.add(StringUtils.join(lists, "|"));
                    	}
                    } 
                    else {
                    	if ( fld.equals("annotationTermId") ){
	                        data.add(NO_INFO_MSG);
	                        data.add(NO_INFO_MSG);
	                        data.add(NO_INFO_MSG);
                    	}
                    	else if ( fld.equals("symbol_gene") ){
                    		data.add(NO_INFO_MSG);
 	                        data.add(NO_INFO_MSG);
                    	}
                    	else {
                    		data.add(NO_INFO_MSG);
                    	}
                    }
                }

                data.add(mediaBaseUrl + "/" + doc.getString("largeThumbnailFilePath"));
                rowData.add(StringUtils.join(data, "\t"));
            }
        } else {
            //System.out.println("MODE: annotview " + showImgView);
			// annotation view
            // annotation view: images group by annotationTerm per row
            rowData.add("Annotation type\tAnnotation term\tAnnotation id\tAnnotation id link\tRelated image count\tImages link"); // column names	
            JSONObject facetFields = json.getJSONObject("facet_counts").getJSONObject("facet_fields");

            JSONArray sumFacets = solrIndex.mergeFacets(facetFields);

            int numFacets = sumFacets.size();
            int quotient = (numFacets / 2) / iDisplayLength - ((numFacets / 2) % iDisplayLength) / iDisplayLength;
            int remainder = (numFacets / 2) % iDisplayLength;
            int start = iDisplayStart * 2;  // 2 elements(name, count), hence multiply by 2
            int end = iDisplayStart == quotient * iDisplayLength ? (iDisplayStart + remainder) * 2 : (iDisplayStart + iDisplayLength) * 2;

            for (int i = start; i < end; i = i + 2) {
                List<String> data = new ArrayList();
				// array element is an alternate of facetField and facetCount
                
                String[] names = sumFacets.get(i).toString().split("_");
                if (names.length == 2) {  // only want facet value of xxx_yyy
                    String annotName = names[0];
                   
                    Map<String, String> hm = solrIndex.renderFacetField(names, request); //MA:xxx, MP:xxx, MGI:xxx, exp					

                    data.add(hm.get("label"));
                    data.add(annotName);
                    data.add(hm.get("id"));
                    //System.out.println("annotname: "+ annotName);
                    if ( hm.get("fullLink") != null ) {
                    	data.add(hm.get("fullLink").toString());
                    }
                    else {
                    	data.add(NO_INFO_MSG);
                    }
                    
                    String imgCount = sumFacets.get(i + 1).toString();
                    data.add(imgCount);

                    String facetField = hm.get("field");
              
                    solrParams = solrParams.replaceAll("&q=.+&", "&q="+ query + " AND " + facetField + ":\"" + names[0] + "\"&");
                    String imgSubSetLink = hostName + request.getAttribute("baseUrl") + "/imagesb?" + solrParams;
                    
                    data.add(imgSubSetLink);
                    rowData.add(StringUtils.join(data, "\t"));
                }
            }
        }

        return rowData;
    }

    private List<String> composeImpcImageDataTableRows(String query, JSONObject json, Integer iDisplayStart, Integer iDisplayLength, boolean showImgView, String solrParams, HttpServletRequest request) {
        //System.out.println("query: "+ query + " -- "+ solrParams);
    	
    	// currently just use the solr field value
     	//String mediaBaseUrl = config.get("impcMediaBaseUrl").replace("https:", "http:");
       
     	List<String> rowData = new ArrayList();
		
     	String mpBaseUrl   = request.getAttribute("baseUrl") + "/phenotypes/";
     	String maBaseUrl   = request.getAttribute("baseUrl") + "/anatomy/";
     	String geneBaseUrl = request.getAttribute("baseUrl") + "/genes/";
		
		if (showImgView) {
		
			JSONArray docs = json.getJSONObject("response").getJSONArray("docs");
			//rowData.add("Annotation term\tAnnotation id\tAnnotation id link\tProcedure\tGene symbol\tGene symbol link\tImage link"); // column names	
			rowData.add("Procedure\tGene symbol\tGene symbol link\tImage link"); // column names	
		 
			for (int i = 0; i < docs.size(); i ++) {
				List<String> data = new ArrayList();
				JSONObject doc = docs.getJSONObject(i);
		
				//String[] fields = {"annotationTermName", "annotationTermId", "expName", "symbol_gene"};
				String[] fields = {"procedure_name", "gene_symbol"};
				for (String fld : fields) {
					if (doc.has(fld)) {
						List<String> lists = new ArrayList();
		         
						if ( fld.equals("gene_symbol") ){	
			
							data.add(doc.getString("gene_symbol"));
							data.add(hostName + geneBaseUrl + doc.getString("gene_accession_id"));
			     		
						}
						else if ( fld.equals("procedure_name") ){
							data.add(doc.getString("procedure_name"));
						}
					} 
					else {
				     	/*if ( fld.equals("annotationTermId") ){
				            data.add(NO_INFO_MSG);
				            data.add(NO_INFO_MSG);
				            data.add(NO_INFO_MSG);
				     	}
				     	else if ( fld.equals("symbol_gene") ){
				     	*/
						if ( fld.equals("gene_symbol") ){
							data.add(NO_INFO_MSG);
							data.add(NO_INFO_MSG);
						}
						else {
							data.add(NO_INFO_MSG);
						}
					}
				}
		
				data.add(doc.getString("jpeg_url"));
				rowData.add(StringUtils.join(data, "\t"));
			}
        } 
		else {
			
            // annotation view: images group by annotationTerm per row
			
			String baseUrl = config.get("baseUrl").replace("https:", "http:");
			String mediaBaseUrl = config.get("impcMediaBaseUrl").replace("https:", "http:");
			
            //rowData.add("Annotation type\tAnnotation term\tAnnotation id\tAnnotation id link\tRelated image count\tImages link"); // column names	
            rowData.add("Annotation type\tAnnotation term\tAnnotation id\tAnnotation id link\tRelated image count\tImages link"); // column names	
             
            String fqStr = query;	
			//System.out.println("fq: "+fqOri); //&fq=(impcImg_procedure_name:"Combined SHIRPA and Dysmorphology")
			String defaultQStr = "q=observation_type:image_record";
			String defaultFqStr = "fq=(biological_sample_group:experimental)";
			
			if ( !query.contains("fq=*:*") ){
				fqStr = fqStr.replace("&fq=","");
				defaultQStr = defaultQStr + " AND " + fqStr; 
				defaultFqStr = defaultFqStr + " AND " + fqStr;
			}
			
			JSONObject facetFields = json.getJSONObject("facet_counts").getJSONObject("facet_fields");
			 
            	//JSONArray sumFacets = solrIndex.mergeFacets(facetFields);
 
			List<AnnotNameValCount> annots = solrIndex.mergeImpcFacets(json, baseUrl);
 				
            //int numFacets = sumFacets.size();
			int numFacets = annots.size();
            /*int quotient = (numFacets / 2) / iDisplayLength - ((numFacets / 2) % iDisplayLength) / iDisplayLength;
            int remainder = (numFacets / 2) % iDisplayLength;
            int start = iDisplayStart * 2;  // 2 elements(name, count), hence multiply by 2
            int end = iDisplayStart == quotient * iDisplayLength ? (iDisplayStart + remainder) * 2 : (iDisplayStart + iDisplayLength) * 2;
             */
            
            //int quotient = (numFacets / 2) / iDisplayLength - ((numFacets / 2) % iDisplayLength) / iDisplayLength;
            //int remainder = (numFacets / 2) % iDisplayLength;
            int start = iDisplayStart;  // 2 elements(name, count), hence multiply by 2
            int end = iDisplayStart + iDisplayLength;
            end = end > numFacets ? numFacets : end; 

            for (int i = start; i < end; i++ ) {
            	
            	List<String> data = new ArrayList();
            	
				AnnotNameValCount annot = annots.get(i);
				
				String displayAnnotName = annot.name;
				data.add(displayAnnotName);
				
				String annotVal = annot.val;
				data.add(annotVal);
				
				if ( annot.id != null ){
					data.add(annot.id);
					data.add(annot.link);
				}
				else {
					data.add(NO_INFO_MSG);
					data.add(NO_INFO_MSG);
				}
				
				int imgCount = annot.imgCount;
				StringBuilder sb = new StringBuilder();
				sb.append("");
				sb.append(imgCount);
				data.add(sb.toString());

				String link = annot.link != null ? annot.link : "";
				String valLink = "<a href='" + link + "'>" + annotVal + "</a>";
				
				query = annot.facet + ":\"" + annotVal + "\"";
				
				//https://dev.mousephenotype.org/data/impcImages/images?q=observation_type:image_record&fq=biological_sample_group:experimental"
				String thisImgUrl = "http:" + mediaBaseUrl + defaultQStr + " AND (" + query + ")&" + defaultFqStr;
				//String imgSubSetLink = "<a href='" + thisImgUrl + "'>" + thisImgUrl + "</a>";
				String imgSubSetLink = thisImgUrl;
				
				data.add(imgSubSetLink);
				
				// image path
				//String imgPath = fetchImpcImagePathByAnnotName(query, defaultFqStr);
				//rowData1.add(imgPath);
				rowData.add(StringUtils.join(data, "\t"));
        	
			}	
            //---
//            JSONObject facetFields = json.getJSONObject("facet_counts").getJSONObject("facet_fields");
//
//            JSONArray sumFacets = solrIndex.mergeFacets(facetFields);
//
//            int numFacets = sumFacets.size();
//            int quotient = (numFacets / 2) / iDisplayLength - ((numFacets / 2) % iDisplayLength) / iDisplayLength;
//            int remainder = (numFacets / 2) % iDisplayLength;
//            int start = iDisplayStart * 2;  // 2 elements(name, count), hence multiply by 2
//            int end = iDisplayStart == quotient * iDisplayLength ? (iDisplayStart + remainder) * 2 : (iDisplayStart + iDisplayLength) * 2;
//
//            for (int i = start; i < end; i = i + 2) {
//            	List<String> data = new ArrayList();
// 				// array element is an alternate of facetField and facetCount
//                 
//                String[] names = sumFacets.get(i).toString().split("_");
//                if (names.length == 2) {  // only want facet value of xxx_yyy
//                	String annotName = names[0];
//                
//                	Map<String, String> hm = solrIndex.renderFacetField(names, request); //MA:xxx, MP:xxx, MGI:xxx, exp					
//                	List<AnnotNameValCount> annots = solrIndex.mergeImpcFacets(json, baseUrl);
//                	
//                	data.add(hm.get("label"));
//                	data.add(annotName);
//                	data.add(hm.get("id"));
//                	//System.out.println("annotname: "+ annotName);
//                	if ( hm.get("fullLink") != null ) {
//                		data.add(hm.get("fullLink").toString());
//                	}
//                	else {
//                		data.add(NO_INFO_MSG);
//                	}
//                 
//                	String imgCount = sumFacets.get(i + 1).toString();
//                	data.add(imgCount);
//
//                	String facetField = hm.get("field");
//           
//                	solrParams = solrParams.replaceAll("&q=.+&", "&q="+ query + " AND " + facetField + ":\"" + names[0] + "\"&");
//                	String imgSubSetLink = hostName + request.getAttribute("baseUrl") + "/imagesb?" + solrParams;
//                 
//                	data.add(imgSubSetLink);
//                	rowData.add(StringUtils.join(data, "\t"));
//                 }
//             }
        }

        return rowData;
     }
    
    private List<String> composeMpDataTableRows(JSONObject json, HttpServletRequest request) {
        JSONArray docs = json.getJSONObject("response").getJSONArray("docs");
        
        String baseUrl = request.getAttribute("baseUrl") + "/phenotypes/";	
        
        List<String> rowData = new ArrayList();
        rowData.add("Mammalian phenotype term\tMammalian phenotype id\tMammalian phenotype id link\tMammalian phenotype definition\tMammalian phenotype synonym\tMammalian phenotype top level term\tComputationally mapped human phenotype terms\tComputationally mapped human phenotype term Ids"); // column names	

        for (int i = 0; i < docs.size(); i ++) {
            List<String> data = new ArrayList();
            JSONObject doc = docs.getJSONObject(i);

            data.add(doc.getString("mp_term"));
            String mpId = doc.getString("mp_id");
            data.add(mpId);
            data.add(hostName + baseUrl + mpId);

            if (doc.has("mp_definition")) {
                data.add(doc.getString("mp_definition"));
            } else {
                data.add(NO_INFO_MSG);
            }

            if (doc.has("mp_term_synonym")) {
                List<String> syns = new ArrayList();
                JSONArray syn = doc.getJSONArray("mp_term_synonym");
                for (int t = 0; t < syn.size(); t ++) {
                	syns.add(syn.getString(t));
                }
                data.add(StringUtils.join(syns, "|"));
            } 
            else {
                data.add(NO_INFO_MSG);
            }
            
            if (doc.has("top_level_mp_term")) {
                List<String> tops = new ArrayList();
                JSONArray top = doc.getJSONArray("top_level_mp_term");
                for (int t = 0; t < top.size(); t ++) {
                    tops.add(top.getString(t));
                }
                data.add(StringUtils.join(tops, "|"));
            } else {
                data.add(NO_INFO_MSG);
            }

            if (doc.has("hp_term")) {
            	Set<SimpleOntoTerm> hpTerms = mpService.getComputationalHPTerms(doc);
            	List<String> terms = new ArrayList<String>();
            	List<String> ids   = new ArrayList<String>();
            
            	for(SimpleOntoTerm term : hpTerms ){
            		ids.add(term.getTermId());
            		System.out.println("TERM: " + term.getTermName());
            		terms.add(term.getTermName().equals("") ? NO_INFO_MSG : term.getTermName() );
            	}
            	
                data.add(StringUtils.join(terms, "|"));
                data.add(StringUtils.join(ids, "|"));
            } 
            else {
                data.add(NO_INFO_MSG);
                data.add(NO_INFO_MSG);
            }
            
            rowData.add(StringUtils.join(data, "\t"));
        }
        return rowData;
    }

    private List<String> composeMaDataTableRows(JSONObject json, HttpServletRequest request) {
        JSONArray docs = json.getJSONObject("response").getJSONArray("docs");

        String baseUrl = request.getAttribute("baseUrl") + "/anatomy/";
        
        List<String> rowData = new ArrayList();
        rowData.add("Mouse adult gross anatomy term\tMouse adult gross anatomy id\tMouse adult gross anatomy id link\tMouse adult gross anatomy synonym"); // column names	

        for (int i = 0; i < docs.size(); i ++) {
            List<String> data = new ArrayList();
            JSONObject doc = docs.getJSONObject(i);

            data.add(doc.getString("ma_term"));
            String maId = doc.getString("ma_id");
            data.add(maId);
            data.add(hostName + baseUrl + maId);
           
            if (doc.has("ma_term_synonym")) {
                List<String> syns = new ArrayList();
                JSONArray syn = doc.getJSONArray("ma_term_synonym");
                for (int t = 0; t < syn.size(); t ++) {
                	syns.add(syn.getString(t));
                }
                data.add(StringUtils.join(syns, "|"));
            } 
            else {
                data.add(NO_INFO_MSG);
            }
            
			// will have these cols coming later
			/*if(doc.has("mp_definition")) {				
             data.add(doc.getString("mp_definition"));					
             }
             else {
             data.add(NO_INFO_MSG);
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
             data.add(NO_INFO_MSG);
             }*/
            
            rowData.add(StringUtils.join(data, "\t"));
        }
        return rowData;
    }

    private List<String> composeGeneDataTableRows(JSONObject json, HttpServletRequest request, boolean legacyOnly) {

        JSONArray docs = json.getJSONObject("response").getJSONArray("docs");
        List<String> rowData = new ArrayList();

        rowData.add("Gene symbol\tHuman ortholog\tGene id\tGene name\tGene synonym\tProduction status\tPhenotype status\tPhenotype status link"); // column names		

        for (int i = 0; i < docs.size(); i ++) {
            List<String> data = new ArrayList();
            JSONObject doc = docs.getJSONObject(i);

            data.add(doc.getString("marker_symbol"));

            if (doc.has("human_gene_symbol")) {
                List<String> hsynData = new ArrayList();
                JSONArray hs = doc.getJSONArray("human_gene_symbol");
                for (int s = 0; s < hs.size(); s ++) {
                    hsynData.add(hs.getString(s));
                }
                data.add(StringUtils.join(hsynData, "|")); // use | as a multiValue separator in CSV output
            } else {
                data.add(NO_INFO_MSG);
            }
            
            // MGI gene id
            data.add(doc.getString("mgi_accession_id"));
            
			// Sanger problem, they should have use string for marker_name and not array
            //data.add(doc.getJSONArray("marker_name").getString(0));
            // now corrected using httpdatasource in dataImportHandler
            if (doc.has("marker_name")) {
                data.add(doc.getString("marker_name"));
            } else {
                data.add(NO_INFO_MSG);
            }

            if (doc.has("marker_synonym")) {
                List<String> synData = new ArrayList();
                JSONArray syn = doc.getJSONArray("marker_synonym");
                for (int s = 0; s < syn.size(); s ++) {
                    synData.add(syn.getString(s));
                }
                data.add(StringUtils.join(synData, "|")); // use | as a multiValue separator in CSV output
            } else {
                data.add(NO_INFO_MSG);
            }

            // ES/Mice production status			
            boolean toExport = true;
            String prodStatus = geneService.getProductionStatusForEsCellAndMice(doc, request, toExport);
            
            data.add(prodStatus);

            // phenotyping status
            String phStatus = geneService.getPhenotypingStatus(doc, request, toExport, legacyOnly);
           
            if ( phStatus.isEmpty() ){
            	data.add(NO_INFO_MSG); 
            	data.add(NO_INFO_MSG); // link column
            }
            else if ( phStatus.startsWith("http://") || phStatus.startsWith("https://") ){
				
				String[] parts = phStatus.split("\\|");
				if ( parts.length != 2  ){
					System.out.println("fileExport: '" + phStatus+ "' --- Expeced length 2 but got " + parts.length  );
				}
				else {
					String url   = parts[0].replace("https", "http");
					String label = parts[1];
					
	                data.add(label);
	                data.add(url);
				}
			}
			else {
				data.add(phStatus); 
			}

            // put together as tab delimited
            rowData.add(StringUtils.join(data, "\t"));
        }

        return rowData;
    }

    private List<String> composeDiseaseDataTableRows(JSONObject json, HttpServletRequest request) {
        JSONArray docs = json.getJSONObject("response").getJSONArray("docs");

        String baseUrl = request.getAttribute("baseUrl") + "/disease/";
        
        List<String> rowData = new ArrayList();
        // column names	
        rowData.add("Disease id"
        		+ "\tDisease id link"
        		+ "\tDisease name"
        		+ "\tSource"
        		+ "\tCurated genes from human (OMIM, Orphanet)"
        		+ "\tCurated genes from mouse (MGI)"
        		+ "\tCurated genes from human data with IMPC prediction"
        		+ "\tCurated genes from human data with MGI prediction"
        		+ "\tCandidate genes by phenotype - IMPC data"
        		+ "\tCandidate genes by phenotype - Novel IMPC prediction in linkage locus"
        		+ "\tCandidate genes by phenotype - MGI data"
        		+ "\tCandidate genes by phenotype - Novel MGI prediction in linkage locus"
        		//+ "\tGene symbol"
        		//+ "\tGene id"
        		); 

        for (int i = 0; i < docs.size(); i ++) {
            List<String> data = new ArrayList();
            JSONObject doc = docs.getJSONObject(i);

            String omimId = doc.getString("disease_id");
            data.add(omimId);
            data.add(hostName + baseUrl + omimId);
            
            data.add(doc.getString("disease_term"));
            data.add(doc.getString("disease_source"));
            
            data.add(doc.getString("human_curated"));
            data.add(doc.getString("mouse_curated"));
            data.add(doc.getString("impc_predicted_known_gene"));
            data.add(doc.getString("mgi_predicted_known_gene"));
            
            data.add(doc.getString("impc_predicted"));
            data.add(doc.getString("impc_novel_predicted_in_locus"));
            data.add(doc.getString("mgi_predicted"));
            data.add(doc.getString("mgi_novel_predicted_in_locus"));
            //JSONArray gsyms = doc.getJSONArray("marker_symbol");
            
            //System.out.println(gsyms);
            
            //String gids = doc.getJSONArray("mgi_accession_id").toString();
            
            
            
            rowData.add(StringUtils.join(data, "\t"));
        }
        return rowData;
    }

    private List<String> composeDataRowGeneOrPhenPage(String id, String pageName, String filters, HttpServletRequest request) {
        List<String> res = new ArrayList<>();
        List<PhenotypeCallSummary> phenotypeList = new ArrayList();
        PhenotypeFacetResult phenoResult;
        String targetGraphUrl = (String)request.getAttribute("mappedHostname") + request.getAttribute("baseUrl");

        if (pageName.equalsIgnoreCase("gene")) {

            try {
                phenoResult = phenoDAO.getPhenotypeCallByGeneAccessionAndFilter(id, filters);
                phenotypeList = phenoResult.getPhenotypeCallSummaries();
            } catch (HibernateException | JSONException e) {
                log.error("ERROR GETTING PHENOTYPE LIST");
                e.printStackTrace();
                phenotypeList = new ArrayList();
            } catch (Exception e) {
                e.printStackTrace();
            }
            ArrayList<GenePageTableRow> phenotypes = new ArrayList();
            for (PhenotypeCallSummary pcs : phenotypeList) {
                GenePageTableRow pr = new GenePageTableRow(pcs, targetGraphUrl, config);
                phenotypes.add(pr);
            }
            Collections.sort(phenotypes);                                       // sort in same order as gene page.
            
            res.add("Phenotype\tAllele\tZygosity\tSex\tProcedure | Parameter\tPhenotyping Center\tSource\tP Value\tGraph");
            for (DataTableRow pr : phenotypes) {
                res.add(pr.toTabbedString("gene"));
            }

        } else if (pageName.equalsIgnoreCase("phenotype")) {

            phenotypeList = new ArrayList();

            try {
                phenoResult = phenoDAO.getPhenotypeCallByMPAccessionAndFilter(id.replaceAll("\"", ""), filters);
                phenotypeList = phenoResult.getPhenotypeCallSummaries();
            } catch (HibernateException | JSONException e) {
                log.error("ERROR GETTING PHENOTYPE LIST");
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

            ArrayList<PhenotypePageTableRow> phenotypes = new ArrayList();
            res.add("Gene\tAllele\tZygosity\tSex\tPhenotype\tProcedure | Parameter\tPhenotyping Center\tSource\tP Value\tGraph");
            for (PhenotypeCallSummary pcs : phenotypeList) {
                PhenotypePageTableRow pr = new PhenotypePageTableRow(pcs, targetGraphUrl, config);

                if (pr.getParameter() != null && pr.getProcedure() != null) {
                    phenotypes.add(pr);
                }
            }
            Collections.sort(phenotypes);                                       // sort in same order as phenotype page.
            
            for (DataTableRow pr : phenotypes) {
                res.add(pr.toTabbedString("phenotype"));
            }
        }
        return res;
    }
    
    private List<String> composeGene2PfamClansDataRows(JSONObject json, HttpServletRequest request) {
    	
        JSONArray docs = json.getJSONObject("response").getJSONArray("docs");
        //System.out.println(" GOT " + docs.size() + " docs");
        String baseUrl = request.getAttribute("baseUrl") + "/genes/";
       
       
        List<String> rowData = new ArrayList<>();
        // column names	
       // latest_phenotype_status,mgi_accession_id,marker_symbol,pfama_id,pfama_acc,clan_id,clan_acc,clan_desc
        String fields = "Gene Symbol"
        		+ "\tMGI gene link"
        		+ "\tPhenotyping status"
        		+ "\tPfam Id"
        		+ "\tClan Id"
        		+ "\tClan Acc"
        		+ "\tClan Description";
        
        rowData.add(fields);
        
        String NOINFO = "no info available";
        
        for (int i = 0; i < docs.size(); i ++) {
        
	        JSONObject doc = docs.getJSONObject(i);
	        String gId = doc.getString("mgi_accession_id");
	        String phenoStatus = doc.getString("latest_phenotype_status");
        	
        	JSONArray _pfamaIds = doc.containsKey("pfama_id") ? doc.getJSONArray("pfama_id") : new JSONArray();
        	JSONArray _clanIds = doc.containsKey("clan_id") ? doc.getJSONArray("clan_id") : new JSONArray();
        	JSONArray _clanAccs = doc.containsKey("clan_acc") ? doc.getJSONArray("clan_acc") : new JSONArray();
        	JSONArray _clanDescs = doc.containsKey("clan_desc") ? doc.getJSONArray("clan_desc") : new JSONArray();
	    	
        	if ( _pfamaIds.size() == 0 ){
        		List<String> data = new ArrayList();
        		data.add(doc.getString("marker_symbol"));
            	data.add(hostName + baseUrl + gId);
            	data.add(phenoStatus);
        		
	        	data.add(NOINFO);
	        	data.add(NOINFO);
	        	data.add(NOINFO);
	        	data.add(NOINFO);
	        	
	        	rowData.add(StringUtils.join(data, "\t"));
        	}
        	else {
	        	for ( int j=0; j< _clanIds.size(); j++ ) {
	            	
	        		List<String> data = new ArrayList();
	        		data.add(doc.getString("marker_symbol"));
	            	data.add(hostName + baseUrl + gId);
	            	data.add(phenoStatus);
	        		
		        	data.add(doc.containsKey("pfama_id") ? _pfamaIds.getString(j) : NOINFO);
		        	data.add(doc.containsKey("clan_id") ? _clanIds.getString(j) : NOINFO);
		        	data.add(doc.containsKey("clan_acc") ? _clanAccs.getString(j) : NOINFO);
		        	data.add(doc.containsKey("clan_desc") ? _clanDescs.getString(j) : NOINFO);
		        	
		        	rowData.add(StringUtils.join(data, "\t"));
	        	}
        	}
        }
        return rowData;
    }
    
    private List<String> composeGene2GoAnnotationDataRows(JSONObject json, HttpServletRequest request, boolean hasgoterm) {
    	
        JSONArray docs = json.getJSONObject("response").getJSONArray("docs");
        //System.out.println(" GOT " + docs.size() + " docs");
        String baseUrl = request.getAttribute("baseUrl") + "/genes/";
       
        List<String> evidsList = new ArrayList<String>(Arrays.asList(request.getParameter("goevids").split(",")));
        List<String> rowData = new ArrayList();
        // column names	
        String fields = "Gene Symbol"
        		+ "\tMGI gene link"
        		+ "\tPhenotyping status"
        		+ "\tGO Term Id"
        		+ "\tGO Term Name"
        		+ "\tGO Term Evidence"
        		+ "\tGO evidence Category"
        		+ "\tGO Term Domain";
        
        rowData.add(fields);
        
        //GO evidence code ranking mapping
        Map<String,String> codeRank = new HashMap<>();
        // experimental 
        codeRank.put("EXP", "4");codeRank.put("IDA", "4");codeRank.put("IPI", "4");codeRank.put("IMP", "4");
        codeRank.put("IGI", "4");codeRank.put("IEP", "4");codeRank.put("TAS", "4");
        
        // curated computational
        codeRank.put("ISS", "3");codeRank.put("ISO", "3");codeRank.put("ISA", "3");codeRank.put("ISM", "3");
        codeRank.put("IGC", "3");codeRank.put("IBA", "3");codeRank.put("IBD", "3");codeRank.put("IKR", "3");
        codeRank.put("IRD", "3");codeRank.put("RCA", "3");codeRank.put("IC", "3");codeRank.put("NAS", "3");
        
        // automated electronic
        codeRank.put("IEA", "2");
        
        // goevid category mapping
        Map<String,String> evidCat = new HashMap<>();
        evidCat.put("1", "No biological data available");
        evidCat.put("2", "Automated electronic");
        evidCat.put("3", "Curated computational");
        evidCat.put("4", "Experimental");
        
        // no biological data available
        codeRank.put("ND", "1");
        
        String NOINFO = "no info available";
        
        for (int i = 0; i < docs.size(); i ++) {
        
            JSONObject doc = docs.getJSONObject(i);
            String gId = doc.getString("mgi_accession_id");
            String phenoStatus = doc.containsKey("latest_phenotype_status") ? doc.getString("latest_phenotype_status") : NOINFO;

            if ( !doc.containsKey("evidCodeRank") ){
            	
            	List<String> data = new ArrayList();
            	data.add(doc.getString("marker_symbol"));
            	data.add(hostName + baseUrl + gId);
            	data.add(phenoStatus);
            	data.add(NOINFO);
            	data.add(NOINFO);
            	data.add(NOINFO);
            	data.add(NOINFO);
            	data.add(NOINFO);
            	rowData.add(StringUtils.join(data, "\t"));
            }
            else {
            	String evidCodeRank = Integer.toString(doc.getInt("evidCodeRank")) ;
	            
	            JSONArray _goTermIds = doc.containsKey("go_term_id") ? doc.getJSONArray("go_term_id") : new JSONArray();
	            JSONArray _goTermNames = doc.containsKey("go_term_name") ? doc.getJSONArray("go_term_name") : new JSONArray();
	            JSONArray _goTermEvids = doc.containsKey("go_term_evid") ? doc.getJSONArray("go_term_evid") : new JSONArray();
	            JSONArray _goTermDomains = doc.containsKey("go_term_domain") ? doc.getJSONArray("go_term_domain") : new JSONArray();
            
	            for ( int j=0; j< _goTermEvids.size(); j++ ) {
	            	
	            	String evid = _goTermEvids.get(j).toString();
	            	
	            	if ( codeRank.get(evid).equals(evidCodeRank) ){
	            		List<String> data = new ArrayList();
	            		data.add(doc.getString("marker_symbol"));
		            	data.add(hostName + baseUrl + gId);
		            	data.add(phenoStatus);
		            	data.add(_goTermIds.size() > 0 ? _goTermIds.get(j).toString() : NOINFO);
		            	data.add(_goTermNames.size() > 0 ? _goTermNames.get(j).toString() : NOINFO);
		            	data.add(_goTermEvids.size() > 0 ? _goTermEvids.get(j).toString() : NOINFO);
		            	data.add(evidCat.get(evidCodeRank));
		            	data.add(_goTermDomains.size() > 0 ? _goTermDomains.get(j).toString() : NOINFO);
		            	rowData.add(StringUtils.join(data, "\t"));
	            	}
	            }
            }
        }
        return rowData;
    }
    
    private List<String> composeGene2GoAnnotationDataRows2(JSONObject json, HttpServletRequest request, boolean hasgoterm) {
    	
        JSONArray docs = json.getJSONObject("response").getJSONArray("docs");
        //System.out.println(" GOT " + docs.size() + " docs");
        String baseUrl = request.getAttribute("baseUrl") + "/genes/";
       
        List<String> evidsList = new ArrayList<String>(Arrays.asList(request.getParameter("goevids").split(",")));
        List<String> rowData = new ArrayList();
        // column names	
        String fields = "Gene Symbol"
        		+ "\tMGI gene link"
        		+ "\tPhenotyping status"
        		+ "\tGO Term Id"
        		+ "\tGO Term Name"
        		+ "\tGO Term Evidence"
        		+ "\tGO Term Domain";
        
        rowData.add(fields);
        
        for (int i = 0; i < docs.size(); i ++) {
            JSONObject doc = docs.getJSONObject(i);
            String gId = doc.getString("mgi_accession_id");
            String phenoStatus = doc.getString("latest_phenotype_status");

            JSONArray _goTermIds = doc.containsKey("go_term_id") ? doc.getJSONArray("go_term_id") : new JSONArray();
            JSONArray _goTermNames = doc.containsKey("go_term_name") ? doc.getJSONArray("go_term_name") : new JSONArray();
            JSONArray _goTermEvids = doc.containsKey("go_term_evid") ? doc.getJSONArray("go_term_evid") : new JSONArray();
            JSONArray _goTermDomains = doc.containsKey("go_term_domain") ? doc.getJSONArray("go_term_domain") : new JSONArray();
            
            String NOINFO = "no info available";
           
            Set<String> uniqEvids = new HashSet<>();
            if ( evidsList.get(0).equals("ND") ){
            	for(int g = 0; g < _goTermEvids.size(); g++){
            		uniqEvids.add(_goTermEvids.get(g).toString());
            	}
            }
            
            // NO GO
            if ( _goTermIds.size() == 0 ){
            	List<String> data = new ArrayList();
            	data.add(doc.getString("marker_symbol"));
            	data.add(hostName + baseUrl + gId);
            	data.add(phenoStatus);
            	data.add(NOINFO);
            	data.add(NOINFO);
            	data.add(NOINFO);
            	data.add(NOINFO);
            	rowData.add(StringUtils.join(data, "\t"));
            }
            else {
            
	            for ( int j=0; j< _goTermEvids.size(); j++ ) {
	            	
	            	String evid = _goTermEvids.get(j).toString();
	            	
	            	if (evidsList.get(0).equals("All GO") || evidsList.get(0).equals("All dataset") ){
	            		List<String> data = new ArrayList();
	            		data.add(doc.getString("marker_symbol"));
		            	data.add(hostName + baseUrl + gId);
		            	data.add(phenoStatus);
		            	data.add(_goTermIds.size() > 0 ? _goTermIds.get(j).toString() : NOINFO);
		            	data.add(_goTermNames.size() > 0 ? _goTermNames.get(j).toString() : NOINFO);
		            	data.add(_goTermEvids.size() > 0 ? _goTermEvids.get(j).toString() : NOINFO);
		            	data.add(_goTermDomains.size() > 0 ? _goTermDomains.get(j).toString() : NOINFO);
		            	rowData.add(StringUtils.join(data, "\t"));
	            	}
	            	else { 
	            		if ( evidsList.size() == 1 && evidsList.get(0).equals("ND") && uniqEvids.size() > 1 ){
            				// surpress ND if there is other evidences for same gene
            				continue;
	            		}
	            		else if ( evidsList.contains(evid) ){
	            			List<String> data = new ArrayList();
			            	data.add(doc.getString("marker_symbol"));
			            	data.add(hostName + baseUrl + gId);
			            	data.add(phenoStatus);
			            	data.add(_goTermIds.size() > 0 ? _goTermIds.get(j).toString() : NOINFO);
			            	data.add(_goTermNames.size() > 0 ? _goTermNames.get(j).toString() : NOINFO);
			            	data.add(_goTermEvids.size() > 0 ? _goTermEvids.get(j).toString() : NOINFO);
			            	data.add(_goTermDomains.size() > 0 ? _goTermDomains.get(j).toString() : NOINFO);
			            	rowData.add(StringUtils.join(data, "\t"));
	            		}
	            	}
	            }
            }
        }
        return rowData;
    }

}
