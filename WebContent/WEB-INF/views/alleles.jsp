<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:genericpage>
	<jsp:attribute name="title">Allele details for ${summary['symbol']}</jsp:attribute>
	<jsp:attribute name="breadcrumb">&nbsp;&raquo; <a href="${baseUrl}/search?q=*:*&core=gene">Genes</a> &raquo; ${gene.symbol}</jsp:attribute>
	<jsp:attribute name="bodyTag"><body  class="gene-node no-sidebars small-header"></jsp:attribute>
	<jsp:attribute name="addToFooter">

	<!--  start of floating menu for genes page -->
	<div class="region region-pinnedzzz">

        <div id="flyingnavizzz" class="block">

            <a href="#top"><i class="fa fa-chevron-up" title="scroll to top"></i></a>

            <ul>
                <li><a href="#top">Gene</a></li>
                <li><a href="#section-associations">Phenotype Associations</a></li><!--  always a section for this even if says no phenotypes found - do not putting in check here -->
                <c:if test="${phenotypeStarted}">
                		<li><a href="#heatmap">Heatmap</a></li>
                </c:if>
                <c:if test="${not empty solrFacets}">
                		<li><a href="#section-images">Associated Images</a></li>
                </c:if>
                <c:if test="${not empty expressionFacets}">
                		<li><a href="#section-expression">Expression</a></li>
                </c:if>
                <c:if test="${!countIKMCAllelesError}">
                		<li><a href="#order">Order Mouse and ES Cells</a></li>
                </c:if>
            </ul>

            <div class="clear"></div>

        </div>

    </div>

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
    <script>
          //new dcc.PhenoHeatMap('procedural', 'phenodcc-heatmap', 'Fam63a', 'MGI:1922257', 6, '//dev.mousephenotype.org/heatmap/rest/heatmap/');
          new dcc.PhenoHeatMap({
                /* identifier of <div> node that will host the heatmap */
                'container': 'phenodcc-heatmap',

                /* colony identifier (MGI identifier) */
                'mgiid': '${gene.id.accession}',

                /* default usage mode: ontological or procedural */
                'mode': 'ontological',

                /* number of phenotype columns to use per section */
                'ncol': 5,

                /* heatmap title to use */
                'title': '${gene.symbol}',

                'url': {
                    /* the base URL of the heatmap javascript source */
                    'jssrc': '${fn:replace(drupalBaseUrl, "https:", "")}/heatmap/js/',

                    /* the base URL of the heatmap data source */
                    'json': '${fn:replace(drupalBaseUrl, "https:", "")}/heatmap/rest/',

                    /* function that generates target URL for data visualisation */
                    'viz': dcc.heatmapUrlGenerator
                }
            });
    </script>

    </c:if>

   <!--     <script>

    $( "#image" ).toggle( "slide" );

    $( document ).ready(function() {
            console.log( "ready!" );
             setTimeout(function(){
                 console.log( "timeout!" );
                 $( "#image" ).toggle( "slide", { direction: "up" }, 2000 );
             }, 3000);
        });

    </script>   -->


	</jsp:attribute>


	<jsp:attribute name="header">


		<!-- CSS Local Imports -->
		<!-- link rel="stylesheet" type="text/css" href="${baseUrl}/css/ui.dropdownchecklist.themeroller.css"/-->

		<!-- JavaScript Local Imports -->
		<script src="${baseUrl}/js/general/enu.js"></script>
		<script src="${baseUrl}/js/general/dropdownfilters.js"></script>
		<script type="text/javascript" src="${baseUrl}/js/general/allele.js"></script>
                <link rel="stylesheet" type="text/css" href="${baseUrl}/css/widetooltip.css"/>


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

<h1 class="title" id="top">${summary['symbol']}</h1>

</br>



<c:if test="${not empty summary}">

<div class="section">
    <div class="inner">
    <h3>Summary</h3>
    <div style="font-size: 150%; font-weight: bold;">
        <p>${summary['allele_description']}</p>
        <c:if test="${not empty summary['statuses']}">

            <table>
          <c:forEach var="status" items="${summary['statuses']}" varStatus="statusx">

              <tr>
                  <td>${status['TEXT']}</td>

                  <td>
<div style="font-size: 115%; font-weight: bold;">

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
<!--</div>-->
          </td>

<!--                  <td>${status['TEXT2']}</td>-->

          </tr>
        </c:forEach>
            <table>

        </c:if>
    </div>
    </div>
