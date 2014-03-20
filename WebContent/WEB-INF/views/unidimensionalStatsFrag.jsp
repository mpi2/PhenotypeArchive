<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>


					<%-- <c:forEach var="unidimensionalChartsAndTable" items="${unidimensionalDataSet.sexChartAndTables}" varStatus="uniDimensionalLoop">
 					${loop.count  % 2}
 --%>
 <!-- unidimensional here -->
 	<c:if test="${unidimensionalChartDataSet!=null}">
 	
  					<div id="chart${experimentNumber}">
								</div>
   								<script type="text/javascript">
   
   								$(function () {
   								   ${unidimensionalChartDataSet.chartData.chart}
								</script>
								<div class="section half"><a href="${acc}?${pageContext.request.queryString}&scatter=1">Graph by date</a></div><div class="section half"><div id="exportIconsDivUni"></div></div>
	
		<table id="continuousTable">
		<thead><tr>
		<th>Line</th>
		<th>Zygosity</th>
			<th>Mean</th>
			<th>SD</th>
			<th>Sex</th>
			<th>Count</th>
			<th>Effect Size</th>
			<th>pValue</th>
		</tr></thead>
		<tbody>									
										
										
											<c:forEach var="statsObject" items="${unidimensionalChartDataSet.statsObjects}">
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
												<td>${statsObject.mean}</td>
												<td>${statsObject.sd}</td>
												<td><c:choose><c:when test="${statsObject.sexType eq 'female'}"><img style="cursor:help;color:#D6247D;" rel="tooltip" data-placement="top" title="Female" alt="Female" src="${baseUrl}/img/female.jpg" /></c:when><c:otherwise><img style="cursor:help;color:#247DD6;" rel="tooltip" data-placement="top" title="Male" alt="Male" src="${baseUrl}/img/male.jpg" /></c:otherwise></c:choose></td>
												<c:if test="${statsObject.sexType eq 'female'}">
												<td>${statsObject.sampleSize}</td>
												</c:if>
												<c:if test="${statsObject.sexType eq 'male'}">
												<td>${statsObject.sampleSize}</td>
												</c:if>
												<td>${statsObject.result.effectSize}</td>
												<td>${statsObject.result.pValue}</td>
												</tr>
												</c:forEach>
										</tbody>
										</table>
				
				
				
				<c:if test="${fn:length(unidimensionalDataSet.allUnidimensionalResults)>0}">
				<div class="section">
						<table>
						${fn:length(unidimensionalDataSet.allUnidimensionalResults)}
						
 							<c:forEach var="data" items="${unidimensionalDataSet.allUnidimensionalResults}">
 							<td>${data.significanceClassification}</td>
 									<c:choose>
          									<c:when test="${data.significanceClassification == 'both_equally' || data.significanceClassification == 'none'  || data.significanceClassification == 'cannot_classify' }">
          												<tr><th>Global Test</th><th>Significance/Classification</th><th>Effect</th></tr>
          												<tr><td>${data.nullTestSignificance}</td><td>${data.significanceClassification.text}</td><td>${data.genotypeParameterEstimate}</td></tr></c:when>
         									<c:when test="${data.significanceClassification == 'female_only' || data.significanceClassification == 'male_only'  || data.significanceClassification == 'female_greater' || data.significanceClassification == 'male_greater' || data.significanceClassification == 'different_directions'}">
       													 <tr><th>Global Test</th><th>Significance/Classification</th><th>Gender</th><th>Effect</th></tr>
       													 <tr>
       													 <td rowspan="2">${data.nullTestSignificance}</td>
       													 <td rowspan="2">${data.significanceClassification.text}</td>
       													 <td>Female</td><td>${data.genderFemaleKoEstimate}</td>
       													 <tr><td> Male</td><td> ${data.genderMaleKoEstimate}</td></tr></c:when>
									</c:choose>
									</c:forEach>
 	 							</table>
 	 							
 	 		<table>
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
 						<c:forEach var="data" items="${unidimensionalDataSet.allUnidimensionalResults}">
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
 	 					</table>
 	 					
 				</div>
 				
 				</c:if>
 				
 				<script>
 				
 				/* <a href="#"><i class="fa fa-download"></i> csv</a> */
	$(document)
			.ready(
					function() {
				//		alert("unidimensional");
						$('div#exportIconsDivUni').html("");
						$('div#exportIconsDivUni').html(
								$.fn.loadFileExporterUI({
									label : 'Export data as: ',
									formatSelector : {
										TSV : 'tsv_phenoAssoc',
										XLS : 'xls_phenoAssoc'
									},
									class : 'fileIcon exportButton'
								}));

						var params = window.location.href.split("/charts?")[1]; //.split("&");
						var paramList = window.location.href.split("/charts?")[1].split("&");
						var windowLocation = window.location;
						var sex = (params.indexOf("gender\=") > 0) ? params.split("gender\=")[1].split("\&")[0] : null;
						var paramIdList = [];
						var mgiGeneId = [];
						var phenotypingCenter = [];
						var strains = [];
						var zygosity = [];
						for (var k = 0; k < paramList.length; k++){
							if (paramList[k].indexOf("parameterId") >= 0){
								paramIdList.push(paramList[k].replace("parameterId=", ""));
								console.log ("parameter : " + paramList[k].replace("parameterId=", "") + " list is " + paramIdList);
							}
							else if (paramList[k].indexOf("accession") >= 0){
								mgiGeneId.push(paramList[k].replace("accession=", ""));
								console.log ("parameter : " + paramList[k].replace("accession=", "") + " list is " + mgiGeneId);
							}
							else if (paramList[k].indexOf("phenotyping_center") >= 0){
								phenotypingCenter.push(paramList[k].replace("phenotyping_center=", ""));
								console.log ("parameter : " + paramList[k].replace("phenotyping_center=", "") + " list is " + phenotypingCenter);
							}
							else if (paramList[k].indexOf("strain") >= 0){
								strains.push(paramList[k].replace("strain=", ""));
								console.log ("parameter : " + paramList[k].replace("strain=", "") + " list is " + strains);
							}
							else if (paramList[k].indexOf("zygosity") >= 0){
								zygosity.push(paramList[k].replace("zygosity=", ""));
								console.log ("parameter : " + paramList[k].replace("zygosity=", "") + " list is " + zygosity);
							}
						}
						
						initFileExporter({
							mgiGeneId : mgiGeneId,
							externalDbId : 3,
							fileName : 'unidimensionalData_'
									+ mgiGeneId[0].replace(/:/g, '_'),
							solrCoreName : 'experiment',
							dumpMode : 'all',
							baseUrl : windowLocation,
							parameterStableId : paramIdList,
							zygosity: zygosity,
							sex: sex,
							strains: strains,
							phenotypingCenter: phenotypingCenter,
							page : "unidimensionalData",
							gridFields : 'gene_accession,date_of_experiment,discrete_point,gene_symbol,data_point,zygosity,sex,date_of_birth,time_point',
							params : ""
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
 				
