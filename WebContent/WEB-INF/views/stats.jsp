<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:genericpage>
	<jsp:attribute name="title"> <c:forEach var="parameter" items="${parameters}" varStatus="loop">
  ${parameter.name}
    <c:if test="${ not loop.last}">,</c:if>
	</c:forEach>Statistics for ${gene.symbol} </jsp:attribute>

	<jsp:attribute name="breadcrumb">&nbsp;&raquo; Statistics &raquo; <a href='${baseUrl}/stats/genes/${gene.id.accession}'>${gene.symbol}</a></jsp:attribute>

	<jsp:attribute name="header">

	<script type="text/javascript">
		var gene_id = '${acc}';
	</script>

	<!--    extra header stuff goes here such as extra page specific javascript -->
	<!-- highcharts -->
<script type='text/javascript' src='${baseUrl}/js/charts/highcharts.js'></script>
 <script type='text/javascript' src='${baseUrl}/js/charts/highcharts-more.js'></script> 
<script src="${baseUrl}/js/charts/exporting.js"></script>


	

<style>
</style>

    </jsp:attribute>

	<jsp:attribute name="footer">
	<script>
		$(document).ready(function(){						
			// bubble popup for brief panel documentation
			$.fn.qTip({
				'pageName': 'stats',
				'textAlign': 'left',
				'tip': 'topRight'
			});
			
		});
	</script>
	</jsp:attribute>

	<jsp:body>

	<div class='documentation'>
   		<a href='' class='generalPanel'><img src="${baseUrl}/img/info_20x20.png" /></a>
	</div>	
        

	<div class='topic'>Gene: ${gene.symbol} Parameters: 
	 <c:forEach var="parameter" items="${parameters}" varStatus="loop">
  ${parameter.name}
    <c:if test="${ not loop.last}">,</c:if>
	</c:forEach>
	</div>
	
		<c:if test="${statsError}">
					<div class="alert alert-error">
						<strong>Error:</strong> An issue occurred processing the statistics for this page - results on this page maybe incorrect.
					</div>
		</c:if>
		<c:if test="${noData}">
					<div class="alert alert-error">
						<strong>We don't appear to have any data for this query please try the europhenome graph link instead</strong>
					</div>
		</c:if>
	



<jsp:include page="timeSeriesStatsFrag.jsp"/>

<jsp:include page="categoricalStatsFrag.jsp"/>

<jsp:include page="unidimensionalStatsFrag.jsp"/>


		
 




    </jsp:body>
</t:genericpage>
