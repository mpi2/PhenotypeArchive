<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:genericpage>
    <jsp:attribute name="title">IMPC Phenotype to GWAS Disease Trait Mapping</jsp:attribute>
    <jsp:attribute name="breadcrumb">&nbsp;&raquo;<a href="${baseUrl}/phenotype2gwas">&nbsp;IMPC Phenotype to GWAS Disease Trait Mapping</a></jsp:attribute>
    <jsp:attribute name="header">
        

        <!-- <link rel="stylesheet" href="${baseUrl}/css/vendor/jquery.ui/jquery.ui.core.css"> -->

        <!--  <link href="${baseUrl}/js/vendor/jquery/jquery.qtip-2.2/jquery.qtip.min.css" rel="stylesheet" />-->
        <link rel="stylesheet" href="${baseUrl}/css/vendor/font-awesome/font-awesome.min.css" />
        <link rel="stylesheet" href="${baseUrl}/css/gwastable.css" />
        <style type="text/css">
        </style>
        
        <script type='text/javascript'>
        
            $(document).ready(function () {
                'use strict';
                
				// test only
                //var baseUrl = '//dev.mousephenotype.org/data';
                //var baseUrl = 'http://localhost:8080/phenotype-archive';
                
                var baseUrl = "${baseUrl}";

                $("table.tablesorter").dataTable({
                	 "bSort": true, // true is default 
                     "processing": true,
                     "paging": false,
                     //"serverSide": false,  // do not want sorting to be processed from server, false by default
                     //"sDom": "i<<'#exportSpinner'>l<f><'#tableTool'>r>ti",
                     "sDom": "i<<'#exportSpinner'>l<'#tableTool'>r>ti",
                     "searchHighlight": true,
                     "iDisplayLength": 50,
                     "oLanguage": {
                         "sSearch": "Filter: "
                     },
                     "aaSorting": [[ 0, "asc" ]],
                 	 "fnDrawCallback": function (oSettings) {  // when dataTable is loaded
                 		// download tool
                 		var mgiGeneSymbol = $('span#mkLeft a').text();
                 		var endPoint = baseUrl + "/impc2gwasExport?";
                 		var gridFields = "Marker symbol\tMGI gene id\tMGI allele id\tMGI allele name\tIMPC Mouse gender\tIMPC MP term id\tIMPC MP term name\tGWAS trait\tGWAS SNP id\tGWAS p value\tGWAS Reported gene\tGWAS Mapped gene\tGWAS Upstream gene\tGWAS Downstream gene\tIMPC phenotypic mapping to GWAS";
                 		var traitCheckBox = $("table.tablesorter").size() > 1 ? "Current trait only <input name='currentTraitName' value='' type='checkbox' />" : "";
                 		
               		  	$('div#tableTool').html("<span id='expoWait'></span><form id='dnld' method='GET' action='" + endPoint + "'>"
                        	+ "<span class='export2'>Export as</span>"
                       		+ "<input name='fileType' value='' type='hidden' />"
                       		+ "<input name='mgiGeneSymbol' value='" + mgiGeneSymbol + "' type='hidden' />"
                       		+ "<input name='gridFields' value='" + gridFields +"' type='hidden' />"
                       		+ "<button class='tsv fa fa-download gridDump'>TSV</button>"
                       		+ " or<button class='xls fa fa-download gridDump'>XLS</button>"
                       		+ traitCheckBox
                       		+ "</form>");
                 		
                   		$('button.gridDump').click(function(){
                   			
                   			$('ul.tabs li a').each(function() {
                   				var id = $(this).attr('href').replace('#','');
                   				
                   				if ( $('div#' + id).is(":visible") ){
                   					$("form#dnld input[name='currentTraitName']").val($(this).text());
                   				}
                   			});
                   			
                    		var errMsg = 'AJAX error trying to export dataset';
                    		var fileType = $(this).hasClass('tsv') ? 'tsv' : 'xls';
                    		$("form#dnld input[name='fileType']").val(fileType);
                    		
                    		//console.log($('form#dnld').serialize()); 
                    		
                    		$("form#dnld").submit();
                   		});
                        
                        $('body').removeClass('footerToBottom'); 
                 	 }
            	});
                
               $( "#tabs" ).tabs();
               $.fn.customJqTabs();
               // $('ul.tabs li:nth-child(2) a').click();  // activate this by default, doing this will conflict with aname scroll
          
            });
            
        </script>
        
        <script type='text/javascript' src='${baseUrl}/js/vendor/jquery/jquery.highlight.js'></script>  
        <script type='text/javascript' src='https://cdn.datatables.net/plug-ins/f2c75b7247b/features/searchHighlight/dataTables.searchHighlight.min.js'></script>  
        <script type='text/javascript' src='${baseUrl}/js/utils/tools.js'></script>  

    </jsp:attribute>

    <jsp:attribute name="addToFooter">	
        <div class="region region-pinned">

        </div>		

    </jsp:attribute>

    <jsp:body>		

        <div class="region region-content">
            <div class="block">
                <div class='content'>
                    <div class="node node-gene">
                        <h1 class="title" id="top">IMPC Phenotype to GWAS Disease Trait Mapping</h1>	 
                        <div class="section">
                            <div class="inner">
                                <div class="clear"></div>

                                <!-- container to display dataTable -->									
                                <div class="HomepageTable" id="pm">${mapping}</div>
                            </div>
                        </div>
                    </div>				
                </div>
            </div>
        </div>		       

    </jsp:body>
</t:genericpage>

