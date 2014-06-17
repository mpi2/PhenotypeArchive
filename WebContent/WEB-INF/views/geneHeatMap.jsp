<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>


	<link type='text/css' rel='stylesheet' href='${baseUrl}/css/geneHeatmapStyling.css'  />
		<script type='text/javascript'  src="http://code.highcharts.com/modules/heatmap.js"></script><!-- this will need to be local as https causes highcharts to fail otherwise as insecure content error-->
 <script>$(function() {
	    var header_height = 0;
	    $('table th span').each(function() {
	        if ($(this).outerWidth() > header_height) header_height = $(this).outerWidth();
	        $(this).width($(this).height());
	    });

	    $('table th').height(header_height);
	    
	});
 </script>
 
                <table>
                <thead>
                <tr> 
                <th><span>Gene</span></th>
                <th><span>Mice Produced</span></th>
                <%-- <th><span>Primary Phenotype</span></th> --%>
                	<c:forEach items="${xAxisBeans}" var="xAxisBean">
                		<th><span>${xAxisBean.name}</span></th>
                	</c:forEach>
                </tr>
                </thead>
                <c:forEach items="${geneRows}" var="row">
                <tr><td>${row.symbol}</td>
               
                	<td style="background-color:rgb(3, 77, 105)">${row.miceProduced}</td>
                	<%-- <c:if test="${row.primaryPhenotype}"><td style="background-color:rgb(61, 167, 208)">Y</td></c:if>
               		<c:if test="${!row.primaryPhenotype}"><td style="background-color:rgb(3, 77, 105)">N</td></c:if>
                  --%>
                    <c:forEach items="${xAxisBeans}" var="xAxisBean"> 
                   <%--  <td>${row.XAxisToCellMap[xAxisBean.id].label}</td> --%>
                         <td  <c:if test="${row.XAxisToCellMap[xAxisBean.id].label!=''}">style="background-color:rgb(191, 75, 50)"</c:if><c:if test="${row.XAxisToCellMap[xAxisBean.id].label==''}">style="background-color: rgb(119, 119, 119)"</c:if> title="${xAxisBean.name}"><%-- ${row.XAxisToCellMap[xAxisBean.id].label} --%></td>
                     </c:forEach>
                </tr>
                </c:forEach>
                
                </table>
                

