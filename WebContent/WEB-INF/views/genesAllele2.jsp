<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<c:choose>
<c:when test="${alleleProducts2.size() > 0}">
<table class="reduce nonwrap">        
        <thead>
                <tr>
                        <th>Product</th>
                        <th style="width:15%">Allele</th>
                        <th>Strain of Origin</th>
                        <th>MGI Allele Name</th>
                        <th>Description</th>
                        <th>Product Details</th>
                        <th style="width:20%">Order / Contact</th>
                </tr>
        </thead>
        <tbody>
                <c:forEach var="alleleProduct" items="${alleleProducts2}" varStatus="status">
                        <tr>
                            
                            <c:choose>
                                <c:when test="${not empty alleleProduct['product_url'] and not empty debug}">
                                    <td><a title="click to visit solr" href="${alleleProduct['product_url']}">${alleleProduct["product"]}</a></td>
                                </c:when>
                                <c:otherwise>
                                    <td>${alleleProduct["product"]}</td>
                                </c:otherwise>
                            </c:choose>

                                                                        
                                    
                            
                                    
                                    
                                    
                                    
                            <td>${alleleProduct["allele_description"]}</td>
                            
                            <c:choose>
                                <c:when test="${empty alleleProduct['genetic_background']}">
                                    <td style="text-align:center">&horbar;</td>
                                </c:when>
                                <c:otherwise>
                                    <td>${alleleProduct["genetic_background"]}</td>
                                </c:otherwise>
                            </c:choose>
                            
                            <c:choose>
                                <c:when test="${empty alleleProduct['mgi_allele_name']}">
                                    <td style="text-align:center">&horbar;</td>
                                </c:when>
                                <c:otherwise>                                    
                                    <td>${alleleProduct["mgi_allele_name"]}</td>
                                </c:otherwise>
                            </c:choose>
                            <td>
                                <c:if test="${not empty alleleProduct['allele_simple_image']}">
                                        <div style="padding:3px;"><a class="fancybox" target="_blank" href="${alleleProduct['allele_simple_image']}.jpg">
                                                <i class="fa fa-th-list fa-lg"></i></a><span>&nbsp;&nbsp;image</span></div>
                                </c:if>
                                
                                <c:if test="${not empty alleleProduct['genbank_file']}">
                                        <div style="padding:3px;"><a href="${alleleProduct['genbank_file']}"><i class="fa fa-file-text fa-lg"></i></a><span>&nbsp;&nbsp;&nbsp;genbank file</span></div>
                                </c:if>
                            </td>
                            <td style="padding-left: 20px;">
                                <a title="allele project page" href="${baseUrl}/${alleleProduct['product_url']}"><i class="fa fa-clipboard fa-2x"></i></a>
                            </td>
                            <td>

                                <c:forEach var="order" items="${alleleProduct['orders']}" varStatus="orderx">
                                    <a class="btn btn-sm" href="${order['url']}"> <i class="fa fa-shopping-cart"></i>${order['name']}</a>&nbsp;
                                </c:forEach>
                                    
                                    
                                <c:if test="${empty alleleProduct['orders']}">
                                <c:forEach var="contact" items="${alleleProduct['contacts']}" varStatus="contactx">
                                    <a class="btn btn-sm" href="${contact['url']}"> <i class="fa fa-envelope"></i>${contact['name']}</a>
                                </c:forEach>
                                </c:if>

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
 
<c:choose>
    <c:when test="${alleleProductsCre2.get('cre_exists').equals('true')}">
        <div><a href="http://www.creline.org/eucommtools#${alleleProductsCre2.get('mgi_acc')}" target="_blank">Cre Knockin ${alleleProductsCre2.get("product_type")} are available for this gene.</a></div>       
    </c:when>
</c:choose>
    
    
