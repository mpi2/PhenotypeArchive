$(document).ready(function(){						
	
console.log('comparator js ready');

//mediaBaseUrl=https://dev.mousephenotype.org/data/media
//var mediaBaseUrl='http://wwwdev.ebi.ac.uk/mi/media/omero/';
var detailUrlExt='/img_detail/';
var url=impcMediaBaseUrl+detailUrlExt;
var annotationBreak='<br/>';
console.log('location='+window.location);
 var sPageURL =location.search.substring(1);
 console.log('sPageUrl='+sPageURL);
 var sURLVariables = sPageURL.split('&');
 var expIds=[];
 var sParam='expImgId';
 for (var i = 0; i < sURLVariables.length; i++) 
 {
     var sParameterName = sURLVariables[i].split('=');
     if (sParameterName[0] == sParam) 
     {
    	 expIds.push(sParameterName[1]);
     }
 }
 var ctrlIds=[];
 var sParam='ctrlImgId';
 for (var i = 0; i < sURLVariables.length; i++) 
 {
     var sParameterName = sURLVariables[i].split('=');
     if (sParameterName[0] == sParam) 
     {
    	 ctrlIds.push(sParameterName[1]);
     }
 }
//make a solr request for these ids
console.log('expIds='+expIds);
console.log('ctrlIds='+ctrlIds);
console.log('solrUrl='+solrUrl);
var thisSolrUrl = solrUrl + '/impc_images/select';
if(expIds.length>0){
var joinedIds=expIds.join(" OR ");
var expParamStr = 'q=omero_id:(' +joinedIds + ')&wt=json&defType=edismax&qf=auto_suggest&rows=100000';
var expDocs;
	$.ajax({
	    'url': thisSolrUrl,
	    'data': expParamStr,
	    'dataType': 'jsonp',
	    'jsonp': 'json.wrf',
	    'success': function(json) {
	        console.log(json.response.numFound + ' images');
	        expDocs=json.response.docs;
	      //loop over solrDocs and split into control and experimental list
	        console.log(expDocs);
	        //
	       //docs=["http://ves-ebi-cf/omero/webgateway/img_detail/5818/", "http://ves-ebi-cf/omero/webgateway/img_detail/5819/"];
	        var len = expDocs.length;
	        var frame = $('#experimental');
	        //var experimentalFrame = $('#experimental', window.parent.document);
	        var i = 0;
	        //initialise navigation to first image annotations
	        doc=expDocs[0];
	        displayDocAnnotations(doc, frame);
	        console.log(mediaBaseUrl);
	        $('#next').click(function(){
	        		console.log('nextControl clicked');
	        		var doc=expDocs[++i % len];
//	        		frame.attr('src', url+doc.omero_id);
//	        		$('#annotations').html(getAnnoataionsDisplayString(doc));
	        		displayDocAnnotations(doc, frame);
	        	});
	
	        $('#prev').click(function(){
	        	console.log('nextControl clicked');
	        	var doc=expDocs[--i % len];
	        	displayDocAnnotations(doc, frame);
//	        	frame.attr('src', url+doc.omero_id);
//	        	$('#annotations').html(getAnnoataionsDisplayString(doc));
	        });
	
	        
	    }
	});
}

if(ctrlIds.length>0){
	var ctrlDocs;
	var ctrlJoinedIds=ctrlIds.join(" OR ");
	var ctrlParamStr = 'q=omero_id:(' +ctrlJoinedIds + ')&wt=json&defType=edismax&qf=auto_suggest&rows=100000';
	$.ajax({
	    'url': thisSolrUrl,
	    'data': ctrlParamStr,
	    'dataType': 'jsonp',
	    'jsonp': 'json.wrf',
	    'success': function(json) {
	        console.log(json.response.numFound + ' images');
	        ctrlDocs=json.response.docs;
	      //loop over solrDocs and split into control and experimental list
	        console.log(ctrlDocs);
	        //
	       //docs=["http://ves-ebi-cf/omero/webgateway/img_detail/5818/", "http://ves-ebi-cf/omero/webgateway/img_detail/5819/"];
	        var len = ctrlDocs.length;
	        var frame = $('#controls');
	        //var experimentalFrame = $('#experimental', window.parent.document);
	        var i = 0;
	        //initialise navigation to first image annotations
	        doc=ctrlDocs[0];
	        displayDocAnnotations(doc, frame);
	        console.log(mediaBaseUrl);
	        $('#next').click(function(){
	        		console.log('nextControl clicked');
	        		var doc=ctrlDocs[++i % len];
//	        		frame.attr('src', url+doc.omero_id);
//	        		$('#annotations').html(getAnnoataionsDisplayString(doc));
	        		displayDocAnnotations(doc, frame);
	        	});
	
	        $('#prev').click(function(){
	        	console.log('nextControl clicked');
	        	var doc=ctrlDocs[--i % len];
	        	displayDocAnnotations(doc, frame);
//	        	frame.attr('src', url+doc.omero_id);
//	        	$('#annotations').html(getAnnoataionsDisplayString(doc));
	        });
	
	        
	    }
	});

}
	
function displayDocAnnotations(doc, frame){
	$.ajax({
	    'url': url+doc.omero_id,
	    crossOrigin: true,
	    'success': function() {
	    	console.log('got something in response!!!');
	      frame.html(response.html);        
	    }
	});
	$('#annotations').html(getAnnoataionsDisplayString(doc));
}
function getAnnoataionsDisplayString(doc){
	var label= doc.biological_sample_group+ annotationBreak+doc.sex+annotationBreak+doc.full_resolution_file_path.substring(doc.full_resolution_file_path.lastIndexOf("/")+1, doc.full_resolution_file_path.length);
	
	if(doc.biological_sample_group === 'experimental'){
		label+=annotationBreak+doc.zygosity+annotationBreak+doc.allele_symbol;
	}
	return label;
}
//setInterval(function () {
//	console.log('changing image');
//    iframe.attr('src', locations[++i % len]);
//}, 3000);


});
