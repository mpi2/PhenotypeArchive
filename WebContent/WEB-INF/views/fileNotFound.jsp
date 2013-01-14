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

	<div class="row-fluid errBox">
    	<div class="span12">
    		<div class="span4 container">
    		<!--left pane content-->
    		<img src='${baseUrl}/img/logo.png' />
    		
    		</div>
    		<div class="span8 container">
    			<!--right pane content-->
    			<h3>Sorry, the page you intended to view does not exist....</h3>
    			<ul><h5>This could be due to, eg.</h5>
    				<li>A mis-typed address</li>
    				<li>An out-of-date bookmark</li>
    				<li>The page no longer exists or being removed</li>
    			</ul>
    			
    		</div>
    	</div>
    </div>
    <div>Try searching for the page here:</div>
    <div class="row-fluid errBox">
    	<div class="span12">	
    		<span class="nav input-append" style="padding: 0 15px 5px 15px;">
				<input id="userInput" type="text" class="span6" value="Search genes, SOP by MGI ID, gene symbol, synonym or name">
				<button id="acSearch" type="submit" class="btn"><i class="icon-search"></i> Search</button>
				<div id="bannerSearch"></div>
			</span>
		</div>
	</div>	
    <p>OR</p>
    <div class="row-fluid errBox">
    	<div class="span12"><span id='contactTrigger'>Contact Us</span> for investigation</div>
    	<div class="span12" id='contactForm'></div>   	
    </div>
    
    <div class="row-fluid footer">
		<div class="span12">
			<img alt="" class="footerLogos"
				src="http://beta.mousephenotype.org/sites/all/themes/impc_zen/images/footerLogos.jpg"
				style="width: 1222px; height: 50px;" />			
		</div>
	</div>
	
	<script type='text/javascript' src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"></script>
	<script type='text/javascript' src='http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.18/jquery-ui.min.js'></script>
    <script type='text/javascript' src='${baseUrl}/js/bootstrap/bootstrap.min.js'></script>
	<script type='text/javascript' src='${baseUrl}/js/searchAndFacet/autocompleteWidget.js'></script>
	<script type='text/javascript' src='${baseUrl}/js/searchAndFacet/sideBarFacetWidget.js'></script>  
	<script type='text/javascript' src='${baseUrl}/js/searchAndFacet/searchAndFacet_primer.js'></script>
	<script type='text/javascript' src='${baseUrl}/js/css3-mediaqueries.js'></script>
</body>
</html>