<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>


	<link type='text/css' rel='stylesheet' href='${baseUrl}/css/geneHeatmapStyling.css'  />
		<script type='text/javascript'  src="http://code.highcharts.com/modules/heatmap.js"></script><!-- this will need to be local as https causes highcharts to fail otherwise as insecure content error-->
 <script>$(function() {
	    var header_height = 0;
	    $('table th span').each(function() {
	        if ($(this).outerWidth() > header_height) header_height = $(this).outerWidth();
	        //console.log('setting this width to '+$(this).height());
	        $(this).width($(this).height()* 0.05);
	    });

	    $('table th').height(header_height);
	    
	});
 </script>
 
                <table>
                <thead>
                <tr> 
                <th class="gene-heatmap-header"><span>Gene</span></th>
                <th><span>Mice Produced</span></th>
                <%-- <th><span>Primary Phenotype</span></th> --%>
                	<c:forEach items="${xAxisBeans}" var="xAxisBean">
                		<th title="${xAxisBean.name}"><span>${xAxisBean.name}</span></th>
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
                         <td  
                         <c:choose>
                         	<c:when test="${row.XAxisToCellMap[xAxisBean.id].status eq 'Data Available'}">style="background-color:rgb(191, 75, 50)"</c:when>
                         	<c:when test="${row.XAxisToCellMap[xAxisBean.id].status eq 'No Data Available'}">style="background-color: rgb(0, 0, 0)"</c:when>
                         	<c:when test="${row.XAxisToCellMap[xAxisBean.id].status eq 'No MP'}">style="background-color: rgb(119, 119, 119)"</c:when>
                         </c:choose>
                         
                          title="${xAxisBean.name}">${row.XAxisToCellMap[xAxisBean.id].status}</td>
                     </c:forEach>
                </tr>
                </c:forEach>
                
                </table>
                

