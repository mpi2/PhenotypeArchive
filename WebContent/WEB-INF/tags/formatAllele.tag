<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<jsp:doBody var="theBody"/>

<%
String allele = (String) jspContext.getAttribute("theBody");
allele = allele.replaceAll("<", "££");
allele = allele.replaceAll(">", "##");

allele = allele.replaceAll("££", "<sup>");
allele = allele.replaceAll("##", "</sup>");

allele = "<span class=\"smallerAlleleFont\">"+allele+"</span>";
%>

<%= allele %>