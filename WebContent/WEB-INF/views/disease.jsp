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

        <script type="text/javascript">
            function getDiseaseAssociations(clicked) {

                var targetRowId = $(clicked).attr("targetRowId");
                var targetRow = $('#' + targetRowId);
                var geneId = $(clicked).attr("geneid");
                var diseaseId = $(clicked).attr("diseaseid");
                var requestPageType = $(clicked).attr("requestpagetype");
                console.log(requestPageType + " page getDiseaseAssociations for: " + geneId + " " + diseaseId);

                var uri = "${baseUrl}/phenodigm/diseaseGeneAssociations";
                $.get(uri, {
                    geneId: geneId,
                    diseaseId: diseaseId,
                    requestPageType: requestPageType
                }, function (response) {
                    console.log(response);
                    //add the response html to the target row
                    $(targetRow).html(response);
                    //change the clicked row icon to an minus sign
                    $(clicked).find("td#toggleButton i").removeClass("fa-plus-square").addClass("fa-minus-square");
                });
            }
            ;

            /*
             * Toggles the row icon between a "fa-minus-square" and a "fa-plus-square".
             */
            function toggleRowIcon(row) {
                var rowOpen = $(row).find("td#toggleButton i").hasClass("fa-minus-square");
                if (rowOpen) {
                    $(row).find("td#toggleButton i").removeClass("fa-minus-square").addClass("fa-plus-square");
                }
                else {
                    $(row).find("td#toggleButton i").removeClass("fa-plus-square").addClass("fa-minus-square");
                }
            }
            ;
        </script>

<!--        <script type="text/javascript">
            $(document).ready(function () {
                $("#orthologous_phenotypes tr:odd").addClass("odd");
                $("#orthologous_phenotypes tr:not(.odd)").hide();
                $("#orthologous_phenotypes tr:first-child").show();

                $("#orthologous_phenotypes tr.odd").click(function () {
                    $(this).next("tr").toggle();
                    if ($(this).next("tr").find("td").length === 0) {
//                        $(this).next().html("<td></td>").prepend("<i></i>").addClass("fa fa-spinner fa-spin");
                        getDiseaseAssociations($(this));
                    }
                    toggleRowIcon($(this));
                });
            });
        </script>-->

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

        <script src="http://code.jquery.com/jquery-1.11.1.min.js"></script>
        <script src="http://cdn.datatables.net/1.10.4/js/jquery.dataTables.min.js"></script>

        <script type="text/javascript">
            /* Formatting function for row details*/
            function makeChildRow(clicked) {

                var targetRowId = $(clicked).attr("targetRowId");
                var geneId = $(clicked).attr("geneid");
                var diseaseId = $(clicked).attr("diseaseid");
                var requestPageType = $(clicked).attr("requestpagetype");

//                console.log('Row ' + targetRowId + ' clicked');
                var formatted = '<table cellpadding="4" cellspacing="0" border="0">' +
                        '<tr id="' + targetRowId + '" geneid="' + geneId + '" diseaseid="' + diseaseId + '" requestpagetype="' + requestPageType + '">' +
                        '<td id="loadingPlaceholder" colspan="4"><i class="fa fa-spinner fa-spin"></i></td>' +
                        '</tr>' +
                        '</table>';
//                console.log(formatted);
                return formatted;
            }
        </script>

        <script type="text/javascript">
            function insertDiseaseAssociations(clicked) {

                var targetRowId = $(clicked).attr("targetRowId");
                var targetRow = $('#' + targetRowId);
                var geneId = $(clicked).attr("geneid");
                var diseaseId = $(clicked).attr("diseaseid");
                var requestPageType = $(clicked).attr("requestpagetype");
//                console.log(requestPageType + " page getDiseaseAssociations for: " + geneId + " " + diseaseId);

                var uri = "${baseUrl}/phenodigm/diseaseGeneAssociations";
                $.get(uri, {
                    geneId: geneId,
                    diseaseId: diseaseId,
                    requestPageType: requestPageType
                }, function (response) {
//                    console.log(response);
                    //add the response html to the target row
                    $(targetRow).remove('#loadingPlaceholder').html(response);
                });
            }
            ;
        </script>

    </jsp:body>

</t:genericpage>
