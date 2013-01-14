<%@tag description="Overall Page template" pageEncoding="UTF-8" import="java.util.Properties"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
	/*
	This block sets the version number to be displayed in the footer
	by reading it from the Implemtation-Version in the manifest.  That
	gets set whenever the version number is incremented in the POM.
	*/
	Properties prop = new Properties();
	prop.load(  application.getResourceAsStream("/META-INF/MANIFEST.MF"));
	String version = prop.getProperty("Implementation-Version");
	if (version == null) {
		version = "<span class='label label-important'>development</span>";
	}
	getJspContext().setAttribute("version", version);
%>
<%@attribute name="header" fragment="true"%>
<%@attribute name="footer" fragment="true"%>
<%@attribute name="title" fragment="true"%>
<% // the baseUrl variable is set from the DeploymentInterceptor class %>

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

<link type='text/css' rel='stylesheet' href='https://ajax.googleapis.com/ajax/libs/jqueryui/1.8.18/themes/base/jquery-ui.css' />
<link type='text/css' rel='stylesheet' href='${baseUrl}/css/pheno.css'  />
<link type='text/css' rel='stylesheet' href='${baseUrl}/css/searchAndFacet.css' />	
<link type='text/css' rel='stylesheet' href='${baseUrl}/css/bootstrap.min.css'  />
<link type='text/css' rel='stylesheet' href='${baseUrl}/css/bootstrap-responsive.min.css'  />
<link type='text/css' rel='stylesheet' href='${baseUrl}/css/vendor/DataTables-1.9.4/jquery.dataTables.css' media='all' />
<link type='text/css' rel='stylesheet' href='${baseUrl}/css/vendor/DataTables-1.9.4/customDataTable.css' media='all' />
<link type='text/css' rel='stylesheet' href='${baseUrl}/css/custom.css'  />

<style>

