<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<c:set var="redirectedJavascript">
	<c:if test="${not empty queryString}">
		<script type="text/javascript">		

		var solrBaseURL_bytemark = '${drupalBaseUrl}/bytemark/solr/';
		var solrBaseURL_ebi      = '${drupalBaseUrl}/mi/solr/';	

		var data = {};
		data.q         = '${queryString}';	
		data.type      = '${queryType}';		
		data.geneFound = '${queryGeneFound}';	// use to load gene grid	
				 		 
		window.jQuery('document').ready(function() {	
			
			// do not use qf: 'auto_suggest', defType: 'edismax' for gene, as the config is different at Sanger
			var facetParams = MPI2.searchAndFacetConfig.facetParams;
									
			if ( facetParams[data.type] && data.type != 'gene'){
				data.fq = facetParams[data.type].fq;
				data.qf = facetParams[data.type].qf;
				data.defType = facetParams[data.type].defType;
				data.wt = facetParams[data.type].wt;
			}
			
			// refresh the side bar
			window.jQuery('div#leftSideBar').mpi2LeftSideBar({			
				solrBaseURL_bytemark : solrBaseURL_bytemark,
				solrBaseURL_ebi : solrBaseURL_ebi,
				data : data, // key q: query string
				geneGridElem : 'div#mpi2-search'				
			});			
			
			// refresh the grid		
			if ( data.type != 'undefined' ){
				
				// invoke sanger grid
				//window.jQuery('#mpi2-search').trigger('search', [{type: data.type, solrParams: data}]);
				
				// invode dataTable
				var oInfos = {};
		 		oInfos.params = $.fn.stringifyJsonAsUrlParams(data);
		 		oInfos.solrCoreName = facetParams[data.type].solrCoreName;
		 		oInfos.mode = facetParams[data.type].solrCoreName + 'Grid';
		 		oInfos.dataTablePath = "${baseUrl}/dataTable";
		 		
		 		var dTable = $.fn.fetchEmptyTable(facetParams[data.type].tableHeader, facetParams[data.type].cols, oInfos.mode);
		 	   			 	    	
		 	   	var title = $.fn.upperCaseFirstLetter(facetParams[data.type].type);	    	
		 	   	var gridTitle = $('<div></div>').attr({'class':'gridTitle'}).html(title);
		 	 
		 	   	$('div#mpi2-search').html('');		 	  
		 	   	$('div#mpi2-search').append(gridTitle, dTable);		 	    	   	
		 	   	
		 	   	$.fn.invokeDataTable(oInfos);
			}
			$('div#facetBrowser').html(MPI2.searchAndFacetConfig.endOfSearch);
			
		});
		</script>
	</c:if>
</c:set>


<t:genericpage>

	<jsp:attribute name="title">IMPC Generic search And Data Facets</jsp:attribute>

	<jsp:attribute name="header">
	<style>
	.pagination {width:450px;}
	.pagination, 
	.pagination a, 
	.pagination span { white-space:nowrap; font-size:0.95em; border:none; padding:0; margin:0;}
	.pagination span.page-info { white-space:nowrap; border:none; padding:0; margin:0;}
	.mpi2-grid {border: 0;}
	div.last-search{margin-top:0;}
	</style>
	</jsp:attribute>

	<jsp:attribute name="footer">	
	<script type="text/javascript" src="${baseUrl}/js/utils/searchAndFacetConfig.js"></script>
	${redirectedJavascript}
	
	<!-- <script type="text/javascript" src="${baseUrl}/js/searchAndFacet/grid.js"></script>-->
	
	</jsp:attribute>

    <jsp:body>
    	<c:if test="${not empty message}">
		<div class="alert alert-info">
			<button type="button" class="close" data-dismiss="alert">&times;</button>
			${message}
		</div>
    	</c:if>
		<div id="wrapper">
			<div id="content">
				<div class="ui-widget">
					<div id='dataView'></div>
				</div>
				<div id='mainDataContainer'></div>
			</div>
		</div>
		<div class='row-fluid'> 
		<div id="leftSideBar" class='rounded-corners span3'>			
			<div id='facetBrowser'><img src='img/loading_small.gif' /> Processing search ...</div> 
			<div id='geneFacet'>
				<div><div class='facetCat'>Genes</div><span class='facetCount'></span></div>
				<div class='facetCatList'></div>
			</div>						
			<div id='phenotypeFacet'>
				<div><div class='facetCat'>Phenotypes<img id='mpFacetInfo' class='facetInfo' src='img/msg.png' /><span id='mpMsg'>Note that the total 
				Phenotypes count refers to unique MP terms and may not agree with the sum of the numbers to the right of the top level 
				MP terms as an MP term may be associated with more than one top level MP terms.</span></div><span class='facetCount'></span></div>
				<div class='facetCatList'></div>
			</div>
			<!-- <div id='tissueFacet'>				
				<div><div class='facetCat'>Tissues<img id='maFacetInfo'  class='facetInfo' src='img/msg.png' /><span id='maMsg'>Note that the total 
				tissue count refers to unique MA terms and may not agree with the sum of the numbers to the right of the top level 
				MA terms as an MA term may be associated with more than one top level MA terms.</span></div><span class='facetCount'></span></div>				
				<div class='facetCatList'></div>
			</div>-->
			<div id='pipelineFacet'>
				<div><div class='facetCat'>Procedures</div><span class='facetCount'></span></div>
				<div class='facetCatList'></div>
			</div>
			<div id='imageFacet'>
				<div><div class='facetCat'>Images</div><span class='facetCount'></span></div>
				<div class='facetCatList'></div>
			</div>
			
		</div>
	    <div class="HomepageTable span9" id="mpi2-search"></div>   
	    </div>
    </jsp:body>

</t:genericpage>

