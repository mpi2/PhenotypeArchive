<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:genericpage>

    	<jsp:attribute name="title">Allele details </jsp:attribute>
	<jsp:attribute name="breadcrumb">&nbsp;&raquo; <a href="${baseUrl}/search?q=*:*&core=gene">Genes</a> &raquo; ${gene.symbol}</jsp:attribute>
	<jsp:attribute name="bodyTag"><body  class="gene-node no-sidebars small-header"></jsp:attribute>
	<jsp:attribute name="addToFooter">

   
    

	<!--  end of floating menu for genes page -->

	<c:if test="${phenotypeStarted}">
	<script type="text/javascript" src="${drupalBaseUrl}/heatmap/js/heatmap.1.3.1.js"></script>
	<!--[if IE 8]>
        <script type="text/javascript">
        dcc.ie8 = true;
        </script>
	<![endif]-->
    <!--[if !IE]><!-->
    <script>
        dcc.heatmapUrlGenerator = function(genotype_id, type) {
            return '${drupalBaseUrl}/phenoview?gid=' + genotype_id + '&qeid=' + type;
        };
    </script>
    <!--<![endif]-->
    <!--[if lt IE 9]>
    <script>
        dcc.heatmapUrlGenerator = function(genotype_id, type) {
           return '${drupalBaseUrl}/phenotypedata?g=' + genotype_id + '&t=' + type + '&w=all';
        };
    </script>
    <![endif]-->
    <!--[if gte IE 9]>
    <script>
        dcc.heatmapUrlGenerator = function(genotype_id, type) {
           return '${drupalBaseUrl}/phenoview?gid=' + genotype_id + '&qeid=' + type;
        };
    </script>
    <![endif]-->

    </c:if>

	</jsp:attribute>


	<jsp:attribute name="header">


		<!-- CSS Local Imports -->
		<!-- link rel="stylesheet" type="text/css" href="${baseUrl}/css/ui.dropdownchecklist.themeroller.css"/-->

		<!-- JavaScript Local Imports -->
		<script src="${baseUrl}/js/general/enu.js"></script>
		<script src="${baseUrl}/js/general/dropdownfilters.js"></script>
                
                <!-- fix allele.js so it contains only local images -->
                
		<script type="text/javascript" src="${baseUrl}/js/general/allele.js"></script>
                <link rel="stylesheet" type="text/css" href="${baseUrl}/css/widetooltip.css"/>
                <link rel="stylesheet" type="text/css" href="${baseUrl}/css/alleles.css"/>


		<script type="text/javascript">var gene_id = '${acc}';</script>
		<style>
		#svgHolder div div {z-index:100;}
		</style>

		<c:if test="${phenotypeStarted}">
	    <!--[if !IE]><!-->
	    <link rel="stylesheet" type="text/css" href="${drupalBaseUrl}/heatmap/css/heatmap.1.3.1.css"/>
	    <!--<![endif]-->
	    <!--[if IE 8]>
	    <link rel="stylesheet" type="text/css" href="${drupalBaseUrl}/heatmap/css/heatmapIE8.1.3.1.css">
	    <![endif]-->
	    <!--[if gte IE 9]>
	    <link rel="stylesheet" type="text/css" href="${drupalBaseUrl}/heatmap/css/heatmap.1.3.1.css">
	    <![endif]-->
		</c:if>

  </jsp:attribute>

	<jsp:body>

<h1 class="title" id="top">${title}</h1>

</br>



<c:if test="${not empty summary}">

<div class="section">
    <div class="inner">
    <h3>Summary</h3>
    <div style="font-size: 110%; font-weight: bold;">
        <p>${summary['allele_description']}</p>
        <c:if test="${not empty summary['statuses']}">




        <c:choose>
        <c:when test="${false}">

            <h5 style="color:red">OLD</h5>

        <table>
            <c:forEach var="status" items="${summary['statuses']}" varStatus="statusx">
                <tr style="background-color: ${status['COLOUR']} !important;">
                    <td style="width:30%">${status['TEXT']}</td>
                    <td>
                    <c:if test="${not empty status['ORDER']}">
                    <a class="btn btn-lg" href="${status['ORDER']}"> <i class="fa fa-shopping-cart"></i> ORDER </a>
                    </c:if>
                    <c:if test="${not empty status['CONTACT']}">
                    <!-- TODO: turn orange-->
                    <a class="btn btn-lg" href="${status['CONTACT']}"> <i class="fa  fa-envelope"></i> CONTACT </a>
                    </c:if>
                    <c:if test="${not empty status['DETAILS']}">
                    <a class="btn btn-lg" href="${status['DETAILS']}"> <i class="fa  fa-info "></i> DETAILS </a>
                    </c:if>
                </td>
                </tr>
            </c:forEach>
        <table>

        </c:when>
        <c:otherwise>


        <table>
            <c:if test="${not empty summary['status_mice']}">

                <tr style="background-color: ${summary['status_mice']['COLOUR']} !important;">

                    <td style="width:30%">${summary['status_mice']['TEXT']}</td>

                    <td>
                    <c:forEach var="order" items="${summary['status_mice']['orders']}" varStatus="statusx">
                        <a class="btn btn-lg" href="${order}"> <i class="fa fa-shopping-cart"></i> ORDER </a>
                    </c:forEach>
                    <c:forEach var="contact" items="${summary['status_mice']['contacts']}" varStatus="statusx">
                        <a class="btn btn-lg" href="${contact}"> <i class="fa  fa-envelope"></i> CONTACT </a>
                    </c:forEach>
                    <c:forEach var="detail" items="${summary['status_mice']['details']}" varStatus="statusx">
                        <a class="btn btn-lg" href="${detail}"> <i class="fa  fa-info "></i> DETAILS </a>
                    </c:forEach>
                    </td>

                </tr>

            </c:if>

            <c:if test="${not empty summary['status_es_cells']}">

                <tr style="background-color: ${summary['status_es_cells']['COLOUR']} !important;">

                    <td style="width:30%">${summary['status_es_cells']['TEXT']}</td>

                    <td>
                    <c:forEach var="order" items="${summary['status_es_cells']['orders']}" varStatus="statusx">
                        <a class="btn btn-lg" href="${order}"> <i class="fa fa-shopping-cart"></i> ORDER </a>
                    </c:forEach>
                    <c:forEach var="contact" items="${summary['status_es_cells']['contacts']}" varStatus="statusx">
                        <a class="btn btn-lg" href="${contact}"> <i class="fa  fa-envelope"></i> CONTACT </a>
                    </c:forEach>
                    <c:forEach var="detail" items="${summary['status_es_cells']['details']}" varStatus="statusx">
                        <a class="btn btn-lg" href="${detail}"> <i class="fa  fa-envelope"></i> DETAILS </a>
                    </c:forEach>
                    </td>

                </tr>

            </c:if>
        <table>

        </c:otherwise>
        </c:choose>








        </c:if>
    </div>
    </div>
