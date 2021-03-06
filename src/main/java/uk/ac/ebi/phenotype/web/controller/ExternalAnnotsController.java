/*******************************************************************************
 * Copyright 2015 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 *******************************************************************************/
package uk.ac.ebi.phenotype.web.controller;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import uk.ac.ebi.generic.util.SolrIndex;
import uk.ac.ebi.phenotype.dao.GwasDAO;
import uk.ac.ebi.phenotype.service.dto.GwasDTO;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.sql.SQLException;
import java.util.*;

@Controller
public class ExternalAnnotsController {

	private Logger log = Logger.getLogger(this.getClass().getCanonicalName());

	@Resource(name = "globalConfiguration")
	private Map<String, String> config;

	@Autowired
	private SolrIndex solrIndex;

	@Autowired
	private GwasDAO gwasDao;
	
	@Autowired
	@Qualifier("admintoolsDataSource")
	//@Qualifier("admintoolsDataSourceLocal")
	private DataSource admintoolsDataSource;
	
	
	/**
	 * redirect calls to the base url to the gwaslookup page
	 * 
	 * @return
	 */
	@RequestMapping(value = "/gwaslookup", method = RequestMethod.GET)
	public String gwaslookup() {
		
		return "gwaslookup";
	}
	
	
	@RequestMapping(value = "/gwaslookup", method = RequestMethod.POST)
	public ResponseEntity<String> getImpcPhenotype2GwasDiseaseTraitMapping2 (

		@RequestParam(value = "keyword", required = false) String keyword,
		@RequestParam(value = "mgi_gene_id", required = false) String mgi_gene_id,
		@RequestParam(value = "mgi_gene_symbol", required = false) String mgi_gene_symbol,
		@RequestParam(value = "gwas_disease_trait", required = false) String gwas_disease_trait,
		@RequestParam(value = "gwas_p_value", required = false) String gwas_p_value,
		@RequestParam(value = "gwas_reported_gene", required = false) String gwas_reported_gene,
		@RequestParam(value = "gwas_mapped_gene", required = false) String gwas_mapped_gene,
		@RequestParam(value = "gwas_upstream_gene", required = false) String gwas_upstream_gene,
		@RequestParam(value = "gwas_downstream_gene", required = false) String gwas_downstream_gene,
		@RequestParam(value = "gwas_snp_id", required = false) String gwas_snp_id,
		@RequestParam(value = "mp_term_id", required = false) String mp_term_id,
		@RequestParam(value = "mp_term_name", required = false) String mp_term_name,
		@RequestParam(value = "mode", required = false) String mode,
		
		HttpServletRequest request,
		HttpServletResponse response,
		Model model) throws IOException, URISyntaxException, SQLException  {
		//System.out.println("PARAMS: " + request.getQueryString());
		
		String content = null;
		
		String[] parts = request.getQueryString().split("&");
		
		if ( parts.length == 1 ){
			parts = request.getQueryString().split("=");
			String field = parts[0];
			String value = parts[1];
			//System.out.println("QUERY: " + field + " --- " + value);
			content = fetchGwasMappingOverviewTable(request, field, value);
		}	
		else {
			List<String> params = new ArrayList<>();
			for( int i=0; i<parts.length; i++ ){
				String[] pair = parts[i].split("=");
				params.add(pair[0] + "=\"" + URLDecoder.decode(pair[1], "UTF-8") + "\"");
			}
			
			String sql = StringUtils.join(params, " AND ");
			content = fetchGwasMappingDetailTable(request, sql);
		}

		//System.out.println("content: " + content);
		return new ResponseEntity<String>(content, createResponseHeaders(), HttpStatus.CREATED);
	}
	
