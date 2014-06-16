<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:genericpage>
	<jsp:attribute name="title">Gene details for ${gene.name}</jsp:attribute>
	<jsp:attribute name="breadcrumb">&nbsp;&raquo; <a href="${baseUrl}/search?q=*:*&core=gene">Genes</a> &raquo; ${gene.symbol}</jsp:attribute>
	<jsp:attribute name="bodyTag"><body  class="gene-node no-sidebars small-header"></jsp:attribute>
	<jsp:attribute name="addToFooter">
	<!--  start of floating menu for genes page -->
	<div class="region region-pinned">
            
        <div id="flyingnavi" class="block">
            
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

<h1 class="title" id="top">${symbol}</h1>

</br>




<div class="section">
    <div class="inner">
    <h3>Summary</h3>
    <div style="font-size: 120%; font-weight: bold;">
        <p>${allele_description}</p>
        <c:if test="${not empty statuses}">
          <c:forEach var="status" items="${statuses}" varStatus="statusx">
            <p>${status['TEXT']}                         
            <c:if test="${not empty status['ORDER']}">
              <td><a class="btn" href="${status['ORDER']}"> <i class="fa fa-shopping-cart"></i> ORDER </a></td>
            </c:if>
            <c:if test="${not empty status['CONTACT']}">
              <!-- TODO: turn orange-->
              <td><a class="btn" href="${status['CONTACT']}"> <i class="fa fa-shopping-cart"></i> CONTACT </a></td>
            </c:if>
            <c:if test="${not empty status['DETAILS']}">
              <td><a class="btn" href="${status['DETAILS']}"> <i class="fa fa-shopping-cart"></i> DETAILS </a></td>
            </p>
            </c:if>
        </c:forEach>	
        </c:if>			
    </div>
    </div>
</div>




                        
                        
                        
                        
                        
                        
                        

	<div class="section">
		<div class="inner">
			<h3>Allele Maps</h3>
                        
        <div id="image">
            <img src="${map_image}" width="930px">        
        </div>
                        

        
        
        
        
                        
        <table style="font-size: 150%; ">         
            <tr>
            <td>   
                <span>
    <a href="${genbank}}">
        <i class="fa fa-file-text fa-lg"></i>
    </a>
        </span>
    <span>&nbsp;&nbsp;&nbsp;genbank file</span>
            </td>

            <td>     
                <c:if test="${not empty mutagenesis_url}">
                    <a href="${mutagenesis_url}">Mutagenesis Prediction</a>     
                </c:if>	
            </td>

            <td>       
                
<div style="text-align: center;">Genome Browsers</div>
<div style="text-align: center;">
<c:if test="${not empty browsers}">
    <c:forEach var="browser" items="${browsers}" varStatus="browsersx">
        <a href="${browser['url']}" target="_blank" class="ensembl_link">${browser['browser']}</a>&nbsp;&nbsp;&nbsp;
    </c:forEach>	
</c:if>	
</div>
            </td>
            
    <c:forEach var="tool" items="${tools}" varStatus="toolsx">
            <td><a href="${tool['url']}">${tool['name']}</a></td>
    </c:forEach>	
            
            </tr>
        </table>
        
                                
                       

        
        
        
        
        
        
        
        
        
        
		</div>
	</div>

        
        
        
        
        
        
        
        
        
<c:if test="${not empty mice_in_progress}">
    <div class="section">
        <div class="inner" style="background-color: #FFE0B2 !important;">
        <h3>Mice - in progress</h3>
        <div class="dataset_content">
            <table>
            <thead>
            <tr>
                <th>Genetic background</th>
                <th>Production Centre</th>
                <th>ES Cell</th>
                <th>Status / Dates</th>
                <th>Full production details</th>
                <th>Contact</th>
            </tr>
            </thead>
            <tbody>               
                
            <c:forEach var="mouse" items="${mice_in_progress}" varStatus="micex">

            <tr>
                <td>${mouse['genetic_background']}</td>
                <td>${mouse['production_centre']}</td>
                <td>${mouse['es_cell']}</td>
                <td>${mouse['status_dates']}</td>
                <td style="text-align: center;"><a href="${mouse['production_detail_url']}">link</a></td>
                <td><a class="btn" href="${mouse['contact_url']}"> <i class="fa fa-shopping-cart"></i> ${mouse['contact_name']} </a></td>
            </tr>
            
            </c:forEach>	
            
            </tbody>
            </table>
            <div class="clear"></div>
        </div>
        </div>
    </div>
