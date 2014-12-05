<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page pageEncoding="UTF-8" %>


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
    <c:forEach var="association" items="${orthologousDiseaseAssociations}" varStatus="loop">
        <c:set var="associationSummary" value="${association.associationSummary}"></c:set>
        <tr id="${disease.diseaseIdentifier.databaseAcc}" targetRowId="P${geneIdentifier.databaseAcc}_${association.diseaseIdentifier.databaseAcc}" requestpagetype= "gene" geneid="${geneIdentifier.compoundIdentifier}" diseaseid="${association.diseaseIdentifier.compoundIdentifier}">
            <!--Disease Name-->
            <td><a href="${baseUrl}/disease/${association.diseaseIdentifier}">${association.diseaseTerm}</a></td>
            <!--Source-->
            <td>
                <a id="diseaseId" href="${association.diseaseIdentifier.externalUri}">${association.diseaseIdentifier}</a>
            </td>
            <!--In syntenic disease locus - Yes or empty-->
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
