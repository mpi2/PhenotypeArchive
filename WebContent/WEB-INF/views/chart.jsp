<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>


<div class="section">
	<h2 class="title documentation" id="section-associations"> 
						Allele -
						<t:formatAllele>${symbol}</t:formatAllele>
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;background	- ${geneticBackgroundString} ${phenotypingCenter} 
					<a href='' id='generalPanel' class="fa fa-question-circle pull-right" ></a>
					<!-- <i class="fa fa-question-circle pull-right" data-hasqtip="30" oldtitle="Brief info about this panel" title="" aria-describedby="qtip-30"> </i> -->
					</h2>
					
	<div class="inner">
			<jsp:include page="scatterStatsFrag.jsp"/>
			
			<jsp:include page="timeSeriesStatsFrag.jsp"/>
			
			<jsp:include page="categoricalStatsFrag.jsp"/>
			
			<jsp:include page="unidimensionalStatsFrag.jsp"/>
		</div>			
	
</div>