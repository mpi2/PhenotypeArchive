<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:genericpage>
    <jsp:attribute name="title">Gene details for ${gene.name}</jsp:attribute>
    <jsp:attribute name="breadcrumb">&nbsp;&raquo; <a href="${baseUrl}/search#q=*:*&facet=gene">Genes</a> &raquo; ${gene.symbol}</jsp:attribute>
    <jsp:attribute name="bodyTag"><body  class="gene-node no-sidebars small-header"></jsp:attribute>
        <jsp:attribute name="addToFooter">
            <!--  start of floating menu for genes page -->
            <div class="region region-pinned">

                <div id="flyingnavi" class="block">

                    <a href="#top"><i class="fa fa-chevron-up" title="scroll to top"></i></a>

                    <ul>
                        <li><a href="#top">Gene</a></li>
                        <li><a href="#section-associations">Phenotype Associations</a></li><!--  always a section for this even if says no phenotypes found - do not putting in check here -->
                            <c:if test="${phenotypeStarted}">
                            <li><a href="#heatmap">Heatmap</a></li>
                            </c:if>
                            <c:if test="${not empty solrFacets}">
                            <li><a href="#section-images">Associated Images</a></li>
                            </c:if>
                            <c:if test="${not empty expressionFacets}">
                            <li><a href="#section-expression">Expression</a></li>
                            </c:if>
                            <c:if test="${not empty impcExpressionImageFacets}">
                            <li><a href="#section-impc_expression">Expression</a></li>
                            </c:if>
                            <c:if test="${not empty impcImageFacets}">
                            <li><a href="#section-impc-images">Impc Images</a></li>
                            </c:if>
                            <c:if test="${not empty orthologousDiseaseAssociations}">
                            <li><a href="#section-disease-models">Disease Models</a></li>
                            </c:if>
                            <c:if test="${not empty phenotypicDiseaseAssociations}">
                            <li><a href="#section-potential-disease-models">Potential Disease Models</a></li>
                            </c:if><c:if test="${!countIKMCAllelesError}">
                            <li><a href="#order2">Order Mouse and ES Cells</a></li>
                            </c:if>
                    </ul>

                    <div class="clear"></div>

                </div>

            </div>
            <!--  end of floating menu for genes page -->

            <c:if test="${phenotypeStarted}">
                <script type="text/javascript" src="${drupalBaseUrl}/heatmap/js/heatmap.1.3.1.js"></script>
                <!--[if !IE]><!-->
                <script>
                    dcc.heatmapUrlGenerator = function (genotype_id, type) {
                        return '${drupalBaseUrl}/phenoview?gid=' + genotype_id + '&qeid=' + type;
                    };
                </script>
                <!--<![endif]-->
                <!--[if gte IE 9]>
                <script>
                    dcc.heatmapUrlGenerator = function(genotype_id, type) {
                       return '${drupalBaseUrl}/phenoview?gid=' + genotype_id + '&qeid=' + type;
                    };
                </script>
                <![endif]-->
                <script>
                    //new dcc.PhenoHeatMap('procedural', 'phenodcc-heatmap', 'Fam63a', 'MGI:1922257', 6, '//dev.mousephenotype.org/heatmap/rest/heatmap/');
                    new dcc.PhenoHeatMap({
                        /* identifier of <div> node that will host the heatmap */
                        'container': 'phenodcc-heatmap',
                        /* colony identifier (MGI identifier) */
                        'mgiid': '${gene.id.accession}',
                        /* default usage mode: ontological or procedural */
                        'mode': 'ontological',
                        /* number of phenotype columns to use per section */
                        'ncol': 5,
                        /* heatmap title to use */
                        'title': '${gene.symbol}',
                        'url': {
                            /* the base URL of the heatmap javascript source */
                            'jssrc': '${fn:replace(drupalBaseUrl, "https:", "")}/heatmap/js/',
                            /* the base URL of the heatmap data source */
                            'json': '${fn:replace(drupalBaseUrl, "https:", "")}/heatmap/rest/',
                            /* function that generates target URL for data visualisation */
                            'viz': dcc.heatmapUrlGenerator
                        }
                    });
                </script>
            </c:if>

        </jsp:attribute>


        <jsp:attribute name="header">


            <!-- CSS Local Imports -->
            <!-- link rel="stylesheet" type="text/css" href="${baseUrl}/css/ui.dropdownchecklist.themeroller.css"/-->

            <!-- JavaScript Local Imports -->
            <link rel="stylesheet" href="${baseUrl}/css/vendor/jquery.ui/jquery.ui.core.css">
            <script src="${baseUrl}/js/general/enu.js"></script>
            <script src="${baseUrl}/js/general/dropdownfilters.js"></script>
            <script type="text/javascript" src="${baseUrl}/js/general/allele.js"></script>

