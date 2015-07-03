<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:genericpage>
    <jsp:attribute name="title">IMPC Phenotype to GWAS Disease Trait Mapping Viewer</jsp:attribute>
    <jsp:attribute name="breadcrumb">&nbsp;&raquo;<a href="${baseUrl}/gwaslookup">&nbsp;IMPC Phenotype to GWAS Disease Trait Mapping Viewer</a></jsp:attribute>
    <jsp:attribute name="header">
        

        <!-- <link rel="stylesheet" href="${baseUrl}/css/vendor/jquery.ui/jquery.ui.core.css"> -->

        <!--  <link href="${baseUrl}/js/vendor/jquery/jquery.qtip-2.2/jquery.qtip.min.css" rel="stylesheet" />-->
        <link rel="stylesheet" href="${baseUrl}/css/vendor/font-awesome/font-awesome.min.css" />
        <link rel="stylesheet" href="${baseUrl}/css/gwastable.css" />
        <style type="text/css">
        	input.gwaslookup {
        		width: 300px;
        	}
        	a i.gwasSearchExample {
        		margin-left: 15px;
        	}
        	div#pm {
        		margin-top: 50px;
        	}
            
        </style>
        
        <script type='text/javascript'>
        
            $(document).ready(function () {
                'use strict';
                
				// test only
                //var baseUrl = '//dev.mousephenotype.org/data';
                //var baseUrl = 'http://localhost:8080/phenotype-archive';
                var solrUrl = '${solrUrl}';
                var baseUrl = "${baseUrl}";

                //console.log('solrurl: ' + solrUrl);
                //console.log('baseurl: ' + baseUrl);
                // IMPC GWAS mapping data autosuggest
                // generic search input autocomplete javascript
				var solrBq = "&bq=gwas_mgi_gene_symbol:*^100 gwas_disease_trait:*^90 gwas_mp_term_id:*^80 gwas_mp_term_name:*^80";
	   			$( "input#gwasInput" ).autocomplete({
		   			source: function( request, response ) {
		   				$.ajax({
		   					//url: solrUrl + "/autosuggest/select?wt=json&qf=string auto_suggest&defType=edismax" + solrBq,	
		   					url: solrUrl + "/autosuggest/select?rows=10&fq=docType:gwas&wt=json&qf=string auto_suggest&defType=edismax" + solrBq,	
		   					dataType: "jsonp",
		   					'jsonp': 'json.wrf',
		   					data: {
		   						q: request.term
			       			},
			       			success: function( data ) {
			       				
			       				var docs = data.response.docs;	
			       				var aKV = [];
			       				var reLabel = new RegExp('_', 'g');	
			       				
			       				if ( docs.length == 0 ){
			       					aKV.push("<span class='" + facet + " sugList'>No mapping found. Please try another keyword</span>");
			       				}
			       				
			       				for ( var i=0; i<docs.length; i++ ){
			       					var facet;
			       					for ( var key in docs[i] ){
			       						//console.log('key: '+key);
			       						
			       						if ( key != 'docType' ){
			       						 
				       						var keyLabel = key;
				       						
				       						if ( key == 'gwas_mgi_gene_id' ||
				       							 key == 'gwas_mgi_gene_symbol' ||
				       		        			 key == 'gwas_mp_term_id' ||
				       		        			 key == 'gwas_mp_term_name' ){
				       							
				       							keyLabel = key.replace('gwas_', 'impc_');
				       						}
				       						keyLabel = keyLabel.replace(reLabel, ' ');
				       						
		       								var term = docs[i][key].toString();	
			       							var termHl = term;
			       							
			       							var termStr = $('input#gwasInput').val().trim(' ').split(' ').join('|').replace(/\*|"|'/g, '').replace(/\(/g,'\\(').replace(/\)/g,'\\)');
			       							
			       							var re = new RegExp("(" + termStr + ")", "gi") ;
			       							var termHl = termHl.replace(re,"<b class='sugTerm'>$1</b>");
			       							
			       							aKV.push("<span class='" + facet + " sugList'>" + "<span class='dtype'>"+ keyLabel + ' : </span>' + termHl + "</span>");
				       				
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
		       				
		       			// send query to server
		       			fetch_gwas(this.value);
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
		       	}).keypress(function(e) {
		       		if(e.keyCode == 13){
			       		e.preventDefault();
	       	         	// send query to server
	       	         	//alert("enter: " + this.value);
	       	         	fetch_gwas(this.value);
	       	         	
	       	         	$(this).autocomplete('close');
			       	}
			    }).data("ui-autocomplete")._renderItem = function( ul, item) { // prevents HTML tags being escaped
		   			return $( "<li></li>" ) 
		 				  .data( "item.autocomplete", item )
		 				  .append( $( "<a></a>" ).html( item.label ) )
		 				  .appendTo( ul );
		        };
	                
                function fetch_gwas(query) {
                	
                	var parts = query.split(" : ");
                	var re = new RegExp(' ', 'g');	
                	var field = parts[0].replace(re, '_');
                	var value = parts[1];
                	
                	field = field.replace('impc_', '');
                	console.log(value == undefined);
                	if ( value == undefined ){
                		value = field;
                		field = 'keyword';
                	}
                	
                	//console.log(baseUrl + "/gwaslookup?" + field + "="+ value);
                	document.location.href = baseUrl + "/gwaslookup?" + field + "="+ value;
                } 
                
               	$( "#tabs" ).tabs();
               	$.fn.customJqTabs();
               	
			});
         
            
        </script>
        
        <script type='text/javascript' src='${baseUrl}/js/vendor/jquery/jquery.highlight.js'></script>  
        <script type='text/javascript' src='https://cdn.datatables.net/plug-ins/f2c75b7247b/features/searchHighlight/dataTables.searchHighlight.min.js'></script>  
        <script type='text/javascript' src='${baseUrl}/js/utils/tools.js'></script>  

    </jsp:attribute>

    <jsp:attribute name="addToFooter">	
        <div class="region region-pinned">

        </div>		

    </jsp:attribute>

    <jsp:body>		

        <div class="region region-content">
            <div class="block">
                <div class='content'>
                    <div class="node node-gene">
                        <h1 class="title" id="top">IMPC Phenotype to GWAS Disease Trait Mapping</h1>	 
                        <div class="section">
                            <div class="inner">
                                <div class="clear"></div>
								<span class='ginput'></span>Search mappings:&nbsp;&nbsp;<input type='text' value='' id ='gwasInput' class='gwaslookup'><a><i class='fa fa-info gwasSearchExample'></i></a>
							
                                <!-- container to display dataTable -->									
                                <div class="gwasTable" id="pm">${mapping}</div>
                            </div>
                        </div>
                    </div>				
                </div>
            </div>
        </div>		       

    </jsp:body>
</t:genericpage>

