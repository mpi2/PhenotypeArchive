<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:genericpage>

	<jsp:attribute name="bodyTag"><body  class="chartpage no-sidebars small-header"></jsp:attribute>
	<jsp:attribute name="header">
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
 
  </jsp:attribute>
	
	<jsp:body>
                <table>
                <thead>
                <tr> 
                <th><span>Gene</span></th>
                <th><span>Mice Produced</span></th>
                <%-- <th><span>Primary Phenotype</span></th> --%>
                	<c:forEach items="${parameters}" var="phenoParam">
                		<th><span>${phenoParam.name}</span></th>
                	</c:forEach>
                </tr>
                </thead>
                <c:forEach items="${geneRows}" var="row">
                <tr><td>${row.symbol}</td>
               
                	<c:if test="${row.miceProduced eq 'In Progress'}"><td style="background-color:rgb(3, 77, 105)">${row.miceProduced}</td></c:if>
                	<c:if test="${row.miceProduced eq 'Yes'}"><td style="background-color:rgb(61, 167, 208)">${row.miceProduced}</td></c:if>
                	<c:if test="${row.miceProduced eq 'No'}"><td style="background-color:rgb(247, 157, 70)">${row.miceProduced}</td></c:if>
               
                	<%-- <c:if test="${row.primaryPhenotype}"><td style="background-color:rgb(61, 167, 208)">Y</td></c:if>
               		<c:if test="${!row.primaryPhenotype}"><td style="background-color:rgb(3, 77, 105)">N</td></c:if>
                  --%>
                    <c:forEach items="${parameters}" var="paramKey"> 
                        <td  <c:if test="${row.paramToCellMap[paramKey].pValue!=null}">style="background-color:rgb(247, 157, 70)"</c:if><c:if test="${row.paramToCellMap[paramKey].pValue==null}">style="background-color: rgb(119, 119, 119)"</c:if>><%-- ${row.paramToCellMap[paramKey.stableId].mpTermName} --%>${row.paramToCellMap[paramKey].pValue}</td>
                    </c:forEach>
                </tr>
                </c:forEach>
                
                </table>
                
	</jsp:body>
	
	
	
</t:genericpage>
