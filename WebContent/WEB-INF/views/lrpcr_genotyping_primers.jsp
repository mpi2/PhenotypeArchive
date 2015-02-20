<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:genericpage>

    <jsp:attribute name="title">LRPCR Genotyping Primers</jsp:attribute>

    <jsp:attribute name="header">

    <link rel="stylesheet" type="text/css" href="${baseUrl}/css/lrpcr_genotyping_primers.css"/>

</jsp:attribute>
<jsp:attribute name="breadcrumb">&nbsp;&raquo; <a href="${baseUrl}/search#q=*:*&fq=*:*&facet=gene">Genes</a> &raquo; <a href = "${baseUrl}/genes/${acc}">${acc}</a> &raquo; <a id = "alleles_link" href ="${baseUrl}/alleles/${acc}" >Alleles</a> &raquo; <a id = "alleles_link" href ="${baseUrl}/alleles/${acc}/${allele_name}" >${allele_name}</a> &raquo; LRPCR Genotyping Primers</jsp:attribute>
<jsp:body>      

    <h1 class="title" id="top">${lrpcr_title}</h1>
    
    <c:if test="${empty lrpcr}"> 
        <br/>
        <br/>
        <p style="font-size: 250%;">Nothing found!</p>
    </c:if>
    
    <c:if test="${not empty lrpcr}"> 

    <div id="content">
<table class="lrpcr">
    <tbody>
     
    <c:if test="${not empty lrpcr['LAR3']}"> 
        <tr>
            <td style="background-color: rgb(204, 204, 204);">5' Universal (LAR3)</td>
            <td><pre>${lrpcr['LAR3']}</pre></td>
        </tr>
    </c:if>
        
    <c:if test="${not empty lrpcr['GF4']}"> 
        <tr>
            <td style="background-color: rgb(204, 204, 204);">5' Gene Specific (GF4)</td>
            <td><pre>${lrpcr['GF4']}</pre></td>
        </tr>
    </c:if>

    <c:if test="${not empty lrpcr['GF3']}"> 
        <tr>
            <td style="background-color: rgb(204, 204, 204);">5' Gene Specific (GF3)</td>
            <td><pre>${lrpcr['GF3']}</pre></td>
        </tr>
    </c:if>
    
    <c:if test="${not empty lrpcr['GF2']}"> 
        <tr>
            <td style="background-color: rgb(204, 204, 204);">5' Gene Specific (GF2)</td>
            <td><pre>${lrpcr['GF2']}</pre></td>
        </tr>
    </c:if>
    
    <c:if test="${not empty lrpcr['GF1']}"> 
        <tr>
            <td style="background-color: rgb(204, 204, 204);">5' Gene Specific (GF1)</td>
            <td><pre>${lrpcr['GF1']}</pre></td>
        </tr>
    </c:if>
    
    <c:if test="${not empty lrpcr['RAF5']}"> 
        <tr>
            <td style="background-color: rgb(204, 204, 204);">3' Universal (RAF5)</td>
            <td><pre>${lrpcr['RAF5']}</pre></td>
        </tr>
    </c:if>
    
    <c:if test="${not empty lrpcr['GR4']}"> 
        <tr>
            <td style="background-color: rgb(204, 204, 204);">3' Gene Specific (GR4)</td>
            <td><pre>${lrpcr['GR4']}</pre></td>
        </tr>
    </c:if>
    
    <c:if test="${not empty lrpcr['GR3']}"> 
        <tr>
            <td style="background-color: rgb(204, 204, 204);">3' Gene Specific (GR3)</td>
            <td><pre>${lrpcr['GR3']}</pre></td>
        </tr>
    </c:if>
        
    <c:if test="${not empty lrpcr['GR2']}"> 
        <tr>
            <td style="background-color: rgb(204, 204, 204);">3' Gene Specific (GR2)</td>
            <td><pre>${lrpcr['GR2']}</pre></td>
        </tr>
    </c:if>

    <c:if test="${not empty lrpcr['GR1']}"> 
        <tr>
            <td style="background-color: rgb(204, 204, 204);">3' Gene Specific (GR1)</td>
            <td><pre>${lrpcr['GR1']}</pre></td>
        </tr>
    </c:if>

    <c:if test="${false}"> 
        <c:if test="${not empty lrpcr['PNF']}"> 
            <tr>
                <td style="background-color: rgb(204, 204, 204);">PNF</td>
                <td><pre>${lrpcr['PNF']}</pre></td>
            </tr>
        </c:if>
        <c:if test="${not empty lrpcr['R2R']}"> 
            <tr>
                <td style="background-color: rgb(204, 204, 204);">R2R</td>
                <td><pre>${lrpcr['R2R']}</pre></td>
            </tr>
        </c:if>
    </c:if>

    </tbody>
</table>
    </div>
    
    </c:if>
    
</jsp:body>
  
</t:genericpage>

        
     