	@RequestMapping(value = "/phenotype2gwas", method = RequestMethod.GET)
	public String getImpcPhenotype2GwasDiseaseTraitMapping (
			@RequestParam(value = "mgi_gene_symbol", required = false) String mgiGeneSymbol, 
			
			HttpServletRequest request,
			HttpServletResponse response,
			Model model) throws IOException, URISyntaxException, SQLException  {
		
		
		//System.out.println("GOT symbol: " + mgiGeneSymbol);
		
		String htmlStr = null;
		if ( ! mgiGeneSymbol.isEmpty() ){
			String mode = "nontool";
			htmlStr = fetchGwasMappingTable(request, "mgi_gene_symbol", mgiGeneSymbol, mode);
		}
		
		model.addAttribute("mapping", htmlStr);
		
		return "phenotype2gwas";
	}
	private String fetchGwasMappingDetailTable(HttpServletRequest request, String sql) throws SQLException {
		
		String baseUrl = request.getAttribute("baseUrl").toString();
		String hostName = request.getAttribute("mappedHostname").toString().replace("https:", "http:");
		
		List<GwasDTO> gwasMappings = gwasDao.getGwasMappingDetailRows(sql);
		String NA = "Info not available";
		
        int totalDocs = gwasMappings.size();
        System.out.println("GOT " + totalDocs + " rows" );

        List<String> thList = new ArrayList<>();
        thList.add("<th>IMPC allele</th>");
        thList.add("<th>IMPC mouse gender</th>");
        thList.add("<th>Phenomapping category</th>");
        thList.add("<th>GWAS SNP id</th>");
        thList.add("<th>GWAS p value</th>");
        thList.add("<th>GWAS reported gene</th>");
        thList.add("<th>GWAS mapped gene</th>");
        thList.add("<th>GWAS upstream gene</th>");
        thList.add("<th>GWAS downstream gene</th>");
        String thead = "<thead>" + StringUtils.join(thList,"") + "</thead>";
        
        List<String> trs = new ArrayList<>();
       
        int count = 0;
        for (GwasDTO gw : gwasMappings) {
        	count++;
        	List<String> tr = new ArrayList<>();

        	String mgi_allele_name = gw.getGwasMgiAlleleName();
        	String mgi_allele_id = gw.getGwasMgiAlleleId();
        	tr.add("<td>" + mgi_allele_name + "<br>" + mgi_allele_id + "</td>");
        	tr.add("<td>" + gw.getGwasMouseGender() + "</td>");
        	tr.add("<td>" + gw.getGwasPhenoMappingCategory() + "</td>");
        	tr.add("<td>" + gw.getGwasSnpId() + "</td>");
        	tr.add("<td>" + Float.toString(gw.getGwasPvalue()) + "</td>");
        	
        	String gwas_reported_gene = gw.getGwasReportedGene().isEmpty() ? NA : gw.getGwasReportedGene();
        	tr.add("<td>" + gwas_reported_gene  + "</td>");
        	
        	String gwas_mapped_gene = gw.getGwasMappedGene().isEmpty() ? NA : gw.getGwasMappedGene();
        	tr.add("<td>" + gwas_mapped_gene + "</td>");
        	
        	String gwas_upstream_gene = gw.getGwasUpstreamGene().isEmpty() ? NA : gw.getGwasUpstreamGene();
        	tr.add("<td>" + gwas_upstream_gene + "</td>");

        	String gwas_downstream_gene = gw.getGwasDownstreamGene().isEmpty() ? NA : gw.getGwasDownstreamGene();
        	tr.add("<td>" + gwas_downstream_gene + "</td>");
        	
        	String trClass = count % 2 == 0 ? "even" : "odd";
        	trs.add("<tr class='" + trClass + "'>" + StringUtils.join(tr,"") + "</tr>");
        }
        
        String table = "<table class='detailed'>" + thead + StringUtils.join(trs,"") + "</table>";
        return table;
	}
	
	private String fetchGwasMappingOverviewTable(HttpServletRequest request, String field, String value) throws SQLException{
		
		String baseUrl = request.getAttribute("baseUrl").toString();
		String hostName = request.getAttribute("mappedHostname").toString().replace("https:", "http:");
		
		List<GwasDTO> gwasMappings = gwasDao.getGwasMappingOverviewByQueryStr(field, value);
		
		JSONObject j = new JSONObject();
        j.put("aaData", new Object[0]);
        int totalDocs = gwasMappings.size();
        
        j.put("iTotalRecords", totalDocs);
        j.put("iTotalDisplayRecords", totalDocs);

        System.out.println("GOT " + totalDocs + " rows" );
        int counter = 0;
        for (GwasDTO gw : gwasMappings) {
        	counter++;
        	String mgi_gene_symbol = gw.getGwasMgiGeneSymbol();
        	String mgi_gene_id = gw.getGwasMgiGeneId();
        	String geneLink = baseUrl + "/genes/" + mgi_gene_id;
        	//String mgi_gene_symbol_link = "<a href='" + hostName + baseUrl + "/" + geneLink + "'>" + mgi_gene_symbol + "</a>";
        	String mgi_gene_symbol_link = "<a href='" + hostName + geneLink + "'>" + mgi_gene_symbol + "</a>";

        	String mp_term_name = gw.getGwasMpTermName();
        	String gwas_disease_trait = gw.getGwasDiseaseTrait();
           
           
        	List<String> rowData = new ArrayList<String>();
            rowData.add(mgi_gene_symbol_link);
            rowData.add(mp_term_name);
            rowData.add(gwas_disease_trait);
            rowData.add("<i class='fa fa-plus-square' id='" + counter + "'></i>");
            j.getJSONArray("aaData").add(rowData);
           
        }
        return j.toString();
        
	}
	
	
	
