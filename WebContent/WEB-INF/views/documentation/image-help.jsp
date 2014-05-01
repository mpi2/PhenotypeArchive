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


                            <h3>More information about the way IMPC uses images.</h3>


                                <h4><a name="explore" href='#'>Explore Image Data</a></h4>
                                <p>The IMPC portal offers images that are annotated with gene associations, Mouse Anatomy (MA) and Mammalian Phenotype (MP) terms. To search for images associated with the gene symbol ""Akt2", type Akt2 into the search box at the top of the page and then click on the images link at the side.
                                    A list with categories of images associated to the Akt2 is then displayed on the left. Click on these categories to see sub-categories and their respective counts of images associated with that category. You can also search for genes using MGI identifiers. 
                                    To search for images associated to an anotomy term you can use MA identifiers or terms such as  blood vessel (MA:0000060) in the search box. Click <a href="#imported">here</a>  to find out more where the images are from and how they are annotated.
                                    <img src="img/image_facet.png">
                                </p>

                                <h4><a name="imported" href='#'>Imported Images</a> </h4>
                                <p>The IMPC portal offers images that are annotated with Mouse Annatomy (MA) and Mammalian Phenotype (MP) terms. Currently images and their annotations are from legacy data from the MGP project at the Wellcome Trust Sanger Institute where the terms were manually annotated by researchers.
                                    However in the future, the portal will also contain images from the IMPC standardised screens <a href="https://www.mousephenotype.org/impress">https://www.mousephenotype.org/impress</a>. The procedures highlighted in red will have image data collected.
                                    <img src="img/pipelines_with_images.png">
                                </p>

                                <h4><a name="update_frequency" href='#'>How often are images updated?</a></h4>
                                <p>
                                    Images from the MGP resource at Wellcome Trust Sanger Institute are updated as needed. The process of obtaining images from other IMPC centres is still being developed.
                                </p>

                                <h4><a name="download" href='#'>How do I get image data/ download?</a></h4>
                                <p>Individual images may be downloaded by right clicking and saving to your hard drive. Batch downloading of images is in development.</p>


                       </div>
                    </div>
                </div>
            </div>
        </div>
   
    </jsp:body>
  
</t:genericpage>
