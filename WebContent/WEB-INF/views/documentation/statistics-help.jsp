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

                            <h3>More information about the way IMPC uses statistics.</h3>

                            <h4>IMPC statistics</h4>
                            
                            <p>
                            High-throughput phenotyping generates large volumes of varied data including 
                            both categorical and continuous data. Operational and cost constraints can 
                            lead to a work-flow that precludes traditional analysis methods. Furthermore, 
                            for a high throughput environment, a robust automated statistical pipeline 
                            that alleviates manual intervention is required.</p>

                            <p>The IMPC uses a variety of statistical methods for making phenotype calls, including:</p>

                            <ul>
                                <li><a href="https://en.wikipedia.org/wiki/Fisher%27s_exact_test"><strong>Fisher's Exact test</strong></a> - for categorical data parameters</li>
                                <li><a href="https://en.wikipedia.org/wiki/Mixed_model"><strong>Mixed Model</strong></a> - for continuous data parameters which include random effects </li>
                                <li><a href="https://en.wikipedia.org/wiki/Linear_model"><strong>Linear Model</strong></a> - for continuous data parameters when random effects are not significant</li>
                                <li><a href="https://en.wikipedia.org/wiki/Wilcoxon_signed-rank_test"><strong>Mann-€“Whitney U Rank sum test</strong></a> - for continuous data parameters when conditions for mixed model are not appropriate</li>
                            </ul>
                            
                            <p>
                            The Mixed model and Fisher's Exact methods used have been formalized into an R package called 
                            <a href="http://bioconductor.org/packages/release/bioc/html/PhenStat.html">PhenStat</a>.  
                            See the <a href="http://bioconductor.org/packages/release/bioc/vignettes/PhenStat/inst/doc/PhenStatUsersGuide.pdf">complete PhenStat user's guide</a>.
                            </p>

                            <p>
                            All analysis frameworks output a statistical significance measure, 
                            an effect size measure, model diagnostics (when appropriate), 
                            and <a href="${baseUrl}/documentation/graph-help">graphical visualisation</a>.
                            </p>
                            
                            <p>
                            The PhenStat package provides statistical methods for 
                            the identification of abnormal phenotypes with an emphasize on high-throughput dataflows. 
                            The package contains:</p>
                            <ul>
	                            <li>dataset checks and cleaning in preparation for the analysis</li> 
	                            <li>2 statistical frameworks for phenodeviant identification
		                            <ul>
			                            <li>Fisher's Exact test for Categorical data</li>
			                            <li>Linear Mixed Model for continuous</li>
		                            </ul>
	                            </li>
	                            <li> and additional functions that help to decide the correct method for analysis.</li>
                            </ul>
                            
                            <p>
                            Mixed Models framework assumes that base line values of 
                            dependent variable are normally distributed but batch (assay date) 
                            adds noise and models variables accordingly in 
                            order to separate the batch and the genotype. Model 
                            optimisation starting with:</p>

                            <blockquote>
                            	<strong><i>Y = Genotype + Sex + Genotypeâˆ—Sex + Weight + (1|Batch)</i></strong>
                            	<p><small>
                            	Weight is an optional parameter.<br />
                            	Genotypeâˆ—Sex is sometimes called the "interaction term" in PhenStat.<br />
                            	Assume batch is normally distributed with defined variance.
                            	</small></p>
                            </blockquote>
                            
                            <p>
                            The Mixed Model framework is an iterative process to select the 
                            best model for the data which considers both the best modelling 
                            approach (mixed model or general linear regression) 
                            and which factors to include in the model.
                            </p>

                            <p>
                            If PhenStat assumptions about the data are not met, a second attempt at analyzing the data
                            will be attempted -- a Mann-Whitney U Rank Sum test.
                            </p>
                            


                        </div><%-- end of content div--%>
                    </div>
                </div>
            </div>
        </div>
   
    </jsp:body>
  
</t:genericpage>
