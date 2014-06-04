<link href="//dev.mousephenotype.org/data/css/vendor/jquery.ui/jquery.ui.core.css" rel="stylesheet" />
<link href="//dev.mousephenotype.org/data/css/vendor/jquery.ui/jquery.ui.theme.css" rel="stylesheet" />
<link href="//dev.mousephenotype.org/data/css/searchPage.css" rel="stylesheet" />
<form action="/data/search" method="GET">
<p><input id="q" name="q" placeholder="Search" type="text" /></p>

<p>Enter your favorite <b>gene</b>, <b>phenotype</b>, <b>anatomy</b> or <b>protocol</b> to find IMPC data important to your research.</p>
</form>

<div class="reposition">
<p>Or browse</p>

<p><a class="btn" href="/data/search#q=*:*&amp;fq=%28imits_phenotype_started:%221%22%29&amp;facet=gene">new gene-phenotype associations</a></p>
</div>
<script src="//ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script><script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.10.3/jquery-ui.min.js"></script><script type='text/javascript'>
	var $ = jQuery;
$(document).ready(function(){
      var solrUrl = '//dev.mousephenotype.org/mi/impc/dev/solr';
      var baseUrl = '//dev.mousephenotype.org/data';
      var matchedFacet = false;
      var kw = false;

    // if users do not hit ENTER too quickly and wait until drop down list appears, then we know about which facet to display by default
   // else if will be gene facet
     $("form").keypress(function (e) {
            var key = CheckBrowser(e);
            if (key == 13 && matchedFacet  ) {
            document.location.href = baseUrl + '/search?q=' + kw + '#facet=' + matchedFacet; 	
                e.preventDefault();
                //return false;
            }        
        });

       	$(function() {
       		$( "input#q" ).autocomplete({
       			source: function( request, response ) {
       				$.ajax({
       					url: solrUrl + "/autosuggest/select?wt=json&qf=auto_suggest&defType=edismax",	
       					dataType: "jsonp",
       					'jsonp': 'json.wrf',
       					data: {
       						q: request.term
		       			},
		       			success: function( data ) {
		       				
			       	        matchedFacet = false; // reset
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
		       							
		       							if (i == 0){
		       								// take the first found in autosuggest and open that facet
		       								matchedFacet = facet;			       				
                                                                                kw = term;			
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

});
function CheckBrowser(e) {
        if (window.event)
            key = window.event.keyCode;     //IE
        else
            key = e.which;     //firefox
        return key;
    }

</script>