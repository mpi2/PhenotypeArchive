<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

 	<!-- PieChart here -->
 	<c:if test="${viabilityDTO!=null}">
 
            		<div id="totalChart">
								</div>
   								<script type="text/javascript">
		${viabilityDTO.totalChart}
	</script>
								<div class="section half">
								<%-- <a href="${acc}?${fn:replace(pageContext.request.queryString, 
                                'UNIDIMENSIONAL_BOX_PLOT', 'UNIDIMENSIONAL_SCATTER_PLOT')}">Graph by date</a> --%>
                                </div><div class="section half"></div>
	</c:if>
	
	<!-- PieChart here -->
<%--  	${viabilityDTO.maleChart} --%>
 	<c:if test="${viabilityDTO!=null}">
            		<div id="maleChart">
								</div>
   								<script type="text/javascript">
		${viabilityDTO.maleChart}
	</script>
								<div class="section half">
								<%-- <a href="${acc}?${fn:replace(pageContext.request.queryString, 
                                'UNIDIMENSIONAL_BOX_PLOT', 'UNIDIMENSIONAL_SCATTER_PLOT')}">Graph by date</a> --%>
                                </div><div class="section half"></div>
	</c:if>
	
	<!-- PieChart here -->
 	<%-- ${viabilityDTO.femaleChart} --%>
 	<c:if test="${viabilityDTO!=null}">
            		<div id="femaleChart">
								</div>
   								<script type="text/javascript">
		${viabilityDTO.femaleChart}
	</script>
								<div class="section half">
								<%-- <a href="${acc}?${fn:replace(pageContext.request.queryString, 
                                'UNIDIMENSIONAL_BOX_PLOT', 'UNIDIMENSIONAL_SCATTER_PLOT')}">Graph by date</a> --%>
                                </div><div class="section half"></div>
	</c:if>
	