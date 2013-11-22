<%-- 
    Document   : gene
    Created on : 29-Aug-2013, 14:33:23
    Author     : Jules Jacobsen <jules.jacobsen@sanger.ac.uk>
--%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:genericpage>
    <jsp:attribute name="title">${mgiId} - ${geneIdentifier.geneSymbol}</jsp:attribute>

    <jsp:attribute name="header">
        <link rel="stylesheet" type="text/css" href="${baseUrl}/css/custom.css"/>
    </jsp:attribute>

    <jsp:attribute name="breadcrumb">
        &nbsp;&raquo; <a href="${baseUrl}/search#q=*:*&core=disease&fq=type:disease"> Diseases</a>&nbsp;&raquo; ${geneIdentifier.geneSymbol}
    </jsp:attribute>

    <jsp:body>

        <script src="https://www.mousephenotype.org/js/general/toggle.js"></script>
        <script type="text/javascript" charset="utf-8" src="${baseUrl}/js/vendor/DataTables-1.9.4/jquery.dataTables.min.js"></script>
        <script type="text/javascript" charset="utf-8" src="${baseUrl}/js/vendor/jquery-1.7.2.min.js"></script>
        <link rel="stylesheet" href="http://code.jquery.com/ui/1.10.3/themes/smoothness/jquery-ui.css" />

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

        <div class='topic'>Disease Associations for <a href="https://www.mousephenotype.org/data/genes/${geneIdentifier.databaseCode}:${geneIdentifier.databaseAcc}">${geneIdentifier.geneSymbol}</a> (human ortholog <a href="http://www.genenames.org/data/hgnc_data.php?hgnc_id=${humanOrtholog.databaseAcc}">${humanOrtholog.geneSymbol}</a>)</div>

        <div class="row-fluid dataset">
            <div class="container span12">
                <h4 class="topic">Curated Disease Associations <a href='http://www.sanger.ac.uk/resources/databases/phenodigm/'></a></h4>
                    <c:choose>
                        <c:when test="${empty curatedAssociations}">
                        No diseases are known to be associated with ${geneIdentifier.geneSymbol} according to external resources.
                    </c:when>
                    <c:otherwise>
                        The following diseases are associated with ${geneIdentifier.geneSymbol} from external resources*
                        <table id="phenotypes" class="table table-striped">
                            <thead>
                                <tr>
                                    <th>Disease Name</th>
                                    <th>Source</th>
                                    <th>Associated in Human</th>
                                    <th>Mouse Literature Evidence (MGI)</th>
                                    <th>In Locus</th>
                                    <th>MGI Mouse Phenotype Evidence (Phenodigm)</th>
                                    <th>MGP Mouse Phenotype Evidence (Phenodigm)</th>
                                    <th></th>
                                </tr>
                            </thead>                        
                            <tbody> 
                                <c:forEach var="association" items="${curatedAssociations}" varStatus="loop">
                                    <c:set var="disease" value="${association.disease}"></c:set>
                                    <c:set var="associationSummary" value="${association.associationSummary}"></c:set>
                                    <tr id="${disease.diseaseIdentifier.databaseAcc}" targetRowId="${geneIdentifier.databaseAcc}_${disease.diseaseIdentifier.databaseAcc}" style="cursor:help;color:#27408B;" rel="tooltip" data-placement="top" title="Click anywhere on row display phenotype terms" alt="Click row to display phenotype terms" >
                                        <!--Disease Name-->
                                        <td><a href="../disease/${disease.diseaseId}">${disease.term}</a></td>
                                        <!--Source-->
                                        <td>
                                            <a id="diseaseId" href="${disease.diseaseIdentifier.externalUri}">${disease.diseaseId}</a></td>
                                        <!--Associated in Human --> 
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
                                            <b style="color:#FF9000">${associationSummary.bestMgiScore}</b>   
                                        </td>
                                        <td>
                                            <c:if test="${0.0 != associationSummary.bestImpcScore}">
                                                <b style="color:#FF9000">${associationSummary.bestImpcScore}</b>
                                            </c:if>                                        
                                        </td>
                                        <td class="arrow">

                                        </td>   
                                    </tr>
                                    <tr id="${geneIdentifier.databaseAcc}_${disease.diseaseIdentifier.databaseAcc}" requestpagetype= "gene" geneid="${geneIdentifier.compoundIdentifier}" diseaseid="${disease.diseaseIdentifier.compoundIdentifier}">
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
                <h4 class="topic">Phenotypic Disease Associations <a href='http://www.sanger.ac.uk/resources/databases/phenodigm/'></a></h4>
                    <c:choose>
                        <c:when test="${empty phenotypeAssociations}">
                        No diseases are known to be associated with ${geneIdentifier.geneSymbol} according to external resources.
                    </c:when>
                    <c:otherwise>
                        The following diseases are associated with ${geneIdentifier.geneSymbol} by phenotypic similarity
                        <table id="predictedPhenotypes" class="table table-striped">
                            <thead>
                                <tr>
                                    <th>Disease Name</th>
                                    <th>Source</th>
                                    <th>Associated in Human</th>
                                    <th>Mouse Literature Evidence (MGI)</th>
                                    <th>In Locus</th>
                                    <th>MGI Mouse Phenotype Evidence (Phenodigm)</th>
                                    <th>MGP Mouse Phenotype Evidence (Phenodigm)</th>
                                    <th></th>
                                </tr>
                            </thead>                        
                            <tbody>
                                <c:forEach var="association" items="${phenotypeAssociations}" varStatus="loop">
                                    <c:set var="disease" value="${association.disease}"></c:set>
                                    <c:set var="associationSummary" value="${association.associationSummary}"></c:set>
                                    <tr id="${disease.diseaseIdentifier.databaseAcc}" targetRowId="P${geneIdentifier.databaseAcc}_${disease.diseaseIdentifier.databaseAcc}" style="cursor:help;color:#27408B;" rel="tooltip" data-placement="top" title="Click anywhere on row display phenotype terms" alt="Click row to display phenotype terms" >
                                        <!--Disease Name-->
                                        <td><a href="../disease/${disease.diseaseId}">${disease.term}</a></td>
                                        <!--Source-->
                                        <td><a href="${disease.diseaseIdentifier.externalUri}">${disease.diseaseId}</a></td>
                                        <!--Associated in Human --> 
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
                                            <b style="color:#FF9000">${associationSummary.bestMgiScore}</b>   
                                        </td>
                                        <td>
                                            <c:if test="${0.0 != associationSummary.bestImpcScore}">
                                                <b style="color:#FF9000">${associationSummary.bestImpcScore}</b>
                                            </c:if>                                        
                                        </td>
                                        <td class="arrow">

                                        </td>   
                                    </tr>
                                    <tr id="P${geneIdentifier.databaseAcc}_${disease.diseaseIdentifier.databaseAcc}" requestpagetype= "gene" geneid="${geneIdentifier.compoundIdentifier}" diseaseid="${disease.diseaseIdentifier.compoundIdentifier}">
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
