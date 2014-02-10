<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<!-- time series charts here-->
<c:if test="${timeSeriesChartsAndTable.chart!=null}">

<div id="exportIconsDivTS"></div>
		<div id="timechart${experimentNumber}">
		</div>
	 <a href="${acc}?${pageContext.request.queryString}&scatter=1">Graph by date</a>
				<script type="text/javascript">
					${timeSeriesChartsAndTable.chart}
		
	$(document)
			.ready(
					function() {
						$('div#exportIconsDivTS').html(
								$.fn.loadFileExporterUI({
									label : 'Export data as: ',
									formatSelector : {
										TSV : 'tsv_phenoAssoc',
										XLS : 'xls_phenoAssoc'
									},
									class : 'fileIcon exportButton'
								}));

						var params = window.location.href.split("/")[window.location.href
								.split("/").length - 1];
						var mgiGeneId = params.split("\?")[0];
						var windowLocation = window.location;						
						var paramId = params.split("parameterId\=")[1].split("\&")[0];
						var paramIdList = paramId;
						for (var k = 2; k < params.split("parameterId\=").length; k++){
							paramIdList += "\t" + params.split("parameterId\=")[k].split("\&")[0];
						}
						var zygosity = null;
						if (params.indexOf("zygosity\=") > 0)
							zygosity = params.split("zygosity\=")[1].split("\&")[0];
						var phenotypingCenter = (params.indexOf("phenotyping_center\=") > 0) ? params.split("phenotyping_center\=")[1].split("\&")[0] : null;
						var sex = (params.indexOf("gender\=") > 0) ? params.split("gender\=")[1].split("\&")[0] : null;
						initFileExporter({
							mgiGeneId : mgiGeneId,
							externalDbId : 3,
							fileName : 'timeSeriesData'
									+ mgiGeneId.replace(/:/g, '_'),
							solrCoreName : 'experiment',
							dumpMode : 'all',
							baseUrl : windowLocation,
							page : "timeSeries",
							parameterStableId : paramIdList,
							phenotypingCenter: phenotypingCenter,
							zygosity: zygosity,
							sex: sex,
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
						//					alert("Oops, there is error during data export..");
										}
									});
						}
					});
</script>
</c:if>
	