<%-- 
    Document   : experimentalObservationAPI
    Created on : Jun 11, 2014, 11:44:21 AM
    Author     : mrelac
--%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:genericpage>
	<jsp:attribute name="title">International Mouse Phenotyping Consortium Documentation</jsp:attribute>
	<jsp:attribute name="breadcrumb">&nbsp;&raquo; <a href="${baseUrl}/documentation/index">Documentation</a></jsp:attribute>
	<jsp:attribute name="bodyTag"><body  class="page-node searchpage one-sidebar sidebar-first small-header"></jsp:attribute>
	<jsp:attribute name="addToFooter">
<jsp:include page="doc-pinned-menu.jsp"></jsp:include>
	</jsp:attribute>
	

	<jsp:attribute name="header">
            <link type='text/css' rel='stylesheet' href='${baseUrl}/css/custom.css' />
        </jsp:attribute>

	<jsp:body>
		
        <div id="wrapper">

            <div id="main">
                <!-- Sidebar First -->
                <jsp:include page="doc-menu.jsp"></jsp:include>

                <!-- Maincontent -->

                <div class="region region-content">              

                    <div class="block block-system">

                        <div id="top" class="content node">
        
                            
                            <h1>Not implemented yet.</h1>
                            
                            
                        </div><%-- end of content div--%>
                    </div>
                </div>
            </div>
        </div>
   
    </jsp:body>
  
</t:genericpage>