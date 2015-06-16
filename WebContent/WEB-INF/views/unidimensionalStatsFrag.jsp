<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<c:if test="${fn:length(unidimensionalChartDataSet.statsObjects)>1}">
<c:set var="data" value="${unidimensionalChartDataSet.statsObjects[1]}"></c:set>
${data.mpTermId}
</c:if>
<!-- unidimensional here -->
<c:if test="${unidimensionalChartDataSet!=null}">
		
	<p class = "chartTitle">${unidimensionalChartDataSet.title}</p>
	<p class = "chartSubtitle">${unidimensionalChartDataSet.subtitle}</p>
	
	<br/>
	
	<div id="chart${experimentNumber}" class="onethird"></div>
	<div id="scatter${experimentNumber}" class="twothird"></div>
	<div class="clear"></div>
	<script type="text/javascript">
				${scatterChartAndData.chart};
  			$(function () {${unidimensionalChartDataSet.chartData.chart}
	</script>
	<br/> <br/>
</c:if>

<jsp:include page="unidimensionalTables.jsp"></jsp:include>