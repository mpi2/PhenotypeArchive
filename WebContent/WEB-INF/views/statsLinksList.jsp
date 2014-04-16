<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>


<t:genericpage>
<jsp:attribute name="title">
IMPC
<!--Default title IMPC-  Your Title Goes here in Plain Text -->
</jsp:attribute>
    <jsp:attribute name="header">
<!--    extra header stuff goes here such as extra page specific javascript and css -->

    </jsp:attribute>
    <jsp:attribute name="footer">
<!--      Anything extra to go in the footer goes here -->
    </jsp:attribute>
    
    
    
    
    <jsp:body>
<!--         main body content goes here -->
      <c:forEach var="row" items="${statsLinks}" varStatus="listLoop">
	${listLoop.count}) <a href="${baseUrl}/charts?accession=${ row.accession}&parameter_stable_id=${row.paramStableId}">"${baseUrl}/charts?accession=${ row.accession}?parameter_stable_id=${row.paramStableId}"</a><br/>
	</c:forEach>
	
	
	
	
	
    </jsp:body>

</t:genericpage>