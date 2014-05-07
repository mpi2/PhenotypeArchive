<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:genericpage>

	<jsp:attribute name="breadcrumb">&nbsp;&raquo; Statistics &raquo; <a href='${baseUrl}/genes/${gene.id.accession}'>${gene.symbol}</a></jsp:attribute>
<jsp:attribute name="bodyTag"><body  class="chartpage no-sidebars small-header"></jsp:attribute>
	<jsp:attribute name="header">

    </jsp:attribute>

		<jsp:attribute name="addToFooter">
			 <script>
			 //ajax chart caller code
$(document).ready(function(){				
				    
	$('.chart').each(function(i, obj) 
	{
		var graphUrl=$(this).attr('graphUrl');
		var id=$(this).attr('id');
		var chartUrl=graphUrl+'&experimentNumber='+id;
			$.ajax({
				  url: chartUrl,
				  cache: false
			})
				  .done(function( html ) {
				    $( '#'+ id ).append( html );
				    $( '#spinner'+ id ).html('');
				    if(html.search( 'section-associations' )===-1){//if this element not found in the html then no graph present so remove placeholder section
				    	console.log('element found');
				    	//$( '#'+ id ).html( '' ); 
				    	 console.log('id='+$('#'+ id).parent().parent().html( '' ));
				    }
				    
			});
			 
	});	 
	
		
	
});
		 </script>
		 
  </jsp:attribute>

	<jsp:body>
	
	
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
		
		 <c:forEach var="graphUrl" items="${allGraphUrlSet}" varStatus="graphUrlLoop">			
  								  							
			  <div class="section">
			  			<div class="inner">
			  					<div class="chart" graphUrl="${baseUrl}/chart?${graphUrl}"  id="${graphUrlLoop.count}">			
			  							<div id="spinner${graphUrlLoop.count}"><i class="fa fa-refresh fa-spin"></i></div>	
			  					</div>
			  			</div>
			 </div>
  						
		</c:forEach>


		<div class="section">
			<div id="exportIconsDivGlobal"></div>
		</div>
		
			
		<script>
			 				
				$(document)
						.ready(
								function() {
							//		alert("unidimensional");
									$('div#exportIconsDivGlobal').html("");
									$('div#exportIconsDivGlobal').html(
											$.fn.loadFileExporterUI({
												label : 'Export data as: ',
												textPos : "textleft",
												formatSelector : {
													TSV : 'tsv_phenoAssoc',
													XLS : 'xls_phenoAssoc'
												},
												class : 'fileIcon exportButton'
											}));
			
									var params = window.location.href.split("/charts?")[1]; //.split("&");
									var paramList = window.location.href.split("/charts?")[1].split("&");
									var windowLocation = window.location;
									var sex = (params.indexOf("gender\=") > 0) ? params.split("gender\=")[1].split("\&")[0] : null;
									var paramIdList = [];
									var mgiGeneId = [];
									var phenotypingCenter = [];
									var strains = [];
									var zygosity = [];
									var pipelineStableId = [];
									var allele = [];
									for (var k = 0; k < paramList.length; k++){
										if (paramList[k].indexOf("parameter_stable_id") >= 0){
											paramIdList.push(paramList[k].replace("parameter_stable_id=", ""));
										}
										else if (paramList[k].indexOf("accession") >= 0){
											mgiGeneId.push(paramList[k].replace("accession=", ""));
										}
										else if (paramList[k].indexOf("phenotyping_center") >= 0){
											phenotypingCenter.push(paramList[k].replace("phenotyping_center=", ""));
										}
										else if (paramList[k].indexOf("strain") >= 0){
											strains.push(paramList[k].replace("strain=", ""));
										}
										else if (paramList[k].indexOf("zygosity") >= 0){
											zygosity.push(paramList[k].replace("zygosity=", ""));
										}
										else if (paramList[k].indexOf("pipeline_stable_id") >= 0){
											pipelineStableId.push(paramList[k].replace("pipeline_stable_id=", ""));
										}
										else if (paramList[k].indexOf("allele_accession") >= 0){
											allele.push(paramList[k].replace("allele_accession=", ""));
										}
									}
									
									initFileExporter({
										mgiGeneId : mgiGeneId,
										externalDbId : 3,
										fileName : 'graphDataDump_'
												+ mgiGeneId[0].replace(/:/g, '_'),
										solrCoreName : 'experiment',
										dumpMode : 'all',
										baseUrl : windowLocation,
										parameterStableId : paramIdList,
										zygosity: zygosity,
										sex: sex,
										allele: allele,
										strains: strains,
										phenotypingCenter: phenotypingCenter,
										pipelineStableId: pipelineStableId,
										page : "unidimensionalData",
										gridFields : 'gene_accession,date_of_experiment,discrete_point,gene_symbol,data_point,zygosity,sex,date_of_birth,time_point',
										params : ""
									});
			
									function initFileExporter(conf) {
										$('button.fileIcon')
												.click(
														function() {
															var fileType = $(this).text();
															var url = baseUrl + '/export';
															var sInputs = '';
															for ( var k in conf) {
																	sInputs += "<input type='text' name='" + k + "' value='" + conf[k] + "'>";
															}
															sInputs += "<input type='text' name='fileType' value='"
																	+ fileType
																			.toLowerCase()
																	+ "'>";
															var form = $("<form action='"+ url + "' method=get>"
																	+ sInputs + "</form>");
															_doDataExport(url, form);
														});
									}
			
									function _doDataExport(url, form) {
										$
												.ajax({
													type : 'GET',
													url : url,
													cache : false,
													data : $(form).serialize(),
													success : function(data) {
														$(form).appendTo('body').submit()
																.remove();
													},
													error : function() {
							//							alert("Oops, there is error during data export..");
													}
												});
									}
								});
		</script>	
 </jsp:body>
    
</t:genericpage>
