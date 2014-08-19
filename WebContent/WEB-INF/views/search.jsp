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
       			
       			//console.log('loading from url');
       			// load page based on url hash parameters	
       			$('input#s').val(decodeURI($.fn.fetchQueryStr()));
       			
       			oUrlParams = $.fn.parseHashString(window.location.hash.substring(1));	
       			if (typeof oUrlParams.fq == 'undefined'){
       				oUrlParams.noFq = true;
       			}

       			//console.log(oUrlParams);
       			$.fn.fetchSolrFacetCount(oUrlParams);	
       		}
       		else {
       			// do not understand the url, redirect to error page
       			document.location.href = baseUrl + '/404.jsp';		
       		}
       		
       		// search via ENTER
       		$('input#s').keyup(function (e) {		
       		    if (e.keyCode == 13) { // user hits enter
       		    	$(".ui-menu-item").hide();
       		    	//$('ul#ul-id-1').remove();
       		    
       		    	//alert('enter: '+ MPI2.searchAndFacetConfig.matchedFacet)
       		    	var input = $('input#s').val().trim();
       		    	//alert(input)
       		    	input = /^\*\**?\*??$/.test(input) ? '' : input;  // lazy matching
       		    	
       		    	var re = new RegExp("^'(.*)'$");
       		    	input = input.replace(re, "\"$1\""); // only use double quotes for phrase query
       		    	
       		    	input = encodeURIComponent(input);

       		    	input = input.replace("%5B", "\\[");
       				input = input.replace("%5D", "\\]");
       				input = input.replace("%7B", "\\{");
       				input = input.replace("%7D", "\\}");
       				input = input.replace("%7C", "\\|");
       				input = input.replace("%5C", "\\\\");
       				input = input.replace("%3C", "\\<");
       				input = input.replace("%3E", "\\>");
       				input = input.replace("."  , "\\.");
       				input = input.replace("("  , "\\(");
       				input = input.replace(")"  , "\\)");
       				input = input.replace("%2F", "\\/");
       				input = input.replace("%60", "\\`");
       				input = input.replace("~"  , "\\~"); 
       				input = input.replace("%"  , "\\%");
       				//alert(input)
       				// no need to escape space - looks cleaner to the users 
       				// and it is not essential to escape space
       				if ( /^\\%22.+\\.+%22$/.test(input) ){
       					input = input.replace(/\\/g, ''); //remove \ in double quotes				
       				}
       				input = input.replace(/\\?%20/g, ' ');
       				
       				var facet = MPI2.searchAndFacetConfig.matchedFacet;
       				
       				//console.log('matched facet: '+ facet)
       		    	if (input == ''){
       		    		
       		    		// if there is no existing facet filter, reload with q
       		    		if ( $('ul#facetFilter li.ftag').size() == 0 ){
       		    			//baseUrl + '/search?q=' + input;
       		    			document.location.href = baseUrl + '/search';
       		    		}
       		    		else {
       		    			var q = encodeURI('*:*');	
       		    			window.location.search = 'q=' + q;
       		    		}
       		    	}
       		    	else if (! facet){
       		    		// user hits enter before autosuggest pops up	
       		    		// ie, facet info is unknown
       		    		//alert('enter-2');
       		    		
       		    		if ( $('ul#facetFilter li.ftag').size() == 0 ){
       		    			// if there is no existing facet filter, reload with q
       		    			document.location.href = baseUrl + '/search?q=' + input;
       		    		}
       		    		else {
       		    			// facet will be figured out by code
       		    			var fqStr = $.fn.getCurrentFq(facet);
           		    		document.location.href = baseUrl + '/search?q=' + input + '#fq=' + fqStr;
       		    		}
       		    	}
       		    	else {	
       		    		//alert('enter-3');
       		    		var fqStr = $.fn.getCurrentFq(facet);
       		    		document.location.href = baseUrl + '/search?q=' + input + '#fq=' + fqStr + '&facet=' + facet;
       		    	}
       		    }
       		});
       		
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
       		
       		$('a#searchExample').mouseover(function(){
       			// override default behavior from default.js - Nicolas	
       			return false;
       		})
       		
       		var solrBq = "&bq=marker_symbol:*^100 top_level_mp_term:*^90 disease_term:*^80 selected_top_level_ma_term:*^70";
       		// autosuggest 
       		$(function() {
	       		$( "input#s" ).autocomplete({
	       			source: function( request, response ) {
		       			$.ajax({
			       			url: "${solrUrl}/autosuggest/select?wt=json&qf=auto_suggest&defType=edismax" + solrBq,				       			
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
			       							var term = docs[i][key].toString();	
			       							var termHl = term;
			       							
			       							// highlight multiple matches (partial matches) while users typing in search keyword(s)
			       							// let jquery autocomplet UI handles the wildcard
			       							//var termStr = $('input#s').val().trim(' ').split(' ').join('|').replace(/\*|"|'/g, ''); 
			       							var termStr = $('input#s').val().trim(' ').split(' ').join('|')
			       							.replace(/\*|"|'/g, '')
			       							.replace(/\(/g,'\\(')
			       							.replace(/\)/g,'\\)');
			       							
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
	       				////console.log(this.value + ' vs ' + ui.item.label);
	       				//var oriText = $(ui.item.label).text();
	       				
	       				var facet = $(ui.item.label).attr('class');
	       				
	       				// handed over to hash change to fetch for results	
	       			
	       				var q = encodeURIComponent(this.value);
	       				var fq = $.fn.getCurrentFq(facet);
	       				
	       				document.location.href = baseUrl + '/search?q="' + q + '"#fq=' + fq + '&facet=' + facet; 	
	       				
	       				// prevents escaped html tag displayed in input box
	       				event.preventDefault(); return false; 
	       				
	       			},
	       			open: function(event, ui) {
	       				//fix jQuery UIs autocomplete width
	       				$(this).autocomplete("widget").css({
	       			    	"width": $(this).width() + "px"
	       			    });
	       			   				
	       				$( this ).removeClass( "ui-corner-all" ).addClass( "ui-corner-top" );	       				
	       			},
	       			close: function() {
	       				$( this ).removeClass( "ui-corner-top" ).addClass( "ui-corner-all" );
	       			}
	       		}).keyup(function (e) {
	       			
	       			// close dropdown list when hit enter
	       			// this fixed problem of hit enter quickly before dropdown list appears 
	       			// and then hit enter while wait a bit after dropdown list appears in a second seach -
	       			// this causes dropdown list putshed up or down but not disappear
     		        if(e.which === 13) {
     		            $(".ui-menu-item").hide();
     		    }})      		
	       		.data("ui-autocomplete")._renderItem = function( ul, item) { // prevents HTML tags being escaped
       				return $( "<li></li>" ) 
     				  .data( "item.autocomplete", item )
     				  .append( $( "<a></a>" ).html( item.label ) )
     				  .appendTo( ul );
     			}
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
   				/*oUrlParams = {};
   				oUrlParams.q = "${q}";    				
   				$.fn.fetchSolrFacetCount(oUrlParams);*/				
   			</c:if>;
   					
   			// hash tag query
   			// catch back/forward buttons and hash change: loada dataTable based on url params
   			$(window).bind("hashchange", function() {

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
   					//console.log('1. widget facet open');
   					
   					MPI2.searchAndFacetConfig.update.widgetOpen = false; // reset
   					
   					// search by keyword (user's input) has no fq in url when hash change is detected
    				if ( oUrlParams.fq ){			
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
    				// qrey value of q
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
    			//console.log(oUrlParams);
    			
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
						oUrlParams.fq = oUrlParams.fq.replace(/img_/g,'');
					}
	   				
	   				oUrlParams.widgetName = oUrlParams.coreName? oUrlParams.coreName : oUrlParams.facetName;	                
					oUrlParams.widgetName += 'Facet';
	   				oUrlParams.q = window.location.search != '' ? $.fn.fetchQueryStr() : '*:*';
    			}	
    			return oUrlParams;
   			}
   							
    		var exampleSearch = 
					 '<h3 id="samplesrch">Example Searches</h3>'
						+ '<p>Sample queries for several fields are shown. Click the desired query to execute any of the samples.'
						+ '	<b>Note that queries are focused on Relationships, leaving modifier terms to be applied as filters.</b>'
						+ '</p>'
						+ '<h5>Gene query examples</h5>'
						+ '<p>'
						+ '<a href="${baseUrl}/search?q=akt2#fq=*:*&facet=gene">Akt2</a>'
						+ '- looking for a specific gene, Akt2'
						+ '<br>'
						+ '<a href="${baseUrl}/search?q=*rik#fq=*:*&facet=gene">*rik</a>'
						+ '- looking for all Riken genes'
						+ '<br>'
						+ '<a href="${baseUrl}/search?q=hox*#fq=*:*&facet=gene">hox*</a>'
						+ '- looking for all hox genes'
						+ '</p>'
						+ '<h5>Phenotype query examples</h5>'
						+ '<p>'					
						+ '<a href="${baseUrl}/search?q=abnormal skin morphology#fq=top_level_mp_term:*&facet=mp">abnormal skin morphology</a>'
						+ '- looking for a specific phenotype'
						+ '<br>'
						+ '<a href="${baseUrl}/search?q=ear#fq=top_level_mp_term:*&facet=mp">ear</a>'
						+ '- find all ear related phenotypes'
						+ '</p>'
						+ '<h5>Procedure query Example</h5>'
						+ '<p>'
						+ '<a href="${baseUrl}/search?q=grip strength#fq=pipeline_stable_id:*&facet=pipeline">grip strength</a>'
						+ '- looking for a specific procedure'
						+ '</p>'
						+ '<h5>Phrase query Example</h5>'
						+ '<p>'
						+ '<a href="${baseUrl}/search?q=zinc finger protein#fq=*:*&facet=gene">zinc finger protein</a>'
						+ '- looking for genes whose product is zinc finger protein'
						+ '</p>'
						+ '<h5>Phrase wildcard query Example</h5>'
						+ '<p>'
						+ '<a href="${baseUrl}/search?q=abnormal phy*#fq=top_level_mp_term:*&facet=mp">abnormal phy*</a>'
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


