<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix='fn' uri='http://java.sun.com/jsp/jstl/functions' %>

<t:genericpage>

	<jsp:attribute name="title">${anatomy.accession} (${anatomy.term}) | IMPC anatomy Information</jsp:attribute>
	 <jsp:attribute name="breadcrumb">&nbsp;&raquo; <a href="${baseUrl}/search#fq=selected_top_level_ma_term:*&facet=ma">anatomy</a> &raquo; ${anatomy.term}</jsp:attribute>
<jsp:attribute name="header">
</jsp:attribute>
    <jsp:attribute name="addToFooter">
		<div class="region region-pinned">
            
        <div id="flyingnavi" class="block">
            
            <a href="#top"><i class="fa fa-chevron-up" title="scroll to top"></i></a>
            
            <ul>
                <li><a href="#top">Anatomy Term</a></li>
                <c:if test="${not empty anatomy.mpTerms}">
                	<li><a href="#associated-phenotypes">Associated Phenotypes</a></li>
                </c:if>
                <c:if test="${fn:length(anatomy.childTerms)>0 }">
                	<li><a href="#explore">Explore</a></li>
                </c:if>
            </ul>
            
            <div class="clear"></div>
            
        </div>
        
    </div>
		
    </jsp:attribute>

                
    <jsp:body>
    
 <div class="region region-content">
			<div class="block block-system">
				<div class="content">
					<div class="node node-gene">
								<h1 class="title" id="top">Anatomy Term: ${anatomy.term}</h1>
					
<c:if test="${empty expressionImages && fn:length(anatomy.childTerms)==0}">
	<div class="section">
		<div class=inner>
			<div class="alert alert-info">No data currently available	</div>
		</div>
	</div>
</c:if>
		<c:if test="${not empty expressionImages && fn:length(expressionImages) !=0}">
	<div  class="section">
		<!-- <h2 class="title">Expression Images<i class="fa fa-question-circle pull-right"></i></h2> -->
				<div class="inner">		
						 <div class="accordion-group">
                        						<div class="accordion-heading">
                        						Expression Associated Images 
                        						</div>
								<div class="accordion-body">
								
		    					<ul>
                                                            
		    					<c:forEach var="doc" items="${expressionImages}">
                                                            <li class="span2">
									<t:imgdisplay img="${doc}" mediaBaseUrl="${mediaBaseUrl}"></t:imgdisplay>
                                                            </li>
                                                        </c:forEach>
                                                        
								</ul>
								<c:if test="${numberExpressionImagesFound>5}">
                                        				<p class="textright">
								<a href='${baseUrl}/images?anatomy_id=${anatomy.accession}&fq=expName:Wholemount Expression'><i class="fa fa-caret-right"></i>show all ${numberExpressionImagesFound} images</a>
								</p>
								</c:if>
							</div>
						</div>
					<!--  end of accordion -->
					</div>
	</div>
	</c:if><!-- end of images lacz expression priority and xray maybe -->
	
	<%-- spoke to terry and these need rethink in terms of MP associations <c:if test="${not empty anatomy.mpTerms}">
		<div class="section">
			<div class='documentation'><a href='' class='mpPanel'><img src="${baseUrl}/img/info_20x20.png" /></a></div>
			<h2 class="title" id="associated-phenotypes">Associated Phenotypes<i class="fa fa-question-circle pull-right"></i></h2>
			<div class="inner">
							
				
				<table>
				<tbody>
					<tr>
						<td>MP Terms:</td>
						<c:forEach items="${anatomy.mpTerms}" var="mpTerm" varStatus="mpStatus">
						<tr>
						<td><a href="${baseUrl}/phenotypes/${anatomy.mpIds[mpStatus.index]}">${mpTerm}</a></td><td>${mpTerm}</td>
						</tr>
						</c:forEach>
					</tr>
					</tbody>
					</table>
					
				
		</div>
		
	</div><!-- end of images lacz expression priority and xray maybe -->
	</c:if> --%>
	
				<c:if test="${fn:length(anatomy.childTerms)>0 }">
				<div class="section">
				<%-- <div class='documentation'><a href='' class='relatedMaPanel'><img src="${baseUrl}/img/info_20x20.png" /></a></div> --%>
					<h2 class="title" id="explore">Explore<i class="fa fa-question-circle pull-right"></i></h2>
							
					<div class="inner">				
								
							<table>
							<tbody>
							<tr>
									<td>Child Terms:</td>
									<td></td>
									<c:forEach items="${anatomy.childTerms}" var="childTerm" varStatus="childStatus">
									<tr>
									<td><a href="${baseUrl}/anatomy/${anatomy.childIds[childStatus.index]}">${anatomy.childIds[childStatus.index]}</a></td><td>${childTerm}</td>
									</tr>
									</c:forEach>
								</tr>
								</tbody>
								</table>
					</div>
					
				</div><!-- end of anatomy explore panel-->
				</c:if>	
	
				<div class="section"> 
					<h2 class="title">Gene with reporter expression for ${anatomy.term}</h2>
						<div class="inner">
							 <div class="container span12">
									<div id="filterParams" >
                     <c:forEach var="filterParameters" items="${paramValues.fq}">
                         ${filterParameters}
                     </c:forEach>
                  </div> 
                  <c:if test="${not empty phenoFacets}">
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
                 </c:if>
                 <jsp:include page="anatomyFrag.jsp"></jsp:include>						 
							</div>
				    </div>
				 </div>	
			</div>
		</div>
	</div>
