<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix='fn' uri='http://java.sun.com/jsp/jstl/functions'%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<t:genericpage>

	<jsp:attribute name="title">IMPC Release Notes</jsp:attribute>
	
	<jsp:attribute name="breadcrumb">&nbsp;&raquo; Release Notes</jsp:attribute>

	<jsp:attribute name="header">

		<script type="text/javascript">
			var drupalBaseUrl = '${drupalBaseUrl}';
		</script>

	</jsp:attribute>

	<jsp:attribute name="bodyTag"><body  class="phenotype-node no-sidebars small-header"></jsp:attribute>
	
	<jsp:attribute name="addToFooter">
			<div class="region region-pinned">
            
        <div id="flyingnavi" class="block">
            
            <a href="#top"><i class="fa fa-chevron-up" title="scroll to top"></i></a>
            
            <ul>
                <li><a href="#top">Release Notes</a></li>
                <li><a href="#new-features">Highlights</a></li>
                <li><a href="#data_reports">Data Reports</a></li>
                <li><a href="#phenome-links">Phenotype Associations</a></li>
                <li><a href="#statistical-analysis">Statistical Analysis</a></li>
                <li><a href="#trends">History</a></li>
            </ul>
            
            <div class="clear"></div>
            
        </div>
        
    </div>
      
	</jsp:attribute>
	<jsp:body>

	<div class="region region-content">
			<div class="block block-system">
				<div class="content">
					<div class="node node-gene">
			<h1 class="title" id="top">IMPC Release Notes</h1>	 
			
				<div class="section">
					<div class="inner">
							<div class="half">			
							<div class="with-label"> <span class="label">IMPC</span>
								<ul>
								<li>Release:&nbsp;${metaInfo["data_release_version"]}</li>
								<li>Published:&nbsp;${metaInfo["data_release_date"]}</li>
								</ul>
							</div>
												
							<div class="with-label"> <span class="label">Statistical Package</span>
								<ul>
								<li>${metaInfo["statistical_packages"]}</li>
								<li>Version:&nbsp;${metaInfo["PhenStat_release_version"]}</li>
								</ul>
							</div>
							
							<div class="with-label"> <span class="label">Genome Assembly</span>
								<ul>
								<li>${metaInfo["species"]}</li>
								<li>Version:&nbsp${metaInfo["genome_assembly_version"]}</li>
								</ul>
							</div>
							</div><!-- half -->
							<div class="half">
							<div class="with-label"> <span class="label">Summary</span>
								<ul>
								<li>Number of phenotyped genes:&nbsp;<fmt:formatNumber type="number" maxFractionDigits="3" value="${metaInfo['phenotyped_genes']}"/></li>
								<li>Number of phenotyped mutant lines:&nbsp;<fmt:formatNumber type="number" maxFractionDigits="3" value="${metaInfo['phenotyped_lines']}"/></li>
								<li>Number of phenotype calls:&nbsp;<fmt:formatNumber type="number" maxFractionDigits="3" value="${metaInfo['statistically_significant_calls']}"/></li>
								</ul>
							</div>
							</br>
							<div class="with-label"> <span class="label">Data access</span>
								<ul>
								<li>Ftp site:&nbsp;<a href="${metaInfo['ftp_site']}">${metaInfo['ftp_site']}</a></li>
								<li>RESTful interfaces:&nbsp;<a href="${baseUrl}/documentation/api-help">APIs</a></li>
								</ul>
							</div>
							</div><!-- half -->
							<div class="clear"></div>
					</div><!--  closing off inner here - but does this look correct in all situations- because of complicated looping rules above? jW -->
				</div><!-- closing off section -->
				
				<div class="section">
				
			    <h2 class="title" id="new-features">Highlights</h2>
				   
					<div class="inner">	 
						<div id="phenotypesDiv">	
							<div class="container span12">
							<h3>Phenotype Association Versioning</h3>
<p>Many factors contribute to the identification of phenodeviants by statistical analysis. This includes the number of mutant and baseline mice, the statistical test used, the selected thresholds and changes to the underlying software that runs the analysis. For these reasons, we will be versioning genotype-to-phenotype associations from data release to data release. A given genotype-to-phenotype may change from release to release.</p>