	private String fetchGwasMappingTable(HttpServletRequest request, String field, String value, String mode) throws SQLException{
		
	// GWAS Gene to IMPC gene mapping
		
		List<GwasDTO> gwasMappings = null;
		if ( field.equals("mgi_gene_symbol") ){
			value = value.toUpperCase();
		}
		gwasMappings = gwasDao.getGwasMappingRows(field, value);
		
		System.out.println("ExternalAnnotsController FOUND " + gwasMappings.size() + " phenotype to gwas trait mappings");
		
		GwasDTO gm1 = gwasMappings.get(0);
		String mgiGeneId = gm1.getGwasMgiGeneId();
		String mgiGeneSymbol = gm1.getGwasMgiGeneSymbol();
		String mappingCat = gm1.getGwasPhenoMappingCategory();
		
		Set<String> traits = new HashSet<>();
		Map<String, List<String>> alleleIdData = new HashMap<>();
		Map<String, List<GwasDTO>> traitGwasMappings = new HashMap<>();
		
		for ( GwasDTO gw : gwasMappings ) {
			String traitName = gw.getGwasDiseaseTrait();
			
			traits.add(traitName);
			if ( ! traitGwasMappings.containsKey(traitName) ){
				traitGwasMappings.put(traitName, new ArrayList<GwasDTO>());
			}
			traitGwasMappings.get(traitName).add(gw);
		}
		
		
		String baseUrl = request.getAttribute("baseUrl").toString();
		String geneLink = baseUrl + "/genes/" + mgiGeneId;
		String markerRow = "<div id='mk'><span id='mkLeft'>Marker symbol: <a href='" + geneLink + "'>" + mgiGeneSymbol + "</a></span><span id='mkRight' class='"+ mappingCat +"'>" + mappingCat + " phenotypic mapping</span></div>";
		List<String> dataRow = new ArrayList<>();
		
		String theadRow = null;
		if ( mode.equals("tool") ){
			theadRow = "<thead><tr><th class='impcData'>Marker symbol</th><th class='impcData'>Phenotypic mapping</th><th class='impcData'>IMPC MP term</th><th class='impcData'>IMPC Mouse gender</th><th>GWAS SNP id</th><th>GWAS p value</th><th>GWAS Reported gene</th><th>GWAS Mapped gene</th><th>GWAS Upstream gene</th><th>GWAS Downstream gene</th></tr></thead>";
		}
		else {
			theadRow = "<thead><tr><th class='impcData'>IMPC MP term</th><th class='impcData'>IMPC Mouse gender</th><th>GWAS SNP id</th><th>GWAS p value</th><th>GWAS Reported gene</th><th>GWAS Mapped gene</th><th>GWAS Upstream gene</th><th>GWAS Downstream gene</th></tr></thead>";
			
		}
		//System.out.println("marker row: " + markerRow);
		List<String> trts = new ArrayList<>();
		trts.add("<li class='trtName'>GWAS disease traits:&nbsp;&nbsp;</li>");
		
		int counter = 0;
		for ( String traitName : traits ){
			//System.out.println(traitName + " has got " + traitGwasMappings.get(traitName).size() + " gwas mappings");
			counter++;
			String tabId = mgiGeneSymbol + "_" + counter; 
			
			trts.add("<li><a href='#" + tabId + "'>" + traitName + "</a></li>");
			
			List<String> tabDivs = new ArrayList<>();
			
			Map<String, List<GwasDTO>> alleleNameGwasMappings = new HashMap<>();
			
			for ( GwasDTO tGw : traitGwasMappings.get(traitName) ){
	
				String thisAlleleName = tGw.getGwasMgiAlleleName();
				if ( ! alleleNameGwasMappings.containsKey(thisAlleleName) ){
					alleleNameGwasMappings.put(thisAlleleName, new ArrayList<GwasDTO>());
				}
				alleleNameGwasMappings.get(thisAlleleName).add(tGw);
			}
			for (Map.Entry<String, List<GwasDTO>> entry : alleleNameGwasMappings.entrySet()){
			    List<GwasDTO> aGws = entry.getValue();
			    
			    String thisAlleleName = entry.getKey();
			    String thisAlleleId = aGws.get(0).getGwasMgiAlleleId();
			    String alleleId = thisAlleleId.startsWith("NULL") ? "" : "(" + thisAlleleId + ")";
			   
			   // String caption = " <caption>IMPC allele: " + thisAlleleName + alleleId + "</caption>";
			    String caption = " <caption>IMPC allele: " + thisAlleleName + "(" + thisAlleleId + ")" + "</caption>";
			    
			    List<String> trs = new ArrayList<>();
			    
			    for ( GwasDTO aGw : aGws ) {
			    	List<String> tds = new ArrayList<>();
			    	String mgiBaseLink = baseUrl + "/phenotypes/";
			    	if ( mode.equals("tool") ){
			    		tds.add("<td class='impcData'>" + aGw.getGwasMgiGeneSymbol() + "</td>");
			    		tds.add("<td class='impcData'>" + aGw.getGwasPhenoMappingCategory() + "</td>");
			    	}
					tds.add("<td class='impcData'><a href='" + mgiBaseLink + aGw.getGwasMpTermId() + "'>" + aGw.getGwasMpTermName() + "</a></td>");
					tds.add("<td class='impcData'>" + aGw.getGwasMouseGender() + "</td>");
					tds.add("<td>" + aGw.getGwasSnpId() + "</td>");
					tds.add("<td>" + Float.toString(aGw.getGwasPvalue()) + "</td>");
					tds.add("<td>" + aGw.getGwasReportedGene() + "</td>");
					tds.add("<td>" + aGw.getGwasMappedGene() + "</td>");
					tds.add("<td>" + aGw.getGwasUpstreamGene() + "</td>");
					tds.add("<td>" + aGw.getGwasDownstreamGene() + "</td>");
					
					String td = StringUtils.join(tds, "");
					trs.add("<tr>" + td + "</tr>");
			    }	
				
				String table = "<table class='tablesorter'>" + theadRow + caption + "<tbody>"+ StringUtils.join(trs, "") + "</tbody></table>";
				
				//System.out.println("table ---- " + table);
				tabDivs.add("<div id='" + tabId + "'>" + table + "</div>");
			    
			}
			
			dataRow.add(StringUtils.join(tabDivs, ""));
		}
		
		if (traits.size() > 1){
			String traitTabs = "<ul class='tabs'>" + StringUtils.join(trts, "") + "</ul>";
			
			if ( mode.equals("tool") ){
				return "<div id='tabs'>" + traitTabs + StringUtils.join(dataRow, "") + "</div>";
			}
			return markerRow + "<div id='tabs'>" + traitTabs + StringUtils.join(dataRow, "") + "</div>";
		}
		else {
			Iterator ti = traits.iterator();
			String traitName = "<div class='trtName'>GWAS disease trait:&nbsp;&nbsp;" + ti.next().toString() + "</div>";
			
			if ( mode.equals("tool") ){
				return traitName + StringUtils.join(dataRow, "");
			}
			return markerRow + traitName + StringUtils.join(dataRow, "");
		}
	}
	
