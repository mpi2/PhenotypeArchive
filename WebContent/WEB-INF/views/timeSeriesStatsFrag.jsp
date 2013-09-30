<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>


<!-- time series charts here-->

<c:forEach var="timeChart" items="${timeSeriesChartsAndTables}" varStatus="timeLoop">
 <%-- ${loop.count  % 2} --%>
	<c:if test = "${timeLoop.count  % 2!=0}">
		<div class="row-fluid dataset">  
		 <div class="row-fluid ">
		 		<div class="container span6"><h4><!--  style="background-color:lightgrey;"  -->Allele -  <t:formatAllele>${timeChart.expBiologicalModel.alleles[0].symbol}</t:formatAllele> <span class="graphGenBackground">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;background -  ${timeSeriesChartsAndTables[timeLoop.index].expBiologicalModel.geneticBackground}</span></h4>
		 		</div>
		 		 <c:if test="${fn:length(timeSeriesChartsAndTables) > (timeLoop.index+1)}">
		 		 <div class="container span6"><h4><!--  style="background-color:lightgrey;"  -->Allele -  <t:formatAllele>${timeSeriesChartsAndTables[timeLoop.index+1].expBiologicalModel.alleles[0].symbol}</t:formatAllele> <span class="graphGenBackground">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;background -  ${timeSeriesChartsAndTables[timeLoop.index+1].expBiologicalModel.geneticBackground}</span></h4>
		 		</div>
		 		</c:if>
 		</div>
 		<div class="row-fluid">
 	</c:if>
  				 <div class="container span6">
								<div id="timeChart${timeLoop.count}"
									style="min-width: 400px; height: 450px; margin: 0 auto">
								</div>
								<script type="text/javascript">
								${timeChart.chart}
								</script>
					</div>
<c:if test = "${(timeLoop.count % 2==0) || timeLoop.count eq fn:length(timeSeriesChartsAndTables)}">
		 </div>
		 </div>
</c:if>
</c:forEach>