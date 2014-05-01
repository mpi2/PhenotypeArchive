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

                           <h1>Release notes</h1>
                            
                            <h4>IMPC Data Portal 0.3.1 - 27 November 2013</h4>
                            <ul>
                                <li>Integration of IKMC project pages in the IMPC portal</li>
                                <li>Search by disease term: retrieve known gene associations and mouse models for diseases</li>
                                <li>Phenotype summary for every gene</li>
                                <li>High-level phenotype summaries (e.g. <a href="https://www.mousephenotype.org/data/phenotypes/MP:0005397">https://www.mousephenotype.org/data/phenotypes/MP:0005397</a>)</li>
                                <li>RESTful API to access experimental data and genotype to phenotype associations (<a href="https://github.com/mpi2/PhenotypeArchive/wiki">https://github.com/mpi2/PhenotypeArchive/wiki</a>)</li>
                                <li>Comprehensive online documentation</li>
                            </ul>

                        </div><%-- end of content div--%>
                    </div>
                </div>
            </div>
        </div>
   
    </jsp:body>
  
</t:genericpage>
