<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix='fn' uri='http://java.sun.com/jsp/jstl/functions'%>

<t:genericpage>

	<jsp:attribute name="title">Center Progress Information</jsp:attribute>
	<jsp:attribute name="breadcrumb">&nbsp;&raquo; <a
			href="${baseUrl}/centerProgress">Center Progress</a></jsp:attribute>
	<jsp:attribute name="bodyTag">
		<body class="chartpage no-sidebars small-header">
	
	</jsp:attribute>
	<jsp:attribute name="header">
</jsp:attribute>
	<jsp:attribute name="addToFooter">
		<div class="region region-pinned">
            
        <div id="flyingnavi" class="block">
            
            <a href="#top"><i class="fa fa-chevron-up"
					title="scroll to top"></i></a>
            
            <ul>
                <li><a href="#top">Center Progress</a></li>
            </ul>
            
            <div class="clear"></div>
            
        </div>
        
    </div>
		
    </jsp:attribute>


	<jsp:body>
    <!-- Assign this as a variable for other components -->
		<script type="text/javascript">
			var base_url = '${baseUrl}';
		</script>
		
 	<div class="region region-content">
		<div class="block block-system">
			<div class="content">
				<div class="node node-gene">
				
						<h1 class="title" id="top">Center Progress</h1>
				
						<div class="section">
							<div class=inner>
								
									
									Center progress information
										
							</div>
						</div>	<!-- section -->
				
				
						 <div class="section" >
								<h2 class="title"	id="section-associations"> Latest Status for each Center </h2>	
					<!-- post QC data -->	
		            <div class="inner">
		            	<c:forEach var="center" items="${centerDataMap}">
		            	<br/>
		            	<br/>
		            	<div id="${center.key}"></div>
		            <script>	
		            
		            $(function () {
    $("[id='${center.key}']").highcharts({
    	colors:[ 'rgba(9, 120, 161,0.7)', 'rgba(247, 157, 70,0.7)', 'rgba(61, 167, 208,0.7)', 'rgba(247, 181, 117,0.7)', 'rgba(100, 178, 208,0.7)', 'rgba(191, 75, 50,0.7)', 'rgba(3, 77, 105,0.7)', 'rgba(166, 30, 1,0.7)', 'rgba(36, 139, 75,0.7)', 'rgba(191, 75, 50,0.7)', 'rgba(1, 121, 46,0.7)', 'rgba(166, 30, 1,0.7)', 'rgba(51, 51, 51,0.7)', 'rgba(255, 201, 67,0.7)', 'rgba(191, 151, 50,0.7)'],
        chart: {
            type: 'bar',
            height: 1000
        },
        title: {
            text: '${center.key}'
        },
        xAxis: {
            type: 'category',
            labels: {
                rotation: 0,
                style: {
                    fontSize: '9px',
                    fontFamily: 'Verdana, sans-serif'
                }
            }
        },
        yAxis: {
            min: 0,
            title: {
                text: 'Number of Procedures for Colony'
            }
        },
        legend: {
            enabled: false
        },
        tooltip: {
            pointFormat: 'Number of procedures with data for this colony: <b>{point.y:.1f}</b>'
        },
        series: [{
            name: 'Population',
            data: ${centerDataJSON[center.key]},
            dataLabels: {
                enabled: true,
                color: '#FFFFFF',
                align: 'right',
                x: 4,
                y: 10,
                style: {
                    fontSize: '13px',
                    fontFamily: 'Verdana, sans-serif',
                    textShadow: '0 0 3px black'
                }
            }
        }]
    });
});
		            </script>
		            		
		            		
		            		
		            		
		            		
		            		
		            		
		            		
		            		<!-- preQc Data -->
		            	<div id="preQc_${center.key}"></div>
		            <script>	
		            
		            
		            $(function () {
    $("[id='preQc_${center.key}']").highcharts({
    	colors:['rgba(239, 123, 11,0.7)', 'rgba(9, 120, 161,0.7)', 'rgba(247, 157, 70,0.7)', 'rgba(61, 167, 208,0.7)', 'rgba(247, 181, 117,0.7)', 'rgba(100, 178, 208,0.7)', 'rgba(191, 75, 50,0.7)', 'rgba(3, 77, 105,0.7)', 'rgba(166, 30, 1,0.7)', 'rgba(36, 139, 75,0.7)', 'rgba(191, 75, 50,0.7)', 'rgba(1, 121, 46,0.7)', 'rgba(166, 30, 1,0.7)', 'rgba(51, 51, 51,0.7)', 'rgba(255, 201, 67,0.7)', 'rgba(191, 151, 50,0.7)'],
        chart: {
            type: 'bar',
            height: 1000
        },
        title: {
            text: 'Pre QC Data For ${center.key}'
        },
        xAxis: {
            type: 'category',
            labels: {
                rotation: 0,
                style: {
                    fontSize: '9px',
                    fontFamily: 'Verdana, sans-serif'
                }
            }
        },
        yAxis: {
            min: 0,
            title: {
                text: 'Number of Procedures for Colony'
            }
        },
        legend: {
            enabled: false
        },
        tooltip: {
            pointFormat: 'Number of procedures with data for this colony: <b>{point.y:.1f}</b>'
        },
        series: [{
            name: 'Population',
            data: ${preQcCenterDataJSON[center.key]},
            dataLabels: {
                enabled: true,
                color: '#FFFFFF',
                align: 'right',
                x: 4,
                y: 10,
                style: {
                    fontSize: '13px',
                    fontFamily: 'Verdana, sans-serif',
                    textShadow: '0 0 3px black'
                }
            }
        }]
    });
});
		            </script>
		            		
		           
		            		
		            		
		           </c:forEach>
									
		          <div class="clear"></div>   
		         </div>
		            
		       </div> <!-- section -->
		                            
		        
		         
		        
						
						
			</div>

		</div>
	</div>
</div>
	
</jsp:body>


</t:genericpage>
