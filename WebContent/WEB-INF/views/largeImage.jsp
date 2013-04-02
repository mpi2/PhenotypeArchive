<!-- copy and paste this page into another for a jsp page with header and footer with navigation menus at top -->
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>


<t:genericpage>
<jsp:attribute name="title">Images IMPC</jsp:attribute>
    <jsp:attribute name="header">
<!--    extra header stuff goes here such as extra page specific javascript -->
		<!-- thumbnail scroller stylesheet -->
		<link href="../css/imagemain.css" rel="stylesheet" />
		<link href="../css/largeimage.css" rel="stylesheet" />
		<script src="../js/imaging/jquery-ui-1.8.13.custom.min.js"></script>
		<script src="../js/imaging/properties.js"></script>
		<script src="../js/imaging/largeimage.js"></script>
		<script src="../js/imaging/imagehelper.js"></script>
		<script src="../js/imaging/jquery.thumbnailScroller.js"></script>
		<script src="../js/imaging/imagedrawing.js"></script>

    </jsp:attribute>
    <jsp:attribute name="footer">
<!--      Anything extra to go in the footer goes here -->
    </jsp:attribute>
    <jsp:body>
<!--         main body content goes here -->

<div class="row-fluid dataset">
			<div class="span3">
<div id="pagination">
	</div>
		<!-- thumbnail scroller markup begin -->
		<div id="tS3" class="jThumbnailScroller" style="height:600px;">
			<div class="jTscrollerContainer">
				<div class="jTscroller">

				</div>
			</div>
			<!--<a href="#" class="jTscrollerPrevButton"></a>
			<a href="#" class="jTscrollerNextButton"></a>-->
		</div>
		
				
			</div>
			<div class="span7">
				
		<div id="largeImage">
	</div>
	<div id="largeImageCaption">
	Caption for large image here!
	</div>
		<canvas id="drawableCanvas" width="800" height="600">Fallback content, in case the browser does not support Canvas.</canvas>
		
		


			</div>
			<div class="span2">
			<div id="imageDataContainer"></div>
			</div>
			
		</div>

			
	
    </jsp:body>

	
</t:genericpage>

	