	@RequestMapping(value = "/allelerefedit", method = RequestMethod.GET)
	public String dataTableJsonAlleleRefEdit(
			HttpServletRequest request,
			HttpServletResponse response,
			Model model) throws IOException, URISyntaxException, SQLException  {
		// model.addAttribute("q", q);
		
		return "allelerefedit";
	}
	
	@RequestMapping(value = "/alleleref", method = RequestMethod.GET)
	public String dataTableJsonAlleleRef(
			HttpServletRequest request,
			HttpServletResponse response,
			Model model) throws IOException, URISyntaxException, SQLException  {
		// model.addAttribute("q", q);
		
		return "alleleref";
	}
	
	@ExceptionHandler(Exception.class)
	private ResponseEntity<String> getSolrErrorResponse(Exception e) {
		e.printStackTrace();
		String bootstrap="<div class=\"alert\"><strong>Warning!</strong>  Error: Search functionality is currently unavailable</div>";
		String errorJSON="{'aaData':[[' "+bootstrap+"','  ', ' ']], 'iTotalRecords':1,'iTotalDisplayRecords':1}";
		JSONObject errorJson = (JSONObject) JSONSerializer.toJSON(errorJSON);
		return new ResponseEntity<String>(errorJson.toString(), createResponseHeaders(), HttpStatus.CREATED);
	}
	
