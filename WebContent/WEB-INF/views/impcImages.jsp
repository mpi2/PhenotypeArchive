<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:genericpage>

	<jsp:attribute name="title">${queryTerms} IMPC Images Information</jsp:attribute>

	<jsp:attribute name="breadcrumb">&nbsp;&raquo; <a href="${baseUrl}/search#q=*:*&core=images&fq=annotationTermId:M* OR expName:* OR symbol:*">Images</a> &raquo; Results</jsp:attribute>
	
    <jsp:attribute name="header">
		<link href="${baseUrl}/css/imagemain.css" rel="stylesheet" />
		<link href="${baseUrl}/css/searchresults.css" rel="stylesheet" />
		<style>
		table th{border-bottom:1px solid #CDC8B1;}
		table tr:last-child th{border-bottom:none;}
		.thumbnail{margin-bottom:30px;}
		.thumbnail p{line-height:0.75em;}
		</style>

		<script src="${baseUrl}/js/vendor/jquery.autopager-1.0.0.js?v=${version}"></script>
		<script src="${baseUrl}/js/imaging/imageUtils.js?v=${version}"></script>
    </jsp:attribute>

	<jsp:attribute name="addToFooter">
		<script>$.autopager({link: '#next',content: '#grid'});</script>
		
		
	<div class="region region-pinned">
            
        <div id="flyingnavi" class="block">
            
            <a href="#top"><i class="fa fa-chevron-up" title="scroll to top"></i></a>
            
            <ul>
            	<c:if test="${imageCount ne 0}">
                	<li><a href="#top">Images</a></li>
                </c:if>
            </ul>
            
            <div class="clear"></div>
            
        </div>
        
    </div>
	</jsp:attribute>

    <jsp:body>
    
   Hello IMPC Images here!
    </jsp:body>	

</t:genericpage>

