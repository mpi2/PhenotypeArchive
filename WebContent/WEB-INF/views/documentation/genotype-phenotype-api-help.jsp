<%-- 
    Document   : genotypePhenotypeAPI
    Created on : Jun 11, 2014, 11:42:00 AM
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
                                    <h3>Genotype associated phenotype calls</h3>
                                    <p>There are many ways to get information about the MP terms associated to the different KO genes. You can select data per:</p>
                                    <ul class="task-list">
                                        <li> phenotyping center (UCD, Wellcome Trust Sanger Institute, JAX, etc.) </li>
                                        <li> phenotyping program (legacy MGP, EUMODIC, etc.)</li>
                                        <li> phenotyping resource (EuroPhenome, MGP, IMPC)</li>
                                        <li> phenotyping pipeline (EUMODIC1, EUMODIC2, MGP, IMPC adult, IMPC embryonic, etc.)</li>
                                        <li> phenotyping procedure or parameter</li>
                                        <li> allele name or MGI allele ID</li>
                                        <li> strain name or MGI strain ID</li>
                                        <li> gene symbol or MGI gene ID</li>
                                        <li> or a combination of all these fields</li>
                                    </ul>

                                    <h3><a name="user-content-retrieve-all-genotype-phenotype-associations"
                                           class="anchor" href="#retrieve-all-genotype-phenotype-associations">
                                            <span class="octicon octicon-link"></span></a>
                                        Retrieve all genotype-phenotype associations
                                    </h3>
                                    <p>This is the basic request to get all the results from the Solr service in JSON</p>
                                    <div class="highlight highlight-bash">
                                        <pre>
curl <span class="se">\</span>
    --basic  <span class="se">\</span>
    -X GET <span class="se">\</span>
    <span class="s1">'http://www.ebi.ac.uk/mi/impc/solr/genotype-phenotype/select?q=*:*&amp;rows=10&amp;wt=json'</span>
                                        </pre>
                                    </div>

                                    <p>A bit of explanation:</p>
                                    <ul class="task-list">
                                        <li><strong>genotype-phenotype</strong> is the name of the Solr core service to query</li>
                                        <li><strong>select</strong> is the method used to query the Solr REST interface</li>
                                        <li><strong>q=<em>:</em></strong> means querying everything without any filtering on any field</li>
                                        <li><strong>rows</strong> allows to limit the number of results returned</li>
                                        <li><strong>wt=json</strong> is the response format</li>
                                    </ul>

                                    <h3><a name="user-content-retrieve-all-genotype-phenotype-associations-for-a-specific-marker"
                                           class="anchor"
                                           href="#retrieve-all-genotype-phenotype-associations-for-a-specific-marker">
                                            <span class="octicon octicon-link"></span></a>
                                        Retrieve all genotype-phenotype associations for a specific marker
                                    </h3>
                                    <p>We will constrain the results by adding a condition to the <strong>q</strong> (query) parameter using the specific 
                                        <strong>marker_symbol</strong> field. For Akt2, simply specify <strong>q=marker_symbol:Akt2</strong></p>

                                    <div class="highlight highlight-bash">
                                        <pre>
curl <span class="se">\</span>
    --basic  <span class="se">\</span>
    -X GET <span class="se">\</span>
    <span class="s1">'http://www.ebi.ac.uk/mi/impc/solr/genotype-phenotype/select?q=marker_symbol:Akt2&amp;wt=json'</span>
                                        </pre>
                                    </div>
                                    <h3><a name="user-content-retrieve-all-genotype-phenotype-associations-for-a-specific-mp-term" 
                                           class="anchor"
                                           href="#retrieve-all-genotype-phenotype-associations-for-a-specific-mp-term">
                                            <span class="octicon octicon-link"></span></a>
                                        Retrieve all genotype-phenotype associations for a specific MP term
                                    </h3>
                                    <p>We will constrain the results by adding a condition to the <strong>q</strong> (query) parameter using the specific 
                                        <strong>mp_term_name</strong> field. To retrieve genotype associated to "decreased total body fat amount",
                                        simply specify <strong>q=mp_term_name:"decreased total body fat amount"</strong></p>
                                    <div class="highlight highlight-bash">
                                        <pre>
