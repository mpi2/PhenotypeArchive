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

                            
                            <h3>Explore the diverse entry points to mouse phenotype data.</h3>
							Currently IMPC portal supports 6 main data types on the search page.
							Ie, Genes, Phenotypes, Diseases, Anatomy, Procedures and Images. 
							These are the main facets designed for our facet search interface.<br>
							Each main data type has sub data types which act as subfacet filters to allow for data filtering.<p>
							
							<h4><a name="mainFaceting" href='#'>Main data type browsing</a></h4>
                            <div>Clicking on a main facet (eg, Genes, Diseases) without ticking its subfacet filter(s) will display all records of that data type.</div><br>
                            <img src='img/main_data_type.png' /><p>

							<h4><a name="subFaceting" href='#'>Cross data type browsing</a></h4>
                            <div>By checking a checkbox (or subfacet filter), the main facet and subfacet counts of all 6 main data types will be updated accordingly.
    						Eg. when 'Genes' main facet is expanded and 'Started' subfacet filter is checked, the main facet counts relevant to a main facet entail 
    						how many phenotypes or diseases , for instance, are annotated to the genes with the IMPC phenotyping status marked as 'Started'.</div><br> 
    						<img src='img/sub_data_type.png' /><p>
    						<div>The subfacet counts of a main facet tell you which sub data type of a main facet is relevant to the 'Started' filter.<br>
    						To see this, click on the 'Phenotypes' main facet, for example, to expand its subfacets.<br>
    						Note that the 'Genes' main facet will then be closed.</div><br>
    						<img src='img/phenotype_subfacets.png' /><p><p>
    						<div>The ticked filter will also appear in a 'filter summary' box right below the big blue 'Filter your search' bar on top left corner of the search page.
    						Now you should have seen 'Phenotyping started'.<br>
    						There, you can also remove individual filter(s) - same as when you untick a checkbox of a subfacet - or remove all filters in one go with the 'Remove all facet filters' link.</div><p>
                                                     
                            
                            <h4><a name="quick_gene_srch" href='#'>Quick Gene Search</a></h4>			
                            <div>Type a gene symbol, gene ID, name or human orthologue into search box and returned results will returned relevant gene pages.
                                <p>Eg. search by gene symbol "mtf1":
                            </div><br>			
                            <img src='img/quick_gene_search.png' /><p>

                            <h4><a name="quick_pheno_srch" href='#'>Quick Phenotype Search</a></h4>
                            <div>Type an abnormal phenotype or MP:ID, click on phenotype facet on the left panel, and the results grid will return relevant phenotype pages.
                                <p>Eg. search by phenotype "glucose":<br>
                            </div><br> 
                            <img src='img/quick_phenotype_search.png' /><p>

                            <h4><a name="quick_disease_srch" href='#'>Quick Disease Search</a></h4>
                            <div>Type a (partial) disease name or ID (OMIM, Orphanet or DECIPHER), click on the disease facet and the the results grid will return relevant disease pages.
                                <p>Eg. Search for "cardiac" diseases:<br>
                            </div><br> 
                            <img src='img/disease_search.png' /><p>

                            <h4><a name="quick_anatomy_srch" href='#'>Quick Anatomy Search</a></h4>
                            <div>Type an anatomical entity or MA:ID, click on the anatomy facet on the on the left panel, and the results grid will return relevant anatomy pages.
                                <p>Eg. search by anatomy "eye":<br>
                            </div><br> 
                            <img src='img/quick_anatomy_search.jpg' /><p>

                            <h4><a name="quick_param_srch" href='#'>Quick Assay Search</a></h4>
                            <div>Type an assay, parameter or IMPReSS ID, click on the procedure facet on the left panel, and the results grid will return relevant parameter pages. 
                                <p>Eg. search by parameter "grip strength":<br>
                            </div><br> 
                            <img src='img/quick_param_search.jpg' /><p>	


                            <h4><a name="quick_img_srch" href='#'>Quick Image Search</a></h4>
                            <div>Type a gene, phenotype, assay, or anatomical entity, click on the images facet on the left panel and the results grid will return relevant image pages.
                                <p>By default, Image View will be displayed, where annotations to an image is enlisted.<p>Eg. search by phenotype "immune":<br>				
                            </div><br> 
                            <img src='img/quick_img_search.jpg' /><p>

                            <p>To view images grouped by annotations, simply click on the "Show Annotation View" button to the top-right corner of the results grid. The label of the same button will then be switched to "Show Image View" so that you can toggle the views.</p><br>
                            <img src='img/quick_img_search_annotView.jpg' /><p>
                                                   

                            <h4><a name="export" href='#'>Data Export of Search Results</a></h4>
                            <div>Click on the export icon <img src='img/export.jpg' /> to the top-right corner of the results grid to expand or hide it. 
                                When expanded, <img src='img/export_expanded.jpg' /><p><p>
                                    click on either TSV (tab separated) or XLS (MS Excel) buttons for format of export. 
                                <p>To limit the export data on current page only, choose the buttons of the "Current paginated entries in table", 
                                    whereas "All entries in table" will export the whole search results".
                                <br>A warning message dialog box will be displayed if the dataset is large and download can take longer depending on your network speed.</div>			

                           

                        </div><%-- end of content div--%>
                    </div>
                </div>
            </div>
        </div>
   
    </jsp:body>
  
</t:genericpage>
