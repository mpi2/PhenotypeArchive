<%@ page language="java" contentType="text/html; charset=US-ASCII"
    pageEncoding="US-ASCII"%>
    <%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
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
body here ${baseUrl}
<form action="../../imageComparator" method="post">
<select name="selectedImages" multiple size="2" class="image-picker show-html">
  <!-- <option data-img-src="http://wwwdev.ebi.ac.uk/mi/media/omero/webgateway/render_thumbnail/5812/200" value="1">Cute Kitten 1</option>
  <option data-img-src="http://wwwdev.ebi.ac.uk/mi/media/omero/webgateway/render_thumbnail/4739/200" value="2">Cute Kitten 2</option>
  <option data-img-src="http://placekitten.com/130/200" value="3">Cute Kitten 3</option>
  <option data-img-src="http://placekitten.com/270/200" value="4">Cute Kitten 4</option> -->
   <c:if test="${not empty controls}">
                                        
                                           
                                                   <%--  ${entry.name} (${entry.count}) --%>
          
                                                
                                                        <c:forEach var="doc" items="${controls}">
                                                            
                                                                <%-- <t:impcimgdisplay2 img="${doc}" impcMediaBaseUrl="${impcMediaBaseUrl}"></t:impcimgdisplay2> --%>
                                                                <option data-img-src="${impcMediaBaseUrl}/render_thumbnail/${doc.omero_id}/200" value="${doc.omero_id}">${doc.biological_sample_group}</option>
                                                        </c:forEach>
                                                    
                                      

                                  
  </c:if>			
  <c:if test="${not empty experimental}">
                                        
                                           
                                                   <%--  ${entry.name} (${entry.count}) --%>
          
                                                
                                                        <c:forEach var="doc" items="${experimental}">
                                                            
                                                                <%-- <t:impcimgdisplay2 img="${doc}" impcMediaBaseUrl="${impcMediaBaseUrl}"></t:impcimgdisplay2> --%>
                                                                <option data-img-src="${impcMediaBaseUrl}/render_thumbnail/${doc.omero_id}/200" value="${doc.omero_id}">${doc.biological_sample_group}</option>
                                                        </c:forEach>
                                                    
                                      

                                  
  </c:if>			
</select>
<input type="submit" value="submit">
</form>

 
</body>

</html>