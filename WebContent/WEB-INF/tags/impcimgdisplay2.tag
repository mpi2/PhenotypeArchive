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
<%@ attribute name="pdfThumbnailUrl" required="false" %>
<%@ attribute name="count" required="false" %>
<%@ attribute name="href" required="false" %>
<%@ attribute name="category" required="false" %>
        <li style="height:275px; max-height:275px; min-height:275px; word-wrap: break-word;width:23%">
         <!-- href specified as arg to tag as in the case of gene page to image picker links -->
         <!-- pdf annotation not image -->
         <!-- defaults to image -->
         <c:choose>
        
         	<c:when test="${not empty href}">
         		<a href="${href}">
         		
         		<img  src="${impcMediaBaseUrl}/render_thumbnail/${img.omero_id}/200" style="max-height: 200px;"></a>
         	</c:when>
         	
         	<c:when test="${fn:containsIgnoreCase(img.download_url, 'annotation') }">
         		<a href="${img.download_url}" >
         		
         		<img  src="${pdfThumbnailUrl}/200" style="max-height: 200px;"></a>
         	</c:when>
         	
         	<c:otherwise>
         		<a href="${impcMediaBaseUrl}/render_image/${img.omero_id}" class="fancybox" fullRes="${impcMediaBaseUrl}/render_image/${img.omero_id}">
         		
         		<img  src="${impcMediaBaseUrl}/render_thumbnail/${img.omero_id}/200" style="max-height: 200px;"></a>
         	</c:otherwise>
         </c:choose>
                                                <div class="caption" style="height:150px; overflow:auto;word-wrap: break-word;">
                                                <c:if test="${not empty category}"><a href="${href}">${category}</a><br/></c:if>
                                                <c:if test="${not empty count}">${count} Images<br/></c:if>
                                                <c:if test="${not empty img.parameter_association_name}">
                                                	<c:forEach items="${img.parameter_association_name}" varStatus="status">
                                                		<c:out value="${img.parameter_association_name[status.index]}"/>
                                                		<c:out value="${img.parameter_association_value[status.index]}"/>
                                                	</c:forEach>
                                                </c:if>
                                                <c:if test="${not empty img.allele_symbol}"><t:formatAllele>${img.allele_symbol}</t:formatAllele><br/></c:if>
                                                </div> 
          
           
                                                
         </li>                                  