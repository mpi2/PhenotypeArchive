<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:genericpage>
	<jsp:attribute name="title">Gene details for ${gene.name}</jsp:attribute>
	
	<jsp:attribute name="breadcrumb">&nbsp;&raquo; <a href="${baseUrl}/search#sort=marker_symbol asc&q=*:*&core=gene">Genes</a> <c:if test="${not empty gene.subtype.name }">&raquo; <a href='${baseUrl}/search#fq=marker_type:"${gene.subtype.name}"&q=*:*&core=gene'>${gene.subtype.name}</a></c:if> &raquo; ${gene.symbol}</jsp:attribute>

	<jsp:attribute name="footer">

	<!--[if !IE]><!-->
	<script type="text/javascript" src="${baseUrl}/js/genomic-browser/dalliance-all.js"></script>
	<!--<![endif]-->


	</jsp:attribute>
	

	<jsp:attribute name="header">


	<script type="text/javascript">var gene_id = '${acc}';</script>

	<script src="${baseUrl}/js/imaging/properties.js"></script>
	<script src="${baseUrl}/js/imaging/genomicB.js"></script>
	<script src="${baseUrl}/js/imaging/mp.js"></script>
	<script src="${baseUrl}/js/imaging/imageUtils.js"></script>
	<script src="${baseUrl}/js/general/toggle.js"></script>
	<script src="${baseUrl}/js/mpi2_search/all.js"></script>


<style>

@media only screen and (max-width: 800px) {
	/* Force table to not be like tables anymore */
	#allele_tracker_panel_results table,
	#allele_tracker_panel_results thead,
	#allele_tracker_panel_results tbody,
	#allele_tracker_panel_results th,
	#allele_tracker_panel_results td,
	#allele_tracker_panel_results tr
	{display: block;}

	/* Hide table headers (but not display: none;, for accessibility) */
	#allele_tracker_panel_results thead tr {position: absolute;top: -9999px;left: -9999px;}
	#allele_tracker_panel_results tr {border: 1px solid #ccc;}
	#allele_tracker_panel_results td { /* Behave  like a "row" */
		border: none;
		border-bottom: 1px solid #eee;
		position: relative;
		padding-left: 50%;
		white-space: normal;
		text-align: left;
	}
	#allele_tracker_panel_results td:before { /* Now like a table header */
		position: absolute;
		/* Top/left values mimic padding */
		top: 6px;
		left: 6px;
		width: 45%;
		padding-right: 10px;
		white-space: nowrap;
		text-align: left;
		font-weight: bold;
	}

	/*
	Label the data
	*/
	#allele_tracker_panel_results td:before {
		content: attr(data-title);
	}
	#allele_tracker_panel_results td:nth-of-type(1):before {
		content: "Product"
	}
	#allele_tracker_panel_results td:nth-of-type(2):before {
		content: "Allele"
	}
	#allele_tracker_panel_results td:nth-of-type(3):before {
		content: "Type"
	}
	#allele_tracker_panel_results td:nth-of-type(4):before {
		content: "Strain of Origin"
	}
	#allele_tracker_panel_results td:nth-of-type(5):before {
		content: "MGI Allele Name"
	}
	#allele_tracker_panel_results td:nth-of-type(6):before {
		content: "Allele Map"
	}
	#allele_tracker_panel_results td:nth-of-type(7):before {
		content: "Allele Sequence"
	}
	#allele_tracker_panel_results td:nth-of-type(8):before {
		content: "Order"
	}
}
#svgHolder div div {z-index:100;}

