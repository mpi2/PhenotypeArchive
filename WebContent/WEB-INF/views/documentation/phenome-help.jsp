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
                            
                            <h1>Phenome Overview Documentation</h1>
		
                            <p>The phenome page summarizes all the significant Mammalian Phenotype (MP) statistical calls for one phenotyping center.
                            This information can be visualized in a graph and is also available in a table below the graph. Links to the phenome page are provided from the 
                            gene page below the phenotype summary. For instance, the screenshot below indicates 'Browse all phenotype data for' the a tm1a allele with a link to <strong>MP calls for all strains</strong>. Clicking on this link will request the phenome view.</p>
                            
                            <img src="img/phenome-hyperlink.png" />
                            
                            <h3><a name="graph" href='#'>Phenome Graph</a></h3>
                            <p>The graph section of the page shows a summary of all significant genotype to phenotype associations for
                            mutant lines phenotyped by a specific mouse genetics clinic. The information is organized by top-level phenotype categories:
                            <ul>
                                <li>immune system</li>
                                <li>hematopoietic system</li>
                                <li>homeostasis/metabolism</li>
                                <li>growth/size/body</li>
                                <li>skeleton</li>
                                <li>hearing/vestibular/ear</li>
                                <li>adipose tissue</li>
                                <li>cardiovascular system</li>
                                <li>vision/eye</li>
                                <li>behavior/neurological</li>
                                <li>pigmentation</li>  
                            </ul>
                            The x-axis indicates each MP term from each top-level category. The y-axis is a log transformation of the original p-value for better interpretation. 
                            Each data point corresponds to a significant hit for a specific mutant line.
                            <img src="img/phenome-graph.png" />

                            <h3><a name="graph-details" href='#'>Graph details</a></h3>
                            <p>A click on each data point will open a pop-up window that will display the details of the underlying data in a graph.
                            </p>

                            <img src="img/phenome-graph-details.png" />

                            <h3><a name="phenotype-table" href='#'>Phenotype table</a></h3>

                            <p>The detailed phenotype section of the phenome page shows all the association of genes to <a href="http://www.informatics.jax.org/searches/MP_form.shtml">Mammalian phenotype</a> terms for a specific phenotyping center.
                            The table mirrors the graph view and displays the following information in sortable columns:</p>
                            <ul>
							<li>Gene / Allele</li>
							<li>Procedure</li>
							<li>Parameter</li>
							<li>Zygosity</li>
							<li>Phenotype</li>
							<li>P-value</li>
							<li>Graph</li>
                            </ul>
                            
                            <img src="img/phenome-table.png" />
                            
                        </div><%-- end of content div--%>
                    </div>
                </div>
            </div>
        </div>
   
    </jsp:body>
  
</t:genericpage>