<!-- from tab tutorial -->
<!-- <link rel="stylesheet" href="//code.jquery.com/ui/1.11.4/themes/smoothness/jquery-ui.css"> -->
<%-- <link rel="stylesheet" href="${baseUrl}/css/vendor/jquery.ui/jquery.ui.core.css"> --%>
  <!-- <script src="//code.jquery.com/jquery-1.10.2.js"></script>
  <script src="//code.jquery.com/ui/1.11.4/jquery-ui.js"></script> -->
  
  
            <script type="text/javascript">var gene_id = '${acc}';
            
            $(function() {
            	console.log('calling tabs now');
                $( "#tabs" ).tabs();
                
                $('ul.tabs li a').click(function(){
             	   $('ul.tabs li a').css({'border-bottom':'none', 'background-color':'#F4F4F4', 'border':'none'});
             	   $(this).css({'border':'1px solid #666', 'border-bottom':'1px solid white', 'background-color':'white', 'color':'#666'});
                });
                
                $('ul.tabs li:nth-child(1) a').click();  // activate this by default
              });
            </script>
            <style>
                #svgHolder div div {z-index:100;}
                span.direct {color: #698B22;} 
                span.indirect {color: #CD8500;}  
                
                /* copied from CKs gwas page */
                div#mk {
        		margin-bottom: 10px;
        	}
        	span#mkLeft {
        		font-size: 20px;
        	}
        	span#mkLeft a {
        		text-decoration: none;
        	}
        	span#mkRight {
        		float: right;
        	}
        	div#tabs {
        		border: none;
        	}
        	ul.tabs {
        		border: none;
        		border-bottom: 1px solid #666;
        		background: none;
        	}
        	ul.tabs li:nth-child(1) {
        		font-size: 14px;
        	}
        	ul.tabs li a {
        		margin-bottom: -1px;
        		border: 1px solid #666;
        		font-size: 12px;
        	}
        	ul.tabs li a:hover {
        		color: white;
        		background-color: gray; 
        	}
        	div#tabs table {
        		font-size: 14px;
        		color: #666;
        	}
        	div.ui-tabs-panel {
        		padding: 0 !important;
        	}
        	table.dataTable caption {
        		font-weight: bold;
        		padding: 3px 5px;
        		background-color: #F2F2F2;
        		border-radius: 5px;
        	}
        	table.dataTable td a {
        		text-decoration: none;
        		color: #0978a1;
        	}
        	.trtName {
        		font-family: Arial !important;
        		font-weight: bold !important;
        		font-size: 16px !important;
        		color: black;
        	}
        	span.direct {color: #698B22;} 
            span.indirect {color: #CD8500;}
            
            div.dataTables_info {
            	font-size: 14px;
            	padding-bottom: 20px !important;
            }
            div.dataTables_filter {
            	font-size: 14px;
            }
            table.dataTable span.highlight {
                background-color: yellow;
                font-weight: bold;
                color: black;
            }
            div#toolBox {
            	font-size: 12px;
            }
            div#tableTool {
                position: relative;
                top: -70px;
                float: right;
            }
            form#dnld {
            	padding: 5px;
            	border-radius: 5px;
			    background: #F2F2F2;
            }
            form#dnld button {
            	text-decoration: none;
            }
                     
            </style>

            <c:if test="${phenotypeStarted}">
                <!--[if !IE]><!-->
                <link rel="stylesheet" type="text/css" href="${drupalBaseUrl}/heatmap/css/heatmap.1.3.1.css"/>
                <!--<![endif]-->
                <!--[if IE 8]>
                <link rel="stylesheet" type="text/css" href="${drupalBaseUrl}/heatmap/css/heatmapIE8.1.3.1.css">
                <![endif]-->
                <!--[if gte IE 9]>
                <link rel="stylesheet" type="text/css" href="${drupalBaseUrl}/heatmap/css/heatmap.1.3.1.css">
                <![endif]-->
            </c:if>

        </jsp:attribute>

        <jsp:body>
            <div class="region region-content">
                <div class="block">
                    <div class="content">
                        <div class="node node-gene">
                            <h1 class="title" id="top">Gene: ${gene.symbol}</h1>
                            <div class="section">
                                <div class="inner">
                                    <!--  login interest button -->
                                    <div class="floatright">
                                        <c:choose>
                                            <c:when test="${registerButtonAnchor!=''}">
                                                <p> <a class="btn" href='${registerButtonAnchor}'><i class="fa fa-sign-in"></i>${registerInterestButtonString}</a></p>
                                                    </c:when>
                                                    <c:otherwise>
                                                <p> <a class="btn interest" id='${registerButtonId}'><i class="fa fa-sign-in"></i>${registerInterestButtonString}</a></p>
                                                    </c:otherwise>
                                                </c:choose>
                                                <c:if test="${orderPossible}">
                                            <p> <a class="btn" href="#order2"> <i class="fa fa-shopping-cart"></i> Order </a> </p>
                                        </c:if>
                                    </div>

                                    <p class="with-label no-margin">
                                        <span class="label">Name</span>
                                        ${gene.name}
                                    </p>

                                    <c:if test="${!(empty gene.synonyms)}">
                                        <p class="with-label no-margin">
                                            <span class="label">Synonyms</span>
                                            <c:forEach var="synonym" items="${gene.synonyms}" varStatus="loop">
                                                ${synonym.symbol}
                                                <c:if test="${!loop.last}">, </c:if>
                                                <c:if test="${loop.last}"></c:if>
                                            </c:forEach>
                                        </p>
                                    </c:if>

                                    <p class="with-label">
                                        <span class="label">MGI Id</span>
                                        <a href="http://www.informatics.jax.org/marker/${gene.id.accession}">${gene.id.accession}</a>
                                    </p>

                                    <c:if test="${!(prodStatusIcons == '')}">
                                        <p class="with-label">
                                            <span class="label">Status</span>
                                            ${prodStatusIcons}
                                        </p>
                                    </c:if>
                                    <p class="with-label">
                                        <span class="label">ENSEMBL Links</span>
                                        <a href="http://www.ensembl.org/Mus_musculus/Gene/Summary?g=${gene.id.accession}"><i class="fa fa-external-link"></i>&nbsp;Gene&nbsp;View</a>&nbsp;&nbsp;
                                        <a href="http://www.ensembl.org/Mus_musculus/Location/View?g=${gene.id.accession};contigviewbottom=das:http://das.sanger.ac.uk/das/ikmc_products=labels"><i class="fa fa-external-link"></i>&nbsp;Location&nbsp;View</a>&nbsp;&nbsp;     
                                        <a href="http://www.ensembl.org/Mus_musculus/Location/Compara_Alignments/Image?align=677;db=core;g=${gene.id.accession}"><i class="fa fa-external-link"></i>&nbsp;Compara&nbsp;View</a> 
                                    </p>

 									
                                    <c:if test="${gwasPhenoMapping != null }">
                                    	
                                       	<c:if test="${gwasPhenoMapping == 'no mapping' }">
                               	 			<p class="with-label">
                                   				<span class="label">GWAS mapping</span>
                                   				<a href="http://www.ebi.ac.uk/gwas/search?query=${gene.symbol}"><i class="fa fa-external-link"></i>&nbsp;GWAS catalog</a>&nbsp;&nbsp;
                               				</p>
                               			</c:if>	
                               			<c:if test="${gwasPhenoMapping == 'indirect' }">
                               	 			<p class="with-label">
                                   				<span class="label">GWAS mapping</span>
                                   				<a href="http://www.ebi.ac.uk/gwas/search?query=${gene.symbol}"><i class="fa fa-external-link"></i>&nbsp;GWAS catalog</a>&nbsp;&nbsp;
                                   				<a href="${baseUrl}/phenotype2gwas?symbol=${gene.symbol}"><i class="fa fa-external-link"></i>&nbsp;<span class='indirect'>${gwasPhenoMapping} phenotypic mapping</span></a>&nbsp;&nbsp;
                                   				
                               				</p>
                               			</c:if>
                               			<c:if test="${gwasPhenoMapping == 'direct' }">
                               	 			<p class="with-label">
                                   				<span class="label">GWAS mapping</span>
                                   				<a href="http://www.ebi.ac.uk/gwas/search?query=${gene.symbol}"><i class="fa fa-external-link"></i>&nbsp;GWAS catalog</a>&nbsp;&nbsp;
                                   				<a href="${baseUrl}/phenotype2gwas?symbol=${gene.symbol}"><i class="fa fa-external-link"></i>&nbsp;<span class='direct'>${gwasPhenoMapping} phenotypic mapping</span></a>&nbsp;&nbsp;
                               				</p>
                               			</c:if>
                                   		
                               		</c:if>

                                    <p><a href="../genomeBrowser/${acc}" target="new"> Gene Browser</a><span id="enu"></span>
                                    </p>

                                </div>	

                            </div><!-- section end -->

                            <!--  Phenotype Associations Panel -->
                            <div class="section">

                                <h2 class="title " id="section-associations"> Phenotype associations for ${gene.symbol} 
                                    <!-- <span class="documentation" > <a href='' id='mpPanel'><i class="fa fa-question-circle pull-right"></i></a></span>-->
                                    <span class="documentation" ><a href='' id='mpPanel' class="fa fa-question-circle pull-right"></a></span> <!--  this works, but need js to drive tip position -->
                                </h2>		

                                <div class="inner">
                                    <c:choose>
                                        <c:when test="${summaryNumber > 0}">
                                            <div class="abnormalities">
                                                <div class="allicons"></div>

                                                <div class="no-sprite sprite_embryogenesis_phenotype" data-hasqtip="27" title="embryogenesis phenotype"></div>
                                                <div class="no-sprite sprite_reproductive_system_phenotype" data-hasqtip="27" title="reproductive system phenotype"></div>
                                                <div class="no-sprite sprite_mortality_aging" data-hasqtip="27" title="mortality/aging"></div>
                                                <div class="no-sprite sprite_growth_size_body_phenotype" data-hasqtip="27" title="growth/size/body phenotype"></div>
                                                <div class="no-sprite sprite_homeostasis_metabolism_phenotype_or_adipose_tissue_phenotype" data-hasqtip="27" title="homeostasis/metabolism phenotype or adipose tissue phenotype"></div>

                                                <div class="no-sprite sprite_behavior_neurological_phenotype_or_nervous_system_phenotype" data-hasqtip="27" title="behavior/neurological phenotype or nervous system phenotype"></div>
                                                <div class="no-sprite sprite_cardiovascular_system_phenotype" data-hasqtip="27" title="cardiovascular system phenotype"></div>
                                                <div class="no-sprite sprite_respiratory_system_phenotype" data-hasqtip="27" title="respiratory system phenotype"></div>
                                                <div class="no-sprite sprite_digestive_alimentary_phenotype_or_liver_biliary_system_phenotype" data-hasqtip="27" title="digestive/alimentary phenotype or liver/biliary system phenotype"></div>
                                                <div class="no-sprite sprite_renal_urinary_system_phenotype" data-hasqtip="27" title="renal/urinary system phenotype"></div>

                                                <div class="no-sprite sprite_limbs_digits_tail_phenotype" data-hasqtip="27" title="limbs/digits/tail phenotype"></div>
                                                <div class="no-sprite sprite_skeleton_phenotype" data-hasqtip="27" title="skeleton phenotype"></div>
                                                <div class="no-sprite sprite_immune_system_phenotype_or_hematopoietic_system_phenotype" data-hasqtip="27" title="immune system phenotype or hematopoietic system phenotype"></div>
                                                <div class="no-sprite sprite_muscle_phenotype" data-hasqtip="27" title="muscle phenotype"></div>
                                                <div class="no-sprite sprite_integument_phenotype_or_pigmentation_phenotype" data-hasqtip="27" title="integument phenotype or pigmentation phenotype"></div>

                                                <div class="no-sprite sprite_craniofacial_phenotype " data-hasqtip="27" title="craniofacial phenotype"></div>
                                                <div class="no-sprite sprite_hearing_vestibular_ear_phenotype " data-hasqtip="27" title="hearing/vestibular/ear phenotype"></div>
                                                <div class="no-sprite sprite_taste_olfaction_phenotype " data-hasqtip="27" title="taste/olfaction phenotype"></div>
                                                <div class="no-sprite sprite_endocrine_exocrine_gland_phenotype " data-hasqtip="27" title="endocrine/exocrine gland phenotype"></div>
                                                <div class="no-sprite sprite_vision_eye_phenotype" data-hasqtip="27" title="vision/eye phenotype"></div>

                                                <c:forEach var="group" items="${topLevelMpGroups}">
                                                    <c:if test="${group != 'mammalian phenotype' }">
                                                        <div class="sprite sprite_${group.replaceAll(' |/', '_')}" data-hasqtip="27" title="${group}"></div>
                                                    </c:if>
                                                </c:forEach>

                                            </div>

                                            <p> Phenotype Summary based on automated MP annotations supported by experiments on knockout mouse models. </p>
                                            <c:forEach var="zyg" items="${phenotypeSummaryObjects.keySet()}">
                                                <p>In <b>${zyg} :</b></p>
                                                <ul>
                                                    <c:if test='${phenotypeSummaryObjects.containsKey(zyg) && phenotypeSummaryObjects.get(zyg).getBothPhenotypes().size() > 0}'>
                                                        <li><p> <b>Both sexes</b> have the following phenotypic abnormalities</p>
                                                            <ul>
                                                                <c:forEach var="summaryObj" items='${phenotypeSummaryObjects.get(zyg).getBothPhenotypes()}'>
                                                                    <li><a href="${baseUrl}/phenotypes/${summaryObj.getId()}">${summaryObj.getName()}</a>. Evidence from <c:forEach var="evidence" items="${summaryObj.getDataSources()}" varStatus="loop"> ${evidence} <c:if test="${!loop.last}">,&nbsp;</c:if>  </c:forEach> &nbsp;&nbsp;&nbsp; (<a class="filterTrigger" id="${summaryObj.getName()}">${summaryObj.getNumberOfEntries()}</a>)</li>    
                                                                    </c:forEach>
                                                            </ul>
                                                        </li>
                                                    </c:if>

                                                    <c:if test='${phenotypeSummaryObjects.containsKey(zyg) && phenotypeSummaryObjects.get(zyg).getFemalePhenotypes().size() > 0}'>
                                                        <li><p> Following phenotypic abnormalities occured in <b>females</b> only</p>
                                                            <ul>
                                                                <c:forEach var="summaryObj" items='${phenotypeSummaryObjects.get(zyg).getFemalePhenotypes()}'> 
                                                                    <li><a href="${baseUrl}/phenotypes/${summaryObj.getId()}">${summaryObj.getName()}</a>. Evidence from <c:forEach var="evidence" items="${summaryObj.getDataSources()}" varStatus="loop"> ${evidence} <c:if test="${!loop.last}">,&nbsp;</c:if> </c:forEach> &nbsp;&nbsp;&nbsp; (<a class="filterTrigger" id="${summaryObj.getName()}">${summaryObj.getNumberOfEntries()}</a>)</li>                
                                                                    </c:forEach>
                                                            </ul>
                                                        </li>
                                                    </c:if>

                                                    <c:if test='${phenotypeSummaryObjects.containsKey(zyg) && phenotypeSummaryObjects.get(zyg).getMalePhenotypes().size() > 0}'>
                                                        <li><p> Following phenotypic abnormalities occured in <b>males</b> only</p>
                                                            <ul>
                                                                <c:forEach var="summaryObj" items='${phenotypeSummaryObjects.get(zyg).getMalePhenotypes()}'>
                                                                    <li><a href="${baseUrl}/phenotypes/${summaryObj.getId()}">${summaryObj.getName()}</a>. Evidence from <c:forEach var="evidence" items="${summaryObj.getDataSources()}" varStatus="loop"> ${evidence} <c:if test="${!loop.last}">,&nbsp;</c:if> </c:forEach> &nbsp;&nbsp;&nbsp;   (<a class="filterTrigger" id="${summaryObj.getName()}">${summaryObj.getNumberOfEntries()}</a>)</li>    
                                                                    </c:forEach>
                                                            </ul>
                                                        </li>
                                                    </c:if>
                                                </ul>
                                            </c:forEach>

                                        </c:when>
                                        <c:otherwise>
                                            <div class="alert alert-info">There are currently no IMPC phenotype associations for the gene ${gene.symbol} </div> <br/>
                                        </c:otherwise>
                                    </c:choose>

                                    <c:if test='${hasPreQcData || summaryNumber > 0}'>
                                        <!-- Associations table -->
                                        <h5>Filter this table</h5>


                                        <div class="row-fluid">
                                            <div class="container span12">
                                                <br/>	
                                                <div class="row-fluid" id="phenotypesDiv">	

                                                    <div class="container span12">
                                                        <div id="filterParams" >
                                                            <c:forEach var="filterParameters" items="${paramValues.fq}">
                                                                ${filterParameters}
                                                            </c:forEach>
                                                        </div> 
                                                        <c:if test="${not empty phenotypes}">
                                                            <form class="tablefiltering no-style" id="target" action="destination.html">
                                                                <c:forEach var="phenoFacet" items="${phenoFacets}" varStatus="phenoFacetStatus">
                                                                    <select id="${phenoFacet.key}" class="impcdropdown" multiple="multiple" title="Filter on ${phenoFacet.key}">
                                                                        <c:forEach var="facet" items="${phenoFacet.value}">
                                                                            <option>${facet.key}</option>
                                                                        </c:forEach>
                                                                    </select> 
                                                                </c:forEach>
                                                                <div class="clear"></div>
                                                            </form>
                                                            <div class="clear"></div>

                                                            <c:set var="count" value="0" scope="page" />
                                                            <c:forEach var="phenotype" items="${phenotypes}" varStatus="status">
                                                                <c:forEach var="sex" items="${phenotype.sexes}"><c:set var="count" value="${count + 1}" scope="page"/></c:forEach>
                                                            </c:forEach>

                                                            <jsp:include page="PhenoFrag.jsp"></jsp:include>
                                                                <br/>
                                                                <div id="exportIconsDiv"></div>
                                                        </c:if>

                                                        <!-- if no data to show -->
                                                        <c:if test="${empty phenotypes}">
                                                            <div class="alert alert-info">Pre QC data has been submitted for this gene. Once the QC process is finished phenotype associations stats will be made available.</div>
                                                        </c:if>

                                                    </div>
                                                </div>
                                            </div>
                                        </div>

                                    </c:if>
                                    <!-- Show list of links to data for every center/pipeline/allele combination -->
                                    <c:if test="${!(empty dataMapList)}">
                                        <p class="with-label no-margin">
                                        <p class="no-margin">Browse all phenotype data for:</p>
                                        <ul>				
                                            <c:forEach var="dataMap" items="${dataMapList}" varStatus="loop">
                                                <li><a href='${baseUrl}/experiments/alleles/${dataMap["allele_accession_id"]}?phenotyping_center=${dataMap["phenotyping_center"]}&pipeline_stable_id=${dataMap["pipeline_stable_id"]}'><t:formatAllele>${dataMap["allele_symbol"]}</t:formatAllele></a> phenotyped by ${dataMap["phenotyping_center"]} using ${dataMap["pipeline_name"]} SOPs (<a href='${baseUrl}/phenome?phenotyping_center=${dataMap["phenotyping_center"]}&pipeline_stable_id=${dataMap["pipeline_stable_id"]}'>MP calls for all strains</a>).</li>					
                                            </c:forEach>
                                        </ul>
                                        </p>
                                    </c:if>	

                                </div>
                            </div>

                            <c:if test="${phenotypeStarted}">
                                <div class="section">
                                    <h2 class="title" id="heatmap">Pre-QC phenotype heatmap <span class="documentation" ><a href='' id='mpPanel' class="fa fa-question-circle pull-right"></a></span> <!--  this works, but need js to drive tip position -->
                                    </h2>

                                    <div class="inner">
                                        <div class="alert alert-info">
                                            <h5>Caution</h5>
                                            <p>These are the results of a preliminary statistical analysis. Data are still in the process of being quality controlled and results may change.</p>
                                        </div>
                                    </div>
                                    <div class="dcc-heatmap-root">
                                        <div class="phenodcc-heatmap" id="phenodcc-heatmap"></div>
                                    </div>
                                </div> <!-- section end -->
                            </c:if>

                            <c:if test="${not empty imageErrors}">
                                <div class="row-fluid dataset">
                                    <div class="alert"><strong>Warning!</strong>${imageErrors }</div>
                                </div>
                            </c:if>

