<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:genericpage>

<jsp:attribute name="title">QC Data</jsp:attribute>

<jsp:attribute name="header">    

    <style>

    .qc_data_class {
        font-size: small;
    }

    </style>
    

</jsp:attribute>

<jsp:body>

        <h1 class="title" id="top">QC Data</h1>

<c:choose>
<c:when test='${qcData.isEmpty()}'>
    <br/>
    <p style="font-size: xx-large">No QC Data Available</p>
</c:when>
<c:otherwise>
  


    <div class="qc_data_class">
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


<c:choose>
<c:when test='${qcData.equals("pass")}'>
    <td style="color: green;font-weight: bold;">${qcData}</td>
</c:when>
<c:otherwise>
    <td style="color: red;">${qcData}</td>
</c:otherwise>
</c:choose>
                    


                </c:forEach>
            </tr>
        </c:forEach>
    </table>
    </div>
    
</c:otherwise>
</c:choose>

        
</jsp:body>
  
</t:genericpage>
