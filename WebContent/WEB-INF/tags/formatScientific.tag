<%@tag description="Overall Page template" pageEncoding="UTF-8" 
import="java.text.NumberFormat,java.text.DecimalFormat"
%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<jsp:doBody var="theBody"/>

<%

String textout ="";
String num = (String) jspContext.getAttribute("theBody");
if(num !=null && !num.equals("")){
Double value = Double.parseDouble(num);

NumberFormat formatter =  new DecimalFormat("0.#####E0");

textout = String.format("%1.5G",value).replace("E", " &#215; 10<sup>") + "</sup>";

textout = String.format("%1.5G",value);
}
	
%>

<%= textout %>