<div class="section">
  <h2 class="title " id="impc-expression">Expression</h2>
	<div class="inner" style="display: block;">
		<c:if test="${not empty impcExpressionImageFacets}"> 
		<c:set var="expressionIcon" scope="page" value="fa fa-check"/>
		<c:set var="noTissueIcon" scope="page" value="fa fa-circle-o"/>
		<c:set var="noExpressionIcon" scope="page" value="fa fa-times"/>
		<c:set var="ambiguousIcon" scope="page" value="fa fa-circle"/>
		<c:set var="yesColor" scope="page" value="#0978a1"/>
		<c:set var="noColor" scope="page" value="gray"/>
		
		<span title="Expression" class="${expressionIcon}" style="color:${yesColor}">&nbspExpression</span>&nbsp&nbsp
		<span title="No Expression" class="${noExpressionIcon}" style="color:gray">&nbspNo Expression</span>&nbsp&nbsp
		<span title="No Tissue Available" class="${noTissueIcon}" style="color:gray">&nbspNo Tissue Available</span>&nbsp&nbsp
		<span title="Ambiguous" class="${ambiguousIcon}" style="color:gray">&nbspAmbiguous</span>&nbsp&nbsp
		
 							<!-- section for expression data here -->
 							<div id="tabs">
 							<ul class='tabs'>
							    <li><a href="#tabs-1">Expression Data Overview</a></li>
							    <li><a href="#tabs-2">Expression Images View</a></li>
							</ul>
							
							
							<div id="tabs-1" style="height: 500px; overflow: auto;">
                                
                                    
                                    <!-- <h2 class="title" id="section-impc_expression">Expression Overview<i class="fa fa-question-circle pull-right" title="Brief info about this panel"></i></h2>
                                    -->
                                     
                                     <table>
                                     <tr><th>Anatomy</th><th title="Number of heterozygous mutant specimens with data for the specified anatomy">#HET Specimens</th><th title="If there are images for homozygous specimens this value will be 'Yes'">HOM Images?</th><th title="Status of expression for Wild Type specimens from any colony with data for this anatomy">WT Expr</th><th title="">Mutant Expr</th><%-- <th>Mutant specimens</th> --%><th title="An clickable image icon will show if images are available for mutant specimens">Images</th></tr>
                                     	<c:forEach var="mapEntry" items="${expressionAnatomyToRow}">
                                     		<tr><td><a href="${baseUrl}/anatomy/${mapEntry.value.abnormalMaId}">${mapEntry.value.abnormalMaName}</a>
                                     				<c:if test="${!fn:containsIgnoreCase(mapEntry.key, mapEntry.value.abnormalMaName)}"> <span title="IMPReSS Term differs from MA term">(${mapEntry.key})</span></c:if></td><td><span title="${mapEntry.value.numberOfHetSpecimens} Heterozygous Mutant Mice">${mapEntry.value.numberOfHetSpecimens}</span></td>
                                     			<td <c:if test="${mutantImagesAnatomyToRow[mapEntry.key].homImages}">style="color:${yesColor}"</c:if>>
                                     			<span title="Homozygote Images are
                                     			<c:if test="${!mutantImagesAnatomyToRow[mapEntry.key].homImages}">not</c:if> available"><c:if test="${mutantImagesAnatomyToRow[mapEntry.key].homImages}">Yes</c:if><c:if test="${!mutantImagesAnatomyToRow[mapEntry.key].homImages}">No</c:if></span></td>
                                     		<td>
                                     		<c:choose>
                                     			<c:when test="${wtAnatomyToRow[mapEntry.key].expression}">
                                     				<span title="${fn:length(wtAnatomyToRow[mapEntry.key].specimenExpressed)} wild type specimens expressed from a total of ${fn:length(wtAnatomyToRow[mapEntry.key].specimen)} wild type specimens" class="${expressionIcon}" style="color:${yesColor}"></span>(${fn:length(wtAnatomyToRow[mapEntry.key].specimenExpressed)}/${fn:length(wtAnatomyToRow[mapEntry.key].specimen)})
                                     			</c:when>
                          						<c:when test="${wtAnatomyToRow[mapEntry.key].notExpressed}">
                          							<span title="${fn:length(wtAnatomyToRow[mapEntry.key].specimenNotExpressed)} Not Expressed ${fn:length(wtAnatomyToRow[mapEntry.key].specimen)} wild type specimens" class="${noExpressionIcon}" style="color:${noColor}"></span>
                          						</c:when> 
                          						<c:when test="${wtAnatomyToRow[mapEntry.key].noTissueAvailable}">
                          							<i title="No Tissue Available" class="${noTissueIcon}" style="color:${noColor}"></i>
                          						</c:when>             			
                                     			
                                     			<c:otherwise>
                                     				<span title="Ambiguous" class="${ambiguousIcon}" style="color:${noColor}"></span>
                                     			</c:otherwise>
                                     		</c:choose>
                                     		</td>
                                     		<td>
                                     		<c:choose>
                                     			<c:when test="${mapEntry.value.expression}">
                                     				<span title="${fn:length(mapEntry.value.specimenExpressed)} mutant specimens expressed from a total of ${fn:length(mapEntry.value.specimen)} mutant specimens" class="${expressionIcon}" style="color:${yesColor}"></span>(${fn:length(mapEntry.value.specimenExpressed)}/${fn:length(mapEntry.value.specimen)})
                                     			</c:when>
                          						<c:when test="${mapEntry.value.notExpressed}">
                          							<span title="${fn:length(mapEntry.value.specimenNotExpressed)} Not Expressed from a total of ${fn:length(mapEntry.value.specimen)} mutant specimens" class="${noExpressionIcon}" style="color:${noColor}"></span>
                          						</c:when> 
                          						<c:when test="${mapEntry.value.noTissueAvailable}">
                          							<span title="No Tissue Available" class="${noTissueIcon}" style="color:${noColor}"></span>
                          						</c:when>             			
                                     			
                                     			<c:otherwise>
                                     				<span title="Ambiguous" class="${ambiguousIcon}" style="color:${noColor}"></span>
                                     			</c:otherwise>
                                     		</c:choose>
                                     		</td>
                               
                                     		<%-- <td>
                          
                                     		<c:forEach var="specimen" items="${mapEntry.value.specimen}">
                                     		<i title="zygosity= ${specimen.value.zyg}">${specimen.key}</i>
                                 
                                     		</c:forEach></td> --%>
                                     		                                     		
                                     		<td>
                                     		<c:if test="${mutantImagesAnatomyToRow[mapEntry.key].imagesAvailable}">
                                     			<a href='${baseUrl}/impcImages/images?q=*:*&fq=(procedure_name:"Adult LacZ" AND ma_id:"${mapEntry.value.abnormalMaId}" AND marker_symbol:"${gene.symbol}")'><i title="Images available (click on this icon to view images)" class="fa fa-image" alt="Images">(${mutantImagesAnatomyToRow[mapEntry.key].numberOfImages})</i>
                                     			</a>
                                     		</c:if>
                                     		</td>
                                     		</tr>
                                     	</c:forEach>
                                     
                                     </table>
                                     
                                    
                                    
                                    
                                    
                                    
                                    
                                    
                                    
                                   
                                </div>
                               
                            
                            
                            
                            
                            <!-- section for expression data here -->
							
							

							<div id="tabs-2">
                               
                                     <!-- <h2 class="title" id="section-impc_expression">Expression Data<i class="fa fa-question-circle pull-right" title="Brief info about this panel"></i></h2>
                                      -->
                                      <div class="accordion-body" style="display: block;">
                                   
										<a href="${baseUrl}/impcImages/laczimages/${acc}">All Images</a>
										<c:forEach var="entry" items="${impcExpressionImageFacets}" varStatus="status">
                                           
                                                <c:set var="href" scope="page" value="${baseUrl}/impcImages/laczimages/${acc}/${entry.name}"></c:set>
                                                     <ul>
                                                    <t:impcimgdisplay2 category="${entry.name}(${entry.count})" href="${href}" img="${impcExpressionFacetToDocs[entry.name][0]}" impcMediaBaseUrl="${impcMediaBaseUrl}"></t:impcimgdisplay2>
                                                    </ul>
                                                    <!-- </a>&nbsp; -->
                                                
                                               
                                        </c:forEach><!-- solrFacets end -->

                                        
                                    <!-- </div> -->
                                
                                     
                            </div><!--  end of tabs-2 -->  
                                	
                                
                      	</div><!-- end of tabs -->
                      </c:if>
              </div> 
                      
        </div><!-- end of inner ide is wrong when displayed in browser these divs are needed-->
        </div><!--  end of section -->

                                    

                            <!-- nicolas accordion for images here -->
                            <c:if test="${not empty impcImageFacets}">
                                <div class="section">
                                    <h2 class="title" id="section-impc-images">IMPC Phenotype Associated Images <i class="fa fa-question-circle pull-right" title="Brief info about this panel"></i></h2>
                                
                                    <div class="inner">
                                        <c:forEach var="entry" items="${impcImageFacets}" varStatus="status">


                                            <c:forEach var="doc" items="${impcFacetToDocs[entry.name]}">
                                                <div id="impc-images-heading" class="accordion-group">    

                                                    <div class="accordion-heading">
                                                        ${doc.parameter_name}(${entry.count}) 
                                                    </div>
                                                    <div class="accordion-body">
                                                        <ul>
																<c:set var="href" scope="page" value="${baseUrl}/imagePicker/${acc}/${entry.name}"></c:set>
                                                            <a href="${href}">
                                                                <t:impcimgdisplay2 img="${doc}" impcMediaBaseUrl="${impcMediaBaseUrl}" pdfThumbnailUrl="${pdfThumbnailUrl}" href="${href}" count="${entry.count}"></t:impcimgdisplay2>
                                                                </a>

                                                            </ul>


                                                        <%--  <div class="clear"></div>
                                                            <c:if test="${entry.count>5}">
                                                                <p class="textright"><a href="${baseUrl}/images?gene_id=${acc}&fq=expName:${entry.name}"><i class="fa fa-caret-right"></i> show all ${entry.count} images</a></p>
                                                            </c:if> --%>
                                                    </div><!--  end of accordion body -->
                                                </div>
                                            </c:forEach>

                                        </c:forEach>



                                    </div><!--  end of inner -->
                                </div> <!-- end of section -->
                            </c:if>			


                            <!-- nicolas accordion for images here -->
                            <c:if test="${not empty solrFacets}">
                                <div class="section">
                                    <h2 class="title" id="section-images">Phenotype Associated Images <i class="fa fa-question-circle pull-right" title="Brief info about this panel"></i></h2>
                                    <!--  <div class="alert alert-info">Work in progress. Images may depict phenotypes not statistically associated with a mouse strain.</div>	 -->
                                    <div class="inner">
                                        <c:forEach var="entry" items="${solrFacets}" varStatus="status">
                                            <div class="accordion-group">
                                                <div class="accordion-heading">
                                                    ${entry.name} (${entry.count})
                                                </div>
                                                <div class="accordion-body">
                                                    <ul>
                                                        <c:forEach var="doc" items="${facetToDocs[entry.name]}">
                                                            <li>
                                                                <t:imgdisplay img="${doc}" mediaBaseUrl="${mediaBaseUrl}"></t:imgdisplay>
                                                                </li>
                                                        </c:forEach>
                                                    </ul>
                                                    <div class="clear"></div>
                                                    <c:if test="${entry.count>5}">
                                                        <p class="textright"><a href="${baseUrl}/images?gene_id=${acc}&fq=expName:${entry.name}"><i class="fa fa-caret-right"></i> show all ${entry.count} images</a></p>
                                                    </c:if>
                                                </div><!--  end of accordion body -->
                                            </div>
                                        </c:forEach><!-- solrFacets end -->

                                    </div><!--  end of inner -->
                                </div> <!-- end of section -->
                            </c:if>			

                            <c:if test="${not empty expressionFacets}">
                                <div class="section">
                                    <h2 class="title" id="section-expression">Expression <i class="fa fa-question-circle pull-right"></i></h2>
                                    <div class="inner">			

                                        <!-- thumbnail scroller markup begin -->
                                        <c:forEach var="entry" items="${expressionFacets}" varStatus="status">
                                            <div class="accordion-group">
                                                <div class="accordion-heading">
                                                    ${entry.name}  (${entry.count})
                                                </div>
                                                <div  class="accordion-body">

                                                    <ul>
                                                        <c:forEach var="doc" items="${expFacetToDocs[entry.name]}">
                                                            <li>
                                                                <t:imgdisplay img="${doc}" mediaBaseUrl="${mediaBaseUrl}"></t:imgdisplay>
                                                                </li>
                                                        </c:forEach>
                                                    </ul>
                                                    <div class="clear"></div>
                                                    <c:if test="${entry.count>5}">
                                                        <p class="textright"><a href='${baseUrl}/images?gene_id=${acc}&q=expName:"Wholemount Expression"&fq=annotated_or_inferred_higherLevelMaTermName:"${entry.name}"'>show all  ${entry.count} images</a></p>
                                                    </c:if>
                                                </div>
                                            </div>
                                        </c:forEach>	
                                    </div>
                                </div>
                            </c:if>

                            <!--Disease Sections-->
                            <c:if test="${not empty orthologousDiseaseAssociations}">                 
                                <div class="section" id="orthologous-diseases">
                                    <h2 class="title" id="section-disease-models">Disease Models <small class="sub">associated by gene orthology</small>
                                        <a href='http://www.sanger.ac.uk/resources/databases/phenodigm/'></a>
                                        <span class="documentation">
                                            <a href='${baseUrl}/documentation/disease-help.html#details' class="mpPanel">
                                                <i class="fa fa-question-circle pull-right"></i>
                                            </a>
                                        </span>
                                    </h2>
                                    <div class="inner">
                                        <table id="orthologous_diseases_table" class="table tableSorter disease">
                                            <jsp:include page="genes_orthologous_diseases_table_frag.jsp"></jsp:include>
                                            </table>
                                        </div>
                                    </div>
                            </c:if>

                            <c:if test="${not empty phenotypicDiseaseAssociations}">                 
                                <div class="section"id="predicted-diseases">
                                    <h2 class="title" id="section-potential-disease-models">Potential Disease Models <small class="sub">predicted by phenotypic similarity</small>
                                        <a href='http://www.sanger.ac.uk/resources/databases/phenodigm/'></a>
                                        <span class="documentation">
                                            <a href='${baseUrl}/documentation/disease-help.html#details' class="mpPanel">
                                                <i class="fa fa-question-circle pull-right"></i>
                                            </a>
                                        </span>
                                    </h2>
                                    <div class="inner">
                                        <table id="predicted_diseases_table" class="table tableSorter disease">
                                            <jsp:include page="genes_predicted_diseases_table_frag.jsp"></jsp:include>
                                            </table>
                                        </div>
                                    </div>
                            </c:if>

                            <div class="section" id="order2">
                                <h2 class="title documentation">Order Mouse and ES Cells
                                    <a href="${baseUrl}/documentation/gene-help.html#alleles" id='allelePanel' class="fa fa-question-circle pull-right" data-hasqtip="212" aria-describedby="qtip-212"></a>
                                </h2>
                                <div class="inner">
                                    <div id="allele2"></div>
                                </div>
                            </div>       
                        </div> <!--end of node wrapper should be after all secions  -->
                    </div>
                </div>
          </div>
           

            <script type="text/javascript" src="${baseUrl}/js/phenodigm/diseasetableutils.min.js?v=${version}"></script>
            <script type="text/javascript">
                    var diseaseTables = [
                        {id: '#orthologous_diseases_table',
                            tableConf: {
                                processing: true,
                                paging: false,
                                info: false,
                                searching: false,
                                order: [[2, 'desc'], [4, 'desc'], [3, 'desc']],
                                "sPaginationType": "bootstrap"
                            }},
                        {id: '#predicted_diseases_table',
                            tableConf: {
                                order: [[2, 'desc'], [4, 'desc'], [3, 'desc']],
                                "sPaginationType": "bootstrap"
                            }}
                    ];

                    $(document).ready(function () {
                        for (var i = 0; i < diseaseTables.length; i++) {
                            var diseaseTable = diseaseTables[i];
                            var dataTable = $(diseaseTable.id).DataTable(diseaseTable.tableConf);
                            $.fn.addTableClickCallbackHandler(diseaseTable.id, dataTable);
                        }
                        
                    });
            </script>
        </jsp:body>

    </t:genericpage>
