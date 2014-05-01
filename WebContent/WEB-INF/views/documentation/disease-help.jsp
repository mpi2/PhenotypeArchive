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

                         <h3>More information about the way IMPC uses disease data.</h3>

                            <h4><a name="explore" href='#'>Explore Disease Data</a></h4>
                            <p>Type a (partial) disease name or ID (OMIM, Orphanet or DECIPHER), click on the disease facet and the the results grid will return relevant disease pages. 
                                Eg. Search for "cardiac" diseases:
                                Click <a href="#details">here</a>  to find out more where the disease data comes from and how it is created.
                                <img src="img/disease_search.png">
                            </p>

                            <h4><a name="details" href='#'>Disease details pages</a> </h4>
                            <p>The IMPC disease details page contains known gene associations and mouse models 
                                for the disease as well as predicted gene candidates and mouse models based on the
                                phenotypic similarity of the disease clinical symptoms and the mouse phenotype 
                                annotations. The latter uses data from both the MGI curated dataset as well as
                                high-throughput phenotype assignments from the IMPC pipeline. 
                                <img src="img/disease_details.png">
                            </p>    

                            <h4><a name="phenodigm" href='#'>PhenoDigm</a> </h4>
                            <p>                        
                                We use our <a href=www.sanger.ac.uk/resources/databases/phenodigm> PhenoDigm</a> 
                                algorithm to calculate a percentage similarity score where the best possible mouse
                                model match to a disease would score 100%. In this interface, we only display
                                high-scoring (> 60%) matches that show a reasonable phenotypic similarity,
                                with the exception of the known gene associations where we display results for
                                all possible mouse models.
                            </p>	  

                        </div><%-- end of content div--%>
                    </div>
                </div>
            </div>
        </div>
   
    </jsp:body>
  
</t:genericpage>
