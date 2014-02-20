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
    <!--[if !IE]><!-->
    <script>
        dcc.heatmapUrlGenerator = function(genotype_id, type) {
            return '${drupalBaseUrl}/phenoview?gid=' + genotype_id + '&qeid=' + type;
        };
    </script>
    <!--<![endif]-->
    <!--[if lt IE 9]>
    <script>
        dcc.heatmapUrlGenerator = function(genotype_id, type) {
           return '${drupalBaseUrl}/phenotypedata?g=' + genotype_id + '&t=' + type + '&w=all';
        };
    </script>
    <![endif]-->
    <!--[if gte IE 9]>
    <script>
        dcc.heatmapUrlGenerator = function(genotype_id, type) {
           return '${drupalBaseUrl}/phenoview?gid=' + genotype_id + '&qeid=' + type;
        };
    </script>
    <![endif]-->
    <script>
          //new dcc.PhenoHeatMap('procedural', 'phenodcc-heatmap', 'Fam63a', 'MGI:1922257', 6, '//dev.mousephenotype.org/heatmap/rest/heatmap/');
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
                    'viz': dcc.heatmapUrlGenerator
                }
            });
    </script>
    </c:if>

	<!--[if !IE]><!-->
	<script type="text/javascript" src="${baseUrl}/js/genomic-browser/dalliance-compiled.js"></script>
	<!--<![endif]-->
	<script src="${baseUrl}/js/imaging/genomicB.js"></script>
		<script src="${baseUrl}/js/general/dropdownfilters.js"></script>
	</jsp:attribute>
	

	<jsp:attribute name="header">
	<script type="text/javascript">var gene_id = '${acc}';</script>
	<%-- <link rel="stylesheet" type="text/css" href="${baseUrl}/css/ui.dropdownchecklist.standalone.css"/> --%>