</style>

    </jsp:attribute>

	<jsp:body>

	<div class='topic'>Gene: ${gene.symbol}  &nbsp;&nbsp;
		<c:choose>
		<c:when test="${registerButtonAnchor!=''}"><a href='${registerButtonAnchor}'  id='${registerButtonId}'  class='btn primary'>${registerInterestButtonString}</a></c:when>
		<c:otherwise><a  id='${registerButtonId}'  class='btn primary interest'>${registerInterestButtonString}</a></c:otherwise>
		</c:choose>
	</div>

	<div class="row-fluid dataset">
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
								<c:if test="${loop.count==2 && fn:length(gene.synonyms)>2}"><a  data-toggle="collapse" data-target="#other_synonyms" href="#">+...</a><div id="other_synonyms" class="collapse"></c:if>
								
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
					<tr class="odd">
							<td>Status:</td>
						<td><button type="button" class="btn btn-info" disabled>${geneStatus}</button></td></tr>
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

		<div class="row-fluid">
				<div class="container span12">
				<button type="button" data-toggle="collapse" data-target="#other_ids" id="showGBrowser">Show/Hide Genome Browser</button>

				<div id="other_ids" class="collapse">
			
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
	<!--/row-->


		<div class="row-fluid dataset">
			<div class="row-fluid">
				<div class="container span12">
					<h4 class="caption">Phenotype Associations</h4>
				</div>
			</div>
			
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
						
			<div class="row-fluid">	
				<div class="container span12">
					<c:if test="${not empty phenotypes}">
					<table id="phenotypes" class="table table-striped">
					<thead>
						<tr>
							<th>Phenotype</th>
							<th>Allele</th>
							<th>Zygosity</th>
							<th>Sex</th>
							<th>Data</th>
							<%-- <th>Strain</th> --%>
						</tr>
					</thead>
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
					<c:when test="${phenotype.linkToOriginalDataProvider eq ''}">
						${phenotype.dataSourceName}
					</c:when>
					<c:otherwise>
					<a href="${phenotype.linkToOriginalDataProvider }">${phenotype.dataSourceName}</a>
					</c:otherwise>
					</c:choose>
					</td>
					<%-- <td>${phenotype.strain.name}</td> --%>
					</tr>
					</c:forEach>
					</table>
					<style>
					</style>
					<script>
					$(document).ready(function(){						
						
						// use jquery DataTable for table searching/sorting/pagination
						var aDataTblCols = [0,1,2,3,4];
						var oDataTable = $.fn.initDataTable($('table#phenotypes'), {
							//"aaSorting": [[0, "asc"], [1, "asc"]],   			     
							"aoColumns": [
								{ "sType": "html", "mRender":function( data, type, full ) {
							        return (type === "filter") ? $(data).text() : data;
							    }},
								{ "sType": "html", "mRender":function( data, type, full ) {
							        return (type === "filter") ? $(data).text() : data;
							    }},
							    { "sType": "string"},
								{ "sType": "alt-string", "bSearchable" : false },
								{ "sType": "string" }

							],
			   	    		"iDisplayLength": 10   // 10 rows as default 
						});

						$('[rel=tooltip]').tooltip();
													    		
			    		$.fn.dataTableshowAllShowLess(oDataTable, aDataTblCols, null);
			    		$('div#phenotypes_wrapper').append($.fn.loadFileExporterUI({
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
			    		
			    		
					});
					</script>
					</c:if>
					<c:if test="${empty phenotypes}">
					<div class="alert alert-info">You'll see EuroPhenome phenotype data when available. You'll find links to the Wellcome Trust Sanger Institute mouse portal and to IMPC preQC data when appropriate.</div>
					</c:if>
				</div>
			</div>
		</div>
		
<c:if test="${not empty solrFacets}">
	<div class="row-fluid dataset">
		<div class="row-fluid">
			<div class="container span12">
				<h4 class="caption">Phenotype Associated Images  <a href='${baseUrl}/images?gene_id=${acc}&fq=!expName:"Wholemount%20Expression"'><small>Show All Images</small></a></h4><div class="alert alert-info">Work in progress. Images may depict phenotypes not statistically associated with a mouse strain.</div>
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
								<li class="span2">
									<a href="${mediaBaseUrl}/${doc.fullResolutionFilePath}">
									<img src="${mediaBaseUrl}/${doc.smallThumbnailFilePath}" /></a>
									<c:forEach var="maTerm" items="${doc.annotationTermName}" varStatus="status">${maTerm}<br/></c:forEach>
									<c:if test="${not empty doc.genotype}">${doc.genotype}<br/></c:if>
									<c:if test="${not empty doc.gender}">${doc.gender}<br/></c:if>
										<c:if test="${not empty doc.institute}"><c:forEach var="org" items="${doc.institute}">${ org}<br /></c:forEach></c:if> 
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
			
			
			<c:if test="${not empty expressionFacets}">
			<div class="row-fluid dataset">
		<div class="row-fluid">
				<div class="container span12">
					<h4 class="caption">Expression</h4><div id="showAllExpression"></div>
						
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
					<a href="${mediaBaseUrl}/${doc.fullResolutionFilePath}">
							<img src="${mediaBaseUrl}/${doc.smallThumbnailFilePath}" /></a>
								<c:forEach var="maTerm" items="${doc.annotationTermName}" varStatus="status">${maTerm}<br/></c:forEach>
								<c:if test="${not empty doc.genotype}">${doc.genotype}<br/></c:if>
								<c:if test="${not empty doc.genotype}">${doc.gender}<br/></c:if>
								<c:if test="${not empty doc.institute}"><c:forEach var="org" items="${doc.institute}">${ org}<br /></c:forEach></c:if> 
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
			<div class="container span12">

				<h4 class="caption">ES Cell and Mouse Alleles</h4>
				<div id="allele_tracker_panel_results">&nbsp;</div>

					<c:choose><c:when test="${countIKMCAlleles == 0}">
					<div class="alert alert-info">There is no IKMC allele available</div>
					</c:when>
					<c:otherwise>

				<!-- END OF ALLELE TRACKER PANEL -->
				<script type="text/javascript">
					var mgiAccession = gene_id;
					//jQuery('#allele_tracker_panel_results')
							//.load("proxy?url=https://beta.mousephenotype.org/i-dcc/martsearch/impc_search?mgi_accession_id="+ mgiAccession);
					
					jQuery('#allele_tracker_panel_results').mpi2GenePageAlleleGrid().trigger('search', {solrParams: {q:'mgi_accession_id:'+ mgiAccession}});
				</script>
				</c:otherwise>
				</c:choose>

			</div>
			<!--/span-->
		</div>
		<!--/row-->

    </jsp:body>
</t:genericpage>
