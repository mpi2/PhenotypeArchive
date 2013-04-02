<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
 <%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>File Not Found</title>
	<link type='text/css' rel='stylesheet' href='http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.18/themes/base/jquery-ui.css' />
	<link type='text/css' rel='stylesheet' href='${baseUrl}/css/searchAndFacet.css' />	
	<link type='text/css' rel='stylesheet' href='${baseUrl}/css/message.css' media='all' />
	<link type='text/css' rel='stylesheet' href='${baseUrl}/css/bootstrap.min.css' media='all' />
</head>

<body>

   <div id="wrap">

      <!-- Begin page content -->
      <div class="container">
        <div class="page-header">
          <h1>Oops! This link appears to be broken</h1>
        </div>
        <p class="lead">This could be due to, eg.</p>
        <ul>
    		<li>A mis-typed address</li>
    		<li>An out-of-date bookmark</li>
    		<li>The page no longer exists or being removed</li>        
        </ul>
      </div>
      
      <div class="row"><img src="${baseUrl}/img/impc.jpg"></div>

      <div id="push"></div>
      
    </div>

    <div id="footer">
      <div class="container">
        <p class="muted credit"><a href="${drupalBaseUrl}">IMPC Home page</a>.</p>
      </div>
    </div>

	<script type='text/javascript' src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"></script>
	<script type='text/javascript' src='http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.18/jquery-ui.min.js'></script>
    <script type='text/javascript' src='${baseUrl}/js/bootstrap/bootstrap.min.js'></script>
	<script type='text/javascript' src='${baseUrl}/js/searchAndFacet/autocompleteWidget.js'></script>
	<script type='text/javascript' src='${baseUrl}/js/searchAndFacet/sideBarFacetWidget.js'></script>  
	<script type='text/javascript' src='${baseUrl}/js/searchAndFacet/searchAndFacet_primer.js'></script>
	<script type='text/javascript' src='${baseUrl}/js/vendor/respond.min.js'></script>
</body>
</html>