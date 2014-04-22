<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<!-- time series charts here-->
<c:if test="${timeSeriesChartsAndTable.chart!=null}">

		<div id="timechart${experimentNumber}"> </div>
		
	 	<a href="${acc}?${pageContext.request.queryString}&scatter=1">Graph by date</a>
	 	
		<script type="text/javascript">
					${timeSeriesChartsAndTable.chart}
		</script>
		<script>
 	$(document).ready(
		function() {
			
			// bubble popup for brief panel documentation - added here as in stats page it doesn't work
		 	$.fn.qTip({
						'pageName': 'stats',					
						'tip': 'top right',
						'corner' : 'right top'
			}); 
 					});
</script>
</c:if>
	