	private HttpHeaders createResponseHeaders(){
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setContentType(MediaType.APPLICATION_JSON);
		return responseHeaders;
	}

	@RequestMapping(value = "/gene2fam", method = RequestMethod.GET)
	public String gene2fam(@RequestParam(value = "q", required = false) String q,
			@RequestParam(value = "core", required = false) String core, 
			@RequestParam(value = "fq", required = false) String fq, HttpServletRequest request,
			Model model) throws IOException, URISyntaxException {

		// model.addAttribute("q", q);
		// model.addAttribute("core", core);
		// model.addAttribute("fq", fq);

		// String dataTableJson = solrIndex.getMgiGenesClansDataTable(request);
		String dataTableJson = solrIndex.getMgiGenesClansPlainTable(request);
		model.addAttribute("datatable", dataTableJson);

		return "gene2pfam";
	}

	@RequestMapping(value = "/reports/gene2go", method = RequestMethod.GET)
	public String goStats(HttpServletRequest request, HttpServletResponse response, Model model) throws IOException, URISyntaxException {

		Map<String, Map<String, Map<String, JSONArray>>> stats = solrIndex.getGO2ImpcGeneAnnotationStats();
		List<String> data = createTable(stats);
		// model.addAttribute("catDoc", get_go_evid_category_mapping());
		model.addAttribute("legend", data.get(0));
		model.addAttribute("goStatsTable", data.get(1));

		return "Go";
	}

	private String get_go_evid_category_mapping() {

		Map<String, List<String>> evidCat = new HashMap<>();

		List<String> expEvids = new ArrayList<>();
		expEvids.add("Inferred from Experiment (EXP)");
		expEvids.add("Inferred from Direct Assay (IDA)");
		expEvids.add("Inferred from Physical Interaction (IPI)");
		expEvids.add("Inferred from Mutant Phenotype (IMP)");
		expEvids.add("Inferred from Genetic Interaction (IGI)");
		expEvids.add("Inferred from Expression Pattern (IEP)");
		evidCat.put("Experimental", expEvids);

		List<String> curatedEvids = new ArrayList<>();
		curatedEvids.add("Inferred from Sequence or structural Similarity (ISS)");
		curatedEvids.add("Inferred from Sequence Orthology (ISO)");
		curatedEvids.add("Inferred from Sequence Alignment (ISA)");
		curatedEvids.add("Inferred from Sequence Model (ISM)");
		curatedEvids.add("Inferred from Genomic Context (IGC)");
		curatedEvids.add("Inferred from Biological aspect of Ancestor (IBA)");
		curatedEvids.add("Inferred from Biological aspect of Descendant (IBD)");
		curatedEvids.add("Inferred from Key Residues (IKR)");
		curatedEvids.add("Inferred from Rapid Divergence(IRD)");
		curatedEvids.add("Inferred from Reviewed Computational Analysis (RCA)");
		evidCat.put("Curated computational", curatedEvids);

		List<String> autoEvids = new ArrayList<>();
		autoEvids.add("Inferred from Electronic Annotation (IEA)");
		evidCat.put("Automated electronic", autoEvids);

		List<String> otherEvids = new ArrayList<>();
		otherEvids.add("Traceable Author Statement (TAS)");
		otherEvids.add("Non-traceable Author Statement (NAS)");
		otherEvids.add("Inferred by Curator (IC)");
		evidCat.put("Other", otherEvids);

		List<String> ndEvids = new ArrayList<>();
		ndEvids.add("No biological Data available (ND)");
		evidCat.put("No biological data available", ndEvids);

		StringBuilder builder = new StringBuilder();
		Iterator ec = evidCat.entrySet().iterator();
		while (ec.hasNext()) {

			Map.Entry pairs = (Map.Entry) ec.next();
			String cat = pairs.getKey().toString();
			List<String> evidList = (List<String>) pairs.getValue();
			builder.append("<ul class='evidCat'>" + cat);
			for (int i = 0; i < evidList.size(); i++) {
				builder.append("<li class='evidCat'>" + evidList.get(i) + "</li>");
			}
			builder.append("</ul>");
		}
		return builder.toString();
	}

