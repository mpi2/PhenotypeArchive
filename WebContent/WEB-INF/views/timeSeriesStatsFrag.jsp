<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>


<!-- time series charts here-->

<c:forEach var="timeChart" items="${timeSeriesChartsAndTables}"
	varStatus="timeLoop">
	<%-- ${loop.count  % 2} --%>
	<c:if test="${timeLoop.count  % 2!=0}">
		<div class="row-fluid dataset">
			<div class="row-fluid ">
				<div class="container span6">
					<h4>
						<!--  style="background-color:lightgrey;"  -->
						Allele -
						<t:formatAllele>${timeChart.expBiologicalModel.alleles[0].symbol}</t:formatAllele>
						<span class="graphGenBackground">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;background
							-
							${timeSeriesChartsAndTables[timeLoop.index].expBiologicalModel.geneticBackground}</span>
					</h4>
				</div>
				<c:if
					test="${fn:length(timeSeriesChartsAndTables) > (timeLoop.index+1)}">
					<div class="container span6">
						<h4>
							<!--  style="background-color:lightgrey;"  -->
							Allele -
							<t:formatAllele>${timeSeriesChartsAndTables[timeLoop.index+1].expBiologicalModel.alleles[0].symbol}</t:formatAllele>
							<span class="graphGenBackground">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;background
								-
								${timeSeriesChartsAndTables[timeLoop.index+1].expBiologicalModel.geneticBackground}</span>
						</h4>
					</div>
				</c:if>
				
			</div>
			<div class="row-fluid">
	</c:if>
	<div class="container span6">
		<div id="timeChart${timeLoop.count}"
			style="min-width: 400px; height: 450px; margin: 0 auto"></div>
		<script type="text/javascript">
			${timeChart.chart}
		</script>
	</div>

	<c:if
		test="${(timeLoop.count % 2==0) || timeLoop.count eq fn:length(timeSeriesChartsAndTables)}">

		<a href="scatter/${acc}?${pageContext.request.queryString}">Graph
			by date</a>


	<%--div id="exportIconsDivTS"></div--%>	
		</div>
		</div>
		
	</c:if>

</c:forEach>


<script>
	$(document)
			.ready(
					function() {
						$('div#exportIconsDivTS').append(
								$.fn.loadFileExporterUI({
									label : 'Export data as: (timeSeries)',
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
						var windowLocation = window.location;

						initFileExporter({
							mgiGeneId : mgiGeneId,
							externalDbId : 3,
							fileName : 'timeSeriesData'
									+ mgiGeneId.replace(/:/g, '_'),
							solrCoreName : 'experiment',
							dumpMode : 'all',
							baseUrl : windowLocation,
							page : "timeSeries",
							gridFields : 'geneAccession,dateOfExperiment,discretePoint,geneSymbol,dataPoint,zygosity,gender,dateOfBirth,timePoint',
							params : "qf=auto_suggest&defType=edismax&wt=json&q=*:*&fq=geneAccession:\""
									+ mgiGeneId
									+ "\"&fq=parameterStableId:"
									+ paramId
									+ "&start=0&rows=10000"
						});

						function initFileExporter(conf) {
							$('button.fileIcon')
									.click(
											function() {
											//	alert("click on timeseries");
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
						//					alert("Oops, there is error during data export..");
										}
									});
						}
					});
</script>