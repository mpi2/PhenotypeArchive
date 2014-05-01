<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:genericpage>
	<jsp:attribute name="title">International Mouse Phenotyping Consortium Documentation</jsp:attribute>
	<jsp:attribute name="breadcrumb">&nbsp;&raquo; <a href="${baseUrl}/search?q=*:*&core=gene">Genes</a> &raquo; ${gene.symbol}</jsp:attribute>
	<jsp:attribute name="bodyTag"><body  class="page-node searchpage one-sidebar sidebar-first small-header"></jsp:attribute>
	<jsp:attribute name="addToFooter">

	</jsp:attribute>
	

	<jsp:attribute name="header">
		
        
        </jsp:attribute>

	<jsp:body>
		
        <div id="wrapper">

            <div id="main">

                <!-- Breadcrumb -->
                <div class="breadcrumb"><a href="/">Home</a> ? Documentation</div>

                <!-- Sidebar First -->
                <jsp:include page="doc-menu.jsp"></jsp:include>

                <!-- Maincontent -->

                <div class="region region-content">              

                    <div class="block block-system">

                        <div class="content node">

                           <h3>More information about the IMPC RESTful API.</h3>

                            <h4><a name="genetype-phenotype" href='#'>Genotype-Phenotype API</a></h4>
                            <p>Please see the comprehensive <a href="https://github.com/mpi2/PhenotypeArchive/wiki/Genotype-associated-Phenotype-calls-REST-API">Genotype-Phenotype API documentation</a> </p>

                            <h4><a name="experiment" href='#'>Experimental observation API</a></h4>
                            <p>Please see the comprehensive <a href="https://github.com/mpi2/PhenotypeArchive/wiki/Experimental-data-REST-API">Experimental observation API documentation</a> </p>


                        </div><%-- end of content div--%>
                    </div>
                </div>
            </div>
        </div>
   
    </jsp:body>
  
</t:genericpage>