curl <span class="se">\</span>
    --basic  <span class="se">\</span>
    -X GET <span class="se">\</span>
    <span class="s1">'http://www.ebi.ac.uk/mi/impc/solr/genotype-phenotype/select?q=mp_term_name:"decreased total body fat amount"&amp;wt=json'</span>
                                        </pre>
                                    </div>
                                    <p>This also work with <strong>mp_term_id</strong> the corresponding MP term identifier. In this case specify <strong>q=mp_term_id:"MP:0010025"</strong></p>

                                    <div class="highlight highlight-bash">
                                        <pre>
curl <span class="se">\</span>
    --basic  <span class="se">\</span>
    -X GET <span class="se">\</span>
    <span class="s1">'http://www.ebi.ac.uk/mi/impc/solr/genotype-phenotype/select?q=mp_term_id:"MP:0010025"&amp;wt=json'</span>
                                        </pre>
                                    </div>
                                    <h3><a name="user-content-retrieve-all-genotype-phenotype-associations-for-a-top-level-mp-term"
                                           class="anchor"
                                           href="#retrieve-all-genotype-phenotype-associations-for-a-top-level-mp-term">
                                            <span class="octicon octicon-link"></span></a>
                                        Retrieve all genotype-phenotype associations for a top level MP term</h3>
                                    <p>We will constrain the results by adding a condition to the <strong>q</strong> (query) parameter using the specific 
                                        <strong>top_level_mp_term_name</strong> field. This will work with <strong>top_level_mp_term_id</strong>
                                        if you pass an identifier instead of the MP term name. To retrieve genotype associated to "decreased
                                        total body fat amount", simply specify <strong>q=top_level_mp_term_name:"nervous system phenotype"</strong></p>

                                    <div class="highlight highlight-bash">
                                        <pre>
curl <span class="se">\</span>
    --basic  <span class="se">\</span>
    -X GET <span class="se">\</span>
    <span class="s1">'http://www.ebi.ac.uk/mi/impc/solr/genotype-phenotype/select?q=top_level_mp_term_name:"nervous system phenotype"&amp;wt=json'</span>
                                        </pre>
                                    </div>

                                    <h3>
                                        <a name="user-content-retrieve-all-genotype-phenotype-associations-with-a-p-value-cut-off"
                                           class="anchor"
                                           href="#retrieve-all-genotype-phenotype-associations-with-a-p-value-cut-off">
                                            <span class="octicon octicon-link"></span></a>
                                        Retrieve all genotype-phenotype associations with a p-value cut-off</h3>
                                    <p>In this example, we will apply a cut-off to the previous query and add a condition to the <strong>q</strong> (query)
                                        command. In Solr, you can specify a range to retrieve results. For instance, if you want p-values below 0.0001,
                                        you can add this condition <strong>p_value:[0 TO 0.0001]</strong>. Here, we will retrieve genotype associated
                                        to a nervous system phenotype with a p-value cut-off of 0.00005.</p>
                                    <div class="highlight highlight-bash">
                                        <pre>
curl <span class="se">\</span>
    --basic  <span class="se">\</span>
    -X GET <span class="se">\</span>
    <span class="s1">'http://www.ebi.ac.uk/mi/impc/solr/genotype-phenotype/select?q=top_level_mp_term_name:"nervous system phenotype" AND p_value:[0 TO 0.00005]&amp;wt=json'</span>
                                        </pre></div>

                                    <h3>
                                        <a name="user-content-retrieve-all-genotype-phenotype-associations-for-a-specific-phenotyping-center"
                                           class="anchor"
                                           href="#retrieve-all-genotype-phenotype-associations-for-a-specific-phenotyping-center">
                                            <span class="octicon octicon-link"></span></a>
                                        Retrieve all genotype-phenotype associations for a specific phenotyping center</h3>
                                    <p>We will constrain the results by adding a condition to the <strong>q</strong> (query) parameter using the specific 
                                        <strong>phenotyping_center</strong> field. To retrieve all MP associations to "WTSI" (Wellcome Trust Sanger Institute)
                                        phenotyping centre ,specify <strong>q=phenotyping_center:"WTSI"</strong></p>
                                    <div class="highlight highlight-bash">
                                        <pre>
