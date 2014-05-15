<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:genericpage>
	<jsp:attribute name="title">Experiment details for ${allele.symbol}</jsp:attribute>
	<jsp:attribute name="breadcrumb">&nbsp;&raquo; Alleles &raquo; <t:formatAllele>${allele.symbol}</t:formatAllele></jsp:attribute>
	<jsp:attribute name="bodyTag"><body  class="gene-node no-sidebars small-header"></jsp:attribute>
	<jsp:attribute name="addToFooter">
	<!--  start of floating menu for genes page -->
	<div class="region region-pinned">
            
        <div id="flyingnavi" class="block">
            
            <a href="#top"><i class="fa fa-chevron-up" title="scroll to top"></i></a>
            
            <ul>
                <li><a href="#top">Allele</a></li>
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
		
		<!-- Why it is there? I don't know -->
		<script type="text/javascript">var gene_id = '${allele.gene.id.accession}';</script>
        
  </jsp:attribute>

	<jsp:body>
		<div class="region region-content">
			<div class="block">
				<div class="content">
					<div class="node node-gene">
						<h1 class="title" id="top"><t:formatAllele>${allele.symbol}</t:formatAllele> - ${phenotyping_center} - ${pipeline.name}&nbsp;&nbsp; </h1>

		<!--  Phenotype Associations Panel -->
		<div class="section">

			<h2 class="title " id="section-associations">&nbsp;
					<!-- <span class="documentation" > <a href='' id='mpPanel'><i class="fa fa-question-circle pull-right"></i></a></span>-->
					<span class="documentation" ><a href='' id='expPanel' class="fa fa-question-circle pull-right"></a></span> <!--  this works, but need js to drive tip position -->
			</h2>		
			
			<div class="inner">

			<!-- Associations table -->
			<c:if test="${chart != null}">
	
	<!-- phenome chart here -->
  		<div id="chart${allele.id.accession}"></div>
		<script type="text/javascript">${chart}</script>	
	</c:if>
				
	<c:set var="count" value="0" scope="page" />
	<c:forEach var="dataMap" items="${mapList}" varStatus="status">
			<c:set var="count" value="${count + 1}" scope="page"/>
	</c:forEach>
	<p class="resultCount">
	Total number of results: ${count}
	</p>

	<script>
	 var resTemp = document.getElementsByClassName("resultCount");
	 if (resTemp.length > 1)
		 resTemp[0].remove();
	</script>
	

				<table id="strainPhenome">
					<thead>
						<tr>
							<th class="headerSort">Procedure</th>
							<th class="headerSort">Parameter</th>
							<th class="headerSort">Data type</th>
							<th class="headerSort">Zygosity</th>
							<th class="headerSort">P Value</th>
							<th class="headerSort">Status</th>
							<th class="headerSort">Graph</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="dataMap" items="${mapList}" varStatus="status">
						<tr>
						<td>${dataMap["procedure_name"]}</td>
						<td>${dataMap["parameter_name"]}</td>
						<td>${dataMap["observation_type"]}</td>
						<td>${dataMap["zygosity"]}</td>
						<c:set var="stableId" value="${dataMap['parameter_stable_id']}"/>
						<c:choose>
						<c:when test="${ ! empty pvalues[stableId] && pvalues[stableId].isSuccessful }">
						<c:set var="paletteIndex" value="${pvalues[stableId].colorIndex}"/>
						<c:set var="Rcolor" value="${palette[0][paletteIndex]}"/>
						<c:set var="Gcolor" value="${palette[1][paletteIndex]}"/>
						<c:set var="Bcolor" value="${palette[2][paletteIndex]}"/>
						<td style="background-color:rgb(${Rcolor},${Gcolor},${Bcolor})">
						${pvalues[stableId].pValue}
						</td>
						</c:when>
						<c:otherwise><td></td></c:otherwise>
						</c:choose>
						<td>${pvalues[stableId].status}</td>
						<td style="text-align:center">
						<a href='${baseUrl}/charts?accession=${allele.gene.id.accession}&allele_accession=${allele.id.accession}&parameter_stable_id=${dataMap["parameter_stable_id"]}&zygosity=${dataMap["zygosity"]}&phenotyping_center=${phenotyping_center}'>
						<i class="fa fa-bar-chart-o" alt="Graphs" > </i></a>
						</td>
						</tr>
						</c:forEach>
					</tbody>
				</table>				
			</div>
		</div> <!-- parameter list -->
 
      </div> <!--end of node wrapper should be after all secions  -->
    </div>
    </div>
    </div>

    </jsp:body>
  
</t:genericpage>
