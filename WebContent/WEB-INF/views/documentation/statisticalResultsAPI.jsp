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
                <tr>
                    <td>coming soon</td>
                    <td>n/a</td>
                    <td>coming soon</td>
                </tr>
            </tbody>
        </table>

        <blockquote>
            <p>(1) - For unidimensional parameters (2) - For multidimensional parameters (3) - For time series parameters (4) - For categorical parameters (5) - For metadata parameters</p>
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