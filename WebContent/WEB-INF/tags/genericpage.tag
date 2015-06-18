<%@ tag description="Overall Page template" pageEncoding="UTF-8" 
        import="uk.ac.ebi.phenotype.web.util.DrupalHttpProxy,java.net.URLEncoder"
        %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://htmlcompressor.googlecode.com/taglib/compressor" prefix="compress" %>


<%-- -------------------------------------------------------------------------- --%>
<%-- NOTE: All "magic" variables are defined in the DeploymentInterceptor class --%>
<%-- This includes such variables isbaseUrl, drupalBaseUrl and releaseVersion.. --%>
<%-- -------------------------------------------------------------------------- --%>


<%
    /*
     Get the menu JSON array from drupal, fallback to a default menu when drupal
     cannot be contacted
     */
    DrupalHttpProxy proxy = new DrupalHttpProxy(request);
    String url = (String) request.getAttribute("drupalBaseUrl");

    String content = proxy.getDrupalMenu(url);
    String[] menus = content.split("MAIN\\*MENU\\*BELOW");

    String baseUrl = (request.getAttribute("baseUrl") != null &&  ! ((String) request.getAttribute("baseUrl")).isEmpty()) ? (String) request.getAttribute("baseUrl") : (String) application.getInitParameter("baseUrl");
    jspContext.setAttribute("baseUrl", baseUrl);


    // Use the drupal destination parameter to redirect back to this page
    // after logging in
    String dest = (String) request.getAttribute("javax.servlet.forward.request_uri");
    String destUnEncoded = dest;
    if (request.getQueryString() != null) {
        dest += URLEncoder.encode("?" + request.getQueryString(), "UTF-8");
        destUnEncoded += "?" + request.getQueryString();
    }

    String usermenu = menus[0]
            .replace("current=menudisplaycombinedrendered", "destination=" + dest)
            .replace("user/register", "user/register?destination=" + dest)
            .replace(request.getContextPath(), baseUrl.substring(1));

    jspContext.setAttribute("usermenu", usermenu);
    
    String genericSearchInputBox = 
    	    "<div class='searchcontent'>"
    		+	"<div id='bigsearchbox' class='block'>"
    		+		"<div class='content'>"								
    		+			"<p><i id='sicon' class='fa fa-search'></i>"
    		+				"<div class='ui-widget'>"
    		+					"<input id='s'>"
    		+ 					"<a><i class='fa fa-info searchExample'></i></a>"
			//+ 					"<i class='fa fa-info searchExample'></i>"
    		+					"<a href='"+baseUrl+"/batchQuery'><i class='fa fa-user batchQuery'></i></a>"
    		//+					"<i class='fa fa-user batchQuery'></i>"
    		//+					"<a id='batchQuery' href='"+baseUrl+"/batchQuery'><i class='fa fa-user batchQuery'></i></a>"
    		+				"</div>"
    		+			"</p>"									
    		+		"</div>"
    		+	"</div>"
    		+"</div>";
	String genericSearchInputBox2 = 
    	    "<div class='searchcontent'>"
    		+	"<div id='bigsearchbox2' class='block'>"
    		+		"<div class='content'>"								
    		+			"<p><i id='sicon' class='fa fa-search'></i>"
    		+				"<div class='ui-widget'>"
    		+					"<input id='s2' placeholder='for comparison only, not functional'>"
    		+ 					"<i class='fa fa-info searchExample2'></i>"
       		+					"<i class='fa fa-user batchQuery2'></i>"
    		+				"</div>"
    		+			"</p>"									
    		+		"</div>"
    		+	"</div>"
    		+"</div>";
    jspContext.setAttribute("searchBox", genericSearchInputBox2);  		
    String bannerMenu = menus[1] + genericSearchInputBox;
    jspContext.setAttribute("menu", bannerMenu);
    //jspContext.setAttribute("menu", menus[1]);
%>
<%@attribute name="header" fragment="true"%>
<%@attribute name="footer" fragment="true"%>
<%@attribute name="title" fragment="true"%>
<%@attribute name="breadcrumb" fragment="true"%>
<%@attribute name="bodyTag" fragment="true"%>
<%@attribute name="addToFooter" fragment="true"%>

