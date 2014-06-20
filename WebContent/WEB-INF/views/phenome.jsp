<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:genericpage>
	<jsp:attribute name="title">Phenome Overview for all ${param.phenotyping_center} Strains (${param.pipeline_stable_id})</jsp:attribute>
	<jsp:attribute name="breadcrumb">&nbsp;&raquo; Phenome &raquo; ${param.phenotyping_center}</jsp:attribute>
	<jsp:attribute name="bodyTag">
		<body class="gene-node no-sidebars small-header">
	</jsp:attribute>
	<jsp:attribute name="addToFooter">
	<!--  start of floating menu for genes page -->
	<div class="region region-pinned">
            
        <div id="flyingnavi" class="block">
            
            <a href="#top"><i class="fa fa-chevron-up"
					title="scroll to top"></i></a>
            
            <ul>
                <li><a href="#top">Phenome Overview</a></li>
                <li><a href="#section-associations">Phenotype Associations</a></li>
					<!--  always a section for this even if says no phenotypes found - do not putting in check here -->
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
		<script src="${baseUrl}/js/general/dropdownfilters.js?v=${version}"></script>
		
		<!-- Assign this as a variable for other components -->
		<script type="text/javascript">
			var base_url = '${baseUrl}';
		</script>
        
  </jsp:attribute>

	<jsp:body>
		<div class="region region-content">
			<div class="block">
				<div class="content">
					<div class="node node-gene">
						<h1 class="title" id="top">Significant MP calls for ${param.phenotyping_center} Strains (${param.pipeline_stable_id})&nbsp;&nbsp; </h1>

		<!--  Phenotype Associations Panel -->
		<div class="section">
				
			<h2 class="title" id="section-associations">&nbsp;
					<span class="documentation"><a href='' id='phenomePanel' class="fa fa-question-circle pull-right"></a></span>
			</h2>		
			
			<div class="inner">
				
				
			<!-- Associations table -->
			<c:if test="${chart != null}">
				<!-- phenome chart here -->
  				<div id="chart${param.phenotyping_center}">
  				<a class="various" id="iframe" data-fancybox-type="iframe"></a></div>
				<script type="text/javascript">
					${chart}
				</script>	
			</c:if>	
			
	<c:set var="count" value="0" scope="page" />
	<c:forEach var="dataMap" items="${phenotypeCalls}" varStatus="status">
			<c:set var="count" value="${count + 1}" scope="page" />
	</c:forEach>
	<p class="resultCount">
	Total number of results: ${count}
	</p>

	<script>
		var resTemp = document.getElementsByClassName("resultCount");
		if (resTemp.length > 1)
			resTemp[0].remove();
	</script>
	
				<table id="phenotypeCalls">
					<thead>
						<tr>
							<th class="headerSort">Gene / Allele</th>
							<th class="headerSort">Procedure</th>
							<th class="headerSort">Parameter</th>
							<th class="headerSort">Zygosity</th>
							<th class="headerSort">Phenotype</th>
							<th class="headerSort">P-value</th>
							<th class="headerSort">Graph</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="phenotypeCall" items="${phenotypeCalls}"
											varStatus="status">
								<tr>
								<td><a href="${baseUrl}/genes/${phenotypeCall.gene.id.accession}">${phenotypeCall.gene.symbol}</a><br/> 
									<span class="smallerAlleleFont"><t:formatAllele>${phenotypeCall.allele.symbol}</t:formatAllele></span> 
								</td>
								<td>${phenotypeCall.procedure.name}</td>
								<td>${phenotypeCall.parameter.name}</td>
								<td>${phenotypeCall.zygosity}</td>
								<td>${phenotypeCall.phenotypeTerm.name}</td>
						<c:set var="paletteIndex" value="${phenotypeCall.colorIndex}" />
						<c:set var="Rcolor" value="${palette[0][paletteIndex]}" />
						<c:set var="Gcolor" value="${palette[1][paletteIndex]}" />
						<c:set var="Bcolor" value="${palette[2][paletteIndex]}" />
						<td style="background-color:rgb(${Rcolor},${Gcolor},${Bcolor})">
						${phenotypeCall.pValue}
						</td>
						<td style="text-align: center">
						<a
													href='${baseUrl}/charts?accession=${phenotypeCall.gene.id.accession}&allele_accession=${phenotypeCall.allele.id.accession}&parameter_stable_id=${phenotypeCall.parameter.stableId}&zygosity=${phenotypeCall.zygosity}&phenotyping_center=${param.phenotyping_center}'>
						<i class="fa fa-bar-chart-o" alt="Graphs"> </i>
												</a>
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

<!--  <script type="text/javascript">
	$(document).ready(function() {
		  var oTable = $('#strainPhenome').dataTable();

		  // Sort immediately with p-value column starting with the lowest one
		  oTable.fnSort( [ [4,'asc'] ] );
		} );
	
	</script>-->
	
    </jsp:body>

</t:genericpage>
