<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

 <!-- unidimensional here -->
 	<c:if test="${unidimensionalChartDataSet!=null}">
            <c:if test="${unidimensionalChartDataSet.experiment.metadataGroup!=null}">Metadata Group - ${unidimensionalChartDataSet.experiment.metadataGroup}</c:if>
  					<div id="chart${experimentNumber}">
								</div>
   								<script type="text/javascript">
   
   								$(function () {
   								   ${unidimensionalChartDataSet.chartData.chart}
								</script>
								<div class="section half"><a href="${acc}?${pageContext.request.queryString}&scatter=1">Graph by date</a></div><div class="section half"></div>
	</c:if>
		<jsp:include page="unidimensionalTables.jsp"></jsp:include>