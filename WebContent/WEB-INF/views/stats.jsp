<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:genericpage>
	<jsp:attribute name="title">Stats for ${gene.name}</jsp:attribute>

	<jsp:attribute name="breadcrumb">&nbsp;&raquo; <a
			href="${baseUrl}/search#sort=marker_symbol asc&q=*:*&core=gene">Genes</a> <c:if
			test="${not empty gene.subtype.name }">&raquo; <a
				href='${baseUrl}/search#fq=marker_type:"${gene.subtype.name}"&q=*:*&core=gene'>${gene.subtype.name}</a>
		</c:if> &raquo; ${gene.symbol}</jsp:attribute>

	<jsp:attribute name="header">

	<script type="text/javascript">
		var gene_id = '${acc}';
	</script>

	<!--    extra header stuff goes here such as extra page specific javascript -->
	<!-- highcharts -->
<script type='text/javascript' src='${baseUrl}/js/charts/highcharts.js'></script>
 <script type='text/javascript' src='${baseUrl}/js/charts/highcharts-more.js'></script> 
<script src="${baseUrl}/js/charts/exporting.js"></script>
	

	

<style>
</style>

    </jsp:attribute>

	<jsp:body>
        
		<div class='topic'>Gene: ${gene.symbol}</div>
		<c:if test="${statsError}">
					<div class="alert alert-error">
							  				<strong>Error:</strong> A stats error occured - results on this page maybe incorrect.
					</div>
		</c:if>
	

<!-- time series charts here-->

<c:forEach var="timeChart" items="${timeSeriesCharts}" varStatus="timeLoop">
 <%-- ${loop.count  % 2} --%>
	<c:if test = "${timeLoop.count  % 2!=0}">
		<div class="row-fluid dataset">  
		 <div class="row-fluid ">
		 		<div class="container span6"><h4><!--  style="background-color:lightgrey;"  -->Allele -  <t:formatAllele>${timeSeriesMutantBiologicalModels[timeLoop.index].alleles[0].symbol}</t:formatAllele> <span class="graphGenBackground">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;background -  ${timeSeriesMutantBiologicalModels[timeLoop.index].geneticBackground}</span></h4>
		 		</div>
		 		 <c:if test="${fn:length(timeSeriesCharts) > (timeLoop.index+1)}">
		 		 <div class="container span6"><h4><!--  style="background-color:lightgrey;"  -->Allele -  <t:formatAllele>${timeSeriesMutantBiologicalModels[timeLoop.index+1].alleles[0].symbol}</t:formatAllele> <span class="graphGenBackground">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;background -  ${timeSeriesMutantBiologicalModels[timeLoop.index+1].geneticBackground}</span></h4>
		 		</div>
		 		</c:if>
 		</div>
 		<div class="row-fluid">
 	</c:if>
  				 <div class="container span6">
								<div id="timeChart${timeLoop.count}"
									style="min-width: 400px; height: 450px; margin: 0 auto">
								</div>
								<script type="text/javascript">
								${timeChart}
								</script>
					</div>
<c:if test = "${(timeLoop.count % 2==0) || timeLoop.count eq fn:length(timeSeriesCharts)}">
		 </div>
		 </div>
</c:if>
</c:forEach>



<c:if test = "${not empty categoricalBarCharts}"><div class="row-fluid dataset">
</c:if>
<c:forEach var="categoricalBarChart" items="${categoricalBarCharts}" varStatus="loop">
 <%-- ${loop.count  % 2} --%>
<c:if test = "${loop.count  % 2!=0}">
<!-- <div class="row-fluid dataset">   -->
		 <div class="row-fluid">
		 		<div class="container span6">
		 				<h4>Allele -  <t:formatAllele>${categoricalMutantBModel[loop.index].alleles[0].symbol}</t:formatAllele> <span class="graphGenBackground"> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;background -  ${categoricalMutantBModel[loop.index].geneticBackground}</span></h4>
		 		</div>
		 		<c:if test="${fn:length(categoricalBarCharts) > (loop.index+1)}">
		 		<div class="container span6">
		 				<h4>Allele -  <t:formatAllele>${categoricalMutantBModel[loop.index+1].alleles[0].symbol}</t:formatAllele> <span class="graphGenBackground"> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;background -  ${categoricalMutantBModel[loop.index+1].geneticBackground}</span></h4>
		 		</div>
		 		</c:if> 
 		</div>
 		<div class="row-fluid">
 </c:if>
  				 <div class="container span6">
								<div id="categoricalBarChart${loop.count}"
									style="min-width: 400px; height: 400px; margin: 0 auto">
								</div>
								
									<c:set var="table" scope="page" value="${tables[loop.index]}"/>
									
										<table id="table${loop.count}" class="table table-bordered  table-striped table-condensed">
										<thead><tr>
										<c:forEach var="colHeader" items="${table.columnHeaders}" varStatus="xCatCount">
										<th>${colHeader}</th>
										</c:forEach>
										<%-- <th>${tables[loop.count-1].xAxisCategories[1]}</th><th>${tables[loop.count-1].xAxisCategories[2]}</th> --%>
										</tr></thead>
										<tbody>
										
										
										
												<c:forEach var="rowHeader" items="${table.rowHeaders}" varStatus="rowCount">
												<tr>
												<td>${rowHeader}</td>
														<c:forEach var="cellRow" items="${table.cellData[rowCount.index]}" varStatus="cellCount">
																<c:forEach var="cell" items="${cellRow}" varStatus="columnCount">
																		<td>${cell}</td>
																</c:forEach>
														</c:forEach>
												</tr>
												</c:forEach>
			
										</tbody>
										</table>
										
   								<script type="text/javascript">
   
   								${categoricalBarChart}
   
   								
								</script>
								
				</div>
