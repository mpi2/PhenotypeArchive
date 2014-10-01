<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:genericpage>

<jsp:body>

<h1 class="title" id="top">${mgi_accession_id}</h1>
<p>${versionDate}</p>
</br>                        

        <c:if test="${not empty message}">
            <div style="font-size: 120%;">
                <p>${message}</p>
            </div>
        </c:if>

<div style="font-size: 120%;">
    
    <c:if test="${not empty list_alleles}">
    
    <h3>Alleles</h3>

    <table>
    <c:forEach var="item" items="${list_alleles}" varStatus="listx">
        <tr>
            <td>
    <c:if test="${empty item['link']}">
                <p>${item['display_name']}</p>
    </c:if>
    <c:if test="${not empty item['link']}">
                <a href="${baseUrl}/alleles/${item['mgi_accession_id']}/${item['allele_name_e']}" target="_blank">${item['display_name']}</a>
    </c:if>
            </td>
        <tr>
    </c:forEach>
    </table>
    
    </c:if>




    <c:if test="${not empty list_none_alleles}">
    
    <h3>Potential Alleles</h3>

    <table>
    <c:forEach var="item" items="${list_none_alleles}" varStatus="listx">
        <tr>
            <td>
                <a href="${baseUrl}/alleles/${item['mgi_accession_id']}/${item['allele_name_e']}" target="_blank">${item['display_name']}</a>
            </td>
        <tr>
    </c:forEach>
    </table>
    
    </c:if>

</div>


</jsp:body>
  
</t:genericpage>
