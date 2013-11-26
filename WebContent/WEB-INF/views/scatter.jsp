<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<!-- most of this page is the same as the unidimensionalStatsFrag.jsp so we should refactor this so they are the same page -->
<t:genericpage>
<jsp:attribute name="title"> <c:forEach var="parameter" items="${parameters}" varStatus="loop">
  ${parameter.name}
    <c:if test="${ not loop.last}">,</c:if>
	</c:forEach>Statistics for ${gene.symbol} </jsp:attribute>

	<jsp:attribute name="breadcrumb">&nbsp;&raquo; Statistics &raquo; <a href='${baseUrl}/genes/${gene.id.accession}'>${gene.symbol}</a> &raquo; Scatter graph</jsp:attribute>

	<jsp:attribute name="header">

	<script type="text/javascript">
		var gene_id = '${acc}';
	</script>

	<!--    extra header stuff goes here such as extra page specific javascript -->
	<!-- highcharts -->
<script type='text/javascript' src='${baseUrl}/js/charts/highcharts.js'></script>
 <script type='text/javascript'
			src='${baseUrl}/js/charts/highcharts-more.js'></script> 
<script src="${baseUrl}/js/charts/exporting.js"></script>
	

<style>
</style>

    </jsp:attribute>

	<jsp:attribute name="footer">

	</jsp:attribute>

	<jsp:body>
        
	<div class='documentation'>
   		<a href='' class='generalPanel'><img
				src="${baseUrl}/img/info_20x20.png" /></a>
	</div>	
    
	<div class='topic'>Gene: ${gene.symbol} Parameters: 
	 <c:forEach var="parameter" items="${parameters}" varStatus="loop">
  ${parameter.name}
    <c:if test="${ not loop.last}">, </c:if>
	</c:forEach>
	</div>
	<div id="exportIconsDiv"></div>
	
	<c:if test="${statsError}">
		<div class="alert alert-error">
			<strong>Error:</strong> An issue occurred processing the statistics for this page - results on this page maybe incorrect.
		</div>
	</c:if>
	


 <c:forEach var="unidimensionalDataSet"
			items="${allUnidimensionalDataSets}"
			varStatus="unidimensionalDataSetLoop">
			
	<c:if test="${fn:length(unidimensionalDataSet.statsObjects)>0}">
 		<div class="row-fluid dataset"> 

		 <div class="row-fluid">
		 	<div class="container span6">
							<h4>Allele -  <t:formatAllele> ${unidimensionalDataSet.statsObjects[1].allele }</t:formatAllele> <span
									class="graphGenBackground">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;background -  ${unidimensionalDataSet.statsObjects[1].geneticBackground }</span>
							</h4>
							<h5>${unidimensionalDataSet.organisation }</h5>
				 	</div>
				 
 		</div>
 		<div class="row-fluid">
