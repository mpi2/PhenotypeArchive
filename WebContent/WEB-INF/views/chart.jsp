<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<c:choose>
		<c:when test="${emptyExperiment}">
				<!-- <div class="alert alert-error">
		 				<strong>Error:</strong> experiment empty
				</div> -->
		</c:when>
<c:otherwise>
										
									

	<h2 class="title" id="section-associations"> 
						Allele -
		<t:formatAllele>${symbol}</t:formatAllele>
		<span class="documentation" ><a href="" id='generalPanel' class="fa fa-question-circle pull-right"></a></span>
		
	</h2>
	
	<p>Background	- ${geneticBackgroundString}&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Phenotyping Center - ${phenotypingCenter}&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Pipeline - ${pipeline.name }</p> 
					

			<jsp:include page="scatterStatsFrag.jsp"/>
			
			<jsp:include page="timeSeriesStatsFrag.jsp"/>
			
			<jsp:include page="categoricalStatsFrag.jsp"/>
			
			<jsp:include page="unidimensionalStatsFrag.jsp"/>
			
  			
				
	


</c:otherwise>
</c:choose>