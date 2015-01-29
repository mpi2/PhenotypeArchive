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
		            <div class="inner">
		            	<c:forEach var="center" items="${centerDataMap}">
		            	<br/>
		            	<br/>
		            	<div id="${center.key}"></div>
		            <script>	
		            
		            
		            var strainData=${centerDataJSON[center.key]};
		            
		            $(function () {
    $('#${center.key}').highcharts({
        chart: {
            type: 'column'
        },
        title: {
            text: '${center.key}'
        },
        subtitle: {
            text: 'Source: <a href="http://en.wikipedia.org/wiki/List_of_cities_proper_by_population">Wikipedia</a>'
        },
        xAxis: {
            type: 'category',
            labels: {
                rotation: -45,
                style: {
                    fontSize: '13px',
                    fontFamily: 'Verdana, sans-serif'
                }
            }
        },
        yAxis: {
            min: 0,
            title: {
                text: 'Number of Procedures for Strain'
            }
        },
        legend: {
            enabled: false
        },
        tooltip: {
            pointFormat: 'Population in 2008: <b>{point.y:.1f} procedures</b>'
        },
        series: [{
            name: 'Population',
            data: ${centerDataJSON[center.key]},
            dataLabels: {
                enabled: true,
                rotation: -90,
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
