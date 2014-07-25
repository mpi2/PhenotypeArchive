<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:genericpage>

<jsp:attribute name="title">Mutagenesis Prediction</jsp:attribute>

<jsp:attribute name="header">

    <script type="text/javascript" src="${baseUrl}/js/general/mutagenesis.js"></script>
    <link rel="stylesheet" type="text/css" href="${baseUrl}/css/mutagenesis.css"/>

</jsp:attribute>

<jsp:body>

        
<h1 class="title" id="top">${mutagenesis_title}</h1>

<c:if test="${not empty mutagenesis_examples}"> 
    <br/>
    <br/>
    <div style="font-size: 150%;">
    <c:forEach var="example" items="${mutagenesis_examples}" varStatus="mutagenesis_examplesx">
        <a target="_blank" href="${example['url']}">${example['name']} (${example['project']})</a>&nbsp;&nbsp;&nbsp;&nbsp;
        <a target="_blank" href="${example['old_url']}">original</a>
        <br/><br/>
    </c:forEach>
    </div>
</c:if>

<c:if test="${empty mutagenesis and empty mutagenesis_examples}"> 
    <br/>
    <br/>
    <p style="font-size: 250%;">Nothing found!</p>
</c:if>

<c:if test="${not empty mutagenesis}"> 

<div style="font-size: 100%;height:5000px;">
    
    <br/>
    <br/>
    <p>${mutagenesis_blurb}</p>

    <table id="mutagenesis_table" class="border">
        
        
        
        
      <thead>
        <tr style="background-color:rgb(204, 204, 204);">
        <th>Ensembl transcript id</th>
        <th>Ensembl Biotype</th>
        <th>Floxed transcript description</th>
        <th>Details</th>
      </tr>
      </thead>
        
        
    <c:forEach var="item" items="${mutagenesis}" varStatus="listx">

        <c:if test="${listx.getIndex() == 0}">                    
            <tr class="first" style="background-color: #E0F9FF !important;">
        </c:if>                    

        <c:if test="${listx.getIndex() > 0}">                    
            <tr class="rest" style="display:none;background-color: #E0F9FF !important;">
        </c:if>                    

            <td>${item['ensembl_transcript_id']}</td>
            <td>${item['biotype']}</td>
            
            <c:if test="${not empty item['is_warning']}"> 
                <td class="warning">${item['floxed_transcript_description']}</td>
            </c:if>
                
            <c:if test="${empty item['is_warning']}"> 
                <td>${item['floxed_transcript_description']}</td>
            </c:if>

                <td>
        <c:if test="${item['exons'].size() > 0}">                    
            <a id="toggle_closed_detail_${listx.getIndex()}" class="toggle_closed_detail" data-count="${listx.getIndex()}">view</a>
        </c:if>
            </td>
            
        </tr>

            
            <tr class="details">
            
            <td colspan="4" style="display:none;" id="hide_target_${listx.getIndex()}" class="hide_target">

                <span style="margin: 5px 0;">
                    <strong>Predicted translation:</strong>
<p style="margin: 3px 0;word-break:break-all;">
${item['floxed_transcript_translation']}
</p>
                </span>
                
                <table width="100%" style="margin-bottom:5px;">
                <thead>
                    <tr style="background-color:rgb(204, 204, 204);">
                    <th>Ensembl Exon ID</th>
                    <th>Pfam Domains</th>
                    <th>WildType</th>
                    <th>Floxed</th>
                    </tr>
                </thead>
                    <tbody>

    <c:forEach var="exon" items="${item['exons']}" varStatus="exonsx">
                        <tr>
                            <td>${exon['ensembl_stable_id']}</td>
                            <td>
                                
                                <c:forEach var="domain" items="${exon['domains']}" varStatus="domainsx">
                                    
                                    <c:if test="${domain['interpro_ac'].length() == 0}">     
                                        ${domain['domains_ex']}
                                    </c:if>                                          
                                    
                                    <c:if test="${domain['interpro_ac'].length() > 0}">     
                                        <a href="http://www.ebi.ac.uk/interpro/ISearch?query=${domain['interpro_ac']}">${domain['domains_ex']}</a>
                                    </c:if>                                          
                                    
                                    |
                                </c:forEach>
                            
                            </td>
                            <td>${exon['structure_ex']}</td>

        <c:if test="${exon['floxed_structure'] == 'U'}">                    
                            <td>${exon['floxed_structure_ex']}</td>
        </c:if>

        <c:if test="${exon['floxed_structure'] != 'U'}">                    
                            <td class="exon_${exon['description']}">${exon['floxed_structure_ex']}</td>
        </c:if>
                            
        
        </tr>
    </c:forEach>

    </tbody>
                </table>
                
                
    <table style="margin: auto !important;">
    <tbody>
        <tr>
            <td style="border: none !important"><strong>Legend:</strong></td>
            <td style="border: none !important" class="exon_upstream"></td>
            <td style="border: none !important">in&nbsp;frame</td>
            <td style="border: none !important" class="exon_deleted"></td>
            <td style="border: none !important">deleted</td>
            <td style="border: none !important" class="exon_frameshifted"></td>
            <td style="border: none !important">frameshifted</td>
        </tr>
    </tbody>
    </table>

                
                
                
            </td>
        </tr>    
            
            
            
            
    </c:forEach>
    </table>

    <c:if test="${mutagenesis.size() > 1}">
        <p class="textright">
            <a id="mutagenesis_table_toggle" data-count='${mutagenesis.size()}' data-type='transcripts' class="toggle_closed">Show all ${mutagenesis.size()} transcripts</a>
        </p>
    </c:if>
   
</div>

</c:if>
        
</jsp:body>
  
</t:genericpage>
