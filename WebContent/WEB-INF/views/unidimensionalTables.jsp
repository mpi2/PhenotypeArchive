<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<c:if test="${unidimensionalChartDataSet!=null}">

	<c:if test="${fn:length(unidimensionalChartDataSet.statsObjects)>1}">
		<c:set var="data" value="${unidimensionalChartDataSet.statsObjects[1]}"></c:set>


		<%-- Display result of a mixed model calculation --%>

		<c:if test="${data.result.statisticalMethod!=null and  data.result.statisticalMethod!='Wilcoxon rank sum test with continuity correction'}">
		<c:if test="${data.result.blupsTest!=null or data.result.interceptEstimate!=null or data.result.varianceSignificance!=null}">
			<table id="globalTest">
				<thead>
					<tr>
						<th width="25%"><strong>P Value</strong></th>
						<th><strong>Classification</strong></th>
					</tr>
				</thead>
				<tbody>
					<tr class="toggle_table_covariate_details">
						<td class="globalTestValue"><strong> <t:formatScientific>${data.result.nullTestSignificance}</t:formatScientific></strong></td>
						<td><strong> ${data.result.significanceClassification.text}</strong><!-- <br/><small>Click for more information <i class="fa" id="toggle_indicator"></i></small> -->
							<table id="table_covariate_details">
								<c:choose>
									<c:when
										test="${data.result.significanceClassification.text == 'Both genders equally' || data.result.significanceClassification.text == 'No significant change'  || data.result.significanceClassification.text == 'Can not differentiate genders' }">
										<thead>
											<tr>
												<th>Genotype effect P Value</th>
												<th>Effect size</th>
												<th>Standard Error</th>
											</tr>
										</thead>
										<tbody>
											<tr>
												<td class="pvalue"><t:formatScientific>${data.result.genotypeEffectPValue }</t:formatScientific></td>
												<td class="effect"><t:formatScientific>${data.result.genotypeParameterEstimate}</t:formatScientific></td>
												<td>
													<c:if test="${data.result.genotypeStandardErrorEstimate!=null}">
														&#177;
													</c:if>
												<t:formatScientific>${data.result.genotypeStandardErrorEstimate}</t:formatScientific></td>
											</tr>
									</c:when>
									<c:when
										test="${data.result.significanceClassification.text == 'Female only' || data.result.significanceClassification.text == 'Male only'  || data.result.significanceClassification.text == 'Different effect size, females greater' || data.result.significanceClassification.text == 'Different effect size, males greater' || data.result.significanceClassification.text == 'Female and male different directions'}">
										<thead>
											<tr>
												<th>Sex</th>
												<th>Sex*Genotype P Value</th>
												<th>Effect size</th>
												<th>Standard Error</th>
											</tr>
										</thead>
										<tbody>
											<tr>
												<td>Female</td>
												<c:if test="${data.result.genderFemaleKoPValue!=null}">
													<td class="pvalue"><t:formatScientific>${data.result.genderFemaleKoPValue }</t:formatScientific></td>
												</c:if>
												<td class="effect"><t:formatScientific>${data.result.genderFemaleKoEstimate}</t:formatScientific></td>
												<c:if
													test="${data.result.genderFemaleKoStandardErrorEstimate!=null}">
													<td>&#177;<t:formatScientific>${data.result.genderFemaleKoStandardErrorEstimate }</t:formatScientific></td>
												</c:if>
											</tr>
											<tr>
												<td>Male</td>
												<c:if test="${data.result.genderMaleKoPValue!=null}">
													<td class="pvalue"><t:formatScientific>${data.result.genderMaleKoPValue }</t:formatScientific></td>
												</c:if>
												<td class="effect"><t:formatScientific>${data.result.genderMaleKoEstimate}</t:formatScientific></td>
												<c:if
													test="${data.result.genderMaleKoStandardErrorEstimate!=null}">
													<td>&#177;<t:formatScientific>${data.result.genderMaleKoStandardErrorEstimate }</t:formatScientific></td>
												</c:if>
											</tr>
										</c:when>
									</c:choose>
								</tbody>
							</table>
						</td>
					</tr>
				</tbody>
			</table>
		</c:if>
	</c:if>
	<table id="continuousTable">
		<thead>
			<tr>
				<th>Control/Hom/Het</th>
				<th>Mean</th>
				<th>SD</th>
				<th>Count</th>

			</tr>
		</thead>
		<tbody>


			<c:forEach var="statsObject"
				items="${unidimensionalChartDataSet.statsObjects}">
				<tr>
					<td><c:choose>
							<c:when test="${statsObject.sexType eq 'female'}">
                                Female
                            </c:when>
							<c:when test="${statsObject.sexType eq 'male'}">
                                Male
                            </c:when>
						</c:choose> <c:choose>
							<c:when test="${statsObject.line =='Control' }">
                                Control
                            </c:when>
							<c:when test="${statsObject.line !='Control' }">
                                ${statsObject.zygosity}
                            </c:when>
						</c:choose></td>
					<td>${statsObject.mean}</td>
					<td>${statsObject.sd}</td>
					<c:if test="${statsObject.sexType eq 'female'}">
						<td>${statsObject.sampleSize}</td>
					</c:if>
					<c:if test="${statsObject.sexType eq 'male'}">
						<td>${statsObject.sampleSize}</td>
					</c:if>

				</tr>
			</c:forEach>
		</tbody>
	</table>


	<%-- some *** result should be here: ${unidimensionalChartDataSet.statsObjects[1].result} --%>
	<c:if test="${fn:length(unidimensionalChartDataSet.statsObjects)>1}">

		<c:set var="data" value="${unidimensionalChartDataSet.statsObjects[1]}"></c:set>

		<c:if test="${data.result.blupsTest!=null or data.result.interceptEstimate!=null or data.result.varianceSignificance!=null}">

			<p>
				<a><i class="fa" id="toggle_table_button${experimentNumber}"> More Statistics</i></a>
			</p>

			<div id="toggle_table${experimentNumber}">

				<c:if test="${data.result.colonyId!=null}"><!-- Colony Id: ${data.result.colonyId } --></c:if>
				<table>
					<tr>
						<th>Model Fitting Estimates</th>
						<th>Value</th>
					</tr>
					<%-- <c:if test="${data.result.experimentalZygosity!=null}"><tr><td>Experimental Zygosity</td><td>${data.result.experimentalZygosity}</td></tr></c:if> --%>
					<%-- <td>${data.result.mixedModel}</td> --%>
					<%-- <c:if test="${data.result.colonyId!=null}"><tr><td>Colony Id</td><td>${data.result.colonyId }</td></tr></c:if> --%>
					<%-- <c:if test="${data.result.dependantVariable!=null}"><tr><td>Dependant Variable</td><td>${data.result.dependantVariable}</td></tr></c:if> --%>
					<c:if test="${data.result.statisticalMethod!=null}">
						<tr>
							<td>Statistical method</td>
							<td>${data.result.statisticalMethod}</td>
						</tr>
					</c:if>
					<c:if test="${data.result.batchSignificance!=null}">
						<tr>
							<td>Batch Significance</td>
							<td>${data.result.batchSignificance }</td>
						</tr>
					</c:if>
					<c:if test="${data.result.varianceSignificance!=null}">
						<tr>
							<td>Variance Significance</td>
							<td>${data.result.varianceSignificance }</td>
						</tr>
					</c:if>
					<c:if test="${data.result.interactionEffectPValue!=null}">
						<tr>
							<td>Interaction Effect P Value</td>
							<td><t:formatScientific>${data.result.interactionEffectPValue }</t:formatScientific></td>
						</tr>
					</c:if>
					<%-- <c:if test="${data.result.nullTestSignificance !=null}"><tr><td>Null Test Significance</td><td>${data.result.nullTestSignificance }</td></tr></c:if> --%>
					<c:if test="${data.result.genotypeParameterEstimate!=null}">
						<tr>
							<td>Genotype Parameter Estimate</td>
							<td><t:formatScientific>${data.result.genotypeParameterEstimate }</t:formatScientific></td>
						</tr>
					</c:if>
					<c:if test="${data.result.genotypeStandardErrorEstimate!=null}">
						<tr>
							<td>Genotype Standard Error Estimate</td>
							<td><t:formatScientific>${data.result.genotypeStandardErrorEstimate }</t:formatScientific></td>
						</tr>
					</c:if>
					<c:if test="${data.result.genotypeEffectPValue!=null}">
						<tr>
							<td>Genotype Effect P Value</td>
							<td><t:formatScientific>${data.result.genotypeEffectPValue}</t:formatScientific></td>
						</tr>
					</c:if>
					<c:if test="${data.result.genderParameterEstimate!=null}">
						<tr>
							<td>Gender Parameter Estimate</td>
							<td><t:formatScientific>${data.result.genderParameterEstimate }</t:formatScientific></td>
						</tr>
					</c:if>
					<c:if test="${data.result.genderStandardErrorEstimate!=null}">
						<tr>
							<td>Gender Standard Error Estimate</td>
							<td><t:formatScientific>${data.result.genderStandardErrorEstimate }</t:formatScientific></td>
						</tr>
					</c:if>
					<c:if test="${data.result.genderEffectPValue!=null}">
						<tr>
							<td>Gender Effect P Value</td>
							<td><t:formatScientific>${data.result.genderEffectPValue}</t:formatScientific></td>
						</tr>
					</c:if>
					<c:if test="${data.result.interceptEstimate!=null}">
						<tr>
							<td>Intercept Estimate</td>
							<td><t:formatScientific>${data.result.interceptEstimate }</t:formatScientific></td>
						</tr>
					</c:if>
					<c:if test="${data.result.interceptEstimateStandardError!=null}">
						<tr>
							<td>Intercept Estimate Standard Error</td>
							<td><t:formatScientific>${data.result.interceptEstimateStandardError }</t:formatScientific></td>
						</tr>
					</c:if>
					<c:if test="${data.result.genderMaleKoPValue!=null}">
						<tr>
							<td>Gender Male KO P Value</td>
							<td><t:formatScientific>${data.result.genderMaleKoPValue }</t:formatScientific></td>
						</tr>
					</c:if>
					<!-- 10-15 -->
					<c:if test="${data.result.weightParameterEstimate!=null}">
						<tr>
							<td>Weight Parameter Estimate</td>
							<td><t:formatScientific>${data.result.weightParameterEstimate }</t:formatScientific></td>
						</tr>
					</c:if>
					<c:if test="${data.result.weightStandardErrorEstimate!=null}">
						<tr>
							<td>Weight Standard Error Estimate</td>
							<td><t:formatScientific>${data.result.weightStandardErrorEstimate }</t:formatScientific></td>
						</tr>
					</c:if>
					<c:if test="${data.result.weightEffectPValue!=null}">
						<tr>
							<td>Weight Effect P Value</td>
							<td><t:formatScientific>${data.result.weightEffectPValue }</t:formatScientific></td>
						</tr>
					</c:if>
					<%-- <c:if test="${data.result.gp1Genotype!=null}"><tr><td>Gp 1 Genotype</td><td>${data.result.gp1Genotype }</td></tr></c:if> --%>
					<%-- <c:if test="${data.result.gp1ResidualsNormalityTest!=null}"><tr><td>Gp 1 Residuals Normality Test </td><td>${data.result.gp1ResidualsNormalityTest }</td></tr></c:if>This one always fails so I wouldn't include due to large number of readings.  If you want to keep add WT residuals then you an lose row above - NC --%>
					<%-- <c:if test="${data.result.gp2Genotype!=null}"><tr><td>Gp 2 Genotype</td><td>${data.result.gp2Genotype }</td></tr></c:if> --%>
					<c:if test="${data.result.gp2ResidualsNormalityTest!=null}">
						<tr>
							<td>KO Residuals Normality Tests</td>
							<td><t:formatScientific>${data.result.gp2ResidualsNormalityTest }</t:formatScientific></td>
						</tr>
					</c:if>
					<!-- relabel as KO residuals normality tests -->
					<c:if test="${data.result.blupsTest!=null}">
						<tr>
							<td>Blups Test</td>
							<td><t:formatScientific>${data.result.blupsTest }</t:formatScientific></td>
						</tr>
					</c:if>
					<c:if test="${data.result.rotatedResidualsNormalityTest !=null}">
						<tr>
							<td>Rotated Residuals Normality Test</td>
							<td><t:formatScientific>${data.result.rotatedResidualsNormalityTest }</t:formatScientific></td>
						</tr>
					</c:if>
					<%-- <c:if test="${data.result.interactionSignificance!=null}"><tr><td>Interaction Significance </td><td>${data.result.interactionSignificance }</td></tr></c:if> do you need as next row gives detail>? - NK --%>
					<%-- <c:if test="${data.result.genderFemaleKoEstimate!=null}"><tr><td>Gender Female KO Estimate </td><td>${data.result.genderFemaleKoEstimate }</td></tr></c:if> --%>
					<%-- <c:if test="${data.result.genderMaleKoEstimate!=null}"><tr><td>Gender Male KO Estimate </td><td>${data.result.genderMaleKoEstimate }</td></tr></c:if> --%>
				</table>

			</div>
		</c:if>
	</c:if>
	</c:if>




		<%-- Display result of a wilcoxon calculation --%>

		<c:if test="${data.result.statisticalMethod!=null and data.result.statisticalMethod=='Wilcoxon rank sum test with continuity correction'}">
			<table id="globalTest">
				<thead>
					<tr>
						<th>Sex</th>
						<th>P Value</th>
						<th>Effect size</th>
					</tr>
				</thead>
				<tbody>
					<tr class="toggle_table_covariate_details">
						<td>Female</td>
						<c:if test="${data.result.genderFemaleKoPValue!=null}">
							<td class="pvalue"><t:formatScientific>${data.result.genderFemaleKoPValue }</t:formatScientific></td>
						</c:if>
						<td class="effect"><t:formatScientific>${data.result.genderFemaleKoEstimate}</t:formatScientific></td>
					</tr>
					<tr>
						<td>Male</td>
						<c:if test="${data.result.genderMaleKoPValue!=null}">
							<td class="pvalue"><t:formatScientific>${data.result.genderMaleKoPValue }</t:formatScientific></td>
						</c:if>
						<td class="effect"><t:formatScientific>${data.result.genderMaleKoEstimate}</t:formatScientific></td>
					</tr>
				</tbody>
			</table>
	<table id="continuousTable">
		<thead>
			<tr>
				<th>Control/Hom/Het</th>
				<th>Mean</th>
				<th>SD</th>
				<th>Count</th>
			</tr>
		</thead>
		<tbody>


			<c:forEach var="statsObject"
				items="${unidimensionalChartDataSet.statsObjects}">
				<tr>
					<td><c:choose>
							<c:when test="${statsObject.sexType eq 'female'}">
                                Female
                            </c:when>
							<c:when test="${statsObject.sexType eq 'male'}">
                                Male
                            </c:when>
						</c:choose> <c:choose>
							<c:when test="${statsObject.line =='Control' }">
                                Control
                            </c:when>
							<c:when test="${statsObject.line !='Control' }">
                                ${statsObject.zygosity}
                            </c:when>
						</c:choose></td>
					<td>${statsObject.mean}</td>
					<td>${statsObject.sd}</td>
					<c:if test="${statsObject.sexType eq 'female'}">
						<td>${statsObject.sampleSize}</td>
					</c:if>
					<c:if test="${statsObject.sexType eq 'male'}">
						<td>${statsObject.sampleSize}</td>
					</c:if>

				</tr>
			</c:forEach>
		</tbody>
	</table>

	<c:if test="${fn:length(unidimensionalChartDataSet.statsObjects)>1}">

		<c:set var="data" value="${unidimensionalChartDataSet.statsObjects[1]}"></c:set>


		<div>

			<c:if test="${data.result.colonyId!=null}"><!-- Colony Id: ${data.result.colonyId } --></c:if>
			<table>
				<tr>
					<th>Model Fitting Estimates</th>
					<th>Value</th>
				</tr>
				<tr>
					<td>Statistical method</td>
					<td>${data.result.statisticalMethod}</td>
				</tr>

			</table>

		</div>
	</c:if>
	</c:if>




	<script>
		$(document).ready(
				function() {

					$("#toggle_table${experimentNumber}").hide();//hide on load
					//toggle the arrow on the link to point right as should be closed on in it
					$("#toggle_table_button${experimentNumber}").toggleClass('fa-caret-right');
					$("#toggle_table_button${experimentNumber}").click(
							function() {
								$("#toggle_table${experimentNumber}").toggle('slow');
								$("#toggle_table_button${experimentNumber}")
										.toggleClass('fa-caret-right')
										.toggleClass('fa-caret-down');//remove right and put down or vica versa
							});

					// bubble popup for brief panel documentation - added here as in stats page it doesn't work
					$.fn.qTip({
						'pageName' : 'stats',
						'tip' : 'top right',
						'corner' : 'right top'
					});
				});
	</script>

</c:if>