<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:genericpage>

	<jsp:attribute name="bodyTag"><body  class="chartpage no-sidebars small-header"></jsp:attribute>
	<jsp:attribute name="header">
		<script type='text/javascript'  src="http://code.highcharts.com/modules/heatmap.js"></script>
  </jsp:attribute>
	
	<jsp:body>
		<div id="heatmapContainer1" style="min-width: 300px; max-width: 800px; height: 400px; margin: 1em auto"> </div>
		<script type="text/javascript">
					${heatmapCode}
		</script>	
	</jsp:body>
	
</t:genericpage>