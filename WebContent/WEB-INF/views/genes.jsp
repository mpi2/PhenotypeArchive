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
		<%-- <script src="${baseUrl}/js/general/toggle.js"></script> --%>
		<script src="${baseUrl}/js/imaging/genomicB.js"></script>
		<script src="${baseUrl}/js/general/enu.js"></script>
		<script src="${baseUrl}/js/general/dropdownfilters.js"></script>
		<!--[if !IE]><!-->
		<script type="text/javascript" src="${baseUrl}/js/genomic-browser/dalliance-compiled.js"></script>
		<!--<![endif]-->
		
		
		<script type="text/javascript">var gene_id = '${acc}';</script>
		<style>
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
						
						
								<%-- <div class="accordion-group">
                                    <div class="accordion-heading withColorWhenOpen"> Gene Browser</div>
                                    <!-- <div class="accordion-body"> -->
                                         <div id="genomebrowser" >
											<div class="floatright"><a href="http://www.biodalliance.org/" target="_blank" title="More information on using this browser"><i class="icon-question-sign"></i></a> <a title="This browser is clickable please experiment by clicking. Click on features to get more info, click on zoom bar etc. To reset click on 'lightning button'">This a an interactive genomic browser</a>
											</div>  
											Gene&nbsp;Location: Chr<span class="label" id='chr'>${gene.sequenceRegion.name}</span>:<span  class="label" id='geneStart'>${gene.start}</span>-<span  class="label" id='geneEnd'>${gene.end}</span> <br/> Gene Type: ${gene.subtype.name}
												<!-- <p><img class="fullimg" src="img/dummy/genebrowser.jpg" /></p> -->
											<div id="svgHolder"></div>
											
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
								<!-- </div> --><!--  end of accordion body -->
							</div> --%>
						</div>	
		
					
				</div><!-- section end -->
				 
				
		
		
		<!--  Phenotype Associations Panel -->
		<div class="section">
			<h2 class="title documentation" id="section-associations"> Phenotype associations for ${gene.symbol} 
				<!-- <a href='' id='mpPanel'><i class="fa fa-question-circle pull-right"></i></a> -->
				<a href='' id='mpPanel' class="fa fa-question-circle pull-right"></a> <!--  this works, but need js to drive tip position -->
			</h2>		
			
			<div class="inner">
				
				<c:if test="${phenotypeSummaryObjects.getBothPhenotypes().size() > 0 or phenotypeSummaryObjects.getFemalePhenotypes().size() > 0 or phenotypeSummaryObjects.getMalePhenotypes().size() > 0 }">
					<div class="abnormalities">
						<div class="allicons"></div>
						<c:forEach var="summaryObj" items="${phenotypeSummaryObjects.getBothPhenotypes()}">
							<a class="filterTrigger" id="phenIconsBox_${summaryObj.getGroup()}">
								<div class="sprite sprite_${summaryObj.getGroup().replaceAll(' |/', '_')}" data-hasqtip="27" title="${summaryObj.getGroup()}"></div>
							</a>
						</c:forEach>
						<c:forEach var="summaryObj" items="${phenotypeSummaryObjects.getFemalePhenotypes()}">
							<a class="filterTrigger" id="phenIconsBox_${summaryObj.getGroup()}">
								<div class="sprite sprite_${summaryObj.getGroup().replaceAll(' |/', '_')}" data-hasqtip="27" title="${summaryObj.getGroup()}"></div>
							</a>
						</c:forEach>
						<c:forEach var="summaryObj" items="${phenotypeSummaryObjects.getMalePhenotypes()}">
							<a class="filterTrigger" id="phenIconsBox_${summaryObj.getGroup()}">
								<div class="sprite sprite_${summaryObj.getGroup().replaceAll(' |/', '_')}" data-hasqtip="27" title="${summaryObj.getGroup()}"></div>
							</a>
						</c:forEach>
					</div>
		            
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
					<div class="alert alert-info">
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
		
			<!-- nicolas accordion for images here -->
<c:if test="${not empty solrFacets}">
        		<div class="section">
                      <h2 class="title" id="section-images">Phenotype Associated Images <i class="fa fa-question-circle pull-right" title="Brief info about this panel"></i></h2>
                       <!--  <div class="alert alert-info">Work in progress. Images may depict phenotypes not statistically associated with a mouse strain.</div>	 -->
                		<div class="inner">
                      				<c:forEach var="entry" items="${solrFacets}" varStatus="status">
                      					 <div class="accordion-group">
                        						<div class="accordion-heading">
                                        		${entry.name} (${entry.count})
                                    			</div>
                                				<div class="accordion-body">
                                       					<ul>
                                        				<c:forEach var="doc" items="${facetToDocs[entry.name]}">
																<li>
																		<t:imgdisplay img="${doc}" mediaBaseUrl="${mediaBaseUrl}"></t:imgdisplay>
		                    									</li>
														</c:forEach>
														</ul>
                                        				<div class="clear"></div>
                                        				<c:if test="${entry.count>5}">
                                        				<p class="textright"><a href="${baseUrl}/images?gene_id=${acc}&fq=expName:${entry.name}"><i class="fa fa-caret-right"></i> show all ${entry.count} images</a></p>
    													</c:if>
    											</div><!--  end of accordion body -->
    										</div>
                                    </c:forEach><!-- solrFacets end -->
                              
                           </div><!--  end of inner -->
         </div> <!-- end of section -->
</c:if>			
					
			<c:if test="${not empty expressionFacets}">
			<div class="section">
				<h2 class="title">Expression <i class="fa fa-question-circle pull-right"></i></h2>
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
			</c:if>
		
			<div class="section">
				<h2 class="title documentation">ES Cell and Mouse Alleles <a href='' id='allelePanel'><i class="fa fa-question-circle pull-right" ></i></a></h2>	
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
			</div> <!--end of node wrapper should be after all secions  -->
		</div>
		</div>
		</div>
		
  </jsp:body>
</t:genericpage>