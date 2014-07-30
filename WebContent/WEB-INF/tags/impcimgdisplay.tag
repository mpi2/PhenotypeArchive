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
<%@ attribute name="img" required="true" type="uk.ac.ebi.phenotype.service.dto.ImageDTO"%>
<%@ attribute name="impcMediaBaseUrl" required="true" %>

        
        <a href="${impcMediaBaseUrl}/render_image/${img.omeroId}" class="fancybox" fullRes="${impcMediaBaseUrl}/${img.omeroId}"><img src="${impcMediaBaseUrl}/render_thumbnail/${img.omeroId}/200"></a>
                                                <div class="caption">
                                                <c:if test="${not empty img.alleleSymbol}"><t:formatAllele>${img.alleleSymbol}</t:formatAllele><br/></c:if>
                                                		<%-- <c:forEach var="maTerm" items="${img.annotationTermName}" varStatus="status">${maTerm}, </c:forEach> --%>
                                                 		<c:if test="${not empty img.zygosity}">${img.zygosity}</c:if>
   												 		<c:if test="${not empty img.sex}">${img.sex}</c:if>
   												 		<c:if test="${not empty img.group}">${img.group}</c:if> 
                                                <br>
                                                </div> 
                                                
                                                
                                          
                                        



<!-- <a rel="gallery" title="Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin imperdiet augue et magna interdum hendrerit" class="fancybox" href="https://dev.mousephenotype.org/data/media/images/584/M00177499_00013016_download_tn_large.jpg"><img src="https://dev.mousephenotype.org/data/media/images/584/M00177499_00013016_download_tn_small.jpg" alt=""/></a>
 -->
