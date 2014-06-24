<%-- 
    Document   : experimentalObservationAPI
    Created on : Jun 11, 2014, 11:44:21 AM
    Author     : mrelac
--%>
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
            <link type='text/css' rel='stylesheet' href='${baseUrl}/css/custom.css' />
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
        
                            

        <h1>REST API documentation for IMPC and Legacy experimental raw data access</h1>

        <p>The experimental data set contains data observations from experiments conducted for the Europhenome project (see <a href="http://www.europhenome.org">http://www.europhenome.org</a>) and for the International Mouse Phenotype Consortium (IMPC) project (see <a href="http://www.mousephenotype.org">http://www.mousephenotype.org</a>). A record in this resource represents a single data point for an experiment. The list of experiments performed are documented in the International Mouse Phenotyping Resource of Standardised Screens (IMPREeSS, see <a href="http://www.mousephenotype.org/impress">http://www.mousephenotype.org/impress</a>). Individual data points are associated to an IMPReSS <strong>Parameter</strong>. Parameters are organised into Procedures. Procedures are organised into Pipelines.</p>

        <p>There are many ways to select and filter experimental data records, e.g.:</p>

        <ul>
            <li>all data points for a parameter</li>
            <li>all data points for a gene for one experiment</li>
            <li>all data for a specific pipeline</li>
        </ul>

        <p>The experimental data REST API provides the fields described in the table below. Each field may be used for restricting the set of experimental data you wish to receive. The full SOLR select syntax is available for use in querying the REST API. See <a href="http://wiki.apache.org/solr/SolrQuerySyntax">http://wiki.apache.org/solr/SolrQuerySyntax</a> and <a href="http://wiki.apache.org/solr/CommonQueryParameters">http://wiki.apache.org/solr/CommonQueryParameters</a> for a more complete list of query options.</p>

        <table>
            <thead>
                <tr>
                    <th>Field name</th>
                    <th>Datatype</th>
                    <th>Description</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td>phenotyping_center</td>
                    <td>string</td>
                    <td>the name of the organisation that performed the experiment</td>
                </tr>
                <tr>
                    <td>gene_accession_id</td>
                    <td>string</td>
                    <td>the gene MGI ID (<a href="http://www.informatics.jax.org">http://www.informatics.jax.org</a>) of the mutant specimen used for the experiment</td>
                </tr>
                <tr>
                    <td>gene_symbol</td>
                    <td>string</td>
                    <td>the gene symbol of the mutant specimen used for the experiment</td>
                </tr>
                <tr>
                    <td>allele_accession_id</td>
                    <td>string</td>
                    <td>the allele MGI ID (<a href="http://www.informatics.jax.org">http://www.informatics.jax.org</a>) of the mutant specimen used for the experiment</td>
                </tr>
                <tr>
                    <td>allele_symbol</td>
                    <td>string</td>
                    <td>the allele symbol of the mutant specimen used for the experiment</td>
                </tr>
                <tr>
                    <td>zygosity</td>
                    <td>string</td>
                    <td>indicating the zygosity of the specimen</td>
                </tr>
                <tr>
                    <td>sex</td>
                    <td>string</td>
                    <td>indicating the sex of the specimen</td>
                </tr>
                <tr>
                    <td>biological_sample_group</td>
                    <td>string</td>
                    <td>indicating if the specimen was a member of the control group or the experimental group (also see metadata_group)</td>
                </tr>
                <tr>
                    <td>metadata_group</td>
                    <td>string</td>
                    <td>a string indicating a group of experimental and control mice that have the same metadata (see also biological_sample_group)</td>
                </tr>
                <tr>
                    <td>metadata</td>
                    <td>list of strings</td>
                    <td>list showing all relevant metadata in effect when the data was collected</td>
                </tr>
                <tr>
                    <td>strain_accession_id</td>
                    <td>string</td>
                    <td>indicating the background strain of the specimen</td>
                </tr>
                <tr>
                    <td>strain_name</td>
                    <td>string</td>
                    <td>indicating the background strain name of the specimen</td>
                </tr>
                <tr>
                    <td>pipeline_name</td>
                    <td>string</td>
                    <td>indicating the name of the pipeline where the experiment was conducted</td>
                </tr>
                <tr>
                    <td>pipeline_stable_id</td>
                    <td>string</td>
                    <td>indicating the IMPReSS ID of the pipeline</td>
                </tr>
                <tr>
                    <td>procedure_stable_id</td>
                    <td>string</td>
                    <td>indicating the IMPReSS ID of the procedure</td>
                </tr>
                <tr>
                    <td>procedure_name</td>
                    <td>string</td>
                    <td>indicating the full name of the procedure</td>
                </tr>
                <tr>
                    <td>parameter_stable_id</td>
                    <td>string</td>
                    <td>indicating the IMPReSS ID of the parameter</td>
                </tr>
                <tr>
                    <td>parameter_name</td>
                    <td>string</td>
                    <td>indicating the full name of the parameter</td>
                </tr>
                <tr>
                    <td>experiment_source_id</td>
                    <td>string</td>
                    <td>indicating the experiment identifier at the center that performed it</td>
                </tr>
                <tr>
                    <td>observation_type</td>
                    <td>string</td>
                    <td>indicating the type of parameter (categorical, unidimensional, multidimensional, time series, metadata)</td>
                </tr>
                <tr>
                    <td>colony_id</td>
                    <td>string</td>
                    <td>indicating the name of the colony of the specimen</td>
                </tr>
                <tr>
                    <td>date_of_birth</td>
                    <td>date</td>
                    <td>indicating the date the specimen was born</td>
                </tr>
                <tr>
                    <td>date_of_experiment</td>
                    <td>date</td>
                    <td>indicating the date the data was collected</td>
                </tr>
                <tr>
                    <td>data_point</td>
                    <td>float</td>
                    <td>indicates the measured data value (1) (2) (3)</td>
                </tr>
                <tr>
                    <td>order_index</td>
                    <td>int</td>
                    <td>indicating the order (2)</td>
                </tr>
                <tr>
                    <td>dimension</td>
                    <td>string</td>
                    <td>indicating the dimension (2)</td>
                </tr>
                <tr>
                    <td>time_point</td>
                    <td>string</td>
                    <td>indicating the time the data value was measured (3)</td>
                </tr>
                <tr>
                    <td>discrete_point</td>
                    <td>float</td>
                    <td>indicating the discrete point (3)</td>
                </tr>
                <tr>
                    <td>category</td>
                    <td>string</td>
                    <td>indicating the category to which the specimen has been classified (4)</td>
                </tr>
                <tr>
                    <td>value</td>
                    <td>string</td>
                    <td>the value of the metadata (5)</td>
                </tr>
            </tbody>
        </table>

        <blockquote>
            <p>(1) - For unidimensional parameters (2) - For multidimensional parameters (3) - For time series parameters (4) - For categorical parameters (5) - For metadata parameters</p>
        </blockquote>
        
        <h4>Examples</h4>

        <p>NOTE: It may be necessary for spaces and double quotes to be url encoded (space = %20, double quote=%22) for command line usage</p>
        <p>Retrieve all experimental results for parameter ESLIM_001_001_009 (Coat hair color) for colony HEPD0527_5_A04</p>
        <div class="highlight highlight-bash"><pre>curl <span class="se">\</span>
<span class="s1">'http://www.ebi.ac.uk/mi/impc/solr/experiment/select?q=parameter_stable_id:ESLIM_001_001_009%20AND%20colony_id:HEPD0527_5_A04&amp;wt=json&amp;indent=true'</span>
</pre>

        </div>
        <p>Retrieve all experimental results for organisation WTSI</p>
        <div class="highlight highlight-bash"><pre>curl <span class="se">\</span>
<span class="s1">'http://www.ebi.ac.uk/mi/impc/solr/experiment/select?q=phenotyping_center:WTSI&amp;wt=json&amp;indent=true'</span>
</pre>

        </div>
        <p>Retrieve all unidimensional experimental results for Harwell for pipeline EUMODIC Pipeline 1</p>
        <div class="highlight highlight-bash"><pre>curl <span class="se">\</span>
<span class="s1">'http://www.ebi.ac.uk/mi/impc/solr/experiment/select?q=phenotyping_center:%22MRC%20Harwell%22%20AND%20pipeline_name:%22EUMODIC%20Pipeline%201%22%20AND%20observation_type:unidimensional&amp;wt=json&amp;indent=true'</span>
</pre>

                            
                        </div><%-- end of content div--%>
                    </div>
                </div>
            </div>
        </div>
   
    </jsp:body>
  
</t:genericpage>