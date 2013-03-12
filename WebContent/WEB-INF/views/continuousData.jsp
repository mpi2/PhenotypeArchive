<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:genericpage>
	<jsp:attribute name="title">Stats for ${gene.name}</jsp:attribute>

	<jsp:attribute name="breadcrumb">&nbsp;&raquo; <a
			href="${baseUrl}/search#sort=marker_symbol asc&q=*:*&core=gene">Genes</a> <c:if
			test="${not empty gene.subtype.name }">&raquo; <a
				href='${baseUrl}/search#fq=marker_type:"${gene.subtype.name}"&q=*:*&core=gene'>${gene.subtype.name}</a>
		</c:if> &raquo; ${gene.symbol}</jsp:attribute>

	<jsp:attribute name="header">

	<script type="text/javascript">
		var gene_id = '${acc}';
	</script>

	<!--    extra header stuff goes here such as extra page specific javascript -->
	<script src="${baseUrl}/js/mpi2_search/all.js"></script>
	<!-- highcharts -->
	<script type='text/javascript' src='http://github.highcharts.com/v3.0Beta/highcharts.js'></script>
	<script src="http://github.highcharts.com/rambera/highcharts-more.js"></script>
	<%-- <script type='text/javascript' src='${baseUrl}/js/charts/highcharts.js'></script>		 --%>
	 <script type="text/javascript" src="https://www.google.com/jsapi"></script>
<!-- <script src="http://code.highcharts.com/modules/exporting.js"></script> do we need the export js as in jsfiddle demo? -->


	

<style>
</style>

    </jsp:attribute>

	<jsp:body>
        
		<div class='topic'>Gene: ${gene.symbol}</div>


<c:forEach var="continuousChart" items="${continuousCharts}" varStatus="loop">
 <%-- ${loop.count  % 2} --%>
<c:if test = "${loop.count  % 2!=0}">
		<div class="row-fluid dataset">  
 </c:if>
		
 
  				 <div class="container span6">
								<div id="chart${loop.count}"
									style="min-width: 400px; height: 400px; margin: 0 auto">
								</div>
								
								
								
			
   								<script type="text/javascript">
   
   								$(function () {
   								    $('#chart${loop.count}').highcharts(${continuousChart});
   								    /* {

   									    chart: {
   									        type: 'boxplot'
   									    },
   									    
   									    title: {
   									        text: 'Continuous Data Example'
   									    },
   									    
   									    legend: {
   									        enabled: false
   									    },
   									
   									    xAxis: {
   									        categories: ['WT', 'WT', 'HOM', 'HOM'],
   									        title: {
   									            text: 'Experiment No.'
   									        }
   									    },
   									    
   									    yAxis: {
   									        title: {
   									            text: 'mmol/l'
   									        },
   									        plotLines: [{
   									            value: 932,
   									            color: 'red',
   									            width: 1,
   									            label: {
   									                text: 'Theoretical mean: 932',
   									                align: 'center',
   									                style: {
   									                    color: 'gray'
   									                }
   									            }
   									        }]  
   									    },
   									
   									    series: [{
   									        name: 'Observations',
   									        data: [
   									          
   									            [733, 853, 939, 980, 1080],
   									            [],
   									            [724, 802, 806, 871, 950],
   									            []
   									        ],
   									        tooltip: {
   									            headerFormat: '<em>Experiment No {point.key}</em><br/>'
   									        }
   									    }, {
   									        name: 'Outlier',
   									        color: Highcharts.getOptions().colors[0],
   									        type: 'scatter',
   									        data: [ // x, y positions where 0 is the first category
   									            [1, 644],
   									            [3, 718],
   									            [3, 951],
   									            [3, 969]
   									        ],
   									        marker: {
   									            fillColor: 'white',
   									            lineWidth: 1,
   									            lineColor: Highcharts.getOptions().colors[0]
   									        },
   									        tooltip: {
   									            pointFormat: 'Observation: {point.y}'
   									        }
   									    }]
   									
   									});
   								} 
   								);*/	
   								
								</script>
								
				</div>

 

			
				
			
	<c:if test = "${loop.count % 2==0}">
</div>
</c:if>
		</c:forEach>
		<!--/row-->
		<div class="row-fluid dataset">
			
			<!--/span-->
		</div>
		<!--/row-->

    </jsp:body>
</t:genericpage>
