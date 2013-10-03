<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:genericpage>

	<jsp:attribute name="title">IMPC Search</jsp:attribute>

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
	
	<!-- hash state: the url params will change when
		(1) the facet count is clicked
	    (2) the back or forward button is clicked
	    The params are parsed to load dataTable -->
	
	<script type="text/javascript">
		$(document).ready(function(){	
			
			// non hash tag keyword query
			<c:if test="${not empty q}">				
				oHashParams = {};
				oHashParams.q = "${q}";
				$.fn.fetchSolrFacetCount(oHashParams);				
			</c:if>;
					
			// hash tag query
			// catch back/forward buttons and hash change: loada dataTable based on url params
			$(window).bind("hashchange", function(e) {
				// In jQuery 1.4, use e.getState( "url" );
				
				var url = $.param.fragment();				
				//console.log('hash change URL: '+ '/search#' + url);
				var oHashParams = $.fn.parseHashString(window.location.hash.substring(1));
				
				if ( typeof oHashParams.q === 'undefined' ){
					oHashParams.q = window.location.search == '' ? '*:*' : window.location.search.replace('?q=', '');					
				}
				
				// keyword search has no fq in url when hash change is detected
				if (oHashParams.fq ){				
					// back/forward button navigation: 
					// make sure checkboxes are updated according to url
					$.fn.removeFacetFilter(oHashParams.coreName);
					var aFields = MPI2.searchAndFacetConfig.facetParams[oHashParams.coreName+'Facet'].subFacetFqFields;				
					$.fn.parseUrlForFacetCheckboxAndTermHighlight(oHashParams.q, oHashParams.fq, oHashParams.coreName+'Facet', aFields);				
					
					$.fn.loadDataTable(oHashParams.q, oHashParams.fq, oHashParams.coreName+'Facet'); 
				}
			});
						
			$('div#filterToggle').click(function(){	
				
				var ul = $('ul#facetFilter');	
				if ( ul.is(":visible") ){				
					ul.hide();					
				}
				else {				
					ul.show();				
				}
			});
		});
	</script>
	<!-- end of hash state stuff -->	
	
	</jsp:attribute>

    <jsp:body>
       
    
       <!-- search filter display -->
       	<div id='filterToggle'>Show facet filters</div>       		
	   	<ul id='facetFilter'> 
	   	    <li class='has-sub none'>no filter added</li>
	   		<li class='has-sub gene'>Genes</li>
	   		<li class='has-sub mp'>Phenotypes</li>
			<li class='has-sub ma'>Anatomy</li>	 
			<li class='has-sub pipeline'>Procedures</li>
			<li class='has-sub images'>Images</li>
		</ul>
       	
       <!--  facet skeleton on left sidebar -->
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
				<div><div class='facetCat'>Genes</div><span class='facetCount countDisplay'></span></div>
				<div class='facetCatList'></div>
			</div>						
			<div id='mpFacet'>
				<div><div class='facetCat'>Phenotypes</div><span class='facetCount countDisplay'></span></div>
				<div class='facetCatList'></div>
			</div>
			<div id='maFacet'>
				<div><div class='facetCat'>Anatomy</div><span class='facetCount countDisplay'></span></div>
				<div class='facetCatList'></div>
			</div>
			<div id='pipelineFacet'>
				<div><div class='facetCat'>Procedures</div><span class='facetCount countDisplay'></span></div>
				<div class='facetCatList'></div>
			</div>
			<div id='imagesFacet'>
				<div><div class='facetCat'>Images</div><span class='facetCount countDisplay'></span></div>
				<div class='facetCatList'></div>
			</div>
			
		</div>
		<!--  end of facet skeleton on left sidebar -->
		
		<!--  container to display dataTable -->
	    <div class="HomepageTable span9" id="mpi2-search"></div>   
	    </div>
    </jsp:body>

</t:genericpage>


