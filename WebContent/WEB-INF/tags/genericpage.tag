<%@tag description="Overall Page template" pageEncoding="UTF-8" import="java.util.Properties,uk.ac.ebi.phenotype.web.util.DrupalHttpProxy,net.sf.json.JSONArray"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<%
	/*
	This block sets the version number to be displayed in the footer
	by reading it from the Implemtation-Version in the manifest.  That
	gets set by Maven whenever the app is deployed and the version number 
	is the same as in the POM.  Overridden for DEV,BETA, and local (non-maven) 
	deployments.
	*/
	Properties prop = new Properties();
	prop.load(  application.getResourceAsStream("/META-INF/MANIFEST.MF"));
	String version = prop.getProperty("Implementation-Version");
	if (request.getRequestURL().toString().toLowerCase().contains("beta")) {
		version = "<span class='label label-important'>BETA</span>";
	}
	if (request.getRequestURL().toString().toLowerCase().contains("dev") || version == null) {
		version = "<span class='label label-important'>development</span>";
	}
	jspContext.setAttribute("version", version);

	/*
	Get the menu JSON array from drupal, fallsback to a default menu when drupal
	cannot be contacted
	*/
	DrupalHttpProxy proxy = new DrupalHttpProxy(request);
	jspContext.setAttribute("menu", proxy.getDrupalMenu((String)request.getAttribute("drupalBaseUrl")));
	
