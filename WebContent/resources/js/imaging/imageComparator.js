$(document).ready(function(){						
	
console.log('comparator js ready');

//mediaBaseUrl=https://dev.mousephenotype.org/data/media
//var mediaBaseUrl='http://wwwdev.ebi.ac.uk/mi/media/omero/';
var detailUrlExt='/img_detail/';
var url=impcMediaBaseUrl+detailUrlExt;
var annotationBreak='<br/>';
//console.log('solrUrl='+solrUrl);
//get all the ids from the parameter list and get solrDocs for each
function getURLParameter(sParam, location)
{
	 location = location || window.parent.location;
    var sPageURL =location.search.substring(1);
    var sURLVariables = sPageURL.split('&');
    var imgIds=[];
    for (var i = 0; i < sURLVariables.length; i++) 
    {
        var sParameterName = sURLVariables[i].split('=');
        if (sParameterName[0] == sParam) 
        {
            imgIds.push(sParameterName[1]);
        }
    }
    return imgIds;
}

//get whether we are the subframe for control or experimental from the arg passed to the page from the imageComparator frame src attribute
var controlOrExp=getURLParameter('controlOrExp', window.location);
console.log('location='+window.location);
console.log('string='+getURLParameter('controlOrExp', window.location));
//if(location.pathname.substring(1).search('Control')>0){
//	controlOrExp='control';
//};

console.log(controlOrExp);

console.log('ctrImgId='+getURLParameter('ctrImgId'));
console.log('expImgId='+getURLParameter('expImgId'));
var ids=[];
if(controlOrExp=='experimental'){
	ids=getURLParameter('expImgId', window.parent.location);
}else{
	ids=getURLParameter('ctrImgId', window.parent.location);
}
//make a solr request for these ids
console.log('ids='+ids);
console.log('solrUrl='+solrUrl);
var thisSolrUrl = solrUrl + '/impc_images/select';
var joinedIds=ids.join(" OR ");
var paramStr = 'q=omero_id:(' +joinedIds + ')&wt=json&defType=edismax&qf=auto_suggest&rows=100000';
var docs;
	$.ajax({
	    'url': thisSolrUrl,
	    'data': paramStr,
	    'dataType': 'jsonp',
	    'jsonp': 'json.wrf',
	    'success': function(json) {
	        console.log(json.response.numFound + ' images');
	        docs=json.response.docs;
	      //loop over solrDocs and split into control and experimental list
	        console.log(docs);
	        //
	       //docs=["http://ves-ebi-cf/omero/webgateway/img_detail/5818/", "http://ves-ebi-cf/omero/webgateway/img_detail/5819/"];
	        var len = docs.length;
	        var frame = $('#'+controlOrExp, window.parent.document);
	        //var experimentalFrame = $('#experimental', window.parent.document);
	        var i = 0;
	        //initialise navigation to first image annotations
	        doc=docs[0];
	        displayDocAnnotations(doc, frame);
	        console.log(mediaBaseUrl);
	        $('#next').click(function(){
	        		console.log('nextControl clicked');
	        		var doc=docs[++i % len];
//	        		frame.attr('src', url+doc.omero_id);
//	        		$('#annotations').html(getAnnoataionsDisplayString(doc));
	        		displayDocAnnotations(doc, frame);
	        	});
	
	        $('#prev').click(function(){
	        	console.log('nextControl clicked');
	        	var doc=docs[--i % len];
	        	displayDocAnnotations(doc, frame);
//	        	frame.attr('src', url+doc.omero_id);
//	        	$('#annotations').html(getAnnoataionsDisplayString(doc));
	        });
	
	        
	    }
	});

	
	
function displayDocAnnotations(doc, frame){
	frame.attr('src', url+doc.omero_id);
	$('#annotations').html(getAnnoataionsDisplayString(doc));
}
function getAnnoataionsDisplayString(doc){
	var label= doc.biological_sample_group+ annotationBreak+doc.sex+annotationBreak+doc.full_resolution_file_path.substring(doc.full_resolution_file_path.lastIndexOf("/")+1, doc.full_resolution_file_path.length);;
	
	if(doc.biological_sample_group === 'experimental'){
		label+=doc.zygosity+annotationBreak+doc.allele_symbol;
	}
	return label;
}
//setInterval(function () {
//	console.log('changing image');
//    iframe.attr('src', locations[++i % len]);
//}, 3000);


});
