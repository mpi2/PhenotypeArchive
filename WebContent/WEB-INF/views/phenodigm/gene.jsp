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
        &nbsp;&raquo; <a href="${baseUrl}/phenodigm/home">PhenoDigm</a>&nbsp;&raquo; <a href="${baseUrl}/phenodigm/gene/"> Genes</a> &nbsp;&raquo; ${geneIdentifier.geneSymbol}
    </jsp:attribute>

    <jsp:body>
        
        <script src="https://www.mousephenotype.org/js/general/toggle.js"></script>
        <script type="text/javascript" charset="utf-8" src="${baseUrl}/js/vendor/DataTables-1.9.4/jquery.dataTables.min.js"></script>
        <script type="text/javascript" charset="utf-8" src="${baseUrl}/js/vendor/jquery-1.7.2.min.js"></script>
        <link rel="stylesheet" href="http://code.jquery.com/ui/1.10.3/themes/smoothness/jquery-ui.css" />
        
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
    
        <div class='topic'>Disease Associations for <a href="https://www.mousephenotype.org/data/genes/${geneIdentifier.databaseCode}:${geneIdentifier.databaseAcc}">${geneIdentifier.geneSymbol}</a> (human ortholog <a href="http://www.genenames.org/data/hgnc_data.php?hgnc_id=${humanOrtholog.databaseAcc}">${humanOrtholog.geneSymbol}</a>)</div>
        
            <div class="row-fluid dataset">
                <div class="container span12">
                    <h4 class="topic">Curated Disease Associations <a href='http://www.sanger.ac.uk/resources/databases/phenodigm/'></a></h4>
                    <table id="phenotypes" class="table table-striped">
                        The following diseases are associated with ${geneIdentifier.geneSymbol} from external resources*
                        <thead>
                            <tr>
                                <th>Disease Name</th>
                                <th>Source</th>
                                <th>Associated in Human</th>
                                <th>Mouse Literature Evidence (MGI)</th>
                                <th>Mouse Phenotype Evidence (Phenodigm)</th>
                                <th></th>
                            </tr>
                        </thead>                        
                        <tbody> 
                            <c:forEach var="association" items="${curatedAssociations}" varStatus="loop">
                                <c:set var="disease" value="${association.disease}"></c:set>
                                <tr>
                                    <!--Disease Name-->
                                    <td><a href="../disease/${disease.diseaseId}">${disease.term}</a></td>
                                    <!--Source-->
                                    <td><a href="http://omim.org/entry/${disease.diseaseIdentifier.databaseAcc}">${disease.diseaseId}</a></td>
                                    <!--Associated in Human --> 
                                    <td>
                                        <c:if test="${association.associatedInHuman}">Yes</c:if>
                                    </td>                                    
                                    <!--Mouse Literature Evidence (MGI)-->
                                    <td><c:if test="${association.hasLiteratureEvidence}">Yes</c:if></td>
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
                    <h4 class="topic">Phenotypic Disease Associations <a href='http://www.sanger.ac.uk/resources/databases/phenodigm/'></a></h4>
                    <table id="predictedPhenotypes" class="table table-striped">
                        The following diseases are associated with ${geneIdentifier.geneSymbol} by phenotypic similarity
                        <thead>
                            <tr>
                                <th>Disease Name</th>
                                <th>Source</th>
                                <th>Associated in Human</th>
                                <th>Mouse Literature Evidence (MGI)</th>
                                <th>Mouse Phenotype Evidence (Phenodigm)</th>
                                <th></th>
                            </tr>
                        </thead>                        
                        <tbody>
                            <c:forEach var="association" items="${phenotypeAssociations}" varStatus="loop">
                                <c:set var="disease" value="${association.disease}"></c:set>
                                <tr>
                                    <!--Disease Name-->
                                    <td><a href="../disease/${disease.diseaseId}">${disease.term}</a></td>
                                    <!--Source-->
                                    <td><a href="http://omim.org/entry/${disease.diseaseIdentifier.databaseAcc}">${disease.diseaseId}</a></td>
                                    <!--Associated in Human --> 
                                    <td>
                                        <c:if test="${association.associatedInHuman}">Yes</c:if>
                                    </td>                                    
                                    <!--Mouse Literature Evidence (MGI)-->
                                    <td><c:if test="${association.hasLiteratureEvidence}">Yes</c:if></td>
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