<link rel="stylesheet" type="text/css" href="${baseUrl}/css/ui.dropdownchecklist.themeroller.css"/>
<link rel="stylesheet" type="text/css" href="${baseUrl}/css/custom.css"/>
	<script src="${baseUrl}/js/general/toggle.js"></script>
	<script src="${baseUrl}/js/general/enu.js"></script>
        <script src="${baseUrl}/js/general/allele.js"></script>
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
		#allele_tracker_panel_results td:nth-of-type(2):before {content: "Allele Type"}
		#allele_tracker_panel_results td:nth-of-type(3):before {content: "Strain of Origin"}
		#allele_tracker_panel_results td:nth-of-type(4):before {content: "MGI Allele Name"}
		#allele_tracker_panel_results td:nth-of-type(5):before {content: "Allele Map"}
		#allele_tracker_panel_results td:nth-of-type(6):before {content: "Allele Sequence"}
		#allele_tracker_panel_results td:nth-of-type(7):before {content: "Order"}
	}
	#svgHolder div div {z-index:100;}
	.ui-dropdownchecklist-selector > .ui-icon {margin-top:4px;}
	.ui-dropdownchecklist-text {padding:2px;margin:0;}
	</style>

	<c:if test="${phenotypeStarted}">
        <!--[if !IE]><!-->
        <link rel="stylesheet" type="text/css" href="${drupalBaseUrl}/heatmap/css/heatmap.1.3.1.css">
        <!--<![endif]-->
        <!--[if IE 8]>
        <link rel="stylesheet" type="text/css" href="${drupalBaseUrl}/heatmap/css/heatmapIE8.1.3.1.css">
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
		<div class='documentation'><a href='${baseUrl}/documentation/gene-help.html#details' class='generalPanel'><img src="${baseUrl}/img/info_20x20.png" /></a></div>
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
						
						 <tr class="odd" id="enu">
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
	<c:if test="${phenotypeStarted or not empty phenotypes}">
	<div class="row-fluid dataset">
	    <div class='documentation'><a href='' class='mpPanel'><img src="${baseUrl}/img/info_20x20.png" /></a></div>
	    <h4 class="caption">Phenotype associations for ${gene.symbol}</h4>
	    
	     <div class="row-fluid">
						<c:if test="${phenotypeSummaryObjects.getBothPhenotypes().size() > 0 or phenotypeSummaryObjects.getFemalePhenotypes().size() > 0 or phenotypeSummaryObjects.getMalePhenotypes().size() > 0 }">
                            
            <div class="container span12">
								              
                <div class="row-fluid" id="phenotypesSummary">
             
                	<p>Phenotype Summary based on automated MP annotations supported by experiments on knockout mouse models.</p>
              
                
                <c:if test="${phenotypeSummaryObjects.getBothPhenotypes().size() > 0}">
                    <p> <b>Both sexes</b> have the following phenotypic abnormalities</p>
                        <ul>
                            <c:forEach var="summaryObj" items="${phenotypeSummaryObjects.getBothPhenotypes()}">
                                    <li><a href="${baseUrl}/phenotypes/${summaryObj.getId()}">${summaryObj.getName()}</a>. Evidence from
                                    <c:forEach var="evidence" items="${summaryObj.getDataSources()}" varStatus="loop">
                                    ${evidence}
                                        <c:if test="${!loop.last}">,&nbsp;</c:if>
                                    </c:forEach>  
                                    &nbsp;&nbsp;&nbsp; (<a class="filterTrigger" id="${summaryObj.getName()}">${summaryObj.getNumberOfEntries()}</a>)</li>    
                            </c:forEach>
                        </ul>
                </c:if>
                
                <c:if test="${phenotypeSummaryObjects.getFemalePhenotypes().size() > 0}">
                <p> Following phenotypic abnormalities occured in <b>females</b> only</p>
                    <ul>
                        <c:forEach var="summaryObj" items="${phenotypeSummaryObjects.getFemalePhenotypes()}">
                                <li><a href="${baseUrl}/phenotypes/${summaryObj.getId()}">${summaryObj.getName()}</a>. Evidence from
                                    <c:forEach var="evidence" items="${summaryObj.getDataSources()}" varStatus="loop">
                                    ${evidence}
                                        <c:if test="${!loop.last}">,&nbsp;</c:if>
                                    </c:forEach>
                                     &nbsp;&nbsp;&nbsp; (<a class="filterTrigger" id="${summaryObj.getName()}">${summaryObj.getNumberOfEntries()}</a>)</li>                
                        </c:forEach>
                    </ul>
                </c:if>
                
                <c:if test="${phenotypeSummaryObjects.getMalePhenotypes().size() > 0}">
                <p> Following phenotypic abnormalities occured in <b>males</b> only</p>
                    <ul>
                        <c:forEach var="summaryObj" items="${phenotypeSummaryObjects.getMalePhenotypes()}">
                                <li><a href="${baseUrl}/phenotypes/${summaryObj.getId()}">${summaryObj.getName()}</a>. Evidence from                     
                                <c:forEach var="evidence" items="${summaryObj.getDataSources()}" varStatus="loop">
                                    ${evidence}
                                    <c:if test="${!loop.last}">,&nbsp;</c:if>
                                </c:forEach>
                                &nbsp;&nbsp;&nbsp;   (<a class="filterTrigger" id="${summaryObj.getName()}">${summaryObj.getNumberOfEntries()}</a>)</li>    
                        </c:forEach>
                    </ul>
                </c:if>
                </div>
            </div>
              </c:if>
        </div>
        
		<div class="row-fluid">
			<div class="container span12">
			<br/>	
				<div class="row-fluid" id="phenotypesDiv">	
			<div class="container span12">
			<!--  style="display: none;" --><div id="filterParams" >
			<c:forEach var="filterParameters" items="${paramValues.fq}">
			${filterParameters}
			</c:forEach>
			</div> 
				<c:if test="${not empty phenotypes}">
				<form id="target" action="destination.html">
					<c:forEach var="phenoFacet" items="${phenoFacets}" varStatus="phenoFacetStatus">
							<select id="${phenoFacet.key}" class="impcdropdown" multiple="multiple" title="Filter on ${phenoFacet.key}">
				<c:forEach var="facet" items="${phenoFacet.value}">
				<option>${facet.key}</option>
				</c:forEach>
				</select> 
				</c:forEach>
				</form>

	<c:set var="count" value="0" scope="page" />
	<c:forEach var="phenotype" items="${phenotypes}" varStatus="status">
		<c:forEach var="sex" items="${phenotype.sexes}"><c:set var="count" value="${count + 1}" scope="page"/></c:forEach>
	</c:forEach>

				<jsp:include page="PhenoFrag.jsp"></jsp:include>
				<div id="exportIconsDiv"></div>
				</c:if>
				<c:if test="${empty phenotypes}">
					<div class="alert alert-info">Pre QC data has been submitted for this gene. Once the QC process is finished phenotype associations stats will be made available.</div>
				</c:if>
			</div>
		</div>
			</div>
		</div>
		</div>
		</c:if>

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
							 			<a href='${baseUrl}/images?gene_id=${acc}&q=expName:"Wholemount Expression"&fq=annotated_or_inferred_higherLevelMaTermName:"${entry.name}"'>[show all  ${entry.count} images]</a>
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

        
        <div class="section">
        <!-- remove div --><div class="row-fluid dataset">
            <div class="documentation"><a href="${baseUrl}/documentation/gene-help.html#alleles" class="allelePanel"><img src="${baseUrl}/img/info_20x20.png"></a></div>
		<!-- <h2>--><h4 class="caption">ES Cell and Mouse Alleles  
       <!--             <a href="${baseUrl}/documentation/gene-help.html#alleles" id='allelePanel'><i class="fa fa-question-circle pull-right" aria-describedby="qtip-26"></i></a>
       -->
                 <!-- </h2> --></h4>	
		<div class="inner">
                        <div id="allele"></div>
        <!-- remove div --></div>
	</div>

    </jsp:body>
</t:genericpage>
