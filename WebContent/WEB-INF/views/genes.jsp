<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:genericpage>
	<jsp:attribute name="title">Gene details for ${gene.name}</jsp:attribute>
	
   <jsp:attribute name="breadcrumb">&nbsp;&raquo; <a href="${baseUrl}/search#sort=marker_symbol asc&q=*:*&core=gene">Genes</a> &raquo; ${gene.symbol}</jsp:attribute>

	<jsp:attribute name="footer">

	<c:if test="${phenotypeStarted}">
	<script type="text/javascript" src="${drupalBaseUrl}/heatmap/js/heatmap.1.3.1.js"></script>
	<!--[if IE 8]>
        <script type="text/javascript">
        dcc.ie8 = true;
        </script>
	<![endif]-->
	<script>
          //new dcc.PhenoHeatMap('procedural', 'phenodcc-heatmap', '${gene.symbol}', '${gene.id.accession}', 6, '${fn:replace(drupalBaseUrl, "https:", "")}/heatmap/rest/heatmap/');
          new dcc.PhenoHeatMap({
                /* identifier of <div> node that will host the heatmap */
                'container': 'phenodcc-heatmap',

                /* colony identifier (MGI identifier) */
                'mgiid': '${gene.id.accession}',

                /* default usage mode: ontological or procedural */
                'mode': 'ontological',

                /* number of phenotype columns to use per section */
                'ncol': 5,

                /* heatmap title to use */
                'title': '${gene.symbol}',

                'url': {
                    /* the base URL of the heatmap javascript source */
                    'jssrc': '${drupalBaseUrl}/heatmap/js/',

                    /* the base URL of the heatmap data source */
                    'json': '${fn:replace(drupalBaseUrl, "https:", "")}/heatmap/rest/',

                    /* function that generates target URL for data visualisation */
                    'viz': function(genotype_id, type) {
                        return '${drupalBaseUrl}/phenotypedata?g=' + genotype_id
                            + '&t=' + type + '&w=all';
                    }
                }
            });
	</script>
	</c:if>

	<!--[if !IE]><!-->
	<script type="text/javascript" src="${baseUrl}/js/genomic-browser/dalliance-compiled.js"></script>
	<!--<![endif]-->
	<script src="${baseUrl}/js/imaging/genomicB.js"></script>
	</jsp:attribute>
	

	<jsp:attribute name="header">
	<script type="text/javascript">var gene_id = '${acc}';</script>
	<%-- <link rel="stylesheet" type="text/css" href="${baseUrl}/css/ui.dropdownchecklist.standalone.css"/> --%>
<link rel="stylesheet" type="text/css" href="${baseUrl}/css/ui.dropdownchecklist.themeroller.css"/>
<link rel="stylesheet" type="text/css" href="${baseUrl}/css/custom.css"/>
	<script src="${baseUrl}/js/general/toggle.js"></script>
