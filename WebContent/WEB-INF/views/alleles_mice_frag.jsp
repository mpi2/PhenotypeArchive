<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<c:if test="${not empty mice}">
    <div class="section">
        <div class="inner">
        <h3 id="mice_block">Mice</h3>
        <div class="dataset_content">
            <table id="mouse_table">
            <thead>
            <tr>
                <th>Genetic Background</th>
                <th>Production Centre</th>
                <th>ES Cell</th>
                <th>QC Data</th>
                <th>Order / Contact</th>
            </tr>
            </thead>
            <tbody class="products">

            <c:forEach var="mouse" items="${mice}" varStatus="micex">

                <c:if test="${micex.getIndex() == 0}">

                    <c:if test="${mouse['production_completed'] == 'true'}">
                        <tr class="first" style="background-color: #E0F9FF !important;">
                    </c:if>
                    <c:if test="${mouse['production_completed'] == 'false'}">
                        <tr class="first" style="background-color: #FFE0B2 !important;">
                    </c:if>

                </c:if>

                <c:if test="${micex.getIndex() > 0}">

                    <c:if test="${mouse['production_completed'] == 'true'}">
                        <tr class="rest" style="display:none;background-color: #E0F9FF !important;">
                    </c:if>
                    <c:if test="${mouse['production_completed'] == 'false'}">
                        <tr class="rest" style="display:none;background-color: #FFE0B2 !important;">
                    </c:if>

                </c:if>

                <td>${mouse['genetic_background']}</td>
                <td>${mouse['production_centre']}</td>
                <td>${mouse['es_cell']}</td>
                <td>
                   
                    
<c:choose>
    <c:when test='${not empty mouse["qc_data_url"]}'>
            <a class="hasTooltip" href="${baseUrl}/${mouse['qc_data_url']}">QC data</a>
</c:when>
<c:otherwise>
            <a class="hasTooltip" href="#">QC data</a>
</c:otherwise>
</c:choose>
                    
                    
                    
                    <div class="hidden">
                        <div class="qcData" data-type="mouse" data-name="${mouse['colony_name']}" data-alleletype="${mouse['allele_type']}"></div>
                    </div>
                    <c:if test="${not empty mouse['qc_about']}">
                        (&nbsp;<a target="_blank" href="${mouse['qc_about']}">about</a>&nbsp;)
                    </c:if>
                </td>
                <td>

                    <c:if test="${not empty mouse['orders']}">

                    <c:forEach var="order" items="${mouse['orders']}" varStatus="ordersx">
                        <a class="btn" href="${order['url']}"> <i class="fa fa-shopping-cart"></i> ${order['name']}</a>
                    </c:forEach>

                    </c:if>

                    <c:if test="${empty mouse['orders'] and not empty mouse['contacts']}">

                    <c:forEach var="contact" items="${mouse['contacts']}" varStatus="contactsx">
                        <a class="btn" href="${contact['url']}"> <i class="fa  fa-envelope"></i> ${contact['name']} </a>
                    </c:forEach>

                    </c:if>

                </td>



            </tr>

            </c:forEach>

            </tbody>
            </table>

        <c:if test="${mice.size() > 1}">
            <p class="textright">
                <a id="mouse_table_toggle" data-count='${mice.size()}' data-type='Mice' class="toggle_closed">Show all ${mice.size()} Mice</a>
            </p>
        </c:if>

            <div class="clear"></div>
        </div>
        </div>
    </div>
</c:if>

