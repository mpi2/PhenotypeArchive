$(document).ready(function(){						
	
console.log('comparator js ready');

//mediaBaseUrl=https://dev.mousephenotype.org/data/media
//var mediaBaseUrl='http://wwwdev.ebi.ac.uk/mi/media/omero/';
var solrUrl='//www.ebi.ac.uk/mi/impc/beta/solr';
var omero_gateway_root="//www.ebi.ac.uk/mi/media/omero/webgateway";

var detailUrlExt='/img_detail/';
var url=omero_gateway_root+detailUrlExt;//may need for this to be passed as a parameter for each request if not being set by jsp?
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
        if (sParameterName[0] == 'omero_gateway_root') //set the default omero url to be this
        {
        	//console.log('setting omero_gateway_root='+sParameterName[1]);
        	omero_gateway_root=sParameterName[1];
        }
        if (sParameterName[0] == sParam) 
        {
            imgIds.push(sParameterName[1]);
        }
    }
    return imgIds;
}

//get whether we are the subframe for control or experimental from the arg passed to the page from the imageComparator frame src attribute
var controlOrExp=getURLParameter('controlOrExp', window.location);
//console.log('location='+window.location);
//console.log('string='+getURLParameter('controlOrExp', window.location));
//if(location.pathname.substring(1).search('Control')>0){
//	controlOrExp='control';
//};

//console.log('control or exp='+controlOrExp);

//console.log('ctrImgId='+getURLParameter('ctrImgId'));
//console.log('expImgId='+getURLParameter('expImgId'));
var ids=[];
if(controlOrExp=='experimental'){
	ids=getURLParameter('expImgId', window.parent.location);
}else{
	ids=getURLParameter('ctrImgId', window.parent.location);
}

//make a solr request for these ids
//console.log('ids='+ids);
if(ids.length!=0){//only search for info related to ids if we have them.

//console.log('solrUrl='+solrUrl);
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
	        //console.log(json.response.numFound + ' images');
	        docs=json.response.docs;
	      //loop over solrDocs and split into control and experimental list
	        //console.log(docs);
	        //
	       //docs=["http://ves-ebi-cf/omero/webgateway/img_detail/5818/", "http://ves-ebi-cf/omero/webgateway/img_detail/5819/"];
	        var len = docs.length;
	        var frame = $('#'+controlOrExp, window.parent.document);
	        //var experimentalFrame = $('#experimental', window.parent.document);
	        var i = 0;
	        //initialise navigation to first image annotations
	        doc=docs[0];
	        displayDocAnnotations(doc, frame);
	        //console.log('mediaBaseUrl='+mediaBaseUrl);
	        $('#next').click(function(){
	        		//console.log('nextControl clicked');
	        		var doc=docs[++i % len];
//	        		frame.attr('src', url+doc.omero_id);
//	        		$('#annotations').html(getAnnoataionsDisplayString(doc));
	        		displayDocAnnotations(doc, frame);
	        	});
	
	        $('#prev').click(function(){
	        	//console.log('nextControl clicked');
	        	var doc=docs[--i % len];
	        	displayDocAnnotations(doc, frame);
//	        	frame.attr('src', url+doc.omero_id);
//	        	$('#annotations').html(getAnnoataionsDisplayString(doc));
	        });
	
	        
	    }
	});
	

}else{//else we don't have ids so display an error to the user
		//console.log('no ids for '+controlOrExp);
		var frame = $('#'+controlOrExp, window.parent.document);
		if(controlOrExp=='experimental'){
			frame.attr('src', 'experimental_images_error.html');
		}else{
			frame.attr('src', 'control_images_error.html');
		}
}
	
	
function displayDocAnnotations(doc, frame){
	frame.attr('src', doc.jpeg_url.replace('render_image', 'img_detail').replace('http://','//'));//get the jpeg url and change it to a img_detail view but idea is we get the correct context from the solr we are pointing at. so no need to pass it as a parameter
	//frame.attr('src','http://omeroweb.jax.org/omero/webgateway/img_detail/7541/?c=1%7C0:255$FF0000,2%7C0:255$00FF00,3%7C0:255$0000FF&m=c&p=normal&ia=0&q=0.9&zm=6.25&t=1&z=1&x=50824&y=19576');
	$('#annotations').html(getAnnoataionsDisplayString(doc));
}
function getAnnoataionsDisplayString(doc){
	var label= annotationBreak+doc.sex+annotationBreak+doc.full_resolution_file_path.substring(doc.full_resolution_file_path.lastIndexOf("/")+1, doc.full_resolution_file_path.length);
	
	if(doc.biological_sample_group === 'experimental'){
		label+=annotationBreak+doc.zygosity+annotationBreak+doc.allele_symbol;
	}
	
	if(doc.parameter_association_name){
		label+=annotationBreak+doc.parameter_association_name+annotationBreak+doc.parameter_association_value;
	}
	return label;
}
//setInterval(function () {
//	console.log('changing image');
//    iframe.attr('src', locations[++i % len]);
//}, 3000);


});
