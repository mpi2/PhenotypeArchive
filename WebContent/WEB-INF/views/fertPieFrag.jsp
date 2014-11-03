<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<!-- PieChart here -->
calling fert chart here
<c:if test="${fertilityDTO!=null}">

                     	
            		<div id="totalChart" class="onethirdForPie ">
								</div>
   								<script type="text/javascript">
		${fertilityDTO.totalChart}
	</script>
								
</c:if>