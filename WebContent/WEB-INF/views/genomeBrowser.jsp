<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
<title>Gene Browser</title>
<script src="${baseUrl}/js/vendor/jquery-1.10.2.min.js"></script>
                                    		<script src="${baseUrl}/dalliance/genomicB.js"></script>
                                    		<script type="text/javascript" src="${baseUrl}/dalliance/dalliance-compiled.js"></script>
                                    		</head>
                                    		<body>
                                         <div id="genomebrowser">
											<div class="floatright"><a href="http://www.biodalliance.org/" target="_blank" title="More information on using this browser"><i class="icon-question-sign"></i></a> <a title="This browser is clickable please experiment by clicking. Click on features to get more info, click on zoom bar etc. To reset click on 'lightning button'">This a an interactive genomic browser</a>
											</div>  
											Gene&nbsp;Location: Chr<span id='chr'>${gene.sequenceRegion.name}</span>:<span  id='geneStart'>${gene.start}</span>-<span  id='geneEnd'>${gene.end}</span> <br/> Gene Type: ${gene.subtype.name}
												<!-- <p><img class="fullimg" src="img/dummy/genebrowser.jpg" /></p> -->
											<div id="svgHolder"></div>
											
										<table>
											<tbody>
												<c:if test="${not empty vegaIds}">
												<tr>
													<td>Vega Ids:</td>
													<td><c:forEach var="id" items="${vegaIds}" varStatus="loop"><a href="http://vega.sanger.ac.uk/Mus_musculus/geneview?gene=${id}&db=core">${id}</a><c:if test="${!loop.last}"><br /></c:if></c:forEach></td>
												</tr>
												</c:if>
												<c:if test="${not empty ncbiIds}">
												<tr>
													<td>NCBI Id:</td>
													<td><c:forEach var="id" items="${ncbiIds}" varStatus="loop"><a href="http://www.ncbi.nlm.nih.gov/sites/entrez?db=gene&cmd=Retrieve&dopt=Graphics&list_uids=${id}">${id}</a><c:if test="${!loop.last}"><br /></c:if></c:forEach></td>
												</tr>
												</c:if>
												<c:if test="${not empty ccdsIds}">
												<tr>
													<td>CCDS Id:</td>
													<td><c:forEach var="id" items="${ccdsIds}" varStatus="loop"><a href="http://www.ncbi.nlm.nih.gov/CCDS/CcdsBrowse.cgi?REQUEST=CCDS&DATA=${id}">${id}</a><c:if test="${!loop.last}"><br /></c:if></c:forEach></td>
												</tr>
												</c:if>
											</tbody>
										</table>				
										
									
										</div>
										</body>
</html>