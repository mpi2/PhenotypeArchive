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
	
	<table id="phenotypes">
	
		<thead>
			<tr>
				<th>Gene</th>
				<th>Allele</th>
				<th>Zygosity</th>
				<th>Sex</th>
				<th>Phenotype </th>
				<th>Procedure / Parameter</th>
				<th>Phenotyping Center</th>
				<th>Analysis</th>
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
						<c:forEach var="sex" items="${phenotype.sexes}"><c:set var="count" value="${count + 1}" scope="page"/>
							<c:if test="${sex == 'female'}"><c:set var="europhenome_gender" value="Female"/>
								<img alt="Female" src="${baseUrl}/img/female.jpg"/></c:if><c:if test="${sex == 'male'}"><c:set var="europhenome_gender" value="Male"/><img alt="Male" src="${baseUrl}/img/male.jpg"/></c:if></c:forEach>
					</td>
					
					<td><a href="${baseUrl}/phenotypes/${phenotype.phenotypeTerm.id.accession}">${phenotype.phenotypeTerm.name}</a> </td>
				
					<td>${phenotype.procedure.name} / ${phenotype.parameter.name}</td>
				
					<td>${phenotype.phenotypingCenter}</td>
					
					<td>
						${phenotype.dataSourceName}
					</td>
					
					<td style="text-align:center">
						<!-- c:if test="${not phenotype.parameter.derivedFlag}"-->
						<c:if test="${phenotype.dataSourceName ne 'MGP' }">
							<c:choose>
								<c:when test="${phenotype.dataSourceName eq 'EuroPhenome'}">
									<a href="${phenotype.phenotypeLink }"  class="fancybox">
										<i class="fa fa-bar-chart-o" alt="Graphs" > </i>
									</a>
								</c:when>
								<c:otherwise>
									<a href="${baseUrl}/charts?accession=${phenotype.gene.id.accession}&parameter_stable_id=${phenotype.parameter.stableId}
										<c:if test="${fn:length(phenotype.sexes) eq 1}">&gender=${phenotype.sexes[0]}</c:if>&zygosity=${phenotype.zygosity}<c:if test="${phenotype.getPhenotypingCenter() != null}">&phenotyping_center=${phenotype.getPhenotypingCenter()}</c:if>&pipeline_stable_id=${phenotype.pipeline.stableId}" class="fancybox">
										<i class="fa fa-bar-chart-o" alt="Graphs" > </i>
									</a>
								</c:otherwise>
							</c:choose>
						</c:if>
					</td>
					
				</tr>
			</c:forEach>
		</tbody>
	
	</table>
	
	
	<script> $(document).ready(function(){ 	 }); </script>
	