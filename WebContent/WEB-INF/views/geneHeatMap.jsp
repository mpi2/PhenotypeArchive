<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:genericpage>

	<jsp:attribute name="bodyTag"><body  class="chartpage no-sidebars small-header"></jsp:attribute>
	<jsp:attribute name="header">
	<link type='text/css' rel='stylesheet' href='${baseUrl}/css/additionalStyling.css'  />
		<script type='text/javascript'  src="http://code.highcharts.com/modules/heatmap.js"></script><!-- this will need to be local as https causes highcharts to fail otherwise as insecure content error-->
 <script>$(function() {
    var header_height = 0;
    $('vertical_header').each(function() {
        if ($(this).outerWidth() > header_height) header_height = $(this).outerWidth();
        $(this).width($(this).height());
    });

    $('table th').height(header_height);
    
});
 </script>
 
  </jsp:attribute>
	
	<jsp:body>
                <table>
                <thead>
                <tr> 
                <th><span class="vertical_header">Gene</span></th>
                	<c:forEach items="${parameters}" var="phenoParam">
                		<th><span class="vertical_header">${phenoParam.name}</span></th>
                	</c:forEach>
                </tr>
                </thead>
                <c:forEach items="${geneRows}" var="row">
                <tr><td>${row.symbol}</td>  
                    <c:forEach items="${parameters}" var="paramKey"> 
                        <td  <c:if test="${row.paramToCellMap[paramKey.stableId].pValue!=null}">style="background-color:rgb(252,141,89)"</c:if>><%-- ${row.paramToCellMap[paramKey.stableId].mpTermName} --%>${row.paramToCellMap[paramKey.stableId].pValue}</td>
                    </c:forEach>
                </tr>
                </c:forEach>
                
                </table>
                
	</jsp:body>
	
	
	
</t:genericpage>