</c:if>
        
        
        
        
        
        
        
                        
                        
                        
<c:if test="${not empty mice}">
    <div class="section">
        <div class="inner" style="background-color: #E0F9FF !important;">
        <h3>Mice</h3>
        <div class="dataset_content">
            <table>
            <thead>
            <tr>
                <th>Genetic background</th>
                <th>Production Centre</th>
                <th>ES Cell</th>
                <th>QC Data / Southern tool</th>
                <th>Order</th>
            </tr>
            </thead>
            <tbody>               
                
            <c:forEach var="mouse" items="${mice}" varStatus="micex">

            <tr>
                <td>${mouse['genetic_background']}</td>
                <td>${mouse['production_centre']}</td>
                <td>${mouse['es_cell']}</td>
                <td style="text-align: center;"><a href="${mouse['qc_data']}">QC data</a> / <a href="${mouse['southern_tool']}">Southern tool</a></td>
                
                
                <td>
                    <c:forEach var="order" items="${mouse['orders']}" varStatus="ordersx">
                        <a class="btn" href="${order['url']}"> <i class="fa fa-shopping-cart"></i> ${order['name']}</a>
                    </c:forEach>	
                </td>
                
                
                
            </tr>
            
            </c:forEach>	
            
            </tbody>
            </table>
            <div class="clear"></div>
        </div>
        </div>
    </div>
</c:if>

                        
                        
                        
<c:if test="${not empty es_cells}">
                        
	<div class="section">
		<div class="inner" style="background-color: #E0F9FF !important;">
			<h3>ES Cells</h3>
                        
<div class="dataset_content">
  <table>
    <thead>
      <tr>
        <th>Genetic background</th>
        <th>ES Cell Clone</th>
        <th>Targeting Vector</th>
        <th>QC  Data / Southern tool</th>
        <th>ES Cell strain / parental cell line</th>
        <th>Order</th>
      </tr>
    </thead>

    <tbody>

            <c:forEach var="es_cell" items="${es_cells}" varStatus="es_cellsx">
      <tr>
        <td>${es_cell['genetic_background']}</td>
        <td>${es_cell['es_cell_clone']}</td>
        <td>${es_cell['targeting_vector']}</td>
        <td style="text-align: center;"><a href="${es_cell['qc_data']}">QC data</a> / <a href="${es_cell['southern_tool']}">Southern tool</a></td>
        
        <td>${es_cell['es_cell_strain']} / ${es_cell['parental_cell_line']}</td>
        
        <td><a class="btn" href="${es_cell['order_url']}"> <i class="fa fa-shopping-cart"></i> ${es_cell['order_name']} </a></td>
      </tr>

                  </c:forEach>	

    </tbody>
  </table>

         <p class="textright">
        <a class="products_toggle toggle-close">show all 14 ES Cells</a>
      </p>

  <div class="clear"></div>
</div>
                        
                        
		</div>
	</div>
</c:if>

                        
                        
                        
<c:if test="${not empty targeting_vectors}">
                        
	<div class="section">
		<div class="inner" style="background-color: #E0F9FF !important;">
			<h3>Targeting Vectors</h3>
                        
                        
<div class="dataset_content">
  <table>
    <thead>
      <tr>
        
        <th>Design Oligos</th>
        <th>Targeting Vector</th>
        <th>Cassette</th>
        <th>Backbone</th>
        <th>Genbank File</th>
        <th>Order</th>

      </tr>
    </thead>

    <tbody>

      <c:forEach var="targeting_vector" items="${targeting_vectors}" varStatus="targeting_vectorsx">
      <tr>
          
          <td style="text-align: center;">
              <a href="${targeting_vector['design_oligos']}" target="_blank"><i class="fa fa-pencil-square-o fa-2x"></i></a>
            </td>
          
        <td>${targeting_vector['targeting_vector']}</td>
        <td>${targeting_vector['cassette']}</td>
        <td>${targeting_vector['backbone']}</td>
        
        <td style="text-align: center;">
        <span>
    <a href="${targeting_vector['genbank_file']}}">
        <i class="fa fa-file-text fa-lg"></i>
    </a>
        </span>
        </td>
        
        <td><a class="btn" href="${targeting_vector['order_url']}"> <i class="fa fa-shopping-cart"></i> ${targeting_vector['order_name']} </a></td>
      </tr>
      </c:forEach>	

    </tbody>
  </table>

     <p class="textright">
        <a class="products_toggle toggle-close">show all 5 targeting vectors</a>
      </p>
      
  <div class="clear"></div>
</div>
                        
                        
                        
		</div>
	</div>
</c:if>

                        
                        
                        
                        
</jsp:body>
  
</t:genericpage>
