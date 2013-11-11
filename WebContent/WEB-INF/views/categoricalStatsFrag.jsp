<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<c:if test="${fn:length(allCategoricalResultAndCharts) > 0}">
	<div id="exportIconsDivCat"></div>
	 <script>
	$(document)
			.ready(
					function() {
						// alert("categorical");
						$('div#exportIconsDivCat').append(
								$.fn.loadFileExporterUI({
									label : 'Export data as: (categ)',
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
		//				var sex = (params.indexOf("gender\=") > 0) ? params.split("gender\=")[1].split("\&")[0] : null;

						initFileExporter({
							mgiGeneId : mgiGeneId,
							externalDbId : 3,
							fileName : 'categoricalData'
									+ mgiGeneId.replace(/:/g, '_'),
							solrCoreName : 'experiment',
							dumpMode : 'all',
							baseUrl : windowLocation,
							page : "categorical",
							parameterStableId : paramIdList,
							zygosity: zygosity,
							gridFields : 'geneAccession,dateOfExperiment,discretePoint,geneSymbol,zygosity,gender,dateOfBirth,timePoint',
							params : "qf=auto_suggest&defType=edismax&wt=json&q=*:*&fq=geneAccession:\""
									+ mgiGeneId
									+ "\"&fq=parameterStableId:"
									+ paramId
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
					//						alert("Oops, there is error during data export..");
										}
									});
						}
					});
</script>
	
</c:if>
<c:forEach var="categoricalResultAndCharts" items="${allCategoricalResultAndCharts}" varStatus="experimentLoop">
	<div class="row-fluid dataset">
	<c:if test="${fn:length(categoricalResultAndCharts.maleAndFemale)==0}">
	No Categorical data for this zygosity and gender for this parameter and gene
	</c:if>
	<c:if test="${fn:length(categoricalResultAndCharts.maleAndFemale)>0}">
 		<div class="row-fluid">
				<div class="container span12">
		 				<h4>Allele - <t:formatAllele>${categoricalResultAndCharts.maleAndFemale[0].biologicalModel.alleles[0].symbol}</t:formatAllele> <span class="graphGenBackground"> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;background -  ${categoricalResultAndCharts.maleAndFemale[0].biologicalModel.geneticBackground} </span></h4>
		 				<h5>${categoricalResultAndCharts.organisation}</h5>
		 		</div>
		 </div>
		
				<div class="row-fluid">
 				<c:forEach var="categoricalChartDataObject" items="${categoricalResultAndCharts.maleAndFemale}" varStatus="chartLoop">
  				 	<div class="container span6">
								<div id="categoricalBarChart${categoricalChartDataObject.chartIdentifier}"
									style="min-width: 400px; height: 400px; margin: 0 auto">
								</div>
   								<script type="text/javascript">
   								${categoricalChartDataObject.chart}
   							</script>
					</div>
 				</c:forEach>
				</div>
 <%--  ${categoricalResultAndCharts.maleAndFemale} --%>
 				
 
 				<div class="row-fluid">
 				<table id="catTable${experimentLoop.index}" class="table table-bordered  table-striped table-condensed">
 							<thead><tr>
 										<th>Exp Sex</th>
										<th>Control/Hom/Het</th><!-- blank usually as no header -->
										<!-- loop over categories -->
										<c:forEach var="categoryObject"  items="${categoricalResultAndCharts.maleAndFemale[0].categoricalSets[0].catObjects}" varStatus="categoriesStatus">
												<th>${categoryObject.category }</th>
										</c:forEach>
										<th>P Value</th>
										<th>Max Effect</th>
										</tr>	
							</thead>	
							<tbody>
							<c:forEach var="maleOrFemale"  items="${categoricalResultAndCharts.maleAndFemale}" varStatus="maleOrFemaleStatus">
												
														
														<c:forEach var="categoricalSet"  items="${maleOrFemale.categoricalSets}" varStatus="catSetStatus">
														<tr>
														<td>${maleOrFemale.sexType }</td>
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
						<%-- <table id="catTable${categoricalChartDataObject.chartIdentifier}" class="table table-bordered  table-striped table-condensed">
										<thead><tr>
										<th>Control/Hom/Het</th><!-- blank usually as no header -->
										<!-- loop over categories -->
										<c:forEach var="category"  items="${categoricalResultAndCharts.categories}" varStatus="catStatsStatus">
										<th>normal</th><th>ctrl zygosity</th><th>exp zygosity</th><th>Ctrl ${categoricalResultAndCharts.statsResults[0].categoryA}</th><th>Ctrl ${categoricalResultAndCharts.statsResults[0].categoryB}</th><th>Exp ${categoricalResultAndCharts.statsResults[0].categoryA}</th><th>Exp ${categoricalResultAndCharts.statsResults[0].categoryB}</th><th>p Value</th><th>Effect size</th>
										</tr></thead>
										<tbody>
										<c:forEach var="catStatsResult"  items="${categoricalResultAndCharts.statsResults}" varStatus="catStatsStatus">
										<tr>
											<td> ${catStatsResult.controlSex }</td><td> ${catStatsResult.experimentalSex }</td><td>${catStatsResult.controlZygosity }</td><td>${catStatsResult.experimentalZygosity }</td>
											<td>
													<c:forEach var="sexCatObject" items="${categoricalResultAndCharts.maleAndFemale}">
												
														<c:if test="${sexCatObject.sexType==catStatsResult.experimentalSex}" >
  
														blah ${sexCatObject.sexType} ${catStatsResult.experimentalSex}
																<c:forEach var="controlHomOrHetObjectCatSetObject" items="${sexCatObject.categoricalSets}">
																	<c:forEach var="catObject" items="${controlHomOrHetObjectCatSetObject.catObjects }">
																	<c:if test="${catObject.name =='control' && catObject.category==catStatsResult.categoryA}" >${catObject.count }
																	</c:if>
																	</c:forEach>
																</c:forEach>
														</c:if> 
													</c:forEach>
											</td>
											<td><c:forEach var="sexCatObject" items="${categoricalResultAndCharts.maleAndFemale}">
												
														<c:if test="${sexCatObject.sexType==catStatsResult.experimentalSex}" >
  
														blah ${sexCatObject.sexType} ${catStatsResult.experimentalSex}
																<c:forEach var="controlHomOrHetObjectCatSetObject" items="${sexCatObject.categoricalSets}">
																	<c:forEach var="catObject" items="${controlHomOrHetObjectCatSetObject.catObjects }">
																	<c:if test="${catObject.name =='control' && catObject.category==catStatsResult.categoryB}" >${catObject.count }
																	</c:if>
																	</c:forEach>
																</c:forEach>
														</c:if> 
													</c:forEach>
											</td>
											<td>
												<c:forEach var="sexCatObject" items="${categoricalResultAndCharts.maleAndFemale}">
												
														<c:if test="${sexCatObject.sexType==catStatsResult.experimentalSex}" >
  
														 ${sexCatObject.sexType} ${catStatsResult.experimentalSex}
																<c:forEach var="controlHomOrHetObjectCatSetObject" items="${sexCatObject.categoricalSets}">
																	<c:forEach var="catObject" items="${controlHomOrHetObjectCatSetObject.catObjects }">
																	catName=${catObject.name} zyg= ${catStatsResult.experimentalZygosity }
																	<c:if test="${(catObject.name ==catStatsResult.experimentalZygosity.name) && catObject.category==catStatsResult.categoryA}" > ${catObject.count }
																	</c:if>
																	</c:forEach>
																</c:forEach>
														</c:if> 
													</c:forEach>
												
											</td>
											<td>
											<c:forEach var="sexCatObject" items="${categoricalResultAndCharts.maleAndFemale}">
												
														<c:if test="${sexCatObject.sexType==catStatsResult.experimentalSex}" >
  
														 ${sexCatObject.sexType} ${catStatsResult.experimentalSex}
																<c:forEach var="controlHomOrHetObjectCatSetObject" items="${sexCatObject.categoricalSets}">
																	<c:forEach var="catObject" items="${controlHomOrHetObjectCatSetObject.catObjects }">
																	catName=${catObject.name} zyg= ${catStatsResult.experimentalZygosity }
																	<c:if test="${(catObject.name ==catStatsResult.experimentalZygosity.name) && catObject.category==catStatsResult.categoryB}" > ${catObject.count }
																	</c:if>
																	</c:forEach>
																</c:forEach>
														</c:if> 
											</c:forEach>
											</td>
											<td>${catStatsResult.pValue }</td><td>${catStatsResult.maxEffect}</td>
										</tr>
										</c:forEach>
						</table> --%>
					<%-- 	<c:forEach var="sexCatObject" items="${categoricalResultAndCharts.maleAndFemale}">
						${sexCatObject.sexType}<br/>
								<c:forEach var="controlHomOrHetObjectCatSetObject" items="${sexCatObject.categoricalSets}">
									${controlHomOrHetObjectCatSetObject.catObjects } <br/>
								</c:forEach>
						</c:forEach> --%>
				</div>
				
				</c:if>
 	</div>
 </c:forEach>
 

<!--/end of categoriacl charts-->


