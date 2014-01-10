<%@tag description="Overall Page template" pageEncoding="UTF-8" import="java.util.Properties,uk.ac.ebi.phenotype.web.util.DrupalHttpProxy,net.sf.json.JSONArray"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://htmlcompressor.googlecode.com/taglib/compressor" prefix="compress" %>

<%
        /*
        This block sets the version number to be displayed in the footer
        by reading it from the Implemtation-Version in the manifest. That
        gets set by Maven whenever the app is deployed and the version number
        is the same as in the POM. Overridden for DEV,BETA, and local (non-maven)
        deployments.
        */
        Properties prop = new Properties();
        prop.load( application.getResourceAsStream("/META-INF/MANIFEST.MF"));
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
        String url = (String)request.getAttribute("drupalBaseUrl");
        url = url.replace("dev.", "test.");
        jspContext.setAttribute("menu", proxy.getDrupalMenu(url));
        
%>
<%@attribute name="header" fragment="true"%>
<%@attribute name="footer" fragment="true"%>
<%@attribute name="title" fragment="true"%>
<%@attribute name="breadcrumb" fragment="true"%>
<%@attribute name="bodyTag" fragment="true"%>

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


<!--  NEW DESIGN CSS -->

<!-- css -->
<link href='http://fonts.googleapis.com/css?family=Source+Sans+Pro:300,400,600' rel='stylesheet' type='text/css'>
<link rel="stylesheet" href="${baseUrl}/css/vendor/jquery.ui/jquery.ui.core.css">
<link rel="stylesheet" href="${baseUrl}/css/vendor/jquery.ui/jquery.ui.theme.css">
<link rel="stylesheet" href="${baseUrl}/css/vendor/jquery.ui/jquery.ui.slider.css">
<link rel="stylesheet" href="${baseUrl}/css/vendor/font-awesome/font-awesome.min.css">
<link rel="stylesheet" href="${baseUrl}/css/vendor/jquery.ui/jquery.qtip.min.css">
<link rel="stylesheet" href="${baseUrl}/css/vendor/jquery.ui/jquery.fancybox-1.3.4.css">
<link href="${baseUrl}/css/default.css?cache=10-01-14" rel="stylesheet" type="text/css" />
<link href="${baseUrl}/css/heatmap.css?cache=10-01-14" rel="stylesheet" type="text/css" />
<link href="${baseUrl}/css/wdm.css?cache=10-01-14" rel="stylesheet" type="text/css" />

<%--
Short circuit favicon requests
See: http://stackoverflow.com/questions/1321878/how-to-prevent-favicon-ico-requests
--%>
<link rel="shortcut icon" href="data:image/x-icon;," type="image/x-icon">

<script type="text/javascript">
var _gaq = _gaq || [];_gaq.push(["_setAccount", "${googleAnalytics}"]);_gaq.push(["_trackPageview"]);(function() {var ga = document.createElement("script");ga.type = "text/javascript";ga.async = true;ga.src = "${drupalBaseUrl}/sites/mousephenotype.org/files/googleanalytics/ga.js?mjafjk";var s = document.getElementsByTagName("script")[0];s.parentNode.insertBefore(ga, s);})();
</script>

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
        console.log(" ");
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
<!-- script type='text/javascript' src="https://ajax.googleapis.com/ajax/libs/jquery/1.8.1/jquery.min.js"></script>
<script type='text/javascript' src='https://ajax.googleapis.com/ajax/libs/jqueryui/1.8.23/jquery-ui.min.js'></script>
<script>window.jQuery || document.write('<script src="${baseUrl}/js/vendor/jquery-1.7.2.min.js"><\/script><script src="${baseUrl}/js/vendor/jquery-ui-1.8.18.min.js"><\/script><link type="text/css" rel="stylesheet" href="${baseUrl}/css/vendor/jquery-ui-1.8.18.css" />');</script-->


<!-- NEW DESIGN JAVASCRIPT -->

<!-- javascript -->
<script type="text/javascript" src="${baseUrl}/js/head.min.js"></script>
<script type="text/javascript" src="${baseUrl}/js/vendor/jquery/jquery_1.5.1.min.js"></script>
<script type="text/javascript" src="${baseUrl}/js/vendor/jquery/jquery.ui.core.min.js"></script>
<script type="text/javascript" src="${baseUrl}/js/vendor/jquery/jquery.ui.widget.min.js"></script>
<script type="text/javascript" src="${baseUrl}/js/vendor/jquery/jquery.ui.mouse.min.js"></script>
<script type="text/javascript" src="${baseUrl}/js/vendor/jquery/jquery.ui.slider.min.js"></script>
<!--[if lt IE 9 ]><script type="text/javascript" src="js/selectivizr-min.js"></script><![endif]-->
<script type="text/javascript" src="${baseUrl}/js/vendor/jquery/jquery.qtip.min.js"></script>
<script type="text/javascript" src="${baseUrl}/js/vendor/jquery/jquery.fancybox-1.3.4.pack.js"></script>
<script type="text/javascript" src="${baseUrl}/js/vendor/jquery/jquery.tablesorter.min.js"></script>
<script src="http://code.highcharts.com/highcharts.js"></script>
<script src="http://code.highcharts.com/highcharts-more.js"></script>
<script type="text/javascript" src="${baseUrl}/js/default.js?cache=09-01-14"></script>



