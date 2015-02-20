<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>


	<link type='text/css' rel='stylesheet' href='${baseUrl}/css/geneHeatmapStyling.css'  />
	<script type='text/javascript'  src="http://code.highcharts.com/modules/heatmap.js"></script><!-- this will need to be local as https causes highcharts to fail otherwise as insecure content error-->
 	<script>
 		$(function() {
	    var header_height = 0;
	    $('table th span').each(function() {
	        if ($(this).outerWidth() > header_height) header_height = $(this).outerWidth();
	        $(this).width($(this).height()* 0.05);
	    });
	    $('table th').height(header_height);
		});
 	</script>
  	
 	<table>
 	
   <thead>
     <tr> 
        <th class="gene-heatmap-header"><span>Gene</span></th>
        <c:forEach var="xAxisBean" items="${xAxisBeans}" >
          	<th title="${xAxisBean.name}"><span>${xAxisBean.name}</span></th>
        </c:forEach>
     </tr>
   </thead>
   
	 <c:forEach items="${geneRows}" var="row">
     	<tr>
     		<td><a href="${baseUrl}/genes/${row.accession}">${row.symbol}</a></td>
        <c:forEach var="xAxisBean" items="${xAxisBeans}" > 
          <td  
           	<c:choose>
           	 	<c:when test="${row.XAxisToCellMap[xAxisBean.id].status eq 'Significant call'}">style="background-color:rgb(191, 75, 50)"</c:when>
             	<c:when test="${row.XAxisToCellMap[xAxisBean.id].status eq 'Analysis failed'}">style="background-color: rgb(0, 0, 0)"</c:when>
             	<c:when test="${row.XAxisToCellMap[xAxisBean.id].status eq 'Data analysed, no significant call'}">style="background-color: rgb(247, 157, 70)"</c:when>
           		<c:when test="${row.XAxisToCellMap[xAxisBean.id].status eq null }">style="background-color: rgb(119, 119, 119)"</c:when>
            </c:choose>
            title="${xAxisBean.name}"></td>
        </c:forEach>
      </tr>
    </c:forEach>
    
    
    
  </table>
                