</div>






	<div class="section">
		<div class="inner">
			<h3>Allele Maps</h3>


        <div id="image">
            <img src="${summary['map_image']}" width="930px">
        </div>








        <table style="font-size: 120%; ">

            <tr>
            <td>
                <span>
                    <a class="btn" href="${summary['genbank']}"><i class="fa fa-info"></i> Genbank file</a>
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

            <td>

                <c:if test="${not empty summary['lrpcr_genotyping_primers']}">
                    <a class="btn" href="${summary['lrpcr_genotyping_primers']}"> <i class="fa fa-info"></i> LRPCR Genotyping Primers </a>
                </c:if>
            </td>







            <td>

<div style="text-align: center;">
<c:if test="${not empty summary['browsers']}">
    <c:forEach var="browser" items="${summary['browsers']}" varStatus="browsersx">
        <a class="btn" href="${browser['url']}"> <i class="fa fa-info"></i> ${browser['browser']} </a> <br/>
    </c:forEach>
</c:if>
</div>

            </td>

            </tr>
        </table>



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


                 <style>
             .toggle_open, .toggle_closed {
                padding-left: 16px;
                padding-top: 2px;
                padding-bottom: 2px;
             }
             .toggle_open {
                background:url(http://www.mousephenotype.org/martsearch_ikmc_project/images/silk/bullet_arrow_up.png) no-repeat 0 center;
             }
             .toggle_closed {
                background:url(http://www.mousephenotype.org/martsearch_ikmc_project/images/silk/bullet_arrow_right.png) no-repeat 0 center;
             }
         </style>




<script>
//    $(function(){
//        //$(".products_toggle").on({'click':function(event){
//        $("#mouse_table_toggle").on({'click':function(event){
//        event.preventDefault();
//        //$(this).closest("tr.main").nextUntil("tr.main").toggle("fast");
//        //alert("toggle!");
//        //$("#mouse_table tbody tr .rest").toggle("fast");
//        $("#mouse_table .rest").toggle("fast");
//        }});
//    });

    function toggleTable(id) {
        $("#" + id + "_toggle").on({'click':function(event){
        event.preventDefault();
        $("#" + id + " .rest").toggle("fast");
//        $("#" + id + "_toggle").css("padding-left", "16px");
//        $("#" + id + "_toggle").css("padding-top", "2px");
//        $("#" + id + "_toggle").css("padding-bottom", "2px");
        //$("#" + id + "_toggle").css("background", "url(http://www.mousephenotype.org/martsearch_ikmc_project/images/silk/bullet_arrow_up.png) no-repeat 0 center");


        //console.log(event);

        if($("#" + id + "_toggle").hasClass("toggle_closed")) {
            $("#" + id + "_toggle").removeClass("toggle_closed");
            $("#" + id + "_toggle").addClass("toggle_open");
            var type = $( "#" + id + "_toggle" ).data( "type" );
            var count = $( "#" + id + "_toggle" ).data( "count" );
            $("#" + id + "_toggle").text("Hide " + type);
        }
        else {
            $("#" + id + "_toggle").removeClass("toggle_open");
            $("#" + id + "_toggle").addClass("toggle_closed");
            //var text = $("#" + id + "_toggle").text();
            //$("#" + id + "_toggle[data-type]").
            var type = $( "#" + id + "_toggle" ).data( "type" );
            var count = $( "#" + id + "_toggle" ).data( "count" );
            $("#" + id + "_toggle").text("Show all " + count + " " + type);
        }



        //bullet_arrow_up
        //bullet_arrow_right
        }});
    }

    $(function(){
        toggleTable("mouse_table");
        toggleTable("es_cell_table");
        toggleTable("targeting_vector_table");
    });

</script>


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
                <th>Order</th>
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
                    <c:forEach var="order" items="${mouse['orders']}" varStatus="ordersx">
                        <a class="btn" href="${order['url']}"> <i class="fa fa-shopping-cart"></i> ${order['name']}</a>
                    </c:forEach>
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
        <th>QC  Data</th>
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
        <td>${es_cell['es_cell_strain']} / ${es_cell['parental_cell_line']}</td>


                <td>
                    <c:forEach var="order" items="${es_cell['orders']}" varStatus="ordersx">
                        <a class="btn" href="${order['url']}"> <i class="fa fa-shopping-cart"></i> ${order['name']}</a>
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