curl <span class="se">\</span>
    --basic  <span class="se">\</span>
    -X GET <span class="se">\</span>
    <span class="s1">'http://www.ebi.ac.uk/mi/impc/solr/genotype-phenotype/select?q=phenotyping_center:"WTSI"&amp;wt=json'</span>
                                        </pre>
                                    </div>

                                    <h3>
                                        <a name="user-content-get-the-phenotyping-resource-names"
                                           class="anchor"
                                           href="#get-the-phenotyping-resource-names">
                                            <span class="octicon octicon-link"></span></a>
                                        Get the phenotyping resource names</h3>
                                    <p>We will start by a simple request to get the different phenotyping resource names (EuroPhenome, MGP, IMPC).
                                        This will be the basis to filter historical phenotyping resources like EuroPhenome or active resource like 
                                        IMPC project.</p>
                                    <p>There are two basic facts you should know about Solr. Solr queries are based on filters and facets.
                                        Using facets enables the retrieval of distinct values from a specific field. In this example, we
                                        want to retrieve the distinct phenotyping resource names. Filtering enables us to sub-select specific
                                        fields we want to retrieve and all the fields from a Solr document. In this example, the fields we are
                                        interested in are 'resource_name' and 'resource_fullname'.</p>
                                    <div class="highlight highlight-bash">
                                        <pre>
curl <span class="se">\</span>
    --basic  <span class="se">\</span>
    -X GET <span class="se">\</span>
    <span class="s1">'http://www.ebi.ac.uk/mi/impc/solr/genotype-phenotype/select/?q=*:*&amp;version=2.2&amp;start=0&amp;rows=0&amp;indent=on&amp;wt=json&amp;fl=resource_name&amp;fl=resource_fullname&amp;facet=on&amp;facet.field=resource_fullname&amp;facet.field=resource_name'</span>
                                        </pre>
                                    </div>
                                    <p>If you look carefully at the request:</p>
                                    <ul class="task-list">
                                        <li>parameter <strong>fl</strong> means 'filter': we want to filter the results and keep only resource_fullname and resource-name fields</li>
                                        <li>parameter <strong>facet=on</strong> means we want to have faceted results</li>
                                        <li>parameter <strong>facet.field</strong> means we are looking at all the possible combination of resource_name and resource_fullname</li>
                                        <li>parameter <strong>q</strong> is the query parameter. q=* means we are not doing any text matching and want to get all the resource name / fullname results.</li>
                                    </ul><p>We will look into more advanced examples and how to use the query parameter 'q'.</p>

                                    <h3>
                                        <a name="user-content-retrieve-all-the-phenotyping-projects"
                                           class="anchor"
                                           href="#retrieve-all-the-phenotyping-projects">
                                            <span class="octicon octicon-link"></span></a>
                                        Retrieve all the phenotyping projects</h3>
                                    <p>This is the same principle. Only the selected field changes. In this case, use the field 'project_name' or/and 'project_fullname'.</p>
                                    <div class="highlight highlight-bash">
                                        <pre>
curl <span class="se">\</span>
    --basic  <span class="se">\</span>
    -X GET <span class="se">\</span>
    <span class="s1">'http://www.ebi.ac.uk/mi/impc/solr/genotype-phenotype/select/?q=*:*&amp;version=2.2&amp;start=0&amp;rows=0&amp;indent=on&amp;wt=json&amp;fl=project_name&amp;facet=on&amp;facet.field=project_name'</span>
                                        </pre>
                                    </div>

                                    <h3>
                                        <a name="user-content-retrieve-all-pipelines-from-a-specific-project"
                                           class="anchor"
                                           href="#retrieve-all-pipelines-from-a-specific-project">
                                            <span class="octicon octicon-link"></span></a>
                                        Retrieve all pipelines from a specific project
                                    </h3>
                                    <p>To retrieve all the phenotyping pipelines from EUMODIC, we'll use the <strong>fq</strong> (filter query)
                                        parameter to filter the query on project_name:EUMODIC.
                                        We are only interested at the distinct pipeline names and we'll use the <strong>facet.field</strong>
                                        parameter to facet on 'pipeline_name'.</p>
                                    <div class="highlight highlight-bash">
                                        <pre>
