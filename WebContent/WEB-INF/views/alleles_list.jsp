<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:genericpage>

<jsp:body>

<h1 class="title" id="top">Alleles Test</h1>
<p>${versionDate}</p>
</br>                        

        <c:if test="${not empty message}">
            <div style="font-size: 120%;">
                <p>${message}</p>
            </div>
        </c:if>

<div style="font-size: 120%;">

    <table>
    <c:forEach var="item" items="${list}" varStatus="listx">
        <tr>
        
            <td>
                <a href="${baseUrl}/alleles/${item['mgi_accession_id']}/${item['allele_name_e']}" target="_blank">${item['marker_symbol']}<sup>${item['allele_name']}</sup></a>
            </td>
            
<!--            <td><a target="_blank" href="${item['solr_product_mouse']}">Solr Product Mouse</a></td>
            <td><a target="_blank" href="${item['solr_product_es_cell']}">Solr Product Cell</a></td>
            <td><a target="_blank" href="${item['solr_product_targeting_vector']}">Solr Product Vector</a></td>
            <td><a target="_blank" href="${item['solr_allele2_alleles']}">Solr Allele2 Allele</a></td>
            <td><a target="_blank" href="${item['solr_allele2_genes']}">Solr Allele2 Gene</a></td>-->
            
        <tr>
    </c:forEach>
    </table>

</div>

</jsp:body>
  
</t:genericpage>
