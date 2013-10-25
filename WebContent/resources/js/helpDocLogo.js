
$(document).ready(function(){
	var url = window.location.href;		
	var arr = url.split("/");
	var domain = arr[0] + "//" + arr[2]
	$('a#logo').attr('href', domain);	
});