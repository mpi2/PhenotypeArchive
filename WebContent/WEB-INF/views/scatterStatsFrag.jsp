<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

	<c:if test="${scatterChartAndData!=null}">
	
	<div id="exportIconsDiv"></div>
	<!-- scatter chart here -->
  					<div id="chart${experimentNumber}">
								</div>
								${timeSeriesChartsAndTable.chart}
		<script type="text/javascript">
			${scatterChartAndData.chart}
		</script>	
	
	<!-- remove the scatter flag so we go back to the original graph version -->
						<c:set var="originalUrl" value="${fn:replace(pageContext.request.queryString, 
                                '&scatter=true', '')}" />
                                
                                <a href="${acc}?${originalUrl}">Box Plot / Time Series Graphs</a>	
                                
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
											});
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
	
	</c:if>
	
	