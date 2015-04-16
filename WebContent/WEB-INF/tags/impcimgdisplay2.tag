<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%-- <jsp:doBody var="theBody"/>

<%
String allele = (String) jspContext.getAttribute("theBody");
allele = allele.replaceAll("<", "££");
allele = allele.replaceAll(">", "##");

allele = allele.replaceAll("££", "<sup>");
allele = allele.replaceAll("##", "</sup>");
%>

<%= allele %> --%>
<%--<jsp:doBody var="theBody"/>--%>
<%@ attribute name="img" required="true" type="java.util.Map"%>
<%@ attribute name="impcMediaBaseUrl" required="true" %>
<%@ attribute name="pdfThumbnailUrl" required="true" %>
<%@ attribute name="count" required="false" %>
<%@ attribute name="href" required="false" %>

        <li>
         <c:choose>
         	<c:when test="${not empty href}">
         		<a href="${href}">
         	</c:when>
         	<c:otherwise>
         		<a href="${impcMediaBaseUrl}/render_image/${img.omero_id}" class="fancybox" fullRes="${impcMediaBaseUrl}/render_image/${img.omero_id}">
         	</c:otherwise>
         </c:choose>
         <div class="thumb-image-holder">
         <c:choose>
         	<c:when test="${fn:containsIgnoreCase(img.download_url, 'annotation') }">
          		<img class="thumb-image" src="${pdfThumbnailUrl}/200"></a>
          	</c:when>
          	<c:otherwise>
          		 <img class="thumb-image" src="${impcMediaBaseUrl}/render_thumbnail/${img.omero_id}/200"></a>
          	</c:otherwise>
         </c:choose>
        
                                                <div class="caption">${pdfThumbnailUrl}
                                                <c:if test="${not empty count}">${count} Images<br/></c:if>
                                                <c:if test="${not empty img.parameter_association_name}">
                                                	<c:forEach var="pAssName" items="${img.parameter_association_name}" varStatus="status">${pAssName}, </c:forEach>
                                                </c:if>
                                                <c:if test="${not empty img.parameter_association_value}">
                                                	<c:forEach var="pAssValue" items="${img.parameter_association_value}" varStatus="status">${pAssValue}, </c:forEach>
                                                </c:if>
                                                <c:if test="${not empty img.allele_symbol}"><t:formatAllele>${img.allele_symbol}</t:formatAllele><br/></c:if>
                                                </div> 
          </div>
           
                                                
         </li>                                  