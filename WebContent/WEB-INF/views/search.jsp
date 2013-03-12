<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<c:set var="redirectedJavascript">
	<c:if test="${not empty queryString}">
		<script type="text/javascript">		
		
		var data = {};
		data.q    = '${q}';	
		data.core = '${core}';
		data.fq   = decodeURI('${fq}');
							 		 
		window.jQuery('document').ready(function() {	
			
			// do not use qf: 'auto_suggest', defType: 'edismax' for gene, as the config is different at Sanger
			var facetParams = MPI2.searchAndFacetConfig.facetParams;
			var facetName = data.core + 'Facet';
									
			if ( facetParams[facetName] && data.core != 'gene'){
				//data.fq = facetParams[facetName].fq;
				data.qf = facetParams[facetName].qf;
				data.defType = facetParams[facetName].defType;
				data.wt = facetParams[facetName].wt;
			}
			
			// refresh the side bar
			window.jQuery('div#leftSideBar').mpi2LeftSideBar({				
				data : data, // key q: query string
				geneGridElem : 'div#mpi2-search'				
			});			
			
			// refresh the grid		
			if ( data.core != 'undefined' ){
						
				// invode dataTable
				var oInfos = {};
		 		oInfos.params = $.fn.stringifyJsonAsUrlParams(data);
		 		oInfos.solrCoreName = facetParams[facetName].solrCoreName;
		 		oInfos.mode = facetParams[facetName].solrCoreName + 'Grid';
		 		oInfos.dataTablePath = "${baseUrl}/dataTable";
		 		
		 		var dTable = $.fn.fetchEmptyTable(facetParams[facetName].tableHeader, facetParams[facetName].cols, oInfos.mode);
		 	   			 	    	
		 	   	var title = $.fn.upperCaseFirstLetter(facetParams[facetName].type);	    	
		 	   	var gridTitle = $('<div></div>').attr({'class':'gridTitle'}).html(title);
		 	 		 	  	
		 	   	$('div#mpi2-search').html('');		 	  
		 	   	$('div#mpi2-search').append(gridTitle, dTable);		 	    	   	
		 	   
		 	   //	$.fn.invokeDataTable(oInfos);
			}
			//$('div#userKeyword').html('Search keyword: ' + data.q);
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
	
	${redirectedJavascript}
	
	<!-- hash state for back button -->
	<script type="text/javascript">
		$(document).ready(function(){
			
			$(window).bind("hashchange", function(e) {
				// In jQuery 1.4, use e.getState( "url" );				
				var url = $.param.fragment();				
				//console.log('hash change URL: '+ '/search#' + url);
								
				// reload wanted url				
				if ( (url != '' && !$.fn.checkHashStrForSkippingReload(url)) 
						|| ( url == '' && /search$/.test(document.location.href) ) ){					
					document.location.reload();	
				}				
			});	
			
		});
	</script>
	<!-- end of hash state for back button -->	
	
	</jsp:attribute>

    <jsp:body>
		<div id="wrapper">
		    <div id="userKeyword" class='rounded-corners'></div>	
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
			<div id='mpFacet'>
				<div><div class='facetCat'>Phenotypes</div><span class='facetCount'></span></div>
				<div class='facetCatList'></div>
			</div>
			<!-- <div id='tissueFace//var url = $.bbq.getState( "url" );t'>				
				<div><div class='facetCat'>Tissues</div><span class='facetCount'></span></div>				
				<div class='facetCatList'></div>
			</div>-->
			<div id='pipelineFacet'>
				<div><div class='facetCat'>Procedures</div><span class='facetCount'></span></div>
				<div class='facetCatList'></div>
			</div>
			<div id='imagesFacet'>
				<div><div class='facetCat'>Images</div><span class='facetCount'></span></div>
				<div class='facetCatList'></div>
			</div>
			
		</div>
	    <div class="HomepageTable span9" id="mpi2-search"></div>   
	    </div>
    </jsp:body>

</t:genericpage>


