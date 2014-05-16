<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:genericpage>

	<jsp:attribute name="bodyTag"><body  class="chartpage no-sidebars small-header"></jsp:attribute>
	<jsp:attribute name="header">
		<script type='text/javascript'  src="http://code.highcharts.com/modules/heatmap.js"></script><!-- this will need to be local as https causes highcharts to fail otherwise as insecure content error-->
  </jsp:attribute>
	
	<jsp:body>
		Heat Map as table here!!
                <table>
                <c:forEach items="${geneRows}" var="row">
                <tr><td>${row.accession}</td>
                    
                    <c:forEach items="${parameters}" var="paramKey">
                        
                        <td>${paramHeader} ${row.paramToCellMap[paramKey].mpTermName}</td>
                    </c:forEach>
                </tr>
                </c:forEach>
                
                </table>
                
	</jsp:body>
	
</t:genericpage>
