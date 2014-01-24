	<c:if test="${scatterChartAndData!=null}">
 	
 	
  					<div id="chart${experimentNumber}"
									style="min-width: 400px; height: 400px; margin: 0 auto">
								</div>
   								<!-- <script type="text/javascript">
   
   								$(function () {
   								   ${unidimensionalChartDataSet.chartData.chart}
								</script> -->
								
								${timeSeriesChartsAndTable.chart}
		<script type="text/javascript">
			${scatterChartAndData.chart}
		</script>
								
	</c:if>
	
	
	
	
	
	