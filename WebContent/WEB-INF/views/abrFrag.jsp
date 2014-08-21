<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>


<!-- categorical here -->
				
 	<c:if test="${abrChart!=null}">
            
  					<div id="chartABR">
								</div>
   								<script type="text/javascript">

   							 	$(document).ready(
   									function() {
   								   ${abrChart};
   								})
								</script>
	</c:if>