	public List<String> createTable(Map<String, Map<String, Map<String, JSONArray>>> stats) {

		StringBuilder builder = new StringBuilder();
		String legend = "F = molecular function<br>P = biological process<br><span id='legendBox'><div class='FP'>F and P</div><div class='F'>F</div><div class='P'>P</div></span>";

		Map<Integer, String> evidRankCat = SolrIndex.getGomapCategory();

		Map<String, String> domainMap = new HashMap<>();
		domainMap.put("F", "molecular_function");
		domainMap.put("P", "biological_process");
		domainMap.put("FP", "*");

		// String internalBaseSolrUrl = config.get("internalSolrUrl") +
		// "/gene/select?;

		List<String> data = new ArrayList<>();
		data.add(legend);

		for (String key : stats.keySet()) {

			builder.append("<table class='goStats'>");
			builder.append("<tbody>");

			String phenoCount = " : " + stats.get(key).get("allPheno").get(key).get(0);

			builder.append("<tr>");
			builder.append("<td class='phenoStatus' colspan=4>" + key + " genes" + phenoCount + "</td>");
			builder.append("</tr>");

			for (String goMode : stats.get(key).keySet()) {

				// System.out.println("GO MODE: " + goMode);
				Map<String, List<String>> evidValDomain = new LinkedHashMap<>();

				Map<String, JSONArray> domainEvid = stats.get(key).get(goMode);

				Iterator itd = domainEvid.entrySet().iterator();

				while (itd.hasNext()) {

					Map.Entry pairs2 = (Map.Entry) itd.next();
					String domain = pairs2.getKey().toString();

					// System.out.println(pairs2.getKey() + " = " +
					// pairs2.getValue());
					JSONArray evids = (JSONArray) pairs2.getValue();
					itd.remove(); // avoids a ConcurrentModificationException

					String domainParam = "go_term_domain:" + domainMap.get(domain);
					if (domain.equals("FP")) {
						List<String> fqStrs = new ArrayList<>();
						fqStrs.add("go_term_domain:\"" + domainMap.get("F") + "\"");
						fqStrs.add("go_term_domain:\"" + domainMap.get("P") + "\"");
						domainParam = StringUtils.join(fqStrs, " AND ");
					}

					for (int i = 0; i < evids.size(); i = i + 2) {
						int hasGoRowSpan = evids.size() / 2;
						int noGoRowSpan = hasGoRowSpan + 1;

						String currCell = "";
						if (goMode.equals("w/o GO")) {

							String qParams = "&q=latest_phenotype_status:\"" + key + "\" AND -go_term_id:*" + "&fq=mp_id:*";

							builder.append("<tr>");
							builder.append("<td rel='" + key + "'>" + goMode + "</td>");
							builder.append("<td colspan=3><div id='nogo' class='dlink nogo' rel='" + qParams + "'>" + evids.get(0) + "</div></td>");
							builder.append("</tr>");
						} else if (goMode.equals("w/  GO")) {
							String rank = evids.get(i).toString();

							if (evidRankCat.containsKey(Integer.parseInt(rank))) {
								List<String> cellVals = new ArrayList<>();

								if (evidValDomain.get(rank) != null) {
									cellVals = evidValDomain.get(rank);
								}

								String qParams = "&q=latest_phenotype_status:\"" + key + "\" AND evidCodeRank:" + rank + " AND " + domainParam;

								String found = evids.get(i + 1).toString();

								cellVals.add("<div id ='" + rank + "' class='dlink " + domain + "' rel='" + qParams + "'>" + found + "</div>");
								evidValDomain.put(rank, cellVals);
							}
						}
					}
				}
				Iterator cell = evidValDomain.entrySet().iterator();

				while (cell.hasNext()) {

					Map.Entry pairs3 = (Map.Entry) cell.next();
					String rank = pairs3.getKey().toString();

					List<String> cellValLst = (List<String>) pairs3.getValue();
					String cellVals = StringUtils.join(cellValLst, "");

					builder.append("<tr>");
					builder.append("<td rel='" + key + "' class='evidCode'>" + evidRankCat.get(Integer.parseInt(rank)) + "</td>");
					builder.append("<td>" + cellVals + "</td>"); // counts
					builder.append("</tr>");

				}
			}
			builder.append("</tbody></table>");
		}

		String htmlTable = builder.toString();
		data.add(htmlTable);

		return data;
	}

}
