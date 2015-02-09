<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix='fn' uri='http://java.sun.com/jsp/jstl/functions'%>

<t:genericpage>

	<jsp:attribute name="title">IDG | IMPC Project Information</jsp:attribute>
	<jsp:attribute name="breadcrumb">&nbsp;&raquo; <a
			href="${baseUrl}/IDG">IDG</a> &raquo; IDG</jsp:attribute>
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
                <li><a href="#top">IDG</a></li>
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
				
						<h1 class="title" id="top">Project Page: IDG</h1>
				
						<div class="section">
							<div class=inner>
									<div class="floatright">
										<img src="${baseUrl}/img/idgLogo.png" height="85" width="130">
									</div>
									<p>
									<b>Illuminating the Druggable Genome (IDG)</b> is an NIH Common Fund project focused on collecting, integrating and making available biological data on approx 300 genes that have been identified as potenital therapeutic targets. KOMP2 - funded IMPC Centers are creating where possible knockout mouse strains for this consortium.
									</p>	
							</div>
						</div>	<!-- section -->
				
				
						 <div class="section" >
								<h2 class="title"	id="section-associations"> Latest status for IDG gene set </h2>		
		            <div class="inner">
		            	
									<div  class="half">
										<div id="genotypeStatusChart">
			            		<script type="text/javascript">
													${genotypeStatusChart.getChart()}
											</script> 
										</div>       	
									</div>
									<div  class="half">
			            	<div id="phenotypeStatusChart">
			            		<script type="text/javascript">
													${phenotypeStatusChart.getChart()}
											</script> 
										</div>   
									</div>
		            	  <div class="clear"></div>   
		            </div>
		            
		        </div> <!-- section -->
		                            
		        
		        <div class="section" id="phenotypePValueDistribution">
								<h2 class="title"	id="section-associations"> Phenotype P value distribution for IDG genes </h2>		
		            <div class="inner">
										<!-- Associations table -->
										<c:if test="${chart != null}">
											<!-- phenome chart here -->
							  				<div id="phenomeChart">
							  				<a class="various" id="iframe" data-fancybox-type="iframe"></a></div>
											<script type="text/javascript">
												${chart}
											</script>	
										</c:if>	
		
		            </div>
		        </div> <!-- section -->                    
		        
						
						<div class="section">
						<h2 class="title">Gene to MP Term Heat Map</h2>
							<div class=inner>
							<div id="legend">
								<table>
									<tr>
										<td>Key:</td>
													<td>Phenotype Detected:</td>
													<td style="background-color: rgb(191, 75, 50)"></td>
													<td>No Phenotype Detected:</td>
													<td style="background-color: rgb(119, 119, 119)"></td>
													<td>No Data Available:</td>
													<td style="background-color: rgb(0, 0, 0)"></td>
									</tr>
								</table>
							</div>
									<div id="geneHeatmap" style="overflow: hidden; overflow-x: auto;">
									</div>
							</div>
						</div>
					</div>

		</div>
	</div>
</div>

		
	<script>
		$(document).ready(function() {
			$.fn.qTip({
				'pageName' : 'idg',
				'textAlign' : 'left',
				'tip' : 'topLeft'
			}); // bubble popup for brief panel documentation					
		});
		var geneHeatmapUrl = "../geneHeatMap?project=idg";
		$.ajax({
			url : geneHeatmapUrl,
			cache : false
		}).done(function(html) {
			$('#geneHeatmap').append(html);
			//$( '#spinner'+ id ).html('');

		});
	</script>
	
</jsp:body>


</t:genericpage>
