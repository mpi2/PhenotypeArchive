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
	
	@RequestMapping(value = "/phenotype2gwas", method = RequestMethod.GET)
	public String getImpcPhenotype2GwasDiseaseTraitMapping (
			@RequestParam(value = "symbol", required = false) String mgiGeneSymbol, 
			HttpServletRequest request,
			HttpServletResponse response,
			Model model) throws IOException, URISyntaxException, SQLException  {
		
		
		System.out.println("GOT symbol: " + mgiGeneSymbol);
		
		String htmlStr = fetchGwasMappingTable(request, mgiGeneSymbol);
		model.addAttribute("mapping", htmlStr);
		
		return "phenotype2gwas";
	}
	
	private String fetchGwasMappingTable(HttpServletRequest request, String mgiGeneSymbol) throws SQLException{
		// GWAS Gene to IMPC gene mapping
		List<GwasDTO> gwasMappings = gwasDao.getGwasMappingRows(mgiGeneSymbol.toUpperCase());
		
		System.out.println("ExternalAnnotsController FOUND " + gwasMappings.size() + " phenotype to gwas trait mappings");
		
		GwasDTO gm1 = gwasMappings.get(0);
		String mgiGeneId = gm1.getMgiGeneId();
		String mappingCat = gm1.getPhenoMappingCategory();
		
		Set<String> traits = new HashSet<>();
		Map<String, List<String>> alleleIdData = new HashMap<>();
		Map<String, List<GwasDTO>> traitGwasMappings = new HashMap<>();
		
		for ( GwasDTO gw : gwasMappings ) {
			String traitName = gw.getDiseaseTrait();
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
		
		String theadRow = "<thead><tr><th>IMPC MP term</th><th>IMPC Mouse gender</th><th>GWAS p value</th><th>GWAS Reported gene</th><th>GWAS Mapped gene</th><th>GWAS Upstream gene</th><th>GWAS Downstream gene</th></tr></thead>";
		
		//System.out.println("marker row: " + markerRow);
		List<String> trts = new ArrayList<>();
		trts.add("<li class='trtName'>GWAS disease traits:&nbsp;&nbsp;</li>");
		
		int counter = 0;
		for ( String traitName : traits ){
			System.out.println(traitName + " has got " + traitGwasMappings.get(traitName).size() + " gwas mappings");
			counter++;
			String tabId = mgiGeneSymbol + "_" + counter; 
			
			trts.add("<li><a href='#" + tabId + "'>" + traitName + "</a></li>");
			
			List<String> tabDivs = new ArrayList<>();
			
			Map<String, List<GwasDTO>> alleleNameGwasMappings = new HashMap<>();
			
			for ( GwasDTO tGw : traitGwasMappings.get(traitName) ){
	
				String thisAlleleName = tGw.getMgiAlleleName();
				if ( ! alleleNameGwasMappings.containsKey(thisAlleleName) ){
					alleleNameGwasMappings.put(thisAlleleName, new ArrayList<GwasDTO>());
				}
				alleleNameGwasMappings.get(thisAlleleName).add(tGw);
			}
			for (Map.Entry<String, List<GwasDTO>> entry : alleleNameGwasMappings.entrySet()){
			    List<GwasDTO> aGws = entry.getValue();
			    
			    String thisAlleleName = entry.getKey();
			    String thisAlleleId = aGws.get(0).getMgiAlleleId();
			    String caption = " <caption>IMPC allele: " + thisAlleleName + " (" + thisAlleleId + ")</caption>";
			    
			    List<String> trs = new ArrayList<>();
			    
			    for ( GwasDTO aGw : aGws ) {
			    	List<String> tds = new ArrayList<>();
			    	String mgiBaseLink = baseUrl + "/phenotypes/";
			    	
					tds.add("<td><a href='" + mgiBaseLink + aGw.getMpTermId() + "'>" + aGw.getMpTermName() + "</a></td>");
					tds.add("<td>" + aGw.getMouseGender() + "</td>");
					tds.add("<td>" + Float.toString(aGw.getPvalue()) + "</td>");
					tds.add("<td>" + aGw.getReportedGene() + "</td>");
					tds.add("<td>" + aGw.getMappedGene() + "</td>");
					tds.add("<td>" + aGw.getUpstreamGene() + "</td>");
					tds.add("<td>" + aGw.getDownstreamGene() + "</td>");
					String td = StringUtils.join(tds, "");
					trs.add("<tr>" + td + "</tr>");
			    }	
				
				String table = "<table class='tablesorter'>" + theadRow + caption + "<tbody>"+ StringUtils.join(trs, "") + "</tbody></table>";
				
				System.out.println("table ---- " + table);
				tabDivs.add("<div id='" + tabId + "'>" + table + "</div>");
			    
			}
			
			dataRow.add(StringUtils.join(tabDivs, ""));
		}
		
		if (traits.size() > 1){
			String traitTabs = "<ul class='tabs'>" + StringUtils.join(trts, "") + "</ul>";
			return markerRow + "<div id='tabs'>" + traitTabs + StringUtils.join(dataRow, "") + "</div>";
		}
		else {
			Iterator ti = traits.iterator();
			String traitName = "<div class='trtName'>GWAS disease trait:&nbsp;&nbsp;" + ti.next().toString() + "</div>";
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
