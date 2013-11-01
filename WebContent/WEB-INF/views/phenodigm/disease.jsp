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
            &nbsp;&raquo; <a href="${baseUrl}/phenodigm/home">PhenoDigm</a>&nbsp;&raquo; <a href="${baseUrl}/phenodigm/disease"> Diseases</a>&nbsp;&raquo; ${disease.diseaseId}
    </jsp:attribute>

    <jsp:body>
        
    <script type="text/javascript" charset="utf-8" src="${baseUrl}/js/vendor/jquery-1.7.2.min.js"></script>

    <script type="text/javascript">
        $(document).ready(function() {
            $("#phenotypes tr:odd").addClass("odd");
            $("#phenotypes tr:not(.odd)").hide();
            $("#phenotypes tr:first-child").show();

            $("#phenotypes tr.odd").click(function() {
                $(this).next("tr").toggle();
                $(this).find(".arrow").toggleClass("up");
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
            });
            //$("#report").jExpand();
        });
    </script>
        <div class='topic'>Gene associations for ${disease.term} <a href="http://omim.org/entry/${disease.diseaseIdentifier.databaseAcc}">(${disease.diseaseId})</a> </div>

        <div class="row-fluid dataset">
            <div class="container span12">
                <h4 class="topic">Curated Gene Associations</h4>
                <table id="phenotypes" class="table table-striped"> 
                    <thead>
                        <tr>
                            <th>Human Gene Symbol</th>
                            <th>Mouse Gene Symbol</th>
                            <th>Associated in Human</th>
                            <th>Mouse Literature Evidence (MGI)</th>
                            <th>Mouse Phenotype Evidence (Phenodigm)</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="association" items="${curatedAssociations}">
                            <c:set var="mouseGeneIdentifier" value="${association.mouseGeneIdentifier}"></c:set>
                            <c:set var="humanGeneIdentifier" value="${association.humanGeneIdentifier}"></c:set>
                            <c:set var="diseaseAssociations" value="${association.curatedAssociations}"></c:set>
                                <tr>
                                    <td>
                                        <!--Human Gene Symbol-->
                                        <a href="http://www.genenames.org/data/hgnc_data.php?hgnc_id=${humanGeneIdentifier.databaseAcc}">${humanGeneIdentifier.geneSymbol}</a> 
                                </td>
                                <td>
                                    <!--Mouse Gene Symbol-->
                                    <a href="../gene/${mouseGeneIdentifier.databaseCode}:${mouseGeneIdentifier.databaseAcc}">${mouseGeneIdentifier.geneSymbol}</a> 
                                </td>

                                <!--Associated in Human - Yes or empty-->   
                                <td>
                                    <c:if test="${association.associatedInHuman}">Yes</c:if>
                                    </td>  
                                    <td>
                                        <!--Mouse Literature Evidence (MGI) - Yes or empty-->
                                    <c:if test="${association.hasLiteratureEvidence}">Yes</c:if>
                                    </td>                                    
                                    <!--Mouse Phenotype Evidence (Phenodigm)-->
                                    <td>
                                        <b style="color:#FF9000">${association.bestScore}</b>   
                                </td>
                                <td><img style="cursor:help;color:#D6247D;" rel="tooltip" data-placement="top" title="Click anywhere on row display phenotype terms" alt="Click row to display phenotype terms" src="${baseUrl}/img/plus.gif" /></td>                                     
                            </tr>
                            <tr>
                                <td>
                                    <h5>${disease.diseaseId} Disease Phenotype Terms</h5>
                                    <c:forEach var="hpTerm" items="${disease.phenotypeTerms}">
                                        ${hpTerm}<br>
                                    </c:forEach>
                                </td>
                                <td></td>
                                <td colspan="5">
                                    <c:if test="${not empty association.curatedAssociations}">
                                        <h5>${disease.diseaseId} Associated Mouse Models (MGI curated)</h5>
                                        <c:forEach var="diseaseAssociation" items="${association.curatedAssociations}" varStatus="loop">
                                            <c:set var="mouseModel" value="${diseaseAssociation.mouseModel}"></c:set>
                                            <b style="color:#FF9000">${diseaseAssociation.modelToDiseaseScore}</b>: ${mouseModel.allelicCompositionLink} ${mouseModel.geneticBackground} (Source: MGI curation)</br>
                                            <c:forEach var="phenotypeTerm" items="${mouseModel.phenotypeTerms}">
                                                ${phenotypeTerm}<br/>
                                            </c:forEach>
                                            <br/>
                                        </c:forEach>
                                    </c:if>
                                    <c:if test="${not empty association.phenotypicAssociations}">
                                        <h5>${disease.diseaseId} Associated Mouse Models (PhenoDigm predicted)</h5>
                                        <c:forEach var="diseaseAssociation" items="${association.phenotypicAssociations}" varStatus="loop">
                                            <c:set var="mouseModel" value="${diseaseAssociation.mouseModel}"></c:set>
                                            <b style="color:#FF9000">${diseaseAssociation.modelToDiseaseScore}</b>: ${mouseModel.allelicCompositionLink} ${mouseModel.geneticBackground} (Source: PhenoDigm)</br>
                                            <c:forEach var="phenotypeTerm" items="${mouseModel.phenotypeTerms}">
                                                ${phenotypeTerm}<br/>
                                            </c:forEach>
                                            <br/>                                        
                                        </c:forEach>
                                    </c:if>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table> 
            </div>
        </div>
        
        <div class="row-fluid dataset">
            <div class="container span12">
                <h4 class="topic">Phenotypic Gene Associations <a href='http://www.sanger.ac.uk/resources/databases/phenodigm/'></a></h4>
                <table id="predictedPhenotypes" class="table table-striped"> 
                    <thead>
                        <tr>
                            <th>Human Gene Symbol</th>
                            <th>Mouse Gene Symbol</th>
                            <th>Associated in Human</th>
                            <th>Mouse Literature Evidence (MGI)</th>
                            <th>Mouse Phenotype Evidence (Phenodigm)</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody>

                        <c:forEach var="association" items="${phenotypeAssociations}">
                            <c:set var="mouseGeneIdentifier" value="${association.mouseGeneIdentifier}"></c:set>
                            <c:set var="humanGeneIdentifier" value="${association.humanGeneIdentifier}"></c:set>
                            <c:set var="diseaseAssociations" value="${association.phenotypicAssociations}"></c:set>
                                <tr>
                                    <td>
                                        <!--Human Gene Symbol-->
                                        <a href="http://www.genenames.org/data/hgnc_data.php?hgnc_id=${humanGeneIdentifier.databaseAcc}">${humanGeneIdentifier.geneSymbol}</a> 
                                </td>
                                <td>
                                    <!--Mouse Gene Symbol-->
                                    <a href="../gene/${mouseGeneIdentifier.databaseCode}:${mouseGeneIdentifier.databaseAcc}">${mouseGeneIdentifier.geneSymbol}</a> 
                                </td>

                                <!--Associated in Human - Yes or empty-->   
                                <td>
                                    <c:if test="${association.associatedInHuman}">Yes</c:if>
                                    </td>  
                                    <td>
                                        <!--Mouse Literature Evidence (MGI) - Yes or empty-->
                                    <c:if test="${association.hasLiteratureEvidence}">Yes</c:if>
                                    </td>                                    
                                    <!--Mouse Phenotype Evidence (Phenodigm)-->
                                    <td>
                                        <b style="color:#FF9000">${association.bestScore}</b>   
                                </td>
                                <td><img style="cursor:help;color:#D6247D;" rel="tooltip" data-placement="top" title="Click anywhere on row display phenotype terms" alt="Click row to display phenotype terms" src="${baseUrl}/img/plus.gif" /></td>                                     
                            </tr>
                            <tr>
                                <td>
                                    <h5>${disease.diseaseId} Disease Phenotype Terms</h5>
                                    <c:forEach var="hpTerm" items="${disease.phenotypeTerms}">
                                        ${hpTerm}<br>
                                    </c:forEach>
                                </td>
                                <td></td>
                                <td colspan="5">
                                    <c:if test="${not empty association.curatedAssociations}">
                                        <h5>${disease.diseaseId} Associated Mouse Models (MGI curated)</h5>
                                        <c:forEach var="diseaseAssociation" items="${association.curatedAssociations}" varStatus="loop">
                                            <c:set var="mouseModel" value="${diseaseAssociation.mouseModel}"></c:set>
                                            <b style="color:#FF9000">${diseaseAssociation.modelToDiseaseScore}</b>: ${mouseModel.allelicCompositionLink} ${mouseModel.geneticBackground} (Source: MGI curation)</br>
                                            <c:forEach var="phenotypeTerm" items="${mouseModel.phenotypeTerms}">
                                                ${phenotypeTerm}<br/>
                                            </c:forEach>
                                            <br/>
                                        </c:forEach>
                                    </c:if>
                                    <c:if test="${not empty association.phenotypicAssociations}">
                                        <h5>${disease.diseaseId} Associated Mouse Models (PhenoDigm predicted)</h5>
                                        <c:forEach var="diseaseAssociation" items="${association.phenotypicAssociations}" varStatus="loop">
                                            <c:set var="mouseModel" value="${diseaseAssociation.mouseModel}"></c:set>
                                            <b style="color:#FF9000">${diseaseAssociation.modelToDiseaseScore}</b>: ${mouseModel.allelicCompositionLink} ${mouseModel.geneticBackground} (Source: PhenoDigm)</br>
                                            <c:forEach var="phenotypeTerm" items="${mouseModel.phenotypeTerms}">
                                                ${phenotypeTerm}<br/>
                                            </c:forEach>
                                            <br/>                                        
                                        </c:forEach>
                                    </c:if>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </jsp:body>
</t:genericpage>
