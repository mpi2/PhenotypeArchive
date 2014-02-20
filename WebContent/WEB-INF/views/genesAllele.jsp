<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<c:choose>
<c:when test="${alleleProducts.size() > 0}">			 
<table class="reduce nonwrap">
        <thead>
                <tr>
                        <th>Product</th>
                        <th style="padding-left:10px">Type</th>
                        <th style="padding-left:10px">Strain of Origin</th>
                        <th style="padding-left:10px">MGI Allele Name</th>
                        <th style="padding-left:10px">Allele Map</th>
                        <th style="padding-left:10px">Allele Sequence</th>
                        <th style="padding-left:10px">Product Details</th>
                        <th style="padding-left:10px">Order</th>
                </tr>
        </thead>
        <tbody>
                <c:forEach var="alleleProduct" items="${alleleProducts}" varStatus="status">
                        <tr>
                        <c:choose>
                                <c:when test='${alleleProduct["type"].equals("gene")}'>
                                    <td colspan="8" align="center">Additional Targeting vectors are available for this gene - see links for details: ${alleleProduct["vectorProjectHtml"]}</td>                                  
                                </c:when>
                                <c:otherwise>
                                        <td>${alleleProduct["product"]}</td>
                                        <td style="padding-left:10px">${alleleProduct["alleleType"]}</td>
                                        <td style="padding-left:10px">${alleleProduct["strainOfOrigin"]}</td>
                                        <td style="padding-left:10px">${alleleProduct["mgiAlleleName"]}</td>
                                        <td style="padding-left:10px"><div><a href="${alleleProduct['alleleMap']}?simple=true" target="_blank"><img width="400" src="${alleleProduct['alleleMap']}?simple=true" alt="allele image"></a></div></td>
                                        <td style="padding-left:10px"><a href="${alleleProduct['alleleGenbankFile']}">Genbank File</a></td>
                                        <td style="padding-left:10px"><a href="http://www.mousephenotype.org/martsearch_ikmc_project/martsearch/ikmc_project/${alleleProduct['ikmcProjectId']}">${alleleProduct["ikmcProjectId"]}</a></td>
                                        <td style="padding-left:10px"><ul>${alleleProduct["orderHtml"]}</ul></td>
                                </c:otherwise>
                        </c:choose>
                        </tr>

                        
                </c:forEach>
        </tbody>

</table>
</c:when>
<c:otherwise>
    <div><p>No products are available for this gene.</p></div>
</c:otherwise>
</c:choose>
