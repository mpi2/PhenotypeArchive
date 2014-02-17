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
                }, function(response) {
//                    console.log(response);
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

        <script type="text/javascript">
            $(document).ready(function() {
                $("#phenotypes tr:odd").addClass("odd");
                $("#phenotypes tr:not(.odd)").hide();
                $("#phenotypes tr:first-child").show();

                $("#phenotypes tr.odd").click(function() {
                    $(this).next("tr").toggle();
                    if ($(this).next("tr").find("td").length === 0) {
                        getDiseaseAssociations($(this));
                    }
                    toggleRowIcon($(this));
                });
            });
        </script>
    </jsp:attribute>

    <jsp:body>

        <h1 class="title">Associated Diseases: ${geneIdentifier.geneSymbol}</h1>

        <div class="section">
            <div class="inner">
                <p class="with-label">
                    <span class="label">Mouse Gene Symbol</span>
                    <a href="https://www.mousephenotype.org/data/genes/${geneIdentifier.databaseCode}:${geneIdentifier.databaseAcc}">${geneIdentifier.geneSymbol}</a> 
                </p>
                <p class="with-label">
                    <span class="label">Human Ortholog</span>
                    <a href="${humanOrtholog.externalUri}">${humanOrtholog.geneSymbol}</a>
                </p>
                <p class="with-label">
                    <span class="label">Associated Diseases</span>
                    <c:choose>
                        <c:when test="${empty knownDiseaseAssociationSummaries}">
                            -
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="association" items="${knownDiseaseAssociationSummaries}" varStatus="loop">
                                <a href="../disease/${association.diseaseIdentifier}">${association.diseaseTerm}</a> (<a id="diseaseId" href="${association.diseaseIdentifier.externalUri}">${association.diseaseIdentifier}</a>)
                                <c:if test="${!loop.last}">, </c:if>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </p>
            </div>
        </div>

        <div class="section">
            <h2 class="topic">Potential Disease Models <a href='http://www.sanger.ac.uk/resources/databases/phenodigm/'></a><span class='documentation'><a href='${baseUrl}/documentation/disease-help.html#details' class='mpPanel'><i class="fa fa-question-circle pull-right"></i></a></span></h2>
            <div class="inner">
                <c:choose>
                    <c:when test="${empty phenotypeAssociations}">
                        No diseases are known to be associated with ${geneIdentifier.geneSymbol} according to external resources.
                    </c:when>
                    <c:otherwise>
                        <!--The following diseases are associated with ${geneIdentifier.geneSymbol} by phenotypic similarity-->
                        <table id="phenotypes" class="table tableSorter">
                            <thead>
                                <tr>
                                    <th><span class="main">Disease Name</span></th>
                                    <th><span class="main">Source</span></th>
                                    <th>Disease Gene Ortholog</th>
                                    <th>Syntenic Disease Locus</th>
                                    <th>Mouse Literature Evidence (MGI)</th>
                                    <th><span class="main">MGI</span><span class="sub">Mouse Phenotype Evidence (Phenodigm)</span></th>
                                    <th><span class="main">IMPC</span><span class="sub">Mouse Phenotype Evidence (Phenodigm)</span></th>
                                    <th></th>
                                </tr>
                            </thead>                        
                            <tbody>
                                <c:forEach var="association" items="${phenotypeAssociations}" varStatus="loop">
                                    <c:set var="associationSummary" value="${association.associationSummary}"></c:set>
                                    <tr id="${disease.diseaseIdentifier.databaseAcc}" targetRowId="P${geneIdentifier.databaseAcc}_${association.diseaseIdentifier.databaseAcc}" requestpagetype= "gene" geneid="${geneIdentifier.compoundIdentifier}" diseaseid="${association.diseaseIdentifier.compoundIdentifier}">
                                        <!--Disease Name-->
                                        <td><a href="../disease/${association.diseaseIdentifier}">${association.diseaseTerm}</a></td>
                                        <!--Source-->
                                        <td>
                                            <a id="diseaseId" href="${association.diseaseIdentifier.externalUri}">${association.diseaseIdentifier}</a>
                                        </td>
                                        <!--Associated in Human --> 
                                        <td>
                                            <c:if test="${associationSummary.associatedInHuman}">Yes</c:if>
                                            </td>  
                                            <!--In syntenic disease locus - Yes or empty-->
                                            <td>
                                            <c:if test="${associationSummary.inLocus}">
                                                Yes
                                            </c:if>
                                        </td>
                                        <!--Mouse Literature Evidence (MGI) - Yes or empty-->
                                        <td>
                                            <c:if test="${associationSummary.hasLiteratureEvidence}">Yes</c:if>
                                            </td>                                    

                                            <!--Mouse Phenotype Evidence (Phenodigm)-->
                                            <td>
                                            <c:if test="${0.0 != associationSummary.bestModScore}">
                                                <b style="color:#EF7B0B">${associationSummary.bestModScore}</b>   
                                            </c:if>   
                                        </td>
                                        <td>
                                            <c:if test="${0.0 != associationSummary.bestHtpcScore}">
                                                <b style="color:#EF7B0B">${associationSummary.bestHtpcScore}</b>
                                            </c:if>                                        
                                        </td>
                                        <td id="toggleButton" title="Click to display phenotype terms"><i class="fa fa-plus-square"></i></td>
                                    </tr>
                                    <tr id="P${geneIdentifier.databaseAcc}_${association.diseaseIdentifier.databaseAcc}">                                      
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
