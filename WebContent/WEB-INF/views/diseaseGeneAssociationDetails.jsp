<%-- 
    Document   : diseaseGeneAssociation
    Created on : 06-Nov-2013, 16:38:20
    Author     : jj8
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsp/jstl/functions"%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<td colspan="2">
    <h6 id="${diseaseGeneAssociationDetails.diseaseId}" >${diseaseGeneAssociationDetails.diseaseId} Disease Phenotype Terms</h6>
    <c:forEach var="hpTerm" items="${diseaseGeneAssociationDetails.diseasePhenotypes}">
        ${hpTerm.term}<br>
    </c:forEach>
</td>
<!--<td>-->
    <%--<c:forEach var="diseaseModelAssociation" items="${diseaseAssociations.diseaseModelAssociation}">--%>
        <%--<c:set var="phenotypeMatches" value="${diseaseModelAssociation.phenotypeMatches}"></c:set>--%>
        <%--<c:forEach var="phenotypeMatch" items="${phenotypeMatches}">--%>
            ${phenotypeMatch}<br>
        <%--</c:forEach>--%>
    <%--</c:forEach>--%>
<!--</td>-->
<c:set var="diseaseAssociations" value="${diseaseGeneAssociationDetails.diseaseAssociations}"/>
<td colspan="2">
    <c:choose>
        <c:when test="${empty diseaseAssociations}">
            <b>No mouse models associated with ${diseaseGeneAssociationDetails.diseaseId}.</b>
            <b>Association is by orthology only.</b>
        </c:when>
        <c:otherwise>
            <c:if test="${not empty literatureAssociations}">
                <h6>${disease.diseaseId} Associated Mouse Models (MGI curated)</h6>
                <c:forEach var="diseaseAssociation" items="${literatureAssociations}" varStatus="loop">
                    <c:set var="mouseModel" value="${diseaseAssociation.mouseModel}"></c:set>
                    <c:set var="score" value = "${diseaseAssociation.modelToDiseaseScore}"/>
                    
                    <c:if test="${requestPageType eq 'disease'}">
                        <c:set var="score" value = "${diseaseAssociation.diseaseToModelScore}"/>                       
                    </c:if>
                    <b style="color:#EF7B0B">${score}</b>: ${mouseModel.allelicCompositionLink} ${mouseModel.geneticBackground} (Source: ${mouseModel.source})<br/>
                    <c:forEach var="phenotypeTerm" items="${mouseModel.phenotypeTerms}">
                        ${phenotypeTerm.term}<br/>
                    </c:forEach>
                    <br/>
                </c:forEach>
            </c:if>
            <c:if test="${not empty phenotypicAssociations}">
                <h6>${disease.diseaseId} Associated Mouse Models (PhenoDigm predicted)</h6>
                <c:forEach var="diseaseAssociation" items="${phenotypicAssociations}" varStatus="loop">
                    <c:set var="mouseModel" value="${diseaseAssociation.mouseModel}"></c:set>
                    <c:set var="score" value = "${diseaseAssociation.modelToDiseaseScore}"/>
                    <!--make sure we display the correct score on the page-->
                    <c:if test="${requestPageType eq 'disease'}">
                        <c:set var="score" value = "${diseaseAssociation.diseaseToModelScore}"/>                       
                    </c:if>
                    <b style="color:#EF7B0B">${score}</b>: ${mouseModel.allelicCompositionLink} ${mouseModel.geneticBackground} (Source: ${mouseModel.source})<br/>
                    <c:forEach var="phenotypeTerm" items="${mouseModel.phenotypeTerms}">
                        ${phenotypeTerm.term}<br/>
                    </c:forEach>
                    <br/>                                        
                </c:forEach>
            </c:if>
        </c:otherwise>        
    </c:choose>
</td>