</div>





        <c:if test="${not empty summary['map_image']}">
	<div class="section">
		<div class="inner">
			<h3>Allele Map</h3>

        <div id="image">
            <img src="${summary['map_image']}" width="930px">
        </div>
        </c:if>

        <c:if test="${not empty summary['buttons']}">
        <table >
            <tr>
            <td>
                <span>
                <c:if test="${not empty summary['genbank']}">
                    <a class="btn" href="${summary['genbank']}"><i class="fa fa-info"></i> Genbank file</a>
                </c:if>
                </span>
            </td>

            <td>
                <c:if test="${not empty summary['mutagenesis_url']}">
                    <a class="btn" href="${summary['mutagenesis_url']}"> <i class="fa fa-info"></i> Mutagenesis Prediction </a>
                </c:if>
            </td>

            <td>
                <c:if test="${not empty summary['southern_tool']}">
                    <a class="btn" href="${summary['southern_tool']}"> <i class="fa fa-info"></i> Southern Tool </a>
                </c:if>
            </td>
            </tr>
            <tr>
            <td>
                <c:if test="${not empty summary['lrpcr_genotyping_primers']}">
                    <a class="btn" href="${summary['lrpcr_genotyping_primers']}"> <i class="fa fa-info"></i> LRPCR Genotyping Primers </a>
                </c:if>
            </td>
            <td>

            <c:if test="${not empty summary['browsers']}">
                <c:forEach var="browser" items="${summary['browsers']}" varStatus="browsersx">
                    <a class="btn" href="${browser['url']}"> <i class="fa fa-info"></i> ${browser['browser']} </a> <br/>
                </c:forEach>
            </c:if>
                    </td>
                    
                    
                    
            <td>
                
                <c:if test="${not empty summary['loa_assays']}">
                    <c:if test="${not empty summary['loa_assays']['upstream']}">
                        <a class="btn" href="${summary['loa_assays']['upstream']}"> <i class="fa fa-info"></i> LOA (upstream) </a> &nbsp;
                    </c:if>

                    <c:if test="${not empty summary['loa_assays']['downstream']}">
                        <a class="btn" href="${summary['loa_assays']['downstream']}"> <i class="fa fa-info"></i> LOA (downstream) </a> &nbsp;
                    </c:if>

                    <c:if test="${not empty summary['loa_assays']['critical']}">
                        <a class="btn" href="${summary['loa_assays']['critical']}"> <i class="fa fa-info"></i> LOA (critical) </a>
                    </c:if>
                </c:if>

            </td>

            </tr>
        </table>
        </c:if>


        <table>
            <tr>
            <c:if test="${not empty summary['tools']}">
            <td>
                <div style="text-align: center;">Tools</div>
                    <div style="text-align: center;">
                    <c:forEach var="tool" items="${summary['tools']}" varStatus="toolsx">
                        <a href="${tool['url']}">${tool['name']}</a><br/>
                    </c:forEach>
                </div>
            </td>
            </c:if>
            </tr>
        </table>

		</div>
	</div>
                </div>

                </c:if>






<c:if test="${not empty mice}">
    <div class="section">
        <div class="inner">
        <h3>Mice</h3>
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
                <td><a class="hasTooltip" href="${baseUrl}/qc_data/${mouse['allele_type']}/mouse/${mouse['colony_name']}">QC data</a>
                    <div class="hidden">
                        <div class="qcData" data-type="mouse" data-name="${mouse['colony_name']}" data-alleletype="${mouse['allele_type']}"></div>
                    </div>
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




<c:if test="${not empty es_cells}">

	<div class="section">
		<div class="inner">
			<h3>ES Cells</h3>

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
        <td><a class="hasTooltip" href="${baseUrl}/qc_data/${es_cell['allele_type']}/es_cell/${es_cell['es_cell_clone']}">QC data</a>
            <div class="hidden">
                <div class="qcData" data-type="es_cell" data-name="${es_cell['es_cell_clone']}" data-alleletype="${es_cell['allele_type']}"></div>
            </div>
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
                <a id="targeting_vector_table_toggle" data-count='${mice.size()}' data-type='Targeting Vectors' class="toggle_closed">Show all ${targeting_vectors.size()} Targeting Vectors</a>
            </p>
        </c:if>

            <div class="clear"></div>
</div>



		</div>
	</div>
</c:if>





</jsp:body>

</t:genericpage>
