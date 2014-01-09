// Helper function to assign the target URL of the logo to the bare domain
// (whatever it is) while preserving the protocol

$(document).ready(function(){
	var url = window.location.href;		
	var arr = url.split("/");
	var domain = arr[0] + "//" + arr[2];
	$('a#logo').attr('href', domain);	
});
