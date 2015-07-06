$(document).ready(function () {
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
		    
	    	//$('a#searchExample').mouseover(function(){
	    	$('i.searchExample').mouseover(function(){
       			// override default behavior from default.js - Nicolas	
       			return false;
       		})	
		    // initialze search example qTip with close button and proper positioning
            //$("a#searchExample").qtip({    
            $("i.searchExample").qtip({  	
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
   			    		   adjust: {x: -480, y: 0}
   			    },
   			 	show: {
   					event: 'click' //override the default mouseover
   				}
            });
	    	
	    	/* $("i.batchQuery").qtip({            	   
                content: "Click to go to batch query page",
                style: {
 			 		classes: 'qtipimpc',			 		
 			        tip: {corner: 'top center'}
 			    },
 			    position: {my: 'left top',
 			    		   adjust: {x: -125 , y: 0}
 			    }
	    	}); */
	    	
	    	 $("span.direct").qtip({            	   
                 content: "Matches GWAS traits - Phenotypes for this knockout strain are highly similar to human GWAS traits "
                 + "associated with SNPs located in, or proximal to, orthologous genes. Mappings done by manual curation." ,
                 	style: {
     			 		classes: 'qtipimpc',			 		
     			        tip: {corner: 'top center'}
     			    },
     			    position: {my: 'left top',
     			    		   adjust: {x: -280, y: 0}
     			    }
      		});		 	
              $("span.indirect").qtip({  	
              	content: "Similar to GWAS traits - Phenotypes for this knockout strain have some overlap to human GWAS traits "
              	+ "associated with SNPs  located in, or proximal to, orthologous genes. Mappings done by manual curation.", 
              	style: {
     			 		classes: 'qtipimpc',			 		
     			        tip: {corner: 'top center'}
     			    },
     			    position: {my: 'left top',
     			    		   adjust: {x: -280, y: 0}
     			    }
              });
	    	
		    var matchedFacet = false;
			var facet2Fq = {
				'gene' : '*:*',
				'mp'   : 'top_level_mp_term:*',
				'disease' : '*:*',
				'ma' : 'selected_top_level_ma_term:*',
				'pipeline' : 'pipeline_stable_id:*',
				'images' : '*:*'
			}

			// generic search input autocomplete javascript
			var solrBq = "&bq=marker_symbol:*^100 hp_term:*^95 hp_term_synonym:*^95 top_level_mp_term:*^90 disease_term:*^70 selected_top_level_ma_term:*^60";
	   		$( "input#s" ).autocomplete({
	   			source: function( request, response ) {
	   				$.ajax({
	   					//url: solrUrl + "/autosuggest/select?wt=json&qf=string auto_suggest&defType=edismax" + solrBq,	
	   					url: solrUrl + "/autosuggest/select?fq=!docType:gwas&wt=json&qf=string auto_suggest&defType=edismax" + solrBq,	
	   					dataType: "jsonp",
	   					'jsonp': 'json.wrf',
	   					data: {
	   						q: request.term
		       			},
		       			success: function( data ) {
		       				
			       	        matchedFacet = false; // reset
		       				var docs = data.response.docs;	
		       				// console.log(docs);
		       				var aKV = [];
		       				for ( var i=0; i<docs.length; i++ ){
		       					var facet;
		       					for ( var key in docs[i] ){
		       						// console.log('key: '+key);
		       						if ( facet == 'hp' && (key == 'hpmp_id' || key == 'hpmp_term') ){
		       							continue;
		       						}
		       						
		       						if ( key == 'docType' ){	
		       							facet = docs[i][key].toString();
		       						}
		       						else {	
			       						
	       								var term = docs[i][key].toString();	
		       							var termHl = term;
		       							
		       							// highlight multiple matches
										// (partial matches) while users
										// typing in search keyword(s)
		       							// let jquery autocomplet UI handles
										// the wildcard
		       							// var termStr =
										// $('input#s').val().trim('
										// ').split('
										// ').join('|').replace(/\*|"|'/g,
										// '');
		       							var termStr = $('input#s').val().trim(' ').split(' ').join('|').replace(/\*|"|'/g, '').replace(/\(/g,'\\(').replace(/\)/g,'\\)');
		       							
		       							var re = new RegExp("(" + termStr + ")", "gi") ;
		       							var termHl = termHl.replace(re,"<b class='sugTerm'>$1</b>");
		       							
		       							if ( facet == 'hp' ){
		       								termHl += " &raquo; <span class='hp2mp'>" + docs[i]['hpmp_id'].toString() + ' - ' + docs[i]['hpmp_term'].toString() + "</span>";		
		       							}
		       							
		       							aKV.push("<span class='" + facet + " sugList'>" + "<span class='dtype'>"+ facet + ' : </span>' + termHl + "</span>");
		       							
		       							if (i == 0){
		       								// take the first found in
											// autosuggest and open that
											// facet
		       								matchedFacet = facet;			       							
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
	       			event.preventDefault(); // Prevent the default focus
											// behavior.
	       		},
	       		minLength: 3,
	       		select: function( event, ui ) {
	       			// select by mouse / KB
	       				// console.log(this.value + ' vs ' + ui.item.label);
	       				// var oriText = $(ui.item.label).text();
	       				
	       				var facet = $(ui.item.label).attr('class').replace(' sugList', '') == 'hp' ? 'mp' : $(ui.item.label).attr('class').replace(' sugList', '');
	       				
	       				var q;
	       				//var matched = this.value.match(/.+ » (MP:\d+) - .+/); 
	       				var matched = this.value.match(/.+(MP:\d+) - .+/); 
	       				
	       				if ( matched ){
	       					q = matched[1];
	       				}
	       				else {
	       					q = this.value;
	       				}	
	       				q = encodeURIComponent(q);
	       				
	       				
	       				// handed over to hash change to fetch for results
	       				var fqStr = facet2Fq[facet];
	       				document.location.href = baseUrl + '/search?q="' + q + '"#fq=' + fqStr + '&facet=' + facet; 	

	       				// prevents escaped html tag displayed in input box
	       				event.preventDefault(); return false; 
	       				
	       		},
	       		open: function(event, ui) {
	       			// fix jQuery UIs autocomplete width
	       			$(this).autocomplete("widget").css({
	       				"width": ($(this).width() + "px")
	       			});
	       			   				
	       			$( this ).removeClass( "ui-corner-all" ).addClass( "ui-corner-top" );	       				
	       		},
	       		close: function() {
	       			$( this ).removeClass( "ui-corner-top" ).addClass( "ui-corner-all" );
	       		}
	       	}).data("ui-autocomplete")._renderItem = function( ul, item) {
	   			// prevents HTML tags being escaped
	   				console.log("Autocomplete " + item);
	   				return $( "<li></li>" ) 
	 				  .data( "item.autocomplete", item )
	 				  .append( $( "<a></a>" ).html( item.label ) )
	 				  .appendTo( ul );
	            };
	  	});

		// search via ENTER
   		$('input#s').keyup(function (e) {		
   		    if (e.keyCode == 13) { // user hits enter
   		    	$(".ui-menu-item").hide();
   		    	//$('ul#ul-id-1').remove();
   		    
   		    	//alert('enter: '+ MPI2.searchAndFacetConfig.matchedFacet)
   		    	var input = $('input#s').val().trim();
   		    	
   		    	//alert(input + ' ' + solrUrl)
   		    	input = /^\*\**?\*??$/.test(input) ? '' : input;  // lazy matching
   		    	
   		    	var re = new RegExp("^'(.*)'$");
   		    	input = input.replace(re, "\"$1\""); // only use double quotes for phrase query
   		    	
   		    	// NOTE: solr special characters to escape
   		    	// + - && || ! ( ) { } [ ] ^ " ~ * ? : \
   		    	
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
   				input = input.replace("!"  , "\\!");
   				input = input.replace("%21", "\\!");
   				
   				if ( /^\\%22.+%22$/.test(input) ){	
   					input = input.replace(/\\/g, ''); //remove starting \ before double quotes	
   				}
   				
   				// no need to escape space - looks cleaner to the users 
   				// and it is not essential to escape space
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
   		    		
   		    		//alert('2: ' + input)
   		    		// user hits enter before autosuggest pops up	
   		    		// ie, facet info is unknown
   		    		
   		    		if (input.match(/HP\\%3A\d+/)){
   		    			// work out the mapped mp_id and fire off the query
       		    		_convertHp2MpAndSearch(input);
   		    		} 
   		    		else if ( input.match(/MP%3A\d+ - (.+)/) ){
   		    			// hover over hp mp mapping but not selecting 
   		    			// eg. Cholesteatoma %C2%BB MP%3A0002102 - abnormal ear morpholog
   		    			var matched = input.match(/MP%3A\d+ - (.+)/); 
   		    			var mpTerm = '"' + matched[1] + '"';
   		    			var fqStr = $.fn.getCurrentFq('mp');
   		    			document.location.href = baseUrl + '/search?q=mp_term:' + mpTerm + '#fq=' + fqStr + '&facet=mp'; 
   		    		}
   		    		else {
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
   		    	}
   		    	else {	
   		    		
   		    		//alert('3: ' + facet)
   		    		if (input.match(/HP\\%3A\d+/)){
       		    		// work out the mapped mp_id and fire off the query
       		    		_convertHp2MpAndSearch(input);
   		    		} 
   		    		else if ( facet == 'hp' ){
   		    			_convertInputForSearch(input);
   		    		}
   		    		else {
   		    			var fqStr = $.fn.getCurrentFq(facet);
   		    			document.location.href = baseUrl + '/search?q=' + input + '#fq=' + fqStr + '&facet=' + facet;
   		    		}
   		    	}
   		    }
   		});
		
   		function _convertHp2MpAndSearch(input){
    		
    		$.ajax({
       			url: "${solrUrl}/autosuggest/select?wt=json&fl=hpmp_id&rows=1&q=hp_id:\""+input+"\"",				       			
       			dataType: "jsonp",
       			jsonp: 'json.wrf',
       			type: 'post',
    	    	async: false,
       			success: function( json ) {
	    				input = json.response.docs[0].hpmp_id;
	    				document.location.href = baseUrl + '/search?q=' + input + '#fq=top_level_mp_term:*&facet=mp';
       			}
				});
   		}
   		
   		function _convertInputForSearch(input){
   			$.ajax({
       			url: "${solrUrl}/autosuggest/select?wt=json&rows=1&qf=auto_suggest&defType=edismax&q=\""+input+"\"",				       			
       			dataType: "jsonp",
       			jsonp: 'json.wrf',
       			type: 'post',
    	    	async: false,
       			success: function( json ) {
       				var doc = json.response.docs[0];
       				var facet, q;
       				
       				for( var field in doc ) {
       					if ( field != 'docType' ){
       						q = doc[field]; 
       					}
       				}
	    			document.location.href = baseUrl + '/search?q=' + q;
       			}
				});
   		}
 	
 	