<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

	
	<c:set var="count" value="0" scope="page" />
	<c:forEach var="phenotype" items="${phenotypes}" varStatus="status">
			<c:forEach var="sex" items="${phenotype.sexes}"><c:set var="count" value="${count + 1}" scope="page"/></c:forEach>
	</c:forEach>
	<p class="resultCount">
	Total number of results: ${count}
	</p>

	<script>
	 var resTemp = document.getElementsByClassName("resultCount");
	 if (resTemp.length > 1)
		 resTemp[0].remove();
	</script>
	
			
			<table id="phenotypes" class="table table-striped">
				<thead>
					<tr>
						<th>Gene</th>
						<th>Allele</th>
						<th>Zygosity</th>
						<th>Sex</th>
						<c:if test="${!isImpcTerm}">
							<th>Phenotype / Parameter</th>
						</c:if>
						<c:if test="${isImpcTerm}">
							<th>Procedure / Parameter</th>
						</c:if>
						<th>Source</th>
						<th>Graph</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="phenotype" items="${phenotypes}" varStatus="status">
					<c:set var="europhenome_gender" value="Both-Split"/>
					<tr>
					<td><a href="${baseUrl}/genes/${phenotype.gene.id.accession}">${phenotype.gene.symbol}</a></td>
					<td><c:choose><c:when test="${fn:contains(phenotype.allele.id.accession, 'MGI')}"><a href="http://www.informatics.jax.org/accession/${phenotype.allele.id.accession}"><t:formatAllele>${phenotype.allele.symbol}</t:formatAllele></a></c:when><c:otherwise><t:formatAllele>${phenotype.allele.symbol}</t:formatAllele></c:otherwise></c:choose></td>
					<td>${phenotype.zygosity}</td>
					<td style="font-family:Verdana;font-weight:bold;">
						<c:set var="count" value="0" scope="page" />
						<c:forEach var="sex" items="${phenotype.sexes}"><c:set var="count" value="${count + 1}" scope="page"/><c:if test="${sex == 'female'}"><c:set var="europhenome_gender" value="Female"/><img style="cursor:help;color:#D6247D;" rel="tooltip" data-placement="top" title="Female" alt="Female" src="${baseUrl}/img/female.jpg" /></c:if><c:if test="${sex == 'male'}"><c:set var="europhenome_gender" value="Male"/><img style="cursor:help;color:#247DD6;margin-left:<c:if test="${count != 2}">16</c:if><c:if test="${count == 2}">4</c:if>px;" rel="tooltip" data-placement="top" title="Male" alt="Male" src="${baseUrl}/img/male.jpg" /></c:if></c:forEach>
					</td>
					<c:if test="${!isImpcTerm}">
						<td><a href="${baseUrl}/phenotypes/${phenotype.phenotypeTerm.id.accession}">${phenotype.phenotypeTerm.name}</a> / ${phenotype.parameter.name}</td>
					</c:if>
					<c:if test="${isImpcTerm}">
						<td>${phenotype.procedure.name} / ${phenotype.parameter.name}</td>
					</c:if>	
					<td>
					<c:choose>
					<c:when test="${phenotype.phenotypeLink eq ''}">
						${phenotype.dataSourceName}
					</c:when>
					<c:otherwise>
					<a href="${phenotype.phenotypeLink }">${phenotype.dataSourceName}</a>
					</c:otherwise>
					</c:choose>
					</td>
					<td style="text-align:center">
					<!-- c:if test="${not phenotype.parameter.derivedFlag}"-->
						<c:if test="${phenotype.dataSourceName eq 'EuroPhenome' }">
						<a href="${baseUrl}/stats/genes/${phenotype.gene.id.accession}?parameterId=${phenotype.parameter.stableId}
							<c:if test="${fn:length(phenotype.sexes) eq 1}">&gender=${phenotype.sexes[0]}</c:if>&zygosity=${phenotype.zygosity}<c:if test="${phenotype.getPhenotypingCenter() != null}">&phenotypingCenter=${phenotype.getPhenotypingCenter()}</c:if>">
							<i class="fa fa-bar-chart-o"alt="Graph" > </i>
						</a>
					</c:if>
					<!-- /c:if-->
					</td>
					
					</tr>
					</c:forEach>
				</tbody>
			</table>
			<script>
				 $(document).ready(function(){
					 
					 
				 }); 
			</script>
	
				
	<!-- /row -->
