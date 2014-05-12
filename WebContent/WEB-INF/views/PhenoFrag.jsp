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
	

				<table id="phenotypes" class="table tableSorter">
					<thead>
						<tr>
							<th class="headerSort">Phenotype</th>
							<th class="headerSort">Allele</th>
							<th class="headerSort">Zygosity</th>
							<th class="headerSort">Sex</th>
							<th class="headerSort">Procedure / Parameter</th>
							<th class="headerSort">Phenotyping Center</th> 
							<th class="headerSort">Source</th>
							<th class="headerSort">Graph</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="phenotype" items="${phenotypes}" varStatus="status">
						<c:set var="europhenome_gender" value="Both-Split"/>
						<tr>
						<td><a href="${baseUrl}/phenotypes/${phenotype.phenotypeTerm.id.accession}">${phenotype.phenotypeTerm.name}</a></td>
						<td><c:choose><c:when test="${fn:contains(phenotype.allele.id.accession, 'MGI')}"><a href="http://www.informatics.jax.org/accession/${phenotype.allele.id.accession}"><t:formatAllele>${phenotype.allele.symbol}</t:formatAllele></a></c:when><c:otherwise><t:formatAllele>${phenotype.allele.symbol}</t:formatAllele></c:otherwise></c:choose></td>
						<td>${phenotype.zygosity}</td>
						<td> 
							<c:set var="count" value="0" scope="page" />
							<c:forEach var="sex" items="${phenotype.sexes}"><c:set var="count" value="${count + 1}" scope="page"/>
								<c:if test="${sex == 'female'}"><c:set var="europhenome_gender" value="Female"/>
									<img alt="Female" src="${baseUrl}/img/female.jpg" />
								</c:if>
								<c:if test="${sex == 'male'}">
									<c:if test="${count != 2}"><img data-placement="top" src="${baseUrl}/img/empty.jpg" /></c:if>
									<c:set var="europhenome_gender" value="Male"/><img alt="Male" src="${baseUrl}/img/male.jpg" />
								</c:if>
							</c:forEach>
						</td>
						
						<td>${phenotype.procedure.name} / ${phenotype.parameter.name}</td>
						<td>${phenotype.phenotypingCenter} </td>
						<td>
							${phenotype.dataSourceName}
						</td>
						
						
						<td style="text-align:center">
						
							<!-- c:if test="${not phenotype.parameter.derivedFlag}"-->
						<c:if test="${phenotype.dataSourceName ne 'MGP' }">
									<a href="${phenotype.graphUrl }"  class="fancybox">
										<i class="fa fa-bar-chart-o" alt="Graphs" > </i>
									</a>
						</c:if>
						
						</td>
						
						
						</tr>
						</c:forEach>
					</tbody>
				</table>
				
	<!-- /row -->