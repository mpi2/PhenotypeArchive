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
<%@ attribute name="impcMediaBaseUrl" required="true" %>
<%@ attribute name="count" required="false" %>

        <li <%-- <c:choose>
        <c:when test="${img.biological_sample_group eq 'control'}">style="background-color:#eee;"</c:when>
        <c:when test="${img.sex eq 'female'}">style="background-color:#F79D46;"</c:when>
        <c:when test="${img.sex eq 'male'}">style="background-color:#0978A1;"</c:when>
        </c:choose> --%>
         >
         
        <a href="${impcMediaBaseUrl}/render_image/${img.omero_id}" class="fancybox" fullRes="${impcMediaBaseUrl}/render_image/${img.omero_id}">
         <img src="${impcMediaBaseUrl}/render_thumbnail/${img.omero_id}/200">
        </a>
                                                <div class="caption">
                                                <c:if test="${not empty count}">${count} Images<br/></c:if>
                                                <c:if test="${not empty img.parameter_association_name}">
                                                	<c:forEach var="pAssName" items="${img.parameter_association_name}" varStatus="status">${pAssName}, </c:forEach>
                                                </c:if>
                                                <c:if test="${not empty img.biological_sample_group}">${img.biological_sample_group}</c:if>
                                                <c:if test="${not empty img.allele_symbol}"><t:formatAllele>${img.allele_symbol}</t:formatAllele><br/></c:if>
                                                		<%-- <c:forEach var="maTerm" items="${img.annotationTermName}" varStatus="status">${maTerm}, </c:forEach> --%>
                                                 		<%--  <c:if test="${not empty img.zygosity}">${img.zygosity}</c:if>
   												 		<c:if test="${not empty img.sex}">${img.sex}</c:if> --%>
   												 		
   												 		<!-- for testing and debug purposes maybe -->
   												 		<%-- <c:if test="${not empty img.biological_sample_id}">${img.biological_sample_id}</c:if><!-- mouse id? -->
   												 		 --%><%-- <c:if test="${not empty img.metadata_group}">${img.metadata_group}</c:if> --%>
   												 		<%-- <c:if test="${not empty img.phenotyping_center}">${img.phenotyping_center}</c:if> --%>
   									
   												 		<%-- <c:if test="${not empty img.date_of_experiment}">${img.date_of_experiment}</c:if>
   												 		<c:if test="${not empty img.strain_name}">${img.strain_name}</c:if> --%>
   												 		
                                                <br>
                                                </div> 
           
                                                
         </li>                                  
                                          
                                        



<!-- <a rel="gallery" title="Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin imperdiet augue et magna interdum hendrerit" class="fancybox" href="https://dev.mousephenotype.org/data/media/images/584/M00177499_00013016_download_tn_large.jpg"><img src="https://dev.mousephenotype.org/data/media/images/584/M00177499_00013016_download_tn_small.jpg" alt=""/></a>
 -->
