<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:genericpage>

<jsp:body>      

<style type="text/css">
table.sample {
	border-width: 1px;
	border-spacing: 2px;
	border-style: outset;
	border-color: gray;
	border-collapse: separate;
	background-color: white;
}
table.sample th {
	border-width: 1px;
	padding: 1px;
	border-style: inset;
	border-color: gray;
	background-color: white;
}
table.sample td {
	border-width: 1px;
	padding: 1px;
	border-style: inset;
	border-color: gray;
	background-color: white;
}

table.sample3 {
    width:50%; 
    margin-left:0%; 
    margin-right:50%;

    border-width: 1px;
    border-spacing: 2px;
    border-style: outset;
    border-color: gray;
    border-collapse: separate;
    background-color: white;
}
table.sample3 td {
    margin-bottom:0;
    border-width: 1px;
    border-style: inset;
    border-color: gray;
    vertical-align: middle;
}

</style>

    <h1 class="title" id="top">LRPCR Genotyping Primers</h1>
    
    <c:if test="${empty lrpcr}"> 
        <br/>
        <br/>
        <p style="font-size: 250%;">Nothing found!</p>
    </c:if>
    
    <c:if test="${not empty lrpcr}"> 

    <div id="content">
<table class="sample3">
    <tbody>
     <tr>
      <td style="background-color: rgb(204, 204, 204);">5' Universal (LAR3)</td>
      <td><pre>${lrpcr['LAR3']}</pre></td>
    </tr>
    <tr>
      <td style="background-color: rgb(204, 204, 204);">3' Universal (RAF5)</td>
      <td><pre>${lrpcr['RAF5']}</pre></td>
    </tr>
</tbody></table>
    </div>
    
    </c:if>
    
</jsp:body>
  
</t:genericpage>
