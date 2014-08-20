<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<!-- time series charts here-->
<c:if test="${timeSeriesChartsAndTable.chart!=null}">
    
		<div id="timechart${experimentNumber}"> </div>
		
                <p><a href="${acc}?${pageContext.request.queryString}&scatter=1">Graph by date</a></p>
        <p><a><i class="fa" id="toggle_table_button${experimentNumber}">More Statistics</i></a></p>
	<div id="toggle_timetable${experimentNumber}">
                <table id="timeTable">
                    <tr>
                        <th>Time</th>
                         <c:forEach var="lineMap" items="${timeSeriesChartsAndTable.lines}" varStatus="keyCount">
                        <th>${lineMap.key}</th>
                         </c:forEach>
                    </tr>
                     <tr>
                    <c:forEach var="lineKey" items="${timeSeriesChartsAndTable.uniqueTimePoints}" varStatus="timeRow"> 
                    <tr>
                        <td>${lineKey}</td>
                        <c:forEach var="lineMap" items="${timeSeriesChartsAndTable.lines}" varStatus="column"> 
                         
                            <td><c:if test="${lineMap.value[timeRow.index].discreteTime==lineKey}">${lineMap.value[timeRow.index].data} (${lineMap.value[timeRow.index].count})</td></c:if>
                        
                        </c:forEach>
                    </tr>
                    </c:forEach>
                      </tr>
                </table>
        </div>
		<script type="text/javascript">
					${timeSeriesChartsAndTable.chart}
		</script>
		<script>
 	$(document).ready(
		function() {
			//set up the toggle on the table
                        $( "#toggle_timetable${experimentNumber}" ).hide();//hide on load
			$( "#toggle_table_button${experimentNumber}" ).toggleClass('fa-caret-right');//toggle the arrow on the link to point right as should be closed on init
			$( "#toggle_table_button${experimentNumber}" ).click(function() {
				console.log("click fired");
										  $( "#toggle_timetable${experimentNumber}" ).toggle('slow');
										  $( "#toggle_table_button${experimentNumber}" ).toggleClass('fa-caret-right').toggleClass('fa-caret-down');//remove right and put down or vica versa
										});
			
			// bubble popup for brief panel documentation - added here as in stats page it doesn't work
		 	$.fn.qTip({
						'pageName': 'stats',					
						'tip': 'top right',
						'corner' : 'right top'
			}); 
 					});
</script>
</c:if>
	