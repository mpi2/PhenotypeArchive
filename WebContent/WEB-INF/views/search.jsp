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
						<div id="resetFilter"><a href="${baseUrl}/search">Remove all facet filters</a></div>
					</div>
										
					<p class='documentation title textright'>
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
									<!-- <input id="s" type="text" value="" placeholder="Search">-->
									<div class="ui-widget">
										<input id="s">
									</div>
								</p>									
							</div>
						</div>
					</div>
					
					<div class="textright">
						<a id = 'searchExample' class="">View example search</a>						
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
			<script type='text/javascript' src='${baseUrl}/js/searchAndFacet/searchAndFacet_primer.js?v=${version}'></script>		
	    </compress:html>        
       
         <script>        		
       	$(document).ready(function(){
       	
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
			       				//console.log(docs);
			       				var aKV = [];
			       				for ( var i=0; i<docs.length; i++ ){
			       					for ( key in docs[i] ){
			       						//console.log('key: '+key);	
			       						var facet;
			       						if ( key == 'docType' ){	
			       							facet = docs[i][key].toString();
			       						}
			       						else {	
			       							var term = docs[i][key].toString().toLowerCase();	
			       							var re = new RegExp("(" + request.term + ")", "gi") ;			       				 			
			       				 			var newTerm = term.replace(re,"<b class='sugTerm'>$1</b>");
			       				 						       				 			
			       							aKV.push("<span class='" + facet + "'>" + newTerm + "</span>");
			       							//console.log('facet: ' +  facet);
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
	       		       this.value = $(ui.item.label).text();
	       		       event.preventDefault(); // Prevent the default focus behavior.
	       			},
	       			minLength: 3,
	       			select: function( event, ui ) {
	       				// select by mouse click
	       				//console.log(this.value + ' vs ' + ui.item.label);
	       				var oriText = $(ui.item.label).text();
	       				var facet = $(ui.item.label).attr('class');
	       				
	       				// handed over to hash change to fetch for results	       				
	       				document.location.href = baseUrl + '/search?q=' + oriText + '#facet=' + facet; 	
	       				
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
       		
       	 	
       		// make "search" menu point active
       		$('nav#mn ul.menu > li:first-child').addClass('active');
       		       		
       		<c:if test="${isLoggedIn}">       			
       			MPI2.searchAndFacetConfig.isLoggedIn = true;
       		</c:if>;
       		
   			$.fn.qTip({'pageName':'search'		 					
   			});  			 						
   			
   			// non hash tag keyword query
   			<c:if test="${not empty q}">				
   				/*oHashParams = {};
   				oHashParams.q = "${q}";    				
   				$.fn.fetchSolrFacetCount(oHashParams);*/				
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
   				//console.log(oHashParams);   				
   				oHashParams.widgetName = oHashParams.coreName? oHashParams.coreName : oHashParams.facetName;	                
				oHashParams.widgetName += 'Facet';
								
   				//console.log('from widget open: '+ MPI2.searchAndFacetConfig.widgetOpen);
   				
   				if ( window.location.search.match(/q=/) ){   					
   					oHashParams.q = window.location.search.replace(/&.+/, '').replace('?q=','');
   				}
   				else if ( typeof oHashParams.q == 'undefined' ){
   					oHashParams.q = window.location.search == '' ? '*:*' : window.location.search.replace('?q=', '');	    					
   				}
   				   	
   				//console.log(oHashParams)
   				
   				/* deals with 4 events here:
   				 	1. widget facet open
   					2. page reload
   					3. added/removed filter
   					4. back button
   					*/
   				if ( MPI2.searchAndFacetConfig.filterChange ){
    				console.log('added or removed a filter');
    				//MPI2.searchAndFacetConfig.filterChange = false;
    				
    				// MA,MP facet stays open when adding/removing filters
    				$('li#mp.fmcat, li#ma.fmcat').each(function(){
    				//$('li.fmcat').each(function(){	
    				
    					if (oHashParams.facetName == $(this).attr('id')) {
    						$(this).addClass('open');
    						MPI2.searchAndFacetConfig.filterChange = false;
    					}
    					
    				});
    				
    				$.fn.loadDataTable(oHashParams);
    			}
   				
   				else if ( MPI2.searchAndFacetConfig.widgetOpen ){
   					console.log('widget facet open');
   					MPI2.searchAndFacetConfig.widgetOpen = false; // reset
   						    				
    				// search by keyword (user's input) has no fq in url when hash change is detected
    				if ( oHashParams.fq ){			
    					
    					if ( oHashParams.coreName ){	    						
    						oHashParams.coreName += 'Facet'; 					
    					}
    					else {						
    						// parse summary facet filters 
    						var facet = oHashParams.facetName;
    						var aFilters = [];
    						//$('ul#facetFilter li.' + facet + ' li a').each(function(){
    						$('ul#facetFilter li.ftag a').each(function(){							
    							aFilters.push($(this).text());
    						});														
    						
    						//console.log(oHashParams);		//console.log('filter: ' + aFilters );
    						oHashParams.filters = aFilters;
    						//oHashParams.facetName = facet + 'Facet';
    						oHashParams.facetName = facet;	    						
    					}
    					
    					$.fn.loadDataTable(oHashParams);
    				}
   				}    			
    			
    			else {
    				// back button event
    				console.log('page reload event');	
    				//console.log(oHashParams);
    				
    				$.fn.loadDataTable(oHashParams);
    				// need to reconstruct filters from url
    				/*var oConf = {};
			    	oConf.q    = oHashParams.q;
			    	oConf.fq   = oHashParams.fq;
			    	oConf.noFq = typeof oHashParams.fq == 'undefined' ? true : false;
			    	oConf.core = oHashParams.coreName; 
			    	oConf.backButton = true;
			    	
	    			$.fn.parseUrl_constructFilters_loadDataTable(oConf);
	    			*/
	    			// remove all previous filers
    				//$('ul#facetFilter li.ftag').remove();
    				// remove caption
    				//$('ul#facetFilter li span.fcap').hide();
    				
    			
	    			//window.location.reload();
    				
    			}
   				
   					
   			});		
    						
    						
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


