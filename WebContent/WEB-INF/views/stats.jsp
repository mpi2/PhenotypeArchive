<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:genericpage>
	<jsp:attribute name="title"> <c:forEach var="parameter" items="${parameters}" varStatus="loop">
  ${parameter.name}
    <c:if test="${ not loop.last}">,</c:if>
	</c:forEach>Statistics for ${gene.symbol} </jsp:attribute>

	<jsp:attribute name="breadcrumb">&nbsp;&raquo; Statistics &raquo; <a href='${baseUrl}/genes/${gene.id.accession}'>${gene.symbol}</a></jsp:attribute>

	<jsp:attribute name="header">

	<script type="text/javascript">
		var gene_id = '${acc}';
	</script>

	<!--    extra header stuff goes here such as extra page specific javascript -->
	<!-- highcharts -->
<script type='text/javascript' src='${baseUrl}/js/charts/highcharts.js'></script>
 <script type='text/javascript' src='${baseUrl}/js/charts/highcharts-more.js'></script> 
<script src="${baseUrl}/js/charts/exporting.js"></script>


	

<style>
</style>

    </jsp:attribute>

	<jsp:attribute name="footer">
	<script>
		$(document).ready(function(){				
							 
				    
//categorical  http://localhost:8080/PhenotypeArchive/stats/genes/MGI:1923523?parameterId=ESLIM_001_001_106
			 
	
         chart_4684femaleHMGU_ = new Highcharts.Chart({
             tooltip: {
                 formatter: function () {
                     return '' + this.series.name + ': ' + this.y + ' (' + (this.y * 100 / this.total).toFixed(1) + '%)';
                 }
             },
             chart: {
                 renderTo: 'categoricalBarChart',
                 type: 'column'
             },
             title: {
                 text: 'Tooth Size'
             },
             credits: {
                 enabled: false
             },
             subtitle: {
                 text: 'Female',
                 x: -20
             },
             xAxis: {
                 categories: ["Control", "homozygote"]
             },
             yAxis: {
                 min: 0,
                 title: {
                     text: 'Percent Occurrance'
                 },
                 labels: {
                     formatter: function () {
                         return this.value + '%';
                     }
                 }
             },
             plotOptions: {
                 column: {
                     stacking: 'percent'
                 }
             },
             series: [{
                 "name": "wt distribution",
                 "data": [7, 7]
             }]
         });
   
			 
			 
			 
//boxplot http://localhost:8080/PhenotypeArchive/stats/genes/MGI:103150?parameterId=GMC_906_001_006
			  var options = {
				        chart: {
				            renderTo: 'container',
				            type: 'spline'
				        },
				        series: [{}]
				    };
			 var boxPlotUrl =  "http://url-to-your-remote-server/jsonp.php?callback=?";
				    $.getJSON(boxPlotUrl,  function(data) {
				        options.series[0].data = data;
				        var chart = new Highcharts.Chart(options);
				    });
				    
				    
				   
				        $('#chartBoxPlot').highcharts({
				            chart: {
				                type: 'boxplot'
				            },
				            tooltip: {
				                formatter: function () {
				                    if (typeof this.point.high === 'undefined') {
				                        return '<b>Observation</b><br/>' + this.point.y;
				                    } else {
				                        return '<b>Genotype: ' + this.key + '</b><br/>LQ - 1.5 * IQR: ' + this.point.low + '<br/>Lower Quartile: ' + this.point.options.q1 + '<br/>Median: ' + this.point.options.median + '<br/>Upper Quartile: ' + this.point.options.q3 + '<br/>UQ + 1.5 * IQR: ' + this.point.options.high + '</b>';
				                    }
				                }
				            },
				            title: {
				                text: 'Chloride'
				            },
				            credits: {
				                enabled: false
				            },
				            subtitle: {
				                text: 'Female',
				                x: -20
				            },
				            legend: {
				                enabled: false
				            },
				            xAxis: {
				                labels: {
				                    style: {
				                        fontSize: 15
				                    }
				                },
				                categories: ["WT", "WT", "HOM", "HOM"]
				            },
				            yAxis: {
				                max: 120.0,
				                min: 102.3,
				                labels: {
				                    style: {
				                        fontSize: 15
				                    }
				                },
				                title: {
				                    text: ''
				                }
				            },
				            series: [{
				                name: 'Observations',
				                data: [
				                    [107.7, 111.3, 112.1, 113.7, 117.3],
				                    [],
				                    [105, 108.6, 109.9, 111, 114.6],
				                    []
				                ],
				                tooltip: {
				                    headerFormat: '<em>Genotype No. {point.key}</em><br/>'
				                }
				            }, {
				                name: 'Observation',
				                color: Highcharts.getOptions().colors[0],
				                type: 'scatter',
				                data: [
				                    [1, 111.2],
				                    [1, 113.6],
				                    [1, 111.4],
				                    [1, 113.8],
				                    [1, 114.6],
				                    [1, 116.8],
				                    [1, 112.4],
				                    [1, 112.8],
				                    [1, 109.4],
				                    [1, 112.6],
				                    [1, 111.6],
				                    [1, 111.6],
				                    [1, 104.8],
				                    [1, 112.6],
				                    [3, 110.6],
				                    [3, 108.4],
				                    [3, 110.2],
				                    [3, 109.6],
				                    [3, 111],
				                    [3, 108.6],
				                    [3, 109.6],
				                    [3, 111.4],
				                    [3, 108],
				                    [3, 109],
				                    [3, 109.6],
				                    [3, 110.2],
				                    [3, 111.6],
				                    [3, 113.4],
				                    [3, 106.8]
				                ],
				                marker: {
				                    fillColor: 'white',
				                    lineWidth: 1,
				                    lineColor: Highcharts.getOptions().colors[0]
				                },
				                tooltip: {
				                    pointFormat: '{point.y:..4f}'
				                }
				            }]
				        });
				   
			
				    
 //time_series http://localhost:8080/PhenotypeArchive/stats/genes/MGI:1923523?parameterId=ESLIM_003_001_004

        chart = new Highcharts.Chart({
            chart: {
                zoomType: 'x',
                renderTo: 'timeSeries',
                type: 'line',
                marginRight: 130,
                marginBottom: 50
            },
            title: {
                text: 'Mean Carbon Dioxide Production',
                x: -20
            },
            credits: {
                enabled: false
            },
            subtitle: {
                text: 'Female',
                x: -20
            },
            xAxis: {
                labels: {
                    style: {
                        fontSize: 15
                    }
                },
                title: {
                    text: 'Time in hours relative to lights out'
                }
            },
            yAxis: {
                max: 127.68023,
                min: 0.0,
                labels: {
                    style: {
                        fontSize: 15
                    }
                },
                title: {
                    text: ' ml/h/animal'
                },
                plotLines: [{
                    value: 0,
                    width: 1,
                    color: '#808080'
                }]
            },
            legend: {
                layout: 'vertical',
                align: 'right',
                verticalAlign: 'top',
                x: -10,
                y: 100,
                borderWidth: 0
            },
            tooltip: {
                shared: true
            },
            series: [{
                tooltip: {
                    headerFormat: '<span style="font-size: 12px">Time In Hours Relative To Lights Out {point.key}</span><br/>',
                    pointFormat: '<span style="font-weight: bold; color: {series.color}">{series.name}</span>:<b>{point.y:.1f}ml/h/animal</b> '
                },
                "name": "Homozygote",
                "data": [
                    [-5, 90.57143],
                    [-4, 87.85714],
                    [-3, 82.190475],
                    [-2, 77.90476],
                    [-1, 77.333336],
                    [0, 89.90476],
                    [1, 97.809525],
                    [2, 92.71429],
                    [3, 94.42857],
                    [4, 100.71429],
                    [5, 96.2381],
                    [6, 94.95238],
                    [7, 92.38095],
                    [8, 83.809525],
                    [9, 80.42857],
                    [10, 93.2381],
                    [11, 95.809525],
                    [12, 85.71429],
                    [13, 80.14286],
                    [14, 71.90476],
                    [15, 73],
                    [16, 73.42857]
                ]
            }, {
                "color": "black",
                "name": "Standard Deviation",
                "data": [
                    [-5, 84.770706, 96.37215],
                    [-4, 80.33195, 95.38234],
                    [-3, 71.04768, 93.333275],
                    [-2, 68.78388, 87.02564],
                    [-1, 67.73993, 86.926735],
                    [0, 78.950745, 100.85878],
                    [1, 89.77448, 105.84457],
                    [2, 80.56462, 104.863945],
                    [3, 81.8049, 107.052246],
                    [4, 89.600586, 111.82798],
                    [5, 79.13574, 113.34045],
                    [6, 76.71924, 113.185524],
                    [7, 80.88541, 103.8765],
                    [8, 68.57923, 99.03982],
                    [9, 65.94903, 94.90811],
                    [10, 71.71623, 114.75996],
                    [11, 79.278885, 112.34016],
                    [12, 74.56017, 96.8684],
                    [13, 69.301796, 90.98392],
                    [14, 59.21656, 84.592964],
                    [15, 58.97859, 87.021416],
                    [16, 62.043537, 84.81361]
                ],
                "type": "errorbar",
                tooltip: {
                    pointFormat: 'SD: {point.low:.1f}-{point.high:.1f}<br/>'
                }
            }, {
                tooltip: {
                    headerFormat: '<span style="font-size: 12px">Time In Hours Relative To Lights Out {point.key}</span><br/>',
                    pointFormat: '<span style="font-weight: bold; color: {series.color}">{series.name}</span>:<b>{point.y:.1f}ml/h/animal</b> '
                },
                "name": "Control",
                "data": [
                    [-5, 91.625],
                    [-4, 91.52778],
                    [-3, 90.02778],
                    [-2, 85.22222],
                    [-1, 82.47222],
                    [0, 88.861115],
                    [1, 96.388885],
                    [2, 94.861115],
                    [3, 96.388885],
                    [4, 99.97222],
                    [5, 97.583336],
                    [6, 98.5],
                    [7, 90.166664],
                    [8, 86.77778],
                    [9, 87.05556],
                    [10, 77.583336],
                    [11, 87.138885],
                    [12, 84.69444],
                    [13, 77.47222],
                    [14, 68.27778],
                    [15, 75.69444],
                    [16, 79.416664]
                ]
            }, {
                "color": "blue",
                "name": "Standard Deviation",
                "data": [
                    [-5, 84.51828, 98.73172],
                    [-4, 83.962875, 99.09268],
                    [-3, 78.49525, 101.5603],
                    [-2, 72.98618, 97.45826],
                    [-1, 71.64768, 93.29676],
                    [0, 78.91223, 98.80999],
                    [1, 87.316864, 105.460915],
                    [2, 85.21256, 104.50966],
                    [3, 85.56754, 107.210236],
                    [4, 88.14372, 111.80073],
                    [5, 85.43573, 109.730934],
                    [6, 87.72039, 109.27961],
                    [7, 73.56805, 106.76529],
                    [8, 70.45057, 103.10499],
                    [9, 69.531784, 104.57932],
                    [10, 64.749504, 90.41716],
                    [11, 68.736786, 105.54099],
                    [12, 67.75789, 101.631],
                    [13, 61.535767, 93.408676],
                    [14, 54.77516, 81.780396],
                    [15, 59.409798, 91.97909],
                    [16, 65.061066, 93.77227]
                ],
                "type": "errorbar",
                tooltip: {
                    pointFormat: 'SD: {point.low:.1f}-{point.high:.1f}<br/>'
                }
            }]
        });

				    
				    
			
			// bubble popup for brief panel documentation
			$.fn.qTip({
				'pageName': 'stats',
				'textAlign': 'left',
				'tip': 'topRight'
			});
			
		});
	</script>
	</jsp:attribute>

	<jsp:body>

	<div class='documentation'>
   		<a href='' class='generalPanel'><img src="${baseUrl}/img/info_20x20.png" /></a>
	</div>	
        

	<div class='topic'>Gene: ${gene.symbol} Parameters: 
	 <c:forEach var="parameter" items="${parameters}" varStatus="loop">
  ${parameter.name}
    <c:if test="${ not loop.last}">,</c:if>
	</c:forEach>
	</div>
	
		<c:if test="${statsError}">
					<div class="alert alert-error">
						<strong>Error:</strong> An issue occurred processing the statistics for this page - results on this page maybe incorrect.
					</div>
		</c:if>
		<c:if test="${noData}">
					<div class="alert alert-error">
						<strong>We don't appear to have any data for this query please try the europhenome graph link instead</strong>
					</div>
		</c:if>
		
		<div class="row-fluid dataset">
			<div class="row-fluid ">
				<div class="container span6">
					<div id="categoricalBarChart"></div>
				</div>
			</div>
		</div>
		
		<div class="row-fluid dataset">
			<div class="row-fluid ">
				<div class="container span6">
		<div id="chartBoxPlot"></div>
		</div>
			</div>
		</div>
		
		<div class="row-fluid dataset">
			<div class="row-fluid ">
				<div class="container span6">
		<div id="timeSeries"></div>
				</div>
			</div>
		</div>
		
		
		<c:if test="${not noData}">			
			<jsp:include page="timeSeriesStatsFrag.jsp"/>
			
			<jsp:include page="categoricalStatsFrag.jsp"/>
			
			<jsp:include page="unidimensionalStatsFrag.jsp"/>
					
		</c:if>




		
 




    </jsp:body>
</t:genericpage>
