<%@tag description="Overall Page template" pageEncoding="UTF-8" import="java.util.Properties"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
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
<link type='text/css' rel='stylesheet' href="${baseUrl}/css/pheno.css"  />
<link type='text/css' rel='stylesheet' href='${baseUrl}/css/searchAndFacet.css' />	
<link type='text/css' rel='stylesheet' href="${baseUrl}/css/bootstrap.css"  />
<link type='text/css' rel='stylesheet' href="${baseUrl}/css/bootstrap-responsive.css"  />
<link type='text/css' rel='stylesheet' href='${baseUrl}/css/vendor/DataTables-1.9.4/jquery.dataTables.css' media='all' />
<link type='text/css' rel='stylesheet' href='${baseUrl}/css/vendor/DataTables-1.9.4/customDataTable.css' media='all' />
<link type='text/css' rel='stylesheet' href="${baseUrl}/css/custom.css"  />

<style>

body {
background-image: url('https://www.mousephenotype.org/sites/all/themes/impc_zen/images/bannerBG.jpg');
background-repeat: repeat-x;
}

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
<!-- Le HTML5 shim, for IE6-8 support of HTML5 elements -->
<!--[if lt IE 9]>
	<script src="https://html5shim.googlecode.com/svn/trunk/html5.js"></script>
	<style>
	#logoImage {margin: 5px; padding:5px;}
	.container .container .navbar .navbar-inner {width:100%}
	img#logoImage{margin-right:10px;padding-right: 30px;}
	</style>
<![endif]-->

<!-- Le fav and touch icons -->
<link rel="shortcut icon" href="../assets/ico/favicon.ico">
<link rel="apple-touch-icon-precomposed" sizes="144x144" href="../ico/apple-touch-icon-144-precomposed.png">
<link rel="apple-touch-icon-precomposed" sizes="114x114" href="../ico/apple-touch-icon-114-precomposed.png">
<link rel="apple-touch-icon-precomposed" sizes="72x72" href="../ico/apple-touch-icon-72-precomposed.png">
<link rel="apple-touch-icon-precomposed" href="../ico/apple-touch-icon-57-precomposed.png">

<!-- jquery -->
<script type='text/javascript' src="https://ajax.googleapis.com/ajax/libs/jquery/1.8.1/jquery.min.js"></script>
<script type='text/javascript' src='https://ajax.googleapis.com/ajax/libs/jqueryui/1.8.23/jquery-ui.min.js'></script>
<script>window.jQuery || document.write('<script src="${baseUrl}/js/vendor/jquery-1.7.2.min.js"><\/script><script src="${baseUrl}/js/vendor/jquery-ui-1.8.18.min.js"><\/script><link type="text/css" rel="stylesheet" href="${baseUrl}/css/vendor/jquery-ui-1.8.18.css" />');</script>

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

<jsp:invoke fragment="header" />

</head>
<body>
	<div class="container" style="padding-top:0;">

		<div class="navbar" style="margin:0; padding:0;text-transform:uppercase;font-weight:bold;">

			<div class="navbar">
				<div class="row">
			<a href="${baseUrl}/searchAndFacet" id="logo" class="pull-left">
			<img id="logoImage" src="${baseUrl}/img/logo.png" alt="International Mouse Phenotyping Consortium"/>
			</a>
					<ul id="menus" class="nav" role="navigation" style="padding-bottom:0;"><!-- drupal page -->
						<li><a tabindex="-1" href="${baseUrl}/">Home</a></li>
						<li class="dropdown">
							<a id="drop1" data-target="#" href="${baseUrl}/" class="dropdown-toggle" data-toggle="dropdown">About IMPC<b class="caret"></b></a>
							<ul class="dropdown-menu" role="menu" aria-labelledby="drop1">
								<li><a tabindex="-1" href="${baseUrl}/">About IMPC</a></li>
								<li><a tabindex="-1" href="#anotherAction">IMPC Goals</a></li>
								<li class="divider"></li>
								<li class="dropdown-submenu"><a data-target="#" tabindex="-1">Members</a>
									<ul class="dropdown-menu">
										<li><a tabindex="-1" href="">Locations</a></li>
									</ul></li>
								<li class="divider"></li>
								<li><a tabindex="-1" href="">Secretariat</a></li>
								<li class="divider"></li>
								<li class="dropdown-submenu"><a tabindex="-1" data-target="#">Working Groups</a>
									<ul class="dropdown-menu">
										<li><a tabindex="-1" href="">IT Work Group</a></li>
										<li><a tabindex="-1" href="">Phenotyping Work Group</a></li>
										<li><a tabindex="-1" href="">Industry Work Group</a></li>
									</ul></li>
								<li class="divider"></li>
								<li><a tabindex="-1" href="">Links</a></li>
							</ul></li>
						<li class="dropdown"><a href="${baseUrl}/searchAndFacet" role="button">Search Genes and Protocols</a></li>

						<li class="dropdown">
							<a data-target="#" id="drop2" role="button" class="dropdown-toggle" data-toggle="dropdown">News and Events<b class="caret"></b></a>
							<ul class="dropdown-menu" role="menu" aria-labelledby="drop2">
								<li><a tabindex="-1" href="#">Teleconference and Events Calandar</a></li>
								<li><a tabindex="-1" href="#">News and Events</a></li>
							</ul></li>
						<li class="dropdown"><a href="" role="button">Send Us Feedback</a></li>

						<li class="dropdown">
							<a data-target="#" id="drop3" role="button" class="dropdown-toggle" data-toggle="dropdown">My IMPC<b class="caret"></b></a>
							<ul class="dropdown-menu" role="menu" aria-labelledby="drop3">
								<li><a tabindex="-1" href="#">Login</a></li>
								<li><a tabindex="-1" href="#">Access IMITS</a></li>
								<li><a href="">Register</a></li>
							</ul>
						</li>
					</ul>

					<span class="row nav input-append" style="padding: 20px 15px 5px 15px;">
						
						<input id="userInput" type="text" class="span4" value='${queryStringPlaceholder}' />
						<button id="acSearch" type="submit" class="btn"><i class="icon-search"></i> Search</button>						
						<div id="bannerSearch"></div>
					</span>
				</div>
			</div>

		</div>
<br />
		<div class="container">
		<jsp:doBody />
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

	<script type='text/javascript' src="${baseUrl}/js/bootstrap/bootstrap.js"></script>	
	<script type='text/javascript' src='${baseUrl}/js/vendor/DataTables-1.9.4/jquery.dataTables.js'></script>
	<script type='text/javascript' src='${baseUrl}/js/vendor/DataTables-1.9.4/core.filter.js'></script>
	<script type='text/javascript' src='${baseUrl}/js/searchAndFacet/autocompleteWidget.js'></script>
	<script type='text/javascript' src='${baseUrl}/js/searchAndFacet/sideBarFacetWidget.js'></script>  
	<script type='text/javascript' src='${baseUrl}/js/searchAndFacet/searchAndFacet_primer.js'></script>
	<script type='text/javascript' src='${baseUrl}/js/respond.min.js'></script>
	<script type='text/javascript' src='${baseUrl}/js/vendor/jquery.corner.mini.js'></script>
	<script type='text/javascript' src='${baseUrl}/js/utils/tools.js'></script>

 	<script>
	$(document).ready(function() {
		$.fn.ieCheck();	// delivers message to legacy IE users
		
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
