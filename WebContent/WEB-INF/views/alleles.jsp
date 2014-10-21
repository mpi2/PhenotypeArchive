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

<jsp:include page="alleles_summary_frag.jsp" />

<jsp:include page="alleles_mice_frag.jsp" />

<jsp:include page="alleles_es_cells_frag.jsp" />

<jsp:include page="alleles_targeting_vectors_frag.jsp" />

</jsp:body>

</t:genericpage>
