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
	
                    <p>Background	- ${geneticBackgroundString}&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Phenotyping Center - ${phenotypingCenter}&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<c:if test="${pipeline.name!=null}">Pipeline - ${pipeline.name }</c:if></p> 
					
<c:choose>
			<c:when test="${param['scatter']==true}">
					<jsp:include page="scatterStatsFrag.jsp"/>
			</c:when>
			<c:otherwise>
					<jsp:include page="unidimensionalStatsFrag.jsp"/>
			</c:otherwise>
</c:choose> <!-- only show scatter if scatter else only show unidimensional - otherwise we get unidimensional tables showing twice on the same page -->

			<jsp:include page="timeSeriesStatsFrag.jsp"/>
			
			<jsp:include page="categoricalStatsFrag.jsp"/>
			
			
			
  			
				
	


</c:otherwise>
</c:choose>