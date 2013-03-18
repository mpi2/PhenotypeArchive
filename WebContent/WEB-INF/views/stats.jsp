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
	<%-- <script type='text/javascript' src='${baseUrl}/js/charts/highcharts.js'></script>	 --%>	
	 <script type="text/javascript" src="https://www.google.com/jsapi"></script>
<!-- <script src="http://code.highcharts.com/modules/exporting.js"></script> do we need the export js as in jsfiddle demo? -->
<script type='text/javascript' src='${baseUrl}/js/charts/highchartsBeta.js'></script>
<script type='text/javascript' src='${baseUrl}/js/charts/highcharts-more-rambera.js'></script>
	<!-- <script src="http://github.highcharts.com/rambera/highcharts-more.js"></script> -->

	

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

<!-- time series charts here-->

<c:forEach var="timeChart" items="${timeSeriesCharts}" varStatus="timeLoop">
 <%-- ${loop.count  % 2} --%>
	<c:if test = "${timeLoop.count  % 2!=0}">
		<div class="row-fluid dataset">  
 	</c:if>
  				 <div class="container span6">
								<div id="timeChart${timeLoop.count}"
									style="min-width: 400px; height: 450px; margin: 0 auto">
								</div>
								<script type="text/javascript">
								${timeChart}
								</script>
					</div>
<c:if test = "${timeLoop.count % 2==0}">
		 </div>
</c:if>
</c:forEach>


<!--  <script type="text/javascript">
 $(function () {
    var chart;
    $(document).ready(function() {
        chart = new Highcharts.Chart({
            chart: {
                renderTo: 'timeChart1',
                type: 'spline',
                marginRight: 130,
                marginBottom: 25
            },
            title: {
                text: 'Monthly Average Temperature',
                x: -20 //center
            },
            subtitle: {
                text: 'Source: WorldClimate.com',
                x: -20
            },
           xAxis: {
        	   title: {
                   text: 'Time (minutes)'
               }
            }, 
            yAxis: {
                title: {
                    text: 'Temperature (°C)'
                },
                plotLines: [{
                    value: 0,
                    width: 1,
                    color: '#808080'
                }]
            },
            tooltip: {
                formatter: function() {
                        return '<b>'+ this.series.name +'</b><br/>'+
                        this.x +': '+ this.y +'°C';
                }
            },
            legend: {
                layout: 'vertical',
                align: 'right',
                verticalAlign: 'top',
                x: -10,
                y: 100,
                borderWidth: 0
            },
            series: [{
                name: 'Control',
                data: [[0.0 ,6.6337337], [15.0 ,15.491567], [30.0 ,12.379637], [60.0 ,9.204818], [120.0 ,5.955421]]
            }, {
                name: 'Mutant',
                data: [[0.0 ,6.7124996], [15.0 ,15.637501], [30.0 ,13.55], [60.0 ,8.724998], [120.0 ,5.7812495]]
            },
            {
                name: 'Confidence',
                type: 'errorbar',
                color: 'black',
                data: [
                    [0,7.5, 15],
                    [15, 7.5, 8.5],
                    [30, 30, 40]
                ]
            }]
        });
    });
    
});

</script> -->
 
 








<c:forEach var="categoricalBarChart" items="${categoricalBarCharts}" varStatus="loop">
 <%-- ${loop.count  % 2} --%>
<c:if test = "${loop.count  % 2!=0}">
		<div class="row-fluid dataset">  
 </c:if>
  				 <div class="container span6">
								<div id="categoricalBarChart${loop.count}"
									style="min-width: 400px; height: 400px; margin: 0 auto">
								</div>
								
									<c:set var="table" scope="page" value="${tables[loop.index]}"/>
										<table id="table${loop.count}" class="table table-bordered table-hover table-striped">
										<thead><tr><th></th> <!-- empty header at left end -->
										<c:forEach var="xAxisCat" items="${table.xAxisCategories}" varStatus="xCatCount">
										<th>${xAxisCat}</th>
										</c:forEach>
										<%-- <th>${tables[loop.count-1].xAxisCategories[1]}</th><th>${tables[loop.count-1].xAxisCategories[2]}</th> --%>
										</tr></thead>
										<tbody>
										
										
										
												<c:forEach var="seriesList" items="${table.seriesDataForCategoricalType}" varStatus="seriesListCount">
												<tr>
												<td>${table.categories[seriesListCount.index]}</td>
														<c:forEach var="seriesItem" items="${seriesList}" varStatus="seriesItemCount">
														<td>${seriesItem}</td>
														</c:forEach>
												</tr>
												</c:forEach>
									
										
										<%-- <td>${tables[loop.count-1].seriesDataForCategoricalType[0][0]}</td><td>${tables[loop.count-1].seriesDataForCategoricalType[0][1]}</td><td>${tables[loop.count-1].seriesDataForCategoricalType[0][2]}</td>
										 </tr>--%>
										<%-- <tr><td>${tables[loop.count-1].categories[1]}</td><td>${tables[loop.count-1].seriesDataForCategoricalType[1][0]}</td><td>${tables[loop.count-1].seriesDataForCategoricalType[1][1]}</td><td>${tables[loop.count-1].seriesDataForCategoricalType[1][2]}</td>
										 </tr>--%>
										</tbody>
										</table>
   								<script type="text/javascript">
   
   								${categoricalBarChart}
   
   								
								</script>
								
				</div>
<c:if test = "${loop.count % 2==0}">
</div>
</c:if>
 </c:forEach>

			
				
			
	
		</div>
		<!--/end of categoriacl charts-->
		

<c:forEach var="continuousChart" items="${continuousCharts}" varStatus="uniDimensionalLoop">
 <%-- ${loop.count  % 2} --%>
<c:if test = "${uniDimensionalLoop.count  % 2!=0}">
		<div class="row-fluid dataset">  
 </c:if>
		
 
  				 <div class="container span6">
								<div id="chart${uniDimensionalLoop.count}"
									style="min-width: 400px; height: 400px; margin: 0 auto">
								</div>
								
								
								
			
   								<script type="text/javascript">
   
   								$(function () {
   								    $('#chart${uniDimensionalLoop.count}').highcharts(${continuousChart});
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

 

			
				
			
	<c:if test = "${uniDimensionalLoop.count % 2==0}">
</div>
</c:if>
		</c:forEach>
		<!--/row-->

<!-- Continuous barchart here -->
<c:forEach var="barChart" items="${continuousBarCharts}" varStatus="barloop">
<c:if test = "${barloop.count  % 2!=0}">
		<div class="row-fluid dataset">  
 </c:if>
 			<div class="container span6">
								<div id="barChart${barloop.count }"
									style="min-width: 400px; height: 400px; margin: 0 auto">
								</div>
								
								
								
			
   								<script type="text/javascript">
   
   								$(function() {$('#barChart${barloop.count }').highcharts(${barChart});
   									});
   								
								</script>
			</div>
	<c:if test = "${barloop.count % 2==0}">
	</div>
	</c:if>
</c:forEach>



    </jsp:body>
</t:genericpage>
