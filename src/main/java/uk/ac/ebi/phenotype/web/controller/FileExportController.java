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

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.CompositeFormat;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

import uk.ac.ebi.phenotype.dao.PhenotypeCallSummaryDAO;
import uk.ac.ebi.phenotype.pojo.PhenotypeCallSummary;

import uk.ac.ebi.generic.util.Tools;

import javax.servlet.*;
import javax.servlet.http.*;
import uk.ac.ebi.generic.util.*;

@Controller
public class FileExportController extends HttpServlet implements BeanFactoryAware {
	
	private BeanFactory bf;
	private PhenotypeCallSummaryDAO phenotypeCallSummaryDAO;
	private String dataString = null;	
	private Workbook wb = null;
	private String tsvDelimiter = "\t";
	
	
	private String patternStr = "(.+_\\d+_\\d+_\\d+)_\\d+";
	private Pattern pattern = Pattern.compile(patternStr);
	private String europhenomeBaseUrl = "http://www.europhenome.org/databrowser/viewer.jsp?set=true&m=true&zygosity=All&compareLines=View+Data";
	
	@Autowired
	public FileExportController(PhenotypeCallSummaryDAO phenotypeCallSummaryDAO) { 
		this.phenotypeCallSummaryDAO = phenotypeCallSummaryDAO;	
	}

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
			@RequestParam(value="geneSymbol", required=false) String geneSymbol,			
			@RequestParam(value="solrCoreName", required=false) String solrCoreName,
			@RequestParam(value="params", required=false) String solrParams,
			@RequestParam(value="gridFields", required=false) String gridFields,		
			@RequestParam(value="showImgView", required=false) boolean showImgView,	
			@RequestParam(value="dumpMode", required=false) String dumpMode,
			
			HttpSession session, 
			HttpServletRequest request, 
			HttpServletResponse response,
			Model model
			) throws Exception{	
						
			//String contextPath = (String) request.getAttribute("baseUrl");
			Map config = (Map) bf.getBean("globalConfiguration");
			panelName = panelName == null ? "" : panelName; 
			String contextPath = (String) request.getAttribute("baseUrl");	
			String serverName = (String) request.getServerName();
			int serverPort = request.getServerPort();
			
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
					}
					SolrIndex solrIndex = new SolrIndex(fileType, solrCoreName, solrParams, rowStart, 
							showImgView, dumpMode, gridFields, request, config, contextPath, serverName, serverPort);
					
					List<String> rows = solrIndex.composeDataTableExportRows(solrCoreName);
					String[] titles = rows.get(0).split("\t");
					
					Wb = new ExcelWorkBook(titles, solrIndex.composeXlsTableData(rows), sheetName);
				}
				
				this.wb = Wb.fetchWorkBook();
			}
			else if ( fileType.equals("tsv") ){				
				response.setContentType("text/tsv; charset=utf-8");					 
				response.setHeader("Content-Disposition","attachment;filename=" + fileName + ".tsv");
				
				if ( panelName.equals("geneVariants") ){
					this.dataString = composeGeneVariantsTsvString(mpId, extDbId);
				}
				else if ( panelName.equals("phenoAssoc") ){				
					this.dataString = composePhenoAssocTsvString(mgiGeneId, extDbId);
				}
				else if ( !solrCoreName.isEmpty() ){
					//System.out.println(solrCoreName + " *** " + solrParams + " *** " + rowStart + " *** " + showImgView + " *** " + dumpMode);
					if (dumpMode.equals("all")){
						rowStart = 0;
					}
					SolrIndex solrIndex = new SolrIndex(fileType, solrCoreName, solrParams, rowStart, 
							showImgView, dumpMode, gridFields, request, config, contextPath, serverName, serverPort);
					this.dataString = StringUtils.join(solrIndex.composeDataTableExportRows(solrCoreName), "\n");					
				}
			}
			
			response.setHeader("Pragma", "no-cache");
			response.setHeader("Expires", "0"); 
			
			try {				
				
				if ( fileType.equals("tsv") ){
					ServletOutputStream output = response.getOutputStream();
					StringBuffer sb = new StringBuffer();						
					sb.append(dataString);					
		 
					InputStream in = new ByteArrayInputStream(sb.toString().getBytes("UTF-8"));
		 
					byte[] outputByte = new byte[4096];
					//copy binary content to output stream
					while(in.read(outputByte, 0, 4096) != -1) {
						output.write(outputByte, 0, 4096);
					}
					
					in.close();					
					output.flush();
					output.close();
				}
				else if ( fileType.equals("xls") ) {	
					ServletOutputStream output = response.getOutputStream();
					try {
						wb.write(output);
					}       
					catch (IOException ioe) { 
						System.out.println("Error: " + ioe.getMessage());
					}
				}	
			}
			catch(Exception e){
				System.out.println("Error: " + e.getMessage());
			}
			return null;		
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
			if ( p.getGene() != null ){
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
		rows.add(StringUtils.join(fetchGeneVariantsTitles(), tsvDelimiter)); //"Gene, Allele Symbol, Zygosity, Sex, Procedure");
				
		
		List<PhenotypeCallSummary> summaries = phenotypeCallSummaryDAO.getPhenotypeCallByMPAccession(mpId, extDbId);
		for(PhenotypeCallSummary p : summaries) {
						
			// only want phenotypes that link to a gene
			if ( p.getGene() != null ){
				//System.out.println("gene symbol" + p.getGene().getSymbol());				
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
		String linkParam = null;
		String extId = p.getExternalId() + "";
		String sex = p.getSex().name();				
		String procedure_sid = p.getProcedure().getStableId();
		String parameter_sid = p.getParameter().getStableId();
		Matcher matcher = this.pattern.matcher(parameter_sid);
		
		if (matcher.find()) {
			String params = "&l=" + extId + "&x=" + sex + "&p=" + procedure_sid + "&pid_" + matcher.group(1) + "=on";
			//linkParam = "<a href='" + europhenomeBaseUrl + params + "'>Europhenome</a>";
			linkParam = europhenomeBaseUrl + params;
						       
		} 
		else {
			System.out.println("NO MATCH");
			linkParam = "failed to find link to legacyData";
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
	
	@Override
	public void setBeanFactory(BeanFactory arg0) throws BeansException {
		this.bf=arg0;		
	}	
}
