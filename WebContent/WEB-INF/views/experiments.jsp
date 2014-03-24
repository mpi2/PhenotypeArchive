<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:genericpage>
	<jsp:attribute name="title">Gene details for ${gene.name}</jsp:attribute>
	<jsp:attribute name="breadcrumb">&nbsp;&raquo; <a href="${baseUrl}/search#sort=marker_symbol asc&q=*:*&core=gene">Genes</a> &raquo; ${gene.symbol}</jsp:attribute>
	<jsp:attribute name="bodyTag"><body  class="gene-node no-sidebars small-header"></jsp:attribute>
	<jsp:attribute name="addToFooter">
	<!--  start of floating menu for genes page -->
	<div class="region region-pinned">
            
        <div id="flyingnavi" class="block">
            
            <a href="#top"><i class="fa fa-chevron-up" title="scroll to top"></i></a>
            
            <ul>
                <li><a href="#top">Gene</a></li>
                <li><a href="#section-associations">Phenotype Associations</a></li><!--  always a section for this even if says no phenotypes found - do not putting in check here -->
            </ul>
            
            <div class="clear"></div>
            
        </div>
        
    </div>
	<!--  end of floating menu for genes page -->

	</jsp:attribute>
	

	<jsp:attribute name="header">
	
		<!-- CSS Local Imports -->
		<!-- link rel="stylesheet" type="text/css" href="${baseUrl}/css/ui.dropdownchecklist.themeroller.css"/-->
		
		<!-- JavaScript Local Imports -->
		<script src="${baseUrl}/js/general/dropdownfilters.js"></script>
		<script type="text/javascript" src="${baseUrl}/js/general/allele.js"></script>
		
		
		<script type="text/javascript">var gene_id = '${acc}';</script>
        
  </jsp:attribute>

	<jsp:body>
		<div class="region region-content">
			<div class="block">
				<div class="content">
					<div class="node node-gene">
						<h1 class="title" id="top">Gene: ${gene.symbol}  &nbsp;&nbsp; </h1>

				 				
		
		
		<!--  Phenotype Associations Panel -->
		<div class="section">

			<h2 class="title " id="section-associations"> Phenotype associations for ${gene.symbol} 
					<!-- <span class="documentation" > <a href='' id='mpPanel'><i class="fa fa-question-circle pull-right"></i></a></span>-->
					<span class="documentation" ><a href='' id='mpPanel' class="fa fa-question-circle pull-right"></a></span> <!--  this works, but need js to drive tip position -->
			</h2>		

			<div class="inner">
				<c:choose>
				<c:when test="${phenotypeSummaryObjects.getBothPhenotypes().size() > 0 or phenotypeSummaryObjects.getFemalePhenotypes().size() > 0 or phenotypeSummaryObjects.getMalePhenotypes().size() > 0 }">
					<div class="abnormalities">
						<div class="allicons"></div>
						<c:forEach var="summaryObj" items="${phenotypeSummaryObjects.getBothPhenotypes()}">
							<c:if test="${summaryObj.getGroup() != 'mammalian phenotype' }">
								<a class="filterTrigger" id="phenIconsBox_${summaryObj.getGroup()}">
									<div class="sprite sprite_${summaryObj.getGroup().replaceAll(' |/', '_')}" data-hasqtip="27" title="${summaryObj.getGroup()}"></div>
								</a>
							</c:if>
						</c:forEach>
						<c:forEach var="summaryObj" items="${phenotypeSummaryObjects.getFemalePhenotypes()}">
							<c:if test="${summaryObj.getGroup() != 'mammalian phenotype' }">
								<a class="filterTrigger" id="phenIconsBox_${summaryObj.getGroup()}">
									<div class="sprite sprite_${summaryObj.getGroup().replaceAll(' |/', '_')}" data-hasqtip="27" title="${summaryObj.getGroup()}"></div>
								</a>
							</c:if>
						</c:forEach>
						<c:forEach var="summaryObj" items="${phenotypeSummaryObjects.getMalePhenotypes()}">
							<c:if test="${summaryObj.getGroup() != 'mammalian phenotype' }">
								<a class="filterTrigger" id="phenIconsBox_${summaryObj.getGroup()}">
									<div class="sprite sprite_${summaryObj.getGroup().replaceAll(' |/', '_')}" data-hasqtip="27" title="${summaryObj.getGroup()}"></div>
								</a>
							</c:if>
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
					
				</c:when>
				<c:otherwise>
				<div class="alert alert-info">There is are currently no phenotype associations for the gene ${gene.symbol} </div>
				</c:otherwise>
				</c:choose>
			</div>
		</div> <!-- phenotype association and graphs -->
 
      </div> <!--end of node wrapper should be after all secions  -->
    </div>
    </div>
    </div>

    </jsp:body>
  
</t:genericpage>
