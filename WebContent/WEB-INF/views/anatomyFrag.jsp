<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>


			<table id="anatomy" class="table tableSorter">
					    <thead>
					    <tr>
					        <th class="headerSort">Gene/Allele</th>
					        <th class="headerSort">Expression</th>
					        <th class="headerSort">Anatomy</th>					        
					        <th class="headerSort">Zygosity</th>
					        <th class="headerSort">Sex</th>
					        <th class="headerSort">Parameter</th>
					        <th class="headerSort">Phenotyping Center</th>
					        <th class="headerSort">Images</th>
					    </tr>
					    </thead>
					    <tbody>
					    <c:forEach var="row" items="${anatomyTable}" varStatus="status">
					        <c:set var="europhenome_gender" value="Both-Split"/>
					        <tr>
					            <td><a href="${baseUrl}/genes/${row.gene.id.accession}">${row.gene.symbol} </a><br/> <span class="smallerAlleleFont"><t:formatAllele>${row.allele.symbol}</t:formatAllele></span></td>
					            <td>${row.expression}</td>
					           	<td>${row.anatomyLinks}</td>
					            <td>${row.zygosity}</td>
					            <td>
					                <c:set var="count" value="0" scope="page"/>
					                <c:forEach var="sex" items="${row.sexes}"><c:set var="count" value="${count + 1}" scope="page"/>
					                    <c:if test="${sex == 'female'}"><c:set var="europhenome_gender" value="Female"/>
					                        <img alt="Female" src="${baseUrl}/img/female.jpg"/>
					                    </c:if>
					                    <c:if test="${sex == 'male'}">
					                        <c:if test="${count != 2}"><img data-placement="top" src="${baseUrl}/img/empty.jpg"/></c:if>
					                        <c:set var="europhenome_gender" value="Male"/><img alt="Male" src="${baseUrl}/img/male.jpg"/>
					                    </c:if>
					                </c:forEach>
					            </td>				
					            <td>${row.parameter.name}</td>
					            <td>${row.phenotypingCenter} </td>
					            <td><a href='${row.imageUrl}'><i class="fa fa-image" alt="Images"></i></a></td>	
					            <!-- http://localhost:8080/phenotype-archive/imagesb?qf=auto_suggest&defType=edismax&wt=json&fq=(ma_term:%22olfactory%20lobe%22)&q=*:*&fq=symbol:%22Adh5%22&fl=annotationTermId,annotationTermName,expName,symbol,symbol_gene,smallThumbnailFilePath,largeThumbnailFilePath -->			
					        </tr>
					    </c:forEach>
					    </tbody>
					</table>