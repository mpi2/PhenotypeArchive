
window.jQuery('document').ready(function(){

        window.jQuery('input#userInput').val('');  // clears input when pages loads

        // search via ENTER
        window.jQuery('input#userInput').keyup(function (e) {
            if (e.keyCode == 13) { // user hits enter
               
                var input = $('input#userInput').val();
	    	    //console.log('user input search: ' + input);
	    	    if  (input == ''){
	    		   document.location.href = baseUrl + '/search';
	    	    }
	    	    else {
	    		   document.location.href = baseUrl + '/search?q=' + input; // handed over to hash change
	    		   console.log(window.location.search);
	    	    }
            }
        }).click(function(){
                window.jQuery(this).val(''); // clears input
        });

        // search via button click
        window.jQuery('button#acSearch').click(function(){

                 var input = $('input#userInput').val();
		         //console.log('button search in search and facet= ' + input);
		         if (input == ''){
    		         document.location.href = baseUrl + '/search';
    	         }
    	         else {
    		         document.location.href = baseUrl + '/search?q=' + input; // handed over to hash change     		
    	         }	                
        });


       // dynamically readjusted position of autosuggest dropdown list due to elastic design of page
       window.jQuery(window).resize(function(){
            var pos = window.jQuery('input#userInput').offset();
            window.jQuery('ul.ui-autocomplete').css({'position':'absolute', 'top':pos.top + 26, 'left': pos.left});
       });
});

