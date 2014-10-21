<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<c:if test="${not empty es_cells}">

	<div class="section">
		<div class="inner">
			<h3 id="es_cell_block">ES Cells</h3>

<div class="dataset_content">
  <table id="es_cell_table">
    <thead>
      <tr>
        <th>ES Cell Clone</th>
        <th>Targeting Vector</th>
        <th>QC Data</th>
        <th>ES Cell strain / Parental Cell Line</th>
        <th>Order</th>
      </tr>
    </thead>

    <tbody>

            <c:forEach var="es_cell" items="${es_cells}" varStatus="es_cellsx">

                <c:if test="${es_cellsx.getIndex() == 0}">

                    <c:if test="${es_cell['production_completed'] == 'true'}">
                        <tr class="first" style="background-color: #E0F9FF !important;">
                    </c:if>
                    <c:if test="${es_cell['production_completed'] == 'false'}">
                        <tr class="first" style="background-color: #FFE0B2 !important;">
                    </c:if>

                </c:if>

                <c:if test="${es_cellsx.getIndex() > 0}">

                    <c:if test="${es_cell['production_completed'] == 'true'}">
                        <tr class="rest" style="display:none;background-color: #E0F9FF !important;">
                    </c:if>
                    <c:if test="${es_cell['production_completed'] == 'false'}">
                        <tr class="rest" style="display:none;background-color: #FFE0B2 !important;">
                    </c:if>

                </c:if>



        <td>${es_cell['es_cell_clone']}</td>
        <td>${es_cell['targeting_vector']}</td>
        <td>           
            
<c:choose>
    <c:when test='${not empty es_cell["qc_data_url"]}'>
            <a class="hasTooltip" href="${baseUrl}/${es_cell['qc_data_url']}">QC data</a>
</c:when>
<c:otherwise>
            <a class="hasTooltip" href="#">QC data</a>
</c:otherwise>
</c:choose>
            
            
            
            
            <div class="hidden">
                <div class="qcData" data-type="es_cell" data-name="${es_cell['es_cell_clone']}" data-alleletype="${es_cell['allele_type']}"></div>                    
            </div>
            <c:if test="${not empty es_cell['qc_about']}">
                (&nbsp;<a target="_blank" href="${es_cell['qc_about']}">about</a>&nbsp;)
            </c:if>
            
        </td>

        <td style="text-align: center;">${es_cell['es_cell_strain']} / ${es_cell['parental_cell_line']}</td>

                <td>
                    <c:forEach var="order" items="${es_cell['orders']}" varStatus="ordersx">
                        <a class="btn" href="${order['url']}"> <i class="fa fa-shopping-cart"></i> ${order['name']}</a>&nbsp;
                    </c:forEach>
                </td>


      </tr>

                  </c:forEach>

    </tbody>
  </table>

        <c:if test="${es_cells.size() > 1}">
            <p class="textright">
                <a id="es_cell_table_toggle" data-count='${es_cells.size()}' data-type='ES Cells' class="toggle_closed">Show all ${es_cells.size()} ES Cells</a>
            </p>
        </c:if>

  <div class="clear"></div>
</div>


		</div>
	</div>
</c:if>
