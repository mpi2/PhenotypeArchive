<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<c:choose>
<c:when test="${alleleProducts.size() > 0}">
<table class="reduce nonwrap">
        <thead>
                <tr>
                        <th>Product (new)</th>
                        <th style="width:15%">Type</th>
                        <th>Strain of Origin</th>
                        <th>MGI Allele Name</th>
                        <th>Allele Description</th>
                        <th>Product Details</th>
                        <th style="width:20%">Order / Contact</th>
                </tr>
        </thead>
        <tbody>
                <c:forEach var="alleleProduct" items="${alleleProducts2}" varStatus="status">
                        <tr>
                            <td>${alleleProduct["product"]}</td>
                            <td>${alleleProduct["allele_description"]}</td>
                            <td>${alleleProduct["genetic_background"]}</td>
                            <td>${alleleProduct["mgi_allele_name"]}</td>
                            <td>
                                <c:if test="${not empty alleleProduct['allele_image']}">
                                        <div style="padding:3px;"><a class="fancybox" target="_blank" href="${alleleProduct['allele_image']}?simple=true.jpg">
                                                <i class="fa fa-th-list fa-lg"></i></a><span>&nbsp;&nbsp;image</span></div>
                                </c:if>

                                <c:if test="${not empty alleleProduct['genbank_file']}">
                                        <div style="padding:3px;"><a href="${alleleProduct['genbank_file']}"><i class="fa fa-file-text fa-lg"></i></a><span>&nbsp;&nbsp;&nbsp;genbank file</span></div>
                                </c:if>
                            </td>
                            <td>

                                <c:if test="${not empty alleleProduct['ikmc_project_id']}">
                                <a title="project page" href="http://www.mousephenotype.org/martsearch_ikmc_project/martsearch/ikmc_project/${alleleProduct['ikmc_project_id']}"><i class="fa fa-clipboard"></i></a>
                                </c:if>

                                <c:if test="${not empty alleleProduct['mgi_accession_id']}">
                                <c:if test="${not empty alleleProduct['allele_name']}">
                                <a title="allele project page" href="${baseUrl}/alleles/${alleleProduct['mgi_accession_id']}/${alleleProduct['allele_name']}"><i class="fa fa-clipboard fa-2x"></i></a>
                                </c:if>
                                </c:if>

                            </td>
                            <td>

                                <c:forEach var="order" items="${alleleProduct['orders']}" varStatus="orderx">
                                    <a class="btn btn-sm" href="${order['url']}"> <i class="fa fa-shopping-cart"></i>${order['name']}</a>&nbsp;
                                </c:forEach>
                                <c:forEach var="contact" items="${alleleProduct['contacts']}" varStatus="contactx">
                                    <a class="btn btn-sm" href="${contact['url']}"> <i class="fa fa-envelope"></i>${contact['name']}</a>
                                </c:forEach>

                            </td>
                        </tr>


                </c:forEach>
        </tbody>

</table>
</c:when>
<c:otherwise>
    <div><p>No products are available for this gene.</p></div>
</c:otherwise>
</c:choose>