curl <span class="se">\</span>
    --basic  <span class="se">\</span>
    -X GET <span class="se">\</span>
    <span class="s1">'http://www.ebi.ac.uk/mi/impc/solr/genotype-phenotype/select?q=*:*&amp;fq=project_name:EUMODIC&amp;rows=0&amp;fl=project_name,pipeline_name&amp;facet=on&amp;facet.field=pipeline_name&amp;facet.mincount=1&amp;wt=json'</span>
                                        </pre>
                                    </div>

                                    <h3>
                                        <a name="user-content-retrieve-all-procedures-from-a-specific-pipeline"
                                           class="anchor"
                                           href="#retrieve-all-procedures-from-a-specific-pipeline">
                                            <span class="octicon octicon-link"></span></a>
                                        Retrieve all procedures from a specific pipeline
                                    </h3>
                                    <p>Again, we'll use the <strong>fq</strong> command to filter the query on pipeline_name using double-quotes
                                        and select <strong>facet.field</strong> called <strong>procedure_name</strong>.</p>
                                    <div class="highlight highlight-bash">
                                        <pre>
curl <span class="se">\</span>
    --basic  <span class="se">\</span>
    -X GET <span class="se">\</span>
    <span class="s1">'http://www.ebi.ac.uk/mi/impc/solr/genotype-phenotype/select?q=*:*&amp;fq=pipeline_name:"EUMODIC Pipeline 1"&amp;rows=0&amp;fl=procedure_name,pipeline_name&amp;facet=on&amp;facet.field=procedure_name&amp;facet.mincount=1&amp;wt=json'</span>
                                        </pre>
                                    </div>

                                    <h3>
                                        <a name="user-content-retrieve-all-parameters-from-a-specific-procedure"
                                           class="anchor" href="#retrieve-all-parameters-from-a-specific-procedure">
                                            <span class="octicon octicon-link"></span></a>
                                        Retrieve all parameters from a specific procedure
                                    </h3>
                                    <div class="highlight highlight-bash">
                                        <pre>
curl <span class="se">\</span>
    --basic  <span class="se">\</span>
    -X GET <span class="se">\</span>
    <span class="s1">'http://www.ebi.ac.uk/mi/impc/solr/genotype-phenotype/select?q=*:*&amp;fq=pipeline_name:"EUMODIC Pipeline 1"&amp;fq=procedure_name:"Non-Invasive blood pressure"&amp;rows=0&amp;fl=procedure_name,parameter_name&amp;facet=on&amp;facet.field=parameter_name&amp;facet.mincount=1&amp;wt=json'</span>
                                        </pre>
                                    </div>

                                    <h3>
                                        <a name="user-content-retrieve-all-mp-calls-grouped-by-top-level-mp-terms-first-and-then-by-resources-mgp-europhenome" class="anchor" href="#retrieve-all-mp-calls-grouped-by-top-level-mp-terms-first-and-then-by-resources-mgp-europhenome"><span class="octicon octicon-link"></span></a>Retrieve all MP calls grouped by top level MP terms first and then by resources (MGP, EuroPhenome)</h3>

                                    <div class="highlight highlight-bash">
                                        <pre>
curl <span class="se">\</span>
    --basic <span class="se">\</span>
    -x GET <span class="se">\</span>
    <span class="s1">'http://www.ebi.ac.uk/mi/impc/solr/genotype-phenotype/select/?q=*:*&amp;version=2.2&amp;start=0&amp;rows=0&amp;indent=on&amp;wt=json&amp;fq=-resource_name:%22IMPC%22&amp;fl=top_level_mp_term_name&amp;facet=on&amp;facet.pivot=top_level_mp_term_name,resource_name'</span>
                                        </pre>
                                    </div>
                                </div><%-- end of content div--%>
                        </div>
                    </div>
                </div>
            </div>

        </jsp:body>

    </t:genericpage>