
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page pageEncoding="UTF-8" %>




<h2 class="title" id="orthologous_mouse_models">Mouse Models <small class="sub">associated by gene orthology</small>
    <a href='http://www.sanger.ac.uk/resources/databases/phenodigm/'></a>
    <span class='documentation'>
        <a href='${baseUrl}/documentation/disease-help.html#details' class='mpPanel'>
            <i class="fa fa-question-circle pull-right"></i>
        </a>
    </span>
</h2>
<div class="inner">                
    <c:choose>
        <c:when test="${empty orthologousGeneAssociations}">
            No mouse models associated with ${disease.diseaseId} by orthology to a human gene.
        </c:when>
        <c:otherwise>
            <!--The following genes are associated with ${disease.diseaseId} by phenotypic similarity.-->
            <table id="orthologous_phenotypes" class="table tablesorter disease"> 
                <thead>
                    <tr>
                        <!--<th>Human Gene Symbol</th>-->
                        <th><span class="main">Mouse Gene Symbol</span></th>
                        <th>Disease Gene Ortholog</th>
                        <!--<th>In Disease Locus</th>-->
                        <!--<th>Mouse Literature Evidence (MGI)</th>-->
                        <th><span class="main">MGI</span><span class="sub">Phenotype Similarity Score</span></th>
                        <th><span class="main">IMPC</span><span class="sub">Phenotype Similarity Score</span></th>            
                        <th></th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="association" items="${orthologousGeneAssociations}">
                        <c:set var="mouseGeneIdentifier" value="${association.modelGeneIdentifier}"></c:set>
                        <c:set var="humanGeneIdentifier" value="${association.hgncGeneIdentifier}"></c:set>
                        <c:set var="associationSummary" value="${association.associationSummary}"></c:set>
                        <tr id="${mouseGeneIdentifier.compoundIdentifier}" targetRowId="P${humanGeneIdentifier.databaseAcc}_${mouseGeneIdentifier.databaseAcc}_${disease.diseaseIdentifier.databaseAcc}" requestpagetype= "disease" geneid="${mouseGeneIdentifier.compoundIdentifier}" diseaseid="${disease.diseaseIdentifier.compoundIdentifier}">
                            <td>
                                <!--Mouse Gene Symbol-->
                                <a href="${baseUrl}/genes/${mouseGeneIdentifier.databaseCode}:${mouseGeneIdentifier.databaseAcc}">${mouseGeneIdentifier.geneSymbol}</a> 
                            </td>

                            <!--Associated in Human - Yes or empty-->   
                            <td>
                                <c:if test="${associationSummary.associatedInHuman}">
                                    <!--Human Gene Symbol-->    
                                    <a href="${humanGeneIdentifier.externalUri}">${humanGeneIdentifier.geneSymbol}</a>
                                </c:if>
                            </td>  
                            <!--in syntenic disease locus - Yes or empty-->
                            <!--                                        <td>
                            <c:if test="${associationSummary.inLocus}">
                                Yes
                            </c:if>
                        </td>-->
                            <!--Mouse Literature Evidence (MGI) - Yes or empty-->
                            <!--                                        <td>
                            <c:if test="${associationSummary.hasLiteratureEvidence}">Yes</c:if>
                        </td>                                    -->

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
<!--                        <tr id="P${humanGeneIdentifier.databaseAcc}_${mouseGeneIdentifier.databaseAcc}_${disease.diseaseIdentifier.databaseAcc}">
                        </tr>-->
                    </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>
</div>


<script type="text/javascript">
    $(document).ready(function () {
        var table = $('#orthologous_phenotypes').DataTable({
            "paging": false,
            "info": false,
            "searching": false,
            "order": [[4, 'desc'], [3, 'desc']]
        });

        $('#orthologous_phenotypes tbody').on('click', 'tr', function () {
            var tr = $(this).closest('tr');
            var row = table.row(tr);

            if (row.child.isShown()) {
                // This row is already open - close it
                row.child.hide();
                tr.removeClass('shown');
                tr.find("td#toggleButton i").removeClass("fa-minus-square").addClass("fa-plus-square");
            }
            else {
                // Open this row
                row.child(makeChildRow(tr)).show();
                row.child(insertDiseaseAssociations(tr)).show();
                tr.addClass('shown');
                tr.find("td#toggleButton i").removeClass("fa-plus-square").addClass("fa-minus-square");
            }
        });
    });
</script>