<c:set var="uri">${pageContext.request.requestURL}</c:set>
<c:set var="domain">${pageContext.request.serverName}</c:set>

<c:set var="queryStringPlaceholder">
    <c:choose>
        <c:when test="${not empty queryString}">${queryString}</c:when>
        <c:otherwise>Search genes, SOP, MP, images by MGI ID, gene symbol, synonym or name</c:otherwise>
    </c:choose>
</c:set>

<!DOCTYPE html>
<html lang="en">
    <head>

        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1.0">

        <title><jsp:invoke fragment="title"></jsp:invoke> | International Mouse Phenotyping Consortium</title>


            <!--  NEW DESIGN CSS -->

            <!-- css -->
        <link href='//fonts.googleapis.com/css?family=Source+Sans+Pro:300,400,600' rel='stylesheet' type='text/css'>
        <link rel="stylesheet" href="${baseUrl}/css/vendor/jquery.ui/jquery.ui.core.css">
        <link rel="stylesheet" href="${baseUrl}/css/vendor/jquery.ui/jquery.ui.slider.css">
        <link rel="stylesheet" href="${baseUrl}/css/vendor/font-awesome/font-awesome.min.css">
        <link rel="stylesheet" href="${baseUrl}/js/vendor/jquery/jquery.qtip-2.2/jquery.qtip.min.css">
        <link rel="stylesheet" href="${baseUrl}/js/vendor/jquery/jquery.fancybox-2.1.5/jquery.fancybox.css">
        <link rel="stylesheet" href="${drupalBaseUrl}/sites/all/modules/feedback_simple/feedback_simple.css">
        <link rel="stylesheet" href="${baseUrl}/js/vendor/DataTables-1.10.4/extensions/TableTools/css/dataTables.tableTools.min.css">
		<link rel="stylesheet" href="${baseUrl}/css/searchPage.css">

        <link href="${baseUrl}/css/default.css" rel="stylesheet" type="text/css" />
        <%--<link href="${baseUrl}/css/wdm.css" rel="stylesheet" type="text/css" />--%>

        <!-- EBI CSS -->
        <link href="${baseUrl}/css/additionalStyling.css" rel="stylesheet" type="text/css" />

        <script>
    var baseUrl = "${baseUrl}";
    var solrUrl = '${solrUrl}';
    var drupalBaseUrl = "${drupalBaseUrl}";
    var mediaBaseUrl = "${mediaBaseUrl}";
    console.log("mediaBaseUrl set="+mediaBaseUrl);
    var pdfThumbnailUrl = "${pdfThumbnailUrl}";
    console.log("pdfThumbnailUrl set="+pdfThumbnailUrl);

            <%--
            Some browsers do not provide a console object see:
            http://stackoverflow.com/questions/690251/what-happened-to-console-log-in-ie8
            http://digitalize.ca/2010/04/javascript-tip-save-me-from-console-log-errors/
            // In case we forget to take out console statements. IE fails otherwise
            --%>
    try {
        console.log(" ");
    } catch (err) {
        var console = {};
        console.log = console.error = console.info = console.debug = console.warn = console.trace = console.dir = console.dirxml = console.group = console.groupEnd = console.time = console.timeEnd = console.assert = console.profile = function () {
        };
    }
        </script>

        <%--
        Include google tracking code on live site
        --%>
        <c:if test="${liveSite}">
            <script>
                (function (i, s, o, g, r, a, m) {
                    i['GoogleAnalyticsObject'] = r;
                    i[r] = i[r] || function () {
                        (i[r].q = i[r].q || []).push(arguments)
                    }, i[r].l = 1 * new Date();
                    a = s.createElement(o),
                            m = s.getElementsByTagName(o)[0];
                    a.async = 1;
                    a.src = g;
                    m.parentNode.insertBefore(a, m)
                })(window, document, 'script', '//www.google-analytics.com/analytics.js', 'ga');

                ga('create', 'UA-23433997-1', 'auto');
                ga('send', 'pageview');
            </script>
        </c:if>

        <!-- Le HTML5 shim, for IE6-8 support of HTML5 elements -->
        <!--[if lt IE 9]>
                <script src="//html5shim.googlecode.com/svn/trunk/html5.js"></script>
                <style>
                #logoImage {margin: 5px; padding:5px;}
                .container .container .navbar .navbar-inner {width:100%}
                img#logoImage{margin-right:10px;padding-right: 30px;}
                </style>
        <![endif]-->

        <!-- NEW DESIGN JAVASCRIPT -->

        <!-- javascript -->
        <script type="text/javascript" src="${baseUrl}/js/head.min.js?v=${version}"></script>
        <!--We're calling these from Google as this will download from the closest geographic location which will speed page-loads for Aussies and Kiwis-->
        <script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
        <script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/jqueryui/1.10.3/jquery-ui.min.js"></script>
        <script type="text/javascript" src="${baseUrl}/js/vendor/DataTables-1.10.4/media/js/jquery.dataTables.min.js?v=${version}"></script>
        <script type="text/javascript" src="${baseUrl}/js/vendor/DataTables-1.10.4/extensions/TableTools/js/dataTables.tableTools.min.js?v=${version}"></script>
        <script type="text/javascript" src="${baseUrl}/js/vendor/jquery.jeditable.js?v=${version}"></script>


        <!--[if lt IE 9 ]><script type="text/javascript" src="js/selectivizr-min.js"></script><![endif]-->
        <script type="text/javascript" src="${baseUrl}/js/vendor/jquery/jquery.qtip-2.2/jquery.qtip.min.js?v=${version}"></script>
        <script type="text/javascript" src="${baseUrl}/js/vendor/jquery/jquery.fancybox-2.1.5/jquery.fancybox.pack.js?v=${version}"></script>
        <script type="text/javascript" src="${baseUrl}/js/vendor/jquery/jquery.tablesorter.min.js?v=${version}"></script>
        <script type='text/javascript' src='${baseUrl}/js/charts/highcharts.js?v=${version}'></script>
        <script type='text/javascript' src='${baseUrl}/js/charts/highcharts-more.js?v=${version}'></script>
        <script type='text/javascript' src='${baseUrl}/js/charts/exporting.js?v=${version}'></script>
        <script type='text/javascript' src="${baseUrl}/js/general/toggle.js?v=${version}"></script> 

        <script type="text/javascript" src="${baseUrl}/js/default.js?v=${version}"></script>
 		
        <jsp:invoke fragment="header" />

        <%-- Always use www.mousephenotype.org as the canonical domain, except for bare pages --%>
        <c:choose>
            <c:when test="${param['bare'] == null}">
                <link rel="canonical" href="http://www.mousephenotype.org/data<%= destUnEncoded.replaceAll(request.getContextPath(), "")%>" />
            </c:when>
        </c:choose>

    </head>


    <jsp:invoke fragment="bodyTag"/>
    
    <c:if test="${!param['bare'].equalsIgnoreCase(\"true\")}">
	    <div id="feedback_simple">
	        <a class="feedback_simple-right feedback_simple" style="top: 35%; height: 100px; width: 35px;" target="_self" href=""><img src="${drupalBaseUrl}/sites/all/modules/feedback_simple/feedback_simple.gif" /></a>
	    </div>
    </c:if>
    
    <div id="wrapper">
        <c:choose>
            <c:when test="${param['bare'] == null}">

                <header id="header">
                    <div class="region region-header">

                        <div id="tn">
                            <div class="region region-usernavi">
                                <div id="block-system-user-menu" class="block block-system block-menu">
                                    <div class="content">
                                        <ul class="menu">${usermenu}</ul>
                                        <div class="clear"></div>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div id="logo">
                            <a href="${drupalBaseUrl}/"><img src="${baseUrl}/img/impc.png" alt="IMPC Logo" /></a>
                            <div id="logoslogan">International Mouse Phenotyping Consortium</div>
                        </div>

                        <nav id="mn">${menu}</nav>
                        <div class="clear"></div>
                    </div>
                   
                </header>

                <div id="main">
                    <div class="breadcrumb">
                        <a href="${drupalBaseUrl}">Home</a> &raquo; <a href="${baseUrl}/search">Search</a><jsp:invokefragment="breadcrumb" /><%-- breadcrumbs here --%>
                        ${searchBox}
                    </div>
                    <jsp:doBody />
                </div>
                <!-- /main -->

                <footer id="footer">

                    <div class="centercontent">
                        <div class="region region-footer">
                            <div id="block-block-7" class="block block-block">
                                <div class="content">
                                    <img src="${baseUrl}/img/footerLogos.jpg" />
                                    <div class="clear"></div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div id="footerline">

                        <div class="centercontent">

                            <div id="footersitemap" class="twothird left">&nbsp;</div>
                            <div class="onethird right">

                                <div id="vnavi">
                                    <ul>
                                        <li><a href="${drupalBaseUrl}/data/release">Release: ${releaseVersion}</a></li>
                                        <li><a href="ftp://ftp.ebi.ac.uk/pub/databases/impc/">FTP</a></li>
                                        <li><a href="http://raw.github.com/mpi2/PhenotypeArchive/master/LICENSE">License</a></li>
                                        <li><a href="http://raw.github.com/mpi2/PhenotypeArchive/master/CHANGES">Changelog</a></li>
                                    </ul>
                                </div>

                                <div class="clear"></div>

                                <p class="textright">&copy; 2015 IMPC &middot; International Mouse Phenotyping Consortium</p>

                                <div class="clear"></div>

                            </div>

                            <div class="clear"></div>

                        </div>

                    </div>

                    <jsp:invoke fragment="addToFooter"/>

                </footer>

            </c:when>
            <c:otherwise>
                <div id="main">
                    <jsp:doBody />
                </div>
                <!-- /main -->
                <footer id="footer">
                    <jsp:invoke fragment="addToFooter"/>
                </footer>
            </c:otherwise>
        </c:choose>

        <!-- <script type="text/javascript" src='${baseUrl}/js/script.min.js?v=${version}' ></script>-->
		<script type='text/javascript' src='${baseUrl}/js/searchAndFacet/searchAndFacetConfig.js?v=${version}'></script>
        <script type='text/javascript' src='${baseUrl}/js/utils/tools.js?v=${version}'></script>                 
        <script type='text/javascript' src='${baseUrl}/js/general/ui.dropdownchecklist_modif.js?v=${version}'></script>     	    
        <script type='text/javascript' src='${baseUrl}/js/documentationConfig.js?v=${version}'></script>

        <script type='text/javascript'>
		$(document).ready(function () {
			
			var baseUrl = "${baseUrl}";
			var solrUrl = "${solrUrl}"; 	
			
		    // assign the url to feedback link dynamically
		    // this won't work with hashtag change which is taken care of in search.jsp
		    $('a.feedback_simple').attr('href', '/website-feedback?page=' + document.URL);
		    
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
	    	
	    	$("i.batchQuery").qtip({            	   
                content: "Click to go to batch query page",
                style: {
 			 		classes: 'qtipimpc',			 		
 			        tip: {corner: 'top center'}
 			    },
 			    position: {my: 'left top',
 			    		   adjust: {x: -125 , y: 0}
 			    }
	    	});
	    	
	    	 $("span.direct").qtip({            	   
                 content: "Phenotypic mappings have been accessed with manual curation. "
                 	+ "A mapping was labelled as direct when mouse and human phenotypes are identical "
                 	+ "or highly related (ie: bone mineral density in GWAS vs increased bone mineral content in IMPC)",
                 	style: {
     			 		classes: 'qtipimpc',			 		
     			        tip: {corner: 'top center'}
     			    },
     			    position: {my: 'left top',
     			    		   adjust: {x: -280, y: 0}
     			    }
      		});		 	
              $("span.indirect").qtip({  	
              	content: "Phenotypic mappings have been accessed with manual curation. "
              	+ "A mapping was labelled as indirect when mouse and human phneotypes are more "
              	+ "loosely linked although related (ie: digit length ratio in "
              	+"GWAS vs abnormal digit morphology in IMPC)",
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
	   					url: solrUrl + "/autosuggest/select?wt=json&qf=string auto_suggest&defType=edismax" + solrBq,	
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
	       	}).data("ui-autocomplete")._renderItem = function( ul, item) { // prevents
																			// HTML
																			// tags
																			// being
																			// escaped
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
 	
 	
		
        </script>  

    </div> <!-- wrapper -->
</body>

