(function ( $ ) {
 
    $.fn.getDropFilterQuery = function( options ) {
 
        // This is the easiest way to have default options.
//        var settings = $.extend({
//            // These are the defaults.
//            color: "#556b2f",
//            backgroundColor: "white"
//        }, options );
 
        // Greenify the collection based on the settings variable.
//        return this.css({
//            color: settings.color,
//            backgroundColor: settings.backgroundColor
//        });
    	
    	
    		 
    	        return this.each(function() {
    	            //$( this ).append( " (" + $( this ).attr( "href" ) + ")" );
    	        	var output='';
    	    		var array1=$(this).val() || [];
	    			 console.log('array Length;'+array1.length);
	    			if(array1.length==1){//if only one entry for this parameter then don't use brackets and or
	    				 output+='&fq=resource_name:"'+array1[0]+'"';
	    			} 
	    			if(array1.length>1){//has to be greater than 1 but not zero!!! so not an else statement here
	    				output+='&fq=resource_name:(';//note " before and after value for solr handle spaces
	    			 		for(var i=0; i<array1.length; i++){
	    						 
	    							 //if(i==0)output+=' " ';
	    						 output+='"'+array1[i]+'"';
	    						 if(i<array1.length-1){
	    							 output+=' OR ';
	    						 }else{
	    							 output+=')';
	    						 }
	    			 }
	    		}
	    			alert(output);
	    	        return output;
    	        });
    	 
 
    };
 
}( jQuery ));