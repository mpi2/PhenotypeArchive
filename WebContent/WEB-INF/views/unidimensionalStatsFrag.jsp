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
								<div class="section half"><a href="${acc}?${pageContext.request.queryString}&scatter=1">Graph by date</a></div><div class="section half"></div>
	
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
				
				
				some *** result should be here: ${unidimensionalChartDataSet.statsObjects[1].result}
			<c:if test="${fn:length(unidimensionalChartDataSet.statsObjects)>1}"> 
			
				<div class="section collapsed">
				<h2 class="title">More Stats</h2>
				<div class="inner">
						<table>				
 							<c:set var="data" value="${unidimensionalChartDataSet.statsObjects[1]}"></c:set>
 									<c:choose>
          									<c:when test="${data.result.significanceClassification.text == 'Both genders equally' || data.result.significanceClassification.text == 'No significant change'  || data.result.significanceClassification.text == 'Can not differentiate genders' }">
          												<tr><th>Global Test</th><th>Significance/Classification</th><th>Effect</th></tr>
          												<tr><td>${data.result.nullTestSignificance}</td><td>${data.result.significanceClassification.text}</td><td>${data.result.genotypeParameterEstimate}</td></tr></c:when>
         									<c:when test="${data.result.significanceClassification == 'female_only' || data.result.significanceClassification == 'male_only'  || data.result.significanceClassification == 'female_greater' || data.result.significanceClassification == 'male_greater' || data.result.significanceClassification == 'different_directions'}">
       													 <tr><th>Global Test</th><th>Significance/Classification</th><th>Gender</th><th>Effect</th></tr>
       													 <tr>
       													 <td rowspan="2">${data.result.nullTestSignificance}</td>
       													 <td rowspan="2">${data.result.significanceClassification.text}</td>
       													 <td>Female</td><td>${data.result.genderFemaleKoEstimate}</td>
       													 <tr><td> Male</td><td> ${data.result.genderMaleKoEstimate}</td></tr></c:when>
									</c:choose>
 	 							</table>
						
						<%-- <th>mixedModel</th> --%>
						

 						<c:set var="data" value="${unidimensionalChartDataSet.statsObjects[1]}"></c:set>
 						<c:if test="${data.result!=null }">
 						<table>
 						<tr><th>Stats Result Name</th><th>Value</th></tr>	
 							<c:if test="${data.result.colonyId!=null}"><tr>
 							<td>colonyId</td>
 							<td>${data.result.colonyId }</td>
 							</tr></c:if>
 							<c:if test="${data.result.experimentalZygosity!=null}"><tr>
 							<td>experimentalZygosity</td>
 							<td>${data.result.experimentalZygosity}</td>
 							</tr></c:if>
 							<%-- <td>${data.result.mixedModel}</td> --%>
 							<c:if test="${data.result.dependantVariable!=null}"><tr>
 							<td>dependantVariable</td>
 							<td>${data.result.dependantVariable}</td>
 							</tr></c:if>
 							<c:if test="${data.result.batchSignificance!=null}"><tr><td>batchSignificance</td><td>${data.result.batchSignificance }</td></tr></c:if>
 							<c:if test="${data.result.varianceSignificance!=null}"><tr><td>varianceSignificance</td><td>${data.result.varianceSignificance }</td></tr></c:if>
 							<c:if test="${data.result.nullTestSignificance !=null}"><tr><td>nullTestSignificance</td><td>${data.result.nullTestSignificance }</td></tr></c:if>
 							<c:if test="${data.result.genotypeParameterEstimate!=null}"><tr><td>genotypeParameterEstimate</td><td>${data.result.genotypeParameterEstimate }</td></tr></c:if>
 							<c:if test="${data.result.genotypeStandardErrorEstimate!=null}"><tr><td>genotypeStandardErrorEstimate</td><td>${data.result.genotypeStandardErrorEstimate }</td></tr></c:if>
 							<c:if test="${data.result.genotypeEffectPValue!=null}"><tr><td>genotypeEffectPValue</td><td>${data.result.genotypeEffectPValue}</td></tr></c:if>
 							<c:if test="${data.result.genderParameterEstimate!=null}"><tr><td>genderParameterEstimate</td><td>${data.result.genderParameterEstimate }</td></tr></c:if>
 							<c:if test="${data.result.genderStandardErrorEstimate!=null}"><tr><td>genderStandardErrorEstimate</td><td>${data.result.genderStandardErrorEstimate }</td></tr></c:if>
 							<c:if test="${data.result.genderEffectPValue!=null}"><tr><td>genderEffectPValue</td><td>${data.result.genderEffectPValue}</td></tr></c:if>
 							<c:if test="${data.result.weightParameterEstimate!=null}"><tr><td>weightParameterEstimate </td><td>${data.result.weightParameterEstimate }</td></tr></c:if>
 							<c:if test="${data.result.weightStandardErrorEstimate!=null}"><tr><td>weightStandardErrorEstimate </td><td>${data.result.weightStandardErrorEstimate }</td></tr></c:if>
 							<c:if test="${data.result.weightEffectPValue!=null}"><tr><td>weightEffectPValue </td><td>${data.result.weightEffectPValue }</td></tr></c:if>
 							<c:if test="${data.result.gp1Genotype!=null}"><tr><td>gp1Genotype </td><td>${data.result.gp1Genotype }</td></tr></c:if>
 							<c:if test="${data.result.gp1ResidualsNormalityTest!=null}"><tr><td>gp1ResidualsNormalityTest </td><td>${data.result.gp1ResidualsNormalityTest }</td></tr></c:if>
 							<c:if test="${data.result.gp2Genotype!=null}"><tr><td>gp2Genotype </td><td>${data.result.gp2Genotype }</td></tr></c:if>
 							<c:if test="${data.result.gp2ResidualsNormalityTest!=null}"><tr><td>gp2ResidualsNormalityTest </td><td>${data.result.gp2ResidualsNormalityTest }</td></tr></c:if>
 							<c:if test="${data.result.blupsTest!=null}"><tr><td>blupsTest </td><td>${data.result.blupsTest }</td></tr></c:if>
 							<c:if test="${data.result.rotatedResidualsNormalityTest !=null}"><tr><td>rotatedResidualsNormalityTest </td><td>${data.result.rotatedResidualsNormalityTest }</td></tr></c:if>
 							<c:if test="${data.result.interceptEstimate!=null}"><tr><td>interceptEstimate </td><td>${data.result.interceptEstimate }</td></tr></c:if>
 							<c:if test="${data.result.interceptEstimateStandardError!=null}"><tr><td>interceptEstimateStandardError </td><td>${data.result.interceptEstimateStandardError }</td></tr></c:if>
 							<c:if test="${data.result.interactionSignificance!=null}"><tr><td>interactionSignificance </td><td>${data.result.interactionSignificance }</td></tr></c:if>
 							<c:if test="${data.result.interactionEffectPValue!=null}"><tr><td>interactionEffectPValue </td><td>${data.result.interactionEffectPValue }</td></tr></c:if>
 							<c:if test="${data.result.genderFemaleKoEstimate!=null}"><tr><td>genderFemaleKoEstimate </td><td>${data.result.genderFemaleKoEstimate }</td></tr></c:if>
 							<c:if test="${data.result.genderFemaleKoStandardErrorEstimate!=null}"><tr><td>genderFemaleKoStandardErrorEstimate </td><td>${data.result.genderFemaleKoStandardErrorEstimate }</td></tr></c:if>
 							<c:if test="${data.result.genderFemaleKoPValue!=null}"><tr><td>genderFemaleKoPValue </td><td>${data.result.genderFemaleKoPValue }</td></tr></c:if>
 							<c:if test="${data.result.genderMaleKoEstimate!=null}"><tr><td>genderMaleKoEstimate </td><td>${data.result.genderMaleKoEstimate }</td></tr></c:if>
 							<c:if test="${data.result.genderMaleKoStandardErrorEstimate!=null}"><tr><td>genderMaleKoStandardErrorEstimate </td><td>${data.result.genderMaleKoStandardErrorEstimate }</td></tr></c:if>
 							<c:if test="${data.result.genderMaleKoPValue!=null}"><tr><td>genderMaleKoPValue </td><td>${data.result.genderMaleKoPValue }</td></tr></c:if>
 							</table>
 	 					</c:if>
						
 				</div>
 				</div>
 				
 				<%-- </c:if> --%>
 				
 		
 				</c:if>
 </c:if>
 				
