<%@ page language="java" contentType="text/html; charset=US-ASCII"
    pageEncoding="US-ASCII"%>
    <%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<HTML>
<HEAD>
<TITLE>A document with anchors with specific targets</TITLE>
<script src="//ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
<script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.10.3/jquery-ui.min.js"></script>
<script src="${baseUrl}/js/imaging/imageComparator.js"></script>
<script type="text/javascript">
var baseUrl="${baseUrl}";
var solrUrl='${solrUrl}';
var drupalBaseUrl = "${drupalBaseUrl}";
var mediaBaseUrl = "${mediaBaseUrl}";
var impcMediaBaseUrl="${impcMediaBaseUrl}";
</script>
<link href="${baseUrl}/css/default.css" rel="stylesheet" type="text/css" />
</HEAD>
<BODY>
<div class="region region-content">
                <div class="block">
                    <div class="content">
                    	<div class="section">
                               
    								<div class="caption">
    										<div class="btn" id="prev">< < Previous</div>
    										<div class="btn" id="next">Next > ></div>
    											<div id="annotations" name="annotations">
    											</div>
    								</div>
								
						</div>
					</div>
				</div>
</div>
</BODY>
</HTML>