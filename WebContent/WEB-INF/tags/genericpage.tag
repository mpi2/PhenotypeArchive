<%@ tag description="Overall Page template" pageEncoding="UTF-8" 
        import="java.util.Properties,uk.ac.ebi.phenotype.web.util.DrupalHttpProxy,net.sf.json.JSONArray,java.net.URLEncoder"
        %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://htmlcompressor.googlecode.com/taglib/compressor" prefix="compress" %>

<%
    /*
     Get the menu JSON array from drupal, fallsback to a default menu when drupal
     cannot be contacted
     */
    DrupalHttpProxy proxy = new DrupalHttpProxy(request);
    String url = (String) request.getAttribute("drupalBaseUrl");

    String content = proxy.getDrupalMenu(url);
    String[] menus = content.split("MAIN\\*MENU\\*BELOW");

    String baseUrl = (request.getAttribute("baseUrl") != null &&  ! ((String) request.getAttribute("baseUrl")).isEmpty()) ? (String) request.getAttribute("baseUrl") : (String) application.getInitParameter("baseUrl");
    jspContext.setAttribute("baseUrl", baseUrl);


    /*String pageUrl = request.getRequestURL().toString();
     System.out.println("pageUrl = " + pageUrl);
    
     String pageParams = request.getQueryString();
     System.out.println("pageParams = " + pageParams);*/
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
    jspContext.setAttribute("menu", menus[1]);
%>
<%@attribute name="header" fragment="true"%>
<%@attribute name="footer" fragment="true"%>
<%@attribute name="title" fragment="true"%>
<%@attribute name="breadcrumb" fragment="true"%>
<%@attribute name="bodyTag" fragment="true"%>
<%@attribute name="addToFooter" fragment="true"%>

<% // the baseUrl variable is set from the DeploymentInterceptor class %>

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


        <link href="${baseUrl}/css/default.css" rel="stylesheet" type="text/css" />
        <link href="${baseUrl}/css/wdm.css" rel="stylesheet" type="text/css" />

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
                            <ul>${usermenu}</ul>
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
                                        <li><a href="${drupalBaseUrl}/data/release">Release: <c:out value="3.0" escapeXml="false" /></a></li>
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

        <script type='text/javascript' src='${baseUrl}/js/utils/tools.js?v=${version}'></script>                 
        <script type='text/javascript' src='${baseUrl}/js/general/ui.dropdownchecklist_modif.js?v=${version}'></script>     	    
        <script type='text/javascript' src='${baseUrl}/js/documentationConfig.js?v=${version}'></script>

        <script type='text/javascript'>
$(document).ready(function () {
    // assign the url to feedback link dynamically
    // this won't work with hashtag change which is taken care of in search.jsp
    $('a.feedback_simple').attr('href', '/website-feedback?page=' + document.URL);
});
        </script>  

    </div> <!-- wrapper -->
</body>

