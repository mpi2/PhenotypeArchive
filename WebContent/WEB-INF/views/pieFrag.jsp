<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

 	<!-- PieChart here -->
 	${viabilityDTO.pieChart}
 	<c:if test="${viabilityDTO!=null}">
            		<div id="viabilityChart">
								</div>
   								<script type="text/javascript">
		${viabilityDTO.pieChart}
	</script>
								<div class="section half">
								<%-- <a href="${acc}?${fn:replace(pageContext.request.queryString, 
                                'UNIDIMENSIONAL_BOX_PLOT', 'UNIDIMENSIONAL_SCATTER_PLOT')}">Graph by date</a> --%>
                                </div><div class="section half"></div>
	</c:if>
	<jsp:include page="unidimensionalTables.jsp"></jsp:include>