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

                            <h1>More information about the way IMPC uses graphs.</h1>

                            <h3><a name="data" href='#'>Where is the data coming from?</a></h3>
                            <p>
                                Data for the graphs are obtained both from the IMPC phenotyping centers as well as the <a href="http://www.europhenome.org/"> Europhenome</a> legacy project.
                                The table of genotype to phenotype associations on the gene and phenotype pages include links to graphs of the data and more information about the processing used to determine statistical significance.</p>

                            <p> The p values and other model fitting estimates are calculated using the <a hre="statistics-help">IMPC statistical methods</a>.
                                <img src="img/graph_links.png">
                            </p>

                            <h3><a name="interactions" href='#'>Interacting with Graphs</a> </h3>
                            <p>Graphs are interactive so that you can adjust the view to your liking. Click on a legend will remove a set of data from the graph. This is especially useful if you wish to remove "noise" from a graph and focus on the control or experimental data. <img src="img/graph_legends.png"> 
                            </p>

                            <p>After clicking on the control legend the male homozygote data has disappeared:<img src="img/graph_no_control.png" alt="graph with no control here">
                            </p>
                            <p>Hovering over a data point or error bars displays extra information about the data point:<img src="img/graph_hover.png" alt="graph with a hovering over mouse label here">
                            </p>
                            <p>If appropriate the graph will allow you to zoom in on a data set by clicking and dragging to create a square/zoomable area:<img src="img/graph_before_zoom.png" alt="Scatter graph with no zoom shown here">
                            </p>
                            <p>Once zoomed a "Reset zoom" button appears at the right of the graph to enable the graph to be reset to original position:
                                <img src="img/graph_after_zoom.png" alt="Zoomed in graph here">
                            </p>

                            <h3><a name="export" href='#'>Exporting Graph Data</a></h3>
                            <p>An export button is always visible on the right hand side of the graphs where the graph picture can be exported in png, jpeg, pdf or svg format which will be downloaded to your computer:
                                <img src="img/graph_export.png" alt="export button in graph here">
                            <p>The data used to generate the graphs can be downloaded in XLS or TSV formats from the buttons on the top of the page (<img src="img/graph-data-export.png" 
                                                                                                                                                          alt="export button in graph here">). This will export data for all graphs shown on the page. Data for males and females from the same experiment will be shown 
                                in the same table while for different zygosities, organizations or strains there will be separate tables exported in the same file. Following we present a list of
                                exported values accompanied by a short description when needed.
                            </p>
                            <table>
                                <tr> <th width="20%">Column</th> <th> Description</th></tr>
                                <tr><td> pipelineName </td><td> Pipeline through which the phenotyping was done, i.e. EUMODIC pipelines </td></tr>
                                <tr><td> pipelineStableId </td><td> Pipeline id </td></tr>
                                <tr><td> procedureStableId </td><td> Procedure id </td></tr>
                                <tr><td> procedureName </td><td> Procedure name </td></tr>
                                <tr><td> parameterStableId </td><td> Parameter id </td></tr>
                                <tr><td> parameterName </td><td> Parameter name </td></tr>
                                <tr><td> strain </td><td> Mouse strain used </td></tr>
                                <tr><td> geneSymbol </td><td> Gene symbol </td></tr>
                                <tr><td> geneAccession </td><td> Gene MGI id </td></tr>
                                <tr><td> organisation </td><td> Organization name </td></tr>
                                <tr><td> colonyId </td><td> Colony id </td></tr>
                                <tr><td> dateOfExperiment </td><td> Date of experiment </td></tr>
                                <tr><td> externalSampleId </td><td> Animal id </td></tr>
                                <tr><td> zygosity </td><td> Zygosity </td></tr>
                                <tr><td> sex </td><td> Sex </td></tr>  
                                <tr><td> group </td><td> This field has only 2 values: control or experiment </td></tr>  
                                <tr><td> category </td><td> Column is exported only for categorical data. It contains the label of the category assigned. </td></tr>  
                                <tr><td> meta data </td><td> Shows any meta data in respect of equipment used which can be used to seperate the data sets used for statistical analysis</td></tr>  
                                <tr><td> dataPoint </td><td> Coulmn is exported for unidimensional or time series data. It contains the value resulted from the measurment decribed by the current procedure.</td></tr>  
                                <tr><td> discretePoint </td><td> Column exported for time series data. It contains the relative timepoints at which the measurments were made. </td></tr> 
                            </table>
                            <br/>
                            <p> A <b>restful web service</b> is also available for retrieving information pertaining to experiments via a web browser or a programming language of your choice. Documentation for this can be found <a href="https://github.com/mpi2/PhenotypeArchive/wiki">here</a>.			</p>

                            <h3><a name="types" href='#'>Types of Graphs and Equations</a></h3>
                            <p>
                                Currently 4 types of graph exist in the IMPC portal from the IMPC: 
                            </p>
                            <ol>
                                <li><a href="graph-help#categorical_graphs">Categorical Bar Graphs</a></li>
                                <li><a href="graph-help#undimensional_graphs">Unidimensional Scatter and Box Plot Graphs</a></li>
                                <li><a href="graph-help#time_graphs">Time Series Graphs</a></li>
                                <li><a href="graph-help#scatter_graphs">Scatter Plots Showing Data Grouped by Date</a></li>
                            </ol>

                            <h3><a name="categorical_graphs" href='#'>Categorical Bar Graphs</a></h3>
                            <img src="img/graph_categorical_normal.png">
                            <p>Categorical graphs contain data where an observation can be categorised into one of two or more groups e.g. Abnormal Eye or Normal Eye. Graphs are presented as bar graphs with a table underneath. If IMPC data is available this will be displayed (see <a href="statistics-help.html">statistic help</a> for more information).
                            </p>

                            <h3><a name="undimensional_graphs" href='#'>Unidimensional Scatter and Box Plot Graphs</a></h3>
                            <img src="img/graph_box_normal.png">
                            <p>Where an observation can be measured on a continuous basis (e.g. red blood cell counts or tail length), we display them in a mixed box and scatter plot. The first columns contain box plots for Wild Type (control), Homozygote then Heterozygote mutants for female then the columns representing males. The second set of columns contain scatter plots for the same sets of data. Hover over the box in the graph will show the basic statistics for that set of data: 
                                <img src="img/graph_box_with_labels.png">

                            </p>
                            <p>
                            Below the graph is a table showing the output of the statistical test used to determine if this data is statistically significant:
                            <img src="img/stats_mm_table.png">
                                </p>
                            <p>In the top left of the table is the global p value used to determine if this is a statistically significant result.<br />
                                The Classification column displays the effect of sexual dimorphism on this association (<a href="http://en.wikipedia.org/wiki/Sexual_dimorphism">sexual dimorphism</a> is the phenomenon when the genotype affects the sexes differently). The possible results are
                            </p>
                            <dl>
                                <dt>No significant change</dt>
                                <dd>The effect is not statistically significant</dd>
                                <dt>Can not differentiate genders</dt>
                                <dd>For the measured parameter, there is a significant effect, but there is not enough power to determine sexual dimorphism</dd>
                                <dt>Both genders equally</dt>
                                <dd>For the measured parameter, both sexes are effected equally</dd>
                                <dt>Female only</dt>
                                <dd>For the measured parameter, only the females are effected</dd>
                                <dt>Male only</dt>
                                <dd>For the measured parameter, only the males are effected</dd>
                                <dt>Different effect size, females greater</dt>
                                <dd>For the measured parameter, there is an effect on males but the size of the effect on females is greater.</dd>
                                <dt>Different effect size, males greater</dt>
                                <dd>For the measured parameter, there is an effect on females but the size of the effect on males is greater.</dd>
                                <dt>Female and male different directions</dt>
                                <dd>For the measured parameter, the effect on one sex is increased while the other sex is decreased.</dd>
                                <dt>If phenotype is significant it is for the one genotype tested</dt>
                                <dd>For the measured parameter, there was no data for one of the sexes, so sexual dimorphism cannot be determined.</dd>
                            </dl>

                            <p>
                            The <strong>more statistics</strong> link at the bottom of the table will display additional output related to the statistical method.
                            </p>

														<!-- Commented out Nov 3rd, 2014 as there is no way (any more) of getting to the timeseries graphs on our portal. -->
                            <!-- h3><a name="time_graphs" href='#'>Time Series Graphs</a></h3>
                            <img src="img/graph_time_series.png">
                            <p>Where an observation can be measured as a time series (e.g. mean blood glucose concentration), we display the data in a line graph and scatter plot. The line graph will contain lines for wild-type and mutant data for female and for males.  The data points displayed are the mean of all data collected at that timepoint and the whiskers indicate standard deviation.
                                Hover over the points to see basic statistics about the data.  </p-->

                            <h3><a name="scatter_graphs" href='#'>Scatter Plots Showing Data grouped by Date</a></h3>
                            <img src="img/graph_scatterplot.png">
                            <p>When the data for a parameter is collected at various points in time, a scatter plot will show each data value on the y a-xis and the date/time the data was collected on the x-axis. </p> 

														<h3><a name="scatter_graphs" href='#'>ABR Charts</a></h3>
                            <img src="img/graph_abr.png">
                            <p>Auditory Brain Stem Responce data is plotted in the context of the whole serie of ABR measurements, as opposed to plotting each parameter 
                            separately. We plot data from the following parameters: click, 6kHz, 12kHz, 18kHz, 24kHz, 30kHz. 
                            For each parameter we plot the mean of it's respective measurements. If one parameter does not have any points it means no data is available. 
      											</p>

                        </div>

                        </div>
                    </div>
                </div>
            </div>
        </div>
   
    </jsp:body>
  
</t:genericpage>
