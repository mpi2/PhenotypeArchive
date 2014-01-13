<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:genericpage>

	<jsp:attribute name="title">IMPC Search</jsp:attribute>

	<jsp:attribute name="header">
	<style>
	
	/*.pagination {width:450px;}
	.pagination, 
	.pagination a, 
	.pagination span { white-space:nowrap; font-size:0.95em; border:none; padding:0; margin:0;}
	.pagination span.page-info { white-space:nowrap; border:none; padding:0; margin:0;}
	.mpi2-grid {border: 0;}
	div.last-search{margin-top:0;}*/
	</style>
	</jsp:attribute>

	<jsp:attribute name="footer">	
	
	<!-- hash state: the url params will change when
		(1) the facet count is clicked
	    (2) the back or forward button is clicked
	    The params are parsed to load dataTable -->
	
	<script type="text/javascript">
	jQuery(document).ready(function($){	
		
			$.fn.qTip({'pageName':'search',
					'textAlign':'left',
					'tip':'topLeft',
					'posX':20,
					'posY':0
			});
			
			// non hash tag keyword query
			<c:if test="${not empty q}">				
				oHashParams = {};
				oHashParams.q = "${q}";
				$.fn.fetchSolrFacetCount(oHashParams);				
			</c:if>;
					
			// hash tag query
			// catch back/forward buttons and hash change: loada dataTable based on url params
			$(window).bind("hashchange", function() {
								
				var url = $.param.fragment();				
				//console.log('hash change URL: '+ '/search#' + url);
				var oHashParams = $.fn.parseHashString(window.location.hash.substring(1));
				
				if ( typeof oHashParams.q === 'undefined' ){
					oHashParams.q = window.location.search == '' ? '*:*' : window.location.search.replace('?q=', '');					
				}
				
				// search by keyword (user's input) has no fq in url when hash change is detected
				if ( oHashParams.fq ){				
					// back/forward button navigation: 
					// make sure checkboxes are updated according to url
					$.fn.removeFacetFilter(oHashParams.coreName);
					//console.log(oHashParams.fq);					
					var pageReload;  // this controls checking which subfacet to open (ie, show by priority). 
									 // Set to undefined for no checking here, as we are now capturing hash change and not page reload
					$.fn.parseUrlForFacetCheckboxAndTermHighlight(oHashParams.q, oHashParams.fq, oHashParams.coreName+'Facet', pageReload);				
					
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
			<li class='has-sub disease'>Diseases</li>
		</ul>
       
       <!--  facet skeleton on left sidebar -->
		<!-- <div id="wrapper">
		    <div id="userKeyword" class='rounded-corners'></div>	
			<div id="content">
				<div class="ui-widget">
					<div id='dataView'></div>
				</div>
				<div id='mainDataContainer'></div>
			</div>
		</div>-->
		
		<div class="region region-sidebar-first">
			<div id='facet' class='block'>	
				<div class="head">Filter your search</div>
			    <div class='content'>
			        
			    	<h2 class='documentation' class='title'>
								<a href='' id='facetPanel'><i class="fa fa-question-circle pull-right" aria-describedby="qtip-26"></i></a></h2>
												
					<div id="leftSideBar" class='rounded-corners span3'>																		
									
						<!-- <div id='facetBrowser'><img src='img/loading_small.gif' /> Processing search ...</div>--> 
						<div id='geneFacet'>
							<div><div class='facetCat'>Genes</div><span class='facetCount countDisplay'></span></div>
							<div class='facetCatList'></div>
						</div>						
						<div id='mpFacet'>
							<div><div class='facetCat'>Phenotypes</div><span class='facetCount countDisplay'></span></div>
							<div class='facetCatList'></div>
						</div>
			                        <div id='diseaseFacet'>
							<div><div class='facetCat'>Diseases</div><span class='facetCount countDisplay'></span></div>
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
				</div>	
				<!--  end of facet skeleton on left sidebar -->
			</div>
		</div>	
		<div class="region region-content">
			<div class="block block-system">
				<div class='content'>
					<div class='searchcontent'>
						<div id="bigsearchbox" class="block">
							<div class="content">								
								<p><i id="sicon" class="fa fa-search"></i>
									<input style="width: 71%; margin-left: 10px;" id="s" type="text" value="Test dummy input" placeholder="Search">
								</p>									
							</div>
						</div>
					</div>
					<div class="textright">
						<a class="has-tooltip" data-hasqtip="19">View example search</a>
						<div class="data-tooltip">
							<h3>Example Searches</h3>
								<p>Sample queries for several fields are shown. Click the desired query to execute any of the samples.
									<b>Note that queries are focused on Relationships, leaving modifier terms to be applied as filters.</b>
								</p>
							<h5>Gene query examples</h5>
								<p>
								<a href="#">Akt2</a>
								- looking for a specific gene, Akt2
								<br>
								<a href="#">*rik</a>
								- looking for all Riken genes
								<br>
								<a href="#">hox*</a>
								- looking for all hox genes
								</p>
							<h5>Phenotype query examples</h5>
								<p>
								<a href="#">abnormal skin morphology</a>
								- looking for a specific phenotype
								<br>
								<a href="#">ear</a>
								- find all ear related phenotypes
								</p>
							<h5>Procedure query Example</h5>
								<p>
								<a href="#">grip strength</a>
								- looking for a specific procedure
								</p>
							<h5>Prase query Example</h5>
								<p>
								<a href="#">"zinc finger protein"</a>
								- looking for genes whose product is zinc finger protein
								</p>
						</div>
					</div>	
					<!-- container to display dataTable --> 
					<!-- <div class="HomepageTable span9" id="mpi2-search"></div>-->					
					<div class="HomepageTable" id="mpi2-search"></div>				
				</div>
			</div>
		</div>
						
    </jsp:body>

</t:genericpage>


