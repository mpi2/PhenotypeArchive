<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:genericpage>
	<jsp:attribute name="title">Procedure Stats for ${gene.name}</jsp:attribute>

	<jsp:attribute name="breadcrumb">&nbsp;&raquo; <a
			href="${baseUrl}/search#sort=marker_symbol asc&q=*:*&core=gene">Genes</a> <c:if
			test="${not empty gene.subtype.name }">&raquo; <a
				href='${baseUrl}/search#fq=marker_type_str:"${gene.subtype.name}"&q=*:*&core=gene'>${gene.subtype.name}</a>
		</c:if> &raquo; ${gene.symbol}</jsp:attribute>

	<jsp:attribute name="header">

	<script type="text/javascript">
		var gene_id = '${acc}';
	</script>

	<!--    extra header stuff goes here such as extra page specific javascript -->
	<script src="${baseUrl}/js/mpi2_search/all.js"></script>
	<!-- highcharts -->
	<script type='text/javascript' src='${baseUrl}/js/charts/highcharts.js'></script>		
	 <script type="text/javascript" src="https://www.google.com/jsapi"></script>
<!-- <script src="http://code.highcharts.com/modules/exporting.js"></script> do we need the export js as in jsfiddle demo? -->


	

<style>
</style>

    </jsp:attribute>

	<jsp:body>
        
		<div class='topic'>Gene: ${gene.symbol}</div>
		<!-- <div class="row-fluid dataset">
			<div class="container span6">
			<div id="chart0"
					style="min-width: 400px; height: 400px; margin: 0 auto"></div>
					</div>
					<div class="container span6">
						<div id="chart1"
					style="min-width: 400px; height: 400px; margin: 0 auto"></div>
					</div>
		</div> -->


			
		<!--/row-->
    <table>
    <tr id="title">
        <th>&nbsp;</th>
        <th>
            <div class="containerVert">
                <div class="head">
                    <div class="vert">Column 1</div>
                </div>
            </div>
        </th>
        <th>
            <div class="containerVert">
                <div class="head">
                    <div class="vert">Col 2</div>
                </div>
            </div>
        </th>
        <th>
            <div class="containerVert">
                <div class="head">
                    <div class="vert">Column Two</div>
                </div>
            </div>
        </th>
    </tr>
    <tr>
        <td>Row 1</td>
        <td>&nbsp;</td>
        <td>&nbsp;</td>
        <td>&nbsp;</td>
    </tr>
    
    <tr>
        <td>Row 2</td>
        <td>&nbsp;</td>
        <td>&nbsp;</td>
        <td>&nbsp;</td>
    </tr>
    
    <tr>
        <td>Row 3</td>
        <td>&nbsp;</td>
        <td>&nbsp;</td>
        <td>&nbsp;</td>
    </tr>
</table>
		<div class="row-fluid dataset">
		<table>
			<c:forEach var="pipeline" items="${allPipelines}" varStatus="pipelineCount">
			<tr id="title2">	
					<th>
					${pipeline}
					</th>
					
					<c:forEach var="procedure" items="${pipeline.procedures}">
					<th>
					<div class="containerVert">
					<div class="headV">
					<div class="vert">${procedure}</div>
					</div>
					</div>
					</th>
					</c:forEach>
			</tr>
			</c:forEach>
			</table>
			<!--/span-->
		</div>
		<!--/row-->

    </jsp:body>
</t:genericpage>
