<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<c:if test="${makeEnuLink>0}">
<td>ENU Link:</td>
<td class="gene-data" id="enu_links">
<a href="https://databases.apf.edu.au/mutations/snpRow/list?mgiAccessionId=${acc}">ENU (${makeEnuLink})</a>
</td>
</c:if>


							 
