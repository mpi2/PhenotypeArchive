<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page pageEncoding="UTF-8" %>

<thead>
    <tr>
        <!--<th>Human Gene Symbol</th>-->
        <th><span class="main">Mouse Gene Symbol</span></th>
        <!--<th>Disease Gene Ortholog</th>-->
        <th>In Disease Locus</th>
        <!--<th>Mouse Literature Evidence (MGI)</th>-->
        <th><span class="main">MGI</span><span class="sub">Phenotype Similarity Score</span></th>
        <th><span class="main">IMPC</span><span class="sub">Phenotype Similarity Score</span></th>
        <th></th>
    </tr>
</thead>
<tbody>
    <c:forEach var="association" items="${phenotypicGeneAssociations}">
        <c:set var="mouseGeneIdentifier" value="${association.modelGeneIdentifier}"></c:set>
        <c:set var="humanGeneIdentifier" value="${association.hgncGeneIdentifier}"></c:set>
        <c:set var="associationSummary" value="${association.associationSummary}"></c:set>
        <tr id="${mouseGeneIdentifier.compoundIdentifier}" targetRowId="P${humanGeneIdentifier.databaseAcc}_${mouseGeneIdentifier.databaseAcc}_${disease.diseaseIdentifier.databaseAcc}" requestpagetype= "disease" geneid="${mouseGeneIdentifier.compoundIdentifier}" diseaseid="${disease.diseaseIdentifier.compoundIdentifier}">
            <td>
                <!--Mouse Gene Symbol-->
                <a href="${baseUrl}/genes/${mouseGeneIdentifier.databaseCode}:${mouseGeneIdentifier.databaseAcc}">${mouseGeneIdentifier.geneSymbol}</a> 
            </td>

            <!--in syntenic disease locus - Yes or empty-->
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

