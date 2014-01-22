<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:genericpage>
	<jsp:attribute name="title">Gene details for ${gene.name}</jsp:attribute>
	<jsp:attribute name="breadcrumb">&nbsp;&raquo; <a href="${baseUrl}/search#sort=marker_symbol asc&q=*:*&core=gene">Genes</a> &raquo; ${gene.symbol}</jsp:attribute>
	<jsp:attribute name="bodyTag"><body  class="gene-node no-sidebars small-header"></jsp:attribute>
	<jsp:attribute name="addToFooter">
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
    
	</jsp:attribute>
	

	<jsp:attribute name="header">
	
		<!-- CSS Local Imports -->
		<!-- link rel="stylesheet" type="text/css" href="${baseUrl}/css/ui.dropdownchecklist.themeroller.css"/-->
		
		<!-- JavaScript Local Imports -->
		<script src="${baseUrl}/js/general/toggle.js"></script>
		<script src="${baseUrl}/js/imaging/genomicB.js"></script>
		<script src="${baseUrl}/js/general/enu.js"></script>
		<script src="${baseUrl}/js/general/dropdownfilters.js"></script>
		<!--[if !IE]><!-->
		<script type="text/javascript" src="${baseUrl}/js/genomic-browser/dalliance-compiled.js"></script>
		<!--<![endif]-->
		
		
		<script type="text/javascript">var gene_id = '${acc}';</script>
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
		</style>
	
		<c:if test="${phenotypeStarted}">
	    <!--[if !IE]><!-->
	    <link rel="stylesheet" type="text/css" href="${drupalBaseUrl}/heatmap/css/heatmap.1.3.1.css"/>
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
		<div class="region region-content">
			<div class="block block-system">
				<div class="content">
					<div class="node node-gene">
						<h1 class="title">Gene: ${gene.symbol}  &nbsp;&nbsp; </h1>
						<div class="section">
							<div class="inner">
							<!--  login interest button -->
							<div class="floatright">
								<c:choose>
									<c:when test="${registerButtonAnchor!=''}">
										<p> <a class="btn" href='${registerButtonAnchor}'><i class="fa fa-sign-in"></i>${registerInterestButtonString}</a></p>
									</c:when>
									<c:otherwise>
										<p> <a class="btn" id='${registerButtonId}'><i class="fa fa-sign-in"></i>${registerInterestButtonString}</a></p>
									</c:otherwise>
								</c:choose>
							</div>
							
							<p class="with-label no-margin">
								<span class="label">Name</span>
								${gene.name}
							</p>
							
							<p class="with-label no-margin">
								<span class="label">Synonyms</span>
								<c:forEach var="synonym" items="${gene.synonyms}" varStatus="loop">
									${synonym.symbol}
									<c:if test="${!loop.last}">, </c:if>
									<c:if test="${loop.last}"></p></c:if>
								</c:forEach>
							
							
							<p class="with-label">
								<span class="label">MGI Id</span>
								<a href="http://www.informatics.jax.org/marker/${gene.id.accession}">${gene.id.accession}</a>
							</p>
							
							<p class="with-label">
								<span class="label">Status</span>
								<c:choose>
										<c:when test="${empty geneStatus}">
												<div class="alert alert-error">
									  				<strong>Error:</strong> Gene status currently unavailable.
												</div>
										</c:when>
										<c:otherwise>
											<c:if test="${fn:contains(geneStatus,'produced')}"> <a class="status done" data-hasqtip="37" oldtitle="${geneStatus}" title aria-describebody="qtip-37"></c:if>
											<c:if test="${!fn:contains(geneStatus,'produced')}"> <a class="status inprogress" data-hasqtip="37" oldtitle="${geneStatus}" title aria-describebody="qtip-37"></c:if>
											<span>ES Cell</span>
											</a>
									</c:otherwise>
								</c:choose>
								<c:if test="${not empty phenotypeStatus}">
										TODO Phenotyping Status:
									 	<button type="button" class="btn btn-info" disabled>${phenotypeStatus}</button>
								</c:if>
							</p>
							
							<p class="with-label">
								<span class="label">ENSEMBL Links</span>
								<a href="http://www.ensembl.org/Mus_musculus/Gene/Summary?g=${gene.id.accession}"><i class="fa fa-external-link"></i>&nbsp;Gene&nbsp;View</a>&nbsp;&nbsp;
								<a href="http://www.ensembl.org/Mus_musculus/Location/View?g=${gene.id.accession};contigviewbottom=das:http://das.sanger.ac.uk/das/ikmc_products=labels"><i class="fa fa-external-link"></i>&nbsp;Location&nbsp;View</a>&nbsp;&nbsp;     
								<a href="http://www.ensembl.org/Mus_musculus/Location/Compara_Alignments/Image?align=601;db=core;g=${gene.id.accession}"><i class="fa fa-external-link"></i>&nbsp;Compara&nbsp;View</a> 
							</p>
							
							<c:if test="${makeEnuLink>0}">
								<p class="with-label no-margin" id="enu">
									<span class="label">Other Links</span>
									<a href="https://databases.apf.edu.au/mutations/snpRow/list?mgiAccessionId=${acc}">ENU (${makeEnuLink})</a>
								</p>
							</c:if>
						
						
								<div class="accordion-group">
									<div class="accordion-heading withColorWhenOpen">
										Gene Browser
									</div>
									<div class="accordion-body collapse <c:if test="${status.count ==1}"> in </c:if>">
										<div id="genomebrowser" >
											<div class="floatright">
												<a href="http://www.biodalliance.org/" target="_blank" data-hasqtip="24" title="More information on using this browser"><i class="icon-question-sign"> </i> </a>
												<a data-hasqtip="25" title="This browser is clickable please experiment by clicking. Click on features to get more info, click on zoom bar etc. To reset click on 'lightning button'" title aria-describeby="qtip-25">This is an interactive genomic browser</a>
											</div>
											<p>Gene&nbsp;Location: Chr<span id='chr'>${gene.sequenceRegion.name}</span>:<span id='geneStart'>${gene.start}</span>-<span id='geneEnd'>${gene.end}</span> <br/> Gene Type: ${gene.subtype.name}</p>
												
											<div class="container span6" id="geneLocation1">
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
				</div> <!-- section end -->
				
				<!-- div class="row-fluid">
				    
					<div class="container span12">
					<div class="accordion" id="accordionMoreGeneInfoAccord">
							<div class="accordion-group">
								<div class="accordion-heading">
									<a class="accordion-toggle" data-toggle="collapse" data-target="#accordionMoreGeneInfo">
										Show More Gene Information<i class="icon-chevron-<c:if test="${status.count ==1}">down</c:if><c:if test="${status.count!=1}">right</c:if> pull-left"></i>
									</a>
								</div>
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
		
		
		
		<!--  Phenotype Associations Panel -->
		<div class="section">
			<h2 class="title documentation" id="section-associations"> Phenotype associations for ${gene.symbol} <a href='' id='mpPanel'><i class="fa fa-question-circle pull-right"></i></a></span></h2>
			<div class="inner">
				<div class="abnormalities">TODO</div>
				<c:if test="${phenotypeSummaryObjects.getBothPhenotypes().size() > 0 or phenotypeSummaryObjects.getFemalePhenotypes().size() > 0 or phenotypeSummaryObjects.getMalePhenotypes().size() > 0 }">
		            
					<p> Phenotype Summary based on automated MP annotations supported by experiments on knockout mouse models. </p>
				    <c:if test="${phenotypeSummaryObjects.getBothPhenotypes().size() > 0}">
			        <p> <b>Both sexes</b> have the following phenotypic abnormalities</p>
			        <ul>
			         	<c:forEach var="summaryObj" items="${phenotypeSummaryObjects.getBothPhenotypes()}">
			           	<li><a href="${baseUrl}/phenotypes/${summaryObj.getId()}">${summaryObj.getName()}</a>. Evidence from <c:forEach var="evidence" items="${summaryObj.getDataSources()}" varStatus="loop"> ${evidence} <c:if test="${!loop.last}">,&nbsp;</c:if>  </c:forEach> &nbsp;&nbsp;&nbsp; (<a class="filterTrigger" id="${summaryObj.getName()}">${summaryObj.getNumberOfEntries()}</a>)</li>    
			          </c:forEach>
			        </ul>
		        </c:if>
		                
		        <c:if test="${phenotypeSummaryObjects.getFemalePhenotypes().size() > 0}">
		        	<p> Following phenotypic abnormalities occured in <b>females</b> only</p>
		        	<ul>
			          <c:forEach var="summaryObj" items="${phenotypeSummaryObjects.getFemalePhenotypes()}"> 
			          	<li><a href="${baseUrl}/phenotypes/${summaryObj.getId()}">${summaryObj.getName()}</a>. Evidence from <c:forEach var="evidence" items="${summaryObj.getDataSources()}" varStatus="loop"> ${evidence} <c:if test="${!loop.last}">,&nbsp;</c:if> </c:forEach> &nbsp;&nbsp;&nbsp; (<a class="filterTrigger" id="${summaryObj.getName()}">${summaryObj.getNumberOfEntries()}</a>)</li>                
			          </c:forEach>
		          </ul>
		        </c:if>
		                
		        <c:if test="${phenotypeSummaryObjects.getMalePhenotypes().size() > 0}">
		       		<p> Following phenotypic abnormalities occured in <b>males</b> only</p>
		          	<ul>
		            	<c:forEach var="summaryObj" items="${phenotypeSummaryObjects.getMalePhenotypes()}">
		              	<li><a href="${baseUrl}/phenotypes/${summaryObj.getId()}">${summaryObj.getName()}</a>. Evidence from <c:forEach var="evidence" items="${summaryObj.getDataSources()}" varStatus="loop"> ${evidence} <c:if test="${!loop.last}">,&nbsp;</c:if> </c:forEach> &nbsp;&nbsp;&nbsp;   (<a class="filterTrigger" id="${summaryObj.getName()}">${summaryObj.getNumberOfEntries()}</a>)</li>    
		              </c:forEach>
		            </ul>
		        </c:if>
		
						<!-- Associations table -->
						<h5>Filter this table</h5>
						   
		        
						<div class="row-fluid">
							<div class="container span12">
								<br/>	
								<div class="row-fluid" id="phenotypesDiv">	
									<div class="container span12">
										<div id="filterParams" >
											<c:forEach var="filterParameters" items="${paramValues.fq}">
												${filterParameters}
											</c:forEach>
										</div> 
									<c:if test="${not empty phenotypes}">
											<form class="tablefiltering no-style" id="target" action="destination.html">
												<c:forEach var="phenoFacet" items="${phenoFacets}" varStatus="phenoFacetStatus">
													<select id="${phenoFacet.key}" class="impcdropdown" multiple="multiple" title="Filter on ${phenoFacet.key}">
														<c:forEach var="facet" items="${phenoFacet.value}">
															<option>${facet.key}</option>
														</c:forEach>
													</select> 
												</c:forEach>
											<div class="clear"></div>
											</form>
											<div class="clear"></div>
																				
										<c:set var="count" value="0" scope="page" />
										<c:forEach var="phenotype" items="${phenotypes}" varStatus="status">
											<c:forEach var="sex" items="${phenotype.sexes}"><c:set var="count" value="${count + 1}" scope="page"/></c:forEach>
										</c:forEach>
				
										<jsp:include page="PhenoFrag.jsp"></jsp:include>
										<div id="exportIconsDiv"></div>
									</c:if>
									
									<!-- if no data to show -->
									<c:if test="${empty phenotypes}">
										<div class="alert alert-info">Pre QC data has been submitted for this gene. Once the QC process is finished phenotype associations stats will be made available.</div>
									</c:if>
									
								</div>
							</div>
						</div>
					</div>
					
				</c:if>
			</div>
		</div>
				
		<c:if test="${phenotypeStarted}">
			<div class="section">
			  <h2 class="documentation title" id="heatmap">Pre-QC phenotype heatmap -
					<c:forEach items="${allColonyStatus}" var="colonyStatus">
						<c:if test="${colonyStatus.phenotypeStarted == 1}">
							${colonyStatus.alleleName}<%-- </td><td>${colonyStatus.backgroundStrain}</td><td>${colonyStatus.phenotypeCenter}</td></tr> --%>
						</c:if>
					</c:forEach>	
					<a href='' id='mpPanel'><i class="fa fa-question-circle pull-right"></i></a>
				</h2>
				
				<div class="inner">
					<div class="messages errors">
						<h5>Caution</h5>
						<p>This is the results of a preliminary statistical analysis. Data are still in the process of being quality controlled and results may change.</p>
					</div>
				</div>
				<div class="dcc-heatmap-root">
		     	<div class="phenodcc-heatmap" id="phenodcc-heatmap"></div>
				</div>
			</div> <!-- section end -->
			</c:if>
		
			<c:if test="${not empty imageErrors}">
				<div class="row-fluid dataset">
					<div class="alert"><strong>Warning!</strong>${imageErrors }</div>
				</div>
			</c:if>
		
			<c:if test="${not empty solrFacets}">
				<div class="section">
			    	<h2 class="title documentation">Phenotype Associated Images  <a href='${baseUrl}/images?gene_id=${acc}&fq=!expName:"Wholemount%20Expression"'><small>Show All Images</small></a><a href='' id='imagePanel'><i class="fa fa-question-circle pull-right" aria-describedby="qtip-26"></i></a></h2>
			    	<div class="alert alert-info">Work in progress. Images may depict phenotypes not statistically associated with a mouse strain.</div>	
						<div class="inner">         		
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
			</c:if>
							
					
			<c:if test="${not empty expressionFacets}">
			<div class="section">
				<h2 class="title documentation">Expression <a href='' id='expressionPanel'><i class="fa fa-question-circle pull-right" aria-describedby="qtip-26"></i></a></h2>
				<div id="showAllExpression"></div>
				<div class="inner">			
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
		
			<div class="section">
				<h2 class="title documentation">ES Cell and Mouse Alleles <a href='' id='allelePanel'><i class="fa fa-question-circle pull-right" aria-describedby="qtip-26"></i></a></h2>	
				    <div class="inner"> 			
						<div id="allele_tracker_panel_results">&nbsp;</div>
						<c:choose>
							<c:when test="${countIKMCAllelesError}">
							<div class="alert alert-error">
								<strong>Error:</strong> IKMC allele status currently unavailable.
							</div>
							</c:when>
							<%-- <c:when test="${countIKMCAlleles == 0}">
								<div class="alert alert-info">There are no IKMC alleles available.</div>
							</c:when> --%>
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
		</div>
		</div>
		</div>
  </jsp:body>
</t:genericpage>