<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

	<c:if test="${scatterChartAndData!=null}">
	
	<!-- scatter chart here -->
  					<div id="chart${experimentNumber}">
								</div>
								${timeSeriesChartsAndTable.chart}
		<script type="text/javascript">
			${scatterChartAndData.chart}
		</script>	
                                <div class="section half"><a id="goBack" >Box Plot / Time Series Graphs</a></div>	
                                <div class="section">
                               
	</c:if>
	
	<jsp:include page="unidimensionalTables.jsp"></jsp:include>
	
	 <script>
	$(document)
			.ready(
					function() {
						
						
						//go back to original graphs functionality here
						function goBack()
						  {
						  window.history.back()
						  }
						
						$('#goBack').click(function() {
							  goBack();
						});
						
				$.fn.qTip({
							'pageName': 'stats',		
							'tip': 'top right',
							'corner' : 'right top'
						});
					});
</script>
	