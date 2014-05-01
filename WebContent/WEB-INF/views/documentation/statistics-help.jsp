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

                            <h3>More information about the way IMPC uses statistics.</h3>

                            <h4>IMPC statistics</h4>

                            <p>The IMPC uses a variety of statistical methods for making phenotype calls, including:</p>

                            <ul>
                                <li><a href="https://en.wikipedia.org/wiki/Fisher%27s_exact_test"><strong>Fisher's Exact test</strong></a> - for categorical data parameters</li>
                                <li><a href="https://en.wikipedia.org/wiki/Mixed_model"><strong>Mixed Model</strong></a> - for continuous data parameters which include random effects </li>
                                <li><a href="https://en.wikipedia.org/wiki/Linear_model"><strong>Linear Model</strong></a> - for continuous data parameters when mixed model doesn't apply</li>
                            </ul>
                            <p> Further documentation will be available as part of an upcoming statistical Bioconducter package </p>

                            <h4>Europhenome statistics</h4>

                            <p>Europhenome data is analyzed using a variety of statistical methods for making phenotype calls, including:</p>

                            <ul>
                                <li><a href="http://europhenome.org/databrowser/significanceCatagorical.jsp"><strong>Pearson's chi-square test</strong></a> - for categorical data parameters</li>
                                <li><a href="http://europhenome.org/databrowser/significanceReference.jsp"><strong>Reference Range</strong></a> - for continuous data parameters</li>
                                <li><a href="http://europhenome.org/databrowser/significance1DT.jsp"><strong>Student's T test</strong></a> - for continuous data parameters </li>
                                <li><a href="http://europhenome.org/databrowser/significance1D.jsp"><strong>Rank sum (Mann-Whitney U test)</strong></a> - for continuous data parameters</li>
                            </ul>
                           

                        </div><%-- end of content div--%>
                    </div>
                </div>
            </div>
        </div>
   
    </jsp:body>
  
</t:genericpage>
