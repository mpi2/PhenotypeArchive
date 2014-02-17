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
                                    <td colspan="8">Additional Targeting vectors are available for this gene - see links for details: ${alleleProduct["vectorProjectHtml"]}</td>                                  
                                </c:when>
                                <c:otherwise>
                                        <td>${alleleProduct["product"]}</td>
                                        <td style="padding-left:10px">${alleleProduct["alleleType"]}</td>
                                        <td style="padding-left:10px">${alleleProduct["strainOfOrigin"]}</td>
                                        <td style="padding-left:10px">${alleleProduct["mgiAlleleName"]}</td>
                                        <td style="padding-left:10px"><div><a href="${alleleProduct["alleleMap"]}" target="_blank"><img width="400" src="${alleleProduct["alleleMap"]}" alt="allele image"></a></div></td>
                                        <td style="padding-left:10px"><a href="${alleleProduct["alleleGenbankFile"]}">Genbank File</a></td>
                                        <td style="padding-left:10px"><a href="http://www.mousephenotype.org/martsearch_ikmc_project/martsearch/ikmc_project/${alleleProduct["ikmcProjectId"]}">${alleleProduct["ikmcProjectId"]}</a></td>
                                        <td style="padding-left:10px"><ul>${alleleProduct["orderHtml"]}</ul></td>
                                </c:otherwise>
                        </c:choose>
                        </tr>

                        
                </c:forEach>
        </tbody>
   <!--                         
        <thead>
                <tr>
                        <th>Product</th>
                        <th>Type</th>
                        <th>Strain of Origin</th>
                        <th>MGI Allele Name</th>
                        <th>Allele Image & Genbank file</th>
                        <th>Product Details</th>
                        <th>Order</th>
                </tr>
        </thead>
        <tbody>                                
                <tr>
                        <td>ES Cell</td>
                        <td>Targeted Non Conditional</td>
                        <td>C57BL/6N</td>
                        <td>Akt2<sup>tm1e(KOMP)Wtsi</sup></td>
                        <td>
                                <div><a class="fancybox" target="_blank" href="https://www.i-dcc.org/imits/targ_rep/alleles/760/allele-image?simple=true"><i class="fa fa-th-list fa-lg"></i></a></div>
                                <div><a href="https://www.i-dcc.org/imits/targ_rep/alleles/26029/escell-clone-genbank-file"><i class="fa fa-file-text fa-lg"></i></a></div>
                        </td>
                        <td><a href="http://www.mousephenotype.org/martsearch_ikmc_project/martsearch/ikmc_project/25071"><i class="fa fa-clipboard fa-lg"></i></a></td>
                        <td><ul><li><a href="http://www.eummcr.org/order.php">EUMMCR</a></li></ul></td>
                </tr>
        </tbody>
-->

</table>
</c:when>
<c:otherwise>
    <div><p>No products are available for this gene.</p></div>
</c:otherwise>
</c:choose>
