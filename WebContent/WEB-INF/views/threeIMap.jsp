<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>


	<!-- link type='text/css' rel='stylesheet' href='${baseUrl}/css/geneHeatmapStyling.css'  /-->
	<script type='text/javascript'  src="http://code.highcharts.com/modules/heatmap.js"></script><!-- this will need to be local as https causes highcharts to fail otherwise as insecure content error-->
  	
 	<table class="hitMap">
 	
   <thead>
     <tr> 
        <th class="gene-heatmap-header-horizontal"><span>Gene</span></th>
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
           	 	<c:when test="${row.XAxisToCellMap[xAxisBean.id].status eq 'Deviance Significant'}">style="background-color:rgb(191, 75, 50)"</c:when>
             	<c:when test="${row.XAxisToCellMap[xAxisBean.id].status eq 'Could not analyse'}">style="background-color: rgb(119, 119, 119)"</c:when>
             	<c:when test="${row.XAxisToCellMap[xAxisBean.id].status eq 'Data analysed, no significant call'}">style="background-color: rgb(247, 157, 70)"</c:when>
           		<c:when test="${row.XAxisToCellMap[xAxisBean.id].status eq null }">style="background-color: rgb(230, 242, 246)"</c:when>
            </c:choose>
            title="${xAxisBean.name}"></td>
        </c:forEach>
      </tr>
    </c:forEach>
    
    
    
  </table>
                