<c:forEach var="unidimensionalChartsAndTable"
							items="${unidimensionalDataSet.sexChartAndTables}"
							varStatus="uniDimensionalLoop">
 <%-- ${loop.count  % 2} --%>

  		<div class="container span6">
				<div
									id="chart${unidimensionalDataSetLoop.count}_${uniDimensionalLoop.count}"
									style="min-width: 400px; height: 400px; margin: 0 auto">
				</div>
		
		
								
								
			
   								<script type="text/javascript">
   
   								$(function () {
   								    $('#chart${unidimensionalDataSetLoop.count}_${uniDimensionalLoop.count}').highcharts(${unidimensionalChartsAndTable.chart});
								</script>
								${parameter }
							<a href="../${acc}?${pageContext.request.queryString}">Box Plot / Time Series  Graphs</a>		
		</div>
							<!-- end of span6  individual chart holder -->
		</c:forEach>
		</div>
					<!-- end of row-fluid for graphs -->
		
				
		<table id="continuousTable${uniDimensionalLoop.count}"
						class="table table-bordered  table-striped table-condensed">
		<thead>
							<tr>
		<th>Line</th>
		<th>Zygosity</th>
			<%-- <th>Sex</th> --%>
			<th>Mean</th>
			<th>SD</th>
			<th>Sex</th>
			<th>Count</th>
			<th>Effect Size</th>
			<th>pValue</th>
		<%-- <th>${tables[loop.count-1].xAxisCategories[1]}</th><th>${tables[loop.count-1].xAxisCategories[2]}</th> --%>
		</tr>
						</thead>
		<tbody>
										
										
										
											<c:forEach var="statsObject"
								items="${unidimensionalDataSet.statsObjects}">
												<tr>
												<td>${statsObject.line}</td>
												<c:choose>
												<c:when test="${statsObject.line =='Control' }">
												<td>NA</td>
												</c:when>
												<c:when test="${statsObject.line !='Control' }">
												<td>${statsObject.zygosity}</td>
												</c:when>
												</c:choose>
												<%-- <td>${statsObject.sexType}</td> --%>
												<td>${statsObject.mean}</td>
												<td>${statsObject.sd}</td>
												<td><c:choose><c:when test="${statsObject.sexType eq 'female'}"><img style="cursor:help;color:#D6247D;" rel="tooltip" data-placement="top" title="Female" alt="Female" src="${baseUrl}/img/icon-female.png" /></c:when><c:otherwise><img style="cursor:help;color:#247DD6;" rel="tooltip" data-placement="top" title="Male" alt="Male" src="${baseUrl}/img/icon-male.png" /></c:otherwise></c:choose></td>
												<c:if test="${statsObject.sexType eq 'female'}">
												<td>${statsObject.sampleSizeFemale}</td>
												</c:if>
												<c:if test="${statsObject.sexType eq 'male'}">
												<td>${statsObject.sampleSizeMale}</td>
												</c:if>
												<td>${statsObject.result.effectSize}</td>
												<td>${statsObject.result.pValue}</td>
												</tr>
												</c:forEach>
												
									
										
										<%-- <td>${tables[loop.count-1].seriesDataForCategoricalType[0][0]}</td><td>${tables[loop.count-1].seriesDataForCategoricalType[0][1]}</td><td>${tables[loop.count-1].seriesDataForCategoricalType[0][2]}</td>
										 </tr>--%>
										<%-- <tr><td>${tables[loop.count-1].categories[1]}</td><td>${tables[loop.count-1].seriesDataForCategoricalType[1][0]}</td><td>${tables[loop.count-1].seriesDataForCategoricalType[1][1]}</td><td>${tables[loop.count-1].seriesDataForCategoricalType[1][2]}</td>
										 </tr>--%>
										</tbody>
										</table>
				
				
				<c:if
						test="${fn:length(unidimensionalDataSet.allUnidimensionalResults)>0}">
				<div class="row-fluid">
						<div class="container span12">
						<table
									class="ttable table-bordered  table-striped table-condensed">
						<%-- ${fn:length(unidimensionalData.allUnidimensionalResults)} --%>
						
 							<c:forEach var="data"
										items="${unidimensionalDataSet.allUnidimensionalResults}">
 							<%-- <td>${data.significanceClassification}</td> --%>
 									<c:choose>
          									<c:when
												test="${data.significanceClassification == 'both_equally' || data.significanceClassification == 'none'  || data.significanceClassification == 'cannot_classify' }">
          												<tr>
													<th>Global Test</th>
													<th>Significance/Classification</th>
													<th>Effect</th>
												</tr>
          												<tr>
													<td>${data.nullTestSignificance}</td>
													<td>${data.significanceClassification.text}</td>
													<td>${data.genotypeParameterEstimate}</td>
												</tr>
											</c:when>
         									<c:when
												test="${data.significanceClassification == 'female_only' || data.significanceClassification == 'male_only'  || data.significanceClassification == 'female_greater' || data.significanceClassification == 'male_greater' || data.significanceClassification == 'different_directions'}">
       													 <tr>
													<th>Global Test</th>
													<th>Significance/Classification</th>
													<th>Gender</th>
													<th>Effect</th>
												</tr>
       													 <tr>
       													 <td rowspan="2">${data.nullTestSignificance}</td>
       													 <td rowspan="2">${data.significanceClassification.text}</td>
       													 <td>Female</td>
													<td>${data.genderFemaleKoEstimate}</td>
       													 
												<tr>
													<td> Male</td>
													<td> ${data.genderMaleKoEstimate}</td>
												</tr>
											</c:when>
									</c:choose>
									</c:forEach>
 	 							</table>
 	 							
 	 		<%-- <table class="ttable table-bordered  table-striped table-condensed">
						<tr>
						<th>colonyId</th>
						<th>experimentalZygosity</th>
						<th>mixedModel</th>
						<th>dependantVariable</th>
						<th>batchSignificance</th>
						<th>varianceSignificance</th>
						<th>nullTestSignificance</th>
						<th>genotypeParameterEstimate</th>
						<th>genotypeStandardErrorEstimate</th>
						<th>genotypeEffectPValue</th>
						<th>genderParameterEstimate</th>
						<th>genderStandardErrorEstimate</th>
						<th>genderEffectPValue</th>
						<th>weightParameterEstimate</th>
						<th>weightStandardErrorEstimate</th>
						<th>weightEffectPValue</th>
						<th>gp1Genotype</th>
						<th>gp1ResidualsNormalityTest</th>
						<th>gp2Genotype</th>
						<th>gp2ResidualsNormalityTest</th>
						<th>blupsTest</th>
						<th>rotatedResidualsNormalityTest</th>
						<th>interceptEstimate</th>
						<th>interceptEstimateStandardError</th>
						<th>interactionSignificance</th>
						<th>interactionEffectPValue</th>
						<th>genderFemaleKoEstimate</th>
						<th>genderFemaleKoStandardErrorEstimate</th>
						<th>genderFemaleKoPValue</th>
						<th>genderMaleKoEstimate</th>
						<th>genderMaleKoStandardErrorEstimate</th>
						<th>genderMaleKoPValue</th>
						</tr>
 						<c:forEach var="data" items="${unidimensionalData.allUnidimensionalResults}">
 							<tr>
 							<td>${data.colonyId }</td>
 							<td>${data.experimentalZygosity}</td>
 							<td>${data.mixedModel}</td>
 							<td>${data.dependantVariable}</td>
 							<td>${data.batchSignificance }</td>
 							<td>${data.varianceSignificance }</td>
 							<td>${data.nullTestSignificance }</td>
 							<td>${data.genotypeParameterEstimate }</td>
 							<td>${data.genotypeStandardErrorEstimate }</td>
 							<td>${data.genotypeEffectPValue}</td>
 							<td>${data.genderParameterEstimate }</td>
 							<td>${data.genderStandardErrorEstimate }</td>
 							<td>${data.genderEffectPValue}</td>
 							<td>${data.weightParameterEstimate }</td>
 							<td>${data.weightStandardErrorEstimate }</td>
 							<td>${data.weightEffectPValue }</td>
 							<td>${data.gp1Genotype }</td>
 							<td>${data.gp1ResidualsNormalityTest }</td>
 							<td>${data.gp2Genotype }</td>
 							<td>${data.gp2ResidualsNormalityTest }</td>
 							<td>${data.blupsTest }</td>
 							<td>${data.rotatedResidualsNormalityTest }</td>
 							<td>${data.interceptEstimate }</td>
 							<td>${data.interceptEstimateStandardError }</td>
 							<td>${data.interactionSignificance }</td>
 							<td>${data.interactionEffectPValue }</td>
 							<td>${data.genderFemaleKoEstimate }</td>
 							<td>${data.genderFemaleKoStandardErrorEstimate }</td>
 							<td>${data.genderFemaleKoPValue }</td>
 							<td>${data.genderMaleKoEstimate }</td>
 							<td>${data.genderMaleKoStandardErrorEstimate }</td>
 							<td>${data.genderMaleKoPValue }</td>
 							</tr>
						</c:forEach>
 	 					</table>--%>
 	 							
 						</div>
 				</div>
 				</c:if>	
	<!-- </div> -->
</div>
 				</c:if>
			<!-- end of row fluid data set -->
</c:forEach>
		<!--  end of undimensional data loop -->

<script>
	$(document)
			.ready(
					function() {
			//			alert("scatter");
						$.fn.qTip({
							'pageName': 'scatter',
							'textAlign': 'left',
							'tip': 'topRight'
						});
						$('div#exportIconsDiv').append(
								$.fn.loadFileExporterUI({
									label : 'Export data as: ',
									formatSelector : {
										TSV : 'tsv_phenoAssoc',
										XLS : 'xls_phenoAssoc'
									},
									class : 'fileIcon'
								}));

						var params = window.location.href.split("/")[window.location.href
								.split("/").length - 1];
						var mgiGeneId = params.split("\?")[0];
						var paramId = params.split("parameterId\=")[1].split("\&")[0];
						var paramIdList = paramId;
						for (var k = 2; k < params.split("parameterId\=").length; k++){
							paramIdList += "\t" + params.split("parameterId\=")[k].split("\&")[0];
						}
						var sex = (params.indexOf("gender\=") > 0) ? params.split("gender\=")[1].split("\&")[0] : null;
						var zygosity = null;
						if (params.indexOf("zygosity\=") > 0)
							zygosity = params.split("zygosity\=")[1].split("\&")[0];
						var windowLocation = window.location;
						var phenotypingCenter = (params.indexOf("phenotypingCenter\=") > 0) ? params.split("phenotypingCenter\=")[1].split("\&")[0] : null;

						initFileExporter({
							mgiGeneId : mgiGeneId,
							externalDbId : 3,
							fileName : 'scatterPlotData'
									+ mgiGeneId.replace(/:/g, '_'),
							solrCoreName : 'experiment',
							dumpMode : 'all',
							baseUrl : windowLocation,
							parameterStableId : paramIdList,
							zygosity: zygosity,
							sex: sex,
							phenotypingCenter: phenotypingCenter,
							page : "scatterPlot",
							gridFields : 'gene_accession,date_of_experiment,discrete_point,gene_symbol,data_point,zygosity,sex,date_of_birth,time_point',
							params : "qf=auto_suggest&defType=edismax&wt=json&q=*:*&fq=gene_accession:\""
									+ mgiGeneId
									+ "\"&fq=parameter_stable_id:\""
									+ paramId
									+ "\"&fq=phenotyping_center:\""
									+ phenotypingCenter
									+ "\""
						});

						function initFileExporter(conf) {
							$('button.fileIcon')
									.click(
											function() {
												var fileType = $(this).text();
												var url = baseUrl + '/export';
												var sInputs = '';
												for ( var k in conf) {
														sInputs += "<input type='text' name='" + k + "' value='" + conf[k] + "'>";
												}
												sInputs += "<input type='text' name='fileType' value='"
														+ fileType
																.toLowerCase()
														+ "'>";
												var form = $("<form action='"+ url + "' method=get>"
														+ sInputs + "</form>");
												_doDataExport(url, form);
											}).corner('6px');
						}

						function _doDataExport(url, form) {
							$
									.ajax({
										type : 'GET',
										url : url,
										cache : false,
										data : $(form).serialize(),
										success : function(data) {
											$(form).appendTo('body').submit()
													.remove();
										},
										error : function() {
											alert("Oops, there is error during data export..");
										}
									});
						}
					});
</script>


    </jsp:body>
</t:genericpage>
