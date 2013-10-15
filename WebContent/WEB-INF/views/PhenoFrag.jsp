<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

	<script>
	
	</script>
	<c:set var="count" value="0" scope="page" />
	<c:forEach var="phenotype" items="${phenotypes}" varStatus="status">
							<c:forEach var="sex" items="${phenotype.sexes}"><c:set var="count" value="${count + 1}" scope="page"/></c:forEach>
	</c:forEach>
	<p id="resultCount">
	Total number of results: ${count}
	</p>

				<table id="phenotypes" class="table table-striped">
					<thead>
						<tr>
							<th>Phenotype</th>
							<th>Allele</th>
							<th>Zygosity</th>
							<th>Sex</th>
							<th>Procedure / Parameter</th> 
							<th>Source</th>
							<th>Graph</th>
							<%-- <th>Strain</th> --%>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="phenotype" items="${phenotypes}" varStatus="status">
						<c:set var="europhenome_gender" value="Both-Split"/>
						<tr>
						<td><a href="${baseUrl}/phenotypes/${phenotype.phenotypeTerm.id.accession}">${phenotype.phenotypeTerm.name}</a></td>
						<td><c:choose><c:when test="${fn:contains(phenotype.allele.id.accession, 'MGI')}"><a href="http://www.informatics.jax.org/accession/${phenotype.allele.id.accession}"><t:formatAllele>${phenotype.allele.symbol}</t:formatAllele></a></c:when><c:otherwise><t:formatAllele>${phenotype.allele.symbol}</t:formatAllele></c:otherwise></c:choose></td>
						<td>${phenotype.zygosity}</td>
						<td style="font-family:Verdana;font-weight:bold;">
							<c:set var="count" value="0" scope="page" />
							<c:forEach var="sex" items="${phenotype.sexes}"><c:set var="count" value="${count + 1}" scope="page"/><c:if test="${sex == 'female'}"><c:set var="europhenome_gender" value="Female"/><img style="cursor:help;color:#D6247D;" rel="tooltip" data-placement="top" title="Female" alt="Female" src="${baseUrl}/img/icon-female.png" /></c:if><c:if test="${sex == 'male'}"><c:set var="europhenome_gender" value="Male"/><img style="cursor:help;color:#247DD6;margin-left:<c:if test="${count != 2}">16</c:if><c:if test="${count == 2}">4</c:if>px;" rel="tooltip" data-placement="top" title="Male" alt="Male" src="${baseUrl}/img/icon-male.png" /></c:if></c:forEach>
						</td>
						<td>${phenotype.procedure.name} / ${phenotype.parameter.name}</td>
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
						
						<td style="text-align:center"><c:if test="${phenotype.dataSourceName eq 'EuroPhenome' }"><a href="${baseUrl}/stats/genes/${acc}?parameterId=${phenotype.parameter.stableId}<c:if test="${fn:length(phenotype.sexes) eq 1}">&gender=${phenotype.sexes[0]}</c:if>&zygosity=${phenotype.zygosity}"><img src="${baseUrl}/img/icon_stats.png" alt="Graph" /></a></c:if></td>
						</tr>
						</c:forEach>
					</tbody>
				</table>
				<script>
					$(document).ready(function(){	
					});
					
						</script>
	<!-- /row -->