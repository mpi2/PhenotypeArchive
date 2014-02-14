<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
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
<%@ attribute name="mediaBaseUrl" required="true" %>

        
         <a href="${mediaBaseUrl}/${img.fullResolutionFilePath}"><img src="${mediaBaseUrl}/${img.smallThumbnailFilePath}"></a><div class="data-title"><a href="#"><i class="fa fa-download"></i> Download this image in high resolution</a></div>
                                                <div class="caption">
                                                <c:if test="${not empty img.genotypeString}"><t:formatAllele>${img.genotypeString}</t:formatAllele><br/></c:if>
                                                		<c:forEach var="maTerm" items="${img.annotationTermName}" varStatus="status">${maTerm}, </c:forEach>
                                                 		<c:if test="${not empty img.genotype}">${img.genotype}, </c:if>
   												 		<c:if test="${not empty img.gender}">${img.gender}, </c:if>
   												 		<c:if test="${not empty img.institute}"><c:forEach var="org" items="${img.institute}">${org}</c:forEach></c:if> 
                                                <br>
                                                </div>
                                          
                                          <!--   <a href="https://dev.mousephenotype.org/data/media/images/914/I20080318131750_download_full.jpg"><img src="https://dev.mousephenotype.org/data/media/images/914/I20080318131750_download_tn_small.jpg" /></a><div class="data-title"><a href="#"><i class="fa fa-download"></i> Download this image in high resolution</a></div> -->