%>
<%@attribute name="header" fragment="true"%>
<%@attribute name="footer" fragment="true"%>
<%@attribute name="title" fragment="true"%>
<%@attribute name="breadcrumb" fragment="true"%>
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
#menus {font-size:88%;}
.navbar .nav  li  a {padding:5px 10px; font-size:13px; color:#555555;}

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
var solrUrl='${solrUrl}';
var drupalBaseUrl = "${drupalBaseUrl}";
var mediaBaseUrl = "${mediaBaseUrl}";

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
			<div class="row">
				<a href="${drupalBaseUrl}/" id="logo" class="span2"><img id="logoImage" src="${baseUrl}/img/IMPC<c:if test='${not fn:contains(drupalBaseUrl,"www")}'>Beta</c:if>logo.png" alt="International Mouse Phenotyping Consortium"/></a>
				<ul id="menus" class="nav span10">
					<c:forEach var="menuitem" items="${menu}" varStatus="loop">
					<c:if test="${menuitem.below != null}">
					<li class="dropdown">
						<a id="drop${loop.count}" data-target="#" class="dropdown-toggle" data-toggle="dropdown" href="${drupalBaseUrl}/${menuitem.href}">${menuitem.title} <b class="caret"></b></a>
						<ul class="dropdown-menu">
							<li><a class="dropdown-submenu" href="<c:if test="${not fn:contains(menuitem.href,'http')}">${drupalBaseUrl}/</c:if>${menuitem.href}">${menuitem.title}</a></li>
							<c:forEach var="submenuitem" items="${menuitem.below}">
							<c:if test="${submenuitem.below != null}">
							<li class="dropdown-submenu">
								<a data-target="#" href="<c:if test="${not fn:contains(submenuitem.href,'http')}">${drupalBaseUrl}/</c:if>${submenuitem.href}">${submenuitem.title}</a>
								<ul class="dropdown-menu">
								<c:forEach var="subsubmenuitem" items="${submenuitem.below}">
									<li><a href="<c:if test="${not fn:contains(submenuitem.href,'http')}">${drupalBaseUrl}/</c:if>${subsubmenuitem.href}">${subsubmenuitem.title}</a></li>
								</c:forEach>
								</ul>
							</li>
							</c:if>
							<c:if test="${submenuitem.below == null}">
							<li><a href="<c:if test="${not fn:contains(submenuitem.href,'http')}">${drupalBaseUrl}/</c:if>${submenuitem.href}">${submenuitem.title}</a></li>
							</c:if>
							</c:forEach>
						</ul>
					</li>
					</c:if>
					<c:if test="${menuitem.below == null}">
					<li><a href="<c:if test="${not fn:contains(menuitem.href,'http')}">${drupalBaseUrl}/</c:if>${fn:replace(menuitem.href,'<front>','')}">${menuitem.title}</a></li>
					</c:if>
					</c:forEach>
				</ul>
				<span id="searchBlock" class="row nav input-append span6">
					<input id="userInput" type="text" />
					<button id="acSearch" type="submit" class="btn"><i class="icon-search"></i> Search</button>
					<div id="bannerSearch"></div>
					<a href="#examples" data-toggle="modal" id="examplesearches" class="pull-right" >View example searches</a>
					<p class="ikmcbreadcrumb">
						<a href="${drupalBaseUrl}">Home</a> &raquo; <a href="${baseUrl}/search">Search</a><jsp:invoke fragment="breadcrumb" /><%-- breadcrumbs here --%>
					</p>
				</span>
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
				<p>Sample queries for several fields are shown. Click the desired query to execute any of the samples. 
				<span class="text-success">Note that queries are focused on Relationships, leaving modifier terms to be applied as filters.</span></p>
	
				<div>
					<h4>Gene query examples</h4>
					<p><a class="example" href="${baseUrl}/search#q=akt2&core=gene">Akt2</a> - looking for a specific gene, Akt2</p>
					<p><a class="example" href="${baseUrl}/search#q=*rik&core=gene">*rik</a> - looking for all Riken genes</p>
					<p><a class="example" href="${baseUrl}/search#q=hox*&core=gene">hox*</a> - looking for all hox genes</p>
				</div>
			
				<div>
					<h4>Phenotype query examples</h4>
					<p><a class="example" href="${baseUrl}/search#q=abnormal skin morphology&core=mp&fq=ontology_subset:*">abnormal skin morphology</a> - looking for a specific phenotype</p>
					<p><a class="example" href="${baseUrl}/search#q=ear&core=mp&fq=ontology_subset:*">ear</a> - find all ear related phenotypes</p>
				</div>
			
				<div>
					<h4>Procedure query Example</h4>
					<p><a class="example" href="${baseUrl}/search#fq=pipeline_stable_id:IMPC_001&q=grip strength&core=pipeline">grip strength</a> - looking for a specific procedure</p>
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
				Version: <c:out value="${version}" escapeXml="false"/> • <a href="http://raw.github.com/mpi2/PhenotypeArchive/master/LICENSE">License</a> • <a href="http://raw.github.com/mpi2/PhenotypeArchive/master/CHANGES">Changelog</a>  
				</small>
			</div>
	
		</div>
	</div><!-- /container -->

	
	<script type='text/javascript' src='${baseUrl}/js/vendor/jquery.ba-bbq.min.js'></script>	
	<script type='text/javascript' src='${baseUrl}/js/bootstrap/bootstrap.min.js'></script>	
	<script type='text/javascript' src='${baseUrl}/js/searchAndFacet/searchAndFacetConfig.js'></script>
		
	<script type='text/javascript' src='${baseUrl}/js/vendor/DataTables-1.9.4/jquery.dataTables.js'></script>
	<script type='text/javascript' src='${baseUrl}/js/vendor/DataTables-1.9.4/core.filter.js'></script>
	<script type='text/javascript' src='${baseUrl}/js/vendor/DataTables-1.9.4/TableTools.min.js'></script>
	<script type='text/javascript' src='${baseUrl}/js/utils/tools.js'></script>
		
	<script type='text/javascript' src='${baseUrl}/js/searchAndFacet/geneFacetWidget.js'></script>
    <script type='text/javascript' src='${baseUrl}/js/searchAndFacet/mpFacetWidget.js'></script>
    <script type='text/javascript' src='${baseUrl}/js/searchAndFacet/pipelineFacetWidget.js'></script>
    <script type='text/javascript' src='${baseUrl}/js/searchAndFacet/imagesFacetWidget.js'></script>
    <script type='text/javascript' src='${baseUrl}/js/searchAndFacet/search.js'></script>
    <script type='text/javascript' src='${baseUrl}/js/searchAndFacet/searchAndFacet_primer.js'></script>	

	<script type='text/javascript' src='${baseUrl}/js/vendor/respond.min.js'></script>
	<script type='text/javascript' src='${baseUrl}/js/vendor/jquery.corner.mini.js'></script>

 	<script>
	$(document).ready(function() {
		// wire up the example queries
   		$("a.example").click(function(){
			$('#examples').modal('hide');
			document.location.href = $(this).attr('href');
			document.location.reload();
		});

		// Message to IE users
		$.fn.ieCheck();
	});	
	</script>
</body>
</html>