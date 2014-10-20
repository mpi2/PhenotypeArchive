<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<c:choose>
<c:when test="${alleleProducts.size() > 0}">
<table class="reduce nonwrap">
        <thead>
                <tr>
                        <th>Product</th>
                        <th>Type</th>
                        <th>Strain of Origin</th>
                        <th>MGI Allele Name</th>
                        <th>Allele Description</th>
                        <th>Product Details</th>
                        <th>Order / Contact</th>
                </tr>
        </thead>
        <tbody>
                <c:forEach var="alleleProduct" items="${alleleProducts}" varStatus="status">
                        <tr>
                        <c:choose>
                                <c:when test='${alleleProduct["type"].equals("gene")}'>
                                    <td class="centered" colspan="7">Additional Targeting vectors are available for this gene - see links for details: ${alleleProduct["vectorProjectHtml"]}</td>
                                </c:when>
                                <c:otherwise>

                                    <c:if test="${not empty alleleProduct['product_url']}">
                                        <td><a title="click to visit solr" href="${alleleProduct['product_url']}">${alleleProduct["product"]}</a></td>
                                    </c:if>
                                    <c:if test="${empty alleleProduct['product_url']}">
                                        <td>${alleleProduct["product"]}</td>
                                    </c:if>                                    

                                    <td>${alleleProduct["alleleType"]}</td>
                                        <td>${alleleProduct["strainOfOrigin"]}</td>
                                        <td>${alleleProduct["mgiAlleleName"]}</td>
                                        <td>
                                            <div style="padding:3px;"><a class="fancybox" target="_blank" href="${alleleProduct['alleleMap']}?simple=true.jpg" title="<a href='${alleleProduct['alleleMap']}?simple=true.jpg'>Download this image</a>"><i class="fa fa-th-list fa-lg"></i></a><span>&nbsp;&nbsp;image</span></div>
                                            <div style="padding:3px;"><a href="${alleleProduct['alleleGenbankFile']}"><i class="fa fa-file-text fa-lg"></i></a><span>&nbsp;&nbsp;&nbsp;genbank file</span></div>
                                        </td>
                                        <td>

                                            <a title="project page" href="http://www.mousephenotype.org/martsearch_ikmc_project/martsearch/ikmc_project/${alleleProduct['ikmcProjectId']}"><i class="fa fa-clipboard"></i></a>

                                            <a title="allele project page" href="${baseUrl}/alleles/${alleleProduct['mgi_accession_id']}/${alleleProduct['mgiAlleleNameStrip']}"><i class="fa fa-clipboard fa-2x"></i></a>

                                        </td>
                                        <td>${alleleProduct["orderHtml"]}</td>
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