<!-- <script src="http://dropdown-check-list.googlecode.com/svn/trunk/doc/jquery-ui-1.8.13.custom.min.js"></script> -->
	<style>
	/* Force allele table to not be like tables anymore for responsive layout */
	@media only screen and (max-width: 800px) {
		#allele_tracker_panel_results table,
		#allele_tracker_panel_results thead,
		#allele_tracker_panel_results tbody,
		#allele_tracker_panel_results th,
		#allele_tracker_panel_results td,
		#allele_tracker_panel_results tr{display: block;}
		#allele_tracker_panel_results thead tr {position: absolute;top: -9999px;left: -9999px;}
		#allele_tracker_panel_results tr {border: 1px solid #ccc;}
		#allele_tracker_panel_results td {border: none;border-bottom: 1px solid #eee;position: relative;padding-left: 50%;white-space: normal;text-align: left;}
		#allele_tracker_panel_results td:before {position: absolute;top: 6px;left: 6px;width: 45%;padding-right: 10px;white-space: nowrap;text-align: left;font-weight: bold;}
		#allele_tracker_panel_results td:before {content: attr(data-title);}
		#allele_tracker_panel_results td:nth-of-type(1):before {content: "Product"}
		#allele_tracker_panel_results td:nth-of-type(2):before {content: "Allele"}
		#allele_tracker_panel_results td:nth-of-type(3):before {content: "Type"}
		#allele_tracker_panel_results td:nth-of-type(4):before {content: "Strain of Origin"}
		#allele_tracker_panel_results td:nth-of-type(5):before {content: "MGI Allele Name"}
		#allele_tracker_panel_results td:nth-of-type(6):before {content: "Allele Map"}
		#allele_tracker_panel_results td:nth-of-type(7):before {content: "Allele Sequence"}
		#allele_tracker_panel_results td:nth-of-type(8):before {content: "Order"}
	}
	#svgHolder div div {z-index:100;}
	</style>

	<c:if test="${phenotypeStarted && !isLive}">
        <!--[if !IE]><!-->
        <link rel="stylesheet" type="text/css" href="${drupalBaseUrl}/heatmap/css/heatmap.1.3.1.css">
        <!--<![endif]-->
        <!--[if IE 8]>
        <link rel="stylesheet" type="text/css" href="${drupalBaseUrl}/heatmap/css/heatmapIE8.1.2.css">
        <![endif]-->
        <!--[if gte IE 9]>
        <link rel="stylesheet" type="text/css" href="${drupalBaseUrl}/heatmap/css/heatmap.1.3.1.css">
        <![endif]-->
	</c:if>
        
    </jsp:attribute>

	<jsp:body>

		<div class='topic'>Gene: ${gene.symbol}  &nbsp;&nbsp;
		<c:choose>
		<c:when test="${registerButtonAnchor!=''}"><a href='${registerButtonAnchor}'  id='${registerButtonId}'  class='btn primary'>${registerInterestButtonString}</a></c:when>
		<c:otherwise><a  id='${registerButtonId}'  class='btn primary interest'>${registerInterestButtonString}</a></c:otherwise>
		</c:choose>
	</div>

	<div class="row-fluid dataset">
		<div class='documentation'><a href='' class='generalPanel'><img src="${baseUrl}/img/info_20x20.png" /></a></div>
		<div class="row-fluid">			
			<div class="container span12">
						<div class="row-fluid">
			<div class="container span6">
				<table>				  
					<tbody>
						<tr class="odd">
							<td>Gene name:</td>
							<td class="gene-data" id="gene_name">${gene.name}</td>
						</tr>
						<tr class="even">
							<td>Synonyms:</td>
							<td class="gene-data" id="synonyms">
								<c:forEach var="synonym" items="${gene.synonyms}" varStatus="loop">${synonym.symbol}<c:if test="${!loop.last}"><br /></c:if>
								<c:if test="${loop.count==2 && fn:length(gene.synonyms)>2}"><a data-toggle="collapse" data-target="#other_synonyms" href="#">+...</a><div id="other_synonyms" class="collapse"></c:if>
								<c:if test="${loop.last && fn:length(gene.synonyms) >2}"></div></c:if>
								</c:forEach>
 							</td>
						</tr>
						<tr class="odd">
							<td>MGI Id:</td>
							<td class="gene-data" id="mgi_id"><a href="http://www.informatics.jax.org/marker/${gene.id.accession}">${gene.id.accession}</a></td>
						</tr>
					</tbody>
				</table>
			</div>
			<div class="container span6">
				<table>				  
					<tbody>
						<tr>
							<td>Production Status:</td>
							<td>	
							<c:choose>
								<c:when test="${empty geneStatus}">
										<div class="alert alert-error">
							  				<strong>Error:</strong> Gene status currently unavailable.
										</div>
								</c:when>
								<c:otherwise>
										<button type="button" class="btn btn-info" disabled>${geneStatus}</button>
								</c:otherwise>
							</c:choose>
							</td>
							
						</tr>
						<c:if test="${not empty phenotypeStatus}">
								<tr>
										<td>Phenotyping Status:</td>
							    		<td><button type="button" class="btn btn-info" disabled>${phenotypeStatus}</button></td>
								</tr>
						</c:if>
						
						<tr class="even">
							<td>Ensembl Links:</td>
							<td class="gene-data" id="ensembl_links">
								<a href="http://www.ensembl.org/Mus_musculus/Gene/Summary?g=${gene.id.accession}">Gene&nbsp;View</a>
								<a href="http://www.ensembl.org/Mus_musculus/Location/View?g=${gene.id.accession};contigviewbottom=das:http://das.sanger.ac.uk/das/ikmc_products=labels">Location&nbsp;View</a>      
								<a href="http://www.ensembl.org/Mus_musculus/Location/Compara_Alignments/Image?align=601;db=core;g=${gene.id.accession}">Compara&nbsp;View</a>
					       </td>
						</tr>
					</tbody>
				</table>
			</div>
		</div>
				
			</div>
			
			
		</div>


		<div class="row-fluid">
		    
			<div class="container span12">
			<div class="accordion" id="accordionMoreGeneInfoAccord">
					<div class="accordion-group">
						<div class="accordion-heading">
							<a class="accordion-toggle" data-toggle="collapse" data-target="#accordionMoreGeneInfo">
								Show More Gene Information<i class="icon-chevron-<c:if test="${status.count ==1}">down</c:if><c:if test="${status.count!=1}">right</c:if> pull-left"></i>
							</a>
						</div>
						<%-- <div id="accordionMoreGeneInfo" class="accordion-body collapse<c:if test="${status.count ==1}"> in</c:if>"> --%>
						<div id="accordionMoreGeneInfo" class="accordion-body collapse <c:if test="${status.count ==1}"> in</c:if>">
							<div class="accordion-inner">
						
							<div class="container span6" id="geneLocation1">Gene&nbsp;Location: Chr<span id='chr'>${gene.sequenceRegion.name}</span>:<span id='geneStart'>${gene.start}</span>-<span id='geneEnd'>${gene.end}</span></div>
					<div class="container span6" >Gene Type: ${gene.subtype.name}</div>
					<span id="genomicBrowserInfo">
						<span class="label label-info" rel="tooltip"  title="This browser is clickable please experiment by clicking. Click on features to get more info, click on zoom bar etc. To reset click on 'lightning button'" disabled>This is an interactive genomic browser </span>
						<a href="http://www.biodalliance.org/"><i class="icon-question-sign" rel="tooltip" title="More information on using this browser"></i></a>
					</span>
					<div class="container span12"  id="svgHolder"></div>
					<table>
						<tbody>
							<c:if test="${not empty vegaIds}">
							<tr>
								<td>Vega Ids:</td>
								<td><c:forEach var="id" items="${vegaIds}" varStatus="loop"><a href="http://vega.sanger.ac.uk/Mus_musculus/geneview?gene=${id}&db=core">${id}</a><c:if test="${!loop.last}"><br /></c:if></c:forEach></td>
							</tr>
							</c:if>
							<c:if test="${not empty ncbiIds}">
							<tr>
								<td>NCBI Id:</td>
								<td><c:forEach var="id" items="${ncbiIds}" varStatus="loop"><a href="http://www.ncbi.nlm.nih.gov/sites/entrez?db=gene&cmd=Retrieve&dopt=Graphics&list_uids=${id}">${id}</a><c:if test="${!loop.last}"><br /></c:if></c:forEach></td>
							</tr>
							</c:if>
							<c:if test="${not empty ccdsIds}">
							<tr>
								<td>CCDS Id:</td>
								<td><c:forEach var="id" items="${ccdsIds}" varStatus="loop"><a href="http://www.ncbi.nlm.nih.gov/CCDS/CcdsBrowse.cgi?REQUEST=CCDS&DATA=${id}">${id}</a><c:if test="${!loop.last}"><br /></c:if></c:forEach></td>
							</tr>
							</c:if>
						</tbody>
					</table>
								
								
							</div>
						</div>
					</div>
				</div>

			</div>
		</div>
	</div><!--/row-->

	<c:if test="${phenotypeStarted}">
	<div class="row-fluid dataset">
	    <div class='documentation'><a href='' class='preQcPanel'><img src="${baseUrl}/img/info_20x20.png" /></a></div>
	    <h4 class="caption">Pre-QC phenotype heatmap -
				<c:forEach items="${allColonyStatus}" var="colonyStatus">
						<c:if test="${colonyStatus.phenotypeStarted == 1}">
							${colonyStatus.alleleName}<%-- </td><td>${colonyStatus.backgroundStrain}</td><td>${colonyStatus.phenotypeCenter}</td></tr> --%>
						</c:if>
				</c:forEach>	
			</h4>
		<div class="row-fluid container clearfix" style="float:none;">
			
			<div class="alert alert-block">
			<h4>Caution!</h4>
			This is the results of a preliminary statistical analysis. Data are still in the process of being quality controlled and results may change.
			</div>
		</div>
		<div class="row-fluid">
       		<div class="phenodcc-heatmap" id="phenodcc-heatmap"></div>
		</div>
	</div>
	</c:if>
	<!--/row-->


	<!--row-->
	<div class="row-fluid dataset">
	    <div class='documentation'><a href='' class='mpPanel'><img src="${baseUrl}/img/info_20x20.png" /></a></div>
	    <h4 class="caption">Phenotype Associations from EuroPhenome and WTSI Mouse Genetics Project</h4>
		<div class="row-fluid">
			<div class="container span12">
				
				<div class="row-fluid" id="phenotypesDiv">	
			<div class="container span12">
			<!--  style="display: none;" --><div id="filterParams" >
			<c:forEach var="filterParameters" items="${paramValues.fq}">
			${filterParameters}
			</c:forEach>
			</div> 
				<c:if test="${not empty phenotypes}">
				<form id="target" action="destination.html">
  <!-- <input type="text" value="Hello there" /> -->
					<c:forEach var="phenoFacet" items="${phenoFacets}" varStatus="phenoFacetStatus">
					<%-- 	${phenoFacet.key}
					${phenoFacet.value} --%>
							<select id="${phenoFacet.key}" class="impcdropdown" multiple="multiple" title="Filter on ${phenoFacet.key}">
				
						<option>All</option>
				<c:forEach var="facet" items="${phenoFacet.value}">
				<option>${facet.key}</option>
				</c:forEach>
				</select> 
				</c:forEach>
				<!-- <input type="text" id="myInputTextField"> -->
				<c:if test="${not empty phenoFacet}">
				<input type="submit" class='btn primary' value="Filter" />
				</c:if>
				</form>
				<jsp:include page="PhenoFrag.jsp"></jsp:include>
				<div id="exportIconsDiv"></div>
				<%-- <div id="phenotypesTableDiv">
				<table id="phenotypes" class="table table-striped">
					<thead>
						<tr>
							<th>Phenotype</th>
							<th>Allele</th>
							<th>Zygosity</th>
							<th>Sex</th>
							<th>Data</th>
							<th>Graph</th>
							<th>Strain</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="phenotype" items="${phenotypes}" varStatus="status">
						<c:set var="europhenome_gender" value="Both-Split"/>
						<tr>
						<td><a href="${baseUrl}/phenotypes/${phenotype.phenotypeTerm.id.accession}">${phenotype.phenotypeTerm.name}</a></td>
						<td><c:choose><c:when test="${fn:contains(phenotype.allele.id.accession, 'MGI')}"><a href="http://www.informatics.jax.org/accession/${phenotype.allele.id.accession}"><t:formatAllele>${phenotype.allele.symbol}</t:formatAllele></a></c:when><c:otherwise><t:formatAllele>${phenotype.allele.symbol}</t:formatAllele></c:otherwise></c:choose></td>
						<td>${phenotype.zygosity}</td>
						<td style="font-family:Verdana;font-weight:bold;">
							<c:set var="count" value="0" scope="page" />
							<c:forEach var="sex" items="${phenotype.sexes}"><c:set var="count" value="${count + 1}" scope="page"/><c:if test="${sex == 'female'}"><c:set var="europhenome_gender" value="Female"/><img style="cursor:help;color:#D6247D;" rel="tooltip" data-placement="top" title="Female" alt="Female" src="${baseUrl}/img/icon-female.png" /></c:if><c:if test="${sex == 'male'}"><c:set var="europhenome_gender" value="Male"/><img style="cursor:help;color:#247DD6;margin-left:<c:if test="${count != 2}">16</c:if><c:if test="${count == 2}">4</c:if>px;" rel="tooltip" data-placement="top" title="Male" alt="Male" src="${baseUrl}/img/icon-male.png" /></c:if></c:forEach>
						</td>
						<td>
						<c:choose>
						<c:when test="${phenotype.phenotypeLink eq ''}">
							${phenotype.dataSourceName}
						</c:when>
						<c:otherwise>
						<a href="${phenotype.phenotypeLink }">${phenotype.dataSourceName}</a>
						</c:otherwise>
						</c:choose>
						</td>
						
						<td style="text-align:center"><c:if test="${phenotype.dataSourceName eq 'EuroPhenome' }"><a href="${baseUrl}/stats/genes/${gene.id.accession}?parameterId=${phenotype.parameter.stableId}<c:if test="${fn:length(phenotype.sexes) eq 1}">&gender=${phenotype.sexes[0]}</c:if>&zygosity=${phenotype.zygosity}"><img src="${baseUrl}/img/icon_stats.png" alt="Graph" /></a></c:if></td>
						</tr>
						</c:forEach>
					</tbody>
				</table>
				</div> --%>
				</c:if>
				<c:if test="${empty phenotypes}">
					<div class="alert alert-info">You'll see EuroPhenome phenotype data when available. You'll find links to the Wellcome Trust Sanger Institute mouse portal when appropriate.</div>
				</c:if>
			</div>
		</div>
			</div>
		</div>
		
			<script>
					$(document).ready(function(){						
						
						$.fn.qTip('gene');	// bubble popup for brief panel documentation					
						
						/* var oDataTable = $('table#phenotypes').dataTable();
						oDataTable.fnDestroy();  */
						// use jquery DataTable for table searching/sorting/pagination
						var aDataTblCols = [0,1,2,3,4,5];
						var oDataTable = $.fn.initDataTable($('table#phenotypes'), {
			  						"aoColumns": [
			  							{ "sType": "html", "mRender":function( data, type, full ) {
			  						        return (type === "filter") ? $(data).text() : data;
			  						    }},
			  							{ "sType": "html", "mRender":function( data, type, full ) {
			  						        return (type === "filter") ? $(data).text() : data;
			  						    }},
			  						    { "sType": "string"},
			  							{ "sType": "alt-string", "bSearchable" : false },
			  						   /*  { "sType": "string"}, */
			  						    { "sType": "html"}
			  						    , { "sType": "string", "bSortable" : false }

			  						],
			  						"bDestroy": true,
			  						"bFilter":false
			  					});
						
						$('[rel=tooltip]').tooltip();
													    		
			    		//$.fn.dataTableshowAllShowLess(oDataTable, aDataTblCols, null);
			    		$('div#exportIconsDiv').append($.fn.loadFileExporterUI({
			    			label: 'Export table as:',
			    			formatSelector: {
			    				TSV: 'tsv_phenoAssoc',
			    				XLS: 'xls_phenoAssoc'	    			 					
			    			},
			    			class: 'fileIcon'
			    		}));
			    		
			    		initFileExporter({
							mgiGeneId: '${gene.id.accession}',
							geneSymbol: '${gene.symbol}',
							externalDbId: 3,
							panel: 'phenoAssoc',
							fileName: 'phenotype_associations_of_gene-${gene.symbol}'
						});
			    		function initFileExporter(conf){
			    			
			    	    	$('button.fileIcon').click(function(){
			    	    		var classString = $(this).attr('class');	    		
			    	    		//var controller = classString.substr(0, classString.indexOf(" "));
			    	    		
			    	    		var fileType = $(this).text();
			    	    		var url = baseUrl + '/export';	    		
			    	    		var sInputs = '';
			    	    		for ( var k in conf ){
			    	    			sInputs += "<input type='text' name='" + k + "' value='" + conf[k] + "'>";	    			
			    	    		}
			    	    		sInputs += "<input type='text' name='fileType' value='" + fileType.toLowerCase() + "'>";
			    	    		
			    	    		$("<form action='"+ url + "' method=get>" + sInputs + "</form>").appendTo('body').submit().remove();    		
			    	    	}).corner('6px');	 
			    	    }  
			    		
			    		//http://stackoverflow.com/questions/5990386/datatables-search-box-outside-datatable
			    		//to move the input text or reassign the div that does it and hide the other one??
			    		//put filtering in another text field than the default so we can position it with the other controls like dropdown ajax filters for project etc
						/* $('#myInputTextField').keypress(function(){
							oDataTable.fnFilter( $(this).val() );
						}); */
					
			    		//stuff for dropdown tick boxes here
			    		$("#resource_fullname").dropdownchecklist( { firstItemChecksAll: true, emptyText: "Projects: All", icon: {}, minWidth: 150 } );
			    		$("#top_level_mp_term_name").dropdownchecklist( { firstItemChecksAll: true, emptyText: "Top Level MP: All", icon: {} , minWidth: 150} );
			    		// $("select[multiple]").bsmSelect();
			    		//if filter parameters are already set then we need to set them as selected in the dropdowns
			    		var previousParams=$("#filterParams").html();
			    		//alert('previous='+previousParams);
						$('#target').submit(function() {
			  					var rootUrl=window.location.href;
			    			 // alert(rootUrl);
			    			  var newUrl=rootUrl.replace("genes", "genesPhenoFrag");
			    			// alert( $("option:selected").parent().attr("id"));
			    			 var output ='?';
			    			//http://wwwdev.ebi.ac.uk/mi/solr/genotype-phenotype/select/?q=marker_accession_id:MGI:98373&rows=100&version=2.2&start=0&indent=on&defType=edismax&wt=json&facet=true&facet.field=project_name&facet.field=top_level_mp_term_name&fq=top_level_mp_term_name:(%22vision/eye%20phenotype%22%20OR%20%22craniofacial%20phenotype%22)
			    			var array1=$("#resource_fullname").val() || [];
			    			if(array1.length==1){//if only one entry for this parameter then don't use brackets and or
			    				 output+='&fq=resource_fullname:"'+array1[0]+'"';
			    			} 
							if(array1.length>1)	{
			    				output+='&fq=resource_fullname:(';//note " before and after value for solr handle spaces
			    			 		for(var i=0; i<array1.length; i++){
			    						 
			    							 //if(i==0)output+=' " ';
			    						 output+='"'+array1[i]+'"';
			    						 if(i<array1.length-1){
			    							 output+=' OR ';
			    						 }else{
			    							 output+=')';
			    						 }
			    						 //console.log('logging='+array1[i]);
			    			 }
			    		}
			    			 var output2 ='';//='"'+ ($("#top_level_mp_term_name").val() || []).join('"&fq=top_level_mp_term_name:"')+'"';
			    			 var array2=$("#top_level_mp_term_name").val() || [];
			    				if(array2.length==1){//if only one entry for this parameter then don't use brackets and or
				    				 output+='&fq=top_level_mp_term_name:"'+array2[0]+'"';
				    			}
			    				if(array2.length>1){
				    				output+='&fq=top_level_mp_term_name:(';//note " before and after value for solr handle spaces
			    			 			for(var i=0; i<array2.length; i++){
			    			 				 output+='"'+array2[i]+'"';
				    						 if(i<array2.length-1){
				    							 output+=' OR ';
				    						 }else{
				    							 output+=')';
				    						 }
			    			 		}
			    			 }
			    			 newUrl+=output+output2;
			    			 //alert(newUrl);
			    			  $.ajax({
			    				  url: newUrl,
			    				  cache: false
			    				}).done(function( html ) {
			    				  $("#phenotypes_wrapper").html(html);//phenotypes wrapper div has been created by the original datatable so we need to replace this div with the new table and content
			    				  var aDataTblCols = [0,1,2,3,4,5];
									var oDataTable = $.fn.initDataTable($('table#phenotypes'), {
						  						"aoColumns": [
						  							{ "sType": "html", "mRender":function( data, type, full ) {
						  						        return (type === "filter") ? $(data).text() : data;
						  						    }},
						  							{ "sType": "html", "mRender":function( data, type, full ) {
						  						        return (type === "filter") ? $(data).text() : data;
						  						    }},
						  						    { "sType": "string"},
						  							{ "sType": "alt-string", "bSearchable" : false },
						  						   /*  { "sType": "string"}, */
						  						    { "sType": "html"}
						  						    , { "sType": "string", "bSortable" : false }

						  						],
						  						"bDestroy": true,
						  						"bFilter":false
						  					});
			    				  	//alert('calling new table in genes.jsp');
									//$oDataTable.fnDraw(); 
			    				});
			    			  return false;
			    			});
					});
				</script>
		
