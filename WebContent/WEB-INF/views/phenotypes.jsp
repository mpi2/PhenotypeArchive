<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix='fn' uri='http://java.sun.com/jsp/jstl/functions'%>

<t:genericpage>

	<jsp:attribute name="title">${phenotype.id.accession} (${phenotype.name}) | IMPC Phenotype Information</jsp:attribute>
	
	<jsp:attribute name="breadcrumb">&nbsp;&raquo; <a href="${baseUrl}/search#q=*:*&core=mp&fq=ontology_subset:*">Phenotypes</a> &raquo; ${phenotype.name}</jsp:attribute>

	<jsp:attribute name="header">

	<!-- CSS Local Imports -->
		<link rel="stylesheet" type="text/css" href="${drupalBaseUrl}/mp-heatmap/heatmap/css/heatmap.css">
		<!-- link rel="stylesheet" type="text/css" href="${baseUrl}/css/ui.dropdownchecklist.themeroller.css" />
		<link rel="stylesheet" type="text/css" href="${baseUrl}/css/custom.css" />
		<style>
			.ui-dropdownchecklist-selector > .ui-icon {margin-top:4px;}
			.ui-dropdownchecklist-text {padding:2px;margin:0;}
		</style-->
		<script type='text/javascript' src="${baseUrl}/js/general/dropDownPhenPage.js"></script>

		
		
		<script type="text/javascript">
			var phenotypeId = '${phenotype.id.accession}'; 
			var drupalBaseUrl = '${drupalBaseUrl}';
		</script>

	</jsp:attribute>


	<jsp:attribute name="bodyTag"><body  class="phenotype-node no-sidebars small-header"></jsp:attribute>
	
	<jsp:attribute name="addToFooter">
			<div class="region region-pinned">
            
        <div id="flyingnavi" class="block">
            
            <a href="#top"><i class="fa fa-chevron-up" title="scroll to top"></i></a>
            
            <ul>
                <li><a href="#top">Phenotype</a></li>
                <c:if test="${genePercentage.getDisplay()}">
                		<li><a href="#data-summary">Phenotype Association Stats</a></li>
                </c:if>
                <li><a href="#gene-variants">Gene Variants</a></li><!-- message comes up in this section so dont' check here -->
            </ul>
            
            <div class="clear"></div>
            
        </div>
        
    </div>
      
		<script type="text/javascript" src="${drupalBaseUrl}/mp-heatmap/heatmap/js/heatmap.js"></script>  
      <script>
      function dcc_get(url, handler) {
          var request = new XMLHttpRequest();
          request.open('GET', url, true);
          request.onreadystatechange = function(event) {
              if (request.readyState == 4) {
                  if (request.status == 200) {
                      handler(JSON.parse(request.responseText));
                  } else
                      dcc_reportError('Unable to retrieve data from ' + url)
              }
          };
          request.send(null);
      }
      dcc_get(drupalBaseUrl + '/mp-heatmap/rest/ontology_tree/mpterm?mpTerm=MP:0003857', function(mpTerm) {
          var title = document.getElementById("phenodcc-heatmap-title");
          title.innerHTML = "<p>MP Term Name: "+mpTerm.mpTermName+"</p>";
      });	 
      console.log("phenotypeId " + phenotypeId + ".")
      console.log("drupalBaseUrl " + drupalBaseUrl + ".")
	                    var heatmap = new dcc.PhenoHeatMap({
	                        'container': 'phenodcc-heatmap-3',
	                        'mode': 'exploration',
	                        'format': {'column': function(datum) {
	                                return datum.v;
	                            }
	                        },
	                        'mpterm': phenotypeId,
	                        'annotationthreshold': 0.05,
	                        'url': {
	                            /* the base URL of the heatmap javascript source */
	                            'jssrc': '${fn:replace(drupalBaseUrl, "https:", "")}/heatmap/js/',
	                            /* the base URL of the heatmap data source */
	                            'json': '${fn:replace(drupalBaseUrl, "https:", "")}/mp-heatmap/rest/',
	                            /* function that generates target URL for data
	                             * visualisation */
	                            'viz': function(r, c) {
	                                return 'http://www.mousephenotype.org/phenoview?gid=' + r + '&qeid=' + c;
	                            }
	                        }
	                    });
	                    
	        </script>   
        
	</jsp:attribute>
	<jsp:body>

	<div class="region region-content">
			<div class="block block-system">
				<div class="content">
					<div class="node node-gene">
			<h1 class="title" id="top">Phenotype: ${phenotype.name} </h1>	 
			
				<div class="section">
					<div class="inner">
						<c:if test="${not empty phenotype.description}">
							<p class="with-label"> <span class="label"> Definition</span> ${phenotype.description} </p>
						</c:if>
						<c:if test="${not empty synonyms}">
							<p class="with-label"> <span class="label">Synonyms</span>
								<c:forEach var="synonym" items="${synonyms}" varStatus="loop">
									${synonym.symbol}
									<c:if test="${!loop.last}">, &nbsp;</c:if>
								</c:forEach>
							</p>
						</c:if>
						<c:if test="${not empty procedures}">
							<div class="with-label"> <span class="label">Procedure</span>
								<ul>
								<c:set var="count" value="0" scope="page"/>
									<c:forEach var="procedure" items="${procedures}" varStatus="firstLoop">
 										<c:forEach var="pipeline" items="${procedure.pipelines}" varStatus="loop">
 											<c:set var="count" value="${count+1}" />
  											<li><a href="${drupalBaseUrl}/impress/impress/displaySOP/${procedure.stableKey}">${procedure.name} (${pipeline.name})</a></li>
	 											<c:if test="${count==3 && !(firstLoop.last && loop.last)}"><p ><a id='show_other_procedures'><i class="fa fa-caret-right"></i> more procedures</a></p> <div id="other_procedures"></c:if>
												<c:if test="${firstLoop.last && loop.last && fn:length(procedures) >3}"></div></c:if>
										</c:forEach>
									</c:forEach>
								</ul>
							</div>
						</c:if>
							<c:if test="${not empty anatomy}">
							<div class="with-label"> <span class="label">Anatomy</span>
								<ul>
									<c:forEach var="term" items="${anatomy}" varStatus="loop">
										<li><a href="http://informatics.jax.org/searches/AMA.cgi?id=${term.id.accession}">${term.name}</a></li>
									</c:forEach>
								</ul>
							</div>
						</c:if>
						<p class="with-label"><span class="label">MGI MP browser</span><a href="http://www.informatics.jax.org/searches/Phat.cgi?id=${phenotype.id.accession}">${phenotype.id.accession}</a></p>
					</div><!--  closing off inner here - but does this look correct in all situations- because of complicated looping rules above? jW -->
					</div>
				
				
				<c:if test="${genePercentage.getDisplay()}">
					<div class="section">
						<h2 class="title" id="data-summary">Phenotype associations stats <span class="documentation" ><a href='' id='phenotypeStatsPanel' class="fa fa-question-circle pull-right"></a></span> </h2>
						<div class="inner">					
							<!-- Phenotype Assoc. summary -->
							<div class="half">
								<p> <span class="muchbigger">${genePercentage.getTotalPercentage()}%</span> of tested genes with null mutations on a B6N genetic background have a phenotype association to ${phenotype.name} (source: EuroPhenome)
									(${genePercentage.getTotalGenesAssociated()}/${genePercentage.getTotalGenesTested()}) </p>
								<p class="padleft"><span class="bigger">${genePercentage.getFemalePercentage()}%</span> females (${genePercentage.getFemaleGenesAssociated()}/${genePercentage.getFemaleGenesTested()}) </p>
								<p class="padleft"><span class="bigger">${genePercentage.getMalePercentage()}%</span> males (${genePercentage.getMaleGenesAssociated()}/${genePercentage.getMaleGenesTested()}) 	</p>
							</div>					
							<!-- Overview Graphs -->
							<c:if test="${parametersAssociated.size() > 0}">
								<div id="chartsHalf" class="half">
								<c:if test="${parametersAssociated.size() > 1}">
									<p> Select a parameter <i class="fa fa-bar-chart-o" ></i>&nbsp; &nbsp;
										<select onchange="ajaxToBe('${phenotype.id.accession}', this.options[this.selectedIndex].value);">
											<c:forEach var="assocParam" items="${parametersAssociated.keySet().toArray()}" varStatus="loop">
												<option value="${assocParam}">${parametersAssociated.get(assocParam)}</option>
											</c:forEach>
										</select>
									</p>
								</c:if>
								<br/>
	
								<div id="chart-container">
									<div id="single-chart-div" class="oChart" parameter="${parametersAssociated.keySet().iterator().next()}" mp="${phenotype.id.accession}">
									</div>
									<div id="spinner-overview-charts"><i class="fa fa-refresh fa-spin"></i></div>
								</div>
								
								<div id='chartFilters'></div>
								
							</div>
						</c:if>
						<div class="clear"></div>
					</div>
				</div>
			</c:if>
						
			
				<div class="section">
				
			    <h2 class="title" id="gene-variants">Gene variants with ${phenotype.name} 	
			    <span class="documentation" ><a href='' id='relatedMpPanel' class="fa fa-question-circle pull-right"></a></span> 
			    </h2>
				   
					<div class="inner">	 
						<div id="phenotypesDiv">	
							<div class="container span12">
								<c:forEach var="filterParameters" items="${paramValues.fq}">
									${filterParameters}
								</c:forEach>
								<c:if test="${not empty phenotypes}">
									<form class="tablefiltering no-style" id="target" action="www.google.com">
											<c:forEach var="phenoFacet" items="${phenoFacets}"
													varStatus="phenoFacetStatus">
													<c:if
														test="${!isImpcTerm || !(phenoFacet.key eq 'mp_term_name')}">
													<select id="${phenoFacet.key}" class="impcdropdown"
															multiple="multiple" title="Filter on ${phenoFacet.key}">
														<c:forEach var="facet" items="${phenoFacet.value}">
															<option>${facet.key}</option>
														</c:forEach>
													</select> 
													</c:if>
											</c:forEach>
											
											<div class="clear"></div>
									</form>
									<jsp:include page="geneVariantsWithPhenotypeTable.jsp">
										<jsp:param name="isImpcTerm" value="${isImpcTerm}"/>
									</jsp:include>
									<div id="exportIconsDiv"></div>					
							</c:if>
							</div>
							<c:if test="${empty phenotypes}">
								<div class="alert alert-info">Phenotype associations to genes and alleles will be available once data has completed quality control.</div>
							</c:if>
						</div>
					</div>
				</div><!-- end of section -->
				
				
				<!--  HEATMAP section -->
				<div class="section">
					<h2 class="title" id="section">Gene phenotyping heatmap for ${phenotype.name} <i class="fa fa-question-circle pull-right"></i></h2>
					<div class="inner" id="heatmap-container">
						<div id="phenodcc-heatmap-3"> </div>							
	        </div>
				</div>
				
				
	<!-- example for images on phenotypes page: http://localhost:8080/phenotype-archive/phenotypes/MP:0000572 -->
			<c:if test="${not empty images && fn:length(images) !=0}">
				<div class="section">
						<h2 class="title" id="section">Images <i class="fa fa-question-circle pull-right"></i></h2>
						<div class="inner">			
											<%-- <a href="${baseUrl}/images?phenotype_id=${phenotype_id}">[show all  ${numberFound} images]</a> --%>
								<div class="accordion-group">
										<div class="accordion-heading">
												Phenotype Associated Images
										</div>
										<div  class="accordion-body">
											<ul>
												<c:forEach var="doc" items="${images}">
		                                                                                    <li class="span2">
													<t:imgdisplay img="${doc}" mediaBaseUrl="${mediaBaseUrl}"></t:imgdisplay>
		                                                                                    </li>
		    	  								</c:forEach>
											</ul>
											
											<div class="clear"></div>
												<c:if test="${entry.count>5}">
												<p class="textright"><a href="${baseUrl}/images?phenotype_id=${phenotype_id}">show all  ${numberFound} images</a></p>
												</c:if>
										</div>
									</div>
								<!--  end of accordion -->
								</div>
					</div>	<!--  end of images section -->
			</c:if>
			
			<%-- <c:if test="${not empty expressionFacets}">
			<div class="section">
				<h2 class="title" id="section-expression">Expression <i class="fa fa-question-circle pull-right"></i></h2>
					<div class="inner">			
					
							<!-- thumbnail scroller markup begin -->
			   						<c:forEach var="entry" items="${expressionFacets}" varStatus="status">
		  							<div class="accordion-group">
										<div class="accordion-heading">
												${entry.name}  (${entry.count})
										</div>
											<div  class="accordion-body">
									 			
												<ul>
												<c:forEach var="doc" items="${expFacetToDocs[entry.name]}">
		                                                                                    <li class="span2">
													<t:imgdisplay img="${doc}" mediaBaseUrl="${mediaBaseUrl}"></t:imgdisplay>
		                                                                                    </li>
		    	  								</c:forEach>
												</ul>
												<div class="clear"></div>
												<c:if test="${entry.count>5}">
												<p class="textright"><a href='${baseUrl}/images?gene_id=${acc}&q=expName:"Wholemount Expression"&fq=annotated_or_inferred_higherLevelMaTermName:"${entry.name}"'>[show all  ${entry.count} images]</a></p>
												</c:if>
											</div>
									</div>
								</c:forEach>	
					</div>
			</div>
			</c:if> --%>
		</div>
	</div>
</div>
</div>

</jsp:body>

</t:genericpage>


