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
<!--			<p>${type}</p>-->
                        <p>Knockout First, reporter-tagged deletion</p>
			<p>${status} <a href="${orderLink}">ORDER</a>, <a href="${detailsLink}">DETAILS</a></p>
			<p>There are ES cells for this allele <a href="${orderLink}">ORDER</a>, <a href="${detailsLink}">DETAILS</a></p>
		</div>
	</div>

	<div class="section">
		<div class="inner">
			<h3>Allele Maps</h3>
                        
        <div>
<img src="http://www.mousephenotype.org/imits/targ_rep/alleles/11268/allele-image" width="930px">        
        </div>
                        
                        
                        
                        
                        <div style="padding:3px;">
                            <a href="${genbank}}">
                                <i class="fa fa-file-text fa-lg"></i>
                            </a>
                            <span>&nbsp;&nbsp;&nbsp;genbank file</span>
                        </div>
                                
                </br>

<!--			<h4>Mutagenesis Predictions</h4>-->
                                
                        <p>${mutagenesis_blurb}</p>
                        
<p class="with-label no-margin">
<span class="label">Genome Browsers</span>
<a href="http://www.ensembl.org/Mus_musculus/Location/View?r=9:54544794-54560218;&contigviewbottom=das:http://das.sanger.ac.uk/das/ikmc_products=normal,contig=normal,ruler=normal,scalebar=normal" target="_blank" class="ensembl_link">Ensembl (mouse)</a> - <a href="http://genome.ucsc.edu/cgi-bin/hgTracks?db=mm10&ikmc=pack&ensGene=pack&position=chr9:54544794-54560218" target="_blank" class="ucsc_link">UCSC (mouse)</a>
</p>
<p class="with-label no-margin">
<span class="label">Tools</span><a href="http://www.sanger.ac.uk/htgt/htgt2/tools/restrictionenzymes?es_clone_name=EPD0337_2_D04&iframe=true&width=100%&height=100%" target="_blank" class="ext_link">Southern Blot Tool</a> - <a href="/martsearch_ikmc_project/martsearch/ikmc_project/41713/pcr_primers?iframe=true&width=60%25&height=60%25" target="_blank" class="ext_link">LRPCR Genotyping Primers</a>
</p>

        </br>
        
        
        
        
        
        
        
        
        
        
		</div>
	</div>

	<div class="section">
		<div class="inner">
			<h3>Mice</h3>
                        
                        
              <div class="dataset_content">
  <table>
    <thead>
      <tr>
        <th>Genetic background</th>

        <th>Production Centre</th>

        <th>ES Cell</th>

        <th>QC  Data / Southern tool</th>

        <th>Order</th>
      </tr>
    </thead>

    <tbody>
      
      <tr>
        <td>C57BL/6NTac;C57BL/6NTac;C57BL/6N-A<sup>tm1Brd</sup>/a</td>
        
        <td>Harwell</td>

        <td>EPD0337_2_D04</td>

        <td><a href="http://www.sanger.ac.uk">link</a></td>

        <td><a class="btn" href="http://www.emmanet.org/mutant_types.php?keyword=Cib2"> <i class="fa fa-shopping-cart"></i> EMMA </a></td>
      </tr>

    </tbody>
  </table>

                 
      
  <div class="clear"></div>
</div>
          
                        
                        
                        
                        
                        
                        
                        
		</div>
	</div>

	<div class="section">
		<div class="inner">
			<h3>ES Cells</h3>
                        
<div class="dataset_content">
  <table>
    <thead>
      <tr>

        <th>Genetic background</th>

        <th>ES Cell Clone</th>

        <th>Targeting Vector</th>

        <th>QC  Data / Southern tool</th>

        <th>Genotyping Primers</th>

        <th>ORDER</th>

      </tr>
    </thead>

    <tbody>

      <tr>
        <td>what?</td>

        <td>whatever</td>

        <td>whatever</td>

        <td>whatever</td>
        
        <td>whatever</td>
        
        <td><a class="btn" href="http://www.emmanet.org/mutant_types.php?keyword=Cib2"> <i class="fa fa-shopping-cart"></i> whoever </a></td>
      </tr>

    </tbody>
  </table>

         <p class="textright">
        <a class="products_toggle toggle-close">show all 14 ES Cells</a>
      </p>

  <div class="clear"></div>
</div>
                        
                        
		</div>
	</div>

	<div class="section">
		<div class="inner">
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
        <th>ORDER</th>

      </tr>
    </thead>

    <tbody>

      <tr>
        <td>whatever</td>

        <td>whatever</td>

        <td>whatever</td>

        <td>whatever</td>

        <td>whatever</td>

        <td><a class="btn" href="http://www.emmanet.org/mutant_types.php?keyword=Cib2"> <i class="fa fa-shopping-cart"></i> whoever </a></td>
      </tr>

    </tbody>
  </table>

     <p class="textright">
        <a class="products_toggle toggle-close">show all 5 targeting vectors</a>
      </p>
      
  <div class="clear"></div>
</div>
                        
                        
                        
		</div>
	</div>

</jsp:body>
  
</t:genericpage>