<c:if test = "${(loop.count % 2==0) || (loop.count eq fn:length(categoricalBarCharts))} ">
</div>
<!-- </div> -->
</c:if>
 </c:forEach>
<c:if test = "${not empty categoricalBarCharts}">
</div>
</c:if>
			
				
			
	
		
		<!--/end of categoriacl charts-->
		
 
<c:forEach var="continuousChart" items="${continuousCharts}" varStatus="uniDimensionalLoop">
 <%-- ${loop.count  % 2} --%>
<c:if test = "${uniDimensionalLoop.count  % 2!=0}">
 <div class="row-fluid dataset"> 
		 <div class="row-fluid">
		 		<div class="container span6"><h4>Allele -  <t:formatAllele>${unidimensionalMutantBiologicalModels[uniDimensionalLoop.index].alleles[0].symbol}</t:formatAllele> <span class="graphGenBackground">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;background -  ${unidimensionalMutantBiologicalModels[uniDimensionalLoop.index].geneticBackground}</span></h4>
				 </div>
				 <c:if test="${fn:length(continuousCharts) > (uniDimensionalLoop.index+1)}">
				 <div class="container span6"><h4>Allele -  <t:formatAllele>${unidimensionalMutantBiologicalModels[uniDimensionalLoop.index+1].alleles[0].symbol}</t:formatAllele> <span class="graphGenBackground">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;background -  ${unidimensionalMutantBiologicalModels[uniDimensionalLoop.index+1].geneticBackground}</span></h4>
				 </div>
				 </c:if>
 		</div>
 		<div class="row-fluid">
 </c:if>
		
 
  		<div class="container span6">
				<div id="chart${uniDimensionalLoop.count}"
									style="min-width: 400px; height: 400px; margin: 0 auto">
				</div>
		<c:set var="table" scope="page" value="${continuousTables[uniDimensionalLoop.index]}"/>
		<table id="continuousTable${uniDimensionalLoop.count}" class="table table-bordered  table-striped table-condensed">
		<thead><tr>
		<c:forEach var="colHeader" items="${table.columnHeaders}" varStatus="xCatCount">
		<th>${colHeader}</th>
		</c:forEach>
		<%-- <th>${tables[loop.count-1].xAxisCategories[1]}</th><th>${tables[loop.count-1].xAxisCategories[2]}</th> --%>
		</tr></thead>
		<tbody>
										
										
										
												<c:forEach var="rowHeader" items="${table.rowHeaders}" varStatus="rowCount">
												<tr>
												<td>${rowHeader}</td>
														<c:forEach var="cellRow" items="${table.cellData[rowCount.index]}" varStatus="cellCount">
																<c:forEach var="cell" items="${cellRow}" varStatus="columnCount">
																		<td>${cell}</td>
																</c:forEach>
														</c:forEach>
												</tr>
												</c:forEach>
									
										
										<%-- <td>${tables[loop.count-1].seriesDataForCategoricalType[0][0]}</td><td>${tables[loop.count-1].seriesDataForCategoricalType[0][1]}</td><td>${tables[loop.count-1].seriesDataForCategoricalType[0][2]}</td>
										 </tr>--%>
										<%-- <tr><td>${tables[loop.count-1].categories[1]}</td><td>${tables[loop.count-1].seriesDataForCategoricalType[1][0]}</td><td>${tables[loop.count-1].seriesDataForCategoricalType[1][1]}</td><td>${tables[loop.count-1].seriesDataForCategoricalType[1][2]}</td>
										 </tr>--%>
										</tbody>
										</table>
								
								
			
   								<script type="text/javascript">
   
   								$(function () {
   								    $('#chart${uniDimensionalLoop.count}').highcharts(${continuousChart});
								</script>
								
		</div><!-- end of span6  individual chart holder -->

 

			
				
			
	<%-- length= ${fn:length(continuousCharts ) } loopcount= ${uniDimensionalLoop.count} --%>
	<c:if test = "${(uniDimensionalLoop.count  % 2==0) || (fn:length(continuousCharts )==uniDimensionalLoop.count )}">
	<!-- ending tables now -->
</div>
</div>
</c:if>
<%-- <c:if test = "${uniDimensionalLoop.count  == fn:length(continuousCharts)}">
</div>
</div>
</c:if> --%>

	
		</c:forEach>
		<!--/row-->

<!-- Continuous barchart here -->
<c:forEach var="barChart" items="${continuousBarCharts}" varStatus="barloop">
<c:if test = "${barloop.count  % 2!=0}">
		<div class="row-fluid dataset">  
 </c:if>
 			<div class="container span6">
								<div id="barChart${barloop.count }"
									style="min-width: 400px; height: 400px; margin: 0 auto">
								</div>
								
								
								
			
   								<script type="text/javascript">
   
   								$(function() {$('#barChart${barloop.count }').highcharts(${barChart});
   									});
   								
								</script>
			</div>
	<c:if test = "${ (barloop.count % 2==0)|| (fn:length(continuousBarCharts )==barloop.count )}">
	</div>
	</c:if>
</c:forEach>



    </jsp:body>
</t:genericpage>
