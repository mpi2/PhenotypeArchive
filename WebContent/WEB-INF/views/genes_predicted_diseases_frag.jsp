<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page pageEncoding="UTF-8" %>

<h2 class="title" id="section-potential-disease-models">Potential Disease Models <small class="sub">predicted by phenotypic similarity</small>
    <a href='http://www.sanger.ac.uk/resources/databases/phenodigm/'></a>
    <span class="documentation">
        <a href='${baseUrl}/documentation/disease-help.html#details' class="mpPanel">
            <i class="fa fa-question-circle pull-right"></i>
        </a>
    </span>
</h2>
<div class="inner">
    <!--The following diseases are associated with ${gene.symbol} by phenotypic similarity-->
    <table id="predicted_diseases" class="table tableSorter disease">
        <thead>
            <tr>
                <th><span class="main">Disease Name</span></th>
                <th><span class="main">Source</span></th>
                <th>In Disease Locus</th>
                <th><span class="main">MGI</span><span class="sub">Mouse Phenotype Evidence (Phenodigm)</span></th>
                <th><span class="main">IMPC</span><span class="sub">Mouse Phenotype Evidence (Phenodigm)</span></th>
                <th></th>
            </tr>
        </thead>                        
        <tbody>
            <c:forEach var="association" items="${phenotypicDiseaseAssociations}" varStatus="loop">
                <c:set var="associationSummary" value="${association.associationSummary}"></c:set>
                <tr id="${disease.diseaseIdentifier.databaseAcc}" targetRowId="P${geneIdentifier.databaseAcc}_${association.diseaseIdentifier.databaseAcc}" requestpagetype= "gene" geneid="${geneIdentifier.compoundIdentifier}" diseaseid="${association.diseaseIdentifier.compoundIdentifier}">
                    <!--Disease Name-->
                    <td><a href="${baseUrl}/disease/${association.diseaseIdentifier}">${association.diseaseTerm}</a></td>
                    <!--Source-->
                    <td>
                        <a id="diseaseId" href="${association.diseaseIdentifier.externalUri}">${association.diseaseIdentifier}</a>
                    </td>
                    <!--In disease locus - Yes or empty-->
                    <td>
                        <c:if test="${associationSummary.inLocus}">
                            Yes
                        </c:if>
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
            </c:forEach>
        </tbody>
    </table>
</div>
  
    <script src="http://code.jquery.com/jquery-1.11.1.min.js"></script>
    <script src="http://cdn.datatables.net/1.10.4/js/jquery.dataTables.min.js"></script>

    <script type="text/javascript">
        $(document).ready(function () {
            var table = $('#predicted_diseases').DataTable({

                order: [[2, 'desc'], [4, 'desc'], [3, 'desc']]
            });

            $('#predicted_diseases tbody').on('click', 'tr', function () {
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