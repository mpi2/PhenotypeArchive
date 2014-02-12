<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<t:genericpage>

	<jsp:attribute name="breadcrumb">&nbsp;&raquo; Statistics &raquo; <a href='${baseUrl}/genes/${gene.id.accession}'>${gene.symbol}</a></jsp:attribute>
<jsp:attribute name="bodyTag"><body  class="chartpage no-sidebars small-header"></jsp:attribute>
	<jsp:attribute name="header">

    </jsp:attribute>

		<jsp:attribute name="addToFooter">
			 <script>
			 //ajax chart caller code
$(document).ready(function(){				
				    
	$('.chart').each(function(i, obj) 
	{
		var graphUrl=$(this).attr('graphUrl');
		var id=$(this).attr('id');
		console.log('id='+id);
		console.log("obj att"+$(this).attr('graphUrl'));
		console.log("request uri="+document.URL);
		
		var chartUrl=graphUrl+'&experimentNumber='+id;
			$.ajax({
				  url: chartUrl,
				  data:{"originalUrl":document.URL},
				  cache: false
			})
				  .done(function( html ) {
				    $( '#'+ id ).append( html );
				    $( '#spinner'+ id ).html('');
				    if(html.search( 'section-associations' )===-1){//if this element not found in the html then no graph present so remove placeholder section
				    	console.log('element found');
				    	//$( '#'+ id ).html( '' ); 
				    	 console.log('id='+$('#'+ id).parent().parent().html( '' ));
				    }
				    
			});
			 
	});	 
	
	
	// bubble popup for brief panel documentation
 	$.fn.qTip({
				'pageName': 'stats',
				'textAlign': 'left',
				'tip': 'topRight'
	}); 
			
	
});
		 </script>
		 
  </jsp:attribute>

	<jsp:body>
		<c:if test="${statsError}">
					<div class="alert alert-error">
						<strong>Error:</strong> An issue occurred processing the statistics for this page - results on this page maybe incorrect.
					</div>
		</c:if>
		<c:if test="${noData}">
					<div class="alert alert-error">
						<strong>We don't appear to have any data for this query please try the europhenome graph link instead</strong>
					</div>
		</c:if>
		
		 <c:forEach var="graphUrl" items="${allGraphUrlSet}" varStatus="graphUrlLoop">			
  						
  							
  <div class="section">
  			<div class="inner">
  					<div class="chart" graphUrl="${baseUrl}/chart?${graphUrl}"  id="${graphUrlLoop.count}">			
  							<div id="spinner${graphUrlLoop.count}"><i class="fa fa-refresh fa-spin"></i></div>	
  					</div>
  			</div>
 </div>
  			
  								
  						
		</c:forEach>

    </jsp:body>
</t:genericpage>
