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
        	div#overviewTable {
        		margin-top: 50px;
        	}
        	table.detailed tr.odd td {
        		background-color: #F2F2F2;
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
                
	
                // clean existing input box
                $( "input#gwasInput" ).val("");
                
                // default load
                fetch_gwas("impc_mgi_gene_symbol : Nos1ap"); 
                
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
	                
                function fetch_gwas_ori(query) {
                	
                	var parts = query.split(" : ");
                	var re = new RegExp(' ', 'g');	
                	var field = parts[0].replace(re, '_');
                	var value = parts[1];
                	
                	field = field.replace('impc_', '');
                	
                	if ( value == undefined ){
                		value = field;
                		field = 'keyword';
                	}
                	
                	console.log(baseUrl + "/gwaslookup?" + field + "="+ value);
                	document.location.href = baseUrl + "/gwaslookup?" + field + "="+ value;
                	
                } 
                
				function fetch_gwas(query) {
                	
                	var parts = query.split(" : ");
                	var field = null;
                	var value = null;
                	
                	if ( parts.length == 1 ) {
                		// user hits enter w/o choosing from dropdown list
                		field = 'keyword';
                		value = parts[0];
					}
					else {
						var re = new RegExp(' ', 'g');	
	                	field = parts[0].replace(re, '_');
	                	value = parts[1];
	                	
	                	field = field.replace('impc_', '');
					}
                	//alert(field + ' --- ' + value)
					
                	var cols = ['Marker symbol', 'IMPC MP term', 'GWAS disease trait', ''];
                	var tableId = 'gwas';
					var dTableSkeleton = _refreshTable(cols, tableId);
					$('div#overviewTable').append(dTableSkeleton);
                	
	       	      	var dTable = $('table#gwas').dataTable({
	       	            "bSort": true,
	       	            "bProcessing": true,
	       	            "bServerSide": true,
	       	            //"sDom": "<lr><'#caption'>tip",
	       	         	"sDom": "<<'#exportSpinner'>l<'#tableTool'>r><'#caption'>tip",
	       	            "sPaginationType": "bootstrap",
	       	         	"oLanguage": {
	       	          		"sLengthMenu": 'Show <select>'+
		       	            '<option value="10">10</option>'+
		       	            '<option value="30">30</option>'+
		       	            '<option value="50">50</option>'+
		       	            '</select> genes',
		       	         	"sInfo": "Showing _START_ to _END_ of _TOTAL_ genes"
	       	        	},
	       	            "fnDrawCallback": function(oSettings) {  // when dataTable is loaded
	       	            	
	       	            	// dumper tool here
	       	            	
	       	            	var dTable = $(this).DataTable();
	       	            	$(this).find('tr td:nth-child(4)').css('cursor','pointer');
	       	            	
	       	            	$(this).find('tr td:nth-child(4)').click(function(){
	       	            		
	       	            		var oTr = $(this).parent();
	       	            		var oRow = dTable.row(oTr);
	       	            		
	       	            	 	if (oRow.child.isShown()) {
	       	            	 		oRow.child.hide();
	       	            	 		oTr.find('td > i.fa').removeClass('fa-minus-square');
	       	            	 	}
	       	            	 	else {
	       	            	 		fetchDetailedGwasData(dTable, oTr, oRow, field, value);
	       	            		}
	       	            	});
							
	       	            	
	       	            },
	       	         	"ajax": {
	                        "url": baseUrl + "/gwaslookup?" + field + '=' + value,
	                       // "data": {field : value}, not working
	                        "type": "POST",
	                        "error": function() {
	                            $('div.dataTables_processing').text("AJAX error trying to fetch your query");
	                        }
	                    }
	       	        });
                	
                } 
				function _refreshTable(cols, tableId){
					
					$('div#overviewTable').html('');
					
					var th;
					for( var i=0; i<cols.length; i++){
						th += '<th>' + cols[i] + '</th>';
					}
					
					var tableHeader = "<thead>" + th + "</thead>";
	            	var tableColNum = cols.length;
	            	
	                var dTable = $.fn.fetchEmptyTable(tableHeader, tableColNum, tableId);
	                
	                return dTable;
     			}
              
				function fetchDetailedGwasData(dTable, oTr, oRow, field, value) {
	       		 	
   	        		var mgi_gene_symbol = oTr.find('td:nth-child(1)').text();
   	        		var mp_term_name = oTr.find('td:nth-child(2)').text();
   	        		var gwas_disease_trait = oTr.find('td:nth-child(3)').text();
   	        		
					$.ajax({
		                'url': baseUrl + '/gwaslookup?mgi_gene_symbol=' + mgi_gene_symbol + '&mp_term_name=' + mp_term_name + '&gwas_disease_trait=' + gwas_disease_trait,
		                "type": "POST",
		                'dataType': 'html',
		                'success': function (detailedTable) {
	       	        	 	//console.log(detailedTable)
	       	        	 	
							//add the response html to the target row
	       	        		oRow.child(detailedTable).show();
	       	        		oTr.find('td > i.fa').addClass('fa-minus-square');
	       	    		},
	       	    		'error': function(jqXHR, textStatus, errorThrown) {
	       		    		oRow.child('Error fetching data ...').show();
	       		    	}	
					});
   	        		
	       		}
               	
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
								<span class='ginput'></span>Search by gene, SNP id, GWAS trait, and IMPC phenotype:<br><input type='text' value='' id ='gwasInput' class='gwaslookup'><a><i class='fa fa-info gwasSearchExample'></i></a>
							
                                <!-- container to display dataTable -->									
                                <div id="overviewTable"></div>
                            </div>
                        </div>
                    </div>				
                </div>
            </div>
        </div>		       

    </jsp:body>
</t:genericpage>

