		<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
		<c:if test="${unidimensionalChartDataSet!=null}">
		<table id="continuousTable">
		<thead><tr>
		<th>Control/Hom/Het</th>
			<th>Mean</th>
			<th>SD</th>
			<th>Count</th>
			<th>pValue</th>
			<th>Effect Size</th>
		</tr></thead>
		<tbody>									
										
										
											<c:forEach var="statsObject" items="${unidimensionalChartDataSet.statsObjects}">
												<tr>
												<td>
												<c:choose>
														<c:when test="${statsObject.sexType eq 'female'}">
															Female
														</c:when>
														<c:when test="${statsObject.sexType eq 'male'}">
															Male
														</c:when>
												</c:choose>
												<c:choose>
												<c:when test="${statsObject.line =='Control' }">
												Control
												</c:when>
												<c:when test="${statsObject.line !='Control' }">
												${statsObject.zygosity}
												</c:when>
												</c:choose>
												</td>
												<td>${statsObject.mean}</td>
												<td>${statsObject.sd}</td>
												<c:if test="${statsObject.sexType eq 'female'}">
												<td>${statsObject.sampleSize}</td>
												</c:if>
												<c:if test="${statsObject.sexType eq 'male'}">
												<td>${statsObject.sampleSize}</td>
												</c:if>
												<td>${statsObject.result.pValue}</td>
												<td>${statsObject.result.effectSize}</td>
												</tr>
												</c:forEach>
										</tbody>
										</table>
				
				
				<%-- some *** result should be here: ${unidimensionalChartDataSet.statsObjects[1].result} --%>
			<c:if test="${fn:length(unidimensionalChartDataSet.statsObjects)>1}"> 
			<c:set var="data" value="${unidimensionalChartDataSet.statsObjects[1]}"></c:set>
				<c:if test="${data.result.blupsTest!=null or data.result.interceptEstimate!=null or data.result.varianceSignificance!=null}">
				<p><a><i class="fa" id="toggle_table_button${experimentNumber}">More Statistics</i></a></p>
				<div id="toggle_table${experimentNumber}">
						<table>				
 									<c:choose>
          									<c:when test="${data.result.significanceClassification.text == 'Both genders equally' || data.result.significanceClassification.text == 'No significant change'  || data.result.significanceClassification.text == 'Can not differentiate genders' }">
          												<tr><th>Global Test</th><th>Significance/Classification</th><th>Effect</th></tr>
          												<tr><td>${data.result.nullTestSignificance}</td><td>${data.result.significanceClassification.text}</td><td>${data.result.genotypeParameterEstimate}</td></tr></c:when>
         									<c:when test="${data.result.significanceClassification.text == 'Female only' || data.result.significanceClassification.text == 'Male only'  || data.result.significanceClassification.text == 'Different size females greater' || data.result.significanceClassification.text == 'Different size males greater' || data.result.significanceClassification.text == 'Female and male different directions'}">
       													 <tr><th>Global Test</th><th>Significance/Classification</th><th>Sex</th><th>Effect</th><th>Standard Error </th><th>P Value</th></tr>
       													 <tr>
       													 <td rowspan="2">${data.result.nullTestSignificance}</td>
       													 <td rowspan="2">${data.result.significanceClassification.text}</td>
       													 <td>Female</td><td>${data.result.genderFemaleKoEstimate}</td><c:if test="${data.result.genderFemaleKoStandardErrorEstimate!=null}"><td>&#177;${data.result.genderFemaleKoStandardErrorEstimate }</td></c:if><c:if test="${data.result.genderFemaleKoPValue!=null}"><td>${data.result.genderFemaleKoPValue }</td></c:if>
       													 <tr><td> Male</td><td> ${data.result.genderMaleKoEstimate}</td><c:if test="${data.result.genderMaleKoStandardErrorEstimate!=null}"><td>&#177;${data.result.genderMaleKoStandardErrorEstimate }</td></c:if><c:if test="${data.result.genderMaleKoPValue!=null}"><td>${data.result.genderMaleKoPValue }</td></c:if>
       													 </tr>
       										</c:when>
									</c:choose>
 	 							</table>
						
						<%-- <th>mixedModel</th> --%>
						

 						<c:set var="data" value="${unidimensionalChartDataSet.statsObjects[1]}"></c:set>
 						<table>
 						<tr><th>Model Fitting Estimates</th><th>Value</th></tr>	
 							<%-- <c:if test="${data.result.colonyId!=null}"><tr><td>Colony Id</td><td>${data.result.colonyId }</td></tr></c:if> --%>
 							<%-- <c:if test="${data.result.experimentalZygosity!=null}"><tr><td>Experimental Zygosity</td><td>${data.result.experimentalZygosity}</td></tr></c:if> --%>
 							<%-- <td>${data.result.mixedModel}</td> --%>
 							<%-- <c:if test="${data.result.dependantVariable!=null}"><tr><td>Dependant Variable</td><td>${data.result.dependantVariable}</td></tr></c:if> --%>
 							<c:if test="${data.result.batchSignificance!=null}"><tr><td>Batch Significance</td><td>${data.result.batchSignificance }</td></tr></c:if>
 							<c:if test="${data.result.varianceSignificance!=null}"><tr><td>Variance Significance</td><td>${data.result.varianceSignificance }</td></tr></c:if>
 							<c:if test="${data.result.interactionEffectPValue!=null}"><tr><td>Interaction Effect P Value </td><td>${data.result.interactionEffectPValue }</td></tr></c:if>
 							<%-- <c:if test="${data.result.nullTestSignificance !=null}"><tr><td>Null Test Significance</td><td>${data.result.nullTestSignificance }</td></tr></c:if> --%>
 							<c:if test="${data.result.genotypeParameterEstimate!=null}"><tr><td>Genotype Parameter Estimate</td><td>${data.result.genotypeParameterEstimate }</td></tr></c:if>
 							<c:if test="${data.result.genotypeStandardErrorEstimate!=null}"><tr><td>Genotype Standard Error Estimate</td><td>${data.result.genotypeStandardErrorEstimate }</td></tr></c:if>
 							<c:if test="${data.result.genotypeEffectPValue!=null}"><tr><td>Genotype Effect P Value</td><td>${data.result.genotypeEffectPValue}</td></tr></c:if>
 							<c:if test="${data.result.genderParameterEstimate!=null}"><tr><td>Gender Parameter Estimate</td><td>${data.result.genderParameterEstimate }</td></tr></c:if>
 							<c:if test="${data.result.genderStandardErrorEstimate!=null}"><tr><td>Gender Standard Error Estimate</td><td>${data.result.genderStandardErrorEstimate }</td></tr></c:if>
 							<c:if test="${data.result.genderEffectPValue!=null}"><tr><td>Gender Effect P Value</td><td>${data.result.genderEffectPValue}</td></tr></c:if>
 							<c:if test="${data.result.interceptEstimate!=null}"><tr><td>Intercept Estimate </td><td>${data.result.interceptEstimate }</td></tr></c:if>
 							<c:if test="${data.result.interceptEstimateStandardError!=null}"><tr><td>Intercept Estimate Standard Error </td><td>${data.result.interceptEstimateStandardError }</td></tr></c:if>
 							<c:if test="${data.result.genderMaleKoPValue!=null}"><tr><td>Gender Male KO P Value </td><td>${data.result.genderMaleKoPValue }</td></tr></c:if>
 							<!-- 10-15 -->
 							<c:if test="${data.result.weightParameterEstimate!=null}"><tr><td>Weight Parameter Estimate </td><td>${data.result.weightParameterEstimate }</td></tr></c:if>
 							<c:if test="${data.result.weightStandardErrorEstimate!=null}"><tr><td>Weight Standard Error Estimate </td><td>${data.result.weightStandardErrorEstimate }</td></tr></c:if>
 							<c:if test="${data.result.weightEffectPValue!=null}"><tr><td>Weight Effect P Value </td><td>${data.result.weightEffectPValue }</td></tr></c:if>
 							<%-- <c:if test="${data.result.gp1Genotype!=null}"><tr><td>Gp 1 Genotype</td><td>${data.result.gp1Genotype }</td></tr></c:if> --%>
 							<%-- <c:if test="${data.result.gp1ResidualsNormalityTest!=null}"><tr><td>Gp 1 Residuals Normality Test </td><td>${data.result.gp1ResidualsNormalityTest }</td></tr></c:if>This one always fails so I wouldn't include due to large number of readings.  If you want to keep add WT residuals then you an lose row above - NC --%>
 							<%-- <c:if test="${data.result.gp2Genotype!=null}"><tr><td>Gp 2 Genotype</td><td>${data.result.gp2Genotype }</td></tr></c:if> --%>
 							<c:if test="${data.result.gp2ResidualsNormalityTest!=null}"><tr><td>KO Residuals Normality Tests</td><td>${data.result.gp2ResidualsNormalityTest }</td></tr></c:if><!-- relabel as KO residuals normality tests -->
 							<c:if test="${data.result.blupsTest!=null}"><tr><td>Blups Test </td><td>${data.result.blupsTest }</td></tr></c:if>
 							<c:if test="${data.result.rotatedResidualsNormalityTest !=null}"><tr><td>Rotated Residuals Normality Test </td><td>${data.result.rotatedResidualsNormalityTest }</td></tr></c:if>
 							<%-- <c:if test="${data.result.interactionSignificance!=null}"><tr><td>Interaction Significance </td><td>${data.result.interactionSignificance }</td></tr></c:if> do you need as next row gives detail>? - NK --%>
 							<%-- <c:if test="${data.result.genderFemaleKoEstimate!=null}"><tr><td>Gender Female KO Estimate </td><td>${data.result.genderFemaleKoEstimate }</td></tr></c:if> --%>
 							<%-- <c:if test="${data.result.genderMaleKoEstimate!=null}"><tr><td>Gender Male KO Estimate </td><td>${data.result.genderMaleKoEstimate }</td></tr></c:if> --%>
 							</table>
 	 					
 				</div>
 				</c:if>	
 		</c:if>	
 
 <script>
 	$(document).ready(
		function() {
 	
			console.log('document ready');
			$( "#toggle_table${experimentNumber}" ).hide();//hide on load
			$( "#toggle_table_button${experimentNumber}" ).toggleClass('fa-caret-right');//toggle the arrow on the link to point right as should be closed on init
			$( "#toggle_table_button${experimentNumber}" ).click(function() {
				console.log("click fired");
										  $( "#toggle_table${experimentNumber}" ).toggle('slow');
										  $( "#toggle_table_button${experimentNumber}" ).toggleClass('fa-caret-right').toggleClass('fa-caret-down');//remove right and put down or vica versa
										});
			
			// bubble popup for brief panel documentation - added here as in stats page it doesn't work
		 	$.fn.qTip({
						'pageName': 'stats',					
						'tip': 'top right',
						'corner' : 'right top'
			}); 
 					});
</script>

</c:if>