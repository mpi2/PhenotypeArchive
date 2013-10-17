<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>


<c:forEach var="unidimensionalDataSet" items="${allUnidimensionalDataSets}" varStatus="unidimensionalDataSetLoop">
 <div class="row-fluid dataset"> 
 		<c:if test="${fn:length(unidimensionalDataSet.statsObjects)==0}">
		No unidimensional data for this zygosity and gender for this parameter and gene
		</c:if>
		<c:if test="${fn:length(unidimensionalDataSet.statsObjects)>0}">
		 	<div class="row-fluid">
		 			<div class="container span6"><h4>Allele -  <t:formatAllele> ${unidimensionalDataSet.statsObjects[1].allele }</t:formatAllele> <span class="graphGenBackground">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;background -  ${unidimensionalDataSet.statsObjects[1].geneticBackground }</span></h4>
				 	</div>
 			</div>
 			<div class="row-fluid">
					<c:forEach var="unidimensionalChartsAndTable" items="${unidimensionalDataSet.sexChartAndTables}" varStatus="uniDimensionalLoop">
 					<%-- ${loop.count  % 2} --%>

  					<div class="container span6">
						<div id="chart${unidimensionalDataSetLoop.count}_${uniDimensionalLoop.count}"
									style="min-width: 400px; height: 400px; margin: 0 auto">
						</div>
   								<script type="text/javascript">
   
   								$(function () {
   								    $('#chart${unidimensionalDataSetLoop.count}_${uniDimensionalLoop.count}').highcharts(${unidimensionalChartsAndTable.chart});
								</script>
								<a href="scatter/${acc}?${pageContext.request.queryString}">Scatter Versions</a>	
					</div><!-- end of span6  individual chart holder -->
			
		
				</c:forEach>
		</div><!-- end of chart row-fluid -->
		<table id="continuousTable${uniDimensionalLoop.count}" class="table table-bordered  table-striped table-condensed">
		<thead><tr>
		<th>Line</th>
		<th>Zygosity</th>
			<th>Sex</th>
			<th>Mean</th>
			<th>SD</th>
			<th>Sample Size</th>
		<%-- <th>${tables[loop.count-1].xAxisCategories[1]}</th><th>${tables[loop.count-1].xAxisCategories[2]}</th> --%>
		</tr></thead>
		<tbody>
										
										
										
											<c:forEach var="statsObject" items="${unidimensionalDataSet.statsObjects}">
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
												<c:choose>
												<c:when test="${statsObject.line =='Control' }">
												<td>Mixed</td>
												</c:when>
												<c:when test="${statsObject.line !='Control' }">
												<td>${statsObject.sexType}</td>
												</c:when>
												</c:choose>
												
												<td>${statsObject.mean}</td>
												<td>${statsObject.sd}</td>
												<td>${statsObject.sampleSize}</td>
												</tr>
												</c:forEach>
												
									
										
										<%-- <td>${tables[loop.count-1].seriesDataForCategoricalType[0][0]}</td><td>${tables[loop.count-1].seriesDataForCategoricalType[0][1]}</td><td>${tables[loop.count-1].seriesDataForCategoricalType[0][2]}</td>
										 </tr>--%>
										<%-- <tr><td>${tables[loop.count-1].categories[1]}</td><td>${tables[loop.count-1].seriesDataForCategoricalType[1][0]}</td><td>${tables[loop.count-1].seriesDataForCategoricalType[1][1]}</td><td>${tables[loop.count-1].seriesDataForCategoricalType[1][2]}</td>
										 </tr>--%>
										</tbody>
										</table>
				
				
				<c:if test="${fn:length(unidimensionalDataSet.allUnidimensionalResults)>0}">
				<div class="row-fluid">
						<div class="container span12">
						<table class="ttable table-bordered  table-striped table-condensed">
						<%-- ${fn:length(unidimensionalDataSet.allUnidimensionalResults)} --%>
						
 							<c:forEach var="data" items="${unidimensionalDataSet.allUnidimensionalResults}">
 							<%-- <td>${data.significanceClassification}</td> --%>
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
 	 					</table>--%>
 	 							
 						</div>
 				</div>
 				</c:if>	
 				</c:if>
	<!-- </div> -->
</div><!-- end of row fluid data set -->
</c:forEach><!--  end of undimensional data loop -->