<h3>Statistical Tests</h3>
<p>In general, we are applying a Fisher Exact Test for categorical data and linear regression for continuous data.  In cases where there is no variability in values for a data parameter in a control or mutant mouse group, a rank sum test is applied instead of a linear regression. The statistical test used is always noted when displayed on the portal or when obtained by the API. Documentation on statistical analysis is available here:
<a href="http://www.mousephenotype.org/data/documentation/statistics-help">http://www.mousephenotype.org/data/documentation/statistics-help</a></p>

<h3>P-value threshold</h3>
<p>In this first release, we are using a p value threshold of &le; 1 x10-4 for all statistical tests to make a phenotype call.  This threshold may be adjusted for some parameters upon further review by statistical experts.</p>

<h3>Clinical Blood Chemistry and Hematology</h3>
<p>Review of PhenStat calls for clinical blood chemistry and hematology by phenotypers at WTSI suggest our current analysis maybe giving a high false positive rate. Alternative statistical approaches are being considered. We suggest looking at the underlying data that supports a phenotype association if it's critical to your research.</p>
	
						</div>
						<div class="clear"></div>
					</div>
					</div>
				</div><!-- end of section -->
								
				<div class="section">
						<h2 class="title" id="data_reports">Data Reports</h2>
						<div class="inner">					
						<div id="phenotypesDiv">	
							<div class="container span12">
							<h3>Lines and Specimens</h3>
							<table id="lines_specimen">
						<thead>
						<tr>
							<th class="headerSort">Phenotyping Center</th>
							<th class="headerSort">Mutant Lines</th>
							<th class="headerSort">Baseline Mice</th>
							<th class="headerSort">Mutant Mice</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="center" items="${phenotypingCenters}">
						<c:set var="phenotyped_lines" value="phenotyped_lines_${center}" />
						<c:set var="control_specimens" value="control_specimens_${center}" />
						<c:set var="mutant_specimens" value="mutant_specimens_${center}" />
						<tr>
							<td>${center}</td>
							<td><fmt:formatNumber type="number" maxFractionDigits="3" value="${metaInfo[phenotyped_lines]}"/></td>
							<td><fmt:formatNumber type="number" maxFractionDigits="3" value="${metaInfo[control_specimens]}"/></td>
							<td><fmt:formatNumber type="number" maxFractionDigits="3" value="${metaInfo[mutant_specimens]}"/></td>
						</tr>
						</c:forEach>
					</tbody>
					</table>
					
							<h3>Experimental Data and Quality Checks</h3>
							<table id="exp_data">
						<thead>
						<tr>
							<th class="headerSort">Data Type</th>
							<c:forEach var="qcType" items="${qcTypes}">
							<th class="headerSort">${fn:replace(qcType, '_', ' ')}</th>
							</c:forEach>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="dataType" items="${dataTypes}">
						<tr><td>${fn:replace(dataType, '_', ' ')}</td>
						<c:forEach var="qcType" items="${qcTypes}">
						<c:set var="qcKey" value="${dataType}_datapoints_${qcType}" />
						<c:set var="qcValue" value="${metaInfo[qcKey]}" />
						<td>
						<c:choose>
						<c:when test="${qcValue != null && qcValue != ''}">
						<fmt:formatNumber type="number" maxFractionDigits="3" value="${qcValue}" />
						<c:if test="${qcType != 'QC_passed'}"><sup>*</sup></c:if>
						</c:when>
						<c:otherwise>0</c:otherwise>
						</c:choose>
						</td>
						</c:forEach>
						</tr>
						</c:forEach>
					</tbody>
					</table>
					<p><sup>*</sup>&nbsp;Excluded from statistical analysis.</p>
					
						<h3>Procedures</h3>
					<div id="lineProcedureChart">
					<script type="text/javascript">
						${lineProcedureChart}
					</script>
					</div>
					
					<h3>Allele Types</h3>
						<table id="allele_types">
						<thead>
						<tr>
							<th class="headerSort">Mutation</th>
							<th class="headerSort">Name</th>
							<th class="headerSort">Mutant Lines</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="alleleType" items="${alleleTypes}">
						<c:set var="alleleTypeKey" value="targeted_allele_type_${alleleType}" />
						<tr>
							<td>Targeted Mutation</td>
							<td>${alleleType}</td>
							<td>${metaInfo[alleleTypeKey]}</td>
						</tr>
						</c:forEach>
					</tbody>
					</table>
					<p>Mouse knockout programs:&nbsp;${metaInfo['mouse_knockout_programs']}</p>
							</div>
						</div>
						<div class="clear"></div>
					</div><!--  closing off inner here -->
					</div>
					
					<div class="section">
					<h2>Distribution of Phenotype Annotations</h3>
					<div class="inner">
						<div id="distribution">
							<script type="text/javascript">
								${annotationDistributionChart}
							</script>
						</div>
					</div>	
				</div><!-- closing off section -->
		
		
			
			<div class="section">
				<h2 class="title" id="section-associations"> Status </h2>		
		      <div class="inner">
		           	
			       <h3>Overall</h3>
							<div class="half">
								<div id="genotypeStatusChart">
			          		<script type="text/javascript">${genotypeStatusChart.getChart()}</script> 
								</div>       	
							</div>
							<div class="half">
			         	<div id="phenotypeStatusChart">
			          		<script type="text/javascript">${phenotypeStatusChart.getChart()}</script> 
								</div>   
							</div>
		         	  <div class="clear"></div>   
		         	  
			        <div>
			        <h3>By Center</h3>
			       		<div class="half">
				         	<div id="genotypeStatusByCenterChart">
				         		<script type="text/javascript">${genotypingDistributionChart}</script> 
							</div>   
							</div>
			       		<div class="half">
				         	<div id="phenotypeStatusByCenterChart">
				         		<script type="text/javascript">${phenotypingDistributionChart}</script> 
								</div>   
							</div>
			   	   	<div class="clear"></div>   
			      </div> 
			      <p>More charts and status information are available from <a href="https://www.mousephenotype.org/imits/v2/reports/mi_production/komp2_graph_report_display">iMits</a>. </p>  
		      </div>     
		    </div> <!-- section -->
	<!-- 				
				<div class="section">
						<h2 class="title" id="section-associations"> Sexual Dimorphism </h2>		
		            <div class="inner">
										<div id="sexualDimorphismChart">
			            		<script type="text/javascript">${sexualDimorphismChart}</script> 
										</div>  
										
										<table>
										<thead>
											<tr>
											<th class="headerSort">Status</th>
											<th class="headerSort">Number of Calls</th>
											</tr>
										</thead>
										<tbody>
											<c:forEach var="status" items="${sexualDimorphismSummary.entrySet()}">
												<tr><td>${status.getKey().getText()}</td><td>${status.getValue()}</td></tr>
											</c:forEach>
										</tbody>
													
										</table>     	
		         </div>		            
		    </div> <!-- section -->
		   
		    
		    <!-- div class="section">
						<h2 class="title" id="section-associations"> Fertility and Viability </h2>		
		            <div class="inner">
										
										<div class="half">
											<div id="fertilityChart">
				            		<script type="text/javascript">${fertilityChart}</script> 
											</div>  
											
											<table>
											<thead>
												<tr>
												<th class="headerSort">Fertility</th>
												<th class="headerSort">Number of Strains</th>
												</tr>
											</thead>
											<tbody>
												<c:forEach var="status" items="${fertilityMap.entrySet()}">
													<tr><td>${status.getKey()}</td><td>${status.getValue()}</td></tr>
												</c:forEach>
											</tbody>
														
											</table>   
									</div>  	
									
									<div class="half">
											<div id="viabilityChart">
				            		<script type="text/javascript">${viabilityChart}</script> 
											</div>  
											
											<table>
											<thead>
												<tr>
												<th class="headerSort">Viability</th>
												<th class="headerSort">Number of Strains</th>
												</tr>
											</thead>
											<tbody>
												<c:forEach var="status" items="${viabilityMap.entrySet()}">
													<tr><td>${status.getKey()}</td><td>${status.getValue()}</td></tr>
												</c:forEach>
											</tbody>
														
											</table>   
									</div>  	
									
									<div class="clear"> </div>
		         </div>		            
		    </div--> <!-- section -->
		    
		
		
		
		
				<div class="section">
				
			    <h2 class="title" id="phenome-links">Phenotype Associations</h2>
				   
					<div class="inner">	 
						<div id="phenotypesDiv">	
							<div class="container span12">
							
							<div id="callProcedureChart">
							<script type="text/javascript">
								${callProcedureChart}
							</script>
							</div>
					
							<h3>Phenotype Associations Overview</h3>
							<p>We provide a 'phenome' overview of statistically significant calls. 
							By following the links below, you'll access the details of the phenotype calls for each center.</p>
							<table>
							  <thead>
								<tr>
								<th class="headerSort">Phenotyping Center</th>
								<th class="headerSort">Significant MP Calls</th>
								<th class="headerSort">Pipeline</th>
								</tr>
							</thead>
							<tbody>
							<c:forEach var="center" items="${phenotypingCenters}">
							<c:set var="centerMapKey" value="phenotype_pipelines_${center}" />
							<c:set var="pipelines" value="${metaInfo[centerMapKey]}" />
							<c:forEach var="pipeline" items="${fn:split(pipelines, ',')}">
							<tr><td>${center}</td><td><a href="${baseUrl}/phenome?phenotyping_center=${center}&pipeline_stable_id=${pipeline}">Browse</td><td>${pipeline}</td></tr>
							</c:forEach>
							</c:forEach>
							</tbody>
							</table>
							</div>
						</div>
					</div>
				</div><!-- end of section -->			
			
				<div class="section">
				
			    <h2 class="title" id="statistical-analysis">Statistical Analysis</h2>
				   
					<div class="inner">	 
						<div id="phenotypesDiv">	
							<div class="container span12">
							
							<h3>Statistical Methods</h3>
							<table id="allele_types">
							<thead>
							<tr>
								<th class="headerSort">Data</th>
								<th class="headerSort">Statistical Method</th>
							</tr>
							</thead>
							<tbody>
							<c:forEach var="entry" items="${statisticalMethods}">
							<c:set var="dataType" value="${entry.key}" />
							<c:forEach var="method" items="${entry.value}">
							<tr>
							<td>${dataType}</td>
							<td>${method}</td>
							</tr>
							</c:forEach>
							</c:forEach>
							</tbody>
							</table>
							
							<h3>P-value distributions</h3>
							<c:forEach var="entry" items="${statisticalMethods}">
							<c:set var="dataType" value="${entry.key}" />
							<c:forEach var="method" items="${entry.value}">
							<c:set var="chartName" value="${statisticalMethodsShortName[method]}Chart" />
							<div id="${chartName}">
							<script type="text/javascript">
								${distributionCharts[chartName]}
							</script>
							</div>
							</c:forEach>
							</c:forEach>
							</div>
						</div>
					</div>
				</div><!-- end of section -->
				
				<div class="section">
				
			    <h2 class="title" id="trends">History</h2>
				   
					<div class="inner">	 
						<div id="phenotypesDiv">	
							<div class="container span12">				
									<h3>Trends</h3>
									<div id="trendsChart">
									<script type="text/javascript">
										${trendsChart}
									</script>
									</div>
									<br/>
									<br/>
									<br/>
									<div id="datapointsTrendsChart">
									<script type="text/javascript">
										${datapointsTrendsChart}
									</script>
									</div>
									<br/>
									<br/>
									<br/>
									<div id="topLevelTrendsChart">
									<script type="text/javascript">
										${topLevelTrendsChart}
									</script>
									</div>
									<br/>
									<br/>
									<br/>
									<h3>Previous Releases</h3>
									<ul>
									<c:forEach var="release" items="${releases}">
									<li><a href="${baseUrl}/release_notes/IMPC_Release_Notes_${release}.html">Release ${release} notes</a></li>
									</c:forEach>
									</ul>
							</div>
						</div>
					</div>
				</div><!-- end of section -->				
				
		</div>
	</div>
</div>
</div>

</jsp:body>

</t:genericpage>


