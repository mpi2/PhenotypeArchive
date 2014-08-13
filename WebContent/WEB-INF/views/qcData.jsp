<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<c:choose>
<c:when test='${qcData.isEmpty()}'>
    <p>No QC Data Available</p>
</c:when>
<c:otherwise>
  
    <table>

        <c:forEach var="qcGroup" items="${qcData.keySet()}" varStatus="status">
            <tr>
                <td rowspan="2"><h3>${qcGroup}</h3></td>
                <c:forEach var="qcData" items="${qcData[qcGroup]['fieldNames']}" varStatus="status">
                    <th>${qcData}</th>
                </c:forEach>
            </tr>
            <tr>
                <c:forEach var="qcData" items="${qcData[qcGroup]['values']}" varStatus="status">
                    <td>${qcData}</td>
                </c:forEach>
            </tr>
        </c:forEach>
    </table>
</c:otherwise>
</c:choose>
