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

    <jsp:attribute name="breadcrumb">
        &nbsp;&raquo; <a href="${baseUrl}/search#q=*:*&core=disease&fq=type:disease"> Diseases</a>&nbsp;&raquo; ${disease.diseaseId}
    </jsp:attribute>

    <jsp:body>

        <script type="text/javascript" charset="utf-8" src="${baseUrl}/js/vendor/jquery-1.7.2.min.js"></script>

        <script type="text/javascript">
            function getDiseaseAssociations(targetRowId) {
                //                alert(targetRowId);
                var targetRow = $('#' + targetRowId);
                var geneId = $(targetRow).attr("geneid");
                var diseaseId = $(targetRow).attr("diseaseid");
                var requestPageType = $(targetRow).attr("requestpagetype");
                console.log(requestPageType + " page getDiseaseAssociations for: " + geneId + " " + diseaseId);

                var uri = "${baseUrl}/phenodigm/diseaseGeneAssociations";
                $.get(uri, {
                    geneId: geneId,
                    diseaseId: diseaseId,
                    requestPageType: requestPageType
                }, function(response) {
                    console.log(response);
                    //${disease.diseaseId}_${mouseGeneIdentifier.compoundIdentifier}
                    var id = "#" + geneId + "_" + diseaseId;
                    console.log("Searching for id:" + id);
                    $(targetRow).html(response);

                });
            }
            ;
        </script>

        <script type="text/javascript">
            $(document).ready(function() {
                $("#phenotypes tr:odd").addClass("odd");
                $("#phenotypes tr:not(.odd)").hide();
                $("#phenotypes tr:first-child").show();

                $("#phenotypes tr.odd").click(function() {
                    $(this).next("tr").toggle();
                    $(this).find(".arrow").toggleClass("up");
                    if ($(this).next("tr").find("td").length === 0) {
                        getDiseaseAssociations($(this).attr("targetRowId"));
                    }
                });
                //$("#report").jExpand();
            });
        </script>
        <script type="text/javascript">
            $(document).ready(function() {
                $("#predictedPhenotypes tr:odd").addClass("odd");
                $("#predictedPhenotypes tr:not(.odd)").hide();
                $("#predictedPhenotypes tr:first-child").show();

                $("#predictedPhenotypes tr.odd").click(function() {
                    $(this).next("tr").toggle();
                    $(this).find(".arrow").toggleClass("up");
                    if ($(this).next("tr").find("td").length === 0) {
                        getDiseaseAssociations($(this).attr("targetRowId"));
                    }
                });
            });
        </script>
        <div class='topic'>Gene associations for ${disease.term} <a href="${disease.diseaseIdentifier.externalUri}">(${disease.diseaseId})</a> </div>
        <div class="row-fluid dataset">
            <div class="container span12">
                <div class='documentation'><a href='${baseUrl}/documentation/disease-help.html#details' class='generalPanel'><img src="${baseUrl}/img/info_20x20.png" /></a></div>
                <h4 class="topic">Curated Gene Associations</h4>
                <c:choose>
                    <c:when test="${empty curatedAssociations}">
                        No genes are known to be associated with ${disease.diseaseId} according to external resources.
                    </c:when>
                    <c:otherwise>
                        The following genes are associated with ${disease.diseaseId} from external resources*
                        <table id="phenotypes" class="table table-striped"> 
                            <thead>
                                <tr>
                                    <th>Human Gene Symbol</th>
                                    <th>Mouse Gene Symbol</th>
                                    <th>Associated in Human</th>
                                    <th>Mouse Literature Evidence (MGI)</th>
                                    <th>In Locus</th>
                                    <th>MGI Mouse Phenotype Evidence (Phenodigm)</th>
                                    <th>MGP Mouse Phenotype Evidence (Phenodigm)</th>
                                    <th></th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="association" items="${curatedAssociations}">
                                    <c:set var="mouseGeneIdentifier" value="${association.mouseGeneIdentifier}"></c:set>
                                    <c:set var="humanGeneIdentifier" value="${association.humanGeneIdentifier}"></c:set>
                                    <c:set var="associationSummary" value="${association.associationSummary}"></c:set>
                                    <tr id="${mouseGeneIdentifier.compoundIdentifier}" targetRowId="${humanGeneIdentifier.databaseAcc}_${mouseGeneIdentifier.databaseAcc}_${disease.diseaseIdentifier.databaseAcc}" style="cursor:help;color:#27408B;" rel="tooltip" data-placement="top" title="Click anywhere on row display phenotype terms" alt="Click row to display phenotype terms" />
                                <td>
                                    <!--Human Gene Symbol-->
                                    <a href="${humanGeneIdentifier.externalUri}">${humanGeneIdentifier.geneSymbol}</a> 
                                </td>
                                <td>
                                    <!--Mouse Gene Symbol-->
                                    <a href="../gene/${mouseGeneIdentifier.databaseCode}:${mouseGeneIdentifier.databaseAcc}">${mouseGeneIdentifier.geneSymbol}</a> 
                                </td>

                                <!--Associated in Human - Yes or empty-->   
                                <td>
                                    <c:if test="${associationSummary.associatedInHuman}">Yes</c:if>
                                    </td>  
                                    <td>
                                        <!--Mouse Literature Evidence (MGI) - Yes or empty-->
                                    <c:if test="${associationSummary.hasLiteratureEvidence}">Yes</c:if>
                                    </td>                                    
                                    <td>
                                    <c:if test="${associationSummary.inLocus}">
                                        Yes
                                    </c:if>
                                </td>
                                <!--Mouse Phenotype Evidence (Phenodigm)-->
                                <td>
                                    <c:if test="${0.0 != associationSummary.bestMgiScore}">
                                        <b style="color:#FF9000">${associationSummary.bestMgiScore}</b>   
                                    </c:if>                                        
                                </td>
                                <td>
                                    <c:if test="${0.0 != associationSummary.bestImpcScore}">
                                        <b style="color:#FF9000">${associationSummary.bestImpcScore}</b>
                                    </c:if>
                                </td>
                                <td class="arrow">

                                </td>                                     
                                </tr>
                                <tr id="${humanGeneIdentifier.databaseAcc}_${mouseGeneIdentifier.databaseAcc}_${disease.diseaseIdentifier.databaseAcc}" requestpagetype= "disease" geneid="${mouseGeneIdentifier.compoundIdentifier}" diseaseid="${disease.diseaseIdentifier.compoundIdentifier}">
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
        <div class="row-fluid dataset">
            <div class="container span12">
                <div class='documentation'><a href='${baseUrl}/documentation/disease-help.html#details' class='generalPanel'><img src="${baseUrl}/img/info_20x20.png" /></a></div>
                <h4 class="topic">Phenotypic Gene Associations <a href='http://www.sanger.ac.uk/resources/databases/phenodigm/'></a></h4>
                    <c:choose>
                        <c:when test="${empty phenotypeAssociations}">
                        No genes associated with ${disease.diseaseId} by phenotypic similarity.
                    </c:when>
                    <c:otherwise>
                        The following genes are associated with ${disease.diseaseId} by phenotypic similarity.
                        <table id="predictedPhenotypes" class="table table-striped"> 
                            <thead>
                                <tr>
                                    <th>Human Gene Symbol</th>
                                    <th>Mouse Gene Symbol</th>
                                    <th>Associated in Human</th>
                                    <th>Mouse Literature Evidence (MGI)</th>
                                    <th>In Locus</th>
                                    <th>MGI Mouse Phenotype Evidence (Phenodigm)</th>
                                    <th>MGP Mouse Phenotype Evidence (Phenodigm)</th>
                                    <th></th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="association" items="${phenotypeAssociations}">
                                    <c:set var="mouseGeneIdentifier" value="${association.mouseGeneIdentifier}"></c:set>
                                    <c:set var="humanGeneIdentifier" value="${association.humanGeneIdentifier}"></c:set>
                                    <c:set var="associationSummary" value="${association.associationSummary}"></c:set>
                                    <tr id="${mouseGeneIdentifier.compoundIdentifier}" targetRowId="P${humanGeneIdentifier.databaseAcc}_${mouseGeneIdentifier.databaseAcc}_${disease.diseaseIdentifier.databaseAcc}" style="cursor:help;color:#27408B;" rel="tooltip" data-placement="top" title="Click anywhere on row display phenotype terms" alt="Click row to display phenotype terms" >
                                        <td>
                                            <!--Human Gene Symbol-->
                                            <a href="${humanGeneIdentifier.externalUri}">${humanGeneIdentifier.geneSymbol}</a> 
                                        </td>
                                        <td>
                                            <!--Mouse Gene Symbol-->
                                            <a href="../gene/${mouseGeneIdentifier.databaseCode}:${mouseGeneIdentifier.databaseAcc}">${mouseGeneIdentifier.geneSymbol}</a> 
                                        </td>

                                        <!--Associated in Human - Yes or empty-->   
                                        <td>
                                            <c:if test="${associationSummary.associatedInHuman}">Yes</c:if>
                                            </td>  
                                            <td>
                                                <!--Mouse Literature Evidence (MGI) - Yes or empty-->
                                            <c:if test="${associationSummary.hasLiteratureEvidence}">Yes</c:if>
                                            </td>                                    
                                            <td>
                                            <c:if test="${associationSummary.inLocus}">
                                                Yes
                                            </c:if>
                                        </td>
                                        <!--Mouse Phenotype Evidence (Phenodigm)-->
                                        <td>
                                            <c:if test="${0.0 != associationSummary.bestMgiScore}">
                                                <b style="color:#FF9000">${associationSummary.bestMgiScore}</b>   
                                            </c:if>                                        
                                        </td>
                                        <td>
                                            <c:if test="${0.0 != associationSummary.bestImpcScore}">
                                                <b style="color:#FF9000">${associationSummary.bestImpcScore}</b>
                                            </c:if>                                        
                                        </td>
                                        <td class="arrow">

                                        </td>                                     
                                    </tr>
                                    <tr id="P${humanGeneIdentifier.databaseAcc}_${mouseGeneIdentifier.databaseAcc}_${disease.diseaseIdentifier.databaseAcc}" requestpagetype= "disease" geneid="${mouseGeneIdentifier.compoundIdentifier}" diseaseid="${disease.diseaseIdentifier.compoundIdentifier}">
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </jsp:body>
</t:genericpage>
