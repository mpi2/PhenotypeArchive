<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Identifier Error</title>
	<link type='text/css' rel='stylesheet' href='http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.18/themes/base/jquery-ui.css' />
	<link type='text/css' rel='stylesheet' href='${phenotypeArchiveBaseUrl}/css/searchAndFacet.css' />	
	<link type='text/css' rel='stylesheet' href='${phenotypeArchiveBaseUrl}/css/message.css' media='all' />
	<link type='text/css' rel='stylesheet' href='${phenotypeArchiveBaseUrl}/css/bootstrap.min.css' media='all' />
</head>

<body>

   <div id="wrap">

      <!-- Begin page content -->
      <div class="container">
        <div class="page-header">
          <h1>Oops! ${acc} is not a valid ${type} identifier.</h1>
        </div>
        <p>Example of a valid page:</p>
        	<ul>
        	<li><a href="${phenotypeArchiveBaseUrl}${exampleURI}">${phenotypeArchiveBaseUrl}${exampleURI}</a></li>
        	</ul>
      </div>
      
      <div class="row"><img src="${phenotypeArchiveBaseUrl}/img/impc.jpg"></div>

      <div id="push"></div>
      
    </div>

    <div id="footer">
      <div class="container">
        <p class="muted credit"><a href="${drupalBaseUrl}">IMPC Home page</a></p>
      </div>
    </div>

	<script type='text/javascript' src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"></script>
	<script type='text/javascript' src='http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.18/jquery-ui.min.js'></script>
    <script type='text/javascript' src='${phenotypeArchiveBaseUrl}/js/bootstrap/bootstrap.min.js'></script>
	<script type='text/javascript' src='${phenotypeArchiveBaseUrl}/js/searchAndFacet/autocompleteWidget.js'></script>
	<script type='text/javascript' src='${phenotypeArchiveBaseUrl}/js/searchAndFacet/sideBarFacetWidget.js'></script>  
	<script type='text/javascript' src='${phenotypeArchiveBaseUrl}/js/searchAndFacet/searchAndFacet_primer.js'></script>
	<script type='text/javascript' src='${phenotypeArchiveBaseUrl}/js/vendor/respond.min.js'></script>
</body>
</html>