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

                            <h1>IMPC data portal documentation</h1>
                            <p>Explore how to retrieve mouse phenotype data</p>

                            <h4>The IMPC portal offers a powerful search interface to query information on 
                            mouse production and mouse phenotyping.</h4>
                            <ul>
                                <li><a href="search-help.html">How to find the  data you want</a>
                                </li>
                            </ul>

                            <h4>What are some of the the things I can do at the data portal</h4>
                            <ul>
                                <li><a href="gene-help.html">Find gene to phenotype associations, allele maps and more</a></li>
                                <li><a href="phenotype-help.html">Find a list of genes associated with a phenotype, 
                                        assays used to measure the phenotype and more</a></li>
                                <li><a href="disease-help.html">Find genes associated with rare diseases</a></li>				
                                <li><a href="image-help.html">Retrieve LacZ and phenotype images</a></li>
                                <li><a href="graph-help.html">Learn how we visualize phenotype data</a></li>
                                <li><a href="statistics-help.html">Learn about the statistical tests used to 
                                        determine gene to phenotype associations</a></li>
                            </ul>

                            <h4>IMPC Quick Reference Card</h4>
                            <ul>
                                <li><a href="pdf/IMPC-refCard.pdf"><i class="fa fa-download"></i> pdf</a>
                                </li>
                            </ul>

                            <h4>Release notes</h4>
                            <ul>
                                <li><a href="release-notes.html">List of new features and changes to this release</a>
                                </li>
                            </ul>

                        </div>
                    </div>
                </div>
            </div>
        </div>
   
    </jsp:body>
  
</t:genericpage>
