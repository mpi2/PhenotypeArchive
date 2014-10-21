<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>




<c:if test="${not empty targeting_vectors}">

	<div class="section">
		<div class="inner">
			<h3>Targeting Vectors</h3>


<div class="dataset_content">
  <table id="targeting_vector_table">
    <thead>
      <tr>

        <th>Design Oligos</th>
        <th>Targeting Vector</th>
        <th>Cassette</th>
        <th>Backbone</th>
        <th>Genbank File</th>
        <th>Allele Image</th>
        <th>Order</th>

      </tr>
    </thead>

    <tbody>

      <c:forEach var="targeting_vector" items="${targeting_vectors}" varStatus="targeting_vectorsx">

                <c:if test="${targeting_vectorsx.getIndex() == 0}">

                    <c:if test="${targeting_vector['production_completed'] == 'true'}">
                        <tr class="first" style="background-color: #E0F9FF !important;">
                    </c:if>
                    <c:if test="${targeting_vector['production_completed'] == 'false'}">
                        <tr class="first" style="background-color: #FFE0B2 !important;">
                    </c:if>

                </c:if>

                <c:if test="${targeting_vectorsx.getIndex() > 0}">

                    <c:if test="${targeting_vector['production_completed'] == 'true'}">
                        <tr class="rest" style="display:none;background-color: #E0F9FF !important;">
                    </c:if>
                    <c:if test="${targeting_vector['production_completed'] == 'false'}">
                        <tr class="rest" style="display:none;background-color: #FFE0B2 !important;">
                    </c:if>

                </c:if>

          <td style="text-align: center;">
            <span>
                <c:if test="${not empty targeting_vector['design_oligos_url']}">
                    <a href="${targeting_vector['design_oligos_url']}" target="_blank"><i class="fa fa-pencil-square-o fa-2x"></i></a>
                </c:if>
            </span>
            </td>

        <td>${targeting_vector['targeting_vector']}</td>
        <td>${targeting_vector['cassette']}</td>
        <td>${targeting_vector['backbone']}</td>

        <td style="text-align: center;">
            <span>
                <c:if test="${not empty targeting_vector['genbank_file_url']}">
                    <a href="${targeting_vector['genbank_file_url']}">
                        <i class="fa fa-file-text fa-lg"></i>
                    </a>
                </c:if>
            </span>
        </td>

        <td style="text-align: center;">
            <span>
                <c:if test="${not empty targeting_vector['allele_image']}">
                    <a href="${targeting_vector['allele_image']}">
                        <i class="fa fa-file-text fa-lg"></i>
                    </a>
                </c:if>
            </span>
        </td>

        <td>
             <c:forEach var="order" items="${targeting_vector['orders']}" varStatus="ordersx">
                 <a class="btn" href="${order['url']}"> <i class="fa fa-shopping-cart"></i> ${order['name']}</a>
             </c:forEach>
         </td>

      </tr>
      </c:forEach>

    </tbody>
  </table>

        <c:if test="${targeting_vectors.size() > 1}">
            <p class="textright">
                <a id="targeting_vector_table_toggle" data-count='${targeting_vectors.size()}' data-type='Targeting Vectors' class="toggle_closed">Show all ${targeting_vectors.size()} Targeting Vectors</a>
            </p>
        </c:if>

            <div class="clear"></div>
</div>

		</div>
	</div>
</c:if>
