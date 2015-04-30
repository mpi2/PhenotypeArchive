<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:genericpage>
    <jsp:attribute name="title">IMPC allele paper references</jsp:attribute>
    <jsp:attribute name="breadcrumb">&nbsp;&raquo;<a href="${baseUrl}/alleleref">&nbsp;Allele references</a></jsp:attribute>
    <jsp:attribute name="header">
        
        <link href="${baseUrl}/js/vendor/jquery/jquery.qtip-2.2/jquery.qtip.min.css" rel="stylesheet" />
        <link href="${baseUrl}/css/searchPage.css" rel="stylesheet" />
        
        <style type="text/css">

            div#tableTool {
                position: relative;
                top: -70px;
                right: 0px;
            }
            div#alleleRef_filter {
            	float: left;
            	clear: right;
            }
            table.dataTable span.highlight {
                background-color: yellow;
                font-weight: bold;
                color: black;
            }
            table#alleleRef {
            	clear: left;
            }
            table#alleleRef th:first-child, table#alleleRef th:nth-child(2) {
                width: 150px !important;
            }
            table#alleleRef th:nth-child(3) {
                width: 80px !important;
            }
            table#alleleRef td {
                font-size: 14px !important;
            }
            .hideMe {
                display: none;
            }
            .showMe {
                display: block;
            }
            .alleleToggle {
                cursor: pointer;
                font-size: 11px;
                font-weight: bold;
            }
            
            div#saveTable {
                top: 54px;
                left: -25px;
            }
            div#toolBox {
                top: -58px;
                right: 35px;
            }
        </style>
        
        <script type='text/javascript'>
        
            $(document).ready(function () {
                'use strict';
                
				// test only
                //var baseUrl = '//dev.mousephenotype.org/data';
                //var baseUrl = 'http://localhost:8080/phenotype-archive';
                
                var baseUrl = "${baseUrl}";
                var solrUrl = "${internalSolrUrl};"

                var tableHeader = "<thead><th>Allele symbol</th><th>Paper title</th><th>Journal</th><th>Date of publication</th><th title='Grant agency cited in manuscript'>Grant agency</th><th>Paper link</th></thead>";
                var tableCols = 6;

                var dTable = $.fn.fetchEmptyTable(tableHeader, tableCols, "alleleRef");
                $('div#alleleRef').append(dTable);

                var oConf = {};
                oConf.doAlleleRef = true;
                oConf.iDisplayLength = 10;
                oConf.iDisplayStart = 0;

                fetchAlleleRefDataTable(oConf);
            });

            function fetchAlleleRefDataTable(oConf) {
            	
            	var aDataTblCols = [0,1,2,3,4,5];
                var oTable = $('table#alleleRef').dataTable({
                    "bSort": true, // true is default 
                    "processing": true,
                    "paging": false,
                    //"serverSide": false,  // do not want sorting to be processed from server, false by default
                    "sDom": "<<'#exportSpinner'>l<f><'#tableTool'>r>tip",
                    "sPaginationType": "bootstrap",
                    "searchHighlight": true,
                    "iDisplayLength": 200,
                    "oLanguage": {
                        "sSearch": "Filter: "
                    },
                    "columnDefs": [                
                        { "type": "alt-string", targets: 3 }   //4th col sorted using alt-string         
                    ],
                    "aaSorting": [[ 3, "desc" ]],  // default sort column order
                    "aoColumns": [
                        {"bSearchable": true, "sType": "html", "bSortable": true},
                        {"bSearchable": true, "sType": "string", "bSortable": true},
                        {"bSearchable": true, "sType": "string", "bSortable": true},
                        {"bSearchable": true, "sType": "string", "bSortable": true},
                        {"bSearchable": true, "sType": "string", "bSortable": true},
                        {"bSearchable": false, "sType": "html", "bSortable": true}
                    ],
                    "fnDrawCallback": function (oSettings) {  // when dataTable is loaded

                        // download tool
                        oConf.externalDbId = 1;
                        oConf.fileType = '';
                        oConf.fileName = 'impc_allele_references';
                        oConf.doAlleleRef = true;
                        oConf.legacyOnly = false;
                        oConf.filterStr = $(".dataTables_filter input").val();

                        $.fn.initDataTableDumpControl(oConf);
                        $('.alleleToggle').click(function () {
                            console.log("toggle");
                            if (!$(this).hasClass('showMe')) {
                                $(this).addClass('showMe').text('Show fewer alleles ...');
                                $(this).siblings().addClass('showMe');
                            }
                            else {
                                var num = $(this).attr('rel');
                                $(this).removeClass('showMe').text('Show all ' + num + ' alleles ...');
                                $(this).siblings().removeClass('showMe');
                            }
                        });
                        $('body').removeClass('footerToBottom');
                    },
                    "sAjaxSource": baseUrl + '/dataTableAlleleRef',
                    "fnServerParams": function (aoData) {
                        aoData.push(
                                {"name": "doAlleleRef",
                                    "value": JSON.stringify(oConf, null, 2)
                                }
                        );
                    }
                });
            }
        </script>
        
        <script type='text/javascript' src='https://bartaz.github.io/sandbox.js/jquery.highlight.js'></script>  
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
                        <h1 class="title" id="top">References using IKMC and IMPC resources</h1>	 
                        <div class="section">
                            <div class="inner">
                                <div class="clear"></div>

                                <!-- container to display dataTable -->									
                                <div class="HomepageTable" id="alleleRef"></div>
                            </div>
                        </div>
                    </div>				
                </div>
            </div>
        </div>		       

    </jsp:body>
</t:genericpage>