</div>
		
	<script>
	$(document).ready(function(){						
					
				
			initAnatomyDataTable();
			
			var selectedFilters = "";
			var dropdownsList = new Array();
			
		  function initAnatomyDataTable(){
			  
				var aDataTblCols = [0,1,2,3,4,5,6,7];
				$('table#anatomy').dataTable( {
						"aoColumns": [
						              { "sType": "html", "mRender":function( data, type, full ) {
						            	  return (type === "filter") ? $(data).text() : data;
						              }},
						              { "sType": "html", "mRender":function( data, type, full ) {
						            	  return (type === "filter") ? $(data).text() : data;
						              }},
						              { "sType": "string"},
						              { "sType": "string"},
						              { "sType": "string"},
						              { "sType": "string"},
						              { "sType": "string"},
						              { "sType": "html"}
						              ],
							"bDestroy": true,
							"bFilter":false,
							"bPaginate":true,
				      "sPaginationType": "bootstrap"
					});
		  }
		  
			
			function refreshAnatomyTable(newUrl){
				$.ajax({
					url: newUrl,
					cache: false
				}).done(function( html ) {
					$("#anatomy_wrapper").html(html);
					initAnatomyDataTable();
				});
			}
			

			//function to fire off a refresh of a table and it's dropdown filters
			var selectedFilters = "";
			var dropdownsList = new Array();
			
			var allDropdowns = new Array();
			allDropdowns[0] = $('#ma_term');
			allDropdowns[1] = $('#procedure_name');
			allDropdowns[2] = $('#parameter_association_value');
			allDropdowns[3] = $('#phenotyping_center');
			createDropdown(allDropdowns[3], "Source: All", allDropdowns);
			createDropdown(allDropdowns[0],"Anatomy: All", allDropdowns);
			createDropdown(allDropdowns[1], "Procedure: All", allDropdowns);
			createDropdown(allDropdowns[2], "Expression: All", allDropdowns);
			
			function createDropdown(multipleSel, emptyText,  allDd){
				
				console.log("called phen createDropdown "+ multipleSel);
				
				$(multipleSel).dropdownchecklist( { firstItemChecksAll: false, emptyText: emptyText, icon: {}, 
					minWidth: 150, onItemClick: function(checkbox, selector){
						console.log("IN dropdownchecklist");
						var justChecked = checkbox.prop("checked");
						console.log("justChecked="+justChecked);
						console.log("checked="+ checkbox.val());
						var values = [];
						for(var  i=0; i < selector.options.length; i++ ) {
							if (selector.options[i].selected && (selector.options[i].value != "")) {
								values .push(selector.options[i].value);
							}
						}

						if(justChecked){				    		 
							values.push( checkbox.val());
						}else{//just unchecked value is in the array so we remove it as already ticked
							var index = $.inArray(checkbox.val(), values);
							values.splice(index, 1);
						}  
						
						console.log("values="+values );
						// add current one and create drop down object 
						dd1 = new Object();
						dd1.name = multipleSel.attr('id'); 
						dd1.array = values; // selected values
						
						dropdownsList[0] = dd1;
						
						var ddI  = 1; 
						for (var ii=0; ii<allDd.length; ii++) { 
							if ($(allDd[ii]).attr('id') != multipleSel.attr('id')) {
								dd = new Object();
								dd.name = allDd[ii].attr('id'); 
								dd.array = allDd[ii].val() || []; 
								dropdownsList[ddI++] = dd;
							}
						}
						refreshAnatomyFrag(dropdownsList);
					}, textFormatFunction: function(options) {
						var selectedOptions = options.filter(":selected");
				        var countOfSelected = selectedOptions.size();
				        var size = options.size();
				        var text = "";
				        if (size > 1){
				        	options.each(function() {
			                    if ($(this).prop("selected")) {
			                        if ( text != "" ) { text += ", "; }
			                        /* NOTE use of .html versus .text, which can screw up ampersands for IE */
			                        var optCss = $(this).attr('style');
			                        var tempspan = $('<span/>');
			                        tempspan.html( $(this).html() );
			                        if ( optCss == null ) {
			                                text += tempspan.html();
			                        } else {
			                                tempspan.attr('style',optCss);
			                                text += $("<span/>").append(tempspan).html();
			                        }
			                    }
			                });
				        }
				        switch(countOfSelected) {
				           case 0: return emptyText;
				           case 1: return selectedOptions.text();
				           case options.size(): return emptyText;
				           default: return text;
				        }
					}
				} );
			}
			
			//if filter parameters are already set then we need to set them as selected in the dropdowns
			var previousParams=$("#filterParams").html();
			
			function refreshAnatomyFrag(dropdownsList) {
				var rootUrl = window.location.href;
				var newUrl = rootUrl.replace("anatomy", "anatomyFrag");
				newUrl += '?';
				selectedFilters = "";
				for (var it = 0; it < dropdownsList.length; it++){
					if(dropdownsList[it].array.length == 1){//if only one entry for this parameter then don't use brackets and or
						selectedFilters += '&' + dropdownsList[it].name + '="' + dropdownsList[it].array+'"';
					} 
					if(dropdownsList[it].array.length > 1)	{
						selectedFilters += '&' + dropdownsList[it].name + '="' + dropdownsList[it].array.join('"&' + dropdownsList[it].name + '="') + '\"';
					}			    			 
				}
				newUrl += selectedFilters;
				refreshAnatomyTable(newUrl);
		    console.log('...refresh genes AnatomyFrag called woth new url='+newUrl);
				return false;
			}
			
	});				
	</script>
	
</jsp:body>
	

</t:genericpage>
