<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:genericpage>

<jsp:body>

<h1 class="title" id="top">${mgi_accession_id}</h1>
        <c:if test="${not empty debug}">
<p>${versionDate}</p>
        </c:if>

            </br>                        

        <c:if test="${not empty message}">
            <div style="font-size: 120%;">
                <p>${message}</p>
            </div>
        </c:if>

<div style="font-size: 120%;">
    
    
    
    <c:if test="${empty list_alleles and empty list_none_alleles}">
        <p>Not found!</p>
    </c:if>

    
    
    
    <c:if test="${not empty list_alleles}">
    
    <h3>Alleles</h3>

    <table>
    <c:forEach var="item" items="${list_alleles}" varStatus="listx">
        <tr>
            <td>

        <c:choose>
        <c:when test="${not empty debug}">
            <a href="${baseUrl}/${item['url']}" target="_blank">${item['display_name_debug']}</a>
        </c:when>
        <c:otherwise>
            <a href="${baseUrl}/${item['url']}" target="_blank">${item['display_name']}</a>
        </c:otherwise>
        </c:choose>
        
        
        
        
        
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


        <c:choose>
        <c:when test="${not empty debug}">
            <a href="${baseUrl}/${item['url']}" target="_blank">${item['display_name_debug']}</a>
        </c:when>
        <c:otherwise>
            <a href="${baseUrl}/${item['url']}" target="_blank">${item['display_name']}</a>
        </c:otherwise>
        </c:choose>



            </td>
        <tr>
    </c:forEach>
    </table>
    
    </c:if>

</div>


</jsp:body>
  
</t:genericpage>
