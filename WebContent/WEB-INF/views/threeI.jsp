<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix='fn' uri='http://java.sun.com/jsp/jstl/functions'%>

<t:genericpage>

	<jsp:attribute name="title">3i | Infection, Immunology, Immunophenotyping Project Information</jsp:attribute>
	<jsp:attribute name="breadcrumb">&nbsp;&raquo; <a
			href="${baseUrl}/3i">3i</a> &raquo; 3i</jsp:attribute>
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
                <li><a href="#top">3i</a></li>
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
				
						<h1 class="title" id="top">Project Page: 3i</h1>
				
						<div class="section">
							<div class=inner>
									<div class="floatleft">
										<a href="http://www.immunophenotyping.org/"><img src="${baseUrl}/img/3i.png" height="85" width="130"></a>
									</div>
									<br/> <br/>
									<p> The <a href="http://www.immunophenotyping.org/">3i project</a> is building an encyclopaedia of immunological gene functions to advance basic and translational research.	</p>	
							</div>
						</div>	<!-- section -->
						
						<div class="section">
							<h2 class="title">Gene to Procedure Phenodeviance Heat Map</h2>
							<div class=inner>
								<div id="legend">
									<table>
										<tr>
											<td> 
												<div class="table_legend_color" style="background-color: rgb(191, 75, 50)"></div> 
												<div class="table_legend_key">Deviance Significant</div>
											</td>
											<td> 
												<div class="table_legend_color" style="background-color: rgb(247, 157, 70)"></div> 
												<div class="table_legend_key">Data analysed, no significant call</div>
											</td>
											<td> 
												<div class="table_legend_color" style="background-color: rgb(0, 0, 0)"></div> 
												<div class="table_legend_key">Could not analyse</div>
											</td>
											<td> 
												<div class="table_legend_color" style="background-color: rgb(119, 119, 119)"></div> 
												<div class="table_legend_key">No data</div>
											</td>
										</tr>
									</table>
								</div>
								<div id="3iHeatmap" style="overflow: hidden; overflow-x: auto;">	</div>
							</div>
						</div>
					</div>

		</div>
	</div>
</div>

	<script>
		$(document).ready(function() {
			$.fn.qTip({
				'pageName' : '3i',
				'textAlign' : 'left',
				'tip' : 'topLeft'
			}); // bubble popup for brief panel documentation					
		});
		var geneHeatmapUrl = "../threeIMap?project=threeI";
		$.ajax({
			url : geneHeatmapUrl,
			cache : false
		}).done(function(html) {
			$('#3iHeatmap').append(html);
			//$( '#spinner'+ id ).html('');

		});
	</script>
	
	
</jsp:body>


</t:genericpage>
