<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<c:if test="${categoricalResultAndChart!=null}">
	<div id="exportIconsDivCat"></div>
	 <script>
	$(document)
			.ready(
					function() {
						// alert("categorical");
						$('div#exportIconsDivCat').append(
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
						var windowLocation = window.location;
						var paramId = params.split("parameterId\=")[1].split("\&")[0];
						var paramIdList = paramId;
						for (var k = 2; k < params.split("parameterId\=").length; k++){
							paramIdList += "\t" + params.split("parameterId\=")[k].split("\&")[0];
						}
						var zygosity = (params.indexOf("zygosity\=") > 0) ? params.split("zygosity\=")[1].split("\&")[0] : null;
						var sex = (params.indexOf("gender\=") > 0) ? params.split("gender\=")[1].split("\&")[0] : null;
						var phenotypingCenter = (params.indexOf("phenotypingCenter\=") > 0) ? params.split("phenotypingCenter\=")[1].split("\&")[0] : null;
						
						initFileExporter({
							mgiGeneId : mgiGeneId,
							externalDbId : 3,
							fileName : 'categoricalData'
									+ mgiGeneId.replace(/:/g, '_'),
							solrCoreName : 'experiment',
							dumpMode : 'all',
							baseUrl : windowLocation,
							page : "categorical",
							phenotypingCenter: phenotypingCenter,
							parameterStableId : paramIdList,
							zygosity: zygosity,
							sex: sex,
							gridFields : 'gene_accession,date_of_experiment,discrete_point,gene_symbol,data_point,zygosity,sex,date_of_birth,time_point',
							params : "qf=auto_suggest&defType=edismax&wt=json&q=*:*&fq=gene_accession:\""
									+ mgiGeneId
									+ "\"&fq=parameter_stable_id:\""
									+ paramId
									+ "\"&fq=phenotyping_center:\""
									+ phenotypingCenter
									+"\""
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
					//						alert("Oops, there is error during data export..");
										}
									});
						}
					});
</script>
	
</c:if>

	
<!-- categorical here -->
				<div class="row-fluid">
 				<c:forEach var="categoricalChartDataObject" items="${categoricalResultAndChart.maleAndFemale}" varStatus="chartLoop">
  				 	
								<div id="chart${experimentNumber}">
								</div>
   								<script type="text/javascript">
   								${categoricalChartDataObject.chart}
   							</script>
				
					
					
					<table id="catTable" class="table table-bordered  table-striped table-condensed">
 							<thead><tr>
 										
										<th>Control/Hom/Het</th><!-- blank usually as no header -->
										<!-- loop over categories -->
										<c:forEach var="categoryObject"  items="${categoricalResultAndChart.maleAndFemale[0].categoricalSets[0].catObjects}" varStatus="categoriesStatus">
												<th>${categoryObject.category }</th>
										</c:forEach>
										<th>P Value</th>
										<th>Max Effect</th>
										</tr>	
							</thead>	
							<tbody>
							<c:forEach var="maleOrFemale"  items="${categoricalResultAndChart.maleAndFemale}" varStatus="maleOrFemaleStatus">
												
														
														<c:forEach var="categoricalSet"  items="${maleOrFemale.categoricalSets}" varStatus="catSetStatus">
														<tr>
																<td>${categoricalSet.name }</td>
																		<c:forEach var="catObject"  items="${categoricalSet.catObjects}" varStatus="catObjectStatus">
																				<td>${catObject.count } </td>
																				<%-- ${catObject} --%>
																		</c:forEach>
																		<%-- ${categoricalSet.catObjects[catSetStatus.index].result} --%>
																		<td>${categoricalSet.catObjects[0].pValue } </td>
																		<td>${categoricalSet.catObjects[0].maxEffect } </td>
														</tr>
														</c:forEach>
												
							</c:forEach>
							
							</tbody>
 				</table>
 				</c:forEach>
				</div>



