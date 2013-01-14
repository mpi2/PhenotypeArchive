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

${drupalHeader}

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
${drupalNavbar}
<br />
		<div class="container" style="padding-top:55px;">
		<jsp:doBody />
		</div>

${drupalFooter}
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
