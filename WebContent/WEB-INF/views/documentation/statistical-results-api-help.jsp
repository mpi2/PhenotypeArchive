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
        
                            

        <h1>REST API documentation for IMPC and Legacy statistical result access</h1>

        <p>The statistical-result REST API provides access to the detailed output generated as part of the statistical analysis process.</p>

        <p>The statistical-result REST API provides the fields described in the table below. Each field may be used for restricting the set of statistical-results you wish to receive. The full SOLR select syntax is available for use in querying the REST API. See <a href="http://wiki.apache.org/solr/SolrQuerySyntax">http://wiki.apache.org/solr/SolrQuerySyntax</a> and <a href="http://wiki.apache.org/solr/CommonQueryParameters">http://wiki.apache.org/solr/CommonQueryParameters</a> for a more complete list of query options.</p>

    <table>
        <thead>
            <tr>

                <th>Field name</th>
                <th>Datatype</th>
                <th>Description</th>
            </tr>
        </thead>
        <tbody>

            <!-- Information about the SOLR document -->
            <tr>
                <td>doc_id</td>
                <td>string</td>
                <td>The id of the solr document</td>
            </tr>
            <tr>
                <td>db_id</td>
                <td>int</td>
                <td>The IMPC internal database identifier</td>
            </tr>
            <tr>
                <td>data_type</td>
                <td>string</td>
                <td>The type of the underlying data for which the statistic was calculated</td>
            </tr>

            <!-- Information about the MP term -->
            <tr>
                <td>mp_term_id</td>
                <td>string</td>
                <td>The accession if of the MP term associated to this result</td>
            </tr>
            <tr>
                <td>mp_term_name</td>
                <td>string</td>
                <td>The name of the MP term associated to this result</td>
            </tr>
            <tr>
                <td>top_level_mp_term_id</td>
                <td>string</td>
                <td>The accession id of top level MP term obtained by interrogating the MP ontology</td>
            </tr>
            <tr>
                <td>top_level_mp_term_name</td>
                <td>string</td>
                <td>The name of top level MP term obtained by interrogating the MP ontology</td>
            </tr>
            <tr>
                <td>intermediate_mp_term_id</td>
                <td>string</td>
                <td>The accession id of all intermediate level MP terms obtained by interrogating the MP ontology</td>
            </tr>
            <tr>
                <td>intermediate_mp_term_name</td>
                <td>string</td>
                <td>The name of all intermediate level MP terms obtained by interrogating the MP ontology</td>
            </tr>
            
            <!-- Information about the data being processed -->
            <tr>
                <td>resource_name</td>
                <td>string</td>
                <td>The short name of the resource responsible for producing the data</td>
            </tr>
            <tr>
                <td>resource_fullname</td>
                <td>string</td>
                <td>The full name of the resource responsible for producing the data</td>
            </tr>
            <tr>
                <td>resource_id</td>
                <td>int</td>
                <td>The IMPC internal identifier of the resource responsible for producing the data</td>
            </tr>
            <tr>
                <td>project_name</td>
                <td>string</td>
                <td>The consortium/project that produced the data</td>
            </tr>
            <tr>
                <td>phenotyping_center</td>
                <td>string</td>
                <td>The center where the data was generated</td>
            </tr>

            <tr>
                <td>pipeline_stable_id</td>
                <td>string</td>
                <td>IMPRESS pipeline identifier</td>
            </tr>
            <tr>
                <td>pipeline_stable_key</td>
                <td>string</td>
                <td>IMPRESS pipeline stable key</td>
            </tr>
            <tr>
                <td>pipeline_name</td>
                <td>string</td>
                <td>IMPRESS pipeline name</td>
            </tr>
            <tr>
                <td>pipeline_id</td>
                <td>int</td>
                <td>IMPC internal id representing the IMPRESS pipeline</td>
            </tr>

            <tr>
                <td>procedure_stable_id</td>
                <td>string</td>
                <td>IMPRESS procedure stable id</td>
            </tr>
            <tr>
                <td>procedure_stable_key</td>
                <td>string</td>
                <td>IMPRESS procedure stable id</td>
            </tr>
            <tr>
                <td>procedure_name</td>
                <td>string</td>
                <td>IMPRESS procedure name</td>
            </tr>
            <tr>
                <td>procedure_id</td>
                <td>int</td>
                <td>IMPC internal id representing the IMPRESS procedure</td>
            </tr>

            <tr>
                <td>parameter_stable_id</td>
                <td>string</td>
                <td>IMPRESS parameter stable id</td>
            </tr>
            <tr>
                <td>parameter_stable_key</td>
                <td>string</td>
                <td>IMPRESS parameter stable key</td>
            </tr>
            <tr>
                <td>parameter_name</td>
                <td>string</td>
                <td>IMPRESS parameter name</td>
            </tr>
            <tr>
                <td>parameter_id</td>
                <td>int</td>
                <td>IMPC internal id representing the IMPRESS parameter</td>
            </tr>

            <tr>
                <td>colony_id</td>
                <td>string</td>
                <td>Phenotyping center specific colony name of the line used to generate the data</td>
            </tr>
            <tr>
                <td>marker_symbol</td>
                <td>string</td>
                <td>Gene symbol</td>
            </tr>
            <tr>
                <td>marker_accession_id</td>
                <td>string</td>
                <td>MGI ID of the gene</td>
            </tr>
            <tr>
                <td>allele_symbol</td>
                <td>string</td>
                <td>Allele symbol</td>
            </tr>
            <tr>
                <td>allele_name</td>
                <td>string</td>
                <td>Allele name</td>
            </tr>
            <tr>
                <td>allele_accession_id</td>
                <td>string</td>
                <td>MGI ID of the allele</td>
            </tr>
            <tr>
                <td>strain_name</td>
                <td>string</td>
                <td>Background strain name</td>
            </tr>
            <tr>
                <td>strain_accession_id</td>
                <td>string</td>
                <td>Background strain MGI accession ID (or IMPC ID when MGI accession is not available)</td>
            </tr>
            <tr>
                <td>sex</td>
                <td>string</td>
                <td>The sex of the specimen</td>
            </tr>
            <tr>
                <td>zygosity</td>
                <td>string</td>
                <td>The zygosity of the mutant specimen</td>
            </tr>
            <tr>
                <td>control_selection_method</td>
                <td>string</td>
                <td>The strategy used to select the control set (options are baseline_all or concurrent)</td>
            </tr>
            <tr>
                <td>dependent_variable</td>
                <td>string</td>
                <td>The variable being tested</td>
            </tr>
            <tr>
                <td>metadata_group</td>
                <td>string</td>
                <td>Identifier specifying the specific experimental conditions when the experiment took place</td>
            </tr>

            <!-- Information about the raw data -->
            <tr>
                <td>control_biological_model_id</td>
                <td>int</td>
                <td>IMPC internal ID of the biological model of the control group</td>
            </tr>
            <tr>
                <td>mutant_biological_model_id</td>
                <td>int</td>
                <td>IMPC internal ID of the biological model of the experimental group</td>
            </tr>
            <tr>
                <td>male_control_count</td>
                <td>int</td>
                <td>Count of male specimens in the control group</td>
            </tr>
            <tr>
                <td>male_mutant_count</td>
                <td>int</td>
                <td>Count of male specimens in the experimental group</td>
            </tr>
            <tr>
                <td>female_control_count</td>
                <td>int</td>
                <td>Count of female specimens in the control group</td>
            </tr>
            <tr>
                <td>female_mutant_count</td>
                <td>int</td>
                <td>Count of female specimens in the experimental group</td>
            </tr>

            <!-- Information about the calculation -->
            <tr>
                <td>statistical_method</td>
                <td>string</td>
                <td>The statistical method used to calculate the p value</td>
            </tr>
            <tr>
                <td>status</td>
                <td>string</td>
                <td>The status of the statistical calculation</td>
            </tr>
            <tr>
                <td>additional_information</td>
                <td>string</td>
                <td>Any additional information about the calculation</td>
            </tr>
            <tr>
                <td>raw_output</td>
                <td>string</td>
                <td>The actual R output produced while performing the calculation</td>
            </tr>
            <tr>
                <td>p_value</td>
                <td>float</td>
                <td>The p value of the data</td>
            </tr>
            <tr>
                <td>effect_size</td>
                <td>float</td>
                <td>The effect size of the data</td>
            </tr>

            <!-- Categorical statistics  -->
            <tr>
                <td>categories</td>
                <td>string</td>
                <td>Categories of data (4)</td>
            </tr>
            <tr>
                <td>categorical_p_value</td>
                <td>float</td>
                <td>The p value for categorical data (4)</td>
            </tr>
            <tr>
                <td>categorical_effect_size</td>
                <td>float</td>
                <td>The effect size (max percentage change) for categorical data (4)</td>
            </tr>

            <!-- Continuous statistics -->
            <tr>
                <td>batch_significant</td>
                <td>boolean</td>
                <td>True/false if random variable "batch" is significant or not (1)</td>
            </tr>
            <tr>
                <td>variance_significant</td>
                <td>boolean</td>
                <td>True/false if variance is significant (1)</td>
            </tr>
            <tr>
                <td>null_test_p_value</td>
                <td>float</td>
                <td>The overall significance result of the calculation (1)</td>
            </tr>
            <tr>
                <td>genotype_effect_p_value</td>
                <td>float</td>
                <td>The significance of the genotype effect to describe variation in the data (1)</td>
            </tr>
            <tr>
                <td>genotype_effect_stderr_estimate</td>
                <td>float</td>
                <td>The estimate of the standard error of the genotype effect (1)</td>
            </tr>
            <tr>
                <td>genotype_effect_parameter_estimate</td>
                <td>float</td>
                <td>The effect size estimate of the genotype effect (1)</td>
            </tr>
            <tr>
                <td>sex_effect_p_value</td>
                <td>float</td>
                <td>The significance of sex to describe variation in the data (1)</td>
            </tr>
            <tr>
                <td>sex_effect_stderr_estimate</td>
                <td>float</td>
                <td>The estimate of the standard error of the sex effect (1)</td>
            </tr>
            <tr>
                <td>sex_effect_parameter_estimate</td>
                <td>float</td>
                <td>The effect size estimate of the sex effect (1)</td>
            </tr>
            <tr>
                <td>weight_effect_p_value</td>
                <td>float</td>
                <td>The significance of weight to describe variation in the data (1)</td>
            </tr>
            <tr>
                <td>weight_effect_stderr_estimate</td>
                <td>float</td>
                <td>The estimate of the standard error of the weight effect (1)</td>
            </tr>
            <tr>
                <td>weight_effect_parameter_estimate</td>
                <td>float</td>
                <td>The effect size estimate of the weight effect (1)</td>
            </tr>
            <tr>
                <td>group_1_genotype</td>
                <td>string</td>
                <td>The genotype of the first group (usually +/+)</td>
            </tr>
            <tr>
                <td>group_1_residuals_normality_test</td>
                <td>float</td>
                <td>Significance that group 1 conforms to normal distribution</td>
            </tr>
            <tr>
                <td>group_2_genotype</td>
                <td>string</td>
                <td>The genotype of the second group (usually the colony id)</td>
            </tr>
            <tr>
                <td>group_2_residuals_normality_test</td>
                <td>float</td>
                <td>Significance that group 2 conforms to normal distribution</td>
            </tr>
            <tr>
                <td>blups_test</td>
                <td>float</td>
                <td>Best Linear Unbiased Prediction test</td>
            </tr>
            <tr>
                <td>rotated_residuals_test</td>
                <td>float</td>
                <td>...</td>
            </tr>
            <tr>
                <td>intercept_estimate</td>
                <td>float</td>
                <td>...</td>
            </tr>
            <tr>
                <td>intercept_estimate_stderr_estimate</td>
                <td>float</td>
                <td>...</td>
            </tr>
            <tr>
                <td>interaction_significant</td>
                <td>boolean</td>
                <td>True/false if the significance of sex~genotype interaction is significant (1)</td>
            </tr>
            <tr>
                <td>interaction_effect_p_value</td>
                <td>float</td>
                <td>The significance of sex~genotype interaction to describe variation in the data (1)</td>
            </tr>
            <tr>
                <td>female_ko_effect_p_value</td>
                <td>float</td>
                <td>If sex is significant, the significance of the female genotype (1)</td>
            </tr>
            <tr>
                <td>female_ko_effect_stderr_estimate</td>
                <td>float</td>
                <td>If sex is significant, the standard error estimate of the female genotype (1)</td>
            </tr>
            <tr>
                <td>female_ko_parameter_estimate</td>
                <td>float</td>
                <td>If sex is significant, the effect size estimate of the female genotype (1)</td>
            </tr>
            <tr>
                <td>male_ko_effect_p_value</td>
                <td>float</td>
                <td>If sex is significant, the significance of the male genotype (1)</td>
            </tr>
            <tr>
                <td>male_ko_effect_stderr_estimate</td>
                <td>float</td>
                <td>If sex is significant, the standard error estimate of the male genotype (1)</td>
            </tr>
            <tr>
                <td>male_ko_parameter_estimate</td>
                <td>float</td>
                <td>If sex is significant, the effect size estimate of the male genotype (1)</td>
            </tr>
            <tr>
                <td>classification_tag</td>
                <td>string</td>
                <td>A summary of the result (1)</td>
            </tr>
        </tbody>
    </table>
        <blockquote>
            <p>(1) - For unidimensional parameters (2) - For categorical parameters</p>
        </blockquote>
        
        <h4>Examples</h4>

        <p>NOTE: It may be necessary for spaces and double quotes to be url encoded (space = %20, double quote=%22) for command line usage</p>
        <p>Coming soon</p>
        <div class="highlight highlight-bash"><pre>
</pre>
        </div>

                            
                        </div><%-- end of content div--%>
                    </div>
                </div>
            </div>
        </div>
   
    </jsp:body>
  
</t:genericpage>