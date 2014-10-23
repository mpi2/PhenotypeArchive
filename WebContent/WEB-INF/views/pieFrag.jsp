<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<!-- PieChart here -->
<c:if test="${viabilityDTO!=null}">

                     	
            		<div id="totalChart" class="onethirdForPie ">
								</div>
   								<script type="text/javascript">
		${viabilityDTO.totalChart}
	</script>
								

	

            		<div id="maleChart" class="onethirdForPie ">
								</div>
   								<script type="text/javascript">
		${viabilityDTO.maleChart}
	</script>
								
	
	
	
            		<div id="femaleChart" class="onethirdForPie ">
								</div>
   								<script type="text/javascript">
		${viabilityDTO.femaleChart}
	</script>
	
	<table>
	<tr><th></th><th>WT</th><th>Hom</th><th>Het</th><th>Total</th></tr>
	<tr>
	<td>Male and Female</td><td>${viabilityDTO.paramStableIdToObservation['IMPC_VIA_004_001'].dataPoint}</td><td>${viabilityDTO.paramStableIdToObservation['IMPC_VIA_006_001'].dataPoint}</td><td>${viabilityDTO.paramStableIdToObservation['IMPC_VIA_005_001'].dataPoint}</td><td>${viabilityDTO.paramStableIdToObservation['IMPC_VIA_003_001'].dataPoint}</td>
	</tr>
	<tr><td>Male</td><td>${viabilityDTO.paramStableIdToObservation['IMPC_VIA_007_001'].dataPoint}</td><td>${viabilityDTO.paramStableIdToObservation['IMPC_VIA_009_001'].dataPoint}</td><td>${viabilityDTO.paramStableIdToObservation['IMPC_VIA_008_001'].dataPoint}</td><td>${viabilityDTO.paramStableIdToObservation['IMPC_VIA_010_001'].dataPoint}</tr>
	<tr><td>Female</td><td>${viabilityDTO.paramStableIdToObservation['IMPC_VIA_011_001'].dataPoint}</td><td>${viabilityDTO.paramStableIdToObservation['IMPC_VIA_013_001'].dataPoint}</td><td>${viabilityDTO.paramStableIdToObservation['IMPC_VIA_012_001'].dataPoint}</td><td>${viabilityDTO.paramStableIdToObservation['IMPC_VIA_014_001'].dataPoint}	</tr>
	</table>	
</c:if>