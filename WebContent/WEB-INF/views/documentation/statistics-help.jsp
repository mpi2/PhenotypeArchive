<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:genericpage>
	<jsp:attribute name="title">International Mouse Phenotyping Consortium Statistics Documentation</jsp:attribute>
	<jsp:attribute name="breadcrumb">&nbsp;&raquo; <a href="${baseUrl}/documentation/index">Documentation</a></jsp:attribute>
	<jsp:attribute name="bodyTag"><body class="page-node searchpage one-sidebar sidebar-first small-header"></jsp:attribute>

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
                                <li><a href="https://en.wikipedia.org/wiki/Mann%E2%80%93Whitney_U"><strong>Mann-Whitney U Rank sum test</strong></a> - for continuous data parameters when conditions for mixed model are not appropriate</li>
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
                            	<strong><i>Y = Genotype + Sex + Genotype&#42;Sex + (1|Batch)</i></strong>
                            	<p><small>
                            	Genotype∗Sex is sometimes called the "interaction term" in PhenStat.<br />
                            	Assume batch is normally distributed with defined variance.<br /><br/>
                            	* - The MM encoded in PhenStat supports an optional "weight" term
                            	</small></p>
                            </blockquote>
                            
                            <p>
                            The Mixed Model framework is an iterative process to select the 
                            best model for the data which considers both the best modelling 
                            approach (mixed model or general linear regression) 
                            and which factors to include in the model.
                            </p>

                            <p>
                            If PhenStat assumptions about the input data are not met, a second attempt at analyzing the data
                            will be attempted -- a Mann-Whitney U Rank Sum test.
                            </p>
                            
                            <h4>Control selection strategy</h4>
                            
                            <p>One side effect of producing data in a high throughput pipeline is that the input data for a statistical calculation might be produced over multiple days.
                            Environmental fluctuations have been identified as a confounding factor when comparing 
                            data gathered on different days.  The IMPC describes this as a "batch effect" and it is treated as a random effect in the Mixed Model framework.
                            </p>
                            
                            <p>The data sets to be analysed are identified using unique combinations of these fields</p>
                            
                            <table>
                            	<thead>
                            		<tr>
                            			<th>Field</th>
                            			<th>Description</th>
                            		</tr>
                            	</thead>
                            	<tbody>
	                            	<tr>
		                            	<td>Background strain</td>
		                            	<td>The original strain from which the mutant specimen was derived.</td>
	                            	</tr>
	                            	<tr>
		                            	<td>Allele / Colony</td>
		                            	<td>The genomic variation in the mutant.  The allele describes the character of the mutation and the <b>Genotype effect</b> term of the mixed model.</td>
	                            	</tr>
	                            	<tr>
		                            	<td>Zygosity</td>
		                            	<td>The severity of the mutation.
		                            	<dl>
		                            	<dt>WT (wildtype or +/+)</dt><dd>The wild type allele.</dd>
		                            	<dt>Heterozygous (het)</dt><dd>The mutation occurred in one copy of the allele.</dd>
		                            	<dt>Homozygous (hom)</dt><dd>The mutation occurred in both copies of the allele.</dd>
		                            	<dt>Hemizygous (hemi)</dt><dd>The mutation occurred in a sex linked allele where, normally, only a single copy exists in the WT.</dd>  
		                            	</dl></td>
	                            	</tr>
	                            	<tr>
		                            	<td>Pipeline</td>
		                            	<td>The standardized phenotyping pipeline as described in <a href="https://www.mousephenotype.org/impress/pipelines">IMPReSS Pipelines</a></td>
	                            	</tr>
	                            	<tr>
		                            	<td>Procedure</td>
		                            	<td>The standardized set of procedures (experiments) as described in <a href="https://www.mousephenotype.org/procedures-impc-pipeline">IMPReSS procedures</a></td>
	                            	</tr>
	                            	<tr>
		                            	<td>Parameter</td>
		                            	<td>The standardized set of measurements as described in <a href="https://www.mousephenotype.org/parameters">IMPReSS parameters</a></td>
	                            	</tr>
	                            	<tr>
		                            	<td>Metadata group</td>
		                            	<td>Some parameters are indicated as "procedureMetadata" type.  Some of these metadata are used to group comparable data together as described on the <a href="https://www.mousephenotype.org/parameters">IMPReSS parameters</a> page under the "Required For Data Analysis" section. The parameters that are marked as "Required For Data Analysis" are collectively identified by an identifier called the metadata group.</td>
	                            	</tr>
	                            	<tr>
		                            	<td>Organisation</td>
		                            	<td>The phenotyping organisation that performed the experiment and collected the data.</td>
	                            	</tr>
	                            	<tr>
		                            	<td>Sex *</td>
		                            	<td>The sex of the specimens. When analyzed using the <b>mixed model</b> males and females are analysed together to determine the <b>Sex</b> and <b>Sex*Genotype interaction</b> effect terms.<br /><small>* - optional</small></td>
	                            	</tr>
                            	</tbody>
								<caption>IMPC data aggregation fields.</caption>
                           	</table> 
                            
							<p>IMPC phenotyping centers operate using different work flows which contribute to the batch effect.</p>
                            
                            <table>
                            <thead>
                            	<tr>
                            		<th>Workflow</th>
                            		<th>Description</th>
                            		<th>Statistical implications</th>
                            		<th>Control selection strategy</th>
                            	</tr>
                            </thead>
                            <tbody>
                            	<tr>
                            		<td>One batch</td>
                            		<td>All mutant and control data are measured on one day.</td>
                            		<td>No batch effect.  The controls and mutants are analysed using <strong><i>Y = Genotype + Sex + Genotype&#42;Sex</i></strong></td> 
                            		<td>Concurrent control strategy -- Use control data that are collected on the same day as the mutant data.</td> 
                            	</tr>
                            	<tr>
                            		<td>Multi-batch batch (2+)</td>
                            		<td>Mutant and control data are gathered over a few days.</td>
                            		<td>Possible batch effect.  The controls and mutants are analysed using <br /><strong><i>Y = Genotype + Sex + Genotype&#42;Sex + (1|Batch)</i></strong>, the batch effect might be removed.</td> 
                            		<td>Baseline control strategy -- Use all control data within the same .</td> 
                            	</tr>
                            </tbody>
                            <caption>Example work flows at IMPC centers.</caption>
                            </table>
                            
                            
                            
                            <p>For each data set, the appropriate work flow is determined and the statistical calculation is performed.
                            For continuous data, Mixed model is the IMPC preferred method of analysis, however, this method requires that the following assumptions are met:
                            </p>

                            <ul>
                            <li>1. The data is normally distributed</li> 
                            <li>2. The data has some variation</li>
                            <li>3. There must be more than four data points per sex per genotype</li>
                            </ul>
                            
                            <p>The graph pages display plots according to the data type of the parameter.  Categorical data parameters display a stacked bar chart 
                            whereas continuous data displays a box plot and a scatter plot of the data point values.  See the <a href="${baseUrl}/documentation/graph-help">graph documentation</a> for more details.   
                            </p> 


                            <h4>Fisher's exact output</h4>
                            <p>A table displaying more information about the data used to determine the pvalue and effect size is displayed below the graph.</p> 
                            <img src="img/stats_fe_table.png">
                            
                            <h4>Mixed model (PhenStat) output</h4>
                            <p>A table displaying more information about the data used to determine the pvalue and effect size is displayed below the graph.</p> 
                            <img src="img/stats_mm_table.png">

                            <h4>Rank sum output</h4>
                            <p>A table displaying more information about the data used to determine the pvalue and effect size is displayed below the graph.</p> 
                            <img src="img/stats_ranksum_table.png">
                            


                        </div><%-- end of content div--%>
                    </div>
                </div>
            </div>
        </div>
   
    </jsp:body>
  
</t:genericpage>
