<%@ page language="java" contentType="text/html; charset=US-ASCII"
    pageEncoding="US-ASCII"%>
    <%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<t:genericpage>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
<title>Image Picker</title>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
 <!--  <link rel="stylesheet" type="text/css" href="css/bootstrap.css">
  <link rel="stylesheet" type="text/css" href="css/bootstrap-responsive.css">
  <link rel="stylesheet" type="text/css" href="examples.css">
  <link rel="stylesheet" type="text/css" href="image-picker/image-picker.css"> -->
  
  <link href="${baseUrl}/css/default.css" rel="stylesheet" type="text/css" />
 <link rel="stylesheet" href="${baseUrl}/css/vendor/jquery.ui/jquery.ui.core.css">
<link rel="stylesheet" href="${baseUrl}/css/vendor/jquery.ui/jquery.ui.slider.css">

<script src="//ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
<script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.10.3/jquery-ui.min.js"></script>

 <!--  <script src="js/prettify.js" type="text/javascript"></script>
  <script src="js/jquery.masonry.min.js" type="text/javascript"></script>
  <script src="js/show_html.js" type="text/javascript"></script> -->
  <link rel="stylesheet" href="${baseUrl}/js/vendor/image-picker/image-picker.css">
  <script src="${baseUrl}/js/vendor/image-picker/image-picker.js" type="text/javascript"></script>
 <script src="${baseUrl}/js/imaging/imagesInteraction.js"></script>
<!-- http://rvera.github.io/image-picker/ -->
</head>
<body>


<div class="region region-content">
	<div class="block">
    	<div class="content">
        	<div class="node">
                           
        	<form action="../../imageComparator" method="get">
        	 <h1 class="title" id="top">Controls</h1>
				<div class=section">
					<div class="inner">
						<select name="ctrImgId" multiple size="2" class="image-picker show-html">
  							<c:if test="${not empty controls}">
                            	<c:forEach var="img" items="${controls}">
                                <%-- <t:impcimgdisplay2 img="${doc}" impcMediaBaseUrl="${impcMediaBaseUrl}"></t:impcimgdisplay2> --%>
                                	<option data-img-src="${impcMediaBaseUrl}/render_thumbnail/${img.omero_id}/200" value="${img.omero_id}">
   										<c:if test="${not empty img.sex}">${img.sex}</c:if>
   										 <c:if test="${not empty count}">${count} Images<br/></c:if>
                                                <c:if test="${not empty img.parameter_association_name}">
                                                	<c:forEach var="pAssName" items="${img.parameter_association_name}" varStatus="status">${pAssName}, </c:forEach>
                                                </c:if>
                                                <c:if test="${not empty img.biological_sample_group}">${img.biological_sample_group}</c:if>
                                                <c:if test="${not empty img.allele_symbol}"><t:formatAllele>${img.allele_symbol}</t:formatAllele><br/></c:if>
   										<%-- <c:if test="${not empty img.date_of_experiment}">${img.date_of_experiment}</c:if> --%>
   										<%-- <c:if test="${not empty img.biological_sample_group}">${img.biological_sample_group}</c:if> --%>
   									</option>
                             	</c:forEach>
  							</c:if>	
  						</select>
  					</div>
  				</div>
  				<h1 class="title" id="top">Experimental</h1>
  				<div class="section">
  					<div class="inner">
  						<select name="expImgId" multiple size="2" class="image-picker show-html">		
  						<c:if test="${not empty experimental}">
                            <c:forEach var="img" items="${experimental}">
                                <option data-img-src="${impcMediaBaseUrl}/render_thumbnail/${img.omero_id}/200" value="${img.omero_id}">
                                    <c:if test="${not empty img.zygosity}">${img.zygosity}</c:if>
   									<c:if test="${not empty img.sex}">${img.sex}</c:if>
   									<%-- <c:if test="${not empty img.date_of_experiment}">${img.date_of_experiment}</c:if> --%>
   									<%-- <c:if test="${not empty img.biological_sample_group}">${img.biological_sample_group}</c:if> --%>
   								</option>
                            </c:forEach>
						</c:if>			
						</select>
					</div>
				</div>
				<input type="submit" value="Click to display selected images">
			</form>
		
	</div>
 </div>
</div>
</div>
</body>

</html>
</t:genericpage>