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
										
									

	<h2 class="title documentation" id="section-associations"> 
						Allele -
						<t:formatAllele>${symbol}</t:formatAllele>
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;background	- ${geneticBackgroundString} ${phenotypingCenter} 
					<!-- <i class="fa fa-question-circle pull-right" data-hasqtip="30" oldtitle="Brief info about this panel" title="" aria-describedby="qtip-30"> </i> -->
					</h2>
					

			<jsp:include page="scatterStatsFrag.jsp"/>
			
			<jsp:include page="timeSeriesStatsFrag.jsp"/>
			
			<jsp:include page="categoricalStatsFrag.jsp"/>
			
			<jsp:include page="unidimensionalStatsFrag.jsp"/>
			
  			
				
	


</c:otherwise>
</c:choose>