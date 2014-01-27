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
	
	<!-- remove the scatter flag so we go back to the original graph version -->
						<c:set var="originalUrl" value="${fn:replace(pageContext.request.queryString, 
                                '&scatter=true', '')}" />
                                
                                <a href="${acc}?${originalUrl}">Box Plot / Time Series Graphs</a>	
	</c:if>