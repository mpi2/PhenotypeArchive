<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<!-- unidimensional here -->
<c:if test="${unidimensionalChartDataSet!=null}">
	<c:if
		test="${unidimensionalChartDataSet.experiment.metadataGroup!=null}"><span title="${unidimensionalChartDataSet.experiment.getMetadataHtml()}">Metadata Group - ${unidimensionalChartDataSet.experiment.metadataGroup}</span></c:if>
	<br/><br/><br/>
	
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