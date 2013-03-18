<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:genericpage>
	<jsp:attribute name="title">Procedure Stats for ${gene.name}</jsp:attribute>

	<jsp:attribute name="breadcrumb">&nbsp;&raquo; <a
			href="${baseUrl}/search#sort=marker_symbol asc&q=*:*&core=gene">Genes</a> <c:if
			test="${not empty gene.subtype.name }">&raquo; <a
				href='${baseUrl}/search#fq=marker_type_str:"${gene.subtype.name}"&q=*:*&core=gene'>${gene.subtype.name}</a>
		</c:if> &raquo; ${gene.symbol}</jsp:attribute>

	<jsp:attribute name="header">

	<script type="text/javascript">
		var gene_id = '${acc}';
	</script>

	<!--    extra header stuff goes here such as extra page specific javascript -->
	<%-- <script src="${baseUrl}/js/mpi2_search/all.js"></script> --%>


	

<style>
</style>

    </jsp:attribute>
    <jsp:body>
    
    
    <c:if test="${not empty pipelineProcedureData}">
	<div class="row-fluid dataset">
		<div class="row-fluid">
			<div class="container span12">
				<h4 class="caption">Pipelines</h4>
				 <div class="accordion" id="accordion1">
					<c:forEach var="pipe" items="${pipelineProcedureData}" varStatus="status">
					<div class="accordion-group">
						<div class="accordion-heading">
							<a class="accordion-toggle" data-toggle="collapse" data-target="#pipeline${status.count}">
								${pipe.pipelineName}<i class="icon-chevron-<c:if test="${status.count ==1}">down</c:if><c:if test="${status.count!=1}">right</c:if> pull-left"></i>
							</a>
						</div>
						<div id="pipeline${status.count}" class="accordion-body collapse<c:if test="${status.count ==1}"> in</c:if>">
						<div class="accordion-inner">
						<!-- loop over the divs for this pipeline-  -->
								<c:forEach var="table" items="${pipe.listOfTables}" varStatus="tableStatus">
								<!-- should be a table in each div with the correct number of columns and rows -->
								<table>
										
												<c:forEach var="row" items="${table.rows}" varStatus="rowStatus">
												<tr> 
														<c:forEach var="cellData" items="${row.cellData}" varStatus="cellStatus">
														<c:if test="${cellData.colspan!=0}">
														<c:set var="string1" value="${cellData.dataString}"/>
														<c:set var="shortString" value="${fn:substring(string1,0, 12)}" />
														<td colspan="${cellData.colspan}" style="border:none;">
														
														
														
														<c:choose>
														<c:when  test="${rowStatus.last}">
														<c:set var="iconString" value='<i class="icon-forward"></i>'/>
														<c:if test="${cellData.dataString eq 'unassigned week'}"><c:set var="iconString" value=""/></c:if>
														<c:if test="${cellData.dataString!=''}"><button class="span12 btn tn_mini btn-warning" style="color:blue" title="${cellData.dataString}">${cellData.dataString} ${iconString}</button></c:if>
														</c:when>
														<c:otherwise>
														<c:if test="${cellData.dataString!=''}"><button class="span12 btn btn_mini btn-info" title="${cellData.dataString}">${shortString}</button></c:if>
														</c:otherwise>
														</c:choose>	
														</td>
														</c:if>
														</c:forEach>
												</tr>
												
								
												</c:forEach><!-- end of table -->
										
								</table>
								</c:forEach><!-- end of bootDiv -->
						
						
						</div><!--  end of accordion inner i.e. the dropdown div-->
						</div>
					</div>
				
				</c:forEach>
			</div>
		</div>
	</div>

</c:if>
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
		<c:if test="${not empty allPipelines}">
	<div class="row-fluid dataset">
		<div class="row-fluid">
			<div class="container span12">
				<h4 class="caption">Pipelines</h4>
				 <div class="accordion" id="accordion1">
					<c:forEach var="pipe" items="${allPipelines}" varStatus="status">
					<div class="accordion-group">
						<div class="accordion-heading">
							<a class="accordion-toggle" data-toggle="collapse" data-target="#pheno${status.count}">
								${pipe.name}<i class="icon-chevron-<c:if test="${status.count ==1}">down</c:if><c:if test="${status.count!=1}">right</c:if> pull-left"></i>
							</a>
						</div>
						<div id="pheno${status.count}" class="accordion-body collapse<c:if test="${status.count ==1}"> in</c:if>">
						<div class="accordion-inner">
						<div class="row">
									<c:set var="spanNumber" value="0"/>
									<c:forEach var="i" begin="0" end="20" varStatus="status">
											<c:set var="columnProduced" value="false"/>
											<c:forEach var="proc" items="${pipe.procedures}" varStatus="procStatus">
														<c:forEach var="metaData" items="${proc.metaDataSet}">
														<c:if test="${i == metaData.value}">
																<c:if test="${columnProduced eq false}">
																		<div class="span1">
																		<c:set var="spanNumber" value="${spanNumber+1}"/>
														
																<button class="span12 btn tn_small btn-warning impcPStatus" style="color:blue">week   ${i}</button>
																
														
												</c:if>
												<button class="span12 btn btn_small btn-info impcPStatus">${proc.name}</button>
													<c:set var="columnProduced" value="true"/>
											</c:if>
										</c:forEach>
									</c:forEach>
									
									<c:if test="${columnProduced}"><!--end of week if column needed  -->
									</div>
									<!--close the fluid div and create a new one  -->
									<c:if test="${spanNumber>=12}"><c:set var="spanNumber" value="0"/></div><div class="row"></c:if>
									</c:if>
								</c:forEach>
								
							</div><!--  end of first row-fluid in toggle dropdown-->
							
							
						</div>
					</div>
				</div>
				</c:forEach>
			</div>
		</div>
	</div>
</div>
</c:if>

    </jsp:body>
</t:genericpage>
