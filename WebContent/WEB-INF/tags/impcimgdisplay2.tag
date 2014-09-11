<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%-- <jsp:doBody var="theBody"/>

<%
String allele = (String) jspContext.getAttribute("theBody");
allele = allele.replaceAll("<", "��");
allele = allele.replaceAll(">", "##");

allele = allele.replaceAll("��", "<sup>");
allele = allele.replaceAll("##", "</sup>");
%>

<%= allele %> --%>
<%--<jsp:doBody var="theBody"/>--%>
<%@ attribute name="img" required="true" type="java.util.Map"%>
<%@ attribute name="impcMediaBaseUrl" required="true" %>

        
        <a href="${impcMediaBaseUrl}/render_image/${img.omero_id}" class="fancybox" fullRes="${impcMediaBaseUrl}/${img.omero_id}"><img src="${impcMediaBaseUrl}/render_thumbnail/${img.omero_id}/96"></a>
                                                <div class="caption">
                                                <c:if test="${not empty img.allele_symbol}"><t:formatAllele>${img.allele_symbol}</t:formatAllele><br/></c:if>
                                                		<%-- <c:forEach var="maTerm" items="${img.annotationTermName}" varStatus="status">${maTerm}, </c:forEach> --%>
                                                 		<c:if test="${not empty img.zygosity}">${img.zygosity}</c:if>
   												 		<c:if test="${not empty img.sex}">${img.sex}</c:if>
   												 		<c:if test="${not empty img.biological_sample_group}">${img.biological_sample_group}</c:if>
   												 		<!-- for testing and debug purposes maybe -->
   												 		<c:if test="${not empty img.biological_sample_id}">${img.biological_sample_id}</c:if><!-- mouse id? -->
   												 		<c:if test="${not empty img.metadata_group}">${img.metadata_group}</c:if>
   												 		<c:if test="${not empty img.phenotyping_center}">${img.phenotyping_center}</c:if>
   												 		<c:if test="${not empty img.parameter_stable_id}">${img.parameter_stable_id}</c:if>
   												 		<c:if test="${not empty img.date_of_experiment}">${img.date_of_experiment}</c:if>
   												 		<c:if test="${not empty img.strain_name}">${img.strain_name}</c:if>
   												 		
                                                <br>
                                                </div> 
                                                
                                                
                                          
                                        



<!-- <a rel="gallery" title="Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin imperdiet augue et magna interdum hendrerit" class="fancybox" href="https://dev.mousephenotype.org/data/media/images/584/M00177499_00013016_download_tn_large.jpg"><img src="https://dev.mousephenotype.org/data/media/images/584/M00177499_00013016_download_tn_small.jpg" alt=""/></a>
 -->