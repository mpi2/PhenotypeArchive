<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

				<div class="row-fluid">
 				<c:forEach var="categoricalChartDataObject" items="${categoricalResultAndChart.maleAndFemale}" varStatus="chartLoop">
  				 	<div class="container span6">
								<div id="chart1"
									style="min-width: 400px; height: 400px; margin: 0 auto">
								</div>
   								<script type="text/javascript">
   								${categoricalChartDataObject.chart}
   							</script>
					</div>
					
					
					<table id="catTable" class="table table-bordered  table-striped table-condensed">
 							<thead><tr>
 										<th>Exp Sex</th>
										<th>Control/Hom/Het</th><!-- blank usually as no header -->
										<!-- loop over categories -->
										<c:forEach var="categoryObject"  items="${categoricalResultAndChart.maleAndFemale[0].categoricalSets[0].catObjects}" varStatus="categoriesStatus">
												<th>${categoryObject.category }</th>
										</c:forEach>
										<th>P Value</th>
										<th>Max Effect</th>
										</tr>	
							</thead>	
							<tbody>
							<c:forEach var="maleOrFemale"  items="${categoricalResultAndChart.maleAndFemale}" varStatus="maleOrFemaleStatus">
												
														
														<c:forEach var="categoricalSet"  items="${maleOrFemale.categoricalSets}" varStatus="catSetStatus">
														<tr>
														<td>${maleOrFemale.sexType }</td>
																<td>${categoricalSet.name }</td>
																		<c:forEach var="catObject"  items="${categoricalSet.catObjects}" varStatus="catObjectStatus">
																				<td>${catObject.count } </td>
																				<%-- ${catObject} --%>
																		</c:forEach>
																		<%-- ${categoricalSet.catObjects[catSetStatus.index].result} --%>
																		<td>${categoricalSet.catObjects[0].pValue } </td>
																		<td>${categoricalSet.catObjects[0].maxEffect } </td>
														</tr>
														</c:forEach>
												
							</c:forEach>
							
							</tbody>
 				</table>
 				</c:forEach>
				</div>



