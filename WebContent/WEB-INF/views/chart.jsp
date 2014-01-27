<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>


<div class="section">
	<h2 class="title " id="section-associations"> 
						Allele -
						<t:formatAllele>${symbol}</t:formatAllele>
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;background	- ${geneticBackgroundString} ${phenotypingCenter} 
					<span class="documentation"><a href='' class='mpPanel'><i class="fa fa-question-circle pull-right"></i></a></span></h2>
					
	<div class="inner">
			<jsp:include page="scatterStatsFrag.jsp"/>
			
			<jsp:include page="timeSeriesStatsFrag.jsp"/>
			
			<jsp:include page="categoricalStatsFrag.jsp"/>
			
			<jsp:include page="unidimensionalStatsFrag.jsp"/>
		</div>			
	
</div>



	