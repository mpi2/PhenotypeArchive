<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:genericpage>

<jsp:body>

<h1 class="title" id="top">Alleles Test</h1>

</br>                        

<div style="font-size: 120%;">

    <table>
    <c:forEach var="item" items="${list}" varStatus="listx">
        <tr>
        
            <td>
                <a href="${baseUrl}/alleles/${item['mgi_accession_id']}/${item['allele_name_e']}" target="_blank">${item['marker_symbol']}<sup>${item['allele_name']}</sup></a>
            </td>
<!--            <td><a target="_blank" href="${item['solr_product']}" target="_blank">Solr Product</a></td>-->
            <td><a target="_blank" href="${item['solr_product_mouse']}" target="_blank">Solr Product Mouse</a></td>
            <td><a target="_blank" href="${item['solr_product_es_cell']}" target="_blank">Solr Product Cell</a></td>
            <td><a target="_blank" href="${item['solr_product_targeting_vector']}" target="_blank">Solr Product Vector</a></td>
<!--            <td><a target="_blank" href="${item['solr_allele2']}" target="_blank">Solr Allele2</a></td>-->
            <td><a target="_blank" href="${item['solr_allele2_alleles']}" target="_blank">Solr Allele2 Allele</a></td>
            <td><a target="_blank" href="${item['solr_allele2_genes']}" target="_blank">Solr Allele2 Gene</a></td>
            
        <tr>
    </c:forEach>
    </table>

</div>

</jsp:body>
  
</t:genericpage>
