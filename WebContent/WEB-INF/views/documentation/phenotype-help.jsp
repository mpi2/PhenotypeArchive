<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:genericpage>
	<jsp:attribute name="title">International Mouse Phenotyping Consortium Documentation</jsp:attribute>
	<jsp:attribute name="breadcrumb">&nbsp;&raquo; <a href="${baseUrl}/documentation/index">Documentation</a></jsp:attribute>
	<jsp:attribute name="bodyTag"><body  class="page-node searchpage one-sidebar sidebar-first small-header"></jsp:attribute>
	<jsp:attribute name="addToFooter">

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

                        <div class="content node">

                            <h1>Explore the Diverse Entry Points to Mouse Phenotype Data.</h1>

                            <p>
                                Our phenotype page consists of three main parts: the phenotype information/details, the gene associations summary and the table of gene variants associated with the current phenotype.
                            </p>

                            <h3><a name="details" href='#'>Phenotype Details</a></h3>
                            
                            <p>The synonyms and definition we provide come from the mouse phenotype ontology (MP). This is also why we provide the MP id of the current
                                phenotype page as well as the link to it on the MGI MP browser.</p>

                            <p> The procedures come from IMPC pipelines, but also from legacy data pipelines such as EUMODIC, GMC.</p>

                            <figure>
                                <img class="well" src="img/phenotype-information.png"/>
                            </figure>


                            <h3><a name="phenotype-stats-panel" href='#'>Phenotype Association Stats</a></h3>
                            
                            <p> IMPC mouse strains are subjected to a wide range of phenotype assays, allowing for estimates on the percentage of genes that when knocked out contribute to 
                                a phenotype. As multiple parameters may map to the same phenotype, percentages are calculated across all parameters that map to a phenotype.</p>
                                <p> The percentage of genes 
                                associated to the current phenotype in one sex only, for example females is the number of genes associated to phenotypeX in 
                                females divided by the total number of genes tested in females for parameters potentially leading to phenotypeX associations. Thus, the percent 
                                values we show for females and males may not add up to 1 nor do the percentages for males/females have to be smaller than the combined percentage.</p>

                            <img class="well" src="img/phenotype-associations-stats.png"/>
                            
                            <p> Currently the data used for this panel is restricted to EuroPhenome data on B6N and B6NTac strains. This will be replaced when a sufficient amount of IMPC phenotype 
                                data becomes available. Mutant strains with the phenotype association appear in the gene -phenoytype table found further down the phenotype page. Users may view the control
                                and phenotype data that was used in the phenotype call using links from this table.</p> 				


                            <h3> <a name="phenGraphs" href='#'> Overview Graphs</a></h3>
                           
                            <p> The way we represent different types of data varies in what we select as control, what we represent (individual animals or strains) or values (means, count) 
                                so we describe each separately. </p>

                                
                            <h5> Graph Filters and Selectors</h5>
                            <p> Multiple parameters can indicate the same phenotype. When this is the case a drop-down list will appear on top of the list, allowing you to select the desired parameter.</p>
                           	<figure>
                                <img class="well" src="img/graph-filters-closed.png"/>
                                <img class="well" src="img/graph-filters-open.png"/>
                            </figure>
                            <p> The filters under the graph allow you to filter the plottted data based on sex and phenotyping center. </p>
                            <figure>
                                <img class="well" src="img/graph-selector-closed.png"/>
                                <img class="well" src="img/graph-selector-open.png"/>
                            </figure>


                            <h5> Unidimensional Data </h5>
                            <figure>
                                <img class="well" src="img/unidimensional-overview.png"/>
                            </figure>
                            <p> Unidimensional data is plotted as stacked histograms. We take the mean for each line and plot these values as a histogram. Mutant lines that have been
                                associated to the phenotype are highlighted including those lines where the phenotype was only observed in one gender or zygotic state. Some lines may be associated
                                to a phenotype but not appear to be an outlier; this usually results from controls having relatively low or high values in the time period the mutant lines was tested.
                            </p>
														<p>[Tip] The bars are clickable and will take you to a graph page to closer analyze the data. </p>

                            <h5> Time Series Data  </h5>
                            <figure>
                                <img class="well" src="img/time-series-overview.png"/>
                            </figure>
                            <p> For time-series data we plot mean values for each mutant line. We average the values in each line at each time-point and thus get the plot values. 
                                To simplify visualization of calorimetric parameters, data is binned into discrete time points by taking the mean of values measured between two time points. The 
                                control data represents the mean of all C57BL/6N baseline animals. For the mutant lines we do not show the error bars in order to avoid overcrowding the graph.
														<p>[Tip] You can select which lines to see on the graph by simply clicking on the names in the legend. </p>

                            <h5> Categorical Data </h5>
                            <figure>
                                <img class="well" src="img/categorical-overview.png"/>
                            </figure>
                            <p> Categorical overview graphs represent the percentage of <b>animals</b> in each category. 
                                The control values are percentages of baseline animals and the mutant values are percentage of animals from mutant strains for which the phenotype association is made. </p>
														<p>[Tip] You can select/deselect the categories to be ploted by clicking on them in the legend below the graph.</p>


                            <h3><a name="associations" href='#'>Gene-Phenotype Associations</a></h3>

                            <p>All gene variants associated with the current phenotype are shown in a table. The table contains several fields of interest such 
                                as gene name and the corresponding allele, zygosity, sex, data source, parameter, a link to the <a href="graph-help.html">graph</a> when one is available as well as the 
                                used procedure and directly associated phenotype. The directly associated phenotype is particularly useful for top level phenotype terms. See <a href="phenotype-help.html#dvi">Direct vs. inferred associations</a> for more information.
                          
                            <p>Rows are collapsed based on sex. For more readability we group entries where all fields are identical except the sex to one row. These rows are identified by 
                                a both-sexes icon (<img src="img/both-sexes-icon.png"/>). The total number of results shown on top of the table will double count this rows (once for females 
                                and once for males).</p>	 
                            <img class="well" src="img/phenotype-association-table.png"/>


                            <h3><a name="dvi" href='#'>Direct vs. inferred associations</a></h3>
														<p>Lower level MP terms, are directly associated. At the moment this is the case for the vast majority of the phenotype pages. Higher level MP terms
														 and the transitively associated gene variants, infered from the directly associated lower level phenotype association. This is the case
														  only for the top level at the moment, but we will add intermediate terms too. One way to navigate to these pages is from the
                                <a href="gene-help.html#phenotype-summary">phenotype summary</a> on the gene page or you could simply do search for a top level MP term.</p>				 
                            <img class="well" src="img/phenotype-top-level-table.png"/>


                            <h3><a name="table-filtering" href='#'>Table Filtering</a></h3>

                            <p>The filters over the gene variants associations table offer flexible filtering possibilities. Multiple checkboxes can be selected from any filter dropdown list 
                                and the table will automatically reload with each new selected option. 
                                These changes will be mirrored by the total number of results over the table as well as by the table export.  </p>
                            <p>Multiple filters from the same dropdown list will be joined by a logical OR while between different lists we will use AND. </p>
                            <img src ="img/phenotype-filters.png"/>


                            <h3><a name="table-download" href='#'>Downloading Results</a></h3>
                          
                            <p> The results in the table may be downloaded for further processing. The resulting download respects all filters that have been applied to the data.</p>
                            <p> We offer 2 export options for the data in the table: text file with tab separated variables (TSV) and Microsoft Excel spread sheet (XSL)</p>
                            <img title="TSV" style="vertical-align:text-bottom;" src="img/export.png"/> 
                            
                            <p> Please note that in the table displayed on our page entry lines are collapsed based on sex. That is, if for 2 lines all fields are identical except the gender, 
                                they will be shown together for a better user experience.
                                In the export file however we export all lines separately, to allow easier further processing of the data. This holds for both XLS and TSV files. </p>
                           

                        </div><%-- end of content div--%>
                    </div>
                </div>
            </div>
        </div>
   
    </jsp:body>
  
</t:genericpage>
