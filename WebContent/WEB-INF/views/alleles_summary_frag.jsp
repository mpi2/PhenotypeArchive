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
                    <td></td>
                    <td></td>
                    <td>
                        <c:if test="${not empty other_available_alleles_with_mice}">
                            <c:if test="${not empty summary['status_mice']['orders'] || not empty summary['status_mice']['contacts']}">These alleles are available as mice</br></c:if>
                            <c:if test="${empty summary['status_mice']['orders'] && empty summary['status_mice']['contacts']}">But these alleles are available as mice</br></c:if>
                            <c:forEach var="allele" items="${other_available_alleles_with_mice.keySet()}" varStatus="status">
                                <a href="${baseUrl}/alleles/${acc}/${other_available_alleles_with_mice[allele]['allele_name']}<c:if test="${bare == true}">?bare=true</c:if>" class="<c:if test="${other_available_alleles_with_mice[allele]['mice_available'] == 'true' }">status done"</c:if> <c:if test="${other_available_alleles_with_mice[allele]['mice_available'] == 'false' }">status inprogress"</c:if> oldtitle="${other_available_alleles_with_mice[allele]['allele_name']}"><span>${other_available_alleles_with_mice[allele]['allele_name_suffix']}</span></a>
                            </c:forEach>
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
                    <td></td>
                    <td></td>
                    <td>
                        <c:if test="${not empty other_available_alleles_with_es_cells}">
                            <c:if test="${not empty summary['status_es_cells']['orders'] || not empty summary['status_es_cells']['contacts']}">These alleles are also available as ES Cells</br></c:if>
                            <c:if test="${empty summary['status_es_cells']['orders'] && empty summary['status_es_cells']['contacts']}">But these alleles are available as ES Cells</br></c:if>
                            <c:forEach var="allele" items="${other_available_alleles_with_es_cells.keySet()}" varStatus="status">
                            <a href="${baseUrl}/alleles/${acc}/${other_available_alleles_with_es_cells[allele]['allele_name']}<c:if test="${bare == true}">?bare=true</c:if>" class="<c:if test="${other_available_alleles_with_es_cells[allele]['es_cells_available'] == 'true' }">status done"</c:if> <c:if test="${other_available_alleles_with_es_cells[allele]['es_cells_available'] == 'false' }">status inprogress"</c:if> oldtitle="${other_available_alleles_with_es_cells[allele]['allele_name']}"><span>${other_available_alleles_with_es_cells[allele]['allele_name_suffix']}</span></a>
                            </c:forEach>
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
                        
                        
        <table class="wrap_table">
                <tr>
                <c:if test="${not empty summary['genbank']}">
                    <td data-count="1">
                            <a class="btn" href="${summary['genbank']}"><i class="fa fa-info"></i> Genbank File </a>
                    </td>
                </c:if>
                <c:if test="${not empty summary['mutagenesis_url']}">
                    <td data-count="2">
                            <a class="btn" href="${summary['mutagenesis_url']}"><i class="fa fa-info"></i> Mutagenesis Prediction </a>
                    </td>
                </c:if>
                <c:if test="${not empty summary['southern_tool']}">
                    <td data-count="3">
                            <a class="btn" href="${summary['southern_tool']}"><i class="fa fa-info"></i> Southern Tool </a>
                    </td>
                </c:if>
                <c:if test="${not empty summary['ensembl_url']}">
                    <td data-count="4">
                            <a class="btn" href="${summary['ensembl_url']}"><i class="fa fa-info"></i> Ensembl </a>
                    </td>
                </c:if>
                <c:if test="${not empty summary['genotype_primers']}">
                    <td data-count="5">
                            <a class="btn" href="${summary['genotype_primers']}"><i class="fa fa-info"></i> Genotyping Primers </a>
                    </td>
                </c:if>                
                </tr>

        </table >


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