body {
background-image: url('https://www.mousephenotype.org/sites/all/themes/impc_zen/images/bannerBG.jpg');
background-repeat: repeat-x;
}
#menus {font-size:85%;}
.navbar .nav  li  a {padding:5px 10px; color:#131313;}

/*
   changed base path for servlet to /.  Must serve images from the resource
   mapping defined in mvc-config.xml
*/
[class^="icon-"],
[class*=" icon-"] {
  background-image: url("${baseUrl}/img/glyphicons-halflings.png");
}

/* White icons with optional class, or on hover/active states of certain elements */

.icon-white,
.nav-tabs > .active > a > [class^="icon-"],
.nav-tabs > .active > a > [class*=" icon-"],
.nav-pills > .active > a > [class^="icon-"],
.nav-pills > .active > a > [class*=" icon-"],
.nav-list > .active > a > [class^="icon-"],
.nav-list > .active > a > [class*=" icon-"],
.navbar-inverse .nav > .active > a > [class^="icon-"],
.navbar-inverse .nav > .active > a > [class*=" icon-"],
.dropdown-menu > li > a:hover > [class^="icon-"],
.dropdown-menu > li > a:hover > [class*=" icon-"],
.dropdown-menu > .active > a > [class^="icon-"],
.dropdown-menu > .active > a > [class*=" icon-"] {
  background-image: url("${baseUrl}/img/glyphicons-halflings-white.png");
}

</style>

<!-- Le fav and touch icons -->
<link rel="shortcut icon" href="../assets/ico/favicon.ico">
<link rel="apple-touch-icon-precomposed" sizes="144x144" href="../ico/apple-touch-icon-144-precomposed.png">
<link rel="apple-touch-icon-precomposed" sizes="114x114" href="../ico/apple-touch-icon-114-precomposed.png">
<link rel="apple-touch-icon-precomposed" sizes="72x72" href="../ico/apple-touch-icon-72-precomposed.png">
<link rel="apple-touch-icon-precomposed" href="../ico/apple-touch-icon-57-precomposed.png">

<script>
var baseUrl='${baseUrl}';
var drupalBaseUrl = "${drupalBaseUrl}";

<%-- 
Some browsers do not provide a console object see:
http://stackoverflow.com/questions/690251/what-happened-to-console-log-in-ie8
http://digitalize.ca/2010/04/javascript-tip-save-me-from-console-log-errors/
--%>
//In case we forget to take out console statements. IE becomes very unhappy when we forget. Let's not make IE unhappy
try {
	console.log("Testing the console.log protection");
} catch(err) {
	var console = {};
	console.log = console.error = console.info = console.debug = console.warn = console.trace = console.dir = console.dirxml = console.group = console.groupEnd = console.time = console.timeEnd = console.assert = console.profile = function() {};
}
</script>

<!-- Le HTML5 shim, for IE6-8 support of HTML5 elements -->
<!--[if lt IE 9]>
	<script src="https://html5shim.googlecode.com/svn/trunk/html5.js"></script>
	<style>
	#logoImage {margin: 5px; padding:5px;}
	.container .container .navbar .navbar-inner {width:100%}
	img#logoImage{margin-right:10px;padding-right: 30px;}
	</style>
<![endif]-->

<!-- jquery -->
<script type='text/javascript' src="https://ajax.googleapis.com/ajax/libs/jquery/1.8.1/jquery.min.js"></script>
<script type='text/javascript' src='https://ajax.googleapis.com/ajax/libs/jqueryui/1.8.23/jquery-ui.min.js'></script>
<script>window.jQuery || document.write('<script src="${baseUrl}/js/vendor/jquery-1.7.2.min.js"><\/script><script src="${baseUrl}/js/vendor/jquery-ui-1.8.18.min.js"><\/script><link type="text/css" rel="stylesheet" href="${baseUrl}/css/vendor/jquery-ui-1.8.18.css" />');</script>

<jsp:invoke fragment="header" />

</head>
<body>
	<div class="container" style="padding-top:0;">

		<div class="navbar" style="margin:0; padding:0;text-transform:uppercase;font-weight:bold;">

			<div class="navbar">
				<div class="row">
			<a href="/" id="logo" class="pull-left">
			<img id="logoImage" src="${baseUrl}/img/logo.png" alt="International Mouse Phenotyping Consortium"/>
			</a>
					<ul id="menus" class="nav" role="navigation" style="padding-bottom:0;"><!-- generic page -->
						<li><a tabindex="-1" href="/">Home</a></li>
						<li class="dropdown">
							<a id="drop1" data-target="#" href="/background" class="dropdown-toggle" data-toggle="dropdown">About IMPC<b class="caret"></b></a>
							<ul class="dropdown-menu" role="menu" aria-labelledby="drop1">
								<li><a href="/background">About IMPC</a></li>
								<li><a href="/background/impc-goals">IMPC Goals</a></li>
								<li class="dropdown-submenu">
									<a href="/impc-steering-committee" data-target="#" tabindex="-1">Members</a>
									<ul class="dropdown-menu">
										<li><a href="/members/impc-global-and-still-expanding-research-initiative">Locations</a></li>
									</ul>
								</li>
								<li><a href="/impc-secretariat">Secretariat</a></li>
								<li class="dropdown-submenu">
									<a href="/impc-work-groups" data-target="#">Working Groups</a>
									<ul class="dropdown-menu">
										<li><a href="/workgroups/impc-it-work-group">IT Work Group</a></li>
										<li><a href="/workgroups/impc-phenotyping-work-group">Phenotyping Work Group</a></li>
										<li><a href="/impc-industry-liaison-work-group">Industry Work Group</a></li>
									</ul>
								</li>
								<li><a tabindex="-1" href="/links">Links</a></li>
							</ul>
						</li>
						<li class="dropdown"><a href="${baseUrl}/search" role="button">Search Genes and Protocols</a></li>

						<li class="dropdown">
							<a href="/news" data-target="#" id="drop2" role="button" class="dropdown-toggle" data-toggle="dropdown">News and Events<b class="caret"></b></a>
							<ul class="dropdown-menu" role="menu" aria-labelledby="drop2">
								<li><a href="/news">News and Events</a></li>
								<li><a href="/telephone-calendar/month">Teleconference and Events Calandar</a></li>
							</ul>
						</li>
						<li class="dropdown"><a href="/contact/Beta%20Website%20Feedback" role="button" data-target="#" id="drop2" class="dropdown-toggle" data-toggle="dropdown">Send Us Feedback</a>
							<ul class="dropdown-menu" role="menu" aria-labelledby="drop3">
								<li><a href="/contact/Beta%20Website%20Feedback">Send Us Feedback</a></li>
								<li><a href="/send-us-feedback/newsletters">Teleconference and Events Calandar</a></li>
							</ul>
						</li>

						<li class="dropdown">
							<a data-target="#" id="drop3" role="button" class="dropdown-toggle" data-toggle="dropdown">My IMPC<b class="caret"></b></a>
							<ul class="dropdown-menu" role="menu" aria-labelledby="drop3">
								<li><a href="/user/login">Login</a></li>
								<li><a href="http://mousephenotype.org/imits">Access IMITS</a></li>
								<li><a href="/user/register">Register</a></li>
							</ul>
						</li>
					</ul>

					<span class="row nav input-append" style="padding: 20px 15px 5px 15px;">
						
						<input id="userInput" type="text" class="span4" value='${queryStringPlaceholder}' />
						<button id="acSearch" type="submit" class="btn"><i class="icon-search"></i> Search</button>
						<div id="bannerSearch"></div>
						<a href="#examples" data-toggle="modal" style="color:#333333;font-size:12px;text-decoration: underline;text-shadow:none;text-transform:none;font-weight:normal;">View example searches</a>
					</span>
				</div>
			</div>

		</div>

		<div class="container">
		<jsp:doBody />
		</div>

		<div id="examples" class="modal hide" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
				<h3>Example Searches</h3>
			</div>

			<div class="modal-body">
				<p>Sample queries for several fields are shown. Click the desired <a onclick="return false;">query</a> to execute any of the samples. 
				<span class="text-success">Note that queries are focused on Relationships, leaving modifier terms to be applied as filters.</span></p>
	
				<div>
					<h4>Gene query examples</h4>
					<p><a class="example" href="javascript:void(0)">pax6</a> - looking for gene Pax6</p>
					<p><a class="example" href="javascript:void(0)">*rik</a> - looking for all Riken genes</p>
					<p><a class="example" href="javascript:void(0)">fbox*</a> - looking for fbox genes</p>
				</div>
			
				<div>
					<h4>Phenotype query examples</h4>
					<p><a class="example" href="javascript:void(0)">abnormal skin morphology</a> - looking for a specific phenotype</p>
					<p><a class="example" href="javascript:void(0)">ear</a> - find all ear related phenotypes</p>
				</div>
			
				<div>
					<h4>Procedure query Example</h4>
					<p><a class="example" href="javascript:void(0)">grip strength</a> - looking for a specific procedure</p>
				</div>
			</div>
		</div>

		<div class="row-fluid" id='logoFooter'>
			<div class="span12 centered-text">
				<img alt="" class="footerLogos"
					src="https://beta.mousephenotype.org/sites/all/themes/impc_zen/images/footerLogos.jpg"
					style="width: 1222px; height: 50px;">
				<jsp:invoke fragment="footer" />
				<small class="muted">
				Version: <c:out value="${version}" escapeXml="false"/> • <a href="${baseUrl}/NOTICE.txt">License</a> • <a href="${baseUrl}/CHANGES.txt">Changelog</a>  
				</small>
			</div>

		</div>
	</div><!-- /container -->

	<script type='text/javascript' src="${baseUrl}/js/bootstrap/bootstrap.min.js"></script>	
	<script type='text/javascript' src="${baseUrl}/js/utils/searchAndFacetConfig.js"></script>	
	<script type='text/javascript' src='${baseUrl}/js/vendor/DataTables-1.9.4/jquery.dataTables.js'></script>
	<script type='text/javascript' src='${baseUrl}/js/vendor/DataTables-1.9.4/core.filter.js'></script>
	<script type='text/javascript' src='${baseUrl}/js/vendor/DataTables-1.9.4/TableTools.min.js'></script>
	<script type='text/javascript' src='${baseUrl}/js/utils/tools.js'></script>
	<script type='text/javascript' src='${baseUrl}/js/searchAndFacet/autocompleteWidget.js'></script>
	<script type='text/javascript' src='${baseUrl}/js/searchAndFacet/sideBarFacetWidget.js'></script>  
	<script type='text/javascript' src='${baseUrl}/js/searchAndFacet/searchAndFacet_primer.js'></script>
	<script type='text/javascript' src='${baseUrl}/js/vendor/respond.min.js'></script>
	<script type='text/javascript' src='${baseUrl}/js/vendor/jquery.corner.mini.js'></script>

 	<script>
	$(document).ready(function() {
		// wire up the example queries
		$("a.example").click(function(){
			$('input#userInput').attr("value",$(this).text());
			$('#examples').modal('hide');
			var form = "<form id='hiddenSrch' action='" + baseUrl + "/search' method='post'>"
            + "<input type='text' name='queryString' value='" + $(this).text() + "'>"
			+ "<input type='text' name='type' value='gene'>"
			+ "<input type='text' name='geneFound' value='1'>"			                                
            + "</form>";                     
			window.jQuery('div#bannerSearch').append(form);
			window.jQuery('form#hiddenSrch').hide().submit();
		});

		// Message to IE users
		$.fn.ieCheck();

		// Display the logout and phenoDCC menu items if appropriate
		$.ajax({
		  	type: "GET",
		  	cache: false,
		 	dataType: 'html',		  	
		 	url: "${baseUrl}/menudisplay",
		 	success: function(data) {				
		   		var items = [];
		    	if (data.indexOf("Logout") !=-1) {
					items.push('<li class="dropdown"><a href="/user/logout?current=menudisplay" role="button">Logout</a></li>');
		            items.push('<li class="dropdown"><a href="/phenodcc" role="button">PhenoDCC</a></li>');				 
				 	$('#menus').append(items);
		    	}		    	 
		  	}
		});
	});	
	</script>
</body>
</html>