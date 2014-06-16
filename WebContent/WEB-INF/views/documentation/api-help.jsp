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

                           <h3>More information about the IMPC RESTful API.</h3>
                           
                           <p>The IMPC offers the following RESTful APIs for consuming data:

                            <h4><a name="genetype-phenotype" href='#'>Genotype-Phenotype API</a></h4>
                            <p>Please see the <a href="genotype-phenotype-api-help">Genotype-Phenotype API documentation</a> </p>

                            <h4><a name="experiment" href='#'>Experimental observation API</a></h4>
                            <p>Please see the <a href="experimental-api-help">Experimental observation API documentation</a> </p>

                            <h4><a name="experiment" href='#'>Statistical results API</a></h4>
                            <p>Please see the <a href="statistical-results-api-help">Statistical results API documentation</a> </p>

                        </div><%-- end of content div--%>
                    </div>
                </div>
            </div>
        </div>
   
    </jsp:body>
  
</t:genericpage>
