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
                            
                            <h1>Gene Page Documentation</h1>
		
                            
                            <h3><a name="details" href='#'>Gene Details</a></h3>
                            <p>The gene details section of the page shows information about the gene.  The information displayed includes:
                            <ul>
                                <li>Name</li>
                                <li>Synonyms</li>
                                <li>MGI ID which links to the corresponding gene detail page at <a href="http://www.informatics.jax.org">Mouse Genome Informatics</a></li>
                                <li>The latest IMPC production status for this gene.  This aggregates the statuses of all ongoing projects for this gene in <a href="https://www.mousephenotype.org/imits/">iMits</a> and displays the status of the project that is closest to producing a mouse.</li>
                                <li>Links to different view of the gene in the Ensembl genome browser: 
                                    <strong>Gene view</strong> links to the browser centered on the gene, 
                                    <strong>Location view</strong> links to a view of the chromosome,
                                    <strong>Compara view</strong> links to the Ensembl compara view for this gene</li>
                                <li>ENU link to the ENU mutant library at the Australian Phenomics Facility</li>   
                            </ul>
                            <img src="img/gene-details.png" />

                            <h3><a name="browser" href='#'>Gene Browser</a></h3>
                            <p>By clicking the <strong> Gene Browser</strong> link, a genome browser will be shown displaying a graphical view
                                of the gene's location and surrounding features.  The browser is interactive and you can use your mouse to zoom and scroll.
                            </p>

                            <img src="img/gene-browser.png" />


                            <h3><a name="phenotypes" href='#'>Gene Phenotypes</a></h3>

                            <p>The gene phenotypes section of a gene page shows the association of genes to <a href="http://www.informatics.jax.org/searches/MP_form.shtml">Mammalian phenotype</a> terms.</p>
                            <img src="img/gene-phenotypes.png" />
                            

                            <h3><a name="phenotype-summary" href='#'>Gene Phenotype Summary</a></h3>

                            <p>A summary of phenotype terms for this gene with associated counts.  The counts indicate the number of unique combinations of:
                            <ul>
                                <li>Phenotype term</li>
                                <li>Allele</li>
                                <li>Zygosity</li>
                                <li>Sex</li>
                                <li>Procedure/Parameter</li>
                                <li>Phenotyping center</li>
                                <li>Analysis - project that asserted the association</li>
                                <li>Graph link</li>
                            </ul>
                            <p>Due to this combining process, the number of rows in the results table may not equal the count shown. Also, be aware that MP terms may have more than 
                                one high level parent and so the counts in the summary may not equal the total number of entries.
                            </p>
                            <img src="img/gene-phenotype-summary.png" />
														<p> The icons on the right hand side show a visual summary of the same data. Blue icons can be clicked to filter the table.</p>

                            <h3><a name="phenotype-table" href='#'>Gene Phenotype Table</a></h3>

                            <p>The phenotype table lists the individual phenotypes associated to this gene through a specific allele.  If both sexes are associated, then both are shown on the same row indicated by the male / female icons (<img src="img/both-sexes-icon.png" />).</p>
                            <img src="img/gene-phenotype-table.png" alt="" />


                            <h3><a name="phenotype-filtering" href='#'>Gene Phenotype Filtering</a></h3>

                            <p>The results shown in the phenotype table may be filtered using the dropdown filters.  Select the check boxes to include entries pertaining to the selection.  The displayed rows are the result of logically ORing within and logically ANDing between dropdowns.</p>
                            <img src="img/gene-phenotype-filter.png" alt="Filter be top level MP term" />


                            <h3><a name="phenotype-download" href='#'>Gene Phenotype Download</a></h3>

                            <p>The results in the table may be downloaded for further processing.  The resulting download respects all filters that have been applied to the data.</p>
                            <p>We offer 2 export options for the data in the table: </p>
                            <ul>
                                <li> TSV, text file with tab separated variables </li>
                                <li> XLS, Microsoft Excel spread sheet</li>
                            </ul>
														<img title="XSL" style="vertical-align:text-bottom;" src="img/export.png"/>
                            <p>
                                In the table displayed on our page entry lines are collapsed based on sex. That is, if for 2 lines all fields are identical except the gender, they will be shown together for a better user experience.
                                In the export file however we export all lines separately, to allow easier further processing of the data. This holds for both XLS and TSV files.
                            </p>


														<h3><a name="pre-qc" href='#'>Pre-QC Phenotype Heatmap</a></h3>

                            <p>When there is data available, but not yet complete, from the IMPC resource, the Pre-QC panel will appear.  The pre QC panel shows a heatmap of the results of preliminary analysis on data that has been collected by the IMPC production centers to date, but is not yet complete. In order to be marked Complete, 7 males and 7 females must complete all core screens required by the IMPC pipeline.
                            </p>
                            <p>Please visit the <a href="https://www.mousephenotype.org/heatmap/manual.html">comprehensive heatmap documentation</a> for more information about the heatmap.</p>			
                            <p>Please visit the <a href="https://www.mousephenotype.org/impress">IMPReSS</a> website for more information about the IMPC pipeline.</p>			
                            <p>NOTE: Use this analysis with caution as the analysis is likely to change when more data becomes available. </p>
                            <img src="img/gene-pre-qc.png" />


                            <h3><a name="phenotype-images" href='#'>Gene Phenotype Images</a></h3>

                            <p>The phenotype images section shows all the images associated to this gene via direct annotation from the image source. Currently all images are courtesy of the <a href="http://www.sanger.ac.uk/mouseportal/">Sanger Mouse Genetic Project</a>.</p>
                            <p>NOTE: This is a work in progress. Images may depict phenotypes not statistically associated with a mouse strain.</p>
                            <img src="img/gene-phenotype-images.png" />
														<br/><br/>


                            <h3><a name="expression-images" href='#'>Gene Expression Images</a></h3>

                            <p>The expression images section shows all the expression images associated to this gene via direct annotation from the image source. Currently all images are courtesy of the <a href="http://www.sanger.ac.uk/mouseportal/">Sanger Mouse Genetic Project</a>.</p>
                            <p>NOTE: This is a work in progress. Images may depict phenotypes not statistically associated with a mouse strain.</p>
                            <img src="img/gene-expression-images.png" />


                            <h3><a name="alleles" href='#'>Order Mouse and ES Cells</a></h3>

                            <p>The alleles and ES cells section describes the mutations available from the IKMC resource.  Each row corresponds to an allele of this gene.  A diagram is included depicting the mutation the allele carries.</p>
                            <p>The links in the <strong>Order</strong> column will take you to the purchase place of the ES cell or mouse when available.</p>  
                            <p>The <strong>genbank file</strong> link points to a genbank file describing the genomic sequence of the allele.</p>
                            <img src="img/gene-alleles.png" />
				

                        </div><%-- end of content div--%>
                    </div>
                </div>
            </div>
        </div>
   
    </jsp:body>
  
</t:genericpage>
