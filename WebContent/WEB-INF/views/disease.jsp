<%-- 
    Document   : disease
    Created on : 10-Sep-2013, 12:13:01
    Author     : Jules Jacobsen <jules.jacobsen@sanger.ac.uk>
--%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<t:genericpage>
    <jsp:attribute name="title">${disease.diseaseId} - ${disease.term}</jsp:attribute>

    <jsp:attribute name="header">
        <link rel="stylesheet" type="text/css" href="${baseUrl}/css/custom.css"/>
    </jsp:attribute>

    <jsp:attribute name="addToFooter">		
        <div class="region region-pinned">

            <div id="flyingnavi" class="block">

                <a href="#top"><i class="fa fa-chevron-up" title="scroll to top"></i></a>

                <ul>
                    <li><a href="#top">Disease</a></li>
                    <li><a href="#orthologous_mouse_models">Mouse Models</a></li>
                    <li><a href="#potential_mouse_models">Potential Mouse Models</a></li>

                </ul>

                <div class="clear"></div>

            </div>

        </div>
    </jsp:attribute>

    <jsp:attribute name="breadcrumb">
        &nbsp;&raquo; <a href="${baseUrl}/search#fq=type:disease&core=disease"> Diseases</a>&nbsp;&raquo; ${disease.diseaseId}
    </jsp:attribute>

    <jsp:attribute name="header">

    </jsp:attribute>

    <jsp:body>

        <h1 class="title" id="top">Disease: ${disease.term}</h1>
        <div class="section">
            <div class="inner">
                <p class="with-label">
                    <span class="label">Name</span>
                    ${disease.term}
                </p>

                <p class="with-label">
                    <span class="label">Synonyms</span>
                    <c:choose>
                        <c:when test="${empty disease.alternativeTerms}">
                            -
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="synonym" items="${disease.alternativeTerms}" varStatus="loop">
                                <!--hack for dealing with non-null empty synonyms -->
                                <c:choose>
                                    <c:when test="${empty synonym}">-</c:when>
                                    <c:otherwise>${synonym}</c:otherwise>
                                </c:choose>    
                                <c:if test="${!loop.last}">, </c:if>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </p>

                <p class="with-label">
                    <span class="label">Locus</span>
                    <c:choose>
                        <c:when test="${empty disease.locus}">
                            -
                        </c:when>
                        <c:otherwise>
                            ${disease.locus}
                        </c:otherwise>
                    </c:choose>
                </p>

                <p class="with-label">
                    <span class="label">Associated Human Genes</span>
                    <c:choose>
                        <c:when test="${empty orthologousGeneAssociations}">
                            -
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="association" items="${orthologousGeneAssociations}" varStatus="loop">
                                <c:set var="humanGeneIdentifier" value="${association.hgncGeneIdentifier}"></c:set>
                                    <!--Human Gene Symbol-->    
                                    <a href="${humanGeneIdentifier.externalUri}">${humanGeneIdentifier.geneSymbol}</a>
                                <c:if test="${!loop.last}">, </c:if>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </p>

                <p class="with-label">
                    <span class="label">Mouse Orthologs</span>
                    <c:choose>
                        <c:when test="${empty orthologousGeneAssociations}">
                            -
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="association" items="${orthologousGeneAssociations}" varStatus="loop">
                                <c:set var="mouseGeneIdentifier" value="${association.modelGeneIdentifier}"></c:set>
                                    <!--Mouse Gene Symbol-->
                                    <a href="${baseUrl}/genes/${mouseGeneIdentifier.databaseCode}:${mouseGeneIdentifier.databaseAcc}">${mouseGeneIdentifier.geneSymbol}</a>
                                <c:if test="${!loop.last}">, </c:if>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </p>

                <p class="with-label">
                    <span class="label">Source</span>
                    <a href="${disease.diseaseIdentifier.externalUri}">${disease.diseaseId}</a>
                </p>

            </div>
        </div>
        
        <div class="section">
            <jsp:include page="disease_orthologous_mouse_models_frag.jsp"></jsp:include>            
        </div>
        
        <div class="section">
            <jsp:include page="disease_predicted_mouse_models_frag.jsp"></jsp:include>            
        </div>

        <script type="text/javascript" src="http://code.jquery.com/jquery-1.11.1.min.js"></script>
        <script type="text/javascript" src="http://cdn.datatables.net/1.10.4/js/jquery.dataTables.min.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/phenodigm/diseasetableutils.js"></script>

    </jsp:body>

</t:genericpage>