<%--
		<div class="row-fluid">		
			<c:if test="${bPreQC}">
			<div class="container span6">
				<a href='${qcLink}'><img src="${drupalBaseUrl}/sites/dev.mousephenotype.org/files/images/phenodcc.png" alt="Click here to access preliminary data" style="border-style: none"/>&nbsp;Pre QC data from PhenoDCC Available</a>
			</div>		
			</c:if>
			<c:if test="${bSangerLegacy}">
			<div class="container span6">
				<a href='${sangerLegacyLink}'><img src="${drupalBaseUrl}/sites/dev.mousephenotype.org/files/sangerLogo.png" alt="Click here to access Sanger phenotype data" style="border-style: none"/>&nbsp;Legacy data from Sanger Institute Mouse Resources Portal</a>
			</div>		
			</c:if>
		</div>
		
		<c:if test="${bEurophenomeLegacy}">
			<div class="row-fluid">
				<div class="container span6">
					<a href='${europhenomeLegacyLink}'><img src="${drupalBaseUrl}/sites/dev.mousephenotype.org/files/europhenomeLogo.png" alt="Click here to access Europhenome phenotype data" style="border-style: none"/>&nbsp;Legacy data from Europhenome</a>
				</div>		
			</div>
		</c:if>
 --%>

		</div>

	<!-- row -->
	<c:if test="${not empty imageErrors}">
		<div class="row-fluid dataset">
			<div class="alert"><strong>Warning!</strong>${imageErrors }</div>
		</div>
	</c:if>
	<!-- /row -->

	<!-- row -->
	<c:if test="${not empty solrFacets}">
	<div class="row-fluid dataset">
	    <div class='documentation'><a href='' class='imagePanel'><img src="${baseUrl}/img/info_20x20.png" /></a></div>
	    <h4 class="caption">Phenotype Associated Images  <a href='${baseUrl}/images?gene_id=${acc}&fq=!expName:"Wholemount%20Expression"'><small>Show All Images</small></a></h4><div class="alert alert-info">Work in progress. Images may depict phenotypes not statistically associated with a mouse strain.</div>	
		<div class="row-fluid">         	
			<div class="container span12">				
				<div class="accordion" id="accordion1">
					<c:forEach var="entry" items="${solrFacets}" varStatus="status">
					<div class="accordion-group">
						<div class="accordion-heading">
							<a class="accordion-toggle" data-toggle="collapse" data-target="#pheno${status.count}">
								${entry.name} [${entry.count}]<i class="icon-chevron-<c:if test="${status.count ==1}">down</c:if><c:if test="${status.count!=1}">right</c:if> pull-left"></i>
							</a>
						</div>
						<div id="pheno${status.count}" class="accordion-body collapse<c:if test="${status.count ==1}"> in</c:if>">
							<div class="accordion-inner">
							<a href="${baseUrl}/images?gene_id=${acc}&fq=expName:${entry.name}">[show all ${entry.count} images]</a>
								<ul>
									<c:forEach var="doc" items="${facetToDocs[entry.name]}">
									<%-- <li class="span2">
										<a href="${mediaBaseUrl}/${doc.fullResolutionFilePath}">
										<img src="${mediaBaseUrl}/${doc.smallThumbnailFilePath}" /></a>
										<c:forEach var="maTerm" items="${doc.annotationTermName}" varStatus="status">${maTerm}<br/></c:forEach>
										<c:if test="${not empty doc.genotype}">${doc.genotype}<br/></c:if>
										<c:if test="${not empty doc.gender}">${doc.gender}<br/></c:if>
										<c:if test="${not empty doc.institute}"><c:forEach var="org" items="${doc.institute}">${org}<br /></c:forEach></c:if> 
									</li> --%>
                                                                        <li class="span2">
									<t:imgdisplay img="${doc}" mediaBaseUrl="${mediaBaseUrl}"></t:imgdisplay>
                                                                        </li>
									</c:forEach>
								</ul>
							</div>
						</div>
					</div>
					</c:forEach>
				</div>
			</div>
		</div>
	</div>
	</c:if>
	<!-- /row -->

			
			
	<c:if test="${not empty expressionFacets}">
	<div class="row-fluid dataset">
		<div class='documentation'><a href='' class='expressionPanel'><img src="${baseUrl}/img/info_20x20.png" /></a></div>
		<h4 class="caption">Expression</h4><div id="showAllExpression"></div>
		<div class="row-fluid">			
			<div class="container span12">				
			</div>
			<div class="row-fluid">
				<div class="container span12">
					<!-- thumbnail scroller markup begin -->
					<div id="expressionInfo">
						<div class="accordion" id="accordion2">
	   						<c:forEach var="entry" items="${expressionFacets}" varStatus="status">
  							<div class="accordion-group">
								<div class="accordion-heading">
									<a class="accordion-toggle" data-toggle="collapse" data-target="#collapse${status.count}" >
										${entry.name}  [${entry.count}]<i class="icon-chevron-<c:if test="${status.count ==1}">down</c:if><c:if test="${status.count!=1}">right</c:if> pull-left"></i>
									</a>
								</div>
								<div id="collapse${status.count}" class="accordion-body collapse<c:if test="${status.count ==1}"> in</c:if>">
									<div class="accordion-inner">
							 			<a href='${baseUrl}/images?gene_id=${acc}&q=expName:"Wholemount Expression"&fq=higherLevelMaTermName:"${entry.name}"'>[show all  ${entry.count} images]</a>
										<ul>
										<c:forEach var="doc" items="${expFacetToDocs[entry.name]}">
                                                                                    <li class="span2">
											<t:imgdisplay img="${doc}" mediaBaseUrl="${mediaBaseUrl}"></t:imgdisplay>
                                                                                    </li>
    	  								</c:forEach>
										</ul>
									</div>
								</div>
							</div>
						</c:forEach>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	</c:if>
	<!--/row-->

	<div class="row-fluid dataset">
	<div class='documentation'><a href='' class='allelePanel'><img src="${baseUrl}/img/info_20x20.png" /></a></div>
	<h4 class="caption">ES Cell and Mouse Alleles</h4>	
	    <div class="row-fluid"> 
			<div class="container span12">
			
			<div id="allele_tracker_panel_results">&nbsp;</div>
			<c:choose>
				<c:when test="${countIKMCAllelesError}">
				<div class="alert alert-error">
					<strong>Error:</strong> IKMC allele status currently unavailable.
				</div>
				</c:when>
				<c:when test="${countIKMCAlleles == 0}">
					<div class="alert alert-info">There are no IKMC alleles available.</div>
				</c:when>
				<c:otherwise>
					<script src="${baseUrl}/js/mpi2_search/all.js"></script>
					<script type="text/javascript">
						var mgiAccession = gene_id;
						jQuery('#allele_tracker_panel_results').mpi2GenePageAlleleGrid().trigger('search', {solrParams: {q:'mgi_accession_id:'+ mgiAccession}});
					</script>
				</c:otherwise>
			</c:choose>
		</div>
	</div>
	</div>

    </jsp:body>
</t:genericpage>
