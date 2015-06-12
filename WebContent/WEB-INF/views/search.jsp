<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:genericpage>

	<jsp:attribute name="title">IMPC Search</jsp:attribute>
	<jsp:attribute name="bodyTag"><body id="top" class="page-node searchpage one-sidebar sidebar-first small-header"></jsp:attribute> 

	<jsp:attribute name="header">
	<style>
	
	</style>
	<link href="${baseUrl}/css/searchPage.css" rel="stylesheet" type="text/css" />
	</jsp:attribute>

	<jsp:attribute name="addToFooter">	
	<div class="region region-pinned">
            
        <div id="flyingnavi" class="block">
            
            <a href="#top"><i class="fa fa-chevron-up" title="scroll to top"></i></a>
            
            <ul>
                <li><a href="#top">Search</a></li>
            </ul>
            
            <div class="clear"></div>
            
        </div>
        
    </div>		
	
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
							<li class="has-sub impc_images"><span class='fcap'>IMPC Images</span></li>	
							<li class="has-sub images"><span class='fcap'>Images</span></li>					
						</ul>
					
						<div id="resetFilter"><span id='rmFilters'>Remove all facet filters</span></div>
					</div>
										
					<p class='documentation title textright'>
						<a href='' id='facetPanel' class="fa fa-question-circle" aria-describedby="qtip-26"></a>
					</p>
										
					<div id='facetSrchMsg'><img src='img/loading_small.gif' /> Processing search ...</div> 
					<div class="flist">
						<ul>
							<li class="fmcat" id="gene">
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
							<!-- <li class="fmcat" id="pipeline">
								<span class="flabel">Procedures</span>
								<span class="fcount"></span>
								<ul></ul>
							</li>
							-->
							<li class="fmcat" id="impc_images">
								<span class="flabel">IMPC Images</span>
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
					<!--  <div class='searchcontent'>
						<div id="bigsearchbox" class="block">
							<div class="content">								
								<p><i id="sicon" class="fa fa-search"></i>
									
									<div class="ui-widget">
										<input id="s">
									</div>
								</p>									
							</div>
						</div>
					</div>
					
					<div class="textright">
						<a id="searchExample" class="">View example search</a>						
					</div>	
					-->
					<div class="clear"></div>
					<!-- facet filter block -->								
					<!-- container to display dataTable -->									
					<div class="HomepageTable" id="mpi2-search"></div>				
				</div>
			</div>
		</div>		       
        
        <compress:html enabled="${param.enabled != 'false'}" compressJavaScript="true">	    
			<script type='text/javascript' src='${baseUrl}/js/searchAndFacet/searchAndFacetConfig.js?v=${version}'></script>
			<script type='text/javascript' src='${baseUrl}/js/searchAndFacet/geneFacetWidget.js?v=${version}'></script>
			<script type='text/javascript' src='${baseUrl}/js/searchAndFacet/mpFacetWidget.js?v=${version}'></script>
			<script type='text/javascript' src='${baseUrl}/js/searchAndFacet/maFacetWidget.js?v=${version}'></script>
			<!--  <script type='text/javascript' src='${baseUrl}/js/searchAndFacet/pipelineFacetWidget.js?v=${version}'></script> -->
			<script type='text/javascript' src='${baseUrl}/js/searchAndFacet/diseaseFacetWidget.js?v=${version}'></script>
			<script type='text/javascript' src='${baseUrl}/js/searchAndFacet/impc_imagesFacetWidget.js?v=${version}'></script>
			<script type='text/javascript' src='${baseUrl}/js/searchAndFacet/imagesFacetWidget.js?v=${version}'></script>
			<script type='text/javascript' src='${baseUrl}/js/searchAndFacet/search.js?v=${version}'></script> 
	    </compress:html>        
       
         <script>        		
       	$(document).ready(function(){
       		'use strict';	
       		
       		// back button will not see this js
       		MPI2.searchAndFacetConfig.update.widgetOpen = false;
       		MPI2.searchAndFacetConfig.update.pageReload = true;
       		
       		$('span.facetCount').text(''); // default when page loads
       		$('input#s').val('');  // clears input when pages loads
       			
       		// default search when search page loads
       		if ( /search\/?$/.exec(location.href) ){
       			//console.log('default search load')
       			// do default gene search by * when search page loads	
       			// handed over to no hash change code below
       		
       		} 
       		else if ( window.location.search == '?q=' ){
       		
       			// catches user hitting ENTER on search input box of homepage
       			document.location.href = baseUrl + '/search';		
       		}
       		else if ( location.href.indexOf('/search?q=') != -1 
       				|| location.href.indexOf('/search#q=*:*') != -1 
       				|| location.href.indexOf('/search#q=*') != -1 
       				|| location.href.indexOf('/search#fq=') != -1 ){   	
       			
       			// load page based on url hash parameters	
       			$('input#s').val(decodeURI($.fn.fetchQueryStr()));
       			
       			oUrlParams = $.fn.parseHashString(window.location.hash.substring(1));	
       			if (typeof oUrlParams.fq == 'undefined'){
       				oUrlParams.noFq = true;
       			}
       		
       			$.fn.fetchSolrFacetCount(oUrlParams);	
       		}
       		else {
       			// do not understand the url, redirect to error page
       			document.location.href = baseUrl + '/404.jsp';		
       		}
       		
       		$('span#rmFilters').click(function(){
       			
       			if ( window.location.search != '' ){
       				if ( MPI2.searchAndFacetConfig.update.notFound ){
       					// no result, remove filter
       					$.fn.resetUrlFqStr();
       				}
       				else {
	       				// need to include search keyword in query when all filters are removed 
	       				var foundCount = 0;
	       				$('ul#facetFilter li.ftag a').each(function(){
	    					$(this).click();
	    				});	
       				}
       			}
       			else {
       				document.location.href = baseUrl + '/search';
       			}
       		});
       	 	
       		// make "search" button on banner active (highlighted)
       		$('nav#mn ul.menu > li:first-child').addClass('active');
       		       		
       		<c:if test="${isLoggedIn}">       			
       			MPI2.searchAndFacetConfig.isLoggedIn = true;
       		</c:if>;
       		
   			$.fn.qTip(
   				{'pageName':'search'}		 					
   			);  			 						
   			
   			// non hash tag keyword query
   			<c:if test="${not empty q}">				
   				/*oUrlParams = {};
   				oUrlParams.q = "${q}";    				
   				$.fn.fetchSolrFacetCount(oUrlParams);*/				
   			</c:if>;
   					
   			// hash tag query
   			// catch back/forward buttons and hash change: loada dataTable based on url params
   			$(window).bind("hashchange", function() {
   				
   				// for page feedback on search page that involves hashtag change
   				$('a.feedback_simple').attr('href', '/website-feedback?page=' + document.URL);
   				
   				MPI2.searchAndFacetConfig.update.hashChange = true;
   				//var hashStr = $.param.fragment();	 // not working with jQuery 10.0.1
   				var hashStr = $(location).attr('hash');	
   				//MPI2.searchAndFacetConfig.currentFq = hashStr.match(/fq=.+\&/)[0].replace(/fq=|\&/g,'');
   				
   				//console.log('hash change URL: '+ '/search' + hashStr);
   				var oUrlParams = _process_hash();
   				//console.log(oUrlParams)
   				
   				/* deals with 3 events here:
   				 	1. added/removed filter 
   					2. back button
   					*/
   				if ( MPI2.searchAndFacetConfig.update.filterChange ){
   					//console.log('added or removed a filter');
    				MPI2.searchAndFacetConfig.update.filterChange = false;
    				
    				// MA,MP facet stays open when adding/removing filters
    				$('li#mp.fmcat, li#ma.fmcat').each(function(){
    					if (oUrlParams.facetName == $(this).attr('id')) {
    						$(this).addClass('open');
    					}
    				});
    				
    				var sumCount = 0;	
    				$('div.flist li.fmcat span.fcount').each(function(){
    					sumCount += parseInt($(this).text());
    				});
    				
    				// after adding/removing a filter, check if we got any result
    				//console.log('check sumcoung: '+ sumCount);
    				if ( sumCount == 0 ){
    					
    					if ( MPI2.searchAndFacetConfig.update.notFound ){
    						
    						// deals with facet filters having zero results	
    						MPI2.searchAndFacetConfig.update.notFound = false;    						
    						$.fn.fetchSolrFacetCount(oUrlParams);
    						
    						if ( MPI2.searchAndFacetConfig.update.lastFilterNotFound ){
        						MPI2.searchAndFacetConfig.update.lastFilterNotFound = false;
        						$.fn.loadDataTable(oUrlParams);
    						}
    					}
    					else {
    						$.fn.showNotFoundMsg();  
    					}
   					}
    				else {
    					$.fn.loadDataTable(oUrlParams);
    				}
    			}
   				
   				else if ( MPI2.searchAndFacetConfig.update.widgetOpen ){
   					//console.log('widget open');	
   				
   					MPI2.searchAndFacetConfig.update.widgetOpen = false; // reset
   					
   					if ( MPI2.searchAndFacetConfig.update.rebuilt  ){
   						// just reset flag, no need to load dataTable again (already done)
   						MPI2.searchAndFacetConfig.update.rebuilt = false;
   					}
   					/* else if ( !MPI2.searchAndFacetConfig.update.mainFacetDone && oUrlParams.fq ){
    					$.fn.loadDataTable(oUrlParams);
    				} */
    				else {
    					$.fn.loadDataTable(oUrlParams);
    				}
   					
   				} 
   				else if ( MPI2.searchAndFacetConfig.update.pageReload == true ){
					//console.log('reload with widget open true');
					// eg. default search page loading 
					if ( /search\/?$/.test(window.location.href) ){
	    				// when the url become ..../search
						document.location.href = baseUrl + '/search';
	    			}
					else {
						//rebuildFilters(oUrlParams);
						$.fn.rebuildFilters(oUrlParams);
					}
				} 
   				else if ( !MPI2.searchAndFacetConfig.update.pageReload ){
    				//console.log('back button OR widget open event');
    				
    				MPI2.searchAndFacetConfig.update.rebuilt = false;  //reset
    				
    				if ( /search\/?$/.test(window.location.href) ){
        				// when the url become ..../search
    					document.location.href = baseUrl + '/search';
        			}
    				else if ( $(location).attr('hash').indexOf('facet=') == -1 ){
    					// if facet on url is unknown, eg. users hit ENTER too fast. 
    					// When this url is reached by back button, just replace it with
    					// document.location.href = ...
    					// caveat: back button clicks becomes a infinite loop
    					
    					// the new url will not work w/o reload, why?
    					document.location.href = baseUrl + '/search'+ window.location.search + $(location).attr('hash');
    					document.location.reload(true);
    				}
    				else {
    					//rebuildFilters(oUrlParams); 
    					$.fn.rebuildFilters(oUrlParams);
    				}
				}
   				
   			});		
   			
    		if ( ! MPI2.searchAndFacetConfig.update.hashChange ){
    			//console.log('page reload: no hash change detected')
				
    			var oUrlParams = $.fn.parseHashString(window.location.hash.substring(1));
    			//console.log(oUrlParams);
    			
    			if ( window.location.search != '' ){
    				// qrep value of q
    				oUrlParams.q = $.fn.fetchQueryStr();
    			}
    			
    			//if ( $.isEmptyObject(oUrlParams || typeof oUrlParams.coreName != 'undefined' ) ){
    			if ( $.isEmptyObject(oUrlParams) ){
    				//console.log('search page default load: /search or /search?');
    				$.fn.fetchSolrFacetCount(oUrlParams);	
    			}
    			else if ( MPI2.searchAndFacetConfig.update.mainFacetNone ){
    				MPI2.searchAndFacetConfig.update.mainFacetDone = false;
    				MPI2.searchAndFacetConfig.update.mainFacetDoneReset = true;
    				MPI2.searchAndFacetConfig.update.mainFacetNone = false;
					$.fn.rebuildFilters(oUrlParams);
    			}
    			else {
        			if ( MPI2.searchAndFacetConfig.update.mainFacetDone ){
        				MPI2.searchAndFacetConfig.update.mainFacetDone = false;
        				MPI2.searchAndFacetConfig.update.mainFacetDoneReset = true;
        			}
        			else {
    					$.fn.rebuildFilters(oUrlParams);
        			}   			
    			}
    		}
    		
    		function _process_hash(){
    			var oUrlParams = $.fn.parseHashString(window.location.hash.substring(1));
    			
    			if ( window.location.search != '' && window.location.hash == '' ){
    				// has q only, no hash string
    				//console.log('has q')
    				
    			}
    			
    			else {
    				
					// img_ prefix is to for fields marker_type, procedure_id, top_level_mp_term and selected_top_level_ma_term
	   				// from images core. This is shown on the url and used to rebuild facet filters 
	   				// - ie, we can tell if a filter is for MP or images, eg
	   				// when doing the query, strip it out
	   				
					if ( typeof oUrlParams.fq != 'undefined' ){
						oUrlParams.oriFq = oUrlParams.fq;
						oUrlParams.fq = oUrlParams.fq.replace(/img_|impcImg_/g,'');
					}
					
	   				oUrlParams.widgetName = oUrlParams.coreName? oUrlParams.coreName : oUrlParams.facetName;	
					oUrlParams.widgetName += 'Facet';
					
	   				oUrlParams.q = window.location.search != '' ? $.fn.fetchQueryStr() : '*:*';
    			}	
    			return oUrlParams;
   			}
   							
    		
            // Message to IE users
            //$.fn.ieCheck();
        });        
        </script>
			
						
    </jsp:body>

</t:genericpage>