<jsp:invoke fragment="header" />


</head>
<jsp:invoke fragment="bodyTag"/>
	<div id="wrapper">
	 <header id="header">
     

 <div class="region region-header">
            
            <div id="tn">
                <ul>
                    <li><i class="fa fa-user"></i><a href="login.php" title="Login with your account">Login</a></li>
                    <li><i class="fa fa-sign-in"></i><a href="register.php" title="Register for an account">Register</a></li>
                </ul>
            </div>
            
            <div id="logo">
                <!-- a href="index.php"><img src="${baseUrl}/img/IMPC<c:if test='${not fn:contains(drupalBaseUrl,"www")}'>Beta</c:if>logo.png" alt="IMPC Logo" /></a-->
                <a href="index.php"><img src="${baseUrl}/img/impc.png" alt="IMPC Logo" /></a>
                <div id="logoslogan">International Mouse Phenotyping Consortium</div>
            </div>
            
						<nav id="mn">
                <ul class="menu">
                    <c:forEach var="menuitem" items="${menu}" varStatus="loop">
                                        <c:if test="${(menuitem.below != null)}">
                                        <li>
                                                <a id="drop${loop.count}" data-target="#" href="${drupalBaseUrl}/${menuitem.href}">${menuitem.title} </a>
                                                <ul>
                                                        <c:forEach var="submenuitem" items="${menuitem.below}">
                                                         	<li><a href="<c:if test="${not fn:contains(submenuitem.href,'http')}">${drupalBaseUrl}/</c:if>${submenuitem.href}">${submenuitem.title}</a></li>
                                                      	</c:forEach>
                                                </ul>
                                        </li>
                                        </c:if>
                                        <c:if test="${menuitem.below == null}">
                                        <li><a href="<c:if test="${not fn:contains(menuitem.href,'http')}">${drupalBaseUrl}/</c:if>${menuitem.href}">${menuitem.title}</a></li>
                                        </c:if>
                                        </c:forEach>

						</ul>
            </nav>

            <div class="clear"></div>
        
        </div>
        

    </header>        




        <div id="main">
                <div class="breadcrumb">
                   <a href="${drupalBaseUrl}">Home</a> &raquo; <a href="${baseUrl}/search">Search</a><jsp:invoke fragment="breadcrumb" /><%-- breadcrumbs here --%>   
                </div>
        
                <div class="container">
                <jsp:doBody />
                </div>
                        
		        </div><!-- /main -->
        
        
    <footer id="footer">
    
        <div class="centercontent">
           <div class="region region-footer">
					   <div id="block-block-7" class="block block-block">
								<div class="content"><img src="${baseUrl}/img/footerLogos.jpg" />
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
                            <li>Version: <c:out value="${version}" escapeXml="false"/></li>
                            <li><a href="http://raw.github.com/mpi2/PhenotypeArchive/master/LICENSE">License</a></li>
                            <li><a href="http://raw.github.com/mpi2/PhenotypeArchive/master/CHANGES">Changelog</a></li>
                        </ul>
                    </div>
                    <div class="clear"></div>
                    
                    <p class="textright">&copy; 2014 IMPC &middot; International Mouse Phenotyping Consortium</p>
                    
                    <div id="fn">
                        <ul>
                            <li><a href="#">Imprint</a></li>
                            <li><a href="#">Legal notices</a></li>
                        </ul>
                    </div>
                    <div class="clear"></div>
                    
                </div>
                
                    
                <div class="clear"></div>
            
            </div>
        
        </div>
        
    </footer>
        
        
        <script type="text/javascript" src='${baseUrl}/js/vendor/jquery.ba-bbq.min.js' ></script>
        <script type="text/javascript" src='${baseUrl}/js/script.min.js' ></script>
        <script type='text/javascript' src='${baseUrl}/js/general/ui.dropdownchecklist_modif.js'></script>
        
        <compress:html enabled="${param.enabled != 'false'}" compressJavaScript="true">
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
        </compress:html>
        </div> <!-- wrapper -->
</body>
</html>