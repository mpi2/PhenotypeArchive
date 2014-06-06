<%@tag description="Overall Page template" pageEncoding="UTF-8" 
import="java.text.NumberFormat,java.text.DecimalFormat"
%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<jsp:doBody var="theBody"/>

<%
String num = (String) jspContext.getAttribute("theBody");
Double value = Double.parseDouble(num);

NumberFormat formatter =  new DecimalFormat("0.#####E0");

String textout = String.format("%1.5G",value).replace("E", " &#215; 10<sup>") + "</sup>";

textout = String.format("%1.5G",value);
%>

<%= textout %>