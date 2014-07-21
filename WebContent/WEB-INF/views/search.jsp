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
									<!-- <input id="s" type="text" value="" placeholder="Search">-->
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
			<script type='text/javascript' src='${baseUrl}/js/searchAndFacet/pipelineFacetWidget.js?v=${version}'></script>
			<script type='text/javascript' src='${baseUrl}/js/searchAndFacet/diseaseFacetWidget.js?v=${version}'></script>
			<script type='text/javascript' src='${baseUrl}/js/searchAndFacet/imagesFacetWidget.js?v=${version}'></script>
			<script type='text/javascript' src='${baseUrl}/js/searchAndFacet/search.js'></script> 
	    </compress:html>        
       
         <script>        		
       	$(document).ready(function(){
       		'use strict';	
       		//console.log('reload');
       		// back button will not see this js
       		MPI2.searchAndFacetConfig.update.pageReload = true;
       		
       		//console.log('reload');
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
       			
       			oHashParams = $.fn.parseHashString(window.location.hash.substring(1));	
       			if (typeof oHashParams.fq == 'undefined'){
       				oHashParams.noFq = true;
       			}
       			//console.log(oHashParams);
       			$.fn.fetchSolrFacetCount(oHashParams);	
       		}
       		else {
       			// do not understand the url, redirect to error page
       			document.location.href = baseUrl + '/404.jsp';		
       		}
       		
       		// search via ENTER
       		$('input#s').keyup(function (e) {		
       		    if (e.keyCode == 13) { // user hits enter
       		    	//alert('enter: '+ MPI2.searchAndFacetConfig.matchedFacet)
       		    	var input = $('input#s').val().trim();
       		    
       		    	MPI2.searchAndFacetConfig.update.kwSearch = true;
       		    	
       		    	if (input == ''){
       		    		
       		    		// if there is no existing facet filter, reload with q
       		    		if ( $('ul#facetFilter li.ftag').size() == 0 ){
       		    			//baseUrl + '/search?q=' + input;
       		    			document.location.href = baseUrl + '/search';
       		    		}
       		    		else {
       		    			window.location.search = "q=*:*";
       		    		}
       		    	}
       		    	else if (! MPI2.searchAndFacetConfig.matchedFacet){
       		    		// user hits enter before autosuggest pops up	
       		    		// ie, facet info is unknown
       		    		//alert('enter-2');
       		    		//document.location.href = baseUrl + '/search?q=' + input;
       		    		window.location.search = 'q=' + input;
       		    		
       		    		// if there is no existing facet filter, reload with q
       		    		if ( $('ul#facetFilter li.ftag').size() == 0 ){
       		    			baseUrl + '/search?q=' + input;
       		    		}
       		    		// handed over to hash change code
       		    	}
       		    	else {	
       		    		//alert('enter-3');
       		    		window.location.search = 'q=' + input;
       		    		window.location.hash = 'facet=' + MPI2.searchAndFacetConfig.matchedFacet;
       		    	}
       		    }
       		});

       		$('span#rmFilters').click(function(){
       			if ( window.location.search != '' ){
       				// need to include search keyword in query when all filters are removed 
       				$('ul#facetFilter li.ftag a').each(function(){
    					$(this).click();
    				});	 
       			}
       			else {
       				document.location.href = baseUrl + '/search';
       			}
       		});
       		
       		$('a#searchExample').mouseover(function(){
       			return false;
       		})
       		
       		// autosuggest 
       		$(function() {
	       		$( "input#s" ).autocomplete({
	       			source: function( request, response ) {
		       			$.ajax({
			       			url: "${solrUrl}/autosuggest/select?wt=json&qf=auto_suggest&defType=edismax",				       			
			       			dataType: "jsonp",
			       			'jsonp': 'json.wrf',
			       			data: {
			       				q: request.term
		       				},
			       			success: function( data ) {

			       				MPI2.searchAndFacetConfig.matchedFacet = false; // reset
			       				var docs = data.response.docs;	
			       				var aKV = [];
			       				for ( var i=0; i<docs.length; i++ ){
			       					
			       					for ( var key in docs[i] ){
			       						
			       						var facet;
			       						if ( key == 'docType' ){	
			       							facet = docs[i][key].toString();
			       						}
			       						else {	
			       							var term = docs[i][key].toString().toLowerCase();	
			       							var termHl = term;
			       							
			       							// highlight multiple matches (partial matches) while users typing in search keyword(s)
			       							
			       							/* --- deals with wildcard in query --- */
			       							
			       							// this won't work
			       							//var termStr = $.ui.autocomplete.escapeRegex($('input#s').val().trim(' ').split(' ').join('|'));
			       							
			       							// this works: let jquery autocomplet UI handles the wildcard
			       							var termStr = $('input#s').val().trim(' ').split(' ').join('|').replace(/\*/g, ''); 
			       							
			       							/* --- endl of deals with wildcard in query --- */
			       							
			       							var re = new RegExp("(" + termStr + ")", "gi") ;
			       							var termHl = termHl.replace(re,"<b class='sugTerm'>$1</b>");
			       							aKV.push("<span class='" + facet + "'>" + "<span class='dtype'>"+ facet + ' : </span>' + termHl + "</span>");
			       							
			       							if (i == 0){
			       								// take the first found in autosuggest and open that facet
			       								MPI2.searchAndFacetConfig.matchedFacet = facet;			       							
			       							}
			       						}
			       					}
			       				}
			       				response( aKV );			       				
			       			}
		       			});
	       			},
	       			focus: function (event, ui) {
	       		       this.value = $(ui.item.label).text().replace(/<\/?span>|^\w* : /g,'');
	       		       event.preventDefault(); // Prevent the default focus behavior.
	       			},
	       			minLength: 3,
	       			select: function( event, ui ) {
	       				// select by mouse / KB
	       				//console.log(this.value + ' vs ' + ui.item.label);
	       				//var oriText = $(ui.item.label).text();
	       				
	       				var facet = $(ui.item.label).attr('class');
	       				
	       				// handed over to hash change to fetch for results	 				
	       				document.location.href = baseUrl + '/search?q=' + this.value + '#facet=' + facet; 	
	       				
	       				// prevents escaped html tag displayed in input box
	       				event.preventDefault(); return false; 
	       				
	       			},
	       			open: function(event, ui) {
	       				//fix jQuery UIs autocomplete width
	       				$(this).autocomplete("widget").css({
	       			    	"width": ($(this).width() + "px")
	       			    });
	       			   				
	       				$( this ).removeClass( "ui-corner-all" ).addClass( "ui-corner-top" );	       				
	       			},
	       			close: function() {
	       				$( this ).removeClass( "ui-corner-top" ).addClass( "ui-corner-all" );
	       			}
	       		}).data("ui-autocomplete")._renderItem = function( ul, item) { // prevents HTML tags being escaped
       				return $( "<li></li>" ) 
     				  .data( "item.autocomplete", item )
     				  .append( $( "<a></a>" ).html( item.label ) )
     				  .appendTo( ul );
     			};
       		});
       		
       	 	
       		// make "search" menu point active (highlighted)
       		$('nav#mn ul.menu > li:first-child').addClass('active');
       		       		
       		<c:if test="${isLoggedIn}">       			
       			MPI2.searchAndFacetConfig.isLoggedIn = true;
       		</c:if>;
       		
   			$.fn.qTip(
   				{'pageName':'search'}		 					
   			);  			 						
   			
   			// non hash tag keyword query
   			<c:if test="${not empty q}">				
   				/*oHashParams = {};
   				oHashParams.q = "${q}";    				
   				$.fn.fetchSolrFacetCount(oHashParams);*/				
   			</c:if>;
   					
   			// hash tag query
   			// catch back/forward buttons and hash change: loada dataTable based on url params
   			$(window).bind("hashchange", function() {
   				
   				MPI2.searchAndFacetConfig.update.hashChange = true;
   				//var url = $.param.fragment();	 // not working with jQuery 10.0.1
   				var url = $(location).attr('hash');		
   				
   				//console.log('hash change URL: '+ '/search' + url);
   				var oHashParams = _process_hash();
   				
   				//console.log(oHashParams)
   				
   				/* deals with 3 events here:
   				 	1. widget facet open
   					2. back button
   					3. added/removed filter   				
   					*/
   				if ( MPI2.searchAndFacetConfig.update.filterChange ){
    				//console.log('added or removed a filter');
    				MPI2.searchAndFacetConfig.update.filterChange = false;
    				
    				// MA,MP facet stays open when adding/removing filters
    				$('li#mp.fmcat, li#ma.fmcat').each(function(){
    				
    					if (oHashParams.facetName == $(this).attr('id')) {
    						$(this).addClass('open');
    						MPI2.searchAndFacetConfig.update.filterChange = false;
    					}
    				});
    				
    				$.fn.loadDataTable(oHashParams);
    			}
   				
    			else if ( MPI2.searchAndFacetConfig.update.widgetOpen ){
   					//console.log('1. widget facet open');
   					MPI2.searchAndFacetConfig.update.widgetOpen = false; // reset
   					
    				// search by keyword (user's input) has no fq in url when hash change is detected
    				if ( oHashParams.fq ){			
    					
    					if ( oHashParams.coreName ){	    						
    						oHashParams.coreName += 'Facet'; 					
    					}
    					else {						
    						// parse summary facet filters 
    						var facet = oHashParams.facetName;
    						var aFilters = [];
    						$('ul#facetFilter li.ftag a').each(function(){							
    							aFilters.push($(this).text());
    						});														
    						
    						//console.log(oHashParams);		
    						//oHashParams.filters = aFilters;

    						oHashParams.facetName = facet;	    						
    					}
    					
    					$.fn.loadDataTable(oHashParams);
    				}
   				} 
				else if ( !MPI2.searchAndFacetConfig.update.pageReload ){
    				//console.log('back button event');
    				if ( /search\/?$/.test(window.location.href) ){
        				// when the url become ..../search
    					document.location.href = baseUrl + '/search';
        			}
    				else {
    					rebuildFilters(oHashParams); 
    				}
				}
				else if ( MPI2.searchAndFacetConfig.update.pageReload ){
    				//console.log('page reload!!!');
				}	
   			});		
    		if ( ! MPI2.searchAndFacetConfig.update.hashChange ){
    			//console.log('page reload: no hash change detected')

    			var oHashParams = $.fn.parseHashString(window.location.hash.substring(1));
    			//console.log(oHashParams);
    			
    			if ( window.location.search != '' ){
    				// qrey value of q
    				oHashParams.q = $.fn.fetchQueryStr();
    			}
    			
    			//if ( $.isEmptyObject(oHashParams || typeof oHashParams.coreName != 'undefined' ) ){
    			if ( $.isEmptyObject(oHashParams) ){
    				//console.log('case core');	
    				// search page default load: /search or /search?
    				$.fn.fetchSolrFacetCount(oHashParams);		
    			}
    			else {
    				//console.log('rebuild here')
    				rebuildFilters(oHashParams);    			
    			}
    		}
    		
    		function removeAllFilters(){
    		
    			 $('ul#facetFilter li.ftag').each(function(){
					$(this).parent().remove();
					
				});	 
				$('ul#facetFilter li span.fcap').hide();
				
				$('div.ffilter').hide();
				// uncheck all filter checkbox
				$('div.flist li.fcat input:checked:enabled').each(function(){
					$(this).prop('checked', false).siblings('span.flabel').removeClass('highlight');
				});	 
    		}
    		
    		function rebuildFilters(oHashParams){
    		
    			MPI2.searchAndFacetConfig.update.resetSummaryFacet = true; 
				MPI2.searchAndFacetConfig.update.filterAdded = false;

				removeAllFilters();
				
				oHashParams.q = typeof oHashParams.q == 'undefined' ? '*:*' : oHashParams.q;
		    	oHashParams.noFq = typeof oHashParams.fq == 'undefined' ? true : false;
		    	
		    	if ( typeof oHashParams.coreName != 'undefined' ){
		    		oHashParams.widgetName = oHashParams.coreName + 'Facet';
		    	}
		    	else if ( typeof oHashParams.facetName != 'undefined' ){
		    		oHashParams.widgetName = oHashParams.facetName + 'Facet';
		    	}
		    	
		    	if ( typeof oHashParams.widgetName == 'undefined'){
		    		$.fn.fetchSolrFacetCount(oHashParams);					
				}
				else {
			    	oHashParams.fq = typeof oHashParams.fq == 'undefined' ? 
			    			MPI2.searchAndFacetConfig.facetParams[oHashParams.widgetName].fq :
			    				oHashParams.fq;
			    			
			    	oHashParams.oriFq = oHashParams.fq; 
			    	
					//console.log(oHashParams);
				
					$.fn.parseUrl_constructFilters_loadDataTable(oHashParams);
				}	
    		}
    		
    		function _process_hash(){
    			var oHashParams = $.fn.parseHashString(window.location.hash.substring(1));
    			//console.log(oHashParams);
    			
    			if ( window.location.search != '' && window.location.hash == '' ){
    				// has q only, no hash string
    				//console.log('has q')
    			}
    			else if ( typeof oHashParams.coreName != 'undefined' ){
    				//console.log('case core');	
    			
    				oHashParams.q = '*:*';
    				oHashParams.widgetName = oHashParams.coreName + 'Facet';
    			}
    			else {
	
					// img_ prefix is to for fields marker_type, procedure_id, top_level_mp_term and selected_top_level_ma_term
	   				// from images core. This is shown on the url and used to rebuild facet filters 
	   				// - ie, we can tell if a filter is for MP or images, eg
	   				// when doing the query, strip it out
	   				
					if ( typeof oHashParams.fq != 'undefined' ){
						oHashParams.fq = oHashParams.fq.replace(/img_/g,'');
					}
	   				
	   				oHashParams.widgetName = oHashParams.coreName? oHashParams.coreName : oHashParams.facetName;	                
					oHashParams.widgetName += 'Facet';
	   				oHashParams.q = window.location.search != '' ? $.fn.fetchQueryStr() : '*:*';
					
    			}	
    			return oHashParams;
   			}
   							
    		var exampleSearch = 
					 '<h3 id="samplesrch">Example Searches</h3>'
						+ '<p>Sample queries for several fields are shown. Click the desired query to execute any of the samples.'
						+ '	<b>Note that queries are focused on Relationships, leaving modifier terms to be applied as filters.</b>'
						+ '</p>'
						+ '<h5>Gene query examples</h5>'
						+ '<p>'
						+ '<a href="${baseUrl}/search?q=akt2">Akt2</a>'
						+ '- looking for a specific gene, Akt2'
						+ '<br>'
						+ '<a href="${baseUrl}/search?q=*rik">*rik</a>'
						+ '- looking for all Riken genes'
						+ '<br>'
						+ '<a href="${baseUrl}/search?q=hox*">hox*</a>'
						+ '- looking for all hox genes'
						+ '</p>'
						+ '<h5>Phenotype query examples</h5>'
						+ '<p>'					
						+ '<a href="${baseUrl}/search?q=abnormal skin morphology">abnormal skin morphology</a>'
						+ '- looking for a specific phenotype'
						+ '<br>'
						+ '<a href="${baseUrl}/search?q=ear">ear</a>'
						+ '- find all ear related phenotypes'
						+ '</p>'
						+ '<h5>Procedure query Example</h5>'
						+ '<p>'
						+ '<a href="${baseUrl}/search?q=grip strength">grip strength</a>'
						+ '- looking for a specific procedure'
						+ '</p>'
						+ '<h5>Phrase query Example</h5>'
						+ '<p>'
						+ '<a href="${baseUrl}/search?q=zinc finger protein">zinc finger protein</a>'
						+ '- looking for genes whose product is zinc finger protein'
						+ '</p>'
						+ '<h5>Phrase wildcard query Example</h5>'
						+ '<p>'
						+ '<a href="${baseUrl}/search?q=abnormal phy*">abnormal phy*</a>'
						+ '- can look for phenotypes that contain abnormal phenotype or abnormal physiology.<br>'
						+ 'Supported queries are a mixture of word with *, eg. abn* immune phy*.<br>NOTE that leading wildcard, eg. *abnormal is not supported.'
						+ '</p>';
						
					
            // initialze search example qTip with close button and proper positioning
            $("a#searchExample").qtip({            	   
               	hide: true,
    			content: {
    				text: exampleSearch,
    				title: {'button': 'close'}
    			},		 	
   			 	style: {
   			 		classes: 'qtipimpc',			 		
   			        tip: {corner: 'top center'}
   			    },
   			    position: {my: 'left top',
   			    		   adjust: {x: -360, y: 0}
   			    },
   			 	show: {
   					event: 'click' //override the default mouseover
   				}
            });
                        
            // Message to IE users
            //$.fn.ieCheck();
        });        
        </script>
			
						
    </jsp:body>

</t:genericpage>


