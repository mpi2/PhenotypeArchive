<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:genericpage>

	<jsp:attribute name="title">IMPC Search</jsp:attribute>
	<jsp:attribute name="bodyTag"><body id="top" class="page-node searchpage one-sidebar sidebar-first small-header"></jsp:attribute> 

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
	<link href="${baseUrl}/css/searchPage.css" rel="stylesheet" type="text/css" />
	</jsp:attribute>

	<jsp:attribute name="footer">	
	
	<!-- hash state: the url params will change when
		(1) the facet count is clicked
	    (2) the back or forward button is clicked
	    The params are parsed to load dataTable -->
	
	<!-- <script type="text/javascript">
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
			
		});
	</script>-->
	<!-- end of hash state stuff -->	
	
	</jsp:attribute>

    <jsp:body>		
		<div class="region region-sidebar-first">
			<div id='facet' class='fblock block'>	
				<div class="head">Filter your search</div>
				<div class='content'>
				    <div class="ffilter">
						<ul id="facetFilter">
							<li class="has-sub gene"><span class='fcap'>Gene</span></li>
							<li class="has-sub mp"><span class='fcap'>Phenotype</span></li>
							<li class="has-sub disease"><span class='fcap'>Disease</span></li>
							<li class="has-sub ma"><span class='fcap'>Anatomy</span></li>
							<li class="has-sub pipeline"><span class='fcap'>Pipeline</span></li>
							<li class="has-sub images"><span class='fcap'>Images</span></li>					
						</ul>
						<div id="resetFilter"><a href="${baseUrl}/search">Remove all facet filters</a></div>
					</div>
										
					<p class='documentation' class='title textright'>
						<a href='' id='facetPanel' class="fa fa-question-circle" aria-describedby="qtip-26"></a>
					</p>
										
					<div id='facetSrchMsg'><img src='img/loading_small.gif' /> Processing search ...</div> 
					<div class="flist">
						<ul>
							<li class="fmcat open" id="gene">
								<span class="flabel">Genes</span>
								<span class="fcount"></span>
								<ul></ul>
							</li>
							<li class="fmcat" id="mp">
								<span class="flabel">Phenotypes</span>
								<span class="fcount"></span>
								<ul></ul>
							</li>
							<li class="fmcat" id="disease">
								<span class="flabel">Diseases</span>
								<span class="fcount"></span>
								<ul></ul>
							</li>
							<li class="fmcat" id="ma">
								<span class="flabel">Anatomy</span>
								<span class="fcount"></span>
								<ul></ul>
							</li>
							<li class="fmcat" id="pipeline">
								<span class="flabel">Procedures</span>
								<span class="fcount"></span>
								<ul></ul>
							</li>
							<li class="fmcat" id="images">
								<span class="flabel">Images</span>
								<span class="fcount"></span>
								<ul></ul>
							</li>
						</ul>
					</div>				
				</div>
			</div>	
		</div>	
				
		<div class="region region-content">
			<div class="block block-system">
				<div class='content'>
					<div class='searchcontent'>
						<div id="bigsearchbox" class="block">
							<div class="content">								
								<p><i id="sicon" class="fa fa-search"></i>
									<input id="s" type="text" value="" placeholder="Search">
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
					<div class="clear"></div>
					<!-- facet filter block -->								
					<!-- container to display dataTable -->									
					<div class="HomepageTable" id="mpi2-search"></div>				
				</div>
			</div>
		</div>		       
        
         <script>
        $(document).ready(function() {        		
        	$(document).ready(function(){        		
    			$.fn.qTip({'pageName':'search'		 					
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
    							
    				//var url = $.param.fragment();	 // not working with jQuery 10.0.1
    				var url = $(location).attr('hash');			
    				console.log('hash change URL: '+ '/search' + url);
    				
    				if ( /search\/?$/.exec(location.href) ){
    					// reload page
    					window.location.reload();
    				}
    				
    				var oHashParams = $.fn.parseHashString(window.location.hash.substring(1));
    				
    				oHashParams.widgetName = oHashParams.coreName? oHashParams.coreName : oHashParams.facetName;	                
					oHashParams.widgetName += 'Facet';
    				
					console.log(oHashParams);
    				console.log('from widget open: '+ MPI2.searchAndFacetConfig.widgetOpen);
    				
    				if ( window.location.search.match(/q=/) ){
    					oHashParams.q = window.location.search.replace('?q=','')
    				}
    				else if ( typeof oHashParams.q == 'undefined' ){
    					oHashParams.q = window.location.search == '' ? '*:*' : window.location.search.replace('?q=', '');	    					
    				}
    				
    				
    				if ( MPI2.searchAndFacetConfig.widgetOpen ){
    					MPI2.searchAndFacetConfig.widgetOpen = false;
    						    				
	    				// search by keyword (user's input) has no fq in url when hash change is detected
	    				if ( oHashParams.fq ){			
	    					
	    					if ( oHashParams.coreName ){	    						
	    						$.fn.removeFacetFilter();
	    						oHashParams.coreName += 'Facet'; 					
	    					}
	    					else {						
	    						// parse selected checkbox(es) of this facet
	    						var facet = oHashParams.facetName;
	    						var aFilters = [];
	    						//$('ul#facetFilter li.' + facet + ' li a').each(function(){
	    						$('ul#facetFilter li.ftag a').each(function(){							
	    							aFilters.push($(this).text());
	    						});														
	    						
	    						//console.log('filter: ' + aFilters );
	    						oHashParams.filters = aFilters;
	    						//oHashParams.facetName = facet + 'Facet';
	    						oHashParams.facetName = facet;	    						
	    					}
	    					$.fn.loadDataTable(oHashParams);
	    				}
    				}
	    			else {	    				   				  				
	    				console.log('back button');	    				
	    				console.log(oHashParams);
	    				    			
	    				var refreshFacet = oHashParams.coreName ? false : true;	    				
						$.fn.parseUrlForFacetCheckboxAndTermHighlight(oHashParams, refreshFacet);
	    				
	    				$.fn.loadDataTable(oHashParams);
	    			}
    			});		
    			
    		});     	
        	
            // wire up the example queries
               $("a.example").click(function(){
                    $('#examples').modal('hide');
                    document.location.href = $(this).attr('href');
                    document.location.reload();
            });

            // Message to IE users
            //$.fn.ieCheck();
        });        
        </script>
			
						
    </jsp:body>

</t:genericpage>


