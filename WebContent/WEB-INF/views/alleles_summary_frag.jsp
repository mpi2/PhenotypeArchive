<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>


<c:if test="${not empty summary}">

<div class="section">
    <div class="inner">
    <h3>Summary</h3>
    <div style="font-size: 110%; font-weight: bold;">
        <p>${summary['allele_description']}</p>
        <c:if test="${not empty summary['statuses']}">
        <table>
            <c:if test="${not empty summary['status_mice']}">

                <tr style="background-color: ${summary['status_mice']['COLOUR']} !important;">

                    <td style="width:30%">${summary['status_mice']['TEXT']}</td>

                    <td>                        
                        
                    <c:if test="${not empty summary['status_mice']['orders']}">
                        <a id="mice_order_contact_button" class="btn btn-lg" href="#mice_block"> <i class="fa fa-shopping-cart"></i> ORDER </a>
                    </c:if>
                    <c:if test="${not empty summary['status_mice']['contacts']}">
                        <a id="mice_order_contact_button" class="btn btn-lg" href="#mice_block"> <i class="fa  fa-envelope"></i> CONTACT </a>
                    </c:if>
                    <c:if test="${not empty summary['status_mice']['details']}">
                        <a class="btn btn-lg" href="${summary['status_mice']['details'][0]}"> <i class="fa  fa-info "></i> DETAILS </a>
                    </c:if>                        
                 
                    </td>

                </tr>

            </c:if>

            <c:if test="${not empty summary['status_es_cells']}">

                <tr style="background-color: ${summary['status_es_cells']['COLOUR']} !important;">

                    <td style="width:30%">${summary['status_es_cells']['TEXT']}</td>

                    <td>                        

                    <c:if test="${not empty summary['status_es_cells']['orders']}">
                        <a id="es_cell_order_contact_button" class="btn btn-lg" href="#es_cell_block"> <i class="fa fa-shopping-cart"></i> ORDER </a>
                    </c:if>
                    <c:if test="${not empty summary['status_es_cells']['contacts']}">
                        <a id="es_cell_order_contact_button" class="btn btn-lg" href="#es_cell_block"> <i class="fa  fa-envelope"></i> CONTACT </a>
                    </c:if>
                    <c:if test="${not empty summary['status_es_cells']['details']}">
                        <a class="btn btn-lg" href="${summary['status_es_cells']['details'][0]}"> <i class="fa  fa-envelope"></i> DETAILS </a>
                    </c:if>                        

                    </td>

                </tr>

            </c:if>
        </table>
            
        </c:if>
    </div>
    </div>





	<div class="section">
		<div class="inner">

        <c:if test="${not empty summary['map_image']}">
			<h3>Allele Map</h3>
        <div id="image">
            <img alt="image not found!" src="${summary['map_image']}" width="930px">
        </div>
        </c:if>


                        
                        
                        <style>
                            .wrap_table  { display: block; }
                            .wrap_table  td {display: inline-block; }
                        </style>
                        
                        
        <c:if test="${not empty summary['buttons']}">

        <table class="wrap_table">
                <tr>
            
                <c:forEach var="button" items="${summary['button_gear']}" varStatus="buttonx">

                    <td data-count="${buttonx.count}">
                        <a class="btn" href="${button['url']}"><i class="fa fa-info"></i> ${button['name']} </a>
                    </td>
                    
                </c:forEach>

                </tr>

        </table >

        </c:if>
                        
                        
                        
                        
                        
                        
                        
                        
                        
                        
                        
                        
        <c:if test="${not empty summary['buttons_xxx']}">
        <table >
            <tr>
            <td>
                <span>
                <c:if test="${not empty summary['genbank']}">
                    <a class="btn" href="${summary['genbank']}"><i class="fa fa-info"></i> Genbank file</a>
                </c:if>
                </span>
            </td>

            <td>
                <c:if test="${not empty summary['mutagenesis_url']}">
                    <a class="btn" href="${summary['mutagenesis_url']}"> <i class="fa fa-info"></i> Mutagenesis Prediction </a>
                </c:if>
            </td>

            <td>
                <c:if test="${not empty summary['southern_tool']}">
                    <a class="btn" href="${summary['southern_tool']}"> <i class="fa fa-info"></i> Southern Tool </a>
                </c:if>
            </td>
            </tr>
            <tr>
            <td>
                <c:if test="${not empty summary['lrpcr_genotyping_primers']}">
                    <a class="btn" href="${summary['lrpcr_genotyping_primers']}"> <i class="fa fa-info"></i> LRPCR Genotyping Primers </a>
                </c:if>
            </td>
            <td>

            <c:if test="${not empty summary['browsers']}">
                <c:forEach var="browser" items="${summary['browsers']}" varStatus="browsersx">
                    <a class="btn" href="${browser['url']}"> <i class="fa fa-info"></i> ${browser['browser']} </a> <br/>
                </c:forEach>
            </c:if>
                    </td>
                    
                    
                    
            <td>
                
                <c:if test="${not empty summary['loa_assays']}">
                    <c:if test="${not empty summary['loa_assays']['upstream']}">
                        <a class="btn" href="${summary['loa_assays']['upstream']}"> <i class="fa fa-info"></i> LOA (upstream) </a> &nbsp;
                    </c:if>

                    <c:if test="${not empty summary['loa_assays']['downstream']}">
                        <a class="btn" href="${summary['loa_assays']['downstream']}"> <i class="fa fa-info"></i> LOA (downstream) </a> &nbsp;
                    </c:if>

                    <c:if test="${not empty summary['loa_assays']['critical']}">
                        <a class="btn" href="${summary['loa_assays']['critical']}"> <i class="fa fa-info"></i> LOA (critical) </a>
                    </c:if>
                </c:if>

            </td>

            </tr>
        </table>
        </c:if>


            <c:if test="${false}">
        <table>
            <tr>
            <c:if test="${not empty summary['tools']}">
            <td>
                <div style="text-align: center;">Tools</div>
                    <div style="text-align: center;">
                    <c:forEach var="tool" items="${summary['tools']}" varStatus="toolsx">
                        <a href="${tool['url']}">${tool['name']}</a><br/>
                    </c:forEach>
                </div>
            </td>
            </c:if>
            </tr>
        </table>
            </c:if>

		</div>
	</div>
                </div>

</c:if>