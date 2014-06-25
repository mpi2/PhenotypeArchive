<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>


<table>
    <c:forEach var="qcGroup" items="${qcData.keySet()}" varStatus="status">
        <tr>
        <td rowspan="2">${qcGroup}</td>
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
