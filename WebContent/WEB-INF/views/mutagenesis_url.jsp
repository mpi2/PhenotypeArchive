<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:genericpage>

<jsp:body>

    <script>
    
    function toggleTable(id) {
        $("#" + id + "_toggle").on({'click':function(event){
        event.preventDefault();
        $("#" + id + " .rest").toggle();
        
        if($("#" + id + "_toggle").hasClass("toggle_closed")) {
            $("#" + id + "_toggle").removeClass("toggle_closed");
            $("#" + id + "_toggle").addClass("toggle_open");
            var type = $( "#" + id + "_toggle" ).data( "type" );
            var count = $( "#" + id + "_toggle" ).data( "count" );
            $("#" + id + "_toggle").text("Hide " + type);
        }
        else {
            $("#" + id + "_toggle").removeClass("toggle_open");
            $("#" + id + "_toggle").addClass("toggle_closed");
            var type = $( "#" + id + "_toggle" ).data( "type" );
            var count = $( "#" + id + "_toggle" ).data( "count" );
            $("#" + id + "_toggle").text("Show all " + count + " " + type);

            $(".hide_target").each(function( index ) {
                $( this ).hide();
                $( this ).removeClass("toggle_open");
                $( this ).addClass("toggle_closed");
              //  $( this ).text("view");
            });
            
            $("[id*=toggle_closed_detail_]").each(function( index ) {
                 $( this ).text("view");
                 $( this ).removeClass("toggle_open");
                 $( this ).addClass("toggle_closed_detail");
            });         
            
            $('html, body').animate({ scrollTop: 0 }, 0);
          }
        
        }});
    }

    function toggleTableDetails() {
        $(".toggle_closed_detail").each(function( index ) {
            var anchor = $( this );
            
         //   alert("add index " + index);            
            
          //  console.log( index + ": " + $( this ).text() + " - " + $( this ));
            $( this ).on({'click':function(event){
             //  $( "#hide_target_" + index ).attr('colspan',4);
             
               var count = $( this ).data( "count" );             
             
               //$( "#hide_target_" + index ).toggle();     
               $( "#hide_target_" + count ).toggle();     

                if(anchor.hasClass("toggle_closed_detail")) {
                    anchor.removeClass("toggle_closed_detail");
                    anchor.addClass("toggle_open");
                    $( this ).text("hide");
                }
                else {
                    anchor.removeClass("toggle_open");
                    anchor.addClass("toggle_closed_detail");
                    $( this ).text("view");
                }

            //   alert("#hide_target_" + index);
            
               event.preventDefault();
            }});
        });
    }
    
    $(function(){
        toggleTable("mutagenesis_table");
        toggleTableDetails();
    });

</script>

<style>
             .toggle_open, .toggle_closed, .toggle_closed_detail {
                padding-left: 16px;
                padding-top: 2px;
                padding-bottom: 2px;
             }
             .toggle_open {
                background:url(http://www.mousephenotype.org/martsearch_ikmc_project/images/silk/bullet_arrow_up.png) no-repeat 0 center;
             }
             .toggle_closed, .toggle_closed_detail {
                background:url(http://www.mousephenotype.org/martsearch_ikmc_project/images/silk/bullet_arrow_right.png) no-repeat 0 center;
             }
         </style>
         
<style>
    /* Project Page Mutagenesis Prediction Tool */

.exon_deleted {
	background-color: #a61e01;
	color: #fff;
}

.exon_frameshifted {
	background-color: #eb790b;
}

.exon_upstream {
	background-color: #0978a1;
	color: #fff;
}

.exon_downstream {
	background-color: #0978a1;
	color: #fff;
}
    </style>

    <style>
    .warning {
  	border-color: #b2a67b;
	padding: 5px 5px 5px 25px;
	margin-bottom: 10px;
	background: #fbe8ad url(http://www.mousephenotype.org/martsearch_ikmc_project/images/silk/exclamation.png) no-repeat 5px 5px;
  	font-style: italic;
  	color: #333;
}    
        .border, .border th, .border td, td.border {
                border: 1px solid #999 !important;
        }
    </style>
        
<h1 class="title" id="top">Mutagenesis</h1>

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
