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
                	<li><a href="#potential_mouse_models">Potential MouseModels</a></li>
               
            </ul>
            
            <div class="clear"></div>
            
        </div>
        
    </div>
	</jsp:attribute>

    <jsp:attribute name="breadcrumb">
        &nbsp;&raquo; <a href="${baseUrl}/search#q=*:*&core=disease&fq=type:disease"> Diseases</a>&nbsp;&raquo; ${disease.diseaseId}
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
            };
            
            /*
             * Toggles the row icon between a "fa-minus-square" and a "fa-plus-square".
             */
            function toggleRowIcon(row){
                    var rowOpen = $(row).find("td#toggleButton i").hasClass("fa-minus-square");
                    if (rowOpen) {
                        $(row).find("td#toggleButton i").removeClass("fa-minus-square").addClass("fa-plus-square");                        
                    } 
                    else {
                        $(row).find("td#toggleButton i").removeClass("fa-plus-square").addClass("fa-minus-square");                                                
                    }
            };
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
                        <c:when test="${empty knownGeneAssociationSummaries}">
                            -
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="association" items="${knownGeneAssociationSummaries}" varStatus="loop">
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
                        <c:when test="${empty knownGeneAssociationSummaries}">
                            -
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="association" items="${knownGeneAssociationSummaries}" varStatus="loop">
                                <c:set var="mouseGeneIdentifier" value="${association.modelGeneIdentifier}"></c:set>
                                <!--Mouse Gene Symbol-->
                                <a href="../gene/${mouseGeneIdentifier.databaseCode}:${mouseGeneIdentifier.databaseAcc}">${mouseGeneIdentifier.geneSymbol}</a>
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
            <h2 class="title" id="potential_mouse_models">Potential Mouse Models <a href='http://www.sanger.ac.uk/resources/databases/phenodigm/'></a><span class='documentation'><a href='${baseUrl}/documentation/disease-help.html#details' class='mpPanel'><i class="fa fa-question-circle pull-right"></i></a></span></h2>
            <div class="inner">                
                <c:choose>
                    <c:when test="${empty phenotypeAssociations}">
                        No potential mouse models associated with ${disease.diseaseId} by phenotypic similarity.
                    </c:when>
                    <c:otherwise>
                        <!--The following genes are associated with ${disease.diseaseId} by phenotypic similarity.-->
                        <table id="phenotypes" class="table tableSorter"> 
                            <thead>
                                <tr>
                                    <!--<th>Human Gene Symbol</th>-->
                                    <th><span class="main">Mouse Gene Symbol</span></th>
                                    <th>Disease Gene Ortholog</th>
                                    <th>Syntenic Disease Locus</th>
                                    <th>Mouse Literature Evidence (MGI)</th>
                                    <th><span class="main">MGI</span><span class="sub">Mouse Phenotype Evidence (Phenodigm)</span></th>
                                    <th><span class="main">IMPC</span><span class="sub">Mouse Phenotype Evidence (Phenodigm)</span></th>
                                    <th></th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="association" items="${phenotypeAssociations}">
                                    <c:set var="mouseGeneIdentifier" value="${association.modelGeneIdentifier}"></c:set>
                                    <c:set var="associationSummary" value="${association.associationSummary}"></c:set>
                                    <tr id="${mouseGeneIdentifier.compoundIdentifier}" targetRowId="P${humanGeneIdentifier.databaseAcc}_${mouseGeneIdentifier.databaseAcc}_${disease.diseaseIdentifier.databaseAcc}" requestpagetype= "disease" geneid="${mouseGeneIdentifier.compoundIdentifier}" diseaseid="${disease.diseaseIdentifier.compoundIdentifier}">
                                        <td>
                                            <!--Mouse Gene Symbol-->
                                            <a href="../gene/${mouseGeneIdentifier.databaseCode}:${mouseGeneIdentifier.databaseAcc}">${mouseGeneIdentifier.geneSymbol}</a> 
                                        </td>

                                        <!--Associated in Human - Yes or empty-->   
                                        <td>
                                            <c:if test="${associationSummary.associatedInHuman}">Yes</c:if>
                                        </td>  
                                        <!--in syntenic disease locus - Yes or empty-->
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
                                    <tr id="P${humanGeneIdentifier.databaseAcc}_${mouseGeneIdentifier.databaseAcc}_${disease.diseaseIdentifier.databaseAcc}">
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
