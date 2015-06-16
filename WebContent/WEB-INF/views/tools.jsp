<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:genericpage>
    <jsp:attribute name="title">IMPC Tools</jsp:attribute>
    <jsp:attribute name="breadcrumb">&nbsp;&raquo;<a href="${baseUrl}/batchQuery">&nbsp;Tools</a></jsp:attribute>
    <jsp:attribute name="header">
        <link rel="stylesheet" href="//code.jquery.com/ui/1.11.4/themes/smoothness/jquery-ui.css">
        <link href="${baseUrl}/js/vendor/jquery/jquery.qtip-2.2/jquery.qtip.min.css" rel="stylesheet" />
        <link href="${baseUrl}/css/searchPage.css" rel="stylesheet" />
        
        <style type="text/css">
			.toolName {
				width: auto;
				padding: 5px;
				color: gray;
				border: 1px solid gray;
				border-radius: 5px;
			}
			.toolLink {
				margin-top: 5px;
			}
        </style>
        
        <script type='text/javascript' src='${baseUrl}/js/utils/tools.js'></script>  
        
        <script type='text/javascript'>
        
            $(document).ready(function () {
                'use strict';
             	// test only
                //var baseUrl = '//dev.mousephenotype.org/data';
                //var baseUrl = 'http://localhost:8080/phenotype-archive';
                
                var baseUrl = "${baseUrl}";
                var solrUrl = "${solrUrl}";
                
              	
            });
           
        </script>
       
    </jsp:attribute>

    <jsp:attribute name="addToFooter">	
        <div class="region region-pinned">

        </div>		

    </jsp:attribute>

    <jsp:body>		
		<div class="region region-content">
			<div class="block block-system">
				<div class="content">
					<div class="node node-gene">
						<h1 class="title" id="top">IMPC Tools</h1>
							 
						<div class="textright">
							<a id="bqdoc" class="">Help</a>						
						</div>	
						
						<div class="section">
							<!--  <h2 id="section-gostats" class="title ">IMPC Dataset Batch Query</h2>-->
							<div class='inner' id='toolBlock'>
								
								${tools}
								
							</div>
							
						</div><!-- end of section -->
						
					</div><!-- end of node div -->
				</div><!-- end of content div -->
			</div>
		</div>  

    </jsp:body>
</t:genericpage>

