<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:genericpage>
    	<jsp:attribute name="title">Allele details </jsp:attribute>
        <jsp:attribute name="breadcrumb">&nbsp;&raquo; <a href="${baseUrl}/search#q=*:*&fq=*:*&facet=gene">Genes</a> &raquo; <a href = "${baseUrl}/genes/${acc}">${acc}</a> &raquo; Alleles</jsp:attribute>
	<jsp:attribute name="bodyTag"><body  class="gene-node no-sidebars small-header"></jsp:attribute>
                
<jsp:body>

    <h1 class="title" id="top">${marker_symbol} (${acc})</h1>
    </br>                        

    <div style="font-size: 120%;">
    
    <c:if test="${empty list_alleles and empty list_none_alleles}">
        <p>Not found!</p>
    </c:if>

    <c:if test="${not empty list_alleles}">
        <h3>Alleles</h3>
        <p> ${search_title}
        <c:if test="${search_title != 'Showing all alleles'}">
        <a href = "${baseUrl}/alleles/${acc}<c:if test="${bare == true}">?bare=true</c:if>">(Display all alleles for ${marker_symbol})</a> 
        </c:if>
        </p>
        
        <table>
        <th>MGI Allele Name</th>
        <th>Type</th>
        <th>IKMC Project</th>
        <th colspan="3">Available Products</th>
        <th>Allele Map</th>
        <c:forEach var="item" items="${list_alleles}" varStatus="listx">
            <tr>
                <td>
                    <a href="${baseUrl}/alleles/${acc}/${item['allele_name']}<c:if test="${bare == true}">?bare=true</c:if>" target="_blank">${item['allele_name']}</a>
                </td>
                <td>
                    ${item['allele_description']}
                </td>
                <td>
                    ${item['ikmc_project']}
                </td>
                <td>
                    <c:if test="${not empty item['mouse_status']}">
                    Mice Available
                    </c:if>
                </td>
                <td>
                    <c:if test="${not empty item['es_cell_status'] and item['es_cell_status'] == 'ES Cell Targeting Confirmed'}">
                    ES Cells Available
                    </c:if>
                </td>

                <td>
                    <c:if test="${not empty item['targeting_vector_status'] and item['targeting_vector_status'] != 'No Targeting Vector Production'}">
                    Targeting Vectors Available
                    </c:if>
                </td>
                <td>
                    <img alt="image not found!" src="${item['allele_simple_image']}" width="400px">
                </td>
            <tr>
        </c:forEach>
        </table>
    
    </c:if>

    </div>

</jsp:body>
  
</t:genericpage>
