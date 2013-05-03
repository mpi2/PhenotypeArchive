<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>


<t:genericpage>
	<jsp:attribute name="title">Genomic Feature Details | IMPC</jsp:attribute>
	<jsp:body>

		<table class="table">
			<tr>
				<th>Accession:</th>
				<td>${genomicFeature.id.accession}</td>
			</tr>
			<tr>
				<th>Symbol:</th>
				<td>${genomicFeature.symbol}</td>
			</tr>
			<tr>
				<th>Name:</th>
				<td>${genomicFeature.name}</td>
			</tr>			
		</table>

	</jsp:body>
</t:genericpage>